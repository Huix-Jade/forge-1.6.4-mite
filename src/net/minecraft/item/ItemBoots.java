package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemBoots extends ItemArmor {
   public ItemBoots(int item_id, Material material, boolean is_chain_mail) {
      super(item_id, material, 3, is_chain_mail);
   }

   public String getArmorType() {
      return "boots";
   }

   public int getNumComponentsForDurability() {
      return 4;
   }
}
