package net.blackofworld.SneakyBastard.Command;

import net.blackofworld.SneakyBastard.Start;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public abstract class CommandBase {
    public final String Command = getInfo().command();
    public final String Description = getInfo().description();
    public final String Syntax = CommandManager.COMMAND_SIGN + Command + " " + getInfo().Syntax();
    public final int Category = getInfo().category();
    public final int requiredArgs = getInfo().requiredArgs();
    protected final WeakReference<Start> Plugin = new WeakReference<>(Start.Instance);

    public abstract void Execute(Player p, ArrayList<String> args);

    protected Start Instance() {
        return Plugin.get();
    }
    public String strCategory()          {
        return switch (this.Category) {
            case CommandCategory.Server -> "Server";
            case CommandCategory.Player -> "Player";
            case CommandCategory.Griefing -> "Griefing";
            case CommandCategory.Miscellaneous -> "Miscellaneous";
            default -> throw new RuntimeException("doo doo retard");
        };
    }
    @SuppressWarnings("unchecked")
    protected <T> T firstParamIsPlayer(Player p, ArrayList<String> args, boolean supportWildcard) {
        if (args.isEmpty())
            return supportWildcard ? (T) Collections.singleton(p) : (T) p;

        if (!supportWildcard) return (T) Bukkit.getPlayerExact(args.get(0));
        return args.get(0).equals("*") ?
                (T) Bukkit.getOnlinePlayers() :
                (T) Collections.singleton(Bukkit.getPlayerExact(args.get(0)));
    }

    private CommandInfo getInfo() {
        final var info = this.getClass().getAnnotation(CommandInfo.class);
        if (info == null) throw new RuntimeException("you goofed");
        return info;
    }
}

