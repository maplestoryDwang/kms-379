package objects.fields.child.will;

import database.DBConfig;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.MobPacket;
import objects.effect.child.WillEffect;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.fieldset.instance.HardWillBoss;
import objects.fields.fieldset.instance.HellWillBoss;
import objects.fields.fieldset.instance.NormalWillBoss;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillCommand;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;
import objects.utils.Triple;

public class Field_WillBattle extends Field {
   private int phase = 1;
   protected List<Integer> willHPList = new ArrayList<>();
   private List<Integer> noticed = new ArrayList<>();
   private long startMonitorBreakTime = 0L;
   private int monitorBreakType = 0;
   private boolean setMirrorOfLies = false;
   private boolean setTakeDown = false;
   private boolean setCreateWeb = false;
   private long nextMonitorBreakTime = 0L;
   private long nextDestructionTime = 0L;
   private long nextTakeDownTime = 0L;
   private long startTakeDownTime = 0L;
   private int mirrorOfLiesCount = 0;
   private long nextStartMirrorOfLiesTime = 0L;
   private int takeDownCount = 0;
   private long startGroggyTime = 0L;
   private long nextCreateWebTime = 0L;
   private long next3rdAttackTime = 0L;
   private int next3rdAttackType = 0;
   private int level;
   private boolean successDestruction = true;
   private long nextGroggyCancelTime = 0L;
   private long playerDeadTime = 0L;
   private long beforeMonitorBreakTime = 0L;

   public Field_WillBattle(int mapid, int channel, int returnMapId, float monsterRate, int phase) {
      super(mapid, channel, returnMapId, monsterRate);
      this.phase = phase;
      this.willHPList = new ArrayList<>();
      this.willHPList.add(666);
      this.willHPList.add(333);
      this.willHPList.add(3);
      if (mapid == 450008750 || mapid == 450008850 || mapid == 450008950) {
         this.level = 1;
      } else if (mapid == 450008150 || mapid == 450008250 || mapid == 450008350) {
         this.level = 2;
      } else if (mapid == 450007850 || mapid == 450007950 || mapid == 450008050) {
         this.level = 0;
      }
   }

   public List<Integer> getWillHPList() {
      return this.willHPList;
   }

   protected void monitorBreak() {
      MapleMonster boss = this.findMapPatternMob(1);
      if (boss != null) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            int type = this.getMonitorBreakType();
            boolean top = player.getTruePosition().y < -1500;
            if (type == 1 && top || type == 0 && !top) {
               WillEffect e = new WillEffect(-1, true, 242, 5);
               this.broadcastMessage(e.encodeForLocal());
               this.sendWillSpiderAttack(null, boss.getId(), 242, 5, Collections.singletonList(null));
               boolean find = false;
               int[] skills = new int[]{4221006, 32121006, 400031039, 400031040};

               for (int skill : skills) {
                  AffectedArea area = this.getMistBySkillId(skill);
                  if (area != null) {
                     MapleCharacter owner = this.getCharacterById(area.getOwnerId());
                     if (owner != null) {
                        Point pos_ = player.getTruePosition();
                        if (player.getId() == owner.getId()
                           || player.getParty() != null && owner.getParty() != null && player.getParty().getId() == owner.getParty().getId()) {
                           Rect rect = area.getMistRect();
                           if (rect != null) {
                              if (rect.getLeft() <= pos_.x && rect.getTop() <= pos_.y && rect.getRight() >= pos_.x && rect.getBottom() >= pos_.y) {
                                 find = true;
                              }
                           } else {
                              Rectangle rectangle = area.getBox();
                              if (rectangle != null && rectangle.contains(pos_)) {
                                 find = true;
                              }
                           }
                        }
                     }
                  }
               }

               if (!find) {
                  if (player.getBuffedValue(SecondaryStatFlag.HolyMagicShell) != null) {
                     Integer value = player.getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                     SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.HolyMagicShell);
                     if (value <= 0) {
                        player.temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                     } else {
                        SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                        statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, eff.getSourceId(), value - 1);
                        statManager.temporaryStatSet();
                     }
                  } else if (player.getBuffedValue(SecondaryStatFlag.BlessingArmor) != null) {
                     Integer v = player.getBuffedValue(SecondaryStatFlag.BlessingArmor);
                     if (v != null) {
                        if (v <= 0) {
                           player.temporaryStatReset(SecondaryStatFlag.BlessingArmor);
                        } else {
                           SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                           statManager.changeStatValue(SecondaryStatFlag.BlessingArmor, 1210016, v - 1);
                           statManager.temporaryStatSet();
                        }
                     }
                  } else if (player.getBuffedValue(SecondaryStatFlag.StormGuard) != null) {
                     player.temporaryStatReset(SecondaryStatFlag.StormGuard);
                  } else if (player.getBuffedValue(SecondaryStatFlag.BlitzShield) != null) {
                     player.temporaryStatReset(SecondaryStatFlag.BlitzShield);
                  } else if (player.getBuffedValue(SecondaryStatFlag.EtherealForm) == null
                     && player.getBuffedValue(SecondaryStatFlag.Asura) == null
                     && player.getIndieTemporaryStatEntries(SecondaryStatFlag.indiePartialNotDamaged).isEmpty()
                     && player.getBuffedValue(SecondaryStatFlag.NotDamaged) == null) {
                     player.addHP(-player.getStat().getCurrentMaxHp(player));
                  }
               }
            }
         }

         if (this.beforeMonitorBreakTime <= this.playerDeadTime) {
            this.successDestruction = false;
         }

         Timer.MapTimer.getInstance().schedule(() -> {
            if (this.successDestruction) {
               boolean check = false;
               if (boss.getMobMaxHp() * 0.001 * 666.0 >= boss.getHp() && this.getWillHPList().contains(666)) {
                  check = true;
               } else if (boss.getMobMaxHp() * 0.001 * 333.0 >= boss.getHp() && this.getWillHPList().contains(333)) {
                  check = true;
               } else if (boss.getMobMaxHp() * 0.001 * 3.0 >= boss.getHp() && this.getWillHPList().contains(3)) {
                  check = true;
               }

               if (check) {
                  MapleMonster mob = this.findMapPatternMob(1);
                  if (mob != null) {
                     MobSkillCommand command = mob.getCommand();
                     command.setSkillCommand(242);
                     command.setSkillCommandLevel(4);
                     mob.setCommand(command);
                  }

                  for (MapleMonster m : this.getAllMonstersThreadsafe()) {
                     for (int i = 0; i < 6; i++) {
                        m.addAttackBlocked(i);
                     }

                     m.broadcastAttackBlocked();
                  }
               }
            }

            this.successDestruction = true;
            this.setStartMonitorBreakTime(0L);
            this.setMonitorBreakType(0);
         }, 3500L);
      }
   }

   public MapleMonster findMapPatternMob(int phase) {
      int[][] mobID = new int[][]{{8880300, 8880340, 8880360}};
      MapleMonster boss = null;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         for (int id : mobID[phase - 1]) {
            if (mob.getId() == id) {
               boss = mob;
               break;
            }
         }
      }

      return boss;
   }

   public void destructionPattern() {
      for (MapleMonster mob : new ArrayList<>(this.getAllMonstersThreadsafe())) {
         if (mob.getId() == 8880305) {
            this.removeMonster(mob, 1);
         }
      }

      int[] mobID = new int[]{8880300, 8880340, 8880360};
      MapleMonster boss = null;

      for (MapleMonster mobx : this.getAllMonstersThreadsafe()) {
         for (int id : mobID) {
            if (mobx.getId() == id) {
               boss = mobx;
               break;
            }
         }
      }

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         boolean top = player.getTruePosition().y < -1500;
         AffectedArea moon = null;

         for (AffectedArea area : this.getAllMistsThreadsafe()) {
            if (area.isMobMist() && area.getMobSkill().getSkillId() == 242 && area.getMobSkill().getSkillLevel() == 4) {
               if (top && area.getTruePosition().y < -1500) {
                  moon = area;
               } else if (!top && area.getTruePosition().y > 0) {
                  moon = area;
               }
            }
         }

         if (moon == null || player.getTruePosition().x < -205 || player.getTruePosition().x > 205) {
            boolean find = false;
            int[] skills = new int[]{4221006, 32121006, 400031039, 400031040};

            for (int skill : skills) {
               AffectedArea areax = this.getMistBySkillId(skill);
               if (areax != null) {
                  MapleCharacter owner = this.getCharacterById(areax.getOwnerId());
                  if (owner != null) {
                     Point pos_ = player.getTruePosition();
                     if (player.getId() == owner.getId()
                        || player.getParty() != null && owner.getParty() != null && player.getParty().getId() == owner.getParty().getId()) {
                        Rect rect = areax.getMistRect();
                        if (rect != null) {
                           if (rect.getLeft() <= pos_.x && rect.getTop() <= pos_.y && rect.getRight() >= pos_.x && rect.getBottom() >= pos_.y) {
                              find = true;
                           }
                        } else {
                           Rectangle rectangle = areax.getBox();
                           if (rectangle != null && rectangle.contains(pos_)) {
                              find = true;
                           }
                        }
                     }
                  }
               }
            }

            if (!find) {
               if (player.getBuffedValue(SecondaryStatFlag.HolyMagicShell) != null) {
                  Integer value = player.getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                  SecondaryStatEffect eff = player.getBuffedEffect(SecondaryStatFlag.HolyMagicShell);
                  if (value <= 0) {
                     player.temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                  } else {
                     SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                     statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, eff.getSourceId(), value - 1);
                     statManager.temporaryStatSet();
                  }
               } else if (player.getBuffedValue(SecondaryStatFlag.BlessingArmor) != null) {
                  Integer v = player.getBuffedValue(SecondaryStatFlag.BlessingArmor);
                  if (v != null) {
                     if (v <= 0) {
                        player.temporaryStatReset(SecondaryStatFlag.BlessingArmor);
                     } else {
                        SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                        statManager.changeStatValue(SecondaryStatFlag.BlessingArmor, 1210016, v - 1);
                        statManager.temporaryStatSet();
                     }
                  }
               } else if (player.getBuffedValue(SecondaryStatFlag.StormGuard) != null) {
                  player.temporaryStatReset(SecondaryStatFlag.StormGuard);
               } else if (player.getBuffedValue(SecondaryStatFlag.BlitzShield) != null) {
                  player.temporaryStatReset(SecondaryStatFlag.BlitzShield);
               } else if (player.getBuffedValue(SecondaryStatFlag.EtherealForm) == null
                  && player.getBuffedValue(SecondaryStatFlag.Asura) == null
                  && player.getIndieTemporaryStatEntries(SecondaryStatFlag.indiePartialNotDamaged).isEmpty()
                  && player.getBuffedValue(SecondaryStatFlag.NotDamaged) == null) {
                  player.addHP(-player.getStat().getCurrentMaxHp(player));
               }
            }
         }
      }

      WillEffect e = new WillEffect(-1, true, 242, 4);
      this.broadcastMessage(e.encodeForLocal());
      this.sendWillSpiderAttack(null, 0, 242, 4, Collections.emptyList());
      if (!(this.getFieldSetInstance() instanceof HellWillBoss)) {
         int[] list = new int[]{
            8880351, 8880352, 8880355, 8880356, 8880321, 8880322, 8880325, 8880326, 8880372, 8880373, 8880376, 8880377, 8880323, 8880327, 8880324, 8880328
         };

         for (int idx : list) {
            MapleMonster m = this.getMonsterById(idx);
            if (m != null) {
               m.addSkillFilter(0);
               m.addSkillFilter(1);
            }
         }
      }

      MapleMonster finalBoss = boss;
      Timer.MapTimer.getInstance()
         .schedule(
            () -> {
               if (this.successDestruction) {
                  if (finalBoss.getMobMaxHp() * 0.001 * 666.0 >= finalBoss.getHp() && this.getWillHPList().contains(666)) {
                     this.getWillHPList().clear();
                     this.getWillHPList().add(333);
                     this.getWillHPList().add(3);
                  } else if (finalBoss.getMobMaxHp() * 0.001 * 333.0 >= finalBoss.getHp() && this.getWillHPList().contains(333)) {
                     this.getWillHPList().clear();
                     this.getWillHPList().add(3);
                  } else if (finalBoss.getMobMaxHp() * 0.001 * 3.0 >= finalBoss.getHp() && this.getWillHPList().contains(3)) {
                     this.getWillHPList().clear();
                  }

                  this.sendWillDisplayHP(null);
                  this.startGroggyTime = System.currentTimeMillis() + 4000L;

                  for (MapleMonster mobx : this.getAllMonstersThreadsafe()) {
                     for (int i = 0; i < 6; i++) {
                        mobx.removeAttackBlocked(i);
                     }

                     mobx.broadcastAttackBlocked();
                  }

                  for (AffectedArea areax : this.getAllMistsThreadsafe()) {
                     if (areax.isMobMist() && areax.getMobSkill().getSkillId() == 242 && areax.getMobSkill().getSkillLevel() == 4) {
                        this.removeMapObject(areax);
                        this.broadcastMessage(CField.removeAffectedArea(areax.getObjectId(), 242, false));
                     }
                  }

                  this.successDestruction = true;
               } else {
                  int[] list = new int[]{
                     8880351,
                     8880352,
                     8880355,
                     8880356,
                     8880321,
                     8880322,
                     8880325,
                     8880326,
                     8880372,
                     8880373,
                     8880376,
                     8880377,
                     8880323,
                     8880327,
                     8880324,
                     8880328
                  };

                  for (int idxx : list) {
                     MapleMonster m = this.getMonsterById(idxx);
                     if (m != null) {
                        m.removeSkillFilter(0);
                        m.removeSkillFilter(1);
                     }
                  }
               }
            },
            3500L
         );
   }

   public void willGroggy() {
      int[] mobID;
      if (this.getFieldSetInstance() instanceof HellWillBoss) {
         mobID = new int[]{8880300, 8880340, 8880360};
      } else {
         mobID = new int[]{8880300, 8880340, 8880360, 8880301, 8880341, 8880361};
      }

      MapleMonster monster = null;

      for (int id : mobID) {
         monster = this.getMonsterById(id);
         if (monster != null) {
            break;
         }
      }

      if (monster != null) {
         this.sendWillFreeze();
         this.sendWillNotice("지금이에요. 윌이 무방비 상태일 때 피해를 입혀야 해요.", 245, 12000);
         if (monster.getId() != 8880300 && monster.getId() != 8880340 && monster.getId() != 8880360) {
            this.broadcastMessage(MobPacket.mobForcedSkillAction(monster.getObjectId(), 2, false));
         } else {
            this.nextGroggyCancelTime = System.currentTimeMillis() + 10000L;
            MapleMonster target1 = this.getMonsterById(monster.getId() + 3);
            if (target1 != null) {
               this.broadcastMessage(MobPacket.mobForcedSkillAction(target1.getObjectId(), 3, false));
            }

            MapleMonster target2 = this.getMonsterById(monster.getId() + 4);
            if (target2 != null) {
               this.broadcastMessage(MobPacket.mobForcedSkillAction(target2.getObjectId(), 3, false));
            }
         }
      }
   }

   public void takeDown() {
      if (++this.takeDownCount <= 5) {
         int mobID = 8880323;
         if (this.getId() == 450008250) {
            mobID = 8880303;
         } else if (this.level == 0) {
            mobID = 8880363;
         }

         final int startX = Randomizer.rand(-650, -480);
         List<Triple<Integer, Integer, Integer>> idt = new ArrayList<Triple<Integer, Integer, Integer>>() {
            {
               for (int i = 1; i <= 3; i++) {
                  this.add(new Triple<>((i - 1) * 3, 1500 * i, startX + 160 * (i - 1)));
                  this.add(new Triple<>((i - 1) * 3 + 1, 1500 * i, startX + 400 + 160 * (i - 1)));
                  this.add(new Triple<>((i - 1) * 3 + 2, 1500 * i, startX + 800 + 160 * (i - 1)));
               }
            }
         };
         this.sendWillSpiderAttack(null, mobID, 242, 14, idt, Randomizer.nextBoolean());
         this.setNextTakeDownTime(System.currentTimeMillis() + 6000L);
      } else {
         this.takeDownCount = 0;
         this.setNextTakeDownTime(0L);
         if (this.successDestruction) {
            MapleMonster boss = null;
            int mobID = 8880341;
            if (this.getId() == 450008250) {
               mobID = 8880301;
            } else if (this.level == 0) {
               mobID = 8880361;
            }

            for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
               if (mob.getId() == mobID) {
                  boss = mob;
                  break;
               }
            }

            if (boss != null) {
               if (boss.getMobMaxHp() * 0.001 * 500.0 >= boss.getHp() && this.getWillHPList().contains(500)) {
                  this.getWillHPList().clear();
                  this.getWillHPList().add(3);
               } else if (boss.getMobMaxHp() * 0.001 * 3.0 >= boss.getHp() && this.getWillHPList().contains(3)) {
                  this.getWillHPList().clear();
               }

               this.sendWillDisplayHP2Phase(null);
               this.startGroggyTime = System.currentTimeMillis() + 4000L;
            }
         }

         for (MapleMonster mobx : this.getAllMonstersThreadsafe()) {
            mobx.removeAttackBlocked(0);
            mobx.removeAttackBlocked(1);
            mobx.removeAttackBlocked(2);
            mobx.removeAttackBlocked(3);
            mobx.removeAttackBlocked(4);
            mobx.removeAttackBlocked(5);
            mobx.removeAttackBlocked(6);
            mobx.removeAttackBlocked(7);
            mobx.removeAttackBlocked(8);
            mobx.removeAttackBlocked(9);
            mobx.broadcastAttackBlocked();
            mobx.removeSkillFilter(0);
         }

         this.successDestruction = true;
      }
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.nextMonitorBreakTime == 0L) {
         this.nextMonitorBreakTime = System.currentTimeMillis() + 60000L;
      }

      if (this.nextMonitorBreakTime <= System.currentTimeMillis()) {
         MapleMonster mob = this.findMapPatternMob(1);
         if (mob != null) {
            MobSkillCommand command = mob.getCommand();
            command.setSkillCommand(242);
            command.setSkillCommandLevel(5);
            mob.setCommand(command);
         }

         this.beforeMonitorBreakTime = this.nextMonitorBreakTime;
         this.nextMonitorBreakTime = System.currentTimeMillis() + 120000L;
      }

      if (this.nextDestructionTime != 0L && this.nextDestructionTime <= System.currentTimeMillis()) {
         MapleMonster mob = this.findMapPatternMob(1);
         if (mob != null) {
            MobSkillCommand command = mob.getCommand();
            command.setSkillCommand(242);
            command.setSkillCommandLevel(15);
            mob.setCommand(command);
         }

         this.nextDestructionTime = 0L;
      }

      if (this.startTakeDownTime != 0L && this.startTakeDownTime <= System.currentTimeMillis()) {
         this.startTakeDownTime = 0L;
         this.takeDown();
      }

      if (this.nextStartMirrorOfLiesTime != 0L
         && this.nextStartMirrorOfLiesTime <= System.currentTimeMillis()
         && (this.getWillHPList().contains(500) && this.mirrorOfLiesCount == 1 || this.getWillHPList().contains(3) && this.mirrorOfLiesCount == 2)) {
         this.nextStartMirrorOfLiesTime = 0L;
         this.setTakeDown();
      }

      if (this.nextTakeDownTime != 0L && this.nextTakeDownTime <= System.currentTimeMillis()) {
         this.takeDown();
      }

      if (this.startGroggyTime != 0L && this.startGroggyTime <= System.currentTimeMillis()) {
         this.willGroggy();
         this.startGroggyTime = 0L;
      }

      if (this.nextGroggyCancelTime != 0L && this.nextGroggyCancelTime <= System.currentTimeMillis()) {
         int[] list = new int[]{
            8880351, 8880352, 8880355, 8880356, 8880321, 8880322, 8880325, 8880326, 8880372, 8880373, 8880376, 8880377, 8880323, 8880327, 8880324, 8880328
         };

         for (int id : list) {
            MapleMonster m = this.getMonsterById(id);
            if (m != null) {
               m.removeSkillFilter(0);
               m.removeSkillFilter(1);
            }
         }

         this.nextGroggyCancelTime = 0L;
      }

      if (this.level == 0) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player.getWillMoonGauge() < 100 && System.currentTimeMillis() >= player.getWillMoonGaugeUpdateableTime()) {
               player.setWillMoonGauge(player.getWillMoonGauge() + 10);
               this.sendWillUpdateMoonGauge(player, player.getWillMoonGauge());
            }
         }
      } else if (this.level == 1) {
         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx.getWillMoonGauge() < 100 && System.currentTimeMillis() >= playerx.getWillMoonGaugeUpdateableTime()) {
               playerx.setWillMoonGauge(playerx.getWillMoonGauge() + 5);
               this.sendWillUpdateMoonGauge(playerx, playerx.getWillMoonGauge());
            }
         }
      } else if (this.level == 2) {
         for (MapleCharacter playerxx : this.getCharactersThreadsafe()) {
            if (playerxx.getWillMoonGauge() < 100 && System.currentTimeMillis() >= playerxx.getWillMoonGaugeUpdateableTime()) {
               playerxx.setWillMoonGauge(playerxx.getWillMoonGauge() + 3);
               this.sendWillUpdateMoonGauge(playerxx, playerxx.getWillMoonGauge());
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.phase = 1;
      this.willHPList = new ArrayList<>();
      this.willHPList.add(666);
      this.willHPList.add(333);
      this.willHPList.add(3);
      this.setMonitorBreakType(0);
      this.setStartMonitorBreakTime(0L);
      this.setStartMonitorBreakTime(0L);
      this.nextMonitorBreakTime = 0L;
      this.nextDestructionTime = 0L;
      this.startGroggyTime = 0L;
      this.successDestruction = true;
      this.setMirrorOfLies = false;
      this.setTakeDown = false;
      this.nextTakeDownTime = 0L;
      this.takeDownCount = 0;
      this.setCreateWeb = false;
      this.nextCreateWebTime = 0L;
      this.mirrorOfLiesCount = 0;
      this.nextStartMirrorOfLiesTime = 0L;
      this.nextGroggyCancelTime = 0L;
      this.clearSpiderWeb();
      this.setSpawnSpiderWeb(true);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellWillBoss) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
         packet.writeInt(EliteState.EliteBoss.getType());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeMapleAsciiString("Bgm49/Diffraction");
         packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect2");
         packet.writeMapleAsciiString("Effect/EventEffect.img/gloryWmission/screenEff");
         player.send(packet.getPacket());
      }

      player.setWillMoonGauge(0);
      if (this.phase == 1) {
         this.sendWillSetMoonGauge(player, 100, 45);
      } else if (this.phase == 2) {
         this.sendWillSetMoonGauge(player, 100, 40);
      } else if (this.phase == 3) {
         this.sendWillSetMoonGauge(player, 100, 25);
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellWillBoss) {
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (mob.getId() == 8880301 || mob.getId() == 8880302 || mob.getId() == 8880303 || mob.getId() == 8880304) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      }

      if (DBConfig.isGanglim
         && (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalWillBoss || this.getFieldSetInstance() instanceof HardWillBoss)
         && (mob.getId() == 8880301 || mob.getId() == 8880302 || mob.getId() == 8880303 || mob.getId() == 8880304)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() >= 8880315 && mob.getId() <= 8880319) {
         mob.getMap().addNextRemoveMonster(mob.getObjectId(), System.currentTimeMillis() + 3000L);
      }

      this.sendWillDisplayHP(null);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
      if (this.nextDestructionTime != 0L || this.nextTakeDownTime != 0L) {
         this.successDestruction = false;
      }

      this.playerDeadTime = System.currentTimeMillis();
   }

   public int getPhase() {
      if (this.getId() == 450008150 || this.getId() == 450008750 || this.getId() == 450007850) {
         return 1;
      } else if (this.getId() == 450008250 || this.getId() == 450008850 || this.getId() == 450007950) {
         return 2;
      } else {
         return this.getId() != 450008350 && this.getId() != 450008950 && this.getId() != 450008050 ? this.phase : 3;
      }
   }

   public void setPhase(int phase) {
      this.phase = phase;
   }

   public void sendWillNotice(String message, int type, int time) {
      this.broadcastMessage(CField.sendWeatherEffectNotice(245, time, false, message));
   }

   public void sendWillFreeze() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_FREEZE.getValue());
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillUnk() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_UNK.getValue());
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillSetMoonGauge(MapleCharacter player, int max, int min) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_SET_MOONGAUGE.getValue());
      packet.writeInt(max);
      packet.writeInt(min);
      player.send(packet.getPacket());
   }

   public void sendWillUpdateMoonGauge(MapleCharacter player, int gauge) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_MOONGAUGE.getValue());
      packet.writeInt(gauge);
      player.send(packet.getPacket());
   }

   public void sendWillSetCooltimeMoonGauge(MapleCharacter player, int time) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_COOLTIME_MOONGAUGE.getValue());
      packet.writeInt(time);
      player.send(packet.getPacket());
   }

   public void sendWillCreateBeholder(WillBeholder beholder) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_CREATE_BEHOLDER.getValue());
      beholder.encode(packet);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillSpiderWeb(MapleCharacter player, SpiderWeb web, boolean create) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_SPIDER.getValue());
      packet.writeInt(create ? 3 : 4);
      web.encode(packet);
      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void sendWillThirdOne() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_THIRD_ONE.getValue());
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillCreatePoison(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_POISON.getValue());
      packet.writeInt(0);
      packet.writeInt(player.getId());
      packet.writeInt(0);
      packet.writeInt(0);
      packet.write(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillRemovePoison(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_POISON_REMOVE.getValue());
      packet.writeInt(1);
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillPoisonAttack() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_POISON_ATTACK.getValue());
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillDisplayHP(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_DISPLAY_HP.getValue());
      packet.writeInt(this.willHPList.size());

      for (int hp : this.willHPList) {
         packet.writeInt(hp);
      }

      int level = 1;
      if (this.getId() == 450008150) {
         level = 2;
      } else if (this.level == 0) {
         level = 0;
      }

      int mobTemplateID1 = level == 2 ? 8880300 : 8880340;
      int mobTemplateID2 = level == 2 ? 8880303 : 8880343;
      int mobTemplateID3 = level == 2 ? 8880304 : 8880344;
      if (level == 0) {
         mobTemplateID1 = 8880360;
         mobTemplateID2 = 8880363;
         mobTemplateID3 = 8880364;
      }

      MapleMonster life1 = this.getMonsterById(mobTemplateID1);
      MapleMonster life2 = this.getMonsterById(mobTemplateID2);
      MapleMonster life3 = this.getMonsterById(mobTemplateID3);
      packet.write(life1 != null);
      if (life1 != null) {
         packet.writeInt(life1.getId());
         Pair<Long, Long> pair = life1.getHPForDisplay();
         packet.writeLong(pair.left);
         packet.writeLong(pair.right);
      }

      packet.write(life2 != null);
      if (life2 != null) {
         packet.writeInt(life2.getId());
         Pair<Long, Long> pair = life2.getHPForDisplay();
         packet.writeLong(pair.left);
         packet.writeLong(pair.right);
      }

      packet.write(life3 != null);
      if (life3 != null) {
         packet.writeInt(life3.getId());
         Pair<Long, Long> pair = life3.getHPForDisplay();
         packet.writeLong(pair.left);
         packet.writeLong(pair.right);
      }

      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void sendWillDisplayHP2Phase(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_DISPLAY_HP2.getValue());
      packet.writeInt(this.willHPList.size());

      for (int hp : this.willHPList) {
         packet.writeInt(hp);
      }

      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void sendWillTeleport() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_TELEPORT.getValue());
      packet.writeInt(1);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWillSpiderAttack(MapleCharacter player, int id, int skillID, int level, List<Triple<Integer, Integer, Integer>> values) {
      this.sendWillSpiderAttack(player, id, skillID, level, values, false);
   }

   public void sendWillSpiderAttack(MapleCharacter player, int id, int skillID, int level, List<Triple<Integer, Integer, Integer>> values, boolean optional) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WILL_SPIDER_ATTACK.getValue());
      packet.writeInt(id);
      packet.writeInt(skillID);
      packet.writeInt(level);
      switch (level) {
         case 1:
         case 2:
         case 3:
         case 14:
            if (level == 14) {
               packet.writeInt(optional ? 2 : 1);
            }

            packet.writeInt(level == 14 ? 9 : 4);
            packet.writeInt(1200);
            packet.writeInt(level == 14 ? 5000 : 9000);
            packet.writeInt(level == 14 && optional ? -60 : -40);
            packet.writeInt(-600);
            packet.writeInt(level == 14 && optional ? 60 : 40);
            packet.writeInt(10);
            packet.writeInt(values.size());

            for (Triple<Integer, Integer, Integer> value : values) {
               packet.writeInt(value.left);
               packet.writeInt(value.mid);
               packet.writeInt(value.right);
               packet.writeInt(0);
            }
            break;
         case 4:
            packet.writeInt(values.size());
            packet.write(optional);
            break;
         case 5:
            packet.writeInt(2);
            if (values.size() == 0) {
               packet.write(0);
               packet.writeInt(-690);
               packet.writeInt(-455);
               packet.writeInt(695);
               packet.writeInt(160);
               packet.write(1);
               packet.writeInt(-690);
               packet.writeInt(-2378);
               packet.writeInt(695);
               packet.writeInt(-2019);
            } else {
               packet.write(0);
               packet.writeInt(-690);
               packet.writeInt(-2378);
               packet.writeInt(695);
               packet.writeInt(-2019);
               packet.write(1);
               packet.writeInt(-690);
               packet.writeInt(-455);
               packet.writeInt(695);
               packet.writeInt(160);
            }
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
      }

      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   protected long getStartMonitorBreakTime() {
      return this.startMonitorBreakTime;
   }

   public void setStartMonitorBreakTime(long startMonitorBreakTime) {
      this.startMonitorBreakTime = startMonitorBreakTime;
   }

   protected int getMonitorBreakType() {
      return this.monitorBreakType;
   }

   public void setMonitorBreakType(int monitorBreakType) {
      this.monitorBreakType = monitorBreakType;
   }

   public long getNextDestructionTime() {
      return this.nextDestructionTime;
   }

   public void setNextDestructionTime(long nextDestructionTime) {
      this.nextDestructionTime = nextDestructionTime;
   }

   public boolean isSetMirrorOfLies() {
      return this.setMirrorOfLies;
   }

   public void setSetMirrorOfLies(boolean setMirrorOfLies) {
      this.setMirrorOfLies = setMirrorOfLies;
   }

   public boolean isSetTakeDown() {
      return this.setTakeDown;
   }

   public void setSetTakeDown(boolean setTakeDown) {
      this.setTakeDown = setTakeDown;
   }

   public long getNextTakeDownTime() {
      return this.nextTakeDownTime;
   }

   public void setNextTakeDownTime(long nextTakeDownTime) {
      this.nextTakeDownTime = nextTakeDownTime;
   }

   public int getTakeDownCount() {
      return this.takeDownCount;
   }

   public void setTakeDownCount(int takeDownCount) {
      this.takeDownCount = takeDownCount;
   }

   public long getNextStartMirrorOfLiesTime() {
      return this.nextStartMirrorOfLiesTime;
   }

   public void setNextStartMirrorOfLiesTime(long nextStartMirrorOfLiesTime) {
      this.nextStartMirrorOfLiesTime = nextStartMirrorOfLiesTime;
   }

   public int getMirrorOfLiesCount() {
      return this.mirrorOfLiesCount;
   }

   public void setMirrorOfLiesCount(int mirrorOfLiesCount) {
      this.mirrorOfLiesCount = mirrorOfLiesCount;
   }

   public long getNextCreateWebTime() {
      return this.nextCreateWebTime;
   }

   public void setNextCreateWebTime(long nextCreateWebTime) {
      this.nextCreateWebTime = nextCreateWebTime;
   }

   public boolean isSetCreateWeb() {
      return this.setCreateWeb;
   }

   public void setSetCreateWeb(boolean setCreateWeb) {
      this.setCreateWeb = setCreateWeb;
   }

   public void setTakeDown() {
      this.sendWillUnk();
      this.sendWillNotice("거짓의 거울은 공격을 반전시켜요. 균열이 나타나면 공격을 마주하세요.", 245, 26000);
      this.setTakeDownCount(0);
      this.setStartTakeDownTime(System.currentTimeMillis() + 3000L);
      this.setNextStartMirrorOfLiesTime(System.currentTimeMillis() + 120000L);

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         mob.addAttackBlocked(0);
         mob.addAttackBlocked(1);
         mob.addAttackBlocked(2);
         mob.addAttackBlocked(3);
         mob.addAttackBlocked(4);
         mob.addAttackBlocked(5);
         mob.addAttackBlocked(6);
         mob.addAttackBlocked(7);
         mob.addAttackBlocked(8);
         mob.addAttackBlocked(9);
         mob.broadcastAttackBlocked();
         mob.addSkillFilter(0);
      }
   }

   public long getStartTakeDownTime() {
      return this.startTakeDownTime;
   }

   public void setStartTakeDownTime(long startTakeDownTime) {
      this.startTakeDownTime = startTakeDownTime;
   }

   public void setStartGroggyTime(long startGroggyTime) {
      this.startGroggyTime = startGroggyTime;
   }

   public long getNext3rdAttackTime() {
      return this.next3rdAttackTime;
   }

   public void setNext3rdAttackTime(long next3rdAttackTime) {
      this.next3rdAttackTime = next3rdAttackTime;
   }

   public int getNext3rdAttackType() {
      return this.next3rdAttackType;
   }

   public void setNext3rdAttackType(int next3rdAttackType) {
      this.next3rdAttackType = next3rdAttackType;
   }

   public boolean getSuccessDestruction() {
      return this.successDestruction;
   }
}
