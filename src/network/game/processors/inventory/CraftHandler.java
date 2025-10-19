package network.game.processors.inventory;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.child.SpecialEffect;
import objects.fields.gameobject.Extractor;
import objects.fields.gameobject.Reactor;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleTrait;
import objects.users.achievement.AchievementFactory;
import objects.users.enchant.ItemFlag;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.Triple;
import scripting.ReactorScriptManager;

public class CraftHandler {
   private static final Map<String, Integer> craftingEffects = new HashMap<>();

   public static void craftCooldown(PacketDecoder in, MapleClient c) {
      int craftID = in.readInt();
      SkillFactory.CraftingEntry ce = SkillFactory.getCraft(craftID);
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMapId() == 910001000 && ce != null && chr.getFatigue() < 200) {
         PacketEncoder o = new PacketEncoder();
         o.writeShort(SendPacketOpcode.CRAFT_COOLDOWN.getValue());
         o.writeInt(craftID);
         o.writeInt((int)chr.getCooldownLimit(craftID));
         c.getSession().writeAndFlush(o.getPacket());
      }
   }

   public static void useRecipe(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.isAlive() && chr.getMap() != null) {
         slea.readInt();
         short slot = slea.readShort();
         int itemId = slea.readInt();
         Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
         if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && itemId / 10000 == 251) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
               MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void makeExtractor(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.isAlive() && chr.getMap() != null) {
         int itemId = slea.readInt();
         if (itemId == -1) {
            chr.removeExtractor();
         } else {
            int fee = slea.readInt();
            Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
            if (toUse != null && toUse.getQuantity() >= 1 && itemId / 10000 == 304 && fee > 0 && chr.getExtractor() == null && chr.getMap().isTown()) {
               chr.setExtractor(new Extractor(chr, itemId, fee, chr.getFH()));
               chr.getMap().spawnExtractor(chr.getExtractor());
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void useBag(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.isAlive() && chr.getMap() != null) {
         slea.readInt();
         short slot = slea.readShort();
         int itemId = slea.readInt();
         byte inv = slea.readByte();
         Item toUse = chr.getInventory(MapleInventoryType.getByType(inv)).getItem(slot);
         if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            boolean firstTime = !chr.getExtendedSlots(inv).contains(itemId);
            if (firstTime) {
               chr.getExtendedSlots(inv).add(itemId);
               chr.changedExtended();
               int flag = toUse.getFlag();
               flag |= ItemFlag.BINDED.getValue();
               flag |= ItemFlag.POSSIBLE_TRADING.getValue();
               toUse.setFlag(flag);
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateSpecialItemUse(toUse, inv, true, chr, false, (byte)0));
            }

            PacketEncoder o = new PacketEncoder();
            o.writeShort(SendPacketOpcode.OPEN_BAG.getValue());
            o.writeInt(chr.getExtendedSlots(inv).indexOf(itemId));
            o.writeInt(itemId);
            o.write(firstTime ? 1 : 0);
            o.write(false);
            c.getSession().writeAndFlush(o.getPacket());
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void startHarvest(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      Reactor reactor = c.getPlayer().getMap().getReactorByOid(slea.readInt());
      if (reactor != null
         && reactor.isAlive()
         && reactor.getReactorId() <= 200013
         && !(reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 10000.0)
         && c.getPlayer().getFatigue() < 200) {
         MapleQuestStatus marr = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(122501));
         if (marr.getCustomData() == null) {
            marr.setCustomData("0");
         }

         long lastTime = Long.parseLong(marr.getCustomData());
         if (lastTime + 5000L > System.currentTimeMillis()) {
            c.getPlayer().dropMessage(5, "아직 채집을 할 수 없습니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            marr.setCustomData(String.valueOf(System.currentTimeMillis()));
            c.getPlayer().getMap().broadcastMessage(chr, CField.showHarvesting(chr.getId(), 0), false);
            c.getSession().writeAndFlush(CField.harvestMessage(reactor.getObjectId(), 15));
         }
      } else {
         c.getPlayer().dropMessage(1, "아직 채집을 할 수 없습니다.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static void stopHarvest(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      Reactor reactor = c.getPlayer().getMap().getReactorByOid(slea.readInt());
      if (reactor != null
         && reactor.isAlive()
         && reactor.getReactorId() <= 200013
         && !(reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 40000.0)
         && reactor.getState() >= 3
         && c.getPlayer().getFatigue() < 200) {
         c.getPlayer().getMap().destroyReactor(reactor.getObjectId());
         ReactorScriptManager.getInstance().act(c, reactor);
      }
   }

   public static void craftEffect(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr.getMapId() == 910001000 || chr.getMap().getExtractorSize() > 0) {
         String effect = slea.readMapleAsciiString();
         Integer profession = craftingEffects.get(effect);
         if (profession != null && (c.getPlayer().getProfessionLevel(profession) > 0 || profession == 92040000 && chr.getMap().getExtractorSize() > 0)) {
            int time = slea.readInt();
            if (time > 6000 || time < 3000) {
               time = 4000;
            }

            SpecialEffect eff = new SpecialEffect(chr.getId(), true, time, effect.endsWith("Extract") ? 1 : 0, 0, effect);
            chr.send(eff.encodeForLocal());
            chr.getMap().broadcastMessage(chr, eff.encodeForRemote(), false);
         }
      }
   }

   public static void craftMake(PacketDecoder slea, MapleCharacter chr) {
      if (chr.getMapId() == 910001000 || chr.getMap().getExtractorSize() > 0) {
         int something = slea.readInt();
         int time = slea.readInt();
         if (time > 6000 || time < 3000) {
            time = 4000;
         }

         PacketEncoder o = new PacketEncoder();
         o.writeShort(SendPacketOpcode.CRAFT_EFFECT.getValue());
         o.writeInt(chr.getId());
         o.writeInt(something);
         o.writeInt(time);
         chr.getMap().broadcastMessage(o.getPacket());
      }
   }

   public static void craftComplete(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      int type = slea.readInt();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      switch (type) {
         case 0:
            int craftID = slea.readInt();
            SkillFactory.CraftingEntry cex = SkillFactory.getCraft(craftID);
            if (chr.getMapId() != 910001000 && (craftID != 92049000 || chr.getMap().getExtractorSize() <= 0) || cex == null || chr.getFatigue() >= 200) {
               return;
            }

            int theLevl = c.getPlayer().getProfessionLevel(craftID / 10000 * 10000);
            if (theLevl <= 0) {
               return;
            }

            int toGet = 0;
            int expGain = 0;
            int fatigue = 0;
            short quantity = 1;
            CraftHandler.CraftRanking cr = CraftHandler.CraftRanking.GOOD;
            if (cex.needOpenItem && chr.getSkillLevel(craftID) <= 0) {
               return;
            }

            for (Entry<Integer, Integer> e : cex.reqItems.entrySet()) {
               if (!chr.haveItem(e.getKey(), e.getValue())) {
                  return;
               }
            }

            for (Triple<Integer, Integer, Integer> ix : cex.targetItems) {
               if (!MapleInventoryManipulator.checkSpace(c, ix.left, ix.mid, "")) {
                  return;
               }
            }

            for (Entry<Integer, Integer> ex : cex.reqItems.entrySet()) {
               MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(ex.getKey()), ex.getKey(), ex.getValue(), false, false);
            }

            if (Randomizer.nextInt(100) >= 100 - (cex.reqSkillLevel - theLevl) * 20 && craftID / 10000 > 9201) {
               quantity = 0;
               cr = CraftHandler.CraftRanking.SOSO;
            } else {
               Map<Skill, SkillEntry> sa = new HashMap<>();

               boolean passed;
               do {
                  passed = false;

                  for (Triple<Integer, Integer, Integer> ixx : cex.targetItems) {
                     if (Randomizer.nextInt(100) < ixx.right) {
                        toGet = ixx.left;
                        quantity = ixx.mid.shortValue();
                        Item receive;
                        if (GameConstants.getInventoryType(toGet) == MapleInventoryType.EQUIP) {
                           Equip first = (Equip)ii.getEquipById(toGet);
                           if (Randomizer.nextInt(100) < theLevl * 2) {
                              ii.randomizeStats(first);
                              cr = CraftHandler.CraftRanking.COOL;
                           }

                           if (Randomizer.nextInt(100) < theLevl * (first.getUpgradeSlots() > 0 ? 2 : 1)) {
                              first.resetPotential();
                              cr = CraftHandler.CraftRanking.COOL;
                           }

                           receive = first;
                           first.setFlag((short)ItemFlag.CRAFTED.getValue());
                        } else {
                           receive = new Item(toGet, (short)0, quantity, (short)ItemFlag.CRAFTED_USE.getValue());
                        }

                        if (cex.period > 0) {
                           receive.setExpiration(System.currentTimeMillis() + cex.period * 60000L);
                        }

                        receive.setOwner(chr.getName());
                        receive.setGMLog("Crafted from " + craftID + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.addFromDrop(c, receive, true, false, false);
                        if (cex.needOpenItem) {
                           byte mLevel = chr.getMasterLevel(craftID);
                           if (mLevel == 1) {
                              sa.put(cex, new SkillEntry(0, (byte)0, SkillFactory.getDefaultSExpiry(cex)));
                           } else if (mLevel > 1) {
                              sa.put(cex, new SkillEntry(Integer.MAX_VALUE, (byte)(chr.getMasterLevel(craftID) - 1), SkillFactory.getDefaultSExpiry(cex)));
                           }
                        }

                        fatigue = cex.incFatigability;
                        expGain = cex.incSkillProficiency == 0 ? fatigue * 20 - (cex.reqSkillLevel - theLevl) * 2 : cex.incSkillProficiency;
                        chr.getTrait(MapleTrait.MapleTraitType.craft).addExp(cr.craft, chr);
                        passed = true;
                        break;
                     }
                  }
               } while (!passed);

               cr = CraftHandler.CraftRanking.GOOD;
               chr.changeSkillsLevel(sa);
            }

            if (expGain > 0 && theLevl < 10) {
               expGain *= chr.getClient().getChannelServer().getTraitRate();
               if (Randomizer.nextInt(100) < chr.getTrait(MapleTrait.MapleTraitType.craft).getLevel() / 5) {
                  expGain *= 2;
               }

               String s = "연금술";
               switch (craftID / 10000) {
                  case 9200:
                     s = "약초채집";
                     break;
                  case 9201:
                     s = "채광";
                     break;
                  case 9202:
                     s = "장비제작";
                     break;
                  case 9203:
                     s = "장신구제작";
               }

               chr.dropMessage(5, s + "의 숙련도가 올랐습니다. (+" + expGain + ")");
               chr.addProfessionExp(craftID / 10000 * 10000, expGain);
            } else {
               expGain = 0;
            }

            MapleQuest.getInstance(2550).forceStart(c.getPlayer(), 9031000, "1");
            chr.setFatigue((byte)(chr.getFatigue() + fatigue));
            PacketEncoder o = new PacketEncoder();
            o.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
            o.writeInt(chr.getId());
            o.writeInt(type);
            o.writeInt(craftID);
            o.writeInt(cr.i);
            o.write(true);
            o.writeInt(toGet);
            o.writeInt((int)quantity);
            o.writeInt(expGain);
            chr.getMap().broadcastMessage(o.getPacket());
            int skillID = craftID / 10000 * 10000;
            AchievementFactory.checkMakingSkillFatigueInc(chr, skillID, fatigue);
            AchievementFactory.checkMakingskillMaking(chr, true, craftID);
            break;
         case 1:
            int extractorId = slea.readInt();
            int size = slea.readInt();
            craftID = 0;
            expGain = 0;
            fatigue = 0;
            quantity = 0;
            cr = CraftHandler.CraftRanking.GOOD;
            List<CraftHandler.CraftResult> list = new ArrayList<>();

            for (int ix = 0; ix < size; ix++) {
               craftID = slea.readInt();
               SkillFactory.CraftingEntry ce = SkillFactory.getCraft(craftID);
               if (ce == null) {
                  return;
               }

               int itemId = slea.readInt();
               long invId = slea.readLong();
               int tab = slea.readInt();
               int pos = slea.readInt();
               int reqLevel = ii.getReqLevel(itemId);
               Item item = chr.getInventory(MapleInventoryType.getByType((byte)tab)).getItem((short)pos);
               if (item == null || chr.getInventory(MapleInventoryType.ETC).isFull()) {
                  return;
               }

               if (extractorId >= 0) {
                  MapleCharacter extract = chr.getMap().getCharacterById(extractorId);
                  if (extract != null && extract.getExtractor() != null) {
                     Extractor extractor = extract.getExtractor();
                     if (extractor.owner != chr.getId()) {
                        if (chr.getMeso() < extractor.fee) {
                           return;
                        }

                        SecondaryStatEffect eff = ii.getItemEffect(extractor.itemId);
                        if (eff != null && eff.getUseLevel() < reqLevel) {
                           return;
                        }

                        chr.gainMeso(-extractor.fee, true);
                        MapleCharacter owner = chr.getMap().getCharacterById(extractor.owner);
                        if (owner != null && owner.getMeso() < Integer.MAX_VALUE - extractor.fee) {
                           owner.gainMeso(extractor.fee, false);
                        }
                     }
                  }
               }

               toGet = 4021016;
               quantity += (short)Randomizer.rand(3, !GameConstants.isWeapon(itemId) && !GameConstants.isOverall(itemId) ? 7 : 11);
               if (reqLevel <= 60) {
                  toGet = 4021013;
               } else if (reqLevel <= 90) {
                  toGet = 4021014;
               } else if (reqLevel <= 120) {
                  toGet = 4021015;
               }

               MapleInventoryManipulator.addById(c, toGet, quantity, FileoutputUtil.CurrentReadable_Date() + " 시간에 " + itemId + " 아이템을 분해하여 얻은 아이템.");
               MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType((byte)tab), (short)pos, (short)1, false);
               list.add(new CraftHandler.CraftResult(craftID, toGet, quantity));
            }

            MapleQuest.getInstance(2550).forceStart(c.getPlayer(), 9031000, "1");
            chr.setFatigue((byte)(chr.getFatigue() + fatigue));
            PacketEncoder o2 = new PacketEncoder();
            o2.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
            o2.writeInt(chr.getId());
            o2.writeInt(type);
            o2.writeInt(extractorId);
            o2.writeInt(cr.i);
            o2.write(true);
            o2.writeInt(list.size());

            for (CraftHandler.CraftResult result : list) {
               o2.writeInt(result.craftID);
               o2.writeInt(result.itemID);
               o2.writeInt(result.quantity);
               o2.writeInt(0);
               o2.writeInt(0);
            }

            o2.writeInt(expGain);
            chr.getMap().broadcastMessage(o2.getPacket());
            AchievementFactory.checkMakingSkillFatigueInc(chr, craftID / 10000 * 10000, fatigue);
            AchievementFactory.checkMakingskillDecomposition(chr);
            break;
         case 2:
            craftID = slea.readInt();
            theLevl = c.getPlayer().getProfessionLevel(craftID / 10000 * 10000);
            if (theLevl <= 0) {
               return;
            }

            quantity = 1;
            cr = CraftHandler.CraftRanking.GOOD;
            int itemId = slea.readInt();
            long invId1 = slea.readLong();
            long invId2 = slea.readLong();
            int reqLevel = ii.getReqLevel(itemId);
            Equip item1 = (Equip)chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId1, itemId);
            Equip item2 = (Equip)chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId2, itemId);

            for (byte i = (byte)0; i < chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit(); i++) {
               Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem(i);
               if (item != null && item.getItemId() == itemId && item != item1 && item != item2) {
                  if (item1 == null) {
                     item1 = (Equip)item;
                  } else if (item2 == null) {
                     item2 = (Equip)item;
                     if (item1 == null || item2 == null) {
                        return;
                     }

                     if (theLevl < (reqLevel > 130 ? 6 : (reqLevel - 30) / 20)) {
                        return;
                     }

                     i = (byte)17;
                     int potentialChance = theLevl * 2;
                     if (item1.getState() > 0 && item2.getState() > 0) {
                        potentialChance = 100;
                     } else if (item1.getState() > 0 || item2.getState() > 0) {
                        potentialChance *= 2;
                     }

                     if (item1.getState() == item2.getState() && item1.getState() > 17) {
                        i = item1.getState();
                     }

                     Equip newEquip = ii.fuse(
                        item1.getLevel() > 0 ? (Equip)ii.getEquipById(itemId) : item1, item2.getLevel() > 0 ? (Equip)ii.getEquipById(itemId) : item2
                     );
                     int newStat = ii.getTotalStat(newEquip);
                     if (newStat > ii.getTotalStat(item1) || newStat > ii.getTotalStat(item2)) {
                        cr = CraftHandler.CraftRanking.COOL;
                     } else if (newStat < ii.getTotalStat(item1) || newStat < ii.getTotalStat(item2)) {
                        cr = CraftHandler.CraftRanking.SOSO;
                     }

                     if (Randomizer.nextInt(100) < (newEquip.getUpgradeSlots() <= 0 && potentialChance < 100 ? potentialChance / 2 : potentialChance)) {
                        newEquip.resetPotential_Fuse(theLevl > 5, i);
                     }

                     newEquip.setFlag((short)ItemFlag.CRAFTED.getValue());
                     newEquip.setOwner(chr.getName());
                     toGet = newEquip.getItemId();
                     expGain = 60 - (theLevl - 1) * 2;
                     fatigue = 3;
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item1.getPosition(), (short)1, false);
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item2.getPosition(), (short)1, false);
                     MapleInventoryManipulator.addbyItem(c, newEquip);
                     MapleQuest.getInstance(2550).forceStart(c.getPlayer(), 9031000, "1");
                     chr.setFatigue((byte)(chr.getFatigue() + fatigue));
                     PacketEncoder o4 = new PacketEncoder();
                     o4.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
                     o4.writeInt(chr.getId());
                     o4.writeInt(type);
                     o4.writeInt(craftID);
                     o4.writeInt(cr.i);
                     o4.write(true);
                     o4.writeInt(toGet);
                     o4.writeInt((int)quantity);
                     o4.writeInt(expGain);
                     chr.getMap().broadcastMessage(o4.getPacket());
                     AchievementFactory.checkMakingSkillFatigueInc(chr, craftID / 10000 * 10000, fatigue);
                     AchievementFactory.checkMakingskillSynthesize(chr, true, item1, item2, newEquip);
                     break;
                  }
               }
            }
      }
   }

   static {
      craftingEffects.put("Effect/BasicEff.img/professions/herbalism", 92000000);
      craftingEffects.put("Effect/BasicEff.img/professions/mining", 92010000);
      craftingEffects.put("Effect/BasicEff.img/professions/herbalismExtract", 92000000);
      craftingEffects.put("Effect/BasicEff.img/professions/miningExtract", 92010000);
      craftingEffects.put("Effect/BasicEff.img/professions/equip_product", 92020000);
      craftingEffects.put("Effect/BasicEff.img/professions/acc_product", 92030000);
      craftingEffects.put("Effect/BasicEff.img/professions/alchemy", 92040000);
   }

   public static enum CraftRanking {
      SOSO(26, 30),
      GOOD(27, 40),
      COOL(28, 50);

      public int i;
      public int craft;

      private CraftRanking(int i, int craft) {
         this.i = i;
         this.craft = craft;
      }
   }

   public static class CraftResult {
      public int craftID;
      public int itemID;
      public int quantity;

      public CraftResult(int craftID, int itemID, int quantity) {
         this.craftID = craftID;
         this.itemID = itemID;
         this.quantity = quantity;
      }
   }
}
