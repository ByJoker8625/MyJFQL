package de.byjoker.myjfql.network.util

import com.fasterxml.jackson.databind.JsonNode

class NetworkRequest(
    override val connection: Connection,
    override val payload: JsonNode?,
    override val method: Request.Method
) : Request
