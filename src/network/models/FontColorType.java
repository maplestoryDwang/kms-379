package network.models;

public enum FontColorType {
   White(0),
   Black(1),
   Brown(2),
   Gray(3),
   Yellow(4),
   Blue(5),
   SkyBlue(6),
   Red(7),
   LightGreen(8),
   Pink(9),
   Orange(10),
   RedViolet(11),
   DimBlue(11),
   DimRed(13),
   DimGreen(14),
   LightViolet(15),
   Cyan(16),
   LightGray(17),
   DarkGray(18),
   DimGray(19),
   Green(20),
   WhiteGray(21),
   Violet(22);

   final int type;

   private FontColorType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
