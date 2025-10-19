package objects.fields;

import java.awt.Point;
import network.game.GameServer;
import network.models.CWvsContext;
import objects.users.MapleClient;
import scripting.PortalScriptManager;
import scripting.newscripting.ScriptManager;

public class Portal {
   public static final int MAP_PORTAL = 2;
   public static final int DOOR_PORTAL = 6;
   private String name;
   private String target;
   private String scriptName;
   private Point position;
   private int targetmap;
   private int type;
   private int id;
   private boolean portalState = true;

   public Portal(int type) {
      this.type = type;
   }

   public final int getId() {
      return this.id;
   }

   public final void setId(int id) {
      this.id = id;
   }

   public final String getName() {
      return this.name;
   }

   public final Point getPosition() {
      return this.position;
   }

   public final String getTarget() {
      return this.target;
   }

   public final int getTargetMapId() {
      return this.targetmap;
   }

   public final int getType() {
      return this.type;
   }

   public final String getScriptName() {
      return this.scriptName != null && this.scriptName.substring(0, 1).matches("[0-9]") ? "_" + this.scriptName : this.scriptName;
   }

   public final void setName(String name) {
      this.name = name;
   }

   public final void setPosition(Point position) {
      this.position = position;
   }

   public final void setTarget(String target) {
      this.target = target;
   }

   public final void setTargetMapId(int targetmapid) {
      this.targetmap = targetmapid;
   }

   public final void setScriptName(String scriptName) {
      this.scriptName = scriptName;
   }

   public final void enterPortal(MapleClient c) {
      Field currentmap = c.getPlayer().getMap();
      if (this.portalState || c.getPlayer().isGM()) {
         if (this.getScriptName() != null) {
            c.getPlayer().checkFollow();
            if (c.getPlayer().isGM()) {
               c.getPlayer().dropMessage(5, "PortalScript : " + this.getScriptName());
               System.out.println("PortalScript : " + this.getScriptName());
            }

            if (ScriptManager.get()._scripts.get(this.getScriptName()) != null) {
               ScriptManager.runScript(c, this.getScriptName(), null, this);
            } else {
               try {
                  PortalScriptManager.getInstance().executePortalScript(this, c);
               } catch (Exception var4) {
                  System.out.println("Portal Script Err");
                  var4.printStackTrace();
               }
            }
         } else if (this.getTargetMapId() != 999999999) {
            Field to = GameServer.getInstance(c.getChannel()).getMapFactory().getMap(this.getTargetMapId());
            if (to == null) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            if (!c.getPlayer().isGM() && to.getLevelLimit() > 0 && to.getLevelLimit() > c.getPlayer().getLevel()) {
               c.getPlayer().dropMessage(-1, "You are too low of a level to enter this place.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            c.getPlayer().changeMapPortal(to, to.getPortal(this.getTarget()) == null ? to.getPortal(0) : to.getPortal(this.getTarget()));
         }
      }

      if (c != null && c.getPlayer() != null && c.getPlayer().getMap() == currentmap) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public boolean getPortalState() {
      return this.portalState;
   }

   public void setPortalState(boolean ps) {
      this.portalState = ps;
   }
}
