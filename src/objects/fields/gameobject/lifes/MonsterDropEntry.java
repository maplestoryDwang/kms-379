package objects.fields.gameobject.lifes;

public class MonsterDropEntry {
   public int itemId;
   public int chance;
   public int Minimum;
   public int Maximum;
   public int questid;
   public int dropperID;
   public boolean individual;

   public MonsterDropEntry(int itemId, int chance, int minimum, int maximum, int questid) {
      this(itemId, chance, minimum, maximum, questid, 0, false);
   }

   public MonsterDropEntry(int itemId, int chance, int Minimum, int Maximum, int questid, int dropperID, boolean individual) {
      this.itemId = itemId;
      this.chance = chance;
      this.questid = questid;
      this.Minimum = Minimum;
      this.Maximum = Maximum;
      this.dropperID = dropperID;
      this.individual = individual;
   }
}
