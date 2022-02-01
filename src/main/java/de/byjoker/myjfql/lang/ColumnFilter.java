package de.byjoker.myjfql.lang;

import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.RelationalTable;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.exception.LanguageException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColumnFilter {

    public static List<Column> filterByCommandLineArguments(Table table, List<String> argument) {
        return ColumnFilter.filterByCommandLineArguments(table, argument, null, null);
    }

    public static List<Column> filter(Table table, List<List<Requirement>> conditions) {
        return filter(table, conditions, null, null);
    }

    public static List<Column> filterByCommandLineArguments(Table table, List<String> argument, ColumnComparator comparator, SortingOrder order) {
        if (argument.size() == 0) {
            throw new LanguageException("No arguments given!");
        }

        try {
            final String[] conditionSets = Objects.requireNonNull(new JFQLCommandFormatter().formatString(argument))
                    .replace(" OR ", " or ")
                    .split(" or ");
            final List<List<Requirement>> conditions = new ArrayList<>();

            for (String conditionSet : conditionSets) {
                final String[] args = conditionSet
                        .replace(" AND ", " and ")
                        .split(" and ");
                final List<Requirement> requirements = new ArrayList<>();

                for (String distro : args) {
                    Requirement.Method method = null;
                    Requirement.State state = null;
                    String[] attributes = null;

                    if (distro.contains(" !== ")) {
                        attributes = distro.split(" !== ");
                        method = Requirement.Method.EQUALS;
                        state = Requirement.State.NOT;
                    } else if (distro.contains(" != ")) {
                        attributes = distro.split(" != ");
                        state = Requirement.State.NOT;
                    }

                    if (state == null) {
                        if (distro.contains(" === ")) {
                            attributes = distro.split(" === ");
                            method = Requirement.Method.EQUALS;
                            state = Requirement.State.IS;
                        } else if (distro.contains(" == ")) {
                            attributes = distro.split(" == ");
                            state = Requirement.State.IS;
                        } else if (distro.contains(" = ")) {
                            attributes = distro.split(" = ");
                            state = Requirement.State.IS;
                        }
                    }

                    if (attributes == null) {
                        throw new LanguageException("Condition format makes no sense!");
                    }

                    {
                        attributes[0] = attributes[0].replace("'", "");
                        attributes[1] = attributes[1].replace("'", "");
                    }

                    if (method == null) {
                        method = Requirement.Method.getFilterByMethod(attributes[1]);
                        attributes[1] = attributes[1].replace(method.getMethod(), "");
                    }

                    final String key = attributes[0];

                    if (table instanceof RelationalTable && !table.getStructure().contains(key)
                            && !key.equals("?")) {
                        throw new LanguageException("Specified fields don't match table structure!");
                    }

                    requirements.add(new Requirement(attributes, method, state));
                }

                conditions.add(requirements);
            }

            return filter(table, conditions, comparator, order);
        } catch (Exception ex) {
            throw new LanguageException(ex);
        }
    }

    public static List<Column> filter(Table table, List<List<Requirement>> conditions, ColumnComparator comparator, SortingOrder order) {
        try {
            final List<Column> columns = table.getColumns().stream().filter(column -> column.matches(conditions)).collect(Collectors.toList());

            if (comparator != null) {
                columns.sort(comparator);

                if (order == SortingOrder.DESC)
                    Collections.reverse(columns);
            }

            return columns;
        } catch (Exception ex) {
            throw new LanguageException(ex);
        }
    }

}
