package objects.fields.child.minigame.yutgame;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class YutGameResult_Action extends YutGameResultEntry {
   private YutGameResult_Action.ActionEntry entry = null;

   public YutGameResult_Action(YutGameResult_Action.ActionEntry entry) {
      this.entry = entry;
   }

   @Override
   public YutGameResultType getType() {
      return YutGameResultType.YutGameAction;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(YutGameResultType.YutGameAction.getType());
      this.entry.encode(packet);
   }

   public static class ActionEntry {
      private YutGameResult_Action.ActionType type;
      private int team;
      private List<Integer> additional;

      public ActionEntry(YutGameResult_Action.ActionType type, int team) {
         this.type = type;
         this.team = team;
         this.additional = new ArrayList<>();
      }

      public void addAdditional(int value) {
         this.additional.add(value);
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.type.getType());
         packet.writeInt(this.team);
         packet.writeInt(this.additional.size());
         this.additional.forEach(a -> packet.writeInt(a));
      }
   }

   public static enum ActionType {
      Unk(1),
      GameSet(2),
      Unk2(4),
      ActiveItem(5),
      NextTurn(6),
      GameResult(9),
      RemainTurn(10),
      InstallItem(11);

      private int type;

      private ActionType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static enum GameSetType {
      ThrowYut(1),
      MovePiece(2),
      FirstItemSet(3);

      private int type;

      private GameSetType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static enum InstallItemType {
      FinishLineFront(1),
      Bomb(2);

      private int type;

      private InstallItemType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
