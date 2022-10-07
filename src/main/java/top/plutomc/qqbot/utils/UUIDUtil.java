package top.plutomc.qqbot.utils;

import java.util.UUID;

public final class UUIDUtil {
    private UUIDUtil() {
    }

    public static UUID trimmedToFull(String uuid) {
        return UUID.fromString(uuid.length() == 32 ? uuid.substring(0, 8) + '-' + uuid.substring(8, 12) + '-' + uuid.substring(12, 16) + '-' + uuid.substring(16, 20) + '-' + uuid.substring(20) : uuid);
    }
}
