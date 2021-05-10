package org.jokergames.myjfql.util;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConditionHelper {

    public static List<Column> getRequiredColumnRows(final List<Column> columns, final List<String> values) {
        final List<Column> requiredColumnRows = new ArrayList<>();

        columns.forEach(column -> {
            column.getContent().keySet().stream().filter(s -> !values.contains(s)).forEach(s -> column.getContent().remove(s));
            requiredColumnRows.add(column);
        });

        return requiredColumnRows;
    }

    public static List<Column> getRequiredColumns(final Table table, final List<String> argument) {
        return getRequiredColumns(table, argument, Sorter.Type.CREATION, null, Sorter.Order.ASC);
    }

    public static List<Column> getRequiredColumns(final Table table, final List<String> argument, final Sorter.Type type, final String sorter, final Sorter.Order order) {
        final List<Column> requiredColumns = new ArrayList<>();

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

            List<Column> columns = null;

            if (type == Sorter.Type.CREATION) {
                columns = table.getColumns();
            } else {
                columns = table.getColumns(type, order, sorter);
            }

            columns.forEach(column -> {
                if (conditions.stream().anyMatch(requirements -> handleRequirements(requirements, table, column) == requirements.size())) {
                    requiredColumns.add(column);
                }
            });
        } catch (Exception ex) {
            return null;
        }

        return requiredColumns;
    }

    private static int handleRequirements(final List<Requirement> requirements, final Table table, final Column column) {
        final List<Integer> finishedRequirements = new ArrayList<>();
        int finished = 0;

        final List<String> tableStructure = table.getStructure();

        for (int j = 0; j < requirements.size(); j++) {
            final Requirement requirement = requirements.get(j);
            final Requirement.Type type = requirement.getType();

            final String[] strings = requirement.getStrings();
            final String key = strings[0];
            final String value = strings[1];

            final Map<String, Object> content = column.getContent();

            if (key.equals("*")) {
                boolean accept = true;

                if (value.equals("null")) {
                    accept = tableStructure.stream().noneMatch(str -> content.containsKey(str) && !content.get(str).toString().equals("null"));
                } else {
                    for (String cck : tableStructure) {
                        if (!content.containsKey(cck)) {
                            accept = false;
                            break;
                        }
                        final String contentValue = content.get(cck).toString();

                        if (value.startsWith("$") && value.endsWith("$")) {
                            final String crs = value.replace("$", "");

                            if (!contentValue.contains(crs)) {
                                accept = false;
                                break;
                            }
                        } else if (!contentValue.equals(value)) {
                            accept = false;
                            break;
                        }
                    }
                }

                if (!finishedRequirements.contains(j)) {
                    if (adaptType(type, accept))
                        finished++;
                    finishedRequirements.add(j);
                }
            } else if (key.equals("?")) {
                boolean accept = false;

                if (value.equals("null")) {
                    accept = tableStructure.stream().anyMatch(str -> !content.containsKey(str) || (content.containsKey(str) && content.get(str).toString().equals("null")));
                } else {
                    for (final String cck : tableStructure) {
                        String contentValue = null;

                        if (!content.containsKey(cck)) {
                            contentValue = "null";
                        } else {
                            contentValue = content.get(cck).toString();
                        }

                        if (value.startsWith("$") && value.endsWith("$")) {
                            final String crs = value.replace("$", "");

                            if (contentValue.contains(crs)) {
                                accept = true;
                                break;
                            }
                        } else if (contentValue.equals(value)) {
                            accept = true;
                            break;
                        }
                    }
                }

                if (!finishedRequirements.contains(j)) {
                    if (adaptType(type, accept))
                        finished++;
                    finishedRequirements.add(j);
                }
            } else {
                boolean accept = false;

                if (value.equals("null")) {
                    if (!content.containsKey(key)) {
                        accept = true;
                    } else if (content.get(key).toString().equals("null")) {
                        accept = true;
                    }
                } else if (content.containsKey(key)) {
                    final String contentValue = content.get(key).toString();

                    if (value.startsWith("$") && value.endsWith("$")) {
                        accept = contentValue.contains(value.replace("$", ""));
                    } else {
                        accept = contentValue.equals(value);
                    }
                }

                if (!finishedRequirements.contains(j)) {
                    if (adaptType(type, accept))
                        finished++;
                    finishedRequirements.add(j);
                }

            }

        }

        return finished;
    }

    private static boolean adaptType(Requirement.Type type, boolean accept) {
        if (type == Requirement.Type.NEGATIVE) {
            return !accept;
        }

        return accept;
    }

}
