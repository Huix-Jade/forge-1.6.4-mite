package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

public class LayeredTexture extends AbstractTexture {
   public final List layeredTextureNames;

   public LayeredTexture(String... var1) {
      this.layeredTextureNames = Lists.newArrayList(var1);
   }

   public void loadTexture(ResourceManager var1) {
      BufferedImage var2 = null;

      try {
         Iterator var3 = this.layeredTextureNames.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (var4 != null) {
               InputStream var5 = var1.getResource(new ResourceLocation(var4)).getInputStream();
               BufferedImage var6 = ImageIO.read(var5);
               if (var2 == null) {
                  var2 = new BufferedImage(var6.getWidth(), var6.getHeight(), 2);
               }

               var2.getGraphics().drawImage(var6, 0, 0, (ImageObserver)null);
            }
         }
      } catch (IOException var7) {
         var7.printStackTrace();
         return;
      }

      TextureUtil.uploadTextureImage(this.getGlTextureId(), var2);
   }
}
