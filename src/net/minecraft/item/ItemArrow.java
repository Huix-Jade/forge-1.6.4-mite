package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.DispenserBehaviorArrow;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Translator;

public class ItemArrow extends Item {
   public static final Material[] material_types;
   public final int type_index;
   public final Material arrowhead_material;

   public ItemArrow(int par1, Material arrowhead_material) {
      super(par1, Material.wood, "arrows/" + arrowhead_material.name + "_arrow");
      this.addMaterial(new Material[]{this.arrowhead_material = arrowhead_material});
      this.type_index = getArrowIndex(arrowhead_material);
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   public static int getArrowIndex(Material arrowhead_material) {
      for(int i = 0; i < material_types.length; ++i) {
         if (material_types[i] == arrowhead_material) {
            return i;
         }
      }

      return -1;
   }

   public int getArrowIndex() {
      return this.type_index;
   }

   public float getChanceOfRecovery() {
      if (this == arrowFlint) {
         return 0.3F;
      } else if (this == arrowObsidian) {
         return 0.4F;
      } else if (this == arrowCopper) {
         return 0.6F;
      } else if (this == arrowSilver) {
         return 0.6F;
      } else if (this == arrowRustedIron) {
         return 0.5F;
      } else if (this == arrowGold) {
         return 0.5F;
      } else if (this == arrowIron) {
         return 0.7F;
      } else if (this != arrowMithril && this != arrowAncientMetal) {
         return this == arrowAdamantium ? 0.9F : 0.3F;
      } else {
         return 0.8F;
      }
   }

   public boolean addToEntityContainedItemsWithChance(Random rand, EntityLivingBase entity) {
      if (rand.nextFloat() < this.getChanceOfRecovery()) {
         entity.addContainedItem(this);
         return true;
      } else {
         return false;
      }
   }

   public float getMaterialDamageVsEntity() {
      return this.arrowhead_material.getDamageVsEntity();
   }

   public Material getArrowheadMaterial() {
      return this.arrowhead_material;
   }

   public float getDamage() {
      return 0.5F + this.getMaterialDamageVsEntity() * 0.5F;
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add("");
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.missileDamage", (int)this.getMaterialDamageVsEntity()));
         if (this.arrowhead_material == Material.silver) {
            info.add(EnumChatFormatting.WHITE + Translator.get("item.tooltip.bonusVsUndead"));
         }

         info.add(EnumChatFormatting.GRAY + Translator.getFormatted("item.tooltip.missileRecovery", (int)(this.getChanceOfRecovery() * 100.0F)));
      }

   }

   public IBehaviorDispenseItem getDispenserBehavior() {
      return new DispenserBehaviorArrow(this);
   }

   static {
      material_types = new Material[]{Material.flint, Material.obsidian, Material.copper, Material.silver, Material.rusted_iron, Material.gold, Material.iron, Material.mithril, Material.adamantium, Material.ancient_metal};
   }
}
