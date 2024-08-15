package net.minecraft.item;

public class ItemSimpleFoiled extends Item {
   public ItemSimpleFoiled(int id, String texture) {
      super(id, texture);
   }

   public boolean hasEffect(ItemStack par1ItemStack) {
      return true;
   }
}
