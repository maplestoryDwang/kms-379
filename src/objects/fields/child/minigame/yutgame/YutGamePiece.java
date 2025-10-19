package objects.fields.child.minigame.yutgame;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class YutGamePiece {
   private int index;
   private int position;
   private boolean hidden;
   private int team;
   private List<Integer> carryIndex;

   public YutGamePiece(int index, int team) {
      this.setIndex(index);
      this.position = 0;
      this.team = team;
      this.setHidden(false);
      this.carryIndex = new ArrayList<>();
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getPosition());
      packet.write(this.isHidden());
      packet.writeInt(this.isHidden() ? 1 : 0);
      packet.writeInt(this.carryIndex.size());
      this.carryIndex.forEach(c -> packet.writeInt(c));
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public void addCarryIndex(int index) {
      boolean f = false;

      for (int i : this.carryIndex) {
         if (i == index) {
            f = true;
            break;
         }
      }

      if (!f) {
         this.carryIndex.add(index);
      }
   }

   public void clearCarryIndex() {
      this.carryIndex.clear();
   }

   public List<Integer> getCarryIndex() {
      return new ArrayList<>(this.carryIndex);
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public int getTeam() {
      return this.team;
   }

   public void setTeam(int team) {
      this.team = team;
   }
}
