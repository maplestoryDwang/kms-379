package objects.fields.child.etc;

import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.FileoutputUtil;
import objects.utils.Timer;

public class Field_DamageMeasurement extends Field {
   private long gameStartTime = 0L;
   private long gameEndTime = 0L;
   private long lastDisplayDamageTime = 0L;
   private long lastHealMpTime = 0L;
   private int tick = 5;
   private boolean startedGame = false;
   public int spawnPoint = 0;
   private MapleCharacter player;

   public Field_DamageMeasurement(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      this.gameStartTime = 0L;
      this.gameEndTime = 0L;
      this.lastDisplayDamageTime = 0L;
      this.tick = 5;
      this.startedGame = false;
      this.player = null;
      super.resetFully(respawn);
   }

   public String getUnit(long damage) {
      int k = 0;
      int m = 0;
      int g = 0;
      int t = 0;
      int p = 0;
      long d = damage;
      p = (int)(damage / 10000000000000000L);
      if (p > 0) {
         d = damage - p * 10000000000000000L;
      }

      t = (int)(d / 1000000000000L);
      if (t > 0) {
         d -= t * 1000000000000L;
      }

      g = (int)(d / 100000000L);
      if (g > 0) {
         d -= g * 100000000L;
      }

      m = (int)(d / 10000L);
      if (m > 0) {
         d -= m * 10000L;
      }

      StringBuilder unit = new StringBuilder();
      if (p > 0) {
         unit.append(p).append("경 ");
      }

      if (t > 0) {
         unit.append(t).append("조 ");
      }

      if (g > 0) {
         unit.append(g).append("억 ");
      }

      if (m > 0) {
         unit.append(m).append("만 ");
      }

      unit.append(d);
      return unit.toString();
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.player == null) {
         this.resetFully(false);
      } else {
         if (this.lastHealMpTime == 0L || this.lastHealMpTime + 3000L <= System.currentTimeMillis()) {
            this.player.healMP(500000L);
            this.lastHealMpTime = System.currentTimeMillis();
         }

         if (!this.startedGame) {
            if (System.currentTimeMillis() >= this.gameStartTime - 6000L) {
               this.broadcastMessage(CField.MapEff("Map/Effect2.img/event/number/" + this.tick));
               this.tick--;
            }

            if (System.currentTimeMillis() >= this.gameStartTime) {
               Point pt = new Point(327, -278);
               switch (this.spawnPoint) {
                  case 0:
                     pt = new Point(-303, -278);
                     break;
                  case 2:
                     pt = new Point(900, -278);
               }

               MapleMonster mob = MapleLifeFactory.getMonster(9020107);
               mob.setHp(Long.MAX_VALUE);
               mob.setScale(300);
               mob.getStats().setHp(Long.MAX_VALUE);
               mob.setPosition(pt);
               this.spawnMonsterOnGroundBelow(mob, pt, (byte)-2, true);
               this.broadcastMessage(CField.MapEff("Map/Effect.img/event/start"));
               this.broadcastMessage(CField.getStopwatch(120000));
               if (DBConfig.isGanglim) {
                  this.gameEndTime = System.currentTimeMillis() + 120000L;
               } else {
                  this.gameEndTime = System.currentTimeMillis() + 60000L;
               }

               this.startedGame = true;
               this.broadcastMessage(CField.addPopupSay(9062025, 3000, "#b[전투력 측정]#k\r\n측정이 시작되었습니다. 용사님의 진가를 발휘해볼까요?", ""));
            }
         } else {
            MapleMonster mob = this.getMonsterById(9020107);
            if (mob != null) {
               long damage = Long.MAX_VALUE - mob.getHp();
               if (damage > 0L && (this.lastDisplayDamageTime == 0L || this.lastDisplayDamageTime + 5000L <= System.currentTimeMillis())) {
                  this.broadcastMessage(CField.addPopupSay(9062025, 1000, "#b[전투력 측정]#k\r\n\r\n현재까지 용사님의 전투력은 #b" + this.getUnit(damage) + "#k 입니다.", ""));
                  this.lastDisplayDamageTime = System.currentTimeMillis();
               }

               if (System.currentTimeMillis() >= this.gameEndTime) {
                  for (MapleMonster m : this.getAllMonstersThreadsafe()) {
                     this.removeMonster(m, 1);
                  }

                  this.broadcastMessage(CField.chatMsg(6, "[측정 완료] 시간 내에 허수아비에 가한 데미지 : " + this.getUnit(damage)));
                  this.broadcastMessage(
                     CField.addPopupSay(
                        9062025,
                        10000,
                        "#b[전투력 측정]#k\r\n측정이 완료되었습니다.\r\n\r\n총 데미지 : #b" + this.getUnit(damage) + "#k\r\n\r\n전투력이 기록되었습니다. 기록은 랭킹에서 확인할 수 있습니다.",
                        ""
                     )
                  );
                  Timer.EtcTimer.getInstance().schedule(() -> {
                     try {
                        int chrid = this.player.getId();
                        int accid = this.player.getAccountID();
                        long oldDamage = DamageMeasurementRank.getDamage(chrid);
                        if (damage > 0L && oldDamage < damage) {
                           PreparedStatement ps = null;
                           ResultSet rs = null;

                           try (Connection con = DBConnection.getConnection()) {
                              ps = con.prepareStatement("SELECT `id` FROM `characters` WHERE `accountid` = ?");
                              ps.setInt(1, accid);
                              rs = ps.executeQuery();

                              while (rs.next()) {
                                 int playerID = rs.getInt("id");
                                 if (playerID != chrid) {
                                    DamageMeasurementRank.removeRecord(playerID);
                                 }
                              }
                           } catch (SQLException var27) {
                           } finally {
                              try {
                                 if (ps != null) {
                                    ps.close();
                                    PreparedStatement var30 = null;
                                 }

                                 if (rs != null) {
                                    rs.close();
                                    ResultSet var31 = null;
                                 }
                              } catch (SQLException var24) {
                              }
                           }

                           DamageMeasurementRank.editRecord(chrid, damage);
                        }
                     } catch (Exception var29) {
                        System.out.println("데미지 기록중 오류 발생 Log_MapUpdate_Except 에 기록됨. " + var29.toString());
                        FileoutputUtil.outputFileError("Log_MapUpdate_Except.rtf", var29);
                     }
                  }, 100L);
                  this.broadcastMessage(CField.getStopwatch(5000));
                  this.player.setRegisterTransferField(ServerConstants.TownMap);
                  this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 5000L);
               }
            }
         }
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.player = player;
      switch (this.spawnPoint) {
         case 0:
            player.send(
               CField.addPopupSay(
                  9062025,
                  7000,
                  "#b[전투력 측정]#k\r\n잠시 후 전투력 측정이 시작됩니다.\n맵 좌측에 #b허수아비#k가 소환되며, 측정은 #b2분#k간 진행됩니다.\r\n측정된 전투력은 자동으로 기록되며 랭킹에 등록되게 됩니다.\r\n\r\n시간내에 용사님께서 가진 전투력을 마음껏 발휘해보시기 바랍니다.",
                  ""
               )
            );
            break;
         case 2:
            player.send(
               CField.addPopupSay(
                  9062025,
                  7000,
                  "#b[전투력 측정]#k\r\n잠시 후 전투력 측정이 시작됩니다.\n맵 우측에 #b허수아비#k가 소환되며, 측정은 #b2분#k간 진행됩니다.\r\n측정된 전투력은 자동으로 기록되며 랭킹에 등록되게 됩니다.\r\n\r\n시간내에 용사님께서 가진 전투력을 마음껏 발휘해보시기 바랍니다.",
                  ""
               )
            );
            break;
         default:
            player.send(
               CField.addPopupSay(
                  9062025,
                  7000,
                  "#b[전투력 측정]#k\r\n잠시 후 전투력 측정이 시작됩니다.\n맵 가운데에 #b허수아비#k가 소환되며, 측정은 #b2분#k간 진행됩니다.\r\n측정된 전투력은 자동으로 기록되며 랭킹에 등록되게 됩니다.\r\n\r\n시간내에 용사님께서 가진 전투력을 마음껏 발휘해보시기 바랍니다.",
                  ""
               )
            );
      }

      if (DBConfig.isGanglim) {
         player.applyBMCurse(99);
      } else {
         player.applyBMCurseValue(90);
      }

      player.send(CField.getStopwatch(20000));
      this.gameStartTime = System.currentTimeMillis() + 20000L;
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      super.onLeave(player);
      player.temporaryStatResetBySkillID(80002635);
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
   }
}
