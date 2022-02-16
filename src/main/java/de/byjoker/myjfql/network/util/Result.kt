package de.byjoker.myjfql.network.util

import de.byjoker.myjfql.database.TableEntry
import de.byjoker.myjfql.util.ResultType

data class Result(
    val result: Collection<TableEntry>,
    val structure: Collection<String>,
    val resultType: ResultType
) : Response(ResponseType.RESULT)
