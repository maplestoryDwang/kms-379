package objects.users.looks.zero;

public enum ZeroInfoFlag {
   IsBeta(1),
   SubHP(2),
   SubMP(4),
   SubSkin(8),
   SubHair(16),
   SubFace(32),
   SubMHP(64),
   SubMMP(128),
   LinkCash(256),
   None(0),
   All(-1);

   private int flag;

   private ZeroInfoFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }

   public boolean hasFlag(ZeroInfoFlag flag) {
      return (this.getFlag() & flag.getFlag()) != 0;
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }

   public static ZeroInfoFlag getFlag(int flag) {
      ZeroInfoFlag f = None;
      f.setFlag(flag);
      return f;
   }
}
