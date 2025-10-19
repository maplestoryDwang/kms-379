package objects.users.jobs.hero;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.SkillEffect;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.Wreckage;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Magician;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Evan extends Magician {
   public boolean activeJodiacRay = false;
   public int zodiacBurstCount = 0;
   private List<Integer> jodiacRaySkills = new ArrayList<>();

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400021012 || attack.skillID == 400021046) {
         this.getPlayer().sendRegisterExtraSkill(this.getPlayer().getPosition(), (attack.display & 32768) != 0, attack.skillID);
      }

      if (attack.targets != 0 && attack.skillID != 22170070 && attack.skillID != 22141017) {
         int wreckageSkillID = 22141017;
         if (this.getPlayer().getSkillLevel(22170070) > 0) {
            wreckageSkillID = 22170070;
         }

         if (this.getPlayer().getCooldownLimit(wreckageSkillID) == 0L && this.getPlayer().getSkillLevel(wreckageSkillID) > 0) {
            List<Point> mobList = new ArrayList<>();
            attack.allDamage.forEach(m -> {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(m.objectid);
               if (mob != null) {
                  mobList.add(mob.getPosition());
               }
            });
            SecondaryStatEffect e = SkillFactory.getSkill(wreckageSkillID).getEffect(this.getPlayer().getSkillLevel(wreckageSkillID));
            if (mobList.size() > 0) {
               int i = 1;

               for (Point pos : mobList) {
                  if (this.getPlayer().getWreckageCount() >= e.getX()) {
                     break;
                  }

                  this.getPlayer()
                     .getMap()
                     .spawnWreckage(new Wreckage(this.getPlayer(), e.getDuration() / 1000, wreckageSkillID, this.getPlayer().incAndGetWreckageCount(), pos));
               }

               this.getPlayer().addCooldown(wreckageSkillID, System.currentTimeMillis(), wreckageSkillID == 22141017 ? 600L : 400L);
            }
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
      if (totalDamage > 0L) {
         Summoned summoned = this.getPlayer().getSummonBySkillID(400021073);
         if (summoned != null) {
            Skill jodiacRay = SkillFactory.getSkill(400021073);
            if (!this.activeJodiacRay && jodiacRay != null) {
               int inc = 0;
               int sid = attack.skillID;
               if (sid == 22170060) {
                  sid = 22170061;
               }

               if (jodiacRay.getSkillList2().contains(sid)) {
                  SecondaryStatEffect e = jodiacRay.getEffect(summoned.getSkillLevel());
                  if (e != null && !this.jodiacRaySkills.contains(sid)) {
                     inc = e.getY();
                     this.jodiacRaySkills.add(sid);
                  }
               } else if (jodiacRay.getSkillList3().contains(sid)) {
                  inc = 1;
               }

               if (inc > 0) {
                  SecondaryStatEffect e = jodiacRay.getEffect(summoned.getSkillLevel());
                  if (e != null) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initJodiacRay(monster.getPosition());
                     ForceAtom forceAtom = new ForceAtom(
                        info,
                        400021073,
                        this.getPlayer().getId(),
                        true,
                        false,
                        monster.getObjectId(),
                        ForceAtom.AtomType.ENERGY_BURST,
                        Collections.EMPTY_LIST,
                        inc
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                     summoned.addEnergyCharge(inc, 30);
                     this.getPlayer().getMap().broadcastMessage(CField.summonSetEnergy(this.getPlayer(), summoned, 2));
                     if (summoned.getEnergyCharge() >= e.getX()) {
                        this.activeJodiacRay = true;
                        this.getPlayer().getMap().broadcastMessage(CField.summonForcedAction(this.getPlayer(), summoned.getObjectId(), 8));
                        SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                        int remain = (int)(
                           this.getPlayer().getSecondaryStat().getIndieTill(SecondaryStatFlag.indieSummon, 400021073) - System.currentTimeMillis()
                        );
                        int passed = e.getDuration() - remain + 1000;
                        statManager.changeTill(SecondaryStatFlag.indieSummon, 400021073, passed);
                        statManager.temporaryStatSet();
                     }
                  }
               }
            }
         }
      }

      if (attack.targets > 0
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.ZodiacBurst) != null
         && attack.skillID != 22201500
         && attack.skillID != 22201501
         && this.getPlayer().getRemainCooltime(22201501) <= 0L) {
         this.getPlayer().send(CField.zodiacBurstEnable(this.zodiacBurstCount++));
         Skill zodiac = SkillFactory.getSkill(22201500);
         List<ExtraSkillInfo> extraSkills = new ArrayList<>(zodiac.getExtraSkillInfo());

         for (int cnt = 0; cnt < extraSkills.size(); cnt++) {
            this.getPlayer()
               .send(
                  CField.getRegisterExtraSkill(
                     22201500, attack.forcedPos.x, attack.forcedPos.y, (attack.display & 32768) != 0, extraSkills.get(cnt), cnt, cnt == 0 ? 0 : 1
                  )
               );
         }

         this.getPlayer().send(CField.skillCooldown(22201501, 3000));
         this.getPlayer().addCooldown(22201501, System.currentTimeMillis(), 3000L);
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      this.tryApplyElementBlastBuff(attack.skillID);
      if (GameConstants.isEvanDragonSkill(attack.skillID) && this.getPlayer().getTotalSkillLevel(22110016) > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(22110016).getEffect(this.getPlayer().getTotalSkillLevel(22110016));
         if (eff != null) {
            Map<SecondaryStatFlag, Integer> statups = new EnumMap<>(SecondaryStatFlag.class);
            statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(eff.getIndieDamR()));
            this.getPlayer().temporaryStatSet(22110016, eff.getLevel(), eff.getDuration(), statups);
         }
      }

      if (totalDamage > 0L) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(22000013);
         if (eff != null && Randomizer.nextInt(100) < 15) {
            int recoveryMp;
            if (boss) {
               recoveryMp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) / 100L * 3L);
            } else {
               recoveryMp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) / 100L * 5L);
            }

            this.getPlayer().addMP(recoveryMp);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 22110013:
            int mobCount = packet.readByte();
            List<Integer> targets = new ArrayList<>();

            for (int i = 0; i < mobCount; i++) {
               targets.add(packet.readInt());
            }

            for (int objectID : targets) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(objectID);
               if (mob != null) {
                  mob.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.WEAKNESS, effect.getX(), this.getActiveSkillID(), null, false),
                     false,
                     effect.getDuration(),
                     false,
                     effect
                  );
               }
            }

            effect.applyTo(this.getPlayer(), true);
            SecondaryStatEffect exx = this.getPlayer().getSkillLevelData(22111017);
            if (exx != null) {
               this.getPlayer().send(CField.skillCooldown(22111017, exx.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(22111017, System.currentTimeMillis(), exx.getCooldown(this.getPlayer()));
            }
            break;
         case 22140013:
            List<MapleCharacter> players = this.getPlayer()
               .getMap()
               .getPlayerInRect(this.getPlayer().getPosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y);
            players.stream()
               .filter(
                  p -> p.getId() == this.getPlayer().getId()
                     || this.getPlayer().getParty() != null && p.getParty() != null && p.getParty().getId() == this.getPlayer().getParty().getId()
               )
               .forEach(p -> p.temporaryStatSet(22140013, effect.getDuration(), SecondaryStatFlag.indieBooster, effect.getIndieBooster()));
            effect.applyTo(this.getPlayer(), true);
            SecondaryStatEffect e = this.getPlayer().getSkillLevelData(22111017);
            if (e != null) {
               this.getPlayer().send(CField.skillCooldown(22111017, e.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(22111017, System.currentTimeMillis(), e.getCooldown(this.getPlayer()));
            }
            break;
         case 22141017:
         case 22170070:
            Field field = this.getPlayer().getMap();
            List<MapleMonster> mobs = this.getPlayer()
               .getMap()
               .getMobsInRect(
                  this.getPlayer().getPosition(), effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y, this.getPlayer().isFacingLeft()
               );
            List<Integer> mobList = new LinkedList<>();
            List<Wreckage> wreckageList = new LinkedList<>();
            int i = 0;
            List<Point> position = new ArrayList<>();
            field.getAllWreakage().stream().filter(w -> w.getOwner().getId() == this.getPlayer().getId()).forEach(w -> {
               wreckageList.add(w);
               position.add(w.getPosition());
               w.removeWreckage(field, true);
            });
            this.getPlayer().setWreckageCount(0);
            wreckageList.sort(Comparator.comparingInt(MapleMapObject::getObjectId));

            for (MapleMonster mo : mobs) {
               mobList.add(mo.getObjectId());
               if (++i >= 15) {
                  break;
               }
            }

            field.broadcastMessage(CField.DelWreckage(this.getPlayer().getId(), wreckageList, this.getPlayer().getCooldownLimit(22171095) != 0L));
            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initMagicWreckage(position);
            ForceAtom forceAtom = new ForceAtom(
               info,
               this.getActiveSkillID(),
               this.getPlayer().getId(),
               false,
               true,
               this.getPlayer().getId(),
               this.getActiveSkillID() == 22170070 ? ForceAtom.AtomType.SUPER_MAGIC_WRECKAGE : ForceAtom.AtomType.MAGIC_WRECKAGE,
               mobList,
               wreckageList.size()
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            effect.applyTo(this.getPlayer(), true);
            break;
         case 22170064:
            SecondaryStatEffect eff = SkillFactory.getSkill(22170093).getEffect(this.getActiveSkillLevel());
            if (eff != null) {
               this.getPlayer().sendAffectedArea(this.getPlayer().getPosition(), eff);
            }

            e = this.getPlayer().getSkillLevelData(22111017);
            if (e != null) {
               this.getPlayer().send(CField.skillCooldown(22111017, e.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(22111017, System.currentTimeMillis(), e.getCooldown(this.getPlayer()));
            }
            break;
         case 22201500:
            int duration = effect.getDuration();
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ZodiacBurst, 22201501, duration, 1);
            this.zodiacBurstCount = 0;
            break;
         case 400020046: {
            Point pos = packet.readPos();
            packet.skip(4);
            byte flip = packet.readByte();
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), flip == 1);
            AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
            area.setRlType(flip);
            this.getPlayer().getMap().spawnMist(area);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400020051: {
            Point pos = packet.readPos();
            packet.skip(4);
            byte flip = packet.readByte();
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), flip == 1);
            AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration());
            this.getPlayer().getMap().spawnMist(area);
            effect.applyTo(this.getPlayer(), true);
            SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(22111017);
            if (ex != null) {
               this.getPlayer().send(CField.skillCooldown(22111017, ex.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(22111017, System.currentTimeMillis(), ex.getCooldown(this.getPlayer()));
            }
            break;
         }
         case 400021095:
            int x = packet.readShort();
            int y = packet.readShort();
            byte isLeft = packet.readByte();
            this.getPlayer().temporaryStatResetBySkillID(400021095);
            Summoned s = new Summoned(
               this.getPlayer(),
               400021095,
               this.getActiveSkillLevel(),
               new Point(x, y),
               SummonMoveAbility.STATIONARY,
               isLeft,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(s, effect.getDuration());
            this.getPlayer().addSummon(s);
            this.getPlayer().addMP(-effect.getMPCon());
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 22171083) {
         SecondaryStat sst = this.getPlayer().getSecondaryStat();
         long till = sst.getTill(SecondaryStatFlag.RideVehicleExpire);
         int offset = (int)((till - System.currentTimeMillis()) / 1000L);
         if (offset > 0) {
            int reduce = 18 * offset;
            if (reduce > 160) {
               reduce = 160;
            }

            this.getPlayer().changeCooldown(22171080, -reduce * 1000L);
         }

         this.getPlayer().temporaryStatSet(SecondaryStatFlag.NotDamaged, 22171080, 1000, 1);
      }

      super.activeSkillCancel();
   }

   public void removeSummon(Summoned s) {
      if (s.getSkill() == 400021073) {
         this.clearJodiacRay();
      }
   }

   @Override
   public void updatePerSecond() {
      if (this.getPlayer().getTotalSkillLevel(22170074) > 0) {
         SecondaryStatEffect dragonFury = this.getPlayer().getSkillLevelData(22170074);
         byte skillType = -1;
         if (this.getPlayer().getStat().getHPPercent() >= dragonFury.getX() && this.getPlayer().getStat().getMPPercent() <= dragonFury.getY()) {
            if (!this.getPlayer().hasBuffBySkillID(22170074)) {
               this.getPlayer().temporaryStatSet(22170074, Integer.MAX_VALUE, SecondaryStatFlag.indieMadR, dragonFury.getDamage());
               skillType = 1;
            }
         } else if (this.getPlayer().hasBuffBySkillID(22170074)) {
            this.getPlayer().temporaryStatResetBySkillID(22170074);
            skillType = 0;
         }

         if (skillType >= 0) {
            PacketEncoder p = new PacketEncoder();
            p.write(skillType);
            SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), 22170074, this.getPlayer().getTotalSkillLevel(22170074), p);
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
         }
      }
   }

   public void clearJodiacRay() {
      this.jodiacRaySkills.clear();
      this.activeJodiacRay = false;
   }

   public void tryApplyElementBlastBuff(int skillID) {
      if (skillID >= 400021012 && skillID <= 400021015) {
         int sid = 400021012;
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(sid);
         if (eff != null) {
            int buffTime = eff.getX() * 1000;
            IndieTemporaryStatEntry e = this.getPlayer().getIndieTemporaryStat(SecondaryStatFlag.indiePMDR, 400021012);
            int value = 0;
            if (e != null) {
               value += e.getStatValue();
            }

            this.getPlayer().temporaryStatSet(400021012, buffTime, SecondaryStatFlag.indiePMDR, value + eff.getY());
         }
      }
   }
}
