package objects.users.skills;

import java.awt.Point;
import network.encode.PacketEncoder;

public class ExtraSkillInfo implements Cloneable {
   public int skillID;
   public int delay;

   public ExtraSkillInfo(int skillID, int delay) {
      this.skillID = skillID;
      this.delay = delay;
   }

   public void encode(PacketEncoder packet, Point position, boolean isLeft, int size) {
      packet.writeInt(0);
      packet.writeInt(this.skillID);
      packet.writeInt(position.x);
      packet.writeInt(position.y);
      packet.writeShort(isLeft ? 1 : -1);
      packet.writeInt(this.delay);
      packet.writeInt(size);
      packet.writeInt(size);
      packet.writeInt(0);
   }

   public ExtraSkillInfo clone() {
      try {
         return (ExtraSkillInfo)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }
}
