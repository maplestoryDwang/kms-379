package scripting;

import constants.GameConstants;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import network.game.GameServer;
import network.models.CField;
import objects.fields.ReactorDropEntry;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;

public class ReactorActionManager extends AbstractPlayerInteraction {
   private Reactor reactor;

   public ReactorActionManager(MapleClient c, Reactor reactor) {
      super(c, reactor.getReactorId(), c.getPlayer().getMapId());
      this.reactor = reactor;
   }

   public void dropItems() {
      this.dropItems(false, 0, 0, 0, 0);
   }

   public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
      this.dropItems(meso, mesoChance, minMeso, maxMeso, 0);
   }

   public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
      List<ReactorDropEntry> chances = ReactorScriptManager.getInstance().getDrops(this.reactor.getReactorId());
      List<ReactorDropEntry> items = new LinkedList<>();
      if (meso && Randomizer.nextInt(1000000) < mesoChance) {
         items.add(new ReactorDropEntry(0, mesoChance, -1));
      }

      int numItems = 0;

      for (ReactorDropEntry d : chances) {
         if (Randomizer.nextInt(1000000) < d.chance && (d.questid <= 0 || this.getPlayer().getQuestStatus(d.questid) == 1)) {
            numItems++;
            items.add(d);
         }
      }

      while (items.size() < minItems) {
         items.add(new ReactorDropEntry(0, mesoChance, -1));
         numItems++;
      }

      Point dropPos = this.reactor.getPosition();
      dropPos.x -= 12 * numItems;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (ReactorDropEntry dx : items) {
         if (dx.itemId == 0) {
            int range = maxMeso - minMeso;
            int mesoDrop = (int)(Randomizer.nextInt(range) + minMeso * GameServer.getInstance(this.getClient().getChannel()).getMesoRate());
            this.reactor.getMap().spawnMesoDrop(mesoDrop, dropPos, this.reactor, this.getPlayer(), false, (byte)0);
         } else {
            Item drop;
            if (GameConstants.getInventoryType(dx.itemId) != MapleInventoryType.EQUIP) {
               drop = new Item(dx.itemId, (short)0, (short)1, 0);
            } else {
               drop = ii.randomizeStats((Equip)ii.getEquipById(dx.itemId));
            }

            drop.setGMLog(
               FileoutputUtil.CurrentReadable_Time()
                  + "에 "
                  + this.c.getPlayer().getName()
                  + "이(가) "
                  + this.reactor.getReactorId()
                  + " 리액터로 부터 얻은 아이템. (맵ID : "
                  + this.getPlayer().getMapId()
                  + ")"
            );
            this.reactor.getMap().spawnItemDrop(this.reactor, this.getPlayer(), drop, dropPos, false, false);
         }

         dropPos.x += 25;
      }
   }

   public void dropSingleItem(int itemId) {
      Item drop;
      if (GameConstants.getInventoryType(itemId) != MapleInventoryType.EQUIP) {
         drop = new Item(itemId, (short)0, (short)1, 0);
      } else {
         drop = MapleItemInformationProvider.getInstance().randomizeStats((Equip)MapleItemInformationProvider.getInstance().getEquipById(itemId));
      }

      drop.setGMLog("Dropped from reactor " + this.reactor.getReactorId() + " on map " + this.getPlayer().getMapId());
      this.reactor.getMap().spawnItemDrop(this.reactor, this.getPlayer(), drop, this.reactor.getPosition(), false, false);
   }

   @Override
   public void spawnNpc(int npcId) {
      this.spawnNpc(npcId, this.getPosition());
   }

   public Point getPosition() {
      Point pos = this.reactor.getPosition();
      pos.y -= 10;
      return pos;
   }

   public Reactor getReactor() {
      return this.reactor;
   }

   public void spawnZakum() {
      this.reactor.getMap().spawnZakum(this.getPosition().x, this.getPosition().y);
   }

   public void spawnFakeMonster(int id) {
      this.spawnFakeMonster(id, 1, this.getPosition());
   }

   public void spawnFakeMonster(int id, int x, int y) {
      this.spawnFakeMonster(id, 1, new Point(x, y));
   }

   public void spawnFakeMonster(int id, int qty) {
      this.spawnFakeMonster(id, qty, this.getPosition());
   }

   public void spawnFakeMonster(int id, int qty, int x, int y) {
      this.spawnFakeMonster(id, qty, new Point(x, y));
   }

   private void spawnFakeMonster(int id, int qty, Point pos) {
      for (int i = 0; i < qty; i++) {
         this.reactor.getMap().spawnFakeMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
      }
   }

   public void killAll() {
      this.reactor.getMap().killAllMonsters(true);
   }

   public void killMonster(int monsId) {
      this.reactor.getMap().killMonster(monsId);
   }

   @Override
   public void spawnMonster(int id) {
      this.spawnMonster(id, 1, this.getPosition());
   }

   @Override
   public void spawnMonster(int id, int qty) {
      this.spawnMonster(id, qty, this.getPosition());
   }

   public void dispelAllMonsters(int num) {
   }

   public void cancelHarvest(boolean succ) {
      this.getPlayer().setFatigue((byte)(this.getPlayer().getFatigue() + 1));
      this.getPlayer().getMap().broadcastMessage(this.getPlayer(), CField.showHarvesting(this.getPlayer().getId(), 0), false);
      this.getPlayer().getMap().broadcastMessage(CField.harvestResult(this.getPlayer().getId(), succ));
   }

   public void doHarvest() {
      int pID = this.getReactor().getReactorId() < 200000 ? 92000000 : 92010000;
      String pName = this.getReactor().getReactorId() < 200000 ? "채집" : "채광";
      if (this.getPlayer().getFatigue() < 200 && !(this.getReactor().getTruePosition().distanceSq(this.getPlayer().getTruePosition()) > 10000.0)) {
         int he = this.getPlayer().getProfessionLevel(pID);
         if (he > 0) {
            int hm = this.getReactor().getReactorId() % 100;
            int successChance = 90 + (he - hm) * 10;
            if (this.getReactor().getReactorId() % 100 == 10) {
               hm = 1;
               successChance = 100;
            } else if (this.getReactor().getReactorId() % 100 == 11) {
               hm = 10;
               successChance -= 40;
            }

            this.getPlayer().getStat().checkEquipDurabilitys(this.getPlayer(), -1, true);
            int masteryIncrease = (hm - he) * 2 + 20;
            boolean succ = this.randInt(100) < successChance;
            if (!succ) {
               masteryIncrease /= 10;
               this.dropSingleItem(this.getReactor().getReactorId() < 200000 ? 4022023 : 4010010);
            } else {
               this.dropItems();
               if (this.getReactor().getReactorId() < 200000) {
                  this.addTrait("sense", 5);
                  if (Randomizer.nextInt(10) == 0) {
                     this.dropSingleItem(2440000);
                  }

                  if (Randomizer.nextInt(100) == 0) {
                     this.dropSingleItem(4032933);
                  }
               } else {
                  this.addTrait("insight", 5);
                  if (Randomizer.nextInt(10) == 0) {
                     this.dropSingleItem(2440001);
                  }
               }
            }

            this.cancelHarvest(succ);
            this.playerMessage(5, pName + "의 숙련도가 증가하였습니다. (+" + masteryIncrease + ")");
            this.getPlayer().addProfessionExp(pID, masteryIncrease);
            AchievementFactory.checkMakingSkillFatigueInc(this.getPlayer(), pID, 1);
            AchievementFactory.checkMakingskillGather(this.getPlayer(), succ, pID);
         }
      } else {
         this.c.getPlayer().dropMessage(5, "피로도가 부족하여" + pName + "을 할 수 없습니다.");
      }
   }
}
