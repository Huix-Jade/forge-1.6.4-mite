package net.minecraft.client.renderer;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class OpenGlHelper {
   public static int defaultTexUnit;
   public static int lightmapTexUnit;
   private static boolean useMultitextureARB;
   private static int current_lightmap_texture_coords_param_1;
   private static float current_lightmap_texture_coords_param_2;
   private static float current_lightmap_texture_coords_param_3;
   private static int previous_lightmap_texture_coords_param_1;
   private static float previous_lightmap_texture_coords_param_2;
   private static float previous_lightmap_texture_coords_param_3;

   public static void initializeTextures() {
      useMultitextureARB = GLContext.getCapabilities().GL_ARB_multitexture && !GLContext.getCapabilities().OpenGL13;
      if (useMultitextureARB) {
         defaultTexUnit = 33984;
         lightmapTexUnit = 33985;
      } else {
         defaultTexUnit = 33984;
         lightmapTexUnit = 33985;
      }

   }

   public static void setActiveTexture(int par0) {
      if (useMultitextureARB) {
         ARBMultitexture.glActiveTextureARB(par0);
      } else {
         GL13.glActiveTexture(par0);
      }

   }

   public static void setClientActiveTexture(int par0) {
      if (useMultitextureARB) {
         ARBMultitexture.glClientActiveTextureARB(par0);
      } else {
         GL13.glClientActiveTexture(par0);
      }

   }

   public static void setLightmapTextureCoords(int par0, float par1, float par2) {
      if (useMultitextureARB) {
         ARBMultitexture.glMultiTexCoord2fARB(par0, par1, par2);
      } else {
         GL13.glMultiTexCoord2f(par0, par1, par2);
      }

      previous_lightmap_texture_coords_param_1 = current_lightmap_texture_coords_param_1;
      previous_lightmap_texture_coords_param_2 = current_lightmap_texture_coords_param_2;
      previous_lightmap_texture_coords_param_3 = current_lightmap_texture_coords_param_3;
      current_lightmap_texture_coords_param_1 = par0;
      current_lightmap_texture_coords_param_2 = par1;
      current_lightmap_texture_coords_param_3 = par2;
   }

   public static void restorePreviousLightmapTextureCoords() {
      setLightmapTextureCoords(previous_lightmap_texture_coords_param_1, previous_lightmap_texture_coords_param_2, previous_lightmap_texture_coords_param_3);
   }
}
