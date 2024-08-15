package net.minecraft.world;

import net.minecraft.util.ChunkCoordinates;

public class PortalPosition extends ChunkCoordinates {
   public long lastUpdateTime;
   final Teleporter teleporterInstance;

   public PortalPosition(Teleporter par1Teleporter, int par2, int par3, int par4, long par5) {
      super(par2, par3, par4);
      this.teleporterInstance = par1Teleporter;
      this.lastUpdateTime = par5;
   }

   public long getLong(String field) {
      try {
         return this.getClass().getDeclaredField(field).getLong(this);
      } catch (Exception var3) {
         return 0L;
      }
   }
}
