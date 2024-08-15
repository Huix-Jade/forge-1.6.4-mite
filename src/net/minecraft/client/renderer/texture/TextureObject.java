package net.minecraft.client.renderer.texture;

import net.minecraft.client.resources.ResourceManager;

import java.io.IOException;

public interface TextureObject {
   void loadTexture(ResourceManager var1) throws IOException;

   int getGlTextureId();
}
