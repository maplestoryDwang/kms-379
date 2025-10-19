package constants;

import io.netty.util.internal.ThreadLocalRandom;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class ForceAtomConstants {
   static Map<Integer, Integer> firstImpactMin = new HashMap<>();
   static Map<Integer, Integer> firstImpactMax = new HashMap<>();
   static Map<Integer, Integer> secondImpactMin = new HashMap<>();
   static Map<Integer, Integer> secondImpactMax = new HashMap<>();
   static Map<Integer, Integer> angleMin = new HashMap<>();
   static Map<Integer, Integer> angleMax = new HashMap<>();
   static Map<Integer, Integer> startDelayMin = new HashMap<>();
   static Map<Integer, Integer> startDelayMax = new HashMap<>();
   static Map<Integer, Integer> randomDelayMin = new HashMap<>();
   static Map<Integer, Integer> randomDelayMax = new HashMap<>();

   public static void infoInit() {
      String wzPath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider etcWz = MapleDataProviderFactory.getDataProvider(new File(wzPath + "/Etc.wz"));
      MapleData atomInfo = etcWz.getData("ForceAtomInfo.img");

      for (MapleData atom : atomInfo.getChildren()) {
         try {
            int atomNum = Integer.parseInt(atom.getName());
            firstImpactMin.put(atomNum, MapleDataTool.getInt("firstImpactMin", atom, 0));
            firstImpactMax.put(atomNum, MapleDataTool.getInt("firstImpactMax", atom, 0));
            secondImpactMin.put(atomNum, MapleDataTool.getInt("secondImpactMin", atom, 0));
            secondImpactMax.put(atomNum, MapleDataTool.getInt("secondImpactMax", atom, 0));
            angleMin.put(atomNum, MapleDataTool.getInt("angleMin", atom, 0));
            angleMax.put(atomNum, MapleDataTool.getInt("angleMax", atom, 0));
            startDelayMin.put(atomNum, MapleDataTool.getInt("startDelayMin", atom, 0));
            startDelayMax.put(atomNum, MapleDataTool.getInt("startDelayMax", atom, 0));
            randomDelayMin.put(atomNum, MapleDataTool.getInt("randomDelayMin", atom, 0));
            randomDelayMax.put(atomNum, MapleDataTool.getInt("randomDelayMax", atom, 0));
         } catch (Exception var6) {
            System.out.println("ForceAtom 파싱 오류");
            var6.printStackTrace();
         }
      }

      System.out.println("ForceAtom 정보 로드 완료");
   }

   static int Random(int a, int b) {
      return ThreadLocalRandom.current().nextInt(a, b + 1);
   }

   public static int getFirstImpact(int atomNum) {
      return Random(firstImpactMin.getOrDefault(atomNum, 0), firstImpactMax.getOrDefault(atomNum, 0));
   }

   public static int getSecondImpact(int atomNum) {
      return Random(secondImpactMin.getOrDefault(atomNum, 0), secondImpactMax.getOrDefault(atomNum, 0));
   }

   public static int getAngle(int atomNum) {
      return Random(angleMin.getOrDefault(atomNum, 0), angleMax.getOrDefault(atomNum, 0));
   }

   public static int getStartDelay(int atomNum) {
      return Random(startDelayMin.getOrDefault(atomNum, 0), startDelayMax.getOrDefault(atomNum, 0));
   }

   public static int getRandomDelay(int atomNum) {
      return Random(randomDelayMin.getOrDefault(atomNum, 0), randomDelayMax.getOrDefault(atomNum, 0));
   }
}
