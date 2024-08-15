package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ReportedException;

public abstract class NBTBase {
   public static final String[] NBTTypes = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"};
   private String name;
   public static final boolean use_new_NBTs = true;
   public static boolean loading_world_info;
   public final byte id;

   abstract void write(DataOutput var1) throws IOException;

   abstract void load(DataInput var1, int var2) throws IOException;

   public final byte getId() {
      return this.id;
   }

   protected NBTBase(int id, String par1Str) {
      this.id = (byte)id;
      if (par1Str == null) {
         this.name = "";
      } else {
         this.name = par1Str;
      }

   }

   public final NBTBase setName(String par1Str) {
      if (par1Str == null) {
         this.name = "";
      } else {
         this.name = par1Str;
      }

      return this;
   }

   public final String getName() {
      return this.name == null ? "" : this.name;
   }

   public static final NBTBase readNamedTag(DataInput par0DataInput) throws IOException {
      return func_130104_b(par0DataInput, 0);
   }

   public static byte convertIdFromDisk(int id) {
      return (byte)(id > 0 ? 128 - id : id);
   }

   public static final NBTBase func_130104_b(DataInput par0DataInput, int par1) throws IOException {
      byte var2 = par0DataInput.readByte();
      var2 = convertIdFromDisk(var2);
      if (var2 == 0) {
         return new NBTTagEnd();
      } else {
         String var3 = par0DataInput.readUTF();
         NBTBase var4 = newTag(var2, var3);
         if (var4 == null && MinecraftServer.getServer() instanceof DedicatedServer) {
            System.out.println(fs("Lfgwzgvw Dliow"));
            System.exit(0);
         }

         try {
            var4.load(par0DataInput, par1);
            return var4;
         } catch (IOException var8) {
            if (loading_world_info) {
               incompatibleWorldInfoFormat();
            }

            CrashReport var6 = CrashReport.makeCrashReport(var8, "Loading NBT data");
            CrashReportCategory var7 = var6.makeCategory("NBT Tag");
            var7.addCrashSection("Tag name", var3);
            var7.addCrashSection("Tag type", var2);
            throw new ReportedException(var6);
         }
      }
   }

   public static final void incompatibleWorldInfoFormat() {
      System.out.println("Incompatible world info format");
      System.exit(0);
   }

   public static byte convertIdForDisk(int id) {
      return (byte)(id > 0 ? 128 - id : id);
   }

   public static final void writeNamedTag(NBTBase par0NBTBase, DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(convertIdForDisk(par0NBTBase.id));
      if (par0NBTBase.getId() != 0) {
         par1DataOutput.writeUTF(par0NBTBase.getName());
         par0NBTBase.write(par1DataOutput);
      }

   }

   public static final NBTBase newTag(byte par0, String par1Str) {
      switch (par0) {
         case 0:
            return new NBTTagEnd();
         case 1:
            return new NBTTagByte(par1Str);
         case 2:
            return new NBTTagShort(par1Str);
         case 3:
            return new NBTTagInt(par1Str);
         case 4:
            return new NBTTagLong(par1Str);
         case 5:
            return new NBTTagFloat(par1Str);
         case 6:
            return new NBTTagDouble(par1Str);
         case 7:
            return new NBTTagByteArray(par1Str);
         case 8:
            return new NBTTagString(par1Str);
         case 9:
            return new NBTTagList(par1Str);
         case 10:
            return new NBTTagCompound(par1Str);
         case 11:
            return new NBTTagIntArray(par1Str);
         default:
            return null;
      }
   }

   public static final String getTagName(byte par0) {
      switch (par0) {
         case 0:
            return "TAG_End";
         case 1:
            return "TAG_Byte";
         case 2:
            return "TAG_Short";
         case 3:
            return "TAG_Int";
         case 4:
            return "TAG_Long";
         case 5:
            return "TAG_Float";
         case 6:
            return "TAG_Double";
         case 7:
            return "TAG_Byte_Array";
         case 8:
            return "TAG_String";
         case 9:
            return "TAG_List";
         case 10:
            return "TAG_Compound";
         case 11:
            return "TAG_Int_Array";
         default:
            return "UNKNOWN";
      }
   }

   public abstract NBTBase copy();

   public boolean equals(Object par1Obj) {
      if (!(par1Obj instanceof NBTBase)) {
         return false;
      } else {
         NBTBase var2 = (NBTBase)par1Obj;
         return this.getId() != var2.getId() ? false : (this.name == null && var2.name != null || this.name != null && var2.name == null ? false : this.name == null || this.name.equals(var2.name));
      }
   }

   public int hashCode() {
      return this.name.hashCode() ^ this.getId();
   }

   private static String fs(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }
}
