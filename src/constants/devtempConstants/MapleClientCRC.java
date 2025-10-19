package constants.devtempConstants;

import constants.ServerConstants;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MapleClientCRC {
   public static final Map<Integer, String> CRCCheckFiles = new HashMap<>();
   public static final Map<Integer, String> fastLoad = new HashMap<>();

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("CRCCheckFiles");
         CRCCheckFiles.clear();
         String wz = "";
         String hash = "";

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
                     wz = value;
                     break;
               }
            }

            CRCCheckFiles.put(i, wz);
         }

         System.out.println("총 " + CRCCheckFiles.size() + "개의 WZ경로를 불러왔습니다.");
      } catch (Exception var25) {
         var25.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var21) {
            var21.printStackTrace();
         }
      }

      try {
         String CRCFile = "CRC.bin";

         try (DataInputStream in = new DataInputStream(new FileInputStream(CRCFile))) {
            byte[] tempbytes = in.readAllBytes();
            ServerConstants.origCRCBytes = tempbytes;
         }
      } catch (Exception var24) {
         var24.printStackTrace();
      }
   }

   static {
      fastLoad.put(1, "Canvas.dll");
      fastLoad.put(2, "CrashReporter_64.dll");
   }
}
