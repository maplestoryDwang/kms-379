package objects.users.enchant;

public enum GradeRandomOption {
   Normal(0),
   Occult(1),
   Master(2),
   Meister(3),
   Red(4),
   Black(5),
   OccultAdditional(6),
   Additional(7),
   Amazing(8),
   AmazingAdditional(9);

   int type;

   private GradeRandomOption(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
