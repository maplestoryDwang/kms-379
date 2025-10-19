package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import objects.utils.HexTool;

public class ExternalCodeTableGetter {
   final Properties props;

   public ExternalCodeTableGetter(Properties properties) {
      this.props = properties;
   }

   private static final <T extends Enum<? extends WritableIntValueHolder> & WritableIntValueHolder> T valueOf(String name, T[] values) {
      for (T val : values) {
         if (val.name().equals(name)) {
            return val;
         }
      }

      return null;
   }

   private final <T extends Enum<? extends WritableIntValueHolder> & WritableIntValueHolder> short getValue(String name, T[] values, short def) {
      String prop = this.props.getProperty(name);
      if (prop != null && prop.length() > 0) {
         String trimmed = prop.trim();
         String[] args = trimmed.split(" ");
         int base = 0;
         String offset;
         if (args.length == 2) {
            base = ((WritableIntValueHolder)valueOf(args[0], values)).getValue();
            if (base == def) {
               base = this.getValue(args[0], values, def);
            }

            offset = args[1];
         } else {
            offset = args[0];
         }

         return offset.length() > 2 && offset.substring(0, 2).equals("0x")
            ? (short)(Short.parseShort(offset.substring(2), 16) + base)
            : (short)(Short.parseShort(offset) + base);
      } else {
         return def;
      }
   }

   public static final <T extends Enum<? extends WritableIntValueHolder> & WritableIntValueHolder> String getOpcodeTable(T[] enumeration) {
      StringBuilder enumVals = new StringBuilder();
      List<T> all = new ArrayList<>(Arrays.asList(enumeration));
      all.sort(Comparator.comparingInt(rec$ -> rec$.getValue()));

      for (T code : all) {
         enumVals.append(code.name());
         enumVals.append(" = ");
         enumVals.append("0x");
         enumVals.append(HexTool.toString(code.getValue()));
         enumVals.append(" (");
         enumVals.append(code.getValue());
         enumVals.append(")\n");
      }

      return enumVals.toString();
   }

   public static final <T extends Enum<? extends WritableIntValueHolder> & WritableIntValueHolder> void populateValues(Properties properties, T[] values) {
      ExternalCodeTableGetter exc = new ExternalCodeTableGetter(properties);

      for (T code : values) {
         code.setValue(exc.getValue(code.name(), values, (short)-2));
      }
   }
}
