package de.byjoker.myjfql.config

import com.fasterxml.jackson.databind.JsonNode
import java.io.File

interface ConfigService {

    fun load(file: File): JsonNode
    fun loadMapped(): GeneralConfig
    fun defaults(): GeneralConfig = GeneralConfig()

}
