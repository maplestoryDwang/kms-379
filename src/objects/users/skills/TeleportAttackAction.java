package objects.users.skills;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.utils.Pair;

public class TeleportAttackAction {
   public List<TeleportAttackElement> actions;
   public int crc;

   public void decode(PacketDecoder packet) {
      this.crc = packet.readInt();
      if (packet.readByte() == 1) {
         packet.skip(4);
         this.actions = new ArrayList<>();

         while (true) {
            int num = packet.readInt();
            if (num == -1) {
               break;
            }

            this.actions.add(new TeleportAttackElement(num, packet));
         }
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.crc);
      packet.write(this.actions != null);
      if (this.actions != null) {
         for (TeleportAttackElement action : this.actions) {
            packet.writeInt(action.type);
            action.encode(packet);
         }

         packet.writeInt(-1);
      }
   }

   public static TeleportAttackAction fromRemote(PacketDecoder packet) {
      TeleportAttackAction teleportAttackAction = new TeleportAttackAction();
      teleportAttackAction.decode(packet);
      return teleportAttackAction;
   }

   public Pair<Point, Integer> getPointWithDirection() {
      if (this.actions != null && !this.actions.isEmpty()) {
         for (TeleportAttackElement e : new ArrayList<>(this.actions)) {
            if (e.data instanceof TeleportAttackData_PointWithDirection) {
               TeleportAttackData_PointWithDirection taq = (TeleportAttackData_PointWithDirection)e.data;
               return new Pair<>(new Point(taq.x, taq.y), taq.direction);
            }
         }
      }

      return null;
   }
}
