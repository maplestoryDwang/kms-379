package constants;

import database.DBConfig;
import java.util.ArrayList;
import java.util.List;
import objects.utils.Properties;
import objects.utils.Table;

public class AutoNotice {
   private int interval = 10000;
   private int noticeType = 6;
   private List<String> notice = new ArrayList<>();

   public void loadAutoNotice() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "AutoNotice.data");
      int count = 0;

      for (Table children : table.list()) {
         this.setInterval(Integer.parseInt(children.getProperty("Interval")));
         this.setNoticeType(Integer.parseInt(children.getProperty("Type")));

         for (Table child : children.list()) {
            this.getNotice().add(child.getProperty("Notice"));
            count++;
         }
      }

      System.out.println("총 " + count + "개의 자동 공지사항 데이터를 불러왔습니다. (공지사항 주기 : " + this.getInterval() + "m/s)");
   }

   public int getInterval() {
      return this.interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }

   public List<String> getNotice() {
      return this.notice;
   }

   public void setNotice(List<String> notice) {
      this.notice = notice;
   }

   public int getNoticeType() {
      return this.noticeType;
   }

   public void setNoticeType(int noticeType) {
      this.noticeType = noticeType;
   }
}
