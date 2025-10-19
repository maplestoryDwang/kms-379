package objects.users.jobs.resistance;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.context.party.Party;
import objects.effect.child.PostSkillEffect;
import objects.fields.ForceAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Warrior;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class DemonSlayer extends Warrior {
   private long lastVampiricTouchTime = 0L;
   private long lastMaxForceTime = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 31111003) {
         this.getPlayer().temporaryStatSet(31111003, 1000, SecondaryStatFlag.NotDamaged, 1);
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
      if (attack.skillID == 31111003) {
         int hp = (int)(effect.getX() * this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01);
         PacketEncoder e = new PacketEncoder();
         e.writeInt(hp);
         PostSkillEffect eff = new PostSkillEffect(this.getPlayer().getId(), attack.skillID, attack.skillLevel, e);
         this.getPlayer().send(eff.encodeForLocal());
         this.getPlayer().getMap().broadcastMessage(this.getPlayer(), eff.encodeForRemote(), false);
      }

      if (totalDamage > 0L) {
         this.tryCreateDemonForce(monster, attack.skillID, totalDamage);
         if (attack.skillID == 31121001) {
            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getX(), attack.skillID, null, false),
               false,
               effect.getDuration(),
               true,
               effect
            );
         } else if (attack.skillID == 31121003) {
            Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
            mse.put(MobTemporaryStatFlag.PAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.PAD, effect.getY(), attack.skillID, null, false));
            mse.put(MobTemporaryStatFlag.MAD, new MobTemporaryStatEffect(MobTemporaryStatFlag.MAD, effect.getY(), attack.skillID, null, false));
            mse.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, effect.getV(), attack.skillID, null, false));
            mse.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, effect.getV(), attack.skillID, null, false));
            mse.put(MobTemporaryStatFlag.BLIND, new MobTemporaryStatEffect(MobTemporaryStatFlag.BLIND, effect.getZ(), attack.skillID, null, false));
            mse.put(MobTemporaryStatFlag.SHOWDOWN, new MobTemporaryStatEffect(MobTemporaryStatFlag.SHOWDOWN, effect.getW(), attack.skillID, null, false));
            monster.applyMonsterBuff(mse, attack.skillID, effect.getSubTime(), null, Collections.EMPTY_LIST);
         }

         this.tryVampiricTouch(totalDamage);
      }

      if (attack.targets > 0 && this.getPlayer().hasBuffBySkillID(31141501)) {
         List<Integer> list = SkillFactory.getSkill(31141502).getSkillList();
         if (list.contains(attack.skillID) && this.getPlayer().getRemainCooltime(31141502) <= 0L) {
            List<Pair<Integer, Integer>> mList = new ArrayList<>();

            for (AttackPair dmg : attack.allDamage) {
               if (mList.size() >= 3) {
                  break;
               }

               if (this.getPlayer().getMap().getMonsterByOid(dmg.objectid) != null) {
                  mList.add(new Pair<>(dmg.objectid, 0));
               }
            }

            this.getPlayer().send(CField.userBonusAttackRequest(31141502, true, mList));
            this.getPlayer().giveCoolDowns(31141502, System.currentTimeMillis(), 300L);
            this.getPlayer().send(CField.skillCooldown(31141502, 300));
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 31000004 && this.getPlayer().getSkillLevel(31120045) > 0) {
         SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePMDR, 31120045);
         if (e == null) {
            this.getPlayer().temporaryStatSet(31120045, 15000, SecondaryStatFlag.indiePMDR, 10);
         }
      }

      if (attack.skillID == 31111003) {
         int hp = (int)(effect.getX() * this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01);
         this.getPlayer().addHP(hp);
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 400011110) {
         SecondaryStatEffect eff = SkillFactory.getSkill(this.getActiveSkillPrepareID()).getEffect(this.getActiveSkillPrepareSLV());
         if (eff != null) {
            this.getPlayer().temporaryStatSet(this.getActiveSkillPrepareID(), (int)(eff.getT() * 1000.0), SecondaryStatFlag.indiePartialNotDamaged, 1);
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 31101002) {
         packet.readInt();
         if (packet.readByte() == 1) {
            this.getPlayer()
               .giveCoolDowns(
                  this.getActiveSkillID(),
                  System.currentTimeMillis(),
                  this.getPlayer().getSkillLevelData(this.getActiveSkillID()).getCooldown(this.getPlayer())
               );
            this.getPlayer()
               .send(CField.skillCooldown(this.getActiveSkillID(), this.getPlayer().getSkillLevelData(this.getActiveSkillID()).getCooldown(this.getPlayer())));
         }
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 31121005:
            this.getPlayer().temporaryStatSet(31121005, 2750, SecondaryStatFlag.NotDamaged, 1);
            effect.applyTo(this.getPlayer());
            break;
         case 31141501:
            int duration = effect.getDuration();
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.Nightmare, this.getActiveSkillID(), duration, 1);
            break;
         case 400011077:
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            Summoned summon = new Summoned(
               this.getPlayer(),
               400011077,
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
               SummonMoveAbility.FOLLOW,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            Summoned summon2 = new Summoned(
               this.getPlayer(),
               400011078,
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
               SummonMoveAbility.FOLLOW,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration());
            this.getPlayer().getMap().spawnSummon(summon2, effect.getDuration(), false, false);
            this.getPlayer().addSummon(summon);
            this.getPlayer().addSummon(summon2);
            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      int slv = 0;
      if ((slv = this.getPlayer().getTotalSkillLevel(31110009)) > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(31110009).getEffect(slv);
         if (eff != null && (this.lastMaxForceTime == 0L || System.currentTimeMillis() - this.lastMaxForceTime >= 4000L)) {
            int delta = (int)(GameConstants.getMPByJob(this.getPlayer()) - this.getPlayer().getStat().getMp());
            if (delta > 0) {
               delta = Math.min(delta, eff.getY());
               this.getPlayer().addMP(delta, true);
            }

            this.lastMaxForceTime = System.currentTimeMillis();
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.InfinityForce) != null && this.getPlayer().getCooldownLimit(31121054) != 0L) {
         SecondaryStatEffect eff = SkillFactory.getSkill(31121054).getEffect(this.getPlayer().getTotalSkillLevel(31121054));
         if (eff != null) {
            this.getPlayer().changeCooldown(31121054, -eff.getQ() * 1000L);
         }
      }
   }

   public void tryCreateDemonForce(MapleMonster mob, int skillID, long totalDamage) {
      if (GameConstants.isDemonSlayer(this.getPlayer().getJob())) {
         SecondaryStatEffect curseOfFury = null;
         SecondaryStatEffect maxForce = null;
         int slv;
         if ((slv = this.getPlayer().getTotalSkillLevel(30010111)) > 0) {
            curseOfFury = SkillFactory.getSkill(30010111).getEffect(slv);
         }

         if ((slv = this.getPlayer().getTotalSkillLevel(31110009)) > 0) {
            maxForce = SkillFactory.getSkill(31110009).getEffect(slv);
         }

         if (mob.getHp() <= totalDamage || mob.getStats().isBoss()) {
            if (curseOfFury == null) {
               return;
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initDemonForce(this.calcDemonForceInc(skillID, false));
            ForceAtom forceAtom = new ForceAtom(
               info, 0, this.getPlayer().getId(), true, false, mob.getObjectId(), ForceAtom.AtomType.DEMON_FORCE, Collections.singletonList(0), 1
            );
            this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            this.getPlayer().getMap().addForceAtom(forceAtom.getKey(), forceAtom);
            int delta = (int)(GameConstants.getMPByJob(this.getPlayer()) - this.getPlayer().getStat().getMp());
            if (delta > 0) {
               delta = Math.min(delta, 10);
               this.getPlayer().addMP(delta, true);
            }
         }

         if (curseOfFury != null && curseOfFury.makeChanceResult()) {
            this.getPlayer().addHP((long)(this.getPlayer().getStat().getCurrentMaxHp() * curseOfFury.getZ() / 100.0));
         }

         if (maxForce != null) {
            if ((skillID == 31000004 || Math.abs(31001007 - skillID) <= 1 || Math.abs(400011008 - skillID) <= 1) && maxForce.makeChanceResult()) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initDemonForce(this.calcDemonForceInc(skillID, true));
               ForceAtom forceAtom = new ForceAtom(
                  info, 0, this.getPlayer().getId(), true, false, mob.getObjectId(), ForceAtom.AtomType.DEMON_FORCE, Collections.singletonList(0), 1
               );
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
               this.getPlayer().getMap().addForceAtom(forceAtom.getKey(), forceAtom);
            }
         }
      }
   }

   public int calcDemonForceInc(int skillID, boolean byMaxForce) {
      int force = Math.min(2, 1 + this.getPlayer().getLevel() / 60);
      if (Randomizer.isSuccess(20)) {
         force = force + 1 + this.getPlayer().getLevel() / 125;
      }

      if (skillID == 31121052) {
         force += 50;
      }

      if (byMaxForce) {
         int slv = 0;
         if ((slv = this.getPlayer().getTotalSkillLevel(31120043)) > 0) {
            SecondaryStatEffect eff = SkillFactory.getSkill(31120043).getEffect(slv);
            if (eff != null) {
               int extraForce = eff.getX();
               if (extraForce > 0) {
                  force += extraForce * force / 100;
               }
            }
         }
      }

      return force;
   }

   public void tryVampiricTouch(long totalDamage) {
      SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.VampiricTouch);
      if (effect != null
         && GameConstants.isDemonSlayer(this.getPlayer().getJob())
         && effect.getSourceId() == 31121002
         && (this.lastVampiricTouchTime == 0L || System.currentTimeMillis() - this.lastVampiricTouchTime >= effect.getY() * 1000L)) {
         this.doVampiricTouch(this.getPlayer(), totalDamage, effect.getX(), effect.getW());
         Party party = this.getPlayer().getParty();
         if (party != null) {
            party.getPartyMemberList().stream().filter(p -> p.getId() != this.getPlayer().getId()).collect(Collectors.toList()).forEach(p -> {
               MapleCharacter target = this.getPlayer().getMap().getCharacterById(p.getId());
               if (target != null) {
                  this.doVampiricTouch(target, totalDamage, effect.getX(), effect.getW());
               }
            });
         }

         this.lastVampiricTouchTime = System.currentTimeMillis();
      }
   }

   public void doVampiricTouch(MapleCharacter player, long totalDamage, int x, int w) {
      int num = (int)Math.min(player.getStat().getCurrentMaxHp(player) * w / 100L, totalDamage * x / 100L);
      player.addHP(num);
   }
}
