package org.jokergames.myjfql.database.util;

import org.jokergames.myjfql.database.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sorter {


    public static List<Column> sortColumns(final String key, final List<Column> columns, final Order order) {
        final List<String> strings = new ArrayList<>();

        columns.forEach(col -> {
            if (!col.containsContentKey(key)) {
                strings.add("null");
            } else {
                strings.add(String.valueOf(col.getContent(key)));
            }
        });

        final List<String> sorted = sortList(strings, order);

        final List<Column> nullable = new ArrayList<>();
        final List<Column> list = new ArrayList<>();

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

    public static List<String> sortList(final List<String> strings, final Order order) {
        final List<String> characters = new ArrayList<>();
        final List<String> nullable = new ArrayList<>();
        final List<String> digits = new ArrayList<>();

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

        final List<String> list = new ArrayList<>();
        list.addAll(digits);
        list.addAll(characters);
        list.addAll(nullable);

        if (order == Order.DESC) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<Column> sortColumns(final List<Column> columns) {
        List<Column> list = new ArrayList<>(columns);
        list.sort(Comparator.comparingLong(Column::getCreation));
        return list;
    }

    public static boolean isNumber(final String s) {
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
