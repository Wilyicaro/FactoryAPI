package wily.factoryapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.util.Mth;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.config.FactoryCommonOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ExpressionEvaluator(String expression, List<Token> tokens, Stack<Value> values, Stack<Operator> operators, Bearer<Function> function) {
    public static final LoadingCache<String,ExpressionEvaluator> EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(12)).build(CacheLoader.from(ExpressionEvaluator::create));

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\\$\\{[\\dA-Za-z_.-]+})|(:?\\d+\\.?\\d*)|(#[A-Z\\d]+)|([+\\-/*%&|^()]|>>|<<)|(sqrt|cbrt|pow|min|max|clamp)");

    public ExpressionEvaluator(String expression, List<Token> tokens){
        this(expression, tokens, new Stack<>(), new Stack<>(), Bearer.of(null));
    }

    public static ExpressionEvaluator create(String expression) {
        return new ExpressionEvaluator(expression, tokenize(expression));
    }

    public static ExpressionEvaluator of(String expression) {
        return EXPRESSION_CACHE.getUnchecked(expression);
    }

    public Number evaluate(VariableResolver variableResolver) {
        values.clear();
        operators.clear();
        function.set(null);
        try {
            for (Token token : tokens) {
                token.process(this, variableResolver);
            }

            while (!operators.isEmpty()) {
                values.push(operators.pop().operate(values.pop(), values.pop()));
            }

            return values.pop().value();
        } catch (Exception e) {
            if (FactoryCommonOptions.EXPRESSION_FAIL_LOGGING.get()) FactoryAPI.LOGGER.warn("Incorrect expression syntax: {} \nExpression: {}", e.getMessage(), toString());
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
                String variableName = token.substring(2, token.length() - 1);
                tokens.add(new Variable(variableName));
           } else if (matcher.group(2) != null) {
               token = matcher.group(2);
               boolean isFallback = token.startsWith(":");
               if (isFallback) token = token.substring(1);
               tokens.add(token.contains(".") ? Value.of(Double.parseDouble(token), isFallback) : Value.of(Integer.parseInt(token), isFallback));
           } else if (matcher.group(3) != null) {
                long colorValue = Long.parseLong(matcher.group(3).substring(1), 16);
                if (colorValue > Integer.MAX_VALUE) {
                    colorValue -= (1L << 32);
                }
                tokens.add(Value.of(colorValue));
            } else if (matcher.group(4) != null) {
                tokens.add(new Operator(matcher.group(4)));
            } else if (matcher.group(5) != null) {
                tokens.add(Function.of(matcher.group(5)));
            }
        }

        if (!tokens.isEmpty() && tokens.get(0) instanceof Operator o && (o.symbol.equals("-") || o.symbol.equals("+"))) tokens.add(0,Value.of(0));
        return tokens;
    }

    public void pushValueOrApplyFunction(Value value) {
        if (function.isEmpty()){
            values.push(value);
        } else {
            function.get().args.add(value);
            if (function.get().canEvaluate()){
                values.push(function.get().tryEvaluate());
                function.get().args.clear();
                function.set(null);
            }
        }

    }

    public interface Token {
        void process(ExpressionEvaluator evaluator, VariableResolver variableResolver);
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
        public void process(ExpressionEvaluator evaluator, VariableResolver variableResolver) {
            if (!isFallback() || !evaluator.values.peek().isValid()) {
                if (isFallback()) evaluator.values.pop();
                evaluator.pushValueOrApplyFunction(this);
            }
        }
    }

    public record Variable(String name) implements Token {
        @Override
        public void process(ExpressionEvaluator evaluator, VariableResolver variableResolver) {
            evaluator.pushValueOrApplyFunction(Value.of(variableResolver.getNumber(name(), null)));
        }
    }

    public record Operator(String symbol) implements Token {

        public boolean hasPrecedence(Operator second) {
            if (second.symbol().equals("(") || second.symbol().equals(")"))
                return false;
            return (!symbol().equals("*") && !symbol().equals("/")) || (!second.symbol().equals("+") && !second.symbol().equals("-"));
        }

        public Value operate(Value b, Value a) {
            boolean integers = a.isInteger() && b.isInteger();
            return switch (symbol()) {
                case "+" -> integers ? Value.of(a.value().intValue() + b.value().intValue()) : Value.of(a.value().doubleValue() + b.value().doubleValue());
                case "-" -> integers ? Value.of(a.value().intValue() - b.value().intValue()) : Value.of(a.value().doubleValue() - b.value().doubleValue());
                case "*" -> integers ? Value.of(a.value().intValue() * b.value().intValue()) : Value.of(a.value().doubleValue() * b.value().doubleValue());
                case "/" -> {
                    if (b.value().doubleValue() == 0) throw new ArithmeticException("Cannot divide by zero");
                    yield integers ? Value.of(a.value().intValue() / b.value().intValue()) : Value.of(a.value().doubleValue() / b.value().doubleValue());
                }
                case "%" -> integers ? Value.of(a.value().intValue() % b.value().intValue()) : Value.of(a.value().doubleValue() % b.value().doubleValue());
                case "&" -> Value.of(a.value().intValue() & b.value().intValue());
                case "|" -> Value.of(a.value().intValue() | b.value().intValue());
                case "^" ->  Value.of(a.value().intValue() ^ b.value().intValue());
                case ">>" -> Value.of(a.value().intValue() >> b.value().intValue());
                case "<<" -> Value.of(a.value().intValue() << b.value().intValue());
                default -> throw new IllegalArgumentException("Unsupported operator: " + symbol());
            };
        }

        @Override
        public void process(ExpressionEvaluator evaluator, VariableResolver variableResolver) {
            if (!symbol().equals("(") && !symbol().equals(")")){
                while (!evaluator.operators.isEmpty() && hasPrecedence(evaluator.operators.peek())) {
                    evaluator.values.push(evaluator.operators.pop().operate(evaluator.values.pop(), evaluator.values.pop()));
                }
            } else if (symbol().equals(")")){
                while (!evaluator.operators.peek().symbol().equals("(")) {
                    evaluator.values.push(evaluator.operators.pop().operate(evaluator.values.pop(), evaluator.values.pop()));
                }
                evaluator.operators.pop();
                return;
            }
            evaluator.operators.push(this);
        }
    }

    public record Function(String type, List<Value> args, int argsCount) implements Token {
        public static Function of(String type){
            return new Function(type, new ArrayList<>(), argsCountByType(type));
        }

        public static int argsCountByType(String type) {
            return switch (type){
                case "sqrt","cbrt" -> 1;
                case "min","max" -> 2;
                case "clamp" -> 3;
                default -> throw new IllegalArgumentException("Unsupported function: " + type);
            };
        }

        public boolean integers(){
            for (int i = 0; i < argsCount; i++) {
                if (!args.get(i).isInteger()) return false;
            }
            return true;
        }

        public boolean canEvaluate(){
            return args.size() >= argsCount;
        }

        public Value tryEvaluate(){
            return switch (type){
                case "sqrt" -> Value.of(Math.sqrt(args.get(0).value().doubleValue()));
                case "cbrt" -> Value.of(Math.cbrt(args.get(0).value().doubleValue()));
                case "pow" -> Value.of(Math.pow(args.get(0).value().doubleValue(),args.get(1).value().doubleValue()));
                case "min" -> integers() ? Value.of(Math.min(args.get(0).value().intValue(), args.get(1).value().intValue())) : Value.of(Math.min(args.get(0).value().doubleValue(), args.get(1).value().doubleValue()));
                case "max" -> integers() ? Value.of(Math.max(args.get(0).value().intValue(), args.get(1).value().intValue())) : Value.of(Math.max(args.get(0).value().doubleValue(), args.get(1).value().doubleValue()));
                case "clamp" -> integers() ? Value.of(Mth.clamp(args.get(0).value().intValue(), args.get(1).value().intValue(), args.get(2).value().intValue())) : Value.of(Mth.clamp(args.get(0).value().doubleValue(), args.get(1).value().doubleValue(), args.get(2).value().doubleValue()));
                default -> throw new IllegalArgumentException("Unsupported function: " + type);
            };
        }

        @Override
        public void process(ExpressionEvaluator evaluator, VariableResolver variableResolver) {
            if (evaluator.function.isPresent()){
                String message = "Last function with incomplete arguments: %s with %s of %s".formatted(evaluator.function.get().type, evaluator.function.get().args.size(), evaluator.function.get().argsCount);
                evaluator.function.get().args.clear();
                evaluator.function.set(this);
                throw new UnsupportedOperationException(message);
            }
            evaluator.function.set(this);
        }
    }
}
