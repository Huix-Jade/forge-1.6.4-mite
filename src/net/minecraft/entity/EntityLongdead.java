package net.minecraft.entity;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityLongdead extends EntitySkeleton {
   public EntityLongdead(World world) {
      super(world);

      for(int i = 1; i < this.equipmentDropChances.length; ++i) {
         float[] var10000 = this.equipmentDropChances;
         var10000[i] *= 0.25F;
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 40.0);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, this.isGuardian() ? 24.0 : 12.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.28999999165534973);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, this.isGuardian() ? 8.0 : 6.0);
   }

   public void addRandomWeapon() {
      this.setHeldItemStack((new ItemStack((Item)(this.getSkeletonType() == 2 ? Item.swordAncientMetal : Item.bowAncientMetal))).randomizeForMob(this, true));
   }

   protected void addRandomEquipment() {
      this.addRandomWeapon();
      this.setBoots((new ItemStack(Item.bootsChainAncientMetal)).randomizeForMob(this, true));
      this.setLeggings((new ItemStack(Item.legsChainAncientMetal)).randomizeForMob(this, true));
      this.setCuirass((new ItemStack(Item.plateChainAncientMetal)).randomizeForMob(this, true));
      this.setHelmet((new ItemStack(Item.helmetChainAncientMetal)).randomizeForMob(this, true));
   }

   public boolean isLongdead() {
      return true;
   }

   public boolean isGuardian() {
      return this instanceof EntityLongdeadGuardian;
   }

   public float getNaturalDefense(DamageSource damage_source) {
      return super.getNaturalDefense(damage_source) + (damage_source.bypassesMundaneArmor() ? 0.0F : (this.isGuardian() ? 2.0F : 1.0F));
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * (this.isGuardian() ? 5 : 3);
   }
}
