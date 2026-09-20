package com.wrait.harvester.listeners;

import com.wrait.harvester.HarvesterPlugin;
import com.wrait.harvester.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CropHarvestListener implements Listener {

    private final HarvesterPlugin plugin;
    private final Random random = new Random();

    public CropHarvestListener(HarvesterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle main hand right-click on block
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        // Check if block is a crop (Ageable)
        if (!(block.getBlockData() instanceof Ageable ageable)) return;

        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Ignore if player is holding Bone Meal in either hand
        // Prevents conflict with vanilla fertilizing and bone meal right-click macros
        if (mainHandItem.getType() == Material.BONE_MEAL || offHandItem.getType() == Material.BONE_MEAL) {
            return;
        }

        ConfigManager config = plugin.getConfigManager();
        Material cropType = block.getType();

        // Check if this crop type is enabled in config
        if (!config.isCropEnabled(cropType)) return;

        // Permission check
        if (!player.hasPermission("sagtikhasat.use")) return;

        boolean isHoe = mainHandItem.getType().name().endsWith("_HOE");

        // Hoe requirement check (Mandatory for balancing & macro prevention)
        if (config.isRequireHoe() && !isHoe) {
            player.sendMessage(config.getHoeRequiredMsg());
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.5f);
            return;
        }

        // Check if fully grown
        if (ageable.getAge() < ageable.getMaximumAge()) {
            if (config.isNotifyUnripe()) {
                player.sendMessage(config.getUnripeCropMsg());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.6f);
            }
            return;
        }

        // Cancel the interact event to prevent accidental block placement / swing
        event.setCancelled(true);

        // Get crop drops considering tool & Fortune
        ItemStack harvestTool = (config.isEnableFortune() && isHoe) ? mainHandItem : new ItemStack(Material.AIR);
        Collection<ItemStack> rawDrops = block.getDrops(harvestTool, player);
        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack drop : rawDrops) {
            drops.add(drop.clone());
        }

        Material seedMaterial = getSeedMaterial(cropType);

        // Seed consumption logic for replanting
        if (config.isConsumeSeed() && seedMaterial != null) {
            boolean seedFound = false;

            // 1. Try to take seed from crop drops
            for (ItemStack drop : drops) {
                if (drop.getType() == seedMaterial && drop.getAmount() > 0) {
                    drop.setAmount(drop.getAmount() - 1);
                    seedFound = true;
                    break;
                }
            }

            // 2. If no seed in drops, try taking from player's inventory safely using removeItem
            if (!seedFound) {
                ItemStack seedStack = new ItemStack(seedMaterial, 1);
                if (player.getInventory().containsAtLeast(seedStack, 1)) {
                    player.getInventory().removeItem(seedStack);
                    seedFound = true;
                }
            }

            // 3. If still no seed found anywhere, notify player and abort replant!
            if (!seedFound) {
                player.sendMessage(config.getNoSeedMsg());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }
        }

        // Replant crop (reset age to 0)
        ageable.setAge(0);
        block.setBlockData(ageable);

        // Clean up empty item stacks from drops
        drops.removeIf(item -> item == null || item.getAmount() <= 0 || item.getType() == Material.AIR);

        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

        // Auto inventory or drop on ground
        if (config.isAutoInventory()) {
            boolean inventoryFull = false;
            for (ItemStack item : drops) {
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                for (ItemStack leftoverItem : leftover.values()) {
                    block.getWorld().dropItemNaturally(dropLocation, leftoverItem);
                    inventoryFull = true;
                }
            }
            if (inventoryFull) {
                player.sendMessage(config.getInventoryFullMsg());
            }
        } else {
            for (ItemStack item : drops) {
                block.getWorld().dropItemNaturally(dropLocation, item);
            }
        }

        // Damage hoe if used
        if (config.isDamageHoe() && isHoe && mainHandItem.hasItemMeta()) {
            applyHoeDamage(player, mainHandItem);
        }

        // Play Sound
        if (config.isPlaySound()) {
            block.getWorld().playSound(dropLocation, config.getSound(), 1.0f, 1.0f);
        }

        // Play Particles
        if (config.isPlayParticles()) {
            block.getWorld().spawnParticle(config.getParticle(), dropLocation, 10, 0.2, 0.2, 0.2, 0.05);
        }
    }

    private Material getSeedMaterial(Material cropType) {
        return switch (cropType) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case NETHER_WART -> Material.NETHER_WART;
            case COCOA -> Material.COCOA_BEANS;
            default -> null;
        };
    }

    private void applyHoeDamage(Player player, ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return;

        // Check Unbreaking enchantment level
        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        if (unbreakingLevel > 0) {
            // Chance to reduce durability = 100 / (level + 1) %
            if (random.nextInt(unbreakingLevel + 1) > 0) {
                return; // Damage avoided by Unbreaking
            }
        }

        damageable.setDamage(damageable.getDamage() + 1);
        tool.setItemMeta(damageable);

        // Check if tool broke
        if (damageable.getDamage() >= tool.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(null);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }
}
