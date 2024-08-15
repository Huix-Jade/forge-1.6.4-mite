package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
   protected ItemEmptyMap(int par1) {
      super(par1, "map_empty");
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      if (player.onClient()) {
         return true;
      } else {
         WorldServer world = player.getWorldServer();
         if (!ItemMap.isAnotherMapIdAvailable(world)) {
            return false;
         } else {
            ItemStack new_item_stack = new ItemStack(Item.map, 1, world.getUniqueDataId("map"));
            String map_name = "map_" + new_item_stack.getItemSubtype();
            MapData map_data = new MapData(map_name);
            world.setItemData(map_name, map_data);
            map_data.scale = 0;
            int var7 = 128 * (1 << map_data.scale);
            map_data.xCenter = (int)(Math.round(player.posX / (double)var7) * (long)var7);
            map_data.zCenter = (int)(Math.round(player.posZ / (double)var7) * (long)var7);
            map_data.dimension = (byte)world.provider.dimensionId;
            map_data.markDirty();
            player.inventory.convertOneOfCurrentItem(new_item_stack);
            return true;
         }
      }
   }
}
