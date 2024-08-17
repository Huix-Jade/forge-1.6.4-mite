package net.minecraft.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateFlatWorld;
import net.minecraft.client.gui.GuiCreateWorld;

public class WorldType {
   public static final WorldType[] worldTypes = new WorldType[16];
   public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
   public static final WorldType FLAT = new WorldType(1, "flat");
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
   private final int worldTypeId;
   private final String worldType;
   private final int generatorVersion;
   private boolean canBeCreated;
   private boolean isWorldTypeVersioned;

   private WorldType(int var1, String var2) {
      this(var1, var2, 0);
   }

   private WorldType(int var1, String var2, int var3) {
      this.worldType = var2;
      this.generatorVersion = var3;
      this.canBeCreated = true;
      this.worldTypeId = var1;
      worldTypes[var1] = this;
   }

   public String getWorldTypeName() {
      return this.worldType;
   }

   public String getTranslateName() {
      return "generator." + this.worldType;
   }

   public int getGeneratorVersion() {
      return this.generatorVersion;
   }

   public WorldType getWorldTypeForGeneratorVersion(int var1) {
      return this == DEFAULT && var1 == 0 ? DEFAULT_1_1 : this;
   }

   private WorldType setCanBeCreated(boolean var1) {
      this.canBeCreated = var1;
      return this;
   }

   public boolean getCanBeCreated() {
      return this.canBeCreated;
   }

   private WorldType setVersioned() {
      this.isWorldTypeVersioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.isWorldTypeVersioned;
   }

   public static WorldType parseWorldType(String var0) {
       for (WorldType type : worldTypes) {
           if (type != null && type.worldType.equalsIgnoreCase(var0)) {
               return type;
           }
       }

      return null;
   }

   public int getWorldTypeID() {
      return this.worldTypeId;
   }

   /**
    * Gets the spawn fuzz for players who join the world.
    * Useful for void world types.
    * @return Fuzz for entity initial spawn in blocks.
    */
   public int getSpawnFuzz()
   {
      return 20;
   }

   /**
    * Called when the 'Customize' button is pressed on world creation GUI
    * @param instance The minecraft instance
    * @param guiCreateWorld the createworld GUI
    */

   public void onCustomizeButton(Minecraft instance, GuiCreateWorld guiCreateWorld)
   {
      if (this == FLAT)
      {
         instance.displayGuiScreen(new GuiCreateFlatWorld(guiCreateWorld, guiCreateWorld.generatorOptionsToUse));
      }
   }

   /*
    * Should world creation GUI show 'Customize' button for this world type?
    * @return if this world type has customization parameters
    */
   public boolean isCustomizable()
   {
      return this == FLAT;
   }


   /**
    * the y level at which clouds are rendered.
    */
   public float getCloudHeight()
   {
      return 128.0F;
   }
}
