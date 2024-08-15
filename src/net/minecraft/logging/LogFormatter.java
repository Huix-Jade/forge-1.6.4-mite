package net.minecraft.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class LogFormatter extends Formatter {
   private SimpleDateFormat field_98228_b;
   // $FF: synthetic field
   final LogAgent field_98229_a;

   private LogFormatter(LogAgent var1) {
      this.field_98229_a = var1;
      this.field_98228_b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   }

   public String format(LogRecord var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.field_98228_b.format(var1.getMillis()));
      if (LogAgent.func_98237_a(this.field_98229_a) != null) {
         var2.append(LogAgent.func_98237_a(this.field_98229_a));
      }

      var2.append(" [").append(var1.getLevel().getName()).append("] ");
      var2.append(this.formatMessage(var1));
      var2.append('\n');
      Throwable var3 = var1.getThrown();
      if (var3 != null) {
         StringWriter var4 = new StringWriter();
         var3.printStackTrace(new PrintWriter(var4));
         var2.append(var4.toString());
      }

      return var2.toString();
   }

   // $FF: synthetic method
   LogFormatter(LogAgent var1, LogAgentEmptyAnon var2) {
      this(var1);
   }
}
