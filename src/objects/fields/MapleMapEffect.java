package objects.fields;

import network.models.CField;
import network.models.CSPacket;
import objects.users.MapleClient;

public class MapleMapEffect {
   private String msg = "";
   private int itemId = 0;
   private boolean active = true;
   private boolean jukebox = false;
   private int unk;

   public MapleMapEffect(String msg, int itemId) {
      this.msg = msg;
      this.itemId = itemId;
   }

   public void setUnk(int unk) {
      this.unk = unk;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public void setJukebox(boolean actie) {
      this.jukebox = actie;
   }

   public boolean isJukebox() {
      return this.jukebox;
   }

   public byte[] makeDestroyData() {
      return this.jukebox ? CSPacket.playCashSong(0, "") : CField.removeMapEffect();
   }

   public byte[] makeStartData() {
      return this.jukebox ? CSPacket.playCashSong(this.itemId, this.msg) : CField.startMapEffect(this.msg, this.itemId, this.active, this.unk);
   }

   public void sendStartData(MapleClient c) {
      c.getSession().writeAndFlush(this.makeStartData());
   }
}
