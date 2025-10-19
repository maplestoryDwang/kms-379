package objects.shop;

import java.util.List;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Pair;

public interface IMaplePlayerShop {
   byte HIRED_MERCHANT = 1;
   byte PLAYER_SHOP = 2;
   byte OMOK = 3;
   byte MATCH_CARD = 4;

   String getOwnerName();

   String getDescription();

   List<Pair<Byte, MapleCharacter>> getVisitors();

   List<MaplePlayerShopItem> getItems();

   boolean isOpen();

   boolean removeItem(int var1);

   boolean isOwner(MapleCharacter var1);

   byte getShopType();

   byte getVisitorSlot(MapleCharacter var1);

   byte getFreeSlot();

   int getItemId();

   int getMeso();

   int getOwnerId();

   int getOwnerAccId();

   void setOpen(boolean var1);

   void setMeso(int var1);

   void addItem(MaplePlayerShopItem var1);

   void removeFromSlot(int var1);

   void broadcastToVisitors(byte[] var1);

   void addVisitor(MapleCharacter var1);

   void removeVisitor(MapleCharacter var1);

   void removeAllVisitors(int var1, int var2);

   void buy(MapleClient var1, int var2, short var3);

   void closeShop(boolean var1, boolean var2);

   String getPassword();

   int getMaxSize();

   int getSize();

   int getGameType();

   void update();

   void setAvailable(boolean var1);

   boolean isAvailable();

   List<AbstractPlayerStore.BoughtItem> getBoughtItems();
}
