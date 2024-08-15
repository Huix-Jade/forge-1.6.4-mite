package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockDoor extends Block implements IBlockWithPartner {
   private Icon[] field_111044_a;
   private Icon[] field_111043_b;

   protected BlockDoor(int par1, Material par2Material) {
      super(par1, par2Material, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      float var3 = 0.5F;
      float var4 = 1.0F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var3), 0.0, (double)(0.5F - var3), (double)(0.5F + var3), (double)var4, (double)(0.5F + var3));
      this.setHardnessRelativeToWood(0.5F);
   }

   public String getMetadataNotes() {
      return "Lower half: Bits 1 and 2 used for orientation and bit 4 set if open. Upper half: Bit 1 set if hinge is reversed and bit 8 always set";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 10;
   }

   public Icon getIcon(int par1, int par2) {
      return this.field_111043_b[0];
   }

   public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (par5 != 1 && par5 != 0) {
         int var6 = this.getFullMetadata(par1IBlockAccess, par2, par3, par4);
         int var7 = var6 & 3;
         boolean var8 = (var6 & 4) != 0;
         boolean var9 = false;
         boolean var10 = (var6 & 8) != 0;
         if (var8) {
            if (var7 == 0 && par5 == 2) {
               var9 = !var9;
            } else if (var7 == 1 && par5 == 5) {
               var9 = !var9;
            } else if (var7 == 2 && par5 == 3) {
               var9 = !var9;
            } else if (var7 == 3 && par5 == 4) {
               var9 = !var9;
            }
         } else {
            if (var7 == 0 && par5 == 5) {
               var9 = !var9;
            } else if (var7 == 1 && par5 == 3) {
               var9 = !var9;
            } else if (var7 == 2 && par5 == 4) {
               var9 = !var9;
            } else if (var7 == 3 && par5 == 2) {
               var9 = !var9;
            }

            if ((var6 & 16) != 0) {
               var9 = !var9;
            }
         }

         return var10 ? this.field_111044_a[var9 ? 1 : 0] : this.field_111043_b[var9 ? 1 : 0];
      } else {
         return this.field_111043_b[0];
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_111044_a = new Icon[2];
      this.field_111043_b = new Icon[2];
      this.field_111044_a[0] = par1IconRegister.registerIcon(this.getTextureName() + "_upper");
      this.field_111043_b[0] = par1IconRegister.registerIcon(this.getTextureName() + "_lower");
      this.field_111044_a[1] = new IconFlipped(this.field_111044_a[0], true, false);
      this.field_111043_b[1] = new IconFlipped(this.field_111043_b[0], true, false);
   }

   public int getRenderType() {
      return 7;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setDoorRotation(this.getFullMetadata(par1IBlockAccess, par2, par3, par4), false);
   }

   public int getDoorOrientation(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return this.getFullMetadata(par1IBlockAccess, par2, par3, par4) & 3;
   }

   public boolean isDoorOpen(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return (this.getFullMetadata(par1IBlockAccess, par2, par3, par4) & 4) != 0;
   }

   public static int getIsOpenBit() {
      return 4;
   }

   public static boolean isOpen(int metadata) {
      return (metadata & getIsOpenBit()) != 0;
   }

   private void setDoorRotation(int par1, boolean for_all_threads) {
      float var2 = 0.1875F;
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 2.0, 1.0, for_all_threads);
      int var3 = par1 & 3;
      boolean var4 = (par1 & 4) != 0;
      boolean var5 = (par1 & 16) != 0;
      if (var3 == 0) {
         if (var4) {
            if (!var5) {
               this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, (double)var2, for_all_threads);
            } else {
               this.setBlockBounds(0.0, 0.0, (double)(1.0F - var2), 1.0, 1.0, 1.0, for_all_threads);
            }
         } else {
            this.setBlockBounds(0.0, 0.0, 0.0, (double)var2, 1.0, 1.0, for_all_threads);
         }
      } else if (var3 == 1) {
         if (var4) {
            if (!var5) {
               this.setBlockBounds((double)(1.0F - var2), 0.0, 0.0, 1.0, 1.0, 1.0, for_all_threads);
            } else {
               this.setBlockBounds(0.0, 0.0, 0.0, (double)var2, 1.0, 1.0, for_all_threads);
            }
         } else {
            this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, (double)var2, for_all_threads);
         }
      } else if (var3 == 2) {
         if (var4) {
            if (!var5) {
               this.setBlockBounds(0.0, 0.0, (double)(1.0F - var2), 1.0, 1.0, 1.0, for_all_threads);
            } else {
               this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, (double)var2, for_all_threads);
            }
         } else {
            this.setBlockBounds((double)(1.0F - var2), 0.0, 0.0, 1.0, 1.0, 1.0, for_all_threads);
         }
      } else if (var3 == 3) {
         if (var4) {
            if (!var5) {
               this.setBlockBounds(0.0, 0.0, 0.0, (double)var2, 1.0, 1.0, for_all_threads);
            } else {
               this.setBlockBounds((double)(1.0F - var2), 0.0, 0.0, 1.0, 1.0, 1.0, for_all_threads);
            }
         } else {
            this.setBlockBounds(0.0, 0.0, (double)(1.0F - var2), 1.0, 1.0, 1.0, for_all_threads);
         }
      }

   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
   }

   private void makeOpenOrCloseSound(World world, int x, int y, int z, int metadata_after) {
      if (isOpen(metadata_after)) {
         world.playSoundAtBlock(x, y, z, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
      } else {
         world.playSoundAtBlock(x, y, z, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
      }

   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (this.blockMaterial != Material.wood) {
         return false;
      } else {
         if (player.onServer()) {
            int var10 = this.getFullMetadata(world, x, y, z);
            int var11 = var10 & 7;
            var11 ^= 4;
            if ((var10 & 8) == 0) {
               world.setBlockMetadataWithNotify(x, y, z, var11, 2);
               world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            } else {
               world.setBlockMetadataWithNotify(x, y - 1, z, var11, 2);
               world.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);
            }

            this.makeOpenOrCloseSound(world, x, y, z, var11);
         }

         return true;
      }
   }

   public boolean onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5) {
      int var6 = this.getFullMetadata(par1World, par2, par3, par4);
      boolean var7 = (var6 & 4) != 0;
      if (var7 != par5) {
         int var8 = var6 & 7;
         var8 ^= 4;
         if ((var6 & 8) == 0) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var8, 2);
            par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
         } else {
            par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, var8, 2);
            par1World.markBlockRangeForRenderUpdate(par2, par3 - 1, par4, par2, par3, par4);
         }

         this.makeOpenOrCloseSound(par1World, par2, par3, par4, var8);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isTopHalf(int metadata) {
      return (metadata & 8) != 0;
   }

   public static boolean isBottomHalf(int metadata) {
      return !isTopHalf(metadata);
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         if (isBottomHalf(metadata)) {
            boolean is_indirectly_powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
            if ((is_indirectly_powered || neighbor_block_id > 0 && Block.blocksList[neighbor_block_id].canProvidePower()) && neighbor_block_id != this.blockID) {
               return this.onPoweredBlockChange(world, x, y, z, is_indirectly_powered);
            }
         } else if (neighbor_block_id > 0 && neighbor_block_id != this.blockID) {
            this.onNeighborBlockChange(world, x, y - 1, z, neighbor_block_id);
         }

         return false;
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      if (!super.isLegalAt(world, x, y, z, metadata)) {
         return false;
      } else if (!this.is_being_placed && isBottomHalf(metadata) && world.getBlock(x, y + 1, z) != this) {
         return false;
      } else if (this.is_being_placed && isTopHalf(metadata)) {
         return true;
      } else {
         EnumDirection direction_facing = this.getDirectionFacing(isBottomHalf(metadata) ? metadata : world.getBlockMetadata(x, y - 1, z));
         boolean is_double_door = isTopHalf(metadata) ? isDoubleDoor(metadata) : this.isDoubleDoor(world, x, y + 1, z);
         if (is_double_door) {
            direction_facing = direction_facing.getOpposite();
         }

         if (direction_facing.isWest()) {
            --z;
         } else if (direction_facing.isNorth()) {
            ++x;
         } else if (direction_facing.isEast()) {
            ++z;
         } else {
            --x;
         }

         if (!is_double_door && world.getBlock(x, y, z) == this && !this.isDoubleDoor(world, x, y, z) && this.getDirectionFacing(world, x, y, z) == direction_facing) {
            direction_facing = direction_facing.getOpposite();
            if (direction_facing.isWest()) {
               z -= 2;
            } else if (direction_facing.isNorth()) {
               x += 2;
            } else if (direction_facing.isEast()) {
               z += 2;
            } else {
               x -= 2;
            }
         }

         if (world.isBlockFaceFlatAndSolid(x, y, z, direction_facing.isWest() ? EnumFace.SOUTH : (direction_facing.isNorth() ? EnumFace.WEST : (direction_facing.isEast() ? EnumFace.NORTH : EnumFace.EAST))) && isSuitableMaterialForAttachingHingesTo(world.getBlockMaterial(x, y, z))) {
            return !isBottomHalf(metadata) || world.isBlockFaceFlatAndSolid(x, y + 1, z, direction_facing.isWest() ? EnumFace.SOUTH : (direction_facing.isNorth() ? EnumFace.WEST : (direction_facing.isEast() ? EnumFace.NORTH : EnumFace.EAST))) && isSuitableMaterialForAttachingHingesTo(world.getBlockMaterial(x, y + 1, z));
         } else {
            return false;
         }
      }
   }

   public static boolean isSuitableMaterialForAttachingHingesTo(Material material) {
      return material != Material.dirt && material != Material.grass && material != Material.sand && material != Material.clay && material != Material.tree_leaves && material != Material.craftedSnow && material != Material.cloth && material != Material.pumpkin && material != Material.sponge && material != Material.glass;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (isBottomHalf(metadata)) {
         return super.isLegalOn(metadata, block_below, block_below_metadata);
      } else if (this.is_being_placed && (block_below == null || block_below.canBeReplacedBy(block_below_metadata, this, metadata))) {
         return true;
      } else {
         return block_below == this && isBottomHalf(block_below_metadata);
      }
   }

   public int getMobilityFlag() {
      return 1;
   }

   public int getFullMetadata(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      boolean var6 = (var5 & 8) != 0;
      int var7;
      int var8;
      if (var6) {
         var7 = par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4);
         var8 = var5;
      } else {
         var7 = var5;
         var8 = par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4);
      }

      boolean var9 = (var8 & 1) != 0;
      return var7 & 7 | (var6 ? 8 : 0) | (var9 ? 16 : 0);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return this.blockMaterial == Material.iron ? Item.doorIron.itemID : Item.doorWood.itemID;
   }

   public void onBlockAboutToBeBroken(BlockBreakInfo info) {
      if (isTopHalf(info.getMetadata()) && info.world.getBlockId(info.x, info.y - 1, info.z) == this.blockID) {
         if (info.isResponsiblePlayerInCreativeMode()) {
            info.world.setBlockToAir(info.x, info.y - 1, info.z);
         } else if (info.wasExploded()) {
            this.dropBlockAsEntityItem((new BlockBreakInfo(info.world, info.x, info.y - 1, info.z)).setExploded(info.explosion));
            info.world.setBlockToAir(info.x, info.y - 1, info.z);
         }

      }
   }

   public boolean partnerDropsAsItem(int metadata) {
      return isTopHalf(metadata);
   }

   public Item getDoorItem() {
      if (this == Block.doorWood) {
         return Item.doorWood;
      } else if (this == Block.doorCopper) {
         return Item.doorCopper;
      } else if (this == Block.doorSilver) {
         return Item.doorSilver;
      } else if (this == Block.doorGold) {
         return Item.doorGold;
      } else if (this == Block.doorIron) {
         return Item.doorIron;
      } else if (this == Block.doorMithril) {
         return Item.doorMithril;
      } else if (this == Block.doorAdamantium) {
         return Item.doorAdamantium;
      } else if (this == Block.doorAncientMetal) {
         return Item.doorAncientMetal;
      } else {
         Minecraft.setErrorMessage("getDoorItem: unhandled door type " + this);
         return null;
      }
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (isTopHalf(info.getMetadata())) {
         return 0;
      } else {
         return info.wasExploded() && this.blockMaterial == Material.wood ? this.dropBlockAsEntityItem(info, Item.stick) : this.dropBlockAsEntityItem(info, this.getDoorItem());
      }
   }

   private static boolean isDoubleDoor(int metadata) {
      if (!isTopHalf(metadata)) {
         Minecraft.setErrorMessage("isDoubleDoor: must be top half of door");
         (new Exception()).printStackTrace();
         return false;
      } else {
         return BitHelper.isBitSet(metadata, 1);
      }
   }

   private boolean isDoubleDoor(World world, int x, int y, int z) {
      if (world.getBlock(x, y, z) != this) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         return isBottomHalf(metadata) ? this.isDoubleDoor(world, x, y + 1, z) : isDoubleDoor(metadata);
      }
   }

   private boolean shouldBeDoubleDoor(World world, int x, int y, int z) {
      if (world.getBlock(x, y - 1, z) != this) {
         Minecraft.setErrorMessage("shouldBeDoubleDoor: top had no bottom below it");
         return false;
      } else {
         int direction = world.getBlockMetadata(x, y - 1, z) & 3;
         if (direction == 0) {
            if (world.getBlock(x, y, z - 1) == this && (world.getBlockMetadata(x, y - 1, z - 1) & 3) == direction && world.getBlockMetadata(x, y, z - 1) == 8) {
               return true;
            }
         } else if (direction == 1) {
            if (world.getBlock(x + 1, y, z) == this && (world.getBlockMetadata(x + 1, y - 1, z) & 3) == direction && world.getBlockMetadata(x + 1, y, z) == 8) {
               return true;
            }
         } else if (direction == 2) {
            if (world.getBlock(x, y, z + 1) == this && (world.getBlockMetadata(x, y - 1, z + 1) & 3) == direction && world.getBlockMetadata(x, y, z + 1) == 8) {
               return true;
            }
         } else if (direction == 3 && world.getBlock(x - 1, y, z) == this && (world.getBlockMetadata(x - 1, y - 1, z) & 3) == direction && world.getBlockMetadata(x - 1, y, z) == 8) {
            return true;
         }

         return false;
      }
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      return world.getBlock(x, y, z) != Block.torchRedstoneActive && super.canBePlacedAt(world, x, y, z, metadata);
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (isTopHalf(metadata)) {
         return true;
      } else if (!this.canBePlacedAt(world, x, y + 1, z, 8)) {
         return false;
      } else {
         int saved_metadata = world.getBlockMetadata(x, y, z);
         world.setBlockMetadataWithNotify(x, y, z, metadata, 0);
         boolean result = this.tryPlaceBlock(world, x, y + 1, z, EnumFace.TOP, !test_only && this.shouldBeDoubleDoor(world, x, y + 1, z) ? 9 : 8, placer, false, true, test_only);
         world.setBlockMetadataWithNotify(x, y, z, saved_metadata, 0);
         return result;
      }
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      EnumDirection direction = entity.getDirectionFromYaw();
      byte metadata;
      if (direction.isEast()) {
         metadata = 0;
      } else if (direction.isSouth()) {
         metadata = 1;
      } else if (direction.isWest()) {
         metadata = 2;
      } else {
         metadata = 3;
      }

      return metadata;
   }

   private boolean isBlockSuitableDoorFrame(World world, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      if (block == null) {
         return false;
      } else {
         return block == this ? true : block.isSolidStandardFormCube(world.getBlockMetadata(x, y, z));
      }
   }

   public int getDefaultMetadata(World world, int x, int y, int z) {
      if (world.getBlock(x, y - 1, z) == this) {
         return this.shouldBeDoubleDoor(world, x, y, z) ? 9 : 8;
      } else if (this.isBlockSuitableDoorFrame(world, x - 1, y, z) && this.isBlockSuitableDoorFrame(world, x + 1, y, z)) {
         return 1;
      } else {
         return this.isBlockSuitableDoorFrame(world, x, y, z - 1) && this.isBlockSuitableDoorFrame(world, x, y, z + 1) ? 2 : -1;
      }
   }

   public EnumDirection getDirectionFacing(int metadata) {
      int direction = metadata & 3;
      return direction == 0 ? EnumDirection.WEST : (direction == 1 ? EnumDirection.NORTH : (direction == 2 ? EnumDirection.EAST : EnumDirection.SOUTH));
   }

   public EnumDirection getDirectionFacing(World world, int x, int y, int z) {
      if (world.getBlock(x, y, z) != this) {
         return null;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         return isTopHalf(metadata) ? this.getDirectionFacing(world, x, y - 1, z) : this.getDirectionFacing(metadata);
      }
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      if (isBottomHalf(metadata)) {
         metadata &= -4;
         metadata |= direction.isWest() ? 0 : (direction.isNorth() ? 1 : (direction.isEast() ? 2 : (direction.isSouth() ? 3 : -1)));
      }

      return metadata;
   }

   public boolean isPortal() {
      return true;
   }

   public boolean isOpenPortal(World world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      if (isTopHalf(metadata)) {
         Block block = world.getBlock(x, y - 1, z);
         return block == null ? false : block.isOpenPortal(world, x, y - 1, z);
      } else {
         return isOpen(metadata);
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public int getPartnerOffsetX(int metadata) {
      return 0;
   }

   public int getPartnerOffsetY(int metadata) {
      return isTopHalf(metadata) ? -1 : 1;
   }

   public int getPartnerOffsetZ(int metadata) {
      return 0;
   }

   public boolean requiresPartner(int metadata) {
      return true;
   }

   public boolean isPartner(int metadata, Block neighbor_block, int neighbor_block_metadata) {
      return neighbor_block == this && isTopHalf(neighbor_block_metadata) != isTopHalf(metadata);
   }
}
