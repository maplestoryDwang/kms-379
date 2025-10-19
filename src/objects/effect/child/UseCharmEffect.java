package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class UseCharmEffect implements Effect {
   public static final int header = EffectHeader.CharmEffect.getValue();
   private final int playerID;
   private final boolean isSafetyCharm;
   private final int charmsLeft;
   private final int daysLeft;
   private final int itemID;

   public UseCharmEffect(int playerID, boolean isSafetyCharm, int charmsLeft, int daysLeft, int itemID) {
      this.playerID = playerID;
      this.isSafetyCharm = isSafetyCharm;
      this.charmsLeft = charmsLeft;
      this.daysLeft = daysLeft;
      this.itemID = itemID;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.isSafetyCharm);
      packet.write(this.charmsLeft);
      packet.write(this.daysLeft);
      if (!this.isSafetyCharm) {
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
