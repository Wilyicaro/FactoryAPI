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

public record BooleanExpressionEvaluator(String expression, List<Token> tokens, Stack<Boolean> values, Stack<Operator> operators) {
    public static final LoadingCache<String, BooleanExpressionEvaluator> EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).build(CacheLoader.from(BooleanExpressionEvaluator::create));

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\\$\\{[\\dA-Za-z_.-]+})|(:?(?:false|true))|(:?\\d+\\.?\\d*)|(!|&&|&|\\|\\||\\|)|(==|!=|>=|<=|>|<)");

    public BooleanExpressionEvaluator(String expression, List<Token> tokens){
        this(expression, tokens, new Stack<>(), new Stack<>());
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
            for (Token token : tokens) {
                token.process(this, variableResolver);
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
        void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver);
        default Token relativeToken(BooleanExpressionEvaluator evaluator, int ordinal){
            int tokenIndex = evaluator.tokens.indexOf(this) + ordinal;
            return tokenIndex < evaluator.tokens.size() && tokenIndex >= 0 ? null : evaluator.tokens.get(tokenIndex);
        }
    }

    public record Variable(String name) implements NumberLikeValue, BooleanLikeValue {
        @Override
        public Number numberValue(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            return variableResolver.getNumber(name, relativeToken(evaluator, 1) instanceof NumberLikeValue value ? value.numberValue(evaluator, variableResolver) : null);
        }

        @Override
        public Boolean booleanValue(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            return variableResolver.getBoolean(name, relativeToken(evaluator, 1) instanceof BooleanLikeValue value ? value.booleanValue(evaluator, variableResolver) : null);
        }

        @Override
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            if (numberValue(evaluator, variableResolver) != null) return;
            BooleanLikeValue.super.process(evaluator, variableResolver);
        }
    }

    public record BooleanValue(boolean value) implements BooleanLikeValue {
        @Override
        public Boolean booleanValue(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            return value;
        }

        @Override
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            if (relativeToken(evaluator, -1) instanceof Variable v && v.booleanValue(evaluator, variableResolver) != null) return;
            BooleanLikeValue.super.process(evaluator, variableResolver);
        }
    }

    public interface BooleanLikeValue extends Token {
        Boolean booleanValue(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver);
        default void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            Boolean variable;
            if ((variable = booleanValue(evaluator, variableResolver)) == null) return;
            if (!evaluator.operators.isEmpty() && evaluator.operators.peek().symbol().equals("!")) {
                variable = !variable;
                evaluator.operators.pop();
            }
            evaluator.values.push(variable);
        }
    }

    public interface NumberLikeValue extends Token {
        Number numberValue(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver);
        @Override
        default void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
        }
    }

    public record NumberValue(Number value) implements NumberLikeValue {
        @Override
        public Number numberValue(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
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
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            int tokenIndex = evaluator.tokens().indexOf(this);
            if (tokenIndex > 0 && tokenIndex < evaluator.tokens().size() - 1 && evaluator.tokens().get(tokenIndex - 1) instanceof NumberLikeValue n && evaluator.tokens().get(tokenIndex + 1) instanceof NumberLikeValue n1) evaluator.values.push(applyEquality(n1.numberValue(evaluator, variableResolver).doubleValue(), n.numberValue(evaluator, variableResolver).doubleValue()));
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
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver) {
            if (!symbol().equals("(") && !symbol().equals(")")){
                while (!evaluator.operators.isEmpty()) {
                    evaluator.values.push(evaluator.operators.pop().operate(evaluator.values.pop(), evaluator.values.pop()));
                }
            } else if (symbol().equals(")")){
                while (evaluator.operators.peek().symbol().equals("(")) {
                    evaluator.values.push(evaluator.operators.pop().operate(evaluator.values.pop(), evaluator.values.pop()));
                }
                evaluator.operators.pop();
                if ("!".equals(evaluator.operators.peek().symbol())) {
                    evaluator.values.push(!evaluator.values.pop());
                    evaluator.operators.pop();
                }
                return;
            }
            evaluator.operators.push(this);
        }
    }
}
