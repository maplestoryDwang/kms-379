package objects.utils.royalMove;

import constants.GameConstants;
import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import database.ZeniaDBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import objects.androids.Android;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;
import objects.item.MapleRing;

public class MoveZeniaToRoyal {
   public static Map<Integer, ZeniaAuctionItem> items = new ConcurrentHashMap<>();

   public static void loadItems() {
      int count = 0;
      System.out.println("[Loading ] Auction Item Load...");
      String[] columns = new String[]{"inventoryitems", "inventoryitemsuse", "inventoryitemssetup", "inventoryitemsetc", "inventoryitemscash"};

      try (
         Connection con = ZeniaDBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("SELECT * FROM `auctionitems` WHERE `state` < 7 AND characterid = 20839");
         ResultSet rs = ps.executeQuery();
      ) {
         while (rs.next()) {
            ZeniaAuctionItem aItem = new ZeniaAuctionItem();
            aItem.setAuctionId(rs.getInt("auctionitemid"));
            aItem.setAuctionType(rs.getInt("auctiontype"));
            aItem.setAccountId(rs.getInt("accountid"));
            aItem.setCharacterId(rs.getInt("characterid"));
            aItem.setState(rs.getInt("state"));
            aItem.setWorldId(rs.getInt("worldid"));
            aItem.setBidUserId(rs.getInt("biduserid"));
            aItem.setNexonOid(rs.getInt("nexonoid"));
            aItem.setDeposit(rs.getInt("deposit"));
            aItem.setsStype(rs.getInt("sstype"));
            aItem.setBidWorld(rs.getInt("bidworld"));
            aItem.setPrice(rs.getLong("price"));
            aItem.setSecondPrice(rs.getLong("secondprice"));
            aItem.setDirectPrice(rs.getLong("directprice"));
            aItem.setEndDate(rs.getLong("enddate"));
            aItem.setRegisterDate(rs.getLong("registerdate"));
            aItem.setName(rs.getString("name"));
            aItem.setBidUserName(rs.getString("bidusername"));
            long inventoryItemId = rs.getLong("inventoryitemid");

            for (String column : columns) {
               StringBuilder query = new StringBuilder();
               query.append("SELECT * FROM `");
               query.append(column);
               query.append("` LEFT JOIN `");
               query.append("inventoryequipment");
               query.append("` USING (`inventoryitemid`) WHERE `type` = 7 AND `inventoryitemid` = ?");

               try (PreparedStatement ps1 = con.prepareStatement(query.toString())) {
                  ps1.setLong(1, inventoryItemId);

                  try (ResultSet rs1 = ps1.executeQuery()) {
                     if (rs1.next()) {
                        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        Item item_ = null;
                        if (rs1.getInt("itemid") / 1000000 == 1) {
                           Equip equip = new Equip(rs1.getInt("itemid"), rs1.getShort("position"), rs1.getInt("uniqueid"), rs1.getInt("flag"));
                           equip.setQuantity((short)1);
                           equip.setInventoryId(rs1.getLong("inventoryitemid"));
                           equip.setOwner(rs1.getString("owner"));
                           equip.setExpiration(rs1.getLong("expiredate"));
                           equip.setUpgradeSlots(rs1.getByte("upgradeslots"));
                           equip.setLevel(rs1.getByte("level"));
                           equip.setStr(rs1.getShort("str"));
                           equip.setDex(rs1.getShort("dex"));
                           equip.setInt(rs1.getShort("int"));
                           equip.setLuk(rs1.getShort("luk"));
                           equip.setHp(rs1.getShort("hp"));
                           equip.setMp(rs1.getShort("mp"));
                           equip.setWatk(rs1.getShort("watk"));
                           equip.setMatk(rs1.getShort("matk"));
                           equip.setWdef(rs1.getShort("wdef"));
                           equip.setMdef(rs1.getShort("mdef"));
                           equip.setAcc(rs1.getShort("acc"));
                           equip.setAvoid(rs1.getShort("avoid"));
                           equip.setHands(rs1.getShort("hands"));
                           equip.setSpeed(rs1.getShort("speed"));
                           equip.setJump(rs1.getShort("jump"));
                           equip.setViciousHammer(rs1.getByte("ViciousHammer"));
                           equip.setItemEXP(rs1.getInt("itemEXP"));
                           equip.setGMLog(rs1.getString("GM_Log"));
                           equip.setDurability(rs1.getInt("durability"));
                           equip.setEnhance(rs1.getByte("enhance"));
                           equip.setState(rs1.getByte("state"));
                           equip.setLines(rs1.getByte("line"));
                           equip.setPotential1(rs1.getInt("potential1"));
                           equip.setPotential2(rs1.getInt("potential2"));
                           equip.setPotential3(rs1.getInt("potential3"));
                           equip.setPotential4(rs1.getInt("potential4"));
                           equip.setPotential5(rs1.getInt("potential5"));
                           equip.setPotential6(rs1.getInt("potential6"));
                           equip.setGiftFrom(rs1.getString("sender"));
                           equip.setIncSkill(rs1.getInt("incSkill"));
                           equip.setPVPDamage(rs1.getShort("pvpDamage"));
                           equip.setCharmEXP(rs1.getShort("charmEXP"));
                           if (equip.getCharmEXP() < 0) {
                              equip.setCharmEXP(((Equip)ii.getEquipById(equip.getItemId())).getCharmEXP());
                           }

                           if (equip.getUniqueId() > -1L) {
                              if (GameConstants.isEffectRing(rs1.getInt("itemid"))) {
                                 MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), false);
                                 if (ring != null) {
                                    equip.setRing(ring);
                                 }
                              } else if (equip.getItemId() / 10000 == 166) {
                                 Android ring = Android.loadFromDb(equip.getItemId(), equip.getUniqueId());
                                 if (ring != null) {
                                    equip.setAndroid(ring);
                                 }
                              }
                           }

                           equip.setSpecialAttribute(rs1.getShort("enchantbuff"));
                           equip.setReqLevel(rs1.getByte("reqLevel"));
                           equip.setGrowthEnchant(rs1.getByte("yggdrasilWisdom"));
                           equip.setFinalStrike(rs1.getByte("finalStrike") > 0);
                           equip.setBossDamage(rs1.getByte("bossDamage"));
                           equip.setIgnorePDR(rs1.getByte("ignorePDR"));
                           equip.setTotalDamage(rs1.getByte("totalDamage"));
                           equip.setAllStat(rs1.getByte("allStat"));
                           equip.setKarmaCount(rs1.getByte("karmaCount"));
                           equip.setSoulEnchanter(rs1.getShort("soulenchanter"));
                           equip.setSoulName(rs1.getShort("soulname"));
                           equip.setSoulPotential(rs1.getShort("soulpotential"));
                           equip.setSoulSkill(rs1.getInt("soulskill"));
                           equip.setFire(rs1.getLong("fire") < 0L ? 0L : rs1.getLong("fire"));
                           equip.setArc(rs1.getShort("arc"));
                           equip.setArcEXP(rs1.getInt("arcexp"));
                           equip.setArcLevel(rs1.getInt("arclevel"));
                           equip.setItemState(rs1.getInt("equipmenttype"));
                           equip.setFusionAnvil(rs1.getInt("moru"));
                           item_ = equip.copy();
                        } else {
                           Item item = new Item(
                              rs1.getInt("itemid"), rs1.getShort("position"), rs1.getShort("quantity"), rs1.getInt("flag"), rs1.getInt("uniqueid")
                           );
                           item.setOwner(rs1.getString("owner"));
                           item.setInventoryId(rs1.getLong("inventoryitemid"));
                           item.setExpiration(rs1.getLong("expiredate"));
                           item.setGMLog(rs1.getString("GM_Log"));
                           item.setGiftFrom(rs1.getString("sender"));
                           item_ = item.copy();
                        }

                        if (item_ != null) {
                           aItem.setItem(item_);

                           try (PreparedStatement ps2 = con.prepareStatement("SELECT * FROM `auctionhistories` WHERE `auctionid` = ?")) {
                              ps2.setInt(1, aItem.getAuctionId());

                              try (ResultSet rs2 = ps2.executeQuery()) {
                                 if (rs2.next()) {
                                    ZeniaAuctionHistory history = new ZeniaAuctionHistory();
                                    history.setId(rs2.getLong("id"));
                                    history.setAuctionId(rs2.getInt("auctionid"));
                                    history.setAccountId(rs2.getInt("accountid"));
                                    history.setCharacterId(rs2.getInt("characterid"));
                                    history.setItemId(rs2.getInt("itemid"));
                                    history.setState(rs2.getInt("state"));
                                    history.setPrice(rs2.getLong("price"));
                                    history.setBuyTime(rs2.getLong("buytime"));
                                    history.setDeposit(rs2.getInt("deposit"));
                                    history.setQuantity(rs2.getInt("quantity"));
                                    history.setWorldId(rs2.getInt("worldid"));
                                    aItem.setHistory(history);
                                 }
                              }
                           }

                           items.put(aItem.getAuctionId(), aItem);
                           count++;
                        }
                     }
                  }
               }
            }
         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

      System.out.println("[Loading Completed] Auction Item " + count + " Items Load Completed.");
   }

   public static void checkItems() {
      Map<Integer, Integer> userIDList = new HashMap<>();
      System.out.println("Check Items...");
      int count = 0;
      List<ZeniaAuctionItem> completeItems = new ArrayList<>();
      userIDList.put(20839, 1615);

      for (Entry<Integer, Integer> ID : userIDList.entrySet()) {
         for (Entry<Integer, ZeniaAuctionItem> item_ : items.entrySet()) {
            ZeniaAuctionItem item = item_.getValue();
            if ((item.getState() == 2 || item.getState() == 7) && item.getBidUserId() == ID.getKey()
               || item.getState() != 7 && item.getState() >= 3 && item.getAccountId() == ID.getValue()) {
               completeItems.add(item);
            }
         }
      }

      for (ZeniaAuctionItem item : completeItems) {
         long time = System.currentTimeMillis() - item.getRegisterDate();
         long seconds = time / 1000L;
         long minutes = seconds / 60L;
         long hours = minutes / 60L;
         long days = hours / 24L;
         if (days < 20L) {
            count++;
         }
      }

      System.out.println("끝 : " + count + " | " + completeItems.size());
   }

   public static void main(String[] args) {
      System.setProperty("net.sf.odinms.wzpath", "wz");
      DBConnection.init();
      DBEventManager.init(6);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      ii.runItems();
      ii.runEtc();
      if (DBConfig.isGanglim) {
         ZeniaDBConnection.init();
      }

      loadItems();
      checkItems();
   }
}
