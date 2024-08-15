package net.minecraft.server.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;

public class BanEntry {
   public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private final String username;
   private Date banStartDate = new Date();
   private String bannedBy = "(Unknown)";
   private Date banEndDate;
   private String reason = "Banned by an operator.";

   public BanEntry(String var1) {
      this.username = var1;
   }

   public String getBannedUsername() {
      return this.username;
   }

   public Date getBanStartDate() {
      return this.banStartDate;
   }

   public void setBanStartDate(Date var1) {
      this.banStartDate = var1 != null ? var1 : new Date();
   }

   public String getBannedBy() {
      return this.bannedBy;
   }

   public void setBannedBy(String var1) {
      this.bannedBy = var1;
   }

   public Date getBanEndDate() {
      return this.banEndDate;
   }

   public void setBanEndDate(Date var1) {
      this.banEndDate = var1;
   }

   public boolean hasBanExpired() {
      return this.banEndDate == null ? false : this.banEndDate.before(new Date());
   }

   public String getBanReason() {
      return this.reason;
   }

   public void setBanReason(String var1) {
      this.reason = var1;
   }

   public String buildBanString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getBannedUsername());
      var1.append("|");
      var1.append(dateFormat.format(this.getBanStartDate()));
      var1.append("|");
      var1.append(this.getBannedBy());
      var1.append("|");
      var1.append(this.getBanEndDate() == null ? "Forever" : dateFormat.format(this.getBanEndDate()));
      var1.append("|");
      var1.append(this.getBanReason());
      return var1.toString();
   }

   public static BanEntry parse(String var0) {
      if (var0.trim().length() < 2) {
         return null;
      } else {
         String[] var1 = var0.trim().split(Pattern.quote("|"), 5);
         BanEntry var2 = new BanEntry(var1[0].trim());
         int var3 = 0;
         int var10000 = var1.length;
         ++var3;
         if (var10000 <= var3) {
            return var2;
         } else {
            try {
               var2.setBanStartDate(dateFormat.parse(var1[var3].trim()));
            } catch (ParseException var6) {
               MinecraftServer.getServer().getLogAgent().logWarningException("Could not read creation date format for ban entry '" + var2.getBannedUsername() + "' (was: '" + var1[var3] + "')", var6);
            }

            var10000 = var1.length;
            ++var3;
            if (var10000 <= var3) {
               return var2;
            } else {
               var2.setBannedBy(var1[var3].trim());
               var10000 = var1.length;
               ++var3;
               if (var10000 <= var3) {
                  return var2;
               } else {
                  try {
                     String var4 = var1[var3].trim();
                     if (!var4.equalsIgnoreCase("Forever") && var4.length() > 0) {
                        var2.setBanEndDate(dateFormat.parse(var4));
                     }
                  } catch (ParseException var5) {
                     MinecraftServer.getServer().getLogAgent().logWarningException("Could not read expiry date format for ban entry '" + var2.getBannedUsername() + "' (was: '" + var1[var3] + "')", var5);
                  }

                  var10000 = var1.length;
                  ++var3;
                  if (var10000 <= var3) {
                     return var2;
                  } else {
                     var2.setBanReason(var1[var3].trim());
                     return var2;
                  }
               }
            }
         }
      }
   }
}
