package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   private static Vec3 staticVector = Vec3.createVectorHelper(0.0, 0.0, 0.0);

   public static Vec3 findRandomTarget(EntityCreature par0EntityCreature, int par1, int par2) {
      return findRandomTargetBlock(par0EntityCreature, par1, par2, (Vec3)null);
   }

   public static Vec3 findRandomTargetBlockTowards(EntityCreature par0EntityCreature, int par1, int par2, Vec3 par3Vec3) {
      staticVector.xCoord = par3Vec3.xCoord - par0EntityCreature.posX;
      staticVector.yCoord = par3Vec3.yCoord - par0EntityCreature.posY;
      staticVector.zCoord = par3Vec3.zCoord - par0EntityCreature.posZ;
      return findRandomTargetBlock(par0EntityCreature, par1, par2, staticVector);
   }

   public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature par0EntityCreature, int par1, int par2, Vec3 par3Vec3) {
      staticVector.xCoord = par0EntityCreature.posX - par3Vec3.xCoord;
      staticVector.yCoord = par0EntityCreature.posY - par3Vec3.yCoord;
      staticVector.zCoord = par0EntityCreature.posZ - par3Vec3.zCoord;
      return findRandomTargetBlock(par0EntityCreature, par1, par2, staticVector);
   }

   private static Vec3 findRandomTargetBlock(EntityCreature creature, int horizontal_range, int vertical_range, Vec3 direction) {
      boolean intensive_search = false;
      byte attempts;
      if (intensive_search) {
         attempts = 64;
         horizontal_range *= 2;
      } else {
         attempts = 10;
      }

      Random var4 = creature.getRNG();
      int creature_x = MathHelper.floor_double(creature.posX);
      int creature_y = MathHelper.floor_double(creature.posY);
      int creature_z = MathHelper.floor_double(creature.posZ);
      boolean var10;
      if (creature.hasHome()) {
         double var11 = (double)(creature.getHomePosition().getDistanceSquared(creature_x, creature_y, creature_z) + 4.0F);
         double var13 = (double)(creature.func_110174_bM() + (float)horizontal_range);
         var10 = var11 < var13 * var13;
      } else {
         var10 = false;
      }

      int selected_block_x = creature_x;
      int selected_block_y = creature_y;
      int selected_block_z = creature_z;
      float heaviest_weight = -100.0F;
      PathEntity path_entity = null;

      for(int var16 = 0; var16 < attempts; ++var16) {
         int x = var4.nextInt(2 * horizontal_range + 1) - horizontal_range;
         int y = var4.nextInt(2 * vertical_range + 1) - vertical_range;
         int z = var4.nextInt(2 * horizontal_range + 1) - horizontal_range;
         if (direction == null || (double)x * direction.xCoord + (double)z * direction.zCoord >= 0.0) {
            x += creature_x;
            y += creature_y;
            z += creature_z;
            if (!var10 || creature.func_110176_b(x, y, z)) {
               float weight = creature.getBlockPathWeight(x, y, z);
               if (weight > heaviest_weight) {
                  if (intensive_search) {
                     path_entity = creature.worldObj.getEntityPathToXYZ(creature, x, y, z, 16.0F, true, false, false, true);
                  }

                  if (!intensive_search || path_entity != null) {
                     heaviest_weight = weight;
                     selected_block_x = x;
                     selected_block_y = y;
                     selected_block_z = z;
                  }
               }
            }
         }
      }

      if (heaviest_weight > -100.0F) {
         return creature.worldObj.getWorldVec3Pool().getVecFromPool((double)selected_block_x, (double)selected_block_y, (double)selected_block_z);
      } else {
         return null;
      }
   }
}
