package de.byjoker.myjfql.network.session

import de.byjoker.myjfql.util.IDGenerator

class StaticSession(
    token: String = IDGenerator.generateMixed(25),
    userId: String,
    databaseId: String? = null,
    addresses: MutableList<String> = mutableListOf()
) : Session(token, SessionType.STATIC, userId, databaseId, addresses) {

    override fun validAddress(address: String): Boolean {
        if (addresses.contains("*")) {
            return true
        }

        for (adr in addresses) {
            if (adr.contains("*")) {
                if (address.startsWith(adr.replace("*", ""))) {
                    return true
                }
            }

            if (adr == address) {
                return true
            }
        }

        return false
    }
}
