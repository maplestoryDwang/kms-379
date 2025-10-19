package objects.item;

import constants.GameConstants;
import constants.ServerConstants;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;

public class DamageSkinSaveInfo {
   private DamageSkinSaveData activeDamageSkinData;
   private List<DamageSkinSaveData> saveDamageSkinDatas = new ArrayList<>();
   private int slotCount;

   public static String getDefaultDesc() {
      return ServerConstants.serverName + " 데미지 스킨 정보이다.\\r\\n\\r\\n\\r\\n\\r\\n\\r\\n";
   }

   public DamageSkinSaveInfo(MapleCharacter player) {
      String slotCount = player.getOneInfoQuest(13190, "slotCount");
      this.slotCount = slotCount != null && !slotCount.isEmpty() ? Integer.parseInt(slotCount) : 0;
      MapleQuestStatus damSkin = player.getQuestIfNullAdd(MapleQuest.getInstance(7291));
      int damageSkinID = damSkin == null ? 0 : (damSkin.getCustomData() == null ? 0 : Integer.parseInt(damSkin.getCustomData()));
      this.activeDamageSkinData = new DamageSkinSaveData(damageSkinID, GameConstants.getDamageSkinItemID(damageSkinID), false, getDefaultDesc());
   }

   public DamageSkinSaveData getActiveDamageSkinData() {
      return this.activeDamageSkinData;
   }

   public void setActiveDamageSkinData(DamageSkinSaveData data) {
      this.activeDamageSkinData = data;
   }

   public List<DamageSkinSaveData> getSaveDamageSkinDatas() {
      return this.saveDamageSkinDatas;
   }

   public boolean hasSaveDamageSkin(int itemID) {
      boolean ret = false;

      for (DamageSkinSaveData data : this.saveDamageSkinDatas) {
         if (data.getItemID() == itemID) {
            ret = true;
            break;
         }
      }

      return ret;
   }

   public void putDamageSkinData(DamageSkinSaveData data) {
      this.saveDamageSkinDatas.add(data);
   }

   public void removeDamageSkinData(short damageSkinID) {
      DamageSkinSaveData remove = null;

      for (DamageSkinSaveData data : this.getSaveDamageSkinDatas()) {
         if (data.getDamageSkinID() == damageSkinID) {
            remove = data;
         }
      }

      if (remove != null) {
         this.saveDamageSkinDatas.remove(remove);
      }
   }

   public int getSlotCount() {
      return this.slotCount;
   }

   public void setSlotCount(int slotCount) {
      this.slotCount = slotCount;
   }

   public void addSlotCount(int delta) {
      this.slotCount += delta;
   }

   public void encode(PacketEncoder packet) {
      boolean useDamageSkin = true;
      packet.write(useDamageSkin);
      if (useDamageSkin) {
         this.activeDamageSkinData.encode(packet);
         packet.writeInt(-1);
         new DamageSkinSaveData(0, 1, false, "").encode(packet);
         packet.writeInt(-1);
         packet.writeInt(0);
         packet.writeInt(1);
         packet.writeShort(0);
         packet.write(0);
         packet.writeShort(this.slotCount);
         packet.writeShort(this.saveDamageSkinDatas.size());
         this.saveDamageSkinDatas.forEach(sd -> sd.encode(packet));
      }
   }
}
