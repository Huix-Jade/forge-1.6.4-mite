package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
   private int[] chanceToEncourageFire = new int[256];
   private int[] abilityToCatchFire = new int[256];
   private Icon[] iconArray;
   private static AxisAlignedBB bounding_box = new AxisAlignedBB();

   protected BlockFire(int par1) {
      super(par1, Material.fire, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return "All bits used to track fire intensity";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public void initializeBlock() {
      super.initializeBlock();
      this.setBurnRate(Block.planks.blockID, 5, 20);
      this.setBurnRate(Block.woodDoubleSlab.blockID, 5, 20);
      this.setBurnRate(Block.woodSingleSlab.blockID, 5, 20);
      this.setBurnRate(Block.fence.blockID, 5, 20);
      this.setBurnRate(Block.stairsWoodOak.blockID, 5, 20);
      this.setBurnRate(Block.stairsWoodBirch.blockID, 5, 20);
      this.setBurnRate(Block.stairsWoodSpruce.blockID, 5, 20);
      this.setBurnRate(Block.stairsWoodJungle.blockID, 5, 20);
      this.setBurnRate(Block.wood.blockID, 5, 5);
      this.setBurnRate(Block.leaves.blockID, 30, 60);
      this.setBurnRate(Block.bookShelf.blockID, 30, 20);
      this.setBurnRate(Block.tnt.blockID, 15, 100);
      this.setBurnRate(Block.tallGrass.blockID, 60, 100);
      this.setBurnRate(Block.cloth.blockID, 30, 60);
      this.setBurnRate(Block.vine.blockID, 15, 100);
      this.setBurnRate(Block.coalBlock.blockID, 5, 5);
      this.setBurnRate(Block.hay.blockID, 60, 100);
      this.setBurnRate(Block.web.blockID, 60, 100);
      this.setBurnRate(Block.crops.blockID, 60, 100);
      this.setBurnRate(Block.reed.blockID, 30, 50);
      this.setBurnRate(Block.deadBush.blockID, 60, 100);
      this.setBurnRate(Block.bush.blockID, 30, 50);
   }

   private void setBurnRate(int par1, int par2, int par3) {
      this.chanceToEncourageFire[par1] = par2;
      this.abilityToCatchFire[par1] = par3;
   }

   public int getRenderType() {
      return 3;
   }

   public int tickRate(World par1World) {
      return 30;
   }

   public boolean tryExtinguishByItems(World world, int x, int y, int z) {
      List entities = world.getEntitiesWithinAABB(EntityItem.class, bounding_box.setBounds((double)((float)x - 0.125F), (double)y, (double)((float)z - 0.125F), (double)((float)x + 1.125F), (double)(y + 1), (double)((float)z + 1.125F)));
      int num_cookable_items = 0;
      Iterator i = entities.iterator();

      while(i.hasNext()) {
         EntityItem entity_item = (EntityItem)i.next();
         ItemStack item_stack = entity_item.getEntityItem();
         Item item = item_stack == null ? null : item_stack.getItem();
         if (item instanceof ItemFood) {
            ItemFood item_food = (ItemFood)item;
            if (item_food.getCookedItem() != null) {
               num_cookable_items += item_stack.stackSize;
            }
         }
      }

      if (num_cookable_items < 2) {
         return false;
      } else if (num_cookable_items > 15) {
         return world.extinguishFire((EntityPlayer)null, x, y - 1, z, EnumFace.TOP);
      } else {
         float chance_of_extinguishing = 0.01F * (float)Math.pow(2.0, (double)num_cookable_items);
         if (!(chance_of_extinguishing >= 1.0F) && !(world.rand.nextFloat() < chance_of_extinguishing)) {
            return false;
         } else {
            return world.extinguishFire((EntityPlayer)null, x, y - 1, z, EnumFace.TOP);
         }
      }
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else {
         boolean changed_state = false;
         if (par1World.getGameRules().getGameRuleBooleanValue("doFireTick")) {
            boolean var6 = par1World.getBlockId(par2, par3 - 1, par4) == Block.netherrack.blockID;
            if (par1World.provider instanceof WorldProviderEnd && par1World.getBlockId(par2, par3 - 1, par4) == Block.bedrock.blockID) {
               var6 = true;
            }

            int var7 = par1World.getBlockMetadata(par2, par3, par4);
            if (!var6 && par1World.isPrecipitating(true) && (par1World.canLightningStrikeAt(par2, par3, par4) || par1World.canLightningStrikeAt(par2 - 1, par3, par4) || par1World.canLightningStrikeAt(par2 + 1, par3, par4) || par1World.canLightningStrikeAt(par2, par3, par4 - 1) || par1World.canLightningStrikeAt(par2, par3, par4 + 1))) {
               par1World.setBlockToAir(par2, par3, par4);
               changed_state = true;
            } else {
               if (var7 < 15) {
                  par1World.setBlockMetadataWithNotify(par2, par3, par4, var7 + par5Random.nextInt(3) / 2, 4);
                  changed_state = true;
               }

               par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World) + par5Random.nextInt(10));
               if (!var6 && !this.canNeighborBurn(par1World, par2, par3, par4)) {
                  if (!par1World.isBlockTopFlatAndSolid(par2, par3 - 1, par4) || var7 > 3) {
                     par1World.setBlockToAir(par2, par3, par4);
                     changed_state = true;
                  }
               } else if (!var6 && !this.canBlockCatchFire(par1World, par2, par3 - 1, par4) && var7 == 15 && par5Random.nextInt(4) == 0) {
                  par1World.setBlockToAir(par2, par3, par4);
                  changed_state = true;
               } else {
                  boolean var8 = par1World.isBlockHighHumidity(par2, par3, par4);
                  byte var9 = 0;
                  if (var8) {
                     var9 = -50;
                  }

                  this.tryToCatchBlockOnFire(par1World, par2 + 1, par3, par4, 300 + var9, par5Random, var7);
                  this.tryToCatchBlockOnFire(par1World, par2 - 1, par3, par4, 300 + var9, par5Random, var7);
                  this.tryToCatchBlockOnFire(par1World, par2, par3 - 1, par4, 250 + var9, par5Random, var7);
                  this.tryToCatchBlockOnFire(par1World, par2, par3 + 1, par4, 250 + var9, par5Random, var7);
                  this.tryToCatchBlockOnFire(par1World, par2, par3, par4 - 1, 300 + var9, par5Random, var7);
                  this.tryToCatchBlockOnFire(par1World, par2, par3, par4 + 1, 300 + var9, par5Random, var7);

                  for(int var10 = par2 - 1; var10 <= par2 + 1; ++var10) {
                     for(int var11 = par4 - 1; var11 <= par4 + 1; ++var11) {
                        for(int var12 = par3 - 1; var12 <= par3 + 4; ++var12) {
                           if (var10 != par2 || var12 != par3 || var11 != par4) {
                              int var13 = 100;
                              if (var12 > par3 + 1) {
                                 var13 += (var12 - (par3 + 1)) * 100;
                              }

                              int var14 = this.getChanceOfNeighborsEncouragingFire(par1World, var10, var12, var11);
                              if (var14 > 0) {
                                 int var15 = (var14 + 40 + par1World.difficultySetting * 7) / (var7 + 30);
                                 if (var8) {
                                    var15 /= 2;
                                 }

                                 if (var15 > 0 && par5Random.nextInt(var13) <= var15 && (!par1World.isPrecipitating(true) || !par1World.canLightningStrikeAt(var10, var12, var11)) && !par1World.canLightningStrikeAt(var10 - 1, var12, par4) && !par1World.canLightningStrikeAt(var10 + 1, var12, var11) && !par1World.canLightningStrikeAt(var10, var12, var11 - 1) && !par1World.canLightningStrikeAt(var10, var12, var11 + 1)) {
                                    int var16 = var7 + par5Random.nextInt(5) / 4;
                                    if (var16 > 15) {
                                       var16 = 15;
                                    }

                                    par1World.setBlock(var10, var12, var11, this.blockID, var16, 3);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return changed_state;
      }
   }

   public boolean func_82506_l() {
      return false;
   }

   public boolean tryToCatchBlockOnFire(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7) {
      int var8 = this.abilityToCatchFire[par1World.getBlockId(par2, par3, par4)];
      if (par6Random.nextInt(par5) < var8) {
         boolean var9 = par1World.getBlockId(par2, par3, par4) == Block.tnt.blockID;
         if (par6Random.nextInt(par7 + 10) < 5 && !par1World.canLightningStrikeAt(par2, par3, par4)) {
            int var10 = par7 + par6Random.nextInt(5) / 4;
            if (var10 > 15) {
               var10 = 15;
            }

            par1World.setBlock(par2, par3, par4, this.blockID, var10, 3);
         } else {
            par1World.setBlockToAir(par2, par3, par4);
         }

         if (var9) {
            BlockTNT var10000 = Block.tnt;
            BlockTNT.primeTnt(par1World, par2, par3, par4, 1, (EntityLivingBase)null);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canNeighborBurn(World par1World, int par2, int par3, int par4) {
      return this.canBlockCatchFire(par1World, par2 + 1, par3, par4) ? true : (this.canBlockCatchFire(par1World, par2 - 1, par3, par4) ? true : (this.canBlockCatchFire(par1World, par2, par3 - 1, par4) ? true : (this.canBlockCatchFire(par1World, par2, par3 + 1, par4) ? true : (this.canBlockCatchFire(par1World, par2, par3, par4 - 1) ? true : this.canBlockCatchFire(par1World, par2, par3, par4 + 1)))));
   }

   private int getChanceOfNeighborsEncouragingFire(World par1World, int par2, int par3, int par4) {
      byte var5 = 0;
      if (!par1World.isAirBlock(par2, par3, par4)) {
         return 0;
      } else {
         int var6 = this.getChanceToEncourageFire(par1World, par2 + 1, par3, par4, var5);
         var6 = this.getChanceToEncourageFire(par1World, par2 - 1, par3, par4, var6);
         var6 = this.getChanceToEncourageFire(par1World, par2, par3 - 1, par4, var6);
         var6 = this.getChanceToEncourageFire(par1World, par2, par3 + 1, par4, var6);
         var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 - 1, var6);
         var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 + 1, var6);
         return var6;
      }
   }

   public boolean isCollidable() {
      return false;
   }

   public boolean canBlockCatchFire(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      if (par1IBlockAccess.getBlockId(par2, par3, par4) == netherrack.blockID) {
         return true;
      } else {
         return this.chanceToEncourageFire[par1IBlockAccess.getBlockId(par2, par3, par4)] > 0;
      }
   }

   public int getChanceToEncourageFire(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = this.chanceToEncourageFire[par1World.getBlockId(par2, par3, par4)];
      return var6 > par5 ? var6 : par5;
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      Block block_below = world.getBlock(x, y - 1, z);
      if (world.isTheEnd() && block_below == bedrock) {
         return true;
      } else {
         return block_below == grass || block_below == netherrack || this.canNeighborBurn(world, x, y, z);
      }
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (par1World.provider.dimensionId > 0 || par1World.getBlockId(par2, par3 - 1, par4) != Block.obsidian.blockID || !Block.portal.tryToCreatePortal(par1World, par2, par3, par4)) {
         if (!par1World.isBlockTopFlatAndSolid(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4)) {
            par1World.setBlockToAir(par2, par3, par4);
         } else {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World) + par1World.rand.nextInt(10));
         }
      }

   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (par5Random.nextInt(24) == 0) {
         par1World.playSound((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "fire.fire", 1.0F + par5Random.nextFloat(), par5Random.nextFloat() * 0.7F + 0.3F, false);
      }

      int var6;
      float var7;
      float var8;
      float var9;
      if (!par1World.isBlockTopFlatAndSolid(par2, par3 - 1, par4) && !Block.fire.canBlockCatchFire(par1World, par2, par3 - 1, par4)) {
         if (Block.fire.canBlockCatchFire(par1World, par2 - 1, par3, par4)) {
            for(var6 = 0; var6 < 2; ++var6) {
               var7 = (float)par2 + par5Random.nextFloat() * 0.1F;
               var8 = (float)par3 + par5Random.nextFloat();
               var9 = (float)par4 + par5Random.nextFloat();
               par1World.spawnParticle(EnumParticle.largesmoke, (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
            }
         }

         if (Block.fire.canBlockCatchFire(par1World, par2 + 1, par3, par4)) {
            for(var6 = 0; var6 < 2; ++var6) {
               var7 = (float)(par2 + 1) - par5Random.nextFloat() * 0.1F;
               var8 = (float)par3 + par5Random.nextFloat();
               var9 = (float)par4 + par5Random.nextFloat();
               par1World.spawnParticle(EnumParticle.largesmoke, (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
            }
         }

         if (Block.fire.canBlockCatchFire(par1World, par2, par3, par4 - 1)) {
            for(var6 = 0; var6 < 2; ++var6) {
               var7 = (float)par2 + par5Random.nextFloat();
               var8 = (float)par3 + par5Random.nextFloat();
               var9 = (float)par4 + par5Random.nextFloat() * 0.1F;
               par1World.spawnParticle(EnumParticle.largesmoke, (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
            }
         }

         if (Block.fire.canBlockCatchFire(par1World, par2, par3, par4 + 1)) {
            for(var6 = 0; var6 < 2; ++var6) {
               var7 = (float)par2 + par5Random.nextFloat();
               var8 = (float)par3 + par5Random.nextFloat();
               var9 = (float)(par4 + 1) - par5Random.nextFloat() * 0.1F;
               par1World.spawnParticle(EnumParticle.largesmoke, (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
            }
         }

         if (Block.fire.canBlockCatchFire(par1World, par2, par3 + 1, par4)) {
            for(var6 = 0; var6 < 2; ++var6) {
               var7 = (float)par2 + par5Random.nextFloat();
               var8 = (float)(par3 + 1) - par5Random.nextFloat() * 0.1F;
               var9 = (float)par4 + par5Random.nextFloat();
               par1World.spawnParticle(EnumParticle.largesmoke, (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for(var6 = 0; var6 < 3; ++var6) {
            var7 = (float)par2 + par5Random.nextFloat();
            var8 = (float)par3 + par5Random.nextFloat() * 0.5F + 0.5F;
            var9 = (float)par4 + par5Random.nextFloat();
            par1World.spawnParticle(EnumParticle.largesmoke, (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
         }
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[]{par1IconRegister.registerIcon(this.getTextureName() + "_layer_0"), par1IconRegister.registerIcon(this.getTextureName() + "_layer_1")};
   }

   public Icon getFireIcon(int par1) {
      return this.iconArray[par1];
   }

   public Icon getIcon(int par1, int par2) {
      return this.iconArray[0];
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public static boolean canBlockCatchFire(Block block) {
      return Block.fire.abilityToCatchFire[block.blockID] > 0;
   }
}
