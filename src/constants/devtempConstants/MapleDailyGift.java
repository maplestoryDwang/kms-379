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

public class MapleDailyGift {
   public static List<MapleDailyGiftInfo> dailyItems = new ArrayList<>();

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;
      dailyItems.clear();

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("DailyGift");
         String day = "";
         String itemid = "";
         String sn = "";
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
                     break;
                  case 3:
                     sn = value;
               }
            }

            dailyItems.add(new MapleDailyGiftInfo(Integer.valueOf(day), Integer.valueOf(itemid), Integer.valueOf(quantity), Integer.valueOf(sn)));
         }

         System.out.println("총 " + dailyItems.size() + "개의 데일리 기프트 설정 데이터를 불러왔습니다.");
      } catch (Exception var21) {
         var21.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var20) {
            var20.printStackTrace();
         }
      }
   }
}
