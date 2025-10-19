package objects.fields.gameobject.lifes.mobskills;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.fields.child.jinhillah.JinHillahPoisonMist;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataDirectoryEntry;
import objects.wz.provider.MapleDataFileEntry;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;
import objects.wz.provider.MapleDataType;

public class MobSkillFactory {
   private static final MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider(
      new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz")
   );
   private final Map<Pair<Integer, Integer>, MobSkillInfo> mobSkillCache = new HashMap<>();
   private static final MobSkillFactory instance = new MobSkillFactory();

   public MobSkillFactory() {
      this.initialize();
   }

   public static MobSkillFactory getInstance() {
      return instance;
   }

   public static MobSkillInfo getMobSkill(int skillId, int level) {
      return instance.mobSkillCache.get(new Pair<>(skillId, level));
   }

   private void initialize() {
      this.load(datasource.getRoot("MobSkill_000.wz"));
      this.load(datasource.getRoot("MobSkill_001.wz"));
   }

   private void load(MapleDataDirectoryEntry root) {
      for (MapleDataFileEntry topDir : root.getFiles()) {
         for (MapleData data : datasource.getData(topDir.getName())) {
            if (data.getName().equals("level")) {
               int skillID = Integer.parseInt(topDir.getName().split(".img")[0]);

               for (MapleData level : data.getChildren()) {
                  if (level != null) {
                     int lv = Integer.parseInt(level.getName());
                     List<Integer> toSummon = new ArrayList<>();
                     String summ = MapleDataTool.getString("summons", level, "");
                     String[] summs = summ.split(", ");
                     if (summs.length <= 0 && summ.length() > 0) {
                        toSummon.add(Integer.parseInt(summ));
                     }

                     for (String s : summs) {
                        if (s.length() > 0) {
                           toSummon.add(Integer.parseInt(s));
                        }
                     }

                     Point lt = null;
                     Point rb = null;
                     Point lt2 = null;
                     Point rb2 = null;
                     MobSkillInfo ret = new MobSkillInfo(skillID, lv);
                     MapleData cmd = level.getChildByPath("command");
                     if (cmd != null) {
                        FieldCommand fieldCommand = new FieldCommand(cmd);
                        ret.setFieldCommand(fieldCommand);
                     }

                     MapleData mobGroup = level.getChildByPath("mobGroup");
                     if (mobGroup != null) {
                        for (MapleData r : mobGroup.getChildren()) {
                           int idx = Integer.parseInt(r.getName());
                           List<Integer> groups = new ArrayList<>();

                           for (MapleData group : r.getChildren()) {
                              groups.add(MapleDataTool.getInt(group, 0));
                           }

                           ret.addMobGroup(groups);
                        }
                     }

                     MapleData affected = level.getChildByPath("affectedOtherSkill");
                     if (affected != null) {
                        int idx = 0;

                        while (true) {
                           MapleData d = affected.getChildByPath(String.valueOf(idx++));
                           if (d == null) {
                              if (ret.getAffectedOtherSkills().isEmpty()) {
                                 int affectedOtherSkillID = MapleDataTool.getInt("affectedOtherSkillID", affected, 0);
                                 int affectedOtherSkillLev = MapleDataTool.getInt("affectedOtherSkillLev", affected, 0);
                                 ret.addAffectedOtherSkill(new AffectedOtherSkill(affectedOtherSkillID, affectedOtherSkillLev));
                              }
                              break;
                           }

                           int affectedOtherSkillID = MapleDataTool.getInt("affectedOtherSkillID", d, 0);
                           int affectedOtherSkillLev = MapleDataTool.getInt("affectedOtherSkillLev", d, 0);
                           ret.addAffectedOtherSkill(new AffectedOtherSkill(affectedOtherSkillID, affectedOtherSkillLev));
                        }
                     }

                     MapleData otherSkill = level.getChildByPath("otherSkill");
                     if (otherSkill != null) {
                        ret.setOtherSkillID(MapleDataTool.getInt("otherSkillID", otherSkill, 0));
                        ret.setOtherSkillLev(MapleDataTool.getInt("otherSkillLev", otherSkill, 0));
                     }

                     for (MapleData name : level.getChildren()) {
                        String name_ = name.getName();
                        if (name.getType() != MapleDataType.CANVAS && name.getType() != MapleDataType.SOUND && name.getType() != MapleDataType.VECTOR) {
                           String value = MapleDataTool.getString(name_, level, "");
                           if (!value.isEmpty()) {
                              ret.addSummons(toSummon);
                              MapleData sx = level.getChildByPath("succeed");
                              if (sx != null) {
                                 MobSkillInfo.CastingActionData cad = new MobSkillInfo.CastingActionData(sx);
                                 ret.setSucceed(cad);
                              }

                              sx = level.getChildByPath("failed");
                              if (sx != null) {
                                 MobSkillInfo.CastingActionData cad = new MobSkillInfo.CastingActionData(sx);
                                 ret.setFailed(cad);
                              }

                              if (ret.getScreen_delay().get(lv) == null) {
                                 sx = level.getChildByPath("screen");
                                 if (sx != null) {
                                    for (MapleData bdata : sx) {
                                       if (ret.getScreen_delay().get(lv) == null) {
                                          ret.getScreen_delay().put(lv, 0);
                                       }

                                       ret.getScreen_delay().put(lv, ret.getScreen_delay().get(lv) + MapleDataTool.getInt(bdata.getChildByPath("delay"), 0));
                                    }
                                 }
                              }

                              switch (name_) {
                                 case "x":
                                    ret.putMobSkillStats(MobSkillStat.x, value);
                                    break;
                                 case "y":
                                    ret.putMobSkillStats(MobSkillStat.y, value);
                                    break;
                                 case "mpCon":
                                    ret.putMobSkillStats(MobSkillStat.mpCon, value);
                                    break;
                                 case "interval":
                                 case "inteval":
                                    ret.putMobSkillStats(MobSkillStat.interval, value);
                                    break;
                                 case "hp":
                                 case "HP":
                                    ret.putMobSkillStats(MobSkillStat.hp, value);
                                    break;
                                 case "info":
                                    ret.putMobSkillStats(MobSkillStat.info, value);
                                    break;
                                 case "limit":
                                    ret.putMobSkillStats(MobSkillStat.limit, value);
                                    break;
                                 case "broadCastScreenMsg":
                                    ret.putMobSkillStats(MobSkillStat.broadCastScreenMsg, value);
                                    break;
                                 case "w":
                                    ret.putMobSkillStats(MobSkillStat.w, value);
                                    break;
                                 case "z":
                                    ret.putMobSkillStats(MobSkillStat.z, value);
                                    break;
                                 case "parsing":
                                    ret.putMobSkillStats(MobSkillStat.parsing, value);
                                    break;
                                 case "prop":
                                    ret.putMobSkillStats(MobSkillStat.prop, value);
                                    break;
                                 case "ignoreResist":
                                    ret.putMobSkillStats(MobSkillStat.ignoreResist, value);
                                    break;
                                 case "count":
                                    ret.putMobSkillStats(MobSkillStat.count, value);
                                    break;
                                 case "time":
                                    ret.putMobSkillStats(MobSkillStat.time, value);
                                    break;
                                 case "targetAggro":
                                    ret.putMobSkillStats(MobSkillStat.targetAggro, value);
                                    break;
                                 case "fieldScript":
                                    ret.putMobSkillStats(MobSkillStat.fieldScript, value);
                                    break;
                                 case "elemAttr":
                                    ret.putMobSkillStats(MobSkillStat.elemAttr, value);
                                    break;
                                 case "delay":
                                    ret.putMobSkillStats(MobSkillStat.delay, value);
                                    break;
                                 case "rank":
                                    ret.putMobSkillStats(MobSkillStat.rank, value);
                                    break;
                                 case "HPDeltaR":
                                    ret.putMobSkillStats(MobSkillStat.HPDeltaR, value);
                                    break;
                                 case "summonEffect":
                                    ret.putMobSkillStats(MobSkillStat.summonEffect, value);
                                    break;
                                 case "y2":
                                    ret.putMobSkillStats(MobSkillStat.y2, value);
                                    break;
                                 case "q":
                                    ret.putMobSkillStats(MobSkillStat.q, value);
                                    break;
                                 case "q2":
                                    ret.putMobSkillStats(MobSkillStat.q2, value);
                                    break;
                                 case "s":
                                    ret.putMobSkillStats(MobSkillStat.s, value);
                                    break;
                                 case "s2":
                                    ret.putMobSkillStats(MobSkillStat.s2, value);
                                    break;
                                 case "u":
                                    ret.putMobSkillStats(MobSkillStat.u, value);
                                    break;
                                 case "u2":
                                    ret.putMobSkillStats(MobSkillStat.u2, value);
                                    break;
                                 case "v":
                                    ret.putMobSkillStats(MobSkillStat.v, value);
                                    break;
                                 case "v2":
                                    ret.putMobSkillStats(MobSkillStat.v2, value);
                                    break;
                                 case "z2":
                                    ret.putMobSkillStats(MobSkillStat.z2, value);
                                    break;
                                 case "w2":
                                    ret.putMobSkillStats(MobSkillStat.w2, value);
                                    break;
                                 case "skillAfter":
                                    ret.putMobSkillStats(MobSkillStat.skillAfter, value);
                                    break;
                                 case "x2":
                                    ret.putMobSkillStats(MobSkillStat.x2, value);
                                    break;
                                 case "script":
                                    ret.putMobSkillStats(MobSkillStat.script, value);
                                    break;
                                 case "attackSuccessProp":
                                    ret.putMobSkillStats(MobSkillStat.attackSuccessProp, value);
                                    break;
                                 case "bossHeal":
                                    ret.putMobSkillStats(MobSkillStat.bossHeal, value);
                                    break;
                                 case "face":
                                    ret.putMobSkillStats(MobSkillStat.face, value);
                                    break;
                                 case "callSkill":
                                    ret.putMobSkillStats(MobSkillStat.callSkill, value);
                                    break;
                                 case "level":
                                    ret.putMobSkillStats(MobSkillStat.level, value);
                                    break;
                                 case "linkHP":
                                    ret.putMobSkillStats(MobSkillStat.linkHP, value);
                                    break;
                                 case "timeLimitedExchange":
                                    ret.putMobSkillStats(MobSkillStat.timeLimitedExchange, value);
                                    break;
                                 case "summonDir":
                                    ret.putMobSkillStats(MobSkillStat.summonDir, value);
                                    break;
                                 case "summonTerm":
                                    ret.putMobSkillStats(MobSkillStat.summonTerm, value);
                                    break;
                                 case "castingTime":
                                    ret.putMobSkillStats(MobSkillStat.castingTime, value);
                                    break;
                                 case "subTime":
                                    ret.putMobSkillStats(MobSkillStat.subTime, value);
                                    break;
                                 case "reduceCasting":
                                    ret.putMobSkillStats(MobSkillStat.reduceCasting, value);
                                    break;
                                 case "additionalTime":
                                    ret.putMobSkillStats(MobSkillStat.additionalTime, value);
                                    break;
                                 case "force":
                                    ret.putMobSkillStats(MobSkillStat.force, value);
                                    break;
                                 case "targetType":
                                    ret.putMobSkillStats(MobSkillStat.targetType, value);
                                    break;
                                 case "forcex":
                                    ret.putMobSkillStats(MobSkillStat.forcex, value);
                                    break;
                                 case "sideAttack":
                                    ret.putMobSkillStats(MobSkillStat.sideAttack, value);
                                    break;
                                 case "afterEffect":
                                 case "rangeGap":
                                    ret.putMobSkillStats(MobSkillStat.rangeGap, value);
                                    break;
                                 case "noGravity":
                                    ret.putMobSkillStats(MobSkillStat.noGravity, value);
                                    break;
                                 case "notDestroyByCollide":
                                    ret.putMobSkillStats(MobSkillStat.notDestroyByCollide, value);
                                    break;
                                 case "areaSequenceDelay":
                                    ret.putMobSkillStats(MobSkillStat.areaSequenceDelay, value);
                                    break;
                                 case "cancleDamage":
                                    ret.putMobSkillStats(MobSkillStat.cancleDamage, value);
                                    break;
                                 case "cancleDamageMultiplier":
                                    ret.putMobSkillStats(MobSkillStat.cancleDamageMultiplier, value);
                                    break;
                                 case "consumeItemCoolTime":
                                    ret.putMobSkillStats(MobSkillStat.consumeItemCoolTime, value);
                                    break;
                                 case "rewardMob":
                                    ret.putMobSkillStats(MobSkillStat.rewardMob, value);
                                    break;
                                 case "userTimeMax":
                                    ret.putMobSkillStats(MobSkillStat.userTimeMax, value);
                                    break;
                                 case "userTimeMin":
                                    ret.putMobSkillStats(MobSkillStat.userTimeMin, value);
                                    break;
                                 case "fixDamR":
                                    ret.putMobSkillStats(MobSkillStat.fixDamR, value);
                                    break;
                                 case "summonOnce":
                                    ret.putMobSkillStats(MobSkillStat.summonOnce, value);
                                    break;
                                 case "lua":
                                    ret.putMobSkillStats(MobSkillStat.lua, value);
                                    break;
                                 case "targetMobType":
                                    ret.putMobSkillStats(MobSkillStat.targetMobType, value);
                                    break;
                                 case "footholdRect":
                                    ret.putMobSkillStats(MobSkillStat.footholdRect, value);
                                 case "screen":
                                 case "effect":
                                 case "mob":
                                 case "mob0":
                                 case "hit":
                                 case "affected":
                                 case "affectedOtherSkill":
                                 case "crash":
                                 case "effectToUser":
                                 case "affected_after":
                                 case "limitMoveSkill":
                                 case "tile":
                                 case "areaWarning":
                                 case "arType":
                                 case "tremble":
                                 case "otherSkill":
                                 case "etcEffect":
                                 case "etcEffect1":
                                 case "etcEffect2":
                                 case "etcEffect3":
                                 case "bombInfo":
                                 case "affected_pre":
                                 case "fixDamR_BT":
                                 case "affectedPhase":
                                 case "notMissAttack":
                                 case "ignoreEvasion":
                                 case "fadeinfo":
                                 case "randomTarget":
                                 case "option_linkedMob":
                                 case "affected0":
                                 case "head":
                                 case "mobGroup":
                                 case "exceptRange":
                                 case "exchangeAttack":
                                 case "range":
                                 case "addDam":
                                 case "special":
                                 case "target":
                                 case "fixedPos":
                                 case "fixedDir":
                                 case "i52":
                                 case "start":
                                 case "cancleType":
                                 case "succeed":
                                 case "failed":
                                 case "during":
                                 case "castingBarHide":
                                 case "skillCancelAlways":
                                 case "bounceBall":
                                 case "info2":
                                 case "regen":
                                 case "kockBackD":
                                 case "areaSequenceRandomSplit":
                                 case "accelerationEffect":
                                 case "repeatEffect":
                                 case "brightness":
                                 case "brightnessDuration":
                                 case "success":
                                 case "fail":
                                 case "affected_S":
                                 case "appear":
                                 case "affected_XS":
                                 case "disappear":
                                 case "damIncPos":
                                 case "option_poison":
                                 case "phaseUserCount":
                              }
                           }
                        }
                     }

                     int i = 0;

                     while (true) {
                        int id = MapleDataTool.getInt(String.valueOf(i++), level, 0);
                        if (id == 0) {
                           MapleData fixedPos = level.getChildByPath("fixedPos");
                           if (fixedPos != null) {
                              i = 0;

                              while (true) {
                                 Point pos = MapleDataTool.getPoint(String.valueOf(i++), fixedPos, new Point(0, 0));
                                 if (pos.x == 0 && pos.y == 0) {
                                    break;
                                 }

                                 ret.addFixedPos(pos);
                              }
                           }

                           MapleData fixedDir = level.getChildByPath("fixedDir");
                           if (fixedDir != null) {
                              i = 0;

                              while (true) {
                                 int dir = MapleDataTool.getInt(String.valueOf(i++), fixedDir, -1);
                                 if (dir == -1) {
                                    break;
                                 }

                                 ret.addFixedDir(dir);
                              }
                           }

                           MapleData area = level.getChildByPath("multiScaleInstallArea");
                           if (area != null) {
                              JinHillahPoisonMist mist = new JinHillahPoisonMist();
                              MapleData areaInfo = area.getChildByPath("areaInfo");
                              if (areaInfo != null) {
                                 for (MapleData areaData : areaInfo) {
                                    MapleData info = areaData.getChildByPath("info");
                                    mist.setRadius(MapleDataTool.getInt("radius", info, 0));
                                    mist.setScale(MapleDataTool.getInt("scale", info, 0));

                                    for (MapleData sInfo : areaData.getChildByPath("scaleInfo")) {
                                       int phase = Integer.parseInt(sInfo.getName());

                                       for (MapleData ssInfo : sInfo) {
                                          int scale_ = MapleDataTool.getInt("scale", ssInfo, 0);
                                          int time = MapleDataTool.getInt("time", ssInfo, 0);
                                          String tile = MapleDataTool.getString("tile", ssInfo, "");
                                          mist.addAreaInfo(phase, scale_, time, tile);
                                       }
                                    }

                                    ret.setJMist(mist);
                                 }
                              }

                              MapleData info = area.getChildByPath("info");
                              if (info != null) {
                                 mist.setLtRb(MapleDataTool.getPoint("lt", info, new Point(0, 0)), MapleDataTool.getPoint("rb", info, new Point(0, 0)));
                                 mist.setTotalArea(MapleDataTool.getInt("totalAreaMin", info, 0), MapleDataTool.getInt("totalAreaMax", info, 0));
                              }
                           }

                           ret.setCoolTime(MapleDataTool.getInt("interval", level, 0) * 1000);
                           if (ret.getCoolTime() == 0L) {
                              ret.setCoolTime(MapleDataTool.getInt("inteval", level, 0) * 1000);
                           }

                           ret.setDuration(MapleDataTool.getInt("time", level, 0) * 1000);
                           ret.setHp(MapleDataTool.getInt("hp", level, 0));
                           ret.setMpCon(MapleDataTool.getInt("mpcon", level, 0));
                           ret.setSpawnEffect(MapleDataTool.getInt("spawneffect", level, 0));
                           ret.setSummonEffect(MapleDataTool.getInt("summonEffect", level, 0));
                           ret.setTargetType(MapleDataTool.getInt("targetType", level, 0));
                           ret.setX(MapleDataTool.getInt("x", level, 0));
                           ret.setY(MapleDataTool.getInt("y", level, 0));
                           ret.setProp(MapleDataTool.getInt("prop", level, 0) / 100.0F);
                           ret.setLimit((short)MapleDataTool.getInt("limit", level, 0));
                           ret.setOnce(MapleDataTool.getInt("once", level, 0) > 0);
                           lt = MapleDataTool.getPoint("lt", level, new Point(0, 0));
                           rb = MapleDataTool.getPoint("rb", level, new Point(0, 0));
                           ret.setLtRb(lt, rb);
                           ret.setForce(MapleDataTool.getInt("force", level, 0));
                           ret.setForceX(MapleDataTool.getInt("forcex", level, 0));
                           lt2 = MapleDataTool.getPoint("lt2", level, new Point(0, 0));
                           rb2 = MapleDataTool.getPoint("rb2", level, new Point(0, 0));
                           ret.setLtRb2(lt2, rb2);
                           this.mobSkillCache.put(new Pair<>(skillID, lv), ret);
                           break;
                        }

                        ret.addSummonID(id);
                     }
                  }
               }
            }
         }
      }
   }
}
