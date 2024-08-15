package net.minecraft.raycast;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public final class RaycastPolicies {
   public static final int TERMINATE = -1;
   public static final int IGNORE = 0;
   public static final int IMPEDE_BY_10 = 1;
   public static final int IMPEDE_BY_50 = 5;
   public static final int IMPEDE_BY_90 = 9;
   public static final int TERMINATE_10_PERCENT = 11;
   public static final int TERMINATE_50_PERCENT = 15;
   public static final RaycastPolicies for_selection_hit_liquids = (new RaycastPolicies()).setImmutable();
   public static final RaycastPolicies for_selection_ignore_liquids = (new RaycastPolicies()).setLiquidsPolicy(0).setImmutable();
   public static final RaycastPolicies for_vision_standard = (new RaycastPolicies()).setForVision().setImmutable();
   public static final RaycastPolicies for_vision_ignore_leaves = (new RaycastPolicies()).setForVision().setLeavesPolicy(0).setImmutable();
   public static final RaycastPolicies for_physical_reach = (new RaycastPolicies()).setForPhysicalReach().setImmutable();
   public static final RaycastPolicies for_physical_reach_narrow = (new RaycastPolicies()).setForPhysicalReach().setMetalBarsPolicy(0).setImmutable();
   public static final RaycastPolicies for_entity_item_pickup = (new RaycastPolicies()).setForPhysicalReach().setLeavesPolicy(0).setImmutable();
   public static final RaycastPolicies for_blunt_projectile = (new RaycastPolicies()).setForBluntProjectile((Entity)null, new Raycast()).setImmutable();
   public static final RaycastPolicies for_piercing_projectile = (new RaycastPolicies()).setForPiercingProjectile((Entity)null, new Raycast()).setImmutable();
   public static final RaycastPolicies for_third_person_view = (new RaycastPolicies()).setForThirdPersonView().setImmutable();
   private int liquids_policy = -1;
   private int glass_and_ice_policy = -1;
   private int all_portals_policy = -1;
   private int open_portals_policy = -1;
   private int open_gates_policy = -1;
   private int tall_grass_policy = -1;
   private int leaves_policy = -1;
   private int reeds_policy = -1;
   private int vines_policy = -1;
   private int fence_policy = -1;
   private int metal_bars_policy = -1;
   private int non_solid_block_policy = -1;
   private int uncovered_cauldron_policy = -1;
   private boolean multiple_entities;
   private boolean include_non_collidable_entities;
   private boolean immutable;

   private RaycastPolicies() {
   }

   public RaycastPolicies getMutableCopy() {
      RaycastPolicies policies = new RaycastPolicies();
      policies.liquids_policy = this.liquids_policy;
      policies.glass_and_ice_policy = this.glass_and_ice_policy;
      policies.all_portals_policy = this.all_portals_policy;
      policies.open_portals_policy = this.open_portals_policy;
      policies.open_gates_policy = this.open_gates_policy;
      policies.tall_grass_policy = this.tall_grass_policy;
      policies.leaves_policy = this.leaves_policy;
      policies.reeds_policy = this.reeds_policy;
      policies.vines_policy = this.vines_policy;
      policies.fence_policy = this.fence_policy;
      policies.metal_bars_policy = this.metal_bars_policy;
      policies.non_solid_block_policy = this.non_solid_block_policy;
      policies.uncovered_cauldron_policy = this.uncovered_cauldron_policy;
      policies.multiple_entities = this.multiple_entities;
      policies.include_non_collidable_entities = this.include_non_collidable_entities;
      return policies;
   }

   public static RaycastPolicies for_selection(boolean hit_liquids) {
      return hit_liquids ? for_selection_hit_liquids : for_selection_ignore_liquids;
   }

   public static RaycastPolicies for_vision(boolean ignore_leaves) {
      return ignore_leaves ? for_vision_ignore_leaves : for_vision_standard;
   }

   private void raiseAttemptingToChangeImmutablePoliciesErrorMessage() {
      Minecraft.setErrorMessage("RaycastPolicies: attempting to change immutable policies");
      (new Exception()).printStackTrace();
   }

   public RaycastPolicies setLiquidsPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.liquids_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setGlassAndIcePolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.glass_and_ice_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setAllPortalsPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.all_portals_policy = policy;
         this.open_portals_policy = policy;
         this.open_gates_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setOpenPortalsPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.open_portals_policy = policy;
         this.open_gates_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setOpenGatesPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.open_gates_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setTallGrassPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.tall_grass_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setLeavesPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.leaves_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setReedsPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.reeds_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setVinesPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.vines_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setFencePolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.fence_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setMetalBarsPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.metal_bars_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setNonSolidBlockPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.non_solid_block_policy = policy;
         return this;
      }
   }

   public RaycastPolicies setUncoveredCauldronPolicy(int policy) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.uncovered_cauldron_policy = policy;
         return this;
      }
   }

   private RaycastPolicies setForVision() {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.setLiquidsPolicy(0);
         this.setGlassAndIcePolicy(0);
         this.setAllPortalsPolicy(0);
         this.setTallGrassPolicy(5);
         this.setLeavesPolicy(5);
         this.setReedsPolicy(5);
         this.setVinesPolicy(5);
         this.setFencePolicy(0);
         this.setMetalBarsPolicy(0);
         return this;
      }
   }

   private RaycastPolicies setForPhysicalReach() {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.setLiquidsPolicy(0);
         this.setOpenGatesPolicy(0);
         this.setTallGrassPolicy(0);
         this.setReedsPolicy(0);
         this.setVinesPolicy(0);
         this.setNonSolidBlockPolicy(0);
         this.setUncoveredCauldronPolicy(0);
         return this;
      }
   }

   private RaycastPolicies setForBluntProjectile(Entity entity, Raycast raycast) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.setLiquidsPolicy(0);
         this.setOpenGatesPolicy(0);
         this.setTallGrassPolicy(0);
         this.setVinesPolicy(15);
         raycast.setOriginator(entity);
         return this;
      }
   }

   private RaycastPolicies setForPiercingProjectile(Entity entity, Raycast raycast) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.setForBluntProjectile(entity, raycast);
         this.setLeavesPolicy(11);
         this.setReedsPolicy(0);
         this.setVinesPolicy(0);
         this.setMetalBarsPolicy(0);
         return this;
      }
   }

   private RaycastPolicies setForThirdPersonView() {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.setLiquidsPolicy(0);
         this.setGlassAndIcePolicy(0);
         this.setAllPortalsPolicy(0);
         this.setTallGrassPolicy(0);
         this.setReedsPolicy(5);
         this.setVinesPolicy(0);
         this.setFencePolicy(0);
         this.setMetalBarsPolicy(0);
         return this;
      }
   }

   private RaycastPolicies setImmutable() {
      this.immutable = true;
      return this;
   }

   private int getLiquidsPolicy() {
      return this.liquids_policy;
   }

   public boolean alwaysIgnoreLiquids() {
      return this.liquids_policy == 0;
   }

   public RaycastPolicies setMultipleEntities(boolean multiple_entities) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.multiple_entities = multiple_entities;
         return this;
      }
   }

   public RaycastPolicies setIncludeNonCollidableEntities(boolean include_non_collidable_entities) {
      if (this.immutable) {
         this.raiseAttemptingToChangeImmutablePoliciesErrorMessage();
         return this;
      } else {
         this.include_non_collidable_entities = include_non_collidable_entities;
         return this;
      }
   }

   public boolean getMultipleEntities() {
      return this.multiple_entities;
   }

   public boolean getNonCollidableEntityPolicy() {
      return this.include_non_collidable_entities;
   }

   public boolean ignoreBlock(World world, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      return block == null ? true : this.ignoreBlock(block, world, x, y, z, new Raycast());
   }

   public boolean ignoreBlock(Block block, World world, int x, int y, int z, Raycast raycast) {
      if (this.glass_and_ice_policy != -1 && (block.blockMaterial == Material.glass || block.blockMaterial == Material.ice) && !raycast.isFullyImpeded(this.glass_and_ice_policy)) {
         return this != for_third_person_view || block.blockMaterial != Material.ice;
      } else {
         if (block.isPortal()) {
            if (this.open_gates_policy != -1 && block instanceof BlockFenceGate && BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z)) && !raycast.isFullyImpeded(this.open_gates_policy)) {
               return true;
            }

            if (this.open_portals_policy != -1 && block.isOpenPortal(world, x, y, z) && !raycast.isFullyImpeded(this.open_portals_policy)) {
               return true;
            }

            if (this.all_portals_policy != -1 && !raycast.isFullyImpeded(this.all_portals_policy)) {
               return true;
            }
         }

         if (this.tall_grass_policy != -1 && (block == Block.tallGrass || block == Block.plantRed || block == Block.plantYellow || block instanceof BlockCrops || block instanceof BlockWeb) && !raycast.isFullyImpeded(this.tall_grass_policy)) {
            return true;
         } else if (this.non_solid_block_policy != -1 && !block.is_always_solid && (block.is_never_solid || !block.isSolid(world.getBlockMetadata(x, y, z)) || block.getCollisionBoundsCombined(world, x, y, z, (Entity)null, true) == null) && !raycast.isFullyImpeded(this.non_solid_block_policy)) {
            return true;
         } else if (this.uncovered_cauldron_policy != -1 && (block == Block.cauldron || block == Block.hopperBlock) && !world.isBlockFaceFlatAndSolid(x, y + 1, z, EnumFace.BOTTOM) && !raycast.isFullyImpeded(this.uncovered_cauldron_policy)) {
            return true;
         } else if (this.leaves_policy != -1 && block instanceof BlockLeaves && !raycast.isFullyImpeded(this.leaves_policy)) {
            return true;
         } else if (this.reeds_policy != -1 && block instanceof BlockReed && !raycast.isFullyImpeded(this.reeds_policy)) {
            return true;
         } else if (this.vines_policy != -1 && block instanceof BlockVine && !raycast.isFullyImpeded(this.vines_policy)) {
            return true;
         } else if (this.fence_policy != -1 && block instanceof BlockFence && !raycast.isFullyImpeded(this.fence_policy)) {
            return true;
         } else {
            return this.metal_bars_policy != -1 && block instanceof BlockPane && block.blockMaterial.isMetal() && !raycast.isFullyImpeded(this.metal_bars_policy);
         }
      }
   }
}
