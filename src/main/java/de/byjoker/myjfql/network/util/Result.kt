package de.byjoker.myjfql.network.util

import com.fasterxml.jackson.databind.JsonNode

data class Result(
    val result: Collection<JsonNode>,
    val structure: Collection<String>,
    val resultType: ResultType
) : Response(ResponseType.RESULT) {

    enum class ResultType {
        RELATIONAL, DOCUMENT
    }

}
