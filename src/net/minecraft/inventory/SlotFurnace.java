package net.minecraft.inventory;

import net.minecraft.client.gui.inventory.SlotCraftingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class SlotFurnace extends SlotCraftingBase {
   public SlotFurnace(EntityPlayer player, IInventory inventory, int slot_index, int display_x, int display_y) {
      super(player, inventory, slot_index, display_x, display_y);
   }

   public void onPickupFromSlot(EntityPlayer player, ItemStack item_stack) {
      this.onCrafting(item_stack);
      super.onPickupFromSlot(player, item_stack);
   }

   protected void onCrafting(ItemStack item_stack) {
      if (!this.player.worldObj.isRemote) {
         int xp_reward = item_stack.getExperienceReward(this.quantity_taken);
         if (xp_reward > 0) {
            this.player.worldObj.spawnEntityInWorld(new EntityXPOrb(this.player.worldObj, this.player.posX, this.player.posY + 0.5, this.player.posZ + 0.5, xp_reward));
         }
      }

      super.onCrafting(item_stack);
      Item item = item_stack.getItem();
      if (item == Item.ingotIron) {
         this.player.addStat(AchievementList.acquireIron, 1);
      } else if (item != Item.fishCooked && item != Item.fishLargeCooked) {
         if (item == Item.bread) {
            this.player.addStat(AchievementList.makeBread, 1);
         } else if (item == Item.ingotMithril) {
            this.player.triggerAchievement(AchievementList.mithrilIngot);
         } else if (item == Item.ingotAdamantium) {
            this.player.triggerAchievement(AchievementList.adamantiumIngot);
         }
      } else {
         this.player.addStat(AchievementList.cookFish, 1);
      }

   }
}
