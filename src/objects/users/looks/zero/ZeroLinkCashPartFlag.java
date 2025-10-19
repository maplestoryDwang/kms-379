package objects.users.looks.zero;

public enum ZeroLinkCashPartFlag {
   None(0, 0),
   EyeAccessory(1, 3),
   Cap(2, 1),
   ForeHead(4, 2),
   EarRing(8, 4),
   Cape(16, 9),
   Clothes(32, 5),
   Gloves(64, 8),
   Weapon(128, 11),
   Pants(256, 6),
   Shoes(512, 7),
   Ring1(1024, 12),
   Ring2(2048, 13);

   private int flag;
   private int order;

   private ZeroLinkCashPartFlag(int flag, int order) {
      this.flag = flag;
      this.order = order;
   }

   public static ZeroLinkCashPartFlag getByOrder(int order) {
      for (ZeroLinkCashPartFlag flag : values()) {
         if (flag.order == order) {
            return flag;
         }
      }

      return null;
   }

   public int getFlag() {
      return this.flag;
   }

   public int getOrder() {
      return this.order;
   }

   public boolean hasFlag(ZeroLinkCashPartFlag flag) {
      return (this.getFlag() & flag.getFlag()) != 0;
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }

   public static ZeroLinkCashPartFlag getFlag(int flag) {
      ZeroLinkCashPartFlag f = None;
      f.setFlag(flag);
      return f;
   }
}
