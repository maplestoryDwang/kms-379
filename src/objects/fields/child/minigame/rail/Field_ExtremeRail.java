package objects.fields.child.minigame.rail;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.HexTool;

public class Field_ExtremeRail extends Field {
   private MapleCharacter player = null;
   private boolean startGame = false;
   private long startGameTime = 0L;
   private boolean endGame = false;
   private long endGameTime = 0L;
   private int distance = 0;

   public Field_ExtremeRail(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (!this.endGame) {
         if (!this.startGame) {
            if (this.startGameTime == 0L) {
               this.sendGameInit();
               this.startGameTime = System.currentTimeMillis() + 3000L;
               return;
            }

            if (this.startGameTime != 0L && this.startGameTime <= System.currentTimeMillis()) {
               this.sendGameStart();
               this.startGame = true;
            }
         } else if (this.endGameTime != 0L && this.endGameTime <= System.currentTimeMillis()) {
            this.endGame(this.getDistance());
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.startGame = false;
      this.startGameTime = 0L;
      this.endGame = false;
      this.endGameTime = 0L;
      this.setDistance(0);
      this.player = null;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.resetFully(false);
      this.player = player;
      player.send(CField.UIPacket.setIngameDirectionMode(false, false, false));
      player.send(CField.UIPacket.hideChar(true));
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.send(CField.UIPacket.endInGameDirectionMode(0));
   }

   public void endGame(int distance) {
      int coin = 0;
      coin = distance / 500;
      if (distance >= 10000) {
         coin = distance / 300;
      } else if (distance >= 50000) {
         coin = distance / 200;
      } else if (distance >= 100000) {
         coin = distance / 150;
      }

      this.player.updateOneInfo(1234569, "miniGame4_coin", String.valueOf(coin));
      this.sendGameResult(coin, distance);
      this.endGame = true;
      this.player.setRegisterTransferField(993190300);
      this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 2000L);
   }

   public void sendGameResult(int coin, int distance) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.EXTREME_RAIL_GAME_RESULT.getValue());
      packet.writeInt(0);
      packet.writeInt(2);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(coin);
      packet.writeInt(coin);
      packet.writeInt(distance);
      this.player.send(packet.getPacket());
   }

   public void sendGameStart() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.EXTREME_RAIL_START.getValue());
      packet.writeInt(0);
      packet.writeInt(1);
      packet.writeInt(1);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      this.player.send(packet.getPacket());
   }

   public void sendGameInit() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.EXTREME_RAIL_INIT.getValue());
      packet.writeInt(0);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00"));
      packet.writeInt(1);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(125000);
      packet.writeInt(3000);
      packet.writeInt(1);
      packet.writeInt(5);
      packet.writeInt(300);
      packet.writeInt(10000);
      packet.writeInt(10000);
      packet.writeInt(0);
      packet.writeInt(-100);
      packet.writeInt(8000);
      packet.writeInt(5000);
      packet.writeInt(1);
      packet.writeInt(22);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C0 D4 01 00 01 00 00 00 00 00 00 00 00 6A E8 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B0 AD 01 00 01 00 00 00 00 00 00 00 00 60 6D 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("28 9A 01 00 01 00 00 00 00 00 00 00 00 60 6D 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A0 86 01 00 01 00 00 00 00 00 00 00 00 00 6E 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("18 73 01 00 01 00 00 00 00 00 00 00 00 40 70 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("90 5F 01 00 01 00 00 00 00 00 00 00 00 E0 70 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("08 4C 01 00 01 00 00 00 00 00 00 00 00 80 71 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("80 38 01 00 01 00 00 00 00 00 00 00 00 D0 71 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("F8 24 01 00 01 00 00 00 00 00 00 00 00 80 71 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("70 11 01 00 01 00 00 00 00 00 00 00 00 20 72 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FD 00 00 01 00 00 00 00 00 00 00 00 C0 72 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("D8 D6 00 00 01 00 00 00 00 00 00 00 00 00 74 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C8 AF 00 00 01 00 00 00 00 00 00 00 00 E0 75 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("40 9C 00 00 01 00 00 00 00 00 00 00 00 00 79 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B8 88 00 00 01 00 00 00 00 00 00 00 00 20 7C 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("30 75 00 00 01 00 00 00 00 00 00 00 00 40 7F 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A8 61 00 00 01 00 00 00 00 00 00 00 00 C0 82 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("20 4E 00 00 01 00 00 00 00 00 00 00 00 E0 85 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("98 3A 00 00 01 00 00 00 00 00 00 00 00 00 89 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("10 27 00 00 01 00 00 00 00 00 00 00 00 40 8F 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("88 13 00 00 01 00 00 00 00 00 00 00 00 70 97 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 40 9F 40"));
      packet.writeInt(1);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 F0 3F"));
      packet.writeInt(21);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("28 9A 01 00 01 00 00 00 00 00 00 00 00 40 5A 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A0 86 01 00 01 00 00 00 00 00 00 00 00 00 59 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("18 73 01 00 01 00 00 00 00 00 00 00 00 80 56 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("90 5F 01 00 01 00 00 00 00 00 00 00 00 80 56 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("08 4C 01 00 01 00 00 00 00 00 00 00 00 40 55 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("80 38 01 00 01 00 00 00 00 00 00 00 00 00 54 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("F8 24 01 00 01 00 00 00 00 00 00 00 00 C0 52 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("70 11 01 00 01 00 00 00 00 00 00 00 00 80 51 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FD 00 00 01 00 00 00 00 00 00 00 00 40 51 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("60 EA 00 00 01 00 00 00 00 00 00 00 00 C0 50 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("D8 D6 00 00 01 00 00 00 00 00 00 00 00 40 50 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C8 AF 00 00 01 00 00 00 00 00 00 00 00 80 4F 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("40 9C 00 00 01 00 00 00 00 00 00 00 00 00 4E 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B8 88 00 00 01 00 00 00 00 00 00 00 00 00 49 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("30 75 00 00 01 00 00 00 00 00 00 00 00 80 46 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A8 61 00 00 01 00 00 00 00 00 00 00 00 80 41 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("20 4E 00 00 01 00 00 00 00 00 00 00 00 00 3E 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("98 3A 00 00 01 00 00 00 00 00 00 00 00 00 39 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("10 27 00 00 01 00 00 00 00 00 00 00 00 00 34 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("88 13 00 00 01 00 00 00 00 00 00 00 00 00 2E 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 24 40"));
      packet.writeInt(11);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C0 D4 01 00 01 00 00 00 00 00 00 00 00 00 00 00"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B0 AD 01 00 01 00 00 00 00 00 00 00 00 60 6D 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A0 86 01 00 01 00 00 00 00 00 00 00 00 00 69 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("90 5F 01 00 01 00 00 00 00 00 00 00 00 C0 62 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("80 38 01 00 01 00 00 00 00 00 00 00 00 80 56 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("70 11 01 00 01 00 00 00 00 00 00 00 00 40 55 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("60 EA 00 00 01 00 00 00 00 00 00 00 00 00 54 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("50 C3 00 00 01 00 00 00 00 00 00 00 00 00 4E 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("40 9C 00 00 01 00 00 00 00 00 00 00 00 00 44 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("20 4E 00 00 01 00 00 00 00 00 00 00 00 00 34 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 24 40"));
      packet.writeInt(21);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("28 9A 01 00 01 00 00 00 00 00 00 00 00 C0 72 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A0 86 01 00 01 00 00 00 00 00 00 00 00 60 73 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("18 73 01 00 01 00 00 00 00 00 00 00 00 00 74 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("90 5F 01 00 01 00 00 00 00 00 00 00 00 A0 74 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("08 4C 01 00 01 00 00 00 00 00 00 00 00 40 75 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("80 38 01 00 01 00 00 00 00 00 00 00 00 E0 75 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("F8 24 01 00 01 00 00 00 00 00 00 00 00 80 76 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("70 11 01 00 01 00 00 00 00 00 00 00 00 20 77 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("E8 FD 00 00 01 00 00 00 00 00 00 00 00 60 78 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("60 EA 00 00 01 00 00 00 00 00 00 00 00 A0 79 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("D8 D6 00 00 01 00 00 00 00 00 00 00 00 E0 7A 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C8 AF 00 00 01 00 00 00 00 00 00 00 00 20 7C 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("40 9C 00 00 01 00 00 00 00 00 00 00 00 60 7D 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B8 88 00 00 01 00 00 00 00 00 00 00 00 A0 7E 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("30 75 00 00 01 00 00 00 00 00 00 00 00 E0 7F 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("A8 61 00 00 01 00 00 00 00 00 00 00 00 90 80 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("20 4E 00 00 01 00 00 00 00 00 00 00 00 30 81 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("98 3A 00 00 01 00 00 00 00 00 00 00 00 C0 82 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("10 27 00 00 01 00 00 00 00 00 00 00 00 50 84 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("88 13 00 00 01 00 00 00 00 00 00 00 00 E0 85 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 89 40"));
      packet.writeInt(3);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("50 C3 00 00 01 00 00 00 00 00 00 00 00 00 49 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("40 9C 00 00 01 00 00 00 00 00 00 00 00 00 59 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 C0 72 40"));
      packet.writeInt(2);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B0 AD 01 00 01 00 00 00 00 00 00 00 00 00 F0 3F"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00"));
      packet.writeInt(3);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("90 5F 01 00 01 00 00 00 00 00 00 00 00 70 A7 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("60 EA 00 00 01 00 00 00 00 00 00 00 00 40 8F 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00"));
      packet.writeInt(1);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
      packet.writeInt(1000);
      packet.writeInt(9);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C0 D4 01 00 01 00 00 00 00 00 00 00 00 00 24 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B0 AD 01 00 01 00 00 00 00 00 00 00 00 00 34 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("50 C3 00 00 01 00 00 00 00 00 00 00 00 00 24 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("C8 AF 00 00 01 00 00 00 00 00 00 00 00 00 1C 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("40 9C 00 00 01 00 00 00 00 00 00 00 00 00 18 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("B8 88 00 00 01 00 00 00 00 00 00 00 00 00 10 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("30 75 00 00 01 00 00 00 00 00 00 00 00 00 08 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("10 27 00 00 01 00 00 00 00 00 00 00 00 00 00 40"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 01 00 00 00 00 00 00 00 00 00 F0 3F"));
      packet.writeInt(0);
      packet.writeInt(2);
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00 01 00 00 00 00 00 00 00 FF FF FF FF 01 00 00 00"));
      packet.encodeBuffer(HexTool.getByteArrayFromHexString("01 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF 19 00 00 00"));
      this.player.send(packet.getPacket());
   }

   public int getDistance() {
      return this.distance;
   }

   public void setDistance(int distance) {
      this.distance = distance;
   }
}
