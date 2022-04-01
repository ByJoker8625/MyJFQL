package de.byjoker.myjfql.user

enum class UserType(val level: Int) {

    INTERNAL(3),
    MANAGER(3),
    WORKER(1);

    fun permitted(action: UserType): Boolean {
        return level >= action.level
    }

}
