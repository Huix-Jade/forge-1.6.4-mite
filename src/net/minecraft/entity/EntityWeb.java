package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWeb extends EntityThrowable {
   public EntityWeb(World world) {
      super(world);
   }

   public EntityWeb(World world, EntityLivingBase thrower) {
      super(world, thrower);
   }

   public EntityWeb(World world, double pos_x, double pos_y, double pos_z) {
      super(world, pos_x, pos_y, pos_z);
   }

   public static boolean onImpact(EntityLivingBase elb_hit, boolean burning) {
      if (burning) {
         elb_hit.setFire(5);
      }

      World worldObj = elb_hit.worldObj;
      int x = elb_hit.getBlockPosX();
      int y = elb_hit.getFootBlockPosY();
      int z = elb_hit.getBlockPosZ();
      int lead = 4;
      int predicted_x = MathHelper.floor_double(elb_hit.getPredictedPosX((float)lead));
      int predicted_z = MathHelper.floor_double(elb_hit.getPredictedPosZ((float)lead));
      if ((predicted_x != x || predicted_z != z) && setBlockToWebIfEmpty(worldObj, predicted_x, y, predicted_z, burning)) {
         return true;
      } else if (setBlockToWebIfEmpty(worldObj, x, y, z, burning)) {
         return true;
      } else {
         int min_x = elb_hit.boundingBox.getBlockCoordForMinX();
         int min_y = elb_hit.boundingBox.getBlockCoordForMinY();
         int min_z = elb_hit.boundingBox.getBlockCoordForMinZ();
         int max_x = elb_hit.boundingBox.getBlockCoordForMaxX();
         int max_y = elb_hit.boundingBox.getBlockCoordForMaxY();
         int max_z = elb_hit.boundingBox.getBlockCoordForMaxZ();

         for(y = min_y; y <= max_y; ++y) {
            for(x = min_x; x <= max_x; ++x) {
               for(z = min_z; z <= max_z; ++z) {
                  if (setBlockToWebIfEmpty(worldObj, x, y, z, burning)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   protected void onImpact(RaycastCollision rc) {
      if (rc.isEntity()) {
         Entity entity_hit = rc.getEntityHit();
         if (entity_hit.isEntityLivingBase() && this.onServer() && onImpact(entity_hit.getAsEntityLivingBase(), this.isBurning())) {
            this.setDead();
         }
      } else if (rc.getNeighborOfBlockHit() != Block.fire && rc.getBlockHitMaterial() != Material.lava && rc.getNeighborOfBlockHitMaterial() != Material.lava) {
         if (!rc.isLiquidBlock() && rc.getNeighborOfBlockHitMaterial() != Material.water) {
            if (this.onServer()) {
               int x = rc.block_hit_x;
               int y = rc.block_hit_y;
               int z = rc.block_hit_z;
               if (!canReplaceBlockAt(this.worldObj, x, y, z)) {
                  x = rc.neighbor_block_x;
                  y = rc.neighbor_block_y;
                  z = rc.neighbor_block_z;
               }

               if (!setBlockToWebIfEmpty(rc.world, x, y, z, this.isBurning())) {
                  setBlockToWebIfEmpty(rc.world, x, y + 1, z, this.isBurning());
               }
            }

            this.setDead();
         } else {
            if (this.onServer() && rc.isBlockHitFullWaterBlock(true)) {
               this.entityFX(EnumEntityFX.splash);
            }

            this.setDead();
         }
      } else {
         if (this.onServer()) {
            this.entityFX(EnumEntityFX.burned_up_in_lava);
         }

         this.setDead();
      }

   }

   private static boolean canReplaceBlockAt(World world, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      if (block == null) {
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         return block == Block.snow && metadata == 0;
      }
   }

   private static boolean setBlockToWebIfEmpty(World worldObj, int x, int y, int z, boolean burning) {
      if (canReplaceBlockAt(worldObj, x, y, z) && worldObj.setBlock(x, y, z, Block.web.blockID)) {
         if (burning) {
            EnumDirection[] directions = new EnumDirection[6];
            int index = 0;
            if (worldObj.getBlockId(x, y + 1, z) == 0) {
               directions[index++] = EnumDirection.UP;
            }

            if (worldObj.getBlockId(x, y - 1, z) == 0) {
               directions[index++] = EnumDirection.DOWN;
            }

            if (worldObj.getBlockId(x, y, z + 1) == 0) {
               directions[index++] = EnumDirection.SOUTH;
            }

            if (worldObj.getBlockId(x, y, z - 1) == 0) {
               directions[index++] = EnumDirection.NORTH;
            }

            if (worldObj.getBlockId(x + 1, y, z) == 0) {
               directions[index++] = EnumDirection.EAST;
            }

            if (worldObj.getBlockId(x - 1, y, z) == 0) {
               directions[index++] = EnumDirection.WEST;
            }

            if (index > 0) {
               EnumDirection direction = directions[worldObj.rand.nextInt(index)];
               int[] coords = World.getNeighboringBlockCoords(x, y, z, direction.getFace());
               worldObj.setBlock(coords[0], coords[1], coords[2], Block.fire.blockID);
               List entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBoxFromPool(x, y, z, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
               Iterator i = entities.iterator();

               while(i.hasNext()) {
                  EntityLivingBase elb = (EntityLivingBase)i.next();
                  elb.setFire(5);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Item getModelItem() {
      return Item.thrownWeb;
   }
}
