package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemEnderEye extends Item {
   public ItemEnderEye(int par1) {
      super(par1, new Material[]{Material.ender_pearl, Material.blaze}, "ender_eye");
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   private boolean tryInsertEyeIntoFrame(World world, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      if (block != Block.endPortalFrame) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         if (BlockEndPortalFrame.isEnderEyeInserted(metadata)) {
            return false;
         } else if (world.isRemote) {
            return true;
         } else {
            world.setBlockMetadataWithNotify(x, y, z, metadata + 4, 2);
            world.func_96440_m(x, y, z, Block.endPortalFrame.blockID);

            int var13;
            for(var13 = 0; var13 < 16; ++var13) {
               double var14 = (double)((float)x + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
               double var16 = (double)((float)y + 0.8125F);
               double var18 = (double)((float)z + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
               double var20 = 0.0;
               double var22 = 0.0;
               double var24 = 0.0;
               world.spawnParticle(EnumParticle.smoke, var14, var16, var18, var20, var22, var24);
            }

            var13 = metadata & 3;
            int var26 = 0;
            int var15 = 0;
            boolean var27 = false;
            boolean var17 = true;
            int var28 = Direction.rotateRight[var13];

            int var19;
            int var23;
            int var30;
            int var21;
            int var29;
            for(var19 = -2; var19 <= 2; ++var19) {
               var29 = x + Direction.offsetX[var28] * var19;
               var21 = z + Direction.offsetZ[var28] * var19;
               var30 = world.getBlockId(var29, y, var21);
               if (var30 == Block.endPortalFrame.blockID) {
                  var23 = world.getBlockMetadata(var29, y, var21);
                  if (!BlockEndPortalFrame.isEnderEyeInserted(var23)) {
                     var17 = false;
                     break;
                  }

                  var15 = var19;
                  if (!var27) {
                     var26 = var19;
                     var27 = true;
                  }
               }
            }

            if (var17 && var15 == var26 + 2) {
               for(var19 = var26; var19 <= var15; ++var19) {
                  var29 = x + Direction.offsetX[var28] * var19;
                  var21 = z + Direction.offsetZ[var28] * var19;
                  var29 += Direction.offsetX[var13] * 4;
                  var21 += Direction.offsetZ[var13] * 4;
                  var30 = world.getBlockId(var29, y, var21);
                  var23 = world.getBlockMetadata(var29, y, var21);
                  if (var30 != Block.endPortalFrame.blockID || !BlockEndPortalFrame.isEnderEyeInserted(var23)) {
                     var17 = false;
                     break;
                  }
               }

               for(var19 = var26 - 1; var19 <= var15 + 1; var19 += 4) {
                  for(var29 = 1; var29 <= 3; ++var29) {
                     var21 = x + Direction.offsetX[var28] * var19;
                     var30 = z + Direction.offsetZ[var28] * var19;
                     var21 += Direction.offsetX[var13] * var29;
                     var30 += Direction.offsetZ[var13] * var29;
                     var23 = world.getBlockId(var21, y, var30);
                     int var31 = world.getBlockMetadata(var21, y, var30);
                     if (var23 != Block.endPortalFrame.blockID || !BlockEndPortalFrame.isEnderEyeInserted(var31)) {
                        var17 = false;
                        break;
                     }
                  }
               }

               if (var17) {
                  for(var19 = var26; var19 <= var15; ++var19) {
                     for(var29 = 1; var29 <= 3; ++var29) {
                        var21 = x + Direction.offsetX[var28] * var19;
                        var30 = z + Direction.offsetZ[var28] * var19;
                        var21 += Direction.offsetX[var13] * var29;
                        var30 += Direction.offsetZ[var13] * var29;
                        world.setBlock(var21, y, var30, Block.endPortal.blockID, 0, 2);
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && rc.getBlockHit() == Block.endPortalFrame && !BlockEndPortalFrame.isEnderEyeInserted(rc.block_hit_metadata) && this.tryInsertEyeIntoFrame(player.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z)) {
         if (player.onClient()) {
            player.swingArm();
         } else if (!player.inCreativeMode()) {
            player.convertOneOfHeldItem((ItemStack)null);
         }

         return true;
      } else if (player.worldObj.provider.dimensionId != 0) {
         return false;
      } else {
         if (player.onClient()) {
            player.swingArm();
         } else {
            WorldServer world = player.getWorldServer();
            ChunkPosition chunk_pos = world.findClosestStructure("Stronghold", (int)player.posX, (int)player.posY, (int)player.posZ);
            if (chunk_pos != null) {
               EntityEnderEye eye = new EntityEnderEye(world, player.posX, player.posY + 1.62 - (double)player.yOffset, player.posZ);
               eye.moveTowards((double)chunk_pos.x, chunk_pos.y, (double)chunk_pos.z);
               world.spawnEntityInWorld(eye);
               world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
               world.playAuxSFXAtEntity((EntityPlayer)null, 1002, (int)player.posX, (int)player.posY, (int)player.posZ, 0);
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }
         }

         return true;
      }
   }
}
