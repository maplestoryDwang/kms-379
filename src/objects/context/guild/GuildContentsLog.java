package objects.context.guild;

public class GuildContentsLog {
   private int characterid;
   private int type;
   private int lastweekpoint;
   private long lastweektime;
   private int thisweekpoint;
   private long thisweektime;

   public GuildContentsLog(int characterid, int type, int lastweekpoint, long lastweektime, int thisweekpoint, long thisweektime) {
      this.characterid = characterid;
      this.type = type;
      this.lastweekpoint = lastweekpoint;
      this.lastweektime = lastweektime;
      this.thisweekpoint = thisweekpoint;
      this.thisweektime = thisweektime;
   }

   public int getCharacterid() {
      return this.characterid;
   }

   public void setCharacterid(int characterid) {
      this.characterid = characterid;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getLastweekpoint() {
      return this.lastweekpoint;
   }

   public void setLastweekpoint(int lastweekpoint) {
      this.lastweekpoint = lastweekpoint;
   }

   public long getLastweektime() {
      return this.lastweektime;
   }

   public void setLastweektime(long lastweektime) {
      this.lastweektime = lastweektime;
   }

   public int getThisweekpoint() {
      return this.thisweekpoint;
   }

   public void setThisweekpoint(int thisweekpoint) {
      this.thisweekpoint = thisweekpoint;
   }

   public long getThisweektime() {
      return this.thisweektime;
   }

   public void setThisweektime(long thisweektime) {
      this.thisweektime = thisweektime;
   }
}
