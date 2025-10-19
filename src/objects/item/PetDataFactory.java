package objects.item;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class PetDataFactory {
   private static MapleDataProvider dataRoot = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
   private static Map<Integer, PetData> petDatas = new HashMap<>();
   private static Map<Pair<Integer, Integer>, PetCommand> petCommands = new HashMap<>();

   public static final PetData getPetData(int petId) {
      PetData ret = petDatas.get(petId);
      if (ret != null) {
         return ret;
      } else {
         MapleData data = dataRoot.getData(petId + ".img");
         if (data != null) {
            ret = new PetData();
            ret.setHungry(MapleDataTool.getInt("info/hungry", data, 0));
            ret.setAutoBuff(MapleDataTool.getInt("info/autoBuff", data, 0));
            ret.setLife(MapleDataTool.getInt("info/life", data, 0));
            ret.setMultiPet(MapleDataTool.getInt("info/multiPet", data, 0));
            ret.setPickupItem(MapleDataTool.getInt("info/pickupItem", data, 0));
            ret.setSetItemID(MapleDataTool.getInt("info/setItemID", data, 0));
            ret.setSwwepForDrop(MapleDataTool.getInt("info/sweepForDrop", data, 0));
            ret.setConsumeMP(MapleDataTool.getInt("info/consumeMP", data, 0));
            ret.setConsumeHP(MapleDataTool.getInt("info/consumeHP", data, 0));
            ret.setWonderGrade(MapleDataTool.getInt("info/wonderGrade", data, -1));
         }

         return ret;
      }
   }

   public static final PetCommand getPetCommand(int petId, int skillId) {
      PetCommand ret = petCommands.get(new Pair<>(petId, skillId));
      if (ret != null) {
         return ret;
      } else {
         MapleData skillData = dataRoot.getData(petId + ".img");
         int prob = 0;
         int inc = 0;
         if (skillData != null) {
            prob = MapleDataTool.getInt("interact/" + skillId + "/prob", skillData, 0);
            inc = MapleDataTool.getInt("interact/" + skillId + "/inc", skillData, 0);
         }

         ret = new PetCommand(petId, skillId, prob, inc);
         petCommands.put(new Pair<>(petId, skillId), ret);
         return ret;
      }
   }

   public static final int getHunger(int petId) {
      PetData data = getPetData(petId);
      return data != null ? data.getHungry() : 0;
   }
}
