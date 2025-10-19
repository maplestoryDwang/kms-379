package objects.context;

import database.DBConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.child.SpecialEffect;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.MobCollectionEx;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class MonsterCollection {
   public static final HashMap<Integer, MonsterCollection.MobCollection> mobCollections = new HashMap<>();
   public static final HashMap<String, MonsterCollection.CollectionMobData> mobByName = new HashMap<>();

   public static void main(String[] args) {
      cacheMonsterCollection();
   }

   public static void setMobCollectionOnFirst(MapleCharacter chr) {
      if (chr.getCollectionInfo().size() < 43) {
         for (Entry<Integer, MonsterCollection.MobCollection> collections : mobCollections.entrySet()) {
            for (int subIndexID = 0; subIndexID < collections.getValue().subIndexList.size(); subIndexID++) {
               int questID = 100000 + collections.getKey() * 100 + subIndexID;
               if (chr.getCollectionInfo().getOrDefault(questID, null) == null) {
                  String prefix = subIndexID + "=";
                  String data = prefix + "0".repeat(50 - prefix.length());
                  chr.updateInfoQuest(questID, data);
                  chr.getCollectionInfo().put(questID, new MobCollectionEx(questID, data));
                  chr.send(CWvsContext.InfoPacket.setMobCollectionOnFirst(questID, data));
               }
            }
         }
      }
   }

   public static void setMobOnCollection(MapleCharacter chr, MonsterCollection.CollectionMobData mobData) {
      setMobCollectionOnFirst(chr);
      int questID = mobData.getQuestID();
      int mobTemplateID = mobData.getMobTemplateID();
      int mobIndex = mobData.getMobIndex();
      int region = questID % 10000 / 100;
      int session = questID % 10;
      if (chr.getCollectionInfo().getOrDefault(questID, null) != null) {
         String[] data = chr.getCollectionInfo().get(questID).getData().split("=");
         String prefix = data[0];
         String value = data[1];
         String bin = "";
         StringBuilder setNewString = new StringBuilder(prefix + "=");

         for (int i = 0; i < 3; i++) {
            int maxLength = Math.min((i + 1) * 8, value.length());
            if (i * 8 < maxLength) {
               String subData = value.substring(i * 8, maxLength);
               if (!subData.isEmpty()) {
                  String binary = Long.toBinaryString(Long.parseLong(subData, 16));

                  while (binary.length() < 32) {
                     binary = "0" + binary;
                  }

                  bin = bin + binary;
               }
            }
         }

         int mobBinaryIndex = 1 + mobIndex * 3;
         StringBuilder sbBin = new StringBuilder(bin);
         sbBin.deleteCharAt(mobBinaryIndex);
         sbBin.insert(mobBinaryIndex, "1");
         String first = Long.toString(Long.parseLong(sbBin.substring(0, 32), 2), 16);
         String second = Long.toString(Long.parseLong(sbBin.substring(32, 64), 2), 16);
         String third = Long.toString(Long.parseLong(sbBin.substring(64, 96), 2), 16);

         while (first.length() < 8) {
            first = "0" + first;
         }

         while (second.length() < 8) {
            second = "0" + second;
         }

         while (third.length() < 8) {
            third = "0" + third;
         }

         String finalText = first + second + third;

         while (finalText.length() < 48) {
            finalText = finalText + "0";
         }

         setNewString.append(finalText);
         SpecialEffect e = new SpecialEffect(chr.getId(), false, 0, 4, 0, "Effect/BasicEff.img/monsterCollectionGet");
         chr.send(e.encodeForLocal());
         chr.send(CField.EffectPacket.showMonsterCollectionMessage(mobTemplateID));
         chr.updateInfoQuest(questID, setNewString.toString());
         chr.getCollectionInfo().put(questID, new MobCollectionEx(questID, setNewString.toString()));
         chr.send(CWvsContext.InfoPacket.setMobOnCollection(questID, setNewString.toString()));
      }
   }

   public static boolean checkIfMobOnCollection(MapleCharacter chr, MonsterCollection.CollectionMobData mobData) {
      setMobCollectionOnFirst(chr);
      int questID = mobData.getQuestID();
      int mobTemplateID = mobData.getMobTemplateID();
      int mobIndex = mobData.getMobIndex();
      int region = questID % 10000 / 100;
      int session = questID % 10;
      if (chr.getCollectionInfo().getOrDefault(questID, null) == null) {
         return true;
      } else {
         String data = chr.getCollectionInfo().get(questID).getData().split("=")[1];
         StringBuilder bin = new StringBuilder();

         for (int i = 0; i < 3; i++) {
            int maxLength = Math.min((i + 1) * 8, data.length());
            if (i * 8 < maxLength) {
               String subData = data.substring(i * 8, maxLength);
               if (!subData.isEmpty()) {
                  if (subData.contains(";")) {
                     subData = subData.replace(";", "");
                  }

                  StringBuilder binary = new StringBuilder(Long.toBinaryString(Long.parseLong(subData, 16)));

                  while (binary.length() < 32) {
                     binary.insert(0, "0");
                  }

                  bin.append((CharSequence)binary);
               }
            }
         }

         int mobBinaryIndex = 1 + mobIndex * 3;
         return bin.charAt(mobBinaryIndex) == '1';
      }
   }

   public static boolean checkLineMobOnCollection(MapleCharacter chr, int region, int session, int group) {
      setMobCollectionOnFirst(chr);
      int questID = region * 100 + 100000 + session;
      if (chr.getCollectionInfo().getOrDefault(questID, null) == null) {
         return false;
      } else {
         MonsterCollection.MobCollection mc = mobCollections.getOrDefault(region, null);
         if (mc == null) {
            return false;
         } else {
            MonsterCollection.CollectionSubIndex si = mc.getSubIndexList().get(session);
            if (si == null) {
               return false;
            } else {
               MonsterCollection.MobCollectionGroup cGroup = si.getGroup().get(group);
               if (cGroup == null) {
                  return false;
               } else {
                  int mobSize = cGroup.getMobs().size();

                  for (Integer mob : cGroup.getMobs()) {
                     if (mob == 9100049) {
                        mobSize--;
                     }
                  }

                  String data = chr.getCollectionInfo().get(questID).getData().split("=")[1];
                  String bin = "";

                  for (int i = 0; i < 3; i++) {
                     int maxLength = Math.min((i + 1) * 8, data.length());
                     if (i * 8 < maxLength) {
                        String subData = data.substring(i * 8, maxLength);
                        if (!subData.isEmpty()) {
                           String binary = Long.toBinaryString(Long.parseLong(subData, 16));

                           while (binary.length() < 32) {
                              binary = "0" + binary;
                           }

                           bin = bin + binary;
                        }
                     }
                  }

                  int checkCount = 0;
                  int[][] checks = new int[][]{{1, 4, 7, 10, 13}, {16, 19, 22, 25, 28}, {31, 34, 37, 40, 43}, {46, 49, 52, 55, 58}, {61, 64, 67, 70, 73}};

                  for (int check : checks[group]) {
                     if (bin.charAt(check) == '1') {
                        checkCount++;
                     }
                  }

                  return checkCount == mobSize;
               }
            }
         }
      }
   }

   public static boolean checkSessionMobOnCollection(MapleCharacter chr, int region, int session) {
      setMobCollectionOnFirst(chr);
      int questID = region * 100 + 100000;
      if (chr.getCollectionInfo().getOrDefault(questID, null) == null) {
         return false;
      } else {
         MonsterCollection.MobCollection mc = mobCollections.getOrDefault(region, null);
         if (mc == null) {
            return false;
         } else {
            MonsterCollection.CollectionSubIndex si = mc.getSubIndexList().get(session);
            if (si == null) {
               return false;
            } else {
               int mobSize = 25;

               for (MonsterCollection.MobCollectionGroup groups : si.getGroup()) {
                  for (Integer mob : groups.getMobs()) {
                     if (mob == 9100049) {
                        mobSize--;
                     }
                  }
               }

               String data = chr.getCollectionInfo().get(questID).getData().split("=")[1];
               String bin = "";

               for (int i = 0; i < 3; i++) {
                  int maxLength = Math.min((i + 1) * 8, data.length());
                  if (i * 8 < maxLength) {
                     String subData = data.substring(i * 8, maxLength);
                     if (!subData.isEmpty()) {
                        String binary = Long.toBinaryString(Long.parseLong(subData, 16));

                        while (binary.length() < 32) {
                           binary = "0" + binary;
                        }

                        bin = bin + binary;
                     }
                  }
               }

               int checkCount = 0;
               int[] checks = new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58, 61, 64, 67, 70, 73};

               for (int check : checks) {
                  if (bin.charAt(check) == '1') {
                     checkCount++;
                  }
               }

               return checkCount == mobSize;
            }
         }
      }
   }

   public static int getTotalCollection(MapleCharacter chr) {
      setMobCollectionOnFirst(chr);
      int ret = 0;

      for (Entry<Integer, MobCollectionEx> entry : chr.getCollectionInfo().entrySet()) {
         if (entry.getKey() >= 100000) {
            String data = chr.getCollectionInfo().get(entry.getKey()).getData().split("=")[1].split(";")[0];
            String bin = "";

            for (int i = 0; i < 3; i++) {
               int maxLength = Math.min((i + 1) * 8, data.length());
               if (i * 8 < maxLength) {
                  String subData = data.substring(i * 8, maxLength);
                  if (!subData.isEmpty()) {
                     String binary = Long.toBinaryString(Long.parseLong(subData, 16));

                     while (binary.length() < 32) {
                        binary = "0" + binary;
                     }

                     bin = bin + binary;
                  }
               }
            }

            int[] checks = new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58, 61, 64, 67, 70, 73};

            for (int check : checks) {
               if (bin.length() > check && bin.charAt(check) == '1') {
                  ret++;
               }
            }
         }
      }

      return ret;
   }

   public static int getTotalCollectionByRegion(MapleCharacter chr, int region) {
      setMobCollectionOnFirst(chr);
      int ret = 0;

      for (Entry<Integer, MobCollectionEx> entry : chr.getCollectionInfo().entrySet()) {
         if (entry.getKey() >= 100000 && (entry.getKey() - 100000) / 100 == region) {
            String data = chr.getCollectionInfo().get(entry.getKey()).getData().split("=")[1].split(";")[0];
            String bin = "";

            for (int i = 0; i < 3; i++) {
               int maxLength = Math.min((i + 1) * 8, data.length());
               if (i * 8 < maxLength) {
                  String subData = data.substring(i * 8, maxLength);
                  if (!subData.isEmpty()) {
                     if (subData.contains(";")) {
                        subData = subData.replace(";", "");
                     }

                     String binary = Long.toBinaryString(Long.parseLong(subData, 16));

                     while (binary.length() < 32) {
                        binary = "0" + binary;
                     }

                     bin = bin + binary;
                  }
               }
            }

            int[] checks = new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58, 61, 64, 67, 70, 73};

            for (int check : checks) {
               if (bin.length() > check && bin.charAt(check) == '1') {
                  ret++;
               }
            }
         }
      }

      return ret;
   }

   public static void cacheMonsterCollection() {
      if (!DBConfig.DB_PASSWORD.equals("J2vs@efh6@K6!2")) {
         System.setProperty("net.sf.odinms.wzpath", "wz");
      }

      MapleData mobCollection = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Etc.wz")).getData("mobCollection.img");

      for (MapleData data : mobCollection.getChildren()) {
         if (Pattern.matches("^[0-9]*$", data.getName())) {
            int region = Integer.parseInt(data.getName());
            MonsterCollection.MobCollection collection = new MonsterCollection.MobCollection();
            MonsterCollection.MobCollectionInfo info = new MonsterCollection.MobCollectionInfo();

            for (MapleData data1 : data.getChildren()) {
               if (data1.getName().equals("info")) {
                  String name = MapleDataTool.getString(data1.getChildByPath("name"), "");
                  int recordID = MapleDataTool.getInt(data1.getChildByPath("recordID"), -1);
                  int rewardID = MapleDataTool.getInt(data1.getChildByPath("rewardID"), -1);
                  int period = MapleDataTool.getInt(data1.getChildByPath("period"), -1);
                  int rewardCount = MapleDataTool.getInt(data1.getChildByPath("rewardCount"), -1);
                  if (data1.getChildByPath("clearQuest") != null) {
                     for (MapleData clearQuest : data1.getChildByPath("clearQuest")) {
                        if (!clearQuest.getName().equals("decl")) {
                           int clearCount = MapleDataTool.getInt(clearQuest.getChildByPath("clearCount"), -1);
                           int recordIDclearQuest = MapleDataTool.getInt(clearQuest.getChildByPath("recordID"), -1);
                           int rewardIDclearQuest = MapleDataTool.getInt(clearQuest.getChildByPath("rewardID"), -1);
                           int titleQuestID = MapleDataTool.getInt(clearQuest.getChildByPath("titleQuestID"), -1);
                           info.getClearQuest()
                              .add(new MonsterCollection.MobCollectionClearQuest(clearCount, recordIDclearQuest, rewardIDclearQuest, titleQuestID));
                        }
                     }
                  }

                  info.setName(name);
                  info.setRecordID(recordID);
                  info.setRewardID(rewardID);
                  info.setPeriod(period);
                  info.setRewardCount(rewardCount);
                  collection.setInfo(info);
               } else {
                  MonsterCollection.CollectionSubIndex si = new MonsterCollection.CollectionSubIndex();

                  for (MapleData subindex : data1.getChildren()) {
                     if (subindex.getName().equals("info")) {
                        MonsterCollection.MobCollectionInfo subIndexInfo = new MonsterCollection.MobCollectionInfo();
                        String name = MapleDataTool.getString(subindex.getChildByPath("name"), "");
                        int recordID = MapleDataTool.getInt(subindex.getChildByPath("recordID"), -1);
                        int rewardID = MapleDataTool.getInt(subindex.getChildByPath("rewardID"), -1);
                        int period = MapleDataTool.getInt(subindex.getChildByPath("period"), -1);
                        int rewardCount = MapleDataTool.getInt(subindex.getChildByPath("rewardCount"), -1);
                        subIndexInfo.setName(name);
                        subIndexInfo.setRecordID(recordID);
                        subIndexInfo.setRewardID(rewardID);
                        subIndexInfo.setPeriod(period);
                        subIndexInfo.setRewardCount(rewardCount);
                        si.setInfo(subIndexInfo);
                     } else if (subindex.getName().equals("group")) {
                        int groupNumber = 0;

                        for (MapleData group : subindex.getChildren()) {
                           try {
                              String name = MapleDataTool.getString(group.getChildByPath("name"), "");
                              int recordID = MapleDataTool.getInt(group.getChildByPath("recordID"), -1);
                              int rewardID = MapleDataTool.getInt(group.getChildByPath("rewardID"), -1);
                              int rewardCount = MapleDataTool.getInt(group.getChildByPath("rewardCount"), -1);
                              int exploraionCycle = MapleDataTool.getInt(group.getChildByPath("exploraionCycle"), -1);
                              int exploraionReward = MapleDataTool.getInt(group.getChildByPath("exploraionReward"), -1);
                              MonsterCollection.MobCollectionGroup groups = new MonsterCollection.MobCollectionGroup(
                                 name, recordID, rewardID, rewardCount, exploraionCycle, exploraionReward
                              );
                              int index = 0;

                              for (MapleData mobs : group.getChildByPath("mob")) {
                                 int questID = 100000 + region * 100 + Integer.parseInt(data1.getName());
                                 int type = MapleDataTool.getInt(mobs.getChildByPath("type"), 0);
                                 int mobTemplateID = MapleDataTool.getInt(mobs.getChildByPath("id"));
                                 String tooltip = MapleDataTool.getString(mobs.getChildByPath("tooltip"), null);
                                 int starRank = MapleDataTool.getInt(mobs.getChildByPath("starRank"), 1);
                                 String eliteName = MapleDataTool.getString(mobs.getChildByPath("eliteName"), null);
                                 int mobindex = 5 * groupNumber + index;
                                 MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
                                 if (mob != null) {
                                    mobByName.put(
                                       MapleLifeFactory.getMonster(mobTemplateID).getStats().getName(),
                                       new MonsterCollection.CollectionMobData(questID, type, mobTemplateID, mobindex, tooltip, starRank, eliteName)
                                    );
                                    groups.getMobs().add(mobTemplateID);
                                    index++;
                                 }
                              }

                              si.getGroup().add(groups);
                              groupNumber++;
                           } catch (Exception var33) {
                              System.out.println("MonsterCollect Err");
                              var33.printStackTrace();
                           }
                        }
                     }
                  }

                  collection.getSubIndexList().add(si);
               }
            }

            mobCollections.put(region, collection);
         }
      }
   }

   public static class CollectionMobData {
      int questID;
      int type;
      int mobTemplateID;
      int mobIndex;
      String tooltip;
      int starRank;
      String eliteName;

      public CollectionMobData(int questID, int type, int mobTemplateID, int mobIndex, String tooltip, int starRank, String eliteName) {
         this.questID = questID;
         this.type = type;
         this.mobTemplateID = mobTemplateID;
         this.mobIndex = mobIndex;
         this.tooltip = tooltip;
         this.starRank = starRank;
         this.eliteName = eliteName;
      }

      public int getQuestID() {
         return this.questID;
      }

      public int getType() {
         return this.type;
      }

      public int getMobTemplateID() {
         return this.mobTemplateID;
      }

      public int getMobIndex() {
         return this.mobIndex;
      }

      public String getTooltip() {
         return this.tooltip;
      }

      public int getStarRank() {
         return this.starRank;
      }

      public String getEliteName() {
         return this.eliteName;
      }
   }

   public static class CollectionSubIndex {
      MonsterCollection.MobCollectionInfo info;
      List<MonsterCollection.MobCollectionGroup> group = new ArrayList<>();

      public void setInfo(MonsterCollection.MobCollectionInfo info) {
         this.info = info;
      }

      public MonsterCollection.MobCollectionInfo getInfo() {
         return this.info;
      }

      public List<MonsterCollection.MobCollectionGroup> getGroup() {
         return this.group;
      }
   }

   public static class MobCollection {
      MonsterCollection.MobCollectionInfo info;
      List<MonsterCollection.CollectionSubIndex> subIndexList = new ArrayList<>();

      public void setInfo(MonsterCollection.MobCollectionInfo info) {
         this.info = info;
      }

      public MonsterCollection.MobCollectionInfo getInfo() {
         return this.info;
      }

      public List<MonsterCollection.CollectionSubIndex> getSubIndexList() {
         return this.subIndexList;
      }
   }

   public static class MobCollectionClearQuest {
      int clearCount;
      int recordID;
      int rewardID;
      int titleQuestID;

      public MobCollectionClearQuest(int clearCount, int recordID, int rewardID, int titleQuestID) {
         this.clearCount = clearCount;
         this.recordID = recordID;
         this.rewardID = rewardID;
         this.titleQuestID = titleQuestID;
      }

      public int getClearCount() {
         return this.clearCount;
      }

      public int getRecordID() {
         return this.recordID;
      }

      public int getRewardID() {
         return this.rewardID;
      }

      public int getTitleQuestID() {
         return this.titleQuestID;
      }
   }

   public static class MobCollectionGroup {
      String name;
      int recordID;
      int rewardID;
      int rewardCount;
      int exploraionCycle;
      int exploraionReward;
      List<Integer> mobs;

      public MobCollectionGroup(String name, int recordID, int rewardID, int rewardCount, int exploraionCycle, int exploraionReward) {
         this.name = name;
         this.recordID = recordID;
         this.rewardID = rewardID;
         this.rewardCount = rewardCount;
         this.exploraionCycle = exploraionCycle;
         this.exploraionReward = exploraionReward;
         this.mobs = new ArrayList<>();
      }

      public String getName() {
         return this.name;
      }

      public int getRecordID() {
         return this.recordID;
      }

      public int getRewardID() {
         return this.rewardID;
      }

      public int getRewardCount() {
         return this.rewardCount;
      }

      public int getExploraionCycle() {
         return this.exploraionCycle;
      }

      public int getExploraionReward() {
         return this.exploraionReward;
      }

      public List<Integer> getMobs() {
         return this.mobs;
      }
   }

   public static class MobCollectionInfo {
      String name;
      int recordID;
      int rewardID;
      int period;
      int rewardCount;
      List<MonsterCollection.MobCollectionClearQuest> clearQuest = new ArrayList<>();

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public int getRecordID() {
         return this.recordID;
      }

      public void setRecordID(int recordID) {
         this.recordID = recordID;
      }

      public int getRewardID() {
         return this.rewardID;
      }

      public void setRewardID(int rewardID) {
         this.rewardID = rewardID;
      }

      public int getPeriod() {
         return this.period;
      }

      public void setPeriod(int period) {
         this.period = period;
      }

      public int getRewardCount() {
         return this.rewardCount;
      }

      public void setRewardCount(int rewardCount) {
         this.rewardCount = rewardCount;
      }

      public List<MonsterCollection.MobCollectionClearQuest> getClearQuest() {
         return this.clearQuest;
      }

      public void setClearQuest(List<MonsterCollection.MobCollectionClearQuest> clearQuest) {
         this.clearQuest = clearQuest;
      }
   }
}
