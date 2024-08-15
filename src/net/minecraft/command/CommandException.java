package net.minecraft.command;

public class CommandException extends RuntimeException {
   private Object[] errorObjects;

   public CommandException(String var1, Object... var2) {
      super(var1);
      this.errorObjects = var2;
   }

   public Object[] getErrorOjbects() {
      return this.errorObjects;
   }
}
