package net.minecraft.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ItemReferencedBook extends ItemEditableBook {
   public static final int BOOK_BOAT = 1;
   public static final int BOOK_CRYPT = 2;
   public static final int BOOK_CRYSTAL = 3;
   public static final int BOOK_DRAGON = 4;
   public static final int BOOK_GLOBE = 5;
   public static final int BOOK_SERPENT = 6;
   public static final int BOOK_SPHINX = 7;
   public static final int BOOK_STAR = 8;
   public static final int BOOK_TEMPLE = 9;

   public ItemReferencedBook(int id) {
      super(id);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      ItemStack held_item_stack = player.getHeldItemStack();
      if (player.onServer() && isReferencedBook(held_item_stack)) {
         player.getAsEntityPlayerMP().addToReferencedBooksRead(held_item_stack);
      }

      return super.onItemRightClick(player, partial_tick, ctrl_is_down);
   }

   public static NBTTagCompound generateBookContents(int index) {
      NBTTagCompound nbt = new NBTTagCompound();
      NBTTagList pages = new NBTTagList();
      nbt.setTag("pages", pages);
      if (index == 1) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Boat *"));
         pages.appendTag(new NBTTagString("1", "* Boat *\n\nThe waters of life do move as the weather, and in life as the waters, thee shall know both calm and storm. He that must embrace the storm shall soon be swept away, while he that learns to navigate,"));
         pages.appendTag(new NBTTagString("2", "shall make his own journey.\n\nWhen thy fear has turned to anger, thee has lost thy soul, and shall make the devil laugh. But to still thy tongue and become amazed, thee begets enlightenment, and thus shall thee know"));
         pages.appendTag(new NBTTagString("3", "bliss.\n\nThus may one discover a craft, and sail upon the waters. Thus may one discover thyself, and sail upon life."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 2) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Crypt *"));
         pages.appendTag(new NBTTagString("1", "* Crypt *\n\nIn the land of the Dead shall thee travel, an isle of ghosts and demons. For as thy knowledge be rooted in history past, thy thoughts shall be but echoes from the crypts of the lost and the dead."));
         pages.appendTag(new NBTTagString("2", "They that stare blindly into the hall of the past shall see not, while he that stands aloof and notes carefully, shall find the secrets which lie nestled along its outskirting waters.\n\nTo embrace the dead as if truth, is to dwell"));
         pages.appendTag(new NBTTagString("3", "in darkened crypts. To cast thine eyes upon the radiant sun, let this be thy truth.\n\nThus may one escape the crypt, and depart the island cube. Thus may one escape the past, and depart the island mind."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 3) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Crystal *"));
         pages.appendTag(new NBTTagString("1", "* Crystal *\n\nThe Mind of Man doth speak a tongue all its own, beyond language and words. So too, doth the mind live in meanings and symbols, comprised of transient glimpses which signify its ephemeral understanding."));
         pages.appendTag(new NBTTagString("2", "As likened unto the crystal orbs of old mystics, piercing the veils of the unknown as a channel for hidden knowledge, likewise the mind makes crystal its symbols, that from this may be wrested forth the perceptions of the man."));
         pages.appendTag(new NBTTagString("3", "The Dragon for \"D\", the first of a word, and Obelisk for \"O\", that which should follow. The Moon stands for \"M\", and Island for \"I\", which is marked by the sign of the Cross. The Nether and Night, the realms of the dark, reveal the letter of \"N\""));
         pages.appendTag(new NBTTagString("4", "behind Death. And as the Star signs for Astral, let this be the \"A\", and lastly the Egg which is \"E\".\n\nThus may thee glimpse one meaning of the crystal, which lies branched among many thousands of combinations. Thus may"));
         pages.appendTag(new NBTTagString("5", "thee glimpse one meaning of life, which lies branched among many thousands of dimensions."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 4) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Dragon *"));
         pages.appendTag(new NBTTagString("1", "* Dragon *\n\nInside a dragon's lair doth Man wander, in pursuit of his fulfillment, a monster behind every shadow lurking. His inner dreams doth hunger for noble rewards, while his world doth comprise naught but"));
         pages.appendTag(new NBTTagString("2", "fruitless conflict and frustration. Only he that yet looks again at all he hath discovered, may find hidden the new meanings which lie concealed between the cracks of his brittle knowledge.\n\nThus may one brave the shadowy dragons,"));
         pages.appendTag(new NBTTagString("3", "and discover the hidden treasures of the labyrinth. Thus may one brave his shadowy fears, and discover the hidden treasures of life."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 5) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Globe *"));
         pages.appendTag(new NBTTagString("1", "* Globe *\n\nAs one journey leads yet into another, so too doth all life continue onward. Within one seed doth lie the secrets of the universe, within one cell the secrets of life, and within thee the secrets of thy"));
         pages.appendTag(new NBTTagString("2", "mystery.\n\nTHOU ART THE KEY."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 6) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Serpent *"));
         pages.appendTag(new NBTTagString("1", "* Serpent *\n\nAs the serpent winds and coils, so shall thy journey through life. He that comprehends the serpent shall ascend its mighty towers, while he that sets forth unknowing shall but sail endlessly within clouds"));
         pages.appendTag(new NBTTagString("2", "of confusion and without enlightenment.\n\nThus may one engage the mists of darkness and glimpse the light, and recognize the harbour of his twining purpose. Thus may one engage the mists of life and glimpse the light, and recognize"));
         pages.appendTag(new NBTTagString("3", "the course of his twining destiny."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 7) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Sphinx *"));
         pages.appendTag(new NBTTagString("1", "* Sphinx *\n\nAnd a vat of slugs shall be her womb, for amongst the hideous and foul doth the seed of her spirit lie waiting. From a bed of salt upon the pool of churning waters shall come the immaculate rebirth, and"));
         pages.appendTag(new NBTTagString("2", "deliverance from the Sea of Chaos.\n\nSo likened too is the life of man, born into a sea of strange voices, living in the land of salt, forever searching for the secret silent whisper that will ignite him, that he may open his eyes"));
         pages.appendTag(new NBTTagString("3", "and stand free against the sky.\n\nThus may one discover a bridge, and ascend from the bubbling turmoil. Thus may one discover thyself, and ascend from the chaos of life."));
         nbt.setInteger("xp_reward", 100);
      } else if (index == 8) {
         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Star *"));
         pages.appendTag(new NBTTagString("1", "* Star *\n\nAs thy gaze doth turn to the heavens to behold the mystery of the stars, thee looks into the mirror of thy soul. Dost thou see the deep well of all things. Or only the swirling chaos of anarchy in motion."));
         pages.appendTag(new NBTTagString("2", "Therein doth the memories of past meet the visions of future, and therein lies the inevitable destiny of all Men. To divine the order from the chaos, the beauty within the cascading tumult of the world in which he is both surrounded and a part. This is the"));
         pages.appendTag(new NBTTagString("3", "unique gift of Man, that by which he alone is separated from all others. This is his purpose, his significance, and his meaning. This is that by which he doth witness the relentless torrents of change, as all life doth move both within him and"));
         pages.appendTag(new NBTTagString("4", "without, and from thus divine order, that he might realize his purpose and his meaning.\n\nLook upon life as thee may look upon the stone. And create thee then thine own order. Look first at a man, and if thee looks"));
         pages.appendTag(new NBTTagString("5", "rightly, then soon shall ye come full circle. Then look beneath him, and if thee looks rightly, then soon shall thee once again come full circle.\n\nThus may thee divine the puzzle from the pieces, and from it"));
         pages.appendTag(new NBTTagString("6", "derive thy solutions. Thus may thee divine the order from the chaos, and from it derive thy meanings."));
         nbt.setInteger("xp_reward", 100);
      } else {
         if (index != 9) {
            Minecraft.setErrorMessage("generateBook: invalid index " + index);
            return null;
         }

         nbt.setTag("author", new NBTTagString("author", "Father Phoonzang"));
         nbt.setTag("title", new NBTTagString("title", "* Temple *"));
         pages.appendTag(new NBTTagString("1", "* Temple *\n\nThe complexities of life do distract and disorient, and thee shall know the turmoil of fear and uncertainty. He that hath not foundation shall in wandering suffer, while he that hath sanctuary shall"));
         pages.appendTag(new NBTTagString("2", "know peace and stability. Discover thee the builder, that thee may light thine eyes. For rooted within him lies the knowledge of structure.\n\nFrom encircling waters wrest thee the works of the stones. As thee make sacrifice twixt all"));
         pages.appendTag(new NBTTagString("3", "four corners thy labor shall not be in vain, for the opaque yielded from the stone will be transformed, and thee shall behold the radiant light.\n\nThus may one discover a haven, and assuredly travel onward. Thus may one"));
         pages.appendTag(new NBTTagString("4", "discover thyself, and assuredly travel through life."));
         nbt.setInteger("xp_reward", 100);
      }

      nbt.setInteger("reference_index", index);
      nbt.setString("flavor_text", "A tattered manuscript bound in old leather. The prose is strange yet somehow familiar");
      return nbt;
   }

   public static ItemStack generateBook(int index) {
      return (new ItemStack(Item.referencedBook)).setTagCompound(generateBookContents(index));
   }

   public static int getReferenceIndex(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() instanceof ItemReferencedBook && item_stack.hasTagCompound() ? item_stack.getTagCompound().getInteger("reference_index") : 0;
   }

   public static boolean isReferencedBook(ItemStack item_stack) {
      return getReferenceIndex(item_stack) > 0;
   }

   public static int getXPReward(ItemStack item_stack) {
      return item_stack != null && item_stack.hasTagCompound() ? item_stack.getTagCompound().getInteger("xp_reward") : 0;
   }

   public static int getSignature(ItemStack item_stack) {
      return getReferenceIndex(item_stack);
   }
}
