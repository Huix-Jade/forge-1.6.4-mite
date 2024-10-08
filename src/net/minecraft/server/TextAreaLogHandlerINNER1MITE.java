package net.minecraft.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class TextAreaLogHandlerINNER1MITE extends Formatter {
   final TextAreaLogHandlerMITE field_120031_a;

   TextAreaLogHandlerINNER1MITE(TextAreaLogHandlerMITE par1TextAreaLogHandler) {
      this.field_120031_a = par1TextAreaLogHandler;
   }

   public String format(LogRecord par1LogRecord) {
      StringBuilder var2 = new StringBuilder();
      var2.append(" [").append(par1LogRecord.getLevel().getName()).append("] ");
      var2.append(this.formatMessage(par1LogRecord));
      var2.append('\n');
      Throwable var3 = par1LogRecord.getThrown();
      if (var3 != null) {
         StringWriter var4 = new StringWriter();
         var3.printStackTrace(new PrintWriter(var4));
         var2.append(var4.toString());
      }

      return var2.toString();
   }
}
