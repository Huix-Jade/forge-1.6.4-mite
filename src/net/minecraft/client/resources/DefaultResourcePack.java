package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.data.MetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class DefaultResourcePack implements ResourcePack {
   public static final Set defaultResourceDomains = ImmutableSet.of("minecraft");
   private final Map mapResourceFiles = Maps.newHashMap();
   private final File fileAssets;

   public DefaultResourcePack(File var1) {
      this.fileAssets = var1;
      this.readAssetsDir(this.fileAssets);
   }

   public InputStream getInputStream(ResourceLocation var1)  throws IOException {
      InputStream var2 = this.getResourceStream(var1);
      if (var2 != null) {
         return var2;
      } else {
         File var3 = (File)this.mapResourceFiles.get(var1.toString());
         if (var3 != null) {
            return new FileInputStream(var3);
         } else {
            throw new FileNotFoundException(var1.getResourcePath());
         }
      }
   }

   private InputStream getResourceStream(ResourceLocation var1) {
      return DefaultResourcePack.class.getResourceAsStream("/assets/minecraft/" + var1.getResourcePath());
   }

   public void addResourceFile(String var1, File var2) {
      this.mapResourceFiles.put((new ResourceLocation(var1)).toString(), var2);
   }

   public boolean resourceExists(ResourceLocation var1) {
      return this.getResourceStream(var1) != null || this.mapResourceFiles.containsKey(var1.toString());
   }

   public Set getResourceDomains() {
      return defaultResourceDomains;
   }

   public void readAssetsDir(File var1) {
      if (var1.isDirectory()) {
         File[] var2 = var1.listFiles();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            File var5 = var2[var4];
            this.readAssetsDir(var5);
         }
      } else {
         this.addResourceFile(AbstractResourcePack.getRelativeName(this.fileAssets, var1), var1);
      }

   }

   public MetadataSection getPackMetadata(MetadataSerializer var1, String var2) throws IOException {
      return AbstractResourcePack.readMetadata(var1, DefaultResourcePack.class.getResourceAsStream("/" + (new ResourceLocation("pack.mcmeta")).getResourcePath()), var2);
   }

   public BufferedImage getPackImage() throws IOException{
      return ImageIO.read(DefaultResourcePack.class.getResourceAsStream("/" + (new ResourceLocation("pack.png")).getResourcePath()));
   }

   public String getPackName() {
      return "Default";
   }
}
