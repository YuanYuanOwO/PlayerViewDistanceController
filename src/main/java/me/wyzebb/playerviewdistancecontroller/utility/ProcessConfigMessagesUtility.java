package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.UpdateChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ProcessConfigMessagesUtility {
    public static void processMessage(String configPath, CommandSender commandSender) {
        String msg = plugin.getConfig().getString(configPath);

        msg = getProcessedConfigMessage(msg);

        if (!(commandSender instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(msg);
        }
    }

    public static void processMessage(String configPath, CommandSender commandSender, int amount) {
        String msg = plugin.getConfig().getString(configPath);

        msg = getProcessedConfigMessage(msg, amount);

        if (!(commandSender instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(msg);
        }
    }

    public static void processMessage(String configPath, int amount, Player target, CommandSender toSendTo) {
        String msg = plugin.getConfig().getString(configPath);

        msg = getProcessedConfigMessage(msg, amount, target);

        if (!(toSendTo instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(toSendTo instanceof ConsoleCommandSender)) {
            toSendTo.sendMessage(msg);
        }
    }

    public static void processMessage(String configPath, Player target, CommandSender toSendTo) {
        String msg = plugin.getConfig().getString(configPath);

        msg = getProcessedConfigMessage(msg, target);

        if (!(toSendTo instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(toSendTo instanceof ConsoleCommandSender)) {
            toSendTo.sendMessage(msg);
        }
    }

    public static String getProcessedConfigMessage(String msg, int amount) {
        msg = msg.replace("{chunks}", String.valueOf(amount));
        return msg;
    }

    public static String getProcessedConfigMessage(String msg) {
        msg = msg.replace("{current}", plugin.getDescription().getVersion());
        msg = msg.replace("{latest}", UpdateChecker.getLatestVersion());
        return msg;
    }

    public static String getProcessedConfigMessage(String msg, int amount, Player target) {
        msg = msg.replace("{chunks}", String.valueOf(amount));
        msg = msg.replace("{target-player}", target.getName());
        return msg;
    }

    public static String getProcessedConfigMessage(String msg, Player target) {
        msg = msg.replace("{target-player}", target.getName());
        return msg;
    }
}
