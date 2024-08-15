package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemCuirass extends ItemArmor {
   public ItemCuirass(int item_id, Material material, boolean is_chain_mail) {
      super(item_id, material, 1, is_chain_mail);
   }

   public String getArmorType() {
      return "chestplate";
   }

   public int getNumComponentsForDurability() {
      return 8;
   }
}
