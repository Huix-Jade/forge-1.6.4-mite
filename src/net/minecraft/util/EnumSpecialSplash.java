package net.minecraft.util;

public enum EnumSpecialSplash {
   ronin_pawn("RoninPawn!", "textures/gui/title/ronin_pawn.png", "Check out the RoninPawn's MITE series on YouTube!", 70, "textures/gui/title/rp_videos.png", 1500, 720, 0.15F, "https://www.youtube.com/watch?v=UaSVsuklHjA"),
   mite_migos("MiTE 'Migos!", "textures/gui/title/mite_migos.png", "Visit the MiTE-Migos official forum thread and public fan server!", 70, "textures/gui/title/mite_migos.png", 1123, 256, 0.25F, "http://www.minecraftforum.net/forums/servers/pc-servers/survival-servers/2383945-mite-migos-fan-server-minecraft-is-too-easy"),
   guten_tag("Guten Tag!"),
   elite_dangerous("Elite: Dangerous!"),
   ice_cream("That ice cream though!"),
   cogmind("Also try Cogmind!", (String)null, "Plot Twist: You are the Cogmind!", 70, "textures/gui/title/cogmind.png", 666, 234, 0.3F, "http://www.gridsagegames.com/cogmind/"),
   ludwig("Ludwig DeLarge!", (String)null, "The Icy Desert Journey! This is how the way to MITE!", 76, "textures/gui/title/ludwig.png", 1280, 610, 0.18F, "http://imgur.com/a/YAzpR");

   private String splash_text;
   private ResourceLocation splash_texture;
   private String message_text;
   private ResourceLocation link_page_texture;
   private int width;
   private int height;
   private float scale;
   private int message_height;
   private String url;

   private EnumSpecialSplash(String splash_text, String splash_texture, String message_text, int message_height, String link_page_texture, int width, int height, float scale, String url) {
      this.splash_text = splash_text;
      this.splash_texture = splash_texture == null ? null : new ResourceLocation(splash_texture);
      this.message_text = message_text;
      this.message_height = message_height;
      this.link_page_texture = link_page_texture == null ? null : new ResourceLocation(link_page_texture);
      this.width = width;
      this.height = height;
      this.scale = scale;
      this.url = url;
   }

   private EnumSpecialSplash(String splash_text) {
      this(splash_text, (String)null, (String)null, 0, (String)null, 0, 0, 0.0F, (String)null);
   }

   public static EnumSpecialSplash getSpecialSplash(String splash_text) {
      for(int i = 0; i < values().length; ++i) {
         if (splash_text.equals(values()[i].splash_text)) {
            return values()[i];
         }
      }

      return null;
   }

   public ResourceLocation getSplashTexture() {
      return this.splash_texture;
   }

   public String getMessageText() {
      return this.message_text;
   }

   public int getMessageHeight() {
      return this.message_height;
   }

   public ResourceLocation getLinkPageTexture() {
      return this.link_page_texture;
   }

   public boolean hasLinkPageTexture() {
      return this.getLinkPageTexture() != null;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float getScale() {
      return this.scale;
   }

   public String getURL() {
      return this.url;
   }

   public boolean hasURL() {
      return this.getURL() != null;
   }
}
