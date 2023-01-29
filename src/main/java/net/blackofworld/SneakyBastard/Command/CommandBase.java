package net.blackofworld.SneakyBastard.Command;

import net.blackofworld.SneakyBastard.Start;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class CommandBase {
    public final String Command = getInfo().command();
    public final String Description = getInfo().description();
    public final String Syntax = CommandManager.COMMAND_SIGN + Command + " " + getInfo().Syntax();
    public final CommandCategory Category = getInfo().category();
    public final int requiredArgs = getInfo().requiredArgs();
    protected final WeakReference<Start> Plugin = new WeakReference<>(Start.Instance);

    public abstract void Execute(Player p, ArrayList<String> args);

    protected Start Instance() {
        return Plugin.get();
    }

    private CommandInfo getInfo() {
        final var info = this.getClass().getAnnotation(CommandInfo.class);
        if (info == null) throw new RuntimeException("you goofed");
        return info;
    }
}

