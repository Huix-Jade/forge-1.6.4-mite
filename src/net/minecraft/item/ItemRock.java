package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Translator;

public class ItemRock extends Item {
   protected ItemRock(int id, Material material, String texture) {
      super(id, material, texture);
      this.setMaxStackSize(32);
      this.setCraftingDifficultyAsComponent(getCraftingDifficultyAsComponent(material));
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   public static float getCraftingDifficultyAsComponent(Material material) {
      return material.durability * (float)(material == Material.quartz ? 225 : 100);
   }

   public static int getExperienceValueWhenSacrificed(ItemStack item_stack) {
      Item item = item_stack.getItem();
      if (item == Item.dyePowder && item_stack.getItemSubtype() == 4) {
         return 25;
      } else {
         return item == netherQuartz ? 50 : (item == emerald ? 250 : (item == diamond ? 500 : 0));
      }
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      return onItemRightClick(player, player.getHeldItemStack(), partial_tick, ctrl_is_down);
   }

   public static boolean onItemRightClick(EntityPlayer player, ItemStack item_stack, float partial_tick, boolean ctrl_is_down) {
      int xp_value = getExperienceValueWhenSacrificed(item_stack);
      if (xp_value < 1) {
         return false;
      } else {
         if (player.onServer()) {
            player.causeBreakingItemEffect(item_stack.getItem(), player.inventory.currentItem);
            player.convertOneOfHeldItem((ItemStack)null);
            player.addExperience(xp_value);
         } else {
            player.bobItem();
         }

         return true;
      }
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      int xp_value = getExperienceValueWhenSacrificed(item_stack);
      if (extended_info && xp_value > 0) {
         info.add(EnumChatFormatting.LIGHT_GRAY + Translator.getFormatted("item.tooltip.XPEach", xp_value));
      }

   }
}
