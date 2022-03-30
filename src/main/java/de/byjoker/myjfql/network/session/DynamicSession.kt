package de.byjoker.myjfql.network.session

import de.byjoker.myjfql.util.IDGenerator

class DynamicSession(
    token: String = IDGenerator.generateMixed(25),
    userId: String,
    databaseId: String? = null,
    addresses: MutableList<String> = mutableListOf()
) : Session(token, Type.DYNAMIC, userId, databaseId, addresses) {

    override fun validAddress(address: String): Boolean {
        return address.contains(address)
    }

}
