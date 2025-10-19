package objects.fields.child.karing.KaringMatching;

public class KaringMatchType {
   public static enum Recv {
      BossSelect(0),
      Ready(1),
      Join(2),
      Leave(3);

      private int type;

      private Recv(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static KaringMatchType.Recv getType(int type) {
         for (KaringMatchType.Recv t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }

   public static enum Send {
      LoadParty(0),
      Select(3),
      Ready(4);

      private int type;

      private Send(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static KaringMatchType.Send getType(int type) {
         for (KaringMatchType.Send t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
