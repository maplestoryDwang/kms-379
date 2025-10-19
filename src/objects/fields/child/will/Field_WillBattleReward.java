package objects.fields.child.will;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.center.Center;
import network.models.CField;
import objects.context.party.PartyMemberEntry;
import objects.fields.fieldset.instance.EasyWillBoss;
import objects.fields.fieldset.instance.HardWillBoss;
import objects.fields.fieldset.instance.HellWillBoss;
import objects.fields.fieldset.instance.NormalWillBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class Field_WillBattleReward extends Field_WillBattle {
   public Field_WillBattleReward(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate, 4);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof EasyWillBoss) {
         EasyWillBoss fs = (EasyWillBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450008080 && this.getMonsterById(8950113) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950113), new Point(58, 248));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalWillBoss) {
         NormalWillBoss fs = (NormalWillBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450008980 && this.getMonsterById(8950114) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950114), new Point(58, 248));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardWillBoss) {
         HardWillBoss fs = (HardWillBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450008380 && this.getMonsterById(8950115) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950115), new Point(58, 248));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HellWillBoss
         && this.getId() == 450008380
         && this.getMonsterById(8950115) == null) {
         this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950115), new Point(58, 248));
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      boolean set = false;
      boolean multiMode = false;

      for (MapleCharacter p : this.getCharactersThreadsafe()) {
         if (p.getBossMode() == 1) {
            return;
         }

         if (p.getParty() != null) {
            if (p.getMapId() == this.getId()) {
               int quantity = 0;
               int rand = Randomizer.nextInt(100);
               if (mob.getId() == 8950114) {
                  if (rand <= 90) {
                     quantity = 1;
                  } else {
                     quantity = 2;
                  }
               } else if (mob.getId() == 8950115) {
                  if (rand <= 30) {
                     quantity = 3;
                  } else {
                     quantity = 2;
                  }
               }

               if (quantity > 0) {
                  p.gainItem(4001890, (short)quantity, false, -1L, "윌 격파로 얻은 아이템");
                  p.gainItem(2438412, 1, false, -1L, "윌 격파로 얻은 아이템");
               }

               this.sendWillRemovePoison(p);
               p.dispelDebuff(SecondaryStatFlag.WillPoison);
            }

            if (!set) {
               if (mob.getId() == 8950114) {
                  this.bossClearQex(p, 1234569, "normal_will_clear");
                  String list = "";
                  List<String> names = new ArrayList<>();

                  for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                     names.add(mpc.getName());
                     StringBuilder sb = new StringBuilder("보스 노말 윌 격파");
                     MapleCharacter player = this.getCharacterById(mpc.getId());
                     if (player != null) {
                        if (!DBConfig.isGanglim && !multiMode) {
                           multiMode = player.isMultiMode();
                        }

                        LoggingManager.putLog(new BossLog(player, BossLogType.ClearLog.getType(), sb));
                     }
                  }

                  list = String.join(",", names);
                  if (!DBConfig.isGanglim) {
                     Center.Broadcast.broadcastMessage(
                        CField.chatMsg(DBConfig.isGanglim ? 8 : 4, "[보스격파] '" + p.getParty().getLeader().getName() + "' 파티(" + list + ")가 [노말 윌]을 격파하였습니다.")
                     );
                  }
               } else if (mob.getId() != 8950115) {
                  if (mob.getId() == 8950113) {
                     this.bossClearQex(p, 1234569, "easy_will_clear");
                  }
               } else {
                  this.bossClearQex(p, 1234569, "hard_will_clear");
                  boolean hell = false;
                  String list = "";
                  List<String> names = new ArrayList<>();
                  boolean check = false;

                  for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                     names.add(mpcx.getName());
                     if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellWillBoss) {
                        MapleCharacter p_ = this.getCharacterById(mpcx.getId());
                        if (p_ != null) {
                           if (!DBConfig.isGanglim && !multiMode) {
                              multiMode = p_.isMultiMode();
                           }

                           if (!DBConfig.isGanglim) {
                              String keyValue = "hell_will_point";
                              p_.updateOneInfo(787777, keyValue, String.valueOf(p_.getOneInfoQuestInteger(787777, keyValue) + 3));
                              if (!check) {
                                 this.bossClearQex(p, 1234569, "hell_will_clear");
                                 check = true;
                              }
                           } else {
                              p_.updateOneInfo(1234569, "hell_will_clear", String.valueOf(p_.getOneInfoQuestInteger(1234569, "hell_will_clear") + 1));
                           }
                        } else if (DBConfig.isGanglim) {
                           this.updateOfflineBossLimit(mpcx.getId(), 1234569, "hell_will_clear", "1");
                        }

                        hell = true;
                     }
                  }

                  list = String.join(",", names);
                  if (this.getFieldSetInstance() instanceof HardWillBoss || this.getFieldSetInstance() instanceof HellWillBoss) {
                     for (PartyMemberEntry mpcxx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                        StringBuilder sb = new StringBuilder("보스 " + (hell ? "헬" : "하드") + " 윌 격파 (" + list + ")");
                        MapleCharacter player = this.getCharacterById(mpcxx.getId());
                        if (player != null) {
                           LoggingManager.putLog(new BossLog(player, BossLogType.ClearLog.getType(), sb));
                        }
                     }
                  }

                  if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellWillBoss) {
                     Center.Broadcast.broadcastMessage(
                        CField.chatMsg(
                           22,
                           "[보스격파] [CH."
                              + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                              + "] '"
                              + p.getParty().getLeader().getName()
                              + "' 파티("
                              + list
                              + ")가 [헬 윌]를 격파하였습니다."
                        )
                     );
                  } else if (!DBConfig.isGanglim) {
                     Center.Broadcast.broadcastMessage(
                        CField.chatMsg(
                           4,
                           "[보스격파] [CH."
                              + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                              + "] '"
                              + p.getParty().getLeader().getName()
                              + "' 파티("
                              + list
                              + ")가 [하드 윌]를 격파하였습니다."
                        )
                     );
                  }
               }

               if (DBConfig.isGanglim) {
                  this.bossClear(8880342, 1234569, "will_clear");
               } else {
                  this.bossClear(8880342, 1234569, "will_clear");
                  if (this.getFieldSetInstance() == null || this.getFieldSetInstance() != null && !(this.getFieldSetInstance() instanceof HellWillBoss)) {
                     this.bossClear(8880342, 1234569, "will_clear_" + (multiMode ? "multi" : "single"));
                  }
               }

               set = true;
            }
         }
      }
   }
}
