package top.plutomc.qqbot.commands;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import top.plutomc.qqbot.QQBot;
import top.plutomc.qqbot.utils.MsgUtil;

public final class RulesCommand extends JRawCommand {
    public RulesCommand() {
        super(QQBot.INSTANCE, "rules", "rule", "规则", "服务器规则");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        if (!(sender instanceof UserCommandSender)) {
            MsgUtil.send2User(sender, new PlainText("只有用户可以执行这个指令！"));
        }

        sender.sendMessage(new At(sender.getUser().getId()));
        QQBot.getConfig().getStringList("misc.rules").forEach(
                sender::sendMessage
        );
    }
}