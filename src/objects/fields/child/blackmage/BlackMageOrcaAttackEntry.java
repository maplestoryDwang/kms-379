package objects.fields.child.blackmage;

import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;

public class BlackMageOrcaAttackEntry {
   private static AtomicInteger SN = new AtomicInteger(1);
   private int key = SN.addAndGet(1);
   private long damage;
   private int delay;

   public BlackMageOrcaAttackEntry(long damage, int delay) {
      this.damage = damage;
      this.delay = delay;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public long getDamage() {
      return this.damage;
   }

   public void setDamage(long damage) {
      this.damage = damage;
   }

   public int getDelay() {
      return this.delay;
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.key);
      packet.writeLong(this.damage);
      packet.writeInt(this.delay);
   }
}
