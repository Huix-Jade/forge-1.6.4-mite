package net.minecraft.item.crafting;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBoots;
import net.minecraft.item.ItemChain;
import net.minecraft.item.ItemCuirass;
import net.minecraft.item.ItemHelmet;
import net.minecraft.item.ItemLeggings;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesArmor {
   private CraftingManager crafting_manager;

   private void addHelmetRecipe(Item helmet, Item component) {
      this.crafting_manager.addRecipe(new ItemStack(helmet, 1), "XXX", "X X", 'X', component).setSkillset(component.containsMetal() ? Skill.BLACKSMITHING.id : 0);
   }

   private void addCuirassRecipe(Item cuirass, Item component) {
      this.crafting_manager.addRecipe(new ItemStack(cuirass, 1), "X X", "XXX", "XXX", 'X', component).setSkillset(component.containsMetal() ? Skill.BLACKSMITHING.id : 0);
   }

   private void addLeggingsRecipe(Item leggings, Item component) {
      this.crafting_manager.addRecipe(new ItemStack(leggings, 1), "XXX", "X X", "X X", 'X', component).setSkillset(component.containsMetal() ? Skill.BLACKSMITHING.id : 0);
   }

   private void addBootsRecipe(Item boots, Item component) {
      this.crafting_manager.addRecipe(new ItemStack(boots, 1), "X X", "X X", 'X', component).setSkillset(component.containsMetal() ? Skill.BLACKSMITHING.id : 0);
   }

   public void addRecipes(CraftingManager par1CraftingManager) {
      this.crafting_manager = par1CraftingManager;
      Item[] components = new Item[]{Item.leather, Item.chainCopper, Item.chainSilver, Item.chainGold, Item.chainRustedIron, Item.chainIron, Item.chainMithril, Item.chainAdamantium, Item.chainAncientMetal, Item.ingotCopper, Item.ingotSilver, Item.ingotGold, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotAncientMetal};

      for(int i = 0; i < components.length; ++i) {
         Item component = components[i];
         if (component.materials.size() != 1) {
            Minecraft.setErrorMessage("addRecipes: armor components can have only 1 material (" + component + ")");
         }

         this.addHelmetRecipe(ItemArmor.getMatchingArmor(ItemHelmet.class, component.getExclusiveMaterial(), component instanceof ItemChain), component);
         this.addCuirassRecipe(ItemArmor.getMatchingArmor(ItemCuirass.class, component.getExclusiveMaterial(), component instanceof ItemChain), component);
         this.addLeggingsRecipe(ItemArmor.getMatchingArmor(ItemLeggings.class, component.getExclusiveMaterial(), component instanceof ItemChain), component);
         this.addBootsRecipe(ItemArmor.getMatchingArmor(ItemBoots.class, component.getExclusiveMaterial(), component instanceof ItemChain), component);
      }

   }
}
