package objects.fields.child.karing.Gauge;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class KaringGauge {
   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.KARING_GAUGE.getValue());
   }

   public void sendPacket(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      player.send(packet.getPacket());
   }

   public void broadcastPacket(Field field) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      field.broadcastMessage(packet.getPacket());
   }

   public static class InitPacket extends KaringGauge {
      public static class MentalityGauge extends KaringGauge.InitPacket {
         int memtality;

         public MentalityGauge(KaringGaugeEntry entry) {
            this.memtality = entry.getMemtality();
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(KaringGauge.karingGaugeType.Mentality.getType());
            packet.writeInt(Math.min(this.memtality, 1000));
         }
      }

      public static class PerilsGauge extends KaringGauge.InitPacket {
         int goongiGauge;
         int doolGauge;
         int hondonGauge;
         boolean goongiClear;
         boolean doolClear;
         boolean hondonClear;

         public PerilsGauge(KaringGaugeEntry entry) {
            this.goongiGauge = entry.getGoongiGauge();
            this.doolGauge = entry.getDoolGauge();
            this.hondonGauge = entry.getHondonGauge();
            this.goongiClear = entry.isGoongiClear();
            this.doolClear = entry.isDoolClear();
            this.hondonClear = entry.isHondonClear();
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(KaringGauge.karingGaugeType.Season.getType());
            packet.writeInt(Math.min(this.goongiGauge, 1000));
            packet.write(this.goongiClear);
            packet.writeInt(Math.min(this.doolGauge, 1000));
            packet.write(this.doolClear);
            packet.writeInt(Math.min(this.hondonGauge, 1000));
            packet.write(this.hondonClear);
         }
      }
   }

   public static enum karingGaugeType {
      Mentality(0),
      Season(1);

      private int type;

      private karingGaugeType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static KaringGauge.karingGaugeType getType(int type) {
         for (KaringGauge.karingGaugeType t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
