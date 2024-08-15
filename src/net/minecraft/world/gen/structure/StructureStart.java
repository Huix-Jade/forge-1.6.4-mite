package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class StructureStart {
   public LinkedList components = new LinkedList();
   protected StructureBoundingBox boundingBox;
   private int field_143024_c;
   private int field_143023_d;

   public StructureStart() {
   }

   public StructureStart(int var1, int var2) {
      this.field_143024_c = var1;
      this.field_143023_d = var2;
   }

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public LinkedList getComponents() {
      return this.components;
   }

   public void generateStructure(World var1, Random var2, StructureBoundingBox var3) {
      Iterator var4 = this.components.iterator();

      while(var4.hasNext()) {
         StructureComponent var5 = (StructureComponent)var4.next();
         if (var5.getBoundingBox().intersectsWith(var3) && !var5.addComponentParts(var1, var2, var3)) {
            var4.remove();
         }
      }

   }

   protected void updateBoundingBox() {
      this.boundingBox = StructureBoundingBox.getNewBoundingBox();
      Iterator var1 = this.components.iterator();

      while(var1.hasNext()) {
         StructureComponent var2 = (StructureComponent)var1.next();
         this.boundingBox.expandTo(var2.getBoundingBox());
      }

   }

   public NBTTagCompound func_143021_a(int var1, int var2) {
      NBTTagCompound var3 = new NBTTagCompound();
      var3.setString("id", MapGenStructureIO.func_143033_a(this));
      var3.setInteger("ChunkX", var1);
      var3.setInteger("ChunkZ", var2);
      var3.setTag("BB", this.boundingBox.func_143047_a("BB"));
      NBTTagList var4 = new NBTTagList("Children");
      Iterator var5 = this.components.iterator();

      while(var5.hasNext()) {
         StructureComponent var6 = (StructureComponent)var5.next();
         var4.appendTag(var6.func_143010_b());
      }

      var3.setTag("Children", var4);
      this.func_143022_a(var3);
      return var3;
   }

   public void func_143022_a(NBTTagCompound var1) {
   }

   public void func_143020_a(World var1, NBTTagCompound var2) {
      this.field_143024_c = var2.getInteger("ChunkX");
      this.field_143023_d = var2.getInteger("ChunkZ");
      if (var2.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(var2.getIntArray("BB"));
      }

      NBTTagList var3 = var2.getTagList("Children");

      for(int var4 = 0; var4 < var3.tagCount(); ++var4) {
         this.components.add(MapGenStructureIO.func_143032_b((NBTTagCompound)var3.tagAt(var4), var1));
      }

      this.func_143017_b(var2);
   }

   public void func_143017_b(NBTTagCompound var1) {
   }

   protected void markAvailableHeight(World var1, Random var2, int var3) {
      int var4 = 63 - var3;
      int var5 = this.boundingBox.getYSize() + 1;
      if (var5 < var4) {
         var5 += var2.nextInt(var4 - var5);
      }

      int var6 = var5 - this.boundingBox.maxY;
      this.boundingBox.offset(0, var6, 0);
      Iterator var7 = this.components.iterator();

      while(var7.hasNext()) {
         StructureComponent var8 = (StructureComponent)var7.next();
         var8.getBoundingBox().offset(0, var6, 0);
      }

   }

   protected void setRandomHeight(World var1, Random var2, int var3, int var4) {
      int var5 = var4 - var3 + 1 - this.boundingBox.getYSize();
      boolean var6 = true;
      int var10;
      if (var5 > 1) {
         var10 = var3 + var2.nextInt(var5);
      } else {
         var10 = var3;
      }

      int var7 = var10 - this.boundingBox.minY;
      this.boundingBox.offset(0, var7, 0);
      Iterator var8 = this.components.iterator();

      while(var8.hasNext()) {
         StructureComponent var9 = (StructureComponent)var8.next();
         var9.getBoundingBox().offset(0, var7, 0);
      }

   }

   public boolean isSizeableStructure() {
      return true;
   }

   public int func_143019_e() {
      return this.field_143024_c;
   }

   public int func_143018_f() {
      return this.field_143023_d;
   }
}
