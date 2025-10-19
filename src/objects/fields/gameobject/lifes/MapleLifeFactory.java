package objects.fields.gameobject.lifes;

import constants.GameConstants;
import constants.devtempConstants.MapleMonsterCustomHP;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import objects.fields.gameobject.lifes.mobskills.LinkMobInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.PassiveInfo;
import objects.fields.gameobject.lifes.mobskills.TransInfo;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataDirectoryEntry;
import objects.wz.provider.MapleDataFileEntry;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;
import objects.wz.provider.MapleDataType;

public class MapleLifeFactory {
   private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Mob.wz"));
   private static final MapleDataProvider npcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Npc.wz"));
   private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(
      new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")
   );
   private static final MapleDataProvider etcDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
   private static final MapleData mobStringData = stringDataWZ.getData("Mob.img");
   private static final MapleData npcStringData = stringDataWZ.getData("Npc.img");
   private static final MapleData npclocData = etcDataWZ.getData("NpcLocation.img");
   private static Map<Integer, String> npcNames = new HashMap<>();
   private static Map<Integer, String> npcFuncNames = new HashMap<>();
   private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<>();
   private static Map<Integer, Integer> NPCLoc = new HashMap<>();
   private static Map<Integer, List<Integer>> questCount = new HashMap<>();
   private static Map<Integer, MapleNPCInfo> npcInfo = new HashMap<>();
   private static Map<Integer, Boolean> npcmoveability = new HashMap<>();
   private static final List<MobSkill> mobRealSkillCache = new ArrayList<>();

   public static MobSkill getRealMobSkill(int mobSkillID, int mobSkillLevel) {
      for (MobSkill skill : mobRealSkillCache) {
         if (skill.getMobSkillID() == mobSkillID && skill.getLevel() == mobSkillLevel) {
            return skill;
         }
      }

      return null;
   }

   public static AbstractLoadedMapleLife getLife(int id, String type) {
      if (type.equalsIgnoreCase("n")) {
         return getNPC(id);
      } else if (type.equalsIgnoreCase("m")) {
         return getMonster(id);
      } else {
         System.err.println("Unknown Life type: " + type);
         return null;
      }
   }

   public static int getNPCLocation(int npcid) {
      if (NPCLoc.containsKey(npcid)) {
         return NPCLoc.get(npcid);
      } else {
         int map = MapleDataTool.getIntConvert(Integer.toString(npcid) + "/0", npclocData, -1);
         NPCLoc.put(npcid, map);
         return map;
      }
   }

   public static final void loadQuestCounts() {
      if (questCount.size() <= 0) {
         for (MapleDataDirectoryEntry mapz : MapleLifeFactory.data.getRoot("QuestCountGroup_000.wz").getSubdirectories()) {
            if (mapz.getName().contains("QuestCountGroup")) {
               for (MapleDataFileEntry entry : mapz.getFiles()) {
                  int id = Integer.parseInt(entry.getName().substring(0, entry.getName().length() - 4));
                  MapleData dat = MapleLifeFactory.data.getData("QuestCountGroup/" + entry.getName());
                  if (dat != null && dat.getChildByPath("info") != null) {
                     List<Integer> z = new ArrayList<>();

                     for (MapleData da : dat.getChildByPath("info")) {
                        z.add(MapleDataTool.getInt(da, 0));
                     }

                     questCount.put(id, z);
                  } else {
                     System.out.println("null questcountgroup");
                  }
               }
            }
         }

         for (MapleData c : npcStringData) {
            int nid = Integer.parseInt(c.getName());
            String n = StringUtil.getLeftPaddedStr(nid + ".img", '0', 11);

            try {
               if (npcData.getData(n) != null) {
                  String name = MapleDataTool.getString("name", c, "MISSINGNO");
                  if (!name.contains("Maple TV") && !name.contains("Baby Moon Bunny")) {
                     npcNames.put(nid, name);
                     String func = MapleDataTool.getString("func", c, "MISSINGNO");
                     npcFuncNames.put(nid, func);
                  }
               }
            } catch (NullPointerException var9) {
            } catch (RuntimeException var10) {
            }
         }

         for (MapleDataDirectoryEntry root : npcData.getRoot()) {
            for (MapleDataFileEntry npcc : root.getFiles()) {
               MapleData data = npcData.getData(npcc.getName());
               MapleData info = data.getChildByPath("info");
               int npcid = Integer.parseInt(npcc.getName().substring(0, 7));
               if (info != null) {
                  npcInfo.put(npcid, new MapleNPCInfo(info));
               }

               MapleData move = data.getChildByPath("move");
               if (move != null) {
                  npcmoveability.put(npcid, Boolean.TRUE);
               }
            }
         }
      }
   }

   public static final String getScript(int npcid) {
      return Optional.ofNullable(npcInfo.get(npcid)).map(MapleNPCInfo::getScript).orElse(null);
   }

   public static boolean hasForceMoveAbility(int npcid) {
      return Optional.ofNullable(npcInfo.get(npcid)).map(MapleNPCInfo::isForceMove).orElse(false);
   }

   public static boolean isStorage(int npcid) {
      Optional<MapleNPCInfo> o = Optional.ofNullable(npcInfo.get(npcid));
      return o.map(MapleNPCInfo::getTrunkPut).orElse(0) > 0 || o.map(MapleNPCInfo::getTrunkGet).orElse(0) > 0;
   }

   public static boolean hasMoveAbility(int npcid) {
      return npcmoveability.containsKey(npcid);
   }

   public static final List<Integer> getQuestCount(int id) {
      return questCount.get(id);
   }

   public static MapleMonster getMonster(int mid) {
      MapleMonsterStats stats = getMonsterStats(mid);
      return stats == null ? null : new MapleMonster(mid, stats);
   }

   public static void setCustomMobHP(int mid, long hp) {
      MapleMonsterStats stats = monsterStats.get(mid);
      if (stats != null && hp > 0L) {
         stats.setHp(hp);
         stats.setMaxHp(hp);
      }
   }

   public static MapleMonsterStats getMonsterStats(int mid) {
      MapleMonsterStats stats = monsterStats.get(mid);
      if (stats == null) {
         MapleData monsterData = null;

         try {
            monsterData = MapleLifeFactory.data.getData(StringUtil.getLeftPaddedStr(mid + ".img", '0', 11));
         } catch (RuntimeException var36) {
            return null;
         }

         if (monsterData == null) {
            return null;
         }

         MapleData monsterInfoData = monsterData.getChildByPath("info");
         stats = new MapleMonsterStats(mid);
         if (monsterData.getChildByPath("jump") != null) {
            stats.setMoveAbility(stats.getMoveAbility() | MoveAbility.Jump.getType());
         }

         if (monsterData.getChildByPath("fly") != null) {
            stats.setMoveAbility(stats.getMoveAbility() | MoveAbility.Fly.getType());
         }

         if (monsterData.getChildByPath("move") != null) {
            stats.setMoveAbility(stats.getMoveAbility() | MoveAbility.Walk.getType());
         }

         if (mobStringData.getChildByPath(String.valueOf(mid)) != null) {
            stats.setName(MapleDataTool.getString("name", mobStringData.getChildByPath(String.valueOf(mid)), "MISSINGNO"));
         }

         long maxHP = MapleMonsterCustomHP.getMaxHP(mid);
         if (maxHP <= 0L) {
            stats.setHp(
               GameConstants.getPartyPlayHP(mid) > 0
                  ? GameConstants.getPartyPlayHP(mid)
                  : (
                     monsterInfoData.getChildByPath("finalmaxHP") != null
                        ? MapleDataTool.getLongConvert("maxHP", monsterInfoData, 0) + MapleDataTool.getLongConvert("finalmaxHP", monsterInfoData, 0)
                        : MapleDataTool.getLongConvert("maxHP", monsterInfoData, 0)
                  )
            );
         } else {
            stats.setHp(maxHP);
         }

         stats.setMp(MapleDataTool.getIntConvert("maxMP", monsterInfoData, 0));
         stats.setMaxMp(stats.getMp());
         stats.setMaxHp(stats.getHp());
         stats.setExp(
            mid == 9300027
               ? 0L
               : (GameConstants.getPartyPlayEXP(mid) > 0 ? GameConstants.getPartyPlayEXP(mid) : MapleDataTool.getLongConvert("exp", monsterInfoData, 0))
         );
         stats.setLevel((short)MapleDataTool.getIntConvert("level", monsterInfoData, 1));
         stats.setCharismaEXP((short)MapleDataTool.getIntConvert("charismaEXP", monsterInfoData, 0));
         stats.setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", monsterInfoData, 0));
         stats.setrareItemDropLevel((byte)MapleDataTool.getIntConvert("rareItemDropLevel", monsterInfoData, 0));
         stats.setFixedDamage(MapleDataTool.getIntConvert("fixedDamage", monsterInfoData, -1));
         stats.setOnlyHittedByCommonAttack(MapleDataTool.getIntConvert("onlyHittedByCommonAttack", monsterInfoData, 0) > 0);
         stats.setOnlyNormalAttack(MapleDataTool.getIntConvert("onlyNormalAttack", monsterInfoData, 0) > 0);
         stats.setBoss(
            GameConstants.getPartyPlayHP(mid) > 0
               || MapleDataTool.getIntConvert("boss", monsterInfoData, 0) > 0
               || mid == 8810214
               || mid == 8810018
               || mid == 9410066
               || mid >= 8810118 && mid <= 8810122
         );
         stats.setSkeleton(MapleDataTool.getIntConvert("skeleton", monsterInfoData, 0) > 0);
         stats.setExplosiveReward(MapleDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0);
         stats.setUndead(MapleDataTool.getIntConvert("undead", monsterInfoData, 0) > 0);
         stats.setEscort(MapleDataTool.getIntConvert("escort", monsterInfoData, 0) > 0);
         stats.setAllyMob(MapleDataTool.getIntConvert("allyMob", monsterInfoData, 0) > 0);
         stats.setPartyBonus(GameConstants.getPartyPlayHP(mid) > 0 || MapleDataTool.getIntConvert("partyBonusMob", monsterInfoData, 0) > 0);
         stats.setPartyBonusRate(MapleDataTool.getIntConvert("partyBonusR", monsterInfoData, 0));
         stats.setBuffToGive(MapleDataTool.getIntConvert("buff", monsterInfoData, -1));
         stats.setHpNoticePerNum(MapleDataTool.getIntConvert("hpNoticePerNum", monsterInfoData, 100));
         stats.setChange(MapleDataTool.getIntConvert("changeableMob", monsterInfoData, 0) > 0);
         stats.setFriendly(MapleDataTool.getIntConvert("damagedByMob", monsterInfoData, 0) > 0);
         stats.setNoDoom(MapleDataTool.getIntConvert("noDoom", monsterInfoData, 0) > 0);
         stats.setFfaLoot(MapleDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0);
         stats.setCP((byte)MapleDataTool.getIntConvert("getCP", monsterInfoData, 0));
         stats.setPoint(MapleDataTool.getIntConvert("point", monsterInfoData, 0));
         stats.setDropItemPeriod(MapleDataTool.getIntConvert("dropItemPeriod", monsterInfoData, 0));
         stats.setPhysicalAttack(MapleDataTool.getIntConvert("PADamage", monsterInfoData, 0));
         stats.setMagicAttack(MapleDataTool.getIntConvert("MADamage", monsterInfoData, 0));
         stats.setPDRate((byte)MapleDataTool.getIntConvert("PDRate", monsterInfoData, 0));
         stats.setMDRate((byte)MapleDataTool.getIntConvert("MDRate", monsterInfoData, 0));
         stats.setAcc(MapleDataTool.getIntConvert("acc", monsterInfoData, 0));
         stats.setEva(MapleDataTool.getIntConvert("eva", monsterInfoData, 0));
         stats.setRewardSprinkleSpeed(MapleDataTool.getIntConvert("rewardSprinkleSpeed", monsterInfoData, 0));
         stats.setRewardSprinkle(MapleDataTool.getIntConvert("rewardSprinkle", monsterInfoData, 0));
         stats.setSummonType((byte)MapleDataTool.getIntConvert("summonType", monsterInfoData, 0));
         stats.setCategory((byte)MapleDataTool.getIntConvert("category", monsterInfoData, 0));
         stats.setSpeed(MapleDataTool.getIntConvert("speed", monsterInfoData, 0));
         stats.setPushed(MapleDataTool.getIntConvert("pushed", monsterInfoData, 0));
         stats.setEx(MapleDataTool.getIntConvert("ex", monsterInfoData, 0));
         if (MapleDataTool.getIntConvert("HPgaugeHide", monsterInfoData, 0) <= 0 && MapleDataTool.getIntConvert("hideHP", monsterInfoData, 0) <= 0) {
            boolean var74 = false;
         } else {
            boolean var10000 = true;
         }

         MapleData selfd = monsterInfoData.getChildByPath("selfDestruction");
         if (selfd != null) {
            stats.setSelfDHP(MapleDataTool.getIntConvert("hp", selfd, 0));
            stats.setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", selfd, stats.getRemoveAfter()));
            stats.setSelfD((byte)MapleDataTool.getIntConvert("action", selfd, -1));
         } else {
            stats.setSelfD((byte)-1);
         }

         MapleData trans = monsterInfoData.getChildByPath("trans");
         if (trans != null) {
            stats.setTransInfo(new TransInfo(trans));
         }

         MapleData linkMob = monsterInfoData.getChildByPath("linkMob");
         if (linkMob != null) {
            stats.setLinkMobInfo(new LinkMobInfo(linkMob));
         }

         MapleData passive = monsterInfoData.getChildByPath("passive");
         if (passive != null) {
            List<PassiveInfo> list = new ArrayList<>();

            for (MapleData root : passive.getChildren()) {
               list.add(new PassiveInfo(root));
            }

            stats.setPassiveInfos(list);
         }

         MapleData firstAttackData = monsterInfoData.getChildByPath("firstAttack");
         if (firstAttackData != null) {
            if (firstAttackData.getType() == MapleDataType.FLOAT) {
               stats.setFirstAttack(Math.round(MapleDataTool.getFloat(firstAttackData)) > 0);
            } else {
               stats.setFirstAttack(MapleDataTool.getInt(firstAttackData) > 0);
            }
         }

         if (stats.isBoss() || isDmgSponge(mid)) {
            if (monsterInfoData.getChildByPath("hpTagColor") != null && monsterInfoData.getChildByPath("hpTagBgcolor") != null) {
               stats.setTagColor(MapleDataTool.getIntConvert("hpTagColor", monsterInfoData));
               stats.setTagBgColor(MapleDataTool.getIntConvert("hpTagBgcolor", monsterInfoData));
            } else {
               stats.setTagColor(0);
               stats.setTagBgColor(0);
            }
         }

         MapleData banishData = monsterInfoData.getChildByPath("ban");
         if (banishData != null) {
            stats.setBanishInfo(
               new BanishInfo(
                  MapleDataTool.getString("banMsg", banishData),
                  MapleDataTool.getInt("banMap/0/field", banishData, -1),
                  MapleDataTool.getString("banMap/0/portal", banishData, "sp")
               )
            );
         }

         MapleData reviveInfo = monsterInfoData.getChildByPath("revive");
         if (reviveInfo != null) {
            List<Integer> revives = new LinkedList<>();

            for (MapleData bdata : reviveInfo) {
               revives.add(MapleDataTool.getInt(bdata, 0));
            }

            stats.setRevives(revives);
         }

         MapleData skeletonData = monsterData.getChildByPath("HitParts");
         if (skeletonData != null) {
            for (MapleData skeleton : skeletonData) {
               int durability = Integer.valueOf(MapleDataTool.getInt("0/stat/durability", skeleton, 0));
               stats.addSkeletonData(skeleton.getName(), 0, durability);
            }
         }

         for (MapleData data : monsterInfoData) {
            if (data.getName().equals("skill")) {
               List<MobSkill> list = new ArrayList<>();

               for (MapleData dd : data) {
                  if (StringUtil.isNumber(dd.getName())) {
                     MobSkill mobSkill = new MobSkill();
                     MapleData d = dd.getChildByPath("cooltimeOnSkill");
                     if (d != null) {
                        List<Integer> l = new ArrayList<>();
                        int i = 0;

                        while (true) {
                           int idx = MapleDataTool.getInt(String.valueOf(i++), d, -1);
                           if (idx == -1) {
                              mobSkill.setCooltimeOnSkill(l);
                              break;
                           }

                           l.add(idx);
                        }
                     }

                     MapleData wm = dd.getChildByPath("weatherMsg");
                     if (wm != null) {
                        mobSkill.setWeatherMsg(
                           new WeatherMsg(MapleDataTool.getString("msg", wm), MapleDataTool.getInt("time", wm), MapleDataTool.getInt("type", wm))
                        );
                     }

                     for (MapleData sn : dd) {
                        String name = sn.getName();
                        String value = MapleDataTool.getString(name, dd, "");
                        mobSkill.setMobSkillSN(Integer.parseInt(dd.getName()));
                        switch (name) {
                           case "skill":
                              mobSkill.setMobSkillID(Integer.parseInt(value));
                              break;
                           case "action":
                              mobSkill.setAction(Byte.parseByte(value));
                              break;
                           case "level":
                              mobSkill.setLevel(Integer.parseInt(value));
                              break;
                           case "effectAfter":
                              if (!value.equals("")) {
                                 mobSkill.setEffectAfter(Integer.parseInt(value));
                              }
                              break;
                           case "skillAfter":
                              mobSkill.setSkillAfter(Integer.parseInt(value));
                              break;
                           case "priority":
                              mobSkill.setPriority(Byte.parseByte(value));
                              break;
                           case "onlyFsm":
                              mobSkill.setOnlyFsm(Integer.parseInt(value) != 0);
                              break;
                           case "onlyOtherSkill":
                              mobSkill.setOnlyOtherSkill(Integer.parseInt(value) != 0);
                              break;
                           case "doFirst":
                              mobSkill.setDoFirst(Integer.parseInt(value) != 0);
                              break;
                           case "afterDead":
                              mobSkill.setAfterDead(Integer.parseInt(value) != 0);
                              break;
                           case "skillForbid":
                              mobSkill.setSkillForbid(Integer.parseInt(value));
                              break;
                           case "afterAttack":
                              mobSkill.setAfterAttack(Integer.parseInt(value));
                              break;
                           case "afterAttackCount":
                              mobSkill.setAfterAttackCount(Integer.parseInt(value));
                              break;
                           case "afterDelay":
                              mobSkill.setAfterDelay(Integer.parseInt(value));
                              break;
                           case "fixDamR":
                              mobSkill.setFixDamR(Integer.parseInt(value));
                              break;
                           case "preSkillIndex":
                              mobSkill.setPreSkillIndex(Integer.parseInt(value));
                              break;
                           case "preSkillCount":
                              mobSkill.setPreSkillCount(Integer.parseInt(value));
                              break;
                           case "castTime":
                              mobSkill.setCastTime(Integer.parseInt(value));
                              break;
                           case "cooltime":
                              mobSkill.setCoolTime(Integer.parseInt(value));
                              break;
                           case "delay":
                              mobSkill.setDelay(Integer.parseInt(value));
                              break;
                           case "useLimit":
                              mobSkill.setUseLimit(Integer.parseInt(value));
                              break;
                           case "info":
                              mobSkill.setInfo(value);
                              break;
                           case "text":
                              mobSkill.setText(value);
                              break;
                           case "speak":
                              mobSkill.setSpeak(value);
                        }
                     }

                     mobSkill.setIndex(list.size());
                     list.add(mobSkill);
                  }
               }

               mobRealSkillCache.addAll(list);
               stats.setSkills(list);
            }
         }

         decodeElementalString(stats, MapleDataTool.getString("elemAttr", monsterInfoData, ""));
         int link = MapleDataTool.getIntConvert("link", monsterInfoData, 0);
         if (link != 0) {
            try {
               monsterData = MapleLifeFactory.data.getData(StringUtil.getLeftPaddedStr(link + ".img", '0', 11));
            } catch (RuntimeException var35) {
               return null;
            }
         }

         MapleData dieDelay = monsterData.getChildByPath("die1");
         if (dieDelay != null) {
            for (MapleData bdata : dieDelay) {
               stats.setDieDelay(stats.getDieDelay() + MapleDataTool.getInt(bdata.getChildByPath("delay"), 0));
            }
         } else if (link != 0) {
            MapleData dieDelay_ = monsterData.getChildByPath("die1");
            if (dieDelay_ != null) {
               for (MapleData bdata : dieDelay_) {
                  stats.setDieDelay(stats.getDieDelay() + MapleDataTool.getInt(bdata.getChildByPath("delay"), 0));
               }
            }
         } else {
            stats.setDieDelay(1000);
         }

         for (MapleData idata : monsterData) {
            if (idata.getName().equals("fly")) {
               stats.setFly(true);
               stats.setMobile(true);
               break;
            }

            if (idata.getName().equals("move")) {
               stats.setMobile(true);
            }
         }

         int i = 0;

         while (true) {
            MapleData monsterAtt = monsterInfoData.getChildByPath("attack/" + i);
            MapleData attackData = monsterData.getChildByPath("attack" + (i + 1) + "/info");
            if (attackData == null || monsterAtt == null) {
               byte hpdisplaytype = -1;
               if (stats.getTagColor() > 0) {
                  hpdisplaytype = 0;
               } else if (stats.isFriendly()) {
                  hpdisplaytype = 1;
               } else if (mid >= 9300184 && mid <= 9300215) {
                  hpdisplaytype = 2;
               } else if (!stats.isBoss() || mid == 9410066 || stats.isPartyBonus()) {
                  hpdisplaytype = 3;
               }

               stats.setHPDisplayType(hpdisplaytype);
               monsterStats.put(mid, stats);
               break;
            }

            MobAttackInfo ret = new MobAttackInfo();
            boolean deadlyAttack = monsterAtt.getChildByPath("deadlyAttack") != null;
            if (!deadlyAttack) {
               deadlyAttack = attackData.getChildByPath("deadlyAttack") != null;
            }

            ret.setDeadlyAttack(deadlyAttack);
            int mpBurn = MapleDataTool.getInt("mpBurn", monsterAtt, 0);
            if (mpBurn == 0) {
               mpBurn = MapleDataTool.getInt("mpBurn", attackData, 0);
            }

            ret.setMpBurn(mpBurn);
            int disease = MapleDataTool.getInt("disease", monsterAtt, 0);
            if (disease == 0) {
               disease = MapleDataTool.getInt("disease", attackData, 0);
            }

            ret.setDiseaseSkill(disease);
            int fixDamR = MapleDataTool.getInt("fixDamR", monsterAtt, 0);
            if (fixDamR == 0) {
               fixDamR = MapleDataTool.getInt("fixDamR", attackData, 0);
            }

            ret.setFixDamR(fixDamR);
            int fixDamRType = MapleDataTool.getInt("fixDamRType", monsterAtt, 0);
            if (fixDamRType == 0) {
               fixDamRType = MapleDataTool.getInt("fixDamRType", attackData, 0);
            }

            ret.setFixDamRType(fixDamRType);
            int level = MapleDataTool.getInt("level", monsterAtt, 0);
            if (level == 0) {
               level = MapleDataTool.getInt("level", attackData, 0);
            }

            ret.setDiseaseLevel(level);
            int conMP = MapleDataTool.getInt("conMP", monsterAtt, 0);
            if (conMP == 0) {
               conMP = MapleDataTool.getInt("conMP", attackData, 0);
            }

            ret.setMpCon(conMP);
            int PADamage = MapleDataTool.getInt("PADamage", monsterAtt, 0);
            if (PADamage == 0) {
               PADamage = MapleDataTool.getInt("PADamage", attackData, 0);
            }

            ret.PADamage = PADamage;
            int MADamage = MapleDataTool.getInt("MADamage", monsterAtt, 0);
            if (MADamage == 0) {
               MADamage = MapleDataTool.getInt("MADamage", attackData, 0);
            }

            ret.MADamage = MADamage;
            boolean magic = MapleDataTool.getInt("magic", monsterAtt, 0) > 0;
            if (!magic) {
               magic = MapleDataTool.getInt("magic", attackData, 0) > 0;
            }

            boolean da = MapleDataTool.getInt("deadlyAttack", monsterAtt, 0) == 1;
            if (!da) {
               da = MapleDataTool.getInt("deadlyAttack", attackData, 0) == 1;
            }

            ret.deadlyAttack = da;
            ret.magic = magic;
            ret.isElement = monsterAtt.getChildByPath("elemAttr") != null;
            if (attackData.getChildByPath("range") != null) {
               ret.range = MapleDataTool.getInt("range/r", attackData, 0);
               if (attackData.getChildByPath("range/lt") != null && attackData.getChildByPath("range/rb") != null) {
                  ret.lt = (Point)attackData.getChildByPath("range/lt").getData();
                  ret.rb = (Point)attackData.getChildByPath("range/rb").getData();
               }
            }

            MapleData cs = monsterAtt.getChildByPath("callSkill");
            if (cs != null) {
               for (MapleData c : cs) {
                  ret.callSkillInfo
                     .add(new CallSkillInfo(MapleDataTool.getInt("delay", c, 0), MapleDataTool.getInt("level", c, 0), MapleDataTool.getInt("skill", c, 0)));
               }
            }

            MapleData cswd = monsterAtt.getChildByPath("callSkillWithData");
            if (cswd != null) {
               ret.callSkillWithData = new CallSkillInfoWithData(
                  MapleDataTool.getInt("delay", cswd, 0), MapleDataTool.getInt("level", cswd, 0), MapleDataTool.getInt("skill", cswd, 0)
               );
            }

            MapleData wm = monsterAtt.getChildByPath("weatherMsg");
            if (wm != null) {
               ret.weatherMsg = new WeatherMsg(MapleDataTool.getString("msg", wm), MapleDataTool.getInt("time", wm), MapleDataTool.getInt("type", wm));
            }

            ret.afterDead = MapleDataTool.getInt("afterDead", monsterAtt, 0) == 1;
            ret.afterAttack = MapleDataTool.getInt("afterAttack", monsterAtt, 0);
            ret.afterAttackCount = MapleDataTool.getInt("afterAttackCount", monsterAtt, 0);
            stats.addMobAttack(ret);
            i++;
         }
      }

      MapleMonsterStats retx = new MapleMonsterStats(mid);
      retx.copy(stats);
      return retx;
   }

   public static final void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
      for (int i = 0; i < elemAttr.length(); i += 2) {
         stats.setEffectiveness(
            Element.getFromChar(elemAttr.charAt(i)), ElementalEffectiveness.getByNumber(Integer.valueOf(String.valueOf(elemAttr.charAt(i + 1))))
         );
      }
   }

   private static final boolean isDmgSponge(int mid) {
      switch (mid) {
         case 8810018:
         case 8810118:
         case 8810119:
         case 8810120:
         case 8810121:
         case 8810122:
         case 8810214:
         case 8820009:
         case 8820010:
         case 8820011:
         case 8820012:
         case 8820013:
         case 8820014:
            return true;
         default:
            return false;
      }
   }

   public static MapleNPC getNPC(int nid) {
      String name = npcNames.get(nid);
      if (name == null) {
         return null;
      } else {
         String func = npcFuncNames.get(nid);
         MapleNPC npc = new MapleNPC(nid, name);
         if (func != null) {
            npc.setFunc(func);
         }

         return npc;
      }
   }

   public static int getRandomNPC() {
      List<Integer> vals = new ArrayList<>(npcNames.keySet());
      int ret = 0;

      while (ret <= 0) {
         ret = vals.get(Randomizer.nextInt(vals.size()));
         if (npcNames.get(ret).contains("MISSINGNO")) {
            ret = 0;
         }
      }

      return ret;
   }
}
