package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;

public final class BlockTallGrass extends BlockPlant implements IShearable {
   private static final String[] grassTypes = new String[]{"deadbush", "tallgrass", "fern"};
   private Icon[] iconArray;

   protected BlockTallGrass(int id) {
      super(id, Material.vine);
      float size = 0.4F;
      this.setBlockBoundsForAllThreads((double)(0.5F - size), 0.0, (double)(0.5F - size), (double)(0.5F + size), 0.800000011920929, (double)(0.5F + size));
   }

   public Icon getIcon(int par1, int par2) {
      if (par2 >= this.iconArray.length) {
         par2 = 0;
      }

      return this.iconArray[par2];
   }

   public int getBlockColor() {
      double var1 = 0.5;
      double var3 = 1.0;
      return ColorizerGrass.getGrassColor(var1, var3);
   }

   public int getRenderColor(int par1) {
      return par1 == 0 ? 16777215 : ColorizerFoliage.getFoliageColorBasic();
   }

   public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      return var5 == 0 ? 16777215 : par1IBlockAccess.getBiomeGenForCoords(par2, par4).getBiomeGrassColor();
   }

   public String getMetadataNotes() {
      return "1=Grass, 2=Fern";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 1 && metadata < 3;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata == 1 ? 1 : 2;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[grassTypes.length];

      for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
         this.iconArray[var2] = par1IconRegister.registerIcon(grassTypes[var2]);
      }

   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (!info.wasSelfDropped() && !info.wasNotLegal()) {
         return info.wasHarvestedByPlayer() && this.getItemSubtype(info.getMetadata()) == 1 ? this.dropBlockAsEntityItem(info, Item.seeds.itemID, 0, 1, 0.2F) : 0;
      } else {
         return super.dropBlockAsEntityItem(info);
      }
   }


   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "tall";
   }

   public boolean dropsAsSelfWhenTrampled(Entity entity) {
      return false;
   }

   public void onBlockAdded(World world, int x, int y, int z) {
      super.onBlockAdded(world, x, y, z);
      if (world.getBlockId(x, y - 1, z) == dirt.blockID) {
         world.setBlock(x, y - 1, z, grass.blockID);
      }

   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return other_block != null && other_block != this;
   }

   public boolean showDestructionParticlesWhenReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return true;
   }

   @Override
   public boolean isShearable(ItemStack item, World world, int x, int y, int z)
   {
      return true;
   }

   @Override
   public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune)
   {
      ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
      ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z)));
      return ret;
   }

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z) {
      return EnumPlantType.Unknown;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z) {
      return 0;
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z) {
      return 0;
   }
}
