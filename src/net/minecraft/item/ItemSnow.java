package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumFace;
import net.minecraft.util.ReferenceFileWriter;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlockWithMetadata {
   public ItemSnow(Block block) {
      super(block);
   }

   public static boolean tryAddToExistingSnow(RaycastCollision rc, EntityPlayer player, int layers_to_place) {
      World world = rc.world;
      int max_depth = BlockSnow.getMaxDepth();
      int x;
      int y;
      int z;
      int existing_layers;
      if (rc.getBlockHit() == Block.snow && BlockSnow.getDepth(rc.block_hit_metadata) < max_depth) {
         x = rc.block_hit_x;
         y = rc.block_hit_y;
         z = rc.block_hit_z;
         existing_layers = BlockSnow.getDepth(rc.block_hit_metadata);
      } else {
         if (rc.getNeighborOfBlockHit() != Block.snow || BlockSnow.getDepth(rc.getNeighborOfBlockHitMetadata()) >= max_depth) {
            return false;
         }

         x = rc.neighbor_block_x;
         y = rc.neighbor_block_y;
         z = rc.neighbor_block_z;
         existing_layers = BlockSnow.getDepth(rc.getNeighborOfBlockHitMetadata());
      }

      layers_to_place += existing_layers;
      if (layers_to_place <= max_depth) {
         return Block.snow.tryPlaceBlock(world, x, y, z, EnumFace.TOP, layers_to_place - 1, player, false, false);
      } else {
         int metadata = layers_to_place - max_depth - 1;
         if (!world.isAirBlock(x, y + 1, z) && !Block.snow.canReplaceBlock(metadata, world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z))) {
            return false;
         } else {
            return Block.snow.tryPlaceBlock(world, x, y, z, EnumFace.TOP, BlockSnow.getDepthBits(), player, false, false) && Block.snow.tryPlaceBlock(world, x, y + 1, z, EnumFace.TOP, metadata, player, false, true);
         }
      }
   }

   public boolean tryPlaceAsBlock(RaycastCollision rc, Block block, EntityPlayer player, ItemStack item_stack) {
      if (block != Block.snow) {
         Minecraft.setErrorMessage("tryPlaceAsBlock: block!=Block.snow");
      }

      if (block == Block.snow && !block.canReplaceBlock(block.getMetadataForPlacement(player.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, item_stack, player, EnumFace.TOP, 0.0F, 0.0F, 0.0F), rc.getBlockHit(), rc.block_hit_metadata)) {
         return tryAddToExistingSnow(rc, player, item_stack.getItemSubtype() + 1) || super.tryPlaceAsBlock(rc, block, player, item_stack);
      } else {
         return super.tryPlaceAsBlock(rc, block, player, item_stack);
      }
   }

   public int getItemStackLimit(int subtype, int damage) {
      return subtype == 0 ? super.getItemStackLimit(subtype, damage) : 8;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return super.getUnlocalizedName(par1ItemStack) + (par1ItemStack != null && !ReferenceFileWriter.running && par1ItemStack.getItemSubtype() == 3 ? ".slab" : "");
   }
}
