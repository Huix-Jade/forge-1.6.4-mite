package net.minecraft.server.management;

import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerPositionComparator implements Comparator {
   private final ChunkCoordinates theChunkCoordinates;

   public PlayerPositionComparator(ChunkCoordinates var1) {
      this.theChunkCoordinates = var1;
   }

   public int comparePlayers(EntityPlayerMP var1, EntityPlayerMP var2) {
      double var3 = var1.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
      double var5 = var2.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
      if (var3 < var5) {
         return -1;
      } else {
         return var3 > var5 ? 1 : 0;
      }
   }

   // $FF: synthetic method
   public int compare(Object var1, Object var2) {
      return this.comparePlayers((EntityPlayerMP)var1, (EntityPlayerMP)var2);
   }
}
