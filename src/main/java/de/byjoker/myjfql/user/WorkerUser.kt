package de.byjoker.myjfql.user

import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.util.IDGenerator

class WorkerUser(
    override var id: String = IDGenerator.generateDigits(8),
    override var name: String,
    override var locked: Boolean = false,
    var permissions: MutableMap<String, DatabasePermissionLevel>,
    override var password: String,
) : User {

    override var type: UserType = UserType.WORKER

    override fun validPassword(password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean {
        return permissions.containsKey(databaseId) && permissions[databaseId] == action
    }

    override fun grantAccess(action: DatabasePermissionLevel, databaseId: String) {
        permissions[databaseId] = action
    }

    override fun revokeAccess(databaseId: String) {
        permissions.remove(databaseId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkerUser

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "WorkerUser(id='$id', name='$name', locked=$locked, permissions=$permissions, password='$password', type=$type)"
    }

}
