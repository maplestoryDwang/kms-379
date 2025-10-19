package objects.fields.child.jinhillah;

import java.awt.Point;
import network.encode.PacketEncoder;

public class JinHillahAltar {
   private Point position;
   private int maxKeyInput;
   private int remainKeyInput;

   public JinHillahAltar(Point position, int remainKeyInput) {
      this.position = position;
      this.remainKeyInput = remainKeyInput;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public int getMaxKeyInput() {
      return this.maxKeyInput;
   }

   public void setMaxKeyInput(int maxKeyInput) {
      this.maxKeyInput = maxKeyInput;
   }

   public int getRemainKeyInput() {
      return this.remainKeyInput;
   }

   public void setRemainKeyInput(int remainKeyInput) {
      this.remainKeyInput = remainKeyInput;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.position.x);
      packet.writeInt(this.position.y);
      packet.writeInt(this.remainKeyInput);
   }
}
