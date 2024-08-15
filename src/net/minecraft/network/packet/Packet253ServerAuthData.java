package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.util.CryptManager;

public class Packet253ServerAuthData extends Packet {
   private String serverId;
   private PublicKey publicKey;
   private byte[] verifyToken = new byte[0];

   public Packet253ServerAuthData() {
   }

   public Packet253ServerAuthData(String var1, PublicKey var2, byte[] var3) {
      this.serverId = var1;
      this.publicKey = var2;
      this.verifyToken = var3;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.serverId = readString(var1, 20);
      this.publicKey = CryptManager.decodePublicKey(readBytesFromStream(var1));
      this.verifyToken = readBytesFromStream(var1);
   }

   public void writePacketData(DataOutput var1) throws IOException {
      writeString(this.serverId, var1);
      writeByteArray(var1, this.publicKey.getEncoded());
      writeByteArray(var1, this.verifyToken);
   }

   public void processPacket(NetHandler var1) {
      var1.handleServerAuthData(this);
   }

   public int getPacketSize() {
      return 2 + this.serverId.length() * 2 + 2 + this.publicKey.getEncoded().length + 2 + this.verifyToken.length;
   }

   public String getServerId() {
      return this.serverId;
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public byte[] getVerifyToken() {
      return this.verifyToken;
   }
}
