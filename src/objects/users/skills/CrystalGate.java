package objects.users.skills;

import network.encode.PacketEncoder;

public class CrystalGate {
   private int playerID;
   private int totalCount;
   private int skillID;
   private int fieldID;
   private int x;
   private int y;
   private CrystalGate.GateType type;
   private int remainDuration;
   private long createTime;

   public CrystalGate(int playerID, int totalCount, int skillID, int fieldID, int x, int y, int remainDuration, CrystalGate.GateType type) {
      this.setPlayerID(playerID);
      this.setTotalCount(totalCount);
      this.setSkillID(skillID);
      this.setFieldID(fieldID);
      this.setX(x);
      this.setY(y);
      this.setRemainDuration(remainDuration);
      this.setType(type);
      this.setCreateTime(System.currentTimeMillis());
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public int getTotalCount() {
      return this.totalCount;
   }

   public void setTotalCount(int totalCount) {
      this.totalCount = totalCount;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public int getFieldID() {
      return this.fieldID;
   }

   public void setFieldID(int fieldID) {
      this.fieldID = fieldID;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getRemainDuration() {
      return this.remainDuration;
   }

   public void setRemainDuration(int remainDuration) {
      this.remainDuration = remainDuration;
   }

   public CrystalGate.GateType getType() {
      return this.type;
   }

   public void setType(CrystalGate.GateType type) {
      this.type = type;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getPlayerID());
      packet.writeInt(this.getTotalCount());
      packet.writeInt(this.getType().getType());
      packet.writeInt(this.getSkillID());
      packet.writeInt(this.getFieldID());
      packet.writeInt(this.getX());
      packet.writeInt(this.getY());
      packet.writeInt(this.getRemainDuration());
      packet.writeInt(0);
   }

   public long getCreateTime() {
      return this.createTime;
   }

   public void setCreateTime(long createTime) {
      this.createTime = createTime;
   }

   public static enum GateType {
      CrystalGate(1),
      AbyssalLightning(2);

      private int type;

      private GateType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
