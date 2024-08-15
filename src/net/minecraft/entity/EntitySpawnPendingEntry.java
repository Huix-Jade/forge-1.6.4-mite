package net.minecraft.entity;

public class EntitySpawnPendingEntry {
   public Entity entity;
   public long scheduled_spawn_time;

   public EntitySpawnPendingEntry(Entity entity, long scheduled_spawn_time) {
      this.entity = entity;
      this.scheduled_spawn_time = scheduled_spawn_time;
   }
}
