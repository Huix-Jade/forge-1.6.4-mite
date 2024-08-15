package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public class EntityAIMoveToRepairItem extends EntityAIMoveToItem {
   public EntityAIMoveToRepairItem(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public List getNearbyItemEntitiesOfInterest() {
      return this.world.getRepairItemEntitiesWithinAABBForLivingEntity(this.task_owner, this.task_owner.boundingBox.expand((double)this.max_path_length, (double)(this.max_path_length / 4), (double)this.max_path_length));
   }

   public boolean willPickUp(ItemStack item_stack) {
      return this.task_owner.isRepairItem(item_stack);
   }

   public boolean shouldExecute() {
      if (this.task_owner.food_or_repair_item_pickup_cooldown > 0) {
         return false;
      } else if (this.task_owner.rand.nextInt(40) > 0) {
         return false;
      } else {
         return this.task_owner.getHealthFraction() >= 1.0F ? false : super.shouldExecute();
      }
   }

   public boolean continueExecuting() {
      return this.task_owner.food_or_repair_item_pickup_cooldown > 0 ? false : super.continueExecuting();
   }
}
