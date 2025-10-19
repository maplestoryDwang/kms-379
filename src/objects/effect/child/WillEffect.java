package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class WillEffect implements Effect {
   public static final int header = EffectHeader.WillEffect.getValue();
   private final int playerID;
   private final boolean isScreen;
   private final int skillID;
   private final int skillLevel;

   public WillEffect(int playerID, boolean isScreen, int skillID, int skillLevel) {
      this.playerID = playerID;
      this.isScreen = isScreen;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.isScreen);
      packet.writeInt(this.skillID);
      packet.writeInt(this.skillLevel);
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
