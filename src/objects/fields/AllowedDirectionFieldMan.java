package objects.fields;

import java.io.File;
import java.util.HashMap;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class AllowedDirectionFieldMan {
   private static final HashMap<String, Integer> allowedFieldTransfer = new HashMap<>();
   private static final MapleDataProvider mdp = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Effect.wz"));

   public static void engage(String path) {
      synchronized (allowedFieldTransfer) {
         if (!allowedFieldTransfer.containsKey(path)) {
            allowedFieldTransfer.put(path, -1);
            String[] splitted = path.split("/");
            if (!"Effect".equals(path)) {
               return;
            }

            MapleData img = mdp.getData(splitted[1]);

            for (int i = 2; i < splitted.length; i++) {
               if (img != null) {
                  img = img.getChildByPath(splitted[i]);
               }
            }

            if (img != null) {
               for (MapleData dat : img.getChildren()) {
                  int type = MapleDataTool.getInt("type", dat, 0);
                  int field = MapleDataTool.getInt("field", dat, 0);
                  if (type == 2 && field != 0) {
                     allowedFieldTransfer.put(path, field);
                     return;
                  }
               }
            }
         }
      }
   }

   public static boolean isAllowed(String path, int target) {
      synchronized (allowedFieldTransfer) {
         Integer v = allowedFieldTransfer.get(path);
         return v != null && v == target;
      }
   }
}
