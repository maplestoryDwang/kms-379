package network.game.processors.inventory;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import network.decode.PacketDecoder;
import objects.effect.child.ItemMaker;
import objects.item.Equip;
import objects.item.Item;
import objects.item.ItemMakerFactory;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.PlayerStats;
import objects.users.skills.SkillFactory;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class ItemMakerHandler {
   public static final void ItemMaker(PacketDecoder slea, MapleClient c) {
      int makerType = slea.readInt();
      switch (makerType) {
         case 1:
            int toCreate = slea.readInt();
            if (GameConstants.isGem(toCreate)) {
               ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
               if (gem == null) {
                  return;
               }

               if (!hasSkill(c, gem.getReqSkillLevel())) {
                  return;
               }

               if (c.getPlayer().getMeso() < gem.getCost()) {
                  return;
               }

               int randGemGiven = getRandomGem(gem.getRandomReward());
               if (c.getPlayer().getInventory(GameConstants.getInventoryType(randGemGiven)).isFull()) {
                  return;
               }

               int taken = checkRequiredNRemove(c, gem.getReqRecipes());
               if (taken == 0) {
                  return;
               }

               c.getPlayer().gainMeso(-gem.getCost(), false);
               MapleInventoryManipulator.addById(
                  c, randGemGiven, (byte)(taken == randGemGiven ? 9 : 1), "Made by Gem " + toCreate + " on " + FileoutputUtil.CurrentReadable_Date()
               );
               ItemMaker e = new ItemMaker(c.getPlayer().getId(), 0);
               c.getSession().writeAndFlush(e.encodeForLocal());
               c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
            } else if (GameConstants.isOtherGem(toCreate)) {
               ItemMakerFactory.GemCreateEntry gemx = ItemMakerFactory.getInstance().getGemInfo(toCreate);
               if (gemx == null) {
                  return;
               }

               if (!hasSkill(c, gemx.getReqSkillLevel())) {
                  return;
               }

               if (c.getPlayer().getMeso() < gemx.getCost()) {
                  return;
               }

               if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                  return;
               }

               if (checkRequiredNRemove(c, gemx.getReqRecipes()) == 0) {
                  return;
               }

               c.getPlayer().gainMeso(-gemx.getCost(), false);
               if (GameConstants.getInventoryType(toCreate) == MapleInventoryType.EQUIP) {
                  MapleInventoryManipulator.addbyItem(c, MapleItemInformationProvider.getInstance().getEquipById(toCreate));
               } else {
                  MapleInventoryManipulator.addById(c, toCreate, (short)1, "Made by Gem " + toCreate + " on " + FileoutputUtil.CurrentReadable_Date());
               }

               ItemMaker e = new ItemMaker(c.getPlayer().getId(), 0);
               c.getSession().writeAndFlush(e.encodeForLocal());
               c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
            } else {
               boolean stimulator = slea.readByte() > 0;
               int numEnchanter = slea.readInt();
               ItemMakerFactory.ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(toCreate);
               if (create == null) {
                  return;
               }

               if (numEnchanter > create.getTUC()) {
                  return;
               }

               if (!hasSkill(c, create.getReqSkillLevel())) {
                  return;
               }

               if (c.getPlayer().getMeso() < create.getCost()) {
                  return;
               }

               if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                  return;
               }

               if (checkRequiredNRemove(c, create.getReqItems()) == 0) {
                  return;
               }

               c.getPlayer().gainMeso(-create.getCost(), false);
               MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
               Equip toGive = (Equip)ii.getEquipById(toCreate);
               if (stimulator || numEnchanter > 0) {
                  if (c.getPlayer().haveItem(create.getStimulator(), 1, false, true)) {
                     ii.randomizeStats_Above(toGive);
                     MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
                  }

                  for (int i = 0; i < numEnchanter; i++) {
                     int enchant = slea.readInt();
                     if (c.getPlayer().haveItem(enchant, 1, false, true)) {
                        Map<String, Integer> stats = ii.getEquipStats(enchant);
                        if (stats != null) {
                           addEnchantStats(stats, toGive);
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, enchant, 1, false, false);
                        }
                     }
                  }
               }

               if (stimulator && Randomizer.nextInt(10) == 0) {
                  c.getPlayer().dropMessage(5, "The item was overwhelmed by the stimulator.");
               } else {
                  MapleInventoryManipulator.addbyItem(c, toGive);
                  ItemMaker e = new ItemMaker(c.getPlayer().getId(), 0);
                  c.getSession().writeAndFlush(e.encodeForLocal());
                  c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
               }

               ItemMaker e = new ItemMaker(c.getPlayer().getId(), 0);
               c.getSession().writeAndFlush(e.encodeForLocal());
               c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
            }
         case 2:
         default:
            break;
         case 3:
            int etc = slea.readInt();
            if (c.getPlayer().haveItem(etc, 100, false, true)) {
               MapleInventoryManipulator.addById(c, getCreateCrystal(etc), (short)1, "Made by Maker " + etc + " on " + FileoutputUtil.CurrentReadable_Date());
               MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, etc, 100, false, false);
               ItemMaker e = new ItemMaker(c.getPlayer().getId(), 0);
               c.getSession().writeAndFlush(e.encodeForLocal());
               c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
            }
            break;
         case 4:
            int itemId = slea.readInt();
            slea.readInt();
            short slot = (short)slea.readInt();
            Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
            if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
               return;
            }

            MapleItemInformationProvider iix = MapleItemInformationProvider.getInstance();
            if (!iix.isDropRestricted(itemId) && !iix.isAccountShared(itemId)) {
               int[] toGivex = getCrystal(itemId, iix.getReqLevel(itemId));
               MapleInventoryManipulator.addById(
                  c, toGivex[0], (byte)toGivex[1], "Made by disassemble " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()
               );
               MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, slot, (short)1, false);
            }

            ItemMaker e = new ItemMaker(c.getPlayer().getId(), 0);
            c.getSession().writeAndFlush(e.encodeForLocal());
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e.encodeForRemote(), false);
      }
   }

   private static final int getCreateCrystal(int etc) {
      short level = MapleItemInformationProvider.getInstance().getItemMakeLevel(etc);
      int itemid;
      if (level >= 31 && level <= 50) {
         itemid = 4260000;
      } else if (level >= 51 && level <= 60) {
         itemid = 4260001;
      } else if (level >= 61 && level <= 70) {
         itemid = 4260002;
      } else if (level >= 71 && level <= 80) {
         itemid = 4260003;
      } else if (level >= 81 && level <= 90) {
         itemid = 4260004;
      } else if (level >= 91 && level <= 100) {
         itemid = 4260005;
      } else if (level >= 101 && level <= 110) {
         itemid = 4260006;
      } else if (level >= 111 && level <= 120) {
         itemid = 4260007;
      } else {
         if (level < 121) {
            throw new RuntimeException("Invalid Item Maker id");
         }

         itemid = 4260008;
      }

      return itemid;
   }

   private static final int[] getCrystal(int itemid, int level) {
      int[] all = new int[]{-1, 0};
      if (level >= 31 && level <= 50) {
         all[0] = 4260000;
      } else if (level >= 51 && level <= 60) {
         all[0] = 4260001;
      } else if (level >= 61 && level <= 70) {
         all[0] = 4260002;
      } else if (level >= 71 && level <= 80) {
         all[0] = 4260003;
      } else if (level >= 81 && level <= 90) {
         all[0] = 4260004;
      } else if (level >= 91 && level <= 100) {
         all[0] = 4260005;
      } else if (level >= 101 && level <= 110) {
         all[0] = 4260006;
      } else if (level >= 111 && level <= 120) {
         all[0] = 4260007;
      } else {
         if (level < 121 || level > 200) {
            throw new RuntimeException("Invalid Item Maker type" + level);
         }

         all[0] = 4260008;
      }

      if (!GameConstants.isWeapon(itemid) && !GameConstants.isOverall(itemid)) {
         all[1] = Randomizer.rand(3, 7);
      } else {
         all[1] = Randomizer.rand(5, 11);
      }

      return all;
   }

   private static final void addEnchantStats(Map<String, Integer> stats, Equip item) {
      Integer s = stats.get("PAD");
      if (s != null && s != 0) {
         item.setWatk((short)(item.getWatk() + s));
      }

      s = stats.get("MAD");
      if (s != null && s != 0) {
         item.setMatk((short)(item.getMatk() + s));
      }

      s = stats.get("ACC");
      if (s != null && s != 0) {
         item.setAcc((short)(item.getAcc() + s));
      }

      s = stats.get("EVA");
      if (s != null && s != 0) {
         item.setAvoid((short)(item.getAvoid() + s));
      }

      s = stats.get("Speed");
      if (s != null && s != 0) {
         item.setSpeed((short)(item.getSpeed() + s));
      }

      s = stats.get("Jump");
      if (s != null && s != 0) {
         item.setJump((short)(item.getJump() + s));
      }

      s = stats.get("MaxHP");
      if (s != null && s != 0) {
         item.setHp((short)(item.getHp() + s));
      }

      s = stats.get("MaxMP");
      if (s != null && s != 0) {
         item.setMp((short)(item.getMp() + s));
      }

      s = stats.get("STR");
      if (s != null && s != 0) {
         item.setStr((short)(item.getStr() + s));
      }

      s = stats.get("DEX");
      if (s != null && s != 0) {
         item.setDex((short)(item.getDex() + s));
      }

      s = stats.get("INT");
      if (s != null && s != 0) {
         item.setInt((short)(item.getInt() + s));
      }

      s = stats.get("LUK");
      if (s != null && s != 0) {
         item.setLuk((short)(item.getLuk() + s));
      }

      s = stats.get("randOption");
      if (s != null && s != 0) {
         int ma = item.getMatk();
         int wa = item.getWatk();
         if (wa > 0) {
            item.setWatk((short)(Randomizer.nextBoolean() ? wa + s : wa - s));
         }

         if (ma > 0) {
            item.setMatk((short)(Randomizer.nextBoolean() ? ma + s : ma - s));
         }
      }

      s = stats.get("randStat");
      if (s != null && s != 0) {
         int str = item.getStr();
         int dex = item.getDex();
         int luk = item.getLuk();
         int int_ = item.getInt();
         if (str > 0) {
            item.setStr((short)(Randomizer.nextBoolean() ? str + s : str - s));
         }

         if (dex > 0) {
            item.setDex((short)(Randomizer.nextBoolean() ? dex + s : dex - s));
         }

         if (int_ > 0) {
            item.setInt((short)(Randomizer.nextBoolean() ? int_ + s : int_ - s));
         }

         if (luk > 0) {
            item.setLuk((short)(Randomizer.nextBoolean() ? luk + s : luk - s));
         }
      }
   }

   private static final int getRandomGem(List<Pair<Integer, Integer>> rewards) {
      List<Integer> items = new ArrayList<>();

      for (Pair<Integer, Integer> p : rewards) {
         int itemid = (Integer)p.getLeft();

         for (int i = 0; i < p.getRight(); i++) {
            items.add(itemid);
         }
      }

      return items.get(Randomizer.nextInt(items.size()));
   }

   private static final int checkRequiredNRemove(MapleClient c, List<Pair<Integer, Integer>> recipe) {
      int itemid = 0;

      for (Pair<Integer, Integer> p : recipe) {
         if (!c.getPlayer().haveItem(p.getLeft(), p.getRight(), false, true)) {
            return 0;
         }
      }

      for (Pair<Integer, Integer> px : recipe) {
         itemid = px.getLeft();
         MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, px.getRight(), false, false);
      }

      return itemid;
   }

   private static final boolean hasSkill(MapleClient c, int reqlvl) {
      MapleCharacter var10000 = c.getPlayer();
      c.getPlayer().getStat();
      return var10000.getSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(1007, c.getPlayer().getJob()))) >= reqlvl;
   }
}
