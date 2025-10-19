package objects.quest;

public class QuestEx {
   private int questID;
   private String data;
   private String time;

   public QuestEx(int questID, String data, String time) {
      this.questID = questID;
      this.data = data;
      this.time = time;
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

   public String getTime() {
      return this.time;
   }

   public void setTime(String time) {
      this.time = time;
   }
}
