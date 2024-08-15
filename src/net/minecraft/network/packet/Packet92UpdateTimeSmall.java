package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public final class Packet92UpdateTimeSmall extends Packet {
   public long[] world_age;

   public Packet92UpdateTimeSmall() {
      this.world_age = new long[4];
   }

   public Packet92UpdateTimeSmall(long[] par1) {
      this.world_age = new long[4];

      for(int i = 0; i < 4; ++i) {
         if (isTimeSuitable(par1[i])) {
            this.world_age[i] = (long)((int)par1[i]);
         } else {
            Minecraft.setErrorMessage("Packet92UpdateTimeSmall: time is too large!");
            this.world_age[i] = getLargestSuitableTime();
         }
      }

   }

   public static long getLargestSuitableTime() {
      return 2147483647L;
   }

   public static boolean isTimeSuitable(long time) {
      return time <= getLargestSuitableTime();
   }

   public static boolean areAllWorldTotalTimesSuitable(WorldServer[] world_servers) {
      for(int i = 0; i < world_servers.length; ++i) {
         if (!isTimeSuitable(world_servers[i].getTotalWorldTime())) {
            return false;
         }
      }

      return true;
   }

   public static long[] getTotalWorldTimes(MinecraftServer mc_server) {
      long[] total_world_times = new long[4];

      for(int i = 0; i < 4; ++i) {
         total_world_times[i] = mc_server.worldServers[i].getTotalWorldTime();
      }

      return total_world_times;
   }

   public Packet92UpdateTimeSmall(MinecraftServer mc_server) {
      this(getTotalWorldTimes(mc_server));
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      for(int i = 0; i < 4; ++i) {
         this.world_age[i] = (long)par1DataInput.readInt();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      for(int i = 0; i < 4; ++i) {
         par1DataOutput.writeInt((int)this.world_age[i]);
      }

   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleUpdateTimeSmall(this);
   }

   public int getPacketSize() {
      return 16;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      return true;
   }

   public boolean canProcessAsync() {
      return false;
   }
}
