package de.byjoker.myjfql.database;

import de.byjoker.myjfql.lang.TableEntryComparator;
import de.byjoker.myjfql.util.Order;

import java.util.Collection;

public interface Table {

    void addEntry(TableEntry tableEntry);

    void removeEntry(String identifier);

    TableEntry getEntry(String identifier);

    Collection<TableEntry> getEntries();

    Collection<TableEntry> getEntries(TableEntryComparator comparator, Order order);

    Collection<String> getStructure();

    void setStructure(Collection<String> structure);

    String getPrimary();

    void setPrimary(String primary);

    Table reformat(TableType type);

    TableType getType();

    String getName();

    void clear();
}
