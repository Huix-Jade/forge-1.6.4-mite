package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GLState {
   public final int pname;
   public Object data;

   public GLState(int pname) {
      this.pname = pname;
      if (pname != 3042 && pname != 3553) {
         Minecraft.setErrorMessage("GLState: unhandled pname (" + pname + ")");
      } else {
         this.data = new Boolean(GL11.glIsEnabled(pname));
      }

   }

   public void restore() {
      if (this.pname != 3042 && this.pname != 3553) {
         Minecraft.setErrorMessage("restore: unhandled pname (" + this.pname + ")");
      } else if ((Boolean)this.data) {
         GL11.glEnable(this.pname);
      } else {
         GL11.glDisable(this.pname);
      }

   }
}
