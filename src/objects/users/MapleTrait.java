package objects.users;

import constants.GameConstants;
import network.models.CWvsContext;
import objects.users.achievement.AchievementFactory;

public class MapleTrait {
   private MapleTrait.MapleTraitType type;
   private int totalExp = 0;
   private int localTotalExp = 0;
   private int todayExp = 0;
   private short exp = 0;
   private byte level = 0;

   public int getTodayExp() {
      return this.todayExp;
   }

   public void setTodayExp(int todayExp) {
      this.todayExp = todayExp;
   }

   public MapleTrait(MapleTrait.MapleTraitType t) {
      this.type = t;
   }

   public void setExp(int e) {
      this.totalExp = e;
      this.localTotalExp = e;
      this.recalcLevel();
   }

   public void addExp(int e, MapleCharacter c) {
      this.addTrueExp(e * c.getClient().getChannelServer().getTraitRate(), c);
   }

   public void addTrueExp(int e, MapleCharacter c) {
      if (e != 0) {
         this.totalExp += e;
         this.localTotalExp += e;
         this.setTodayExp(this.getTodayExp() + e);
         c.updateSingleStat(this.type.stat, this.totalExp);
         c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.showTraitGain(this.type, e));
         AchievementFactory.checkNCStatExpUp(c, this);
         if (this.recalcLevel()) {
            AchievementFactory.checkNCStatLevelUp(c, this);
         }

         c.getStat().recalcLocalStats(c);
      }
   }

   public boolean recalcLevel() {
      if (this.totalExp < 0) {
         this.totalExp = 0;
         this.localTotalExp = 0;
         this.level = 0;
         this.exp = 0;
         return false;
      } else {
         int oldLevel = this.level;

         for (byte i = 0; i < 100; i++) {
            if (GameConstants.getTraitExpNeededForLevel(i) > this.localTotalExp) {
               this.exp = (short)(GameConstants.getTraitExpNeededForLevel(i) - this.localTotalExp);
               this.level = (byte)(i - 1);
               return this.level > oldLevel;
            }
         }

         this.exp = 0;
         this.level = 100;
         this.totalExp = GameConstants.getTraitExpNeededForLevel(this.level);
         this.localTotalExp = this.totalExp;
         return this.level > oldLevel;
      }
   }

   public int getLevel() {
      return this.level;
   }

   public int getExp() {
      return this.exp;
   }

   public int getTotalExp() {
      return this.totalExp;
   }

   public int getLocalTotalExp() {
      return this.localTotalExp;
   }

   public void addLocalExp(int e) {
      this.localTotalExp += e;
   }

   public void clearLocalExp() {
      this.localTotalExp = this.totalExp;
   }

   public MapleTrait.MapleTraitType getType() {
      return this.type;
   }

   public static enum MapleTraitType {
      charisma(500, MapleStat.CHARISMA, "카리스마"),
      insight(500, MapleStat.INSIGHT, "통찰력"),
      will(500, MapleStat.WILL, "의지"),
      craft(500, MapleStat.CRAFT, "손재주"),
      sense(500, MapleStat.SENSE, "감성"),
      charm(5000, MapleStat.CHARM, "매력");

      final int limit;
      final MapleStat stat;
      final String name;

      private MapleTraitType(int type, MapleStat theStat, String name) {
         this.limit = type;
         this.stat = theStat;
         this.name = name;
      }

      public int getLimit() {
         return this.limit;
      }

      public MapleStat getStat() {
         return this.stat;
      }

      public String getName() {
         return this.name;
      }

      public static MapleTrait.MapleTraitType getByName(String name) {
         for (MapleTrait.MapleTraitType t : values()) {
            if (t.name().equals(name)) {
               return t;
            }
         }

         return null;
      }

      public static MapleTrait.MapleTraitType getByQuestName(String q) {
         String qq = q.substring(0, q.length() - 3);

         for (MapleTrait.MapleTraitType t : values()) {
            if (t.name().equals(qq)) {
               return t;
            }
         }

         return null;
      }
   }
}
