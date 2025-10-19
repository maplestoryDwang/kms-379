package network.game.processors;

import constants.GameConstants;
import database.DBConfig;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.effect.child.HPHeal;
import objects.fields.Field;
import objects.fields.FieldType;
import objects.fields.MapleNodes;
import objects.fields.SmartMobMsgType;
import objects.fields.SmartMobNoticeType;
import objects.fields.child.blackmage.Field_BlackMage;
import objects.fields.child.hillah.Field_Hillah;
import objects.fields.child.jinhillah.Field_JinHillah;
import objects.fields.child.karing.Field_BossKaring;
import objects.fields.child.karing.Field_BossKaringMatch;
import objects.fields.child.karrotte.Field_BossKalos;
import objects.fields.child.lucid.Field_LucidBattlePhase2;
import objects.fields.child.moonbridge.Field_FerociousBattlefield;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.child.rimen.Field_RimenNearTheEnd;
import objects.fields.child.slime.Field_GuardianAngelSlime;
import objects.fields.child.slime.GuardianAngelSlime;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.child.zakum.Field_Zakum;
import objects.fields.fieldset.instance.SpiritSavior;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.CallSkillInfo;
import objects.fields.gameobject.lifes.CallSkillInfoWithData;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobAttackInfo;
import objects.fields.gameobject.lifes.MobMoveAction;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.WeatherMsg;
import objects.fields.gameobject.lifes.mobskills.BounceAttackInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkillCommand;
import objects.fields.gameobject.lifes.mobskills.MobSkillContext;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.fields.gameobject.lifes.mobskills.MobTeleportType;
import objects.fields.gameobject.lifes.mobskills.SmartMobNoticeData;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomCreatorOption;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.movepath.LifeMovementFragment;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleDiseaseValueHolder;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;

public class MobHandler {
   public static final void MoveMonster(PacketDecoder slea, final MapleClient c, final MapleCharacter chr) {
      int oid = slea.readInt();
      if (chr != null) {
         final MapleMonster monster = chr.getMap().getMonsterByOid(oid);
         if (monster != null && monster.isAlive()) {
            if (monster.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) == null) {
               short ctrlSN = slea.readShort();
               byte unk = slea.readByte();
               boolean mobCtrlState = (unk & 240) != 0;
               boolean nextAttackPossible = (unk & 15) != 0;
               int action = slea.readByte();
               final boolean isLeft = (action & 1) == 1;
               monster.setFaceLeft(isLeft);
               MobMoveAction moveAction = MobMoveAction.Null;
               if ((action & 128) == 0) {
                  moveAction = MobMoveAction.getAction(action >> 1);
               }

               short skillID = slea.readShort();
               short skillLevel = slea.readShort();
               if (moveAction.getType() != -1
                  && skillID != 0 & skillLevel != 0
                  && !DBConfig.isHosting
                  && (c.getPlayer().isGM() || c.getPlayer().getClient().isGm())) {
                  c.getPlayer()
                     .dropMessage(
                        6,
                        "mobID : "
                           + monster.getId()
                           + ", mobMoveAction("
                           + moveAction.name()
                           + "/"
                           + moveAction.getType()
                           + "), skillID : "
                           + skillID
                           + ", skillLevel2 : "
                           + skillLevel
                     );
               }

               int skillDelay = slea.readInt();
               slea.readByte();
               slea.readByte();
               int count = slea.readByte();
               List<Pair<Short, Short>> multiTargetForBall = new ArrayList<>();

               for (int i = 0; i < count; i++) {
                  multiTargetForBall.add(new Pair<>(slea.readShort(), slea.readShort()));
               }

               int var31 = slea.readByte();
               List<Short> randTimeForAreaAttack = new ArrayList<>();

               for (int i = 0; i < var31; i++) {
                  randTimeForAreaAttack.add(slea.readShort());
               }

               int v329 = slea.readInt();
               List<Integer> v329List = new ArrayList<>();
               if (v329 > 0) {
                  for (int i = 0; i < 11; i++) {
                     v329List.add(slea.readInt());
                  }
               }

               slea.readByte();
               slea.readInt();
               slea.readInt();
               slea.readInt();
               slea.readInt();
               slea.readInt();
               slea.readByte();
               int tEncodedGatherDuration = slea.readInt();
               final Point startPos = new Point(slea.readShort(), slea.readShort());
               Point velPos = new Point(slea.readShort(), slea.readShort());
               List<LifeMovementFragment> res = MovementParse.parseMovement(slea);
               monster.setNextAttackPossible(nextAttackPossible);
               if (moveAction.getType() >= MobMoveAction.Skill1.getType() && moveAction.getType() <= MobMoveAction.SkillF.getType()) {
                  MobSkill mobSkill = null;
                  List<MobSkill> mobSkills = monster.getSkills();
                  if (mobSkills != null && mobSkills.size() > 0) {
                     for (MobSkill ms : mobSkills) {
                        if (ms.getMobSkillID() == skillID && ms.getLevel() == skillLevel) {
                           mobSkill = ms;
                           break;
                        }
                     }

                     if (mobSkill != null) {
                        if (monster.getDamageShareUserCount() > 0) {
                           monster.clearDamageShareUsers();
                        }

                        MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillID, skillLevel);
                        mobSkillInfo.applyEffect(c.getPlayer(), monster, mobSkill, true, isLeft, startPos);
                        if (mobSkill.getSkillForbid() > 0) {
                           monster.setSkillForbid(System.currentTimeMillis() + mobSkill.getSkillForbid());
                        }

                        if (mobSkill.getWeatherMsg() != null) {
                           WeatherMsg weatherMsg = mobSkill.getWeatherMsg();
                           monster.getMap()
                              .broadcastMessage(CField.sendWeatherEffectNotice(weatherMsg.getType(), weatherMsg.getTime() * 1000, false, weatherMsg.getMsg()));
                        }

                        if (mobSkillInfo.getOtherSkillID() > 0) {
                           monster.setCallOtherSkillID(mobSkillInfo.getOtherSkillID());
                           monster.setCallOtherSkillLevel(mobSkillInfo.getOtherSkillLev());
                        } else if (mobSkillInfo.getSkillId() == 186 && mobSkillInfo.getSkillLevel() == 11) {
                           monster.setCallOtherSkillID(213);
                           monster.setCallOtherSkillLevel(10);
                        }

                        monster.setLastUseSkillTime(System.currentTimeMillis());
                        if (SmartMobNoticeData.skills.containsKey(monster.getId())) {
                           for (Pair<Integer, String> pair : SmartMobNoticeData.skills.get(monster.getId())) {
                              if (pair.left == mobSkill.getIndex()) {
                                 monster.getMap().sendSmartMobNotice(SmartMobNoticeType.Normal, monster.getId(), SmartMobMsgType.Field, skillLevel, pair.right);
                              }
                           }
                        }

                        if (mobSkill.getAfterAttack() > 0 && !monster.containsAttackBlocked(mobSkill.getAfterAttack())) {
                           monster.getMap()
                              .broadcastMessage(
                                 MobPacket.mobSetAfterAttack(
                                    monster.getObjectId(), mobSkill.getAfterAttack(), mobSkill.getAfterAttackCount(), moveAction.getType(), isLeft
                                 )
                              );
                        }
                     }

                     monster.getMap().onMobSkill(monster, skillID, skillLevel);
                     if (mobSkill.isAfterDead()) {
                        monster.getMap().removeMonster(monster, 0);
                     }
                  }
               } else if (moveAction.getType() >= MobMoveAction.Attack1.getType() && moveAction.getType() <= MobMoveAction.AttackF.getType()) {
                  int attackIdx = moveAction.getType() - MobMoveAction.Attack1.getType();
                  MobAttackInfo info = monster.getStats().getMobAttack(attackIdx);
                  if (info != null) {
                     if ((monster.getId() == 8645066 || monster.getId() == 8645009) && attackIdx == 0 && monster.getMap() instanceof Field_RimenNearTheEnd) {
                        Field_RimenNearTheEnd f = (Field_RimenNearTheEnd)monster.getMap();
                        f.spawnEliteBoss();
                     }

                     if (info.weatherMsg != null) {
                        WeatherMsg weatherMsg = info.weatherMsg;
                        monster.getMap()
                           .broadcastMessage(CField.sendWeatherEffectNotice(weatherMsg.getType(), weatherMsg.getTime() * 1000, false, weatherMsg.getMsg()));
                     }

                     if (info.afterDead) {
                        Timer.MapTimer.getInstance().schedule(new Runnable() {
                           @Override
                           public void run() {
                              monster.getMap().killMonster(monster);
                           }
                        }, 4000L);
                     }

                     if (SmartMobNoticeData.attacks.containsKey(monster.getId())) {
                        for (Pair<Integer, String> pairx : SmartMobNoticeData.attacks.get(monster.getId())) {
                           if (pairx.left == attackIdx) {
                              monster.getMap().sendSmartMobNotice(SmartMobNoticeType.Normal, monster.getId(), SmartMobMsgType.Field, skillLevel, pairx.right);
                           }
                        }
                     }

                     doAdditionalAttackAction(monster, moveAction);
                     if (info.afterAttack > 0 && !monster.containsAttackBlocked(info.afterAttack)) {
                        monster.getMap()
                           .broadcastMessage(
                              MobPacket.mobSetAfterAttack(monster.getObjectId(), info.afterAttack, info.afterAttackCount, moveAction.getType(), isLeft)
                           );
                     }

                     if (info.callSkillInfo != null && !info.callSkillInfo.isEmpty()) {
                        for (final CallSkillInfo csi : info.callSkillInfo) {
                           final MobSkillInfo mobSkillInfox = MobSkillFactory.getMobSkill(csi.skill, csi.level);
                           if (!mobSkillInfox.checkCurrentBuff(chr, monster)) {
                              long now = System.currentTimeMillis();
                              monster.setLastSkillUsed(mobSkillInfox, now, mobSkillInfox.getCoolTime());
                              monster.setLastUseSkillTime(System.currentTimeMillis());
                              Timer.ShowTimer.getInstance().schedule(new Runnable() {
                                 @Override
                                 public void run() {
                                    if (monster != null && monster.getMap().getId() == chr.getMapId()) {
                                       MobSkill mobSkill = MapleLifeFactory.getRealMobSkill(csi.skill, csi.level);
                                       mobSkillInfox.applyEffect(c.getPlayer(), monster, mobSkill, true, isLeft, startPos);
                                       if (mobSkill != null && mobSkill.getSkillForbid() > 0) {
                                          monster.setSkillForbid(System.currentTimeMillis() + mobSkill.getSkillForbid());
                                       }
                                    }
                                 }
                              }, csi.delay);
                           }
                        }
                     }

                     if (info.callSkillWithData != null) {
                        final CallSkillInfoWithData csix = info.callSkillWithData;
                        MobSkillCommand command = new MobSkillCommand(0, 0, 0);
                        command.setSkillCommand(csix.skill);
                        command.setSkillCommandLevel(csix.level);
                        monster.setCommand(command);
                        final MobSkillInfo mobSkillInfox = MobSkillFactory.getMobSkill(csix.skill, csix.level);
                        if (!mobSkillInfox.checkCurrentBuff(chr, monster)) {
                           long now = System.currentTimeMillis();
                           monster.setLastSkillUsed(mobSkillInfox, now, mobSkillInfox.getCoolTime());
                           monster.setLastUseSkillTime(System.currentTimeMillis());
                           Timer.ShowTimer.getInstance().schedule(new Runnable() {
                              @Override
                              public void run() {
                                 if (monster != null && monster.getMap().getId() == chr.getMapId()) {
                                    MobSkill mobSkill = MapleLifeFactory.getRealMobSkill(csix.skill, csix.level);
                                    mobSkillInfox.applyEffect(c.getPlayer(), monster, mobSkill, true, isLeft, startPos);
                                    if (mobSkill.getSkillForbid() > 0) {
                                       monster.setSkillForbid(System.currentTimeMillis() + mobSkill.getSkillForbid());
                                    }
                                 }
                              }
                           }, csix.delay);
                        }
                     }
                  }
               }

               prepareNextSkill(c.getPlayer(), monster);
               prepareNextForcedAttack(monster);
               if (monster.getController() != null && monster.getController().getId() != c.getPlayer().getId()) {
                  if (!mobCtrlState) {
                     c.getSession().writeAndFlush(MobPacket.stopControllingMonster(oid));
                     return;
                  }

                  monster.switchController(chr, true);
               }

               if (monster.getCommand().getSkillCommand() > 0) {
                  monster.getCommand().setAttackCommand(0);
               }

               c.getSession().writeAndFlush(MobPacket.mobCtrlAck(oid, ctrlSN, monster.getMp(), nextAttackPossible, monster.getCommand()));
               monster.setCommand(new MobSkillCommand(0, 0, 0));
               slea.skip(28);
               boolean cannotUseSkill = slea.readByte() == 1;
               if (res != null) {
                  Field map = c.getPlayer().getMap();
                  MovementParse.updatePosition(res, monster, -1);
                  if (monster.getFrozenLinkSerialNumber() == 0L) {
                     map.moveMonster(monster, monster.getPosition());
                     map.broadcastMessage(
                        chr,
                        MobPacket.moveMonster(
                           unk,
                           action,
                           skillID,
                           skillLevel,
                           skillDelay,
                           oid,
                           tEncodedGatherDuration,
                           startPos,
                           velPos,
                           res,
                           multiTargetForBall,
                           randTimeForAreaAttack,
                           v329,
                           v329List,
                           cannotUseSkill
                        ),
                        false
                     );
                  }
               }
            }
         }
      }
   }

   private static void doAdditionalAttackAction(MapleMonster mob, MobMoveAction action) {
      if (mob.getId() == 8930000 && action == MobMoveAction.Attack9) {
         mob.getMap().broadcastMessage(CWvsContext.getScriptProgressMessage("벨룸이 깊은 숨을 들이쉽니다."));
      }

      if (mob.getId() == 8870100 && action == MobMoveAction.Attack6) {
         mob.getMap().broadcastMessage(MobPacket.talkMonster(mob.getObjectId(), 1));
      }

      if (mob.getId() == 8910000 && action == MobMoveAction.Attack4) {
         Rect mbr = mob.getMap().calculateMBR();

         for (int i = 22; i <= 27; i++) {
            mob.getMap()
               .registerObstacleAtom(
                  ObstacleAtomCreateType.NORMAL,
                  i,
                  0,
                  4,
                  60000,
                  5000,
                  false,
                  ObstacleAtomCreatorOption.SetCreateDelay(300, 900),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(100, 200),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mbr.getLeft(), mbr.getRight(), -800, 455, mob.getMap())
               );
         }
      }
   }

   public static void prepareNextForcedAttack(MapleMonster mob) {
      if (mob.getId() != 8800102 && mob.getId() != 8800002 && mob.getId() != 8800022) {
         if (mob.getId() == 8880153 && mob.getMap() instanceof Field_LucidBattlePhase2) {
            Field_LucidBattlePhase2 f = (Field_LucidBattlePhase2)mob.getMap();
            long nextBoardAttack = f.getNextBoardAttack();
            if (nextBoardAttack <= System.currentTimeMillis()) {
               f.setNextBoardAttack(System.currentTimeMillis() + Randomizer.rand(3, 10) * 1000);
               int attackCommand = 1;
               MobSkillCommand command = new MobSkillCommand(0, 0, 0);
               command.setAttackCommand(attackCommand);
               mob.setCommand(command);
               return;
            }
         }

         int attackCommand = mob.removeAndGetOneTimeForcedAttack();
         if (attackCommand != 0) {
            MobSkillCommand command = new MobSkillCommand(0, 0, 0);
            command.setAttackCommand(attackCommand);
            mob.setCommand(command);
         }
      } else {
         if (mob.getMap() instanceof Field_Zakum) {
            Field_Zakum f = (Field_Zakum)mob.getMap();
            if (f.getNextPhase3AttackTime() != 0L && f.getNextPhase3AttackTime() <= System.currentTimeMillis()) {
               f.setNextPhase3AttackTime(System.currentTimeMillis() + 30000L);
               int attackCommand = 4;
               MobSkillCommand command = new MobSkillCommand(0, 0, 0);
               command.setAttackCommand(attackCommand);
               mob.setCommand(command);
               return;
            }
         }
      }
   }

   public static void prepareNextSkill(MapleCharacter player, MapleMonster mob) {
      MobSkillCommand command = new MobSkillCommand(0, 0, 0);
      if (mob.getMap() instanceof Field_WillBattle) {
         Field_WillBattle f = (Field_WillBattle)mob.getMap();
         if (f.getNext3rdAttackTime() <= System.currentTimeMillis() && f.getNext3rdAttackTime() > 0L) {
            command.setSkillCommand(242);
            command.setSkillCommandLevel(f.getNext3rdAttackType() + 10);
            mob.setCommand(command);
            return;
         }
      }

      if (mob.getMap() instanceof Field_JinHillah) {
         Field_JinHillah f = (Field_JinHillah)mob.getMap();
         if ((mob.getId() == 8880410 || mob.getId() == 8880405) && f.isSetFullMapAttackTime()) {
            command.setSkillCommand(247);
            command.setSkillCommandLevel(1);
            mob.setCommand(command);
            f.setSetFullMapAttackTime(false);
            f.setDisabledRedThread(true);
            f.setEnableRedThreadTime(System.currentTimeMillis() + 2000L);
            return;
         }
      }

      if (mob.getMap() instanceof Field_BlackMage) {
         Field_BlackMage f = (Field_BlackMage)mob.getMap();
         if ((mob.getId() == 8880500 || mob.getId() == 8880501) && mob.isSetShriekingWallPattern()) {
            f.setSetShriekingWallPattern(true);
            command.setSkillCommand(170);
            command.setSkillCommandLevel(mob.getId() - 8880435);
            mob.setCommand(command);
            mob.setSetShriekingWallPattern(false);
            return;
         }
      }

      if (mob.getMap() instanceof Field_FerociousBattlefield) {
         Field_FerociousBattlefield f = (Field_FerociousBattlefield)mob.getMap();
         if ((mob.getId() == 8644655 || mob.getId() == 8644650) && f.isSetLaserAttack()) {
            command.setSkillCommand(186);
            command.setSkillCommandLevel(11);
            mob.setCommand(command);
            f.setSetLaserAttack(false);
         }
      }

      if (mob.getMap() instanceof Field_Papulatus) {
         Field_Papulatus f = (Field_Papulatus)mob.getMap();
         if ((mob.getId() == 8500001 || mob.getId() == 8500011 || mob.getId() == 8500021) && f.isStartPapulatusCrack()) {
            command.setSkillCommand(241);
            command.setSkillCommandLevel(7);
            mob.setCommand(command);
            f.setStartPapulatusCrack(false);
         }
      }

      if (mob.getCastingSkillID() > 0 && mob.getCastingEnd() <= System.currentTimeMillis()) {
         mob.stopCastingSkill(true);
      } else if (mob.getBuff(MobTemporaryStatFlag.SEAL) == null
         && !(mob.getBuff(MobTemporaryStatFlag.SEAL_SKILL) != null | mob.getBuff(MobTemporaryStatFlag.MAGIC_CRASH) != null)) {
         if (mob.getCommand().getSkillCommand() <= 0) {
            if (mob.isNextAttackPossible()) {
               if (System.currentTimeMillis() - mob.getLastUseSkillTime() >= 3000L) {
                  if (mob.getSkillForbid() <= System.currentTimeMillis()) {
                     if (mob.getCallOtherSkillID() > 0) {
                        command.setSkillCommand(mob.getCallOtherSkillID());
                        command.setSkillCommandLevel(mob.getCallOtherSkillLevel());
                        mob.setCommand(command);
                        mob.setCallOtherSkillID(0);
                        mob.setCallOtherSkillLevel(0);
                     } else {
                        if (mob.getMap() instanceof Field_LucidBattlePhase2) {
                           Field_LucidBattlePhase2 f = (Field_LucidBattlePhase2)mob.getMap();
                           if (f.maxButterfly) {
                              command.setSkillCommand(238);
                              command.setSkillCommandLevel(9);
                              f.maxButterfly = false;
                              mob.setCommand(command);
                              return;
                           }
                        }

                        List<MobSkill> skills = new ArrayList<>(mob.getSkills());
                        MobSkill s = mob.getSkills().stream().min((a, b) -> b.getPriority() - a.getPriority()).orElse(null);
                        if (s != null) {
                           int highestPriority = s.getPriority();
                           if (highestPriority <= 0) {
                              Collections.shuffle(skills);
                           } else {
                              List<MobSkill> list = new ArrayList<>();
                              List<MobSkill> temp = new ArrayList<>();

                              for (int i = highestPriority; i >= 0; i--) {
                                 int idx = i;
                                 skills.stream().filter(m -> m.getPriority() == idx).forEach(temp::add);
                                 Collections.shuffle(temp);
                                 list.addAll(temp);
                              }

                              skills.clear();
                              skills = list;
                           }

                           skills = skills.stream()
                              .filter(m -> !mob.containsSkillFilter(m.getIndex()))
                              .filter(m -> !m.isOnlyOtherSkill())
                              .filter(m -> !m.isOnlyFsm() || mob.containsAllowedFsmSkill(m.getIndex()) || mob.containsOnetimeFsmSkill(m.getIndex()))
                              .collect(Collectors.toList());
                           int i = 0;

                           MobSkillInfo mobSkillInfo;
                           long now;
                           int skillID;
                           int skillLv;
                           long cooltime;
                           while (true) {
                              if (i >= skills.size()) {
                                 return;
                              }

                              s = skills.get(i++);
                              MobSkillContext msc = mob.getMsc().get(s.getIndex());
                              skillID = msc.getSkillID();
                              skillLv = msc.getSkillLevel();
                              mobSkillInfo = MobSkillFactory.getMobSkill(skillID, skillLv);
                              if (!mobSkillInfo.checkCurrentBuff(player, mob)) {
                                 now = System.currentTimeMillis();
                                 long ls = mob.getLastSkillUsed(mobSkillInfo);
                                 if (skillID == 201 && skillLv >= 51 && skillLv <= 54) {
                                    ls = mob.getLastSkillUsed(MobSkillFactory.getMobSkill(201, 51));
                                 } else if (skillID == 201 && skillLv >= 55 && skillLv <= 58) {
                                    ls = mob.getLastSkillUsed(MobSkillFactory.getMobSkill(201, 55));
                                 } else if (skillID == 188 && skillLv == 1 && ls == 0L) {
                                    mob.setLastSkillUsed(mobSkillInfo, now, 5000L);
                                    ls = System.currentTimeMillis();
                                 }

                                 MobSkillID ms = MobSkillID.getMobSkillIDByValue(skillID);
                                 switch (ms) {
                                    case SUMMON2:
                                       if (!mob.getMap().isCanSummonSubMob(skillID, skillLv)) {
                                          continue;
                                       }
                                       break;
                                    case LUCID_SKILL:
                                       if (msc.getSkillLevel() != 9 || !(mob.getMap() instanceof Field_LucidBattlePhase2)) {
                                          break;
                                       }

                                       Field_LucidBattlePhase2 f = (Field_LucidBattlePhase2)mob.getMap();
                                       if (!f.maxButterfly) {
                                          continue;
                                       }
                                       break;
                                    case LASER_ATTACK:
                                       if (mob.getBuff(MobTemporaryStatFlag.LASER) != null) {
                                          continue;
                                       }
                                 }

                                 cooltime = mobSkillInfo.getCoolTime();
                                 if (cooltime == 0L) {
                                    cooltime = 10000L;
                                 }

                                 if (ls == 0L || now - ls > 0L || mob.checkIgnoreIntervalSkill(s.getIndex())) {
                                    int reqHp = (int)((float)mob.getHp() / (float)mob.getMobMaxHp() * 100.0F);
                                    if (mobSkillInfo.getHP() == 0 || reqHp <= mobSkillInfo.getHP()) {
                                       int prop = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.prop);
                                       if (prop <= 0 || Randomizer.isSuccess(prop)) {
                                          int preSkillCount = s.getPreSkillCount();
                                          if (preSkillCount <= 0 || mob.getSkillIndexUsedCount(s.getPreSkillIndex()) >= preSkillCount) {
                                             int limit = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.limit);
                                             if ((limit <= 0 || mob.getSkillIndexUsedCount(s.getIndex()) < limit)
                                                && (
                                                   mobSkillInfo.getMobSkillStatsInt(MobSkillStat.summonOnce) != 1
                                                      || mob.getSkillIndexUsedCount(s.getIndex()) <= 0
                                                )) {
                                                if (msc.getSkillID() != MobSkillID.SUMMON.getVal()) {
                                                   break;
                                                }

                                                List<Integer> mobGroup = mobSkillInfo.getMobGroup(0);
                                                if (mobGroup == null || mobGroup.size() > mob.getSkillIndexUsedCount(s.getIndex())) {
                                                   break;
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }

                           if (mob.containsOnetimeFsmSkill(s.getIndex())) {
                              mob.removeOnetimeFsmSkill(s.getIndex());
                           }

                           if (mob.checkIgnoreIntervalSkill(s.getIndex())) {
                              mob.removeIgnoreIntervalSkill(s.getIndex());
                           }

                           if (skillID == 201 && skillLv >= 51 && skillLv <= 54) {
                              MobSkillInfo msi = MobSkillFactory.getMobSkill(201, 51);
                              mob.setLastSkillUsed(msi, now, 30000L);
                           } else if (skillID == 201 && skillLv >= 55 && skillLv <= 58) {
                              MobSkillInfo msi = MobSkillFactory.getMobSkill(201, 55);
                              mob.setLastSkillUsed(msi, now, 30000L);
                           } else {
                              mob.setLastSkillUsed(mobSkillInfo, now, cooltime);
                              if (s.getCooltimeOnSkill() != null && !s.getCooltimeOnSkill().isEmpty()) {
                                 for (int skillIndex : s.getCooltimeOnSkill()) {
                                    MobSkill ms_ = null;

                                    for (MobSkill e : skills) {
                                       if (e.getMobSkillSN() == skillIndex) {
                                          ms_ = e;
                                          break;
                                       }
                                    }

                                    if (ms_ != null) {
                                       MobSkillInfo msi = MobSkillFactory.getMobSkill(ms_.getMobSkillID(), ms_.getLevel());
                                       if (msi != null) {
                                          mob.setLastSkillUsed(msi, now, cooltime);
                                       }
                                    }
                                 }
                              }
                           }

                           command.setSkillCommand(skillID);
                           command.setSkillCommandLevel(skillLv);
                           mob.incrementSkillIndexUsed(s.getIndex());
                           mob.setCommand(command);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static final void FriendlyDamage(PacketDecoder slea, MapleCharacter chr) {
      Field map = chr.getMap();
      if (map != null) {
         MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
         slea.skip(4);
         MapleMonster mobto = map.getMonsterByOid(slea.readInt());
         if (mobfrom != null && mobto != null && mobto.getStats().isFriendly()) {
            int damage = mobto.getStats().getLevel() * Randomizer.nextInt(mobto.getStats().getLevel()) / 2;
            mobto.damage(chr, damage, true);
            checkShammos(chr, mobto, map);
         }
      }
   }

   public static final void BindMonster(PacketDecoder slea, MapleClient c) {
      Field map = c.getPlayer().getMap();
      if (map != null) {
         MapleMonster mob = map.getMonsterByOid(slea.readInt());
      }
   }

   public static final void checkShammos(MapleCharacter chr, MapleMonster mobto, Field map) {
      if (!mobto.isAlive() && mobto.getStats().isEscort()) {
         for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
            if (chrz.getParty() != null && chrz.getParty().getLeader().getId() == chrz.getId()) {
               if (chrz.haveItem(2022698)) {
                  MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                  mobto.heal((int)mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                  return;
               }
               break;
            }
         }

         map.broadcastMessage(CWvsContext.serverNotice(6, "Your party has failed to protect the monster."));
         Field mapp = chr.getMap().getForcedReturnMap();

         for (MapleCharacter chrzx : map.getCharactersThreadsafe()) {
            chrzx.changeMap(mapp, mapp.getPortal(0));
         }
      } else if (mobto.getStats().isEscort() && mobto.getEventInstance() != null) {
         mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
      }
   }

   public static final void monsterBomb(PacketDecoder slea, MapleCharacter chr) {
      MapleMonster monster = chr.getMap().getMonsterByOid(slea.readInt());
      if (monster != null && chr.isAlive() && !chr.isHidden() && monster.getLinkCID() <= 0) {
         if (!monster.isSelfDestruct()) {
            byte selfd = monster.getStats().getSelfD();
            if (monster.getId() == 8920004 || monster.getId() == 8920104) {
               selfd = 2;
            }

            if (selfd != -1) {
               if (chr.getMap().getFieldSetInstance() != null && chr.getMap().getFieldSetInstance() instanceof SpiritSavior) {
                  SpiritSavior ss = (SpiritSavior)chr.getMap().getFieldSetInstance();
                  ss.bombSpirit(chr, monster);
               } else if (selfd == 6) {
                  int playerID = slea.readInt();
                  if (chr.getId() == playerID) {
                     chr.getMap().killMonster(monster, true);
                  }
               } else {
                  monster.setSelfDestruct(true);
                  chr.getMap().addNextRemoveMonster(monster.getObjectId(), System.currentTimeMillis() + 3000L);
                  chr.getMap().killMonster(monster, chr, false, false, selfd, true);
               }
            }
         }
      }
   }

   public static final void AutoAggro(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null && !chr.isHidden()) {
         MapleMonster monster = chr.getMap().getMonsterByOid(slea.readInt());
         int monsterId = slea.readInt();
         int distance = slea.readInt();
         int distanceReq = DBConfig.isGanglim ? 200000 : 100;
         if (monster != null) {
            if (monster.getController() != null) {
               if (chr.getMap().getCharacterById(monster.getController().getId()) == null) {
                  if (distance <= distanceReq) {
                     monster.switchController(chr, true);
                  }
               } else if (monster.getController().getTruePosition().distanceSq(monster.getTruePosition()) > 2455780.0) {
                  if (distance <= distanceReq || monster.getStats().isBoss()) {
                     monster.switchController(chr, true);
                  }
               } else if (!monster.isControllerHasAggro()) {
                  monster.setControllerHasAggro(true);
               }
            } else if (distance <= distanceReq || monster.getStats().isBoss()) {
               monster.switchController(chr, true);
            }

            if (monster.getStats().isBoss()) {
               monster.broadcastAttackBlocked();
            }
         }
      }
   }

   public static final void HypnotizeDmg(PacketDecoder slea, MapleCharacter chr) {
      MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
      slea.skip(4);
      int to = slea.readInt();
      slea.skip(1);
      int damage = slea.readInt();
      MapleMonster mob_to = chr.getMap().getMonsterByOid(to);
      if (mob_from != null && mob_to != null && mob_to.getStats().isFriendly()) {
         if (damage > 30000) {
            return;
         }

         mob_to.damage(chr, damage, true);
         checkShammos(chr, mob_to, chr.getMap());
      }
   }

   public static final void DisplayNode(PacketDecoder slea, MapleCharacter chr) {
      MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
      if (mob_from != null) {
         chr.getClient().getSession().writeAndFlush(MobPacket.getNodeProperties(mob_from, chr.getMap()));
      }
   }

   public static final void MobNode(PacketDecoder slea, MapleCharacter chr) {
      MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
      int newNode = slea.readInt();
      int nodeSize = chr.getMap().getNodes().size();
      if (mob_from != null && nodeSize > 0) {
         MapleNodes.MapleNodeInfo mni = chr.getMap().getNode(newNode);
         if (mni == null) {
            return;
         }

         if (mni.attr == 2) {
            switch (chr.getMapId() / 100) {
               case 9211200:
               case 9211201:
               case 9211202:
               case 9211203:
               case 9211204:
                  chr.getMap().talkMonster("Please escort me carefully.", 5120035, mob_from.getObjectId());
                  break;
               case 9320001:
               case 9320002:
               case 9320003:
                  chr.getMap().talkMonster("Please escort me carefully.", 5120051, mob_from.getObjectId());
            }
         }

         mob_from.setLastNode(newNode);
         if (chr.getMap().isLastNode(newNode)) {
            switch (chr.getMapId() / 100) {
               case 9211200:
               case 9211201:
               case 9211202:
               case 9211203:
               case 9211204:
               case 9320001:
               case 9320002:
               case 9320003:
                  chr.getMap().broadcastMessage(CWvsContext.serverNotice(5, "Proceed to the next stage."));
                  chr.getMap().removeMonster(mob_from);
            }
         }
      }
   }

   public static final void fallObstacleAtom(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      slea.skip(4);
      int SN = slea.readInt();
      int type = slea.readInt();
      int posX = slea.readInt();
      int posY = slea.readInt();
      if (chr != null) {
         ObstacleAtom atom = chr.getMap().getObstacleAtom(SN);
         if (atom != null) {
            if (atom.getAtomType() == 64) {
               if (type == 1) {
                  List<Point> posList = List.of(new Point(0, 158), new Point(0, -2020));
                  if (chr.getMap().getAffectedAreaByMobSkill(MobSkillFactory.getMobSkill(242, 4)) == null) {
                     for (Point pos : posList) {
                        Rect rect = new Rect(-362, pos.y - 362, 204, pos.y + 20);
                        AffectedArea area = new AffectedArea(
                           rect, chr.getId(), MobSkillFactory.getMobSkill(242, 4), pos, 0, System.currentTimeMillis() + 60000L
                        );
                        chr.getMap().spawnMist(area);
                     }
                  }
               }
            } else if (atom.getAtomType() == 75) {
               if (type == 1 && chr.getMap() instanceof Field_BlackMage) {
                  Field_BlackMage f = (Field_BlackMage)chr.getMap();
                  if (f != null) {
                     if (chr.hasDisease(SecondaryStatFlag.CurseOfDestruction)) {
                        MapleDiseaseValueHolder h = chr.getDiseases(SecondaryStatFlag.CurseOfDestruction);
                        if (h != null && h.level != 2) {
                           f.decrementDeathCount(chr, false);
                           int hp = (int)(chr.getStat().getCurrentMaxHp(chr) * 0.01 * 60.0);
                           chr.addHP(-hp, false);
                           chr.giveDebuff(SecondaryStatFlag.Seal, 1, 0, 5000L, 120, 39);
                        }
                     }

                     chr.giveDebuff(SecondaryStatFlag.CurseOfDestruction, 4, 0, 3230L, 249, 1);
                  }
               }
            } else if (atom.getAtomType() >= 65 && atom.getAtomType() <= 67) {
               if (type == 1 && chr.getMap() instanceof Field_FerociousBattlefield) {
                  Field_FerociousBattlefield f = (Field_FerociousBattlefield)chr.getMap();
                  if (f != null
                     && chr.getBuffedValue(SecondaryStatFlag.StormGuard) == null
                     && chr.getBuffedValue(SecondaryStatFlag.BlessingArmor) == null
                     && chr.getBuffedValue(SecondaryStatFlag.HolyMagicShell) == null
                     && chr.getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
                     if (atom.getAtomType() == 65) {
                        chr.giveDebuff(SecondaryStatFlag.Undead, MobSkillFactory.getMobSkill(133, 29));
                     } else if (atom.getAtomType() == 66) {
                        chr.giveDebuff(SecondaryStatFlag.Stun, MobSkillFactory.getMobSkill(123, 66));
                     } else if (atom.getAtomType() == 67) {
                        chr.giveDebuff(SecondaryStatFlag.Darkness, MobSkillFactory.getMobSkill(121, 27));
                     }
                  }
               }
            } else if ((atom.getAtomType() == 60 || atom.getAtomType() == 61) && type == 1 && chr.getMap() instanceof Field_Papulatus) {
               Field_Papulatus f = (Field_Papulatus)chr.getMap();
               if (f != null
                  && chr.getBuffedValue(SecondaryStatFlag.StormGuard) == null
                  && chr.getBuffedValue(SecondaryStatFlag.BlessingArmor) == null
                  && chr.getBuffedValue(SecondaryStatFlag.HolyMagicShell) == null
                  && chr.getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
                  chr.giveDebuff(SecondaryStatFlag.Stun, MobSkillFactory.getMobSkill(123, 66));
               }
            }

            if (atom.getDiseaseSkillID() > 0
               && type == 1
               && chr.getBuffedValue(SecondaryStatFlag.StormGuard) == null
               && chr.getBuffedValue(SecondaryStatFlag.BlessingArmor) == null
               && chr.getBuffedValue(SecondaryStatFlag.HolyMagicShell) == null
               && chr.getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
               chr.giveDebuff(
                  SecondaryStatFlag.getBySkill(atom.getDiseaseSkillID()), MobSkillFactory.getMobSkill(atom.getDiseaseSkillID(), atom.getDiseaseSkillLevel())
               );
            }

            chr.getMap().removeObstacleAtom(atom);
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.OBSTACLE_ATOM_REMOVE.getValue());
         packet.writeInt(SN);
         chr.getMap().broadcastMessage(packet.getPacket());
      }
   }

   public static final void checkVellumStoneFall(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int type = slea.readInt();
         if (chr.getMap().getFieldType() == FieldType.Bellum.getType()) {
            if (type == 1) {
               boolean chaos = chr.getMap().getId() % 1000 / 100 == 8;
               chr.addHP(-1L);
               int duration = chaos ? 5000 : 1000;
               chr.giveDebuff(SecondaryStatFlag.Stun, 1, 0, duration, 123, 1, false);
            }
         } else if (!DBConfig.isHosting) {
            System.out.println("처리되지 않은 돌덩이 맵 발견 : " + chr.getMapId());
         }
      }
   }

   public static final void mobSkillDelayEnd(PacketDecoder slea, MapleClient c) {
      int mobObjectID = slea.readInt();
      int mobSkillID = slea.readInt();
      int mobSkillLevel = slea.readInt();
      byte fireAtRandomAttack = slea.readByte();
      MobSkillID msi = MobSkillID.getMobSkillIDByValue(mobSkillID);
      MobSkillInfo mobSkill = MobSkillFactory.getMobSkill(mobSkillID, mobSkillLevel);
      MapleMonster monster = c.getPlayer().getMap().getMonsterByOid(mobObjectID);
      if (monster != null) {
         if (monster.getController().getId() == c.getPlayer().getId() || msi == MobSkillID.FIRE_AT_RANDOM_ATTACK) {
            if (monster != null) {
               switch (msi) {
                  case SUMMON2:
                     if (mobSkillLevel == 47 || mobSkillLevel == 48 || mobSkillLevel == 59 || mobSkillLevel == 60) {
                        int createCount = 12;
                        int count = 0;
                        int limitCount = 0;
                        List<Point> posList = new ArrayList<>();
                        int xLeft = monster.getMap().getLeft();

                        while (limitCount++ < mobSkill.getSummonSize()) {
                           int randomX = Randomizer.rand(-790, 762);
                           if (count == 0) {
                              posList.add(new Point(randomX, 134));
                           } else {
                              boolean find = true;
                              if (!find) {
                                 continue;
                              }

                              posList.add(new Point(randomX, 134));
                           }

                           if (createCount <= ++count) {
                              break;
                           }
                        }

                        count = 0;

                        for (Point posx : posList) {
                           int mobTemplateID = mobSkill.getSummonID(count++);
                           MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
                           if (c.getPlayer().getMap().getMobsSize(mobTemplateID) >= mobSkill.getLimit()) {
                              break;
                           }

                           mob.setStance(12);
                           c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, posx, true);
                           if (++count >= mobSkill.getSummonSize()) {
                              break;
                           }
                        }
                     }
                  case LUCID_SKILL:
                  case LASER_ATTACK:
                  default:
                     break;
                  case PM_COUNTER:
                     if (mobSkillLevel == 18 || mobSkillLevel == 19) {
                        Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new EnumMap<>(MobTemporaryStatFlag.class);
                        List<Integer> reflection = new LinkedList<>();
                        stats.put(
                           MobTemporaryStatFlag.P_COUNTER,
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.P_COUNTER, mobSkill.getX(), mobSkillID, mobSkill, true)
                        );
                        stats.put(
                           MobTemporaryStatFlag.P_IMMUNE,
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.P_IMMUNE, mobSkill.getX(), mobSkillID, mobSkill, true)
                        );
                        stats.put(
                           MobTemporaryStatFlag.M_COUNTER,
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.M_COUNTER, mobSkill.getX(), mobSkillID, mobSkill, true)
                        );
                        stats.put(
                           MobTemporaryStatFlag.M_IMMUNE,
                           new MobTemporaryStatEffect(MobTemporaryStatFlag.M_IMMUNE, mobSkill.getX(), mobSkillID, mobSkill, true)
                        );
                        reflection.add(mobSkill.getY());
                        monster.applyMonsterBuff(stats, mobSkillID, mobSkill.getDuration(), mobSkill, reflection);
                     }
                     break;
                  case UNDEAD:
                     if (mobSkillLevel == 17) {
                        c.getPlayer().giveDebuff(SecondaryStatFlag.FireBomb, mobSkill.getX(), 0, mobSkill.getDuration(), mobSkillID, mobSkillLevel);
                     }
                     break;
                  case FIRE_BOMB:
                     c.getPlayer().getMap().getCharacters().stream().collect(Collectors.toList()).forEach(p -> p.startFireBombTask());
                     break;
                  case AREA_WARNING:
                     if (mobSkillLevel == 10) {
                        Point posx = monster.getMap().calcDropPos(new Point(monster.getPosition().x, monster.getPosition().y), monster.getPosition());
                        MobSkillInfo msx = MobSkillFactory.getMobSkill(252, 1);
                        Rect rectx = new Rect(posx, msx.getLt(), msx.getRb(), false);
                        AffectedArea mistx = new AffectedArea(rectx, monster, msx, posx, System.currentTimeMillis() + msx.getDuration());
                        if (mistx != null) {
                           c.getPlayer().getMap().spawnMist(mistx);
                        }
                     }
                     break;
                  case AREA_FORCE: {
                     Point pos = c.getPlayer()
                        .getMap()
                        .calcDropPos(new Point(c.getPlayer().getPosition().x, c.getPlayer().getPosition().y), c.getPlayer().getPosition());
                     AffectedArea mist = null;
                     MobSkillInfo ms = MobSkillFactory.getMobSkill(mobSkillID, mobSkillLevel);
                     Rect rect = AffectedArea.calculateRect(pos, false, ms.getLt(), ms.getRb());
                     mist = new AffectedArea(rect, monster, ms, pos, System.currentTimeMillis() + ms.getDuration());
                     mist.setForcePos(AffectedArea.calculateForce(pos, true, ms.getForce(), ms.getForceX()));
                     if (mist != null) {
                        c.getPlayer().getMap().spawnMist(mist);
                     }
                     break;
                  }
                  case PAPULATUS_SKILL:
                     if (mobSkillLevel == 4 && monster.getMap() instanceof Field_Papulatus) {
                        Field_Papulatus f = (Field_Papulatus)monster.getMap();
                        MapleCharacter target = f.getTeleportUser();
                        if (target != null) {
                           f.sendPapulatusTeleport(target, Randomizer.rand(0, 9));
                           f.setTeleportUser(null);
                        }
                     }
                     break;
                  case FIRE_AT_RANDOM_ATTACK:
                     if (fireAtRandomAttack == 1) {
                        int idx = slea.readInt();
                        Rect posx = monster.getFireAtRandomAttack().get(monster.getFireAtRandomAttack().size() - idx);
                        Point charPos = c.getPlayer().getPosition();
                        if (posx.getLeft() <= charPos.x && posx.getTop() <= charPos.y && posx.getRight() >= charPos.x && posx.getBottom() >= charPos.y) {
                           int delta = (int)c.getPlayer().getStat().getCurrentMaxHp(c.getPlayer());
                           if (c.getPlayer().getBuffedValue(SecondaryStatFlag.HolyMagicShell) != null) {
                              Integer value = c.getPlayer().getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                              SecondaryStatEffect eff = c.getPlayer().getBuffedEffect(SecondaryStatFlag.HolyMagicShell);
                              if (value <= 0) {
                                 c.getPlayer().temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                              } else {
                                 SecondaryStatManager statManager = new SecondaryStatManager(c.getPlayer().getClient(), c.getPlayer().getSecondaryStat());
                                 statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, eff.getSourceId(), value - 1);
                                 statManager.temporaryStatSet();
                              }

                              delta = 0;
                           } else if (c.getPlayer().getBuffedValue(SecondaryStatFlag.BlessingArmor) != null) {
                              Integer v = c.getPlayer().getBuffedValue(SecondaryStatFlag.BlessingArmor);
                              if (v != null) {
                                 if (v <= 0) {
                                    c.getPlayer().temporaryStatReset(SecondaryStatFlag.BlessingArmor);
                                 } else {
                                    SecondaryStatManager statManager = new SecondaryStatManager(c.getPlayer().getClient(), c.getPlayer().getSecondaryStat());
                                    statManager.changeStatValue(SecondaryStatFlag.BlessingArmor, 1210016, v - 1);
                                    statManager.temporaryStatSet();
                                 }
                              }

                              delta = 0;
                           } else if (c.getPlayer().getBuffedValue(SecondaryStatFlag.StormGuard) != null) {
                              c.getPlayer().temporaryStatReset(SecondaryStatFlag.StormGuard);
                              delta = 0;
                           } else if (c.getPlayer().getBuffedValue(SecondaryStatFlag.Asura) != null
                              || c.getPlayer().getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() > 0
                              || c.getPlayer().getBuffedValue(SecondaryStatFlag.NotDamaged) != null) {
                              delta = 0;
                           }

                           int[] skills = new int[]{4221006, 32121006, 400031039, 400031040};

                           for (int skill : skills) {
                              AffectedArea area = c.getPlayer().getMap().getMistBySkillId(skill);
                              if (area != null) {
                                 MapleCharacter owner = c.getPlayer().getMap().getCharacterById(area.getOwnerId());
                                 if (owner != null) {
                                    Point pos_ = c.getPlayer().getTruePosition();
                                    if (c.getPlayer().getId() == owner.getId()
                                       || c.getPlayer().getParty() != null
                                          && owner.getParty() != null
                                          && c.getPlayer().getParty().getId() == owner.getParty().getId()) {
                                       Rect rectx = area.getMistRect();
                                       if (rectx != null) {
                                          if (rectx.getLeft() <= pos_.x
                                             && rectx.getTop() <= pos_.y
                                             && rectx.getRight() >= pos_.x
                                             && rectx.getBottom() >= pos_.y) {
                                             delta = 0;
                                          }
                                       } else {
                                          Rectangle rectangle = area.getBox();
                                          if (rectangle != null && rectangle.contains(pos_)) {
                                             delta = 0;
                                          }
                                       }
                                    }
                                 }
                              }
                           }

                           c.getPlayer().addHP(-delta);
                           HPHeal e = new HPHeal(c.getPlayer().getId(), -delta);
                           c.getPlayer().send(e.encodeForLocal());
                           c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
                        }
                     }
                     break;
                  case BOUNCE_ATTACK:
                     PacketEncoder packet = new PacketEncoder();
                     packet.writeShort(SendPacketOpcode.BOUNCE_ATTACK_SKILL.getValue());
                     packet.writeInt(monster.getObjectId());
                     packet.writeInt(mobSkillID);
                     packet.writeInt(mobSkillLevel);
                     if (mobSkillLevel != 6 && mobSkillLevel != 13 && mobSkillLevel != 14 && mobSkillLevel != 16) {
                        packet.write(0);
                        List<BounceAttackInfo> list = new ArrayList<>();

                        for (int i = 0; i < mobSkill.getMobSkillStatsInt(MobSkillStat.count); i++) {
                           list.add(new BounceAttackInfo(monster, mobSkill, 0));
                        }

                        packet.writeInt(mobSkill.getX());
                        packet.writeInt(mobSkill.getY());
                        packet.writeInt(list.size());
                        list.forEach(info -> info.encode(packet, mobSkillID, mobSkillLevel));
                     } else {
                        packet.write(1);
                        packet.writeInt(monster.getDynamicObjects().size());
                        packet.write(1);
                        packet.writeInt(mobSkill.getMobSkillStatsInt(MobSkillStat.y));
                        packet.writeInt(mobSkill.getMobSkillStatsInt(MobSkillStat.x));
                        packet.writeInt(mobSkill.getMobSkillStatsInt(MobSkillStat.z));
                        packet.writeInt(mobSkill.getMobSkillStatsInt(MobSkillStat.w));
                        packet.writeInt(mobSkill.getDuration());

                        for (int i = 0; i < monster.getDynamicObjects().size(); i++) {
                           packet.writeInt(monster.getDynamicObjects().get(i));
                        }
                     }

                     monster.getMap().broadcastMessage(packet.getPacket());
                     break;
                  case TOSS:
                     c.getPlayer()
                        .getMap()
                        .getCharacters()
                        .stream()
                        .collect(Collectors.toList())
                        .forEach(p -> p.send(CField.userTossedBySkill(p.getId(), mobObjectID, mobSkillID, mobSkillLevel, new Point(-1400, -1))));
                     break;
                  case DAMAGE: {
                     MobSkill ms = MapleLifeFactory.getRealMobSkill(mobSkillID, mobSkillLevel);
                     mobSkill.applyEffect(c.getPlayer(), monster, ms, true, false, new Point(0, 0));
                     break;
                  }
                  case HILLAH_VAMPIRE:
                     if (monster.getMap() instanceof Field_Hillah) {
                        Field_Hillah f = (Field_Hillah)monster.getMap();
                        monster.setVampireState(true);
                        f.applyHillahVampireState(monster);
                     }
                     break;
                  case TELEPORT:
                     if (mobSkillLevel == 77) {
                        int x = mobSkill.getSkillStatIntValue(MobSkillStat.x);
                        int y = mobSkill.getSkillStatIntValue(MobSkillStat.y);
                        PacketEncoder packet4 = new PacketEncoder();
                        int m = 1;
                        if (monster.isFacingLeft()) {
                           m = -1;
                        }

                        int newX = monster.getTruePosition().x + m * y;
                        int newY = monster.getTruePosition().y;
                        packet4.writeInt(newX);
                        packet4.writeInt(newY);
                        monster.getController().send(MobPacket.teleportRequest(monster.getObjectId(), MobTeleportType.RandomUser2, packet4));
                        packet4 = new PacketEncoder();
                        packet4.writeShort(SendPacketOpcode.MOB_DUNKEL_DELAYED_ATTACK_CREATE.getValue());
                        packet4.writeInt(monster.getObjectId());
                        packet4.writeInt(mobSkillID);
                        packet4.writeInt(mobSkillLevel);
                        packet4.writeInt(monster.getId());
                        packet4.writeInt(mobSkill.getSkillStatIntValue(MobSkillStat.v2));
                        System.out.println("@마을" + mobSkill.getSkillStatIntValue(MobSkillStat.v2));
                        packet4.write(monster.isFacingLeft() ? 1 : 0);
                        packet4.writeInt(newX);
                        packet4.writeInt(newY);
                        monster.getMap().broadcastMessage(packet4.getPacket());
                     }
               }
            }
         }
      }
   }

   public static void mobDamageShareInfo(PacketDecoder slea, MapleClient c) {
      int mobObjectID = slea.readInt();
      byte delta = slea.readByte();
      MapleMonster monster = c.getPlayer().getMap().getMonsterByOid(mobObjectID);
      if (monster != null) {
         if (delta > 0) {
            monster.addDamageShareUsers(c.getPlayer());
         } else {
            monster.removeDamageShareUsers(c.getPlayer());
         }

         c.getPlayer().send(MobPacket.mobDamageShareInfoToLocal(mobObjectID, delta, (byte)monster.getDamageShareUserCount()));

         for (MapleCharacter player : monster.getDamageShareUsers()) {
            if (player.getId() != c.getPlayer().getId()) {
               player.send(MobPacket.mobDamageShareInfoToRemote(mobObjectID, (byte)monster.getDamageShareUserCount()));
            }
         }
      }
   }

   public static final void mobExplosionStart(PacketDecoder slea, MapleClient c) {
      int objectID = slea.readInt();
      int playerID = slea.readInt();
      int skillID = slea.readInt();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         MapleMonster mob = player.getMap().getMonsterByOid(objectID);
         if (mob != null) {
            int activeSkillID = 0;
            int mobCount = 1;
            if (skillID == 11121004) {
               MobTemporaryStatEffect eff = mob.getBuff(MobTemporaryStatFlag.SOUL_EXPLOSTION);
               if (eff == null) {
                  return;
               }

               if (eff.getW() != playerID) {
                  return;
               }

               activeSkillID = 11121013;
               mob.cancelStatus(MobTemporaryStatFlag.SOUL_EXPLOSTION);
            } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
               activeSkillID = 36110005;
               mob.cancelStatus(MobTemporaryStatFlag.EXPLOSION);
               mob.cancelStatus(MobTemporaryStatFlag.EVA);
               mob.cancelStatus(MobTemporaryStatFlag.BLIND);
            } else if (GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
               activeSkillID = 65101006;
               mob.cancelStatus(MobTemporaryStatFlag.EXPLOSION);
            }

            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.USER_EXPLOSION_ATTACK.getValue());
            packet.writeInt(activeSkillID);
            packet.writeInt(mob.getPosition().x);
            packet.writeInt(mob.getPosition().y);
            packet.writeInt(objectID);
            packet.writeInt(mobCount);
            player.send(packet.getPacket());
         }
      }
   }

   public static final void mobAreaAttackDisease(PacketDecoder slea, final MapleClient c) {
      int objectID = slea.readInt();
      final MapleMonster monster = c.getPlayer().getMap().getMonsterByOid(objectID);
      if (monster != null) {
         int attackIdx = slea.readInt();
         final MobAttackInfo info = monster.getStats().getMobAttack(attackIdx);
         if (info != null) {
            final Point diseasePos = new Point(slea.readInt(), slea.readInt());
            int delay = slea.readInt();
            Timer.MapTimer.getInstance()
               .schedule(
                  new Runnable() {
                     @Override
                     public void run() {
                        if (monster.getMap().getId() == c.getPlayer().getMap().getId()) {
                           MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(info.getDiseaseSkill(), info.getDiseaseLevel());
                           if (mobSkillInfo != null) {
                              Rect rect = new Rect(
                                 mobSkillInfo.getLt().x + diseasePos.x,
                                 mobSkillInfo.getLt().y + diseasePos.y,
                                 mobSkillInfo.getRb().x + diseasePos.x,
                                 mobSkillInfo.getRb().y + diseasePos.y
                              );
                              c.getPlayer()
                                 .getMap()
                                 .spawnMist(new AffectedArea(rect, monster, mobSkillInfo, diseasePos, System.currentTimeMillis() + mobSkillInfo.getDuration()));
                           }
                        }
                     }
                  },
                  delay
               );
         }
      }
   }

   public static void mobAttackMob(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field map = player.getMap();
         if (map != null) {
            int objectID = slea.readInt();
            slea.skip(4);
            int target = slea.readInt();
            int attackIndex = slea.readByte() & 255;
            byte uuu = slea.readByte();
            slea.skip(1);
            int damage = slea.readInt();
            byte critical = slea.readByte();
            slea.skip(4);
            Point pos = slea.readPos();
            MapleMonster from = map.getMonsterByOid(objectID);
            MapleMonster to = map.getMonsterByOid(target);
            if (from != null && from.isAlive() && to != null && to.isAlive()) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.MOB_ATTACKED_BY_MOB.getValue());
               packet.writeInt(to.getObjectId());
               packet.write(attackIndex);
               packet.writeInt(damage);
               packet.write(critical);
               packet.writeInt(from.getId());
               packet.write(uuu);
               to.damage(player, damage, true);
            }
         }
      }
   }

   public static void papulatusLaserCollision(MapleClient c) {
      Field map = c.getPlayer().getMap();
      if (map != null) {
         if (map instanceof Field_Papulatus) {
            Field_Papulatus f = (Field_Papulatus)map;
            f.onLaserCollision();
         }
      }
   }

   public static void papulatusHoldCrane(PacketDecoder slea, MapleClient c) {
      Field map = c.getPlayer().getMap();
      if (map != null) {
         if (map instanceof Field_Papulatus) {
            Field_Papulatus f = (Field_Papulatus)map;
            f.onHoldByCrane(c.getPlayer(), slea);
         }
      }
   }

   public static void papulatusReleaseHoldCrane(PacketDecoder slea, MapleClient c) {
      Field map = c.getPlayer().getMap();
      if (map != null) {
         if (map instanceof Field_Papulatus) {
            Field_Papulatus f = (Field_Papulatus)map;
            f.onRelaseHoldCrane(c.getPlayer(), slea);
         }
      }
   }

   public static void mobCreateFireWalk(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            MapleMonster mob = field.getMonsterByOid(slea.readInt());
            if (mob != null) {
               int skillID = slea.readInt();
               int skillLevel = slea.readInt();
               Point point = slea.readPos();
               MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillID, skillLevel);
               if (mobSkillInfo != null) {
                  int callSkill = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.callSkill);
                  int callSkillLevel = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.level);
                  if (callSkill > 0 && callSkillLevel > 0) {
                     MobSkillInfo skill = MobSkillFactory.getMobSkill(callSkill, callSkillLevel);
                     if (skill != null && skill.getSkillId() == MobSkillID.AREA_POISON.getVal()) {
                        Rect rect = new Rect(point, skill.getLt(), skill.getRb(), false);
                        AffectedArea area = new AffectedArea(
                           rect,
                           c.getPlayer().getId(),
                           MobSkillFactory.getMobSkill(callSkill, callSkillLevel),
                           point,
                           0,
                           System.currentTimeMillis() + skill.getDuration()
                        );
                        c.getPlayer().getMap().spawnMist(area);
                        return;
                     }
                  }

                  System.out
                     .println(
                        "[ERROR] 알 수 없는 CreateFireWalk 몹 스킬. CallSkill=" + callSkill + ", CallSkillLevel=" + callSkillLevel + ", TemplateID=" + mob.getId()
                     );
               }
            }
         }
      }
   }

   public static void guardianWaveCrystal(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         if (chr.getMap() instanceof Field_GuardianAngelSlime) {
            int objectID = slea.readInt();
            int x = slea.readInt();
            int y = slea.readInt();
            GuardianAngelSlime.InitCrystal encode = new GuardianAngelSlime.InitCrystal(1, 6, chr.getId());
            encode.objectID = objectID;
            encode.charID = chr.getId();
            encode.posX = x;
            encode.posY = y;
            byte[] packet = ((Field_GuardianAngelSlime)chr.getMap()).encode(encode);
            chr.getMap().broadcastMessage(packet);
         }
      }
   }

   public static void guardianWaveEnterPortal(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         if (chr.getMap() instanceof Field_GuardianAngelSlime) {
            int count = slea.readInt();
            int towerObjectID = slea.readInt();
            if (((Field_GuardianAngelSlime)chr.getMap()).slimeTowerMap.getOrDefault(towerObjectID, 0) == 3) {
               ((Field_GuardianAngelSlime)chr.getMap()).successGuardianWave();
            } else {
               GuardianAngelSlime.InitGuardianWave encode = new GuardianAngelSlime.InitGuardianWave();
               encode.state = 2;
               byte[] packet = ((Field_GuardianAngelSlime)chr.getMap()).encode(encode);
               chr.getMap().broadcastMessage(packet);
            }
         }
      }
   }

   public static void guardianAngelSlime(PacketDecoder slea, MapleClient c) {
      int type = slea.readInt();
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         if (chr.getMap() instanceof Field_GuardianAngelSlime) {
            switch (type) {
               case 4: {
                  int objectID = slea.readInt();
                  GuardianAngelSlime.MagmaSlimeInit encode = new GuardianAngelSlime.MagmaSlimeInit(2);
                  if (((Field_GuardianAngelSlime)chr.getMap()).slimeMap.get(objectID) != null) {
                     encode.objectID = objectID;
                     byte[] packetx = ((Field_GuardianAngelSlime)chr.getMap()).encode(encode);
                     chr.getMap().broadcastMessage(packetx);
                  }
                  break;
               }
               case 5: {
                  int charIDx = slea.readInt();
                  boolean bLeft = slea.readByte() == 1;
                  boolean bBottom = slea.readByte() == 1;
                  int posX = slea.readInt();
                  int posY = slea.readInt();
                  GuardianAngelSlime.MagmaSlimeInit encode = new GuardianAngelSlime.MagmaSlimeInit(6);
                  encode.charID = charIDx;
                  if (bLeft && bBottom) {
                     encode.value1 = 1;
                  } else if (bLeft) {
                     encode.value1 = 0;
                  } else if (!bBottom) {
                     encode.value1 = 2;
                  } else {
                     encode.value1 = 3;
                  }

                  encode.srcPos = new Point(posX, posY);
                  encode.destPos = new Point(bLeft ? 90 : -90, bBottom ? 90 : -90);
                  encode.objectID = ((Field_GuardianAngelSlime)chr.getMap()).findMagmaSlime(new Point(posX - encode.destPos.x, chr.getTruePosition().y));
                  byte[] packet = ((Field_GuardianAngelSlime)chr.getMap()).encode(encode);
                  chr.getMap().broadcastMessage(packet);
                  break;
               }
               case 6:
               case 8:
               case 10:
               case 11:
               case 13:
               default:
                  System.out.println("Unknown type : " + type);
                  break;
               case 7: {
                  int objectID = slea.readInt();
                  int srcPosX = slea.readInt();
                  int srcPosY = slea.readInt();
                  int destPosX = slea.readInt();
                  int destPosY = slea.readInt();
                  GuardianAngelSlime.MagmaSlimeInit encode = new GuardianAngelSlime.MagmaSlimeInit(8);
                  encode.objectID = objectID;
                  encode.srcPos = new Point(srcPosX, srcPosY);
                  encode.destPos = new Point(destPosX, destPosY);
                  byte[] packet = ((Field_GuardianAngelSlime)chr.getMap()).encode(encode);
                  ((Field_GuardianAngelSlime)chr.getMap()).slimePointList.put(encode.objectID, encode.destPos);
                  chr.getMap().broadcastMessage(packet);
                  break;
               }
               case 9: {
                  GuardianAngelSlime.MagmaSlimeInit encode = new GuardianAngelSlime.MagmaSlimeInit(10);
                  encode.objectID = slea.readInt();
                  encode.value2 = slea.readInt();
                  encode.value3 = slea.readLong();
                  byte[] packet = ((Field_GuardianAngelSlime)chr.getMap()).encode(encode);
                  chr.getMap().broadcastMessage(packet);
                  break;
               }
               case 12: {
                  GuardianAngelSlime.MagmaSlimeInit encode = new GuardianAngelSlime.MagmaSlimeInit(13);
                  encode.objectID = slea.readInt();
                  slea.skip(4);
                  encode.towerObjectID = slea.readInt();
                  ((Field_GuardianAngelSlime)chr.getMap()).slimePointList.remove(encode.objectID);
                  ((Field_GuardianAngelSlime)chr.getMap()).updateTower(encode);
                  break;
               }
               case 14:
                  int charID = slea.readInt();
                  if (chr.getId() == charID) {
                     ((Field_GuardianAngelSlime)chr.getMap()).reduceGauge(chr);
                  }
            }
         }
      }
   }

   public static void kalosActionRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_BossKalos) {
               Field_BossKalos f = (Field_BossKalos)field;
               f.doAction(packet, player);
            }
         }
      }
   }

   public static void karingMatchRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            if (field instanceof Field_BossKaring) {
               Field_BossKaringMatch f = (Field_BossKaringMatch)field;
               f.doMatching(packet, player);
            }
         }
      }
   }
}
