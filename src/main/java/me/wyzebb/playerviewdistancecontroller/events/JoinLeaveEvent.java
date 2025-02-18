package me.wyzebb.playerviewdistancecontroller.events;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.UpdateChecker;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class JoinLeaveEvent implements Listener {

    private final MiniMessage mm;

    public JoinLeaveEvent() {
        this.mm = MiniMessage.miniMessage();
    }

    public static int getLuckpermsDistance(Player player) {
        try {
            Class.forName("net.luckperms.api.LuckPerms"); // Use reflection to check if LuckPerms is available
            return LuckPermsDataHandler.getLuckpermsDistance(player);
        } catch (ClassNotFoundException ex) {
            return 32; // Return default distance if LuckPerms is not available
        } catch (Exception ex) {
            plugin.getLogger().warning("An unknown error occurred while accessing LuckPerms data: " + ex.getMessage());
            return 32; // Return default distance if LuckPerms is not available
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && !UpdateChecker.isUpToDate()) {
            Component updateMsg = mm.deserialize("<yellow><b>(!)</b> <click:open_url:'https://modrinth.com/plugin/pvdc'><hover:show_text:'<green>Click to go to the plugin page</green>'>PVDC update available: <b><red>v" + plugin.getDescription().getVersion() + "</red> -> <green>v" + UpdateChecker.getLatestVersion() + "</green></b></hover></click></yellow>");

            e.getPlayer().sendMessage(updateMsg);
        }

        VdCalculator.calcVdAndSet(e.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(e.getPlayer());
        PlayerUtility playerDataHandler = new PlayerUtility();

        File playerDataFile = playerDataHandler.getPlayerDataFile(e.getPlayer());
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

        cfg.set("chunks", dataHandler.getChunks());
        cfg.set("chunksOthers", dataHandler.getChunksOthers());

        try {
            cfg.save(playerDataFile);
        } catch (IOException ioException) {
            plugin.getLogger().severe("IOException occurred while saving player view distance data for " + e.getPlayer().getName() + ": " + ioException.getMessage());
            ioException.printStackTrace(); // Print the stack trace for detailed debugging
        } catch (Exception ex) {
            plugin.getLogger().severe("An unexpected error occurred saving the player view distance data for " + e.getPlayer().getName() + ": " + ex.getMessage());
            ex.printStackTrace(); // Print the stack trace for unexpected errors
        } finally {
            PlayerViewDistanceController.playerAfkMap.remove(e.getPlayer().getUniqueId());
            PlayerUtility.setPlayerDataHandler(e.getPlayer(), null);
        }
    }
}
