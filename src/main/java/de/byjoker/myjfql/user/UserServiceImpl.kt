package de.byjoker.myjfql.user

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabaseActionPerformType
import de.byjoker.myjfql.exception.UserException
import de.byjoker.myjfql.util.FileFactory
import de.byjoker.myjfql.util.IDGenerator.generateDigits
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

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
        Arrays.stream(Objects.requireNonNull(space.listFiles())).forEach { file: File ->
            val jsonUser = factory.load(file)
            val jsonAccesses = jsonUser.getJSONObject("accesses")
            val name = file.name.replaceFirst(".json".toRegex(), "")
            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                MyJFQL.getInstance().console.logWarning("Database used unauthorized characters in the id!")
            } else {
                val user =
                    SimpleUser(id = name, name = jsonUser.getString("name"), password = jsonUser.getString("password"))
                user.preferredDatabaseId = jsonUser.getString("preferred")
                user.accesses = jsonAccesses.keySet().stream().collect(
                    Collectors.toMap(
                        { key: String? -> key },
                        { key: String? -> DatabaseActionPerformType.valueOf(jsonAccesses.getString(key)) },
                        { _: DatabaseActionPerformType?, b: DatabaseActionPerformType -> b })
                )

                if (!user.name.contains("%") && !user.name.contains("#") && !user.name.contains("'")) users.add(user) else
                    MyJFQL.getInstance().console.logWarning("User used unauthorized characters in the name!")
            }
        }
    }

    override fun updateAll() {
        updateAll(File("user"))
    }

    override fun updateAll(space: File) {
        users.forEach(Consumer { user ->
            val file = File(space.path + "/" + user.id + ".json")
            val jsonObject = JSONObject()
            jsonObject.put("name", user.name)
            jsonObject.put("password", user.password)
            jsonObject.put("accesses", user.accesses)
            jsonObject.put("preferred", user.preferredDatabaseId)
            factory.save(file, jsonObject)
        })
    }
}
