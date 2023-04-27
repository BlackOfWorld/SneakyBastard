package net.blackofworld.SneakyBastard.Commands.Miscellaneous;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

@CommandInfo(command = "enchant", description = "Enchant your shit pepega", category = CommandCategory.Miscellaneous)
@ExtensionMethod({Player.class, PlayerExt.class})
public class Enchant extends CommandBase {
    private void addEnchantment(final Player p, ItemStack stack, final Enchantment enchantment, final int level) {
        if (enchantment == null) {
            p.Reply(ChatColor.RED + "Enchantment cannot be null!");
            return;
        }
        try {
            if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                if (level == 0) meta.removeStoredEnchant(enchantment);
                else meta.addStoredEnchant(enchantment, level, true);
                stack.setItemMeta(meta);
            } else {
                if (level == 0) stack.removeEnchantment(enchantment);
                else stack.addUnsafeEnchantment(enchantment, level);
            }
        } catch (Exception ex) {
            p.sendException(ex);
        }
    }

    @Override
    public void Execute(Player p, ArrayList<String> args) {

        ItemStack stack = p.getInventory().getItemInMainHand();
        ItemMeta metaStack = stack.getItemMeta();
        short level;
        try {
            level = Short.parseShort(args.get(1));
            if(level < 0 || level > 255) throw new ArithmeticException();
        } catch (Exception e) {
            p.Reply(ChatColor.RED + "Level must be a number in range 0 - 255!");
            return;
        }

        Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args.get(0).toLowerCase()));
        if (enchantment == null) {
            //noinspection deprecation
            enchantment = Enchantment.getByName(args.get(0).toUpperCase());
        }
        if (enchantment == null) {
            p.Reply(ChatColor.RED + "That enchantment doesn't exist!");
            return;
        }
        addEnchantment(p, stack, enchantment, level);
        p.getInventory().setItemInMainHand(stack);
        p.updateInventory();
        p.Reply(ChatColor.GREEN + (level == 0 ? "Enchantment removed!" : "Enchantment added!"));
    }
}
