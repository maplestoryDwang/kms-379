package network.connector;

public class TundraAes {
   private static byte[] key = new byte[]{
      -117, -43, 50, 57, -80, -52, 3, 101, 69, 0, -74, -28, -18, 120, 39, 122, 3, -103, 94, -124, 48, 40, 56, 120, -73, -87, -29, 28, -8, 22, -126, 19
   };
   public static byte[] key2 = new byte[]{70, 114, -93, -118, 58, -67, 106, -88, -124, -110, -54, -45, 64, -45, 83, -84};
   private final byte[] mIV;

   public TundraAes(byte[] _iv) {
      this.mIV = _iv;
   }

   public byte[] Encrypt(byte[] pData) throws Exception {
      byte[] freshIVBlock = new byte[]{
         this.mIV[2],
         this.mIV[0],
         this.mIV[3],
         this.mIV[1],
         this.mIV[0],
         this.mIV[1],
         this.mIV[2],
         this.mIV[3],
         this.mIV[1],
         this.mIV[3],
         this.mIV[0],
         this.mIV[2],
         this.mIV[3],
         this.mIV[2],
         this.mIV[1],
         this.mIV[0]
      };
      byte[] RealKey = new byte[pData.length];

      for (int a = 0; a < pData.length; a++) {
         if (a % 2 == 0) {
            RealKey[a] = (byte)(freshIVBlock[a % freshIVBlock.length] ^ key[a % key.length]);
         } else {
            RealKey[a] = (byte)(freshIVBlock[a % freshIVBlock.length] ^ key2[a % key2.length]);
         }
      }

      for (int ax = 0; ax < RealKey.length; ax++) {
         pData[ax] ^= RealKey[ax];
      }

      return pData;
   }

   public byte[] Decrypt(byte[] pData) throws Exception {
      byte[] freshIVBlock = new byte[]{
         this.mIV[2],
         this.mIV[0],
         this.mIV[3],
         this.mIV[1],
         this.mIV[0],
         this.mIV[1],
         this.mIV[2],
         this.mIV[3],
         this.mIV[1],
         this.mIV[3],
         this.mIV[0],
         this.mIV[2],
         this.mIV[3],
         this.mIV[2],
         this.mIV[1],
         this.mIV[0]
      };
      byte[] RealKey = new byte[pData.length];

      for (int a = 0; a < pData.length; a++) {
         if (a % 2 == 0) {
            RealKey[a] = (byte)(freshIVBlock[a % freshIVBlock.length] ^ key[a % key.length]);
         } else {
            RealKey[a] = (byte)(freshIVBlock[a % freshIVBlock.length] ^ key2[a % key2.length]);
         }
      }

      for (int ax = 0; ax < RealKey.length; ax++) {
         pData[ax] ^= RealKey[ax];
      }

      return pData;
   }
}
