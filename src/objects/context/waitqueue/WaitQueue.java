package objects.context.waitqueue;

import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;

public class WaitQueue {
   private int waitingType;
   private int waitingQueueID;
   private int fieldID;
   private int maxUserCount;
   private List<MapleCharacter> players = new ArrayList<>();
   private List<MapleCharacter> acceptPlayers = new ArrayList<>();

   public WaitQueue(int maxUserCount, int waitingType, int waitingQueueID, int fieldID, MapleCharacter player) {
      this.setMaxUserCount(maxUserCount);
      this.waitingType = waitingType;
      this.setWaitingQueueID(waitingQueueID);
      this.setFieldID(fieldID);
      this.players.add(player);
   }

   public int getMaxUserCount() {
      return this.maxUserCount;
   }

   public void setMaxUserCount(int maxUserCount) {
      this.maxUserCount = maxUserCount;
   }

   public void addPlayer(MapleCharacter player) {
      this.players.add(player);
   }

   public void deletePlayer(MapleCharacter player) {
      this.players.removeIf(p -> p.getId() == player.getId());
   }

   public int getWaitPlayerCount() {
      return this.getPlayers().size();
   }

   public List<MapleCharacter> getPlayers() {
      return new ArrayList<>(this.players);
   }

   public void addAcceptPlayer(MapleCharacter player) {
      this.acceptPlayers.add(player);
   }

   public int getAcceptPlayerCount() {
      return this.acceptPlayers.size();
   }

   public void clearAcceptPlayers() {
      this.acceptPlayers.clear();
   }

   public void sendWaitQueueSuccess(MapleCharacter player, WaitQueueResult result) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_WAIT_QUEUE_RESPONSE.getValue());
      WaitQueueEntry entry = new WaitQueueEntry(player.getId(), this.waitingType, this.getWaitingQueueID(), this.getFieldID());
      entry.makeWaitQueueSuccess(result);
      entry.encode(packet);
      player.send(packet.getPacket());
   }

   public void sendWaitQueueFailed(MapleCharacter player, WaitQueueError error) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_WAIT_QUEUE_RESPONSE.getValue());
      WaitQueueEntry entry = new WaitQueueEntry(player.getId(), this.waitingType, this.getWaitingQueueID(), this.getFieldID());
      entry.makeWaitQueueError(error);
      entry.encode(packet);
      player.send(packet.getPacket());
   }

   public void sendWaitQueueType(WaitQueueType type) {
      for (MapleCharacter player : this.players) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_WAIT_QUEUE_RESPONSE.getValue());
         WaitQueueEntry entry = new WaitQueueEntry(player.getId(), this.waitingType, this.getWaitingQueueID(), this.getFieldID());
         entry.makeWaitQueueType(type);
         entry.encode(packet);
         player.send(packet.getPacket());
      }
   }

   public void sendWaitQueueTypeOnce(WaitQueueType type, MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_WAIT_QUEUE_RESPONSE.getValue());
      WaitQueueEntry entry = new WaitQueueEntry(player.getId(), this.waitingType, this.getWaitingQueueID(), this.getFieldID());
      entry.makeWaitQueueType(type);
      entry.encode(packet);
      player.send(packet.getPacket());
   }

   public void allPlayerTransferField() {
      for (MapleCharacter p : new ArrayList<>(this.acceptPlayers)) {
         p.setRegisterTransferField(this.getFieldID());
         p.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
      }
   }

   public int getWaitingQueueID() {
      return this.waitingQueueID;
   }

   public void setWaitingQueueID(int waitingQueueID) {
      this.waitingQueueID = waitingQueueID;
   }

   public int getFieldID() {
      return this.fieldID;
   }

   public void setFieldID(int fieldID) {
      this.fieldID = fieldID;
   }
}
