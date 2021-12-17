package de.byjoker.myjfql.util;

import java.util.Arrays;

public class Requirement {

    private final String[] attributes;
    private final Filter filter;
    private final State state;

    public Requirement(String[] attributes, Filter filter, State state) {
        this.attributes = attributes;
        this.filter = filter;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "attributes=" + Arrays.toString(attributes) +
                ", filter=" + filter +
                ", type=" + state +
                '}';
    }

    public String[] getAttributes() {
        return attributes;
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
