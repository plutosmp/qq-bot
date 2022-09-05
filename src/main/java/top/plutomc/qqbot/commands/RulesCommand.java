package top.plutomc.qqbot.commands;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import top.plutomc.qqbot.QQBot;

public final class RulesCommand extends JRawCommand {
    public RulesCommand() {
        super(QQBot.INSTANCE, "rules", "rule");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        if (sender instanceof UserCommandSender) {
            sender.sendMessage(new At(sender.getUser().getId()));
            QQBot.getConfig().getStringList("misc.rules").forEach(
                    s -> sender.sendMessage(s)
            );
        } else {
            sender.sendMessage("Only users can run this command!");
        }
    }
}