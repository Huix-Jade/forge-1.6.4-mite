package net.minecraft.item;

import net.minecraft.world.World;

public class ItemDamageResult {
   private boolean item_lost_durability;
   private boolean item_was_destroyed;

   public ItemDamageResult setItemLostDurability() {
      this.item_lost_durability = true;
      return this;
   }

   public boolean itemLostDurability() {
      return this.item_lost_durability;
   }

   public ItemDamageResult setItemWasDestroyed(World world, ItemStack item_stack) {
      this.item_was_destroyed = true;
      if (!world.isRemote) {
         world.tryRemoveFromWorldUniques(item_stack);
      }

      return this;
   }

   public boolean itemWasDestroyed() {
      return this.item_was_destroyed;
   }
}
