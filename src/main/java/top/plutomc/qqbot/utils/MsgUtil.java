package top.plutomc.qqbot.utils;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

public final class MsgUtil {
    private MsgUtil() {
    }

    public static void send2User(CommandSender sender, Message message) {
        if (sender instanceof UserCommandSender) {
            sender.sendMessage(new At(sender.getUser().getId()).plus(new PlainText(" ")).plus(message));
            return;
        }
        sender.sendMessage(message);
    }
}
