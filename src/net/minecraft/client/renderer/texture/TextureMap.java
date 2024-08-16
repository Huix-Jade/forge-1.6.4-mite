package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class TextureMap extends AbstractTexture implements TickableTextureObject, IconRegister {
   public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png", false);
   public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png", false);
   private final List listAnimatedSprites = Lists.newArrayList();
   private final Map mapRegisteredSprites = Maps.newHashMap();
   private final Map mapUploadedSprites = Maps.newHashMap();
   private final int textureType;
   private final String basePath;
   private final TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno");

   public TextureMap(int par1, String par2Str) {
      this.textureType = par1;
      this.basePath = par2Str;
      this.registerIcons();
   }

   private void initMissingImage() {
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][]{TextureUtil.missingTextureData}));
      this.missingImage.setIconWidth(16);
      this.missingImage.setIconHeight(16);
   }

   public void loadTexture(ResourceManager par1ResourceManager) throws IOException {
      this.initMissingImage();
      this.loadTextureAtlas(par1ResourceManager);
   }

   public void loadTextureAtlas(ResourceManager par1ResourceManager) {
      registerIcons(); //Re-gather list of Icons, allows for addition/removal of blocks/items after this map was initially constructed.
      int var2 = Minecraft.getGLMaximumTextureSize();
      Stitcher var3 = new Stitcher(var2, var2, true);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      ForgeHooksClient.onTextureStitchedPre(this);
      Iterator var4 = this.mapRegisteredSprites.entrySet().iterator();

      TextureAtlasSprite var17;
      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         ResourceLocation var6 = new ResourceLocation((String)var5.getKey(), false);
         var17 = (TextureAtlasSprite)var5.getValue();
         ResourceLocation var8 = new ResourceLocation(var6.getResourceDomain(), String.format("%s/%s%s", this.basePath, var6.getResourcePath(), ".png"), false);

         try {
            if (!var17.load(par1ResourceManager, var8)) continue;
         } catch (RuntimeException var14) {
            RuntimeException var13 = var14;
            Minecraft.getMinecraft().getLogAgent().logSevere(String.format("Unable to parse animation metadata from %s: %s", var8, var13.getMessage()));
            continue;
         } catch (IOException var15) {
            String error_message = "Missing resource: " + var8.getResourcePath();
            Minecraft.getMinecraft().getLogAgent().logSevere(error_message);
            Minecraft.setErrorMessage(error_message, false);
            continue;
         }

         var3.addSprite(var17);
      }

      var3.addSprite(this.missingImage);

      try {
         var3.doStitch();
      } catch (StitcherException var13) {
         StitcherException var12 = var13;
         throw var12;
      }

      TextureUtil.allocateTexture(this.getGlTextureId(), var3.getCurrentWidth(), var3.getCurrentHeight());
      HashMap var15 = Maps.newHashMap(this.mapRegisteredSprites);
      Iterator var16 = var3.getStichSlots().iterator();

      while(var16.hasNext()) {
         var17 = (TextureAtlasSprite)var16.next();
         String var18 = var17.getIconName();
         var15.remove(var18);
         this.mapUploadedSprites.put(var18, var17);

         try {
            TextureUtil.uploadTextureSub(var17.getFrameTextureData(0), var17.getIconWidth(), var17.getIconHeight(), var17.getOriginX(), var17.getOriginY(), false, false);
         } catch (Throwable var12) {
            Throwable var11 = var12;
            CrashReport var9 = CrashReport.makeCrashReport(var11, "Stitching texture atlas");
            CrashReportCategory var10 = var9.makeCategory("Texture being stitched together");
            var10.addCrashSection("Atlas path", this.basePath);
            var10.addCrashSection("Sprite", var17);
            throw new ReportedException(var9);
         }

         if (var17.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(var17);
         } else {
            var17.clearFramesTextureData();
         }
      }

      var16 = var15.values().iterator();

      while(var16.hasNext()) {
         var17 = (TextureAtlasSprite)var16.next();
         var17.copyFrom(this.missingImage);
      }

      ForgeHooksClient.onTextureStitchedPost(this);
   }

   private void registerIcons() {
      this.mapRegisteredSprites.clear();
      int var2;
      int var3;
      if (this.textureType == 0) {
         Block[] var1 = Block.blocksList;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            Block var4 = var1[var3];
            if (var4 != null) {
               var4.registerIcons(this);
            }
         }

         Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
         RenderManager.instance.updateIcons(this);
      }

      Item[] var5 = Item.itemsList;
      var2 = var5.length;

      for(var3 = 0; var3 < var2; ++var3) {
         Item var6 = var5[var3];
         if (var6 != null && var6.getSpriteNumber() == this.textureType) {
            var6.registerIcons(this);
         }
      }

   }

   public TextureAtlasSprite getAtlasSprite(String par1Str) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.mapUploadedSprites.get(par1Str);
      if (var2 == null) {
         var2 = this.missingImage;
      }

      return var2;
   }

   public void updateAnimations() {
      TextureUtil.bindTexture(this.getGlTextureId());
      Iterator var1 = this.listAnimatedSprites.iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();
         var2.updateAnimation();
      }

   }

   public Icon registerIcon(String par1Str) {
      if (par1Str == null) {
         (new RuntimeException("Don't register null!")).printStackTrace();
         par1Str = "null"; //Don't allow things to actually register null..
      }

      Object var2 = (TextureAtlasSprite)this.mapRegisteredSprites.get(par1Str);
      if (var2 == null) {
         if (this.textureType == 1) {
            if ("clock".equals(par1Str)) {
               var2 = new TextureClock(par1Str);
            } else if ("compass".equals(par1Str)) {
               var2 = new TextureCompass(par1Str);
            } else {
               var2 = new TextureAtlasSprite(par1Str);
            }
         } else {
            var2 = new TextureAtlasSprite(par1Str);
         }

         this.mapRegisteredSprites.put(par1Str, var2);
      }

      return (Icon)var2;
   }

   public int getTextureType() {
      return this.textureType;
   }

   public void tick() {
      this.updateAnimations();
   }

   //===================================================================================================
   //                                           Forge Start
   //===================================================================================================
   /**
    * Grabs the registered entry for the specified name, returning null if there was not a entry.
    * Opposed to registerIcon, this will not instantiate the entry, useful to test if a mapping exists.
    *
    * @param name The name of the entry to find
    * @return The registered entry, null if nothing was registered.
    */
   public TextureAtlasSprite getTextureExtry(String name)
   {
      return (TextureAtlasSprite)mapRegisteredSprites.get(name);
   }

   /**
    * Adds a texture registry entry to this map for the specified name if one does not already exist.
    * Returns false if the map already contains a entry for the specified name.
    *
    * @param name Entry name
    * @param entry Entry instance
    * @return True if the entry was added to the map, false otherwise.
    */
   public boolean setTextureEntry(String name, TextureAtlasSprite entry)
   {
      if (!mapRegisteredSprites.containsKey(name))
      {
         mapRegisteredSprites.put(name, entry);
         return true;
      }
      return false;
   }
}
