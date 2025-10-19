package network.models;

import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.processors.PlayerInteractionHandler;
import objects.context.ReportLogEntry;
import objects.item.Item;
import objects.item.MerchItemPackage;
import objects.shop.AbstractPlayerStore;
import objects.shop.HiredMerchant;
import objects.shop.IMaplePlayerShop;
import objects.shop.MapleMiniGame;
import objects.shop.MaplePlayerShop;
import objects.shop.MaplePlayerShopItem;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Pair;

public class PlayerShopPacket {
   public static byte[] sendTitleBox() {
      return sendTitleBox(7);
   }

   public static byte[] sendTitleBox(int mode) {
      PacketEncoder mplew = new PacketEncoder(8);
      mplew.writeShort(SendPacketOpcode.SEND_TITLE_BOX.getValue());
      mplew.write(mode);
      if (mode == 8 || mode == 16) {
         mplew.writeInt(0);
         mplew.write(0);
      } else if (mode == 13) {
         mplew.writeInt(0);
      } else if (mode == 14) {
         mplew.write(0);
      } else if (mode == 18) {
         mplew.write(1);
         mplew.writeMapleAsciiString("");
      }

      return mplew.getPacket();
   }

   public static byte[] requestShopPic(int oid) {
      PacketEncoder mplew = new PacketEncoder(17);
      mplew.writeShort(SendPacketOpcode.SEND_TITLE_BOX.getValue());
      mplew.write(17);
      mplew.writeInt(oid);
      mplew.writeShort(0);
      mplew.writeLong(0L);
      return mplew.getPacket();
   }

   public static final byte[] removeCharBox(MapleCharacter c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(c.getId());
      mplew.write(0);
      return mplew.getPacket();
   }

   public static final byte[] sendPlayerShopBox(MapleCharacter c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
      mplew.writeInt(c.getId());
      PacketHelper.addAnnounceBox(mplew, c);
      return mplew.getPacket();
   }

   public static final byte[] getHiredMerch(MapleCharacter chr, HiredMerchant merch, boolean firstTime) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(20);
      mplew.write(6);
      mplew.write(7);
      mplew.writeShort(merch.getVisitorSlot(chr));
      mplew.writeInt(merch.getItemId());
      mplew.writeMapleAsciiString("고용상인");

      for (Pair<Byte, MapleCharacter> storechr : merch.getVisitors()) {
         mplew.write(storechr.left);
         PacketHelper.addCharLook(mplew, storechr.right, false, false);
         mplew.writeMapleAsciiString(storechr.right.getName());
         mplew.writeShort(storechr.right.getJob());
      }

      mplew.write(-1);
      mplew.writeShort(0);
      mplew.writeMapleAsciiString(merch.getOwnerName());
      if (merch.isOwner(chr)) {
         mplew.writeInt(merch.getTimeLeft());
         mplew.write(firstTime ? 1 : 0);
         mplew.write(merch.getBoughtItems().size());

         for (AbstractPlayerStore.BoughtItem SoldItem : merch.getBoughtItems()) {
            mplew.writeInt(SoldItem.id);
            mplew.writeShort(SoldItem.quantity);
            mplew.writeLong(SoldItem.totalPrice);
            mplew.writeMapleAsciiString(SoldItem.buyer);
         }

         mplew.writeLong(merch.getMeso());
      }

      mplew.writeInt(263);
      mplew.writeMapleAsciiString(merch.getDescription());
      mplew.write(16);
      mplew.writeLong(merch.getMeso());
      mplew.write(merch.getItems().size());

      for (MaplePlayerShopItem item : merch.getItems()) {
         mplew.writeShort(item.bundles);
         mplew.writeShort(item.item.getQuantity());
         mplew.writeLong(item.price);
         PacketHelper.addItemInfo(mplew, item.item);
      }

      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static final byte[] getPlayerStore(MapleCharacter chr, boolean firstTime) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      IMaplePlayerShop ips = chr.getPlayerShop();
      mplew.writeInt(5);
      switch (ips.getShopType()) {
         case 2:
            mplew.write(4);
            mplew.write(4);
            break;
         case 3:
            mplew.write(2);
            mplew.write(2);
            break;
         case 4:
            mplew.write(1);
            mplew.write(2);
      }

      mplew.writeShort(ips.getVisitorSlot(chr));
      PacketHelper.addCharLook(mplew, ((MaplePlayerShop)ips).getMCOwner(), false, false);
      mplew.writeMapleAsciiString(ips.getOwnerName());
      mplew.writeShort(((MaplePlayerShop)ips).getMCOwner().getJob());

      for (Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
         mplew.write(storechr.left);
         PacketHelper.addCharLook(mplew, storechr.right, false, false);
         mplew.writeMapleAsciiString(storechr.right.getName());
         mplew.writeShort(storechr.right.getJob());
      }

      mplew.write(255);
      mplew.writeMapleAsciiString(ips.getDescription());
      mplew.write(10);
      mplew.write(ips.getItems().size());

      for (MaplePlayerShopItem item : ips.getItems()) {
         mplew.writeShort(item.bundles);
         mplew.writeShort(item.item.getQuantity());
         mplew.writeInt(item.price);
         PacketHelper.addItemInfo(mplew, item.item);
      }

      return mplew.getPacket();
   }

   public static final byte[] shopChat(String sender, String message, int playerID, int slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.CHAT.action);
      mplew.write(25);
      mplew.write(slot);
      mplew.writeMapleAsciiString(sender);
      mplew.writeMapleAsciiString(message);
      mplew.writeLong(0L);
      ReportLogEntry report = new ReportLogEntry(sender, message, playerID);
      report.encode(mplew);
      return mplew.getPacket();
   }

   public static final byte[] shopErrorMessage(int error, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(28);
      mplew.write(type);
      mplew.write(error);
      return mplew.getPacket();
   }

   public static final byte[] spawnHiredMerchant(HiredMerchant hm) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_HIRED_MERCHANT.getValue());
      mplew.writeInt(hm.getOwnerId());
      mplew.writeInt(hm.getItemId());
      mplew.encodePos(hm.getTruePosition());
      mplew.writeShort(0);
      mplew.writeMapleAsciiString(hm.getOwnerName());
      PacketHelper.addInteraction(mplew, hm);
      return mplew.getPacket();
   }

   public static final byte[] destroyHiredMerchant(int id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DESTROY_HIRED_MERCHANT.getValue());
      mplew.writeInt(id);
      return mplew.getPacket();
   }

   public static final byte[] shopItemUpdate(IMaplePlayerShop shop) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(75);
      if (shop.getShopType() == 1) {
         mplew.writeLong(0L);
      }

      mplew.write(shop.getItems().size());

      for (MaplePlayerShopItem item : shop.getItems()) {
         mplew.writeShort(item.bundles);
         mplew.writeShort(item.item.getQuantity());
         mplew.writeLong(item.price);
         PacketHelper.addItemInfo(mplew, item.item);
      }

      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static final byte[] shopVisitorAdd(MapleCharacter chr, int slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.VISIT.action);
      mplew.write(slot);
      PacketHelper.addCharLook(mplew, chr, false, false);
      mplew.writeMapleAsciiString(chr.getName());
      mplew.writeShort(chr.getJob());
      mplew.writeInt(0);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static final byte[] shopVisitorLeave(byte slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.EXIT.action);
      mplew.write(slot);
      return mplew.getPacket();
   }

   public static final byte[] updateHiredMerchant(HiredMerchant shop) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_HIRED_MERCHANT.getValue());
      mplew.writeInt(shop.getOwnerId());
      PacketHelper.addInteraction(mplew, shop);
      return mplew.getPacket();
   }

   public static final byte[] merchantNameChange(int cid, String name) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MERCHANT_NAME_CHANGE.getValue());
      mplew.write(17);
      mplew.writeInt(cid);
      mplew.writeInt(957);
      mplew.writeMapleAsciiString(name);
      mplew.write(5);
      mplew.write(1);
      mplew.write(7);
      return mplew.getPacket();
   }

   public static final byte[] merchItem_Message(int op) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MERCH_ITEM_MSG.getValue());
      mplew.write(op);
      return mplew.getPacket();
   }

   public static final byte[] merchItemStore(byte op, int days, int fees) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
      mplew.write(op);
      switch (op) {
         case 38:
            mplew.writeInt(days);
            mplew.writeInt(fees);
            break;
         case 39:
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
            mplew.write(0);
      }

      return mplew.getPacket();
   }

   public static final byte[] merchItemStore_ItemData(MerchItemPackage pack) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
      mplew.write(40);
      mplew.writeInt(9030000);
      mplew.writeInt(32272);
      mplew.writeZeroBytes(5);
      mplew.writeLong(pack.getMesos());
      mplew.writeZeroBytes(3);
      mplew.write(pack.getItems().size());

      for (Item item : pack.getItems()) {
         PacketHelper.addItemInfo(mplew, item);
      }

      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static final byte[] merchItemStore_ItemDataNone() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
      mplew.write(42);
      mplew.writeInt(9030000);
      mplew.write(-1);
      mplew.writeInt(3906249);
      return mplew.getPacket();
   }

   public static final byte[] merchItemStore2PWCheck(byte type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
      mplew.write(39);
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] getMiniGame(MapleClient c, MapleMiniGame minigame) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(20);
      mplew.write(minigame.getGameType());
      mplew.write(minigame.getMaxSize());
      mplew.writeShort(minigame.getVisitorSlot(c.getPlayer()));
      PacketHelper.addCharLook(mplew, minigame.getMCOwner(), false, false);
      mplew.writeMapleAsciiString(minigame.getOwnerName());
      mplew.writeShort(minigame.getMCOwner().getJob());
      mplew.writeInt(0);

      for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
         mplew.write(visitorz.getLeft());
         PacketHelper.addCharLook(mplew, visitorz.getRight(), false, false);
         mplew.writeMapleAsciiString(visitorz.getRight().getName());
         mplew.writeShort(visitorz.getRight().getJob());
         mplew.writeInt(0);
      }

      mplew.write(-1);
      mplew.write(0);
      addGameInfo(mplew, minigame.getMCOwner(), minigame);

      for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
         mplew.write(visitorz.getLeft());
         addGameInfo(mplew, visitorz.getRight(), minigame);
      }

      mplew.write(-1);
      mplew.writeMapleAsciiString(minigame.getDescription());
      mplew.writeShort(minigame.getPieceType());
      return mplew.getPacket();
   }

   public static byte[] getMiniGameReady(boolean ready) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(ready ? PlayerInteractionHandler.Interaction.READY.action : PlayerInteractionHandler.Interaction.UN_READY.action);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameInfoMsg(byte type, String name) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.CHAT.action);
      mplew.write(23);
      mplew.write(type);
      mplew.writeMapleAsciiString(name);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameStart(int loser) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.START.action);
      mplew.write(loser == 1 ? 0 : 1);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameSkip(int slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.SKIP.action);
      mplew.write(slot);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameRequestTie() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.REQUEST_TIE.action);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameDenyTie() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.ANSWER_TIE.action);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameRequestRedo() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.REQUEST_REDO.action);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameDenyRedo() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.ANSWER_REDO.action);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameFull() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(5);
      mplew.write(2);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameMoveOmok(int move1, int move2, int move3) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.MOVE_OMOK.action);
      mplew.writeInt(move1);
      mplew.writeInt(move2);
      mplew.write(move3);
      return mplew.getPacket();
   }

   public static byte[] getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.VISIT.action);
      mplew.write(slot);
      boolean isBeta = false;
      if (c.getZeroInfo() != null && c.getZeroInfo().isBeta()) {
         isBeta = true;
      }

      PacketHelper.addCharLook(mplew, c, false, isBeta);
      mplew.writeMapleAsciiString(c.getName());
      mplew.writeShort(c.getJob());
      mplew.writeInt(0);
      addGameInfo(mplew, c, game);
      return mplew.getPacket();
   }

   public static void addGameInfo(PacketEncoder mplew, MapleCharacter chr, MapleMiniGame game) {
      mplew.writeInt(game.getGameType());
      mplew.writeInt(game.getWins(chr));
      mplew.writeInt(game.getTies(chr));
      mplew.writeInt(game.getLosses(chr));
      mplew.writeInt(game.getScore(chr));
   }

   public static byte[] getMatchCardStart(MapleMiniGame game, int loser) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.START.action);
      mplew.write(loser == 1 ? 0 : 1);
      int times = game.getPieceType() == 1 ? 20 : (game.getPieceType() == 2 ? 30 : 12);
      mplew.write(times);

      for (int i = 1; i <= times; i++) {
         mplew.writeInt(game.getCardId(i));
      }

      return mplew.getPacket();
   }

   public static byte[] getMatchCardSelect(int turn, int slot, int firstslot, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.SELECT_CARD.action);
      mplew.write(turn);
      mplew.write(slot);
      if (turn == 0) {
         mplew.write(firstslot);
         mplew.write(type);
      }

      return mplew.getPacket();
   }

   public static byte[] getMiniGameResult(MapleMiniGame game, int type, int x) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.RESULT.action);
      mplew.write(type);
      game.setPoints(x, type);
      if (type == 0) {
         game.setPoints(x == 1 ? 0 : 1, type == 0 ? 2 : 0);
      }

      if (type != 0) {
         game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
      }

      if (type != 1) {
         if (type == 0) {
            mplew.write(x == 1 ? 0 : 1);
         } else {
            mplew.write(x);
         }
      }

      addGameInfo(mplew, game.getMCOwner(), game);

      for (Pair<Byte, MapleCharacter> visitorz : game.getVisitors()) {
         addGameInfo(mplew, visitorz.right, game);
      }

      return mplew.getPacket();
   }

   public static final byte[] MerchantClose(int error, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.CLOSE_MERCHANT.action + 1);
      mplew.write(type);
      mplew.write(error);
      return mplew.getPacket();
   }

   public static final byte[] MerchantBlackListView(List<String> blackList) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.VIEW_MERCHANT_BLACKLIST.action);
      mplew.writeShort(blackList.size());

      for (String visit : blackList) {
         mplew.writeMapleAsciiString(visit);
      }

      return mplew.getPacket();
   }

   public static final byte[] MerchantVisitorView(List<String> visitor) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.VIEW_MERCHANT_VISITOR.action);
      mplew.writeShort(visitor.size());

      for (String visit : visitor) {
         mplew.writeMapleAsciiString(visit);
         mplew.writeInt(1);
      }

      return mplew.getPacket();
   }

   public static byte[] StartRPS() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(PlayerInteractionHandler.Interaction.START.action);
      return mplew.getPacket();
   }

   public static byte[] FinishRPS(byte result, byte rps) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeInt(84);
      mplew.write(result);
      mplew.write(rps);
      return mplew.getPacket();
   }
}
