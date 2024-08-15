package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityFireElemental;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityNetherspawn;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.item.Item;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable {
   public EntitySnowball(World world) {
      super(world);
   }

   public EntitySnowball(World world, EntityLivingBase thrower) {
      super(world, thrower);
   }

   public EntitySnowball(World world, double pos_x, double pos_y, double pos_z) {
      super(world, pos_x, pos_y, pos_z);
   }

   protected void onImpact(RaycastCollision rc) {
      if (!this.worldObj.isRemote) {
         if (rc.isEntity()) {
            Entity entity_hit = rc.getEntityHit();
            float damage;
            if (!(entity_hit instanceof EntityBlaze) && !(entity_hit instanceof EntityFireElemental)) {
               if (!(entity_hit instanceof EntityMagmaCube) && !(entity_hit instanceof EntityNetherspawn)) {
                  damage = 1.0F;
               } else {
                  damage = 2.0F;
                  entity_hit.causeQuenchEffect();
               }
            } else {
               damage = 3.0F;
               entity_hit.causeQuenchEffect();
            }

            entity_hit.attackEntityFrom(new Damage(DamageSource.causeThrownDamage(this, this.getThrower()), damage));
            if (entity_hit.isBurning()) {
               entity_hit.causeQuenchEffect();
               if (Math.random() < 0.5) {
                  entity_hit.extinguish();
               }
            } else if (entity_hit instanceof EntityEarthElemental) {
               EntityEarthElemental elemental = (EntityEarthElemental)entity_hit;
               if (elemental.isMagma()) {
                  elemental.convertToNormal(true);
               }
            }
         } else {
            if (rc.getNeighborOfBlockHit() == Block.fire) {
               this.worldObj.douseFire(rc.neighbor_block_x, rc.neighbor_block_y, rc.neighbor_block_z, this);
            }

            rc.getBlockHit().onEntityCollidedWithBlock(this.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, this);
         }
      }

      for(int var3 = 0; var3 < 8; ++var3) {
         this.worldObj.spawnParticle(EnumParticle.snowballpoof, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }

   }

   public Item getModelItem() {
      return Item.snowball;
   }
}
