package net.minecraft.client.main;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class MainProxyAuthenticator extends Authenticator {
   // $FF: synthetic field
   final String field_111237_a;
   // $FF: synthetic field
   final String field_111236_b;

   public MainProxyAuthenticator(String var1, String var2) {
      this.field_111237_a = var1;
      this.field_111236_b = var2;
   }

   protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.field_111237_a, this.field_111236_b.toCharArray());
   }
}
