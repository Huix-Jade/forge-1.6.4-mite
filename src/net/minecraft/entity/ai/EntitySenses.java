package net.minecraft.entity.ai;

import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public final class EntitySenses {
   EntityLiving entityObj;
   HashMap sensing_cache_when_leaves_block_LOS = new HashMap();
   HashMap sensing_cache_when_leaves_allow_LOS = new HashMap();

   public EntitySenses(EntityLiving par1EntityLiving) {
      this.entityObj = par1EntityLiving;
   }

   public void clearSensingCache() {
      this.sensing_cache_when_leaves_block_LOS.clear();
      this.sensing_cache_when_leaves_allow_LOS.clear();
   }

   public boolean canSee(Entity entity) {
      return this.canSee(entity, false);
   }

   public boolean canSee(Entity entity, boolean ignore_leaves) {
      if (entity == null) {
         return false;
      } else {
         HashMap sensing_cache = ignore_leaves ? this.sensing_cache_when_leaves_allow_LOS : this.sensing_cache_when_leaves_block_LOS;
         Boolean seen_obj = (Boolean)sensing_cache.get(entity);
         if (seen_obj == null) {
            this.entityObj.worldObj.theProfiler.startSection("canSee");
            boolean seen = this.entityObj.canSeeEntity(entity, ignore_leaves);
            this.entityObj.worldObj.theProfiler.endSection();
            sensing_cache.put(entity, seen);
            return seen;
         } else {
            return seen_obj;
         }
      }
   }
}
