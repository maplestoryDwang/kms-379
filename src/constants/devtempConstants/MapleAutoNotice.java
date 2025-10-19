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

public class MapleAutoNotice {
   public static MapleAutoNotice autoNotice = null;
   private static int interval = 600000;
   private static int noticeType = 6;
   private List<String> notices = new ArrayList<>();

   public void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;
      this.notices.clear();

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("AutoNotice");
         String notice = "";

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
                     notice = value;
                     break;
               }
            }

            this.notices.add(notice);
         }

         this.setNoticeType(noticeType);
         System.out.println("총 " + this.notices.size() + "개의 자동 공지사항 데이터를 불러왔습니다. (공지사항 주기 : " + interval + "m/s)");
      } catch (Exception var19) {
         var19.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var18) {
            var18.printStackTrace();
         }
      }
   }

   public int getInterval() {
      return interval;
   }

   public void setInterval(int interval) {
      MapleAutoNotice.interval = interval;
   }

   public List<String> getNotice() {
      return this.notices;
   }

   public void setNotice(List<String> notice) {
      this.notices = notice;
   }

   public int getNoticeType() {
      return noticeType;
   }

   public void setNoticeType(int noticeType) {
      MapleAutoNotice.noticeType = noticeType;
   }
}
