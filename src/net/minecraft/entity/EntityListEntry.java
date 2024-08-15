package net.minecraft.entity;

public class EntityListEntry {
   public Class _class;
   public String name;
   public int id;

   public EntityListEntry(Class _class, String name, int id) {
      this._class = _class;
      this.name = name;
      this.id = id;
   }
}
