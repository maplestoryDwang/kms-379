package objects.effect.child;

import network.SendPacketOpcode;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class SpecialSkillEffect implements Effect {
   public static final int header = EffectHeader.SpecialSkill.getValue();
   private final int playerID;
   private final int skillID;
   private boolean canSend = true;
   private final PacketEncoder addPacket;

   public SpecialSkillEffect(int playerID, int skillID, PacketEncoder addPacket) {
      this.playerID = playerID;
      this.skillID = skillID;
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
         if (this.skillID == 11121013 || this.skillID == 12100029 || this.skillID == 13121009 || this.skillID == 36110005 || this.skillID == 65101006) {
            int x = p.readInt();
            int y = p.readInt();
            int width = p.readInt();
         }

         if (this.skillID == 32111016) {
            int playerLevel = p.readInt();
            int skillLevel = p.readByte();
            int x = p.readInt();
            int y = p.readInt();
            int destX = p.readInt();
            int var7 = p.readInt();
         }

         if (this.skillID == 80002206
            || this.skillID == 80000257
            || this.skillID == 80000260
            || this.skillID == 80002599
            || Math.abs(this.skillID - 80002725) <= 22
            || Math.abs(this.skillID - 80002801) <= 37) {
            int x = p.readInt();
            int y = p.readInt();
            int var18 = p.readInt();
         }

         if (this.skillID == 80002753) {
            int skillLevel = p.readInt();
            p.readInt();
            p.readInt();
            if (skillLevel == 2) {
               p.readByte();
            } else {
               p.readInt();
            }
         }

         if (this.skillID == 80002779) {
            p.readLong();
         }

         if (this.skillID == 400021088) {
            int x = p.readInt();
            int y = p.readInt();
            int width = p.readInt();
            int var20 = p.readInt();
         }

         if (this.skillID == 400031053 || this.skillID == 500061016) {
            p.readInt();
            p.readInt();
            p.readInt();
         }

         if (this.skillID == 400041068) {
            int var13 = p.readInt();
         }

         return true;
      } catch (Exception var8) {
         return false;
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
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
