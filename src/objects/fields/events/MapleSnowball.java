package objects.fields.events;

import java.util.concurrent.ScheduledFuture;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Timer;

public class MapleSnowball extends MapleEvent {
   private MapleSnowball.MapleSnowballs[] balls = new MapleSnowball.MapleSnowballs[2];

   public MapleSnowball(int channel, MapleEventType type) {
      super(channel, type);
   }

   @Override
   public void finished(MapleCharacter chr) {
   }

   @Override
   public void unreset() {
      super.unreset();

      for (int i = 0; i < 2; i++) {
         this.getSnowBall(i).resetSchedule();
         this.resetSnowBall(i);
      }
   }

   @Override
   public void reset() {
      super.reset();
      this.makeSnowBall(0);
      this.makeSnowBall(1);
   }

   @Override
   public void startEvent() {
      for (int i = 0; i < 2; i++) {
         MapleSnowball.MapleSnowballs ball = this.getSnowBall(i);
         ball.broadcast(this.getMap(0), 0);
         ball.setInvis(false);
         ball.broadcast(this.getMap(0), 5);
         this.getMap(0).broadcastMessage(CField.enterSnowBall());
      }
   }

   public void resetSnowBall(int teamz) {
      this.balls[teamz] = null;
   }

   public void makeSnowBall(int teamz) {
      this.resetSnowBall(teamz);
      this.balls[teamz] = new MapleSnowball.MapleSnowballs(teamz);
   }

   public MapleSnowball.MapleSnowballs getSnowBall(int teamz) {
      return this.balls[teamz];
   }

   public static class MapleSnowballs {
      private int position = 0;
      private final int team;
      private int startPoint = 0;
      private boolean invis = true;
      private boolean hittable = true;
      private int snowmanhp = 7500;
      private ScheduledFuture<?> snowmanSchedule = null;

      public MapleSnowballs(int team_) {
         this.team = team_;
      }

      public void resetSchedule() {
         if (this.snowmanSchedule != null) {
            this.snowmanSchedule.cancel(false);
            this.snowmanSchedule = null;
         }
      }

      public int getTeam() {
         return this.team;
      }

      public int getPosition() {
         return this.position;
      }

      public void setPositionX(int pos) {
         this.position = pos;
      }

      public void setStartPoint(Field map) {
         this.startPoint++;
         this.broadcast(map, this.startPoint);
      }

      public boolean isInvis() {
         return this.invis;
      }

      public void setInvis(boolean i) {
         this.invis = i;
      }

      public boolean isHittable() {
         return this.hittable && !this.invis;
      }

      public void setHittable(boolean b) {
         this.hittable = b;
      }

      public int getSnowmanHP() {
         return this.snowmanhp;
      }

      public void setSnowmanHP(int shp) {
         this.snowmanhp = shp;
      }

      public void broadcast(Field map, int message) {
         for (MapleCharacter chr : map.getCharactersThreadsafe()) {
            chr.getClient().getSession().writeAndFlush(CField.snowballMessage(this.team, message));
         }
      }

      public int getLeftX() {
         return this.position * 3 + 175;
      }

      public int getRightX() {
         return this.getLeftX() + 275;
      }

      public static final void hitSnowball(MapleCharacter chr) {
         int team = chr.getTruePosition().y > -80 ? 0 : 1;
         MapleSnowball sb = (MapleSnowball)chr.getClient().getChannelServer().getEvent(MapleEventType.Snowball);
         MapleSnowball.MapleSnowballs ball = sb.getSnowBall(team);
         if (ball != null && !ball.isInvis()) {
            boolean snowman = chr.getTruePosition().x < -360 && chr.getTruePosition().x > -560;
            if (!snowman) {
               int damage = (Math.random() < 0.01 || chr.getTruePosition().x > ball.getLeftX() && chr.getTruePosition().x < ball.getRightX())
                     && ball.isHittable()
                  ? 10
                  : 0;
               chr.getMap().broadcastMessage(CField.hitSnowBall(team, damage, 0, 1));
               if (damage == 0) {
                  if (Math.random() < 0.2) {
                     chr.getClient().getSession().writeAndFlush(CField.leftKnockBack());
                     chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
                  }
               } else {
                  ball.setPositionX(ball.getPosition() + 1);
                  if (ball.getPosition() == 255 || ball.getPosition() == 511 || ball.getPosition() == 767) {
                     ball.setStartPoint(chr.getMap());
                     chr.getMap().broadcastMessage(CField.rollSnowball(4, sb.getSnowBall(0), sb.getSnowBall(1)));
                  } else if (ball.getPosition() == 899) {
                     Field map = chr.getMap();

                     for (int i = 0; i < 2; i++) {
                        sb.getSnowBall(i).setInvis(true);
                        map.broadcastMessage(CField.rollSnowball(i + 2, sb.getSnowBall(0), sb.getSnowBall(1)));
                     }

                     chr.getMap()
                        .broadcastMessage(
                           CWvsContext.serverNotice(6, "Congratulations! Team " + (team == 0 ? "Story" : "Maple") + " has won the Snowball Event!")
                        );

                     for (MapleCharacter chrz : chr.getMap().getCharactersThreadsafe()) {
                        if (team == 0 && chrz.getTruePosition().y > -80 || team == 1 && chrz.getTruePosition().y <= -80) {
                           MapleSnowball.givePrize(chrz);
                        }

                        sb.warpBack(chrz);
                     }

                     sb.unreset();
                  } else if (ball.getPosition() < 899) {
                     chr.getMap().broadcastMessage(CField.rollSnowball(4, sb.getSnowBall(0), sb.getSnowBall(1)));
                     ball.setInvis(false);
                  }
               }
            } else if (ball.getPosition() < 899) {
               int damage = 15;
               if (Math.random() < 0.3) {
                  damage = 0;
               }

               if (Math.random() < 0.05) {
                  damage = 45;
               }

               chr.getMap().broadcastMessage(CField.hitSnowBall(team + 2, damage, 0, 0));
               ball.setSnowmanHP(ball.getSnowmanHP() - damage);
               if (damage > 0) {
                  chr.getMap().broadcastMessage(CField.rollSnowball(0, sb.getSnowBall(0), sb.getSnowBall(1)));
                  if (ball.getSnowmanHP() <= 0) {
                     ball.setSnowmanHP(7500);
                     final MapleSnowball.MapleSnowballs oBall = sb.getSnowBall(team == 0 ? 1 : 0);
                     oBall.setHittable(false);
                     final Field map = chr.getMap();
                     oBall.broadcast(map, 4);
                     oBall.snowmanSchedule = Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           oBall.setHittable(true);
                           oBall.broadcast(map, 5);
                        }
                     }, 10000L);

                     for (MapleCharacter chrz : chr.getMap().getCharactersThreadsafe()) {
                        if (ball.getTeam() == 0 && chr.getTruePosition().y < -80 || ball.getTeam() == 1 && chr.getTruePosition().y > -80) {
                           chrz.giveDebuff(SecondaryStatFlag.Attract, MobSkillFactory.getMobSkill(128, 1));
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
