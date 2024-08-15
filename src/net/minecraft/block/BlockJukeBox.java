package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityRecordPlayer;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockJukeBox extends BlockContainer {
   private Icon theIcon;

   protected BlockJukeBox(int par1) {
      super(par1, Material.wood, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.theIcon : this.blockIcon;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!world.isAirOrPassableBlock(x, y + 1, z, false)) {
         return false;
      } else if (world.getBlockMetadata(x, y, z) == 0) {
         return false;
      } else {
         if (player.onServer()) {
            this.ejectRecord(world, x, y, z);
         }

         return true;
      }
   }

   public void insertRecord(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack) {
      if (!par1World.isRemote) {
         TileEntityRecordPlayer var6 = (TileEntityRecordPlayer)par1World.getBlockTileEntity(par2, par3, par4);
         if (var6 != null) {
            var6.func_96098_a(par5ItemStack.copy());
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
         }
      }

   }

   public void ejectRecord(World par1World, int par2, int par3, int par4) {
      if (!par1World.isRemote) {
         TileEntityRecordPlayer var5 = (TileEntityRecordPlayer)par1World.getBlockTileEntity(par2, par3, par4);
         if (var5 != null) {
            ItemStack var6 = var5.func_96097_a();
            if (var6 != null) {
               par1World.playAuxSFX(1005, par2, par3, par4, 0);
               par1World.playRecord((String)null, par2, par3, par4);
               var5.func_96098_a((ItemStack)null);
               par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 2);
               float var7 = 0.7F;
               double var8 = (double)(par1World.rand.nextFloat() * var7) + (double)(1.0F - var7) * 0.5;
               double var10 = (double)(par1World.rand.nextFloat() * var7) + (double)(1.0F - var7) * 0.2 + 0.6;
               double var12 = (double)(par1World.rand.nextFloat() * var7) + (double)(1.0F - var7) * 0.5;
               ItemStack var14 = var6.copy();
               EntityItem var15 = new EntityItem(par1World, (double)par2 + var8, (double)par3 + var10, (double)par4 + var12, var14);
               var15.delayBeforeCanPickup = 10;
               par1World.spawnEntityInWorld(var15);
            }
         }
      }

   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      this.ejectRecord(par1World, par2, par3, par4);
      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityRecordPlayer();
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.theIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      ItemStack var6 = ((TileEntityRecordPlayer)par1World.getBlockTileEntity(par2, par3, par4)).func_96097_a();
      return var6 == null ? 0 : var6.itemID + 1 - Item.record13.itemID;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.diamond});
   }
}
