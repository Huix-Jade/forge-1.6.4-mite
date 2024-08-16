package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public final class BlockLog extends BlockRotatedPillar implements IBlockWithSubtypes {
   private BlockSubtypes subtypes = new BlockSubtypes(new String[]{"oak", "spruce", "birch", "jungle"});
   private Icon[] end_icons;

   protected BlockLog(int par1) {
      super(par1, Material.wood);
      this.modifyMinHarvestLevel(1);
      this.setHardness(BlockHardness.log);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      byte var7 = 4;
      int var8 = var7 + 1;
      if (par1World.checkChunksExist(par2 - var8, par3 - var8, par4 - var8, par2 + var8, par3 + var8, par4 + var8)) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            for(int var10 = -var7; var10 <= var7; ++var10) {
               for(int var11 = -var7; var11 <= var7; ++var11) {
                  int var12 = par1World.getBlockId(par2 + var9, par3 + var10, par4 + var11);
                  if (Block.blocksList[var12] != null) {
                     Block.blocksList[var12].beginLeavesDecay(par1World, par2 + var9, par3 + var10, par4 + var11);
                  }
               }
            }
         }
      }

   }

   protected Icon getSideIcon(int par1) {
      return this.subtypes.getIcon(par1);
   }

   protected Icon getEndIcon(int par1) {
      return this.end_icons[par1];
   }

   public static int limitToValidMetadata(int par0) {
      return par0 & 3;
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for subtype, bit 4 set if aligned WE, and bit 8 set if aligned NS";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16 && !BitHelper.isBitSet(metadata, 12);
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.subtypes.setIcons(this.registerIcons(par1IconRegister, this.getTextures(), this.getTextureName() + "_"));
      this.end_icons = this.registerIcons(par1IconRegister, this.getTextures(), this.getTextureName() + "_", "_top");
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.stick.itemID, 0, 1, 1.5F) : super.dropBlockAsEntityItem(info);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "log";
   }

   public String[] getTextures() {
      return this.subtypes.getTextures();
   }

   public String[] getNames() {
      return this.subtypes.getNames();
   }

   @Override
   public boolean canSustainLeaves(World world, int x, int y, int z)
   {
      return true;
   }

   @Override
   public boolean isWood(World world, int x, int y, int z)
   {
      return true;
   }
}
