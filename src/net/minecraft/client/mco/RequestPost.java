package net.minecraft.client.mco;

import java.io.OutputStream;

public class RequestPost extends Request {
   private byte[] field_96373_c;

   public RequestPost(String var1, byte[] var2, int var3, int var4) {
      super(var1, var3, var4);
      this.field_96373_c = var2;
   }

   public RequestPost func_96372_f() {
      try {
         this.field_96367_a.setDoInput(true);
         this.field_96367_a.setDoOutput(true);
         this.field_96367_a.setUseCaches(false);
         this.field_96367_a.setRequestMethod("POST");
         OutputStream var1 = this.field_96367_a.getOutputStream();
         var1.write(this.field_96373_c);
         var1.flush();
         return this;
      } catch (Exception var2) {
         throw new ExceptionMcoHttp("Failed URL: " + this.field_96365_b, var2);
      }
   }

   // $FF: synthetic method
   public Request func_96359_e() {
      return this.func_96372_f();
   }
}
