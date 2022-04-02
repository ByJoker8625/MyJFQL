package de.byjoker.myjfql.user

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import de.byjoker.myjfql.database.DatabasePermissionLevel
import java.time.LocalDate

interface User {

    var id: String
    var name: String
    var type: UserType
    var locked: Boolean
    var password: String
    fun validPassword(password: String): Boolean
    fun permitted(level: UserType): Boolean = type.permitted(level)
    fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean
    fun grantAccess(action: DatabasePermissionLevel, databaseId: String)
    fun revokeAccess(databaseId: String)


    @get:JsonSerialize(using = LocalDateSerializer::class)
    @get:JsonDeserialize(using = LocalDateDeserializer::class)
    var createdAt: LocalDate

}
