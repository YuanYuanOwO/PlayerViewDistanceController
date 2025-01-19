package me.wyzebb.playerviewdistancecontroller.utility;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.playerAfkMap;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "pvdc";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Wyzebb";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (params.contains("chunks_")) {
                String playerName = params.replace("chunks_", "");

                try {
                    Player target = Bukkit.getPlayer(playerName);
                    return String.valueOf(VdCalculator.calcVdAndGet(target));

                } catch (Exception e) {
                    MessageProcessor.processMessage("messages.player-offline", 1, 0, player);
                }


                return String.valueOf(VdCalculator.calcVdAndGet(player));
            } else if (params.equalsIgnoreCase("chunks")) {
                return String.valueOf(VdCalculator.calcVdAndGet(player));
            } else if (params.contains("afk_")) {
                String playerName = params.replace("afk_", "");

                try {
                    Player target = Bukkit.getPlayer(playerName);

                    if (playerAfkMap.get(target.getUniqueId()) == 0) {
                        return "AFK";
                    } else {
                        return "NOT AFK";
                    }

                } catch (Exception e) {
                    MessageProcessor.processMessage("messages.player-offline", 1, 0, player);
                }
            } else if (params.equalsIgnoreCase("afk")) {
                if (playerAfkMap.get(player.getUniqueId()) == 0) {
                    return "AFK";
                } else {
                    return "NOT AFK";
                }
            }
        }

        return null;
    }

    public static void registerHook() {
        new PlaceholderAPIExpansion().register();
    }
}