package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class UnknownMovement2 implements LifeMovementFragment {
   private int type;
   private int unk;
   private int unk2;
   private int unk3;
   private int unk4;
   private int unk5;
   private int newState;
   private int duration;
   private int force;

   public UnknownMovement2(int type, int unk, int unk2, int unk3, int unk4, int unk5, int newState, int duration, int force) {
      this.type = type;
      this.unk = unk;
      this.unk2 = unk2;
      this.unk3 = unk3;
      this.unk4 = unk4;
      this.unk5 = unk5;
      this.newState = newState;
      this.duration = duration;
      this.force = force;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.write(this.type);
      packet.writeShort(this.unk);
      packet.writeShort(this.unk2);
      packet.writeShort(this.unk3);
      packet.writeShort(this.unk4);
      packet.writeShort(this.unk5);
      packet.write(this.newState);
      packet.writeShort(this.duration);
      packet.write(this.force);
   }

   @Override
   public Point getPosition() {
      return new Point(0, 0);
   }
}
