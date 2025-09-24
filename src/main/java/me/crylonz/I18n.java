package me.crylonz;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class I18n {
    private static YamlConfiguration messages;

    public static void init(Plugin plugin, String language) {
        File file = new File(plugin.getDataFolder(), "messages." + language + ".yml");
        if (!file.exists()) {
            String resourceName = "messages." + language + ".yml";
            try {
                InputStream in = plugin.getResource(resourceName);
                if (in != null) {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    in.close();
                } else {
                    resourceName = "messages.en.yml";
                    in = plugin.getResource(resourceName);
                    File en = new File(plugin.getDataFolder(), "messages.en.yml");
                    if (in != null) {
                        Files.copy(in, en.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        in.close();
                        file = en;
                    }
                }
            } catch (IOException ignored) {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!file.exists()) {
            InputStream in = plugin.getResource("messages.en.yml");
            if (in != null) {
                messages = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
                try {
                    in.close();
                } catch (IOException ignore) {
                }
                return;
            }
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public static String get(String key) {
        return messages.getString(key, key);
    }

    public static String format(String key, Object... args) {
        String msg = get(key);
        for (int i = 0; i < args.length; i += 2) {
            msg = msg.replace("{" + args[i] + "}", String.valueOf(args[i + 1]));
        }
        return msg;
    }
}
