package de.byjoker.myjfql.network.util

import com.fasterxml.jackson.databind.JsonNode

interface Request {

    val connection: Connection
    val payload: JsonNode?
    val method: RequestMethod

}
