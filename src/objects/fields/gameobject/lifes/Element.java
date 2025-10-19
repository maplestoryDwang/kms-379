package objects.fields.gameobject.lifes;

public enum Element {
   Physical(0),
   Ice(1, true),
   Fire(2, true),
   Light(3),
   Poison(4),
   Holy(5, true),
   Dark(6),
   Undead(7),
   Gravity(8),
   Number(9);

   private int value;
   private boolean special = false;

   private Element(int v) {
      this.value = v;
   }

   private Element(int v, boolean special) {
      this.value = v;
      this.special = special;
   }

   public boolean isSpecial() {
      return this.special;
   }

   public static Element getFromChar(char c) {
      switch (Character.toUpperCase(c)) {
         case 'D':
            return Dark;
         case 'E':
         case 'G':
         case 'J':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'Q':
         case 'R':
         default:
            throw new IllegalArgumentException("unknown elemnt char " + c);
         case 'F':
            return Fire;
         case 'H':
            return Holy;
         case 'I':
            return Ice;
         case 'L':
            return Light;
         case 'P':
            return Physical;
         case 'S':
            return Poison;
      }
   }

   public static Element getFromId(int c) {
      for (Element e : values()) {
         if (e.value == c) {
            return e;
         }
      }

      throw new IllegalArgumentException("unknown elemnt id " + c);
   }

   public int getValue() {
      return this.value;
   }
}
