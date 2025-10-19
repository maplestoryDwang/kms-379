package objects.fields.child.rootabyss;

import constants.QuestExConstants;
import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import network.game.GameServer;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.LinkMobInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.TransInfo;
import objects.fields.gameobject.lifes.mobskills.TransformBackReference;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import objects.utils.Randomizer;
import scripting.EventInstanceManager;

public class Field_Pierre extends Field {
   public Field_Pierre(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.dispelDebuff(SecondaryStatFlag.CapDebuff);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      if (mob.getMap().getAllMonster().size() != 2) {
         List<Integer> mobs = new ArrayList<>(Arrays.asList(8900100, 8900101, 8900102, 8900000, 8900001, 8900002));
         if (mobs.contains(mob.getId())) {
            boolean set = false;
            int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());

            for (MapleCharacter p : this.getCharactersThreadsafe()) {
               if (p.getParty() != null) {
                  if (mob.getId() < 8900100) {
                     p.addGuildContributionByBoss(8900000);
                  } else {
                     p.addGuildContributionByBoss(8900100);
                  }

                  if (!set) {
                     String qexKey = "pierre_clear";
                     int bossQuest = QuestExConstants.Pierre.getQuestID();
                     if (mob.getId() == 8900000 || mob.getId() == 8900001 || mob.getId() == 8900002) {
                        qexKey = "chaos_pierre_clear";
                        bossQuest = QuestExConstants.ChaosPierre.getQuestID();
                     }

                     int qexID = 1234569;
                     List<Pair<Integer, String>> qex = new ArrayList<>(Arrays.asList(new Pair<>(qexID, qexKey), new Pair<>(bossQuest, "eNum")));
                     EventInstanceManager eim = p.getEventInstance();
                     if (eim != null) {
                        eim.restartEventTimer(300000L);
                        if (eim.getProperty("mode").equals("chaos")) {
                           eim.getMapInstance(Integer.parseInt(eim.getProperty("map")) + 10)
                              .spawnMonster(MapleLifeFactory.getMonster(8900003), new Point(489, 551), 1);
                        } else {
                           eim.getMapInstance(Integer.parseInt(eim.getProperty("map")) + 10)
                              .spawnMonster(MapleLifeFactory.getMonster(8900103), new Point(489, 551), 1);
                        }

                        List<Integer> partyPlayerList = eim.getPartyPlayerList();
                        boolean multiMode = false;
                        if (!DBConfig.isGanglim && partyPlayerList != null && !partyPlayerList.isEmpty()) {
                           for (Integer playerID : partyPlayerList) {
                              for (GameServer gs : GameServer.getAllInstances()) {
                                 MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                                 if (player != null && player.isMultiMode()) {
                                    multiMode = true;
                                    break;
                                 }
                              }
                           }
                        }

                        if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                           for (Integer playerID : partyPlayerList) {
                              boolean find = false;

                              for (GameServer gsx : GameServer.getAllInstances()) {
                                 MapleCharacter player = gsx.getPlayerStorage().getCharacterById(playerID);
                                 if (player != null) {
                                    player.updateOneInfo(questId, "count", String.valueOf(player.getOneInfoQuestInteger(questId, "count") + 1));
                                    player.updateOneInfo(questId, "mobid", String.valueOf(mob.getId()));
                                    player.updateOneInfo(questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                                    player.updateOneInfo(questId, "mobDead", "1");
                                    player.updateOneInfo(qexID, qexKey, "1");
                                    if (!DBConfig.isGanglim) {
                                       player.updateOneInfo(bossQuest, "eNum", "1");
                                       if (multiMode) {
                                          String questKey = "eNum_multi";
                                          int eNum_Count = player.getOneInfoQuestInteger(bossQuest, "eNum_multi");
                                          player.updateOneInfo(bossQuest, "eNum_multi", String.valueOf(eNum_Count + 1));
                                       } else {
                                          String questKey = "eNum_single";
                                          int eNum_Count = player.getOneInfoQuestInteger(bossQuest, "eNum_single");
                                          player.updateOneInfo(bossQuest, "eNum_single", String.valueOf(eNum_Count + 1));
                                       }
                                    }

                                    find = true;
                                    break;
                                 }
                              }

                              if (!find) {
                                 this.updateOfflineBossLimit(playerID, questId, "count", "1");
                                 this.updateOfflineBossLimit(playerID, questId, "mobid", String.valueOf(mob.getId()));
                                 this.updateOfflineBossLimit(playerID, questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                                 this.updateOfflineBossLimit(playerID, questId, "mobDead", "1");

                                 for (int count = 0; count < qex.size(); count++) {
                                    DBConnection db = new DBConnection();

                                    try (Connection con = DBConnection.getConnection()) {
                                       PreparedStatement ps = con.prepareStatement("SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
                                       ps.setInt(1, playerID);
                                       ps.setInt(2, (Integer)qex.get(count).left);
                                       ResultSet rs = ps.executeQuery();
                                       boolean f = false;

                                       while (rs.next()) {
                                          f = true;
                                          String value = rs.getString("customData");
                                          String[] v = value.split(";");
                                          StringBuilder sb = new StringBuilder();
                                          int i = 1;
                                          boolean a = false;
                                          sb.append((String)qex.get(count).right);
                                          sb.append("=");
                                          sb.append("1");
                                          sb.append(";");

                                          for (String v_ : v) {
                                             String[] cd = v_.split("=");
                                             if (!cd[0].equals(qex.get(count).right)) {
                                                sb.append(cd[0]);
                                                sb.append("=");
                                                if (cd.length > 1) {
                                                   sb.append(cd[1]);
                                                }

                                                if (v.length > i++) {
                                                   sb.append(";");
                                                }
                                             } else {
                                                a = true;
                                             }
                                          }

                                          PreparedStatement ps2 = con.prepareStatement(
                                             "UPDATE questinfo SET customData = ? WHERE characterid = ? and quest = ?"
                                          );
                                          ps2.setString(1, sb.toString());
                                          ps2.setInt(2, playerID);
                                          ps2.setInt(3, (Integer)qex.get(count).left);
                                          ps2.executeUpdate();
                                          ps2.close();
                                       }

                                       if (!f) {
                                          PreparedStatement ps2 = con.prepareStatement(
                                             "INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)"
                                          );
                                          ps2.setInt(1, playerID);
                                          ps2.setInt(2, (Integer)qex.get(count).left);
                                          ps2.setString(3, (String)qex.get(count).right + "=1");
                                          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                          String time = sdf.format(Calendar.getInstance().getTime());
                                          ps2.setString(4, time);
                                          ps2.executeQuery();
                                          ps2.close();
                                       }

                                       rs.close();
                                       ps.close();
                                    } catch (SQLException var35) {
                                       var35.printStackTrace();
                                    }
                                 }
                              }
                           }

                           set = true;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      int mobSize = this.getAllMonster().size();

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         long now = System.currentTimeMillis();
         if (mob.getHp() > 0L && mob.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) == null) {
            TransInfo transInfo = mob.getStats().getTransInfo();
            LinkMobInfo linkMobInfo = mob.getStats().getLinkMobInfo();
            if (mob.getId() != 8900000 && mob.getId() != 8900100) {
               int Case = -1;
               if (mob.getTransformBackReference() != null && mobSize < 2) {
                  Case = 0;
               } else if (transInfo != null
                  && transInfo.getHpTriggerOn() >= mob.getHPPercent()
                  && transInfo.getHpTriggerOff() <= mob.getHPPercent()
                  && mobSize == 2) {
                  Case = 1;
               } else if (transInfo != null
                  && transInfo.getHpTriggerOn() >= mob.getHPPercent()
                  && transInfo.getHpTriggerOff() <= mob.getHPPercent()
                  && mobSize < 2) {
                  Case = 2;
               } else if (transInfo != null && mob.getTransformBackReference() == null && mobSize < 2) {
                  Case = 3;
               }

               switch (Case) {
                  case -1:
                     this.broadcastGMMessage(null, CWvsContext.serverNotice(5, "Fucking Pierrreee"), true);
                     break;
                  case 0:
                     if (mob.getTransformBackReference().getNextTransformBack() <= now) {
                        TransformBackReference tbr = mob.getTransformBackReference();
                        int parentTemplateID = tbr.getParentTemplateID();
                        Point positionx = mob.getTruePosition();
                        MapleMonster newMob = MapleLifeFactory.getMonster(parentTemplateID);
                        if (newMob != null) {
                           if (tbr.isLinkHP()) {
                              newMob.setHp(mob.getHp());
                           }

                           newMob.setLinkedNextTransformCooltime(tbr.getNextTransformCooltime());
                           this.removeMonster(mob, 0);
                           this.createMonsterWithDelay(newMob, positionx, -2, mob.getStats().getDieDelay() + 300);
                        }
                     }
                     break;
                  case 1:
                     if (mob.getLinkedNextTransformCooltime() <= now) {
                        List<Integer> targets = new ArrayList<>(transInfo.getTargets());
                        Collections.shuffle(targets);
                        int transTemplateID = targets.stream().findAny().orElse(0);
                        if (transTemplateID != 0 && !transInfo.getSkillInfos().isEmpty()) {
                           TransInfo.SkillInfo skill = transInfo.getSkillInfos().get(Randomizer.rand(0, transInfo.getSkillInfos().size() - 1));
                           if (skill != null && mob.getController() != null) {
                              MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skill.getSkill(), skill.getLevel());
                              if (mobSkillInfo != null) {
                                 mobSkillInfo.applyEffect(mob.getController(), mob, null, true);
                              }
                           }

                           MapleMonster newMob = MapleLifeFactory.getMonster(transTemplateID);
                           if (newMob != null) {
                              Point positionx = mob.getTruePosition();
                              if (transInfo.isLinkHP()) {
                                 newMob.setHp(mob.getHp());
                              }

                              newMob.setLinkedNextTransformCooltime(System.currentTimeMillis() + mob.getStats().getTransInfo().getTime() * 1000);
                              this.removeMonster(mob, 0);
                              this.createMonsterWithDelay(newMob, positionx, -2, mob.getStats().getDieDelay() + 300);
                           }
                        }
                     }
                     break;
                  case 2:
                     if (mob.getLinkedNextTransformCooltime() <= now && linkMobInfo != null) {
                        int linkMob = linkMobInfo.getMob();
                        Point positionx = mob.getTruePosition();
                        MapleMonster newMob = MapleLifeFactory.getMonster(linkMob);
                        if (newMob != null) {
                           long hp = (long)(newMob.getStats().getHp() * (linkMobInfo.getReviveHP() * 0.01));
                           newMob.setHp(hp);
                           newMob.setLinkedNextTransformCooltime(System.currentTimeMillis() + mob.getStats().getTransInfo().getTime() * 1000);
                           mob.setLinkedNextTransformCooltime(newMob.getLinkedNextTransformCooltime());
                           this.createMonsterWithDelay(newMob, positionx, -2, mob.getStats().getDieDelay() + 300);
                        }
                     }
                     break;
                  case 3:
                     if (mob.getLinkedNextTransformCooltime() <= now) {
                        MapleMonster newMob = MapleLifeFactory.getMonster(8900000);
                        if (newMob != null) {
                           Point position = mob.getTruePosition();
                           TransInfo newTrans = newMob.getStats().getTransInfo();
                           if (newTrans == null) {
                              return;
                           }

                           TransformBackReference tr = new TransformBackReference(
                              mob.getId(),
                              newTrans.isLinkHP(),
                              newTrans.getHpTriggerOn(),
                              newTrans.getHpTriggerOff(),
                              System.currentTimeMillis() + newTrans.getCooltime() * 1000,
                              System.currentTimeMillis() + newTrans.getTime() * 1000
                           );
                           if (newTrans.isLinkHP()) {
                              newMob.setHp(mob.getHp());
                           }

                           newMob.setTransformBackReference(tr);
                           this.removeMonster(mob, 0);
                           this.createMonsterWithDelay(newMob, position, -2, mob.getStats().getDieDelay() + 300);
                        }
                     }
               }
            } else if (mobSize <= 1
               && transInfo.getHpTriggerOn() >= mob.getHPPercent()
               && transInfo.getHpTriggerOff() <= mob.getHPPercent()
               && mob.getLinkedNextTransformCooltime() <= now) {
               if (transInfo == null) {
                  return;
               }

               List<Integer> targets = new ArrayList<>(transInfo.getTargets());
               Collections.shuffle(targets);
               int transTemplateID = targets.stream().findAny().orElse(0);
               if (transTemplateID != 0 && !transInfo.getSkillInfos().isEmpty()) {
                  TransInfo.SkillInfo skillx = transInfo.getSkillInfos().get(Randomizer.rand(0, transInfo.getSkillInfos().size() - 1));
                  if (skillx != null && mob.getController() != null) {
                     MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillx.getSkill(), skillx.getLevel());
                     if (mobSkillInfo != null) {
                        mobSkillInfo.applyEffect(mob.getController(), mob, null, true);
                     }
                  }

                  TransformBackReference tr = new TransformBackReference(
                     mob.getId(),
                     transInfo.isLinkHP(),
                     transInfo.getHpTriggerOn(),
                     transInfo.getHpTriggerOff(),
                     System.currentTimeMillis() + transInfo.getCooltime() * 1000,
                     System.currentTimeMillis() + transInfo.getTime() * 1000
                  );
                  MapleMonster newMob = MapleLifeFactory.getMonster(transTemplateID);
                  if (newMob != null) {
                     Point positionx = mob.getTruePosition();
                     if (transInfo.isLinkHP()) {
                        newMob.setHp(mob.getHp());
                     }

                     newMob.setTransformBackReference(tr);
                     this.removeMonster(mob, 0);
                     this.createMonsterWithDelay(newMob, positionx, -2, mob.getStats().getDieDelay() + 300);
                  }
               }
            }
         }
      }

      super.fieldUpdatePerSeconds();
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }
}
