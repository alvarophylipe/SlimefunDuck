package io.github.ducklin.core.services.localization;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.ducklin.core.Slimefun;
import io.github.ducklin.api.MinecraftVersion;
import io.github.ducklin.migration.SlimefunBranch;
import io.github.ducklin.migration.recipes.RecipeType;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.ducklin.core.services.LocalizationService;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * This is an abstract parent class of {@link LocalizationService}.
 * There is not really much more I can say besides that...
 *
 * @author TheBusyBiscuit
 *
 * @see LocalizationService
 *
 */
public abstract class SlimefunLocalization implements Keyed {

    private final Config defaultConfig;

    protected SlimefunLocalization(@Nonnull Slimefun plugin) {
        this.defaultConfig = new Config(plugin, "messages.yml");
    }

    protected @Nonnull Config getConfig() {
        return defaultConfig;
    }

    protected void save() {
        defaultConfig.save();
    }


    public @Nonnull String getChatPrefix() {
        return getMessage("prefix");
    }



    public abstract @Nullable Language getLanguage(@Nonnull String id);


    public abstract @Nullable Language getLanguage(@Nonnull Player p);


    public abstract @Nullable Language getDefaultLanguage();


    protected abstract boolean hasLanguage(@Nonnull String id);


    public abstract @Nonnull Collection<Language> getLanguages();

    protected abstract void addLanguage(@Nonnull String id, @Nonnull String texture);

    /**
     * This will load every {@link LanguagePreset} into memory.
     * To be precise: It performs {@link #addLanguage(String, String)} for every
     * value of {@link LanguagePreset}.
     */
    protected void loadEmbeddedLanguages() {
        for (LanguagePreset lang : LanguagePreset.values()) {
            if (lang.isReadyForRelease() || Slimefun.getUpdater().getBranch() != SlimefunBranch.STABLE) {
                addLanguage(lang.getLanguageCode(), lang.getTexture());
            }
        }
    }

    private @Nonnull FileConfiguration getDefaultFile(@Nonnull LanguageFile file) {
        Language language = getLanguage(LanguagePreset.ENGLISH.getLanguageCode());

        if (language == null) {
            throw new IllegalStateException("Fallback language \"en\" is missing!");
        }

        FileConfiguration fallback = language.getFile(file);

        if (fallback != null) {
            return fallback;
        } else {
            throw new IllegalStateException("Fallback file: \"" + file.getFilePath("en") + "\" is missing!");
        }
    }

    @ParametersAreNonnullByDefault
    private @Nullable String getStringOrNull(@Nullable Language language, LanguageFile file, String path) {
        Validate.notNull(file, "You need to provide a LanguageFile!");
        Validate.notNull(path, "The path cannot be null!");

        if (language == null) {
            // Unit-Test scenario (or something went horribly wrong)
            return "Error: No language present";
        }

        FileConfiguration config = language.getFile(file);

        if (config != null) {
            String value = config.getString(path);

            // Return the found value (unless null)
            if (value != null) {
                return value;
            }
        }

        // Fallback to default configuration
        FileConfiguration defaults = getDefaultFile(file);
        String defaultValue = defaults.getString(path);

        // Return the default value or an error message
        return defaultValue != null ? defaultValue : null;
    }

    @ParametersAreNonnullByDefault
    private @Nonnull String getString(@Nullable Language language, LanguageFile file, String path) {
        String string = getStringOrNull(language, file, path);
        return string != null ? string : "! Missing string \"" + path + '"';
    }

    @ParametersAreNonnullByDefault
    private @Nullable List<String> getStringListOrNull(@Nullable Language language, LanguageFile file, String path) {
        Validate.notNull(file, "You need to provide a LanguageFile!");
        Validate.notNull(path, "The path cannot be null!");

        if (language == null) {
            // Unit-Test scenario (or something went horribly wrong)
            return Arrays.asList("Error: No language present");
        }

        FileConfiguration config = language.getFile(file);

        if (config != null) {
            List<String> value = config.getStringList(path);

            // Return the found value (unless empty)
            if (!value.isEmpty()) {
                return value;
            }
        }

        // Fallback to default configuration
        FileConfiguration defaults = getDefaultFile(file);
        List<String> defaultValue = defaults.getStringList(path);

        // Return the default value or an error message
        return !defaultValue.isEmpty() ? defaultValue : null;
    }

    @ParametersAreNonnullByDefault
    private @Nonnull List<String> getStringList(@Nullable Language language, LanguageFile file, String path) {
        List<String> list = getStringListOrNull(language, file, path);
        return list != null ? list : Arrays.asList("! Missing string \"" + path + '"');
    }

    public @Nonnull String getMessage(@Nonnull String key) {
        Validate.notNull(key, "Message key must not be null!");

        Language language = getDefaultLanguage();

        String message = language == null ? null : language.getFile(LanguageFile.MESSAGES).getString(key);

        if (message == null) {
            return getDefaultFile(LanguageFile.MESSAGES).getString(key);
        }

        return message;
    }

    public @Nonnull String getMessage(@Nonnull Player p, @Nonnull String key) {
        Validate.notNull(p, "Player must not be null!");
        Validate.notNull(key, "Message key must not be null!");

        return getString(getLanguage(p), LanguageFile.MESSAGES, key);
    }

    /**
     * Returns the Strings referring to the specified Key
     *
     * @param key
     *            The Key of those Messages
     * @return The List this key is referring to
     */
    public @Nonnull List<String> getDefaultMessages(@Nonnull String key) {
        return defaultConfig.getStringList(key);
    }

    public @Nonnull List<String> getMessages(@Nonnull Player p, @Nonnull String key) {
        Validate.notNull(p, "Player should not be null.");
        Validate.notNull(key, "Message key cannot be null.");

        return getStringList(getLanguage(p), LanguageFile.MESSAGES, key);
    }

    @ParametersAreNonnullByDefault
    public @Nonnull List<String> getMessages(Player p, String key, UnaryOperator<String> function) {
        Validate.notNull(p, "Player cannot be null.");
        Validate.notNull(key, "Message key cannot be null.");
        Validate.notNull(function, "Function cannot be null.");

        List<String> messages = getMessages(p, key);
        messages.replaceAll(function);

        return messages;
    }

    public @Nullable String getResearchName(@Nonnull Player p, @Nonnull NamespacedKey key) {
        Validate.notNull(p, "Player must not be null.");
        Validate.notNull(key, "NamespacedKey cannot be null.");

        return getStringOrNull(getLanguage(p), LanguageFile.RESEARCHES, key.getNamespace() + '.' + key.getKey());
    }

    public @Nullable String getItemGroupName(@Nonnull Player p, @Nonnull NamespacedKey key) {
        Validate.notNull(p, "Player must not be null.");
        Validate.notNull(key, "NamespacedKey cannot be null!");

        return getStringOrNull(getLanguage(p), LanguageFile.CATEGORIES, key.getNamespace() + '.' + key.getKey());
    }

    public @Nullable String getResourceString(@Nonnull Player p, @Nonnull String key) {
        Validate.notNull(p, "Player should not be null!");
        Validate.notNull(key, "Message key should not be null!");

        return getStringOrNull(getLanguage(p), LanguageFile.RESOURCES, key);
    }

    public @Nonnull ItemStack getRecipeTypeItem(@Nonnull Player p, @Nonnull RecipeType recipeType) {
        Validate.notNull(p, "Player cannot be null!");
        Validate.notNull(recipeType, "Recipe type cannot be null!");

        ItemStack item = recipeType.toItem();

        if (item == null) {
            // Fixes #3088
            return new ItemStack(Material.AIR);
        }

        Language language = getLanguage(p);
        NamespacedKey key = recipeType.getKey();

        return CustomItemStack.create(item, meta -> {
            String displayName = getStringOrNull(language, LanguageFile.RECIPES, key.getNamespace() + "." + key.getKey() + ".name");

            // Set the display name if possible, else keep the default item name.
            if (displayName != null) {
                meta.setDisplayName(ChatColor.AQUA + displayName);
            }

            List<String> lore = getStringListOrNull(language, LanguageFile.RECIPES, key.getNamespace() + "." + key.getKey() + ".lore");

            // Set the lore if possible, else keep the default lore.
            if (lore != null) {
                lore.replaceAll(line -> ChatColor.GRAY + line);
                meta.setLore(lore);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
    }

    public void sendMessage(@Nonnull CommandSender recipient, @Nonnull String key, boolean addPrefix) {
        Validate.notNull(recipient, "Recipient cannot be null!");
        Validate.notNull(key, "Message key cannot be null!");

        String prefix = addPrefix ? getChatPrefix() : "";

        if (recipient instanceof Player player) {
            recipient.sendMessage(ChatColors.color(prefix + getMessage(player, key)));
        } else {
            recipient.sendMessage(ChatColor.stripColor(ChatColors.color(prefix + getMessage(key))));
        }
    }

    public void sendActionbarMessage(@Nonnull Player player, @Nonnull String key, boolean addPrefix) {
        Validate.notNull(player, "Player cannot be null!");
        Validate.notNull(key, "Message key cannot be null!");

        String prefix = addPrefix ? getChatPrefix() : "";
        String message = ChatColors.color(prefix + getMessage(player, key));

        BaseComponent[] components = TextComponent.fromLegacyText(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    public void sendMessage(@Nonnull CommandSender recipient, @Nonnull String key) {
        sendMessage(recipient, key, true);
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender recipient, String key, UnaryOperator<String> function) {
        sendMessage(recipient, key, true, function);
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender recipient, String key, boolean addPrefix, UnaryOperator<String> function) {
        if (Slimefun.getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            return;
        }

        String prefix = addPrefix ? getChatPrefix() : "";

        if (recipient instanceof Player player) {
            recipient.sendMessage(ChatColors.color(prefix + function.apply(getMessage(player, key))));
        } else {
            recipient.sendMessage(ChatColor.stripColor(ChatColors.color(prefix + function.apply(getMessage(key)))));
        }
    }

    public void sendMessages(@Nonnull CommandSender recipient, @Nonnull String key) {
        String prefix = getChatPrefix();

        if (recipient instanceof Player player) {
            for (String translation : getMessages(player, key)) {
                String message = ChatColors.color(prefix + translation);
                recipient.sendMessage(message);
            }
        } else {
            for (String translation : getDefaultMessages(key)) {
                String message = ChatColors.color(prefix + translation);
                recipient.sendMessage(ChatColor.stripColor(message));
            }
        }
    }

    @ParametersAreNonnullByDefault
    public void sendMessages(CommandSender recipient, String key, boolean addPrefix, UnaryOperator<String> function) {
        String prefix = addPrefix ? getChatPrefix() : "";

        if (recipient instanceof Player player) {
            for (String translation : getMessages(player, key)) {
                String message = ChatColors.color(prefix + function.apply(translation));
                recipient.sendMessage(message);
            }
        } else {
            for (String translation : getDefaultMessages(key)) {
                String message = ChatColors.color(prefix + function.apply(translation));
                recipient.sendMessage(ChatColor.stripColor(message));
            }
        }
    }

    @ParametersAreNonnullByDefault
    public void sendMessages(CommandSender recipient, String key, UnaryOperator<String> function) {
        sendMessages(recipient, key, true, function);
    }

    protected @Nonnull Set<String> getTotalKeys(@Nonnull Language lang) {
        return getKeys(lang.getFiles());
    }

    protected @Nonnull Set<String> getKeys(@Nonnull FileConfiguration... files) {
        Set<String> keys = new HashSet<>();

        for (FileConfiguration cfg : files) {
            keys.addAll(cfg.getKeys(true));
        }

        return keys;
    }
}
