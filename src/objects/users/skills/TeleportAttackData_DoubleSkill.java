package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_DoubleSkill extends TeleportAttackData {
   public int firstSkillID;
   public int secondSkillId;
   public int skillLevel;
   public byte unk1;
   public byte unk2;
   public int[] thirdSkillId;

   public TeleportAttackData_DoubleSkill(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.firstSkillID = packet.readInt();
      this.secondSkillId = packet.readInt();
      this.skillLevel = packet.readInt();
      this.unk1 = packet.readByte();
      this.unk2 = packet.readByte();
      this.thirdSkillId = new int[this.unk2];

      for (int i = 0; i < this.unk2; i++) {
         this.thirdSkillId[i] = packet.readInt();
      }
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.firstSkillID);
      packet.writeInt(this.secondSkillId);
      packet.writeInt(this.skillLevel);
      packet.write(this.unk1);
      packet.write(this.unk2);

      for (int i = 0; i < this.unk2; i++) {
         packet.writeInt(this.thirdSkillId[i]);
      }
   }
}
