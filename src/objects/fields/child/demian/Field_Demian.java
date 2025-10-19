package objects.fields.child.demian;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.MobPacket;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.StigmaInfo;
import objects.fields.StigmaObject;
import objects.fields.StigmaStackLevel;
import objects.fields.StigmaStackType;
import objects.fields.fieldset.instance.HardDemianBoss;
import objects.fields.fieldset.instance.HellDemianBoss;
import objects.fields.fieldset.instance.NormalDemianBoss;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobMoveAction;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.MobZoneInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.skills.TemporarySkill;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Field_Demian extends Field {
   private List<Integer> noticed = new ArrayList<>();
   private StigmaObject stigmaObject;
   private StigmaStackType nextStigmaStackType;
   private StigmaStackLevel nextStigmaStackLevel;
   private long stigmaStackBegin;
   private long nextStigmaObjectCreate;
   private int demianCorruptionStack;
   private int remainingSubFlyingSword = 0;
   private Map<Integer, DemianFlyingSword> flyingSwords = new HashMap<>();
   public final int MAX_USER_STIGMA = 7;
   public final int flyingSwordStartX = 895;
   public final int flyingSwordStartY = -200;
   public List<MapleMonster> mobZoneMobs = new ArrayList<>();
   public MobZoneInfo currentMobZone = null;
   public boolean clear = false;
   public int actionBarIdx;
   public int boss;
   public int phase;
   public int currentPhase = 1;
   public int shadowZone;
   public StigmaInfo stigma;
   private boolean hellmode = false;
   private long potionTime = 0L;

   public boolean isHellmode() {
      return this.hellmode;
   }

   public void setPotionTime(long potionTime) {
      this.potionTime = potionTime;
   }

   public Field_Demian(int mapid, int channel, int returnMapId, float monsterRate, MapleData node) {
      super(mapid, channel, returnMapId, monsterRate);
      if (node != null) {
         this.stigma = new StigmaInfo(node.getChildByPath("stigma"));
         MapleData info = node.getChildByPath("info");
         if (info != null) {
            this.shadowZone = MapleDataTool.getInt("shadowzone", info, 0);
            this.boss = MapleDataTool.getInt("boss", info, 0);
            this.phase = MapleDataTool.getInt("phase", info, 1);
            this.actionBarIdx = MapleDataTool.getInt("actionBarIdx", node, 0);
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.flyingSwords.clear();
      this.remainingSubFlyingSword = 0;
      this.demianCorruptionStack = 0;
      this.currentPhase = 1;
      this.clear = false;
      this.hellmode = false;
      this.potionTime = 0L;
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      long now = System.currentTimeMillis();
      if (this.getCharactersSize() != 0 && this.getAllMonstersThreadsafe().size() > 0) {
         this.onUpdateStigmaStack(now);
         this.onUpdateStigmaObject(now);
         this.onUpdateFlyingSword(now);
         this.onUpdateNextTargetFromSvr(now);
      }

      if (this.hellmode) {
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalDemianBoss) {
         NormalDemianBoss fs = (NormalDemianBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 350160280 && this.getMonsterById(8950111) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950111), new Point(910, 17));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardDemianBoss) {
         HardDemianBoss fs = (HardDemianBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 350160180 && this.getMonsterById(8950112) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950112), new Point(910, 17));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HellDemianBoss
         && this.getId() == 350160180
         && this.getMonsterById(8950112) == null
         && player.getCurrentBossPhase() > 0) {
         this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950112), new Point(910, 17));
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDemianBoss) {
         this.hellmode = true;
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
         packet.writeInt(EliteState.EliteBoss.getType());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeMapleAsciiString("Bgm45/Demian");
         packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect2");
         packet.writeMapleAsciiString("Effect/EventEffect.img/gloryWmission/screenEff");
         player.send(packet.getPacket());
      }

      if (this.stigmaObject != null) {
         player.send(makeCreateStigmaObjectPacket(this.stigmaObject, true));
      }

      if (!this.isPhase2()) {
         player.send(makeDemianCorruptionStack(false, this.demianCorruptionStack));
      } else {
         player.send(makeDemianCorruptionStack(true, 0));
      }

      this.flyingSwords.values().stream().collect(Collectors.toList()).forEach(fs -> {
         if (fs.target != 0) {
            MapleCharacter p = this.getCharacterById(fs.target);
            if (p != null) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.DEMIAN_FLYING_SWORD_MAKE_ENTER_REQUEST.getValue());
               packet.writeInt(fs.objectID);
               packet.writeInt(player.getId());
               p.send(packet.getPacket());
            }
         }
      });
      this.broadcastMessage(this.makeStigmaDurationPacket());
      StigmaStackLevel curLevel = this.getCurrentStackLevel();
      if (curLevel != null && this.nextStigmaStackType != null) {
         this.sendDemianNotice(
            this.nextStigmaStackType.weatherType,
            this.nextStigmaStackType.weatherMsg,
            -1,
            (int)(this.stigmaStackBegin + this.nextStigmaStackLevel.stackTime * 1000 - System.currentTimeMillis())
         );
      }

      MapleMonster boss = this.findBoss();
      if (boss != null) {
         boss.getRelMobZones().stream().collect(Collectors.toList()).forEach(p -> boss.sendRegisterMobZone(p.left, p.right));
         this.broadcastMobPhaseChange(boss.getObjectId(), boss.getPhase(), 0);
         this.broadcastMobZoneChange(boss.getObjectId(), boss.getMobZoneDataType());
      }

      player.send(CField.setTemporarySkill(13, new TemporarySkill[]{new TemporarySkill((byte)0, 80001974, (byte)1, 0, 0, 0)}));
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.resetStigma();
      player.send(CField.setTemporarySkill(0, new TemporarySkill[0]));
      player.temporaryStatReset(SecondaryStatFlag.MobZoneState);
      this.flyingSwords.values().stream().collect(Collectors.toList()).forEach(fs -> {
         if (fs.target == player.getId()) {
            this.removeFlyingSword(fs.objectID);
         }
      });
      this.leaveTargetFromSvr(player.getId());
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      if (this.phase == 1) {
         if (mob.getHPPercent() <= 30) {
            this.transferPhase2();
         }
      } else if (this.phase == 2) {
         int stoneCount = 0;

         for (MapleMonster Stone : this.getAllMonstersThreadsafe()) {
            if (Stone.getId() == 8880102) {
               stoneCount++;
            }
         }

         if (mob.getHPPercent() <= 25 && mob.getMobZoneDataType() == 1) {
            this.changeMobZone(2);
            mob.addOneTimeForcedAttack(3);
         } else if (mob.getHPPercent() <= 13 && mob.getMobZoneDataType() == 2) {
            this.changeMobZone(3);
            mob.addOneTimeForcedAttack(3);
            if (stoneCount == 1) {
               MapleMonster mob_ = MapleLifeFactory.getMonster(8880102);
               this.spawnMonsterOnGroundBelow(mob_, mob.getPosition());
            }
         } else if (mob.getHPPercent() <= 5 && mob.getMobZoneDataType() == 3) {
            if (stoneCount == 2) {
               MapleMonster mob_ = MapleLifeFactory.getMonster(8880102);
               this.spawnMonsterOnGroundBelow(mob_, mob.getPosition());
            }

            this.changeMobZone(4);
            mob.addOneTimeForcedAttack(3);
         }
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (this.phase == 2 && this.boss == mob.getId()) {
         MapleCharacter p = null;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               p = player;
               break;
            }
         }

         Party party = p.getParty();
         if (party == null) {
            return;
         }

         for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
            MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
            if (character != null
               && character.getDeathCount() > 0
               && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
               character.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
               character.setRegisterTransferField(mob.getMap().getId() + 40);
            }
         }
      }

      if ((mob.getId() == 8950111 || mob.getId() == 8950112) && !this.clear) {
         if (this.getFieldSetInstance() != null) {
            this.getFieldSetInstance().restartTimeOut(300000);
         }

         this.killAllMonsters(true);
         this.removeStigamObject();
         this.removeFlyingSwordAll();
         this.clearObstacleAtom();
         AtomicBoolean broadcast = new AtomicBoolean(false);
         this.getCharactersThreadsafe()
            .stream()
            .collect(Collectors.toList())
            .forEach(
               p -> {
                  p.temporaryStatReset(SecondaryStatFlag.MobZoneState);
                  p.dispelDebuff(SecondaryStatFlag.Stigma);
                  if (p.getBossMode() == 0) {
                     if (p.getMapId() == this.getId()) {
                        int quantity = 0;
                        int rand = Randomizer.nextInt(100);
                        byte var13;
                        if (rand <= 20) {
                           var13 = 1;
                        } else if (rand <= 95) {
                           var13 = 2;
                        } else {
                           var13 = 3;
                        }

                        p.gainItem(4001869, var13, false, -1L, "데미안 격파로 얻은 아이템");
                     }

                     if (p.getParty() != null && !broadcast.get()) {
                        boolean multiMode = false;
                        if (mob.getId() == 8950111) {
                           for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMember().getPartyMemberList())) {
                              StringBuilder sb = new StringBuilder("보스 노말 데미안 격파");
                              MapleCharacter playerxx = this.getCharacterById(mpc.getId());
                              if (playerxx != null) {
                                 if (!DBConfig.isGanglim && !multiMode) {
                                    multiMode = playerxx.isMultiMode();
                                 }

                                 LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                              }
                           }

                           this.bossClearQex(p, 1234569, "normal_demian_clear");
                        } else if (mob.getId() == 8950112) {
                           String list = "";
                           List<String> names = new ArrayList<>();
                           boolean check = false;

                           for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                              names.add(mpcx.getName());
                           }

                           list = String.join(",", names);

                           for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                              boolean hell = false;
                              if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDemianBoss) {
                                 MapleCharacter p_ = this.getCharacterById(mpcx.getId());
                                 if (p_ != null) {
                                    if (!DBConfig.isGanglim && !multiMode) {
                                       multiMode = p_.isMultiMode();
                                    }

                                    String keyValue = "hell_demian_point";
                                    p_.updateOneInfo(787777, keyValue, String.valueOf(p_.getOneInfoQuestInteger(787777, keyValue) + 3));
                                    if (!check) {
                                       this.bossClearQex(p, 1234569, "hell_demian_clear");
                                       check = true;
                                    }
                                 } else if (DBConfig.isGanglim) {
                                    this.updateOfflineBossLimit(mpcx.getId(), 1234569, "hell_demian_clear", "1");
                                 }

                                 hell = true;
                              }

                              StringBuilder sb = new StringBuilder("보스 " + (hell ? "헬" : "하드") + " 데미안 격파 (" + list + ")");
                              MapleCharacter playerx = this.getCharacterById(mpcx.getId());
                              if (playerx != null) {
                                 if (!DBConfig.isGanglim && !multiMode) {
                                    multiMode = playerx.isMultiMode();
                                 }

                                 LoggingManager.putLog(new BossLog(playerx, BossLogType.ClearLog.getType(), sb));
                              }
                           }

                           if (!DBConfig.isGanglim) {
                              if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDemianBoss) {
                                 Center.Broadcast.broadcastMessage(
                                    CField.chatMsg(
                                       22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [헬 데미안]을 격파하였습니다."
                                    )
                                 );
                              } else {
                                 Center.Broadcast.broadcastMessage(
                                    CField.chatMsg(
                                       22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [하드 데미안]을 격파하였습니다."
                                    )
                                 );
                              }
                           } else if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDemianBoss) {
                              Center.Broadcast.broadcastMessage(
                                 CField.chatMsg(
                                    22,
                                    "[보스격파] [CH."
                                       + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                       + "] '"
                                       + p.getParty().getLeader().getName()
                                       + "' 파티("
                                       + list
                                       + ")가 [헬 데미안]을 격파하였습니다."
                                 )
                              );
                           }

                           this.bossClearQex(p, 1234569, "hard_demian_clear");
                        }

                        if (DBConfig.isGanglim) {
                           if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDemianBoss) {
                              for (MapleCharacter partyMember : p.getPartyMembers()) {
                                 if (partyMember.getClient().getChannel() == p.getClient().getChannel()) {
                                    partyMember.updateOneInfo(
                                       1234569, "hell_demian_clear", String.valueOf(partyMember.getOneInfoQuestInteger(1234569, "hell_demian_clear") + 1)
                                    );
                                 }
                              }
                           } else {
                              this.bossClear(this.boss, 1234569, "demian_clear");
                           }
                        } else {
                           this.bossClear(this.boss, 1234569, "demian_clear");
                           if (this.getFieldSetInstance() == null
                              || this.getFieldSetInstance() != null && !(this.getFieldSetInstance() instanceof HellDemianBoss)) {
                              this.bossClear(this.boss, 1234569, "demian_clear_" + (multiMode ? "multi" : "single"));
                           }
                        }

                        this.clear = true;
                        broadcast.set(true);
                     }
                  }
               }
            );
      }
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellDemianBoss) {
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (this.findBoss() != null && this.findBoss().getId() == mob.getId()) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      }

      if (DBConfig.isGanglim
         && (
            this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardDemianBoss
               || this.getFieldSetInstance() instanceof NormalDemianBoss
         )
         && this.findBoss() != null
         && this.findBoss().getId() == mob.getId()) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (this.phase == 2) {
         MapleMonster boss = this.findBoss();
         if (boss != null && mob.getId() == 8880102) {
            boss.registerMobZone(5, mob.getObjectId());
         }
      }

      if (mob.getId() == 8880111 || mob.getId() == 8880101) {
         this.changeMobZone(1);
      }
   }

   public void changeMobZone(int zone) {
      MapleMonster boss = this.findBoss();
      if (boss != null) {
         if (zone != 0) {
            this.mobZoneMobs.add(boss);
            boss.setMobZoneDataType(zone);
            boss.setPhase(zone);
            MobZoneInfo zoneInfo = MobZoneInfo.getInfo(boss.getId());
            if (zoneInfo != null) {
               this.currentMobZone = zoneInfo;
            }

            this.broadcastMobPhaseChange(boss.getObjectId(), zone, 0);
            this.broadcastMobZoneChange(boss.getObjectId(), zone);
         } else {
            this.mobZoneMobs.stream().forEach(m -> {
               m.setPhase(6666);
               m.setMobZoneDataType(0);
               this.currentMobZone = null;
               this.broadcastMobPhaseChange(boss.getObjectId(), 6666, 0);
               this.broadcastMobZoneChange(boss.getObjectId(), 0);
            });
         }
      }
   }

   public boolean checkStigmaDecObject() {
      if (this.stigmaObject == null) {
         return false;
      } else {
         long now = System.currentTimeMillis();
         this.nextStigmaObjectCreate = now + Randomizer.rand(10, 20) * 1000;
         this.removeStigamObject();
         return true;
      }
   }

   private void createFlyingSword(int type) {
      DemianFlyingSword flyingSword = new DemianFlyingSword();
      flyingSword.objectType = type;
      flyingSword.mobTemplateID = this.boss;
      flyingSword.attackIdx = 4;
      flyingSword.startX = 895;
      flyingSword.startY = -200;
      this.flyingSwords.put(flyingSword.objectID, flyingSword);
      this.sendFlyingSwordCreate(flyingSword);
      List<DemianObjectNodeData> d = new ArrayList<>();
      d.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 0, 0, 30, 0, 0, 0, false, 0, new Point(895, -200)));
      flyingSword.lastNode = d;
      this.sendDemianFlyingSwordNode(flyingSword.objectID, 0, false, d);
   }

   private void createStigmaObject() {
      if (this.stigmaObject == null) {
         Point pos = new Point(Randomizer.rand(10, 1590), 0);
         int fh = this.getFootholds().findBelow(pos).getId();
         this.stigmaObject = new StigmaObject();
         this.stigmaObject.endTime = System.currentTimeMillis() + 10000L;
         this.stigmaObject.x = pos.x;
         this.stigmaObject.y = pos.y + 10;
         this.stigmaObject.footholdSN = fh;
         this.stigmaObject.castingTime = 2500;
         this.stigmaObject.objectUOL = "Map/Obj/BossDemian.img/demian/altar";
         this.sendCreateStigmaIncinerateObj(this.stigmaObject);
      }
   }

   private StigmaStackLevel getCurrentStackLevel() {
      MapleMonster boss = this.findBoss();
      if (boss != null) {
         List<StigmaStackLevel> stackLevel = this.stigma.stackLevel;
         StigmaStackLevel le = null;

         for (StigmaStackLevel level : stackLevel) {
            if (level != null && boss.getHPPercent() <= level.hp) {
               le = level;
            }
         }

         if (le != null) {
            return le;
         }
      }

      return null;
   }

   private void leaveTargetFromSvr(int id) {
      if (this.phase == 2) {
         this.getAllMonstersThreadsafe().stream().filter(m -> m.getId() == 8880102).collect(Collectors.toList()).forEach(m -> {
            if (m.getTargetFromSvr() == id) {
               m.setTargetFromSvr(0);
               m.setChaseEnd(0L);
            }
         });
      }
   }

   private void onUpdateNextTargetFromSvr(long now) {
      if (this.phase == 2) {
         MapleMonster boss = this.findBoss();
         if (boss != null) {
            boss.getRelMobZones().stream().collect(Collectors.toList()).forEach(z -> {
               int type = z.left;
               int zoneID = z.right;
               MapleMonster zone = this.getMonsterByOid(zoneID);
               if (zone != null && zone.getChaseEnd() <= now) {
                  MapleCharacter random = this.getRandomTargetUser();
                  if (random != null) {
                     zone.setChaseEnd(now + 30000L);
                     if (zone.getTargetFromSvr() != random.getId()) {
                        zone.setTargetFromSvr(random.getId());
                     }
                  }
               }
            });
         }
      }
   }

   private MapleCharacter getRandomTargetUser() {
      return this.getCharactersSize() <= 0 ? null : this.getCharactersThreadsafe().stream().findAny().orElse(null);
   }

   private void insertFlyingSwordArea(Point pos) {
      MapleMonster boss = this.findBoss();
      MobSkillInfo msi = MobSkillFactory.getMobSkill(131, 28);
      if (boss != null && msi != null) {
         Point pos_ = this.calcDropPos(new Point(pos.x, pos.y - 23), pos);
         Rect rect = new Rect(msi.getLt().x + pos_.x, msi.getLt().y + pos_.y, msi.getRb().x + pos_.x, msi.getRb().y + pos_.y);
         this.spawnMist(new AffectedArea(rect, boss, msi, pos_, System.currentTimeMillis() + msi.getDuration()));
      }
   }

   private boolean isPhase2() {
      return this.currentPhase == 2;
   }

   public boolean isClear() {
      return this.clear;
   }

   private MapleMonster findBoss() {
      return this.getMonsterById(this.boss);
   }

   private void removeStigamObject() {
      if (this.stigmaObject != null) {
         this.sendDeleteStigmaIncinerateObj();
         this.stigmaObject = null;
      }
   }

   private void onUpdateStigmaStack(long now) {
      if (!this.isPhase2()) {
         if (this.nextStigmaStackLevel != null && this.nextStigmaStackType != null) {
            if (this.stigmaStackBegin + this.nextStigmaStackLevel.stackTime * 1000 <= now) {
               int mobSkillID = this.nextStigmaStackLevel.mobSkillID;
               int mobSkillLev = this.nextStigmaStackLevel.mobSkillLev;
               List<MapleCharacter> list = this.getCharactersThreadsafe().stream().filter(p -> !p.isHidden()).collect(Collectors.toList());
               MapleCharacter player = null;
               if (list.size() > 0) {
                  String type = this.nextStigmaStackType.type;
                  if (type != null) {
                     if (type.equals("random")) {
                        Collections.shuffle(list);
                        player = list.stream().findAny().orElse(null);
                     } else if (type.equals("lowStack")) {
                        player = list.stream().sorted((a, b) -> a.getStigma() - b.getStigma()).findFirst().orElse(null);
                     } else if (type.equals("highStack")) {
                        player = list.stream().sorted((a, b) -> b.getStigma() - a.getStigma()).findFirst().orElse(null);
                     } else if (type.equals("aggro")) {
                     }
                  }
               }

               if (player != null) {
                  player.incStigma(1);
               }

               this.nextStigmaStackLevel = null;
               this.nextStigmaStackType = null;
               this.stigmaStackBegin = System.currentTimeMillis() + 5000L;
               return;
            }
         } else if (this.stigmaStackBegin <= now) {
            StigmaStackLevel curLevel = this.getCurrentStackLevel();
            if (curLevel != null) {
               List<StigmaStackType> random = this.stigma.stackType.stream().filter(t -> !t.type.equals("aggro")).collect(Collectors.toList());
               StigmaStackType picked = random.get(Randomizer.rand(0, random.size() - 1));
               if (picked != null) {
                  this.stigmaStackBegin = now;
                  this.nextStigmaStackType = picked;
                  this.nextStigmaStackLevel = curLevel;
                  this.sendDemianNotice(picked.weatherType, picked.weatherMsg, -1, curLevel.stackTime * 1000);
                  this.broadcastMessage(this.makeStigmaDurationPacket());
               }
            }
         }
      }
   }

   private void onUpdateStigmaObject(long now) {
      if (!this.isPhase2()) {
         if (this.findBoss() != null) {
            if (this.nextStigmaObjectCreate == 0L) {
               this.nextStigmaObjectCreate = System.currentTimeMillis() + 2000L;
            } else {
               if (this.stigmaObject == null) {
                  if (this.nextStigmaObjectCreate <= now) {
                     this.createStigmaObject();
                     return;
                  }
               } else if (this.stigmaObject.endTime <= now) {
                  this.removeStigamObject();
                  this.nextStigmaObjectCreate = System.currentTimeMillis() + Randomizer.rand(10, 20) * 1000;
               }
            }
         }
      }
   }

   private void onUpdateFlyingSword(long now) {
      if (!this.isPhase2() && this.phase != 2) {
         this.flyingSwords.values().stream().collect(Collectors.toList()).forEach(fs -> {
            if (fs.target != 0) {
               MapleCharacter player = this.getCharacterById(fs.target);
               if (player != null) {
                  return;
               }

               this.removeFlyingSword(fs.objectID);
            } else {
               this.setNewFlyingSwordTarget(fs);
            }
         });
         if (this.hasFlyingSwordType(0) == 0) {
            this.createFlyingSword(0);
            if (this.hellmode) {
               this.createFlyingSword(0);
            }
         }

         if (this.hasFlyingSwordType(1) < this.remainingSubFlyingSword) {
            for (int i = 0; i < this.remainingSubFlyingSword - this.hasFlyingSwordType(1); i++) {
               this.createFlyingSword(1);
            }
         }
      } else {
         this.removeFlyingSwordAll();
      }
   }

   public void onDemianCorruptionStackInc() {
      if (this.currentPhase == 1 && this.demianCorruptionStack < this.stigma.stackMax.stackMax) {
         this.demianCorruptionStack++;
         this.remainingSubFlyingSword++;
         this.sendDemianCorruptionStack(false, this.demianCorruptionStack);
         if (this.demianCorruptionStack >= this.stigma.stackMax.stackMax) {
            this.onDemianCorruptionStackMax();
         }
      }
   }

   private void onDemianCorruptionStackMax() {
      if (this.currentPhase == 1) {
         this.transferPhase2();
      }
   }

   public void transferPhase2() {
      MapleMonster boss = this.findBoss();
      if (boss != null) {
         this.currentPhase = 2;
         this.broadcastMessage(MobPacket.mobDirectionActionPacket(boss.getObjectId(), MobMoveAction.DirectionAct1));
      }

      this.removeStigamObject();
      this.removeFlyingSwordAll();
      this.clearObstacleAtom();
      this.sendDemianCorruptionStack(true, 0);
      this.sendDemianNotice(216, "데미안이 완전한 어둠의 힘을 손에 넣었습니다.", -1, 31250);
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            Field_Demian.this.getCharactersThreadsafe().stream().collect(Collectors.toList()).forEach(p -> {
               if (p.getMapId() == Field_Demian.this.getId()) {
                  p.setCurrentBossPhase(2);
                  p.warp(Field_Demian.this.getId() + 20);
               }
            });
         }
      }, 6150L);
   }

   public void onStigmaStackChanged(MapleCharacter player, int userStack) {
      if (this.phase == 1 && userStack >= 7) {
         player.send(CField.makeEffectScreen("Effect/OnUserEff.img/demian/screen"));
         this.sendDemianNotice(216, "낙인이 완성되어 데미안이 점점 어둠의 힘에 물들어 갑니다.", -1, 10000);
         player.resetStigma();
         this.onDemianCorruptionStackInc();
         player.addHP(-999999L);
      }

      if (this.phase == 2 && userStack >= 7) {
         player.send(CField.makeEffectScreen("Effect/OnUserEff.img/demian/screen"));
         player.addHP(-999999L);
      }
   }

   private void clearObstacleAtom() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.OBSTACLE_ATOM_CLEAR.getValue());
      this.broadcastMessage(packet.getPacket());
   }

   private int hasFlyingSwordType(int type) {
      return (int)this.flyingSwords.values().stream().filter(f -> f.objectType == type).count();
   }

   private void removeFlyingSword(int swordID) {
      this.flyingSwords.remove(swordID);
      this.sendFlyingSwordRemove(swordID);
   }

   private void removeFlyingSwordAll() {
      this.flyingSwords.keySet().stream().collect(Collectors.toList()).forEach(fs -> this.removeFlyingSword(fs));
   }

   public void sendDemianNotice(int weather, String msg, int type, int time) {
      if (type >= 0) {
         if (this.noticed.contains(type)) {
            return;
         }

         this.noticed.add(type);
      }

      this.broadcastMessage(CField.sendWeatherEffectNotice(weather, time, false, msg));
   }

   public void sendDeleteStigmaIncinerateObj() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_RANDOM_STIGMA_INCINERATE.getValue());
      packet.writeInt(1);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendFlyingSwordRemove(int objectID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEMIAN_FLYING_SWORD_CREATE.getValue());
      packet.write(0);
      packet.writeInt(objectID);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendFlyingSwordCreate(DemianFlyingSword flyingSword) {
      this.broadcastMessage(makeFlyingSwordCreate(flyingSword));
   }

   public void sendDemianFlyingSwordNode(int objectID, int targetID, boolean forceStop, List<DemianObjectNodeData> datas) {
      this.broadcastMessage(makeDemianFlyingSwordNode(objectID, targetID, forceStop, datas));
   }

   public void sendCreateStigmaIncinerateObj(StigmaObject object) {
      this.broadcastMessage(makeCreateStigmaObjectPacket(object, false));
   }

   private void setNewFlyingSwordTarget(DemianFlyingSword flyingSword) {
      MapleCharacter player = this.getRandomTargetUser();
      if (player != null) {
         flyingSword.target = player.getId();
         this.sendDemianFlyingSwordTarget(flyingSword.objectID, flyingSword.target);
         this.sendRandomDemianObjectNode(flyingSword, player.getId());
      }
   }

   public void sendRandomDemianObjectNode(DemianFlyingSword flyingSword, int targetID) {
      int random = Randomizer.rand(1, DemianObjectNodeData.datas.size());
      List<DemianObjectNodeData> data = DemianObjectNodeData.datas.get(random);
      flyingSword.lastNode = data;
      this.sendDemianFlyingSwordNode(flyingSword.objectID, targetID, false, data);
   }

   public void sendDemianFlyingSwordTarget(int objectID, int targetID) {
      this.broadcastMessage(makeDemianFlyingSwordTarget(objectID, targetID));
   }

   public void sendDemianCorruptionStack(boolean max, int stack) {
      this.broadcastMessage(makeDemianCorruptionStack(max, stack));
   }

   private static byte[] makeDemianFlyingSwordTarget(int objectID, int targetID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEMIAN_FLYING_SWORD_TARGET.getValue());
      packet.writeInt(objectID);
      packet.writeInt(targetID);
      return packet.getPacket();
   }

   public static byte[] makeFlyingSwordCreate(DemianFlyingSword flyingSword) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEMIAN_FLYING_SWORD_CREATE.getValue());
      packet.write(1);
      packet.writeInt(flyingSword.objectID);
      flyingSword.encode(packet);
      return packet.getPacket();
   }

   private static byte[] makeDemianFlyingSwordNode(int objectID, int targetID, boolean forceStop, List<DemianObjectNodeData> datas) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEMIAN_FLYING_SWORD_NODE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(targetID);
      packet.write(forceStop);
      packet.writeInt(datas.size());
      datas.forEach(d -> d.encode(packet));
      return packet.getPacket();
   }

   private static byte[] makeCreateStigmaObjectPacket(StigmaObject object, boolean immediatelyStart) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_RANDOM_STIGMA_INCINERATE.getValue());
      packet.writeInt(0);
      object.encode(packet, immediatelyStart);
      return packet.getPacket();
   }

   private static byte[] makeDemianCorruptionStack(boolean max, int stack) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEMIAN_CORRUPTION.getValue());
      packet.write(max);
      packet.writeInt(stack);
      return packet.getPacket();
   }

   private byte[] makeStigmaDurationPacket() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.STIGMA_STACK_DURATION.getValue());
      long time = this.stigmaStackBegin;
      long now = System.currentTimeMillis();
      if (time != 0L && this.nextStigmaStackLevel != null) {
         packet.writeInt(this.stigmaStackBegin + this.nextStigmaStackLevel.stackTime * 1000 - now);
      } else {
         packet.writeInt(0);
      }

      return packet.getPacket();
   }

   public void onFlyingSwordMakeEnterAck(PacketDecoder packet) {
      int swordID = packet.readInt();
      if (packet.readByte() > 0) {
         MapleCharacter player = this.getCharacterById(packet.readInt());
         if (player != null) {
            DemianFlyingSword sword = this.flyingSwords.get(swordID);
            if (sword != null) {
               int pathIndex = packet.readShort();
               int nodeIndex = packet.readShort();
               int posX = packet.readInt();
               int posY = packet.readInt();
               List<DemianObjectNodeData> datas = sword.lastNode;
               PacketEncoder p = new PacketEncoder();
               p.writeShort(SendPacketOpcode.DEMIAN_FLYING_SWORD_MAKE_ENTER_INFO.getValue());
               p.writeInt(swordID);
               p.write(sword.objectType);
               p.writeInt(sword.target);
               p.write(sword.attackIdx);
               p.writeInt(sword.mobTemplateID);
               p.writeShort(pathIndex);
               p.writeShort(nodeIndex);
               p.writeInt(posX);
               p.writeInt(posY);
               p.writeInt(datas.size());
               datas.forEach(d -> d.encode(p));
               player.send(p.getPacket());
            }
         }
      }
   }

   public void onDemianNodeEnd(MapleCharacter player, PacketDecoder packet) {
      final int flyingSwordID = packet.readInt();
      int pathIndex = packet.readShort();
      int nodeIndex = packet.readShort();
      new Point(packet.readInt(), packet.readInt());
      Point userPos = new Point(packet.readInt(), packet.readInt());
      DemianFlyingSword flyingSword = this.flyingSwords.get(flyingSwordID);
      if (flyingSword != null) {
         DemianObjectNodeData data = DemianObjectNodeData.get(pathIndex, nodeIndex);
         if (data != null && DemianObjectNodeData.datas.get(pathIndex).size() - 3 == nodeIndex) {
            List<DemianObjectNodeData> localPaths = new ArrayList<>();
            localPaths.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 8, 0, 60, 500, 0, 0, false, 0, userPos));
            localPaths.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 8, 1, 35, 0, 11000, 0, true, 0, userPos));
            flyingSword.lastNode = localPaths;
            this.sendDemianFlyingSwordNode(flyingSwordID, player.getId(), true, localPaths);
            this.insertFlyingSwordArea(userPos);
            if (flyingSword.objectType == 1) {
               this.remainingSubFlyingSword--;
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     Field_Demian.this.removeFlyingSword(flyingSwordID);
                  }
               }, 5000L);
            } else {
               this.sendDemianFlyingSwordNode(flyingSwordID, player.getId(), true, localPaths);
               this.insertFlyingSwordArea(userPos);
               this.setNewFlyingSwordTarget(flyingSword);
            }
         }
      }
   }
}
