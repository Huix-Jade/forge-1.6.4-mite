package net.minecraft.block.material;

public class MaterialSoup extends Material {
   public MaterialSoup(String name) {
      super(name);
      this.setLiquid(true);
      this.setHarmedByAcid(true);
      this.setDrinkable();
   }
}
