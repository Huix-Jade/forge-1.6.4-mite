package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockMantleOrCore extends Block implements IBlockWithSubtypes {
   public static final int METADATA_MANTLE = 0;
   public static final int METADATA_CORE = 1;
   public static final int SUBTYPE_MANTLE = 0;
   public static final int SUBTYPE_CORE = 1;
   private BlockSubtypes subtypes = new BlockSubtypes(new String[]{"mantle", "core"});

   public BlockMantleOrCore(int id, Material material, BlockConstants constants) {
      super(id, material, constants);
      this.setTickRandomly(true);
   }

   public boolean canBeCarried() {
      return false;
   }

   public String getMetadataNotes() {
      return "0=Mantle, 1=Core";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 2;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata;
   }

   public boolean isMantle(int metadata) {
      return isMantle(this, metadata);
   }

   public static boolean isMantle(Block block, int metadata) {
      return block == mantleOrCore && block.getBlockSubtype(metadata) == 0;
   }

   public boolean isCore(int metadata) {
      return isCore(this, metadata);
   }

   public static boolean isCore(Block block, int metadata) {
      return block == mantleOrCore && block.getBlockSubtype(metadata) == 1;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.subtypes.setIcons(this.registerIcons(par1IconRegister, this.getTextures()));
   }

   public Icon getIcon(int side, int metadata) {
      return this.subtypes.getIcon(this.getBlockSubtype(metadata));
   }

   public String[] getTextures() {
      return this.subtypes.getTextures();
   }

   public String[] getNames() {
      return this.subtypes.getNames();
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      int var6 = par5Random.nextInt(3);

      int var7;
      int var8;
      for(var7 = 0; var7 < var6; ++var7) {
         par2 += par5Random.nextInt(3) - 1;
         ++par3;
         par4 += par5Random.nextInt(3) - 1;
         var8 = par1World.getBlockId(par2, par3, par4);
         if (var8 == 0) {
            if (BlockStationary.isFlammable(par1World, par2 - 1, par3, par4) || BlockStationary.isFlammable(par1World, par2 + 1, par3, par4) || BlockStationary.isFlammable(par1World, par2, par3, par4 - 1) || BlockStationary.isFlammable(par1World, par2, par3, par4 + 1) || BlockStationary.isFlammable(par1World, par2, par3 - 1, par4) || BlockStationary.isFlammable(par1World, par2, par3 + 1, par4)) {
               par1World.setBlock(par2, par3, par4, Block.fire.blockID);
               return false;
            }
         } else if (getBlock(var8).isSolid(par1World, par2, par3, par4)) {
            return false;
         }
      }

      if (var6 == 0) {
         var7 = par2;
         var8 = par4;

         for(int var9 = 0; var9 < 3; ++var9) {
            par2 = var7 + par5Random.nextInt(3) - 1;
            par4 = var8 + par5Random.nextInt(3) - 1;
            if (par1World.isAirBlock(par2, par3 + 1, par4) && BlockStationary.isFlammable(par1World, par2, par3, par4)) {
               par1World.setBlock(par2, par3 + 1, par4, Block.fire.blockID);
            }
         }
      }

      return false;
   }
}
