package objects.context;

import database.DBConfig;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Table;

public class SundayEventList {
   public static final LinkedList<SundayEventList> sEventList = new LinkedList<>();
   String eventName;
   String eventDesc0;
   String eventDesc1;
   String eventDesc2;
   int eventStartTime;
   int eventEndTime;
   int eventEndHour;
   int eventFlag0;
   int eventFlag1;
   int eventVal0;
   int eventVal1;
   int eventVal2;
   int eventVal3;
   int eventVal4;
   int starForceSaleValue;
   List<Pair<Integer, Integer>> starForce100Success;
   int starForceDoubleValue;

   public static void cachingSundayEventList() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "SundayEventList.data");

      for (Table child : table.list()) {
         String eventName = child.getProperty("eventName");
         String eventDesc0 = child.getProperty("eventDesc0");
         String eventDesc1 = child.getProperty("eventDesc1");
         String eventDesc2 = child.getProperty("eventDesc2");
         int eventStartTime = Integer.parseInt(child.getProperty("eventStartTime"));
         int eventEndTime = Integer.parseInt(child.getProperty("eventEndTime"));
         int eventEndHour = Integer.parseInt(child.getProperty("eventEndHour"));
         int eventFlag0 = Integer.parseInt(child.getProperty("eventFlag0"));
         int eventFlag1 = Integer.parseInt(child.getProperty("eventFlag1"));
         int eventVal0 = Integer.parseInt(child.getProperty("eventVal0"));
         int eventVal1 = Integer.parseInt(child.getProperty("eventVal1"));
         int eventVal2 = Integer.parseInt(child.getProperty("eventVal2"));
         int eventVal3 = Integer.parseInt(child.getProperty("eventVal3"));
         int eventVal4 = Integer.parseInt(child.getProperty("eventVal4"));
         int starForceSaleValue = Integer.parseInt(child.getProperty("starForceSaleValue"));
         List<Pair<Integer, Integer>> starForce100Success = new ArrayList<>();

         for (Table item : child.getChild("starForce100Success").list()) {
            int toStarForce = Integer.parseInt(item.getProperty("toStarForce"));
            int fixedProb = Integer.parseInt(item.getProperty("fixedProb"));
            starForce100Success.add(new Pair<>(toStarForce, fixedProb));
         }

         int starForceDoubleValue = Integer.parseInt(child.getProperty("starForceDoubleValue"));
         SundayEventList eL = new SundayEventList(
            eventName,
            eventDesc0,
            eventDesc1,
            eventDesc2,
            eventStartTime,
            eventEndTime,
            eventEndHour,
            eventFlag0,
            eventFlag1,
            eventVal0,
            eventVal1,
            eventVal2,
            eventVal3,
            eventVal4,
            starForceSaleValue,
            starForce100Success,
            starForceDoubleValue
         );
         sEventList.add(eL);
      }

      System.out.println("[선데이리스트] " + sEventList.size() + "개 캐싱완료");
   }

   public SundayEventList(
      String eventName,
      String eventDesc0,
      String eventDesc1,
      String eventDesc2,
      int eventStartTime,
      int eventEndTime,
      int eventEndHour,
      int eventFlag0,
      int eventFlag1,
      int eventVal0,
      int eventVal1,
      int eventVal2,
      int eventVal3,
      int eventVal4,
      int starForceSaleValue,
      List<Pair<Integer, Integer>> starForce100Success,
      int starForceDoubleValue
   ) {
      this.eventName = eventName;
      this.eventDesc0 = eventDesc0;
      this.eventDesc1 = eventDesc1;
      this.eventDesc2 = eventDesc2;
      this.eventStartTime = eventStartTime;
      this.eventEndTime = eventEndTime;
      this.eventEndHour = eventEndHour;
      this.eventFlag0 = eventFlag0;
      this.eventFlag1 = eventFlag1;
      this.eventVal0 = eventVal0;
      this.eventVal1 = eventVal1;
      this.eventVal2 = eventVal2;
      this.eventVal3 = eventVal3;
      this.eventVal4 = eventVal4;
      this.starForceSaleValue = starForceSaleValue;
      this.starForce100Success = starForce100Success;
      this.starForceDoubleValue = starForceDoubleValue;
   }

   public String getEventName() {
      return this.eventName;
   }

   public String getEventDesc0() {
      return this.eventDesc0;
   }

   public String getEventDesc1() {
      return this.eventDesc1;
   }

   public String getEventDesc2() {
      return this.eventDesc2;
   }

   public int getEventStartTime() {
      return this.eventStartTime;
   }

   public int getEventEndTime() {
      return this.eventEndTime;
   }

   public int getEventEndHour() {
      return this.eventEndHour;
   }

   public int getEventFlag0() {
      return this.eventFlag0;
   }

   public int getEventFlag1() {
      return this.eventFlag1;
   }

   public int getEventVal0() {
      return this.eventVal0;
   }

   public int getEventVal1() {
      return this.eventVal1;
   }

   public int getEventVal2() {
      return this.eventVal2;
   }

   public int getEventVal3() {
      return this.eventVal3;
   }

   public int getEventVal4() {
      return this.eventVal4;
   }

   public int getStarForceSaleValue() {
      return this.starForceSaleValue;
   }

   public List<Pair<Integer, Integer>> getStarForce100Success() {
      return this.starForce100Success;
   }

   public int getStarForceDoubleValue() {
      return this.starForceDoubleValue;
   }
}
