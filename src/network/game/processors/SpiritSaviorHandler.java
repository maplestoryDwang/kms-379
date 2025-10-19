package network.game.processors;

import java.awt.Point;
import network.decode.PacketDecoder;
import objects.fields.fieldset.instance.SpiritSavior;
import objects.fields.gameobject.Reactor;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class SpiritSaviorHandler {
   public static void AttachReactor(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap().getFieldSetInstance() != null) {
         SpiritSavior fieldset = (SpiritSavior)chr.getMap().getFieldSetInstance();
         if (fieldset != null) {
            Reactor spirit = chr.getMap().getReactorByOid(slea.readInt());
            if (spirit != null) {
               fieldset.attachReactor(chr, spirit);
            }
         }
      }
   }

   public static void SpiritSaviorGoalArea(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap().getFieldSetInstance() != null) {
         SpiritSavior fieldset = (SpiritSavior)chr.getMap().getFieldSetInstance();
         if (fieldset != null) {
            new Point(slea.readInt(), slea.readInt());
            fieldset.goalArea(chr);
         }
      }
   }
}
