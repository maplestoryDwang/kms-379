package objects.fields.gameobject.lifes.mobskills;

public class TransformBackReference {
   private int parentTemplateID;
   private boolean linkHP;
   private int hpTriggerOn;
   private int hpTriggerOff;
   private long nextTransformCooltime;
   private long nextTransformBack;

   public TransformBackReference(int parentTemplateID, boolean linkHP, int hpTriggerOn, int hpTriggerOff, long nextTransformCooltime, long nextTransformBack) {
      this.setParentTemplateID(parentTemplateID);
      this.setLinkHP(linkHP);
      this.setHpTriggerOn(hpTriggerOn);
      this.setHpTriggerOff(hpTriggerOff);
      this.setNextTransformCooltime(nextTransformCooltime);
      this.setNextTransformBack(nextTransformBack);
   }

   public int getParentTemplateID() {
      return this.parentTemplateID;
   }

   public void setParentTemplateID(int parentTemplateID) {
      this.parentTemplateID = parentTemplateID;
   }

   public boolean isLinkHP() {
      return this.linkHP;
   }

   public void setLinkHP(boolean linkHP) {
      this.linkHP = linkHP;
   }

   public int getHpTriggerOn() {
      return this.hpTriggerOn;
   }

   public void setHpTriggerOn(int hpTriggerOn) {
      this.hpTriggerOn = hpTriggerOn;
   }

   public int getHpTriggerOff() {
      return this.hpTriggerOff;
   }

   public void setHpTriggerOff(int hpTriggerOff) {
      this.hpTriggerOff = hpTriggerOff;
   }

   public long getNextTransformCooltime() {
      return this.nextTransformCooltime;
   }

   public void setNextTransformCooltime(long nextTransformCooltime) {
      this.nextTransformCooltime = nextTransformCooltime;
   }

   public long getNextTransformBack() {
      return this.nextTransformBack;
   }

   public void setNextTransformBack(long nextTransformBack) {
      this.nextTransformBack = nextTransformBack;
   }
}
