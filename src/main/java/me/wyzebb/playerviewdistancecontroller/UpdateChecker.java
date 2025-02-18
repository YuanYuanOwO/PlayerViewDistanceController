// Credit to Ferdzz on GitHub (https://github.com/Ferdzz) as this code is a modified version of their update checker

package me.wyzebb.playerviewdistancecontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class UpdateChecker implements Runnable {
    private static final boolean upToDate = false;
    private static String latest = "";

    @Override
    public void run() {
        plugin.getLogger().info("Checking for updates...");

        String versionUrl = "https://raw.githubusercontent.com/Wyzebb/PlayerViewDistanceController/refs/heads/master/version.txt";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(versionUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    latest = reader.readLine();
                }

                String pluginVersion = plugin.getDescription().getVersion();

                if (pluginVersion.equals(latest)) {
                    plugin.getLogger().info("Plugin is up to date!");
                } else {
                    plugin.getLogger().warning("Plugin is out of date! Please update from v" + pluginVersion + " to v" + latest + "!");
                }
            } else {
                plugin.getLogger().warning("Unable to check for updates! HTTP response code: " + connection.getResponseCode());
            }

        } catch (IOException exception) {
            plugin.getLogger().warning("Error while checking for updates: " + exception.getMessage());
        }
    }

    public static boolean isUpToDate() {
        return upToDate;
    }

    public static String getLatestVersion() {
        return latest;
    }
}