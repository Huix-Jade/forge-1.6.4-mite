package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Translator;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayerFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.BonemealEvent;

public class ItemDye extends Item {
   public static final String[] dyeColorNames = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
   public static final String[] dyeItemNames = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
   public static final int[] dyeColors = new int[]{1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};
   private Icon[] dyeIcons;
   public static boolean suppress_standard_particle_effect;
   public static final int BLACK = 0;
   public static final int RED = 1;
   public static final int GREEN = 2;
   public static final int BROWN = 3;
   public static final int BLUE = 4;
   public static final int PURPLE = 5;
   public static final int CYAN = 6;
   public static final int SILVER = 7;
   public static final int GRAY = 8;
   public static final int PINK = 9;
   public static final int LIME = 10;
   public static final int YELLOW = 11;
   public static final int LIGHT_BLUE = 12;
   public static final int MAGENTA = 13;
   public static final int ORANGE = 14;
   public static final int WHITE = 15;

   public ItemDye(int par1) {
      super(par1, Material.dye, "dye_powder");
      this.setCreativeTab(CreativeTabs.tabMaterials);
      this.setCraftingDifficultyAsComponent(25.0F);
   }

   public Icon getIconFromSubtype(int par1) {
      int var2 = MathHelper.clamp_int(par1, 0, 15);
      return this.dyeIcons[var2];
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return super.getUnlocalizedName();
      } else {
         int var2 = MathHelper.clamp_int(par1ItemStack.getItemSubtype(), 0, 15);
         return super.getUnlocalizedName() + "." + dyeColorNames[var2];
      }
   }

   public int getExperienceReward(int subtype) {
      return subtype == 4 ? 5 : 0;
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntitySheep) {
         EntitySheep sheep = (EntitySheep)entity;
         if (sheep.tryDyeing(item_stack)) {
            if (player.onServer() && !player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }

            return true;
         }
      }

      return false;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         World world = rc.world;
         int x = rc.block_hit_x;
         int y = rc.block_hit_y;
         int z = rc.block_hit_z;
         ItemStack item_stack = player.getHeldItemStack();
         if (!player.canPlayerEdit(x, y, z, item_stack)) {
            return false;
         } else {
            if (item_stack.getItemSubtype() == 15) {
               if (tryFertilize(item_stack, world, x, y, z, rc.face_hit, player)) {
                  if (player.onServer() && !player.inCreativeMode()) {
                     player.convertOneOfHeldItem((ItemStack)null);
                  }

                  if (suppress_standard_particle_effect) {
                     suppress_standard_particle_effect = false;
                     return true;
                  }

                  if (player.onServer()) {
                     Block block = rc.getBlockHit();
                     if (block == Block.grass || block == Block.tilledField || block == Block.mycelium) {
                        ++y;
                     }

                     world.playAuxSFX(2005, x, y, z, 0);
                  }

                  if (world.getBlock(x, y, z) instanceof BlockCrops) {
                     player.triggerAchievement(AchievementList.plantDoctor);
                  }

                  return true;
               }
            } else if (item_stack.getItemSubtype() == 3) {
               return player.tryPlaceHeldItemAsBlock(rc, Block.cocoaPlant);
            }

            return false;
         }
      } else {
         ItemStack item_stack = player.getHeldItemStack();
         return item_stack.getItemSubtype() == 4 ? ItemRock.onItemRightClick(player, item_stack, partial_tick, ctrl_is_down) : false;
      }
   }


   public static boolean tryFertilize(ItemStack par0ItemStack, World par1World, int par2, int par3, int par4, EnumFace face)
   {
      return tryFertilize(par0ItemStack, par1World, par2, par3, par4, face, FakePlayerFactory.getMinecraft(par1World));
   }

   public static boolean tryFertilize(ItemStack item_stack, World world, int x, int y, int z, EnumFace face, EntityPlayer player) {
      Block block = Block.blocksList[world.getBlockId(x, y, z)];
      BonemealEvent event = new BonemealEvent(player, world, block.blockID, x, y, z);
      if (MinecraftForge.EVENT_BUS.post(event))
      {
         return false;
      }

      if (event.getResult() == Event.Result.ALLOW)
      {
         if (!world.isRemote)
         {
            item_stack.stackSize--;
         }
         return true;
      }

      world.getBlockMetadata(x, y, z);
      if (block == Block.grass) {
         BlockGrass grass = (BlockGrass)block;
         return grass.fertilize(world, x, y, z, item_stack);
      } else if (block instanceof BlockCrops) {
         BlockCrops crops = (BlockCrops)block;
          return crops.fertilize(world, x, y, z, item_stack);
      } else {
         return false;
      }
   }

   public static void func_96603_a(World par0World, int par1, int par2, int par3, int par4) {
      int var5 = par0World.getBlockId(par1, par2, par3);
      if (par4 == 0) {
         par4 = 15;
      }

      Block var6 = var5 > 0 && var5 < Block.blocksList.length ? Block.blocksList[var5] : null;
      if (var6 != null) {
         var6.setBlockBoundsBasedOnStateAndNeighbors(par0World, par1, par2, par3);
      }

      int index = Minecraft.getThreadIndex();

      for(int var7 = 0; var7 < par4; ++var7) {
         double var8 = itemRand.nextGaussian() * 0.02;
         double var10 = itemRand.nextGaussian() * 0.02;
         double var12 = itemRand.nextGaussian() * 0.02;
         par0World.spawnParticle(EnumParticle.happyVillager, (double)((float)par1 + itemRand.nextFloat()), (double)par2 + (double)itemRand.nextFloat() * (var6 == null ? 1.0 : var6.getBlockBoundsMaxY(index)), (double)((float)par3 + itemRand.nextFloat()), var8, var10, var12);
      }

   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      for(int var4 = 0; var4 < 16; ++var4) {
         par3List.add(new ItemStack(par1, 1, var4));
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.dyeIcons = new Icon[dyeItemNames.length];

      for(int var2 = 0; var2 < dyeItemNames.length; ++var2) {
         this.dyeIcons[var2] = par1IconRegister.registerIcon(this.getIconString() + "_" + dyeItemNames[var2]);
      }

   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      int xp_value = ItemRock.getExperienceValueWhenSacrificed(item_stack);
      if (extended_info && xp_value > 0) {
         info.add(EnumChatFormatting.LIGHT_GRAY + Translator.getFormatted("item.tooltip.XPEach", xp_value));
      }

   }
}
