package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.TableException;
import de.byjoker.myjfql.lang.TableEntryComparator;
import de.byjoker.myjfql.util.Order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class InternalTable implements Table {

    private final String name;
    private final Collection<String> structure;
    private final String primary;

    protected InternalTable(String name, List<String> structure, String primary) {
        this.name = name;
        this.structure = structure;
        this.primary = primary;
    }

    @Override
    public Table reformat(TableType type) {
        throw new TableException("System-internal table structure cannot be changed manually!");
    }

    @Override
    public String getPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(String primary) {
        throw new TableException("System-internal table structure cannot be changed manually!");
    }

    @Override
    public TableEntry getEntry(String identifier) {
        return getEntries().stream().filter(entry -> entry.selectStringify(primary).equals(identifier)).findFirst().orElse(null);
    }

    @Override
    public Collection<TableEntry> getEntries(TableEntryComparator comparator, Order order) {
        final List<TableEntry> entries = new ArrayList<>(getEntries());
        entries.sort(comparator);
        if (order == Order.DESC) Collections.reverse(entries);
        return entries;
    }

    @Override
    public Collection<String> getStructure() {
        return structure;
    }

    @Override
    public void setStructure(Collection<String> structure) {
        throw new TableException("System-internal table structure cannot be changed manually!");
    }

    @Override
    public TableType getType() {
        return TableType.RELATIONAL;
    }

    @Override
    public String getName() {
        return name;
    }
}
