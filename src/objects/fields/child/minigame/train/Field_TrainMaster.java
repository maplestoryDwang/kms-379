package objects.fields.child.minigame.train;

import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Triple;

public class Field_TrainMaster extends Field {
   public static final int PLAYER_COUNT = 3;
   public static final int DURATION = 1800;
   public static final int WIN_GAME = 0;
   public static final int LOSE_GAME = 1;
   public static final int DRAW_GAME = 2;
   public boolean init;
   public boolean startGame;
   public boolean endGame;
   public boolean warp;
   public int state;
   public long startGameTime;
   public long endGameTime;
   public long warpTime;
   public List<TrainMasterPlayer> userList = new ArrayList<>();
   public List<Triple<Integer, Integer, Integer>> itemList = List.of(
      new Triple<>(4, 14, 0),
      new Triple<>(6, 12, 0),
      new Triple<>(10, 0, 1),
      new Triple<>(6, 16, 1),
      new Triple<>(8, 6, 2),
      new Triple<>(10, 4, 2),
      new Triple<>(2, 12, 3),
      new Triple<>(2, 0, 3),
      new Triple<>(4, 6, 4),
      new Triple<>(4, 2, 4),
      new Triple<>(0, 10, 5),
      new Triple<>(8, 10, 5)
   );

   public Field_TrainMaster(int mapID, int channel, int returnMapId, float monsterRate) {
      super(mapID, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();

      try {
         if (!this.init) {
            this.initGame();
         } else if (!this.startGame) {
            if (this.startGameTime < System.currentTimeMillis()) {
               this.startGame();
            }
         } else if (!this.endGame) {
            if (this.endGameTime >= System.currentTimeMillis()) {
               this.checkGame();
            }
         } else if (!this.warp && this.warpTime < System.currentTimeMillis()) {
         }
      } catch (Exception var2) {
         System.out.println("TrainMaster Err");
         var2.printStackTrace();
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.init = false;
      this.startGame = false;
      this.endGame = false;
      this.warp = false;
      this.state = -1;
      this.startGameTime = 0L;
      this.endGameTime = 0L;
      this.warpTime = 0L;
      this.userList = new ArrayList<>();
   }

   public void checkGame() {
      this.userList.forEach(user -> user.getPlayer().send(this.updateUser(user)));
   }

   public void startGame() {
      this.startGame = true;
      this.showMsg();
   }

   public void initGame() {
      this.init = true;
      this.startGameTime = System.currentTimeMillis() + 3000L;
      this.showEffect();
      this.onSetTimer();
      this.onInit();
   }

   public void onInit() {
      this.broadcastMessage(CField.environmentChange("UI/UIWindowEvent.img/TrainVLog/screenEff/countdown", 16));
      this.onSetPlayer();
      this.onSetItem();
   }

   public byte[] updateUser(TrainMasterPlayer user) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TRAIN_MASTER.getValue());
      packet.writeInt(7);
      packet.writeInt(user.getPlayer().getId());

      for (Pair<Integer, Integer> data : user.getData()) {
         packet.writeInt(data.left);
         packet.writeInt(data.right);
      }

      return packet.getPacket();
   }

   public void onSetItem() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TRAIN_MASTER.getValue());
      packet.writeInt(10);
      packet.writeInt(this.itemList.size());

      for (Triple<Integer, Integer, Integer> key : this.itemList) {
         packet.writeInt(key.left);
         packet.writeInt(key.mid);
         packet.writeInt(key.right);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void addPlayer(List<MapleCharacter> chrList) {
      chrList.forEach(chr -> {
         TrainMasterPlayer user = new TrainMasterPlayer(chr);
         user.set(this.userList.size());
         this.userList.add(user);
      });
   }

   public void movePlayer(int id, int option1, int option2, int option3, int option4) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TRAIN_MASTER.getValue());
      packet.writeInt(6);
      packet.writeInt(id);
      packet.writeInt(option1);
      packet.writeInt(option2);
      packet.writeInt(option3);
      packet.writeInt(option4);
      this.broadcastMessage(packet.getPacket());
   }

   public void onSetPlayer() {
      this.addPlayer(this.getCharacters());
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TRAIN_MASTER.getValue());
      packet.writeInt(8);
      packet.writeInt(this.userList.size());

      for (TrainMasterPlayer user : this.userList) {
         packet.writeInt(user.getPlayer().getId());

         for (Pair<Integer, Integer> set : user.getData()) {
            packet.writeInt(set.left);
            packet.writeInt(set.right);
         }
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void onSetTimer() {
      this.endGameTime = System.currentTimeMillis() + 1800000L;
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TRAIN_MASTER.getValue());
      packet.writeInt(21);
      packet.writeInt(1800);
      this.broadcastMessage(packet.getPacket());
   }

   public void showEffect() {
      this.broadcastMessage(CWvsContext.showNewEffect("TrainVLog_1", "TrainVLog_", false));
      this.broadcastMessage(CField.addPopupSay(9062575, 5000, "#b#e크리에이터들의\r\n기차 운행 도전!#n#k\r\n#r지금 바로 시작하겠네!#k", ""));
   }

   public void showMsg() {
      this.broadcastMessage(CField.environmentChange("UI/UIWindowEvent.img/TrainVLog/screenEff/start", 16));
      this.broadcastMessage(CField.environmentChange("UI/UIMiniGame.img/mapleOneCard/Effect/screeneff/start", 16));
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TRAIN_MASTER.getValue());
      packet.writeInt(9);
      packet.writeInt(1800);
      this.broadcastMessage(packet.getPacket());
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }
}
