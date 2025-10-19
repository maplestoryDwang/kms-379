package objects.fields.child.union;

public class UnionBoardInfo {
   private boolean changeable;
   private int groupIndex;
   private int openLevel;
   private int xPos;
   private int yPos;

   public UnionBoardInfo(boolean changeable, int groupIndex, int openLevel, int xPos, int yPos) {
      this.changeable = changeable;
      this.groupIndex = groupIndex;
      this.openLevel = openLevel;
      this.xPos = xPos;
      this.yPos = yPos;
   }

   public boolean isChangeable() {
      return this.changeable;
   }

   public void setChangeable(boolean changeable) {
      this.changeable = changeable;
   }

   public int getGroupIndex() {
      return this.groupIndex;
   }

   public void setGroupIndex(int groupIndex) {
      this.groupIndex = groupIndex;
   }

   public int getOpenLevel() {
      return this.openLevel;
   }

   public void setOpenLevel(int openLevel) {
      this.openLevel = openLevel;
   }

   public int getxPos() {
      return this.xPos;
   }

   public void setxPos(int xPos) {
      this.xPos = xPos;
   }

   public int getyPos() {
      return this.yPos;
   }

   public void setyPos(int yPos) {
      this.yPos = yPos;
   }
}
