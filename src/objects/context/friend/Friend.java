package objects.context.friend;

import constants.devtempConstants.DummyCharacterName;
import database.DBConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import network.models.CWvsContext;
import objects.users.CharacterNameAndId;
import objects.users.MapleClient;

public class Friend implements Serializable {
   private static final long serialVersionUID = 1413738569L;
   private final Map<Integer, FriendEntry> buddies;
   private byte capacity;
   private boolean changed = false;
   private Deque<CharacterNameAndId> pendingRequests = new LinkedList<>();

   public Friend(byte capacity) {
      this.buddies = new LinkedHashMap<>();
      this.capacity = capacity;
   }

   public boolean contains(int characterId) {
      return this.buddies.containsKey(characterId);
   }

   public boolean containsVisible(int characterId) {
      FriendEntry ble = this.buddies.get(characterId);
      return ble == null ? false : ble.isVisible();
   }

   public byte getCapacity() {
      return this.capacity;
   }

   public void setCapacity(byte capacity) {
      this.capacity = capacity;
   }

   public FriendEntry get(int characterId) {
      return this.buddies.get(characterId);
   }

   public FriendEntry get(String characterName) {
      String lowerCaseName = characterName.toLowerCase();

      for (FriendEntry ble : this.buddies.values()) {
         if (ble.getName().toLowerCase().equals(lowerCaseName)) {
            return ble;
         }
      }

      return null;
   }

   public void put(FriendEntry entry) {
      this.buddies.put(entry.getAccountId(), entry);
   }

   public void remove(int characterId) {
      this.buddies.remove(characterId);
      this.changed = true;
   }

   public Collection<FriendEntry> getBuddies() {
      return this.buddies.values();
   }

   public boolean isFull() {
      return this.buddies.size() >= this.capacity;
   }

   public int[] getBuddyIds() {
      int[] buddyIds = new int[this.buddies.size()];
      int i = 0;

      for (FriendEntry ble : this.buddies.values()) {
         buddyIds[i++] = ble.getAccountId();
      }

      return buddyIds;
   }

   public void loadFromTransfer(Map<CharacterNameAndId, Boolean> data) {
      for (Entry<CharacterNameAndId, Boolean> qs : data.entrySet()) {
         CharacterNameAndId buddyid = qs.getKey();
         boolean pair = qs.getValue();
         if (!pair) {
            this.pendingRequests.push(buddyid);
         } else {
            this.put(
               new FriendEntry(
                  buddyid.getName(),
                  buddyid.getAccId(),
                  buddyid.getId(),
                  buddyid.getGroupName(),
                  -1,
                  true,
                  buddyid.getLevel(),
                  buddyid.getJob(),
                  buddyid.getMemo()
               )
            );
         }
      }
   }

   public void loadFromDb(int accountId) throws SQLException {
      DBConnection db = new DBConnection();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement(
            "SELECT b.buddyaccid, b.pending, c.name as buddyname, c.id as buddyid, c.job as buddyjob, c.level as buddylevel, b.groupname, b.memo FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.accid = ?"
         );
         ps.setInt(1, accountId);
         rs = ps.executeQuery();

         while (rs.next()) {
            int buddyid = rs.getInt("buddyaccid");
            int playerID = rs.getInt("buddyid");
            if (playerID >= 100000000) {
               int count = 0;
               int c = playerID - 100000000;
               String buddyname = "";

               for (String name : new ArrayList<>(DummyCharacterName.dummyList)) {
                  if (count++ == c) {
                     buddyname = name;
                     break;
                  }
               }

               if (!buddyname.isEmpty()) {
                  this.pendingRequests.push(new CharacterNameAndId(playerID, buddyid, buddyname, 200, 6512, "그룹 미지정", ""));
               }
            } else {
               String buddyname = rs.getString("buddyname");
               if (rs.getInt("pending") == 1) {
                  this.pendingRequests
                     .push(
                        new CharacterNameAndId(
                           playerID, buddyid, buddyname, rs.getInt("buddylevel"), rs.getInt("buddyjob"), rs.getString("groupname"), rs.getString("memo")
                        )
                     );
               } else {
                  this.put(
                     new FriendEntry(
                        buddyname, buddyid, playerID, rs.getString("groupname"), -1, true, rs.getInt("buddylevel"), rs.getInt("buddyjob"), rs.getString("memo")
                     )
                  );
               }
            }
         }

         ps.close();
         rs.close();
         ps = con.prepareStatement("SELECT `buddyid` FROM `buddies` WHERE `accid` = ?");
         ps.setInt(1, accountId);
         rs = ps.executeQuery();

         while (rs.next()) {
            int buddyid = rs.getInt("buddyid");
            if (buddyid >= 100000000) {
               int count = 0;
               int c = buddyid - 100000000;
               String buddyname = "";

               for (String namex : new ArrayList<>(DummyCharacterName.dummyList)) {
                  if (count++ == c) {
                     buddyname = namex;
                     break;
                  }
               }

               if (!buddyname.isEmpty()) {
                  this.put(new FriendEntry(buddyname, buddyid, buddyid, "그룹 미지정", -1, true, 200, 6512, ""));
               }
            }
         }

         ps.close();
         rs.close();
         ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND accid = ?");
         ps.setInt(1, accountId);
         ps.executeUpdate();
      } finally {
         if (ps != null) {
            ps.close();
            PreparedStatement var20 = null;
         }

         if (rs != null) {
            rs.close();
            ResultSet var21 = null;
         }
      }
   }

   public void addBuddyRequest(
      MapleClient c, int cidFrom, int accIdFrom, String nameFrom, int channelFrom, int levelFrom, int jobFrom, String groupName, String memo
   ) {
      this.put(new FriendEntry(nameFrom, accIdFrom, cidFrom, groupName, channelFrom, false, levelFrom, jobFrom, memo));
      if (this.pendingRequests.isEmpty()) {
         c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.requestBuddylistAdd(cidFrom, accIdFrom, nameFrom, levelFrom, jobFrom, c, groupName, memo));
      } else {
         this.pendingRequests.push(new CharacterNameAndId(cidFrom, accIdFrom, nameFrom, levelFrom, jobFrom, groupName, memo));
      }
   }

   public void setChanged(boolean v) {
      this.changed = v;
   }

   public boolean changed() {
      return this.changed;
   }

   public CharacterNameAndId pollPendingRequest() {
      return this.pendingRequests.pollLast();
   }

   public static enum BuddyAddResult {
      BUDDYLIST_FULL,
      ALREADY_ON_LIST,
      ADD_BLOCKED,
      OK;
   }

   public static enum BuddyOperation {
      ADDED,
      DELETED;
   }
}
