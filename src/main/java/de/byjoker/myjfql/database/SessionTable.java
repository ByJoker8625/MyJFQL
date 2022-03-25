package de.byjoker.myjfql.database;

import de.byjoker.myjfql.exception.UserException;
import de.byjoker.myjfql.network.session.DynamicSession;
import de.byjoker.myjfql.network.session.SessionService;
import de.byjoker.myjfql.network.session.SessionType;
import de.byjoker.myjfql.network.session.StaticSession;
import de.byjoker.myjfql.util.Json;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SessionTable extends InternalTable {

    private final SessionService sessionService;

    public SessionTable(SessionService sessionService) {
        super("sessions", Arrays.asList("token", "type", "user_id", "database_id", "addresses"), "token");
        this.sessionService = sessionService;
    }

    @Override
    public void addEntry(TableEntry entry) {
        if (!(entry instanceof RelationalTableEntry)) {
            return;
        }

        if (!entry.contains("token") || !entry.contains("type") || !entry.contains("user_id") || !entry.contains("addresses")) {
            throw new UserException("Cannot parse table-entry into session!");
        }

        final List<String> addresses = new JSONArray(entry.selectStringify("addresses")).toList().stream().map(Object::toString).collect(Collectors.toList());

        switch (SessionType.valueOf(entry.selectStringify("type"))) {
            case STATIC: {
                sessionService.saveSession(new StaticSession(entry.selectStringify("token"), entry.selectStringify("user_id"), entry.contains("database_id") ? entry.selectStringify("database_id") : null, addresses));
                break;
            }
            case DYNAMIC: {
                sessionService.saveSession(new DynamicSession(entry.selectStringify("token"), entry.selectStringify("user_id"), entry.contains("database_id") ? entry.selectStringify("database_id") : null, addresses));
                break;
            }
            case INTERNAL: {
                throw new UserException("Cannot parse table-entry into session!");
            }
        }
    }

    @Override
    public void removeEntry(String identifier) {
        sessionService.closeSession(identifier);
    }

    @Override
    public Collection<TableEntry> getEntries() {
        return sessionService.getSessions().stream().map(session -> session.asTableEntry().append("addresses", Json.stringify(session.getAddresses()))).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        getEntries().forEach(entry -> sessionService.closeSession(entry.selectStringify(getPrimary())));
    }

    public SessionService getSessionService() {
        return sessionService;
    }
}
