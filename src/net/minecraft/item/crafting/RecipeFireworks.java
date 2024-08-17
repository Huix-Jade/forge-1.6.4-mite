package net.minecraft.item.crafting;

import java.util.ArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe {
   private ItemStack field_92102_a;
   private float difficulty = 100.0F;
   private int[] skillsets;
   private Material material_to_check_tool_bench_hardness_against;

   public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
      this.field_92102_a = null;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      for(int var9 = 0; var9 < par1InventoryCrafting.getSizeInventory(); ++var9) {
         ItemStack var10 = par1InventoryCrafting.getStackInSlot(var9);
         if (var10 != null) {
            if (var10.itemID == Item.gunpowder.itemID) {
               ++var4;
            } else if (var10.itemID == Item.fireworkCharge.itemID) {
               ++var6;
            } else if (var10.itemID == Item.dyePowder.itemID) {
               ++var5;
            } else if (var10.itemID == Item.paper.itemID) {
               ++var3;
            } else if (var10.itemID == Item.glowstone.itemID) {
               ++var7;
            } else if (var10.itemID == Item.diamond.itemID) {
               ++var7;
            } else if (var10.itemID == Item.fireballCharge.itemID) {
               ++var8;
            } else if (var10.itemID == Item.feather.itemID) {
               ++var8;
            } else if (var10.itemID == Item.goldNugget.itemID) {
               ++var8;
            } else {
               if (var10.itemID != Item.skull.itemID) {
                  return false;
               }

               ++var8;
            }
         }
      }

      var7 += var5 + var8;
      if (var4 <= 3 && var3 <= 1) {
         int var20;
         ItemStack var11;
         NBTTagCompound var15 = new NBTTagCompound();
         NBTTagCompound var18;
         if (var4 >= 1 && var3 == 1 && var7 == 0) {
            this.field_92102_a = new ItemStack(Item.firework);
            if (var6 > 0) {
               var15 = new NBTTagCompound();
               var18 = new NBTTagCompound("Fireworks");
               NBTTagList var25 = new NBTTagList("Explosions");

               for(var20 = 0; var20 < par1InventoryCrafting.getSizeInventory(); ++var20) {
                  var11 = par1InventoryCrafting.getStackInSlot(var20);
                  if (var11 != null && var11.itemID == Item.fireworkCharge.itemID && var11.hasTagCompound() && var11.getTagCompound().hasKey("Explosion")) {
                     var25.appendTag(var11.getTagCompound().getCompoundTag("Explosion"));
                  }
               }
               var18.setTag("Explosions", var25);
               var18.setByte("Flight", (byte)var4);
               var15.setTag("Fireworks", var18);

            }

            this.field_92102_a.setTagCompound(var15);
            return true;
         } else {
            int var19;
            if (var4 == 1 && var3 == 0 && var6 == 0 && var5 > 0 && var8 <= 1) {
               this.field_92102_a = new ItemStack(Item.fireworkCharge);
               var15 = new NBTTagCompound();
               var18 = new NBTTagCompound("Explosion");
               byte var21 = 0;
               ArrayList var12 = new ArrayList();

               for(var19 = 0; var19 < par1InventoryCrafting.getSizeInventory(); ++var19) {
                  ItemStack var14 = par1InventoryCrafting.getStackInSlot(var19);
                  if (var14 != null) {
                     if (var14.itemID == Item.dyePowder.itemID) {
                        var12.add(ItemDye.dyeColors[var14.getItemSubtype()]);
                     } else if (var14.itemID == Item.glowstone.itemID) {
                        var18.setBoolean("Flicker", true);
                     } else if (var14.itemID == Item.diamond.itemID) {
                        var18.setBoolean("Trail", true);
                     } else if (var14.itemID == Item.fireballCharge.itemID) {
                        var21 = 1;
                     } else if (var14.itemID == Item.feather.itemID) {
                        var21 = 4;
                     } else if (var14.itemID == Item.goldNugget.itemID) {
                        var21 = 2;
                     } else if (var14.itemID == Item.skull.itemID) {
                        var21 = 3;
                     }
                  }
               }

               int[] var24 = new int[var12.size()];

               for(int var27 = 0; var27 < var24.length; ++var27) {
                  var24[var27] = (Integer)var12.get(var27);
               }

               var18.setIntArray("Colors", var24);
               var18.setByte("Type", var21);
               var15.setTag("Explosion", var18);
               this.field_92102_a.setTagCompound(var15);
               return true;
            } else if (var4 == 0 && var3 == 0 && var6 == 1 && var5 > 0 && var5 == var7) {
               ArrayList var16 = new ArrayList();

               for(var20 = 0; var20 < par1InventoryCrafting.getSizeInventory(); ++var20) {
                  var11 = par1InventoryCrafting.getStackInSlot(var20);
                  if (var11 != null) {
                     if (var11.itemID == Item.dyePowder.itemID) {
                        var16.add(ItemDye.dyeColors[var11.getItemSubtype()]);
                     } else if (var11.itemID == Item.fireworkCharge.itemID) {
                        this.field_92102_a = var11.copy();
                        this.field_92102_a.stackSize = 1;
                     }
                  }
               }

               int[] var17 = new int[var16.size()];

               for(var19 = 0; var19 < var17.length; ++var19) {
                  var17[var19] = (Integer)var16.get(var19);
               }

               if (this.field_92102_a != null && this.field_92102_a.hasTagCompound()) {
                  NBTTagCompound var23 = this.field_92102_a.getTagCompound().getCompoundTag("Explosion");
                  if (var23 == null) {
                     return false;
                  } else {
                     var23.setIntArray("FadeColors", var17);
                     return true;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public CraftingResult getCraftingResult(InventoryCrafting par1InventoryCrafting) {
      return new CraftingResult(this.field_92102_a.copy(), this.difficulty, this.skillsets, this);
   }

   public int getRecipeSize() {
      return 10;
   }

   public ItemStack getRecipeOutput() {
      return this.field_92102_a;
   }

   public ItemStack[] getComponents() {
      return null;
   }

   public IRecipe setDifficulty(float difficulty) {
      this.difficulty = difficulty;
      return this;
   }

   public IRecipe scaleDifficulty(float factor) {
      this.difficulty *= factor;
      return this;
   }

   public float getUnmodifiedDifficulty() {
      return this.difficulty;
   }

   public void setIncludeInLowestCraftingDifficultyDetermination() {
   }

   public boolean getIncludeInLowestCraftingDifficultyDetermination() {
      return false;
   }

   public void setSkillsets(int[] skillsets) {
      this.skillsets = skillsets;
   }

   public void setSkillset(int skillset) {
      this.skillsets = skillset == 0 ? null : new int[]{skillset};
   }

   public int[] getSkillsets() {
      return this.skillsets;
   }

   public void setMaterialToCheckToolBenchHardnessAgainst(Material material_to_check_tool_bench_hardness_against) {
      this.material_to_check_tool_bench_hardness_against = material_to_check_tool_bench_hardness_against;
   }

   public Material getMaterialToCheckToolBenchHardnessAgainst() {
      return this.material_to_check_tool_bench_hardness_against;
   }
}
