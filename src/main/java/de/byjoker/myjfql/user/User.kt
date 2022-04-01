package de.byjoker.myjfql.user

import java.time.LocalDate

interface User {

    var id: String
    var name: String
    var type: UserType
    var locked: Boolean
    var password: String
    fun validPassword(password: String): Boolean
    fun permitted()
    fun grantAccess()
    fun revokeAccess()
    var createdAt: LocalDate

}
