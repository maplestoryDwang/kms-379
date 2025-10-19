package objects.fields.child.karing;

import network.decode.PacketDecoder;
import objects.context.party.PartyMemberEntry;
import objects.fields.child.karing.KaringMatching.KaringMatchType;
import objects.fields.child.karing.KaringMatching.KaringMatching;
import objects.users.MapleCharacter;

public class Field_BossKaringMatch extends Field_BossKaring {
   public int position = 0;

   public Field_BossKaringMatch(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   public void doMatching(PacketDecoder packet, MapleCharacter player) {
      int type = packet.readInt();
      KaringMatchType.Recv actionType = KaringMatchType.Recv.getType(type);
      player.dropMessage(-5, "첫 타입 : " + actionType);
      switch (actionType) {
         case BossSelect:
            int types = packet.readInt();
            this.position = 0;
            player.dropMessage(-5, " 두번쨰 : " + types);
            switch (types) {
               case 0:
                  this.addGoongiParty(player);
                  this.removeDoolParty(player);
                  this.removeHondonParty(player);
                  break;
               case 1:
                  this.addDoolParty(player);
                  this.removeGoongiParty(player);
                  this.removeHondonParty(player);
                  break;
               case 2:
                  this.addHondonParty(player);
                  this.removeGoongiParty(player);
                  this.removeDoolParty(player);
                  break;
               case 255:
                  this.removeGoongiParty(player);
                  this.removeDoolParty(player);
                  this.removeHondonParty(player);
                  this.position = -1;
            }

            this.position = this.getGoongiParty().size() <= 1 && this.getDoolParty().size() <= 1 && this.getHondonParty().size() <= 1 ? 0 : 1;
            KaringMatching.InitPacket Select = new KaringMatching.InitPacket.Select(player, player.getParty(), types, this.position);
            Select.broadcastPacket(this);
            break;
         case Ready:
            boolean ready = packet.readByte() == 1;
            KaringMatching.InitPacket Ready = new KaringMatching.InitPacket.Ready(player, player.getParty(), ready);
            Ready.broadcastPacket(this);
            break;
         case Join:
            for (MapleCharacter chr : this.getGoongiParty()) {
               chr.warp(410007120);
            }

            for (MapleCharacter chr : this.getDoolParty()) {
               chr.warp(410007160);
            }

            for (MapleCharacter chr : this.getHondonParty()) {
               chr.warp(410007200);
            }
            break;
         case Leave:
            for (PartyMemberEntry chr : player.getParty().getMembers()) {
               MapleCharacter curChar = player.getClient().getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
               curChar.warp(410007025);
            }
            break;
         default:
            System.err.println("[Karing] 처리 되지 않은 리시브가 있습니다. type : " + type);
      }
   }
}
