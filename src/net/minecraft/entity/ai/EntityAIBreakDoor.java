package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraft.pathfinding.PathFinder;

public class EntityAIBreakDoor extends EntityAIDoorInteract {
   private int breakingTime;
   private int field_75358_j = -1;

   public EntityAIBreakDoor(EntityLiving par1EntityLiving) {
      super(par1EntityLiving);
   }

   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else if (this.theEntity.worldObj.getBlock(this.entityPosX, this.entityPosY, this.entityPosZ) == Block.fenceGate && !(this.theEntity.getTarget() instanceof EntityPlayer)) {
         return false;
      } else {
         return this.theEntity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && PathFinder.isAClosedWoodenPortal(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ);
      }
   }

   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   public boolean continueExecuting() {
      double var1 = this.theEntity.getDistanceSq((double)this.entityPosX, (double)this.entityPosY, (double)this.entityPosZ);
      return this.breakingTime <= this.getTicksToBreakPortal() && PathFinder.isAClosedWoodenPortal(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ) && var1 < 4.0;
   }

   public void resetTask() {
      super.resetTask();
      this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.entityPosX, this.entityPosY, this.entityPosZ, -1);
   }

   public void updateTask() {
      super.updateTask();
      this.theEntity.refreshDespawnCounter(-400);
      int metadata = this.getTargetBlockMetadata();
      if (this.theEntity.getRNG().nextInt(20) == 0) {
         this.theEntity.worldObj.playAuxSFX(1010, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
         this.theEntity.worldObj.playAuxSFX(2001, this.entityPosX, this.entityPosY, this.entityPosZ, this.targetDoor.blockID + (metadata << 12) + RenderGlobal.SFX_2001_SUPPRESS_SOUND + RenderGlobal.SFX_2001_FOR_AI_BREAK_DOOR);
      }

      this.breakingTime += (int)(this.getStrVsTargetBlock() + 0.5F);
      int ticks_to_break_portal = this.getTicksToBreakPortal();
      if (this.breakingTime > ticks_to_break_portal) {
         this.breakingTime = ticks_to_break_portal;
      }

      int var1 = (int)((float)this.breakingTime / (float)ticks_to_break_portal * 10.0F);
      if (var1 != this.field_75358_j) {
         this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.entityPosX, this.entityPosY, this.entityPosZ, var1);
         this.field_75358_j = var1;
      }

      if (this.breakingTime == ticks_to_break_portal && this.theEntity.worldObj.difficultySetting == 3) {
         if (this.getTargetBlock() instanceof BlockDoor && BlockDoor.isTopHalf(this.getTargetBlockMetadata())) {
            this.theEntity.worldObj.setBlockToAir(this.entityPosX, this.entityPosY - 1, this.entityPosZ, 2);
         }

         this.theEntity.worldObj.setBlockToAir(this.entityPosX, this.entityPosY, this.entityPosZ);
         this.theEntity.worldObj.playAuxSFX(1012, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
         this.theEntity.worldObj.playAuxSFX(2001, this.entityPosX, this.entityPosY, this.entityPosZ, this.targetDoor.blockID + (metadata << 12));
      }

   }

   private Block getTargetBlock() {
      return this.theEntity.worldObj.getBlock(this.entityPosX, this.entityPosY, this.entityPosZ);
   }

   private int getTargetBlockMetadata() {
      return this.theEntity.worldObj.getBlockMetadata(this.entityPosX, this.entityPosY, this.entityPosZ);
   }

   private int getTicksToBreakPortal() {
      int ticks = this.getTargetBlock() == Block.doorWood ? 1920 : 480;
      if (this.theEntity instanceof EntityEarthElemental) {
         EntityEarthElemental elemental = (EntityEarthElemental)this.theEntity;
         if (elemental.isNormalClay()) {
            ticks /= 4;
         } else if (elemental.isHardenedClay()) {
            ticks /= 6;
         } else {
            ticks /= 8;
         }
      }

      return this.theEntity.isFrenzied() ? ticks / 2 : ticks;
   }

   private float getStrVsTargetBlock() {
      Block block = this.getTargetBlock();
      if (block == null) {
         return 0.0F;
      } else {
         Item held_item = this.theEntity.getHeldItem();
         if (held_item instanceof ItemTool) {
            ItemTool held_tool = (ItemTool)held_item;
            return Math.max(held_tool.getStrVsBlock(block, this.getTargetBlockMetadata()), 1.0F);
         } else {
            return 1.0F;
         }
      }
   }
}
