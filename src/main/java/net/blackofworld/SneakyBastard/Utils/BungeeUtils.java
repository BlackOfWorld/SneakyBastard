package net.blackofworld.SneakyBastard.Utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import net.blackofworld.SneakyBastard.Start;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.spigotmc.SpigotConfig;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;
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
        proxyEnabled = isProxyEnabled(false);
        if (proxyEnabled) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(Start.Instance, "BungeeCord");
            Bukkit.getMessenger().registerIncomingPluginChannel(Start.Instance, "BungeeCord", listener);
        }
    }

    public static void dummy() {
    }

    @SneakyThrows()
    public static Boolean isPlayerOnline(final String playerName) {
        if(proxyEnabled) return null;
        ExecutorService service = Executors.newFixedThreadPool(1);

        Future<Boolean> result = service.submit(() -> {
            CompletableFuture<Boolean> playerOnline = new CompletableFuture<>();
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF("ALL");
            getServer().sendPluginMessage(Start.Instance, "BungeeCord", out.toByteArray());
            var queue = tasks.get().getOrDefault("PlayerList", new ArrayDeque<>());
            queue.add((s, player, message) -> {
                ByteArrayDataInput in = ByteStreams.newDataInput(message);
                if (in.readUTF().equals("") || in.readUTF().equals("")) {
                    return;
                }
                String pll = in.readUTF();
                getLogger().log(Level.SEVERE, pll);
                playerOnline.complete(Arrays.asList(pll.split(", ")).contains(playerName));
            });
            tasks.get().put("PlayerList", queue);
            return playerOnline.join();
        });
        try {
            return result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean isProxyEnabled(boolean extended) {
        if (extended) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF("ALL");
            getServer().sendPluginMessage(Start.Instance, "BungeeCord", out.toByteArray());

        }
        // Start.Instance.getConfig()
        return SpigotConfig.bungee && !getServer().getOnlineMode();
    }
}
