package objects.fields.child.karrotte.guardian;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;

public class MysticShot {
   public static AtomicInteger SN = new AtomicInteger(0);
   private int sn;
   private int playerID;
   private int createDelay;
   private int tick;
   private Point position;

   public MysticShot(int playerID, int createDelay, int tick, Point position) {
      this.playerID = playerID;
      this.createDelay = createDelay;
      this.tick = tick;
      this.position = position;
      this.sn = SN.getAndAdd(1);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.sn);
      packet.writeInt(this.playerID);
      packet.writeInt(this.createDelay);
      packet.writeInt(this.tick);
      packet.writeInt(this.position.x);
      packet.writeInt(this.position.y);
   }
}
