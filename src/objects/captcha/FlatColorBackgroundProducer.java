package objects.captcha;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;

public final class FlatColorBackgroundProducer implements BackgroundProducer {
   private Color _color = Color.GRAY;

   public FlatColorBackgroundProducer() {
      this(Color.GRAY);
   }

   public FlatColorBackgroundProducer(Color color) {
      this._color = color;
   }

   @Override
   public BufferedImage addBackground(BufferedImage bi) {
      int width = bi.getWidth();
      int height = bi.getHeight();
      return this.getBackground(width, height);
   }

   @Override
   public BufferedImage getBackground(int width, int height) {
      BufferedImage img = new BufferedImage(width, height, 1);
      Graphics2D graphics = img.createGraphics();
      graphics.setPaint(this._color);
      graphics.fill(new Double(0.0, 0.0, width, height));
      graphics.drawImage(img, 0, 0, null);
      graphics.dispose();
      return img;
   }
}
