package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NBTTagList extends NBTBase {
   private List tagList = new ArrayList();
   private byte tagType;

   public NBTTagList() {
      super(9, "");
   }

   public NBTTagList(String par1Str) {
      super(9, par1Str);
   }

   void write(DataOutput par1DataOutput) throws IOException {
      if (!this.tagList.isEmpty()) {
         this.tagType = ((NBTBase)this.tagList.get(0)).getId();
      } else {
         this.tagType = 1;
      }

      par1DataOutput.writeByte(convertIdForDisk(this.tagType));
      par1DataOutput.writeInt(this.tagList.size());

      for(int var2 = 0; var2 < this.tagList.size(); ++var2) {
         ((NBTBase)this.tagList.get(var2)).write(par1DataOutput);
      }

   }

   void load(DataInput par1DataInput, int par2) throws IOException {
      if (par2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.tagType = par1DataInput.readByte();
         this.tagType = convertIdFromDisk(this.tagType);
         int var3 = par1DataInput.readInt();
         this.tagList = new ArrayList();

         for(int var4 = 0; var4 < var3; ++var4) {
            NBTBase var5 = NBTBase.newTag(this.tagType, (String)null);
            if (var5 == null && NBTBase.loading_world_info) {
               NBTBase.incompatibleWorldInfoFormat();
            }

            var5.load(par1DataInput, par2 + 1);
            this.tagList.add(var5);
         }

      }
   }

   public String toString() {
      return "" + this.tagList.size() + " entries of type " + NBTBase.getTagName(this.tagType);
   }

   public void appendTag(NBTBase par1NBTBase) {
      this.tagType = par1NBTBase.getId();
      this.tagList.add(par1NBTBase);
   }

   public NBTBase removeTag(int par1) {
      return (NBTBase)this.tagList.remove(par1);
   }

   public NBTBase tagAt(int par1) {
      return (NBTBase)this.tagList.get(par1);
   }

   public int tagCount() {
      return this.tagList.size();
   }

   public NBTBase copy() {
      NBTTagList var1 = new NBTTagList(this.getName());
      var1.tagType = this.tagType;
      Iterator var2 = this.tagList.iterator();

      while(var2.hasNext()) {
         NBTBase var3 = (NBTBase)var2.next();
         NBTBase var4 = var3.copy();
         var1.tagList.add(var4);
      }

      return var1;
   }

   public boolean equals(Object par1Obj) {
      if (super.equals(par1Obj)) {
         NBTTagList var2 = (NBTTagList)par1Obj;
         if (this.tagType == var2.tagType) {
            return this.tagList.equals(var2.tagList);
         }
      }

      return false;
   }

   public int hashCode() {
      return super.hashCode() ^ this.tagList.hashCode();
   }
}
