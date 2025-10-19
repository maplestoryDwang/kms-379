package objects.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import objects.users.stats.SecondaryStatFlag;

public class SecondaryStatAnalyzer {
   public static void main(String[] args) {
      boolean isLocal = false;
      String sFile = isLocal ? "DecodeForLocal.txt" : "DecodeForRemote.txt";
      String bitComp = "1404C1A00";
      String En4Byte = "140D1E160";
      File file = new File(sFile);

      try {
         if (!file.exists()) {
            return;
         }

         try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            BufferedReader d = new BufferedReader(new InputStreamReader(in));

            for (String line = d.readLine(); line != null; line = d.readLine()) {
               if (isLocal) {
                  if (line.contains(bitComp)) {
                     int bit = Integer.parseInt(line.split(", ")[1].split("i64")[0]);
                     SecondaryStatFlag sf = SecondaryStatFlag.getByBit(bit);
                     if (sf == null) {
                        System.out.println("bit " + bit + " is null");
                     } else {
                        line = d.readLine();
                        line = d.readLine();
                        if (line.contains(En4Byte)) {
                           System.out.println("encodeCommon(toSet, packet, SecondaryStatFlag." + sf.name() + ", now, fromMob);  //bit : " + bit);
                        } else if (line.contains("CInPacket")) {
                           System.out.println("AddInfo " + sf.name() + " bit : " + bit);
                           System.out.println(line);
                           line = d.readLine();

                           while (!line.contains("}")) {
                              if (line.contains("CInPacket")) {
                                 System.out.println(line);
                                 line = d.readLine();
                              } else {
                                 line = d.readLine();
                              }
                           }
                        } else {
                           System.out.println("Unknown  " + sf.name() + " bit : " + bit);
                        }
                     }
                  }
               } else if (line.contains(bitComp)) {
                  int bit = Integer.parseInt(line.split(", ")[1].split("i64")[0]);
                  SecondaryStatFlag sf = SecondaryStatFlag.getByBit(bit);
                  if (sf == null) {
                     System.out.println("bit " + bit + " is null");
                  } else {
                     System.out.println("Unknown  " + sf.name() + " bit : " + bit);
                  }
               } else if (line.contains("CInPacket")) {
                  System.out.println(line);
               }
            }
         }
      } catch (Exception var13) {
         System.out.println("SS Analyzer Err");
         var13.printStackTrace();
      }
   }
}
