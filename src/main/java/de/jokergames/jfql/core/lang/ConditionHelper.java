package de.jokergames.jfql.core.lang;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.Column;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.util.Sorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class ConditionHelper {

    public List<Column> getRequiredColumns(Table table, List<String> argument) {
        return getRequiredColumns(table, argument, Sorter.Type.CREATION, null, Sorter.Order.ASC);
    }

    public List<Column> getRequiredColumns(Table table, List<String> argument, Sorter.Type sort, String sorter, Sorter.Order order) {
        List<Column> columns = new ArrayList<>();

        final String[] where = JFQL.getInstance().getFormatter().formatString(argument).replace(" or ", " OR ").split(" OR ");
        final List<List<Requirement>> conditions = new ArrayList<>();

        for (int j = 0; j < where.length; j++) {
            String[] args = where[j].replace(" and ", " AND ").replace("not ", "NOT ").replace(" not ", " NOT ").split(" AND ");

            List<Requirement> list1 = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                String[] strings = args[i].split(" = ");
                Requirement.Type type = Requirement.Type.POSITIVE;

                strings[0] = strings[0].replace("'", "");

                if (strings[0].startsWith("NOT ")) {
                    strings[0] = strings[0].replaceFirst("NOT ", "");
                    type = Requirement.Type.NEGATIVE;
                } else if (strings[0].startsWith(" NOT ")) {
                    strings[0] = strings[0].replaceFirst(" NOT ", "");
                    type = Requirement.Type.NEGATIVE;
                }

                strings[1] = strings[1].replace("'", "");
                System.out.println(strings[0]);

                if (!table.getStructure().contains(strings[0]) && !strings[0].equals("?") && !strings[0].equals("*")) {
                    return null;
                }

                list1.add(new Requirement(strings, type));
            }

            conditions.add(list1);
        }

        List<Column> cols;

        if (sort == Sorter.Type.CREATION || sorter == null) {
            cols = table.getColumns();
        } else {
            cols = table.getColumns(sort, order, sorter);
        }

        for (Column col : cols) {

            for (List<Requirement> requirements : conditions) {

                if (handleRequirement(requirements, table, col) == requirements.size()) {
                    columns.add(col);
                    break;
                }
            }

        }

        return columns;
    }

    public int handleRequirement(List<Requirement> requirements, Table table, Column column) {
        Map<Integer, Boolean> finishedMap = new HashMap<>();
        int finished = 0;

        for (int j = 0; j < requirements.size(); j++) {
            Requirement requirement = requirements.get(j);

            final String key = requirement.getStrings()[0];
            final String value = requirement.getStrings()[1];

            if (key.equals("*")) {
                boolean accept = true;

                for (String s : table.getStructure()) {
                    if (value.equalsIgnoreCase("null")) {
                        if (column.getContent().containsKey(s)) {
                            accept = false;
                            break;
                        }
                    } else {
                        if (column.getContent().containsKey(s)) {
                            if (value.startsWith("$") && value.endsWith("$")) {
                                if (!column.getContent(s).toString().contains(value.replace("$", ""))) {
                                    accept = false;
                                    break;
                                }
                            } else {
                                if (!column.getContent(s).toString().equals(value)) {
                                    accept = false;
                                    break;
                                }
                            }
                        } else {
                            accept = false;
                            break;
                        }
                    }
                }

                if (accept) {
                    if (!finishedMap.containsKey(j)) {
                        if (adaptToType(requirement.getType(), true))
                            finished++;
                        finishedMap.put(j, true);
                    }
                }

            } else if (key.equals("?")) {
                for (String s : table.getStructure()) {
                    if (value.equalsIgnoreCase("null")) {
                        if (!column.getContent().containsKey(s)) {
                            if (!finishedMap.containsKey(j)) {
                                if (adaptToType(requirement.getType(), true))
                                    finished++;
                                finishedMap.put(j, true);
                            }
                        }
                    } else {
                        if (column.getContent().containsKey(s)) {
                            if (value.startsWith("$") && value.endsWith("$")) {
                                if (column.getContent(s).toString().contains(value.replace("$", ""))) {
                                    if (!finishedMap.containsKey(j)) {
                                        if (adaptToType(requirement.getType(), true))
                                            finished++;
                                        finishedMap.put(j, true);
                                    }
                                    break;
                                }
                            } else {
                                if (column.getContent(s).toString().equals(value)) {
                                    if (!finishedMap.containsKey(j)) {
                                        if (adaptToType(requirement.getType(), true))
                                            finished++;
                                        finishedMap.put(j, true);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                if (value.equalsIgnoreCase("null")) {
                    if (!column.getContent().containsKey(key)) {
                        if (!finishedMap.containsKey(j)) {
                            if (adaptToType(requirement.getType(), true))
                                finished++;
                            finishedMap.put(j, true);
                        }
                    }
                } else {
                    if (column.getContent().containsKey(key)) {
                        if (value.startsWith("$") && value.endsWith("$")) {
                            if (column.getContent(key).toString().contains(value.replace("$", ""))) {
                                if (!finishedMap.containsKey(j)) {
                                    if (adaptToType(requirement.getType(), true))
                                        finished++;
                                    finishedMap.put(j, true);
                                }
                            }
                        } else {
                            if (column.getContent(key).toString().equals(value)) {
                                if (!finishedMap.containsKey(j)) {
                                    if (adaptToType(requirement.getType(), true))
                                        finished++;
                                    finishedMap.put(j, true);
                                }
                            }
                        }
                    }
                }
            }

            if (!finishedMap.containsKey(j)) {
                if (adaptToType(requirement.getType(), false))
                    finished++;
                finishedMap.put(j, true);
            }

        }

        return finished;
    }

    public boolean adaptToType(Requirement.Type type, boolean b) {
        switch (type) {
            case NEGATIVE:
                return !b;
            case POSITIVE:
            default:
                return b;
        }
    }

/*
    public boolean handleRequirement(Requirement requirement, Table table, Column column) {
        final String key = requirement.getStrings()[0];
        final String value = requirement.getStrings()[1];

        if (key.equals("*")) {
            boolean accept = true;

            for (String s : table.getStructure()) {
                if (value.equalsIgnoreCase("null")) {
                    if (column.getContent().containsKey(s)) {
                        accept = false;
                        break;
                    }
                } else {
                    if (column.getContent().containsKey(s)) {
                        if (value.startsWith("$") && value.endsWith("$")) {
                            if (!column.getContent(s).toString().contains(value.replace("$", ""))) {
                                accept = false;
                                break;
                            }
                        } else {
                            if (!column.getContent(s).toString().equals(value)) {
                                accept = false;
                                break;
                            }
                        }
                    } else {
                        accept = false;
                        break;
                    }
                }
            }

            if (accept) {
                adaptToType(requirement.getType(), true);
            }

        } else if (key.equals("?")) {
            for (String s : table.getStructure()) {
                if (value.equalsIgnoreCase("null")) {
                    if (!column.getContent().containsKey(s)) {
                        adaptToType(requirement.getType(), true);
                    }
                } else {
                    if (column.getContent().containsKey(s)) {
                        if (value.startsWith("$") && value.endsWith("$")) {
                            if (column.getContent(s).toString().contains(value.replace("$", ""))) {
                                adaptToType(requirement.getType(), true);
                                break;
                            }
                        } else {
                            if (column.getContent(s).toString().equals(value)) {
                                adaptToType(requirement.getType(), true);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            if (value.equalsIgnoreCase("null")) {
                if (!column.getContent().containsKey(key)) {
                    adaptToType(requirement.getType(), true);
                }
            } else {
                if (column.getContent().containsKey(key)) {
                    if (value.startsWith("$") && value.endsWith("$")) {
                        if (column.getContent(key).toString().contains(value.replace("$", ""))) {
                            adaptToType(requirement.getType(), true);
                        }
                    } else {
                        if (column.getContent(key).toString().equals(value)) {
                            adaptToType(requirement.getType(), true);
                        }
                    }
                }
            }
        }

        return adaptToType(requirement.getType(), false);
    }

    public boolean adaptToType(Requirement.Type type, boolean b) {
        switch (type) {
            case NEGATIVE:
                return b;
            case POSITIVE:
            default:
                return !b;
        }
    }
*/

}
