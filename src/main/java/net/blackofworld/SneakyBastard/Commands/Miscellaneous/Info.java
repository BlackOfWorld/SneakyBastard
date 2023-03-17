package net.blackofworld.SneakyBastard.Commands.Miscellaneous;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;


@CommandInfo(command = "info", category = CommandCategory.Miscellaneous, description = "Tells you stuff about yourself or another player", Syntax = "[player]")
@ExtensionMethod({Player.class, PlayerExt.class})

public class Info extends CommandBase {

    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Player pa = firstParamIsPlayer(p, args);
        if(pa == null) pa = p;

        p.Reply("Name: " + pa.getName());
        p.Reply("UUID: " + pa.getUniqueId());
        p.Reply("Health: " + pa.getHealth());
        p.Reply("Health scale: " + pa.getHealthScale());
        p.Reply("Invulnerable: " + pa.isInvulnerable());
        p.Reply("Locale: " + pa.getLocale());
        p.Reply("Op: " + pa.isOp());
        p.Reply("Dead: " + pa.isDead());
        p.Reply("Gravity: " + pa.hasGravity());
        p.Reply("Location: %s | %.2f | %.2f %.2f".formatted(pa.getWorld().getName(),pa.getLocation().getX(),
                pa.getLocation().getY(), pa.getLocation().getZ()));
        p.Reply("Walk speed: " + pa.getWalkSpeed());
        p.Reply("Fly speed: " + pa.getFlySpeed());

        p.Reply("IP: %s".formatted(Objects.requireNonNull(pa.getAddress()).isUnresolved() ? pa.getAddress().getHostName() + ChatColor.RED + "(UNRESOLVED)" + ChatColor.RESET : pa.getAddress().getAddress().getHostAddress()));
        p.Reply("Ping: %dms".formatted(pa.getPing()));
    }
}
