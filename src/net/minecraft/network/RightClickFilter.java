package net.minecraft.network;

public class RightClickFilter {
   public static final byte ALL = -1;
   public static final byte NO_ACTION = 0;
   public static final byte BLOCK_ACTIVATION = 1;
   public static final byte ENTITY_INTERACTION = 2;
   public static final byte INGESTION = 4;
   public static final byte ON_ITEM_RIGHT_CLICK = 8;
   private byte allowed_actions;

   public RightClickFilter() {
      this(-1);
   }

   public RightClickFilter(int allowed_actions) {
      this.allowed_actions = (byte)allowed_actions;
   }

   public void setAllowedActions(int allowed_actions) {
      this.allowed_actions = (byte)allowed_actions;
   }

   public int getAllowedActions() {
      return this.allowed_actions;
   }

   public RightClickFilter setExclusive(int allowed_action) {
      this.allowed_actions = (byte)allowed_action;
      return this;
   }

   public boolean allowsBlockActivation() {
      return (this.allowed_actions & 1) != 0;
   }

   public boolean allowsEntityInteraction() {
      return (this.allowed_actions & 2) != 0;
   }

   public boolean allowsEntityInteractionOnly() {
      return this.allowed_actions == 2;
   }

   public boolean allowsIngestion() {
      return (this.allowed_actions & 4) != 0;
   }

   public boolean allowsIngestionOnly() {
      return this.allowed_actions == 4;
   }

   public boolean allowsOnItemRightClick() {
      return (this.allowed_actions & 8) != 0;
   }

   public RightClickFilter setNoActionAllowed() {
      return this.setExclusive(0);
   }

   public boolean allowsNoActions() {
      return this.allowed_actions == 0;
   }
}
