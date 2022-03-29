package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.TableException;
import de.byjoker.myjfql.lang.TableEntryComparator;
import de.byjoker.myjfql.util.Order;

import java.util.*;

public abstract class InternalTable implements Table {

    private final String id;
    private final String name;
    private final Collection<String> structure;
    private final String primary;

    protected InternalTable(String id, String name, List<String> structure, String primary) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public TableType getType() {
        return TableType.RELATIONAL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "InternalTable{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", structure=" + structure +
                ", primary='" + primary + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalTable that = (InternalTable) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
