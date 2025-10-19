package objects.users.jobs.flora;

import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.SecondAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Thief;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_PointWithDirection;
import objects.users.skills.TeleportAttackData_RectPos;
import objects.users.skills.TeleportAttackData_Skill;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Randomizer;

public class Khali extends Thief {
   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      switch (attack.skillID) {
         case 154101001:
         case 154101002:
         case 154141001:
         case 154141002:
            int skill = 154101001;
            if (attack.skillID == 154141001 || attack.skillID == 154141002) {
               skill = 154141001;
            }

            SecondaryStatEffect eff = SkillFactory.getSkill(skill).getEffect(this.getPlayer().getTotalSkillLevel(skill));
            List<Integer> cancelList = SkillFactory.getSkill(skill).getCancelableSkillID();
            int u = eff.getU();

            for (int cancelSkill : cancelList) {
               this.getPlayer().changeCooldown(cancelSkill, -u * 1000);
            }
         default:
            super.prepareAttack(attack, effect, opcode);
      }
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
      int skillLevel = 0;
      if ((skillLevel = this.getPlayer().getTotalSkillLevel(154110005)) > 0) {
         Skill s = SkillFactory.getSkill(154110005);
         SecondaryStatEffect level = s.getEffect(skillLevel);
         if (this.getPlayer().hasBuffBySkillID(154110005) && this.getPlayer().getCooldownLimit(154110005) <= 0L) {
            this.getPlayer()
               .sendRegisterExtraSkill(
                  attack.attackPosition, (attack.display & 32768) != 0, 154110005, 1, Collections.EMPTY_LIST, Collections.EMPTY_LIST, attackPair.objectid
               );
            this.getPlayer().send(CField.skillCooldown(154110005, (int)(level.getT() * 1000.0)));
            this.getPlayer().addCooldown(154110005, System.currentTimeMillis(), (int)(level.getT() * 1000.0));
         }

         if (level != null && s.getSkillList().contains(attack.skillID)) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.DeceivingBlade, 154110005, level.getDuration(), 1);
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      switch (attack.skillID) {
         case 154001000:
         case 154101000:
         case 154111002:
            if (attack.targets > 0) {
               int u = effect.getU();
               this.getPlayer().changeCooldown(154001003, -u * 1000);
               this.getPlayer().changeCooldown(154101004, -u * 1000);
               this.getPlayer().changeCooldown(154111004, -u * 1000);
               this.getPlayer().changeCooldown(154121009, -u * 1000);
               this.getPlayer().changeCooldown(154121003, -u * 1000);
            }
         case 154111004:
         case 154111011:
         default:
            break;
         case 400041085:
            int remain = this.getPlayer().getBuffedValue(SecondaryStatFlag.VoidBurst) - 1;
            int remainTime = (int)(this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.VoidBurst) - System.currentTimeMillis());
            if (remain <= 0) {
               this.getPlayer().temporaryStatResetBySkillID(400041084);
            } else {
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
               statups.put(SecondaryStatFlag.VoidBurst, remain);
               this.getPlayer().temporaryStatSet(400041084, effect.getLevel(), remainTime, statups, false, 0, true, false);
            }
      }

      int skillLevel = 0;
      if ((skillLevel = this.getPlayer().getTotalSkillLevel(400041087)) > 0 && this.getPlayer().getCooldownLimit(400041087) <= 0L) {
         Skill s = SkillFactory.getSkill(400041087);
         if (attack.targets > 0 && s.getSkillList().contains(attack.skillID)) {
            SecondaryStatEffect level = s.getEffect(skillLevel);
            if (level != null) {
               PacketEncoder p = new PacketEncoder();
               p.writeShort(SendPacketOpcode.ARTS_ASTRA_REQUEST.getValue());
               p.writeInt(400041087);
               this.getPlayer().send(p.getPacket());
               this.getPlayer().send(CField.skillCooldown(400041087, level.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(400041087, System.currentTimeMillis(), level.getCooldown(this.getPlayer()));
            }
         }
      }

      if (attack.targets > 0) {
         if (this.getPlayer().hasBuffBySkillID(154111000) && (boss || totalExp > 0L)) {
            Skill summonChakri = SkillFactory.getSkill(154110010);
            List<Integer> summonChakriList = summonChakri.getSkillList();
            if (summonChakriList.contains(attack.skillID)) {
               if (attack.skillID == 154121011 && attack.allDamage.size() >= 1) {
                  Skill voidBlitz = SkillFactory.getSkill(154121011);
                  SecondaryStatEffect blitzEff = voidBlitz.getEffect(attack.skillLevel);
                  Point pos = this.getPlayer().getPosition();
                  pos.x = pos.x + (ThreadLocalRandom.current().nextInt(400) - 200);
                  this.tryCreateSummonChakri(pos, attack.skillID, false);
               } else {
                  for (AttackPair pair : attack.allDamage) {
                     Point pos = pair.point;
                     pos.x = pos.x + (ThreadLocalRandom.current().nextInt(400) - 200);
                     this.tryCreateSummonChakri(pos, attack.skillID, false);
                  }
               }
            }
         }

         if (this.isHexSkill(attack.skillID)) {
            this.tryCreateResonate();
         }

         if (attack.skillID == 154121000 || attack.skillID == 154141000) {
            for (int coolDownSkillId : SkillFactory.getSkill(attack.skillID).getCancelableSkillID()) {
               this.getPlayer().changeCooldown(coolDownSkillId, -1000L);
            }
         }
      }

      Integer darkSight = this.getPlayer().getBuffedValue(SecondaryStatFlag.DarkSight);
      if (darkSight != null && attack.allDamage.size() > 0) {
         int[] skillIDs = new int[]{154001003, 154101004, 154101009, 154101010, 154111004, 154111011, 154121009, 154121012, 154121003, 154121011, 154110001};
         boolean find = false;
         int attackID = attack.skillID;

         for (int skillCheck : skillIDs) {
            if (attackID == skillCheck) {
               find = true;
            }
         }

         if (!find) {
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.DarkSight);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 150031005:
            this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.MaxLevelBuff, this.getActiveSkillID(), effect.getDuration(effect.getDuration(), this.getPlayer()), 4);
            break;
         case 154101004:
         case 154111004:
         case 154111011:
         case 154121009:
         case 154121012:
            packet.seek(packet.getPosition() - 4L);
            int flag = packet.readInt();
            if ((flag & 32) == 0) {
               if (this.getPlayer().hasBuffBySkillID(154111000)) {
                  this.tryCreateSummonChakriByCanceledSkill();
               }

               SecondaryStatEffect level = SkillFactory.getSkill(154101009).getEffect(effect.getLevel());
               if (level != null) {
                  this.getPlayer().temporaryStatSet(SecondaryStatFlag.DarkSight, 154101009, level.getDuration(), level.getLevel());
                  SecondaryStatEffect l = this.getPlayer().getSkillLevelData(154001003);
                  if (l != null) {
                     int skillID = 154001003;
                     if (this.getPlayer().getTotalSkillLevel(154101004) > 0) {
                        skillID = 154101004;
                     }

                     if (this.getPlayer().getTotalSkillLevel(154111004) > 0) {
                        skillID = 154111004;
                     }

                     if (this.getPlayer().getTotalSkillLevel(154121009) > 0) {
                        skillID = 154121009;
                     }

                     this.getPlayer().send(CField.skillCooldown(skillID, l.getCooldown(this.getPlayer())));
                     this.getPlayer().addCooldown(skillID, System.currentTimeMillis(), l.getCooldown(this.getPlayer()));
                  }

                  if (this.getActiveSkillID() != 154101004) {
                     effect.applyTo(this.getPlayer());
                  }
               }
            }
            break;
         case 154101005:
            this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.indieBooster, 154101005, effect.getDuration(effect.getDuration(), this.getPlayer()), effect.getX());
            break;
         case 154111000:
            if (this.getPlayer().hasBuffBySkillID(154111000)) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.SummonChakri);
            } else {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.SummonChakri, 154111000, Integer.MAX_VALUE, 1);
            }
            break;
         case 154111007:
            if (this.getPlayer().hasBuffBySkillID(154111007)) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.VoidEnhance);
            } else {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.VoidEnhance, 154111007, Integer.MAX_VALUE, 1);
            }
            break;
         case 154121003:
            if (this.getPlayer().hasBuffBySkillID(154111000)) {
               this.tryCreateSummonChakriByCanceledSkill();
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.DarkSight, 154121011, effect.getDuration(), effect.getLevel());
            break;
         case 154121004:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 154121004, effect.getDuration(), 1);
            break;
         case 154121005:
            this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.BasicStatUp, 154121005, effect.getDuration(effect.getDuration(), this.getPlayer()), effect.getX());
            break;
         case 154121041:
            Point position = new Point(packet.readShort(), packet.readShort());
            byte direction = packet.readByte();
            Summoned s = new Summoned(
               this.getPlayer(),
               this.getActiveSkillID(),
               this.getActiveSkillLevel(),
               position,
               SummonMoveAbility.STATIONARY,
               direction,
               System.currentTimeMillis() + effect.getDuration(effect.getDuration(), this.getPlayer())
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration(effect.getDuration(), this.getPlayer()));
            this.getPlayer().addSummon(s);
            break;
         case 154121042:
            this.getPlayer()
               .temporaryStatSet(SecondaryStatFlag.indieDamR, 154121042, effect.getDuration(effect.getDuration(), this.getPlayer()), effect.getIndieDamR());
            break;
         case 154121043:
            int[] voidSkills = new int[]{154001003, 154101004, 154101009, 154101010, 154111004, 154111011, 154121009, 154121012, 154121003, 154121011};

            for (int sid : voidSkills) {
               this.getPlayer().clearCooldown(sid);
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.Oblivion, 154121043, effect.getDuration(effect.getDuration(), this.getPlayer()), 1);
            break;
         case 154141502:
            SecondaryStatEffect eff = SkillFactory.getSkill(154141500).getEffect(this.getActiveSkillLevel());
            int q2 = eff.getQ2();
            int dur = effect.getDuration();
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePMDR, this.getActiveSkillID(), dur, q2);
            break;
         case 400041084:
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            statups.put(SecondaryStatFlag.VoidBurst, effect.getY());
            this.getPlayer().temporaryStatSet(effect.getSourceId(), effect.getLevel(), effect.getDuration() / 1000, statups, false, 0, true, false);
            break;
         case 400041086:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 400041086, effect.getSubTime() / 1000, 1);
            break;
         case 400041089:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ResonateUltimatum, 400041089, effect.getDuration(effect.getDuration(), this.getPlayer()), 1);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      this.checkSummonChakri();
      super.updatePerSecond();
   }

   public void tryCreateSummonChakri(Point point, int skillID, boolean byCanceled) {
      Skill skill = SkillFactory.getSkill(154110010);
      if (skill != null) {
         SecondaryStatEffect level = skill.getEffect(1);
         if (level != null
            && (skill.getSkillList().contains(skillID) || skill.getSkillList2().contains(skillID) || skill.getSkillList3().contains(skillID) || byCanceled)) {
            int max = level.getX();
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ResonateUltimatum) != null) {
               SecondaryStatEffect ru = this.getPlayer().getSkillLevelData(400041089);
               if (ru != null) {
                  max = ru.getX();
               }
            }

            int current = this.getPlayer().getMap().getSummonCount(this.getPlayer(), 154110010);
            if (max > current) {
               SecondaryStatEffect l = this.getPlayer().getSkillLevelData(154111000);
               if (l != null) {
                  int prob = l.getProb();
                  if (this.getPlayer().getTotalSkillLevel(154120010) > 0) {
                     SecondaryStatEffect ascend = this.getPlayer().getSkillLevelData(154120010);
                     if (ascend != null) {
                        prob += ascend.getW2();
                     }
                  }

                  if (Randomizer.isSuccess(prob) || byCanceled || this.getPlayer().getClient().isGm()) {
                     Point savePos = point;
                     point = this.getPlayer().getMap().calcPointBelow(point);
                     if (point == null) {
                        point = point;
                     }

                     point.x = point.x + Randomizer.rand(-200, 200);
                     point.y = point.y + Randomizer.rand(-100, 100);
                     Point lt = level.getLt();
                     Point rb = level.getRb();
                     if (this.getPlayer().getMap().getSummonedInRect(this.getPlayer().getId(), 154110010, point, lt.x, lt.y, rb.x, rb.y).size() > 0) {
                        boolean rePosition = false;

                        for (int i = 0; i < 5; i++) {
                           point = this.getPlayer().getMap().calcPointBelow(savePos);
                           point.x = point.x + Randomizer.rand(-200, 200);
                           point.y = point.y + Randomizer.rand(-100, 100);
                           if (this.getPlayer().getMap().getSummonedInRect(this.getPlayer().getId(), 154110010, point, lt.x, lt.y, rb.x, rb.y).size() == 0) {
                              rePosition = true;
                              break;
                           }
                        }

                        if (!rePosition) {
                           return;
                        }
                     }

                     Summoned summon = new Summoned(
                        this.getPlayer(), 154110010, 1, point, SummonMoveAbility.STATIONARY, (byte)0, System.currentTimeMillis() + l.getDuration()
                     );
                     this.getPlayer().getMap().spawnSummon(summon, l.getDuration(), false, false);
                     this.getPlayer().addSummon(summon);
                  }
               }
            }
         }
      }
   }

   public void checkSummonChakri() {
      List<Summoned> list = new ArrayList<>();
      this.getPlayer().getMap().getAllSummonsThreadsafe().forEach(s -> {
         if (s.getOwnerId() == this.getPlayer().getId()) {
            list.add(s);
         }
      });
      long time = System.currentTimeMillis();

      for (Summoned summoned : new ArrayList<>(list)) {
         if (summoned.getSummonRemoveTime() <= time) {
            this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summoned, true));
            this.getPlayer().getMap().removeMapObject(summoned);
            this.getPlayer().removeSummon(summoned);
         }
      }
   }

   public void tryCreateSummonChakriByCanceledSkill() {
      if (this.teleportAttackAction != null) {
         int chakriSkillID = 0;
         Point pos = new Point(0, 0);

         for (TeleportAttackElement element : new ArrayList<>(this.teleportAttackAction.actions)) {
            if (element.type == 3) {
               TeleportAttackData_Skill tad = (TeleportAttackData_Skill)element.data;
               chakriSkillID = tad.skillID;
            } else if (element.type == 4) {
               TeleportAttackData_RectPos p = (TeleportAttackData_RectPos)element.data;
               pos = p.getPos();
            } else if (element.type == 7) {
               TeleportAttackData_PointWithDirection p = (TeleportAttackData_PointWithDirection)element.data;
               pos = new Point(p.x, p.y);
            }
         }

         if (chakriSkillID > 0) {
            this.tryCreateSummonChakri(pos, chakriSkillID, true);
         }
      }
   }

   public void tryCreateResonate() {
      List<Point> posList = new ArrayList<>();
      Skill skill = SkillFactory.getSkill(154110001);
      SecondaryStatEffect level = skill.getEffect(this.getPlayer().getTotalSkillLevel(154110001));
      Point lt = level.getLt();
      Point rb = level.getRb();
      int ltx = lt.x;
      int lty = lt.y;
      int rbx = rb.x;
      int rby = rb.y;
      List<Summoned> summonedCheckRange = this.getPlayer()
         .getMap()
         .getSummonedInRect(this.getPlayer().getId(), 154110010, this.getPlayer().getPosition(), ltx, lty, rbx, rby);
      summonedCheckRange.stream().forEach(s -> {
         posList.add(s.getPosition());
         this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(s, true));
         this.getPlayer().getMap().removeMapObject(s);
         this.getPlayer().removeSummon(s);
      });
      List<SecondAtom.Atom> atoms = new ArrayList<>();
      int targetIndex = 0;

      for (Point pos : posList) {
         if (skill != null && level != null) {
            int bulletCount = level.getBulletCount();
            boolean ultimatum = false;
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ResonateUltimatum) != null) {
               SecondaryStatEffect ru = this.getPlayer().getSkillLevelData(400041089);
               if (ru != null) {
                  bulletCount = ru.getBulletCount();
                  ultimatum = true;
               }
            }

            List<MapleMonster> mobs = this.getPlayer().getMap().getMobsInRect(new Point(pos.x, pos.y), ltx, lty, rbx, rby);
            if (!mobs.isEmpty()) {
               CollectionUtil.sortMonsterByBossHP(mobs);
            }

            SecondAtomData data = skill.getSecondAtomData();

            for (SecondAtomData.atom d : data.getAtoms()) {
               for (int next = 0; next < bulletCount; next++) {
                  Point p = new Point(pos);
                  int startX = p.x - 30;
                  int delta = 120 / bulletCount;
                  if (ultimatum) {
                     p.x = startX + delta * next;
                  }

                  SecondAtom.Atom a = new SecondAtom.Atom(
                     this.getPlayer().getMap(), this.getPlayer().getId(), 154110001, SecondAtom.SN.getAndAdd(1), SecondAtom.SecondAtomType.Resonate, 0, null, p
                  );
                  a.setPlayerID(this.getPlayer().getId());
                  a.setCreateDelay(d.getCreateDelay());
                  a.setEnableDelay(d.getEnableDelay());
                  a.setSkillID(154110001);
                  a.setAngle(ThreadLocalRandom.current().nextInt(300) + 1);
                  a.setExpire(d.getExpire());
                  int attackableCount = d.getAttackableCount();
                  int slv = 0;
                  if (this.getPlayer().getTotalSkillLevel(154120010) > 0) {
                     SecondaryStatEffect ascend = this.getPlayer().getSkillLevelData(154120010);
                     attackableCount += ascend.getV();
                  }

                  a.setAttackableCount(attackableCount);
                  this.getPlayer().addSecondAtom(a);
                  atoms.add(a);
               }
            }

            if (targetIndex < mobs.size()) {
               for (int i = 0; i < atoms.size(); i++) {
                  atoms.get(i).setTargetObjectID(mobs.get(targetIndex).getObjectId());
               }

               targetIndex++;
            }
         }
      }

      SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 154110001, atoms);
      this.getPlayer().getMap().createSecondAtom(secondAtom);
   }

   public boolean isHexSkill(int skillID) {
      return skillID == 154111006 || skillID == 154121001 || skillID == 154121002 || skillID == 400041082;
   }
}
