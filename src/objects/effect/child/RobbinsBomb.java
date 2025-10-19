package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class RobbinsBomb implements Effect {
   public static final int header = EffectHeader.RobbinsBomb.getValue();
   private final int playerID;
   private final boolean reset;
   private final boolean numberOnly;
   private final int count;

   public RobbinsBomb(int playerID, boolean reset, int count, boolean numberOnly) {
      this.playerID = playerID;
      this.reset = reset;
      this.count = count;
      this.numberOnly = numberOnly;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.reset);
      if (!this.reset) {
         packet.writeInt(this.count);
         packet.write(this.numberOnly);
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
