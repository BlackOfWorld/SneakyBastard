package net.blackofworld.SneakyBastard.Commands.Miscellaneous;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.entity.Player;

import java.util.ArrayList;


@CommandInfo(command = "info", category = CommandCategory.Miscellaneous, description = "Tells you stuff about yourself or another player", Syntax = "[player]")
@ExtensionMethod({Player.class, PlayerExt.class})

public class Info extends CommandBase {

    @Override
    public void Execute(Player p, ArrayList<String> args) {
        p = firstParamCouldBePlayer(p, args);
        p.Reply("Name: " + p.getName());
        p.Reply("UUID: " + p.getUniqueId());
        p.Reply("Health: " + p.getHealth());
        p.Reply("Health scale: " + p.getHealthScale());
        p.Reply("Invulnerable: " + p.isInvulnerable());
        p.Reply("Locale: " + p.getLocale());
        p.Reply("Op: " + p.isOp());
        p.Reply("Dead: " + p.isDead());
        p.Reply("Gravity: " + p.hasGravity());
        p.Reply(String.format("Location: %s | %.2f | %.2f %.2f", p.getWorld().getName(),p.getLocation().getX(),
                p.getLocation().getY(), p.getLocation().getZ()));
        p.Reply("Walk speed: " + p.getWalkSpeed());
        p.Reply("Fly speed: " + p.getFlySpeed());

        p.Reply("IP: " + p.getAddress().getAddress().getHostAddress());
        p.Reply("Ping: " + p.getPing() + "ms");
    }
}
