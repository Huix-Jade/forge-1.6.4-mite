package net.minecraft.block;

public class BlockEventData {
   private int coordX;
   private int coordY;
   private int coordZ;
   private int blockID;
   private int eventID;
   private int eventParameter;

   public BlockEventData(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.coordX = var1;
      this.coordY = var2;
      this.coordZ = var3;
      this.eventID = var5;
      this.eventParameter = var6;
      this.blockID = var4;
   }

   public int getX() {
      return this.coordX;
   }

   public int getY() {
      return this.coordY;
   }

   public int getZ() {
      return this.coordZ;
   }

   public int getEventID() {
      return this.eventID;
   }

   public int getEventParameter() {
      return this.eventParameter;
   }

   public int getBlockID() {
      return this.blockID;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData var2 = (BlockEventData)var1;
         return this.coordX == var2.coordX && this.coordY == var2.coordY && this.coordZ == var2.coordZ && this.eventID == var2.eventID && this.eventParameter == var2.eventParameter && this.blockID == var2.blockID;
      }
   }

   public String toString() {
      return "TE(" + this.coordX + "," + this.coordY + "," + this.coordZ + ")," + this.eventID + "," + this.eventParameter + "," + this.blockID;
   }
}
