package com.wrait.harvester.config;

import com.wrait.harvester.HarvesterPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final HarvesterPlugin plugin;
    private final Map<Material, Boolean> enabledCrops = new HashMap<>();

    private boolean requireHoe;
    private boolean enableFortune;
    private boolean autoInventory;
    private boolean consumeSeed;
    private boolean notifyUnripe;
    private boolean damageHoe;

    private boolean playSound;
    private Sound sound;

    private boolean playParticles;
    private Particle particle;

    private String prefix;
    private String noPermissionMsg;
    private String configReloadedMsg;
    private String hoeRequiredMsg;
    private String unripeCropMsg;
    private String noSeedMsg;
    private String inventoryFullMsg;
    private String infoMsg;

    public ConfigManager(HarvesterPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Load crop settings
        enabledCrops.clear();
        if (config.isConfigurationSection("enabled-crops")) {
            for (String key : config.getConfigurationSection("enabled-crops").getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    boolean enabled = config.getBoolean("enabled-crops." + key, true);
                    enabledCrops.put(mat, enabled);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Bilinmeyen ekin türü konfigürasyonda atlandı: " + key);
                }
            }
        }

        // Mechanics
        requireHoe = config.getBoolean("require-hoe", false);
        enableFortune = config.getBoolean("enable-fortune", true);
        autoInventory = config.getBoolean("auto-inventory", false);
        consumeSeed = config.getBoolean("consume-seed", true);
        notifyUnripe = config.getBoolean("notify-unripe", true);
        damageHoe = config.getBoolean("damage-hoe", true);

        // Sound
        playSound = config.getBoolean("effects.play-sound", true);
        String soundStr = config.getString("effects.sound-name", "BLOCK_CROP_BREAK");
        try {
            sound = Sound.valueOf(soundStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Geçersiz ses adı: " + soundStr + ". Varsayılan BLOCK_CROP_BREAK kullanılıyor.");
            sound = Sound.BLOCK_CROP_BREAK;
        }

        // Particle
        playParticles = config.getBoolean("effects.play-particles", true);
        String particleStr = config.getString("effects.particle-name", "VILLAGER_HAPPY");
        try {
            particle = Particle.valueOf(particleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Geçersiz parçacık adı: " + particleStr + ". Varsayılan VILLAGER_HAPPY kullanılıyor.");
            particle = Particle.VILLAGER_HAPPY;
        }

        // Messages
        prefix = color(config.getString("messages.prefix", "&8[&aSağTıkHasat&8] "));
        noPermissionMsg = prefix + color(config.getString("messages.no-permission", "&cBu komutu kullanmak için yetkiniz yok!"));
        configReloadedMsg = prefix + color(config.getString("messages.config-reloaded", "&aKonfigürasyon başarıyla yeniden yüklendi."));
        hoeRequiredMsg = prefix + color(config.getString("messages.hoe-required", "&cEkinleri toplayabilmek için elinizde bir çapa olmalıdır!"));
        unripeCropMsg = prefix + color(config.getString("messages.unripe-crop", "&cBu ekin henüz olgunlaşmadı!"));
        noSeedMsg = prefix + color(config.getString("messages.no-seed", "&cEkinin yeniden ekilmesi için yeterli tohumunuz yok!"));
        inventoryFullMsg = prefix + color(config.getString("messages.inventory-full", "&eEnvanteriniz dolu olduğu için bazı ürünler yere düştü!"));
        infoMsg = prefix + color(config.getString("messages.info-message", "&aSağ Tık Hasat aktif! Olgunlaşmış ekinlere sağ tıklayarak toplayabilirsin."));
    }

    private String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public boolean isCropEnabled(Material material) {
        return enabledCrops.getOrDefault(material, false);
    }

    public boolean isRequireHoe() {
        return requireHoe;
    }

    public boolean isEnableFortune() {
        return enableFortune;
    }

    public boolean isAutoInventory() {
        return autoInventory;
    }

    public boolean isConsumeSeed() {
        return consumeSeed;
    }

    public boolean isNotifyUnripe() {
        return notifyUnripe;
    }

    public boolean isDamageHoe() {
        return damageHoe;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public Sound getSound() {
        return sound;
    }

    public boolean isPlayParticles() {
        return playParticles;
    }

    public Particle getParticle() {
        return particle;
    }

    public String getNoPermissionMsg() {
        return noPermissionMsg;
    }

    public String getConfigReloadedMsg() {
        return configReloadedMsg;
    }

    public String getHoeRequiredMsg() {
        return hoeRequiredMsg;
    }

    public String getUnripeCropMsg() {
        return unripeCropMsg;
    }

    public String getNoSeedMsg() {
        return noSeedMsg;
    }

    public String getInventoryFullMsg() {
        return inventoryFullMsg;
    }

    public String getInfoMsg() {
        return infoMsg;
    }
}
