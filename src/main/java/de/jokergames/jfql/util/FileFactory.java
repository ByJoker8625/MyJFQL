package de.jokergames.jfql.util;

import de.jokergames.jfql.exception.FileException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author Janick
 */

public class FileFactory {

    public JSONObject load(File file) {
        try {
            var reader = new FileReader(file);
            var builder = new StringBuilder();

            int read;

            while ((read = reader.read()) != -1) {
                builder.append((char) read);
            }

            reader.close();

            return new JSONObject(builder.toString());
        } catch (Exception ex) {
            throw new FileException("Can't load file '" + file.getName() + "'.");
        }
    }

    public void save(File file, JSONObject jsonObject) {
        try {
            var writer = new FileWriter(file);
            writer.write(jsonObject.toString());
            writer.close();
        } catch (Exception ex) {
            throw new FileException("Can't save file '" + file.getName() + "'.");
        }
    }

}
