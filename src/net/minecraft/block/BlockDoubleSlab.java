package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockDoubleSlab extends Block {
   private BlockSlab single_slab;

   public BlockDoubleSlab(int id, BlockSlab single_slab) {
      super(id, single_slab.blockMaterial, new BlockConstants());
      if (single_slab != this.getSingleSlab()) {
         Minecraft.setErrorMessage("BlockDoubleSlab: single slab mismatch");
      }

      this.single_slab = single_slab;
      this.setUnlocalizedName("slab.group" + single_slab.getGroup());
   }

   private BlockSlab getSingleSlab() {
      if (this.single_slab == null) {
         this.single_slab = (BlockSlab)Block.getBlock(this.blockID + 1);
      }

      return this.single_slab;
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public Icon getIcon(int side, int metadata) {
      return this.single_slab.getIcon(side, metadata);
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used for subtype";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata < 8 && this.getSingleSlab().isValidMetadata(metadata);
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return this.getSingleSlab().getBlockSubtypeUnchecked(metadata);
   }

   public String getFullSlabName(int metadata) {
      return this.single_slab.getFullSlabName(metadata);
   }

   public int idPicked(World world, int x, int y, int z) {
      return this.single_slab.blockID;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasExploded()) {
         Object model_block;
         if (this.blockMaterial == Material.wood) {
            model_block = Block.wood;
         } else if (this.blockMaterial == Material.stone) {
            int subtype = this.getItemSubtype(info.getMetadata());
            if (subtype == 0) {
               model_block = Block.stone;
            } else if (subtype == 1) {
               model_block = Block.sandStone;
            } else if (subtype == 3) {
               model_block = Block.cobblestone;
            } else if (subtype == 5) {
               model_block = Block.stoneBrick;
            } else if (subtype == 6) {
               model_block = Block.netherrack;
            } else if (subtype == 7) {
               model_block = Block.blockNetherQuartz;
            } else {
               model_block = null;
            }

            info.setMetadata(0);
         } else if (this.blockMaterial == Material.obsidian) {
            model_block = Block.obsidian;
         } else {
            Minecraft.setErrorMessage("dropBlockAsEntityItem: blockMaterial " + this.blockMaterial + " not handled");
            model_block = null;
         }

         return model_block == null ? 0 : ((Block)model_block).dropBlockAsEntityItem(info);
      } else {
         return this.dropBlockAsEntityItem(info, this.createStackedBlock(info.getMetadata()));
      }
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "double";
   }

   public float getBlockHardness(int metadata) {
      return this.single_slab.getBlockHardness(metadata) * 2.0F;
   }

   public int getMinHarvestLevel(int metadata) {
      return this.single_slab.getMinHarvestLevel(metadata);
   }

   public ItemStack createStackedBlock(int metadata) {
      ItemStack item_stack = this.single_slab.createStackedBlock(metadata);
      item_stack.stackSize *= 2;
      return item_stack;
   }

   public void getItemStacks(int id, CreativeTabs creative_tabs, List list) {
      if (creative_tabs == null) {
         super.getItemStacks(id, creative_tabs, list);
      }

   }
}
