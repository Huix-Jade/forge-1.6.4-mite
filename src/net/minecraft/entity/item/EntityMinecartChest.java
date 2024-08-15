package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   public EntityMinecartChest(World par1World) {
      super(par1World);
   }

   public EntityMinecartChest(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public void killMinecart(DamageSource par1DamageSource) {
      super.killMinecart(par1DamageSource);
      this.dropItem(Block.chest.blockID, 1, 0.0F);
   }

   public int getSizeInventory() {
      return 27;
   }

   public int getMinecartType() {
      return 1;
   }

   public Block getDefaultDisplayTile() {
      return Block.chest;
   }

   public int getDefaultDisplayTileOffset() {
      return 8;
   }

   public Item getModelItem() {
      return Item.minecartCrate;
   }
}
