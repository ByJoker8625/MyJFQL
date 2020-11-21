package de.jokergames.jfql.util;

import de.jokergames.jfql.database.Column;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Janick
 */

public class Sorter {

    public List<Column> sort(String key, List<Column> columns) {
        final List<Column> list = new ArrayList<>();

        List<Column> numbers = new ArrayList<>();
        List<Column> letters = new ArrayList<>();
        List<Column> unknowns = new ArrayList<>();

        for (Column column : sort(columns)) {

            System.out.println(key);
            System.out.println(column);

            if (column.getContent(key) == null) {
                unknowns.add(column);
            } else {
                System.out.println(1);

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
                    if (getSlot(item.charAt(0)) != -1) {
                        letters.add(column);
                    } else {
                        unknowns.add(column);
                    }
                }
            }
        }

        numbers.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getContent(key).toString())));
        letters.sort(Comparator.comparingInt(o -> getSlot(o.toString().charAt(0))));

        list.addAll(numbers);
        list.addAll(letters);
        list.addAll(unknowns);
        return list;
    }

    public List<Column> sort(List<Column> columns) {
        List<Column> list = new ArrayList<>(columns);
        list.sort(Comparator.comparingLong(Column::getCreation));
        return list;
    }

    public boolean isUpperCase(char c) {
        String s = new String(new char[]{c});

        String upperCase = s.toUpperCase();
        String lowCase = s.toLowerCase();

        if (s.equals(upperCase)) {
            return true;
        } else if (s.equals(lowCase)) {
            return false;
        }

        return false;
    }

    public int getSlot(char c) {
        String lowCase = "abcdefghijklmnopqrstuvwxyz";
        String upperCase = lowCase.toUpperCase();

        if (isUpperCase(c)) {
            char[] chars = upperCase.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == c) {
                    return i;
                }
            }
        } else {
            char[] chars = lowCase.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == c) {
                    return i;
                }
            }
        }

        return -1;
    }

}
