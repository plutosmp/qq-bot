package top.plutomc.qqbot.commands;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import top.plutomc.qqbot.QQBot;
import top.plutomc.qqbot.utils.BindUtil;

import java.util.Set;

public final class BindsCommand extends JRawCommand {

    public BindsCommand() {
        super(QQBot.INSTANCE, "binds", "bindlist");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        QQBot.EXECUTOR_SERVICE.submit(() -> {
            if (sender instanceof UserCommandSender) {
                Set<String> names = BindUtil.getBindsName(sender.getUser().getId());
                StringBuilder stringBuilder = new StringBuilder();
                for (String name : names) {
                    stringBuilder.append(" " + name);
                }
                sender.sendMessage(new At(sender.getUser().getId())
                        .plus(new PlainText("你当前绑定了以下游戏名，以空格分割。 (" + names.size() + " 个)：" + stringBuilder)));
            }else {
                sender.sendMessage("Only users can run this command!");
            }
        });
    }
}
