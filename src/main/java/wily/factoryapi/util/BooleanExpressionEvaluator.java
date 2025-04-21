package wily.factoryapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.config.FactoryCommonOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record BooleanExpressionEvaluator(String expression, List<Token> tokens, Stack<Boolean> values, Stack<Operator> operators, TokenProcessor processor) {
    public static final LoadingCache<String, BooleanExpressionEvaluator> EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).build(CacheLoader.from(BooleanExpressionEvaluator::create));

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\\$\\{[\\dA-Za-z_.-]+})|(:?(?:false|true))|(:?\\d+\\.?\\d*)|(!|&&|&|\\|\\||\\|)|(==|!=|>=|<=|>|<)");

    public BooleanExpressionEvaluator(String expression, List<Token> tokens){
        this(expression, tokens, new Stack<>(), new Stack<>(), new TokenProcessor());
    }

    public static BooleanExpressionEvaluator create(String expression) {
        return new BooleanExpressionEvaluator(expression, tokenize(expression));
    }

    public static BooleanExpressionEvaluator of(String expression) {
        return EXPRESSION_CACHE.getUnchecked(expression);
    }


    public Boolean evaluate(VariableResolver variableResolver) {
        values.clear();
        operators.clear();
        try {
            for (int i = 0; i < tokens.size(); i++) {
                processor.process(i, this, variableResolver);
            }

            while (!operators.isEmpty()) {
                values.push(operators.pop().operate(values.pop(), values.pop()));
            }

            return values.pop();
        } catch (Exception e) {
            if (FactoryCommonOptions.EXPRESSION_FAIL_LOGGING.get()) FactoryAPI.LOGGER.warn("Incorrect expression syntax: {} \nExpression: {}", e.getMessage(), toString());
            return false;
        }
    }

    public static List<Token> tokenize(String expression) {
        Matcher matcher = TOKEN_PATTERN.matcher(expression);
        List<Token> tokens = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                String token = matcher.group(1);
                String variableName = token.substring(2, token.length() - 1);
                tokens.add(new Variable(variableName));
            } if (matcher.group(2) != null) {
                String token = matcher.group(2);
                tokens.add(new BooleanValue(Boolean.parseBoolean(token.startsWith(":") ? token.substring(1) : token)));
            } else if (matcher.group(3) != null) {
                String token = matcher.group(3);
                tokens.add(new NumberValue(Double.parseDouble(token.startsWith(":") ? token.substring(1) : token)));
            } else if (matcher.group(4) != null) {
                tokens.add(new Operator(matcher.group(4)));
            } else if (matcher.group(5) != null) {
                tokens.add(new Equality(matcher.group(5)));
            }
        }
        return tokens;
    }

    public interface Token {
        void process(TokenProcessor processor);
    }

    public static class TokenProcessor {
        private BooleanExpressionEvaluator evaluator;
        private int index;
        private VariableResolver resolver;

        public Token relative(int offset){
            int relative = index + offset;
            return relative < evaluator.tokens.size() && relative >= 0 ? evaluator.tokens.get(relative) : null;
        }

        public void process(int index, BooleanExpressionEvaluator evaluator, VariableResolver resolver){
            this.index = index;
            this.evaluator = evaluator;
            this.resolver = resolver;
            evaluator.tokens.get(index).process(this);
        }

        public BooleanExpressionEvaluator evaluator(){
            return evaluator;
        }

        public VariableResolver resolver() {
            return resolver;
        }
    }

    public record Variable(String name) implements NumberLikeValue, BooleanLikeValue {
        @Override
        public Number numberValue(TokenProcessor processor) {
            return processor.resolver().getNumber(name, processor.relative(1) instanceof NumberLikeValue value ? value.numberValue(processor) : null);
        }

        @Override
        public Boolean booleanValue(TokenProcessor processor) {
            return processor.resolver().getBoolean(name, processor.relative(1) instanceof BooleanLikeValue value ? value.booleanValue(processor) : null);
        }

        @Override
        public void process(TokenProcessor processor) {
            if (numberValue(processor) != null) return;
            BooleanLikeValue.super.process(processor);
        }
    }

    public record BooleanValue(boolean value) implements BooleanLikeValue {
        @Override
        public Boolean booleanValue(TokenProcessor processor) {
            return value;
        }

        @Override
        public void process(TokenProcessor processor) {
            if (processor.relative(-1) instanceof Variable v && v.booleanValue(processor) != null) return;
            BooleanLikeValue.super.process(processor);
        }
    }

    public interface BooleanLikeValue extends Token {
        Boolean booleanValue(TokenProcessor processor);

        default void process(TokenProcessor processor) {
            Boolean variable;
            if ((variable = booleanValue(processor)) == null) return;
            if (!processor.evaluator().operators.isEmpty() && processor.evaluator().operators.peek().symbol().equals("!")) {
                variable = !variable;
                processor.evaluator().operators.pop();
            }
            processor.evaluator().values.push(variable);
        }
    }

    public interface NumberLikeValue extends Token {
        Number numberValue(TokenProcessor processor);
        @Override
        default void process(TokenProcessor processor) {
        }
    }

    public record NumberValue(Number value) implements NumberLikeValue {
        @Override
        public Number numberValue(TokenProcessor processor) {
            return value();
        }
    }

    public record Equality(String symbol) implements Token {
        public boolean applyEquality(double b, double a) {
            return switch (symbol()) {
                case "==" -> a == b;
                case "!=" -> a != b;
                case ">=" -> a >= b;
                case "<=" -> a <= b;
                case ">" -> a > b;
                case "<" -> a < b;
                default -> false;
            };
        }

        @Override
        public void process(TokenProcessor processor) {
            if (processor.relative(-1) instanceof NumberLikeValue n && processor.relative(1) instanceof NumberLikeValue n1) processor.evaluator().values.push(applyEquality(n1.numberValue(processor).doubleValue(), n.numberValue(processor).doubleValue()));
        }
    }

    public record Operator(String symbol) implements Token {

        public boolean operate(boolean b, boolean a) {
            return switch (symbol()) {
                case "&" -> a & b;
                case "|" -> a | b;
                case "&&" -> a && b;
                case "||" -> a || b;
                default -> false;
            };
        }

        @Override
        public void process(TokenProcessor processor) {
            if (!symbol().equals("(") && !symbol().equals(")")){
                while (!processor.evaluator().operators.isEmpty()) {
                    processor.evaluator().values.push(processor.evaluator().operators.pop().operate(processor.evaluator().values.pop(), processor.evaluator().values.pop()));
                }
            } else if (symbol().equals(")")){
                while (processor.evaluator().operators.peek().symbol().equals("(")) {
                    processor.evaluator().values.push(processor.evaluator().operators.pop().operate(processor.evaluator().values.pop(), processor.evaluator().values.pop()));
                }
                processor.evaluator().operators.pop();
                if ("!".equals(processor.evaluator().operators.peek().symbol())) {
                    processor.evaluator().values.push(!processor.evaluator().values.pop());
                    processor.evaluator().operators.pop();
                }
                return;
            }
            processor.evaluator().operators.push(this);
        }
    }
}
