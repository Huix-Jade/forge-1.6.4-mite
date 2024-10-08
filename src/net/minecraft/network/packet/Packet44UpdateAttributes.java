package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class Packet44UpdateAttributes extends Packet {
   private int field_111005_a;
   private final List field_111004_b = new ArrayList();

   public Packet44UpdateAttributes() {
   }

   public Packet44UpdateAttributes(int var1, Collection var2) {
      this.field_111005_a = var1;
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         AttributeInstance var4 = (AttributeInstance)var3.next();
         this.field_111004_b.add(new Packet44UpdateAttributesSnapshot(this, var4.func_111123_a().getAttributeUnlocalizedName(), var4.getBaseValue(), var4.func_111122_c()));
      }

   }

   public void readPacketData(DataInput var1) throws IOException {
      this.field_111005_a = var1.readInt();
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = readString(var1, 64);
         double var5 = var1.readDouble();
         ArrayList var7 = new ArrayList();
         short var8 = var1.readShort();

         for(int var9 = 0; var9 < var8; ++var9) {
            UUID var10 = new UUID(var1.readLong(), var1.readLong());
            var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", var1.readDouble(), var1.readByte()));
         }

         this.field_111004_b.add(new Packet44UpdateAttributesSnapshot(this, var4, var5, var7));
      }

   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.field_111005_a);
      var1.writeInt(this.field_111004_b.size());
      Iterator var2 = this.field_111004_b.iterator();

      while(var2.hasNext()) {
         Packet44UpdateAttributesSnapshot var3 = (Packet44UpdateAttributesSnapshot)var2.next();
         writeString(var3.func_142040_a(), var1);
         var1.writeDouble(var3.func_142041_b());
         var1.writeShort(var3.func_142039_c().size());
         Iterator var4 = var3.func_142039_c().iterator();

         while(var4.hasNext()) {
            AttributeModifier var5 = (AttributeModifier)var4.next();
            var1.writeLong(var5.getID().getMostSignificantBits());
            var1.writeLong(var5.getID().getLeastSignificantBits());
            var1.writeDouble(var5.getAmount());
            var1.writeByte(var5.getOperation());
         }
      }

   }

   public void processPacket(NetHandler var1) {
      var1.func_110773_a(this);
   }

   public int getPacketSize() {
      return 8 + this.field_111004_b.size() * 24;
   }

   public int func_111002_d() {
      return this.field_111005_a;
   }

   public List func_111003_f() {
      return this.field_111004_b;
   }
}
