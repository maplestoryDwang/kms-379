package objects.quest;

import constants.GameConstants;
import database.DBConfig;
import database.DBConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.MapleCharacter;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.caching.mission.submission.checkvalue.QuestChangeInfo;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Table;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;

public class MapleQuest implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private static final Map<Integer, MapleQuest> quests = new LinkedHashMap<>();
   protected int id;
   protected final List<MapleQuestRequirement> startReqs = new LinkedList<>();
   protected final List<MapleQuestRequirement> completeReqs = new LinkedList<>();
   protected final List<MapleQuestAction> startActs = new LinkedList<>();
   protected final List<MapleQuestAction> completeActs = new LinkedList<>();
   protected final Map<String, List<Pair<String, Pair<String, Integer>>>> partyQuestInfo = new LinkedHashMap<>();
   protected final Map<Integer, Integer> relevantMobs = new LinkedHashMap<>();
   private boolean autoStart = false;
   private boolean autoPreComplete = false;
   private boolean repeatable = false;
   private boolean customend = false;
   private boolean blocked = false;
   private boolean autoAccept = false;
   private boolean autoComplete = false;
   private boolean scriptedStart = false;
   private int viewMedalItem = 0;
   private int selectedSkillID = 0;
   private int startNavi = 0;
   private String startscript;
   private String endscript;
   private static List<ModifiedQuestTime> modifiedQuestTimes = new ArrayList<>();
   protected String name = "";
   private List<Pair<Integer, Integer>> requireItems = new ArrayList<>();

   protected MapleQuest(int id) {
      this.id = id;
   }

   private static MapleQuest loadQuest(
      ResultSet rs, PreparedStatement psr, PreparedStatement psa, PreparedStatement pss, PreparedStatement psq, PreparedStatement psi, PreparedStatement psp
   ) throws SQLException {
      MapleQuest ret = new MapleQuest(rs.getInt("questid"));
      ret.name = rs.getString("name");
      ret.autoStart = rs.getInt("autoStart") > 0;
      ret.autoPreComplete = rs.getInt("autoPreComplete") > 0;
      ret.autoAccept = rs.getInt("autoAccept") > 0;
      ret.autoComplete = rs.getInt("autoComplete") > 0;
      ret.viewMedalItem = rs.getInt("viewMedalItem");
      ret.selectedSkillID = rs.getInt("selectedSkillID");
      ret.startNavi = rs.getInt("startNavi");
      ret.blocked = rs.getInt("blocked") > 0;
      psr.setInt(1, ret.id);
      ResultSet rse = psr.executeQuery();

      while (rse.next()) {
         MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(rse.getString("name"));
         MapleQuestRequirement req = new MapleQuestRequirement(ret, type, rse);
         if (type.equals(MapleQuestRequirementType.interval)) {
            ret.repeatable = true;
         } else if (type.equals(MapleQuestRequirementType.normalAutoStart)) {
            ret.repeatable = true;
            ret.autoStart = true;
         } else if (type.equals(MapleQuestRequirementType.startscript)) {
            ret.startscript = rse.getString("stringStore");
            ret.scriptedStart = true;
         } else if (type.equals(MapleQuestRequirementType.endscript)) {
            ret.endscript = rse.getString("stringStore");
            ret.customend = true;
         } else if (type.equals(MapleQuestRequirementType.mob)) {
            for (Pair<String, Integer> mob : req.getDataStore()) {
               ret.relevantMobs.put(Integer.parseInt(mob.left), mob.right);
            }
         } else if (type.equals(MapleQuestRequirementType.item)) {
            String intstoreFirst = rse.getString("intStoresFirst").replace(" ", "");
            String intstoreSecond = rse.getString("intStoresSecond").replace(" ", "");

            for (int i = 0; i < intstoreFirst.split(",").length; i++) {
               ret.requireItems.add(new Pair<>(Integer.parseInt(intstoreFirst.split(",")[i]), Integer.parseInt(intstoreSecond.split(",")[i])));
            }
         }

         if (rse.getInt("type") == 0) {
            ret.startReqs.add(req);
         } else {
            ret.completeReqs.add(req);
         }
      }

      rse.close();
      psa.setInt(1, ret.id);
      rse = psa.executeQuery();

      while (rse.next()) {
         MapleQuestActionType ty = MapleQuestActionType.getByWZName(rse.getString("name"));
         if (rse.getInt("type") == 0) {
            if (ty != MapleQuestActionType.item || ret.id != 7103) {
               ret.startActs.add(new MapleQuestAction(ty, rse, ret, pss, psq, psi));
            }
         } else if (ty != MapleQuestActionType.item || ret.id != 7102) {
            ret.completeActs.add(new MapleQuestAction(ty, rse, ret, pss, psq, psi));
         }
      }

      rse.close();
      psp.setInt(1, ret.id);

      for (rse = psp.executeQuery();
         rse.next();
         ret.partyQuestInfo.get(rse.getString("rank")).add(new Pair<>(rse.getString("mode"), new Pair<>(rse.getString("property"), rse.getInt("value"))))
      ) {
         if (!ret.partyQuestInfo.containsKey(rse.getString("rank"))) {
            ret.partyQuestInfo.put(rse.getString("rank"), new ArrayList<>());
         }
      }

      rse.close();
      return ret;
   }

   public static void loadModifiedQuestTime() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/quest" : "data/Jin/quest", "ModifiedQuestTime.data");
      int count = 0;
      modifiedQuestTimes.clear();

      for (Table children : table.list()) {
         String start = children.getProperty("Start");
         String end = children.getProperty("End");
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd/HH/mm");

         try {
            long startTime = sdf.parse(start).getTime();
            long endTime = sdf.parse(end).getTime();
            modifiedQuestTimes.add(new ModifiedQuestTime(Integer.parseInt(children.getName()), startTime, endTime));
            count++;
         } catch (Exception var11) {
         }
      }

      System.out.println("总共加载了 " + count + " 个ModifiedQuestTime数据。");

   }

   public static void sendModifiedQuestTime(MapleCharacter player) {
      List<ModifiedQuestTime> list = new ArrayList<>(modifiedQuestTimes);
      int recount = list.size() / 250;
      if (list.size() - list.size() / 250 * 250 > 0) {
         recount++;
      }

      for (int i = 0; i < recount; i++) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SET_QUEST_TIME.getValue());
         List<ModifiedQuestTime> lista = new ArrayList<>();

         for (int a = 250 * i; a < 250 * (i + 1) && a < list.size(); a++) {
            lista.add(list.get(a));
         }

         packet.write(lista.size());

         for (ModifiedQuestTime modifiedQuestTime : lista) {
            modifiedQuestTime.encode(packet);
         }

         player.send(packet.getPacket());
      }
   }

   public List<Pair<String, Pair<String, Integer>>> getInfoByRank(String rank) {
      return this.partyQuestInfo.get(rank);
   }

   public boolean isPartyQuest() {
      return this.partyQuestInfo.size() > 0;
   }

   public final int getSkillID() {
      return this.selectedSkillID;
   }

   public final String getName() {
      return this.name;
   }

   public final List<MapleQuestAction> getCompleteActs() {
      return this.completeActs;
   }

   public static void initQuests() {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_questdata");
         PreparedStatement psr = con.prepareStatement("SELECT * FROM wz_questreqdata WHERE questid = ?");
         PreparedStatement psa = con.prepareStatement("SELECT * FROM wz_questactdata WHERE questid = ?");
         PreparedStatement pss = con.prepareStatement("SELECT * FROM wz_questactskilldata WHERE uniqueid = ?");
         PreparedStatement psq = con.prepareStatement("SELECT * FROM wz_questactquestdata WHERE uniqueid = ?");
         PreparedStatement psi = con.prepareStatement("SELECT * FROM wz_questactitemdata WHERE uniqueid = ?");
         PreparedStatement psp = con.prepareStatement("SELECT * FROM wz_questpartydata WHERE questid = ?");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            quests.put(rs.getInt("questid"), loadQuest(rs, psr, psa, pss, psq, psi, psp));
         }

         rs.close();
         ps.close();
         psr.close();
         psa.close();
         pss.close();
         psq.close();
         psi.close();
         psp.close();
      } catch (Exception var12) {
         System.out.println("InitQuest Err");
         var12.printStackTrace();
      }

      loadModifiedQuestTime();
   }

   public static MapleQuest getInstance(int id) {
      MapleQuest ret = quests.get(id);
      if (ret == null) {
         ret = new MapleQuest(id);
         quests.put(id, ret);
      }

      return ret;
   }

   public static Collection<MapleQuest> getAllInstances() {
      return quests.values();
   }

   public boolean canStart(MapleCharacter c, Integer npcid) {
      if (c.getQuest(this).getStatus() != 0 && !this.repeatable) {
         return false;
      } else if (this.blocked && !c.isGM()) {
         return false;
      } else {
         for (MapleQuestRequirement r : this.startReqs) {
            if (!r.check(c, npcid)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canComplete(MapleCharacter c, Integer npcid) {
      if (c.getQuest(this).getStatus() != 1) {
         return false;
      } else if (this.blocked && !c.isGM()) {
         return false;
      } else {
         for (MapleQuestRequirement r : this.completeReqs) {
            if (!r.check(c, npcid)) {
               return false;
            }
         }

         return true;
      }
   }

   public final void RestoreLostItem(MapleCharacter c, int itemid) {
      if (!this.blocked || c.isGM()) {
         for (MapleQuestAction a : this.startActs) {
            if (a.RestoreLostItem(c, itemid)) {
               break;
            }
         }
      }
   }

   public void start(MapleCharacter c, int npc) {
      if ((this.autoStart || this.checkNPCOnMap(c, npc)) && this.canStart(c, npc)) {
         for (MapleQuestAction a : this.startActs) {
            if (!a.checkEnd(c, null)) {
               return;
            }
         }

         for (MapleQuestAction ax : this.startActs) {
            ax.runStart(c, null);
         }

         if (!this.customend) {
            this.forceStart(c, npc, null);
         } else {
            NPCScriptManager.getInstance().endQuest(c.getClient(), npc, this.getId(), true);
         }

         if (QuestChangeInfo.allQuestList.contains(this.getId())) {
            AchievementFactory.checkQuest(c, this.getId(), 1);
         }
      }
   }

   public void complete(MapleCharacter c, int npc) {
      this.complete(c, npc, null);
   }

   public void complete(MapleCharacter c, int npc, Integer selection) {
      if (c.getMap() != null && (this.autoPreComplete || this.checkNPCOnMap(c, npc) || this.autoComplete) && this.canComplete(c, npc)) {
         for (MapleQuestAction a : this.completeActs) {
            if (!a.checkEnd(c, selection)) {
               return;
            }
         }

         this.forceComplete(c, npc);

         for (MapleQuestAction ax : this.completeActs) {
            ax.runEnd(c, selection);
         }

         if (QuestChangeInfo.allQuestList.contains(this.getId())) {
            AchievementFactory.checkQuest(c, this.getId(), 2);
         }

         NormalEffect eff = new NormalEffect(c.getId(), EffectHeader.QuestClear);
         c.getClient().getSession().writeAndFlush(eff.encodeForLocal());
         c.getMap().broadcastMessage(c, eff.encodeForRemote(), false);
      }
   }

   public void forfeit(MapleCharacter c) {
      if (this.getId() >= 2000018 && this.getId() <= 2000027 || c.getQuest(this).getStatus() == 1) {
         MapleQuestStatus oldStatus = c.getQuest(this);
         MapleQuestStatus newStatus = new MapleQuestStatus(this, 0);
         newStatus.setForfeited(oldStatus.getForfeited() + 1);
         newStatus.setCompletionTime(oldStatus.getCompletionTime());
         c.updateQuest(newStatus);
      }
   }

   public void forceStart(MapleCharacter c, int npc, String customData) {
      MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte)1, npc);
      newStatus.setForfeited(c.getQuest(this).getForfeited());
      newStatus.setCompletionTime(c.getQuest(this).getCompletionTime());
      newStatus.setCustomData(customData);
      c.updateQuest(newStatus);
      if (QuestChangeInfo.allQuestList.contains(this.getId())) {
         AchievementFactory.checkQuest(c, this.getId(), 1);
      }
   }

   public void forceComplete(MapleCharacter c, int npc) {
      int questId = 0;
      switch (this.getId()) {
         case 34129:
            questId = 2000004;
            break;
         case 34275:
            questId = 2000008;
            break;
         case 34773:
            questId = 2000009;
            break;
         case 35560:
         case 35561:
         case 35562:
         case 35563:
         case 35564:
         case 35565:
            questId = 2000011;
            break;
         case 35570:
         case 35571:
         case 35572:
         case 35573:
         case 35574:
         case 35575:
         case 35576:
         case 35577:
         case 35578:
         case 35579:
         case 35580:
         case 35581:
         case 35582:
         case 35583:
            questId = 2000012;
            break;
         case 35590:
         case 35591:
         case 35592:
         case 35593:
         case 35594:
            questId = 2000013;
            break;
         case 38150:
         case 38151:
         case 38152:
         case 38153:
         case 38154:
         case 38155:
         case 38156:
            questId = 2000016;
            break;
         case 39035:
            questId = 2000007;
            break;
         case 39819:
         case 39820:
         case 39821:
         case 39822:
         case 39823:
         case 39824:
         case 39825:
            questId = 2000014;
            break;
         case 39923:
         case 39924:
         case 39925:
         case 39926:
         case 39927:
         case 39928:
            questId = 2000015;
            break;
         case 2000027:
            ScriptManager.runScript(c.getClient(), "consume_2433482", MapleLifeFactory.getNPC(2007));
      }

      boolean checkServerTutorial = questId > 0;
      if (checkServerTutorial && c.getQuestStatus(questId) == 1 && c.getOneInfo(questId, "clear") == null) {
         c.updateOneInfo(questId, "clear", "1");
      }

      if (QuestChangeInfo.allQuestList.contains(this.getId())) {
         AchievementFactory.checkQuest(c, this.getId(), 2);
      }

      MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte)2, npc);
      newStatus.setForfeited(c.getQuest(this).getForfeited());
      NormalEffect eff = new NormalEffect(c.getId(), EffectHeader.QuestClear);
      c.getClient().getSession().writeAndFlush(eff.encodeForLocal());
      c.getMap().broadcastMessage(c, eff.encodeForRemote(), false);
      c.updateQuest(newStatus);
   }

   public int getId() {
      return this.id;
   }

   public Map<Integer, Integer> getRelevantMobs() {
      return this.relevantMobs;
   }

   public int getItemAmountNeeded(int itemid) {
      if (this.requireItems.size() < 1) {
         return 0;
      } else {
         for (Pair<Integer, Integer> req : this.requireItems) {
            if (req.getLeft() == itemid) {
               return req.getRight();
            }
         }

         return 0;
      }
   }

   private boolean checkNPCOnMap(MapleCharacter player, int npcid) {
      return GameConstants.isEvan(player.getJob()) && npcid == 1013000
         || npcid == 9000040
         || npcid == 9000066
         || player.getMap() != null && player.getMap().containsNPC(npcid)
         || npcid == 0;
   }

   public int getMedalItem() {
      return this.viewMedalItem;
   }

   public boolean isBlocked() {
      return this.blocked;
   }

   public boolean hasStartScript() {
      return this.scriptedStart;
   }

   public String getStartscript() {
      return this.startscript;
   }

   public boolean hasEndScript() {
      return this.customend;
   }

   public String getEndscript() {
      return this.endscript;
   }

   public int getStartNavi() {
      return this.startNavi;
   }

   public static enum MedalQuest {
      Beginner(
         29005,
         29015,
         15,
         new int[]{
            104000000,
            104010001,
            100000006,
            104020000,
            100000000,
            100010000,
            100040000,
            100040100,
            101010103,
            101020000,
            101000000,
            102000000,
            101030104,
            101030406,
            102020300,
            103000000,
            102050000,
            103010001,
            103030200,
            110000000
         },
         "초보"
      ),
      ElNath(
         29006, 29012, 50, new int[]{200000000, 200010100, 200010300, 200080000, 200080100, 211000000, 211030000, 211040300, 211041200, 211041800}, "엘나스 산맥"
      ),
      LudusLake(
         29007, 29012, 40, new int[]{222000000, 222010400, 222020000, 220000000, 220020300, 220040200, 221020701, 221000000, 221030600, 221040400}, "루더스 호수"
      ),
      Underwater(
         29008, 29012, 40, new int[]{230000000, 230010400, 230010200, 230010201, 230020000, 230020201, 230030100, 230040000, 230040200, 230040400}, "해저"
      ),
      MuLung(29009, 29012, 50, new int[]{251000000, 251010200, 251010402, 251010500, 250010500, 250010504, 250000000, 250010300, 250010304, 250020300}, "무릉도원"),
      NihalDesert(
         29010, 29012, 70, new int[]{261030000, 261020401, 261020000, 261010100, 261000000, 260020700, 260020300, 260000000, 260010600, 260010300}, "니할사막"
      ),
      MinarForest(
         29011, 29012, 70, new int[]{240000000, 240010200, 240010800, 240020401, 240020101, 240030000, 240040400, 240040511, 240040521, 240050000}, "미나르숲"
      ),
      Sleepywood(
         29014, 29015, 50, new int[]{105040300, 105070001, 105040305, 105090200, 105090300, 105090301, 105090312, 105090500, 105090900, 105080000}, "슬리피우드"
      );

      public int questid;
      public int level;
      public int lquestid;
      public String questname;
      public int[] maps;

      private MedalQuest(int questid, int lquestid, int level, int[] maps, String questname) {
         this.questid = questid;
         this.level = level;
         this.lquestid = lquestid;
         this.maps = maps;
         this.questname = questname;
      }
   }
}
