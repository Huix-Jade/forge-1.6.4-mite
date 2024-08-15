package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
   private Icon field_94461_a;
   private Icon field_94460_b;
   private Material gem_type;

   protected BlockEnchantmentTable(int par1, Material gem_type) {
      super(par1, Material.stone, new BlockConstants());
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
      this.setLightOpacity(255);
      this.gem_type = gem_type;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      super.randomDisplayTick(par1World, par2, par3, par4, par5Random);

      for(int var6 = par2 - 2; var6 <= par2 + 2; ++var6) {
         for(int var7 = par4 - 2; var7 <= par4 + 2; ++var7) {
            if (var6 > par2 - 2 && var6 < par2 + 2 && var7 == par4 - 1) {
               var7 = par4 + 2;
            }

            if (par5Random.nextInt(16) == 0) {
               for(int var8 = par3; var8 <= par3 + 1; ++var8) {
                  if (par1World.getBlockId(var6, var8, var7) == Block.bookShelf.blockID) {
                     if (!par1World.isAirBlock((var6 - par2) / 2 + par2, var8, (var7 - par4) / 2 + par4)) {
                        break;
                     }

                     par1World.spawnParticle(EnumParticle.enchantmenttable, (double)par2 + 0.5, (double)par3 + 2.0, (double)par4 + 0.5, (double)((float)(var6 - par2) + par5Random.nextFloat()) - 0.5, (double)((float)(var8 - par3) - par5Random.nextFloat() - 1.0F), (double)((float)(var7 - par4) + par5Random.nextFloat()) - 0.5);
                  }
               }
            }
         }
      }

   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 0 ? this.field_94460_b : (par1 == 1 ? this.field_94461_a : this.blockIcon);
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityEnchantmentTable();
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!world.isAirOrPassableBlock(x, y + 1, z, false)) {
         return false;
      } else {
         if (player.onServer()) {
            TileEntityEnchantmentTable tile_entity = (TileEntityEnchantmentTable)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               player.displayGUIEnchantment(x, y, z, tile_entity.func_94135_b() ? tile_entity.func_94133_a() : null);
            }
         }

         return true;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_" + "side");
      this.field_94461_a = par1IconRegister.registerIcon(this.getTextureName() + "_" + "top");
      this.field_94460_b = par1IconRegister.registerIcon("enchanting_table_bottom");
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.obsidian, this.gem_type, Material.paper, Material.leather});
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.gem_type.name;
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      return face.isBottom();
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return side == 1;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
