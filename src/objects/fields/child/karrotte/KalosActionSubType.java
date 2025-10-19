package objects.fields.child.karrotte;

public class KalosActionSubType {
   public static enum Recv {
      Kalos_Unk1(1),
      FighterPlaneAttack_Receive(2),
      EyeOfRedemptionAttack_Receive(3),
      EyeOfAbyssAttack_Receive(4),
      EyeOfAbyssAttack_HitBlind(5),
      Kalos_Unk7(6),
      CreateMysticShotRequest_Receive(7),
      HitMysticShot_Top(8),
      HitMysticShot_Bottom(9),
      HitMysticShot_Left(10),
      HitMysticShot_Right(11),
      FighterPlane_Deactive(12),
      SphereOfOdium_Deactive(13),
      Kalos_Unk15(14),
      Kalos_Unk16(15),
      Kalos_Unk17(16),
      Kalos_Unk18(17),
      Kalos_Unk19(18);

      private int type;

      private Recv(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static KalosActionSubType.Recv getType(int type) {
         for (KalosActionSubType.Recv t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }

   public static enum Send {
      CreateGuardian(1),
      FighterPlaneAttack_Receive(2),
      EyeOfRedemptionAttack_Receive(3),
      EyeOfAbyssAttack_Receive(4),
      FighterPlaneDeactive(5),
      FighterPlane(6),
      FighterPlaneUnk(7),
      FighterPlaneStatus(8),
      SphereOfOdiumDeactive(9),
      SphereOfOdium(10),
      SphereOfOdiumStatus(11),
      EyeOfRedemptionDeactive(12),
      EyeOfRedemption(13),
      EyeOfRedemptionAttack(14),
      EyeOfRedemptionStatus(15),
      EyeOfTheAbyssDeactive(16),
      EyeOfTheAbyss(17),
      EyeOfTheAbyssAttack(18),
      EyeOfTheAbyssStatus(19),
      EyeOfTheAbyssDoBlind(20),
      KalosWill(21),
      LiberationTop(22),
      LiberationBottom(23),
      CreateMysticShot(24),
      HitMysticShotTop(25),
      HitMysticShotBottom(26),
      FuryOfTheGuardianSafetyZoneSet(27),
      FuryOfTheGuardianSafetyZoneGen(28),
      FuryOfTheGuardianSuccess(29),
      FuryOfTheGuardianStart(30),
      FuryOfTheGuardianEnd(31),
      Kalos_Unk36(32),
      Kalos_Unk37(33),
      Kalos_Unk38(34),
      Kalos_Unk39(35),
      CurseState(36),
      Kalos_Unk41(37);

      private int type;

      private Send(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static KalosActionSubType.Send getType(int type) {
         for (KalosActionSubType.Send t : values()) {
            if (t.getType() == type) {
               return t;
            }
         }

         return null;
      }
   }
}
