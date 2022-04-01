package de.byjoker.myjfql.user

interface UserService {

    fun createUser(user: User)
    fun saveUser(user: User)
    fun deleteUser(id: String)
    fun getUserByName(name: String): User?
    fun getUser(id: String): User?
    fun getUsers(): List<User>

}
