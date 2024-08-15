package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.util.CryptManager;

public class Packet252SharedKey extends Packet {
   private byte[] sharedSecret = new byte[0];
   private byte[] verifyToken = new byte[0];
   private SecretKey sharedKey;

   public Packet252SharedKey() {
   }

   public Packet252SharedKey(SecretKey var1, PublicKey var2, byte[] var3) {
      this.sharedKey = var1;
      this.sharedSecret = CryptManager.encryptData(var2, var1.getEncoded());
      this.verifyToken = CryptManager.encryptData(var2, var3);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.sharedSecret = readBytesFromStream(var1);
      this.verifyToken = readBytesFromStream(var1);
   }

   public void writePacketData(DataOutput var1) throws IOException {
      writeByteArray(var1, this.sharedSecret);
      writeByteArray(var1, this.verifyToken);
   }

   public void processPacket(NetHandler var1) {
      var1.handleSharedKey(this);
   }

   public int getPacketSize() {
      return 2 + this.sharedSecret.length + 2 + this.verifyToken.length;
   }

   public SecretKey getSharedKey(PrivateKey var1) {
      return var1 == null ? this.sharedKey : (this.sharedKey = CryptManager.decryptSharedKey(var1, this.sharedSecret));
   }

   public SecretKey getSharedKey() {
      return this.getSharedKey((PrivateKey)null);
   }

   public byte[] getVerifyToken(PrivateKey var1) {
      return var1 == null ? this.verifyToken : CryptManager.decryptData(var1, this.verifyToken);
   }
}
