package top.plutomc.qqbot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class QQBot extends JavaPlugin {
    public static final QQBot INSTANCE = new QQBot();
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
    private static File configFile;
    private static FileConfiguration config;
    private static Bot targetBot;
    private static Listener<BotOnlineEvent> botOnlineListener;

    private QQBot() {
        super(new JvmPluginDescriptionBuilder("top.plutomc.qq-bot", "0.1.0")
                .name("qq-bot")
                .author("GerryYuu")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info(" ");
        getLogger().info("PlutoMC QQ Bot | By GerryYuu");

        initConfig();

        botOnlineListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event->{
            if (targetBot == null && event.getBot().getId() == config.getLong("botSettings.account")) {
                targetBot = event.getBot();
                getLogger().info(" ");
                getLogger().info("Target bot (" + event.getBot().getId() + ") logged!");
                getLogger().info(" ");
            }
        });

        getLogger().info("Plugin enabled.");
        getLogger().info(" ");
    }

    @Override
    public void onDisable() {
        getLogger().info(" ");
        getLogger().info("Saving data...");

        EXECUTOR_SERVICE.shutdown();

        getLogger().info("Plugin disabled.");
        getLogger().info(" ");
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    private static void initConfig() {
        configFile = new File("plutobot_config.yml");

        if (!configFile.exists()) {
            QQBot.INSTANCE.getLogger().info("Creating default config file...");
            try {
                configFile.createNewFile();
            }catch (IOException e) {
                QQBot.INSTANCE.getLogger().error("Failed to create config file!", e);
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        config.addDefault("botSettings.account", 10086L);
        config.addDefault("groupSettings.chatGroup", 114514L);
        config.addDefault("groupSettings.testGroup", 1919810L);

        config.options().copyDefaults(true);

        saveConfig();

        QQBot.INSTANCE.getLogger().info("Bot account: " + config.getLong("botSettings.account"));
        QQBot.INSTANCE.getLogger().info("Server chat group: " + config.getLong("groupSettings.chatGroup"));
        QQBot.INSTANCE.getLogger().info("Test group: " + config.getLong("groupSettings.testGroup"));
    }

    private static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}