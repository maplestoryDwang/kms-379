package objects.fields.child.minigame.battlereverse;

import objects.users.MapleCharacter;

public class BattleReversePlayer {
   private int score;
   private final int team;
   private final MapleCharacter player;

   public BattleReversePlayer(int team, MapleCharacter player) {
      this.team = team;
      this.score = 0;
      this.player = player;
   }

   public int getScore() {
      return this.score;
   }

   public void setScore(int score) {
      this.score = score;
   }

   public int getTeam() {
      return this.team;
   }

   public MapleCharacter getPlayer() {
      return this.player;
   }
}
