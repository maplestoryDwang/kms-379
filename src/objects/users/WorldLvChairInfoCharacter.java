package objects.users;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.users.looks.AvatarLook;

public class WorldLvChairInfoCharacter {
   private int level;
   private int job;
   private String name;
   private AvatarLook primaryAvatar = null;
   private AvatarLook secondaryAvatar = null;

   public void decode(PacketDecoder packet) {
      this.level = packet.readInt();
      this.job = packet.readInt();
      this.name = packet.readMapleAsciiString();
      if (packet.readByte() == 1) {
         AvatarLook look = new AvatarLook();
         look.decode(packet);
         this.primaryAvatar = look;
      }

      if (packet.readByte() == 1) {
         AvatarLook look = new AvatarLook();
         look.decode(packet);
         this.secondaryAvatar = look;
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.level);
      packet.writeInt(this.job);
      packet.writeMapleAsciiString(this.name);
      if (this.primaryAvatar == null) {
         packet.write(0);
      } else {
         packet.write(1);
         this.primaryAvatar.encode(packet);
      }

      if (this.secondaryAvatar == null) {
         packet.write(0);
      } else {
         packet.write(1);
         this.secondaryAvatar.encode(packet);
      }
   }
}
