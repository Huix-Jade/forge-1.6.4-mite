package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;

public class EntityDragonPart extends Entity {
   public final IEntityMultiPart entityDragonObj;
   public final String name;

   public EntityDragonPart(IEntityMultiPart par1IEntityMultiPart, String par2Str, float par3, float par4) {
      super(par1IEntityMultiPart.func_82194_d());
      this.setSize(par3, par4);
      this.entityDragonObj = par1IEntityMultiPart;
      this.name = par2Str;
      this.renderDistanceWeight = 10.0;
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      return this.entityDragonObj.attackEntityFromPart(this, damage);
   }

   public boolean isEntityEqual(Entity par1Entity) {
      return this == par1Entity || this.entityDragonObj == par1Entity;
   }

   public boolean canCatchFire() {
      return true;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }
}
