package de.byjoker.myjfql.lang;

import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.exception.LanguageException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColumnFilter {

    public static List<Column> filter(Table table, List<String> argument) {
        return filter(table, argument, null, null);
    }

    public static List<Column> filter(Table table, List<String> argument, ColumnComparator comparator, SortingOrder order) {
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
                            && !key.equals("?")) {
                        throw new LanguageException("Key doesn't exists!");
                    }

                    requirements.add(new Requirement(attributes, filter, state));
                }

                conditions.add(requirements);
            }

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
