package net.blackofworld.sneakybastard;

import net.blackofworld.sneakybastard.Command.CommandManager;
import net.blackofworld.sneakybastard.Listeners.SneakyListener;
import net.blackofworld.sneakybastard.Utils.BukkitReflection;
import net.blackofworld.sneakybastard.Utils.Packets.PacketInjector;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Start extends JavaPlugin {
    public static Start Instance = null;
    public static Logger LOGGER;
    public static CommandManager cm;
    private final PluginDescriptionFile pdfFile = this.getDescription();
    PacketInjector injector;
    private boolean isReload;

    private void onPostWorldLoad() {
        cm = new CommandManager();
        injector = new PacketInjector();
        CommandManager.Instance = cm;
        //packetInjector = new PacketInjector();
        Bukkit.getPluginManager().registerEvents(new SneakyListener(), this);
        String loadString = "--| " + pdfFile.getName() + " (version " + pdfFile.getVersion() + ") loaded |--";
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.repeat("-", loadString.length()));
        Bukkit.getConsoleSender().sendMessage("§3" + loadString);
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.repeat("-", loadString.length()));
    }

    private void onStartup() {
        try {
            BukkitReflection.changeCommandBlockStatus(true);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }

        if (isReload) {
        }
        // Do every hooking here
    }

    @Override
    public void onEnable() {
        Instance = this;
        LOGGER = Instance.getLogger();
        isReload = Bukkit.getWorlds().size() != 0;
        onStartup();
        Bukkit.getScheduler().runTask(this, this::onPostWorldLoad);
    }

    @Override
    public void onDisable() {
        String unloadString = "--| " + pdfFile.getName() + " (version " + pdfFile.getVersion() + ") unloaded |--";
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.repeat("-", unloadString.length()));
        Bukkit.getConsoleSender().sendMessage("§3" + unloadString);
        Bukkit.getConsoleSender().sendMessage("§2" + StringUtils.repeat("-", unloadString.length()));
        cm.Destroy();
    }
}
