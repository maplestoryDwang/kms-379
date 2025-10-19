package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class EventScreenSkill implements Effect {
   public static final int header = EffectHeader.EventScreenSkill.getValue();
   private final int playerID;
   private final int skillID;
   private final int addID;
   private final int posX;
   private final int posY;

   public EventScreenSkill(int playerID, int skillID, int addID, int posX, int posY) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.addID = addID;
      this.posX = posX;
      this.posY = posY;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.writeInt(this.addID);
      packet.writeInt(this.posX);
      packet.writeInt(this.posY);
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
