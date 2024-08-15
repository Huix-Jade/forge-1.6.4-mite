package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.mite.Skill;
import net.minecraft.raycast.RaycastCollision;

public class ItemSeeds extends ItemFood {
   private int blockType;
   private int soilBlockID;

   public ItemSeeds(int id, int satiation, int nutrition, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients, int crop_block_id, int soil_block_id, String texture) {
      super(id, Material.seed, satiation, nutrition, has_protein, has_essential_fats, has_phytonutrients, texture);
      this.blockType = crop_block_id;
      this.soilBlockID = soil_block_id;
      this.setMaxStackSize(64);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMaterials);
      this.setPlantProduct();
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock()) {
         if (player.worldObj.areSkillsEnabled() && !player.hasSkill(Skill.FARMING)) {
            return false;
         } else if (rc.face_hit.isTop() && rc.getBlockHitID() == this.soilBlockID && rc.isNeighborAirBlock()) {
            return player.tryPlaceHeldItemAsBlock(rc, Block.getBlock(this.blockType));
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

   public float getCompostingValue() {
      return 0.0F;
   }
}
