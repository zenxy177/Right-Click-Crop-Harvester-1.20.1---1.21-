package com.wrait.harvester.commands;

import com.wrait.harvester.HarvesterPlugin;
import com.wrait.harvester.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HarvestCommand implements CommandExecutor, TabCompleter {

    private final HarvesterPlugin plugin;

    public HarvestCommand(HarvesterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigManager config = plugin.getConfigManager();

        boolean isAdmin = sender.hasPermission("sagtikhasat.admin");
        boolean isUser = sender.hasPermission("sagtikhasat.use");

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!isAdmin) {
                sender.sendMessage(config.getNoPermissionMsg());
                return true;
            }
            config.loadConfig();
            sender.sendMessage(config.getConfigReloadedMsg());
            return true;
        }

        if (isUser || isAdmin) {
            sender.sendMessage(config.getInfoMsg());
            if (isAdmin) {
                sender.sendMessage("§8[§aSağTıkHasat§8] §7Yönetici Komutları: §f/" + label + " reload");
            }
            return true;
        }

        sender.sendMessage(config.getNoPermissionMsg());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("sagtikhasat.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], List.of("reload", "info"), completions);
            Collections.sort(completions);
            return completions;
        }

        return Collections.emptyList();
    }
}
