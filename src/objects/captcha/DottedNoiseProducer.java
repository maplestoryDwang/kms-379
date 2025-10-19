package objects.captcha;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

public class DottedNoiseProducer implements NoiseProducer {
   static final int WID = 1;
   static final int HEI = 1;
   private static final SecureRandom RAND = new SecureRandom();
   static final Color[] colors = new Color[]{Color.BLACK, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.YELLOW};

   @Override
   public void makeNoise(BufferedImage image) {
      Graphics2D graphics = image.createGraphics();
      int height = image.getHeight();
      int width = image.getWidth();

      for (int i = 0; i < width; i++) {
         graphics.setColor(colors[RAND.nextInt(colors.length)]);
         graphics.drawRoundRect(RAND.nextInt(width), RAND.nextInt(height), 1, 1, 90, 90);
      }
   }
}
