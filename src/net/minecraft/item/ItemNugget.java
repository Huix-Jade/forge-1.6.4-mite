package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemNugget extends ItemIngot {
   protected ItemNugget(int id, Material material) {
      super(id, material);
      this.setTextureName("nuggets/" + material.name);
      this.setMaxStackSize(64);
      this.setCraftingDifficultyAsComponent(ItemIngot.getCraftingDifficultyAsComponent(material) / 9.0F);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   public ItemNugget getForMaterial(Material material) {
      return material == Material.copper ? copperNugget : (material == Material.silver ? silverNugget : (material == Material.gold ? goldNugget : (material == Material.iron ? ironNugget : (material == Material.mithril ? mithrilNugget : (material == Material.adamantium ? adamantiumNugget : (material == Material.ancient_metal ? ancientMetalNugget : null))))));
   }
}
