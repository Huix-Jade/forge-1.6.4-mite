package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_CORRIDOR;

public class ComponentStrongholdChestCorridor extends ComponentStronghold {
   public static final WeightedRandomChestContent[] strongholdChestContents;
   private boolean hasMadeChest;

   public ComponentStrongholdChestCorridor() {
   }

   public ComponentStrongholdChestCorridor(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4) {
      super(par1);
      this.coordBaseMode = par4;
      this.field_143013_d = this.getRandomDoor(par2Random);
      this.boundingBox = par3StructureBoundingBox;
   }

   protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
      super.func_143012_a(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("Chest", this.hasMadeChest);
   }

   protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
      super.func_143011_b(par1NBTTagCompound);
      this.hasMadeChest = par1NBTTagCompound.getBoolean("Chest");
   }

   public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {
      this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
   }

   public static ComponentStrongholdChestCorridor findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6) {
      StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
      return canStrongholdGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentStrongholdChestCorridor(par6, par1Random, var7, par5) : null;
   }

   public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox) {
      if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox)) {
         return false;
      } else {
         this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
         this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);
         this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.OPENING, 1, 1, 6);
         this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 1, 2, 3, 1, 4, Block.stoneBrick.blockID, Block.stoneBrick.blockID, false);
         this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 5, 3, 1, 1, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 5, 3, 1, 5, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 5, 3, 2, 2, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 5, 3, 2, 4, par3StructureBoundingBox);

         int var4;
         for(var4 = 2; var4 <= 4; ++var4) {
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 5, 2, 1, var4, par3StructureBoundingBox);
         }

         if (!this.hasMadeChest) {
            var4 = this.getYWithOffset(2);
            int var5 = this.getXWithOffset(3, 3);
            int var6 = this.getZWithOffset(3, 3);
            if (par3StructureBoundingBox.isVecInside(var5, var4, var6)) {
               this.hasMadeChest = true;
               this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 3, 2, 3, Block.chest.blockID, ChestGenHooks.getItems(STRONGHOLD_CORRIDOR, par2Random), ChestGenHooks.getCount(STRONGHOLD_CORRIDOR, par2Random), (float[])null, EnumDirection.WEST);
            }
         }

         return true;
      }
   }

   static {
      strongholdChestContents = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.bread.itemID, 0, 1, 3, 15), new WeightedRandomChestContent(Item.appleRed.itemID, 0, 1, 3, 15), new WeightedRandomChestContent(Item.carrot.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.potato.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.onion.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.appleGold.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.enderPearl.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.emerald.itemID, 0, 1, 3, 3), new WeightedRandomChestContent(Item.diamond.itemID, 0, 1, 2, 2), new WeightedRandomChestContent(Item.ingotCopper.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.ingotSilver.itemID, 0, 1, 3, 3), new WeightedRandomChestContent(Item.ingotGold.itemID, 0, 1, 3, 2), new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 5, 5), new WeightedRandomChestContent(Item.redstone.itemID, 0, 3, 5, 5), new WeightedRandomChestContent(Item.pickaxeCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.pickaxeIron.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.daggerCopper.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.daggerSilver.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.daggerIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.swordCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.swordSilver.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.swordIron.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.battleAxeCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.battleAxeIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.warHammerCopper.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.warHammerIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.plateChainCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.helmetChainCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.legsChainCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.bootsChainCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.plateChainIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.helmetChainIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.legsChainIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.bootsChainIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.plateCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.helmetCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.legsCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.bootsCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.plateIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.helmetIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.legsIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.bootsIron.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.saddle.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.horseArmorCopper.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.horseArmorIron.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.shears.itemID, 0, 1, 1, 1)};
   }
}
