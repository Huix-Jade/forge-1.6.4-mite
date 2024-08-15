package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class BlockStoneBrick extends Block {
   public static final String[] STONE_BRICK_TYPES = new String[]{"default", "mossy", "cracked", "chiseled"};
   public static final String[] field_94407_b = new String[]{null, "mossy", "cracked", "carved"};
   private Icon[] field_94408_c;

   public BlockStoneBrick(int par1) {
      super(par1, Material.stone, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      if (par2 < 0 || par2 >= field_94407_b.length) {
         par2 = 0;
      }

      return this.field_94408_c[par2];
   }

   public String getMetadataNotes() {
      return "0=Regular, 1=Mossy, 2=Cracked, 3=Chiseled";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_94408_c = new Icon[field_94407_b.length];

      for(int var2 = 0; var2 < this.field_94408_c.length; ++var2) {
         String var3 = this.getTextureName();
         if (field_94407_b[var2] != null) {
            var3 = var3 + "_" + field_94407_b[var2];
         }

         this.field_94408_c[var2] = par1IconRegister.registerIcon(var3);
      }

   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, cobblestone) : super.dropBlockAsEntityItem(info);
   }
}
