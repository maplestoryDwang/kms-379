package objects.effect.child;

import network.SendPacketOpcode;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class PostSkillEffect implements Effect {
   public static final int header = EffectHeader.PostSkill.getValue();
   private final int playerID;
   private final int skillID;
   private final int skillLevel;
   private boolean canSend = true;
   private final PacketEncoder addPacket;

   public PostSkillEffect(int playerID, int skillID, int skillLevel, PacketEncoder addPacket) {
      this.playerID = playerID;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.addPacket = addPacket;
      if (!this.decode()) {
         this.canSend = false;
         if (!decodeList.containsKey(skillID)) {
            decodeList.put(skillID, header);
            System.out.print(skillID + " 스킬의 이펙트가 클라이언트 데이터와 맞지 않아 패킷 출력을 중단했습니다.\n");
         }
      }
   }

   public boolean decode() {
      try {
         PacketDecoder p = new PacketDecoder(new ByteArrayByteStream(this.addPacket != null ? this.addPacket.getPacket() : new byte[0]));
         if (this.skillID == 400051076 || this.skillID == 500061031) {
            p.readInt();
            p.readInt();
         } else if (this.skillID == 31111003) {
            p.readInt();
         } else if (this.skillID == 25121006) {
            p.readInt();
         }

         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.write(this.skillLevel);
      if (this.addPacket != null) {
         packet.encodeBuffer(this.addPacket.getPacket());
      }
   }

   @Override
   public byte[] encodeForLocal() {
      PacketEncoder packet = new PacketEncoder();
      if (this.canSend) {
         packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
         packet.write(header);
         this.encode(packet);
      } else {
         packet.writeShort(-2);
      }

      return packet.getPacket();
   }

   @Override
   public byte[] encodeForRemote() {
      PacketEncoder packet = new PacketEncoder();
      if (this.canSend) {
         packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
         packet.writeInt(this.playerID);
         packet.write(header);
         this.encode(packet);
      } else {
         packet.writeShort(-2);
      }

      return packet.getPacket();
   }
}
