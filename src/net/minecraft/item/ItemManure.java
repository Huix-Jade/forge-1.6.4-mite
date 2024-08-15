package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class ItemManure extends Item {
   public ItemManure(int id) {
      super(id, Material.manure, "manure");
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   private boolean tryFertilizeBlock(World world, int x, int y, int z, EnumFace face, ItemStack item_stack, EntityPlayer player) {
      Block block = world.getBlock(x, y, z);
      if (block instanceof BlockMushroom) {
         Block block_below = world.getBlock(x, y - 1, z);
         if (block_below == Block.tilledField) {
            return this.tryFertilizeBlock(world, x, y - 1, z, EnumFace.TOP, item_stack, player);
         } else {
            BlockMushroom block_mushroom = (BlockMushroom)block;
            if (!block_mushroom.isLegalAt(world, x, y, z, world.getBlockMetadata(x, y, z))) {
               return false;
            } else {
               if (block == Block.mushroomRed) {
                  if (block_below != Block.grass) {
                     return false;
                  }

                  if (!world.isOutdoors(x, y, z)) {
                     return false;
                  }
               } else {
                  if (block_below != Block.mycelium) {
                     return false;
                  }

                  if (world.isOutdoors(x, y, z)) {
                     return false;
                  }
               }

               if (!world.isRemote && world.rand.nextFloat() < 0.5F) {
                  block_mushroom.fertilizeMushroom(world, x, y, z, item_stack, player);
               }

               if (!world.isRemote) {
                  world.blockFX(EnumBlockFX.manure, x, y, z);
               }

               return true;
            }
         }
      } else if (!(block instanceof BlockCrops) && !(block instanceof BlockStem) && block != Block.mushroomBrown) {
         if (block == Block.grass && face.isTop() && world.getBlock(x, y + 1, z) == Block.mushroomRed) {
            return this.tryFertilizeBlock(world, x, y + 1, z, EnumFace.TOP, item_stack, player);
         } else if (block == Block.mycelium && face.isTop()) {
            return ((BlockMycelium)block).fertilize(world, x, y, z, item_stack, player);
         } else {
            boolean var11;
            if (block == Block.tilledField && face.isTop()) {
               BlockFarmland var10000 = (BlockFarmland)block;
               if (BlockFarmland.fertilize(world, x, y, z, item_stack, player)) {
                  var11 = true;
                  return var11;
               }
            }

            var11 = false;
            return var11;
         }
      } else {
         return this.tryFertilizeBlock(world, x, y - 1, z, EnumFace.TOP, item_stack, player);
      }
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && this.tryFertilizeBlock(rc.world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, rc.face_hit, player.getHeldItemStack(), player)) {
         if (player.onClient()) {
            player.swingArm();
         } else if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void particleEffect(World world, int x, int y, int z, int num_particles) {
      if (num_particles == 0) {
         num_particles = 4;
      }

      Block block = Block.getBlock(world.getBlockId(x, y, z));
      if (block != null) {
         block.setBlockBoundsBasedOnStateAndNeighbors(world, x, y, z);
      }

      int index = Minecraft.getThreadIndex();

      for(int i = 0; i < num_particles; ++i) {
         double vx = itemRand.nextGaussian() * 0.02;
         double vy = itemRand.nextGaussian() * 0.02;
         double vz = itemRand.nextGaussian() * 0.02;
         world.spawnParticle(EnumParticle.manure, (double)((float)x + itemRand.nextFloat()), (double)y + (block == null ? 1.0 : block.getBlockBoundsMaxY(index)) + 0.15000000596046448 + (double)(itemRand.nextFloat() * 0.1F), (double)((float)z + itemRand.nextFloat()), vx, vy, vz);
      }

   }
}
