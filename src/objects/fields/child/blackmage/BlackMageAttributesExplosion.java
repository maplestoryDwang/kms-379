package objects.fields.child.blackmage;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class BlackMageAttributesExplosion {
   private List<BlackMageAttributesExplosion.AttributesExplosionEntry> entrys = new ArrayList<>();

   public BlackMageAttributesExplosion() {
      int size = Randomizer.rand(1, 4);

      for (int i = 0; i < size; i++) {
         Point pos = new Point(Randomizer.rand(-690, 710), Randomizer.rand(-200, 200));
         this.entrys
            .add(
               new BlackMageAttributesExplosion.AttributesExplosionEntry(
                  i == 0 ? 0 : 550, new Rect(pos.x - 170, pos.y - 170, pos.x + 170, pos.y + 170), BlackMageAttributesExplosion.ExplosionType.getRandom()
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

   public class AttributesExplosionEntry {
      private int delay;
      private Rect rect;
      private BlackMageAttributesExplosion.ExplosionType type;

      public AttributesExplosionEntry(int delay, Rect rect, BlackMageAttributesExplosion.ExplosionType type) {
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

      public BlackMageAttributesExplosion.ExplosionType getType() {
         return this.type;
      }

      public void setType(BlackMageAttributesExplosion.ExplosionType type) {
         this.type = type;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.rect.getLeft());
         packet.writeInt(this.rect.getTop());
         packet.writeInt(this.rect.getRight());
         packet.writeInt(this.rect.getBottom());
         packet.write(this.type.getType());
         packet.writeZeroBytes(3);
         packet.writeInt(this.delay);
      }
   }

   public static enum ExplosionType {
      Creation(0),
      Destruction(1);

      private int type;

      private ExplosionType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static BlackMageAttributesExplosion.ExplosionType get(int type) {
         for (BlackMageAttributesExplosion.ExplosionType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }

      public static BlackMageAttributesExplosion.ExplosionType getRandom() {
         return get(Randomizer.rand(0, 1));
      }
   }
}
