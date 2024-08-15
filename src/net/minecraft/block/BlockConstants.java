package net.minecraft.block;

import net.minecraft.client.Minecraft;

public class BlockConstants {
   private boolean is_always_legal = true;
   private boolean never_hides_adjacent_faces;
   private boolean uses_new_sand_physics;
   private boolean is_always_immutable = false;
   private boolean uses_alpha_blending;
   private Boolean connects_with_fence;

   public void validate(Block block) {
      String msg = null;
      if (!(block instanceof BlockStairs)) {
         if (this.uses_alpha_blending) {
            if (block.getRenderBlockPass() != 1) {
               msg = block + " uses alpha blending but getRenderBlockPass()==" + block.getRenderBlockPass();
            }
         } else if (block.getRenderBlockPass() != 0) {
            msg = block + " does not use alpha blending but getRenderBlockPass()==" + block.getRenderBlockPass();
         }
      }

      if (msg != null) {
         Minecraft.setErrorMessage("validate: " + msg);
         (new Exception()).printStackTrace();
      }
   }

   public BlockConstants setNotAlwaysLegal() {
      this.is_always_legal = false;
      return this;
   }

   public BlockConstants setAlwaysImmutable() {
      this.is_always_legal = true;
      this.is_always_immutable = true;
      return this;
   }

   public BlockConstants setUsesAlphaBlending() {
      this.uses_alpha_blending = true;
      return this.setNeverHidesAdjacentFaces();
   }

   public BlockConstants setNeverHidesAdjacentFaces() {
      this.connects_with_fence = false;
      this.never_hides_adjacent_faces = true;
      return this;
   }

   public BlockConstants setUseNewSandPhysics() {
      if (!Minecraft.allow_new_sand_physics) {
         return this;
      } else {
         this.uses_new_sand_physics = true;
         this.is_always_legal = false;
         return this;
      }
   }

   public BlockConstants setAlwaysConnectsWithFence() {
      this.connects_with_fence = true;
      return this;
   }

   public BlockConstants setNeverConnectsWithFence() {
      this.connects_with_fence = false;
      return this;
   }

   public boolean isAlwaysLegal() {
      return this.is_always_legal;
   }

   public boolean neverHidesAdjacentFaces() {
      return this.never_hides_adjacent_faces;
   }

   public boolean isAlwaysImmutable() {
      return this.is_always_immutable;
   }

   public boolean usesNewSandPhysics() {
      return this.uses_new_sand_physics;
   }

   public Boolean connectsWithFence() {
      return this.connects_with_fence;
   }
}
