package objects.context.friend;

public class FriendEntry {
   private String name;
   private String group;
   private String memo;
   private int accId;
   private int characterId;
   private int channel = -1;
   private int level;
   private int job;
   private boolean visible;

   public FriendEntry(String name, int accountId, int characterId, String group, int channel, boolean visible, int level, int job, String memo) {
      this.name = name;
      this.accId = accountId;
      this.characterId = characterId;
      this.group = group;
      this.channel = channel;
      this.visible = visible;
      this.level = level;
      this.job = job;
      this.memo = memo;
   }

   public int getChannel() {
      return this.channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   public boolean isOnline() {
      return this.channel >= 0;
   }

   public void setOffline() {
      this.channel = -1;
   }

   public String getName() {
      return this.name;
   }

   public int getCharacterId() {
      return this.characterId;
   }

   public int getAccountId() {
      return this.accId;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public String getGroupName() {
      return this.group == null ? "그룹 미지정" : this.group;
   }

   public void setGroupName(String groupName) {
      this.group = groupName;
   }

   public String getMemo() {
      return this.memo == null ? "" : this.memo;
   }

   public void setMemo(String m) {
      this.memo = m;
   }

   public int getLevel() {
      return this.level;
   }

   public int getJob() {
      return this.job;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setCharacterId(int id) {
      this.characterId = id;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + this.accId;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         FriendEntry other = (FriendEntry)obj;
         return this.accId == other.accId;
      }
   }
}
