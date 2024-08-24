package firenh.eleanorsvillagers.config;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;

import firenh.eleanorsvillagers.EleanorsVillagers;

public class EVConfig {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //Config Default Values

    public String comment = "Chances are a decimal on a scale of 0 to 1";
    public String CONFIG_VERSION_DO_NOT_TOUCH_PLS = EleanorsVillagers.VERSION;
    public EVEnchantedBookConfig enchanted_book_config = new EVEnchantedBookConfig();
    
    //~~~~~~~~

    public static EVConfig init() {
        EVConfig config = null;

        try {
            Path configPath = Paths.get("", "config", "eleanorsvillagers.json");

            if (Files.exists(configPath)) {
                config = gson.fromJson(
                    new FileReader(configPath.toFile()),
                    EVConfig.class
                );

                if (!config.CONFIG_VERSION_DO_NOT_TOUCH_PLS.equals(EleanorsVillagers.VERSION)) {
                    config.CONFIG_VERSION_DO_NOT_TOUCH_PLS = EleanorsVillagers.VERSION;

                    BufferedWriter writer = new BufferedWriter(
                        new FileWriter(configPath.toFile())
                    );

                    writer.write(gson.toJson(config));
                    writer.close();
                }

            } else {
                config = new EVConfig();
                Paths.get("", "config").toFile().mkdirs();

                BufferedWriter writer = new BufferedWriter(
                    new FileWriter(configPath.toFile())
                );

                writer.write(gson.toJson(config));
                writer.close();
            }


        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return config;
    }
}
