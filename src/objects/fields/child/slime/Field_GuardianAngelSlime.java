package objects.fields.child.slime;

import constants.ServerConstants;
import database.DBConfig;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.MapleDynamicFoothold;
import objects.fields.fieldset.instance.NormalGuardianSlimeBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Triple;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Field_GuardianAngelSlime extends Field {
   public final int CHAOS_SLIME = 8880700;
   public final int NORMAL_SLIME = 8880711;
   public boolean clear = false;
   public int gaugeUpdateIndex = 0;
   public MapleMonster boss;
   public int magmaObjectID = 0;
   public MapleDynamicFoothold mapFHList;
   public Map<Integer, GuardianAngelSlime.MagmaSlimeInit.MagmaSlime> slimeMap = new HashMap<>();
   public Map<Integer, Integer> slimeTowerMap = new HashMap<>();
   public Map<Integer, Point> slimePointList = new HashMap<>();
   public boolean init = false;
   public boolean guardianWave = false;
   public boolean success = false;
   public long lastSpawnMonsterTime = System.currentTimeMillis();
   public long endGuardianWaveTime = System.currentTimeMillis();
   public long lastGuardianWaveTime = System.currentTimeMillis();
   public long lastGuardianWaveStartTime = System.currentTimeMillis();
   public long lastLaserTime = System.currentTimeMillis();
   public Map<MapleCharacter, Integer> gaugeList = new HashMap<>();

   public Field_GuardianAngelSlime(int mapID, int channel, int returnMapId, float monsterRate, MapleData mapData) {
      super(mapID, channel, returnMapId, monsterRate);
      this.mapFHList = this.loadFootHold(mapData);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
      this.clear = false;
      this.gaugeUpdateIndex = 0;
      this.boss = null;
      this.magmaObjectID = 0;
      this.slimeMap.clear();
      this.slimeTowerMap.clear();
      this.slimePointList.clear();
      this.init = false;
      this.guardianWave = false;
      this.success = false;
      this.lastSpawnMonsterTime = System.currentTimeMillis();
      this.endGuardianWaveTime = System.currentTimeMillis();
      this.lastGuardianWaveTime = System.currentTimeMillis();
      this.lastGuardianWaveStartTime = System.currentTimeMillis();
      this.lastLaserTime = System.currentTimeMillis();
      this.gaugeList.clear();
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.gaugeList.put(player, 0);
      if (this.boss != null) {
         this.init(this.boss.getId() == 8880700);
      }

      player.send(CField.UIPacket.endInGameDirectionMode(1));
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (DBConfig.isGanglim
         && this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof NormalGuardianSlimeBoss
         && (mob.getId() == 8880700 || mob.getId() == 8880711)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() == 8880700 || mob.getId() == 8880711) {
         this.boss = mob;
         this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob.getId() + 2), mob.getTruePosition());
         this.init(mob.getId() == 8880700);
         this.broadcastMessage(CField.addPopupSay(0, 3000, "왕관의 수호로 제대로 된 피해를 입힐 수 없겠군. 마스코트 슬라임에게 도움을 받아 마그마 슬라임을 날려버리자.", ""));
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      if (mob.getId() == 8880700 || mob.getId() == 8880711) {
         this.clear = true;

         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            this.removeMonster(m, 1);
         }

         AtomicBoolean broadcast = new AtomicBoolean(false);
         this.getCharactersThreadsafe().stream().collect(Collectors.toList()).forEach(p -> {
            if (p.getBossMode() == 0) {
               if (mob.getId() == 8880700) {
                  p.setRegisterTransferField(160050000);
               } else {
                  p.setRegisterTransferField(160060000);
               }

               p.setRegisterTransferFieldTime(System.currentTimeMillis() + 5000L);
            } else {
               p.send(CField.getClock(60));
               p.setRegisterTransferField(ServerConstants.TownMap);
               p.setRegisterTransferFieldTime(System.currentTimeMillis() + 60000L);
            }

            if (p.getBossMode() == 0 && p.getDeathCount() > 0 && p.getParty() != null) {
               if (!broadcast.get()) {
                  if (mob.getId() == 8880700) {
                     this.writeLog(p, true);
                  } else {
                     this.writeLog(p, false);
                  }
               }

               this.bossClear(mob.getId(), 1234569, "guardian_angel_slime_clear");
               if (!broadcast.get()) {
                  boolean multiMode = false;
                  if (!DBConfig.isGanglim) {
                     for (MapleCharacter mapChar : this.getCharacters()) {
                        if (multiMode) {
                           break;
                        }

                        multiMode = mapChar.isMultiMode();
                     }

                     this.bossClear(mob.getId(), 1234569, "guardian_angel_slime_clear_" + (multiMode ? "multi" : "single"));
                  }
               }

               broadcast.set(true);
            }
         });
      }
   }

   private void writeLog(MapleCharacter p, boolean isChaos) {
      String bossName = isChaos ? "카오스 가디언 엔젤 슬라임" : "노말 가디언 엔젤 슬라임";
      String list = "";
      List<String> names = new ArrayList<>();

      for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
         names.add(mpc.getName());
         StringBuilder sb = new StringBuilder();
         sb.append("보스 ");
         sb.append(bossName);
         sb.append(" 격파");
         MapleCharacter player = this.getCharacterById(mpc.getId());
         if (player != null) {
            LoggingManager.putLog(new BossLog(player, BossLogType.ClearLog.getType(), sb));
         }
      }

      list = String.join(",", names);
      if (isChaos) {
         Center.Broadcast.broadcastMessageCheckQuest(
            CField.chatMsg(
               DBConfig.isGanglim ? 8 : 22,
               "[보스격파] [CH."
                  + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                  + "] '"
                  + p.getParty().getLeader().getName()
                  + "' 파티("
                  + list
                  + ")가 ["
                  + bossName
                  + "] 을 격파하였습니다."
            ),
            "BossMessage"
         );
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.init) {
         if (this.clear) {
            return;
         }

         if (!this.guardianWave) {
            if (System.currentTimeMillis() - this.lastLaserTime >= 20000L && this.boss != null) {
               this.lastLaserTime = System.currentTimeMillis();
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
               packet.writeInt(100023);
               packet.writeInt(2);
               packet.writeInt(this.boss.getId() + 2);
               packet.writeInt(1);
               packet.writeInt(3000);
               packet.writeInt(60);
               packet.writeInt(240);
               packet.writeInt(1440);
               packet.writeInt(210);
               Point[] gatePosList = new Point[]{new Point(36, -1649), new Point(36, -1102), new Point(1375, -1649), new Point(1375, -1102)};
               int size = 1;
               if (this.boss.getHPPercent() <= 66) {
                  size = 2;
               }

               boolean isLeft = Randomizer.nextBoolean();
               List<Pair<Point, Point>> list = new ArrayList<>();
               MapleCharacter player = null;
               if (this.getCharactersSize() == 1) {
                  player = this.getCharactersThreadsafe().get(0);
               } else if (this.getCharactersSize() > 1) {
                  player = this.getCharactersThreadsafe().get(Randomizer.rand(0, this.getCharactersSize() - 1));
               }

               if (player != null) {
                  for (int i = 0; i < size; i++) {
                     int x1 = 0;
                     int y1 = 0;
                     int side = i + (isLeft ? 2 : 0);
                     if (size == 1) {
                        side = Randomizer.rand(0, 1) + (isLeft ? 2 : 0);
                     }

                     Point pick = gatePosList[side];
                     x1 = pick.x;
                     y1 = pick.y;
                     int x2 = player.getPosition().x;
                     int y2 = player.getPosition().y;
                     int x3 = 1442;
                     if (isLeft) {
                        x3 = -23;
                     }

                     double f = (double)(x3 - x2) / (x2 - x1);
                     int y3 = (int)(y2 + (y2 - y1) * f);
                     list.add(new Pair<>(new Point(x1, y1), new Point(x3, y3)));
                  }
               }

               packet.writeInt(list.size());
               list.stream().forEach(p -> {
                  packet.writeShort(p.left.x);
                  packet.writeShort(p.left.y);
                  packet.writeShort(p.right.x);
                  packet.writeShort(p.right.y);
                  packet.writeShort(3000);
               });
               this.broadcastMessage(packet.getPacket());
            }

            if (System.currentTimeMillis() - this.lastGuardianWaveTime >= 160000L) {
               this.lastGuardianWaveTime = System.currentTimeMillis();
               this.initGuardianWave();
            } else {
               if (this.gaugeUpdateIndex % 5 == 0) {
                  this.gaugeUpdateIndex = 1;
                  this.updateGauge();
               } else {
                  this.gaugeUpdateIndex++;
               }

               if (System.currentTimeMillis() - this.lastSpawnMonsterTime >= 10000L) {
                  this.lastSpawnMonsterTime = System.currentTimeMillis();
                  this.initMonster(this.boss.getId() == 8880700 ? 8880702 : 8880713);
               }
            }
         } else {
            if (Math.abs(System.currentTimeMillis() - this.lastGuardianWaveStartTime) < 1000L) {
               this.lastGuardianWaveStartTime = 0L;
               this.spawnGuardianWave();
            }

            if (Math.abs(System.currentTimeMillis() - this.endGuardianWaveTime) < 1000L || System.currentTimeMillis() - this.lastGuardianWaveTime >= 45000L) {
               this.endGuardianWaveTime = 0L;
               this.lastGuardianWaveTime = System.currentTimeMillis();
               this.endGuardianWave();
            }
         }
      }
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }

   public void initMonster(int mobID) {
      GuardianAngelSlime.MagmaSlimeInit slime = new GuardianAngelSlime.MagmaSlimeInit(1);
      Point[] posList = new Point[]{
         new Point((Randomizer.nextBoolean() ? Randomizer.rand(3, 4) : Randomizer.rand(12, 18)) * 100, -1638),
         new Point(Randomizer.rand(6, 10) * 100, -1937),
         new Point(Randomizer.rand(4, 11) * 100, -1382),
         new Point(Randomizer.rand(4, 11) * 100, -1089)
      };
      int[] FHList = new int[]{1, 4, 5, 8};
      List<GuardianAngelSlime.MagmaSlimeInit.MagmaSlime> slimeList = new ArrayList<>();
      int i = 0;

      for (Point pos : posList) {
         boolean check = true;

         for (Entry<Integer, Point> posData : this.slimePointList.entrySet()) {
            if (posData.getValue() == pos) {
               check = false;
               break;
            }
         }

         if (check) {
            GuardianAngelSlime.MagmaSlimeInit.MagmaSlime slimeGet = new GuardianAngelSlime.MagmaSlimeInit.MagmaSlime(
               ++this.magmaObjectID,
               mobID,
               pos,
               new Rectangle(pos.x - Randomizer.rand(3, 9) * 10, pos.y - 180, 180, 180),
               new Rectangle(pos.x - Randomizer.rand(5, 7) * 10, pos.y - 90, 140, 90),
               new Rectangle(-70, -90, 140, 90),
               new Rectangle(pos.x - Randomizer.rand(4, 8) * 10, pos.y - 80, 80, 80),
               new Rectangle(-40, -80, 80, 80)
            );
            slimeGet.FH = FHList[i];
            i++;
            slimeList.add(slimeGet);
            this.slimeMap.put(slimeGet.objectID, slimeGet);
            this.slimePointList.put(slimeGet.objectID, pos);
         }
      }

      slime.slimeList = slimeList;
      this.broadcastMessage(this.encode(slime));
   }

   public int findMagmaSlime(Point pos) {
      int value = Integer.MAX_VALUE;
      int objectID = 0;

      for (Entry<Integer, GuardianAngelSlime.MagmaSlimeInit.MagmaSlime> slime : this.slimeMap.entrySet()) {
         if (slime.getValue().pos.y == pos.y) {
            int v = Math.abs(pos.x - slime.getValue().pos.x);
            if (value > v) {
               objectID = slime.getKey();
               value = v;
            }
         }
      }

      return objectID;
   }

   public void init(boolean isChaos) {
      this.init = true;
      if (isChaos) {
         this.broadcastMessage(CWvsContext.showNewEffect("GuardianSlime_Chaos_1", "GuardianSlime_Chaos_", true));
      } else {
         this.broadcastMessage(CWvsContext.showNewEffect("GuardianSlime_Normal_1", "GuardianSlime_Normal_", true));
      }

      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitState(1)));
      this.initFootHold();
      this.initAttack();
      this.initPortal();
      this.broadcastMessage(CField.updatePortalScript("base", false, 1150));
   }

   public void endGuardianWave() {
      this.boss.cancelStatus(MobTemporaryStatFlag.CASTING);
      if (this.success) {
         this.broadcastMessage(MobPacket.guardianAngelSlimeAction(this.boss, List.of(new Triple<>(68, 36, 0), new Triple<>(36, 37, 0))));
         this.showShieldEffect();
      }

      this.guardianWave = false;
      this.success = false;
      this.initFootHold();
      GuardianAngelSlime.InitGuardianWave encode = new GuardianAngelSlime.InitGuardianWave(1);
      byte[] packet = this.encode(encode);
      this.broadcastMessage(packet);
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitCrystal(1, 7, 0)));
      this.broadcastMessage(CField.updatePortalScript("base", false, 1150));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitState(2)));
      this.slimeTowerMap.clear();
   }

   public void successGuardianWave() {
      List<Pair<String, Boolean>> getFHList = new ArrayList<>();
      this.mapFHList.getDynamicFootholds().forEach(FH -> {
         if (FH.getFootholdName().startsWith("hole")) {
            getFHList.add(new Pair<>(FH.getFootholdName(), true));
         }
      });
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitFootHold(getFHList)));
      GuardianAngelSlime.SlimePattern encode = new GuardianAngelSlime.InitGuardianWave(1);
      byte[] packet = this.encode(encode);
      this.broadcastMessage(packet);
      encode = new GuardianAngelSlime.InitCrystal(1, 7, 0);
      packet = this.encode(encode);
      this.broadcastMessage(packet);
      this.broadcastMessage(CField.sendWeatherEffectNotice(348, 5000, true, "성공했어!! 하단 공간이 닫히기 전에 10초 안에 위로 올라가자!!"));
      encode = new GuardianAngelSlime.InitGuardianWave(5);
      packet = this.encode(encode);
      this.broadcastMessage(packet);
      encode = new GuardianAngelSlime.InitState(4);
      packet = this.encode(encode);
      this.broadcastMessage(packet);
      encode = new GuardianAngelSlime.InitGuardianWave();
      ((GuardianAngelSlime.InitGuardianWave)encode).state = 0;
      packet = this.encode(encode);
      this.broadcastMessage(packet);
      this.success = true;
      this.endGuardianWaveTime = System.currentTimeMillis() + 10000L;
   }

   public void spawnGuardianWave() {
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitGuardianWave(new Point(692, -2199))));
   }

   public void initGuardianWave() {
      this.guardianWave = true;
      this.endGuardianWaveTime = 0L;
      this.lastGuardianWaveStartTime = System.currentTimeMillis() + 10000L;
      this.broadcastMessage(this.encode(new GuardianAngelSlime.MagmaSlimeInit(3)));
      List<Pair<String, Boolean>> getFHList = new ArrayList<>();
      Map<Integer, Boolean> getFHUpdateList = new HashMap<>();
      String[] list = new String[]{"aset", "bset", "cset", "dset", "eset"};
      int index = Randomizer.nextInt(list.length);
      String FSet = list[index];
      this.mapFHList.getDynamicFootholds().forEach(FH -> {
         boolean set = FH.getFootholdName().startsWith("bottom") || FH.getFootholdName().startsWith(FSet);
         getFHList.add(new Pair<>(FH.getFootholdName(), set));
         getFHUpdateList.put(FH.getId(), set);
      });
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitFootHold(getFHList)));
      this.broadcastMessage(CField.updatePortalScript("base", true, 1150));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.SetSlimeGuardPortalCount(10, index)));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.CameraWork(new Point(717, -1287), new Point(717, -118), 500, 300, 4000)));
      List<Object> portalList = new ArrayList<>(List.of(0, 1, 2, 3));
      Collections.shuffle(portalList);
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitPortal(portalList)));
      this.killAllMonster(8880704);
      this.killAllMonster(8880705);
      this.killAllMonster(8880706);
      this.killAllMonster(8880707);
      this.killAllMonster(8880708);
      this.killAllMonster(8880715);
      this.killAllMonster(8880716);
      this.killAllMonster(8880717);
      this.killAllMonster(8880718);
      this.killAllMonster(8880719);
      this.slimeMap.clear();
      this.slimePointList.clear();
      this.broadcastMessage(this.slimeAction(this.boss.getObjectId()));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitCrystal(0, 20, 1000)));
      this.broadcastMessage(MobPacket.blackMageSkillAction(this.boss.getObjectId(), 5, true));
      MobSkillInfo info = MobSkillFactory.getMobSkill(266, 3);
      info.setCasting(this.boss, 0);
      this.broadcastMessage(CField.sendWeatherEffectNotice(348, 5000, true, "가디언 웨이브가 떨어질 거야!! 크리스탈 방울을 설치해 웨이브의 흐름을 제어해줘. 이번에도 내가 도와줄게!"));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitState(3)));
   }

   public byte[] slimeAction(int objectID) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.SLIME_ACTION.getValue());
      p.writeInt(objectID);
      p.writeInt(0);
      return p.getPacket();
   }

   public void initPortal() {
      List<Object> portalPos = new ArrayList<>();
      portalPos.add(new Point(36, -1639));
      portalPos.add(new Point(1375, -1639));
      portalPos.add(new Point(36, -1092));
      portalPos.add(new Point(1375, -1092));
      portalPos.add(new Point(36, -288));
      portalPos.add(new Point(1393, -288));
      portalPos.add(new Point(36, 55));
      portalPos.add(new Point(1393, 55));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitPortal(portalPos)));
   }

   public void initAttack() {
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitAttack1()));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitAttack2()));
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitState(2)));
   }

   public void initFootHold() {
      List<Pair<String, Boolean>> getFHList = new ArrayList<>();
      Map<Integer, Boolean> getFHUpdateList = new HashMap<>();
      this.mapFHList.getDynamicFootholds().forEach(FH -> {
         boolean set = FH.getFootholdName().contains("hole") || FH.getFootholdName().contains("bottom");
         getFHUpdateList.put(FH.getId(), set);
         getFHList.add(new Pair<>(FH.getFootholdName(), set));
      });
      this.broadcastMessage(this.encode(new GuardianAngelSlime.InitFootHold(getFHList)));
   }

   public void updateGauge() {
      for (MapleCharacter chr : this.gaugeList.keySet()) {
         int gauge = this.gaugeList.get(chr);
         chr.send(this.encode(new GuardianAngelSlime.SlimeGauge(gauge, Math.min(100, gauge + 25), 100)));
         this.gaugeList.put(chr, Math.min(100, gauge + 25));
      }
   }

   public void reduceGauge(MapleCharacter chr) {
      int gauge = this.gaugeList.get(chr);
      this.gaugeList.put(chr, 0);
      chr.send(this.encode(new GuardianAngelSlime.SlimeGauge(gauge, 0, 100)));
   }

   public MapleDynamicFoothold loadFootHold(MapleData mapData) {
      MapleDynamicFoothold dynamicFoothold = new MapleDynamicFoothold(System.currentTimeMillis() + 2147483647L);

      for (int i = 0; i <= 7; i++) {
         MapleData layer = mapData.getChildByPath(String.valueOf(i));
         if (layer != null) {
            MapleData obj = layer.getChildByPath("obj");
            if (obj != null) {
               for (MapleData o : obj) {
                  int id = Integer.parseInt(o.getName());
                  int dynamic = MapleDataTool.getInt("dynamic", o, 0);
                  if (dynamic != 0) {
                     String name = MapleDataTool.getString("name", o, null);
                     int x = MapleDataTool.getInt("x", o, 0);
                     int y = MapleDataTool.getInt("y", o, 0);
                     dynamicFoothold.putDynamicFootholdRealName(name, id, 0, new Point(x, y));
                  }
               }
            }
         }
      }

      return dynamicFoothold;
   }

   public void updateTower(GuardianAngelSlime.MagmaSlimeInit slime) {
      Integer count = this.slimeTowerMap.getOrDefault(slime.towerObjectID, 0);
      this.slimeTowerMap.put(slime.towerObjectID, Math.min(3, count + 1));
      slime.count = Math.min(3, count + 1);
      this.broadcastMessage(this.encode(slime));
   }

   public byte[] encode(GuardianAngelSlime.SlimePattern sp) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.GUARDIAN_ANGEL_SLIME.getValue());
      p.writeInt(sp.getOpcode());
      sp.encode(p);
      return p.getPacket();
   }

   public void showShieldEffect() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_BARRIER_EFFECT.getValue());
      packet.writeInt(this.boss.getObjectId());
      packet.write(true);
      packet.writeMapleAsciiString("Etc/BossGuardianSlime.img/groggyEffect");
      packet.writeInt(1);
      packet.write(false);
      packet.write(false);
      this.broadcastMessage(packet.getPacket());
   }
}
