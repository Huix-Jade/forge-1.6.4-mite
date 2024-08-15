package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelInvisibleStalker;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.util.ResourceLocation;

public class RenderEarthElemental extends RenderBiped {
   public static final int texture_stone_normal = 0;
   public static final int texture_stone_magma = 1;
   public static final int texture_obsidian_normal = 2;
   public static final int texture_obsidian_magma = 3;
   public static final int texture_netherrack_normal = 4;
   public static final int texture_netherrack_magma = 5;
   public static final int texture_end_stone_normal = 6;
   public static final int texture_end_stone_magma = 7;
   public static final int texture_clay_normal = 8;
   public static final int texture_clay_hardened = 9;

   public RenderEarthElemental() {
      super(new ModelInvisibleStalker(), 0.5F);
   }

   private void setTexture(int index, String name, boolean magma) {
      if (index == 9) {
         this.setTexture(index, "textures/entity/earth_elemental/" + name + "/earth_elemental_" + name + "_hardened", "textures/entity/earth_elemental/earth_elemental");
      } else {
         this.setTexture(index, "textures/entity/earth_elemental/" + name + "/earth_elemental_" + name + (magma ? "_magma" : ""), "textures/entity/earth_elemental/" + (magma ? "earth_elemental_magma" : "earth_elemental"));
      }
   }

   protected void setTextures() {
      this.setTexture(0, "stone", false);
      this.setTexture(1, "stone", true);
      this.setTexture(2, "obsidian", false);
      this.setTexture(3, "obsidian", true);
      this.setTexture(4, "netherrack", false);
      this.setTexture(5, "netherrack", true);
      this.setTexture(6, "end_stone", false);
      this.setTexture(7, "end_stone", true);
      this.setTexture(8, "clay", false);
      this.setTexture(9, "clay", false);
   }

   protected ResourceLocation getTextures(EntityEarthElemental earth_elemental) {
      int type = earth_elemental.getType();
      if (type == EntityEarthElemental.CLAY_NORMAL) {
         return this.textures[8];
      } else {
         return type == EntityEarthElemental.CLAY_HARDENED ? this.textures[9] : this.textures[type == EntityEarthElemental.STONE_NORMAL ? 0 : (type == EntityEarthElemental.STONE_MAGMA ? 1 : (type == EntityEarthElemental.OBSIDIAN_NORMAL ? 2 : (type == EntityEarthElemental.OBSIDIAN_MAGMA ? 3 : (type == EntityEarthElemental.NETHERRACK_NORMAL ? 4 : (type == EntityEarthElemental.NETHERRACK_MAGMA ? 5 : (type == EntityEarthElemental.END_STONE_NORMAL ? 6 : 7))))))];
      }
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return this.getTextures((EntityEarthElemental)entity);
   }
}
