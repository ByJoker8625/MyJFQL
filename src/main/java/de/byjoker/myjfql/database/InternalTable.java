package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.DatabaseException;
import de.byjoker.myjfql.lang.Requirement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class InternalTable implements Table {

    private final String name;
    private final String primary;
    private final List<String> structure;

    public InternalTable(String name, String primary, List<String> structure) {
        this.name = name;
        this.primary = primary;
        this.structure = structure;
    }

    @NotNull
    @Override
    public String getId() {
        return name;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        throw new DatabaseException("Internal tables are immutable!");
    }

    @NotNull
    @Override
    public List<String> getStructure() {
        return structure;
    }

    @NotNull
    @Override
    public String getPrimary() {
        return primary;
    }

    @NotNull
    @Override
    public String getPartitioner() {
        return primary;
    }

    @NotNull
    @Override
    public TableType getType() {
        return TableType.RELATIONAL;
    }

    @NotNull
    @Override
    public List<Entry> findEntries(@NotNull List<? extends List<Requirement>> conditions, int limit) {
        List<Entry> match = new ArrayList<>();
        int count = 0;

        for (Entry entry : getEntries()) {
            if (count == limit) {
                break;
            }

            if (entry.matches(conditions)) {
                match.add(entry);
                count++;
            }
        }

        return match;
    }

    @NotNull
    @Override
    public String getDatabaseId() {
        return "internal";
    }
}
