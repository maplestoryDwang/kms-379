package logging;

import database.LogDBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoggingManager {
   static boolean processSave = false;
   static List<LoggingEntry> logList = new ArrayList<>();

   public static void insert() {
      if (!processSave) {
         List<LoggingEntry> list = new ArrayList<>(logList);
         processSave = true;
         PreparedStatement ps = null;
         int count = 0;

         try (Connection con = LogDBConnection.getConnection()) {
            Iterator var4 = list.iterator();

            label141:
            while (true) {
               LoggingEntry entry;
               while (true) {
                  if (!var4.hasNext()) {
                     break label141;
                  }

                  entry = (LoggingEntry)var4.next();

                  try {
                     if (entry != null) {
                        entry.insert(con);
                        break;
                     }
                  } catch (Exception var22) {
                     count++;
                     System.out.println("로그 insert 작업 중 오류 발생!");
                     if (entry != null) {
                        try {
                           System.out.println("오류 발생한 로그 : " + entry.getLoggingType().name());
                        } catch (Exception var21) {
                        }
                     }

                     var22.printStackTrace();
                     break;
                  }
               }

               logList.remove(entry);
            }
         } catch (SQLException var24) {
            var24.printStackTrace();
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var27 = null;
               }
            } catch (SQLException var19) {
               var19.printStackTrace();
            }
         }

         processSave = false;
         list.clear();
         list = null;
         if (count > 0) {
            System.out.println(String.format("로그저장중 실패 발생. 기록 실패 갯수 %d개", count));
         }
      }
   }

   public static void putLog(LoggingEntry entry) {
      logList.add(entry);
   }
}
