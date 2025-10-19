package objects.fields.child.sernium;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class Field_Seren extends Field {
   public Field_Seren(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.UIPacket.endInGameDirectionMode(0));
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   public void sendSerenLaser(int objectID, int skillLevel, int attackDelay, List<SerenLaser> lasers) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SEREN_LASER.getValue());
      packet.writeInt(objectID);
      packet.writeInt(skillLevel);
      packet.writeInt(attackDelay);
      packet.writeInt(lasers.size());
      lasers.forEach(laser -> laser.encode(packet));
      this.broadcastMessage(packet.getPacket());
   }

   public boolean addSerenGauge(MapleCharacter player, int max, int delta) {
      if (player.getBuffedValue(SecondaryStatFlag.NotIncSerenGauge) != null) {
         return false;
      } else {
         player.setSerenGauge(max, player.getSerenGauge() + delta);
         if (player.getSerenGauge() >= max) {
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.GiveMeHeal, 1);
            statups.put(SecondaryStatFlag.SerenFreeze, 1);
            statups.put(SecondaryStatFlag.NotIncSerenGauge, 1);
            player.temporaryStatSet(182, 3, 5000, statups, false, 0, false, true);
            player.send(CField.makeEffectScreen("UI/UIWindow7.img/SerenDeath"));
            player.setSerenGauge(max, 0);
            this.sendSerenGauge(player, max, player.getSerenGauge());
            return true;
         } else {
            this.sendSerenGauge(player, max, player.getSerenGauge());
            return false;
         }
      }
   }

   public void sendSerenGauge(MapleCharacter player, int max, int current) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.APPLY_SEREN_GAUGE.getValue());
      packet.writeInt(max);
      packet.writeInt(current);
      player.send(packet.getPacket());
   }
}
