package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class PetLevelUp implements Effect {
   public static final int header = EffectHeader.PetLevelUp.getValue();
   private final int playerID;
   private final int msgType;
   private final int petIndex;

   public PetLevelUp(int playerID, int msgType, int petIndex) {
      this.playerID = playerID;
      this.msgType = msgType;
      this.petIndex = petIndex;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.msgType);
      packet.writeInt(this.petIndex);
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
