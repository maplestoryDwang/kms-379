package objects.effect.child;

import java.awt.Point;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;
import objects.utils.Pair;

public class FlashMirageEffect implements Effect {
   public static final int header = EffectHeader.FlashMirage.getValue();
   private final int playerID;
   private final int skillID;
   private final List<Pair<Point, Integer>> pointList;

   public FlashMirageEffect(int playerID, int skillID, List<Pair<Point, Integer>> pointList) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.pointList = pointList;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(3111015);
      packet.writeInt(this.playerID);
      packet.writeInt(this.pointList.size());

      for (Pair<Point, Integer> data : this.pointList) {
         packet.writeInt(data.left.x);
         packet.writeInt(data.left.y);
         packet.write(data.right);
         packet.writeInt(this.pointList.indexOf(data));
         packet.writeInt(0);
      }
   }

   @Override
   public byte[] encodeForLocal() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
      packet.write(header);
      this.encode(packet);
      return packet.getPacket();
   }

   @Override
   public byte[] encodeForRemote() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
      packet.writeInt(this.playerID);
      packet.write(header);
      this.encode(packet);
      return packet.getPacket();
   }
}
