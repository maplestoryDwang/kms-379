package objects.utils;

import constants.GameConstants;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;
import objects.users.enchant.EquipStat;
import objects.users.enchant.StarForceHyperUpgrade;

public class ExGradeOptionChange {
   public static void main(String[] args) {
      DBConnection.init();
      System.setProperty("net.sf.odinms.wzpath", "wz");
      Map<Long, Item> itemMap = new HashMap<>();
      MapleItemInformationProvider.getInstance().runEtc();
      MapleItemInformationProvider.getInstance().runItems();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM `auctionitems` LEFT JOIN `auctionequipment` USING(`inventoryitemid`) WHERE `ex_grade_option` > 0 AND itemid < 2000000"
         );
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            Item item_ = null;
            int itemID = rs.getInt("itemid");
            if (itemID / 1000000 == 1) {
               Equip equip = new Equip(itemID, rs.getShort("position"), rs.getLong("uniqueid"), rs.getInt("flag"));
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

               item_ = equip.copy();
            }

            if (item_ != null) {
               itemMap.put(rs.getLong("inventoryitemid"), item_);
            }
         }
      } catch (SQLException var22) {
         var22.printStackTrace();
      }

      try (Connection con = DBConnection.getConnection()) {
         for (Entry<Long, Item> itemData : itemMap.entrySet()) {
            List<Integer> data = new ArrayList<>();
            long invID = itemData.getKey();
            Equip item = (Equip)itemData.getValue();
            ExGradeOptionChange.IntColumn[] columnList = new ExGradeOptionChange.IntColumn[]{
               new ExGradeOptionChange.IntColumn(item.getTotalStr(), item.getStr(), StarForceHyperUpgrade.getHUStat(item, EquipStat.STR, item.getStr()), "str"),
               new ExGradeOptionChange.IntColumn(item.getTotalDex(), item.getDex(), StarForceHyperUpgrade.getHUStat(item, EquipStat.DEX, item.getDex()), "dex"),
               new ExGradeOptionChange.IntColumn(item.getTotalInt(), item.getInt(), StarForceHyperUpgrade.getHUStat(item, EquipStat.INT, item.getInt()), "int"),
               new ExGradeOptionChange.IntColumn(item.getTotalLuk(), item.getLuk(), StarForceHyperUpgrade.getHUStat(item, EquipStat.LUK, item.getLuk()), "luk"),
               new ExGradeOptionChange.IntColumn(item.getTotalHp(), item.getHp(), StarForceHyperUpgrade.getHUStat(item, EquipStat.MHP, item.getHp()), "hp"),
               new ExGradeOptionChange.IntColumn(item.getTotalMp(), item.getMp(), StarForceHyperUpgrade.getHUStat(item, EquipStat.MMP, item.getMp()), "mp"),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalDownLevel(), item.getDownLevel(), StarForceHyperUpgrade.getHUStat(item, EquipStat.DOWNLEVEL, item.getDownLevel()), "downlevel"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalWdef(), item.getWdef(), StarForceHyperUpgrade.getHUStat(item, EquipStat.WDEF, item.getWdef()), "wdef"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalMdef(), item.getMdef(), StarForceHyperUpgrade.getHUStat(item, EquipStat.MDEF, item.getMdef()), "mdef"
               ),
               new ExGradeOptionChange.IntColumn(item.getTotalAcc(), item.getAcc(), StarForceHyperUpgrade.getHUStat(item, EquipStat.ACC, item.getAcc()), "acc"),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalAvoid(), item.getAvoid(), StarForceHyperUpgrade.getHUStat(item, EquipStat.AVOID, item.getAvoid()), "avoid"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalWatk(), item.getWatk(), StarForceHyperUpgrade.getHUStat(item, EquipStat.WATK, item.getWatk()), "watk"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalMatk(), item.getMatk(), StarForceHyperUpgrade.getHUStat(item, EquipStat.MATK, item.getMatk()), "matk"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalSpeed(), item.getSpeed(), StarForceHyperUpgrade.getHUStat(item, EquipStat.SPEED, item.getSpeed()), "speed"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalJump(), item.getJump(), StarForceHyperUpgrade.getHUStat(item, EquipStat.JUMP, item.getJump()), "jump"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalBossDamage(),
                  item.getBossDamage(),
                  StarForceHyperUpgrade.getHUStat(item, EquipStat.BOSS_DAMAGE, item.getBossDamage()),
                  "bossDamage"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalIgnorePDR(), item.getIgnorePDR(), StarForceHyperUpgrade.getHUStat(item, EquipStat.IGNORE_PDR, item.getIgnorePDR()), "ignorePDR"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalMaxDamage(),
                  item.getTotalDamage(),
                  StarForceHyperUpgrade.getHUStat(item, EquipStat.TOTAL_DAMAGE, item.getTotalDamage()),
                  "totalDamage"
               ),
               new ExGradeOptionChange.IntColumn(
                  item.getTotalAllStat(), item.getAllStat(), StarForceHyperUpgrade.getHUStat(item, EquipStat.ALL_STAT, item.getAllStat()), "allStat"
               )
            };
            StringBuilder str = new StringBuilder("UPDATE auctionequipment ");
            List<ExGradeOptionChange.IntColumn> columns = new ArrayList<>();

            for (ExGradeOptionChange.IntColumn column : columnList) {
               int exGradeOption = column.int2 - (column.int2 + (!item.isAmazingHyperUpgradeUsed() ? column.int4 : 0));
               if (exGradeOption < column.int2 && column.int1 != column.int2) {
                  columns.add(column);
               }
            }

            int i = 0;

            for (ExGradeOptionChange.IntColumn columnx : columns) {
               int exGradeOption = columnx.int1 - (columnx.int2 + (!item.isAmazingHyperUpgradeUsed() ? columnx.int4 : 0));
               i++;
               if (data.isEmpty()) {
                  str.append("SET ");
               }

               str.append("`").append(columnx.key).append("` = ?");
               if (columns.size() != i) {
                  str.append(", ");
               }

               data.add(columnx.int2 - exGradeOption);
            }

            str.append(" WHERE inventoryitemid = ?");
            if (!data.isEmpty()) {
               PreparedStatement ps = con.prepareStatement(str.toString());

               for (int var29 = 0; var29 < data.size(); var29++) {
                  ps.setInt(var29 + 1, data.get(var29));
               }

               ps.setLong(data.size() + 1, invID);
               ps.executeUpdate();
            }
         }
      } catch (SQLException var20) {
         var20.printStackTrace();
      }
   }

   public static class IntColumn {
      public int int1;
      public int int2;
      public int int4;
      public String key;

      public IntColumn(int int1, int int2, int int4, String key) {
         this.int1 = int1;
         this.int2 = int2;
         this.int4 = int4;
         this.key = key;
      }
   }
}
