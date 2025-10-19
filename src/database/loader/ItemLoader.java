package database.loader;

import constants.GameConstants;
import database.DBConfig;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import network.auction.AuctionItemPackage;
import network.center.Center;
import network.game.processors.inventory.InventoryHandler;
import objects.androids.Android;
import objects.item.Equip;
import objects.item.IntensePowerCrystal;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.item.MapleRing;
import objects.users.MapleCabinet;
import objects.users.MapleCabinetItem;
import objects.users.enchant.GradeRandomOption;
import objects.utils.Pair;
import objects.utils.Randomizer;

public enum ItemLoader {
   INVENTORY("inventoryitems", "inventoryequipment", "inventoryequipexceptional", 0, "characterid"),
   STORAGE("inventoryitems", "inventoryequipment", "inventoryequipexceptional", 1, "accountid"),
   CASHSHOP("csitems", "csequipment", "", 2, "accountid"),
   DUEY("dueyitems", "dueyequipment", "", 6, "packageid"),
   AUCTION("auctionitems", "auctionequipment", "auctionequipmentexceptional", 7, "characterid"),
   CABINET("cabinet_items", "cabinet_equipment", "cabinet_equipment_exceptional", 8, "characterid");

   private int value;
   private String table;
   private String table_equip;
   private String table_equip_exceptional;
   private String arg;

   private ItemLoader(String table, String table_equip, String table_equip_exceptional, int value, String arg) {
      this.table = table;
      this.table_equip = table_equip;
      this.table_equip_exceptional = table_equip_exceptional;
      this.value = value;
      this.arg = arg;
   }

   public int getValue() {
      return this.value;
   }

   public void LoadEquipExceptionalUpgrade(Equip equip, String table) {
      try (
              Connection con = DBConnection.getConnection();
              PreparedStatement psEx = con.prepareStatement("SELECT * FROM `" + table + "` WHERE inventoryitemid = ?");
      ) {
         psEx.setLong(1, equip.getInventoryId());

         try (ResultSet rsEx = psEx.executeQuery()) {
            if (rsEx.next()) {
               equip.setExceptionalSlot(rsEx.getByte("slot"));
               equip.addExceptSTR(rsEx.getShort("str"));
               equip.addExceptDEX(rsEx.getShort("dex"));
               equip.addExceptINT(rsEx.getShort("int"));
               equip.addExceptLUK(rsEx.getShort("luk"));
               equip.addExceptHP(rsEx.getShort("hp"));
               equip.addExceptMP(rsEx.getShort("mp"));
               equip.addExceptWATK(rsEx.getShort("watk"));
               equip.addExceptMATK(rsEx.getShort("matk"));
               equip.addExceptWDEF(rsEx.getShort("wdef"));
               equip.addExceptMDEF(rsEx.getShort("mdef"));
               equip.addExceptAVOID(rsEx.getShort("avoid"));
            }
         }
      } catch (Exception var14) {
         System.out.println("익셉셔널 아이템 정보 불러오기 오류");
         var14.printStackTrace();
      }
   }

   public List<MapleCabinetItem> loadCabinetItems(int accountID) {
      List<MapleCabinetItem> ret = new ArrayList<>();
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `cabinet_items` LEFT JOIN `cabinet_equipment` USING(`inventoryitemid`) WHERE `accountid` = ?");
         ps.setInt(1, accountID);
         rs = ps.executeQuery();

         while (rs.next()) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            Item item_ = null;
            if (ii.itemExists(rs.getInt("itemid"))) {
               MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
               if (mit == MapleInventoryType.EQUIP && ii.isCash(rs.getInt("itemid"))) {
                  mit = MapleInventoryType.CASH_EQUIP;
               }

               if (!mit.equals(MapleInventoryType.EQUIP) && !mit.equals(MapleInventoryType.EQUIPPED) && !mit.equals(MapleInventoryType.CASH_EQUIP)) {
                  Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getInt("flag"), rs.getLong("uniqueid"));
                  item.setOwner(rs.getString("owner"));
                  item.setInventoryId(rs.getLong("inventoryitemid"));
                  item.setExpiration(rs.getLong("expiredate"));
                  item.setGMLog(rs.getString("GM_Log"));
                  item.setGiftFrom(rs.getString("sender"));
                  item.setOnceTrade(rs.getInt("once_trade"));
                  if (GameConstants.isPet(item.getItemId())) {
                     if (item.getUniqueId() > -1L) {
                        MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                        if (pet != null) {
                           item.setPet(pet);
                        }
                     } else {
                        item.setPet(MaplePet.createPet(item.getItemId(), MapleInventoryIdentifier.getInstance()));
                     }
                  }

                  if (GameConstants.isIntensePowerCrystal(item.getItemId())) {
                     PreparedStatement ps2 = con.prepareStatement("SELECT * FROM `intense_power_crystal` WHERE `item_unique_id` = ?");
                     ps2.setLong(1, item.getUniqueId());
                     ResultSet rs2 = ps2.executeQuery();
                     if (rs2.next()) {
                        IntensePowerCrystal ipc = new IntensePowerCrystal(
                                rs2.getInt("player_id"),
                                rs2.getLong("item_unique_id"),
                                rs2.getInt("member_count"),
                                rs2.getInt("mob_id"),
                                rs2.getLong("price"),
                                rs2.getLong("unk"),
                                rs2.getLong("gain_time")
                        );
                        item.setIntensePowerCrystal(ipc);
                     }

                     rs2.close();
                     ps2.close();
                  }

                  item_ = item.copy();
               } else {
                  Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getLong("uniqueid"), rs.getInt("flag"));
                  if (equip.getPosition() != -55) {
                     equip.setQuantity((short)1);
                     equip.setInventoryId(rs.getLong("inventoryitemid"));
                     equip.setOwner(rs.getString("owner"));
                     equip.setExpiration(rs.getLong("expiredate"));
                     equip.setUpgradeSlots((byte)Math.max(0, rs.getByte("upgradeslots")));
                     equip.setLevel(rs.getByte("level"));
                     equip.setStr(rs.getShort("str"));
                     equip.setDex(rs.getShort("dex"));
                     equip.setInt(rs.getShort("int"));
                     equip.setLuk(rs.getShort("luk"));
                     equip.setHp(rs.getShort("hp"));
                     equip.setMp(rs.getShort("mp"));
                     equip.setHpR(rs.getShort("hpR"));
                     equip.setMpR(rs.getShort("mpR"));
                     Equip eq = (Equip)ii.getEquipById(equip.getItemId());
                     if (equip.getHpR() == 0 && eq.getHpR() > 0) {
                        equip.setHpR(eq.getHpR());
                     }

                     if (equip.getMpR() == 0 && eq.getMpR() > 0) {
                        equip.setMpR(eq.getMpR());
                     }

                     equip.setWatk(rs.getShort("watk"));
                     equip.setMatk(rs.getShort("matk"));
                     equip.setWdef(rs.getShort("wdef"));
                     equip.setMdef(rs.getShort("mdef"));
                     equip.setAcc(rs.getShort("acc"));
                     equip.setAvoid(rs.getShort("avoid"));
                     equip.setHands(rs.getShort("hands"));
                     equip.setSpeed(rs.getShort("speed"));
                     equip.setJump(rs.getShort("jump"));
                     equip.setViciousHammer(rs.getByte("ViciousHammer"));
                     equip.setItemEXP(rs.getInt("itemEXP"));
                     equip.setGMLog(rs.getString("GM_Log"));
                     equip.setDurability(rs.getInt("durability"));
                     equip.setEnhance(rs.getByte("enhance"));
                     equip.setState(rs.getByte("state"));
                     equip.setLines(rs.getByte("line"));
                     equip.setPotential1(rs.getInt("potential1"));
                     equip.setPotential2(rs.getInt("potential2"));
                     equip.setPotential3(rs.getInt("potential3"));
                     equip.setPotential4(rs.getInt("potential4"));
                     equip.setPotential5(rs.getInt("potential5"));
                     equip.setPotential6(rs.getInt("potential6"));
                     equip.setFusionAnvil(rs.getInt("fusionAnvil"));
                     equip.setGiftFrom(rs.getString("sender"));
                     equip.setIncSkill(rs.getInt("incSkill"));
                     equip.setPVPDamage(rs.getShort("pvpDamage"));
                     equip.setCharmEXP(rs.getShort("charmEXP"));
                     if (equip.getCharmEXP() < 0) {
                        equip.setCharmEXP(((Equip)ii.getEquipById(equip.getItemId())).getCharmEXP());
                     }

                     if (equip.getUniqueId() > -1L) {
                        if (GameConstants.isEffectRing(rs.getInt("itemid"))) {
                           MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                           if (ring != null) {
                              equip.setRing(ring);
                           }
                        } else if (equip.getItemId() / 10000 == 166) {
                           Android android = Android.loadFromDb(equip.getItemId(), equip.getUniqueId());
                           if (android != null) {
                              equip.setAndroid(android);
                           }
                        }
                     }

                     equip.setSpecialAttribute(rs.getShort("enhanctBuff"));
                     equip.setReqLevel(rs.getByte("reqLevel"));
                     equip.setGrowthEnchant(rs.getByte("yggdrasilWisdom"));
                     if (!GameConstants.isTheSeedRing(equip.getItemId())) {
                        equip.setFinalStrike(rs.getByte("finalStrike") > 0);
                     } else {
                        equip.setFinalStrike(false);
                        equip.setTheSeedRingLevel(rs.getByte("finalStrike"));
                     }

                     equip.setBossDamage(rs.getByte("bossDamage"));
                     equip.setIgnorePDR(rs.getByte("ignorePDR"));
                     equip.setTotalDamage(rs.getByte("totalDamage"));
                     equip.setAllStat(rs.getByte("allStat"));
                     equip.setKarmaCount(rs.getByte("karmaCount"));
                     equip.setSoulEnchanter(rs.getShort("soulenchanter"));
                     equip.setSoulName(rs.getShort("soulname"));
                     equip.setSoulPotential(rs.getShort("soulpotential"));
                     equip.setSoulSkill(rs.getInt("soulskill"));
                     equip.setStarForce(rs.getByte("starforce"));
                     equip.setFire(rs.getLong("fire"));
                     equip.setArc(rs.getInt("arc"));
                     equip.setArcEXP(rs.getInt("arcexp"));
                     equip.setArcLevel(rs.getInt("arclevel"));
                     equip.setDownLevel(rs.getByte("downlevel"));
                     equip.setSpecialPotential(rs.getInt("additional_enhance"));
                     equip.setItemState(Math.max(0, rs.getInt("item_state_flag")));
                     equip.setCsGrade(rs.getInt("csoption_grade"));
                     equip.setCsOption1(rs.getInt("csoption1"));
                     equip.setCsOption2(rs.getInt("csoption2"));
                     equip.setCsOption3(rs.getInt("csoption3"));
                     equip.setCsOptionExpireDate(rs.getLong("csoption_expired"));
                     equip.setExGradeOption(rs.getLong("ex_grade_option"));
                     equip.setCHUC(rs.getInt("CHUC"));
                     equip.setClearCheck(rs.getInt("check_clear"));
                     equip.setSpecialRoyal(rs.getInt("special_royal") == 1);
                     equip.setSPGrade(rs.getInt("sp_grade"));
                     equip.setSPAttack(rs.getInt("sp_attack"));
                     equip.setSPAllStat(rs.getInt("sp_all_stat"));
                     equip.setSerialNumberEquip(rs.getLong("serial_number"));
                     equip.setCashEnchantCount(rs.getInt("cash_enchant_count"));
                     if (equip.getSerialNumberEquip() == 0L) {
                        equip.setSerialNumberEquip(System.currentTimeMillis() + Randomizer.nextInt());
                     }

                     if (GameConstants.isCanUpgradeExceptionalEquip(equip.getItemId())) {
                        this.LoadEquipExceptionalUpgrade(equip, "cabinet_equipment_exceptional");
                     }
                  }

                  item_ = equip.copy();
               }

               ret.add(
                       new MapleCabinetItem(
                               rs.getInt("cabinet_index"), rs.getLong("cabinet_expired_time"), rs.getString("cabinet_title"), rs.getString("cabinet_desc"), item_
                       )
               );
            }
         }
      } catch (SQLException var26) {
      } finally {
         try {
            if (rs != null) {
               rs.close();
               ResultSet var29 = null;
            }

            if (ps != null) {
               ps.close();
               PreparedStatement var28 = null;
            }
         } catch (SQLException var23) {
         }
      }

      return ret;
   }

   public void saveCabinetItems(MapleCabinet cabinet, int accountID) {
      if (cabinet != null) {
         List<MapleCabinetItem> items = cabinet.getItems();
         DBConnection db = new DBConnection();
         PreparedStatement ps = null;
         PreparedStatement pse = null;
         PreparedStatement psee = null;
         List<String> psList = new ArrayList<>();

         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("DELETE FROM `cabinet_items` WHERE `accountid` = ?");
            ps.setInt(1, accountID);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement(
                    "INSERT INTO `cabinet_items` (accountid, itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender, once_trade, cabinet_index, cabinet_expired_time, cabinet_title, cabinet_desc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    1
            );
            pse = con.prepareStatement(
                    "INSERT INTO `cabinet_equipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            psee = con.prepareStatement("INSERT INTO `cabinet_equipenchant` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)");
            int ai = 0;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            for (MapleCabinetItem item_ : new LinkedList<>(items)) {
               Item item = item_.getItem();
               MapleInventoryType mit = MapleInventoryType.getByType((byte)(item.getItemId() / 1000000));
               if (item.getPosition() != -55) {
                  if (mit == MapleInventoryType.EQUIP && ii.isCash(item.getItemId())) {
                     mit = MapleInventoryType.CASH_EQUIP;
                  }

                  AtomicInteger idx = new AtomicInteger(1);
                  ps.setInt(idx.getAndIncrement(), accountID);
                  ps.setInt(idx.getAndIncrement(), item.getItemId());
                  ps.setInt(idx.getAndIncrement(), mit.getType());
                  ps.setInt(idx.getAndIncrement(), item.getPosition());
                  ps.setInt(idx.getAndIncrement(), item.getQuantity());
                  ps.setString(idx.getAndIncrement(), item.getOwner());
                  ps.setString(idx.getAndIncrement(), item.getGMLog());
                  if (item.getPet() != null) {
                     ps.setLong(idx.getAndIncrement(), Math.max(item.getUniqueId(), item.getPet().getUniqueId()));
                  } else {
                     ps.setLong(idx.getAndIncrement(), item.getUniqueId());
                  }

                  ps.setLong(idx.getAndIncrement(), item.getExpiration());
                  ps.setInt(idx.getAndIncrement(), item.getFlag());
                  ps.setByte(idx.getAndIncrement(), (byte)this.value);
                  ps.setString(idx.getAndIncrement(), item.getGiftFrom());
                  ps.setInt(idx.getAndIncrement(), item.getOnceTrade());
                  ps.setInt(idx.getAndIncrement(), item_.getIndex());
                  ps.setLong(idx.getAndIncrement(), item_.getExpiredTime());
                  ps.setString(idx.getAndIncrement(), item_.getTitle());
                  ps.setString(idx.getAndIncrement(), item_.getDesc());
                  ps.executeUpdate();
                  ResultSet rs = ps.getGeneratedKeys();
                  if (!rs.next()) {
                     rs.close();
                  } else {
                     long iid = rs.getLong(1);
                     rs.close();
                     item.setInventoryId(iid);
                     if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED) || mit.equals(MapleInventoryType.CASH_EQUIP)) {
                        Equip equip = (Equip)item;
                        if (equip.getUniqueId() > 0L && equip.getItemId() / 10000 == 166) {
                           Android android = equip.getAndroid();
                           if (android != null) {
                              android.saveToDb();
                           }
                        }

                        int index = 1;
                        pse.setLong(index++, iid);
                        pse.setInt(index++, 0);
                        pse.setInt(index++, accountID);
                        pse.setInt(index++, equip.getItemId());
                        pse.setInt(index++, Math.max(0, equip.getUpgradeSlots()));
                        pse.setInt(index++, equip.getLevel());
                        pse.setInt(index++, equip.getStr());
                        pse.setInt(index++, equip.getDex());
                        pse.setInt(index++, equip.getInt());
                        pse.setInt(index++, equip.getLuk());
                        pse.setInt(index++, equip.getArc());
                        pse.setInt(index++, equip.getArcEXP());
                        pse.setInt(index++, equip.getArcLevel());
                        pse.setInt(index++, equip.getHp());
                        pse.setInt(index++, equip.getMp());
                        pse.setInt(index++, equip.getHpR());
                        pse.setInt(index++, equip.getMpR());
                        pse.setInt(index++, equip.getWatk());
                        pse.setInt(index++, equip.getMatk());
                        pse.setInt(index++, equip.getWdef());
                        pse.setInt(index++, equip.getMdef());
                        pse.setInt(index++, equip.getAcc());
                        pse.setInt(index++, equip.getAvoid());
                        pse.setInt(index++, equip.getHands());
                        pse.setInt(index++, equip.getSpeed());
                        pse.setInt(index++, equip.getJump());
                        pse.setInt(index++, equip.getViciousHammer());
                        pse.setInt(index++, equip.getItemEXP());
                        pse.setInt(index++, equip.getDurability());
                        pse.setByte(index++, equip.getEnhance());
                        pse.setByte(index++, equip.getState());
                        pse.setByte(index++, equip.getLines());
                        pse.setInt(index++, equip.getPotential1());
                        pse.setInt(index++, equip.getPotential2());
                        pse.setInt(index++, equip.getPotential3());
                        pse.setInt(index++, equip.getPotential4());
                        pse.setInt(index++, equip.getPotential5());
                        pse.setInt(index++, equip.getPotential6());
                        pse.setInt(index++, equip.getFusionAnvil());
                        pse.setInt(index++, equip.getIncSkill());
                        pse.setShort(index++, equip.getCharmEXP());
                        pse.setShort(index++, equip.getPVPDamage());
                        pse.setShort(index++, equip.getSpecialAttribute());
                        pse.setByte(index++, equip.getReqLevel());
                        pse.setByte(index++, equip.getGrowthEnchant());
                        pse.setByte(index++, (byte)(equip.getFinalStrike() ? 1 : 0));
                        pse.setShort(index++, equip.getBossDamage());
                        pse.setShort(index++, equip.getIgnorePDR());
                        pse.setByte(index++, equip.getTotalDamage());
                        pse.setByte(index++, equip.getAllStat());
                        pse.setByte(index++, equip.getKarmaCount());
                        pse.setShort(index++, equip.getSoulName());
                        pse.setShort(index++, equip.getSoulEnchanter());
                        pse.setShort(index++, equip.getSoulPotential());
                        pse.setInt(index++, equip.getSoulSkill());
                        pse.setLong(index++, equip.getFire());
                        pse.setByte(index++, equip.getStarForce());
                        pse.setInt(index++, 0);
                        pse.setInt(index++, equip.getDownLevel());
                        pse.setInt(index++, equip.getSpecialPotential());
                        pse.setInt(index++, equip.getSPGrade());
                        pse.setInt(index++, equip.getSPAttack());
                        pse.setInt(index++, equip.getSPAllStat());
                        pse.setInt(index++, equip.getItemState());
                        pse.setInt(index++, equip.getCsGrade());
                        pse.setInt(index++, equip.getCsOption1());
                        pse.setInt(index++, equip.getCsOption2());
                        pse.setInt(index++, equip.getCsOption3());
                        pse.setLong(index++, equip.getCsOptionExpireDate());
                        pse.setLong(index++, equip.getExGradeOption());
                        pse.setInt(index++, equip.getCHUC());
                        pse.setInt(index++, equip.getClearCheck());
                        pse.setInt(index++, equip.isSpecialRoyal() ? 1 : 0);
                        pse.setLong(index++, equip.getSerialNumberEquip());
                        pse.setInt(index++, equip.getCashEnchantCount());
                        pse.executeUpdate();
                        if (equip.getExceptionalSlot() > 0) {
                           String exInfo = "INSERT INTO `cabinet_equipment_exceptional` (`inventoryitemid`, `slot`, `str`, `dex`, `int`, `luk`, `hp`, `mp`, `watk`, `matk`, `wdef`, `mdef`, `acc`, `avoid`) VALUES ("
                                   + iid
                                   + ", "
                                   + equip.getExceptionalSlot()
                                   + ", "
                                   + equip.getExceptSTR()
                                   + ", "
                                   + equip.getExceptDEX()
                                   + ", "
                                   + equip.getExceptINT()
                                   + ", "
                                   + equip.getExceptLUK()
                                   + ", "
                                   + equip.getExceptHP()
                                   + ", "
                                   + equip.getExceptMP()
                                   + ", "
                                   + equip.getExceptWATK()
                                   + ", "
                                   + equip.getExceptMATK()
                                   + ", "
                                   + equip.getExceptWDEF()
                                   + ", "
                                   + equip.getExceptMDEF()
                                   + ", "
                                   + equip.getExceptAVOID()
                                   + ", "
                                   + equip.getExceptJUMP()
                                   + ")";
                           psList.add(exInfo);
                        }
                     }

                     ai++;
                  }
               }
            }
         } catch (SQLException var47) {
            var47.printStackTrace();
            new RuntimeException(var47);
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var49 = null;
               }

               if (pse != null) {
                  pse.close();
                  PreparedStatement var50 = null;
               }

               if (psee != null) {
                  psee.close();
                  PreparedStatement var51 = null;
               }
            } catch (SQLException var38) {
            }
         }

         if (!psList.isEmpty()) {
            try (Connection con = DBConnection.getConnection()) {
               for (String exInfo : psList) {
                  try (PreparedStatement psEx = con.prepareStatement(exInfo)) {
                     psEx.executeUpdate();
                     psEx.close();
                  } catch (Exception var43) {
                     System.out.println("[ERROR] 익셉셔널 정보 저장 오류");
                     var43.printStackTrace();
                  }
               }
            } catch (Exception var45) {
               System.out.println("[ERROR] 익셉셔널 정보 저장 오류");
               var45.printStackTrace();
            }
         }
      }
   }

   public Map<Long, Pair<Item, MapleInventoryType>> loadItems(boolean login, int id, int jobId) throws SQLException {
      Map<Long, Pair<Item, MapleInventoryType>> items = new LinkedHashMap<>();
      StringBuilder query = new StringBuilder();
      query.append("SELECT * FROM `");
      query.append(this.table);
      query.append("` LEFT JOIN `");
      query.append(this.table_equip);
      query.append("` USING(`inventoryitemid`) WHERE `type` = ?");
      if (this.getValue() != 7) {
         query.append(" AND `");
         query.append(this.arg);
         query.append("` = ?");
      }

      if (login) {
         query.append(" AND `inventorytype` = ");
         query.append(MapleInventoryType.EQUIPPED.getType());
      }

      DBConnection db = new DBConnection();

      try (
              Connection con = DBConnection.getConnection();
              PreparedStatement ps = con.prepareStatement(query.toString());
      ) {
         ps.setInt(1, this.value);
         if (this.getValue() != 7) {
            ps.setInt(2, id);
         }

         try (ResultSet rs = ps.executeQuery()) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            while (rs.next()) {
               if (ii.itemExists(rs.getInt("itemid"))) {
                  MapleInventoryType mit = MapleInventoryType.getByType(rs.getByte("inventorytype"));
                  if (mit == MapleInventoryType.EQUIP && ii.isCash(rs.getInt("itemid"))) {
                     mit = MapleInventoryType.CASH_EQUIP;
                  }

                  Item item_;
                  if (!mit.equals(MapleInventoryType.EQUIP) && !mit.equals(MapleInventoryType.EQUIPPED) && !mit.equals(MapleInventoryType.CASH_EQUIP)) {
                     Item item = new Item(rs.getInt("itemid"), rs.getShort("position"), rs.getShort("quantity"), rs.getInt("flag"), rs.getLong("uniqueid"));
                     item.setOwner(rs.getString("owner"));
                     item.setInventoryId(rs.getLong("inventoryitemid"));
                     item.setExpiration(rs.getLong("expiredate"));
                     item.setGMLog(rs.getString("GM_Log"));
                     item.setGiftFrom(rs.getString("sender"));
                     item.setOnceTrade(rs.getInt("once_trade"));
                     if (GameConstants.isPet(item.getItemId())) {
                        if (item.getUniqueId() > -1L) {
                           MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getUniqueId(), item.getPosition());
                           if (pet != null) {
                              item.setPet(pet);
                           }
                        } else {
                           item.setPet(MaplePet.createPet(item.getItemId(), MapleInventoryIdentifier.getInstance()));
                        }
                     }

                     if (GameConstants.isIntensePowerCrystal(item.getItemId())) {
                        PreparedStatement ps2 = con.prepareStatement("SELECT * FROM `intense_power_crystal` WHERE `item_unique_id` = ?");
                        ps2.setLong(1, item.getUniqueId());
                        ResultSet rs2 = ps2.executeQuery();
                        if (rs2.next()) {
                           IntensePowerCrystal ipc = new IntensePowerCrystal(
                                   rs2.getInt("player_id"),
                                   rs2.getLong("item_unique_id"),
                                   rs2.getInt("member_count"),
                                   rs2.getInt("mob_id"),
                                   rs2.getLong("price"),
                                   rs2.getLong("unk"),
                                   rs2.getLong("gain_time")
                           );
                           item.setIntensePowerCrystal(ipc);
                        }

                        rs2.close();
                        ps2.close();
                     }

                     if (item.getUniqueId() == -1L
                             && (mit.getType() == MapleInventoryType.CASH.getType() || mit.getType() == MapleInventoryType.CASH_EQUIP.getType())) {
                        item.setUniqueId(MapleInventoryIdentifier.getInstance());
                     }

                     item_ = item.copy();
                     items.put(rs.getLong("inventoryitemid"), new Pair<>(item_, mit));
                  } else {
                     Equip equip = new Equip(rs.getInt("itemid"), rs.getShort("position"), rs.getLong("uniqueid"), rs.getInt("flag"));
                     if (!login && equip.getPosition() != -55
                             || mit.equals(MapleInventoryType.EQUIPPED) && equip.getPosition() < -1800 & equip.getPosition() > -1900
                             || equip.getPosition() == -10
                             || equip.getPosition() == -11
                             || equip.getPosition() == -111
                             || equip.getPosition() == -1507) {
                        int itemid = equip.getItemId();
                        if ((
                                itemid == 1109000
                                        || itemid >= 1009017 && itemid <= 1009082
                                        || itemid >= 1709000 && itemid <= 1709018
                                        || itemid >= 1079000 && itemid <= 1079005
                                        || itemid >= 1059000 && itemid <= 1059049
                                        || itemid >= 1009000 && itemid <= 1009016
                        )
                                && equip.getUniqueId() <= -1L) {
                           equip.setUniqueId(Randomizer.rand(100055555, 109999999));
                        }

                        equip.setQuantity((short)1);
                        equip.setInventoryId(rs.getLong("inventoryitemid"));
                        equip.setTempUniqueID(rs.getLong("inventoryitemid"));
                        equip.setOwner(rs.getString("owner"));
                        equip.setExpiration(rs.getLong("expiredate"));
                        equip.setUpgradeSlots((byte)Math.max(0, rs.getByte("upgradeslots")));
                        equip.setLevel(rs.getByte("level"));
                        equip.setStr(rs.getShort("str"));
                        equip.setDex(rs.getShort("dex"));
                        equip.setInt(rs.getShort("int"));
                        equip.setLuk(rs.getShort("luk"));
                        equip.setHp(rs.getShort("hp"));
                        equip.setMp(rs.getShort("mp"));
                        equip.setHpR(rs.getShort("hpR"));
                        equip.setMpR(rs.getShort("mpR"));
                        Equip eq = (Equip)ii.getEquipById(equip.getItemId());
                        if (equip.getHpR() == 0 && eq.getHpR() > 0) {
                           equip.setHpR(eq.getHpR());
                        }

                        if (equip.getMpR() == 0 && eq.getMpR() > 0) {
                           equip.setMpR(eq.getMpR());
                        }

                        equip.setWatk(rs.getShort("watk"));
                        equip.setMatk(rs.getShort("matk"));
                        equip.setWdef(rs.getShort("wdef"));
                        equip.setMdef(rs.getShort("mdef"));
                        equip.setAcc(rs.getShort("acc"));
                        equip.setAvoid(rs.getShort("avoid"));
                        equip.setHands(rs.getShort("hands"));
                        equip.setSpeed(rs.getShort("speed"));
                        equip.setJump(rs.getShort("jump"));
                        equip.setViciousHammer(rs.getByte("ViciousHammer"));
                        equip.setItemEXP(rs.getInt("itemEXP"));
                        equip.setGMLog(rs.getString("GM_Log"));
                        equip.setDurability(rs.getInt("durability"));
                        equip.setEnhance(rs.getByte("enhance"));
                        equip.setState(rs.getByte("state"));
                        equip.setLines(rs.getByte("line"));
                        equip.setPotential1(rs.getInt("potential1"));
                        equip.setPotential2(rs.getInt("potential2"));
                        equip.setPotential3(rs.getInt("potential3"));
                        equip.setPotential4(rs.getInt("potential4"));
                        equip.setPotential5(rs.getInt("potential5"));
                        equip.setPotential6(rs.getInt("potential6"));
                        equip.setFusionAnvil(rs.getInt("fusionAnvil"));
                        equip.setGiftFrom(rs.getString("sender"));
                        equip.setIncSkill(rs.getInt("incSkill"));
                        equip.setPVPDamage(rs.getShort("pvpDamage"));
                        equip.setCharmEXP(rs.getShort("charmEXP"));
                        if (equip.getCharmEXP() < 0) {
                           equip.setCharmEXP(((Equip)ii.getEquipById(equip.getItemId())).getCharmEXP());
                        }

                        if (equip.getUniqueId() > -1L) {
                           if (GameConstants.isEffectRing(rs.getInt("itemid"))) {
                              MapleRing ring = MapleRing.loadFromDb(equip.getUniqueId(), mit.equals(MapleInventoryType.EQUIPPED));
                              if (ring != null) {
                                 equip.setRing(ring);
                              }
                           } else if (equip.getItemId() / 10000 == 166) {
                              Android android = Android.loadFromDb(equip.getItemId(), equip.getUniqueId());
                              if (android != null) {
                                 equip.setAndroid(android);
                              }
                           }
                        }

                        equip.setSpecialAttribute(rs.getShort("enhanctBuff"));
                        equip.setReqLevel(rs.getByte("reqLevel"));
                        equip.setGrowthEnchant(rs.getByte("yggdrasilWisdom"));
                        if (!GameConstants.isTheSeedRing(equip.getItemId())) {
                           equip.setFinalStrike(rs.getByte("finalStrike") > 0);
                        } else {
                           equip.setFinalStrike(false);
                           equip.setTheSeedRingLevel(rs.getByte("finalStrike"));
                        }

                        equip.setBossDamage(rs.getByte("bossDamage"));
                        equip.setIgnorePDR(rs.getByte("ignorePDR"));
                        equip.setTotalDamage(rs.getByte("totalDamage"));
                        equip.setAllStat(rs.getByte("allStat"));
                        equip.setKarmaCount(rs.getByte("karmaCount"));
                        equip.setSoulEnchanter(rs.getShort("soulenchanter"));
                        equip.setSoulName(rs.getShort("soulname"));
                        equip.setSoulPotential(rs.getShort("soulpotential"));
                        equip.setSoulSkill(rs.getInt("soulskill"));
                        equip.setStarForce(rs.getByte("starforce"));
                        equip.setFire(rs.getLong("fire"));
                        equip.setArc(rs.getInt("arc"));
                        equip.setArcEXP(rs.getInt("arcexp"));
                        equip.setArcLevel(rs.getInt("arclevel"));
                        equip.setDownLevel(rs.getByte("downlevel"));
                        equip.setSpecialPotential(rs.getInt("additional_enhance"));
                        equip.setItemState(Math.max(0, rs.getInt("item_state_flag")));
                        equip.setCsGrade(rs.getInt("csoption_grade"));
                        equip.setCsOption1(rs.getInt("csoption1"));
                        equip.setCsOption2(rs.getInt("csoption2"));
                        equip.setCsOption3(rs.getInt("csoption3"));
                        equip.setCsOptionExpireDate(rs.getLong("csoption_expired"));
                        equip.setExGradeOption(rs.getLong("ex_grade_option"));
                        equip.setCHUC(rs.getInt("CHUC"));
                        equip.setClearCheck(rs.getInt("check_clear"));
                        equip.setSpecialRoyal(rs.getInt("special_royal") == 1);
                        equip.setSPGrade(rs.getInt("sp_grade"));
                        equip.setSPAttack(rs.getInt("sp_attack"));
                        equip.setSPAllStat(rs.getInt("sp_all_stat"));
                        equip.setSerialNumberEquip(rs.getLong("serial_number"));
                        equip.setCashEnchantCount(rs.getInt("cash_enchant_count"));
                        if (equip.getSerialNumberEquip() == 0L) {
                           equip.setSerialNumberEquip(System.currentTimeMillis() + Randomizer.nextInt());
                        }

                        if (GameConstants.isCanUpgradeExceptionalEquip(equip.getItemId()) && !this.table_equip_exceptional.isEmpty()) {
                           this.LoadEquipExceptionalUpgrade(equip, this.table_equip_exceptional);
                        }
                     }

                     item_ = equip.copy();
                     this.checkEquip((Equip)item_);
                     if (GameConstants.isDemonAvenger(jobId)) {
                        int itemId = item_.getItemId();
                        if (GameConstants.isArcaneSymbol(itemId) || GameConstants.isAuthenticSymbol(itemId)) {
                           this.checkDemonAvangerSymbol((Equip)item_);
                        }
                     }

                     items.put(rs.getLong("inventoryitemid"), new Pair<>(item_, mit));
                  }

                  if (this.getValue() == 7) {
                     Center.Auction.addItem(
                             new AuctionItemPackage(
                                     rs.getInt("characterid"),
                                     rs.getInt("accountid"),
                                     rs.getString("ownername"),
                                     item_.copy(),
                                     rs.getLong("bid"),
                                     rs.getLong("meso"),
                                     rs.getLong("expired"),
                                     rs.getBoolean("bargain"),
                                     rs.getInt("buyer"),
                                     rs.getLong("buytime"),
                                     rs.getLong("starttime"),
                                     rs.getInt("status"),
                                     rs.getInt("historyID")
                             )
                     );
                  }
               }
            }

            rs.close();
         } catch (SQLException var21) {
            var21.printStackTrace();
         }

         ps.close();
      } catch (SQLException var24) {
         System.out.println(var24);
         new RuntimeException(var24);
      }

      return items;
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, int id) throws SQLException {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         this.saveItems(items, con, id);
      } catch (SQLException var9) {
         new RuntimeException(var9);
      }
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, Connection con, int id) throws SQLException {
      this.saveItems(items, con, id, null);
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, Connection con, int id, List<AuctionItemPackage> aitems) throws SQLException {
      StringBuilder query = new StringBuilder();
      query.append("DELETE FROM `").append(this.table).append("` WHERE `type` = ?");
      if (this.getValue() != 7) {
         query.append(" AND `").append(this.arg).append("` = ?");
      }

      PreparedStatement ps = con.prepareStatement(query.toString());
      ps.setInt(1, this.value);
      if (this.getValue() != 7) {
         ps.setInt(2, id);
      }
      ps.executeUpdate();
      ps.close();

      if (items != null && !items.isEmpty()) {
         List<String> psList = new ArrayList<>();
         for (int i = 0; i < items.size(); i++) {
            Pair<Item, MapleInventoryType> pair = items.get(i);
            Item item = pair.getLeft();
            MapleInventoryType mit = pair.getRight();
            AuctionItemPackage aItem = (this.getValue() == 7 && aitems != null) ? aitems.get(i) : null;

            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO `").append(this.table).append("` (");
            if (this.getValue() == 7) {
               insert.append("historyID, accountid, ");
            }
            insert.append(this.arg)
                    .append(", itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender, once_trade");
            if (this.getValue() == 7) {
               insert.append(", bid, meso, expired, bargain, ownername, buyer, buytime, starttime, `status`, inventoryitemid");
            }
            insert.append(") VALUES (");
            if (this.getValue() == 7) {
               insert.append("?, ?, ");
            }
            insert.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
            if (this.getValue() == 7) {
               insert.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
            }
            insert.append(")");

            PreparedStatement psItem = con.prepareStatement(insert.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            int idx = 1;
            if (this.getValue() == 7) {
               psItem.setInt(idx++, aItem.getHistoryID());
               psItem.setInt(idx++, aItem.getAccountID());
            }
            psItem.setInt(idx++, (id == -1 && aItem != null) ? aItem.getOwnerId() : id);
            psItem.setInt(idx++, item.getItemId());
            psItem.setInt(idx++, mit.getType());
            psItem.setInt(idx++, item.getPosition());
            psItem.setInt(idx++, item.getQuantity());
            psItem.setString(idx++, item.getOwner());
            psItem.setString(idx++, item.getGMLog());
            psItem.setInt(idx++, (item.getPet() != null ? (int)Math.max(item.getUniqueId(), item.getPet().getUniqueId()) : (int)item.getUniqueId()));
            psItem.setInt(idx++, (int)item.getExpiration());
            psItem.setInt(idx++, item.getFlag());
            psItem.setInt(idx++, this.value);
            psItem.setString(idx++, item.getGiftFrom());
            psItem.setInt(idx++, item.getOnceTrade());
            if (this.getValue() == 7) {
               psItem.setInt(idx++, (int) aItem.getBid());
               psItem.setInt(idx++, (int)aItem.getMesos());
               psItem.setInt(idx++, (int)aItem.getExpiredTime());
               psItem.setBoolean(idx++, aItem.isBargain());
               psItem.setString(idx++, aItem.getOwnerName());
               psItem.setInt(idx++, aItem.getBuyer());
               psItem.setInt(idx++, (int)aItem.getBuyTime());
               psItem.setInt(idx++, (int)aItem.getStartTime());
               psItem.setInt(idx++, aItem.getType(false, true));
               psItem.setInt(idx++, (int)item.getInventoryId());
            }
            psItem.executeUpdate();
            ResultSet rs = psItem.getGeneratedKeys();
            if (rs.next()) {
               long iid = rs.getLong(1);
               item.setInventoryId(iid);
            }
            rs.close();
            psItem.close();

            // Save equipment if needed
            if (mit == MapleInventoryType.EQUIP || mit == MapleInventoryType.EQUIPPED || mit == MapleInventoryType.CASH_EQUIP) {
               Equip equip = (Equip)item;
               StringBuilder equipInsert = new StringBuilder();
               equipInsert.append("INSERT INTO `").append(this.table_equip).append("` VALUES (DEFAULT, ");
               equipInsert.append((int)equip.getInventoryId()).append(", ");
               equipInsert.append(this.arg.equals("characterid") ? id : 0).append(", ");
               equipInsert.append(this.arg.equals("characterid") ? 0 : id).append(", ");
               equipInsert.append(equip.getItemId()).append(", ");
               equipInsert.append(Math.max(0, equip.getUpgradeSlots())).append(", ");
               equipInsert.append(equip.getLevel()).append(", ");
               equipInsert.append(equip.getStr()).append(", ");
               equipInsert.append(equip.getDex()).append(", ");
               equipInsert.append(equip.getInt()).append(", ");
               equipInsert.append(equip.getLuk()).append(", ");
               equipInsert.append(equip.getArc()).append(", ");
               equipInsert.append(equip.getArcEXP()).append(", ");
               equipInsert.append(equip.getArcLevel()).append(", ");
               equipInsert.append(equip.getHp()).append(", ");
               equipInsert.append(equip.getMp()).append(", ");
               equipInsert.append(equip.getHpR()).append(", ");
               equipInsert.append(equip.getMpR()).append(", ");
               equipInsert.append(equip.getWatk()).append(", ");
               equipInsert.append(equip.getMatk()).append(", ");
               equipInsert.append(equip.getWdef()).append(", ");
               equipInsert.append(equip.getMdef()).append(", ");
               equipInsert.append(equip.getAcc()).append(", ");
               equipInsert.append(equip.getAvoid()).append(", ");
               equipInsert.append(equip.getHands()).append(", ");
               equipInsert.append(equip.getSpeed()).append(", ");
               equipInsert.append(equip.getJump()).append(", ");
               equipInsert.append(equip.getViciousHammer()).append(", ");
               equipInsert.append(equip.getItemEXP()).append(", ");
               equipInsert.append(equip.getDurability()).append(", ");
               equipInsert.append(equip.getEnhance()).append(", ");
               equipInsert.append(equip.getState()).append(", ");
               equipInsert.append(equip.getLines()).append(", ");
               equipInsert.append(equip.getPotential1()).append(", ");
               equipInsert.append(equip.getPotential2()).append(", ");
               equipInsert.append(equip.getPotential3()).append(", ");
               equipInsert.append(equip.getPotential4()).append(", ");
               equipInsert.append(equip.getPotential5()).append(", ");
               equipInsert.append(equip.getPotential6()).append(", ");
               equipInsert.append(equip.getFusionAnvil()).append(", ");
               equipInsert.append(equip.getIncSkill()).append(", ");
               equipInsert.append(equip.getCharmEXP()).append(", ");
               equipInsert.append(equip.getPVPDamage()).append(", ");
               equipInsert.append(equip.getSpecialAttribute()).append(", ");
               equipInsert.append(equip.getReqLevel()).append(", ");
               equipInsert.append(equip.getGrowthEnchant()).append(", ");
               if (!GameConstants.isTheSeedRing(equip.getItemId())) {
                  equipInsert.append(equip.getFinalStrike() ? 1 : 0).append(", ");
               } else {
                  equipInsert.append(equip.getTheSeedRingLevel()).append(", ");
               }
               equipInsert.append(equip.getBossDamage()).append(", ");
               equipInsert.append(equip.getIgnorePDR()).append(", ");
               equipInsert.append(equip.getTotalDamage()).append(", ");
               equipInsert.append(equip.getAllStat()).append(", ");
               equipInsert.append(equip.getKarmaCount()).append(", ");
               equipInsert.append(equip.getSoulName()).append(", ");
               equipInsert.append(equip.getSoulEnchanter()).append(", ");
               equipInsert.append(equip.getSoulPotential()).append(", ");
               equipInsert.append(equip.getSoulSkill()).append(", ");
               equipInsert.append(equip.getFire()).append(", ");
               equipInsert.append(equip.getStarForce()).append(", ");
               equipInsert.append("0, ");
               equipInsert.append(equip.getDownLevel()).append(", ");
               equipInsert.append(equip.getSpecialPotential()).append(", ");
               equipInsert.append(equip.getSPGrade()).append(", ");
               equipInsert.append(equip.getSPAttack()).append(", ");
               equipInsert.append(equip.getSPAllStat()).append(", ");
               equipInsert.append(equip.getItemState()).append(", ");
               equipInsert.append(equip.getCsGrade()).append(", ");
               equipInsert.append(equip.getCsOption1()).append(", ");
               equipInsert.append(equip.getCsOption2()).append(", ");
               equipInsert.append(equip.getCsOption3()).append(", ");
               equipInsert.append(equip.getCsOptionExpireDate()).append(", ");
               equipInsert.append(equip.getExGradeOption()).append(", ");
               equipInsert.append(equip.getCHUC()).append(", ");
               equipInsert.append(equip.getClearCheck()).append(", ");
               equipInsert.append(equip.isSpecialRoyal() ? 1 : 0).append(", ");
               equipInsert.append(equip.getSerialNumberEquip()).append(", ");
               equipInsert.append(equip.getCashEnchantCount());
               equipInsert.append(")");
               PreparedStatement psEquip = con.prepareStatement(equipInsert.toString());
               psEquip.executeUpdate();
               psEquip.close();

               if (equip.getExceptionalSlot() > 0 && !this.table_equip_exceptional.isEmpty()) {
                  String exInfo = "INSERT INTO `"
                          + this.table_equip_exceptional
                          + "` (`inventoryitemid`, `slot`, `str`, `dex`, `int`, `luk`, `hp`, `mp`, `watk`, `matk`, `wdef`, `mdef`, `acc`, `avoid`) VALUES ("
                          + (int)equip.getInventoryId()
                          + ", "
                          + equip.getExceptionalSlot()
                          + ", "
                          + equip.getExceptSTR()
                          + ", "
                          + equip.getExceptDEX()
                          + ", "
                          + equip.getExceptINT()
                          + ", "
                          + equip.getExceptLUK()
                          + ", "
                          + equip.getExceptHP()
                          + ", "
                          + equip.getExceptMP()
                          + ", "
                          + equip.getExceptWATK()
                          + ", "
                          + equip.getExceptMATK()
                          + ", "
                          + equip.getExceptWDEF()
                          + ", "
                          + equip.getExceptMDEF()
                          + ", "
                          + equip.getExceptAVOID()
                          + ", "
                          + equip.getExceptJUMP()
                          + ")";
                  psList.add(exInfo);
               }

               if (equip.getUniqueId() > 0L && equip.getItemId() / 10000 == 166) {
                  Android android = equip.getAndroid();
                  if (android != null) {
                     android.saveToDb();
                  }
               }
            }

            // Save pet if needed
            if (item.getPet() != null) {
               item.getPet().saveToDb();
            }
         }

         if (!psList.isEmpty()) {
            for (String exInfo : psList) {
               try (PreparedStatement psEx = con.prepareStatement(exInfo)) {
                  psEx.executeUpdate();
               } catch (Exception var32) {
                  System.out.println("[ERROR] 익셉셔널 정보 저장 오류");
                  var32.printStackTrace();
               }
            }
         }
      }
   }

   /*

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, Connection con, int id, List<AuctionItemPackage> aitems) throws SQLException {
      StringBuilder query = new StringBuilder();
      query.append("DELETE FROM `");
      query.append(this.table);
      query.append("` WHERE `type` = ?");
      if (this.getValue() != 7) {
         query.append(" AND `").append(this.arg);
         query.append("` = ?");
      }

      PreparedStatement ps = con.prepareStatement(query.toString());
      ps.setInt(1, this.value);
      if (this.getValue() != 7) {
         ps.setInt(2, id);
      }

      ps.executeUpdate();
      ps.close();
      if (items != null && !items.isEmpty()) {
         String q = "INSERT INTO " + this.table_equip + " VALUES (";
         StringBuilder pse_sb = new StringBuilder(q);
         StringBuilder ps_sb = new StringBuilder("INSERT INTO `");
         ps_sb.append(this.table);
         ps_sb.append("` (");
         if (this.getValue() == 7) {
            ps_sb.append("historyID, accountid, ");
         }

         ps_sb.append(this.arg);
         ps_sb.append(", itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender, once_trade");
         if (this.getValue() == 7) {
            ps_sb.append(", bid, meso, expired, bargain, ownername, buyer, buytime, starttime, `status`, inventoryitemid");
         }

         ps_sb.append(") VALUES (");
         StringBuilder[] buffer = new StringBuilder[1000];
         StringBuilder[] buffer2 = new StringBuilder[1000];
         List<String> psList = new ArrayList<>();
         buffer[0] = new StringBuilder(ps_sb.toString());
         buffer2[0] = new StringBuilder(pse_sb.toString());
         int[] itemSize = new int[1000];
         List<Item> itemList = new ArrayList<>();
         int ai = 0;
         int totalSize = 0;
         int folderSize = 0;

         for (Pair<Item, MapleInventoryType> pair : new LinkedList<>(items)) {
            AuctionItemPackage aItem = this.getValue() == 7 ? aitems.get(totalSize) : null;
            if (ai != 0) {
               buffer[folderSize].append(", (");
            }

            Item item = pair.getLeft();
            MapleInventoryType mit = pair.getRight();
            if (item.getPosition() != -55) {
               if (this.getValue() == 7) {
                  buffer[folderSize].append(aItem.getHistoryID() + ", ");
                  buffer[folderSize].append(aItem.getAccountID() + ", ");
               }

               buffer[folderSize].append((id == -1 ? aitems.get(totalSize).getOwnerId() : id) + ", ");
               buffer[folderSize].append(item.getItemId() + ", ");
               buffer[folderSize].append(mit.getType() + ", ");
               buffer[folderSize].append(item.getPosition() + ", ");
               buffer[folderSize].append(item.getQuantity() + ", ");
               buffer[folderSize].append("'" + item.getOwner() + "', ");
               buffer[folderSize].append("'" + item.getGMLog() + "', ");
               if (item.getPet() != null) {
                  buffer[folderSize].append(Math.max(item.getUniqueId(), item.getPet().getUniqueId()) + ", ");
               } else {
                  buffer[folderSize].append(item.getUniqueId() + ", ");
               }

               buffer[folderSize].append(item.getExpiration() + ", ");
               buffer[folderSize].append(item.getFlag() + ", ");
               buffer[folderSize].append(this.value + ", ");
               buffer[folderSize].append("'" + item.getGiftFrom() + "', ");
               buffer[folderSize].append(item.getOnceTrade());
               if (this.getValue() == 7) {
                  buffer[folderSize].append(", ");
                  buffer[folderSize].append(aItem.getBid() + ", ");
                  buffer[folderSize].append(aItem.getMesos() + ", ");
                  buffer[folderSize].append(aItem.getExpiredTime() + ", ");
                  buffer[folderSize].append(aItem.isBargain() + ", ");
                  buffer[folderSize].append("'" + aItem.getOwnerName() + "', ");
                  buffer[folderSize].append(aItem.getBuyer() + ", ");
                  buffer[folderSize].append(aItem.getBuyTime() + ", ");
                  buffer[folderSize].append(aItem.getStartTime() + ", ");
                  buffer[folderSize].append(aItem.getType(false, true) + ", ");
                  buffer[folderSize].append(item.getInventoryId());
               }

               buffer[folderSize].append(")");
               ai++;
               totalSize++;
               itemList.add(item);
               itemSize[folderSize]++;
               if (ai >= 1000) {
                  buffer[++folderSize] = new StringBuilder(ps_sb.toString());
                  ai = 0;
               }
            }
         }

         int equipCount = 0;
         int itemIndex = 0;
         int folderSize2 = 0;

         for (int i = 0; i <= folderSize; i++) {
            ps = con.prepareStatement(buffer[i].toString(), 1);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int index = 0;
            int count = 0;
            buffer2[i] = new StringBuilder(pse_sb.toString());
            if (!DBConfig.isGanglim) {
               folderSize2 = i;
            }

            for (; rs.next(); itemIndex++) {
               long autoIncrement = 0L;
               Item item = null;
               if (!DBConfig.isGanglim) {
                  autoIncrement = this.getValue() == 7 ? rs.getLong(1) - (itemSize[i] - 1) : rs.getLong(1);
                  item = itemList.get(itemIndex);
                  item.setInventoryId(autoIncrement);
               } else {
                  autoIncrement = rs.getLong(1);
                  item = itemList.get(itemIndex);
                  if (this.getValue() != 7) {
                     item.setInventoryId(autoIncrement);
                  }
               }

               if (item.getPet() != null) {
                  item.getPet().saveToDb();
               }

               if (item.getItemId() / 1000000 == 1) {
                  count++;
                  Equip equip = (Equip)item;
                  if (equip.getUniqueId() > 0L && equip.getItemId() / 10000 == 166) {
                     Android android = equip.getAndroid();
                     if (android != null) {
                        android.saveToDb();
                     }
                  }

                  if (equip.getExceptionalSlot() > 0 && !this.table_equip_exceptional.isEmpty()) {
                     String exInfo = "INSERT INTO `"
                        + this.table_equip_exceptional
                        + "` (`inventoryitemid`, `slot`, `str`, `dex`, `int`, `luk`, `hp`, `mp`, `watk`, `matk`, `wdef`, `mdef`, `acc`, `avoid`) VALUES ("
                        + autoIncrement
                        + ", "
                        + equip.getExceptionalSlot()
                        + ", "
                        + equip.getExceptSTR()
                        + ", "
                        + equip.getExceptDEX()
                        + ", "
                        + equip.getExceptINT()
                        + ", "
                        + equip.getExceptLUK()
                        + ", "
                        + equip.getExceptHP()
                        + ", "
                        + equip.getExceptMP()
                        + ", "
                        + equip.getExceptWATK()
                        + ", "
                        + equip.getExceptMATK()
                        + ", "
                        + equip.getExceptWDEF()
                        + ", "
                        + equip.getExceptMDEF()
                        + ", "
                        + equip.getExceptAVOID()
                        + ", "
                        + equip.getExceptJUMP()
                        + ")";
                     psList.add(exInfo);
                  }

                  if (index != 0) {
                     buffer2[i].append(", (");
                  }

                  buffer2[i].append("DEFAULT, ");
                  buffer2[i].append(equip.getInventoryId()).append(", ");
                  buffer2[i].append(this.arg.equals("characterid") ? id : 0).append(", ");
                  buffer2[i].append(this.arg.equals("characterid") ? 0 : id).append(", ");
                  buffer2[i].append(equip.getItemId()).append(", ");
                  buffer2[i].append(Math.max(0, equip.getUpgradeSlots())).append(", ");
                  buffer2[i].append(equip.getLevel()).append(", ");
                  buffer2[i].append(equip.getStr()).append(", ");
                  buffer2[i].append(equip.getDex()).append(", ");
                  buffer2[i].append(equip.getInt()).append(", ");
                  buffer2[i].append(equip.getLuk()).append(", ");
                  buffer2[i].append(equip.getArc()).append(", ");
                  buffer2[i].append(equip.getArcEXP()).append(", ");
                  buffer2[i].append(equip.getArcLevel()).append(", ");
                  buffer2[i].append(equip.getHp()).append(", ");
                  buffer2[i].append(equip.getMp()).append(", ");
                  buffer2[i].append(equip.getHpR()).append(", ");
                  buffer2[i].append(equip.getMpR()).append(", ");
                  buffer2[i].append(equip.getWatk()).append(", ");
                  buffer2[i].append(equip.getMatk()).append(", ");
                  buffer2[i].append(equip.getWdef()).append(", ");
                  buffer2[i].append(equip.getMdef()).append(", ");
                  buffer2[i].append(equip.getAcc()).append(", ");
                  buffer2[i].append(equip.getAvoid()).append(", ");
                  buffer2[i].append(equip.getHands()).append(", ");
                  buffer2[i].append(equip.getSpeed()).append(", ");
                  buffer2[i].append(equip.getJump()).append(", ");
                  buffer2[i].append(equip.getViciousHammer()).append(", ");
                  buffer2[i].append(equip.getItemEXP()).append(", ");
                  buffer2[i].append(equip.getDurability()).append(", ");
                  buffer2[i].append(equip.getEnhance()).append(", ");
                  buffer2[i].append(equip.getState()).append(", ");
                  buffer2[i].append(equip.getLines()).append(", ");
                  buffer2[i].append(equip.getPotential1()).append(", ");
                  buffer2[i].append(equip.getPotential2()).append(", ");
                  buffer2[i].append(equip.getPotential3()).append(", ");
                  buffer2[i].append(equip.getPotential4()).append(", ");
                  buffer2[i].append(equip.getPotential5()).append(", ");
                  buffer2[i].append(equip.getPotential6()).append(", ");
                  buffer2[i].append(equip.getFusionAnvil()).append(", ");
                  buffer2[i].append(equip.getIncSkill()).append(", ");
                  buffer2[i].append(equip.getCharmEXP()).append(", ");
                  buffer2[i].append(equip.getPVPDamage()).append(", ");
                  buffer2[i].append(equip.getSpecialAttribute()).append(", ");
                  buffer2[i].append(equip.getReqLevel()).append(", ");
                  buffer2[i].append(equip.getGrowthEnchant()).append(", ");
                  if (!GameConstants.isTheSeedRing(equip.getItemId())) {
                     buffer2[i].append(equip.getFinalStrike() ? 1 : 0).append(", ");
                  } else {
                     buffer2[i].append(equip.getTheSeedRingLevel()).append(", ");
                  }

                  buffer2[i].append(equip.getBossDamage()).append(", ");
                  buffer2[i].append(equip.getIgnorePDR()).append(", ");
                  buffer2[i].append(equip.getTotalDamage()).append(", ");
                  buffer2[i].append(equip.getAllStat()).append(", ");
                  buffer2[i].append(equip.getKarmaCount()).append(", ");
                  buffer2[i].append(equip.getSoulName()).append(", ");
                  buffer2[i].append(equip.getSoulEnchanter()).append(", ");
                  buffer2[i].append(equip.getSoulPotential()).append(", ");
                  buffer2[i].append(equip.getSoulSkill()).append(", ");
                  buffer2[i].append(equip.getFire()).append(", ");
                  buffer2[i].append(equip.getStarForce()).append(", ");
                  buffer2[i].append("0, ");
                  buffer2[i].append(equip.getDownLevel()).append(", ");
                  buffer2[i].append(equip.getSpecialPotential()).append(", ");
                  buffer2[i].append(equip.getSPGrade()).append(", ");
                  buffer2[i].append(equip.getSPAttack()).append(", ");
                  buffer2[i].append(equip.getSPAllStat()).append(", ");
                  buffer2[i].append(equip.getItemState()).append(", ");
                  buffer2[i].append(equip.getCsGrade()).append(", ");
                  buffer2[i].append(equip.getCsOption1()).append(", ");
                  buffer2[i].append(equip.getCsOption2()).append(", ");
                  buffer2[i].append(equip.getCsOption3()).append(", ");
                  buffer2[i].append(equip.getCsOptionExpireDate()).append(", ");
                  buffer2[i].append(equip.getExGradeOption()).append(", ");
                  buffer2[i].append(equip.getCHUC()).append(", ");
                  buffer2[i].append(equip.getClearCheck()).append(", ");
                  buffer2[i].append(equip.isSpecialRoyal() ? 1 : 0).append(", ");
                  buffer2[i].append(equip.getSerialNumberEquip()).append(", ");
                  buffer2[i].append(equip.getCashEnchantCount());
                  buffer2[i].append(")");
                  equipCount++;
                  if (DBConfig.isGanglim && ++index >= 1000) {
                     folderSize2++;
                     buffer2[folderSize] = new StringBuilder(pse_sb.toString());
                     index = 0;
                  }
               }
            }

            ps.close();
            rs.close();
            if (DBConfig.isGanglim && this.getValue() == 7 && count == 0) {
               buffer2[i].setLength(0);
            }
         }

         if (equipCount > 0) {
            if (DBConfig.isGanglim && this.getValue() == 7) {
               for (int i = 0; i <= folderSize; i++) {
                  String str = buffer2[i].toString();
                  if (!str.isEmpty()) {
                     ps = con.prepareStatement(str);
                     ps.executeUpdate();
                     ps.close();
                  }
               }
            } else {
               for (int ix = 0; ix <= folderSize2; ix++) {
                  ps = con.prepareStatement(buffer2[ix].toString());
                  ps.executeUpdate();
                  ps.close();
               }
            }
         }

         if (!psList.isEmpty()) {
            for (String exInfo : psList) {
               try (PreparedStatement psEx = con.prepareStatement(exInfo)) {
                  psEx.executeUpdate();
                  psEx.close();
               } catch (Exception var32) {
                  System.out.println("[ERROR] 익셉셔널 정보 저장 오류");
                  var32.printStackTrace();
               }
            }
         }
      }
   }



   public void checkSymbol(Equip equip, int jobId) {
      int neededStat = 0;
      if (GameConstants.isArcaneSymbol(equip.getItemId()) || GameConstants.isAuthenticSymbol(equip.getItemId())) {
         if (equip.getOwner().equals("강화된 심볼")) {
            neededStat += 1500;
         }

         if (GameConstants.isArcaneSymbol(equip.getItemId())) {
            if (GameConstants.isXenon(jobId)) {
               neededStat += 48 * (equip.getArcLevel() + 2);
            } else if (GameConstants.isDemonAvenger(jobId)) {
               neededStat = 210 * (equip.getArcLevel() + 2);
            } else {
               neededStat += 100 * (equip.getArcLevel() + 2);
            }
         } else if (GameConstants.isXenon(jobId)) {
            neededStat += 96 * (equip.getArcLevel() * 2 + 3);
         } else if (GameConstants.isDemonAvenger(jobId)) {
            neededStat = 210 * (equip.getArcLevel() * 2 + 3);
         } else {
            neededStat += 100 * (equip.getArcLevel() * 2 + 3);
         }

         if ((jobId < 100 || jobId >= 200)
            && jobId != 512
            && jobId != 1512
            && jobId != 2512
            && (jobId < 1100 || jobId >= 1200)
            && !GameConstants.isAran(jobId)
            && !GameConstants.isBlaster(jobId)
            && !GameConstants.isDemonSlayer(jobId)
            && !GameConstants.isMichael(jobId)
            && !GameConstants.isKaiser(jobId)
            && !GameConstants.isZero(jobId)
            && !GameConstants.isArk(jobId)
            && !GameConstants.isAdele(jobId)) {
            if ((jobId < 200 || jobId >= 300)
               && !GameConstants.isFlameWizard(jobId)
               && !GameConstants.isEvan(jobId)
               && !GameConstants.isLuminous(jobId)
               && (jobId < 3200 || jobId >= 3300)
               && !GameConstants.isKinesis(jobId)
               && !GameConstants.isIllium(jobId)
               && !GameConstants.isLara(jobId)) {
               if (!GameConstants.isKain(jobId)
                  && (jobId < 300 || jobId >= 400)
                  && jobId != 522
                  && jobId != 532
                  && !GameConstants.isMechanic(jobId)
                  && !GameConstants.isAngelicBuster(jobId)
                  && (jobId < 1300 || jobId >= 1400)
                  && !GameConstants.isMercedes(jobId)
                  && (jobId < 3300 || jobId >= 3400)) {
                  if ((jobId < 400 || jobId >= 500)
                     && (jobId < 1400 || jobId >= 1500)
                     && !GameConstants.isPhantom(jobId)
                     && !GameConstants.isKadena(jobId)
                     && !GameConstants.isHoyoung(jobId)) {
                     if (GameConstants.isDemonAvenger(jobId)) {
                        if (equip.getHp() != neededStat) {
                           equip.setHp((short)neededStat);
                        }
                     } else if (GameConstants.isXenon(jobId) && equip.getStr() != neededStat) {
                        equip.setStr((short)neededStat);
                        equip.setDex((short)neededStat);
                        equip.setLuk((short)neededStat);
                     }
                  } else if (equip.getLuk() != neededStat) {
                     equip.setLuk((short)neededStat);
                  }
               } else if (equip.getDex() != neededStat) {
                  equip.setDex((short)neededStat);
               }
            } else if (equip.getInt() != neededStat) {
               equip.setInt((short)neededStat);
            }
         } else if (equip.getStr() != neededStat) {
            equip.setStr((short)neededStat);
         }
      }
   }
*/

   public void checkDemonAvangerSymbol(Equip symbol) {
      int itemId = symbol.getItemId();
      if (GameConstants.isArcaneSymbol(itemId)) {
         short checkStat = (short)(210 * (symbol.getArcLevel() + 2));
         if (symbol.getHp() != checkStat) {
            symbol.setHp(checkStat);
         }
      } else if (GameConstants.isAuthenticSymbol(itemId)) {
         short checkStat = (short)(210 * (2 * symbol.getArcLevel() + 3));
         if (symbol.getHp() != checkStat) {
            symbol.setHp(checkStat);
         }
      }
   }

   public void checkEquip(Equip equip) {
      if (equip.getItemGrade() >= 5) {
         equip.setItemGrade(4);

         for (int i = 0; i < 3; i++) {
            InventoryHandler.setPotential(GradeRandomOption.Black, true, equip, i);
         }
      }

      if (GameConstants.isEmblem(equip.getItemId())) {
         List<Integer> potentials = new ArrayList<>();
         potentials.addAll(equip.getPotentials(false, 3));

         for (int i = 0; i < potentials.size(); i++) {
            if (potentials.get(i) != 0 && MapleItemInformationProvider.getInstance().getPotentialInfo(potentials.get(i)).get(0).boss) {
               InventoryHandler.setPotential(GradeRandomOption.Black, false, equip, i);
            }
         }

         List<Integer> additionalPotentials = new ArrayList<>();
         additionalPotentials.addAll(equip.getPotentials(true, 3));

         for (int ix = 0; ix < additionalPotentials.size(); ix++) {
            if (additionalPotentials.get(ix) != 0 && MapleItemInformationProvider.getInstance().getPotentialInfo(additionalPotentials.get(ix)).get(0).boss) {
               InventoryHandler.setPotential(GradeRandomOption.Additional, false, equip, ix);
            }
         }
      }
   }
}
