package scripting;

import objects.fields.Portal;
import objects.users.MapleClient;

public class PortalPlayerInteraction extends AbstractPlayerInteraction {
   private final Portal portal;

   public PortalPlayerInteraction(MapleClient c, Portal portal) {
      super(c, portal.getId(), c.getPlayer().getMapId());
      this.portal = portal;
   }

   public final Portal getPortal() {
      return this.portal;
   }

   public final void inFreeMarket() {
      if (this.getMapId() != 910000000) {
         this.saveLocation("FREE_MARKET");
         this.playPortalSE();
         this.warp(910000000, "st00");
      }
   }

   public final void inArdentmill() {
      if (this.getMapId() != 910001000) {
         if (this.getPlayer().getLevel() >= 30) {
            this.saveLocation("ARDENTMILL");
            this.playPortalSE();
            this.warp(910001000, "st00");
         } else {
            this.playerMessage(5, "레벨 30 이상 되어야 마이스터 빌로 이동하실 수 있습니다.");
         }
      }
   }

   @Override
   public void spawnMonster(int id) {
      this.spawnMonster(id, 1, this.portal.getPosition());
   }

   @Override
   public void spawnMonster(int id, int qty) {
      this.spawnMonster(id, qty, this.portal.getPosition());
   }
}
