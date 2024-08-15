package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialSoup;
import net.minecraft.block.material.MaterialStew;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.world.World;

public class ItemBowl extends ItemVessel {
   public ItemBowl(int id, Material contents, String texture) {
      super(id, Material.wood, contents, 1, 16, 4, "bowls/" + texture);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      if (player.onServer()) {
         if (this.contains(Material.milk)) {
            player.clearActivePotions();
         }

         player.addFoodValue(this);
         if (this.isEatable(item_stack)) {
            world.playSoundAtEntity(player, "random.burp", 0.5F, player.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

      super.onItemUseFinish(item_stack, world, player);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public EnumItemInUseAction getItemInUseAction(ItemStack item_stack, EntityPlayer player) {
      return !this.isIngestable(item_stack) ? null : super.getItemInUseAction(item_stack, player);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         if (this.isEmpty()) {
            if (rc.getBlockHitMaterial() == Material.water || rc.getNeighborOfBlockHitMaterial() == Material.water) {
               if (player.onServer()) {
                  player.convertOneOfHeldItem(new ItemStack(this.getPeerForContents(Material.water)));
               }

               return true;
            }
         } else {
            if (rc.getNeighborOfBlockHit() == Block.fire && this.canContentsDouseFire()) {
               if (player.onServer()) {
                  rc.world.douseFire(rc.neighbor_block_x, rc.neighbor_block_y, rc.neighbor_block_z, (Entity)null);
                  player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
               }

               return true;
            }

            if (this.contains(Material.water)) {
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
                     player.convertOneOfHeldItem(new ItemStack(this.getEmptyVessel()));
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public int getSimilarityToItem(Item item) {
      if (item instanceof ItemBowl) {
         ItemBowl item_bowl = (ItemBowl)item;
         if (item_bowl.isEmpty() || this.isEmpty()) {
            return 2;
         }
      }

      return super.getSimilarityToItem(item);
   }

   public ItemBowl setAnimalProduct() {
      super.setAnimalProduct();
      return this;
   }

   public ItemBowl setPlantProduct() {
      super.setPlantProduct();
      return this;
   }

   public int getBurnTime(ItemStack item_stack) {
      return this.isEmpty() ? 200 : 0;
   }

   public static ItemVessel getPeer(Material vessel_material, Material contents) {
      if (vessel_material == Material.wood) {
         if (contents == null) {
            return bowlEmpty;
         }

         if (contents == Material.mushroom_stew) {
            return bowlMushroomStew;
         }

         if (contents == Material.milk) {
            return bowlMilk;
         }

         if (contents == Material.water) {
            return bowlWater;
         }

         if (contents == Material.beef_stew) {
            return bowlBeefStew;
         }

         if (contents == Material.chicken_soup) {
            return bowlChickenSoup;
         }

         if (contents == Material.vegetable_soup) {
            return bowlVegetableSoup;
         }

         if (contents == Material.ice_cream) {
            return bowlIceCream;
         }

         if (contents == Material.salad) {
            return bowlSalad;
         }

         if (contents == Material.cream_of_mushroom_soup) {
            return bowlCreamOfMushroomSoup;
         }

         if (contents == Material.cream_of_vegetable_soup) {
            return bowlCreamOfVegetableSoup;
         }

         if (contents == Material.mashed_potato) {
            return bowlMashedPotato;
         }

         if (contents == Material.porridge) {
            return bowlPorridge;
         }

         if (contents == Material.cereal) {
            return bowlCereal;
         }
      }

      return null;
   }

   public ItemVessel getPeerForContents(Material contents) {
      return getPeer(this.getVesselMaterial(), contents);
   }

   public ItemVessel getPeerForVesselMaterial(Material vessel_material) {
      return getPeer(vessel_material, this.getContents());
   }

   public boolean hasIngestionPriority(ItemStack item_stack, boolean ctrl_is_down) {
      return !this.contains(Material.water);
   }

   public static boolean isSoupOrStew(Item item) {
      if (!(item instanceof ItemBowl)) {
         return false;
      } else {
         Material contents = ((ItemBowl)item).getContents();
         return contents instanceof MaterialSoup || contents instanceof MaterialStew;
      }
   }

   public float getCompostingValue() {
      return this == bowlMilk ? 0.0F : super.getCompostingValue();
   }

   public Item getCompostingRemains(ItemStack item_stack) {
      return this.getEmptyVessel();
   }
}
