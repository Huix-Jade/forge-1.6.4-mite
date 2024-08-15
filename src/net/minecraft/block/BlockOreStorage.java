package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemIngot;
import net.minecraft.item.ItemRock;

public class BlockOreStorage extends Block {
   public BlockOreStorage(int par1, Material material) {
      super(par1, material, new BlockConstants());
      this.modifyMinHarvestLevel(1);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setMaxStackSize(4);
      this.setHardnessRelativeToWood(BlockHardness.log);
   }

   public float getCraftingDifficultyAsComponent(int metadata) {
      return this.blockMaterial.isMetal() ? ItemIngot.getCraftingDifficultyAsComponent(this.blockMaterial) * 9.0F : ItemRock.getCraftingDifficultyAsComponent(this.blockMaterial) * (float)(this.blockMaterial == Material.quartz ? 4 : 9);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasExploded()) {
         if (this == Block.blockEmerald) {
            return this.dropBlockAsEntityItem(info, Item.shardEmerald.itemID, 0, 9, 1.0F);
         }

         if (this == Block.blockDiamond) {
            return this.dropBlockAsEntityItem(info, Item.shardDiamond.itemID, 0, 9, 1.0F);
         }
      }

      return super.dropBlockAsEntityItem(info);
   }
}
