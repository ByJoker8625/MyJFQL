package de.byjoker.myjfql.util;

import de.byjoker.myjfql.database.Column;

import java.util.*;

public class Sorter {


    public static Collection<Column> sortColumns(String key, Collection<Column> columns, Order order) {
        List<String> strings = new ArrayList<>();

        columns.forEach(col -> {
            if (!col.containsContentKey(key)) {
                strings.add("null");
            } else {
                strings.add(String.valueOf(col.getContent(key)));
            }
        });

        List<String> sorted = sortList(strings, order);

        List<Column> nullable = new ArrayList<>();
        List<Column> list = new ArrayList<>();

        sorted.forEach(string -> {
            if (string.equals("null")) {
                columns.stream().filter(col -> (!col.containsContentKey(key) || (col.containsContentKey(key) && col.getContent(key).equals("null"))) && !nullable.contains(col)).findFirst().ifPresent(nullable::add);
            } else {
                columns.stream().filter(col -> col.containsContentKey(key) && col.getContent(key).equals(string) && !list.contains(col)).findFirst().ifPresent(list::add);
            }
        });

        if (nullable.size() != 0)
            list.addAll(nullable);

        return list;
    }

    public static List<String> sortList(List<String> strings, Order order) {
        List<String> characters = new ArrayList<>();
        List<String> nullable = new ArrayList<>();
        List<String> digits = new ArrayList<>();

        strings.forEach(string -> {
            if (string.equals("null")) {
                nullable.add(string);
            } else if (isNumber(string)) {
                digits.add(string);
            } else {
                characters.add(string);
            }
        });

        digits.sort(Comparator.comparingLong(Long::parseLong));
        characters.sort(Comparator.naturalOrder());

        List<String> list = new ArrayList<>();
        list.addAll(digits);
        list.addAll(characters);
        list.addAll(nullable);

        if (order == Order.DESC) {
            Collections.reverse(list);
        }

        return list;
    }

    public static Collection<Column> sortColumns(Collection<Column> columns) {
        List<Column> list = new ArrayList<>(columns);
        list.sort(Comparator.comparingLong(Column::getCreation));
        return list;
    }

    public static boolean isNumber(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
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
