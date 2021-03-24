package org.jokergames.myjfql.util;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.FileException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

/**
 * @author Janick
 */

public class ConfigService {

    private final FileFactory factory;
    private final JSONObject configuration;
    private final JSONObject encryption;
    private boolean first = false;


    public ConfigService() {
        File file = new File("database");
        this.factory = new FileFactory();

        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File("module");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File("user");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File("script");

        if (!file.exists()) {
            first = true;
            file.mkdir();
        }

        file = new File("log.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileException("Can't save file 'log.txt'!");
            }
        }

        file = new File("config.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("AutoUpdate", true);
            jsonObject.put("Uppercase", false);
            jsonObject.put("Port", 2291);
            jsonObject.put("UpdateServer", "https://joker-games.org/lib/myjfql/rest.json");

            factory.save(file, jsonObject);
        }

        file = new File("encryption.json");

        if (!file.exists()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("default", "None");
            jsonObject.put("None", "key");

            {
                final Random random = new Random();
                int j;

                while ((j = random.nextInt(32)) < 5) ;
                boolean negative = random.nextBoolean();

                if (negative) {
                    jsonObject.put("DDP", String.valueOf(-j));
                } else {
                    jsonObject.put("DDP", String.valueOf(j));
                }
            }

            factory.save(file, jsonObject);
        }

        this.build(false);

        this.encryption = factory.load(new File("encryption.json"));
        this.configuration = factory.loadJoin(new File("config.json"), new File("build.json"));

        if (configuration.opt("AutoUpdate") == null)
            configuration.put("AutoUpdate", true);

        if (configuration.opt("Uppercase") == null)
            configuration.put("Uppercase", false);

        if (configuration.opt("Port") == null)
            configuration.put("Port", 2291);

        if (configuration.opt("UpdateServer") == null)
            configuration.put("UpdateServer", "https://joker-games.org/lib/myjfql/rest.json");

    }

    public void build(boolean overwrite) {
        File file = new File("build.json");
        JSONObject jsonObject = new JSONObject();

        if (!file.exists()
                || overwrite) {
            jsonObject.put("Build", MyJFQL.getInstance().getVersion());
            jsonObject.put("Date", LocalDate.now());
            factory.save(file, jsonObject);
        }
    }

    public JSONObject getConfig() {
        return configuration;
    }

    public JSONObject getEncryption() {
        return encryption;
    }

    public boolean isFirstStart() {
        return first;
    }

    public FileFactory getFactory() {
        return factory;
    }
}
