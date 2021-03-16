package org.jokergames.myjfql.util;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Table;

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

        final String[] where = MyJFQL.getInstance().getFormatter().formatString(argument)
                .replace(" or ", " OR ")
                .split(" OR ");
        final List<List<Requirement>> conditions = new ArrayList<>();

        for (String s : where) {
            String[] args = s
                    .replace(" and ", " AND ")
                    .replace("not smaller ", "BIGGER ")
                    .replace(" not smaller ", " BIGGER ")
                    .replace("not bigger ", "SMALLER ")
                    .replace(" not bigger ", " SMALLER ")
                    .replace("smaller ", "SMALLER ")
                    .replace(" smaller ", " SMALLER ")
                    .replace("bigger ", "BIGGER ")
                    .replace(" bigger ", " BIGGER ")
                    .replace("not ", "NOT ")
                    .replace(" not ", " NOT ")
                    .replace("NOT BIGGER ", "SMALLER ")
                    .replace(" NOT BIGGER ", " SMALLER ")
                    .replace("NOT SMALLER ", "BIGGER ")
                    .replace(" NOT SMALLER ", " BIGGER ")
                    .split(" AND ");

            List<Requirement> list1 = new ArrayList<>();

            for (String arg : args) {
                String[] strings = arg.split(" = ");
                Requirement.Type type = Requirement.Type.POSITIVE;

                strings[0] = strings[0].replace("'", "");

                if (strings[0].startsWith(" NOT ")) {
                    strings[0] = strings[0].replaceFirst(" NOT ", "");
                    type = Requirement.Type.NEGATIVE;
                }

                if (strings[0].startsWith("NOT ")) {
                    strings[0] = strings[0].replaceFirst("NOT ", "");
                    type = Requirement.Type.NEGATIVE;
                }

                if (strings[0].startsWith(" BIGGER ")) {
                    strings[0] = strings[0].replaceFirst(" BIGGER ", "");
                    type = Requirement.Type.BIGGER;
                }

                if (strings[0].startsWith("BIGGER ")) {
                    strings[0] = strings[0].replaceFirst("BIGGER ", "");
                    type = Requirement.Type.BIGGER;
                }

                if (strings[0].startsWith(" SMALLER ")) {
                    strings[0] = strings[0].replaceFirst(" SMALLER ", "");
                    type = Requirement.Type.SMALLER;
                }

                if (strings[0].startsWith("SMALLER ")) {
                    strings[0] = strings[0].replaceFirst("SMALLER ", "");
                    type = Requirement.Type.SMALLER;
                }

                strings[1] = strings[1].replace("'", "");

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
            if (conditions.stream().anyMatch(requirements -> handleRequirement(requirements, table, col) == requirements.size())) {
                columns.add(col);
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

            if (requirement.getType() == Requirement.Type.BIGGER || requirement.getType() == Requirement.Type.SMALLER) {

                if (requirement.getType() == Requirement.Type.BIGGER) {
                    if (key.equals("*")) {
                        boolean accept = true;

                        for (String s : table.getStructure()) {
                            if (column.getContent().containsKey(s)) {
                                int compare = isBigger(value, column.getContent(s).toString());

                                if (compare == 1 || compare == -1) {
                                    accept = false;
                                    break;
                                }
                            } else {
                                accept = false;
                                break;
                            }
                        }

                        if (accept) {
                            if (!finishedMap.containsKey(j)) {
                                finishedMap.put(j, true);
                                finished++;
                            }
                        }

                    } else if (key.equals("?")) {
                        for (String s : table.getStructure()) {
                            if (column.getContent().containsKey(s)) {
                                int compare = isBigger(value, column.getContent(s).toString());

                                if (compare == 0) {
                                    if (!finishedMap.containsKey(j)) {
                                        finishedMap.put(j, true);
                                        finished++;
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (column.getContent().containsKey(key)) {
                            int compare = isBigger(value, column.getContent(key).toString());

                            if (compare == 0) {
                                if (!finishedMap.containsKey(j)) {
                                    finishedMap.put(j, true);
                                    finished++;
                                }
                            }
                        }
                    }
                } else {
                    if (key.equals("*")) {
                        boolean accept = true;

                        for (String s : table.getStructure()) {
                            if (column.getContent().containsKey(s)) {
                                int compare = isSmaller(value, column.getContent(s).toString());

                                if (compare == 1 || compare == -1) {
                                    accept = false;
                                    break;
                                }
                            } else {
                                accept = false;
                                break;
                            }
                        }

                        if (accept) {
                            if (!finishedMap.containsKey(j)) {
                                finishedMap.put(j, true);
                                finished++;
                            }
                        }

                    } else if (key.equals("?")) {
                        for (String s : table.getStructure()) {
                            if (column.getContent().containsKey(s)) {
                                int compare = isSmaller(value, column.getContent(s).toString());

                                if (compare == 0) {
                                    if (!finishedMap.containsKey(j)) {
                                        finishedMap.put(j, true);
                                        finished++;
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (column.getContent().containsKey(key)) {
                            int compare = isSmaller(value, column.getContent(key).toString());

                            if (compare == 0) {
                                if (!finishedMap.containsKey(j)) {
                                    finishedMap.put(j, true);
                                    finished++;
                                }
                            }
                        }
                    }
                }

            } else {

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

    public int isBigger(String value, String than) {
        if (instanceOfNumber(value) && instanceOfNumber(than)) {
            double v = Double.parseDouble(value);
            double t = Double.parseDouble(than);

            if (v < t) {
                return 0;
            } else {
                return 1;
            }
        }

        return -1;
    }


    public int isSmaller(String value, String than) {
        if (instanceOfNumber(value) && instanceOfNumber(than)) {
            double v = Double.parseDouble(value);
            double t = Double.parseDouble(than);

            if (v < t) {
                return 1;
            } else {
                return 0;
            }
        }

        return -1;
    }

    public boolean instanceOfNumber(String s) {
        try {
            Double.parseDouble(s);
        } catch (Exception exception) {
            return false;
        }

        return true;
    }

}
