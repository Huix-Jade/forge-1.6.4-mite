package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class Packet203AutoComplete extends Packet {
   private String text;

   public Packet203AutoComplete() {
   }

   public Packet203AutoComplete(String var1) {
      this.text = var1;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.text = readString(var1, 32767);
   }

   public void writePacketData(DataOutput var1) throws IOException {
      writeString(StringUtils.substring(this.text, 0, 32767), var1);
   }

   public void processPacket(NetHandler var1) {
      var1.handleAutoComplete(this);
   }

   public int getPacketSize() {
      return 2 + this.text.length() * 2;
   }

   public String getText() {
      return this.text;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      return true;
   }
}
