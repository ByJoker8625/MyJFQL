package de.byjoker.myjfql.user


import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.database.Entry
import de.byjoker.myjfql.database.InternalTable
import de.byjoker.myjfql.database.RelationalEntry
import de.byjoker.myjfql.exception.UserException
import de.byjoker.myjfql.util.Json
import de.byjoker.myjfql.util.Json.stringify
import java.util.stream.Collectors


class UsersTable :
    InternalTable("users", listOf("id", "name", "password", "permissions", "preferred_database_id")) {

    val users: MutableMap<String, User> = mutableMapOf("console" to InternalUser("console", "console"))
    private val factory: JsonNodeFactory = JsonNodeFactory.instance

    override fun pushEntry(entry: Entry) {
        val user = convert(entry) ?: throw UserException("Entry isn't valid user!")
        users[user.id] = user
    }

    override fun getEntry(entryId: String): Entry? {
        return convert(users[entryId])
    }

    override fun removeEntry(entryId: String) {
        users.remove(entryId)
    }

    override fun getEntries(): List<Entry> {
        return users.values.stream().map { user -> this.convert(user)!! }
            .collect(Collectors.toList())
    }

    override fun clear() {
        users.clear()
    }

    private fun convert(user: User?): Entry? {
        if (user == null) return null

        return RelationalEntry()
            .insert("id", factory.textNode(user.id))
            .insert("name", factory.textNode(user.name))
            .insert("password", factory.textNode(user.password))
            .insert("type", factory.textNode(user.type.name))
            .insert("permissions", factory.textNode(stringify(user.permissions)))
            .insert("preferred_database_id", factory.textNode(user.preferredDatabaseId))
    }

    private fun convert(entry: Entry): User? {
        return try {
            val user = when (entry.selectStringify("type")) {
                "INTERNAL" -> InternalUser(entry.selectStringify("id")!!, entry.selectStringify("name")!!)
                "MANAGER" -> ManagerUser(
                    entry.selectStringify("id")!!,
                    entry.selectStringify("name")!!,
                    entry.selectStringify("password")!!
                )
                "WORKER" -> {
                    val permissions = mutableMapOf<String, DatabasePermissionLevel>()
                    val permissionsNode = Json.parse(entry.selectStringify("permissions")!!)

                    for (key in permissionsNode.fieldNames()) {
                        permissions[key] = DatabasePermissionLevel.valueOf(permissionsNode[key].asText())
                    }

                    WorkerUser(
                        entry.selectStringify("id")!!,
                        entry.selectStringify("name")!!,
                        entry.selectStringify("locked")!!.toBoolean(),
                        permissions,
                        if (entry.contains("preferredDatabaseId")) entry.selectStringify("preferredDatabaseId")!! else null,
                        entry.selectStringify("password")!!
                    )
                }
                else -> throw UserException("Unknown user type!")
            }

            user
        } catch (ex: Exception) {
            null
        }
    }
}
