package objects.fields;

import network.encode.PacketEncoder;

public class DebuffObj {
   private int activeRate;
   private int dataType;
   private String effectName;
   private int key;
   private long lastActive;
   private String objectTag;

   public DebuffObj(int key, int dataType, String objectTag, String effectName, int activeRate) {
      this.key = key;
      this.dataType = dataType;
      this.objectTag = objectTag;
      this.effectName = effectName;
      this.activeRate = activeRate;
   }

   public int getActiveRate() {
      return this.activeRate;
   }

   public void setActiveRate(int activeRate) {
      this.activeRate = activeRate;
   }

   public int getDataType() {
      return this.dataType;
   }

   public void setDataType(int dataType) {
      this.dataType = dataType;
   }

   public String getEffectName() {
      return this.effectName;
   }

   public void setEffectName(String effectName) {
      this.effectName = effectName;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public long getLastActive() {
      return this.lastActive;
   }

   public void setLastActive(long lastActive) {
      this.lastActive = lastActive;
   }

   public String getObjectTag() {
      return this.objectTag;
   }

   public void setObjectTag(String objectTag) {
      this.objectTag = objectTag;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.key);
      packet.writeInt(this.dataType);
      packet.writeMapleAsciiString(this.objectTag);
      packet.writeMapleAsciiString(this.effectName);
   }
}
