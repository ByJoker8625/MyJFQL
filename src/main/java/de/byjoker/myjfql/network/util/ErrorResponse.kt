package de.byjoker.myjfql.network.util

data class ErrorResponse(val exception: String?) : Response(ResponseType.ERROR)
