package de.byjoker.myjfql.util

import de.byjoker.myjfql.command.ContextCommandSender
import de.byjoker.myjfql.database.TableEntry

class TableResult(
    val result: Collection<TableEntry>,
    val type: ContextCommandSender.ResponseType = ContextCommandSender.ResponseType.RESULT,
    val structure: Collection<String>,
    val resultType: ResultType
)
