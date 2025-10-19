package constants.devtempConstants;

import constants.ServerConstants;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MapleDimensionalMirror {
   public static List<MapleDimensionalMirrorEntry> mirror = new ArrayList<>();
   public static List<Integer> rewardItems = new ArrayList<>();

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;
      mirror.clear();
      rewardItems.clear();
      ServerConstants.dimensionalMirror = new MapleDimensionalMirror();

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("DimensionalMirror");
         String title = "";
         String desc = "";
         String minlevel = "";
         String type = "";
         String reward = "";

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
                     title = value;
                     break;
                  case 1:
                     desc = value;
                     break;
                  case 2:
                     minlevel = value;
                     break;
                  case 3:
                     type = value;
                     break;
                  case 4:
                     reward = value;
               }
            }

            if (reward != "") {
               String[] rewards = reward.split(",");

               for (String token : rewards) {
                  int number = Integer.parseInt(token);
                  rewardItems.add(number);
               }
            }

            mirror.add(new MapleDimensionalMirrorEntry(title, desc, Integer.valueOf(minlevel), Integer.valueOf(type), rewardItems));
         }

         System.out.println("총 " + mirror.size() + "개의 컨텐츠 도우미 데이터를 불러왔습니다.");
      } catch (Exception var26) {
         var26.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var25) {
            var25.printStackTrace();
         }
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.UI_DIMENSIONAL_MIRROR.getValue());
      packet.writeInt(mirror.size());
      mirror.stream().forEach(mirror -> mirror.encode(packet));
   }
}
