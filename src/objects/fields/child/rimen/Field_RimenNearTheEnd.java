package objects.fields.child.rimen;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.context.party.PartyMemberEntry;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.MapleFoothold;
import objects.fields.fieldset.instance.HardDunkelBoss;
import objects.fields.fieldset.instance.HellDunkelBoss;
import objects.fields.fieldset.instance.NormalDunkelBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class Field_RimenNearTheEnd extends Field {
   private long nextCreateObstacleTime = 0L;
   private long nextSpawnMonsterTime = 0L;
   private boolean hellmode = false;

   public boolean isHellmode() {
      return this.hellmode;
   }

   public Field_RimenNearTheEnd(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.nextCreateObstacleTime == 0L) {
         this.nextCreateObstacleTime = System.currentTimeMillis() + 4000L;
      }

      if (System.currentTimeMillis() >= this.nextCreateObstacleTime) {
         this.createObstacleAtoms();
         this.nextCreateObstacleTime = System.currentTimeMillis() + 4000L;
      }

      if (this.nextSpawnMonsterTime == 0L) {
         this.nextSpawnMonsterTime = System.currentTimeMillis() + 35000L;
      }

      if (System.currentTimeMillis() >= this.nextSpawnMonsterTime) {
         this.spawnMonster();
         this.nextSpawnMonsterTime = System.currentTimeMillis() + Randomizer.rand(35, 65) * 1000;
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.nextCreateObstacleTime = 0L;
      this.nextSpawnMonsterTime = 0L;
      this.hellmode = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalDunkelBoss) {
         NormalDunkelBoss fs = (NormalDunkelBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450012250 && this.getMonsterById(8950118) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950118), new Point(88, 29));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardDunkelBoss) {
         HardDunkelBoss fs = (HardDunkelBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450012650 && this.getMonsterById(8950119) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950119), new Point(88, 29));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HellDunkelBoss
         && this.getId() == 450012650
         && this.getMonsterById(8950119) == null
         && player.getCurrentBossPhase() > 0) {
         this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950119), new Point(88, 29));
         this.clearCurrentPhase(player.getParty());
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDunkelBoss) {
         this.hellmode = true;
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
         packet.writeInt(EliteState.EliteBoss.getType());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeMapleAsciiString("Bgm50/SubterminalPoint");
         packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect2");
         packet.writeMapleAsciiString("Effect/EventEffect.img/gloryWmission/screenEff");
         player.send(packet.getPacket());
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDunkelBoss) {
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (mob.getId() == 8645066 || mob.getId() == 8645009 || mob.getId() == 8950118 || mob.getId() == 8950119) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      }

      if (DBConfig.isGanglim
         && (
            this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalDunkelBoss
               || this.getFieldSetInstance() instanceof HardDunkelBoss
         )
         && (mob.getId() == 8645066 || mob.getId() == 8645009 || mob.getId() == 8950118 || mob.getId() == 8950119)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8645066 || mob.getId() == 8645009) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               if (this.getId() == 450012210) {
                  player.setRegisterTransferField(this.getId() + 40);
               } else {
                  player.setRegisterTransferField(this.getId() + 50);
               }

               player.mobKilled(mob.getId(), 1, 0);
               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
            }
         }
      }

      if (mob.getId() == 8950118 || mob.getId() == 8950119) {
         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            this.removeMonster(m, 1);
         }

         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx.getBossMode() == 1) {
               return;
            }
         }

         boolean set = false;

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               if (p.getMapId() == this.getId()) {
                  int quantity = 14;
                  if (this.getId() == 450012650) {
                     quantity = 16;
                  }

                  p.gainItem(4001893, (short)quantity, false, -1L, "듄켈 격파로 얻은 아이템");
               }

               if (!set) {
                  if (this.getId() == 450012650) {
                     this.bossClearQex(p, 1234569, "hard_dunkel_clear");
                     String list = "";
                     List<String> names = new ArrayList<>();
                     boolean check = false;

                     for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                        names.add(mpc.getName());
                     }

                     list = String.join(",", names);

                     for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                        boolean hell = false;
                        if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDunkelBoss) {
                           MapleCharacter p_ = this.getCharacterById(mpc.getId());
                           if (p_ != null) {
                              String keyValue = "hell_dunkel_point";
                              p_.updateOneInfo(787777, keyValue, String.valueOf(p_.getOneInfoQuestInteger(787777, keyValue) + 3));
                              if (!check) {
                                 this.bossClearQex(p, 1234569, "hell_dunkel_clear");
                                 check = true;
                              }
                           } else if (DBConfig.isGanglim) {
                              this.updateOfflineBossLimit(mpc.getId(), 1234569, "hell_dunkel_clear", "1");
                           }

                           hell = true;
                        }

                        StringBuilder sb = new StringBuilder("보스 " + (hell ? "헬" : "하드") + " 듄켈 격파 (" + list + ")");
                        MapleCharacter playerxx = this.getCharacterById(mpc.getId());
                        if (playerxx != null) {
                           LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                        }
                     }

                     if (!DBConfig.isGanglim) {
                        Center.Broadcast.broadcastMessage(
                           CField.chatMsg(
                              DBConfig.isGanglim ? 8 : 22,
                              "[보스격파] [CH."
                                 + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                 + "] '"
                                 + p.getParty().getLeader().getName()
                                 + "' 파티("
                                 + list
                                 + ")가 [하드 듄켈]을 격파하였습니다."
                           )
                        );
                     } else if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDunkelBoss) {
                        Center.Broadcast.broadcastMessage(
                           CField.chatMsg(
                              22,
                              "[보스격파] [CH."
                                 + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                 + "] '"
                                 + p.getParty().getLeader().getName()
                                 + "' 파티("
                                 + list
                                 + ")가 [헬 듄켈]을 격파하였습니다."
                           )
                        );
                     }
                  } else {
                     for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMember().getPartyMemberList())) {
                        StringBuilder sb = new StringBuilder("보스 노말 듄켈 격파");
                        MapleCharacter playerxx = this.getCharacterById(mpc.getId());
                        if (playerxx != null) {
                           LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                        }
                     }

                     this.bossClearQex(p, 1234569, "normal_dunkel_clear");
                  }

                  if (DBConfig.isGanglim) {
                     if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDunkelBoss) {
                        for (MapleCharacter partyMember : p.getPartyMembers()) {
                           if (partyMember.getClient().getChannel() == p.getClient().getChannel()) {
                              partyMember.updateOneInfo(
                                 1234569, "hell_dunkel_clear", String.valueOf(partyMember.getOneInfoQuestInteger(1234569, "hell_dunkel_clear") + 1)
                              );
                           }
                        }
                     } else {
                        this.bossClear(8645009, 1234589, "dunkel_clear");
                     }
                  }

                  if (!DBConfig.isGanglim) {
                     this.bossClear(8645009, 1234589, "dunkel_clear");
                     boolean single = this.getCharactersSize() == 1;
                     this.bossClear(8645009, 1234589, "dunkel_clear_" + (single ? "single" : "multi"));
                  }

                  set = true;
               }
            }
         }
      }
   }

   public MapleMonster getBoss() {
      int[] mobs = new int[]{8645066, 8645009};

      for (int id : mobs) {
         MapleMonster mob = this.getMonsterById(id);
         if (mob != null) {
            return mob;
         }
      }

      return null;
   }

   public void spawnMonster() {
      if (this.getBoss() != null) {
         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            if (m.getId() == 8645003) {
               this.removeMonster(m, 1);
            }
         }

         int spawncount = 5;
         if (this.hellmode) {
            spawncount = 20;
         }

         for (int i = 0; i < spawncount; i++) {
            int x = Randomizer.rand(-700, 700);
            int y = 29;
            MapleMonster mob = MapleLifeFactory.getMonster(8645003);
            if (mob != null) {
               MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
               e.setCancelTask(5000L);
               this.spawnMonsterOnGroundBelow(mob, new Point(x, y));
               mob.applyStatus(e);
            }
         }
      }
   }

   public void spawnEliteBoss() {
      DunkelEliteBoss eliteBoss = new DunkelEliteBoss();
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         MapleCharacter target = this.getCharactersThreadsafe().stream().findAny().orElse(null);
         boolean normal = this.getId() == 450012210;
         int size = 1;
         if (!normal) {
            if (boss.getHPPercent() >= 50) {
               size = 2;
            } else if (boss.getHPPercent() < 50) {
               size = 3;
            }
         } else if (boss.getHPPercent() >= 33 && boss.getHPPercent() < 66) {
            size = 2;
         } else if (boss.getHPPercent() < 33) {
            size = 3;
         }

         if (this.hellmode) {
            size = 3;
         }

         if (target != null) {
            eliteBoss.createEntry(size, boss.getObjectId(), target);
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.WILL_SPIDER_ATTACK.getValue());
            eliteBoss.encode(packet);
            this.broadcastMessage(packet.getPacket());
         }
      }
   }

   public void createMeteorite(int size) {
      if (this.getBoss() != null) {
         int m = 300;
         int m2 = 1;
         boolean leftToRight = true;
         int startX = Randomizer.rand(-1600, -1500);
         if (Randomizer.isSuccess(50)) {
            leftToRight = false;
            m = -300;
            m2 = -1;
            startX = Randomizer.rand(1350, 1450);
         }

         boolean normal = this.getId() == 450012210;
         if (this.hellmode) {
            size *= 2;
         }

         for (int i = 0; i < size; i++) {
            int rand = Randomizer.rand(0, 50) * m2;
            ObstacleAtom atom = new ObstacleAtom(
               81 + (leftToRight ? 1 : 0), new Point(startX + i * m + rand, -796), new Point(startX + i * m + rand + 825 * m2, 29), i * 450
            );
            atom.setKey(Randomizer.nextInt());
            atom.setvPerSec(5);
            atom.setHeight(1000);
            atom.setTrueDamR(normal ? 30 : (this.hellmode ? 100 : 40));
            atom.setMaxP(2);
            this.broadcastMessage(CField.createSingleObstacle(ObstacleAtomCreateType.DIAGONAL, null, null, atom));
         }
      }
   }

   public void createObstacleAtoms() {
      if (this.getBoss() != null) {
         int xLeft = this.getLeft();
         int yTop = this.getTop();
         Set<ObstacleAtom> obstacleAtomInfosSet = new HashSet<>();
         int amount = Randomizer.rand(1, 5);
         if (this.hellmode) {
            amount *= 5;
         }

         for (int i = 0; i < amount; i++) {
            int randomX = Randomizer.nextInt(this.getWidth()) + xLeft;
            Point position = new Point(randomX, -615);
            MapleFoothold fh = this.getFootholds().findBelow(position);
            if (fh != null) {
               Point pos = this.calcPointBelow(position);
               int footholdY = pos.y;
               int height = position.y - footholdY;
               height = height < 0 ? -height : height;
               ObstacleAtom atom = new ObstacleAtom(Randomizer.rand(72, 74), position, new Point(randomX, 29), 0);
               atom.setKey(Randomizer.nextInt());
               atom.setHitBoxRange(36);
               atom.setTrueDamR(10);
               atom.setHeight(0);
               atom.setMaxP(1);
               atom.setLength(height);
               atom.setAngle(0);
               atom.setvPerSec(500);
               atom.setCreateDelay(550 + i * Randomizer.rand(100, 800));
               switch (atom.getAtomType()) {
                  case 72:
                     atom.setDiseaseSkillID(123);
                     atom.setDiseaseSkillLevel(54);
                     break;
                  case 73:
                     atom.setDiseaseSkillID(121);
                     atom.setDiseaseSkillLevel(27);
                     break;
                  case 74:
                     atom.setDiseaseSkillID(120);
                     atom.setDiseaseSkillLevel(38);
               }

               obstacleAtomInfosSet.add(atom);
               this.addObstacleAtom(atom);
            }
         }

         this.broadcastMessage(CField.createObstacle(ObstacleAtomCreateType.NORMAL, null, null, obstacleAtomInfosSet));
      }
   }
}
