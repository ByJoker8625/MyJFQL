package de.byjoker.myjfql.lang;

import de.byjoker.myjfql.database.TableEntry;

import java.util.Comparator;

public class TableEntryComparator implements Comparator<TableEntry> {

    private final StringComparator comparator = new StringComparator();

    private final String key;

    public TableEntryComparator(String key) {
        this.key = key;
    }

    @Override
    public int compare(TableEntry o1, TableEntry o2) {
        if (key == null)
            return Long.compare(o1.getCreatedAt(), o2.getCreatedAt());

        if (!o1.containsOrNotNullItem(key)) {
            if (o2.containsOrNotNullItem(key)) {
                return 1;
            }

            return 0;
        }

        if (!o2.containsOrNotNullItem(key)) {
            return -1;
        }

        return comparator.compare(o1.selectStringify(key), o2.selectStringify(key));
    }

    public String getKey() {
        return key;
    }

}
