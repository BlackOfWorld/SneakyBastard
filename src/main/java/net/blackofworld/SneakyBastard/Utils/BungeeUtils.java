package net.blackofworld.SneakyBastard.Utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blackofworld.SneakyBastard.Start;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.spigotmc.SpigotConfig;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.bukkit.Bukkit.getServer;


public class BungeeUtils {
    public static final boolean proxyEnabled;
    static final AtomicReference<LinkedHashMap<String, ArrayDeque<PluginMessageListener>>> tasks = new AtomicReference<>(new LinkedHashMap<>());
    static final PluginMessageListener listener = (channel, player, message) -> {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        PluginMessageListener method;
        String subChannel;
        try {
            if (tasks.get().isEmpty()) return;
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            subChannel = in.readUTF();
            method = tasks.get().remove(subChannel).pop();
            method.onPluginMessageReceived(channel, player, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    static {
        proxyEnabled = isProxyEnabled();
        if (proxyEnabled) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(Start.Instance, "BungeeCord");
            Bukkit.getMessenger().registerIncomingPluginChannel(Start.Instance, "BungeeCord", listener);
        }
    }

    public static void dummy() {
    }

    public static Boolean kickPlayer(final String playerName, final String message) {
        try {
            if (!proxyEnabled) return null;
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("KickPlayer");
            out.writeUTF(playerName);
            out.writeUTF(message);
            getServer().sendPluginMessage(Start.Instance, "BungeeCord", out.toByteArray());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPlayerOnline(final String playerName) {
        if (!proxyEnabled) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        getServer().sendPluginMessage(Start.Instance, "BungeeCord", out.toByteArray());
        ArrayDeque<PluginMessageListener> queue = tasks.get().computeIfAbsent("PlayerList", k -> new ArrayDeque<>());

        AtomicBoolean playerOnline = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        queue.add((s, player, message) -> {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            if (in.readUTF().equals("") && in.readUTF().equals("")) {
                return;
            }
            in.readUTF(); // wtf?
            String playerList = in.readUTF();

            playerOnline.set((Arrays.asList(playerList.split(", ")).contains(playerName)));
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
        return playerOnline.get();
    }

    private static boolean isProxyEnabled() {
        // Start.Instance.getConfig()
        return SpigotConfig.bungee && !getServer().getOnlineMode();
    }
}
