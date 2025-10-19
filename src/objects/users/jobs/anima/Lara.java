package objects.users.jobs.anima;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.MobPacket;
import objects.effect.child.SkillEffect;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Magician;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackAction;
import objects.users.skills.TeleportAttackData_PointWithDirection;
import objects.users.skills.TeleportAttackData_TripleInt;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Lara extends Magician {
   final int SpiritSprinkle = 162001000;
   final int SpiritSprinkleEnhanced = 162121021;
   final int MountainKids = 162000003;
   final int MountainKidsAttack = 162001004;
   final int MountainGuardian = 162001005;
   final int DragonVeinReading = 162101000;
   final int DragonVeinEruption = 162101001;
   final int Eruption_SwellingRiver = 162101003;
   final int Eruption_SwellingRiverEnhanced = 162121012;
   final int Eruption_Whirlwind = 162101006;
   final int Eruption_WhirlwindEnhanced = 162121015;
   final int Eruption_SunriseWell = 162101009;
   final int Eruption_SunriseWell2 = 162101011;
   final int Eruption_SunriseWellEnhanced = 162121017;
   final int Eruption_SunriseWell2Enhanced = 162121019;
   final int MountainsSeeds = 162101012;
   final int WandBooster = 162101013;
   final int Expression_WindSwing = 162111000;
   final int Expression_WhereTheRiverFlows = 162111002;
   final int Expression_SunlightFilledGround = 162111003;
   final int SlumberAwakening = 162111005;
   final int DragonVeinsTraces = 162111006;
   final int DragonVeinsEcho = 162110007;
   final int DragonVeinAbsorption = 162121000;
   final int Absorption_RiverPuddleDrench = 162121003;
   final int Absorption_DesolateWinds = 162121006;
   final int Absorption_SunlightSprout = 162121009;
   final int Absorption_SunlightSproutActive = 162121010;
   final int MountainEnclosure = 162121022;
   final int MountainEnclosure_ExtraShield = 162120038;
   final int VineCoil = 162121041;
   final int FreeDragonVein = 162121042;
   final int BreathtakinglyLargeTree = 162121043;
   final int BreathtakinglyLargeTree2 = 162121044;
   final int LotusFlower = 400001061;
   final int LotusFlower2 = 400001062;
   final int LargeStretch = 400021122;
   final int SunRiverMountainWind = 400021123;
   long lastHpRecoveryTime = 0L;
   long mountainEnclosureTime = 0L;
   long lastFreeDragonVeinTime = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      super.prepareAttack(attack, effect, opcode);
   }

   @Override
   public void onAttack(
         MapleMonster monster,
         boolean boss,
         AttackPair attackPair,
         Skill skill,
         long totalDamage,
         AttackInfo attack,
         SecondaryStatEffect effect,
         RecvPacketOpcode opcode) {
      if (attack.skillID == 162121041) {
         if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
            monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false),
                  false,
                  effect.getDuration(),
                  false,
                  effect);
            monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(),
                  attack.skillID);
         } else {
            this.getPlayer()
                  .send(
                        MobPacket.monsterResist(
                              monster,
                              this.getPlayer(),
                              (int) ((monster.getResistSkill(MobTemporaryStatFlag.FREEZE) - System.currentTimeMillis())
                                    / 1000L),
                              attack.skillID));
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
         boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill,
         long totalExp, RecvPacketOpcode opcode) {
      if ((attack.skillID == 162001000 || attack.skillID == 162121021 || attack.skillID == 162141000)
            && attack.targets > 0) {
         if (this.getPlayer().getTotalSkillLevel(162000003) > 0) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162000003);
            if (level != null && level.makeChanceResult()) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, attack.skillID,
                     1);
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.SunlightSprout) != null) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162120008);
            if (level != null
                  && (this.getPlayer().getLastSunlightSproutTime() == 0L
                        || System.currentTimeMillis() - this.getPlayer().getLastSunlightSproutTime() >= level.getT()
                              * 1000.0)) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 162121010,
                     level.getBulletCount());
               if (this.getPlayer().getInnerSkillLevel(70000045) > 0) {
                  SecondaryStatEffect nocool = SkillFactory.getSkill(70000045)
                        .getEffect(this.getPlayer().getInnerSkillLevel(70000045));
                  if (nocool != null) {
                     int nocoolProp = nocool.getNocoolProp();
                     if (!Randomizer.isSuccess(nocoolProp)) {
                        this.getPlayer().setLastSunlightSproutTime(System.currentTimeMillis());
                     }
                  }
               } else {
                  this.getPlayer().setLastSunlightSproutTime(System.currentTimeMillis());
               }
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DesolateWinds) != null) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162120005);
            if (level != null
                  && (this.getPlayer().getLastDesolateWindsTime() == 0L
                        || System.currentTimeMillis() - this.getPlayer().getLastDesolateWindsTime() >= level.getT()
                              * 1000.0)) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 162121007,
                     level.getU());
               if (this.getPlayer().getInnerSkillLevel(70000045) > 0) {
                  SecondaryStatEffect nocool = SkillFactory.getSkill(70000045)
                        .getEffect(this.getPlayer().getInnerSkillLevel(70000045));
                  if (nocool != null) {
                     int nocoolProp = nocool.getNocoolProp();
                     if (!Randomizer.isSuccess(nocoolProp)) {
                        this.getPlayer().setLastDesolateWindsTime(System.currentTimeMillis());
                     }
                  }
               } else {
                  this.getPlayer().setLastDesolateWindsTime(System.currentTimeMillis());
               }
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.RiverPuddleDrench) != null) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162120002);
            if (level != null
                  && (this.getPlayer().getLastRiverPuddleDrenchTime() == 0L
                        || System.currentTimeMillis() - this.getPlayer().getLastRiverPuddleDrenchTime() >= level.getT()
                              * 1000.0)) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 162121004, 1);
               if (this.getPlayer().getInnerSkillLevel(70000045) > 0) {
                  SecondaryStatEffect nocool = SkillFactory.getSkill(70000045)
                        .getEffect(this.getPlayer().getInnerSkillLevel(70000045));
                  if (nocool != null) {
                     int nocoolProp = nocool.getNocoolProp();
                     if (!Randomizer.isSuccess(nocoolProp)) {
                        this.getPlayer().setLastRiverPuddleDrenchTime(System.currentTimeMillis());
                     }
                  }
               } else {
                  this.getPlayer().setLastRiverPuddleDrenchTime(System.currentTimeMillis());
               }
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
      SecondaryStatEffect effect = SkillFactory.getSkill(162121022)
            .getEffect(this.getPlayer().getSkillLevel(162121022));
      statups.put(SecondaryStatFlag.AntiMagicShell, 1);
      statups.put(SecondaryStatFlag.IndieDamageReduce, -effect.getX());
      statups.put(SecondaryStatFlag.KeyDownStart, 1);
      this.getPlayer().temporaryStatSet(162121022, effect.getLevel(), effect.getQ() * 1000, statups);
      if (this.getPlayer().getSkillLevel(162120038) > 0) {
         this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.indieBarrier, 162120038, Integer.MAX_VALUE,
                     (int) (this.getPlayer().getStat().getMaxHp() / 100L * effect.getX()));
      }

      this.mountainEnclosureTime = System.currentTimeMillis();
      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 162101001:
         default:
            break;
         case 162101003:
         case 162121012: {
            Point position = new Point(packet.readShort(), packet.readShort());
            byte direction = packet.readByte();
            this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
            SecondaryStatEffect levelx = this.getPlayer().getSkillLevelData(this.getActiveSkillID());
            if (levelx != null) {
               Summoned s = new Summoned(
                     this.getPlayer(),
                     this.getActiveSkillID(),
                     this.getActiveSkillLevel(),
                     position,
                     SummonMoveAbility.STATIONARY,
                     direction,
                     System.currentTimeMillis() + levelx.getDuration());
               this.getPlayer().getMap().spawnSummon(s, levelx.getDuration());
               this.getPlayer().addSummon(s);
               this.getPlayer().sendRegisterExtraSkill(position, direction == 1, this.getActiveSkillID());
            }

            this.onDragonVeinEcho();
            this.autoMountainSeed(position, direction);
         }
            break;
         case 162101006:
         case 162121015: {
            Point position = new Point(packet.readShort(), packet.readShort());
            byte direction = packet.readByte();
            this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
            SecondaryStatEffect levelx = this.getPlayer().getSkillLevelData(this.getActiveSkillID());
            if (levelx != null) {
               Summoned s = new Summoned(
                     this.getPlayer(),
                     this.getActiveSkillID(),
                     this.getActiveSkillLevel(),
                     position,
                     SummonMoveAbility.STATIONARY,
                     direction,
                     System.currentTimeMillis() + levelx.getDuration());
               this.getPlayer().getMap().spawnSummon(s, levelx.getDuration());
               this.getPlayer().addSummon(s);
            }

            this.onDragonVeinEcho();
            this.autoMountainSeed(position, direction);
         }
            break;
         case 162101009:
         case 162121017: {
            this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
            new ArrayList<>(this.getPlayer().getMap().getAllMistsThreadsafe()).forEach(area -> {
               if (area.getOwner().getId() == this.getPlayer().getId()
                     && area.getSource().getSourceId() == this.getActiveSkillID()) {
                  this.getPlayer().getMap().broadcastMessage(
                        CField.removeAffectedArea(area.getObjectId(), this.getActiveSkillID() + 1, false));
                  this.getPlayer().getMap().removeMapObject(area);
               }
            });
            TeleportAttackAction teleportAttackActionxxx = this.getTeleportAttackAction();
            if (teleportAttackActionxxx != null) {
               teleportAttackActionxxx.actions.forEach(action -> {
                  if (action.type == 7) {
                     TeleportAttackData_PointWithDirection point = (TeleportAttackData_PointWithDirection) action.data;
                     Point posx = new Point(point.x, point.y);
                     int direction = point.direction;
                     SecondaryStatEffect levelx = this.getPlayer().getSkillLevelData(this.getActiveSkillID());
                     if (levelx != null) {
                        Rect rect = new Rect(posx, levelx.getLt(), levelx.getRb(), direction == 1);
                        AffectedArea aa = new AffectedArea(rect, this.getPlayer(), levelx, posx,
                              System.currentTimeMillis() + levelx.getDuration());
                        aa.setSkillId(this.getActiveSkillID() + 1);
                        aa.setRlType(direction);
                        this.getPlayer().getMap().spawnMist(aa);
                        Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                        statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        statups.put(SecondaryStatFlag.indieSummon, 1);
                        this.getPlayer().temporaryStatSet(this.getActiveSkillID(), this.getActiveSkillLevel(),
                              levelx.getDuration(), statups, true);
                     }

                     this.autoMountainSeed(posx, (byte) direction);
                  }
               });
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162101011:
         case 162121019: {
            byte targetSize = packet.readByte();
            List<Integer> targets = new ArrayList<>();

            for (int i = 0; i < targetSize; i++) {
               int targetObjectID = packet.readInt();
               targets.add(targetObjectID);
            }

            packet.skip(3);
            int xPosx = packet.readInt();
            int yPosx = packet.readInt();
            Point position = new Point(xPosx, yPosx);
            SecondaryStatEffect levelx = this.getPlayer().getSkillLevelData(this.getActiveSkillID());
            Skill skill = SkillFactory.getSkill(this.getActiveSkillID());
            if (levelx != null && skill != null) {
               List<SecondAtom.Atom> atoms = new ArrayList<>();

               for (int i = 0; i < levelx.getBulletCount(); i++) {
                  SecondAtomData data = skill.getSecondAtomData();
                  Point pos = new Point(position);
                  pos.x = pos.x + Randomizer.rand(-200, 200);
                  pos.y = pos.y + Randomizer.rand(-250, 0);
                  SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        this.getActiveSkillID(),
                        SecondAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.SunriseWell,
                        0,
                        null,
                        pos);
                  a.setPlayerID(this.getPlayer().getId());
                  SecondAtomData.atom atomx = data.getAtoms().get(i);
                  a.setExpire(atomx.getExpire());
                  a.setCreateDelay(atomx.getCreateDelay());
                  a.setEnableDelay(atomx.getEnableDelay());
                  a.setAttackableCount(1);
                  a.setSkillID(this.getActiveSkillID());
                  List<SecondAtom.Custom> customs = new ArrayList<>();
                  customs.add(new SecondAtom.Custom(0, xPosx));
                  customs.add(new SecondAtom.Custom(1, yPosx));
                  a.setCustoms(customs);
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
               }

               if (atoms.size() > 0) {
                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), this.getActiveSkillID(), atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }
            }
         }
            break;
         case 162101012: {
            packet.readInt();
            Point position = new Point(packet.readShort(), packet.readShort());
            byte direction = packet.readByte();
            SecondaryStatEffect levelxx = this.getPlayer().getSkillLevelData(162101012);
            if (levelxx != null) {
               int maxCount = levelxx.getQ();
               if (this.getPlayer().getSummonedSize(162101012) >= maxCount) {
                  List<Summoned> removes = new ArrayList<>();

                  try {
                     for (Summoned summoned : this.getPlayer().getSummonsReadLock()) {
                        if (summoned.getSkill() == 162101012) {
                           removes.add(summoned);
                           break;
                        }
                     }
                  } finally {
                     this.getPlayer().unlockSummonsReadLock();
                  }

                  for (Summoned remove : removes) {
                     this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(remove, true));
                     this.getPlayer().getMap().removeMapObject(remove);
                     this.getPlayer().removeVisibleMapObject(remove);
                     this.getPlayer().removeSummon(remove);
                  }
               }

               Summoned s = new Summoned(
                     this.getPlayer(),
                     162101012,
                     this.getActiveSkillLevel(),
                     position,
                     SummonMoveAbility.WALK_STATIONARY,
                     direction,
                     System.currentTimeMillis() + levelxx.getDuration(levelxx.getDuration(), this.getPlayer()));
               this.getPlayer().getMap().spawnSummon(s, levelxx.getDuration(levelxx.getDuration(), this.getPlayer()));
               this.getPlayer().addSummon(s);
               int stackx = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.MountainsSeedsStack, 1);
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.MountainsSeedsStack, 162101012, Integer.MAX_VALUE,
                     stackx - 1);
            }
         }
            break;
         case 162101013: {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162101013);
            if (level != null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.Booster, 162101013,
                     level.getDuration(level.getDuration(), this.getPlayer()), level.getX());
            }
         }
            break;
         case 162111000: {
            this.getPlayer().temporaryStatResetBySkillID(162111000);
            this.getPlayer().getMap().getAllMistsThreadsafe().stream().collect(Collectors.toList()).forEach(area -> {
               if (area.getOwner().getId() == this.getPlayer().getId() && area.getSource().getSourceId() == 162111000) {
                  this.getPlayer().getMap()
                        .broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 162111000, false));
                  this.getPlayer().getMap().removeMapObject(area);
               }
            });
            TeleportAttackAction teleportAttackActionxxx = this.getTeleportAttackAction();
            if (teleportAttackActionxxx != null) {
               teleportAttackActionxxx.actions.stream().forEach(action -> {
                  if (action.type == 7) {
                     TeleportAttackData_PointWithDirection point = (TeleportAttackData_PointWithDirection) action.data;
                     Point posx = new Point(point.x, point.y);
                     int direction = point.direction;
                     SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162111000);
                     if (level != null) {
                        Rect rect = new Rect(posx, level.getLt(), level.getRb(), direction == 1);
                        AffectedArea aa = new AffectedArea(rect, this.getPlayer(), level, posx,
                              System.currentTimeMillis() + level.getDuration());
                        aa.setRlType(direction);
                        Rect buffRect = new Rect(posx, level.getLt3(), level.getRb3(), true);
                        aa.setBuffRect(buffRect);
                        this.getPlayer().getMap().spawnMist(aa);
                     }
                  }
               });
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162111002: {
            int unk3 = packet.readInt();
            int xPos = packet.readInt();
            int yPos = packet.readInt();
            byte unk = packet.readByte();
            if (this.getPlayer().hasBuffBySkillID(162111002)) {
               this.getPlayer().temporaryStatResetBySkillID(162111002);
            }

            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162111002);
            if (level != null) {
               int duration = 0;
               int key = 0;
               List<SecondAtom.Atom> atom = this.getPlayer()
                     .getSecondAtomByType(SecondAtom.SecondAtomType.WhereTheRiverFlows);
               SecondAtom.SecondAtomType atomType = SecondAtom.SecondAtomType.WhereTheRiverFlows;
               if (atom.isEmpty()) {
                  duration = level.getDuration();
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieSummon, 162111002, level.getU(), 1);
               } else {
                  duration = level.getDuration() / 1000;
                  atomType = SecondAtom.SecondAtomType.WhereTheRiverFlows2;
                  key = atom.get(0).getKey();
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieSummon, 162111002, duration, 1);
               }

               duration = this.getPlayer().getBuffTime(162111002, this.getPlayer().getTotalSkillLevel(162111002),
                     duration);
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               SecondAtom.Atom a = new SecondAtom.Atom(
                     this.getPlayer().getMap(), this.getPlayer().getId(), 162111002, SecondAtom.SN.getAndAdd(1),
                     atomType, 0, null, new Point(xPos, yPos));
               a.setPlayerID(this.getPlayer().getId());
               a.setExpire(duration);
               a.setAttackableCount(1);
               a.setSkillID(162111002);
               a.setRange(level.getRange());
               List<SecondAtom.Custom> customs = new ArrayList<>();
               customs.add(new SecondAtom.Custom(0, key));
               a.setCustoms(customs);
               this.getPlayer().addSecondAtom(a);
               atoms.add(a);
               if (atoms.size() > 0) {
                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 162111002, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162111003: {
            this.getPlayer().temporaryStatResetBySkillID(162111003);
            this.getPlayer().getMap().getAllMistsThreadsafe().stream().collect(Collectors.toList()).forEach(area -> {
               if (area.getOwner().getId() == this.getPlayer().getId() && area.getSource().getSourceId() == 162111003) {
                  this.getPlayer().getMap()
                        .broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 162111003, false));
                  this.getPlayer().getMap().removeMapObject(area);
               }
            });
            TeleportAttackAction teleportAttackActionxxx = this.getTeleportAttackAction();
            if (teleportAttackActionxxx != null) {
               teleportAttackActionxxx.actions.stream().forEach(action -> {
                  if (action.type == 7) {
                     TeleportAttackData_PointWithDirection point = (TeleportAttackData_PointWithDirection) action.data;
                     Point posx = new Point(point.x, point.y);
                     int direction = point.direction;
                     SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162111003);
                     if (level != null) {
                        Rect rect = new Rect(posx, level.getLt(), level.getRb(), direction == 1);
                        AffectedArea aa = new AffectedArea(rect, this.getPlayer(), level, posx,
                              System.currentTimeMillis() + level.getDuration());
                        aa.setRlType(direction);
                        this.getPlayer().getMap().spawnMist(aa);
                     }
                  }
               });
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162111005: {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162111005);
            Skill skill = SkillFactory.getSkill(162111005);
            if (level != null && skill != null) {
               TeleportAttackAction teleportAttackActionxxx = this.getTeleportAttackAction();
               if (teleportAttackActionxxx != null) {
                  teleportAttackActionxxx.actions
                        .stream()
                        .forEach(
                              action -> {
                                 if (action.type == 19) {
                                    TeleportAttackData_TripleInt datax = (TeleportAttackData_TripleInt) action.data;
                                    int min = level.getBulletCount();
                                    int max = level.getZ();
                                    byte targetCount = packet.readByte();
                                    List<Integer> targets = new ArrayList<>();

                                    for (int i = 0; i < targetCount; i++) {
                                       targets.add(packet.readInt());
                                    }

                                    packet.skip(3);
                                    Point position = new Point(packet.readInt(), packet.readInt());
                                    int targetSize = Math.min(max, Math.max(min, targetCount));
                                    List<SecondAtom.Atom> atomsx = new ArrayList<>();

                                    for (int i = 0; i < targetSize; i++) {
                                       Point posx = new Point(position);
                                       posx.x = posx.x + Randomizer.rand(-100, 100);
                                       posx.y = posx.y + Randomizer.rand(-350, -200);
                                       SecondAtom.SecondAtomType type = SecondAtom.SecondAtomType.SlumberAwakening_1;
                                       if (datax.getData1() == 2) {
                                          type = SecondAtom.SecondAtomType.SlumberAwakening_2;
                                       } else if (datax.getData1() == 4) {
                                          type = SecondAtom.SecondAtomType.SlumberAwakening_3;
                                       } else if (datax.getData1() == 8) {
                                          type = SecondAtom.SecondAtomType.SlumberAwakening_4;
                                       }

                                       SecondAtom.Atom ax = new SecondAtom.Atom(
                                             this.getPlayer().getMap(), this.getPlayer().getId(), 162111005,
                                             SecondAtom.SN.getAndAdd(1), type, 0, null, posx);
                                       SecondAtomData d = skill.getSecondAtomData();
                                       ax.setPlayerID(this.getPlayer().getId());
                                       ax.setExpire(d.getExpire());
                                       ax.setRotate(d.getRotate());
                                       ax.setCreateDelay(d.getCreateDelay());
                                       ax.setEnableDelay(d.getEnableDelay());
                                       if (!targets.isEmpty()) {
                                          if (i < targetCount) {
                                             ax.setTargetObjectID(targets.get(i));
                                          } else {
                                             ax.setTargetObjectID(targets.get(Randomizer.rand(0, targets.size() - 1)));
                                          }
                                       }

                                       ax.setUnk4(2);
                                       ax.setAttackableCount(1);
                                       ax.setSkillID(162111005);
                                       this.getPlayer().addSecondAtom(ax);
                                       atomsx.add(ax);
                                    }

                                    if (atomsx.size() > 0) {
                                       SecondAtom secondAtomx = new SecondAtom(this.getPlayer().getId(), 162111005,
                                             atomsx);
                                       this.getPlayer().getMap().createSecondAtom(secondAtomx);
                                    }
                                 }
                              });
               }
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162111006: {
            int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.TraceOfDragonVeinStack, 1);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.TraceOfDragonVeinStack, 162111006, Integer.MAX_VALUE,
                  stack - 1);
         }
         case 162121042: {
            TeleportAttackAction teleportAttackActionxx = this.getTeleportAttackAction();
            if (teleportAttackActionxx != null) {
               teleportAttackActionxx.actions
                     .stream()
                     .forEach(
                           action -> {
                              if (action.type == 7) {
                                 TeleportAttackData_PointWithDirection point = (TeleportAttackData_PointWithDirection) action.data;
                                 Point posx = new Point(point.x, point.y);
                                 int direction = point.direction;
                                 SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162121042);
                                 if (level != null) {
                                    SecondAtom.Atom ax = new SecondAtom.Atom(
                                          this.getPlayer().getMap(),
                                          this.getPlayer().getId(),
                                          162101000,
                                          SecondAtom.SN.getAndAdd(1),
                                          SecondAtom.SecondAtomType.DragonVein,
                                          0,
                                          null,
                                          posx);
                                    Skill skillx = SkillFactory.getSkill(162101000);
                                    List<SecondAtom.Atom> atomsx = new ArrayList<>();
                                    if (skillx != null) {
                                       ax.setPlayerID(this.getPlayer().getId());
                                       ax.setSkillID(162101000);
                                       ax.setAttackableCount(1);
                                       ax.setRange(1);
                                       ax.setPos(posx);
                                       ax.setUnk2(1);
                                       List<SecondAtom.Custom> customsx = new ArrayList<>();
                                       customsx.add(new SecondAtom.Custom(0, 8));
                                       customsx.add(new SecondAtom.Custom(1, SecondAtom.SN.get() - 1));
                                       ax.setCustoms(customsx);
                                       this.getPlayer().addSecondAtom(ax);
                                       atomsx.add(ax);
                                    }

                                    if (!atomsx.isEmpty()) {
                                       SecondAtom secondAtomx = new SecondAtom(this.getPlayer().getId(), 162101000,
                                             atomsx);
                                       this.getPlayer().getMap().createSecondAtom(secondAtomx);
                                    }
                                 }
                              }
                           });
            }

            int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.FreeDragonVeinStack, 1);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.FreeDragonVeinStack, 162121042, Integer.MAX_VALUE,
                  stack - 1);
            if (stack - 1 == 0) {
               this.lastFreeDragonVeinTime = 0L;
            } else {
               long passed = System.currentTimeMillis() - this.lastFreeDragonVeinTime;
               this.lastFreeDragonVeinTime = System.currentTimeMillis() - passed;
            }
            break;
         }
         case 162121003: {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162121003);
            if (level != null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.RiverPuddleDrench, 162121003, level.getDuration(),
                     1);
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162121006: {
            if (this.getPlayer().getSummonBySkillID(162101006) != null) {
               this.getPlayer().temporaryStatResetBySkillID(162101006);
            }

            if (this.getPlayer().getSummonBySkillID(162121015) != null) {
               this.getPlayer().temporaryStatResetBySkillID(162121015);
            }

            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162121006);
            if (level != null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.DesolateWinds, 162121006, level.getDuration(), 1);
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162121009: {
            if (this.getPlayer().getMap().getAffectedAreaBySkillId(162121017, this.getPlayer().getId()) != null) {
               AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(162121017,
                     this.getPlayer().getId());
               area.sendDestroyData(this.getPlayer().getClient());
               this.getPlayer().getMap().removeMapObject(area);
            }

            if (this.getPlayer().getMap().getAffectedAreaBySkillId(162101009, this.getPlayer().getId()) != null) {
               AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(162101009,
                     this.getPlayer().getId());
               area.sendDestroyData(this.getPlayer().getClient());
               this.getPlayer().getMap().removeMapObject(area);
            }

            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162121009);
            if (level != null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.SunlightSprout, 162121009, level.getDuration(), 1);
            }

            this.onDragonVeinEcho();
         }
            break;
         case 162121010: {
            SecondaryStatEffect levelx = this.getPlayer().getSkillLevelData(162121010);
            if (levelx != null) {
               byte targetCount = packet.readByte();

               for (int i = 0; i < targetCount; i++) {
                  packet.readInt();
               }

               packet.skip(3);
               Point position = new Point(packet.readInt(), packet.readInt());
               boolean isLeft = packet.readByte() == 0;
               PacketEncoder p = new PacketEncoder();
               p.write(this.getPlayer().isFacingLeft());
               p.writeInt(position.x);
               p.writeInt(position.y);
               SkillEffect e = new SkillEffect(
                     this.getPlayer().getId(), this.getPlayer().getLevel(), 162121010,
                     this.getPlayer().getTotalSkillLevel(162121009), p);
               this.getPlayer().send(e.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               Skill s = SkillFactory.getSkill(162121010);

               for (int i = 0; i < levelx.getBulletCount(); i++) {
                  SecondAtomData data = s.getSecondAtomData();
                  int delta = 1;
                  if (isLeft) {
                     delta = -1;
                  }

                  Point pos = new Point(position);
                  pos.y += -50;
                  pos.x += delta * 50;
                  SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        162121010,
                        SecondAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.SunlightSprout,
                        0,
                        null,
                        pos);
                  a.setPlayerID(this.getPlayer().getId());
                  SecondAtomData.atom atomx = data.getAtoms().get(i);
                  a.setExpire(atomx.getExpire());
                  a.setCreateDelay(levelx.getW() + 120 * i);
                  a.setEnableDelay(1200 + 120 * i);
                  a.setAttackableCount(1);
                  a.setSkillID(162121010);
                  a.setUnk4(1);
                  a.setRotate(atomx.getRotate() * delta);
                  a.setAngle(atomx.getRotate() * delta);
                  List<SecondAtom.Custom> customs = new ArrayList<>();
                  customs.add(new SecondAtom.Custom(0, 156));
                  customs.add(new SecondAtom.Custom(0, levelx.getQ()));
                  a.setCustoms(customs);
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
               }

               if (atoms.size() > 0) {
                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 162121010, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }
            }
         }
            break;
         case 162121043: {
            TeleportAttackAction teleportAttackActionx = this.getTeleportAttackAction();
            if (teleportAttackActionx != null) {
               teleportAttackActionx.actions.stream().forEach(action -> {
                  if (action.type == 7) {
                     TeleportAttackData_PointWithDirection point = (TeleportAttackData_PointWithDirection) action.data;
                     Point posx = new Point(point.x, point.y);
                     posx.y -= 30;
                     int direction = point.direction;
                     SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162121043);
                     if (level != null) {
                        Rect rect = new Rect(posx, level.getLt(), level.getRb(), direction == 1);
                        AffectedArea aa = new AffectedArea(rect, this.getPlayer(), level, posx,
                              System.currentTimeMillis() + level.getDuration());
                        aa.setRlType(direction);
                        this.getPlayer().getMap().spawnMist(aa);
                        Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                        statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        statups.put(SecondaryStatFlag.indieSummon, 1);
                        this.getPlayer().temporaryStatSet(this.getActiveSkillID() + 1, this.getActiveSkillLevel(),
                              level.getDuration(), statups, true);
                     }
                  }
               });
            }
         }
            break;
         case 400021122:
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(400021122);
            Skill skill = SkillFactory.getSkill(400021122);
            if (level != null && skill != null) {
               TeleportAttackAction teleportAttackActionx = this.getTeleportAttackAction();
               if (teleportAttackActionx != null) {
                  teleportAttackActionx.actions
                        .stream()
                        .forEach(
                              action -> {
                                 if (action.type == 19) {
                                    TeleportAttackData_TripleInt datax = (TeleportAttackData_TripleInt) action.data;
                                    byte targetCount = packet.readByte();
                                    List<Integer> targets = new ArrayList<>();

                                    for (int i = 0; i < targetCount; i++) {
                                       targets.add(packet.readInt());
                                    }

                                    packet.skip(3);
                                    Point position = new Point(packet.readInt(), packet.readInt());
                                    int targetSize = level.getBulletCount();
                                    List<SecondAtom.Atom> atomsx = new ArrayList<>();

                                    for (int i = 0; i < targetSize; i++) {
                                       Point posx = new Point(position);
                                       posx.x = posx.x + Randomizer.rand(-100, 100);
                                       posx.y = posx.y + Randomizer.rand(-350, -200);
                                       SecondAtom.SecondAtomType type = SecondAtom.SecondAtomType.SlumberAwakening_1;
                                       if (datax.getData1() == 2) {
                                          type = SecondAtom.SecondAtomType.LargeStretch_2;
                                       } else if (datax.getData1() == 4) {
                                          type = SecondAtom.SecondAtomType.LargeStretch_3;
                                       } else if (datax.getData1() == 8) {
                                          type = SecondAtom.SecondAtomType.LargeStretch_4;
                                       }

                                       SecondAtom.Atom ax = new SecondAtom.Atom(
                                             this.getPlayer().getMap(), this.getPlayer().getId(), 400021122,
                                             SecondAtom.SN.getAndAdd(1), type, 0, null, posx);
                                       SecondAtomData d = skill.getSecondAtomData();
                                       ax.setPlayerID(this.getPlayer().getId());
                                       ax.setExpire(d.getExpire());
                                       ax.setRotate(d.getRotate());
                                       ax.setCreateDelay(d.getCreateDelay());
                                       ax.setEnableDelay(d.getEnableDelay());
                                       if (!targets.isEmpty()) {
                                          if (i < targetCount) {
                                             ax.setTargetObjectID(targets.get(i));
                                          } else {
                                             ax.setTargetObjectID(targets.get(Randomizer.rand(0, targets.size() - 1)));
                                          }
                                       }

                                       ax.setUnk4(7);
                                       ax.setAttackableCount(1);
                                       ax.setSkillID(400021122);
                                       this.getPlayer().addSecondAtom(ax);
                                       atomsx.add(ax);
                                    }

                                    if (atomsx.size() > 0) {
                                       SecondAtom secondAtomx = new SecondAtom(this.getPlayer().getId(), 400021122,
                                             atomsx);
                                       this.getPlayer().getMap().createSecondAtom(secondAtomx);
                                    }

                                    int cooldownReduce = datax.getData2();
                                    if (cooldownReduce > 0 && cooldownReduce < level.getBulletCount()) {
                                       int stackx = 5 - cooldownReduce;
                                       this.getPlayer().changeCooldown(400021122, -level.getW() * 1000 * stackx);
                                    }
                                 }
                              });
               }
            }

            this.onDragonVeinEcho();
            break;
         case 400021123:
            TeleportAttackAction teleportAttackAction = this.getTeleportAttackAction();
            if (teleportAttackAction != null) {
               teleportAttackAction.actions.stream().forEach(action -> {
                  if (action.type == 7) {
                     TeleportAttackData_PointWithDirection point = (TeleportAttackData_PointWithDirection) action.data;
                     Point posx = new Point(point.x, point.y);
                     this.getPlayer().sendRegisterExtraSkill(posx, point.direction == 1, 400021123);
                  }
               });
            }
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 162121022) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(162121022);
         if (eff != null) {
            long gap = 10000L - (System.currentTimeMillis() - this.mountainEnclosureTime);
            this.getPlayer().changeCooldown(162121022, -((int) (gap * eff.getT())));
         }

         this.getPlayer().temporaryStatResetBySkillID(162121022);
         if (this.getPlayer().hasBuffBySkillID(162120038)) {
            IndieTemporaryStatEntry entry = this.getPlayer().getIndieTemporaryStatEntry(SecondaryStatFlag.indieBarrier,
                  162120038);
            if (entry != null) {
               int value = entry.getStatValue();
               SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162120038);
               if (level != null) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieBarrier, 162120038,
                        level.getDuration() / 1000, value);
               }
            }
         }
      }

      super.activeSkillCancel();
   }

   public void updateLaraSkillStack(int skillID) {
      if (skillID == 162101012) {
         int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.MountainsSeedsStack, 0);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.MountainsSeedsStack, 162101012, Integer.MAX_VALUE,
               stack + 1);
      } else if (skillID == 162121042) {
         int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.FreeDragonVeinStack, 0);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.FreeDragonVeinStack, 162121042, Integer.MAX_VALUE,
               stack + 1);
      } else if (skillID == 162111006) {
         int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.TraceOfDragonVeinStack, 0);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.TraceOfDragonVeinStack, 162111006, Integer.MAX_VALUE,
               stack + 1);
      }

      this.getPlayer().send(CField.updateSkillStackRequestResult(skillID));
   }

   public void onDragonVeinEcho() {
      SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162110007);
      if (level != null) {
         HashMap<SecondaryStatFlag, Integer> flags = new HashMap<>();
         SecondaryStatEffect enhance = this.getPlayer().getSkillLevelData(162120037);
         if (enhance != null) {
            int speed = enhance.getX();
            int jump = enhance.getY();
            int recoveryHp = enhance.getW();
            flags.put(SecondaryStatFlag.indieSpeed, speed);
            flags.put(SecondaryStatFlag.indieJump, jump);
         }

         if (this.getPlayer().hasBuffBySkillID(400001049)) {
            SecondaryStatEffect gGodBless = this.getPlayer().getSkillLevelData(400001046);
            if (gGodBless != null) {
               flags.put(SecondaryStatFlag.indiePMDR, gGodBless.getQ());
            } else {
               flags.put(SecondaryStatFlag.indiePMDR, level.getX());
            }
         } else {
            flags.put(SecondaryStatFlag.indiePMDR, level.getX());
         }

         this.getPlayer().temporaryStatSet(162110007, this.getPlayer().getSkillLevel(162110007), level.getDuration(),
               flags);
         SecondaryStatEffect stanceEnhance = this.getPlayer().getSkillLevelData(162120039);
         if (stanceEnhance != null) {
            HashMap<SecondaryStatFlag, Integer> sflags = new HashMap<>();
            sflags.put(SecondaryStatFlag.indieFlyAcc, 1);
            sflags.put(SecondaryStatFlag.indieSuperStance, 1);
            this.getPlayer().temporaryStatSet(162120039, this.getPlayer().getSkillLevel(162120039), 1000, flags);
         }

         int u = level.getU();
         int mp = (int) (this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * u);
         int hp = (int) (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * u);
         this.getPlayer().addMPHP(hp, mp);
      }
   }

   public void autoMountainSeed(Point pos, byte direction) {
      int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.MountainsSeedsStack, 0);
      if (this.getPlayer().getOneInfoQuestInteger(1544, "162101012") > 0 && stack > 0) {
         SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162101012);
         if (level != null) {
            int maxCount = level.getQ();
            if (this.getPlayer().getSummonedSize(162101012) >= maxCount) {
               List<Summoned> removes = new ArrayList<>();

               try {
                  for (Summoned summoned : this.getPlayer().getSummonsReadLock()) {
                     if (summoned.getSkill() == 162101012) {
                        removes.add(summoned);
                        break;
                     }
                  }
               } finally {
                  this.getPlayer().unlockSummonsReadLock();
               }

               for (Summoned remove : removes) {
                  this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(remove, true));
                  this.getPlayer().getMap().removeMapObject(remove);
                  this.getPlayer().removeVisibleMapObject(remove);
                  this.getPlayer().removeSummon(remove);
               }
            }

            Summoned s = new Summoned(
                  this.getPlayer(),
                  162101012,
                  this.getActiveSkillLevel(),
                  pos,
                  SummonMoveAbility.WALK_STATIONARY,
                  direction,
                  System.currentTimeMillis() + level.getDuration(level.getDuration(), this.getPlayer()));
            this.getPlayer().getMap().spawnSummon(s, level.getDuration(level.getDuration(), this.getPlayer()));
            this.getPlayer().addSummon(s);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.MountainsSeedsStack, 162101012, Integer.MAX_VALUE,
                  stack - 1);
         }
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();

      try {
         if (this.getPlayer().getTotalSkillLevel(162121042) > 0) {
            if (this.lastFreeDragonVeinTime == 0L) {
               this.lastFreeDragonVeinTime = System.currentTimeMillis();
            }

            if (this.lastFreeDragonVeinTime + 10000L < System.currentTimeMillis()) {
               int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.FreeDragonVeinStack, 0);
               if (stack < 3) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.FreeDragonVeinStack, 162121042, Integer.MAX_VALUE,
                        stack + 1);
                  this.getPlayer().send(CField.updateSkillStackRequestResult(162121042));
                  this.lastFreeDragonVeinTime = System.currentTimeMillis();
               }
            }
         }

         if (this.getPlayer().hasBuffBySkillID(162110007) && this.getPlayer().getSkillLevel(162120037) > 0) {
            SecondaryStatEffect enhance = this.getPlayer().getSkillLevelData(162120037);
            if (enhance != null
                  && (this.lastHpRecoveryTime + 5000L <= System.currentTimeMillis() || this.lastHpRecoveryTime == 0L)) {
               int recoveryHp = enhance.getW();
               int hp = (int) (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * recoveryHp);
               this.getPlayer().addHP(hp, false);
               this.lastHpRecoveryTime = System.currentTimeMillis();
            }
         }
      } catch (Exception var4) {
         System.out.println("Lara Err");
         var4.printStackTrace();
      }
   }

   public void skillUpdatePerTick(int skillID, PacketDecoder slea) {
      if (skillID == 162121022) {
         SecondaryStatEffect effect = SkillFactory.getSkill(162120038)
               .getEffect(this.getPlayer().getTotalSkillLevel(162120038));
         if (effect == null) {
            return;
         }

         if (this.getPlayer().hasBuffBySkillID(162120038)) {
            IndieTemporaryStatEntry entry = this.getPlayer().getIndieTemporaryStatEntry(SecondaryStatFlag.indieBarrier,
                  162120038);
            if (entry != null) {
               int hp = (int) (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01) * effect.getX();
               int value = entry.getStatValue();
               SecondaryStatEffect level = this.getPlayer().getSkillLevelData(162120038);
               if (level != null) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieBarrier, 162120038,
                        level.getDuration() / 1000, value + hp);
               }
            }
         }
      }
   }
}
