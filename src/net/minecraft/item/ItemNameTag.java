package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item {
   public ItemNameTag(int par1) {
      super(par1, Material.leather, "name_tag");
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (!item_stack.hasDisplayName()) {
         return false;
      } else if (entity instanceof EntityLiving) {
         if (player.onServer()) {
            EntityLiving entity_living = entity.getAsEntityLiving();
            entity_living.setCustomNameTag(item_stack.getDisplayName());
            entity_living.func_110163_bv();
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
