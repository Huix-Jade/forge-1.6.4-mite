package net.minecraft.client.mco;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.Session;

public class McoClient {
   private final String field_96390_a;
   private final String field_100007_c;
   private static String field_96388_b = "https://mcoapi.minecraft.net/";

   public McoClient(Session var1) {
      this.field_96390_a = var1.getSessionID();
      this.field_100007_c = var1.getUsername();
   }

   public ValueObjectList func_96382_a() throws ExceptionMcoService, IOException{
      String var1 = this.func_96377_a(Request.func_96358_a(field_96388_b + "worlds"));
      return ValueObjectList.func_98161_a(var1);
   }

   public McoServer func_98176_a(long var1) throws ExceptionMcoService, IOException {
      String var3 = this.func_96377_a(Request.func_96358_a(field_96388_b + "worlds" + "/$ID".replace("$ID", String.valueOf(var1))));
      return McoServer.func_98165_c(var3);
   }

   public McoServerAddress func_96374_a(long var1) throws ExceptionMcoService, IOException {
      String var3 = field_96388_b + "worlds" + "/$ID/join".replace("$ID", "" + var1);
      String var4 = this.func_96377_a(Request.func_96358_a(var3));
      return McoServerAddress.func_98162_a(var4);
   }

   public void func_96386_a(String var1, String var2, String var3, String var4) throws UnsupportedEncodingException, ExceptionMcoService {
      StringBuilder var5 = new StringBuilder();
      var5.append(field_96388_b).append("worlds").append("/$NAME/$LOCATION_ID".replace("$NAME", this.func_96380_a(var1)));
      HashMap var6 = new HashMap();
      if (var2 != null && !var2.trim().equals("")) {
         var6.put("motd", var2);
      }

      if (var3 != null && !var3.equals("")) {
         var6.put("seed", var3);
      }

      var6.put("template", var4);
      if (!var6.isEmpty()) {
         boolean var7 = true;

         Map.Entry var9;
         for(Iterator var8 = var6.entrySet().iterator(); var8.hasNext(); var5.append((String)var9.getKey()).append("=").append(this.func_96380_a((String)var9.getValue()))) {
            var9 = (Map.Entry)var8.next();
            if (var7) {
               var5.append("?");
               var7 = false;
            } else {
               var5.append("&");
            }
         }
      }

      this.func_96377_a(Request.func_104064_a(var5.toString(), "", 5000, 30000));
   }

   public Boolean func_96375_b() throws ExceptionMcoService, IOException {
      String var1 = field_96388_b + "mco" + "/available";
      String var2 = this.func_96377_a(Request.func_96358_a(var1));
      return Boolean.valueOf(var2);
   }

   public Boolean func_140054_c() throws ExceptionMcoService, IOException {
      String var1 = field_96388_b + "mco" + "/client/outdated";
      String var2 = this.func_96377_a(Request.func_96358_a(var1));
      return Boolean.valueOf(var2);
   }

   public int func_96379_c() throws ExceptionMcoService {
      String var1 = field_96388_b + "payments" + "/unused";
      String var2 = this.func_96377_a(Request.func_96358_a(var1));
      return Integer.valueOf(var2);
   }

   public void func_96381_a(long var1, String var3) throws ExceptionMcoService {
      String var4 = field_96388_b + "invites" + "/$WORLD_ID/invite/$USER_NAME".replace("$WORLD_ID", String.valueOf(var1)).replace("$USER_NAME", var3);
      this.func_96377_a(Request.func_96355_b(var4));
   }

   public void func_140055_c(long var1) throws ExceptionMcoService {
      String var3 = field_96388_b + "invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1));
      this.func_96377_a(Request.func_96355_b(var3));
   }

   public McoServer func_96387_b(long var1, String var3) throws ExceptionMcoService, IOException {
      String var4 = field_96388_b + "invites" + "/$WORLD_ID/invite/$USER_NAME".replace("$WORLD_ID", String.valueOf(var1)).replace("$USER_NAME", var3);
      String var5 = this.func_96377_a(Request.func_96361_b(var4, ""));
      return McoServer.func_98165_c(var5);
   }

   public BackupList func_111232_c(long var1) throws ExceptionMcoService {
      String var3 = field_96388_b + "worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(var1));
      String var4 = this.func_96377_a(Request.func_96358_a(var3));
      return BackupList.func_111222_a(var4);
   }

   public void func_96384_a(long var1, String var3, String var4, int var5, int var6) throws UnsupportedEncodingException, ExceptionMcoService {
      StringBuilder var7 = new StringBuilder();
      var7.append(field_96388_b).append("worlds").append("/$WORLD_ID/$NAME".replace("$WORLD_ID", String.valueOf(var1)).replace("$NAME", this.func_96380_a(var3)));
      if (var4 != null && !var4.trim().equals("")) {
         var7.append("?motd=").append(this.func_96380_a(var4));
      } else {
         var7.append("?motd=");
      }

      var7.append("&difficulty=").append(var5).append("&gameMode=").append(var6);
      this.func_96377_a(Request.func_96363_c(var7.toString(), ""));
   }

   public void func_111235_c(long var1, String var3) throws ExceptionMcoService {
      String var4 = field_96388_b + "worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(var1)) + "?backupId=" + var3;
      this.func_96377_a(Request.func_96363_c(var4, ""));
   }

   public WorldTemplateList func_111231_d() throws ExceptionMcoService {
      String var1 = field_96388_b + "worlds" + "/templates";
      String var2 = this.func_96377_a(Request.func_96358_a(var1));
      return WorldTemplateList.func_110735_a(var2);
   }

   public Boolean func_96383_b(long var1) throws ExceptionMcoService, IOException {
      String var3 = field_96388_b + "worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(var1));
      String var4 = this.func_96377_a(Request.func_96363_c(var3, ""));
      return Boolean.valueOf(var4);
   }

   public Boolean func_96378_c(long var1) throws ExceptionMcoService, IOException {
      String var3 = field_96388_b + "worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(var1));
      String var4 = this.func_96377_a(Request.func_96363_c(var3, ""));
      return Boolean.valueOf(var4);
   }

   public Boolean func_96376_d(long var1, String var3) throws UnsupportedEncodingException, ExceptionMcoService {
      StringBuilder var4 = new StringBuilder();
      var4.append(field_96388_b).append("worlds").append("/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      if (var3 != null && var3.length() > 0) {
         var4.append("?seed=").append(this.func_96380_a(var3));
      }

      String var5 = this.func_96377_a(Request.func_96353_a(var4.toString(), "", 30000, 80000));
      return Boolean.valueOf(var5);
   }

   public Boolean func_111233_e(long var1, String var3) throws ExceptionMcoService {
      StringBuilder var4 = new StringBuilder();
      var4.append(field_96388_b).append("worlds").append("/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      if (var3 != null) {
         var4.append("?template=").append(var3);
      }

      String var5 = this.func_96377_a(Request.func_96353_a(var4.toString(), "", 30000, 80000));
      return Boolean.valueOf(var5);
   }

   public ValueObjectSubscription func_98177_f(long var1) throws IOException, ExceptionMcoService {
      String var3 = this.func_96377_a(Request.func_96358_a(field_96388_b + "subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1))));
      return ValueObjectSubscription.func_98169_a(var3);
   }

   public int func_130106_e() throws ExceptionMcoService {
      String var1 = this.func_96377_a(Request.func_96358_a(field_96388_b + "invites" + "/count/pending"));
      return Integer.parseInt(var1);
   }

   public PendingInvitesList func_130108_f() throws ExceptionMcoService {
      String var1 = this.func_96377_a(Request.func_96358_a(field_96388_b + "invites" + "/pending"));
      return PendingInvitesList.func_130095_a(var1);
   }

   public void func_130107_a(String var1) throws ExceptionMcoService {
      this.func_96377_a(Request.func_96363_c(field_96388_b + "invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", var1), ""));
   }

   public void func_130109_b(String var1) throws ExceptionMcoService {
      this.func_96377_a(Request.func_96363_c(field_96388_b + "invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", var1), ""));
   }

   private String func_96380_a(String var1) throws UnsupportedEncodingException {
      return URLEncoder.encode(var1, "UTF-8");
   }

   private String func_96377_a(Request var1) throws ExceptionMcoService{
      var1.func_100006_a("sid", this.field_96390_a);
      var1.func_100006_a("user", this.field_100007_c);
      var1.func_100006_a("version", "1.6.4");

      try {
         int var2 = var1.func_96362_a();
         if (var2 == 503) {
            int var3 = var1.func_111221_b();
            throw new ExceptionRetryCall(var3);
         } else if (var2 >= 200 && var2 < 300) {
            return var1.func_96364_c();
         } else {
            throw new ExceptionMcoService(var1.func_96362_a(), var1.func_96364_c(), var1.func_130110_g());
         }
      } catch (ExceptionMcoHttp var4) {
         throw new ExceptionMcoService(500, "Server not available!", -1);
      }
   }
}
