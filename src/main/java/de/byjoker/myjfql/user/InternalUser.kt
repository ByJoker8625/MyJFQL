package de.byjoker.myjfql.user

import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.util.IDGenerator
import java.time.LocalDate

class InternalUser(
    override var id: String = IDGenerator.generateDigits(8),
    override var name: String,
    override var createdAt: LocalDate = LocalDate.now()
) : User {

    override var type: UserType = UserType.INTERNAL
    override var locked: Boolean = true
    override var password: String = "internal"

    override fun validPassword(password: String): Boolean {
        return false
    }

    override fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean {
        return true
    }

    override fun grantAccess(action: DatabasePermissionLevel, databaseId: String) {
    }

    override fun revokeAccess(databaseId: String) {
    }


    override fun toString(): String {
        return "InternalUser(id='$id', name='$name', createdAt=$createdAt, type=$type, locked=$locked, password='$password')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InternalUser

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
