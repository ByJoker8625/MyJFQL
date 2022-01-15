package de.byjoker.myjfql.lang;

import java.util.Arrays;

public class Requirement {

    private final Filter filter;
    private final State state;

    private final String key;
    private final String value;

    public Requirement(String[] attributes, Filter filter, State state) {
        this.key = attributes[0];
        this.value = attributes[1];
        this.filter = filter;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "filter=" + filter +
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

    public Filter getFilter() {
        return filter;
    }

    public State getState() {
        return state;
    }

    public enum Filter {
        EQUALS("equals:"),
        EQUALS_IGNORE_CASE("equals_ignore_case:"),
        CONTAINS("contains:"),
        CONTAINS_EQUALS_IGNORE_CASE("contains_ignore_case:"),
        ARGUMENT_BASED("argument_based:");

        private final String method;

        Filter(String method) {
            this.method = method;
        }

        public static Filter getFilterByMethod(String method) {
            return Arrays.stream(Filter.values()).filter(filter -> method.startsWith(filter.getMethod()))
                    .findFirst()
                    .orElse(ARGUMENT_BASED);
        }

        public String getMethod() {
            return method;
        }
    }

    public enum State {
        NEGATIVE,
        POSITIVE
    }
}
