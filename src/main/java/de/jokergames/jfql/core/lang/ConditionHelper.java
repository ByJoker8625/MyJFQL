package de.jokergames.jfql.core.lang;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.Column;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.util.Sorter;

import java.util.ArrayList;
import java.util.List;

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
        final List<List<String[]>> conditions = new ArrayList<>();

        for (int j = 0; j < where.length; j++) {
            String[] args = where[j].replace(" and ", " AND ").split(" AND ");

            List<String[]> list1 = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                String[] strings = args[i].split(" = ");

                strings[0] = strings[0].replace("'", "");
                strings[1] = strings[1].replace("'", "");

                if (!table.getStructure().contains(strings[0]) && !strings[0].equals("?") && !strings[0].equals("*")) {
                    return null;
                }

                list1.add(strings);
            }

            conditions.add(list1);
        }

        List<Column> cols = new ArrayList<>();

        if (sort == Sorter.Type.CREATION || sorter == null) {
            cols = table.getColumns();
        } else {
            cols = table.getColumns(sort, order, sorter);
        }

        for (Column col : cols) {

            for (List<String[]> requirements : conditions) {
                int finished = 0;

                for (String[] strings : requirements) {
                    final String key = strings[0];
                    final String value = strings[1];

                    if (key.equals("*")) {
                        boolean accept = true;

                        for (String s : table.getStructure()) {
                            if (value.equalsIgnoreCase("null")) {
                                if (col.getContent().containsKey(s)) {
                                    accept = false;
                                    break;
                                }
                            } else {
                                if (col.getContent().containsKey(s)) {
                                    if (value.startsWith("$") && value.endsWith("$")) {
                                        if (!col.getContent(s).toString().contains(value.replace("$", ""))) {
                                            accept = false;
                                            break;
                                        }
                                    } else {
                                        if (!col.getContent(s).toString().equals(value)) {
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
                            finished++;
                        }

                    } else if (key.equals("?")) {
                        for (String s : table.getStructure()) {
                            if (value.equalsIgnoreCase("null")) {
                                if (!col.getContent().containsKey(s)) {
                                    finished++;
                                    break;
                                }
                            } else {
                                if (col.getContent().containsKey(s)) {
                                    if (value.startsWith("$") && value.endsWith("$")) {
                                        if (col.getContent(s).toString().contains(value.replace("$", ""))) {
                                            finished++;
                                            break;
                                        }
                                    } else {
                                        if (col.getContent(s).toString().equals(value)) {
                                            finished++;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        /*
                        for (Object val : col.getContent().values()) {
                            if (val.toString().equals(value)) {
                                finished++;
                                break;
                            } else if (val.toString().equalsIgnoreCase("null") && value.equalsIgnoreCase("null")) {
                                finished++;
                                break;
                            }
                        }*/
                    } else {
                        if (value.equalsIgnoreCase("null")) {
                            if (!col.getContent().containsKey(key)) {
                                finished++;
                                break;
                            }
                        } else {
                            if (col.getContent().containsKey(key)) {
                                if (value.startsWith("$") && value.endsWith("$")) {
                                    if (col.getContent(key).toString().contains(value.replace("$", ""))) {
                                        finished++;
                                        break;
                                    }
                                } else {
                                    if (col.getContent(key).toString().equals(value)) {
                                        finished++;
                                        break;
                                    }
                                }
                            }
                        }

                        /*if (col.getContent().containsKey(key)) {
                        if (col.getContent(key).toString().equals(value)) {
                            finished++;
                        } else if (col.getContent(key) == null && value.equalsIgnoreCase("null")) {
                            finished++;
                        }
                    } else if (value.equalsIgnoreCase("null")) {
                        finished++;
                    }*/
                    }

                }

                if (finished == requirements.size()) {
                    columns.add(col);
                    break;
                }
            }

        }

        return columns;
    }

}
