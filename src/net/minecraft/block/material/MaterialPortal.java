package net.minecraft.block.material;

public class MaterialPortal extends Material {
   public MaterialPortal(MapColor par1MapColor) {
      super("portal", par1MapColor);
      this.setHarmedByAcid(false);
   }

   public boolean isSolid() {
      return false;
   }
}
