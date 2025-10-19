package objects.fields.child.karrotte;

import constants.QuestExConstants;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.MobPacket;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.TextEffect;
import objects.fields.child.karrotte.guardian.EyeOfAbyss;
import objects.fields.child.karrotte.guardian.EyeOfRedemption;
import objects.fields.child.karrotte.guardian.FighterPlane;
import objects.fields.child.karrotte.guardian.Guardian;
import objects.fields.child.karrotte.guardian.GuardianEntry;
import objects.fields.child.karrotte.guardian.GuardianType;
import objects.fields.child.karrotte.guardian.SphereOfOdium;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;

public class Field_BossKalosPhase2 extends Field_BossKalos {
   private static final Point EYE_OF_REDEMPTION_POS = new Point(-739, -98);
   private static final Point EYE_OF_THE_ABYSS_POS = new Point(1775, -98);
   private static final Point FIGHTER_PLANE_POS = new Point(630, -553);
   private static final Point SPHERE_OF_ODIUM_POS = new Point(0, 0);
   private long nextAssaultTime = 0L;
   private long nextSpecialAssaultTime = 0L;
   private long nextFieldSkillTime = 0L;
   private long nextFieldUpWingSkillTime = 0L;
   private long nextFieldDownWingSkillTime = 0L;
   private int AssaultInterval = 60000;
   private int SpecialAssaultInterval = 150000;
   private int FieldSkillInterval = 15000;
   private int FieldSkillIntervalUpWing = 15000;
   private int FieldSkillIntervalDownWing = 15000;
   private int hpRefMob = 8880802;
   private int[] phaseMob = new int[]{8880803, 8880804, 8880805, 8880806};
   private int skillMob = 8880807;
   private int subPhase = 1;
   private boolean doFury = false;
   private long endFuryTime = 0L;
   private boolean furySuccess = true;
   private boolean clearSet = false;

   public Field_BossKalosPhase2(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   public boolean nextPhase() {
      return this.subPhase == 4 && this.getMonsterById(this.phaseMob[3]) == null;
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.nextPhase()) {
         if (!this.canClear) {
            return;
         }

         MapleCharacter player = null;

         for (MapleCharacter mapleCharacter : this.getCharacters()) {
            if (mapleCharacter.getParty() != null) {
               player = mapleCharacter;
               break;
            }
         }

         if (player != null) {
            int warpTo = this.getId() + 20;

            for (MapleCharacter chr : player.getPartyMembers()) {
               if (chr.getMapId() == this.getId() - 20 || chr.getMapId() == this.getId() && chr.getRegisterTransferFieldTime() == 0L) {
                  TextEffect e = new TextEffect("#fn나눔고딕 ExtraBold##fs32##r#e여기까진가... 죄송합니다. 아버지...", 100, 2500, 4, 0, 0, 1, 0);
                  chr.send(e.encodeForLocal());
                  chr.setRegisterTransferField(warpTo);
                  chr.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
               }
            }

            for (MapleCharacter p : this.getCharactersThreadsafe()) {
               if (p.getBossMode() == 1) {
                  return;
               }

               if (p.getParty() != null && !this.clearSet) {
                  this.bossClear(this.phaseMob[3], 1234569, "kalos_clear");
                  String list = "";
                  ArrayList<String> names = new ArrayList<>();

                  for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                     names.add(mpc.getName());
                  }

                  String difficulty = "";
                  String mode = this.getMode();
                  if (mode.equals("easy")) {
                     difficulty = "이지";
                  } else if (mode.equals("normal")) {
                     difficulty = "노말";
                  } else if (mode.equals("chaos")) {
                     difficulty = "카오스";
                  } else {
                     difficulty = "익스트림";
                  }

                  difficulty = "노말";
                  list = String.join(",", names);

                  for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                     StringBuilder sb = new StringBuilder("보스 " + difficulty + " 칼로스 격파 (" + list + ")");
                     MapleCharacter pm = this.getCharacterById(mpc.getId());
                     if (pm != null) {
                        LoggingManager.putLog(new BossLog(pm, BossLogType.ClearLog.getType(), sb));
                     }
                  }

                  Center.Broadcast.broadcastMessageCheckQuest(
                     CField.chatMsg(
                        8,
                        "[보스격파] [CH."
                           + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                           + "] '"
                           + p.getParty().getPartyMember().getLeader().getName()
                           + "' 파티("
                           + list
                           + ")가 ["
                           + difficulty
                           + " 칼로스]를 격파하였습니다."
                     ),
                     "BossMessage"
                  );

                  try {
                     FieldSetInstance boss = this.getFieldSetInstance();
                     int questId = (Integer)QuestExConstants.bossQuests.getOrDefault(this.hpRefMob, 0);
                     if (boss.userList != null && !boss.userList.isEmpty() && !boss.isPracticeMode && questId != 0) {
                        for (Integer playerID : boss.userList) {
                           boolean find = false;

                           for (GameServer gs : GameServer.getAllInstances()) {
                              MapleCharacter party = gs.getPlayerStorage().getCharacterById(playerID);
                              if (party != null) {
                                 party.updateOneInfo(questId, "count", String.valueOf(party.getOneInfoQuestInteger(questId, "count") + 1));
                                 party.updateOneInfo(questId, "mobid", String.valueOf(this.hpRefMob));
                                 party.updateOneInfo(questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                                 party.updateOneInfo(questId, "mobDead", "1");
                                 find = true;
                                 break;
                              }
                           }

                           if (!find) {
                              this.updateOfflineBossLimit(playerID, questId, "count", "1");
                              this.updateOfflineBossLimit(playerID, questId, "mobid", String.valueOf(this.hpRefMob));
                              this.updateOfflineBossLimit(playerID, questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                              this.updateOfflineBossLimit(playerID, questId, "mobDead", "1");
                           }
                        }
                     }
                  } catch (Exception var18) {
                     System.out.println("칼로스 클리어 처리 오류");
                     var18.printStackTrace();
                  }

                  this.clearSet = true;
               }
            }
         }
      }

      MapleMonster boss = this.findBoss();
      if (this.getCharactersSize() != 0) {
         if (boss != null) {
            if (this.doFury && !this.furySuccess) {
               KalosAction.Phase2KalosAction.FuryOfTheGuardianEnd endFury = new KalosAction.Phase2KalosAction.FuryOfTheGuardianEnd();
               endFury.broadcastPacket(this);
               int n = this.hpRefMob + this.subPhase;
               this.killAllMonster(n);
               MapleMonster phase2Boss = MapleLifeFactory.getMonster(n);
               phase2Boss.setHp((long)(phase2Boss.getMobMaxHp() * 0.2));
               this.spawnMonsterOnGroundBelow(phase2Boss, new Point(570, 83));
               this.handleMobHP(phase2Boss);
               this.furySuccess = true;
               this.doFury = false;
               this.endFuryTime = 0L;
            }

            if (this.doFury && this.endFuryTime <= System.currentTimeMillis()) {
               KalosAction.Phase2KalosAction.FuryOfTheGuardianEnd endFury = new KalosAction.Phase2KalosAction.FuryOfTheGuardianEnd();
               endFury.broadcastPacket(this);
               int n = this.hpRefMob + this.subPhase;
               this.killAllMonster(n);
               MapleMonster nextPhaseKalos = MapleLifeFactory.getMonster(n + 1);
               this.spawnMonsterOnGroundBelow(nextPhaseKalos, new Point(570, 83));
               KalosAction.Phase2KalosAction.FuryOfTheGuardianSuccess successFury = new KalosAction.Phase2KalosAction.FuryOfTheGuardianSuccess();
               successFury.broadcastPacket(this);
               this.subPhase++;
               this.doFury = false;
               this.endFuryTime = 0L;
            }

            if (this.nextAssaultTime <= System.currentTimeMillis()) {
               if (this.nextAssaultTime != 0L) {
                  this.doAssault();
               }

               this.nextAssaultTime = System.currentTimeMillis() + this.AssaultInterval;
            }

            if (this.nextFieldSkillTime <= System.currentTimeMillis()) {
               if (this.nextFieldSkillTime != 0L && (double)boss.getHp() / boss.getMobMaxHp() <= 50.0) {
                  this.FieldSkillInterval = 10000;
               }

               this.nextFieldSkillTime = System.currentTimeMillis() + this.FieldSkillInterval;
            }

            if (this.subPhase >= 2 && this.nextFieldUpWingSkillTime <= System.currentTimeMillis()) {
               if (this.nextFieldUpWingSkillTime != 0L) {
               }

               this.nextFieldUpWingSkillTime = System.currentTimeMillis() + this.FieldSkillIntervalUpWing;
            }

            if (this.subPhase >= 3 && this.nextFieldDownWingSkillTime <= System.currentTimeMillis()) {
               if (this.nextFieldDownWingSkillTime != 0L) {
               }

               this.nextFieldDownWingSkillTime = System.currentTimeMillis() + this.FieldSkillIntervalDownWing;
            }

            if (boss.getId() != this.hpRefMob && this.nextSpecialAssaultTime <= System.currentTimeMillis()) {
               if (this.nextSpecialAssaultTime != 0L) {
                  boss.addOnetimeFsmSkill(1);
               }

               this.nextSpecialAssaultTime = System.currentTimeMillis() + this.SpecialAssaultInterval;
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.nextAssaultTime = 0L;
      this.nextFieldDownWingSkillTime = 0L;
      this.nextFieldUpWingSkillTime = 0L;
      this.nextSpecialAssaultTime = 0L;
      this.nextFieldSkillTime = 0L;
      this.doFury = false;
      this.subPhase = 1;
      this.clearSet = false;
      int guardianRefMobID = 8880801;
      this.skillMob = guardianRefMobID;
      MapleMonster guardianAttackMob = MapleLifeFactory.getMonster(guardianRefMobID);
      this.spawnMonster_sSack(guardianAttackMob, new Point(398, 772), 0, false, true);
      int phase2BossMobID = 8880803;

      for (int i = 0; i < 4; i++) {
         this.phaseMob[i] = phase2BossMobID + i;
      }

      MapleMonster phase2Boss = MapleLifeFactory.getMonster(phase2BossMobID);
      this.spawnMonster_sSack(phase2Boss, new Point(398, 770), 0, false, true);
      this.hpRefMob = 8880802;
      MapleMonster phase2BossHP = MapleLifeFactory.getMonster(this.hpRefMob);
      this.spawnMonster_sSack(phase2BossHP, new Point(398, 770), 0, false, true);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.guardian == null) {
         int refMob = this.skillMob;
         ArrayList<GuardianEntry> guardians = new ArrayList<>();
         guardians.add(new EyeOfRedemption(0, EYE_OF_REDEMPTION_POS, (byte)1, GuardianType.EyeOfRedemption, refMob));
         guardians.add(new EyeOfAbyss(1, EYE_OF_THE_ABYSS_POS, (byte)0, GuardianType.EyeOfTheAbyss, refMob));
         guardians.add(new FighterPlane(2, FIGHTER_PLANE_POS, (byte)0, GuardianType.FighterPlane, refMob));
         guardians.add(new SphereOfOdium(3, SPHERE_OF_ODIUM_POS, (byte)0, GuardianType.SphereOfOdium, refMob));
         this.guardian = new Guardian(guardians);
      }

      KalosAction.GuardianAction.CreateGuardian createGuardian = new KalosAction.GuardianAction.CreateGuardian(this.guardian.getGuardians());
      createGuardian.sendPacket(player);
      KalosAction.InitPacket.CreateLiberationTop liberationTop = new KalosAction.InitPacket.CreateLiberationTop();
      liberationTop.sendPacket(player);
      KalosAction.InitPacket.CreateLiberationBottom liberationBottom = new KalosAction.InitPacket.CreateLiberationBottom(2);
      liberationBottom.sendPacket(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
      if (this.doFury && this.endFuryTime - System.currentTimeMillis() > 0L) {
         this.furySuccess = false;
      }
   }

   @Override
   public void handleFreeze(MapleMonster mob) {
      if (mob != null) {
         for (int mobid : this.phaseMob) {
            if (mobid == mob.getId()) {
               this.SpecialAssaultInterval += 10000;
               break;
            }
         }
      }
   }

   @Override
   public void handleMobHP(MapleMonster mob) {
      if (!this.doFury) {
         MapleMonster hpMob = this.getMonsterById(this.hpRefMob);
         int phase1 = this.phaseMob[0];
         int phase2 = this.phaseMob[1];
         int phase3 = this.phaseMob[2];
         int phase4 = this.phaseMob[3];
         if (mob != null && hpMob != null) {
            long maxHP = hpMob.getMobMaxHp();
            long curHP;
            if (mob.getId() == phase1) {
               long defHP = (long)(maxHP * 0.75);
               long addHP = (long)(maxHP * 0.25 * mob.getHp() / mob.getMobMaxHp());
               if (addHP <= 0L) {
                  addHP = 0L;
                  this.doFury = true;
               }

               curHP = defHP + addHP;
            } else if (mob.getId() == phase2) {
               long defHP = (long)(maxHP * 0.5);
               long addHP = (long)(maxHP * 0.25 * mob.getHp() / mob.getMobMaxHp());
               if (addHP <= 0L) {
                  addHP = 0L;
                  this.doFury = true;
               }

               curHP = defHP + addHP;
            } else if (mob.getId() == phase3) {
               long defHP = (long)(maxHP * 0.25);
               long addHP = (long)(maxHP * 0.25 * mob.getHp() / mob.getMobMaxHp());
               if (addHP <= 0L) {
                  addHP = 0L;
                  this.doFury = true;
               }

               curHP = defHP + addHP;
            } else {
               if (mob.getId() != phase4) {
                  return;
               }

               long defHP = 0L;
               long addHP = (long)(maxHP * 0.25 * mob.getHp() / mob.getMobMaxHp());
               if (addHP < 0L) {
                  addHP = 0L;
               }

               curHP = defHP + addHP;
            }

            hpMob.setHp(curHP);
            this.broadcastMessage(MobPacket.showBossHP(hpMob));
            if (this.doFury) {
               if (this.getCharactersSize() == 0) {
                  return;
               }

               this.killAllMonster(mob.getId());
               MapleMonster fakeMob = MapleLifeFactory.getMonster(mob.getId());
               fakeMob.setPosition(mob.getPosition());
               fakeMob.setBlockedController(true);
               MapleCharacter player = this.getCharacters().get(0);
               MobTemporaryStatEffect mtse = null;
               mtse = new MobTemporaryStatEffect(MobTemporaryStatFlag.CASTING, 1, 0, null, false);
               mtse.setDuration(600);
               fakeMob.applyStatus(mtse, false);
               this.spawnMonster(fakeMob, -1, true, false);
               this.broadcastMessage(MobPacket.teleportRequest(fakeMob.getObjectId(), 17, 570, 83));
               KalosAction.Phase2KalosAction.FuryOfTheGuardianStart furyStart = new KalosAction.Phase2KalosAction.FuryOfTheGuardianStart(
                  fakeMob.getObjectId(), fakeMob.getId()
               );
               furyStart.broadcastPacket(this);
               TextEffect e = new TextEffect("#fn나눔고딕 ExtraBold##fs26##fc0xff71ffff#폭주에 휘말리지 않도록 수호 지대를 찾아라...", 100, 2500, 4, 0, 0, 1, 0);
               this.broadcastMessage(e.encodeForLocal());
               KalosAction.Phase2KalosAction.FuryOfTheGuardianSafetyZoneSet furySet = new KalosAction.Phase2KalosAction.FuryOfTheGuardianSafetyZoneSet(
                  this.skillMob, 9
               );
               furySet.broadcastPacket(player.getMap());
               this.endFuryTime = System.currentTimeMillis() + 28500L;
               this.furySuccess = true;
            }
         }
      }
   }
}
