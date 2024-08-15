package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet82AddHunger;

public class FoodStats {
   private int satiation;
   private int nutrition;
   private float hunger;
   private float hunger_for_nutrition_only;
   private float heal_progress;
   private float starve_progress;
   private EntityPlayer player;
   private float global_hunger_rate = 1.0F;

   public FoodStats(EntityPlayer player) {
      this.player = player;
      this.satiation = this.nutrition = this.getNutritionLimit();
   }

   public void addFoodValue(Item item) {
      this.addSatiation(item.getSatiation(this.player));
      this.addNutrition(item.getNutrition());
      if (this.player instanceof EntityPlayerMP) {
         this.player.getAsEntityPlayerMP().addInsulinResistance(item.getInsulinResponse());
         this.player.getAsEntityPlayerMP().addNutrients(item);
      }

   }

   public static float getHungerPerTick() {
      return 0.002F;
   }

   public static float getHungerPerFoodUnit() {
      return 4.0F;
   }

   public void onUpdate(EntityPlayerMP par1EntityPlayer) {
      if (!par1EntityPlayer.isGhost() && !par1EntityPlayer.isZevimrgvInTournament()) {
         if (!par1EntityPlayer.isDead && !(par1EntityPlayer.getHealth() <= 0.0F)) {
            par1EntityPlayer.decrementNutrients();
            par1EntityPlayer.decrementInsulinResistance();
            float hunger_factor = par1EntityPlayer.getWetnessAndMalnourishmentHungerMultiplier();
            this.addHungerServerSide(getHungerPerTick() * hunger_factor);
            if (!par1EntityPlayer.inCreativeMode()) {
               this.hunger_for_nutrition_only += getHungerPerTick() * 0.25F;
            }

            if (this.hunger >= getHungerPerFoodUnit()) {
               this.hunger -= getHungerPerFoodUnit();
               if (this.satiation > 0 || this.nutrition > 0) {
                  if (this.satiation < 1 || this.hunger_for_nutrition_only + 0.001F >= getHungerPerFoodUnit() && this.nutrition > 0) {
                     --this.nutrition;
                     this.hunger_for_nutrition_only = 0.0F;
                  } else {
                     --this.satiation;
                  }
               }
            }

            if (par1EntityPlayer.inBed() && par1EntityPlayer.isOnHitList()) {
               par1EntityPlayer.addHungerServerSide(getHungerPerTick() * 20.0F);
            }

            if (this.player.isStarving()) {
               this.heal_progress = 0.0F;
               this.starve_progress += 0.002F;
               if (this.starve_progress >= 1.0F) {
                  if (par1EntityPlayer.getHealth() > 10.0F || this.player.worldObj.difficultySetting >= 3 || par1EntityPlayer.getHealth() > 1.0F && this.player.worldObj.difficultySetting >= 2) {
                     par1EntityPlayer.attackEntityFrom(new Damage(DamageSource.starve, 1.0F));
                  }

                  --this.starve_progress;
                  this.hunger_for_nutrition_only = 0.0F;
               }
            } else {
               this.heal_progress += (4.0E-4F + (float)this.nutrition * 2.0E-5F) * (par1EntityPlayer.isMalnourished() ? 0.25F : 1.0F) * (par1EntityPlayer.inBed() ? 4.0F : 1.0F) * EnchantmentHelper.getRegenerationModifier(this.player);
               this.starve_progress = 0.0F;
               if (par1EntityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && par1EntityPlayer.shouldHeal()) {
                  if (this.heal_progress >= 1.0F) {
                     par1EntityPlayer.heal(1.0F);
                     this.addHungerServerSide(1.0F);
                     --this.heal_progress;
                  }
               } else {
                  this.heal_progress = 0.0F;
               }
            }

         }
      }
   }

   public void readNBT(NBTTagCompound par1NBTTagCompound) {
      if (par1NBTTagCompound.hasKey("nutrition")) {
         this.satiation = par1NBTTagCompound.getInteger("fullness");
         this.nutrition = par1NBTTagCompound.getInteger("nutrition");
         this.heal_progress = par1NBTTagCompound.getFloat("heal_progress");
         this.starve_progress = par1NBTTagCompound.getFloat("starve_progress");
         this.hunger = par1NBTTagCompound.getFloat("hunger");
         this.hunger_for_nutrition_only = par1NBTTagCompound.getFloat("hunger_for_nutrition_only");
      }

   }

   public void writeNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setInteger("fullness", this.satiation);
      par1NBTTagCompound.setInteger("nutrition", this.nutrition);
      par1NBTTagCompound.setFloat("heal_progress", this.heal_progress);
      par1NBTTagCompound.setFloat("starve_progress", this.starve_progress);
      par1NBTTagCompound.setFloat("hunger", this.hunger);
      par1NBTTagCompound.setFloat("hunger_for_nutrition_only", this.hunger_for_nutrition_only);
   }

   public int getSatiation() {
      return this.satiation;
   }

   public int getNutrition() {
      return this.nutrition;
   }

   private void addHunger(float hunger) {
      if (!this.player.capabilities.isCreativeMode && !this.player.capabilities.disableDamage && !this.player.isGhost() && !this.player.isZevimrgvInTournament()) {
         hunger *= this.global_hunger_rate;
         this.hunger = Math.min(this.hunger + hunger, 40.0F);
         if (this.player.worldObj.isRemote && this.hunger > 0.2F) {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new Packet82AddHunger(this.hunger));
            this.hunger = 0.0F;
         }

      }
   }

   public void addHungerClientSide(float hunger) {
      if (!this.player.worldObj.isRemote) {
         Minecraft.setErrorMessage("addHungerClientSide: cannot add hunger to client if not remote");
      } else {
         this.addHunger(hunger);
      }
   }

   public void addHungerServerSide(float hunger) {
      if (this.player.worldObj.isRemote) {
         Minecraft.setErrorMessage("addHungerServerSide: cannot add hunger to server if remote");
      } else {
         this.addHunger(hunger);
      }
   }

   public float getHunger() {
      return this.hunger;
   }

   public void setHungerServerSide(float hunger) {
      if (this.player.worldObj.isRemote) {
         Minecraft.setErrorMessage("setHunger: cannot set hunger on server if remote");
      } else {
         this.hunger = hunger;
      }
   }

   public void setSatiation(int satiation, boolean check_limit) {
      if (check_limit) {
         this.satiation = Math.min(satiation, this.getSatiationLimit());
      } else {
         this.satiation = satiation;
      }

   }

   public int addSatiation(int satiation) {
      this.setSatiation(this.satiation + satiation, true);
      return this.satiation;
   }

   public void setNutrition(int nutrition, boolean check_limit) {
      if (check_limit) {
         this.nutrition = Math.min(nutrition, this.getNutritionLimit());
      } else {
         this.nutrition = nutrition;
      }

   }

   public int addNutrition(int nutrition) {
      this.setNutrition(this.nutrition + nutrition, true);
      return this.nutrition;
   }

   public int getSatiationLimit() {
      return this.getNutritionLimit();
   }

   public int getNutritionLimit() {
      return Math.max(Math.min(6 + this.player.getExperienceLevel() / 5 * 2, 20), 6);
   }
}
