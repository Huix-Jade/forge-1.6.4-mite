package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase {
   private EntityCreature theCreature;
   private double shelterX;
   private double shelterY;
   private double shelterZ;
   private double movementSpeed;
   private World theWorld;

   public EntityAIFleeSun(EntityCreature par1EntityCreature, double par2) {
      this.theCreature = par1EntityCreature;
      this.movementSpeed = par2;
      this.theWorld = par1EntityCreature.worldObj;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.theWorld.isDaytime()) {
         return false;
      } else if (!this.theCreature.isBurning()) {
         return false;
      } else if (!this.theWorld.canBlockSeeTheSky(MathHelper.floor_double(this.theCreature.posX), (int)this.theCreature.boundingBox.minY, MathHelper.floor_double(this.theCreature.posZ))) {
         return false;
      } else if (this.theCreature instanceof EntitySkeleton && !((EntitySkeleton)this.theCreature).avoidsSunlight()) {
         return false;
      } else {
         Vec3 var1 = this.findPossibleShelter();
         if (var1 == null) {
            return false;
         } else {
            this.shelterX = var1.xCoord;
            this.shelterY = var1.yCoord;
            this.shelterZ = var1.zCoord;
            return true;
         }
      }
   }

   public boolean continueExecuting() {
      return !this.theCreature.getNavigator().noPath();
   }

   public void startExecuting() {
      this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
   }

   private Vec3 findPossibleShelter() {
      Random random = this.theCreature.getRNG();
      int origin_x = this.theCreature.getBlockPosX();
      int origin_y = this.theCreature.getFootBlockPosY();
      int origin_z = this.theCreature.getBlockPosZ();

      for(int attempt = 0; attempt < 10; ++attempt) {
         int dx = random.nextInt(3 + attempt * 2) - (1 + attempt);
         int dy = random.nextInt(7) - 3;
         int dz = random.nextInt(3 + attempt * 2) - (1 + attempt);
         int x = origin_x + dx;
         int y = origin_y + dy;
         int z = origin_z + dz;
         Block block = this.theWorld.getBlock(x, y, z);
         if (block != null) {
            if (block.isAlwaysSolidStandardFormCube()) {
               continue;
            }

            if (block.blockMaterial == Material.water && this.theWorld.isAirOrPassableBlock(x, y + 1, z, true)) {
               return this.theWorld.getWorldVec3Pool().getVecFromPool((double)x, (double)y, (double)z);
            }

            if (block.blockMaterial == Material.lava && this.theCreature.isHarmedByLava() || block.blockMaterial == Material.fire && this.theCreature.isHarmedByFire()) {
               continue;
            }
         }

         if ((block == null || block.isNeverSolid() || this.theWorld.isAirOrPassableBlock(x, y, z, false)) && !this.theWorld.canBlockSeeTheSky(x, y + 1, z)) {
            Material material_below = this.theWorld.getBlockMaterial(x, y - 1, z);
            if ((material_below != Material.lava || !this.theCreature.isHarmedByLava()) && (material_below != Material.fire || !this.theCreature.isHarmedByFire())) {
               if (this.theCreature.height <= 1.0F) {
                  return this.theWorld.getWorldVec3Pool().getVecFromPool((double)x, (double)y, (double)z);
               }

               block = this.theWorld.getBlock(x, y + 1, z);
               if (block == null) {
                  return this.theWorld.getWorldVec3Pool().getVecFromPool((double)x, (double)y, (double)z);
               }

               if (!block.isAlwaysSolidStandardFormCube() && (block.isNeverSolid() || this.theWorld.isAirOrPassableBlock(x, y + 1, z, false))) {
                  return this.theWorld.getWorldVec3Pool().getVecFromPool((double)x, (double)y, (double)z);
               }
            }
         }
      }

      return null;
   }
}
