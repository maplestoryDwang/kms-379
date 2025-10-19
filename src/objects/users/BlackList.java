package objects.users;

public class BlackList {
   private String name;
   private String denoteName;
   private int id;
   private int unk;

   public BlackList(String name, String denoteName, int id, int unk) {
      this.name = name;
      this.denoteName = denoteName;
      this.id = id;
      this.unk = unk;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDenoteName() {
      return this.denoteName;
   }

   public void setDenoteName(String denoteName) {
      this.denoteName = denoteName;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getUnk() {
      return this.unk;
   }

   public void setUnk(int unk) {
      this.unk = unk;
   }
}
