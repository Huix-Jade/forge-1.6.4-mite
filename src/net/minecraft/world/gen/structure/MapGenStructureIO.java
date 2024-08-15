package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MapGenStructureIO {
   private static Map field_143040_a = new HashMap();
   private static Map field_143038_b = new HashMap();
   private static Map field_143039_c = new HashMap();
   private static Map field_143037_d = new HashMap();

   private static void func_143034_b(Class var0, String var1) {
      field_143040_a.put(var1, var0);
      field_143038_b.put(var0, var1);
   }

   static void func_143031_a(Class var0, String var1) {
      field_143039_c.put(var1, var0);
      field_143037_d.put(var0, var1);
   }

   public static String func_143033_a(StructureStart var0) {
      return (String)field_143038_b.get(var0.getClass());
   }

   public static String func_143036_a(StructureComponent var0) {
      return (String)field_143037_d.get(var0.getClass());
   }

   public static StructureStart func_143035_a(NBTTagCompound var0, World var1) {
      StructureStart var2 = null;

      try {
         Class var3 = (Class)field_143040_a.get(var0.getString("id"));
         if (var3 != null) {
            var2 = (StructureStart)var3.newInstance();
         }
      } catch (Exception var4) {
         var1.getWorldLogAgent().logWarning("Failed Start with id " + var0.getString("id"));
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.func_143020_a(var1, var0);
      } else {
         var1.getWorldLogAgent().logWarning("Skipping Structure with id " + var0.getString("id"));
      }

      return var2;
   }

   public static StructureComponent func_143032_b(NBTTagCompound var0, World var1) {
      StructureComponent var2 = null;

      try {
         Class var3 = (Class)field_143039_c.get(var0.getString("id"));
         if (var3 != null) {
            var2 = (StructureComponent)var3.newInstance();
         }
      } catch (Exception var4) {
         var1.getWorldLogAgent().logWarning("Failed Piece with id " + var0.getString("id"));
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.func_143009_a(var1, var0);
      } else {
         var1.getWorldLogAgent().logWarning("Skipping Piece with id " + var0.getString("id"));
      }

      return var2;
   }

   static {
      func_143034_b(StructureMineshaftStart.class, "Mineshaft");
      func_143034_b(StructureVillageStart.class, "Village");
      func_143034_b(StructureNetherBridgeStart.class, "Fortress");
      func_143034_b(StructureStrongholdStart.class, "Stronghold");
      func_143034_b(StructureScatteredFeatureStart.class, "Temple");
      StructureMineshaftPieces.func_143048_a();
      StructureVillagePieces.func_143016_a();
      StructureNetherBridgePieces.func_143049_a();
      StructureStrongholdPieces.func_143046_a();
      ComponentScatteredFeaturePieces.func_143045_a();
   }
}
