package objects.users;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SendOpcodeTool {
   public static void main(String[] args) {
      String filePath = "C:\\Send.txt";
      File file = new File(filePath);
      BufferedReader br = null;
      String line = null;
      if (!file.exists()) {
         file = new File("Send.txt");
         if (!file.exists()) {
            System.out.println("지정된 경로에 파일이 없습니다.");
            return;
         }
      }

      try {
         br = new BufferedReader(new FileReader(file));
         int delta = 22;

         while ((line = br.readLine()) != null) {
            if (!line.isEmpty() && !line.startsWith("#")) {
               String zzz = line.trim();
               String[] split = zzz.split("=");
               if (split.length > 1) {
                  int value = Integer.parseInt(split[1].trim());
                  System.out.println(split[0] + "= " + (value + delta));
               } else {
                  split = zzz.split(" = ");
                  int value = Integer.parseInt(split[1].trim());
                  System.out.println(split[0] + "= " + (value + delta));
               }
            }
         }
      } catch (IOException var18) {
         System.out.println(line);
         var18.printStackTrace();
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException var17) {
               var17.printStackTrace();
            }
         }
      }
   }
}
