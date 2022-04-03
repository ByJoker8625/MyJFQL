package de.byjoker.myjfql.user;

import de.byjoker.myjfql.database.Entry;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.database.TableType;
import de.byjoker.myjfql.lang.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UserTable implements Table {

    @NotNull
    @Override
    public String getId() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(@NotNull String name) {

    }

    @NotNull
    @Override
    public List<String> getStructure() {
        return null;
    }

    @NotNull
    @Override
    public String getPrimary() {
        return null;
    }

    @NotNull
    @Override
    public String getPartitioner() {
        return null;
    }

    @NotNull
    @Override
    public TableType getType() {
        return null;
    }

    @Override
    public void pushEntry(@NotNull Entry entry) {

    }

    @Nullable
    @Override
    public Entry getEntry(@NotNull String entryId) {
        return null;
    }

    @Override
    public void removeEntry(@NotNull String entryId) {

    }

    @NotNull
    @Override
    public List<Entry> findEntries(@NotNull List<? extends List<Requirement>> conditions, int limit) {
        return null;
    }

    @NotNull
    @Override
    public List<Entry> getEntries() {
        return null;
    }

    @Override
    public void clear() {

    }

    @NotNull
    @Override
    public String getDatabaseId() {
        return "internal";
    }

}
