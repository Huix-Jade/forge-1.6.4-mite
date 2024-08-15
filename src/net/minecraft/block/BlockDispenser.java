package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.IRegistry;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.dispenser.RegistryDefaulted;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public class BlockDispenser extends BlockDirectionalWithTileEntity {
   public static final IRegistry dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
   protected Random random = new Random();
   protected Icon furnaceTopIcon;
   protected Icon furnaceFrontIcon;
   protected Icon field_96473_e;

   protected BlockDispenser(int par1) {
      super(par1, Material.stone, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public String getMetadataNotes() {
      String[] array = new String[6];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=" + this.getDirectionFacing(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 6;
   }

   public int tickRate(World par1World) {
      return 4;
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard6(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata = direction.isDown() ? 0 : (direction.isUp() ? 1 : (direction.isNorth() ? 2 : (direction.isSouth() ? 3 : (direction.isWest() ? 4 : (direction.isEast() ? 5 : -1)))));
      return metadata;
   }

   public Icon getIcon(int par1, int par2) {
      int var3 = par2 & 7;
      return par1 == var3 ? (var3 != 1 && var3 != 0 ? this.furnaceFrontIcon : this.field_96473_e) : (var3 != 1 && var3 != 0 ? (par1 != 1 && par1 != 0 ? this.blockIcon : this.furnaceTopIcon) : this.furnaceTopIcon);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace_side");
      this.furnaceTopIcon = par1IconRegister.registerIcon("furnace_top");
      this.furnaceFrontIcon = par1IconRegister.registerIcon(this.getTextureName() + "_front_horizontal");
      this.field_96473_e = par1IconRegister.registerIcon(this.getTextureName() + "_front_vertical");
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float dx, float dy, float dz) {
      if (!world.isAirOrPassableBlock(World.getNeighboringBlockCoords(x, y, z, this.getDirectionFacing(world.getBlockMetadata(x, y, z)).getFace()), true)) {
         return false;
      } else {
         if (player.onServer()) {
            TileEntityDispenser tile_entity = (TileEntityDispenser)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               player.displayGUIDispenser(tile_entity);
            }
         }

         return true;
      }
   }

   protected void dispense(World par1World, int par2, int par3, int par4) {
      BlockSourceImpl var5 = new BlockSourceImpl(par1World, par2, par3, par4);
      TileEntityDispenser var6 = (TileEntityDispenser)var5.getBlockTileEntity();
      if (var6 != null) {
         int var7 = var6.getRandomStackFromInventory();
         if (var7 < 0) {
            par1World.playAuxSFX(1001, par2, par3, par4, 0);
         } else {
            ItemStack var8 = var6.getStackInSlot(var7);
            IBehaviorDispenseItem var9 = this.getBehaviorForItemStack(var8);
            if (var9 != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
               ItemStack var10 = var9.dispense(var5, var8);
               var6.setInventorySlotContents(var7, var10.stackSize == 0 ? null : var10);
            }
         }
      }

   }

   protected IBehaviorDispenseItem getBehaviorForItemStack(ItemStack par1ItemStack) {
      return (IBehaviorDispenseItem)dispenseBehaviorRegistry.getObject(par1ItemStack.getItem());
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      boolean is_indirectly_powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
      int metadata = world.getBlockMetadata(x, y, z);
      boolean var8 = (metadata & 8) != 0;
      if (is_indirectly_powered && !var8) {
         world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
         world.setBlockMetadataWithNotify(x, y, z, metadata | 8, 4);
         return true;
      } else if (!is_indirectly_powered && var8) {
         world.setBlockMetadataWithNotify(x, y, z, metadata & -9, 4);
         return true;
      } else {
         return false;
      }
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      this.dispense(world, x, y, z);
      return false;
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityDispenser();
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      TileEntityDispenser var7 = (TileEntityDispenser)par1World.getBlockTileEntity(par2, par3, par4);
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
            ItemStack var9 = var7.getStackInSlot(var8);
            if (var9 != null) {
               float var10 = this.random.nextFloat() * 0.8F + 0.1F;
               float var11 = this.random.nextFloat() * 0.8F + 0.1F;
               float var12 = this.random.nextFloat() * 0.8F + 0.1F;

               while(var9.stackSize > 0) {
                  int var13 = this.random.nextInt(21) + 10;
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
                  var14.motionX = (double)((float)this.random.nextGaussian() * var15);
                  var14.motionY = (double)((float)this.random.nextGaussian() * var15 + 0.2F);
                  var14.motionZ = (double)((float)this.random.nextGaussian() * var15);
                  par1World.spawnEntityInWorld(var14);
               }
            }
         }

         par1World.func_96440_m(par2, par3, par4, par5);
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public static IPosition getIPositionFromBlockSource(IBlockSource par0IBlockSource) {
      EnumFacing var1 = getFacing(par0IBlockSource.getBlockMetadata());
      double var2 = par0IBlockSource.getX() + 0.7 * (double)var1.getFrontOffsetX();
      double var4 = par0IBlockSource.getY() + 0.7 * (double)var1.getFrontOffsetY();
      double var6 = par0IBlockSource.getZ() + 0.7 * (double)var1.getFrontOffsetZ();
      return new PositionImpl(var2, var4, var6);
   }

   public static EnumFacing getFacing(int par0) {
      return EnumFacing.getFront(par0 & 7);
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      return Container.calcRedstoneFromInventory((IInventory)par1World.getBlockTileEntity(par2, par3, par4));
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.stone, Material.wood, Material.redstone});
   }
}
