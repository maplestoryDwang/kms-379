package objects.quest;

import constants.GameConstants;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MaplePet;
import objects.users.MapleCharacter;
import objects.users.MapleTrait;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.utils.Pair;

public class MapleQuestRequirement implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private MapleQuest quest;
   private MapleQuestRequirementType type;
   private int intStore;
   private String stringStore;
   private List<Pair<String, Integer>> dataStore;

   public MapleQuestRequirement(MapleQuest quest, MapleQuestRequirementType type, ResultSet rse) throws SQLException {
      this.type = type;
      this.quest = quest;
      switch (type) {
         case pet:
         case mbcard:
         case mob:
         case item:
         case quest:
         case skill:
         case job:
         case infoex:
            this.dataStore = new ArrayList<>();
            String[] first = rse.getString("intStoresFirst").split(", ");
            String[] second = rse.getString("intStoresSecond").split(", ");
            if (first.length <= 0 && rse.getString("intStoresFirst").length() > 0) {
               this.dataStore.add(new Pair<>(rse.getString("intStoresFirst"), Integer.parseInt(rse.getString("intStoresSecond"))));
            }

            for (int i = 0; i < first.length; i++) {
               if (first[i].length() > 0 && second[i].length() > 0) {
                  this.dataStore.add(new Pair<>(first[i], Integer.parseInt(second[i])));
               }
            }
            break;
         case partyQuest_S:
         case dayByDay:
         case normalAutoStart:
         case subJobFlags:
         case fieldEnter:
         case pettamenessmin:
         case npc:
         case questComplete:
         case pop:
         case interval:
         case mbmin:
         case lvmax:
         case lvmin:
         case comboKill:
         case multiKillCount:
         case runeAct:
         case breakTimeField:
         case suddenMissionClearCount:
            this.intStore = Integer.parseInt(rse.getString("stringStore"));
            break;
         case end:
            this.stringStore = rse.getString("stringStore");
      }
   }

   public boolean check(MapleCharacter c, Integer npcid) {
      switch (this.type) {
         case pet:
            for (Pair<String, Integer> ax : this.dataStore) {
               if (c.getPetById(ax.getRight()) != -1) {
                  return true;
               }
            }

            return false;
         case mbcard:
         case dayByDay:
         case normalAutoStart:
         case mbmin:
         case runeAct:
         case breakTimeField:
         case suddenMissionClearCount:
         default:
            return true;
         case mob:
            for (Pair<String, Integer> a : this.dataStore) {
               int mobId = Integer.parseInt(a.getLeft());
               int killReq = a.getRight();
               if (c.getQuest(this.quest).getMobKills(mobId) < killReq) {
                  return false;
               }
            }

            return true;
         case item:
            for (Pair<String, Integer> a : this.dataStore) {
               int itemId = Integer.parseInt(a.getLeft());
               short quantity = 0;
               MapleInventoryType iType = GameConstants.getInventoryType(itemId);

               for (Item item : c.getInventory(iType).listById(itemId)) {
                  quantity += item.getQuantity();
               }

               int count = a.getRight();
               if (quantity < count || count <= 0 && quantity > 0) {
                  return false;
               }
            }

            return true;
         case quest:
            for (Pair<String, Integer> axxx : this.dataStore) {
               MapleQuestStatus q = c.getQuest(MapleQuest.getInstance(Integer.parseInt(axxx.getLeft())));
               int state = axxx.getRight();
               if (state != 0 && (q != null || state != 0) && (q == null || q.getStatus() != state)) {
                  return false;
               }
            }

            return true;
         case skill:
            for (Pair<String, Integer> axxx : this.dataStore) {
               boolean acquire = axxx.getRight() > 0;
               int skill = Integer.parseInt(axxx.getLeft());
               Skill skil = SkillFactory.getSkill(skill);
               if (skill == 91000009 && c.getGuild().getSkillLevel(skill) >= 1) {
                  return true;
               }

               if (!acquire) {
                  if (c.getSkillLevel(skil) > 0 || c.getMasterLevel(skil) > 0) {
                     return false;
                  }
               } else if (skil.isFourthJob()) {
                  if (c.getMasterLevel(skil) == 0) {
                     return false;
                  }
               } else if (c.getSkillLevel(skil) == 0) {
                  return false;
               }
            }

            return true;
         case job:
            for (Pair<String, Integer> axx : this.dataStore) {
               if (axx.getRight() == c.getJob() || c.isGM()) {
                  return true;
               }
            }

            return false;
         case infoex:
            for (Pair<String, Integer> data : this.dataStore) {
               try {
                  int info = Integer.parseInt(c.getOneInfo(this.quest.getId(), data.getLeft()));
                  if (info < data.getRight()) {
                     return false;
                  }
               } catch (Exception var15) {
                  return false;
               }
            }

            return true;
         case partyQuest_S:
            int[] partyQuests = new int[]{1200, 1201, 1202, 1203, 1204, 1205, 1206, 1300, 1301, 1302};
            int sRankings = 0;

            for (int i : partyQuests) {
               String rank = c.getOneInfo(i, "rank");
               if (rank != null && rank.equals("S")) {
                  sRankings++;
               }
            }

            return sRankings >= 5;
         case subJobFlags:
            return (1 << c.getSubcategory() & this.intStore) != 0;
         case fieldEnter:
            if (this.intStore > 0) {
               return this.intStore == c.getMapId();
            }

            return true;
         case pettamenessmin:
            for (MaplePet pet : c.getPets()) {
               if (pet != null && pet.getSummoned() && pet.getCloseness() >= this.intStore) {
                  return true;
               }
            }

            return false;
         case npc:
            return npcid == null || npcid == this.intStore;
         case questComplete:
            return c.getNumQuest() >= this.intStore;
         case pop:
            return c.getFame() >= this.intStore;
         case interval:
            return c.getQuest(this.quest).getStatus() != 2
               || c.getQuest(this.quest).getCompletionTime() <= System.currentTimeMillis() - this.intStore * 60 * 1000L;
         case lvmax:
            return c.getLevel() <= this.intStore;
         case lvmin:
            return c.getLevel() >= this.intStore;
         case comboKill:
            return c.getMonsterCombo() >= this.intStore;
         case multiKillCount:
            return c.getMultiKillCount() >= this.intStore;
         case end:
            String timeStr = this.stringStore;
            if (timeStr != null && timeStr.length() > 0) {
               Calendar cal = Calendar.getInstance();
               cal.set(
                  Integer.parseInt(timeStr.substring(0, 4)),
                  Integer.parseInt(timeStr.substring(4, 6)),
                  Integer.parseInt(timeStr.substring(6, 8)),
                  Integer.parseInt(timeStr.substring(8, 10)),
                  0
               );
               return cal.getTimeInMillis() >= System.currentTimeMillis();
            }

            return true;
         case craftMin:
         case willMin:
         case charismaMin:
         case insightMin:
         case charmMin:
         case senseMin:
            return c.getTrait(MapleTrait.MapleTraitType.getByQuestName(this.type.name())).getLevel() >= this.intStore;
      }
   }

   public MapleQuestRequirementType getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return this.type.toString();
   }

   public List<Pair<String, Integer>> getDataStore() {
      return this.dataStore;
   }
}
