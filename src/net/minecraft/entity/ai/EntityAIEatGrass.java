package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockCropsDead;
import net.minecraft.block.BlockGrass;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIEatGrass extends EntityAIBase {
   private EntityLiving theEntity;
   private World theWorld;
   int eatGrassTick;

   public EntityAIEatGrass(EntityLiving par1EntityLiving) {
      this.theEntity = par1EntityLiving;
      this.theWorld = par1EntityLiving.worldObj;
      this.setMutexBits(7);
   }

   public boolean shouldExecute() {
      if (this.theEntity.hurtTime <= 0 && !this.theEntity.has_decided_to_flee) {
         if (this.theEntity.getRNG().nextInt(this.theEntity.isChild() ? 50 : 1000) != 0) {
            return false;
         } else {
            int var1 = MathHelper.floor_double(this.theEntity.posX);
            int var2 = this.theEntity.getBlockPosY();
            int var3 = MathHelper.floor_double(this.theEntity.posZ);
            Block block = Block.blocksList[this.theWorld.getBlockId(var1, var2, var3)];
            if (block == Block.tallGrass || block instanceof BlockCrops && !(block instanceof BlockCropsDead)) {
               return true;
            } else {
               block = Block.blocksList[this.theWorld.getBlockId(var1, var2 - 1, var3)];
               if (block == Block.grass && BlockGrass.getTramplingEffect(BlockGrass.getTramplings(this.theWorld.getBlockMetadata(var1, var2 - 1, var3))) == 0.0F) {
                  return true;
               } else {
                  List predators = this.theWorld.getPredatorsWithinAABBForEntity(this.theEntity, this.theEntity.boundingBox.expand(16.0, 2.0, 16.0));
                  return predators.isEmpty();
               }
            }
         }
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.eatGrassTick = 40;
      this.theWorld.setEntityState(this.theEntity, EnumEntityState.tnt_ignite_or_eating_grass);
      this.theEntity.getNavigator().clearPathEntity();
   }

   public void resetTask() {
      this.eatGrassTick = 0;
   }

   public boolean continueExecuting() {
      if (this.theEntity.hurtTime <= 0 && !this.theEntity.has_decided_to_flee) {
         return this.eatGrassTick > 0;
      } else {
         return false;
      }
   }

   public int getEatGrassTick() {
      return this.eatGrassTick;
   }

   public void updateTask() {
      this.eatGrassTick = Math.max(0, this.eatGrassTick - 1);
      if (this.eatGrassTick == 4) {
         int var1 = MathHelper.floor_double(this.theEntity.posX);
         int var2 = MathHelper.floor_double(this.theEntity.posY);
         int var3 = MathHelper.floor_double(this.theEntity.posZ);
         Block block = Block.blocksList[this.theWorld.getBlockId(var1, var2, var3)];
         if (block != Block.tallGrass && !(block instanceof BlockCrops)) {
            if (this.theWorld.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID) {
               this.theWorld.playAuxSFX(2001, var1, var2 - 1, var3, Block.grass.blockID);
               this.theWorld.setBlock(var1, var2 - 1, var3, Block.dirt.blockID, 0, 2);
               this.theEntity.eatGrassBonus();
            }
         } else {
            this.theWorld.destroyBlock((new BlockBreakInfo(this.theWorld, var1, var2, var3)).setEatenBy(this.theEntity), false);
            this.theEntity.eatGrassBonus();
         }
      }

   }
}
