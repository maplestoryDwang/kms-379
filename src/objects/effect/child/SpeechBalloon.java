package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class SpeechBalloon implements Effect {
   public static final int header = EffectHeader.SpeechBalloon.getValue();
   private final int playerID;
   private final int normal;
   private final int idx;
   private final int linkType;
   private final int time;
   private final int flip;
   private final int z;
   private final int magLevel;
   private final int align;
   private final int lineSpace;
   private final int npc;
   private final int user;
   private final String text;

   public SpeechBalloon(
      int playerID, int normal, int idx, int linkType, String text, int time, int flip, int z, int magLevel, int align, int lineSpace, int npc, int user
   ) {
      this.playerID = playerID;
      this.normal = normal;
      this.idx = idx;
      this.linkType = linkType;
      this.text = text;
      this.time = time;
      this.flip = flip;
      this.z = z;
      this.magLevel = magLevel;
      this.align = align;
      this.lineSpace = lineSpace;
      this.npc = npc;
      this.user = user;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.normal);
      packet.writeInt(this.idx);
      packet.writeInt(this.linkType);
      packet.writeMapleAsciiString(this.text);
      packet.writeInt(this.time);
      packet.writeInt(this.flip);
      packet.writeInt(this.z);
      packet.writeInt(this.magLevel);
      packet.writeInt(this.align);
      packet.writeInt(this.lineSpace);
      packet.writeInt(this.npc);
      packet.writeInt(this.user);
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
