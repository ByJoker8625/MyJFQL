package de.byjoker.myjfql.user

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.exception.FileException
import de.byjoker.myjfql.exception.UserException
import de.byjoker.myjfql.util.FileFactory
import de.byjoker.myjfql.util.IDGenerator.generateDigits
import de.byjoker.myjfql.util.Json
import java.io.File

class UserServiceImpl : UserService {
    private val factory: FileFactory
    private val users: MutableList<User>

    init {
        users = ArrayList()
        factory = FileFactory()
    }

    override fun createUser(user: User) {
        if (existsUserByName(user.name)) {
            throw UserException("User already exists!")
        }

        if (existsUser(user.id)) {
            user.id = generateDigits(8)
            createUser(user)
            return
        }

        user.password = MyJFQL.getInstance().encryptor.encrypt(user.password)
        saveUser(user)
    }

    override fun saveUser(user: User) {
        for (i in users.indices) if (users[i].name == user.name) {
            users[i] = user
            return
        }

        users.add(user)
    }

    override fun getUserByIdentifier(identifier: String): User? {
        return if (identifier.startsWith("#")) getUser(identifier.replaceFirst("#".toRegex(), "")) else getUserByName(
            identifier
        )
    }

    override fun getUserByName(name: String): User? {
        return users.stream().filter { user -> user.name == name }.findFirst().orElse(null)
    }

    override fun getUser(id: String): User? {
        return users.stream().filter { user -> user.id == id }.findFirst().orElse(null)
    }

    override fun existsUserByIdentifier(identifier: String): Boolean {
        return if (identifier.startsWith("#")) existsUser(
            identifier.replaceFirst(
                "#".toRegex(),
                ""
            )
        ) else existsUserByName(identifier)
    }

    override fun existsUserByName(name: String): Boolean {
        return users.stream().anyMatch { user -> user.name == name }
    }

    override fun existsUser(id: String): Boolean {
        return users.stream().anyMatch { user -> user.id == id }
    }

    override fun deleteUser(id: String) {
        users.removeIf { user -> user.id == id }
        File("user/$id.json").delete()
    }

    override fun getUsers(): List<User> {
        return users
    }

    override fun loadAll() {
        loadAll(File("user"))
    }

    override fun loadAll(space: File) {
        if (!space.isDirectory) {
            throw FileException("${space.name} isn't a valid user file space!")
        }

        val reserved = listOf('%', '#', '\'')

        space.listFiles()?.iterator()?.forEach { file ->
            val node = Json.read(file)

            val user = SimpleUser(
                name = node.get("name").asText(),
                password = node.get("password").asText(),
                accesses = Json.convert(node.get("accesses"))
            )

            if (node.has("id")) {
                user.id = node.get("id").asText()
            } else {
                user.id = file.name.replace(".json", "")
            }

            if (reserved.any { char -> user.name.contains(char) || user.id.contains(char) }) {
                throw UserException("User used reserved character in id or name!")
            }

            if (existsUserByName(user.name)) {
                throw UserException("User already has been initialized!")
            }

            user.preferredDatabaseId = if (node.has("preferred")) {
                node.get("preferred").asText()
            } else {
                node.get("preferredDatabaseId").asText()
            }

            users.add(user)
        }
    }

    override fun updateAll() {
        updateAll(File("user"))
    }

    override fun updateAll(space: File) {
        users.forEach { user -> Json.write(user, File("${space.path}/${user.id}.json")) }
    }
}
