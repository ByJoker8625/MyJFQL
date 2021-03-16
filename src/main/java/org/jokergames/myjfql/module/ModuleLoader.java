package org.jokergames.myjfql.module;

import org.jokergames.myjfql.exception.FileException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Janick
 */

public class ModuleLoader {

    public ModuleInfo loadFile(File file) throws Exception {
        JarFile jarFile = new JarFile(file);
        JarEntry jarEntry = jarFile.getJarEntry("config.json");

        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
        } catch (Exception ex) {
            throw new FileException("Module.json was not found!");
        }


        JSONObject jsonObject;

        {
            StringBuilder stringBuilder = new StringBuilder();

            int count;

            while ((count = bufferedReader.read()) != -1) {
                stringBuilder.append((char) count);
            }

            jsonObject = new JSONObject(stringBuilder.toString());
        }

        String name;
        String main;

        try {
            name = jsonObject.getString("Name");
            main = jsonObject.getString("Main");
        } catch (Exception ex) {
            throw new FileException("Fill all arguments in the module.json!");
        }

        return new ModuleInfo(file, name, main);
    }

    public ModuleInfo[] loadDirectory(File file) throws Exception {
        final File[] files = file.listFiles();

        List<File> plugins = Arrays.stream(Objects.requireNonNull(files)).filter(current -> current.getName().endsWith(".jar")).collect(Collectors.toList());
        int length = plugins.size();

        File[] jars = new File[length];
        int index = 0;

        for (File current : plugins) {
            jars[index] = current;
            index++;
        }

        ModuleInfo[] moduleInfos = new ModuleInfo[length];

        IntStream.range(0, jars.length).forEach(j -> {
            final File current = jars[j];
            try {
                moduleInfos[j] = loadFile(current);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return moduleInfos;
    }

}
