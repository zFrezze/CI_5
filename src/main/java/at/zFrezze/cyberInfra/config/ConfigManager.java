package at.zFrezze.cyberInfra.config;

import at.zFrezze.cyberInfra.CyberInfra;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class ConfigManager {

    private YamlConfiguration yamlConfiguration;

    public ConfigManager(CyberInfra main) {
        File file = new File(main.getDataFolder(), main.getConfig().getString("language" + ".yml"));

        if (file.exists()) {
            yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        }else {
            main.getLogger().severe("CyberInfra couldn't start as an invalid language file was selected in config.yml!");
            Bukkit.getPluginManager().disablePlugin(main);
            return;
        }
    }

    public Component getMessage(ConfigMessage message, Map<String, String> placeholders) {
        String rawMessage =  yamlConfiguration.getString(message.getKey(), "Missing: " + message.getKey());
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            rawMessage = rawMessage.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage);
    }

    public Component getMessage(ConfigMessage message) {
        return getMessage(message, Map.of());
    }

}
