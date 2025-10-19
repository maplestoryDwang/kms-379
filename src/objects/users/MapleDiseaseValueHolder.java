package objects.users;

import java.io.Serializable;
import objects.users.stats.SecondaryStatFlag;

public class MapleDiseaseValueHolder implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   public long startTime;
   public long length;
   public int skillID;
   public int level;
   public int x;
   public int y;
   public SecondaryStatFlag disease;

   public MapleDiseaseValueHolder(SecondaryStatFlag disease, long startTime, long length, int skillID, int level, int x, int y) {
      this.disease = disease;
      this.startTime = startTime;
      this.length = length;
      this.skillID = skillID;
      this.level = level;
      this.x = x;
      this.y = y;
   }
}
