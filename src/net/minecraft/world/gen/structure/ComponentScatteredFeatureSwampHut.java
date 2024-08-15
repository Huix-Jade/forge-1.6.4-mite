package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeatureSwampHut extends ComponentScatteredFeature {
   private boolean hasWitch;
   protected boolean has_made_chest;
   protected static final WeightedRandomChestContent[] chest_contents;

   public ComponentScatteredFeatureSwampHut() {
   }

   public ComponentScatteredFeatureSwampHut(Random par1Random, int par2, int par3) {
      super(par1Random, par2, 64, par3, 7, 5, 9);
   }

   protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
      super.func_143012_a(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("Witch", this.hasWitch);
   }

   protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
      super.func_143011_b(par1NBTTagCompound);
      this.hasWitch = par1NBTTagCompound.getBoolean("Witch");
   }

   public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox) {
      if (!this.func_74935_a(par1World, par3StructureBoundingBox, 0)) {
         return false;
      } else {
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 5, 1, 7, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 4, 2, 5, 4, 7, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 2, 1, 0, 4, 1, 0, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 2, 2, 2, 3, 3, 2, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 2, 3, 1, 3, 6, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 5, 2, 3, 5, 3, 6, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 2, 2, 7, 4, 3, 7, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
         this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 2, 1, 3, 2, Block.wood.blockID, Block.wood.blockID, false);
         this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 0, 2, 5, 3, 2, Block.wood.blockID, Block.wood.blockID, false);
         this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 7, 1, 3, 7, Block.wood.blockID, Block.wood.blockID, false);
         this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 0, 7, 5, 3, 7, Block.wood.blockID, Block.wood.blockID, false);
         this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 2, 3, 2, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 3, 3, 7, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, 3, 4, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, 0, 0, 5, 3, 4, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, 0, 0, 5, 3, 5, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.flowerPot.blockID, 7, 1, 3, 5, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.cauldron.blockID, 0, 4, 2, 6, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 1, 2, 1, par3StructureBoundingBox);
         this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 5, 2, 1, par3StructureBoundingBox);
         int var4 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 3);
         int var5 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 1);
         int var6 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 0);
         int var7 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 2);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 4, 1, 6, 4, 1, Block.stairsWoodSpruce.blockID, var4, Block.stairsWoodSpruce.blockID, var4, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 4, 2, 0, 4, 7, Block.stairsWoodSpruce.blockID, var6, Block.stairsWoodSpruce.blockID, var6, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 6, 4, 2, 6, 4, 7, Block.stairsWoodSpruce.blockID, var5, Block.stairsWoodSpruce.blockID, var5, false);
         this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 4, 8, 6, 4, 8, Block.stairsWoodSpruce.blockID, var7, Block.stairsWoodSpruce.blockID, var7, false);
         if (!this.has_made_chest) {
            this.has_made_chest = this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 2, 2, 6, Block.chest.blockID, chest_contents, 4 + par2Random.nextInt(5), (float[])null, EnumDirection.SOUTH);
         }

         int var8;
         int var9;
         for(var8 = 2; var8 <= 7; var8 += 5) {
            for(var9 = 1; var9 <= 5; var9 += 4) {
               this.fillCurrentPositionBlocksDownwards(par1World, Block.wood.blockID, 0, var9, -1, var8, par3StructureBoundingBox);
            }
         }

         if (!this.hasWitch) {
            var8 = this.getXWithOffset(2, 5);
            var9 = this.getYWithOffset(2);
            int var10 = this.getZWithOffset(2, 5);
            if (par3StructureBoundingBox.isVecInside(var8, var9, var10)) {
               this.hasWitch = true;
               EntityWitch var11 = new EntityWitch(par1World);
               var11.setLocationAndAngles((double)var8 + 0.5, (double)var9, (double)var10 + 0.5, 0.0F, 0.0F);
               var11.onSpawnWithEgg((EntityLivingData)null);
               par1World.spawnEntityInWorld(var11);
            }
         }

         return true;
      }
   }

   static {
      chest_contents = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.glassBottle.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Block.mushroomRed.blockID, 0, 1, 4, 8), new WeightedRandomChestContent(Block.mushroomBrown.blockID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.bowlEmpty.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.reed.itemID, 0, 1, 5, 8), new WeightedRandomChestContent(Item.chipFlint.itemID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.knifeFlint.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.hatchetFlint.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.stick.itemID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.rottenFlesh.itemID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.silk.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.chickenRaw.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.feather.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.leather.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Block.cloth.blockID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.bone.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.sugar.itemID, 0, 1, 3, 5), new WeightedRandomChestContent(Item.pumpkinSeeds.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.appleRed.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.carrot.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.potato.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Item.onion.itemID, 0, 1, 2, 3), new WeightedRandomChestContent(Block.plantYellow.blockID, 0, 1, 2, 3), new WeightedRandomChestContent(Block.plantRed.blockID, 2, 1, 2, 3), new WeightedRandomChestContent(Item.potion.itemID, 8227, 1, 1, 1), new WeightedRandomChestContent(Item.potion.itemID, 8261, 1, 1, 1), new WeightedRandomChestContent(Item.potion.itemID, 16388, 1, 1, 1), new WeightedRandomChestContent(Item.potion.itemID, 16424, 1, 1, 1), new WeightedRandomChestContent(Item.potion.itemID, 16426, 1, 1, 1), new WeightedRandomChestContent(Item.potion.itemID, 16460, 1, 1, 1)};
   }
}
