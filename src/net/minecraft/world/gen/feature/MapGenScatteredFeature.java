package net.minecraft.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.ComponentScatteredFeatureSwampHut;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureScatteredFeatureStart;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenScatteredFeature extends MapGenStructure {
   private static List biomelist;
   private List scatteredFeatureSpawnList;
   private int maxDistanceBetweenScatteredFeatures;
   private int minDistanceBetweenScatteredFeatures;

   public MapGenScatteredFeature() {
      this.scatteredFeatureSpawnList = new ArrayList();
      this.maxDistanceBetweenScatteredFeatures = 40;
      this.minDistanceBetweenScatteredFeatures = 20;
   }

   public MapGenScatteredFeature(Map par1Map) {
      this();
      Iterator var2 = par1Map.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (((String)var3.getKey()).equals("distance")) {
            this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1);
         }
      }

   }

   public String func_143025_a() {
      return "Temple";
   }

   protected boolean canSpawnStructureAtCoords(int par1, int par2) {
      int var3 = par1;
      int var4 = par2;
      if (par1 < 0) {
         par1 -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      if (par2 < 0) {
         par2 -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      int var5 = par1 / this.maxDistanceBetweenScatteredFeatures;
      int var6 = par2 / this.maxDistanceBetweenScatteredFeatures;
      Random var7 = new Random((long)var5 * 341873128712L + (long)var6 * 132897987541L + this.worldObj.getWorldInfo().getSeed() + 14357617L);
      var5 *= this.maxDistanceBetweenScatteredFeatures;
      var6 *= this.maxDistanceBetweenScatteredFeatures;
      var5 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      var6 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      if (var3 == var5 && var4 == var6) {
         BiomeGenBase var8 = this.worldObj.getWorldChunkManager().getBiomeGenAt(var3 * 16 + 8, var4 * 16 + 8);
         Iterator var9 = biomelist.iterator();

         while(var9.hasNext()) {
            BiomeGenBase var10 = (BiomeGenBase)var9.next();
            if (var8 == var10) {
               return true;
            }
         }
      }

      return false;
   }

   protected StructureStart getStructureStart(int par1, int par2) {
      return new StructureScatteredFeatureStart(this.worldObj, this.rand, par1, par2);
   }

   public boolean func_143030_a(int par1, int par2, int par3) {
      StructureStart var4 = this.func_143028_c(par1, par2, par3);
      if (var4 != null && var4 instanceof StructureScatteredFeatureStart && !var4.components.isEmpty()) {
         StructureComponent var5 = (StructureComponent)var4.components.getFirst();
         return var5 instanceof ComponentScatteredFeatureSwampHut;
      } else {
         return false;
      }
   }

   public List getScatteredFeatureSpawnList() {
      return this.scatteredFeatureSpawnList;
   }

   static {
      biomelist = Arrays.asList(BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland);
   }
}
