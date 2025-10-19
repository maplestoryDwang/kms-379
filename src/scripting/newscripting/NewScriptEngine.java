package scripting.newscripting;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.PartyMemberEntry;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.enchant.BonusStat;
import objects.users.enchant.BonusStatPlaceType;
import objects.users.enchant.EquipEnchantMan;
import objects.users.enchant.EquipEnchantOption;
import objects.users.enchant.EquipEnchantScroll;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.ItemUpgradeFlag;
import objects.users.enchant.ScrollType;
import objects.utils.FileoutputUtil;
import scripting.EventInstanceManager;
import scripting.EventManager;

public abstract class NewScriptEngine {
   private MapleClient c;

   protected NewScriptEngine() {
   }

   public void initEngine(MapleClient c) {
      this.c = c;
   }

   public MapleClient getClient() {
      return this.c;
   }

   public MapleCharacter getPlayer() {
      return this.c == null ? null : this.c.getPlayer();
   }

   public final EventManager getEventManager(String event) {
      return this.c.getChannelServer().getEventSM().getEventManager(event);
   }

   public final EventInstanceManager getEventInstance() {
      return this.c.getPlayer().getEventInstance();
   }

   public final void mapMessage(int type, String message) {
      this.c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(type, message));
   }

   public String checkEventNumber(MapleCharacter partyLeader, int questID) {
      return this.checkEventNumber(partyLeader, questID, false);
   }

   public String checkEventNumber(MapleCharacter partyLeader, int questID, boolean bosstier) {
      if (partyLeader.isGM() && DBConfig.isGanglim) {
         return null;
      } else {
         String overLap = "";

         for (PartyMemberEntry pchr : partyLeader.getParty().getPartyMemberList()) {
            MapleCharacter chr = this.getPlayer().getMap().getCharacterById(pchr.getId());
            if (chr.getOneInfo(questID, "eNum") != null) {
               int max = 1;
               if (bosstier) {
                  max = chr.getBossTier() + 1;
               }

               if (!DBConfig.isGanglim) {
                  boolean single = partyLeader.getPartyMembers().size() == 1;
                  if (questID == QuestExConstants.Magnus.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalMagnus" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.Pierre.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPierre" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.CrimsonQueen.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalCrimsonQueen" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.VonBon.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVonBon" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.Vellum.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVellum" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.VonLeon.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "VonLeon" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.Horntail.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Horntail" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.Arkarium.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.PinkBeen.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPinkBeen" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.ChaosPinkBeen.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosPinkBeen" + (single ? "Single" : "Multi"));
                  }

                  if (questID == QuestExConstants.Cygnus.getQuestID()) {
                     max += chr.getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "Cygnus" + (single ? "Single" : "Multi"));
                  }

                  if (chr.isGM()) {
                  }
               }

               if (DBConfig.isGanglim) {
                  if (Integer.parseInt(chr.getOneInfo(questID, "eNum")) >= max) {
                     overLap = overLap + " " + chr.getName();
                  }
               } else if (questID != QuestExConstants.Pierre.getQuestID()
                  && questID != QuestExConstants.CrimsonQueen.getQuestID()
                  && questID != QuestExConstants.VonBon.getQuestID()
                  && questID != QuestExConstants.Vellum.getQuestID()
                  && questID != QuestExConstants.Magnus.getQuestID()
                  && questID != QuestExConstants.ChaosPierre.getQuestID()
                  && questID != QuestExConstants.ChaosCrimsonQueen.getQuestID()
                  && questID != QuestExConstants.ChaosVonBon.getQuestID()
                  && questID != QuestExConstants.ChaosVellum.getQuestID()
                  && questID != QuestExConstants.HardMagnus.getQuestID()
                  && questID != QuestExConstants.PinkBeen.getQuestID()
                  && questID != QuestExConstants.ChaosPinkBeen.getQuestID()
                  && questID != QuestExConstants.VonLeon.getQuestID()
                  && questID != QuestExConstants.Arkarium.getQuestID()
                  && questID != QuestExConstants.Horntail.getQuestID()
                  && questID != QuestExConstants.Cygnus.getQuestID()) {
                  overLap = overLap + " " + chr.getName();
               } else {
                  boolean singlex = partyLeader.getPartyMembers().size() == 1;
                  if (!singlex
                     && (
                        questID == QuestExConstants.Pierre.getQuestID()
                           || questID == QuestExConstants.CrimsonQueen.getQuestID()
                           || questID == QuestExConstants.VonBon.getQuestID()
                           || questID == QuestExConstants.Vellum.getQuestID()
                           || questID == QuestExConstants.Magnus.getQuestID()
                           || questID == QuestExConstants.VonLeon.getQuestID()
                           || questID == QuestExConstants.Arkarium.getQuestID()
                           || questID == QuestExConstants.Horntail.getQuestID()
                           || questID == QuestExConstants.PinkBeen.getQuestID()
                     )) {
                     max++;
                  }

                  String eNum = chr.getOneInfo(questID, "eNum_" + (singlex ? "single" : "multi"));
                  if (Integer.parseInt(eNum == null ? "0" : eNum) >= max) {
                     overLap = overLap + " " + chr.getName();
                  }
               }
            }
         }

         return overLap.equals("") ? null : overLap;
      }
   }

   public String checkEventNumber(MapleCharacter partyLeader, int questID, String key) {
      if (partyLeader.isGM() && DBConfig.isGanglim) {
         return null;
      } else {
         String overLap = "";

         for (PartyMemberEntry pchr : partyLeader.getParty().getPartyMemberList()) {
            MapleCharacter chr = this.getPlayer().getMap().getCharacterById(pchr.getId());
            if (chr.getOneInfo(questID, key) != null && chr.getOneInfo(questID, key).equals("1")) {
               overLap = overLap + " " + chr.getName();
            }
         }

         return overLap.equals("") ? null : overLap;
      }
   }

   public void updateEventNumber(MapleCharacter partyLeader, int questID) {
      for (PartyMemberEntry pchr : partyLeader.getParty().getPartyMemberList()) {
         MapleCharacter chr = this.getPlayer().getMap().getCharacterById(pchr.getId());
         chr.updateOneInfo(questID, "eNum", String.valueOf(chr.getOneInfoQuestInteger(questID, "eNum") + 1));
         if (!DBConfig.isGanglim
            && (
               questID == QuestExConstants.Arkarium.getQuestID()
                  || questID == QuestExConstants.VonLeon.getQuestID()
                  || questID == QuestExConstants.Horntail.getQuestID()
                  || questID == QuestExConstants.PinkBeen.getQuestID()
            )) {
            boolean single = partyLeader.getPartyMemberSize() == 1;
            chr.updateOneInfo(
               questID,
               "eNum_" + (single ? "single" : "multi"),
               String.valueOf(chr.getOneInfoQuestInteger(questID, "eNum_" + (single ? "single" : "multi")) + 1)
            );
         }
      }
   }

   public String checkEventLastDate(MapleCharacter partyLeader, int questID) {
      if (!partyLeader.isGM() && DBConfig.isHosting) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
         Date date = new Date();
         String lastDate = "";

         for (PartyMemberEntry pchr : partyLeader.getParty().getPartyMemberList()) {
            MapleCharacter chr = this.getPlayer().getMap().getCharacterById(pchr.getId());
            if (chr.getOneInfo(questID, "lastDate") != null) {
               try {
                  long remaining = date.getTime() - sdf.parse(chr.getOneInfo(questID, "lastDate")).getTime();
                  if (remaining < 1800000L) {
                     lastDate = lastDate + " " + chr.getName() + "(" + (30L - remaining / 60000L) + "분)";
                  }
               } catch (Exception var11) {
               }
            }
         }

         return lastDate.equals("") ? null : lastDate;
      } else {
         return null;
      }
   }

   public void updateLastDate(MapleCharacter partyLeader, int questID) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
      String fDate = sdf.format(new Date());

      for (PartyMemberEntry pchr : partyLeader.getParty().getPartyMemberList()) {
         MapleCharacter chr = this.getPlayer().getMap().getCharacterById(pchr.getId());
         chr.updateOneInfo(questID, "lastDate", fDate);
      }
   }

   public void updateQuestEx(MapleCharacter partyLeader, int bossQuest) {
      for (PartyMemberEntry pchr : partyLeader.getParty().getPartyMemberList()) {
         MapleCharacter chr = this.getPlayer().getMap().getCharacterById(pchr.getId());
         chr.updateOneInfo(bossQuest, "eNum", String.valueOf(chr.getOneInfoQuestInteger(bossQuest, "eNum") + 1));
      }
   }

   public void exchangeSupportEquip(int itemID, int allStat, int attack, int downLevel) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.addStr((short)allStat);
      item.addDex((short)allStat);
      item.addInt((short)allStat);
      item.addLuk((short)allStat);
      item.addWatk((short)attack);
      item.addMatk((short)attack);
      item.setDownLevel((byte)downLevel);
      item.setCHUC(10);
      item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
      item.setUpgradeSlots((byte)0);
      item.setState((byte)2);
      if (!DBConfig.isGanglim) {
         item.setState((byte)19);
         item.setPotential1(40086);
         item.setPotential2(30086);
         item.setPotential3(30086);
      }

      item.setLines((byte)3);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangeSupportEquipPeriod(int itemID, int allStat, int attack, int period) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.addStr((short)allStat);
      item.addDex((short)allStat);
      item.addInt((short)allStat);
      item.addLuk((short)allStat);
      item.addWatk((short)attack);
      item.addMatk((short)attack);
      if (period > 0) {
         item.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
      }

      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangeSupportEquipPeriod(int itemID, int allStat, int attack, int period, int downLevel) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.addStr((short)allStat);
      item.addDex((short)allStat);
      item.addInt((short)allStat);
      item.addLuk((short)allStat);
      item.addWatk((short)attack);
      item.addMatk((short)attack);
      item.setDownLevel((byte)downLevel);
      if (period > 0) {
         item.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
      }

      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangePinkBeanSupportEquip(int itemID) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      if (itemID != 1022144) {
         item.addWatk((short)100);
         item.setCHUC(15);
         item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
         item.setUpgradeSlots((byte)0);
      }

      item.setState((byte)3);
      item.setLines((byte)3);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangeUniqueItem(int itemID) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.setState((byte)3);
      item.setLines((byte)3);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangeEquipBonusStatPeriod(int itemID, int period) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
      if (BonusStat.resetBonusStat(item, BonusStatPlaceType.LevelledRebirthFlame)) {
      }

      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangeSupportEquipBonusStatPeriod(int itemID, int allStat, int attack, int period) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
      item.addStr((short)allStat);
      item.addDex((short)allStat);
      item.addInt((short)allStat);
      item.addLuk((short)allStat);
      item.addWatk((short)attack);
      item.addMatk((short)attack);
      if (BonusStat.resetBonusStat(item, BonusStatPlaceType.LevelledRebirthFlame)) {
      }

      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangeEquipCHUCWithScroll(int itemID, int chuc, int scrollIndex) {
      Equip equip = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      int flag = EquipEnchantMan.filterForJobWeapon(itemID);
      ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD};
      ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP};
      ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_INT};
      List<EquipEnchantScroll> source = new ArrayList<>();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (ItemUpgradeFlag f : flagArray) {
         for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
            if (f2.check(flag)) {
               option.setOption(
                  f2.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex) * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1)
               );
               if (option.flag > 0) {
                  source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
               }
            }
         }
      }

      if (equip.getItemId() == 1242140) {
         source.clear();
         EquipEnchantOption option = new EquipEnchantOption();
         option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
         option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex));
         source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
      }

      if (equip.getItemId() == 1232121) {
         source.clear();
         EquipEnchantOption option = new EquipEnchantOption();
         option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
         option.setOption(ItemUpgradeFlag.INC_MHP.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex) * 50);
         source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
      }

      if (equip.getItemId() == 1292021) {
         source.clear();
         EquipEnchantOption option = new EquipEnchantOption();
         option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
         option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex));
         source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
      }

      if (equip.getItemId() == 1362148) {
         source.clear();
         EquipEnchantOption option = new EquipEnchantOption();
         option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
         option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex));
         source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
      }

      if (equip.getItemId() == 1362148) {
         source.clear();
         EquipEnchantOption option = new EquipEnchantOption();
         option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
         option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex));
         source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
      }

      if (source.size() > 0) {
         EquipEnchantScroll scroll = source.get(0);
         if (scroll != null) {
            Equip zeroEquip = null;
            if (GameConstants.isZero(this.c.getPlayer().getJob())) {
               zeroEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
            }

            for (int i = 0; i < 8; i++) {
               scroll.upgrade(equip, 0, true, zeroEquip);
            }

            equip.setCHUC(chuc);
            equip.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            if (BonusStat.resetBonusStat(equip, BonusStatPlaceType.LevelledRebirthFlame)) {
            }

            if (zeroEquip != null) {
               zeroEquip.setCHUC(chuc);
               zeroEquip.setItemState(equip.getItemState());
               zeroEquip.setExGradeOption(equip.getExGradeOption());
            }

            MapleInventoryManipulator.addFromDrop(this.c, equip, false);
         }
      }
   }

   public void exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(int itemID, int chuc, int scrollIndex, int period, int type) {
      Equip equip = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      int flag = EquipEnchantMan.filterForJobWeapon(itemID);
      ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD};
      ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP};
      ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_INT};
      List<EquipEnchantScroll> source = new ArrayList<>();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (ItemUpgradeFlag f : flagArray) {
         for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(itemID), scrollIndex));
            if (f2.check(flag)) {
               option.setOption(
                  f2.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(itemID), scrollIndex) * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1)
               );
               if (option.flag > 0) {
                  source.add(new EquipEnchantScroll(itemID, scrollIndex, option, ScrollType.UPGRADE, 0, false));
               }
            }
         }
      }

      if (source.size() > 0) {
         EquipEnchantScroll scroll = source.get(0);
         if (scroll != null) {
            for (int i = 0; i < 12; i++) {
               scroll.upgrade(equip, 0, true, null);
            }

            equip.setCHUC(chuc);
            int nFlag = equip.getFlag();
            nFlag |= (short)ItemFlag.POSSIBLE_TRADING.getValue();
            equip.setDownLevel((byte)120);
            equip.setFlag(nFlag);
            equip.setKarmaCount((byte)0);
            equip.addStr((short)100);
            equip.addDex((short)100);
            equip.addInt((short)100);
            equip.addLuk((short)100);
            equip.addWatk((short)50);
            equip.addMatk((short)50);
            equip.addHp((short)3000);
            equip.addMp((short)3000);
            equip.setState((byte)20);
            equip.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            if (type == 0) {
               equip.setPotential1(40086);
               equip.setPotential2(40086);
               equip.setPotential3(40086);
               equip.setPotential4(42086);
               equip.setPotential5(42086);
               equip.setPotential6(42086);
            } else if (type == 1) {
               equip.setPotential1(40045);
               equip.setPotential2(40045);
               equip.setPotential3(40045);
               equip.setPotential4(42045);
               equip.setPotential5(42045);
               equip.setPotential6(42045);
            } else if (type == 2) {
               equip.setPotential1(40051);
               equip.setPotential2(40051);
               equip.setPotential3(40051);
               equip.setPotential4(42051);
               equip.setPotential5(42051);
               equip.setPotential6(42051);
            } else if (type == 3) {
               equip.setPotential1(40070);
               equip.setPotential2(40070);
               equip.setPotential3(40070);
               equip.setPotential4(40070);
               equip.setPotential5(40070);
               equip.setPotential6(40070);
            } else if (type == 4) {
               equip.setPotential1(40070);
               equip.setPotential2(40070);
               equip.setPotential3(40070);
               equip.setPotential4(40070);
               equip.setPotential5(40070);
               equip.setPotential6(40070);
            }

            equip.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
            MapleInventoryManipulator.addFromDrop(this.c, equip, false);
         }
      }
   }

   public void exchangePetPeriod(int itemID, int period) {
      Item item = new Item(itemID, (short)1, (short)1, 0);
      item.setExpiration(System.currentTimeMillis() + 86400000L * period);
      MaplePet pet = MaplePet.createPet(itemID, MapleInventoryIdentifier.getInstance());
      item.setPet(pet);
      item.setUniqueId(pet.getUniqueId());
      MapleInventoryManipulator.addFromDrop(this.getClient(), item, false);
   }

   public void exchangeEquipPeriod(int itemID, int period) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.setExpiration(System.currentTimeMillis() + 86400000L * period);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void exchangePeriod(int itemID, int period) {
      Item item = new Item(itemID, (short)1, (short)1, 0);
      item.setExpiration(System.currentTimeMillis() + 86400000L * period);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public int exchange(int... values) {
      if (values.length % 2 > 0) {
         return -2;
      } else {
         HashMap<MapleInventoryType, Integer> slots = new HashMap<>();

         for (int i = 0; i < values.length; i++) {
            if (i % 2 == 0) {
               if (values[i + 1] > 0) {
                  MapleInventoryType type = GameConstants.getInventoryType(values[i]);
                  slots.putIfAbsent(type, 0);
                  slots.put(type, slots.get(type) + MapleInventoryManipulator.getNeedNumSlots(this.getClient(), values[i], values[i + 1]));
               } else if (this.getPlayer().getItemQuantity(values[i], false) < -values[i + 1]) {
                  return -1;
               }
            }
         }

         for (MapleInventoryType type : slots.keySet()) {
            if (this.getPlayer().getInventory(type).isFull(slots.get(type) - 1)) {
               return -1;
            }
         }

         for (int ix = 0; ix < values.length; ix++) {
            if (ix % 2 == 0) {
               this.gainItem(values[ix], (short)values[ix + 1]);
            }
         }

         return 1;
      }
   }

   public final void gainItem(int id, short quantity) {
      this.gainItem(id, quantity, false, 0L, -1, "");
   }

   public final void gainItemSilent(int id, short quantity) {
      this.gainItem(id, quantity, false, 0L, -1, "", this.getClient(), false);
   }

   public final void gainItem(int id, short quantity, boolean randomStats) {
      this.gainItem(id, quantity, randomStats, 0L, -1, "");
   }

   public final void gainItem(int id, short quantity, boolean randomStats, int slots) {
      this.gainItem(id, quantity, randomStats, 0L, slots, "");
   }

   public final void gainItem(int id, short quantity, long period) {
      this.gainItem(id, quantity, false, period, -1, "");
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots) {
      this.gainItem(id, quantity, randomStats, period, slots, "");
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner) {
      this.gainItem(id, quantity, randomStats, period, slots, owner, this.getClient());
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, MapleClient cg) {
      this.gainItem(id, quantity, randomStats, period, slots, owner, cg, true);
   }

   public final void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, MapleClient cg, boolean show) {
      if (quantity >= 0) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         MapleInventoryType type = GameConstants.getInventoryType(id);
         if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
            return;
         }

         if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
            Equip item = (Equip)(randomStats ? ii.randomizeStats((Equip)ii.getEquipById(id)) : ii.getEquipById(id));
            if (period > 0L) {
               item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }

            if (slots > 0) {
               item.setUpgradeSlots((byte)(item.getUpgradeSlots() + slots));
            }

            if (owner != null) {
               item.setOwner(owner);
            }

            String name = ii.getName(id);
            if (id / 10000 == 114 && name != null && name.length() > 0) {
               String msg = "<" + name + "> 훈장을 획득하셨습니다!";
               cg.getPlayer().dropMessage(-1, msg);
               cg.getPlayer().dropMessage(5, msg);
            }

            MapleInventoryManipulator.addbyItem(cg, item.copy());
            if (quantity > 1) {
               for (int i = 0; i < quantity - 1; i++) {
                  MapleInventoryManipulator.addbyItem(cg, item.copy());
               }
            }
         } else {
            MapleInventoryManipulator.addById(
               cg, id, quantity, owner == null ? "" : owner, null, period, FileoutputUtil.CurrentReadable_Time() + "]로 부터 획득한 아이템."
            );
         }

         StringBuilder sb = new StringBuilder();
         sb.append("아이템 생성 로그 : ");
         sb.append(this.getPlayer().getName());
         sb.append(" | 아이템 : ");
         sb.append(id);
         sb.append(" ");
         sb.append((int)quantity);
         sb.append("개");
         sb.append(" | ");
      } else {
         MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
      }

      if (show) {
         cg.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, quantity, true));
      }
   }

   public final void delay(int time) {
      this.getPlayer().send(CField.UIPacket.getDirectionInfo(1, time));
   }

   public void setIngameDirectionMode(boolean blackFrame, boolean forceMouseOver, boolean showUI) {
      this.getPlayer().send(CField.UIPacket.setIngameDirectionMode(blackFrame, forceMouseOver, showUI));
   }

   public void blind(int blindOnOff, int bitMapNumber, int usR, int aniInfo, int spineSkeletonAnimation, int delay, int layer) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      packet.write(23);
      packet.write(blindOnOff);
      packet.writeShort(bitMapNumber);
      packet.writeShort(usR);
      packet.writeShort(aniInfo);
      packet.writeShort(spineSkeletonAnimation);
      packet.writeInt(delay);
      packet.writeInt(layer);
      this.getPlayer().send(packet.getPacket());
   }

   public void effectSound(String path) {
      this.getPlayer().send(CField.playSE(path));
   }

   public void spineEffect(String path, String animation, String unk1, int onoff, int loop, int postRender, int endDelay) {
      this.getPlayer().send(CField.EffectPacket.spineEffect(path, animation, onoff, loop, postRender, endDelay, "", unk1, 0, 0, 0));
   }

   public void environmentChange(int mode, String env, int option) {
      this.getPlayer().send(CField.environmentChange(env, mode, option));
   }

   public void getOnOffFade(int term, String key, int unk) {
      this.getPlayer().send(CField.getOnOffFade(term, key, unk));
   }

   public void getOnOff(int term, String key, String path, int rx, int ry, int rz, int org, int postRender, int unk1, int unk2) {
      this.getPlayer().send(CField.getOnOff(term, key, path, rx, ry, rz, org, postRender, unk1, unk2));
   }
}
