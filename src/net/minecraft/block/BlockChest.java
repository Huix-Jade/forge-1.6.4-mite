package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumChestType;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockChest extends BlockDirectionalWithTileEntity {
   private final Random random = new Random();
   public EnumChestType chest_type;

   protected BlockChest(int id, EnumChestType chest_type, Material material) {
      super(id, material, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.chest_type = chest_type;
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBoundsForAllThreads(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
      this.setTickRandomly(true);
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

   public int getRenderType() {
      return 22;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      if (par1IBlockAccess.getBlockId(par2, par3, par4 - 1) == this.blockID) {
         this.setBlockBoundsForCurrentThread(0.0625, 0.0, 0.0, 0.9375, 0.875, 0.9375);
      } else if (par1IBlockAccess.getBlockId(par2, par3, par4 + 1) == this.blockID) {
         this.setBlockBoundsForCurrentThread(0.0625, 0.0, 0.0625, 0.9375, 0.875, 1.0);
      } else if (par1IBlockAccess.getBlockId(par2 - 1, par3, par4) == this.blockID) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
      } else if (par1IBlockAccess.getBlockId(par2 + 1, par3, par4) == this.blockID) {
         this.setBlockBoundsForCurrentThread(0.0625, 0.0, 0.0625, 1.0, 0.875, 0.9375);
      } else {
         this.setBlockBoundsForCurrentThread(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
      }

   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
      this.tryAlignWithNeighboringChest(par1World, par2, par3, par4);
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard6(metadata, false);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata = direction.isNorth() ? 2 : (direction.isSouth() ? 3 : (direction.isWest() ? 4 : (direction.isEast() ? 5 : -1)));
      return metadata;
   }

   public void tryAlignWithNeighboringChest(World world, int x, int y, int z) {
      if (!world.isRemote) {
         if (!(this instanceof BlockStrongbox)) {
            int metadata = world.getBlockMetadata(x, y, z);
            if (world.getBlockId(x - 1, y, z) == this.blockID) {
               if (metadata != 2 && metadata != 3) {
                  metadata = world.getBlockMetadata(x - 1, y, z);
                  if (metadata != 2 && metadata != 3) {
                     metadata = 2;
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                     world.setBlockMetadataWithNotify(x - 1, y, z, metadata, 2);
                  } else {
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                  }
               } else {
                  world.setBlockMetadataWithNotify(x - 1, y, z, metadata, 2);
               }
            } else if (world.getBlockId(x + 1, y, z) == this.blockID) {
               if (metadata != 2 && metadata != 3) {
                  metadata = world.getBlockMetadata(x + 1, y, z);
                  if (metadata != 2 && metadata != 3) {
                     metadata = 3;
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                     world.setBlockMetadataWithNotify(x + 1, y, z, metadata, 2);
                  } else {
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                  }
               } else {
                  world.setBlockMetadataWithNotify(x + 1, y, z, metadata, 2);
               }
            } else if (world.getBlockId(x, y, z - 1) == this.blockID) {
               if (metadata != 4 && metadata != 5) {
                  metadata = world.getBlockMetadata(x, y, z - 1);
                  if (metadata != 4 && metadata != 5) {
                     metadata = 4;
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                     world.setBlockMetadataWithNotify(x, y, z - 1, metadata, 2);
                  } else {
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                  }
               } else {
                  world.setBlockMetadataWithNotify(x, y, z - 1, metadata, 2);
               }
            } else if (world.getBlockId(x, y, z + 1) == this.blockID) {
               if (metadata != 4 && metadata != 5) {
                  metadata = world.getBlockMetadata(x, y, z + 1);
                  if (metadata != 4 && metadata != 5) {
                     metadata = 5;
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                     world.setBlockMetadataWithNotify(x, y, z + 1, metadata, 2);
                  } else {
                     world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
                  }
               } else {
                  world.setBlockMetadataWithNotify(x, y, z + 1, metadata, 2);
               }
            }

         }
      }
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      if (this instanceof BlockStrongbox) {
         return super.canBePlacedAt(world, x, y, z, metadata);
      } else {
         int num_orthagonal_chests = 0;
         if (world.getBlock(x - 1, y, z) == this) {
            ++num_orthagonal_chests;
         }

         if (world.getBlock(x + 1, y, z) == this) {
            ++num_orthagonal_chests;
         }

         if (world.getBlock(x, y, z - 1) == this) {
            ++num_orthagonal_chests;
         }

         if (world.getBlock(x, y, z + 1) == this) {
            ++num_orthagonal_chests;
         }

         if (num_orthagonal_chests > 1) {
            return false;
         } else {
            return !this.isThereANeighborChest(world, x - 1, y, z) && !this.isThereANeighborChest(world, x + 1, y, z) && !this.isThereANeighborChest(world, x, y, z - 1) && !this.isThereANeighborChest(world, x, y, z + 1) ? super.canBePlacedAt(world, x, y, z, metadata) : false;
         }
      }
   }

   private boolean isThereANeighborChest(World par1World, int par2, int par3, int par4) {
      return par1World.getBlockId(par2, par3, par4) != this.blockID ? false : (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID ? true : (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID ? true : (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID ? true : par1World.getBlockId(par2, par3, par4 + 1) == this.blockID)));
   }

   public boolean updateTick(World world, int x, int y, int z, Random rand) {
      if (super.updateTick(world, x, y, z, rand) && world.getBlock(x, y, z) != this) {
         return true;
      } else {
         TileEntityChest chest_entity = (TileEntityChest)world.getBlockTileEntity(x, y, z);
         if (chest_entity != null) {
            chest_entity.checkForWormComposting();
         }

         return false;
      }
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else {
         TileEntityChest chest_entity = (TileEntityChest)world.getBlockTileEntity(x, y, z);
         if (chest_entity != null) {
            chest_entity.updateContainingBlockInfo();
            return true;
         } else {
            return false;
         }
      }
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      TileEntityChest var7 = (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4);
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
            ItemStack var9 = var7.getStackInSlot(var8);
            if (var9 != null) {
               float var10 = this.random.nextFloat() * 0.8F + 0.1F;
               float var11 = this.random.nextFloat() * 0.8F + 0.1F;

               EntityItem var14;
               for(float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; par1World.spawnEntityInWorld(var14)) {
                  int var13 = this.random.nextInt(21) + 10;
                  if (var13 > var9.stackSize) {
                     var13 = var9.stackSize;
                  }

                  var9.stackSize -= var13;
                  var14 = new EntityItem(par1World, (double)((float)par2 + var10), (double)((float)par3 + var11), (double)((float)par4 + var12), new ItemStack(var9.itemID, var13, var9.getItemSubtype()));
                  if (var9.isItemDamaged()) {
                     var14.getEntityItem().setItemDamage(var9.getItemDamage());
                  }

                  float var15 = 0.05F;
                  var14.motionX = (double)((float)this.random.nextGaussian() * var15);
                  var14.motionY = (double)((float)this.random.nextGaussian() * var15 + 0.2F);
                  var14.motionZ = (double)((float)this.random.nextGaussian() * var15);
                  if (var9.getItem().hasQuality()) {
                     var14.getEntityItem().setQuality(var9.getQuality());
                  }

                  if (var9.hasTagCompound()) {
                     var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                  }
               }
            }
         }

         par1World.func_96440_m(par2, par3, par4, par5);
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public int[] getUnifiedNeighborCoordinates(World world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      if (metadata != 2 && metadata != 3) {
         if (metadata != 4 && metadata != 5) {
            return null;
         } else if (world.getBlock(x, y, z - 1) == this) {
            return new int[]{x, y, z - 1};
         } else {
            return world.getBlock(x, y, z + 1) == this ? new int[]{x, y, z + 1} : null;
         }
      } else if (world.getBlock(x - 1, y, z) == this) {
         return new int[]{x - 1, y, z};
      } else {
         return world.getBlock(x + 1, y, z) == this ? new int[]{x + 1, y, z} : null;
      }
   }

   public boolean canOpenChest(World world, int x, int y, int z, EntityLivingBase entity_living_base) {
      if (world.isAirOrPassableBlock(x, y + 1, z, true) && !entity_living_base.hasCurse(Curse.cannot_open_chests, true)) {
         int[] coords = this.getUnifiedNeighborCoordinates(world, x, y, z);
         return coords == null || world.isAirOrPassableBlock(coords[0], coords[1] + 1, coords[2], true);
      } else {
         return false;
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float dx, float dy, float dz) {
      if (player.onServer()) {
         if (this.canOpenChest(world, x, y, z, player)) {
            IInventory inventory = this.getInventory(world, x, y, z);
            if (inventory != null) {
               player.displayGUIChest(x, y, z, inventory);
            }
         } else {
            world.playSoundAtBlock(x, y, z, "imported.random.chest_locked", 0.2F);
         }
      }

      return true;
   }

   public IInventory getInventory(World par1World, int par2, int par3, int par4) {
      Object var5 = (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4);
      if (var5 == null) {
         return null;
      } else if (par1World.isBlockSolidOnSide(par2, par3 + 1, par4, ForgeDirection.DOWN)) {
         return null;
      } else if (isOcelotBlockingChest(par1World, par2, par3, par4)) {
         return null;
      }
      else if (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID && (par1World.isBlockSolidOnSide(par2 - 1, par3 + 1, par4, DOWN) || isOcelotBlockingChest(par1World, par2 - 1, par3, par4)))
      {
         return null;
      }
      else if (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID && (par1World.isBlockSolidOnSide(par2 + 1, par3 + 1, par4, DOWN) || isOcelotBlockingChest(par1World, par2 + 1, par3, par4)))
      {
         return null;
      }
      else if (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID && (par1World.isBlockSolidOnSide(par2, par3 + 1, par4 - 1, DOWN) || isOcelotBlockingChest(par1World, par2, par3, par4 - 1)))
      {
         return null;
      }
      else if (par1World.getBlockId(par2, par3, par4 + 1) == this.blockID && (par1World.isBlockSolidOnSide(par2, par3 + 1, par4 + 1, DOWN) || isOcelotBlockingChest(par1World, par2, par3, par4 + 1))) {
         if (par1World.getBlockId(par2, par3, par4 + 1) != this.blockID || !par1World.isBlockNormalCube(par2, par3 + 1, par4 + 1) && !isOcelotBlockingChest(par1World, par2, par3, par4 + 1)) {
            if (this instanceof BlockStrongbox) {
               return (IInventory)var5;
            } else {
               if (par1World.getBlockId(par2 - 1, par3, par4) == this.blockID) {
                  var5 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)par1World.getBlockTileEntity(par2 - 1, par3, par4), (IInventory)var5);
               }

               if (par1World.getBlockId(par2 + 1, par3, par4) == this.blockID) {
                  var5 = new InventoryLargeChest("container.chestDouble", (IInventory)var5, (TileEntityChest)par1World.getBlockTileEntity(par2 + 1, par3, par4));
               }

               if (par1World.getBlockId(par2, par3, par4 - 1) == this.blockID) {
                  var5 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4 - 1), (IInventory)var5);
               }

               if (par1World.getBlockId(par2, par3, par4 + 1) == this.blockID) {
                  var5 = new InventoryLargeChest("container.chestDouble", (IInventory)var5, (TileEntityChest)par1World.getBlockTileEntity(par2, par3, par4 + 1));
               }

               return (IInventory)var5;
            }
      } else {
         return null;
         }
      } else {
         return null;
      }
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityChest(this.chest_type, this);
   }

   public boolean canProvidePower() {
      return this.chest_type == EnumChestType.trapped;
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (!this.canProvidePower()) {
         return 0;
      } else {
         int var6 = ((TileEntityChest)par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).numUsingPlayers;
         return MathHelper.clamp_int(var6, 0, 15);
      }
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 == 1 ? this.isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5) : 0;
   }

   private static boolean isOcelotBlockingChest(World par0World, int par1, int par2, int par3) {
      Iterator var4 = par0World.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getAABBPool().getAABB((double)par1, (double)(par2 + 1), (double)par3, (double)(par1 + 1), (double)(par2 + 2), (double)(par3 + 1))).iterator();

      while(var4.hasNext()) {
         EntityOcelot var5 = (EntityOcelot)var4.next();
         EntityOcelot var6 = var5;
         if (var6.isSitting()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      return Container.calcRedstoneFromInventory(this.getInventory(par1World, par2, par3, par4));
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("planks_oak");
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return empty_handed;
   }

   public boolean canBePlacedOnBlock(int metadata, Block block_below, int block_below_metadata, double block_below_bounds_max_y) {
      return block_below.isBlockTopFacingSurfaceSolid(block_below_metadata) && super.canBePlacedOnBlock(metadata, block_below, block_below_metadata, block_below_bounds_max_y);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
