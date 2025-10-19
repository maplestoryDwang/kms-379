package objects.item;

import constants.GameConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.loader.CharacterSaveFlag2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import logging.LoggingManager;
import logging.entry.DropLog;
import logging.entry.DropLogType;
import network.models.CField;
import network.models.CSPacket;
import network.models.CWvsContext;
import network.shop.CashItemFactory;
import network.shop.CashItemInfo;
import objects.androids.Android;
import objects.fields.gameobject.AffectedArea;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleTrait;
import objects.users.PlayerStats;
import objects.users.enchant.EquipSpecialAttribute;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemStateFlag;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.CurrentTime;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.StringUtil;

public class MapleInventoryManipulator {
   public static int[] bmWeapons = new int[]{
      1212128,
      1213021,
      1222121,
      1232121,
      1242138,
      1242140,
      1262050,
      1272039,
      1282039,
      1292021,
      1302354,
      1312212,
      1322263,
      1332288,
      1362148,
      1372236,
      1382273,
      1402267,
      1412188,
      1422196,
      1432226,
      1442284,
      1452265,
      1462251,
      1472274,
      1482231,
      1492244,
      1522151,
      1532156,
      1582043,
      1592021
   };

   public static void addRing(MapleCharacter chr, int itemId, long ringId, int sn, String partner) {
      CashItemInfo csi = CashItemFactory.getInstance().getItem(sn, false);
      if (csi != null) {
         Item ring = chr.getCashInventory().toItem(csi, ringId);
         if (ring != null && ring.getUniqueId() == ringId && ring.getUniqueId() > 0L && ring.getItemId() == itemId) {
            chr.getCashInventory().addToInventory(ring);
            chr.getClient()
               .getSession()
               .writeAndFlush(CSPacket.sendBoughtRings(GameConstants.isCrushRing(itemId), ring, sn, chr.getClient().getAccID(), partner));
         }
      }
   }

   public static boolean addbyItem(MapleClient c, Item item) {
      return addbyItem(c, item, false) >= 0;
   }

   public static short addbyItem(MapleClient c, Item item, boolean fromcs) {
      MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (type == MapleInventoryType.EQUIP && ii.isCash(item.getItemId())) {
         type = MapleInventoryType.CASH_EQUIP;
         if (item.getUniqueId() <= 0L) {
            item.setUniqueId(MapleInventoryIdentifier.getInstance());
         }
      }

      if (item.getItemId() / 1000000 == 5 && !GameConstants.isPet(item.getItemId())) {
         item.setUniqueId(MapleInventoryIdentifier.getInstance());
      }

      if (GameConstants.isFairyPendant(item.getItemId())) {
         item.setUniqueId(MapleInventoryIdentifier.getInstance());
      }

      if (item.getInventoryId() <= 0L) {
         item.setTempUniqueID(System.currentTimeMillis());
      }

      if (item.getQuantity() < 0 && c.getPlayer().getChangeEmotionTime() != 0L && System.currentTimeMillis() - c.getPlayer().getChangeEmotionTime() <= 2000L) {
         c.getPlayer().ban("아이템 복사 시도로 인한 영구 정지", true, true, true);
         return -1;
      } else {
         if (GameConstants.isNeedToWriteLogItem(item.getItemId())) {
            FileoutputUtil.log(
               "./TextLog/GiveItemLog.txt",
               "addByItem 아이템 지급 : (itemId : "
                  + item.getItemId()
                  + ", quantity : "
                  + item.getQuantity()
                  + ") 유저 닉네임 : "
                  + c.getPlayer().getName()
                  + ", 계정 이름 : "
                  + c.getAccountName()
                  + ", Script : "
                  + c.getLastUsedScriptName()
                  + "\r\n NewScript : "
                  + c.getLastUsedNewScriptName()
                  + "\r\n"
            );
         }

         short newSlot = c.getPlayer().getInventory(type).addItem(item);
         if (newSlot == -1) {
            if (!fromcs) {
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
            }

            return newSlot;
         } else {
            if (GameConstants.isHarvesting(item.getItemId())) {
               c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }

            if (GameConstants.isArcaneSymbol(item.getItemId())) {
               Equip equip = (Equip)item;
               if (equip.getArcLevel() < 1) {
                  equip.setArc(30);
                  equip.setArcLevel(1);
                  equip.setArcEXP(1);
                  if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
                     && c.getPlayer().getJob() != 512
                     && c.getPlayer().getJob() != 1512
                     && c.getPlayer().getJob() != 2512
                     && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
                     && !GameConstants.isAran(c.getPlayer().getJob())
                     && !GameConstants.isBlaster(c.getPlayer().getJob())
                     && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
                     && !GameConstants.isMichael(c.getPlayer().getJob())
                     && !GameConstants.isKaiser(c.getPlayer().getJob())
                     && !GameConstants.isZero(c.getPlayer().getJob())
                     && !GameConstants.isArk(c.getPlayer().getJob())
                     && !GameConstants.isAdele(c.getPlayer().getJob())) {
                     if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
                        && !GameConstants.isFlameWizard(c.getPlayer().getJob())
                        && !GameConstants.isEvan(c.getPlayer().getJob())
                        && !GameConstants.isLuminous(c.getPlayer().getJob())
                        && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
                        && !GameConstants.isKinesis(c.getPlayer().getJob())
                        && !GameConstants.isIllium(c.getPlayer().getJob())
                        && !GameConstants.isLara(c.getPlayer().getJob())) {
                        if (!GameConstants.isKain(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                           && c.getPlayer().getJob() != 522
                           && c.getPlayer().getJob() != 532
                           && !GameConstants.isMechanic(c.getPlayer().getJob())
                           && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                           && !GameConstants.isMercedes(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                           if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                              && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                              && !GameConstants.isPhantom(c.getPlayer().getJob())
                              && !GameConstants.isKadena(c.getPlayer().getJob())
                              && !GameConstants.isHoyoung(c.getPlayer().getJob())) {
                              if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                                 equip.setHp((short)630);
                              } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                                 equip.setStr((short)144);
                                 equip.setDex((short)144);
                                 equip.setLuk((short)144);
                              }
                           } else {
                              equip.setLuk((short)300);
                           }
                        } else {
                           equip.setDex((short)300);
                        }
                     } else {
                        equip.setInt((short)300);
                     }
                  } else {
                     equip.setStr((short)300);
                  }
               }
            }

            if (GameConstants.isAuthenticSymbol(item.getItemId())) {
               Equip equip = (Equip)item;
               if (equip.getArcLevel() < 1) {
                  equip.setArc(10);
                  equip.setArcLevel(1);
                  equip.setArcEXP(1);
                  if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
                     && c.getPlayer().getJob() != 512
                     && c.getPlayer().getJob() != 1512
                     && c.getPlayer().getJob() != 2512
                     && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
                     && !GameConstants.isAran(c.getPlayer().getJob())
                     && !GameConstants.isBlaster(c.getPlayer().getJob())
                     && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
                     && !GameConstants.isMichael(c.getPlayer().getJob())
                     && !GameConstants.isKaiser(c.getPlayer().getJob())
                     && !GameConstants.isZero(c.getPlayer().getJob())
                     && !GameConstants.isArk(c.getPlayer().getJob())
                     && !GameConstants.isAdele(c.getPlayer().getJob())) {
                     if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
                        && !GameConstants.isFlameWizard(c.getPlayer().getJob())
                        && !GameConstants.isEvan(c.getPlayer().getJob())
                        && !GameConstants.isLuminous(c.getPlayer().getJob())
                        && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
                        && !GameConstants.isKinesis(c.getPlayer().getJob())
                        && !GameConstants.isIllium(c.getPlayer().getJob())
                        && !GameConstants.isLara(c.getPlayer().getJob())) {
                        if (!GameConstants.isKain(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                           && c.getPlayer().getJob() != 522
                           && c.getPlayer().getJob() != 532
                           && !GameConstants.isMechanic(c.getPlayer().getJob())
                           && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                           && !GameConstants.isMercedes(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                           if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                              && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                              && !GameConstants.isPhantom(c.getPlayer().getJob())
                              && !GameConstants.isKadena(c.getPlayer().getJob())
                              && !GameConstants.isHoyoung(c.getPlayer().getJob())) {
                              if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                                 equip.setHp((short)1050);
                              } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                                 equip.setStr((short)240);
                                 equip.setDex((short)240);
                                 equip.setLuk((short)240);
                              }
                           } else {
                              equip.setLuk((short)500);
                           }
                        } else {
                           equip.setDex((short)500);
                        }
                     } else {
                        equip.setInt((short)500);
                     }
                  } else {
                     equip.setStr((short)500);
                  }
               }
            }

            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, item));
            c.getPlayer().havePartyQuest(item.getItemId());
            return newSlot;
         }
      }
   }

   public static long getUniqueId(int itemId, MaplePet pet) {
      long uniqueid = -1L;
      if (GameConstants.isPet(itemId)) {
         if (pet != null) {
            uniqueid = pet.getUniqueId();
         } else {
            uniqueid = MapleInventoryIdentifier.getInstance();
         }
      } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) {
         uniqueid = MapleInventoryIdentifier.getInstance();
      }

      return uniqueid;
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String gmLog) {
      return addById(c, itemId, quantity, null, null, 0L, gmLog);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
      return addById(c, itemId, quantity, owner, null, 0L, gmLog);
   }

   public static byte addId(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
      return addId(c, itemId, quantity, owner, null, 0L, gmLog);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, String gmLog) {
      return addById(c, itemId, quantity, owner, pet, 0L, gmLog);
   }

   public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
      return addId(c, itemId, quantity, owner, pet, period, gmLog) >= 0;
   }

   public static byte addId(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if ((!ii.isPickupRestricted(itemId) || !c.getPlayer().haveItem(itemId, 1, true, false)) && ii.itemExists(itemId)) {
         if (quantity >= 1000) {
         }

         if (quantity < 0 && c.getPlayer().getChangeEmotionTime() != 0L && System.currentTimeMillis() - c.getPlayer().getChangeEmotionTime() <= 2000L) {
            c.getPlayer().ban("아이템 복사 시도로 인한 영구 정지", true, true, true);
            return -1;
         } else {
            if (GameConstants.isNeedToWriteLogItem(itemId)) {
               FileoutputUtil.log(
                  "./TextLog/GiveItemLog.txt",
                  "addId 아이템 지급 : (itemId : "
                     + itemId
                     + ", quantity : "
                     + quantity
                     + ") 유저 닉네임 : "
                     + c.getPlayer().getName()
                     + ", 계정 이름 : "
                     + c.getAccountName()
                     + ", Script : "
                     + c.getLastUsedScriptName()
                     + "\r\n NewScript : "
                     + c.getLastUsedNewScriptName()
                     + "\r\n"
               );
            }

            MapleInventoryType type = GameConstants.getInventoryType(itemId);
            if (type == MapleInventoryType.EQUIP && ii.isCash(itemId)) {
               type = MapleInventoryType.CASH_EQUIP;
            }

            long uniqueid = getUniqueId(itemId, pet);
            short newSlot = -1;
            if (!type.equals(MapleInventoryType.EQUIP) && !type.equals(MapleInventoryType.CASH_EQUIP)) {
               short slotMax = ii.getSlotMax(itemId);
               List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
               if (GameConstants.isRechargable(itemId)) {
                  Item nItem = new Item(itemId, (short)0, quantity, 0, uniqueid);
                  newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                  if (newSlot == -1) {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                     return -1;
                  }

                  if (period > 0L) {
                     nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                  }

                  if (gmLog != null) {
                     nItem.setGMLog(gmLog);
                  }

                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nItem));
               } else {
                  if (existing.size() > 0) {
                     Iterator<Item> i = existing.iterator();

                     while (quantity > 0 && i.hasNext()) {
                        Item eItem = i.next();
                        short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1L) {
                           short newQ = (short)Math.min(oldQ + quantity, slotMax);
                           quantity = (short)(quantity - (newQ - oldQ));
                           eItem.setQuantity(newQ);
                           c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventorySlot(type, eItem, false));
                           newSlot = eItem.getPosition();
                        }
                     }
                  }

                  while (quantity > 0) {
                     short newQ = (short)Math.min((int)quantity, (int)slotMax);
                     if (newQ == 0) {
                        c.getPlayer().havePartyQuest(itemId);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return (byte)newSlot;
                     }

                     quantity -= newQ;
                     Item nItemx = new Item(itemId, (short)0, newQ, 0, uniqueid);
                     newSlot = c.getPlayer().getInventory(type).addItem(nItemx);
                     if (newSlot == -1) {
                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                        return -1;
                     }

                     if (gmLog != null) {
                        nItemx.setGMLog(gmLog);
                     }

                     if (owner != null) {
                        nItemx.setOwner(owner);
                     }

                     if (period > 0L) {
                        nItemx.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                     }

                     if (pet != null) {
                        nItemx.setPet(pet);
                        pet.setInventoryPosition(newSlot);
                     }

                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nItemx));
                     if (GameConstants.isRechargable(itemId) && quantity == 0) {
                        break;
                     }
                  }
               }
            } else {
               if (quantity != 1) {
                  throw new InventoryException("Trying to create equip with non-one quantity");
               }

               Item nEquip = ii.getEquipById(itemId, uniqueid);
               if (owner != null) {
                  nEquip.setOwner(owner);
               }

               if (gmLog != null) {
                  nEquip.setGMLog(gmLog);
               }

               if (period > 0L) {
                  nEquip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
               }

               if (type == MapleInventoryType.CASH_EQUIP && nEquip.getUniqueId() <= 0L) {
                  nEquip.setUniqueId(MapleInventoryIdentifier.getInstance());
               }

               newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
               if (newSlot == -1) {
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                  return -1;
               }

               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nEquip));
               if (GameConstants.isHarvesting(itemId)) {
                  c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
               }
            }

            c.getPlayer().havePartyQuest(itemId);
            return (byte)newSlot;
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.showItemUnavailable());
         return -1;
      }
   }

   public static Item addbyId_Gachapon(MapleClient c, int itemId, short quantity) {
      if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() != -1
         && c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() != -1
         && c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() != -1
         && c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() != -1) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         if ((!ii.isPickupRestricted(itemId) || !c.getPlayer().haveItem(itemId, 1, true, false)) && ii.itemExists(itemId)) {
            if (quantity < 0 && c.getPlayer().getChangeEmotionTime() != 0L && System.currentTimeMillis() - c.getPlayer().getChangeEmotionTime() <= 2000L) {
               c.getPlayer().ban("아이템 복사 시도로 인한 영구 정지", true, true, true);
               return null;
            } else {
               MapleInventoryType type = GameConstants.getInventoryType(itemId);
               if (!type.equals(MapleInventoryType.EQUIP) && !type.equals(MapleInventoryType.CASH_EQUIP)) {
                  short slotMax = ii.getSlotMax(itemId);
                  List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
                  if (GameConstants.isRechargable(itemId)) {
                     Item nItem = new Item(itemId, (short)0, quantity, 0);
                     short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                     if (newSlot == -1) {
                        return null;
                     } else {
                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nItem));
                        c.getPlayer().havePartyQuest(nItem.getItemId());
                        return nItem;
                     }
                  } else {
                     Item nItem = null;
                     boolean recieved = false;
                     if (existing.size() > 0) {
                        Iterator<Item> i = existing.iterator();

                        while (quantity > 0 && i.hasNext()) {
                           nItem = i.next();
                           short oldQ = nItem.getQuantity();
                           if (oldQ < slotMax) {
                              recieved = true;
                              short newQ = (short)Math.min(oldQ + quantity, slotMax);
                              quantity = (short)(quantity - (newQ - oldQ));
                              nItem.setQuantity(newQ);
                              c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventorySlot(type, nItem, false));
                           }
                        }
                     }

                     while (quantity > 0) {
                        short newQ = (short)Math.min((int)quantity, (int)slotMax);
                        if (newQ == 0) {
                           break;
                        }

                        quantity -= newQ;
                        nItem = new Item(itemId, (short)0, newQ, 0);
                        short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1 && recieved) {
                           return nItem;
                        }

                        if (newSlot == -1) {
                           return null;
                        }

                        recieved = true;
                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                           break;
                        }
                     }

                     if (recieved) {
                        c.getPlayer().havePartyQuest(nItem.getItemId());
                        return nItem;
                     } else {
                        return null;
                     }
                  }
               } else if (quantity == 1) {
                  Item item = ii.randomizeStats((Equip)ii.getEquipById(itemId));
                  short newSlotx = c.getPlayer().getInventory(type).addItem(item);
                  if (newSlotx == -1) {
                     return null;
                  } else {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, item, true));
                     c.getPlayer().havePartyQuest(item.getItemId());
                     return item;
                  }
               } else {
                  throw new InventoryException("Trying to create equip with non-one quantity");
               }
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.showItemUnavailable());
            return null;
         }
      } else {
         return null;
      }
   }

   public static short addFromAuction(MapleClient c, Item item) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      short quantity = item.getQuantity();
      MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
      if (type == MapleInventoryType.EQUIP && ii.isCash(item.getItemId())) {
         type = MapleInventoryType.CASH_EQUIP;
      }

      if (!type.equals(MapleInventoryType.EQUIP) && !type.equals(MapleInventoryType.CASH_EQUIP)) {
         short slotMax = ii.getSlotMax(item.getItemId());
         List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
         if (GameConstants.isPet(item.getItemId())) {
            Item nItem = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
            nItem.setExpiration(item.getExpiration());
            nItem.setOwner(item.getOwner());
            nItem.setPet(item.getPet());
            nItem.setGMLog(item.getGMLog());
            nItem.setOnceTrade(item.getOnceTrade());
            nItem.setFlag(item.getFlag());
            nItem.setUniqueId(item.getUniqueId());
            short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
            return newSlot == -1 ? -1 : newSlot;
         } else if (!GameConstants.isRechargable(item.getItemId())) {
            if (quantity <= 0) {
               return -1;
            } else {
               if (existing.size() > 0) {
                  Iterator<Item> i = existing.iterator();

                  while (quantity > 0 && i.hasNext()) {
                     Item eItem = i.next();
                     short oldQ = eItem.getQuantity();
                     if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity = (short)(quantity - (newQ - oldQ));
                        eItem.setQuantity(newQ);
                     }
                  }
               }

               while (quantity > 0) {
                  short newQ = (short)Math.min((int)quantity, (int)slotMax);
                  quantity -= newQ;
                  Item nItem = new Item(item.getItemId(), (short)0, newQ, item.getFlag());
                  nItem.setExpiration(item.getExpiration());
                  nItem.setOwner(item.getOwner());
                  nItem.setPet(item.getPet());
                  nItem.setGMLog(item.getGMLog());
                  nItem.setOnceTrade(item.getOnceTrade());
                  nItem.setFlag(item.getFlag());
                  nItem.setUniqueId(item.getUniqueId());
                  short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                  if (newSlot == -1) {
                     item.setQuantity((short)(quantity + newQ));
                     return -1;
                  }
               }

               return 1;
            }
         } else {
            Item nItem = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
            nItem.setExpiration(item.getExpiration());
            nItem.setOwner(item.getOwner());
            nItem.setPet(item.getPet());
            nItem.setGMLog(item.getGMLog());
            nItem.setOnceTrade(item.getOnceTrade());
            nItem.setFlag(item.getFlag());
            nItem.setUniqueId(item.getUniqueId());
            short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
            return newSlot == -1 ? -1 : newSlot;
         }
      } else if (quantity == 1) {
         short newSlot = c.getPlayer().getInventory(type).addItem(item);
         return newSlot == -1 ? -1 : newSlot;
      } else {
         return -1;
      }
   }

   public static boolean addFromDrop(MapleClient c, Item item, boolean show) {
      return addFromDrop(c, item, show, false, false);
   }

   public static boolean addFromDrop(MapleClient c, Item item, boolean show, boolean enhance, boolean exclusive) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (c.getPlayer() == null
         || ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)
         || !ii.itemExists(item.getItemId())) {
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.showItemUnavailable());
         return false;
      } else if (item.getQuantity() < 0
         && c.getPlayer().getChangeEmotionTime() != 0L
         && System.currentTimeMillis() - c.getPlayer().getChangeEmotionTime() <= 2000L) {
         c.getPlayer().ban("아이템 복사 시도로 인한 영구 정지", true, true, true);
         return false;
      } else {
         if (GameConstants.isNeedToWriteLogItem(item.getItemId())) {
            FileoutputUtil.log(
               "./TextLog/GiveItemLog.txt",
               "addFromDrop 아이템 지급 : (itemId : "
                  + item.getItemId()
                  + ", quantity : "
                  + item.getQuantity()
                  + ") 유저 닉네임 : "
                  + c.getPlayer().getName()
                  + ", 계정 이름 : "
                  + c.getAccountName()
                  + ", Script : "
                  + c.getLastUsedScriptName()
                  + "\r\n NewScript : "
                  + c.getLastUsedNewScriptName()
                  + "\r\n"
            );
         }

         int before = c.getPlayer().itemQuantity(item.getItemId());
         short quantity = item.getQuantity();
         MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
         if (type == MapleInventoryType.EQUIP && ii.isCash(item.getItemId())) {
            type = MapleInventoryType.CASH_EQUIP;
         }

         if (!type.equals(MapleInventoryType.EQUIP) && !type.equals(MapleInventoryType.CASH_EQUIP)) {
            if (GameConstants.isArcaneSymbol(item.getItemId())) {
               Equip equip = (Equip)item;
               if (equip.getArcLevel() < 1) {
                  equip.setArc(30);
                  equip.setArcLevel(1);
                  equip.setArcEXP(1);
                  if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
                     && c.getPlayer().getJob() != 512
                     && c.getPlayer().getJob() != 1512
                     && c.getPlayer().getJob() != 2512
                     && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
                     && !GameConstants.isAran(c.getPlayer().getJob())
                     && !GameConstants.isBlaster(c.getPlayer().getJob())
                     && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
                     && !GameConstants.isMichael(c.getPlayer().getJob())
                     && !GameConstants.isKaiser(c.getPlayer().getJob())
                     && !GameConstants.isZero(c.getPlayer().getJob())
                     && !GameConstants.isArk(c.getPlayer().getJob())
                     && !GameConstants.isAdele(c.getPlayer().getJob())) {
                     if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
                        && !GameConstants.isFlameWizard(c.getPlayer().getJob())
                        && !GameConstants.isEvan(c.getPlayer().getJob())
                        && !GameConstants.isLuminous(c.getPlayer().getJob())
                        && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
                        && !GameConstants.isKinesis(c.getPlayer().getJob())
                        && !GameConstants.isIllium(c.getPlayer().getJob())
                        && !GameConstants.isLara(c.getPlayer().getJob())) {
                        if (!GameConstants.isKain(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                           && c.getPlayer().getJob() != 522
                           && c.getPlayer().getJob() != 532
                           && !GameConstants.isMechanic(c.getPlayer().getJob())
                           && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                           && !GameConstants.isMercedes(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                           if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                              && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                              && !GameConstants.isPhantom(c.getPlayer().getJob())
                              && !GameConstants.isKadena(c.getPlayer().getJob())
                              && !GameConstants.isHoyoung(c.getPlayer().getJob())) {
                              if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                                 equip.setHp((short)630);
                              } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                                 equip.setStr((short)144);
                                 equip.setDex((short)144);
                                 equip.setLuk((short)144);
                              }
                           } else {
                              equip.setLuk((short)300);
                           }
                        } else {
                           equip.setDex((short)300);
                        }
                     } else {
                        equip.setInt((short)300);
                     }
                  } else {
                     equip.setStr((short)300);
                  }
               }
            }

            if (GameConstants.isAuthenticSymbol(item.getItemId())) {
               Equip equip = (Equip)item;
               if (equip.getArcLevel() < 1) {
                  equip.setArc(10);
                  equip.setArcLevel(1);
                  equip.setArcEXP(1);
                  if ((c.getPlayer().getJob() < 100 || c.getPlayer().getJob() >= 200)
                     && c.getPlayer().getJob() != 512
                     && c.getPlayer().getJob() != 1512
                     && c.getPlayer().getJob() != 2512
                     && (c.getPlayer().getJob() < 1100 || c.getPlayer().getJob() >= 1200)
                     && !GameConstants.isAran(c.getPlayer().getJob())
                     && !GameConstants.isBlaster(c.getPlayer().getJob())
                     && !GameConstants.isDemonSlayer(c.getPlayer().getJob())
                     && !GameConstants.isMichael(c.getPlayer().getJob())
                     && !GameConstants.isKaiser(c.getPlayer().getJob())
                     && !GameConstants.isZero(c.getPlayer().getJob())
                     && !GameConstants.isArk(c.getPlayer().getJob())
                     && !GameConstants.isAdele(c.getPlayer().getJob())) {
                     if ((c.getPlayer().getJob() < 200 || c.getPlayer().getJob() >= 300)
                        && !GameConstants.isFlameWizard(c.getPlayer().getJob())
                        && !GameConstants.isEvan(c.getPlayer().getJob())
                        && !GameConstants.isLuminous(c.getPlayer().getJob())
                        && (c.getPlayer().getJob() < 3200 || c.getPlayer().getJob() >= 3300)
                        && !GameConstants.isKinesis(c.getPlayer().getJob())
                        && !GameConstants.isIllium(c.getPlayer().getJob())
                        && !GameConstants.isLara(c.getPlayer().getJob())) {
                        if (!GameConstants.isKain(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 300 || c.getPlayer().getJob() >= 400)
                           && c.getPlayer().getJob() != 522
                           && c.getPlayer().getJob() != 532
                           && !GameConstants.isMechanic(c.getPlayer().getJob())
                           && !GameConstants.isAngelicBuster(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 1300 || c.getPlayer().getJob() >= 1400)
                           && !GameConstants.isMercedes(c.getPlayer().getJob())
                           && (c.getPlayer().getJob() < 3300 || c.getPlayer().getJob() >= 3400)) {
                           if ((c.getPlayer().getJob() < 400 || c.getPlayer().getJob() >= 500)
                              && (c.getPlayer().getJob() < 1400 || c.getPlayer().getJob() >= 1500)
                              && !GameConstants.isPhantom(c.getPlayer().getJob())
                              && !GameConstants.isKadena(c.getPlayer().getJob())
                              && !GameConstants.isHoyoung(c.getPlayer().getJob())) {
                              if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {
                                 equip.setHp((short)1050);
                              } else if (GameConstants.isXenon(c.getPlayer().getJob())) {
                                 equip.setStr((short)240);
                                 equip.setDex((short)240);
                                 equip.setLuk((short)240);
                              }
                           } else {
                              equip.setLuk((short)500);
                           }
                        } else {
                           equip.setDex((short)500);
                        }
                     } else {
                        equip.setInt((short)500);
                     }
                  } else {
                     equip.setStr((short)500);
                  }
               }
            }

            if (item.getItemId() == 2430783 && !DBConfig.isGanglim) {
               item.setExpiration(System.currentTimeMillis() + -1702967296L);
            }

            short slotMax = ii.getSlotMax(item.getItemId());
            List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
               if (quantity <= 0) {
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.showItemUnavailable());
                  return false;
               }

               if (existing.size() > 0) {
                  Iterator<Item> i = existing.iterator();

                  while (quantity > 0 && i.hasNext()) {
                     Item eItem = i.next();
                     short oldQ = eItem.getQuantity();
                     if (oldQ < slotMax
                        && item.getOwner().equals(eItem.getOwner())
                        && item.getExpiration() == eItem.getExpiration()
                        && !GameConstants.isPet(item.getItemId())
                        && (item.getUniqueId() == eItem.getUniqueId() || item.getUniqueId() > 0L && eItem.getUniqueId() > 0L)) {
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity = (short)(quantity - (newQ - oldQ));
                        eItem.setQuantity(newQ);
                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventorySlot(type, eItem, exclusive));
                     }
                  }
               }

               while (quantity > 0) {
                  short newQ = (short)Math.min((int)quantity, (int)slotMax);
                  quantity -= newQ;
                  Item nItem = new Item(item.getItemId(), (short)0, newQ, item.getFlag());
                  nItem.setExpiration(item.getExpiration());
                  nItem.setOwner(item.getOwner());
                  nItem.setPet(item.getPet());
                  nItem.setGMLog(item.getGMLog());
                  nItem.setOnceTrade(item.getOnceTrade());
                  nItem.setFlag(item.getFlag());
                  nItem.setUniqueId(item.getUniqueId());
                  if (GameConstants.isIntensePowerCrystal(item.getItemId())) {
                     nItem.setIntensePowerCrystal(item.getIntensePowerCrystal());
                     if (nItem.getIntensePowerCrystal() != null) {
                        c.getPlayer().getIntensePowerCrystals().put(nItem.getUniqueId(), nItem.getIntensePowerCrystal());
                        c.getPlayer().sendIntensePowerCrystalUpdate();
                        c.getPlayer().setSaveFlag2(c.getPlayer().getSaveFlag2() | CharacterSaveFlag2.INTENSE_POWER_CRYSTAL.getFlag());
                     }
                  }

                  short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                  if (newSlot == -1) {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                     item.setQuantity((short)(quantity + newQ));
                     return false;
                  }

                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nItem, exclusive, c.getPlayer()));
               }
            } else {
               Item nItemx = new Item(item.getItemId(), (short)0, quantity, item.getFlag());
               nItemx.setExpiration(item.getExpiration());
               nItemx.setOwner(item.getOwner());
               nItemx.setPet(item.getPet());
               nItemx.setGMLog(item.getGMLog());
               nItemx.setOnceTrade(item.getOnceTrade());
               nItemx.setFlag(item.getFlag());
               nItemx.setUniqueId(item.getUniqueId());
               short newSlot = c.getPlayer().getInventory(type).addItem(nItemx);
               if (newSlot == -1) {
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                  return false;
               }

               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, nItemx, exclusive, c.getPlayer()));
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), exclusive));
            }
         } else {
            if (quantity != 1) {
               throw new RuntimeException("Trying to create equip with non-one quantity");
            }

            if (enhance) {
               item = checkEnhanced(item, c.getPlayer());
            }

            short newSlot = c.getPlayer().getInventory(type).addItem(item);
            if (newSlot == -1) {
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
               return false;
            }

            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(type, item, exclusive, c.getPlayer()));
            if (GameConstants.isHarvesting(item.getItemId())) {
               c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
         }

         if (item.getQuantity() >= 50 && item.getItemId() == 2340000) {
            c.setMonitored(true);
         }

         if (DBConfig.isGanglim && item.getItemId() == 4310308) {
            int vv = c.getPlayer().getItemQuantity(4310308, false);
            c.getPlayer().updateOneInfo(QuestExConstants.NeoCoreEvent.getQuestID(), "point", String.valueOf(vv));
         }

         if (before == 0) {
            switch (item.getItemId()) {
               case 4001128:
                  c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                  break;
               case 4001246:
                  c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                  break;
               case 4001473:
                  c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
            }
         }

         c.getPlayer().havePartyQuest(item.getItemId());
         if (show) {
            c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(item.getItemId(), item.getQuantity()));
         }

         return true;
      }
   }

   private static Item checkEnhanced(Item before, MapleCharacter chr) {
      if (before instanceof Equip) {
         Equip eq = (Equip)before;
         if (eq.getState() == 0
            && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1)
            && GameConstants.canScroll(eq.getItemId())
            && Randomizer.nextInt(100) >= 80) {
            eq.resetPotential();
         }
      }

      return before;
   }

   public static boolean checkSpace(MapleClient c, int itemid, int quantity, String owner) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (c.getPlayer() == null || ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false) || !ii.itemExists(itemid)) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         return false;
      } else if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
         return false;
      } else {
         MapleInventoryType type = GameConstants.getInventoryType(itemid);
         if (type == MapleInventoryType.EQUIP && ii.isCash(itemid)) {
            type = MapleInventoryType.CASH_EQUIP;
         }

         if (c.getPlayer() != null && c.getPlayer().getInventory(type) != null) {
            int numSlotsNeeded;
            if (!type.equals(MapleInventoryType.EQUIP) && !type.equals(MapleInventoryType.CASH_EQUIP)) {
               short slotMax = ii.getSlotMax(itemid);
               List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
               if (!GameConstants.isRechargable(itemid) && existing.size() > 0) {
                  for (Item eItem : existing) {
                     short oldQ = eItem.getQuantity();
                     if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                        short newQ = (short)Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                     }

                     if (quantity <= 0) {
                        break;
                     }
                  }
               }

               if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
                  numSlotsNeeded = (int)Math.ceil((double)quantity / slotMax);
               } else {
                  numSlotsNeeded = 1;
               }
            } else {
               numSlotsNeeded = quantity;
            }

            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
         } else {
            return false;
         }
      }
   }

   public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop) {
      return removeFromSlot(c, type, slot, quantity, fromDrop, false);
   }

   public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
      return removeFromSlot(c, type, slot, quantity, fromDrop, consume, false);
   }

   public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume, boolean sort) {
      if (c.getPlayer() != null && c.getPlayer().getInventory(type) != null) {
         Item item = c.getPlayer().getInventory(type).getItem(slot);
         if (item == null) {
            return false;
         } else {
            boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (GameConstants.isHarvesting(item.getItemId())) {
               c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }

            if (GameConstants.isIntensePowerCrystal(item.getItemId())) {
               c.getPlayer().removeIntensePowerCrystal(item.getUniqueId());
               c.getPlayer().setSaveFlag2(c.getPlayer().getSaveFlag2() | CharacterSaveFlag2.INTENSE_POWER_CRYSTAL.getFlag());
            }

            if (!sort && !c.getPlayer().extendedSlots.isEmpty() && c.getPlayer().extendedSlots.containsKey(type.getType())) {
               List<Integer> s = c.getPlayer().extendedSlots.get(type.getType());
               if (s != null && !s.isEmpty()) {
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.removeInventoryBag(type, s.indexOf(item.getItemId())));
                  s.remove(Integer.valueOf(item.getItemId()));
               }
            }

            if (item.getQuantity() == 0 && !allowZero) {
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.clearInventoryItem(type, item.getPosition(), fromDrop));
            } else {
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventorySlot(type, item, fromDrop));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean removeById(MapleClient c, MapleInventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
      int remremove = quantity;
      if (c.getPlayer() != null && c.getPlayer().getInventory(type) != null) {
         for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            int theQ = item.getQuantity();
            if (remremove <= theQ && removeFromSlot(c, type, item.getPosition(), (short)remremove, fromDrop, consume)) {
               remremove = 0;
               break;
            }

            if (remremove > theQ && removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume)) {
               remremove -= theQ;
            }
         }

         return remremove <= 0;
      } else {
         return false;
      }
   }

   public static boolean removeFromSlot_Lock(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
      if (c.getPlayer() != null && c.getPlayer().getInventory(type) != null) {
         Item item = c.getPlayer().getInventory(type).getItem(slot);
         if (item == null) {
            return false;
         } else if (!ItemFlag.PROTECTED.check(item.getFlag()) && !ItemFlag.LOCK.check(item.getFlag())) {
            if (item instanceof Equip) {
               int flag = ((Equip)item).getItemState();
               if ((flag & ItemStateFlag.LOCK.getValue()) != 0) {
                  return false;
               }
            }

            return removeFromSlot(c, type, slot, quantity, fromDrop, consume);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean removeById_Lock(MapleClient c, MapleInventoryType type, int itemId, int quantity) {
      for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
         if (removeFromSlot_Lock(c, type, item.getPosition(), (short)quantity, false, false)) {
            return true;
         }
      }

      return false;
   }

   public static void move(MapleClient c, MapleInventoryType type, short src, short dst) {
      if (src >= 0 && dst >= 0 && src != dst && type != MapleInventoryType.EQUIPPED) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         Item source = c.getPlayer().getInventory(type).getItem(src);
         Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
         if (source != null) {
            short olddstQ = -1;
            if (initialTarget != null) {
               olddstQ = initialTarget.getQuantity();
            }

            short oldsrcQ = source.getQuantity();
            short slotMax = ii.getSlotMax(source.getItemId());
            c.getPlayer().getInventory(type).move(src, dst, slotMax);
            boolean isBag = src > 10000 || dst > 10000;
            boolean bothBag = src > 10000 && dst > 10000;
            boolean switchSrcDst = src > 10000;
            if (type == MapleInventoryType.EQUIP
               || type == MapleInventoryType.CASH
               || initialTarget == null
               || source.getItemId() != initialTarget.getItemId()
               || GameConstants.isThrowingStar(source.getItemId())
               || GameConstants.isBullet(source.getItemId())
               || !initialTarget.getOwner().equals(source.getOwner())
               || initialTarget.getExpiration() != source.getExpiration()) {
               c.getSession()
                  .writeAndFlush(CWvsContext.InventoryPacket.moveInventoryItem(type, switchSrcDst ? dst : src, switchSrcDst ? src : dst, isBag, bothBag));
            } else if (olddstQ + oldsrcQ > slotMax) {
               c.getSession()
                  .writeAndFlush(
                     CWvsContext.InventoryPacket.moveAndMergeWithRestInventoryItem(
                        type, src, dst, (short)(olddstQ + oldsrcQ - slotMax), slotMax, isBag, switchSrcDst, bothBag
                     )
                  );
            } else {
               c.getSession()
                  .writeAndFlush(
                     CWvsContext.InventoryPacket.moveAndMergeInventoryItem(
                        type, src, dst, c.getPlayer().getInventory(type).getItem(dst).getQuantity(), isBag, switchSrcDst, bothBag
                     )
                  );
            }
         }
      }
   }

   public static void equip(MapleInventoryType type, MapleClient c, short src, short dst) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         PlayerStats statst = c.getPlayer().getStat();
         Equip source = (Equip)chr.getInventory(type).getItem(src);
         Equip target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
         int count = 0;
         if (source != null && source.getCashEnchantCount() > 0) {
            for (Item item : chr.getInventory(MapleInventoryType.EQUIPPED).list()) {
               Equip e = (Equip)item;
               if (e.getCashEnchantCount() > 0) {
                  count++;
                  if (item.getItemId() / 10000 == 105) {
                     count++;
                  }
               }
            }
         }

         if (count >= 7) {
            c.getPlayer().dropMessage(1, "치장 옵션 강화가 적용된 아이템은 총 7개 이상 장착할 수 없습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else if (source != null && source.getItemId() == 1112917 && chr.getInventory(MapleInventoryType.EQUIPPED).findById(1112917) != null) {
            c.getPlayer().dropMessage(1, "해당 아이템은 중복해서 착용할 수 없습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else if (source != null && source.getItemId() == 1114401 && chr.getInventory(MapleInventoryType.EQUIPPED).findById(1114401) != null) {
            c.getPlayer().dropMessage(1, "해당 아이템은 중복해서 착용할 수 없습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            if (!DBConfig.isGanglim) {
               if (source != null && source.getItemId() == 1114402 && chr.getInventory(MapleInventoryType.EQUIPPED).findById(1114402) != null) {
                  c.getPlayer().dropMessage(1, "해당 아이템은 중복해서 착용할 수 없습니다.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (source != null && source.getItemId() == 1112921 && chr.getInventory(MapleInventoryType.EQUIPPED).findById(1112921) != null) {
                  c.getPlayer().dropMessage(1, "해당 아이템은 중복해서 착용할 수 없습니다.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }
            }

            chr.setZeroSlot(dst);
            if (source != null && source.getDurability() != 0 && !GameConstants.isHarvesting(source.getItemId())) {
               if (GameConstants.isGenesisWeapon(source.getItemId())) {
                  if (!DBConfig.isGanglim) {
                     MapleQuestStatus quest = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(1269));
                     if (quest != null) {
                        String cd = quest.getCustomData();
                        if (c.getPlayer().getQuestStatus(2000022) == 2) {
                           c.getPlayer().changeSkillLevel(80002632, 1, 1);
                        }
                     }

                     quest = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(1273));
                     if (quest != null) {
                        String cd = quest.getCustomData();
                        if (c.getPlayer().getQuestStatus(2000026) == 2) {
                           c.getPlayer().changeSkillLevel(80002633, 1, 1);
                        }
                     }
                  } else {
                     c.getPlayer().changeSkillLevel(80002632, 1, 1);
                     c.getPlayer().changeSkillLevel(80002633, 1, 1);
                  }
               }

               if (target != null && GameConstants.isGenesisWeapon(target.getItemId())) {
                  if (c.getPlayer().getTotalSkillLevel(80002633) > 0) {
                     c.getPlayer().changeSkillLevel(80002633, -1, 0);
                  }

                  if (c.getPlayer().getTotalSkillLevel(80002632) > 0) {
                     c.getPlayer().changeSkillLevel(80002632, -1, 0);
                  }
               }

               if (GameConstants.isContinousRing(source.getItemId())) {
                  chr.temporaryStatSet(SecondaryStatFlag.ContinousRingReady, 80003341, 120000, 1);
               }

               Map<String, Integer> stats = ii.getEquipStats(source.getItemId());
               if (!GameConstants.isEvanDragonItem(source.getItemId()) || chr.getJob() >= 2200 && chr.getJob() <= 2218) {
                  if ((dst <= -1200 || dst >= -999 && dst < -99)
                     && !stats.containsKey("cash")
                     && !GameConstants.isArcaneSymbol(source.getItemId())
                     && !GameConstants.isAuthenticSymbol(source.getItemId())
                     && !GameConstants.isFairyPendant(source.getItemId())) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  } else if (dst <= -1600
                     && c.getPlayer().getAndroid() == null
                     && !GameConstants.isArcaneSymbol(source.getItemId())
                     && !GameConstants.isAuthenticSymbol(source.getItemId())
                     && !GameConstants.isFairyPendant(source.getItemId())) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  } else {
                     Equip shield = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
                     if (shield != null
                        && GameConstants.isShield(shield.getItemId())
                        && !ii.isCash(source.getItemId())
                        && GameConstants.isWeapon(source.getItemId())
                        && !GameConstants.isOneHandWeapon(source.getItemId())
                        && !DBConfig.isGanglim) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     } else {
                        boolean arcane = false;
                        if (GameConstants.isArcaneSymbol(source.getItemId()) || GameConstants.isAuthenticSymbol(source.getItemId())) {
                           arcane = true;
                           Equip symbol1 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1600);
                           Equip symbol2 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1601);
                           Equip symbol3 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1602);
                           Equip symbol4 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1603);
                           Equip symbol5 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1604);
                           Equip symbol6 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1605);
                           Equip symbol7 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1700);
                           Equip symbol8 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1701);
                           Equip symbol9 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1702);
                           Equip symbol10 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1703);
                           Equip symbol11 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1704);
                           Equip symbol12 = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1705);
                           if (symbol1 != null
                              && symbol1.getItemId() == source.getItemId()
                              && (
                                 symbol1.getArcLevel() == 20
                                    || symbol1.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(symbol1.getArcLevel())
                                    || source.getArcLevel() == 20
                                    || source.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol2 != null
                              && symbol2.getItemId() == source.getItemId()
                              && (
                                 symbol2.getArcLevel() == 20
                                    || symbol2.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(symbol2.getArcLevel())
                                    || source.getArcLevel() == 20
                                    || source.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol3 != null
                              && symbol3.getItemId() == source.getItemId()
                              && (
                                 symbol3.getArcLevel() == 20
                                    || symbol3.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(symbol3.getArcLevel())
                                    || source.getArcLevel() == 20
                                    || source.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol4 != null
                              && symbol4.getItemId() == source.getItemId()
                              && (
                                 symbol4.getArcLevel() == 20
                                    || symbol4.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(symbol4.getArcLevel())
                                    || source.getArcLevel() == 20
                                    || source.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol5 != null
                              && symbol5.getItemId() == source.getItemId()
                              && (
                                 symbol5.getArcLevel() == 20
                                    || symbol5.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(symbol5.getArcLevel())
                                    || source.getArcLevel() == 20
                                    || source.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol6 != null
                              && symbol6.getItemId() == source.getItemId()
                              && (
                                 symbol6.getArcLevel() == 20
                                    || symbol6.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(symbol6.getArcLevel())
                                    || source.getArcLevel() == 20
                                    || source.getArcEXP() >= GameConstants.getExpNeededForArcaneSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol7 != null
                              && symbol7.getItemId() == source.getItemId()
                              && (
                                 symbol7.getArcLevel() == 11
                                    || symbol7.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(symbol7.getArcLevel())
                                    || source.getArcLevel() == 11
                                    || source.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol8 != null
                              && symbol8.getItemId() == source.getItemId()
                              && (
                                 symbol8.getArcLevel() == 11
                                    || symbol8.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(symbol8.getArcLevel())
                                    || source.getArcLevel() == 11
                                    || source.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol9 != null
                              && symbol9.getItemId() == source.getItemId()
                              && (
                                 symbol9.getArcLevel() == 11
                                    || symbol9.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(symbol9.getArcLevel())
                                    || source.getArcLevel() == 11
                                    || source.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol10 != null
                              && symbol10.getItemId() == source.getItemId()
                              && (
                                 symbol10.getArcLevel() == 11
                                    || symbol10.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(symbol10.getArcLevel())
                                    || source.getArcLevel() == 11
                                    || source.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol11 != null
                              && symbol11.getItemId() == source.getItemId()
                              && (
                                 symbol11.getArcLevel() == 11
                                    || symbol11.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(symbol11.getArcLevel())
                                    || source.getArcLevel() == 11
                                    || source.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           if (symbol12 != null
                              && symbol12.getItemId() == source.getItemId()
                              && (
                                 symbol12.getArcLevel() == 11
                                    || symbol12.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(symbol12.getArcLevel())
                                    || source.getArcLevel() == 11
                                    || source.getArcEXP() >= GameConstants.getExpNeededForAuthenticSymbol(source.getArcLevel())
                              )) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }
                        }

                        int equippedSummon = 0;
                        switch (source.getItemId()) {
                           case 1112585:
                              equippedSummon = 1085;
                              break;
                           case 1112586:
                              equippedSummon = 1087;
                              break;
                           case 1112594:
                              equippedSummon = 1090;
                              break;
                           case 1112663:
                              equippedSummon = 1179;
                              break;
                           case 1112735:
                              equippedSummon = 1179;
                        }

                        if (equippedSummon > 0) {
                           Summoned s = new Summoned(
                              chr, equippedSummon, 1, chr.getTruePosition(), SummonMoveAbility.FOLLOW, (byte)0, System.currentTimeMillis() + 2147483647L
                           );
                           chr.getMap().spawnSummon(s, Integer.MAX_VALUE);
                           chr.addSummon(s);
                        }

                        if ((GameConstants.AlphaSlot(dst) || GameConstants.BetaSlot(dst))
                           && c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(GameConstants.linkedZeroSlot(dst)) != null) {
                           ((Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(GameConstants.linkedZeroSlot(dst))).setStarForce((byte)0);
                        }

                        if (GameConstants.isTheSeedRing(source.getItemId())) {
                           c.getPlayer().changeSkillLevel(80003034, 1, 1);
                        } else if (target != null && GameConstants.isTheSeedRing(target.getItemId())) {
                           c.getPlayer().changeSkillLevel(80003034, -1, 0);
                        }

                        if (source.getItemId() == 1113329) {
                           String key = "ContinousRingTime";
                           c.getPlayer().temporaryStatSet(SecondaryStatFlag.ContinousRingReady, 80003341, 120000, 1);
                           c.getPlayer().setTempKeyValue(key, System.currentTimeMillis() + 120000L);
                        }

                        if (target == null && c.getPlayer().getCurrentCashCodyPreset() != 0 && dst < -100 && dst > -117) {
                           unequipCashCodyPreset0(c, dst, src, false);
                        }

                        if (stats == null) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        } else if (dst > -1200
                           && dst < -999
                           && !GameConstants.isEvanDragonItem(source.getItemId())
                           && !GameConstants.isMechanicItem(source.getItemId())) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        } else if ((dst <= -1200 && dst > -1300 || dst >= -999 && dst < -99)
                           && !stats.containsKey("cash")
                           && !GameConstants.isFairyPendant(source.getItemId())) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        } else if (GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11 && !stats.containsKey("cash")) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        } else if (dst == -18 && !GameConstants.isMountItemAvailable(source.getItemId(), c.getPlayer().getJob())) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        } else if (dst == -118 && source.getItemId() / 10000 != 190) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        } else {
                           if (dst == -55) {
                              MapleQuestStatus stat = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122700));
                              if (stat == null || stat.getCustomData() == null || Long.parseLong(stat.getCustomData()) < System.currentTimeMillis()) {
                                 c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                                 return;
                              }
                           }

                           if ((GameConstants.isKatara(source.getItemId()) || source.getItemId() / 10000 == 135) && !stats.containsKey("cash")) {
                              dst = -10;
                           }

                           if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           } else if (!GameConstants.isMechanicItem(source.getItemId()) || chr.getJob() >= 3500 && chr.getJob() <= 3512) {
                              if (source.getItemId() / 1000 == 1112) {
                                 for (EquipAdditions.RingSet s : EquipAdditions.RingSet.values()) {
                                    if (s.id.contains(source.getItemId())) {
                                       List<Integer> theList = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).listIds();

                                       for (Integer i : s.id) {
                                          if (theList.contains(i)) {
                                             c.getPlayer()
                                                .dropMessage(
                                                   1,
                                                   "You may not equip this item because you already have a "
                                                      + StringUtil.makeEnumHumanReadable(s.name())
                                                      + " equipped."
                                                );
                                             c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                                             return;
                                          }
                                       }
                                    }
                                 }
                              }

                              switch (dst) {
                                 case -106:
                                    Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-105);
                                    if (top != null && GameConstants.isOverall(top.getItemId())) {
                                       if (chr.getInventory(type).isFull()) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }

                                       unequip(type, c, (short)-105, chr.getInventory(MapleInventoryType.CASH_EQUIP).getNextFreeSlot());
                                    }
                                    break;
                                 case -105:
                                    Item topxxx = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-105);
                                    Item bottomx = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-106);
                                    if (topxxx != null && GameConstants.isOverall(source.getItemId())) {
                                       if (chr.getInventory(type).isFull(bottomx != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }

                                       if (chr.getInventory(MapleInventoryType.CASH_EQUIP).getNextFreeSlot() == -1) {
                                          chr.dropMessage(1, "치장 인벤토리 공간을 최소 1칸 남기고 시도해주세요.");
                                          return;
                                       }

                                       unequip(type, c, (short)-105, chr.getInventory(MapleInventoryType.CASH_EQUIP).getNextFreeSlot());
                                    }

                                    if (bottomx != null && GameConstants.isOverall(source.getItemId())) {
                                       if (chr.getInventory(type).isFull()) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }

                                       unequip(type, c, (short)-106, chr.getInventory(MapleInventoryType.CASH_EQUIP).getNextFreeSlot());
                                    }
                                    break;
                                 case -10:
                                    Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
                                    if (GameConstants.isKatara(source.getItemId())) {
                                       if (chr.getJob() != 900 && (chr.getJob() < 430 || chr.getJob() > 434)) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }
                                    } else if ((
                                          weapon != null && GameConstants.isTwoHanded(weapon.getItemId()) && !GameConstants.isSpecialShield(source.getItemId())
                                             || chr.getJob() >= 1100 && chr.getJob() <= 1112 && source.getItemId() / 1000 == 1092
                                       )
                                       && !DBConfig.isGanglim) {
                                       c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
                                       return;
                                    }
                                    break;
                                 case -6:
                                    Item topx = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-5);
                                    if (topx != null && GameConstants.isOverall(topx.getItemId())) {
                                       if (chr.getInventory(type).isFull()) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }

                                       unequip(type, c, (short)-5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                                    }
                                    break;
                                 case -5:
                                    Item topxx = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-5);
                                    Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-6);
                                    if (topxx != null && GameConstants.isOverall(source.getItemId())) {
                                       if (chr.getInventory(type).isFull(bottom != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }

                                       unequip(type, c, (short)-5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                                    }

                                    if (bottom != null && GameConstants.isOverall(source.getItemId())) {
                                       if (chr.getInventory(type).isFull()) {
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                                          c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                                          return;
                                       }

                                       unequip(type, c, (short)-6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                                    }
                              }

                              source = (Equip)chr.getInventory(type).getItem(src);
                              target = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                              if (source == null) {
                                 c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              } else {
                                 int flag = source.getFlag();
                                 boolean change = false;
                                 if (ii.isEquipTradeBlocked(source.getItemId())
                                    || source.getItemId() / 10000 == 167
                                    || stats.containsKey("cash")
                                    || source.getKarmaCount() > 0 && !ii.isKarmaEnabled(source.getItemId()) && !ii.isPKarmaEnabled(source.getItemId())) {
                                    if (!ItemFlag.POSSIBLE_TRADING.check(flag)) {
                                       if (!DBConfig.isGanglim) {
                                          flag |= ItemFlag.POSSIBLE_TRADING.getValue();
                                       } else if (DBConfig.isGanglim && !ii.isAccountShared(source.getItemId())) {
                                          flag |= ItemFlag.POSSIBLE_TRADING.getValue();
                                       }

                                       change = true;
                                    }

                                    if (stats.containsKey("cash") && ItemFlag.KARMA_EQ.check(flag)) {
                                       flag -= ItemFlag.KARMA_EQ.getValue();
                                       change = true;
                                    }

                                    if (change && (source.getItemId() != 1672077 || DBConfig.isGanglim && source.getItemId() != 1662141)) {
                                       source.setFlag(flag);
                                       c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, type.getType(), c.getPlayer()));
                                    }
                                 }

                                 boolean updateAndroid = false;
                                 if (source.getItemId() / 10000 == 166) {
                                    Item heart = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-28);
                                    chr.removeAndroid();
                                    if (heart != null) {
                                       if (source.getAndroid() == null) {
                                          long uid = MapleInventoryIdentifier.getInstance();
                                          source.setUniqueId(uid);
                                          source.setAndroid(Android.create(source.getItemId(), uid));
                                          if (source.getItemId() != 1662025 && source.getItemId() != 1662026) {
                                          }

                                          flag |= ItemFlag.ANDROID_ACTIVATED.getValue();
                                          source.setFlag(flag);
                                          updateAndroid = true;
                                       }

                                       if (source.getAndroid() == null) {
                                          chr.dropMessage(1, "안드로이드 오류가 발생하였습니다.");
                                          return;
                                       }

                                       chr.setAndroid(source.getAndroid());
                                    }
                                 } else if (source.getItemId() / 10000 == 167) {
                                    Item android = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-27);
                                    chr.removeAndroid();
                                    if (android != null && android.getAndroid() == null) {
                                       long uid = MapleInventoryIdentifier.getInstance();
                                       android.setUniqueId(uid);
                                       android.setAndroid(Android.create(android.getItemId(), uid));
                                       if (source.getItemId() != 1662025 && source.getItemId() != 1662026) {
                                       }

                                       flag |= ItemFlag.ANDROID_ACTIVATED.getValue();
                                       if (DBConfig.isGanglim && ii.isAccountShared(android.getItemId()) && (flag & ItemFlag.POSSIBLE_TRADING.getValue()) > 0) {
                                          flag -= ItemFlag.POSSIBLE_TRADING.getValue();
                                       }

                                       android.setFlag(flag);
                                       updateAndroid = true;
                                    }

                                    if (android != null && android.getAndroid() != null) {
                                       chr.setAndroid(android.getAndroid());
                                    }
                                 }

                                 if (source.getCharmEXP() == 0 && !ItemFlag.CHARM_EQUIPPED.check(flag)) {
                                    Integer originalCharmEXP = ii.getEquipStats(source.getItemId()).get("charmEXP");
                                    if (originalCharmEXP != null && originalCharmEXP > 0) {
                                       flag |= ItemFlag.CHARM_EQUIPPED.getValue();
                                       source.setFlag(flag);
                                       c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, (byte)dst, c.getPlayer()));
                                       chr.send(CWvsContext.InventoryPacket.updateEquipSlot(source));
                                    }
                                 }

                                 if (source.getCharmEXP() > 0 && !ItemFlag.CHARM_EQUIPPED.check(flag)) {
                                    chr.getTrait(MapleTrait.MapleTraitType.charm).addExp(source.getCharmEXP(), chr);
                                    source.setCharmEXP((short)0);
                                    flag |= ItemFlag.CHARM_EQUIPPED.getValue();
                                    source.setFlag(flag);
                                    c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateSpecialItemUse_(source, (byte)dst, c.getPlayer()));
                                    chr.send(CWvsContext.InventoryPacket.updateEquipSlot(source));
                                 }

                                 chr.getInventory(type).removeSlot(src);
                                 if (target != null) {
                                    chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
                                 }

                                 source.setPosition(dst);
                                 chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
                                 if (target != null) {
                                    target.setPosition(src);
                                    chr.getInventory(type).addFromDB(target);
                                 }

                                 if (GameConstants.isWeapon(source.getItemId())) {
                                    c.getPlayer().temporaryStatReset(SecondaryStatFlag.Booster);
                                    c.getPlayer().temporaryStatReset(SecondaryStatFlag.NoBulletConsume);
                                    c.getPlayer().temporaryStatReset(SecondaryStatFlag.SoulArrow);
                                 }

                                 if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
                                    c.getPlayer().temporaryStatReset(SecondaryStatFlag.RideVehicle);
                                 } else if (source.getItemId() == 1113085) {
                                    chr.setRoadRingExpBoost(0);
                                    chr.doRoadRing();
                                 }

                                 for (int medalID : ServerConstants.guildMedalItemID) {
                                    if (source.getItemId() == medalID) {
                                       chr.checkGuildBonusExpBoost();
                                    }
                                 }

                                 for (int medalIDx : ServerConstants.singleMedalItemID) {
                                    if (source.getItemId() == medalIDx) {
                                       chr.checkGuildBonusExpBoost();
                                    }
                                 }

                                 if (source.getState() >= 17 || source.getPotential4() > 0) {
                                    handlePotentialSkills(chr, source, 1);
                                 }

                                 if (target != null && (target.getState() >= 17 || target.getPotential4() > 0)) {
                                    handlePotentialSkills(chr, target, -1);
                                 }

                                 if (dst <= -1200
                                    && chr.getAndroid() != null
                                    && !GameConstants.isArcaneSymbol(source.getItemId())
                                    && !GameConstants.isAuthenticSymbol(source.getItemId())) {
                                    chr.setAndroid(chr.getAndroid());
                                 }

                                 c.getPlayer().checkEquippedMasterLabel();

                                 for (int medalIDxx : ServerConstants.donatorMedalItemID) {
                                    c.getPlayer().checkDonatorMedalBuff(medalIDxx);
                                 }

                                 c.getPlayer().checkPitchBlackBuff();
                                 chr.getMap().calcIncMobGen(chr, true);
                                 c.getSession()
                                    .writeAndFlush(
                                       CWvsContext.InventoryPacket.moveInventoryItem(type, src, dst, (short)(arcane ? 26 : 0), false, false, arcane)
                                    );
                                 chr.equipChanged();
                                 if (target != null && GameConstants.isFairyPendant(target.getItemId())) {
                                    chr.cancelFairySchedule(true);
                                 }

                                 if (GameConstants.isFairyPendant(source.getItemId())) {
                                    chr.startFairySchedule(false);
                                 }

                                 if (target != null && chr.isSoulWeapon(target) || chr.isSoulWeapon(source)) {
                                    chr.checkSoulSkillLevel();
                                 }

                                 if (updateAndroid) {
                                    Item androidx = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-27);
                                    if (androidx != null) {
                                       chr.send(CWvsContext.InventoryPacket.updateEquipSlot(androidx));
                                    }
                                 }
                              }
                           } else {
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           }
                        }
                     }
                  }
               } else {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      }
   }

   public static void ZeroUnEquip(MapleClient c, short src, short dst) {
      c.getPlayer().setZeroEquip((byte)1);
      c.getPlayer().setZeroSlot(dst);
   }

   public static void unequipCashCodyPreset0(MapleClient c, short src, short dst, boolean move) {
      int questId = QuestExConstants.CashCodyPreset.getQuestID();
      int preset0Value = c.getPlayer().getOneInfoQuestInteger(questId, "preset0");
      short addPos = 1700;
      switch (preset0Value) {
         case 0:
            addPos = 1700;
            break;
         case 1:
            addPos = 1717;
      }

      short checkSrc = (short)(src - addPos);
      Equip source = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(checkSrc);
      if (source != null) {
         int currentPreset = c.getPlayer().getCurrentCashCodyPreset();
         if ((source.getItemState() & (currentPreset == 1 ? ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue() : ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue()))
            <= 0) {
            List<byte[]> operations = new ArrayList<>();
            source.setItemState(
               source.getItemState() | (currentPreset == 1 ? ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue() : ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue())
            );
            operations.add(InventoryOperation.updatePacket(source, checkSrc, MapleInventoryType.CASH_EQUIP, c.getPlayer(), false, false));
            if (move) {
               operations.add(InventoryOperation.swapPacket(MapleInventoryType.CASH_EQUIP, src, checkSrc));
               operations.add(InventoryOperation.swapPacket(MapleInventoryType.CASH_EQUIP, src, dst));
               operations.add(InventoryOperation.removePacket(MapleInventoryType.CASH_EQUIP, dst));
            } else {
               operations.add(InventoryOperation.removePacket(MapleInventoryType.CASH_EQUIP, src));
            }

            c.getPlayer().send(InventoryOperation.getInventoryOperationPacket(operations, true));
            c.getPlayer().equipChanged();
         }
      }
   }

   public static void equipCashCodyPreset0(MapleClient c, short src) {
      int questId = QuestExConstants.CashCodyPreset.getQuestID();
      int preset0Value = c.getPlayer().getOneInfoQuestInteger(questId, "preset0");
      short addPos = 1700;
      switch (preset0Value) {
         case 0:
            addPos = 1700;
            break;
         case 1:
            addPos = 1717;
      }

      short checkSrc = (short)(src - addPos);
      Equip source = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(checkSrc);
      if (source != null) {
         List<byte[]> operations = new ArrayList<>();
         int currentPreset = c.getPlayer().getCurrentCashCodyPreset();
         source.setItemState(
            source.getItemState() & ~(currentPreset == 1 ? ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue() : ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue())
         );
         operations.add(InventoryOperation.updatePacket(source, checkSrc, MapleInventoryType.CASH_EQUIP, c.getPlayer(), false, false));
         operations.add(InventoryOperation.swapPacket(MapleInventoryType.CASH_EQUIP, checkSrc, src));
         c.getPlayer().send(InventoryOperation.getInventoryOperationPacket(operations, true));
         c.getPlayer().equipChanged();
      }
   }

   public static void unequip(MapleInventoryType type, MapleClient c, short src, short dst) {
      Equip source = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
      Equip target = (Equip)c.getPlayer().getInventory(type).getItem(dst);
      if (GameConstants.AlphaSlot(src) && dst < 0) {
         c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
         source.setPosition(dst);
         c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.moveInventoryItem(type, src, dst, (short)7, false, false, false));
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else if (source == null && c.getPlayer().getCurrentCashCodyPreset() != 0 && src < -100 && src > -117) {
         unequipCashCodyPreset0(c, src, dst, true);
      } else if (dst >= 0 && source != null) {
         if (target != null && src <= 0) {
            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
         } else {
            if (source != null && c.getPlayer().getCurrentCashCodyPreset() == 0) {
               if ((source.getItemState() & ItemStateFlag.CODY_PRESET_ITEM.getValue()) > 0) {
                  source.setItemState(source.getItemState() & ~ItemStateFlag.CODY_PRESET_ITEM.getValue());
               }

               if ((source.getItemState() & ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue()) > 0) {
                  source.setItemState(source.getItemState() & ~ItemStateFlag.CODY_PRESET1_UNEQUIP.getValue());
               }

               if ((source.getItemState() & ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue()) > 0) {
                  source.setItemState(source.getItemState() & ~ItemStateFlag.CODY_PRESET2_UNEQUIP.getValue());
               }
            }

            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
            if (target != null) {
               c.getPlayer().getInventory(type).removeSlot(dst);
            }

            int equippedSummon = 0;
            switch (source.getItemId()) {
               case 1112585:
                  equippedSummon = 1085;
                  break;
               case 1112586:
                  equippedSummon = 1087;
                  break;
               case 1112594:
                  equippedSummon = 1090;
                  break;
               case 1112663:
                  equippedSummon = 1179;
                  break;
               case 1112735:
                  equippedSummon = 1179;
            }

            if (equippedSummon > 0) {
               c.getPlayer().temporaryStatResetBySkillID(equippedSummon);
            }

            source.setPosition(dst);
            c.getPlayer().getInventory(type).addFromDB(source);
            if (target != null) {
               target.setPosition(src);
               c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
            }

            if (!DBConfig.isGanglim && src == -21) {
               int[] donatorBuffs = new int[]{
                  80003170, 80003171, 80003172, 80003173, 80003174, 80003175, 80003176, 80003177, 80003178, 80003203, 80003204, 80003205, 80003206, 80003207
               };

               for (int buff : donatorBuffs) {
                  c.getPlayer().temporaryStatResetBySkillID(buff);
               }
            }

            if (GameConstants.isWeapon(source.getItemId())) {
               c.getPlayer().temporaryStatReset(SecondaryStatFlag.Booster);
               c.getPlayer().temporaryStatReset(SecondaryStatFlag.NoBulletConsume);
               c.getPlayer().temporaryStatReset(SecondaryStatFlag.SoulArrow);
            } else if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
               c.getPlayer().temporaryStatReset(SecondaryStatFlag.RideVehicle);
            } else if (source.getItemId() / 10000 == 166) {
               c.getPlayer().removeAndroid();
            } else if (source.getItemId() / 10000 == 167) {
               c.getPlayer().removeAndroid();
            } else if (src <= -1200
               && c.getPlayer().getAndroid() != null
               && !GameConstants.isArcaneSymbol(source.getItemId())
               && !GameConstants.isAuthenticSymbol(source.getItemId())) {
               c.getPlayer().setAndroid(c.getPlayer().getAndroid());
            } else if (source.getItemId() == 1112585) {
               c.getPlayer().dispelSkill(-2022746);
            } else if (source.getItemId() == 1112586) {
               c.getPlayer().dispelSkill(-2022747);
            } else if (source.getItemId() == 1112594) {
               c.getPlayer().dispelSkill(-2022764);
            } else if (source.getItemId() == 1112663) {
               c.getPlayer().dispelSkill(-2022823);
            } else if (source.getItemId() == 1112735) {
               c.getPlayer().dispelSkill(-2022823);
            }

            if (source.getItemId() == 1122334) {
               c.getPlayer().setFairyExp((byte)0);
            }

            for (int medalID : ServerConstants.guildMedalItemID) {
               if (source.getItemId() == medalID) {
                  c.getPlayer().checkGuildBonusExpBoost();
               }
            }

            for (int medalIDx : ServerConstants.singleMedalItemID) {
               if (source.getItemId() == medalIDx) {
                  c.getPlayer().checkGuildBonusExpBoost();
               }
            }

            if (GameConstants.isGenesisWeapon(source.getItemId())) {
               if (c.getPlayer().getTotalSkillLevel(80002633) > 0) {
                  c.getPlayer().changeSkillLevel(80002633, -1, 0);
               }

               if (c.getPlayer().getTotalSkillLevel(80002632) > 0) {
                  c.getPlayer().changeSkillLevel(80002632, -1, 0);
               }
            }

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (source.getState() >= 17 || source.getPotential4() > 0) {
               handlePotentialSkills(c.getPlayer(), source, -1);
            }

            c.getPlayer().checkEquippedMasterLabel();

            for (int medalIDxx : ServerConstants.donatorMedalItemID) {
               c.getPlayer().checkDonatorMedalBuff(medalIDxx);
            }

            if (GameConstants.isTheSeedRing(source.getItemId())) {
               c.getPlayer().changeSkillLevel(80003034, -1, 0);
            }

            if (DBConfig.isGanglim && source.getItemId() >= 1142093 && source.getItemId() <= 1142099) {
               for (int i = 80004003; i <= 80004009; i++) {
                  c.getPlayer().temporaryStatResetBySkillID(i);
               }
            }

            c.getPlayer().getMap().resetIncMobGen(c.getPlayer());
            boolean arcane = GameConstants.isArcaneSymbol(source.getItemId()) || GameConstants.isAuthenticSymbol(source.getItemId());
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.moveInventoryItem(type, src, dst, (short)(arcane ? 25 : 1), false, false, arcane));
            c.getPlayer().equipChanged();
            if (GameConstants.isFairyPendant(source.getItemId())) {
               c.getPlayer().cancelFairySchedule(true);
            }

            if (GameConstants.isTheSeedRing(source.getItemId())) {
               c.getPlayer().temporaryStatResetBySkillID(GameConstants.getTheSeedRingSkill(source.getItemId()));
               if (source.getItemId() == 1113098) {
                  AffectedArea area = c.getPlayer()
                     .getMap()
                     .getAffectedAreaBySkillId(GameConstants.getTheSeedRingSkill(source.getItemId()), c.getPlayer().getId());
                  c.getPlayer()
                     .getMap()
                     .broadcastMessage(CField.removeAffectedArea(area.getObjectId(), GameConstants.getTheSeedRingSkill(source.getItemId()), false));
                  c.getPlayer().getMap().removeMapObject(area);
               }

               if (source.getItemId() == 1113329) {
                  c.getPlayer().temporaryStatResetBySkillID(80003341);
                  c.getPlayer().temporaryStatResetBySkillID(80003342);
                  c.getPlayer().removeTempKeyValue("ContinousRingTime");
               }
            }

            if (source != null && c.getPlayer().isSoulWeapon(source) || target != null && c.getPlayer().isSoulWeapon(target)) {
               c.getPlayer().setSoulCount((short)0);
               c.getPlayer().checkSoulSkillLevel();
            }
         }
      }
   }

   public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity) {
      return drop(c, type, src, quantity, false);
   }

   public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity, boolean npcInduced) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (src < 0) {
         type = MapleInventoryType.EQUIPPED;
      }

      if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
         Item source = c.getPlayer().getInventory(type).getItem(src);
         if (source.getItemId() == 2431307 || source.getItemId() == 2432128) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return false;
         } else if (quantity >= 0
            && source != null
            && (npcInduced || !GameConstants.isPet(source.getItemId()))
            && (quantity != 0 || GameConstants.isRechargable(source.getItemId()))
            && !c.getPlayer().inPVP()) {
            int flag = source.getFlag();
            if (quantity > source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return false;
            } else if (!ItemFlag.PROTECTED.check(flag) && (quantity == 1 || type != MapleInventoryType.EQUIP)) {
               if (GameConstants.isEquip(source.getItemId()) && (((Equip)source).getSpecialAttribute() & EquipSpecialAttribute.VESTIGE.getType()) > 0) {
                  c.getPlayer().dropMessage(1, "장비의 흔적은 버리기 불가능합니다.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return false;
               } else {
                  if (DBConfig.isGanglim) {
                     if (GameConstants.isRoyalBanMesoItem(source.getItemId())) {
                        c.getPlayer().dropMessage(1, "메소 주머니는 버릴 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return false;
                     }

                     if (GameConstants.isRoyalBanDropItem(source.getItemId())) {
                        c.getPlayer().dropMessage(1, "해당 아이템은 버릴 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return false;
                     }
                  }

                  StringBuilder sb = new StringBuilder();
                  sb.append("플레이어가 필드에 아이템 드롭함");
                  if (source instanceof Equip) {
                     sb.append(" (장비 정보[");
                     sb.append(((Equip)source).toString());
                     sb.append("])");
                  }

                  LoggingManager.putLog(
                     new DropLog(c.getPlayer(), source.getItemId(), quantity, c.getChannel(), c.getPlayer().getMapId(), DropLogType.PlayerDrop.getType(), sb)
                  );
                  Point dropPos = new Point(c.getPlayer().getPosition());
                  c.getPlayer().getCheatTracker().checkDrop();
                  source.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "이(가) 필드 드랍한 아이템.");
                  if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
                     Item target = source.copy();
                     target.setQuantity(quantity);
                     source.setQuantity((short)(source.getQuantity() - quantity));
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventorySlot(type, source, true));
                     if (!ii.isDropRestricted(target.getItemId()) && !ii.isAccountShared(target.getItemId())) {
                        if (!GameConstants.isPet(target.getItemId()) && !ItemFlag.POSSIBLE_TRADING.check(target.getFlag())) {
                           c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                        } else if (ItemFlag.KARMA_EQ.check(target.getFlag())) {
                           target.setFlag(target.getFlag() - ItemFlag.KARMA_EQ.getValue());
                           c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                        } else if (ItemFlag.KARMA_USE.check(target.getFlag())) {
                           target.setFlag(target.getFlag() - ItemFlag.KARMA_USE.getValue());
                           c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                        } else {
                           c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                        }
                     } else if (ItemFlag.KARMA_EQ.check(target.getFlag())) {
                        target.setFlag(target.getFlag() - ItemFlag.KARMA_EQ.getValue());
                        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                     } else if (ItemFlag.KARMA_USE.check(target.getFlag())) {
                        target.setFlag(target.getFlag() - ItemFlag.KARMA_USE.getValue());
                        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                     } else {
                        c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                     }
                  } else {
                     c.getPlayer().getInventory(type).removeSlot(src);
                     if (GameConstants.isHarvesting(source.getItemId())) {
                        c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
                     }

                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.clearInventoryItem(type, src, true));
                     if (src < 0) {
                        c.getPlayer().equipChanged();
                     }

                     if (!ii.isDropRestricted(source.getItemId()) && !ii.isAccountShared(source.getItemId())) {
                        if (!GameConstants.isPet(source.getItemId()) && !ItemFlag.POSSIBLE_TRADING.check(flag)) {
                           c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                        } else if (ItemFlag.KARMA_EQ.check(flag)) {
                           source.setFlag(flag - ItemFlag.KARMA_EQ.getValue());
                           c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                        } else if (ItemFlag.KARMA_USE.check(flag)) {
                           source.setFlag(flag - ItemFlag.KARMA_USE.getValue());
                           c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                        } else {
                           c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                        }
                     } else if (ItemFlag.KARMA_EQ.check(flag)) {
                        source.setFlag(flag - ItemFlag.KARMA_EQ.getValue());
                        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                     } else if (ItemFlag.KARMA_USE.check(flag)) {
                        source.setFlag(flag - ItemFlag.KARMA_USE.getValue());
                        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                     } else {
                        c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                     }
                  }

                  if (!DBConfig.isGanglim) {
                     for (int itemID : bmWeapons) {
                        if (source.getItemId() == 2439614 || source.getItemId() == itemID || source.getItemId() == itemID + 1) {
                           for (int i = 2000018; i <= 2000027; i++) {
                              c.getPlayer().updateOneInfo(i, "clear", "0");
                              MapleQuestStatus newStatus = new MapleQuestStatus(MapleQuest.getInstance(i), (byte)0, 2003);
                              c.getPlayer().updateQuest(newStatus);
                              c.getPlayer().dropMessage(1, "제네시스 해방 퀘스트가 초기화되었습니다. 재접속 시 다시 수행 가능합니다.");
                           }
                        }
                     }
                  }

                  return true;
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return false;
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return false;
         }
      } else {
         return false;
      }
   }

   public static int getNeedNumSlots(MapleClient c, int itemid, int quantity) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventoryType type = GameConstants.getInventoryType(itemid);
      int numSlotsNeeded;
      if (!type.equals(MapleInventoryType.EQUIP) && !type.equals(MapleInventoryType.CASH_EQUIP)) {
         short slotMax = ii.getSlotMax(itemid);
         List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
         if (!GameConstants.isRechargable(itemid) && existing.size() > 0) {
            for (Item eItem : existing) {
               short oldQ = eItem.getQuantity();
               if (oldQ < slotMax) {
                  short newQ = (short)Math.min(oldQ + quantity, slotMax);
                  quantity -= newQ - oldQ;
               }

               if (quantity <= 0) {
                  break;
               }
            }
         }

         if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
            numSlotsNeeded = (int)Math.ceil((double)quantity / slotMax);
         } else {
            numSlotsNeeded = 1;
         }
      } else {
         numSlotsNeeded = quantity;
      }

      return numSlotsNeeded;
   }

   public static void handlePotentialSkills(MapleCharacter chr, Equip equip, int skillLevel) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int[] potentials = new int[]{
         equip.getPotential1(), equip.getPotential2(), equip.getPotential3(), equip.getPotential4(), equip.getPotential5(), equip.getPotential6()
      };

      for (int potential : potentials) {
         int lv = ii.getReqLevel(equip.getItemId()) / 10 - 1;
         if (lv < 0) {
            lv = 0;
         }

         if (potential != 0 && ii.getPotentialInfo(potential) != null && ii.getPotentialInfo(potential).get(lv) != null) {
            int usefulSkill = ii.getPotentialInfo(potential).get(lv).skillID;
            if (usefulSkill > 0) {
               int skillId = GameConstants.get_novice_skill_root(chr.getJob()) * 10000 + usefulSkill;
               if (chr.getSkillLevel(skillId) != skillLevel) {
                  chr.changeSingleSkillLevel(SkillFactory.getSkill(skillId), skillLevel, (byte)skillLevel, true);
               }
            }
         }
      }
   }
}
