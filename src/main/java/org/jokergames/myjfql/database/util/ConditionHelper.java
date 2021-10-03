package org.jokergames.myjfql.database.util;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConditionHelper {

    public static List<Column> getRequiredColumns(final Table table, final List<String> argument) {
        return getRequiredColumns(table, argument, Sorter.Type.CREATION, null, Sorter.Order.ASC);
    }

    public static List<Column> getRequiredColumns(final Table table, final List<String> argument, final Sorter.Type type, final String sorter, final Sorter.Order order) {
        if (argument.size() == 0) {
            return null;
        }

        try {
            final String[] where = Objects.requireNonNull(MyJFQL.getInstance().getCommandService().getFormatter().formatString(argument))
                    .replace(" or ", " OR ")
                    .split(" OR ");
            final List<List<Requirement>> conditions = new ArrayList<>();

            for (final String require : where) {
                final String[] args = require
                        .replace(" and ", " AND ")
                        .replace(" not ", " NOT ")
                        .replace(" not not ", "")
                        .replace("and ", "AND ")
                        .replace("not ", "NOT ")
                        .replace("not not ", "")
                        .split(" AND ");
                final List<Requirement> requirements = new ArrayList<>();

                for (final String distro : args) {
                    final String[] strings = distro.split(" = ");

                    {
                        strings[0] = strings[0].replace("'", "");
                        strings[1] = strings[1].replace("'", "");
                    }

                    final String key = strings[0]
                            .replace("not ", "")
                            .replace("NOT ", "");

                    if (!table.getStructure().contains(key)
                            && !key.equals("?")
                            && !key.equals("*")) {
                        return null;
                    }

                    if (strings[0].startsWith("NOT ")) {
                        strings[0] = key;
                        requirements.add(new Requirement(strings, Requirement.Type.NEGATIVE));
                    } else {
                        requirements.add(new Requirement(strings, Requirement.Type.POSITIVE));
                    }
                }

                conditions.add(requirements);
            }

            final Predicate<Column> predicate = column -> passConditions(conditions, table.getStructure(), column);

            if (type == Sorter.Type.CREATION) {
                return table.getColumns().stream().filter(predicate).collect(Collectors.toList());
            }

            return table.getColumns(type, order, sorter).stream().filter(predicate).collect(Collectors.toList());
        } catch (Exception ex) {
            return null;
        }

    }

    private static boolean passConditions(final List<List<Requirement>> conditions, final List<String> structure, final Column column) {
        return conditions.stream().anyMatch(requirements -> passRequirements(requirements, structure, column));
    }

    private static boolean passRequirements(final List<Requirement> requirements, final List<String> structure, final Column column) {
        final List<Integer> passed = new ArrayList<>();
        int amountPassed = 0;

        for (int j = 0; j < requirements.size(); j++) {
            final Requirement requirement = requirements.get(j);
            final Requirement.Type type = requirement.getType();

            final String[] strings = requirement.getStrings();
            final String key = strings[0];
            final String value = strings[1];

            final Map<String, Object> content = column.getContent();

            if (key.equals("*")) {
                boolean accept;

                if (value.equals("null")) {
                    accept = structure.stream().noneMatch(str -> content.containsKey(str) && !content.get(str).toString().equals("null"));
                } else {
                    accept = content.values().stream().allMatch(o -> adaptObject(o, value));
                }

                if (!passed.contains(j)) {
                    if (adaptType(type, accept))
                        amountPassed++;
                    passed.add(j);
                }
            } else if (key.equals("?")) {
                boolean accept;

                if (value.equals("null")) {
                    accept = structure.stream().anyMatch(str -> !content.containsKey(str) || (content.containsKey(str) && content.get(str).toString().equals("null")));
                } else {
                    accept = content.values().stream().anyMatch(o -> adaptObject(o, value));
                }

                if (!passed.contains(j)) {
                    if (adaptType(type, accept))
                        amountPassed++;
                    passed.add(j);
                }
            } else {
                boolean accept;

                if (value.equals("null")) {
                    accept = !content.containsKey(key) || (content.containsKey(key) && content.get(key).toString().equals("null"));
                } else {
                    accept = adaptObject(content.get(key), value);
                }

                if (!passed.contains(j)) {
                    if (adaptType(type, accept))
                        amountPassed++;
                    passed.add(j);
                }
            }
        }

        return requirements.size() == amountPassed;
    }

    private static boolean adaptObject(Object o, String value) {
        final String content = o.toString();

        if (value.startsWith("$|") && value.endsWith("|$")) {
            return content.toLowerCase().contains(value.substring(2, value.length() - 2).toLowerCase());
        }

        if (value.startsWith("$") && value.endsWith("$")) {
            return content.contains(value.substring(1, value.length() - 1));
        }

        if (value.startsWith("|") && value.endsWith("|")) {
            return content.equalsIgnoreCase(value.substring(1, value.length() - 1));
        }

        return content.equals(value);
    }

    private static boolean adaptType(Requirement.Type type, boolean accept) {
        return (type == Requirement.Type.NEGATIVE) != accept;
    }

}
