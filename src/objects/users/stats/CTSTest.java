package objects.users.stats;

public class CTSTest {
   public static void main(String[] args) {
      int flag = 790623;
      int flag2 = 532531;
      int position = 1;
      int flag3 = flag & flag2;

      for (int j = 0; j < 32; j++) {
         int vl = 1 << j;
         if ((flag3 & vl) != 0) {
            String name = null;

            for (SecondaryStatFlag ssf : SecondaryStatFlag.values()) {
               if (ssf.getBit() == 31 - j + 32 * position) {
                  name = ssf.name();
                  break;
               }
            }

            System.out.printf("value: 0x%02X [%s]", vl, name);
            System.out.println("");
         }
      }
   }
}
