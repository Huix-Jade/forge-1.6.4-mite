package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityBrick extends EntityThrowable {
   private Item item;

   public EntityBrick(World world, Item item) {
      super(world);
      this.item = item;
   }

   public EntityBrick(World world, EntityLivingBase thrower, Item item) {
      super(world, thrower);
      this.item = item;
   }

   public EntityBrick(World world, double pos_x, double pos_y, double pos_z, Item item) {
      super(world, pos_x, pos_y, pos_z);
      this.item = item;
   }

   protected float getGravityVelocity() {
      return 0.07F;
   }

   protected void onImpact(RaycastCollision rc) {
      if (rc.isEntity()) {
         rc.getEntityHit().attackEntityFrom(new Damage(DamageSource.causeThrownDamage(this, this.getThrower()), 2.0F));
      }

      if (this.onServer() && rc.isBlock()) {
         if (rc.getBlockHit() == Block.thinGlass) {
            this.worldObj.destroyBlock(new BlockBreakInfo(this.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z), true);
         } else {
            rc.getBlockHit().onEntityCollidedWithBlock(this.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, this);
         }
      }

      for(int var5 = 0; var5 < 8; ++var5) {
         this.worldObj.spawnParticle(this.item == Item.netherrackBrick ? EnumParticle.brickpoof_netherrack : EnumParticle.brickpoof, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }

   }

   public Item getModelItem() {
      return this.item;
   }
}
