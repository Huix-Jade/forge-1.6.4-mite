package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Objects;

public class BlockPistonBase extends BlockDirectional implements IBlockWithPartner {
   private final boolean isSticky;
   private Icon innerTopIcon;
   private Icon bottomIcon;
   private Icon topIcon;

   public BlockPistonBase(int par1, boolean par2) {
      super(par1, Material.piston, new BlockConstants());
      this.isSticky = par2;
      this.setStepSound(soundStoneFootstep);
      this.setHardness(0.5F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public String getMetadataNotes() {
      String[] array = new String[6];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=" + this.getDirectionFacing(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", bit 8 set if piston is extended";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 6 || metadata >= 8 && metadata < 14;
   }

   public Icon getPistonExtensionTexture() {
      return this.topIcon;
   }

   public void func_96479_b(float par1, float par2, float par3, float par4, float par5, float par6) {
      this.setBlockBoundsForCurrentThread((double)par1, (double)par2, (double)par3, (double)par4, (double)par5, (double)par6);
   }

   public Icon getIcon(int par1, int par2) {
      int var3 = getOrientation(par2);
      int index = Minecraft.getThreadIndex();
      return var3 > 5 ? this.topIcon : (par1 == var3 ? (!isExtended(par2) && this.minX[index] <= 0.0 && this.minY[index] <= 0.0 && this.minZ[index] <= 0.0 && this.maxX[index] >= 1.0 && this.maxY[index] >= 1.0 && this.maxZ[index] >= 1.0 ? this.topIcon : this.innerTopIcon) : (par1 == Facing.oppositeSide[var3] ? this.bottomIcon : this.blockIcon));
   }

   public static Icon getPistonBaseIcon(String par0Str) {
      return Objects.equals(par0Str, "piston_side") ? Block.pistonBase.blockIcon : (Objects.equals(par0Str, "piston_top_normal") ?
              Block.pistonBase.topIcon : (Objects.equals(par0Str, "piston_top_sticky") ? Block.pistonStickyBase.topIcon :
              (Objects.equals(par0Str, "piston_inner") ? Block.pistonBase.innerTopIcon : null)));
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("piston_side");
      this.topIcon = par1IconRegister.registerIcon(this.isSticky ? "piston_top_sticky" : "piston_top_normal");
      this.innerTopIcon = par1IconRegister.registerIcon("piston_inner");
      this.bottomIcon = par1IconRegister.registerIcon("piston_bottom");
   }

   public int getRenderType() {
      return 16;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return false;
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      return this.updatePistonState(world, x, y, z);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (!par1World.isRemote && par1World.getBlockTileEntity(par2, par3, par4) == null) {
         this.updatePistonState(par1World, par2, par3, par4);
      }

   }

   private boolean updatePistonState(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      int var6 = getOrientation(var5);
      if (var6 != 7) {
         boolean var7 = this.isIndirectlyPowered(par1World, par2, par3, par4, var6);
         if (var7 && !isExtended(var5)) {
            if (canExtend(par1World, par2, par3, par4, var6)) {
               par1World.addBlockEvent(par2, par3, par4, this.blockID, 0, var6);
            }
         } else if (!var7 && isExtended(var5)) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6, 2);
            par1World.addBlockEvent(par2, par3, par4, this.blockID, 1, var6);
            return true;
         }
      }

      return false;
   }

   private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4, int par5) {
      return par5 != 0 && par1World.getIndirectPowerOutput(par2, par3 - 1, par4, 0) ? true : (par5 != 1 && par1World.getIndirectPowerOutput(par2, par3 + 1, par4, 1) ? true : (par5 != 2 && par1World.getIndirectPowerOutput(par2, par3, par4 - 1, 2) ? true : (par5 != 3 && par1World.getIndirectPowerOutput(par2, par3, par4 + 1, 3) ? true : (par5 != 5 && par1World.getIndirectPowerOutput(par2 + 1, par3, par4, 5) ? true : (par5 != 4 && par1World.getIndirectPowerOutput(par2 - 1, par3, par4, 4) ? true : (par1World.getIndirectPowerOutput(par2, par3, par4, 0) ? true : (par1World.getIndirectPowerOutput(par2, par3 + 2, par4, 1) ? true : (par1World.getIndirectPowerOutput(par2, par3 + 1, par4 - 1, 2) ? true : (par1World.getIndirectPowerOutput(par2, par3 + 1, par4 + 1, 3) ? true : (par1World.getIndirectPowerOutput(par2 - 1, par3 + 1, par4, 4) ? true : par1World.getIndirectPowerOutput(par2 + 1, par3 + 1, par4, 5)))))))))));
   }

   public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if (!par1World.isRemote) {
         boolean var7 = this.isIndirectlyPowered(par1World, par2, par3, par4, par6);
         if (var7 && par5 == 1) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par6 | 8, 2);
            return false;
         }

         if (!var7 && par5 == 0) {
            return false;
         }
      }

      if (par5 == 0) {
         if (!this.tryExtend(par1World, par2, par3, par4, par6)) {
            return false;
         }

         par1World.setBlockMetadataWithNotify(par2, par3, par4, par6 | 8, 3);
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, "tile.piston.out", 0.5F, par1World.rand.nextFloat() * 0.25F + 0.6F);
      } else if (par5 == 1) {
         TileEntity var16 = par1World.getBlockTileEntity(par2 + Facing.offsetsXForSide[par6], par3 + Facing.offsetsYForSide[par6], par4 + Facing.offsetsZForSide[par6]);
         if (var16 instanceof TileEntityPiston) {
            ((TileEntityPiston)var16).clearPistonTileEntity();
         }

         par1World.setBlock(par2, par3, par4, Block.pistonMoving.blockID, par6, 3);
         par1World.setBlockTileEntity(par2, par3, par4, BlockPistonMoving.getTileEntity(this.blockID, par6, par6, false, true));
         if (this.isSticky) {
            int var8 = par2 + Facing.offsetsXForSide[par6] * 2;
            int var9 = par3 + Facing.offsetsYForSide[par6] * 2;
            int var10 = par4 + Facing.offsetsZForSide[par6] * 2;
            int var11 = par1World.getBlockId(var8, var9, var10);
            int var12 = par1World.getBlockMetadata(var8, var9, var10);
            boolean var13 = false;
            if (var11 == Block.pistonMoving.blockID) {
               TileEntity var14 = par1World.getBlockTileEntity(var8, var9, var10);
               if (var14 instanceof TileEntityPiston) {
                  TileEntityPiston var15 = (TileEntityPiston)var14;
                  if (var15.getPistonOrientation() == par6 && var15.isExtending()) {
                     var15.clearPistonTileEntity();
                     var11 = var15.getStoredBlockID();
                     var12 = var15.getBlockMetadata();
                     var13 = true;
                  }
               }
            }

            if (!var13 && var11 > 0 && canPushBlock(var11, par1World, var8, var9, var10, false) && (Block.blocksList[var11].getMobilityFlag() == 0 || var11 == Block.pistonBase.blockID || var11 == Block.pistonStickyBase.blockID)) {
               par2 += Facing.offsetsXForSide[par6];
               par3 += Facing.offsetsYForSide[par6];
               par4 += Facing.offsetsZForSide[par6];
               par1World.setBlock(par2, par3, par4, Block.pistonMoving.blockID, var12, 3);
               par1World.setBlockTileEntity(par2, par3, par4, BlockPistonMoving.getTileEntity(var11, var12, par6, false, false));
               par1World.setBlockToAir(var8, var9, var10);
            } else if (!var13) {
               par1World.setBlockToAir(par2 + Facing.offsetsXForSide[par6], par3 + Facing.offsetsYForSide[par6], par4 + Facing.offsetsZForSide[par6]);
            }
         } else {
            par1World.setBlockToAir(par2 + Facing.offsetsXForSide[par6], par3 + Facing.offsetsYForSide[par6], par4 + Facing.offsetsZForSide[par6]);
         }

         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, "tile.piston.in", 0.5F, par1World.rand.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
   {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

      if (isExtended(var5))
      {
         float var6 = 0.25F;

         switch (getOrientation(var5))
         {
            case 0:
               this.setBlockBoundsForCurrentThread(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);
               break;

            case 1:
               this.setBlockBoundsForCurrentThread(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
               break;

            case 2:
               this.setBlockBoundsForCurrentThread(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
               break;

            case 3:
               this.setBlockBoundsForCurrentThread(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
               break;

            case 4:
               this.setBlockBoundsForCurrentThread(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
               break;

            case 5:
               this.setBlockBoundsForCurrentThread(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
         }
      }
      else
      {
         this.setBlockBoundsForCurrentThread(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
      }
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public static int getOrientation(int par0) {
      return par0 & 7;
   }

   public static boolean isExtended(int par0) {
      return (par0 & 8) != 0;
   }

   public static int determineOrientation(World par0World, int par1, int par2, int par3, EntityLivingBase par4EntityLivingBase) {
      if (MathHelper.abs((float)par4EntityLivingBase.posX - (float)par1) < 2.0F && MathHelper.abs((float)par4EntityLivingBase.posZ - (float)par3) < 2.0F) {
         double var5 = par4EntityLivingBase.posY + 1.82 - (double)par4EntityLivingBase.yOffset;
         if (var5 - (double)par2 > 2.0) {
            return 1;
         }

         if ((double)par2 - var5 > 0.0) {
            return 0;
         }
      }

      int var7 = MathHelper.floor_double((double)(par4EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
      return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard6(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata = direction.isDown() ? 0 : (direction.isUp() ? 1 : (direction.isNorth() ? 2 : (direction.isSouth() ? 3 : (direction.isWest() ? 4 : (direction.isEast() ? 5 : -1)))));
      return metadata;
   }

   private static boolean canPushBlock(int par0, World par1World, int par2, int par3, int par4, boolean par5) {
      if (par0 == Block.obsidian.blockID) {
         return false;
      } else {
         int metadata = par1World.getBlockMetadata(par2, par3, par4);
         if (par0 != Block.pistonBase.blockID && par0 != Block.pistonStickyBase.blockID) {
            if (Block.blocksList[par0].getBlockHardness(metadata) == -1.0F) {
               return false;
            }

            if (Block.blocksList[par0].getMobilityFlag() == 2) {
               return false;
            }

            if (Block.blocksList[par0].getMobilityFlag() == 1) {
               if (!par5) {
                  return false;
               }

               return true;
            }
         } else if (isExtended(metadata)) {
            return false;
         }

         return !(Block.blocksList[par0] instanceof ITileEntityProvider);
      }
   }

   private static boolean canExtend(World par0World, int par1, int par2, int par3, int par4) {
      int var5 = par1 + Facing.offsetsXForSide[par4];
      int var6 = par2 + Facing.offsetsYForSide[par4];
      int var7 = par3 + Facing.offsetsZForSide[par4];
      int var8 = 0;
      BlockInfo crushable_block_info = null;

      while(var8 < 13) {
         if (var6 > 0 && var6 < par0World.getHeight() - 1) {
            int var9 = par0World.getBlockId(var5, var6, var7);
            if (!par0World.isAirBlock(var5, var6, var7)) {
               if (!canPushBlock(var9, par0World, var5, var6, var7, true)) {
                  return false;
               }

               Block block = Block.getBlock(var9);
               if (crushable_block_info == null && block == Block.thinGlass) {
                  crushable_block_info = new BlockInfo(block, var5, var6, var7);
               }

               if (Block.blocksList[var9].getMobilityFlag() != 1) {
                  if (var8 == 12) {
                     if (crushable_block_info == null) {
                        return false;
                     }

                     par0World.destroyBlock((new BlockBreakInfo(par0World, crushable_block_info.x, crushable_block_info.y, crushable_block_info.z)).setCrushed(Block.pistonBase), true);
                     return true;
                  }

                  var5 += Facing.offsetsXForSide[par4];
                  var6 += Facing.offsetsYForSide[par4];
                  var7 += Facing.offsetsZForSide[par4];
                  ++var8;
                  continue;
               }
            }
            break;
         }

         return false;
      }

      return true;
   }

   private boolean tryExtend(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = par2 + Facing.offsetsXForSide[par5];
      int var7 = par3 + Facing.offsetsYForSide[par5];
      int var8 = par4 + Facing.offsetsZForSide[par5];
      int var9 = 0;

      while(true) {
         int var10;
         int x;
         int y;
         if (var9 < 13) {
            if (var7 <= 0 || var7 >= par1World.getHeight() - 1) {
               return false;
            }

            var10 = par1World.getBlockId(var6, var7, var8);
            if (!par1World.isAirBlock(var6, var7, var8)) {
               if (!canPushBlock(var10, par1World, var6, var7, var8, true)) {
                  return false;
               }

               if (Block.blocksList[var10].getMobilityFlag() != 1) {
                  if (var9 == 12) {
                     return false;
                  }

                  var6 += Facing.offsetsXForSide[par5];
                  var7 += Facing.offsetsYForSide[par5];
                  var8 += Facing.offsetsZForSide[par5];
                  ++var9;
                  continue;
               }

               if (!par1World.isRemote) {
                  x = var6 + Facing.offsetsXForSide[par5];
                  y = var7 + Facing.offsetsYForSide[par5];
                  int z = var8 + Facing.offsetsZForSide[par5];
                  Block block_beyond = par1World.getBlock(x, y, z);
                  boolean was_crushed = block_beyond != null && block_beyond.isSolid(par1World.getBlockMetadata(x, y, z));
                  BlockBreakInfo info = new BlockBreakInfo(par1World, var6, var7, var8);
                  info.setDropCoords(x, y, z);
                  if (was_crushed) {
                     info.setCrushed(this);
                  }

                  info.chance = (Block.blocksList[var10] instanceof BlockSnow ? -1.0f : 1.0f);
                  info.dropBlockAsEntityItem(true);
               } else {
                  par1World.setBlockToAir(var6, var7, var8);
               }
            }
         }

         var9 = var6;
         var10 = var7;
         x = var8;
         y = 0;

         int[] var13;
         int var14;
         int var15;
         int var16 = 0;
         for(var13 = new int[13]; var6 != par2 || var7 != par3 || var8 != par4; var8 = var16) {
            var14 = var6 - Facing.offsetsXForSide[par5];
            var15 = var7 - Facing.offsetsYForSide[par5];
            var16 = var8 - Facing.offsetsZForSide[par5];
            int var17 = par1World.getBlockId(var14, var15, var16);
            int var18 = par1World.getBlockMetadata(var14, var15, var16);
            if (var17 == this.blockID && var14 == par2 && var15 == par3 && var16 == par4) {
               par1World.setBlock(var6, var7, var8, Block.pistonMoving.blockID, par5 | (this.isSticky ? 8 : 0), 4);
               par1World.setBlockTileEntity(var6, var7, var8, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, par5 | (this.isSticky ? 8 : 0), par5, true, false));
            } else {
               par1World.setBlock(var6, var7, var8, Block.pistonMoving.blockID, var18, 4);
               par1World.setBlockTileEntity(var6, var7, var8, BlockPistonMoving.getTileEntity(var17, var18, par5, true, false));
            }

            var13[y++] = var17;
            var6 = var14;
            var7 = var15;
         }

         var6 = var9;
         var7 = var10;
         var8 = x;

         for(y = 0; var6 != par2 || var7 != par3 || var8 != par4; var8 = var16) {
            var14 = var6 - Facing.offsetsXForSide[par5];
            var15 = var7 - Facing.offsetsYForSide[par5];
            var16 = var8 - Facing.offsetsZForSide[par5];
            par1World.notifyBlocksOfNeighborChange(var14, var15, var16, var13[y++]);
            var6 = var14;
            var7 = var15;
         }

         return true;
      }
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.stone, Material.iron, Material.redstone});
      if (this.isSticky) {
         item_block.addMaterial(new Material[]{Material.slime});
      }

   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      if (!isExtended(metadata)) {
         return true;
      } else {
         return face == this.getBackFace(metadata);
      }
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      int metadata = block_access.getBlockMetadata(x, y, z);
      if (!isExtended(metadata)) {
         return true;
      } else {
         return EnumFace.get(side) == this.getFrontFace(metadata);
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return !isExtended(metadata);
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public boolean canSupportEntityShadow(int metadata) {
      return !isExtended(metadata) || this.getDirectionFacing(metadata).isDown();
   }

   public int getPartnerOffsetX(int metadata) {
      return this.getDirectionFacing(metadata).dx;
   }

   public int getPartnerOffsetY(int metadata) {
      return this.getDirectionFacing(metadata).dy;
   }

   public int getPartnerOffsetZ(int metadata) {
      return this.getDirectionFacing(metadata).dz;
   }

   public boolean requiresPartner(int metadata) {
      return isExtended(metadata);
   }

   public boolean isPartner(int metadata, Block neighbor_block, int neighbor_block_metadata) {
      return neighbor_block instanceof BlockPistonExtension && ((BlockPistonExtension)neighbor_block).getDirectionFacing(neighbor_block_metadata) == this.getDirectionFacing(metadata);
   }

   public boolean partnerDropsAsItem(int metadata) {
      return false;
   }
}
