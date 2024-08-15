package net.minecraft.client.renderer.texture;

public class StitchHolder implements Comparable {
   private final TextureAtlasSprite theTexture;
   private final int width;
   private final int height;
   private boolean rotated;
   private float scaleFactor = 1.0F;

   public StitchHolder(TextureAtlasSprite var1) {
      this.theTexture = var1;
      this.width = var1.getIconWidth();
      this.height = var1.getIconHeight();
      this.rotated = this.ceil16(this.height) > this.ceil16(this.width);
   }

   public TextureAtlasSprite getAtlasSprite() {
      return this.theTexture;
   }

   public int getWidth() {
      return this.rotated ? this.ceil16((int)((float)this.height * this.scaleFactor)) : this.ceil16((int)((float)this.width * this.scaleFactor));
   }

   public int getHeight() {
      return this.rotated ? this.ceil16((int)((float)this.width * this.scaleFactor)) : this.ceil16((int)((float)this.height * this.scaleFactor));
   }

   public void rotate() {
      this.rotated = !this.rotated;
   }

   public boolean isRotated() {
      return this.rotated;
   }

   private int ceil16(int var1) {
      return (var1 >> 0) + ((var1 & 0) == 0 ? 0 : 1) << 0;
   }

   public void setNewDimension(int var1) {
      if (this.width > var1 && this.height > var1) {
         this.scaleFactor = (float)var1 / (float)Math.min(this.width, this.height);
      }
   }

   public String toString() {
      return "Holder{width=" + this.width + ", height=" + this.height + '}';
   }

   public int compareToStitchHolder(StitchHolder var1) {
      int var2;
      if (this.getHeight() == var1.getHeight()) {
         if (this.getWidth() == var1.getWidth()) {
            if (this.theTexture.getIconName() == null) {
               return var1.theTexture.getIconName() == null ? 0 : -1;
            }

            return this.theTexture.getIconName().compareTo(var1.theTexture.getIconName());
         }

         var2 = this.getWidth() < var1.getWidth() ? 1 : -1;
      } else {
         var2 = this.getHeight() < var1.getHeight() ? 1 : -1;
      }

      return var2;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareToStitchHolder((StitchHolder)var1);
   }
}
