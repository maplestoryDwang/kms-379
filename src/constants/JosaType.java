package constants;

public enum JosaType {
   이가(0),
   은는(1),
   을를(2),
   과와(3);

   private int type;

   private JosaType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
