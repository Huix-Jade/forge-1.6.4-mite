package net.minecraft.block.material;

final class MaterialWeb extends Material {
   MaterialWeb(MapColor par1MapColor) {
      super("web", par1MapColor);
      this.setHarmedByPepsin();
   }
}
