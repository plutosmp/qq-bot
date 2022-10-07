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
import top.plutomc.qqbot.utils.MCPlayerUtil;
import top.plutomc.qqbot.utils.MsgUtil;

import java.sql.SQLException;
import java.util.UUID;

public final class BindCommand extends JRawCommand {
    public BindCommand() {
        super(QQBot.INSTANCE, "bind", "绑定", "绑定游戏账号");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        if (!(sender instanceof UserCommandSender)) {
            MsgUtil.send2User(sender, new PlainText("只有用户可以执行这个指令！"));
            return;
        }

        if (!(args.get(0) instanceof PlainText)) {
            MsgUtil.send2User(sender, new PlainText("游戏账号名必须是文本！"));
            return;
        }

        try {
            MsgUtil.send2User(sender, new PlainText("正在尝试进行绑定操作..."));
            UUID uuid = MCPlayerUtil.getUUID(args.get(0).contentToString());

            if (BindUtil.isBound(uuid)) {
                MsgUtil.send2User(sender, new PlainText("绑定失败！这个账号已经绑定给 ").plus(new At(BindUtil.getBind(uuid))).plus(new PlainText(" 啦。如果对方恶意绑定了你的账号，请联系管理员！")));
                return;
            }

            if (BindUtil.isWaitingToVerify(uuid)) {
                MsgUtil.send2User(sender, new PlainText("你已经提交绑定这个账号的请求了哦，请使用这个账号加入服务器来完成验证！"));
                return;
            }

            QQBot.EXECUTOR_SERVICE.submit(() -> {
                try {
                    Thread.sleep(1000L * 60L * 5L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!BindUtil.isWaitingToVerify(uuid)) {
                    return;
                }

                try {
                    BindUtil.completeVerify(uuid);
                    MsgUtil.send2User(sender, new PlainText("绑定请求已经过期啦！请重新操作。"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            QQBot.INSTANCE.getLogger().error("Failed to bind! (user: " + sender.getUser().getId() + ", tryToBind: " + args.get(0).contentToString() + ")", e);
            MsgUtil.send2User(sender, new PlainText("唔... 绑定似乎失败了。请确定你的账号名没有输错，然后再试一次。"));
        }
    }
}