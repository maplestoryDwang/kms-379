package constants.devtempConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MapleMonsterCustomHP {
   public static Map<Integer, Long> maxHP = new HashMap<>();

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;

      try {
         maxHP.clear();
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("MobHP");

         for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow curRow = sheet.getRow(i);
            String floor = null;
            String mobid = null;
            String mobhp = null;

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
                     mobid = value;
                     break;
                  case 1:
                     mobhp = value;
               }
            }

            int mid = Integer.valueOf(mobid);
            long mhp = Long.valueOf(mobhp);
            MapleLifeFactory.setCustomMobHP(mid, mhp);
            maxHP.put(mid, mhp);
         }

         System.out.println("총 " + maxHP.size() + "개의 커스텀 몹 설정 데이터를 불러왔습니다.");
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

   public static long getMaxHP(int mobID) {
      Long ret = maxHP.get(mobID);
      return ret != null ? ret : 0L;
   }
}
