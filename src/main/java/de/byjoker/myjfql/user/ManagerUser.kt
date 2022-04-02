package de.byjoker.myjfql.user

import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.util.IDGenerator
import java.time.LocalDate

class ManagerUser(
    override var id: String = IDGenerator.generateDigits(8),
    override var name: String,
    override var password: String,
    override var createdAt: LocalDate = LocalDate.now()
) : User {

    override var locked: Boolean = false
    override var type: UserType = UserType.MANAGER

    override fun validPassword(password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean {
        return true
    }

    override fun grantAccess(action: DatabasePermissionLevel, databaseId: String) {
    }

    override fun revokeAccess(databaseId: String) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ManagerUser

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ManagerUser(id='$id', name='$name', password='$password', createdAt=$createdAt, locked=$locked, type=$type)"
    }

}
