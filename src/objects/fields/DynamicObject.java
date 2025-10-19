package objects.fields;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class DynamicObject {
   private long beginShowTime = 0L;
   private int curState = 0;
   private List<Integer> footholdSN = new ArrayList<>();
   private int homeX = 0;
   private int homeY = 0;
   private int index;
   private String name;
   private String tags;
   private List<DynamicObject.CollisionDisease> userCollisionDisease = new ArrayList<>();
   private boolean waiting = false;
   private int waitingDuration = 0;
   private int posX = 0;
   private int posY = 0;

   public DynamicObject(String tags, String name, int posX, int posY, int index) {
      this.tags = tags;
      this.name = name;
      this.posX = posX;
      this.homeX = posX;
      this.posY = posY;
      this.homeY = posY;
      this.index = index;
   }

   public long getBeginShowTime() {
      return this.beginShowTime;
   }

   public void setBeginShowTime(long beginShowTime) {
      this.beginShowTime = beginShowTime;
   }

   public int getCurState() {
      return this.curState;
   }

   public void setCurState(int curState) {
      this.curState = curState;
   }

   public List<Integer> getFootholdSN() {
      return this.footholdSN;
   }

   public void setFootholdSN(List<Integer> footholdSN) {
      this.footholdSN = footholdSN;
   }

   public int getHomeX() {
      return this.homeX;
   }

   public void setHomeX(int homeX) {
      this.homeX = homeX;
   }

   public int getHomeY() {
      return this.homeY;
   }

   public void setHomeY(int homeY) {
      this.homeY = homeY;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTags() {
      return this.tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public List<DynamicObject.CollisionDisease> getUserCollisionDisease() {
      return this.userCollisionDisease;
   }

   public void setUserCollisionDisease(List<DynamicObject.CollisionDisease> userCollisionDisease) {
      this.userCollisionDisease = userCollisionDisease;
   }

   public boolean isWaiting() {
      return this.waiting;
   }

   public void setWaiting(boolean waiting) {
      this.waiting = waiting;
   }

   public int getWaitingDuration() {
      return this.waitingDuration;
   }

   public void setWaitingDuration(int waitingDuration) {
      this.waitingDuration = waitingDuration;
   }

   public int getPosX() {
      return this.posX;
   }

   public void setPosX(int posX) {
      this.posX = posX;
   }

   public int getPosY() {
      return this.posY;
   }

   public void setPosY(int posY) {
      this.posY = posY;
   }

   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.name);
      packet.write(0);
      packet.writeInt(this.curState);
      packet.writeInt(this.posX);
      packet.writeInt(this.posY);
   }

   public void reset() {
      this.posX = this.homeX;
      this.posY = this.homeY;
      this.curState = 0;
      this.waiting = false;
      this.waitingDuration = 0;
      this.beginShowTime = 0L;
      this.userCollisionDisease.clear();
   }

   public static class CollisionDisease {
      private int diseaseSkillID;
      private int diseaseSkillLevel;

      public CollisionDisease(int diseaseSkillID, int diseaseSkillLevel) {
         this.setDiseaseSkillID(diseaseSkillID);
         this.setDiseaseSkillLevel(diseaseSkillLevel);
      }

      public int getDiseaseSkillID() {
         return this.diseaseSkillID;
      }

      public void setDiseaseSkillID(int diseaseSkillID) {
         this.diseaseSkillID = diseaseSkillID;
      }

      public int getDiseaseSkillLevel() {
         return this.diseaseSkillLevel;
      }

      public void setDiseaseSkillLevel(int diseaseSkillLevel) {
         this.diseaseSkillLevel = diseaseSkillLevel;
      }
   }
}
