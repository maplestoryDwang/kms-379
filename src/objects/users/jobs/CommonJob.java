package objects.users.jobs;

import constants.GameConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.loader.CharacterSaveFlag;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.effect.child.DiceRoll;
import objects.effect.child.HPHeal;
import objects.effect.child.SkillEffect;
import objects.effect.child.SpecialSkillEffect;
import objects.effect.child.WZEffectBased;
import objects.fields.Field;
import objects.fields.FieldLimitType;
import objects.fields.ForceAtom;
import objects.fields.Grenade;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.child.blackmage.Field_BlackMage;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase4;
import objects.fields.child.demian.Field_Demian;
import objects.fields.child.jinhillah.Field_JinHillah;
import objects.fields.child.minigame.soccer.Field_MultiSoccer;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.instance.ErdaSpectrum;
import objects.fields.gameobject.lifes.Element;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.MapleDiseaseValueHolder;
import objects.users.achievement.AchievementFactory;
import objects.users.jobs.adventure.archer.BowMaster;
import objects.users.jobs.adventure.archer.DefaultArcher;
import objects.users.jobs.adventure.archer.Marksman;
import objects.users.jobs.adventure.archer.PathFinder;
import objects.users.jobs.adventure.magician.ArcMageFP;
import objects.users.jobs.adventure.magician.ArcMageIL;
import objects.users.jobs.adventure.magician.Bishop;
import objects.users.jobs.adventure.magician.DefaultMagician;
import objects.users.jobs.adventure.pirate.Buccaneer;
import objects.users.jobs.adventure.pirate.Cannoneer;
import objects.users.jobs.adventure.pirate.Captain;
import objects.users.jobs.adventure.pirate.DefaultPirate;
import objects.users.jobs.adventure.thief.DefaultThief;
import objects.users.jobs.adventure.thief.DualBlade;
import objects.users.jobs.adventure.thief.NightLord;
import objects.users.jobs.adventure.thief.Shadower;
import objects.users.jobs.adventure.warrior.DarkKnight;
import objects.users.jobs.adventure.warrior.DefaultWarrior;
import objects.users.jobs.adventure.warrior.Hero;
import objects.users.jobs.adventure.warrior.Paladin;
import objects.users.jobs.anima.Hoyoung;
import objects.users.jobs.anima.Lara;
import objects.users.jobs.event.Pinkbean;
import objects.users.jobs.event.Yeti;
import objects.users.jobs.flora.Adele;
import objects.users.jobs.flora.Ark;
import objects.users.jobs.flora.Illium;
import objects.users.jobs.flora.Khali;
import objects.users.jobs.hero.Aran;
import objects.users.jobs.hero.Evan;
import objects.users.jobs.hero.Luminous;
import objects.users.jobs.hero.Mercedes;
import objects.users.jobs.hero.Phantom;
import objects.users.jobs.hero.Shade;
import objects.users.jobs.kinesis.Kinesis;
import objects.users.jobs.koc.FlameWizard;
import objects.users.jobs.koc.Mikhail;
import objects.users.jobs.koc.NightWalker;
import objects.users.jobs.koc.SoulMaster;
import objects.users.jobs.koc.Striker;
import objects.users.jobs.koc.WindArcher;
import objects.users.jobs.nova.AngelicBuster;
import objects.users.jobs.nova.Cadena;
import objects.users.jobs.nova.Kain;
import objects.users.jobs.nova.Kaiser;
import objects.users.jobs.resistance.BattleMage;
import objects.users.jobs.resistance.Blaster;
import objects.users.jobs.resistance.DemonAvenger;
import objects.users.jobs.resistance.DemonSlayer;
import objects.users.jobs.resistance.Mechanic;
import objects.users.jobs.resistance.WildHunter;
import objects.users.jobs.resistance.Xenon;
import objects.users.jobs.zero.Zero;
import objects.users.skills.DamageParse;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackAction;
import objects.users.skills.TeleportAttackData_ListInt;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.ArrayMap;
import objects.utils.AttackPair;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import scripting.EventInstanceManager;

public class CommonJob implements BasicJob {
   byte speed = 0;
   short moveAction = 0;
   int activeSkillID = 0;
   int activeSkillLevel = 0;
   int activeSkillFlag = 0;
   protected boolean exclusive = false;
   protected TeleportAttackAction teleportAttackAction = null;
   int activeSkillPrepareID = 0;
   int activeSkillPrepareSLV = 0;
   List<MapleMonster> mobKillCount = new ArrayList<>();
   Point keyDownRectMoveXY = new Point(0, 0);
   MapleCharacter player = null;
   boolean nextDiceChange = false;
   int[] diceStatData = new int[22];
   boolean bAntiMagicShellBarrier = false;
   int tAntiMagicShellBarrier = 0;
   private int empericalKnowledgeX = 0;
   private int rhoAiasFrom = 0;
   private int rhoAiasC = 0;
   private int rhoAiasX = 0;
   private int rhoAiasLevel = 0;
   private int crossOverChainX = 0;
   private int nobilityFromID = 0;
   private int nobilityShield = 0;
   private int michaelSoulLinkX = 0;
   private int michaelSoulLinkY = 0;
   private int michaelSoulLinkFrom = 0;
   private int michaelSoulLink = 0;
   private long lastSoulLinkHealTime = 0L;
   private int holySymbolUserID = 0;
   private int holySymbolLv = 0;
   private boolean holySymbolActive = false;
   private boolean holySymbolDecrease = false;
   private int holySymbolDropR = 0;
   private int erdaFountainStack = 0;
   private long blessStackTime = 0L;

   @Override
   public void setPlayer(MapleCharacter player) {
      this.player = player;
   }

   @Override
   public MapleCharacter getPlayer() {
      return this.player;
   }

   @Override
   public int getActiveSkillID() {
      return this.activeSkillID;
   }

   @Override
   public int getActiveSkillLevel() {
      return this.activeSkillLevel;
   }

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (effect != null) {
         this.setCooltime(attack, effect);
         if (this.setBullet(attack, effect, opcode)) {
            try {
               String key = "ContinousRingTime";
               if (this.player.getTempKeyValue(key) != null) {
                  Long time = (Long)this.player.getTempKeyValue(key);
                  if (time != null && time <= System.currentTimeMillis()) {
                     MapleInventory inv = this.player.getInventory(MapleInventoryType.EQUIPPED);
                     int[] ringpos = new int[]{-12, -13, -15, -16};
                     int level = 0;

                     for (int pos : ringpos) {
                        Item item = inv.getItem((short)pos);
                        if (item != null && GameConstants.isContinousRing(item.getItemId())) {
                           Equip contiRing = (Equip)item;
                           level = contiRing.getTheSeedRingLevel();
                           break;
                        }
                     }

                     if (level > 0) {
                        SecondaryStatEffect eff = SkillFactory.getSkill(80003342).getEffect(level);
                        int bufftime = eff.getTime() * 1000;
                        int cooldown = eff.getCoolTime();
                        int bdr = eff.getIndieBdR();
                        this.player.setTempKeyValue(key, System.currentTimeMillis() + cooldown);
                        this.player.temporaryStatSet(SecondaryStatFlag.ContinousRingReady, 80003341, cooldown, 1);
                        this.player.temporaryStatSet(SecondaryStatFlag.indieBDR, 80003342, bufftime, bdr);
                     }
                  }
               }
            } catch (Exception var15) {
               System.out.println("CommonJob Err");
               var15.printStackTrace();
            }

            if (attack.skillID == 80001829 && this.getPlayer().getMapId() != ServerConstants.TownMap && this.getPlayer().getMap().getFieldSetInstance() == null
               )
             {
               int lastmap = this.getPlayer().getOneInfoQuestInteger(19771, "lastmap");
               int lastx = this.getPlayer().getOneInfoQuestInteger(19771, "lastx");
               int lastcount = this.getPlayer().getOneInfoQuestInteger(19771, "lastcount");
               if (lastmap == this.getPlayer().getMapId() && lastx == this.getPlayer().getPosition().x) {
                  int count = lastcount + 1;
                  this.getPlayer().updateOneInfo(19771, "lastcount", String.valueOf(count));
                  if (count >= 5) {
                     this.getPlayer().warp(ServerConstants.TownMap);
                     this.getPlayer().dropMessage(5, "제자리에서 비연 스킬을 5회이상 사용하여 마을로 이동됩니다.");
                     this.getPlayer().updateOneInfo(19771, "lastcount", String.valueOf(0));
                  }
               } else {
                  this.getPlayer().updateOneInfo(19771, "lastmap", String.valueOf(this.getPlayer().getMapId()));
                  this.getPlayer().updateOneInfo(19771, "lastx", String.valueOf(this.getPlayer().getPosition().x));
                  this.getPlayer().updateOneInfo(19771, "lastcount", String.valueOf(1));
               }
            }

            int skillID = GameConstants.getLinkedSkillID(attack.skillID);
            SecondaryStatEffect real = SkillFactory.getSkill(skillID).getEffect(attack.skillLevel);
            if (attack.skillID == 4341002) {
               real.applyTo(this.getPlayer());
               this.getPlayer().temporaryStatSet(4341002, effect.getV() * 1000, SecondaryStatFlag.NotDamaged, 1);
            }

            if (this.player.isEquippedSoulWeapon() && attack.skillID == this.player.getEquippedSoulSkill()) {
               this.player.checkSoulState(true);
            }

            if (this.player.getBuffedValue(SecondaryStatFlag.SoulExplosion) != null) {
               this.player.temporaryStatReset(SecondaryStatFlag.SoulExplosion);
            }

            if (this.player.getBuffedValue(SecondaryStatFlag.UsefulAdvancedBless) != null) {
               this.player.temporaryStatReset(SecondaryStatFlag.UsefulAdvancedBless);
            }

            if (attack.skillID == 80002684) {
               ErdaSpectrum fieldSet = (ErdaSpectrum)this.player.getMap().getFieldSetInstance();
               if (fieldSet != null) {
                  int Erda = Integer.parseInt(fieldSet.getVar("Erda"));
                  if (Erda >= 30) {
                     fieldSet.incErdaGuage(-30);
                     Erda = Integer.parseInt(fieldSet.getVar("Erda"));
                     this.player.getMap().broadcastMessage(ErdaSpectrum.ErdaSpectrumCrackInfo(Erda, this.player.getMap().getAllMonstersThreadsafe().size()));
                  }
               }
            }

            long money = effect.getMoneyCon();
            if (money != 0L) {
               if (money > this.player.getMeso()) {
                  money = this.player.getMeso();
               }

               this.player.gainMeso(-money, false);
            }

            if (GameConstants.isMulungSkill(attack.skillID)) {
               if (this.player.getMapId() / 10000 != 92502) {
                  return;
               }

               if (this.player.getMulungEnergy() < 10000) {
                  return;
               }

               this.player.mulung_EnergyModify(false);
            } else if (GameConstants.isPyramidSkill(attack.skillID)) {
               if (this.player.getMapId() / 1000000 != 926) {
                  return;
               }

               if (this.player.getPyramidSubway() == null || !this.player.getPyramidSubway().onSkillUse(this.player)) {
                  return;
               }
            }

            if (this.player.getMapId() == 109090300) {
               DamageParse.doHideAndSeek(this.player, attack, true);
            }
         }
      }
   }

   public boolean setBullet(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (opcode == RecvPacketOpcode.SHOOT_ATTACK && attack.skillID > 0) {
         boolean AOE = attack.skillID == 4111004 || attack.skillID == 3111013 || attack.skillID == 95001000;
         boolean noBullet = GameConstants.isLuminous(this.player.getJob())
            || GameConstants.isEvan(this.player.getJob())
            || this.player.getJob() == 301
            || this.player.getJob() >= 330 && this.player.getJob() <= 332
            || attack.skillID == 5221022
            || attack.skillID >= 5220023 && attack.skillID <= 5220025
            || this.player.getJob() >= 510 && this.player.getJob() <= 512
            || this.player.getJob() >= 3500 && this.player.getJob() <= 3512
            || GameConstants.isCannon(this.player.getJob())
            || GameConstants.isPhantom(this.player.getJob())
            || GameConstants.isMercedes(this.player.getJob())
            || GameConstants.isZero(this.player.getJob())
            || GameConstants.isXenon(this.player.getJob())
            || GameConstants.isKaiser(this.player.getJob())
            || GameConstants.isAngelicBuster(this.player.getJob())
            || GameConstants.isAran(this.player.getJob());
         int bulletCount = 1;
         if (attack.skillID != 0) {
            switch (attack.skillID) {
               case 1077:
               case 1078:
               case 1079:
               case 11077:
               case 11078:
               case 11079:
               case 3111004:
               case 3211004:
               case 4121003:
               case 4121016:
               case 4121017:
               case 4121052:
               case 4221003:
               case 5121002:
               case 11101004:
               case 13101005:
               case 13101020:
               case 13111007:
               case 14101006:
               case 15111007:
               case 21000004:
               case 21100004:
               case 21100007:
               case 21110004:
               case 21110011:
               case 21120006:
               case 33101002:
               case 33101007:
               case 33121001:
               case 33121002:
               case 33121052:
               case 35121054:
               case 51001004:
               case 51111007:
               case 51121008:
               case 64001000:
               case 64001007:
               case 64001008:
               case 64001009:
               case 64001010:
               case 64001011:
               case 64001012:
               case 400010000:
                  AOE = true;
                  bulletCount = effect.getAttackCount();
                  break;
               case 5211008:
               case 5221017:
               case 5221022:
               case 5221052:
               case 13001020:
               case 13111020:
               case 13121002:
               case 13121052:
                  bulletCount = effect.getAttackCount();
                  break;
               case 35111004:
               case 35121005:
               case 35121013:
                  AOE = true;
                  bulletCount = 6;
                  break;
               default:
                  bulletCount = effect.getBulletCount();
            }
         }

         int[] skillList = new int[]{3100011, 3200014, 4110016, 5200016, 13100028, 14110031, 33100017};

         for (int skill : skillList) {
            if (this.player.getTotalSkillLevel(skill) > 0) {
               return true;
            }
         }

         if (!noBullet && effect.getBulletCount() < effect.getAttackCount()) {
            bulletCount = effect.getAttackCount();
         }

         if (attack.skillID == 0) {
            bulletCount = 1;
         }

         Integer shadowPartner = this.player.getBuffedValue(SecondaryStatFlag.ShadowPartner);
         if (shadowPartner != null) {
            bulletCount *= 2;
         }

         int projectile = 0;
         Item ipp = this.player.getInventory(MapleInventoryType.USE).getItem(attack.slot);
         if (ipp != null && ipp.getItemId() > 0) {
            projectile = ipp.getItemId();
         }

         if (!AOE
            && this.player.getBuffedValue(SecondaryStatFlag.SoulArrow) == null
            && !noBullet
            && this.player.getBuffedValue(SecondaryStatFlag.NoBulletConsume) == null) {
            int bulletConsume = bulletCount;
            if (this.player.getJob() == 412 && bulletCount > 0 && ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile)) {
               Skill expert = SkillFactory.getSkill(4120010);
               if (this.player.getTotalSkillLevel(expert) > 0) {
                  SecondaryStatEffect eff = expert.getEffect(this.player.getTotalSkillLevel(expert));
                  if (eff.makeChanceResult()) {
                     ipp.setQuantity((short)(ipp.getQuantity() + 1));
                     this.player.send(CWvsContext.InventoryPacket.updateInventorySlot(MapleInventoryType.USE, ipp, false));
                     bulletConsume = 0;
                     this.player.send(CWvsContext.enableActions(this.player, false));
                  }
               }
            }

            if (bulletConsume > 0) {
               this.player.setBulletItemID(projectile);
               if (!MapleInventoryManipulator.removeById(this.player.getClient(), MapleInventoryType.USE, projectile, bulletConsume, false, true)) {
                  this.player.dropMessage(5, "소모성 아이템이 부족합니다.");
                  return false;
               }
            }
         }
      }

      return true;
   }

   public void setCooltime(AttackInfo attack, SecondaryStatEffect effect) {
      Skill skill = SkillFactory.getSkill(attack.skillID);
      if (skill != null && skill.getSkillType() != 3) {
         if (attack.skillID != 151001001 && attack.skillID != 151141002) {
            if (attack.skillID != 155111212 && attack.skillID != 155111211 && attack.skillID != 155111202) {
               int skillID = GameConstants.getLinkedSkillID(attack.skillID);
               if (skillID != 27121201) {
                  skill = SkillFactory.getSkill(skillID);
                  SecondaryStatEffect real = skill.getEffect(attack.skillLevel);
                  int target = real.getMobCount();
                  if (attack.skillID == 400011100) {
                     skillID = 400011100;
                  } else if (attack.skillID == 400011135) {
                     skillID = 400011135;
                  }

                  if (!attack.isUnstableMemorize
                     && !this.player.checkSpiritFlow(attack.skillID)
                     && !this.player.checkSpiritFlow(skillID)
                     && !GameConstants.isKeydownEndCooltimeSkill(attack.skillID)
                     && GameConstants.canConsumeAttackSkill(attack.skillID)
                     && !GameConstants.isSuperNovaSkill(attack.skillID)) {
                     int cooltime = real.getCooldown(this.player);
                     if (cooltime > 0 && (GameConstants.isAfterApplyCooltimeSkill(skillID, attack.skillID) || attack.dragonShowSkillEffect)) {
                        boolean dracoSlasher = false;
                        if (attack.skillID == 400011079 || attack.skillID == 400011080) {
                           Integer value = this.player.getBuffedValue(SecondaryStatFlag.DracoSlasher);
                           if (value != null) {
                              int v = value - 1;
                              dracoSlasher = true;
                              if (v > 0) {
                                 this.player.temporaryStatSet(400011058, (int)this.player.getRemainCooltime(400011058), SecondaryStatFlag.DracoSlasher, v);
                              } else {
                                 this.player.temporaryStatReset(SecondaryStatFlag.DracoSlasher);
                              }
                           }
                        }

                        if (!dracoSlasher
                           && this.player.canApplyCooldown(skillID)
                           && (this.player.getBuffedValue(SecondaryStatFlag.Ellision) == null || attack.skillID != 11121052 && attack.skillID != 11121055)
                           && (this.player.getBuffedValue(SecondaryStatFlag.StrikerHyperElectric) == null || skillID != 15111022 && skillID != 15120003)) {
                           int cooldown = effect.getCooldown(this.getPlayer());
                           if (attack.skillID == 400001036) {
                              cooldown -= attack.targets;
                           }

                           if (attack.skillID == 2121003) {
                              boolean f = false;

                              for (AttackPair pair : attack.allDamage) {
                                 MapleMonster mob = this.player.getMap().getMonsterByOid(pair.objectid);
                                 if (mob != null && mob.getBurnedSize() >= 5) {
                                    f = true;
                                 }
                              }

                              if (f) {
                                 if (cooldown <= 5000) {
                                    cooldown -= 1000;
                                 } else {
                                    cooldown -= 2000;
                                 }
                              }
                           }

                           if (attack.skillID == 155111006 && this.player.getSkillLevel(155120038) > 0) {
                              cooldown -= (int)(cooldown * (this.player.getSkillLevelData(155120038).getCoolTimeR() / 100.0));
                           }

                           if (GameConstants.getCooldown6thAttackSkill(attack.skillID) > 0 && cooldown == 0) {
                              cooldown = cooltime;
                           }

                           if (cooldown > 0) {
                              this.player.send(CField.skillCooldown(skillID, cooldown));
                              this.player.addCooldown(skillID, System.currentTimeMillis(), cooldown);
                              if (skillID >= 80003086 && skillID <= 80003089) {
                                 this.player.send(CField.temporarySkillCooldown(skillID, cooldown));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
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
      int skillID = attack.skillID;
      if (attack.skillID == 2221011) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> statups = new ArrayMap<>();
         if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
            statups.put(MobTemporaryStatFlag.FREEZE, new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false));
            monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
            statups.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, effect.getX(), attack.skillID, null, false));
            statups.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR, effect.getY(), attack.skillID, null, false));
            monster.applyMonsterBuff(statups, attack.skillID, effect.getDuration(), null, Collections.EMPTY_LIST);
         } else {
            this.getPlayer()
               .send(
                  MobPacket.monsterResist(
                     monster,
                     this.getPlayer(),
                     (int)((monster.getResistSkill(MobTemporaryStatFlag.FREEZE) - System.currentTimeMillis()) / 1000L),
                     attack.skillID,
                     2
                  )
               );
         }
      }

      if (monster.getId() == 8641018) {
         FieldSetInstance fieldSet = monster.getMap().getFieldSetInstance();
         if (fieldSet != null) {
            long now = System.currentTimeMillis();
            long lastSpawnTime = Long.parseLong(fieldSet.getVar("lastSpawnTime"));
            if (lastSpawnTime == 0L || now - lastSpawnTime >= 1000L) {
               fieldSet.setVar("lastSpawnTime", String.valueOf(System.currentTimeMillis()));
               String nowPuck = fieldSet.getVar("lastPuck");
               Point sumPos = new Point(416 + Randomizer.rand(-120, 120), 47);
               switch (nowPuck) {
                  case "red":
                     monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8641019), sumPos);
                     break;
                  case "blue":
                     monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8641020), sumPos);
                     break;
                  case "purple":
                     monster.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8641021), sumPos);
               }
            }
         }
      }

      if (skill != null && skill.getId() == 80002685) {
         ErdaSpectrum fieldSet = (ErdaSpectrum)monster.getMap().getFieldSetInstance();
         if (fieldSet != null && (monster.getId() == 8641030 || monster.getId() == 8641031) && !monster.isBuffed(MobTemporaryStatFlag.INVINCIBLE)) {
            switch (monster.getId()) {
               case 8641030:
                  if (monster.getPosition().x > 600) {
                     this.getPlayer().getMap().killMonster(monster);
                     fieldSet.incElim();
                  }
                  break;
               case 8641031:
                  if (monster.getPosition().x < -800) {
                     this.getPlayer().getMap().killMonster(monster);
                     fieldSet.incElim();
                  }
            }
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.IncarnationAura) != null
         && this.getPlayer().getSecondaryStat().getFromID(SecondaryStatFlag.IncarnationAura) != this.getPlayer().getId()
         && this.getPlayer().getCooldownLimit(80003017) <= 0L) {
         SecondaryStatEffect e_ = this.getPlayer().getBuffedEffect(SecondaryStatFlag.IncarnationAura);
         if (e_ != null) {
            SecondaryStatEffect e = SkillFactory.getSkill(80003017).getEffect(e_.getLevel());
            this.getPlayer()
               .sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 80003017, 1, Collections.singletonList(monster.getObjectId()));
            this.getPlayer().addCooldown(80003017, System.currentTimeMillis(), e.getX() * 1000L);
         }
      }

      if (monster.getId() == 8880305 && this.getPlayer().getMap() instanceof Field_WillBattle) {
         Field_WillBattle f = (Field_WillBattle)this.getPlayer().getMap();
         boolean soloPlay = f.getCharactersThreadsafe().size() == 1;
         Set<ObstacleAtom> atoms = new HashSet<>();
         if (Randomizer.isSuccess(30)) {
            for (int i = 0; i < Randomizer.rand(1, 3); i++) {
               int type = 63 + Randomizer.nextInt(2);
               int x = Randomizer.nextInt(1200) - 600;
               int y = monster.getTruePosition().y > 0 ? -2020 : 159;
               if (soloPlay) {
                  y = monster.getTruePosition().y > 0 ? 159 : -2020;
               }

               ObstacleAtom atom = new ObstacleAtom(type, new Point(x, y - 599), new Point(x, y), 1013);
               atom.setHitBoxRange(40);
               atom.setKey(Randomizer.nextInt());
               atom.setTrueDamR(type == 64 ? 0 : 60);
               atom.setMobDamR(0);
               atom.setHeight(6400);
               atom.setvPerSec(138);
               atom.setMaxP(3);
               atom.setAngle(0);
               atom.setLength(599);
               atoms.add(atom);
               this.getPlayer().getMap().addObstacleAtom(atom);
            }
         }

         if (!atoms.isEmpty()) {
            this.getPlayer().getMap().broadcastMessage(CField.createObstacle(ObstacleAtomCreateType.NORMAL, null, null, atoms));
         }
      }

      Skill skill_ = SkillFactory.getSkill(attack.skillID);
      if (skill_ != null && monster.getBuff(MobTemporaryStatFlag.BAHAMUT_LIGHT_ELEM_ADD_DAM) != null && skill_.getElement() == Element.Holy) {
         monster.cancelStatus(MobTemporaryStatFlag.BAHAMUT_LIGHT_ELEM_ADD_DAM);
      }

      this.getPlayer().checkMonsterAggro(monster);
      switch (attack.skillID) {
         case 1221052:
         case 27121052:
         case 31121006:
         case 151121040:
         case 400001008:
            if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
               monster.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false), false, 10000L, false, effect
               );
               monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
            }
            break;
         case 80003365:
            if (monster.checkResistOriginSkill(MobTemporaryStatFlag.FREEZE)) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, this.getPlayer().getId(), 80003365, null, false),
                  false,
                  10000L,
                  false,
                  effect
               );
               monster.addResistOriginSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer());
            }
            break;
         case 100001283:
            monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false), false, 3000L, false, effect
            );
            break;
         case 164121043:
            int proportion = (int)((float)totalDamage / this.getPlayer().getStat().getCurrentMaxBaseDamage());
            if (proportion > 100) {
               proportion = 100;
            }

            int duration = effect.getDuration() + effect.getDuration() * proportion / 100;
            monster.applyStatus(
               this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attack.skillID, null, false), false, duration, false, effect
            );
            monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
      }

      if (totalDamage > 0L) {
         try {
            monster.damage(this.getPlayer(), totalDamage, false, skillID);
         } catch (Exception var20) {
            System.out.println("CommonJob Err22");
            var20.printStackTrace();
            FileoutputUtil.log("Log_Damage_Except.rtf", "Error executing on Damage. (playerName : " + this.player.getName() + ") " + var20);
         }

         if (!monster.isAlive()) {
            this.mobKillCount.add(monster);
         }

         if (monster.isBuffed(MobTemporaryStatFlag.P_COUNTER)) {
            boolean counterDmg = this.getPlayer().getBuffedValue(SecondaryStatFlag.Reincarnation) == null;
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.IgnorePCounter) != null) {
               counterDmg = false;
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.NotDamaged) != null) {
               counterDmg = false;
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) != null) {
               counterDmg = false;
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Morph) != null) {
               int sourceId = this.getPlayer().getBuffedEffect(SecondaryStatFlag.Morph).getSourceId();
               if (sourceId == 61111008 || sourceId == 61120008 || sourceId == 61121053) {
                  counterDmg = false;
               }
            }

            if (skill != null && skill.isIgnoreCounter()) {
               counterDmg = false;
            }

            if (attack.skillID == 2221012) {
               counterDmg = false;
            }

            List<MapleMapObject> grenadeList = this.player
               .getMap()
               .getMapObjectsInRange(attack.attackPosition, Double.POSITIVE_INFINITY, List.of(MapleMapObjectType.GRENADE));
            if (!grenadeList.isEmpty()) {
               for (MapleMapObject g : grenadeList) {
                  if (g instanceof Grenade && ((Grenade)g).getSkillId() == attack.skillID && ((Grenade)g).getOwnerId() == this.player.getId()) {
                     counterDmg = false;
                     break;
                  }
               }
            }

            if (counterDmg) {
               int d = 7000 + Randomizer.nextInt(8000);
               HPHeal e = new HPHeal(this.player.getId(), -d);
               this.player.send(e.encodeForLocal());
               this.player.getMap().broadcastMessage(this.player, e.encodeForRemote(), false);
               this.getPlayer().addHP(-d);
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Slow) != null) {
            SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.Slow);
            if (eff != null && eff.makeChanceResult() && !monster.isBuffed(MobTemporaryStatFlag.SPEED)) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, eff.getX(), eff.getSourceId(), null, false),
                  false,
                  eff.getY() * 1000,
                  true,
                  eff
               );
            }
         }

         this.playerOnAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), totalDamage);
         if (effect != null
            && effect.getMonsterStati().size() > 0
            && skill != null
            && effect.makeChanceResult()
            && attack.skillID != 4120019
            && attack.skillID != 4100012) {
            for (Entry<MobTemporaryStatFlag, Integer> z : effect.getMonsterStati().entrySet()) {
               monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(z.getKey(), z.getValue(), skill.getId(), null, false),
                  effect.isPoison(),
                  effect.getDuration(),
                  true,
                  effect
               );
            }
         }

         Item weapon_ = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
         if (weapon_ != null) {
            MobTemporaryStatFlag stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
            if (stat != null && Randomizer.nextInt(100) < GameConstants.getStatChance()) {
               MobTemporaryStatEffect monsterStatusEffect = new MobTemporaryStatEffect(
                  stat, GameConstants.getXForStat(stat), GameConstants.getSkillForStat(stat), null, false
               );
               monster.applyStatus(this.getPlayer(), monsterStatusEffect, false, 10000L, false, null);
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.Blind) != null) {
            SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.Blind);
            if (eff != null && eff.makeChanceResult()) {
               MobTemporaryStatEffect monsterStatusEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.ACC, eff.getX(), eff.getSourceId(), null, false);
               monster.applyStatus(this.getPlayer(), monsterStatusEffect, false, eff.getY() * 1000, true, eff);
            }
         }
      }
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().getAntiMacro() != null) {
         this.getPlayer().getAntiMacro().setLastAttackTime(System.currentTimeMillis());
      }

      if (attack.targets > 0 && this.getPlayer().getBuffedValue(SecondaryStatFlag.RuneOfIgnition) != null && this.getPlayer().getCooldownLimit(80002890) <= 0L) {
         this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 80002890);
         this.getPlayer().send(CField.skillCooldown(80002890, 2000));
         this.getPlayer().addCooldown(80002890, System.currentTimeMillis(), 2000L);
      }

      if (attack.targets > 0) {
         int[] arcaneAimList = new int[]{2120010, 2220010, 2320011};

         for (int skillID : arcaneAimList) {
            SecondaryStatEffect arcaneAim = this.player.getSkillLevelData(skillID);
            if (arcaneAim != null && arcaneAim.makeChanceResult()) {
               if (this.player.arcaneAim < 5) {
                  this.player.arcaneAim++;
               }

               arcaneAim.applyTo(this.player);
            }
         }
      }

      if (attack.targets > 0) {
         int[] skillList = new int[]{150030241, 80003224};

         for (int s : skillList) {
            if (this.getPlayer().getTotalSkillLevel(s) > 0) {
               if (this.getPlayer().getCooldownLimit(s) <= 0L) {
                  SecondaryStatEffect level = this.getPlayer().getSkillLevelData(s);
                  if (level != null) {
                     Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                     statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                     statups.put(SecondaryStatFlag.indieDotHeal, 1);
                     statups.put(SecondaryStatFlag.indieRocketBooster, 1);
                     this.getPlayer().temporaryStatSet(s, level.getLevel(), level.getDuration(), statups, false, 0, true, false);
                     this.getPlayer().send(CField.skillCooldown(s, level.getCooldown(this.getPlayer())));
                     this.getPlayer().addCooldown(s, System.currentTimeMillis(), level.getCooldown(this.getPlayer()));
                  }
               }
               break;
            }
         }
      }

      if (attack.targets > 0 && attack.allDamage != null && this.getPlayer().getEventInstance() != null) {
         EventInstanceManager eim = this.getPlayer().getEventInstance();
         if (!eim.getDamageInfo().containsKey(this.getPlayer().getName())) {
            eim.getDamageInfo().put(this.getPlayer().getName(), 0L);
         }

         attack.allDamage.forEach(damageList -> damageList.attack.forEach(damage -> {
            MapleMonster monster = MapleLifeFactory.getMonster(damageList.refImgId);
            if (monster != null && monster.getStats().isBoss()) {
               long d = damage.getLeft();
               eim.getDamageInfo().put(this.getPlayer().getName(), d + eim.getDamageInfo().get(this.getPlayer().getName()));
            }
         }));
         List<String> nameList = new ArrayList<>(eim.getDamageInfo().keySet());
         nameList.sort((value1, value2) -> eim.getDamageInfo().get(value2).compareTo(eim.getDamageInfo().get(value1)));
         this.getPlayer().getMap().broadcastMessage(CField.showAggressiveRank(nameList));
      }

      if (attack.teleportAttackAction != null && attack.teleportAttackAction.actions != null) {
         attack.teleportAttackAction.actions.forEach(action -> {
            if (action.data instanceof TeleportAttackData_ListInt) {
               List<Integer> targetList = ((TeleportAttackData_ListInt)action.data).getIntList();
               if (this.getPlayer().getMap() instanceof Field_MultiSoccer && this.getPlayer().getBuffedValue(SecondaryStatFlag.MultiSoccerAddBall) == null) {
                  ((Field_MultiSoccer)this.getPlayer().getMap()).stunPlayer(targetList);
               }
            }
         });
      }

      if (this.getPlayer().getTotalSkillLevel(160010001) > 0 || this.getPlayer().getTotalSkillLevel(80003058) > 0) {
         int skillId = 160010001;
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(160010001);
         int lv = this.getPlayer().getTotalSkillLevel(160010001);
         if (lv <= 0) {
            skillId = 80003058;
            lv = this.getPlayer().getTotalSkillLevel(80003058);
            eff = this.getPlayer().getSkillLevelData(80003058);
         }

         if (multiKill > 0 && this.getPlayer().getCooldownLimit(skillId) <= 0L) {
            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.LaLa_LinkSkill, 0);
            if (value < 19) {
               this.getPlayer().temporaryStatSet(80003070, Integer.MAX_VALUE, SecondaryStatFlag.LaLa_LinkSkill, value + 1);
            } else {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.LaLa_LinkSkill);
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.indieNBDR, 160010001, 30000, lv, eff.getW(), true);
               this.getPlayer().giveCoolDowns(skillId, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
               this.getPlayer().send(CField.skillCooldown(skillId, eff.getCooldown(this.getPlayer())));
            }
         }
      }

      int lvx = 0;
      if (((lvx = this.getPlayer().getTotalSkillLevel(60030241)) > 0 || (lvx = this.getPlayer().getTotalSkillLevel(80003015)) > 0)
         && this.getPlayer().getCooldownLimit(60030241) <= 0L
         && this.getPlayer().getCooldownLimit(80003015) <= 0L) {
         SecondaryStatEffect e = SkillFactory.getSkill(60030241).getEffect(lvx);
         if (e != null) {
            if (boss) {
               this.getPlayer().priorPreparationBossCount++;
            }

            this.getPlayer().priorPreparationMobCount += multiKill;
            if (this.getPlayer().priorPreparationBossCount >= e.getY() || this.getPlayer().priorPreparationMobCount >= e.getX()) {
               int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.PriorPreparation, 0);
               if (value < e.getW()) {
                  this.getPlayer().temporaryStatSet(80003018, Integer.MAX_VALUE, SecondaryStatFlag.PriorPreparation, value + 1);
               } else {
                  this.getPlayer().temporaryStatReset(SecondaryStatFlag.PriorPreparation);
                  int damR = e.getY();
                  this.getPlayer().temporaryStatSet(80003018, e.getDuration(), SecondaryStatFlag.indieDamR, damR);
                  if (this.getPlayer().getTotalSkillLevel(80003015) > 0) {
                     this.getPlayer().giveCoolDowns(80003015, System.currentTimeMillis(), e.getCooldown(this.getPlayer()));
                     this.getPlayer().send(CField.skillCooldown(80003015, e.getCooldown(this.getPlayer())));
                  } else {
                     this.getPlayer().giveCoolDowns(60030241, System.currentTimeMillis(), e.getCooldown(this.getPlayer()));
                     this.getPlayer().send(CField.skillCooldown(60030241, e.getCooldown(this.getPlayer())));
                  }
               }

               this.getPlayer().priorPreparationBossCount = 0;
               this.getPlayer().priorPreparationMobCount = 0;
            }
         }
      }

      if (attack.skillID != 400001038) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400001037);
         if (e != null
            && this.getPlayer().getBuffedValue(SecondaryStatFlag.MagicCircuitFullDrive) != null
            && this.getPlayer().checkInterval(this.getPlayer().getLastMagicCircuitTime(), e.getX() * 1000)) {
            this.getPlayer().send(CField.userBonusAttackRequest(400001038, true, Collections.EMPTY_LIST));
            this.getPlayer().setLastMagicCircuitTime(System.currentTimeMillis());
            int damR = 0;
            this.getPlayer().addMP(-e.getW(), true);
            int mpPer = (int)(this.getPlayer().getStat().getMp() / (this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01));
            damR = (int)(e.getY() * 0.01 * mpPer);
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.indieDamR, e.getSourceId(), damR);
            statManager.temporaryStatSet();
         }
      }

      if (attack.skillID == 400001014) {
         this.getPlayer().temporaryStatSet(400001014, effect.getX() * 1000, SecondaryStatFlag.HeavensDoor, 1);
      }

      if (attack.skillID == 400001036) {
         int x = attack.targets * effect.getX();
         this.getPlayer().changeCooldown(attack.skillID, -x * 1000L);
      }

      this.trySummonedAttackManual();
      this.tryApplyEmpiricalKnowledge(attack);
      Map<Integer, Integer> killedMap = new HashMap<>();

      for (MapleMonster mob : this.mobKillCount) {
         killedMap.put(mob.getId(), killedMap.getOrDefault(mob.getId(), 0) + 1);
         boolean delta = Math.abs(this.player.getLevel() - mob.getStats().getLevel()) < 20 || this.player.getLevel() >= 275;
         if (delta) {
            killedMap.put(9101025, killedMap.getOrDefault(9101025, 0) + 1);
            if (this.player.getGuild() != null && !mob.getStats().isBoss() && this.player.getOneInfoQuestInteger(100813, "huntPoint") < 25000) {
               this.player.addGuildBossPointByBossID(-1);
            }

            if (mob.getEliteMobType().getType() > 0) {
               killedMap.put(9101223, killedMap.getOrDefault(9101223, 0) + 1);
               if (this.player.isQuestStarted(501534)) {
                  if (this.player.getOneInfoQuestInteger(501534, "value") < 1) {
                     this.player.updateOneInfo(501534, "value", "1");
                  }

                  if (this.player.getOneInfoQuestInteger(501524, "state") < 2) {
                     this.player.updateOneInfo(501524, "state", "2");
                  }
               }
            }
         }
      }

      try {
         killedMap.forEach((k, v) -> {
            this.player.mobKilled(k, v, attack.skillID);
            if (this.player.isQuestStarted(QuestExConstants.NeoEventNormalMob.getQuestID())) {
               int m0 = this.player.getOneInfoQuestInteger(QuestExConstants.NeoEventNormalMob.getQuestID(), "m0");
               if (m0 < 10000) {
                  m0 = Math.min(10000, m0 + v);
                  this.player.updateOneInfo(QuestExConstants.NeoEventNormalMob.getQuestID(), "m0", String.valueOf(m0));
                  MapleQuestStatus status = this.player.getQuest(MapleQuest.getInstance(QuestExConstants.NeoEventNormalMob.getQuestID()));
                  if (status != null) {
                     String keySet = "000";
                     if (m0 < 100) {
                        keySet = "0" + m0;
                     } else {
                        keySet = String.valueOf(m0);
                     }

                     keySet = StringUtil.getLeftPaddedStr(String.valueOf(m0), '0', 3);
                     status.setCustomData(keySet);
                     this.player.updateQuest(status);
                  }

                  if (m0 >= 10000) {
                     this.player.updateOneInfo(QuestExConstants.NeoEventAdventureLog.getQuestID(), "state", "2");
                  }
               }
            } else if (this.player.isQuestStarted(QuestExConstants.NeoEventEliteMob.getQuestID())) {
               MapleQuestStatus statusx = this.player.getQuest(MapleQuest.getInstance(QuestExConstants.NeoEventEliteMob.getQuestID()));
               if (statusx != null && k == 9101223) {
                  String keySet = "000";
                  int count = 0;
                  if (!statusx.getCustomData().isEmpty()) {
                     keySet = statusx.getCustomData();
                     count = Integer.parseInt(keySet);
                  }

                  if (count < 20) {
                     count = Math.min(20, count + v);
                     keySet = StringUtil.getLeftPaddedStr(String.valueOf(count), '0', 3);
                     statusx.setCustomData(keySet);
                     this.player.updateQuest(statusx);
                     this.player.updateOneInfo(QuestExConstants.NeoEventEliteMob.getQuestID(), "m1", String.valueOf(count));
                     if (count >= 20) {
                        this.player.updateOneInfo(QuestExConstants.NeoEventAdventureLog.getQuestID(), "state", "2");
                     }
                  }
               }
            }

            if (k != 9101025 && k != 9101223) {
               AchievementFactory.checkMobKillOptimum(MapleLifeFactory.getMonster(k), this.player, v);
            }
         });
      } finally {
         this.mobKillCount.clear();
      }

      if (this.getPlayer().getKeyValue2("mark_of_assassins") == 1 && attack.targets > 0) {
         Skill skill_ = SkillFactory.getSkill(attack.skillID);
         if (skill_.getType() != 51 && Randomizer.isSuccess(50)) {
            List<MapleMapObject> objs = this.getPlayer()
               .getMap()
               .getMapObjectsInRange(this.getPlayer().getTruePosition(), 500000.0, Arrays.asList(MapleMapObjectType.MONSTER));
            List<Integer> monsters = new ArrayList<>();
            int bulletCount = Randomizer.rand(1, 3);
            int consumeItemID = 2070006;

            for (int i = 0; i < bulletCount; i++) {
               int rand = Randomizer.rand(0, objs.size() - 1);
               if (objs.size() < bulletCount) {
                  if (i < objs.size()) {
                     monsters.add(((MapleMonster)objs.get(i)).getObjectId());
                  }
               } else {
                  monsters.add(((MapleMonster)objs.get(rand)).getObjectId());
                  objs.remove(rand);
               }
            }

            if (monsters.size() > 0) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initMarkOfThief(this.getPlayer().getPosition(), consumeItemID);
               ForceAtom forceAtom = new ForceAtom(
                  info, 13100027, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.MARK_OF_THIEF, monsters, bulletCount
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
         }
      }

      if (multiKill >= 3) {
         float expR;
         switch (multiKill) {
            case 3:
               expR = 0.03F;
               break;
            case 4:
               expR = 0.08F;
               break;
            case 5:
               expR = 0.15F;
               break;
            case 6:
               expR = 0.198F;
               break;
            case 7:
               expR = 0.252F;
               break;
            case 8:
               expR = 0.312F;
               break;
            case 9:
               expR = 0.378F;
               break;
            default:
               expR = 0.45F;
         }

         long comboexp = (long)((float)totalExp * expR);
         boolean check = true;
         if (check) {
            this.getPlayer().setMultiKillCount(this.getPlayer().getMultiKillCount() + 1);
            this.getPlayer()
               .getClient()
               .getSession()
               .writeAndFlush(CWvsContext.InfoPacket.multiKill(this.getPlayer().getStylishKillSkin(), multiKill, comboexp));
            this.getPlayer().gainExp(comboexp, false, false, false);
         }
      }

      if (attack.targets > 0) {
         Item item = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
         if (item != null && attack.skillID > 0 && !GameConstants.isAntiRepeatSkill(attack.skillID) && skill.getType() != 51) {
            int weaponIdx = item.getItemId() / 10000 % 100;
            List<Integer> targets = new ArrayList<>();
            attack.allDamage.forEach(t -> targets.add(t.objectid));
            SecondaryStatEffect meteor = SkillFactory.getSkill(2121007).getEffect(this.getPlayer().getTotalSkillLevel(2121007));
            if (meteor != null) {
               this.getPlayer().doActiveFinalAttack(2120013, meteor.getProb(), attack.skillID, weaponIdx, targets);
            }

            SecondaryStatEffect blizzard = SkillFactory.getSkill(2221007).getEffect(this.getPlayer().getTotalSkillLevel(2221007));
            if (blizzard != null) {
               this.getPlayer().doActiveFinalAttack(2220014, blizzard.getProb(), attack.skillID, weaponIdx, targets);
            }

            if (this.getPlayer().getCooldownLimit(32121004) > 0L) {
               SecondaryStatEffect darkLightning = SkillFactory.getSkill(32121004).getEffect(this.getPlayer().getTotalSkillLevel(32121004));
               if (darkLightning != null) {
                  this.getPlayer().doActiveFinalAttack(32121011, darkLightning.getProb(), attack.skillID, weaponIdx, targets);
               }
            }

            if (this.getPlayer().getJob() >= 531 && this.getPlayer().getJob() <= 532) {
               Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.Roulette);
               if (value != null && value == 1) {
                  SecondaryStatEffect effect_ = SkillFactory.getSkill(5311004).getEffect(this.getPlayer().getTotalSkillLevel(5311004));
                  if (effect_ != null) {
                     this.getPlayer().doActiveFinalAttack(5311004, effect_.getProb(), attack.skillID, weaponIdx, targets);
                  }
               }
            }
         }

         if (item != null && attack.targets > 0) {
            List<Integer> targetsx = new ArrayList<>();
            attack.allDamage.forEach(t -> targetsx.add(t.objectid));
            this.getPlayer().doFinalAttack(attack.skillID, targetsx);
         }
      }

      if (attack.skillID > 0
         && attack.skillID != 13121054
         && attack.skillID != 13121055
         && attack.skillID != 1311020
         && (
            !GameConstants.isNoDelaySkill(attack.skillID)
               || attack.skillID == 400031003
               || attack.skillID == 400031004
               || attack.skillID == 400001018
               || this.getPlayer().getKeyValue2("trifling_whim") == 1 && attack.skillID != 13120003
         )
         && skill.getType() != 51) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.TriflingWhimOnOff) != null) {
            SecondaryStatEffect effx = this.getPlayer().getBuffedEffect(SecondaryStatFlag.TriflingWhimOnOff);
            if (effx != null) {
               int sid = effx.getSourceId();
               int forceAtomSkillID = 0;
               int propBonus = this.getPlayer().getSkillLevelDataOne(13120044, SecondaryStatEffect::getProb);
               int maxMultiplier = Math.max(1, this.getPlayer().getSkillLevelDataOne(13120045, SecondaryStatEffect::getX));
               if (sid == 13101022) {
                  if (Randomizer.isSuccess(effx.getProb() + propBonus)) {
                     forceAtomSkillID = !Randomizer.isSuccess(effx.getSubProp()) ? sid : 13100027;
                  }
               } else if (sid == 13110022) {
                  if (Randomizer.isSuccess(effx.getProb() + propBonus)) {
                     forceAtomSkillID = !Randomizer.isSuccess(effx.getSubProp()) ? sid : 13110027;
                  }
               } else if (sid == 13120003 && Randomizer.isSuccess(effx.getProb() + propBonus)) {
                  forceAtomSkillID = !Randomizer.isSuccess(effx.getSubProp()) ? sid : 13120010;
               }

               if (forceAtomSkillID > 0) {
                  int bulletCount = 1;
                  int rand = Randomizer.rand(1, 100);
                  byte var86;
                  if (rand < 75) {
                     var86 = 1;
                  } else if (rand < 85) {
                     var86 = 2;
                  } else if (rand < 90) {
                     var86 = 3;
                  } else if (rand < 95) {
                     var86 = 4;
                  } else {
                     var86 = 5;
                  }

                  List<Integer> mobs = new ArrayList<>();
                  Field var93 = this.getPlayer().getMap();
                  Point var10001 = this.getPlayer().getPosition();
                  boolean var10006 = (attack.display & 32768) != 0;
                  List<MapleMonster> mobList = var93.getMobsInRect(var10001, effx.getLt().x, effx.getLt().y, effx.getRb().x, effx.getRb().y, var10006, 100);
                  Collections.sort(mobList, (o1, o2) -> Long.compare(o2.getStats().getMaxHp(), o1.getStats().getMaxHp()));

                  for (MapleMonster mobx : mobList) {
                     if (mobx != null && mobx.isAlive() && mobs.size() < var86 && !mobs.contains(mobx.getObjectId()) && !mobx.getStats().isFriendly()) {
                        mobs.add(mobx.getObjectId());
                     }
                  }

                  if (mobs.size() > 0) {
                     ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                     ForceAtom.AtomType atom = ForceAtom.AtomType.TRI_FLING;
                     info.initTriFling(sid != forceAtomSkillID ? 2 : 1, 0);
                     ForceAtom forceAtom = new ForceAtom(
                        info, forceAtomSkillID, this.getPlayer().getId(), false, true, this.getPlayer().getId(), atom, mobs, var86 * maxMultiplier
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
                  }
               }
            }
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.StormBringer) != null) {
            SecondaryStatEffect effx = this.getPlayer().getBuffedEffect(SecondaryStatFlag.StormBringer);
            SecondaryStatEffect eff2 = SkillFactory.getSkill(13101022).getEffect(1);
            if (effx != null && effx.makeChanceResult()) {
               List<Integer> monsters = new ArrayList<>();

               for (MapleMonster mobxx : this.getPlayer()
                  .getMap()
                  .getMobsInRect(
                     this.getPlayer().getPosition(), eff2.getLt().x, eff2.getLt().y, eff2.getRb().x, eff2.getRb().y, this.getPlayer().isFacingLeft()
                  )) {
                  if (mobxx != null && mobxx.isAlive() && monsters.size() < 1) {
                     monsters.add(mobxx.getObjectId());
                  }
               }

               if (monsters.size() > 0) {
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initStormBringer();
                  ForceAtom forceAtom = new ForceAtom(
                     info, 13121017, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.STORM_BRINGER, monsters, 1
                  );
                  this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }
            }
         }
      }

      if (attack.skillID > 0) {
         Skill sx = SkillFactory.getSkill(attack.skillID);
         if (sx != null) {
            SecondaryStatEffect consume = sx.getEffect(effect.getLevel());
            if (consume != null
               && (GameConstants.canConsumeAttackSkill(attack.skillID) || GameConstants.isKeydownSkill(attack.skillID))
               && !GameConstants.isExceptionKeydownSkill(attack.skillID)
               && !GameConstants.isAffectedAreaSkill(attack.skillID)
               && (
                  !this.getPlayer().hasBuffBySkillID(attack.skillID) && !this.getPlayer().hasBuffBySkillID(attack.skillID) && !attack.dragonAttack
                     || attack.dragonAttack && attack.dragonShowSkillEffect
               )) {
               if (attack.skillID == 400011110 || attack.skillID == 400011111 || attack.skillID == 31101002 || attack.skillID == 36101001) {
                  return;
               }

               consume.applyTo(this.getPlayer(), attack.forcedPos, (byte)0, false, true);
            }
         }
      }
   }

   @Override
   public void activeSkillPrepare(PacketDecoder slea) {
      if (this.activeSkillPrepareID == 2221011) {
         this.player.temporaryStatSet(2221011, Integer.MAX_VALUE, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      if (this.activeSkillPrepareID != 400011056
         && this.activeSkillPrepareID != 11121055
         && this.activeSkillPrepareID != 11121056
         && (this.activeSkillPrepareID < 3321035 || this.activeSkillPrepareID > 3321038)
         && this.activeSkillPrepareID != 3321040
         && this.activeSkillPrepareID != 155111306
         && this.activeSkillPrepareID != 155121341
         && this.activeSkillPrepareID != 3111013
         && !this.getPlayer().checkSpiritFlow(this.activeSkillPrepareID)
         && !GameConstants.isKeydownEndCooltimeSkill(this.activeSkillPrepareID)
         && (!this.getPlayer().getMemorize() || !this.getPlayer().checkUnstableMemorize(2221011) || this.activeSkillPrepareID != 2221011)) {
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(this.activeSkillPrepareID);
         if (effect != null) {
            int cooltimeID = this.activeSkillPrepareID;
            if (this.activeSkillPrepareID == 400041056) {
               cooltimeID = 400041055;
            }

            if (this.activeSkillPrepareID == 400021043) {
               effect = this.getPlayer().getSkillLevelData(400021042);
               cooltimeID = 400021042;
            }

            if (this.activeSkillPrepareID == 31101002) {
               this.getPlayer().giveCoolDowns(cooltimeID, System.currentTimeMillis(), 0L);
               this.getPlayer().send(CField.skillCooldown(cooltimeID, 0));
            } else {
               int cooldown = effect.getCooldown(this.getPlayer());
               this.getPlayer().giveCoolDowns(cooltimeID, System.currentTimeMillis(), cooldown);
               this.getPlayer().send(CField.skillCooldown(cooltimeID, cooldown));
            }
         }
      }

      if (this.activeSkillPrepareID != 164121042 && this.activeSkillPrepareID != 4341002) {
         SecondaryStatEffect effect = this.player.getSkillLevelData(this.activeSkillPrepareID);
         if (effect != null) {
            effect.applyTo(this.player);
         }
      }

      Skill skill = SkillFactory.getSkill(this.activeSkillPrepareID);
      if (this.activeSkillPrepareSLV > 0
         && (this.activeSkillPrepareID == 400051040 || this.activeSkillPrepareID == 33101005 || this.activeSkillPrepareID == 22171083 || skill.isChargeSkill())
         )
       {
         this.player.setKeyDownSkill_Time(System.currentTimeMillis());
         if (this.activeSkillPrepareID == 33101005) {
            this.player.setLinkMid(slea.readInt(), 0);
         }

         if (this.activeSkillPrepareID == 155111306) {
            this.player.temporaryStatSet(155111306, Integer.MAX_VALUE, SecondaryStatFlag.indiePartialNotDamaged, 1);
         }

         if ((this.activeSkillPrepareID == 155111306 || this.activeSkillPrepareID == 155121341)
            && (Integer)this.player.getJobField("specterStateCount") > 0
            && this.player.getBuffedValue(SecondaryStatFlag.SpectralForm) == null) {
            SecondaryStatEffect eff = SkillFactory.getSkill(155101006).getEffect(this.player.getTotalSkillLevel(155101006));
            if (eff != null) {
               eff.applyTo(this.player);
            }
         }

         if (this.activeSkillPrepareID == 400051040) {
            this.player.temporaryStatSet(400051040, 3000, SecondaryStatFlag.indiePartialNotDamaged, 1);
            long cool = this.player.getCooldownLimit(5221013);
            SecondaryStatEffect eff = SkillFactory.getSkill(400051040).getEffect(this.player.getTotalSkillLevel(400051040));
            if (eff != null && cool < eff.getW() * 1000L) {
               this.player.giveCoolDowns(5221013, System.currentTimeMillis(), eff.getW() * 1000);
               this.player.send(CField.skillCooldown(5221013, eff.getW() * 1000));
            }
         }

         if (this.activeSkillPrepareID == 400011072) {
            this.player.temporaryStatSet(400011072, Integer.MAX_VALUE, SecondaryStatFlag.GrandCrossSize, 1);
         }

         if (this.activeSkillPrepareID == 400011068) {
            this.player.temporaryStatSet(400011068, Integer.MAX_VALUE, SecondaryStatFlag.KeyDownMoving, 100);
         }

         if (this.activeSkillPrepareID == 37121052) {
            SecondaryStatEffect eff = SkillFactory.getSkill(this.activeSkillPrepareID).getEffect(this.activeSkillPrepareSLV);
            if (eff != null) {
               this.player.temporaryStatSet(37121052, eff.getSubTime(), SecondaryStatFlag.RWMagnumBlow, 1);
            }
         }

         if (this.activeSkillPrepareID == 3321036 || this.activeSkillPrepareID == 3321038) {
            SecondaryStatEffect eff = SkillFactory.getSkill(this.activeSkillPrepareID).getEffect(this.activeSkillPrepareSLV);
            if (eff != null) {
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               statups.put(SecondaryStatFlag.indieDamReduceR, Integer.valueOf(eff.getIndieDamReduceR()));
               this.player.temporaryStatSet(3321035, this.activeSkillPrepareSLV, 17000, statups);
               this.player.setDamReduceR(eff.getIndieDamReduceR());
            }
         }

         if (this.activeSkillPrepareID >= 3321035 && this.activeSkillPrepareID <= 3321038 || this.activeSkillPrepareID == 3321040) {
            SecondaryStatEffect eff = SkillFactory.getSkill(this.activeSkillPrepareID).getEffect(this.activeSkillPrepareSLV);
            if (eff != null && this.player.getRelicCharge() >= eff.getForceCon()) {
               this.player.startAncientAstraTask(this.activeSkillPrepareID, eff.getForceCon());
            }

            SecondaryStatEffect eff2 = SkillFactory.getSkill(3321035).getEffect(this.activeSkillPrepareSLV);
            if (eff2 != null) {
               this.player.send(CField.skillCooldown(3321035, eff2.getCooldown(this.player)));
               this.player.addCooldown(3321035, System.currentTimeMillis(), eff2.getCooldown(this.player));
            }
         }

         if (this.activeSkillPrepareID == 400031024) {
            this.player.setStateIrkallasWrath(true);
         }

         PacketEncoder add = new PacketEncoder();
         if (GameConstants.isKeydownSkillRectMoveXY(this.activeSkillPrepareID)) {
            add.writeShort(this.keyDownRectMoveXY.x);
            add.writeShort(this.keyDownRectMoveXY.y);
         }

         if (this.activeSkillPrepareID == 154121002 && this.activeSkillPrepareSLV > 30) {
            this.activeSkillPrepareSLV = 30;
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SKILL_PREPARE.getValue());
         packet.writeInt(this.getPlayer().getId());
         packet.writeInt(this.activeSkillPrepareID);
         packet.write(this.activeSkillPrepareSLV);
         packet.writeShort(this.moveAction);
         packet.write(this.speed);
         byte[] addPacket = new byte[8];

         try {
            addPacket = add.getPacket();
         } catch (Exception var7) {
         }

         packet.encodeBuffer(addPacket);
         this.player.getMap().broadcastMessage(this.player, packet.getPacket(), false);
      }
   }

   @Override
   public void activeSkillCancel() {
      Skill skill = SkillFactory.getSkill(this.getActiveSkillID());
      if (!this.player.checkSpiritFlow(this.getActiveSkillID()) && GameConstants.isKeydownEndCooltimeSkill(this.getActiveSkillID())) {
         SecondaryStatEffect eff = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.player.getTotalSkillLevel(this.getActiveSkillID()));
         if (eff != null) {
            int cooldown = eff.getCooldown(this.player);
            this.player.giveCoolDowns(this.getActiveSkillID(), System.currentTimeMillis(), cooldown);
            this.player.send(CField.skillCooldown(this.getActiveSkillID(), cooldown));
         }
      }

      if (skill != null) {
         if (this.getActiveSkillID() == 2221011) {
            this.getPlayer().temporaryStatResetBySkillID(2221011);
         }

         if (skill.getEffect(1) != null && (skill.getEffect(1).isMonsterRiding() || skill.getEffect(1).isMonsterRiding_())) {
            this.player.temporaryStatResetBySkillID(80001242);
         }

         if (this.getActiveSkillID() == 51111008) {
            this.michaelSoulLinkFrom = 0;
            this.michaelSoulLink = 0;
            this.michaelSoulLinkY = 0;
            this.michaelSoulLinkX = 0;
         }

         if (this.getActiveSkillID() == 63001004) {
            if (this.player.getGrapplingWireX() > 0) {
               this.player.getMap().broadcastMessage(this.player, CField.skillCancel(this.player, this.getActiveSkillID()), false);
               this.player.setGrapplingWireX(0);
            } else {
               this.player.setGrapplingWireX(1);
            }
         }

         if (skill.isChargeSkill() && this.getActiveSkillID() != 4341052) {
            this.player.setKeyDownSkill_Time(0L);
            this.player.getMap().broadcastMessage(this.player, CField.skillCancel(this.player, this.getActiveSkillID()), false);
         } else if (this.getActiveSkillID() == 13101022) {
            this.player.temporaryStatReset(SecondaryStatFlag.TriflingWhimOnOff);
         } else {
            this.player.temporaryStatResetBySkillID(this.getActiveSkillID());
            if (this.getActiveSkillID() == 35001002 || this.getActiveSkillID() == 35111003) {
               this.player.temporaryStatResetBySkillID(30000227);
            }

            if (this.getActiveSkillID() == 400031006) {
               Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
               statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
               this.player.temporaryStatSet(400031006, this.player.getTotalSkillLevel(400031006), 2000, statups);
            }

            if (this.getActiveSkillID() == 400021032 || this.getActiveSkillID() == 400021033) {
               SecondaryStatEffect e = this.player.getSkillLevelData(2321003);
               if (e != null) {
                  e.applyTo(this.player);
               }
            }

            if (this.getActiveSkillID() == 2111011) {
               SecondaryStatEffect real = SkillFactory.getSkill(2111011).getEffect(this.getPlayer().getSkillLevel(2111011));
               if (real != null) {
                  this.getPlayer().send(CField.skillCooldown(2111011, real.getCooldown(this.getPlayer())));
                  this.getPlayer().addCooldown(2111011, System.currentTimeMillis(), real.getCooldown(this.getPlayer()));
               }
            }

            if (this.getActiveSkillID() == 400001012) {
               int[] skills = new int[]{3111005, 3211005, 3311009};

               for (int skillID : skills) {
                  SecondaryStatEffect e = this.player.getSkillLevelData(skillID);
                  if (e != null) {
                     e.applyTo(this.player);
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      Skill skill = SkillFactory.getSkill(this.activeSkillID);
      if (skill == null) {
         this.player.send(CWvsContext.enableActions(this.player, this.exclusive));
      } else {
         SecondaryStatEffect effect = SkillFactory.getSkill(this.activeSkillID).getEffect(this.activeSkillLevel);
         if (effect == null) {
            this.player.send(CWvsContext.enableActions(this.player, this.exclusive));
         } else {
            this.handleSixJobNoDeathTime(this.getActiveSkillID(), effect);
            if (this.activeSkillID == 80002633 && this.player.getBuffedValue(SecondaryStatFlag.Aion) != null) {
               this.player.temporaryStatReset(SecondaryStatFlag.Aion);
               this.player.send(CWvsContext.enableActions(this.player, this.exclusive));
            } else {
               if (GameConstants.isSoulSummonSkill(this.activeSkillID)) {
                  this.player.checkSoulState(true);
               }

               BasicJob job = this.player.getPlayerJob();
               job.setActiveSkill(this.activeSkillID);
               job.setActiveSkillLevel(this.activeSkillLevel);
               job.setTeleportAttackAction(this.teleportAttackAction);
               job.onActiveSkill(skill, effect, packet);
               job.afterActiveSkill();
            }
         }
      }
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      int linked5thSkill = GameConstants.getLinked5thSkill(this.activeSkillID);
      if (GameConstants.isBlessStackSkill(linked5thSkill)) {
         Integer buffValue = this.getPlayer().getBuffedValue(SecondaryStatFlag.EmpressBlessStack);
         if (buffValue != null && buffValue > 0) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.EmpressBlessStack, linked5thSkill, Integer.MAX_VALUE, buffValue - 1);
         }
      }

      if (this.activeSkillID == 400011000) {
         Integer buffValue = this.getPlayer().getBuffedValue(SecondaryStatFlag.AuraWeaponStack);
         if (buffValue != null && buffValue > 0) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.AuraWeaponStack, linked5thSkill, Integer.MAX_VALUE, buffValue - 1);
         }
      }

      if (this.activeSkillID == 400001037) {
         Integer buffValue = this.getPlayer().getBuffedValue(SecondaryStatFlag.MagicCircuitFullDriveStack);
         if (buffValue != null && buffValue > 0) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.MagicCircuitFullDriveStack, linked5thSkill, Integer.MAX_VALUE, buffValue - 1);
         }
      }

      switch (this.activeSkillID) {
         case 2121052:
         case 31221001:
         case 31241000:
         case 35101002:
         case 35110017:
            try {
               List<Integer> moblist = new ArrayList<>();
               if (this.activeSkillID != 35101002 && this.activeSkillID != 35110017 && this.activeSkillID != 2121052) {
                  packet.skip(4);
               }

               byte count = packet.readByte();

               for (byte ix = 1; ix <= count; ix++) {
                  moblist.add(packet.readInt());
               }

               if (moblist.size() > 0) {
                  ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
                  if (this.activeSkillID == 31221001) {
                     atomInfo.initShieldChasing(this.getPlayer().getForceAtomKey());
                     this.getPlayer().initShieldChasingCount(this.getPlayer().getAndAddForceAtomKey());
                     ForceAtom atom = new ForceAtom(
                        atomInfo, 31221014, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.MEGIDDO_FLAME, moblist, 2
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                  } else if (this.activeSkillID == 31241000) {
                     atomInfo.initShieldChasing(this.getPlayer().getForceAtomKey());
                     this.getPlayer().initShieldChasingCount(this.getPlayer().getAndAddForceAtomKey());
                     ForceAtom atom = new ForceAtom(
                        atomInfo, 31221014, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.DAShieldChasing2_1, moblist, 2
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                  } else if (this.activeSkillID == 36001005) {
                     ForceAtom atom = new ForceAtom(
                        atomInfo, 36001005, this.getPlayer().getId(), false, true, moblist.get(0), ForceAtom.AtomType.PIN_POINT_ROCKET, moblist, moblist.size()
                     );
                     this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                  } else if (this.activeSkillID != 2121052) {
                     if (this.activeSkillID == 35101002 || this.activeSkillID == 35110017) {
                        int bulletCount = moblist.size();
                        Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.BombTime);
                        if (value != null) {
                           int var55 = effect.getMobCount();
                           bulletCount = var55 + value;
                           int slv = 0;
                           if (this.getPlayer().getTotalSkillLevel(35120017) > 0) {
                              slv = this.getPlayer().getTotalSkillLevel(35120017);
                              SecondaryStatEffect eff = SkillFactory.getSkill(35120017).getEffect(slv);
                              if (eff != null) {
                                 bulletCount += eff.getBulletCount();
                              }
                           }
                        } else if (this.getPlayer().hasBuffBySkillID(35111003)) {
                           bulletCount = effect.getMobCount();
                           int slv = 0;
                           if (this.getPlayer().getTotalSkillLevel(35120017) > 0) {
                              slv = this.getPlayer().getTotalSkillLevel(35120017);
                              SecondaryStatEffect eff = SkillFactory.getSkill(35120017).getEffect(slv);
                              if (eff != null) {
                                 bulletCount += eff.getBulletCount();
                              }
                           }
                        }

                        if (this.getPlayer().hasBuffBySkillID(400051094)) {
                           SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400051094);
                           if (eff != null) {
                              bulletCount += eff.getX();
                           }
                        }

                        ForceAtom atom = new ForceAtom(
                           atomInfo,
                           this.activeSkillID,
                           this.getPlayer().getId(),
                           false,
                           true,
                           this.getPlayer().getId(),
                           ForceAtom.AtomType.HOMING_MISILE,
                           moblist,
                           bulletCount
                        );
                        this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
                     }
                  } else {
                     List<SecondAtom.Atom> atoms = new ArrayList<>();
                     this.getPlayer().setBlackJackCount(0);
                     SecondAtomData data = skill.getSecondAtomData();

                     for (int ix = 0; ix < 3; ix++) {
                        SecondAtom.Atom a = new SecondAtom.Atom(
                           this.getPlayer().getMap(),
                           this.getPlayer().getId(),
                           2121052,
                           ForceAtom.SN.getAndAdd(1),
                           SecondAtom.SecondAtomType.MegiddoFlame,
                           0,
                           null
                        );
                        SecondAtomData.atom at = data.getAtoms().get(ix);
                        a.setPlayerID(this.getPlayer().getId());
                        a.setTargetObjectID(moblist.get(Math.min(ix, moblist.size() - 1)));
                        a.setExpire(at.getExpire());
                        a.setCreateDelay(at.getCreateDelay());
                        a.setEnableDelay(at.getEnableDelay());
                        a.setRange(1);
                        a.setUnk4(1);
                        a.setAttackableCount(1);
                        a.setSkillID(2121052);
                        a.setPos(new Point(this.player.getTruePosition().x + at.getPos().x, this.player.getTruePosition().y + at.getPos().y));
                        this.getPlayer().addSecondAtom(a);
                        atoms.add(a);
                        this.getPlayer().setBlackJackCount(this.getPlayer().getBlackJackCount() + 1);
                     }

                     SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 2121052, atoms);
                     this.getPlayer().getMap().createSecondAtom(secondAtom);
                  }
               }

               effect.applyTo(this.getPlayer(), true);
               break;
            } catch (Exception var14) {
               System.out
                  .println(
                     "[오류] useSkillRequest 함수 실행중 오류 발생 (캐릭터 이름 : " + this.getPlayer().getName() + ", 스킬 ID : " + this.activeSkillID + ") " + var14.toString()
                  );
               var14.printStackTrace();
            }
         case 4221052:
         case 400041021:
            this.getPlayer().send(CField.onSkilllUseRequest(this.activeSkillID));
            effect.applyTo(this.getPlayer());
            break;
         case 3011004:
         case 3300002:
         case 3321003:
            new Point(packet.readShort(), packet.readShort());
            byte targetSize = packet.readByte();
            List<Integer> targets = new ArrayList<>();

            for (int i = 0; i < targetSize; i++) {
               targets.add(packet.readInt());
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initCardinalDischarge(this.getActiveSkillID() == 3321003 ? 2 : 1);
            ForceAtom forceAtom = new ForceAtom(
               info,
               this.getActiveSkillID(),
               this.getPlayer().getId(),
               false,
               true,
               this.getPlayer().getId(),
               ForceAtom.AtomType.CARDINAL_DISCHARGE,
               targets,
               targetSize,
               new Point(-70, 10)
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            this.getPlayer().handleRelicChargeCon(this.getActiveSkillID(), -1, 0);
            break;
         case 5111007:
         case 5120012:
         case 5211007:
         case 5220014:
         case 5311005:
         case 5320007:
         case 35111013:
         case 35120014:
         case 400051001:
            this.onLuckyDice(effect);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 9001020:
         case 9101020:
         case 31111001:
            byte number_of_mobs = packet.readByte();
            packet.skip(3);

            for (int i = 0; i < number_of_mobs; i++) {
               int mobId = packet.readInt();
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(mobId);
               if (mob != null) {
                  mob.switchController(this.getPlayer(), mob.isControllerHasAggro());
                  mob.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, this.activeSkillID, null, false),
                     false,
                     effect.getDuration(),
                     true,
                     effect
                  );
               }
            }

            effect.applyTo(this.getPlayer());
            break;
         case 20050286:
         case 25111209:
         case 80000169:
            effect.applyTo(this.getPlayer(), true);
            break;
         case 30001061:
            int mobID = packet.readInt();
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(mobID);
            int result = 0;
            if (mob == null || mob.getStats().isBoss() || !mob.isAlive() || mob.getStats().getLevel() > this.getPlayer().getLevel()) {
               result = 2;
            } else if (mob.getHPPercent() > effect.getX()) {
               result = 1;
               this.getPlayer().getMap().broadcastMessage(MobPacket.catchMonster(mobID, false, true));
            } else {
               if (mob.getId() / 1000 != 9304) {
                  int posx = this.getPlayer().getWildHunterInfo().getNextCapturedPos();
                  this.getPlayer().getWildHunterInfo().setCapturedMob(posx, mob.getId());
               } else {
                  int index = GameConstants.getJaguarIndex(mob.getId());
                  String key = this.getPlayer().getOneInfoQuest(23008, String.valueOf(index));
                  if (key == null || key.isEmpty() || !key.equals("1")) {
                     this.getPlayer().updateOneInfo(23008, String.valueOf(index), "1");
                     this.getPlayer().onJaguarLinkPassive();
                  }

                  this.getPlayer().getWildHunterInfo().setRidingType(index);
               }

               this.getPlayer().getMap().broadcastMessage(MobPacket.catchMonster(mobID, true, true));
               this.getPlayer().getMap().killMonster(mob, this.getPlayer(), true, false, (byte)1);
               this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.WILD_HUNTER_INFO.getFlag());
               this.getPlayer().send(CWvsContext.updateJaguar(this.getPlayer()));
            }

            PacketEncoder e = new PacketEncoder();
            e.write(result);
            SkillEffect eff = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), this.activeSkillID, this.activeSkillLevel, e);
            this.getPlayer().send(eff.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), eff.encodeForRemote(), false);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 30001062:
            int mobTemplateID = packet.readInt();
            Point posx = packet.readPos();
            MapleMonster monster = MapleLifeFactory.getMonster(mobTemplateID);
            if (monster != null) {
               this.getPlayer().getMap().spawnMonsterOnGroundBelow(monster, posx);
            }

            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.DAZZLE, this.getPlayer().getId(), this.activeSkillID, null, false),
               false,
               effect.getDuration(),
               true,
               effect
            );
            effect.applyTo(this.getPlayer(), true);
            break;
         case 80002758:
            this.tryApplyInvincibleBelief();
            effect.applyTo(this.getPlayer(), true);
            break;
         case 80003226:
            this.getPlayer().temporaryStatSet(80003226, effect.getDuration(), SecondaryStatFlag.DoolInducedCurrent, 1);
            break;
         case 80003228:
            this.getPlayer().temporaryStatSet(80003228, effect.getDuration(), SecondaryStatFlag.Karing2PhasePurify, 1);
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.KaringDisruptDeBuff);
            break;
         case 80003261:
            this.getPlayer().temporaryStatSet(80003261, effect.getDuration(), SecondaryStatFlag.Karing3PhaseLightOfWill, 1);
            break;
         case 160001075:
         case 160011075:
            int shift = this.getPlayer().getShift();
            this.getPlayer().updateInfoQuest(7786, "sw=" + shift);
            this.getPlayer().setShift(shift ^ 1);
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.SHIFT_ON_OFF.getValue());
            p.writeInt(this.getPlayer().getId());
            p.write(shift ^ 1);
            this.getPlayer().getMap().broadcastMessage(this.player, p.getPacket(), false);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 400001061:
            Point position = new Point(packet.readShort(), packet.readShort());
            byte direction = packet.readByte();
            SecondaryStatEffect level = this.getPlayer().getSkillLevelData(400001061);
            if (level != null) {
               Summoned s = new Summoned(
                  this.getPlayer(),
                  400001061,
                  this.getActiveSkillLevel(),
                  position,
                  SummonMoveAbility.STATIONARY,
                  direction,
                  System.currentTimeMillis() + level.getDuration()
               );
               this.getPlayer().getMap().spawnSummon(s, level.getDuration());
               this.getPlayer().addSummon(s);
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.LotusFlower, 400001062, Integer.MAX_VALUE, 1);
            }
            break;
         case 400001064:
            Summoned summon = this.getPlayer().getSummonBySkillID(400001064);
            SecondaryStatEffect erdaEffect = SkillFactory.getSkill(400001064).getEffect(this.getActiveSkillLevel());
            position = new Point(packet.readShort(), packet.readShort());
            if (summon == null) {
               summon = new Summoned(
                  this.getPlayer(),
                  this.getActiveSkillID(),
                  this.getActiveSkillLevel(),
                  position,
                  SummonMoveAbility.STATIONARY,
                  (byte)1,
                  System.currentTimeMillis() + erdaEffect.getDuration()
               );
            } else {
               this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
               this.getPlayer().getMap().removeMapObject(summon);
               this.getPlayer().removeVisibleMapObject(summon);
               this.getPlayer().removeSummon(summon);
               summon.setPosition(position);
            }

            this.getPlayer().getMap().spawnSummon(summon, erdaEffect.getDuration());
            this.getPlayer().addSummon(summon);
            this.erdaFountainStack = 0;
            this.getPlayer().send(CField.skillCooldown(400001036, erdaEffect.getCoolTime()));
            this.getPlayer().addCooldown(400001036, System.currentTimeMillis(), erdaEffect.getCoolTime());
            break;
         case 400041032:
            packet.seek(packet.getPosition() - 9L);
            int flagx = packet.readByteToInt();
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            int duration = effect.getDuration(effect.getDuration(), this.getPlayer());
            if (flagx == 1) {
               statups.put(SecondaryStatFlag.ReadyToDie, this.getPlayer().getReadyToDie() + 1);
               switch (this.getPlayer().getReadyToDie()) {
                  case 0:
                     statups.put(SecondaryStatFlag.indieEvasion, effect.getX());
                     statups.put(SecondaryStatFlag.indieIncreaseHitDamage, effect.getZ());
                     statups.put(SecondaryStatFlag.indiePMDR, effect.getY());
                     break;
                  case 1:
                     statups.put(SecondaryStatFlag.indieEvasion, effect.getW());
                     statups.put(SecondaryStatFlag.indieIncreaseHitDamage, effect.getS());
                     statups.put(SecondaryStatFlag.indiePMDR, effect.getQ());
                     int remain = (int)(this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.ReadyToDie) - System.currentTimeMillis()) / 2;
                     duration = remain;
                     break;
                  default:
                     this.getPlayer().temporaryStatReset(SecondaryStatFlag.ReadyToDie);
                     return;
               }

               this.getPlayer().addReadyToDie();
            } else if (flagx == 2) {
               statups.put(SecondaryStatFlag.ReadyToDie, 2);
               statups.put(SecondaryStatFlag.indieEvasion, effect.getW());
               statups.put(SecondaryStatFlag.indieIncreaseHitDamage, effect.getS());
               statups.put(SecondaryStatFlag.indiePMDR, effect.getQ());
               duration /= 2;
               this.getPlayer().addReadyToDie();
               this.getPlayer().addReadyToDie();
            }

            this.getPlayer().temporaryStatSet(400041032, effect.getLevel(), duration, statups, false, 0, true, false);
            break;
         default:
            if (packet.available() <= 0L) {
               return;
            }

            if (packet.available() > 0L) {
               Point pos = null;
               byte flag = 0;
               byte facingLeft = 0;
               boolean byPet = false;
               if (packet.available() >= 7L) {
                  if (packet.available() >= 4L) {
                     pos = packet.readPos();
                  }

                  if (packet.available() >= 5L) {
                     packet.skip(4);
                  }

                  if (packet.available() >= 1L) {
                     facingLeft = packet.readByte();
                  }
               } else if (this.activeSkillID == 4331006) {
                  packet.skip(4);
                  facingLeft = packet.readByte();
               } else if (this.activeSkillID == 35101005) {
                  pos = packet.readPos();
                  facingLeft = packet.readByte();
               } else if (packet.available() >= 1L) {
                  flag = packet.readByte();
               }

               if ((flag & 128) > 0) {
                  packet.skip(1);
               }

               if (effect.isMagicDoor()) {
                  if (!FieldLimitType.MysticDoor.check(this.getPlayer().getMap().getFieldLimit())) {
                     effect.applyTo(this.getPlayer(), pos);
                  } else {
                     this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
                  }
               } else {
                  if (packet.available() > 0L) {
                     packet.skip(1);
                  }

                  if (packet.available() > 0L) {
                     byPet = packet.readByte() == 1;
                  }

                  int mountid = SecondaryStatEffect.parseMountInfo(this.getPlayer(), skill.getId());
                  if (skill.getId() / 10000 != 8000
                     && skill.getId() != 400031017
                     && skill.getId() != 500061050
                     && skill.getId() != 35111003
                     && mountid != 0
                     && mountid != GameConstants.getMountItem(skill.getId(), this.getPlayer())
                     && !this.getPlayer().isIntern()
                     && this.getPlayer().getBuffedValue(SecondaryStatFlag.RideVehicle) == null
                     && this.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-118) == null
                     && !GameConstants.isMountItemAvailable(mountid, this.getPlayer().getJob())) {
                     this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
                     return;
                  }

                  Integer targetID = this.getPlayer().getBuffedValue(SecondaryStatFlag.HolyUnity);
                  if (skill.isPairSkill() && targetID != null && skill.getId() != 400011003 && skill.getId() != 500061000 && skill.getId() != 1221054) {
                     MapleCharacter target = this.getPlayer().getMap().getCharacterById(targetID);
                     if (target != null) {
                        effect.applyTo(target, pos);
                     }
                  }

                  if (this.activeSkillID != 31101002) {
                     effect.applyTo(this.getPlayer(), pos, facingLeft, this.exclusive);
                  }
               }
            } else {
               effect.applyTo(this.getPlayer(), this.getPlayer().getTruePosition(), (byte)0, this.exclusive);
            }
      }
   }

   @Override
   public void afterActiveSkill() {
   }

   @Override
   public void setActiveSkillPrepareID(int skillID) {
      this.activeSkillPrepareID = skillID;
   }

   @Override
   public void setActiveSkillPrepareSLV(int skillLevel) {
      this.activeSkillPrepareSLV = skillLevel;
   }

   @Override
   public Point getKeyDownRectMoveXY() {
      return this.keyDownRectMoveXY;
   }

   @Override
   public void setKeyDownRectMoveXY(Point point) {
      this.keyDownRectMoveXY = point;
   }

   @Override
   public int getActiveSkillPrepareID() {
      return this.activeSkillPrepareID;
   }

   @Override
   public int getActiveSkillPrepareSLV() {
      return this.activeSkillPrepareSLV;
   }

   @Override
   public void setActiveSkill(int skillID) {
      this.activeSkillID = skillID;
   }

   @Override
   public void setActiveSkillLevel(int skillLevel) {
      this.activeSkillLevel = skillLevel;
   }

   @Override
   public void setExclusive(boolean exclusive) {
      this.exclusive = exclusive;
   }

   @Override
   public void setTeleportAttackAction(TeleportAttackAction action) {
      this.teleportAttackAction = action;
   }

   @Override
   public TeleportAttackAction getTeleportAttackAction() {
      return this.teleportAttackAction;
   }

   @Override
   public void updatePerSecond() {
      this.updateAutoChargeStack();
      this.updateMobSkills();
      if (this.player.hasBuffBySkillID(80003224) || this.player.hasBuffBySkillID(150030241)) {
         int skill = 0;
         int[] skillList = new int[]{150030241, 80003224};

         for (int s : skillList) {
            if (this.getPlayer().getTotalSkillLevel(s) > 0) {
               skill = s;
               break;
            }
         }

         SecondaryStatEffect level = this.getPlayer().getSkillLevelData(skill);
         if (level != null) {
            int x = level.getX();
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * x);
            int mp = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * 0.01 * x);
            this.getPlayer().addMPHP(hp, mp);
            PacketEncoder packetEncoder = new PacketEncoder();
            packetEncoder.writeInt(0);
            packetEncoder.writeInt(0);
            packetEncoder.writeInt(0);
            packetEncoder.writeInt(0);
            SkillEffect e = new SkillEffect(this.player.getId(), this.player.getLevel(), skill, this.getPlayer().getTotalSkillLevel(skill), packetEncoder);
            this.player.send(e.encodeForLocal());
            this.player.getMap().broadcastMessage(this.player, e.encodeForRemote(), false);
         }
      }

      if (this.player.getBuffedValue(SecondaryStatFlag.EmpressBless) != null) {
         SecondaryStatEffect eff = this.player.getBuffedEffect(SecondaryStatFlag.EmpressBless);
         if (eff != null) {
            if (eff.getSourceId() == 400001043 || eff.getSourceId() == 400001044) {
               if (this.player.checkInterval(this.player.getLastEmpressCygnussBlessTime(), eff.getX() * 1000)) {
                  IndieTemporaryStatEntry entry = this.player.getIndieTemporaryStat(SecondaryStatFlag.indieDamR, 400001043);
                  if (eff.getSourceId() == 400001044) {
                     entry = this.player.getIndieTemporaryStat(SecondaryStatFlag.indieDamR, 400001044);
                  }

                  int v = 0;
                  if (entry != null) {
                     v = entry.getStatValue();
                  }

                  v = Math.min(eff.getW(), v + eff.getDamage());
                  SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
                  if (eff.getSourceId() == 400001043) {
                     statManager.changeStatValue(SecondaryStatFlag.indieDamR, 400001043, v);
                  } else {
                     statManager.changeStatValue(SecondaryStatFlag.indieDamR, 400001044, v);
                  }

                  statManager.temporaryStatSet();
                  int hp = (int)(this.player.getStat().getCurrentMaxHp(this.player) * 0.01 * eff.getQ());
                  this.player.healHP(hp);
                  this.player.setLastEmpressCygnussBlessTime(System.currentTimeMillis());
               }
            } else if (eff.getSourceId() == 400001050) {
               SecondaryStat ss = this.player.getSecondaryStat();
               if (ss.EmpressBlessX == 400001051 || ss.EmpressBlessX == 400001055) {
                  if (ss.EmpressBlessX == 400001051) {
                     if (GameConstants.isDemonSlayer(this.player.getJob())) {
                        int mp = (int)(this.player.getStat().getCurrentMaxMp(this.player) * 0.01 * eff.getY());
                        this.player.addMP(mp, true);
                     } else if (GameConstants.isKinesis(this.player.getJob())) {
                        int pp = (int)(((Integer)this.player.getJobField("MaxPPoint")).intValue() * 0.01 * eff.getY());
                        this.player.invokeJobMethod("gainPP", pp);
                     } else {
                        int hp = (int)(this.player.getStat().getCurrentMaxHp(this.player) * 0.01 * eff.getY());
                        this.player.healHP(hp, true);
                     }
                  } else {
                     this.player.send(CField.userBonusAttackRequest(400001055, true, Collections.EMPTY_LIST));
                  }

                  ss.EmpressBlessX = 0;
                  SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.EmpressBless, 400001050, 1);
                  statManager.temporaryStatSet();
               }

               if (this.player.checkInterval(this.player.getLastEmpressCygnussBlessTime(), eff.getX() * 1000)) {
                  int[] skills = new int[]{400001051, 400001053, 400001054, 400001055};
                  int skill = skills[Randomizer.rand(0, skills.length - 1)];
                  PacketEncoder p = new PacketEncoder();
                  p.write(true);
                  SkillEffect e = new SkillEffect(this.player.getId(), this.player.getLevel(), skill, 1, p);
                  this.player.send(e.encodeForLocal());
                  this.player.getMap().broadcastMessage(this.player, e.encodeForRemote(), false);
                  this.player.setLastEmpressCygnussBlessTime(System.currentTimeMillis());
                  ss.EmpressBlessX = skill;
                  SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.EmpressBless, 400001050, 1);
                  statManager.temporaryStatSet();
               }
            }
         }
      }

      if (this.getPlayer().hasBuffBySkillID(2311003)
         && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HolySymbol, 0) != (DBConfig.isGanglim ? 75 : 150)) {
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.HolySymbol);
         if (effect != null && this.getPlayer().getId() != this.holySymbolUserID) {
            MapleCharacter owner = this.getPlayer().getMap().getCharacterById(this.holySymbolUserID);
            if (this.holySymbolActive) {
               boolean bBuffDecrease = false;
               if (owner != null) {
                  if (owner.getParty() != null && this.getPlayer().getParty() != null && owner.getParty().getId() == this.getPlayer().getParty().getId()) {
                     Rectangle rect = effect.calculateBoundingBox(owner.getTruePosition(), owner.isFacingLeft());
                     if (!rect.contains(this.getPlayer().getTruePosition())) {
                        bBuffDecrease = true;
                     }
                  } else {
                     bBuffDecrease = true;
                  }
               } else {
                  bBuffDecrease = true;
               }

               if (bBuffDecrease) {
                  this.holySymbolDecrease = true;
                  this.holySymbolActive = false;
                  this.holySymbolDropR = 0;
                  SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.HolySymbol, 2311003, effect.getX() / 2);
                  statManager.temporaryStatSet();
                  this.getPlayer().send(CWvsContext.InfoPacket.brownMessage("홀리 심볼을 시전한 캐릭터의 근처를 벗어났습니다."));
               }
            } else {
               boolean bBuffIncrease = false;
               if (owner != null) {
                  Rectangle rect = effect.calculateBoundingBox(owner.getTruePosition(), owner.isFacingLeft());
                  if (rect.contains(this.getPlayer().getTruePosition())) {
                     bBuffIncrease = true;
                  }
               }

               if (bBuffIncrease) {
                  this.holySymbolDecrease = false;
                  this.holySymbolActive = true;
                  this.holySymbolDropR = owner.getSkillLevelDataOne(2320048, SecondaryStatEffect::getV);
                  SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.HolySymbol, 2311003, effect.getX());
                  statManager.temporaryStatSet();
                  this.getPlayer().send(CWvsContext.InfoPacket.brownMessage("홀리 심볼을 시전한 캐릭터의 근처에 들어왔습니다."));
               }
            }
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.MichaelSoulLink) != null && this.michaelSoulLinkFrom == 0) {
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.MichaelSoulLink);
         if (eff != null && this.getPlayer().isAlive()) {
            this.getPlayer().addMP(-eff.getMPCon());
            List<MapleCharacter> playerList = this.getPlayerInArea(eff);

            for (MapleCharacter player : playerList) {
               if (player.getId() == this.getPlayer().getId()) {
                  int value = eff.getIndieDamR() * playerList.size();
                  if (player.getBuffedValueDefault(SecondaryStatFlag.MichaelSoulLink, 0) != playerList.size()) {
                     this.michaelSoulLink = 1;
                     this.michaelSoulLinkX = playerList.size();
                     SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
                     statManager.changeStatValue(SecondaryStatFlag.MichaelSoulLink, 51111008, playerList.size());
                     statManager.temporaryStatSet();
                     player.temporaryStatSet(SecondaryStatFlag.indieDamR, 51111008, Integer.MAX_VALUE, value);
                  }
               } else if (!player.hasBuffBySkillID(51111008)) {
                  int totalPAD = (Integer)this.getPlayer().getJobField("royalGuardState");
                  int pad = totalPAD * eff.getX() / 100;
                  int mad = totalPAD * eff.getX() / 100;
                  int asrR = 0;
                  int ddr = 0;
                  Integer vx;
                  if ((vx = this.getPlayer().getBuffedValue(SecondaryStatFlag.AsrR)) != null) {
                     asrR = vx;
                  }

                  if ((vx = this.getPlayer().getBuffedValue(SecondaryStatFlag.DDR)) != null) {
                     ddr = vx;
                  }

                  int y = asrR * eff.getY() / 100;
                  int z = ddr * eff.getZ() / 100;
                  Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
                  player.setJobField("michaelSoulLinkFrom", this.getPlayer().getId());
                  player.setJobField("michaelSoulLink", 0);
                  player.setJobField("michaelSoulLinkY", eff.getQ());
                  player.setJobField("michaelSoulLinkX", 0);
                  SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
                  statManager.changeStatValue(SecondaryStatFlag.MichaelSoulLink, 51111008, playerList.size());
                  statManager.temporaryStatSet();
                  statups.put(SecondaryStatFlag.indiePAD, pad);
                  statups.put(SecondaryStatFlag.indieMAD, mad);
                  statups.put(SecondaryStatFlag.indieAsrR, y);
                  statups.put(SecondaryStatFlag.indiePddR, z);
                  player.temporaryStatSet(51111008, eff.getLevel(), 8000, statups);
               }
            }
         }
      }

      SecondaryStatEffect crossOverChain = this.getPlayer().getBuffedEffect(SecondaryStatFlag.CrossOverChain);
      if (crossOverChain != null) {
         int remainHP = (int)(this.player.getStat().getCurrentMaxHp() - this.player.getStat().getHp());
         int crossOverChainX = Math.min(crossOverChain.getZ(), (int)(remainHP * crossOverChain.getY() / 100.0));
         int value = (int)(this.player.getStat().getHPPercent() / 2.5);
         if (this.crossOverChainX != crossOverChainX || this.player.getBuffedValueDefault(SecondaryStatFlag.CrossOverChain, 0) != value) {
            this.crossOverChainX = crossOverChainX;
            SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.CrossOverChain, 1311015, Math.min(crossOverChain.getX(), value));
            statManager.temporaryStatSet();
         }
      }

      List<IndieTemporaryStatEntry> entryx = this.player.getIndieTemporaryStats(SecondaryStatFlag.indieDotHeal);
      if (entryx != null && this.player.getStat().getHp() > 0L) {
         for (IndieTemporaryStatEntry e : entryx) {
            if (e != null) {
               this.player.addHP(e.getStatValue(), false);
            }
         }
      }

      Integer value;
      if ((value = this.player.getBuffedValue(SecondaryStatFlag.DotHealMPPerSecondR)) != null && this.player.getStat().getHp() > 0L) {
         int hp = (int)(this.player.getStat().getCurrentMaxHp(this.player) * 0.01 * value.intValue());
         this.player.addHP(hp, false);
      }

      if ((value = this.player.getBuffedValue(SecondaryStatFlag.DotHealHPPerSecondR)) != null) {
         int mp = (int)(this.player.getStat().getCurrentMaxMp(this.player) * 0.01 * value.intValue());
         this.player.addMP(mp);
      }

      if ((value = this.player.getBuffedValue(SecondaryStatFlag.RandAreaAttack)) != null
         && this.player.checkInterval(this.player.getLastRandomAreaAttackTime(), value * 1000)) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_RAND_AREA_ATTACK_REQUEST.getValue());
         packet.writeInt(80001762);
         packet.writeInt(1);
         List<MapleMonster> mobs = this.player.getMap().getAllMonstersThreadsafe();
         Collections.shuffle(mobs);
         MapleMonster mob = mobs.stream().findAny().orElse(null);
         if (mob != null) {
            packet.writeInt(mob.getPosition().x);
            packet.writeInt(mob.getPosition().y);
            packet.writeInt(0);
            this.player.send(packet.getPacket());
         }

         this.player.setLastRandomAreaAttackTime(System.currentTimeMillis());
      }
   }

   @Override
   public short getMoveAction() {
      return this.moveAction;
   }

   @Override
   public void setMoveAction(short moveAction) {
      this.moveAction = moveAction;
   }

   @Override
   public byte getSpeed() {
      return this.speed;
   }

   @Override
   public void setSpeed(byte speed) {
      this.speed = speed;
   }

   public void updateAutoChargeStack() {
      if (this.player.getSkillLevel(3311002) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(3311002).getEffect(this.player.getSkillLevel(3311002));
         if (effect != null) {
            this.player.setAutoChargeSkillID(3311002);
            this.player.setAutoChargeMaxStack(effect.getY());
            this.player.setAutoChargeCycle(effect.getS2() * 1000);
         }
      }

      if (this.player.getSkillLevel(400021047) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021047).getEffect(this.player.getSkillLevel(400021047));
         if (effect != null) {
            this.player.setAutoChargeSkillID(400021047);
            this.player.setAutoChargeMaxStack(effect.getY());
            this.player.setAutoChargeCycle(effect.getV() * 1000);
         }
      }

      if (this.player.getSkillLevel(400051042) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400051042).getEffect(this.player.getSkillLevel(400051042));
         if (effect != null) {
            this.player.setAutoChargeSkillID(400051042);
            this.player.setAutoChargeMaxStack(effect.getY());
            this.player.setAutoChargeCycle(effect.getW() * 1000);
         }
      }

      if (this.player.getSkillLevel(400021068) > 0) {
         int skillId = 400021068;
         if (this.player.getTotalSkillLevel(500061012) > 0) {
            skillId = 500061012;
         }

         SecondaryStatEffect effect = SkillFactory.getSkill(skillId).getEffect(this.player.getSkillLevel(skillId));
         if (effect != null) {
            this.player.setAutoChargeSkillID(skillId);
            this.player.setAutoChargeMaxStack(effect.getY());
            this.player.setAutoChargeCycle(effect.getQ() * 1000);
         }
      }

      if (this.player.getSkillLevel(400011131) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400011131).getEffect(this.player.getSkillLevel(400011131));
         if (effect != null) {
            this.player.setAutoChargeSkillID(400011131);
            this.player.setAutoChargeMaxStack(effect.getY());
            this.player.setAutoChargeCycle(effect.getX() * 1000);
         }
      }

      if (this.player.getSkillLevel(400021086) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(400021086).getEffect(this.player.getSkillLevel(400021086));
         if (effect != null) {
            this.player.setAutoChargeSkillID(400021086);
            this.player.setAutoChargeMaxStack(effect.getY());
            this.player.setAutoChargeCycle((int)(effect.getT() * 1000.0));
         }
      }

      if (this.player.getAutoChargeSkillID() != 0
         && (this.player.getLastAutoChargeTime() == 0L || System.currentTimeMillis() - this.player.getLastAutoChargeTime() >= this.player.getAutoChargeCycle())
         )
       {
         if (this.player.getBuffedValue(SecondaryStatFlag.AutoChargeStack) != null && this.player.getAutoChargeStack() < this.player.getAutoChargeMaxStack()) {
            this.player.setAutoChargeStack(this.player.getAutoChargeStack() + 1);
         }

         this.player
            .temporaryStatSet(this.player.getAutoChargeSkillID(), Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack, this.player.getAutoChargeStack());
         this.player.setLastAutoChargeTime(System.currentTimeMillis());
      }

      if (this.player.getTotalSkillLevel(400041074) > 0) {
         SecondaryStatEffect e = this.player.getSkillLevelData(400041074);
         if (e != null) {
            if (this.player.getLastAutoChargeTime() == 0L) {
               this.player.setLastAutoChargeTime(System.currentTimeMillis());
            }

            if (this.player.checkInterval(this.player.getLastAutoChargeTime(), (int)(e.getT() * 1000.0))) {
               Integer value = this.player.getBuffedValue(SecondaryStatFlag.WeaponVarietyFinale);
               int v = 0;
               if (value != null) {
                  v = value;
               }

               if (v < e.getY()) {
                  this.player.temporaryStatSet(400041074, Integer.MAX_VALUE, SecondaryStatFlag.WeaponVarietyFinale, ++v);
                  this.player.setLastAutoChargeTime(System.currentTimeMillis());
               }
            }
         }
      }
   }

   public void updateMobSkills() {
      if (this.player.serenLaserDebuffEndTime != 0L && System.currentTimeMillis() >= this.player.serenLaserDebuffEndTime) {
         this.player.send(CField.blind(0, 255, 255, 255, 255, 500, 0));
         this.player.serenLaserDebuffEndTime = 0L;
      }

      if (this.player.getNextDebuffIncHPTime() != 0L && System.currentTimeMillis() >= this.player.getNextDebuffIncHPTime()) {
         if (this.player.getMap() instanceof Field_WillBattle) {
            SecondaryStatEffect eff = SkillFactory.getSkill(80002404).getEffect(50);
            if (eff != null) {
               eff.applyTo(this.player);
            }
         }

         if (this.player.getMap() instanceof Field_JinHillah) {
            SecondaryStatEffect eff = SkillFactory.getSkill(80002543).getEffect(50);
            if (eff != null) {
               eff.applyTo(this.player);
            }
         }

         this.player.setNextDebuffIncHPTime(0L);
      }

      if (this.player.getMap() instanceof Field_BlackMage) {
         Field_BlackMage f = (Field_BlackMage)this.player.getMap();
         Integer value = null;
         if ((value = this.player.getBuffedValue(SecondaryStatFlag.CurseOfDestruction)) != null && f != null) {
            MapleDiseaseValueHolder h = this.player.getDiseases(SecondaryStatFlag.CurseOfDestruction);
            if (h != null && h.level == 1) {
               int hp = (int)(this.player.getStat().getCurrentMaxHp(this.player) * 0.01) * value;
               int mp = (int)(this.player.getStat().getCurrentMaxMp(this.player) * 0.01) * value;
               this.player.healHP(hp);
               this.player.healMP(mp);
               if (this.player.lastCreateRedFlamesTime + 2000L <= System.currentTimeMillis()) {
                  int level = 1;
                  if (this.player.getMap() instanceof Field_BlackMageBattlePhase4) {
                     level = 2;
                  }

                  f.sendCreateRedFalmes(this.player, level);
                  this.player.lastCreateRedFlamesTime = System.currentTimeMillis();
               }
            }
         }
      }

      if (this.player.getNextEndRemoveWebTime() != 0L && System.currentTimeMillis() >= this.player.getNextEndRemoveWebTime()) {
         this.player.setNextEndRemoveWebTime(0L);
         this.player.setWillCanRemoveWeb(0);
      }

      if (this.player.getBuffedValue(SecondaryStatFlag.WillPoison) != null && this.player.getMap() instanceof Field_WillBattle) {
         Field_WillBattle f = (Field_WillBattle)this.player.getMap();
         f.sendWillPoisonAttack();

         for (MapleCharacter p : this.player.getMap().getPlayerInRect(this.player.getTruePosition(), -200, -200, 200, 200)) {
            if (p.getId() != this.player.getId()) {
               int hp = (int)(p.getStat().getCurrentMaxHp(p) * 0.01 * 44.0);
               p.healHP(-hp);
            }
         }

         if (this.player.getLastWillAttackTime() != 0L && System.currentTimeMillis() >= this.player.getLastWillAttackTime() + 7000L) {
            this.player.addHP(-500000L);
            this.player.setLastWillAttackTime(0L);
            this.player.temporaryStatReset(SecondaryStatFlag.WillPoison);
            f.sendWillRemovePoison(this.player);
            List<MapleCharacter> players = this.player.getMap().getCharactersThreadsafe();
            Collections.shuffle(players);
            MapleCharacter target = players.stream().filter(px -> px.getId() != this.player.getId()).filter(MapleCharacter::isAlive).findAny().orElse(null);
            if (target != null) {
               f.sendWillCreatePoison(target);
               target.setLastWillAttackTime(System.currentTimeMillis());
               target.giveDebuff(SecondaryStatFlag.WillPoison, 1, 0, 15000L, 242, 9);
            }
         }
      }

      if (this.player.getBuffedValue(SecondaryStatFlag.MobZoneState) != null && !this.player.isInvincible() && this.player.getMap() instanceof Field_Demian) {
         int hp = (int)(this.player.getStat().getCurrentMaxHp(this.player) * 0.01 * 5.0);
         this.player.addHP(-hp);
         HPHeal e = new HPHeal(this.player.getId(), -hp);
         this.player.send(e.encodeForLocal());
         this.player.getMap().broadcastMessage(this.player, e.encodeForRemote(), false);
      }
   }

   public static BasicJob setJob(int job) {
      switch (job) {
         case 100:
            return new DefaultWarrior();
         case 110:
         case 111:
         case 112:
            return new Hero();
         case 120:
         case 121:
         case 122:
            return new Paladin();
         case 130:
         case 131:
         case 132:
            return new DarkKnight();
         case 200:
            return new DefaultMagician();
         case 210:
         case 211:
         case 212:
            return new ArcMageFP();
         case 220:
         case 221:
         case 222:
            return new ArcMageIL();
         case 230:
         case 231:
         case 232:
            return new Bishop();
         case 300:
            return new DefaultArcher();
         case 301:
         case 330:
         case 331:
         case 332:
            return new PathFinder();
         case 310:
         case 311:
         case 312:
            return new BowMaster();
         case 320:
         case 321:
         case 322:
            return new Marksman();
         case 400:
            return new DefaultThief();
         case 410:
         case 411:
         case 412:
            return new NightLord();
         case 420:
         case 421:
         case 422:
            return new Shadower();
         case 430:
         case 431:
         case 432:
         case 433:
         case 434:
            return new DualBlade();
         case 500:
            return new DefaultPirate();
         case 501:
         case 530:
         case 531:
         case 532:
            return new Cannoneer();
         case 510:
         case 511:
         case 512:
            return new Buccaneer();
         case 520:
         case 521:
         case 522:
            return new Captain();
         case 1100:
         case 1110:
         case 1111:
         case 1112:
            return new SoulMaster();
         case 1200:
         case 1210:
         case 1211:
         case 1212:
            return new FlameWizard();
         case 1300:
         case 1310:
         case 1311:
         case 1312:
            return new WindArcher();
         case 1400:
         case 1410:
         case 1411:
         case 1412:
            return new NightWalker();
         case 1500:
         case 1510:
         case 1511:
         case 1512:
            return new Striker();
         case 2000:
         case 2100:
         case 2110:
         case 2111:
         case 2112:
            return new Aran();
         case 2001:
         case 2200:
         case 2210:
         case 2211:
         case 2212:
         case 2213:
         case 2214:
         case 2215:
         case 2216:
         case 2217:
         case 2218:
            return new Evan();
         case 2002:
         case 2300:
         case 2310:
         case 2311:
         case 2312:
            return new Mercedes();
         case 2003:
         case 2400:
         case 2410:
         case 2411:
         case 2412:
            return new Phantom();
         case 2004:
         case 2700:
         case 2710:
         case 2711:
         case 2712:
            return new Luminous();
         case 2005:
         case 2500:
         case 2510:
         case 2511:
         case 2512:
            return new Shade();
         case 3002:
         case 3600:
         case 3610:
         case 3611:
         case 3612:
            return new Xenon();
         case 3100:
         case 3110:
         case 3111:
         case 3112:
            return new DemonSlayer();
         case 3101:
         case 3120:
         case 3121:
         case 3122:
            return new DemonAvenger();
         case 3200:
         case 3210:
         case 3211:
         case 3212:
            return new BattleMage();
         case 3300:
         case 3310:
         case 3311:
         case 3312:
            return new WildHunter();
         case 3500:
         case 3510:
         case 3511:
         case 3512:
            return new Mechanic();
         case 3700:
         case 3710:
         case 3711:
         case 3712:
            return new Blaster();
         case 5000:
         case 5100:
         case 5110:
         case 5111:
         case 5112:
            return new Mikhail();
         case 6000:
         case 6100:
         case 6110:
         case 6111:
         case 6112:
            return new Kaiser();
         case 6001:
         case 6500:
         case 6510:
         case 6511:
         case 6512:
            return new AngelicBuster();
         case 6002:
         case 6400:
         case 6410:
         case 6411:
         case 6412:
            return new Cadena();
         case 6003:
         case 6300:
         case 6310:
         case 6311:
         case 6312:
            return new Kain();
         case 10112:
            return new Zero();
         case 13100:
            return new Pinkbean();
         case 13500:
            return new Yeti();
         case 14000:
         case 14200:
         case 14210:
         case 14211:
         case 14212:
            return new Kinesis();
         case 15000:
         case 15200:
         case 15210:
         case 15211:
         case 15212:
            return new Illium();
         case 15001:
         case 15500:
         case 15510:
         case 15511:
         case 15512:
            return new Ark();
         case 15002:
         case 15100:
         case 15110:
         case 15111:
         case 15112:
            return new Adele();
         case 15003:
         case 15400:
         case 15410:
         case 15411:
         case 15412:
            return new Khali();
         case 16000:
         case 16400:
         case 16410:
         case 16411:
         case 16412:
            return new Hoyoung();
         case 16001:
         case 16200:
         case 16210:
         case 16211:
         case 16212:
            return new Lara();
         default:
            return new CommonJob();
      }
   }

   public void trySummonedAttackManual() {
      if (GameConstants.isDemonSlayer(this.getPlayer().getJob())) {
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(400011077);
         if (effect != null) {
            Summoned summoned = this.getPlayer().getSummonBySkillID(400011077);
            if (summoned != null && this.getPlayer().checkInterval(this.getPlayer().getLastOrthros1(), effect.getX() * 1000)) {
               this.getPlayer().setLastOrthros1(System.currentTimeMillis());
               this.getPlayer().send(CField.summonAssistAttackRequest(this.getPlayer().getId(), summoned.getObjectId(), 0));
            }
         }

         effect = this.getPlayer().getSkillLevelData(400011078);
         if (effect != null) {
            Summoned summoned = this.getPlayer().getSummonBySkillID(400011078);
            if (summoned != null && this.getPlayer().checkInterval(this.getPlayer().getLastOrthros2(), effect.getX() * 1000)) {
               this.getPlayer().setLastOrthros2(System.currentTimeMillis());
               this.getPlayer().send(CField.summonAssistAttackRequest(this.getPlayer().getId(), summoned.getObjectId(), 0));
            }
         }
      }
   }

   public void tryApplyEmpiricalKnowledge(AttackInfo attack) {
      SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(80002762);
      if (effect != null && attack.targets > 0) {
         for (AttackPair ap : attack.allDamage) {
            if (Randomizer.isSuccess(effect.getProb())) {
               MapleMonster target = null;
               long hp = 0L;

               for (AttackPair a : attack.allDamage) {
                  MapleMonster m = this.getPlayer().getMap().getMonsterByOid(a.objectid);
                  if (m != null && m.isAlive() && m.getStats().getMaxHp() > hp) {
                     target = m;
                     hp = m.getStats().getMaxHp();
                  }
               }

               if (target != null) {
                  int value = 0;
                  if (this.empericalKnowledgeX != target.getObjectId()) {
                     value = 1;
                  } else {
                     value = Math.min(this.empericalKnowledgeX + 1, effect.getX());
                  }

                  this.empericalKnowledgeX = target.getObjectId();
                  this.getPlayer().temporaryStatSet(80002762, effect.getDuration(), SecondaryStatFlag.EmpericalKnowledge, value);
                  if (this.getPlayer().getTotalSkillLevel(80002770) > 0 && this.getPlayer().getCooldownLimit(80002770) == 0L) {
                     SecondaryStatEffect e = this.getPlayer().getSkillLevelData(80002770);
                     this.getPlayer().send(CField.skillCooldown(80002770, e.getCooldown(this.getPlayer())));
                     this.getPlayer().addCooldown(80002770, System.currentTimeMillis(), e.getCooldown(this.getPlayer()));
                     this.getPlayer().temporaryStatSet(80002770, e.getDuration(), SecondaryStatFlag.indieDamR, e.getIndieDamR());
                  }
               }
            }
         }
      }
   }

   public void playerOnAttack(long maxmp, int skillID, long totDamage) {
      if (this.player.getStat().hpRecoverProp > 0 && Randomizer.nextInt(100) <= this.player.getStat().hpRecoverProp) {
         if (this.player.getStat().hpRecover > 0) {
            this.player.healHP(this.player.getStat().hpRecover);
         }

         if (this.player.getStat().hpRecoverPercent > 0) {
            this.player
               .addHP(
                  (int)Math.min(
                     this.player.getStat().getMaxHp(), (long)((double)this.player.getStat().getMaxHp() * this.player.getStat().hpRecoverPercent / 100.0)
                  )
               );
         }
      }

      if (this.player.getStat().mpRecoverProp > 0
         && !GameConstants.isDemonSlayer(this.player.getJob())
         && Randomizer.nextInt(100) <= this.player.getStat().mpRecoverProp) {
         this.player.healMP(this.player.getStat().mpRecover);
      }

      if (this.player.getBuffedValue(SecondaryStatFlag.AranDrain) != null) {
         if (this.player.getSecondaryStatReason(SecondaryStatFlag.AranDrain) == 21101005) {
            SecondaryStatEffect eff = this.player.getBuffedEffect(SecondaryStatFlag.AranDrain);
            if (eff != null) {
               this.player.addHP((int)Math.min((double)this.player.getStat().getMaxHp(), this.player.getStat().getMaxHp() * 0.01 * eff.getX()));
            }
         } else {
            this.player
               .addHP(
                  (int)Math.min(
                     this.player.getStat().getMaxHp(),
                     Math.min(
                        (long)((double)totDamage * this.player.getBuffedEffect(SecondaryStatFlag.AranDrain).getX() / 100.0),
                        this.player.getStat().getMaxHp() / 2L
                     )
                  )
               );
         }
      }

      if (this.player.getSecondaryStatReason(SecondaryStatFlag.AranDrain) == 23101003) {
         this.player
            .addMP(
               (int)Math.min(
                  maxmp,
                  Math.min(
                     (long)((double)totDamage * this.player.getBuffedEffect(SecondaryStatFlag.AranDrain).getX() / 100.0), this.player.getStat().getMaxMp() / 2L
                  )
               )
            );
      }
   }

   public void tryApplyInvincibleBelief() {
      int skillID = 80002758;
      if (this.player.getJob() >= 110 && this.player.getJob() <= 112) {
         skillID = 252;
      } else if (this.player.getJob() >= 120 && this.player.getJob() <= 122) {
         skillID = 253;
      } else if (this.player.getJob() >= 130 && this.player.getJob() <= 132) {
         skillID = 254;
      }

      SecondaryStatEffect eff = this.player.getSkillLevelData(skillID);
      if (eff != null) {
         int x = (int)(eff.getY() * this.player.getStat().getCurrentMaxHp(this.player) * 0.01);
         this.player.temporaryStatSet(skillID, eff.getDuration(), SecondaryStatFlag.indieDotHeal, x);
      }
   }

   public void onLuckyDice(SecondaryStatEffect effect) {
      String skillName = "더블 럭키 다이스";
      List<Integer> datas = new ArrayList<>();
      if (this.nextDiceChange || effect.makeChanceResult() && this.getActiveSkillID() != 400051001) {
         int max = 6;
         int min = 1;
         if (this.player.getTotalSkillLevel(5120044) > 0 || this.player.getTotalSkillLevel(5220044) > 0) {
            max = 7;
         }

         int enhanceProp = 0;
         SecondaryStatEffect enhance = this.player.getSkillLevelData(5120045);
         if (enhance != null) {
            enhanceProp = enhance.getProb();
         }

         if (this.nextDiceChange) {
            min = 4;
         }

         if (Randomizer.isSuccess(enhanceProp)) {
            datas.add(Randomizer.rand(4, max));
         } else {
            datas.add(Randomizer.rand(min, max));
         }

         if (Randomizer.isSuccess(enhanceProp)) {
            datas.add(Randomizer.rand(4, max));
         } else {
            datas.add(Randomizer.rand(min, max));
         }

         this.nextDiceChange = false;
      }

      Integer value = null;
      if ((value = this.player.getBuffedValue(SecondaryStatFlag.LoadedDice)) != null) {
         datas.add(value);
      }

      int buffID = this.getActiveSkillID();
      int cooltime = effect.getCooldown(this.player);
      if (datas.size() <= 1) {
         skillName = "럭키 다이스";
         if (this.getActiveSkillID() == 5120012) {
            buffID = 5111007;
         } else if (this.getActiveSkillID() == 5220014) {
            buffID = 5211007;
         } else if (this.getActiveSkillID() == 5320007) {
            buffID = 5311005;
         } else if (this.getActiveSkillID() == 35120014) {
            buffID = 35111013;
         }

         if (this.getActiveSkillID() != 400051001 || (value = this.player.getBuffedValue(SecondaryStatFlag.LoadedDice)) == null) {
            datas.add(Randomizer.rand(1, 6));
         }

         SecondaryStatEffect chance;
         if ((chance = this.player.getSkillLevelData(5120043)) != null || (chance = this.player.getSkillLevelData(5220043)) != null) {
            if (chance.makeChanceResult()) {
               cooltime = 0;
            }

            this.nextDiceChange = true;
         }
      }

      if (value != null && datas.size() == 2) {
         datas.add(1);
      }

      AtomicBoolean fail = new AtomicBoolean(true);
      List<String> dataString = new ArrayList<>();
      datas.forEach(i -> {
         if (i != 1) {
            fail.set(false);
         }

         dataString.add("[" + i + "]");
      });
      int x = 0;
      if (fail.get()) {
         String str = String.join(", ", dataString);
         this.player.dropMessage(5, skillName + " 스킬이 " + str + " 이 나와 아무런 효과를 받지 못했습니다.");
         cooltime = effect.getCooldown(this.player) / 2;
      } else {
         for (int index = 0; index < datas.size(); index++) {
            x += (int)(Math.pow(10.0, index) * datas.get(index).intValue());
         }

         Arrays.fill(this.diceStatData, 0);

         for (int data : datas) {
            switch (data) {
               case 2:
                  this.addDiceStat(7, effect.getWDEFRate());
                  break;
               case 3:
                  this.addDiceStat(0, effect.getW());
                  break;
               case 4:
                  this.addDiceStat(1, effect.getCr());
                  break;
               case 5:
                  this.addDiceStat(11, effect.getDAMRate());
                  break;
               case 6:
                  this.addDiceStat(16, effect.getEXPRate());
                  break;
               case 7:
                  this.addDiceStat(17, effect.getIgnoreMob());
            }

            if (data != 1) {
               this.player.dropMessage(5, skillName + " 스킬이 [" + data + "]번 효과를 발동시켰습니다.");
            }
         }

         this.player.temporaryStatSet(buffID, effect.getDuration(effect.getDuration(), this.player), SecondaryStatFlag.Dice, x);
      }

      this.player.send(CField.skillCooldown(this.getActiveSkillID(), cooltime));
      this.player.addCooldown(this.getActiveSkillID(), System.currentTimeMillis(), cooltime);
      if (value != null && this.getActiveSkillID() != 400051001) {
         int skillID = this.player.getSecondaryStatReason(SecondaryStatFlag.LoadedDice);
         int skillLevel = this.player.getTotalSkillLevel(skillID);
         this.sendLuckyDiceEffect(skillID, skillLevel, false, -1, 1);

         for (int index = 0; index < datas.size(); index++) {
            boolean special = index > 0;
            boolean flag = index == datas.size() - 1;
            this.sendLuckyDiceEffect(skillID, skillLevel, special, datas.get(index), flag ? 0 : -1);
         }

         this.sendLuckyDiceEffect(skillID, skillLevel, false, -1, 2);
      } else {
         for (int index = 0; index < datas.size(); index++) {
            boolean special = index > 0;
            this.sendLuckyDiceEffect(buffID, this.player.getTotalSkillLevel(buffID), special, datas.get(index), -1);
         }
      }
   }

   private void sendLuckyDiceEffect(int skillID, int skillLevel, boolean special, int selectDice, int rootSelect) {
      DiceRoll roll = new DiceRoll(this.player.getId(), selectDice, rootSelect, skillID, skillLevel, special);
      this.player.getMap().broadcastMessage(this.player, roll.encodeForRemote(), false);
      this.player.send(roll.encodeForLocal());
   }

   public void addDiceStat(int index, int value) {
      if (this.diceStatData[index] == 0) {
         this.diceStatData[index] = value;
      } else {
         this.diceStatData[index] = this.diceStatData[index] + value;
      }
   }

   public int onNobilityInit(SecondaryStatEffect effect, int damage) {
      int reduce = (int)(damage * effect.getX() / 100.0);
      int shield = (int)(reduce * effect.getY() / 100.0);
      if (this.nobilityFromID == this.player.getId()) {
         if (reduce < this.player.getStat().getHp()) {
            this.setNobilityShield(this.nobilityShield + shield);
         }
      } else {
         MapleCharacter owner = this.player.getMap().getCharacterById(this.nobilityFromID);
         if (owner != null) {
            int shield_ = (Integer)owner.getJobField("nobilityShield");
            if (reduce < owner.getStat().getHp()) {
               owner.addHP(-reduce);
               owner.invokeJobMethod("setNobilityShield", shield + shield_);
               return reduce;
            }
         } else {
            this.player.temporaryStatReset(SecondaryStatFlag.Nobility);
         }
      }

      return 0;
   }

   public void setNobilityShield(int delta) {
      this.nobilityShield = delta;
      SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
      statManager.changeStatValue(SecondaryStatFlag.Nobility, 151111005, this.player.getBuffedValueDefault(SecondaryStatFlag.Nobility, 0));
      statManager.temporaryStatSet();
   }

   public int onSoulLinkHit(int damage) {
      MapleCharacter owner = this.player.getMap().getCharacterById(this.michaelSoulLinkFrom);
      if (owner != null
         && owner.getId() != this.player.getId()
         && owner.getParty() != null
         && this.player.getParty() != null
         && owner.getParty().getId() == this.player.getParty().getId()) {
         SecondaryStatEffect eff = this.player.getBuffedEffect(SecondaryStatFlag.MichaelSoulLink);
         if (eff != null) {
            int shareD = (int)(damage * eff.getQ() / 100.0);
            if (shareD < owner.getStat().getCurrentMaxHp() * eff.getV() / 100.0 && shareD < owner.getStat().getHp()) {
               Object b = owner.invokeJobMethod("onCheckRoyalGuard");
               if (b == null || !(Boolean)b) {
                  owner.addHP(-shareD);
                  return shareD;
               }
            }
         }
      }

      return 0;
   }

   public void onRhoAiasHit() {
      MapleCharacter owner = this.player.getMap().getCharacterById(this.rhoAiasFrom);
      if (owner != null
         && (
            owner.getId() == this.player.getId()
               || owner.getParty() != null && this.player.getParty() != null && owner.getParty().getId() == this.player.getParty().getId()
         )) {
         int skillId = 400011011;
         if (this.player.getSkillLevel(500061008) > 0) {
            skillId = 500061008;
         }

         int newX = this.rhoAiasX - 1;
         if (newX > 0) {
            this.rhoAiasX = newX;
            SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.RhoAias, skillId, this.player.getBuffedValue(SecondaryStatFlag.RhoAias));
            statManager.temporaryStatSet();
            return;
         }

         int newLevel = this.rhoAiasLevel + 1;
         if (newLevel <= 3) {
            this.rhoAiasLevel = newLevel;
            this.rhoAiasX = this.getRhoAiasDefenceCount(newLevel);
            SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.RhoAias, skillId, this.player.getBuffedValue(SecondaryStatFlag.RhoAias));
            statManager.temporaryStatSet();
            return;
         }
      }

      this.player.temporaryStatReset(SecondaryStatFlag.RhoAias);
   }

   public int getRemainingTotalRhoAiasDefenceCount() {
      int total = 0;

      for (int i = this.rhoAiasLevel; i <= 3; i++) {
         if (i != this.rhoAiasLevel) {
            total += this.getRhoAiasDefenceCount(i);
         } else {
            total += this.rhoAiasX;
         }
      }

      return total;
   }

   public int getRhoAiasDefenceCount(int level) {
      int skillID = 400011011;
      if (this.getPlayer().getTotalSkillLevel(500061008) > 0) {
         skillID = 500061008;
      }

      SecondaryStatEffect effect = this.player.getSkillLevelData(skillID);
      if (effect != null) {
         switch (level) {
            case 1:
               return effect.getY();
            case 2:
               return effect.getW();
            case 3:
               return effect.getZ();
         }
      }

      return 0;
   }

   public boolean checkAntiMagicShell(SecondaryStatEffect effect) {
      switch (effect.getSourceId()) {
         case 2111011:
            int countxx = this.getPlayer().getBuffedValue(SecondaryStatFlag.AntiMagicShell);
            int delta = (int)(this.getPlayer().getStat().getCurrentMaxMp(this.getPlayer()) * (effect.getX() * 0.01));
            if (countxx > 0 && this.getPlayer().getStat().getMp() >= delta && effect.makeChanceResult()) {
               this.getPlayer().temporaryStatSet(2111011, Integer.MAX_VALUE, SecondaryStatFlag.AntiMagicShell, --countxx);
               this.getPlayer().addMP(-delta);
               NormalEffect e = new NormalEffect(this.getPlayer().getId(), EffectHeader.ResistEffect);
               this.getPlayer().send(e.encodeForLocal());
               this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
               return false;
            }

            WZEffectBased e = new WZEffectBased(this.getPlayer().getId(), "Skill/211.img/skill/2111011/special0");
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.AntiMagicShell);
            SecondaryStatEffect real = SkillFactory.getSkill(2111011).getEffect(this.getPlayer().getSkillLevel(2111011));
            if (real != null) {
               this.getPlayer().send(CField.skillCooldown(2111011, real.getCooldown(this.getPlayer())));
               this.getPlayer().addCooldown(2111011, System.currentTimeMillis(), real.getCooldown(this.getPlayer()));
            }
            break;
         case 2211012:
            if (!this.bAntiMagicShellBarrier) {
               this.bAntiMagicShellBarrier = true;
               this.tAntiMagicShellBarrier = effect.getDuration();
               SecondaryStatManager statManager = new SecondaryStatManager(this.player.getClient(), this.player.getSecondaryStat());
               statManager.changeTill(SecondaryStatFlag.AntiMagicShell, 2211012, effect.getDuration());
               statManager.temporaryStatSet();
               this.player.getSecondaryStat().setVarriableLong("AntiMagicShellTill", System.currentTimeMillis() + effect.getDuration());
               this.player.addCooldown(2211012, System.currentTimeMillis(), effect.getCooldown(this.player));
               this.player.send(CField.skillCooldown(2211012, effect.getCooldown(this.player)));
               SpecialSkillEffect e2 = new SpecialSkillEffect(this.player.getId(), 2211012, null);
               this.player.send(e2.encodeForLocal());
               this.player.getMap().broadcastMessage(this.player, e2.encodeForRemote(), false);
            }

            return false;
         case 2311012:
            int count = this.getPlayer().getBuffedValue(SecondaryStatFlag.AntiMagicShell);
            if (count > 0) {
               if (--count <= 0) {
                  SecondaryStatEffect e2 = this.getPlayer().getBuffedEffect(SecondaryStatFlag.AntiMagicShell);
                  if (e2 != null) {
                     this.getPlayer().send(CField.skillCooldown(2311012, effect.getCooldown(this.getPlayer())));
                     this.getPlayer().addCooldown(2311012, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
                  }

                  this.getPlayer().temporaryStatResetBySkillID(27111004);
               } else {
                  this.getPlayer().temporaryStatSet(2311012, effect.getDuration(), SecondaryStatFlag.AntiMagicShell, count);
               }

               return false;
            }

            return false;
         case 12121003:
            count = this.getPlayer().getBuffedValue(SecondaryStatFlag.AntiMagicShell);
            if (count > 0) {
               if (--count <= 0) {
                  this.getPlayer().temporaryStatResetBySkillID(12121003);
               } else {
                  long till = this.getPlayer().getSecondaryStat().getTill(SecondaryStatFlag.AntiMagicShell);
                  int remainDuration = 0;
                  if (till <= 0L) {
                     remainDuration = 0;
                  } else {
                     remainDuration = (int)(till - System.currentTimeMillis());
                  }

                  this.getPlayer().temporaryStatSet(12121003, remainDuration, SecondaryStatFlag.AntiMagicShell, count);
               }

               return false;
            }
            break;
         case 27111004:
            int countx = this.getPlayer().getBuffedValue(SecondaryStatFlag.AntiMagicShell);
            if (countx > 0) {
               if (--countx <= 0) {
                  SecondaryStatEffect e2 = this.getPlayer().getBuffedEffect(SecondaryStatFlag.AntiMagicShell);
                  if (e2 != null) {
                     this.getPlayer().send(CField.skillCooldown(27111004, effect.getCooldown(this.getPlayer())));
                     this.getPlayer().addCooldown(27111004, System.currentTimeMillis(), effect.getCooldown(this.getPlayer()));
                  }

                  this.getPlayer().temporaryStatResetBySkillID(27111004);
               } else {
                  this.getPlayer().temporaryStatSet(27111004, effect.getDuration(), SecondaryStatFlag.AntiMagicShell, countx);
               }

               return false;
            }
      }

      return !effect.isHeroWill();
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(flag);
      switch (flag) {
         case Dice:
            for (int diceStat : this.diceStatData) {
               packet.writeInt(diceStat);
            }
            break;
         case AntiMagicShell:
            packet.write(this.bAntiMagicShellBarrier);
            packet.writeInt(this.tAntiMagicShellBarrier);
            break;
         case EmpericalKnowledge:
            packet.writeInt(this.empericalKnowledgeX);
            break;
         case RhoAias:
            packet.writeInt(this.rhoAiasFrom);
            packet.writeInt(this.rhoAiasC);
            packet.writeInt(this.rhoAiasX);
            packet.writeInt(this.rhoAiasLevel);
            break;
         case Nobility:
            packet.writeInt(this.nobilityFromID);
            packet.writeInt(this.nobilityShield);
            break;
         case CrossOverChain:
            int remainHP = (int)(this.player.getStat().getCurrentMaxHp() - this.player.getStat().getHp());
            if (effect == null) {
               packet.writeInt(0);
            } else {
               this.crossOverChainX = Math.min(effect.getZ(), (int)(remainHP * effect.getY() / 100.0));
               packet.writeInt(this.crossOverChainX);
            }
            break;
         case MichaelSoulLink:
            packet.writeInt(this.michaelSoulLinkX);
            packet.write(this.michaelSoulLink);
            packet.writeInt(this.michaelSoulLinkFrom);
            packet.writeInt(this.michaelSoulLinkY);
            break;
         case Bless:
            packet.writeInt(effect.getX());
            break;
         case AdvancedBless:
            packet.writeInt(this.player.getSkillLevelDataOne(2320050, SecondaryStatEffect::getBossDamage));
            packet.writeInt(effect.getX() + this.player.getSkillLevelDataOne(2320049, SecondaryStatEffect::getX));
            break;
         case HolySymbol:
            if (DBConfig.isGanglim && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HolySymbol, 0) == 75
               || !DBConfig.isGanglim && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HolySymbol, 0) == 150) {
               this.holySymbolDropR = DBConfig.isGanglim ? 25 : 30;
               packet.writeInt(this.getPlayer().getId());
               packet.writeInt(DBConfig.isGanglim ? 20 : 1);
               packet.writeInt(1);
               packet.writeInt(0);
               packet.write(true);
               packet.write(false);
               packet.writeInt(this.holySymbolDropR);
            } else {
               packet.writeInt(this.holySymbolUserID);
               packet.writeInt(this.holySymbolLv);
               packet.writeInt(this.holySymbolUserID != this.player.getId() ? 1 : 0);
               packet.writeInt(0);
               packet.write(this.holySymbolActive);
               packet.write(this.holySymbolDecrease);
               packet.writeInt(this.holySymbolDropR);
            }
            break;
         case DotHealHPPerSecondR:
         case DotHealMPPerSecondR:
            packet.writeInt(effect.getDuration() / 1000);
            break;
         case HolyUnity:
            Integer fromID = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HolyUnity, 0);
            if (fromID == 0 || fromID == this.getPlayer().getId()) {
               packet.writeShort(1);
            } else if (!GameConstants.isPaladin(this.getPlayer().getJob())) {
               SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.HolyUnity);
               if (e != null) {
                  packet.writeShort(0);
               } else {
                  packet.writeShort(1);
               }
            } else {
               packet.writeShort(1);
            }
            break;
         case Magnet:
            packet.writeInt(this.player.getMagnetAreaFrom());
            break;
         case CurseOfCreation:
         case CurseOfDestruction:
         case Unk76:
            packet.writeInt(0);
      }
   }

   @Override
   public void encodeForRemote(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case AntiMagicShell:
            packet.write(this.bAntiMagicShellBarrier);
            packet.writeInt(this.tAntiMagicShellBarrier);
            break;
         case RhoAias:
            packet.writeInt(this.rhoAiasFrom);
            packet.writeInt(this.rhoAiasC);
            packet.writeInt(this.rhoAiasX);
            packet.writeInt(this.rhoAiasLevel);
            break;
         case Nobility:
            packet.writeInt(this.nobilityFromID);
            packet.writeInt(this.nobilityShield);
            break;
         case HolyUnity:
            Integer fromID = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.HolyUnity, 0);
            if (fromID == 0 || fromID == this.getPlayer().getId()) {
               packet.writeShort(1);
            } else if (!GameConstants.isPaladin(this.getPlayer().getJob())) {
               SecondaryStatEffect e = this.getPlayer().getBuffedEffect(SecondaryStatFlag.HolyUnity);
               if (e != null) {
                  packet.writeShort(0);
               } else {
                  packet.writeShort(1);
               }
            } else {
               packet.writeShort(1);
            }
            break;
         case AutoChargeStackOnOff:
            Object field = this.player.getJobField("autoChargeStackOnOffStack");
            packet.writeInt(field != null ? (Integer)field : 0);
      }
   }

   public List<MapleCharacter> getPlayerInArea(SecondaryStatEffect eff) {
      if (this.getPlayer() != null && this.getPlayer().getMap() != null && eff != null) {
         List<MapleCharacter> playerList = new ArrayList<>();
         playerList.add(this.getPlayer());

         for (MapleCharacter player : this.getPlayer()
            .getMap()
            .getPlayerInRect(this.getPlayer().getTruePosition(), eff.getLt().x, eff.getLt().y, eff.getRb().x, eff.getRb().y)) {
            if (player != null
               && this.getPlayer().getParty() != null
               && player.getParty() != null
               && player.getId() != this.getPlayer().getId()
               && player.getParty().getId() == this.getPlayer().getParty().getId()) {
               playerList.add(player);
            }
         }

         return playerList;
      } else {
         return new ArrayList<>();
      }
   }

   protected void handleSixJobNoDeathTime(int skillId, SecondaryStatEffect effect) {
      if (effect.getNoDeathTime() > 0) {
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePartialNotDamaged, skillId);
         if (eff == null) {
            int noDeathTime = effect.getNoDeathTime();
            if (noDeathTime > 0) {
               this.getPlayer().temporaryStatSet(skillId, noDeathTime, SecondaryStatFlag.indiePartialNotDamaged, 1);
               this.getPlayer().dropMessageGM(5, "무적 시간 적용 : " + noDeathTime);
            }

            eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.SixthSkillFrozen, this.activeSkillID);
            if (eff == null) {
               Skill skil = SkillFactory.getSkill(this.activeSkillID);
               if (skil != null && skil.getScreenTime() > 0) {
                  int screenTime = skil.getScreenTime();
                  this.getPlayer().temporaryStatSet(skillId, screenTime, SecondaryStatFlag.SixthSkillFrozen, 1);
               }
            }

            if (this.getPlayer().getParty() != null) {
               for (MapleCharacter partyMember : this.getPlayer().getPartyMembersSameMap()) {
                  if (!partyMember.getName().equals(this.getPlayer().getName())) {
                     partyMember.send(CField.showOriginSkillPartyEffect(this.getPlayer().getId(), this.getPlayer().getJob(), this.getPlayer().getName()));
                  }
               }
            }
         }
      }
   }

   @Override
   public void setActiveSkillFlag(int flag) {
      this.activeSkillFlag = flag;
   }

   public int getActiveSkillFlag() {
      return this.activeSkillFlag;
   }
}
