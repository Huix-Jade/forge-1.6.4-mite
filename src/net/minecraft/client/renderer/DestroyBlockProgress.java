package net.minecraft.client.renderer;

public class DestroyBlockProgress {
   private final int miningPlayerEntId;
   private final int partialBlockX;
   private final int partialBlockY;
   private final int partialBlockZ;
   private int partialBlockProgress;
   private int createdAtCloudUpdateTick;

   public DestroyBlockProgress(int var1, int var2, int var3, int var4) {
      this.miningPlayerEntId = var1;
      this.partialBlockX = var2;
      this.partialBlockY = var3;
      this.partialBlockZ = var4;
   }

   public int getPartialBlockX() {
      return this.partialBlockX;
   }

   public int getPartialBlockY() {
      return this.partialBlockY;
   }

   public int getPartialBlockZ() {
      return this.partialBlockZ;
   }

   public void setPartialBlockDamage(int var1) {
      if (var1 > 10) {
         var1 = 10;
      }

      this.partialBlockProgress = var1;
   }

   public int getPartialBlockDamage() {
      return this.partialBlockProgress;
   }

   public void setCloudUpdateTick(int var1) {
      this.createdAtCloudUpdateTick = var1;
   }

   public int getCreationCloudUpdateTick() {
      return this.createdAtCloudUpdateTick;
   }
}
