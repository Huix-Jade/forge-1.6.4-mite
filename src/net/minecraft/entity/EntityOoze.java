package net.minecraft.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityOoze extends EntityGelatinousCube {
   private int data_object_id_is_climbing;

   public EntityOoze(World world) {
      super(world);
   }

   protected void entityInit() {
      super.entityInit();
      this.data_object_id_is_climbing = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
   }

   public void setClimbing(boolean is_climbing) {
      this.dataWatcher.updateObject(this.data_object_id_is_climbing, (byte)(is_climbing ? -1 : 0));
   }

   public EntityCubic createInstance() {
      return new EntityOoze(this.worldObj);
   }

   public EnumParticle getSquishParticle() {
      return EnumParticle.gray_ooze;
   }

   protected int getDropItemSubtype() {
      return 3;
   }

   protected boolean isValidLightLevel() {
      return EntityMob.isValidLightLevel(this);
   }

   public DamageSource getDamageTypeVsItems() {
      return DamageSource.acid;
   }

   public int getAttackStrengthMultiplierForType() {
      return 3;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      return !damage_source.isLavaDamage() && !damage_source.hasMagicAspect() && !damage_source.isSnowball();
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return false;
   }

   public void setSize(int size) {
      super.setSize(size < 2 ? size : 2);
   }

   public boolean isOnLadder() {
      return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_climbing) != 0;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.onServer()) {
         this.setClimbing(this.isCollidedHorizontally);
      }

   }
}
