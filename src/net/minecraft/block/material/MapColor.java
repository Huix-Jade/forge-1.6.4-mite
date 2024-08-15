package net.minecraft.block.material;

public class MapColor {
   public static final MapColor[] mapColorArray = new MapColor[32];
   public static final MapColor airColor = new MapColor(0, 0);
   public static final MapColor grassColor = new MapColor(1, 8368696);
   public static final MapColor sandColor = new MapColor(2, 16247203);
   public static final MapColor clothColor = new MapColor(3, 10987431);
   public static final MapColor tntColor = new MapColor(4, 16711680);
   public static final MapColor iceColor = new MapColor(5, 10526975);
   public static final MapColor ironColor = new MapColor(6, 10987431);
   public static final MapColor foliageColor = new MapColor(7, 31744);
   public static final MapColor snowColor = new MapColor(8, 16777215);
   public static final MapColor clayColor = new MapColor(9, 10791096);
   public static final MapColor dirtColor = new MapColor(10, 12020271);
   public static final MapColor stoneColor = new MapColor(11, 7368816);
   public static final MapColor waterColor = new MapColor(12, 4210943);
   public static final MapColor woodColor = new MapColor(13, 6837042);
   public static final MapColor copperColor = new MapColor(14, 10970880);
   public static final MapColor silverColor = new MapColor(15, 13092807);
   public static final MapColor goldColor = new MapColor(16, 10983168);
   public static final MapColor mithrilColor = new MapColor(17, 10991559);
   public static final MapColor adamantiumColor = new MapColor(18, 3090231);
   public static final MapColor rustedIronColor = new MapColor(19, 10854039);
   public static final MapColor emeraldColor = new MapColor(20, 3787611);
   public static final MapColor diamondColor = new MapColor(21, 6938586);
   public static final MapColor redstoneColor = new MapColor(22, 13708051);
   public static final MapColor obsidianColor = new MapColor(23, 3549776);
   public static final MapColor netherrackColor = new MapColor(24, 7288372);
   public static final MapColor leatherColor = new MapColor(25, 12999733);
   public static final MapColor quartzColor = new MapColor(26, 15394014);
   public static final MapColor ancientMetalColor = new MapColor(27, 10331545);
   public final int colorValue;
   public final int colorIndex;

   private MapColor(int par1, int par2) {
      this.colorIndex = par1;
      this.colorValue = par2;
      mapColorArray[par1] = this;
   }
}
