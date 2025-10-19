package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class MobDamage implements Effect {
   public static final int header = EffectHeader.MobDamage.getValue();
   private final int playerID;
   private final int damage;

   public MobDamage(int playerID, int damage) {
      this.playerID = playerID;
      this.damage = damage;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.damage);
      packet.write(0);
      packet.write(0);
      packet.writeInt(this.damage);
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
