package net.minecraft.entity.item;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingSand extends Entity {
   public int blockID;
   public int metadata;
   public int item_damage;
   public String custom_name;
   public int fallTime;
   public boolean shouldDropItem;
   private boolean isBreakingAnvil;
   private boolean isAnvil;
   private int fallHurtMax;
   private float fallHurtAmount;
   public NBTTagCompound fallingBlockTileEntityData;

   public EntityFallingSand(World par1World) {
      super(par1World);
      this.shouldDropItem = true;
      this.fallHurtMax = 40;
      this.fallHurtAmount = 2.0F;
   }

   public EntityFallingSand(World par1World, double par2, double par4, double par6, int par8) {
      this(par1World, par2, par4, par6, par8, 0);
   }

   public EntityFallingSand(World par1World, double par2, double par4, double par6, int par8, int par9) {
      super(par1World);
      this.shouldDropItem = true;
      this.fallHurtMax = 40;
      this.fallHurtAmount = 2.0F;
      this.blockID = par8;
      this.metadata = par9;
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      this.yOffset = this.height / 2.0F;
      this.setPosition(par2, par4, par6);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = par2;
      this.prevPosY = par4;
      this.prevPosZ = par6;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void entityInit() {
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public static boolean canDislodgeOrCrushBlockAt(World world, Block falling_block, int falling_block_metadata, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      return block.isDislodgedOrCrushedByFallingBlock(world.getBlockMetadata(x, y, z), falling_block, falling_block_metadata);
   }

   private void checkForBlockOccupyingSameSpace(int x, int y, int z) {
      Block block_occupying_same_space = this.worldObj.getBlock(x, y, z);
      if (block_occupying_same_space != null && !block_occupying_same_space.blockMaterial.isLiquid()) {
         if (block_occupying_same_space != null && canDislodgeOrCrushBlockAt(this.worldObj, Block.getBlock(this.blockID), this.metadata, x, y, z)) {
            BlockBreakInfo info = new BlockBreakInfo(this.worldObj, x, y, z);
            if (Block.sand.canFallDownTo(this.worldObj, x, y - 1, z, this.metadata)) {
               info.setCollidedWith(this);
            } else {
               info.setCrushed(Block.getBlock(this.blockID));
            }

            if (block_occupying_same_space.dropBlockAsEntityItem(info) > 0 && info.wasCollidedWithEntity()) {
               this.playSound("random.pop", 0.3F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.worldObj.setBlockToAir(x, y, z);
         }

      }
   }

   public void onUpdate() {
      if (this.blockID == 0) {
         this.setDead();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         ++this.fallTime;
         this.motionY -= 0.03999999910593033;
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9800000190734863;
         this.motionY *= 0.9800000190734863;
         this.motionZ *= 0.9800000190734863;
         Material material = this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
         if (material == Material.water) {
            if (!this.inWater && !this.firstUpdate && this.motionY < -0.20000000298023224 && !this.worldObj.isRemote) {
               this.entityFX(EnumEntityFX.splash);
            }

            this.inWater = true;
         }

         if (!this.worldObj.isRemote) {
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY);
            int var3 = MathHelper.floor_double(this.posZ);
            if (this.fallTime == 1) {
               if (this.worldObj.getBlockId(var1, var2, var3) != this.blockID) {
                  this.setDead();
                  return;
               }

               this.worldObj.setBlockToAir(var1, var2, var3);
            }

            this.checkForBlockOccupyingSameSpace(var1, var2, var3);
            if (this.onGround && this.worldObj.getBlockMaterial(var1, var2 - 1, var3) == Material.glass) {
               this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, var1, var2 - 1, var3)).setCollidedWith(this), true);
            } else if (this.onGround) {
               this.motionX *= 0.699999988079071;
               this.motionZ *= 0.699999988079071;
               this.motionY *= -0.5;
               var2 = MathHelper.floor_double(this.posY - (double)this.yOffset);
               this.checkForBlockOccupyingSameSpace(var1, var2, var3);
               if (this.worldObj.getBlockId(var1, var2, var3) != Block.pistonMoving.blockID) {
                  this.setDead();
                  Block block = Block.getBlock(this.blockID);
                  if (!this.isBreakingAnvil && !Block.sand.canFallDownTo(this.worldObj, var1, var2 - 1, var3, this.metadata) && (this.worldObj.isAirBlock(var1, var2, var3) || canDislodgeOrCrushBlockAt(this.worldObj, block, this.metadata, var1, var2, var3)) && this.worldObj.setBlock(var1, var2, var3, this.blockID, this.metadata, 3)) {
                     if (block != null && !(block instanceof BlockAnvil)) {
                        this.worldObj.playSoundEffect((double)((float)var1 + 0.5F), (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getPitch() * 0.8F);
                     }

                     if (Block.blocksList[this.blockID] instanceof BlockFalling) {
                        ((BlockFalling)Block.blocksList[this.blockID]).onFinishFalling(this.worldObj, var1, var2, var3, this.metadata, this);
                     }

                     Block block_below = this.worldObj.getBlock(var1, var2 - 1, var3);
                     if (block_below != null) {
                        block_below.onEntityWalking(this.worldObj, var1, var2 - 1, var3, this);
                     }

                     if (this.fallingBlockTileEntityData != null && Block.blocksList[this.blockID] instanceof ITileEntityProvider) {
                        TileEntity var4 = this.worldObj.getBlockTileEntity(var1, var2, var3);
                        if (var4 != null) {
                           NBTTagCompound var5 = new NBTTagCompound();
                           var4.writeToNBT(var5);
                           Iterator var6 = this.fallingBlockTileEntityData.getTags().iterator();

                           while(var6.hasNext()) {
                              NBTBase var7 = (NBTBase)var6.next();
                              if (!var7.getName().equals("x") && !var7.getName().equals("y") && !var7.getName().equals("z")) {
                                 var5.setTag(var7.getName(), var7.copy());
                              }
                           }

                           var4.readFromNBT(var5);
                           var4.onInventoryChanged();
                        }
                     }
                  } else if (this.shouldDropItem && !this.isBreakingAnvil) {
                     ItemStack item_stack = new ItemStack(this.blockID, 1, Block.blocksList[this.blockID].getItemSubtype(this.metadata));
                     if (this.item_damage != 0) {
                        item_stack.setItemDamage(this.item_damage);
                     }

                     if (this.custom_name != null) {
                        item_stack.setItemName(this.custom_name);
                     }

                     this.dropItemStack(item_stack, 0.0F);
                  }
               }
            } else if (this.fallTime > 100 && !this.worldObj.isRemote && (var2 < 1 || var2 > 256) || this.fallTime > 600) {
               if (this.shouldDropItem) {
                  ItemStack item_stack = new ItemStack(this.blockID, 1, Block.blocksList[this.blockID].getItemSubtype(this.metadata));
                  if (this.item_damage != 0) {
                     item_stack.setItemDamage(this.item_damage);
                  }

                  if (this.custom_name != null) {
                     item_stack.setItemName(this.custom_name);
                  }

                  this.dropItemStack(item_stack, 0.0F);
               }

               this.setDead();
            }
         }
      }

      this.firstUpdate = false;
   }

   protected void fall(float par1) {
      if (this.isAnvil) {
         int var2 = MathHelper.ceiling_float_int(par1 - 1.0F);
         if (var2 > 0) {
            ArrayList var3 = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
            Block block = Block.blocksList[this.blockID];
            DamageSource var4 = block instanceof BlockAnvil ? DamageSource.anvil : DamageSource.fallingBlock;
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Entity var6 = (Entity)var5.next();
               var6.attackEntityFrom(new Damage(var4, (float)Math.min(MathHelper.floor_float((float)var2 * this.fallHurtAmount), this.fallHurtMax)));
            }
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setByte("Tile", (byte)this.blockID);
      par1NBTTagCompound.setInteger("TileID", this.blockID);
      par1NBTTagCompound.setByte("Data", (byte)this.metadata);
      par1NBTTagCompound.setByte("Time", (byte)this.fallTime);
      par1NBTTagCompound.setBoolean("DropItem", this.shouldDropItem);
      par1NBTTagCompound.setBoolean("HurtEntities", this.isAnvil);
      par1NBTTagCompound.setFloat("FallHurtAmount", this.fallHurtAmount);
      par1NBTTagCompound.setInteger("FallHurtMax", this.fallHurtMax);
      if (this.isAnvil) {
         par1NBTTagCompound.setInteger("item_damage", this.item_damage);
      }

      if (this.custom_name != null) {
         par1NBTTagCompound.setString("custom_name", this.custom_name);
      }

      if (this.fallingBlockTileEntityData != null) {
         par1NBTTagCompound.setCompoundTag("TileEntityData", this.fallingBlockTileEntityData);
      }

   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      if (par1NBTTagCompound.hasKey("TileID")) {
         this.blockID = par1NBTTagCompound.getInteger("TileID");
      } else {
         this.blockID = par1NBTTagCompound.getByte("Tile") & 255;
      }

      this.metadata = par1NBTTagCompound.getByte("Data") & 255;
      this.fallTime = par1NBTTagCompound.getByte("Time") & 255;
      if (par1NBTTagCompound.hasKey("HurtEntities")) {
         this.isAnvil = par1NBTTagCompound.getBoolean("HurtEntities");
         this.fallHurtAmount = par1NBTTagCompound.getFloat("FallHurtAmount");
         this.fallHurtMax = par1NBTTagCompound.getInteger("FallHurtMax");
      } else if (Block.blocksList[this.blockID] instanceof BlockAnvil) {
         this.isAnvil = true;
      }

      if (this.isAnvil) {
         this.item_damage = par1NBTTagCompound.getInteger("item_damage");
      }

      if (par1NBTTagCompound.hasKey("custom_name")) {
         this.custom_name = par1NBTTagCompound.getString("custom_name");
      }

      if (par1NBTTagCompound.hasKey("DropItem")) {
         this.shouldDropItem = par1NBTTagCompound.getBoolean("DropItem");
      }

      if (par1NBTTagCompound.hasKey("TileEntityData")) {
         this.fallingBlockTileEntityData = par1NBTTagCompound.getCompoundTag("TileEntityData");
      }

      if (this.blockID == 0) {
         this.blockID = Block.sand.blockID;
      }

   }

   public float getShadowSize() {
      return 0.0F;
   }

   public void setIsAnvil(boolean par1) {
      this.isAnvil = par1;
   }

   public boolean canRenderOnFire() {
      return false;
   }

   public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory) {
      super.addEntityCrashInfo(par1CrashReportCategory);
      par1CrashReportCategory.addCrashSection("Immitating block ID", this.blockID);
      par1CrashReportCategory.addCrashSection("Immitating block data", this.metadata);
   }

   public boolean canCatchFire() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }
}
