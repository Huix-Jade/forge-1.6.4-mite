package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.ISignalSubtype;

public enum EnumSignal {
   boolean_test(1),
   byte_test(1),
   short_test(2),
   integer_test(4),
   float_test(16),
   complex_test(23),
   approx_pos_test(64),
   exact_pos_test(-128),
   achievement_unlocked(4),
   increment_stat_for_this_world_only(4),
   crafting_completed(4),
   slot_locked,
   unlock_slots,
   start_falling_asleep,
   start_waking_up,
   sleeping,
   fully_awake,
   stop_rain_and_thunder_immediately,
   send_nearby_chunk_report,
   clear_inventory,
   terraform,
   tournament_mode(1),
   reconnection_delay(7),
   save_world_maps,
   cpu_overburdened,
   runegate_start,
   runegate_execute,
   runegate_finished,
   curse_realized(1),
   cursed(1),
   curse_effect_learned,
   curse_lifted,
   damage_taken(2),
   block_fx(true, 32),
   entity_fx(true, 8),
   block_fx_compact(true, 6),
   transfered_to_world,
   change_world_time(5),
   after_respawn,
   take_screenshot_of_world_seed,
   drop_one_item(1),
   stopped_using_item,
   digging_block_start(33),
   digging_block_cancel(32),
   digging_block_complete(32),
   block_hit_fx(33),
   try_auto_switch_or_restock(3),
   try_auto_switch_or_restock_large_subtype(6),
   toggle_night_vision_override,
   update_minecart_fuel(12),
   confirm_or_cancel_item_in_use,
   left_click_entity(8),
   malnourished(4),
   tournament_score(5),
   prize_key_code(4),
   put_out_fire,
   item_in_use(12),
   nocked_arrow(10),
   mh(4),
   see,
   allotted_time(4),
   server_load(2),
   block_hit_sound(32),
   clear_tentative_bounding_box(32),
   dedicated_server,
   sync_pos(-128),
   arrow_hit_block(-120),
   fish_hook_in_entity(12),
   fireball_reversal(72),
   in_love(10),
   update_potion_effect(7),
   toggle_mute,
   tag_entity(9),
   skills(1),
   skillset(4),
   respawn_screen(2),
   loaded_tile_entities,
   vision_dimming_to_server(16),
   entity_stats_dump(8),
   last_issued_map_id(2),
   list_commands,
   delete_selection,
   furnace_heat_level(1),
   picked_up_held_item,
   teleport_away(8);

   private boolean has_subtype;
   private byte data_types;

   private EnumSignal(boolean has_subtypes, int data_types) {
      this.has_subtype = has_subtypes;
      this.data_types = (byte)data_types;
   }

   private EnumSignal(int data_types) {
      this(false, data_types);
   }

   private EnumSignal() {
      this(0);
   }

   public static EnumSignal get(int ordinal) {
      return values()[ordinal];
   }

   public boolean hasSubtype() {
      return this.has_subtype;
   }

   public ISignalSubtype getSubtype(byte sub_type_ordinal) {
      if (this != block_fx && this != block_fx_compact) {
         if (this == entity_fx) {
            return EnumEntityFX.get(sub_type_ordinal);
         } else {
            Minecraft.setErrorMessage("getSubtype: no handler for " + this);
            return null;
         }
      } else {
         return EnumBlockFX.get(sub_type_ordinal);
      }
   }

   byte getDataTypes() {
      return this.data_types;
   }

   public boolean hasDataType(byte data_type, ISignalSubtype signal_subtype) {
      byte data_types = this.data_types;
      if (this.hasSubtype()) {
         byte subsignal_data_types = signal_subtype.getDataTypes();
         if (data_types + subsignal_data_types != (data_types | subsignal_data_types)) {
            Minecraft.setErrorMessage("hasDataType: conflict between signal and sub signal data types");
         }

         data_types |= subsignal_data_types;
      }

      return (data_types | data_type) == data_types;
   }
}
