package net.minecraft.entity.effect;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class EntityWeatherEffect extends Entity {
   public EntityWeatherEffect(World par1World) {
      super(par1World);
   }

   public boolean canCatchFire() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }
}
