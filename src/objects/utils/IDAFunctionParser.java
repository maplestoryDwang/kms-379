package objects.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IDAFunctionParser {
   public static String[] removeTargets = new String[]{
      "\\(unsigned int\\)", "\\(unsigned __int8\\)", "signed ", "__cdecl ", "\\*", "\\(int\\)", "\\(String \\)", "\\(_BYTE \\)"
   };
   public static String[][] replaceTargets = new String[][]{
      {"char", "String"},
      {"bool", "boolean"},
      {"return 1", "return true"},
      {"return 0", "return false"},
      {"&loc_", "0x"},
      {"&byte_", "0x"},
      {"byte_", "0x"},
      {"&off_", "0x"},
      {"&sub_", "0x"}
   };

   public static void main(String[] args) {
      String filePath = "I:\\IDA.txt";
      File file = new File(filePath);
      BufferedReader br = null;
      String line = null;
      if (!file.exists()) {
         System.out.println("지정된 경로에 파일이 없습니다.");
      } else {
         try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
               String l = line;

               for (String remove : removeTargets) {
                  l = l.replaceAll(remove, "");
               }

               for (String[] replace : replaceTargets) {
                  l = l.replaceAll(replace[0], replace[1]);
               }

               if (l.contains(" + ")) {
                  l = processArithmetic("+", l);
               }

               if (l.contains(" - ")) {
                  l = processArithmetic("-", l);
               }

               l = l.replaceAll("\\(String \\)", "");
               if (l.contains("0x")) {
                  String[] hex = l.split("0x");
                  if (hex.length > 1) {
                     for (int i = 1; i <= hex.length - 1; i++) {
                        int targetValue = 0;
                        String target = hex[i];
                        String[] s = target.split(" ");
                        target = s[0];
                        target = target.replaceAll("\\)", "");
                        target = target.replaceAll("\\|\\|", "");
                        target = target.replaceAll("\\;", "");
                        int value = Integer.parseInt(target, 16);
                        l = l.replaceAll("0x" + target, String.valueOf(value));
                     }
                  }
               }

               System.out.println(l);
            }
         } catch (IOException var21) {
            var21.printStackTrace();
         } finally {
            if (br != null) {
               try {
                  br.close();
               } catch (IOException var20) {
                  var20.printStackTrace();
               }
            }
         }
      }
   }

   public static String processArithmetic(String type, String line) {
      String[] a = null;
      if (type.equals("+")) {
         a = line.split(" \\+ ");
      } else if (type.equals("-")) {
         a = line.split(" \\- ");
      }

      int processLength = a.length - 1;

      for (int i = 1; i <= processLength; i++) {
         String v = a[i];
         v = v.replaceAll("\\(\\(String \\)", "");
         v = v.replaceAll("\\(String \\)", "");
         if (v.contains(" ")) {
            v = v.split(" ")[0];
         }

         v = v.replaceAll("\\;", "");
         v = v.replaceAll("\\)", "");
         int vv = 0;
         if (v.contains("0x")) {
            vv = Integer.parseInt(v.replaceAll("0x", ""), 16);
         } else {
            vv = Integer.parseInt(v);
         }

         int targetValue = 0;
         String target = a[i - 1];
         String[] s = target.split(" ");
         target = s[s.length - 1];
         target = target.replaceAll("\\)", "");
         if (target.contains("0x")) {
            String t = target.replaceAll("0x", "");
            targetValue = Integer.parseInt(t, 16);
         } else if (StringUtil.isNumber(target)) {
            targetValue = Integer.parseInt(target);
         }

         int result = 0;
         if (type.equals("+")) {
            result = targetValue + vv;
         } else if (type.equals("-")) {
            result = targetValue - vv;
         }

         if (!target.contains("a1")) {
            if (type.equals("+")) {
               line = line.replaceFirst(target + " \\+ " + vv, String.valueOf(result));
            } else if (type.equals("-")) {
               line = line.replaceFirst(target + " \\- " + vv, String.valueOf(result));
            }
         }
      }

      return line;
   }
}
