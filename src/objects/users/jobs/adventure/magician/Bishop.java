package objects.users.jobs.adventure.magician;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.GameServer;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.MobPacket;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.PostSkillEffect;
import objects.fields.ForceAtom;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_ListLong;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.ArrayMap;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class Bishop extends DefaultMagician {
   public int angelRayAttack = 0;
   private long lastFeatherTime = 0L;
   private long lastBlessEnsenbleTime = 0L;
   private long lastAngelOfLibraTime = 0L;
   private long libraRevocable = 0L;
   private long prayRevocable = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400021086) {
         effect = SkillFactory.getSkill(400021086).getEffect(attack.skillLevel);
         if (effect != null) {
            this.getPlayer().addMP(-effect.getMPCon());
         }
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
      if (attack.skillID != 2301002 || monster.getStats().getUndead()) {
         if (attack.skillID == 2301010) {
            Map<MobTemporaryStatFlag, MobTemporaryStatEffect> statups = new ArrayMap<>();
            MobTemporaryStatEffect eff2 = new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, -effect.getU(), 2301010, null, false);
            statups.put(MobTemporaryStatFlag.INDIE_PDR, eff2);
            monster.applyMonsterBuff(statups, 2301010, effect.getDuration(), null, Collections.EMPTY_LIST);
         }

         if (attack.skillID == 2321007 || attack.skillID == 2341000 || attack.skillID == 2341001) {
            Map<MobTemporaryStatFlag, MobTemporaryStatEffect> statups = new ArrayMap<>();
            int stack = monster.getIndieMdrStack();
            stack = Math.min(effect.getQ(), stack + 1);
            monster.setIndieMdrStack(stack);
            monster.setIndieMdrFrom(this.getPlayer().getId());
            MobTemporaryStatEffect eff2 = new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_MDR, effect.getS() * stack, 2321007, null, false);
            statups.put(MobTemporaryStatFlag.INDIE_MDR, eff2);
            this.getPlayer().getMap().broadcastMessage(MobPacket.mobAffected(monster.getObjectId(), 2321007, 450, attack.skillLevel));
            monster.applyMonsterBuff(statups, 2321007, effect.getDuration(), null, Collections.EMPTY_LIST);
         }

         super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
      }
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if ((attack.skillID == 2321007 || attack.skillID == 2341000 || attack.skillID == 2341001) && this.getPlayer().getTotalSkillLevel(2321015) > 0) {
         this.angelRayAttack++;
         if (this.angelRayAttack >= this.getPlayer().getSkillLevelDataOne(2321015, SecondaryStatEffect::getU)) {
            this.angelRayAttack = 0;
            Integer holyWaterValue = this.getPlayer().getBuffedValue(SecondaryStatFlag.HolyWater);
            if (holyWaterValue == null || holyWaterValue < this.getPlayer().getSkillLevelDataOne(2321015, SecondaryStatEffect::getW)) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.HolyWater, 0, Integer.MAX_VALUE, holyWaterValue != null ? holyWaterValue + 1 : 1);
            }
         }
      }

      if (attack.skillID == 2341000 && attack.allDamage.size() > 0) {
         int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AngelRay, 0) + 1;
         if (stack > 12) {
            this.getPlayer().send(CField.userBonusAttackRequest(2341001, true, Collections.emptyList()));
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.AngelRay);
         } else {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.AngelRay, 2341000, Integer.MAX_VALUE, stack);
         }
      }

      try {
         if (this.getPlayer().hasBuffBySkillID(2311015) && attack.targets > 0) {
            SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.TriumphFeather);
            if (e != null && this.getPlayer().getCooldownLimit(2311017) <= 0L) {
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               Skill sk = SkillFactory.getSkill(2311015);
               if (sk.getSkillList().contains(attack.skillID)
                  && this.getPlayer().checkInterval(this.lastFeatherTime, this.getPlayer().getSkillLevelDataOne(2311017, SecondaryStatEffect::getCoolTime))) {
                  Skill sk_ = SkillFactory.getSkill(2311017);
                  SecondAtomData data = sk_.getSecondAtomData();

                  for (SecondAtomData.atom atom : data.getAtoms()) {
                     this.lastFeatherTime = System.currentTimeMillis();
                     SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        2311017,
                        ForceAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.TriumphFeather,
                        0,
                        null,
                        attack.attackPosition
                     );
                     a.setRotate(atom.getRotate());
                     a.setExpire(atom.getExpire());
                     if ((attack.display & 32768) != 0) {
                        a.setAngle(-atom.getRotate());
                        a.setPos(new Point(attack.attackPosition.x - atom.getPos().x, attack.attackPosition.y + atom.getPos().y));
                     } else {
                        a.setAngle(atom.getRotate());
                        a.setPos(new Point(attack.attackPosition.x + atom.getPos().x, attack.attackPosition.y + atom.getPos().y));
                     }

                     a.setEnableDelay(atom.getEnableDelay());
                     a.setCreateDelay(atom.getCreateDelay());
                     int targetID = atoms.size() < attack.allDamage.size()
                        ? attack.allDamage.get(atoms.size()).objectid
                        : attack.allDamage.get(Randomizer.nextInt(attack.allDamage.size())).objectid;
                     a.setPlayerID(this.getPlayer().getId());
                     a.setTargetObjectID(targetID);
                     a.setAttackableCount(1);
                     a.setSkillID(2311017);
                     this.getPlayer().addSecondAtom(a);
                     atoms.add(a);
                  }

                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 2311017, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
                  this.getPlayer().send(CField.skillCooldown(2311017, e.getU2() * 1000));
                  this.getPlayer().addCooldown(2311017, System.currentTimeMillis(), e.getU2() * 1000);
               }
            }
         }
      } catch (Exception var20) {
         System.out.println("Bishop Err");
         var20.printStackTrace();
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 2311011:
            AffectedArea mist = new AffectedArea(
               effect.calculateBoundingBox(this.getPlayer().getTruePosition(), this.getPlayer().isFacingLeft()),
               this.getPlayer(),
               effect,
               this.getPlayer().getTruePosition(),
               System.currentTimeMillis() + effect.getDuration()
            );
            mist.setRemainHealCount(effect.getY());
            this.getPlayer().getMap().spawnMist(mist);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 2321007:
         case 2341000:
         case 2341001:
            int delta = (int)(this.getPlayer().getStat().getCurrentMaxHp() * 0.1);
            if (delta <= 0) {
               delta = 50000;
            }

            this.getPlayer().addHP(delta, true);
            Party party = this.getPlayer().getParty();
            if (party != null) {
               int rangeX = -700;
               int rangeY = 50;

               for (PartyMemberEntry pc : party.getPartyMemberList()) {
                  if (pc.isOnline() && pc.getChannel() == this.getPlayer().getClient().getChannel() && pc.getFieldID() == this.getPlayer().getMap().getId()) {
                     MapleCharacter victim = this.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterById(pc.getId());
                     if (victim != null && victim.getId() != this.getPlayer().getId() && victim.isAlive()) {
                        boolean applyBuff = false;
                        if (this.getPlayer().isFacingLeft()) {
                           if (this.getPlayer().getTruePosition().x + rangeX < victim.getTruePosition().x
                              && this.getPlayer().getTruePosition().x >= victim.getTruePosition().x
                              && this.getPlayer().getTruePosition().y + rangeY > victim.getTruePosition().y
                              && this.getPlayer().getTruePosition().y - rangeY < victim.getTruePosition().y) {
                              applyBuff = true;
                           }
                        } else if (this.getPlayer().getTruePosition().x - rangeX > victim.getTruePosition().x
                           && this.getPlayer().getTruePosition().x <= victim.getTruePosition().x
                           && this.getPlayer().getTruePosition().y + rangeY > victim.getTruePosition().y
                           && this.getPlayer().getTruePosition().y - rangeY < victim.getTruePosition().y) {
                           applyBuff = true;
                        }

                        if (applyBuff) {
                           victim.addHP((int)(victim.getStat().getCurrentMaxHp() * 0.2), false);
                        }
                     }
                  }
               }
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 2321015:
            List<Point> pointList = new ArrayList<>();
            short count = packet.readShort();

            for (int i = 0; i < count; i++) {
               pointList.add(new Point(packet.readInt(), packet.readInt()));
            }

            byte rlType = packet.readByte();

            for (Point point : pointList) {
               AffectedArea mistx = new AffectedArea(
                  new Rectangle(point.x - 40, point.y - 100, 80, 120),
                  this.getPlayer(),
                  effect,
                  point,
                  System.currentTimeMillis() + effect.getQ() * 1000L + (long)this.getPlayer().getStat().getTotalInt() / effect.getS2() * effect.getQ2() * 1000L
               );
               this.getPlayer().getMap().spawnMist(mistx);
            }

            this.getPlayer().temporaryStatReset(SecondaryStatFlag.HolyWater);
            this.angelRayAttack = 0;
            effect.applyTo(this.getPlayer(), true);
            break;
         case 2341500:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePMDR, this.getActiveSkillID(), 5000, 5);
            break;
         case 2341501:
            for (TeleportAttackElement act : this.teleportAttackAction.actions) {
               if (act.data != null && act.data instanceof TeleportAttackData_ListLong) {
                  TeleportAttackData_ListLong data = (TeleportAttackData_ListLong)act.data;
                  int value = 0;

                  for (long ltest : data.getLongList()) {
                     int xpos = (int)ltest;
                     int ypos = (int)(ltest >> 32);
                     int summonId = value == 0 ? 2341502 : (value == 1 ? 2341501 : 2341503);
                     Summoned tosummon = new Summoned(
                        this.getPlayer(),
                        summonId,
                        this.getActiveSkillLevel(),
                        new Point(xpos, ypos),
                        SummonMoveAbility.STATIONARY,
                        (byte)1,
                        effect.getDuration()
                     );
                     this.getPlayer().getMap().spawnSummon(tosummon, effect.getDuration());
                     this.getPlayer().addSummon(tosummon);
                     value++;
                  }
               }
            }
            break;
         case 400021003:
         case 500061002:
            SecondaryStatEffect pray = SkillFactory.getSkill(400021003).getEffect(this.getActiveSkillLevel());
            if (pray == null) {
               break;
            }

            long passedx = 0L;
            boolean commandLockx = this.getPlayer().getOneInfoQuestInteger(1544, "400021003") != 0;
            if (this.prayRevocable == 0L && !commandLockx) {
               pray.applyTo(this.getPlayer());
               this.prayRevocable = System.currentTimeMillis();
               break;
            }

            int originalDuration = pray.getDuration();
            this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
            if (!commandLockx) {
               passedx = System.currentTimeMillis() - this.prayRevocable;
            }

            this.getPlayer().send(CField.skillCooldown(400021003, pray.getCooldown(this.getPlayer()) - 60000));
            this.getPlayer().addCooldown(400021003, System.currentTimeMillis(), pray.getCooldown(this.getPlayer()) - 60000 - passedx);
            pray.setDuration((int)(pray.getDuration() - pray.getU2() * 1000 - passedx));
            pray.applyTo(this.getPlayer());
            this.prayRevocable = 0L;
            pray.setDuration(originalDuration);
            return;
         case 400021032:
            SecondaryStatEffect libra = SkillFactory.getSkill(400021032).getEffect(this.getActiveSkillLevel());
            if (libra == null) {
               break;
            }

            long passed = 0L;
            boolean commandLock = this.getPlayer().getOneInfoQuestInteger(1544, "400021032") != 0;
            if (this.libraRevocable == 0L && !commandLock) {
               libra.applyTo(this.getPlayer());
               this.libraRevocable = System.currentTimeMillis();
               break;
            }

            originalDuration = libra.getDuration();
            if (!commandLock) {
               passed = System.currentTimeMillis() - this.libraRevocable;
            }

            this.getPlayer().send(CField.skillCooldown(400021032, libra.getCooldown(this.getPlayer()) - 60000));
            this.getPlayer().addCooldown(400021032, System.currentTimeMillis(), libra.getCooldown(this.getPlayer()) - 60000 - passed);
            libra.setDuration((int)(libra.getDuration() - libra.getU2() * 1000 - passed));
            libra.applyTo(this.getPlayer());
            this.libraRevocable = 0L;
            libra.setDuration(originalDuration);
            return;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.getPlayer().getSkillLevel(2300009) > 0) {
         this.updateBlessEnsenble();
      }

      if (this.getPlayer().hasBuffBySkillID(2311012)) {
         this.getPlayer().changeCooldown(2311012, 1000L);
      }

      this.updateAngelOfLibra();
      this.updateRevocable();
   }

   private void updateRevocable() {
      if (!this.getPlayer().skillisCooling(400021032)) {
         SecondaryStatEffect libra = SkillFactory.getSkill(400021032).getEffect(400021032);
         if (libra != null
            && this.getPlayer().getSummonBySkillID(400021032) != null
            && this.libraRevocable != 0L
            && System.currentTimeMillis() >= this.libraRevocable + 5000L) {
            this.getPlayer().send(CField.skillCooldown(400021032, libra.getCooldown(this.getPlayer()) - libra.getX() * 1000));
            this.getPlayer().addCooldown(400021032, System.currentTimeMillis(), libra.getCooldown(this.getPlayer()) - 5000);
            this.libraRevocable = 0L;
         }
      }

      if (!this.getPlayer().skillisCooling(400021003)) {
         SecondaryStatEffect pray = SkillFactory.getSkill(400021003).getEffect(400021003);
         if (pray != null
            && this.getPlayer().hasBuffBySkillID(400021003)
            && this.prayRevocable != 0L
            && System.currentTimeMillis() >= this.prayRevocable + 5000L) {
            this.getPlayer().send(CField.skillCooldown(400021003, pray.getCooldown(this.getPlayer()) - pray.getX() * 1000));
            this.getPlayer().addCooldown(400021003, System.currentTimeMillis(), pray.getCooldown(this.getPlayer()) - 5000);
            this.prayRevocable = 0L;
         }
      }
   }

   public void updateBlessEnsenble() {
      if (this.lastBlessEnsenbleTime == 0L || System.currentTimeMillis() - this.lastBlessEnsenbleTime >= 3000L) {
         AtomicInteger massCount = new AtomicInteger(0);
         SecondaryStatFlag[] secs = new SecondaryStatFlag[]{
            SecondaryStatFlag.Bless, SecondaryStatFlag.HolySymbol, SecondaryStatFlag.HolyMagicShell, SecondaryStatFlag.AdvancedBless
         };

         for (SecondaryStatFlag sec : secs) {
            if (this.getPlayer().getBuffedValue(sec) != null) {
               massCount.addAndGet(1);
               break;
            }
         }

         if (this.getPlayer().getParty() != null) {
            this.getPlayer()
               .getParty()
               .getPartyMemberList()
               .stream()
               .filter(pchr -> pchr.getId() != this.getPlayer().getId())
               .filter(pchr -> pchr.getChannel() == this.getPlayer().getClient().getChannel())
               .filter(pchr -> pchr.getFieldID() == this.getPlayer().getMapId())
               .filter(PartyMemberEntry::isOnline)
               .forEach(pchr -> {
                  MapleCharacter player = GameServer.getInstance(this.getPlayer().getClient().getChannel()).getPlayerStorage().getCharacterById(pchr.getId());
                  new AtomicReference<>(false);
                  if (player != null) {
                     for (SecondaryStatFlag secx : secs) {
                        SecondaryStatEffect eff = player.getBuffedEffect(secx);
                        if (eff != null) {
                           massCount.addAndGet(1);
                           break;
                        }
                     }
                  }
               });
         }

         if (massCount.get() > 0) {
            SecondaryStatEffect effect = SkillFactory.getSkill(2300009).getEffect(this.getPlayer().getSkillLevel(2300009));
            if (effect != null) {
               int x = effect.getX();
               int y = 0;
               if (this.getPlayer().getJob() == 232) {
                  effect = SkillFactory.getSkill(2320013).getEffect(this.getPlayer().getSkillLevel(2320013));
                  if (effect != null) {
                     x = effect.getX();
                     y = effect.getY();
                  }
               }

               this.getPlayer().temporaryStatSet(2300009, Integer.MAX_VALUE, SecondaryStatFlag.BlessEnsenble, massCount.get() * x + y);
            }
         } else if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BlessEnsenble) != null) {
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.BlessEnsenble);
         }

         this.lastBlessEnsenbleTime = System.currentTimeMillis();
      }
   }

   private void updateAngelOfLibra() {
      Summoned summon = this.getPlayer().getSummonBySkillID(400021032);
      if (summon != null) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021032).getEffect(this.getPlayer().getSkillLevel(400021032));
         if (this.lastAngelOfLibraTime == 0L || System.currentTimeMillis() >= this.lastAngelOfLibraTime + effect.getX() * 1000) {
            List<MapleCharacter> toBuff = new ArrayList<>();
            toBuff.add(this.getPlayer());
            if (this.getPlayer().getParty() != null) {
               for (PartyMemberEntry mpc : this.getPlayer().getParty().getPartyMemberList()) {
                  if (mpc.isOnline() && mpc.getChannel() == this.getPlayer().getClient().getChannel() && mpc.getFieldID() == this.getPlayer().getMapId()) {
                     MapleCharacter hp = this.getPlayer().getMap().getCharacterById(mpc.getId());
                     if (hp != null) {
                        toBuff.add(hp);
                     }
                  }
               }
            }

            Map<SecondaryStatFlag, Integer> statups = new EnumMap<>(SecondaryStatFlag.class);
            statups.put(
               SecondaryStatFlag.indieDamR,
               Integer.max(0, Integer.min(effect.getDamage(), effect.getW() + this.getPlayer().getStat().getTotalInt() / effect.getAttackCount()))
            );

            for (MapleCharacter target : toBuff) {
               if (target.isAlive()) {
                  target.addHP((long)(target.getStat().getCurrentMaxHp(target) * (effect.getY() * 0.01)), false);
                  int localDuration = effect.getSubTime();
                  target.temporaryStatSet(400021052, effect.getLevel(), localDuration, statups);
                  PostSkillEffect e = new PostSkillEffect(target.getId(), 400021032, effect.getLevel(), null);
                  target.getClient().getSession().writeAndFlush(e.encodeForLocal());
                  target.getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
               }
            }

            this.lastAngelOfLibraTime = System.currentTimeMillis();
         }
      }
   }

   @Override
   public void afterActiveSkill() {
      super.afterActiveSkill();
   }
}
