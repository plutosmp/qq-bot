package top.plutomc.qqbot.utils;

import cc.carm.lib.easysql.api.SQLManager;
import top.plutomc.qqbot.QQBot;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class BindUtil {
    private static SQLManager sqlManager;

    private BindUtil() {
    }

    public static void setSqlManager(SQLManager sqlManager) {
        BindUtil.sqlManager = sqlManager;
    }

    public static void bind(UUID uuid, String name, long qqAccount) throws SQLException {
        SQLManager sqlManager = BindUtil.sqlManager;
        sqlManager.executeSQLBatch("use " + QQBot.getConfig().getString("database.database") + ";");
        sqlManager.createReplace(QQBot.getConfig().getString("database.table"))
                .setColumnNames("uuid", "name", "qq")
                .setParams(uuid.toString(), name.toLowerCase(), qqAccount)
                .execute();
    }

    public static void unBind(UUID uuid) throws SQLException {
        SQLManager sqlManager = BindUtil.sqlManager;
        sqlManager.executeSQLBatch("use " + QQBot.getConfig().getString("database.database") + ";");
        sqlManager.createDelete(QQBot.getConfig().getString("database.table"))
                .addCondition("uuid", uuid.toString())
                .build()
                .execute();
    }

    public static long getBind(UUID uuid) {
        AtomicLong atomicLong = new AtomicLong();
        SQLManager sqlManager = BindUtil.sqlManager;
        sqlManager.executeSQLBatch("use " + QQBot.getConfig().getString("database.database") + ";");
        sqlManager.createQuery()
                .inTable(QQBot.getConfig().getString("database.table"))
                .selectColumns("uuid", "name", "qq")
                .addCondition("uuid", uuid.toString())
                .build().execute(sqlQuery -> {
                    sqlQuery.getResultSet().next();
                    atomicLong.set(sqlQuery.getResultSet().getLong("qq"));
                    return sqlQuery.getResultSet();
                }, (exception, sqlAction) -> {
                    QQBot.INSTANCE.getLogger().error("Failed to get bind!", exception);
                });
        return atomicLong.get();
    }

    public static Set<UUID> getBinds(long qq) {
        Set<AtomicReference<UUID>> atomicReferences = new HashSet<>();
        SQLManager sqlManager = BindUtil.sqlManager;
        sqlManager.executeSQLBatch("use " + QQBot.getConfig().getString("database.database") + ";");
        sqlManager.createQuery()
                .inTable(QQBot.getConfig().getString("database.table"))
                .selectColumns("uuid", "name", "qq")
                .addCondition("qq", qq)
                .build().execute(sqlQuery -> {
                    while (sqlQuery.getResultSet().next()) {
                        AtomicReference<UUID> atomicReference = new AtomicReference<>();
                        atomicReference.set(UUID.fromString(sqlQuery.getResultSet().getString("uuid")));
                        atomicReferences.add(atomicReference);
                    }
                    return sqlQuery.getResultSet();
                }, (exception, sqlAction) -> {
                });
        Set<UUID> uuids = new HashSet<>();
        for (AtomicReference<UUID> atomicReference : atomicReferences) {
            uuids.add(atomicReference.get());
        }
        return uuids;
    }

    public static Set<String> getBindsName(long qq) {
        Set<AtomicReference<String>> atomicReferences = new HashSet<>();
        SQLManager sqlManager = BindUtil.sqlManager;
        sqlManager.executeSQLBatch("use " + QQBot.getConfig().getString("database.database") + ";");
        sqlManager.createQuery()
                .inTable(QQBot.getConfig().getString("database.table"))
                .selectColumns("uuid", "name", "qq")
                .addCondition("qq", qq)
                .build().execute(sqlQuery -> {
                    while (sqlQuery.getResultSet().next()) {
                        AtomicReference<String> atomicReference = new AtomicReference<>();
                        atomicReference.set(sqlQuery.getResultSet().getString("name"));
                        atomicReferences.add(atomicReference);
                    }
                    return sqlQuery.getResultSet();
                }, (exception, sqlAction) -> {
                });
        Set<String> names = new HashSet<>();
        for (AtomicReference<String> atomicReference : atomicReferences) {
            names.add(atomicReference.get());
        }
        return names;
    }

    public static boolean isBound(UUID uuid) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        SQLManager sqlManager = BindUtil.sqlManager;
        sqlManager.executeSQLBatch("use " + QQBot.getConfig().getString("database.database") + ";");
        sqlManager.createQuery()
                .inTable(QQBot.getConfig().getString("database.table"))
                .selectColumns("uuid", "name", "qq")
                .addCondition("uuid", uuid.toString())
                .build().execute((sqlQuery -> {
                    atomicBoolean.set(sqlQuery.getResultSet().next());
                    return sqlQuery.getResultSet();
                }), (exception, sqlAction) -> {
                });
        return atomicBoolean.get();
    }
}