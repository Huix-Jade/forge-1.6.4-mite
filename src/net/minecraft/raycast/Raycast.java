package net.minecraft.raycast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AABBIntercept;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumEntityReachContext;
import net.minecraft.util.EnumFace;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class Raycast implements Comparator {
   private final World world;
   private final Vec3 origin;
   private final Vec3 limit;
   private RaycastPolicies policies;
   public static final int MAX_IMPEDANCE = 100;
   private int impedance;
   private boolean has_been_performed_vs_blocks;
   private boolean has_been_performed_vs_entities;
   private RaycastCollision block_collision;
   private List entity_collisions;
   private Entity originator;
   private boolean has_produced_collisions;
   private boolean is_for_player_selection;

   public Raycast() {
      this.world = null;
      this.origin = null;
      this.limit = null;
   }

   public Raycast(World world) {
      this.world = world;
      this.origin = world.getVec3();
      this.limit = world.getVec3();
   }

   public Raycast(World world, Vec3 origin) {
      this.world = world;
      this.origin = origin.copy();
      this.limit = world.getVec3();
   }

   public Raycast(World world, Vec3 origin, Vec3 limit) {
      this.world = world;
      this.origin = origin.copy();
      this.limit = limit.copy();
   }

   private Raycast(World world, Vec3 origin, Vec3 normalized_direction, double distance_to_limit) {
      this(world, origin, origin.applyDirectionAndDistance(normalized_direction, distance_to_limit));
   }

   public Raycast(EntityLivingBase observer, float partial_tick) {
      this(observer, partial_tick, 16.0);
   }

   public Raycast(EntityLivingBase observer, float partial_tick, double distance_to_limit) {
      this(observer.worldObj, observer.getEyePosition(partial_tick), observer.getLook(partial_tick), distance_to_limit);
      this.originator = observer;
   }

   public World getWorld() {
      return this.world;
   }

   public Vec3 getOrigin() {
      return this.origin;
   }

   public Vec3 getLimit() {
      return this.limit;
   }

   public Raycast setOrigin(Vec3 vec3) {
      if (this.has_produced_collisions) {
         Minecraft.setErrorMessage("setOrigin: cannot change origin after raycast has produced collisions");
         (new Exception()).printStackTrace();
         return this;
      } else {
         this.origin.setComponents(vec3);
         return this;
      }
   }

   public Raycast setLimit(Vec3 vec3) {
      this.limit.setComponents(vec3);
      return this;
   }

   public Raycast setLimitToBlockCollisionPoint() {
      this.setLimit(this.block_collision.position_hit);
      return this;
   }

   public Raycast setOriginAndLimit(Vec3 origin, Vec3 limit) {
      this.setOrigin(origin);
      this.setLimit(limit);
      return this;
   }

   public Raycast setOriginAndLimitForLookVector(EntityLivingBase observer, float partial_tick) {
      return this.setOriginAndLimitForLookVector(observer, partial_tick, 16.0);
   }

   public Raycast setOriginAndLimitForLookVector(EntityLivingBase observer, float partial_tick, double distance_to_limit) {
      this.setOriginator(observer);
      Vec3 origin = observer.getEyePosition(partial_tick);
      return this.setOriginAndLimit(origin, origin.applyDirectionAndDistance(observer.getLook(partial_tick), distance_to_limit));
   }

   public Raycast setOriginator(Entity originator) {
      this.originator = originator;
      return this;
   }

   public Entity getOriginator() {
      return this.originator;
   }

   public Raycast setHasProducedCollisions() {
      this.has_produced_collisions = true;
      return this;
   }

   public Raycast setPolicies(RaycastPolicies policies) {
      if (this.policies != null) {
         Minecraft.setErrorMessage("setPolicies: raycast already has a policies object");
         (new Exception()).printStackTrace();
      }

      this.policies = policies;
      return this;
   }

   public Raycast setForSelection(boolean hit_liquids) {
      this.setPolicies(RaycastPolicies.for_selection(hit_liquids));
      return this;
   }

   public Raycast setForPlayerSelection() {
      this.is_for_player_selection = true;
      return this;
   }

   public boolean isForPlayerSelection() {
      return this.is_for_player_selection;
   }

   public Raycast setForVision(boolean ignore_leaves) {
      this.setPolicies(RaycastPolicies.for_vision(ignore_leaves));
      return this;
   }

   public Raycast setForPhysicalReach() {
      this.setPolicies(RaycastPolicies.for_physical_reach);
      return this;
   }

   public Raycast setForBluntProjectile(Entity entity) {
      this.setPolicies(RaycastPolicies.for_blunt_projectile);
      this.setOriginator(entity);
      return this;
   }

   public Raycast setForThrownWeb(Entity entity) {
      this.setPolicies(RaycastPolicies.for_selection_hit_liquids);
      this.setOriginator(entity);
      return this;
   }

   public Raycast setForPiercingProjectile(Entity entity) {
      this.setPolicies(RaycastPolicies.for_piercing_projectile);
      this.setOriginator(entity);
      return this;
   }

   public boolean alwaysIgnoreLiquids() {
      return this.policies.alwaysIgnoreLiquids();
   }

   public Raycast setMultipleEntities(boolean multiple_entities) {
      this.policies.setMultipleEntities(multiple_entities);
      return this;
   }

   public Raycast setIncludeNonCollidableEntities(boolean include_non_collidable_entities) {
      this.policies.setIncludeNonCollidableEntities(include_non_collidable_entities);
      return this;
   }

   public Raycast clearImpedance() {
      this.impedance = 0;
      return this;
   }

   private int addImpedance(int impedance) {
      this.impedance += impedance;
      return this.impedance;
   }

   public boolean isFullyImpeded(int policy) {
      if (policy == -1) {
         return true;
      } else if (policy == 0) {
         return false;
      } else if (policy >= 1 && policy <= 9) {
         return this.addImpedance(policy * 10) >= 100;
      } else if (policy == 11) {
         return this.originator == null ? false : RNG.chance_in_10[++this.originator.raycast_seed_offset + this.originator.entityId & 32767] && RNG.chance_in_4[++this.originator.raycast_seed_offset + this.originator.entityId & 32767];
      } else if (policy != 15) {
         Minecraft.setErrorMessage("isFullyImpeded: unhandled policy " + policy);
         return true;
      } else {
         return this.originator == null ? false : RNG.chance_in_2[++this.originator.raycast_seed_offset + this.originator.entityId & 32767] && RNG.chance_in_4[++this.originator.raycast_seed_offset + this.originator.entityId & 32767];
      }
   }

   public boolean ignoreBlock(Block block, World world, int x, int y, int z) {
      return this.policies.ignoreBlock(block, world, x, y, z, this);
   }

   private void setBlockCollision(RaycastCollision rc) {
      if (rc != null && rc.raycast != this) {
         Minecraft.setErrorMessage("setBlockCollision: rc.raycast!=this");
         (new Exception()).printStackTrace();
      }

      this.block_collision = rc;
   }

   public Raycast performVsBlocks() {
      this.has_been_performed_vs_blocks = true;
      RaycastCollision[] rc = new RaycastCollision[4];
      double x_coord = this.origin.xCoord;
      double z_coord = this.origin.zCoord;
      this.origin.xCoord = x_coord - 1.0E-4;
      rc[0] = this.world.tryRaycastVsBlocks(this);
      this.origin.xCoord = x_coord + 1.0E-4;
      rc[1] = this.world.tryRaycastVsBlocks(this);
      this.origin.xCoord = x_coord;
      this.origin.zCoord = z_coord - 1.0E-4;
      rc[2] = this.world.tryRaycastVsBlocks(this);
      this.origin.zCoord = z_coord + 1.0E-4;
      rc[3] = this.world.tryRaycastVsBlocks(this);
      this.origin.zCoord = z_coord;
      double shortest_distance_to_collision = 0.0;
      this.block_collision = null;

      for(int i = 0; i < rc.length; ++i) {
         if (rc[i] != null) {
            double distance = rc[i].getDistanceFromOriginToCollisionPoint();
            if (this.block_collision == null || distance < shortest_distance_to_collision) {
               this.setBlockCollision(rc[i]);
               shortest_distance_to_collision = distance;
            }
         }
      }

      return this;
   }

   public Raycast performVsBlocksSingle() {
      this.has_been_performed_vs_blocks = true;
      this.setBlockCollision(this.world.tryRaycastVsBlocks(this));
      return this;
   }

   public Raycast performVsEntities() {
      this.has_been_performed_vs_entities = true;
      this.entity_collisions = new ArrayList();
      double min_x;
      double max_x;
      if (this.origin.xCoord < this.limit.xCoord) {
         min_x = this.origin.xCoord;
         max_x = this.limit.xCoord;
      } else {
         min_x = this.limit.xCoord;
         max_x = this.origin.xCoord;
      }

      double min_y;
      double max_y;
      if (this.origin.yCoord < this.limit.yCoord) {
         min_y = this.origin.yCoord;
         max_y = this.limit.yCoord;
      } else {
         min_y = this.limit.yCoord;
         max_y = this.origin.yCoord;
      }

      double min_z;
      double max_z;
      if (this.origin.zCoord < this.limit.zCoord) {
         min_z = this.origin.zCoord;
         max_z = this.limit.zCoord;
      } else {
         min_z = this.limit.zCoord;
         max_z = this.origin.zCoord;
      }

      AxisAlignedBB bb = new AxisAlignedBB(min_x, min_y, min_z, max_x, max_y, max_z);
      List entities = this.world.getEntitiesWithinAABBExcludingEntity(this.originator, bb.expand(1.0, 1.0, 1.0));
      if (this.policies.getMultipleEntities()) {
         for(int i = 0; i < entities.size(); ++i) {
            Entity entity = (Entity)entities.get(i);
            if ((this.policies.getNonCollidableEntityPolicy() || entity.canBeCollidedWith()) && (this.originator == null || !this.originator.cannotRaycastCollideWith(entity))) {
               float cbs = entity.getCollisionBorderSize(this.originator);
               AxisAlignedBB effective_collision_box = entity.boundingBox.expand((double)cbs, (double)cbs, (double)cbs);
               entity.modifyEffectiveCollisionBoxForRaycastFromEntity(effective_collision_box, this.originator);
               if (effective_collision_box != null) {
                  if (effective_collision_box.isVecInside(this.origin)) {
                     this.entity_collisions.add(new RaycastCollision(this, entity, new AABBIntercept(this.origin.copy(), (EnumFace)null)));
                  } else {
                     AABBIntercept intercept = effective_collision_box.calculateIntercept(this.world, this.origin, this.limit);
                     if (intercept != null) {
                        this.entity_collisions.add(new RaycastCollision(this, entity, intercept));
                     }
                  }
               }
            }
         }

         Collections.sort(this.entity_collisions, this);
      } else {
         Entity nearest_collided_entity = null;
         AABBIntercept nearest_intercept = null;
         double distance_to_nearest_collision = 0.0;

         for(int i = 0; i < entities.size(); ++i) {
            Entity entity = (Entity)entities.get(i);
            if ((this.policies.getNonCollidableEntityPolicy() || entity.canBeCollidedWith()) && (this.originator == null || !this.originator.cannotRaycastCollideWith(entity))) {
               float cbs = entity.getCollisionBorderSize(this.originator);
               AxisAlignedBB effective_collision_box = entity.boundingBox.expand((double)cbs, (double)cbs, (double)cbs);
               entity.modifyEffectiveCollisionBoxForRaycastFromEntity(effective_collision_box, this.originator);
               if (effective_collision_box != null) {
                  if (effective_collision_box.isVecInside(this.origin)) {
                     nearest_collided_entity = entity;
                     nearest_intercept = new AABBIntercept(this.origin.copy(), (EnumFace)null);
                     break;
                  }

                  AABBIntercept intercept = effective_collision_box.calculateIntercept(this.world, this.origin, this.limit);
                  if (intercept != null) {
                     double distance_to_collision = this.origin.distanceTo(intercept.position_hit);
                     if (nearest_collided_entity == null || distance_to_collision < distance_to_nearest_collision) {
                        nearest_collided_entity = entity;
                        nearest_intercept = intercept;
                        distance_to_nearest_collision = distance_to_collision;
                     }
                  }
               }
            }
         }

         if (nearest_collided_entity != null) {
            this.entity_collisions.add(new RaycastCollision(this, nearest_collided_entity, nearest_intercept));
         }
      }

      return this;
   }

   public Raycast performVsBlocksAndEntities() {
      this.performVsBlocks();
      this.performVsEntities();
      return this;
   }

   public int compare(Object a, Object b) {
      RaycastCollision rc_a = (RaycastCollision)a;
      RaycastCollision rc_b = (RaycastCollision)b;
      double distance_a = this.origin.distanceTo(rc_a.position_hit);
      double distance_b = this.origin.distanceTo(rc_b.position_hit);
      return distance_a < distance_b ? -1 : (distance_a > distance_b ? 1 : 0);
   }

   private boolean hasBeenPerformedVsBlocksOrEntities() {
      return this.has_been_performed_vs_blocks || this.has_been_performed_vs_entities;
   }

   public boolean hasBlockCollision() {
      if (!this.hasBeenPerformedVsBlocksOrEntities()) {
         Minecraft.setErrorMessage("hasBlockCollision: raycast was never performed vs blocks or entities");
         (new Exception()).printStackTrace();
      }

      return this.block_collision != null;
   }

   public boolean hasNoBlockCollision() {
      return !this.hasBlockCollision();
   }

   public RaycastCollision getNearestEntityCollision() {
      if (!this.hasBeenPerformedVsBlocksOrEntities()) {
         Minecraft.setErrorMessage("getNearestEntityCollision: raycast was never performed vs blocks or entities");
         (new Exception()).printStackTrace();
      }

      return this.entity_collisions.isEmpty() ? null : (RaycastCollision)this.entity_collisions.get(0);
   }

   public boolean hasEntityCollisions() {
      if (!this.hasBeenPerformedVsBlocksOrEntities()) {
         Minecraft.setErrorMessage("hasEntityCollisions: raycast was never performed vs blocks or entities");
         (new Exception()).printStackTrace();
      }

      return this.entity_collisions != null && !this.entity_collisions.isEmpty();
   }

   public boolean hasCollisions() {
      return this.hasBlockCollision() || this.hasEntityCollisions();
   }

   public boolean hasNoCollisions() {
      return !this.hasCollisions();
   }

   public RaycastCollision getBlockCollision() {
      if (!this.hasBeenPerformedVsBlocksOrEntities()) {
         Minecraft.setErrorMessage("getBlockCollision: raycast was never performed vs blocks or entities");
         (new Exception()).printStackTrace();
      }

      return this.block_collision;
   }

   public RaycastCollision getBlockCollision(Vec3 limit) {
      return this.setLimit(limit).performVsBlocks().getBlockCollision();
   }

   public boolean checkForNoBlockCollision(Vec3 limit) {
      return this.getBlockCollision(limit) == null;
   }

   public RaycastCollision getBlockCollision(Vec3 origin, Vec3 limit) {
      return this.setOriginAndLimit(origin, limit).performVsBlocks().getBlockCollision();
   }

   public boolean checkForNoBlockCollision(Vec3 origin, Vec3 limit) {
      return this.getBlockCollision(origin, limit) == null;
   }

   public List getEntityCollisions() {
      if (!this.hasBeenPerformedVsBlocksOrEntities()) {
         Minecraft.setErrorMessage("getEntityCollisions: raycast was never performed vs blocks or entities");
         (new Exception()).printStackTrace();
      }

      return this.entity_collisions;
   }

   private float calcEntityBiasMethod2(double distance_to_entity_intersection, Entity collided_entity) {
      float entity_bias = -collided_entity.getCollisionBorderSize(this.originator) * 3.0F;
      if (this.originator instanceof EntityPlayer) {
         RaycastCollision rc = this.world.getBlockCollisionForPhysicalReach(this.origin, this.limit);
         if (rc == null || rc.getDistanceFromOriginToCollisionPoint() > distance_to_entity_intersection + (double)(collided_entity.getCollisionBorderSize(this.originator) * 3.0F)) {
            entity_bias += 0.75F;
         }
      } else if (this.originator instanceof EntityArrow) {
      }

      return entity_bias;
   }

   public RaycastCollision getNearestCollision() {
      if (this.hasNoCollisions()) {
         return null;
      } else if (!this.hasEntityCollisions()) {
         return this.block_collision;
      } else {
         RaycastCollision nearest_entity_collision = this.getNearestEntityCollision();
         if (!this.hasBlockCollision()) {
            return nearest_entity_collision;
         } else {
            double distance_to_block_intersection = this.block_collision.getDistanceFromOriginToCollisionPoint();
            double distance_to_entity_intersection = nearest_entity_collision.getDistanceFromOriginToCollisionPoint();
            float entity_bias = this.calcEntityBiasMethod2(distance_to_entity_intersection, nearest_entity_collision.getEntityHit());
            return distance_to_entity_intersection - (double)entity_bias <= distance_to_block_intersection ? nearest_entity_collision : this.block_collision;
         }
      }
   }

   public RaycastCollision getNearestCollisionReachableByObserver(EnumEntityReachContext entity_reach_context, float partial_tick) {
      RaycastCollision nearest_rc = this.getNearestCollision();
      if (nearest_rc == null) {
         return null;
      } else {
         double reach;
         if (nearest_rc.isBlock()) {
            Vec3[] block_reach_from_points;
            if (this.originator.isEntityPlayer()) {
               EntityPlayer player = (EntityPlayer)this.originator;
               reach = (double)player.getReach(nearest_rc.getBlockHit(), nearest_rc.block_hit_metadata);
               block_reach_from_points = player.getBlockReachFromPoints(partial_tick);
            } else {
               reach = 16.0;
               block_reach_from_points = new Vec3[]{this.originator.isEntityLivingBase() ? this.originator.getAsEntityLivingBase().getPrimaryPointOfAttack() : this.originator.getCenterPoint()};
            }

            for(int i = 0; i < block_reach_from_points.length; ++i) {
               if (block_reach_from_points[i].distanceTo((double)nearest_rc.block_hit_x + 0.5, (double)nearest_rc.block_hit_y + 0.5, (double)nearest_rc.block_hit_z + 0.5) <= reach) {
                  return nearest_rc;
               }
            }

            return null;
         } else {
            reach = entity_reach_context == null ? 16.0 : (this.originator.isEntityPlayer() ? (double)this.originator.getAsPlayer().getReach(entity_reach_context, nearest_rc.getEntityHit()) : (double)this.originator.getAsEntityLiving().getReach());
            return nearest_rc.getDistanceFromOriginToCollisionPoint() <= reach ? nearest_rc : null;
         }
      }
   }

   public static RaycastCollision getShortestRaycastCollision(RaycastCollision[] rc) {
      RaycastCollision shortest_rc = null;
      double shortest_distance = 0.0;

      for(int i = 0; i < rc.length; ++i) {
         if (rc[i] != null) {
            double distance = rc[i].getDistanceFromOriginToCollisionPoint();
            if (shortest_rc == null || distance < shortest_distance) {
               shortest_rc = rc[i];
               shortest_distance = distance;
            }
         }
      }

      return shortest_rc;
   }

   public String toString() {
      if (!this.hasBeenPerformedVsBlocksOrEntities()) {
         return null;
      } else {
         StringBuffer sb = new StringBuffer();
         if (this.block_collision != null) {
            sb.append(this.block_collision + (this.hasEntityCollisions() ? ", " : ""));
         }

         if (this.hasEntityCollisions()) {
            if (this.policies.getMultipleEntities()) {
               Iterator i = this.entity_collisions.iterator();
               sb.append((RaycastCollision)i.next());

               while(i.hasNext()) {
                  RaycastCollision rc = (RaycastCollision)i.next();
                  sb.append(", " + rc);
               }
            } else {
               RaycastCollision rc = this.getNearestEntityCollision();
               sb.append(rc + " @ " + StringHelper.formatFloat((float)rc.getDistanceFromOriginToCollisionPoint()));
            }
         }

         return sb.toString();
      }
   }

   private static boolean coordsExistInArray(int x, int y, int z, int[] coords, int num_coord_triplets) {
      for(int i = 0; i < num_coord_triplets; ++i) {
         int offset = i * 3;
         if (coords[offset] == x && coords[offset + 1] == y && coords[offset + 2] == z) {
            return true;
         }
      }

      return false;
   }

   public static int[] getFullBlockIntercepts(Vec3 origin, Vec3 limit) {
      double distance = origin.distanceTo(limit);
      int[] coords = new int[((int)distance + 3) * 4 * 3];
      int num_coord_triplets = 1;
      coords[0] = origin.getBlockX();
      coords[1] = origin.getBlockY();
      coords[2] = origin.getBlockZ();
      int limit_x = limit.getBlockX();
      int limit_y = limit.getBlockY();
      int limit_z = limit.getBlockZ();
      Vec3 pos = origin.copy();
      Vec3 normalized_vector = origin.copy().setComponents(limit.xCoord - origin.xCoord, limit.yCoord - origin.yCoord, limit.zCoord - origin.zCoord).normalize();
      AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB();

      while(true) {
         int x = pos.getBlockX();
         int y = pos.getBlockY();
         int z = pos.getBlockZ();

         for(int dx = -1; dx <= 1; ++dx) {
            for(int dy = -1; dy <= 1; ++dy) {
               for(int dz = -1; dz <= 1; ++dz) {
                  if (!coordsExistInArray(x + dx, y + dy, z + dz, coords, num_coord_triplets)) {
                     bb.setBounds(x + dx, y + dy, z + dz, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                     AABBIntercept intercept = bb.calculateIntercept((World)null, origin, limit);
                     if (intercept != null) {
                        int offset = num_coord_triplets * 3;
                        coords[offset] = x + dx;
                        coords[offset + 1] = y + dy;
                        coords[offset + 2] = z + dz;
                        ++num_coord_triplets;
                     }
                  }
               }
            }
         }

         if (x == limit_x && y == limit_y && z == limit_z || pos.distanceTo(origin) > distance) {
            int[] coords_trimmed = new int[num_coord_triplets * 3];
            System.arraycopy(coords, 0, coords_trimmed, 0, num_coord_triplets * 3);
            return coords_trimmed;
         }

         pos.xCoord += normalized_vector.xCoord;
         pos.yCoord += normalized_vector.yCoord;
         pos.zCoord += normalized_vector.zCoord;
      }
   }
}
