package objects.fields.child.pollo;

import constants.QuestExConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.RandomPortal;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class Field_TownDefense extends Field {
   private MapleCharacter player = null;
   private long displayStartGameTime = 0L;
   private boolean displayStartGame = false;
   private long startGameTime = 0L;
   private boolean startGame = false;
   private long endGameTime = 0L;
   private boolean endGame = false;
   private boolean clearGame = false;
   private int wave = 0;
   private int life = 20;
   private int spawnIndex = 0;
   private static List<List<List<Pair<Point, Integer>>>> mobLists = new ArrayList<>();

   public Field_TownDefense(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player == null) {
         this.resetFully(false);
      } else if (this.clearGame) {
         this.player.send(CField.showEffect("killing/clear"));
         this.player.setRegisterTransferField(993000600);
         this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         this.clearGame = false;
         this.endGame = true;
      } else {
         if (this.endGameTime == 0L) {
            this.endGameTime = System.currentTimeMillis() + 300000L;
            this.player.send(CField.getClock(300));
         }

         if (!this.endGame && this.endGameTime <= System.currentTimeMillis()) {
            this.player.send(CField.showEffect("killing/timeout"));
            this.player.setRegisterTransferField(993000600);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            this.endGame = true;
         } else if (!this.endGame) {
            if (this.displayStartGameTime == 0L) {
               this.displayStartGameTime = System.currentTimeMillis() + 2000L;
            }

            if (!this.displayStartGame && this.displayStartGameTime <= System.currentTimeMillis()) {
               this.startGameTime = System.currentTimeMillis() + 3000L;
               this.broadcastMessage(CField.showEffect("defense/count"));
               this.displayStartGame = true;
            }

            if (!this.startGame) {
               if (this.startGameTime != 0L && this.startGameTime <= System.currentTimeMillis()) {
                  this.startGame = true;
                  this.nextWave();
               }
            } else if (this.wave <= 3) {
               List<List<Pair<Point, Integer>>> l = mobLists.get(this.wave - 1);
               if (l.size() > this.spawnIndex) {
                  List<Pair<Point, Integer>> list = l.get(this.spawnIndex++);
                  if (list != null) {
                     RandomPortal portal = this.player.getRandomPortal();
                     if (portal != null) {
                        for (Pair<Point, Integer> pair : list) {
                           Point pos = pair.left;
                           int mobID = pair.right;
                           MapleMonster mob = MapleLifeFactory.getMonster(mobID);
                           ChangeableStats cs = new ChangeableStats(mob.getStats());
                           int level = portal.getMobAvgLevel();
                           cs.level = level;
                           long hp = portal.getMobAvgHp();
                           long exp = portal.getMobAvgExp();
                           cs.hp = hp;
                           if (mobID == 9831002) {
                              hp *= 5L;
                              exp *= 20L;
                           } else if (mobID == 9831008) {
                              hp *= 7L;
                              exp *= 30L;
                           } else if (mobID == 9831014) {
                              hp *= 10L;
                              exp *= 50L;
                           } else {
                              exp *= 7L;
                           }

                           cs.exp = (int)exp;
                           mob.getStats().setLevel((short)level);
                           mob.getStats().setMaxHp(hp);
                           mob.getStats().setHp(hp);
                           mob.getStats().setExp((int)exp);
                           mob.getStats().setSpeed(0);
                           if (pos.getX() == 466.0) {
                              mob.setFh(23);
                              mob.setStance(3);
                           } else {
                              mob.setFh(10);
                              mob.setStance(2);
                           }

                           mob.setOverrideStats(cs);
                           this.spawnMonsterOnGroundBelow(mob, pos);
                        }
                     }
                  }
               }

               if (this.wave <= 3 && mobLists.get(this.wave - 1).size() <= this.spawnIndex && this.getAllMonstersThreadsafe().size() <= 0) {
                  this.nextWave();
                  return;
               }
            }
         }
      }
   }

   public void nextWave() {
      if (this.wave >= 3) {
         this.clearGame = true;
      } else {
         this.wave++;
         this.spawnIndex = 0;
         this.sendWave();
         if (this.wave > 1) {
            this.player.send(CWvsContext.getScriptProgressMessage("WAVE를 막아냈습니다. 다음 WAVE를 준비해주세요."));
         }

         this.player.send(CField.environmentChange("defense/wave/" + this.wave, 19));
         this.player.send(CField.environmentChange("killing/first/start", 17, 0));
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.player = null;
      this.displayStartGameTime = 0L;
      this.displayStartGame = false;
      this.startGameTime = 0L;
      this.startGame = false;
      this.endGameTime = 0L;
      this.endGame = false;
      this.clearGame = false;
      this.spawnIndex = 0;
      this.setLife(20);
      this.setWave(0);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.player = player;
      player.send(CField.startMapEffect("놈들이 겁도 없이 성벽 안의 마을을 습격하는군! 모조리 해치워라!", 5120159, true, 10));
      this.sendLife();
      this.sendWave();
      RandomPortal portal = player.getRandomPortal();
      if (portal != null) {
         player.updateOneInfo(15142, "gameType", String.valueOf(portal.getGameType().getType()));
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   public int getWave() {
      return this.wave;
   }

   public void setWave(int wave) {
      this.wave = wave;
   }

   public int getLife() {
      return this.life;
   }

   public void setLife(int life) {
      this.life = life;
   }

   public void decLife() {
      if (this.life > 0) {
         this.life--;
         this.player.send(CField.addPopupSay(9001059, 3000, this.player.getName() + ", 너의 사냥 실력이 이정도밖에 안됐나?", ""));
         this.sendLife();
      }

      if (this.life <= 0 && !this.endGame) {
         this.player.send(CField.showEffect("killing/fail"));
         this.player.setRegisterTransferField(993000600);
         this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         this.endGame = true;
      }
   }

   public void sendLife() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TOWN_DEFENSE_LIFE.getValue());
      packet.writeInt(this.life);
      this.player.send(packet.getPacket());
   }

   public void sendWave() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TOWN_DEFENSE_WAVE.getValue());
      packet.writeInt(this.wave);
      this.player.send(packet.getPacket());
   }

   static {
      List<List<Pair<Point, Integer>>> wave1 = new ArrayList<>();
      List<Pair<Point, Integer>> list1 = new ArrayList<>();
      list1.add(new Pair<>(new Point(-706, -195), 9831000));
      list1.add(new Pair<>(new Point(-706, -195), 9831000));
      list1.add(new Pair<>(new Point(466, 165), 9831000));
      wave1.add(list1);
      list1 = new ArrayList<>();
      list1.add(new Pair<>(new Point(-706, -195), 9831000));
      list1.add(new Pair<>(new Point(466, 165), 9831001));
      list1.add(new Pair<>(new Point(466, 165), 9831001));
      wave1.add(list1);
      List<Pair<Point, Integer>> var7 = new ArrayList();
      var7.add(new Pair<>(new Point(466, 165), 9831000));
      var7.add(new Pair<>(new Point(466, 165), 9831001));
      wave1.add(var7);
      List<Pair<Point, Integer>> var8 = new ArrayList();
      var8.add(new Pair<>(new Point(466, 165), 9831000));
      var8.add(new Pair<>(new Point(466, 165), 9831000));
      var8.add(new Pair<>(new Point(-706, -195), 9831000));
      var8.add(new Pair<>(new Point(466, 165), 9831001));
      wave1.add(var8);
      List<Pair<Point, Integer>> var9 = new ArrayList();
      var9.add(new Pair<>(new Point(-706, -195), 9831001));
      var9.add(new Pair<>(new Point(-706, -195), 9831000));
      wave1.add(var9);
      List<Pair<Point, Integer>> var10 = new ArrayList();
      var10.add(new Pair<>(new Point(466, 165), 9831000));
      var10.add(new Pair<>(new Point(466, 165), 9831000));
      wave1.add(var10);
      List<Pair<Point, Integer>> var11 = new ArrayList();
      var11.add(new Pair<>(new Point(-706, -195), 9831000));
      var11.add(new Pair<>(new Point(-706, -195), 9831000));
      var11.add(new Pair<>(new Point(466, 165), 9831001));
      var11.add(new Pair<>(new Point(-706, -195), 9831001));
      wave1.add(var11);
      List<Pair<Point, Integer>> var12 = new ArrayList();
      var12.add(new Pair<>(new Point(-706, -195), 9831000));
      var12.add(new Pair<>(new Point(466, 165), 9831002));
      wave1.add(var12);
      List<Pair<Point, Integer>> var13 = new ArrayList();
      var13.add(new Pair<>(new Point(466, 165), 9831000));
      var13.add(new Pair<>(new Point(-706, -195), 9831001));
      wave1.add(var13);
      List<Pair<Point, Integer>> var14 = new ArrayList();
      var14.add(new Pair<>(new Point(466, 165), 9831001));
      var14.add(new Pair<>(new Point(466, 165), 9831001));
      var14.add(new Pair<>(new Point(-706, -195), 9831001));
      wave1.add(var14);
      List<Pair<Point, Integer>> var15 = new ArrayList();
      var15.add(new Pair<>(new Point(466, 165), 9831001));
      var15.add(new Pair<>(new Point(-706, -195), 9831001));
      wave1.add(var15);
      List<List<Pair<Point, Integer>>> wave2 = new ArrayList<>();
      List<Pair<Point, Integer>> list2 = new ArrayList<>();
      list2.add(new Pair<>(new Point(466, 165), 9831006));
      list2.add(new Pair<>(new Point(466, 165), 9831006));
      list2.add(new Pair<>(new Point(-706, -195), 9831006));
      wave2.add(list2);
      list2 = new ArrayList<>();
      list2.add(new Pair<>(new Point(466, 165), 9831006));
      list2.add(new Pair<>(new Point(-706, -195), 9831007));
      list2.add(new Pair<>(new Point(-706, -195), 9831007));
      wave2.add(list2);
      List<Pair<Point, Integer>> var17 = new ArrayList();
      var17.add(new Pair<>(new Point(466, 165), 9831006));
      var17.add(new Pair<>(new Point(466, 165), 9831007));
      var17.add(new Pair<>(new Point(-706, -195), 9831006));
      wave2.add(var17);
      List<Pair<Point, Integer>> var18 = new ArrayList();
      var18.add(new Pair<>(new Point(-706, -195), 9831006));
      var18.add(new Pair<>(new Point(466, 165), 9831006));
      var18.add(new Pair<>(new Point(-706, -195), 9831007));
      wave2.add(var18);
      List<Pair<Point, Integer>> var19 = new ArrayList();
      var19.add(new Pair<>(new Point(466, 165), 9831007));
      var19.add(new Pair<>(new Point(466, 165), 9831006));
      wave2.add(var19);
      List<Pair<Point, Integer>> var20 = new ArrayList();
      var20.add(new Pair<>(new Point(-706, -195), 9831006));
      var20.add(new Pair<>(new Point(-706, -195), 9831006));
      wave2.add(var20);
      List<Pair<Point, Integer>> var21 = new ArrayList();
      var21.add(new Pair<>(new Point(466, 165), 9831006));
      var21.add(new Pair<>(new Point(466, 165), 9831006));
      var21.add(new Pair<>(new Point(-706, -195), 9831007));
      wave2.add(var21);
      List<Pair<Point, Integer>> var22 = new ArrayList();
      var22.add(new Pair<>(new Point(466, 165), 9831007));
      var22.add(new Pair<>(new Point(466, 165), 9831006));
      wave2.add(var22);
      List<Pair<Point, Integer>> var23 = new ArrayList();
      var23.add(new Pair<>(new Point(466, 165), 9831008));
      var23.add(new Pair<>(new Point(466, 165), 9831007));
      wave2.add(var23);
      List<Pair<Point, Integer>> var24 = new ArrayList();
      var24.add(new Pair<>(new Point(466, 165), 9831006));
      var24.add(new Pair<>(new Point(-706, -195), 9831006));
      wave2.add(var24);
      List<Pair<Point, Integer>> var25 = new ArrayList();
      var25.add(new Pair<>(new Point(466, 165), 9831006));
      var25.add(new Pair<>(new Point(-706, -195), 9831006));
      var25.add(new Pair<>(new Point(-706, -195), 9831006));
      wave2.add(var25);
      List<List<Pair<Point, Integer>>> wave3 = new ArrayList<>();
      List<Pair<Point, Integer>> list3 = new ArrayList<>();
      list3.add(new Pair<>(new Point(466, 165), 9831013));
      list3.add(new Pair<>(new Point(466, 165), 9831014));
      list3.add(new Pair<>(new Point(-706, -195), 9831013));
      wave3.add(list3);
      list3 = new ArrayList<>();
      list3.add(new Pair<>(new Point(-706, -195), 9831013));
      list3.add(new Pair<>(new Point(466, 165), 9831013));
      list3.add(new Pair<>(new Point(466, 165), 9831013));
      wave3.add(list3);
      List<Pair<Point, Integer>> var27 = new ArrayList();
      var27.add(new Pair<>(new Point(-706, -195), 9831013));
      var27.add(new Pair<>(new Point(466, 165), 9831013));
      wave3.add(var27);
      List<Pair<Point, Integer>> var28 = new ArrayList();
      var28.add(new Pair<>(new Point(466, 165), 9831013));
      var28.add(new Pair<>(new Point(-706, -195), 9831013));
      var28.add(new Pair<>(new Point(-706, -195), 9831014));
      wave3.add(var28);
      List<Pair<Point, Integer>> var29 = new ArrayList();
      var29.add(new Pair<>(new Point(466, 165), 9831013));
      var29.add(new Pair<>(new Point(466, 165), 9831013));
      wave3.add(var29);
      mobLists.add(wave1);
      mobLists.add(wave2);
      mobLists.add(wave3);
   }
}
