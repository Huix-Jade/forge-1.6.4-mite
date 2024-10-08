package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   boolean entityCallsForHelp;
   private int field_142052_b;

   public EntityAIHurtByTarget(EntityCreature par1EntityCreature, boolean par2) {
      super(par1EntityCreature, false);
      this.entityCallsForHelp = par2;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      int var1 = this.taskOwner.func_142015_aE();
      return var1 != this.field_142052_b && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
      this.field_142052_b = this.taskOwner.func_142015_aE();
      if (this.entityCallsForHelp) {
         double var1 = (double)this.taskOwner.getMaxTargettingRange();
         List var3 = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0, this.taskOwner.posY + 1.0, this.taskOwner.posZ + 1.0).expand(var1, 10.0, var1));
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            EntityCreature var5 = (EntityCreature)var4.next();
            if (this.taskOwner != var5 && var5.getAttackTarget() == null && !var5.isOnSameTeam(this.taskOwner.getAITarget())) {
               var5.setAttackTarget(this.taskOwner.getAITarget());
            }
         }
      }

      super.startExecuting();
   }
}
