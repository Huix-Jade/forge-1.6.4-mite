package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {
   private final boolean powered;

   public BlockRedstoneLight(int par1, boolean par2) {
      super(par1, Material.redstoneLight, (new BlockConstants()).setNeverConnectsWithFence());
      this.powered = par2;
      if (par2) {
         this.setLightValue(1.0F);
      }

   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (!par1World.isRemote) {
         if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 4);
         } else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
            par1World.setBlock(par2, par3, par4, Block.redstoneLampActive.blockID, 0, 2);
         }
      }

   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (!world.isRemote) {
         if (this.powered && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 4);
         } else if (!this.powered && world.isBlockIndirectlyGettingPowered(x, y, z)) {
            return world.setBlock(x, y, z, Block.redstoneLampActive.blockID, 0, 2);
         }
      }

      return false;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      return this.powered && !world.isBlockIndirectlyGettingPowered(x, y, z) ? world.setBlock(x, y, z, Block.redstoneLampIdle.blockID, 0, 2) : false;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Block.redstoneLampIdle.blockID;
   }

   public boolean canBeCarried() {
      return !this.powered;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.redstone.itemID, 0, 4, 0.25F) : super.dropBlockAsEntityItem(info);
   }

   public ItemStack createStackedBlock(int par1) {
      return new ItemStack(redstoneLampIdle);
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.redstone, Material.glowstone});
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.powered ? "lit" : "unlit";
   }
}
