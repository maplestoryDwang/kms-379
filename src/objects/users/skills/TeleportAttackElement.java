package objects.users.skills;

import java.awt.Point;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackElement {
   public TeleportAttackData data;
   public int switch_;
   public int type;
   public int causeSkill;
   public Point summonPos;

   public TeleportAttackElement(int type, PacketDecoder packet) {
      this.type = type;
      this.decode(packet);
   }

   public void decode(PacketDecoder packet) {
      switch (this.type) {
         case 1:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_DoubleSkill(packet);
            return;
         case 2:
            boolean summon = packet.readByte() != 0;
            if (!summon) {
               return;
            }

            packet.skip(1);
            packet.skip(1);
            this.causeSkill = packet.readInt();
            packet.skip(4);
            packet.skip(1);
            this.summonPos = new Point(packet.readInt(), packet.readInt());
            packet.readInt();
            packet.readInt();
            packet.readInt();
            packet.readInt();
            return;
         case 3:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_Skill(packet);
            return;
         case 4:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_RectPos(packet);
            return;
         case 5:
         case 6:
            this.switch_ = packet.readByte();
            return;
         case 7:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_PointWithDirection(packet);
            return;
         case 8: {
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            TeleportAttackData_Quad quad = new TeleportAttackData_Quad(packet);
            this.data = quad;
         }
            return;
         case 9: {
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            TeleportAttackData_Quad quad = new TeleportAttackData_Quad(packet);
            quad.setObjectID(packet.readInt());
            this.data = quad;
         }
            return;
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
            this.switch_ = packet.readByte();
            return;
         case 15:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            int size = packet.readInt();

            for (int i = 0; i < size; i++) {
               packet.readInt();
               packet.readInt();
               packet.readInt();
               packet.readInt();
            }

            packet.skip(4);
            packet.skip(4);
            packet.skip(4);
            packet.skip(4);
            return;
         case 16:
         case 17:
         case 18:
         case 21:
         case 25:
         case 26:
         case 27:
         case 29:
         case 30:
         case 31:
         case 32:
         case 34:
         case 35:
         case 37:
         case 39:
         case 40:
         case 43:
         case 45:
         case 46:
         default:
            return;
         case 19:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_TripleInt(packet);
            return;
         case 20:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_ListInt(packet);
            return;
         case 22:
            this.switch_ = packet.readByte();
            return;
         case 23:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_Octa(packet);
            return;
         case 24:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_TriArray(packet);
            return;
         case 28:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            packet.readInt();
            return;
         case 33:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_DoubleArray(packet);
            return;
         case 36:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            packet.readInt();
            packet.readInt();
            packet.readInt();
            int damageSize = packet.readInt();
            int consumeID = packet.readInt();
            int beforeX = packet.readInt();
            int beforeY = packet.readInt();
            int afterX = packet.readInt();
            int afterY = packet.readInt();
            packet.readInt();
            packet.readByte();
            packet.readByte();
            packet.readByte();
            return;
         case 38:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            this.data = new TeleportAttackData_Unk(packet);
            return;
         case 41:
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            int count = packet.readInt();

            for (int i = 0; i < count; i++) {
               packet.readInt();
            }

            return;
         case 42: {
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            int unk = packet.readInt();
         }
            return;
         case 44: {
            this.switch_ = packet.readByte();
            if (this.switch_ == 0) {
               return;
            }

            int x = packet.readInt();
            int y = packet.readInt();
            byte unk = packet.readByte();
            int flag1 = packet.readInt();
            int flag2 = packet.readInt();
         }
            return;
         case 47:
         case 48:
            this.switch_ = packet.readByte();
            if (this.switch_ != 0) {
               this.data = new TeleportAttackData_ListLong(packet);
            }
      }
   }

   public void encode(PacketEncoder packet) {
      switch (this.type) {
         case 1:
         case 7:
         case 8:
         case 9:
         case 22:
         case 23:
            packet.write(this.switch_);
            if (this.switch_ == 0) {
               return;
            }

            this.data.encode(packet);
            return;
         case 2:
         case 4:
         case 5:
         case 6:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         case 20:
            packet.write(this.switch_);
            return;
         case 3:
            packet.write(this.switch_);
            if (this.switch_ == 0) {
               return;
            }

            this.data.encode(packet);
            return;
         case 17:
            return;
         case 21:
      }
   }
}
