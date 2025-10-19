package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class DiceRoll implements Effect {
   public static final int header = EffectHeader.DiceRoll.getValue();
   private final int playerID;
   private final boolean isSpecial;
   private final int skillID;
   private final int skillLevel;
   private final int rootEffect;
   private final int select;

   public DiceRoll(int playerID, int select, int rootEffect, int skillID, int skillLV, boolean bSpecial) {
      this.playerID = playerID;
      this.select = select;
      this.rootEffect = rootEffect;
      this.skillID = skillID;
      this.skillLevel = skillLV;
      this.isSpecial = bSpecial;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.select);
      packet.writeInt(this.rootEffect);
      packet.writeInt(this.skillID);
      packet.write(this.skillLevel);
      packet.write(this.isSpecial);
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
