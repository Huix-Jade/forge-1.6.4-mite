package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemGelatinousSphere;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityGelatinousSphere extends EntityThrowable {
   private ItemGelatinousSphere item;
   private int subtype;

   public EntityGelatinousSphere(World world, ItemGelatinousSphere item, int subtype) {
      super(world);
      this.item = item;
      this.subtype = subtype;
   }

   public EntityGelatinousSphere(World world, EntityLivingBase thrower, ItemGelatinousSphere item, int subtype) {
      super(world, thrower);
      this.item = item;
      this.subtype = subtype;
   }

   public EntityGelatinousSphere(World world, double pos_x, double pos_y, double pos_z, ItemGelatinousSphere item, int subtype) {
      super(world, pos_x, pos_y, pos_z);
      this.item = item;
      this.subtype = subtype;
   }

   protected float getGravityVelocity() {
      return 0.07F;
   }

   protected void onImpact(RaycastCollision rc) {
      if (rc.isEntity()) {
         EntityDamageResult result = rc.getEntityHit().attackEntityFrom(new Damage(DamageSource.causeThrownDamage(this, this.getThrower()), 1.0F + (float)this.item.getAttackDamage(this.subtype)));
         if (result != null && result.entityWasNegativelyAffected()) {
            rc.getEntityHit().entityFX(EnumEntityFX.steam_with_hiss);
         }
      }

      int x;
      if (this.onServer() && rc.isBlock()) {
         Block block = rc.getBlockHit();
         x = rc.block_hit_x;
         int y = rc.block_hit_y;
         int z = rc.block_hit_z;
         World var10000;
         int[] coords;
         Block neighbor_block;
         if (this.hasPepsin()) {
            var10000 = this.worldObj;
            coords = World.getNeighboringBlockCoords(x, y, z, rc.face_hit);
            neighbor_block = this.worldObj.getBlock(coords);
            if (neighbor_block != null && neighbor_block.onContactWithPepsin(this.worldObj, coords[0], coords[1], coords[2], rc.face_hit.getOpposite(), true)) {
               block = this.worldObj.getBlock(x, y, z);
            }

            if (block != null && block.onContactWithPepsin(this.worldObj, x, y, z, rc.face_hit, true)) {
               block = this.worldObj.getBlock(x, y, z);
            }
         }

         if (this.isAcidic()) {
            var10000 = this.worldObj;
            coords = World.getNeighboringBlockCoords(x, y, z, rc.face_hit);
            neighbor_block = this.worldObj.getBlock(coords);
            if (neighbor_block != null && neighbor_block.onContactWithAcid(this.worldObj, coords[0], coords[1], coords[2], rc.face_hit.getOpposite(), true)) {
               block = this.worldObj.getBlock(x, y, z);
            }

            if (block != null && block.onContactWithAcid(this.worldObj, x, y, z, rc.face_hit, true)) {
               block = this.worldObj.getBlock(x, y, z);
            }
         }

         if (block != null) {
            block.onEntityCollidedWithBlock(this.worldObj, x, y, z, this);
         }
      }

      EnumParticle enum_particle = this.subtype == 0 ? EnumParticle.slime : (this.subtype == 1 ? EnumParticle.ochre_jelly : (this.subtype == 2 ? EnumParticle.crimson_blob : (this.subtype == 3 ? EnumParticle.gray_ooze : EnumParticle.black_pudding)));

      for(x = 0; x < 8; ++x) {
         this.worldObj.spawnParticle(enum_particle, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }

   }

   public ItemGelatinousSphere getModelItem() {
      return this.item;
   }

   public int getModelSubtype() {
      return this.subtype;
   }

   public DamageSource getDamageType() {
      return this.item.getDamageType(this.subtype);
   }

   public boolean isAcidic() {
      return this.getDamageType().isAcidDamage();
   }

   public boolean hasPepsin() {
      return this.getDamageType().isPepsinDamage();
   }
}
