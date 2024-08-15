package net.minecraft.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public final class EntityJelly extends EntityGelatinousCube {
   public EntityJelly(World world) {
      super(world);
   }

   public EntityCubic createInstance() {
      return new EntityJelly(this.worldObj);
   }

   public EnumParticle getSquishParticle() {
      return EnumParticle.ochre_jelly;
   }

   protected int getDropItemSubtype() {
      return 1;
   }

   protected boolean isValidLightLevel() {
      return EntityMob.isValidLightLevel(this);
   }

   public DamageSource getDamageTypeVsItems() {
      return DamageSource.pepsin;
   }

   public int getAttackStrengthMultiplierForType() {
      return 2;
   }
}
