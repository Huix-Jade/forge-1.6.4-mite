package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDireWolf;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.DamageSource;

public class ItemFlintAndSteel extends Item implements IDamageableItem {
   public ItemFlintAndSteel(int par1) {
      super(par1, Material.flint, "flint_and_steel");
      this.addMaterial(new Material[]{Material.iron});
      this.setMaxStackSize(1);
      this.setMaxDamage(16);
      this.setCreativeTab(CreativeTabs.tabTools);
      this.setSkillsetThatCanRepairThis(-1);
   }

   public int getNumComponentsForDurability() {
      return 1;
   }

   public int getRepairCost() {
      return 0;
   }

   private void makeIgniteSoundAndApplyDamage(EntityPlayer player) {
      if (player.onClient()) {
         Minecraft.setErrorMessage("makeIgniteSoundAndApplyDamage: not meant to be called on client");
      } else {
         player.worldObj.playSoundAtEntity(player, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
         player.tryDamageHeldItem(DamageSource.generic, 1);
      }
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (player.onServer()) {
         boolean entity_can_be_ignited = false;
         if (entity instanceof EntityChicken) {
            entity_can_be_ignited = true;
         } else if (entity instanceof EntitySheep) {
            EntitySheep sheep = (EntitySheep)entity;
            if (!sheep.getSheared()) {
               entity_can_be_ignited = true;
            }
         } else if (entity instanceof EntityWolf) {
            if (entity instanceof EntityHellhound) {
               entity.getAsEntityLiving().setTarget(player);
            } else if (entity instanceof EntityDireWolf) {
               if (entity.getAsEntityTameable().isTamed()) {
                  entity_can_be_ignited = true;
               }

               entity.getAsEntityLiving().setTarget(player);
            } else {
               entity_can_be_ignited = true;
            }
         }

         if (entity_can_be_ignited) {
            entity.setFire(6);
         }

         this.makeIgniteSoundAndApplyDamage(player);
      }

      return true;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc == null) {
         return false;
      } else if (!rc.isBlock()) {
         return false;
      } else {
         if (rc.getBlockHit() == Block.tnt) {
            if (player.onServer()) {
               BlockTNT.ignite(rc.world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, player);
            }
         } else {
            if (!rc.isNeighborAirBlock() || !rc.canPlayerEditNeighborOfBlockHit(player, player.getHeldItemStack())) {
               return false;
            }

            if (player.onServer()) {
               rc.setNeighborBlock(Block.spark);
            }
         }

         if (player.onClient()) {
            player.swingArm();
         } else {
            rc.world.playSoundAtEntity(player, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            player.tryDamageHeldItem(DamageSource.generic, 1);
         }

         return true;
      }
   }
}
