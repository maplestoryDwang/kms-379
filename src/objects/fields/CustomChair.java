package objects.fields;

import java.awt.Point;
import java.util.List;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Rect;

public class CustomChair extends MapleMapObject {
   private MapleCharacter owner;
   private int itemID;
   private List<MapleCharacter> players;
   private Rect rect;
   private Point position;

   public CustomChair(MapleCharacter owner, int itemID, List<MapleCharacter> players, Rect rect, Point position) {
      this.owner = owner;
      this.itemID = itemID;
      this.players = players;
      this.rect = rect;
      this.position = position;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getItemID());
      packet.writeInt(this.getPlayers().size());
      packet.writeInt(this.getRect().getLeft() + this.getPosition().x);
      packet.writeInt(this.getRect().getTop() + this.getPosition().y);
      packet.writeInt(this.getRect().getRight() + this.getPosition().x);
      packet.writeInt(this.getRect().getBottom() + this.getPosition().y);
      packet.writeInt(this.getPosition().x);
      packet.writeInt(this.getPosition().y);
      packet.writeInt(this.getPlayers().size());

      for (MapleCharacter player : this.getPlayers()) {
         boolean enable = player != null;
         packet.writeInt(player.getId());
         packet.write(enable);
         packet.writeInt(player.getChairEmotion());
      }

      packet.write(0);
      packet.writeMapleAsciiString("");
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.CUSTOM_CHAIR;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.customChairResult(this.getOwner(), true, false, false, this));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
   }

   public MapleCharacter getOwner() {
      return this.owner;
   }

   public void setOwner(MapleCharacter owner) {
      this.owner = owner;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public List<MapleCharacter> getPlayers() {
      return this.players;
   }

   public void setPlayers(List<MapleCharacter> players) {
      this.players = players;
   }

   public void updatePlayer(MapleCharacter player) {
      this.players.add(player);
   }

   public void removePlayer(MapleCharacter player) {
      this.players.remove(player);
   }

   public Rect getRect() {
      return this.rect;
   }

   public void setRect(Rect rect) {
      this.rect = rect;
   }

   @Override
   public Point getPosition() {
      return this.position;
   }

   @Override
   public void setPosition(Point position) {
      this.position = position;
   }
}
