package objects.users.jobs.anima;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.ForceAtom;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.jobs.Thief;
import objects.users.skills.HoyoungAttributes;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.CollectionUtil;
import objects.utils.Randomizer;

public class Hoyoung extends Thief {
   private long lastCommingOfTheSuperpoweredGodsTime = 0L;
   private int charmPowerGauge = 0;
   private int scrollPowerGauge = 0;
   private int hoyoungAttributes = HoyoungAttributes.None.getType();
   private int hoyoungAllThingsAttributes = HoyoungAttributes.None.getType();
   private long dreamGardenChargingStartTime = 0L;
   private int commingOfTheSuperpoweredGodsCount = 0;
   private int remainHeavenEarthHumanApparition = 0;
   private int ConsumingFlamesAttackConut = 0;
   public int[] attributeSkills = new int[]{164111003, 164121000, 164121003};
   final int ShatteringEarth = 164101000;
   final int ShatteringWaveTrue = 164101001;
   final int ShatteringWaveClone = 164101002;
   final int IronFanGaleHeaven = 164111000;
   final int IronFanGaleTrue = 164111001;
   final int IronFanGaleClone = 164111002;
   final int IronFanGaleTrueAttack = 164111009;
   final int IronFanGaleCloneAttack = 164111010;
   final int StoneTremorEarth = 164111003;
   final int StoneTremorTrueLeft = 164111004;
   final int StoneTremorTrueRight = 164111005;
   final int StoneTremorClone = 164111006;
   final int ConsumingFlamesHeaven = 164120000;
   final int ConsumingFlamesTrue = 164121001;
   final int ConsumingFlamesClone = 164121002;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (this.getPlayer().getSkillLevel(164110014) > 0 && SkillFactory.getSkill(164110014).getSkillListSub().contains(attack.skillID)) {
         this.getPlayer().getSecondaryStat().HoyoungLastCheonJiInSkill = attack.skillID;
         this.getPlayer().temporaryStatSet(164110014, 2100000000, SecondaryStatFlag.HoyoungLastCheonJiInSkillSet, 1);
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
      if (attack.skillID == 164111003) {
         monster.applyStatus(
            this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attack.skillID, null, false), false, effect.getDuration(), true, effect
         );
      }

      if (attack.skillID == 164001001) {
         monster.setSuctionBottlePlayerID(this.getPlayer().getId());
         monster.applyStatus(
            this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.SUCTION_BOTTLE, 1, 164001001, null, false), false, 23000L, true, effect
         );
         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
         info.initSuctionBottle();
         ForceAtom forceAtom = new ForceAtom(
            info,
            164001001,
            this.getPlayer().getId(),
            true,
            true,
            monster.getObjectId(),
            ForceAtom.AtomType.SUCTION_BOTTLE,
            Collections.singletonList(this.getPlayer().getId()),
            1
         );
         this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
      }

      if (attack.skillID == 164111008) {
         if (monster.getStats().isBoss()) {
            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, effect.getV2(), attack.skillID, null, false),
               false,
               effect.getDuration(),
               false,
               effect
            );
         } else {
            Map<MobTemporaryStatFlag, MobTemporaryStatEffect> list = new HashMap<>();
            list.put(MobTemporaryStatFlag.INDIE_PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.INDIE_PDR, -20, 164111008, null, false));
            list.put(
               MobTemporaryStatFlag.TRANSFORMATION,
               new MobTemporaryStatEffect(MobTemporaryStatFlag.TRANSFORMATION, 2400500 + Randomizer.rand(0, 2), 164111008, null, false)
            );
            monster.applyMonsterBuff(list, 164111008, effect.getDuration(), null, Collections.EMPTY_LIST);
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.targets > 0) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.CloneAttack) != null && attack.skillID != 164101004 && attack.skillID != 164120007) {
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(164101004);
            SecondaryStatEffect eff2 = this.getPlayer().getSkillLevelData(164101003);
            if (eff != null && eff2 != null) {
               double t = eff2.getT();
               if (this.getPlayer().checkInterval(this.getPlayer().getLastCloneAttackTime(), (int)t * 1000)) {
                  if (this.getPlayer().getBuffedValue(SecondaryStatFlag.HyperCloneRampage) != null) {
                     eff = this.getPlayer().getSkillLevelData(400041049);
                  }

                  int bulletCount = eff.getBulletCount();
                  List<Integer> mobList = new ArrayList<>();

                  for (MapleMonster mob : this.getPlayer()
                     .getMap()
                     .getMobsInRect(this.getPlayer().getTruePosition(), eff.getLt().x, eff.getLt().y, eff.getRb().x, eff.getRb().y)) {
                     if (!mob.getStats().isFriendly()) {
                        mobList.add(mob.getObjectId());
                        if (mobList.size() >= bulletCount) {
                           break;
                        }
                     }
                  }

                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initCloneAttack();
                  ForceAtom forceAtom = new ForceAtom(
                     info, 164101004, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.CLONE_ATTACK, mobList, bulletCount
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                  this.getPlayer().setLastCloneAttackTime(System.currentTimeMillis());
               }
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ButterflyDream) != null && attack.skillID != 164101004 && attack.skillID != 164120007) {
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(164120007);
            if (eff != null && this.getPlayer().checkInterval(this.getPlayer().getLastButterflyDream(), eff.getX() * 1000)) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initButterflyDream(1, this.getPlayer().getTruePosition());
               ForceAtom forceAtom = new ForceAtom(
                  info,
                  164120007,
                  this.getPlayer().getId(),
                  false,
                  false,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.BUTTERFLY_DREAM,
                  Collections.singletonList(0),
                  5
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               this.getPlayer().setLastButterflyDream(System.currentTimeMillis());
            }
         }

         Summoned summoned = this.getPlayer().getSummonBySkillID(400041052);
         if (summoned != null) {
            this.commingOfTheSuperpoweredGodsCount++;
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400041052);
            if (eff != null
               && this.commingOfTheSuperpoweredGodsCount >= eff.getX()
               && this.getPlayer().checkInterval(this.lastCommingOfTheSuperpoweredGodsTime, (int)(eff.getT() * 1000.0))) {
               List<MapleMonster> mobs = this.getPlayer().getMap().getAllMonster();
               CollectionUtil.sortMonsterByBossHP(mobs);
               if (mobs.size() > 0) {
                  this.getPlayer().controlMonster(mobs.get(mobs.size() - 1), false);
               }

               this.getPlayer().send(CField.summonAssistAttackRequest(this.getPlayer().getId(), summoned.getObjectId(), Randomizer.rand(8, 10)));
               this.commingOfTheSuperpoweredGodsCount = 0;
               this.lastCommingOfTheSuperpoweredGodsTime = System.currentTimeMillis();
            }
         }
      }

      this.incCharmPower(attack.skillID, attack.targets > 0, attack);
      if (attack.skillID == 164141013 || attack.skillID == 164141019 || attack.skillID == 164141028) {
         if (attack.skillID == 164141013) {
            int time = SkillFactory.getSkill(attack.skillID).getEffect(this.getPlayer().getTotalSkillLevel(attack.skillID)).getW();
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.ConsumingFlamesVI, 164141013, time * 1000, 0);
         }

         SecondaryStatEffect eff = SkillFactory.getSkill(164141029).getEffect(this.getPlayer().getTotalSkillLevel(164141029));
         int cooltime = eff.getCoolTime();
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.AllThings, 164141029, Integer.MAX_VALUE, 0);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.indiePMDR, 164141029, eff.getTime() * 1000, eff.getIndiePMdR());
         this.getPlayer().send(CField.skillCooldown(164141029, cooltime));
         this.getPlayer().addCooldown(164141029, System.currentTimeMillis(), cooltime);
      }

      if (this.getPlayer().hasBuffBySkillID(164141013)) {
         List<Integer> list = SkillFactory.getSkill(164141013).getSkillList();
         if (list.contains(attack.skillID)) {
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            int attackCount = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.ConsumingFlamesVI, 0) + 1;
            if (attackCount >= 3) {
               attackCount = 0;
               this.getPlayer().send(CField.userBonusAttackRequest(164141018, true, Collections.emptyList()));
            }

            statManager.changeStatValue(SecondaryStatFlag.ConsumingFlamesVI, 164141013, attackCount);
            statManager.temporaryStatSet();
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 164121042) {
         Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
         statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
         statups.put(SecondaryStatFlag.KeyDownStart, 1);
         this.getPlayer().temporaryStatSet(this.getActiveSkillPrepareID(), this.getPlayer().getTotalSkillLevel(164121042), 20000, statups);
         this.dreamGardenChargingStartTime = System.currentTimeMillis();
         SecondaryStatEffect eff = SkillFactory.getSkill(164121042).getEffect(this.getPlayer().getTotalSkillLevel(164121042));
         if (eff != null) {
            int mp = (int)(eff.getMpRCon() * 0.01 * this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()));
            this.getPlayer().addMP(-mp);
         }
      }

      if (this.getActiveSkillPrepareID() == 400041053) {
         this.getPlayer().temporaryStatResetBySkillID(400041052);
         this.getPlayer().temporaryStatSet(400041053, 5000, SecondaryStatFlag.indiePartialNotDamaged, 1);
         this.getPlayer().temporaryStatSet(400041054, 30000, SecondaryStatFlag.AdventOfGods, 1);
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (GameConstants.isHoyoung(GameConstants.getSkillRootFromSkill(this.getActiveSkillID())) || this.getActiveSkillID() == 400041050) {
         this.incCharmPower(this.getActiveSkillID(), false, null);
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 164121015:
            Summoned summoned = this.getPlayer().getSummonBySkillID(164121008);
            if (summoned != null) {
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summoned, true));
               this.getPlayer().getMap().removeMapObject(summoned);
               this.getPlayer().removeVisibleMapObject(summoned);
               this.getPlayer().removeSummon(summoned);
               this.getPlayer().temporaryStatResetBySkillID(164121008);
               Party party = this.getPlayer().getParty();
               if (party == null) {
                  int addHP = (int)(this.getPlayer().getStat().getCurrentMaxHp() * 0.01 * effect.getX()) * summoned.getAbsorbingVortexStack();
                  int addMP = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * effect.getX()) * summoned.getAbsorbingVortexStack();
                  this.getPlayer().healHP(addHP, false);
                  this.getPlayer().healMP(addMP);
               } else {
                  party.getPartyMemberList()
                     .stream()
                     .filter(PartyMemberEntry::isOnline)
                     .filter(p -> p.getChannel() == this.getPlayer().getClient().getChannel())
                     .filter(p -> p.getFieldID() == this.getPlayer().getMapId())
                     .forEach(p -> {
                        MapleCharacter player = this.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterById(p.getId());
                        if (player != null) {
                           int addHPx = (int)(player.getStat().getCurrentMaxHp() * 0.01 * effect.getX()) * summoned.getAbsorbingVortexStack();
                           int addMPx = (int)(player.getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * effect.getX()) * summoned.getAbsorbingVortexStack();
                           player.healHP(addHPx, false);
                           player.healMP(addMPx);
                        }
                     });
               }
            }

            effect.applyTo(this.getPlayer(), true);
            break;
         case 164141025:
            int x = packet.readShort();
            int y = packet.readShort();
            byte flip = packet.readByte();
            int dur = effect.getDuration();
            Summoned summon = new Summoned(this.getPlayer(), 164141025, this.getActiveSkillLevel(), new Point(x, y), SummonMoveAbility.STATIONARY, flip, dur);
            this.getPlayer().getMap().spawnSummon(summon, dur);
            this.getPlayer().addSummon(summon);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 164121042) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.KeyDownStart);
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(164121042);
         if (eff != null) {
            int x = eff.getX();
            long gap = 20000L - (System.currentTimeMillis() - this.dreamGardenChargingStartTime);
            this.getPlayer().changeCooldown(164121042, -(gap / 1000L * x));
         }
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.MastersElixir);
      if (eff != null) {
         int x = eff.getX();
         int y = eff.getY();
         this.charmPowerGauge += x;
         this.scrollPowerGauge += y;
         this.getPlayer().temporaryStatSet(164000010, Integer.MAX_VALUE, SecondaryStatFlag.CharmPower, this.charmPowerGauge);
      }
   }

   public void removeSummon(Summoned s) {
      if (s.getSkill() == 164121006) {
         this.getPlayer().getSummons().stream().filter(summoned -> summoned.getSkill() == 164121011).forEach(summoned -> {
            this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summoned, true));
            this.getPlayer().getMap().removeMapObject(summoned);
            this.getPlayer().removeVisibleMapObject(summoned);
            this.getPlayer().getSummons().remove(summoned);
         });
      }
   }

   public void incCharmPower(int realSkillID, boolean hit, AttackInfo attack) {
      if (hit) {
         HoyoungAttributes attribute = GameConstants.getHoyoungAttribute(realSkillID);
         if (attribute != HoyoungAttributes.None) {
            int charm = GameConstants.getCharmByAttributeLevel(0);
            this.hoyoungLinkageAttribute(realSkillID, attribute);
            this.hoyoungAllThingsAttribute(realSkillID, attribute);
            List<Integer> list = SkillFactory.getSkill(400041068).getSkillList();
            if (list != null && list.contains(attack.skillID) && this.getPlayer().getTotalSkillLevel(400041063) > 0) {
               boolean check = false;
               SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400041063);
               if (e != null) {
                  if (this.getPlayer().getBuffedValue(SecondaryStatFlag.HeavenEarthHumanApparition) != null && this.remainHeavenEarthHumanApparition < e.getY()
                     )
                   {
                     this.remainHeavenEarthHumanApparition++;
                     check = true;
                  }

                  if (this.getPlayer().getCooldownLimit(400041067) <= 0L || check) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     info.initHeavenEarthHumanAppartion();
                     ForceAtom forceAtom = new ForceAtom(
                        info,
                        400041063,
                        this.getPlayer().getId(),
                        false,
                        true,
                        this.getPlayer().getId(),
                        ForceAtom.AtomType.HEAVEN_EARTH_HUMAN_APPARTION,
                        Collections.EMPTY_LIST,
                        1
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                     HoyoungAttributes attribute2 = HoyoungAttributes.Earth;
                     int extraSkillID = 400041064;
                     if ((this.hoyoungAttributes & HoyoungAttributes.Human.getType()) == 0) {
                        extraSkillID = 400041065;
                        attribute2 = HoyoungAttributes.Human;
                     } else if ((this.hoyoungAttributes & HoyoungAttributes.Heaven.getType()) == 0) {
                        extraSkillID = 400041066;
                        attribute2 = HoyoungAttributes.Heaven;
                     }

                     this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, extraSkillID);
                     this.hoyoungLinkageAttribute(extraSkillID, attribute2);
                     if (this.getPlayer().getTotalSkillLevel(164141029) > 0) {
                        Integer value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AllThings, 0);
                        this.getPlayer().temporaryStatSet(SecondaryStatFlag.AllThings, 164141029, Integer.MAX_VALUE, Math.min(value + 1, 3));
                     }

                     if (!check) {
                        int cool = e.getQ() * 1000;
                        if (this.getPlayer().getBuffedValue(SecondaryStatFlag.HeavenEarthHumanApparition) != null) {
                           cool = e.getV2() * 1000;
                        }

                        this.getPlayer().send(CField.skillCooldown(400041067, cool));
                        this.getPlayer().addCooldown(400041067, System.currentTimeMillis(), cool);
                     }
                  }
               }
            }
         }
      }

      SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(realSkillID);
      if (effect != null) {
         int atGauge1Con = effect.getAtGauge1Con();
         int atGauge2Inc = effect.getAtGauge2Inc();
         int atGauge2Con = effect.getAtGauge2Con();
         if (this.getPlayer().hasBuffBySkillID(400001049)) {
            SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.EmpressBless, 400001049);
            if (eff != null) {
               atGauge2Inc += (int)(atGauge2Inc * 0.01 * eff.getX());
            }
         }

         if (atGauge1Con != 0) {
            this.charmPowerGauge = Math.max(0, this.charmPowerGauge - atGauge1Con);
         }

         if (atGauge2Inc != 0) {
            this.scrollPowerGauge = Math.min(900, this.scrollPowerGauge + atGauge2Inc);
         }

         if (atGauge2Con != 0) {
            this.scrollPowerGauge = Math.max(0, this.scrollPowerGauge - atGauge2Con);
         }

         if (realSkillID == 164121041) {
            this.charmPowerGauge = 100;
            this.scrollPowerGauge = 900;
         }
      }

      this.getPlayer().temporaryStatSet(164000010, Integer.MAX_VALUE, SecondaryStatFlag.CharmPower, this.charmPowerGauge);
      if (realSkillID == 164101002 || realSkillID == 164111002 || realSkillID == 164111006 || realSkillID == 164121002) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(164101006);
         if (eff != null) {
            this.getPlayer().temporaryStatSet(164101006, eff.getDuration(), SecondaryStatFlag.DarkSight, eff.getW2());
         }
      }
   }

   public void hoyoungAllThingsAttribute(int realSkillID, HoyoungAttributes attribute) {
      if ((this.hoyoungAllThingsAttributes & attribute.getType()) == 0) {
         this.hoyoungAllThingsAttributes = this.hoyoungAllThingsAttributes | attribute.getType();
         int level = this.getHoyoungAttributeLevel(true);
         if (level == 3) {
            this.hoyoungAllThingsAttributes = HoyoungAttributes.None.getType();
            if (this.getPlayer().getTotalSkillLevel(164141029) > 0) {
               Integer value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AllThings, 0);
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.AllThings, 164141029, Integer.MAX_VALUE, Math.min(value + 1, 3));
            }
         }
      }
   }

   public void hoyoungLinkageAttribute(int realSkillID, HoyoungAttributes attribute) {
      int charm = 10;
      if ((this.hoyoungAttributes & attribute.getType()) == 0) {
         this.hoyoungAttributes = this.hoyoungAttributes | attribute.getType();
         int level = this.getHoyoungAttributeLevel(false);
         charm = GameConstants.getCharmByAttributeLevel(level);
         if (level == 3) {
            this.hoyoungAttributes = HoyoungAttributes.None.getType();
            SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(164110013);
            if (eff != null) {
               int x = eff.getX();
               int addHP = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
               int y = eff.getY();
               int addMP = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * y);
               this.getPlayer().healHP(addHP, false);
               this.getPlayer().healMP(addMP);
            }

            Summoned summoned = this.getPlayer().getSummonBySkillID(400041050);
            if (summoned != null) {
               this.getPlayer().send(CField.summonBeholderShock(this.getPlayer().getId(), summoned.getObjectId(), 400041051));
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.HeavenEarthHumanApparition) != null) {
               List<Integer> skills = new ArrayList<>();

               for (int i : this.attributeSkills) {
                  if (this.getPlayer().getCooldownLimit(i) > 0L) {
                     skills.add(i);
                  }
               }

               if (skills.size() > 0) {
                  Collections.shuffle(skills);
                  Integer v = skills.stream().findAny().orElse(null);
                  if (v != null) {
                     this.getPlayer().clearCooldown(v);
                  }
               }
            }
         }
      }

      this.getPlayer()
         .temporaryStatSet(
            164000010, Integer.MAX_VALUE, SecondaryStatFlag.HoyoungAttributes, (this.hoyoungAttributes & HoyoungAttributes.Heaven.getType()) != 0 ? 1 : 0
         );
      int incScroll = 15;
      if (this.getPlayer().hasBuffBySkillID(400001049)) {
         SecondaryStatEffect effx = this.getPlayer().getBuffedEffect(SecondaryStatFlag.EmpressBless, 400001049);
         if (effx != null) {
            incScroll += (int)(incScroll * 0.01 * effx.getX());
            charm += (int)(charm * 0.01 * effx.getX());
         }
      }

      this.charmPowerGauge = Math.min(100, this.charmPowerGauge + charm);
      this.scrollPowerGauge = Math.min(900, this.scrollPowerGauge + incScroll);
   }

   public int getHoyoungAttributeLevel(boolean isAllThings) {
      int ret = 0;
      int check = isAllThings ? this.hoyoungAllThingsAttributes : this.hoyoungAttributes;
      if ((HoyoungAttributes.Heaven.getType() & check) != 0) {
         ret++;
      }

      if ((HoyoungAttributes.Earth.getType() & check) != 0) {
         ret++;
      }

      if ((HoyoungAttributes.Human.getType() & check) != 0) {
         ret++;
      }

      return ret;
   }

   public int getScrollPowerGauge() {
      return this.scrollPowerGauge;
   }

   public int getHoyoungAttributes() {
      return this.hoyoungAttributes;
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case HoyoungAttributes:
            packet.writeInt((this.getHoyoungAttributes() & HoyoungAttributes.Earth.getType()) != 0 ? 1 : 0);
            packet.writeInt((this.getHoyoungAttributes() & HoyoungAttributes.Human.getType()) != 0 ? 1 : 0);
            break;
         case CharmPower:
            packet.writeInt(this.getScrollPowerGauge());
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }
}
