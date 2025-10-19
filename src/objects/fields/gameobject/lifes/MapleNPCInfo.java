package objects.fields.gameobject.lifes;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class MapleNPCInfo {
   private final int trunkPut;
   private final int trunkGet;
   private final boolean rpsGame;
   private final boolean parcel;
   private final boolean storeBank;
   private final boolean guildRank;
   private final boolean imitate;
   private final boolean forceMove;
   private final boolean shop;
   private final String script;

   public MapleNPCInfo(MapleData info) {
      MapleData scriptdata = info.getChildByPath("script/0/script");
      this.trunkPut = MapleDataTool.getInt("trunkPut", info, 0);
      this.trunkGet = MapleDataTool.getInt("trunkGet", info, 0);
      this.rpsGame = MapleDataTool.getInt("rpsGame", info, 0) != 0;
      this.parcel = MapleDataTool.getInt("parcel", info, 0) != 0;
      this.storeBank = MapleDataTool.getInt("storeBank", info, 0) != 0;
      this.guildRank = MapleDataTool.getInt("guildRank", info, 0) != 0;
      this.imitate = MapleDataTool.getInt("imitate", info, 0) != 0;
      this.forceMove = MapleDataTool.getInt("forceMove", info, 0) != 0;
      this.shop = MapleDataTool.getInt("shop", info, 0) != 0;
      if (scriptdata != null) {
         this.script = MapleDataTool.getString(scriptdata);
      } else {
         this.script = null;
      }
   }

   public int getTrunkPut() {
      return this.trunkPut;
   }

   public int getTrunkGet() {
      return this.trunkGet;
   }

   public boolean isRpsGame() {
      return this.rpsGame;
   }

   public boolean isParcel() {
      return this.parcel;
   }

   public boolean isStoreBank() {
      return this.storeBank;
   }

   public boolean isGuildRank() {
      return this.guildRank;
   }

   public boolean isImitate() {
      return this.imitate;
   }

   public boolean isForceMove() {
      return this.forceMove;
   }

   public String getScript() {
      return this.script;
   }

   public boolean isShop() {
      return this.shop;
   }
}
