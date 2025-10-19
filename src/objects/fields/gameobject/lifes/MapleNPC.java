package objects.fields.gameobject.lifes;

import network.models.CField;
import objects.fields.MapleMapObjectType;
import objects.shop.MapleShopFactory;
import objects.users.MapleClient;

public class MapleNPC extends AbstractLoadedMapleLife {
   private String name = "MISSINGNO";
   private String func = "MISSINGNO";
   private boolean custom = false;
   private int localUserID;
   private boolean canMove = true;
   boolean blossom = false;
   private String customTEXT = "";

   public MapleNPC(int id, String name) {
      super(id);
      this.name = name;
   }

   public String getFunc() {
      return this.func;
   }

   public void setFunc(String func) {
      this.func = func;
   }

   public final boolean hasShop() {
      return MapleShopFactory.getInstance().getShopForNPC(this.getId()) != null;
   }

   public final void sendShop(MapleClient c) {
      MapleShopFactory.getInstance().getShopForNPC(this.getId()).sendShop(c);
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      if (this.getId() != 9000453) {
         if (this.getId() < 9901000) {
            if (this.localUserID > 0 && client.getPlayer().getId() == this.localUserID || this.localUserID == 0) {
               client.getSession().writeAndFlush(CField.NPCPacket.spawnNPC(this, true));
               client.getSession().writeAndFlush(CField.NPCPacket.spawnNPCRequestController(this, true));
               if ((this.getId() == 9062530 || this.getId() == 9062531 || this.getId() == 9062532) && this.isBlossom()) {
                  client.getSession().writeAndFlush(CField.NPCPacket.npcSpecialAction(this.getObjectId(), "special2", 210000000, 1));
               }
            }
         }
      }
   }

   @Override
   public final void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.NPCPacket.removeNPCController(this.getObjectId()));
      client.getSession().writeAndFlush(CField.NPCPacket.removeNPC(this.getObjectId()));
   }

   @Override
   public final MapleMapObjectType getType() {
      return MapleMapObjectType.NPC;
   }

   public final String getName() {
      return this.name;
   }

   public void setName(String n) {
      this.name = n;
   }

   public final boolean isCustom() {
      return this.custom;
   }

   public final void setCustom(boolean custom) {
      this.custom = custom;
   }

   public int getLocalUserID() {
      return this.localUserID;
   }

   public void setLocalUserID(int localUserID) {
      this.localUserID = localUserID;
   }

   public boolean isCanMove() {
      return this.canMove;
   }

   public void setCanMove(boolean canMove) {
      this.canMove = canMove;
   }

   public boolean isBlossom() {
      return this.blossom;
   }

   public void setBlossom(boolean b) {
      this.blossom = b;
   }

   public String getCustomTEXT() {
      return this.customTEXT;
   }

   public void setCustomTEXT(String customTEXT) {
      this.customTEXT = customTEXT;
   }
}
