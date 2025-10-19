package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class WZEffectBased2 implements Effect {
   public static final int header = EffectHeader.WZEffectBased2.getValue();
   private final int playerID;
   private final boolean isUsed;
   private final int posX;
   private final int posY;
   private final String data;

   public WZEffectBased2(int playerID, boolean isUsed, int posX, int posY, String data) {
      this.playerID = playerID;
      this.isUsed = isUsed;
      this.posX = posX;
      this.posY = posY;
      this.data = data;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.isUsed);
      if (this.isUsed) {
         packet.writeInt(this.posX);
         packet.writeInt(this.posY);
         packet.writeMapleAsciiString(this.data);
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
