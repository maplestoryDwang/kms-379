package objects.users.jobs.hero;

import constants.GameConstants;
import constants.ServerConstants;
import database.loader.CharacterSaveFlag;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.child.DiceRoll;
import objects.effect.child.SkillEffect;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.users.jobs.Thief;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_Quad;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class Phantom extends Thief {
   private int judgementX = 0;
   private int markOfPhantomX = 0;
   private int markOfPhantomDebuffTarget = 0;
   private int markOfPhantomCount = 0;
   private ScheduledFuture<?> defyingFateTask = null;

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
      if (attack.skillID == 24121010) {
         SecondaryStatEffect eff = SkillFactory.getSkill(24121003).getEffect(attack.skillLevel);
         if (eff != null) {
            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, eff.getZ(), attack.skillID, null, false),
               false,
               eff.getDuration(),
               false,
               eff
            );
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0
         && GameConstants.isPhantom(this.getPlayer().getJob())
         && attack.skillID != 24120002
         && attack.skillID != 24100003
         && attack.skillID != 24121011) {
         this.handleCardStack(attack);
      }

      SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400041040);
      if (eff != null) {
         List<Integer> talentop = new ArrayList<>();

         for (Pair<Integer, Integer> top : this.getPlayer().getStolenSkills()) {
            if (top.right > 0) {
               talentop.add(top.left);
            }
         }

         if (SkillFactory.getSkill(400041040).getSkillList().contains(attack.skillID) || talentop.contains(GameConstants.getLinkedAranSkill(attack.skillID))) {
            if (attack.skillID != 24121000 && attack.skillID != 24121005 && attack.skillID != 24141000) {
               this.tryApplyMarkOfPhantom(attack, eff);
            } else {
               this.markOfPhantomCount++;
               if (this.markOfPhantomCount % eff.getY() == 0) {
                  this.tryApplyMarkOfPhantom(attack, eff);
               }
            }
         }
      }

      Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.Judgement);
      if (v != null && v == 5) {
         int z = this.judgementX;
         int hp = (int)(this.getPlayer().getStat().getMaxHp() * 0.01 * z);
         this.getPlayer().addHP(hp, false);
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 400041040) {
         this.getPlayer().temporaryStatSet(400041045, 2500, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.beforeActiveSkill(packet);
   }

   public void handleCardStack(AttackInfo attack) {
      SecondaryStatEffect effect = null;
      int slv = 0;
      if ((slv = this.getPlayer().getTotalSkillLevel(24120002)) > 0) {
         effect = SkillFactory.getSkill(24120002).getEffect(slv);
      } else if ((slv = this.getPlayer().getTotalSkillLevel(24100003)) > 0) {
         effect = SkillFactory.getSkill(24100003).getEffect(slv);
      }

      if (effect != null) {
         int max = 20;
         if (this.getPlayer().getTotalSkillLevel(20031210) > 0) {
            max = 40;
         }

         boolean critical = false;
         if (ServerConstants.useCriticalDll) {
            for (Boolean bool : attack.dllCritical) {
               if (bool) {
                  critical = true;
                  break;
               }
            }
         } else {
            critical = Randomizer.nextInt(75) < this.getPlayer().getStat().passive_sharpeye_rate;
         }

         if (critical) {
            if (effect.getSourceId() == 24120002 && this.getPlayer().getCooldownLimit(24121011) == 0L) {
               SecondaryStatEffect reverseCard = SkillFactory.getSkill(24121011).getEffect(slv);
               List<MapleMonster> mobs = this.getPlayer()
                  .getMap()
                  .getMobsInRect(
                     this.getPlayer().getPosition(),
                     reverseCard.getLt().x,
                     reverseCard.getLt().y,
                     reverseCard.getRb().x,
                     reverseCard.getRb().y,
                     (attack.display & 32768) != 0
                  );
               Field map = this.getPlayer().getMap();
               List<Integer> target = new ArrayList<>();

               for (MapleMonster mob : mobs) {
                  if (!mob.getStats().isFriendly()) {
                     boolean attackedMob = false;

                     for (AttackPair f : attack.allDamage) {
                        if (mob.getObjectId() == f.objectid) {
                           attackedMob = true;
                           break;
                        }
                     }

                     if (!attackedMob) {
                        target.add(mob.getObjectId());
                        if (target.size() >= reverseCard.getMobCount()) {
                           break;
                        }
                     }
                  }
               }

               if (target.size() > 0) {
                  this.getPlayer().addCooldown(24121011, System.currentTimeMillis(), 5000L);
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  ForceAtom forceAtom = new ForceAtom(
                     info,
                     24121011,
                     this.getPlayer().getId(),
                     false,
                     true,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.PHANTOM_REVERSE_CARD,
                     target,
                     target.size()
                  );
                  map.broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }
            }

            if (effect.makeChanceResult()) {
               if (this.getPlayer().getCardStack() < max) {
                  this.getPlayer().setCardStack((byte)(this.getPlayer().getCardStack() + 1));
               }

               this.getPlayer().addRunningStack(1);
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               int carteID = this.getCarteID();
               info.initNoirCarte(effect.getSourceId());
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  carteID,
                  this.getPlayer().getId(),
                  false,
                  true,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.NOIR_CARTE,
                  Collections.singletonList(0),
                  1
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               this.getPlayer().send(CField.updateCardStack(this.getPlayer().getCardStack()));
            }
         }
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 20031209:
         case 20031210:
            int zz = Randomizer.nextInt(this.getActiveSkillID() == 20031209 ? 2 : 5);
            int carteID = 24100003;
            if (this.getPlayer().getSkillLevel(24120002) > 0) {
               carteID = 24120002;
            }

            this.getPlayer().setCardStack((byte)0);
            this.getPlayer().addRunningStack(carteID == 24100003 ? 5 : 10);
            this.getPlayer().send(CField.updateCardStack(this.getPlayer().getCardStack()));
            List<MapleMapObject> objs = this.getPlayer()
               .getMap()
               .getMapObjectsInRange(this.getPlayer().getTruePosition(), 640000.0, Arrays.asList(MapleMapObjectType.MONSTER));
            List<Integer> monsters = new ArrayList<>();
            int count = effect.getU();
            if (objs.size() > 0) {
               Collections.shuffle(objs);

               for (int i = 0; i < count; i++) {
                  MapleMonster mob = (MapleMonster)objs.get(i);
                  if (mob != null && !mob.getStats().isFriendly()) {
                     monsters.add(mob.getObjectId());
                     break;
                  }
               }
            }

            if (monsters.size() > 0) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               ForceAtom forceAtom = new ForceAtom(
                  info, carteID, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.NOIR_CARTE, monsters, effect.getU()
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }

            DiceRoll roll = new DiceRoll(this.getPlayer().getId(), zz, -1, this.getActiveSkillID(), this.getActiveSkillLevel(), true);
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), roll.encodeForRemote(), false);
            this.getPlayer().getClient().getSession().writeAndFlush(roll.encodeForLocal());
            if (this.getActiveSkillID() == 20031209) {
               int rand = Randomizer.rand(1, 2);
               if (rand == 1) {
                  this.judgementX = effect.getV();
               } else if (rand == 2) {
                  this.judgementX = effect.getW();
               }

               this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getDuration(), SecondaryStatFlag.Judgement, rand);
            } else if (this.getActiveSkillID() == 20031210) {
               int rand = Randomizer.rand(1, 4);
               if (rand == 4) {
                  this.judgementX = effect.getZ();
                  Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                  statups.put(SecondaryStatFlag.Judgement, 5);
                  statups.put(SecondaryStatFlag.VampiricTouch, effect.getZ());
                  this.getPlayer().temporaryStatSet(this.getActiveSkillID(), this.getActiveSkillLevel(), effect.getDuration(), statups);
               } else {
                  if (rand == 1) {
                     this.judgementX = effect.getV();
                  } else if (rand == 2) {
                     this.judgementX = effect.getW();
                  } else if (rand == 3) {
                     this.judgementX = effect.getX() * 100 + effect.getY();
                  }

                  this.getPlayer().temporaryStatSet(this.getActiveSkillID(), effect.getDuration(), SecondaryStatFlag.Judgement, rand);
               }
            }
            break;
         case 24121007:
            packet.skip(4);
            byte mobCount = packet.readByte();
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();

            for (int ix = 0; ix < mobCount; ix++) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(packet.readInt());
               if (mob != null) {
                  if (mob.getBuff(MobTemporaryStatFlag.P_COUNTER) != null || mob.getBuff(MobTemporaryStatFlag.M_COUNTER) != null) {
                     mob.cancelStatus(MobTemporaryStatFlag.P_COUNTER);
                     mob.cancelStatus(MobTemporaryStatFlag.M_COUNTER);
                     statups.put(SecondaryStatFlag.PowerGuard, effect.getY());
                     break;
                  }

                  if (mob.getBuff(MobTemporaryStatFlag.P_IMMUNE) != null
                     || mob.getBuff(MobTemporaryStatFlag.M_IMMUNE) != null
                     || mob.getBuff(MobTemporaryStatFlag.HARD_SKIN) != null) {
                     mob.cancelStatus(MobTemporaryStatFlag.P_IMMUNE);
                     mob.cancelStatus(MobTemporaryStatFlag.M_IMMUNE);
                     mob.cancelStatus(MobTemporaryStatFlag.HARD_SKIN);
                     statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                     break;
                  }

                  if (mob.getBuff(MobTemporaryStatFlag.P_GUARD_UP) != null) {
                     mob.cancelStatus(MobTemporaryStatFlag.P_GUARD_UP);
                     statups.put(SecondaryStatFlag.DamAbsorbShield, effect.getX());
                     break;
                  }

                  if (mob.getBuff(MobTemporaryStatFlag.POWER_UP) != null) {
                     mob.cancelStatus(MobTemporaryStatFlag.POWER_UP);
                     statups.put(SecondaryStatFlag.EnhancedPAD, effect.getEnhancedWatk());
                     break;
                  }
               }
            }

            if (statups.size() > 0) {
               this.getPlayer().temporaryStatSet(this.getActiveSkillID(), this.getActiveSkillLevel(), effect.getDuration(), statups);
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 24141501:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.DefyingFate, 24141501, effect.getDuration(), 1);
            this.startDefyingFateTask();
            break;
         case 400041022: {
            boolean downArrowSkill = false;
            int flag = this.getActiveSkillFlag();
            if ((flag & 131072) != 0) {
               downArrowSkill = true;
            }

            int objectCount = packet.readByte();
            int objectId = 0;

            for (int ixx = 0; ixx < objectCount; ixx++) {
               objectId = packet.readInt();
            }

            packet.readShort();
            packet.readByte();
            int special_pos1 = packet.readInt();
            int special_pos2 = packet.readInt();
            if (!downArrowSkill) {
               this.getPlayer().setBlackJackCount(0);
               ForceAtom.AtomInfo atomInfo1 = new ForceAtom.AtomInfo();
               atomInfo1.initBlackJack();
               atomInfo1.forcedTargetX = this.getPlayer().getPosition().x;
               atomInfo1.forcedTargetY = this.getPlayer().getPosition().y;
               atomInfo1.specialPos1 = special_pos1;
               atomInfo1.specialPos2 = special_pos2;
               ForceAtom.AtomInfo atomInfo2 = new ForceAtom.AtomInfo();
               atomInfo2.initBlackJack();
               atomInfo2.forcedTargetX = this.getPlayer().getPosition().x;
               atomInfo2.forcedTargetY = this.getPlayer().getPosition().y;
               atomInfo2.specialPos1 = special_pos1;
               atomInfo2.specialPos2 = special_pos2;
               ForceAtom.AtomInfo atomInfo3 = new ForceAtom.AtomInfo();
               atomInfo3.initBlackJack();
               atomInfo3.forcedTargetX = this.getPlayer().getPosition().x;
               atomInfo3.forcedTargetY = this.getPlayer().getPosition().y;
               atomInfo3.specialPos1 = special_pos1;
               atomInfo3.specialPos2 = special_pos2;
               ForceAtom atom1 = new ForceAtom(
                  atomInfo1,
                  400041023,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BLACK_JACK,
                  Collections.singletonList(objectId),
                  1
               );
               ForceAtom atom2 = new ForceAtom(
                  atomInfo2,
                  400041023,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BLACK_JACK,
                  Collections.singletonList(objectId),
                  1
               );
               ForceAtom atom3 = new ForceAtom(
                  atomInfo3,
                  400041023,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BLACK_JACK,
                  Collections.singletonList(objectId),
                  1
               );
               this.getPlayer().getMap().addForceAtom(atom1.getKey(), atom1);
               this.getPlayer().getMap().addForceAtom(atom2.getKey(), atom2);
               this.getPlayer().getMap().addForceAtom(atom3.getKey(), atom3);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom1));
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom2));
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom3));
            } else {
               ForceAtom.AtomInfo atomInfo1 = new ForceAtom.AtomInfo();
               atomInfo1.initBlackJack();
               atomInfo1.forcedTargetX = this.getPlayer().getPosition().x;
               atomInfo1.forcedTargetY = this.getPlayer().getPosition().y;
               atomInfo1.specialPos1 = special_pos1;
               atomInfo1.specialPos2 = special_pos2;
               atomInfo1.atomFlag = flag;
               ForceAtom.AtomInfo atomInfo2 = new ForceAtom.AtomInfo();
               atomInfo2.initBlackJack();
               atomInfo2.forcedTargetX = this.getPlayer().getPosition().x;
               atomInfo2.forcedTargetY = this.getPlayer().getPosition().y;
               atomInfo2.specialPos1 = special_pos1;
               atomInfo2.specialPos2 = special_pos2;
               atomInfo2.atomFlag = flag;
               ForceAtom.AtomInfo atomInfo3 = new ForceAtom.AtomInfo();
               atomInfo3.initBlackJack();
               atomInfo3.forcedTargetX = this.getPlayer().getPosition().x;
               atomInfo3.forcedTargetY = this.getPlayer().getPosition().y;
               atomInfo3.specialPos1 = special_pos1;
               atomInfo3.specialPos2 = special_pos2;
               atomInfo3.atomFlag = flag;
               ForceAtom atom1 = new ForceAtom(
                  atomInfo1,
                  400041023,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BLACK_JACK,
                  Collections.singletonList(objectId),
                  1
               );
               ForceAtom atom2 = new ForceAtom(
                  atomInfo2,
                  400041023,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BLACK_JACK,
                  Collections.singletonList(objectId),
                  1
               );
               ForceAtom atom3 = new ForceAtom(
                  atomInfo3,
                  400041023,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BLACK_JACK,
                  Collections.singletonList(objectId),
                  1
               );
               this.getPlayer().getMap().addForceAtom(atom1.getKey(), atom1);
               this.getPlayer().getMap().addForceAtom(atom2.getKey(), atom2);
               this.getPlayer().getMap().addForceAtom(atom3.getKey(), atom3);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom1));
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom2));
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom3));
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400041040:
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.MarkOfPhantom);
            Point pos = this.getPlayer().getPosition();

            for (TeleportAttackElement e : this.getTeleportAttackAction().actions) {
               if (e.type == 8 || e.type == 9) {
                  TeleportAttackData_Quad quad = (TeleportAttackData_Quad)e.data;
                  if (quad != null) {
                     pos = new Point(quad.x, quad.y);
                  }
               }
            }

            this.getPlayer().sendRegisterExtraSkill(pos, this.getPlayer().isFacingLeft(), 400041040);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400041080: {
            int objectId = packet.readInt();
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.BLACK_JACK_SPECIAL_CARD.getValue());
            p.writeInt(400041023);
            p.writeInt(this.getPlayer().getId());
            p.writeInt(3);
            p.writeInt(objectId);
            this.getPlayer().send(p.getPacket());
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      switch (this.getActiveSkillID()) {
         case 20031205:
            int count = this.getPlayer().getPhantomShroudCount();
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(20031205);
            if (eff != null) {
               this.getPlayer().setPhantomShroudCount(0);
               this.getPlayer().giveCoolDowns(20031205, System.currentTimeMillis(), (long)eff.getCooldown(this.getPlayer()) * count);
               this.getPlayer().send(CField.skillCooldown(20031205, eff.getCooldown(this.getPlayer()) * count));
            }
         default:
            super.activeSkillCancel();
            return;
         case 400041009:
            int skillLv = this.getPlayer().getTotalSkillLevel(this.getActiveSkillID());
            this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
            int[] skills = new int[]{400041011, 400041012, 400041013, 400041014, 400041015};

            for (int skill_ : skills) {
               this.getPlayer().temporaryStatResetBySkillID(skill_);
            }

            SecondaryStatEffect skillEff = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(skillLv);
            if (skillEff != null) {
               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, this.getActiveSkillID(), skillLv, 3000);
            }

            int jokerSkill = skills[Randomizer.nextInt(skills.length)];
            SecondaryStatEffect jokerEff = SkillFactory.getSkill(jokerSkill).getEffect(skillLv);
            if (jokerEff != null) {
               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), true));
               this.getPlayer().temporaryStatSet(jokerSkill, skillLv, jokerEff.getDuration(), this.getBuffList(jokerEff));
               if (this.getPlayer().getParty() != null) {
                  for (MapleCharacter pChr : this.getPlayer().getPartyMembers()) {
                     if (this.getPlayer().getId() != pChr.getId()) {
                        pChr.temporaryStatSet(jokerSkill, skillLv, jokerEff.getDuration(), this.getBuffList(jokerEff));
                     }
                  }
               }

               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), false));
               PacketEncoder p = new PacketEncoder();
               p.writeInt(0);
               SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), this.getActiveSkillID(), skillLv, p);
               this.getPlayer().send(e.encodeForLocal());
               e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), jokerSkill, skillLv, p);
               this.getPlayer().send(e.encodeForLocal());
            }
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (!this.getPlayer().hasBuffBySkillID(24141501) && this.defyingFateTask != null) {
         this.cancelDefyingFateTask();
      }

      if (this.getPlayer().hasBuffBySkillID(400041011) || this.getPlayer().hasBuffBySkillID(400041015)) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400041009);
         if (eff != null && this.getPlayer().isAlive()) {
            int u = eff.getU();
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * u);
            this.getPlayer().healHP(hp);
         }
      }
   }

   public void tryApplyMarkOfPhantom(AttackInfo info, SecondaryStatEffect eff) {
      long maxHP = 0L;
      MapleMonster mob = null;
      boolean targetChange = false;
      if (this.markOfPhantomDebuffTarget > 0) {
         mob = this.getPlayer().getMap().getMonsterByOid(this.markOfPhantomDebuffTarget);
         if (mob != null) {
            maxHP = mob.getStats().getMaxHp();
         }
      }

      for (AttackPair ap : info.allDamage) {
         mob = this.getPlayer().getMap().getMonsterByOid(ap.objectid);
         if (mob != null && mob.getHp() > maxHP) {
            maxHP = mob.getStats().getMaxHp();
            targetChange = true;
         }
      }

      if (!targetChange) {
         mob = this.getPlayer().getMap().getMonsterByOid(this.markOfPhantomDebuffTarget);
      }

      if (mob != null) {
         int value = 0;
         if (mob.getObjectId() == this.markOfPhantomDebuffTarget) {
            Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.MarkOfPhantomDebuff);
            if (v != null) {
               value = v;
            }

            value = Math.min(value + 1, eff.getS2());
         } else {
            value = 1;
            this.markOfPhantomDebuffTarget = mob.getObjectId();
         }

         if (this.markOfPhantomX < eff.getX()) {
            int x = 0;
            Integer v = this.getPlayer().getBuffedValue(SecondaryStatFlag.MarkOfPhantom);
            if (v != null) {
               x = v;
            }

            this.markOfPhantomX = ++x;
         }

         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         statups.put(SecondaryStatFlag.MarkOfPhantom, this.markOfPhantomX);
         statups.put(SecondaryStatFlag.MarkOfPhantomDebuff, value);
         this.markOfPhantomCount = 0;
         int buffTime = eff.getDuration(eff.getDuration(), this.getPlayer());
         this.getPlayer().temporaryStatSet(400041040, eff.getLevel(), buffTime, statups);
      }
   }

   public void throwJokerResult(PacketDecoder slea) {
      boolean isFaceLeft = slea.readByte() > 0;
      if (GameConstants.isPhantom(this.getPlayer().getJob())) {
         int carteID = this.getCarteID();
         if (carteID == 0) {
            return;
         }

         int atomInc = 0;
         if (carteID == 24100003) {
            atomInc = 1;
         }

         if (carteID == 24120002) {
            atomInc = 2;
         }

         if (atomInc == 0) {
            return;
         }

         int jokerID = 400041010;
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(jokerID);
         if (effect == null) {
            return;
         }

         List<MapleMonster> mobs = this.getPlayer()
            .getMap()
            .getMobsInRect(this.getPlayer().getPosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y, isFaceLeft);
         if (mobs.size() > 0) {
            for (int i = 0; i < effect.getX(); i++) {
               Collections.shuffle(mobs);
               MapleMonster mob = mobs.stream().findAny().orElse(null);
               if (mob != null) {
                  int playerID = this.getPlayer().getId();
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  ForceAtom forceAtom = new ForceAtom(
                     info, 400041010, playerID, false, true, playerID, ForceAtom.AtomType.NOIR_CARTE, Collections.singletonList(mob.getObjectId()), 1
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }
            }

            for (int ix = 0; ix < 4; ix++) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  carteID,
                  this.getPlayer().getId(),
                  false,
                  true,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.NOIR_CARTE,
                  Collections.singletonList(0),
                  1
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
         }
      }
   }

   public int getCarteID() {
      if (this.getPlayer().getTotalSkillLevel(24120002) > 0) {
         return 24120002;
      } else {
         return this.getPlayer().getTotalSkillLevel(24100003) > 0 ? 24100003 : 0;
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case Judgement:
            packet.writeInt(this.judgementX);
            break;
         case MarkOfPhantom:
            packet.writeInt(this.markOfPhantomX);
            break;
         case MarkOfPhantomDebuff:
            packet.writeInt(this.markOfPhantomDebuffTarget);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public Map<SecondaryStatFlag, Integer> getBuffList(SecondaryStatEffect effect) {
      Map<SecondaryStatFlag, Integer> buffList = new HashMap<>();
      switch (effect.getSourceId()) {
         case 400041011:
            buffList.put(SecondaryStatFlag.indieMHPR, effect.getIndieMhpR());
            break;
         case 400041012:
            buffList.put(SecondaryStatFlag.indieAsrR, effect.getIndieAsrR());
            buffList.put(SecondaryStatFlag.ReduceFixDamR, effect.getZ());
            break;
         case 400041013:
            buffList.put(SecondaryStatFlag.indieCooltimeReduce, Integer.valueOf(effect.getIndieCooltimeReduce()));
            break;
         case 400041014:
            buffList.put(SecondaryStatFlag.indiePMDR, effect.getIndiePMdR());
            break;
         case 400041015:
            buffList.put(SecondaryStatFlag.indiePMDR, effect.getIndiePMdR());
            buffList.put(SecondaryStatFlag.indieAsrR, effect.getIndieAsrR());
            buffList.put(SecondaryStatFlag.indieMHPR, effect.getIndieMhpR());
            buffList.put(SecondaryStatFlag.ReduceFixDamR, effect.getZ());
            buffList.put(SecondaryStatFlag.indieCooltimeReduce, Integer.valueOf(effect.getIndieCooltimeReduce()));
      }

      return buffList;
   }

   public void addStolenSkill(int skillId, int skillLevel) {
      List<Pair<Integer, Integer>> stolenSkills = this.getPlayer().getStolenSkills();
      if (!this.getPlayer().skillisCooling(20031208) && stolenSkills != null) {
         int jobId = GameConstants.getJobNumber(skillId);
         int stealSkill = this.getStealSkill(jobId);
         Pair<Integer, Integer> dummy = new Pair<>(skillId, 0);
         Skill skill = SkillFactory.getSkill(skillId);
         AtomicBoolean isAllowedToSteal = new AtomicBoolean(true);
         AtomicInteger slot = new AtomicInteger(0);
         stolenSkills.forEach(stSkill -> {
            if (stSkill.getLeft() == skillId) {
               this.getPlayer().dropMessage(-6, "이미 해당 스킬을 훔치셨습니다. 삭제 하신 뒤 다시 시도해주세요.");
               if (this.getPlayer().isGM()) {
                  this.getPlayer().dropMessage(5, "Skill ID: " + skillId);
               }

               isAllowedToSteal.set(false);
            } else {
               if (GameConstants.getJobNumber(stSkill.getLeft()) == jobId) {
                  slot.set(slot.get() + 1);
               }
            }
         });
         skillLevel = Math.min(this.getPlayer().getSkillLevel(stealSkill), skillLevel);
         if (isAllowedToSteal.get()) {
            if (slot.get() < GameConstants.getNumSteal(jobId)) {
               stolenSkills.add(dummy);
               this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.STOLEN_SKILLS.getFlag());
               this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.SKILLS.getFlag());
               this.getPlayer().changeSkillLevel_Skip(skill, skillLevel, (byte)skillLevel);
               this.getPlayer().getClient().getSession().writeAndFlush(CField.addStolenSkill(jobId, slot.get(), skillId, skillLevel));
            }
         }
      } else {
         this.getPlayer().dropMessage(-6, "잠시 뒤에 다시 시도해주세요.");
      }
   }

   public void removeAllStolenSkill() {
      List<Pair<Integer, Integer>> stolenSkills = this.getPlayer().getStolenSkills();
      int jobId = 0;
      int slot = 0;

      try {
         for (Pair<Integer, Integer> stSkill : new ArrayList<>(stolenSkills)) {
            this.unchooseStolenSkill(stSkill.getRight());
            this.cancelStolenSkill(stSkill.getLeft());
            this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.STOLEN_SKILLS.getFlag());
            this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.SKILLS.getFlag());
            jobId = getJobNumber(stSkill.getLeft());
            byte var9;
            if (jobId == 1 || jobId == 2) {
               var9 = 4;
            } else if (jobId != 3 && jobId != 4) {
               var9 = 2;
            } else {
               var9 = 3;
            }

            for (int a = 0; a < var9; a++) {
               this.getPlayer().getClient().getSession().writeAndFlush(CField.removeStolenSkill(jobId, a));
            }

            Skill skil = SkillFactory.getSkill(stSkill.getLeft());
            this.getPlayer().changeSkillLevel_Skip(skil, 0, 0);
         }
      } catch (Exception var7) {
      }

      stolenSkills.clear();
   }

   public void removeStolenSkill(int skillId) {
      List<Pair<Integer, Integer>> stolenSkills = this.getPlayer().getStolenSkills();
      if (!this.getPlayer().skillisCooling(20031208) && stolenSkills != null) {
         int jobId = getJobNumber(skillId);
         AtomicReference<Pair<Integer, Integer>> dummy = new AtomicReference<>();
         Skill skill = SkillFactory.getSkill(skillId);
         AtomicInteger slot = new AtomicInteger(-1);

         for (Pair<Integer, Integer> stSkill : stolenSkills) {
            if (stSkill.getLeft() == skillId) {
               if (stSkill.getRight() > 0) {
                  this.unchooseStolenSkill(stSkill.getRight());
               }

               dummy.set(stSkill);
               if (getJobNumber(stSkill.getLeft()) == jobId) {
                  slot.set(slot.get() + 1);
               }
               break;
            }
         }

         if (slot.get() >= 0) {
            this.cancelStolenSkill(skillId);
            this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.STOLEN_SKILLS.getFlag());
            this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.SKILLS.getFlag());
            this.getPlayer().getClient().getSession().writeAndFlush(CField.replaceStolenSkill(this.getStealSkill(jobId), 0));
            slot.set(-1);

            for (Pair<Integer, Integer> stSkillx : stolenSkills) {
               if (GameConstants.getJobNumber(stSkillx.getLeft()) == jobId) {
                  if (stSkillx.getRight() > 0) {
                     this.getPlayer().getClient().getSession().writeAndFlush(CField.replaceStolenSkill(this.getStealSkill(jobId), stSkillx.getLeft()));
                  }

                  slot.set(slot.get() + 1);
                  if (this.getPlayer().isGM()) {
                     this.getPlayer().dropMessage(5, "slot : " + slot.get() + "," + stSkillx.getLeft() + ", " + skillId);
                  }

                  if (stSkillx.getLeft().equals(skillId)) {
                     break;
                  }
               }
            }

            Skill skil = SkillFactory.getSkill(skillId);
            this.getPlayer().changeSkillLevel_Skip(skil, 0, 0);
            stolenSkills.remove(slot.get());
            this.getPlayer().getClient().getSession().writeAndFlush(CField.removeStolenSkill(jobId, slot.get()));
         }
      } else {
         this.getPlayer().dropMessage(-6, "잠시 뒤에 다시 시도해주세요.");
      }
   }

   public void chooseStolenSkill(int skillId, int baseSkill) {
      List<Pair<Integer, Integer>> stolenSkills = this.getPlayer().getStolenSkills();
      if (!this.getPlayer().skillisCooling(20031208) && stolenSkills != null) {
         stolenSkills.forEach(dummy -> {
            if (dummy.getLeft() == skillId) {
               if (stolenSkills.contains(dummy)) {
                  int dummyEquipped = dummy.getRight();
                  stolenSkills.forEach(stSkill -> {
                     if (stSkill.getRight() == baseSkill) {
                        this.unchooseStolenSkill(stSkill.getRight());
                     }

                     if (dummyEquipped > 0) {
                        this.unchooseStolenSkill((Integer)dummy.getRight());
                     }
                  });
                  stolenSkills.get(stolenSkills.indexOf(dummy)).right = baseSkill;
                  this.getPlayer().getClient().getSession().writeAndFlush(CField.replaceStolenSkill(baseSkill, skillId));
               }
            }
         });
         this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.STOLEN_SKILLS.getFlag());
         this.getPlayer().dropMessage(-8, "스킬 매니지먼트를 통해 스킬을 변경하셨습니다. 해당 스킬에 30초의 대기시간이 적용됩니다.");
         this.getPlayer().addCooldown(baseSkill, System.currentTimeMillis(), 30000L);
      } else {
         this.getPlayer().dropMessage(-6, "잠시 뒤에 다시 시도해주세요.");
      }
   }

   public void unchooseStolenSkill(int baseSkill) {
      if (baseSkill != 0) {
         List<Pair<Integer, Integer>> stolenSkills = this.getPlayer().getStolenSkills();
         if (!this.getPlayer().skillisCooling(20031208) && stolenSkills != null) {
            int jobId = GameConstants.getJobNumber(baseSkill);
            AtomicBoolean isChanged = new AtomicBoolean(false);
            stolenSkills.forEach(stSkill -> {
               if (this.getPlayer().isGM()) {
                  this.getPlayer().dropMessage(5, stSkill.getLeft() + " : " + stSkill.getRight() + " : " + baseSkill);
               }

               if (stSkill.getRight() == baseSkill) {
                  this.cancelStolenSkill(stSkill.getLeft());
                  stSkill.right = 0;
                  isChanged.set(true);
               }
            });
            if (isChanged.get()) {
               this.getPlayer().getClient().getSession().writeAndFlush(CField.replaceStolenSkill(baseSkill, 0));
            }
         } else {
            this.getPlayer().dropMessage(-6, "잠시 뒤에 다시 시도해주세요.");
         }
      }
   }

   public void cancelStolenSkill(int skillID) {
      Skill skk = SkillFactory.getSkill(skillID);
      SecondaryStatEffect eff = skk.getEffect(this.getPlayer().getTotalSkillLevel(skk));
      if (eff != null) {
         if (eff.getDuration() > 0 && !eff.getStatups().isEmpty()) {
            for (MapleCharacter chr : this.getPlayer().getMap().getCharactersThreadsafe()) {
               chr.temporaryStatResetBySkillID(skillID);
            }
         }
      }
   }

   public int getStealSkill(int job) {
      switch (job) {
         case 1:
            return 24001001;
         case 2:
            return 24101001;
         case 3:
            return 24111001;
         case 4:
            return 24121001;
         case 5:
            return 24121054;
         default:
            return 0;
      }
   }

   public static int getJobNumber(int skill) {
      int jobz;
      int job = (jobz = skill / 10000) % 1000;
      if (SkillFactory.getSkill(skill) != null && SkillFactory.getSkill(skill).isHyper()) {
         return 5;
      } else if (job / 100 != 0 && !GameConstants.isNovice(jobz)) {
         if (job / 10 % 10 == 0 || job == 301 || job == 501 || job == 430) {
            return 1;
         } else if (job == 431 || job == 432) {
            return 2;
         } else if (job == 433) {
            return 3;
         } else {
            return job == 434 ? 4 : 2 + job % 10;
         }
      } else {
         return 0;
      }
   }

   public void cancelDefyingFateTask() {
      try {
         if (this.defyingFateTask == null) {
            return;
         }

         this.defyingFateTask.cancel(true);
         this.defyingFateTask = null;
      } catch (Exception var2) {
         System.out.println("Phantom Err");
         var2.printStackTrace();
      }
   }

   public void startDefyingFateTask() {
      if (this.defyingFateTask != null) {
         this.cancelDefyingFateTask();
      }

      this.defyingFateTask = Timer.BuffTimer.getInstance().register(() -> {
         if (this.getPlayer() != null && this.getPlayer().getMap() != null) {
            if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.DefyingFate) != null) {
               this.getPlayer().send(CField.userBonusAttackRequest(24141502, true, Collections.EMPTY_LIST, 0, 0));
            } else {
               this.cancelDefyingFateTask();
            }
         } else {
            this.cancelDefyingFateTask();
         }
      }, 1800L);
   }
}
