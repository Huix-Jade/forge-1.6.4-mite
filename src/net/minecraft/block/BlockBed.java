package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class BlockBed extends BlockDirectional implements IBlockWithPartner {
   public static final int[][] footBlockToHeadBlockMap = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
   private Icon[] field_94472_b;
   private Icon[] bedSideIcons;
   private Icon[] bedTopIcons;

   public BlockBed(int par1) {
      super(par1, Material.cloth, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setBounds(true);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bit 8 is set for head of bed";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4 || metadata >= 8 && metadata < 12;
   }

   public EnumDirection getDirectionFacing(int metadata) {
      int direction = metadata & 3;
      if (direction == 0) {
         return EnumDirection.NORTH;
      } else if (direction == 1) {
         return EnumDirection.EAST;
      } else {
         return direction == 2 ? EnumDirection.SOUTH : EnumDirection.WEST;
      }
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata &= -4;
      metadata |= direction.isNorth() ? 0 : (direction.isEast() ? 1 : (direction.isSouth() ? 2 : (direction.isWest() ? 3 : -1)));
      return metadata;
   }

   public static int j(int metadata) {
      return metadata & 3;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!player.onGround) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         if (!isBlockHeadOfBed(metadata)) {
            int direction = j(metadata);
            x += footBlockToHeadBlockMap[direction][0];
            z += footBlockToHeadBlockMap[direction][1];
            if (world.getBlockId(x, y, z) != this.blockID) {
               return false;
            }

            metadata = world.getBlockMetadata(x, y, z);
            if (!isBlockHeadOfBed(metadata)) {
               return false;
            }
         }

         if (world.provider.canRespawnHere() && world.getBiomeGenForCoords(x, z) != BiomeGenBase.hell) {
            if (isBedOccupied(metadata)) {
               Iterator i = world.playerEntities.iterator();

               while(i.hasNext()) {
                  EntityPlayer player_to_check = (EntityPlayer)i.next();
                  if (player_to_check.inBed()) {
                     ChunkCoordinates chunk_coords = player_to_check.bed_location;
                     if (chunk_coords.posX == x && chunk_coords.posY == y && chunk_coords.posZ == z) {
                        player.addChatMessage("tile.bed.occupied");
                        if (player.isLocalClient()) {
                           player.getPlayerController().setUseButtonDelay();
                        }

                        return false;
                     }
                  }
               }

               if (player.onServer()) {
                  setBedOccupied(world, x, y, z, false);
               }
            }

            if (player.onServer()) {
               player.tryToSleepInBedAt(x, y, z);
            }

            return true;
         } else {
            player.addChatMessage("tile.bed.mobsDigging");
            if (player.isLocalClient()) {
               player.getPlayerController().setUseButtonDelay();
            }

            return false;
         }
      }
   }

   public Icon getIcon(int par1, int par2) {
      if (par1 == 0) {
         return Block.planks.getBlockTextureFromSide(par1);
      } else {
         int var3 = j(par2);
         int var4 = Direction.bedDirection[var3][par1];
         int var5 = isBlockHeadOfBed(par2) ? 1 : 0;
         return var5 == 1 && var4 == 2 || var5 == 0 && var4 == 3 ? this.field_94472_b[var5] : (var4 != 5 && var4 != 4 ? this.bedTopIcons[var5] : this.bedSideIcons[var5]);
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.bedTopIcons = new Icon[]{par1IconRegister.registerIcon(this.getTextureName() + "_feet_top"), par1IconRegister.registerIcon(this.getTextureName() + "_head_top")};
      this.field_94472_b = new Icon[]{par1IconRegister.registerIcon(this.getTextureName() + "_feet_end"), par1IconRegister.registerIcon(this.getTextureName() + "_head_end")};
      this.bedSideIcons = new Icon[]{par1IconRegister.registerIcon(this.getTextureName() + "_feet_side"), par1IconRegister.registerIcon(this.getTextureName() + "_head_side")};
   }

   public int getRenderType() {
      return 14;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBounds(false);
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      if (!super.isLegalAt(world, x, y, z, metadata)) {
         return false;
      } else if (this.is_being_placed) {
         return true;
      } else {
         return !this.requiresPartner(metadata) || this.isPartnerPresent(world, x, y, z);
      }
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below != null && block_below.isTopFlatAndSolid(block_below_metadata);
   }

   private void setBounds(boolean for_all_threads) {
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0, for_all_threads);
   }

   public static boolean isBlockHeadOfBed(int par0) {
      return (par0 & 8) != 0;
   }

   public static boolean isBedOccupied(int par0) {
      return (par0 & 4) != 0;
   }

   public static void setBedOccupied(World par0World, int par1, int par2, int par3, boolean par4) {
      int var5 = par0World.getBlockMetadata(par1, par2, par3);
      if (par4) {
         var5 |= 4;
      } else {
         var5 &= -5;
      }

      par0World.setBlockMetadataWithNotify(par1, par2, par3, var5, 6);
   }

   public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int bed_head_x, int bed_head_y, int bed_head_z, int par4, Vec3 prescribed_pos) {
      int direction = j(world.getBlockMetadata(bed_head_x, bed_head_y, bed_head_z));
      int bed_foot_x = bed_head_x - footBlockToHeadBlockMap[direction][0];
      int bed_foot_z = bed_head_z - footBlockToHeadBlockMap[direction][1];
      RaycastPolicies policies = RaycastPolicies.for_physical_reach;
      int[] dy = new int[]{0, -1, 1};

      for(int i = prescribed_pos == null ? 1 : 0; i < 2; ++i) {
         for(int dy_index = 0; dy_index < dy.length; ++dy_index) {
            for(int var7 = 0; var7 <= 1; ++var7) {
               int var8 = bed_head_x - footBlockToHeadBlockMap[direction][0] * var7 - 1;
               int var9 = bed_head_z - footBlockToHeadBlockMap[direction][1] * var7 - 1;
               int var10 = var8 + 2;
               int var11 = var9 + 2;
               if (i == 0) {
                  var8 -= 2;
                  var9 -= 2;
                  var10 += 2;
                  var11 += 2;
               }

               for(int var12 = var8; var12 <= var10; ++var12) {
                  for(int var13 = var9; var13 <= var11; ++var13) {
                     int x = var12;
                     int y = bed_head_y + dy[dy_index];
                     int z = var13;
                     if (i != 0 || x == MathHelper.floor_double(prescribed_pos.xCoord) && y == MathHelper.floor_double(prescribed_pos.yCoord + 0.95) && z == MathHelper.floor_double(prescribed_pos.zCoord)) {
                        Block block_below = world.getBlock(x, y - 1, z);
                        if (block_below != null && block_below.isSolid(world.getBlockMetadata(x, y - 1, z)) && (world.isAirOrPassableBlock(x, y, z, true) || world.getBlock(x, y, z) instanceof BlockDoor || world.getBlock(x, y, z) instanceof BlockLadder) && (world.isAirOrPassableBlock(x, y + 1, z, true) || world.getBlock(x, y + 1, z) instanceof BlockDoor || world.getBlock(x, y + 1, z) instanceof BlockLadder)) {
                           if (par4 <= 0) {
                              if (world.canCastRayBetweenBlockCenters(policies, x, y + 1, z, bed_head_x, bed_head_y, bed_head_z, true)) {
                                 return new ChunkCoordinates(x, y, z);
                              }

                              if (world.canCastRayBetweenBlockCenters(policies, x, y, z, bed_head_x, bed_head_y, bed_head_z, true)) {
                                 return new ChunkCoordinates(x, y, z);
                              }

                              if (world.canCastRayBetweenBlockCenters(policies, x, y + 1, z, bed_foot_x, bed_head_y, bed_foot_z, true)) {
                                 return new ChunkCoordinates(x, y, z);
                              }

                              if (world.canCastRayBetweenBlockCenters(policies, x, y, z, bed_foot_x, bed_head_y, bed_foot_z, true)) {
                                 return new ChunkCoordinates(x, y, z);
                              }
                           }

                           --par4;
                        }
                     }
                  }
               }
            }
         }
      }

      if (!world.getBlockMaterial(bed_head_x, bed_head_y + 1, bed_head_z).isSolid() && !world.getBlockMaterial(bed_head_x, bed_head_y + 2, bed_head_z).isSolid() && par4 <= 0) {
         return new ChunkCoordinates(bed_head_x, bed_head_y + 1, bed_head_z);
      } else if (!world.getBlockMaterial(bed_foot_x, bed_head_y + 1, bed_foot_z).isSolid() && !world.getBlockMaterial(bed_foot_x, bed_head_y + 2, bed_foot_z).isSolid() && par4 <= 0) {
         return new ChunkCoordinates(bed_foot_x, bed_head_y + 1, bed_foot_z);
      } else {
         return null;
      }
   }

   public void onBlockAboutToBeBroken(BlockBreakInfo info) {
      if (isBlockHeadOfBed(info.getMetadata())) {
         int direction = j(info.getMetadata());
         int x = info.x - footBlockToHeadBlockMap[direction][0];
         int z = info.z - footBlockToHeadBlockMap[direction][1];
         if (info.world.getBlockId(x, info.y, z) == this.blockID) {
            if (info.isResponsiblePlayerInCreativeMode()) {
               info.world.setBlockToAir(x, info.y, z);
            } else if (info.wasExploded()) {
               this.dropBlockAsEntityItem((new BlockBreakInfo(info.world, x, info.y, z)).setExploded(info.explosion));
               info.world.setBlockToAir(x, info.y, z);
            }

         }
      }
   }

   public boolean partnerDropsAsItem(int metadata) {
      return isBlockHeadOfBed(metadata);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (isBlockHeadOfBed(info.getMetadata())) {
         return 0;
      } else {
         return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.stick.itemID, 0, 1, 1.5F) | this.dropBlockAsEntityItem(info, Item.silk.itemID, 0, 1, 1.5F) : this.dropBlockAsEntityItem(info, Item.bed);
      }
   }

   public int getMobilityFlag() {
      return 1;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.bed.itemID;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.cloth});
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (isBlockHeadOfBed(metadata)) {
         return true;
      } else {
         int direction = j(metadata);
         x += footBlockToHeadBlockMap[direction][0];
         z += footBlockToHeadBlockMap[direction][1];
         return world.isAirBlock(x, y - 1, z) ? false : this.tryPlaceBlock(world, x, y, z, EnumFace.TOP, metadata | 8, placer, true, true, test_only);
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public int getPartnerOffsetX(int metadata) {
      return isBlockHeadOfBed(metadata) ? -footBlockToHeadBlockMap[j(metadata)][0] : footBlockToHeadBlockMap[j(metadata)][0];
   }

   public int getPartnerOffsetY(int metadata) {
      return 0;
   }

   public int getPartnerOffsetZ(int metadata) {
      return isBlockHeadOfBed(metadata) ? -footBlockToHeadBlockMap[j(metadata)][1] : footBlockToHeadBlockMap[j(metadata)][1];
   }

   public boolean requiresPartner(int metadata) {
      return true;
   }

   public boolean isPartner(int metadata, Block neighbor_block, int neighbor_block_metadata) {
      return neighbor_block == this && isBlockHeadOfBed(neighbor_block_metadata) != isBlockHeadOfBed(metadata);
   }
}
