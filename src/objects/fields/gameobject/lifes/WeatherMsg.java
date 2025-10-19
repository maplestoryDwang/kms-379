package objects.fields.gameobject.lifes;

public class WeatherMsg {
   private String msg = "";
   private int time = 0;
   private int type = 0;

   public WeatherMsg(String msg, int time, int type) {
      this.setMsg(msg);
      this.setTime(time);
      this.setType(type);
   }

   public String getMsg() {
      return this.msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   public int getTime() {
      return this.time;
   }

   public void setTime(int time) {
      this.time = time;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }
}
