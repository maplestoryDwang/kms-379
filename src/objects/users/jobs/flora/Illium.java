package objects.users.jobs.flora;

import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Magician;
import objects.users.skills.CrystalGate;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_ListLong;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Pair;
import objects.utils.Rect;

public class Illium extends Magician {
   private long harmonyWingBeatLastTime = 0L;
   private int crystalGateNum = 0;
   private int craftJavelinVIStack = 0;
   private int gloryWingJavelinVIStack = 0;
   private long gloryWingJavelinAttackTime = 0L;
   private int unlimitedCrystalStack = 0;
   private long unlimitedCrystalTime = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 152121041) {
         this.getPlayer().temporaryStatSet(152121041, 4000, SecondaryStatFlag.NotDamaged, 1);
      }

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
      RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400021061) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(152000009);
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID != 152001002 && attack.skillID != 152120003) {
         int curseMarkSkillID = this.getPlayer().getCurseMarkSkillID();
         if (curseMarkSkillID != 0 && attack.skillID != curseMarkSkillID) {
            MobTemporaryStatEffect mse_ = null;
            if (monster.getBuff(MobTemporaryStatFlag.CURSE_MARK) != null) {
               this.getPlayer()
                  .send(CField.userBonusAttackRequest(curseMarkSkillID, false, Collections.singletonList(new Pair<>(monster.getObjectId(), 0)), 1, 0));
            }
         }
      }

      if (attack.skillID == 152121007) {
         monster.tryApplyCurseMark(this.getPlayer(), 152121007);
      }

      SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CrystalGate);
      if (eff != null
         && SkillFactory.getSkill(attack.skillID).getType() == 1
         && System.currentTimeMillis() - this.getPlayer().getLastActiveCrystalGateAttackTime() >= eff.getT() * 1000.0) {
         this.getPlayer().setLastActiveCrystalGateAttackTime(System.currentTimeMillis());
         this.getPlayer().getClient().getSession().writeAndFlush(CField.userBonusAttackRequest(400021111, true, new ArrayList<>(), 0));
      }

      if (this.getPlayer().getTotalSkillLevel(152100011) > 0 && monster.getMp() > 0) {
         int maxMp = monster.getMobMaxMp();
         if (ThreadLocalRandom.current().nextInt(100) < 20) {
            long healMp = 0L;
            if (boss) {
               healMp = (long)(maxMp * 0.05);
            } else {
               healMp = (long)(maxMp * 0.1);
            }

            this.getPlayer().healMP(healMp);
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 152121007) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.GloryWing);
         if (e != null) {
            this.getPlayer().setUseMortalWingBit(true);
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.GloryWing, 152111003, 1);
            statManager.temporaryStatSet();
         }
      }

      Summoned sum = this.getPlayer().getSummonBySkillID(152121005);
      if (sum != null) {
         this.getPlayer().send(CField.summonAttackActive(this.getPlayer().getId(), sum.getObjectId(), 1));
      }

      if (attack.skillID == 152141000 || attack.skillID == 152141001) {
         if (attack.targets > 0) {
            this.craftJavelinVIStack++;
            if (this.craftJavelinVIStack >= 30) {
               this.craftJavelinVIStack = 30;
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.CraftEnchantJavelin, 152141000, Integer.MAX_VALUE, 1);
            } else {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.CraftEnchantJavelin, 152141000, Integer.MAX_VALUE, 0);
            }
         }

         if (this.gloryWingJavelinVIStack > 0) {
            this.gloryWingJavelinVIStack = 0;
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.GloryWingEnchantJavelin, 152141000, Integer.MAX_VALUE, 0);
         }
      }

      if (attack.skillID == 152141004 && attack.targets > 0 && System.currentTimeMillis() - this.gloryWingJavelinAttackTime > 100L) {
         this.gloryWingJavelinVIStack++;
         if (this.gloryWingJavelinVIStack >= 10) {
            this.gloryWingJavelinVIStack = 10;
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.GloryWingEnchantJavelin, 152141004, Integer.MAX_VALUE, 1);
         } else {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.GloryWingEnchantJavelin, 152141004, Integer.MAX_VALUE, 0);
         }

         this.gloryWingJavelinAttackTime = System.currentTimeMillis();
      }

      if (attack.skillID == 152141506) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.UnlimitedCrystal);
         if (value != null) {
            if (value == 3) {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.UnlimitedCrystal, 152141500, 0);
               statManager.temporaryStatSet();
            } else {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.UnlimitedCrystal, 152141500, value + 1);
               statManager.temporaryStatSet();
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      label785:
      switch (this.getActiveSkillID()) {
         case 152001001:
         case 152120001:
         case 152141000:
         case 152141002:
            packet.skip(7);
            int maxBound = packet.readInt();
            int useTick = packet.readInt();
            if (this.getActiveSkillID() == 152141002) {
               for (int i = 0; i < 3; i++) {
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initCraftJabelin(
                     useTick, new Rect(effect.getLt(), effect.getRb()), new Rect(effect.getLt2(), effect.getRb2()), effect.getRange(), maxBound
                  );
                  ForceAtom forceAtom = new ForceAtom(
                     info,
                     this.getActiveSkillID(),
                     this.getPlayer().getId(),
                     false,
                     false,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.CraftJavelinVI,
                     Collections.singletonList(0),
                     1
                  );
                  this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
               }

               try {
                  for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
                     if (summon.getMoveAbility() == SummonMoveAbility.SHADOW_SERVANT_EXTEND) {
                        this.handleCrystalCharge(152141002, summon);
                        break;
                     }
                  }
               } finally {
                  this.getPlayer().unlockSummonsReadLock();
               }

               this.craftJavelinVIStack = 0;
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.CraftEnchantJavelin, 152141000, Integer.MAX_VALUE, 0);
            }

            if (this.getActiveSkillID() == 152141000) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initCraftJabelin(useTick, new Rect(effect.getLt(), effect.getRb()), new Rect(effect.getLt2(), effect.getRb2()), effect.getRange(), maxBound);
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  this.getActiveSkillID(),
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.CraftJavelinVI,
                  Collections.singletonList(0),
                  1
               );
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            } else {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initCraftJabelin(useTick, new Rect(effect.getLt(), effect.getRb()), new Rect(effect.getLt2(), effect.getRb2()), effect.getRange(), maxBound);
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  this.getActiveSkillID(),
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.CRAFT_JABELIN,
                  Collections.singletonList(0),
                  1
               );
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 152101003:
            packet.skip(5);
            short x = packet.readShort();
            short y = packet.readShort();

            try {
               for (Summoned summonxxx : this.getPlayer().getSummonsReadLock()) {
                  if (summonxxx.getMoveAbility() == SummonMoveAbility.SHADOW_SERVANT_EXTEND) {
                     this.getPlayer()
                        .getMap()
                        .broadcastMessage(
                           this.getPlayer(),
                           CField.summonCrystalMove(this.getPlayer().getId(), summonxxx.getObjectId(), this.getActiveSkillID(), new Point(x, y)),
                           true
                        );
                     return;
                  }
               }
               break;
            } finally {
               this.getPlayer().unlockSummonsReadLock();
            }
         case 152101004:
            packet.skip(5);
            x = packet.readShort();
            y = packet.readShort();

            try {
               for (Summoned summonxx : this.getPlayer().getSummonsReadLock()) {
                  if (summonxx.getMoveAbility() == SummonMoveAbility.SHADOW_SERVANT_EXTEND) {
                     this.getPlayer()
                        .getMap()
                        .broadcastMessage(
                           this.getPlayer(), CField.summonCrystalTeleport(this.getPlayer().getId(), summonxx.getObjectId(), new Point(x, y)), true
                        );
                     return;
                  }
               }
               break;
            } finally {
               this.getPlayer().unlockSummonsReadLock();
            }
         case 152110004:
         case 152141004:
         case 152141005:
            SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(152120001);
            if (ex != null) {
               packet.skip(4);
               int u = packet.readByte();
               int maxBoundx = packet.readInt();
               int tick = packet.readInt();
               List<MapleMapObject> objects = this.getPlayer()
                  .getMap()
                  .getMapObjectsInRange(this.getPlayer().getPosition(), 320000.0, Arrays.asList(MapleMapObjectType.MONSTER));
               if (!objects.isEmpty()) {
                  CollectionUtil.sortMonsterByBossObjectHP(objects);
               }

               List<Integer> list = new LinkedList<>();

               for (int i = 0; i < objects.size() && list.size() < 5; i++) {
                  MapleMonster mob_ = (MapleMonster)objects.get(i);
                  list.add(mob_.getObjectId());
               }

               ForceAtom.AtomType atomType = ForceAtom.AtomType.GLORY_WING_JABELIN;
               if (this.getActiveSkillID() == 152141004 || this.getActiveSkillID() == 152141005) {
                  atomType = ForceAtom.AtomType.GloryWingJavelin_VI;
               }

               if (this.getActiveSkillID() != 152141005) {
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initGloryWingJabelin(this.getActiveSkillID(), new Rect(effect.getLt2(), effect.getRb2()), tick, effect.getRange());
                  ForceAtom forceAtom = new ForceAtom(
                     info, this.getActiveSkillID(), this.getPlayer().getId(), false, true, this.getPlayer().getId(), atomType, list, ex.getU()
                  );
                  this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
               } else {
                  for (int i = 0; i < 3; i++) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initGloryWingJabelin(this.getActiveSkillID(), new Rect(effect.getLt2(), effect.getRb2()), tick, effect.getRange());
                     info.initForceAtomFromData(2, 91);
                     ForceAtom forceAtom = new ForceAtom(
                        info, this.getActiveSkillID(), this.getPlayer().getId(), false, true, this.getPlayer().getId(), atomType, list, ex.getU()
                     );
                     this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
                  }

                  this.gloryWingJavelinVIStack = 0;
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.GloryWingEnchantJavelin, 152141004, Integer.MAX_VALUE, 0);
               }

               effect.applyTo(this.getPlayer(), true);
            }
            break;
         case 152121011:
            try {
               for (Summoned summonx : this.getPlayer().getSummonsReadLock()) {
                  if (summonx.getMoveAbility() == SummonMoveAbility.SHADOW_SERVANT_EXTEND) {
                     for (int i = 0; i < 50; i++) {
                        this.handleCrystalCharge(152121004, summonx);
                     }
                     break label785;
                  }
               }
               break;
            } finally {
               this.getPlayer().unlockSummonsReadLock();
            }
         case 152121041:
            Point pos = packet.readPos();
            short delay = packet.readShort();
            packet.skip(1);
            byte flip = packet.readByte();
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), flip == 1);
            AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(152121041, this.getPlayer().getId());
            if (area != null) {
               this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 152121041, false));
               this.getPlayer().getMap().removeMapObject(area);
            }

            AffectedArea aa = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration() / 1000);
            aa.setRlType(flip);
            this.getPlayer().getMap().spawnMist(aa);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 152141500:
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.GloryWing) != null) {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeTill(SecondaryStatFlag.indieBDR, 152111003, 10000);
               statManager.changeTill(SecondaryStatFlag.indieStance, 152111003, 10000);
               statManager.changeTill(SecondaryStatFlag.indiePMDR, 152111003, 10000);
               statManager.changeTill(SecondaryStatFlag.NewFlying, 152111003, 10000);
               statManager.changeTill(SecondaryStatFlag.indieFlyAcc, 152111003, 10000);
               statManager.changeTill(SecondaryStatFlag.GloryWing, 152111003, 10000);
               statManager.temporaryStatSet();
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.UnlimitedCrystal, 152141500, effect.getDuration(), 0);
            this.unlimitedCrystalStack = 0;
            break;
         case 152141501:
            pos = new Point(packet.readShort(), packet.readShort());
            byte faceLeft = packet.readByte();

            for (int cPos = 0; cPos < 6; cPos++) {
               Summoned summonxxxx = new Summoned(
                  this.getPlayer(), 152141505, this.getActiveSkillLevel(), pos, SummonMoveAbility.SHADOW_SERVANT_EXTEND, faceLeft, effect.getDuration()
               );
               summonxxxx.setCrystalPos(cPos);
               this.getPlayer().getMap().spawnSummon(summonxxxx, effect.getDuration());
               this.getPlayer().addSummon(summonxxxx);
            }

            for (int cPos = 0; cPos < 18; cPos++) {
               Summoned summonxxxx = new Summoned(
                  this.getPlayer(), 152141501, this.getActiveSkillLevel(), pos, SummonMoveAbility.SHADOW_SERVANT_EXTEND, faceLeft, effect.getDuration()
               );
               summonxxxx.setCrystalPos(cPos);
               this.getPlayer().getMap().spawnSummon(summonxxxx, effect.getDuration());
               this.getPlayer().addSummon(summonxxxx);
            }
            break;
         case 152141502:
            List<SecondAtom.Atom> atoms = new ArrayList<>();

            for (TeleportAttackElement e : this.teleportAttackAction.actions) {
               if (e.data != null && e.data instanceof TeleportAttackData_ListLong) {
                  TeleportAttackData_ListLong data = (TeleportAttackData_ListLong)e.data;
                  int cnt = 0;

                  for (long ltest : data.getLongList()) {
                     int xpos = (int)ltest;
                     int ypos = (int)(ltest >> 32);
                     pos = new Point(xpos, ypos);
                     SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        152141502,
                        SecondAtom.SN.getAndAdd(1),
                        cnt++ < 10 ? SecondAtom.SecondAtomType.UnlimitedCrystal : SecondAtom.SecondAtomType.UnlimitedCrystal2,
                        0,
                        null,
                        pos
                     );
                     SecondAtomData dd = skill.getSecondAtomData();
                     a.setPlayerID(this.getPlayer().getId());
                     a.setExpire(dd.getExpire());
                     a.setAttackableCount(1);
                     a.setEnableDelay(dd.getEnableDelay());
                     a.setCreateDelay(dd.getCreateDelay());
                     a.setSkillID(152141502);
                     int atomangle = -30 + ThreadLocalRandom.current().nextInt(61);
                     a.setAngle(atomangle);
                     this.getPlayer().addSecondAtom(a);
                     atoms.add(a);
                  }
               }
            }

            if (atoms.size() > 0) {
               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 152141502, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
            }
            break;
         case 400021099:
         case 500061066:
            if (this.crystalGateNum == 0) {
               this.crystalGateNum = effect.getProb();
            }

            int fieldID = this.getPlayer().getMapId();
            long till = this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.CrystalGate);
            int remainDuration = 0;
            if (till == 0L) {
               remainDuration = effect.getDuration();
            } else {
               remainDuration = (int)(till - System.currentTimeMillis());
            }

            CrystalGate gate = new CrystalGate(
               this.getPlayer().getId(),
               4 - this.crystalGateNum,
               this.getActiveSkillID(),
               fieldID,
               this.getPlayer().getTruePosition().x,
               this.getPlayer().getTruePosition().y,
               remainDuration,
               CrystalGate.GateType.CrystalGate
            );
            this.getPlayer().send(CField.createCrystalGate(this.getPlayer().getId(), Collections.singletonList(gate)));
            this.crystalGateNum--;
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CrystalGate) != null) {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.CrystalGate, this.getActiveSkillID(), 1);
               statManager.temporaryStatSet();
            } else {
               this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getDuration(), SecondaryStatFlag.CrystalGate, 1);
            }
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.GloryWing) != null && this.getPlayer().checkInterval(this.harmonyWingBeatLastTime, 2000)) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(152120008);
         if (e != null) {
            this.doHarmonyWingBeat(e);
            this.harmonyWingBeatLastTime = System.currentTimeMillis();
         }
      }
   }

   public void doHarmonyWingBeat(SecondaryStatEffect effect) {
      List<MapleMapObject> targets = new ArrayList<>(
         this.getPlayer()
            .getMap()
            .getMobsInRect(
               this.getPlayer().getTruePosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y, this.getPlayer().isFacingLeft()
            )
      );

      for (MapleCharacter player : this.getPlayer()
         .getMap()
         .getPlayerInRect(this.getPlayer().getTruePosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y)) {
         if (player.getId() != this.getPlayer().getId()
            && player.getParty() != null
            && this.getPlayer().getParty() != null
            && player.getParty().getId() == this.getPlayer().getParty().getId()) {
            targets.add(player);
         }
      }

      Collections.shuffle(targets);
      List<MapleMonster> targetMobs = new ArrayList<>();
      List<MapleCharacter> targetPlayers = new ArrayList<>();

      for (MapleMapObject t : targets) {
         if (t instanceof MapleCharacter) {
            targetPlayers.add((MapleCharacter)t);
         } else {
            if (targetMobs.size() >= 12) {
               break;
            }

            targetMobs.add((MapleMonster)t);
         }
      }

      for (MapleMonster mob : targetMobs) {
         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
         info.initHarmonyWingBeatToMob();
         ForceAtom forceAtom = new ForceAtom(
            info,
            152120017,
            this.getPlayer().getId(),
            false,
            true,
            this.getPlayer().getId(),
            ForceAtom.AtomType.HARMONY_WING_BEAT_TO_MOB,
            Collections.singletonList(mob.getObjectId()),
            1
         );
         this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
         this.getPlayer().getMap().addForceAtom(forceAtom.getKey(), forceAtom);
      }

      for (MapleCharacter playerx : targetPlayers) {
         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
         info.initHarmonyWingBeatToUser();
         ForceAtom forceAtom = new ForceAtom(
            info,
            152120018,
            this.getPlayer().getId(),
            false,
            false,
            this.getPlayer().getId(),
            ForceAtom.AtomType.HARMONY_WING_BEAT_TO_USER,
            Collections.singletonList(playerx.getId()),
            1
         );
         this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
         this.getPlayer().getMap().addForceAtom(forceAtom.getKey(), forceAtom);
      }
   }

   public void skillUpdatePerTick(int skillID, PacketDecoder slea) {
      int size = 0;
      if (slea.available() >= 4L) {
         size = slea.readInt();
      }

      if (skillID == 400021089) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021087).getEffect(this.getPlayer().getTotalSkillLevel(400021087));
         if (effect == null) {
            return;
         }

         for (CrystalGate gate : new ArrayList<>(this.getPlayer().getCrystalGates())) {
            if (System.currentTimeMillis() - gate.getCreateTime() >= gate.getRemainDuration()) {
               this.getPlayer().removeCrystalGate(gate.getTotalCount());
            }
         }

         if (this.getPlayer().getCrystalGates().size() >= effect.getU()) {
            return;
         }

         List<CrystalGate> gates = new ArrayList<>();

         for (int i = 0; i < size && this.getPlayer().getCrystalGates().size() < effect.getU(); i++) {
            CrystalGate gatex = new CrystalGate(
               this.getPlayer().getId(),
               this.crystalGateNum + 1,
               skillID,
               this.getPlayer().getMap().getId(),
               slea.readInt(),
               slea.readInt(),
               effect.getV() * 1000,
               CrystalGate.GateType.CrystalGate
            );
            gates.add(gatex);
            this.getPlayer().addCrystalGate(gatex);
            this.crystalGateNum++;
         }

         this.getPlayer().send(CField.createCrystalGate(this.getPlayer().getId(), gates));
      }
   }

   public int getCrystalGateNum() {
      return this.crystalGateNum;
   }

   public void summonedEnergyStackRequest(PacketDecoder slea) {
      int skillID = slea.readInt();
      int realSkillID = skillID;
      if (skillID == 152001001 || skillID == 152120001 || skillID == 152121004 || skillID == 152141000) {
         skillID = 152110001;
      } else if (skillID == 152001002 || skillID == 152120003) {
         skillID = 152100002;
         if (this.getPlayer().getSkillLevel(152110002) > 0) {
            skillID = 152110002;
         }
      }

      try {
         for (Summoned summon : this.getPlayer().getSummonsReadLock()) {
            if (summon.getMoveAbility() == SummonMoveAbility.SHADOW_SERVANT_EXTEND) {
               if ((realSkillID == 152001002 || realSkillID == 152120003) && this.getPlayer().getCooldownLimit(skillID) <= 0L) {
                  this.getPlayer().getMap().broadcastMessage(this.getPlayer(), CField.summonSetEnergy(this.getPlayer(), summon, 3), true);
               }

               if ((skillID == 152100001 || skillID == 152110001) && this.getPlayer().getCooldownLimit(skillID) <= 0L) {
                  this.getPlayer()
                     .getMap()
                     .broadcastMessage(this.getPlayer(), CField.summonEnergyAttack(this.getPlayer().getId(), summon.getObjectId(), skillID), true);
               }

               this.handleCrystalCharge(realSkillID, summon);
               break;
            }
         }
      } finally {
         this.getPlayer().unlockSummonsReadLock();
      }
   }

   public void handleCrystalCharge(int skillID, Summoned summon) {
      int count = 0;
      if (skillID == 152001002 || skillID == 152120003) {
         count = 2;
      } else if (skillID == 152001001 || skillID == 152120001) {
         count = 1;
      } else if (skillID == 152121004) {
         count = 3;
      } else if (skillID == 152141002) {
         if (this.craftJavelinVIStack > 0) {
            count = 25;
         } else {
            count = 1;
         }
      }

      int level = summon.getEnergyLevel();
      int max = 30;
      if (this.getPlayer().getTotalSkillLevel(152110008) > 0) {
         max = 150;
      }

      summon.addEnergyCharge(count, max);
      if (summon.getEnergyCharge() >= 30 && summon.getEnergyCharge() < 60 && level == 0
         || summon.getEnergyCharge() >= 60 && summon.getEnergyCharge() < 90 && level == 1
         || summon.getEnergyCharge() >= 90 && summon.getEnergyCharge() < 150 && level == 2
         || summon.getEnergyCharge() >= 150 && level == 3) {
         summon.setEnableEnergySkill(summon.getEnergyLevel(), 1);
         this.getPlayer().getMap().broadcastMessage(CField.summonCrystalToggleSkill(this.getPlayer(), summon, 2));
      }

      this.getPlayer().getMap().broadcastMessage(this.getPlayer(), CField.summonSetEnergy(this.getPlayer(), summon, 2), true);
      this.getPlayer().getMap().broadcastMessage(this.getPlayer(), CField.summonEnergyUpdate(this.getPlayer().getId(), summon, 2), true);
      if (summon.getEnergyCharge() >= 150 && this.getPlayer().getBuffedValue(SecondaryStatFlag.CrystalChargeMax) == null) {
         int charge = 152110008;
         if (this.getPlayer().getSkillLevel(152120014) > 0) {
            charge = 152120014;
         }

         this.getPlayer().temporaryStatSet(charge, 10000, SecondaryStatFlag.CrystalChargeMax, 1);
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case CrystalGate:
            packet.writeInt(this.getCrystalGateNum());
            break;
         case SkillDamageR:
            List<Integer> skillList = List.of(152120001, 400021000);
            packet.writeInt(skillList.size());
            skillList.forEach(packet::writeInt);
            break;
         case CraftEnchantJavelin:
            packet.writeInt(this.craftJavelinVIStack);
            break;
         case GloryWingEnchantJavelin:
            packet.writeInt(this.gloryWingJavelinVIStack);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
