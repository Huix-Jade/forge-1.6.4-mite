package net.minecraft.mite;

import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomItem;

public class RandomItemListEntry extends WeightedRandomItem {
   public Item item;

   public RandomItemListEntry(Item item, int weight) {
      super(weight);
      this.item = item;
   }
}
