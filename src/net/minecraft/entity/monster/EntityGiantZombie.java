package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityGiantZombie extends EntityMob {
   public EntityGiantZombie(World var1) {
      super(var1);
      this.yOffset *= 6.0F;
      this.setSize(this.width * 6.0F, this.height * 6.0F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(100.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.5);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(50.0);
   }

   public float getBlockPathWeight(int var1, int var2, int var3) {
      return this.worldObj.getLightBrightness(var1, var2, var3) - 0.5F;
   }
}
