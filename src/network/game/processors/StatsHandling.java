package network.game.processors;

import constants.GameConstants;
import java.util.EnumMap;
import java.util.Map;
import network.decode.PacketDecoder;
import network.models.CWvsContext;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.PlayerStats;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class StatsHandling {
   public static final void DistributeAP(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
      c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, true, chr));
      slea.readInt();
      PlayerStats stat = chr.getStat();
      int job = chr.getJob();
      if (chr.getRemainingAp() > 0) {
         switch (slea.readInt()) {
            case 64:
               if (stat.getStr() >= 32767) {
                  return;
               }

               stat.setStr((short)(stat.getStr() + 1), chr);
               statupdate.put(MapleStat.STR, (long)stat.getStr());
               break;
            case 128:
               if (stat.getDex() >= 32767) {
                  return;
               }

               stat.setDex((short)(stat.getDex() + 1), chr);
               statupdate.put(MapleStat.DEX, (long)stat.getDex());
               break;
            case 256:
               if (stat.getInt() >= 32767) {
                  return;
               }

               stat.setInt((short)(stat.getInt() + 1), chr);
               statupdate.put(MapleStat.INT, (long)stat.getInt());
               break;
            case 512:
               if (stat.getLuk() >= 32767) {
                  return;
               }

               stat.setLuk((short)(stat.getLuk() + 1), chr);
               statupdate.put(MapleStat.LUK, (long)stat.getLuk());
               break;
            case 2048:
               long maxhp = stat.getMaxHp();
               if (chr.getHpApUsed() >= 10000 || maxhp >= 500000L) {
                  return;
               }

               int delta = 0;
               if (GameConstants.isNovice(job)) {
                  delta = Randomizer.rand(8, 12);
               } else if ((job < 100 || job > 132) && (job < 3200 || job > 3212) && (job < 1100 || job > 1112) && (job < 3100 || job > 3112)) {
                  if ((job < 200 || job > 232) && !GameConstants.isEvan(job)) {
                     if ((job < 300 || job > 322)
                        && (job < 400 || job > 434)
                        && (job < 1300 || job > 1312)
                        && (job < 1400 || job > 1412)
                        && (job < 3300 || job > 3312)
                        && (job < 2300 || job > 2312)) {
                        if ((job < 510 || job > 512) && (job < 1510 || job > 1512)) {
                           if ((job < 500 || job > 532) && (job < 3500 || job > 3512) && job != 1500) {
                              if (job >= 1200 && job <= 1212) {
                                 delta = Randomizer.rand(15, 21);
                              } else if (job >= 2000 && job <= 2112) {
                                 delta = Randomizer.rand(38, 42);
                              } else {
                                 delta = Randomizer.rand(50, 100);
                              }
                           } else {
                              delta = Randomizer.rand(18, 22);
                           }
                        } else {
                           delta = Randomizer.rand(28, 32);
                        }
                     } else {
                        delta = Randomizer.rand(16, 20);
                     }
                  } else {
                     delta = Randomizer.rand(10, 20);
                  }
               } else {
                  delta = Randomizer.rand(36, 42);
               }

               maxhp = Math.min(500000L, Math.abs(maxhp + delta));
               chr.setHpApUsed((short)(chr.getHpApUsed() + 1));
               stat.setMaxHp(maxhp, chr);
               statupdate.put(MapleStat.MAXHP, maxhp);
               c.getPlayer().updateOneInfo(100710, "ap_hp", String.valueOf(c.getPlayer().getOneInfoQuestInteger(100710, "ap_hp") + delta));
               break;
            case 8192:
               long maxmp = stat.getMaxMp();
               if (chr.getHpApUsed() >= 10000 || stat.getMaxMp() >= 500000L) {
                  return;
               }

               delta = 0;
               if (GameConstants.isNovice(job)) {
                  delta = Randomizer.rand(6, 8);
               } else {
                  if (job >= 3100 && job <= 3112) {
                     return;
                  }

                  if ((job < 200 || job > 232) && !GameConstants.isEvan(job) && (job < 3200 || job > 3212) && (job < 1200 || job > 1212)) {
                     if ((job < 300 || job > 322)
                        && (job < 400 || job > 434)
                        && (job < 500 || job > 532)
                        && (job < 3200 || job > 3212)
                        && (job < 3500 || job > 3512)
                        && (job < 1300 || job > 1312)
                        && (job < 1400 || job > 1412)
                        && (job < 1500 || job > 1512)
                        && (job < 2300 || job > 2312)) {
                        if ((job < 100 || job > 132) && (job < 1100 || job > 1112) && (job < 2000 || job > 2112)) {
                           delta = Randomizer.rand(50, 100);
                        } else {
                           delta = Randomizer.rand(6, 9);
                        }
                     } else {
                        delta = Randomizer.rand(10, 12);
                     }
                  } else {
                     delta = Randomizer.rand(38, 40);
                  }
               }

               maxmp = Math.min(500000L, Math.abs(maxmp + delta));
               chr.setHpApUsed((short)(chr.getHpApUsed() + 1));
               stat.setMaxMp(maxmp, chr);
               statupdate.put(MapleStat.MAXMP, maxmp);
               c.getPlayer().updateOneInfo(100710, "ap_mp", String.valueOf(c.getPlayer().getOneInfoQuestInteger(100710, "ap_mp") + delta));
               break;
            default:
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
         }

         chr.setRemainingAp((short)(chr.getRemainingAp() - 1));
         statupdate.put(MapleStat.AVAILABLEAP, (long)chr.getRemainingAp());
         c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, true, chr));
      }
   }

   public static final void DistributeSP(int skillid, int count, MapleClient c, MapleCharacter chr) {
      boolean isBeginnerSkill = false;
      int remainingSp;
      if (GameConstants.isNovice(skillid / 10000) && (skillid % 10000 == 1000 || skillid % 10000 == 1001 || skillid % 10000 == 1002 || skillid % 10000 == 2)) {
         boolean resistance = skillid / 10000 == 3000 || skillid / 10000 == 3001;
         int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(skillid / 10000 * 10000 + 1000));
         int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(skillid / 10000 * 10000 + 1001));
         int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(skillid / 10000 * 10000 + (resistance ? 2 : 1002)));
         remainingSp = Math.min(chr.getLevel() - 1, resistance ? 9 : 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
         isBeginnerSkill = true;
      } else {
         if (GameConstants.isNovice(skillid / 10000)) {
            return;
         }

         remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillid));
      }

      Skill skill = SkillFactory.getSkill(skillid);

      for (Pair<String, Integer> ski : skill.getRequiredSkills()) {
         if (ski.left.equals("level")) {
            if (chr.getLevel() < ski.right) {
               return;
            }
         } else {
            int left = Integer.parseInt(ski.left);
            if (chr.getSkillLevel(SkillFactory.getSkill(left)) < ski.right) {
               return;
            }
         }
      }

      int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
      int curLevel = chr.getSkillLevel(skill);
      if (!skill.isInvisible()
         || chr.getSkillLevel(skill) != 0
         || (!skill.isFourthJob() || chr.getMasterLevel(skill) != 0) && (skill.isFourthJob() || maxlevel >= 10 || isBeginnerSkill)) {
         for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               chr.dropMessage(1, "This skill has been blocked and may not be added.");
               return;
            }
         }

         if (remainingSp > 0 && curLevel + count <= maxlevel && skill.canBeLearnedBy(chr.getJob())) {
            if (!isBeginnerSkill) {
               int skillbook = GameConstants.getSkillBookForSkill(skillid);
               chr.setRemainingSp(chr.getRemainingSp(skillbook) - count, skillbook);
            }

            chr.updateSingleStat(MapleStat.AVAILABLESP, chr.getRemainingSp());
            int masterLevel = chr.getMasterLevel(skill);
            if (masterLevel == 0) {
               masterLevel = skill.getMaxLevel();
            }

            if (masterLevel < count && masterLevel < maxlevel) {
               masterLevel = maxlevel;
            }

            chr.changeSingleSkillLevel(skill, (byte)(curLevel + count), (byte)masterLevel);
            chr.setChangedSkills();
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void AutoAssignAP(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.skip(4);
      int count = slea.readInt();
      int PrimaryStat = slea.readInt();
      int amount = slea.readInt();
      int SecondaryStat = count == 2 ? slea.readInt() : 0;
      int amount2 = count == 2 ? slea.readInt() : 0;
      if (amount >= 0 && amount2 >= 0) {
         PlayerStats playerst = chr.getStat();
         Map statupdate = new EnumMap<>(MapleStat.class);
         c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, true, chr));
         if (chr.getRemainingAp() == amount + amount2 || GameConstants.isXenon(chr.getJob())) {
            switch (PrimaryStat) {
               case 64:
                  if (playerst.getStr() + amount > 32767) {
                     return;
                  }

                  playerst.setStr((short)(playerst.getStr() + amount), chr);
                  statupdate.put(MapleStat.STR, (long)playerst.getStr());
                  break;
               case 128:
                  if (playerst.getDex() + amount > 32767) {
                     return;
                  }

                  playerst.setDex((short)(playerst.getDex() + amount), chr);
                  statupdate.put(MapleStat.DEX, (long)playerst.getDex());
                  break;
               case 256:
                  if (playerst.getInt() + amount > 32767) {
                     return;
                  }

                  playerst.setInt((short)(playerst.getInt() + amount), chr);
                  statupdate.put(MapleStat.INT, (long)playerst.getInt());
                  break;
               case 512:
                  if (playerst.getLuk() + amount > 32767) {
                     return;
                  }

                  playerst.setLuk((short)(playerst.getLuk() + amount), chr);
                  statupdate.put(MapleStat.LUK, (long)playerst.getLuk());
                  break;
               case 2048:
                  if (playerst.getMaxHp() + amount * 30 > 500000L) {
                     return;
                  }

                  playerst.setMaxHp(playerst.getMaxHp() + amount * 30, chr);
                  statupdate.put(MapleStat.MAXHP, playerst.getMaxHp());
                  break;
               default:
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
            }

            switch (SecondaryStat) {
               case 64:
                  if (playerst.getStr() + amount2 > 32767) {
                     return;
                  }

                  playerst.setStr((short)(playerst.getStr() + amount2), chr);
                  statupdate.put(MapleStat.STR, (long)playerst.getStr());
                  break;
               case 128:
                  if (playerst.getDex() + amount2 > 32767) {
                     return;
                  }

                  playerst.setDex((short)(playerst.getDex() + amount2), chr);
                  statupdate.put(MapleStat.DEX, (long)playerst.getDex());
                  break;
               case 256:
                  if (playerst.getInt() + amount2 > 32767) {
                     return;
                  }

                  playerst.setInt((short)(playerst.getInt() + amount2), chr);
                  statupdate.put(MapleStat.INT, (long)playerst.getInt());
                  break;
               case 512:
                  if (playerst.getLuk() + amount2 > 32767) {
                     return;
                  }

                  playerst.setLuk((short)(playerst.getLuk() + amount2), chr);
                  statupdate.put(MapleStat.LUK, (long)playerst.getLuk());
                  break;
               case 2048:
                  if (playerst.getMaxHp() + amount * 30 > 500000L) {
                     return;
                  }

                  playerst.setMaxHp(playerst.getMaxHp() + amount * 30, chr);
                  statupdate.put(MapleStat.MAXHP, playerst.getMaxHp());
            }

            chr.setRemainingAp((short)(chr.getRemainingAp() - (amount + amount2)));
            statupdate.put(MapleStat.AVAILABLEAP, (long)chr.getRemainingAp());
            c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, true, chr));
         }
      }
   }
}
