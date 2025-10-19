package objects.quest;

import constants.GameConstants;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RandomRewards;
import network.game.GameServer;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.item.InventoryException;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleStat;
import objects.users.MapleTrait;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Triple;

public class MapleQuestAction implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private MapleQuestActionType type;
   private MapleQuest quest;
   private int intStore = 0;
   private List<Integer> applicableJobs = new ArrayList<>();
   private List<MapleQuestAction.QuestItem> items = null;
   private List<Triple<Integer, Integer, Integer>> skill = null;
   private List<Pair<Integer, Integer>> state = null;

   public MapleQuestAction(MapleQuestActionType type, ResultSet rse, MapleQuest quest, PreparedStatement pss, PreparedStatement psq, PreparedStatement psi) throws SQLException {
      this.type = type;
      this.quest = quest;
      this.intStore = rse.getInt("intStore");
      String[] jobs = rse.getString("applicableJobs").split(", ");
      if (jobs.length <= 0 && rse.getString("applicableJobs").length() > 0) {
         this.applicableJobs.add(Integer.parseInt(rse.getString("applicableJobs")));
      }

      for (String j : jobs) {
         if (j.length() > 0) {
            this.applicableJobs.add(Integer.parseInt(j));
         }
      }

      switch (type) {
         case item:
            this.items = new ArrayList<>();
            psi.setInt(1, rse.getInt("uniqueid"));
            ResultSet rs = psi.executeQuery();

            while (rs.next()) {
               this.items
                  .add(
                     new MapleQuestAction.QuestItem(
                        rs.getInt("itemid"),
                        rs.getInt("count"),
                        rs.getInt("period"),
                        rs.getInt("gender"),
                        rs.getInt("job"),
                        rs.getInt("jobEx"),
                        rs.getInt("prop")
                     )
                  );
            }

            rs.close();
            break;
         case quest:
            this.state = new ArrayList<>();
            psq.setInt(1, rse.getInt("uniqueid"));
            rs = psq.executeQuery();

            while (rs.next()) {
               this.state.add(new Pair<>(rs.getInt("quest"), rs.getInt("state")));
            }

            rs.close();
            break;
         case skill:
            this.skill = new ArrayList<>();
            pss.setInt(1, rse.getInt("uniqueid"));
            rs = pss.executeQuery();

            while (rs.next()) {
               this.skill.add(new Triple<>(rs.getInt("skillid"), rs.getInt("skillLevel"), rs.getInt("masterLevel")));
            }

            rs.close();
      }
   }

   private static boolean canGetItem(MapleQuestAction.QuestItem item, MapleCharacter c) {
      if (item.gender != 2 && item.gender >= 0 && item.gender != c.getGender()) {
         return false;
      } else if (item.job <= 0) {
         return true;
      } else {
         List<Integer> code = getJobBy5ByteEncoding(item.job);
         boolean jobFound = false;

         for (int codec : code) {
            if (codec / 100 == c.getJob() / 100) {
               jobFound = true;
               break;
            }
         }

         if (!jobFound && item.jobEx > 0) {
            for (int codecx : getJobBySimpleEncoding(item.jobEx)) {
               if (codecx / 100 % 10 == c.getJob() / 100 % 10) {
                  jobFound = true;
                  break;
               }
            }
         }

         return jobFound;
      }
   }

   public final boolean RestoreLostItem(MapleCharacter c, int itemid) {
      if (this.type == MapleQuestActionType.item) {
         for (MapleQuestAction.QuestItem item : this.items) {
            if (item.itemid == itemid) {
               if (!c.haveItem(item.itemid, item.count, true, false)) {
                  MapleInventoryManipulator.addById(
                     c.getClient(),
                     item.itemid,
                     (short)item.count,
                     "Obtained from quest (Restored) " + this.quest.getId() + " on " + FileoutputUtil.CurrentReadable_Date()
                  );
               }

               return true;
            }
         }
      }

      return false;
   }

   public void runStart(MapleCharacter c, Integer extSelection) {
      switch (this.type) {
         case item:
            Map<Integer, Integer> props = new HashMap<>();

            for (MapleQuestAction.QuestItem item : this.items) {
               if (item.prop > 0 && canGetItem(item, c)) {
                  for (int i = 0; i < item.prop; i++) {
                     props.put(props.size(), item.itemid);
                  }
               }
            }

            int selection = 0;
            int extNum = 0;
            if (props.size() > 0) {
               selection = props.get(Randomizer.nextInt(props.size()));
            }

            for (MapleQuestAction.QuestItem itemx : this.items) {
               if (canGetItem(itemx, c)) {
                  int id = itemx.itemid;
                  if (itemx.prop == -2 || (itemx.prop == -1 ? extSelection == null || extSelection == extNum++ : id == selection)) {
                     short count = (short)itemx.count;
                     if (count < 0) {
                        try {
                           MapleInventoryManipulator.removeById(c.getClient(), GameConstants.getInventoryType(id), id, count * -1, true, false);
                        } catch (InventoryException var18) {
                           System.err.println("[h4x] Completing a quest without meeting the requirements" + var18);
                        }

                        c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, count, true));
                     } else {
                        int period = itemx.period / 1440;
                        String name = MapleItemInformationProvider.getInstance().getName(id);
                        if (id / 10000 == 114 && name != null && name.length() > 0) {
                           String msg = "<" + name + "> 훈장을 획득하셨습니다!";
                           c.dropMessage(-1, msg);
                           c.dropMessage(5, msg);
                        }

                        MapleInventoryManipulator.addById(
                           c.getClient(),
                           id,
                           count,
                           "",
                           null,
                           period,
                           "Obtained from quest " + this.quest.getId() + " on " + FileoutputUtil.CurrentReadable_Date()
                        );
                        c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, count, true));
                     }
                  }
               }
            }
            break;
         case quest:
            for (Pair<Integer, Integer> q : this.state) {
               c.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(q.left), q.right));
            }
            break;
         case skill:
            Map<Skill, SkillEntry> sa = new HashMap<>();

            for (Triple<Integer, Integer, Integer> skills : this.skill) {
               int skillid = skills.left;
               int skillLevel = skills.mid;
               int masterLevel = skills.right;
               Skill skillObject = SkillFactory.getSkill(skillid);
               boolean found = false;

               for (int applicableJob : this.applicableJobs) {
                  if (c.getJob() == applicableJob) {
                     found = true;
                     break;
                  }
               }

               if (skillObject.isBeginnerSkill() || found) {
                  sa.put(
                     skillObject,
                     new SkillEntry(
                        (byte)Math.max(skillLevel, c.getSkillLevel(skillObject)),
                        (byte)Math.max(masterLevel, c.getMasterLevel(skillObject)),
                        SkillFactory.getDefaultSExpiry(skillObject)
                     )
                  );
               }
            }

            c.changeSkillsLevel(sa);
            break;
         case exp:
            MapleQuestStatus statusxxxx = c.getQuest(this.quest);
            if (statusxxxx.getForfeited() <= 0) {
               c.gainExp(
                  this.intStore
                     * GameConstants.getExpRate_Quest(c.getLevel())
                     * c.getStat().questBonus
                     * (c.getTrait(MapleTrait.MapleTraitType.sense).getLevel() * 3 / 10 + 100)
                     / 100,
                  true,
                  true,
                  true
               );
            }
            break;
         case transferField:
            Field to = GameServer.getInstance(c.getClient().getChannel()).getMapFactory().getMap(this.intStore);
            c.changeMap(to, to.getPortal(0));
         case nextQuest:
         case infoNumber:
         default:
            break;
         case money:
            MapleQuestStatus statusxxx = c.getQuest(this.quest);
            if (statusxxx.getForfeited() <= 0) {
               c.gainMeso(this.intStore, true, true, true);
            }
            break;
         case pop:
            MapleQuestStatus statusxx = c.getQuest(this.quest);
            if (statusxx.getForfeited() <= 0) {
               int fameGain = this.intStore;
               c.addFame(fameGain);
               c.updateSingleStat(MapleStat.FAME, c.getFame());
               c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowFameGain(fameGain));
            }
            break;
         case buffItemID:
            MapleQuestStatus statusx = c.getQuest(this.quest);
            if (statusx.getForfeited() <= 0) {
               int tobuff = this.intStore;
               if (tobuff > 0) {
                  MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(c);
               }
            }
            break;
         case sp:
            MapleQuestStatus statusxxxxx = c.getQuest(this.quest);
            if (statusxxxxx.getForfeited() <= 0) {
               int sp_val = this.intStore;
               if (this.applicableJobs.size() > 0) {
                  int finalJob = 0;

                  for (int job_val : this.applicableJobs) {
                     if (c.getJob() >= job_val && job_val > finalJob) {
                        finalJob = job_val;
                     }
                  }

                  if (finalJob == 0) {
                     c.gainSP(sp_val);
                  } else {
                     c.gainSP(sp_val, GameConstants.getSkillBook(finalJob, 0));
                  }
               } else {
                  c.gainSP(sp_val);
               }
            }
            break;
         case charmEXP:
         case charismaEXP:
         case craftEXP:
         case insightEXP:
         case senseEXP:
         case willEXP:
            MapleQuestStatus status = c.getQuest(this.quest);
            if (status.getForfeited() <= 0) {
               c.getTrait(MapleTrait.MapleTraitType.getByQuestName(this.type.name())).addExp(this.intStore, c);
            }
      }
   }

   public boolean checkEnd(MapleCharacter c, Integer extSelection) {
      switch (this.type) {
         case item:
            Map<Integer, Integer> props = new HashMap<>();

            for (MapleQuestAction.QuestItem item : this.items) {
               if (item.prop > 0 && canGetItem(item, c)) {
                  for (int i = 0; i < item.prop; i++) {
                     props.put(props.size(), item.itemid);
                  }
               }
            }

            int selection = 0;
            int extNum = 0;
            if (props.size() > 0) {
               selection = props.get(Randomizer.nextInt(props.size()));
            }

            byte eq = 0;
            byte use = 0;
            byte setup = 0;
            byte etc = 0;
            byte cash = 0;

            for (MapleQuestAction.QuestItem itemx : this.items) {
               if (canGetItem(itemx, c)) {
                  int id = itemx.itemid;
                  if (itemx.prop == -2 || (itemx.prop == -1 ? extSelection == null || extSelection == extNum++ : id == selection)) {
                     short count = (short)itemx.count;
                     if (count < 0) {
                        if (!c.haveItem(id, count, false, true)) {
                           c.dropMessage(1, "You are short of some item to complete quest.");
                           return false;
                        }
                     } else {
                        if (MapleItemInformationProvider.getInstance().isPickupRestricted(id) && c.haveItem(id, 1, true, false)) {
                           c.dropMessage(1, "You have this item already: " + MapleItemInformationProvider.getInstance().getName(id));
                           return false;
                        }

                        switch (GameConstants.getInventoryType(id)) {
                           case EQUIP:
                              eq++;
                              break;
                           case USE:
                              use++;
                              break;
                           case SETUP:
                              setup++;
                              break;
                           case ETC:
                              etc++;
                              break;
                           case CASH:
                              cash++;
                        }
                     }
                  }
               }
            }

            if (c.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq) {
               c.dropMessage(1, "장비 인벤토리 공간을 " + eq + "칸 이상 비운 후 다시 시도해주시기 바랍니다.");
               return false;
            } else if (c.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use) {
               c.dropMessage(1, "소비 인벤토리 공간을 " + use + "칸 이상 비운 후 다시 시도해주시기 바랍니다.");
               return false;
            } else if (c.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup) {
               c.dropMessage(1, "설치 인벤토리 공간을 " + setup + "칸 이상 비운 후 다시 시도해주시기 바랍니다.");
               return false;
            } else if (c.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc) {
               c.dropMessage(1, "기타 인벤토리 공간을 " + etc + "칸 이상 비운 후 다시 시도해주시기 바랍니다.");
               return false;
            } else {
               if (c.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
                  c.dropMessage(1, "캐시 인벤토리 공간을 " + cash + "칸 이상 비운 후 다시 시도해주시기 바랍니다.");
                  return false;
               }

               return true;
            }
         case money:
            int meso = this.intStore;
            if (c.getMeso() + meso < 0L) {
               c.dropMessage(1, "Meso exceed the max amount, 2147483647.");
               return false;
            } else {
               if (meso < 0 && c.getMeso() < Math.abs(meso)) {
                  c.dropMessage(1, "Insufficient meso.");
                  return false;
               }

               return true;
            }
         default:
            return true;
      }
   }

   public void runEnd(MapleCharacter c, Integer extSelection) {
      switch (this.type) {
         case item:
            Map<Integer, Integer> props = new HashMap<>();

            for (MapleQuestAction.QuestItem item : this.items) {
               if (item.prop > 0 && canGetItem(item, c)) {
                  for (int i = 0; i < item.prop; i++) {
                     props.put(props.size(), item.itemid);
                  }
               }
            }

            int selection = 0;
            int extNum = 0;
            if (props.size() > 0) {
               selection = props.get(Randomizer.nextInt(props.size()));
            }

            for (MapleQuestAction.QuestItem itemx : this.items) {
               if (canGetItem(itemx, c)) {
                  int id = itemx.itemid;
                  if (itemx.prop == -2 || (itemx.prop == -1 ? extSelection == null || extSelection == extNum++ : id == selection)) {
                     short count = (short)itemx.count;
                     if (count < 0) {
                        MapleInventoryManipulator.removeById(c.getClient(), GameConstants.getInventoryType(id), id, count * -1, true, false);
                        c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, count, true));
                     } else {
                        int period = itemx.period / 1440;
                        String name = MapleItemInformationProvider.getInstance().getName(id);
                        if (id / 10000 == 114 && name != null && name.length() > 0) {
                           String msg = "<" + name + "> 훈장을 획득하셨습니다!";
                           c.dropMessage(-1, msg);
                           c.dropMessage(5, msg);
                        }

                        MapleInventoryManipulator.addById(c.getClient(), id, count, "", null, period + " on " + FileoutputUtil.CurrentReadable_Date());
                        c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, count, true));
                     }
                  }
               }
            }
            break;
         case quest:
            for (Pair<Integer, Integer> q : this.state) {
               c.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(q.left), q.right));
            }
            break;
         case skill:
            Map<Skill, SkillEntry> sa = new HashMap<>();

            for (Triple<Integer, Integer, Integer> skills : this.skill) {
               int skillid = skills.left;
               int skillLevel = skills.mid;
               int masterLevel = skills.right;
               Skill skillObject = SkillFactory.getSkill(skillid);
               boolean found = false;

               for (int applicableJob : this.applicableJobs) {
                  if (c.getJob() == applicableJob) {
                     found = true;
                     break;
                  }
               }

               if (skillObject.isBeginnerSkill() || found) {
                  sa.put(
                     skillObject,
                     new SkillEntry(
                        (byte)Math.max(skillLevel, c.getSkillLevel(skillObject)),
                        (byte)Math.max(masterLevel, c.getMasterLevel(skillObject)),
                        SkillFactory.getDefaultSExpiry(skillObject)
                     )
                  );
               }
            }

            c.changeSkillsLevel(sa);
            break;
         case exp:
            c.gainExp(
               this.intStore
                  * GameConstants.getExpRate_Quest(c.getLevel())
                  * c.getStat().questBonus
                  * (c.getTrait(MapleTrait.MapleTraitType.sense).getLevel() * 3 / 10 + 100)
                  / 100,
               true,
               true,
               true
            );
            break;
         case transferField:
            Field to = GameServer.getInstance(c.getClient().getChannel()).getMapFactory().getMap(this.intStore);
            c.changeMap(to, to.getPortal(0));
         case nextQuest:
         case infoNumber:
         default:
            break;
         case money:
            c.gainMeso(this.intStore, true, true, true);
            break;
         case pop:
            int fameGain = this.intStore;
            c.addFame(fameGain);
            c.updateSingleStat(MapleStat.FAME, c.getFame());
            c.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getShowFameGain(fameGain));
            break;
         case buffItemID:
            int tobuff = this.intStore;
            if (tobuff > 0) {
               MapleItemInformationProvider.getInstance().getItemEffect(tobuff).applyTo(c);
            }
            break;
         case sp:
            int sp_val = this.intStore;
            if (this.applicableJobs.size() > 0) {
               int finalJob = 0;

               for (int job_val : this.applicableJobs) {
                  if (c.getJob() >= job_val && job_val > finalJob) {
                     finalJob = job_val;
                  }
               }

               if (finalJob == 0) {
                  c.gainSP(sp_val);
               } else {
                  c.gainSP(sp_val, GameConstants.getSkillBook(finalJob, 0));
               }
            } else {
               c.gainSP(sp_val);
            }
            break;
         case charmEXP:
         case charismaEXP:
         case craftEXP:
         case insightEXP:
         case senseEXP:
         case willEXP:
            c.getTrait(MapleTrait.MapleTraitType.getByQuestName(this.type.name())).addExp(this.intStore, c);
      }
   }

   private static List<Integer> getJobBy5ByteEncoding(int encoded) {
      List<Integer> ret = new ArrayList<>();
      if ((encoded & 1) != 0) {
         ret.add(0);
      }

      if ((encoded & 2) != 0) {
         ret.add(100);
      }

      if ((encoded & 4) != 0) {
         ret.add(200);
      }

      if ((encoded & 8) != 0) {
         ret.add(300);
      }

      if ((encoded & 16) != 0) {
         ret.add(400);
      }

      if ((encoded & 32) != 0) {
         ret.add(500);
      }

      if ((encoded & 1024) != 0) {
         ret.add(1000);
      }

      if ((encoded & 2048) != 0) {
         ret.add(1100);
      }

      if ((encoded & 4096) != 0) {
         ret.add(1200);
      }

      if ((encoded & 8192) != 0) {
         ret.add(1300);
      }

      if ((encoded & 16384) != 0) {
         ret.add(1400);
      }

      if ((encoded & 32768) != 0) {
         ret.add(1500);
      }

      if ((encoded & 131072) != 0) {
         ret.add(2001);
         ret.add(2200);
      }

      if ((encoded & 1048576) != 0) {
         ret.add(2000);
         ret.add(2001);
      }

      if ((encoded & 2097152) != 0) {
         ret.add(2100);
      }

      if ((encoded & 4194304) != 0) {
         ret.add(2001);
         ret.add(2200);
      }

      if ((encoded & 1073741824) != 0) {
         ret.add(3000);
         ret.add(3200);
         ret.add(3300);
         ret.add(3500);
      }

      return ret;
   }

   private static List<Integer> getJobBySimpleEncoding(int encoded) {
      List<Integer> ret = new ArrayList<>();
      if ((encoded & 1) != 0) {
         ret.add(200);
      }

      if ((encoded & 2) != 0) {
         ret.add(300);
      }

      if ((encoded & 4) != 0) {
         ret.add(400);
      }

      if ((encoded & 8) != 0) {
         ret.add(500);
      }

      return ret;
   }

   public MapleQuestActionType getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return this.type.toString();
   }

   public List<Triple<Integer, Integer, Integer>> getSkills() {
      return this.skill;
   }

   public List<MapleQuestAction.QuestItem> getItems() {
      return this.items;
   }

   public int getIntData() {
      return this.intStore;
   }

   public static class QuestItem {
      public int itemid;
      public int count;
      public int period;
      public int gender;
      public int job;
      public int jobEx;
      public int prop;

      public QuestItem(int itemid, int count, int period, int gender, int job, int jobEx, int prop) {
         if (RandomRewards.getTenPercent().contains(itemid)) {
            count += Randomizer.nextInt(3);
         }

         this.itemid = itemid;
         this.count = count;
         this.period = period;
         this.gender = gender;
         this.job = job;
         this.jobEx = jobEx;
         this.prop = prop;
      }
   }
}
