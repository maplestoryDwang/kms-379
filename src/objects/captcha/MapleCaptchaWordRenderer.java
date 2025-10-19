package objects.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Random;

public class MapleCaptchaWordRenderer implements WordRenderer {
   private static final double YOFFSET = 0.25;
   private static final double XOFFSET = 0.03;
   private static final Color[] colors = new Color[]{
      Color.BLACK, Color.BLUE, Color.MAGENTA, Color.RED, new Color(97, 23, 73), new Color(127, 45, 25), new Color(75, 138, 8)
   };
   private static final Font font = new Font("궁서체", 1, 30);
   private static final Random rand = new Random();

   @Override
   public void render(String word, BufferedImage image) {
      Graphics2D g = image.createGraphics();
      RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
      g.setRenderingHints(hints);
      int i = 0;

      for (char c : word.toCharArray()) {
         g.setColor(colors[rand.nextInt(colors.length - 1)]);
         AffineTransform fontAT = new AffineTransform();
         double dl = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
         fontAT.rotate(dl / 3.0);
         AttributedString as = new AttributedString(String.valueOf(c));
         as.addAttribute(TextAttribute.FONT, font.deriveFont(fontAT));
         AttributedCharacterIterator aci = as.getIterator();
         int xBaseline = (int)Math.round(image.getWidth() * 0.03 + font.getSize2D() * i++);
         int yBaseline = image.getHeight() - (int)Math.round(image.getHeight() * 0.25);
         g.drawString(aci, xBaseline, yBaseline);
      }
   }
}
