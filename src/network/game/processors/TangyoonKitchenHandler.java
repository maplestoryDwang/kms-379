package network.game.processors;

import java.awt.Point;
import network.decode.PacketDecoder;
import objects.fields.Field;
import objects.fields.fieldset.instance.PuzzleMaster;
import objects.fields.fieldset.instance.TangyoonKitchen;
import objects.fields.gameobject.Reactor;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class TangyoonKitchenHandler {
   public static void ReadyPickUpFood(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      TangyoonKitchen kitchen = (TangyoonKitchen)user.getMap().getFieldSetInstance();
      if (kitchen != null) {
         int command = slea.readByte();
         if (command == 1) {
            int setFood = slea.readInt();
            if (setFood == 1) {
               int foodCode = slea.readInt();
            } else {
               int foodCode = slea.readInt();
               kitchen.setReadyFood(user.getId(), foodCode);
            }

            user.getMap().broadcastMessage(TangyoonKitchen.TangyoonMultiMotion(user.getId(), true, setFood));
         } else if (command == 0) {
            user.getMap().broadcastMessage(TangyoonKitchen.TangyoonMultiMotion(user.getId(), false, 0));
            kitchen.setReadyFood(user.getId(), -1);
         }
      }
   }

   public static void PickUpFood(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      TangyoonKitchen kitchen = (TangyoonKitchen)user.getMap().getFieldSetInstance();
      if (kitchen != null) {
         int command = slea.readInt();
         if (command == 0) {
            int foodCode = slea.readInt();
            int readyCode = kitchen.getReadyFood(user.getId());
            if (readyCode != -1 && readyCode == foodCode) {
               kitchen.PickUpFood(user, foodCode);
            }
         } else if (command == 1) {
            int ovenNumber = slea.readInt();
            int foodId = slea.readInt();
            if (foodId == 1) {
               kitchen.putOven(user, ovenNumber);
            } else if (foodId == 2) {
               kitchen.pickUpOven(user, ovenNumber);
            }
         } else if (command == 2) {
            int customer = slea.readInt();
            kitchen.serving(user, customer);
         }
      }
   }

   public static void EventMapAutoExit(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         if (user.getMap().getFieldSetInstance() != null) {
            Field target = c.getChannelServer().getMapFactory().getMap(user.getMap().getForcedReturnId());
            if (target != null) {
            }
         }
      }
   }

   public static void PickUpReator(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         if (user.getMap().getFieldSetInstance() != null) {
            PuzzleMaster fieldSet = (PuzzleMaster)user.getMap().getFieldSetInstance();
            Reactor reactor = user.getMap().getReactorByOid(slea.readInt());
            if (reactor != null && fieldSet != null) {
               fieldSet.pickUpPuzzle(user, reactor);
            }
         }
      }
   }

   public static void PutPuzzleReactor(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         if (user.getMap().getFieldSetInstance() != null) {
            PuzzleMaster fieldSet = (PuzzleMaster)user.getMap().getFieldSetInstance();
            if (fieldSet != null) {
               int x = slea.readInt();
               int y = slea.readInt();
               Point pos = new Point(x, y);
               fieldSet.putDownPuzzle(user, pos);
            }
         }
      }
   }
}
