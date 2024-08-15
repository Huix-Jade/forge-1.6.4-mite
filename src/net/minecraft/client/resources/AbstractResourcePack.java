package net.minecraft.client.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.MetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.logging.ILogAgent;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public abstract class AbstractResourcePack implements ResourcePack {
   protected static final ILogAgent resourceLog = Minecraft.getMinecraft().getLogAgent();
   protected final File resourcePackFile;

   public AbstractResourcePack(File var1) {
      this.resourcePackFile = var1;
   }

   private static String locationToName(ResourceLocation var0) {
      return String.format("%s/%s/%s", "assets", var0.getResourceDomain(), var0.getResourcePath());
   }

   protected static String getRelativeName(File var0, File var1) {
      return var0.toURI().relativize(var1.toURI()).getPath();
   }

   public InputStream getInputStream(ResourceLocation var1) throws IOException {
      return this.getInputStreamByName(locationToName(var1));
   }

   public boolean resourceExists(ResourceLocation var1) {
      return this.hasResourceName(locationToName(var1));
   }

   protected abstract InputStream getInputStreamByName(String var1) throws IOException;

   protected abstract boolean hasResourceName(String var1);

   protected void logNameNotLowercase(String var1) {
      resourceLog.logWarningFormatted("ResourcePack: ignored non-lowercase namespace: %s in %s", var1, this.resourcePackFile);
   }

   public MetadataSection getPackMetadata(MetadataSerializer var1, String var2) throws IOException {
      return readMetadata(var1, this.getInputStreamByName("pack.mcmeta"), var2);
   }

   static MetadataSection readMetadata(MetadataSerializer var0, InputStream var1, String var2) {
      JsonObject var3 = null;
      BufferedReader var4 = null;

      try {
         var4 = new BufferedReader(new InputStreamReader(var1));
         var3 = (new JsonParser()).parse(var4).getAsJsonObject();
      } finally {
         IOUtils.closeQuietly(var4);
      }

      return var0.parseMetadataSection(var2, var3);
   }

   public BufferedImage getPackImage() throws IOException {
      return ImageIO.read(this.getInputStreamByName("pack.png"));
   }

   public String getPackName() {
      return this.resourcePackFile.getName();
   }
}
