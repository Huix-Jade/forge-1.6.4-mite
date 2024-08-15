package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCubic;
import net.minecraft.entity.EntityGelatinousCube;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public final class EntitySlime extends EntityGelatinousCube {
   public EntitySlime(World world) {
      super(world);
   }

   public EntityCubic createInstance() {
      return new EntitySlime(this.worldObj);
   }

   public EnumParticle getSquishParticle() {
      return EnumParticle.slime;
   }

   public DamageSource getDamageTypeVsItems() {
      return DamageSource.pepsin;
   }

   public int getAttackStrengthMultiplierForType() {
      return 1;
   }
}
