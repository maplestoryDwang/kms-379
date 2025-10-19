package objects.context.guild;

public class JoinSettingFlag {
   public static enum ActivityFlag {
      Friendship(1),
      Coordination(2),
      Hunting(4),
      Merchant(8),
      Collection(16),
      ProfessionalSkill(32),
      PartyPlay(64),
      BossRaid(128);

      private final int flag;

      private ActivityFlag(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }
   }

   public static enum AgeGroupFlag {
      Teenage(1),
      Twenty(2),
      Thirty(4),
      Etc(8);

      private final int flag;

      private AgeGroupFlag(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }
   }

   public static enum ConnectTimeFlag {
      Weekday(1),
      Weekend(2),
      AM0006(4),
      AM0612(8),
      PM1218(16),
      PM1824(32);

      private final int flag;

      private ConnectTimeFlag(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }
   }
}
