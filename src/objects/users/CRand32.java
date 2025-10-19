package objects.users;

import objects.utils.Randomizer;

public class CRand32 {
   private int seed1;
   private int seed2;
   private int seed3;
   private int oldSeed1;
   private int oldSeed2;
   private int oldSeed3;

   public CRand32() {
      int randInt = Randomizer.nextInt();
      this.Seed(randInt, randInt, randInt);
   }

   public final long Random() {
      this.oldSeed1 = this.seed1;
      this.oldSeed2 = this.seed2;
      this.oldSeed3 = this.seed3;
      long seed1 = Integer.toUnsignedLong(this.seed1);
      long seed2 = Integer.toUnsignedLong(this.seed2);
      long seed3 = Integer.toUnsignedLong(this.seed3);
      long newSeed1 = seed1 << 12 ^ seed1 >> 19 ^ (seed1 >> 6 ^ seed1 << 12) & 8191L;
      long newSeed2 = 16L * seed2 ^ seed2 >> 25 ^ (16L * seed2 ^ seed2 >> 23) & 127L;
      long newSeed3 = seed3 >> 11 ^ seed3 << 17 ^ (seed3 >> 8 ^ seed3 << 17) & 2097151L;
      this.seed1 = (int)(newSeed1 & 4294967295L);
      this.seed2 = (int)(newSeed2 & 4294967295L);
      this.seed3 = (int)(newSeed3 & 4294967295L);
      return (newSeed1 ^ newSeed2 ^ newSeed3) & 4294967295L;
   }

   public final void Seed(int s1, int s2, int s3) {
      this.seed1 = s1 | 1048576;
      this.oldSeed1 = s1 | 1048576;
      this.seed2 = s2 | 4096;
      this.oldSeed2 = s2 | 4096;
      this.seed3 = s3 | 16;
      this.oldSeed3 = s3 | 16;
   }
}
