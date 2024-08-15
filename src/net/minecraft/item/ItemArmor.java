package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.mite.Skill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Curse;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Translator;

public abstract class ItemArmor extends Item implements IDamageableItem {
   private static final String[] field_94606_cu = new String[]{"leather_helmet_overlay", "leather_chestplate_overlay", "leather_leggings_overlay", "leather_boots_overlay"};
   public static final String[] field_94603_a = new String[]{"empty_armor_slot_helmet", "empty_armor_slot_chestplate", "empty_armor_slot_leggings", "empty_armor_slot_boots"};
   private static final IBehaviorDispenseItem field_96605_cw = new BehaviorDispenseArmor();
   public final int armorType;
   protected Icon field_94605_cw;
   protected Icon field_94604_cx;
   protected Material effective_material;
   private final boolean is_leather;
   private final boolean is_chain_mail;

   public ItemArmor(int par1, Material material, int par4, boolean is_chain_mail) {
      super(par1, (Material)material, (String)null);
      this.effective_material = material;
      this.setTextureName("armor/" + material.name + (is_chain_mail ? "_chainmail_" : "_") + this.getArmorType());
      this.armorType = par4;
      this.is_leather = this.effective_material == Material.leather;
      this.is_chain_mail = is_chain_mail;
      this.setMaxDamage(this.getMultipliedDurability());
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabCombat);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, field_96605_cw);
      this.setSkillsetThatCanRepairThis(material.isMetal() ? Skill.BLACKSMITHING.id : -1);
   }

   public abstract String getArmorType();

   public final int getMultipliedDurability() {
      float durability = (float)this.getNumComponentsForDurability() * this.effective_material.durability;
      if (!this.is_chain_mail) {
         durability *= 2.0F;
      }

      return (int)durability;
   }

   public int getMaterialProtection() {
      int protection;
      if (this.effective_material == Material.leather) {
         protection = 2;
      } else if (this.effective_material == Material.rusted_iron) {
         protection = 6;
      } else if (this.effective_material == Material.copper) {
         protection = 7;
      } else if (this.effective_material == Material.silver) {
         protection = 7;
      } else if (this.effective_material == Material.gold) {
         protection = 6;
      } else if (this.effective_material != Material.iron && this.effective_material != Material.ancient_metal) {
         if (this.effective_material == Material.mithril) {
            protection = 9;
         } else {
            if (this.effective_material != Material.adamantium) {
               return 0;
            }

            protection = 10;
         }
      } else {
         protection = 8;
      }

      if (this.is_chain_mail) {
         protection -= 2;
      }

      return protection;
   }

   public final float getMultipliedProtection(ItemStack item_stack) {
      float multiplied_protection = (float)(this.getNumComponentsForDurability() * this.getMaterialProtection()) / 24.0F;
      if (item_stack != null && item_stack.hasEnchantment(Enchantment.protection, false)) {
         multiplied_protection += multiplied_protection * item_stack.getEnchantmentLevelFraction(Enchantment.protection) * 0.5F;
      }

      return multiplied_protection;
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      if (par2 > 0) {
         return 16777215;
      } else {
         int var3 = this.getColor(par1ItemStack);
         if (var3 < 0) {
            var3 = 16777215;
         }

         return var3;
      }
   }

   public boolean requiresMultipleRenderPasses() {
      return this.effective_material == Material.leather;
   }

   public int getItemEnchantability() {
      return this.getMaterialForEnchantment().enchantability;
   }

   public Material getArmorMaterial() {
      return this.effective_material;
   }

   public boolean hasColor(ItemStack par1ItemStack) {
      return this.effective_material != Material.leather ? false : (!par1ItemStack.hasTagCompound() ? false : (!par1ItemStack.getTagCompound().hasKey("display") ? false : par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color")));
   }

   public int getColor(ItemStack par1ItemStack) {
      if (this.effective_material != Material.leather) {
         return -1;
      } else {
         NBTTagCompound var2 = par1ItemStack.getTagCompound();
         if (var2 == null) {
            return 10511680;
         } else {
            NBTTagCompound var3 = var2.getCompoundTag("display");
            return var3 == null ? 10511680 : (var3.hasKey("color") ? var3.getInteger("color") : 10511680);
         }
      }
   }

   public Icon getIconFromSubtypeForRenderPass(int par1, int par2) {
      return par2 == 1 ? this.field_94605_cw : super.getIconFromSubtypeForRenderPass(par1, par2);
   }

   public void removeColor(ItemStack par1ItemStack) {
      if (this.effective_material == Material.leather) {
         NBTTagCompound var2 = par1ItemStack.getTagCompound();
         if (var2 != null) {
            NBTTagCompound var3 = var2.getCompoundTag("display");
            if (var3.hasKey("color")) {
               var3.removeTag("color");
            }
         }
      }

   }

   public void func_82813_b(ItemStack par1ItemStack, int par2) {
      if (this.effective_material != Material.leather) {
         throw new UnsupportedOperationException("Can't dye non-leather!");
      } else {
         NBTTagCompound var3 = par1ItemStack.getTagCompound();
         if (var3 == null) {
            var3 = new NBTTagCompound();
            par1ItemStack.setTagCompound(var3);
         }

         NBTTagCompound var4 = var3.getCompoundTag("display");
         if (!var3.hasKey("display")) {
            var3.setCompoundTag("display", var4);
         }

         var4.setInteger("color", par2);
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
      if (this.effective_material == Material.leather) {
         this.field_94605_cw = par1IconRegister.registerIcon(field_94606_cu[this.armorType]);
      }

      this.field_94604_cx = par1IconRegister.registerIcon(field_94603_a[this.armorType]);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.hasCurse(Curse.cannot_wear_armor, true)) {
         return false;
      } else {
         ItemStack item_stack = player.getHeldItemStack();
         int index = EntityLiving.getEquipmentPosition(item_stack) - 1;
         ItemStack var5 = player.getCurrentArmor(index);
         if (var5 == null) {
            if (player.onServer()) {
               player.setCurrentItemOrArmor(index, item_stack.copy());
               player.convertOneOfHeldItem((ItemStack)null);
               player.suppressNextStatIncrement();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static Icon func_94602_b(int par0) {
      switch (par0) {
         case 0:
            return Item.helmetIron.field_94604_cx;
         case 1:
            return Item.plateIron.field_94604_cx;
         case 2:
            return Item.legsIron.field_94604_cx;
         case 3:
            return Item.bootsIron.field_94604_cx;
         default:
            return null;
      }
   }

   public String getTextureFilenamePrefix() {
      return this.effective_material == Material.iron && this.is_chain_mail ? "chainmail" : this.effective_material.name + (this.is_chain_mail ? "_chainmail" : "");
   }

   public final float getDamageFactor(ItemStack item_stack, EntityLivingBase owner) {
      if (owner != null && !owner.isEntityPlayer()) {
         return 0.5F;
      } else if (owner instanceof EntityPlayer && item_stack.getMaxDamage() > 1 && item_stack.getItemDamage() >= item_stack.getMaxDamage() - 1) {
         return 0.0F;
      } else {
         float armor_damage_factor = 2.0F - (float)item_stack.getItemDamage() / (float)item_stack.getItem().getMaxDamage(item_stack) * 2.0F;
         if (armor_damage_factor > 1.0F) {
            armor_damage_factor = 1.0F;
         }

         return armor_damage_factor;
      }
   }

   public final float getProtectionAfterDamageFactor(ItemStack item_stack, EntityLivingBase owner) {
      return this.getMultipliedProtection(item_stack) * this.getDamageFactor(item_stack, owner);
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add("");
         float protection = this.getProtectionAfterDamageFactor(item_stack, player);
         int decimal_places = protection < 1.0F ? 2 : 1;
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.protectionBonus", StringHelper.formatFloat(protection, decimal_places, decimal_places)));
      }

   }

   public boolean hasQuality() {
      return true;
   }

   public static ItemArmor getMatchingArmor(Class item_class, Material armor_material, boolean is_chain_mail) {
      ItemArmor matching_armor = null;

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemArmor && item.getClass() == item_class) {
            ItemArmor armor = (ItemArmor)item;
            if (armor.getArmorMaterial() == armor_material && armor.is_chain_mail == is_chain_mail) {
               if (matching_armor == null) {
                  matching_armor = armor;
               } else {
                  Minecraft.setErrorMessage("getMatchingArmor: more than one item matched " + item_class + ", " + armor_material + ", " + is_chain_mail);
               }
            }
         }
      }

      return matching_armor;
   }

   public Material getMaterialForDurability() {
      return this.getArmorMaterial();
   }

   public boolean isLeather() {
      return this.is_leather;
   }

   public boolean isChainMail() {
      return this.is_chain_mail;
   }

   public boolean isSolidMetal() {
      return this.getArmorMaterial().isMetal() && !this.isChainMail();
   }

   public boolean hasBreakingEffect() {
      return !this.isLeather();
   }

   public static float getTotalArmorProtection(ItemStack[] armors, DamageSource damage_source, boolean include_enchantments, EntityLivingBase owner) {
      float total_defense = 0.0F;
      if (damage_source != null && damage_source.isUnblockable()) {
         return total_defense;
      } else {
         if (damage_source == null || !damage_source.bypassesMundaneArmor()) {
            for(int i = 0; i < armors.length; ++i) {
               ItemStack item_stack = armors[i];
               if (item_stack != null) {
                  Item item = item_stack.getItem();
                  if (item instanceof ItemHorseArmor) {
                     ItemHorseArmor barding = (ItemHorseArmor)item;
                     total_defense += (float)barding.getProtection();
                  } else if (item.isArmor()) {
                     ItemArmor armor = (ItemArmor)item_stack.getItem();
                     total_defense += armor.getProtectionAfterDamageFactor(item_stack, owner);
                  }
               }
            }
         }

         if (include_enchantments) {
            total_defense += EnchantmentProtection.getTotalProtectionOfEnchantments(armors, damage_source, owner);
         }

         total_defense = MathHelper.tryFitToNearestInteger(total_defense, 1.0E-4F);
         return total_defense;
      }
   }

   public static void showHandleChainMailSeparatelyErrorMsg() {
      Minecraft.setErrorMessage("getRepairCost: Chain mail should be handled separately");
      (new Exception()).printStackTrace();
   }

   public final int getRepairCost(boolean for_chain_mail) {
      if (for_chain_mail) {
         showHandleChainMailSeparatelyErrorMsg();
      }

      return this.getNumComponentsForDurability() * 2 / (!this.isLeather() && !for_chain_mail ? 1 : 2);
   }

   public final int getRepairCost() {
      return this.getRepairCost(this.is_chain_mail);
   }

   public boolean hasRepairCost() {
      return this.getRepairCost(false) > 0;
   }

   public float getCoverage() {
      float coverage = (float)this.getNumComponentsForDurability() / 24.0F;
      if (this.is_chain_mail) {
         coverage /= 2.0F;
      }

      return coverage;
   }
}
