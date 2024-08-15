package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;

public class EntityAITargetIfHostileToPlayers extends EntityAINearestAttackableTarget {
   private EntityWolf theEntity;

   public EntityAITargetIfHostileToPlayers(EntityWolf par1Entity, Class par2Class, int par3, boolean par4) {
      super(par1Entity, par2Class, par3, par4);
      this.theEntity = par1Entity;
   }

   public boolean shouldExecute() {
      return (this.theEntity.isHostileToPlayers() || this.theEntity.is_witch_ally) && super.shouldExecute();
   }

   public void startExecuting() {
      this.theEntity.setIsAttacking(true);
      super.startExecuting();
   }
}
