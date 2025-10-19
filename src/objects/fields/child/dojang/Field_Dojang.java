package objects.fields.child.dojang;

import database.DBConfig;
import java.awt.Point;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class Field_Dojang extends Field {
   private MapleCharacter player = null;
   private boolean startGame = false;
   private boolean endGame = false;
   private long resumeClockTime = 0L;
   private int playTimeTick = 0;
   private long startGameTime = 0L;
   private int stage = 0;
   private int killCount = 0;
   private long goNextStageTime = 0L;
   private long reservedSpawnedMobTime = 0L;
   private static int PLAY_TIME_SECONDS = 900;
   private static int[] mobLists = null;
   private static long[] hpLists = null;
   private boolean challengeMode = false;

   public Field_Dojang(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.stage == 0) {
         if (this.goNextStageTime != 0L && this.goNextStageTime <= System.currentTimeMillis()) {
            this.goNextStageTime = 0L;
            this.player.setRegisterTransferField(this.getId() + 100);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         }
      } else if (!this.endGame && this.playTimeTick > PLAY_TIME_SECONDS) {
         this.player.send(CField.environmentChange("dojang/timeOver", 19, 0));
         this.player.updateOneInfo(1234580, "dojang_tick", String.valueOf(this.playTimeTick));
         this.player.updateOneInfo(1234580, "dojang_floor", String.valueOf(this.stage));
         this.player.setRegisterTransferField(925020002);
         this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         this.endGame = true;
      } else if (!this.endGame) {
         if (!this.startGame && this.startGameTime == 0L) {
            this.startGameTime = System.currentTimeMillis() + 3000L;
         }

         if (this.reservedSpawnedMobTime != 0L && this.reservedSpawnedMobTime <= System.currentTimeMillis()) {
            this.reservedSpawnedMobTime = 0L;
            Point pos = new Point(Randomizer.rand(-300, 270), 7);
            MapleMonster mob = MapleLifeFactory.getMonster(mobLists[this.stage - 1]);
            if (mob != null) {
               ChangeableStats cs = new ChangeableStats(mob.getStats());
               long hp = hpLists[this.stage - 1];
               if (DBConfig.isGanglim) {
                  if (this.challengeMode) {
                     hp *= 100L;
                  } else {
                     hp *= 10L;
                  }
               } else if (this.challengeMode) {
                  hp *= 2L;
               }

               cs.hp = hp;
               mob.getStats().setMaxHp(hp);
               mob.getStats().setHp(hp);
               mob.getStats().setSpeed(0);
               mob.setOverrideStats(cs);
               this.spawnMonsterOnGroundBelow(mob, pos, (byte)1);
            }
         }

         if (!this.startGame && this.startGameTime <= System.currentTimeMillis()) {
            Point pos = new Point(Randomizer.rand(-300, 270), 7);
            if (this.stage == 61) {
               pos.x = 325;
            } else if (this.stage == 68) {
               pos.x = 246;
            }

            MapleMonster mob = MapleLifeFactory.getMonster(mobLists[this.stage - 1]);
            if (mob != null) {
               ChangeableStats cs = new ChangeableStats(mob.getStats());
               long hp = hpLists[this.stage - 1];
               if (DBConfig.isGanglim) {
                  if (this.challengeMode) {
                     hp *= 100L;
                  } else {
                     hp *= 10L;
                  }
               } else if (this.challengeMode) {
                  hp *= 10L;
               }

               cs.hp = hp;
               mob.getStats().setMaxHp(hp);
               mob.getStats().setHp(hp);
               mob.getStats().setSpeed(0);
               mob.setOverrideStats(cs);
               this.spawnMonsterOnGroundBelow(mob, pos, (byte)1);
            }

            if (this.stage == 21) {
               for (int i = 0; i < 5; i++) {
                  mob = MapleLifeFactory.getMonster(9305644);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 22) {
               for (int ix = 0; ix < 5; ix++) {
                  mob = MapleLifeFactory.getMonster(9305646);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 23) {
               for (int ixx = 0; ixx < 5; ixx++) {
                  mob = MapleLifeFactory.getMonster(9305645);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 24) {
               for (int ixxx = 0; ixxx < 5; ixxx++) {
                  mob = MapleLifeFactory.getMonster(9830000);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 25) {
               for (int ixxxx = 0; ixxxx < 5; ixxxx++) {
                  mob = MapleLifeFactory.getMonster(9305686);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 26) {
               for (int ixxxxx = 0; ixxxxx < 5; ixxxxx++) {
                  mob = MapleLifeFactory.getMonster(9305688);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 27) {
               for (int ixxxxxx = 0; ixxxxxx < 5; ixxxxxx++) {
                  mob = MapleLifeFactory.getMonster(9305689);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 28) {
               for (int ixxxxxxx = 0; ixxxxxxx < 5; ixxxxxxx++) {
                  mob = MapleLifeFactory.getMonster(9305691);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            } else if (this.stage == 29) {
               for (int ixxxxxxxx = 0; ixxxxxxxx < 5; ixxxxxxxx++) {
                  mob = MapleLifeFactory.getMonster(9305693);
                  if (mob != null) {
                     Point p = new Point(Math.min(470, Math.max(-440, Randomizer.rand(pos.x - 50, pos.x + 50))), 7);
                     this.spawnMonsterOnGroundBelow(mob, p, (byte)1);
                  }
               }
            }

            this.startGame = true;
         }

         if (this.resumeClockTime != 0L) {
            if (this.resumeClockTime != 0L && this.resumeClockTime <= System.currentTimeMillis()) {
               this.player.send(CField.getToggleDojangClock(false, PLAY_TIME_SECONDS - this.playTimeTick, 0));
               this.resumeClockTime = 0L;
            }
         } else {
            this.playTimeTick++;
         }
      }
   }

   public void updateRanking() {
      int floor = this.player.getOneInfoQuestInteger(100466, "Floor");
      int time = this.player.getOneInfoQuestInteger(100466, "Time");
      if (time == 0 || floor < this.stage || floor == this.stage && time > this.playTimeTick) {
         this.player.updateOneInfo(100466, "Time", String.valueOf(this.playTimeTick));
         this.player.updateOneInfo(100466, "Floor", String.valueOf(this.stage));
      }

      if (this.stage >= 80 && this.player.getOneInfoQuestInteger(1234590, "open_challenge") <= 0) {
         this.player.updateOneInfo(1234590, "open_challenge", "1");
         this.player.dropMessage(5, "무릉도장 챌린지 모드가 해방되었습니다.");
      }

      int score = this.stage * 1000 + (999 - this.playTimeTick);
      if (DojangRanking.addAndCalcRank(score, this.player, this.challengeMode)) {
         this.player.dropMessage(5, "금주 최고 기록을 달성하였습니다.");
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.player = this.player;
      this.startGame = false;
      this.endGame = false;
      this.startGameTime = 0L;
      this.stage = 0;
      this.resumeClockTime = 0L;
      this.killCount = 0;
      this.playTimeTick = 0;
      this.reservedSpawnedMobTime = 0L;
      this.goNextStageTime = 0L;
      this.challengeMode = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);

      for (MapleCharacter p : this.getCharactersThreadsafe()) {
         if (p != null && p.getId() != player.getId()) {
            player.warp(15);
            return;
         }
      }

      if (player.getDojangChallengeMode() == 1) {
         this.challengeMode = true;
      }

      this.player = player;
      int temp = (this.getId() - 925070000) / 100;
      int stage = temp - temp / 100 * 100;
      this.stage = stage;
      if (DBConfig.isGanglim && this.challengeMode && this.getId() == 925070100) {
         player.applyBMCurse(99);
      }

      if (stage > 0) {
         if (stage == 1) {
            player.send(CField.startMapEffect("제한시간은 15분, 최대한 신속하게 몬스터를 쓰러트리고 다음 층으로 올라가면 돼!", 5120024, true, 10));
            player.send(CField.getStartDojangClock(PLAY_TIME_SECONDS, 0));
         } else {
            this.playTimeTick = player.getOneInfoQuestInteger(1234580, "dojang_tick");
            player.send(CField.getStartDojangClock(PLAY_TIME_SECONDS, this.playTimeTick));
            player.send(CField.getToggleDojangClock(false, PLAY_TIME_SECONDS - this.playTimeTick, 0));
         }
      } else {
         player.send(CField.getStartDojangClock(901, 0));
         player.send(CField.getToggleDojangClock(true, 901, 30));
         player.send(CField.startMapEffect("사부님의 특별한 도법으로 모든 버프가 해제되었어. 이래야 좀 공평하지? 30초 줄테니까 준비해서 올라가라고.", 5120024, true, 10));
         player.send(CField.environmentChange("Map/Effect2.img/MuruengTime", 19));
         this.goNextStageTime = System.currentTimeMillis() + 30000L;
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      if (DBConfig.isGanglim && this.challengeMode && this.getId() == 925078000) {
         player.temporaryStatReset(SecondaryStatFlag.BlackMageCursePmdReduce);
      }

      this.resetFully(false);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      this.killCount++;
      if (this.checkClear()) {
         this.player.send(CField.getToggleDojangClock(true, PLAY_TIME_SECONDS - this.playTimeTick, 10));
         this.player.send(CField.environmentChange("Dojang/clear", 5, 100));
         this.player.send(CField.environmentChange("dojang/end/clear", 19, 0));
         Reactor reactor = this.getReactorByName("door");
         if (reactor != null) {
            reactor.setState((byte)1);
            this.player.send(CField.triggerReactor(reactor, 0));
         }

         this.player.send(CWvsContext.getScriptProgressMessage("상대를 격파하였습니다. 10초간 타이머가 정지됩니다."));
         this.resumeClockTime = System.currentTimeMillis() + 10000L;
      }
   }

   public boolean checkClear() {
      if (this.stage >= 21 && this.stage <= 29) {
         return this.killCount >= 6;
      } else if (this.stage >= 31 && this.stage <= 37) {
         if (this.killCount <= 2) {
            this.reservedSpawnedMobTime = System.currentTimeMillis() + 500L;
            return false;
         } else {
            return true;
         }
      } else if (this.stage < 38 || this.stage > 39) {
         return this.killCount >= 1;
      } else if (this.killCount <= 3) {
         this.reservedSpawnedMobTime = System.currentTimeMillis() + 500L;
         return false;
      } else {
         return true;
      }
   }

   public void checkStopClockTime() {
      this.player.updateOneInfo(1234580, "dojang_tick", String.valueOf(this.playTimeTick));
   }

   static {
      mobLists = new int[80];
      hpLists = new long[80];
      int i = 0;
      mobLists[i] = 9305600;
      hpLists[i++] = 5200000L;
      mobLists[i] = 9305601;
      hpLists[i++] = 5740800L;
      mobLists[i] = 9305602;
      hpLists[i++] = 6307200L;
      mobLists[i] = 9305603;
      hpLists[i++] = 6930000L;
      mobLists[i] = 9305604;
      hpLists[i++] = 7549200L;
      mobLists[i] = 9305605;
      hpLists[i++] = 12342000L;
      mobLists[i] = 9305606;
      hpLists[i++] = 13923000L;
      mobLists[i] = 9305607;
      hpLists[i++] = 15105000L;
      mobLists[i] = 9305608;
      hpLists[i++] = 16846000L;
      mobLists[i] = 9305619;
      hpLists[i++] = 100000000L;
      mobLists[i] = 9305610;
      hpLists[i++] = 40824000L;
      mobLists[i] = 9305617;
      hpLists[i++] = 45404550L;
      mobLists[i] = 9305612;
      hpLists[i++] = 48593250L;
      mobLists[i] = 9305611;
      hpLists[i++] = 55350000L;
      mobLists[i] = 9305628;
      hpLists[i++] = 61600500L;
      mobLists[i] = 9305682;
      hpLists[i++] = 68121000L;
      mobLists[i] = 9305683;
      hpLists[i++] = 78840000L;
      mobLists[i] = 9305614;
      hpLists[i++] = 90011250L;
      mobLists[i] = 9305620;
      hpLists[i++] = 97902000L;
      mobLists[i] = 9305609;
      hpLists[i++] = 1500000000L;
      mobLists[i] = 9305623;
      hpLists[i++] = 130536000L;
      mobLists[i] = 9305625;
      hpLists[i++] = 159138000L;
      mobLists[i] = 9305624;
      hpLists[i++] = 190350000L;
      mobLists[i] = 9305684;
      hpLists[i++] = 242424000L;
      mobLists[i] = 9305658;
      hpLists[i++] = 405504000L;
      mobLists[i] = 9305687;
      hpLists[i++] = 497040000L;
      mobLists[i] = 9305616;
      hpLists[i++] = 596496000L;
      mobLists[i] = 9305690;
      hpLists[i++] = 706176000L;
      mobLists[i] = 9305692;
      hpLists[i++] = 824256000L;
      mobLists[i] = 9305629;
      hpLists[i++] = 3000000000L;
      mobLists[i] = 9305630;
      hpLists[i++] = 2108240000L;
      mobLists[i] = 9305631;
      hpLists[i++] = 2526520000L;
      mobLists[i] = 9305659;
      hpLists[i++] = 2976000000L;
      mobLists[i] = 9305633;
      hpLists[i++] = 3464920000L;
      mobLists[i] = 9305621;
      hpLists[i++] = 3986640000L;
      mobLists[i] = 9305632;
      hpLists[i++] = 4551000000L;
      mobLists[i] = 9305694;
      hpLists[i++] = 5149760000L;
      mobLists[i] = 9305634;
      hpLists[i++] = 6474960000L;
      mobLists[i] = 9305656;
      hpLists[i++] = 7971840000L;
      mobLists[i] = 9305639;
      hpLists[i++] = 8000000000L;
      mobLists[i] = 9305660;
      hpLists[i++] = 40000000000L;
      mobLists[i] = 9305661;
      hpLists[i++] = 64000000000L;
      mobLists[i] = 9305627;
      hpLists[i++] = 84000000000L;
      mobLists[i] = 9305622;
      hpLists[i++] = 105000000000L;
      mobLists[i] = 9305662;
      hpLists[i++] = 105000000000L;
      mobLists[i] = 9305635;
      hpLists[i++] = 210000000000L;
      mobLists[i] = 9305636;
      hpLists[i++] = 315000000000L;
      mobLists[i] = 9305637;
      hpLists[i++] = 420000000000L;
      mobLists[i] = 9305638;
      hpLists[i++] = 525000000000L;
      mobLists[i] = 9305695;
      hpLists[i++] = 525000000000L;
      mobLists[i] = 9305696;
      hpLists[i++] = 630000000000L;
      mobLists[i] = 9305663;
      hpLists[i++] = 735000000000L;
      mobLists[i] = 9305664;
      hpLists[i++] = 840000000000L;
      mobLists[i] = 9305665;
      hpLists[i++] = 945000000000L;
      mobLists[i] = 9305666;
      hpLists[i++] = 1050000000000L;
      mobLists[i] = 9305667;
      hpLists[i++] = 1155000000000L;
      mobLists[i] = 9305668;
      hpLists[i++] = 1260000000000L;
      mobLists[i] = 9305669;
      hpLists[i++] = 1365000000000L;
      mobLists[i] = 9305670;
      hpLists[i++] = 1470000000000L;
      mobLists[i] = 9305671;
      hpLists[i++] = 1575000000000L;
      mobLists[i] = 9305697;
      hpLists[i++] = 1680000000000L;
      mobLists[i] = 9305698;
      hpLists[i++] = 1785000000000L;
      mobLists[i] = 9305699;
      hpLists[i++] = 1890000000000L;
      mobLists[i] = 9305700;
      hpLists[i++] = 1995000000000L;
      mobLists[i] = 9305701;
      hpLists[i++] = 2100000000000L;
      mobLists[i] = 9305657;
      hpLists[i++] = 2205000000000L;
      mobLists[i] = 9305702;
      hpLists[i++] = 2310000000000L;
      mobLists[i] = 9305703;
      hpLists[i++] = 2415000000000L;
      mobLists[i] = 9305704;
      hpLists[i++] = 2520000000000L;
      mobLists[i] = 9305705;
      hpLists[i++] = 2100000000000L;
      mobLists[i] = 9305706;
      hpLists[i++] = 2625000000000L;
      mobLists[i] = 9305707;
      hpLists[i++] = 2730000000000L;
      mobLists[i] = 9305708;
      hpLists[i++] = 2850000000000L;
      mobLists[i] = 9305672;
      hpLists[i++] = 3010000000000L;
      mobLists[i] = 9305673;
      hpLists[i++] = 3175000000000L;
      mobLists[i] = 9305674;
      hpLists[i++] = 3340000000000L;
      mobLists[i] = 9305675;
      hpLists[i++] = 3515000000000L;
      mobLists[i] = 9305676;
      hpLists[i++] = 3735000000000L;
      mobLists[i] = 9305677;
      hpLists[i++] = 3905000000000L;
      mobLists[i] = 9305640;
      hpLists[i++] = 20000000000000L;
   }
}
