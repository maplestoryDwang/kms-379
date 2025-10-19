package objects.fields.gameobject;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.Party;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.Portal;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class TownPortal extends MapleMapObject {
   private WeakReference<MapleCharacter> owner;
   private Field town;
   private Portal townPortal;
   private Field target;
   private int skillId;
   private int ownerId;
   private Point targetPosition;

   public TownPortal(MapleCharacter owner, Point targetPosition, int skillId) {
      this.owner = new WeakReference<>(owner);
      this.ownerId = owner.getId();
      this.target = owner.getMap();
      this.targetPosition = targetPosition;
      this.setPosition(this.targetPosition);
      this.town = this.target.getReturnMap();
      this.townPortal = this.getFreePortal();
      this.skillId = skillId;
   }

   public TownPortal(TownPortal origDoor) {
      this.owner = new WeakReference<>(origDoor.owner.get());
      this.town = origDoor.town;
      this.townPortal = origDoor.townPortal;
      this.target = origDoor.target;
      this.targetPosition = new Point(origDoor.targetPosition);
      this.skillId = origDoor.skillId;
      this.ownerId = origDoor.ownerId;
      this.setPosition(this.townPortal.getPosition());
   }

   public final int getSkill() {
      return this.skillId;
   }

   public final int getOwnerId() {
      return this.ownerId;
   }

   private final Portal getFreePortal() {
      List<Portal> freePortals = new ArrayList<>();

      for (Portal port : this.town.getPortals()) {
         if (port.getType() == 6) {
            freePortals.add(port);
         }
      }

      freePortals.sort(Comparator.comparingInt(Portal::getId));

      for (MapleMapObject obj : this.town.getAllDoorsThreadsafe()) {
         TownPortal door = (TownPortal)obj;
         if (door.getOwner() != null
            && door.getOwner().getParty() != null
            && this.getOwner() != null
            && this.getOwner().getParty() != null
            && this.getOwner().getParty().getId() == door.getOwner().getParty().getId()) {
            return null;
         }

         freePortals.remove(door.getTownPortal());
      }

      return freePortals.size() <= 0 ? null : freePortals.iterator().next();
   }

   @Override
   public final void sendSpawnData(MapleClient client) {
      if (this.getOwner() != null && this.target != null && client.getPlayer() != null) {
         if (this.target.getId() == client.getPlayer().getMapId()
            || this.getOwnerId() == client.getPlayer().getId()
            || this.getOwner() != null
               && this.getOwner().getParty() != null
               && client.getPlayer().getParty() != null
               && this.getOwner().getParty().getId() == client.getPlayer().getParty().getId()) {
            client.getSession()
               .writeAndFlush(
                  CField.spawnDoor(
                     this.getOwnerId(), this.target.getId() == client.getPlayer().getMapId() ? this.targetPosition : this.townPortal.getPosition(), true
                  )
               );
            if (this.getOwner() != null
               && this.getOwner().getParty() != null
               && client.getPlayer().getParty() != null
               && (this.getOwnerId() == client.getPlayer().getId() || this.getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
               PacketEncoder packet = new PacketEncoder();
               Party.PartyPacket.TownPortal portal = new Party.PartyPacket.TownPortal(
                  this.town.getId(),
                  this.target.getId(),
                  this.skillId,
                  true,
                  this.target.getId() == client.getPlayer().getMapId() ? this.targetPosition : this.townPortal.getPosition()
               );
               portal.encode(packet);
               client.getSession().writeAndFlush(packet.getPacket());
            }

            client.getSession()
               .writeAndFlush(
                  CWvsContext.spawnPortal(
                     this.town.getId(),
                     this.target.getId(),
                     this.skillId,
                     this.target.getId() == client.getPlayer().getMapId() ? this.targetPosition : this.townPortal.getPosition()
                  )
               );
         }
      }
   }

   @Override
   public final void sendDestroyData(MapleClient client) {
      if (client.getPlayer() != null && this.getOwner() != null && this.target != null) {
         if (this.target.getId() == client.getPlayer().getMapId()
            || this.getOwnerId() == client.getPlayer().getId()
            || this.getOwner() != null
               && this.getOwner().getParty() != null
               && client.getPlayer().getParty() != null
               && this.getOwner().getParty().getId() == client.getPlayer().getParty().getId()) {
            client.getSession().writeAndFlush(CField.removeDoor(this.getOwnerId(), false));
            if (this.getOwner() != null
               && this.getOwner().getParty() != null
               && client.getPlayer().getParty() != null
               && (this.getOwnerId() == client.getPlayer().getId() || this.getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
               PacketEncoder packet = new PacketEncoder();
               Party.PartyPacket.TownPortal portal = new Party.PartyPacket.TownPortal(999999999, 999999999, 0, false, new Point(-1, -1));
               portal.encode(packet);
               client.getSession().writeAndFlush(packet.getPacket());
            }

            client.getSession().writeAndFlush(CWvsContext.spawnPortal(999999999, 999999999, 0, null));
         }
      }
   }

   public final void warp(MapleCharacter chr, boolean toTown) {
      if (chr.getId() == this.getOwnerId()
         || this.getOwner() != null
            && this.getOwner().getParty() != null
            && chr.getParty() != null
            && this.getOwner().getParty().getId() == chr.getParty().getId()) {
         if (!toTown) {
            chr.changeMap(this.target, this.target.findClosestPortal(this.targetPosition));
         } else {
            chr.changeMap(this.town, this.townPortal);
         }
      } else {
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public final MapleCharacter getOwner() {
      return this.owner.get();
   }

   public final Field getTown() {
      return this.town;
   }

   public final Portal getTownPortal() {
      return this.townPortal;
   }

   public final Field getTarget() {
      return this.target;
   }

   public final Point getTargetPosition() {
      return this.targetPosition;
   }

   @Override
   public final MapleMapObjectType getType() {
      return MapleMapObjectType.DOOR;
   }
}
