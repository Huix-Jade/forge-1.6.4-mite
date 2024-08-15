package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public abstract class ModelBase {
   public float onGround;
   public boolean isRiding;
   public List boxList = new ArrayList();
   public boolean isChild = true;
   private Map modelTextureMap = new HashMap();
   public int textureWidth = 64;
   public int textureHeight = 32;

   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
   }

   public void setLivingAnimations(EntityLivingBase var1, float var2, float var3, float var4) {
   }

   public ModelRenderer getRandomModelBox(Random var1) {
      return (ModelRenderer)this.boxList.get(var1.nextInt(this.boxList.size()));
   }

   protected void setTextureOffset(String var1, int var2, int var3) {
      this.modelTextureMap.put(var1, new TextureOffset(var2, var3));
   }

   public TextureOffset getTextureOffset(String var1) {
      return (TextureOffset)this.modelTextureMap.get(var1);
   }
}
