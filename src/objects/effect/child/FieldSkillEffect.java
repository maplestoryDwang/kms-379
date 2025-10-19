package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class FieldSkillEffect implements Effect {
   public static final int header = EffectHeader.FieldSkillEffect.getValue();
   private final int playerID;
   private final int skillID;
   private final int skillLevel;
   private final PacketEncoder add;

   public FieldSkillEffect(int playerID, int skillID, int skillLevel, PacketEncoder add) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.add = add;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.writeInt(this.skillLevel);
      if (this.add != null) {
         packet.encodeBuffer(this.add.getPacket());
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
