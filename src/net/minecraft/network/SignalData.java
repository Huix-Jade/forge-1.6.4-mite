package net.minecraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SpatialScaler;

public class SignalData {
   public static final byte NONE = 0;
   public static final byte BOOLEAN_OR_BYTE = 1;
   public static final byte SHORT = 2;
   public static final byte INTEGER = 4;
   public static final byte ENTITY_ID = 8;
   public static final byte FLOAT = 16;
   public static final byte BLOCK_COORDS = 32;
   public static final byte APPROX_POSITION = 64;
   public static final byte EXACT_POSITION = -128;
   private byte byte_data;
   private short short_data;
   private int integer_data;
   private int entity_id;
   private float float_data;
   private int block_x;
   private int block_y;
   private int block_z;
   private int scaled_pos_x;
   private int scaled_pos_y;
   private int scaled_pos_z;
   private double pos_x;
   private double pos_y;
   private double pos_z;
   private byte data_types_set;
   private static final int bits_for_compact_xz = 19;
   private static final int bits_for_compact_y = 9;
   private static final int bits_for_compact_block_coords = 47;
   private static final int largest_positive_compact_xz = 262143;
   private static final int largest_positive_compact_y = 511;

   public SignalData setBoolean(boolean boolean_data) {
      if (this.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("setBoolean: data already set");
      }

      this.byte_data = (byte)(boolean_data ? -1 : 0);
      this.data_types_set = (byte)(this.data_types_set | 1);
      return this;
   }

   public SignalData setByte(int byte_data) {
      if (this.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("setByte: data already set");
      } else if (byte_data < -128 || byte_data > 127) {
         Minecraft.setErrorMessage("setByte: byte data is out of range (" + byte_data + ")");
      }

      this.byte_data = (byte)byte_data;
      this.data_types_set = (byte)(this.data_types_set | 1);
      return this;
   }

   public SignalData setShort(int short_data) {
      if (this.isShortSet()) {
         Minecraft.setErrorMessage("setShort: data already set");
      } else if (short_data < -32768 || short_data > 32767) {
         Minecraft.setErrorMessage("setShort: short data is out of range (" + short_data + ")");
      }

      this.short_data = (short)short_data;
      this.data_types_set = (byte)(this.data_types_set | 2);
      return this;
   }

   public SignalData setInteger(int integer_data) {
      if (this.isIntegerSet()) {
         Minecraft.setErrorMessage("setInteger: data already set");
      }

      this.integer_data = integer_data;
      this.data_types_set = (byte)(this.data_types_set | 4);
      return this;
   }

   public SignalData setEntityID(int entity_id) {
      if (this.isEntityIDSet()) {
         Minecraft.setErrorMessage("setEntityID: data already set");
      }

      this.entity_id = entity_id;
      this.data_types_set = (byte)(this.data_types_set | 8);
      return this;
   }

   public SignalData setFloat(float float_data) {
      if (this.isFloatSet()) {
         Minecraft.setErrorMessage("setFloat: data already set");
      }

      this.float_data = float_data;
      this.data_types_set = (byte)(this.data_types_set | 16);
      return this;
   }

   public SignalData setBlockCoords(int block_x, int block_y, int block_z) {
      if (this.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("setBlockCoords: data already set");
      }

      this.block_x = block_x;
      this.block_y = block_y;
      this.block_z = block_z;
      this.data_types_set = (byte)(this.data_types_set | 32);
      return this;
   }

   public void setBlockCoordsFromCompactedData() {
      this.setBlockCoords(this.getBlockXFromCompactedCoords(), this.getBlockYFromCompactedCoords(), this.getBlockZFromCompactedCoords());
   }

   public static boolean canBlockCoordsBeCompacted(int block_x, int block_y, int block_z) {
      return Math.abs(block_x) <= 262143 && block_y <= 511 && Math.abs(block_z) <= 262143;
   }

   public void setBlockCoordsCompact(int block_x, int block_y, int block_z) {
      int sign_bit_x = block_x < 0 ? 1 : 0;
      int sign_bit_z = block_z < 0 ? 1 : 0;
      block_x = Math.abs(block_x);
      block_z = Math.abs(block_z);
      long long_data = 0L;
      long_data |= (long)block_x;
      long_data |= (long)block_y << 18;
      long_data |= (long)block_z << 27;
      long_data |= (long)sign_bit_x << 45;
      long_data |= (long)sign_bit_z << 46;
      short short_data = 0;
      int integer_data = 0;
      short_data = (short)((int)((long)short_data | long_data));
      integer_data = (int)((long)integer_data | long_data >> 16);
      this.setShort(short_data);
      this.setInteger(integer_data);
   }

   public SignalData setApproxPosition(double pos_x, double pos_y, double pos_z) {
      return this.setScaledPosition(SpatialScaler.getScaledPosX(pos_x), SpatialScaler.getScaledPosY(pos_y), SpatialScaler.getScaledPosZ(pos_z));
   }

   public SignalData setScaledPosition(int scaled_pos_x, int scaled_pos_y, int scaled_pos_z) {
      if (this.isApproxPositionSet()) {
         Minecraft.setErrorMessage("setScaledPosition: data already set");
      }

      this.scaled_pos_x = scaled_pos_x;
      this.scaled_pos_y = scaled_pos_y;
      this.scaled_pos_z = scaled_pos_z;
      this.data_types_set = (byte)(this.data_types_set | 64);
      return this;
   }

   public SignalData setExactPosition(double pos_x, double pos_y, double pos_z) {
      if (this.isExactPositionSet()) {
         Minecraft.setErrorMessage("setExactPosition: data already set");
      }

      this.pos_x = pos_x;
      this.pos_y = pos_y;
      this.pos_z = pos_z;
      this.data_types_set |= -128;
      return this;
   }

   boolean isDataTypeSet(byte data_type) {
      return (this.data_types_set | data_type) == this.data_types_set;
   }

   public boolean isBooleanOrByteSet() {
      return this.isDataTypeSet((byte)1);
   }

   public boolean isShortSet() {
      return this.isDataTypeSet((byte)2);
   }

   public boolean isIntegerSet() {
      return this.isDataTypeSet((byte)4);
   }

   public boolean isEntityIDSet() {
      return this.isDataTypeSet((byte)8);
   }

   public boolean isFloatSet() {
      return this.isDataTypeSet((byte)16);
   }

   public boolean isBlockCoordsSet() {
      return this.isDataTypeSet((byte)32);
   }

   public boolean isApproxPositionSet() {
      return this.isDataTypeSet((byte)64);
   }

   public boolean isExactPositionSet() {
      return this.isDataTypeSet((byte)-128);
   }

   public boolean getBoolean() {
      if (!this.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("getBoolean: boolean or byte has not been set");
      }

      return this.byte_data != 0;
   }

   public byte getByte() {
      if (!this.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("getByte: boolean or byte has not been set");
      }

      return this.byte_data;
   }

   public short getShort() {
      if (!this.isShortSet()) {
         Minecraft.setErrorMessage("getShort: short has not been set");
      }

      return this.short_data;
   }

   public int getInteger() {
      if (!this.isIntegerSet()) {
         Minecraft.setErrorMessage("getInteger: integer has not been set");
      }

      return this.integer_data;
   }

   public int getEntityID() {
      if (!this.isEntityIDSet()) {
         Minecraft.setErrorMessage("getEntityID: entity_id has not been set");
      }

      return this.entity_id;
   }

   public float getFloat() {
      if (!this.isFloatSet()) {
         Minecraft.setErrorMessage("getFloat: float has not been set");
      }

      return this.float_data;
   }

   public int getBlockX() {
      if (!this.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("getBlockX: block coords data has not been set");
      }

      return this.block_x;
   }

   public int getBlockY() {
      if (!this.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("getBlockY: block coords data has not been set");
      }

      return this.block_y;
   }

   public int getBlockZ() {
      if (!this.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("getBlockZ: block coords data has not been set");
      }

      return this.block_z;
   }

   private long getCompactedCoords() {
      return (long)(this.getShort() & '\uffff') | (long)this.getInteger() << 16;
   }

   private int getBlockXFromCompactedCoords() {
      long long_data = this.getCompactedCoords();
      int block_x = (int)(long_data & 262143L);
      if ((long_data >> 45 & 1L) == 1L) {
         block_x = -block_x;
      }

      return block_x;
   }

   private int getBlockYFromCompactedCoords() {
      long long_data = this.getCompactedCoords();
      int block_y = (int)(long_data >> 19 - 1 & 511L);
      return block_y;
   }

   private int getBlockZFromCompactedCoords() {
      long long_data = this.getCompactedCoords();
      int block_z = (int)(long_data >> 27 & 262143L);
      if ((long_data >> 46 & 1L) == 1L) {
         block_z = -block_z;
      }

      return block_z;
   }

   public int getScaledPosX() {
      if (!this.isApproxPositionSet()) {
         Minecraft.setErrorMessage("getScaledPosX: approx position has not been set");
      }

      return this.scaled_pos_x;
   }

   public int getScaledPosY() {
      if (!this.isApproxPositionSet()) {
         Minecraft.setErrorMessage("getScaledPosY: approx position has not been set");
      }

      return this.scaled_pos_y;
   }

   public int getScaledPosZ() {
      if (!this.isApproxPositionSet()) {
         Minecraft.setErrorMessage("getScaledPosZ: approx position has not been set");
      }

      return this.scaled_pos_z;
   }

   public double getExactPosX() {
      if (!this.isExactPositionSet()) {
         Minecraft.setErrorMessage("getExactPosX: exact position has not been set");
      }

      return this.pos_x;
   }

   public double getExactPosY() {
      if (!this.isExactPositionSet()) {
         Minecraft.setErrorMessage("getExactPosY: exact position has not been set");
      }

      return this.pos_y;
   }

   public double getExactPosZ() {
      if (!this.isExactPositionSet()) {
         Minecraft.setErrorMessage("getExactPosZ: exact position has not been set");
      }

      return this.pos_z;
   }
}
