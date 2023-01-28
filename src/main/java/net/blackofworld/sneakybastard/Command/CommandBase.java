package net.blackofworld.sneakybastard.Command;

import net.blackofworld.sneakybastard.Start;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class CommandBase {
    protected final WeakReference<Start> Plugin = new WeakReference<>(Start.Instance);
    public final String Command = getInfo().command();
    public final String Description = getInfo().description();
    public final String Syntax = CommandManager.COMMAND_SIGN + Command + " " + getInfo().Syntax();
    public final CommandCategory Category = getInfo().category();
    public final int requiredArgs = getInfo().requiredArgs();
    public abstract void Execute(Player p, ArrayList<String> args);
    protected Start Instance() {
        return Plugin.get();
    }
    private CommandInfo getInfo() {
        final var info = this.getClass().getAnnotation(CommandInfo.class);
        if(info == null) throw new RuntimeException("you goofed");
        return info;
    }
}
