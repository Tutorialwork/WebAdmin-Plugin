package de.tutorialwork.webadmin.utils;

import de.tutorialwork.webadmin.main.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class Updater {

    private String currentVersion;
    private boolean update = false;

    public Updater() {
        this.currentVersion = callURL("https://api.spigotmc.org/legacy/update.php?resource=72803");
        if(!Main.installedVersion.equals(this.currentVersion)){
            update = true;
        }
    }

    private static String callURL(String URL) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            java.net.URL url = new URL(URL);
            urlConn = url.openConnection();

            if (urlConn != null) urlConn.setReadTimeout(60 * 1000);

            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);

                if (bufferedReader != null) {
                    int cp;

                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }

                    bufferedReader.close();
                }
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public boolean isUpdate() {
        return update;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }
}
