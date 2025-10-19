package network.game.processors;

import java.awt.Point;
import network.decode.PacketDecoder;
import objects.fields.fieldset.instance.ErdaSpectrum;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Timer;

public class ErdaSpectrumHandler {
   public static void ErdaSpectrumBombAction(PacketDecoder slea, final MapleClient c) {
      int mapId = slea.readInt();
      if (c.getPlayer().getMap().getId() == mapId && c.getPlayer().getMap().getFieldSetInstance() != null) {
         slea.readInt();
         int type = slea.readInt();
         if (type == 33) {
            slea.readInt();
            slea.readInt();
            ErdaSpectrum fieldset = (ErdaSpectrum)c.getPlayer().getMap().getFieldSetInstance();
            int Erda = Integer.parseInt(fieldset.getVar("Erda")) + 10;
            fieldset.setVar("Erda", String.valueOf(Erda));
            final Point pos = new Point(Integer.parseInt(fieldset.getVar("ballPos").split(",")[0]), Integer.parseInt(fieldset.getVar("ballPos").split(",")[1]));

            for (int i = 0; i < 10; i++) {
               Timer.EventTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getPlayer().getMap().broadcastMessage(ErdaSpectrum.ErdaSpectrumIncEffect(pos));
                  }
               }, 50 * i);
            }

            if (c.getPlayer().getMap().getId() != 450001500) {
               if (c.getPlayer().getMap().getId() == 450001400) {
                  c.getPlayer()
                     .getMap()
                     .broadcastMessage(ErdaSpectrum.ErdaSpectrumErdaInfo(Erda, Integer.parseInt(fieldset.getVar("TransferCount")), fieldset.getVar("lastPuck")));
               } else {
                  c.getPlayer()
                     .getMap()
                     .broadcastMessage(ErdaSpectrum.ErdaSpectrumCrackInfo(Erda, c.getPlayer().getMap().getAllMonstersThreadsafe().size() - 1));
               }
            } else {
               c.getPlayer().getMap().broadcastMessage(ErdaSpectrum.ErdaSpectrumWormInfo(Erda, fieldset.getVar("elim")));
            }

            c.getPlayer()
               .getMap()
               .broadcastMessage(ErdaSpectrum.ErdaSpectrumErdaInfo(Erda, Integer.parseInt(fieldset.getVar("TransferCount")), fieldset.getVar("lastPuck")));
         }
      }
   }

   public static void ErdaSpectrumTouchObject(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (c.getPlayer().getMap().getFieldSetInstance() != null && chr != null) {
         ErdaSpectrum fieldset = (ErdaSpectrum)chr.getMap().getFieldSetInstance();
         if (fieldset != null) {
            if (Integer.parseInt(fieldset.getVar("Erda")) >= 10) {
               fieldset.erdaDec();
               fieldset.changePuck();
               chr.getMap().setLastRespawnTime(Long.MAX_VALUE);
               chr.getMap().killAllMonsters(true);
               chr.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8641018), new Point(483, 47));
               chr.getMap().broadcastMessage(ErdaSpectrum.ErdaSpectrumPhase(3));
            } else {
               chr.dropMessage(5, "에르다 응집기를 사용하는 데 필요한 에르다가 부족합니다.");
            }
         }
      }
   }

   public static void ErdaSpectrumPuckInArea(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr.getMap().getFieldSetInstance() != null && chr != null) {
         ErdaSpectrum fieldset = (ErdaSpectrum)chr.getMap().getFieldSetInstance();
         if (fieldset != null) {
            int areaType = slea.readInt();
            slea.readInt();
            int objectId = slea.readInt();
            MapleMonster monster = chr.getMap().getMonsterByOid(objectId);
            if (monster != null) {
               chr.getMap().removeMonster(monster, 1);
               switch (monster.getId()) {
                  case 8641019:
                     if (areaType == 0) {
                        int redCountx = Integer.parseInt(fieldset.getVar("Red"));
                        fieldset.setVar("Red", String.valueOf(redCountx + 1));
                        fieldset.addTransferCount(1);
                     }
                     break;
                  case 8641020:
                     if (areaType == 1) {
                        int blueCountx = Integer.parseInt(fieldset.getVar("Blue"));
                        fieldset.setVar("Blue", String.valueOf(blueCountx + 1));
                        fieldset.addTransferCount(1);
                     }
                     break;
                  case 8641021:
                     int redCount = Integer.parseInt(fieldset.getVar("Red"));
                     fieldset.setVar("Red", String.valueOf(redCount + 1));
                     int blueCount = Integer.parseInt(fieldset.getVar("Blue"));
                     fieldset.setVar("Blue", String.valueOf(blueCount + 1));
                     fieldset.addTransferCount(2);
               }
            }
         }
      }
   }
}
