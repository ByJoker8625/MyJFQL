package org.jokergames.jfql.util;

import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.exception.NetworkException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Janick
 */

public class Downloader {

    private final Connection connection;

    public Downloader(Connection connection) {
        this.connection = connection;
    }

    public void download() {
        final int j = 1024;

        try {
            final URL url = new URL(connection.getDownload());
            final File file = new File("MyJFQL.jar");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream, j);

            JFQL.getInstance().getConsole().logInfo("Starting download...");

            byte[] bytes = new byte[j];
            int read;

            while ((read = inputStream.read(bytes, 0, j)) >= 0) {
                outputStream.write(bytes, 0, read);
            }

            outputStream.close();
            inputStream.close();

            JFQL.getInstance().getConsole().logInfo("Download completed.");
            JFQL.getInstance().getConfigService().build();
            System.exit(0);
        } catch (Exception ex) {
            throw new NetworkException("Download failed!");
        }
    }

}
