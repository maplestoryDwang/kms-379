package objects.fields.child.minigame.yutgame;

public enum YutGameResultType {
   YutGameUnk(1),
   YutGameInstallItem(2),
   YutGameInfo(3),
   YutGameAction(4);

   private int type;

   private YutGameResultType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
