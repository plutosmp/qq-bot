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

import java.sql.SQLException;
import java.util.UUID;

public final class BindCommand extends JRawCommand {
    public BindCommand() {
        super(QQBot.INSTANCE, "bind");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        QQBot.EXECUTOR_SERVICE.submit(() -> {
            if (sender instanceof UserCommandSender) {
                if (args.get(0) instanceof PlainText) {
                    try {
                        sender.sendMessage(new At(sender.getUser().getId()).plus(new PlainText(" 正在尝试进行绑定操作...")));
                        UUID uuid = MCPlayerUtil.getUUID(args.get(0).contentToString());
                        if (!BindUtil.isBound(uuid)) {
                            if (!BindUtil.isWaitingToVerify(uuid)) {
                                QQBot.EXECUTOR_SERVICE.submit(() -> {
                                    try {
                                        Thread.sleep(1000L * 60L * 5L);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (BindUtil.isWaitingToVerify(uuid)) {
                                        try {
                                            BindUtil.completeVerify(uuid);
                                            sender.sendMessage(new At(sender.getUser().getId()).plus(new PlainText("对于 ")).plus(new PlainText(args.get(0).contentToString())).plus(new PlainText(" 的绑定验证已经超时！请重新执行绑定操作。")));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                BindUtil.verify(uuid, args.get(0).contentToString(), sender.getUser().getId());
                                // sender.sendMessage(new At(sender.getUser().getId()).plus(new PlainText(" 绑定成功！现在你可以进行游戏了。（请勿恶意绑定不是你的游戏名，否则将会遭到封禁！）")));
                                sender.sendMessage(new At(sender.getUser().getId()).plus(new PlainText(" 请在五分钟内使用 ").plus(new PlainText(args.get(0).contentToString()).plus(new PlainText(" 这个账号进入服务器来完成绑定验证！如果你现在无法进行验证，请在能够进行验证的时候重新进行绑定操作！")))));
                            }else {
                                sender.sendMessage(new At(sender.getUser().getId()).plus(" 已经提交绑定这个账号的请求了，请使用这个账号加入服务器来完成验证！"));
                            }
                        } else {
                            sender.sendMessage(new At(sender.getUser().getId())
                                    .plus(new PlainText(" 绑定失败！这个游戏名已经绑定给 ").plus(new At(BindUtil.getBind(uuid)))).plus(" 了！如果这个游戏名是你的却被别人绑定了，请联系群主。"));
                        }
                    } catch (Exception e) {
                        QQBot.INSTANCE.getLogger().error("Failed to bind! (user: " + sender.getUser().getId() + ", tryToBind: " + args.get(0).contentToString() + ")", e);
                        sender.sendMessage(new At(sender.getUser().getId()).plus(new PlainText(" 绑定失败！请检查用户名是否正确！（" + e.getClass().getName() + ": " + e.getMessage() + "）")));
                    }
                }
            } else {
                sender.sendMessage("Only users can run this command!");
            }
        });
    }
}