package objects.utils;

import java.util.Random;

public class GenerateCertCharacter {
   private int pwdLength = 160;
   private final char[] passwordTable = new char[]{
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
      '!',
      '@',
      '#',
      '$',
      '%',
      '^',
      '&',
      '*',
      '(',
      ')',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0'
   };

   public String excuteGenerate() {
      Random random = new Random(System.currentTimeMillis());
      int tablelength = this.passwordTable.length;
      StringBuffer buf = new StringBuffer();

      for (int i = 0; i < this.pwdLength; i++) {
         buf.append(this.passwordTable[random.nextInt(tablelength)]);
      }

      return buf.toString();
   }

   public int getPwdLength() {
      return this.pwdLength;
   }

   public void setPwdLength(int pwdLength) {
      this.pwdLength = pwdLength;
   }
}
