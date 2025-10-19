package objects.effect.child;

import java.awt.Point;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class HarmonyLInkEffect implements Effect {
   public static final int header = EffectHeader.HarmonyLink.getValue();
   private final int playerID;
   private final int skillID;
   private final Point summonPosition;
   private final Point playerPosition;

   public HarmonyLInkEffect(int playerID, int skillID, Point summonPosition, Point playerPosition) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.summonPosition = summonPosition;
      this.playerPosition = playerPosition;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.encodePos(this.summonPosition);
      packet.encodePos(this.playerPosition);
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
