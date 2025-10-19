package objects.fields.child.will;

import network.encode.PacketEncoder;
import objects.utils.Rect;

public class WillBeholder {
   private WillBeholder.BeholderEntry entry;

   public WillBeholder(WillBeholder.BeholderEntry entry) {
      this.entry = entry;
   }

   public void encode(PacketEncoder packet) {
      this.entry.encode(packet);
   }

   public static class BeholderEntry {
      private WillBeholder.BeholderType type;
      private int mobTemplateID;

      public BeholderEntry(WillBeholder.BeholderType type, int mobTemplateID) {
         this.type = type;
         this.mobTemplateID = mobTemplateID;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.type.getType());
         packet.writeInt(this.mobTemplateID);
      }
   }

   public static enum BeholderType {
      Gen(0),
      Straight(1);

      private int type;

      private BeholderType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static class Gen extends WillBeholder.BeholderEntry {
      private int x;
      private int ry;

      public Gen(int mobTemplateID, int x, int ry) {
         super(WillBeholder.BeholderType.Gen, mobTemplateID);
         this.x = x;
         this.ry = ry;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.x);
         packet.writeInt(this.ry);
      }
   }

   public static class Straight extends WillBeholder.BeholderEntry {
      private int shootAngle;
      private int shootSpeed;
      private int shootInterval;
      private int shootCount;
      private Rect rect;

      public Straight(int mobTemplateID, int shootAngle, int shootSpeed, int shootInterval, int shootCount, Rect rect) {
         super(WillBeholder.BeholderType.Straight, mobTemplateID);
         this.shootAngle = shootAngle;
         this.shootSpeed = shootSpeed;
         this.shootInterval = shootInterval;
         this.shootCount = shootCount;
         this.rect = rect;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.shootAngle);
         packet.writeInt(this.shootSpeed);
         packet.writeInt(this.shootInterval);
         packet.writeInt(this.shootCount);
         packet.write(1);
         packet.writeInt(this.rect.getLeft());
         packet.writeInt(this.rect.getTop());
         packet.writeInt(this.rect.getRight());
         packet.writeInt(this.rect.getBottom());
      }
   }
}
