package net.minecraft.entity.passive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIAvoidPotentialPredators;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISeekShelterFromRain;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityVillager extends EntityAgeable implements IMerchant, INpc {
   private int randomTickDivider;
   private boolean isMating;
   private boolean isPlaying;
   Village villageObj;
   private EntityPlayer buyingPlayer;
   private MerchantRecipeList buyingList;
   private int timeUntilReset;
   private boolean needsInitilization;
   private int wealth;
   private String lastBuyingPlayer;
   private boolean field_82190_bM;
   private float field_82191_bN;
   private static final Map villagerStockList = new HashMap();
   private static final Map blacksmithSellingList = new HashMap();

   public EntityVillager(World par1World) {
      this(par1World, 0);
   }

   public EntityVillager(World par1World, int par2) {
      super(par1World);
      this.setProfession(par2);
      this.setSize(0.6F, 1.8F);
      this.getNavigator().setBreakDoors(true);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6, 0.6));
      this.tasks.addTask(1, new EntityAITradePlayer(this));
      this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
      this.tasks.addTask(2, new EntityAIMoveIndoors(this, 0.6F));
      this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
      this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6));
      this.tasks.addTask(6, new EntityAIVillagerMate(this));
      this.tasks.addTask(7, new EntityAIFollowGolem(this));
      this.tasks.addTask(8, new EntityAIPlay(this, 0.32));
      this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
      this.tasks.addTask(9, new EntityAIWander(this, 0.6));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.tasks.addTask(4, new EntityAISeekShelterFromRain(this, 0.6F, true));
      this.tasks.addTask(2, new EntityAIAvoidPotentialPredators(this, 0.6F, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.5);
   }

   public boolean isAIEnabled() {
      return true;
   }

   protected void updateAITick() {
      if (--this.randomTickDivider <= 0) {
         this.worldObj.villageCollectionObj.addVillagerPosition(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
         this.randomTickDivider = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
         if (this.villageObj == null) {
            this.detachHome();
         } else {
            ChunkCoordinates var1 = this.villageObj.getCenter();
            this.setHomeArea(var1.posX, var1.posY, var1.posZ, (int)((float)this.villageObj.getVillageRadius() * 0.6F));
            if (this.field_82190_bM) {
               this.field_82190_bM = false;
               this.villageObj.func_82683_b(5);
            }
         }
      }

      if (!this.isTrading() && this.timeUntilReset > 0) {
         --this.timeUntilReset;
         if (this.timeUntilReset <= 0) {
            if (this.needsInitilization) {
               if (this.buyingList.size() > 1) {
                  Iterator var3 = this.buyingList.iterator();

                  while(var3.hasNext()) {
                     MerchantRecipe var2 = (MerchantRecipe)var3.next();
                     if (var2.func_82784_g()) {
                        var2.func_82783_a(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                     }
                  }
               }

               this.addDefaultEquipmentAndRecipies(1);
               this.needsInitilization = false;
               if (this.villageObj != null && this.lastBuyingPlayer != null) {
                  this.worldObj.setEntityState(this, EnumEntityState.villager_pleased);
                  this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
               }
            }

            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
         }
      }

      super.updateAITick();
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (super.onEntityRightClicked(player, item_stack)) {
         return true;
      } else if (this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
         if (player.onServer()) {
            this.setCustomer(player);
            player.displayGUIMerchant(this, this.getCustomNameTag());
         }

         return true;
      } else {
         return false;
      }
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("Profession", this.getProfession());
      par1NBTTagCompound.setInteger("Riches", this.wealth);
      if (this.buyingList != null) {
         par1NBTTagCompound.setCompoundTag("Offers", this.buyingList.getRecipiesAsTags());
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setProfession(par1NBTTagCompound.getInteger("Profession"));
      this.wealth = par1NBTTagCompound.getInteger("Riches");
      if (par1NBTTagCompound.hasKey("Offers")) {
         NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Offers");
         this.buyingList = new MerchantRecipeList(var2);
         Iterator i = this.buyingList.iterator();

         while(i.hasNext()) {
            MerchantRecipe recipe = (MerchantRecipe)i.next();
            if (recipe.getItemToSell().itemID == Item.expBottle.itemID) {
               recipe.setItemToSell(new ItemStack(Item.bottleOfDisenchanting));
            } else if (recipe.getItemToSell().itemID == Item.knifeCopper.itemID) {
               recipe.setItemToSell(new ItemStack(Item.daggerCopper));
            } else if (recipe.getItemToSell().itemID == Item.knifeIron.itemID) {
               recipe.setItemToSell(new ItemStack(Item.daggerIron));
            }
         }
      }

   }

   protected boolean canDespawn() {
      return false;
   }

   protected String getLivingSound() {
      return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
   }

   protected String getHurtSound() {
      return "mob.villager.hit";
   }

   protected String getDeathSound() {
      return "mob.villager.death";
   }

   public void setProfession(int par1) {
      this.dataWatcher.updateObject(16, par1);
   }

   public int getProfession() {
      return this.dataWatcher.getWatchableObjectInt(16);
   }

   public boolean isMating() {
      return this.isMating;
   }

   public void setMating(boolean par1) {
      this.isMating = par1;
   }

   public void setPlaying(boolean par1) {
      this.isPlaying = par1;
   }

   public boolean isPlaying() {
      return this.isPlaying;
   }

   public void setRevengeTarget(EntityLivingBase par1EntityLivingBase) {
      super.setRevengeTarget(par1EntityLivingBase);
      if (this.villageObj != null && par1EntityLivingBase != null) {
         this.villageObj.addOrRenewAgressor(par1EntityLivingBase);
         if (par1EntityLivingBase instanceof EntityPlayer) {
            byte var2 = -1;
            if (this.isChild()) {
               var2 = -3;
            }

            this.villageObj.setReputationForPlayer(((EntityPlayer)par1EntityLivingBase).getCommandSenderName(), var2);
            if (this.isEntityAlive()) {
               this.worldObj.setEntityState(this, EnumEntityState.villager_displeased);
            }
         }
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      if (this.villageObj != null) {
         Entity var2 = par1DamageSource.getResponsibleEntity();
         if (var2 != null) {
            if (var2 instanceof EntityPlayer) {
               this.villageObj.setReputationForPlayer(((EntityPlayer)var2).getCommandSenderName(), -2);
            } else if (var2 instanceof IMob) {
               this.villageObj.endMatingSeason();
            }
         } else if (var2 == null) {
            EntityPlayer var3 = this.worldObj.getClosestPlayerToEntity(this, 16.0, false);
            if (var3 != null) {
               this.villageObj.endMatingSeason();
            }
         }
      }

      super.onDeath(par1DamageSource);
   }

   public void setCustomer(EntityPlayer par1EntityPlayer) {
      this.buyingPlayer = par1EntityPlayer;
   }

   public EntityPlayer getCustomer() {
      return this.buyingPlayer;
   }

   public boolean isTrading() {
      return this.buyingPlayer != null;
   }

   public void useRecipe(MerchantRecipe par1MerchantRecipe) {
      par1MerchantRecipe.incrementToolUses();
      this.livingSoundTime = -this.getTalkInterval();
      this.makeSound("mob.villager.yes");
      if (par1MerchantRecipe.hasSameIDsAs((MerchantRecipe)this.buyingList.get(this.buyingList.size() - 1))) {
         this.timeUntilReset = 40;
         this.needsInitilization = true;
         if (this.buyingPlayer != null) {
            this.lastBuyingPlayer = this.buyingPlayer.getCommandSenderName();
         } else {
            this.lastBuyingPlayer = null;
         }
      }

      if (par1MerchantRecipe.getItemToBuy().itemID == Item.emerald.itemID) {
         this.wealth += par1MerchantRecipe.getItemToBuy().stackSize;
      }

   }

   public void func_110297_a_(ItemStack par1ItemStack) {
      if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
         this.livingSoundTime = -this.getTalkInterval();
         if (par1ItemStack != null) {
            this.makeSound("mob.villager.yes");
         } else {
            this.makeSound("mob.villager.no");
         }
      }

   }

   public MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer) {
      if (this.buyingList == null) {
         this.addDefaultEquipmentAndRecipies(1);
      }

      return this.buyingList;
   }

   private float adjustProbability(float par1) {
      float var2 = par1 + this.field_82191_bN;
      return var2 > 0.9F ? 0.9F - (var2 - 0.9F) : var2;
   }

   private void addDefaultEquipmentAndRecipies(int par1) {
      if (this.buyingList != null) {
         this.field_82191_bN = MathHelper.sqrt_float((float)this.buyingList.size()) * 0.2F;
      } else {
         this.field_82191_bN = 0.0F;
      }

      MerchantRecipeList var2;
      var2 = new MerchantRecipeList();
      int var6;
      label51:
      switch (this.getProfession()) {
         case 0:
            addMerchantItem(var2, Item.wheat.itemID, this.rand, this.adjustProbability(0.9F));
            addMerchantItem(var2, Block.cloth.blockID, this.rand, this.adjustProbability(0.5F));
            addMerchantItem(var2, Item.chickenRaw.itemID, this.rand, this.adjustProbability(0.5F));
            addMerchantItem(var2, Item.fishCooked.itemID, this.rand, this.adjustProbability(0.4F));
            addBlacksmithItem(var2, Item.bread.itemID, this.rand, this.adjustProbability(0.9F));
            addBlacksmithItem(var2, Item.melon.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.appleRed.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.cookie.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.shears.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.flintAndSteel.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.chickenCooked.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.arrowFlint.itemID, this.rand, this.adjustProbability(0.5F));
            if (this.rand.nextFloat() < this.adjustProbability(0.5F)) {
               var2.add(new MerchantRecipe(new ItemStack(Block.gravel, 4), new ItemStack(Item.emerald), new ItemStack(Item.flint.itemID, 4 + this.rand.nextInt(2), 0)));
            }
            break;
         case 1:
            addMerchantItem(var2, Item.paper.itemID, this.rand, this.adjustProbability(0.8F));
            addMerchantItem(var2, Item.book.itemID, this.rand, this.adjustProbability(0.8F));
            addMerchantItem(var2, Item.writtenBook.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Block.bookShelf.blockID, this.rand, this.adjustProbability(0.8F));
            addBlacksmithItem(var2, Block.glass.blockID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.compass.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.pocketSundial.itemID, this.rand, this.adjustProbability(0.2F));
            if (this.rand.nextFloat() < this.adjustProbability(0.07F)) {
               Enchantment var8 = Enchantment.enchantmentsBookList[this.rand.nextInt(Enchantment.enchantmentsBookList.length)];
               int var10 = MathHelper.getRandomIntegerInRange(this.rand, 1, var8.getNumLevels());
               ItemStack var11 = Item.enchantedBook.getEnchantedItemStack(new EnchantmentData(var8, var10));
               var6 = 2 + this.rand.nextInt(5 + var10 * 10) + 3 * var10;
               var2.add(new MerchantRecipe(new ItemStack(Item.book), new ItemStack(Item.emerald, var6), var11));
            }
            break;
         case 2:
            addBlacksmithItem(var2, Item.eyeOfEnder.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.redstone.itemID, this.rand, this.adjustProbability(0.4F));
            addBlacksmithItem(var2, Block.glowStone.blockID, this.rand, this.adjustProbability(0.3F));
            int[] var3 = new int[]{Item.swordCopper.itemID, Item.swordIron.itemID, Item.plateCopper.itemID, Item.plateIron.itemID, Item.axeCopper.itemID, Item.axeIron.itemID, Item.pickaxeCopper.itemID, Item.pickaxeIron.itemID};
            int[] var4 = var3;
            int var5 = var3.length;
            var6 = 0;

            while(true) {
               if (var6 >= var5) {
                  break label51;
               }

               int var7 = var4[var6];
               if (this.rand.nextFloat() < this.adjustProbability(0.05F)) {
                  var2.add(new MerchantRecipe(new ItemStack(var7, 1, 0), new ItemStack(Item.emerald, 2 + this.rand.nextInt(3), 0), EnchantmentHelper.addRandomEnchantment(this.rand, new ItemStack(var7, 1, 0), 5 + this.rand.nextInt(15))));
               }

               ++var6;
            }
         case 3:
            addMerchantItem(var2, Item.coal.itemID, this.rand, this.adjustProbability(0.7F));
            addMerchantItem(var2, Item.ingotIron.itemID, this.rand, this.adjustProbability(0.5F));
            addMerchantItem(var2, Item.ingotGold.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.swordIron.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.axeIron.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.pickaxeIron.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.shovelIron.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.hoeIron.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.helmetIron.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.plateIron.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.legsIron.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.bootsIron.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.pickaxeCopper.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.shovelCopper.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.axeCopper.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.hoeCopper.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.daggerCopper.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.swordCopper.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.daggerIron.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.helmetCopper.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.plateCopper.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.legsCopper.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.bootsCopper.itemID, this.rand, this.adjustProbability(0.2F));
            addBlacksmithItem(var2, Item.helmetChainCopper.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.plateChainCopper.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.legsChainCopper.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.bootsChainCopper.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.helmetChainIron.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.plateChainIron.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.legsChainIron.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.bootsChainIron.itemID, this.rand, this.adjustProbability(0.1F));
            break;
         case 4:
            addMerchantItem(var2, Item.coal.itemID, this.rand, this.adjustProbability(0.7F));
            addMerchantItem(var2, Item.porkRaw.itemID, this.rand, this.adjustProbability(0.5F));
            addMerchantItem(var2, Item.beefRaw.itemID, this.rand, this.adjustProbability(0.5F));
            addMerchantItem(var2, Item.lambchopRaw.itemID, this.rand, this.adjustProbability(0.5F));
            addBlacksmithItem(var2, Item.saddle.itemID, this.rand, this.adjustProbability(0.1F));
            addBlacksmithItem(var2, Item.plateLeather.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.bootsLeather.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.helmetLeather.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.legsLeather.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.porkCooked.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.beefCooked.itemID, this.rand, this.adjustProbability(0.3F));
            addBlacksmithItem(var2, Item.lambchopCooked.itemID, this.rand, this.adjustProbability(0.3F));
      }

      if (var2.isEmpty()) {
         addMerchantItem(var2, Item.ingotGold.itemID, this.rand, 1.0F);
      }

      Collections.shuffle(var2);
      if (this.buyingList == null) {
         this.buyingList = new MerchantRecipeList();
      }

      for(int var9 = 0; var9 < par1 && var9 < var2.size(); ++var9) {
         this.buyingList.addToListWithCheck((MerchantRecipe)var2.get(var9));
      }

   }

   public void setRecipes(MerchantRecipeList par1MerchantRecipeList) {
   }

   private static void addMerchantItem(MerchantRecipeList par0MerchantRecipeList, int par1, Random par2Random, float par3) {
      if (par2Random.nextFloat() < par3) {
         par0MerchantRecipeList.add(new MerchantRecipe(getRandomSizedStack(par1, par2Random), Item.emerald));
      }

   }

   private static ItemStack getRandomSizedStack(int par0, Random par1Random) {
      return new ItemStack(par0, getRandomCountForItem(par0, par1Random), 0);
   }

   private static int getRandomCountForItem(int par0, Random par1Random) {
      Tuple var2 = (Tuple)villagerStockList.get(par0);
      return var2 == null ? 1 : ((Integer)var2.getFirst() >= (Integer)var2.getSecond() ? (Integer)var2.getFirst() : (Integer)var2.getFirst() + par1Random.nextInt((Integer)var2.getSecond() - (Integer)var2.getFirst()));
   }

   private static void addBlacksmithItem(MerchantRecipeList par0MerchantRecipeList, int par1, Random par2Random, float par3) {
      if (par2Random.nextFloat() < par3) {
         int var4 = getRandomCountForBlacksmithItem(par1, par2Random);
         ItemStack var5;
         ItemStack var6;
         if (var4 < 0) {
            var5 = new ItemStack(Item.emerald.itemID, 1, 0);
            var6 = new ItemStack(par1, -var4, 0);
            if (var6.stackSize > var6.getMaxStackSize()) {
               var6.stackSize = var6.getMaxStackSize();
            }
         } else {
            var5 = new ItemStack(Item.emerald.itemID, var4, 0);
            var6 = new ItemStack(par1, 1, 0);
            if (var5.stackSize > var5.getMaxStackSize()) {
               var5.stackSize = var5.getMaxStackSize();
            }
         }

         par0MerchantRecipeList.add(new MerchantRecipe(var5, var6));
      }

   }

   private static int getRandomCountForBlacksmithItem(int par0, Random par1Random) {
      Tuple var2 = (Tuple)blacksmithSellingList.get(par0);
      return var2 == null ? 1 : ((Integer)var2.getFirst() >= (Integer)var2.getSecond() ? (Integer)var2.getFirst() : (Integer)var2.getFirst() + par1Random.nextInt((Integer)var2.getSecond() - (Integer)var2.getFirst()));
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.villager_mated) {
         this.generateRandomParticles(EnumParticle.heart);
      } else if (par1 == EnumEntityState.villager_displeased) {
         this.generateRandomParticles(EnumParticle.angryVillager);
      } else if (par1 == EnumEntityState.villager_pleased) {
         this.generateRandomParticles(EnumParticle.happyVillager);
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   private void generateRandomParticles(EnumParticle particle) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.rand.nextGaussian() * 0.02;
         double var5 = this.rand.nextGaussian() * 0.02;
         double var7 = this.rand.nextGaussian() * 0.02;
         this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 1.0 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var3, var5, var7);
      }

   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
      this.setProfession(this.worldObj.rand.nextInt(5));
      return par1EntityLivingData;
   }

   public void func_82187_q() {
      this.field_82190_bM = true;
   }

   public EntityVillager func_90012_b(EntityAgeable par1EntityAgeable) {
      EntityVillager var2 = new EntityVillager(this.worldObj);
      var2.onSpawnWithEgg((EntityLivingData)null);
      return var2;
   }

   public boolean allowLeashing() {
      return false;
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.func_90012_b(par1EntityAgeable);
   }

   private static void addToVillagerStockList(Item item, int min_count, int max_count) {
      if (min_count > item.getItemStackLimit(0, 0)) {
         min_count = item.getItemStackLimit(0, 0);
      }

      if (max_count > item.getItemStackLimit(0, 0)) {
         max_count = item.getItemStackLimit(0, 0);
      }

      villagerStockList.put(item.itemID, new Tuple(min_count, max_count));
   }

   public float getBlockPathWeight(int x, int y, int z) {
      return this.worldObj.isInRain(x, y + 1, z) ? -1.0F : super.getBlockPathWeight(x, y, z);
   }

   public int getExperienceValue() {
      return 0;
   }

   static {
      addToVillagerStockList(Item.coal, 16, 24);
      addToVillagerStockList(Item.ingotIron, 4, 8);
      addToVillagerStockList(Item.ingotGold, 4, 8);
      addToVillagerStockList(Item.paper, 24, 36);
      addToVillagerStockList(Item.book, 11, 13);
      addToVillagerStockList(Item.writtenBook, 1, 1);
      addToVillagerStockList(Item.enderPearl, 3, 4);
      addToVillagerStockList(Item.eyeOfEnder, 2, 3);
      addToVillagerStockList(Item.porkRaw, 14, 18);
      addToVillagerStockList(Item.beefRaw, 14, 18);
      addToVillagerStockList(Item.chickenRaw, 14, 18);
      addToVillagerStockList(Item.fishCooked, 9, 13);
      addToVillagerStockList(Item.seeds, 34, 48);
      addToVillagerStockList(Item.melonSeeds, 30, 38);
      addToVillagerStockList(Item.pumpkinSeeds, 30, 38);
      addToVillagerStockList(Item.wheat, 18, 22);
      addToVillagerStockList(Item.getItem(Block.cloth.blockID), 14, 22);
      addToVillagerStockList(Item.rottenFlesh, 36, 64);
      addToVillagerStockList(Item.lambchopRaw, 14, 18);
      blacksmithSellingList.put(Item.flintAndSteel.itemID, new Tuple(3, 4));
      blacksmithSellingList.put(Item.shears.itemID, new Tuple(3, 4));
      blacksmithSellingList.put(Item.swordIron.itemID, new Tuple(7, 11));
      blacksmithSellingList.put(Item.axeIron.itemID, new Tuple(6, 8));
      blacksmithSellingList.put(Item.pickaxeIron.itemID, new Tuple(7, 9));
      blacksmithSellingList.put(Item.shovelIron.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.hoeIron.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.bootsIron.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.helmetIron.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.plateIron.itemID, new Tuple(10, 14));
      blacksmithSellingList.put(Item.legsIron.itemID, new Tuple(8, 10));
      blacksmithSellingList.put(Item.helmetChainIron.itemID, new Tuple(5, 7));
      blacksmithSellingList.put(Item.plateChainIron.itemID, new Tuple(11, 15));
      blacksmithSellingList.put(Item.legsChainIron.itemID, new Tuple(9, 11));
      blacksmithSellingList.put(Item.bootsChainIron.itemID, new Tuple(5, 7));
      blacksmithSellingList.put(Item.bread.itemID, new Tuple(-4, -2));
      blacksmithSellingList.put(Item.melon.itemID, new Tuple(-8, -4));
      blacksmithSellingList.put(Item.appleRed.itemID, new Tuple(-8, -4));
      blacksmithSellingList.put(Item.cookie.itemID, new Tuple(-10, -7));
      blacksmithSellingList.put(Block.glass.blockID, new Tuple(-5, -3));
      blacksmithSellingList.put(Block.bookShelf.blockID, new Tuple(3, 4));
      blacksmithSellingList.put(Item.plateLeather.itemID, new Tuple(4, 5));
      blacksmithSellingList.put(Item.bootsLeather.itemID, new Tuple(2, 4));
      blacksmithSellingList.put(Item.helmetLeather.itemID, new Tuple(2, 4));
      blacksmithSellingList.put(Item.legsLeather.itemID, new Tuple(2, 4));
      blacksmithSellingList.put(Item.saddle.itemID, new Tuple(6, 8));
      blacksmithSellingList.put(Item.redstone.itemID, new Tuple(-4, -1));
      blacksmithSellingList.put(Item.compass.itemID, new Tuple(10, 12));
      blacksmithSellingList.put(Item.pocketSundial.itemID, new Tuple(10, 12));
      blacksmithSellingList.put(Block.glowStone.blockID, new Tuple(-3, -1));
      blacksmithSellingList.put(Item.porkCooked.itemID, new Tuple(-7, -5));
      blacksmithSellingList.put(Item.beefCooked.itemID, new Tuple(-7, -5));
      blacksmithSellingList.put(Item.chickenCooked.itemID, new Tuple(-8, -6));
      blacksmithSellingList.put(Item.eyeOfEnder.itemID, new Tuple(7, 11));
      blacksmithSellingList.put(Item.arrowFlint.itemID, new Tuple(-12, -8));
      blacksmithSellingList.put(Item.pickaxeCopper.itemID, new Tuple(7, 9));
      blacksmithSellingList.put(Item.shovelCopper.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.axeCopper.itemID, new Tuple(6, 8));
      blacksmithSellingList.put(Item.hoeCopper.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.daggerCopper.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.swordCopper.itemID, new Tuple(7, 11));
      blacksmithSellingList.put(Item.daggerIron.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.helmetCopper.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.plateCopper.itemID, new Tuple(10, 14));
      blacksmithSellingList.put(Item.legsCopper.itemID, new Tuple(8, 10));
      blacksmithSellingList.put(Item.bootsCopper.itemID, new Tuple(4, 6));
      blacksmithSellingList.put(Item.helmetChainCopper.itemID, new Tuple(5, 7));
      blacksmithSellingList.put(Item.plateChainCopper.itemID, new Tuple(11, 15));
      blacksmithSellingList.put(Item.legsChainCopper.itemID, new Tuple(9, 11));
      blacksmithSellingList.put(Item.bootsChainCopper.itemID, new Tuple(5, 7));
      blacksmithSellingList.put(Item.lambchopCooked.itemID, new Tuple(-7, -5));
   }
}
