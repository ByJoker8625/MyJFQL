package de.byjoker.myjfql.config

import com.fasterxml.jackson.databind.JsonNode
import java.io.File
import java.lang.reflect.Field

interface ConfigService {

    fun load(file: File): JsonNode
    fun loadMapped(): GeneralConfig

}
