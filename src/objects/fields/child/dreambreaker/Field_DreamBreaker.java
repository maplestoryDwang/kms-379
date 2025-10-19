package objects.fields.child.dreambreaker;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class Field_DreamBreaker extends Field {
   private int gauge = 0;
   private int stage = 0;
   private int dreamPoint = 0;
   private boolean startGame = false;
   private boolean firstSet = false;
   private long startGameTime = 0L;
   private long lastRespawnTime = 0L;
   private long stopRespawnTime = 0L;
   private long stopGaugeTime = 0L;
   private long gameStartTime = 0L;
   private long nextStageTime = 0L;
   private List<Integer> usedSkills = new ArrayList<>();
   private static int exitMapID = 921171100;
   MapleCharacter player = null;
   private int[][] orgel = new int[][]{{9833080, 9833070}, {9833081, 9833071}, {9833082, 9833072}, {9833083, 9833073}, {9833084, 9833074}};
   private int[][] etcMob = new int[][]{{9833090, 9833091}, {9833092, 9833093}, {9833094, 9833095}, {9833096, 9833097}, {9833098, 9833099}};
   private Point[] spawnPos = new Point[]{new Point(792, -457), new Point(813, -1963), new Point(3107, -237), new Point(838, 1485), new Point(-1450, -237)};

   public Field_DreamBreaker(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
      this.gauge = 500;
      this.startGameTime = System.currentTimeMillis() + 3000L;
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.gauge = 500;
      this.stage = 0;
      this.setDreamPoint(0);
      this.firstSet = false;
      this.startGame = false;
      this.startGameTime = 0L;
      this.lastRespawnTime = 0L;
      this.nextStageTime = 0L;
      this.player = null;
      this.usedSkills.clear();
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (System.currentTimeMillis() - this.startGameTime >= 180000L) {
         if (this.player.getRegisterTransferFieldTime() == 0L) {
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis());
            this.player.setRegisterTransferField(exitMapID);
         }
      } else if (this.nextStageTime != 0L) {
         if (this.nextStageTime <= System.currentTimeMillis()) {
            this.startGame = false;
            this.startGameTime = System.currentTimeMillis() + 3000L;
            this.broadcastMessage(dreamBreaker_ReadyToGame(this.stage));
            this.broadcastMessage(dreamBreaker_UIFirstSet(this.stage));
            this.broadcastMessage(dreamBreaker_ToggleTimer(true, 180000));
            this.nextStageTime = 0L;
         }
      } else {
         if (!this.startGame) {
            if (this.startGameTime <= System.currentTimeMillis()) {
               this.startGame();
               this.startGame = true;
            }
         } else {
            if (this.lastRespawnTime + 10000L <= System.currentTimeMillis()) {
               if (this.getStopRespawnTime() == 0L || this.getStopRespawnTime() <= System.currentTimeMillis()) {
                  if (this.getStopRespawnTime() != 0L) {
                     this.setStopRespawnTime(0L);
                  }

                  int count = 3 - this.countMonsterById(9833090);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833090, 792, -457);
                  }

                  count = 3 - this.countMonsterById(9833091);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833091, 792, -457);
                  }

                  count = 3 - this.countMonsterById(9833092);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833092, 813, -1963);
                  }

                  count = 3 - this.countMonsterById(9833093);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833093, 813, -1963);
                  }

                  count = 3 - this.countMonsterById(9833094);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833094, 3107, -237);
                  }

                  count = 3 - this.countMonsterById(9833095);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833095, 3107, -237);
                  }

                  count = 3 - this.countMonsterById(9833096);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833096, 838, 1485);
                  }

                  count = 3 - this.countMonsterById(9833097);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833097, 838, 1485);
                  }

                  count = 3 - this.countMonsterById(9833098);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833098, -1450, -237);
                  }

                  count = 3 - this.countMonsterById(9833099);

                  for (int i = 0; i < count; i++) {
                     this.spawnMob(9833099, -1450, -237);
                  }
               }

               this.lastRespawnTime = System.currentTimeMillis();
            }

            if (this.getStopGaugeTime() == 0L || this.getStopGaugeTime() <= System.currentTimeMillis()) {
               if (this.getStopGaugeTime() != 0L) {
                  this.setStopGaugeTime(0L);
               }

               int purple = 0;
               int yellow = 0;

               for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
                  if (mob.getId() >= 9833080 && mob.getId() <= 9833084) {
                     purple++;
                  } else if (mob.getId() >= 9833070 && mob.getId() <= 9833074) {
                     yellow++;
                  }
               }

               int delta = yellow - purple;
               int speed = delta * -10;
               this.gauge += speed;
               this.broadcastMessage(dreamBreaker_UpdateGauge(this.gauge));
               if (this.gauge <= 0) {
                  long time = (System.currentTimeMillis() - this.startGameTime) / 1000L;
                  this.broadcastMessage(dreamBreaker_ToggleTimer(true, (int)(180L - time) * 1000));
                  String bestValue = this.player.getOneInfoQuest(15901, "best");
                  int best = 0;
                  if (bestValue != null && !bestValue.isEmpty()) {
                     best = Integer.parseInt(bestValue);
                  }

                  String bestTimeValue = this.player.getOneInfoQuest(15901, "besttime");
                  int bestTime = 0;
                  if (bestTimeValue != null && !bestTimeValue.isEmpty()) {
                     bestTime = Integer.parseInt(bestTimeValue);
                  }

                  if (best == this.stage) {
                     if (bestTime > time) {
                        this.player.updateOneInfo(15901, "besttime_b", bestTimeValue);
                        this.player.updateOneInfo(15901, "best_b", String.valueOf(this.stage));
                        this.player.updateOneInfo(15901, "besttime", String.valueOf(time));
                     }
                  } else if (this.stage > best) {
                     this.player.updateOneInfo(15901, "besttime_b", bestTimeValue);
                     this.player.updateOneInfo(15901, "best_b", bestValue);
                     this.player.updateOneInfo(15901, "best", String.valueOf(this.stage));
                     this.player.updateOneInfo(15901, "besttime", String.valueOf(time));
                  }

                  int gainPoint = 0;
                  if (this.stage < 10) {
                     gainPoint = 10;
                  } else {
                     if (this.stage >= 30 && this.player.getQuestStatus(2000006) == 1 && this.player.getOneInfo(2000006, "clear") == null) {
                        this.player.updateOneInfo(2000006, "clear", "1");
                     }

                     gainPoint = this.stage - this.stage % 10;
                  }

                  this.player.send(dreamBreakerMsg("드림포인트 " + gainPoint + " 획득!"));
                  this.setDreamPoint(this.getDreamPoint() + gainPoint);
                  this.player.updateOneInfo(15901, "dream", String.valueOf(this.getDreamPoint()));
                  this.player.updateOneInfo(15901, "clearTime", String.valueOf(time));
                  this.player.gainKillPoint(this.stage);
                  int rank = DreamBreakerRank.getRank(this.player.getName());
                  this.player.updateOneInfo(15901, "rank_b", String.valueOf(rank));
                  this.gauge = 500;
                  this.stage++;
                  this.player.send(dreamBreaker_UpdateGauge(this.gauge));

                  for (MapleMonster mobx : this.getAllMonstersThreadsafe()) {
                     this.removeMonster(mobx, 1);
                  }

                  this.player.updateOneInfo(15901, "stage", String.valueOf(this.stage));
                  this.player.send(dreamBreaker_EndGame((int)time));
                  this.nextStageTime = System.currentTimeMillis() + 2000L;
                  this.usedSkills.clear();
               } else if (this.gauge >= 1000) {
                  long timex = (System.currentTimeMillis() - this.startGameTime) / 1000L;
                  this.broadcastMessage(dreamBreaker_ToggleTimer(true, (int)(180L - timex) * 1000));
                  if (this.player.getRegisterTransferFieldTime() == 0L) {
                     this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 2000L);
                     this.player.setRegisterTransferField(exitMapID);
                  }
               }
            }
         }
      }
   }

   public boolean isUsedSkill(int skill) {
      for (int s : this.usedSkills) {
         if (s == skill) {
            return true;
         }
      }

      return false;
   }

   public int getStage() {
      return this.stage;
   }

   public void addUsedSkill(int skill) {
      this.usedSkills.add(skill);
   }

   public void spawnMob(int mobID, Point pos) {
      this.spawnMob(mobID, pos.x, pos.y);
   }

   public void spawnMob(int mobID, int x, int y) {
      MapleMonster mob = MapleLifeFactory.getMonster(mobID);
      if (mobID < 9833070 || mobID > 9833074) {
         long hp = GameConstants.getDreamBreakerHP(this.stage);
         mob.setHp(hp);
         mob.getStats().setHp(hp);
      }

      this.spawnMonsterOnGroundBelow(mob, new Point(x, y));
   }

   public void startGame() {
      this.broadcastMessage(dreamBreaker_ToggleTimer(false, 180000));
      int count = 2;
      List<Integer> purpleOrgel = new ArrayList<>();

      for (int i = 0; i < 5; i++) {
         purpleOrgel.add(i);
      }

      List<Integer> yellowOrgel = new ArrayList<>();

      while (true) {
         Integer r = Randomizer.rand(0, 4);
         boolean f = false;

         for (int y : yellowOrgel) {
            if (r == y) {
               f = true;
               break;
            }
         }

         if (!f) {
            yellowOrgel.add(r);
            purpleOrgel.remove(r);
            if (--count <= 0) {
               for (int i = 0; i < purpleOrgel.size(); i++) {
                  this.spawnMob(this.orgel[purpleOrgel.get(i)][0], this.spawnPos[purpleOrgel.get(i)]);
               }

               for (int i = 0; i < yellowOrgel.size(); i++) {
                  this.spawnMob(this.orgel[yellowOrgel.get(i)][1], this.spawnPos[yellowOrgel.get(i)]);
               }

               for (int i = 0; i < 5; i++) {
                  for (int j = 0; j < 2; j++) {
                     for (int h = 0; h < 3; h++) {
                        this.spawnMob(this.etcMob[i][j], this.spawnPos[i]);
                     }
                  }
               }

               this.lastRespawnTime = System.currentTimeMillis();
               this.gameStartTime = System.currentTimeMillis();
               return;
            }
         }
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (!this.firstSet) {
         this.stage = 1;
         this.gauge = 500;
         String v = player.getOneInfoQuest(15901, "selectedStage");
         if (v != null && !v.isEmpty()) {
            this.stage = Integer.parseInt(v);
         }

         player.updateOneInfo(15901, "stage", String.valueOf(this.stage));
         this.setDreamPoint(0);
         v = player.getOneInfoQuest(15901, "dream");
         if (v != null && !v.isEmpty()) {
            this.setDreamPoint(Integer.parseInt(v));
         }

         this.broadcastMessage(dreamBreaker_ReadyToGame(this.stage));
         this.broadcastMessage(dreamBreaker_UIFirstSet(this.stage));
         this.broadcastMessage(dreamBreaker_ToggleTimer(true, 180000));
         this.firstSet = true;
         this.startGameTime = System.currentTimeMillis() + 3000L;
      }

      this.player = player;
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      String v = player.getOneInfoQuest(15901, "selectedStage");
      int selectedStage = 0;
      if (v != null && !v.isEmpty()) {
         selectedStage = Integer.parseInt(v);
      }

      boolean clear = false;
      if (selectedStage > 0 && this.stage > 1 && this.stage > selectedStage) {
         clear = true;
      }

      player.send(CField.MapEff(clear ? "Map/Effect3.img/hungryMuto/Clear" : "Map/Effect2.img/event/gameover"));
      if (clear) {
         player.send(dreamBreakerMsg(this.stage - 1 + "스테이지 클리어!"));
      }

      this.resetFully(false);
   }

   public int getGauge() {
      return this.gauge;
   }

   public void setGauge(int gauge) {
      this.gauge = gauge;
   }

   public static byte[] dreamBreaker_UIFirstSet(int stage) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(3);
      mplew.writeInt(500);
      mplew.writeInt(180000);
      mplew.writeInt(stage);
      return mplew.getPacket();
   }

   public static byte[] dreamBreaker_UpdateGauge(int gauge) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(4);
      mplew.writeInt(gauge);
      return mplew.getPacket();
   }

   public static byte[] dreamBreaker_ReadyToGame(int stage) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(5);
      mplew.writeInt(stage);
      return mplew.getPacket();
   }

   public static byte[] dreamBreaker_ToggleTimer(boolean disable, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(6);
      mplew.write(disable);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] dreamBreaker_EndGame(int clearTime) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(7);
      mplew.writeInt(clearTime);
      return mplew.getPacket();
   }

   public static byte[] dreamBreaker_DisableSkill(int skillID) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(8);
      mplew.writeInt(skillID);
      return mplew.getPacket();
   }

   public static byte[] dreamBreaker_UseSkillResult() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DREAM_BREAKER.getValue());
      mplew.writeInt(9);
      return mplew.getPacket();
   }

   public static byte[] dreamBreakerMsg(String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PROGRESS_MESSAGE_FONT.getValue());
      mplew.writeInt(3);
      mplew.writeInt(20);
      mplew.writeInt(20);
      mplew.writeInt(4);
      mplew.write(0);
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] dreamBreakerRanking(String name) {
      PacketEncoder packet = new PacketEncoder();
      int count = 1;
      packet.writeShort(SendPacketOpcode.UI_DREAM_BREAKER_RANKING.getValue());
      packet.writeInt(0);
      packet.write(200);
      packet.writeZeroBytes(8);
      packet.writeInt(DreamBreakerRank.rank.containsKey(name) ? DreamBreakerRank.rank.get(name) : 0);
      packet.writeInt(DreamBreakerRank.rank.containsKey(name) ? DreamBreakerRank.getRank(name) : 0);
      packet.writeZeroBytes(16);
      packet.write(200);
      packet.writeInt(DreamBreakerRank.rank.size() > 100 ? 100 : DreamBreakerRank.rank.size());

      for (Entry<String, Integer> info : DreamBreakerRank.rank.entrySet()) {
         if (count == 101) {
            break;
         }

         packet.writeZeroBytes(8);
         packet.writeInt(info.getValue());
         packet.writeInt(count);
         packet.writeMapleAsciiString(info.getKey());
         packet.write(false);
         count++;
      }

      return packet.getPacket();
   }

   public int getDreamPoint() {
      return this.dreamPoint;
   }

   public void setDreamPoint(int dreamPoint) {
      this.dreamPoint = Math.min(3000, dreamPoint);
   }

   public long getStopGaugeTime() {
      return this.stopGaugeTime;
   }

   public void setStopGaugeTime(long stopGaugeTime) {
      this.stopGaugeTime = stopGaugeTime;
   }

   public long getStopRespawnTime() {
      return this.stopRespawnTime;
   }

   public void setStopRespawnTime(long stopRespawnTime) {
      this.stopRespawnTime = stopRespawnTime;
   }
}
