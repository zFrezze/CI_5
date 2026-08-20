package at.zFrezze.cyberInfra.config;

import at.zFrezze.cyberInfra.CyberInfra;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final CyberInfra main;
    private final String defaultLanguage;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();
    private final List<String> availableLanguages = List.of("en", "de", "at", "ch", "es", "fr", "");

    public ConfigManager(CyberInfra main) {
        this.defaultLanguage = main.getConfig().getString("language");
        this.main = main;

        for (String lang : availableLanguages) {

            File file = new File(main.getDataFolder(), "lang/" + lang + ".yml");
            if (!file.exists()) {
                main.getLogger().warning("Language file " + lang + " doesn't exist!");
                continue;
            }
            languages.put(lang, YamlConfiguration.loadConfiguration(file));
        }
        if (!languages.containsKey(defaultLanguage)) {
            main.getLogger().severe("Default language '" + defaultLanguage + "' could not be loaded!");
            Bukkit.getPluginManager().disablePlugin(main);
        }
    }

    public Component getMessage(ConfigMessage message, Map<String, String> placeholders, String language) {
        YamlConfiguration lang = languages.get(language);
        if (lang == null) {
            lang = languages.get(defaultLanguage);
        }
        String rawMessage = lang.getString(message.getKey(), "Missing: " + message.getKey());
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            rawMessage = rawMessage.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage);
    }

    public Component getMessage(ConfigMessage message, String language) {
        return getMessage(message, Map.of(), language);
    }

    public boolean isValidLanguage(String language) {
        return languages.containsKey(language);
    }

}
