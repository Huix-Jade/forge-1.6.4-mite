package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
   private Random rand = new Random();
   private Icon theIcon;
   private static final AxisAlignedBB[] multiple_bounds = new AxisAlignedBB[]{new AxisAlignedBB(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0)};

   public BlockBrewingStand(int par1) {
      super(par1, Material.iron, (new BlockConstants()).setNeverHidesAdjacentFaces());
   }

   public int getRenderType() {
      return 25;
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityBrewingStand();
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return multiple_bounds;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         TileEntityBrewingStand tile_entity = (TileEntityBrewingStand)world.getBlockTileEntity(x, y, z);
         if (tile_entity != null) {
            player.displayGUIBrewingStand(tile_entity);
         }
      }

      return true;
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      double var6 = (double)((float)par2 + 0.4F + par5Random.nextFloat() * 0.2F);
      double var8 = (double)((float)par3 + 0.7F + par5Random.nextFloat() * 0.3F);
      double var10 = (double)((float)par4 + 0.4F + par5Random.nextFloat() * 0.2F);
      par1World.spawnParticle(EnumParticle.smoke, var6, var8, var10, 0.0, 0.0, 0.0);
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      TileEntity var7 = par1World.getBlockTileEntity(par2, par3, par4);
      if (var7 instanceof TileEntityBrewingStand) {
         TileEntityBrewingStand var8 = (TileEntityBrewingStand)var7;

         for(int var9 = 0; var9 < var8.getSizeInventory(); ++var9) {
            ItemStack var10 = var8.getStackInSlot(var9);
            if (var10 != null) {
               float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
               float var12 = this.rand.nextFloat() * 0.8F + 0.1F;
               float var13 = this.rand.nextFloat() * 0.8F + 0.1F;

               while(var10.stackSize > 0) {
                  int var14 = this.rand.nextInt(21) + 10;
                  if (var14 > var10.stackSize) {
                     var14 = var10.stackSize;
                  }

                  var10.stackSize -= var14;
                  ItemStack item_stack = new ItemStack(var10.itemID, var14, var10.getItemSubtype());
                  if (var10.isItemDamaged()) {
                     item_stack.setItemDamage(var10.getItemDamage());
                  }

                  EntityItem var15 = new EntityItem(par1World, (double)((float)par2 + var11), (double)((float)par3 + var12), (double)((float)par4 + var13), item_stack);
                  float var16 = 0.05F;
                  var15.motionX = (double)((float)this.rand.nextGaussian() * var16);
                  var15.motionY = (double)((float)this.rand.nextGaussian() * var16 + 0.2F);
                  var15.motionZ = (double)((float)this.rand.nextGaussian() * var16);
                  par1World.spawnEntityInWorld(var15);
               }
            }
         }
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.brewingStand.itemID;
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      return Container.calcRedstoneFromInventory((IInventory)par1World.getBlockTileEntity(par2, par3, par4));
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
      this.theIcon = par1IconRegister.registerIcon(this.getTextureName() + "_base");
   }

   public Icon getBrewingStandIcon() {
      return this.theIcon;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.brewingStand);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
