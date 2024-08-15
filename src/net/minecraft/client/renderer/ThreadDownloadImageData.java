package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

public class ThreadDownloadImageData extends AbstractTexture {
   private final String imageUrl;
   private final IImageBuffer imageBuffer;
   private BufferedImage bufferedImage;
   private Thread imageThread;
   private SimpleTexture imageLocation;
   private boolean textureUploaded;

   public ThreadDownloadImageData(String var1, ResourceLocation var2, IImageBuffer var3) {
      this.imageUrl = var1;
      this.imageBuffer = var3;
      this.imageLocation = var2 != null ? new SimpleTexture(var2) : null;
   }

   public int getGlTextureId() {
      int var1 = super.getGlTextureId();
      if (!this.textureUploaded && this.bufferedImage != null) {
         TextureUtil.uploadTextureImage(var1, this.bufferedImage);
         this.textureUploaded = true;
      }

      return var1;
   }

   public void getBufferedImage(BufferedImage var1) {
      this.bufferedImage = var1;
   }

   public void loadTexture(ResourceManager var1) throws IOException {
      if (this.bufferedImage == null) {
         if (this.imageLocation != null) {
            this.imageLocation.loadTexture(var1);
            this.glTextureId = this.imageLocation.getGlTextureId();
         }
      } else {
         TextureUtil.uploadTextureImage(this.getGlTextureId(), this.bufferedImage);
      }

      if (this.imageThread == null) {
         this.imageThread = new ThreadDownloadImageDataINNER1(this);
         this.imageThread.setDaemon(true);
         this.imageThread.setName("Skin downloader: " + this.imageUrl);
         this.imageThread.start();
      }

   }

   public boolean isTextureUploaded() {
      this.getGlTextureId();
      return this.textureUploaded;
   }

   // $FF: synthetic method
   static String getImageUrl(ThreadDownloadImageData var0) {
      return var0.imageUrl;
   }

   // $FF: synthetic method
   static IImageBuffer getImageBuffer(ThreadDownloadImageData var0) {
      return var0.imageBuffer;
   }
}
