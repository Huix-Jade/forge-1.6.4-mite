package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemLeash extends Item {
   public ItemLeash(int par1) {
      super(par1, new Material[]{Material.silk, Material.slime}, "lead");
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      if (entity instanceof EntityLiving) {
         EntityLiving entity_living = entity.getAsEntityLiving();
         if (entity_living.allowLeashing() && !entity_living.getLeashed()) {
            boolean leashing_prevented = false;
            if (!leashing_prevented) {
               if (player.onClient()) {
                  player.suppressNextArmSwing();
               } else {
                  entity_living.setLeashedToEntity(player, true);
                  if (!player.inCreativeMode()) {
                     player.convertOneOfHeldItem((ItemStack)null);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   public static boolean tryTieingLeashedEntitiesToBlock(EntityPlayer player, int x, int y, int z) {
      List leashed_entities = getEntitiesThatAreLeashedToEntity(player);
      if (leashed_entities.size() == 0) {
         return false;
      } else if (player.onClient()) {
         return true;
      } else {
         World world = player.getWorld();
         EntityLeashKnot knot = EntityLeashKnot.getKnotForBlock(world, x, y, z);
         if (knot == null) {
            knot = EntityLeashKnot.func_110129_a(world, x, y, z);
         }

         return leashEntitiesToEntity(leashed_entities, knot, true);
      }
   }

   public static List getEntitiesThatAreLeashedToEntity(Entity entity) {
      World world = entity.worldObj;
      float radius = 7.0F;
      List nearby_living_entities = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(entity.posX - (double)radius, entity.posY - (double)radius, entity.posZ - (double)radius, entity.posX + (double)radius, entity.posY + (double)radius, entity.posZ + (double)radius));
      if (nearby_living_entities == null) {
         return null;
      } else {
         Iterator i = nearby_living_entities.iterator();

         while(true) {
            EntityLiving entity_living;
            do {
               if (!i.hasNext()) {
                  return nearby_living_entities;
               }

               entity_living = (EntityLiving)i.next();
            } while(entity_living.getLeashed() && entity_living.getLeashedToEntity() == entity);

            i.remove();
         }
      }
   }

   public static boolean unleashEntitiesThatAreLeashedToEntity(Entity entity, boolean drop_leash_items, boolean send_packet_to_tracking_players) {
      List leashed_entities = getEntitiesThatAreLeashedToEntity(entity);
      if (entity.onServer()) {
         for(int i = 0; i < leashed_entities.size(); ++i) {
            EntityLiving entity_living = (EntityLiving)leashed_entities.get(i);
            entity_living.clearLeashed(drop_leash_items, send_packet_to_tracking_players);
         }
      }

      return leashed_entities.size() > 0;
   }

   public static boolean leashEntitiesToEntity(List leashed_entities, Entity entity, boolean send_packet_to_tracking_players) {
      if (entity.onServer()) {
         for(int i = 0; i < leashed_entities.size(); ++i) {
            EntityLiving entity_living = (EntityLiving)leashed_entities.get(i);
            entity_living.setLeashedToEntity(entity, send_packet_to_tracking_players);
         }
      }

      return leashed_entities.size() > 0;
   }

   public static boolean transferLeashedEntitiesToAnotherEntity(Entity source_entity, Entity target_entity, boolean send_packet_to_tracking_players) {
      return leashEntitiesToEntity(getEntitiesThatAreLeashedToEntity(source_entity), target_entity, send_packet_to_tracking_players);
   }
}
