package objects.users.jobs.koc;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.ForceAtom_Parallel;
import objects.fields.ForceAtom_Parallel_Bullet;
import objects.fields.ForceAtom_Parallel_Entry;
import objects.fields.SecondAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.EmeraldDust;
import objects.summoned.EmeraldFlower;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Archer;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class WindArcher extends Archer {
   private long lastOffset = 0L;
   private long lastSecondAtomTime = 0L;

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
      if (attack.skillID == 13111021) {
         monster.setPinpointPierceDebuffX(effect.getY());
         monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.PINPOINT_PIERCE_DEBUFF, effect.getX(), attack.skillID,
                     null, false),
               false,
               effect.getDuration(),
               false,
               effect);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
         boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill,
         long totalExp, RecvPacketOpcode opcode) {
      if (attack.targets > 0) {
         if (this.getPlayer().getTotalSkillLevel(13110026) > 0) {
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(13110026);
            if (level != null) {
               this.getPlayer()
                     .temporaryStatSet(SecondaryStatFlag.indiePAD, 13110026,
                           level.getDuration(level.getDuration(), this.getPlayer()), level.getIndiePad());
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StormWhim) != null) {
            SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.StormWhim);
            if (e != null) {
               Skill s = SkillFactory.getSkill(13121055);
               if (!s.getSkillList().contains(attack.skillID)
                     && attack.skillID != 13121055
                     && attack.skillID > 0
                     && attack.skillID != 13121054
                     && attack.skillID != 1311020
                     && !GameConstants.isNoDelaySkill(attack.skillID)
                     && skill.getType() != 51
                     && e.makeChanceResult()) {
                  List<MapleMonster> mobList = this.getPlayer()
                        .getMap()
                        .getMobsInRect(this.getPlayer().getTruePosition(), e.getLt().x, e.getLt().y, e.getRb().x,
                              e.getRb().y, (attack.display & 32768) != 0);
                  if (!mobList.isEmpty()) {
                     CollectionUtil.sortMonsterByBossHP(mobList);
                  }

                  List<Integer> targets = new ArrayList<>();
                  int targetSize = 4;

                  for (MapleMonster mob : mobList) {
                     targets.add(mob.getObjectId());
                     if (targets.size() >= targetSize) {
                        break;
                     }
                  }

                  List<SecondAtom.Atom> atoms = new ArrayList<>();

                  for (int next = 0; next < targetSize; next++) {
                     SecondAtom.Atom a = new SecondAtom.Atom(
                           this.getPlayer().getMap(),
                           this.getPlayer().getId(),
                           13121055,
                           SecondAtom.SN.getAndAdd(1),
                           SecondAtom.SecondAtomType.StormWhim,
                           next,
                           null,
                           0);
                     SecondAtomData.atom atom = s.getSecondAtomData().getAtoms().get(next);
                     a.setPlayerID(this.getPlayer().getId());
                     int target = 0;
                     if (!targets.isEmpty()) {
                        if (targets.size() >= targetSize) {
                           target = targets.get(next);
                        } else {
                           target = targets.get(Randomizer.rand(0, targets.size() - 1));
                        }
                     }

                     a.setTargetObjectID(target);
                     a.setCreateDelay(atom.getCreateDelay());
                     a.setEnableDelay(atom.getEnableDelay());
                     a.setSkillID(13121055);
                     a.setExpire(atom.getExpire());
                     a.setAngle(Randomizer.rand(atom.getFirstAngleStart(),
                           atom.getFirstAngleStart() + atom.getFirstAngleRange()));
                     a.setIndex(next);
                     a.setAttackableCount(1);
                     int posX = this.getPlayer().getTruePosition().x + atom.getPos().x;
                     int posY = this.getPlayer().getTruePosition().y + atom.getPos().y;
                     a.setPos(new Point(posX, posY));
                     atoms.add(a);
                  }

                  SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 13121055, atoms);
                  this.getPlayer().getMap().createSecondAtom(secondAtom);
               }
            }
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StormGuard) != null && attack.skillID != 400031031
            && attack.skillID != 500061034) {
         int sid = 400031031;
         if (this.getPlayer().getTotalSkillLevel(500061033) > 0) {
            sid = 500061034;
         }

         SecondaryStatEffect sld = this.getPlayer().getSkillLevelData(sid);
         if (sld != null && this.getPlayer().getCooldownLimit(sid) == 0L) {
            List<MapleMonster> mobs = this.getPlayer()
                  .getMap()
                  .getMobsInRect(this.getPlayer().getPosition(), sld.getLt().x, sld.getLt().y, sld.getRb().x,
                        sld.getRb().y, this.getPlayer().isFacingLeft());
            if (mobs.size() > 0) {
               ForceAtom_Parallel atom = new ForceAtom_Parallel();
               atom.fromID = this.getPlayer().getId();
               atom.skillID = sid;

               for (int i = 0; i < sld.getMobCount(); i++) {
                  Collections.shuffle(mobs);
                  MapleMonster target = mobs.stream().findAny().orElse(null);
                  if (target != null) {
                     ForceAtom_Parallel_Entry spellEntry = new ForceAtom_Parallel_Entry();
                     spellEntry.atomType = ForceAtom.AtomType.STORM_GUARD_WIND;
                     spellEntry.bulletSkillID = sid;
                     atom.entries.add(spellEntry);
                     ForceAtom_Parallel_Bullet bullet = new ForceAtom_Parallel_Bullet();
                     bullet.targetID = target.getObjectId();
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initStormGuardWind(i);
                     bullet.atomInfos.add(info);
                     spellEntry.bullets.add(bullet);
                  }
               }

               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtomParallel(atom));
            }

            int skillId = 400031030;
            if (this.getPlayer().getTotalSkillLevel(500061033) > 0) {
               skillId = 500061033;
            }

            SecondaryStatEffect s = this.getPlayer().getSkillLevelData(skillId);
            if (s != null) {
               this.getPlayer().giveCoolDowns(sid, System.currentTimeMillis(), s.getW2() * 1000);
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 13111024:
         case 13120007: {
            Point pos = packet.readPos();
            byte faceLeft = packet.readByte();
            packet.skip(2);
            this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
            Summoned summonx = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID(),
                  this.getActiveSkillLevel(),
                  this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
                  SummonMoveAbility.STATIONARY,
                  faceLeft,
                  effect.getDuration());
            Rect rect = new Rect(effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y);
            summonx.setArea(rect);
            if (this.getActiveSkillID() == 13111024) {
               summonx.addAura(new EmeraldFlower());
            } else {
               summonx.addAura(new EmeraldDust());
            }

            summonx.addHP(effect.getX());
            this.getPlayer().getMap().spawnSummon(summonx, effect.getDuration());
            this.getPlayer().addSummon(summonx);
            effect.applyTo(this.getPlayer(), true);
         }
            break;
         case 13121017:
            this.getPlayer()
                  .temporaryStatSet(
                        SecondaryStatFlag.StormBringer,
                        this.getActiveSkillID(),
                        effect.getDuration(effect.getDuration(), this.getPlayer()),
                        this.getPlayer().getSkillLevel(13121017));
            break;
         case 13121055:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.StormWhim, this.getActiveSkillID(),
                  effect.getDuration(), 1);
            break;
         case 13141501:
            Summoned s = new Summoned(
                  this.getPlayer(),
                  13141501,
                  this.getActiveSkillLevel(),
                  this.getPlayer().getPosition(),
                  SummonMoveAbility.STATIONARY,
                  (byte) (this.getPlayer().isFacingLeft() ? 1 : 0),
                  System.currentTimeMillis() + effect.getDuration());
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            break;
         case 13141505:
            Summoned summon = this.getPlayer().getSummonBySkillID(13141501);
            short xpos = packet.readShort();
            short ypos = packet.readShort();
            if (summon != null) {
               summon.setPosition(new Point(xpos, ypos));
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
               this.getPlayer().getMap().removeMapObject(summon);
               this.getPlayer().removeVisibleMapObject(summon);
               this.getPlayer().removeSummon(summon);
               this.getPlayer().getMap().spawnSummon(summon, effect.getDuration());
               this.getPlayer().addSummon(summon);
            }
         default:
            super.onActiveSkill(skill, effect, packet);
            break;
         case 400031003:
         case 400031004:
         case 400031068:
            int skillID = this.getActiveSkillID();
            if (skillID == 400031003 || skillID == 400031004 || skillID == 400031068) {
               int offset = 0;
               if (this.getPlayer().getLastHowlingGaleUseTime() != 0L) {
                  offset = (int) (System.currentTimeMillis() - this.getPlayer().getLastHowlingGaleUseTime());
                  if (offset <= 0 || offset >= 20000) {
                     offset = 0;
                  }
               }

               int count = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HowlingGale, 0);
               if (count == 3) {
                  this.lastOffset = 0L;
               }

               if (skillID == 400031004) {
                  count -= 2;
               } else if (skillID == 400031068) {
                  count = 0;
               } else {
                  count--;
                  if (offset + this.lastOffset <= 20000L && this.lastOffset != 0L) {
                     offset = (int) (offset + this.lastOffset);
                  }

                  this.lastOffset = offset;
               }

               if (count == 0) {
                  this.lastOffset = 0L;
               }

               this.getPlayer().temporaryStatSet(400031003, 20000 - offset, SecondaryStatFlag.HowlingGale, count);
            }

            this.getPlayer().setLastHowlingGaleUseTime(System.currentTimeMillis());
            break;
         case 400031022: {
            List<Point> pos = new ArrayList<>();
            List<Integer> targets = new LinkedList<>();

            for (int i = 0; i < 10; i++) {
               Point p = new Point(this.getPlayer().getPosition().x + Randomizer.rand(-250, 100),
                     this.getPlayer().getPosition().y + Randomizer.rand(-200, -25));
               pos.add(p);
               targets.add(0);
            }

            ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
            atomInfo.initDotPunisher(pos);
            ForceAtom atom = new ForceAtom(
                  atomInfo, 400031022, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                  ForceAtom.AtomType.IDLE_WORM, targets, 10);
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
            effect.applyTo(this.getPlayer(), true);
         }
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      this.createSummonSecondAtom();
   }

   public void updateHowlingGale() {
   }

   public void createSummonSecondAtom() {
      if (System.currentTimeMillis() - this.lastSecondAtomTime > 2000L) {
         this.lastSecondAtomTime = System.currentTimeMillis();
         Summoned s = this.getPlayer().getSummonBySkillID(13141501);
         if (s != null) {
            for (int skillID = 13141502; skillID <= 13141504; skillID++) {
               Skill skillData = SkillFactory.getSkill(skillID);
               int bullet = skillData.getEffect(this.getPlayer().getTotalSkillLevel(skillID)).getBulletCount();
               SecondAtomData data = skillData.getSecondAtomData();
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               SecondAtom.SecondAtomType atomType = skillID == 13141502
                     ? SecondAtom.SecondAtomType.SpiritAura
                     : (skillID == 13141503 ? SecondAtom.SecondAtomType.SpiritAura2
                           : SecondAtom.SecondAtomType.SpiritAura3);

               for (int i = 0; i < bullet; i++) {
                  SecondAtomData.atom at = data.getAtoms().get(i);
                  SecondAtom.Atom a = new SecondAtom.Atom(
                        this.getPlayer().getMap(), this.getPlayer().getId(), skillID, ForceAtom.SN.getAndAdd(1),
                        atomType, 0, at);
                  a.setPlayerID(this.getPlayer().getId());
                  a.setSkillID(13141501);
                  Point pos = this.getPlayer().getPosition();
                  int diffx = at.getPos().x;
                  int diffy = at.getPos().y;
                  a.setPos(new Point(pos.x + diffx, pos.y + diffy));
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
               }

               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), skillID, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
            }
         }
      }
   }
}
