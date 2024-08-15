package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;

public class ServerData {
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int field_82821_f;
   public String gameVersion;
   public boolean field_78841_f;
   private boolean field_78842_g;
   private boolean acceptsTextures;
   private boolean hideAddress;
   public final boolean is_preset;
   public String start_date;
   public String description;
   public String website;
   public String image_url;
   public int theme_color;
   public float backdrop_opacity;

   public ServerData(String par1Str, String par2Str) {
      this(par1Str, par2Str, false);
   }

   public ServerData(String par1Str, String par2Str, boolean is_preset) {
      this.field_82821_f = 78;
      this.gameVersion = "1.6.4";
      this.field_78842_g = true;
      this.serverName = par1Str;
      this.serverIP = par2Str;
      this.is_preset = is_preset;
   }

   public NBTTagCompound getNBTCompound() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.setString("name", this.serverName);
      var1.setString("ip", this.serverIP);
      var1.setBoolean("hideAddress", this.hideAddress);
      if (!this.field_78842_g) {
         var1.setBoolean("acceptTextures", this.acceptsTextures);
      }

      return var1;
   }

   public void setAcceptsTextures(boolean par1) {
      this.acceptsTextures = par1;
      this.field_78842_g = false;
   }

   public boolean isHidingAddress() {
      return this.hideAddress;
   }

   public void setHideAddress(boolean par1) {
      this.hideAddress = par1;
   }

   public static ServerData getServerDataFromNBTCompound(NBTTagCompound par0NBTTagCompound) {
      ServerData var1 = new ServerData(par0NBTTagCompound.getString("name"), par0NBTTagCompound.getString("ip"));
      var1.hideAddress = par0NBTTagCompound.getBoolean("hideAddress");
      if (par0NBTTagCompound.hasKey("acceptTextures")) {
         var1.setAcceptsTextures(par0NBTTagCompound.getBoolean("acceptTextures"));
      }

      return var1;
   }

   public ServerData setInfo(String start_date, String description, String website, String image_url, int theme_color, float backdrop_opacity) {
      this.start_date = start_date;
      this.description = description;
      this.website = website;
      this.image_url = image_url;
      this.theme_color = theme_color;
      this.backdrop_opacity = backdrop_opacity;
      return this;
   }

   public boolean hasInfo() {
      if (this.start_date != null && !this.start_date.isEmpty()) {
         return true;
      } else if (this.description != null && !this.description.isEmpty()) {
         return true;
      } else {
         return this.image_url != null && !this.image_url.isEmpty();
      }
   }

   public boolean hasWebsite() {
      return this.website != null && !this.website.isEmpty();
   }
}
