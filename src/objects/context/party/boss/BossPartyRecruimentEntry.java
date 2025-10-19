package objects.context.party.boss;

import network.encode.PacketEncoder;

public class BossPartyRecruimentEntry {
   private String title;
   private boolean activeRecruiment;
   private byte bossDifficulty;
   private int minLevel;
   private int minArcane;
   private int minAthentic;
   private int minDojangRank;
   private int minUnion;

   public BossPartyRecruimentEntry(String title, byte bossDifficulty, int minLevel, int minArcane, int minAthentic, int minDojangRank, int minUnion) {
      this.title = title;
      this.bossDifficulty = bossDifficulty;
      this.minLevel = minLevel;
      this.minArcane = minArcane;
      this.minAthentic = minAthentic;
      this.minDojangRank = minDojangRank;
      this.minUnion = minUnion;
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.bossDifficulty);
      packet.writeMapleAsciiString(this.title);
      packet.writeInt(this.minLevel);
      packet.writeInt(this.minArcane);
      packet.writeInt(this.minAthentic);
      packet.writeInt(this.minDojangRank);
      packet.writeInt(this.minUnion);
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public boolean isActiveRecruiment() {
      return this.activeRecruiment;
   }

   public void setActiveRecruiment(boolean activeRecruiment) {
      this.activeRecruiment = activeRecruiment;
   }

   public int getMinLevel() {
      return this.minLevel;
   }

   public void setMinLevel(int minLevel) {
      this.minLevel = minLevel;
   }

   public int getMinArcane() {
      return this.minArcane;
   }

   public void setMinArcane(int minArcane) {
      this.minArcane = minArcane;
   }

   public int getMinAthentic() {
      return this.minAthentic;
   }

   public void setMinAthentic(int minAthentic) {
      this.minAthentic = minAthentic;
   }

   public int getMinDojangRank() {
      return this.minDojangRank;
   }

   public void setMinDojangRank(int minDojangRank) {
      this.minDojangRank = minDojangRank;
   }

   public int getMinUnion() {
      return this.minUnion;
   }

   public void setMinUnion(int minUnion) {
      this.minUnion = minUnion;
   }

   public byte getBossDifficulty() {
      return this.bossDifficulty;
   }

   public void setBossDifficulty(byte bossDifficulty) {
      this.bossDifficulty = bossDifficulty;
   }
}
