package net.minecraft.util;

import net.minecraft.network.ISignalSubtype;

public enum EnumEntityFX implements ISignalSubtype {
   steam_with_hiss,
   single_steam_particle_with_hiss,
   summoned,
   burned_up_in_lava,
   smoke,
   smoke_and_steam,
   smoke_and_steam_with_hiss,
   frags,
   curse_effect_learned,
   item_breaking(3),
   splash,
   heal,
   vampiric_gain,
   repair,
   item_vanish,
   crafting(2);

   private byte data_types;

   private EnumEntityFX(int signal_data_type) {
      this.data_types = (byte)signal_data_type;
   }

   private EnumEntityFX() {
      this(0);
   }

   static EnumEntityFX get(int ordinal) {
      return values()[ordinal];
   }

   public byte getDataTypes() {
      return this.data_types;
   }

   public int getOrdinal() {
      return this.ordinal();
   }
}
