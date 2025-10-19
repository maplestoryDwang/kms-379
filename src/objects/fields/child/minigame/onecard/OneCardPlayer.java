package objects.fields.child.minigame.onecard;

import java.util.ArrayList;
import java.util.List;
import objects.users.MapleCharacter;

public class OneCardPlayer {
   private final MapleCharacter player;
   private final int position;
   private final List<OneCardGameCardInfo> deckInfo = new ArrayList<>();
   private boolean isDead;

   public OneCardPlayer(MapleCharacter player, int position) {
      this.player = player;
      this.position = position;
      this.isDead = false;
   }

   public void addCard(OneCardGameCardInfo card) {
      this.deckInfo.add(card);
   }

   public void removeCard(OneCardGameCardInfo card) {
      this.deckInfo.remove(card);
   }

   public MapleCharacter getPlayer() {
      return this.player;
   }

   public int getPOS() {
      return this.position;
   }

   public List<OneCardGameCardInfo> getDeckInfo() {
      return this.deckInfo;
   }

   public boolean getIsDead() {
      return this.isDead;
   }

   public void userDead() {
      this.isDead = true;
   }
}
