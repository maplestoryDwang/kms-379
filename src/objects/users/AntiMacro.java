package objects.users;

import java.util.concurrent.atomic.AtomicInteger;
import objects.captcha.Captcha;
import objects.utils.StringUtil;

public class AntiMacro {
   private int fromID = 0;
   private long lastAttackTime = 0L;
   private long lastActiveMacroTime = 0L;
   private AtomicInteger trying = new AtomicInteger(1);
   private Captcha captcha = null;
   private AntiMacroType macroType = null;

   public long getLastAttackTime() {
      return this.lastAttackTime;
   }

   public void setLastAttackTime(long lastAttackTime) {
      this.lastAttackTime = lastAttackTime;
   }

   public AtomicInteger getTrying() {
      return this.trying;
   }

   public int canCheck() {
      if (this.getCaptcha() != null) {
         return -1;
      } else if (this.getLastActiveMacroTime() != 0L && System.currentTimeMillis() - this.getLastActiveMacroTime() < 1800000L) {
         return -2;
      } else {
         return this.getLastAttackTime() != 0L && System.currentTimeMillis() - this.getLastAttackTime() <= 10000L ? 0 : -3;
      }
   }

   public void renew() {
      this.setCaptcha(StringUtil.getRandomCaptcha(216, 64, 4));
   }

   public Captcha getCaptcha() {
      return this.captcha;
   }

   public void setCaptcha(Captcha captcha) {
      this.captcha = captcha;
   }

   public long getLastActiveMacroTime() {
      return this.lastActiveMacroTime;
   }

   public void setLastActiveMacroTime(long lastActiveMacroTime) {
      this.lastActiveMacroTime = lastActiveMacroTime;
   }

   public AntiMacroType getMacroType() {
      return this.macroType;
   }

   public void setMacroType(AntiMacroType macroType) {
      this.macroType = macroType;
   }

   public int getFromID() {
      return this.fromID;
   }

   public void setFromID(int fromID) {
      this.fromID = fromID;
   }
}
