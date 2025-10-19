package objects.users;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.game.processors.AttackInfo;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;

public class CalcDamage {
   CRand32 rndGenForCharacter;
   long successcount = 0L;
   long failcount = 0L;
   private int numRand = 11;

   public CalcDamage() {
      this.rndGenForCharacter = new CRand32();
   }

   public void SetSeed(int seed1, int seed2, int seed3) {
      this.rndGenForCharacter.Seed(seed1, seed2, seed3);
   }

   public double getRand(long nRand, long f0, long f1) {
      long rand = nRand & 4294967295L;
      if (f0 > f1) {
         return f1 + (double)(rand % 10000000L) * (f0 - f1) / 9999999.0;
      } else {
         return f0 != f1 ? f0 + (double)(rand % 10000000L) * (f1 - f0) / 9999999.0 : f0;
      }
   }

   public boolean is_ignore_pimune(int skillID, MapleCharacter player) {
      Integer value = player.getBuffedValue(SecondaryStatFlag.Morph);
      if (value != null) {
         switch (value) {
            case 61111008:
            case 61120008:
            case 61121053:
               return true;
         }
      }

      if (player.getBuffedValue(SecondaryStatFlag.Reincarnation) != null) {
         return true;
      } else {
         return player.getBuffedValue(SecondaryStatFlag.IgnorePImmune) != null
            ? true
            : this.is_equalibrium_skill(skillID) || this.is_royal_guard_skill(skillID);
      }
   }

   public boolean is_equalibrium_skill(int skillID) {
      return skillID >= 20040219 && skillID <= 20040220;
   }

   public boolean is_royal_guard_skill(int skillID) {
      return skillID <= 51111012 && (skillID >= 51111011 || skillID >= 51001006 && skillID <= 51001013);
   }

   private int getPAD(MapleCharacter player) {
      Item weapon_item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
      Equip e_weapon = (Equip)weapon_item;
      double watk = e_weapon.getTotalWatk();
      List<IndieTemporaryStatEntry> indiePAD = player.getIndieTemporaryStats(SecondaryStatFlag.indiePAD);
      if (indiePAD != null && indiePAD.size() != 0) {
         for (IndieTemporaryStatEntry ip : indiePAD) {
            watk += ip.getStatValue();
         }
      }

      List<IndieTemporaryStatEntry> indiePADR = player.getIndieTemporaryStats(SecondaryStatFlag.indiePadR);
      if (indiePADR != null && indiePADR.size() != 0) {
         double mult = 1.0;

         for (IndieTemporaryStatEntry ipr : indiePADR) {
            mult += ipr.getStatValue() / 100.0;
         }

         watk *= mult;
      }

      return (int)watk;
   }

   private int getMAD(MapleCharacter player) {
      Item weapon_item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
      Equip e_weapon = (Equip)weapon_item;
      double matk = e_weapon.getMatk();
      List<IndieTemporaryStatEntry> indieMAD = player.getIndieTemporaryStats(SecondaryStatFlag.indieMAD);
      if (indieMAD != null && indieMAD.size() != 0) {
         for (IndieTemporaryStatEntry ip : indieMAD) {
            matk += ip.getStatValue();
         }
      }

      List<IndieTemporaryStatEntry> indieMADR = player.getIndieTemporaryStats(SecondaryStatFlag.indieMadR);
      if (indieMADR != null && indieMADR.size() != 0) {
         double mult = 1.0;

         for (IndieTemporaryStatEntry ipr : indieMADR) {
            mult += ipr.getStatValue() / 100.0;
         }

         matk *= mult;
      }

      return (int)matk;
   }

   public List<Pair<Long, Boolean>> PDamageForPvM(MapleCharacter player, AttackInfo attack) {
      boolean isdebug = true;
      if (player != null && attack != null) {
         Item weapon_item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
         if (weapon_item == null) {
            return null;
         } else {
            PlayerStats stat = player.getStat();
            AtomicInteger Str = new AtomicInteger(stat.getStr());
            AtomicInteger Dex = new AtomicInteger(stat.getDex());
            AtomicInteger Int = new AtomicInteger(stat.getInt());
            AtomicInteger Luk = new AtomicInteger(stat.getLuk());
            new ArrayList<>(player.getInventory(MapleInventoryType.EQUIPPED).list()).forEach(item -> {
               Equip eqp = (Equip)item;
               Str.addAndGet(eqp.getTotalStr());
               Dex.addAndGet(eqp.getTotalDex());
               Int.addAndGet(eqp.getTotalInt());
               Luk.addAndGet(eqp.getTotalLuk());
            });
            Str.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.STR, 0));
            Dex.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.DEX, 0));
            Int.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.INT, 0));
            Luk.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.LUK, 0));
            Str.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.indieSTR, 0));
            Dex.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.indieDEX, 0));
            Int.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.indieINT, 0));
            Luk.addAndGet(player.getBuffedValueDefault(SecondaryStatFlag.indieLUK, 0));
            if (player.isGM()) {
               player.dropMessage(5, "계산된 STR DEX INT LUK " + Str.get() + " / " + Dex.get() + " / " + Int.get() + " / " + Luk.get());
            }

            int playerstar = stat.getStarForce();
            int playerauthentic = stat.getAuthenticForce();
            int playerarcane = stat.getArcaneForce();
            int pad = this.getPAD(player);
            int mad = this.getMAD(player);
            short jobid = player.getJob();
            long maxdmg = 0L;
            if (GameConstants.isDemonAvenger(jobid)) {
               double consts = CalcDamageUtil.getJobConstants(jobid) + 1.3;
               maxdmg = CalcDamageUtil.calcDemonAvengerDamage(
                  player.getStat().getHp(), player.getStat().getCurrentMaxHp(), player.getStat().getTotalStr(), pad, consts
               );
            } else {
               maxdmg = CalcDamageUtil.getBaseDamageByWT(player, weapon_item.getItemId(), pad, mad, attack.skillID);
            }

            long mindmg = (long)(maxdmg * 0.2 + 0.5);
            mindmg = 14588L;
            maxdmg = 72942L;
            if (isdebug) {
               player.dropMessage(6, "PAD : " + pad);
               player.dropMessage(6, "MAD : " + mad);
               player.dropMessage(6, "maxdmg : " + maxdmg);
               player.dropMessage(6, "mindmg : " + mindmg);
            }

            List<Pair<Long, Boolean>> realDamageList = new ArrayList<>();

            for (AttackPair pair : attack.allDamage) {
               realDamageList.clear();
               MapleMonster monster = player.getMap().getMonsterByOid(pair.objectid);
               long[] rand = new long[this.numRand];
               boolean[] usedrand = new boolean[this.numRand];

               for (int next = 0; next < this.numRand; next++) {
                  rand[next] = this.rndGenForCharacter.Random();
                  usedrand[next] = false;
               }

               int index = 1;
               int attackcount = 0;
               int skillID = attack.skillID;
               boolean isFirst = true;

               for (Pair<Long, Boolean> p : pair.attack) {
                  boolean shuffleIndex = false;
                  if (!usedrand[index % this.numRand]) {
                     usedrand[index % this.numRand] = true;
                  } else {
                     shuffleIndex = true;
                  }

                  index++;
                  if (CalcDamageUtil.isDashSkill(skillID)) {
                     index++;
                  }

                  double totaldmg = CalcDamageUtil.get_rand(rand[index++ % this.numRand], maxdmg, mindmg);
                  if (skillID != 0) {
                     Skill skil = SkillFactory.getSkill(skillID);
                     if (skil != null) {
                        SecondaryStatEffect eff = skil.getEffect(player.getTotalSkillLevel(skillID));
                        if (eff != null) {
                           int damage = eff.getDamage();
                           if (damage != 0) {
                              totaldmg *= damage / 100.0;
                           } else if (player.isGM()) {
                              player.dropMessage(6, "데미지가 0인 스킬 ID : " + skillID);
                           }
                        } else if (player.isGM()) {
                           player.dropMessage(6, "이펙트가 null인 스킬 Skill ID : " + skillID);
                        }
                     } else if (player.isGM()) {
                        player.dropMessage(6, "존재하지 않는 스킬 ID : " + skillID);
                     }
                  }

                  if (monster.getChangedStats() != null) {
                     totaldmg = totaldmg * (100.0 - monster.getChangedStats().PDRate) / 100.0;
                  } else {
                     totaldmg = totaldmg * (100.0 - monster.getStats().getPDRate()) / 100.0;
                  }

                  double critrate = CalcDamageUtil.get_rand(rand[index++ % this.numRand], 100.0, 0.0);
                  if (shuffleIndex) {
                     index += (int)CalcDamageUtil.get_rand(rand[(index + attackcount) % this.numRand], 0.0, 9.0);
                  }

                  boolean iscrit = false;
                  if (critrate <= 5.0) {
                     int critdmg = (int)CalcDamageUtil.get_rand(rand[index++ % this.numRand], 5000.0, 2000.0);
                     totaldmg += critdmg / 10000.0 * (long)totaldmg;
                     iscrit = true;
                  }

                  totaldmg = CalcDamageUtil.calcLevelDiffDamage(player.getLevel(), monster.getStats().getLevel(), totaldmg);
                  index++;
                  Field field = player.getMap();
                  int mapstar = field.getNeedStarForce();
                  int mapauthentic = field.getNeedAuthenticForce();
                  int maparcane = field.getNeedArcaneForce();
                  if (mapstar > 0) {
                     if (player.isGM()) {
                        player.dropMessage(6, "스타포스맵 : " + playerstar + " / " + mapstar);
                     }

                     totaldmg = CalcDamageUtil.calcStarForceDamage(playerstar, mapstar, totaldmg);
                  }

                  if (mapauthentic > 0) {
                     if (player.isGM()) {
                        player.dropMessage(6, "어센틱포스맵 : " + playerauthentic + " / " + mapauthentic);
                     }

                     totaldmg = CalcDamageUtil.calcAuthenticForceDamage(playerauthentic, mapauthentic, totaldmg);
                  }

                  if (maparcane > 0) {
                     if (player.isGM()) {
                        player.dropMessage(6, "어센틱포스맵 : " + playerarcane + " / " + maparcane);
                     }

                     totaldmg = CalcDamageUtil.calcArcaneForceDamage(playerarcane, maparcane, totaldmg);
                  }

                  if (isFirst && monster.getMobMaxHp() > totaldmg) {
                     isFirst = false;
                     index++;
                  }

                  if (player.isGM()) {
                     if (p.left == (long)totaldmg) {
                        player.dropMessage(6, "데미지 일치 " + attackcount + "타  서버데미지 : " + totaldmg + " / 클라데미지 : " + p.left);
                     } else {
                        player.dropMessage(6, "데미지 불일치 " + attackcount + "타 서버데미지 : " + totaldmg + " / 클라데미지 : " + p.left + " SKILLID : " + attack.skillID);
                     }
                  }

                  attackcount++;
               }
            }

            return realDamageList;
         }
      } else {
         return null;
      }
   }
}
