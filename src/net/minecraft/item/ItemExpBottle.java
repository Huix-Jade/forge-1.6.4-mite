package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Translator;
import net.minecraft.world.WorldServer;

public class ItemExpBottle extends Item {
   public static final int enchantment_levels_worth_of_experience = 2;

   public ItemExpBottle(int par1) {
      super(par1, Material.glass, "experience_bottle");
      this.setCreativeTab(CreativeTabs.tabMisc);
      this.setCraftingDifficultyAsComponent(25.0F);
   }

   public boolean hasEffect(ItemStack par1ItemStack) {
      return true;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.onServer()) {
         if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
         }

         WorldServer world = player.getWorldServer();
         world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         world.spawnEntityInWorld(new EntityExpBottle(world, player));
      }

      return true;
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add("");
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.XP", Enchantment.getExperienceCost(2)));
      }

   }
}
