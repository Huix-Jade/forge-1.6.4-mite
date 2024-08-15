package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;

public class StructureNetherBridgePieces {
   private static final StructureNetherBridgePieceWeight[] primaryComponents = new StructureNetherBridgePieceWeight[]{new StructureNetherBridgePieceWeight(ComponentNetherBridgeStraight.class, 30, 0, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing3.class, 10, 4), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing.class, 10, 4), new StructureNetherBridgePieceWeight(ComponentNetherBridgeStairs.class, 10, 3), new StructureNetherBridgePieceWeight(ComponentNetherBridgeThrone.class, 5, 2), new StructureNetherBridgePieceWeight(ComponentNetherBridgeEntrance.class, 5, 1)};
   private static final StructureNetherBridgePieceWeight[] secondaryComponents = new StructureNetherBridgePieceWeight[]{new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor5.class, 25, 0, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing2.class, 15, 5), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor2.class, 5, 10), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor.class, 5, 10), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor3.class, 10, 3, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor4.class, 7, 2), new StructureNetherBridgePieceWeight(ComponentNetherBridgeNetherStalkRoom.class, 5, 2)};

   public static void func_143049_a() {
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCrossing3.class, "NeBCr");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeEnd.class, "NeBEF");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeStraight.class, "NeBS");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCorridor3.class, "NeCCS");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCorridor4.class, "NeCTB");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeEntrance.class, "NeCE");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCrossing2.class, "NeSCSC");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCorridor.class, "NeSCLT");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCorridor5.class, "NeSC");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCorridor2.class, "NeSCRT");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeNetherStalkRoom.class, "NeCSR");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeThrone.class, "NeMT");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeCrossing.class, "NeRC");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeStairs.class, "NeSR");
      MapGenStructureIO.func_143031_a(ComponentNetherBridgeStartPiece.class, "NeStart");
   }

   private static ComponentNetherBridgePiece createNextComponentRandom(StructureNetherBridgePieceWeight var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
      Class var8 = var0.weightClass;
      Object var9 = null;
      if (var8 == ComponentNetherBridgeStraight.class) {
         var9 = ComponentNetherBridgeStraight.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCrossing3.class) {
         var9 = ComponentNetherBridgeCrossing3.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCrossing.class) {
         var9 = ComponentNetherBridgeCrossing.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeStairs.class) {
         var9 = ComponentNetherBridgeStairs.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeThrone.class) {
         var9 = ComponentNetherBridgeThrone.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeEntrance.class) {
         var9 = ComponentNetherBridgeEntrance.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCorridor5.class) {
         var9 = ComponentNetherBridgeCorridor5.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCorridor2.class) {
         var9 = ComponentNetherBridgeCorridor2.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCorridor.class) {
         var9 = ComponentNetherBridgeCorridor.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCorridor3.class) {
         var9 = ComponentNetherBridgeCorridor3.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCorridor4.class) {
         var9 = ComponentNetherBridgeCorridor4.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeCrossing2.class) {
         var9 = ComponentNetherBridgeCrossing2.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == ComponentNetherBridgeNetherStalkRoom.class) {
         var9 = ComponentNetherBridgeNetherStalkRoom.createValidComponent(var1, var2, var3, var4, var5, var6, var7);
      }

      return (ComponentNetherBridgePiece)var9;
   }

   // $FF: synthetic method
   static ComponentNetherBridgePiece createNextComponent(StructureNetherBridgePieceWeight var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
      return createNextComponentRandom(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   static StructureNetherBridgePieceWeight[] getPrimaryComponents() {
      return primaryComponents;
   }

   // $FF: synthetic method
   static StructureNetherBridgePieceWeight[] getSecondaryComponents() {
      return secondaryComponents;
   }
}
