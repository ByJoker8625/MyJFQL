package de.byjoker.myjfql.config;

import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.util.Json;
import de.byjoker.myjfql.util.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigServiceImpl implements ConfigService {

    @Override
    public void buildDefaults() {
        File file = new File("config.yml");

        if (!file.exists())
            Yaml.write(new Config(), file);

        file = new File("sessions.json");

        if (!file.exists()) {
            Json.write(new ArrayList<Session>(), file);
        }
    }

    @Override
    public void mkdirs() {
        for (File file : Stream.of("database", "backup", "user").map(File::new).collect(Collectors.toList())) {
            if (!file.exists())
                file.mkdirs();
        }

        buildDefaults();
    }

    @Override
    public Config load() {
        return Yaml.parse(Yaml.read(new File("config.yml")), Config.class);
    }

}
