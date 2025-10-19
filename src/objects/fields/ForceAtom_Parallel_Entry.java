package objects.fields;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class ForceAtom_Parallel_Entry {
   public List<ForceAtom_Parallel_Bullet> bullets = new ArrayList<>();
   public int bulletSkillID;
   public ForceAtom.AtomType atomType;

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.atomType.getType());
      packet.writeInt(this.bulletSkillID);
      packet.writeInt(this.bullets.size());

      for (ForceAtom_Parallel_Bullet b : this.bullets) {
         b.encode(packet);
      }
   }
}
