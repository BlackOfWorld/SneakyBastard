package net.blackofworld.SneakyBastard.Utils.Packets;

import io.netty.channel.Channel;
import net.blackofworld.SneakyBastard.Utils.BukkitReflection;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInject.ChannelListener;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PacketPlayer {
    private static final String CHANNEL_NAME = RandomStringUtils.randomAlphabetic(24);
    private final UUID UUID;
    private final Channel channel;

    private final ChannelListener channelListener;
    private boolean hooked;

    PacketPlayer(Player player) {
        this.channel = BukkitReflection.getServerPlayer(player).connection.connection.channel;
        this.channelListener = new ChannelListener(this);
        this.UUID = player.getUniqueId();
    }

    public void hook() {
        if (hooked || Bukkit.getPlayer(UUID) == null) return;
        this.channel.pipeline().addBefore("packet_handler", CHANNEL_NAME, channelListener);
        hooked = true;
    }

    public void unhook() {
        if (!hooked || Bukkit.getPlayer(UUID) == null) return;
        this.channel.eventLoop().submit(() -> { this.channel.pipeline().remove(CHANNEL_NAME);});
        hooked = true;
    }
}
