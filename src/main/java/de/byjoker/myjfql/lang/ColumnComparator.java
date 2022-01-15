package de.byjoker.myjfql.lang;

import de.byjoker.myjfql.database.Column;

import java.util.Comparator;

public class ColumnComparator implements Comparator<Column> {

    private final StringComparator comparator = new StringComparator();

    private final String key;

    public ColumnComparator(String key) {
        this.key = key;
    }

    @Override
    public int compare(Column o1, Column o2) {
        if (key == null)
            return Long.compare(o1.getCreatedAt(), o2.getCreatedAt());

        return !o1.containsOrNotNullItem(key) ? !o2.containsOrNotNullItem(key) ? 0 : 1 : !o2.containsOrNotNullItem(key) ? -1 : comparator.compare(o1.getStringifyItem(key), o1.getStringifyItem(key));
    }

    public String getKey() {
        return key;
    }

}
