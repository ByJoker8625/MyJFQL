package de.byjoker.myjfql.user

import de.byjoker.myjfql.exception.UserException

class RelationalUserService(private var table: UsersTable = UsersTable()) : UserService {

    override fun createUser(user: User) {
        if (getUser(user.id) != null || getUserByName(user.name) != null) {
            throw UserException("User already exist!")
        }

        saveUser(user)
    }

    override fun saveUser(user: User) {
        table.users[user.id] = user
    }

    override fun deleteUser(id: String) {
        table.users.remove(id)
    }

    override fun load(table: UsersTable) {
        this.table = table
    }

    override fun getUserByName(name: String): User? {
        return table.users.values.firstOrNull { it.name == name }
    }

    override fun getUserByIdentifier(identifier: String): User? {
        if (identifier.startsWith("#")) {
            return getUser(identifier.replaceFirst("#", ""))
        }

        return getUserByName(identifier)
    }

    override fun getUser(id: String): User? {
        return table.users[id]
    }

    override fun getUsers(): List<User> {
        return table.users.values.toList()
    }

}
