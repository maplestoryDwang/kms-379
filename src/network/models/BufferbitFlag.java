package network.models;

import java.util.Arrays;
import java.util.List;

public enum BufferbitFlag {
   unk(0),
   Meso(1),
   EQUIP(2),
   CONSUME(3),
   INSTALL(4),
   ETC(5),
   CASH(6),
   CODY(7);

   private int flag;

   private BufferbitFlag(int flag) {
      this.flag = flag;
   }

   public int getValue() {
      return this.flag;
   }

   public static byte calculateFlag(BufferbitFlag bufferbitflag) {
      return (byte)(1 << bufferbitflag.getValue() - 1);
   }

   public static byte[] getValueArray(List<BufferbitFlag> flag) {
      byte[] flagArray = new byte[100];

      for (BufferbitFlag flagValue : flag) {
         int flagIndex = flagValue.getValue();
         if (flagIndex >= 0 && flagIndex < flagArray.length) {
            flagArray[flagIndex] = 1;
         }
      }

      return flagArray;
   }

   public static byte[] getValueArray(BufferbitFlag flag) {
      byte[] flagArray = new byte[100];
      int flagIndex = flag.getValue();
      if (flagIndex >= 0 && flagIndex < flagArray.length) {
         flagArray[flagIndex] = 1;
      }

      return flagArray;
   }

   public static String byteArrayToString(byte[] byteArray) {
      return Arrays.toString(byteArray);
   }
}
