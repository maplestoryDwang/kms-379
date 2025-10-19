package objects.fields.child.karrotte.guardian;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class EyeOfAbyss extends GuardianEntry {
   private int attackDelay;
   private List<EyeOfAbyssAttackEntry> bullets = new ArrayList<>();

   public EyeOfAbyss(int index, Point position, byte unk, GuardianType type, int refMobID) {
      super(index, position, unk, type, refMobID);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getRefMobID());
      packet.writeInt(this.getType().getType());
      packet.writeInt(this.getAttackInterval());
      packet.writeInt(this.getAttackDelay());
      packet.writeInt(this.getDeactiveHitCount());
      packet.writeInt(this.getBullets().size());
      this.getBullets().forEach(b -> packet.writeInt(b.getKey()));
   }

   public int getAttackDelay() {
      return this.attackDelay;
   }

   public void setAttackDelay(int attackDelay) {
      this.attackDelay = attackDelay;
   }

   public List<EyeOfAbyssAttackEntry> getBullets() {
      return this.bullets;
   }

   public void setBullets(List<EyeOfAbyssAttackEntry> bullets) {
      this.bullets = bullets;
   }
}
