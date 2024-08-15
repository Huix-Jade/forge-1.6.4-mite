package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.world.WorldServer;

public class ItemSnowball extends Item {
   public ItemSnowball(int id) {
      super(id, Material.snow, "snowball");
      this.setMaxStackSize(16);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
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
         world.spawnEntityInWorld(new EntitySnowball(world, player));
      }

      return true;
   }
}
