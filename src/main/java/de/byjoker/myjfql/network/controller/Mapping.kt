package de.byjoker.myjfql.network.controller

import de.byjoker.myjfql.network.util.AccessLevel
import de.byjoker.myjfql.network.util.RequestMethod

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mapping(
    val path: String,
    val method: RequestMethod,
    val access: AccessLevel = AccessLevel.SESSION_AND_NO_SESSION
)
