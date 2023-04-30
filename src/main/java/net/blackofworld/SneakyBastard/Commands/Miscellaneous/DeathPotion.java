package net.blackofworld.SneakyBastard.Commands.Miscellaneous;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

@CommandInfo(command = "deathpotion", category = CommandCategory.Miscellaneous, description = "Kill people in survival and in creative too!", syntax = "")
@ExtensionMethod({Player.class, PlayerExt.class})
public class DeathPotion extends CommandBase
{
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
        PotionEffect heal = new PotionEffect(PotionEffectType.HEAL, 2000, 125);
        PotionEffect harm = new PotionEffect(PotionEffectType.HARM, 2000, 125);
        potionmeta.addCustomEffect(heal, true);
        potionmeta.addCustomEffect(harm, true);
        potionmeta.setDisplayName("§6Splash Potion of §4§lDEATH");
        potion.setItemMeta(potionmeta);
        p.getInventory().addItem(potion);
        p.getInventory().setItemInMainHand(potion);
    }
}
