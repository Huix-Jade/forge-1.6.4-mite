//package net.minecraft.network.packet;
//
//import java.io.DataInput;
//import java.io.DataOutput;
//
//public class Packet7UseEntity extends Packet {
//   public int playerEntityId;
//   public int targetEntity;
//   public int isLeftClick;
//
//   public Packet7UseEntity() {
//   }
//
//   public Packet7UseEntity(int var1, int var2, int var3) {
//      this.playerEntityId = var1;
//      this.targetEntity = var2;
//      this.isLeftClick = var3;
//   }
//
//   public void readPacketData(DataInput var1) {
//      this.playerEntityId = var1.readInt();
//      this.targetEntity = var1.readInt();
//      this.isLeftClick = var1.readByte();
//   }
//
//   public void writePacketData(DataOutput var1) {
//      var1.writeInt(this.playerEntityId);
//      var1.writeInt(this.targetEntity);
//      var1.writeByte(this.isLeftClick);
//   }
//
//   public void processPacket(NetHandler var1) {
//      var1.a(this);
//   }
//
//   public int getPacketSize() {
//      return 9;
//   }
//}
