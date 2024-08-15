package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.mite.MITEConstant;
import net.minecraft.stats.AchievementList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemEgg extends ItemFood {
   public ItemEgg(int par1) {
      super(par1, Material.meat, 1, 3, true, MITEConstant.egg_has_essential_fats, false, "egg");
      this.setMaxStackSize(16);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMaterials);
      this.setAnimalProduct();
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.onServer()) {
         if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
         }

         WorldServer world = player.getWorldServer();
         world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         world.spawnEntityInWorld(new EntityEgg(world, player));
      } else {
         player.bobItem();
      }

      return true;
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      player.triggerAchievement(AchievementList.eggs);
      super.onItemUseFinish(item_stack, world, player);
   }

   public boolean hasIngestionPriority(ItemStack item_stack, boolean ctrl_is_down) {
      return !ctrl_is_down;
   }

   public float getCompostingValue() {
      return 0.0F;
   }
}
