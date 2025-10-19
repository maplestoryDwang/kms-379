package objects.users.jobs.flora;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.PostSkillAffected;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Warrior;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_TriArray;
import objects.users.skills.TeleportAttackData_TriArray_Elem;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Adele extends Warrior {
   private long lastAttributeTime = 0L;
   private long lastShardByWonderTime = 0L;
   private long lastCreationAttackTime = 0L;
   private long lastAddEtherTime = 0L;
   private long lastCreateEtherCrystalTime = 0L;
   private long dikeChargingStartTime = 0L;

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
      RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 151111002) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> list = new HashMap<>();
         list.put(MobTemporaryStatFlag.INDIE_PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, -effect.getX(), 151111002, null, false));
         list.put(MobTemporaryStatFlag.INDIE_MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_MDR, -effect.getX(), 151111002, null, false));
         monster.applyMonsterBuff(list, 151111002, effect.getDuration(), null, Collections.EMPTY_LIST);
      }

      if (attack.skillID == 151121001) {
         this.getPlayer().temporaryStatSet(151121001, Integer.MAX_VALUE, SecondaryStatFlag.Divide, monster.getObjectId());
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400011109 || attack.skillID == 500061065) {
         PostSkillAffected e = new PostSkillAffected(this.getPlayer().getId(), attack.skillID, attack.skillLevel);
         this.getPlayer().send(e.encodeForLocal());
         this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
      }

      if (attack.targets > 0) {
         if (GameConstants.isTriggerSkill(attack.skillID)) {
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(151100017);
            if (eff != null) {
               this.getPlayer().addEtherPoint(eff.getS());
            }

            eff = this.getPlayer().getSkillLevelData(151101006);
            if (eff != null) {
               int term = (int)(eff.getT() * 1000.0);
               if (this.getPlayer().getJob() == 15111) {
                  term -= eff.getS() * 1000;
               } else if (this.getPlayer().getJob() == 15112) {
                  term -= eff.getS() * 2000;
               }

               if (this.getPlayer().checkInterval(this.lastCreationAttackTime, term)) {
                  for (SecondAtom.Atom atom : new ArrayList<>(this.getPlayer().getSecondAtoms())) {
                     if (atom.getType().getType() >= SecondAtom.SecondAtomType.Creation1.getType()
                        && atom.getType().getType() <= SecondAtom.SecondAtomType.Creation6.getType()) {
                        this.getPlayer()
                           .send(
                              CField.secondAtomAttack(this.getPlayer().getId(), atom.getKey(), atom.getType() == SecondAtom.SecondAtomType.Creation1 ? 3 : 0)
                           );
                     }
                  }

                  if (this.getPlayer().getInnerSkillLevel(70000045) > 0) {
                     SecondaryStatEffect reduce = SkillFactory.getSkill(70000045).getEffect(this.getPlayer().getInnerSkillLevel(70000045));
                     if (reduce != null) {
                        int nocoolProp = reduce.getNocoolProp();
                        if (!Randomizer.isSuccess(nocoolProp)) {
                           this.lastCreationAttackTime = System.currentTimeMillis();
                        }
                     } else {
                        this.lastCreationAttackTime = System.currentTimeMillis();
                     }
                  } else {
                     this.lastCreationAttackTime = System.currentTimeMillis();
                  }
               }
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Wonder) != null) {
               int skillID = this.getPlayer().getBuffedValue(SecondaryStatFlag.Wonder) == 1 ? 151101013 : 151141003;
               SecondaryStatEffect e = this.getPlayer().getSkillLevelData(skillID);
               if (eff != null && this.getPlayer().checkInterval(this.lastShardByWonderTime, e.getX() * 1000)) {
                  SecondAtom secondAtom = new SecondAtom(
                     this.getPlayer().getMap(),
                     this.getPlayer(),
                     skillID == 151101013 ? 151001001 : 151141002,
                     skillID == 151101013 ? SecondAtom.SecondAtomType.Shard : SecondAtom.SecondAtomType.ShardVI,
                     SkillFactory.getSkill(skillID == 151101013 ? 151001001 : 151141002),
                     this.getPlayer().getTruePosition()
                  );
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
                  this.getPlayer().addMP(-e.getMPCon());
                  this.lastShardByWonderTime = System.currentTimeMillis();
               }
            }
         }

         if (attack.skillID == 151101009) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(151100002);
            if (e != null) {
               this.tryCreateEtherCrystal(e.getProb(), attack.forcedPos, false);
            }
         }

         if (attack.skillID == 151111002) {
            this.tryCreateEtherCrystal(effect.getS(), attack.forcedPos, false);
         }

         if (attack.skillID == 151111003) {
            this.tryCreateEtherCrystal(effect.getProb(), attack.forcedPos, false);
         }

         if (attack.skillID == 151121003) {
            this.tryCreateEtherCrystal(effect.getProb(), attack.forcedPos, false);
         }

         if (attack.skillID == 400011108) {
            this.tryCreateEtherCrystal(effect.getProb(), attack.forcedPos, false);
         }

         if (attack.skillID == 400011109 || attack.skillID == 500061065) {
            double t = effect.getT();
            Party party = this.getPlayer().getParty();
            if (party != null) {
               for (PartyMemberEntry mpc : party.getPartyMemberList()) {
                  if (mpc.isOnline() && mpc.getChannel() == this.getPlayer().getClient().getChannel() && mpc.getFieldID() == this.getPlayer().getMapId()) {
                     MapleCharacter chr = this.getPlayer().getMap().getCharacterById(mpc.getId());
                     if (chr != null) {
                        int hp = (int)(chr.getStat().getCurrentMaxHp(chr) * 0.01 * t);
                        chr.addHP(hp, false);
                     }
                  }
               }
            } else {
               int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * t);
               this.getPlayer().addHP(hp, false);
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 151121004) {
         this.dikeChargingStartTime = System.currentTimeMillis();
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(151121004);
         if (eff != null) {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieFlyAcc, 1);
            statups.put(SecondaryStatFlag.indieDamReduceR, -eff.getX());
            statups.put(SecondaryStatFlag.indieSuperStance, 1);
            statups.put(SecondaryStatFlag.AntiMagicShell, 1);
            statups.put(SecondaryStatFlag.KeyDownStart, 1);
            this.getPlayer().temporaryStatSet(151121004, this.getActiveSkillPrepareSLV(), eff.getUpdatableTime(), statups);
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 151001001:
         case 151141002: {
            packet.skip(4);
            byte targetNum = packet.readByte();

            for (int i = 0; i < targetNum; i++) {
               int var42 = packet.readInt();
            }

            packet.skip(3);
            Point pos = new Point(packet.readInt(), packet.readInt());
            SecondAtom secondAtom = new SecondAtom(
               this.getPlayer().getMap(),
               this.getPlayer(),
               this.getActiveSkillID(),
               this.getActiveSkillID() == 151001001 ? SecondAtom.SecondAtomType.Shard : SecondAtom.SecondAtomType.ShardVI,
               SkillFactory.getSkill(this.getActiveSkillID()),
               pos
            );
            this.getPlayer().getMap().createSecondAtom(secondAtom);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 151100002: {
            Point pos = packet.readPos();
            this.tryCreateEtherCrystal(100, pos, true);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 151101003:
         case 151101004:
            int objectID = 0;
            if (this.getActiveSkillID() == 151101003) {
               objectID = packet.readInt();
            }

            Summoned summon = (Summoned)this.getPlayer().getMap().getMapObject(objectID, MapleMapObjectType.SUMMON);
            if (summon != null) {
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
               this.getPlayer().getMap().removeMapObject(summon);
               this.getPlayer().removeVisibleMapObject(summon);
               this.getPlayer().removeSummon(summon);
            }

            Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.Resonance);
            int v = 0;
            if (value != null) {
               v = value;
            }

            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(151101010);
            if (e != null) {
               v = Math.min(e.getX(), v + 1);
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               statups.put(SecondaryStatFlag.Resonance, v);
               statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, v * e.getY());
               this.getPlayer()
                  .temporaryStatSet(151101010, this.getPlayer().getTotalSkillLevel(151101010), effect.getDuration(e.getDuration(), this.getPlayer()), statups);
            }

            e = this.getPlayer().getSkillLevelData(151120034);
            if (e != null) {
               this.getPlayer().addEtherPoint(e.getX());
            }
            break;
         case 151101006: {
            boolean disable = this.getPlayer().hasBuffBySkillID(this.getActiveSkillID());
            Point pos = packet.readPos();
            this.getPlayer().tryCreateEtherCreation(pos, disable);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 151101013:
         case 151141003:
            int valuex = this.getActiveSkillID() == 151101013 ? 1 : 2;
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Wonder) == null) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.Wonder, this.getActiveSkillID(), Integer.MAX_VALUE, valuex);
            } else {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.Wonder);
            }
            break;
         case 151111001: {
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            Summoned summoned = this.getPlayer().getSummonBySkillID(151111001);
            if (summoned != null) {
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summoned, true));
               this.getPlayer().getMap().removeMapObject(summoned);
               this.getPlayer().removeVisibleMapObject(summoned);
               this.getPlayer().removeSummon(summoned);
            }

            summon = new Summoned(
               this.getPlayer(),
               151111001,
               this.getActiveSkillLevel(),
               pos,
               SummonMoveAbility.STATIONARY,
               flip,
               System.currentTimeMillis() + effect.getDuration(effect.getDuration(), this.getPlayer())
            );
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration(effect.getDuration(), this.getPlayer()));
            this.getPlayer().addSummon(summon);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 151111003: {
            byte targetNum = packet.readByte();

            for (int i = 0; i < targetNum; i++) {
               int var38 = packet.readInt();
            }

            packet.skip(3);
            Point pos = new Point(packet.readInt(), packet.readInt());
            int etherIndex = Math.min(2, this.getPlayer().getEtherPoint() / 100 - 1);
            if (etherIndex >= 0) {
               SecondAtom secondAtom = new SecondAtom(
                  this.getPlayer().getMap(),
                  this.getPlayer(),
                  151111003,
                  SecondAtom.SecondAtomType.Order,
                  SkillFactory.getSkill(this.getActiveSkillID()),
                  pos,
                  etherIndex
               );
               this.getPlayer().getMap().createSecondAtom(secondAtom);
               this.getPlayer().addEtherPoint(-100);
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 151121041:
            for (TeleportAttackElement ex : this.teleportAttackAction.actions) {
               TeleportAttackData_TriArray data = (TeleportAttackData_TriArray)ex.data;
               if (data != null) {
                  for (TeleportAttackData_TriArray_Elem d : data.data) {
                     int objectIDx = d.objectID;
                     Point posx = new Point(d.x, d.y);
                     Rect rect = new Rect(posx, effect.getLt2(), effect.getRb2(), false);
                     AffectedArea aa = new AffectedArea(rect, this.getPlayer(), effect, posx, System.currentTimeMillis() + 2000L);
                     this.getPlayer().getMap().spawnMist(aa);
                  }
               }
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 400011108: {
            byte targetNum = packet.readByte();

            for (int i = 0; i < targetNum; i++) {
               int index = packet.readInt();
            }

            packet.skip(3);
            Point pos = new Point(packet.readInt(), packet.readInt());
            int index = 0;
            List<SecondAtom.Atom> atoms = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
               int minX = effect.getLt().x;
               int maxX = effect.getRb().x;
               int minY = effect.getLt().y;
               int maxY = effect.getRb().y;
               int randX = Randomizer.rand(pos.x + minX, pos.x + maxX);
               int randY = Randomizer.rand(pos.y + minY, pos.y + maxY);
               SecondAtom.Atom atom = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  400011108,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.Ruin,
                  index++,
                  1320,
                  31000,
                  40,
                  new Point(randX, randY)
               );
               atoms.add(atom);
            }

            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), this.getActiveSkillID(), atoms);
            secondAtom.setShowEffect(1);
            this.getPlayer().getMap().createSecondAtom(secondAtom);
            this.getPlayer().temporaryStatSet(400011108, 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400011109:
         case 500061065:
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            if (this.getActiveSkillID() == 500061065) {
               statups.put(SecondaryStatFlag.indiePMDR, effect.getU());
            }

            statups.put(SecondaryStatFlag.indieDamR, effect.getY());
            statups.put(SecondaryStatFlag.Restore, 1);
            this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getLevel(), effect.getDuration(), statups);
            break;
         case 400011136:
            int size = 0;
            size += this.getPlayer().getSecondAtomCount(SecondAtom.SecondAtomType.Order);
            this.getPlayer().addEtherPoint(30 * size);
            this.getPlayer().temporaryStatSet(400011136, effect.getDuration(effect.getDuration(), this.getPlayer()), SecondaryStatFlag.DevilishPower, size);
            effect.applyTo(this.getPlayer());
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 151121004) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(151121004);
         if (eff != null) {
            long gap = 10000L - (System.currentTimeMillis() - this.dikeChargingStartTime);
            this.getPlayer().changeCooldown(151121004, -((int)(gap * eff.getT())));
         }

         this.getPlayer().temporaryStatResetBySkillID(151121004);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(151100017);
      if (effect != null) {
         int w = effect.getW();
         if (this.getPlayer().checkInterval(this.lastAddEtherTime, w * 1000)) {
            int z = effect.getZ();
            this.getPlayer().addEtherPoint(z);
            this.lastAddEtherTime = System.currentTimeMillis();
         }
      }

      effect = this.getPlayer().getSkillLevelData(151100015);
      if (effect != null && this.getPlayer().checkInterval(this.lastAttributeTime, effect.getW() * 1000) && this.getPlayer().isAlive()) {
         int x = effect.getX();
         int extra = this.getPlayer().getSkillLevelDataOne(151120037, SecondaryStatEffect::getX);
         x += extra;
         int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
         int mp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * x);
         this.getPlayer().addMPHP(hp, mp);
         this.lastAttributeTime = System.currentTimeMillis();
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Restore) != null) {
         int skillID = 400011109;
         if (this.getPlayer().getTotalSkillLevel(500061065) > 0) {
            skillID = 500061065;
         }

         effect = this.getPlayer().getSkillLevelData(skillID);
         if (effect != null) {
            int mpRCon = effect.getMpRCon();
            int mp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * mpRCon);
            this.getPlayer().addMP(-mpRCon);
         }
      }
   }

   public void tryCreateEtherCrystal(int prob, Point pos, boolean exclusive) {
      SecondaryStatEffect e = this.getPlayer().getSkillLevelData(151100002);
      if (e != null
         && (this.getPlayer().checkInterval(this.lastCreateEtherCrystalTime, e.getCooldown(this.getPlayer())) || exclusive)
         && Randomizer.isSuccess(prob)) {
         Point p = pos;
         int minX = e.getLt().x;
         int maxX = e.getRb().x;
         int minY = e.getLt().y;
         int maxY = e.getRb().y;
         int count = 0;
         boolean check = false;

         do {
            if (this.getPlayer().getMap().getSummonedInRect(this.getPlayer().getId(), 151100002, p, -250, -250, 250, 250).isEmpty()) {
               check = true;
               break;
            }
         } while (++count <= 100);

         if (check) {
            if (this.getPlayer().getSummonedSize(151100002) >= e.getX()) {
               List<Summoned> summons = new ArrayList<>(this.getPlayer().getSummons());
               Summoned summon = summons.stream()
                  .sorted((a, b) -> (int)(a.getSummonRemoveTime() - b.getSummonRemoveTime()))
                  .filter(s -> s.getSkill() == 151100002)
                  .findFirst()
                  .orElse(null);
               if (summon != null) {
                  this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                  this.getPlayer().getMap().removeMapObject(summon);
                  this.getPlayer().removeVisibleMapObject(summon);
                  this.getPlayer().removeSummon(summon);
               }
            }

            Summoned summon = new Summoned(this.getPlayer(), 151100002, e.getLevel(), p, SummonMoveAbility.STATIONARY, (byte)0, e.getDuration());
            this.getPlayer().getMap().spawnSummon(summon, e.getDuration());
            this.getPlayer().addSummon(summon);
         }

         this.lastCreateEtherCrystalTime = System.currentTimeMillis();
      }
   }

   public void skillUpdatePerTick(int skillID, PacketDecoder slea) {
      if (skillID == 400011109 || skillID == 500061065) {
         SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getTotalSkillLevel(skillID));
         this.getPlayer().addMP(-effect.calcMPChange(this.getPlayer(), true));
      }
   }
}
