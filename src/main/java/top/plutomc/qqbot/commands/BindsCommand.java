package top.plutomc.qqbot.commands;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.UserCommandSender;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import top.plutomc.qqbot.QQBot;
import top.plutomc.qqbot.utils.BindUtil;
import top.plutomc.qqbot.utils.MsgUtil;

import java.util.Set;

public final class BindsCommand extends JRawCommand {

    public BindsCommand() {
        super(QQBot.INSTANCE, "binds", "绑定列表", "我绑定了哪些账号");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        if (!(sender instanceof UserCommandSender)) {
            MsgUtil.send2User(sender, new PlainText("只有用户可以执行这个指令！"));
        }

        QQBot.EXECUTOR_SERVICE.submit(() -> {
            Set<String> names = BindUtil.getBindsName(sender.getUser().getId());
            StringBuilder stringBuilder = new StringBuilder();
            for (String name : names) {
                stringBuilder.append(" " + name);
            }

            MsgUtil.send2User(sender, new PlainText("你当前绑定了以下账号。 (" + names.size() + " 个)：" + stringBuilder));
        });
    }
}
