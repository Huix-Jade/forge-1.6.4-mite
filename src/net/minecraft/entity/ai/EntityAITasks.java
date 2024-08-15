package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;

public final class EntityAITasks {
   private List taskEntries = new ArrayList();
   private List executingTaskEntries = new ArrayList();
   private final Profiler theProfiler;
   private int tickCount;
   private int tickRate = 3;

   public EntityAITasks(Profiler par1Profiler) {
      this.theProfiler = par1Profiler;
   }

   public void addTask(int par1, EntityAIBase par2EntityAIBase) {
      this.taskEntries.add(new EntityAITaskEntry(this, par1, par2EntityAIBase));
   }

   public List getTaskEntries() {
      return this.taskEntries;
   }

   public EntityAIBase getTask(Class _class) {
      Iterator i = this.taskEntries.iterator();

      EntityAITaskEntry entry;
      do {
         if (!i.hasNext()) {
            return null;
         }

         entry = (EntityAITaskEntry)i.next();
      } while(entry.action.getClass() != _class);

      return entry.action;
   }

   public void removeTask(EntityAIBase par1EntityAIBase) {
      Iterator var2 = this.taskEntries.iterator();

      while(var2.hasNext()) {
         EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();
         EntityAIBase var4 = var3.action;
         if (var4 == par1EntityAIBase) {
            if (this.executingTaskEntries.contains(var3)) {
               var4.resetTask();
               this.executingTaskEntries.remove(var3);
            }

            var2.remove();
         }
      }

   }

   public void clear() {
      this.taskEntries.clear();
   }

   public void onUpdateTasks() {
      ArrayList var1 = new ArrayList();
      Iterator var2;
      EntityAITaskEntry var3;
      if (this.tickCount++ % this.tickRate == 0) {
         var2 = this.taskEntries.iterator();

         label58:
         while(true) {
            while(true) {
               if (!var2.hasNext()) {
                  break label58;
               }

               var3 = (EntityAITaskEntry)var2.next();
               boolean var4 = this.executingTaskEntries.contains(var3);
               if (!var4) {
                  break;
               }

               if (!this.canUse(var3) || !this.canContinue(var3)) {
                  var3.action.resetTask();
                  this.executingTaskEntries.remove(var3);
                  break;
               }
            }

            if (this.canUse(var3) && var3.action.shouldExecute()) {
               var1.add(var3);
               this.executingTaskEntries.add(var3);
            }
         }
      } else {
         var2 = this.executingTaskEntries.iterator();

         while(var2.hasNext()) {
            var3 = (EntityAITaskEntry)var2.next();
            if (!var3.action.continueExecuting()) {
               var3.action.resetTask();
               var2.remove();
            }
         }
      }

      this.theProfiler.startSection("goalStart");
      var2 = var1.iterator();

      while(var2.hasNext()) {
         var3 = (EntityAITaskEntry)var2.next();
         this.theProfiler.startSection(var3.action.getClass().getSimpleName());
         var3.action.startExecuting();
         this.theProfiler.endSection();
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("goalTick");
      var2 = this.executingTaskEntries.iterator();

      while(var2.hasNext()) {
         var3 = (EntityAITaskEntry)var2.next();
         var3.action.updateTask();
      }

      this.theProfiler.endSection();
   }

   private boolean canContinue(EntityAITaskEntry par1EntityAITaskEntry) {
      this.theProfiler.startSection("canContinue");
      boolean var2 = par1EntityAITaskEntry.action.continueExecuting();
      this.theProfiler.endSection();
      return var2;
   }

   private boolean canUse(EntityAITaskEntry par1EntityAITaskEntry) {
      this.theProfiler.startSection("canUse");
      Iterator var2 = this.taskEntries.iterator();

      while(var2.hasNext()) {
         EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();
         if (var3 != par1EntityAITaskEntry) {
            if (par1EntityAITaskEntry.priority >= var3.priority) {
               if (this.executingTaskEntries.contains(var3) && !this.areTasksCompatible(par1EntityAITaskEntry, var3)) {
                  this.theProfiler.endSection();
                  return false;
               }
            } else if (this.executingTaskEntries.contains(var3) && !var3.action.isInterruptible()) {
               this.theProfiler.endSection();
               return false;
            }
         }
      }

      this.theProfiler.endSection();
      return true;
   }

   private boolean areTasksCompatible(EntityAITaskEntry par1EntityAITaskEntry, EntityAITaskEntry par2EntityAITaskEntry) {
      return (par1EntityAITaskEntry.action.getMutexBits() & par2EntityAITaskEntry.action.getMutexBits()) == 0;
   }

   public boolean isTaskExecuting(Class class_of_task) {
      Iterator i = this.executingTaskEntries.iterator();

      EntityAITaskEntry entry;
      do {
         if (!i.hasNext()) {
            return false;
         }

         entry = (EntityAITaskEntry)i.next();
      } while(entry.action.getClass() != class_of_task);

      return true;
   }
}
