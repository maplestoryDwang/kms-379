package objects.fields.gameobject.lifes;

import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MaplePet;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class PlayerNPC extends MapleNPC {
   private MapleCharacter chr = null;
   private Map<Byte, Integer> equips = new HashMap<>();
   private Map<Byte, Integer> secondEquips = new HashMap<>();
   private int mapid;
   private int face;
   private int hair;
   private int charId;
   private int secondFace;
   private byte skin;
   private byte gender;
   private byte secondSkin;
   private byte secondGender;
   private int[] pets = new int[3];

   public PlayerNPC(ResultSet rs) throws Exception {
      super(rs.getInt("ScriptId"), rs.getString("name"));
      this.hair = rs.getInt("hair");
      this.face = rs.getInt("face");
      this.mapid = rs.getInt("map");
      this.skin = rs.getByte("skin");
      this.charId = rs.getInt("charid");
      this.gender = rs.getByte("gender");
      this.setCoords(rs.getInt("x"), rs.getInt("y"), rs.getInt("dir"), rs.getInt("Foothold"));
      String[] pet = rs.getString("pets").split(",");

      for (int i = 0; i < 3; i++) {
         if (pet[i] != null) {
            this.pets[i] = Integer.parseInt(pet[i]);
         } else {
            this.pets[i] = 0;
         }
      }

      DBConnection db = new DBConnection();
      Connection con = DBConnection.getConnection();
      PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_equip WHERE NpcId = ?");
      ps.setInt(1, this.getId());
      ResultSet rs2 = ps.executeQuery();

      while (rs2.next()) {
         this.equips.put(rs2.getByte("equippos"), rs2.getInt("equipid"));
      }

      rs2.close();
      ps.close();
      con.close();
   }

   public PlayerNPC(MapleCharacter cid, int npc, Field map, MapleCharacter base) {
      super(npc, cid.getName());
      this.charId = cid.getId();
      this.mapid = map.getId();
      this.setCoords(89, 321, 0, 3);
      this.chr = cid;
   }

   public PlayerNPC(MapleCharacter cid, int npc, int map) {
      super(npc, cid.getName());
      this.charId = cid.getId();
      this.mapid = map;
      if (DBConfig.isGanglim) {
         this.setCoords(2946, 892, 0, 83);
      } else {
         this.setCoords(2208, 1892, 0, 2);
      }

      this.update(cid);
      this.chr = cid;
   }

   public void setCoords(int x, int y, int f, int fh) {
      this.setPosition(new Point(x, y));
      this.setCy(y);
      this.setRx0(x - 50);
      this.setRx1(x + 50);
      this.setF(f);
      this.setFh(fh);
   }

   public static void loadAll() {
      List<PlayerNPC> toAdd = new ArrayList<>();
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            toAdd.add(new PlayerNPC(rs));
         }

         rs.close();
         ps.close();
      } catch (Exception var7) {
         System.out.println("playernpc Err");
         var7.printStackTrace();
      }

      for (PlayerNPC npc : toAdd) {
         npc.addToServer();
      }
   }

   public static void updateByCharId(MapleCharacter chr) {
      if (Center.Find.findChannel(chr.getId()) > 0) {
         for (PlayerNPC npc : GameServer.getInstance(Center.Find.findChannel(chr.getId())).getAllPlayerNPC()) {
            npc.update(chr);
         }
      }
   }

   public void addToServer() {
      for (GameServer cserv : GameServer.getAllInstances()) {
         cserv.addPlayerNPC(this);
      }
   }

   public void removeFromServer() {
      for (GameServer cserv : GameServer.getAllInstances()) {
         cserv.removePlayerNPC(this);
      }
   }

   public void update(MapleCharacter chr) {
      if (chr != null && this.charId == chr.getId()) {
         this.setName(chr.getName());
         this.setHair(chr.getHair());
         this.setFace(chr.getFace());
         this.setSkin((byte)chr.getSkinColor());
         this.setGender(chr.getGender());
         this.equips = new HashMap<>();

         for (Item item : chr.getInventory(MapleInventoryType.EQUIPPED).newList()) {
            if (item.getPosition() >= -127) {
               this.equips.put((byte)item.getPosition(), item.getItemId());
            }
         }
      }
   }

   public void destroy() {
      this.destroy(false);
   }

   public void destroy(boolean remove) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("DELETE FROM playernpcs WHERE scriptid = ?");
         ps.setInt(1, this.getId());
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("DELETE FROM playernpcs_equip WHERE npcid = ?");
         ps.setInt(1, this.getId());
         ps.executeUpdate();
         ps.close();
         if (remove) {
            this.removeFromServer();
         }
      } catch (Exception var8) {
         System.out.println("PlayerNPC Equip Err");
         var8.printStackTrace();
      }
   }

   public void saveToDB() {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            if (this.getNPCFromWZ() != null) {
               this.destroy();
               PreparedStatement ps = con.prepareStatement(
                  "INSERT INTO playernpcs(name, hair, face, skin, x, y, map, charid, scriptid, foothold, dir, gender, pets) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
               );
               ps.setString(1, this.getName());
               ps.setInt(2, this.getHair());
               ps.setInt(3, this.getFace());
               ps.setInt(4, this.getSkinColor());
               ps.setInt(5, this.getTruePosition().x);
               ps.setInt(6, this.getTruePosition().y);
               ps.setInt(7, this.getMapId());
               ps.setInt(8, this.getCharId());
               ps.setInt(9, this.getId());
               ps.setInt(10, this.getFh());
               ps.setInt(11, this.getF());
               ps.setInt(12, this.getGender());
               String[] pet = new String[]{"0", "0", "0"};

               for (int i = 0; i < 3; i++) {
                  if (this.pets[i] > 0) {
                     pet[i] = String.valueOf(this.pets[i]);
                  }
               }

               ps.setString(13, pet[0] + "," + pet[1] + "," + pet[2]);
               ps.executeUpdate();
               ps.close();
               ps = con.prepareStatement("INSERT INTO playernpcs_equip(npcid, charid, equipid, equippos) VALUES (?, ?, ?, ?)");
               ps.setInt(1, this.getId());
               ps.setInt(2, this.getCharId());

               for (Entry<Byte, Integer> equip : this.equips.entrySet()) {
                  ps.setInt(3, equip.getValue());
                  ps.setInt(4, equip.getKey());
                  ps.executeUpdate();
               }

               ps.close();
               return;
            }

            this.destroy(true);
         }
      } catch (Exception var9) {
         System.out.println("PlayerNPC Equip  2 Err");
         var9.printStackTrace();
      }
   }

   public short getJob() {
      return 0;
   }

   public int getDemonMarking() {
      return 0;
   }

   public Map<Byte, Integer> getEquips(boolean fusionAnvil) {
      return this.equips;
   }

   public Map<Byte, Integer> getSecondEquips(boolean fusionAnvil) {
      return this.secondEquips;
   }

   public Map<Byte, Integer> getTotems() {
      return new HashMap<>();
   }

   public byte getSkinColor() {
      return this.skin;
   }

   public byte getSecondSkinColor() {
      return this.secondSkin;
   }

   public byte getGender() {
      return this.gender;
   }

   public byte getSecondGender() {
      return this.secondGender;
   }

   public int getFace() {
      return this.face;
   }

   public int getSecondFace() {
      return this.secondFace;
   }

   public int getHair() {
      return this.hair;
   }

   public int getCharId() {
      return this.charId;
   }

   public int getMapId() {
      return this.mapid;
   }

   public void setSkin(byte s) {
      this.skin = s;
   }

   public void setFace(int f) {
      this.face = f;
   }

   public void setHair(int h) {
      this.hair = h;
   }

   public void setGender(int g) {
      this.gender = (byte)g;
   }

   public int getPet(int i) {
      return this.pets[i] > 0 ? this.pets[i] : 0;
   }

   public void setPets(List<MaplePet> p) {
      for (int i = 0; i < 3; i++) {
         if (p != null && p.size() > i && p.get(i) != null) {
            this.pets[i] = p.get(i).getPetItemId();
         } else {
            this.pets[i] = 0;
         }
      }
   }

   public MapleCharacter getChr() {
      return this.chr;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.NPCPacket.spawnNPC(this, true));
      client.getSession().writeAndFlush(CWvsContext.spawnPlayerNPC(this, client.getPlayer()));
      client.getSession().writeAndFlush(CField.NPCPacket.spawnNPCRequestController(this, true));
   }

   public MapleNPC getNPCFromWZ() {
      MapleNPC npc = MapleLifeFactory.getNPC(this.getId());
      if (npc != null) {
         npc.setName(this.getName());
      }

      return npc;
   }
}
