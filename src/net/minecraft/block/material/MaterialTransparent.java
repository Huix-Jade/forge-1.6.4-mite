package net.minecraft.block.material;

public class MaterialTransparent extends Material {
   public MaterialTransparent(String name, MapColor par1MapColor) {
      super(name, par1MapColor);
      this.setReplaceable();
      this.setHarmedByAcid(false);
   }

   public boolean isSolid() {
      return false;
   }
}
