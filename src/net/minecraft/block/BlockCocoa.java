package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockMounted {
   private Icon[] iconArray;

   public BlockCocoa(int par1) {
      super(par1, Material.plants, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=Mounted " + this.getDirectionOfSupportBlock(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", bit 4 set if at intermediate growth stage, and bit 8 set if mature";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 12;
   }

   public Icon getIcon(int par1, int par2) {
      return this.iconArray[2];
   }

   public Icon getCocoaIcon(int par1) {
      if (par1 < 0 || par1 >= this.iconArray.length) {
         par1 = this.iconArray.length - 1;
      }

      return this.iconArray[par1];
   }

   public static int getDirection(int metadata) {
      return metadata & 3;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else {
         if (world.rand.nextInt(40) == 0) {
            int metadata = world.getBlockMetadata(x, y, z);
            int growth = func_72219_c(metadata);
            if (growth < 2) {
               ++growth;
               return world.setBlockMetadataWithNotify(x, y, z, growth << 2 | getDirection(metadata), 2);
            }
         }

         return false;
      }
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      Block block_above = Block.blocksList[world.getBlockId(x, y + 1, z)];
      if (block_above != Block.cocoaPlant && !BlockLeaves.isBlockUnplacedJungleLeaves(world, x, y + 1, z)) {
         return false;
      } else {
         boolean has_jungle_leaves_some_distance_above = false;

         for(int dy = 1; dy < 4; ++dy) {
            if (BlockLeaves.isBlockUnplacedJungleLeaves(world, x, y + dy, z)) {
               has_jungle_leaves_some_distance_above = true;
               break;
            }
         }

         return !has_jungle_leaves_some_distance_above ? false : super.canBePlacedAt(world, x, y, z, metadata);
      }
   }

   public EnumFace getFaceMountedTo(int metadata) {
      int direction = metadata & 3;
      if (direction == 0) {
         return EnumFace.NORTH;
      } else if (direction == 1) {
         return EnumFace.EAST;
      } else {
         return direction == 2 ? EnumFace.SOUTH : EnumFace.WEST;
      }
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (face.isNorth()) {
         return 0;
      } else if (face.isEast()) {
         return 1;
      } else if (face.isSouth()) {
         return 2;
      } else {
         return face.isWest() ? 3 : -1;
      }
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      return neighbor_block == wood && neighbor_block_metadata == 3 && super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
   }

   public int getRenderType() {
      return 28;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = getDirection(var5);
      int var7 = func_72219_c(var5);
      int var8 = 4 + var7 * 2;
      int var9 = 5 + var7 * 2;
      float var10 = (float)var8 / 2.0F;
      switch (var6) {
         case 0:
            this.setBlockBoundsForCurrentThread((double)((8.0F - var10) / 16.0F), (double)((12.0F - (float)var9) / 16.0F), (double)((15.0F - (float)var8) / 16.0F), (double)((8.0F + var10) / 16.0F), 0.75, 0.9375);
            break;
         case 1:
            this.setBlockBoundsForCurrentThread(0.0625, (double)((12.0F - (float)var9) / 16.0F), (double)((8.0F - var10) / 16.0F), (double)((1.0F + (float)var8) / 16.0F), 0.75, (double)((8.0F + var10) / 16.0F));
            break;
         case 2:
            this.setBlockBoundsForCurrentThread((double)((8.0F - var10) / 16.0F), (double)((12.0F - (float)var9) / 16.0F), 0.0625, (double)((8.0F + var10) / 16.0F), 0.75, (double)((1.0F + (float)var8) / 16.0F));
            break;
         case 3:
            this.setBlockBoundsForCurrentThread((double)((15.0F - (float)var8) / 16.0F), (double)((12.0F - (float)var9) / 16.0F), (double)((8.0F - var10) / 16.0F), 0.9375, 0.75, (double)((8.0F + var10) / 16.0F));
      }

   }

   public static int func_72219_c(int par0) {
      return (par0 & 12) >> 2;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      int growth = func_72219_c(info.getMetadata());
      return growth < 2 ? 0 : this.dropBlockAsEntityItem(info, Item.dyePowder.itemID, 3, 1, 1.5F);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.dyePowder.itemID;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[3];

      for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
         this.iconArray[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_stage_" + var2);
      }

   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
