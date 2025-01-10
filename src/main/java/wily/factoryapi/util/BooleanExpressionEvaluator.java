package wily.factoryapi.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import wily.factoryapi.FactoryAPI;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record BooleanExpressionEvaluator(String expression, List<Token> tokens, Cache<VariablesMap.View, Boolean> cache) {
    public static final LoadingCache<String, BooleanExpressionEvaluator> EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).build(CacheLoader.from(BooleanExpressionEvaluator::create));

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(:?\\d+\\.?\\d*)|(\\$\\{[A-Za-z_.]+}(?::false|:true)?)|(!|&&|&|\\|\\||\\|)|(==|!=|>=|<=|>|<)");

    public BooleanExpressionEvaluator(String expression, List<Token> tokens){
        this(expression, tokens, CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build());
    }

    public static BooleanExpressionEvaluator create(String expression) {
        return new BooleanExpressionEvaluator(expression, tokenize(expression));
    }

    public static BooleanExpressionEvaluator of(String expression) {
        return EXPRESSION_CACHE.getUnchecked(expression);
    }

    public Boolean evaluateCached(VariableResolver variableResolver) {
        // Disabled cache, thinking in a better solution :)
        //return cache.asMap().computeIfAbsent(variableResolver.getView(), i-> evaluate(variableResolver));
        return evaluate(variableResolver);
    }

    public Boolean evaluate(VariableResolver variableResolver) {
        Stack<Boolean> values = new Stack<>();
        Stack<Operator> operators = new Stack<>();

        try {
            for (Token token : tokens) {
                token.process(this, variableResolver, values, operators);
            }

            while (!operators.isEmpty()) {
                values.push(operators.pop().operate(values.pop(), values.pop()));
            }

            return values.pop();
        } catch (Exception e) {
            FactoryAPI.LOGGER.warn("Incorrect expression syntax: {} \nExpression: {}  \nValues: {} \nOperators: {}", e.getMessage(), toString(), values, operators);
            return false;
        }
    }

    public static List<Token> tokenize(String expression) {
        Matcher matcher = TOKEN_PATTERN.matcher(expression);
        List<Token> tokens = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(new NumberValue(Double.parseDouble(matcher.group(1))));
            }
            else if (matcher.group(2) != null) {
                String token = matcher.group(2);
                boolean endsWithTrue = token.endsWith("true");
                boolean hasFallback = token.endsWith("false") || endsWithTrue;
                String variableName = token.substring(2, token.length() - (hasFallback ? endsWithTrue ? 6 : 7 : 1));
                tokens.add(new Variable(variableName, hasFallback ? endsWithTrue : null));
            }
            // Operator
            else if (matcher.group(3) != null) {
                tokens.add(new Operator(matcher.group(3)));
            }
            else if (matcher.group(4) != null) {
                tokens.add(new Equality(matcher.group(4)));
            }
        }
        return tokens;
    }



    public interface Token {
        void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver, Stack<Boolean> values, Stack<Operator> operators);
    }

    public record Variable(String name, Boolean fallbackValue) implements NumberLikeValue {
        @Override
        public Number value(VariableResolver variableResolver) {
            return variableResolver.getNumber(name, null);
        }

        @Override
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver, Stack<Boolean> values, Stack<Operator> operators) {
            if (value(variableResolver) != null) return;
            boolean variable = variableResolver.getBoolean(name(), fallbackValue);
            if (!operators.isEmpty() && operators.peek().symbol().equals("!")) {
                variable = !variable;
                operators.pop();
            }
            values.push(variable);
        }
    }

    public interface NumberLikeValue extends Token {
        Number value(VariableResolver variableResolver);
        @Override
        default void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver, Stack<Boolean> values, Stack<Operator> operators) {
        }
    }

    public record NumberValue(Number value) implements NumberLikeValue {
        @Override
        public Number value(VariableResolver variableResolver) {
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
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver, Stack<Boolean> values, Stack<Operator> operators) {
            int tokenIndex = evaluator.tokens().indexOf(this);
            if (tokenIndex > 0 && tokenIndex < evaluator.tokens().size() - 1 && evaluator.tokens().get(tokenIndex - 1) instanceof NumberLikeValue n && evaluator.tokens().get(tokenIndex + 1) instanceof NumberLikeValue n1) values.push(applyEquality(n1.value(variableResolver).doubleValue(), n.value(variableResolver).doubleValue()));
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
        public void process(BooleanExpressionEvaluator evaluator, VariableResolver variableResolver, Stack<Boolean> values, Stack<Operator> operators) {
            if (!symbol().equals("(") && !symbol().equals(")")){
                while (!operators.isEmpty()) {
                    values.push(operators.pop().operate(values.pop(), values.pop()));
                }
            } else if (symbol().equals(")")){
                while (operators.peek().symbol().equals("(")) {
                    values.push(operators.pop().operate(values.pop(), values.pop()));
                }
                operators.pop();
                if ("!".equals(operators.peek().symbol())) {
                    values.push(!values.pop());
                    operators.pop();
                }
                return;
            }
            operators.push(this);
        }
    }
}
