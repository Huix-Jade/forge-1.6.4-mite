package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public class BlockFlowerPot extends Block {
   public BlockFlowerPot(int par1) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setBounds(true);
   }

   public String getMetadataNotes() {
      String[] array = new String[16];

      for(int i = 0; i < 16; ++i) {
         if (this.isValidMetadata(i)) {
            ItemStack item_stack = getPlantForMeta(i);
            StringHelper.addToStringArray(i + "=" + (item_stack == null ? "Empty" : item_stack.getNameForReferenceFile()), array);
         }
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 13;
   }

   private void setBounds(boolean for_all_threads) {
      float var1 = 0.375F;
      float var2 = var1 / 2.0F;
      this.setBlockBounds((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), (double)var1, (double)(0.5F + var2), for_all_threads);
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBounds(false);
   }

   public int getRenderType() {
      return 33;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      ItemStack item_stack = player.getHeldItemStack();
      if (item_stack == null) {
         return false;
      } else {
         int metadata_for_plant;
         if (BlockFlowerPotMulti.a(item_stack) != 0) {
            if (player.onServer()) {
               metadata_for_plant = world.getBlockMetadata(x, y, z);
               if (metadata_for_plant != 0) {
                  BlockBreakInfo info = new BlockBreakInfo(world, x, y, z);
                  this.dropBlockAsEntityItem(info, getPlantForMeta(metadata_for_plant));
                  world.playSoundAtBlock(x, y, z, "random.pop", 0.1F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
               }

               world.setBlock(x, y, z, flowerPotMulti.blockID, BlockFlowerPotMulti.a(item_stack), 2);
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }

            return true;
         } else {
            metadata_for_plant = getMetaForPlant(item_stack);
            if (metadata_for_plant == 0) {
               return false;
            } else {
               int metadata = world.getBlockMetadata(x, y, z);
               if (metadata == metadata_for_plant) {
                  return false;
               } else {
                  if (player.onServer()) {
                     if (metadata != 0) {
                        BlockBreakInfo info = new BlockBreakInfo(world, x, y, z);
                        this.dropBlockAsEntityItem(info, getPlantForMeta(metadata));
                        world.playSoundAtBlock(x, y, z, "random.pop", 0.1F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                     }

                     world.setBlockMetadataWithNotify(x, y, z, metadata_for_plant, 2);
                     if (!player.inCreativeMode()) {
                        player.convertOneOfHeldItem((ItemStack)null);
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      ItemStack var5 = getPlantForMeta(par1World.getBlockMetadata(par2, par3, par4));
      return var5 == null ? Item.flowerPot.itemID : var5.itemID;
   }

   public boolean isFlowerPot() {
      return true;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below != null && block_below != leaves && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (!info.wasExploded() && !info.wasCrushed()) {
         int num_drops;
         return (num_drops = super.dropBlockAsEntityItem(info, Item.flowerPot)) > 0 ? num_drops + this.dropBlockAsEntityItem(info, getPlantForMeta(info.getMetadata())) : 0;
      } else {
         return 0;
      }
   }

   public static ItemStack getPlantForMeta(int par0) {
      switch (par0) {
         case 1:
            return new ItemStack(Block.plantRed);
         case 2:
            return new ItemStack(Block.plantYellow);
         case 3:
            return new ItemStack(Block.sapling, 1, 0);
         case 4:
            return new ItemStack(Block.sapling, 1, 1);
         case 5:
            return new ItemStack(Block.sapling, 1, 2);
         case 6:
            return new ItemStack(Block.sapling, 1, 3);
         case 7:
            return new ItemStack(Block.mushroomRed);
         case 8:
            return new ItemStack(Block.mushroomBrown);
         case 9:
            return new ItemStack(Block.cactus);
         case 10:
            return new ItemStack(Block.deadBush);
         case 11:
            return new ItemStack(Block.tallGrass, 1, 2);
         case 12:
            return new ItemStack(Block.deadBush, 1, 1);
         default:
            return null;
      }
   }

   public static int getMetaForPlant(ItemStack par0ItemStack) {
      int var1 = par0ItemStack.getItem().itemID;
      if (var1 == Block.plantRed.blockID) {
         return par0ItemStack.getItemSubtype() == 0 ? 1 : 0;
      } else if (var1 == Block.plantYellow.blockID) {
         return 2;
      } else if (var1 == Block.cactus.blockID) {
         return 9;
      } else if (var1 == Block.mushroomBrown.blockID) {
         return 8;
      } else if (var1 == Block.mushroomRed.blockID) {
         return 7;
      } else if (var1 == Block.deadBush.blockID) {
         return deadBush.isWitherwood(par0ItemStack.getItemSubtype()) ? 12 : 10;
      } else {
         if (var1 == Block.sapling.blockID) {
            switch (par0ItemStack.getItemSubtype()) {
               case 0:
                  return 3;
               case 1:
                  return 4;
               case 2:
                  return 5;
               case 3:
                  return 6;
            }
         }

         if (var1 == Block.tallGrass.blockID) {
            switch (par0ItemStack.getItemSubtype()) {
               case 2:
                  return 11;
            }
         }

         return 0;
      }
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.clay});
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return false;
   }

   private boolean isWitherwood(World world, int x, int y, int z) {
      return world.getBlockMetadata(x, y, z) == getMetaForPlant(new ItemStack(deadBush, 1, 1));
   }

   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      if (this.isWitherwood(world, x, y, z)) {
         BlockDeadBush.spawnWitherwoodParticles(world, x, y, z, random);
      }

   }

   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if (world.isWorldServer() && entity instanceof EntityLivingBase && this.isWitherwood(world, x, y, z)) {
         BlockDeadBush.addWitherEffect(entity.getAsEntityLivingBase());
      }

   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return false;
   }
}
