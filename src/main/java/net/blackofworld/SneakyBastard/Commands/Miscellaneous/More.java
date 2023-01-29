package net.blackofworld.sneakybastard.Commands.Miscellaneous;

import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandCategory;
import net.blackofworld.sneakybastard.Command.CommandInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.regex.Pattern;

@CommandInfo(command = "more", description = "Gimme more items please", Syntax = "", category = CommandCategory.Miscellaneous)
public class More extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        final ItemStack stack = p.getInventory().getItemInMainHand();
        stack.setAmount(127);
        p.updateInventory();
        final String name = Pattern.compile("\\b(.)(.*?)\\b")
                .matcher(stack.getType().getKey().getKey().replaceAll("_", " "))
                .replaceAll(match -> match.group(1).toUpperCase() + match.group(2));
        p.Reply(ChatColor.GREEN + "You now have 127 " + name);
    }
}
