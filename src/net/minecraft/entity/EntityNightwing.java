package net.minecraft.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityNightwing extends EntityBat {
   private int attack_cooldown;

   public EntityNightwing(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 1.0 * (double)this.getScaleFactor());
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      return !damage_source.hasSilverAspect() && !damage_source.hasMagicAspect() && !damage_source.isSunlight();
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return perform_light_check && this.isInSunlight() ? false : super.getCanSpawnHere(perform_light_check);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.attack_cooldown > 0) {
         --this.attack_cooldown;
      }

      if (this.getTicksExistedWithOffset() % 20 == 0) {
         this.setAttackTarget(this.worldObj.getClosestPrey(this, 32.0, true, false));
      }

   }

   public void onLivingUpdate() {
      if (this.onServer()) {
         if (this.isInSunlight()) {
            this.attackEntityFrom(new Damage(DamageSource.sunlight, 1000.0F));
         } else if (this.ticksExisted % 40 == 0) {
            float brightness = this.getBrightness(1.0F);
            int amount_to_heal = (int)((0.4F - brightness) * 10.0F);
            if (amount_to_heal > 0) {
               this.heal((float)amount_to_heal);
            }
         }
      }

      super.onLivingUpdate();
   }

   public final boolean preysUpon(Entity entity) {
      return entity.isEntityPlayer() && !entity.getAsPlayer().inCreativeMode() || entity.isTrueAnimal() || entity instanceof EntityVillager;
   }

   protected void collideWithEntity(Entity entity) {
      super.collideWithEntity(entity);
      if (this.attack_cooldown <= 0 && entity == this.getAttackTarget()) {
         if (this.boundingBox.copy().scaleXZ(0.5).intersectsWith(entity.boundingBox)) {
            EntityDamageResult result = EntityMob.attackEntityAsMob(this, entity);
            if (result != null && result.entityWasNegativelyAffected() && entity instanceof EntityPlayer) {
               EntityPlayer var10000 = entity.getAsPlayer();
               var10000.vision_dimming += entity.getAsEntityLivingBase().getAmountAfterResistance(1.25F, 4);
            }
         }

         this.attack_cooldown = 20;
      }

   }

   public int getMaxSpawnedInChunk() {
      return 8;
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }
}
