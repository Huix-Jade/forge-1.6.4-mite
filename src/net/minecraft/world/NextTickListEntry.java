package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable {
   private static long nextTickEntryID;
   public int xCoord;
   public int yCoord;
   public int zCoord;
   public int blockID;
   public long scheduledTime;
   public int priority;
   private long tickEntryID;

   public NextTickListEntry(int var1, int var2, int var3, int var4) {
      this.tickEntryID = (long)(nextTickEntryID++);
      this.xCoord = var1;
      this.yCoord = var2;
      this.zCoord = var3;
      this.blockID = var4;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry var2 = (NextTickListEntry)var1;
         return this.xCoord == var2.xCoord && this.yCoord == var2.yCoord && this.zCoord == var2.zCoord && Block.isAssociatedBlockID(this.blockID, var2.blockID);
      }
   }

   public int hashCode() {
      return (this.xCoord * 1024 * 1024 + this.zCoord * 1024 + this.yCoord) * 256;
   }

   public NextTickListEntry setScheduledTime(long var1) {
      this.scheduledTime = var1;
      return this;
   }

   public void setPriority(int var1) {
      this.priority = var1;
   }

   public int comparer(NextTickListEntry var1) {
      if (this.scheduledTime < var1.scheduledTime) {
         return -1;
      } else if (this.scheduledTime > var1.scheduledTime) {
         return 1;
      } else if (this.priority != var1.priority) {
         return this.priority - var1.priority;
      } else if (this.tickEntryID < var1.tickEntryID) {
         return -1;
      } else {
         return this.tickEntryID > var1.tickEntryID ? 1 : 0;
      }
   }

   public String toString() {
      return this.blockID + ": (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + "), " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.comparer((NextTickListEntry)var1);
   }
}
