package net.minecraft.block;

import net.minecraft.util.Icon;

public class BlockSubtypes {
   private final String[] textures;
   private final String[] names;
   private Icon[] icons;

   public BlockSubtypes(String[] types) {
      this(types, types);
   }

   public BlockSubtypes(String[] textures, String[] names) {
      this.textures = textures;
      this.names = names;
   }

   public String[] getTextures() {
      return this.textures;
   }

   public String[] getNames() {
      return this.names;
   }

   public void setIcons(Icon[] icons) {
      this.icons = icons;
   }

   public Icon[] getIcons() {
      return this.icons;
   }

   public Icon getIcon(int index) {
      return this.icons[index];
   }
}
