package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StringUtils;

public class Potion {
   public static final Potion[] potionTypes = new Potion[32];
   public static final Potion field_76423_b = null;
   public static final Potion moveSpeed;
   public static final Potion moveSlowdown;
   public static final Potion digSpeed;
   public static final Potion digSlowdown;
   public static final Potion damageBoost;
   public static final Potion heal;
   public static final Potion harm;
   public static final Potion jump;
   public static final Potion confusion;
   public static final Potion regeneration;
   public static final Potion resistance;
   public static final Potion fireResistance;
   public static final Potion waterBreathing;
   public static final Potion invisibility;
   public static final Potion blindness;
   public static final Potion nightVision;
   public static final Potion hunger;
   public static final Potion weakness;
   public static final Potion poison;
   public static final Potion wither;
   public static final Potion field_76434_w;
   public static final Potion field_76444_x;
   public static final Potion field_76443_y;
   public static final Potion field_76442_z;
   public static final Potion field_76409_A;
   public static final Potion field_76410_B;
   public static final Potion field_76411_C;
   public static final Potion field_76405_D;
   public static final Potion field_76406_E;
   public static final Potion field_76407_F;
   public static final Potion field_76408_G;
   public final int id;
   private final Map field_111188_I = Maps.newHashMap();
   private final boolean isBadEffect;
   private final int liquidColor;
   private String name = "";
   private int statusIconIndex = -1;
   private double effectiveness;
   private boolean usable;
   public static final int SUBTYPE_POTION_OF_FIRE_RESISTANCE_I = 8227;
   public static final int SUBTYPE_POTION_OF_HEALING_I = 8261;
   public static final int SUBTYPE_SPLASH_POTION_OF_POISON_I = 16388;
   public static final int SUBTYPE_SPLASH_POTION_OF_WEAKNESS_I = 16424;
   public static final int SUBTYPE_SPLASH_POTION_OF_SLOWNESS_I = 16426;
   public static final int SUBTYPE_SPLASH_POTION_OF_HARMING_I = 16460;

   protected Potion(int par1, boolean par2, int par3) {
      this.id = par1;
      potionTypes[par1] = this;
      this.isBadEffect = par2;
      if (par2) {
         this.effectiveness = 0.5;
      } else {
         this.effectiveness = 1.0;
      }

      this.liquidColor = par3;
   }

   protected Potion setIconIndex(int par1, int par2) {
      this.statusIconIndex = par1 + par2 * 8;
      return this;
   }

   public int getId() {
      return this.id;
   }

   public void performEffect(EntityLivingBase par1EntityLivingBase, int par2) {
      if (!par1EntityLivingBase.onClient()) {
         if (this.id == regeneration.id) {
            if (par1EntityLivingBase.getHealth() < par1EntityLivingBase.getMaxHealth()) {
               par1EntityLivingBase.heal(1.0F);
            }
         } else if (this.id == poison.id) {
            par1EntityLivingBase.attackEntityFrom(new Damage(DamageSource.poison, 1.0F));
         } else if (this.id == wither.id) {
            par1EntityLivingBase.attackEntityFrom(new Damage(DamageSource.wither, 1.0F));
         } else if (this.id == hunger.id && par1EntityLivingBase instanceof EntityPlayer) {
            if (!par1EntityLivingBase.worldObj.isRemote) {
               ((EntityPlayer)par1EntityLivingBase).addHungerServerSide(0.025F * (float)(par2 + 1));
            }
         } else if (this.id == field_76443_y.id && par1EntityLivingBase instanceof EntityPlayer) {
            if (!par1EntityLivingBase.worldObj.isRemote) {
               ((EntityPlayer)par1EntityLivingBase).addFoodValue(new ItemFood(par2 + 1, par2 + 1, false, false, false));
            }
         } else if ((this.id != heal.id || par1EntityLivingBase.isEntityUndead()) && (this.id != harm.id || !par1EntityLivingBase.isEntityUndead())) {
            if (this.id == harm.id && !par1EntityLivingBase.isEntityUndead() || this.id == heal.id && par1EntityLivingBase.isEntityUndead()) {
               par1EntityLivingBase.attackEntityFrom(new Damage(DamageSource.magic, (float)(6 << par2)));
            }
         } else {
            par1EntityLivingBase.heal((float)Math.max(4 << par2, 0));
         }

      }
   }

   public void affectEntity(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase, int par3, double par4) {
      int var6;
      if (this.id == heal.id && !par2EntityLivingBase.isEntityUndead() || this.id == harm.id && par2EntityLivingBase.isEntityUndead()) {
         var6 = (int)(par4 * (double)(4 << par3) + 0.5);
         par2EntityLivingBase.heal((float)var6);
      } else if (this.id == harm.id && !par2EntityLivingBase.isEntityUndead() || this.id == heal.id && par2EntityLivingBase.isEntityUndead()) {
         var6 = (int)(par4 * (double)(6 << par3) + 0.5);
         if (par1EntityLivingBase == null) {
            par2EntityLivingBase.attackEntityFrom(new Damage(DamageSource.magic, (float)var6));
         } else {
            par2EntityLivingBase.attackEntityFrom(new Damage(DamageSource.causeIndirectMagicDamage(par2EntityLivingBase, par1EntityLivingBase), (float)var6));
         }
      }

   }

   public boolean isInstant() {
      return false;
   }

   public int getEffectInterval(int amplifier) {
      int interval;
      if (this.id == regeneration.id) {
         interval = 50 >> amplifier;
      } else if (this.id == poison.id) {
         interval = 100 >> amplifier;
      } else {
         if (this.id != wither.id) {
            if (this.id == hunger.id) {
               return 1;
            }

            return -1;
         }

         interval = 40 >> amplifier;
      }

      return interval < 1 ? 1 : interval;
   }

   public boolean isReady(int par1, int par2) {
      int effect_interval = this.getEffectInterval(par2);
      return effect_interval == -1 ? false : par1 % effect_interval == 0;
   }

   public Potion setPotionName(String par1Str) {
      this.name = par1Str;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasStatusIcon() {
      return this.statusIconIndex >= 0;
   }

   public int getStatusIconIndex() {
      return this.statusIconIndex;
   }

   public boolean isBadEffect() {
      return this.isBadEffect;
   }

   public static String getDurationString(PotionEffect par0PotionEffect) {
      if (par0PotionEffect.getIsPotionDurationMax()) {
         return "**:**";
      } else {
         int var1 = par0PotionEffect.getDuration();
         return StringUtils.ticksToElapsedTime(var1);
      }
   }

   protected Potion setEffectiveness(double par1) {
      this.effectiveness = par1;
      return this;
   }

   public double getEffectiveness() {
      return this.effectiveness;
   }

   public boolean isUsable() {
      return this.usable;
   }

   public int getLiquidColor() {
      return this.liquidColor;
   }

   public Potion func_111184_a(Attribute par1Attribute, String par2Str, double par3, int par5) {
      AttributeModifier var6 = new AttributeModifier(UUID.fromString(par2Str), this.getName(), par3, par5);
      this.field_111188_I.put(par1Attribute, var6);
      return this;
   }

   public Map func_111186_k() {
      return this.field_111188_I;
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase par1EntityLivingBase, BaseAttributeMap par2BaseAttributeMap, int par3) {
      Iterator var4 = this.field_111188_I.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         AttributeInstance var6 = par2BaseAttributeMap.getAttributeInstance((Attribute)var5.getKey());
         if (var6 != null) {
            var6.removeModifier((AttributeModifier)var5.getValue());
         }
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase par1EntityLivingBase, BaseAttributeMap par2BaseAttributeMap, int par3) {
      Iterator var4 = this.field_111188_I.entrySet().iterator();

      while(true) {
         AttributeInstance var6;
         AttributeModifier var7;
         do {
            Map.Entry var5;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               var5 = (Map.Entry)var4.next();
               var6 = par2BaseAttributeMap.getAttributeInstance((Attribute)var5.getKey());
            } while(var6 == null);

            var7 = (AttributeModifier)var5.getValue();
            var6.removeModifier(var7);
         } while(par1EntityLivingBase instanceof EntityPlayer && (this == moveSlowdown || this == moveSpeed));

         AttributeModifier attribute_modifier = new AttributeModifier(var7.getID(), this.getName() + " " + par3, this.func_111183_a(par3, var7), var7.getOperation());
         if (this.getName().equals("potion.moveSlowdown")) {
            int free_action = EnchantmentHelper.getFreeActionModifier(par1EntityLivingBase);
            if (free_action > 0) {
               attribute_modifier.setAmount(attribute_modifier.getAmount() * (double)(1.0F - (float)free_action * 0.8F / (float)Enchantment.free_action.getNumLevels()));
            }
         }

         var6.applyModifier(attribute_modifier);
      }
   }

   public double func_111183_a(int par1, AttributeModifier par2AttributeModifier) {
      return par2AttributeModifier.getAmount() * (double)(par1 + 1);
   }

   public static Potion get(int id) {
      return potionTypes[id];
   }

   static {
      moveSpeed = (new Potion(1, false, 8171462)).setPotionName("potion.moveSpeed").setIconIndex(0, 0).func_111184_a(SharedMonsterAttributes.movementSpeed, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224, 2);
      moveSlowdown = (new Potion(2, true, 5926017)).setPotionName("potion.moveSlowdown").setIconIndex(1, 0).func_111184_a(SharedMonsterAttributes.movementSpeed, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.20000000298023224, 2);
      digSpeed = (new Potion(3, false, 14270531)).setPotionName("potion.digSpeed").setIconIndex(2, 0).setEffectiveness(1.5);
      digSlowdown = (new Potion(4, true, 4866583)).setPotionName("potion.digSlowDown").setIconIndex(3, 0);
      damageBoost = (new PotionAttackDamage(5, false, 9643043)).setPotionName("potion.damageBoost").setIconIndex(4, 0).func_111184_a(SharedMonsterAttributes.attackDamage, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.4, 1);
      heal = (new PotionHealth(6, false, 16262179)).setPotionName("potion.heal");
      harm = (new PotionHealth(7, true, 4393481)).setPotionName("potion.harm");
      jump = (new Potion(8, false, 7889559)).setPotionName("potion.jump").setIconIndex(2, 1);
      confusion = (new Potion(9, true, 5578058)).setPotionName("potion.confusion").setIconIndex(3, 1).setEffectiveness(0.25);
      regeneration = (new Potion(10, false, 13458603)).setPotionName("potion.regeneration").setIconIndex(7, 0).setEffectiveness(0.25);
      resistance = (new Potion(11, false, 10044730)).setPotionName("potion.resistance").setIconIndex(6, 1);
      fireResistance = (new Potion(12, false, 14981690)).setPotionName("potion.fireResistance").setIconIndex(7, 1);
      waterBreathing = (new Potion(13, false, 3035801)).setPotionName("potion.waterBreathing").setIconIndex(0, 2);
      invisibility = (new Potion(14, false, 8356754)).setPotionName("potion.invisibility").setIconIndex(0, 1);
      blindness = (new Potion(15, true, 2039587)).setPotionName("potion.blindness").setIconIndex(5, 1).setEffectiveness(0.25);
      nightVision = (new Potion(16, false, 2039713)).setPotionName("potion.nightVision").setIconIndex(4, 1);
      hunger = (new Potion(17, true, 5797459)).setPotionName("potion.hunger").setIconIndex(1, 1);
      weakness = (new PotionAttackDamage(18, true, 4738376)).setPotionName("potion.weakness").setIconIndex(5, 0).func_111184_a(SharedMonsterAttributes.attackDamage, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.4, 1);
      poison = (new Potion(19, true, 5149489)).setPotionName("potion.poison").setIconIndex(6, 0).setEffectiveness(0.25);
      wither = (new Potion(20, true, 3484199)).setPotionName("potion.wither").setIconIndex(1, 2).setEffectiveness(0.25);
      field_76434_w = (new PotionHealthBoost(21, false, 16284963)).setPotionName("potion.healthBoost").setIconIndex(2, 2).func_111184_a(SharedMonsterAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0);
      field_76444_x = (new PotionAbsoption(22, false, 2445989)).setPotionName("potion.absorption").setIconIndex(2, 2);
      field_76443_y = (new PotionHealth(23, false, 16262179)).setPotionName("potion.saturation");
      field_76442_z = null;
      field_76409_A = null;
      field_76410_B = null;
      field_76411_C = null;
      field_76405_D = null;
      field_76406_E = null;
      field_76407_F = null;
      field_76408_G = null;
   }
}
