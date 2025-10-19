package objects.fields.child.blackmage;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class BlackMageWelding {
   private List<BlackMageWelding.BlackMageWeldingEntry> entrys = new ArrayList<>();

   public BlackMageWelding() {
      int startX = Randomizer.rand(-935, -681);
      int size = Randomizer.rand(14, 16);

      for (int i = 0; i < size; i++) {
         this.entrys
            .add(
               new BlackMageWelding.BlackMageWeldingEntry(
                  i == 0 ? 2700 : 0, new Rect(startX + i * 120, -404, startX + 120 + i * 120, 238), BlackMageWelding.WeldingType.getRandom()
               )
            );
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.entrys.size());
      packet.writeInt(2700);
      packet.write(1);
      this.entrys.forEach(e -> e.encode(packet));
   }

   public class BlackMageWeldingEntry {
      private int delay;
      private Rect rect;
      private BlackMageWelding.WeldingType type;

      public BlackMageWeldingEntry(int delay, Rect rect, BlackMageWelding.WeldingType type) {
         this.delay = delay;
         this.rect = rect;
         this.type = type;
      }

      public int getDelay() {
         return this.delay;
      }

      public void setDelay(int delay) {
         this.delay = delay;
      }

      public Rect getRect() {
         return this.rect;
      }

      public void setRect(Rect rect) {
         this.rect = rect;
      }

      public BlackMageWelding.WeldingType getType() {
         return this.type;
      }

      public void setType(BlackMageWelding.WeldingType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.rect.getLeft());
         packet.writeInt(this.rect.getTop());
         packet.writeInt(this.rect.getRight());
         packet.writeInt(this.rect.getBottom());
         packet.write(this.type.getType());
         packet.writeZeroBytes(7);
      }
   }

   public static enum WeldingType {
      UpToDownBlack(0),
      UpToDownWhite(1),
      DownToUpBlack(2),
      DownToUpWhite(3);

      private int type;

      private WeldingType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static BlackMageWelding.WeldingType get(int type) {
         for (BlackMageWelding.WeldingType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }

      public static BlackMageWelding.WeldingType getRandom() {
         return get(Randomizer.rand(0, 3));
      }
   }
}
