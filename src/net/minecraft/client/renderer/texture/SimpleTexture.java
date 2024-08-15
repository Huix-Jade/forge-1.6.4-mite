package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public final class SimpleTexture extends AbstractTexture {
   private final ResourceLocation textureLocation;

   public SimpleTexture(ResourceLocation par1ResourceLocation) {
      this.textureLocation = par1ResourceLocation;
   }

   private void rB(byte[] bytes) {
      for(int i = 0; i < bytes.length / 2; ++i) {
         byte temp = bytes[i];
         bytes[i] = bytes[bytes.length - 1 - i];
         bytes[bytes.length - 1 - i] = temp;
      }

   }

   public void loadTexture(ResourceManager par1ResourceManager) throws IOException {
      InputStream var2 = null;

      try {
         Resource var3 = par1ResourceManager.getResource(this.textureLocation);
         var2 = var3.getInputStream();
         if (this.textureLocation.generate_encoded_file && !Minecraft.inDevMode()) {
            this.textureLocation.generate_encoded_file = false;
            Minecraft.setErrorMessage("SimpleTexture: Error for " + this.textureLocation.getResourcePath());
         }

         byte[] bytes;
         if (this.textureLocation.getResourcePath().endsWith(".enc")) {
            bytes = IOUtils.toByteArray((InputStream)var2);
            this.rB(bytes);
            bytes[1] = 80;
            bytes[2] = 78;
            bytes[3] = 71;
            var2 = new ByteArrayInputStream(bytes);
         } else if (this.textureLocation.generate_encoded_file) {
            bytes = IOUtils.toByteArray((InputStream)var2);
            byte[] copy_of_bytes = new byte[bytes.length];

            for(int i = 0; i < bytes.length; ++i) {
               copy_of_bytes[i] = bytes[i];
            }

            copy_of_bytes[1] = 0;
            copy_of_bytes[2] = 0;
            copy_of_bytes[3] = 0;
            this.rB(copy_of_bytes);
            String resource_path = this.textureLocation.getResourcePath();
            if (resource_path.endsWith(".png")) {
               resource_path = resource_path.substring(0, resource_path.length() - 4);
            }

            String output_path = "resourcepacks/MITE Resource Pack 1.6.4/assets/minecraft/" + resource_path + ".enc";
            System.out.print("Attempting to create encoded file (" + output_path + ")...");

            try {
               FileOutputStream fos = new FileOutputStream(new File(output_path));
               fos.write(copy_of_bytes, 0, copy_of_bytes.length);
               fos.flush();
               fos.close();
               System.out.println("succeeded");
            } catch (Exception var14) {
               System.out.println("failed");
            }

            var2 = new ByteArrayInputStream(bytes);
         }

         BufferedImage var4 = ImageIO.read((InputStream)var2);
         boolean var5 = false;
         boolean var6 = false;
         if (var3.hasMetadata()) {
            try {
               TextureMetadataSection var7 = (TextureMetadataSection)var3.getMetadata("texture");
               if (var7 != null) {
                  var5 = var7.getTextureBlur();
                  var6 = var7.getTextureClamp();
               }
            } catch (RuntimeException var13) {
               Minecraft.getMinecraft().getLogAgent().logWarningException("Failed reading metadata of: " + this.textureLocation, var13);
            }
         }

         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), var4, var5, var6);
      } finally {
         if (var2 != null) {
            ((InputStream)var2).close();
         }

      }

   }
}
