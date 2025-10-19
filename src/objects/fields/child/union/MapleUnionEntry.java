package objects.fields.child.union;

import network.encode.PacketEncoder;

public class MapleUnionEntry {
   public int angle;
   public int board;
   public int characterID;
   public int job;
   public int level;
   public String mobileName;
   public String name;
   public int starForce;
   public int type;
   public int unk1;

   public MapleUnionEntry copy() {
      MapleUnionEntry e = new MapleUnionEntry();
      e.type = this.type;
      e.characterID = this.characterID;
      e.level = this.level;
      e.job = this.job;
      e.angle = this.angle;
      e.board = this.board;
      e.starForce = this.starForce;
      e.name = this.name;
      e.mobileName = this.mobileName;
      e.unk1 = this.unk1;
      return e;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.type);
      packet.writeInt(this.characterID);
      packet.writeInt(this.level);
      packet.writeInt(this.job);
      packet.writeInt(this.unk1);
      packet.writeInt(this.angle);
      packet.writeInt(this.board);
      packet.writeInt(this.starForce);
      packet.writeMapleAsciiString(this.name);
      if (this.type == 2) {
         packet.writeMapleAsciiString(this.mobileName);
      }
   }
}
