package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class BlockWeb extends Block {
   public BlockWeb(int par1) {
      super(par1, Material.web, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      par5Entity.setInWeb();
   }

   public int getRenderType() {
      return 1;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasHarvested()) {
         Item item = info.getHarvesterItem();
         if (item instanceof ItemSword || item instanceof ItemShears) {
            return super.dropBlockAsEntityItem(info, Item.silk);
         }
      }

      return 0;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
