package objects.fields.child.minigame.soccer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;

public class MultiSoccerPacket {
   public static byte[] onSetMultiSoccerState(int nState) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULTI_SOCCER_STATE.getValue());
      packet.writeInt(nState);
      return packet.getPacket();
   }

   public static byte[] onMultiSoccerInit(List<MultiSoccerPlayer> playerList) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULTI_SOCCER_INIT.getValue());
      packet.writeInt(playerList.size());

      for (MultiSoccerPlayer player : playerList) {
         packet.writeInt(player.getPlayer().getId());
         packet.writeInt(player.getScore());
      }

      packet.writeInt(playerList.size());

      for (MultiSoccerPlayer player : playerList) {
         packet.writeInt(player.getPlayer().getId());
         packet.writeInt(player.getTeam());
      }

      Stream<MultiSoccerPlayer> redTeamList = playerList.stream().filter(player -> player.getTeam() == 0);
      Stream<MultiSoccerPlayer> blueTeamList = playerList.stream().filter(player -> player.getTeam() == 1);
      int redScore = redTeamList.mapToInt(MultiSoccerPlayer::getScore).sum();
      int blueScore = blueTeamList.mapToInt(MultiSoccerPlayer::getScore).sum();
      Map<Integer, Integer> map = Map.of(0, redScore, 1, blueScore);
      packet.writeInt(map.size());

      for (Entry<Integer, Integer> entry : map.entrySet()) {
         packet.writeInt(entry.getKey());
         packet.writeInt(entry.getValue());
      }

      return packet.getPacket();
   }

   public static byte[] onSetMultiSoccerTime(int tDuration) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULTI_SOCCER_SET_TIMER.getValue());
      packet.writeInt(tDuration);
      return packet.getPacket();
   }

   public static byte[] onSetMultiSoccerAddPlayer(int nCharID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULTI_SOCCER_ADD_PLAYER.getValue());
      packet.writeInt(nCharID);
      return packet.getPacket();
   }

   public static byte[] onSetMultiSoccerGoalPlayer(MultiSoccerPlayer player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULTI_SOCCER_GOAL_PLAYER.getValue());
      packet.writeInt(player.getPlayer().getId());
      packet.writeInt(player.getTeam() == 1 ? 0 : 1);
      return packet.getPacket();
   }

   public static byte[] onSetMultiSoccerResult(int nState) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULTI_SOCCER_RESULT.getValue());
      packet.writeInt(nState);
      return packet.getPacket();
   }
}
