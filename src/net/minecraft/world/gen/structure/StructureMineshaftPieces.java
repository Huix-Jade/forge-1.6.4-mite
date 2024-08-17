package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;

public class StructureMineshaftPieces {
   public static final WeightedRandomChestContent[] mineshaftChestContents;

   public static void func_143048_a() {
      MapGenStructureIO.func_143031_a(ComponentMineshaftCorridor.class, "MSCorridor");
      MapGenStructureIO.func_143031_a(ComponentMineshaftCross.class, "MSCrossing");
      MapGenStructureIO.func_143031_a(ComponentMineshaftRoom.class, "MSRoom");
      MapGenStructureIO.func_143031_a(ComponentMineshaftStairs.class, "MSStairs");
   }

   private static StructureComponent getRandomComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6) {
      int var7 = par1Random.nextInt(100);
      StructureBoundingBox var8;
      if (var7 >= 80) {
         var8 = ComponentMineshaftCross.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);
         if (var8 != null) {
            return new ComponentMineshaftCross(par6, par1Random, var8, par5);
         }
      } else if (var7 >= 70) {
         var8 = ComponentMineshaftStairs.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);
         if (var8 != null) {
            return new ComponentMineshaftStairs(par6, par1Random, var8, par5);
         }
      } else {
         var8 = ComponentMineshaftCorridor.findValidPlacement(par0List, par1Random, par2, par3, par4, par5);
         if (var8 != null) {
            return new ComponentMineshaftCorridor(par6, par1Random, var8, par5);
         }
      }

      return null;
   }

   private static StructureComponent getNextMineShaftComponent(StructureComponent par0StructureComponent, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7) {
      if (par7 > 8) {
         return null;
      } else if (Math.abs(par3 - par0StructureComponent.getBoundingBox().minX) <= 80 && Math.abs(par5 - par0StructureComponent.getBoundingBox().minZ) <= 80) {
         StructureComponent var8 = getRandomComponent(par1List, par2Random, par3, par4, par5, par6, par7 + 1);
         if (var8 != null) {
            par1List.add(var8);
            var8.buildComponent(par0StructureComponent, par1List, par2Random);
         }

         return var8;
      } else {
         return null;
      }
   }

   static StructureComponent getNextComponent(StructureComponent par0StructureComponent, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7) {
      return getNextMineShaftComponent(par0StructureComponent, par1List, par2Random, par3, par4, par5, par6, par7);
   }

   static WeightedRandomChestContent[] func_78816_a() {
      return mineshaftChestContents;
   }

   static {
      mineshaftChestContents = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.appleRed.itemID, 0, 1, 3, 3), new WeightedRandomChestContent(Item.bread.itemID, 0, 1, 3, 15), new WeightedRandomChestContent(Item.carrot.itemID, 0, 1, 2, 2), new WeightedRandomChestContent(Item.potato.itemID, 0, 1, 2, 2), new WeightedRandomChestContent(Item.onion.itemID, 0, 1, 2, 2), new WeightedRandomChestContent(Item.cheese.itemID, 0, 1, 3, 3), new WeightedRandomChestContent(Block.mushroomBrown.blockID, 0, 1, 3, 5), new WeightedRandomChestContent(Block.mushroomRed.blockID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.bowlEmpty.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.copperNugget.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.silverNugget.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.goldNugget.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.ingotCopper.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.ingotSilver.itemID, 0, 1, 3, 3), new WeightedRandomChestContent(Item.ingotGold.itemID, 0, 1, 3, 2), new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.chainRustedIron.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.dyePowder.itemID, 4, 2, 5, 5), new WeightedRandomChestContent(Item.redstone.itemID, 0, 2, 5, 5), new WeightedRandomChestContent(Item.shardEmerald.itemID, 0, 1, 6, 5), new WeightedRandomChestContent(Item.shardDiamond.itemID, 0, 1, 4, 3), new WeightedRandomChestContent(Item.emerald.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.diamond.itemID, 0, 1, 2, 1), new WeightedRandomChestContent(Item.coal.itemID, 0, 2, 5, 10), new WeightedRandomChestContent(Item.bootsLeather.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.saddle.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Block.torchWood.blockID, 0, 1, 6, 10), new WeightedRandomChestContent(Item.flintAndSteel.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Block.tnt.blockID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.bucketCopperEmpty.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.bucketEmpty.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.shovelWood.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.shovelRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.shovelCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.shovelIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.hatchetRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.hatchetCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.hatchetIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.axeRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.axeCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.axeIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.mattockRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.mattockCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.mattockIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.pickaxeRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.pickaxeCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.pickaxeIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.warHammerRustedIron.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.warHammerCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.warHammerIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Block.rail.blockID, 0, 2, 5, 1)};
   }
}
