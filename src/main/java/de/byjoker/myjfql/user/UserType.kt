package de.byjoker.myjfql.user

enum class UserType(val level: Int) {

    INTERNAL(4),
    MANAGER(3),
    WORKER(1);

    fun permitted(action: UserType): Boolean {
        return level >= action.level
    }

    fun permitted(permission: Permission): Boolean {
        return level >= permission.level
    }

}
