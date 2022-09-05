package top.plutomc.qqbot;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.hikari.HikariConfig;
import com.google.gson.Gson;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import okhttp3.OkHttpClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.plutomc.qqbot.commands.*;
import top.plutomc.qqbot.utils.BindUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class QQBot extends JavaPlugin {
    public static final QQBot INSTANCE = new QQBot();
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
    public static final Gson GSON = new Gson();
    public static final HikariConfig HIKARI_CONFIG = new HikariConfig();
    public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static File configFile;
    private static FileConfiguration config;
    private static Bot targetBot;
    private static Listener<BotOnlineEvent> botOnlineListener;
    private static SQLManager sqlManager;

    private QQBot() {
        super(new JvmPluginDescriptionBuilder("top.plutomc.qq-bot", "0.1.0")
                .name("qq-bot")
                .author("GerryYuu")
                .build());
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
            } catch (IOException e) {
                QQBot.INSTANCE.getLogger().error("Failed to create config file!", e);
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        config.addDefault("botSettings.account", 10086L);
        config.addDefault("groupSettings.chatGroup", 114514L);
        config.addDefault("groupSettings.testGroup", 1919810L);
        config.addDefault("misc.rules", List.of("Example"));
        config.addDefault("database.host", "127.0.0.1");
        config.addDefault("database.port", 3306);
        config.addDefault("database.user", "root");
        config.addDefault("database.password", "123456");
        config.addDefault("database.database", "minecraft");
        config.addDefault("database.table", "qq_bind");

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

    @Override
    public void onEnable() {
        getLogger().info(" ");
        getLogger().info("PlutoMC QQ Bot | By GerryYuu");

        initConfig();

        botOnlineListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            if (targetBot == null && event.getBot().getId() == config.getLong("botSettings.account")) {
                targetBot = event.getBot();
                getLogger().info(" ");
                getLogger().info("Target bot (" + event.getBot().getId() + ") logged!");
                getLogger().info(" ");
            }
        });

        CommandManager.INSTANCE.registerCommand(new RulesCommand(), true);
        CommandManager.INSTANCE.registerCommand(new BindCommand(), true);
        CommandManager.INSTANCE.registerCommand(new BindsCommand(), true);
        CommandManager.INSTANCE.registerCommand(new UnbindCommand(), true);
        CommandManager.INSTANCE.registerCommand(new UnbindOtherCommand(), true);

        HIKARI_CONFIG.setDriverClassName("com.mysql.cj.jdbc.Driver");
        HIKARI_CONFIG.setJdbcUrl("jdbc:mysql://" + config.getString("database.host") + ":" + config.getInt("database.port") + "/");
        HIKARI_CONFIG.setUsername(config.getString("database.user"));
        HIKARI_CONFIG.setPassword(config.getString("database.password"));

        sqlManager = EasySQL.createManager(HIKARI_CONFIG);
        try {
            sqlManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sqlManager.executeSQLBatch("use " + config.getString("database.database") + ";");
        try {
            sqlManager.createTable(config.getString("database.table"))
                    .addColumn("uuid", "LONGTEXT")
                    .addColumn("name", "LONGTEXT")
                    .addColumn("qq", "BIGINT")
                    .setIndex(IndexType.INDEX, "qq_index", "qq")
                    .build().execute();
        } catch (SQLException e) {
            getLogger().error("Failed to create table!", e);
        }

        BindUtil.setSqlManager(sqlManager);

        getLogger().info("Plugin enabled.");
        getLogger().info(" ");
    }

    public static SQLManager getSqlManager() {
        return sqlManager;
    }

    @Override
    public void onDisable() {
        getLogger().info(" ");
        getLogger().info("Saving data...");

        EXECUTOR_SERVICE.shutdown();
        botOnlineListener.complete();

        getLogger().info("Plugin disabled.");
        getLogger().info(" ");
    }
}