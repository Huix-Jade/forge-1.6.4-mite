package net.minecraft.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public abstract class TileEntity {
   private static Map nameToClassMap = new HashMap();
   private static Map classToNameMap = new HashMap();
   public World worldObj;
   public int xCoord;
   public int yCoord;
   public int zCoord;
   protected boolean tileEntityInvalid;
   public int blockMetadata = -1;
   private Block blockType;
   private String custom_inv_name;

   public static void addMapping(Class par0Class, String par1Str) {
      if (nameToClassMap.containsKey(par1Str)) {
         throw new IllegalArgumentException("Duplicate id: " + par1Str);
      } else {
         nameToClassMap.put(par1Str, par0Class);
         classToNameMap.put(par0Class, par1Str);
      }
   }

   public World getWorldObj() {
      return this.worldObj;
   }

   public void setWorldObj(World par1World) {
      this.worldObj = par1World;
   }

   public boolean hasWorldObj() {
      return this.worldObj != null;
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.xCoord = par1NBTTagCompound.getInteger("x");
      this.yCoord = par1NBTTagCompound.getInteger("y");
      this.zCoord = par1NBTTagCompound.getInteger("z");
      if (par1NBTTagCompound.hasKey("CustomName")) {
         this.setCustomInvName(par1NBTTagCompound.getString("CustomName"));
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      String var2 = (String)classToNameMap.get(this.getClass());
      if (var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         par1NBTTagCompound.setString("id", var2);
         par1NBTTagCompound.setInteger("x", this.xCoord);
         par1NBTTagCompound.setInteger("y", this.yCoord);
         par1NBTTagCompound.setInteger("z", this.zCoord);
         if (this.hasCustomInvName()) {
            par1NBTTagCompound.setString("CustomName", this.getCustomInvName());
         }

      }
   }

   public void updateEntity() {
   }

   public static TileEntity createAndLoadEntity(NBTTagCompound par0NBTTagCompound) {
      TileEntity var1 = null;

      try {
         Class var2 = (Class)nameToClassMap.get(par0NBTTagCompound.getString("id"));
         if (var2 != null) {

            try
            {
               var1 = (TileEntity)var2.newInstance();
            }
            catch (Exception e)
            {
               FMLLog.log(Level.SEVERE, e,
                       "A TileEntity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                       par0NBTTagCompound.getString("id"), var2.getName());
               var1 = null;
            }

         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      if (var1 != null) {
         var1.readFromNBT(par0NBTTagCompound);
      } else {
         MinecraftServer.getServer().getLogAgent().logWarning("Skipping TileEntity with id " + par0NBTTagCompound.getString("id"));
      }

      return var1;
   }

   public int getBlockMetadata() {
      if (this.blockMetadata == -1) {
         this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
      }

      return this.blockMetadata;
   }

   public void onInventoryChanged() {
      if (this.worldObj != null) {
         this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
         this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
         if (this.getBlockType() != null) {
            this.worldObj.func_96440_m(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
         }
      }

   }

   public double getDistanceFrom(double par1, double par3, double par5) {
      double var7 = (double)this.xCoord + 0.5 - par1;
      double var9 = (double)this.yCoord + 0.5 - par3;
      double var11 = (double)this.zCoord + 0.5 - par5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getMaxRenderDistanceSquared() {
      return 4096.0;
   }

   public Block getBlockType() {
      if (this.blockType == null) {
         this.blockType = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)];
      }

      return this.blockType;
   }

   public void setBlock(Block block) {
      this.blockType = block;
   }

   public Packet getDescriptionPacket() {
      return null;
   }

   public boolean isInvalid() {
      return this.tileEntityInvalid;
   }

   public void invalidate() {
      this.tileEntityInvalid = true;
   }

   public void validate() {
      this.tileEntityInvalid = false;
   }

   public boolean receiveClientEvent(int par1, int par2) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.blockType = null;
      this.blockMetadata = -1;
   }

   public void func_85027_a(CrashReportCategory par1CrashReportCategory) {
      par1CrashReportCategory.addCrashSectionCallable("Name", new CallableTileEntityName(this));
      CrashReportCategory.addBlockCrashInfo(par1CrashReportCategory, this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, this.getBlockMetadata());
      par1CrashReportCategory.addCrashSectionCallable("Actual block type", new CallableTileEntityID(this));
      par1CrashReportCategory.addCrashSectionCallable("Actual block data value", new CallableTileEntityData(this));
   }

   public Material getBlockMaterial() {
      return this.getBlockType().blockMaterial;
   }

   public final boolean hasCustomName() {
      return this.custom_inv_name != null && this.custom_inv_name.length() > 0;
   }

   public final void setCustomInvName(String custom_inv_name) {
      this.custom_inv_name = custom_inv_name;
   }

   public final String getCustomInvName() {
      return this.custom_inv_name;
   }

   public String getUnlocalizedInvName() {
      return null;
   }

   public final String getCustomNameOrUnlocalized() {
      return this.hasCustomName() ? this.getCustomInvName() : this.getUnlocalizedInvName();
   }

   public final String getCustomInvNameOrTranslated() {
      return this.hasCustomName() ? this.getCustomInvName() : I18n.getString(this.getUnlocalizedInvName());
   }

   public final boolean hasCustomInvName() {
      return this.custom_inv_name != null && !this.custom_inv_name.isEmpty();
   }

   public final String getTranslatedStandardName() {
      return I18n.getString(this.getUnlocalizedInvName());
   }

   public static void printTileEntitiesList(String title, List list) {
      Class filter = null;
      System.out.println("\n" + title);
      System.out.println(StringHelper.repeat("-", title.length()));
      boolean items_outputted = false;
      Iterator i = list.iterator();

      while(true) {
         TileEntity tile_entity;
         do {
            if (!i.hasNext()) {
               if (!items_outputted) {
                  System.out.println("(none)");
               }

               return;
            }

            tile_entity = (TileEntity)i.next();
         } while(filter != null && tile_entity.getClass() != filter);

         String name = tile_entity.getCustomInvNameOrTranslated();
         if (name == null) {
            name = tile_entity.toString();
         } else if (tile_entity.hasCustomInvName()) {
            name = tile_entity.getTranslatedStandardName() + " \"" + tile_entity.getCustomInvName() + "\"";
         }

         System.out.println(name + " [" + StringHelper.getCoordsAsString(tile_entity.xCoord, tile_entity.yCoord, tile_entity.zCoord) + "]");
         items_outputted = true;
      }
   }

   static Map getClassToNameMap() {
      return classToNameMap;
   }

   static {
      addMapping(TileEntityFurnace.class, "Furnace");
      addMapping(TileEntityChest.class, "Chest");
      addMapping(TileEntityStrongbox.class, "Strongbox");
      addMapping(TileEntityEnderChest.class, "EnderChest");
      addMapping(TileEntityRecordPlayer.class, "RecordPlayer");
      addMapping(TileEntityDispenser.class, "Trap");
      addMapping(TileEntityDropper.class, "Dropper");
      addMapping(TileEntitySign.class, "Sign");
      addMapping(TileEntityMobSpawner.class, "MobSpawner");
      addMapping(TileEntityNote.class, "Music");
      addMapping(TileEntityPiston.class, "Piston");
      addMapping(TileEntityBrewingStand.class, "Cauldron");
      addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
      addMapping(TileEntityEndPortal.class, "Airportal");
      addMapping(TileEntityCommandBlock.class, "Control");
      addMapping(TileEntityBeacon.class, "Beacon");
      addMapping(TileEntitySkull.class, "Skull");
      addMapping(TileEntityDaylightDetector.class, "DLDetector");
      addMapping(TileEntityHopper.class, "Hopper");
      addMapping(TileEntityComparator.class, "Comparator");
      addMapping(TileEntityAnvil.class, "Anvil");
   }

   // -- BEGIN FORGE PATCHES --
   /**
    * Determines if this TileEntity requires update calls.
    * @return True if you want updateEntity() to be called, false if not
    */
   public boolean canUpdate()
   {
      return true;
   }

   /**
    * Called when you receive a TileEntityData packet for the location this
    * TileEntity is currently in. On the client, the NetworkManager will always
    * be the remote server. On the server, it will be whomever is responsible for
    * sending the packet.
    *
    * @param net The NetworkManager the packet originated from
    * @param pkt The data packet
    */
   public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
   {
   }

   /**
    * Called when the chunk this TileEntity is on is Unloaded.
    */
   public void onChunkUnload()
   {
   }

   private boolean isVanilla = getClass().getName().startsWith("net.minecraft.tileentity");
   /**
    * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
    * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
    *
    * @param oldID The old ID of the block
    * @param newID The new ID of the block (May be the same)
    * @param oldMeta The old metadata of the block
    * @param newMeta The new metadata of the block (May be the same)
    * @param world Current world
    * @param x X Postion
    * @param y Y Position
    * @param z Z Position
    * @return True to remove the old tile entity, false to keep it in tact {and create a new one if the new values specify to}
    */
   public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
   {
      return !isVanilla || (oldID != newID);
   }

   public boolean shouldRenderInPass(int pass)
   {
      return pass == 0;
   }
   /**
    * Sometimes default render bounding box: infinite in scope. Used to control rendering on { TileEntitySpecialRenderer}.
    */
   public static final AxisAlignedBB INFINITE_EXTENT_AABB = AxisAlignedBB.getBoundingBox(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

   /**
    * Return an {@link AxisAlignedBB} that controls the visible scope of a { TileEntitySpecialRenderer} associated with this {@link TileEntity}
    * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
    * at this location.
    *
    * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
    */

   public AxisAlignedBB getRenderBoundingBox()
   {
      AxisAlignedBB bb = INFINITE_EXTENT_AABB;
      Block type = getBlockType();
      if (type == Block.enchantmentTable)
      {
         bb = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
      }
      else if (type == Block.chest || type == Block.chestTrapped)
      {
         bb = AxisAlignedBB.getAABBPool().getAABB(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2, zCoord + 2);
      }
      else if (type != null && type != Block.beacon)
      {
         AxisAlignedBB cbb = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord);
         if (cbb != null)
         {
            bb = cbb;
         }
      }
      return bb;
   }
}
