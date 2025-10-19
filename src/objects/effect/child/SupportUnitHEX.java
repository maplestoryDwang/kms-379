package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class SupportUnitHEX implements Effect {
   public static final int header = EffectHeader.SupportUnitHEX.getValue();
   private final int playerID;
   private final int skillID;
   private final int skillLevel;
   private final int rootSelect;
   private final int select;

   public SupportUnitHEX(int playerID, int skillID, int skillLevel, int rootSelect, int select) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.rootSelect = rootSelect;
      this.select = select;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.write(this.skillLevel);
      packet.writeInt(this.rootSelect);
      packet.writeInt(this.select);
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
