package net.minecraft.util;

public enum EnumArt {
   Kebab("Kebab", 16, 16, 0, 0),
   Aztec("Aztec", 16, 16, 16, 0),
   Alban("Alban", 16, 16, 32, 0),
   Aztec2("Aztec2", 16, 16, 48, 0),
   Bomb("Bomb", 16, 16, 64, 0),
   Plant("Plant", 16, 16, 80, 0),
   Wasteland("Wasteland", 16, 16, 96, 0),
   Pool("Pool", 32, 16, 0, 32),
   Courbet("Courbet", 32, 16, 32, 32),
   Sea("Sea", 32, 16, 64, 32),
   Sunset("Sunset", 32, 16, 96, 32),
   Creebet("Creebet", 32, 16, 128, 32),
   Wanderer("Wanderer", 16, 32, 0, 64),
   Graham("Graham", 16, 32, 16, 64),
   Match("Match", 32, 32, 0, 128),
   Bust("Bust", 32, 32, 32, 128),
   Stage("Stage", 32, 32, 64, 128),
   Void("Void", 32, 32, 96, 128),
   SkullAndRoses("SkullAndRoses", 32, 32, 128, 128),
   Wither("Wither", 32, 32, 160, 128),
   Fighters("Fighters", 64, 32, 0, 96),
   Pointer("Pointer", 64, 64, 0, 192),
   Pigscene("Pigscene", 64, 64, 64, 192),
   BurningSkull("BurningSkull", 64, 64, 128, 192),
   Skeleton("Skeleton", 64, 48, 192, 64),
   DonkeyKong("DonkeyKong", 64, 48, 192, 112),
   Abyss("Abyss", 48, 32, "abyss.png", 32),
   BaronAlmric("BaronAlmric", 48, 32, "baron_almric.png", 64),
   Boat("Boat", 48, 32, "boat.png", 16),
   Castle("Castle", 32, 32, "castle.png", 8),
   CastleBritannia("CastleBritannia", 64, 32, "castle_britannia.png", 8),
   Darklands("Darklands", 32, 32, "darklands.png", 8),
   DeathtrapDungeon("DeathtrapDungeon", 48, 48, "deathtrap_dungeon.png", 64),
   DnDBasic("DnDBasic", 32, 32, "dnd_basic.png", 64),
   Draracle("Draracle", 48, 32, "draracle.png", 16),
   EldenGrove("EldenGrove", 64, 32, "elden_grove.png", 8),
   FairDay("FairDay", 64, 32, "fair_day.png", 8),
   FallenBridge("FallenBridge", 48, 32, "fallen_bridge.png", 16),
   GateClosing("GateClosing", 64, 32, "gate_closing.png", 16),
   Ghoul("Ghoul", 32, 48, "ghoul.png", 16),
   GladstoneKeep("GladstoneKeep", 48, 32, "gladstone_keep.png", 16),
   Graves("Graves", 32, 32, "graves.png", 8),
   KingRichard("KingRichard", 32, 32, "king_richard.png", 64),
   Messenger("Messenger", 48, 32, "messenger.png", 16),
   Mountains("Mountains", 32, 32, "mountains.png", 8),
   RolandsManor("RolandsManor", 48, 32, "rolands_manor.png", 8),
   Scotia("Scotia", 32, 32, "scotia.png", 64),
   Ship("Ship", 48, 32, "ship.png", 8),
   Sunlight("Sunlight", 32, 32, "sunlight.png", 8),
   Titan("Titan", 48, 32, "titan.png", 8),
   Wolves("Wolves", 32, 32, "wolves.png", 8);

   public static final int maxArtTitleLength = "SkullAndRoses".length();
   public final String title;
   public final int sizeX;
   public final int sizeY;
   public final int offsetX;
   public final int offsetY;
   public ResourceLocation special_texture;
   public int rarity;

   private EnumArt(String par3Str, int par4, int par5, int par6, int par7) {
      this.title = par3Str;
      this.sizeX = par4;
      this.sizeY = par5;
      this.offsetX = par6;
      this.offsetY = par7;
   }

   private EnumArt(String title, int width, int height, String filename, int rarity) {
      this(title, width, height, 0, 0);
      this.special_texture = new ResourceLocation("textures/painting/" + filename);
      this.rarity = rarity;
   }
}
