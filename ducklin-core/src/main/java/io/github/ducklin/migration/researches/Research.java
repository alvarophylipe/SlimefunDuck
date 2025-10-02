package io.github.ducklin.migration.researches;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.api.items.DuckItem;
import io.github.ducklin.core.Slimefun;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.migration.events.PlayerPreResearchEvent;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.migration.items.ItemState;
import io.github.ducklin.migration.player.PlayerProfile;


public class Research implements Keyed {

    private final NamespacedKey key;
    private final int id;
    private final String name;
    private boolean enabled = true;
    private int cost;

    private final List<DuckItem> items = new LinkedList<>();

    public Research(@Nonnull NamespacedKey key, int id, @Nonnull String defaultName, int defaultCost) {
        Validate.notNull(key, "A NamespacedKey must be provided");
        Validate.notNull(defaultName, "A default name must be specified");

        this.key = key;
        this.id = id;
        this.name = defaultName;
        this.cost = defaultCost;
    }

    @Override
    public @Nonnull NamespacedKey getKey() {
        return key;
    }

    public boolean isEnabled() {
        return Slimefun.getRegistry().isResearchingEnabled() && enabled;
    }

    @Deprecated
    public int getID() {
        return id;
    }


    public @Nonnull String getName(@Nonnull Player p) {
        String localized = Slimefun.getLocalization().getResearchName(p, key);
        return localized != null ? localized : name;
    }

    public @Nonnull String getUnlocalizedName() {
        return ChatColor.stripColor(name);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Research cost must be zero or greater!");
        }

        this.cost = cost;
    }


    public void addItems(DuckItem... items) {
        for (DuckItem item : items) {
            if (item != null) {
                item.setResearch(this);
            }
        }
    }

    @Nonnull
    public Research addItems(ItemStack... items) {
        for (ItemStack item : items) {
            DuckItem sfItem = DuckItem.getByItem(item);

            if (sfItem != null) {
                sfItem.setResearch(this);
            }
        }

        return this;
    }

    @Nonnull
    public List<DuckItem> getAffectedItems() {
        return items;
    }

    public boolean hasEnabledItems() {
        for (DuckItem item : items) {
            if (item.getState() == ItemState.ENABLED) {
                return true;
            }
        }
        return false;
    }


    @ParametersAreNonnullByDefault
    public void unlockFromGuide(SlimefunGuideImplementation guide, Player player, PlayerProfile profile, DuckItem sfItem, ItemGroup itemGroup, int page) {
        if (!Slimefun.getRegistry().getCurrentlyResearchingPlayers().contains(player.getUniqueId())) {
            if (profile.hasUnlocked(this)) {
                guide.openItemGroup(profile, itemGroup, page);
            } else {
                PlayerPreResearchEvent event = new PlayerPreResearchEvent(player, this, sfItem);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    if (this.canUnlock(player)) {
                        guide.unlockItem(player, sfItem, pl -> guide.openItemGroup(profile, itemGroup, page));
                    } else {
                        Slimefun.getLocalization().sendMessage(player, "messages.not-enough-xp", true);
                    }
                }
            }
        }
    }

    public boolean canUnlock(@Nonnull Player p) {
        if (!isEnabled()) {
            return true;
        }

        boolean creativeResearch = p.getGameMode() == GameMode.CREATIVE && Slimefun.getRegistry().isFreeCreativeResearchingEnabled();
        return creativeResearch || p.getLevel() >= cost;
    }


    public void unlock(@Nonnull Player p, boolean instant) {
        unlock(p, instant, null);
    }


    public void unlock(@Nonnull Player p, boolean isInstant, @Nullable Consumer<Player> callback) {
        PlayerProfile.get(p, new PlayerResearchTask(this, isInstant, callback));
    }

    public void register() {
        Slimefun.getResearchCfg().setDefaultValue("enable-researching", true);
        String path = key.getNamespace() + '.' + key.getKey();

        if (Slimefun.getResearchCfg().contains(path + ".enabled") && !Slimefun.getResearchCfg().getBoolean(path + ".enabled")) {
            for (DuckItem item : new ArrayList<>(items)) {
                if (item != null) {
                    item.setResearch(null);
                }
            }

            enabled = false;
            return;
        }

        Slimefun.getResearchCfg().setDefaultValue(path + ".cost", getCost());
        Slimefun.getResearchCfg().setDefaultValue(path + ".enabled", true);

        setCost(Slimefun.getResearchCfg().getInt(path + ".cost"));
        enabled = true;

        Slimefun.getRegistry().getResearches().add(this);
    }

    @Nonnull
    public static Optional<Research> getResearch(@Nullable NamespacedKey key) {
        if (key == null) {
            return Optional.empty();
        }

        for (Research research : Slimefun.getRegistry().getResearches()) {
            if (research.getKey().equals(key)) {
                return Optional.of(research);
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Research (" + getKey() + ')';
    }
}