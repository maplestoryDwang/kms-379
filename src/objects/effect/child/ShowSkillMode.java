package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class ShowSkillMode implements Effect {
   public static final int header = EffectHeader.ShowSkillMode.getValue();
   private final int playerID;
   private final int skillID;
   private final int mode;
   private final int modeStatus;

   public ShowSkillMode(int playerID, int skillID, int mode, int modeStatus) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.mode = mode;
      this.modeStatus = modeStatus;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.writeInt(this.mode);
      packet.writeInt(this.modeStatus);
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
