package wily.factoryapi.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import wily.factoryapi.FactoryAPI;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ExpressionEvaluator(String expression, List<Token> tokens, Cache<VariablesMap.View, Number> cache) {
    public static final LoadingCache<String,ExpressionEvaluator> EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(12)).build(CacheLoader.from(ExpressionEvaluator::create));

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(:?\\d+\\.?\\d*)|(\\$\\{[A-Za-z_.]+})|(#[A-Z\\d]+)|([+\\-/*%&|^()])");

    public ExpressionEvaluator(String expression, List<Token> tokens){
        this(expression, tokens, CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(3)).build());
    }

    public static ExpressionEvaluator create(String expression) {
        return new ExpressionEvaluator(expression, tokenize(expression));
    }

    public static ExpressionEvaluator of(String expression) {
        return EXPRESSION_CACHE.getUnchecked(expression);
    }

    public Number evaluateCached(VariableResolver variableResolver) {
        // Disabled cache, thinking in a better solution :)
        //return cache.asMap().computeIfAbsent(variableResolver.getView(), i-> evaluate(variableResolver));
        return evaluate(variableResolver);
    }

    public Number evaluate(VariableResolver variableResolver) {
        Stack<Value> values = new Stack<>();
        Stack<Operator> operators = new Stack<>();

        try {
            for (Token token : tokens) {
                token.process(variableResolver, values, operators);
            }

            while (!operators.isEmpty()) {
                values.push(operators.pop().operate(values.pop(), values.pop()));
            }

            return values.pop().value();
        } catch (Exception e) {
            FactoryAPI.LOGGER.warn("Incorrect expression syntax: {} \nExpression: {}  \nValues: {} \nOperators: {}", e.getMessage(), toString(), values, operators);
            return 0;
        }
    }

    public static List<Token> tokenize(String expression) {
        Matcher matcher = TOKEN_PATTERN.matcher(expression);
        List<Token> tokens = new ArrayList<>();

        while (matcher.find()) {
            String token;

            if (matcher.group(1) != null) {
                token = matcher.group(1);
                boolean isFallback = token.startsWith(":");
                if (isFallback) token = token.substring(1);
                tokens.add(token.contains(".") ? Value.of(Double.parseDouble(token), isFallback) : Value.of(Integer.parseInt(token), isFallback));
            }
            else if (matcher.group(2) != null) {
                token = matcher.group(2);
                String variableName = token.substring(2, token.length() - 1);
                tokens.add(new Variable(variableName));
            }
            else if (matcher.group(3) != null) {
                long colorValue = Long.parseLong(matcher.group(3).substring(1), 16);
                if (colorValue > Integer.MAX_VALUE) {
                    colorValue -= (1L << 32);
                }
                tokens.add(Value.of(colorValue));
            }
            else if (matcher.group(4) != null) {
                tokens.add(new Operator(matcher.group(4).charAt(0)));
            }
        }

        if (!tokens.isEmpty() && tokens.get(0) instanceof Operator o && (o.symbol == '-' || o.symbol == '+')) tokens.add(0,Value.of(0));
        return tokens;
    }



    public interface Token {
        void process(VariableResolver variableResolver, Stack<Value> values, Stack<Operator> operators);
    }

    public record Value(Number value, boolean isInteger, boolean isFallback) implements Token {
        public static Value of(Number value, boolean isFallback){
            return new Value(value, value instanceof Integer || value instanceof Long, isFallback);
        }
        public static Value of(Number value){
            return of(value, false);
        }

        public boolean isValid(){
            return value != null;
        }

        @Override
        public void process(VariableResolver variableResolver, Stack<Value> values, Stack<Operator> operators) {
            if (!isFallback() || !values.peek().isValid()) {
                if (isFallback()) values.pop();
                values.push(this);
            }
        }
    }

    public record Variable(String name) implements Token {
        @Override
        public void process(VariableResolver variableResolver, Stack<Value> values, Stack<Operator> operators) {
            values.push(Value.of(variableResolver.getNumber(name(), null)));
        }
    }

    public record Operator(char symbol) implements Token {

        public boolean hasPrecedence(Operator second) {
            if (second.symbol() == '(' || second.symbol() == ')')
                return false;
            return (symbol() != '*' && symbol() != '/') || (second.symbol() != '+' && second.symbol() != '-');
        }

        public Value operate(Value b, Value a) {
            boolean integers = a.isInteger() && b.isInteger();
            return switch (symbol()) {
                case '+' -> integers ? Value.of(a.value().intValue() + b.value().intValue()) : Value.of(a.value().doubleValue() + b.value().doubleValue());
                case '-' -> integers ? Value.of(a.value().intValue() - b.value().intValue()) : Value.of(a.value().doubleValue() - b.value().doubleValue());
                case '*' -> integers ? Value.of(a.value().intValue() * b.value().intValue()) : Value.of(a.value().doubleValue() * b.value().doubleValue());
                case '^' ->  Value.of(Math.pow(a.value().doubleValue(), b.value().doubleValue()));
                case '/' -> {
                    if (b.value().doubleValue() == 0) throw new ArithmeticException("Cannot divide by zero");
                    yield integers ? Value.of(a.value().intValue() / b.value().intValue()) : Value.of(a.value().doubleValue() / b.value().doubleValue());
                }
                case '%' -> integers ? Value.of(a.value().intValue() % b.value().intValue()) : Value.of(a.value().doubleValue() % b.value().doubleValue());
                case '&' -> Value.of(a.value().intValue() & b.value().intValue());
                case '|' -> Value.of(a.value().intValue() | b.value().intValue());
                default -> throw new IllegalArgumentException("Unsupported operator: " + symbol());
            };
        }

        @Override
        public void process(VariableResolver variableResolver, Stack<Value> values, Stack<Operator> operators) {
            if (symbol() != '(' && symbol() != ')'){
                while (!operators.isEmpty() && hasPrecedence(operators.peek())) {
                    values.push(operators.pop().operate(values.pop(), values.pop()));
                }
            } else if (symbol() == ')'){
                while (operators.peek().symbol() != '(') {
                    values.push(operators.pop().operate(values.pop(), values.pop()));
                }
                operators.pop();
                return;
            }
            operators.push(this);
        }
    }
}
