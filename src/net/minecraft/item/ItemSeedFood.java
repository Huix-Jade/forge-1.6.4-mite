package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.mite.Skill;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class ItemSeedFood extends ItemFood implements IPlantable {
   private int cropId;
   private int soilId;

   public ItemSeedFood(int id, int satiation, int nutrition, boolean has_protein, boolean has_phytonutrients, int crop_block_id, int soil_block_id, String texture) {
      super(id, Material.seed, satiation, nutrition, has_protein, false, has_phytonutrients, texture);
      this.cropId = crop_block_id;
      this.soilId = soil_block_id;
      this.setPlantProduct();
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock()) {
         if (player.worldObj.areSkillsEnabled() && !player.hasSkill(Skill.FARMING)) {
            return false;
         } else if (rc.face_hit.isTop() && rc.getBlockHitID() == this.soilId && rc.isNeighborAirBlock()) {
            return player.tryPlaceHeldItemAsBlock(rc, Block.getBlock(this.cropId));
         } else if (rc.getBlockHit() instanceof BlockCrops || rc.getBlockHit() instanceof BlockStem || rc.face_hit.isTop() && rc.getBlockHit() instanceof BlockFarmland) {
            player.cancelRightClick();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean hasIngestionPriority(ItemStack item_stack, boolean ctrl_is_down) {
      return false;
   }

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z)
   {
      return EnumPlantType.Crop;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z)
   {
      return cropId;
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z)
   {
      return 0;
   }
}
