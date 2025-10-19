package objects.users;

public class MobCollectionEx {
   private int questID;
   private String data;

   public MobCollectionEx(int questID, String data) {
      this.questID = questID;
      this.data = data;
   }

   public int getQuestID() {
      return this.questID;
   }

   public void setQuestID(int questID) {
      this.questID = questID;
   }

   public String getData() {
      return this.data;
   }

   public void setData(String data) {
      this.data = data;
   }
}
