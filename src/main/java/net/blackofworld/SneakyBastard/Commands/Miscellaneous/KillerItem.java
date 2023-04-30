package net.blackofworld.SneakyBastard.Commands.Miscellaneous;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.ArrayList;
import java.util.Collections;

@CommandInfo(command = "killeritem", category = CommandCategory.Miscellaneous, description = "Tells you stuff about yourself or another player", syntax = "")
@ExtensionMethod({Player.class, PlayerExt.class})
public class KillerItem extends CommandBase
{
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        ItemStack is = p.getInventory().getItemInMainHand();
        ItemMeta im = is.getItemMeta();
        // Display
        im.setLore(Collections.singletonList(String.valueOf(ChatColor.RED) + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.UNDERLINE + "Killer"));

        im.setUnbreakable(true);

        // Repair cost
        if(im instanceof Repairable) ((Repairable) im).setRepairCost(0);
        // Hide flags
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Enchantments
        im.addEnchant(Enchantment.DAMAGE_ALL, 255, true);
        im.addEnchant(Enchantment.DAMAGE_UNDEAD, 255, true);
        im.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 255, true);
        im.addEnchant(Enchantment.SWEEPING_EDGE, 255, true);
        im.addEnchant(Enchantment.DURABILITY, 255, true);
        im.addEnchant(Enchantment.MENDING, 255, true);
        im.addEnchant(Enchantment.VANISHING_CURSE, 255, true);

        // Attributes
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", Double.MAX_VALUE, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", Double.MAX_VALUE, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        is.setItemMeta(im);
        p.getInventory().setItemInMainHand(is);
    }
}
