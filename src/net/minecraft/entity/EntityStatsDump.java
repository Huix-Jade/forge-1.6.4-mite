package net.minecraft.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet94CreateFile;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumLevelBonus;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.StringHelper;
import net.minecraft.util.StringUtils;

public final class EntityStatsDump {
   private static String newline = new String(System.getProperty("line.separator").getBytes());

   private static String customFloatFormat(float f) {
      return f < 1.0F && (double)f != 0.0 ? StringHelper.formatFloat(f, 2, 2) : StringHelper.formatFloat(f, 1, 1);
   }

   private static String getItemStackDescriptorForStatsFile(ItemStack item_stack) {
      if (item_stack == null) {
         return "(nothing)";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append(item_stack.getNameForReferenceFile());
         if (item_stack.stackSize != 1) {
            sb.append(" (" + item_stack.stackSize + ")");
         }

         String[] notes = new String[16];
         EnumQuality quality = item_stack.getQuality();
         if (quality != null && !quality.isAverage()) {
            StringHelper.addToStringArray(quality.getDescriptor(), notes);
         }

         if (item_stack.isItemEnchanted()) {
            Map enchantments = EnchantmentHelper.getEnchantmentsMap(item_stack);
            Set set = enchantments.entrySet();
            Iterator i = set.iterator();

            while(i.hasNext()) {
               Map.Entry e = (Map.Entry)i.next();
               Enchantment enchantment = Enchantment.get((Integer)e.getKey());
               int level = (Integer)e.getValue();
               StringHelper.addToStringArray(enchantment.getTranslatedName(level, item_stack), notes);
            }
         }

         if (notes[0] != null) {
            sb.append(" {");
            sb.append(StringHelper.implode(notes, ", ", true, false));
            sb.append("}");
         }

         if (item_stack.isItemDamaged()) {
            sb.append(" [" + item_stack.getRemainingDurability() + "/" + item_stack.getMaxDamage() + "]");
         }

         return sb.toString();
      }
   }

   private static void appendSectionProtection(EntityLivingBase entity_living_base, StringBuilder sb) {
      sb.append("Protection" + newline);
      sb.append(" vs Generic: " + customFloatFormat(entity_living_base.getTotalProtection(DamageSource.causeMobDamage((EntityLivingBase)null))) + newline);
      sb.append(" vs Falls: " + customFloatFormat(entity_living_base.getTotalProtection(DamageSource.fall)) + newline);
      sb.append(" vs Fire: " + customFloatFormat(entity_living_base.getTotalProtection(DamageSource.onFire)) + newline);
      sb.append(" vs Explosions: " + customFloatFormat(entity_living_base.getTotalProtection((new DamageSource("explosion")).setExplosion())) + newline);
      EntityArrow entity_arrow = new EntityArrow(entity_living_base.worldObj);
      entity_arrow.item_arrow = Item.arrowFlint;
      sb.append(" vs Projectiles: " + customFloatFormat(entity_living_base.getTotalProtection(DamageSource.causeArrowDamage(entity_arrow, (Entity)null))) + newline + newline);
   }

   private static void appendSectionResistance(EntityLivingBase entity_living_base, StringBuilder sb) {
      sb.append("Resistance" + newline);
      sb.append(" vs Poison: " + Math.round(entity_living_base.getResistanceToPoison() * 100.0F) + "%" + newline);
      sb.append(" vs Paralysis: " + Math.round(entity_living_base.getResistanceToParalysis() * 100.0F) + "%" + newline);
      sb.append(" vs Drain: " + Math.round(entity_living_base.getResistanceToDrain() * 100.0F) + "%" + newline);
      sb.append(" vs Shadow: " + Math.round(entity_living_base.getResistanceToShadow() * 100.0F) + "%" + newline + newline);
   }

   private static void appendSectionPotionEffects(EntityLivingBase entity_living_base, StringBuilder sb) {
      sb.append("Potion Effects" + newline);
      if (!entity_living_base.hasActivePotionEffects()) {
         sb.append(" (none)" + newline + newline);
      } else {
         Collection potion_effects = entity_living_base.getActivePotionEffects();
         Iterator i = potion_effects.iterator();

         while(i.hasNext()) {
            PotionEffect potion_effect = (PotionEffect)i.next();
            Potion potion = Potion.get(potion_effect.getPotionID());
            int level = potion_effect.getAmplifier();
            sb.append(" " + I18n.getString(potion.getName()));
            sb.append(" " + StringHelper.getRomanNumeral(level + 1));
            Map attribute_modifiers = potion.func_111186_k();
            Iterator iterator = attribute_modifiers.entrySet().iterator();
            String[] effect_descriptions = new String[16];

            while(iterator.hasNext()) {
               Map.Entry entry = (Map.Entry)iterator.next();
               Attribute attribute = (Attribute)entry.getKey();
               AttributeModifier modifier = (AttributeModifier)entry.getValue();
               modifier = new AttributeModifier(modifier.getID(), modifier.getName(), potion.func_111183_a(level, modifier), modifier.getOperation());
               String effect_details = ItemPotion.getEffectDetails(attribute.getAttributeUnlocalizedName(), modifier);
               if (effect_details != null) {
                  StringHelper.addToStringArray(StringUtils.stripControlCodes(effect_details), effect_descriptions);
               }
            }

            if (effect_descriptions[0] != null) {
               sb.append(" (" + StringHelper.implode(effect_descriptions, ", ", true, false) + ")");
            }

            sb.append(" [" + Potion.getDurationString(potion_effect) + "]");
            sb.append(newline);
         }

         sb.append(newline);
      }
   }

   private static void appendSectionEquipment(EntityLivingBase entity_living_base, StringBuilder sb) {
      sb.append("Equipment" + newline);
      if (entity_living_base.getHeldItemStack() != null) {
         sb.append(" Held: " + getItemStackDescriptorForStatsFile(entity_living_base.getHeldItemStack()) + newline);
      }

      ItemStack[] worn_items = entity_living_base.getWornItems();
      if (worn_items != null) {
         for(int i = 0; i < worn_items.length; ++i) {
            sb.append(" Worn[" + i + "]: " + getItemStackDescriptorForStatsFile(worn_items[i]) + newline);
         }
      }

      sb.append(newline);
   }

   private static void appendSectionInventory(EntityLivingBase entity_living_base, StringBuilder sb) {
      sb.append("Inventory" + newline);
      int i;
      if (entity_living_base.isEntityPlayer()) {
         EntityPlayer player = entity_living_base.getAsPlayer();
         boolean none = true;

         for(i = 0; i < player.inventory.mainInventory.length; ++i) {
            ItemStack item_stack = player.inventory.mainInventory[i];
            if (item_stack != null) {
               none = false;
               sb.append(" " + (i < 9 ? "Hotbar" : "Extended") + "[" + i + "]: " + getItemStackDescriptorForStatsFile(item_stack) + newline);
            }
         }

         if (none) {
            sb.append(" (none)" + newline);
         }
      } else if (entity_living_base instanceof EntityLiving) {
         EntityLiving entity_living = entity_living_base.getAsEntityLiving();
         ItemStack[] contained_items = entity_living.getContainedItems();
         if (contained_items == null) {
            boolean has_inventory = false;
            if (entity_living_base instanceof EntityLongdeadGuardian) {
               EntityLongdeadGuardian guardian = (EntityLongdeadGuardian)entity_living_base;
               if (guardian.getStowedItemStack() != null) {
                  has_inventory = true;
                  sb.append(" Stowed[0]: " + getItemStackDescriptorForStatsFile(guardian.getStowedItemStack()) + newline);
               }
            }

            if (!has_inventory) {
               sb.append(" (none)" + newline);
            }
         } else {
            for(i = 0; i < contained_items.length; ++i) {
               sb.append(" Contained[" + i + "]: " + getItemStackDescriptorForStatsFile(contained_items[i]) + newline);
            }
         }
      } else {
         sb.append(" (Don't know how to handle)" + newline);
      }

   }

   private static String getMeleeDamageString(EntityLivingBase entity_living_base) {
      float total_melee_damage;
      if (entity_living_base.isEntityPlayer()) {
         total_melee_damage = entity_living_base.getAsPlayer().calcRawMeleeDamageVs((Entity)null, false, false);
      } else if (entity_living_base.hasEntityAttribute(SharedMonsterAttributes.attackDamage)) {
         total_melee_damage = (float)entity_living_base.getEntityAttributeValue(SharedMonsterAttributes.attackDamage);
      } else {
         total_melee_damage = 0.0F;
      }

      StringBuilder sb = new StringBuilder();
      sb.append("Melee Damage: " + customFloatFormat(total_melee_damage));
      if (total_melee_damage > 0.0F) {
         ItemStack held_item_stack = entity_living_base.getHeldItemStack();
         float damage_from_held_item = held_item_stack == null ? 0.0F : held_item_stack.getMeleeDamageBonus();
         if (entity_living_base.isEntityPlayer()) {
            EntityPlayer player = entity_living_base.getAsPlayer();
            int level_modifier_melee = Math.round(player.getLevelModifier(EnumLevelBonus.MELEE_DAMAGE) * 100.0F);
            float base_melee_damage = (float)entity_living_base.getEntityAttributeBaseValue(SharedMonsterAttributes.attackDamage);
            String level_modifier_melee_string = level_modifier_melee == 0 ? "" : " + " + level_modifier_melee + "%";
            if (damage_from_held_item != 0.0F) {
               sb.append(" (" + base_melee_damage + " + " + held_item_stack.getDisplayName() + ")" + level_modifier_melee_string);
            } else if (level_modifier_melee != 0) {
               sb.append(" (" + base_melee_damage + level_modifier_melee_string + ")");
            }
         } else if (damage_from_held_item > 0.0F) {
            sb.append(" (" + customFloatFormat(total_melee_damage - damage_from_held_item) + " + " + held_item_stack.getDisplayName() + ")");
         }
      }

      return sb.toString();
   }

   public static StringBuilder getStatsDump(String header, EntityLivingBase entity_living_base) {
      StringBuilder sb = new StringBuilder();
      if (entity_living_base.onClient()) {
         sb.append("* GENERATED ON CLIENT *" + newline + newline);
      }

      if (header == null) {
         header = entity_living_base.getEntityName();
      }

      sb.append(header + newline);
      sb.append(StringHelper.repeat("-", header.length()) + newline);
      sb.append("UUID: " + entity_living_base.getUniqueIDSilent() + newline + newline);
      EntityPlayer player = entity_living_base.isEntityPlayer() ? entity_living_base.getAsPlayer() : null;
      if (player != null) {
         int level_modifier_harvesting = Math.round(player.getLevelModifier(EnumLevelBonus.HARVESTING) * 100.0F);
         int level_modifier_crafting = Math.round(player.getLevelModifier(EnumLevelBonus.CRAFTING) * 100.0F);
         int level_modifier_melee = Math.round(player.getLevelModifier(EnumLevelBonus.MELEE_DAMAGE) * 100.0F);
         sb.append("Level: " + player.getExperienceLevel() + " (");
         sb.append((level_modifier_harvesting < 0 ? "" : "+") + level_modifier_harvesting + "% harvesting, ");
         sb.append((level_modifier_crafting < 0 ? "" : "+") + level_modifier_crafting + "% crafting, ");
         sb.append((level_modifier_melee < 0 ? "" : "+") + level_modifier_melee + "% melee)" + newline);
         sb.append("XP: " + player.experience + newline + newline);
         sb.append("Health: " + StringHelper.formatFloat(player.getHealth(), 1, 1) + "/" + player.getMaxHealth() + newline + newline);
         sb.append("Satiation: " + player.getSatiation() + "/" + player.getSatiationLimit() + newline);
         sb.append("Nutrition: " + player.getNutrition() + "/" + player.getNutritionLimit() + newline + newline);
         int protein;
         int phytonutrients;
         if (player instanceof EntityPlayerMP) {
            protein = player.getAsEntityPlayerMP().getProtein();
            phytonutrients = player.getAsEntityPlayerMP().getPhytonutrients();
         } else {
            phytonutrients = 0;
            protein = 0;
         }

         sb.append("Protein: " + protein + " (" + 100 * protein / 160000 + "%)" + newline);
         sb.append("Phytonutrients: " + phytonutrients + " (" + 100 * phytonutrients / 160000 + "%)" + newline + newline);
         sb.append(getMeleeDamageString(player) + newline + newline);
      } else {
         sb.append("Health: " + StringHelper.formatFloat(entity_living_base.getHealth(), 1, 1) + "/" + entity_living_base.getMaxHealth() + newline + newline);
         sb.append(getMeleeDamageString(entity_living_base) + newline + newline);
      }

      appendSectionProtection(entity_living_base, sb);
      appendSectionResistance(entity_living_base, sb);
      appendSectionPotionEffects(entity_living_base, sb);
      appendSectionEquipment(entity_living_base, sb);
      appendSectionInventory(entity_living_base, sb);
      if (entity_living_base.ridingEntity instanceof EntityLivingBase) {
         EntityLivingBase riding_entity = entity_living_base.ridingEntity.getAsEntityLivingBase();
         sb.append(newline + getStatsDump("Mount (" + riding_entity.getEntityName() + ")", riding_entity));
      }

      return sb;
   }

   public static Packet94CreateFile generatePacketFor(EntityLivingBase entity_living_base) {
      byte[] content = getStatsDump((String)null, entity_living_base).toString().getBytes();
      StringBuilder filepath = new StringBuilder("MITE/stats/dump/");
      if (entity_living_base.isEntityPlayer()) {
         filepath.append(entity_living_base.getEntityName());
      } else {
         filepath.append(entity_living_base.getEntityName());
         filepath.append("/");
         filepath.append(entity_living_base.getUniqueIDSilent());
      }

      filepath.append(".txt");
      return (new Packet94CreateFile(filepath.toString(), content)).setOptions(1, true);
   }
}
