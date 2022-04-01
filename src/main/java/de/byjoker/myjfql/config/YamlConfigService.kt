package de.byjoker.myjfql.config

import com.fasterxml.jackson.databind.JsonNode
import de.byjoker.myjfql.util.Yaml
import java.io.File

class YamlConfigService : ConfigService {

    override fun load(file: File): JsonNode {
        if (!file.exists()) {
            Yaml.write(GeneralConfig(), file)
        }

        return Yaml.read(file)
    }

    override fun loadMapped(): GeneralConfig {
        return Yaml.parse(load(File("config.yml")), GeneralConfig::class.java)
    }

}
