package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
   private boolean glowing;

   public BlockRedstoneOre(int par1, boolean par2) {
      super(par1, Material.stone, new BlockConstants());
      if (par2) {
         this.setTickRandomly(true);
      }

      this.glowing = par2;
   }

   public int tickRate(World par1World) {
      return 30;
   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
      this.glow(par1World, par2, par3, par4);
      super.onBlockClicked(par1World, par2, par3, par4, par5EntityPlayer);
   }

   public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      this.glow(par1World, par2, par3, par4);
      super.onEntityWalking(par1World, par2, par3, par4, par5Entity);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (!this.glowing) {
         if (player.onClient()) {
            this.sparkle(world, x, y, z);
         } else {
            this.glow(world, x, y, z);
         }

         return true;
      } else {
         return false;
      }
   }

   public void onBlockDamageStageChange(int x, int y, int z, Entity entity, int damage_stage) {
      if (entity != null && !entity.onClient()) {
         if (damage_stage > -1 && entity.worldObj.getBlock(x, y, z) == oreRedstone) {
            this.glow(entity.worldObj, x, y, z);
         }

      }
   }

   private void glow(World world, int x, int y, int z) {
      if (!world.isRemote && this == Block.oreRedstone) {
         world.setBlock(x, y, z, Block.oreRedstoneGlowing.blockID, world.getBlockMetadata(x, y, z), 3);
      }

   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      return this == Block.oreRedstoneGlowing ? world.setBlock(x, y, z, Block.oreRedstone.blockID, world.getBlockMetadata(x, y, z), 3) : false;
   }

   public void onBlockAboutToBeBroken(BlockBreakInfo info) {
      if (info.block == oreRedstoneGlowing) {
         info.setBlock(oreRedstone, info.getMetadata(), 0);
      }

   }

   public boolean canBeCarried() {
      return !this.glowing;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.redstone.itemID, 0, 3 + info.world.rand.nextInt(3), 1.0F + (float)info.getHarvesterFortune() * 0.1F);
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (this.glowing) {
         this.sparkle(par1World, par2, par3, par4);
      }

   }

   private void sparkle(World par1World, int par2, int par3, int par4) {
      if (par1World.isRemote) {
         Random var5 = par1World.rand;
         double var6 = 0.0625;

         for(int var8 = 0; var8 < 6; ++var8) {
            double var9 = (double)((float)par2 + var5.nextFloat());
            double var11 = (double)((float)par3 + var5.nextFloat());
            double var13 = (double)((float)par4 + var5.nextFloat());
            if (var8 == 0 && !par1World.isBlockStandardFormOpaqueCube(par2, par3 + 1, par4)) {
               var11 = (double)(par3 + 1) + var6;
            }

            if (var8 == 1 && !par1World.isBlockStandardFormOpaqueCube(par2, par3 - 1, par4)) {
               var11 = (double)(par3 + 0) - var6;
            }

            if (var8 == 2 && !par1World.isBlockStandardFormOpaqueCube(par2, par3, par4 + 1)) {
               var13 = (double)(par4 + 1) + var6;
            }

            if (var8 == 3 && !par1World.isBlockStandardFormOpaqueCube(par2, par3, par4 - 1)) {
               var13 = (double)(par4 + 0) - var6;
            }

            if (var8 == 4 && !par1World.isBlockStandardFormOpaqueCube(par2 + 1, par3, par4)) {
               var9 = (double)(par2 + 1) + var6;
            }

            if (var8 == 5 && !par1World.isBlockStandardFormOpaqueCube(par2 - 1, par3, par4)) {
               var9 = (double)(par2 + 0) - var6;
            }

            if (var9 < (double)par2 || var9 > (double)(par2 + 1) || var11 < 0.0 || var11 > (double)(par3 + 1) || var13 < (double)par4 || var13 > (double)(par4 + 1)) {
               par1World.spawnParticle(EnumParticle.reddust, var9, var11, var13, 0.0, 0.0, 0.0);
            }
         }

      }
   }

   public ItemStack createStackedBlock(int par1) {
      return new ItemStack(Block.oreRedstone);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.glowing ? "lit" : "unlit";
   }
}
