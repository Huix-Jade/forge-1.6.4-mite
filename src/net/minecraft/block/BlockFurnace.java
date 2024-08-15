package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public abstract class BlockFurnace extends BlockDirectionalWithTileEntity {
   private final Random furnaceRand = new Random();
   public final boolean isActive;
   private static boolean keepFurnaceInventory;
   protected Icon furnaceIconTop;
   protected Icon furnaceIconFront;

   protected BlockFurnace(int par1, Material material, boolean par2) {
      super(par1, material, new BlockConstants());
      this.isActive = par2;
      this.setMaxStackSize(1);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + 2 + "=" + this.getDirectionFacing(i + 2).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata > 1 && metadata < 6;
   }

   public boolean canBeCarried() {
      return !this.isActive;
   }

   public ItemStack createStackedBlock(int par1) {
      return new ItemStack(this.getIdleBlockID());
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasExploded()) {
         Block furnace_block = Block.getBlock(this.getIdleBlockID());
         if (furnace_block == Block.furnaceClayIdle) {
            return 0;
         } else {
            Block model_block;
            if (furnace_block == Block.furnaceSandstoneIdle) {
               model_block = Block.sandStone;
            } else if (furnace_block == Block.furnaceIdle) {
               model_block = Block.cobblestone;
            } else if (furnace_block == Block.furnaceObsidianIdle) {
               model_block = Block.obsidian;
            } else {
               if (furnace_block != Block.furnaceNetherrackIdle) {
                  return 0;
               }

               model_block = Block.netherrack;
            }

            return model_block.dropBlockAsEntityItem(info.setBlock(model_block, 0));
         }
      } else {
         return super.dropBlockAsEntityItem(info);
      }
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard6(metadata, false);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return direction.isNorth() ? 2 : (direction.isSouth() ? 3 : (direction.isWest() ? 4 : (direction.isEast() ? 5 : -1)));
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.furnaceIconTop : (par1 == 0 ? this.furnaceIconTop : (par1 != par2 ? this.blockIcon : this.furnaceIconFront));
   }

   public abstract void registerIcons(IconRegister var1);

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (this.isActive) {
         int var6 = par1World.getBlockMetadata(par2, par3, par4);
         float var7 = (float)par2 + 0.5F;
         float var8 = (float)par3 + 0.0F + par5Random.nextFloat() * 6.0F / 16.0F;
         float var9 = (float)par4 + 0.5F;
         float var10 = 0.52F;
         float var11 = par5Random.nextFloat() * 0.6F - 0.3F;
         if (var6 == 4) {
            par1World.spawnParticle(EnumParticle.smoke, (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
            par1World.spawnParticle(EnumParticle.flame, (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
         } else if (var6 == 5) {
            par1World.spawnParticle(EnumParticle.smoke, (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
            par1World.spawnParticle(EnumParticle.flame, (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
         } else if (var6 == 2) {
            par1World.spawnParticle(EnumParticle.smoke, (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
            par1World.spawnParticle(EnumParticle.flame, (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
         } else if (var6 == 3) {
            par1World.spawnParticle(EnumParticle.smoke, (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
            par1World.spawnParticle(EnumParticle.flame, (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
         }
      }

   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!world.isAirOrPassableBlock(World.getNeighboringBlockCoords(x, y, z, this.getDirectionFacing(world.getBlockMetadata(x, y, z)).getFace()), true)) {
         return false;
      } else {
         if (player.onServer()) {
            TileEntityFurnace tile_entity = (TileEntityFurnace)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               player.displayGUIFurnace(tile_entity);
            }
         }

         return true;
      }
   }

   public abstract int getIdleBlockID();

   public abstract int getActiveBlockID();

   public static void updateFurnaceBlockState(boolean par0, World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      TileEntity var6 = par1World.getBlockTileEntity(par2, par3, par4);
      keepFurnaceInventory = true;
      BlockFurnace block_furnace = (BlockFurnace)Block.blocksList[par1World.getBlockId(par2, par3, par4)];
      if (par0) {
         par1World.setBlock(par2, par3, par4, block_furnace.getActiveBlockID(), var5, 3);
      } else {
         par1World.setBlock(par2, par3, par4, block_furnace.getIdleBlockID(), var5, 3);
      }

      keepFurnaceInventory = false;
      if (var6 != null) {
         var6.validate();
         par1World.setBlockTileEntity(par2, par3, par4, var6);
      }

   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityFurnace();
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if (!keepFurnaceInventory) {
         TileEntityFurnace var7 = (TileEntityFurnace)par1World.getBlockTileEntity(par2, par3, par4);
         if (var7 != null) {
            for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
               ItemStack var9 = var7.getStackInSlot(var8);
               if (var9 != null) {
                  float var10 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                  float var11 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                  float var12 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

                  while(var9.stackSize > 0) {
                     int var13 = this.furnaceRand.nextInt(21) + 10;
                     if (var13 > var9.stackSize) {
                        var13 = var9.stackSize;
                     }

                     var9.stackSize -= var13;
                     EntityItem var14 = new EntityItem(par1World, (double)((float)par2 + var10), (double)((float)par3 + var11), (double)((float)par4 + var12), new ItemStack(var9.itemID, var13, var9.getItemSubtype()));
                     if (var9.isItemDamaged()) {
                        var14.getEntityItem().setItemDamage(var9.getItemDamage());
                     }

                     if (var9.getItem().hasQuality()) {
                        var14.getEntityItem().setQuality(var9.getQuality());
                     }

                     if (var9.hasTagCompound()) {
                        var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                     }

                     float var15 = 0.05F;
                     var14.motionX = (double)((float)this.furnaceRand.nextGaussian() * var15);
                     var14.motionY = (double)((float)this.furnaceRand.nextGaussian() * var15 + 0.2F);
                     var14.motionZ = (double)((float)this.furnaceRand.nextGaussian() * var15);
                     par1World.spawnEntityInWorld(var14);
                  }
               }
            }

            par1World.func_96440_m(par2, par3, par4, par5);
         }
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      return Container.calcRedstoneFromInventory((IInventory)par1World.getBlockTileEntity(par2, par3, par4));
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return this.getIdleBlockID();
   }

   public abstract int getMaxHeatLevel();

   public float getCraftingDifficultyAsComponent(int metadata) {
      return -1.0F;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.isActive ? "active" : "idle";
   }

   public boolean isOven() {
      return false;
   }

   public boolean acceptsLargeItems() {
      return true;
   }
}
