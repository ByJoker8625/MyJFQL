package org.jokergames.myjfql.util;

import org.jokergames.myjfql.database.Column;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Janick
 */

public class Sorter {

    public static List<Column> sortColumns(final String key, final List<Column> columns, final Order order) {
        final List<Column> list = new ArrayList<>(columns);

        list.sort((o1, o2) -> {
            String s1 = null;
            String s2 = null;

            if (o1.getContent().containsKey(key))
                s1 = o1.getContent(key).toString();

            if (o2.getContent().containsKey(key))
                s2 = o2.getContent(key).toString();

            final int compare = String.valueOf(s1).compareTo(String.valueOf(s2));

            if (order == Order.ASC) {
                return compare;
            } else {
                return -compare;
            }
        });

        /*
        List<Column> numbers = new ArrayList<>();
        List<Column> letters = new ArrayList<>();
        List<Column> unknowns = new ArrayList<>();

        for (Column column : sortColumns(columns)) {

            if (column.getContent(key) == null) {
                unknowns.add(column);
            } else {
                String item = column.getContent(key).toString();

                boolean number = true;

                try {
                    Integer.parseInt(item);
                } catch (Exception ex) {
                    number = false;
                }

                if (number) {
                    numbers.add(column);
                } else {
                    letters.add(column);
                }
            }
        }

        if (order == Order.ASC) {
            numbers.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getContent(key).toString())));
            letters.sort((o1, o2) -> String.valueOf(o1.getContent(key)).compareTo(o2.getContent(key).toString()));
        } else if (order == Order.DESC) {
            numbers.sort((o1, o2) -> -Integer.compare(Integer.parseInt(o1.getContent(key).toString()), Integer.parseInt(o2.getContent(key).toString())));
            letters.sort((o1, o2) -> -String.valueOf(o1.getContent(key)).compareTo(o2.getContent(key).toString()));
        }

        list.addAll(numbers);
        list.addAll(letters);
        list.addAll(unknowns);*/
        return list;
    }

    public static List<String> sortList(List<String> strings, Order order) {
        final List<String> list = new ArrayList<>(strings);

        list.sort((o1, o2) -> {
            final int compare = String.valueOf(o1).compareTo(String.valueOf(o2));

            if (order == Order.ASC) {
                return compare;
            } else {
                return -compare;
            }
        });

        /*
        List<String> numbers = new ArrayList<>();
        List<String> letters = new ArrayList<>();
        List<String> unknowns = new ArrayList<>();

        for (String s : strings) {

            if (s == null) {
                unknowns.add("null");
            } else {
                boolean number = true;

                try {
                    Integer.parseInt(s);
                } catch (Exception ex) {
                    number = false;
                }

                if (number) {
                    numbers.add(s);
                } else {
                    letters.add(s);
                }
            }
        }

        if (order == Order.ASC) {
            numbers.sort(Comparator.comparingInt(Integer::parseInt));
            letters.sort((o1, o2) -> String.valueOf(o1).compareTo(o2));
        } else if (order == Order.DESC) {
            numbers.sort((o1, o2) -> -Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2)));
            letters.sort((o1, o2) -> -String.valueOf(o1).compareTo(o2));
        }

        list.addAll(numbers);
        list.addAll(letters);
        list.addAll(unknowns);
*/
        return list;
    }

    public static List<Column> sortColumns(final List<Column> columns) {
        List<Column> list = new ArrayList<>(columns);
        list.sort(Comparator.comparingLong(Column::getCreation));
        return list;
    }

    public enum Order {
        DESC,
        ASC
    }

    public enum Type {
        CREATION,
        CUSTOM
    }

}
