package objects.users.skills;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class VCoreEnforcement {
   private static final Map<Integer, VCoreEnforcement.EnforceInfo> enforceInfos = new HashMap<>();
   private static final Map<Integer, VCoreEnforcement.EnforceInfo> skillInfos = new HashMap<>();
   private static final Map<Integer, VCoreEnforcement.EnforceInfo> specialInfos = new HashMap<>();

   public static void LoadEnforcement() {
      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"))
         .getData("VCore.img")
         .getChildByPath("Enforcement");

      for (MapleData d : data.getChildByPath("Enforce")) {
         enforceInfos.put(
            Integer.parseInt(d.getName()),
            new VCoreEnforcement.EnforceInfo(
               MapleDataTool.getInt(d.getChildByPath("expEnforce"), 0),
               MapleDataTool.getInt(d.getChildByPath("extract"), 0),
               MapleDataTool.getInt(d.getChildByPath("nextExp"), 0)
            )
         );
      }

      for (MapleData d : data.getChildByPath("Skill")) {
         skillInfos.put(
            Integer.parseInt(d.getName()),
            new VCoreEnforcement.EnforceInfo(
               MapleDataTool.getInt(d.getChildByPath("expEnforce"), 0),
               MapleDataTool.getInt(d.getChildByPath("extract"), 0),
               MapleDataTool.getInt(d.getChildByPath("nextExp"), 0)
            )
         );
      }

      for (MapleData d : data.getChildByPath("Special")) {
         specialInfos.put(
            Integer.parseInt(d.getName()),
            new VCoreEnforcement.EnforceInfo(
               MapleDataTool.getInt(d.getChildByPath("expEnforce"), 0),
               MapleDataTool.getInt(d.getChildByPath("extract"), 0),
               MapleDataTool.getInt(d.getChildByPath("nextExp"), 0)
            )
         );
      }
   }

   public static VCoreEnforcement.EnforceInfo getEnforceInfo(int level) {
      return enforceInfos != null && !enforceInfos.isEmpty() ? enforceInfos.get(level) : null;
   }

   public static VCoreEnforcement.EnforceInfo getSkillInfo(int level) {
      return skillInfos != null && !skillInfos.isEmpty() ? skillInfos.get(level) : null;
   }

   public static VCoreEnforcement.EnforceInfo getSpecialInfo(int level) {
      return specialInfos != null && !specialInfos.isEmpty() ? specialInfos.get(level) : null;
   }

   public static class EnforceInfo {
      private int expEnforce;
      private int extract;
      private int nextExp;

      public EnforceInfo(int expEnforce, int extract, int nextExp) {
         this.expEnforce = expEnforce;
         this.extract = extract;
         this.nextExp = nextExp;
      }

      public int getExpEnforce() {
         return this.expEnforce;
      }

      public int getExtract() {
         return this.extract;
      }

      public int getNextExp() {
         return this.nextExp;
      }
   }
}
