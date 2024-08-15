package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockQuartz extends Block {
   public static final String[] quartzBlockTypes = new String[]{"default", "chiseled", "lines"};
   private static final String[] quartzBlockTextureTypes = new String[]{"side", "chiseled", "lines", null, null};
   private Icon[] quartzblockIcons;
   private Icon quartzblock_chiseled_top;
   private Icon quartzblock_lines_top;
   private Icon quartzblock_top;
   private Icon quartzblock_bottom;

   public BlockQuartz(int par1) {
      super(par1, Material.quartz, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      if (par2 != 2 && par2 != 3 && par2 != 4) {
         if (par1 != 1 && (par1 != 0 || par2 != 1)) {
            if (par1 == 0) {
               return this.quartzblock_bottom;
            } else {
               if (par2 < 0 || par2 >= this.quartzblockIcons.length) {
                  par2 = 0;
               }

               return this.quartzblockIcons[par2];
            }
         } else {
            return par2 == 1 ? this.quartzblock_chiseled_top : this.quartzblock_top;
         }
      } else {
         return par2 != 2 || par1 != 1 && par1 != 0 ? (par2 != 3 || par1 != 5 && par1 != 4 ? (par2 != 4 || par1 != 2 && par1 != 3 ? this.quartzblockIcons[par2] : this.quartzblock_lines_top) : this.quartzblock_lines_top) : this.quartzblock_lines_top;
      }
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      int metadata = super.getMetadataForPlacement(world, x, y, z, item_stack, entity, face, offset_x, offset_y, offset_z);
      if (metadata != 2) {
         return metadata;
      } else {
         if (face.isEastOrWest()) {
            metadata = 3;
         } else if (face.isNorthOrSouth()) {
            metadata = 4;
         }

         return metadata;
      }
   }

   public String getMetadataNotes() {
      return "0=Regular, 1=Chiseled, 2=Pillar UD, 3=Pillar EW, 4=Pillar NS";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 5;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata > 1 && metadata < 5 ? 2 : metadata;
   }

   public int getRenderType() {
      return 39;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.quartzblockIcons = new Icon[quartzBlockTextureTypes.length];

      for(int var2 = 0; var2 < this.quartzblockIcons.length; ++var2) {
         if (quartzBlockTextureTypes[var2] == null) {
            this.quartzblockIcons[var2] = this.quartzblockIcons[var2 - 1];
         } else {
            this.quartzblockIcons[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_" + quartzBlockTextureTypes[var2]);
         }
      }

      this.quartzblock_top = par1IconRegister.registerIcon(this.getTextureName() + "_" + "top");
      this.quartzblock_chiseled_top = par1IconRegister.registerIcon(this.getTextureName() + "_" + "chiseled_top");
      this.quartzblock_lines_top = par1IconRegister.registerIcon(this.getTextureName() + "_" + "lines_top");
      this.quartzblock_bottom = par1IconRegister.registerIcon(this.getTextureName() + "_" + "bottom");
   }

   public float getCraftingDifficultyAsComponent(int metadata) {
      return ItemRock.getCraftingDifficultyAsComponent(Material.quartz) * 4.0F;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.netherQuartz.itemID, 0, 6, 0.5F) : super.dropBlockAsEntityItem(info);
   }

   public EnumDirection getDirectionFacing(int metadata) {
      return metadata == 3 ? EnumDirection.WEST : (metadata == 4 ? EnumDirection.NORTH : (metadata == 5 ? EnumDirection.DOWN : null));
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return direction.isEastOrWest() ? 3 : (direction.isNorthOrSouth() ? 4 : (direction.isUpOrDown() ? 5 : -1));
   }
}
