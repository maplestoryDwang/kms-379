package network.models;

public enum FontType {
   Dotum0(0),
   Dotum1(1),
   Tahoma(2),
   NanumGothicBold(3),
   NanumGothic(4),
   YunGothic250(5),
   Moris(6),
   GungSeo(7),
   NamSanL(8),
   NamSanM(9);

   private final int type;

   private FontType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
