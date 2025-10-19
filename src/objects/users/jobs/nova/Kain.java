package objects.users.jobs.nova;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.SkillEffect;
import objects.fields.SecondAtom;
import objects.fields.Wreckage;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.jobs.Archer;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.KainDeathBlessing;
import objects.users.skills.KainExecuteSkill;
import objects.users.skills.KainRemainIncense;
import objects.users.skills.KainStackSkill;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Kain extends Archer {
   private long lastUpdatePossessionTime = 0L;
   public int dragonFangX;
   private KainDeathBlessing kainDeathBlessing = null;
   int wreckageCount = 0;
   int lastSkill = 0;

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
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      Skill deathBlessing = SkillFactory.getSkill(63110011);
      if (deathBlessing != null && deathBlessing.getSkillList().contains(attack.skillID)) {
         KainDeathBlessing kdb = this.kainDeathBlessing;
         if (kdb != null) {
            kdb.incrementStack(monster.getObjectId());
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, final AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      if (this.getPlayer().getJob() >= 6300 && this.getPlayer().getJob() <= 6312) {
         if (attack.skillID == 63111104) {
            int cooldown = effect.getCooldown(this.getPlayer());
            this.getPlayer().send(CField.skillCooldown(63111103, cooldown));
            this.getPlayer().addCooldown(63111103, System.currentTimeMillis(), cooldown);
         }

         if (attack.skillID == 63001000 || attack.skillID == 63100002 || attack.skillID == 63110001) {
            int skillId = attack.skillID;
            int attackSkillId = skillId == 63001000 ? skillId + 1 : skillId + 1001;
            List<Integer> monsters = new ArrayList<>();
            attack.allDamage.forEach(att -> monsters.add(att.objectid));
            List<ExtraSkillInfo> extraSkills = List.of(new ExtraSkillInfo(attackSkillId, 0));
            this.getPlayer()
               .send(
                  CField.getRegisterExtraSkill(
                     skillId,
                     attack.attackPosition.x,
                     attack.attackPosition.y,
                     (attack.display & 32768) != 0,
                     extraSkills,
                     1,
                     monsters,
                     Collections.emptyList(),
                     0
                  )
               );
         }

         if ((
               attack.skillID == 63111007
                  || attack.skillID == 63121004
                  || attack.skillID == 63121006
                  || attack.skillID == 63121007
                  || attack.skillID == 400031065
            )
            && multiKill > 0) {
            this.getPlayer().clearCooldown(63001002);
         }

         if (attack.skillID == 63001100 || attack.skillID == 63101003 || attack.skillID == 63111002) {
            List<Integer> targetIDList = new ArrayList<Integer>() {
               {
                  attack.allDamage.forEach(pair -> this.add(pair.objectid));
               }
            };
            this.getPlayer().sendRegisterExtraSkill(attack.position2, this.getPlayer().isFacingLeft(), 63001001, 1, targetIDList, Collections.emptyList());
         }

         if (attack.skillID == 63141501 && this.getPlayer().getRemainCooltime(63141502) <= 0L) {
            SecondaryStatEffect eff = SkillFactory.getSkill(63141502).getEffect(this.getPlayer().getTotalSkillLevel(63141502));
            int subTime = eff.getSubTime();
            this.getPlayer().send(CField.skillCooldown(63141502, subTime));
            this.getPlayer().addCooldown(63111103, System.currentTimeMillis(), subTime);
            List<Pair<Integer, Integer>> mobList = new ArrayList<>();

            for (AttackPair pair : attack.allDamage) {
               int objId = pair.objectid;
               if (this.getPlayer().getMap().getMonsterByOid(objId) != null) {
                  mobList.add(new Pair<>(objId, 456));
               }
            }

            List<Integer> list = SkillFactory.getSkill(63141502).getSkillList();
            if (list != null && !list.isEmpty()) {
               for (int skil : list) {
                  this.getPlayer().changeCooldown(skil, -1000L);
               }
            }

            this.getPlayer().send(CField.userBonusAttackRequest(63141502, true, mobList));
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ThanatosDescent) != null && this.getPlayer().getCooldownLimit(400031063) <= 0L) {
            SecondaryStatEffect td = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ThanatosDescent);
            if (td != null) {
               List<SecondAtom.Atom> atoms = new ArrayList<>();

               for (int i = 0; i < td.getMobCount(); i++) {
                  Point pos = new Point(this.getPlayer().getTruePosition());
                  pos.x = pos.x + Randomizer.rand(-150, 150);
                  pos.y = pos.y + Randomizer.rand(-150, 150);
                  SecondAtom.Atom a = new SecondAtom.Atom(
                     this.getPlayer().getMap(),
                     this.getPlayer().getId(),
                     400031063,
                     SecondAtom.SN.getAndAdd(1),
                     SecondAtom.SecondAtomType.ThanatosDescent,
                     0,
                     null,
                     pos
                  );
                  skill = SkillFactory.getSkill(400031063);
                  if (skill != null) {
                     SecondAtomData data = skill.getSecondAtomData();
                     a.setPlayerID(this.getPlayer().getId());
                     a.setExpire(data.getExpire());
                     a.setRotate(data.getRotate());
                     a.setSkillID(400031063);
                     a.setAttackableCount(data.getAttackableCount());
                     a.setEnableDelay(data.getEnableDelay());
                     this.getPlayer().addSecondAtom(a);
                     atoms.add(a);
                  }
               }

               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400031063, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
               this.getPlayer().giveCoolDowns(400031063, System.currentTimeMillis(), td.getSubTime());
               this.getPlayer().send(CField.skillCooldown(400031063, td.getSubTime()));
            }
         }

         Skill possession = SkillFactory.getSkill(63101001);
         if (possession != null
            && (possession.getSkillList().contains(attack.skillID) || possession.getSkillList2().contains(attack.skillID))
            && attack.targets > 0) {
            SecondaryStatEffect e = SkillFactory.getSkill(63101001).getEffect(1);
            if (this.getPlayer().getJob() >= 6312) {
               e = SkillFactory.getSkill(63120000).getEffect(1);
            }

            if (e != null) {
               int x = e.getX();
               if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ThanatosDescent) != null) {
                  SecondaryStatEffect td = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ThanatosDescent);
                  if (td != null) {
                     x = td.getX();
                  }
               }

               this.applyKainPossession(x);
            }
         }

         Skill deathBlessing = SkillFactory.getSkill(63110011);
         if (deathBlessing != null && deathBlessing.getSkillList2().contains(attack.skillID)) {
            KainDeathBlessing kdb = this.kainDeathBlessing;
            if (kdb != null) {
               int skillID = 63111012;
               if (this.getPlayer().hasBuffBySkillID(63141503)) {
                  skillID = 63141501;
               }

               KainExecuteSkill kes = new KainExecuteSkill(skillID);
               int delay = 157;
               List<Pair<Integer, Integer>> bonusAttackList = new ArrayList<>();

               for (AttackPair pairx : attack.allDamage) {
                  int objectID = pairx.objectid;
                  if (kdb.getEntry(objectID) != null && kdb.getEntry(objectID).getStack() > 0) {
                     kdb.decrementStack(objectID);
                     kes.addEntry(objectID, delay + kes.getEntryCount() * 70);
                     bonusAttackList.add(new Pair<>(objectID, 0));
                  }
               }

               if (kes.getEntryCount() > 0) {
                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.KAIN_EXECUTE_SKILL_RESULT.getValue());
                  kes.encode(packet);
                  this.getPlayer().send(packet.getPacket());
                  SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(63111013);
                  if (ex != null) {
                     this.getPlayer()
                        .temporaryStatSet(
                           63111013, ex.getDuration(), SecondaryStatFlag.DeathBlessing, this.getPlayer().getTotalSkillLevel(63120036) > 0 ? 2 : 1
                        );
                  }

                  this.getPlayer().clearCooldown(63001002);
                  int s = deathBlessing.getEffect(this.getPlayer().getTotalSkillLevel(63110011)).getS();
                  SecondaryStatEffect advanced = this.getPlayer().getSkillLevelData(63120001);
                  if (advanced != null) {
                     s += advanced.getS();
                     int heal = boss ? advanced.getX() : 0;
                     if (heal > 0) {
                        int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01) * heal;
                        this.getPlayer().healHP(hp);
                     }

                     if (!boss) {
                        this.getPlayer().setAdvancedDeathBlessingX(this.getPlayer().getAdvancedDeathBlessingX() + multiKill);
                        if (this.getPlayer().getAdvancedDeathBlessingX() >= advanced.getY()) {
                           int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01) * effect.getV();
                           this.getPlayer().healHP(hp);
                           this.getPlayer().setAdvancedDeathBlessingX(0);
                        }
                     }

                     this.applyKainPossession(s);
                  }

                  if (skillID == 63141501 && this.getPlayer().getRemainCooltime(63141502) <= 0L) {
                     this.getPlayer().send(CField.userBonusAttackRequest(63141502, true, bonusAttackList));
                     List<Integer> list = SkillFactory.getSkill(63141502).getSkillList();
                     if (list != null) {
                        for (int skid : list) {
                           this.getPlayer().changeCooldown(skid, -1000L);
                        }
                     }

                     this.getPlayer().addCooldown(63141502, System.currentTimeMillis(), 250L);
                     this.getPlayer().send(CField.skillCooldown(63141502, 250));
                  }
               }
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.RemainIncense) != null && attack.skillID != 63111010 && attack.targets > 0) {
            Skill incense = SkillFactory.getSkill(63111009);
            if (incense != null) {
               SecondaryStatEffect exx = this.getPlayer().getBuffedEffect(SecondaryStatFlag.RemainIncense);
               if (exx != null) {
                  SecondaryStatEffect e_ = SkillFactory.getSkill(63111010).getEffect(exx.getLevel());
                  if (e_ != null) {
                     if (incense.getSkillList2().contains(attack.skillID) && this.getPlayer().getMap().getWrekageCount(this.getPlayer().getId()) > 0) {
                        List<Wreckage> wreckages = this.getPlayer()
                           .getMap()
                           .getWreckageInRect(
                              this.getPlayer().getTruePosition(), e_.getLt2().x, e_.getLt2().y, e_.getRb2().x, e_.getRb2().y, this.getPlayer().getId()
                           );
                        if (!wreckages.isEmpty()) {
                           KainRemainIncense wri = new KainRemainIncense(this.getPlayer().getId());

                           for (Wreckage wreckage : wreckages) {
                              wreckage.removeWreckage(this.getPlayer().getMap(), false);
                              wri.addEntry(wreckage.getObjectId(), wreckage.getPosition().x, wreckage.getPosition().y);
                           }

                           this.getPlayer().getMap().broadcastMessage(CField.DelWreckage(this.getPlayer().getId(), wreckages, false));
                           PacketEncoder packetx = new PacketEncoder();
                           packetx.writeShort(SendPacketOpcode.KAIN_REMAIN_INCENSE_ATTACK.getValue());
                           wri.encode(packetx);
                           this.getPlayer().send(packetx.getPacket());
                        }
                     }

                     if (this.getPlayer().getRemainCooltime(63111010) <= 0L || attack.skillID == 400031062) {
                        Integer specialValue = SkillFactory.getSkill(63111010).getSkillSpecialValue().get(attack.skillID);
                        if (specialValue != null) {
                           int bulletCount = specialValue * 2;
                           Rect rc = new Rect(this.getPlayer().getTruePosition(), e_.getLt(), e_.getRb(), this.getPlayer().isFacingLeft());
                           List<Point> fhs = this.getPlayer().getMap().getFootholdRandomly(bulletCount, rc);
                           int count = 0;

                           for (Point pos : fhs) {
                              pos.x = pos.x + Randomizer.rand(-10, 10);
                              if (this.getPlayer().getMap().getWrekageCount(this.getPlayer().getId()) >= bulletCount) {
                                 break;
                              }

                              count++;
                              this.getPlayer()
                                 .getMap()
                                 .spawnWreckage(new Wreckage(this.getPlayer(), exx.getW() * 1000, 63111010, this.getPlayer().incAndGetWreckageCount(), pos));
                           }
                        }

                        int cooltime = (int)(exx.getT() * 1000.0);
                        this.getPlayer().addCooldown(63111010, System.currentTimeMillis(), cooltime);
                        this.getPlayer().send(CField.skillCooldown(exx.getSourceId(), cooltime));
                        this.lastSkill = attack.skillID;
                     }
                  }
               }
            }
         }

         if (this.getPlayer().getTotalSkillLevel(400031066) > 0 && attack.skillID != 400031066) {
            SecondaryStatEffect exx = this.getPlayer().getSkillLevelData(400031066);
            if (exx != null) {
               if (boss) {
                  this.getPlayer().gripOfAgonyBossCount++;
               }

               this.getPlayer().gripOfAgonyMobCount += multiKill;
               if (this.getPlayer().gripOfAgonyBossCount >= exx.getQ2() || this.getPlayer().gripOfAgonyMobCount >= exx.getQ()) {
                  int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.GripOfAgony, 0);
                  if (value < exx.getU()) {
                     this.getPlayer().temporaryStatSet(400031066, Integer.MAX_VALUE, SecondaryStatFlag.GripOfAgony, value + 1);
                  }

                  this.getPlayer().gripOfAgonyBossCount = 0;
                  this.getPlayer().gripOfAgonyMobCount = 0;
               }
            }
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DragonFang) != null && attack.skillID != 63101006 && attack.targets > 0) {
         SecondaryStatEffect exx = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DragonFang);
         if (exx != null) {
            int x = exx.getX();
            new ArrayList<>(this.getPlayer().getSecondAtoms())
               .stream()
               .filter(ax -> ax.getType() == SecondAtom.SecondAtomType.DragonFang)
               .forEach(ax -> this.getPlayer().send(CField.secondAtomAttack(this.getPlayer().getId(), ax.getKey(), 1)));
            this.dragonFangX++;
            if (this.dragonFangX >= x && this.getPlayer().getSecondAtomCount(SecondAtom.SecondAtomType.DragonFang) < exx.getQ()) {
               List<SecondAtom.Atom> atoms = new ArrayList<>();
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  63101006,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.DragonFang,
                  0,
                  null,
                  new Point(0, 0)
               );
               Skill sx = SkillFactory.getSkill(63101006);
               SecondAtomData sad = sx.getSecondAtomData();
               a.setAttackableCount(sad.getAttackableCount());
               a.setExpire(sad.getExpire());
               a.setSkillID(63101006);
               atoms.add(a);
               this.getPlayer().addSecondAtom(a);
               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 63101006, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
               this.dragonFangX = 0;
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      if (GameConstants.isPossessSkill(this.getActiveSkillPrepareID())) {
         this.getPlayer().onPossessSkill(this.getActiveSkillPrepareID());
      }

      if (this.getActiveSkillPrepareID() == 63121008) {
         SecondaryStatEffect eff = SkillFactory.getSkill(63121008).getEffect(this.getPlayer().getTotalSkillLevel(63121008));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(63121008, Integer.MAX_VALUE, SecondaryStatFlag.KeyDownMoving, eff.getX());
            this.getPlayer().temporaryStatSet(63121008, Integer.MAX_VALUE, SecondaryStatFlag.indiePartialNotDamaged, 1);
         }
      }

      if (this.getActiveSkillPrepareID() == 400031064) {
         long remaining = this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.ThanatosDescent) - System.currentTimeMillis();
         this.getPlayer().temporaryStatResetBySkillID(400031062);
         SecondaryStatEffect effect = SkillFactory.getSkill(400031062).getEffect(this.getPlayer().getTotalSkillLevel(400031062));
         int indieDamR = effect.getIndieDamR();
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieDamR, 400031069, (int)remaining, indieDamR);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 400031064, 6720, 1);
      }

      if (GameConstants.isKainStackSkill(this.getActiveSkillPrepareID())) {
         KainStackSkill kss = this.getPlayer().getKainStackSKill();
         if (kss != null) {
            kss.decrementStack(this.getActiveSkillPrepareID());
         }
      }

      if (GameConstants.isPossessSkill(this.getActiveSkillPrepareID())) {
         this.getPlayer().onPossessSkill(this.getActiveSkillPrepareID());
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      if (GameConstants.isKain(this.getPlayer().getJob())) {
         if (this.isShadowStep()) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(63001002);
            if (e != null) {
               this.getPlayer().temporaryStatSet(63001002, e.getSubTime() / 1000, SecondaryStatFlag.DarkSight, this.getActiveSkillLevel());
            }
         }

         if (GameConstants.isKainStackSkill(this.getActiveSkillID())) {
            KainStackSkill kss = this.getPlayer().getKainStackSKill();
            if (kss != null) {
               kss.decrementStack(this.getActiveSkillID());
            }
         }

         if (GameConstants.isPossessSkill(this.getActiveSkillID())) {
            this.getPlayer().onPossessSkill(this.getActiveSkillID());
         }
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      switch (this.getActiveSkillID()) {
         case 63001004:
            byte left = packet.readByte();
            int sX = packet.readInt();
            int sY = packet.readInt();
            int unk = packet.readInt();
            PacketEncoder p = new PacketEncoder();
            p.write(left);
            p.writeInt(sX);
            p.writeInt(sY);
            p.writeInt(unk);
            SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), this.getActiveSkillID(), this.getActiveSkillLevel(), p);
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
            break;
         case 63101104: {
            byte mobCount = packet.readByte();
            List<Integer> targets = new ArrayList<>();

            for (int i = 0; i < mobCount; i++) {
               targets.add(packet.readInt());
            }

            packet.skip(3);
            Point position = new Point(packet.readInt(), packet.readInt());
            boolean isLeft = packet.readByte() == 1;
            List<SecondAtom.Atom> atoms = new ArrayList<>();
            Skill s = SkillFactory.getSkill(63101104);
            int bulletCount = effect.getBulletCount();
            Collections.shuffle(targets);

            for (int i = 0; i < bulletCount; i++) {
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  63101104,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.ScatteringShot,
                  i,
                  null,
                  this.getPlayer().getTruePosition()
               );
               SecondAtomData.atom dd = skill.getSecondAtomData().getAtoms().get(i);
               a.setPlayerID(this.getPlayer().getId());
               a.setTargetObjectID(i % targets.size());
               a.setExpire(dd.getExpire());
               a.setAttackableCount(dd.getAttackableCount());
               a.setCreateDelay(dd.getCreateDelay());
               a.setRotate(dd.getRotate());
               a.setAngle(dd.getRotate());
               a.setUnk4(1);
               if (isLeft) {
                  a.setAngle(360 - dd.getRotate());
                  a.setPos(new Point(position.x - dd.getPos().x, position.y + dd.getPos().y));
               } else {
                  a.setPos(new Point(position.x + dd.getPos().x, position.y + dd.getPos().y));
               }

               a.setSkillID(63101104);
               this.getPlayer().addSecondAtom(a);
               atoms.add(a);
            }

            if (atoms.size() > 0) {
               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 63101104, atoms);
               this.getPlayer().getMap().createSecondAtom(secondAtom);
            }
            break;
         }
         case 63141503:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.Annihilation, this.getActiveSkillID(), effect.getDuration(), 1);
            break;
         case 400031066: {
            packet.skip(4);
            Point pos = new Point(packet.readInt(), packet.readInt());
            boolean isLeft = packet.readByte() == 1;
            this.onGripOfAgony(pos, isLeft);
            effect.applyTo(this.getPlayer());
            break;
         }
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      if (this.getActiveSkillID() == 63121008) {
         this.getPlayer().temporaryStatResetBySkillID(63121008);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      super.updatePerSecond();
      if (this.lastUpdatePossessionTime == 0L) {
         this.lastUpdatePossessionTime = System.currentTimeMillis();
      } else {
         SecondaryStatEffect effect = SkillFactory.getSkill(63101001).getEffect(1);
         int cycle = effect.getU();
         if (this.getPlayer().getJob() >= 6312) {
            effect = SkillFactory.getSkill(63120000).getEffect(1);
         }

         int add = effect.getY();
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ThanatosDescent) != null) {
            SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ThanatosDescent);
            if (e != null) {
               add = e.getY();
            }
         }

         if (this.getPlayer().checkInterval(this.lastUpdatePossessionTime, cycle * 1000)) {
            this.applyKainPossession(add);
            this.lastUpdatePossessionTime = System.currentTimeMillis();
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.KeyDownMoving) != null) {
         SecondaryStatEffect e_ = this.getPlayer().getBuffedEffect(SecondaryStatFlag.KeyDownMoving);
         if (e_ != null) {
            int mp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * e_.getMpRCon());
            this.getPlayer().addMP(-mp);
         }

         if (this.getPlayer().getTotalSkillLevel(63120039) > 0) {
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(63120039);
            if (e != null) {
               int x = e.getX();
               int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
               this.getPlayer().healHP(hp);
            }
         }
      }
   }

   public void onGripOfAgony(Point pos, boolean isLeft) {
      int stack = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.GripOfAgony, 0);
      if (stack > 0) {
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.GripOfAgony);
         if (effect != null) {
            int duration = effect.getS2() * 1000 + stack * effect.getS() * 1000;
            Skill skill = SkillFactory.getSkill(effect.getSourceId());
            if (skill != null) {
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  400031066,
                  SecondAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.GripOfAgony,
                  0,
                  null,
                  pos
               );
               SecondAtomData data = skill.getSecondAtomData();
               a.setPlayerID(this.getPlayer().getId());
               a.setExpire(duration);
               a.setAttackableCount(data.getAttackableCount());
               a.setEnableDelay(data.getEnableDelay());
               a.setUnk5(3);
               a.setPos(pos);
               a.setSkillID(400031066);
               this.getPlayer().addSecondAtom(a);
               SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 400031066, Collections.singletonList(a));
               this.getPlayer().getMap().createSecondAtom(secondAtom);
            }
         }
      }

      this.getPlayer().temporaryStatReset(SecondaryStatFlag.GripOfAgony);
   }

   public void applyKainPossession(int delta) {
      if (this.getPlayer().getJob() >= 6300 && this.getPlayer().getJob() <= 6312) {
         SecondaryStatEffect e = SkillFactory.getSkill(63101001).getEffect(1);
         if (this.getPlayer().getJob() >= 6312) {
            e = SkillFactory.getSkill(63120000).getEffect(1);
         }

         if (e != null) {
            int max = e.getV() * 100 + 99;
            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.Possession, 0);
            value = Math.max(0, Math.min(max, value + delta));
            this.getPlayer().temporaryStatSet(6003, Integer.MAX_VALUE, SecondaryStatFlag.Possession, value);
         }
      }
   }

   public boolean isShadowStep() {
      switch (this.getActiveSkillID()) {
         case 63001002:
         case 63001003:
         case 63001005:
            return true;
         case 63001004:
         default:
            return false;
      }
   }

   public void kainStackClear() {
      this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      if (this.getPlayer().getSecondAtomCount(SecondAtom.SecondAtomType.DragonFang) > 0) {
         for (SecondAtom.Atom atom : new ArrayList<>(this.getPlayer().getSecondAtoms())) {
            if (atom.getType().getType() == SecondAtom.SecondAtomType.DragonFang.getType()) {
               int expire = atom.getExpire();
               int delta = (int)Math.min((long)expire, System.currentTimeMillis() - atom.getCreateTime());
               delta = expire - delta;
               if (delta > 0) {
                  double r = (double)delta / expire;
                  SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.DragonFang);
                  if (e != null) {
                     int x = e.getX();
                     int max = x - 1;
                     this.dragonFangX = Math.min(max, (int)(x * r));
                  }

                  this.getPlayer().removeSecondAtom(atom.getKey());
                  this.getPlayer().getMap().removeSecondAtom(atom.getKey());
               }
            }
         }
      }
   }

   private boolean noWreckagePerHit(int skillId) {
      switch (skillId) {
         case 63101104:
            if (this.lastSkill == skillId) {
               this.wreckageCount = 0;
               return true;
            }

            int maxCount = 2;
            if (this.wreckageCount >= maxCount) {
               return true;
            }
            break;
         case 63121141:
            if (this.lastSkill == skillId) {
               this.wreckageCount = 0;
               return true;
            }

            maxCount = 8;
            if (this.wreckageCount >= maxCount) {
               return true;
            }
      }

      return false;
   }

   public void removeKainStack(int size, PacketDecoder slea) {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      for (int i = 0; i < size; i++) {
         int targetObjectID = slea.readInt();
         KainDeathBlessing kdb = this.kainDeathBlessing;
         if (kdb != null) {
            kdb.removeEntry(targetObjectID);
            kdb.updateDeathBlessing();
         }

         if (slea.available() < 4L) {
            return;
         }
      }
   }

   public void checkKainDeathBlessing() {
      if (this.kainDeathBlessing == null) {
         this.kainDeathBlessing = new KainDeathBlessing(this.getPlayer());
      }

      Skill deathBlessing = SkillFactory.getSkill(63110011);
      if (deathBlessing != null) {
         KainDeathBlessing kdb = this.kainDeathBlessing;
         if (kdb != null) {
            SecondaryStatEffect eff = SkillFactory.getSkill(63141503).getEffect(this.getPlayer().getTotalSkillLevel(63141503));
            Point lt = eff.getLt();
            Point rb = eff.getRb();

            for (MapleMonster mob : this.getPlayer().getMap().getMobsInRect(this.getPlayer().getPosition(), lt.x, lt.y, rb.x, rb.y)) {
               kdb.incrementStack(mob.getObjectId());
            }
         }
      }
   }
}
