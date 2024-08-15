package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderCrystal extends Entity {
   public int innerRotation;
   public int health;

   public EntityEnderCrystal(World par1World) {
      super(par1World);
      this.preventEntitySpawning = true;
      this.setSize(2.0F, 2.0F);
      this.yOffset = this.height / 2.0F;
      this.health = 5;
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EntityEnderCrystal(World par1World, double par2, double par4, double par6) {
      this(par1World);
      this.setPosition(par2, par4, par6);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(8, this.health);
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      ++this.innerRotation;
      this.dataWatcher.updateObject(8, this.health);
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.posY);
      int var3 = MathHelper.floor_double(this.posZ);
      if (this.worldObj.getBlockId(var1, var2, var3) != Block.fire.blockID) {
         this.worldObj.setBlock(var1, var2, var3, Block.fire.blockID);
      }

   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      if (super.isImmuneTo(damage_source)) {
         return true;
      } else {
         if (damage_source.isMelee()) {
            ItemStack item_stack = damage_source.getItemAttackedWith();
            if (item_stack != null && item_stack.getItem().isTool()) {
               ItemTool item_tool = item_stack.getItemAsTool();
               if (item_tool.isEffectiveAgainstBlock(Block.blockMithril, 0)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return null;
      } else {
         this.health = 0;
         if (this.health <= 0) {
            this.setDead();
            result.setEntityWasDestroyed();
            this.worldObj.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 6.0F, 6.0F, true);
         }

         return result;
      }
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
