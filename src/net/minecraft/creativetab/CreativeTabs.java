package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabs {
   public static CreativeTabs[] creativeTabArray = new CreativeTabs[12];
   public static final CreativeTabs tabBlock = new CreativeTabCombat(0, "buildingBlocks");
   public static final CreativeTabs tabDecorations = new CreativeTabBlock(1, "decorations");
   public static final CreativeTabs tabRedstone = new CreativeTabDeco(2, "redstone");
   public static final CreativeTabs tabTransport = new CreativeTabRedstone(3, "transportation");
   public static final CreativeTabs tabMisc = new CreativeTabTransport(4, "misc");
   public static final CreativeTabs tabAllSearch = (new CreativeTabMisc(5, "search")).setBackgroundImageName("item_search.png");
   public static final CreativeTabs tabFood = new CreativeTabSearch(6, "food");
   public static final CreativeTabs tabTools = new CreativeTabFood(7, "tools");
   public static final CreativeTabs tabCombat = new CreativeTabTools(8, "combat");
   public static final CreativeTabs tabBrewing = new CreativeTabBrewing(9, "brewing");
   public static final CreativeTabs tabMaterials = new CreativeTabMaterial(10, "materials");
   public static final CreativeTabs tabInventory = (new CreativeTabInventory(11, "inventory")).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
   private final int tabIndex;
   private final String tabLabel;
   private String backgroundImageName = "items.png";
   private boolean hasScrollbar = true;
   private boolean drawTitle = true;

   public CreativeTabs(String label)
   {
      this(getNextID(), label);
   }

   public CreativeTabs(int par1, String par2Str) {

      if (par1 >= creativeTabArray.length)
      {
         CreativeTabs[] tmp = new CreativeTabs[par1 + 1];
         System.arraycopy(creativeTabArray, 0, tmp, 0, creativeTabArray.length);
         creativeTabArray = tmp;
      }

      this.tabIndex = par1;
      this.tabLabel = par2Str;
      creativeTabArray[par1] = this;
   }

   public int getTabIndex() {
      return this.tabIndex;
   }

   public String getTabLabel() {
      return this.tabLabel;
   }

   public String getTranslatedTabLabel() {
      return "itemGroup." + this.getTabLabel();
   }

   public Item getTabIconItem() {
      return Item.itemsList[this.getTabIconItemIndex()];
   }

   public int getTabIconItemIndex() {
      return 1;
   }

   public String getBackgroundImageName() {
      return this.backgroundImageName;
   }

   public CreativeTabs setBackgroundImageName(String par1Str) {
      this.backgroundImageName = par1Str;
      return this;
   }

   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public CreativeTabs setNoTitle() {
      this.drawTitle = false;
      return this;
   }

   public boolean shouldHidePlayerInventory() {
      return this.hasScrollbar;
   }

   public CreativeTabs setNoScrollbar() {
      this.hasScrollbar = false;
      return this;
   }

   public int getTabColumn() {
      if (tabIndex > 11)
      {
         return ((tabIndex - 12) % 10) % 5;
      }

      return this.tabIndex % 6;
   }

   public boolean isTabInFirstRow() {
      if (tabIndex > 11) {
         return ((tabIndex - 12) % 10) < 5;
      }

      return this.tabIndex < 6;
   }

   public void displayAllReleventItems(List par1List) {
      Item[] itemsList = Item.itemsList;

       for (Item item : itemsList) {

          if (item == null)
          {
             continue;
          }

          for (CreativeTabs tab : item.getCreativeTabs())
          {
             if (tab == this)
             {
                item.getSubItems(item.itemID, this, par1List);
             }
          }


       }

      this.addEnchantmentBooksToList(par1List);
   }

   public void addEnchantmentBooksToList(List par1List) {
      for(int i = 0; i < Enchantment.enchantmentsList.length; ++i) {
         Enchantment enchantment = Enchantment.get(i);
         if (enchantment != null && enchantment.isOnCreativeTab(this)) {
            par1List.add(Item.enchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getNumLevels())));
         }
      }

   }


   public int getTabPage()
   {
      if (tabIndex > 11)
      {
         return ((tabIndex - 12) / 10) + 1;
      }
      return 0;
   }

   public static int getNextID()
   {
      return creativeTabArray.length;
   }

   /**
    * Get the ItemStack that will be rendered to the tab.
    */
   public ItemStack getIconItemStack()
   {
      return new ItemStack(getTabIconItem());
   }

   /**
    * Determines if the search bar should be shown for this tab.
    *
    * @return True to show the bar
    */
   public boolean hasSearchBar()
   {
      return tabIndex == CreativeTabs.tabAllSearch.tabIndex;
   }
}
