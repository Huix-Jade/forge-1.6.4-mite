package net.minecraft.util;

import net.minecraft.network.ISignalSubtype;

public enum EnumBlockFX implements ISignalSubtype {
   lava_mixing_with_water,
   water_evaporation_in_hell,
   steam,
   steam_particles_only,
   smoke_and_steam,
   manure,
   particle_trail(67),
   destroy(4),
   item_consumed_by_lava;

   private byte data_types;

   private EnumBlockFX(int signal_data_type) {
      this.data_types = (byte)signal_data_type;
   }

   private EnumBlockFX() {
      this(0);
   }

   static EnumBlockFX get(int ordinal) {
      return values()[ordinal];
   }

   public byte getDataTypes() {
      return this.data_types;
   }

   public int getOrdinal() {
      return this.ordinal();
   }
}
