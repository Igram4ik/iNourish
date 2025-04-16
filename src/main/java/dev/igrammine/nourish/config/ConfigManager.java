package dev.igrammine.nourish.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoaderImpl.INSTANCE.getConfigDir().resolve("iNourish.json");
    private static final Logger logger = Logger.getLogger("iNourish");
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static iNourishConfig config;

    public static void loadConfig() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            config = GSON.fromJson(reader, iNourishConfig.class);
        } catch (IOException e) {
            config = new iNourishConfig();
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, writer);
            logger.fine("Config created successfully");
        } catch (IOException e) {
            logger.severe("Error on loading config: " + e);
        }
    }

    public static iNourishConfig getConfig() {
        return config;
    }
}
