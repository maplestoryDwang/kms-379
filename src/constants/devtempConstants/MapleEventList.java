package constants.devtempConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MapleEventList {
   public static final LinkedHashMap<Integer, MapleEventList> EventList = new LinkedHashMap<>();
   String eventName;
   String eventDesc;
   int eventEndHour;
   int eventStartTime;
   int eventEndTime;
   List<Integer> items;

   public MapleEventList(String eventName, String eventDesc, int eventEndHour, int eventStartTime, int eventEndTime, List<Integer> items) {
      this.eventName = eventName;
      this.eventDesc = eventDesc;
      this.eventEndHour = eventEndHour;
      this.eventStartTime = eventStartTime;
      this.eventEndTime = eventEndTime;
      this.items = items;
   }

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;
      EventList.clear();

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("EventList");
         String eventName = "";
         String eventDesc = "";
         String eventEndHour = "";
         String eventStartTime = "";
         String eventEndTime = "";
         String item = "";
         int count = 0;

         for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow curRow = sheet.getRow(i);

            for (int z = 0; z < curRow.getPhysicalNumberOfCells(); z++) {
               XSSFCell curCell = curRow.getCell(z);
               switch (curCell.getCellType()) {
                  case FORMULA:
                     value = curCell.getCellFormula();
                     break;
                  case NUMERIC:
                     value = String.valueOf((int)curCell.getNumericCellValue());
                     break;
                  case STRING:
                     value = curCell.getStringCellValue();
                     break;
                  case BOOLEAN:
                     value = String.valueOf(curCell.getBooleanCellValue());
               }

               switch (z) {
                  case 0:
                     eventName = value;
                     break;
                  case 1:
                     eventDesc = value;
                     break;
                  case 2:
                     eventEndHour = value;
                     break;
                  case 3:
                     eventStartTime = value;
                     break;
                  case 4:
                     eventEndTime = value;
                     break;
                  case 5:
                     item = value;
               }
            }

            List<Integer> items = new ArrayList<>();
            if (item != "") {
               String[] rewards = item.split(", ");

               for (String token : rewards) {
                  int number = Integer.parseInt(token);
                  items.add(number);
               }
            }

            EventList.put(
               count++,
               new MapleEventList(eventName, eventDesc, Integer.parseInt(eventEndHour), Integer.parseInt(eventStartTime), Integer.parseInt(eventEndTime), items)
            );
         }

         System.out.println(EventList.size() + "개의 이벤트 리스트 데이터를 불러왔습니다.");
      } catch (Exception var29) {
         var29.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var28) {
            var28.printStackTrace();
         }
      }
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
