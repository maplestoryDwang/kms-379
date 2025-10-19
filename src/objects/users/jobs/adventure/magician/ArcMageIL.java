package objects.users.jobs.adventure.magician;

import constants.GameConstants;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.Element;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_ListLong;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Rect;
import objects.utils.Timer;

public class ArcMageIL extends DefaultMagician {
   private ScheduledFuture<?> iceAuraTask = null;
   private boolean upgradeAura = false;

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
      if (GameConstants.isColdSkill(attack.skillID)) {
         MobTemporaryStatEffect eff = monster.getBuff(MobTemporaryStatFlag.SPEED);
         int stack = 0;
         if (eff != null) {
            stack = eff.getValue();
         }

         int duration = 5000;
         stack = Math.min(5, stack + 1);
         eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -15 * stack, attack.skillID, null, false);
         eff.setDuration(duration);
         eff.setCancelTask(duration);
         eff.setValue(stack);
         monster.applyStatus(eff);
      }

      if (attack.skillID == 2201009 || attack.skillID == 400020002) {
         MobTemporaryStatEffect eff = monster.getBuff(MobTemporaryStatFlag.SPEED);
         int stack = 0;
         if (eff != null) {
            stack = eff.getValue();
         }

         stack = Math.min(5, stack + 1);
         eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -15 * stack, attack.skillID, null, false);
         eff.setDuration(5000);
         eff.setCancelTask(5000L);
         eff.setValue(stack);
         monster.applyStatus(eff);
      }

      if (totalDamage > 0L && attack.skillID == 2221003) {
         monster.setTempEffectiveness(Element.Ice, effect.getDuration());
      }

      if (attack.skillID == 2241000) {
         int prop = effect.getU2();
         if (ThreadLocalRandom.current().nextInt(100) < prop || this.getPlayer().getClient().isGm()) {
            int count = effect.getZ();
            SecondaryStatEffect eff = SkillFactory.getSkill(2241001).getEffect(attack.skillLevel);
            Point lt = eff.getLt();
            Point rb = eff.getRb();
            Point lt2 = new Point(-600, -550);
            Point rb2 = new Point(600, 550);
            List<MapleMonster> mobs = this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), lt2.x, lt2.y, rb2.x, rb2.y);
            CollectionUtil.sortMonsterByBossHP(mobs);
            List<AffectedArea> summoned = this.getPlayer().getMap().getAffectedAreasBySkillId(2241001, this.getPlayer().getId());

            for (int i = summoned.size(); i < count; i++) {
               MapleMonster mob = null;

               for (int index = 0; i < mobs.size(); index++) {
                  boolean canSummon = true;
                  if (index >= mobs.size()) {
                     mob = null;
                     break;
                  }

                  mob = mobs.get(index);

                  for (AffectedArea area : summoned) {
                     if (area.isInMistRect(mob.getPosition())) {
                        canSummon = false;
                        break;
                     }
                  }

                  if (canSummon) {
                     break;
                  }

                  mob = null;
               }

               if (mob != null) {
                  Rect rect = new Rect(mob.getPosition(), lt, rb, false);
                  AffectedArea aa = new AffectedArea(rect, this.getPlayer(), eff, mob.getPosition(), System.currentTimeMillis() + effect.getDOTTime() * 1000);
                  this.getPlayer().getMap().spawnMist(aa);
                  summoned.add(aa);
                  this.getPlayer().giveCoolDowns(2241001, System.currentTimeMillis(), effect.getW2() * 1000);
                  this.getPlayer().send(CField.skillCooldown(2241001, effect.getW2() * 1000));
               }
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400021002) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400020002).getEffect(attack.skillLevel);
         if (eff != null) {
            Rect rect = new Rect(attack.forcedPos, effect.getLt(), effect.getRb(), (attack.display & 32768) > 0);
            this.getPlayer().sendCreateAreaDotInfo(400020002, eff, attack.forcedPos, rect);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 2221054:
         case 2221055:
            this.upgradeAura = this.getActiveSkillID() == 2221055;
            if (this.upgradeAura) {
               long removeTime = System.currentTimeMillis() + 30200L;
               Point pt = this.getPlayer().getPosition();
               Rect rect = new Rect(pt, effect.getLt(), effect.getRb(), this.getPlayer().isFacingLeft());
               this.getPlayer().temporaryStatResetBySkillID(2221054);
               this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), effect, pt, removeTime));
               this.getPlayer().giveCoolDowns(2221055, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
               this.getPlayer().send(CField.skillCooldown(2221055, effect.getCooldown(this.getPlayer())));
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               statups.put(SecondaryStatFlag.indieSummon, 1);
               statups.put(SecondaryStatFlag.indieFlyAcc, 1);
               this.getPlayer().temporaryStatSet(2221056, 1, 30200, statups);
            }

            this.startIceAuraTask();
            effect.applyTo(this.getPlayer(), true);
            break;
         case 2241501:
            List<SecondAtom.Atom> atoms = new ArrayList<>();

            for (TeleportAttackElement e : this.teleportAttackAction.actions) {
               if (e.data != null && e.data instanceof TeleportAttackData_ListLong) {
                  TeleportAttackData_ListLong data = (TeleportAttackData_ListLong)e.data;
                  int angle = -45;

                  for (long ltest : data.getLongList()) {
                     int xpos = (int)ltest;
                     int ypos = (int)(ltest >> 32);
                     Point pos = new Point(xpos, ypos);
                     SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(),
                        this.getPlayer().getId(),
                        2241500,
                        SecondAtom.SN.getAndAdd(1),
                        SecondAtom.SecondAtomType.FrozenLightning,
                        0,
                        null,
                        pos
                     );
                     SecondAtomData dd = skill.getSecondAtomData();
                     a.setPlayerID(this.getPlayer().getId());
                     a.setExpire(dd.getExpire());
                     a.setAttackableCount(3);
                     a.setEnableDelay(dd.getEnableDelay());
                     a.setSkillID(2241501);
                     int atomangle = angle + ThreadLocalRandom.current().nextInt(120);

                     while (atomangle > 360) {
                        atomangle -= 360;
                     }

                     a.setRotate(atomangle);
                     angle += 90;
                     this.getPlayer().addSecondAtom(a);
                     atoms.add(a);
                  }
               }
            }

            if (atoms.size() > 0) {
               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 2141500, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
            }

            effect.applyTo(this.getPlayer());
            break;
         case 400021030:
            SecondaryStatEffect subEffect = SkillFactory.getSkill(400021040).getEffect(this.getActiveSkillLevel());
            List<Point> points = new ArrayList<>();
            packet.skip(4);
            boolean isCharStanceLeft = packet.readByte() > 0;
            if (isCharStanceLeft) {
               subEffect = SkillFactory.getSkill(400021031).getEffect(this.getActiveSkillLevel());
            }

            int size = packet.readInt();

            for (int i = 0; i < size; i++) {
               points.add(new Point(packet.readInt(), packet.readInt()));
            }

            int delay = 2600;
            int tdBreakTime = -2160;

            for (Point pos : points) {
               this.getPlayer()
                  .getMap()
                  .spawnMist(
                     new AffectedArea(
                        effect.calculateBoundingBox(pos, this.getPlayer().isFacingLeft()),
                        this.getPlayer(),
                        subEffect,
                        pos,
                        System.currentTimeMillis() + (delay + tdBreakTime),
                        tdBreakTime
                     )
                  );
               tdBreakTime += 350;
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      super.activeSkillCancel();
   }

   public void cancelIceAuraTask() {
      if (this.iceAuraTask != null) {
         this.iceAuraTask.cancel(true);
         this.iceAuraTask = null;
      }
   }

   public void startIceAuraTask() {
      if (this.iceAuraTask != null) {
         this.cancelIceAuraTask();
      }

      this.iceAuraTask = Timer.BuffTimer.getInstance()
         .register(
            () -> {
               if (this.getPlayer() != null && this.getPlayer().getMap() != null) {
                  if (this.upgradeAura) {
                     List<AffectedArea> mists = this.getPlayer().getMap().getAllMistsThreadsafe();
                     AffectedArea iceAuraMist = null;

                     for (AffectedArea mist : mists) {
                        if (mist.getSourceSkillID() == 2221055 && mist.getOwnerId() == this.getPlayer().getId()) {
                           iceAuraMist = mist;
                        }
                     }

                     if (iceAuraMist != null) {
                        List<MapleCharacter> partyPlayers = this.getPlayer().getPartyMembersSameMap();
                        if (partyPlayers == null) {
                           partyPlayers = new ArrayList<>();
                           partyPlayers.add(this.getPlayer());
                        }

                        int count = 0;
                        SecondaryStatEffect effect = SkillFactory.getSkill(2221054).getEffect(this.getPlayer().getTotalSkillLevel(2221054));
                        if (effect == null) {
                           this.cancelIceAuraTask();
                           return;
                        }

                        for (MapleCharacter partyMember : partyPlayers) {
                           Point pos_ = partyMember.getTruePosition();
                           if (iceAuraMist.isInMistRect(pos_)) {
                              if (!partyMember.hasBuffBySkillID(2221055)) {
                                 iceAuraMist.getSource().applyTo(partyMember, true);
                              }

                              count++;
                           } else if (partyMember.hasBuffBySkillID(2221055)) {
                              partyMember.temporaryStatResetBySkillID(2221055);
                           }
                        }

                        this.getPlayer().addMP(-effect.getMPCon());
                        List<MapleMonster> mobs = this.getPlayer().getMap().getAllMonstersThreadsafe();
                        int mobcnt = 0;

                        for (MapleMonster m : mobs) {
                           if (mobcnt >= effect.getMobCount()) {
                              break;
                           }

                           Point pos_ = m.getTruePosition();
                           if (iceAuraMist.isInMistRect(pos_)) {
                              MobTemporaryStatEffect eff = m.getBuff(MobTemporaryStatFlag.SPEED);
                              int stack = 0;
                              if (eff != null) {
                                 stack = eff.getValue();
                              }

                              stack += count;
                              stack = Math.min(5, stack + 1);
                              eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -15 * stack, 2221055, null, false);
                              eff.setDuration(5000);
                              eff.setCancelTask(5000L);
                              eff.setValue(stack);
                              m.applyStatus(eff);
                              mobcnt++;
                           }
                        }
                     } else {
                        this.upgradeAura = false;
                        List<MapleCharacter> partyPlayersx = this.getPlayer().getPartyMembersSameMap();
                        if (partyPlayersx == null) {
                           partyPlayersx = new ArrayList<>();
                           partyPlayersx.add(this.getPlayer());
                        }

                        for (MapleCharacter partyMemberx : partyPlayersx) {
                           if (partyMemberx.hasBuffBySkillID(2221055)) {
                              partyMemberx.temporaryStatResetBySkillID(2221055);
                           }
                        }

                        SecondaryStatEffect effect = SkillFactory.getSkill(2221054).getEffect(this.getPlayer().getTotalSkillLevel(2221054));
                        effect.applyTo(this.getPlayer(), true);
                     }
                  } else {
                     List<MapleMapObject> mobs = this.getPlayer()
                        .getMap()
                        .getMapObjectsInRange(this.getPlayer().getPosition(), 40000.0, List.of(MapleMapObjectType.MONSTER));
                     SecondaryStatEffect effect = SkillFactory.getSkill(2221054).getEffect(this.getPlayer().getTotalSkillLevel(2221054));
                     if (effect != null) {
                        int count = 0;
                        if (effect.getMPCon() > this.getPlayer().getStat().getMp() || !this.getPlayer().isAlive()) {
                           this.getPlayer().temporaryStatResetBySkillID(2221054);
                           return;
                        }

                        this.getPlayer().addMP(-effect.getMPCon());

                        for (MapleMapObject m : mobs) {
                           if (count >= effect.getMobCount()) {
                              break;
                           }

                           MapleMonster monster = (MapleMonster)m;
                           MobTemporaryStatEffect eff = monster.getBuff(MobTemporaryStatFlag.SPEED);
                           int stack = 0;
                           if (eff != null) {
                              stack = eff.getValue();
                           }

                           stack = Math.min(5, stack + 1);
                           eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -15 * stack, 2221054, null, false);
                           eff.setDuration(5000);
                           eff.setCancelTask(5000L);
                           eff.setValue(stack);
                           monster.applyStatus(eff);
                           count++;
                        }
                     }
                  }
               } else {
                  this.cancelIceAuraTask();
               }
            },
            1000L
         );
   }
}
