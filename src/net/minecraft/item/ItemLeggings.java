package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemLeggings extends ItemArmor {
   public ItemLeggings(int item_id, Material material, boolean is_chain_mail) {
      super(item_id, material, 2, is_chain_mail);
   }

   public String getArmorType() {
      return "leggings";
   }

   public int getNumComponentsForDurability() {
      return 7;
   }
}
