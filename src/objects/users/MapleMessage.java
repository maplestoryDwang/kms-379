package objects.users;

public class MapleMessage {
   private final int uniqueID;
   private final int fromcid;
   private final String from;
   private final int tocid;
   private final String to;
   private final String message;
   private final long sentTime;
   private boolean addPop;
   private boolean checked;

   public MapleMessage(int uniqueID, int fromcid, String from, int tocid, String to, String message, long sentTime, boolean addPop, boolean checked) {
      this.uniqueID = uniqueID;
      this.fromcid = fromcid;
      this.from = from;
      this.tocid = tocid;
      this.to = to;
      this.message = message;
      this.sentTime = sentTime;
      this.addPop = addPop;
      this.checked = checked;
   }

   public int getUniqueID() {
      return this.uniqueID;
   }

   public int getFromcid() {
      return this.fromcid;
   }

   public String getFrom() {
      return this.from;
   }

   public int getTocid() {
      return this.tocid;
   }

   public String getTo() {
      return this.to;
   }

   public String getMessage() {
      return this.message;
   }

   public long getSentTime() {
      return this.sentTime;
   }

   public boolean isAddPop() {
      return this.addPop;
   }

   public void setAddPop(boolean addPop) {
      this.addPop = addPop;
   }

   public boolean isChecked() {
      return this.checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }
}
