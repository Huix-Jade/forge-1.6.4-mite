package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.StringHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;

public class WorldGenDungeons extends WorldGenerator {
   private static final WeightedRandomChestContent[] field_111189_a;
   private static final WeightedRandomChestContent[] chest_contents_for_underworld;

   private static WeightedRandomChestContent[] getChestContentsForWorld(World world) {
      return world.isUnderworld() ? chest_contents_for_underworld : field_111189_a;
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      byte var6 = 3;
      int var7 = par2Random.nextInt(2) + 2;
      int var8 = par2Random.nextInt(2) + 2;
      int var9 = 0;

      int var10;
      int var11;
      int var12;
      for(var10 = par3 - var7 - 1; var10 <= par3 + var7 + 1; ++var10) {
         for(var11 = par4 - 1; var11 <= par4 + var6 + 1; ++var11) {
            for(var12 = par5 - var8 - 1; var12 <= par5 + var8 + 1; ++var12) {
               Material var13 = par1World.getBlockMaterial(var10, var11, var12);
               if (var11 == par4 - 1 && !var13.isSolid()) {
                  return false;
               }

               if (var11 == par4 + var6 + 1) {
                  if (!var13.isSolid()) {
                     return false;
                  }

                  Block block = par1World.getBlock(var10, var11, var12);
                  if (block instanceof BlockFalling) {
                     return false;
                  }
               }

               if ((var10 == par3 - var7 - 1 || var10 == par3 + var7 + 1 || var12 == par5 - var8 - 1 || var12 == par5 + var8 + 1) && var11 == par4 && par1World.isAirBlock(var10, var11, var12) && par1World.isAirBlock(var10, var11 + 1, var12)) {
                  ++var9;
               }
            }
         }
      }

      if (var9 >= 1 && var9 <= 5) {
         for(var10 = par3 - var7 - 1; var10 <= par3 + var7 + 1; ++var10) {
            for(var11 = par4 + var6; var11 >= par4 - 1; --var11) {
               for(var12 = par5 - var8 - 1; var12 <= par5 + var8 + 1; ++var12) {
                  if (var10 != par3 - var7 - 1 && var11 != par4 - 1 && var12 != par5 - var8 - 1 && var10 != par3 + var7 + 1 && var11 != par4 + var6 + 1 && var12 != par5 + var8 + 1) {
                     par1World.setBlockToAir(var10, var11, var12);
                  } else if (var11 >= 0 && !par1World.getBlockMaterial(var10, var11 - 1, var12).isSolid()) {
                     par1World.setBlockToAir(var10, var11, var12);
                  } else if (par1World.getBlockMaterial(var10, var11, var12).isSolid()) {
                     if (var11 == par4 - 1 && par2Random.nextInt(4) != 0) {
                        par1World.setBlock(var10, var11, var12, Block.cobblestoneMossy.blockID, 0, 2);
                     } else {
                        par1World.setBlock(var10, var11, var12, Block.cobblestone.blockID, 0, 2);
                     }
                  }
               }
            }
         }

         for(var10 = 0; var10 < 2; ++var10) {
            for(var11 = 0; var11 < 3; ++var11) {
               var12 = par3 + par2Random.nextInt(var7 * 2 + 1) - var7;
               int var14 = par5 + par2Random.nextInt(var8 * 2 + 1) - var8;
               if (par1World.isAirBlock(var12, par4, var14)) {
                  int var15 = 0;
                  EnumDirection direction = null;
                  if (par1World.getBlockMaterial(var12 - 1, par4, var14).isSolid()) {
                     ++var15;
                     direction = EnumDirection.EAST;
                  }

                  if (par1World.getBlockMaterial(var12 + 1, par4, var14).isSolid()) {
                     ++var15;
                     direction = EnumDirection.WEST;
                  }

                  if (par1World.getBlockMaterial(var12, par4, var14 - 1).isSolid()) {
                     ++var15;
                     direction = EnumDirection.SOUTH;
                  }

                  if (par1World.getBlockMaterial(var12, par4, var14 + 1).isSolid()) {
                     ++var15;
                     direction = EnumDirection.NORTH;
                  }

                  if (var15 == 1) {
                     par1World.setBlock(var12, par4, var14, Block.chest.blockID, Block.chest.getMetadataForDirectionFacing(0, direction), 2);
                     TileEntityChest var17 = (TileEntityChest)par1World.getBlockTileEntity(var12, par4, var14);
                     if (var17 != null) {
                        ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
                        WeightedRandomChestContent.generateChestContents(par1World, par4, par2Random, info.getItems(par2Random), var17, info.getCount(par2Random), (float[])null);
                     }
                     break;
                  }
               }
            }
         }

         if (!par1World.isOverworld()) {
            Debug.println("Dungeon at " + StringHelper.getCoordsAsString(par3, par4, par5));
         }

         par1World.setBlock(par3, par4, par5, Block.mobSpawner.blockID, 0, 2);
         TileEntityMobSpawner var18 = (TileEntityMobSpawner)par1World.getBlockTileEntity(par3, par4, par5);
         if (var18 != null) {
            var18.getSpawnerLogic().setMobID(this.pickMobSpawner(par1World, par2Random, par4));
         } else {
            System.err.println("Failed to fetch mob spawner entity at (" + par3 + ", " + par4 + ", " + par5 + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String pickMobSpawner(World world, Random par1Random, int y) {
      if (world.isUnderworld()) {
         return par1Random.nextInt(6) == 0 ? "LongdeadGuardian" : "Longdead";
      } else {
         int danger;
         if (par1Random.nextInt(2) == 0) {
            danger = par1Random.nextInt(4);
         } else {
            danger = (int)Math.max(1.0F - (float)y / 64.0F, 0.0F) * 4 + par1Random.nextInt(3) - par1Random.nextInt(3);
         }

         if (danger < 0) {
            danger = par1Random.nextInt(4);
         }

         switch (danger) {
            case 0:
               return "Zombie";
            case 1:
               return "Ghoul";
            case 2:
               return "Skeleton";
            case 3:
               return "Spider";
            case 4:
               return "Wight";
            case 5:
               return "DemonSpider";
            case 6:
               return "Hellhound";
            default:
               return DungeonHooks.getRandomDungeonMob(par1Random);
         }
      }
   }

   static {
      field_111189_a = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.bread.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.carrot.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.potato.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.onion.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.wheat.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.appleGold.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.coinCopper.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.coinSilver.itemID, 0, 1, 2, 2), new WeightedRandomChestContent(Item.coinGold.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.saddle.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.gunpowder.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.silk.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.bowlEmpty.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.bucketCopperEmpty.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.bucketIronEmpty.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.record13.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.recordCat.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.nameTag.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.horseArmorGold.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.horseArmorCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.horseArmorIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.shearsRustedIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.fishingRodFlint.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.fishingRodCopper.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.fishingRodIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.shovelWood.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.shovelRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.shovelCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.hoeRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.hoeCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.mattockRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.mattockCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.daggerRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.daggerCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.daggerSilver.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.swordRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.swordCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.battleAxeRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.battleAxeCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.warHammerRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.warHammerCopper.itemID, 0, 1, 1, 1)};
      chest_contents_for_underworld = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.ancientMetalNugget.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.ingotAncientMetal.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.gunpowder.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.silk.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.bowlEmpty.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.bucketAncientMetalEmpty.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.coinAncientMetal.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.recordUnderworld.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.recordDescent.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.recordWanderer.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.recordLegends.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.nameTag.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.horseArmorAncientMetal.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.fishingRodAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.pickaxeAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.shovelAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.daggerAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.swordAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.bowAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.bootsChainAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.legsChainAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.plateChainAncientMetal.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.helmetChainAncientMetal.itemID, 0, 1, 1, 2)};
   }
}
