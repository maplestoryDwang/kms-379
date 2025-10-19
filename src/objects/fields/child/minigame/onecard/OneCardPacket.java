package objects.fields.child.minigame.onecard;

import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.processors.PlayerInteractionHandler;
import network.models.PacketHelper;

public class OneCardPacket {
   public static byte[] createUI(OneCardPlayer chr, List<OneCardPlayer> chrList) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(20);
      o.write(16);
      o.write(chrList.size());
      o.write(chr.getPOS());

      for (OneCardPlayer player : chrList) {
         o.write(player.getPOS());
         PacketHelper.addCharLook(o, player.getPlayer(), chr.getPlayer().getId() == player.getPlayer().getId(), false);
         o.writeMapleAsciiString(player.getPlayer().getName());
         o.writeShort(player.getPlayer().getJob());
         o.writeInt(0);
      }

      o.write(-1);
      o.write(chrList.size());

      for (OneCardPlayer oneCardPlayer : chrList) {
         o.writeInt(oneCardPlayer.getPlayer().getId());
      }

      return o.getPacket();
   }

   public static byte[] onChangeColorRequest(List<Integer> ableColors) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(2);
      o.writeInt(15);
      o.write(ableColors.size());

      for (int color : ableColors) {
         o.write(color);
      }

      return o.getPacket();
   }

   public static byte[] onStart(List<OneCardPlayer> players) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(90);
      o.writeInt(players.size());

      for (OneCardPlayer player : players) {
         o.writeInt(player.getPlayer().getId());
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(player.getPOS());
         o.writeInt(player.getDeckInfo().size());
      }

      return o.getPacket();
   }

   public static byte[] onPutCardResult(OneCardPlayer player, OneCardGameCardInfo card) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(128);
      o.writeInt(player == null ? 0 : player.getPlayer().getId());
      o.writeInt(card.getObjectID());
      o.write(card.getColor());
      o.write(card.getNumber());
      o.write(false);
      return o.getPacket();
   }

   public static byte[] onGetCardResult(OneCardPlayer player, List<OneCardGameCardInfo> cards) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(129);
      o.writeInt(player.getPlayer().getId());
      o.write(cards.size());

      for (OneCardGameCardInfo card : cards) {
         o.writeInt(card.getObjectID());
         o.write(card.getColor());
         o.write(card.getNumber());
      }

      return o.getPacket();
   }

   public static byte[] onChangeColorResult(boolean bHero, byte color) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(130);
      o.write(bHero);
      o.write(color);
      return o.getPacket();
   }

   public static byte[] onUserPossibleAction(
      OneCardPlayer player, int time, List<OneCardGameCardInfo> cards, boolean bGetCardFromGraves, boolean bClockWiseTurn
   ) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(131);
      o.write(time);
      o.writeInt(player.getPlayer().getId());
      o.write(bGetCardFromGraves);
      o.write(bClockWiseTurn);
      o.writeInt(cards.size());

      for (OneCardGameCardInfo card : cards) {
         o.writeInt(card.getObjectID());
      }

      return o.getPacket();
   }

   public static byte[] onShowScreenEffect(String str) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(132);
      o.writeMapleAsciiString(str);
      return o.getPacket();
   }

   public static byte[] onEffectResult(int type, int data, int id, boolean gameOver) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(133);
      o.write(type);
      switch (type) {
         case 0:
         case 1:
         default:
            break;
         case 2:
            o.write(data);
            break;
         case 3:
         case 4:
            o.writeInt(id);
            break;
         case 5:
            o.writeInt(id);
            o.write(gameOver);
      }

      return o.getPacket();
   }

   public static byte[] onEmotion(int charID, int eid) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(134);
      o.writeInt(charID);
      o.writeInt(eid);
      return o.getPacket();
   }

   public static byte[] onShowText(String str) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(136);
      o.writeMapleAsciiString(str);
      return o.getPacket();
   }

   public static final byte[] leaveResult() {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      o.writeInt(PlayerInteractionHandler.Interaction.EXIT.action);
      o.write(0);
      return o.getPacket();
   }
}
