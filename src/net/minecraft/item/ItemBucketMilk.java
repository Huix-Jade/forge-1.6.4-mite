package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class ItemBucketMilk extends ItemVessel {
   public ItemBucketMilk(int id, Material material) {
      super(id, material, Material.milk, 4, 8, 1, "buckets/" + material.name + "/milk");
      this.setCreativeTab(CreativeTabs.tabMisc);
      this.setFoodValue(0, 4, true, false, false);
      this.setAnimalProduct();
      this.setAlwaysEdible();
      this.setCraftingDifficultyAsComponent(100.0F);
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      if (player.onServer()) {
         player.curePotionEffects(item_stack);
         player.foodStats.addFoodValue(this);
      }

      super.onItemUseFinish(item_stack, world, player);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock() && rc.getNeighborOfBlockHit() == Block.fire) {
         if (player.onServer()) {
            rc.world.douseFire(rc.neighbor_block_x, rc.neighbor_block_y, rc.neighbor_block_z, (Entity)null);
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem(new ItemStack(this.getContainerItem()));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static ItemVessel getPeer(Material vessel_material, Material contents) {
      return ItemBucket.getPeer(vessel_material, contents);
   }

   public ItemVessel getPeerForContents(Material contents) {
      return getPeer(this.getVesselMaterial(), contents);
   }

   public ItemVessel getPeerForVesselMaterial(Material vessel_material) {
      return getPeer(vessel_material, this.getContents());
   }

   public float getCompostingValue() {
      return 0.0F;
   }
}
