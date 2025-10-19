package objects.captcha;

import java.awt.image.BufferedImage;

public interface BackgroundProducer {
   BufferedImage addBackground(BufferedImage var1);

   BufferedImage getBackground(int var1, int var2);
}
