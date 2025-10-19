package objects.users.enchant.skilloption;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;

public class SkillOption {
   private static Map<Integer, SkillEntry> skillOptions;
   private static Map<Integer, SocketEntry> sockets;

   public static void init() {
      String wzPath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(wzPath + "/Item.wz"));
      MapleData data = prov.getData("SkillOption.img");
      MapleData skillData = data.getChildByPath("skill");
      skillOptions = new HashMap<>();

      for (MapleData root : skillData.getChildren()) {
         int skillID = Integer.parseInt(root.getName());
         SkillEntry skillOption = new SkillEntry(root);
         skillOptions.put(skillID, skillOption);
      }

      MapleData socketData = data.getChildByPath("socket");
      sockets = new HashMap<>();

      for (MapleData root : socketData.getChildren()) {
         int socketID = Integer.parseInt(root.getName());
         SocketEntry socket = new SocketEntry(root);
         sockets.put(socketID, socket);
      }
   }

   public static SkillEntry getSkillOption(Integer skillID) {
      return skillOptions.get(skillID);
   }

   public static SocketEntry getSocket(Integer socketID) {
      return sockets.get(socketID);
   }
}
