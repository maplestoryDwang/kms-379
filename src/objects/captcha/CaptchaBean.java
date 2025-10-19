package objects.captcha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CaptchaBean {
   private BackgroundProducer _bgProd = new FlatColorBackgroundProducer();
   private TextProducer _txtProd;
   private NoiseProducer _noiseProd;
   private GimpyRenderer _gimpy;
   private boolean _addBorder = false;
   private String _answer = "";
   private BufferedImage _img;
   private BufferedImage _bg;

   public CaptchaBean(int width, int height) {
      this._img = new BufferedImage(width, height, 2);
   }

   public CaptchaBean build() {
      if (this._txtProd != null) {
         this._answer = this._answer + this._txtProd.getText();
         WordRenderer wr = new MapleCaptchaWordRenderer();
         wr.render(this._answer, this._img);
      }

      if (this._noiseProd != null) {
         this._noiseProd.makeNoise(this._img);
      }

      if (this._gimpy != null) {
         this._gimpy.gimp(this._img);
      }

      this._bg = this._bgProd.getBackground(this._img.getWidth(), this._img.getHeight());
      Graphics2D g = this._bg.createGraphics();
      g.setComposite(AlphaComposite.getInstance(3, 1.0F));
      g.drawImage(this._img, null, null);
      if (this._addBorder) {
         int width = this._img.getWidth();
         int height = this._img.getHeight();
         g.setColor(Color.BLACK);
         g.drawLine(0, 0, 0, width);
         g.drawLine(0, 0, width, 0);
         g.drawLine(0, height - 1, width, height - 1);
         g.drawLine(width - 1, height - 1, width - 1, 0);
      }

      this._img = this._bg;
      g.dispose();
      return this;
   }

   public boolean isCorrect(String answer) {
      return answer.equals(this._answer);
   }

   public BufferedImage getImage() {
      return this._img;
   }

   public BackgroundProducer getBgProd() {
      return this._bgProd;
   }

   public void setBgProd(BackgroundProducer bgProd) {
      this._bgProd = bgProd;
   }

   public TextProducer getTxtProd() {
      return this._txtProd;
   }

   public void setTxtProd(TextProducer txtProd) {
      this._txtProd = txtProd;
   }

   public NoiseProducer getNoiseProd() {
      return this._noiseProd;
   }

   public void setNoiseProd(NoiseProducer noiseProd) {
      this._noiseProd = noiseProd;
   }

   public GimpyRenderer getGimpy() {
      return this._gimpy;
   }

   public void setGimpy(GimpyRenderer gimpy) {
      this._gimpy = gimpy;
   }

   public boolean isAddBorder() {
      return this._addBorder;
   }

   public void setAddBorder(boolean addBorder) {
      this._addBorder = addBorder;
   }

   public String getAnswer() {
      return this._answer;
   }

   public void setAnswer(String answer) {
      this._answer = answer;
   }
}
