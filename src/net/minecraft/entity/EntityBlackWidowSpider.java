package net.minecraft.entity;

import net.minecraft.world.World;

public class EntityBlackWidowSpider extends EntityWoodSpider {
   public EntityBlackWidowSpider(World world) {
      super(world);
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 8 / 5;
   }
}
