package net.minecraft.item;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Translator;

public class ItemHorseArmor extends Item {
   public Material effective_material;

   protected ItemHorseArmor(int id, Material material) {
      super(id, material, "armor/horse/" + material.name);
      this.effective_material = material;
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public Material getArmorMaterial() {
      return this.effective_material;
   }

   public int getProtection() {
      if (this.effective_material == Material.copper) {
         return 4;
      } else if (this.effective_material == Material.silver) {
         return 4;
      } else if (this.effective_material == Material.gold) {
         return 3;
      } else if (this.effective_material != Material.iron && this.effective_material != Material.ancient_metal) {
         if (this.effective_material == Material.mithril) {
            return 6;
         } else if (this.effective_material == Material.adamantium) {
            return 7;
         } else {
            Minecraft.setErrorMessage("getProtection: unhandled armor type");
            return 0;
         }
      } else {
         return 5;
      }
   }

   public float getCoverage() {
      return 1.0F;
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         info.add("");
         float protection = (float)this.getProtection();
         int decimal_places = protection < 1.0F ? 2 : 1;
         info.add(EnumChatFormatting.BLUE + Translator.getFormatted("item.tooltip.protectionBonus", StringHelper.formatFloat(protection, decimal_places, decimal_places)));
      }

   }
}
