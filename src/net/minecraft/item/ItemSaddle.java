package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item {
   public ItemSaddle(int par1) {
      super(par1, Material.leather, "saddle");
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntityPig) {
         EntityPig pig = (EntityPig)entity;
         if (!pig.getSaddled() && !pig.isChild()) {
            if (player.onServer()) {
               pig.setSaddled(true);
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }

            return true;
         }
      }

      return false;
   }
}
