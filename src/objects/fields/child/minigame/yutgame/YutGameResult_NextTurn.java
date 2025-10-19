package objects.fields.child.minigame.yutgame;

import network.encode.PacketEncoder;

public class YutGameResult_NextTurn extends YutGameResultEntry {
   private int nextTurnTeam = 0;
   private int unk2 = 0;
   private boolean unk = false;

   public YutGameResult_NextTurn(int nextTurnTeam) {
      this.nextTurnTeam = nextTurnTeam;
   }

   @Override
   public YutGameResultType getType() {
      return YutGameResultType.YutGameUnk;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(YutGameResultType.YutGameUnk.getType());
      packet.writeInt(this.isUnk() ? 4 : 3);
      packet.writeInt(this.getUnk2());
      packet.writeInt(this.getNextTurnTeam());
   }

   public int getNextTurnTeam() {
      return this.nextTurnTeam;
   }

   public void setNextTurnTeam(int nextTurnTeam) {
      this.nextTurnTeam = nextTurnTeam;
   }

   public boolean isUnk() {
      return this.unk;
   }

   public void setUnk(boolean unk) {
      this.unk = unk;
   }

   public int getUnk2() {
      return this.unk2;
   }

   public void setUnk2(int unk2) {
      this.unk2 = unk2;
   }
}
