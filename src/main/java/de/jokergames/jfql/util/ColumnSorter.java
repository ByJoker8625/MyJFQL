package de.jokergames.jfql.util;

import de.jokergames.jfql.database.Column;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Janick
 */

public class ColumnSorter {

    public List<Column> sort(String key, List<Column> columns) {
        final List<Column> list = new ArrayList<>();

        List<Column> numbers = new ArrayList<>();
        List<Column> letters = new ArrayList<>();
        List<Column> unknowns = new ArrayList<>();

        for (Column column : sort(columns)) {

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

        numbers.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getContent(key).toString())));
        letters.sort((o1, o2) -> String.valueOf(o1.getContent(key)).compareTo(o2.getContent(key).toString()));

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

}
