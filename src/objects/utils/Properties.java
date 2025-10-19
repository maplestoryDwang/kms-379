package objects.utils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Properties extends Table {
   private static final Charset utf8 = Charset.forName("UTF-8");
   private static final String bom = new String(new byte[]{-17, -69, -65}, utf8);
   private final Path path;

   private Properties(String root, String... path) {
      super(path[path.length - 1]);
      this.path = Paths.get(root, path);
   }

   private Properties(Path path) {
      super(path.getFileName().toString());
      this.path = path;
   }

   public static Properties loadTable(Path path) {
      Properties result = new Properties(path);
      return result.load() ? result : null;
   }

   public static Properties loadTable(String root, String... path) {
      Properties result = new Properties(root, path);
      return result.load() ? result : null;
   }

   private boolean load() {
      int line = 0;

      try {
         Table current = this;
         boolean bom_chk = true;

         for (String x : Files.readAllLines(this.path, utf8)) {
            line++;
            String f_x;
            if (bom_chk) {
               f_x = x.startsWith(bom) ? x.substring(bom.length()) : x;
               bom_chk = false;
            } else {
               f_x = x;
            }

            String t_x = f_x.trim();
            if (!t_x.isEmpty() && t_x.charAt(0) != '#') {
               if (t_x.equals("}")) {
                  current = current.getParent();
               } else {
                  String[] entry = t_x.split("=", 2);
                  String key = entry[0].trim();
                  String value = entry[1];
                  String t_value = value.trim();
                  if (t_value.equals("{")) {
                     Table table = new Table(key, current);
                     current.putChild(table);
                     current = table;
                  } else {
                     String var14 = t_value.replace("\\b", "\b");
                     var14 = var14.replace("\\t", "\t");
                     var14 = var14.replace("\\n", "\n");
                     var14 = var14.replace("\\f", "\f");
                     var14 = var14.replace("\\r", "\r");
                     current.put(key, var14);
                  }
               }
            }
         }

         return true;
      } catch (Exception var13) {
         return false;
      }
   }
}
