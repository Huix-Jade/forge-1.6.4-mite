package net.minecraft.item;

import net.minecraft.block.BlockAnvil;

public class ItemAnvilBlock extends ItemMultiTextureTile implements IDamageableItem {
   public ItemAnvilBlock(BlockAnvil par1Block) {
      super(par1Block, BlockAnvil.statuses);
      this.setMaxDamage(par1Block.getDurability());
   }

   public int getMetadata(int par1) {
      return par1 << 2;
   }

   public BlockAnvil getBlock() {
      return (BlockAnvil)super.getBlock();
   }

   public int getNumComponentsForDurability() {
      return 31;
   }

   public void updateSubtypeForDamage(ItemStack item_stack) {
      int damage_stage = this.getBlock().getDamageStage(item_stack.getItemDamage());
      item_stack.setItemSubtype(damage_stage);
   }
}
