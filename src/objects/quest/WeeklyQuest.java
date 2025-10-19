package objects.quest;

public class WeeklyQuest {
   private int questID;
   private int type;
   private int needID;
   private int needQuantity;
   private int quantity;

   public WeeklyQuest(int questID, int type, int needID, int needQuantity, int quantity) {
      this.questID = questID;
      this.type = type;
      this.needID = needID;
      this.needQuantity = needQuantity;
      this.quantity = quantity;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getNeedID() {
      return this.needID;
   }

   public void setNeedID(int needID) {
      this.needID = needID;
   }

   public int getNeedQuantity() {
      return this.needQuantity;
   }

   public void setNeedQuantity(int needQuantity) {
      this.needQuantity = needQuantity;
   }

   public int getQuestID() {
      return this.questID;
   }

   public void setQuestID(int questID) {
      this.questID = questID;
   }

   public int getQuantity() {
      return this.quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public static enum QuestName {
      QUEST_1("헤이븐 주간 퀘스트", 1234571),
      QUEST_2("버려진야영지 주간 퀘스트", 1234572);

      final String name;
      final int questID;

      private QuestName(String name, int questID) {
         this.name = name;
         this.questID = questID;
      }

      public static String getQuestName(int questID) {
         for (WeeklyQuest.QuestName name : values()) {
            if (name.questID == questID) {
               return name.name;
            }
         }

         return "";
      }
   }
}
