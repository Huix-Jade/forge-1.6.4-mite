package net.minecraft.world;

import java.util.Comparator;

public class WeatherEventComparator implements Comparator {
   public int compareWeatherEvents(WeatherEvent a, WeatherEvent b) {
      return a.start < b.start ? -1 : (a.start > b.start ? 1 : (a.end < b.end ? -1 : (a.end > b.end ? 1 : 0)));
   }

   public int compare(Object a, Object b) {
      return this.compareWeatherEvents((WeatherEvent)a, (WeatherEvent)b);
   }
}
