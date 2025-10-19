package objects.androids;

import java.util.List;

public class BasicAndroid {
   private List<Integer> hairs;
   private List<Integer> faces;
   private int gender;

   public BasicAndroid(List<Integer> h, List<Integer> f, int g) {
      this.hairs = h;
      this.faces = f;
      this.gender = g;
   }

   public int getRandomHair() {
      return this.hairs.get((int)Math.floor(Math.random() * this.hairs.size()));
   }

   public int getRandomFace() {
      return this.faces.get((int)Math.floor(Math.random() * this.faces.size()));
   }

   public int getGender() {
      return this.gender;
   }
}
