package net.minecraft.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityPudding extends EntityGelatinousCube {
   public EntityPudding(World world) {
      super(world);
   }

   public EntityCubic createInstance() {
      return new EntityPudding(this.worldObj);
   }

   public EnumParticle getSquishParticle() {
      return EnumParticle.black_pudding;
   }

   protected int getDropItemSubtype() {
      return 4;
   }

   protected boolean isValidLightLevel() {
      return EntityMob.isValidLightLevel(this);
   }

   public DamageSource getDamageTypeVsItems() {
      return DamageSource.acid;
   }

   public int getAttackStrengthMultiplierForType() {
      return 4;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      return !damage_source.hasFireAspect() && !damage_source.isLavaDamage() && !damage_source.hasMagicAspect() && !damage_source.isSnowball();
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return false;
   }
}
