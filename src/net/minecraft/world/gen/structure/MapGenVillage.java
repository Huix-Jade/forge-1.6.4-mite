package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.WorldInfo;

public class MapGenVillage extends MapGenStructure {
   public static final List villageSpawnBiomes;
   private int terrainType;
   private int field_82665_g;
   private int field_82666_h;

   public MapGenVillage() {
      this.field_82665_g = 40;
      this.field_82666_h = 20;
   }

   public MapGenVillage(Map par1Map) {
      this();
      Iterator var2 = par1Map.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (((String)var3.getKey()).equals("size")) {
            this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.terrainType, 0);
         } else if (((String)var3.getKey()).equals("distance")) {
            this.field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.field_82665_g, this.field_82666_h + 1);
         }
      }

   }

   public String func_143025_a() {
      return "Village";
   }

   protected boolean canSpawnStructureAtCoords(int par1, int par2) {
      if (Minecraft.isInTournamentMode()) {
         return false;
      } else if (this.worldObj.getDayOfWorld() < 60) {
         return false;
      } else {
         byte required_village_conditions = WorldInfo.getVillagePrerequisites();
         if (this.worldObj.worldInfo.getVillageConditions() < required_village_conditions) {
            return false;
         } else {
            int var3 = par1;
            int var4 = par2;
            if (par1 < 0) {
               par1 -= this.field_82665_g - 1;
            }

            if (par2 < 0) {
               par2 -= this.field_82665_g - 1;
            }

            int var5 = par1 / this.field_82665_g;
            int var6 = par2 / this.field_82665_g;
            Random var7 = new Random((long)var5 * 341873128712L + (long)var6 * 132897987541L + this.worldObj.getWorldInfo().getSeed() + 10387312L);
            var5 *= this.field_82665_g;
            var6 *= this.field_82665_g;
            var5 += var7.nextInt(this.field_82665_g - this.field_82666_h);
            var6 += var7.nextInt(this.field_82665_g - this.field_82666_h);
            if (var3 == var5 && var4 == var6) {
               boolean var8 = this.worldObj.getWorldChunkManager().areBiomesViable(var3 * 16 + 8, var4 * 16 + 8, 0, villageSpawnBiomes);
               if (var8) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   protected StructureStart getStructureStart(int par1, int par2) {
      return new StructureVillageStart(this.worldObj, this.rand, par1, par2, this.terrainType);
   }

   static {
      villageSpawnBiomes = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert);
   }
}
