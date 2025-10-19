package objects.users.skills;

public class LinkSkillEntry {
   private int realSkillID;
   private int skillID;
   private int linkingPlayerID;
   private int linkedPlayerID;
   private short skillLevel;
   private long linkedTime;

   public LinkSkillEntry(int realSkillID, int skillID, int linkingPlayerID, int linkedPlayerID, short skillLevel, long linkedTime) {
      this.realSkillID = realSkillID;
      this.skillID = skillID;
      this.linkingPlayerID = linkingPlayerID;
      this.linkedPlayerID = linkedPlayerID;
      this.skillLevel = skillLevel;
      this.linkedTime = linkedTime;
   }

   public int getRealSkillID() {
      return this.realSkillID;
   }

   public void setRealSkillID(int realSkillID) {
      this.realSkillID = realSkillID;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public int getLinkingPlayerID() {
      return this.linkingPlayerID;
   }

   public void setLinkingPlayerID(int linkingPlayerID) {
      this.linkingPlayerID = linkingPlayerID;
   }

   public int getLinkedPlayerID() {
      return this.linkedPlayerID;
   }

   public void setLinkedPlayerID(int linkedPlayerID) {
      this.linkedPlayerID = linkedPlayerID;
   }

   public short getSkillLevel() {
      return this.skillLevel;
   }

   public void setSkillLevel(short skillLevel) {
      this.skillLevel = skillLevel;
   }

   public long getLinkedTime() {
      return this.linkedTime;
   }

   public void setLinkedTime(long linkedTime) {
      this.linkedTime = linkedTime;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj != null && obj instanceof LinkSkillEntry) {
            LinkSkillEntry e = (LinkSkillEntry)obj;
            if (e.linkingPlayerID == this.linkingPlayerID && e.realSkillID == this.realSkillID) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.realSkillID + this.linkingPlayerID;
   }
}
