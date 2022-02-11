package de.byjoker.myjfql.server.response

import de.byjoker.myjfql.database.TableEntry
import de.byjoker.myjfql.util.ResultType

data class TableResult(
    val result: Collection<TableEntry>,
    val structure: Collection<String>,
    val resultType: ResultType
) : Response(ResponseType.RESULT)
