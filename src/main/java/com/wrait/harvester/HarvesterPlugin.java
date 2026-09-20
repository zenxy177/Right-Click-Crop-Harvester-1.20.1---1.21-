package com.wrait.harvester;

import com.wrait.harvester.commands.HarvestCommand;
import com.wrait.harvester.config.ConfigManager;
import com.wrait.harvester.listeners.CropHarvestListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class HarvesterPlugin extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize Configuration
        configManager = new ConfigManager(this);

        // Register Event Listener
        getServer().getPluginManager().registerEvents(new CropHarvestListener(this), this);

        // Register Command
        HarvestCommand commandExecutor = new HarvestCommand(this);
        PluginCommand command = getCommand("sagtikhasat");
        if (command != null) {
            command.setExecutor(commandExecutor);
            command.setTabCompleter(commandExecutor);
        }

        getLogger().info("========================================");
        getLogger().info(" SagTikHasat v" + getDescription().getVersion() + " basariyla aktif edildi!");
        getLogger().info(" Minecraft 1.20.1+ Destekli Sağ Tık Hasat");
        getLogger().info(" Geliştirici: Wrait");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("SagTikHasat pasif konuma getirildi.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
