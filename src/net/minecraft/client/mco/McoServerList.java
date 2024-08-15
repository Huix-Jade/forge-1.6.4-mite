package net.minecraft.client.mco;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class McoServerList {
   private volatile boolean field_98259_a;
   private McoServerListUpdateTask field_98257_b = new McoServerListUpdateTask(this, (McoServerListEmptyAnon)null);
   private Timer field_98258_c = new Timer();
   private Set field_140060_d = Sets.newHashSet();
   private List field_98255_d = Lists.newArrayList();
   private int field_130130_e;
   private boolean field_140059_g;
   private Session field_98254_f;
   private int field_140061_i;

   public McoServerList() {
      this.field_98258_c.schedule(this.field_98257_b, 0L, 10000L);
      this.field_98254_f = Minecraft.getMinecraft().getSession();
   }

   public synchronized void func_130129_a(Session var1) {
      this.field_98254_f = var1;
      if (this.field_98259_a) {
         this.field_98259_a = false;
         this.field_98257_b = new McoServerListUpdateTask(this, (McoServerListEmptyAnon)null);
         this.field_98258_c = new Timer();
         this.field_98258_c.schedule(this.field_98257_b, 0L, 10000L);
      }

   }

   public synchronized boolean func_130127_a() {
      return this.field_140059_g;
   }

   public synchronized void func_98250_b() {
      this.field_140059_g = false;
   }

   public synchronized List func_98252_c() {
      return Lists.newArrayList(this.field_98255_d);
   }

   public int func_130124_d() {
      return this.field_130130_e;
   }

   public int func_140056_e() {
      return this.field_140061_i;
   }

   public synchronized void func_98248_d() {
      this.field_98259_a = true;
      this.field_98257_b.cancel();
      this.field_98258_c.cancel();
   }

   private synchronized void func_96426_a(List var1) {
      int var2 = 0;
      Iterator var3 = this.field_140060_d.iterator();

      while(var3.hasNext()) {
         McoServer var4 = (McoServer)var3.next();
         if (var1.remove(var4)) {
            ++var2;
         }
      }

      if (var2 == 0) {
         this.field_140060_d.clear();
      }

      this.field_98255_d = var1;
      this.field_140059_g = true;
   }

   public synchronized void func_140058_a(McoServer var1) {
      this.field_98255_d.remove(var1);
      this.field_140060_d.add(var1);
   }

   private void func_130123_a(int var1) {
      this.field_130130_e = var1;
   }

   // $FF: synthetic method
   static boolean func_98249_b(McoServerList var0) {
      return var0.field_98259_a;
   }

   // $FF: synthetic method
   static Session func_100014_a(McoServerList var0) {
      return var0.field_98254_f;
   }

   // $FF: synthetic method
   static void func_98247_a(McoServerList var0, List var1) {
      var0.func_96426_a(var1);
   }

   // $FF: synthetic method
   static void func_130122_a(McoServerList var0, int var1) {
      var0.func_130123_a(var1);
   }

   // $FF: synthetic method
   static int func_140057_b(McoServerList var0, int var1) {
      return var0.field_140061_i = var1;
   }
}
