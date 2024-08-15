package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ItemPickaxe extends ItemTool {
   protected ItemPickaxe(int par1, Material material) {
      super(par1, material);
      this.addMaterialsEffectiveAgainst(new Material[]{Material.adamantium, Material.ancient_metal, Material.circuits, Material.clay, Material.coal, Material.copper, Material.coral, Material.diamond, Material.emerald, Material.glass, Material.gold, Material.hardened_clay, Material.ice, Material.iron, Material.mithril, Material.netherrack, Material.obsidian, Material.quartz, Material.redstone, Material.stone, Material.rusted_iron, Material.silver});
   }

   public String getToolType() {
      return "pickaxe";
   }

   public float getBaseDamageVsEntity() {
      return 2.0F;
   }

   public int getNumComponentsForDurability() {
      return 3;
   }

   public float getBaseDecayRateForBreakingBlock(Block block) {
      return 1.0F;
   }

   public float getBaseDecayRateForAttackingEntity(ItemStack item_stack) {
      return 1.0F;
   }

   public Class[] getSimilarClasses() {
      return this.spliceClassArrays(new Class[]{ItemWarHammer.class}, ItemTool.weapon_classes);
   }
}
