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

import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_LIBRARY;

public class ComponentStrongholdLibrary extends ComponentStronghold {
   public static final WeightedRandomChestContent[] strongholdLibraryChestContents;
   private boolean isLargeRoom;

   public ComponentStrongholdLibrary() {
   }

   public ComponentStrongholdLibrary(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4) {
      super(par1);
      this.coordBaseMode = par4;
      this.field_143013_d = this.getRandomDoor(par2Random);
      this.boundingBox = par3StructureBoundingBox;
      this.isLargeRoom = par3StructureBoundingBox.getYSize() > 6;
   }

   protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
      super.func_143012_a(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("Tall", this.isLargeRoom);
   }

   protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
      super.func_143011_b(par1NBTTagCompound);
      this.isLargeRoom = par1NBTTagCompound.getBoolean("Tall");
   }

   public static ComponentStrongholdLibrary findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6) {
      StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 14, 11, 15, par5);
      if (!canStrongholdGoDeeper(var7) || StructureComponent.findIntersecting(par0List, var7) != null) {
         var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 14, 6, 15, par5);
         if (!canStrongholdGoDeeper(var7) || StructureComponent.findIntersecting(par0List, var7) != null) {
            return null;
         }
      }

      return new ComponentStrongholdLibrary(par6, par1Random, var7, par5);
   }

   public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox) {
      if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox)) {
         return false;
      } else {
         byte var4 = 11;
         if (!this.isLargeRoom) {
            var4 = 6;
         }

         this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 13, var4 - 1, 14, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
         this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 4, 1, 0);
         this.randomlyFillWithBlocks(par1World, par3StructureBoundingBox, par2Random, 0.07F, 2, 1, 1, 11, 4, 13, Block.web.blockID, Block.web.blockID, false);
         boolean var5 = true;
         boolean var6 = true;

         int var7;
         for(var7 = 1; var7 <= 13; ++var7) {
            if ((var7 - 1) % 4 == 0) {
               this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, var7, 1, 4, var7, Block.planks.blockID, Block.planks.blockID, false);
               this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 1, var7, 12, 4, var7, Block.planks.blockID, Block.planks.blockID, false);
               this.placeBlockRelativeWithAdjustedMetadata(par1World, Block.torchWood, 2, 3, var7, 1, par3StructureBoundingBox);
               this.placeBlockRelativeWithAdjustedMetadata(par1World, Block.torchWood, 11, 3, var7, 2, par3StructureBoundingBox);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 6, var7, 1, 9, var7, Block.planks.blockID, Block.planks.blockID, false);
                  this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 6, var7, 12, 9, var7, Block.planks.blockID, Block.planks.blockID, false);
               }
            } else {
               this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, var7, 1, 4, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
               this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 1, var7, 12, 4, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 6, var7, 1, 9, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
                  this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 6, var7, 12, 9, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
               }
            }
         }

         for(var7 = 3; var7 < 12; var7 += 2) {
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 1, var7, 4, 3, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 1, var7, 7, 3, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 1, var7, 10, 3, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
         }

         if (this.isLargeRoom) {
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 1, 3, 5, 13, Block.planks.blockID, Block.planks.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 5, 1, 12, 5, 13, Block.planks.blockID, Block.planks.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 1, 9, 5, 2, Block.planks.blockID, Block.planks.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 12, 9, 5, 13, Block.planks.blockID, Block.planks.blockID, false);
            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 9, 5, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 5, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 9, 5, 10, par3StructureBoundingBox);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 6, 2, 3, 6, 12, Block.fence.blockID, Block.fence.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 6, 2, 10, 6, 10, Block.fence.blockID, Block.fence.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 6, 2, 9, 6, 2, Block.fence.blockID, Block.fence.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 6, 12, 8, 6, 12, Block.fence.blockID, Block.fence.blockID, false);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 9, 6, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 8, 6, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 9, 6, 10, par3StructureBoundingBox);
            var7 = this.getMetadataWithOffset(Block.ladder.blockID, 3);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 1, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 2, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 3, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 4, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 5, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 6, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 7, 13, par3StructureBoundingBox);
            byte var8 = 7;
            byte var9 = 7;
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 9, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 9, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 8, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 8, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 7, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 7, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 2, 7, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 + 1, 7, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 7, var9 - 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 7, var9 + 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 7, var9 - 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 7, var9 + 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 5, var8 - 2, 8, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 5, var8 + 1, 8, var9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 5, var8 - 1, 8, var9 - 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 5, var8 - 1, 8, var9 + 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 5, var8, 8, var9 - 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 5, var8, 8, var9 + 1, par3StructureBoundingBox);
         }

         float[] chances_of_artifact = new float[]{0.5F};

         ChestGenHooks info = ChestGenHooks.getInfo(STRONGHOLD_LIBRARY);
         this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 3, 3, 5, Block.chest.blockID, info.getItems(par2Random), info.getCount(par2Random), chances_of_artifact, EnumDirection.NORTH);
         if (this.isLargeRoom) {
            this.placeBlockAtCurrentPosition(par1World, 0, 0, 12, 9, 1, par3StructureBoundingBox);
            this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 12, 8, 1, Block.chest.blockID, info.getItems(par2Random), info.getCount(par2Random), chances_of_artifact, EnumDirection.WEST);
         }

         return true;
      }
   }

   static {
      strongholdLibraryChestContents = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Item.book.itemID, 0, 1, 3, 20), new WeightedRandomChestContent(Item.paper.itemID, 0, 2, 7, 20), new WeightedRandomChestContent(Item.emptyMap.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.compass.itemID, 0, 1, 1, 1)};
   }
}
