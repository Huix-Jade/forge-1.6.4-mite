package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
   public BlockSlab(int id, Material material) {
      super(id, material, new BlockConstants());
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
      this.setMaxStackSize(8);
      this.setLightOpacity(255);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setUnlocalizedName("slab.group" + this.getGroup());
   }

   public abstract int getGroup();

   public abstract String[] getTypes();

   public abstract Block getModelBlock(int var1);

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public Icon getIcon(int side, int metadata) {
      Block model_block = this.getModelBlock(metadata);
      return model_block.getIcon(side, model_block.getBlockSubtypeUnchecked(metadata));
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess block_access, int x, int y, int z) {
      if (isTop(block_access.getBlockMetadata(x, y, z))) {
         this.setBlockBoundsForCurrentThread(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
      }

   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      int metadata = item_stack.getItemSubtype();
      return face != EnumFace.BOTTOM && (face == EnumFace.TOP || !(offset_y > 0.5F)) ? metadata : metadata | 8;
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used for subtype, bit 8 set if slab is top";
   }

   public abstract int getBlockSubtypeUnchecked(int var1);

   public static boolean isBottom(int metadata) {
      return (metadata & 8) == 0;
   }

   public static boolean isTop(int metadata) {
      return !isBottom(metadata);
   }

   public String getFullSlabName(int metadata) {
      return super.getUnlocalizedName() + "." + this.getTypes()[this.getItemSubtype(metadata)];
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? 0 : this.dropBlockAsEntityItem(info, this.createStackedBlock(info.getMetadata()));
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "single";
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      return isBottom(metadata) ? face.isBottom() : face.isTop();
   }

   public float getBlockHardness(int metadata) {
      Block model_block = this.getModelBlock(metadata);
      float hardness = 0.0F;
      if (model_block == stone) {
         hardness = 2.4F;
      } else if (model_block == sandStone) {
         hardness = 1.0F;
      } else if (model_block == planks) {
         hardness = 0.8F;
      } else if (model_block == cobblestone) {
         hardness = 2.0F;
      } else if (model_block == brick) {
         hardness = 2.0F;
      } else if (model_block == stoneBrick) {
         hardness = 2.0F;
      } else if (model_block == netherBrick) {
         hardness = 2.0F;
      } else if (model_block == blockNetherQuartz) {
         hardness = 0.8F;
      } else if (model_block == obsidian) {
         hardness = 2.4F;
      } else {
         Minecraft.setErrorMessage("getBlockHardness: unhandled model block " + model_block);
      }

      return hardness * 0.5F;
   }

   public int getMinHarvestLevel(int metadata) {
      return this.getModelBlock(metadata) == sandStone ? 1 : super.getMinHarvestLevel(metadata);
   }

   public void getItemStacks(int id, CreativeTabs creative_tabs, List list) {
      super.getItemStacks(id, creative_tabs, list);
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      boolean is_bottom = isBottom(block_access.getBlockMetadata(x, y, z));
      if (side == 1) {
         return is_bottom;
      } else if (side == 0) {
         return !is_bottom;
      } else if (neighbor instanceof BlockSlab) {
         EnumFace face = EnumFace.get(side).getOpposite();
         return isBottom(block_access.getBlockMetadata(face.getNeighborX(x), y, face.getNeighborZ(z))) == is_bottom;
      } else {
         return false;
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
