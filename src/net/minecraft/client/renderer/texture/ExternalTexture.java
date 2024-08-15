package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.ResourceManager;

public final class ExternalTexture extends AbstractTexture {
   private final File file;

   public ExternalTexture(File file) {
      this.file = file;
   }

   public void loadTexture(ResourceManager par1ResourceManager) throws IOException {
      InputStream var2 = null;

      try {
         FileInputStream fis = new FileInputStream(this.file);
         var2 = fis;
         BufferedImage var4 = ImageIO.read(var2);
         boolean var5 = false;
         boolean var6 = false;
         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), var4, var5, var6);
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

   }
}
