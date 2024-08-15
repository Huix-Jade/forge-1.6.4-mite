package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.MathHelper;

public class MapGenMineshaft extends MapGenStructure {
   private double field_82673_e = 0.01;

   public MapGenMineshaft() {
   }

   public String func_143025_a() {
      return "Mineshaft";
   }

   public MapGenMineshaft(Map par1Map) {
      Iterator var2 = par1Map.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (((String)var3.getKey()).equals("chance")) {
            this.field_82673_e = MathHelper.parseDoubleWithDefault((String)var3.getValue(), this.field_82673_e);
         }
      }

   }

   protected boolean canSpawnStructureAtCoords(int chunk_x, int chunk_z) {
      if (this.rand.nextFloat() >= 0.005F) {
         return false;
      } else if (!this.worldObj.getBiomeGenForCoords(chunk_x * 16, chunk_z * 16).canHaveMineshafts()) {
         return false;
      } else {
         boolean ret = this.rand.nextInt(80) < Math.max(Math.abs(chunk_x), Math.abs(chunk_z));
         return ret;
      }
   }

   protected StructureStart getStructureStart(int par1, int par2) {
      return new StructureMineshaftStart(this.worldObj, this.rand, par1, par2);
   }
}
