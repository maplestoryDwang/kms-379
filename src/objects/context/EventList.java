package objects.context;

import database.DBConfig;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import objects.utils.Properties;
import objects.utils.Table;

public class EventList {
   public static final LinkedHashMap<Integer, EventList> cEventList = new LinkedHashMap<>();
   String eventName;
   String eventDesc;
   int eventEndHour;
   int eventStartTime;
   int eventEndTime;
   List<Integer> items;

   public static void cachingEventList() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "EventList.data");

      for (Table child : table.list()) {
         int eventKey = Integer.parseInt(child.getName());
         String eventName = child.getProperty("eventName");
         String eventDesc = child.getProperty("eventDesc");
         int eventEndHour = Integer.parseInt(child.getProperty("eventEndHour"));
         int eventStartTime = Integer.parseInt(child.getProperty("eventStartTime"));
         int eventEndTime = Integer.parseInt(child.getProperty("eventEndTime"));
         List<Integer> items = new ArrayList<>();

         for (Table item : child.getChild("items").list()) {
            items.add(Integer.parseInt(item.getProperty("itemID")));
         }

         EventList eL = new EventList(eventName, eventDesc, eventEndHour, eventStartTime, eventEndTime, items);
         cEventList.put(eventKey, eL);
      }

      System.out.println("[事件列表] " + cEventList.size() + "个 缓存完成");
   }

   public EventList(String eventName, String eventDesc, int eventEndHour, int eventStartTime, int eventEndTime, List<Integer> items) {
      this.eventName = eventName;
      this.eventDesc = eventDesc;
      this.eventEndHour = eventEndHour;
      this.eventStartTime = eventStartTime;
      this.eventEndTime = eventEndTime;
      this.items = items;
   }

   public String getEventName() {
      return this.eventName;
   }

   public String getEventDesc() {
      return this.eventDesc;
   }

   public int getEventEndHour() {
      return this.eventEndHour;
   }

   public int getEventStartTime() {
      return this.eventStartTime;
   }

   public int getEventEndTime() {
      return this.eventEndTime;
   }

   public List<Integer> getItems() {
      return this.items;
   }
}
