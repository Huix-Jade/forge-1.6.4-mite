package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

public class ItemEnderPearl extends Item {
   public ItemEnderPearl(int par1) {
      super(par1, Material.ender_pearl, "ender_pearl");
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.tabMisc);
      this.setCraftingDifficultyAsComponent(100.0F);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.onClient()) {
         player.bobItem();
      } else {
         if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
         }

         WorldServer world = player.getWorldServer();
         world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         world.spawnEntityInWorld(new EntityEnderPearl(world, player));
      }

      return true;
   }
}
