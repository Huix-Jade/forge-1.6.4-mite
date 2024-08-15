package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemFood extends Item {
   public final int itemUseDuration;
   private int potionId;
   private int potionDuration;
   private int potionAmplifier;
   private float potionEffectProbability;
   private ItemFood uncooked_item;
   private ItemFood cooked_item;

   public ItemFood() {
      this.itemUseDuration = 0;
   }

   public ItemFood(int satiation, int nutrition, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients) {
      this(satiation, nutrition, 0, has_protein, has_essential_fats, has_phytonutrients);
   }

   public ItemFood(int satiation, int nutrition, int sugar_content, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients) {
      this.itemUseDuration = 0;
      this.setFoodValue(satiation, nutrition, sugar_content, has_protein, has_essential_fats, has_phytonutrients);
   }

   public ItemFood(int id, Material material, int satiation, int nutrition, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients, String texture) {
      this(id, material, satiation, nutrition, 0, has_protein, has_essential_fats, has_phytonutrients, texture);
   }

   public ItemFood(int id, Material material, int satiation, int nutrition, int sugar_content, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients, String texture) {
      super(id, material, "food/" + texture);
      this.itemUseDuration = 32;
      this.setFoodValue(satiation, nutrition, sugar_content, has_protein, has_essential_fats, has_phytonutrients);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setCreativeTab(CreativeTabs.tabFood);
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      if (player.onServer()) {
         player.addFoodValue(this);
         world.playSoundAtEntity(player, "random.burp", 0.5F, player.rand.nextFloat() * 0.1F + 0.9F);
         this.onEaten(item_stack, world, player);
      }

      super.onItemUseFinish(item_stack, world, player);
   }

   protected void onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      if (!par2World.isRemote && this.potionId > 0 && par2World.rand.nextFloat() < this.potionEffectProbability) {
         par3EntityPlayer.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
      }

   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 32;
   }

   public boolean isEatable(int item_subtype) {
      return true;
   }

   public ItemFood setPotionEffect(int par1, int par2, int par3, float par4) {
      this.potionId = par1;
      this.potionDuration = par2;
      this.potionAmplifier = par3;
      this.potionEffectProbability = par4;
      return this;
   }

   public ItemFood setAlwaysEdible() {
      super.setAlwaysEdible();
      return this;
   }

   public ItemFood setAnimalProduct() {
      super.setAnimalProduct();
      return this;
   }

   public ItemFood setPlantProduct() {
      super.setPlantProduct();
      return this;
   }

   public boolean hasCraftingEffect() {
      return this == cheese ? false : super.hasCraftingEffect();
   }

   public static void setCookingResult(ItemFood uncooked_item, ItemFood cooked_item, int xp_reward) {
      uncooked_item.cooked_item = cooked_item;
      cooked_item.uncooked_item = uncooked_item;
      cooked_item.setXPReward(xp_reward);
   }

   public ItemFood getUncookedItem() {
      return this.uncooked_item;
   }

   public ItemFood getCookedItem() {
      return this.cooked_item;
   }

   public ItemStack getItemProducedWhenDestroyed(ItemStack item_stack, DamageSource damage_source) {
      if (damage_source.isFireDamage()) {
         Item cooked_item = this.getCookedItem();
         if (cooked_item != null) {
            return new ItemStack(cooked_item, item_stack.stackSize);
         }
      }

      return super.getItemProducedWhenDestroyed(item_stack, damage_source);
   }

   public float getCompostingValue() {
      return this != appleGold && this != goldenCarrot ? super.getCompostingValue() : 0.0F;
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this == melon ? "slice" : super.getNameDisambiguationForReferenceFile(subtype);
   }
}
