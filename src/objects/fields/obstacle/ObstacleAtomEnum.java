package objects.fields.obstacle;

public enum ObstacleAtomEnum {
   GreenMeteor(5, 20),
   BlueMeteor(6, 40),
   PurpleMeteor(8, 60),
   SmallPumpkin(12, 10),
   LargePumpkin(13, 20),
   PigTotem(18, 15),
   PepeTotem(19, 15),
   VonBonPurpleClock(22, 60),
   VonBonBlueClock(23, 40),
   VonBonGreenClock(24, 20),
   SmallPurpleMeteor(30, 25),
   RedStirge(34, 15),
   HekatonRedOrb(35, 35),
   HekatonLightOrd(36, 30),
   Icicle(38, 10),
   LargeFallingSnow(39, 20),
   MediumFallingSnow(40, 15),
   LittleFallingSnow(41, 10),
   SmallDorothyMeteor(42, 27),
   MediumDorothyMeteor(43, 30),
   LargeDorothyMeteor(44, 30),
   LargeMushroom(45, 20),
   MediumMushroom(46, 18),
   SmallMushroom(47, 13),
   LotusBlueDebris(48, 25),
   LotusYellowDebris(49, 35),
   LotusPurpleDebris(50, 55),
   LotusRobotDebris(51, 85),
   LotusCrusherDebris(52, 80),
   IceSpike(53, 10),
   FlameOrb(54, 18),
   IceRockFalling(55, 15),
   IceRockFalling_2(56, 12),
   IceRockFalling_3(57, 10),
   DemianYellowOrb(58, 44),
   RedOrb(59, 20),
   DrakFalling(75, 95),
   RedMeteor(76, 95),
   MorningStarFall(77, 95),
   PowerOfCreation(78, 95),
   VioletBead(79, 95);

   private int type;
   private int hitBox;

   private ObstacleAtomEnum(int type, int hitBox) {
      this.type = type;
      this.hitBox = hitBox;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getHitBox() {
      return this.hitBox;
   }

   public void setHitBox(int hitBox) {
      this.hitBox = hitBox;
   }
}
