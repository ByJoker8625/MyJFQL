package de.byjoker.myjfql.user

interface UserService {

    fun createUser(user: User)
    fun saveUser(user: User)
    fun deleteUser(id: String)
    fun load(table: UsersTable)
    fun getUser(id: String): User?
    fun getUserByName(name: String): User?
    fun getUserByIdentifier(identifier: String): User?
    fun getUsers(): List<User>

}
