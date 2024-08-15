package net.minecraft.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public final class EntityBlob extends EntityGelatinousCube {
   public EntityBlob(World world) {
      super(world);
   }

   public EntityCubic createInstance() {
      return new EntityBlob(this.worldObj);
   }

   public EnumParticle getSquishParticle() {
      return EnumParticle.crimson_blob;
   }

   protected int getDropItemSubtype() {
      return 2;
   }

   protected boolean isValidLightLevel() {
      return EntityMob.isValidLightLevel(this);
   }

   public DamageSource getDamageTypeVsItems() {
      return DamageSource.pepsin;
   }

   public int getAttackStrengthMultiplierForType() {
      return 3;
   }
}
