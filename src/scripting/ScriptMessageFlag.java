package scripting;

public enum ScriptMessageFlag {
   None(0),
   NoEsc(1),
   NpcReplacedByUser(2),
   NpcReplacedByNpc(4),
   FlipImage(8),
   Self(16),
   Scenario(32),
   Unk1(64),
   BigScenario(128),
   Change(256);

   private int flag;

   private ScriptMessageFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }
}
