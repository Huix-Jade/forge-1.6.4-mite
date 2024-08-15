package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Translator;
import net.minecraft.world.World;

public class ItemCoin extends Item {
   public ItemCoin(int id, Material material) {
      super(id, material, "coins/" + material.name);
      this.setMaxStackSize(64);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public int getExperienceValue() {
      Material material = this.getExclusiveMaterial();
      if (material == Material.ancient_metal) {
         return 500;
      } else if (material == Material.mithril) {
         return 2500;
      } else if (material == Material.adamantium) {
         return 10000;
      } else {
         return material == Material.copper ? 5 : (material == Material.silver ? 25 : (material == Material.gold ? 100 : 0));
      }
   }

   public static ItemCoin getForMaterial(Material material) {
      if (material == Material.ancient_metal) {
         return coinAncientMetal;
      } else if (material == Material.mithril) {
         return coinMithril;
      } else if (material == Material.adamantium) {
         return coinAdamantium;
      } else {
         return material == Material.copper ? coinCopper : (material == Material.silver ? coinSilver : (material == Material.gold ? coinGold : null));
      }
   }

   public Item getNuggetPeer() {
      Material material = this.getExclusiveMaterial();
      if (material == Material.ancient_metal) {
         return ancientMetalNugget;
      } else if (material == Material.mithril) {
         return mithrilNugget;
      } else if (material == Material.adamantium) {
         return adamantiumNugget;
      } else {
         return material == Material.copper ? Item.copperNugget : (material == Material.silver ? Item.silverNugget : (material == Material.gold ? Item.goldNugget : null));
      }
   }

   public void onCreated(ItemStack item_stack, World world, EntityPlayer player) {
      player.addExperience(-this.getExperienceValue() * item_stack.stackSize, false, true);
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add(EnumChatFormatting.LIGHT_GRAY + Translator.getFormatted("item.tooltip.XPEach", this.getExperienceValue()));
      }

   }
}
