package net.minecraft.world;

import java.util.List;
import java.util.Random;
import net.minecraft.util.Debug;

public final class WeatherEvent {
   public int type;
   public long start;
   public int duration;
   public long end;
   public boolean removed;
   public long start_of_storm;
   public int duration_of_storm;
   public long end_of_storm;

   public WeatherEvent(long start, int duration) {
      this.start = start;
      this.duration = duration;
      this.end = start + (long)duration;
   }

   public void setStartAndEnd(long start, long end) {
      this.start = start;
      this.end = end;
      this.duration = (int)(end - start);
   }

   public void randomizeType() {
      if (this.hasStorm()) {
         this.type = 3;
      } else {
         Random random = new Random(this.start);
         random.nextInt();
         this.type = random.nextInt(3);
      }
   }

   public void setStorm(long start_of_storm, long end_of_storm) {
      this.start_of_storm = start_of_storm;
      this.end_of_storm = end_of_storm;
      this.duration_of_storm = (int)(end_of_storm - start_of_storm);
      if (this.hasStorm()) {
         this.type = 3;
      }

   }

   public void addStorm() {
      if (!this.hasStorm()) {
         Random random = new Random(this.start);
         random.nextInt();
         if (random.nextInt(4) <= 0) {
            this.duration_of_storm = Math.min(random.nextInt(4000) + 2000, this.duration);
            if (random.nextInt(4) == 0) {
               if (random.nextBoolean()) {
                  this.start_of_storm = this.start;
               } else {
                  this.start_of_storm = this.end - (long)this.duration_of_storm;
               }
            } else {
               this.start_of_storm = (long)random.nextInt(this.duration - this.duration_of_storm + 1) + this.start;
            }

            this.end_of_storm = this.start_of_storm + (long)this.duration_of_storm;
            this.type = 3;
         }
      }
   }

   public boolean hasStorm() {
      return this.start_of_storm > 0L;
   }

   public boolean isOccurringAt(long unadjusted_tick) {
      return this.start <= unadjusted_tick && this.end > unadjusted_tick;
   }

   public boolean isPrecipitatingAt(long unadjusted_tick) {
      return this.isOccurringAt(unadjusted_tick);
   }

   public boolean startsPrecipitating(long unadjusted_tick_from, long unadjusted_tick_to) {
      return this.start >= unadjusted_tick_from && this.start <= unadjusted_tick_to;
   }

   public boolean isStormingAt(long unadjusted_tick) {
      return this.hasStorm() && this.start_of_storm <= unadjusted_tick && this.end_of_storm > unadjusted_tick;
   }

   public boolean startsStorming(long unadjusted_tick_from, long unadjusted_tick_to) {
      return this.hasStorm() && this.start_of_storm >= unadjusted_tick_from && this.start_of_storm <= unadjusted_tick_to;
   }

   public static void printWeatherEvents(List list) {
      for(int i = 0; i < list.size(); ++i) {
         WeatherEvent event = (WeatherEvent)list.get(i);
         Debug.println("[" + i + "] " + event);
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("WeatherEvent on Day ");
      int start_day = World.getDayOfWorld(this.start);
      int end_day = World.getDayOfWorld(this.end);
      sb.append(start_day);
      if (end_day != start_day) {
         sb.append(" and " + end_day);
      }

      sb.append(": Rain from " + this.start + " to " + this.end + " (duration=" + this.duration + ")");
      if (this.hasStorm()) {
         sb.append(" and Storm from " + this.start_of_storm + " to " + this.end_of_storm + " (duration=" + this.duration_of_storm + ")");
      }

      sb.append(", Wind=" + this.type);
      return sb.toString();
   }
}
