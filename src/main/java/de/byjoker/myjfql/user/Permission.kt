package de.byjoker.myjfql.user

enum class Permission(val level: Int) {

    SHUTDOWN(3),
    CREATE_DATABASE(3),
    CREATE_USER(3),
    DELETE_DATABASE(3),
    DELETE_USER(3);

}
