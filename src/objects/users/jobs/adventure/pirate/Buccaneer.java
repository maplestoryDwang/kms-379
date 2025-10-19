package objects.users.jobs.adventure.pirate;

import constants.GameConstants;
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
import objects.fields.ForceAtom;
import objects.fields.SecondAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.users.MapleCoolDownValueHolder;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class Buccaneer extends DefaultPirate {
   public int SerpentSpiritCount = 0;
   public long NextSeaSerpentAttackTime = 0L;
   public final int SeaSerpent = 5101017;
   public final int SeaSerpentBurstPassive = 5100018;
   public final int SeaSerpentBurstActive = 5101019;
   public final int AdvSeaSerpentPassive = 5110016;
   public final int SerpentStone = 5111017;
   public final int SerpentAssault = 5110018;
   public final int AdvSeaSerpentActive = 5111021;
   public final int TimeLeap = 5121010;
   public final int AdvSeaSerpent2 = 5120022;
   public final int SeaSerpentBurst2Active = 5121023;
   public final int SeaSerpentEnragePassive = 5120024;
   public final int SeaSerpentEnrageActive = 5121025;
   public final int SerpentAssaultEnrage = 5121027;
   public final int SeaSerpentBurst2Passive = 5120029;
   public final int SerpentScrew = 400051015;
   public final int LiberateNeptuneAttack = 5141500;
   public final int LiberateNeptuneBuff = 5141501;
   public final int[] LiberateNeptuneEnrage = new int[]{5141503, 5141504, 5141505, 5141505, 5141506};

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
      if (attack.skillID == 5121001) {
         monster.applyStatus(
            this.getPlayer(),
            new MobTemporaryStatEffect(MobTemporaryStatFlag.MULTI_PMDR, effect.getX(), attack.skillID, null, false),
            false,
            effect.getDuration(),
            false,
            effect
         );
      } else if (attack.skillID == 5121025 && this.getPlayer().hasBuffBySkillID(5121052)) {
         this.addSerpentSpirit(monster);
      }

      if (this.getPlayer().getTotalSkillLevel(5110000) > 0) {
         SecondaryStatEffect mastery = this.getPlayer().getSkillLevelData(5110000);
         if (Randomizer.isSuccess(mastery.getSubProp())) {
            monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, 5110000, null, false), false, mastery.getDuration(), false, mastery
            );
         }
      }

      if (totalDamage > 0L && this.getPlayer().hasBuffBySkillID(5141501)) {
         int enrageSkillID = this.LiberateNeptuneEnrage[0];
         if (this.getPlayer().getRemainCooltime(enrageSkillID) <= 0L) {
            List<Integer> list = SkillFactory.getSkill(5141501).getSkillList();
            if (list.contains(attack.skillID)) {
               this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 5141501);
               int cooldown = SkillFactory.getSkill(enrageSkillID).getEffect(this.getPlayer().getTotalSkillLevel(enrageSkillID)).getCoolTime();
               this.getPlayer().getClient().getSession().writeAndFlush(CField.skillCooldown(this.LiberateNeptuneEnrage[0], cooldown));
               this.getPlayer().addCooldown(enrageSkillID, System.currentTimeMillis(), cooldown);
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.indieSummon, 5111017) != null) {
         if (attack.skillID != 400051015) {
            this.registerSerpentStoneAttack(attack);
         }
      } else if (this.getPlayer().hasBuffBySkillID(5101017)) {
         this.registerSeaSerpentAttack(attack);
      }

      if ((
            attack.skillID == 5121025
               || attack.skillID == 5101019
               || attack.skillID == 5110018
               || attack.skillID == 5121027
               || attack.skillID == 5100018
               || attack.skillID == 5110016
               || attack.skillID == 5120024
               || attack.skillID == 5120029
               || attack.skillID == 5121023
               || attack.skillID == 5120022
         )
         && !this.getPlayer().hasBuffBySkillID(5120011)) {
         Skill divine = SkillFactory.getSkill(5120011);
         SecondaryStatEffect divineShield = divine.getEffect(this.getPlayer().getTotalSkillLevel(divine));
         if (this.getPlayer().getTotalSkillLevel(divine) > 0 && !this.getPlayer().skillisCooling(divine.getId())) {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieDamReduceR, -divineShield.getY());
            statups.put(SecondaryStatFlag.indieFlyAcc, 1);
            this.getPlayer().temporaryStatSet(5120011, this.getPlayer().getTotalSkillLevel(5120011), 3000, statups);
            this.getPlayer().getClient().getSession().writeAndFlush(CField.skillCooldown(divine.getId(), divineShield.getCooldown(this.getPlayer())));
            this.getPlayer().addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this.getPlayer()));
         }
      }

      if (attack.skillID == 5121027 && this.getPlayer().getTotalSkillLevel(5141000) > 0) {
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieSummon, 5141001, 10000, 2);
      }

      switch (attack.skillID) {
         case 5121025:
            this.setSerpentEnrageMark(attack);
            break;
         case 400051015:
            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SerpentScrew, 0);
            if (value > 1) {
               SecondaryStat stat = this.getPlayer().getSecondaryStat();
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), stat);
               if (boss) {
                  stat.SerpentScrewBossAttackCount--;
                  if (stat.SerpentScrewBossAttackCount <= 0) {
                     statManager.changeStatValue(SecondaryStatFlag.SerpentScrew, 400051015, value - 1);
                     stat.SerpentScrewBossAttackCount = effect.getQ();
                  }
               } else {
                  statManager.changeStatValue(SecondaryStatFlag.SerpentScrew, 400051015, value - 1);
                  stat.SerpentScrewRemainCount -= multiKill;
                  if (stat.SerpentScrewRemainCount <= 0) {
                     this.getPlayer().changeCooldown(400051015, -effect.getY() * 1000L);
                     stat.SerpentScrewRemainCount = effect.getW2();
                     if (this.getPlayer().getRemainCooltime(400051015) <= 0L) {
                        this.getPlayer().invokeJobMethod("resetSerpentScrewValue");
                     }
                  }
               }

               statManager.temporaryStatSet();
            } else {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.SerpentScrew);
            }
            break;
         case 400051042:
            this.getPlayer().setAutoChargeStack(this.getPlayer().getAutoChargeStack() - 1);
            this.getPlayer().temporaryStatSet(400051042, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, this.getPlayer().getAutoChargeStack());
      }

      if (totalDamage > 0L && this.getPlayer().getTotalSkillLevel(5120028) > 0) {
         SecondaryStatEffect offenseForm = this.getPlayer().getSkillLevelData(5120028);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieDamR, 5120028, offenseForm.getDuration(), offenseForm.getIndieDamR());
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
         case 5121010:
            if (this.getPlayer().getParty() != null) {
               for (MapleCharacter player : this.getPlayer().getPartyMembers()) {
                  if (player.getBuffedValue(SecondaryStatFlag.ViperTimeLeap) == null
                     && player.getId() != this.getPlayer().getId()
                     && this.getPlayer().getMap().getCharacterById(player.getId()) != null) {
                     for (MapleCoolDownValueHolder i : player.getCooldowns()) {
                        if (GameConstants.isResettableCooltimeSkill(i.skillId)) {
                           player.clearCooldown(i.skillId);
                        }
                     }
                  }
               }
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ViperTimeLeap) == null) {
               for (MapleCoolDownValueHolder ix : this.getPlayer().getCooldowns()) {
                  if (GameConstants.isResettableCooltimeSkill(ix.skillId)) {
                     this.getPlayer().clearCooldown(ix.skillId);
                  }
               }
            }

            super.onActiveSkill(skill, effect, packet);
            break;
         case 5121027:
            packet.skip(4);
            SecondAtom.Atom a = new SecondAtom.Atom(
               this.getPlayer().getMap(),
               this.getPlayer().getId(),
               5121027,
               ForceAtom.SN.getAndAdd(1),
               SecondAtom.SecondAtomType.SerpentAssaultEnrage,
               0,
               null,
               new Point(packet.readInt(), packet.readInt())
            );
            SecondAtomData data = skill.getSecondAtomData();
            a.setPlayerID(this.getPlayer().getId());
            a.setExpire(data.getExpire());
            a.setCreateDelay(data.getCreateDelay());
            a.setEnableDelay(data.getEnableDelay());
            a.setRotate(data.getRotate());
            a.setAttackableCount(data.getAttackableCount());
            a.setSkillID(5121027);
            this.getPlayer().addSecondAtom(a);
            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 5121027, Collections.singletonList(a));
            this.getPlayer().getMap().createSecondAtom(secondAtom);
            break;
         case 5141501:
            SecondaryStatEffect eff = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel());
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieSummon, 5141501, eff.getDuration(), 1);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400051015) {
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(400051015);
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.SerpentScrew);
         if (effect != null && value != null) {
            long remainTime = value / effect.getU() * effect.getX() * 1000L;
            this.getPlayer().changeCooldown(400051015, -remainTime);
         }
      } else if (this.getActiveSkillID() == 400051070) {
         this.getPlayer().temporaryStatSet(400051071, 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
   }

   public void resetSerpentScrewValue() {
      SecondaryStatEffect effect;
      if ((effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.SerpentScrew)) != null) {
         SecondaryStat stat = this.getPlayer().getSecondaryStat();
         stat.SerpentScrewRemainCount = effect.getW2();
         stat.SerpentScrewBossAttackCount = effect.getQ();
         SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), stat);
         statManager.changeStatValue(SecondaryStatFlag.SerpentScrew, 400051015, effect.getV());
         statManager.temporaryStatSet();
         this.getPlayer().removeCooldown(400051015);
         this.getPlayer().addCooldown(400051015, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
         this.getPlayer().send(CField.skillCooldown(400051015, effect.getCooldown(this.getPlayer())));
      }
   }

   private void setSeaSerpentSkillCoolTime(List<ExtraSkillInfo> extraSkillInfo) {
      this.NextSeaSerpentAttackTime = System.currentTimeMillis();

      for (ExtraSkillInfo skill : extraSkillInfo) {
         switch (GameConstants.getLinkedAranSkill(skill.skillID)) {
            case 5100018:
               this.NextSeaSerpentAttackTime = this.NextSeaSerpentAttackTime
                  + this.getPlayer().getSkillLevelDataOne(5100018, SecondaryStatEffect::getCoolTime) / (this.getPlayer().hasBuffBySkillID(5121054) ? 2 : 1);
               break;
            case 5110016:
               this.NextSeaSerpentAttackTime = this.NextSeaSerpentAttackTime
                  + this.getPlayer().getSkillLevelDataOne(5110016, SecondaryStatEffect::getZ) * 500L * (this.getPlayer().hasBuffBySkillID(5121054) ? 1 : 2);
               break;
            case 5120024:
               Skill sk = SkillFactory.getSkill(5120024);
               if (sk != null) {
                  SecondaryStatEffect eff = sk.getEffect(this.getPlayer().getTotalSkillLevel(5120022));
                  long time = eff.getV() * 500L * (this.getPlayer().hasBuffBySkillID(5121054) ? 1 : 2);
                  this.NextSeaSerpentAttackTime += time;
               }
               break;
            case 5120029:
               this.NextSeaSerpentAttackTime = this.NextSeaSerpentAttackTime
                  + this.getPlayer().getSkillLevelDataOne(5120029, SecondaryStatEffect::getZ) * 500L * (this.getPlayer().hasBuffBySkillID(5121054) ? 1 : 2);
         }
      }
   }

   public void addSerpentSpirit(MapleMonster monster) {
      if (monster.isAdventurerMarkSet()) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(5121052);
         this.SerpentSpiritCount = Math.min(e.getU(), this.SerpentSpiritCount + 1);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.SerpentSpirit, 5121055, e.getW() * 1000, this.SerpentSpiritCount);
      }
   }

   public void setSerpentSpirit(int skillID) {
      if (skillID == 5121052) {
         this.getPlayer().temporaryStatResetBySkillID(5121055);
      } else if (skillID == 5121055) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(5121052);
         if (this.getPlayer().hasBuffBySkillID(5121052)) {
            this.SerpentSpiritCount = Math.max(0, this.SerpentSpiritCount - 1);
            if (this.SerpentSpiritCount > 0) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.SerpentSpirit, 5121055, e.getW() * 1000, this.SerpentSpiritCount);
            }
         }
      }
   }

   public void setSerpentEnrageMark(AttackInfo attack) {
      List<MapleMonster> targetList = new ArrayList<>();
      if (attack != null) {
         attack.allDamage.forEach(object -> {
            int objectID = object.objectid;
            MapleMonster targetx;
            if ((targetx = this.getPlayer().getMap().getMonsterByOid(objectID)) != null && !targetx.isAdventurerMarkSet()) {
               targetx.setAdventurerMark(true);
               targetx.setAdventurerMarkCancelTime(System.currentTimeMillis() + 15000L);
               targetList.add(targetx);
            }
         });
      }

      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_ADVENTURER_MARK.getValue());
      packet.writeInt(5121025);
      packet.write(attack != null);
      if (attack != null) {
         packet.writeInt(targetList.size());

         for (MapleMonster target : targetList) {
            packet.writeInt(target.getObjectId());
            packet.writeInt(1);
            packet.writeInt(0);
            packet.writeInt(15000);
            packet.writeInt(0);
         }
      }

      this.getPlayer().send(packet.getPacket());
   }

   public void registerSeaSerpentAttack(AttackInfo attack) {
      Skill skill = SkillFactory.getSkill(5101017);
      if (skill != null) {
         if (skill.getExtraSkillInfo() != null && !skill.getExtraSkillInfo().isEmpty()) {
            List<ExtraSkillInfo> extraSkills = new ArrayList<>(skill.getExtraSkillInfo());
            if (!extraSkills.isEmpty()) {
               if (skill.getSkillList().contains(attack.skillID)) {
                  extraSkills.removeIf(info -> info.delay <= 0);
                  int index = -1;

                  for (int i = 0; i < extraSkills.size(); i++) {
                     if (this.getPlayer().getTotalSkillLevel(extraSkills.get(i).skillID) > 0) {
                        index = i;
                     }
                  }

                  if (index >= 0) {
                     ExtraSkillInfo extraSkill = extraSkills.get(index);
                     if (this.NextSeaSerpentAttackTime >= 0L && this.NextSeaSerpentAttackTime <= System.currentTimeMillis()) {
                        this.getPlayer()
                           .send(
                              CField.getRegisterExtraSkill(
                                 5101017,
                                 attack.attackPosition.x,
                                 attack.attackPosition.y,
                                 (attack.display & 32768) != 0,
                                 Collections.singletonList(extraSkill),
                                 1,
                                 Collections.emptyList(),
                                 Collections.emptyList(),
                                 0
                              )
                           );
                        this.setSeaSerpentSkillCoolTime(Collections.singletonList(extraSkill));
                        if (this.getPlayer().getTotalSkillLevel(5111017) > 0) {
                           int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SerpentStone, 0);
                           int u = 5;
                           this.getPlayer().temporaryStatSet(SecondaryStatFlag.SerpentStone, 5111017, Integer.MAX_VALUE, Math.min(u, value + 1));
                           if (value + 1 >= u && this.getPlayer().getOneInfoQuest(1544, String.valueOf(5111017)).equals("1")) {
                              SecondaryStatEffect e = this.getPlayer().getSkillLevelData(5111017);
                              e.applyTo(this.getPlayer());
                           }
                        }
                     }
                  }
               } else if (skill.getSkillList2().contains(attack.skillID)) {
                  extraSkills.removeIf(info -> info.delay > 0);
                  if (this.getPlayer().getTotalSkillLevel(5120022) > 0
                     && this.NextSeaSerpentAttackTime >= 0L
                     && this.NextSeaSerpentAttackTime <= System.currentTimeMillis()) {
                     this.getPlayer()
                        .send(
                           CField.getRegisterExtraSkill(
                              5101017,
                              attack.attackPosition.x,
                              attack.attackPosition.y,
                              (attack.display & 32768) != 0,
                              extraSkills,
                              1,
                              Collections.emptyList(),
                              Collections.emptyList(),
                              0
                           )
                        );
                     this.setSeaSerpentSkillCoolTime(extraSkills);
                     if (this.getPlayer().getTotalSkillLevel(5111017) > 0) {
                        int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SerpentStone, 0);
                        int u = 5;
                        this.getPlayer().temporaryStatSet(SecondaryStatFlag.SerpentStone, 5111017, Integer.MAX_VALUE, Math.min(u, value + 1));
                        if (value + 1 >= u && this.getPlayer().getOneInfoQuest(1544, String.valueOf(5111017)).equals("1")) {
                           SecondaryStatEffect e = this.getPlayer().getSkillLevelData(5111017);
                           e.applyTo(this.getPlayer());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void registerSerpentStoneAttack(AttackInfo attack) {
      Skill skill = SkillFactory.getSkill(5110018);
      if (skill != null) {
         if (skill.getExtraSkillInfo() != null && !skill.getExtraSkillInfo().isEmpty()) {
            List<ExtraSkillInfo> extraSkills = new ArrayList<>(skill.getExtraSkillInfo());
            if (!extraSkills.isEmpty()) {
               if (attack.skillID != 5121025
                  && attack.skillID != 5121007
                  && attack.skillID != 5120021
                  && (attack.skillID < 5141000 || attack.skillID > 5141003)) {
                  extraSkills.removeIf(info -> info.delay <= 0);
               } else {
                  extraSkills.removeIf(info -> info.delay > 0);
               }

               this.getPlayer()
                  .send(
                     CField.getRegisterExtraSkill(
                        5110018,
                        attack.attackPosition.x,
                        attack.attackPosition.y,
                        (attack.display & 32768) != 0,
                        extraSkills,
                        1,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        0
                     )
                  );
               this.getPlayer().temporaryStatResetBySkillID(5111017);
            }
         }
      }
   }
}
