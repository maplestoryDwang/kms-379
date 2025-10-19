package objects.captcha;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class StretchGimpyRenderer implements GimpyRenderer {
   private static final double XDEFAULT = 1.0;
   private static final double YDEFAULT = 3.0;
   private final double _xScale;
   private final double _yScale;

   public StretchGimpyRenderer() {
      this(1.0, 3.0);
   }

   public StretchGimpyRenderer(double xScale, double yScale) {
      this._xScale = xScale;
      this._yScale = yScale;
   }

   @Override
   public void gimp(BufferedImage image) {
      Graphics2D g = image.createGraphics();
      AffineTransform at = new AffineTransform();
      at.scale(this._xScale, this._yScale);
      g.drawRenderedImage(image, at);
   }
}
