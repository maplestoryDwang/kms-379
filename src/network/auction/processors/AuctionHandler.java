package network.auction.processors;

import constants.GameConstants;
import constants.ServerConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import logging.LoggingManager;
import logging.entry.AuctionLog;
import logging.entry.AuctionLogType;
import network.auction.Auction;
import network.auction.AuctionAlarmType;
import network.auction.AuctionHistoryIDManager;
import network.auction.AuctionItemPackage;
import network.auction.AuctionMessage;
import network.auction.AuctionSearchManager;
import network.auction.AuctionServer;
import network.center.Center;
import network.center.CharacterTransfer;
import network.decode.PacketDecoder;
import network.game.GameServer;
import network.login.LoginServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.enchant.EquipSpecialAttribute;
import objects.users.enchant.ItemFlag;
import objects.utils.AdminClient;
import objects.utils.CurrentTime;
import objects.utils.Pair;

public class AuctionHandler {
   public static final void EnterAuction(int playerID, MapleClient c, boolean noGame) {
      CharacterTransfer transfer = AuctionServer.getPlayerStorage().getPendingCharacter(playerID);
      MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
      c.setPlayer(chr);
      c.setAccID(chr.getAccountID());
      if (c.CheckIPAddress()) {
         boolean allowLogin = true;
         if (!allowLogin) {
            c.setPlayer(null);
         } else if (noGame) {
            c.updateLoginState(0, c.getSessionIPAddress());
            c.clearInformation();
            c.disconnect(false);
         } else {
            c.updateLoginState(2, c.getSessionIPAddress());
            AuctionServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().writeAndFlush(CField.AuctionPacket.stageSetAuction(chr));
            c.sendPing();
            if (ServerConstants.useAdminClient) {
               AdminClient.updatePlayerList();
            }
         }
      }
   }

   public static void LeaveAuction(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      AuctionServer.getPlayerStorage().deregisterPlayer(chr);
      c.updateLoginState(1, c.getSessionIPAddress());

      try {
         Center.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
         c.getSession().writeAndFlush(CField.getChannelChange(c,
               Integer.parseInt(GameServer.getInstance(c.getChannel()).getIP().split(":")[1])));
      } finally {
         String s = c.getSessionIPAddress();
         LoginServer.addIPAuth(s.substring(s.indexOf(47) + 1, s.length()));
         chr.saveToDB(true, true);
         c.setPlayer(null);
         c.setReceiving(false);
      }
   }

   public static final void auctionRequestHandler(PacketDecoder slea, MapleClient c) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int op = slea.readInt();
      if (AuctionServer.getPlayerStorage().getCharacterById(c.getPlayer().getId()) == null) {
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "알 수 없는 오류가 발생하였습니다.\r\n경매장 재접속 부탁드립니다."));
      } else {
         switch (op) {
            case 0:
               c.getSession().writeAndFlush(new Auction.MyHistory(
                     Center.Auction.getHistoryItems(c.getAccID(), c.getPlayer().getId()), c.getPlayer()).encode());
               c.getSession().writeAndFlush(new Auction.MyItemList(
                     Center.Auction.getMyItems(c.getAccID(), c.getPlayer().getId()), c.getPlayer()).encode());
               c.getSession()
                     .writeAndFlush(new Auction.WishList(c.getPlayer().getAuctionWishList(), c.getPlayer()).encode());
               c.getSession().writeAndFlush(new Auction.NotInitialize().encode());
               c.getSession()
                     .writeAndFlush(new Auction.MarketPriceList(
                           Center.Auction.getMarketPriceItems(c.getPlayer().getId()), c.getPlayer(), 1000).encode());
               c.getSession().writeAndFlush(
                     new Auction.RegisterSaleItemList(Center.Auction.getItems(), c.getPlayer()).encode());
               break;
            case 1:
               LeaveAuction(slea, c, c.getPlayer());
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 42:
            case 43:
            case 44:
            case 48:
            case 49:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            default:
               System.out.println(
                     "Unhandled 경매 action by " + c.getPlayer().getName() + " : " + op + ", " + slea.toString());
               break;
            case 10: {
               slea.skip(4);
               int itemID = slea.readInt();
               int quantity = slea.readInt();
               long meso = slea.readLong();
               int duration = slea.readInt();
               byte inv = slea.readByte();
               short slot = slea.readShort();
               boolean isPremiumUser = isPremiumUser(c.getPlayer());
               int limitCount = isPremiumUser ? 20 : 10;
               Item itemxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.getByType(inv)).getItem(slot);
               if (GameConstants.isEquip(itemxxxxxxxx.getItemId())
                     && (((Equip) itemxxxxxxxx).getSpecialAttribute() & EquipSpecialAttribute.VESTIGE.getType()) > 0) {
                  c.getPlayer().dropMessage(1, "장비의 흔적은 판매 등록이 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               if (quantity <= 0 || System.currentTimeMillis() - c.getPlayer().getChangeEmotionTime() <= 3000L) {
                  c.getPlayer().ban("아이템 복사 핵 사용으로 인한 영구밴", true, true, true);
                  return;
               }

               if (itemxxxxxxxx == null || itemxxxxxxxx.getItemId() != itemID || itemxxxxxxxx.getQuantity() < quantity
                     || c.getPlayer().getMeso() < 2000L) {
                  c.getPlayer().dropMessage(1, "오류가 발생했습니다.");
                  return;
               }

               if (itemxxxxxxxx.getItemId() / 1000000 == 1) {
                  Equip e = (Equip) itemxxxxxxxx;
                  if (e.getCashEnchantCount() > 0) {
                     c.getPlayer().dropMessage(1, "치장 옵션 부여를 적용한 아이템은 등록할 수 없습니다.");
                     c.getSession().writeAndFlush(
                           new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                     return;
                  }
               }

               if (GameConstants.isForbiddenListAuctionItem(itemxxxxxxxx.getItemId())) {
                  c.getPlayer().dropMessage(1, "해당 아이템은 등록이 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               StringBuilder sb = new StringBuilder();
               sb.append("경매장 아이템 등록 (계정 : ");
               sb.append(c.getAccountName());
               sb.append(", 캐릭터 : ");
               sb.append(c.getPlayer().getName());
               sb.append(", 등록 가격 : ");
               sb.append(meso);
               sb.append(", 아이템 : ");
               sb.append(itemxxxxxxxx.getItemId());
               sb.append(" ");
               sb.append(itemxxxxxxxx.getQuantity());
               sb.append("개");
               long serialNumber = 0L;
               if (itemxxxxxxxx instanceof Equip) {
                  sb.append(" (정보 : ");
                  serialNumber = ((Equip) itemxxxxxxxx).getSerialNumberEquip();
                  sb.append(((Equip) itemxxxxxxxx).toString());
               }

               sb.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(), itemxxxxxxxx.getItemId(), itemxxxxxxxx.getQuantity(), 0L, serialNumber,
                           AuctionLogType.RegisterItem.getType(), sb));
               if (itemxxxxxxxx.getPet() != null) {
                  if (itemxxxxxxxx.getExpiration() > System.currentTimeMillis()
                        && itemxxxxxxxx.getExpiration() - System.currentTimeMillis() <= 31536000000L) {
                     c.getSession().writeAndFlush(
                           new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                     return;
                  }

                  if (itemxxxxxxxx.getQuantity() > 1 || quantity > 1) {
                     itemxxxxxxxx.setQuantity((short) 1);
                     quantity = 1;
                  }

                  c.getPlayer().unequipPet(itemxxxxxxxx.getPet(), false, false, 0);
               }

               if (itemxxxxxxxx.getQuantity() <= 0) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                  c.getPlayer().ban("아이템 복사 핵 사용으로 인한 영구밴", true, true, true);
                  return;
               }

               Item copyItem = itemxxxxxxxx.copy();
               copyItem.setQuantity((short) quantity);
               AuctionItemPackage aitem = new AuctionItemPackage(
                     c.getPlayer().getId(),
                     c.getAccID(),
                     c.getPlayer().getName(),
                     copyItem,
                     0L,
                     meso,
                     System.currentTimeMillis() + duration * 60 * 60 * 1000,
                     false,
                     0,
                     0L,
                     System.currentTimeMillis(),
                     0,
                     0);
               Center.Auction.addItem(aitem);
               c.getPlayer().getInventory(MapleInventoryType.getByType(inv)).removeItem(slot, (short) quantity, false);
               c.getPlayer().setMeso(c.getPlayer().getMeso() - 2000L);
               long flag = -1L;
               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), flag));
               c.getSession().writeAndFlush(new Auction.RegisterItem(aitem, c.getPlayer()).encode());
               c.getSession().writeAndFlush(new Auction.RegisterItemDone(AuctionMessage.SUCCESS.getValue(),
                     (int) aitem.getItem().getInventoryId()).encode());
            }
               break;
            case 11: {
               int auctionInventoryID = slea.readInt();
               slea.skip(4);
               slea.skip(4);
               int accountID = slea.readInt();
               int playerID = slea.readInt();
               int itemIDx = slea.readInt();
               int status = slea.readInt();
               long mesox = slea.readLong();
               long time = slea.readLong();
               boolean isPremiumUserx = isPremiumUser(c.getPlayer());
               int limitCountx = isPremiumUserx ? 20 : 10;
               AuctionItemPackage itemxxxxxxxxx = Center.Auction.getItemByHistoryID(auctionInventoryID);
               if (itemxxxxxxxxx == null) {
                  return;
               }

               if (GameConstants.isEquip(itemxxxxxxxxx.getItem().getItemId())
                     && (((Equip) itemxxxxxxxxx.getItem()).getSpecialAttribute()
                           & EquipSpecialAttribute.VESTIGE.getType()) > 0) {
                  c.getPlayer().dropMessage(1, "장비의 흔적은 재등록이 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               if (GameConstants.isForbiddenListAuctionItem(itemxxxxxxxxx.getItem().getItemId())) {
                  c.getPlayer().dropMessage(1, "해당 아이템은 등록이 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               if (itemxxxxxxxxx.getItem().getPet() != null
                     && itemxxxxxxxxx.getItem().getExpiration() > System.currentTimeMillis()
                     && itemxxxxxxxxx.getItem().getExpiration() - System.currentTimeMillis() <= 31536000000L) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                  return;
               }

               if (itemxxxxxxxxx.getItem().getQuantity() <= 0) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                  c.getPlayer().ban("아이템 복사 핵 사용으로 인한 영구밴", true, true, true);
                  return;
               }

               if (playerID != c.getPlayer().getId()
                     || itemIDx != itemxxxxxxxxx.getItem().getItemId()
                     || status != itemxxxxxxxxx.getType(itemxxxxxxxxx.getOwnerId() == c.getPlayer().getId(), true)) {
                  return;
               }

               if (Center.Auction.getBuyAllItemsCount(c.getPlayer().getId()) >= limitCountx) {
                  return;
               }

               if (c.getPlayer().getMeso() < 2000L) {
                  return;
               }

               itemxxxxxxxxx.setType(9);
               c.getSession().writeAndFlush(new Auction.RegisterHistoryItem(itemxxxxxxxxx, c.getPlayer()).encode());
               Item item_ = itemxxxxxxxxx.getItem().copy();
               AuctionItemPackage aitem = new AuctionItemPackage(
                     itemxxxxxxxxx.getOwnerId(),
                     itemxxxxxxxxx.getAccountID(),
                     itemxxxxxxxxx.getOwnerName(),
                     item_,
                     0L,
                     itemxxxxxxxxx.getMesos(),
                     System.currentTimeMillis() + (isPremiumUserx ? 48 : 24) * 60 * 60 * 1000,
                     false,
                     0,
                     0L,
                     System.currentTimeMillis(),
                     0,
                     0);
               Center.Auction.addItem(aitem);
               StringBuilder sbx = new StringBuilder();
               sbx.append("경매장 아이템 재등록 (계정 : ");
               sbx.append(c.getAccountName());
               sbx.append(", 캐릭터 : ");
               sbx.append(c.getPlayer().getName());
               sbx.append(", 등록 가격 : ");
               sbx.append(aitem.getMesos());
               sbx.append(", 아이템 : ");
               sbx.append(itemxxxxxxxxx.getItem().getItemId());
               sbx.append(" ");
               sbx.append(itemxxxxxxxxx.getItem().getQuantity());
               sbx.append("개");
               long serialNumberx = 0L;
               if (itemxxxxxxxxx.getItem() instanceof Equip) {
                  sbx.append(" (정보 : ");
                  serialNumberx = ((Equip) itemxxxxxxxxx.getItem()).getSerialNumberEquip();
                  sbx.append(((Equip) itemxxxxxxxxx.getItem()).toString());
               }

               sbx.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(),
                           itemxxxxxxxxx.getItem().getItemId(),
                           itemxxxxxxxxx.getItem().getQuantity(),
                           0L,
                           serialNumberx,
                           AuctionLogType.ReRegisterItem.getType(),
                           sbx));
               aitem.setExpiredTime(System.currentTimeMillis() + 86400000L);
               c.getPlayer().setMeso(c.getPlayer().getMeso() - 2000L);
               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
               c.getSession().writeAndFlush(new Auction.RegisterItem(aitem, c.getPlayer()).encode());
               c.getSession()
                     .writeAndFlush(new Auction.ReRegisterItemDone((int) aitem.getItem().getInventoryId()).encode());
            }
               break;
            case 12: {
               int auctionInventoryIDx = slea.readInt();
               AuctionItemPackage itemxxxxxxxxxx = Center.Auction.getItem(auctionInventoryIDx);
               if (itemxxxxxxxxxx == null) {
                  return;
               }

               int type = itemxxxxxxxxxx.getType(true, true);
               if (type == 2 || type == 4 || type == 9 || type == 13 || type == 14 || type == 15) {
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               itemxxxxxxxxxx.setType(4);
               itemxxxxxxxxxx.setBuyTime(System.currentTimeMillis());
               itemxxxxxxxxxx.setHistoryID(AuctionHistoryIDManager.getAndIncrement());
               int flag = 2;
               StringBuilder sbx = new StringBuilder();
               sbx.append("경매장 아이템 등록 취소 (계정 : ");
               sbx.append(c.getAccountName());
               sbx.append(", 캐릭터 : ");
               sbx.append(c.getPlayer().getName());
               sbx.append(", 아이템 : ");
               sbx.append(itemxxxxxxxxxx.getItem().getItemId());
               sbx.append(" ");
               sbx.append(itemxxxxxxxxxx.getItem().getQuantity());
               sbx.append("개");
               long serialNumberx = 0L;
               if (itemxxxxxxxxxx.getItem() instanceof Equip) {
                  sbx.append(" (정보 : ");
                  serialNumberx = ((Equip) itemxxxxxxxxxx.getItem()).getSerialNumberEquip();
                  sbx.append(((Equip) itemxxxxxxxxxx.getItem()).toString());
               }

               sbx.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(),
                           itemxxxxxxxxxx.getItem().getItemId(),
                           itemxxxxxxxxxx.getItem().getQuantity(),
                           0L,
                           serialNumberx,
                           AuctionLogType.RegisterCancel.getType(),
                           sbx));
               c.getPlayer().setMeso(c.getPlayer().getMeso() + 2000L);
               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
               c.getSession().writeAndFlush(new Auction.RegisterItem(itemxxxxxxxxxx, c.getPlayer()).encode());
               c.getSession().writeAndFlush(new Auction.RegisterHistoryItem(itemxxxxxxxxxx, c.getPlayer()).encode());
               c.getSession().writeAndFlush(
                     new Auction.CancelItemDone((int) itemxxxxxxxxxx.getItem().getInventoryId()).encode());
            }
               break;
            case 20: {
               int auctionInventoryIDxx = slea.readInt();
               long mesoxx = slea.readLong();
               AuctionItemPackage itemxxxxxxxxxxx = Center.Auction.getItem(auctionInventoryIDxx);
               if (itemxxxxxxxxxxx == null) {
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (c.getPlayer().getMeso() < mesoxx) {
                  c.getSession().writeAndFlush(
                        new Auction.BuyItemDone(AuctionMessage.NOT_ENOUGH_MONEY.getValue(), 0L).encode());
                  return;
               }

               if (itemxxxxxxxxxxx.getBuyer() != 0) {
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (itemxxxxxxxxxxx.getItem().getQuantity() <= 0) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                  return;
               }

               if (itemxxxxxxxxxxx.getItem().getPet() != null
                     && itemxxxxxxxxxxx.getItem().getExpiration() > System.currentTimeMillis()
                     && itemxxxxxxxxxxx.getItem().getExpiration() - System.currentTimeMillis() <= 31536000000L) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                  return;
               }

               if (itemxxxxxxxxxxx.getAccountID() == c.getAccID()) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.CAN_NOT_BUY_OWN_ITEM.getValue(), 0).encode());
                  return;
               }

               if (GameConstants.isEquip(itemxxxxxxxxxxx.getItem().getItemId())
                     && (((Equip) itemxxxxxxxxxxx.getItem()).getSpecialAttribute()
                           & EquipSpecialAttribute.VESTIGE.getType()) > 0) {
                  c.getPlayer().dropMessage(1, "장비의 흔적은 구매가 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               if (GameConstants.isForbiddenListAuctionItem(itemxxxxxxxxxxx.getItem().getItemId())) {
                  c.getPlayer().dropMessage(1, "해당 아이템은 구매가 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               int typex = itemxxxxxxxxxxx.getType(true, true);
               if (typex == 2 || typex == 4 || typex == 9 || typex == 13 || typex == 14 || typex == 15) {
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               if (mesoxx / itemxxxxxxxxxxx.getItem().getQuantity() != itemxxxxxxxxxxx.getMesos()
                     && !ii.isCash(itemxxxxxxxxxxx.getItem().getItemId())) {
                  StringBuilder sbx = new StringBuilder();
                  sbx.append(
                        "경매장 아이템 가격 수정 핵 사용 / 등록된 가격 : "
                              + itemxxxxxxxxxxx.getMesos()
                              + ", 클라이언트 가격 : "
                              + mesoxx / itemxxxxxxxxxxx.getItem().getQuantity()
                              + " [이름 : "
                              + c.getPlayer().getName()
                              + "]");
                  sbx.append(c.getAccountName());
                  sbx.append(", 아이템 : ");
                  sbx.append(itemxxxxxxxxxxx.getItem().getItemId());
                  sbx.append(" ");
                  sbx.append(itemxxxxxxxxxxx.getItem().getQuantity());
                  sbx.append("개");
                  long serialNumberx = 0L;
                  if (itemxxxxxxxxxxx.getItem() instanceof Equip) {
                     sbx.append(" (정보 : ");
                     serialNumberx = ((Equip) itemxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                     sbx.append(((Equip) itemxxxxxxxxxxx.getItem()).toString());
                  }

                  sbx.append("), 판매자 : ");
                  sbx.append(itemxxxxxxxxxxx.getOwnerName());
                  sbx.append(")");
                  LoggingManager.putLog(
                        new AuctionLog(
                              c.getPlayer(),
                              itemxxxxxxxxxxx.getItem().getItemId(),
                              itemxxxxxxxxxxx.getItem().getQuantity(),
                              0L,
                              serialNumberx,
                              AuctionLogType.HackLog.getType(),
                              sbx));
                  c.disconnect(false);
                  return;
               }

               boolean isPremiumUserxx = isPremiumUser(c.getPlayer());
               int limitCountxx = isPremiumUserxx ? 20 : 10;
               StringBuilder sbx = new StringBuilder();
               sbx.append("경매장 아이템 구매완료 (계정 : ");
               sbx.append(c.getAccountName());
               sbx.append(", 캐릭터 : ");
               sbx.append(c.getPlayer().getName());
               sbx.append(", 아이템 : ");
               sbx.append(itemxxxxxxxxxxx.getItem().getItemId());
               sbx.append(" ");
               sbx.append(itemxxxxxxxxxxx.getItem().getQuantity());
               sbx.append("개");
               long serialNumberx = 0L;
               if (itemxxxxxxxxxxx.getItem() instanceof Equip) {
                  sbx.append(" (정보 : ");
                  serialNumberx = ((Equip) itemxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                  sbx.append(((Equip) itemxxxxxxxxxxx.getItem()).toString());
               }

               sbx.append("), 판매자 : ");
               sbx.append(itemxxxxxxxxxxx.getOwnerName());
               sbx.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(),
                           itemxxxxxxxxxxx.getItem().getItemId(),
                           itemxxxxxxxxxxx.getItem().getQuantity(),
                           mesoxx,
                           serialNumberx,
                           AuctionLogType.BuyItem.getType(),
                           sbx));
               StringBuilder sb2 = new StringBuilder(sbx.toString());
               sb2.replace(8, 12, "판매완료");
               int sellerAccID = itemxxxxxxxxxxx.getAccountID();
               String sellerAccName = "";
               int sellerChrID = itemxxxxxxxxxxx.getOwnerId();
               String sellerChrName = itemxxxxxxxxxxx.getOwnerName();
               LoggingManager.putLog(
                     new AuctionLog(
                           sellerAccID,
                           sellerAccName,
                           sellerChrID,
                           sellerChrName,
                           itemxxxxxxxxxxx.getItem().getItemId(),
                           ii.getName(itemxxxxxxxxxxx.getItem().getItemId()),
                           itemxxxxxxxxxxx.getItem().getQuantity(),
                           mesoxx,
                           serialNumberx,
                           AuctionLogType.SellDone.getType(),
                           sb2));
               itemxxxxxxxxxxx.setBuyer(c.getPlayer().getId());
               itemxxxxxxxxxxx.setType(2);
               itemxxxxxxxxxxx.setBuyTime(System.currentTimeMillis());
               itemxxxxxxxxxxx.setHistoryID(AuctionHistoryIDManager.getAndIncrement());
               c.getPlayer().setMeso(c.getPlayer().getMeso() - mesoxx);
               int itemFlag = itemxxxxxxxxxxx.getItem().getFlag();
               if ((itemFlag & ItemFlag.KARMA_EQ.getValue()) != 0 || (itemFlag & ItemFlag.KARMA_USE.getValue()) != 0) {
                  itemFlag |= ItemFlag.POSSIBLE_TRADING.getValue();
                  itemFlag &= ~ItemFlag.KARMA_EQ.getValue();
                  itemFlag &= ~ItemFlag.KARMA_USE.getValue();
                  itemxxxxxxxxxxx.getItem().setFlag(itemFlag);
               }

               int ownerID = itemxxxxxxxxxxx.getOwnerId();

               for (GameServer cs : GameServer.getAllInstances()) {
                  MapleCharacter player = cs.getPlayerStorage().getCharacterById(ownerID);
                  if (player != null) {
                     player.send(
                           CWvsContext.AuctionAlarm(
                                 AuctionAlarmType.Sold,
                                 itemxxxxxxxxxxx.getItem().getItemId(),
                                 mesoxx,
                                 itemxxxxxxxxxxx.getItem().getQuantity(),
                                 itemxxxxxxxxxxx.getItem().getQuantity()));
                     break;
                  }
               }

               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
               c.getSession().writeAndFlush(new Auction.BuyItem(itemxxxxxxxxxxx, c.getPlayer(), false).encode());
               c.getSession().writeAndFlush(new Auction.RegisterHistoryItem(itemxxxxxxxxxxx, c.getPlayer()).encode());
               c.getSession().writeAndFlush(new Auction.BuyItemDone(AuctionMessage.SUCCESS.getValue(),
                     itemxxxxxxxxxxx.getItem().getInventoryId()).encode());
            }
               break;
            case 21:
               int auctionInventoryIDxxx = slea.readInt();
               long mesoxxx = slea.readLong();
               int quantityx = slea.readInt();
               AuctionItemPackage itemxxxxxxxxxxxx = Center.Auction.getItem(auctionInventoryIDxxx);
               if (itemxxxxxxxxxxxx == null) {
                  c.getSession().writeAndFlush(
                        new Auction.BuyBundleItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (itemxxxxxxxxxxxx.getBuyer() != 0) {
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (itemxxxxxxxxxxxx.getItem().getQuantity() <= 0 || quantityx <= 0) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0).encode());
                  return;
               }

               if (itemxxxxxxxxxxxx.getItem().getQuantity() < quantityx) {
                  return;
               }

               if (GameConstants.isEquip(itemxxxxxxxxxxxx.getItem().getItemId())
                     && (((Equip) itemxxxxxxxxxxxx.getItem()).getSpecialAttribute()
                           & EquipSpecialAttribute.VESTIGE.getType()) > 0) {
                  c.getPlayer().dropMessage(1, "장비의 흔적은 구매가 불가능합니다.");
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               int typexx = itemxxxxxxxxxxxx.getType(true, true);
               if (typexx == 2 || typexx == 4 || typexx == 9 || typexx == 13 || typexx == 14 || typexx == 15) {
                  c.getSession()
                        .writeAndFlush(new Auction.BuyItemDone(AuctionMessage.UNKNOWN_ERROR.getValue(), 0L).encode());
                  return;
               }

               if (quantityx < 0 || System.currentTimeMillis() - c.getPlayer().getChangeEmotionTime() <= 3000L) {
                  c.getPlayer().ban("아이템 복사 핵 사용으로 인한 영구밴", true, true, true);
                  return;
               }

               if (mesoxxx / quantityx != itemxxxxxxxxxxxx.getMesos()) {
                  StringBuilder sbxx = new StringBuilder();
                  sbxx.append(
                        "경매장 아이템 가격 수정 핵 사용 / 등록된 가격 : "
                              + itemxxxxxxxxxxxx.getMesos()
                              + ", 클라이언트 가격 : "
                              + mesoxxx / itemxxxxxxxxxxxx.getItem().getQuantity()
                              + " [이름 : "
                              + c.getPlayer().getName()
                              + "]");
                  sbxx.append(c.getAccountName());
                  sbxx.append(", 아이템 : ");
                  sbxx.append(itemxxxxxxxxxxxx.getItem().getItemId());
                  sbxx.append(" ");
                  sbxx.append(itemxxxxxxxxxxxx.getItem().getQuantity());
                  sbxx.append("개");
                  long serialNumberxx = 0L;
                  if (itemxxxxxxxxxxxx.getItem() instanceof Equip) {
                     sbxx.append(" (정보 : ");
                     serialNumberxx = ((Equip) itemxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                     sbxx.append(((Equip) itemxxxxxxxxxxxx.getItem()).toString());
                  }

                  sbxx.append("), 판매자 : ");
                  sbxx.append(itemxxxxxxxxxxxx.getOwnerName());
                  sbxx.append(")");
                  LoggingManager.putLog(
                        new AuctionLog(
                              c.getPlayer(),
                              itemxxxxxxxxxxxx.getItem().getItemId(),
                              itemxxxxxxxxxxxx.getItem().getQuantity(),
                              0L,
                              serialNumberxx,
                              AuctionLogType.HackLog.getType(),
                              sbxx));
                  c.disconnect(false);
                  return;
               }

               if (c.getPlayer().getMeso() < mesoxxx) {
                  c.getSession().writeAndFlush(
                        new Auction.BuyItemDone(AuctionMessage.NOT_ENOUGH_MONEY.getValue(), 0L).encode());
                  return;
               }

               boolean isPremiumUserxxx = isPremiumUser(c.getPlayer());
               int limitCountxxx = isPremiumUserxxx ? 20 : 10;
               int itemFlag = itemxxxxxxxxxxxx.getItem().getFlag();
               if ((itemFlag & ItemFlag.KARMA_EQ.getValue()) != 0 || (itemFlag & ItemFlag.KARMA_USE.getValue()) != 0) {
                  itemFlag |= ItemFlag.POSSIBLE_TRADING.getValue();
                  itemFlag &= ~ItemFlag.KARMA_EQ.getValue();
                  itemFlag &= ~ItemFlag.KARMA_USE.getValue();
                  itemxxxxxxxxxxxx.getItem().setFlag(itemFlag);
               }

               Item copy = itemxxxxxxxxxxxx.getItem().copy();
               c.getPlayer().setMeso(c.getPlayer().getMeso() - mesoxxx);
               copy.setQuantity((short) quantityx);
               Item auctionItem = itemxxxxxxxxxxxx.getItem();
               itemxxxxxxxxxxxx.setBuyer(0);
               itemxxxxxxxxxxxx.setType(0);
               itemxxxxxxxxxxxx.setBuyTime(System.currentTimeMillis());
               int q = auctionItem.getQuantity() - quantityx;
               if (q <= 0) {
                  itemxxxxxxxxxxxx.setBuyer(c.getPlayer().getId());
                  itemxxxxxxxxxxxx.setType(2);
                  itemxxxxxxxxxxxx.setHistoryID(AuctionHistoryIDManager.getAndIncrement());
                  itemxxxxxxxxxxxx.setMesos(mesoxxx);
                  auctionItem.setQuantity((short) quantityx);
               } else {
                  auctionItem.setQuantity((short) q);
               }

               StringBuilder sbxx = new StringBuilder();
               sbxx.append("경매장 아이템 구매완료 (계정 : ");
               sbxx.append(c.getAccountName());
               sbxx.append(", 캐릭터 : ");
               sbxx.append(c.getPlayer().getName());
               sbxx.append(", 아이템 : ");
               sbxx.append(itemxxxxxxxxxxxx.getItem().getItemId());
               sbxx.append(" ");
               sbxx.append(quantityx);
               sbxx.append("개");
               long serialNumberxx = 0L;
               if (itemxxxxxxxxxxxx.getItem() instanceof Equip) {
                  sbxx.append(" (정보 : ");
                  serialNumberxx = ((Equip) itemxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                  sbxx.append(((Equip) itemxxxxxxxxxxxx.getItem()).toString());
               }

               sbxx.append("), 판매자 : ");
               sbxx.append(itemxxxxxxxxxxxx.getOwnerName());
               sbxx.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(), itemxxxxxxxxxxxx.getItem().getItemId(), quantityx, mesoxxx, serialNumberxx,
                           AuctionLogType.BuyItem.getType(), sbxx));
               StringBuilder sb2 = new StringBuilder(sbxx.toString());
               sb2.replace(8, 12, "판매완료");
               int sellerAccID = itemxxxxxxxxxxxx.getAccountID();
               String sellerAccName = "";
               int sellerChrID = itemxxxxxxxxxxxx.getOwnerId();
               String sellerChrName = itemxxxxxxxxxxxx.getOwnerName();
               LoggingManager.putLog(
                     new AuctionLog(
                           sellerAccID,
                           sellerAccName,
                           sellerChrID,
                           sellerChrName,
                           itemxxxxxxxxxxxx.getItem().getItemId(),
                           ii.getName(itemxxxxxxxxxxxx.getItem().getItemId()),
                           quantityx,
                           mesoxxx,
                           serialNumberxx,
                           AuctionLogType.SellDone.getType(),
                           sb2));
               if (q > 0) {
                  AuctionItemPackage aitem = new AuctionItemPackage(
                        itemxxxxxxxxxxxx.getOwnerId(),
                        itemxxxxxxxxxxxx.getAccountID(),
                        itemxxxxxxxxxxxx.getOwnerName(),
                        copy,
                        mesoxxx,
                        mesoxxx,
                        itemxxxxxxxxxxxx.getExpiredTime(),
                        itemxxxxxxxxxxxx.isBargain(),
                        c.getPlayer().getId(),
                        System.currentTimeMillis(),
                        itemxxxxxxxxxxxx.getStartTime(),
                        2,
                        0);
                  aitem.setHistoryID(AuctionHistoryIDManager.getAndIncrement());
                  Center.Auction.addItem(aitem);
                  c.getSession().writeAndFlush(new Auction.RegisterHistoryItem(aitem, c.getPlayer()).encode());
               }

               int ownerID = itemxxxxxxxxxxxx.getOwnerId();

               for (GameServer csx : GameServer.getAllInstances()) {
                  MapleCharacter player = csx.getPlayerStorage().getCharacterById(ownerID);
                  if (player != null) {
                     player.send(
                           CWvsContext.AuctionAlarm(
                                 AuctionAlarmType.Sold,
                                 itemxxxxxxxxxxxx.getItem().getItemId(),
                                 mesoxxx,
                                 itemxxxxxxxxxxxx.getItem().getQuantity() + quantityx,
                                 quantityx));
                     break;
                  }
               }

               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
               c.getSession().writeAndFlush(new Auction.BuyItem(itemxxxxxxxxxxxx, c.getPlayer(), false).encode());
               c.getSession().writeAndFlush(new Auction.UpdateSaleItem(itemxxxxxxxxxxxx, c.getPlayer()).encode());
               c.getSession().writeAndFlush(
                     new Auction.BuyBundleItemDone(AuctionMessage.SUCCESS.getValue(), auctionItem.getInventoryId())
                           .encode());
               break;
            case 30:
               int historyIDx = slea.readInt();
               slea.skip(4);
               slea.skip(4);
               int accountIDxx = slea.readInt();
               int playerIDxx = slea.readInt();
               int itemIDxxx = slea.readInt();
               int statusxx = slea.readInt();
               long mesoxxxxx = slea.readLong();
               long timexx = slea.readLong();
               long deposit = slea.readLong();
               AuctionItemPackage itemxxxxxxxxxxxxxx = Center.Auction.getItemByHistoryID(historyIDx);
               if (itemxxxxxxxxxxxxxx == null) {
                  c.getSession().writeAndFlush(
                        new Auction.PaymentReceiptDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (itemxxxxxxxxxxxxxx.getItem().getQuantity() <= 0) {
                  c.getPlayer().ban("경매장 수령 버그 핵 사용으로 인한 영구밴", true, true, true);
                  return;
               }

               if (playerIDxx != c.getPlayer().getId() || itemIDxxx != itemxxxxxxxxxxxxxx.getItem().getItemId()) {
                  return;
               }

               boolean isPremiumUserxxxx = isPremiumUser(c.getPlayer());
               boolean isOwnerx = itemxxxxxxxxxxxxxx.getOwnerId() == c.getPlayer().getId();
               if (itemxxxxxxxxxxxxxx.getType(isOwnerx, true) == 13
                     || itemxxxxxxxxxxxxxx.getType(isOwnerx, true) == 15) {
                  return;
               }

               long delta = 0L;
               int typexxxx = 0;
               if (statusxx == 3) {
                  delta = (long) Math.ceil(mesoxxxxx * (isPremiumUserxxxx ? 0.97 : 0.95));
                  if (itemxxxxxxxxxxxxxx.getType(isOwnerx, true) == 13
                        || itemxxxxxxxxxxxxxx.getType(isOwnerx, true) == 15) {
                     StringBuilder sbxxxx = new StringBuilder();
                     sbxxxx.append(
                           "이미 수령한 대금을 다시 수령 시도 : 아이템 (" + itemIDxxx + ") [이름 : " + c.getPlayer().getName() + "]");
                     sbxxxx.append(c.getAccountName());
                     sbxxxx.append(", 아이템 : ");
                     sbxxxx.append(itemxxxxxxxxxxxxxx.getItem().getItemId());
                     sbxxxx.append(" ");
                     sbxxxx.append(itemxxxxxxxxxxxxxx.getItem().getQuantity());
                     sbxxxx.append("개");
                     long serialNumberxxxx = 0L;
                     if (itemxxxxxxxxxxxxxx.getItem() instanceof Equip) {
                        sbxxxx.append(" (정보 : ");
                        serialNumberxxxx = ((Equip) itemxxxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                        sbxxxx.append(((Equip) itemxxxxxxxxxxxxxx.getItem()).toString());
                     }

                     sbxxxx.append("), 판매자 : ");
                     sbxxxx.append(itemxxxxxxxxxxxxxx.getOwnerName());
                     sbxxxx.append(")");
                     LoggingManager.putLog(
                           new AuctionLog(
                                 c.getPlayer(),
                                 itemxxxxxxxxxxxxxx.getItem().getItemId(),
                                 itemxxxxxxxxxxxxxx.getItem().getQuantity(),
                                 0L,
                                 serialNumberxxxx,
                                 AuctionLogType.HackLog.getType(),
                                 sbxxxx));
                     c.disconnect(false);
                     return;
                  }

                  if (itemxxxxxxxxxxxxxx.getType(isOwnerx, true) == 2) {
                     typexxxx = 13;
                  } else if (itemxxxxxxxxxxxxxx.getType(isOwnerx, true) == 14) {
                     typexxxx = 15;
                  }
               }

               if (deposit != 0L) {
                  delta += deposit;
               }

               itemxxxxxxxxxxxxxx.setType(typexxxx);
               c.getPlayer().setMeso(c.getPlayer().getMeso() + delta);
               StringBuilder sbxxxx = new StringBuilder();
               sbxxxx.append("경매장 대금 수령 (계정 : ");
               sbxxxx.append(c.getAccountName());
               sbxxxx.append(", 캐릭터 : ");
               sbxxxx.append(c.getPlayer().getName());
               sbxxxx.append(", 수령 메소 : ");
               sbxxxx.append(delta);
               sbxxxx.append(", 아이템 : ");
               sbxxxx.append(itemxxxxxxxxxxxxxx.getItem().getItemId());
               sbxxxx.append(" ");
               sbxxxx.append(itemxxxxxxxxxxxxxx.getItem().getQuantity());
               sbxxxx.append("개");
               long serialNumberxxxx = 0L;
               if (itemxxxxxxxxxxxxxx.getItem() instanceof Equip) {
                  sbxxxx.append(" (정보 : ");
                  serialNumberxxxx = ((Equip) itemxxxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                  sbxxxx.append(((Equip) itemxxxxxxxxxxxxxx.getItem()).toString());
               }

               sbxxxx.append("), 판매자 : ");
               sbxxxx.append(itemxxxxxxxxxxxxxx.getOwnerName());
               sbxxxx.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(),
                           itemxxxxxxxxxxxxxx.getItem().getItemId(),
                           itemxxxxxxxxxxxxxx.getItem().getQuantity(),
                           0L,
                           serialNumberxxxx,
                           AuctionLogType.GetMeso.getType(),
                           sbxxxx));
               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
               c.getSession()
                     .writeAndFlush(new Auction.RegisterHistoryItem(itemxxxxxxxxxxxxxx, c.getPlayer()).encode());
               c.getSession()
                     .writeAndFlush(new Auction.PaymentReceiptDone(AuctionMessage.SUCCESS.getValue(),
                           itemxxxxxxxxxxxxxx.getItem().getInventoryId()).encode());
               break;
            case 31: {
               int historyID = slea.readInt();
               slea.skip(4);
               slea.skip(4);
               int accountIDx = slea.readInt();
               int playerIDx = slea.readInt();
               int itemIDxx = slea.readInt();
               int statusx = slea.readInt();
               long mesoxxxx = slea.readLong();
               long timex = slea.readLong();
               AuctionItemPackage itemxxxxxxxxxxxxx = Center.Auction.getItemByHistoryID(historyID);
               if (itemxxxxxxxxxxxxx == null) {
                  c.getSession().writeAndFlush(
                        new Auction.ReturnItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (statusx != 2 && playerIDx != c.getPlayer().getId()) {
                  c.getSession().writeAndFlush(
                        new Auction.ReturnItemDone(AuctionMessage.CAN_NOT_BUY_OWN_ITEM.getValue(), 0L).encode());
                  return;
               }

               if (itemIDxx != itemxxxxxxxxxxxxx.getItem().getItemId()) {
                  return;
               }

               if (itemxxxxxxxxxxxxx.getItem().getQuantity() <= 0) {
                  c.getPlayer().ban("경매장 수령 버그 핵 사용으로 인한 영구밴", true, true, true);
                  return;
               }

               boolean isOwner = itemxxxxxxxxxxxxx.getOwnerId() == c.getPlayer().getId();
               if (itemxxxxxxxxxxxxx.getType(isOwner, true) == 9
                     || itemxxxxxxxxxxxxx.getType(isOwner, true) == 14
                     || itemxxxxxxxxxxxxx.getType(isOwner, true) == 15) {
                  return;
               }

               if (itemxxxxxxxxxxxxx.getItem().getQuantity() < 0 || itemxxxxxxxxxxxxx.getItem().getQuantity() > 32767) {
                  return;
               }

               MapleInventoryType t = MapleInventoryType.getByType((byte) (itemIDxx / 1000000));
               if (t == MapleInventoryType.EQUIP && ii.isCash(itemIDxx)) {
                  t = MapleInventoryType.CASH_EQUIP;
               }

               if (c.getPlayer().getInventory(t).isFull()) {
                  c.getSession().writeAndFlush(
                        new Auction.ReturnItemDone(AuctionMessage.NOT_ENOUGH_INVENTORY_SLOT.getValue(), 0L).encode());
                  return;
               }

               itemxxxxxxxxxxxxx.getItem().setGMLog(CurrentTime.getAllCurrentTime() + "에 경매장으로 부터 얻은 아이템");
               int typexxx = 0;
               if (statusx == 2) {
                  if (itemxxxxxxxxxxxxx.getType(isOwner, true) == 14) {
                     StringBuilder sbxxx = new StringBuilder();
                     sbxxx.append(
                           "이미 수령한 아이템을 다시 수령 시도 : 아이템 (" + itemIDxx + ") [이름 : " + c.getPlayer().getName() + "]");
                     sbxxx.append(c.getAccountName());
                     sbxxx.append(", 아이템 : ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getItem().getItemId());
                     sbxxx.append(" ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getItem().getQuantity());
                     sbxxx.append("개");
                     long serialNumberxxx = 0L;
                     if (itemxxxxxxxxxxxxx.getItem() instanceof Equip) {
                        sbxxx.append(" (정보 : ");
                        serialNumberxxx = ((Equip) itemxxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                        sbxxx.append(((Equip) itemxxxxxxxxxxxxx.getItem()).toString());
                     }

                     sbxxx.append("), 판매자 : ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getOwnerName());
                     sbxxx.append(")");
                     LoggingManager.putLog(
                           new AuctionLog(
                                 c.getPlayer(),
                                 itemxxxxxxxxxxxxx.getItem().getItemId(),
                                 itemxxxxxxxxxxxxx.getItem().getQuantity(),
                                 0L,
                                 serialNumberxxx,
                                 AuctionLogType.HackLog.getType(),
                                 sbxxx));
                     c.disconnect(false);
                     return;
                  }

                  if (itemxxxxxxxxxxxxx.getType(isOwner, true) == 15) {
                     StringBuilder sbxxx = new StringBuilder();
                     sbxxx.append(
                           "이미 수령한 아이템을 다시 수령 시도 : 아이템 (" + itemIDxx + ") [이름 : " + c.getPlayer().getName() + "]");
                     sbxxx.append(c.getAccountName());
                     sbxxx.append(", 아이템 : ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getItem().getItemId());
                     sbxxx.append(" ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getItem().getQuantity());
                     sbxxx.append("개");
                     long serialNumberxxx = 0L;
                     if (itemxxxxxxxxxxxxx.getItem() instanceof Equip) {
                        sbxxx.append(" (정보 : ");
                        serialNumberxxx = ((Equip) itemxxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                        sbxxx.append(((Equip) itemxxxxxxxxxxxxx.getItem()).toString());
                     }

                     sbxxx.append("), 판매자 : ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getOwnerName());
                     sbxxx.append(")");
                     LoggingManager.putLog(
                           new AuctionLog(
                                 c.getPlayer(),
                                 itemxxxxxxxxxxxxx.getItem().getItemId(),
                                 itemxxxxxxxxxxxxx.getItem().getQuantity(),
                                 0L,
                                 serialNumberxxx,
                                 AuctionLogType.HackLog.getType(),
                                 sbxxx));
                     c.disconnect(false);
                     return;
                  }

                  if (itemxxxxxxxxxxxxx.getType(isOwner, true) == 2) {
                     typexxx = 14;
                  } else if (itemxxxxxxxxxxxxx.getType(isOwner, true) == 13) {
                     typexxx = 15;
                  }
               } else {
                  if (itemxxxxxxxxxxxxx.getType(isOwner, true) == 9) {
                     StringBuilder sbxxx = new StringBuilder();
                     sbxxx.append(
                           "이미 반환한 아이템을 다시 반환 시도 : 아이템 (" + itemIDxx + ") [이름 : " + c.getPlayer().getName() + "]");
                     sbxxx.append(c.getAccountName());
                     sbxxx.append(", 아이템 : ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getItem().getItemId());
                     sbxxx.append(" ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getItem().getQuantity());
                     sbxxx.append("개");
                     long serialNumberxxx = 0L;
                     if (itemxxxxxxxxxxxxx.getItem() instanceof Equip) {
                        sbxxx.append(" (정보 : ");
                        serialNumberxxx = ((Equip) itemxxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
                        sbxxx.append(((Equip) itemxxxxxxxxxxxxx.getItem()).toString());
                     }

                     sbxxx.append("), 판매자 : ");
                     sbxxx.append(itemxxxxxxxxxxxxx.getOwnerName());
                     sbxxx.append(")");
                     LoggingManager.putLog(
                           new AuctionLog(
                                 c.getPlayer(),
                                 itemxxxxxxxxxxxxx.getItem().getItemId(),
                                 itemxxxxxxxxxxxxx.getItem().getQuantity(),
                                 0L,
                                 serialNumberxxx,
                                 AuctionLogType.HackLog.getType(),
                                 sbxxx));
                     c.disconnect(false);
                     return;
                  }

                  typexxx = 9;
               }

               itemxxxxxxxxxxxxx.setType(typexxx);
               Item it = itemxxxxxxxxxxxxx.getItem().copy();
               it.setInventoryId(MapleInventoryIdentifier.getInstance());
               short result = MapleInventoryManipulator.addFromAuction(c, it);
               if (result == -1) {
                  c.getSession().writeAndFlush(
                        new Auction.ReturnItemDone(AuctionMessage.NOT_ENOUGH_INVENTORY_SLOT.getValue(), 0L).encode());
                  return;
               }

               StringBuilder sbxxx = new StringBuilder();
               sbxxx.append("경매장 아이템 수령 (캐릭터 : ");
               sbxxx.append(c.getPlayer().getName());
               sbxxx.append(", 계정 : ");
               sbxxx.append(c.getAccountName());
               sbxxx.append("(");
               sbxxx.append(c.getAccID());
               sbxxx.append(")");
               sbxxx.append(", 아이템 : ");
               sbxxx.append(itemxxxxxxxxxxxxx.getItem().getItemId());
               sbxxx.append(" ");
               sbxxx.append(itemxxxxxxxxxxxxx.getItem().getQuantity());
               sbxxx.append("개, 판매자 : ");
               sbxxx.append(itemxxxxxxxxxxxxx.getOwnerName());
               sbxxx.append(")");
               long serialNumberxxx = 0L;
               if (itemxxxxxxxxxxxxx.getItem() instanceof Equip) {
                  serialNumberxxx = ((Equip) itemxxxxxxxxxxxxx.getItem()).getSerialNumberEquip();
               }

               sbxxx = new StringBuilder();
               sbxxx.append("경매장 아이템 수령 (계정 : ");
               sbxxx.append(c.getAccountName());
               sbxxx.append(", 캐릭터 : ");
               sbxxx.append(c.getPlayer().getName());
               sbxxx.append(", 아이템 : ");
               sbxxx.append(itemxxxxxxxxxxxxx.getItem().getItemId());
               sbxxx.append(" ");
               sbxxx.append(itemxxxxxxxxxxxxx.getItem().getQuantity());
               sbxxx.append("개");
               if (itemxxxxxxxxxxxxx.getItem() instanceof Equip) {
                  sbxxx.append(" (정보 : ");
                  sbxxx.append(((Equip) itemxxxxxxxxxxxxx.getItem()).toString());
               }

               sbxxx.append("), 판매자 : ");
               sbxxx.append(itemxxxxxxxxxxxxx.getOwnerName());
               sbxxx.append(")");
               LoggingManager.putLog(
                     new AuctionLog(
                           c.getPlayer(),
                           itemxxxxxxxxxxxxx.getItem().getItemId(),
                           itemxxxxxxxxxxxxx.getItem().getQuantity(),
                           0L,
                           serialNumberxxx,
                           AuctionLogType.GetItem.getType(),
                           sbxxx));
               long flag = -1L;
               c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), flag));
               c.getSession().writeAndFlush(new Auction.RegisterHistoryItem(itemxxxxxxxxxxxxx, c.getPlayer()).encode());
               c.getSession().writeAndFlush(
                     new Auction.ReturnItemDone(0, itemxxxxxxxxxxxxx.getItem().getInventoryId()).encode());
            }
               break;
            case 40:
            case 41: {
               List<AuctionItemPackage> allItem = op == 41 ? Center.Auction.getMarketPriceItems(c.getPlayer().getId())
                     : Center.Auction.getItems();
               slea.skip(1);
               int itemType = slea.readInt();
               String trimSearchText = slea.readMapleAsciiString();
               String realSearchText = slea.readMapleAsciiString();
               List<AuctionItemPackage> filter = new LinkedList<>();

               for (AuctionItemPackage item : allItem) {
                  if (item.getOwnerId() != c.getPlayer().getId()) {
                     String name = ii.getName(item.getItem().getItemId());
                     if (name != null
                           && (name.contains(realSearchText) || name.replaceAll(" ", "").contains(trimSearchText))) {
                        filter.add(item);
                     }
                  }
               }

               AuctionSearchManager.ClassifyFlag itemTypeFlag = AuctionSearchManager.ClassifyFlag.getFlag(itemType);
               if (itemTypeFlag != null && itemTypeFlag != AuctionSearchManager.ClassifyFlag.All) {
                  int itemClass = slea.readInt();
                  int itemSemiClass = slea.readInt();
                  List<AuctionItemPackage> removeList = new ArrayList<>(
                        AuctionSearchManager.setFilterItemType(filter, itemTypeFlag, itemClass, itemSemiClass));
                  filter.removeAll(removeList);
                  removeList.clear();
                  int minLevel = slea.readInt();
                  int maxLevel = slea.readInt();

                  for (AuctionItemPackage itemx : filter) {
                     int reqLev = ii.getReqLevel(itemx.getItem().getItemId());
                     if (reqLev < minLevel || reqLev > maxLevel) {
                        removeList.add(itemx);
                     }
                  }

                  filter.removeAll(removeList);
                  removeList.clear();
                  long minPrice = slea.readLong();
                  long maxPrice = slea.readLong();

                  for (AuctionItemPackage itemxx : filter) {
                     if (itemxx.getMesos() < minPrice || itemxx.getMesos() > maxPrice) {
                        removeList.add(itemxx);
                     }
                  }

                  filter.removeAll(removeList);
                  removeList.clear();
                  int count = slea.readInt();
                  List<Pair<Integer, Integer>> subDataList = new ArrayList<>();

                  for (int i = 0; i < count; i++) {
                     subDataList.add(new Pair<>(slea.readInt(), slea.readInt()));
                  }

                  for (Pair<Integer, Integer> data : subDataList) {
                     switch (data.left) {
                        case 0:
                        case 1:
                           for (AuctionItemPackage itemxxx : filter) {
                              if (itemxxx.getItem() instanceof Equip) {
                                 Equip equip = (Equip) itemxxx.getItem();
                                 int potential = data.left == 0 ? equip.getPotential1() : equip.getPotential4();
                                 if (data.right != potential / 10000) {
                                    removeList.add(itemxxx);
                                 }
                              } else {
                                 removeList.add(itemxxx);
                              }
                           }

                           filter.removeAll(removeList);
                           removeList.clear();
                           break;
                        case 2:
                           for (AuctionItemPackage itemxxxxx : filter) {
                              if (itemxxxxx.getItem() instanceof Equip) {
                                 Equip equip = (Equip) itemxxxxx.getItem();
                                 if (data.right == -1 && ii.isSpecialLabel(equip.getItemId())) {
                                    removeList.add(itemxxxxx);
                                 } else if (data.right == 0 && !ii.isSpecialLabel(equip.getItemId())) {
                                    removeList.add(itemxxxxx);
                                 } else if (equip.getCsGrade() != data.right) {
                                    removeList.add(itemxxxxx);
                                 }
                              }
                           }

                           filter.removeAll(removeList);
                           removeList.clear();
                           break;
                        case 3:
                           for (AuctionItemPackage itemxxxx : filter) {
                              if (itemxxxx.getItem().getPet() != null
                                    && itemxxxx.getItem().getPet().getWonderGrade() != data.right) {
                                 removeList.add(itemxxxx);
                              }
                           }

                           filter.removeAll(removeList);
                           removeList.clear();
                           break;
                        case 4:
                           for (AuctionItemPackage itemxxxxxx : filter) {
                              int reqJob = ii.getReqJob(itemxxxxxx.getItem().getItemId());
                              if (reqJob != 0 && (data.right & reqJob) == 0) {
                                 removeList.add(itemxxxxxx);
                              }
                           }

                           filter.removeAll(removeList);
                           removeList.clear();
                     }
                  }

                  int starForceMin = slea.readInt();
                  int starForceMax = slea.readInt();

                  for (AuctionItemPackage itemxxxxxxx : filter) {
                     Item checkItem = itemxxxxxxx.getItem();
                     if (GameConstants.isEquip(checkItem.getItemId()) && checkItem instanceof Equip) {
                        Equip checkEquip = (Equip) checkItem;
                        if (checkEquip.getCHUC() < starForceMin || checkEquip.getCHUC() > starForceMax) {
                           removeList.add(itemxxxxxxx);
                        }
                     }
                  }

                  boolean isOptionTypeAnd = slea.readByte() == 1;
                  List<AuctionItemPackage> copyArrayList = new ArrayList<>();
                  int equipDetailOpCount = slea.readInt();

                  for (int i = 0; i < equipDetailOpCount; i++) {
                     int optionValue = slea.readInt();
                     int optionCount = slea.readInt();
                     List<Pair<Integer, Integer>> detailDataList = new ArrayList<>();

                     for (int a = 0; a < optionCount; a++) {
                        int flag = slea.readInt();
                        int value = slea.readInt();
                        detailDataList.add(new Pair<>(flag, value));
                     }

                     if (isOptionTypeAnd) {
                        filter = AuctionSearchManager.searchEquipDetailOption(filter, optionValue, detailDataList,
                              true);
                     } else {
                        copyArrayList.addAll(
                              AuctionSearchManager.searchEquipDetailOption(filter, optionValue, detailDataList, false));
                     }
                  }

                  if (!isOptionTypeAnd) {
                     filter.clear();
                     filter.addAll(copyArrayList);
                  }
               }

               if (op == 40) {
                  c.getSession().writeAndFlush(
                        new Auction.SearchResult(filter, c.getPlayer(), filter.isEmpty() ? 102 : 1000).encode());
               } else {
                  c.getSession().writeAndFlush(
                        new Auction.MarketPriceList(filter, c.getPlayer(), filter.isEmpty() ? 102 : 1000).encode());
               }
            }
               break;
            case 45: {
               int auctionInventoryID = slea.readInt();
               AuctionItemPackage item = Center.Auction.getItem(auctionInventoryID);
               if (item == null) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterWishItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               c.getPlayer().addAuctionWishList(auctionInventoryID);
               c.getSession().writeAndFlush(new Auction.RegisterWishItemDone(AuctionMessage.SUCCESS.getValue(),
                     item.getItem().getInventoryId()).encode());
               c.getSession().writeAndFlush(new Auction.BuyItem(item, c.getPlayer(), true).encode());
            }
               break;
            case 46:
               c.getSession()
                     .writeAndFlush(new Auction.WishList(c.getPlayer().getAuctionWishList(), c.getPlayer()).encode());
               break;
            case 47:
               int auctionInventoryID = slea.readInt();
               AuctionItemPackage item = Center.Auction.getItem(auctionInventoryID);
               if (item == null) {
                  c.getSession().writeAndFlush(
                        new Auction.RegisterWishItemDone(AuctionMessage.NOT_EXSIST_ITEM.getValue(), 0L).encode());
                  return;
               }

               c.getPlayer().removeAuctionWishList(auctionInventoryID);
               c.getSession().writeAndFlush(new Auction.BuyItem(item, c.getPlayer(), false).encode());
               c.getSession().writeAndFlush(new Auction.RemoveWishList(item.getItem().getInventoryId()).encode());
               break;
            case 50:
               c.getSession().writeAndFlush(new Auction.MyItemList(
                     Center.Auction.getMyItems(c.getAccID(), c.getPlayer().getId()), c.getPlayer()).encode());
               break;
            case 51:
               c.getSession().writeAndFlush(new Auction.MyHistory(
                     Center.Auction.getHistoryItems(c.getAccID(), c.getPlayer().getId()), c.getPlayer()).encode());
            case 61:
         }
      }
   }

   public static final boolean isPremiumUser(MapleCharacter player) {
      String grade = player.getOneInfoQuest(18202, "grade");
      if (grade != null && !grade.isEmpty() && Integer.parseInt(grade) > 5) {
         String date = player.getOneInfoQuest(18202, "end");
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

         try {
            Date d = sdf.parse(date);
            if (d.getTime() > System.currentTimeMillis()) {
               return true;
            }
         } catch (ParseException var5) {
            System.out.println("경매장 핸들러 오류");
            var5.printStackTrace();
         }
      }

      return false;
   }
}
