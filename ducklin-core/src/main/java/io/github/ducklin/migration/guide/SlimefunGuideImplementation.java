package io.github.ducklin.migration.guide;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.migration.items.ItemGroup;
import io.github.ducklin.api.items.DuckItem;
import io.github.ducklin.migration.player.PlayerProfile;
import io.github.ducklin.migration.researches.Research;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.ducklin.migration.guide.options.SlimefunGuideSettings;


public interface SlimefunGuideImplementation {

    /**
     * Every {@link SlimefunGuideImplementation} can be associated with a
     * {@link SlimefunGuideMode}.
     *
     * @return The mode this {@link SlimefunGuideImplementation} represents
     */
    @Nonnull
    SlimefunGuideMode getMode();

    /**
     * Returns the {@link ItemStack} representation for this {@link SlimefunGuideImplementation}.
     * In other words: The {@link ItemStack} you hold in your hand and that you use to
     * open your {@link SlimefunGuide}
     *
     * @return The {@link ItemStack} representation for this {@link SlimefunGuideImplementation}
     */
    @Nonnull
    ItemStack getItem();

    @ParametersAreNonnullByDefault
    void openMainMenu(PlayerProfile profile, int page);

    @ParametersAreNonnullByDefault
    void openItemGroup(PlayerProfile profile, ItemGroup group, int page);

    @ParametersAreNonnullByDefault
    void openSearch(PlayerProfile profile, String input, boolean addToHistory);

    @ParametersAreNonnullByDefault
    void displayItem(PlayerProfile profile, ItemStack item, int index, boolean addToHistory);

    @ParametersAreNonnullByDefault
    void displayItem(PlayerProfile profile, DuckItem item, boolean addToHistory);

    @ParametersAreNonnullByDefault
    default void unlockItem(Player p, DuckItem sfitem, Consumer<Player> callback) {
        Research research = sfitem.getResearch();

        if (p.getGameMode() == GameMode.CREATIVE && Slimefun.getRegistry().isFreeCreativeResearchingEnabled()) {
            research.unlock(p, true, callback);
        } else {
            p.setLevel(p.getLevel() - research.getCost());

            boolean skipLearningAnimation = Slimefun.getRegistry().isLearningAnimationDisabled() || !SlimefunGuideSettings.hasLearningAnimationEnabled(p);
            research.unlock(p, skipLearningAnimation, callback);
        }
    }

}
