package network.center;

import java.io.Serializable;
import java.util.Map;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;

public class PlayerBuffValueHolder implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   public long startTime;
   public int localDuration;
   public int cid;
   public SecondaryStatEffect effect;
   public Map<SecondaryStatFlag, Integer> statup;

   public PlayerBuffValueHolder(long startTime, SecondaryStatEffect effect, Map<SecondaryStatFlag, Integer> statup, int localDuration, int cid) {
      this.startTime = startTime;
      this.effect = effect;
      this.statup = statup;
      this.localDuration = localDuration;
      this.cid = cid;
   }
}
