package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemHelmet extends ItemArmor {
   public ItemHelmet(int item_id, Material material, boolean is_chain_mail) {
      super(item_id, material, 0, is_chain_mail);
   }

   public String getArmorType() {
      return "helmet";
   }

   public int getNumComponentsForDurability() {
      return 5;
   }
}
