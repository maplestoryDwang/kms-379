package network.game.processors.monstercollection;

import constants.GameConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import objects.context.MonsterCollection;
import objects.item.MapleInventoryType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MobCollectionEx;
import objects.utils.Randomizer;

public class MonsterCollectionHandler {
   public static void RewardMonsterCollection(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         int opcode = slea.readInt();
         int region = slea.readInt();
         int session = slea.readInt();
         int group = slea.readInt();
         int startIndex = slea.readInt();
         int exploreIndex = 0;
         if (slea.available() >= 4L) {
            exploreIndex = slea.readInt();
         }

         MonsterCollection.MobCollection mc = getMonsterCollectionData(c, region, session, group);
         if (mc == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(user));
         } else {
            MonsterCollection.CollectionSubIndex si = mc.getSubIndexList().get(session);
            MonsterCollection.MobCollectionGroup cGroup = si.getGroup().get(group);
            int recordID = cGroup.getRecordID();
            int rewardID = cGroup.getRewardID();
            int rewardCount = cGroup.getRewardCount();
            if (rewardCount < 0) {
               rewardCount *= -1;
            }

            int explorationCycle = cGroup.getExploraionCycle();
            int explorationReward = cGroup.getExploraionReward();
            if (explorationReward == -1) {
               explorationReward = 0;
            }

            int specialReward = si.getInfo().getRewardID();
            int specialRewardCount = si.getInfo().getRewardCount();
            if (specialRewardCount < 0) {
               specialRewardCount *= -1;
            }

            int specialRewardPeriod = si.getInfo().getPeriod();
            int rewardCollection = 0;
            int specialRewardCollection = 1;
            int rewardMedal = 3;
            int rewardExplore = 4;
            switch (opcode) {
               case 0:
                  if (!MonsterCollection.checkLineMobOnCollection(user, region, session, group)) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  if (user.getInventory(GameConstants.getInventoryType(rewardID)).getNumFreeSlot() < rewardCount) {
                     user.dropMessage(1, "인벤토리 공간이 부족합니다.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  String key = String.format("G:%d:%d:%d:0", region, session, group);
                  if (user.getOneInfoQuestInteger(recordID, key) != 0) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  user.updateOneInfo(recordID, key, "1");
                  String realKey = user.getInfoQuest(recordID).getData();
                  user.getCollectionInfo().put(recordID, new MobCollectionEx(recordID, realKey));
                  PacketEncoder mplew = new PacketEncoder();
                  mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                  mplew.write(39);
                  mplew.writeInt(recordID);
                  mplew.writeMapleAsciiString(realKey);
                  c.getSession().writeAndFlush(mplew.getPacket());
                  mplew = new PacketEncoder();
                  mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
                  mplew.writeInt(0);
                  mplew.writeInt(rewardCount);

                  for (int i = 0; i < rewardCount; i++) {
                     mplew.writeInt(rewardID);
                     mplew.writeInt(1);
                  }

                  c.getSession().writeAndFlush(mplew.getPacket());
                  user.gainItem(rewardID, rewardCount, false, -1L, "몬스터 컬렉션 보상");
                  break;
               case 1:
                  if (!MonsterCollection.checkSessionMobOnCollection(user, region, session)) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  if (user.getInventory(GameConstants.getInventoryType(rewardID)).getNumFreeSlot() < rewardCount) {
                     user.dropMessage(1, "인벤토리 공간이 부족합니다.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  key = String.format("S:%d:%d:%d:0", region, session, group);
                  if (user.getOneInfoQuestInteger(recordID, key) != 0) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  user.updateOneInfo(recordID, key, "1");
                  realKey = user.getInfoQuest(recordID).getData();
                  user.getCollectionInfo().put(recordID, new MobCollectionEx(recordID, realKey));
                  mplew = new PacketEncoder();
                  mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                  mplew.write(39);
                  mplew.writeInt(recordID);
                  mplew.writeMapleAsciiString(realKey);
                  c.getSession().writeAndFlush(mplew.getPacket());
                  mplew = new PacketEncoder();
                  mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
                  mplew.writeInt(0);
                  mplew.writeInt(specialRewardCount);

                  for (int i = 0; i < specialRewardCount; i++) {
                     mplew.writeInt(specialReward);
                     mplew.writeInt(1);
                  }

                  c.getSession().writeAndFlush(mplew.getPacket());
                  user.gainItem(specialReward, specialRewardCount, false, -1L, "몬스터 컬렉션 스페셜 보상");
               case 2:
               default:
                  break;
               case 3:
                  if (!MonsterCollection.checkSessionMobOnCollection(user, region, session)) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  if (mc.getInfo() == null) {
                     user.dropMessage(1, "알 수 없는 오류가 발생했습니다.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  MonsterCollection.MobCollectionClearQuest cc = null;
                  Iterator sdf = mc.getInfo().getClearQuest().iterator();

                  while (true) {
                     if (sdf.hasNext()) {
                        MonsterCollection.MobCollectionClearQuest cq = (MonsterCollection.MobCollectionClearQuest)sdf.next();
                        if (user.getQuestStatus(cq.getTitleQuestID()) == 2) {
                           continue;
                        }

                        cc = cq;
                     }

                     if (cc == null) {
                        user.dropMessage(1, "알 수 없는 오류가 발생했습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                        return;
                     }

                     int ccRecordID = cc.getRecordID();
                     int titleQuestID = cc.getTitleQuestID();
                     int ccRewardID = cc.getRewardID();
                     int clearCount = cc.getClearCount();
                     int count = MonsterCollection.getTotalCollectionByRegion(user, region);
                     if (count < clearCount) {
                        user.dropMessage(1, "알 수 없는 오류가 발생했습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                        return;
                     }

                     mplew = new PacketEncoder();
                     if (user.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                        mplew = new PacketEncoder();
                        mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
                        mplew.writeInt(3);
                        mplew.writeInt(1);
                        mplew.writeInt(1);
                        mplew.writeInt(1);
                        c.getSession().writeAndFlush(mplew.getPacket());
                        return;
                     }

                     user.updateOneInfo(ccRecordID, String.format("cc%d", clearCount), "1");
                     String fullKey = user.getInfoQuest(ccRecordID).getData();
                     if (user.getCollectionInfo().getOrDefault(ccRecordID, null) != null) {
                        user.getCollectionInfo().replace(ccRecordID, new MobCollectionEx(ccRecordID, fullKey));
                     } else {
                        user.getCollectionInfo().put(ccRecordID, new MobCollectionEx(ccRecordID, fullKey));
                     }

                     user.forceCompleteQuest(titleQuestID);
                     mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                     mplew.write(39);
                     mplew.writeInt(ccRecordID);
                     mplew.writeMapleAsciiString(fullKey);
                     c.getSession().writeAndFlush(mplew.getPacket());
                     mplew = new PacketEncoder();
                     mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
                     mplew.writeInt(0);
                     mplew.writeInt(1);
                     mplew.writeInt(ccRewardID);
                     mplew.writeInt(1);
                     c.getSession().writeAndFlush(mplew.getPacket());
                     user.gainItem(ccRewardID, 1, false, -1L, "몬스터 컬렉션 훈장 보상");
                     break;
                  }
               case 4:
                  int defaultKey = 20 + exploreIndex;
                  SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");
                  Date cycleDate = new Date();
                  String endTime = sdf2.format(cycleDate);
                  String end = user.getOneInfo(defaultKey, "end");
                  if (user.getOneInfo(defaultKey, "end") == null || user.getOneInfo(defaultKey, "end").isEmpty()) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  try {
                     Date now = sdf2.parse(sdf2.format(new Date()));
                     if (now.compareTo(sdf2.parse(end)) < 0) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                        return;
                     }
                  } catch (Exception var40) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  if (user.getInventory(MapleInventoryType.USE).getNumFreeSlot() < 5
                     || user.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 3
                     || user.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                     user.dropMessage(1, "인벤토리 공간이 부족합니다. 소비슬롯 5칸 기타슬롯 3칸 캐시슬롯2칸 이상을 비워주세요.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(user));
                     return;
                  }

                  int[][] firstLineRewards = new int[][]{{2632808}, {2632808}, {2632808}, {2632808}};
                  int[][] secondLineRewards = new int[][]{
                     {2022798, 2022795, 2022796, 2022797, 2022794, 2022799},
                     {2433604, 2022798, 2022795, 2022796, 2022797, 2022794, 2022799},
                     {2433604, 2022798, 2022795, 2022796, 2022797, 2022794, 2022799},
                     {2433604, 2022798, 2022795, 2022796, 2022797, 2022794, 2022799}
                  };
                  int[][] thirdLineRewards = new int[][]{{4001832}, {2711013}, {2048745, 2711013}, {2048746, 5062010, 2048745}};
                  int firstReward = firstLineRewards[explorationReward][Randomizer.nextInt(firstLineRewards[explorationReward].length)];
                  int secondReward = secondLineRewards[explorationReward][Randomizer.nextInt(secondLineRewards[explorationReward].length)];
                  int thirdReward = thirdLineRewards[explorationReward][Randomizer.nextInt(thirdLineRewards[explorationReward].length)];
                  int firstQty = 30;
                  int secondQty = secondReward == 2433604 ? 1 : 3 + explorationReward * 5;
                  int thirdQty = secondReward == 4001832 ? 3000 : explorationReward * 1;
                  PacketEncoder mplewx = new PacketEncoder();
                  mplewx.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                  mplewx.write(39);
                  mplewx.writeInt(defaultKey);
                  mplewx.writeMapleAsciiString(String.format("mobKey=%d:0:%d:0;end=%s;state=0", region, group, endTime));
                  c.getSession().writeAndFlush(mplewx.getPacket());
                  mplewx = new PacketEncoder();
                  mplewx.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
                  mplewx.writeInt(9);
                  mplewx.writeInt(3);
                  mplewx.writeInt(firstReward);
                  mplewx.writeInt(firstQty);
                  mplewx.writeInt(secondReward);
                  mplewx.writeInt(secondQty);
                  mplewx.writeInt(thirdReward);
                  mplewx.writeInt(thirdQty);
                  c.getSession().writeAndFlush(mplewx.getPacket());
                  key = String.format("mobKey=%d:%d:%d:0;end=%s;state=0", region, session, group, endTime);
                  user.updateInfoQuest(defaultKey, key);
                  if (user.getCollectionInfo().getOrDefault(defaultKey, null) != null) {
                     user.getCollectionInfo().replace(defaultKey, new MobCollectionEx(defaultKey, key));
                  } else {
                     user.getCollectionInfo().put(defaultKey, new MobCollectionEx(defaultKey, key));
                  }

                  user.gainItem(firstReward, firstQty, false, -1L, "몬스터 컬렉션 탐험보상");
                  user.gainItem(secondReward, secondQty, false, -1L, "몬스터 컬렉션 탐험보상");
                  user.gainItem(thirdReward, thirdQty, false, -1L, "몬스터 컬렉션 탐험보상");
            }
         }
      }
   }

   public static void ExploreMonsterCollection(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         int region = slea.readInt();
         int session = slea.readInt();
         int group = slea.readInt();
         MonsterCollection.MobCollection mc = getMonsterCollectionData(c, region, session, group);
         if (mc == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(user));
         } else {
            MonsterCollection.CollectionSubIndex si = mc.getSubIndexList().get(session);
            MonsterCollection.MobCollectionGroup cGroup = si.getGroup().get(group);
            int recordID = cGroup.getRecordID();
            int rewardID = cGroup.getRewardID();
            int rewardCount = cGroup.getRewardCount();
            if (rewardCount < 0) {
               rewardCount *= -1;
            }

            int explorationCycle = cGroup.getExploraionCycle();
            int explorationReward = cGroup.getExploraionReward();
            int specialReward = si.getInfo().getRewardID();
            int specialRewardCount = si.getInfo().getRewardCount();
            if (specialRewardCount < 0) {
               specialRewardCount *= -1;
            }

            int specialRewardPeriod = si.getInfo().getPeriod();
            int exploreSlot = 2;
            int totalCollection = MonsterCollection.getTotalCollection(user);
            if (totalCollection >= 150) {
               exploreSlot++;
            }

            if (totalCollection >= 300) {
               exploreSlot++;
            }

            if (totalCollection >= 600) {
               exploreSlot++;
            }

            if (!MonsterCollection.checkLineMobOnCollection(user, region, session, group)) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(user));
            } else {
               int defaultKey = 20;

               while (defaultKey - 20 != exploreSlot - 1 && user.getOneInfoQuestInteger(defaultKey, "state") != 0) {
                  defaultKey++;
               }

               SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmm");
               Date cycleDate = new Date();
               cycleDate.setTime(cycleDate.getTime() + 60000 * explorationCycle);
               String endTime = sdf.format(cycleDate);
               PacketEncoder mplew = new PacketEncoder();
               mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
               mplew.write(39);
               mplew.writeInt(defaultKey);
               mplew.writeMapleAsciiString(String.format("mobKey=%d:%d:%d:0;end=%s;state=1", region, session, group, endTime));
               user.updateInfoQuest(defaultKey, String.format("mobKey=%d:%d:%d:0;end=%s;state=1", region, session, group, endTime));
               user.getCollectionInfo()
                  .put(defaultKey, new MobCollectionEx(defaultKey, String.format("mobKey=%d:%d:%d:0;end=%s;state=1", region, session, group, endTime)));
               c.getSession().writeAndFlush(mplew.getPacket());
               mplew = new PacketEncoder();
               mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
               mplew.writeInt(8);
               mplew.writeInt(0);
               mplew.writeInt(0);
               mplew.writeInt(0);
               c.getSession().writeAndFlush(mplew.getPacket());
            }
         }
      }
   }

   public static void CancelExploreMonsterCollection(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         int region = slea.readInt();
         int session = slea.readInt();
         int group = slea.readInt();
         int exploreIndex = slea.readInt();
         if (exploreIndex > 4) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(user));
         } else {
            MonsterCollection.MobCollection mc = getMonsterCollectionData(c, region, session, group);
            if (mc == null) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(user));
            } else {
               MonsterCollection.CollectionSubIndex si = mc.getSubIndexList().get(session);
               MonsterCollection.MobCollectionGroup cGroup = si.getGroup().get(group);
               int explorationCycle = cGroup.getExploraionCycle();
               SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmm");
               Date cycleDate = new Date();
               cycleDate.setTime(cycleDate.getTime() + 60000 * explorationCycle);
               String endTime = sdf.format(cycleDate);
               int defaultKey = 20 + exploreIndex;
               PacketEncoder mplew = new PacketEncoder();
               mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
               mplew.write(39);
               mplew.writeInt(defaultKey);
               mplew.writeMapleAsciiString(String.format("mobKey=%d:%d:%d:0;end=%s;state=0", region, session, group, endTime));
               c.getSession().writeAndFlush(mplew.getPacket());
               user.updateInfoQuest(defaultKey, String.format("mobKey=%d:%d:%d:0;end=%s;state=0", region, session, group, endTime));
               user.getCollectionInfo()
                  .put(defaultKey, new MobCollectionEx(defaultKey, String.format("mobKey=%d:%d:%d:0;end=%s;state=0", region, session, group, endTime)));
               mplew = new PacketEncoder();
               mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
               mplew.writeInt(16);
               mplew.writeInt(0);
               mplew.writeInt(0);
               mplew.writeInt(0);
               c.getSession().writeAndFlush(mplew.getPacket());
            }
         }
      }
   }

   public static MonsterCollection.MobCollection getMonsterCollectionData(MapleClient c, int region, int session, int group) {
      MapleCharacter user = c.getPlayer();
      MonsterCollection.MobCollection mc = MonsterCollection.mobCollections.getOrDefault(region, null);
      if (mc == null) {
         if (user.isGM()) {
            user.dropMessage(6, "mc == null 입니다.");
         }

         c.getSession().writeAndFlush(CWvsContext.enableActions(user));
         return null;
      } else {
         MonsterCollection.CollectionSubIndex si = mc.getSubIndexList().get(session);
         if (si == null) {
            if (user.isGM()) {
               user.dropMessage(6, "si == null 입니다.");
            }

            c.getSession().writeAndFlush(CWvsContext.enableActions(user));
            return null;
         } else {
            MonsterCollection.MobCollectionGroup cGroup = si.getGroup().get(group);
            if (cGroup == null) {
               if (user.isGM()) {
                  user.dropMessage(6, "cGroup == null 입니다.");
               }

               c.getSession().writeAndFlush(CWvsContext.enableActions(user));
               return null;
            } else {
               return mc;
            }
         }
      }
   }
}
