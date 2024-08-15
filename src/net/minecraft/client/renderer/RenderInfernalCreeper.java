package net.minecraft.client.renderer;

import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.EntityInfernalCreeper;

public class RenderInfernalCreeper extends RenderCreeper {
   public RenderInfernalCreeper() {
      this.shadowSize *= this.scale = EntityInfernalCreeper.getScale();
   }

   public String getSubtypeName() {
      return "infernal_creeper";
   }
}
