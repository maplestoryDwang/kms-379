package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class SpecialEffect implements Effect {
   public static final int header = EffectHeader.SpecialEffect.getValue();
   private final int playerID;
   private final boolean exlc;
   private final int duration;
   private final int mode;
   private final int itemID;
   private final String effectURL;

   public SpecialEffect(int playerID, boolean exlc, int duration, int mode, int itemID, String effectURL) {
      this.playerID = playerID;
      this.exlc = exlc;
      this.duration = duration;
      this.mode = mode;
      this.itemID = itemID;
      this.effectURL = effectURL;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.effectURL);
      packet.write(this.exlc);
      packet.writeInt(this.duration);
      packet.writeInt(this.mode);
      if (this.mode == 2) {
         packet.writeInt(this.itemID);
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
