package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityAnimalWatcher;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.world.WorldServer;

public class EntityAINearestAttackableTarget extends EntityAITarget {
   private final Class targetClass;
   private final int targetChance;
   private final EntityAINearestAttackableTargetSorter theNearestAttackableTargetSorter;
   private final IEntitySelector targetEntitySelector;
   private EntityLivingBase targetEntity;

   public EntityAINearestAttackableTarget(EntityCreature par1EntityCreature, Class par2Class, int par3, boolean par4) {
      this(par1EntityCreature, par2Class, par3, par4, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature par1EntityCreature, Class par2Class, int par3, boolean par4, boolean par5) {
      this(par1EntityCreature, par2Class, par3, par4, par5, (IEntitySelector)null);
   }

   public EntityAINearestAttackableTarget(EntityCreature par1EntityCreature, Class par2Class, int par3, boolean par4, boolean par5, IEntitySelector par6IEntitySelector) {
      super(par1EntityCreature, par4, par5);
      this.targetClass = par2Class;
      this.targetChance = par3;
      this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTargetSorter(par1EntityCreature);
      this.setMutexBits(1);
      this.targetEntitySelector = new EntityAINearestAttackableTargetSelector(this, par6IEntitySelector);
   }

   private EntityLivingBase getNearestAttackableTarget() {
      double var1 = (double)this.taskOwner.getMaxTargettingRange();
      float y_range = 6.0F;
      if (this.taskOwner instanceof EntityZombie && this.targetClass == EntityAnimal.class) {
         var1 /= 2.0;
         y_range /= 2.0F;
      }

      List var3 = this.taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(var1, (double)y_range, var1), this.targetEntitySelector);
      Collections.sort(var3, this.theNearestAttackableTargetSorter);
      if (var3.isEmpty() && this.targetClass == EntityPlayer.class) {
         WorldServer world_server = (WorldServer)this.taskOwner.worldObj;
         if (this.taskOwner instanceof EntityAnimalWatcher) {
            EntityAnimalWatcher entity_digger = (EntityAnimalWatcher)this.taskOwner;
            if (entity_digger.isDiggingEnabled() && entity_digger.isOutdoors()) {
               Iterator i = world_server.playerEntities.iterator();

               while(i.hasNext()) {
                  EntityPlayer entity_player = (EntityPlayer)i.next();
                  if (!entity_player.isGhost() && !entity_player.isZevimrgvInTournament()) {
                     double dx = entity_player.posX - this.taskOwner.posX;
                     double dy = entity_player.posY - this.taskOwner.posY;
                     double dz = entity_player.posZ - this.taskOwner.posZ;
                     if (!entity_player.capabilities.isCreativeMode && dy >= (double)y_range && dy <= 14.0 && dx * dx + dz * dz < 16.0) {
                        var3.add(entity_player);
                     }
                  }
               }

               Collections.sort(var3, this.theNearestAttackableTargetSorter);
            }
         } else if (this.taskOwner instanceof EntitySkeleton && this.taskOwner.getHeldItemStack() != null && this.taskOwner.getHeldItemStack().getItem() instanceof ItemBow) {
            Iterator i = world_server.playerEntities.iterator();

            while(i.hasNext()) {
               EntityPlayer entity_player = (EntityPlayer)i.next();
               if (!entity_player.isGhost() && !entity_player.isZevimrgvInTournament()) {
                  double dx = entity_player.posX - this.taskOwner.posX;
                  double dy = entity_player.posY - this.taskOwner.posY;
                  double dz = entity_player.posZ - this.taskOwner.posZ;
                  if (!entity_player.capabilities.isCreativeMode && dy >= (double)y_range && dy <= 20.0 && dx * dx + dz * dz < var1 * var1 && this.taskOwner.getEntitySenses().canSee(entity_player, entity_player == this.taskOwner.getAttackTarget())) {
                     var3.add(entity_player);
                  }
               }
            }

            Collections.sort(var3, this.theNearestAttackableTargetSorter);
         }
      }

      if (var3.isEmpty()) {
         return null;
      } else {
         int size = var3.size();
         int i = 0;

         EntityLivingBase potential_target;
         while(true) {
            if (i >= size) {
               return null;
            }

            potential_target = (EntityLivingBase)var3.get(i);
            if (potential_target instanceof EntityPlayer) {
               if (!((EntityPlayer)potential_target).isGhost() && !potential_target.isZevimrgvInTournament()) {
                  break;
               }
            } else if ((!(potential_target instanceof EntityHellhound) || this.targetClass != EntityAnimal.class) && (!(potential_target instanceof EntityWolf) || !(this.taskOwner instanceof EntityZombie) || !(this.taskOwner.getDistanceToEntity(potential_target) > 8.0F))) {
               break;
            }

            ++i;
         }

         return this.isOnAttackableMount(potential_target) ? potential_target.ridingEntity.getAsEntityLivingBase() : potential_target;
      }
   }

   public boolean shouldExecute() {
      if (this.taskOwner.AI_retarget != null) {
         if (!this.taskOwner.AI_retarget.isDead) {
            this.targetEntity = this.taskOwner.AI_retarget;
            this.taskOwner.AI_retarget = null;
            return true;
         }

         this.taskOwner.AI_retarget = null;
      }

      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else {
         this.targetEntity = this.getNearestAttackableTarget();
         return this.targetEntity != null;
      }
   }

   public void updateTask() {
      if (this.taskOwner.rand.nextInt(40) == 0) {
         EntityLivingBase nearest_attackable_target = this.getNearestAttackableTarget();
         if (nearest_attackable_target != null && nearest_attackable_target != this.targetEntity && this.taskOwner.getEntitySenses().canSee(nearest_attackable_target)) {
            this.resetTask();
            this.targetEntity = nearest_attackable_target;
            this.startExecuting();
            return;
         }
      }

      if (this.isOnAttackableMount(this.targetEntity)) {
         this.targetEntity = this.targetEntity.ridingEntity.getAsEntityLivingBase();
      }

      super.updateTask();
   }

   public void startExecuting() {
      if (this.isOnAttackableMount(this.targetEntity)) {
         this.targetEntity = this.targetEntity.ridingEntity.getAsEntityLivingBase();
      }

      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   private boolean isOnAttackableMount(EntityLivingBase entity_living_base) {
      return entity_living_base != null && entity_living_base.ridingEntity instanceof EntityLivingBase && !entity_living_base.ridingEntity.isEntityInvulnerable();
   }
}
