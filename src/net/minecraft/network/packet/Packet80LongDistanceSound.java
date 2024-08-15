package net.minecraft.network.packet;

public class Packet80LongDistanceSound extends Packet62LevelSound {
   public Packet80LongDistanceSound() {
   }

   public Packet80LongDistanceSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {
      super(par1Str, par2, par4, par6, par8, par9);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleLongDistanceSound(this);
   }
}
