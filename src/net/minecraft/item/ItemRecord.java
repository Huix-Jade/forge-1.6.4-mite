package net.minecraft.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemRecord extends Item {
   private static final Map records = new HashMap();
   public final String recordName;
   private final String title;
   private final String composer;

   protected ItemRecord(int par1, String par2Str, String texture, String title, String composer) {
      super(par1, Material.vinyl, "C418".equals(composer) ? texture : "records/" + texture);
      this.recordName = par2Str;
      this.title = title;
      this.composer = composer;
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabMisc);
      records.put(par2Str, this);
   }

   protected ItemRecord(int par1, String par2Str, String texture) {
      this(par1, par2Str, texture, par2Str, "C418");
   }

   public Icon getIconFromSubtype(int par1) {
      return this.itemIcon;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && rc.getBlockHit() == Block.jukebox && rc.block_hit_metadata == 0 && rc.canPlayerEditBlockHit(player, player.getHeldItemStack()) && player.worldObj.isAirOrPassableBlock(rc.block_hit_x, rc.block_hit_y + 1, rc.block_hit_z, false)) {
         if (player.onClient()) {
            player.swingArm();
         } else {
            World world = player.getWorld();
            int x = rc.block_hit_x;
            int y = rc.block_hit_y;
            int z = rc.block_hit_z;
            Block.jukebox.insertRecord(world, x, y, z, player.getHeldItemStack());
            world.playAuxSFXAtEntity((EntityPlayer)null, 1005, x, y, z, this.itemID);
            player.convertOneOfHeldItem((ItemStack)null);
         }

         return true;
      } else {
         return false;
      }
   }

   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      par3List.add(this.getRecordTitle());
   }

   public String getRecordTitle() {
      return this.composer + " - " + this.title;
   }

   public EnumRarity getRarity(ItemStack par1ItemStack) {
      return EnumRarity.rare;
   }

   public static ItemRecord getRecord(String par0Str) {
      return (ItemRecord)records.get(par0Str);
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this.recordName;
   }

   public static boolean isUniqueRecord(ItemStack item_stack) {
      if (item_stack.getItem() instanceof ItemRecord) {
         ItemRecord item_record = (ItemRecord)item_stack.getItem();
         return !"C418".equals(item_record.composer);
      } else {
         return false;
      }
   }

   public static int getSignature(ItemStack item_stack) {
      return 101 + item_stack.itemID - Item.recordUnderworld.itemID;
   }
}
