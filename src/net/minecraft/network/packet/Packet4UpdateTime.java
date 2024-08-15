package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public final class Packet4UpdateTime extends Packet {
   public long[] world_age;

   public Packet4UpdateTime() {
      this.world_age = new long[4];
   }

   public Packet4UpdateTime(long[] par1) {
      this.world_age = new long[4];
      boolean use_small_packet_instead = true;

      for(int i = 0; i < 4; ++i) {
         if (!Packet92UpdateTimeSmall.isTimeSuitable(par1[i])) {
            use_small_packet_instead = false;
         }

         this.world_age[i] = par1[i];
      }

      if (use_small_packet_instead) {
         Minecraft.setErrorMessage("Packet4UpdateTime: use Packet92UpdateTimeSmall instead");
         (new Exception()).printStackTrace();
      }

   }

   public static long[] getTotalWorldTimes(MinecraftServer mc_server) {
      long[] total_world_times = new long[4];

      for(int i = 0; i < 4; ++i) {
         total_world_times[i] = mc_server.worldServers[i].getTotalWorldTime();
      }

      return total_world_times;
   }

   public Packet4UpdateTime(MinecraftServer mc_server) {
      this(getTotalWorldTimes(mc_server));
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      for(int i = 0; i < 4; ++i) {
         this.world_age[i] = par1DataInput.readLong();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      for(int i = 0; i < 4; ++i) {
         par1DataOutput.writeLong(this.world_age[i]);
      }

   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleUpdateTime(this);
   }

   public int getPacketSize() {
      return 32;
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
