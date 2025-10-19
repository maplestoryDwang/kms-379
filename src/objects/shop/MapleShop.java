package objects.shop;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import logging.LoggingManager;
import logging.entry.ConsumeLogType;
import logging.entry.ItemLog;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.item.Equip;
import objects.item.IntensePowerCrystal;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.caching.mission.submission.checkvalue.NpcID;
import objects.users.enchant.ItemFlag;
import objects.users.skills.SkillFactory;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;

public class MapleShop {
   private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();
   private final int id;
   private int npcId;
   private final List<MapleShopItem> items = new LinkedList<>();
   private final List<Pair<Integer, String>> ranks = new ArrayList<>();

   public MapleShop(int id, int npcId) {
      this.id = id;
      this.npcId = npcId;
   }

   public void setNpcId(int npcId) {
      this.npcId = npcId;
   }

   public void addItem(MapleShopItem item) {
      this.items.add(item);
   }

   public List<MapleShopItem> getItems() {
      return this.items;
   }

   public void sendShop(MapleClient c) {
      long shopTime = c.getPlayer().getShopTime();
      long currentTime = System.currentTimeMillis();
      if (currentTime - shopTime >= 1000L) {
         if (c.isGm()) {
            System.out.println("상점 ID : " + this.getId());
         }

         c.getPlayer().setShop(this);
         if (DBConfig.isGanglim) {
            this.isUnionShop(c, this.getId());
         }

         List<objects.users.BuyLimitEntry> limits = new ArrayList<>();
         List<Integer> reset = new ArrayList<>();
         if (this.getId() == 9062145) {
            int grade = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.KillPoint.getQuestID(), "lv");

            for (int idx = 0; idx < this.getItems().size(); idx++) {
               MapleShopItem item = this.getItems().get(idx);
               if (item != null
                  && item.getLimitQuestExID() == QuestExConstants.KillPoint.getQuestID()
                  && item.getLimitQuestExKey().equals("lv")
                  && item.getLimitQuestExValue() != grade) {
                  limits.add(new objects.users.BuyLimitEntry(this.getId(), idx, item.getItemId(), item.getWorldBuyLimit(), System.currentTimeMillis()));
               } else {
                  reset.add(idx);

                  for (objects.users.BuyLimitEntry entry : c.getPlayer().getWorldBuyLimit().getBuyLimits()) {
                     if (entry.getItemIndex() == idx) {
                        limits.add(new objects.users.BuyLimitEntry(this.getId(), idx, item.getItemId(), entry.getBuyCount(), entry.getBuyTime()));
                        break;
                     }
                  }
               }
            }
         }

         c.getSession().writeAndFlush(CField.NPCPacket.getNPCShop(this.getNpcId(), this, c));
         c.getPlayer().getBuyLimit().checkBuyLimit(c.getPlayer(), this.getId(), this.getNpcId());
         c.getPlayer().getWorldBuyLimit().checkBuyLimit(c.getPlayer(), this.getId());
         if (!limits.isEmpty()) {
            limits.forEach(e -> {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.SET_BUY_LIMIT_COUNT.getValue());
               packet.write(1);
               e.encode(packet);
               c.getPlayer().send(packet.getPacket());
            });
         }

         c.getPlayer().setShopTime(currentTime);
      }
   }

   public void sendShop(MapleClient c, int customNpc) {
      long shopTime = c.getPlayer().getShopTime();
      long currentTime = System.currentTimeMillis();
      if (currentTime - shopTime >= 1000L) {
         c.getPlayer().setShop(this);
         c.getSession().writeAndFlush(CField.NPCPacket.getNPCShop(customNpc, this, c));
         c.getPlayer().setShopTime(currentTime);
      }
   }

   public void buy(MapleClient c, int slot, int itemId, short bundle) {
      if (itemId / 10000 == 190 && !GameConstants.isMountItemAvailable(itemId, c.getPlayer().getJob())) {
         c.getPlayer().dropMessage(1, "You may not buy this item.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int x = 0;
         int index = -1;
         if (slot >= this.items.size()) {
            index = slot - this.items.size();
         }

         if (index >= 0) {
            Item i = c.getPlayer().getRebuy().get(index);
            if (i.getItemId() == itemId) {
               long price = i.getRebuyPrice();
               if (this.getId() == 9062145) {
                  int grade = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.KillPoint.getQuestID(), "lv");
                  MapleShopItem item = this.getItems().get(index);
                  if (item != null
                     && item.getLimitQuestExID() == QuestExConstants.KillPoint.getQuestID()
                     && item.getLimitQuestExKey().equals("lv")
                     && item.getLimitQuestExValue() != grade) {
                     return;
                  }
               }

               if (price < 0L || c.getPlayer().getMeso() < price) {
                  c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
               } else if (MapleInventoryManipulator.checkSpace(c, itemId, i.getQuantity(), i.getOwner())) {
                  c.getPlayer().gainMeso(-price, false);
                  MapleInventoryManipulator.addbyItem(c, i);
                  c.getPlayer().getRebuy().remove(index);
                  c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, index));
                  StringBuilder sb = new StringBuilder();
                  sb.append("상점 아이템 재구매 시도 (캐릭터 : ");
                  sb.append(c.getPlayer().getName());
                  sb.append(", 계정 : ");
                  sb.append(c.getPlayer().getClient().getAccountName());
                  sb.append(", 상점ID : ");
                  sb.append(this.getId());
                  sb.append(", 아이템 : ");
                  sb.append(i.getItemId());
                  sb.append(" ");
                  sb.append(i.getQuantity());
                  sb.append("개, 가격 : ");
                  sb.append(price);
                  sb.append(" 메소)");
                  LoggingManager.putLog(
                     new ItemLog(
                        c.getPlayer(),
                        c.getChannel(),
                        i.getItemId(),
                        i.getQuantity(),
                        c.getPlayer().getMapId(),
                        ConsumeLogType.NpcShopBuy.getType(),
                        price,
                        0L,
                        sb
                     )
                  );
               } else {
                  c.getPlayer().dropMessage(1, "인벤토리가 꽉 찼습니다.");
                  c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
               }
            }
         } else {
            MapleShopItem item = this.findBySlot((short)slot);
            short quantity = (short)(item.getQuantity() * bundle);
            if (item == null) {
               System.out.println("error shop");
            }

            if (item.getItemId() == itemId) {
               if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), quantity, "")) {
                  c.getPlayer().dropMessage(1, "인벤토리 슬롯이 부족합니다.");
                  c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
               } else {
                  if (item.getBuyLimit() > 0) {
                     for (objects.users.BuyLimitEntry entry : new ArrayList<>(c.getPlayer().getBuyLimit().getBuyLimits())) {
                        if (entry.getShopID() == this.getId() && entry.getItemIndex() == slot && entry.getBuyCount() >= item.getBuyLimit()) {
                           c.getPlayer().dropMessage(1, "더 이상 구매할 수 없습니다.");
                           return;
                        }
                     }

                     for (int i = 0; i < bundle; i++) {
                        c.getPlayer().getBuyLimit().setLimit(c.getPlayer(), this, item, slot);
                     }
                  }

                  if (item.getWorldBuyLimit() > 0) {
                     for (objects.users.BuyLimitEntry entryx : new ArrayList<>(c.getPlayer().getWorldBuyLimit().getBuyLimits())) {
                        if (entryx.getShopID() == this.getId() && entryx.getItemIndex() == slot && entryx.getBuyCount() >= item.getWorldBuyLimit()) {
                           c.getPlayer().dropMessage(1, "더 이상 구매할 수 없습니다.");
                           return;
                        }
                     }

                     for (int i = 0; i < bundle; i++) {
                        c.getPlayer().getWorldBuyLimit().setLimit(c.getPlayer(), this, item, slot);
                     }
                  }

                  StringBuilder sb = new StringBuilder();
                  sb.append("상점 아이템 구매 시도 (캐릭터 : ");
                  sb.append(c.getPlayer().getName());
                  sb.append(", 계정 : ");
                  sb.append(c.getPlayer().getClient().getAccountName());
                  sb.append(", 상점ID : ");
                  sb.append(this.getId());
                  sb.append(", 아이템 : ");
                  sb.append(item.getItemId());
                  sb.append(" ");
                  sb.append(item.getQuantity() * bundle);
                  sb.append("개, 가격 : ");
                  sb.append(item.getPrice());
                  sb.append(" 메소, ");
                  sb.append(item.getPointPrice());
                  sb.append(" 포인트)");
                  LoggingManager.putLog(
                     new ItemLog(
                        c.getPlayer(),
                        c.getChannel(),
                        item.getItemId(),
                        item.getQuantity() * bundle,
                        c.getPlayer().getMapId(),
                        ConsumeLogType.NpcShopBuy.getType(),
                        item.getPrice(),
                        0L,
                        sb
                     )
                  );
                  if (item.getLimitQuestExID() > 0) {
                     int value = c.getPlayer().getOneInfoQuestInteger(item.getLimitQuestExID(), item.getLimitQuestExKey());
                     if (value < item.getLimitQuestExValue()) {
                        return;
                     }
                  }

                  if (item != null && item.getPointQuestExID() > 0 && item.getPointPrice() > 0) {
                     String value = c.getPlayer().getOneInfoQuest(item.getPointQuestExID(), "point");
                     int point = 0;
                     if (value != null && !value.isEmpty()) {
                        point = Integer.parseInt(value);
                     }

                     if (point >= item.getPointPrice()) {
                        if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                           c.getPlayer().updateOneInfo(item.getPointQuestExID(), "point", String.valueOf(point - item.getPointPrice() * bundle));
                           MapleInventoryManipulator.removeById(
                              c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ() * bundle, false, false
                           );
                           if (NpcID.allNpcIDList.contains(this.getNpcId())) {
                              AchievementFactory.checkNpcShopBuy(c.getPlayer(), itemId, this.getNpcId(), 1L);
                           }

                           if (GameConstants.isPet(itemId)) {
                              Item items = new Item(itemId, (short)1, (short)1, 0);
                              items.setExpiration(System.currentTimeMillis() + 2592000000L);
                              MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                              items.setPet(pet);
                              items.setUniqueId(pet.getUniqueId());
                              MapleInventoryManipulator.addFromDrop(c, items, false);
                           } else {
                              if (GameConstants.isRechargable(itemId)) {
                                 quantity = ii.getSlotMax(item.getItemId());
                              }

                              if (item.getPointQuestExID() == 17015) {
                                 if (item.getItemId() != 5068300 && item.getItemId() != 5680157) {
                                    MapleInventoryManipulator.addById(
                                       c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date()
                                    );
                                 } else {
                                    Item wonderBerry = new Item(
                                       item.getItemId(), (short)1, (short)(item.getQuantity() * bundle), (short)ItemFlag.KARMA_USE.getValue()
                                    );
                                    MapleInventoryManipulator.addFromDrop(c, wonderBerry, true);
                                 }
                              } else if (item.getPointQuestExID() != QuestExConstants.NeoCoreEvent.getQuestID() && item.getPointQuestExID() != 16180) {
                                 if (item.getPointQuestExID() == 100711) {
                                    if (item.getItemId() == 5530544) {
                                       Item wonderBerry = new Item(
                                          item.getItemId(), (short)1, (short)(item.getQuantity() * bundle), (short)ItemFlag.KARMA_USE.getValue()
                                       );
                                       MapleInventoryManipulator.addFromDrop(c, wonderBerry, true);
                                    } else {
                                       MapleInventoryManipulator.addById(
                                          c,
                                          itemId,
                                          quantity,
                                          "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date()
                                       );
                                    }
                                 } else {
                                    MapleInventoryManipulator.addById(
                                       c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date()
                                    );
                                 }
                              } else if (item.getItemId() != 5068300 && item.getItemId() != 5680157) {
                                 MapleInventoryManipulator.addById(
                                    c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date()
                                 );
                              } else {
                                 Item wonderBerry = new Item(
                                    item.getItemId(), (short)1, (short)(item.getQuantity() * bundle), (short)ItemFlag.KARMA_USE.getValue()
                                 );
                                 MapleInventoryManipulator.addFromDrop(c, wonderBerry, true);
                              }
                           }

                           if (item.getPointQuestExID() == QuestExConstants.KillPoint.getQuestID()) {
                              c.getPlayer().gainKillPoint(-item.getPointPrice() * 100 * bundle, 7, false);
                           }

                           if (item.getPointQuestExID() == 17015) {
                              c.getPlayer().gainRealCash(-item.getPointPrice() * bundle, false);
                           }

                           if (item.getPointQuestExID() == 16180) {
                              c.getPlayer().gainHongboPoint(-item.getPointPrice() * bundle, false);
                           }

                           if (DBConfig.isGanglim) {
                              if (item.getPointQuestExID() == 100777) {
                                 c.getPlayer().updateOneInfo(100711, "sum", String.valueOf(c.getPlayer().getOneInfoQuestInteger(100777, "point") * 100));
                              }

                              if (item.getPointQuestExID() == 100778) {
                                 c.getPlayer().updateOneInfo(100712, "sum", String.valueOf(c.getPlayer().getOneInfoQuestInteger(100778, "point")));
                              }
                           }

                           if (item.getPointQuestExID() == 3887) {
                              c.getPlayer().getPraisePoint().setPoint(c.getPlayer().getPraisePoint().getPoint() - item.getPointPrice() * bundle);
                              c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.PRAISE_POINT.getFlag());
                           }
                        } else {
                           c.getPlayer().dropMessage(1, "인벤토리가 꽉 찼습니다.");
                        }

                        c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
                     }
                  } else {
                     if (item != null && item.getPrice() > 0L && item.getReqItem() == 0) {
                        if (item.getRank() >= 0) {
                           boolean passed = true;
                           int y = 0;

                           for (Pair i : this.getRanks()) {
                              if (c.getPlayer().haveItem((Integer)i.left, 1, true, true) && item.getRank() >= y) {
                                 passed = true;
                                 break;
                              }

                              y++;
                           }

                           if (!passed) {
                              c.getPlayer().dropMessage(1, "You need a higher rank.");
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }
                        }

                        long pricex = GameConstants.isRechargable(itemId) ? item.getPrice() : item.getPrice() * bundle;
                        if (pricex >= 0L && c.getPlayer().getMeso() >= pricex) {
                           if (c.getPlayer().getStat().disCountR > 0
                              && (item.getItemId() < 4001713 || item.getItemId() > 4001716)
                              && item.getItemId() != 4001785
                              && item.getItemId() != 4001784
                              && item.getItemId() != 4001759) {
                              pricex -= (int)(pricex * 0.01 * c.getPlayer().getStat().disCountR);
                           }

                           if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                              if (NpcID.allNpcIDList.contains(this.getNpcId())) {
                                 AchievementFactory.checkNpcShopBuy(c.getPlayer(), itemId, this.getNpcId(), pricex);
                              }

                              c.getPlayer().gainMeso(-pricex, false);
                              if (GameConstants.isPet(itemId)) {
                                 Item items = new Item(itemId, (short)1, (short)1, 0);
                                 items.setExpiration(System.currentTimeMillis() + 2592000000L);
                                 MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                                 items.setPet(pet);
                                 items.setUniqueId(pet.getUniqueId());
                                 MapleInventoryManipulator.addFromDrop(c, items, false);
                              } else {
                                 if (GameConstants.isRechargable(itemId)) {
                                    quantity = ii.getSlotMax(item.getItemId());
                                 }

                                 MapleInventoryManipulator.addById(
                                    c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date()
                                 );
                              }
                           } else {
                              c.getPlayer().dropMessage(1, "인벤토리가 꽉 찼습니다.");
                           }

                           c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
                        }
                     } else if (item != null && item.getReqItem() > 0 && c.getPlayer().haveItem(item.getReqItem(), item.getReqItemQ(), false, true)) {
                        if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                           MapleInventoryManipulator.removeById(
                              c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ() * bundle, false, false
                           );
                           if (DBConfig.isGanglim) {
                              if (item.getReqItem() == 4310308) {
                                 int vv = c.getPlayer().getItemQuantity(4310308, false);
                                 c.getPlayer().updateOneInfo(QuestExConstants.NeoCoreEvent.getQuestID(), "point", String.valueOf(vv));
                              } else if (item.getReqItem() == 4310229) {
                                 int vv = c.getPlayer().getItemQuantity(4310229, false);
                                 c.getPlayer().updateOneInfo(QuestExConstants.UnionCoin.getQuestID(), "point", String.valueOf(vv));
                              }
                           }

                           if (NpcID.allNpcIDList.contains(this.getNpcId())) {
                              AchievementFactory.checkNpcShopBuy(c.getPlayer(), itemId, this.getNpcId(), 1L);
                           }

                           if (GameConstants.isPet(itemId)) {
                              Item items = new Item(itemId, (short)1, (short)1, 0);
                              items.setExpiration(System.currentTimeMillis() + 2592000000L);
                              MaplePet pet = MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance());
                              items.setPet(pet);
                              items.setUniqueId(pet.getUniqueId());
                              MapleInventoryManipulator.addFromDrop(c, items, false);
                           } else {
                              if (GameConstants.isRechargable(itemId)) {
                                 quantity = ii.getSlotMax(item.getItemId());
                              }

                              MapleInventoryManipulator.addById(
                                 c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           }
                        } else {
                           c.getPlayer().dropMessage(1, "인벤토리가 꽉 찼습니다.");
                        }

                        c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
                     }
                  }
               }
            }
         }
      }
   }

   public void sell(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean sendpacket) {
      if (quantity > -1 && quantity != 0) {
         Item item = c.getPlayer().getInventory(type).getItem(slot);
         if (item != null && this.id != 9418) {
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
               quantity = item.getQuantity();
            }

            if (quantity <= c.getPlayer().getInventory(type).getItem(slot).getQuantity()) {
               short iQuant = item.getQuantity();
               if (iQuant > -1) {
                  MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                  if (!ii.cantSell(item.getItemId()) && !GameConstants.isPet(item.getItemId())) {
                     if (quantity <= iQuant && iQuant > 0 || GameConstants.isRechargable(item.getItemId())) {
                        Item itemm = item.copy();
                        itemm.setQuantity(quantity);
                        double price;
                        if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
                           price = ii.getPrice(item.getItemId());
                        } else {
                           price = (double)ii.getWholePrice(item.getItemId()) / ii.getSlotMax(item.getItemId());
                        }

                        for (IntensePowerCrystal v : new ArrayList<>(c.getPlayer().getIntensePowerCrystals().values())) {
                           if (v.getItemUniqueID() == item.getUniqueId()) {
                              price = v.getPrice() / v.getMemberCount();
                              break;
                           }
                        }

                        if (GameConstants.isIntensePowerCrystal(item.getItemId()) && !DBConfig.isGanglim) {
                           int vx = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.IntensePowerCrystal.getQuestID(), "count");
                           if (vx <= 0) {
                              return;
                           }

                           c.getPlayer().updateOneInfo(QuestExConstants.IntensePowerCrystal.getQuestID(), "count", String.valueOf(Math.max(0, vx - 1)));
                        }

                        MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                        long recvMesos = (long)Math.max(Math.ceil(price * quantity), 0.0);
                        itemm.setRebuyPrice(recvMesos);
                        c.getPlayer().getRebuy().add(itemm);
                        if (price != -1.0 && recvMesos > 0L) {
                           c.getPlayer().gainMeso(recvMesos, false);
                        }

                        long serialNum = 0L;
                        boolean saveLog = true;
                        if (DBConfig.isGanglim && item.getItemId() == 4001886) {
                           saveLog = false;
                        }

                        if (saveLog) {
                           StringBuilder sb = new StringBuilder();
                           sb.append("상점에 아이템 판매 시도 (캐릭터 : ");
                           sb.append(c.getPlayer().getName());
                           sb.append(", 계정 : ");
                           sb.append(c.getPlayer().getClient().getAccountName());
                           sb.append(", 상점ID : ");
                           sb.append(this.getId());
                           sb.append(", 아이템 : ");
                           sb.append(item.getItemId());
                           sb.append(" ");
                           sb.append((int)quantity);
                           sb.append("개, ");
                           if (type == MapleInventoryType.EQUIP) {
                              Equip equip = (Equip)item;
                              serialNum = equip.getSerialNumberEquip();
                              sb.append("(정보 : ");
                              sb.append(equip.toString());
                           }

                           sb.append("), 가격 : ");
                           sb.append(recvMesos);
                           sb.append(" 메소)");
                           LoggingManager.putLog(
                              new ItemLog(
                                 c.getPlayer(),
                                 c.getChannel(),
                                 item.getItemId(),
                                 quantity,
                                 c.getPlayer().getMapId(),
                                 ConsumeLogType.NpcShopSell.getType(),
                                 recvMesos,
                                 serialNum,
                                 sb
                              )
                           );
                        }

                        if (sendpacket) {
                           c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)11, this, c, -1));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void recharge(MapleClient c, short slot) {
      Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
      if (item != null && (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()))) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         short slotMax = ii.getSlotMax(item.getItemId());
         int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());
         if (skill != 0) {
            slotMax = (short)(slotMax + c.getPlayer().getTotalSkillLevel(SkillFactory.getSkill(skill)) * 10);
         }

         if (item.getQuantity() < slotMax) {
            int price = (int)Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
               item.setQuantity(slotMax);
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventorySlot(MapleInventoryType.USE, item, false));
               c.getPlayer().gainMeso(-price, false, false, true);
               c.getSession().writeAndFlush(CField.NPCPacket.confirmShopTransaction((byte)0, this, c, -1));
            }
         }
      }
   }

   protected MapleShopItem findById(int itemId) {
      for (MapleShopItem item : this.items) {
         if (item.getItemId() == itemId) {
            return item;
         }
      }

      return null;
   }

   protected MapleShopItem findBySlot(short slot) {
      int i = 0;

      for (MapleShopItem item : this.items) {
         if (i == slot) {
            return item;
         }

         i++;
      }

      return null;
   }

   public static MapleShop createFromDB(int id, boolean isShopId) {
      MapleShop ret = null;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      DBConnection db = new DBConnection();

      try {
         List<Integer> recharges;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               int shopId = rs.getInt("shopid");
               ret = new MapleShop(shopId, rs.getInt("npcid"));
               rs.close();
               ps.close();
               ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
               ps.setInt(1, shopId);
               rs = ps.executeQuery();
               recharges = new ArrayList<>(rechargeableItems);

               while (rs.next()) {
                  if (ii.itemExists(rs.getInt("itemid"))) {
                     if (!GameConstants.isThrowingStar(rs.getInt("itemid")) && !GameConstants.isBullet(rs.getInt("itemid"))) {
                        ret.addItem(
                           new MapleShopItem(
                              rs.getInt("shopitemid"),
                              rs.getShort("buyable"),
                              rs.getShort("quantity"),
                              rs.getInt("itemid"),
                              rs.getInt("price"),
                              (short)rs.getInt("position"),
                              rs.getInt("reqitem"),
                              rs.getInt("reqitemq"),
                              rs.getByte("rank"),
                              rs.getInt("category"),
                              rs.getInt("minLevel"),
                              rs.getInt("expiration"),
                              false,
                              rs.getInt("pointQuestEx"),
                              rs.getInt("pointPrice"),
                              rs.getInt("buyLimit"),
                              rs.getInt("worldBuyLimit"),
                              rs.getInt("limitQuestExID"),
                              rs.getString("limitQuestEXKey"),
                              rs.getInt("limitQuestExValue")
                           )
                        );
                     } else {
                        MapleShopItem starItem = new MapleShopItem(
                           rs.getInt("shopitemid"),
                           rs.getShort("buyable"),
                           ii.getSlotMax(rs.getInt("itemid")),
                           rs.getInt("itemid"),
                           rs.getInt("price"),
                           (short)rs.getInt("position"),
                           rs.getInt("reqitem"),
                           rs.getInt("reqitemq"),
                           rs.getByte("rank"),
                           rs.getInt("category"),
                           rs.getInt("minLevel"),
                           rs.getInt("expiration"),
                           false,
                           rs.getInt("pointQuestEx"),
                           rs.getInt("pointPrice"),
                           rs.getInt("buyLimit"),
                           rs.getInt("worldBuyLimit"),
                           rs.getInt("limitQuestExID"),
                           rs.getString("limitQuestEXKey"),
                           rs.getInt("limitQuestExValue")
                        );
                        ret.addItem(starItem);
                        if (rechargeableItems.contains(starItem.getItemId())) {
                           recharges.remove(Integer.valueOf(starItem.getItemId()));
                        }
                     }
                  }
               }

               for (Integer recharge : recharges) {
                  ret.addItem(
                     new MapleShopItem(0, (short)1, ii.getSlotMax(recharge), recharge, 0L, (short)0, 0, 0, (byte)0, 0, 0, 0, false, 0, 0, 0, 0, 0, "", 0)
                  );
               }

               rs.close();
               ps.close();
               ps = con.prepareStatement("SELECT * FROM shopranks WHERE shopid = ? ORDER BY rank ASC");
               ps.setInt(1, shopId);
               rs = ps.executeQuery();

               while (rs.next()) {
                  if (ii.itemExists(rs.getInt("itemid"))) {
                     ret.ranks.add(new Pair<>(rs.getInt("itemid"), rs.getString("name")));
                  }
               }

               rs.close();
               ps.close();
               return ret;
            }

            rs.close();
            ps.close();
            recharges = null;
         }

         return ret;
      } catch (SQLException var14) {
         System.err.println("Could not load shop" + var14);
         return ret;
      }
   }

   public int getNpcId() {
      return this.npcId;
   }

   public int getId() {
      return this.id;
   }

   public List<Pair<Integer, String>> getRanks() {
      return this.ranks;
   }

   private void isUnionShop(MapleClient c, int id) {
      if (id == 9010107) {
         int vv = c.getPlayer().getItemQuantity(4310229, false);
         c.getPlayer().updateOneInfo(QuestExConstants.UnionCoin.getQuestID(), "point", String.valueOf(vv));
      }
   }

   static {
      rechargeableItems.add(2070000);
      rechargeableItems.add(2070001);
      rechargeableItems.add(2070002);
      rechargeableItems.add(2070003);
      rechargeableItems.add(2070004);
      rechargeableItems.add(2070005);
      rechargeableItems.add(2070006);
      rechargeableItems.add(2070007);
      rechargeableItems.add(2070008);
      rechargeableItems.add(2070009);
      rechargeableItems.add(2070010);
      rechargeableItems.add(2070011);
      rechargeableItems.add(2070023);
      rechargeableItems.add(2070024);
      rechargeableItems.add(2330000);
      rechargeableItems.add(2330001);
      rechargeableItems.add(2330002);
      rechargeableItems.add(2330003);
      rechargeableItems.add(2330004);
      rechargeableItems.add(2330005);
      rechargeableItems.add(2330008);
      rechargeableItems.add(2331000);
      rechargeableItems.add(2332000);
   }
}
