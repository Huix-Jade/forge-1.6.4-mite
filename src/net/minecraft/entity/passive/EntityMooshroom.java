package net.minecraft.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.world.World;

public class EntityMooshroom extends EntityCow {
   public EntityMooshroom(World par1World) {
      super(par1World);
      this.setSize(0.9F, 1.3F);
   }

   public EntityMooshroom func_94900_c(EntityAgeable par1EntityAgeable) {
      return new EntityMooshroom(this.worldObj);
   }

   public EntityCow spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
      return this.func_94900_c(par1EntityAgeable);
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.func_94900_c(par1EntityAgeable);
   }
}
