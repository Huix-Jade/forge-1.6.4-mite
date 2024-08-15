package net.minecraft.block.material;

public class MaterialLiquid extends Material {
   public MaterialLiquid(String name, MapColor par1MapColor) {
      super(name, par1MapColor);
      this.setReplaceable();
      this.setNoPushMobility();
      this.setLiquid("water".equals(name));
   }

   public boolean isSolid() {
      return false;
   }
}
