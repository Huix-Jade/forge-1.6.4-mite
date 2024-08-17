package net.minecraft.enchantment;

import java.util.ArrayList;

import com.google.common.collect.ObjectArrays;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public abstract class Enchantment {
   public static final Enchantment[] enchantmentsList = new Enchantment[256];
   public static final Enchantment[] enchantmentsBookList;
   public static final EnchantmentProtection protection;
   public static final EnchantmentProtection fireProtection;
   public static final EnchantmentProtection featherFalling;
   public static final EnchantmentProtection blastProtection;
   public static final EnchantmentProtection projectileProtection;
   public static final Enchantment respiration;
   public static final Enchantment aquaAffinity;
   public static final Enchantment thorns;
   public static final Enchantment sharpness;
   public static final Enchantment smite;
   public static final Enchantment baneOfArthropods;
   public static final Enchantment knockback;
   public static final Enchantment fireAspect;
   public static final Enchantment looting;
   public static final Enchantment efficiency;
   public static final Enchantment silkTouch;
   public static final Enchantment unbreaking;
   public static final Enchantment fortune;
   public static final Enchantment power;
   public static final Enchantment punch;
   public static final Enchantment flame;
   public static final Enchantment arrow_recovery;
   public static final Enchantment stun;
   public static final Enchantment fishing_fortune;
   public static final Enchantment fertility;
   public static final Enchantment tree_felling;
   public static final Enchantment vampiric;
   public static final Enchantment speed;
   public static final Enchantment regeneration;
   public static final Enchantment free_action;
   public static final Enchantment quickness;
   public static final Enchantment true_flight;
   public static final Enchantment poison;
   public static final Enchantment disarming;
   public static final Enchantment piercing;
   public static final Enchantment harvesting;
   public static final Enchantment butchering;
   public static final Enchantment endurance;
   public final int effectId;
   private final int weight;
   public EnumRarity rarity;
   public int difficulty;

   protected Enchantment(int id, EnumRarity rarity, int difficulty) {
      this.effectId = id;
      this.rarity = rarity;
      this.weight = rarity.standard_weight;
      this.difficulty = difficulty;
      if (enchantmentsList[id] != null) {
         throw new IllegalArgumentException("Duplicate enchantment id!");
      } else {
         enchantmentsList[id] = this;
      }
   }

   /**
    * This applies specifically to applying at the enchanting table. The other method { #canApply(ItemStack)}
    * applies for <i>all possible</i> enchantments.
    * @param stack
    * @return
    */
   public boolean canApplyAtEnchantingTable(ItemStack stack)
   {
      return false;
   }

   /**
    * Add to the list of enchantments applicable by the anvil from a book
    *
    * @param enchantment
    */
   public static void addToBookList(Enchantment enchantment)
   {
      ObjectArrays.concat(enchantmentsBookList, enchantment);
   }

   /**
    * Is this enchantment allowed to be enchanted on books via Enchantment Table
    * @return false to disable the vanilla feature
    */
   public boolean isAllowedOnBooks()
   {
      return true;
   }

   public int getWeight() {
      return this.weight;
   }

   public int getNumLevels() {
      return 5;
   }

   public boolean hasLevels() {
      return this.getNumLevels() > 1;
   }

   public boolean canApplyTogether(Enchantment par1Enchantment) {
      return this != par1Enchantment;
   }

   public final String getName() {
      return "enchantment." + this.getNameSuffix();
   }

   public abstract String getNameSuffix();

   public String getTranslatedName(Item item) {
      return StatCollector.translateToLocal(this.getName());
   }

   public String getTranslatedName(int level, ItemStack item_stack) {
      String var2 = this.getTranslatedName(item_stack == null ? null : item_stack.getItem());
      return !this.hasLevels() ? var2 : var2 + " " + StatCollector.translateToLocal("enchantment.level." + level);
   }

   public abstract boolean canEnchantItem(Item var1);

   public static int getExperienceCost(int enchantment_levels) {
      return enchantment_levels * 100;
   }

   public abstract boolean isOnCreativeTab(CreativeTabs var1);

   public static Enchantment get(int id) {
      return enchantmentsList[id];
   }

   public String toString() {
      return StatCollector.translateToLocal(this.getName());
   }

   public int getLevelFromEnchantmentLevels(int enchantment_levels) {
      if (this.hasLevels()) {
         if (enchantment_levels <= this.difficulty - 10) {
            return 0;
         } else {
            int level = (enchantment_levels + this.difficulty - 1) / this.difficulty;
            return level >= 1 && level <= this.getNumLevels() ? level : 0;
         }
      } else {
         return enchantment_levels >= this.difficulty * 2 + 1 && enchantment_levels <= this.difficulty * 3 ? 1 : 0;
      }
   }

   public int getMinEnchantmentLevelsCost(int level) {
      if (level < 1) {
         Minecraft.setErrorMessage("getMinEnchantmentLevelsCost: level < 1 for " + this);
         return 0;
      } else {
         if (!this.hasLevels()) {
            if (level != 3) {
               Minecraft.setErrorMessage("getMinEnchantmentLevelsCost: level must be 3 for " + this);
               return 0;
            }
         } else if (level > this.getNumLevels()) {
            Minecraft.setErrorMessage("getMinEnchantmentLevelsCost: level too high for " + this);
            return 0;
         }

         return Math.max(this.difficulty - 10, 0) + this.difficulty * (level - 1) + 1;
      }
   }

   public int getMinEnchantmentLevelsCost() {
      return this.getMinEnchantmentLevelsCost(3);
   }

   public int getLevel(ItemStack item_stack) {
      return EnchantmentHelper.getEnchantmentLevel(this.effectId, item_stack);
   }

   public float getLevelFraction(ItemStack item_stack) {
      return EnchantmentHelper.getEnchantmentLevelFraction(this, item_stack);
   }

   static {
      protection = new EnchantmentProtection(0, EnumRarity.common, 10);
      fireProtection = new EnchantmentProtection(1, EnumRarity.uncommon, 10);
      featherFalling = new EnchantmentProtection(2, EnumRarity.uncommon, 10);
      blastProtection = new EnchantmentProtection(3, EnumRarity.uncommon, 10);
      projectileProtection = new EnchantmentProtection(4, EnumRarity.uncommon, 10);
      respiration = new EnchantmentOxygen(5, EnumRarity.rare, 10);
      aquaAffinity = new EnchantmentWaterWorker(6, EnumRarity.rare, 10);
      thorns = new EnchantmentThorns(7, EnumRarity.rare, 20);
      sharpness = new EnchantmentDamage(16, EnumRarity.common, 10);
      smite = new EnchantmentDamage(17, EnumRarity.uncommon, 10);
      baneOfArthropods = new EnchantmentDamage(18, EnumRarity.uncommon, 10);
      knockback = new EnchantmentKnockback(19, EnumRarity.uncommon, 10);
      fireAspect = new EnchantmentFireAspect(20, EnumRarity.rare, 20);
      looting = new EnchantmentLootBonus(21, EnumRarity.uncommon, 10);
      efficiency = new EnchantmentDigging(32, EnumRarity.common, 10);
      silkTouch = new EnchantmentUntouching(33, EnumRarity.rare, 10);
      unbreaking = new EnchantmentDurability(34, EnumRarity.uncommon, 10);
      fortune = new EnchantmentLootBonus(35, EnumRarity.rare, 10);
      power = new EnchantmentArrowDamage(48, EnumRarity.common, 10);
      punch = new EnchantmentArrowKnockback(49, EnumRarity.uncommon, 10);
      flame = new EnchantmentArrowFire(50, EnumRarity.rare, 20);
      arrow_recovery = new EnchantmentArrowRecovery(51, EnumRarity.uncommon, 10);
      stun = new EnchantmentStun(80, EnumRarity.uncommon, 15);
      fishing_fortune = new EnchantmentFishingFortune(81, EnumRarity.common, 10);
      fertility = new EnchantmentFertility(82, EnumRarity.uncommon, 10);
      tree_felling = new EnchantmentTreeFelling(83, EnumRarity.uncommon, 10);
      vampiric = new EnchantmentVampiric(84, EnumRarity.epic, 20);
      speed = new EnchantmentSpeed(85, EnumRarity.rare, 10);
      regeneration = new EnchantmentRegeneration(86, EnumRarity.rare, 20);
      free_action = new EnchantmentFreeAction(87, EnumRarity.uncommon, 10);
      quickness = new EnchantmentQuickness(88, EnumRarity.uncommon, 10);
      true_flight = new EnchantmentTrueFlight(89, EnumRarity.common, 10);
      poison = new EnchantmentPoison(90, EnumRarity.rare, 10);
      disarming = new EnchantmentDisarming(91, EnumRarity.rare, 10);
      piercing = new EnchantmentPiercing(92, EnumRarity.rare, 10);
      harvesting = new EnchantmentHarvesting(93, EnumRarity.uncommon, 10);
      butchering = new EnchantmentButchering(94, EnumRarity.uncommon, 10);
      endurance = new EnchantmentEndurance(95, EnumRarity.uncommon, 10);
      ArrayList var0 = new ArrayList();
      Enchantment[] var1 = enchantmentsList;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Enchantment var4 = var1[var3];
         if (var4 != null) {
            var0.add(var4);
         }
      }

      enchantmentsBookList = (Enchantment[])((Enchantment[])var0.toArray(new Enchantment[0]));
   }
}
