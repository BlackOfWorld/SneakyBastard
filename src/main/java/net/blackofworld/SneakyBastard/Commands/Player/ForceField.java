package net.blackofworld.SneakyBastard.Commands.Player;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Utils.BukkitReflection;
import net.blackofworld.SneakyBastard.Utils.Events.TickEvent;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandInfo(command = "ff", description = "Bruh, how are you hitting me that far away?", Syntax = "<on/off/hostile/friendly/players/range>", category = CommandCategory.Player, requiredArgs = 1)
@ExtensionMethod({Player.class, PlayerExt.class})
public class ForceField extends CommandBase {
    private final HashMap<UUID, forceField> players = new HashMap<>();
    private int tick = 0;

    @Override
    public void Execute(Player p, ArrayList<String> args) {

        Object index = players.get(p.getUniqueId());
        switch (args.get(0)) {
            case "on" -> {
                if (index == null) {
                    players.put(p.getUniqueId(), new forceField(p.getUniqueId()));
                    p.Reply(ChatColor.GREEN + "Successfully turned on FF!");
                } else {
                    p.Reply(ChatColor.RED + "You already have FF on!");
                }
            }
            case "off" -> {
                if (index == null) {
                    p.Reply(ChatColor.RED + "You already have FF off!");
                    break;
                }
                players.remove(p.getUniqueId());
                p.Reply(ChatColor.GREEN + "Successfully turned off FF!");
            }
            case "hostile" -> {
                if (index == null) {
                    p.Reply(ChatColor.RED + "You must have FF on to access this setting!");
                    break;
                }
                forceField ff = (forceField) index;
                ff.hitHostileMobs = !ff.hitHostileMobs;
                p.Reply(ChatColor.RED + "Hit hostile mobs: " + (ff.hitHostileMobs ? "On" : "Off"));
                players.replace(p.getUniqueId(), ff);
            }
            case "friendly" -> {
                if (index == null) {
                    p.Reply(ChatColor.RED + "You must have FF on to access this setting!");
                    break;
                }
                forceField ff = (forceField) index;
                ff.hitFriendlyMobs = !ff.hitFriendlyMobs;
                p.Reply(ChatColor.RED + "Hit friendly mobs: " + (ff.hitFriendlyMobs ? "On" : "Off"));
                players.replace(p.getUniqueId(), ff);
            }
            case "players" -> {
                if (index == null) {
                    p.Reply(ChatColor.RED + "You must have FF on to access this setting!");
                    break;
                }
                forceField ff = (forceField) index;
                ff.hitPlayers = !ff.hitPlayers;
                p.Reply(ChatColor.RED + "Hit players: " + (ff.hitPlayers ? "On" : "Off"));
                players.replace(p.getUniqueId(), ff);
            }
            case "range" -> {
                if (args.size() < 2) {
                    p.Reply(ChatColor.RED + "Not enough arguments!");
                    break;
                }
                if (index == null) {
                    p.Reply(ChatColor.RED + "You must have FF on to access this setting!");
                    break;
                }
                forceField ff = (forceField) index;
                ff.range = Double.parseDouble(args.get(1));
                p.Reply(ChatColor.RED + "Range: " + ff.range);
                players.replace(p.getUniqueId(), ff);
            }
        }
    }

    public void onTick(TickEvent e) {
        if (tick++ < 5) return;
        tick = 0;
        for (Map.Entry<UUID, forceField> fe : players.entrySet()) {
            Player p = Bukkit.getPlayer(fe.getKey());
            if (p == null || !p.isOnline() || p.getGameMode() == GameMode.SPECTATOR) continue;
            forceField ff = fe.getValue();
            for (Entity ps : p.getNearbyEntities(ff.range, ff.range, ff.range))
                hitEntityCheck(p, ps, ff.hitPlayers, ff.hitHostileMobs, ff.hitFriendlyMobs);
        }
    }

    private void hitEntityCheck(Player p, Entity e, boolean damagePlayer, boolean hitHostileMobs, boolean hitFriendlyMobs) {
        if (!(e instanceof Monster || e instanceof Flying || e instanceof Ageable ||
                e instanceof WaterMob || e instanceof HumanEntity || e instanceof Ambient ||
                e instanceof Boss))
            return;
        if (e.isDead()) return;
        if (hitFriendlyMobs && (e instanceof Ageable || e instanceof WaterMob || e instanceof Ambient)) {
            hitEntity(p, e);
        }
        if (hitHostileMobs && (e instanceof Monster || e instanceof Flying || e instanceof Boss)) {
            hitEntity(p, e instanceof ComplexLivingEntity ? ((ComplexEntityPart) ((ComplexLivingEntity) e).getParts().toArray()[0]) : e);
        }
        if (damagePlayer && e instanceof HumanEntity) {
            hitEntity(p, e);
        }
    }

    private void hitEntity(Player p, Entity e) {
        try {
            ServerPlayer pl = BukkitReflection.getServerPlayer(p);
            pl.attack(BukkitReflection.getEntity(e));
            ClientboundAnimatePacket packet = new ClientboundAnimatePacket(pl, 0);
            p.SendPacket(packet);
        } catch (Exception ignored) {
        }
    }

    static final class forceField {
        UUID player;
        double range = 6.0d;
        boolean hitPlayers = true;
        boolean hitHostileMobs = false;
        boolean hitFriendlyMobs = false;

        forceField(UUID player) {
            this.player = player;
        }
    }
}
