package net.minecraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SoonestReconnectionTime {
   public String username;
   public long tick_of_disconnection;
   public int adjusted_hour_of_disconnection;
   public long soonest_reconnection_tick;
   public long ticks_disconnected;

   public SoonestReconnectionTime(EntityPlayer player) {
      this.update(player);
   }

   public void update(EntityPlayer player) {
      World world = player.worldObj;
      this.username = player.username;
      this.tick_of_disconnection = world.getTotalWorldTime();
      int hour_of_sunrise = World.getTimeOfSunrise() / 1000;
      int hour_of_latest_reconnection = World.getHourOfLatestReconnection();
      this.adjusted_hour_of_disconnection = world.getHourOfDay();
      if (this.adjusted_hour_of_disconnection < hour_of_sunrise || this.adjusted_hour_of_disconnection > hour_of_latest_reconnection) {
         this.adjusted_hour_of_disconnection = hour_of_latest_reconnection;
      }

      int ticks_until_midnight = 24000 - world.getAdjustedTimeOfDay();
      int ticks_to_wait = ticks_until_midnight + this.adjusted_hour_of_disconnection * 1000;
      this.soonest_reconnection_tick = this.tick_of_disconnection + (long)ticks_to_wait;
   }
}
