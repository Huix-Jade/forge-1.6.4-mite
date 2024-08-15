package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet2ClientProtocol extends Packet {
   private int protocolVersion;
   private String username;
   private String serverHost;
   private int serverPort;
   public String MC_version;
   public String MITE_release_number;
   private static final String delimiter = ":";
   private static final String delimiter_for_regexp = "\\:";

   public Packet2ClientProtocol() {
   }

   public Packet2ClientProtocol(int par1, String par2Str, String par3Str, int par4) {
      this.protocolVersion = par1;
      this.username = par2Str + ":" + "1.6.4" + ":" + "R" + 196;
      this.serverHost = par3Str;
      this.serverPort = par4;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.protocolVersion = par1DataInput.readByte();
      this.username = readString(par1DataInput, 32);
      this.serverHost = readString(par1DataInput, 255);
      this.serverPort = par1DataInput.readInt();
      String[] parts = this.username.split("\\:");
      if (parts.length > 1) {
         this.MC_version = parts[1];
      }

      if (parts.length > 2) {
         this.MITE_release_number = parts[2];
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.protocolVersion);
      writeString(this.username, par1DataOutput);
      writeString(this.serverHost, par1DataOutput);
      par1DataOutput.writeInt(this.serverPort);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleClientProtocol(this);
   }

   public int getPacketSize() {
      return 3 + 2 * this.username.length();
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getUsername() {
      return this.username.split("\\:")[0];
   }
}
