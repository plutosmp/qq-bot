package top.plutomc.qqbot.commands;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.message.data.PlainText;
import top.plutomc.qqbot.QQBot;
import top.plutomc.qqbot.utils.BindUtil;
import top.plutomc.qqbot.utils.MCPlayerUtil;
import top.plutomc.qqbot.utils.MsgUtil;

import java.io.IOException;
import java.sql.SQLException;

public final class UnbindCommand extends JSimpleCommand {
    public UnbindCommand() {
        super(QQBot.INSTANCE, "unbind");
    }

    @Handler
    public void unbindOne(CommandContext context, String arg) {
        CommandSender sender = context.getSender();

        if (!(sender instanceof UserCommandSender)) {
            MsgUtil.send2User(sender, new PlainText("只有用户可以执行这个指令！"));
        }

        QQBot.EXECUTOR_SERVICE.submit(() -> {
            if (!BindUtil.getBindsName(sender.getUser().getId()).contains(arg.toLowerCase())) {
                MsgUtil.send2User(sender, new PlainText("你没有绑定这个账号哦！"));
            }

            try {
                BindUtil.unBind(MCPlayerUtil.getUUID(arg));
                MsgUtil.send2User(sender, new PlainText("解绑失败！"));
            } catch (SQLException | IOException e) {
                QQBot.INSTANCE.getLogger().error("Failed to unbind! (user: " + sender.getUser().getId() + ", tryToUnbind: " + arg + ")", e);
                MsgUtil.send2User(sender, new PlainText("解绑失败！请再试一次！如果这个问题一直出现，请联系管理员。"));
            }
        });
    }
}