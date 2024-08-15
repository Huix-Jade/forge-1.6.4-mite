package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAvoidFire;
import net.minecraft.entity.ai.EntityAIGetOutOfWater;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToRepairItem;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISeekShelterFromRain;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   public EntitySnowman(World par1World) {
      super(par1World);
      this.setSize(0.6F, 1.8F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0F));
      this.tasks.addTask(2, new EntityAIWander(this, 1.0));
      this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(4, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.mobSelector));
      this.tasks.addTask(4, new EntityAISeekShelterFromRain(this, 1.0F, true));
      this.tasks.addTask(2, new EntityAIMoveToRepairItem(this, 1.0F, true));
      this.tasks.addTask(2, new EntityAIAvoidFire(this, 1.0F, true));
      this.tasks.addTask(1, new EntityAIGetOutOfWater(this, 1.0F));
   }

   public boolean isAIEnabled() {
      return true;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      int damage_rate;
      if (this.onServer()) {
         if (this.getTicksExistedWithOffset() % (this.isInWater() ? 40 : 100) == 0 && this.isWet()) {
            this.attackEntityFrom(new Damage(DamageSource.melt, 1.0F));
         } else {
            float temperature = this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ()).getFloatTemperature();
            if (temperature >= 0.2F) {
               damage_rate = temperature <= 0.4F ? 4000 : (temperature <= 0.6F ? 3000 : (temperature <= 0.8F ? 2000 : (temperature <= 1.0F ? 1000 : (temperature <= 1.2F ? 500 : (temperature <= 1.4F ? 300 : (temperature <= 1.6F ? 200 : 100))))));
               if (this.getTicksExistedWithOffset() % damage_rate == 0) {
                  this.attackEntityFrom(new Damage(DamageSource.melt, 1.0F));
               }
            }
         }
      }

      for(int var1 = 0; var1 < 4; ++var1) {
         damage_rate = MathHelper.floor_double(this.posX + (double)((float)(var1 % 2 * 2 - 1) * 0.25F));
         int var3 = MathHelper.floor_double(this.posY);
         int var4 = MathHelper.floor_double(this.posZ + (double)((float)(var1 / 2 % 2 * 2 - 1) * 0.25F));
         if (this.worldObj.getBlockId(damage_rate, var3, var4) == 0 && this.worldObj.getBiomeGenForCoords(damage_rate, var4).getFloatTemperature() < 0.8F && Block.snow.canOccurAt(this.worldObj, damage_rate, var3, var4, 0)) {
            this.worldObj.setBlock(damage_rate, var3, var4, Block.snow.blockID);
         }
      }

   }

   protected int getDropItemId() {
      return Item.snowball.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(8 + damage_source.getLootingModifier() * 4);

      for(int i = 0; i < num_drops; ++i) {
         this.dropItem(Item.snowball.itemID, 1);
      }

   }

   public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
      EntitySnowball var3 = new EntitySnowball(this.worldObj, this);
      double var4 = par1EntityLivingBase.posX - this.posX;
      double var6 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858 - var3.posY;
      double var8 = par1EntityLivingBase.posZ - this.posZ;
      float var10 = MathHelper.sqrt_double(var4 * var4 + var8 * var8) * 0.2F;
      var3.setThrowableHeading(var4, var6 + (double)var10, var8, 1.6F, 12.0F);
      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(var3);
   }

   public boolean canDouseFire() {
      return true;
   }

   public boolean canCatchFire() {
      return true;
   }

   public int getExperienceValue() {
      return 0;
   }

   public boolean isRepairItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() == Item.snowball;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      if (damage.getSource() != DamageSource.melt) {
         damage.scaleAmount(2.0F);
      }

      if (damage.isArrowDamage() && damage.getAmount() > 1.0F) {
         damage.scaleAmount(0.5F, 1.0F);
      }

      return super.attackEntityFrom(damage);
   }

   public boolean isEntityBiologicallyAlive() {
      return false;
   }

   public EnumEntityFX getHealFX() {
      return EnumEntityFX.repair;
   }

   public boolean healsWithTime() {
      return false;
   }
}
