package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.ChatMessageComponent;

public class Packet3Chat extends Packet {
   public String message;
   private boolean isServer;
   public boolean permission_override;

   public Packet3Chat() {
      this.isServer = true;
   }

   public Packet3Chat(ChatMessageComponent par1ChatMessageComponent) {
      this(par1ChatMessageComponent.toJson());
   }

   public Packet3Chat(ChatMessageComponent par1ChatMessageComponent, boolean par2) {
      this(par1ChatMessageComponent.toJson(), par2);
   }

   public Packet3Chat(String par1Str) {
      this(par1Str, true);
   }

   public Packet3Chat(String par1Str, boolean par2) {
      this.isServer = true;
      if (par1Str.length() > 32767) {
         par1Str = par1Str.substring(0, 32767);
      }

      this.message = par1Str;
      this.isServer = par2;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.permission_override = par1DataInput.readBoolean();
      this.message = readString(par1DataInput, 32767);
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeBoolean(this.permission_override);
      writeString(this.message, par1DataOutput);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleChat(this);
   }

   public int getPacketSize() {
      return 3 + this.message.length() * 2;
   }

   public boolean getIsServer() {
      return this.isServer;
   }

   public Packet3Chat setPermissionOverride() {
      this.permission_override = true;
      return this;
   }
}
