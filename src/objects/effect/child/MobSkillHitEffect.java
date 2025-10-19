package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class MobSkillHitEffect implements Effect {
   public static final int header = EffectHeader.MobSkillHit.getValue();
   private final int playerID;
   private final int skillID;
   private final int skillLevel;

   public MobSkillHitEffect(int playerID, int skillID, int skillLevel) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
   }

   @Override
   public void encode(PacketEncoder packet) {
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
