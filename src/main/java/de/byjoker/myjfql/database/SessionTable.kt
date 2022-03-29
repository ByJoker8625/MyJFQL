package de.byjoker.myjfql.database

import de.byjoker.myjfql.exception.UserException
import de.byjoker.myjfql.network.session.*
import de.byjoker.myjfql.util.Json.stringify
import org.json.JSONArray
import java.util.stream.Collectors

class SessionTable(private val sessionService: SessionService) : InternalTable(
    "sessions",
    "sessions",
    listOf("token", "type", "user_id", "database_id", "addresses"),
    "token"
) {

    override fun addEntry(entry: TableEntry) {
        if (entry !is RelationalTableEntry) {
            return
        }

        if (!entry.contains("token") || !entry.contains("type") || !entry.contains("user_id") || !entry.contains("addresses")) {
            throw UserException("Cannot parse table-entry into session!")
        }

        val addresses =
            JSONArray(entry.selectStringify("addresses")).toList().stream().map { obj: Any -> obj.toString() }
                .collect(Collectors.toList())

        when (SessionType.valueOf(entry.selectStringify("type"))) {
            SessionType.STATIC -> {
                sessionService.saveSession(
                    StaticSession(
                        entry.selectStringify("token"),
                        entry.selectStringify("user_id"),
                        if (entry.contains("database_id")) entry.selectStringify("database_id") else null,
                        addresses
                    )
                )
            }
            SessionType.DYNAMIC -> {
                sessionService.saveSession(
                    DynamicSession(
                        entry.selectStringify("token"),
                        entry.selectStringify("user_id"),
                        if (entry.contains("database_id")) entry.selectStringify("database_id") else null,
                        addresses
                    )
                )
            }
            SessionType.INTERNAL -> {
                throw UserException("Cannot parse table-entry into session!")
            }
        }
    }

    override fun removeEntry(identifier: String) {
        sessionService.closeSession(identifier)
    }

    override fun getEntries(): Collection<TableEntry> {
        return sessionService.sessions.stream()
            .map { session: Session -> session.asTableEntry().append("addresses", stringify(session.addresses)) }
            .collect(Collectors.toList())
    }

    override fun clear() {
        entries.forEach { entry ->
            sessionService.closeSession(
                entry.selectStringify(
                    primary
                )
            )
        }
    }

}
