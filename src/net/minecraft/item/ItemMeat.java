package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemMeat extends ItemFood {
   public boolean is_cooked;

   public ItemMeat(int id, int satiation, int nutrition, boolean has_essential_fats, boolean is_cooked, String texture) {
      super(id, Material.meat, satiation, nutrition, true, has_essential_fats, false, texture);
      this.is_cooked = is_cooked;
      this.setAnimalProduct();
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this != fishLargeRaw && this != fishLargeCooked ? null : "large";
   }

   public float getCompostingValue() {
      if (this == wormRaw) {
         return 0.0F;
      } else if (this == wormCooked) {
         return 0.1F;
      } else {
         return this.is_cooked ? Item.getItem(this.itemID - 1).getCompostingValue() : super.getCompostingValue();
      }
   }
}
