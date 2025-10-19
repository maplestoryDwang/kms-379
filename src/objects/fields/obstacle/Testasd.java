package objects.fields.obstacle;

public class Testasd {
   public static void main(String[] args) {
      int carASpeed = 55;
      int carBSpeed = 78;
      int remainingTime = 11;
      int remainingTimeSeconds = 4;
      int timeToSeconds = remainingTime * 60 + remainingTimeSeconds;
      double secondSpeedA = carASpeed * 1000.0 / 60.0 / 60.0;
      double secondSpeedB = carBSpeed * 1000.0 / 60.0 / 60.0;
      double totalTimeA = secondSpeedA * timeToSeconds;
      double totalTimeB = secondSpeedB * timeToSeconds;
      System.out.println(totalTimeA / 1000.0 + "km " + totalTimeB / 1000.0 + "km");
   }
}
