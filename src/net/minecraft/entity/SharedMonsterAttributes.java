package net.minecraft.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class SharedMonsterAttributes {
   public static final Attribute maxHealth = (new RangedAttribute("generic.maxHealth", 20.0, 0.0, Double.MAX_VALUE)).func_111117_a("Max Health").setShouldWatch(true);
   public static final Attribute followRange = (new RangedAttribute("generic.followRange", 32.0, 0.0, 2048.0)).func_111117_a("Follow Range");
   public static final Attribute knockbackResistance = (new RangedAttribute("generic.knockbackResistance", 0.0, 0.0, 1.0)).func_111117_a("Knockback Resistance");
   public static final Attribute movementSpeed = (new RangedAttribute("generic.movementSpeed", 0.699999988079071, 0.0, Double.MAX_VALUE)).func_111117_a("Movement Speed").setShouldWatch(true);
   public static final Attribute attackDamage = new RangedAttribute("generic.attackDamage", 2.0, 0.0, Double.MAX_VALUE);

   public static NBTTagList func_111257_a(BaseAttributeMap var0) {
      NBTTagList var1 = new NBTTagList();
      Iterator var2 = var0.getAllAttributes().iterator();

      while(var2.hasNext()) {
         AttributeInstance var3 = (AttributeInstance)var2.next();
         var1.appendTag(func_111261_a(var3));
      }

      return var1;
   }

   private static NBTTagCompound func_111261_a(AttributeInstance var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      Attribute var2 = var0.func_111123_a();
      var1.setString("Name", var2.getAttributeUnlocalizedName());
      var1.setDouble("Base", var0.getBaseValue());
      Collection var3 = var0.func_111122_c();
      if (var3 != null && !var3.isEmpty()) {
         NBTTagList var4 = new NBTTagList();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            AttributeModifier var6 = (AttributeModifier)var5.next();
            if (var6.isSaved()) {
               var4.appendTag(func_111262_a(var6));
            }
         }

         var1.setTag("Modifiers", var4);
      }

      return var1;
   }

   private static NBTTagCompound func_111262_a(AttributeModifier var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.setString("Name", var0.getName());
      var1.setDouble("Amount", var0.getAmount());
      var1.setInteger("Operation", var0.getOperation());
      var1.setLong("UUIDMost", var0.getID().getMostSignificantBits());
      var1.setLong("UUIDLeast", var0.getID().getLeastSignificantBits());
      return var1;
   }

   public static void func_111260_a(BaseAttributeMap var0, NBTTagList var1, ILogAgent var2) {
      for(int var3 = 0; var3 < var1.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var1.tagAt(var3);
         AttributeInstance var5 = var0.getAttributeInstanceByName(var4.getString("Name"));
         if (var5 != null) {
            func_111258_a(var5, var4);
         } else if (var2 != null) {
            var2.logWarning("Ignoring unknown attribute '" + var4.getString("Name") + "'");
         }
      }

   }

   private static void func_111258_a(AttributeInstance var0, NBTTagCompound var1) {
      var0.setAttribute(var1.getDouble("Base"));
      if (var1.hasKey("Modifiers")) {
         NBTTagList var2 = var1.getTagList("Modifiers");

         for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
            AttributeModifier var4 = func_111259_a((NBTTagCompound)var2.tagAt(var3));
            AttributeModifier var5 = var0.getModifier(var4.getID());
            if (var5 != null) {
               var0.removeModifier(var5);
            }

            var0.applyModifier(var4);
         }
      }

   }

   public static AttributeModifier func_111259_a(NBTTagCompound var0) {
      UUID var1 = new UUID(var0.getLong("UUIDMost"), var0.getLong("UUIDLeast"));
      return new AttributeModifier(var1, var0.getString("Name"), var0.getDouble("Amount"), var0.getInteger("Operation"));
   }
}
