package objects.users.stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import objects.utils.FileoutputUtil;

public class CTS_EncodeForRemoteTool {
   public static void main(String[] args) {
      String filePath = "I:\\remote.txt";
      File file = new File(filePath);
      BufferedReader br = null;
      String line = null;
      List<Integer> list = new ArrayList<>();
      if (!file.exists()) {
         System.out.println("지정된 경로에 파일이 없습니다.");
      } else {
         try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
               if (line.contains("CTS_Get")) {
                  String[] split = line.split(", ");
                  String[] split2 = split[1].split("\\)");
                  int value = Integer.parseInt(split2[0]);
                  boolean find = false;

                  for (int v : list) {
                     if (v == value) {
                        find = true;
                     }
                  }

                  if (!find) {
                     list.add(value);
                  }
               }
            }

            StringBuilder bb = new StringBuilder();
            list.stream().sorted((a, b) -> a - b).forEach(a -> bb.append(a + ", "));
            FileoutputUtil.log("./remote.txt", bb.toString());
         } catch (IOException var20) {
            var20.printStackTrace();
         } finally {
            if (br != null) {
               try {
                  br.close();
               } catch (IOException var19) {
                  var19.printStackTrace();
               }
            }
         }
      }
   }
}
