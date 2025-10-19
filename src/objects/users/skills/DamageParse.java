package objects.users.skills;

import constants.GameConstants;
import constants.ServerConstants;
import constants.undefinedIDA;
import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import logging.LoggingManager;
import logging.entry.DamageHackLog;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.game.processors.AttackType;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.SpecialSunday;
import objects.effect.child.SkillEffect;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleMonsterStats;
import objects.item.Item;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.MapleStat;
import objects.users.achievement.AchievementFactory;
import objects.users.jobs.BasicJob;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import security.anticheat.CheatingOffense;

public class DamageParse {
   public static void applyAttack(
      AttackInfo attack,
      Skill skill,
      MapleCharacter player,
      int attackCount,
      double maxDamagePerMonster,
      SecondaryStatEffect effect,
      AttackType attackType,
      boolean energy,
      RecvPacketOpcode opcode
   ) {
      if (!player.isAlive()) {
         player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
      } else {
         byte multiKill = 0;
         long totalExp = 0L;
         long totalDamage = 0L;
         boolean boss = false;
         MapleMonster monster = null;
         BasicJob basicJob = player.getPlayerJob();
         basicJob.setTeleportAttackAction(attack.teleportAttackAction);
         if (effect != null) {
            basicJob.prepareAttack(attack, effect, opcode);
         }

         Field map = player.getMap();

         for (AttackPair oned : attack.allDamage) {
            monster = map.getMonsterByOid(oned.objectid);
            if (monster != null && monster.getLinkCID() <= 0) {
               if (monster.getStats().isAllyMob() || monster.getStats().isEscort()) {
                  return;
               }

               if (monster.getStats().isBoss()) {
                  oned.setIsBoss(true);
                  boss = true;
               }

               player.checkSpecialCoreSkills("attackCountMob", monster.getObjectId(), effect);
               MapleMonsterStats monsterStats = monster.getStats();
               long fixedDmg = monsterStats.getFixedDamage();
               long damage = 0L;

               for (Pair<Long, Boolean> eachde : oned.attack) {
                  long eachDamage = eachde.left;
                  if (fixedDmg != -1L) {
                     if (monsterStats.getOnlyNormalAttack()) {
                        eachDamage = attack.skillID != 0 ? 0L : fixedDmg;
                     } else {
                        eachDamage = fixedDmg;
                     }
                  } else if (monsterStats.getOnlyNormalAttack()) {
                     eachDamage = attack.skillID != 0 ? 0L : eachDamage;
                  }

                  if (damage >= 0L && eachDamage > 0L) {
                     long checkDmg = damage + eachDamage;
                     if (checkDmg < 0L) {
                        continue;
                     }
                  }

                  damage += eachDamage;
               }

               if (!DBConfig.isHosting && !DBConfig.isGanglim && player.isGM()) {
                  damage += monster.getMobMaxHp() - 1L;
               }

               basicJob.onAttack(monster, boss, oned, skill, damage, attack, effect, opcode);
               totalDamage += damage;
               if (!monster.isAlive()) {
                  multiKill++;
                  totalExp += monster.getStats().getExp();
                  RuneofPurification(player, monster);
                  player.checkSpecialCoreSkills("attackCount", monster.getObjectId(), effect);
               }
            }
         }

         if (multiKill > 0) {
            AchievementFactory.checkMultiKillCount(player, multiKill);
            addComboCount(monster, player);
         }

         player.checkSpecialCoreSkills("prob", 0, effect);
         if (attack.skillID != 0) {
            player.checkSpecialCoreSkills("cooltime", 0, effect);
         }

         basicJob.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
         player.checkFollow();
      }
   }

   public static final void applyAttackMagic(
      AttackInfo attack, Skill theSkill, MapleCharacter player, SecondaryStatEffect effect, double maxDamagePerHit, RecvPacketOpcode opcode
   ) {
      if (!player.isAlive()) {
         player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
      } else {
         BasicJob basicJob = player.getPlayerJob();
         if (player.getBuffedValue(SecondaryStatFlag.ElementReset) == null) {
            theSkill.getElement();
         }

         if (effect != null) {
            basicJob.prepareAttack(attack, effect, opcode);
         }

         long totalDamage = 0L;
         boolean boss = false;
         Field map = player.getMap();
         int multikill = 0;
         long totalExp = 0L;
         MapleMonster monster = null;
         Skill skill = SkillFactory.getSkill(attack.skillID);

         for (AttackPair oned : attack.allDamage) {
            monster = map.getMonsterByOid(oned.objectid);
            if (monster != null && monster.getLinkCID() <= 0) {
               if (monster.getStats().isBoss()) {
                  oned.setIsBoss(true);
                  boss = true;
               }

               player.checkSpecialCoreSkills("attackCountMob", monster.getObjectId(), effect);
               MapleMonsterStats monsterStats = monster.getStats();
               long fixedDmg = monsterStats.getFixedDamage();
               long damage = 0L;

               for (Pair<Long, Boolean> eachde : oned.attack) {
                  long eachDamage = eachde.left;
                  if (fixedDmg != -1L) {
                     if (monsterStats.getOnlyNormalAttack()) {
                        eachDamage = attack.skillID != 0 ? 0L : fixedDmg;
                     } else {
                        eachDamage = fixedDmg;
                     }
                  } else if (monsterStats.getOnlyNormalAttack()) {
                     eachDamage = attack.skillID != 0 ? 0L : eachDamage;
                  }

                  if (damage >= 0L && eachDamage > 0L) {
                     long checkDmg = damage + eachDamage;
                     if (checkDmg < 0L) {
                        continue;
                     }
                  }

                  damage += eachDamage;
               }

               if (!DBConfig.isHosting && !DBConfig.isGanglim && player.isGM()) {
                  damage += monster.getMobMaxHp() - 1L;
               }

               basicJob.onAttack(monster, boss, oned, skill, damage, attack, effect, opcode);
               totalDamage += damage;
               if (!monster.isAlive()) {
                  multikill++;
                  totalExp += monster.getStats().getExp();
                  player.checkSpecialCoreSkills("attackCount", monster.getObjectId(), effect);
               }
            }
         }

         if (multikill > 0) {
            addComboCount(monster, player);
         }

         player.checkSpecialCoreSkills("prob", 0, effect);
         if (attack.skillID != 0) {
            player.checkSpecialCoreSkills("cooltime", 0, effect);
         }

         basicJob.afterAttack(boss, attack, totalDamage, effect, theSkill, multikill, totalExp, opcode);
      }
   }

   public static void addComboCount(MapleMonster monster, MapleCharacter player) {
      boolean check = true;
      if (check) {
         if (monster != null) {
            if (player.getMonsterCombo() == 0) {
               player.setMonsterComboTime(System.currentTimeMillis());
            }

            if (player.getMonsterComboTime() < System.currentTimeMillis() - 8000L) {
               player.setMonsterCombo((short)0);
            }

            player.addMonsterCombo((short)1);
            AchievementFactory.checkComboKillIncrease(player, player.getMonsterCombo());
            if (player.getMonsterCombo() >= 300
               && GameConstants.isYetiPinkBean(player.getJob())
               && player.getQuestStatus(100567) == 1
               && !player.getOneInfo(100565, "questNum").equals("100566")) {
               player.updateOneInfo(100565, "questNum", "100566");
               player.updateOneInfo(100567, "ComboK", "300");
               player.send(CWvsContext.getShowQuestCompletion(100567));
            }

            if (player.getMonsterCombo() > 1 && monster != null) {
               player.getClient()
                  .getSession()
                  .writeAndFlush(
                     CWvsContext.InfoPacket.comboKill(
                        player.getStylishKillSkin(),
                        Math.min(9999, player.getMonsterCombo()),
                        monster.getObjectId(),
                        monster.giveComboExpToCharacter(player, (int)(monster.getMobExp() / Randomizer.rand(1, 3)))
                     )
                  );
            }

            if (monster != null) {
               player.checkSpecialCoreSkills("combokill", monster.getObjectId(), null);
            }

            if (player.getMonsterCombo() % 50 == 0) {
               Item item = null;
               double expr = 1.0;
               int SLV = player.getSkillLevel(80000370);
               byte d = 1;
               Point pos = new Point(0, monster.getTruePosition().y);
               if (SLV > 0) {
                  SecondaryStatEffect effects = SkillFactory.getSkill(80000370).getEffect(SLV);
                  if (effects != null) {
                     expr = effects.getX() * 0.01;
                  }
               }

               long exp = 0L;
               if (player.getMonsterCombo() >= 50 && player.getMonsterCombo() <= 300) {
                  exp = (long)(monster.getStats().getExp() * 5L * expr);
                  item = new Item(2023484, (short)0, (short)1, 0);
               } else if (player.getMonsterCombo() >= 350 && player.getMonsterCombo() <= 700) {
                  exp = (long)(monster.getStats().getExp() * 7L * expr);
                  item = new Item(2023494, (short)0, (short)1, 0);
               } else if (player.getMonsterCombo() > 700 && player.getMonsterCombo() <= 2000) {
                  exp = (long)(monster.getStats().getExp() * 10L * expr);
                  item = new Item(2023495, (short)0, (short)1, 0);
               } else if (player.getMonsterCombo() > 2000) {
                  exp = (long)(monster.getStats().getExp() * 11L * expr);
                  item = new Item(2023669, (short)0, (short)1, 0);
               }

               if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeCombokillEXP) {
                  exp *= 3L;
               }

               if (exp > 2147483647L) {
                  exp = 2147483647L;
               }

               if (exp < 0L) {
                  exp = 0L;
               }

               pos.x = monster.getTruePosition().x + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               monster.getMap().spawnMobDrop(item, monster.getMap().calcDropPos(pos, monster.getTruePosition()), monster, player, (byte)0, 0, exp);
            }

            player.setMonsterComboTime(System.currentTimeMillis());
         }
      }
   }

   public static final AttackInfo Modify_AttackCrit(AttackInfo attack, MapleCharacter chr, int type, SecondaryStatEffect effect) {
      if (attack.skillID != 4211006 && attack.skillID != 3211003 && attack.skillID != 4111004) {
         int CriticalRate = chr.getStat().passive_sharpeye_rate() + (effect == null ? 0 : effect.getCr());
         boolean shadow = chr.getBuffedValue(SecondaryStatFlag.ShadowPartner) != null && (type == 1 || type == 2);
         List<Long> damages = new ArrayList<>();
         List<Long> damage = new ArrayList<>();
         boolean isCritical = false;

         for (AttackPair p : attack.allDamage) {
            if (p.attack != null) {
               int hit = 0;
               int mid_att = shadow ? p.attack.size() / 2 : p.attack.size();
               int toCrit = attack.skillID != 4221016
                     && attack.skillID != 3221007
                     && attack.skillID != 23121003
                     && attack.skillID != 4341005
                     && attack.skillID != 4331006
                     && attack.skillID != 21120005
                     && attack.skillID != 21121013
                  ? 0
                  : mid_att;
               if (toCrit == 0) {
                  for (Pair<Long, Boolean> eachd : p.attack) {
                     if (!eachd.right && hit < mid_att) {
                        if (eachd.left > 999999L || Randomizer.nextInt(100) < CriticalRate) {
                           toCrit++;
                           isCritical = true;
                        }

                        damage.add(eachd.left);
                     }

                     hit++;
                  }

                  if (toCrit == 0) {
                     damage.clear();
                     continue;
                  }

                  Collections.sort(damage);

                  for (int i = damage.size(); i > damage.size() - toCrit; i--) {
                     damages.add(damage.get(i - 1));
                  }

                  damage.clear();
               }

               hit = 0;

               for (Pair<Long, Boolean> eachd : p.attack) {
                  if (!eachd.right) {
                     if (attack.skillID == 4221016) {
                        eachd.right = hit == 3;
                     } else if (attack.skillID == 3221007
                        || attack.skillID == 23121003
                        || attack.skillID == 21120005
                        || attack.skillID == 21121013
                        || attack.skillID == 4341005
                        || attack.skillID == 4331006
                        || eachd.left > 999999L) {
                        eachd.right = true;
                     } else if (hit >= mid_att) {
                        eachd.right = p.attack.get(hit - mid_att).right;
                     } else {
                        eachd.right = damages.contains(eachd.left);
                     }

                     if (eachd.right) {
                        isCritical = true;
                     }
                  }

                  hit++;
               }

               damages.clear();
            }
         }

         if (isCritical && chr.hasBuffBySkillID(4221054)) {
            int value = chr.getBuffedValue(SecondaryStatFlag.FlipTheCoin);
            SecondaryStatManager statManager = new SecondaryStatManager(chr.getClient(), chr.getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.FlipTheCoin, 4221054, Math.min(6, value + 1));
            statManager.temporaryStatSet();
         }
      }

      return attack;
   }

   public static void RuneofPurification(MapleCharacter player, MapleMonster monster) {
      if (player.hasBuffBySkillID(80002888)) {
         Summoned summon = player.getSummonBySkillID(80002888);
         if (summon != null && player.getSecondaryStat().RuneofPurificationGuage < 1000) {
            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initRuneofPurification(monster.getPosition(), summon.getPosition());
            ForceAtom forceAtom = new ForceAtom(
               info, 80002888, player.getId(), true, false, monster.getObjectId(), ForceAtom.AtomType.ENERGY_BURST, Collections.EMPTY_LIST, 1
            );
            player.getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            SecondaryStatManager statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
            statManager.changeStatValue(SecondaryStatFlag.RuneofPurification, 80002888, 1);
            player.getSecondaryStat().RuneofPurificationGuage = Math.min(1000, player.getSecondaryStat().RuneofPurificationGuage + 10);
            statManager.temporaryStatSet();
            if (player.getSecondaryStat().RuneofPurificationGuage == 1000) {
               statManager = new SecondaryStatManager(player.getClient(), player.getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.RuneofPurification, 80002888, 2);
               int remainTime = (int)(player.getSecondaryStat().getTill(SecondaryStatFlag.RuneofPurification) - System.currentTimeMillis());
               int addTime = 30000 - remainTime;
               statManager.changeTill(SecondaryStatFlag.indieSummon, 80002888, addTime);
               statManager.changeTill(SecondaryStatFlag.RuneofPurification, 80002888, addTime);
               statManager.temporaryStatSet();
            }
         }
      }
   }

   static void attackBonusRecv(PacketDecoder lea, AttackInfo ret) {
      int unk1_1 = lea.readByte();
      lea.readByte();
      ret.slot = (byte)lea.readShort();
      ret.consumeItemID = lea.readInt();
      ret.isUnstableMemorize = lea.readByte() == 1;
      ret.isJumpAttack = lea.readByte() == 1;
      int unk1_5 = lea.readByte();
      int unk1_6_count = lea.readInt();
      ret.attackPosition = new Point(lea.readInt(), lea.readInt());
      lea.skip(4);
      lea.skip(4);
      int unk1_7_count = lea.readInt();

      for (int i = 0; i < unk1_7_count; i++) {
         lea.skip(4);
      }

      ret.isAttackWithDarkSight = lea.readByte() == 1;
      lea.skip(1);
      ret.specialFlag = lea.readInt();
      lea.read(4);
      lea.read(8);
      lea.read(8);
      lea.skip(1);
      int unkSize = lea.readInt();

      for (int i = 0; i < unkSize; i++) {
         lea.readInt();
      }

      lea.readInt();
      lea.readInt();
      lea.readInt();
      lea.readInt();
   }

   public static AttackInfo OnMeleeAttack(PacketDecoder lea, MapleCharacter player, RecvPacketOpcode opcode, boolean retry) {
      AttackInfo ret = new AttackInfo();
      if (ServerConstants.useCriticalDll) {
         ret.dllAttackCount = lea.readInt();

         for (int i = 0; i < ret.dllAttackCount; i++) {
            boolean isCritical = lea.readInt() > 0;
            ret.dllCritical.add(isCritical);
         }
      }

      boolean isBox2DAttack = false;
      if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         isBox2DAttack = lea.readByte() == 1;
      }

      byte specialType = lea.readByte();
      ret.tbyte = lea.readByte();
      ret.targets = (byte)(ret.tbyte >>> 4 & 15);
      ret.hits = (byte)(ret.tbyte & 15);
      ret.skillID = lea.readInt();
      ret.skillLevel = lea.readInt();
      int skillID = ret.skillID;

      try {
         boolean forced = false;
         if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK) {
            ret.bAddAttackProc = lea.readByte() == 1;
         }

         lea.skip(4);
         lea.skip(4);
         attackBonusRecv(lea, ret);
         ret.teleportAttackAction = TeleportAttackAction.fromRemote(lea);
         if (opcode == RecvPacketOpcode.MELEE_ATTACK && GameConstants.sub_140A874C0(skillID)) {
            lea.readInt();
         } else if (skillID == 3341002) {
            lea.readInt();
            lea.readInt();
         } else if (skillID != 3321003
            && skillID != 14000029
            && skillID != 25120115
            && (
               undefinedIDA.isUnknown379(skillID)
                  || skillID == 14110034
                  || skillID == 31241001
                  || skillID == 152141000
                  || skillID == 14110035
                  || GameConstants.isKeydownSkill(skillID)
                  || GameConstants.isSuperNovaSkill(skillID)
            )
            && !retry) {
            ret.keydown = lea.readInt();
         }

         if (GameConstants.isRushBombSkill(skillID)
            || skillID == 5300007
            || skillID == 14101029
            || skillID == 11101030
            || skillID >= 400031003 && skillID <= 400031004
            || skillID == 400031068
            || skillID == 64101008) {
            int damage = lea.readInt();
         }

         if (GameConstants.isZeroSkill(skillID)) {
            ret.tag = lea.readByte() == 1;
         }

         if (GameConstants.isUserCloneSummonedAbleSkill(skillID)) {
            ret.bySummonedID = lea.readInt();
         }

         if (undefinedIDA.isUnknown379(skillID)) {
         }

         if (skillID == 80002823) {
            int unk_400031010 = lea.readInt();
            int var10 = lea.readInt();
         }

         if (skillID == 400041019) {
            ret.shadowSpearPos = new Point(lea.readInt(), lea.readInt());
         }

         ret.isShadowPartner = lea.readByte();
         ret.isBuckShot = lea.readByte();
         ret.display = lea.readShort();
         lea.readInt();
         ret.attacktype = lea.readByte();
         ret.speed = lea.readByte();
         ret.lastAttackTickCount = lea.readInt();
         lea.readInt();
         if (skillID == 0) {
            lea.readByte();
         } else {
            lea.readInt();
            lea.readByte();
         }

         if (skillID == 5111009) {
            lea.readByte();
         }

         if (skillID == 25111005) {
            lea.readInt();
         }

         if ((skillID == 23121011 || skillID == 80001913) && ret.bAddAttackProc) {
            lea.readByte();
         }

         if (GameConstants.isBeyonderSkill(skillID)) {
            lea.readByte();
         }

         ret.allDamage = new ArrayList<>();
         ret.affectedSpawnPos = new ArrayList<>();

         for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            ret.hitAction = lea.readByte();
            ret.lifting = lea.readByte();
            ret.alone = lea.readByte();
            byte fa = lea.readByte();
            byte foreAction = (byte)(fa & 127);
            boolean left = (fa >> 7 & 1) != 0;
            byte frameIndex = lea.readByte();
            int refImgMobID = lea.readInt();
            byte calcDamageStatIndex = (byte)(lea.readByte() & 127);
            Point affectedSpawnPos = new Point(lea.readShort(), lea.readShort());
            new Point(lea.readShort(), lea.readShort());
            ret.affectedSpawnPos.add(affectedSpawnPos);
            if (skillID != 4211006) {
               ret.delay = lea.readShort();
            }

            ret.UNK_Additional_1 = lea.readInt();
            ret.UNK_Additional_2 = lea.readInt();
            lea.skip(1);
            List<Pair<Long, Boolean>> allDamageNumbers = new ArrayList<>();

            for (int j = 0; j < ret.hits; j++) {
               long damage = lea.readLong();
               if (ServerConstants.DEBUG_DAMAGE) {
                  player.dropMessage(5, oid + " : " + ret.skillID + " // " + damage);
               }

               if (ServerConstants.SET_TEST_DAMAGE) {
                  damage = ServerConstants.TEST_DAMAGE;
               }

               long limit = 150000000000L;
               limit = 300000000000000L;
               if (damage > limit) {
                  StringBuilder sb = new StringBuilder();
                  sb.append("데미지 핵 사용 : ");
                  sb.append(player.getName());
                  sb.append(", 스킬ID : ");
                  sb.append(skillID);
                  sb.append(")");
                  LoggingManager.putLog(new DamageHackLog(player, damage, sb));
                  damage = 1L;
               }

               if (damage < 0L) {
                  damage &= 4294967295L;
               }

               allDamageNumbers.add(new Pair<>(damage, false));
            }

            int mobUpDownYRange = lea.readInt();
            if (GameConstants.isSpecialMovingSkill(skillID)) {
               int mid = lea.readInt();
               int x = lea.readShort();
               int y = lea.readShort();
               byte var26 = lea.readByte();
            }

            if (opcode != RecvPacketOpcode.SUMMON_ATTACK) {
               decodeMobDebuff(lea, ret);
            }

            if (skillID == 37111005) {
               boolean var52 = lea.readByte() == 1;
            } else if (skillID == 400021029) {
               byte poisonNovaUNK = lea.readByte();
               lea.readInt();
            }

            decodeMakeAttackInfoPacket(lea, ret, oid, refImgMobID, allDamageNumbers);
         }

         if (lea.available() <= 4L) {
            ret.forcedPos = player.getPosition();
            return ret;
         }

         ret.forcedPos = lea.readPos();
         if (skillID == 61121052 || skillID == 36121052 || undefinedIDA.sub_140A6C0C0(skillID)) {
            lea.readShort();
            lea.readShort();
         }

         if (GameConstants.isSuperNovaSkill(skillID)) {
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 400031016 || skillID == 3221019 || skillID == 400041024 || skillID == 80002452 || undefinedIDA.sub_140A87740(skillID)) {
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 400011132) {
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 101000102 && lea.available() == 4L) {
            lea.readShort();
            lea.readShort();
         }

         if (GameConstants.isSpecialMovingSkill(skillID)) {
            int mid = lea.readInt();
            int x = lea.readShort();
            int y = lea.readShort();
            byte var45 = lea.readByte();
         }

         if (skillID == 21121057) {
            Point var34 = lea.readPos();
         }

         if (skillID == 32111016) {
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 63111010) {
            lea.readInt();
         }

         if (undefinedIDA.AranSkill(skillID) || undefinedIDA.BlasterUnk(skillID) || skillID == 37121004) {
            lea.readByte();
         }

         if (skillID == 21121029 || skillID == 37121052 || undefinedIDA.sub_1409CEA40(skillID) || skillID == 11121014 || undefinedIDA.sub_140A90650(skillID)) {
            lea.readByte();
            lea.readInt();
            lea.readInt();
         }

         if (skillID == 61121105 || skillID == 61121222 || skillID == 24121052) {
            short count = lea.readShort();

            for (int i = 0; i < count; i++) {
               Point pos = lea.readPos();
               if (skillID == 24121052) {
                  SecondaryStatEffect effect = SkillFactory.getSkill(24121052).getEffect(ret.skillLevel);
                  if (effect != null) {
                     Rect rect = new Rect(pos, effect.getLt2(), effect.getRb2(), false);
                     AffectedArea aa = new AffectedArea(rect, player, effect, pos, System.currentTimeMillis() + effect.getDuration());
                     player.getMap().spawnMist(aa);
                  }
               }
            }
         }

         if (skillID == 14111006) {
            Point var36 = lea.readPos();
         }

         if (skillID == 80002686) {
            int count = lea.readInt();

            for (int ix = 0; ix < count; ix++) {
               lea.readInt();
            }
         }

         if (skillID == 101120104 || skillID == 101141003) {
            int count = lea.readShort();

            for (int ix = 0; ix < count; ix++) {
               lea.readShort();
               lea.readShort();
            }
         }
      } catch (Exception var27) {
         System.out.println("DMG Parse Err");
         var27.printStackTrace();
      }

      return ret;
   }

   public static AttackInfo OnShootAttack(PacketDecoder lea, MapleCharacter player, RecvPacketOpcode opcode, boolean retry) {
      AttackInfo ret = new AttackInfo();
      if (ServerConstants.useCriticalDll) {
         ret.dllAttackCount = lea.readInt();

         for (int i = 0; i < ret.dllAttackCount; i++) {
            boolean isCritical = lea.readInt() > 0;
            ret.dllCritical.add(isCritical);
         }
      }

      boolean isBox2DAttack = false;
      if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         isBox2DAttack = lea.readByte() == 1;
      }

      byte specialType = lea.readByte();
      ret.tbyte = lea.readByte();
      ret.targets = (byte)(ret.tbyte >>> 4 & 15);
      ret.hits = (byte)(ret.tbyte & 15);
      ret.skillID = lea.readInt();
      ret.skillLevel = lea.readInt();
      int skillID = ret.skillID;

      try {
         boolean forced = false;
         if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK) {
            ret.bAddAttackProc = lea.readByte() == 1;
         }

         lea.skip(4);
         lea.skip(4);
         attackBonusRecv(lea, ret);
         ret.teleportAttackAction = TeleportAttackAction.fromRemote(lea);
         if (GameConstants.isKeydownSkill(skillID) || GameConstants.isSuperNovaSkill(skillID)) {
            ret.keydown = lea.readInt();
         }

         if (GameConstants.isZeroSkill(skillID)) {
            ret.tag = lea.readByte() == 1;
         }

         if (GameConstants.isUserCloneSummonedAbleSkill(skillID)) {
            ret.bySummonedID = lea.readInt();
         }

         ret.isShadowPartner = lea.readByte();
         ret.isBuckShot = lea.readByte();
         lea.readInt();
         lea.readByte();
         ret.display = lea.readShort();
         lea.readInt();
         ret.attacktype = lea.readByte();
         if (skillID == 36111010) {
            lea.readInt();
            lea.readInt();
            lea.readInt();
         }

         ret.speed = lea.readByte();
         ret.lastAttackTickCount = lea.readInt();
         lea.readInt();
         if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
            lea.readInt();
            lea.readByte();
            lea.readShort();
            lea.readByte();
            new Point(lea.readShort(), lea.readShort());
            new Point(lea.readShort(), lea.readShort());
         }

         ret.allDamage = new ArrayList<>();
         ret.affectedSpawnPos = new ArrayList<>();

         for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            if (ServerConstants.DEBUG_DAMAGE) {
               player.dropMessage(6, opcode.name() + " // " + ret.skillID + " // oid : " + oid);
            }

            ret.hitAction = lea.readByte();
            ret.lifting = lea.readByte();
            ret.alone = lea.readByte();
            byte fa = lea.readByte();
            byte foreAction = (byte)(fa & 127);
            boolean left = (fa >> 7 & 1) != 0;
            byte frameIndex = lea.readByte();
            int refImgMobID = lea.readInt();
            byte calcDamageStatIndex = (byte)(lea.readByte() & 127);
            Point affectedSpawnPos = new Point(lea.readShort(), lea.readShort());
            new Point(lea.readShort(), lea.readShort());
            ret.affectedSpawnPos.add(affectedSpawnPos);
            if (opcode == RecvPacketOpcode.MAGIC_ATTACK) {
               byte mobUpDownYRange = lea.readByte();
            }

            if (skillID != 4211006) {
               ret.delay = lea.readShort();
            }

            ret.UNK_Additional_1 = lea.readInt();
            ret.UNK_Additional_2 = lea.readInt();
            lea.skip(1);
            List<Pair<Long, Boolean>> allDamageNumbers = new ArrayList<>();

            for (int j = 0; j < ret.hits; j++) {
               long damage = lea.readLong();
               if (ServerConstants.DEBUG_DAMAGE) {
                  player.dropMessage(5, oid + " : " + ret.skillID + " // " + damage);
               }

               if (ServerConstants.SET_TEST_DAMAGE) {
                  damage = ServerConstants.TEST_DAMAGE;
               }

               long limit = 150000000000L;
               limit = 300000000000000L;
               if (damage > limit) {
                  StringBuilder sb = new StringBuilder();
                  sb.append("데미지 핵 사용 : ");
                  sb.append(player.getName());
                  sb.append(", 스킬ID : ");
                  sb.append(skillID);
                  sb.append(")");
                  LoggingManager.putLog(new DamageHackLog(player, damage, sb));
                  damage = 1L;
               }

               if (damage < 0L) {
                  damage &= 4294967295L;
               }

               allDamageNumbers.add(new Pair<>(damage, false));
            }

            int mobUpDownYRange = lea.readInt();
            if (GameConstants.isSpecialMovingSkill(skillID)) {
               int mid = lea.readInt();
               int x = lea.readShort();
               int y = lea.readShort();
               byte var26 = lea.readByte();
            }

            if (opcode != RecvPacketOpcode.SUMMON_ATTACK) {
               decodeMobDebuff(lea, ret);
            }

            if (skillID == 37111005) {
               boolean var35 = lea.readByte() == 1;
            } else if (skillID == 400021029) {
               byte poisonNovaUNK = lea.readByte();
               lea.readInt();
            } else if (GameConstants.isKinesisPsychicLockSkill(skillID)) {
               int psychicLockKey = lea.readInt();
               int var38 = lea.readInt();
            }

            decodeMakeAttackInfoPacket(lea, ret, oid, refImgMobID, allDamageNumbers);
         }

         if (lea.available() <= 4L) {
            ret.forcedPos = player.getPosition();
            return ret;
         }

         if (undefinedIDA.AranSkill(skillID) || undefinedIDA.BlasterUnk(skillID) || skillID == 37121004) {
            lea.readByte();
         }

         if (undefinedIDA.KadenaUnk(skillID)) {
            lea.readByte();
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 400011134) {
            lea.readShort();
            lea.readShort();
            lea.readInt();
         } else if (skillID == 23121002 || skillID == 80001914) {
            lea.readByte();
         }

         if (skillID == 80001629) {
            lea.readShort();
            lea.readShort();
         }

         if (opcode == RecvPacketOpcode.SHOOT_ATTACK && GameConstants.isScreenCenterAttackSkill(skillID)) {
            lea.readShort();
            lea.readShort();
         }
      } catch (Exception var27) {
         System.out.println("SDMG Parse Err");
         System.err.println(var27);
      }

      return ret;
   }

   public static final AttackInfo onAttack(PacketDecoder lea, MapleCharacter player, RecvPacketOpcode opcode) {
      return onAttack(lea, player, opcode, false);
   }

   public static final AttackInfo onAttack(PacketDecoder lea, MapleCharacter player, RecvPacketOpcode opcode, boolean retry, boolean test) {
      AttackInfo ret = new AttackInfo();
      if (ServerConstants.useCriticalDll) {
         ret.dllAttackCount = lea.readInt();

         for (int i = 0; i < ret.dllAttackCount; i++) {
            boolean isCritical = lea.readInt() > 0;
            ret.dllCritical.add(isCritical);
         }
      }

      boolean isBox2DAttack = false;
      if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         isBox2DAttack = lea.readByte() == 1;
      }

      byte specialType = lea.readByte();
      ret.tbyte = lea.readByte();
      ret.targets = (byte)(ret.tbyte >>> 4 & 15);
      ret.hits = (byte)(ret.tbyte & 15);
      ret.skillID = lea.readInt();
      ret.skillLevel = lea.readInt();
      int skillID = ret.skillID;

      try {
         boolean forced = false;
         if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK || opcode == RecvPacketOpcode.MELEE_ATTACK || opcode == RecvPacketOpcode.SHOOT_ATTACK) {
            ret.bAddAttackProc = lea.readByte() == 1;
         }

         lea.skip(4);
         lea.skip(4);
         attackBonusRecv(lea, ret);
         ret.teleportAttackAction = TeleportAttackAction.fromRemote(lea);
         if (opcode == RecvPacketOpcode.MELEE_ATTACK && GameConstants.sub_140A874C0(skillID)) {
            lea.readInt();
         } else if ((
               undefinedIDA.isUnknown379(skillID)
                  || GameConstants.isKeydownSkill(skillID)
                  || GameConstants.isSuperNovaSkill(skillID)
                  || skillID == 4221052
                  || skillID == 65121052
                  || skillID >= 14110034 && skillID <= 14110035
                  || skillID == 31241001
                  || skillID == 152141000
            )
            && !retry) {
            ret.keydown = lea.readInt();
         }

         if (GameConstants.isZeroSkill(skillID)) {
            ret.tag = lea.readByte() == 1;
         }

         if (GameConstants.isUserCloneSummonedAbleSkill(skillID)) {
            ret.bySummonedID = lea.readInt();
         }

         if (GameConstants.isRushBombSkill(skillID)
            || skillID == 5300007
            || skillID == 14101029
            || skillID == 11101030
            || skillID >= 400031003 && skillID <= 400031004
            || skillID == 400031068
            || skillID == 64101008) {
            int var33 = lea.readInt();
         }

         if (skillID == 80002823) {
            int unk_400031010 = lea.readInt();
            int unkpos = lea.readInt();
         }

         if (skillID == 400041019) {
            ret.shadowSpearPos = new Point(lea.readInt(), lea.readInt());
         }

         if (GameConstants.isUserCloneSummonedAbleSkill(skillID)) {
            ret.bySummonedID = lea.readInt();
         }

         ret.isShadowPartner = lea.readByte();
         ret.isBuckShot = lea.readByte();
         if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
            lea.readInt();
            lea.readByte();
         }

         ret.display = lea.readShort();
         lea.readInt();
         ret.attacktype = lea.readByte();
         if (skillID == 36111010) {
            lea.skip(4);
            lea.skip(4);
            lea.skip(4);
         }

         ret.speed = lea.readByte();
         ret.lastAttackTickCount = lea.readInt();
         if (skillID != 1200002 && opcode == RecvPacketOpcode.MELEE_ATTACK && ret.lastAttackTickCount != 0) {
            lea.readInt();
            lea.readInt();
         }

         if (skillID == 80001762 || skillID == 80002212 || skillID == 80002463) {
            lea.readInt();
         }

         if (opcode == RecvPacketOpcode.MAGIC_ATTACK && GameConstants.isEvanForceSkill(skillID)) {
            lea.readByte();
         }

         if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
            lea.readInt();
            int finalAtatck = lea.readInt();
            if (finalAtatck > 0) {
               lea.readByte();
            }

            lea.readShort();
            lea.readByte();
            new Point(lea.readShort(), lea.readShort());
            new Point(lea.readShort(), lea.readShort());
         }

         if (skillID == 5111009) {
            lea.readByte();
         }

         if (skillID == 25111005) {
            lea.readInt();
         }

         if ((skillID == 23121011 || skillID == 80001913) && ret.bAddAttackProc) {
            lea.readByte();
         }

         if (GameConstants.isBeyonderSkill(skillID)) {
            lea.readByte();
         }

         ret.allDamage = new ArrayList<>();
         ret.affectedSpawnPos = new ArrayList<>();

         for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            ret.hitAction = lea.readByte();
            ret.lifting = lea.readByte();
            ret.alone = lea.readByte();
            byte fa = lea.readByte();
            byte foreAction = (byte)(fa & 127);
            boolean left = (fa >> 7 & 1) != 0;
            byte frameIndex = lea.readByte();
            int refImgMobID = lea.readInt();
            byte calcDamageStatIndex = (byte)(lea.readByte() & 127);
            Point affectedSpawnPos = new Point(lea.readShort(), lea.readShort());
            new Point(lea.readShort(), lea.readShort());
            ret.affectedSpawnPos.add(affectedSpawnPos);
            if (opcode == RecvPacketOpcode.MAGIC_ATTACK) {
               byte mobUpDownYRange = lea.readByte();
            }

            if (skillID != 4211006) {
               ret.delay = lea.readShort();
            }

            ret.UNK_Additional_1 = lea.readInt();
            ret.UNK_Additional_2 = lea.readInt();
            lea.skip(1);
            List<Pair<Long, Boolean>> allDamageNumbers = new ArrayList<>();

            for (int j = 0; j < ret.hits; j++) {
               long damage = lea.readLong();
               if (ServerConstants.DEBUG_DAMAGE) {
                  player.dropMessage(5, oid + " : " + ret.skillID + " // " + damage);
               }

               if (ServerConstants.SET_TEST_DAMAGE) {
                  damage = ServerConstants.TEST_DAMAGE;
               }

               long limit = 150000000000L;
               limit = 300000000000000L;
               if (damage > limit) {
                  StringBuilder sb = new StringBuilder();
                  sb.append("데미지 핵 사용 : ");
                  sb.append(player.getName());
                  sb.append(", 스킬ID : ");
                  sb.append(skillID);
                  sb.append(")");
                  LoggingManager.putLog(new DamageHackLog(player, damage, sb));
                  damage = 1L;
               }

               if (damage < 0L) {
                  damage &= 4294967295L;
               }

               allDamageNumbers.add(new Pair<>(damage, false));
            }

            int mobUpDownYRange = lea.readInt();
            if (GameConstants.isSpecialMovingSkill(skillID)) {
               int mid = lea.readInt();
               int x = lea.readShort();
               int y = lea.readShort();
               byte var27 = lea.readByte();
            }

            if (opcode != RecvPacketOpcode.SUMMON_ATTACK) {
               decodeMobDebuff(lea, ret);
            }

            if (skillID == 37111005) {
               boolean var74 = lea.readByte() == 1;
            } else if (skillID == 400021029) {
               byte poisonNovaUNK = lea.readByte();
               lea.readInt();
            } else if (GameConstants.isKinesisPsychicLockSkill(skillID)) {
               int psychicLockKey = lea.readInt();
               int var77 = lea.readInt();
            }

            decodeMakeAttackInfoPacket(lea, ret, oid, refImgMobID, allDamageNumbers);
         }

         if (lea.available() <= 4L) {
            ret.forcedPos = player.getPosition();
            return ret;
         }

         if (opcode == RecvPacketOpcode.SHOOT_ATTACK && GameConstants.isScreenCenterAttackSkill(skillID)) {
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 151121041) {
            lea.readByte();
            lea.readByte();
         }

         ret.forcedPos = lea.readPos();
         if (GameConstants.isUnkSkill1(skillID) || GameConstants.isUnkSkill2(skillID) || skillID == 37121004) {
            lea.skip(1);
         }

         if (GameConstants.sub_140A5FC90(skillID)) {
            sub_14099E850(lea, ret);
         }

         if (skillID == 80001629) {
            lea.readShort();
            lea.readShort();
         }

         if (GameConstants.isSpecialMovingSkill(skillID)) {
            int mid = lea.readInt();
            int x = lea.readShort();
            int y = lea.readShort();
            byte var64 = lea.readByte();
         }

         if (skillID == 101120104 || skillID == 101141003) {
            int count = lea.readShort();

            for (int i = 0; i < count; i++) {
               lea.readShort();
               lea.readShort();
            }
         }

         if (skillID == 101000102 && lea.available() == 4L) {
            lea.readShort();
            lea.readShort();
         }

         if (opcode == RecvPacketOpcode.AREA_DOT_ATTACK && lea.available() >= 4L) {
            lea.readInt();
         }

         if (GameConstants.isKinesisPsychicTornadoSkill(skillID)) {
            lea.readInt();
            lea.readShort();
         }

         if (GameConstants.isKinesisPsychicLockSkill(skillID)) {
            lea.readByte();
         }

         if (GameConstants.isThrowingBombSkill(skillID)) {
            int bombID = lea.readInt();
            Point hitPos = lea.readPos();
            byte bombD = lea.readByte();
            ret.bombID = bombID;
            ret.bombHitPos = hitPos;
            ret.bombD = bombD;
         }

         if (skillID == 21121057) {
            Point var40 = lea.readPos();
         }

         if (skillID == 32111016) {
            lea.readShort();
            lea.readShort();
         }

         if (skillID == 63111010) {
            lea.readInt();
         }

         if (ret.skillID == 21120019
            || ret.skillID == 37121052
            || ret.skillID >= 400041002 && ret.skillID <= 400041005
            || ret.skillID >= 500061025 && ret.skillID <= 500061028
            || ret.skillID == 11121014
            || ret.skillID == 5101004) {
            ret.unk2 = lea.readByte();
            ret.position2 = new Point(lea.readInt(), lea.readInt());
         }

         if (skillID == 61121105 || skillID == 61121222 || skillID == 24121052) {
            short count = lea.readShort();

            for (int i = 0; i < count; i++) {
               Point pos = lea.readPos();
               if (skillID == 24121052) {
                  SecondaryStatEffect effect = SkillFactory.getSkill(24121052).getEffect(ret.skillLevel);
                  if (effect != null) {
                     Rect rect = new Rect(pos, effect.getLt2(), effect.getRb2(), false);
                     AffectedArea aa = new AffectedArea(rect, player, effect, pos, System.currentTimeMillis() + effect.getDuration());
                     player.getMap().spawnMist(aa);
                  }
               }
            }
         }

         if (skillID == 101120104 && lea.available() >= 2L) {
            short count = lea.readShort();

            for (int ix = 0; ix < count; ix++) {
               if (lea.available() >= 4L) {
                  Point var61 = lea.readPos();
               }
            }
         }

         if (skillID == 14111006) {
            Point var43 = lea.readPos();
         }

         if (skillID == 80002686) {
            int count = lea.readInt();

            for (int ixx = 0; ixx < count; ixx++) {
               lea.readInt();
            }
         }

         if (GameConstants.isChainArtsChase2(skillID)) {
            byte sb = lea.readByte();
            short sX = lea.readShort();
            short sY = lea.readShort();
            PacketEncoder packet = new PacketEncoder();
            packet.write((ret.display & 32768) != 0);
            packet.writeInt((int)sb);
            packet.writeInt((int)sX);
            packet.writeInt((int)sY);
            ret.unk2 = sb;
            ret.position2 = new Point(sX, sY);
            SkillEffect e = new SkillEffect(player.getId(), player.getLevel(), ret.skillID, ret.skillLevel, packet);
            player.send(e.encodeForLocal());
            player.getMap().broadcastMessage(player, e.encodeForRemote(), false);
         }

         if (skillID == 400011134) {
            lea.skip(2);
            lea.skip(2);
            lea.skip(4);
         }

         if (skillID == 23121002 || skillID == 80001914) {
            lea.skip(1);
         }

         if (opcode == RecvPacketOpcode.MAGIC_ATTACK && skillID != 32111016 && skillID != 12120023) {
            boolean isDragonAttack = lea.readByte() == 1;
            if (isDragonAttack) {
               ret.dragonPos = lea.readPos();
               lea.readShort();
               lea.readShort();
               ret.dragonShowSkillEffect = lea.readByte() == 1;
               ret.dragonAttackAction = lea.readByte();
               ret.dragonShowAttackAction = lea.readByte() == 1;
            }

            ret.dragonAttack = isDragonAttack;
         }

         if (opcode == RecvPacketOpcode.SHOOT_ATTACK && GameConstants.isSwiftOfWindSkill(skillID)) {
            ret.dragonShowSkillEffect = lea.readByte() == 1;
            ret.dragonAttackAction = lea.readByte();
            ret.dragonAttack = true;
         }

         if (skillID == 12100029) {
            lea.skip(4);
         } else if (skillID == 2111003 || skillID == 80001835) {
            if (skillID == 2111003) {
               forced = lea.readByte() == 1;
               ret.position2 = new Point();
               ret.position2.x = lea.readShort();
            } else if (skillID == 80001835) {
               byte count = lea.readByte();

               for (int ixx = 0; ixx < count; ixx++) {
                  lea.skip(4);
                  lea.skip(2);
               }
            }

            ret.position2.y = lea.readShort();
         }

         if (skillID == 2121003) {
            int unk_count = lea.readByte();

            for (int ixx = 0; ixx < unk_count; ixx++) {
               int var63 = lea.readInt();
            }
         }

         if (lea.available() > 0L && (ret.flag & 32) > 0 && lea.available() >= 8L) {
            ret.targetID = lea.readInt();
            ret.targetPosition = lea.readPos();
         }
      } catch (Exception var28) {
         long pos = lea.getPosition();
         lea.seek(0L);
         FileoutputUtil.outputFileErrorReason("Log_DamageParseError.rtf", ret.skillID + " // Damage Parse Error " + lea.toString(), var28);
         lea.seek(pos);
      }

      return ret;
   }

   public static final AttackInfo onAttack(PacketDecoder lea, MapleCharacter player, RecvPacketOpcode opcode, boolean retry) {
      AttackInfo ret = new AttackInfo();
      if (ServerConstants.useCriticalDll) {
         ret.dllAttackCount = lea.readInt();

         for (int i = 0; i < ret.dllAttackCount; i++) {
            boolean isCritical = lea.readInt() > 0;
            ret.dllCritical.add(isCritical);
         }
      }

      if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK) {
         ret.skillID = lea.readInt();
         lea.skip(16);
      }

      boolean isBox2DAttack = false;
      if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         isBox2DAttack = lea.readByte() == 1;
      }

      lea.skip(1);
      ret.tbyte = lea.readByte();
      ret.targets = (byte)(ret.tbyte >>> 4 & 15);
      ret.hits = (byte)(ret.tbyte & 15);
      ret.skillID = lea.readInt();
      ret.skillLevel = lea.readInt();
      int skillID = ret.skillID;
      int mastery = 0;
      boolean forced = false;
      if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK || opcode == RecvPacketOpcode.MELEE_ATTACK || opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         ret.bAddAttackProc = lea.readByte() == 1;
      }

      lea.skip(4);
      lea.skip(4);
      int bulletItemID = 0;
      int bulletItemPos = 0;
      int bulletCashItemPos = 0;
      int bulletCashItemID = 0;
      int shootRange = 0;
      int rushBomb = 0;
      attackBonusRecv(lea, ret);
      ret.teleportAttackAction = TeleportAttackAction.fromRemote(lea);
      if (opcode == RecvPacketOpcode.MELEE_ATTACK && GameConstants.sub_140A874C0(skillID)) {
         lea.readInt();
      } else if (skillID == 3341002) {
         lea.readInt();
         lea.readInt();
      } else if (skillID != 3321003 && skillID != 14000029 && skillID != 25120115 && skillID != 35110017) {
         if ((
               undefinedIDA.isUnknown379(skillID)
                  || skillID == 14110034
                  || skillID == 31241001
                  || skillID == 152141000
                  || skillID == 14110035
                  || GameConstants.isKeydownSkill(skillID)
                  || GameConstants.isSuperNovaSkill(skillID)
            )
            && !retry) {
            ret.keydown = lea.readInt();
         }

         if (GameConstants.isRushBombSkill(skillID)
            || skillID == 5300007
            || skillID == 14101029
            || skillID == 11101030
            || skillID == 400031003
            || skillID == 400031004
            || skillID == 400031068
            || skillID == 64101008) {
            rushBomb = lea.readInt();
         }
      }

      if (opcode == RecvPacketOpcode.MAGIC_ATTACK && skillID == 400021062) {
         lea.readInt();
      }

      if (GameConstants.isZeroSkill(skillID)) {
         ret.tag = lea.readByte() == 1;
      }

      if (GameConstants.isUserCloneSummonedAbleSkill(skillID)) {
         ret.bySummonedID = lea.readInt();
      }

      if (skillID == 4361501) {
         lea.readInt();
      }

      if (skillID == 400031010 || skillID == 80002823) {
         int unk_400031010 = lea.readInt();
         int lvBuckShot = lea.readInt();
      }

      if (skillID == 400041019) {
         ret.shadowSpearPos = new Point(lea.readInt(), lea.readInt());
      }

      switch (skillID) {
         case 11111130:
         case 11111230:
         case 131001000:
         case 131001001:
         case 131001002:
         case 131001003:
         case 131001004:
         case 131001010:
         case 131001011:
            lea.readInt();
            break;
         case 12121056:
         case 12121057:
         case 12121058:
         case 12121059:
            lea.readInt();
            lea.readInt();
      }

      int rBuckShot = 0;
      int lvBuckShot = 0;
      if (player.getBuffedValue(SecondaryStatFlag.BuckShot) != null) {
         rBuckShot = player.getSecondaryStatReason(SecondaryStatFlag.BuckShot);
         lvBuckShot = player.getTotalSkillLevel(rBuckShot);
      }

      AttackOption attackOption = AttackOption.getOption(lea.readByte());
      AttackOption2 option2 = AttackOption2.getOption(lea.readByte());
      if (skillID == 101001100 || skillID == 101000101) {
         lea.readInt();
      }

      if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
         ret.consumeItemID = lea.readInt();
         ret.AOE = lea.readByte();
      }

      if (isBox2DAttack) {
         int box2d_0 = lea.readInt();
         short box2d_1 = lea.readShort();
         short partyCount = lea.readShort();
      }

      if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK && skillID != 155111207) {
         boolean skipSkill = true;
         if (skillID > 12120020) {
            if (skillID > 12141002) {
               if (skillID != 12141004 && skillID != 12141005) {
                  skipSkill = false;
               }
            } else if (skillID != 12141002 && skillID != 12121057 && skillID != 12121059 && skillID != 12141001) {
               skipSkill = false;
            }
         } else if (skillID != 12120020) {
            if (skillID > 12110030) {
               if (skillID != 12120010 && skillID != 12120017 && skillID != 12120019) {
                  skipSkill = false;
               }
            } else if (skillID != 12110030 && skillID != 12000026 && skillID != 12100028 && skillID != 12110028) {
               skipSkill = false;
            }
         }

         if (skipSkill) {
            lea.skip(4);
         }

         if (skillID >= 12141000 && skillID <= 12141005) {
            lea.skip(4);
         }
      }

      ret.display = lea.readShort();
      lea.skip(4);
      int attackType = lea.readByte();
      if (opcode == RecvPacketOpcode.SHOOT_ATTACK && (skillID == 80001915 || skillID == 36111010)) {
         lea.skip(4);
         lea.skip(4);
         lea.skip(4);
      }

      int td = 0;
      if (GameConstants.isEvanForceSkill(skillID)) {
         int var57 = lea.readByte();
      }

      ret.speed = lea.readByte();
      int partyCount = ret.speed >> 4;
      int speedDegree = ret.speed & 15;
      int tick = lea.readInt();
      if (skillID == 33000036) {
         int chillingObjectID = lea.readInt();
      }

      int chillingObjectID = 0;
      if (opcode == RecvPacketOpcode.AREA_DOT_ATTACK) {
         chillingObjectID = lea.readInt();
      }

      if (opcode == RecvPacketOpcode.NON_TARGET_FORCE_ATOM_ATTACK) {
         lea.skip(4);
      }

      if (skillID == 80001762 || skillID == 400011133) {
         lea.skip(4);
      }

      if (skillID != 400031010) {
         lea.skip(4);
         if (opcode == RecvPacketOpcode.MELEE_ATTACK || opcode == RecvPacketOpcode.SHOOT_ATTACK) {
            ret.finalAttackActiveSkillID = lea.readInt();
            if (skillID != 0 && ret.finalAttackActiveSkillID > 0) {
               ret.finalAttackActiveSkillLevel = lea.readByte();
            }

            if (skillID == 5111009) {
               if (lea.readByte() == 1) {
                  SecondaryStatEffect eff = player.getSkillLevelData(5111009);
                  if (eff != null) {
                     int cool = eff.getCooldown(player);
                     player.giveCoolDowns(5111009, System.currentTimeMillis(), cool);
                     player.send(CField.skillCooldown(5111009, cool));
                  }
               }
            } else if (skillID == 25111005) {
               int damage = lea.readInt();
            } else if ((skillID == 23121011 || skillID == 80001913) && ret.bAddAttackProc) {
               lea.skip(1);
            }
         }

         if (opcode == RecvPacketOpcode.SHOOT_ATTACK) {
            ret.bulletCashItemPos = (byte)lea.readShort();
            ret.AOE = lea.readByte();
            lea.skip(2);
            lea.skip(2);
            lea.skip(2);
            lea.skip(2);
         }

         if (GameConstants.isBeyonderSkill(skillID)) {
            lea.skip(1);
         }
      }

      ret.allDamage = new ArrayList<>();
      ret.affectedSpawnPos = new ArrayList<>();

      for (int i = 0; i < ret.targets; i++) {
         int oid = lea.readInt();
         if (ServerConstants.DEBUG_DAMAGE) {
            player.dropMessage(6, opcode.name() + " // " + ret.skillID + " // oid : " + oid);
         }

         byte hitAction = lea.readByte();
         byte lifting = lea.readByte();
         byte alone = lea.readByte();
         ret.hitAction = hitAction;
         ret.lifting = lifting;
         ret.alone = alone;
         byte fa = lea.readByte();
         byte foreAction = (byte)(fa & 127);
         boolean left = (fa >> 7 & 1) != 0;
         byte frameIndex = lea.readByte();
         int refImgMobID = lea.readInt();
         byte calcDamageStatIndex = (byte)(lea.readByte() & 127);
         Point affectedSpawnPos = new Point(lea.readShort(), lea.readShort());
         new Point(lea.readShort(), lea.readShort());
         ret.affectedSpawnPos.add(affectedSpawnPos);
         if (opcode == RecvPacketOpcode.MAGIC_ATTACK) {
            byte mobUpDownYRange = lea.readByte();
         }

         List<Pair<Long, Boolean>> allDamageNumbers;
         if (skillID == 80001835) {
            lea.skip(1);
            allDamageNumbers = new ArrayList<>();

            for (int j = 0; j < ret.hits; j++) {
               long damage = lea.readLong();
               if (ServerConstants.DEBUG_DAMAGE) {
                  player.dropMessage(5, oid + " : " + ret.skillID + " // " + damage);
               }

               if (ServerConstants.SET_TEST_DAMAGE) {
                  damage = ServerConstants.TEST_DAMAGE;
               }

               long limit = 150000000000L;
               limit = 300000000000000L;
               if (damage > limit) {
                  StringBuilder sb = new StringBuilder();
                  sb.append("데미지 핵 사용 : ");
                  sb.append(player.getName());
                  sb.append(", 스킬ID : ");
                  sb.append(skillID);
                  sb.append(")");
                  LoggingManager.putLog(new DamageHackLog(player, damage, sb));
                  damage = 1L;
               }

               if (damage < 0L) {
                  damage &= 4294967295L;
               }

               allDamageNumbers.add(new Pair<>(damage, false));
            }
         } else {
            if (skillID != 4211006) {
               ret.delay = lea.readShort();
            }

            ret.UNK_Additional_1 = lea.readInt();
            ret.UNK_Additional_2 = lea.readInt();
            lea.skip(1);
            allDamageNumbers = new ArrayList<>();

            for (int j = 0; j < ret.hits; j++) {
               long damagex = lea.readLong();
               if (ServerConstants.DEBUG_DAMAGE) {
                  if (ret.skillID == 23100006) {
                     System.out.print("");
                  }

                  player.dropMessage(5, oid + " : " + ret.skillID + " // " + damagex);
               }

               if (ServerConstants.SET_TEST_DAMAGE) {
                  damagex = ServerConstants.TEST_DAMAGE;
               }

               long limitx = 150000000000L;
               limitx = 300000000000000L;
               if (damagex > limitx) {
                  StringBuilder sb = new StringBuilder();
                  sb.append("데미지 핵 사용 : ");
                  sb.append(player.getName());
                  sb.append(", 스킬ID : ");
                  sb.append(skillID);
                  sb.append(")");
                  LoggingManager.putLog(new DamageHackLog(player, damagex, sb));
                  damagex = 1L;
               }

               if (damagex < 0L) {
                  damagex &= 4294967295L;
               }

               allDamageNumbers.add(new Pair<>(damagex, false));
            }
         }

         int mobUpDownYRange = lea.readInt();
         if (opcode != RecvPacketOpcode.SUMMON_ATTACK) {
            decodeMobDebuff(lea, ret);
         }

         if (skillID == 37111005) {
            boolean var103 = lea.readByte() == 1;
         } else if (skillID == 400021029) {
            byte poisonNovaUNK = lea.readByte();
            lea.readInt();
         } else if (GameConstants.isKinesisPsychicLockSkill(skillID)) {
            int psychicLockKey = lea.readInt();
            int var43 = lea.readInt();
         }

         decodeMakeAttackInfoPacket(lea, ret, oid, refImgMobID, allDamageNumbers);
      }

      if (lea.available() < 4L) {
         ret.forcedPos = player.getPosition();
         return ret;
      } else {
         if (opcode == RecvPacketOpcode.SHOOT_ATTACK && GameConstants.isScreenCenterAttackSkill(skillID)) {
            lea.readShort();
            lea.readShort();
         }

         try {
            if (skillID == 151121041) {
               lea.readByte();
               lea.readByte();
            }

            ret.forcedPos = lea.readPos();
            if (GameConstants.isUnkSkill1(skillID) || GameConstants.isUnkSkill2(skillID) || skillID == 37121004) {
               lea.skip(1);
            }

            if (GameConstants.sub_140A5FC90(skillID)) {
               sub_14099E850(lea, ret);
            }

            if (skillID == 80001629) {
               lea.readShort();
               lea.readShort();
            }

            if (GameConstants.isSpecialMovingSkill(skillID)) {
               int mid = lea.readInt();
               int x = lea.readShort();
               int y = lea.readShort();
               byte var91 = lea.readByte();
            }

            if (skillID == 101120104 || skillID == 101141003) {
               int count = lea.readShort();

               for (int i = 0; i < count; i++) {
                  lea.readShort();
                  lea.readShort();
               }
            }

            if (skillID == 101000102 && lea.available() == 4L) {
               lea.readShort();
               lea.readShort();
            }

            if (opcode == RecvPacketOpcode.AREA_DOT_ATTACK && lea.available() >= 4L) {
               lea.readInt();
            }

            if (GameConstants.isKinesisPsychicTornadoSkill(skillID)) {
               lea.readInt();
               lea.readShort();
            }

            if (GameConstants.isKinesisPsychicLockSkill(skillID)) {
               lea.readByte();
            }

            if (GameConstants.isThrowingBombSkill(skillID)) {
               int bombID = lea.readInt();
               Point hitPos = lea.readPos();
               byte bombD = lea.readByte();
               ret.bombID = bombID;
               ret.bombHitPos = hitPos;
               ret.bombD = bombD;
            }

            if (skillID == 21121057) {
               Point var67 = lea.readPos();
            }

            if (skillID == 32111016) {
               lea.readShort();
               lea.readShort();
            }

            if (skillID == 63111010) {
               lea.readInt();
            }

            if (ret.skillID == 21120019
               || ret.skillID == 37121052
               || ret.skillID >= 400041002 && ret.skillID <= 400041005
               || ret.skillID >= 500061025 && ret.skillID <= 500061028
               || ret.skillID == 11121014
               || ret.skillID == 5101004) {
               ret.unk2 = lea.readByte();
               ret.position2 = new Point(lea.readInt(), lea.readInt());
            }

            if (skillID == 61121105 || skillID == 61121222 || skillID == 24121052) {
               short count = lea.readShort();

               for (int i = 0; i < count; i++) {
                  Point pos = lea.readPos();
                  if (skillID == 24121052) {
                     SecondaryStatEffect effect = SkillFactory.getSkill(24121052).getEffect(ret.skillLevel);
                     if (effect != null) {
                        Rect rect = new Rect(pos, effect.getLt2(), effect.getRb2(), false);
                        AffectedArea aa = new AffectedArea(rect, player, effect, pos, System.currentTimeMillis() + effect.getDuration());
                        player.getMap().spawnMist(aa);
                     }
                  }
               }
            }

            if (skillID == 101120104 && lea.available() >= 2L) {
               short count = lea.readShort();

               for (int ix = 0; ix < count; ix++) {
                  if (lea.available() >= 4L) {
                     Point var88 = lea.readPos();
                  }
               }
            }

            if (skillID == 14111006) {
               Point var70 = lea.readPos();
            }

            if (skillID == 80002686) {
               int count = lea.readInt();

               for (int ixx = 0; ixx < count; ixx++) {
                  lea.readInt();
               }
            }

            if (GameConstants.isChainArtsChase2(skillID)) {
               byte sb = lea.readByte();
               short sX = lea.readShort();
               short sY = lea.readShort();
               PacketEncoder packet = new PacketEncoder();
               packet.write((ret.display & 32768) != 0);
               packet.writeInt((int)sb);
               packet.writeInt((int)sX);
               packet.writeInt((int)sY);
               ret.unk2 = sb;
               ret.position2 = new Point(sX, sY);
               SkillEffect e = new SkillEffect(player.getId(), player.getLevel(), ret.skillID, ret.skillLevel, packet);
               player.send(e.encodeForLocal());
               player.getMap().broadcastMessage(player, e.encodeForRemote(), false);
            }

            if (skillID == 400011134) {
               lea.skip(2);
               lea.skip(2);
               lea.skip(4);
            }

            if (skillID == 23121002 || skillID == 80001914) {
               lea.skip(1);
            }

            if (opcode == RecvPacketOpcode.MAGIC_ATTACK && skillID != 32111016 && skillID != 12120023 && GameConstants.isEvan(player.getJob())) {
               boolean isDragonAttack = lea.readByte() == 1;
               if (isDragonAttack) {
                  ret.dragonPos = lea.readPos();
                  lea.readShort();
                  lea.readShort();
                  ret.dragonShowSkillEffect = lea.readByte() == 1;
                  ret.dragonAttackAction = lea.readByte();
                  ret.dragonShowAttackAction = lea.readByte() == 1;
               }

               ret.dragonAttack = isDragonAttack;
            }

            if (opcode == RecvPacketOpcode.SHOOT_ATTACK && GameConstants.isSwiftOfWindSkill(skillID)) {
               ret.dragonShowSkillEffect = lea.readByte() == 1;
               ret.dragonAttackAction = lea.readByte();
               ret.dragonAttack = true;
            }

            if (skillID == 12100029) {
               lea.skip(4);
            } else if (skillID == 2111003 || skillID == 80001835) {
               if (skillID == 2111003) {
                  lea.skip(1);
                  forced = lea.readByte() == 1;
                  ret.position2 = new Point();
                  ret.position2.x = lea.readShort();
               } else if (skillID == 80001835) {
                  byte count = lea.readByte();

                  for (int ixx = 0; ixx < count; ixx++) {
                     lea.skip(4);
                     lea.skip(2);
                  }
               }

               ret.position2.y = lea.readShort();
            }

            if (skillID == 2121003) {
               int unk_count = lea.readByte();

               for (int ixx = 0; ixx < unk_count; ixx++) {
                  int var90 = lea.readInt();
               }
            }

            if (GameConstants.isRWChargingSkill(ret.skillID) && lea.available() == 1L) {
               ret.canRWCharge = lea.readByte();
            }

            if (lea.available() > 0L && (ret.flag & 32) > 0 && lea.available() >= 8L) {
               ret.targetID = lea.readInt();
               ret.targetPosition = lea.readPos();
            }
         } catch (Exception var45) {
            FileoutputUtil.outputFileErrorReason("Log_DamageParseError.rtf", ret.skillID + " 공격 데미지 추가 정보 파싱 오류 : " + lea.toString(true), var45);
         }

         return ret;
      }
   }

   static void sub_14099E850(PacketDecoder lea, AttackInfo ret) {
      lea.skip(4);
      lea.skip(4);
      lea.skip(8);
      lea.skip(4);
      lea.skip(4);
      lea.skip(4);
      lea.skip(4);
      int cnt = lea.readInt();

      for (int i = 0; i < cnt; i++) {
         lea.readInt();
      }

      lea.skip(16);
   }

   static void decodeMobDebuff(PacketDecoder lea, AttackInfo ret) {
      lea.readInt();
      lea.readInt();
   }

   static void decodeMakeAttackInfoPacket(PacketDecoder lea, AttackInfo ret, int oid, int refImgMobID, List<Pair<Long, Boolean>> allDamageNumbers) {
      int hitType = lea.readByte();
      if (hitType == 1 || hitType == 2) {
         String animationName = lea.readMapleAsciiString();
         int animationDeltaL = lea.readInt();
         if (hitType == 1) {
            int hitParts = lea.readInt();

            for (int next = 0; next < hitParts; next++) {
               String var10 = lea.readMapleAsciiString();
            }
         }
      }

      lea.readByte();
      lea.skip(2);
      lea.skip(2);
      Point mobPos = lea.readPos();
      lea.skip(2);
      lea.skip(2);
      AttackPair ap = new AttackPair(oid, mobPos, allDamageNumbers);
      ap.setRefImgId(refImgMobID);
      ret.allDamage.add(ap);
      lea.skip(1);
      lea.skip(1);
      lea.skip(4);
      lea.skip(4);
      lea.skip(4);
      int unk = lea.readInt();

      for (int unkl = 0; unkl < unk; unkl++) {
         lea.skip(4);
         lea.skip(4);
      }
   }

   public static void doHideAndSeek(MapleCharacter player, AttackInfo attack, boolean catched) {
      for (MapleCharacter attackedPlayers : player.getMap()
         .getNearestPvpChar(player.getPosition(), 91.0, 40.0, attack.display >= 256, Collections.unmodifiableCollection(player.getMap().getCharacters()))) {
         if (attackedPlayers.isAlive() && attackedPlayers.isCatched && player.isCatching) {
            CatchPlayer(player, attackedPlayers);
         }
      }
   }

   public static void CatchPlayer(MapleCharacter player, MapleCharacter catched) {
      player.getMap().broadcastMessage(CField.harvestResult(player.getId(), true));
      player.getMap().broadcastMessage(CWvsContext.serverNotice(6, "[술래잡기 알림] 늑대 " + player.getName() + "님이 양 " + catched.getName() + "님을 잡으셨습니다."));
      catched.getStat().setHp(0L, catched);
      catched.updateSingleStat(MapleStat.HP, 0L);
      player.addSheepScore();
      player.getMap().broadcastMessage(CField.farmScore(player.getWolfScore(), player.getSheepScore()));
      boolean alliveCatched = false;

      for (MapleCharacter chr : player.getMap().getCharacters()) {
         if (chr.isAlive() && chr.isCatched) {
            alliveCatched = true;
            break;
         }
      }

      if (!alliveCatched) {
         player.getMap().stopCatch();

         for (MapleCharacter chrx : player.getMap().getCharacters()) {
            chrx.getStat().setHp(chrx.getStat().getCurrentMaxHp(chrx), chrx);
            chrx.updateSingleStat(MapleStat.HP, chrx.getStat().getHp());
            if (chrx.isCatching) {
               chrx.changeMap(
                  chrx.getClient().getChannelServer().getMapFactory().getMap(910040003),
                  chrx.getClient().getChannelServer().getMapFactory().getMap(910040003).getPortalSP().get(0)
               );
               chrx.isWolfShipWin = true;
            } else {
               chrx.changeMap(
                  chrx.getClient().getChannelServer().getMapFactory().getMap(910040006),
                  chrx.getClient().getChannelServer().getMapFactory().getMap(910040006).getPortalSP().get(0)
               );
               chrx.isWolfShipWin = false;
            }
         }

         player.getMap().broadcastMessage(CWvsContext.serverNotice(1, "[술래잡기 알림]\r\n양들이 모두 잡혀서 늑대가 승리하였습니다!\r\n모든 분들은 게임 보상맵으로 이동 됩니다."));
      }
   }
}
