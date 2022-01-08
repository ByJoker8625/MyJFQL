package de.byjoker.myjfql.core.lang;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.exception.LanguageException;
import de.byjoker.myjfql.util.Requirement;
import de.byjoker.myjfql.util.Sorter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConditionFormatter {

    public static List<Column> getRequiredColumns(Table table, List<String> argument) {
        return getRequiredColumns(table, argument, Sorter.Type.CREATION, null, Sorter.Order.ASC);
    }

    public static List<Column> getRequiredColumns(Table table, List<String> argument, Sorter.Type sortType, String sorter, Sorter.Order order) {
        if (argument.size() == 0) {
            throw new LanguageException("No arguments given!");
        }

        try {
            final String[] conditionSets = Objects.requireNonNull(MyJFQL.getInstance().getFormatter().formatString(argument))
                    .replace(" OR ", " or ")
                    .split(" or ");
            final List<List<Requirement>> conditions = new ArrayList<>();

            for (String conditionSet : conditionSets) {
                final String[] args = conditionSet
                        .replace(" AND ", " and ")
                        .split(" and ");
                final List<Requirement> requirements = new ArrayList<>();

                for (String distro : args) {
                    Requirement.Filter filter = null;
                    Requirement.State state = null;
                    String[] attributes = null;

                    if (distro.contains(" !== ")) {
                        attributes = distro.split(" !== ");
                        filter = Requirement.Filter.EQUALS;
                        state = Requirement.State.NEGATIVE;
                    } else if (distro.contains(" != ")) {
                        attributes = distro.split(" != ");
                        state = Requirement.State.NEGATIVE;
                    }

                    if (state == null) {
                        if (distro.contains(" === ")) {
                            attributes = distro.split(" === ");
                            filter = Requirement.Filter.EQUALS;
                            state = Requirement.State.POSITIVE;
                        } else if (distro.contains(" == ")) {
                            attributes = distro.split(" == ");
                            state = Requirement.State.POSITIVE;
                        } else if (distro.contains(" = ")) {
                            attributes = distro.split(" = ");
                            state = Requirement.State.POSITIVE;
                        }
                    }

                    if (attributes == null) {
                        throw new LanguageException("Condition format makes no sense!");
                    }

                    {
                        attributes[0] = attributes[0].replace("'", "");
                        attributes[1] = attributes[1].replace("'", "");
                    }

                    if (filter == null) {
                        filter = Requirement.Filter.getFilterByMethod(attributes[1]);
                        attributes[1] = attributes[1].replace(filter.getMethod(), "");
                    }

                    final String key = attributes[0];

                    if (!table.getStructure().contains(key)
                            && !key.equals("?")
                            && !key.equals("*")) {
                        throw new LanguageException("Key doesn't exists!");
                    }

                    requirements.add(new Requirement(attributes, filter, state));
                }

                conditions.add(requirements);
            }

            final Predicate<Column> predicate = column -> passConditions(conditions, table.getStructure(), column);

            if (sortType == Sorter.Type.CREATION) {
                return table.getColumns().stream().filter(predicate).collect(Collectors.toList());
            }

            return table.getColumns(sortType, order, sorter).stream().filter(predicate).collect(Collectors.toList());
        } catch (Exception ex) {
            throw new LanguageException(ex);
        }
    }

    private static boolean passConditions(List<List<Requirement>> conditions, Collection<String> structure, Column column) {
        return conditions.stream().anyMatch(requirements -> passRequirements(requirements, structure, column));
    }

    private static boolean passRequirements(List<Requirement> requirements, Collection<String> structure, Column column) {
        final List<Integer> passed = new ArrayList<>();
        int amountPassed = 0;

        for (int j = 0; j < requirements.size(); j++) {
            final Requirement requirement = requirements.get(j);

            final Requirement.Filter filter = requirement.getFilter();
            final Requirement.State state = requirement.getState();

            final String[] attributes = requirement.getAttributes();
            final String key = attributes[0];
            final String value = attributes[1];

            final Map<String, Object> content = column.getContent();

            if (key.equals("*")) {
                boolean accept;

                if (value.equals("null")) {
                    accept = structure.stream().noneMatch(str -> content.containsKey(str) && !content.get(str).toString().equals("null"));
                } else {
                    accept = content.values().stream().allMatch(o -> adaptObject(o, value, filter));
                }

                if (!passed.contains(j)) {
                    if (adaptType(state, accept))
                        amountPassed++;
                    passed.add(j);
                }
            } else if (key.equals("?")) {
                boolean accept;

                if (value.equals("null")) {
                    accept = structure.stream().anyMatch(str -> !content.containsKey(str) || (content.containsKey(str) && content.get(str).toString().equals("null")));
                } else {
                    accept = content.values().stream().anyMatch(o -> adaptObject(o, value, filter));
                }

                if (!passed.contains(j)) {
                    if (adaptType(state, accept))
                        amountPassed++;
                    passed.add(j);
                }
            } else {
                boolean accept;

                if (value.equals("null")) {
                    accept = !content.containsKey(key) || (content.containsKey(key) && content.get(key).toString().equals("null"));
                } else {
                    accept = adaptObject(content.get(key), value, filter);
                }

                if (!passed.contains(j)) {
                    if (adaptType(state, accept))
                        amountPassed++;
                    passed.add(j);
                }
            }
        }

        return requirements.size() == amountPassed;
    }

    private static boolean adaptObject(Object required, String given, Requirement.Filter filter) {
        final String content = required.toString();

        switch (filter) {
            case EQUALS: {
                return content.equals(given);
            }
            case EQUALS_IGNORE_CASE: {
                return content.equalsIgnoreCase(given);
            }
            case CONTAINS: {
                return content.contains(given);
            }
            case CONTAINS_EQUALS_IGNORE_CASE: {
                return content.toLowerCase().contains(given.toLowerCase());
            }
            case ARGUMENT_BASED:
            default: {
                if (given.startsWith("$|") && given.endsWith("|$")) {
                    return content.toLowerCase().contains(given.substring(2, given.length() - 2).toLowerCase());
                }

                if (given.startsWith("$") && given.endsWith("$")) {
                    return content.contains(given.substring(1, given.length() - 1));
                }

                if (given.startsWith("|") && given.endsWith("|")) {
                    return content.equalsIgnoreCase(given.substring(1, given.length() - 1));
                }

                return content.equals(given);
            }
        }
    }

    private static boolean adaptType(Requirement.State state, boolean accept) {
        return (state == Requirement.State.NEGATIVE) != accept;
    }

}
