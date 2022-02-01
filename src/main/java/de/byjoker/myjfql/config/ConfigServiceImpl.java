package de.byjoker.myjfql.config;

import com.google.common.reflect.ClassPath;
import de.byjoker.myjfql.exception.FileException;
import de.byjoker.myjfql.util.FileFactory;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.reflect.ClassPath.from;

public class ConfigServiceImpl implements ConfigService {

    private final List<ConfigBuilder> configBuilders;
    private final FileFactory factory;
    private JSONObject config;

    public ConfigServiceImpl() {
        this.factory = new FileFactory();
        this.configBuilders = new ArrayList<>();
        this.config = null;
    }

    @Override
    public void registerConfigBuilder(ConfigBuilder configBuilder) {
        configBuilders.add(configBuilder);
    }

    @Override
    public void unregisterConfigBuilder(ConfigBuilder configBuilder) {
        configBuilders.remove(configBuilder);
    }

    @Override
    public void searchConfigBuilders(String path) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            for (ClassPath.ClassInfo info : from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(path)) {
                    try {
                        final Class<? extends ConfigBuilder> clazz = (Class<? extends ConfigBuilder>) info.load();

                        if (clazz.isAnnotationPresent(ConfigHandler.class))
                            registerConfigBuilder(clazz.newInstance());
                    } catch (Exception ignore) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConfigBuilder getConfigBuilder(JSONObject json) {
        final List<String> fields = new ArrayList<>(json.toMap().keySet());
        return configBuilders.stream().filter(configBuilder -> configBuilder.getIdentifiers().equals(fields)).findFirst().orElse(null);
    }

    @Override
    public List<ConfigBuilder> getConfigBuilders() {
        return configBuilders;
    }

    @Override
    public void load() {
        buildRequirements();
        config = factory.load(new File("config.json"));
    }

    @Override
    public void buildRequirements() {
        File file = new File("database");

        if (!file.exists())
            file.mkdirs();

        file = new File("user");

        if (!file.exists())
            file.mkdirs();

        file = new File("backup");

        if (!file.exists())
            file.mkdirs();

        file = new File("sessions.json");

        if (!file.exists())
            factory.save(file, new JSONObject().put("sessions", Collections.emptyList()));

        file = new File("config.json");

        if (!file.exists())
            factory.save(file, new ConfigDefaults().asJson());
    }

    @Override
    public Config getConfig() {
        if (config == null)
            throw new FileException("File 'config.json' wasn't load!");

        ConfigBuilder builder = getConfigBuilder(config);

        if (builder == null)
            throw new FileException("File 'config.json' is incompatible!");

        return builder.build(config);
    }
}
