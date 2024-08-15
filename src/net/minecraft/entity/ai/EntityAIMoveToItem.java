package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public abstract class EntityAIMoveToItem extends EntityAIMovementTask {
   public int max_path_length = 16;
   private List item_entities;
   private EntityItem selected_item_entity;

   public EntityAIMoveToItem(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public abstract List getNearbyItemEntitiesOfInterest();

   public abstract boolean willPickUp(ItemStack var1);

   public boolean shouldExecute() {
      this.item_entities = this.getNearbyItemEntitiesOfInterest();
      return !this.item_entities.isEmpty();
   }

   protected PathEntity getMovementPath() {
      PathEntity selected_path = null;
      int shortest_path_length_to_item_entity = Integer.MAX_VALUE;
      Iterator i = this.item_entities.iterator();

      while(i.hasNext()) {
         EntityItem entity_item = (EntityItem)i.next();
         if (this.willPickUp(entity_item.getEntityItem())) {
            int entity_item_x = MathHelper.floor_double(entity_item.posX);
            int entity_item_y = MathHelper.floor_double(entity_item.posY);
            int entity_item_z = MathHelper.floor_double(entity_item.posZ);
            PathEntity path = this.task_owner.getNavigator().getPathToXYZ(entity_item_x, entity_item_y, entity_item_z, this.max_path_length);
            if (path != null && path.getCurrentPathLength() < shortest_path_length_to_item_entity) {
               PathPoint final_point = path.getFinalPathPoint();
               if (final_point.xCoord == entity_item_x && final_point.yCoord == entity_item_y && final_point.zCoord == entity_item_z) {
                  shortest_path_length_to_item_entity = path.getCurrentPathLength();
                  this.selected_item_entity = entity_item;
                  selected_path = path;
               }
            }
         }
      }

      return selected_path;
   }

   public boolean continueExecuting() {
      return this.selected_item_entity != null && !this.selected_item_entity.isDead ? super.continueExecuting() : false;
   }

   public void resetTask() {
      super.resetTask();
      this.item_entities = null;
      this.selected_item_entity = null;
   }
}
