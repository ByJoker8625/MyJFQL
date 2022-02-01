package de.byjoker.myjfql.lang;

import java.util.Arrays;

public class Requirement {

    private final Method method;
    private final State state;

    private final String key;
    private final String value;

    public Requirement(String[] attributes, Method method, State state) {
        this.key = attributes[0];
        this.value = attributes[1];
        this.method = method;
        this.state = state;
    }

    public Requirement(String key, State state, Method method, String value) {
        this.key = key;
        this.state = state;
        this.method = method;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "method=" + method +
                ", state=" + state +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public Method getFilter() {
        return method;
    }

    public State getState() {
        return state;
    }

    public enum Method {
        EQUALS("equals:"),
        EQUALS_IGNORE_CASE("equals_ignore_case:"),
        CONTAINS("contains:"),
        CONTAINS_EQUALS_IGNORE_CASE("contains_ignore_case:"),
        ARGUMENT_BASED("argument_based:");

        private final String method;

        Method(String method) {
            this.method = method;
        }

        public static Method getFilterByMethod(String method) {
            return Arrays.stream(Method.values()).filter(filter -> method.startsWith(filter.getMethod()))
                    .findFirst()
                    .orElse(ARGUMENT_BASED);
        }

        public String getMethod() {
            return method;
        }
    }

    public enum State {
        NOT,
        IS
    }
}
