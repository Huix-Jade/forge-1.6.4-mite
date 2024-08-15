package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemPotion extends Item {
   private HashMap effectCache = new HashMap();
   private static final Map field_77835_b = new LinkedHashMap();
   private Icon field_94591_c;
   private Icon field_94590_d;
   private Icon field_94592_ct;

   public ItemPotion(int par1) {
      super(par1, Material.glass, "potion");
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabBrewing);
      this.setCraftingDifficultyAsComponent(100.0F);
   }

   public List getEffects(ItemStack par1ItemStack) {
      if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("CustomPotionEffects")) {
         ArrayList var6 = new ArrayList();
         NBTTagList var3 = par1ItemStack.getTagCompound().getTagList("CustomPotionEffects");

         for(int var4 = 0; var4 < var3.tagCount(); ++var4) {
            NBTTagCompound var5 = (NBTTagCompound)var3.tagAt(var4);
            var6.add(PotionEffect.readCustomPotionEffectFromNBT(var5));
         }

         return var6;
      } else {
         List var2 = (List)this.effectCache.get(par1ItemStack.getItemSubtype());
         if (var2 == null) {
            var2 = PotionHelper.getPotionEffects(par1ItemStack.getItemSubtype(), false);
            this.effectCache.put(par1ItemStack.getItemSubtype(), var2);
         }

         return var2;
      }
   }

   public List getEffects(int par1) {
      List var2 = (List)this.effectCache.get(par1);
      if (var2 == null) {
         var2 = PotionHelper.getPotionEffects(par1, false);
         this.effectCache.put(par1, var2);
      }

      return var2;
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      if (player.onServer()) {
         List effects = this.getEffects(item_stack);
         if (effects != null) {
            Iterator i = effects.iterator();

            while(i.hasNext()) {
               player.addPotionEffect(new PotionEffect((PotionEffect)i.next()));
            }
         }
      }

      super.onItemUseFinish(item_stack, world, player);
   }

   public Item getItemProducedOnItemUseFinish() {
      return glassBottle;
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public boolean isDrinkable(int item_subtype) {
      return !isSplash(item_subtype);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      ItemStack item_stack = player.getHeldItemStack();
      if (isSplash(item_stack.getItemSubtype())) {
         if (player.onServer()) {
            WorldServer world = player.getWorldServer();
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            world.spawnEntityInWorld(new EntityPotion(world, player, item_stack));
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }

         return true;
      } else {
         if (isBottleOfWater(player.getHeldItemStack())) {
            RaycastCollision rc = player.getSelectedObject(partial_tick, true);
            if (rc != null) {
               if (rc.getNeighborOfBlockHit() == Block.fire) {
                  if (player.onServer()) {
                     rc.world.douseFire(rc.neighbor_block_x, rc.neighbor_block_y, rc.neighbor_block_z, (Entity)null);
                     player.convertOneOfHeldItem(new ItemStack(glassBottle));
                  }

                  return true;
               }

               Block block = rc.getBlockHit();
               int x = rc.block_hit_x;
               int y = rc.block_hit_y;
               int z = rc.block_hit_z;
               EnumFace face_hit = rc.face_hit;
               if (block instanceof BlockCrops || block instanceof BlockStem || block == Block.mushroomBrown) {
                  --y;
                  block = rc.world.getBlock(x, y, z);
                  face_hit = EnumFace.TOP;
               }

               if (block == Block.tilledField && face_hit == EnumFace.TOP && BlockFarmland.fertilize(rc.world, x, y, z, player.getHeldItemStack(), player)) {
                  if (player.onServer() && !player.inCreativeMode()) {
                     player.convertOneOfHeldItem(new ItemStack(glassBottle));
                  }

                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isBottleOfWater(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() == potion && item_stack.getItemSubtype() == 0;
   }

   public boolean hasIngestionPriority(ItemStack item_stack, boolean ctrl_is_down) {
      return !isBottleOfWater(item_stack);
   }

   public Icon getIconFromSubtype(int par1) {
      return isSplash(par1) ? this.field_94591_c : this.field_94590_d;
   }

   public Icon getIconFromSubtypeForRenderPass(int par1, int par2) {
      return par2 == 0 ? this.field_94592_ct : super.getIconFromSubtypeForRenderPass(par1, par2);
   }

   public static boolean isSplash(int par0) {
      return (par0 & 16384) != 0;
   }

   public int getColorFromDamage(int par1) {
      return PotionHelper.func_77915_a(par1, false);
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      return par2 > 0 ? 16777215 : this.getColorFromDamage(par1ItemStack.getItemSubtype());
   }

   public boolean requiresMultipleRenderPasses() {
      return true;
   }

   public boolean isEffectInstant(int par1) {
      List var2 = this.getEffects(par1);
      if (var2 != null && !var2.isEmpty()) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            PotionEffect var4 = (PotionEffect)var3.next();
            if (Potion.potionTypes[var4.getPotionID()].isInstant()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public String getItemDisplayName(ItemStack par1ItemStack) {
      if (par1ItemStack != null && par1ItemStack.getItemSubtype() != 0) {
         String var2 = "";
         if (isSplash(par1ItemStack.getItemSubtype())) {
            var2 = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
         }

         List var3 = Item.potion.getEffects(par1ItemStack);
         String var4;
         if (var3 != null && !var3.isEmpty()) {
            var4 = ((PotionEffect)var3.get(0)).getEffectName();
            var4 = var4 + ".postfix";
            return var2 + StatCollector.translateToLocal(var4).trim();
         } else {
            var4 = PotionHelper.func_77905_c(par1ItemStack.getItemSubtype());
            return StatCollector.translateToLocal(var4).trim() + " " + super.getItemDisplayName(par1ItemStack);
         }
      } else {
         return StatCollector.translateToLocal("item.emptyPotion.name").trim();
      }
   }

   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      if (par1ItemStack.getItemSubtype() != 0) {
         List var5 = Item.potion.getEffects(par1ItemStack);
         HashMultimap var6 = HashMultimap.create();
         Iterator var16;
         if (var5 != null && !var5.isEmpty()) {
            var16 = var5.iterator();

            while(var16.hasNext()) {
               PotionEffect var8 = (PotionEffect)var16.next();
               String var9 = StatCollector.translateToLocal(var8.getEffectName()).trim();
               Potion var10 = Potion.potionTypes[var8.getPotionID()];
               Map var11 = var10.func_111186_k();
               if (var11 != null && var11.size() > 0) {
                  Iterator var12 = var11.entrySet().iterator();

                  while(var12.hasNext()) {
                     Map.Entry var13 = (Map.Entry)var12.next();
                     AttributeModifier var14 = (AttributeModifier)var13.getValue();
                     AttributeModifier var15 = new AttributeModifier(var14.getName(), var10.func_111183_a(var8.getAmplifier(), var14), var14.getOperation());
                     var6.put(((Attribute)var13.getKey()).getAttributeUnlocalizedName(), var15);
                  }
               }

               if (var8.getAmplifier() > 0) {
                  var9 = var9 + " " + StatCollector.translateToLocal("potion.potency." + var8.getAmplifier()).trim();
               }

               if (var8.getDuration() > 20) {
                  var9 = var9 + " (" + Potion.getDurationString(var8) + ")";
               }

               if (var10.isBadEffect()) {
                  par3List.add(EnumChatFormatting.RED + var9);
               } else {
                  par3List.add(EnumChatFormatting.GRAY + var9);
               }
            }
         } else {
            String var7 = StatCollector.translateToLocal("potion.empty").trim();
            par3List.add(EnumChatFormatting.GRAY + var7);
         }

         if (!par4) {
            return;
         }

         if (!var6.isEmpty()) {
            par3List.add("");
            par3List.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
            var16 = var6.entries().iterator();

            while(var16.hasNext()) {
               Map.Entry var17 = (Map.Entry)var16.next();
               AttributeModifier var18 = (AttributeModifier)var17.getValue();
               String effect_details = getEffectDetails((String)var17.getKey(), var18);
               if (effect_details != null) {
                  par3List.add(effect_details);
               }
            }
         }
      }

   }

   public static String getEffectDetails(String attribute_name, AttributeModifier attribute_modifier) {
      double var19 = attribute_modifier.getAmount();
      double var20;
      if (attribute_modifier.getOperation() != 1 && attribute_modifier.getOperation() != 2) {
         var20 = attribute_modifier.getAmount();
      } else {
         var20 = attribute_modifier.getAmount() * 100.0;
      }

      if (var19 > 0.0) {
         return EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attribute_modifier.getOperation(), ItemStack.field_111284_a.format(var20), StatCollector.translateToLocal("attribute.name." + attribute_name));
      } else if (var19 < 0.0) {
         var20 *= -1.0;
         return EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attribute_modifier.getOperation(), ItemStack.field_111284_a.format(var20), StatCollector.translateToLocal("attribute.name." + attribute_name));
      } else {
         return null;
      }
   }

   public boolean hasEffect(ItemStack par1ItemStack) {
      List var2 = this.getEffects(par1ItemStack);
      return var2 != null && !var2.isEmpty();
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      super.getSubItems(par1, par2CreativeTabs, par3List);
      int var5;
      if (field_77835_b.isEmpty()) {
         for(int var4 = 0; var4 <= 15; ++var4) {
            for(var5 = 0; var5 <= 1; ++var5) {
               int var6;
               if (var5 == 0) {
                  var6 = var4 | 8192;
               } else {
                  var6 = var4 | 16384;
               }

               for(int var7 = 0; var7 <= 2; ++var7) {
                  int var8 = var6;
                  if (var7 != 0) {
                     if (var7 == 1) {
                        var8 = var6 | 32;
                     } else if (var7 == 2) {
                        var8 = var6 | 64;
                     }
                  }

                  List var9 = PotionHelper.getPotionEffects(var8, false);
                  if (var9 != null && !var9.isEmpty()) {
                     field_77835_b.put(var9, var8);
                  }
               }
            }
         }
      }

      Iterator var10 = field_77835_b.values().iterator();

      while(var10.hasNext()) {
         var5 = (Integer)var10.next();
         par3List.add(new ItemStack(par1, 1, var5));
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_94590_d = par1IconRegister.registerIcon(this.getIconString() + "_" + "bottle_drinkable");
      this.field_94591_c = par1IconRegister.registerIcon(this.getIconString() + "_" + "bottle_splash");
      this.field_94592_ct = par1IconRegister.registerIcon(this.getIconString() + "_" + "overlay");
   }

   public static Icon func_94589_d(String par0Str) {
      return par0Str.equals("bottle_drinkable") ? Item.potion.field_94590_d : (par0Str.equals("bottle_splash") ? Item.potion.field_94591_c : (par0Str.equals("overlay") ? Item.potion.field_94592_ct : null));
   }

   public int getSimilarityToItem(Item item) {
      return item == glassBottle ? 1 : super.getSimilarityToItem(item);
   }
}
