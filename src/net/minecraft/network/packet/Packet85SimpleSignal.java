package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.ISignalSubtype;
import net.minecraft.network.SignalData;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.SpatialScaler;

public class Packet85SimpleSignal extends Packet {
   public EnumSignal signal_type;
   public ISignalSubtype signal_subtype;
   private SignalData signal_data;

   public Packet85SimpleSignal() {
      this((EnumSignal)null);
   }

   public Packet85SimpleSignal(EnumSignal signal_type) {
      this(signal_type, (ISignalSubtype)null);
   }

   public Packet85SimpleSignal(EnumSignal signal_type, ISignalSubtype signal_subtype) {
      this.signal_type = signal_type;
      this.signal_subtype = signal_subtype;
      this.signal_data = new SignalData();
   }

   public boolean hasSubtype() {
      return this.signal_type.hasSubtype();
   }

   private void setSubtype(byte subtype_ordinal) {
      this.signal_subtype = this.signal_type.getSubtype(subtype_ordinal);
   }

   private boolean hasDataType(byte data_type) {
      return this.signal_type.hasDataType(data_type, this.signal_subtype);
   }

   private boolean hasBooleanOrByte() {
      return this.hasDataType((byte)1);
   }

   private boolean hasShort() {
      return this.hasDataType((byte)2);
   }

   private boolean hasInteger() {
      return this.hasDataType((byte)4);
   }

   private boolean hasEntityID() {
      return this.hasDataType((byte)8);
   }

   private boolean hasFloat() {
      return this.hasDataType((byte)16);
   }

   private boolean hasBlockCoords() {
      return this.hasDataType((byte)32);
   }

   private boolean hasApproxPosition() {
      return this.hasDataType((byte)64);
   }

   private boolean hasExactPosition() {
      return this.hasDataType((byte)-128);
   }

   public void readPacketData(DataInput data_input) throws IOException {
      this.signal_type = EnumSignal.get(data_input.readUnsignedByte());
      if (this.hasSubtype()) {
         this.setSubtype(data_input.readByte());
      }

      if (this.hasBooleanOrByte()) {
         this.setByte(data_input.readByte());
      }

      if (this.hasShort()) {
         this.setShort(data_input.readShort());
      }

      if (this.hasInteger()) {
         this.setInteger(data_input.readInt());
      }

      if (this.hasEntityID()) {
         this.setEntityID(data_input.readInt());
      }

      if (this.hasFloat()) {
         this.setFloat(data_input.readFloat());
      }

      if (this.hasBlockCoords()) {
         this.setBlockCoords(data_input.readInt(), data_input.readShort(), data_input.readInt());
      }

      if (this.hasApproxPosition()) {
         this.signal_data.setScaledPosition(data_input.readInt(), data_input.readShort(), data_input.readInt());
      }

      if (this.hasExactPosition()) {
         this.setExactPosition(data_input.readDouble(), data_input.readDouble(), data_input.readDouble());
      }

   }

   public void writePacketData(DataOutput data_output) throws IOException {
      data_output.writeByte(this.signal_type.ordinal());
      if (this.hasSubtype()) {
         data_output.writeByte(this.signal_subtype.getOrdinal());
      }

      if (this.hasBooleanOrByte()) {
         if (!this.signal_data.isBooleanOrByteSet()) {
            Minecraft.setErrorMessage("writePacketData: boolean or byte data required but never set for " + this);
         }

         data_output.writeByte(this.getByte());
      }

      if (this.hasShort()) {
         if (!this.signal_data.isShortSet()) {
            Minecraft.setErrorMessage("writePacketData: short data required but never set for " + this);
         }

         data_output.writeShort(this.getShort());
      }

      if (this.hasInteger()) {
         if (!this.signal_data.isIntegerSet()) {
            Minecraft.setErrorMessage("writePacketData: integer data required but never set for " + this);
         }

         data_output.writeInt(this.getInteger());
      }

      if (this.hasEntityID()) {
         if (!this.signal_data.isEntityIDSet()) {
            Minecraft.setErrorMessage("writePacketData: entity ID required but never set for " + this);
         }

         data_output.writeInt(this.getEntityID());
      }

      if (this.hasFloat()) {
         if (!this.signal_data.isFloatSet()) {
            Minecraft.setErrorMessage("writePacketData: float data required but never set for " + this);
         }

         data_output.writeFloat(this.getFloat());
      }

      if (this.hasBlockCoords()) {
         if (!this.signal_data.isBlockCoordsSet()) {
            Minecraft.setErrorMessage("writePacketData: block coords required but never set for " + this);
         }

         data_output.writeInt(this.getBlockX());
         data_output.writeShort(this.getBlockY());
         data_output.writeInt(this.getBlockZ());
      }

      if (this.hasApproxPosition()) {
         if (!this.signal_data.isApproxPositionSet()) {
            Minecraft.setErrorMessage("writePacketData: approx position required but never set for " + this);
         }

         data_output.writeInt(this.signal_data.getScaledPosX());
         data_output.writeShort(this.signal_data.getScaledPosY());
         data_output.writeInt(this.signal_data.getScaledPosZ());
      }

      if (this.hasExactPosition()) {
         if (!this.signal_data.isExactPositionSet()) {
            Minecraft.setErrorMessage("writePacketData: exact position required but never set for " + this);
         }

         data_output.writeDouble(this.getExactPosX());
         data_output.writeDouble(this.getExactPosY());
         data_output.writeDouble(this.getExactPosZ());
      }

   }

   public void processPacket(NetHandler net_handler) {
      if (this.signal_type == EnumSignal.block_fx_compact) {
         this.signal_data.setBlockCoordsFromCompactedData();
         this.signal_type = EnumSignal.block_fx;
      }

      net_handler.handleSimpleSignal(this);
   }

   public int getPacketSize() {
      int size = 1;
      if (this.signal_type.hasSubtype()) {
         ++size;
      }

      if (this.hasBooleanOrByte()) {
         ++size;
      }

      if (this.hasShort()) {
         size += 2;
      }

      if (this.hasInteger()) {
         size += 4;
      }

      if (this.hasEntityID()) {
         size += 4;
      }

      if (this.hasFloat()) {
         size += 4;
      }

      if (this.hasBlockCoords()) {
         size += 10;
      }

      if (this.hasApproxPosition()) {
         size += 10;
      }

      if (this.hasExactPosition()) {
         size += 24;
      }

      return size;
   }

   public Packet85SimpleSignal setBoolean(boolean boolean_data) {
      if (!this.hasBooleanOrByte()) {
         Minecraft.setErrorMessage("setBoolean: data not part of " + this);
      } else if (this.signal_data.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("setBoolean: data already set for " + this);
      }

      this.signal_data.setBoolean(boolean_data);
      return this;
   }

   public Packet85SimpleSignal setByte(int byte_data) {
      if (!this.hasBooleanOrByte()) {
         Minecraft.setErrorMessage("setByte: data not part of " + this);
      } else if (this.signal_data.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("setByte: data already set for " + this);
      }

      if (byte_data < -128 || byte_data > 127) {
         Minecraft.setErrorMessage("setByte: data is out of range for " + this);
      }

      this.signal_data.setByte(byte_data);
      return this;
   }

   public Packet85SimpleSignal setShort(int short_data) {
      if (!this.hasShort()) {
         Minecraft.setErrorMessage("setShort: data not part of " + this);
      } else if (this.signal_data.isShortSet()) {
         Minecraft.setErrorMessage("setShort: data already set for " + this);
      }

      if (short_data < -32768 || short_data > 32767) {
         Minecraft.setErrorMessage("setShort: data is out of range for " + this);
      }

      this.signal_data.setShort(short_data);
      return this;
   }

   public Packet85SimpleSignal setInteger(int integer_data) {
      if (!this.hasInteger()) {
         Minecraft.setErrorMessage("setInteger: data not part of " + this);
      } else if (this.signal_data.isIntegerSet()) {
         Minecraft.setErrorMessage("setInteger: data already set for " + this);
      }

      this.signal_data.setInteger(integer_data);
      return this;
   }

   public Packet85SimpleSignal setEntityID(int entity_id) {
      if (!this.hasEntityID()) {
         Minecraft.setErrorMessage("setEntityID: data not part of " + this);
      } else if (this.signal_data.isEntityIDSet()) {
         Minecraft.setErrorMessage("setEntityID: data already set for " + this);
      }

      this.signal_data.setEntityID(entity_id);
      return this;
   }

   public Packet85SimpleSignal setEntityID(Entity entity) {
      return this.setEntityID(entity.entityId);
   }

   public Packet85SimpleSignal setFloat(float float_data) {
      if (!this.hasFloat()) {
         Minecraft.setErrorMessage("setFloat: data not part of " + this);
      } else if (this.signal_data.isFloatSet()) {
         Minecraft.setErrorMessage("setFloat: data already set for " + this);
      }

      this.signal_data.setFloat(float_data);
      return this;
   }

   public Packet85SimpleSignal setBlockCoords(int block_x, int block_y, int block_z) {
      if (!this.hasBlockCoords()) {
         Minecraft.setErrorMessage("setBlockCoords: data not part of " + this);
      }

      if (this.signal_type == EnumSignal.block_fx && SignalData.canBlockCoordsBeCompacted(block_x, block_y, block_z) && !this.hasShort() && !this.hasInteger()) {
         if (this.signal_data.isShortSet() || this.signal_data.isIntegerSet()) {
            Minecraft.setErrorMessage("setBlockCoords: compacted data already set for " + this);
         }

         this.signal_type = EnumSignal.block_fx_compact;
         this.signal_data.setBlockCoordsCompact(block_x, block_y, block_z);
         return this;
      } else {
         if (this.signal_data.isBlockCoordsSet()) {
            Minecraft.setErrorMessage("setBlockCoords: data already set for " + this);
         }

         this.signal_data.setBlockCoords(block_x, block_y, block_z);
         return this;
      }
   }

   public Packet85SimpleSignal setApproxPosition(double pos_x, double pos_y, double pos_z) {
      if (!this.hasApproxPosition()) {
         Minecraft.setErrorMessage("setApproxPosition: data not part of " + this);
      } else if (this.signal_data.isApproxPositionSet()) {
         Minecraft.setErrorMessage("setApproxPosition: data already set for " + this);
      }

      this.signal_data.setApproxPosition(pos_x, pos_y, pos_z);
      return this;
   }

   public Packet85SimpleSignal setExactPosition(double pos_x, double pos_y, double pos_z) {
      if (!this.hasExactPosition()) {
         Minecraft.setErrorMessage("setExactPosition: data not part of " + this);
      } else if (this.signal_data.isExactPositionSet()) {
         Minecraft.setErrorMessage("setExactPosition: data already set for " + this);
      }

      this.signal_data.setExactPosition(pos_x, pos_y, pos_z);
      return this;
   }

   public void addData(SignalData data) {
      if (data.isBooleanOrByteSet()) {
         this.setByte(data.getByte());
      }

      if (data.isShortSet()) {
         this.setShort(data.getShort());
      }

      if (data.isIntegerSet()) {
         this.setInteger(data.getInteger());
      }

      if (data.isEntityIDSet()) {
         this.setEntityID(data.getEntityID());
      }

      if (data.isFloatSet()) {
         this.setFloat(data.getFloat());
      }

      if (data.isBlockCoordsSet()) {
         this.setBlockCoords(data.getBlockX(), data.getBlockY(), data.getBlockZ());
      }

      if (data.isApproxPositionSet()) {
         this.signal_data.setScaledPosition(data.getScaledPosX(), data.getScaledPosY(), data.getScaledPosZ());
      }

      if (data.isExactPositionSet()) {
         this.setExactPosition(data.getExactPosX(), data.getExactPosY(), data.getExactPosZ());
      }

   }

   public boolean getBoolean() {
      if (!this.hasBooleanOrByte()) {
         Minecraft.setErrorMessage("getBoolean: data not part of " + this);
      } else if (!this.signal_data.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("getBoolean: boolean data required but not set for " + this);
      }

      return this.signal_data.getBoolean();
   }

   public byte getByte() {
      if (!this.hasBooleanOrByte()) {
         Minecraft.setErrorMessage("getByte: data not part of " + this);
      } else if (!this.signal_data.isBooleanOrByteSet()) {
         Minecraft.setErrorMessage("getByte: byte data required but not set for " + this);
      }

      return this.signal_data.getByte();
   }

   public short getShort() {
      if (!this.hasShort()) {
         Minecraft.setErrorMessage("getShort: data not part of " + this);
      } else if (!this.signal_data.isShortSet()) {
         Minecraft.setErrorMessage("getShort: short data required but not set for " + this);
      }

      return this.signal_data.getShort();
   }

   public int getInteger() {
      if (!this.hasInteger()) {
         Minecraft.setErrorMessage("getInteger: data not part of " + this);
      } else if (!this.signal_data.isIntegerSet()) {
         Minecraft.setErrorMessage("getInteger: integer data required but not set for " + this);
      }

      return this.signal_data.getInteger();
   }

   public int getEntityID() {
      if (!this.hasEntityID()) {
         Minecraft.setErrorMessage("getEntityID: data not part of " + this);
      } else if (!this.signal_data.isEntityIDSet()) {
         Minecraft.setErrorMessage("getEntityID: entity id data required but not set for " + this);
      }

      return this.signal_data.getEntityID();
   }

   public float getFloat() {
      if (!this.hasFloat()) {
         Minecraft.setErrorMessage("getFloat: data not part of " + this);
      } else if (!this.signal_data.isFloatSet()) {
         Minecraft.setErrorMessage("getFloat: float data required but not set for " + this);
      }

      return this.signal_data.getFloat();
   }

   public int getBlockX() {
      if (!this.hasBlockCoords()) {
         Minecraft.setErrorMessage("getBlockX: data not part of " + this);
      } else if (!this.signal_data.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("getBlockX: block coords data required but not set for " + this);
      }

      return this.signal_data.getBlockX();
   }

   public int getBlockY() {
      if (!this.hasBlockCoords()) {
         Minecraft.setErrorMessage("getBlockY: data not part of " + this);
      } else if (!this.signal_data.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("getBlockY: block coords data required but not set for " + this);
      }

      return this.signal_data.getBlockY();
   }

   public int getBlockZ() {
      if (!this.hasBlockCoords()) {
         Minecraft.setErrorMessage("getBlockZ: data not part of " + this);
      } else if (!this.signal_data.isBlockCoordsSet()) {
         Minecraft.setErrorMessage("getBlockZ: block coords data required but not set for " + this);
      }

      return this.signal_data.getBlockZ();
   }

   public double getApproxPosX() {
      if (!this.hasApproxPosition()) {
         Minecraft.setErrorMessage("getApproxPosX: data not part of " + this);
      } else if (!this.signal_data.isApproxPositionSet()) {
         Minecraft.setErrorMessage("getApproxPosX: approx positon data required but not set for " + this);
      }

      return SpatialScaler.getPosX(this.signal_data.getScaledPosX());
   }

   public double getApproxPosY() {
      if (!this.hasApproxPosition()) {
         Minecraft.setErrorMessage("getApproxPosY: data not part of " + this);
      } else if (!this.signal_data.isApproxPositionSet()) {
         Minecraft.setErrorMessage("getApproxPosY: approx positon data required but not set for " + this);
      }

      return SpatialScaler.getPosY(this.signal_data.getScaledPosY());
   }

   public double getApproxPosZ() {
      if (!this.hasApproxPosition()) {
         Minecraft.setErrorMessage("getApproxPosZ: data not part of " + this);
      } else if (!this.signal_data.isApproxPositionSet()) {
         Minecraft.setErrorMessage("getApproxPosZ: approx positon data required but not set for " + this);
      }

      return SpatialScaler.getPosZ(this.signal_data.getScaledPosZ());
   }

   public double getExactPosX() {
      if (!this.hasExactPosition()) {
         Minecraft.setErrorMessage("getExactPosX: data not part of " + this);
      } else if (!this.signal_data.isExactPositionSet()) {
         Minecraft.setErrorMessage("getExactPosX: exact positon data required but not set for " + this);
      }

      return this.signal_data.getExactPosX();
   }

   public double getExactPosY() {
      if (!this.hasExactPosition()) {
         Minecraft.setErrorMessage("getExactPosY: data not part of " + this);
      } else if (!this.signal_data.isExactPositionSet()) {
         Minecraft.setErrorMessage("getExactPosY: exact positon data required but not set for " + this);
      }

      return this.signal_data.getExactPosY();
   }

   public double getExactPosZ() {
      if (!this.hasExactPosition()) {
         Minecraft.setErrorMessage("getExactPosZ: data not part of " + this);
      } else if (!this.signal_data.isExactPositionSet()) {
         Minecraft.setErrorMessage("getExactPosZ: exact positon data required but not set for " + this);
      }

      return this.signal_data.getExactPosZ();
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(this.signal_type);
      if (this.hasSubtype()) {
         sb.append(":" + this.signal_subtype);
      }

      return sb.toString();
   }
}
