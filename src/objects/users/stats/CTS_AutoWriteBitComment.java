package objects.users.stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CTS_AutoWriteBitComment {
   public static void main(String[] args) {
      String filePath = "D:\\cts.txt";
      File file = new File(filePath);
      BufferedReader br = null;
      String line = null;
      if (!file.exists()) {
         System.out.println("지정된 경로에 파일이 없습니다.");
      } else {
         try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
               if (line.contains("encodeCommon(toSet")) {
                  String bitName = "UNK";
                  String[] split = line.split("SecondaryStatFlag.");
                  if (split != null && split.length > 1 && !split[0].contains("//")) {
                     String[] split2 = split[1].split("\\)");
                     if (split2 != null && split2.length > 1) {
                        SecondaryStatFlag flag = SecondaryStatFlag.valueOf(split2[0].split(",")[0].trim());
                        String[] split3 = line.split("//");
                        if (split3 != null && split3.length > 1) {
                           line = split3[0];
                        }

                        line = line + " //Bit : " + flag.getBit();
                     }
                  }
               } else if (line.contains("toSet.check")) {
                  String bitName = "UNK";
                  String[] split = line.split("SecondaryStatFlag.");
                  if (split != null && split.length > 1) {
                     String[] split2 = split[1].split("\\)");
                     if (split2 != null && split2.length > 1) {
                        SecondaryStatFlag flag = SecondaryStatFlag.valueOf(split2[0].split(",")[0].trim());
                        String[] split3 = line.split("//");
                        if (split3 != null && split3.length > 1) {
                           line = split3[0];
                        }

                        line = line + " //Bit : " + flag.getBit();
                     }
                  }
               } else if (line.contains("flag992.check")) {
                  String bitName = "UNK";
                  String[] split = line.split("SecondaryStatFlag.");
                  if (split != null && split.length > 1) {
                     String[] split2 = split[1].split("\\)");
                     if (split2 != null && split2.length > 1) {
                        SecondaryStatFlag flag = SecondaryStatFlag.valueOf(split2[0].split(",")[0].trim());
                        String[] split3 = line.split("//");
                        if (split3 != null && split3.length > 1) {
                           line = split3[0];
                        }

                        line = line + " //Bit : " + flag.getBit();
                     }
                  }
               } else if (line.contains("encodeCommonForRemote(packet")) {
                  String bitName = "UNK";
                  String[] split = line.split("SecondaryStatFlag.");
                  if (split != null && split.length > 1) {
                     String[] split2 = split[1].split("\\,");
                     if (split2 != null && split2.length > 1) {
                        SecondaryStatFlag flag = SecondaryStatFlag.valueOf(split2[0]);
                        String[] split3 = line.split("//");
                        if (split3 != null && split3.length > 1) {
                           line = split3[0];
                        }

                        line = line + " //Bit : " + flag.getBit();
                     }
                  }
               }

               System.out.println(line);
            }
         } catch (IOException var18) {
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
}
