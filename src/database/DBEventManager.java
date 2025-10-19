package database;

public class DBEventManager {
   static DBProcessor[] processors;
   static int nextProcessor;
   static int refreshProssor;

   public static void init(int updaterLength) {
      processors = new DBProcessor[updaterLength];

      for (int next = 0; next < updaterLength; next++) {
         processors[next] = new DBProcessor("DBProcessor - " + next, next);
         processors[next].start();
      }

      nextProcessor = 0;
      refreshProssor = 0;
   }

   static int nextProcess() {
      nextProcessor++;
      if (nextProcessor >= processors.length) {
         nextProcessor = 0;
      }

      return nextProcessor;
   }

   public static DBProcessor getNextProcessor() {
      DBProcessor process = processors[nextProcess()];
      if (!process.isError()) {
         return process;
      } else {
         DBProcessor check = processors[nextProcess()];

         while (check == null) {
            check = processors[nextProcess()];
         }

         while (!check.isError()) {
            check = processors[nextProcess()];
         }

         return check;
      }
   }

   public static synchronized void restartDBProcessor(DBProcessor process, int number) {
      try {
         if (!processors[number].isError()) {
            return;
         }

         process.saveRemaining();
         String resumeName = process.getName();
         int restartCount = 0;
         String startName = "";
         if (resumeName.contains("_Restart")) {
            try {
               restartCount = Integer.parseInt(resumeName.split("_Restart")[1]);
            } catch (Exception var6) {
            }

            startName = resumeName.replace("_Restart" + restartCount, "_Restart" + (restartCount + 1));
         } else {
            startName = resumeName + "_Restart" + (restartCount + 1);
         }

         DBProcessor tempProcessor = new DBProcessor(startName, number);
         tempProcessor.start();
         processors[number] = tempProcessor;
      } catch (Exception var7) {
         System.out.println("DBProcessor 재실행 실패");
         var7.printStackTrace();
      }
   }
}
