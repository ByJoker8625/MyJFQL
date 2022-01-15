package de.byjoker.myjfql.database;

import de.byjoker.myjfql.lang.Requirement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleColumn implements Column {

    private Map<String, Object> content;
    private long createdAt;

    public SimpleColumn(Map<String, Object> content, long createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    public SimpleColumn() {
        this.content = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public Object getItem(String key) {
        return content.get(key);
    }

    @Override
    public String getStringifyItem(String key) {
        return content.get(key).toString();
    }

    @Override
    public void setItem(String key, Object value) {
        content.put(key, value.toString());
    }

    @Override
    public void removeItem(String key) {
        content.remove(key);
    }

    @Override
    public boolean containsItem(String key) {
        return content.containsKey(key);
    }

    @Override
    public boolean containsOrNotNullItem(String key) {
        return content.containsKey(key) && !content.get(key).equals("null");
    }

    @Override
    public boolean matches(List<List<Requirement>> conditions) {
        return conditions.stream().anyMatch(this::passRequirements);
    }

    private boolean adjustState(Requirement.State state, boolean passed) {
        return (state == Requirement.State.NEGATIVE) != passed;
    }

    private boolean matches(String key, String value, Requirement.Filter filter) {
        if (!containsOrNotNullItem(key)) {
            return value.equals("null");
        }

        final String given = getStringifyItem(key);

        switch (filter) {
            case EQUALS: {
                return value.equals(given);
            }
            case EQUALS_IGNORE_CASE: {
                return value.equalsIgnoreCase(given);
            }
            case CONTAINS: {
                return value.contains(given);
            }
            case CONTAINS_EQUALS_IGNORE_CASE: {
                return value.toLowerCase().contains(given.toLowerCase());
            }
            case ARGUMENT_BASED:
            default: {
                if (given.startsWith("$|") && given.endsWith("|$")) {
                    return value.toLowerCase().contains(given.substring(2, given.length() - 2).toLowerCase());
                }

                if (given.startsWith("$") && given.endsWith("$")) {
                    return value.contains(given.substring(1, given.length() - 1));
                }

                if (given.startsWith("|") && given.endsWith("|")) {
                    return value.equalsIgnoreCase(given.substring(1, given.length() - 1));
                }

                return value.equals(given);
            }
        }
    }

    private boolean passRequirements(List<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            final Requirement.Filter filter = requirement.getFilter();
            final Requirement.State state = requirement.getState();

            final String key = requirement.getKey();
            final String value = requirement.getValue();

            switch (key) {
                case "*": {
                    if (!adjustState(state, content.keySet().stream().anyMatch(s -> !matches(s, value, filter)))) {
                        return false;
                    }

                    break;
                }
                case "?": {
                    if (!adjustState(state, content.keySet().stream().anyMatch(s -> matches(s, value, filter)))) {
                        return false;
                    }

                    break;
                }
                default: {
                    if (!adjustState(state, matches(key, value, filter))) {
                        return false;
                    }

                    break;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "SimpleColumn{" +
                "content=" + content +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public Map<String, Object> getContent() {
        return content;
    }

    @Override
    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    @Override
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Deprecated
    public long getCreation() {
        return createdAt;
    }

}
