package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumQuality;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {
   private static final UUID field_110189_bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier field_110190_br;
   private int angerLevel;
   private int randomSoundDelay;
   private Entity field_110191_bu;

   public EntityPigZombie(World par1World) {
      super(par1World);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(field_110186_bp).setAttribute(0.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.5);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(8.0);
   }

   protected boolean isAIEnabled() {
      return false;
   }

   public void onUpdate() {
      if (this.field_110191_bu != this.entityToAttack && !this.worldObj.isRemote) {
         AttributeInstance var1 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         var1.removeModifier(field_110190_br);
         if (this.entityToAttack != null) {
            var1.applyModifier(field_110190_br);
         }
      }

      this.field_110191_bu = this.entityToAttack;
      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume("mob.zombiepig.zpigangry") * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      super.onUpdate();
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return this.worldObj.difficultySetting > 0 && this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setShort("Anger", (short)this.angerLevel);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.angerLevel = par1NBTTagCompound.getShort("Anger");
   }

   protected EntityPlayer findPlayerToAttack(float max_distance) {
      if (this.angerLevel < 1) {
         max_distance /= 4.0F;
      }

      Entity previous_target = this.getEntityToAttack();
      EntityPlayer target = super.findPlayerToAttack(max_distance);
      if (target != null && target != previous_target) {
         this.becomeAngryAt(target);
      }

      return target;
   }

   public EntityItem findTargetEntityItem(float max_distance) {
      Iterator i = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand((double)max_distance, (double)(max_distance * 0.25F), (double)max_distance)).iterator();

      EntityItem entity_item;
      do {
         if (!i.hasNext()) {
            return null;
         }

         entity_item = (EntityItem)i.next();
      } while(!this.willPickupAsValuable(entity_item.getEntityItem()));

      return entity_item;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (result.entityWasNegativelyAffected()) {
            Entity var3 = damage.getResponsibleEntity();
            if (var3 instanceof EntityPlayer) {
               List var4 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0, 32.0, 32.0));

               for(int var5 = 0; var5 < var4.size(); ++var5) {
                  Entity var6 = (Entity)var4.get(var5);
                  if (var6 instanceof EntityPigZombie) {
                     EntityPigZombie var7 = (EntityPigZombie)var6;
                     var7.becomeAngryAt(var3);
                  }
               }

               this.becomeAngryAt(var3);
            }
         }

         return result;
      }
   }

   private void becomeAngryAt(Entity par1Entity) {
      this.entityToAttack = par1Entity;
      this.angerLevel = 400 + this.rand.nextInt(400);
      this.randomSoundDelay = this.rand.nextInt(40);
   }

   protected String getLivingSound() {
      return "mob.zombiepig.zpig";
   }

   protected String getHurtSound() {
      return "mob.zombiepig.zpighurt";
   }

   protected String getDeathSound() {
      return "mob.zombiepig.zpigdeath";
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (this.rand.nextFloat() < (recently_hit_by_player ? 0.5F : 0.25F)) {
         this.dropItem(Item.rottenFlesh);
      }

      int num_drops = this.rand.nextInt(2 + damage_source.getLootingModifier());

      for(int i = 0; i < num_drops; ++i) {
         this.dropItem(Item.goldNugget);
      }

      if (recently_hit_by_player && !this.has_taken_massive_fall_damage && this.rand.nextInt(this.getBaseChanceOfRareDrop()) < 5 + damage_source.getLootingModifier() * 2) {
         this.dropItem(Item.ingotGold);
      }

   }

   protected int getDropItemId() {
      return Item.rottenFlesh.itemID;
   }

   public void addRandomEquipment() {
      Item[] items = new Item[]{Item.swordGold, Item.swordGold, Item.battleAxeGold, Item.pickaxeGold};
      this.setCurrentItemOrArmor(0, (new ItemStack(items[this.rand.nextInt(items.length)])).setQuality(EnumQuality.poor).randomizeForMob(this, false));
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      super.onSpawnWithEgg(par1EntityLivingData);
      this.setVillager(false, 0);
      return par1EntityLivingData;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
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

   public boolean catchesFireInSunlight() {
      return false;
   }

   public boolean willPickupAsValuable(ItemStack item_stack) {
      return item_stack.getItemSubtype() == 0 && item_stack.getItemDamage() == 0 && item_stack.hasMaterial(Material.gold);
   }

   static {
      field_110190_br = (new AttributeModifier(field_110189_bq, "Attacking speed boost", 0.45, 0)).setSaved(false);
   }
}
