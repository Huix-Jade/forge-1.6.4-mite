package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelArachnid;

public class RenderDemonSpider extends RenderArachnid {
   public RenderDemonSpider() {
      super(new ModelArachnid(), new ModelArachnid(), 1.0F);
   }

   public String getSubtypeName() {
      return "demon_spider";
   }
}
