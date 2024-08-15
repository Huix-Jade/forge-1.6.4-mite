package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;

public class ComponentOfCraftingProductEntry {
   public int subtype_of_component_or_0;
   public ItemStack crafting_product;

   public ComponentOfCraftingProductEntry(int subtype_of_component_or_0, ItemStack crafting_product) {
      this.subtype_of_component_or_0 = subtype_of_component_or_0;
      this.crafting_product = crafting_product;
   }

   public ComponentOfCraftingProductEntry matchAllSubtypes() {
      this.subtype_of_component_or_0 = -1;
      return this;
   }
}
