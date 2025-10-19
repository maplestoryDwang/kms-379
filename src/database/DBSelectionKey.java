package database;

public enum DBSelectionKey {
   INSERT_OR_UPDATE(1),
   INSERT_OR_UPDATE_BATCH(2),
   INSERT_RETURN_GENERATED_KEYS(4);

   private int flag;

   private DBSelectionKey(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }
}
