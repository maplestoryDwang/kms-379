package objects.fields.child.blackmage;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.MobPacket;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.fieldset.instance.HardBlackMageBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.MobZoneInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class Field_BlackMageBattlePhase3 extends Field_BlackMage {
   private static Map<String, Point> bmFootHoldList = new HashMap<>();
   public List<MapleMonster> mobZoneMobs = new ArrayList<>();
   public MobZoneInfo currentMobZone = null;
   private long nextCreateRedMeteorsTime = 0L;
   private long nextCreateDarkFallingTime = 0L;
   private long nextCreateMorningStarfallTime = 0L;
   private long nextCreateLaserTime = 0L;
   private long nextCreateBMFootHoldTime = 0L;
   private long nextPrepareWeldingCreationTime = 0L;
   private long nextNoticeWeldingCreationTime = 0L;
   private long nextWeldingCreationTime = 0L;
   private long endWeldingDestructionTime = 0L;
   private long nextSpawnKamaelTime = 0L;
   private boolean spawned3PhaseBoss = false;
   private int mobZone = 1;
   private Field_BlackMageBattlePhase3.WeldingCreationType weldingCreationType = Field_BlackMageBattlePhase3.WeldingCreationType.None;
   private List<BMFootHold> bmFootHolds = new ArrayList<>();
   private BlackMageOrca orca = null;
   private long nextOrcaAttackTime = 0L;
   private List<BlackMageOrcaAttackEntry> orcaAttacks = new ArrayList<>();

   public Field_BlackMageBattlePhase3(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
      this.setPhase(3);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.findBoss();
      if (this.spawned3PhaseBoss && boss == null) {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            this.removeMonster(mob, 1);
         }

         this.sendBlackMageNotice("압도적인 기운에 의해 주변의 모든 것이 순식간에 소멸해간다.", 7000);
         MapleCharacter p = null;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               p = player;
               break;
            }
         }

         Party party = p.getParty();
         if (party != null) {
            for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
               MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
               if (character != null
                  && character.getDeathCount() > 0
                  && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                  character.setCurrentBossPhase(4);
                  if (character.getRegisterTransferFieldTime() == 0L) {
                     character.setRegisterTransferField(this.getId() + 100);
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                  }
               }
            }

            this.spawned3PhaseBoss = false;
         }
      } else if (this.spawned3PhaseBoss) {
         if (this.nextCreateRedMeteorsTime == 0L) {
            this.nextCreateRedMeteorsTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextCreateRedMeteorsTime != 0L && this.nextCreateRedMeteorsTime <= System.currentTimeMillis()) {
            this.createRedMeteors();
            this.nextCreateRedMeteorsTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextCreateDarkFallingTime == 0L) {
            this.nextCreateDarkFallingTime = System.currentTimeMillis() + 35000L;
         }

         if (this.nextCreateDarkFallingTime != 0L && this.nextCreateDarkFallingTime <= System.currentTimeMillis()) {
            this.createDarkFalling();
            this.nextCreateDarkFallingTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextCreateMorningStarfallTime == 0L) {
            this.nextCreateMorningStarfallTime = System.currentTimeMillis() + 5000L;
         }

         if (this.nextCreateMorningStarfallTime != 0L && this.nextCreateMorningStarfallTime <= System.currentTimeMillis()) {
            this.createMorningStarfall();
            this.nextCreateMorningStarfallTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextCreateBMFootHoldTime == 0L) {
            this.nextCreateBMFootHoldTime = System.currentTimeMillis() + 18000L;
         }

         if (this.nextCreateBMFootHoldTime != 0L && this.nextCreateBMFootHoldTime <= System.currentTimeMillis()) {
            this.createBMFootHold();
            this.nextCreateBMFootHoldTime = System.currentTimeMillis() + 18000L;
         }

         if (this.nextCreateLaserTime == 0L) {
            this.nextCreateLaserTime = System.currentTimeMillis() + 4000L;
         }

         if (this.nextCreateLaserTime != 0L && this.nextCreateLaserTime <= System.currentTimeMillis()) {
            this.broadcastMessage(MobPacket.blackMageSkillAction(boss.getObjectId(), 3, false));
            this.createLaser();
            this.nextCreateLaserTime = System.currentTimeMillis() + Randomizer.rand(4, 8) * 1000;
         }

         if (this.nextSpawnKamaelTime == 0L) {
            this.nextSpawnKamaelTime = System.currentTimeMillis() + 120000L;
         }

         if (this.nextSpawnKamaelTime <= System.currentTimeMillis()) {
            this.spawnKamael();
            this.nextSpawnKamaelTime = System.currentTimeMillis() + 120000L;
         }

         if (this.nextPrepareWeldingCreationTime == 0L) {
            this.nextPrepareWeldingCreationTime = System.currentTimeMillis() + 65000L;
         }

         if (this.nextPrepareWeldingCreationTime != 0L && this.nextPrepareWeldingCreationTime <= System.currentTimeMillis()) {
            this.weldingCreationType = Field_BlackMageBattlePhase3.WeldingCreationType.get(Randomizer.rand(0, 1));
            boss.addAttackBlocked(0);
            boss.addSkillFilter(0);
            boss.broadcastAttackBlocked();
            this.nextNoticeWeldingCreationTime = System.currentTimeMillis() + 3000L;
            this.nextPrepareWeldingCreationTime = 0L;
         }

         if (this.nextNoticeWeldingCreationTime != 0L && this.nextNoticeWeldingCreationTime <= System.currentTimeMillis()) {
            this.broadcastMessage(MobPacket.blackMageSkillAction(boss.getObjectId(), this.weldingCreationType.getType(), true));
            this.sendBlackMageNotice("검은 마법사가 창조와 파괴의 권능을 사용한다. 위와 아래, 어느 쪽으로 피할지 선택해야 한다.", 4000);
            if (this.weldingCreationType == Field_BlackMageBattlePhase3.WeldingCreationType.Creation) {
               this.nextWeldingCreationTime = System.currentTimeMillis() + 3000L;
            } else if (this.weldingCreationType == Field_BlackMageBattlePhase3.WeldingCreationType.Destruction) {
               this.nextWeldingCreationTime = System.currentTimeMillis() + 1000L;
            }

            boss.removeAttackBlocked(0);
            boss.removeSkillFilter(0);
            boss.broadcastAttackBlocked();
            this.nextNoticeWeldingCreationTime = 0L;
         }

         if (this.endWeldingDestructionTime != 0L) {
            if (this.endWeldingDestructionTime <= System.currentTimeMillis()) {
               this.removeBMFootHolds();
               this.createBarrier();
               int x = boss.getTruePosition().x + 350;
               if (boss.getTruePosition().x >= 150) {
                  x = boss.getTruePosition().x - 350;
               }

               int y = -115;
               this.orca.setPosition(new Point(x, y));
               this.orcaTeleportResult();
               this.orcaDoAttack(1);
               this.endWeldingDestructionTime = 0L;
            } else {
               for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
                  if (playerx.isAlive()
                     && playerx.getTruePosition().y > 0
                     && playerx.getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) == null
                     && playerx.getBuffedValue(SecondaryStatFlag.NotDamaged) == null) {
                     int hp = (int)(playerx.getStat().getCurrentMaxHp(playerx) * 0.01 * 10000.0);
                     playerx.healHP(-hp);
                  }
               }
            }
         }

         if (this.nextWeldingCreationTime != 0L && this.nextWeldingCreationTime <= System.currentTimeMillis()) {
            if (this.weldingCreationType == Field_BlackMageBattlePhase3.WeldingCreationType.Creation) {
               this.createWeldingCreationObstacle();
               this.removeBMFootHolds();
               this.createBarrier();
               int x = boss.getTruePosition().x + 350;
               if (boss.getTruePosition().x >= 150) {
                  x = boss.getTruePosition().x - 350;
               }

               int y = -115;
               this.orca.setPosition(new Point(x, y));
               this.orcaTeleportResult();
               this.orcaDoAttack(1);
            } else if (this.weldingCreationType == Field_BlackMageBattlePhase3.WeldingCreationType.Destruction) {
               this.sendWeldingDestruction();
               this.endWeldingDestructionTime = System.currentTimeMillis() + 6000L;
            }

            this.nextWeldingCreationTime = 0L;
         }

         if (this.nextOrcaAttackTime == 0L) {
            this.nextOrcaAttackTime = System.currentTimeMillis() + 4000L;
         }

         if (this.nextOrcaAttackTime != 0L && this.nextOrcaAttackTime <= System.currentTimeMillis()) {
            this.orcaDoAttack(0);
            this.nextOrcaAttackTime = System.currentTimeMillis() + 4000L;
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.mobZoneMobs.clear();
      this.currentMobZone = null;
      this.nextCreateRedMeteorsTime = 0L;
      this.nextCreateDarkFallingTime = 0L;
      this.nextCreateMorningStarfallTime = 0L;
      this.nextCreateLaserTime = 0L;
      this.nextPrepareWeldingCreationTime = 0L;
      this.nextNoticeWeldingCreationTime = 0L;
      this.nextWeldingCreationTime = 0L;
      this.endWeldingDestructionTime = 0L;
      this.nextCreateBMFootHoldTime = 0L;
      this.nextSpawnKamaelTime = 0L;
      this.mobZone = 0;
      this.bmFootHolds.clear();
      this.spawned3PhaseBoss = false;
      this.weldingCreationType = Field_BlackMageBattlePhase3.WeldingCreationType.None;
      this.orca = null;
      this.nextOrcaAttackTime = 0L;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.setPhase(3);
      MapleMonster boss = this.findBoss();
      if (boss != null) {
         boss.getRelMobZones().stream().collect(Collectors.toList()).forEach(p -> boss.sendRegisterMobZone(p.left, p.right));
         this.broadcastMobPhaseChange(boss.getObjectId(), boss.getPhase(), 0);
         this.broadcastMobZoneChange(boss.getObjectId(), boss.getMobZoneDataType());
      }

      for (BMFootHold fh : new ArrayList<>(this.bmFootHolds)) {
         this.sendBMFootHold(fh);
      }

      player.setCanAttackBMRewardMob(false);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (DBConfig.isGanglim
         && this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HardBlackMageBoss
         && this.findBoss() != null
         && this.findBoss().getId() == mob.getId()) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      MapleMonster boss = this.findBoss();
      if (boss != null && mob.getId() == 8880503) {
         boss.registerMobZone(5, mob.getObjectId());
         this.changeMobZone(1);
         this.mobZone = 1;
         this.spawned3PhaseBoss = true;
         this.orca = new BlackMageOrca(1, 1, new Point(-518, -380));
         this.orcaEnterField();
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      if (mob.getId() == 8880503) {
         if (mob.getHPPercent() <= 60 && mob.getHPPercent() > 30) {
            if (this.mobZone == 1) {
               this.changeMobZone(2);
               this.mobZone = 2;
            }
         } else if (mob.getHPPercent() <= 30 && this.mobZone == 2) {
            this.changeMobZone(3);
            this.mobZone = 3;
         }
      }
   }

   public void createBMFootHold() {
      int size = Randomizer.rand(1, 2);

      for (int i = 0; i < size; i++) {
         BMFootHold fh = null;
         int count = 0;

         do {
            BMFootHold f = null;
            int c = 0;
            int rand = Randomizer.rand(0, bmFootHoldList.size() - 1);

            for (Entry<String, Point> entry : bmFootHoldList.entrySet()) {
               if (c++ == rand) {
                  f = new BMFootHold(entry.getValue(), entry.getKey());
                  break;
               }
            }

            if (f != null) {
               boolean find = false;

               for (BMFootHold bf : new ArrayList<>(this.bmFootHolds)) {
                  if (bf.getFootHoldName().equals(f.getFootHoldName())) {
                     find = true;
                     break;
                  }
               }

               if (!find) {
                  fh = f;
                  break;
               }
            }
         } while (count++ < 1000);

         if (fh != null) {
            this.sendBMFootHold(fh);
            this.bmFootHolds.add(fh);
         }
      }
   }

   public void orcaEnterField() {
      if (this.orca != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ORCA_ENTER_FIELD.getValue());
         this.orca.encode(packet);
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void orcaAttackResult(BlackMageOrcaAttack attack) {
      if (this.orca != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ORCA_ATTACK_RESULT.getValue());
         attack.encode(packet);
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void setOrcaPosition(Point pos) {
      if (this.orca != null) {
         this.orca.setPosition(pos);
      }
   }

   public void orcaTeleportResult() {
      if (this.orca != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ORCA_TELEPORT_RESULT.getValue());
         packet.writeInt(this.orca.getKey());
         packet.writeInt(this.orca.getPosition().x);
         packet.writeInt(this.orca.getPosition().y);
         packet.write(0);
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void orcaDoAttack(int type) {
      if (this.orca != null) {
         MapleMonster boss = this.findBoss();
         if (boss != null) {
            if (type == 0) {
               BlackMageOrcaAttackEntry entry = new BlackMageOrcaAttackEntry(9999999999L, 0);
               BlackMageOrcaAttack attack = new BlackMageOrcaAttack(
                  this.orca.getKey(), 0, boss.getTruePosition(), boss.getObjectId(), Collections.singletonList(entry)
               );
               this.orcaAttacks.add(entry);
               this.orcaAttackResult(attack);
            } else if (type == 1) {
               List<BlackMageOrcaAttackEntry> entrys = new ArrayList<>();

               for (int i = 0; i < 13; i++) {
                  BlackMageOrcaAttackEntry entry = new BlackMageOrcaAttackEntry(9999999999L, 540 + i * 180);
                  entrys.add(entry);
                  this.orcaAttacks.add(entry);
               }

               BlackMageOrcaAttack attack = new BlackMageOrcaAttack(this.orca.getKey(), 1, boss.getTruePosition(), boss.getObjectId(), entrys);
               this.orcaAttackResult(attack);
            }
         }
      }
   }

   public BlackMageOrcaAttackEntry getOrcaAttackEntry(int key) {
      for (BlackMageOrcaAttackEntry e : new ArrayList<>(this.orcaAttacks)) {
         if (e.getKey() == key) {
            return e;
         }
      }

      return null;
   }

   public void removeOrcaAttackEntry(BlackMageOrcaAttackEntry entry) {
      this.orcaAttacks.remove(entry);
   }

   public void sendBMFootHold(BMFootHold fh) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.PowerOfCreation.getSkillID());
      packet.writeInt(1);
      packet.writeInt(1);
      fh.encode(packet);
      packet.writeInt(1);
      this.broadcastMessage(packet.getPacket());
   }

   public void removeBMFootHolds() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.PowerOfCreation.getSkillID());
      packet.writeInt(1);
      packet.writeInt(this.bmFootHolds.size());

      for (BMFootHold fh : this.bmFootHolds) {
         fh.encode(packet);
         packet.writeInt(0);
      }

      this.broadcastMessage(packet.getPacket());
      this.bmFootHolds.clear();
   }

   public void createRedMeteors() {
      ObstacleAtom atom = new ObstacleAtom(76, new Point(0, 350), new Point(0, -1350), 0);
      atom.setKey(Randomizer.nextInt());
      atom.setvPerSec(55);
      atom.setTrueDamR(10);
      atom.setMaxP(2);
      atom.setAngle(180);
      this.broadcastMessage(CField.createSingleObstacle(ObstacleAtomCreateType.NORMAL, null, null, atom));
   }

   public void createLaser() {
      List<BlackMageLaser> lasers = new ArrayList<>();
      int rand = Randomizer.rand(2, 5);
      if (Randomizer.isSuccess(10)) {
         rand = Randomizer.rand(6, 8);
      }

      for (int i = 0; i < rand; i++) {
         BlackMageLaser laser = new BlackMageLaser(
            new Point(Randomizer.rand(-925, 925), -Randomizer.rand(100, 380)), Randomizer.rand(0, 359), 250 + i * 200 + Randomizer.rand(0, 150)
         );
         lasers.add(laser);
      }

      this.sendCreateLasers(lasers);
   }

   public void spawnKamael() {
      int count = 0;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mob.getId() >= 8880509 && mob.getId() <= 8880511) {
            count++;
         }
      }

      if (count < 8) {
         this.sendBlackMageNotice("파괴의 천사가 무에서 창조된다.", 3000);
         int size = Randomizer.rand(1, 3);

         for (int i = 0; i < size; i++) {
            MapleMonster mobx = MapleLifeFactory.getMonster(8880509 + Randomizer.rand(0, 2));
            this.spawnMonsterOnGroundBelow(mobx, new Point(Randomizer.rand(-760, 830), 85));
         }
      }
   }

   public void createWeldingCreationObstacle() {
      Set<ObstacleAtom> atoms = new HashSet<>();

      for (int i = 0; i < Randomizer.rand(20, 30); i++) {
         int type = 78;
         int x = Randomizer.rand(-970, 970);
         int y = -600;
         Point endPos = new Point(x, y + 700);
         if (x >= -920 && x <= -696) {
            endPos.y = -151;
         } else if (x >= -617 && x <= -512) {
            endPos.y = -296;
         } else if (x >= -366 && x <= -231) {
            endPos.y = -209;
         } else if (x >= 106 && x <= 177) {
            endPos.y = -88;
         } else if (x >= 391 && x <= 550) {
            endPos.y = -183;
         } else if (x >= 669 && x <= 898) {
            endPos.y = -307;
         }

         ObstacleAtom atom = new ObstacleAtom(type, new Point(x, y), endPos, 450);
         atom.setHitBoxRange(100);
         atom.setKey(Randomizer.nextInt());
         atom.setTrueDamR(999);
         atom.setMobDamR(0);
         atom.setHeight(0);
         atom.setvPerSec(Randomizer.rand(520, 600));
         atom.setMaxP(3);
         atom.setAngle(0);
         atoms.add(atom);
      }

      this.broadcastMessage(CField.createObstacle(ObstacleAtomCreateType.NORMAL, null, null, atoms));
   }

   @Override
   public MapleMonster findBoss() {
      return this.getMonsterById(8880503);
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

   @Override
   public void broadcastMobPhaseChange(int objectID, int phase, int rampage) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_PHASE_CHANGE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(phase);
      packet.write(rampage);
      this.broadcastMessage(packet.getPacket());
   }

   @Override
   public void broadcastMobZoneChange(int objectID, int mobZoneDataType) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_ZONE_CHANGE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(mobZoneDataType);
      this.broadcastMessage(packet.getPacket());
   }

   static {
      bmFootHoldList.put("foo1", new Point(500, -298));
      bmFootHoldList.put("foo2", new Point(-561, -298));
      bmFootHoldList.put("foo3", new Point(-283, -211));
      bmFootHoldList.put("foo4", new Point(144, -90));
      bmFootHoldList.put("foo5", new Point(485, -185));
      bmFootHoldList.put("foo6", new Point(789, -309));
      bmFootHoldList.put("foot1", new Point(-792, -153));
   }

   public static enum WeldingCreationType {
      None(-1),
      Destruction(0),
      Creation(1);

      private int type;

      private WeldingCreationType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static Field_BlackMageBattlePhase3.WeldingCreationType get(int type) {
         for (Field_BlackMageBattlePhase3.WeldingCreationType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
