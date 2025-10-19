package objects.quest;

import constants.GameConstants;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import objects.fields.gameobject.lifes.MapleLifeFactory;

public class MapleQuestStatus implements Serializable {
   private static final long serialVersionUID = 91795419934134L;
   private transient MapleQuest quest;
   private byte status;
   private Map<Integer, Integer> killedMobs = null;
   private int npc;
   private long completionTime;
   private int forfeited = 0;
   private String customData;

   public MapleQuestStatus(MapleQuest quest, int status) {
      this.quest = quest;
      this.setStatus((byte)status);
      this.completionTime = System.currentTimeMillis();
      if (status == 1 && !quest.getRelevantMobs().isEmpty()) {
         this.registerMobs();
      }
   }

   public MapleQuestStatus(MapleQuest quest, byte status, int npc) {
      this.quest = quest;
      this.setStatus(status);
      this.setNpc(npc);
      this.completionTime = System.currentTimeMillis();
      if (status == 1 && !quest.getRelevantMobs().isEmpty()) {
         this.registerMobs();
      }
   }

   public final void setQuest(int qid) {
      this.quest = MapleQuest.getInstance(qid);
   }

   public final MapleQuest getQuest() {
      return this.quest;
   }

   public final byte getStatus() {
      return this.status;
   }

   public final void setStatus(byte status) {
      this.status = status;
   }

   public final int getNpc() {
      return this.npc;
   }

   public final void setNpc(int npc) {
      this.npc = npc;
   }

   public boolean isCustom() {
      return GameConstants.isCustomQuest(this.quest.getId());
   }

   private final void registerMobs() {
      this.killedMobs = new LinkedHashMap<>();

      for (int i : this.quest.getRelevantMobs().keySet()) {
         this.killedMobs.put(i, 0);
      }
   }

   public final int maxMob(int mobid) {
      for (Entry<Integer, Integer> qs : this.quest.getRelevantMobs().entrySet()) {
         if (qs.getKey() == mobid) {
            return qs.getValue();
         }
      }

      return 0;
   }

   public final boolean mobKilled(int id, int killCount, int skillID) {
      if (this.quest != null && this.quest.getSkillID() > 0 && this.quest.getSkillID() != skillID) {
         return false;
      } else {
         Integer mob = this.killedMobs.get(id);
         if (mob != null) {
            int mo = this.maxMob(id);
            if (mob >= mo) {
               return false;
            } else {
               this.killedMobs.put(id, Math.min(mob + killCount, mo));
               return true;
            }
         } else {
            for (Entry<Integer, Integer> mo : this.killedMobs.entrySet()) {
               if (this.questCount(mo.getKey(), id)) {
                  int mobb = this.maxMob(mo.getKey());
                  if (mo.getValue() >= mobb) {
                     return false;
                  }

                  this.killedMobs.put(mo.getKey(), Math.min(mo.getValue() + killCount, mobb));
                  return true;
               }
            }

            return false;
         }
      }
   }

   private final boolean questCount(int mo, int id) {
      if (MapleLifeFactory.getQuestCount(mo) != null) {
         for (int i : MapleLifeFactory.getQuestCount(mo)) {
            if (i == id) {
               return true;
            }
         }
      }

      return false;
   }

   public final void setMobKills(int id, int count) {
      if (this.killedMobs == null) {
         this.registerMobs();
      }

      this.killedMobs.put(id, count);
   }

   public final boolean hasMobKills() {
      return this.killedMobs == null ? false : this.killedMobs.size() > 0;
   }

   public final int getMobKills(int id) {
      Integer mob = this.killedMobs.get(id);
      return mob == null ? 0 : mob;
   }

   public final Map<Integer, Integer> getMobKills() {
      return this.killedMobs;
   }

   public final long getCompletionTime() {
      return this.completionTime;
   }

   public final void setCompletionTime(long completionTime) {
      this.completionTime = completionTime;
   }

   public final int getForfeited() {
      return this.forfeited;
   }

   public final void setForfeited(int forfeited) {
      if (forfeited >= this.forfeited) {
         this.forfeited = forfeited;
      } else {
         throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
      }
   }

   public final void setCustomData(String customData) {
      this.customData = customData;
   }

   public final String getCustomData() {
      return this.customData;
   }
}
