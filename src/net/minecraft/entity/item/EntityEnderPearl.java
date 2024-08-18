package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityEnderPearl extends EntityThrowable {
   public EntityEnderPearl(World par1World) {
      super(par1World);
   }

   public EntityEnderPearl(World par1World, EntityLivingBase par2EntityLivingBase) {
      super(par1World, par2EntityLivingBase);
   }

   public EntityEnderPearl(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   protected void onImpact(RaycastCollision rc) {
      if (rc.isEntity()) {
         rc.getEntityHit().attackEntityFrom((new Damage(DamageSource.causeThrownDamage(this, this.getThrower()), 1.0F)).setKnockbackOnly());
      }

      for(int var2 = 0; var2 < 32; ++var2) {
         this.worldObj.spawnParticle(EnumParticle.portal_underworld, this.posX, this.posY + this.rand.nextDouble() * 2.0, this.posZ, this.rand.nextGaussian(), 0.0, this.rand.nextGaussian());
      }

      if (!this.worldObj.isRemote) {
         if (this.getThrower() != null && this.getThrower() instanceof EntityPlayerMP) {
            EntityPlayerMP var3 = (EntityPlayerMP)this.getThrower();
             if (!var3.playerNetServerHandler.connectionClosed && var3.worldObj == this.worldObj) {
               EnderTeleportEvent event = new EnderTeleportEvent(var3, this.posX, this.posY, this.posZ, 5.0F);
               if (!MinecraftForge.EVENT_BUS.post(event)) {
                  if (this.getThrower().isRiding())
                  {
                     this.getThrower().mountEntity((Entity)null);
                  }

                  this.getThrower().setPositionAndUpdate(this.posX, this.posY, this.posZ);
                  this.getThrower().fallDistance = 0.0F;
                  this.playSound("mob.endermen.portal", 1.0F, 1.0F);
               }
            }
         }

         this.setDead();
      }

   }

   public Item getModelItem() {
      return Item.enderPearl;
   }
}
