package objects.quest;

public class MobQuest {
   private int questID;
   private int mobID;
   private int needCount;
   private int mobCount;

   public MobQuest(int questID, int mobID, int needCount, int mobCount) {
      this.questID = questID;
      this.mobID = mobID;
      this.needCount = needCount;
      this.mobCount = mobCount;
   }

   public int getQuestID() {
      return this.questID;
   }

   public void setQuestID(int questID) {
      this.questID = questID;
   }

   public int getMobID() {
      return this.mobID;
   }

   public void setMobID(int mobID) {
      this.mobID = mobID;
   }

   public int getNeedCount() {
      return this.needCount;
   }

   public void setNeedCount(int needCount) {
      this.needCount = needCount;
   }

   public int getMobCount() {
      return this.mobCount;
   }

   public void setMobCount(int mobCount) {
      this.mobCount = mobCount;
   }

   public static enum QuestName {
      QUEST_1("주황버섯의 갓 30개 모아오기", 1234),
      QUEST_2("와일드 보어의 송곳니 50개 모아오기", 1235),
      QUEST_3("루 광석 50개 모아오기", 1236),
      QUEST_4("추억의 사제 200마리 처치하기", 1237),
      QUEST_5("노말 혼테일 격파하기", 1238),
      QUEST_6("상급기사 A~D 각 50마리 처치하기", 1239),
      QUEST_7("노말 블러디 퀸 격파하기", 1240),
      QUEST_8("200레벨 달성하기", 1241),
      QUEST_9("기쁨의 에르다스 200마리 처치하기", 1242),
      QUEST_10("아르마의 부하 200마리 처치하기", 1243),
      QUEST_11("레벨 210 달성하기", 1244),
      QUEST_12("레벨 220 달성하기", 1245),
      QUEST_13("라이터틀, 버샤크 200마리 처치하기", 1246),
      QUEST_14("레벨 230 달성하기", 1247),
      QUEST_15("노말 스우 격파하기", 1248),
      QUEST_16("아투스 200마리 처치하기", 1249),
      QUEST_17("레벨 240 달성하기", 1250),
      QUEST_18("어둠의 집행자, 빛의 집행자 300마리 처치하기", 1251),
      QUEST_19("레벨 250 달성하기", 1252),
      QUEST_20("혼돈의 피조물 500마리 처치하기", 1253),
      QUEST_21("절망의 날개 500마리 처치하기", 1254),
      QUEST_22("레벨 260 달성하기", 1255),
      QUEST_23("안세스티온 1000마리 처치하기", 1256),
      QUEST_24("레벨 265 달성하기", 1257),
      QUEST_25("포어베리온 1000마리 처치하기", 1258),
      QUEST_26("레벨 270 달성하기", 1259),
      QUEST_27("엠브리온 1000마리 처치하기", 1260),
      QUEST_28("레벨 275 달성하기", 1261),
      QUEST_29("게릴라 스펙터 200마리 처치하기", 1263),
      QUEST_30("울프룻 200마리 처치하기", 1264),
      QUEST_31("검은 마법사 처치하기", 1265),
      QUEST_32("사자왕 반 레온의 흔적", 1266),
      QUEST_33("시간의 대신관 아카이럼의 흔적", 1267),
      QUEST_1000("유니온 - 골드 와이번 50마리 처치하기", 1400),
      QUEST_1001("유니온 - 미니 와이번 100마리 처치하기", 1401);

      final String name;
      final int questID;

      private QuestName(String name, int questID) {
         this.name = name;
         this.questID = questID;
      }

      public static String getQuestName(int questID) {
         for (MobQuest.QuestName name : values()) {
            if (name.questID == questID) {
               return name.name;
            }
         }

         return "";
      }
   }
}
