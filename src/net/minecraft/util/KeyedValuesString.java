package net.minecraft.util;

import java.util.regex.Pattern;

public final class KeyedValuesString {
   private final String key_value_separator;
   private final String[] pairs;

   public KeyedValuesString(String keyed_values, String pairs_separator, String key_value_separator) {
      this.key_value_separator = key_value_separator;
      this.pairs = this.getPairs(keyed_values, pairs_separator);
   }

   public KeyedValuesString(String keyed_values) {
      this(keyed_values, Pattern.quote("|"), "=");
   }

   private String[] getPairs(String keyed_values, String pairs_separator) {
      return keyed_values != null && !keyed_values.trim().isEmpty() ? keyed_values.split(pairs_separator) : null;
   }

   public String getValue(String key, boolean return_null_if_empty) {
      for(int i = 0; i < this.pairs.length; ++i) {
         String pair = this.pairs[i].trim();
         if (!pair.isEmpty() && pair.indexOf(this.key_value_separator) > 0) {
            String[] arr = pair.split(this.key_value_separator);
            if (arr[0].trim().equals(key)) {
               if (arr.length == 1) {
                  return return_null_if_empty ? null : "";
               }

               String value = arr[1].trim();
               return return_null_if_empty && value.isEmpty() ? null : value;
            }
         }
      }

      return null;
   }
}
