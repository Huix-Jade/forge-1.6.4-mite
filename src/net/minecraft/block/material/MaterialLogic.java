package net.minecraft.block.material;

public class MaterialLogic extends Material {
   public MaterialLogic(String name, MapColor par1MapColor) {
      super(name, par1MapColor);
      this.setAdventureModeExempt();
   }

   public boolean isSolid() {
      return false;
   }
}
