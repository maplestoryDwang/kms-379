package constants.devtempConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MapleGoldenChariot {
   public static final List<MapleGoldenChariot> goldenChariotList = new ArrayList<>();
   int day;
   int itemID;
   int itemQty;

   public MapleGoldenChariot(int day, int itemID, int itemQty) {
      this.day = day;
      this.itemID = itemID;
      this.itemQty = itemQty;
   }

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;
      goldenChariotList.clear();

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("GoldCart");
         String day = "";
         String itemid = "";
         String quantity = "";

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
                     day = value;
                     break;
                  case 1:
                     itemid = value;
                     break;
                  case 2:
                     quantity = value;
               }
            }

            goldenChariotList.add(new MapleGoldenChariot(Integer.valueOf(day), Integer.valueOf(itemid), Integer.valueOf(quantity)));
         }

         System.out.println("[황금마차] " + goldenChariotList.size() + "개 캐싱완료");
      } catch (Exception var20) {
         var20.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var19) {
            var19.printStackTrace();
         }
      }
   }

   public int getDay() {
      return this.day;
   }

   public int getItemID() {
      return this.itemID;
   }

   public int getItemQty() {
      return this.itemQty;
   }
}
