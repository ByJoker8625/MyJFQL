package de.byjoker.myjfql.util;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.exception.NetworkException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {

    private final Updater updater;

    public Downloader(Updater updater) {
        this.updater = updater;
    }

    public void downloadLatestVersion() {
        downloadByVersion("v" + updater.getLatestVersion());
    }

    public void downloadByVersion(String version) {
        downloadByURL(updater.getDownloads().get(version));
    }

    public void downloadByURL(String download) {
        final int j = 1024;

        try {
            final URL url = new URL(download);
            final File file = new File(MyJFQL.getInstance().getConfigService().getConfig().updateFile());

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream, j);

            MyJFQL.getInstance().getConsole().logInfo("Starting download...");

            final byte[] bytes = new byte[j];
            int read;

            while ((read = inputStream.read(bytes, 0, j)) >= 0) {
                outputStream.write(bytes, 0, read);
            }

            outputStream.close();
            inputStream.close();

            MyJFQL.getInstance().getConsole().logInfo("Download completed.");
            MyJFQL.getInstance().shutdown();
        } catch (Exception ex) {
            throw new NetworkException("Download failed!");
        }
    }

}
