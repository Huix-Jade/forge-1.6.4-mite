package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;

public class EntityMooshroom extends EntityCow implements IShearable {
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

   @Override
   public boolean isShearable(ItemStack item, World world, int X, int Y, int Z)
   {
      return getGrowingAge() >= 0;
   }

   @Override
   public ArrayList<ItemStack> onSheared(ItemStack item, World world, int X, int Y, int Z, int fortune)
   {
      setDead();
      EntityCow entitycow = new EntityCow(worldObj);
      entitycow.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
      entitycow.setHealth(this.getHealth());
      entitycow.renderYawOffset = renderYawOffset;
      worldObj.spawnEntityInWorld(entitycow);
      worldObj.spawnParticle("largeexplode", posX, posY + (double)(height / 2.0F), posZ, 0.0D, 0.0D, 0.0D);

      ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
      for (int x = 0; x < 5; x++)
      {
         ret.add(new ItemStack(Block.mushroomRed));
      }
      return ret;
   }
}
