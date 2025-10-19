package objects.fields.child.jinhillah;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class JinHillahThread {
   private static AtomicInteger SN = new AtomicInteger(0);
   private int objectID;
   private int skillID;
   private int skillLevel;
   private int createDelay;
   private Point position;
   private List<Rect> threadRects;

   public JinHillahThread(int skillID, int skillLevel, int createDelay, Point position) {
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.createDelay = createDelay;
      this.position = position;
      this.objectID = SN.getAndAdd(1);
      List<Rect> rand = Field_JinHillah.threadRect.get(Randomizer.rand(0, Field_JinHillah.threadRect.size() - 1));
      this.threadRects = new ArrayList<>();

      for (int i = 0; i < rand.size(); i++) {
         Rect r = rand.get(i);
         this.threadRects.add(r);
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(1);
      packet.writeInt(this.objectID);
      packet.writeInt(this.skillID);
      packet.writeInt(this.skillLevel);
      packet.write(true);
      packet.writeInt(1);
      packet.encodePos(this.position);
      packet.writeInt(this.createDelay);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(this.threadRects.size());

      for (Rect rect : this.threadRects) {
         rect.encode(packet);
      }
   }
}
