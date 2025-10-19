package objects.effect.child;

import constants.GameConstants;
import network.SendPacketOpcode;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class SkillEffect implements Effect {
   public int header;
   private final int playerID;
   private final int playerLevel;
   private final int skillID;
   private final int skillLevel;
   private boolean canSend = true;
   private final PacketEncoder addPacket;

   public SkillEffect(int playerID, int playerLevel, int skillID, int skillLevel, PacketEncoder addPacket) {
      this.playerID = playerID;
      this.playerLevel = playerLevel;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.addPacket = addPacket;
      if (!this.decode()) {
         this.canSend = false;
         if (!decodeList.containsKey(skillID)) {
            decodeList.put(skillID, -1);
            System.out.print(skillID + " 스킬의 이펙트가 클라이언트 데이터와 맞지 않아 패킷 출력을 중단했습니다.\n");
         }
      }
   }

   public boolean decode() {
      try {
         PacketDecoder p = new PacketDecoder(new ByteArrayByteStream(this.addPacket != null ? this.addPacket.getPacket() : new byte[0]));
         if (this.skillID == 22170074) {
            byte rangeX = p.readByte();
         } else if (this.skillID == 4331006) {
            int objectID = p.readInt();
            byte rangeY = p.readByte();
         } else if (this.skillID == 64001000 || Math.abs(this.skillID - 64001007) <= 1) {
            byte var10 = p.readByte();
         } else if (Math.abs(this.skillID - 64001009) <= 2) {
            byte rlType = p.readByte();
            p.readInt();
            p.readInt();
            p.readInt();
         } else if (this.skillID == 64001012) {
            byte rlType = p.readByte();
            p.readInt();
            p.readInt();
            p.readInt();
         } else if (this.skillID == 30001062) {
            p.readByte();
            p.readShort();
            p.readShort();
         } else if (this.skillID == 30001061) {
            p.readByte();
         }

         switch (this.skillID) {
            case 4221052:
            case 65121052:
               int rangeX = p.readInt();
               int rangeY = p.readInt();
               break;
            case 15001021:
            case 20041222:
            case 20051284:
            case 152001004:
            case 400041026: {
               int x = p.readInt();
               int y = p.readInt();
               int destX = p.readInt();
               int destY = p.readInt();
               break;
            }
            case 60001218:
            case 60011218:
            case 400001000: {
               int distance = p.readInt();
               int destX = p.readInt();
               int destY = p.readInt();
            }
         }

         if (GameConstants.isRWMultiChargeSkill(this.skillID)) {
            p.readInt();
         } else if (this.skillID == 400041019) {
            p.readInt();
            p.readInt();
         } else if (this.skillID == 400041009) {
            p.readInt();
         } else if (Math.abs(this.skillID - 400041011) <= 4) {
            p.readInt();
         } else if (this.skillID == 400041036) {
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
         } else if (Math.abs(this.skillID - 80002393) <= 2 || this.skillID == 80002421) {
            p.readInt();
         } else if (this.skillID == 3311002 || this.skillID == 3321006) {
            p.readInt();
         } else if (this.skillID == 164001002) {
            p.readInt();
            p.readInt();
         } else if (Math.abs(this.skillID - 400020009) <= 2 || this.skillID == 400031050) {
            p.readByte();
            p.readInt();
            p.readInt();
         } else if (this.skillID == 131003016) {
            p.readByte();
            p.readInt();
            p.readInt();
         } else if (this.skillID == 400051080) {
            p.readInt();
            p.readInt();
         } else if (this.skillID == 400051073) {
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readInt();
            p.readByte();
            p.readByte();
            p.readByte();
         } else if (this.skillID == 63001004) {
            p.readByte();
            p.readInt();
            p.readInt();
            p.readInt();
         } else if ((this.skillID - 63001002 & -4) == 0 || this.skillID == 4101015) {
            p.readByte();
            p.readInt();
            p.readInt();
         } else if (this.skillID == 162121010) {
            p.readByte();
            p.readInt();
            p.readInt();
         } else if (GameConstants.is_unregistered_skill(this.skillID)) {
            byte var14 = p.readByte();
         } else if (this.skillID == 150030241) {
            p.readByte();
         } else if (Math.abs(this.skillID - 400020009) <= 2 || this.skillID == 400031050) {
            p.readByte();
            p.readInt();
            p.readInt();
         }

         return true;
      } catch (Exception var6) {
         return false;
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      if (this.header == EffectHeader.SkillOther.getValue()) {
         packet.writeInt(this.playerID);
      }

      packet.writeInt(this.skillID);
      packet.writeInt(this.playerLevel > 300 ? 300 : this.playerLevel);
      packet.writeInt(this.skillLevel);
      if (this.addPacket != null) {
         packet.encodeBuffer(this.addPacket.getPacket());
      }
   }

   @Override
   public byte[] encodeForLocal() {
      this.header = EffectHeader.Skill.getValue();
      PacketEncoder packet = new PacketEncoder();
      if (this.canSend) {
         packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
         packet.write(this.header);
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
         if (this.skillID == 400001020) {
            this.header = EffectHeader.Skill.getValue();
            packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
            packet.writeInt(this.playerID);
            packet.write(this.header);
            this.encode(packet);
         } else {
            this.header = EffectHeader.SkillOther.getValue();
            packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
            packet.writeInt(this.playerID);
            packet.write(this.header);
            this.encode(packet);
         }
      } else {
         packet.writeShort(-2);
      }

      return packet.getPacket();
   }
}
