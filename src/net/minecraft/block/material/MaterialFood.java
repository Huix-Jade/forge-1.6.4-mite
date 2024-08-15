package net.minecraft.block.material;

public class MaterialFood extends Material {
   public MaterialFood(String name) {
      super(name);
      this.setFlammability(true, false, true);
      this.setEdible();
   }
}
