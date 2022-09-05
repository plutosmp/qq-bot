package top.plutomc.qqbot.commands;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.message.data.At;
import top.plutomc.qqbot.QQBot;
import top.plutomc.qqbot.utils.BindUtil;
import top.plutomc.qqbot.utils.MCPlayerUtil;

import java.io.IOException;
import java.sql.SQLException;

public final class UnbindCommand extends JSimpleCommand {
    public UnbindCommand() {
        super(QQBot.INSTANCE, "unbind");
    }

    @Handler
    public void unbindOne(CommandContext context, String arg) {
        QQBot.EXECUTOR_SERVICE.submit(() -> {
            CommandSender sender = context.getSender();
            if (sender instanceof UserCommandSender) {
                if (BindUtil.getBindsName(sender.getUser().getId()).contains(arg.toLowerCase())) {
                    try {
                        BindUtil.unBind(MCPlayerUtil.getUUID(arg));
                        sender.sendMessage(new At(sender.getUser().getId()).plus(" 解绑成功！"));
                    } catch (SQLException | IOException e) {
                        sender.sendMessage(new At(sender.getUser().getId()).plus(" 解绑失败！请再试一次！如果这个问题一直出现，请联系管理员。"));
                        QQBot.INSTANCE.getLogger().error("Failed to unbind! (user: " + sender.getUser().getId() + ", tryToUnbind: " + arg + ")", e);
                    }
                } else {
                    sender.sendMessage(new At(sender.getUser().getId()).plus(" 你没有绑定这个游戏名！"));
                }
            } else {
                sender.sendMessage("Only users can run this command!");
            }
        });
    }
}