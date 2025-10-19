package objects.fields;

import java.util.concurrent.ScheduledFuture;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.PartyMemberEntry;
import objects.context.party.PartySearch;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import objects.users.MapleTrait;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class Event_PyramidSubway {
   private int kill = 0;
   private int cool = 0;
   private int miss = 0;
   private int skill = 0;
   private int type;
   private int energybar = 100;
   private int bar = 0;
   private boolean broaded = false;
   private ScheduledFuture<?> energyBarDecrease;
   private ScheduledFuture<?> timerSchedule;

   public Event_PyramidSubway(final MapleCharacter c) {
      int mapid = c.getMapId();
      if (mapid / 10000 == 91032) {
         this.type = -1;
      } else {
         this.type = mapid % 10000 / 1000;
      }

      if (c.getParty() == null || c.getParty().getLeader().getId() == c.getId()) {
         if (c.getParty() != null && c.getParty().getLeader().getId() == c.getId()) {
            PartySearch ps = Center.Party.getSearch(c.getParty());
            if (ps != null) {
               Center.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest started.");
            }
         }

         this.commenceTimerNextMap(c, 1);
         this.energyBarDecrease = Timer.MapTimer.getInstance()
            .register(
               new Runnable() {
                  @Override
                  public void run() {
                     Event_PyramidSubway.this.energybar = Event_PyramidSubway.this.energybar
                        - (c.getParty() != null && c.getParty().getPartyMemberList().size() > 1 ? 5 : 2);
                     if (Event_PyramidSubway.this.broaded) {
                        c.getMap().respawn(true);
                     } else {
                        Event_PyramidSubway.this.broaded = true;
                     }

                     if (Event_PyramidSubway.this.energybar <= 0) {
                        Event_PyramidSubway.this.fail(c);
                     }
                  }
               },
               1000L
            );
      }
   }

   public final void fullUpdate(MapleCharacter c, int stage) {
      this.broadcastEnergy(c, "massacre_party", c.getParty() == null ? 0 : c.getParty().getPartyMemberList().size());
      this.broadcastEnergy(c, "massacre_miss", this.miss);
      this.broadcastEnergy(c, "massacre_cool", this.cool);
      this.broadcastEnergy(c, "massacre_skill", this.skill);
      this.broadcastEnergy(c, "massacre_laststage", stage - 1);
      this.broadcastEnergy(c, "massacre_hit", this.kill);
      this.broadcastUpdate(c);
   }

   public final void commenceTimerNextMap(final MapleCharacter c, int stage) {
      if (this.timerSchedule != null) {
         this.timerSchedule.cancel(false);
         this.timerSchedule = null;
      }

      Field ourMap = c.getMap();
      int time = (this.type == -1 ? 180 : (stage == 1 ? 240 : 300)) - 1;
      this.energybar = 100;
      this.bar = 0;
      if (c.getParty() != null && c.getParty().getPartyMemberList().size() > 1) {
         for (PartyMemberEntry mpc : c.getParty().getPartyMemberList()) {
            MapleCharacter chr = ourMap.getCharacterById(mpc.getId());
            if (chr != null) {
               chr.getClient().getSession().writeAndFlush(CField.getClock(time));
               chr.getClient().getSession().writeAndFlush(CField.showEffect("killing/first/number/" + stage));
               chr.getClient().getSession().writeAndFlush(CField.showEffect("killing/first/stage"));
               chr.getClient().getSession().writeAndFlush(CField.showEffect("killing/first/start"));
               this.fullUpdate(chr, stage);
            }
         }
      } else {
         c.getClient().getSession().writeAndFlush(CField.getClock(time));
         c.getClient().getSession().writeAndFlush(CField.showEffect("killing/first/number/" + stage));
         c.getClient().getSession().writeAndFlush(CField.showEffect("killing/first/stage"));
         c.getClient().getSession().writeAndFlush(CField.showEffect("killing/first/start"));
         this.fullUpdate(c, stage);
      }

      if (this.type != -1 && (stage == 4 || stage == 5)) {
         for (int i = 0; i < (stage == 4 ? 1 : 2); i++) {
            ourMap.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9700023), c.getPosition());
         }
      }

      this.timerSchedule = Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            boolean ret = false;
            if (Event_PyramidSubway.this.type == -1) {
               ret = Event_PyramidSubway.warpNextMap_Subway(c);
            } else {
               ret = Event_PyramidSubway.warpNextMap_Pyramid(c, Event_PyramidSubway.this.type);
            }

            if (!ret) {
               Event_PyramidSubway.this.fail(c);
            }
         }
      }, time * 1000L);
   }

   public final void onKill(MapleCharacter c) {
      this.kill++;
      this.bar++;
      if (Randomizer.nextInt(100) < 5) {
         this.cool++;
         this.bar++;
         this.broadcastEnergy(c, "massacre_cool", this.cool);
      }

      this.energybar++;
      if (this.energybar > 100) {
         this.energybar = 100;
      }

      if (this.type != -1) {
         for (int i = 5; i >= 1; i--) {
            if ((this.kill + this.cool) % (i * 100) == 0 && Randomizer.nextInt(100) < 20) {
               this.broadcastEffect(c, "killing/yeti" + (i - 1));
               break;
            }
         }

         if ((this.kill + this.cool) % 500 == 0) {
            this.skill++;
            this.broadcastEnergy(c, "massacre_skill", this.skill);
         }
      }

      this.broadcastUpdate(c);
      this.broadcastEnergy(c, "massacre_hit", this.kill);
   }

   public final void onMiss(MapleCharacter c) {
      this.miss++;
      this.energybar -= 5;
      this.broadcastUpdate(c);
      this.broadcastEnergy(c, "massacre_miss", this.miss);
   }

   public final boolean onSkillUse(MapleCharacter c) {
      if (this.skill > 0 && this.type != -1) {
         this.skill--;
         this.broadcastEnergy(c, "massacre_skill", this.skill);
         return true;
      } else {
         return false;
      }
   }

   public final void onChangeMap(MapleCharacter c, int newmapid) {
      if ((newmapid != 910330001 || this.type != -1) && (newmapid != 926020001 + this.type || this.type == -1)) {
         if (this.type == -1 && (newmapid < 910320100 || newmapid > 910320304)) {
            this.dispose(c);
            return;
         }

         if (this.type != -1 && (newmapid < 926010100 || newmapid > 926013504)) {
            this.dispose(c);
            return;
         }

         if (c.getParty() == null || c.getParty().getLeader().getId() == c.getId()) {
            this.energybar = 100;
            this.commenceTimerNextMap(c, newmapid % 1000 / 100);
         }
      } else {
         this.succeed(c);
      }
   }

   public final void succeed(MapleCharacter c) {
      MapleQuestStatus record = c.getQuestIfNullAdd(MapleQuest.getInstance(this.type == -1 ? 7662 : 7760));
      String data = record.getCustomData();
      if (data == null) {
         record.setCustomData("0");
         data = record.getCustomData();
      }

      int mons = Integer.parseInt(data);
      int tk = this.kill + this.cool;
      record.setCustomData(String.valueOf(mons + tk));
      byte rank = 4;
      if (this.type == -1) {
         if (tk >= 2000) {
            rank = 0;
         } else if (tk >= 1500 && tk <= 1999) {
            rank = 1;
         } else if (tk >= 1000 && tk <= 1499) {
            rank = 2;
         } else if (tk >= 500 && tk <= 999) {
            rank = 3;
         }
      } else if (tk >= 3000) {
         rank = 0;
      } else if (tk >= 2000 && tk <= 2999) {
         rank = 1;
      } else if (tk >= 1500 && tk <= 1999) {
         rank = 2;
      } else if (tk >= 500 && tk <= 1499) {
         rank = 3;
      }

      int pt;
      pt = 0;
      label77:
      switch (this.type) {
         case 0:
            switch (rank) {
               case 0:
                  pt = 30500;
                  break label77;
               case 1:
                  pt = 25000;
                  break label77;
               case 2:
                  pt = 21750;
                  break label77;
               case 3:
                  pt = 12000;
               default:
                  break label77;
            }
         case 1:
            switch (rank) {
               case 0:
                  pt = 36000;
                  break label77;
               case 1:
                  pt = 30000;
                  break label77;
               case 2:
                  pt = 25750;
                  break label77;
               case 3:
                  pt = 13000;
               default:
                  break label77;
            }
         case 2:
            switch (rank) {
               case 0:
                  pt = 36500;
                  break label77;
               case 1:
                  pt = 35000;
                  break label77;
               case 2:
                  pt = 27250;
                  break label77;
               case 3:
                  pt = 14000;
               default:
                  break label77;
            }
         case 3:
            switch (rank) {
               case 0:
                  pt = 47000;
                  break label77;
               case 1:
                  pt = 40000;
                  break label77;
               case 2:
                  pt = 29500;
                  break label77;
               case 3:
                  pt = 18000;
               default:
                  break label77;
            }
         default:
            switch (rank) {
               case 0:
                  pt = 12000;
                  break;
               case 1:
                  pt = 9000;
                  break;
               case 2:
                  pt = 5750;
                  break;
               case 3:
                  pt = 3000;
            }
      }

      int exp = 0;
      if (rank < 4) {
         exp = (int)((this.kill + this.cool * 5 + pt) * c.getClient().getChannelServer().getExpRate());
         c.gainExp(exp, true, false, false);
      }

      c.getTrait(MapleTrait.MapleTraitType.will).addExp((this.type + 2) * 8, c);
      c.getClient().getSession().writeAndFlush(CField.showEffect("killing/clear"));
      c.getClient().getSession().writeAndFlush(CField.sendPyramidResult(rank, exp));
      this.dispose(c);
   }

   public final void fail(MapleCharacter c) {
      Field map;
      if (this.type == -1) {
         map = c.getClient().getChannelServer().getMapFactory().getMap(910320001);
      } else {
         map = c.getClient().getChannelServer().getMapFactory().getMap(926010001 + this.type);
      }

      changeMap(c, map, 1, 200, 2);
      this.dispose(c);
   }

   public final void dispose(MapleCharacter c) {
      boolean lead = this.energyBarDecrease != null && this.timerSchedule != null;
      if (this.energyBarDecrease != null) {
         this.energyBarDecrease.cancel(false);
         this.energyBarDecrease = null;
      }

      if (this.timerSchedule != null) {
         this.timerSchedule.cancel(false);
         this.timerSchedule = null;
      }

      if (c.getParty() != null && lead && c.getParty().getPartyMemberList().size() > 1) {
         this.fail(c);
      } else {
         c.setPyramidSubway(null);
      }
   }

   public final void broadcastUpdate(MapleCharacter c) {
      c.getClient().getSession().writeAndFlush(CField.sendPyramidUpdate(this.bar));
   }

   public final void broadcastEffect(MapleCharacter c, String effect) {
      c.getClient().getSession().writeAndFlush(CField.showEffect(effect));
   }

   public final void broadcastEnergy(MapleCharacter c, String type, int amount) {
      c.getClient().getSession().writeAndFlush(CWvsContext.sendPyramidEnergy(type, String.valueOf(amount)));
   }

   public static boolean warpStartSubway(MapleCharacter c) {
      int mapid = 910320100;
      GameServer ch = c.getClient().getChannelServer();

      for (int i = 0; i < 5; i++) {
         Field map = ch.getMapFactory().getMap(910320100 + i);
         if (map.getCharactersSize() == 0) {
            clearMap(map, false);
            changeMap(c, map, 25, 30);
            return true;
         }
      }

      return false;
   }

   public static boolean warpBonusSubway(MapleCharacter c) {
      int mapid = 910320010;
      GameServer ch = c.getClient().getChannelServer();

      for (int i = 0; i < 20; i++) {
         Field map = ch.getMapFactory().getMap(910320010 + i);
         if (map.getCharactersSize() == 0) {
            clearMap(map, false);
            c.changeMap(map, map.getPortal(0));
            return true;
         }
      }

      return false;
   }

   public static boolean warpNextMap_Subway(MapleCharacter c) {
      int currentmap = c.getMapId();
      int thisStage = (currentmap - 910320100) / 100;
      Field map = c.getMap();
      clearMap(map, true);
      GameServer ch = c.getClient().getChannelServer();
      if (thisStage >= 2) {
         map = ch.getMapFactory().getMap(910330001);
         changeMap(c, map, 1, 200, 1);
         return true;
      } else {
         int nextmapid = 910320100 + (thisStage + 1) * 100;

         for (int i = 0; i < 5; i++) {
            map = ch.getMapFactory().getMap(nextmapid + i);
            if (map.getCharactersSize() == 0) {
               clearMap(map, false);
               changeMap(c, map, 1, 200, 1);
               return true;
            }
         }

         return false;
      }
   }

   public static boolean warpStartPyramid(MapleCharacter c, int difficulty) {
      int mapid = 926010100 + difficulty * 1000;
      int minLevel = 40;
      int maxLevel = 60;
      switch (difficulty) {
         case 1:
            minLevel = 45;
            break;
         case 2:
            minLevel = 50;
            break;
         case 3:
            minLevel = 61;
            maxLevel = 200;
      }

      GameServer ch = c.getClient().getChannelServer();

      for (int i = 0; i < 5; i++) {
         Field map = ch.getMapFactory().getMap(mapid + i);
         if (map.getCharactersSize() == 0) {
            clearMap(map, false);
            changeMap(c, map, minLevel, maxLevel);
            return true;
         }
      }

      return false;
   }

   public static boolean warpBonusPyramid(MapleCharacter c, int difficulty) {
      int mapid = 926010010 + difficulty * 20;
      GameServer ch = c.getClient().getChannelServer();

      for (int i = 0; i < 20; i++) {
         Field map = ch.getMapFactory().getMap(mapid + i);
         if (map.getCharactersSize() == 0) {
            clearMap(map, false);
            c.changeMap(map, map.getPortal(0));
            return true;
         }
      }

      return false;
   }

   public static boolean warpNextMap_Pyramid(MapleCharacter c, int difficulty) {
      int currentmap = c.getMapId();
      int thisStage = (currentmap - (926010100 + difficulty * 1000)) / 100;
      Field map = c.getMap();
      clearMap(map, true);
      GameServer ch = c.getClient().getChannelServer();
      if (thisStage >= 4) {
         map = ch.getMapFactory().getMap(926020001 + difficulty);
         changeMap(c, map, 1, 200, 1);
         return true;
      } else {
         int nextmapid = 926010100 + (thisStage + 1) * 100 + difficulty * 1000;

         for (int i = 0; i < 5; i++) {
            map = ch.getMapFactory().getMap(nextmapid + i);
            if (map.getCharactersSize() == 0) {
               clearMap(map, false);
               changeMap(c, map, 1, 200, 1);
               return true;
            }
         }

         return false;
      }
   }

   private static final void changeMap(MapleCharacter c, Field map, int minLevel, int maxLevel) {
      changeMap(c, map, minLevel, maxLevel, 0);
   }

   private static final void changeMap(MapleCharacter c, Field map, int minLevel, int maxLevel, int clear) {
      Field oldMap = c.getMap();
      if (c.getParty() != null && c.getParty().getPartyMemberList().size() > 1) {
         for (PartyMemberEntry mpc : c.getParty().getPartyMemberList()) {
            MapleCharacter chr = oldMap.getCharacterById(mpc.getId());
            if (chr != null && chr.getId() != c.getId() && chr.getLevel() >= minLevel && chr.getLevel() <= maxLevel) {
               if (clear == 1) {
                  chr.getClient().getSession().writeAndFlush(CField.showEffect("killing/clear"));
               } else if (clear == 2) {
                  chr.getClient().getSession().writeAndFlush(CField.showEffect("killing/fail"));
               }

               chr.changeMap(map, map.getPortal(0));
            }
         }
      }

      if (clear == 1) {
         c.getClient().getSession().writeAndFlush(CField.showEffect("killing/clear"));
      } else if (clear == 2) {
         c.getClient().getSession().writeAndFlush(CField.showEffect("killing/fail"));
      }

      c.changeMap(map, map.getPortal(0));
   }

   private static final void clearMap(Field map, boolean check) {
      if (!check || map.getCharactersSize() <= 0) {
         map.resetFully(false);
      }
   }
}
