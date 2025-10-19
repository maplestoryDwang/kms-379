package objects.fields.child.fritto;

import constants.QuestExConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.Drop;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;
import objects.wz.provider.MapleData;

public class Field_ReceivingTreasure extends Field {
   private boolean startGame = false;
   private boolean displayStartGame = false;
   private boolean endGame = false;
   private boolean displayEndGame = false;
   private long startGameTime = 0L;
   private long displayStartGameTime = 0L;
   private long endGameTime = 0L;
   private long lastDropTime = 0L;
   private PocketDrop pocketDrop;
   private Map<Integer, Long> dropCooltimes = new HashMap<>();
   private MapleCharacter player = null;

   public Field_ReceivingTreasure(int mapid, int channel, int returnMapId, float monsterRate, MapleData data) {
      super(mapid, channel, returnMapId, monsterRate);
      if (data != null) {
         this.pocketDrop = new PocketDrop(data);
      }
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player != null) {
         if (this.endGame) {
            if (!this.displayEndGame) {
               this.player.send(CField.showEffect("killing/clear"));
               this.player.setRegisterTransferField(993000601);
               this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
               this.displayEndGame = true;
            }
         } else {
            if (this.startGameTime == 0L) {
               this.broadcastMessage(CField.startMapEffect("점프키와 방향키를 이용해 내가 떨어트리는 보물을 최대한 많이 받아줘!", 5120160, true, 10));
               this.startGameTime = System.currentTimeMillis() + 6000L;
               this.endGameTime = System.currentTimeMillis() + 66000L;
            }

            if (!this.startGame && this.startGameTime <= System.currentTimeMillis()) {
               this.broadcastMessage(CField.getStartClock(this.pocketDrop.getPlayTime()));
               this.startGame = true;
            }

            if (this.endGameTime <= System.currentTimeMillis()) {
               this.player.send(CField.showEffect("killing/timeout"));
               this.player.setRegisterTransferField(993000601);
               this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
               this.endGame = true;
               this.displayEndGame = true;
            } else {
               if (!this.startGame) {
                  if (this.displayStartGameTime == 0L) {
                     this.displayStartGameTime = System.currentTimeMillis() + 3000L;
                  }

                  if (!this.displayStartGame && this.displayStartGameTime <= System.currentTimeMillis()) {
                     this.displayStartGame = true;
                     this.broadcastMessage(CField.showEffect("defense/count"));
                     this.broadcastMessage(CField.environmentChange("killing/first/start", 17, 1000));
                     return;
                  }
               } else if (this.lastDropTime == 0L || System.currentTimeMillis() - this.lastDropTime >= this.pocketDrop.getDropInterval()) {
                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.FRITTO_ACTION.getValue());
                  this.broadcastMessage(packet.getPacket());
                  int dropCount = 1;
                  int r = Randomizer.rand(0, 100);
                  byte var17;
                  if (r < 20) {
                     var17 = 1;
                  } else if (r < 80) {
                     var17 = 2;
                  } else if (r < 90) {
                     var17 = 3;
                  } else if (r < 95) {
                     var17 = 4;
                  } else {
                     var17 = 5;
                  }

                  List<Point> dropPoints = new ArrayList<>();

                  for (int i = 0; i < var17; i++) {
                     Collections.shuffle(this.pocketDrop.getDropItems());
                     List<PocketDropItem> drop = this.pocketDrop
                        .getDropItems()
                        .stream()
                        .filter(
                           item -> this.dropCooltimes.get(item.getItemID()) == null
                              || this.dropCooltimes.get(item.getItemID()) != null
                                 && System.currentTimeMillis() - this.dropCooltimes.get(item.getItemID()) >= item.getCoolTime()
                        )
                        .filter(item -> item.getWeight() != -1)
                        .collect(Collectors.toList());
                     int maxWeight = 0;

                     for (PocketDropItem pdi : drop) {
                        maxWeight += pdi.getWeight();
                     }

                     Collections.shuffle(drop);
                     int rand = Randomizer.rand(1, maxWeight);
                     int count = 0;
                     PocketDropItem pickItem = null;

                     for (PocketDropItem pdi : drop) {
                        count += pdi.getWeight();
                        if (count >= rand) {
                           pickItem = pdi;
                           break;
                        }
                     }

                     if (pickItem != null) {
                        Item item = new Item(pickItem.getItemID(), (short)0, (short)1, 0);
                        Point pos = null;
                        boolean check = true;
                        int c = 0;

                        do {
                           pos = new Point(Randomizer.rand(this.pocketDrop.getLt().x, this.pocketDrop.getRb().x), this.pocketDrop.getLt().y);

                           for (Point p : dropPoints) {
                              if (!(Math.abs(pos.getX() - p.getX()) >= this.pocketDrop.getMinDropRange())) {
                                 check = false;
                              }
                           }
                        } while (!check && c++ < 3000);

                        dropPoints.add(pos);
                        Drop dropItem = new Drop(item, pos, this.player, this.player, (byte)2, true, null);
                        dropItem.setDropMotionType(4);
                        dropItem.setDropSpeed(Randomizer.rand(400, 500));
                        dropItem.setCollisionPickUp(true);
                        dropItem.setDropDelay(this.pocketDrop.getDropDelay());
                        this.spawnItemDrop(this.player, this.player, dropItem, this.pocketDrop.getStart(), pos);
                        this.dropCooltimes.put(item.getItemId(), System.currentTimeMillis());
                        this.lastDropTime = System.currentTimeMillis();
                     }
                  }
               }
            }
         }
      }
   }

   public void catchItem(int itemID) {
      int score = this.player.getOneInfoQuestInteger(26022, "score");
      PocketDropItem item = null;

      for (PocketDropItem i : this.pocketDrop.getDropItems()) {
         if (i.getItemID() == itemID) {
            item = i;
            break;
         }
      }

      if (item != null) {
         score = Math.max(0, score + item.getScore());
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.FRITTO_RECEIVING_TREASURE_SCORE.getValue());
         packet.writeInt(item.getScore());
         packet.writeInt(score);
         this.player.send(packet.getPacket());
         this.player.updateOneInfo(26022, "score", String.valueOf(score));
         if (score >= this.pocketDrop.getMaxScore()) {
            this.endGame = true;
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.startGame = false;
      this.displayStartGame = false;
      this.endGame = false;
      this.displayEndGame = false;
      this.startGameTime = 0L;
      this.displayStartGameTime = 0L;
      this.endGameTime = 0L;
      this.lastDropTime = 0L;
      this.player = null;
      this.dropCooltimes = new HashMap<>();
   }

   @Override
   public void onEnter(MapleCharacter player) {
      this.resetFully(false);
      super.onEnter(player);
      this.player = player;
      player.temporaryStatSet(80002896, Integer.MAX_VALUE, SecondaryStatFlag.RideVehicle, 1932644);
      player.send(CField.getStopClock(this.pocketDrop.getPlayTime()));
      player.updateOneInfo(26022, "gameType", "1");
      player.updateOneInfo(26022, "reward", "1");
      player.updateOneInfo(26022, "score", "0");
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      player.temporaryStatReset(SecondaryStatFlag.RideVehicle);
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   public boolean isEndGame() {
      return this.endGame;
   }

   public void setEndGame(boolean endGame) {
      this.endGame = endGame;
   }
}
