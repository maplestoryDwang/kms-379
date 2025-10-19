package network.game.processors.inventory;

import constants.GameConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import database.loader.CharacterSaveFlag2;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import logging.LoggingManager;
import logging.entry.ConsumeLog;
import logging.entry.ConsumeLogType;
import logging.entry.EnchantLog;
import logging.entry.EnchantLogResult;
import logging.entry.EnchantLogType;
import logging.entry.PickupLog;
import network.RandomRewards;
import network.SendPacketOpcode;
import network.center.Center;
import network.decode.PacketDecoder;
import network.discordbot.DiscordBotHandler;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.game.processors.EnchantHandler;
import network.game.processors.HyperHandler;
import network.models.CField;
import network.models.CSPacket;
import network.models.CWvsContext;
import network.models.PacketHelper;
import network.models.PetPacket;
import network.models.PlayerShopPacket;
import objects.androids.Android;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.ExpEffect;
import objects.effect.child.PetLevelUp;
import objects.effect.child.RewardItem;
import objects.effect.child.SpecialEffect;
import objects.effect.child.WZEffectBased;
import objects.fields.Field;
import objects.fields.FieldLimitType;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SavedLocationType;
import objects.fields.child.arkarium.Field_Arkaium;
import objects.fields.child.fritto.Field_ReceivingTreasure;
import objects.fields.child.minigame.soccer.Field_MultiSoccer;
import objects.fields.child.muto.HungryMuto;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.events.MapleEvent;
import objects.fields.events.MapleEventType;
import objects.fields.gameobject.Drop;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.item.BlackRebirthFlame;
import objects.item.DamageSkinSaveData;
import objects.item.DamageSkinSaveInfo;
import objects.item.Equip;
import objects.item.IntensePowerCrystal;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MapleMount;
import objects.item.MaplePet;
import objects.item.StructRewardItem;
import objects.item.rewards.RoyalStyle;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.shop.HiredMerchant;
import objects.shop.IMaplePlayerShop;
import objects.shop.MapleShopFactory;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.MapleTrait;
import objects.users.PlayerStats;
import objects.users.achievement.AchievementFactory;
import objects.users.achievement.caching.mission.submission.checkvalue.ItemCheck;
import objects.users.enchant.BonusStat;
import objects.users.enchant.BonusStatPlaceType;
import objects.users.enchant.ExItemType;
import objects.users.enchant.GradeRandomOption;
import objects.users.enchant.InnerAbility;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemOptionInfo;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.skilloption.SkillOption;
import objects.users.enchant.skilloption.TempOptionEntry;
import objects.users.looks.zero.ZeroInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.users.skills.VCore;
import objects.users.skills.VCoreData;
import objects.users.skills.VSpecialCoreOption;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;
import security.anticheat.CheatingOffense;

public class InventoryHandler {
   public static final int OWL_ID = 2;

   public static final void ItemMove(PacketDecoder slea, MapleClient c) {
      c.getPlayer().setScrolledPosition((short)0);
      slea.readInt();
      MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
      short src = slea.readShort();
      short dst = slea.readShort();
      short quantity = slea.readShort();
      if (c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getId()) != null) {
         if ((src >= 0 || dst <= 0) && (src >= 0 || dst >= 0 || !GameConstants.isZero(c.getPlayer().getJob()))) {
            if (dst < 0) {
               MapleInventoryManipulator.equip(type, c, src, dst);
            } else if (dst == 0) {
               MapleInventoryManipulator.drop(c, type, src, quantity);
            } else {
               MapleInventoryManipulator.move(c, type, src, dst);
               if ((type == MapleInventoryType.EQUIP || type == MapleInventoryType.EQUIPPED)
                  && (c.getPlayer().getJuhunEquipPosition() == src || c.getPlayer().getJuhunEquipPosition() == dst)) {
                  short source = 0;
                  if (c.getPlayer().getJuhunEquipPosition() == src) {
                     source = src;
                  } else {
                     source = dst;
                  }

                  Equip item = null;
                  if (source > 0) {
                     item = (Equip)c.getPlayer().getInventory(type).getItem(source);
                  } else {
                     item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(source);
                  }

                  if (item != null) {
                     c.getSession().writeAndFlush(EnchantHandler.display_ScrollUpgrade(item, !DBConfig.isGanglim && ServerConstants.JuhunFever == 1));
                  }
               }
            }
         } else {
            MapleInventoryManipulator.unequip(type, c, src, dst);
         }
      }
   }

   public static void SwitchBag(PacketDecoder in, MapleClient c) {
      c.getPlayer().setScrolledPosition((short)0);
      in.skip(4);
      short src = (short)in.readInt();
      short dst = (short)in.readInt();
      byte invType = in.readByte();
      if (src >= 100 && dst >= 100) {
         MapleInventoryManipulator.move(c, MapleInventoryType.getByType(invType), src, dst);
      }
   }

   public static void MoveBag(PacketDecoder in, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr == null) {
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
      } else {
         chr.setScrolledPosition((short)0);
         in.readInt();
         int type = in.readInt();
         byte invType = in.readByte();
         MapleInventory pInv = chr.getInventory(MapleInventoryType.getByType(invType));
         if (pInv.getType() != MapleInventoryType.UNDEFINED && pInv.getNextFreeSlot() > 0) {
            switch (type) {
               case 0: {
                  short dst = (short)in.readInt();
                  short src = in.readShort();
                  MapleInventoryManipulator.move(c, pInv.getType(), src, dst);
                  break;
               }
               case 1: {
                  short src = (short)in.readInt();
                  short dst = in.readShort();
                  MapleInventoryManipulator.move(c, pInv.getType(), src, dst);
                  break;
               }
               case 2: {
                  short src = in.readShort();
                  Item item = pInv.getItem(src);
                  if (item != null) {
                     int itemBagType = GameConstants.getNeededBagTypeByInputItemID(item.getItemId(), invType);
                     MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                     int bagID = chr.extendedSlots.get(invType).stream().filter(ID -> ii.getBagType_(ID) == itemBagType).findFirst().orElse(-1);
                     if (bagID >= 0) {
                        int index = chr.extendedSlots.get(invType).indexOf(bagID);

                        for (int i = 1; i <= ii.getBagSlotCount(bagID); i++) {
                           short pos = (short)(10000 + (index + 1) * 100 + i);
                           if (pInv.getItem(pos) == null) {
                              MapleInventoryManipulator.move(c, pInv.getType(), src, pos);
                              return;
                           }
                        }

                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     } else {
                        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     }
                  } else {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  }
                  break;
               }
               case 3: {
                  short src = (short)in.readInt();
                  if (pInv.getNextFreeSlot() <= 0) {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  } else {
                     MapleInventoryManipulator.move(c, pInv.getType(), src, pInv.getNextFreeSlot());
                  }
               }
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
         }
      }
   }

   public static void ItemSort(PacketDecoder slea, MapleClient c) {
      slea.readInt();
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         chr.setScrolledPosition((short)0);
         MapleInventoryType pInvType = MapleInventoryType.getByType(slea.readByte());
         if (pInvType == MapleInventoryType.UNDEFINED) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
         } else {
            MapleInventory pInv = chr.getInventory(pInvType);
            List<Item> itemMap = new LinkedList<>();

            for (Item item : pInv.list()) {
               if (item.getType() == 1) {
                  Equip equip = (Equip)item;
                  if ((equip.getItemState() & ItemStateFlag.SORT_LOCK.getValue()) != 0) {
                     continue;
                  }
               } else if (ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.check(item.getFlag())) {
                  continue;
               }

               if (item.getPosition() <= 128) {
                  Item copy = item.copy();
                  itemMap.add(copy);
                  if (copy.getIntensePowerCrystal() != null) {
                     IntensePowerCrystal ipc = chr.getIntensePowerCrystals().get(item.getUniqueId());
                     if (ipc != null) {
                        ipc.setItemUniqueID(copy.getUniqueId());
                     }

                     chr.setSaveFlag2(chr.getSaveFlag2() | CharacterSaveFlag2.INTENSE_POWER_CRYSTAL.getFlag());
                  }
               }
            }

            itemMap = itemMap.stream().sorted(Comparator.comparingInt(Item::getItemId)).collect(Collectors.toList());

            for (Item itemStats : itemMap) {
               MapleInventoryManipulator.removeFromSlot(c, pInvType, itemStats.getPosition(), itemStats.getQuantity(), true, false, true);
            }

            for (Item item : itemMap) {
               MapleInventoryManipulator.addFromDrop(c, item, false, false, false);
               if (item.getPet() != null) {
                  int index = chr.getPetIndex(item.getPet());
                  if (index != -1) {
                     Item pet = chr.getInventory(MapleInventoryType.CASH).findByUniqueId(item.getUniqueId());
                     if (pet != null) {
                        chr.addPetBySlotId(pet.getPet(), index);
                        int questId = 101080 + index;

                        for (int skillIndex = 0; skillIndex < 2; skillIndex++) {
                           String questKey = String.valueOf(index * 10 + skillIndex);
                           int skillId = chr.getOneInfoQuestInteger(questId, questKey);
                           if (skillId != 0) {
                              chr.updateOneInfo(questId, questKey, String.valueOf(skillId));
                           }
                        }
                     }
                  }
               }
            }

            chr.updatePet();
            short source = (short)chr.getJuhunEquipPosition();
            if (source != 999) {
               Equip itemx;
               if (source > 0) {
                  itemx = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem(source);
               } else {
                  itemx = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(source);
               }

               if (itemx != null) {
                  c.getSession().writeAndFlush(EnchantHandler.display_ScrollUpgrade(itemx, !DBConfig.isGanglim && ServerConstants.JuhunFever == 1));
               }
            }

            c.getSession().writeAndFlush(CWvsContext.finishedSort(pInvType.getType()));
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
         }
      }
   }

   public static void itemLock(PacketDecoder in, MapleClient c) {
      in.readInt();
      MapleCharacter chr = c.getPlayer();
      MapleInventoryType pInvType = MapleInventoryType.getByType((byte)in.readInt());
      if (chr != null && pInvType != null && pInvType != MapleInventoryType.UNDEFINED) {
         short pos = in.readShort();
         boolean isLock = in.readByte() == 1;
         Item item = chr.getInventory(pInvType).getItem(pos);
         if (item == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
         } else {
            if (isLock) {
               if (item.getType() == 1) {
                  Equip equip = (Equip)item;
                  equip.setItemState(equip.getItemState() | ItemStateFlag.LOCK.getValue());
               } else {
                  item.setFlag(item.getFlag() | ItemFlag.LOCK.getValue());
               }
            } else if (item.getType() == 1) {
               Equip equip = (Equip)item;
               equip.setItemState(equip.getItemState() - ItemStateFlag.LOCK.getValue());
            } else {
               item.setFlag(item.getFlag() - ItemFlag.LOCK.getValue());
            }

            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventoryItem(pInvType, item, true, chr));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static void itemSortLock(PacketDecoder in, MapleClient c) {
      in.readInt();
      MapleCharacter chr = c.getPlayer();
      MapleInventoryType pInvType = MapleInventoryType.getByType((byte)in.readInt());
      if (chr != null && pInvType != null && pInvType != MapleInventoryType.UNDEFINED) {
         short pos = (short)in.readInt();
         boolean isLock = in.readByte() == 1;
         Item item = chr.getInventory(pInvType).getItem(pos);
         if (item == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
         } else {
            if (isLock) {
               if (item.getType() == 1) {
                  Equip equip = (Equip)item;
                  equip.setItemState(equip.getItemState() | ItemStateFlag.SORT_LOCK.getValue());
               } else {
                  item.setFlag(item.getFlag() | ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.getValue());
               }
            } else if (item.getType() == 1) {
               Equip equip = (Equip)item;
               equip.setItemState(equip.getItemState() - ItemStateFlag.SORT_LOCK.getValue());
            } else {
               item.setFlag(item.getFlag() - ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.getValue());
            }

            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateInventoryItem(pInvType, item, true, chr));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static void ItemGather(PacketDecoder slea, MapleClient c) {
      slea.readInt();
      c.getPlayer().setScrolledPosition((short)0);
      MapleInventoryType pInvType = MapleInventoryType.getByType(slea.readByte());
      if (pInvType == MapleInventoryType.UNDEFINED) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         MapleInventory pInv = c.getPlayer().getInventory(pInvType);
         boolean sorted = false;

         while (!sorted) {
            int freeSlot = pInv.getNextFreeSlot();
            if (freeSlot != -1) {
               int itemSlot = -1;
               int i = freeSlot + 1;

               label67: {
                  while (true) {
                     if (i > pInv.getSlotLimit()) {
                        break label67;
                     }

                     Item item = pInv.getItem((short)i);
                     if (item != null) {
                        if (item.getType() == 1) {
                           Equip equip = (Equip)item;
                           if ((equip.getItemState() & ItemStateFlag.SORT_LOCK.getValue()) == 0) {
                              break;
                           }
                        } else if (!ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.check(item.getFlag())) {
                           break;
                        }
                     }

                     i++;
                  }

                  itemSlot = i;
               }

               if (itemSlot > 0) {
                  MapleInventoryManipulator.move(c, pInvType, (short)itemSlot, (short)freeSlot);
               } else {
                  sorted = true;
               }
            } else {
               sorted = true;
            }
         }

         short source = (short)c.getPlayer().getJuhunEquipPosition();
         if (c.getPlayer().getJuhunEquipPosition() != 999) {
            Equip item = null;
            if (c.getPlayer().getJuhunEquipPosition() > 0) {
               item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(source);
            } else {
               item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(source);
            }

            if (item != null) {
               c.getSession().writeAndFlush(EnchantHandler.display_ScrollUpgrade(item, !DBConfig.isGanglim && ServerConstants.JuhunFever == 1));
            }
         }

         c.getSession().writeAndFlush(CWvsContext.finishedGather(pInvType.getType()));
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final boolean UseRewardItem(short slot, int itemId, MapleClient c, MapleCharacter chr) {
      Item toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
         if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1
            && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1
            && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1
            && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);
            if (rewards != null && rewards.getLeft() > 0) {
               while (true) {
                  for (StructRewardItem reward : rewards.getRight()) {
                     if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) {
                        if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                           Item item = ii.getEquipById(reward.itemid);
                           if (reward.period > 0L) {
                              item.setExpiration(System.currentTimeMillis() + reward.period * 60L * 60L * 10L);
                           }

                           item.setGMLog("Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                           MapleInventoryManipulator.addbyItem(c, item);
                        } else {
                           MapleInventoryManipulator.addById(
                              c, reward.itemid, reward.quantity, "Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()
                           );
                        }

                        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                        RewardItem e = new RewardItem(c.getPlayer().getId(), reward.itemid, reward.effect);
                        c.getSession().writeAndFlush(e.encodeForLocal());
                        chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
                        return true;
                     }
                  }
               }
            }

            if (itemId == 2028272) {
               int rewardx = RandomRewards.getTheSeedReward();
               if (rewardx == 0) {
                  c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(9000155, (byte)0, "아쉽지만, 꽝이 나왔습니다. 다음 기회에 다시 이용해주세요!", "00 00", (byte)0));
               } else if (rewardx == 1) {
                  chr.gainMeso(10000000L, true);
                  c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(9000155, (byte)0, "1천만메소를 획득하셨습니다!", "00 00", (byte)0));
               } else {
                  int max_quantity = 1;
                  switch (rewardx) {
                     case 4001208:
                     case 4001209:
                     case 4001210:
                     case 4001211:
                     case 4001547:
                     case 4001548:
                     case 4001549:
                     case 4001550:
                     case 4001551:
                        max_quantity = 1;
                        break;
                     case 4310014:
                        max_quantity = 10;
                        break;
                     case 4310016:
                        max_quantity = 10;
                        break;
                     case 4310034:
                        max_quantity = 10;
                  }

                  c.getSession()
                     .writeAndFlush(
                        CField.NPCPacket.getNPCTalk(9000155, (byte)0, "축하드립니다!!\r\n돌림판에서 [#b#i" + rewardx + "##z" + rewardx + "#](이)가 나왔습니다.", "00 00", (byte)0)
                     );
                  c.getPlayer().gainItem(rewardx, max_quantity);
                  c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(rewardx, (byte)max_quantity, true));
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               }
            } else {
               chr.dropMessage(6, "아이템 보상 정보를 찾을 수 없습니다.");
            }
         } else {
            chr.dropMessage(6, "Insufficient inventory slot.");
         }
      }

      return false;
   }

   public static final void UseItem(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.isAlive() && chr.getMapId() != 749040100 && chr.getMap() != null && !chr.hasDisease(SecondaryStatFlag.StopPortion) && !chr.inPVP()
         )
       {
         long time = System.currentTimeMillis();
         if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "이상한 기운이 감돌아 아이템을 당분간 사용할 수 없습니다");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            slea.skip(4);
            short slot = slea.readShort();
            int itemId = slea.readInt();
            Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
            int qty;
            if (slea.available() >= 4L) {
               qty = slea.readInt();
            } else {
               qty = 1;
            }

            if (qty < 0) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            } else {
               if (FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) && chr.getMap().getId() != 921170050 && chr.getMap().getId() != 921170100) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               } else if (qty > 1) {
                  for (int i = 0; i < qty; i++) {
                     if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                        if (toUse.getItemId() != 2000054) {
                           MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        }

                        if (chr.getMap().getConsumeItemCoolTime() > 0) {
                           chr.setNextConsume(time + chr.getMap().getConsumeItemCoolTime() * 1000);
                        }
                     }
                  }
               } else if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                  if (toUse.getItemId() != 2000054) {
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                  }

                  if (chr.getMap().getConsumeItemCoolTime() > 0) {
                     chr.setNextConsume(time + chr.getMap().getConsumeItemCoolTime() * 1000);
                  }
               }
            }
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void UseOccultAdditionalCube(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      MapleCharacter player = c.getPlayer();
      Item toUse = player.getInventory(MapleInventoryType.USE).getItem(slea.readShort());
      slea.skip(4);
      short dst = slea.readShort();
      Item target = null;
      if (dst < 0) {
         target = player.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
      } else {
         target = player.getInventory(MapleInventoryType.EQUIP).getItem(dst);
      }

      if (toUse != null && target != null) {
         Equip eq = (Equip)target;
         GradeRandomOption option = GradeRandomOption.OccultAdditional;
         long cost = GameConstants.getItemReleaseCost(eq.getItemId());
         if (cost > player.getMeso()) {
            c.getPlayer().dropMessage(1, "메소가 부족하여 사용할 수 없습니다.");
         } else {
            c.getPlayer().gainMeso(-cost, true);
            int beforeGrade = eq.getState();
            setPotential(option, true, eq);
            int afterGrade = eq.getState();
            Equip zeroEquip = null;
            if (GameConstants.isZeroWeapon(eq.getItemId())) {
               zeroEquip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(dst == -11 ? -10 : -11));
               zeroEquip.setPotential4(eq.getPotential4());
               zeroEquip.setPotential5(eq.getPotential5());
               zeroEquip.setPotential6(eq.getPotential6());
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, toUse.getPosition(), (short)1, false);
            c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId(), eq.getItemId()));
            int remainCount = c.getPlayer().getItemQuantity(toUse.getItemId(), false);
            c.getSession()
               .writeAndFlush(CField.getInGameAdditionalCubeResult(c.getPlayer(), target, toUse.getItemId(), beforeGrade != afterGrade, remainCount));
            c.getPlayer().forceReAddItem(target, MapleInventoryType.EQUIP);
            if (zeroEquip != null) {
               c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId(), zeroEquip.getItemId()));
               c.getPlayer().forceReAddItem(target, MapleInventoryType.EQUIPPED);
               c.getPlayer().forceReAddItem(zeroEquip, MapleInventoryType.EQUIPPED);
               c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
            } else {
               c.getPlayer().forceReAddItem(target, MapleInventoryType.EQUIP);
            }
         }
      }
   }

   public static final void UseInGameCube(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      MapleCharacter player = c.getPlayer();
      Item toUse = player.getInventory(MapleInventoryType.USE).getItem(slea.readShort());
      short dst = slea.readShort();
      Item target = null;
      if (dst < 0) {
         target = player.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
      } else {
         target = player.getInventory(MapleInventoryType.EQUIP).getItem(dst);
      }

      if (toUse != null && target != null) {
         long time = System.currentTimeMillis();
         if (time - c.getPlayer().lastItemUsedTime <= 300L) {
            c.getPlayer().dropMessage(5, "지금은 사용자가 많아 아이템을 사용할 수 없습니다. 잠시 후 다시 시도해 주세요.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            c.getPlayer().lastItemUsedTime = time;
            Equip eq = (Equip)target;
            GradeRandomOption option = GradeRandomOption.Master;
            if (toUse.getItemId() >= 2711000 && toUse.getItemId() <= 2711002) {
               option = GradeRandomOption.Occult;
            }

            if (toUse.getItemId() == 2711004
               || toUse.getItemId() == 2711006
               || toUse.getItemId() == 2711013
               || toUse.getItemId() == 2711017
               || toUse.getItemId() == 2711020
               || toUse.getItemId() == 2711021
               || toUse.getItemId() == 2711022) {
               option = GradeRandomOption.Meister;
            }

            long cost = GameConstants.getItemReleaseCost(eq.getItemId());
            if (cost > player.getMeso()) {
               c.getPlayer().dropMessage(1, "메소가 부족하여 사용할 수 없습니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            } else {
               c.getPlayer().gainMeso(-cost, true);
               int beforeGrade = eq.getState();
               setPotential(option, true, eq);
               int afterGrade = eq.getState();
               Equip zeroEquip = null;
               if (GameConstants.isZeroWeapon(eq.getItemId())) {
                  zeroEquip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(dst == -11 ? -10 : -11));
                  zeroEquip.setPotential1(eq.getPotential1());
                  zeroEquip.setPotential2(eq.getPotential2());
                  zeroEquip.setPotential3(eq.getPotential3());
               }

               StringBuilder sb = new StringBuilder("무료 큐브 사용 결과 (");
               sb.append("계정 : ");
               sb.append(c.getAccountName());
               sb.append(", 캐릭터 : ");
               sb.append(c.getPlayer().getName());
               sb.append(" (정보 : ");
               sb.append(eq.toString());
               sb.append("))");
               LoggingManager.putLog(
                  new EnchantLog(
                     c.getPlayer(),
                     toUse.getItemId(),
                     eq.getItemId(),
                     eq.getSerialNumberEquip(),
                     EnchantLogType.InGameCube.getType(),
                     EnchantLogResult.Success.getType(),
                     sb
                  )
               );
               MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, toUse.getPosition(), (short)1, false);
               c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId(), eq.getItemId()));
               int remainCount = c.getPlayer().getItemQuantity(toUse.getItemId(), false);
               c.getSession().writeAndFlush(CField.getInGameCubeResult(c.getPlayer(), target, toUse.getItemId(), beforeGrade != afterGrade, remainCount));
               if (zeroEquip != null) {
                  c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId(), zeroEquip.getItemId()));
                  c.getPlayer().forceReAddItem(target, MapleInventoryType.EQUIPPED);
                  c.getPlayer().forceReAddItem(zeroEquip, MapleInventoryType.EQUIPPED);
                  c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
               } else {
                  c.getPlayer().forceReAddItem(target, MapleInventoryType.EQUIP);
               }
            }
         }
      }
   }

   public static final void UseReturnScroll(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr.isAlive() && chr.getMapId() != 749040100 && !chr.isInBlockedMap() && !chr.inPVP()) {
         slea.readInt();
         short slot = slea.readShort();
         int itemId = slea.readInt();
         Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
         if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
               if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
               } else {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void UseMagnify(PacketDecoder slea, MapleClient c) {
      try {
         slea.skip(4);
         boolean useGlass = false;
         boolean isEquipped = false;
         short useSlot = slea.readShort();
         short equSlot = slea.readShort();
         Equip equip = null;
         Equip zeroEquip = null;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         if (equSlot < 0) {
            equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(equSlot);
            isEquipped = true;
         } else {
            equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equSlot);
         }

         if (GameConstants.isZeroWeapon(equip.getItemId())) {
            zeroEquip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
         }

         Item glass = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(useSlot);
         if (useSlot != 20000) {
            if (glass == null || equip == null) {
               c.getPlayer().dropMessage(1, "GLASS NULL!");
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
               return;
            }

            useGlass = true;
         } else {
            long price = GameConstants.getItemReleaseCost(equip.getItemId());
            c.getPlayer().gainMeso(-price, false);
         }

         boolean additional = equip.getState() >= 49 && equip.getPotential4() > 0;
         if (additional) {
            equip.setState((byte)(equip.getState() - 32));
            if (zeroEquip != null) {
               zeroEquip.setState(equip.getState());
            }
         } else {
            equip.setState((byte)(equip.getState() + 16));
            if (zeroEquip != null) {
               zeroEquip.setState(equip.getState());
            }
         }

         if (DBConfig.isGanglim) {
            setFirstPotential(additional ? GradeRandomOption.Additional : GradeRandomOption.Normal, false, equip, 3);
         } else {
            setFirstPotential(additional ? GradeRandomOption.Additional : GradeRandomOption.Normal, false, equip, Randomizer.nextInt(100) < 20 ? 3 : 2);
         }

         if (zeroEquip != null) {
            zeroEquip.setPotential1(equip.getPotential1());
            zeroEquip.setPotential2(equip.getPotential2());
            zeroEquip.setPotential3(equip.getPotential3());
            zeroEquip.setPotential4(equip.getPotential4());
            zeroEquip.setPotential5(equip.getPotential5());
            zeroEquip.setPotential6(equip.getPotential6());
         }

         if (useGlass) {
            MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
            useInventory.removeItem(useSlot, (short)1, false);
         }

         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(glass, equip, false, true, isEquipped));
         if (zeroEquip != null) {
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(glass, zeroEquip, false, true, isEquipped));
         }

         c.getPlayer().getTrait(MapleTrait.MapleTraitType.insight).addExp(10, c.getPlayer());
         c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), equSlot));
         if (isEquipped) {
            c.getPlayer().forceReAddItem_NoUpdate(equip, MapleInventoryType.EQUIPPED);
            if (zeroEquip != null) {
               c.getPlayer().forceReAddItem_NoUpdate(zeroEquip, MapleInventoryType.EQUIPPED);
            }

            c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
         } else {
            c.getPlayer().forceReAddItem_NoUpdate(equip, MapleInventoryType.EQUIP);
         }

         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } catch (Exception var12) {
         System.out.println("인벤토리 핸들러 오류 발생");
         var12.printStackTrace();
      }
   }

   public static void setPotential(GradeRandomOption rOption, boolean allowGradeUp, Equip equip) {
      boolean additional = rOption == GradeRandomOption.Additional
         || rOption == GradeRandomOption.OccultAdditional
         || rOption == GradeRandomOption.AmazingAdditional;
      int pa = additional ? equip.getAdditionalPA() : equip.getPA();
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      if (rOption == GradeRandomOption.Amazing && DBConfig.isGanglim) {
         int option = ItemOptionInfo.getItemOption(equip.getItemId(), grade, equip.getPotentials(false, 0), rOption);

         for (int i = 0; i < 3; i++) {
            equip.setPotentialOption(i, option);
         }
      } else if (rOption == GradeRandomOption.AmazingAdditional && DBConfig.isGanglim) {
         int optionGrade = ItemOptionInfo.getItemGrade(grade, 0, rOption);
         int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(true, 0), rOption);

         for (int i = 3; i < 6; i++) {
            equip.setPotentialOption(i, option);
         }
      } else {
         for (int i = 0; i < pa; i++) {
            int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (i > 0 ? -1 : 0) : 0), i, rOption);
            if (!allowGradeUp) {
               optionGrade = Math.min(grade, optionGrade);
            } else if (optionGrade > grade) {
               if (!additional) {
                  equip.setItemGrade(optionGrade);
               }

               grade = (byte)optionGrade;
            }

            int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, i), rOption);
            equip.setPotentialOption(i + (additional ? 3 : 0), option);
         }
      }
   }

   public static int setPotentialReturnInt(GradeRandomOption rOption, boolean allowGradeUp, Equip equip) {
      boolean additional = rOption == GradeRandomOption.Additional || rOption == GradeRandomOption.OccultAdditional;
      int pa = additional ? equip.getAdditionalPA() : equip.getPA();
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      int optionID = 0;

      for (int i = 0; i < pa; i++) {
         int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (i > 0 ? -1 : 0) : 0), i, rOption);
         if (!allowGradeUp) {
            optionGrade = Math.min(grade, optionGrade);
         } else if (optionGrade > grade) {
            if (!additional) {
               equip.setItemGrade(optionGrade);
            }

            grade = (byte)optionGrade;
         }

         int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, i), rOption);
         if (i == 0) {
            optionID = option;
         }

         equip.setPotentialOption(i + (additional ? 3 : 0), option);
      }

      return optionID;
   }

   public static void setFirstPotential(GradeRandomOption rOption, boolean allowGradeUp, Equip equip, int pa) {
      boolean additional = rOption == GradeRandomOption.Additional || rOption == GradeRandomOption.OccultAdditional;
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      if (!additional) {
         equip.setLines((byte)pa);
      } else if (grade == 0) {
         grade = 1;
      }

      for (int i = 0; i < pa; i++) {
         int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (i > 0 ? -1 : 0) : 0), i, rOption);
         if (!allowGradeUp) {
            optionGrade = Math.min(grade, optionGrade);
         } else if (optionGrade > grade) {
            if (!additional) {
               equip.setItemGrade(optionGrade);
            }

            grade = (byte)optionGrade;
         }

         int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, i), rOption);
         equip.setPotentialOption(i + (additional ? 3 : 0), option);
      }
   }

   public static void setFirstPotentialEpic(GradeRandomOption rOption, boolean allowGradeUp, Equip equip, int pa) {
      boolean additional = rOption == GradeRandomOption.Additional || rOption == GradeRandomOption.OccultAdditional;
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      if (!additional) {
         equip.setLines((byte)pa);
      } else {
         grade = (byte)Math.max(2, grade);
      }

      for (int i = 0; i < pa; i++) {
         int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (i > 0 ? -1 : 0) : 0), i, rOption);
         if (!allowGradeUp) {
            optionGrade = Math.min(grade, optionGrade);
         } else if (optionGrade > grade) {
            if (!additional) {
               equip.setItemGrade(optionGrade);
            }

            grade = (byte)optionGrade;
         }

         int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, i), rOption);
         equip.setPotentialOption(i + (additional ? 3 : 0), option);
      }
   }

   public static void setFirstPotentialUnique(GradeRandomOption rOption, boolean allowGradeUp, Equip equip, int pa) {
      boolean additional = rOption == GradeRandomOption.Additional || rOption == GradeRandomOption.OccultAdditional;
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      if (!additional) {
         equip.setLines((byte)pa);
      } else {
         grade = (byte)Math.max(3, grade);
      }

      for (int i = 0; i < pa; i++) {
         int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (i > 0 ? -1 : 0) : 0), i, rOption);
         if (!allowGradeUp) {
            optionGrade = Math.min(grade, optionGrade);
         } else if (optionGrade > grade) {
            if (!additional) {
               equip.setItemGrade(optionGrade);
            }

            grade = (byte)optionGrade;
         }

         int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, i), rOption);
         equip.setPotentialOption(i + (additional ? 3 : 0), option);
      }
   }

   public static void setFirstPotentialLegendary(GradeRandomOption rOption, boolean allowGradeUp, Equip equip, int pa) {
      boolean additional = rOption == GradeRandomOption.Additional || rOption == GradeRandomOption.OccultAdditional;
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      if (!additional) {
         equip.setLines((byte)pa);
      } else {
         grade = (byte)Math.max(4, grade);
      }

      for (int i = 0; i < pa; i++) {
         int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (i > 0 ? -1 : 0) : 0), i, rOption);
         if (!allowGradeUp) {
            optionGrade = Math.min(grade, optionGrade);
         } else if (optionGrade > grade) {
            if (!additional) {
               equip.setItemGrade(optionGrade);
            }

            grade = (byte)optionGrade;
         }

         int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, i), rOption);
         equip.setPotentialOption(i + (additional ? 3 : 0), option);
      }
   }

   public static void setPotential(GradeRandomOption rOption, boolean allowGradeUp, Equip equip, int idx) {
      boolean additional = rOption == GradeRandomOption.Additional || rOption == GradeRandomOption.OccultAdditional;
      byte grade = additional ? equip.getAdditionalGrade() : equip.getItemGrade();
      int optionGrade = ItemOptionInfo.getItemGrade(grade + (grade != 0 ? (idx > 0 ? -1 : 0) : 0), idx, rOption);
      if (!allowGradeUp) {
         optionGrade = Math.min(grade, optionGrade);
      } else if (optionGrade > grade) {
         if (!additional) {
            equip.setItemGrade(optionGrade);
         }

         grade = (byte)optionGrade;
      }

      int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, equip.getPotentials(additional, idx), rOption);
      equip.setPotentialOption(idx + (additional ? 3 : 0), option);
   }

   public static void UseStamp(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      short slot = slea.readShort();
      short dst = slea.readShort();
      boolean sucstamp = false;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Equip toStamp;
      if (dst < 0) {
         toStamp = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
      } else {
         toStamp = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
      }

      Equip zeroEquip = null;
      if (GameConstants.isZeroWeapon(toStamp.getItemId())) {
         zeroEquip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(dst == -11 ? -10 : -11));
      }

      MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
      Item stamp = useInventory.getItem(slot);
      if (Randomizer.isSuccess(ii.getSuccess(toStamp.getItemId(), c.getPlayer(), toStamp))) {
         toStamp.setLines((byte)3);
         setPotential(GradeRandomOption.Normal, false, toStamp, 2);
         if (zeroEquip != null) {
            zeroEquip.setLines(toStamp.getLines());
            zeroEquip.setPotential1(toStamp.getPotential1());
            zeroEquip.setPotential2(toStamp.getPotential2());
            zeroEquip.setPotential3(toStamp.getPotential3());
         }

         sucstamp = true;
      }

      useInventory.removeItem(stamp.getPosition(), (short)1, false);
      c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(stamp, toStamp, false, true, dst < 0));
      c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), sucstamp, stamp.getItemId(), toStamp.getItemId()));
      if (zeroEquip != null) {
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(stamp, zeroEquip, false, true, dst < 0));
         c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), sucstamp, stamp.getItemId(), zeroEquip.getItemId()));
         c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
      }

      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static void UseAdditionalStamp(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      short slot = slea.readShort();
      short dst = slea.readShort();
      boolean sucstamp = false;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Equip toStamp;
      if (dst < 0) {
         toStamp = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
      } else {
         toStamp = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
      }

      Equip zeroEquip = null;
      if (GameConstants.isZeroWeapon(toStamp.getItemId())) {
         zeroEquip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(dst == -11 ? -10 : -11));
      }

      MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
      Item stamp = useInventory.getItem(slot);
      if (Randomizer.isSuccess(ii.getSuccess(toStamp.getItemId(), c.getPlayer(), toStamp))) {
         setPotential(GradeRandomOption.Additional, true, toStamp, 2);
         sucstamp = true;
         if (zeroEquip != null) {
            zeroEquip.setLines(toStamp.getLines());
            zeroEquip.setPotential4(toStamp.getPotential4());
            zeroEquip.setPotential5(toStamp.getPotential5());
            zeroEquip.setPotential6(toStamp.getPotential6());
         }
      }

      useInventory.removeItem(stamp.getPosition(), (short)1, false);
      c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(stamp, toStamp, false, true, dst < 0));
      c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), sucstamp, stamp.getItemId(), toStamp.getItemId()));
      if (zeroEquip != null) {
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(stamp, zeroEquip, false, true, dst < 0));
         c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), sucstamp, stamp.getItemId(), zeroEquip.getItemId()));
         c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
      }

      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static void UseChooseCube(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      byte type = slea.readByte();
      Equip equip = null;
      Equip zeroequip = null;
      Equip memorialEquip = c.getPlayer().memorialCube;
      if (memorialEquip != null) {
         if (c.getPlayer().memorialCube.getPosition() > 0) {
            equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(c.getPlayer().memorialCube.getPosition());
         } else {
            equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(c.getPlayer().memorialCube.getPosition());
         }

         if (equip != null) {
            if (type == 6) {
               if (GameConstants.isAndroid(equip.getItemId())) {
                  if (equip.getItemId() != memorialEquip.getItemId() || equip.getUniqueId() != memorialEquip.getUniqueId()) {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                     return;
                  }
               } else if (equip.getItemId() != memorialEquip.getItemId() || equip.getSerialNumberEquip() != memorialEquip.getSerialNumberEquip()) {
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                  return;
               }

               equip.set(c.getPlayer().memorialCube);
            } else if (type != 2 && type != 7) {
               c.getPlayer().dropMessage(5, "오류가 발생했습니다. type : " + type);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("큐브 옵션 선택 (계정 : ");
            sb.append(c.getAccountName());
            sb.append(", 캐릭터 : ");
            sb.append(c.getPlayer().getName());
            sb.append(", 선택옵션 : ");
            if (type != 7 && type != 2) {
               sb.append("After");
            } else {
               sb.append("Before");
            }

            long serialNumber = 0L;
            sb.append(" (정보 : ");
            serialNumber = equip.getSerialNumberEquip();
            sb.append(equip.toString());
            sb.append(")");
            LoggingManager.putLog(
               new EnchantLog(
                  c.getPlayer(),
                  c.getPlayer().memorialCubeItemID,
                  equip.getItemId(),
                  serialNumber,
                  EnchantLogType.SelectChooseCube.getType(),
                  EnchantLogResult.Success.getType(),
                  sb
               )
            );
            if (GameConstants.isZeroWeapon(c.getPlayer().memorialCube.getItemId())) {
               zeroequip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
               zeroequip.setState(equip.getState());
               zeroequip.setLines(equip.getLines());
               zeroequip.setPotential1(equip.getPotential1());
               zeroequip.setPotential2(equip.getPotential2());
               zeroequip.setPotential3(equip.getPotential3());
               zeroequip.setPotential4(equip.getPotential4());
               zeroequip.setPotential5(equip.getPotential5());
               zeroequip.setPotential6(equip.getPotential6());
            }

            c.getPlayer().memorialCube = null;
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(equip));
            if (zeroequip != null) {
               c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(zeroequip));
               c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
            }
         }
      }
   }

   public static final void addToScrollLog(
      int accountID, int charID, int scrollID, int itemID, byte oldSlots, byte newSlots, byte viciousHammer, String result, boolean ws, boolean ls, int vega
   ) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("INSERT INTO scroll_log VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
         ps.setInt(1, accountID);
         ps.setInt(2, charID);
         ps.setInt(3, scrollID);
         ps.setInt(4, itemID);
         ps.setByte(5, oldSlots);
         ps.setByte(6, newSlots);
         ps.setByte(7, viciousHammer);
         ps.setString(8, result);
         ps.setByte(9, (byte)(ws ? 1 : 0));
         ps.setByte(10, (byte)(ls ? 1 : 0));
         ps.setInt(11, vega);
         ps.execute();
         ps.close();
      } catch (SQLException var17) {
         FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var17);
      }
   }

   public static void UseBlackFlame(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         BlackRebirthFlame brf = chr.getBlackRebirthFlame();
         if (brf != null) {
            Equip beforeItem = brf.getBeforeItem();
            Equip afterItem = brf.getAfterItem();
            if (beforeItem != null && afterItem != null) {
               if (GameConstants.IsBlackRebirthFlame(brf.getBlackRebirthItemID())) {
                  int command = slea.readInt();
                  int print_ui = 0;
                  int select_before = 1;
                  int select_after = 2;
                  int reroll = 3;
                  switch (command) {
                     case 0:
                        chr.send(setBlackFlameUI(true, beforeItem));
                        chr.send(setBlackFlameUI(false, afterItem));
                        break;
                     case 1:
                     case 2: {
                        MapleInventoryType type = beforeItem.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
                        Equip eq = (Equip)chr.getInventory(type).getItem(beforeItem.getPosition());
                        if (command == 1) {
                           eq.setExGradeOption(beforeItem.getExGradeOption());
                        } else {
                           eq.setExGradeOption(afterItem.getExGradeOption());
                           eq.setKarmaCount(afterItem.getKarmaCount());
                           eq.setFlag(afterItem.getFlag());
                        }

                        chr.send(releaseBlackFlameUI(eq));
                        chr.send(CWvsContext.InventoryPacket.updateEquipSlot(eq));
                        chr.setBlackRebirthFlame(null);
                        StringBuilder sb = new StringBuilder();
                        sb.append("검환불 옵션 선택 (계정 : ");
                        sb.append(c.getAccountName());
                        sb.append(", 캐릭터 : ");
                        sb.append(c.getPlayer().getName());
                        sb.append(", 선택옵션 : ");
                        if (command == 1) {
                           sb.append("Before");
                        } else {
                           sb.append("After");
                        }

                        long serialNumber = 0L;
                        sb.append(" (정보 : ");
                        serialNumber = eq.getSerialNumberEquip();
                        sb.append(eq.toString());
                        sb.append(")");
                        LoggingManager.putLog(
                           new EnchantLog(
                              c.getPlayer(),
                              2048753,
                              eq.getItemId(),
                              serialNumber,
                              EnchantLogType.SelectBlackFlame.getType(),
                              EnchantLogResult.Success.getType(),
                              sb
                           )
                        );
                        break;
                     }
                     case 3: {
                        MapleInventoryType type = beforeItem.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
                        Equip eq = (Equip)chr.getInventory(type).getItem(beforeItem.getPosition());
                        int scrollPos = chr.getInventory(MapleInventoryType.USE).listById(brf.getBlackRebirthItemID()).get(0).getPosition();
                        chr.send(rerollBlackFlameUI(scrollPos, eq));
                        chr.setBlackRebirthFlame(null);
                     }
                  }
               }
            }
         }
      }
   }

   private static byte[] setBlackFlameUI(boolean isBefore, Equip item) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_BLACK_REBIRTH_FLAME_UI.getValue());
      packet.writeInt(0);
      packet.write(1);
      packet.write(isBefore);
      HashMap<ExItemType, Integer> bonusStat = (HashMap<ExItemType, Integer>)BonusStat.getExItemOptionsBlack(item);
      int bonusStatSize = 0;

      for (ExItemType ext : bonusStat.keySet()) {
         if (ext.getType() < 4 || ext.getType() > 9) {
            bonusStatSize++;
         }
      }

      packet.writeInt(bonusStatSize);

      for (Entry<ExItemType, Integer> entry : bonusStat.entrySet()) {
         if (entry.getKey().getType() < 4 || entry.getKey().getType() > 9) {
            packet.writeInt(entry.getKey().getType());
            int bonusStatValue = BonusStat.getBonusStat(item, entry.getKey(), entry.getValue());
            if (entry.getKey().getType() >= 0 && entry.getKey().getType() <= 3) {
               bonusStatValue = entry.getValue();
               ExItemType[] eTypes = null;
               switch (entry.getKey().getType()) {
                  case 0:
                     eTypes = new ExItemType[]{ExItemType.StrDex, ExItemType.StrInt, ExItemType.StrLuk};
                     break;
                  case 1:
                     eTypes = new ExItemType[]{ExItemType.StrDex, ExItemType.DexInt, ExItemType.DexLuk};
                     break;
                  case 2:
                     eTypes = new ExItemType[]{ExItemType.StrInt, ExItemType.IntLuk, ExItemType.DexInt};
                     break;
                  case 3:
                     eTypes = new ExItemType[]{ExItemType.StrLuk, ExItemType.IntLuk, ExItemType.DexLuk};
               }

               int tempValue = 0;

               for (ExItemType eType : eTypes) {
                  if (bonusStat.get(eType) != null) {
                     bonusStatValue -= bonusStat.get(eType);
                     tempValue += BonusStat.getBonusStat(item, eType, bonusStat.get(eType));
                  }
               }

               bonusStatValue = BonusStat.getBonusStat(item, entry.getKey(), bonusStatValue);
               bonusStatValue += tempValue;
            }

            packet.writeInt(bonusStatValue);
         }
      }

      if (!isBefore) {
         PacketHelper.addItemInfo(packet, item);
      }

      return packet.getPacket();
   }

   private static byte[] releaseBlackFlameUI(Equip item) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_REBIRTH_FLAME_UI.getValue());
      packet.writeLong(item.getSerialNumberEquip());
      packet.writeLong(0L);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeShort(0);
      packet.writeInt(0);
      packet.writeInt(0);
      return packet.getPacket();
   }

   private static byte[] rerollBlackFlameUI(int scrollPos, Equip item) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_REBIRTH_FLAME_UI.getValue());
      packet.writeLong(item.getSerialNumberEquip());
      packet.writeLong(0L);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeShort(0);
      packet.writeInt(scrollPos);
      packet.writeInt(item.getPosition());
      return packet.getPacket();
   }

   private static byte[] showTransMissionExItemOptions(Equip equip) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_TRANSMISSION_EX_ITEM_OPTIONS.getValue());
      packet.write(2);
      PacketHelper.addItemInfo(packet, equip);
      return packet.getPacket();
   }

   private static byte[] showTransMissionExItemOptionsResult(Equip srcItem, Equip dstItem) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_TRANSMISSION_EX_ITEM_OPTIONS.getValue());
      packet.write(3);
      PacketHelper.addItemInfo(packet, srcItem);
      PacketHelper.addItemInfo(packet, dstItem);
      return packet.getPacket();
   }

   private static byte[] errorTransMissionExItemOptions() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_TRANSMISSION_EX_ITEM_OPTIONS.getValue());
      packet.write(4);
      return packet.getPacket();
   }

   public static void UseTransmissionExItemOptions(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         slea.readInt();
         byte mode = slea.readByte();
         int usePos = slea.readInt();
         Item useItem = chr.getInventory(MapleInventoryType.USE).getItem((short)usePos);
         if (useItem == null) {
            c.getPlayer().send(errorTransMissionExItemOptions());
         }

         switch (mode) {
            case 0:
               if (useItem.getItemId() == 2538000) {
                  int src = slea.readInt();
                  int dst = slea.readInt();
                  Equip srcItem = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem((short)src);
                  Equip dstItem = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem((short)dst);
                  Equip resultItem = (Equip)dstItem.copy();
                  resultItem.setExGradeOption(srcItem.getExGradeOption());
                  c.getPlayer().send(showTransMissionExItemOptions(resultItem));
               } else {
                  c.getPlayer().send(errorTransMissionExItemOptions());
               }
               break;
            case 1:
               if (useItem.getItemId() == 2538000) {
                  int src = slea.readInt();
                  int dst = slea.readInt();
                  Equip srcItem = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem((short)src);
                  Equip dstItem = (Equip)chr.getInventory(MapleInventoryType.EQUIP).getItem((short)dst);
                  if (srcItem == null || dstItem == null) {
                     c.getPlayer().send(errorTransMissionExItemOptions());
                     return;
                  }

                  Equip srcItemCopy = (Equip)srcItem.copy();
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short)usePos, (short)1, false);
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (short)src, (short)1, false);
                  dstItem.setExGradeOption(srcItemCopy.getExGradeOption());
                  c.getPlayer().send(CWvsContext.InventoryPacket.updateInventoryItem(MapleInventoryType.EQUIP, dstItem, false, chr));
                  c.getPlayer().send(showTransMissionExItemOptionsResult(srcItemCopy, dstItem));
               } else {
                  c.getPlayer().send(errorTransMissionExItemOptions());
               }
               break;
            default:
               c.getPlayer().send(errorTransMissionExItemOptions());
         }
      }
   }

   public static boolean UseUpgradeScroll(short scrollPos, short dst, byte ws, MapleClient c, MapleCharacter chr, int invType) {
      boolean whiteScroll = false;
      boolean recovery = false;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if ((ws & 2) == 2) {
         whiteScroll = true;
      }

      Equip toScroll = null;
      Equip toScrollAlpha = null;
      if (dst < 0) {
         toScroll = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
         if (GameConstants.isZeroWeapon(toScroll.getItemId())) {
            toScrollAlpha = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(toScroll.getPosition() == -11 ? -10 : -11));
         }
      } else {
         toScroll = (Equip)chr.getInventory(MapleInventoryType.getByType((byte)invType)).getItem(dst);
      }

      if (toScroll == null) {
         return false;
      } else {
         byte origUpgradeSlots = toScroll.getUpgradeSlots();
         Equip returnItem = null;
         if (ItemFlag.RETURN_SCROLLED.check(toScroll.getFlag())) {
            returnItem = (Equip)toScroll.copy();
            chr.returnScroll = returnItem;
         }

         byte oldLevel = toScroll.getLevel();
         byte oldCHUC = (byte)toScroll.getCHUC();
         byte oldState = toScroll.getState();
         int oldFlag = toScroll.getFlag();
         byte oldSlots = toScroll.getUpgradeSlots();
         Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(scrollPos);
         if (scroll == null) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(scrollPos);
         } else if (!GameConstants.isSpecialScroll(scroll.getItemId())
            && !GameConstants.isCleanSlate(scroll.getItemId())
            && !GameConstants.isEquipScroll(scroll.getItemId())
            && !GameConstants.isPotentialScroll(scroll.getItemId())
            && !GameConstants.isRebirhFireScroll(scroll.getItemId())
            && scroll.getItemId() / 10000 != 204
            && scroll.getItemId() / 10000 != 264
            && scroll.getItemId() != 2643132
            && scroll.getItemId() != 2643133
            && scroll.getItemId() != 2643131) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(scrollPos);
         }

         if (GameConstants.IsBlackRebirthFlame(scroll.getItemId())) {
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.BLACK_REBIRTH_FLAME_UI.getValue());
            p.writeLong(toScroll.getSerialNumberEquip());
            p.writeLong(toScroll.getExGradeOption());
            p.writeInt(toScroll.getPosition());
            p.writeInt(scroll.getItemId());
            p.writeShort(scroll.getPosition());
            p.writeInt(0);
            p.writeInt(0);
            chr.send(p.getPacket());
         }

         if (scroll.getItemId() == 2041200 && toScroll.getUpgradeSlots() < 3) {
            c.getPlayer().dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return false;
         } else if (DBConfig.isGanglim && GameConstants.isZeniaScroll(scroll.getItemId()) && toScroll.getUpgradeSlots() <= 0) {
            c.getPlayer().dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return false;
         } else {
            if (!GameConstants.isSpecialScroll(scroll.getItemId())
               && !GameConstants.isCleanSlate(scroll.getItemId())
               && !GameConstants.isEquipScroll(scroll.getItemId())
               && scroll.getItemId() != 2049360
               && scroll.getItemId() != 2049361
               && !GameConstants.isPotentialScroll(scroll.getItemId())
               && scroll.getItemId() / 10000 != 264) {
               int[] itemId = new int[]{
                  1352100,
                  1352101,
                  1352102,
                  1352103,
                  1352104,
                  1352105,
                  1352106,
                  1352107,
                  1352000,
                  1352001,
                  1352002,
                  1352003,
                  1352004,
                  1352005,
                  1352006,
                  1352007,
                  1099000,
                  1099001,
                  1099002,
                  1099003,
                  1099004,
                  1098000,
                  1098001,
                  1098002,
                  1098003,
                  1352500,
                  1352501,
                  1352502,
                  1352503,
                  1352504,
                  1352600,
                  1352601,
                  1352602,
                  1352603,
                  1352604,
                  1672020,
                  1352400,
                  1352401,
                  1352402,
                  1352403
               };

               for (int i = 0; i < itemId.length; i++) {
                  if (toScroll.getItemId() == itemId[i]) {
                     if (toScroll.getUpgradeSlots() == 0) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
                     }

                     chr.dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
                     return false;
                  }
               }

               if (!GameConstants.IsExNewScroll(scroll.getItemId())
                  && !GameConstants.IsBlackRebirthFlame(scroll.getItemId())
                  && toScroll.getUpgradeSlots() == 0) {
                  chr.dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return false;
               }

               if (!GameConstants.IsExNewScroll(scroll.getItemId())
                  && !GameConstants.IsBlackRebirthFlame(scroll.getItemId())
                  && toScroll.getUpgradeSlots() < 1
                  && toScroll.getItemId() / 1000 != 1352
                  && toScroll.getUpgradeSlots() < ii.getUpgradeScrollUseSlot(scroll.getItemId())
                  && toScroll.getItemId() / 100 != 1099
                  && toScroll.getItemId() / 100 != 1098) {
                  chr.dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
                  return false;
               }
            } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
               if (scroll.getItemId() != 2049360 && toScroll.getUpgradeSlots() >= 1
                  || scroll.getItemId() == 2049360 && !toScroll.isAmazingHyperUpgradeUsed() && toScroll.getCHUC() > 0
                  || ii.isCash(toScroll.getItemId())) {
                  c.getPlayer().dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return false;
               }
            } else if (GameConstants.isPotentialScroll(scroll.getItemId())
               && (toScroll.getState() >= 3 && (toScroll.getState() < 17 || toScroll.getState() >= 20) || scroll.getItemId() != 2049790)
               && toScroll.getState() >= 1
               && scroll.getItemId() != 2049731
               && scroll.getItemId() != 2049762) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            }

            if (GameConstants.isCleanSlate(scroll.getItemId())
               && toScroll.getLevel() + toScroll.getUpgradeSlots()
                  >= ii.getEquipStats(toScroll.getItemId()).get("tuc") + (toScroll.getViciousHammer() > 0 ? 1 : 0)) {
               chr.dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            } else if ((scroll.getItemId() == 2049731 || scroll.getItemId() == 2049762) && toScroll.getState() == 0) {
               chr.dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            } else if (GameConstants.isProstyScroll(scroll.getItemId()) && toScroll.getUpgradeSlots() <= 0) {
               chr.dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            } else if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId())) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            } else if ((
                  GameConstants.isCleanSlate(scroll.getItemId())
                     || GameConstants.isTablet(scroll.getItemId())
                     || GameConstants.isChaosScroll(scroll.getItemId())
               )
               && ii.isCash(toScroll.getItemId())) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            } else if ((!GameConstants.isTablet(scroll.getItemId()) || toScroll.getDurability() >= 0)
               && !GameConstants.isTablet(scroll.getItemId())
               && toScroll.getDurability() >= 0) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return false;
            } else {
               Item wscroll = null;
               List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
               if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
                  return false;
               } else {
                  if (whiteScroll) {
                     wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
                     if (wscroll == null) {
                        whiteScroll = false;
                     }
                  }

                  if (scroll.getItemId() == 2049115 && toScroll.getItemId() != 1003068) {
                     return false;
                  } else {
                     if (GameConstants.isTablet(scroll.getItemId())) {
                        switch (scroll.getItemId() % 1000 / 100) {
                           case 0:
                              if (GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                                 return false;
                              }
                              break;
                           case 1:
                              if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                                 return false;
                              }
                              break;
                           case 2:
                              if (GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                                 return false;
                              }
                              break;
                           case 3:
                              if (!GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                                 return false;
                              }
                        }
                     } else if (scroll.getItemId() != 2643133
                        && scroll.getItemId() != 2643132
                        && scroll.getItemId() != 2643131
                        && !GameConstants.isAccessoryScroll(scroll.getItemId())
                        && !GameConstants.isChaosScroll(scroll.getItemId())
                        && !GameConstants.isCleanSlate(scroll.getItemId())
                        && !GameConstants.isEquipScroll(scroll.getItemId())
                        && !GameConstants.isPotentialScroll(scroll.getItemId())
                        && !GameConstants.isEightRockScroll(scroll.getItemId())
                        && !GameConstants.isSpecialScroll(scroll.getItemId())
                        && scroll.getItemId() != 2049360
                        && scroll.getItemId() != 2049361
                        && !GameConstants.isRebirhFireScroll(scroll.getItemId())
                        && !GameConstants.isProstyScroll(scroll.getItemId())
                        && !ii.canScroll(scroll.getItemId(), toScroll.getItemId())
                        && toScroll.getItemId() / 1000 != 1352
                        && toScroll.getItemId() / 10000 != 167
                        && toScroll.getItemId() / 100 != 1099
                        && toScroll.getItemId() / 100 != 1098
                        && scroll.getItemId() / 100 != 20496) {
                        return false;
                     }

                     if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
                        return false;
                     } else if (scroll.getQuantity() <= 0) {
                        return false;
                     } else {
                        Equip scrolled = ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr);
                        Equip.ScrollResult scrollSuccess;
                        if (scrolled == null) {
                           scrollSuccess = Equip.ScrollResult.CURSE;
                        } else if (scroll.getItemId() == 5064200
                           || GameConstants.isRebirhFireScroll(scroll.getItemId())
                           || scroll.getItemId() == 2643133
                           || scroll.getItemId() == 2643131
                           || scroll.getItemId() == 2643132) {
                           scrollSuccess = Equip.ScrollResult.SUCCESS;
                        } else if (scrolled.getLevel() != oldLevel
                           || scrolled.getCHUC() > oldCHUC
                           || scrolled.getState() > oldState
                           || scrolled.getFlag() > oldFlag) {
                           scrollSuccess = Equip.ScrollResult.SUCCESS;
                        } else if (GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots) {
                           scrollSuccess = Equip.ScrollResult.SUCCESS;
                        } else {
                           scrollSuccess = Equip.ScrollResult.FAIL;
                           if (ItemFlag.RECOVERY_SCROLLED.check(toScroll.getFlag())) {
                              recovery = true;
                           }
                        }

                        if (scroll.getItemId() == 2049731 || scroll.getItemId() == 2049762 || scroll.getItemId() == 2049047) {
                           scrollSuccess = Equip.ScrollResult.SUCCESS;
                        }

                        if (scrollSuccess == Equip.ScrollResult.FAIL && origUpgradeSlots != toScroll.getUpgradeSlots()) {
                           int tucProtectR = chr.getStat().itemTUCProtectR;
                           if (tucProtectR > 0 && Randomizer.isSuccess(tucProtectR)) {
                              chr.dropMessage(5, "실패를 두려워 않는 효과로 업그레이드 가능 횟수가 차감되지 않았습니다.");
                              toScroll.setUpgradeSlots(origUpgradeSlots);
                              if (toScrollAlpha != null) {
                                 toScrollAlpha.setUpgradeSlots((byte)(toScrollAlpha.getUpgradeSlots() + 1));
                              }
                           }
                        }

                        if (recovery && scroll.getItemId() != 2049762) {
                           chr.dropMessage(5, "주문서의 효과로 사용된 주문서가 차감되지 않았습니다.");
                        } else {
                           chr.removeItem(scroll.getItemId(), -1);
                        }

                        if (whiteScroll) {
                           MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short)1, false, false);
                        }

                        StringBuilder sb = new StringBuilder();
                        sb.append("주문서 강화 결과 ");
                        sb.append(scrollSuccess == Equip.ScrollResult.SUCCESS ? "성공" : (scrollSuccess == Equip.ScrollResult.FAIL ? "실패" : "파괴"));
                        sb.append(" (계정 : ");
                        sb.append(c.getAccountName());
                        sb.append(", 캐릭터 : ");
                        sb.append(c.getPlayer().getName());
                        if (scroll.getItemId() == 2049360 || scroll.getItemId() == 2049361) {
                           boolean safety = (toScroll.getFlag() & ItemFlag.SAFETY_SCROLLED.getValue()) != 0;
                           boolean protect = (toScroll.getFlag() & ItemFlag.PROTECTION_SCROLLED.getValue()) != 0;
                           boolean recovery_ = (toScroll.getFlag() & ItemFlag.RECOVERY_SCROLLED.getValue()) != 0;
                           sb.append(", 놀라운 장비 주문서 (세이프티 사용 여부 : " + safety + ", 프로텍트 사용 여부 : " + protect + ", 리커버리 사용 여부 : " + recovery_ + ")");
                        }

                        boolean return_ = (toScroll.getFlag() & ItemFlag.RETURN_SCROLLED.getValue()) != 0;
                        sb.append(", 리턴스크롤 사용여부 : ").append(return_);
                        sb.append(", 장비옵션 [");
                        sb.append(toScroll.toString());
                        sb.append("])");
                        EnchantLogResult result = EnchantLogResult.Success;
                        if (scrollSuccess != null) {
                           if (scrollSuccess == Equip.ScrollResult.FAIL) {
                              result = EnchantLogResult.Failed;
                           } else if (scrollSuccess == Equip.ScrollResult.CURSE) {
                              result = EnchantLogResult.Destroyed;
                           }
                        }

                        LoggingManager.putLog(
                           new EnchantLog(
                              chr,
                              scroll.getItemId(),
                              toScroll.getItemId(),
                              toScroll.getSerialNumberEquip(),
                              EnchantLogType.EquipScroll.getType(),
                              result.getType(),
                              sb
                           )
                        );
                        if (ItemFlag.RECOVERY_SCROLLED.check(toScroll.getFlag())) {
                           toScroll.setFlag((short)(toScroll.getFlag() - ItemFlag.RECOVERY_SCROLLED.getValue()));
                           if (GameConstants.isZero(chr.getJob()) && toScrollAlpha != null) {
                              toScrollAlpha.setFlag((short)(toScrollAlpha.getFlag() - ItemFlag.RECOVERY_SCROLLED.getValue()));
                           }
                        }

                        if (ItemFlag.SAFETY_SCROLLED.check(toScroll.getFlag())) {
                           toScroll.setFlag((short)(toScroll.getFlag() - ItemFlag.SAFETY_SCROLLED.getValue()));
                           if (GameConstants.isZero(chr.getJob()) && toScrollAlpha != null) {
                              toScrollAlpha.setFlag((short)(toScrollAlpha.getFlag() - ItemFlag.SAFETY_SCROLLED.getValue()));
                           }
                        }

                        if (ItemFlag.PROTECTION_SCROLLED.check(toScroll.getFlag())) {
                           toScroll.setFlag((short)(toScroll.getFlag() - ItemFlag.PROTECTION_SCROLLED.getValue()));
                           if (GameConstants.isZero(chr.getJob()) && toScrollAlpha != null) {
                              toScrollAlpha.setFlag((short)(toScrollAlpha.getFlag() - ItemFlag.PROTECTION_SCROLLED.getValue()));
                           }
                        }

                        if (ItemFlag.LUCKY_DAY_SCROLLED.check(toScroll.getFlag())) {
                           toScroll.setFlag((short)(toScroll.getFlag() - ItemFlag.LUCKY_DAY_SCROLLED.getValue()));
                           if (GameConstants.isZero(chr.getJob()) && toScrollAlpha != null) {
                              toScrollAlpha.setFlag((short)(toScrollAlpha.getFlag() - ItemFlag.LUCKY_DAY_SCROLLED.getValue()));
                           }
                        }

                        boolean returnScroll = false;
                        if (ItemFlag.RETURN_SCROLLED.check(toScroll.getFlag())) {
                           returnScroll = true;
                           toScroll.setFlag((short)(toScroll.getFlag() - ItemFlag.RETURN_SCROLLED.getValue()));
                           if (GameConstants.isZero(chr.getJob()) && toScrollAlpha != null) {
                              toScrollAlpha.setFlag((short)(toScrollAlpha.getFlag() - ItemFlag.RETURN_SCROLLED.getValue()));
                           }
                        }

                        if (scrollSuccess == Equip.ScrollResult.CURSE && c.getPlayer().getStat().itemCursedProtectR > 0) {
                           if (c.getPlayer().isGM()) {
                              System.out.println("파괴는 남의 일 확률 : " + c.getPlayer().getStat().itemCursedProtectR + "%");
                           }

                           if (Randomizer.isSuccess(c.getPlayer().getStat().itemCursedProtectR)) {
                              c.getPlayer().dropMessage(5, "파괴는 남의 일 효과로 인하여 아이템이 파괴되지 않았습니다.");
                              scrollSuccess = Equip.ScrollResult.FAIL;
                           }
                        }

                        if (scrollSuccess == Equip.ScrollResult.CURSE) {
                           if (toScrollAlpha != null) {
                              ii.zeroEquipReset(chr);
                           } else {
                              c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(scroll, toScroll, true, false, dst < 0));
                              if (dst < 0) {
                                 chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
                              } else {
                                 chr.getInventory(MapleInventoryType.getByType((byte)invType)).removeItem(toScroll.getPosition());
                              }
                           }
                        } else if (toScrollAlpha != null) {
                           c.getSession()
                              .writeAndFlush(CWvsContext.InventoryPacket.updateSpecialItemUse(toScrollAlpha, (byte)invType, c.getPlayer(), (byte)12));
                           c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)invType, c.getPlayer(), (byte)11));
                        } else {
                           c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(scroll, scrolled, false, false, dst < 0));
                        }

                        if (!GameConstants.isZero(chr.getJob()) || toScroll.getPosition() != -11) {
                           chr.getMap()
                              .broadcastMessage(
                                 chr, CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, scroll.getItemId(), toScroll.getItemId()), true
                              );
                        }

                        if (dst < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE)) {
                           chr.equipChanged();
                        }

                        if (GameConstants.IsPowerfulRebirthFlame(scroll.getItemId()) || GameConstants.IsEternalRebirthFlame(scroll.getItemId())) {
                           PacketEncoder p = new PacketEncoder();
                           p.writeShort(SendPacketOpcode.REBIRTH_FIRE_UI.getValue());
                           p.writeInt(scroll.getItemId());
                           p.writeInt(scrolled.getPosition());
                           p.write(0);
                           chr.send(p.getPacket());
                        }

                        if (returnScroll && scrollSuccess == Equip.ScrollResult.SUCCESS) {
                           c.getSession().writeAndFlush(CField.ReturnScroll_Confirm(returnItem, scroll.getItemId()));
                           c.getSession().writeAndFlush(CField.ReturnScroll_Modify(toScroll, scroll.getItemId()));
                        }

                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   public static void UseFlagScroll(PacketDecoder slea, MapleClient c) {
      short slot = slea.readShort();
      byte invType = slea.readByte();
      slea.skip(1);
      byte dst = (byte)slea.readShort();
      MapleCharacter player = c.getPlayer();
      slea.skip(1);
      boolean use = false;
      Equip toScroll;
      if (dst < 0) {
         toScroll = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
      } else {
         toScroll = (Equip)player.getInventory(MapleInventoryType.getByType(invType)).getItem(dst);
      }

      Equip toScroll2 = null;
      if (GameConstants.isBetaWeapon(toScroll.getItemId())) {
         toScroll2 = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
      }

      Item scroll = player.getInventory(MapleInventoryType.CASH).getItem(slot);
      if (scroll == null || !GameConstants.isSpecialCSScroll(scroll.getItemId())) {
         scroll = player.getInventory(MapleInventoryType.USE).getItem(slot);
         use = true;
      }

      if (!use) {
         if (scroll.getItemId() == 5064000) {
            int flag = toScroll.getFlag();
            flag |= ItemFlag.PROTECTION_SCROLLED.getValue();
            toScroll.setFlag(flag);
            if (toScroll2 != null) {
               toScroll2.setFlag(flag);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         } else if (scroll.getItemId() == 5064100) {
            int flag = toScroll.getFlag();
            flag |= ItemFlag.SAFETY_SCROLLED.getValue();
            toScroll.setFlag(flag);
            if (toScroll2 != null) {
               toScroll2.setFlag(flag);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         } else if (scroll.getItemId() == 5064300) {
            int flag = toScroll.getFlag();
            flag |= ItemFlag.RECOVERY_SCROLLED.getValue();
            toScroll.setFlag(flag);
            if (toScroll2 != null) {
               toScroll2.setFlag(flag);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         } else if (scroll.getItemId() == 5063000) {
            int flag = toScroll.getFlag();
            flag |= ItemFlag.LUCKY_DAY_SCROLLED.getValue();
            toScroll.setFlag(flag);
            if (toScroll2 != null) {
               toScroll2.setFlag(flag);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         } else if (scroll.getItemId() == 5063100) {
            int flag = toScroll.getFlag();
            if (ItemFlag.LUCKY_DAY_SCROLLED.check(flag) || ItemFlag.PROTECTION_SCROLLED.check(flag)) {
               player.send(CWvsContext.enableActions(player));
               return;
            }

            flag |= ItemFlag.LUCKY_DAY_SCROLLED.getValue();
            flag |= ItemFlag.PROTECTION_SCROLLED.getValue();
            toScroll.setFlag(flag);
            if (toScroll2 != null) {
               toScroll2.setFlag(flag);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         }

         player.getInventory(MapleInventoryType.CASH).removeItem(scroll.getPosition(), (short)1, false);
      } else {
         if (scroll.getItemId() == 2531000) {
            int flagx = toScroll.getFlag();
            flagx |= ItemFlag.PROTECTION_SCROLLED.getValue();
            toScroll.setFlag(flagx);
            if (toScroll2 != null) {
               toScroll2.setFlag(flagx);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         } else if (scroll.getItemId() == 2532000) {
            int flagx = toScroll.getFlag();
            flagx |= ItemFlag.SAFETY_SCROLLED.getValue();
            toScroll.setFlag(flagx);
            if (toScroll2 != null) {
               toScroll2.setFlag(flagx);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         } else if (scroll.getItemId() == 2530000 || scroll.getItemId() == 2530001 || scroll.getItemId() == 2530002 || scroll.getItemId() == 2530004) {
            int flagx = toScroll.getFlag();
            flagx |= ItemFlag.LUCKY_DAY_SCROLLED.getValue();
            toScroll.setFlag(flagx);
            if (toScroll2 != null) {
               toScroll2.setFlag(flagx);
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll2, (byte)1, player, (byte)12));
               player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(toScroll, (byte)1, player, (byte)11));
            } else {
               player.send(CWvsContext.InventoryPacket.addInventorySlot(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toScroll));
            }
         }

         player.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short)1, false);
      }

      StringBuilder sb = new StringBuilder("플래그 주문서 사용 (");
      sb.append("계정 : ");
      sb.append(c.getAccountName());
      sb.append(", 캐릭터 : ");
      sb.append(c.getPlayer().getName());
      sb.append(" (정보 : ");
      sb.append(toScroll.toString());
      sb.append("))");
      LoggingManager.putLog(
         new EnchantLog(
            c.getPlayer(),
            scroll.getItemId(),
            toScroll.getItemId(),
            toScroll.getSerialNumberEquip(),
            EnchantLogType.FlagScroll.getType(),
            EnchantLogResult.Success.getType(),
            sb
         )
      );
      player.send(CWvsContext.InventoryPacket.scrolledItem(scroll, toScroll, false, false, false));
      player.getMap()
         .broadcastMessage(player, CField.getScrollEffect(player.getId(), Equip.ScrollResult.SUCCESS, scroll.getItemId(), toScroll.getItemId()), true);
      if (dst < 0) {
         player.equipChanged();
      }
   }

   public static void UseAdditionalScroll(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      short mode = slea.readShort();
      Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(mode);
      if (toUse.getItemId() >= 2048305 && toUse.getItemId() <= 2048316
         || toUse.getItemId() >= 2048337 && toUse.getItemId() <= 2048344
         || toUse.getItemId() == 2049731) {
         short slot = slea.readShort();
         Item item;
         if (slot < 0) {
            item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
         } else {
            item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
         }

         Equip eq = (Equip)item;
         if (eq.getState() == 0 || eq.getState() == 1 && eq.getPotential1() == 0) {
            c.getPlayer().dropMessage(1, "먼저 잠재능력을 열어주세요.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         if (toUse.getItemId() == 2049731) {
            if (eq.getAdditionalGrade() > 1) {
               c.getPlayer().dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }
         } else if (toUse.getItemId() == 2049762) {
            if (eq.getAdditionalGrade() > 2) {
               c.getPlayer().dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }
         } else if (eq.getPotential4() > 0) {
            c.getPlayer().dropMessage(1, "주문서를 사용할 수 없는 아이템입니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return;
         }

         Equip zeroEquip = null;
         if (GameConstants.isZeroWeapon(eq.getItemId())) {
            zeroEquip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(slot == -11 ? -10 : -11));
         }

         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         boolean succes = Randomizer.isSuccess(ii.getSuccess(item.getItemId(), c.getPlayer(), item));
         if (!succes) {
            MapleInventoryManipulator.removeFromSlot(c, slot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, item.getPosition(), (short)1, false);
         } else {
            if (toUse.getItemId() == 2049731) {
               setFirstPotentialEpic(GradeRandomOption.Additional, false, eq, 3);
            } else if (toUse.getItemId() == 2049762) {
               setFirstPotentialUnique(GradeRandomOption.Additional, false, eq, 3);
            } else if (toUse.getItemId() == 2048305 && DBConfig.isGanglim) {
               setFirstPotentialLegendary(GradeRandomOption.Additional, false, eq, 3);
            } else {
               setFirstPotential(GradeRandomOption.Additional, false, eq, Randomizer.nextInt(100) >= 20 && toUse.getItemId() != 2048306 ? 2 : 3);
            }

            if (zeroEquip != null) {
               zeroEquip.setState(eq.getState());
               zeroEquip.setLines(eq.getLines());
               zeroEquip.setPotential4(eq.getPotential4());
               zeroEquip.setPotential5(eq.getPotential5());
               zeroEquip.setPotential6(eq.getPotential6());
            }
         }

         StringBuilder sb = new StringBuilder("잠재능력 부여 주문서 사용 (");
         sb.append("계정 : ");
         sb.append(c.getAccountName());
         sb.append(", 캐릭터 : ");
         sb.append(c.getPlayer().getName());
         sb.append(" (정보 : ");
         sb.append(eq.toString());
         sb.append("))");
         LoggingManager.putLog(
            new EnchantLog(
               c.getPlayer(),
               toUse.getItemId(),
               eq.getItemId(),
               eq.getSerialNumberEquip(),
               EnchantLogType.AdditionalScroll.getType(),
               succes ? EnchantLogResult.Success.getType() : EnchantLogResult.Failed.getType(),
               sb
            )
         );
         c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(toUse, item, !succes, true, slot < 0));
         c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), succes, toUse.getItemId(), eq.getItemId()));
         if (zeroEquip != null) {
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(toUse, zeroEquip, !succes, true, slot < 0));
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), succes, toUse.getItemId(), zeroEquip.getItemId()));
         }

         MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false);
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void UseExceptionalScroll(PacketDecoder slea, MapleClient c) {
      int tick = slea.readInt();
      byte useslot = slea.readByte();
      short usepos = slea.readShort();
      byte equipslot = slea.readByte();
      short equippos = slea.readShort();
      Equip.ScrollResult scrollSuccess = Equip.ScrollResult.SUCCESS;
      Equip equip;
      if (equippos < 0) {
         equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(equippos);
      } else {
         equip = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equippos);
      }

      MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
      Item scroll = useInventory.getItem(usepos);
      if (scroll != null && equip != null) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         List<Integer> equipList = ii.getScrollReqs(scroll.getItemId());
         if (!equipList.contains(equip.getItemId())) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else if (equip.getExceptionalSlot() >= ii.getMaxExceptionalSlots(equip.getItemId())) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            int scrollId = scroll.getItemId();
            Map<String, Short> dataMap = ii.getScrollData(scrollId);
            equip.addExceptionalSlot((byte)1);
            equip.addExceptSTR(dataMap.getOrDefault("incSTR", (short)0));
            equip.addExceptDEX(dataMap.getOrDefault("incDEX", (short)0));
            equip.addExceptINT(dataMap.getOrDefault("incINT", (short)0));
            equip.addExceptLUK(dataMap.getOrDefault("incLUK", (short)0));
            equip.addExceptWATK(dataMap.getOrDefault("incPAD", (short)0));
            equip.addExceptMATK(dataMap.getOrDefault("incMAD", (short)0));
            equip.addExceptHP(dataMap.getOrDefault("incMHP", (short)0));
            equip.addExceptMP(dataMap.getOrDefault("incMMP", (short)0));
            StringBuilder sb = new StringBuilder();
            sb.append("검환불 옵션 선택 (계정 : ");
            sb.append(c.getAccountName());
            sb.append(", 캐릭터 : ");
            sb.append(c.getPlayer().getName());
            long serialNumber = 0L;
            sb.append(", (정보 : ");
            serialNumber = equip.getSerialNumberEquip();
            sb.append(equip.toString());
            sb.append(")");
            LoggingManager.putLog(
               new EnchantLog(
                  c.getPlayer(), scrollId, equip.getItemId(), serialNumber, EnchantLogType.SelectBlackFlame.getType(), EnchantLogResult.Success.getType(), sb
               )
            );
            useInventory.removeItem(scroll.getPosition(), (short)1, false);
            c.getPlayer()
               .getMap()
               .broadcastMessage(c.getPlayer(), CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, scroll.getItemId(), equip.getItemId()), true);
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(equip, scroll, false, false, false));
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateScrollandItem(scroll, equip, false));
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final boolean UseSkillBook(short slot, int itemId, MapleClient c, MapleCharacter chr) {
      Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
      if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
         Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getEquipStats(toUse.getItemId());
         if (skilldata == null) {
            return false;
         } else {
            boolean canuse = false;
            boolean success = false;
            int skill = 0;
            int maxlevel = 0;
            Integer SuccessRate = skilldata.get("success");
            Integer ReqSkillLevel = skilldata.get("reqSkillLevel");
            Integer MasterLevel = skilldata.get("masterLevel");
            byte i = 0;

            while (true) {
               Integer CurrentLoopedSkillId = skilldata.get("skillid" + i);
               i++;
               if (CurrentLoopedSkillId == null || MasterLevel == null) {
                  break;
               }

               Skill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
               if (CurrSkillData != null
                  && CurrSkillData.canBeLearnedBy(chr.getJob())
                  && (ReqSkillLevel == null || chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel)
                  && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                  canuse = true;
                  if (SuccessRate != null && Randomizer.nextInt(100) > SuccessRate) {
                     success = false;
                  } else {
                     success = true;
                     chr.changeSingleSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData), (byte)MasterLevel.intValue());
                  }

                  MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short)1, false);
                  break;
               }
            }

            c.getPlayer().getMap().broadcastMessage(CWvsContext.useSkillBook(chr, skill, maxlevel, canuse, success));
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return canuse;
         }
      } else {
         return false;
      }
   }

   public static final void UseCatchItem(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.readInt();
      c.getPlayer().setScrolledPosition((short)0);
      short slot = slea.readShort();
      int itemid = slea.readInt();
      MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
      Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
      Field map = chr.getMap();
      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static final void UseMountFood(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.readInt();
      short slot = slea.readShort();
      int itemid = slea.readInt();
      Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
      MapleMount mount = chr.getMount();
      if (itemid / 10000 == 226 && toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null) {
         int fatigue = mount.getFatigue();
         boolean levelup = false;
         mount.setFatigue((byte)-30);
         if (fatigue > 0) {
            mount.increaseExp();
            int level = mount.getLevel();
            if (level < 30 && mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1)) {
               mount.setLevel((byte)(level + 1));
               levelup = true;
            }
         }

         chr.getMap().broadcastMessage(CWvsContext.updateMount(chr, levelup));
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
      }

      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static final void UseScriptedNPCItem(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      int tickCount = slea.readInt();
      short slot = slea.readShort();
      int itemId = slea.readInt();
      Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
      long expiration_days = 0L;
      int mountid = 0;
      long time = System.currentTimeMillis();
      if (time - c.getPlayer().lastItemUsedTime <= 300L) {
         c.getPlayer().dropMessage(5, "지금은 사용자가 많아 아이템을 사용할 수 없습니다. 잠시 후 다시 시도해 주세요.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         c.getPlayer().lastItemUsedTime = time;
         if (toUse.getExpiration() > 0L && toUse.getExpiration() < time) {
            c.getPlayer().dropMessage(5, "이미 기간이 만료된 아이템입니다.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.inPVP()) {
               if (DBConfig.isGanglim) {
                  switch (toUse.getItemId()) {
                     case 2430732: {
                        int id = 0;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430885: {
                        int id = 1;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430886: {
                        int id = 2;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430887: {
                        int id = 3;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430888: {
                        int id = 4;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430889: {
                        int id = 5;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430890: {
                        int id = 6;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430891: {
                        int id = 7;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430892: {
                        int id = 8;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430893: {
                        int id = 9;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430894: {
                        int id = 10;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430945: {
                        int id = 11;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2430946: {
                        int id = 12;
                        chr.addCustomItem(id);
                        c.getPlayer().dropMessage(5, GameConstants.customItems.get(id).getName() + " 를 획득하였습니다. 소비칸에 특수 장비창 -> 인벤토리를 확인해주세요.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        break;
                     }
                     case 2431940:
                        int pirodo = 1000;
                        switch (c.getPlayer().getTier()) {
                           case 1:
                              pirodo = 60;
                              break;
                           case 2:
                              pirodo = 80;
                              break;
                           case 3:
                              pirodo = 100;
                              break;
                           case 4:
                              pirodo = 120;
                              break;
                           case 5:
                              pirodo = 160;
                              break;
                           case 6:
                              pirodo = 160;
                              break;
                           case 7:
                              pirodo = 160;
                              break;
                           case 8:
                              pirodo = 160;
                        }

                        long point = c.getPlayer().getKeyValue(123, "pp") + 10;
                        if (c.getPlayer().getKeyValue(123, "pp") >= pirodo) {
                           c.getPlayer().dropMessage(5, "이미 모든 피로도가 충전되있습니다.");
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           return;
                        }

                        if (c.getPlayer().getKeyValue(123, "pp") + 10 > pirodo) {
                           point = pirodo;
                        }

                        c.getPlayer().setKeyValue(123, "pp", String.valueOf(point));
                        c.getPlayer().dropMessage(5, "피로도가 증가했습니다. 피로도 : " + c.getPlayer().getKeyValue(123, "pp"));
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                  }
               }

               switch (toUse.getItemId()) {
                  case 2430007:
                     if (!DBConfig.isGanglim) {
                        MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        if (inventory.countById(3994102) >= 20
                           && inventory.countById(3994103) >= 20
                           && inventory.countById(3994104) >= 20
                           && inventory.countById(3994105) >= 20) {
                           MapleInventoryManipulator.addById(c, 2430008, (short)1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                        } else {
                           MapleInventoryManipulator.addById(c, 2430007, (short)1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        }

                        NPCScriptManager.getInstance().start(c, 2084001);
                     } else {
                        MapleItemInformationProvider iix = MapleItemInformationProvider.getInstance();
                        int npcId = iix.getScriptedItemNpc(itemId);
                        String script = iix.getScriptedItemScript(itemId);
                        Item item = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
                        if (item == null || item.getItemId() != itemId || item.getQuantity() < 1 || npcId == 0) {
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           return;
                        }

                        NPCScriptManager.getInstance().start(c, npcId, "consume_" + itemId, false);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     }
                     break;
                  case 2430008:
                     chr.saveLocation(SavedLocationType.RICHIE);
                     boolean warped = false;

                     for (int i = 390001000; i <= 390001004; i++) {
                        Field map = c.getChannelServer().getMapFactory().getMap(i);
                        if (map.getCharactersSize() == 0) {
                           chr.changeMap(map, map.getPortal(0));
                           warped = true;
                           break;
                        }
                     }

                     if (warped) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                     } else {
                        c.getPlayer().dropMessage(5, "All maps are currently in use, please try again later.");
                     }
                     break;
                  case 2430036:
                     mountid = 1027;
                     expiration_days = 1L;
                     break;
                  case 2430037:
                     mountid = 1028;
                     expiration_days = 1L;
                     break;
                  case 2430038:
                     mountid = 1029;
                     expiration_days = 1L;
                     break;
                  case 2430039:
                     mountid = 1030;
                     expiration_days = 1L;
                     break;
                  case 2430040:
                     mountid = 1031;
                     expiration_days = 1L;
                     break;
                  case 2430053:
                     mountid = 1027;
                     expiration_days = 30L;
                     break;
                  case 2430054:
                     mountid = 1028;
                     expiration_days = 30L;
                     break;
                  case 2430055:
                     mountid = 1029;
                     expiration_days = 30L;
                     break;
                  case 2430056:
                     mountid = 1035;
                     expiration_days = 30L;
                     break;
                  case 2430057:
                     mountid = 1033;
                     expiration_days = 30L;
                     break;
                  case 2430072:
                     mountid = 1034;
                     expiration_days = 7L;
                     break;
                  case 2430075:
                     mountid = 1038;
                     expiration_days = 15L;
                     break;
                  case 2430076:
                     mountid = 1039;
                     expiration_days = 15L;
                     break;
                  case 2430077:
                     mountid = 1040;
                     expiration_days = 15L;
                     break;
                  case 2430080:
                     mountid = 1042;
                     expiration_days = 20L;
                     break;
                  case 2430082:
                     mountid = 1044;
                     expiration_days = 7L;
                     break;
                  case 2430091:
                     mountid = 1049;
                     expiration_days = 10L;
                     break;
                  case 2430092:
                     mountid = 1050;
                     expiration_days = 10L;
                     break;
                  case 2430093:
                     mountid = 1051;
                     expiration_days = 10L;
                     break;
                  case 2430101:
                     mountid = 1052;
                     expiration_days = 10L;
                     break;
                  case 2430102:
                     mountid = 1053;
                     expiration_days = 10L;
                     break;
                  case 2430103:
                     mountid = 1054;
                     expiration_days = 30L;
                     break;
                  case 2430112:
                     if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 25) {
                           if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 25, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 2049400, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 10) {
                           if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 2049401, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Potential Scroll, 25 for Advanced Potential Scroll.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                     }
                     break;
                  case 2430117:
                     mountid = 1036;
                     expiration_days = 365L;
                     break;
                  case 2430118:
                     mountid = 1039;
                     expiration_days = 365L;
                     break;
                  case 2430119:
                     mountid = 1040;
                     expiration_days = 365L;
                     break;
                  case 2430120:
                     mountid = 1037;
                     expiration_days = 365L;
                     break;
                  case 2430130:
                  case 2430131:
                     if (GameConstants.isResist(c.getPlayer().getJob())) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getPlayer().gainExp(20000.0 + c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate(), true, true, false);
                     } else {
                        c.getPlayer().dropMessage(5, "You may not use this item.");
                     }
                     break;
                  case 2430132:
                  case 2430133:
                  case 2430134:
                  case 2430142:
                     if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getJob() == 3200
                           || c.getPlayer().getJob() == 3210
                           || c.getPlayer().getJob() == 3211
                           || c.getPlayer().getJob() == 3212) {
                           MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                           MapleInventoryManipulator.addById(c, 1382101, (short)1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else if (c.getPlayer().getJob() == 3300
                           || c.getPlayer().getJob() == 3310
                           || c.getPlayer().getJob() == 3311
                           || c.getPlayer().getJob() == 3312) {
                           MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                           MapleInventoryManipulator.addById(c, 1462093, (short)1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else if (c.getPlayer().getJob() != 3500
                           && c.getPlayer().getJob() != 3510
                           && c.getPlayer().getJob() != 3511
                           && c.getPlayer().getJob() != 3512) {
                           c.getPlayer().dropMessage(5, "You may not use this item.");
                        } else {
                           MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                           MapleInventoryManipulator.addById(c, 1492080, (short)1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Make some space.");
                     }
                     break;
                  case 2430144:
                     int itemid = Randomizer.nextInt(373) + 2290000;
                     if (MapleItemInformationProvider.getInstance().itemExists(itemid)
                        && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special")
                        && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(
                           c, itemid, (short)1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                        );
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                     }
                     break;
                  case 2430149:
                     mountid = 1072;
                     expiration_days = 30L;
                     break;
                  case 2430158:
                     if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 100) {
                           if (MapleInventoryManipulator.checkSpace(c, 4310010, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 4310010, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 50) {
                           if (MapleInventoryManipulator.checkSpace(c, 4310009, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 4310009, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "There needs to be 50 Purification Totems for a Noble Lion King Medal, 100 for Royal Lion King Medal.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                     }
                     break;
                  case 2430159:
                     MapleQuest.getInstance(3182).forceComplete(c.getPlayer(), 2161004);
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                     break;
                  case 2430179:
                     mountid = 80001026;
                     expiration_days = 15L;
                     break;
                  case 2430200:
                     if (c.getPlayer().getQuestStatus(31152) != 2) {
                        c.getPlayer().dropMessage(5, "You have no idea how to use it.");
                     } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000660) >= 1
                           && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000661) >= 1
                           && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000662) >= 1
                           && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000663) >= 1) {
                           if (MapleInventoryManipulator.checkSpace(c, 4032923, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000660, 1, true, false)
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000661, 1, true, false)
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000662, 1, true, false)
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000663, 1, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 4032923, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "There needs to be 1 of each Stone for a Dream Key.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                     }
                     break;
                  case 2430206:
                     mountid = 1033;
                     expiration_days = 7L;
                     break;
                  case 2430211:
                     mountid = 80001009;
                     expiration_days = 30L;
                     break;
                  case 2430212:
                     MapleQuestStatus marrxx = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(122500));
                     if (marrxx.getCustomData() == null) {
                        marrxx.setCustomData("0");
                     }

                     long lastTime = Long.parseLong(marrxx.getCustomData());
                     if (lastTime + 600000L > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                     } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 5);
                     }
                     break;
                  case 2430213:
                     MapleQuestStatus marrx = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(122500));
                     if (marrx.getCustomData() == null) {
                        marrx.setCustomData("0");
                     }

                     lastTime = Long.parseLong(marrx.getCustomData());
                     if (lastTime + 600000L > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                     } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 10);
                     }
                     break;
                  case 2430214:
                  case 2430220:
                     if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 30);
                     }
                     break;
                  case 2430225:
                     mountid = 1031;
                     expiration_days = 10L;
                     break;
                  case 2430227:
                     if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 50);
                     }
                     break;
                  case 2430231:
                     MapleQuestStatus marr = c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(122500));
                     if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                     }

                     lastTime = Long.parseLong(marr.getCustomData());
                     if (lastTime + 600000L > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                     } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 40);
                     }
                     break;
                  case 2430232:
                     mountid = 1106;
                     expiration_days = 10L;
                     break;
                  case 2430242:
                     mountid = 1063;
                     expiration_days = 10L;
                     break;
                  case 2430243:
                     mountid = 1064;
                     expiration_days = 10L;
                     break;
                  case 2430249:
                     mountid = 80001027;
                     expiration_days = 3L;
                     break;
                  case 2430257:
                     mountid = 1029;
                     expiration_days = 7L;
                     break;
                  case 2430258:
                     mountid = 1115;
                     expiration_days = 365L;
                     break;
                  case 2430259:
                     mountid = 1031;
                     expiration_days = 3L;
                     break;
                  case 2430260:
                     mountid = 1044;
                     expiration_days = 3L;
                     break;
                  case 2430261:
                     mountid = 1064;
                     expiration_days = 3L;
                     break;
                  case 2430262:
                     mountid = 1072;
                     expiration_days = 3L;
                     break;
                  case 2430263:
                     mountid = 1050;
                     expiration_days = 3L;
                     break;
                  case 2430264:
                     mountid = 1019;
                     expiration_days = 3L;
                     break;
                  case 2430265:
                     mountid = 1151;
                     expiration_days = 3L;
                     break;
                  case 2430266:
                     mountid = 1054;
                     expiration_days = 3L;
                     break;
                  case 2430271:
                     mountid = 80001191;
                     expiration_days = 3L;
                     break;
                  case 2430272:
                     mountid = 80001032;
                     expiration_days = 3L;
                     break;
                  case 2430275:
                     mountid = 80001033;
                     expiration_days = 7L;
                     break;
                  case 2430283:
                     mountid = 1025;
                     expiration_days = 10L;
                     break;
                  case 2430292:
                     mountid = 1145;
                     expiration_days = 90L;
                     break;
                  case 2430294:
                     mountid = 1146;
                     expiration_days = 90L;
                     break;
                  case 2430296:
                     mountid = 1147;
                     expiration_days = 90L;
                     break;
                  case 2430298:
                     mountid = 1148;
                     expiration_days = 90L;
                     break;
                  case 2430300:
                     mountid = 1149;
                     expiration_days = 90L;
                     break;
                  case 2430302:
                     mountid = 1150;
                     expiration_days = 90L;
                     break;
                  case 2430304:
                     mountid = 1151;
                     expiration_days = 90L;
                     break;
                  case 2430306:
                     mountid = 1152;
                     expiration_days = 90L;
                     break;
                  case 2430308:
                     mountid = 1153;
                     expiration_days = 90L;
                     break;
                  case 2430310:
                     mountid = 1154;
                     expiration_days = 90L;
                     break;
                  case 2430312:
                     mountid = 1156;
                     expiration_days = 90L;
                     break;
                  case 2430313:
                     mountid = 1156;
                     expiration_days = -1L;
                     break;
                  case 2430314:
                     mountid = 1156;
                     expiration_days = 90L;
                     break;
                  case 2430316:
                     mountid = 1118;
                     expiration_days = 90L;
                     break;
                  case 2430317:
                     mountid = 1121;
                     expiration_days = -1L;
                     break;
                  case 2430318:
                     mountid = 1121;
                     expiration_days = 90L;
                     break;
                  case 2430319:
                     mountid = 1122;
                     expiration_days = -1L;
                     break;
                  case 2430320:
                     mountid = 1122;
                     expiration_days = 90L;
                     break;
                  case 2430321:
                     mountid = 1123;
                     expiration_days = -1L;
                     break;
                  case 2430322:
                     mountid = 1123;
                     expiration_days = 90L;
                     break;
                  case 2430323:
                     mountid = 1124;
                     expiration_days = -1L;
                     break;
                  case 2430325:
                     mountid = 1129;
                     expiration_days = -1L;
                     break;
                  case 2430326:
                     mountid = 1129;
                     expiration_days = 90L;
                     break;
                  case 2430327:
                     mountid = 1130;
                     expiration_days = -1L;
                     break;
                  case 2430328:
                     mountid = 1130;
                     expiration_days = 90L;
                     break;
                  case 2430329:
                     mountid = 1063;
                     expiration_days = -1L;
                     break;
                  case 2430330:
                     mountid = 1063;
                     expiration_days = 90L;
                     break;
                  case 2430331:
                     mountid = 1025;
                     expiration_days = -1L;
                     break;
                  case 2430332:
                     mountid = 1025;
                     expiration_days = 90L;
                     break;
                  case 2430333:
                     mountid = 1034;
                     expiration_days = -1L;
                     break;
                  case 2430334:
                     mountid = 1034;
                     expiration_days = 90L;
                     break;
                  case 2430335:
                     mountid = 1136;
                     expiration_days = -1L;
                     break;
                  case 2430336:
                     mountid = 1136;
                     expiration_days = 90L;
                     break;
                  case 2430337:
                     mountid = 1051;
                     expiration_days = -1L;
                     break;
                  case 2430338:
                     mountid = 1051;
                     expiration_days = 90L;
                     break;
                  case 2430339:
                     mountid = 1138;
                     expiration_days = -1L;
                     break;
                  case 2430340:
                     mountid = 1138;
                     expiration_days = 90L;
                     break;
                  case 2430341:
                     mountid = 1139;
                     expiration_days = -1L;
                     break;
                  case 2430342:
                     mountid = 1139;
                     expiration_days = 90L;
                     break;
                  case 2430343:
                     mountid = 1027;
                     expiration_days = -1L;
                     break;
                  case 2430344:
                     mountid = 1027;
                     expiration_days = 90L;
                     break;
                  case 2430346:
                     mountid = 1029;
                     expiration_days = -1L;
                     break;
                  case 2430347:
                     mountid = 1029;
                     expiration_days = 90L;
                     break;
                  case 2430348:
                     mountid = 1028;
                     expiration_days = -1L;
                     break;
                  case 2430349:
                     mountid = 1028;
                     expiration_days = 90L;
                     break;
                  case 2430350:
                     mountid = 1033;
                     expiration_days = -1L;
                     break;
                  case 2430352:
                     mountid = 1064;
                     expiration_days = -1L;
                     break;
                  case 2430354:
                     mountid = 1096;
                     expiration_days = -1L;
                     break;
                  case 2430356:
                     mountid = 1101;
                     expiration_days = -1L;
                     break;
                  case 2430358:
                     mountid = 1102;
                     expiration_days = -1L;
                     break;
                  case 2430360:
                     mountid = 1054;
                     expiration_days = -1L;
                     break;
                  case 2430362:
                     mountid = 1053;
                     expiration_days = -1L;
                     break;
                  case 2430369:
                     mountid = 1049;
                     expiration_days = 10L;
                     break;
                  case 2430370:
                     if (MapleInventoryManipulator.checkSpace(c, 2028062, 1, "")) {
                        MapleInventoryManipulator.addById(
                           c, 2028062, (short)1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                        );
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                     }
                     break;
                  case 2430392:
                     mountid = 80001038;
                     expiration_days = 90L;
                     break;
                  case 2430458:
                     mountid = 80001044;
                     expiration_days = 7L;
                     break;
                  case 2430481:
                     if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 30) {
                           if (MapleInventoryManipulator.checkSpace(c, 2049701, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 30, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 2049701, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 20) {
                           if (MapleInventoryManipulator.checkSpace(c, 2049300, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 2049300, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else {
                           c.getPlayer()
                              .dropMessage(5, "There needs to be 20 Fragments for a Advanced Equip Enhancement Scroll, 30 for Epic Potential Scroll 80%.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                     }
                     break;
                  case 2430506:
                     mountid = 80001082;
                     expiration_days = 30L;
                     break;
                  case 2430507:
                     mountid = 80001083;
                     expiration_days = 30L;
                     break;
                  case 2430508:
                     mountid = 80001175;
                     expiration_days = 30L;
                     break;
                  case 2430518:
                     mountid = 80001090;
                     expiration_days = 30L;
                     break;
                  case 2430521:
                     mountid = 80001044;
                     expiration_days = 30L;
                     break;
                  case 2430691:
                     if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430691) >= 10) {
                           if (MapleInventoryManipulator.checkSpace(c, 5750001, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 5750001, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Nebulite Diffuser.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                     }
                     break;
                  case 2430727:
                     mountid = 80001148;
                     expiration_days = 30L;
                     break;
                  case 2430748:
                     if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430748) >= 20) {
                           if (MapleInventoryManipulator.checkSpace(c, 4420000, 1, "")
                              && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                              MapleInventoryManipulator.addById(
                                 c, 4420000, (short)1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date()
                              );
                           } else {
                              c.getPlayer().dropMessage(5, "Please make some space.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Premium Fusion Ticket.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                     }
                     break;
                  case 2430908:
                     mountid = 80001175;
                     expiration_days = 30L;
                     break;
                  case 2430927:
                     mountid = 80001183;
                     expiration_days = 30L;
                     break;
                  case 2430934:
                     mountid = 1042;
                     expiration_days = 0L;
                     break;
                  case 2430937:
                     mountid = 80001193;
                     expiration_days = 0L;
                     break;
                  case 2430938:
                     mountid = 80001148;
                     expiration_days = 0L;
                     break;
                  case 2430939:
                     mountid = 80001195;
                     expiration_days = 0L;
                     break;
                  case 2431174:
                     int hounerexp = Randomizer.rand(1, 100);
                     chr.gainHonor(hounerexp, true);
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                     break;
                  case 2431835:
                     int rd = Randomizer.nextInt(2);
                     if (rd == 0) {
                        chr.getStat().setHp(chr.getStat().getCurrentMaxHp(), chr);
                        c.getPlayer().updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                     } else {
                        chr.getStat().setMp(chr.getStat().getCurrentMaxMp(chr), chr);
                        c.getPlayer().updateSingleStat(MapleStat.MP, chr.getStat().getMp());
                     }
                     break;
                  case 2431850:
                     chr.getStat().setHp(chr.getStat().getCurrentMaxHp(), chr);
                     c.getPlayer().updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                     chr.getStat().setMp(chr.getStat().getCurrentMaxMp(chr), chr);
                     c.getPlayer().updateSingleStat(MapleStat.MP, chr.getStat().getMp());
                     MapleItemInformationProvider.getInstance().getItemEffect(2002093).applyTo(chr);
                     break;
                  case 2431965:
                  case 2431966:
                  case 2431967:
                  case 2432131:
                  case 2435516:
                  case 2438147:
                  case 2438149:
                  case 2438151:
                  case 2438159:
                  case 2438163:
                  case 2438164:
                  case 2438165:
                  case 2438166:
                  case 2438167:
                  case 2438168:
                  case 2438169:
                  case 2438170:
                  case 2438171:
                  case 2438172:
                  case 2438173:
                  case 2438174:
                  case 2438175:
                  case 2438176:
                  case 2438177:
                  case 2438178:
                  case 2438179:
                  case 2438180:
                  case 2438181:
                  case 2438182:
                  case 2438184:
                  case 2438185:
                  case 2438186:
                  case 2438187:
                  case 2438188:
                  case 2438189:
                  case 2438190:
                  case 2438191:
                  case 2438192:
                  case 2438193:
                  case 2438195:
                  case 2438196:
                  case 2438197:
                  case 2438198:
                  case 2438199:
                  case 2438200:
                  case 2438201:
                  case 2438202:
                  case 2438203:
                  case 2438204:
                  case 2438205:
                  case 2438206:
                  case 2438207:
                  case 2438208:
                  case 2438209:
                  case 2438210:
                  case 2438211:
                  case 2438212:
                  case 2438213:
                  case 2438214:
                  case 2438215:
                  case 2438216:
                  case 2438217:
                  case 2438218:
                  case 2438219:
                  case 2438220:
                  case 2438221:
                  case 2438222:
                  case 2438223:
                  case 2438224:
                  case 2438225:
                  case 2438226:
                  case 2438227:
                  case 2438228:
                  case 2438229:
                  case 2438230:
                  case 2438231:
                  case 2438232:
                  case 2438233:
                  case 2438234:
                  case 2438235:
                  case 2438236:
                  case 2438237:
                  case 2438238:
                  case 2438239:
                  case 2438240:
                  case 2438241:
                  case 2438242:
                  case 2438243:
                  case 2438244:
                  case 2438245:
                  case 2438246:
                  case 2438247:
                  case 2438248:
                  case 2438249:
                  case 2438250:
                  case 2438251:
                  case 2438252:
                  case 2438253:
                  case 2438254:
                  case 2438255:
                  case 2438256:
                  case 2438257:
                  case 2438258:
                  case 2438259:
                  case 2438260:
                  case 2438261:
                  case 2438262:
                  case 2438263:
                  case 2438264:
                  case 2438265:
                  case 2438266:
                  case 2438267:
                  case 2438268:
                  case 2438269:
                  case 2438270:
                  case 2438271:
                  case 2438272:
                  case 2438273:
                  case 2438274:
                  case 2438275:
                  case 2438276:
                  case 2438277:
                  case 2438278:
                  case 2438279:
                  case 2438280:
                  case 2438281:
                  case 2438282:
                  case 2438283:
                  case 2438284:
                  case 2438285:
                  case 2438286:
                  case 2438287:
                  case 2438288:
                  case 2438289:
                  case 2438290:
                  case 2438291:
                  case 2438292:
                  case 2438293:
                  case 2438294:
                  case 2438295:
                  case 2438296:
                  case 2438297:
                  case 2438298:
                  case 2438299:
                  case 2438300:
                  case 2438301:
                  case 2438302:
                  case 2438303:
                  case 2438304:
                  case 2438305:
                  case 2438306:
                  case 2438307:
                  case 2438308:
                  case 2438309:
                  case 2438310:
                  case 2438311:
                  case 2438312:
                  case 2438313:
                  case 2438314:
                  case 2438315:
                  case 2438353:
                  case 2438378:
                  case 2438379:
                  case 2438413:
                  case 2438415:
                  case 2438417:
                  case 2438419:
                  case 2438485:
                  case 2438492:
                  case 2438530:
                  case 2438637:
                  case 2438672:
                  case 2438713:
                  case 2438871:
                  case 2438872:
                  case 2438881:
                  case 2438885:
                  case 2439298:
                  case 2439336:
                  case 2439337:
                  case 2439338:
                  case 2439381:
                  case 2439393:
                  case 2439395:
                  case 2439397:
                  case 2439399:
                  case 2439401:
                  case 2439408:
                  case 2439572:
                  case 2439617:
                  case 2439652:
                  case 2439682:
                  case 2439684:
                  case 2439686:
                  case 2439769:
                  case 2439925:
                  case 2439927:
                  case 2630137:
                  case 2630178:
                  case 2630214:
                  case 2630222:
                  case 2630224:
                  case 2630235:
                  case 2630262:
                  case 2630264:
                  case 2630266:
                  case 2630268:
                  case 2630380:
                  case 2630385:
                  case 2630400:
                  case 2630434:
                  case 2630436:
                  case 2630477:
                  case 2630479:
                  case 2630481:
                  case 2630483:
                  case 2630485:
                  case 2630516:
                  case 2630552:
                  case 2630554:
                  case 2630556:
                  case 2630558:
                  case 2630560:
                  case 2630652:
                  case 2630743:
                  case 2630745:
                  case 2630747:
                  case 2630749:
                  case 2630751:
                  case 2630753:
                  case 2630804:
                  case 2630969:
                  case 2631094:
                  case 2631095:
                  case 2631097:
                  case 2631098:
                  case 2631135:
                  case 2631183:
                  case 2631189:
                  case 2631401:
                  case 2631451:
                  case 2631471:
                  case 2631492:
                  case 2631610:
                  case 2631798:
                  case 2631815:
                  case 2631884:
                  case 2631885:
                  case 2631892:
                  case 2631893:
                  case 2632124:
                  case 2632281:
                  case 2632288:
                  case 2632350:
                  case 2632430:
                  case 2632498:
                  case 2632544:
                  case 2632712:
                  case 2632816:
                  case 2632888:
                  case 2632976:
                  case 2633045:
                  case 2633047:
                  case 2633074:
                  case 2633218:
                  case 2633220:
                  case 2633306:
                  case 2633313:
                  case 2633557:
                  case 2633573:
                  case 2633599:
                  case 2633700:
                  case 2633995:
                  case 2634020:
                  case 2634176:
                  case 2634251:
                  case 2634259:
                  case 2634268:
                  case 2634277:
                  case 2634279:
                  case 2634416:
                  case 2634513:
                  case 2634640:
                  case 2634728:
                  case 2634811:
                  case 2634906:
                  case 2634941:
                  case 2635056:
                  case 2635128:
                  case 2635233:
                  case 2635408:
                  case 2635469:
                  case 2635516:
                  case 2635529:
                  case 2635535:
                  case 2635633:
                  case 2635689:
                  case 2635782:
                  case 2635794:
                  case 2635914:
                  case 2635966:
                  case 2635968:
                  case 2635970:
                  case 2635972:
                  case 2635996:
                  case 2636151:
                  case 5680862:
                  case 5681000:
                  case 5681001:
                     MapleQuest quest = MapleQuest.getInstance(7291);
                     MapleQuestStatus queststatus = new MapleQuestStatus(quest, 1);
                     int damageSkinID = GameConstants.getDamageSkinNumberByItem(itemId);
                     String skinString = String.valueOf(damageSkinID);
                     queststatus.setCustomData(skinString == null ? "0" : skinString);
                     c.getPlayer().updateQuest(queststatus);
                     DamageSkinSaveInfo info = c.getPlayer().getDamageSkinSaveInfo();
                     info.setActiveDamageSkinData(
                        new DamageSkinSaveData(damageSkinID, itemId, info.hasSaveDamageSkin(itemId), DamageSkinSaveInfo.getDefaultDesc())
                     );
                     c.getPlayer().send(CField.damageSkinSaveResult((byte)0, info));
                     chr.setSaveFlag(chr.getSaveFlag() | CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag());
                     chr.dropMessage(5, "데미지 스킨이 변경되었습니다.");
                     chr.getMap().broadcastMessage(chr, CField.showForeignDamageSkin(chr, damageSkinID), false);
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType((byte)(itemId / 1000000)), slot, (short)1, false);
                     break;
                  case 2432290:
                  case 2631501:
                     MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short)1, true, false);
                     MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                     int buffItemId = itemId == 2432290 ? 5121041 : 5121101;
                     String msg = ii.getMsg(buffItemId);
                     c.getPlayer().getMap().startMapEffect(msg, buffItemId);
                     int buff = ii.getStateChangeItem(buffItemId);
                     if (buff != 0) {
                        for (MapleCharacter mChar : c.getPlayer().getMap().getCharactersThreadsafe()) {
                           ii.getItemEffect(buff).applyTo(mChar);
                        }
                     }
                     break;
                  case 2435719:
                  case 2435902:
                  case 2436078:
                     if (c.getPlayer().getQuestStatus(1465) != 2) {
                        c.getPlayer().dropMessage(6, "5차 전직 퀘스트를 완료하지 않아 사용하실 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (c.getPlayer().getVCoreSkillsNoLock().size() >= 500) {
                        c.getPlayer().dropMessage(1, "스킬 코어를 더 이상 소지할 수 없습니다. 코어를 분해하거나 강화하여 공간을 확보해주시기 바랍니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(toUse.getItemId()), toUse.getItemId(), 1, true, false);
                     int jobid = c.getPlayer().getJob();
                     int coreId = VCoreData.getCoreIdByJob(jobid);
                     int[] connectSkill = VCoreData.getMainCoreSkill(coreId);
                     List<Integer> skillList = VCoreData.generateConnectSkillByJob(jobid);
                     if (connectSkill[1] == 0 && coreId >= 20000000 && coreId < 30000000) {
                        for (int ix = 1; ix < 3; ix++) {
                           int skill;
                           boolean exist;
                           do {
                              skill = skillList.get(Randomizer.nextInt(skillList.size()));
                              exist = false;

                              for (int next = 0; next < ix; next++) {
                                 if (skill == connectSkill[next]) {
                                    exist = true;
                                    break;
                                 }
                              }
                           } while (exist);

                           connectSkill[ix] = skill;
                        }
                     }

                     VSpecialCoreOption specialCoreOption = VCoreData.getSpecialCoreOption(coreId);
                     long availableTime = -1L;
                     if (specialCoreOption != null) {
                        availableTime = System.currentTimeMillis() + 1209600000L;
                     }

                     VCore core = new VCore(
                        tickCount,
                        coreId,
                        c.getPlayer().getId(),
                        1,
                        0,
                        1,
                        connectSkill[0],
                        connectSkill[1],
                        connectSkill[2],
                        specialCoreOption,
                        availableTime,
                        -1,
                        false
                     );
                     c.getPlayer().addVCoreSkills(core);
                     c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 13, 0));
                     c.getSession().writeAndFlush(CWvsContext.AddCore(core));
                     c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
                     break;
                  case 2438411:
                  case 2438412:
                     if (c.getPlayer().getQuestStatus(1465) != 2) {
                        c.getPlayer().dropMessage(6, "5차 전직 퀘스트를 완료하지 않아 사용하실 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (c.getPlayer().getVCoreSkillsNoLock().size() >= 500) {
                        c.getPlayer().dropMessage(1, "스킬 코어를 더 이상 소지할 수 없습니다. 코어를 분해하거나 강화하여 공간을 확보해주시기 바랍니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(toUse.getItemId()), toUse.getItemId(), 1, true, false);
                     core = new VCore(tickCount, 10000024, c.getPlayer().getId(), 1, 0, 1, 400001039, 0, 0, null, -1L, -1, false);
                     c.getPlayer().addVCoreSkills(core);
                     c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 13, 0));
                     c.getSession().writeAndFlush(CWvsContext.AddCore(core));
                     c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
                     c.getPlayer().dropMessage(6, "코어 젬스톤을 사용하여 V 코어를 획득했습니다.");
                     break;
                  case 2439279:
                  case 2630402:
                  case 2631527:
                     if (c.getPlayer().getQuestStatus(1465) != 2) {
                        c.getPlayer().dropMessage(6, "5차 전직 퀘스트를 완료하지 않아 사용하실 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (c.getPlayer().getVCoreSkillsNoLock().size() >= 500) {
                        c.getPlayer().dropMessage(1, "스킬 코어를 더 이상 소지할 수 없습니다. 코어를 분해하거나 강화하여 공간을 확보해주시기 바랍니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(toUse.getItemId()), toUse.getItemId(), 1, true, false);
                     core = new VCore(tickCount, 40000000, c.getPlayer().getId(), 1, 0, 1, 400001000, 0, 0, null, -1L, -1, false);
                     c.getPlayer().addVCoreSkills(core);
                     c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 13, 0));
                     c.getSession().writeAndFlush(CWvsContext.AddCore(core));
                     c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
                     c.getPlayer().dropMessage(6, "코어 젬스톤을 사용하여 V 코어를 획득했습니다.");
                     break;
                  case 2632972:
                     if (c.getPlayer().getQuestStatus(1465) != 2) {
                        c.getPlayer().dropMessage(6, "5차 전직 퀘스트를 완료하지 않아 사용하실 수 없습니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (c.getPlayer().getVCoreSkillsNoLock().size() >= 500) {
                        c.getPlayer().dropMessage(1, "스킬 코어를 더 이상 소지할 수 없습니다. 코어를 분해하거나 강화하여 공간을 확보해주시기 바랍니다.");
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(toUse.getItemId()), toUse.getItemId(), 1, true, false);
                     core = new VCore(tickCount, 10000031, c.getPlayer().getId(), 1, 0, 1, 400001059, 0, 0, null, -1L, -1, false);
                     c.getPlayer().addVCoreSkills(core);
                     c.getSession().writeAndFlush(CWvsContext.UpdateCore(c.getPlayer(), true, 13, 0));
                     c.getSession().writeAndFlush(CWvsContext.AddCore(core));
                     c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.V_MATRIX.getFlag());
                     c.getPlayer().dropMessage(6, "코어 젬스톤을 사용하여 V 코어를 획득했습니다.");
                     break;
                  case 3994225:
                     c.getPlayer().dropMessage(5, "Please bring this item to the NPC.");
                     break;
                  case 5680019: {
                     int hair = 32150 + c.getPlayer().getHair() % 10;
                     c.getPlayer().setHair(hair);
                     c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short)1, false);
                     break;
                  }
                  case 5680020: {
                     int hair = 32160 + c.getPlayer().getHair() % 10;
                     c.getPlayer().setHair(hair);
                     c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short)1, false);
                     break;
                  }
                  case 5680157:
                  case 5680159:
                     String Cscript = "mapleRoyal";
                     if (toUse.getItemId() == 5680159) {
                        Cscript = "newMapleRoyal";
                     }

                     if ((toUse.getFlag() & ItemFlag.KARMA_USE.getValue()) != 0) {
                        chr.setKeyValue("tAble", "1");
                     }

                     NPCScriptManager.getInstance().start(c, 9000172, Cscript, false);
                     break;
                  case 5680222:
                     NPCScriptManager.getInstance().start(c, 9000216, "mannequin", true);
                     break;
                  default:
                     MapleItemInformationProvider iix = MapleItemInformationProvider.getInstance();
                     int npcId = iix.getScriptedItemNpc(itemId);
                     String script = iix.getScriptedItemScript(itemId);
                     Item item = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
                     if (item == null || item.getItemId() != itemId || item.getQuantity() < 1 || npcId == 0) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     Pair<Integer, String> data;
                     if (DBConfig.isGanglim && (data = GameConstants.getRidingData(itemId)) != null) {
                        int skillId = data.getLeft() + 80000000;
                        if (c.getPlayer().getSkillLevel(skillId) > 0) {
                           c.getPlayer().dropMessage(6, "[라이딩] 이미 해당 라이딩 스킬을 보유하고 있습니다.");
                        } else {
                           MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short)1, true, false);
                           c.getPlayer().changeSkillLevel(skillId, 1, 1);
                           c.getPlayer().setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.SKILLS.getFlag());
                           c.getPlayer().dropMessage(6, "[라이딩] " + data.getRight() + "을 획득하셨습니다.");
                        }

                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (itemId == 2432636 && !DBConfig.isGanglim) {
                        NPCScriptManager.getInstance().start(c, 2411020);
                        return;
                     }

                     if (chr.isGM()) {
                        chr.dropMessage(6, "스크립트 : " + script + ", 아이템ID : " + itemId);
                        System.out.println("스크립트 : " + script + ", 아이템ID : " + itemId);
                     }

                     if (DBConfig.isGanglim && (itemId == 2633336 || itemId == 2630133 || itemId == 2630782 || itemId == 2437760)) {
                        NPCScriptManager.getInstance().start(c, npcId, "consume_" + itemId, false);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if (ScriptManager.get()._scripts.get(script) != null) {
                        MapleNPC npc = MapleLifeFactory.getNPC(npcId);
                        ScriptManager.runScript(c, script, npc, null, null, itemId);
                     } else if (DBConfig.isGanglim) {
                        NPCScriptManager.getInstance().start(c, npcId, "consume_" + itemId, false);
                     } else {
                        NPCScriptManager.getInstance().start(c, npcId, script.isEmpty() ? null : script, false);
                     }

                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               }
            }

            if (mountid > 0) {
               c.getPlayer().getStat();
               mountid = PlayerStats.getSkillByJob(mountid, c.getPlayer().getJob());
               int fk = GameConstants.getMountItem(mountid, c.getPlayer());
               if (fk > 0 && mountid < 80001000) {
                  for (int ix = 80001001; ix < 80001999; ix++) {
                     Skill skillx = SkillFactory.getSkill(ix);
                     if (skillx != null && GameConstants.getMountItem(skillx.getId(), c.getPlayer()) == fk) {
                        mountid = ix;
                        break;
                     }
                  }
               }

               if (c.getPlayer().getSkillLevel(mountid) > 0) {
                  c.getPlayer().dropMessage(5, "이미 해당 라이딩스킬이 있습니다.");
               } else if (SkillFactory.getSkill(mountid) == null) {
                  c.getPlayer().dropMessage(5, "해당스킬은 얻으실 수 없습니다.");
               } else if (expiration_days > 0L) {
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                  c.getPlayer()
                     .changeSingleSkillLevel(SkillFactory.getSkill(mountid), 1, (byte)1, System.currentTimeMillis() + expiration_days * 24L * 60L * 60L * 1000L);
                  c.getPlayer().dropMessage(-1, "[" + SkillFactory.getSkillName(mountid) + "] 스킬을 얻었습니다.");
               } else if (expiration_days == 0L) {
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
                  c.getPlayer().changeSkillLevel(SkillFactory.getSkill(mountid), 1, 1);
                  c.getPlayer().dropMessage(-1, "[" + SkillFactory.getSkillName(mountid) + "] 스킬을 얻었습니다.");
               }
            }

            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      }
   }

   public static final void UseSummonBag(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr.isAlive() && !chr.inPVP()) {
         slea.readInt();
         short slot = slea.readShort();
         int itemId = slea.readInt();
         Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
         if (toUse != null
            && toUse.getQuantity() >= 1
            && toUse.getItemId() == itemId
            && (c.getPlayer().getMapId() < 910000000 || c.getPlayer().getMapId() > 910000022)) {
            Map<String, Integer> toSpawn = MapleItemInformationProvider.getInstance().getEquipStats(itemId);
            if (toSpawn == null) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            MapleMonster ht = null;
            int type = 0;

            for (Entry<String, Integer> i : toSpawn.entrySet()) {
               if (i.getKey().startsWith("mob") && Randomizer.nextInt(99) <= i.getValue()) {
                  ht = MapleLifeFactory.getMonster(Integer.parseInt(i.getKey().substring(3)));
                  chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
               }
            }

            if (ht == null) {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               return;
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
         }

         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   private static final void HairColorCouponUse(PacketDecoder slea, MapleClient c) {
      int flag = slea.readByteToInt();
      slea.skip(1);
      if (flag == 100) {
         slea.skip(8);
      }

      int select = slea.readInt();
      int[] codies = c.getPlayer().getCodyColors();
      if (codies != null) {
         if (select >= 0 && select <= codies.length) {
            if (flag == 0) {
               int hair = codies[select];
               boolean checkfh = ServerConstants.cacheFaceHair.contains(String.valueOf(hair));
               if (!checkfh) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getPlayer().dropMessage(5, "존재하지 않는 얼굴로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 얼굴을 고객센터로 문의해주세요.");
                  return;
               }

               c.getPlayer().setHair(hair);
               c.getPlayer().setBaseColor(-1);
               c.getPlayer().setAddColor(0);
               c.getPlayer().setBaseProb(0);
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else if (flag == 1) {
               int hair = codies[select];
               boolean checkfh = ServerConstants.cacheFaceHair.contains(String.valueOf(hair));
               if (!checkfh) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getPlayer().dropMessage(5, "존재하지 않는 얼굴로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 얼굴을 고객센터로 문의해주세요.");
                  return;
               }

               c.getPlayer().setSecondHair(hair);
               c.getPlayer().setSecondBaseColor(-1);
               c.getPlayer().setSecondAddColor(0);
               c.getPlayer().setSecondBaseProb(0);
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else if (flag == 2) {
               int hair = codies[select];
               boolean checkfh = ServerConstants.cacheFaceHair.contains(String.valueOf(hair));
               if (!checkfh) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getPlayer().dropMessage(5, "존재하지 않는 얼굴로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 얼굴을 고객센터로 문의해주세요.");
                  return;
               }

               c.getPlayer().getZeroInfo().setSubHair(hair);
               ZeroInfo zeroInfo = c.getPlayer().getZeroInfo();
               if (zeroInfo == null) {
                  return;
               }

               zeroInfo.setMixAddHairColor(hair);
               zeroInfo.setMixBaseHairColor(-1);
               zeroInfo.setMixAddHairColor(0);
               zeroInfo.setMixHairBaseProb(0);
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else if (flag == 100) {
               int hairx = codies[select];
               boolean checkfhx = ServerConstants.cacheFaceHair.contains(String.valueOf(hairx));
               if (!checkfhx) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getPlayer().dropMessage(5, "존재하지 않는 얼굴로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 얼굴을 고객센터로 문의해주세요.");
                  return;
               }

               Android android = c.getPlayer().getAndroid();
               if (android == null) {
                  return;
               }

               android.setHair(hairx);
               c.getPlayer().updateAndroid();
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else {
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            }
         }
      }
   }

   private static final void SkinColorCouponUse(PacketDecoder slea, MapleClient c) {
      int flag = slea.readByteToInt();
      slea.skip(1);
      if (flag == 100) {
         slea.skip(8);
      }

      int select = slea.readInt();
      int[] codies = c.getPlayer().getCodyColors();
      if (codies != null) {
         if (select < 0 || select > codies.length) {
            boolean isSkin = false;

            for (int cody : codies) {
               if (cody == select) {
                  isSkin = true;
               }
            }

            if (!isSkin) {
               return;
            }
         }

         if (flag == 0) {
            c.getPlayer().setSkinColor(select);
            c.getPlayer().setCodyColors(null);
            c.getPlayer().fakeRelog();
         } else if (flag == 1) {
            c.getPlayer().setSecondSkinColor((byte)select);
            c.getPlayer().setCodyColors(null);
            c.getPlayer().fakeRelog();
         } else if (flag == 2) {
            ZeroInfo zeroInfo = c.getPlayer().getZeroInfo();
            if (zeroInfo == null) {
               return;
            }

            zeroInfo.setSubSkin(select);
            c.getPlayer().setCodyColors(null);
            c.getPlayer().fakeRelog();
         } else if (flag == 100) {
            Android android = c.getPlayer().getAndroid();
            if (android == null) {
               return;
            }

            android.setSkin(select);
            c.getPlayer().updateAndroid();
            c.getPlayer().setCodyColors(null);
            c.getPlayer().fakeRelog();
         } else {
            c.getPlayer().setCodyColors(null);
            c.getPlayer().fakeRelog();
         }
      }
   }

   private static final void LensColorCouponUse(PacketDecoder slea, MapleClient c) {
      int flag = slea.readByteToInt();
      slea.skip(1);
      if (flag == 100) {
         slea.skip(8);
      }

      int select = slea.readInt();
      int[] codies = c.getPlayer().getCodyColors();
      if (codies != null) {
         if (select >= 0 && select <= codies.length) {
            if (flag == 0) {
               int face = codies[select];
               boolean checkfh = ServerConstants.cacheFaceHair.contains(String.valueOf(face));
               if (!checkfh) {
                  c.getPlayer().dropMessage(5, "존재하지 않는 헤어로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 헤어를 고객센터로 문의해주세요.");
                  c.getPlayer().fakeRelog();
                  return;
               }

               c.getPlayer().setFace(face);
               c.getPlayer().setFaceBaseColor(-1);
               c.getPlayer().setFaceAddColor(0);
               c.getPlayer().setFaceBaseProb(0);
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else if (flag == 1) {
               int face = codies[select];
               boolean checkfh = ServerConstants.cacheFaceHair.contains(String.valueOf(face));
               if (!checkfh) {
                  c.getPlayer().dropMessage(5, "존재하지 않는 헤어로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 헤어를 고객센터로 문의해주세요.");
                  c.getPlayer().fakeRelog();
                  return;
               }

               c.getPlayer().setSecondFace(face);
               c.getPlayer().setSecondFaceBaseColor(-1);
               c.getPlayer().setSecondFaceAddColor(0);
               c.getPlayer().setSecondFaceBaseProb(0);
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else if (flag == 2) {
               int face = codies[select];
               boolean checkfh = ServerConstants.cacheFaceHair.contains(String.valueOf(face));
               if (!checkfh) {
                  c.getPlayer().dropMessage(5, "존재하지 않는 헤어로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 헤어를 고객센터로 문의해주세요.");
                  c.getPlayer().fakeRelog();
                  return;
               }

               ZeroInfo zeroInfo = c.getPlayer().getZeroInfo();
               if (zeroInfo == null) {
                  return;
               }

               zeroInfo.setSubFace(face);
               zeroInfo.setMixBaseHairColor(-1);
               zeroInfo.setMixAddHairColor(0);
               zeroInfo.setMixHairBaseProb(0);
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else if (flag == 100) {
               int facex = codies[select];
               boolean checkfhx = ServerConstants.cacheFaceHair.contains(String.valueOf(facex));
               if (!checkfhx) {
                  c.getPlayer().dropMessage(5, "존재하지 않는 헤어로 변경에 실패하였습니다. 동일한 오류가 발생하는 경우 해당 헤어를 고객센터로 문의해주세요.");
                  c.getPlayer().fakeRelog();
                  return;
               }

               Android android = c.getPlayer().getAndroid();
               if (android == null) {
                  return;
               }

               android.setFace(facex);
               c.getPlayer().updateAndroid();
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            } else {
               c.getPlayer().setCodyColors(null);
               c.getPlayer().fakeRelog();
            }
         }
      }
   }

   public static final void UseCashItem(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer() != null && c.getPlayer().getMap() != null && !c.getPlayer().inPVP()) {
         slea.readInt();
         c.getPlayer().setScrolledPosition((short)0);
         short slot = slea.readShort();
         int itemId = slea.readInt();
         if (itemId == 5151036) {
            HairColorCouponUse(slea, c);
         } else if (itemId == 5153015) {
            SkinColorCouponUse(slea, c);
         } else if (itemId == 5152111) {
            LensColorCouponUse(slea, c);
         } else {
            Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
            if (toUse != null && toUse.getItemId() == itemId && toUse.getQuantity() >= 1) {
               boolean used = false;
               boolean cc = false;
               long time = System.currentTimeMillis();
               if (time - c.getPlayer().lastItemUsedTime <= 300L) {
                  c.getPlayer().dropMessage(5, "지금은 사용자가 많아 아이템을 사용할 수 없습니다. 잠시 후 다시 시도해 주세요.");
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               } else {
                  c.getPlayer().lastItemUsedTime = time;
                  if ((itemId / 10000 == 507 || itemId / 10000 == 512 || itemId / 10000 == 537) && c.isAccountChatBan()) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  } else {
                     switch (itemId) {
                        case 2320000:
                        case 5040000:
                        case 5040001:
                        case 5040002:
                        case 5040003:
                        case 5040004:
                        case 5041000:
                        case 5041001:
                           used = UseTeleRock(slea, c, itemId);
                        case 5043000:
                        case 5043001:
                           break;
                        case 5044000:
                        case 5044001:
                        case 5044002:
                        case 5044006:
                        case 5044007:
                        case 5044008:
                        case 5044012:
                        case 5044013:
                        case 5044014:
                        case 5044015:
                        case 5044018:
                        case 5044019:
                        case 5044020:
                           slea.readByte();
                           int mapId = slea.readInt();
                           if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                              && !c.getPlayer().isInBlockedMap()
                              && c.getPlayer().getMap().getLevelLimit() <= c.getPlayer().getLevel()) {
                              c.getPlayer().changeMap(mapId);
                           } else {
                              c.getPlayer().send(CWvsContext.serverNotice(0, "레벨이 낮거나 이동이 불가능한 맵입니다."));
                              c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                           }

                           used = false;
                           break;
                        case 5050000:
                           Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
                           int apto = slea.readInt();
                           int apfrom = slea.readInt();
                           if (apto != apfrom) {
                              int job = c.getPlayer().getJob();
                              PlayerStats playerst = c.getPlayer().getStat();
                              used = true;
                              switch (apto) {
                                 case 64:
                                    if (playerst.getStr() >= 999) {
                                       used = false;
                                    }
                                    break;
                                 case 128:
                                    if (playerst.getDex() >= 999) {
                                       used = false;
                                    }
                                    break;
                                 case 256:
                                    if (playerst.getInt() >= 999) {
                                       used = false;
                                    }
                                    break;
                                 case 512:
                                    if (playerst.getLuk() >= 999) {
                                       used = false;
                                    }
                                    break;
                                 case 2048:
                                    if (playerst.getCurrentMaxHp(c.getPlayer()) >= 500000L) {
                                       used = false;
                                    }
                                    break;
                                 case 8192:
                                    if (playerst.getMaxMp() >= 500000L) {
                                       used = false;
                                    }
                              }

                              switch (apfrom) {
                                 case 64:
                                    if (playerst.getStr() <= 4 || c.getPlayer().getJob() % 1000 / 100 == 1 && playerst.getStr() <= 35) {
                                       used = false;
                                    }
                                    break;
                                 case 128:
                                    if (playerst.getDex() <= 4
                                       || c.getPlayer().getJob() % 1000 / 100 == 3 && playerst.getDex() <= 25
                                       || c.getPlayer().getJob() % 1000 / 100 == 4 && playerst.getDex() <= 25
                                       || c.getPlayer().getJob() % 1000 / 100 == 5 && playerst.getDex() <= 20) {
                                       used = false;
                                    }
                                    break;
                                 case 256:
                                    if (playerst.getInt() <= 4 || c.getPlayer().getJob() % 1000 / 100 == 2 && playerst.getInt() <= 20) {
                                       used = false;
                                    }
                                    break;
                                 case 512:
                                    if (playerst.getLuk() <= 4) {
                                       used = false;
                                    }
                                    break;
                                 case 2048:
                                    if (c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                                       used = false;
                                       c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                                    }
                                    break;
                                 case 8192:
                                    if (c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                                       used = false;
                                       c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                                    }
                              }

                              if (used) {
                                 switch (apto) {
                                    case 64: {
                                       int toSet = playerst.getStr() + 1;
                                       playerst.setStr((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.STR, (long)toSet);
                                       break;
                                    }
                                    case 128: {
                                       int toSet = playerst.getDex() + 1;
                                       playerst.setDex((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.DEX, (long)toSet);
                                       break;
                                    }
                                    case 256: {
                                       int toSet = playerst.getInt() + 1;
                                       playerst.setInt((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.INT, (long)toSet);
                                       break;
                                    }
                                    case 512: {
                                       int toSet = playerst.getLuk() + 1;
                                       playerst.setLuk((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.LUK, (long)toSet);
                                       break;
                                    }
                                    case 2048:
                                       long maxhp = playerst.getMaxHp();
                                       if (GameConstants.isNovice(job)) {
                                          maxhp += Randomizer.rand(4, 8);
                                       } else if ((job < 100 || job > 132)
                                          && (job < 3200 || job > 3212)
                                          && (job < 1100 || job > 1112)
                                          && (job < 3100 || job > 3112)) {
                                          if ((job < 200 || job > 232) && !GameConstants.isEvan(job) && (job < 1200 || job > 1212)) {
                                             if ((job < 300 || job > 322)
                                                && (job < 400 || job > 434)
                                                && (job < 1300 || job > 1312)
                                                && (job < 1400 || job > 1412)
                                                && (job < 3300 || job > 3312)
                                                && (job < 2300 || job > 2312)) {
                                                if ((job < 510 || job > 512) && (job < 1510 || job > 1512)) {
                                                   if ((job < 500 || job > 532) && (job < 3500 || job > 3512) && job != 1500) {
                                                      if (job >= 2000 && job <= 2112) {
                                                         maxhp += Randomizer.rand(34, 38);
                                                      } else {
                                                         maxhp += Randomizer.rand(50, 100);
                                                      }
                                                   } else {
                                                      maxhp += Randomizer.rand(16, 20);
                                                   }
                                                } else {
                                                   maxhp += Randomizer.rand(24, 28);
                                                }
                                             } else {
                                                maxhp += Randomizer.rand(14, 18);
                                             }
                                          } else {
                                             maxhp += Randomizer.rand(10, 12);
                                          }
                                       } else {
                                          maxhp += Randomizer.rand(36, 42);
                                       }

                                       maxhp = Math.min(500000L, Math.abs(maxhp));
                                       c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() + 1));
                                       playerst.setMaxHp(maxhp, c.getPlayer());
                                       statupdate.put(MapleStat.MAXHP, maxhp);
                                       break;
                                    case 8192:
                                       label2190: {
                                          long maxmp = playerst.getMaxMp();
                                          if (GameConstants.isNovice(job)) {
                                             maxmp += Randomizer.rand(6, 8);
                                          } else {
                                             if (job >= 3100 && job <= 3112) {
                                                break label2190;
                                             }

                                             if ((job < 100 || job > 132) && (job < 1100 || job > 1112) && (job < 2000 || job > 2112)) {
                                                if ((job < 200 || job > 232)
                                                   && !GameConstants.isEvan(job)
                                                   && (job < 3200 || job > 3212)
                                                   && (job < 1200 || job > 1212)) {
                                                   if ((job < 300 || job > 322)
                                                      && (job < 400 || job > 434)
                                                      && (job < 500 || job > 532)
                                                      && (job < 3200 || job > 3212)
                                                      && (job < 3500 || job > 3512)
                                                      && (job < 1300 || job > 1312)
                                                      && (job < 1400 || job > 1412)
                                                      && (job < 1500 || job > 1512)
                                                      && (job < 2300 || job > 2312)) {
                                                      maxmp += Randomizer.rand(50, 100);
                                                   } else {
                                                      maxmp += Randomizer.rand(8, 10);
                                                   }
                                                } else {
                                                   maxmp += Randomizer.rand(32, 36);
                                                }
                                             } else {
                                                maxmp += Randomizer.rand(4, 9);
                                             }
                                          }

                                          maxmp = Math.min(500000L, Math.abs(maxmp));
                                          c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() + 1));
                                          playerst.setMaxMp(maxmp, c.getPlayer());
                                          statupdate.put(MapleStat.MAXMP, maxmp);
                                       }
                                 }

                                 switch (apfrom) {
                                    case 64: {
                                       int toSet = playerst.getStr() - 1;
                                       playerst.setStr((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.STR, (long)toSet);
                                       break;
                                    }
                                    case 128: {
                                       int toSet = playerst.getDex() - 1;
                                       playerst.setDex((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.DEX, (long)toSet);
                                       break;
                                    }
                                    case 256: {
                                       int toSet = playerst.getInt() - 1;
                                       playerst.setInt((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.INT, (long)toSet);
                                       break;
                                    }
                                    case 512: {
                                       int toSet = playerst.getLuk() - 1;
                                       playerst.setLuk((short)toSet, c.getPlayer());
                                       statupdate.put(MapleStat.LUK, (long)toSet);
                                       break;
                                    }
                                    case 2048:
                                       long maxhp = playerst.getMaxHp();
                                       if (GameConstants.isNovice(job)) {
                                          maxhp -= 12L;
                                       } else if ((job < 200 || job > 232) && (job < 1200 || job > 1212)) {
                                          if ((job < 300 || job > 322)
                                             && (job < 400 || job > 434)
                                             && (job < 1300 || job > 1312)
                                             && (job < 1400 || job > 1412)
                                             && (job < 3300 || job > 3312)
                                             && (job < 3500 || job > 3512)
                                             && (job < 2300 || job > 2312)) {
                                             if ((job < 500 || job > 532) && (job < 1500 || job > 1512)) {
                                                if ((job < 100 || job > 132) && (job < 1100 || job > 1112) && (job < 3100 || job > 3112)) {
                                                   if ((job < 2000 || job > 2112) && (job < 3200 || job > 3212)) {
                                                      maxhp -= 20L;
                                                   } else {
                                                      maxhp -= 40L;
                                                   }
                                                } else {
                                                   maxhp -= 32L;
                                                }
                                             } else {
                                                maxhp -= 22L;
                                             }
                                          } else {
                                             maxhp -= 15L;
                                          }
                                       } else {
                                          maxhp -= 10L;
                                       }

                                       c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() - 1));
                                       playerst.setMaxHp(maxhp, c.getPlayer());
                                       statupdate.put(MapleStat.MAXHP, maxhp);
                                       break;
                                    case 8192:
                                       label2058: {
                                          long maxmp = playerst.getMaxMp();
                                          if (GameConstants.isNovice(job)) {
                                             maxmp -= 8L;
                                          } else {
                                             if (job >= 3100 && job <= 3112) {
                                                break label2058;
                                             }

                                             if ((job < 100 || job > 132) && (job < 1100 || job > 1112)) {
                                                if ((job < 200 || job > 232) && (job < 1200 || job > 1212)) {
                                                   if ((job < 500 || job > 532)
                                                      && (job < 300 || job > 322)
                                                      && (job < 400 || job > 434)
                                                      && (job < 1300 || job > 1312)
                                                      && (job < 1400 || job > 1412)
                                                      && (job < 1500 || job > 1512)
                                                      && (job < 3300 || job > 3312)
                                                      && (job < 3500 || job > 3512)
                                                      && (job < 2300 || job > 2312)) {
                                                      if (job >= 2000 && job <= 2112) {
                                                         maxmp -= 5L;
                                                      } else {
                                                         maxmp -= 20L;
                                                      }
                                                   } else {
                                                      maxmp -= 10L;
                                                   }
                                                } else {
                                                   maxmp -= 30L;
                                                }
                                             } else {
                                                maxmp -= 4L;
                                             }
                                          }

                                          c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() - 1));
                                          playerst.setMaxMp(maxmp, c.getPlayer());
                                          statupdate.put(MapleStat.MAXMP, maxmp);
                                       }
                                 }

                                 c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
                              }
                           }
                           break;
                        case 5050001:
                        case 5050002:
                        case 5050003:
                        case 5050004:
                        case 5050005:
                        case 5050006:
                        case 5050007:
                        case 5050008:
                        case 5050009:
                           if (itemId >= 5050005 && !GameConstants.isEvan(c.getPlayer().getJob())) {
                              c.getPlayer().dropMessage(1, "This reset is only for Evans.");
                           } else if (itemId < 5050005 && GameConstants.isEvan(c.getPlayer().getJob())) {
                              c.getPlayer().dropMessage(1, "This reset is only for non-Evans.");
                           } else {
                              int skill1 = slea.readInt();
                              int skill2 = slea.readInt();

                              for (int ixxxxxx : GameConstants.blockedSkills) {
                                 if (skill1 == ixxxxxx) {
                                    c.getPlayer().dropMessage(1, "You may not add this skill.");
                                    return;
                                 }
                              }

                              Skill skillSPTo = SkillFactory.getSkill(skill1);
                              Skill skillSPFrom = SkillFactory.getSkill(skill2);
                              if (!skillSPTo.isBeginnerSkill() && !skillSPFrom.isBeginnerSkill()) {
                                 if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) {
                                    c.getPlayer().dropMessage(1, "You may not add different job skills.");
                                 } else if (c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel()
                                    && c.getPlayer().getSkillLevel(skillSPFrom) > 0
                                    && skillSPTo.canBeLearnedBy(c.getPlayer().getJob())) {
                                    if (skillSPTo.isFourthJob() && c.getPlayer().getSkillLevel(skillSPTo) + 1 > c.getPlayer().getMasterLevel(skillSPTo)) {
                                       c.getPlayer().dropMessage(1, "You will exceed the master level.");
                                    } else {
                                       if (itemId >= 5050005) {
                                          if (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2
                                             && GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1) {
                                             c.getPlayer().dropMessage(1, "You may not add this job SP using this reset.");
                                             break;
                                          }
                                       } else {
                                          int theJob = GameConstants.getJobNumber(skill2);
                                          switch (skill2 / 10000) {
                                             case 430:
                                                theJob = 1;
                                                break;
                                             case 431:
                                             case 432:
                                                theJob = 2;
                                                break;
                                             case 433:
                                                theJob = 3;
                                                break;
                                             case 434:
                                                theJob = 4;
                                          }

                                          if (theJob != itemId - 5050000) {
                                             c.getPlayer().dropMessage(1, "You may not subtract from this skill. Use the appropriate SP reset.");
                                             break;
                                          }
                                       }

                                       Map<Skill, SkillEntry> sa = new HashMap<>();
                                       sa.put(
                                          skillSPFrom,
                                          new SkillEntry(
                                             (byte)(c.getPlayer().getSkillLevel(skillSPFrom) - 1),
                                             c.getPlayer().getMasterLevel(skillSPFrom),
                                             SkillFactory.getDefaultSExpiry(skillSPFrom)
                                          )
                                       );
                                       sa.put(
                                          skillSPTo,
                                          new SkillEntry(
                                             (byte)(c.getPlayer().getSkillLevel(skillSPTo) + 1),
                                             c.getPlayer().getMasterLevel(skillSPTo),
                                             SkillFactory.getDefaultSExpiry(skillSPTo)
                                          )
                                       );
                                       c.getPlayer().changeSkillsLevel(sa);
                                       used = true;
                                    }
                                 }
                              } else {
                                 c.getPlayer().dropMessage(1, "You may not add beginner skills.");
                              }
                           }
                           break;
                        case 5050100:
                           PlayerStats stats = c.getPlayer().getStat();
                           int total = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt() + c.getPlayer().getRemainingAp();
                           if (total > 32767) {
                              c.getPlayer().dropMessage(1, "스탯이 과도하게 높아 AP 초기화 주문서를 사용하실 수 없습니다.");
                           } else {
                              c.getPlayer().resetStats(4, 4, 4, 4);
                              used = true;
                           }
                           break;
                        case 5051001:
                           for (Entry<Skill, SkillEntry> entry : c.getPlayer().getSkills().entrySet()) {
                              Skill skill = entry.getKey();
                              int skillid = skill.getId();
                              int skillbase = skillid / 10000;
                              if (skillid != 2300009 && !skill.isInvisible() && GameConstants.isJobCode(skillbase) && !GameConstants.isNovice(skillbase)) {
                                 c.getPlayer().changeSingleSkillLevel(skill, 0, (byte)skill.getMasterLevel());
                                 int skillbook = GameConstants.getSkillBookForSkill(skillid);
                                 c.getPlayer().gainSP(skill.getMaxLevel(), skillbook);
                                 if (c.getPlayer().isGM()) {
                                    c.getPlayer().dropMessage(5, skillbook + " 차 스킬 : " + skillid + " / " + skill.getMaxLevel() + " 증가");
                                 }
                              }
                           }
                           break;
                        case 5060000:
                           Item itemxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           if (itemxxxxxxxxxxxxxxxxxx != null && itemxxxxxxxxxxxxxxxxxx.getOwner().equals("")) {
                              boolean change = true;

                              for (String zxxxxx : GameConstants.RESERVED) {
                                 if (c.getPlayer().getName().indexOf(zxxxxx) != -1) {
                                    change = false;
                                 }
                              }

                              if (change) {
                                 String owner = itemxxxxxxxxxxxxxxxxxx.getOwner();

                                 for (int i = 1; i <= 9; i++) {
                                    if (owner != null && !owner.isEmpty() && owner.equals(i + "성")) {
                                       change = false;
                                       break;
                                    }
                                 }

                                 if (change) {
                                    itemxxxxxxxxxxxxxxxxxx.setOwner(c.getPlayer().getName());
                                    c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                    used = true;
                                 }
                              }
                           }
                           break;
                        case 5060001:
                           MapleInventoryType typexxxxxxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           Item itemxxxxxxxxx = c.getPlayer().getInventory(typexxxxxxxxx).getItem((short)slea.readInt());
                           if (itemxxxxxxxxx != null && itemxxxxxxxxx.getExpiration() == -1L) {
                              int flagxxx = itemxxxxxxxxx.getFlag();
                              flagxxx |= ItemFlag.PROTECTED.getValue();
                              itemxxxxxxxxx.setFlag(flagxxx);
                              c.getPlayer().forceReAddItem_Flag(itemxxxxxxxxx, typexxxxxxxxx);
                              used = true;
                           }
                           break;
                        case 5060002:
                        case 5060003:
                        case 5060004:
                        case 5060005:
                        case 5060012:
                        case 5060018:
                           ScriptManager.runScript(c, "Royal_Peanut", MapleLifeFactory.getNPC(9010000), null, null, itemId);
                           break;
                        case 5060010:
                           Item itemxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           if (itemxxxxxxxxxxxxxxxxxxx != null && !itemxxxxxxxxxxxxxxxxxxx.getOwner().isEmpty()) {
                              boolean change = true;
                              String owner = itemxxxxxxxxxxxxxxxxxxx.getOwner();

                              for (int ix = 1; ix <= 9; ix++) {
                                 if (owner != null && !owner.isEmpty() && owner.equals(ix + "성")) {
                                    change = false;
                                    break;
                                 }
                              }

                              if (change) {
                                 itemxxxxxxxxxxxxxxxxxxx.setOwner("");
                                 c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                 used = true;
                              }
                           }
                           break;
                        case 5060028: {
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           byte inventory2 = (byte)slea.readInt();
                           byte slot2 = (byte)slea.readInt();
                           slea.readInt();
                           int[] coin = new int[]{
                              2049300,
                              4001785,
                              4001784,
                              4310008,
                              4310066,
                              4310015,
                              4310066,
                              4310015,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4310008,
                              4310066,
                              4310015,
                              4310066,
                              4310015,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4310008,
                              4310066,
                              4310015,
                              4310066,
                              4310015,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4310008,
                              4310066,
                              4310015,
                              4310066,
                              4310015,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126,
                              4001126
                           };
                           int coin1 = coin[Randomizer.rand(0, coin.length - 1)];
                           short quantity = (short)Randomizer.rand(1, 5);
                           Item item2 = c.getPlayer().getInventory(MapleInventoryType.getByType(inventory2)).getItem(slot2);
                           if (item2 != null) {
                              if (c.getPlayer().getPandoraBoxFever() == 0 || c.getPlayer().getPandoraBoxFever() == 90) {
                                 c.getPlayer().addPandoraBoxFever((byte)10);
                              } else if (c.getPlayer().getPandoraBoxFever() == 10) {
                                 c.getPlayer().addPandoraBoxFever((byte)8);
                              } else if (c.getPlayer().getPandoraBoxFever() == 100) {
                                 MapleInventoryManipulator.addById(c, coin1, quantity, "Reward item: " + coin1 + " on " + FileoutputUtil.CurrentReadable_Date());
                                 c.getPlayer().setPandoraBoxFever((byte)10);
                              } else {
                                 c.getPlayer().addPandoraBoxFever((byte)9);
                              }

                              String text = null;
                              switch (c.getPlayer().getPandoraBoxFever()) {
                                 case 10:
                                    text = "03 67 1A 00 73 75 6D 3D 31 30 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 31 30";
                                    break;
                                 case 18:
                                    text = "03 67 1A 00 73 75 6D 3D 31 38 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 31 38";
                                    break;
                                 case 27:
                                    text = "03 67 1A 00 73 75 6D 3D 32 37 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 32 37";
                                    break;
                                 case 36:
                                    text = "03 67 1A 00 73 75 6D 3D 33 36 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 33 36";
                                    break;
                                 case 45:
                                    text = "03 67 1A 00 73 75 6D 3D 34 35 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 34 35";
                                    break;
                                 case 54:
                                    text = "03 67 1A 00 73 75 6D 3D 35 34 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 35 34";
                                    break;
                                 case 63:
                                    text = "03 67 1A 00 73 75 6D 3D 36 33 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 36 33";
                                    break;
                                 case 72:
                                    text = "03 67 1A 00 73 75 6D 3D 37 32 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 37 32";
                                    break;
                                 case 81:
                                    text = "03 67 1A 00 73 75 6D 3D 38 31 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 38 31";
                                    break;
                                 case 90:
                                    text = "03 67 1A 00 73 75 6D 3D 39 30 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 39 30";
                                    break;
                                 case 100:
                                    text = "03 67 1C 00 73 75 6D 3D 31 30 38 3B 69 64 3D 35 30 36 30 30 32 38 3B 67 61 75 67 65 3D 31 30 30";
                              }

                              c.getSession().writeAndFlush(CWvsContext.InfoPacket.updatePandoraBox(text));
                              NPCScriptManager.getInstance().start(c, 9000159, "PandoraBox", false);
                           }

                           used = true;
                           break;
                        }
                        case 5060048: {
                           objects.item.rewards.RandomRewards rewardx = RoyalStyle.getRandomItem(RoyalStyle.goldApple);
                           if (rewardx == null) {
                              return;
                           }

                           if (c.getPlayer().getInventory(MapleInventoryType.CASH).isFull(2) || c.getPlayer().getInventory(MapleInventoryType.USE).isFull(2)) {
                              c.getPlayer().dropMessage(1, "캐시, 소비창을 2칸 이상 비워주시기 바랍니다.");
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item itemxxxxxxxxxxxxxxxxxxxxxxxxxx = new Item(rewardx.getMaleItemID(), (short)1, (short)rewardx.getItemQuantity(), 0);
                           if (itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId() / 1000000 == 1) {
                              itemxxxxxxxxxxxxxxxxxxxxxxxxxx = ii.getEquipById(rewardx.getMaleItemID());
                           }

                           Item item2 = new Item(2435458, (short)1, (short)1, 0);
                           if (ii.isCash(itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId())) {
                              itemxxxxxxxxxxxxxxxxxxxxxxxxxx.setUniqueId(MapleInventoryIdentifier.getInstance());
                           }

                           if (GameConstants.isFairyPendant(itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId())) {
                              itemxxxxxxxxxxxxxxxxxxxxxxxxxx.setUniqueId(MapleInventoryIdentifier.getInstance());
                           }

                           if (rewardx.isAnnounce()) {
                              Center.Broadcast.broadcastGachaponMessage(
                                 c.getPlayer().getName()
                                    + "님이 "
                                    + ii.getName(5060048)
                                    + "에서 {"
                                    + ii.getName(itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId())
                                    + "} 아이템을 획득하였습니다",
                                 5060048,
                                 itemxxxxxxxxxxxxxxxxxxxxxxxxxx
                              );
                           }

                           StringBuilder sbx = new StringBuilder(
                              "골드애플 사용 (출현 아이템 : "
                                 + ii.getName(itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId())
                                 + "("
                                 + itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId()
                                 + "), 사용자 : "
                                 + c.getPlayer().getName()
                                 + ")"
                           );
                           LoggingManager.putLog(new ConsumeLog(c.getPlayer(), 5060048, sbx));
                           c.getPlayer()
                              .gainItem(
                                 itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId(), itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getQuantity(), false, -1L, "골드 애플을 통해 획득한 아이템"
                              );
                           c.getPlayer().gainItem(item2.getItemId(), item2.getQuantity(), false, -1L, "골드 애플을 통해 획득한 아이템");
                           c.getSession()
                              .writeAndFlush(
                                 CWvsContext.getIncubatorResult(
                                    item2.getItemId(),
                                    item2.getQuantity(),
                                    itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getItemId(),
                                    itemxxxxxxxxxxxxxxxxxxxxxxxxxx.getQuantity(),
                                    5060048
                                 )
                              );
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           used = true;
                           break;
                        }
                        case 5061000:
                           MapleInventoryType typexxxxxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           Item itemxxxxxxxx = c.getPlayer().getInventory(typexxxxxxxx).getItem((short)slea.readInt());
                           if (itemxxxxxxxx != null && itemxxxxxxxx.getExpiration() == -1L) {
                              int flagxxx = itemxxxxxxxx.getFlag();
                              flagxxx |= ItemFlag.PROTECTED.getValue();
                              itemxxxxxxxx.setFlag(flagxxx);
                              itemxxxxxxxx.setExpiration(System.currentTimeMillis() + 604800000L);
                              c.getPlayer().forceReAddItem_Flag(itemxxxxxxxx, typexxxxxxxx);
                              used = true;
                           }
                           break;
                        case 5061001:
                           MapleInventoryType typexxxxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           Item itemxxxxxxx = c.getPlayer().getInventory(typexxxxxxx).getItem((short)slea.readInt());
                           if (itemxxxxxxx != null && itemxxxxxxx.getExpiration() == -1L) {
                              int flagxxx = itemxxxxxxx.getFlag();
                              flagxxx |= ItemFlag.PROTECTED.getValue();
                              itemxxxxxxx.setFlag(flagxxx);
                              itemxxxxxxx.setExpiration(System.currentTimeMillis() + -1702967296L);
                              c.getPlayer().forceReAddItem_Flag(itemxxxxxxx, typexxxxxxx);
                              used = true;
                           }
                           break;
                        case 5061002:
                           MapleInventoryType typexxxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           Item itemxxxxxx = c.getPlayer().getInventory(typexxxxxx).getItem((short)slea.readInt());
                           if (itemxxxxxx != null && itemxxxxxx.getExpiration() == -1L) {
                              int flagxxx = itemxxxxxx.getFlag();
                              flagxxx |= ItemFlag.PROTECTED.getValue();
                              itemxxxxxx.setFlag(flagxxx);
                              itemxxxxxx.setExpiration(System.currentTimeMillis() + -813934592L);
                              c.getPlayer().forceReAddItem_Flag(itemxxxxxx, typexxxxxx);
                              used = true;
                           }
                           break;
                        case 5061003:
                           MapleInventoryType typexxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           Item itemxxxxx = c.getPlayer().getInventory(typexxxxx).getItem((short)slea.readInt());
                           if (itemxxxxx != null && itemxxxxx.getExpiration() == -1L) {
                              int flagxxx = itemxxxxx.getFlag();
                              flagxxx |= ItemFlag.PROTECTED.getValue();
                              itemxxxxx.setFlag(flagxxx);
                              itemxxxxx.setExpiration(System.currentTimeMillis() + 1471228928L);
                              c.getPlayer().forceReAddItem_Flag(itemxxxxx, typexxxxx);
                              used = true;
                           }
                           break;
                        case 5062005:
                           if (!DBConfig.isGanglim) {
                              return;
                           }

                           int posxxxx = slea.readInt();
                           Item itemxxxxxxxxxxx = c.getPlayer()
                              .getInventory(posxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP)
                              .getItem((short)posxxxx);
                           if (itemxxxxxxxxxxx != null) {
                              Equip eq = (Equip)itemxxxxxxxxxxx;
                              long price = GameConstants.getItemReleaseCost(eq.getItemId());
                              if (price > c.getPlayer().getMeso()) {
                                 c.getPlayer().dropMessage(5, "메소가 부족하여 잠재능력 재설정을 할 수 없습니다.");
                                 return;
                              }

                              MapleInventoryManipulator.addById(
                                 c, 2430759, (short)1, null, null, 0L, FileoutputUtil.CurrentReadable_Date() + "에 어메이징 미라클 큐브에 의해 생성된 아이템"
                              );
                              int beforeGrade = eq.getState();
                              setPotential(GradeRandomOption.Amazing, true, eq);
                              int afterGrade = eq.getState();
                              c.getPlayer().gainMeso(-price, true);
                              Equip zeroEquipxxx = null;
                              if (GameConstants.isZeroWeapon(eq.getItemId())) {
                                 zeroEquipxxx = (Equip)c.getPlayer()
                                    .getInventory(MapleInventoryType.EQUIPPED)
                                    .getItem((short)(eq.getPosition() == -11 ? -10 : -11));
                                 zeroEquipxxx.setState(eq.getState());
                                 zeroEquipxxx.setLines(eq.getLines());
                                 zeroEquipxxx.setPotential1(eq.getPotential1());
                                 zeroEquipxxx.setPotential2(eq.getPotential2());
                                 zeroEquipxxx.setPotential3(eq.getPotential3());
                              }

                              c.getPlayer().forceReAddItem(itemxxxxxxxxxxx, posxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                              if (zeroEquipxxx != null) {
                                 c.getPlayer().forceReAddItem(zeroEquipxxx, MapleInventoryType.EQUIPPED);
                                 c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
                              }

                              c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(toUse, itemxxxxxxxxxxx, false, true, false));
                              c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, itemId, eq.getItemId()));
                              used = true;
                           } else {
                              c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId, 0));
                           }
                           break;
                        case 5062009:
                           int posxxxxxx = slea.readInt();
                           Item itemxxxxxxxxxxxxxxxxxxxxxxxx = c.getPlayer()
                              .getInventory(posxxxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP)
                              .getItem((short)posxxxxxx);
                           if (itemxxxxxxxxxxxxxxxxxxxxxxxx != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                              Equip eqxx = (Equip)itemxxxxxxxxxxxxxxxxxxxxxxxx;
                              long pricexx = GameConstants.getItemReleaseCost(eqxx.getItemId());
                              if (pricexx > c.getPlayer().getMeso()) {
                                 c.getPlayer().dropMessage(5, "메소가 부족하여 잠재능력 재설정을 할 수 없습니다.");
                                 return;
                              }

                              MapleInventoryManipulator.addById(
                                 c, 2431893, (short)1, null, null, 0L, FileoutputUtil.CurrentReadable_Date() + "에 레드 큐브에 의해 생성된 아이템"
                              );
                              int beforeGradexx = eqxx.getState();
                              int beforeItemGrade = eqxx.getItemGrade();
                              setPotential(GradeRandomOption.Red, true, eqxx);
                              int afterItemGrade = eqxx.getItemGrade();
                              String checkType = "Red";
                              switch (beforeItemGrade) {
                                 case 1:
                                    checkType = checkType + "RtoE";
                                    break;
                                 case 2:
                                    checkType = checkType + "EtoU";
                                    break;
                                 case 3:
                                    checkType = checkType + "UtoL";
                              }

                              int tryCountx = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.CubeLevelUp.getQuestID(), checkType) + 1;
                              int levelUpCountx = GameConstants.getCubeLevelUpCount(GradeRandomOption.Red, beforeItemGrade);
                              checkCubeLevelUp(c, eqxx, GradeRandomOption.Red, beforeItemGrade, afterItemGrade);
                              int afterGradexx = eqxx.getState();
                              int var387 = eqxx.getItemGrade();
                              if (var387 > beforeItemGrade) {
                                 tryCountx = 0;
                                 levelUpCountx = 0;
                              }

                              c.getPlayer().gainMeso(-pricexx, true);
                              Equip zeroEquipxxxxx = null;
                              if (GameConstants.isZeroWeapon(eqxx.getItemId())) {
                                 zeroEquipxxxxx = (Equip)c.getPlayer()
                                    .getInventory(MapleInventoryType.EQUIPPED)
                                    .getItem((short)(eqxx.getPosition() == -11 ? -10 : -11));
                                 zeroEquipxxxxx.setState(eqxx.getState());
                                 zeroEquipxxxxx.setLines(eqxx.getLines());
                                 zeroEquipxxxxx.setPotential1(eqxx.getPotential1());
                                 zeroEquipxxxxx.setPotential2(eqxx.getPotential2());
                                 zeroEquipxxxxx.setPotential3(eqxx.getPotential3());
                              }

                              c.getPlayer()
                                 .forceReAddItem(itemxxxxxxxxxxxxxxxxxxxxxxxx, posxxxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                              if (zeroEquipxxxxx != null) {
                                 c.getPlayer().forceReAddItem(zeroEquipxxxxx, MapleInventoryType.EQUIPPED);
                                 c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
                              }

                              c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, itemId, eqxx.getItemId()));
                              int remainCount = c.getPlayer().getItemQuantity(5062009, false);
                              c.getSession()
                                 .writeAndFlush(
                                    CField.getRedCubeStart(
                                       c.getPlayer(),
                                       itemxxxxxxxxxxxxxxxxxxxxxxxx,
                                       itemId,
                                       beforeGradexx != afterGradexx,
                                       remainCount - 1,
                                       tryCountx,
                                       levelUpCountx
                                    )
                                 );
                              used = true;
                           } else {
                              c.getPlayer().dropMessage(5, "소비 인벤토리의 공간이 부족하여 잠재능력 재설정을 할 수 없습니다.");
                           }
                           break;
                        case 5062010:
                           Item itemxxxxxxxxxxxxxxxxxxxxxxx = null;
                           int posxxxxx = slea.readInt();
                           itemxxxxxxxxxxxxxxxxxxxxxxx = c.getPlayer()
                              .getInventory(posxxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP)
                              .getItem((short)posxxxxx);
                           if (itemxxxxxxxxxxxxxxxxxxxxxxx != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                              Equip eqx = (Equip)itemxxxxxxxxxxxxxxxxxxxxxxx;
                              Equip neq = (Equip)eqx.copy();
                              c.getPlayer().memorialCube = neq;
                              c.getPlayer().memorialCubeItemID = 5062010;
                              long pricex = GameConstants.getItemReleaseCost(eqx.getItemId());
                              if (pricex > c.getPlayer().getMeso()) {
                                 c.getPlayer().dropMessage(5, "메소가 부족하여 잠재능력 재설정을 할 수 없습니다.");
                                 return;
                              }

                              int beforeGradexx = neq.getState();
                              int beforeItemGrade = neq.getItemGrade();
                              setPotential(GradeRandomOption.Black, true, neq);
                              int afterItemGrade = neq.getItemGrade();
                              String checkType = "Black";
                              switch (beforeItemGrade) {
                                 case 1:
                                    checkType = checkType + "RtoE";
                                    break;
                                 case 2:
                                    checkType = checkType + "EtoU";
                                    break;
                                 case 3:
                                    checkType = checkType + "UtoL";
                              }

                              int tryCountx = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.CubeLevelUp.getQuestID(), checkType) + 1;
                              int levelUpCountx = GameConstants.getCubeLevelUpCount(GradeRandomOption.Black, beforeItemGrade);
                              checkCubeLevelUp(c, neq, GradeRandomOption.Black, beforeItemGrade, afterItemGrade);
                              int afterGradexx = neq.getState();
                              int var402 = neq.getItemGrade();
                              if (var402 > beforeItemGrade) {
                                 tryCountx = 0;
                                 levelUpCountx = 0;
                              }

                              c.getPlayer().gainMeso(-pricex, true);
                              int remainCount = c.getPlayer().getItemQuantity(5062010, false);
                              c.getSession().writeAndFlush(CField.getBlackCubeResult(c.getPlayer(), neq, itemId, remainCount - 1, tryCountx, levelUpCountx));
                              c.getPlayer().getMap().broadcastMessage(CField.showItemMemorialEffect(c.getPlayer().getId(), itemId));
                              MapleInventoryManipulator.addById(
                                 c, 2431894, (short)1, null, null, 0L, FileoutputUtil.CurrentReadable_Date() + "에 블랙 큐브에 의해 생성된 아이템"
                              );
                              used = true;
                           } else {
                              c.getPlayer().dropMessage(5, "소비 인벤토리의 공간이 부족하여 잠재 설정을 할 수 없습니다.");
                           }
                           break;
                        case 5062400:
                        case 5062402:
                           Equip appearance_item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)slea.readInt());
                           Equip function_item = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)slea.readInt());
                           if (function_item.getFusionAnvil() != 0) {
                              function_item.setFusionAnvil(appearance_item.getFusionAnvil());
                           } else {
                              String lol = Integer.valueOf(appearance_item.getItemId()).toString();
                              String ss = lol.substring(3, 7);
                              function_item.setFusionAnvil(Integer.parseInt(ss));
                           }

                           c.getSession().writeAndFlush(CField.getAnvilStart(function_item));
                           used = true;
                           break;
                        case 5062500:
                           int posxxxxxxx = slea.readInt();
                           Item itemxxxxxxxxxxxxxxxxxxxxxxxxx = c.getPlayer()
                              .getInventory(posxxxxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP)
                              .getItem((short)posxxxxxxx);
                           if (itemxxxxxxxxxxxxxxxxxxxxxxxxx != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                              Equip eqxxx = (Equip)itemxxxxxxxxxxxxxxxxxxxxxxxxx;
                              if (eqxxx.getState() <= 4) {
                                 c.getPlayer().dropMessage(1, "먼저 잠재능력을 열어주세요.");
                                 c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                                 return;
                              }

                              long pricexxx = GameConstants.getItemReleaseCost(eqxxx.getItemId());
                              if (pricexxx > c.getPlayer().getMeso()) {
                                 c.getPlayer().dropMessage(5, "메소가 부족하여 잠재능력 재설정을 할 수 없습니다.");
                                 return;
                              }

                              MapleInventoryManipulator.addById(
                                 c, 2430915, (short)1, null, null, 0L, FileoutputUtil.CurrentReadable_Date() + "에 에디셔널 큐브에 의해 생성된 아이템"
                              );
                              int beforeGradexx = eqxxx.getAdditionalGrade();
                              setPotential(GradeRandomOption.Additional, true, eqxxx);
                              int afterGradexxx = eqxxx.getAdditionalGrade();
                              String checkType = "Red";
                              switch (beforeGradexx) {
                                 case 1:
                                    checkType = checkType + "RtoE";
                                    break;
                                 case 2:
                                    checkType = checkType + "EtoU";
                                    break;
                                 case 3:
                                    checkType = checkType + "UtoL";
                              }

                              int tryCountxx = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.CubeLevelUp.getQuestID(), checkType) + 1;
                              int levelUpCountxx = GameConstants.getCubeLevelUpCount(GradeRandomOption.Additional, beforeGradexx);
                              checkCubeLevelUp(c, eqxxx, GradeRandomOption.Additional, beforeGradexx, afterGradexxx);
                              int var366 = eqxxx.getAdditionalGrade();
                              if (var366 > beforeGradexx) {
                                 tryCountxx = 0;
                                 levelUpCountxx = 0;
                              }

                              Equip zeroEquipxxxxxx = null;
                              if (GameConstants.isZeroWeapon(eqxxx.getItemId())) {
                                 zeroEquipxxxxxx = (Equip)c.getPlayer()
                                    .getInventory(MapleInventoryType.EQUIPPED)
                                    .getItem((short)(eqxxx.getPosition() == -11 ? -10 : -11));
                                 zeroEquipxxxxxx.setState(eqxxx.getState());
                                 zeroEquipxxxxxx.setLines(eqxxx.getLines());
                                 zeroEquipxxxxxx.setPotential4(eqxxx.getPotential4());
                                 zeroEquipxxxxxx.setPotential5(eqxxx.getPotential5());
                                 zeroEquipxxxxxx.setPotential6(eqxxx.getPotential6());
                              }

                              c.getPlayer().gainMeso(-pricexxx, true);
                              c.getSession().writeAndFlush(CField.showPotentialReset(c.getPlayer().getId(), true, itemId, eqxxx.getItemId()));
                              int remainCount = c.getPlayer().getItemQuantity(5062500, false);
                              c.getSession()
                                 .writeAndFlush(
                                    CField.getAdditionalCubeResult(
                                       c.getPlayer(),
                                       itemxxxxxxxxxxxxxxxxxxxxxxxxx,
                                       itemId,
                                       beforeGradexx != var366,
                                       remainCount - 1,
                                       tryCountxx,
                                       levelUpCountxx
                                    )
                                 );
                              c.getPlayer()
                                 .forceReAddItem(itemxxxxxxxxxxxxxxxxxxxxxxxxx, posxxxxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
                              if (zeroEquipxxxxxx != null) {
                                 c.getPlayer().forceReAddItem(zeroEquipxxxxxx, MapleInventoryType.EQUIPPED);
                                 c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
                              }

                              used = true;
                           } else {
                              c.getPlayer().dropMessage(5, "소비 인벤토리의 공간이 부족하여 잠재능력 재설정을 할 수 없습니다.");
                           }
                           break;
                        case 5062503:
                           Item itemxxxxxxxxxxxxxxxxxxxxxx = null;
                           int tar = slea.readInt();
                           if (tar > 0) {
                              itemxxxxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)tar);
                           } else {
                              itemxxxxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)tar);
                           }

                           if (itemxxxxxxxxxxxxxxxxxxxxxx == null || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
                              c.getPlayer().dropMessage(5, "소비 인벤토리의 공간이 부족하여 잠재 설정을 할 수 없습니다.");
                           } else if (GameConstants.isAndroid(itemxxxxxxxxxxxxxxxxxxxxxx.getItemId())
                              && (itemxxxxxxxxxxxxxxxxxxxxxx.getFlag() & ItemFlag.ANDROID_ACTIVATED.getValue()) == 0) {
                              c.getPlayer().dropMessage(5, "활성화 되지 않은 안드로이드는 잠재능력을 재설정을 할 수 없습니다.");
                           } else {
                              Equip eqx = (Equip)itemxxxxxxxxxxxxxxxxxxxxxx;
                              Equip neq = (Equip)eqx.copy();
                              c.getPlayer().memorialCube = neq;
                              c.getPlayer().memorialCubeItemID = 5062503;
                              if (eqx.getState() >= 17) {
                                 long pricex = GameConstants.getItemReleaseCost(eqx.getItemId());
                                 if (pricex > c.getPlayer().getMeso()) {
                                    c.getPlayer().dropMessage(5, "메소가 부족하여 잠재능력 재설정을 할 수 없습니다.");
                                 } else {
                                    int beforeGradex = neq.getAdditionalGrade();
                                    if (DBConfig.isGanglim) {
                                       setPotential(GradeRandomOption.AmazingAdditional, true, neq);
                                    } else {
                                       setPotential(GradeRandomOption.Additional, true, neq);
                                    }

                                    int afterGradex = neq.getAdditionalGrade();
                                    int tryCount = 0;
                                    int levelUpCount = 0;
                                    if (DBConfig.isJin) {
                                       String checkType = "AmazingAdditional";
                                       switch (beforeGradex) {
                                          case 1:
                                             checkType = checkType + "RtoE";
                                             break;
                                          case 2:
                                             checkType = checkType + "EtoU";
                                             break;
                                          case 3:
                                             checkType = checkType + "UtoL";
                                       }

                                       tryCount = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.CubeLevelUp.getQuestID(), checkType) + 1;
                                       levelUpCount = GameConstants.getCubeLevelUpCount(GradeRandomOption.AmazingAdditional, beforeGradex);
                                    }

                                    if (!DBConfig.isGanglim) {
                                       checkCubeLevelUp(c, neq, GradeRandomOption.AmazingAdditional, beforeGradex, afterGradex);
                                       int var383 = neq.getAdditionalGrade();
                                       if (var383 > beforeGradex) {
                                          tryCount = 0;
                                          levelUpCount = 0;
                                       }
                                    }

                                    c.getPlayer().gainMeso(-pricex, true);
                                    int remainCount = c.getPlayer().getItemQuantity(5062503, false);
                                    c.getSession()
                                       .writeAndFlush(CField.getWhiteAdditionalCubeResult(c.getPlayer(), neq, itemId, remainCount - 1, tryCount, levelUpCount));
                                    c.getPlayer().getMap().broadcastMessage(CField.showItemMemorialEffect(c.getPlayer().getId(), itemId));
                                    MapleInventoryManipulator.addById(
                                       c, 2434782, (short)1, null, null, 0L, FileoutputUtil.CurrentReadable_Date() + "에 화이트 에디셔널 큐브에 의해 생성된 아이템"
                                    );
                                    used = true;
                                 }
                              } else {
                                 c.getPlayer().dropMessage(5, "잠재 능력을 먼저 개방해야 사용할 수 있습니다.");
                              }
                           }
                           break;
                        case 5062800:
                           if (c.getPlayer().getInnerSkills().isEmpty()) {
                              return;
                           }

                           InnerAbility.gachaAbilityMiracle(c.getPlayer(), toUse);
                           used = true;
                           break;
                        case 5063000:
                           MapleInventoryType typexxxx = MapleInventoryType.getByType(slea.readByte());
                           slea.skip(1);
                           byte posxxx = slea.readByte();
                           Item itemxxxx = c.getPlayer().getInventory(typexxxx).getItem(posxxx);
                           if (itemxxxx == null) {
                              itemxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(posxxx);
                           }

                           if (itemxxxx != null && itemxxxx.getType() == 1) {
                              int flagxx = itemxxxx.getFlag();
                              flagxx |= ItemFlag.LUCKY_DAY_SCROLLED.getValue();
                              itemxxxx.setFlag(flagxx);
                              Item zeroEquipxx = null;
                              if (GameConstants.isZeroWeapon(itemxxxx.getItemId())) {
                                 zeroEquipxx = c.getPlayer()
                                    .getInventory(MapleInventoryType.EQUIPPED)
                                    .getItem((short)(itemxxxx.getPosition() == -11 ? -10 : -11));
                                 zeroEquipxx.setFlag(itemxxxx.getFlag());
                              }

                              if (zeroEquipxx != null) {
                                 c.getPlayer().forceReAddItem_Flag(zeroEquipxx, typexxxx);
                                 c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, zeroEquipxx, false, false, false));
                              }

                              c.getPlayer().forceReAddItem_Flag(itemxxxx, typexxxx);
                              c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, itemxxxx, false, false, false));
                              c.getPlayer()
                                 .getMap()
                                 .broadcastMessage(
                                    c.getPlayer(),
                                    CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, toUse.getItemId(), itemxxxx.getItemId()),
                                    true
                                 );
                              used = true;
                           }
                           break;
                        case 5064000:
                           MapleInventoryType typexxxxxxxxxxx = MapleInventoryType.getByType(slea.readByte());
                           slea.skip(1);
                           posxxxx = slea.readByte();
                           Item itemxxxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(typexxxxxxxxxxx).getItem((short)posxxxx);
                           if (itemxxxxxxxxxxxxxxxxxxxxx == null) {
                              itemxxxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)posxxxx);
                           }

                           if (itemxxxxxxxxxxxxxxxxxxxxx != null && (itemxxxxxxxxxxxxxxxxxxxxx.getType() == 1 || itemxxxxxxxxxxxxxxxxxxxxx.getType() == -1)) {
                              int flagxxx = itemxxxxxxxxxxxxxxxxxxxxx.getFlag();
                              flagxxx |= ItemFlag.PROTECTION_SCROLLED.getValue();
                              itemxxxxxxxxxxxxxxxxxxxxx.setFlag(flagxxx);
                              Item zeroEquipxxxx = null;
                              if (GameConstants.isZeroWeapon(itemxxxxxxxxxxxxxxxxxxxxx.getItemId())) {
                                 zeroEquipxxxx = c.getPlayer()
                                    .getInventory(MapleInventoryType.EQUIPPED)
                                    .getItem((short)(itemxxxxxxxxxxxxxxxxxxxxx.getPosition() == -11 ? -10 : -11));
                                 zeroEquipxxxx.setFlag(itemxxxxxxxxxxxxxxxxxxxxx.getFlag());
                              }

                              if (zeroEquipxxxx != null) {
                                 c.getPlayer().forceReAddItem_Flag(zeroEquipxxxx, typexxxxxxxxxxx);
                                 c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, zeroEquipxxxx, false, false, false));
                              }

                              c.getPlayer().forceReAddItem_Flag(itemxxxxxxxxxxxxxxxxxxxxx, typexxxxxxxxxxx);
                              c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, itemxxxxxxxxxxxxxxxxxxxxx, false, false, false));
                              c.getPlayer()
                                 .getMap()
                                 .broadcastMessage(
                                    c.getPlayer(),
                                    CField.getScrollEffect(
                                       c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, toUse.getItemId(), itemxxxxxxxxxxxxxxxxxxxxx.getItemId()
                                    ),
                                    true
                                 );
                              used = true;
                           }
                           break;
                        case 5064100:
                           MapleInventoryType typexxx = MapleInventoryType.getByType(slea.readByte());
                           slea.skip(1);
                           short posxx = slea.readShort();
                           Item itemxxx = c.getPlayer().getInventory(typexxx).getItem(posxx);
                           if (itemxxx == null) {
                              itemxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(posxx);
                           }

                           if (itemxxx != null && itemxxx.getType() == 1) {
                              int flagx = itemxxx.getFlag();
                              flagx |= ItemFlag.SAFETY_SCROLLED.getValue();
                              itemxxx.setFlag(flagx);
                              Item zeroEquipx = null;
                              if (GameConstants.isZeroWeapon(itemxxx.getItemId())) {
                                 zeroEquipx = c.getPlayer()
                                    .getInventory(MapleInventoryType.EQUIPPED)
                                    .getItem((short)(itemxxx.getPosition() == -11 ? -10 : -11));
                                 zeroEquipx.setFlag(itemxxx.getFlag());
                              }

                              if (zeroEquipx != null) {
                                 c.getPlayer().forceReAddItem_Flag(zeroEquipx, typexxx);
                                 c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, zeroEquipx, false, false, false));
                              }

                              c.getPlayer().forceReAddItem_Flag(itemxxx, typexxx);
                              c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, itemxxx, false, false, false));
                              c.getPlayer()
                                 .getMap()
                                 .broadcastMessage(
                                    c.getPlayer(),
                                    CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, toUse.getItemId(), itemxxx.getItemId()),
                                    true
                                 );
                              used = true;
                           }
                           break;
                        case 5064300:
                           MapleInventoryType typexx = MapleInventoryType.getByType(slea.readByte());
                           slea.skip(1);
                           short posx = slea.readShort();
                           Item itemxx = c.getPlayer().getInventory(typexx).getItem(posx);
                           if (itemxx == null) {
                              itemxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(posx);
                           }

                           if (itemxx != null && itemxx.getType() == 1) {
                              int flag = itemxx.getFlag();
                              flag |= ItemFlag.RECOVERY_SCROLLED.getValue();
                              itemxx.setFlag(flag);
                              Item zeroEquip = null;
                              if (GameConstants.isZeroWeapon(itemxx.getItemId())) {
                                 zeroEquip = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(itemxx.getPosition() == -11 ? -10 : -11));
                                 zeroEquip.setFlag(itemxx.getFlag());
                              }

                              if (zeroEquip != null) {
                                 c.getPlayer().forceReAddItem_Flag(zeroEquip, typexx);
                                 c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, zeroEquip, false, false, false));
                              }

                              c.getPlayer().forceReAddItem_Flag(itemxx, typexx);
                              c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, itemxx, false, false, false));
                              c.getPlayer()
                                 .getMap()
                                 .broadcastMessage(
                                    c.getPlayer(),
                                    CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, toUse.getItemId(), itemxx.getItemId()),
                                    true
                                 );
                              used = true;
                           }
                           break;
                        case 5064400:
                           MapleInventoryType typex = MapleInventoryType.getByType(slea.readByte());
                           slea.skip(1);
                           short pos = slea.readShort();
                           Item itemx = c.getPlayer().getInventory(typex).getItem(pos);
                           if (itemx == null) {
                              itemx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(pos);
                           }

                           if (itemx != null && itemx.getType() == 1) {
                              int flag = itemx.getFlag();
                              flag |= ItemFlag.RETURN_SCROLLED.getValue();
                              itemx.setFlag(flag);
                              c.getPlayer().forceReAddItem_Flag(itemx, typex);
                              c.getPlayer().send(CWvsContext.InventoryPacket.scrolledItem(toUse, itemx, false, false, false));
                              c.getPlayer()
                                 .getMap()
                                 .broadcastMessage(
                                    c.getPlayer(),
                                    CField.getScrollEffect(c.getPlayer().getId(), Equip.ScrollResult.SUCCESS, toUse.getItemId(), itemx.getItemId()),
                                    true
                                 );
                              used = true;
                           }
                           break;
                        case 5068300: {
                           objects.item.rewards.RandomRewards reward = RoyalStyle.getRandomItem(RoyalStyle.wonderBerry);
                           if (reward == null) {
                              return;
                           }

                           if (c.getPlayer().getInventory(MapleInventoryType.CASH).isFull()) {
                              c.getSession().writeAndFlush(CWvsContext.wonderBerryResult(26));
                              return;
                           }

                           itemxxxxxxxxxxx = new Item(reward.getMaleItemID(), (short)1, (short)1, 0);
                           itemxxxxxxxxxxx.setUniqueId(MapleInventoryIdentifier.getInstance());
                           if ((toUse.getFlag() & ItemFlag.KARMA_USE.getValue()) != 0) {
                              itemxxxxxxxxxxx.setFlag((short)ItemFlag.KARMA_EQ.getValue());
                           }

                           if (GameConstants.isPet(reward.getMaleItemID())) {
                              itemxxxxxxxxxxx.setExpiration(2475606994921L);
                              MaplePet petxx = MaplePet.createPet(reward.getMaleItemID(), itemxxxxxxxxxxx.getUniqueId());
                              itemxxxxxxxxxxx.setPet(petxx);
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           if (reward.isAnnounce() && !DBConfig.isGanglim) {
                              Center.Broadcast.broadcastGachaponMessage(
                                 c.getPlayer().getName() + "님이 " + ii.getName(5068300) + "에서 {" + ii.getName(itemxxxxxxxxxxx.getItemId()) + "} 아이템을 획득하였습니다",
                                 5068300,
                                 itemxxxxxxxxxxx
                              );
                           }

                           StringBuilder sbx = new StringBuilder(
                              "위습의 원더베리 사용 (출현 아이템 : "
                                 + ii.getName(itemxxxxxxxxxxx.getItemId())
                                 + "("
                                 + itemxxxxxxxxxxx.getItemId()
                                 + "), 사용자 : "
                                 + c.getPlayer().getName()
                                 + ")"
                           );
                           LoggingManager.putLog(new ConsumeLog(c.getPlayer(), 5068300, sbx));
                           short TI = MapleInventoryManipulator.addFromAuction(c, itemxxxxxxxxxxx);
                           c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
                           c.getSession().writeAndFlush(CWvsContext.wonderBerryResult(5068300, itemxxxxxxxxxxx));
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           used = true;
                           break;
                        }
                        case 5068305:
                        case 5068306: {
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).isFull()) {
                              c.getSession().writeAndFlush(CWvsContext.wonderBerryResult(26));
                              return;
                           }

                           if (c.getPlayer().getInventory(MapleInventoryType.USE).isFull()) {
                              c.getSession().writeAndFlush(CWvsContext.wonderBerryResult(26));
                              return;
                           }

                           if (c.getPlayer().getInventory(MapleInventoryType.CASH).isFull()) {
                              c.getSession().writeAndFlush(CWvsContext.wonderBerryResult(26));
                              return;
                           }

                           int[][] randomRewards = RoyalStyle.specialBerry.get(toUse.getItemId());
                           int[] randomValue = randomRewards[Randomizer.nextInt(randomRewards.length)];
                           Item itemxxxxxxxxxxxx = new Item(randomValue[0], (short)1, (short)randomValue[1], 0);
                           if (itemxxxxxxxxxxxx.getItemId() / 1000000 == 5 || ii.isCash(itemxxxxxxxxxxxx.getItemId())) {
                              itemxxxxxxxxxxxx.setUniqueId(MapleInventoryIdentifier.getInstance());
                           }

                           if (itemxxxxxxxxxxxx.getItemId() == 4310229) {
                              c.getPlayer()
                                 .updateOneInfo(
                                    QuestExConstants.UnionCoin.getQuestID(),
                                    "point",
                                    String.valueOf(
                                       c.getPlayer().getOneInfoQuestInteger(QuestExConstants.UnionCoin.getQuestID(), "point") + itemxxxxxxxxxxxx.getQuantity()
                                    )
                                 );
                           }

                           if (GameConstants.isPet(randomValue[0])) {
                              itemxxxxxxxxxxxx.setExpiration(2475606994921L);
                              MaplePet petxx = MaplePet.createPet(randomValue[0], itemxxxxxxxxxxxx.getUniqueId());
                              itemxxxxxxxxxxxx.setPet(petxx);
                           }

                           short TI = MapleInventoryManipulator.addFromAuction(c, itemxxxxxxxxxxxx);
                           c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
                           HyperHandler.updateSkills(c.getPlayer(), 0);
                           c.getPlayer().updateMatrixSkillsNoLock();
                           c.getSession().writeAndFlush(CWvsContext.wonderBerryResult(toUse.getItemId(), itemxxxxxxxxxxxx));
                           c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                           used = true;
                           break;
                        }
                        case 5069000:
                        case 5069001:
                        case 5069100: {
                           int baseItemID = slea.readInt();
                           byte baseItemTI = 6;
                           short baseItemPos = slea.readShort();
                           long baseItemSN = slea.readLong();
                           int materialItemID = slea.readInt();
                           byte materialItemTI = 6;
                           short materialItemPos = slea.readShort();
                           long materialItemSN = slea.readLong();
                           if (itemId == 5069100) {
                              baseItemTI = 5;
                              materialItemTI = 5;
                           }

                           if (c.getPlayer().getInventory(MapleInventoryType.getByType((byte)(itemId == 5069100 ? 5 : 6))).getNextFreeSlot() <= -1) {
                              return;
                           }

                           Item baseItem = c.getPlayer().getInventory(MapleInventoryType.getByType(baseItemTI)).getItem(baseItemPos);
                           if (baseItem.getUniqueId() != baseItemSN) {
                              return;
                           }

                           Item materialItem = c.getPlayer().getInventory(MapleInventoryType.getByType(materialItemTI)).getItem(materialItemPos);
                           if (materialItem.getUniqueId() != materialItemSN) {
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           if (itemId >= 5069000 && itemId <= 5069001) {
                              Equip b = (Equip)baseItem;
                              Equip m = (Equip)materialItem;
                              if (!b.isSpecialRoyal() || !m.isSpecialRoyal()) {
                                 c.getPlayer().dropMessage(5, "합성 재료 및 베이스 아이템이 메이플 강림 쿠폰 및 마스터피스에 의해 제작된 아이템만 합성이 가능합니다.");
                                 c.getPlayer().send(CField.getMasterPieceFailed(2));
                                 c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                                 return;
                              }

                              int rewardItemID = ii.getMasterPieceRewardItemID(
                                 c.getPlayer().isGM(), ((Equip)baseItem).getCsGrade() == 1, baseItem.getItemId(), c.getPlayer().getGender()
                              );
                              if (rewardItemID == -1) {
                                 return;
                              }

                              Item rewardItem = ii.getEquipById(rewardItemID);
                              Equip rewardEquip = null;
                              if (rewardItem != null) {
                                 rewardEquip = (Equip)rewardItem;
                              }

                              if (rewardEquip != null) {
                                 rewardEquip.setUniqueId(MapleInventoryIdentifier.getInstance());
                                 int atk = 0;
                                 int stat = 0;
                                 if (rewardEquip.isMasterLabel()) {
                                    rewardEquip.setCsGrade(3);
                                    atk = Randomizer.rand(35, 45);
                                    rewardEquip.setCsOption1(21000 + atk);
                                    rewardEquip.setCsOption2(22000 + atk);
                                    stat = Randomizer.rand(35, 45);
                                    rewardEquip.setCsOption3(10000 + Randomizer.rand(1, 4) * 1000 + stat);
                                    rewardEquip.setCsOptionExpireDate(System.currentTimeMillis() + 31536000000L);
                                    Center.Broadcast.broadcastGachaponMessage(
                                       c.getPlayer().getName() + "님이 " + ii.getName(5069000) + "에서 {" + ii.getName(rewardEquip.getItemId()) + "} 아이템을 획득하였습니다",
                                       5068300,
                                       rewardEquip
                                    );
                                 } else {
                                    Equip base = (Equip)baseItem;
                                    if ((base.getItemState() & ItemStateFlag.RED_LABEL_ITEM.getValue()) != 0) {
                                       rewardEquip.setCsGrade(2);
                                       atk = Randomizer.rand(20, 25);
                                       rewardEquip.setCsOption1(21000 + atk);
                                       rewardEquip.setCsOption2(22000 + atk);
                                       stat = Randomizer.rand(20, 25);
                                       rewardEquip.setCsOption3(10000 + Randomizer.rand(1, 4) * 1000 + stat);
                                       rewardEquip.setCsOptionExpireDate(System.currentTimeMillis() + 31536000000L);
                                       rewardEquip.setItemState(ItemStateFlag.BLACK_LABEL_ITEM.getValue());
                                    } else {
                                       rewardEquip.setCsGrade(1);
                                       atk = Randomizer.rand(10, 15);
                                       rewardEquip.setCsOption1(21000 + atk);
                                       rewardEquip.setCsOption2(22000 + atk);
                                       stat = Randomizer.rand(10, 15);
                                       rewardEquip.setCsOption3(10000 + Randomizer.rand(1, 4) * 1000 + stat);
                                       rewardEquip.setCsOptionExpireDate(System.currentTimeMillis() + 31536000000L);
                                       rewardEquip.setItemState(ItemStateFlag.RED_LABEL_ITEM.getValue());
                                    }
                                 }

                                 AchievementFactory.checkMasterPieceSuccess(c.getPlayer(), rewardEquip.getCsGrade());
                                 rewardEquip.setSpecialRoyal(true);
                                 if (itemId == 5069001) {
                                    rewardEquip.setOnceTrade(1);
                                    rewardEquip.setFlag((short)(ItemFlag.KARMA_EQ.getValue() | ItemFlag.KARMA_USE.getValue()));
                                 }

                                 StringBuilder sbx = new StringBuilder(
                                    "마스터피스 사용 (출현 아이템 : "
                                       + ii.getName(rewardEquip.getItemId())
                                       + "("
                                       + rewardEquip.getItemId()
                                       + "), 공/마 : "
                                       + atk
                                       + ", 스탯 : "
                                       + stat
                                       + ", 사용자 : "
                                       + c.getPlayer().getName()
                                       + ")"
                                 );
                                 LoggingManager.putLog(new ConsumeLog(c.getPlayer(), itemId, sbx));
                                 c.getPlayer().getInventory(MapleInventoryType.getByType(baseItemTI)).removeItem(baseItemPos, (short)1, false);
                                 c.getPlayer().getInventory(MapleInventoryType.getByType(materialItemTI)).removeItem(materialItemPos, (short)1, false);
                                 short TI = MapleInventoryManipulator.addFromAuction(c, rewardEquip);
                                 c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
                                 if (TI > 0) {
                                    c.getPlayer().send(CField.getMasterPieceReward(rewardEquip));
                                 }

                                 used = true;
                              }
                              break;
                           }

                           MaplePet basePet = baseItem.getPet();
                           MaplePet materialPet = materialItem.getPet();
                           if (basePet == null || materialPet == null) {
                              return;
                           }

                           if (basePet.getWonderGrade() == -1 || materialPet.getWonderGrade() == -1) {
                              c.getPlayer().dropMessage(1, "위습의 원더베리로 얻은 펫이 아니므로 사용할 수 없습니다.");
                              c.getPlayer().send(CField.getMasterPieceFailed(2));
                              c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           objects.item.rewards.RandomRewards rewardxx = RoyalStyle.getRandomItem(
                              DBConfig.isGanglim ? RoyalStyle.lunaCrystal_Royal : RoyalStyle.lunaCrystal_Jin
                           );
                           Item itemr = null;
                           if (GameConstants.isEquip(rewardxx.getMaleItemID())) {
                              itemr = ii.getEquipById(rewardxx.getMaleItemID());
                           } else {
                              itemr = new Item(rewardxx.getMaleItemID(), (short)1, (short)1, 0);
                           }

                           if (GameConstants.isPet(itemr.getItemId())) {
                              itemr.setUniqueId(MapleInventoryIdentifier.getInstance());
                              if (DBConfig.isGanglim) {
                                 itemr.setExpiration(new Date().getTime() + 1296000000L);
                              } else {
                                 itemr.setExpiration(2475606994921L);
                              }

                              MaplePet petxxxxx = MaplePet.createPet(rewardxx.getMaleItemID(), itemr.getUniqueId());
                              if (petxxxxx.getWonderGrade() != 6) {
                                 if (basePet.getWonderGrade() == 1 && materialPet.getWonderGrade() == 1) {
                                    petxxxxx.setWonderGrade((short)4);
                                 } else {
                                    petxxxxx.setWonderGrade((short)5);
                                 }
                              }

                              itemr.setPet(petxxxxx);
                           }

                           itemr.setFlag((short)(itemr.getFlag() | ItemFlag.KARMA_EQ.getValue()));
                           c.getPlayer().getInventory(MapleInventoryType.getByType(baseItemTI)).removeItem(baseItemPos, (short)1, false);
                           c.getPlayer().getInventory(MapleInventoryType.getByType(materialItemTI)).removeItem(materialItemPos, (short)1, false);
                           if (rewardxx.isAnnounce()) {
                              Center.Broadcast.broadcastGachaponMessage(
                                 c.getPlayer().getName() + "님이 " + ii.getName(5069100) + "에서 {" + ii.getName(itemr.getItemId()) + "} 아이템을 획득하였습니다",
                                 5068300,
                                 itemr
                              );
                           }

                           StringBuilder sbx = new StringBuilder(
                              "루나 크리스탈 사용 (출현 아이템 : " + ii.getName(itemr.getItemId()) + "(" + itemr.getItemId() + "), 사용자 : " + c.getPlayer().getName() + ")"
                           );
                           LoggingManager.putLog(new ConsumeLog(c.getPlayer(), itemId, sbx));
                           short TI = MapleInventoryManipulator.addbyItem(c, itemr, false);
                           c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
                           if (TI > 0) {
                              c.getPlayer().send(CField.getMasterPieceReward(itemr));
                           }

                           used = true;
                           break;
                        }
                        case 5070000:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 StringBuilder sbx = new StringBuilder();
                                 addMedalString(c.getPlayer(), sbx);
                                 sbx.append(c.getPlayer().getName());
                                 sbx.append(" : ");
                                 sbx.append(message);
                                 c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(2, sbx.toString()));
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5071000:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 StringBuilder sbx = new StringBuilder();
                                 addMedalString(c.getPlayer(), sbx);
                                 sbx.append(c.getPlayer().getName());
                                 sbx.append(" : ");
                                 sbx.append(message);
                                 c.getChannelServer().broadcastSmegaPacket(CWvsContext.serverNotice(2, sbx.toString()));
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5072000:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 StringBuilder sbx = new StringBuilder();
                                 addMedalString(c.getPlayer(), sbx);
                                 sbx.append(c.getPlayer().getName());
                                 sbx.append(" : ");
                                 sbx.append(message);
                                 boolean earx = slea.readByte() != 0;
                                 int channel = c.getChannel();
                                 Center.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, channel, sbx.toString(), earx));
                                 DiscordBotHandler.requestSendMegaphone(sbx.toString());
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5073000:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 StringBuilder sbx = new StringBuilder();
                                 addMedalString(c.getPlayer(), sbx);
                                 sbx.append(c.getPlayer().getName());
                                 sbx.append(" : ");
                                 sbx.append(message);
                                 boolean earx = slea.readByte() != 0;
                                 int channel = c.getChannel();
                                 if (channel > 1) {
                                    channel++;
                                 }

                                 Center.Broadcast.broadcastSmega(CWvsContext.serverNotice(9, channel, sbx.toString(), earx));
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5074000:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 StringBuilder sbx = new StringBuilder();
                                 addMedalString(c.getPlayer(), sbx);
                                 sbx.append(c.getPlayer().getName());
                                 sbx.append(" : ");
                                 sbx.append(message);
                                 boolean earx = slea.readByte() != 0;
                                 int channel = c.getChannel();
                                 if (channel > 1) {
                                    channel++;
                                 }

                                 Center.Broadcast.broadcastSmega(CWvsContext.serverNotice(22, channel, sbx.toString(), earx));
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5075003:
                        case 5075004:
                        case 5075005:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else {
                              int tvType = itemId % 10;
                              if (tvType == 3) {
                                 slea.readByte();
                              }

                              boolean earx = tvType != 1 && tvType != 2 && slea.readByte() > 1;
                              MapleCharacter victim = tvType != 1 && tvType != 4
                                 ? c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString())
                                 : null;
                              if (tvType != 0 && tvType != 3) {
                                 if (victim == null) {
                                    c.getPlayer().dropMessage(1, "That character is not in the channel.");
                                    break;
                                 }
                              } else {
                                 victim = null;
                              }

                              String message = slea.readMapleAsciiString();
                              int channel = c.getChannel();
                              if (channel > 1) {
                                 channel++;
                              }

                              Center.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, channel, c.getPlayer().getName() + " : " + message, earx));
                              used = true;
                           }
                           break;
                        case 5076000: {
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 StringBuilder sb = new StringBuilder();
                                 addMedalString(c.getPlayer(), sb);
                                 sb.append(c.getPlayer().getName());
                                 sb.append(" : ");
                                 sb.append(message);
                                 boolean ear = slea.readByte() > 0;
                                 Item item = null;
                                 String itemName = null;
                                 int type = slea.readInt();
                                 if (type == 1) {
                                    byte invType = (byte)slea.readInt();
                                    pos = (byte)slea.readInt();
                                    if (pos <= 0) {
                                       invType = -1;
                                    }

                                    item = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
                                    itemName = ii.getName(item.getItemId());
                                 }

                                 int channel = c.getChannel();
                                 if (channel > 1) {
                                    channel++;
                                 }

                                 Center.Broadcast.broadcastSmega(CWvsContext.itemMegaphone(sb.toString(), ear, channel, item, itemName));
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        }
                        case 5076100:
                           if (c.getPlayer().getLevel() < 200) {
                              c.getPlayer().dropMessage(5, "레벨 200 미만은 사용할 수 없습니다.");
                           } else {
                              String vx = c.getPlayer().getOneInfoQuest(1234677, "last_time");
                              if (vx != null && !vx.isEmpty()) {
                                 time = Long.parseLong(vx);
                                 if (!DBConfig.isGanglim) {
                                    if (System.currentTimeMillis() - time <= 60000L) {
                                       c.getPlayer().dropMessage(5, "1분 마다 사용할 수 있습니다.");
                                       break;
                                    }
                                 } else if (System.currentTimeMillis() - time <= 10000L) {
                                    c.getPlayer().dropMessage(5, "10초 마다 사용할 수 있습니다.");
                                    break;
                                 }
                              }

                              MapleItemInformationProvider iix = MapleItemInformationProvider.getInstance();
                              String msgx = slea.readMapleAsciiString();
                              PacketEncoder packet = new PacketEncoder();
                              packet.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
                              packet.write(8);
                              Item honor = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-21);
                              String title = "";
                              if (honor != null) {
                                 title = "<" + iix.getName(honor.getItemId()) + "> ";
                              }

                              Center.Broadcast.broadcastMessage(
                                 CWvsContext.getHyperMegaphone(
                                    title + c.getPlayer().getName() + " : " + msgx, msgx, slea.readByte(), slea.readInt(), c.getPlayer(), c, slea
                                 )
                              );
                              DiscordBotHandler.requestSendMegaphone(title + c.getPlayer().getName() + " : " + msgx);
                              if (!c.getPlayer().isGM()) {
                                 c.getPlayer().updateOneInfo(1234677, "last_time", String.valueOf(System.currentTimeMillis()));
                                 if (DBConfig.isGanglim) {
                                    c.getPlayer().addConsumeItemLimit(5076100, System.currentTimeMillis() + 10000L);
                                 } else {
                                    c.getPlayer().addConsumeItemLimit(5076100, System.currentTimeMillis() + 60000L);
                                 }
                              }

                              used = true;
                           }
                           break;
                        case 5077000:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              byte numLines = slea.readByte();
                              if (numLines > 3) {
                                 return;
                              }

                              List<String> messages = new LinkedList<>();

                              for (int ixx = 0; ixx < numLines; ixx++) {
                                 String message = slea.readMapleAsciiString();
                                 if (message.length() > 65) {
                                    break;
                                 }

                                 messages.add(c.getPlayer().getName() + " : " + message);
                              }

                              boolean earx = slea.readByte() > 0;
                              int channel = c.getChannel();
                              if (channel > 1) {
                                 channel++;
                              }

                              Center.Broadcast.broadcastSmega(CWvsContext.tripleSmega(messages, earx, channel));
                              used = true;
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5079000:
                        case 5079001:
                        case 5390000:
                        case 5390001:
                        case 5390002:
                        case 5390003:
                        case 5390004:
                        case 5390005:
                        case 5390006:
                        case 5390007:
                        case 5390008:
                        case 5390009:
                        case 5390010:
                        case 5390011:
                        case 5390012:
                        case 5390013:
                        case 5390014:
                        case 5390015:
                        case 5390016:
                        case 5390017:
                        case 5390018:
                        case 5390019:
                        case 5390020:
                        case 5390021:
                        case 5390022:
                        case 5390023:
                        case 5390024:
                        case 5390025:
                        case 5390026:
                        case 5390027:
                        case 5390028:
                        case 5390029:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10 레벨 이상이어야합니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "여기에서는 사용하실 수 없습니다.");
                           } else if (!c.getPlayer().getCheatTracker().canAvatarSmega()) {
                              c.getPlayer().dropMessage(5, "5분후에 다시 사용하실 수 있습니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              List<String> lines = new LinkedList<>();
                              StringBuilder sbx = new StringBuilder();

                              for (int ixxx = 0; ixxx < 4; ixxx++) {
                                 String text = slea.readMapleAsciiString();
                                 if (text.length() <= 55) {
                                    sbx.append(text);
                                    lines.add(text);
                                 }
                              }

                              boolean earx = slea.readByte() != 0;
                              Center.Broadcast.broadcastSmega(CWvsContext.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, lines, sbx.toString(), earx));
                              used = true;
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5079004:
                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "10레벨 미만은 사용할 수 없습니다.");
                           } else if (c.getPlayer().getMapId() == 180000002) {
                              c.getPlayer().dropMessage(5, "Cannot be used here.");
                           } else if (!c.getPlayer().getCheatTracker().canSmega()) {
                              c.getPlayer().dropMessage(5, "10초에 한번만 사용이 가능합니다.");
                           } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                              String message = slea.readMapleAsciiString();
                              if (message.length() <= 65) {
                                 Center.Broadcast.broadcastSmega(CWvsContext.echoMegaphone(c.getPlayer().getName(), message));
                                 used = true;
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                           }
                           break;
                        case 5090000:
                        case 5090100:
                           String sendTo = slea.readMapleAsciiString();
                           String msg = slea.readMapleAsciiString();
                           if (MapleCharacterUtil.canCreateChar(sendTo, false, true)) {
                              c.getSession().writeAndFlush(CSPacket.OnMemoResult((byte)9, (byte)1));
                           } else {
                              int ch = Center.Find.findChannel(sendTo);
                              if (ch <= 0) {
                                 c.getPlayer().sendNote(sendTo, msg);
                                 c.getSession().writeAndFlush(CSPacket.OnMemoResult((byte)8, (byte)0));
                                 used = true;
                              } else {
                                 c.getSession().writeAndFlush(CSPacket.OnMemoResult((byte)9, (byte)0));
                              }
                           }
                           break;
                        case 5100000:
                           c.getPlayer().getMap().broadcastMessage(CField.musicChange("Jukebox/Congratulation"));
                           used = true;
                           break;
                        case 5152300:
                           byte flagxxx = slea.readByte();
                           byte iszero = slea.readByte();
                           boolean isDressUp = false;
                           boolean isZeroBeta = false;
                           boolean isZeroMulti = false;
                           if (flagxxx == 1) {
                              isDressUp = true;
                           } else if (flagxxx == 2) {
                              isZeroBeta = true;
                           } else if (flagxxx == 101) {
                              isZeroMulti = true;
                           }

                           boolean fakeRelog = false;
                           int baseFace = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFace() : c.getPlayer().getFace();
                           int faceBaseColor = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFaceBaseColor() : c.getPlayer().getFaceBaseColor();
                           int faceAddColor = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFaceAddColor() : c.getPlayer().getFaceAddColor();
                           int faceBaseProb = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFaceBaseProb() : c.getPlayer().getFaceBaseProb();
                           baseFace = (baseFace / 1000 * 1000 + baseFace % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;

                           try {
                              if (isDressUp) {
                                 int mixInfo = slea.readInt();
                                 int afterFaceBaseColor = mixInfo / 10000;
                                 int afterFaceAddColor = mixInfo / 1000 % 10;
                                 int afterFaceBaseProb = mixInfo % 100;
                                 c.getPlayer().setSecondFaceBaseColor(afterFaceBaseColor);
                                 c.getPlayer().setSecondFaceAddColor(afterFaceAddColor);
                                 c.getPlayer().setSecondFaceBaseProb(afterFaceBaseProb);
                                 fakeRelog = true;
                              } else if (isZeroMulti) {
                                 int mixInfo = slea.readInt();
                                 int afterFaceBaseColor = mixInfo / 10000;
                                 int afterFaceAddColor = mixInfo / 1000 % 10;
                                 int afterFaceBaseProb = mixInfo % 100;
                                 int mixBetaInfo = slea.readInt();
                                 int afterBetaFaceBaseColor = mixBetaInfo / 10000;
                                 int afterBetaFaceAddColor = mixBetaInfo / 1000 % 10;
                                 int afterBetaFaceBaseProb = mixBetaInfo % 100;
                                 c.getPlayer().setFaceBaseColor(afterFaceBaseColor);
                                 c.getPlayer().setFaceAddColor(afterFaceAddColor);
                                 c.getPlayer().setFaceBaseProb(afterFaceBaseProb);
                                 c.getPlayer().setSecondFaceBaseColor(afterBetaFaceBaseColor);
                                 c.getPlayer().setSecondFaceAddColor(afterBetaFaceAddColor);
                                 c.getPlayer().setSecondFaceBaseProb(afterBetaFaceBaseProb);
                                 fakeRelog = true;
                              } else if (isZeroBeta) {
                                 int mixInfo = slea.readInt();
                                 int afterFaceBaseColor = mixInfo / 10000;
                                 int afterFaceAddColor = mixInfo / 1000 % 10;
                                 int afterFaceBaseProb = mixInfo % 100;
                                 c.getPlayer().setSecondFaceBaseColor(afterFaceBaseColor);
                                 c.getPlayer().setSecondFaceAddColor(afterFaceAddColor);
                                 c.getPlayer().setSecondFaceBaseProb(afterFaceBaseProb);
                                 fakeRelog = true;
                              } else {
                                 int mixInfo = slea.readInt();
                                 int afterFaceBaseColor = mixInfo / 10000;
                                 int afterFaceAddColor = mixInfo / 1000 % 10;
                                 int afterFaceBaseProb = mixInfo % 100;
                                 c.getPlayer().setFaceBaseColor(afterFaceBaseColor);
                                 c.getPlayer().setFaceAddColor(afterFaceAddColor);
                                 c.getPlayer().setFaceBaseProb(afterFaceBaseProb);
                              }

                              int face = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFace() : c.getPlayer().getFace();
                              faceBaseColor = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFaceBaseColor() : c.getPlayer().getFaceBaseColor();
                              faceAddColor = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFaceAddColor() : c.getPlayer().getFaceAddColor();
                              faceBaseProb = c.getPlayer().isDressUp() ? c.getPlayer().getSecondFaceBaseProb() : c.getPlayer().getFaceBaseProb();
                              face = (face / 1000 * 1000 + face % 100 + faceBaseColor * 100) * 1000 + faceAddColor * 100 + faceBaseProb;
                              if (!fakeRelog) {
                                 statupdate = new EnumMap<>(MapleStat.class);
                                 statupdate.put(MapleStat.FACE, (long)face);
                                 c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, false, c.getPlayer()));
                              }

                              c.getPlayer().getMap().broadcastMessage(CField.updateCharLook(c.getPlayer()));
                              c.getPlayer().send(CWvsContext.onUseMixLensResult(itemId, baseFace, face, isDressUp, flagxxx));
                              if (fakeRelog) {
                                 c.getPlayer().fakeRelog();
                              }

                              used = true;
                           } catch (Exception var31) {
                              used = false;
                           }
                           break;
                        case 5155000:
                        case 5155001:
                        case 5155002:
                        case 5155003:
                        case 5155004:
                        case 5155005:
                        case 5155006:
                           int qid = 0;
                           int val = 0;
                           int def = 0;
                           String effect = "Effect/BasicEff.img/JobChanged";
                           switch (itemId) {
                              case 5155000:
                                 qid = 7784;
                                 val = 1;
                                 def = 0;
                                 effect = effect + "Elf";
                                 break;
                              case 5155001:
                                 qid = 7786;
                                 val = 0;
                                 def = 1;
                                 effect = effect + "Kaiser";
                                 break;
                              case 5155002:
                                 qid = 7786;
                                 val = 0;
                                 def = 1;
                                 effect = effect + "Xenon";
                                 break;
                              case 5155003:
                                 qid = 7786;
                                 val = 0;
                                 def = 1;
                                 effect = effect + "Demon";
                                 break;
                              case 5155004:
                                 qid = 7784;
                                 val = 2;
                                 def = 0;
                                 effect = effect + "IlliumFront";
                                 break;
                              case 5155005:
                                 qid = 7784;
                                 val = 3;
                                 def = 0;
                                 effect = effect + "ArkFront";
                                 break;
                              case 5155006:
                                 qid = 7786;
                                 val = 0;
                                 def = 1;
                                 effect = effect + "ArkFront";
                           }

                           if (qid == 7784) {
                              val = slea.readInt();
                           }

                           if (itemId == 5155005 || itemId == 5155004 || itemId == 5155000) {
                              if (!GameConstants.isArk(c.getPlayer().getJob())
                                 && !GameConstants.isAdele(c.getPlayer().getJob())
                                 && !GameConstants.isKhali(c.getPlayer().getJob())) {
                                 if (GameConstants.isIllium(c.getPlayer().getJob())) {
                                    if (val == 2) {
                                       val = 0;
                                    } else if (val == 0) {
                                       val = 2;
                                    }
                                 } else if (GameConstants.isMercedes(c.getPlayer().getJob())) {
                                    if (val == 1) {
                                       val = 0;
                                    } else if (val == 0) {
                                       val = 1;
                                    }
                                 }
                              } else {
                                 if (val == 3) {
                                    val = 0;
                                 } else if (val == 0) {
                                    val = 3;
                                 }

                                 def = 0;
                                 effect = effect + "AdeleFront";
                              }
                           }

                           String data = c.getPlayer().getOneInfoQuest(qid, "sw");
                           int v = 0;
                           if (qid == 7784) {
                              v = val;
                           } else if (!data.isEmpty() && Integer.parseInt(data) != def) {
                              v = def;
                           } else {
                              v = val;
                           }

                           data = "sw=" + v;
                           c.getPlayer().updateInfoQuest(qid, data);
                           if (qid == 7784) {
                              c.getPlayer().setDrawElfEar(v);
                           } else {
                              c.getPlayer().setDrawTail(v == 1 ? 0 : 1);
                           }

                           c.getPlayer().getMap().broadcastMessage(CField.updateCharLook(c.getPlayer()));
                           SpecialEffect e = new SpecialEffect(c.getPlayer().getId(), true, 0, 2, itemId, effect);
                           c.getPlayer().send(e.encodeForLocal());
                           c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), e.encodeForRemote(), false);
                           used = true;
                           break;
                        case 5170000:
                           int uniqueid = (int)slea.readLong();
                           Item itemxxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.CASH).findByUniqueId(uniqueid);
                           if (itemxxxxxxxxxxxxxxxxxxxx != null) {
                              MaplePet petxx = itemxxxxxxxxxxxxxxxxxxxx.getPet();
                              if (petxx != null) {
                                 String nName = slea.readMapleAsciiString();

                                 for (String zxxxxxx : GameConstants.RESERVED) {
                                    if (petxx.getName().indexOf(zxxxxxx) != -1 || nName.indexOf(zxxxxxx) != -1) {
                                       break;
                                    }
                                 }

                                 posxxxx = -1;

                                 for (int ixx = 0; ixx < 3; ixx++) {
                                    MaplePet p = c.getPlayer().getPet(ixx);
                                    if (p != null && p.getUniqueId() == uniqueid) {
                                       posxxxx = ixx;
                                       break;
                                    }
                                 }

                                 petxx.setName(nName);
                                 c.getSession()
                                    .writeAndFlush(
                                       PetPacket.updatePet(
                                          c.getPlayer(),
                                          petxx,
                                          c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(petxx.getInventoryPosition()),
                                          false,
                                          c.getPlayer().getPetLoot()
                                       )
                                    );
                                 c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                                 if (posxxxx != -1) {
                                    c.getPlayer().getMap().broadcastMessage(CSPacket.changePetName(c.getPlayer(), nName, posxxxx));
                                 }

                                 used = true;
                              }
                           }
                           break;
                        case 5190000:
                        case 5190001:
                        case 5190002:
                        case 5190003:
                        case 5190004:
                        case 5190005:
                        case 5190006:
                        case 5190010:
                        case 5190011:
                        case 5190012:
                        case 5190013:
                           int uniqueIdx = slea.readInt();
                           MaplePet petx = null;
                           int petIndexx = c.getPlayer().getPetIndex(uniqueIdx);
                           if (petIndexx >= 0) {
                              petx = c.getPlayer().getPet(petIndexx);
                           } else {
                              petx = c.getPlayer().getInventory(MapleInventoryType.CASH).findByUniqueId(uniqueIdx).getPet();
                           }

                           if (petx == null) {
                              c.getPlayer().dropMessage(1, "펫을 찾는데 실패하였습니다!");
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           MaplePet.PetFlag zz = MaplePet.PetFlag.getByAddId(itemId);
                           petx.setFlags(petx.getFlags() | zz.getValue());
                           c.getPlayer()
                              .send(
                                 PetPacket.updatePet(
                                    c.getPlayer(),
                                    petx,
                                    c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(petx.getInventoryPosition()),
                                    false,
                                    c.getPlayer().getPetLoot()
                                 )
                              );
                           PacketEncoder p = new PacketEncoder();
                           p.writeShort(SendPacketOpcode.PET_FLAG_UPDATE.getValue());
                           p.writeLong(petx.getUniqueId());
                           p.write(1);
                           p.writeShort(zz.getValue());
                           c.getPlayer().send(p.getPacket());
                           c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                           used = true;
                           break;
                        case 5190014:
                        case 5191000:
                        case 5191001:
                        case 5191002:
                        case 5191003:
                        case 5191004:
                           int uniqueId = slea.readInt();
                           MaplePet pet = null;
                           int petIndex = c.getPlayer().getPetIndex(uniqueId);
                           if (petIndex >= 0) {
                              pet = c.getPlayer().getPet(petIndex);
                           } else {
                              pet = c.getPlayer().getInventory(MapleInventoryType.CASH).findByUniqueId(uniqueId).getPet();
                           }

                           if (pet == null) {
                              c.getPlayer().dropMessage(1, "펫을 찾는데 실패하였습니다!");
                              c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           zz = MaplePet.PetFlag.getByAddId(itemId);
                           pet.setFlags(pet.getFlags() - zz.getValue());
                           c.getPlayer()
                              .send(
                                 PetPacket.updatePet(
                                    c.getPlayer(),
                                    pet,
                                    c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()),
                                    false,
                                    c.getPlayer().getPetLoot()
                                 )
                              );
                           used = true;
                           break;
                        case 5230000:
                        case 5230001:
                           int itemSearch = slea.readInt();
                           List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
                           if (hms.size() > 0) {
                              c.getSession().writeAndFlush(CWvsContext.getOwlSearched(itemSearch, hms));
                              used = true;
                           } else {
                              c.getPlayer().dropMessage(1, "아이템을 찾을 수 없습니다.");
                           }
                           break;
                        case 5240000:
                        case 5240001:
                        case 5240002:
                        case 5240003:
                        case 5240004:
                        case 5240005:
                        case 5240006:
                        case 5240007:
                        case 5240008:
                        case 5240009:
                        case 5240010:
                        case 5240011:
                        case 5240012:
                        case 5240013:
                        case 5240014:
                        case 5240015:
                        case 5240016:
                        case 5240017:
                        case 5240018:
                        case 5240019:
                        case 5240020:
                        case 5240021:
                        case 5240022:
                        case 5240023:
                        case 5240024:
                        case 5240025:
                        case 5240026:
                        case 5240027:
                        case 5240028:
                        case 5240029:
                        case 5240030:
                        case 5240031:
                        case 5240032:
                        case 5240033:
                        case 5240034:
                        case 5240035:
                        case 5240036:
                        case 5240037:
                        case 5240038:
                        case 5240039:
                        case 5240040:
                        case 5240088:
                           for (MaplePet petxxx : c.getPlayer().getPets()) {
                              if (!petxxx.canConsume(itemId)) {
                                 int petindex = c.getPlayer().getPetIndex(petxxx);
                                 petxxx.setFullness(100);
                                 if (petxxx.getCloseness() < 30000) {
                                    if (petxxx.getCloseness() + 100 > 30000) {
                                       petxxx.setCloseness(30000);
                                    } else {
                                       petxxx.setCloseness(petxxx.getCloseness() + 100);
                                    }

                                    if (petxxx.getCloseness() >= GameConstants.getClosenessNeededForLevel(petxxx.getLevel() + 1)) {
                                       petxxx.setLevel(petxxx.getLevel() + 1);
                                       PetLevelUp e4 = new PetLevelUp(c.getPlayer().getId(), 0, c.getPlayer().getPetIndex(petxxx));
                                       c.getSession().writeAndFlush(e4.encodeForLocal());
                                       c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e4.encodeForRemote(), false);
                                    }
                                 }

                                 c.getSession()
                                    .writeAndFlush(
                                       PetPacket.updatePet(
                                          c.getPlayer(),
                                          petxxx,
                                          c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(petxxx.getInventoryPosition()),
                                          false,
                                          c.getPlayer().getPetLoot()
                                       )
                                    );
                                 c.getPlayer()
                                    .getMap()
                                    .broadcastMessage(
                                       c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte)1, (byte)petindex, true, true), true
                                    );
                              }
                           }

                           used = true;
                           break;
                        case 5300000:
                        case 5300001:
                        case 5300002: {
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           ii.getItemEffect(itemId).applyTo(c.getPlayer());
                           used = true;
                           break;
                        }
                        case 5330000:
                           c.getPlayer().setConversation(2);
                           c.getSession().writeAndFlush(CField.sendDuey((byte)9, null, null));
                           break;
                        case 5370000:
                        case 5370001:
                           for (MapleEventType t : MapleEventType.values()) {
                              MapleEvent e2 = GameServer.getInstance(c.getChannel()).getEvent(t);
                              if (e2.isRunning()) {
                                 for (int ixxxxx : e2.getType().mapids) {
                                    if (c.getPlayer().getMapId() == ixxxxx) {
                                       c.getPlayer().dropMessage(5, "You may not use that here.");
                                       c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                                       return;
                                    }
                                 }
                              }
                           }

                           c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                           break;
                        case 5450000:
                        case 5450003:
                        case 5452001:
                           for (int ixxxx : GameConstants.blockedMaps) {
                              if (c.getPlayer().getMapId() == ixxxx) {
                                 c.getPlayer().dropMessage(5, "You may not use this command here.");
                                 c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                                 return;
                              }
                           }

                           if (c.getPlayer().getLevel() < 10) {
                              c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                           } else if (c.getPlayer().getMap().getSquadByMap() == null
                              && c.getPlayer().getEventInstance() == null
                              && c.getPlayer().getMap().getEMByMap() == null
                              && c.getPlayer().getMapId() < 990000000) {
                              if ((c.getPlayer().getMapId() < 680000210 || c.getPlayer().getMapId() > 680000502)
                                 && (c.getPlayer().getMapId() / 1000 != 980000 || c.getPlayer().getMapId() == 980000000)
                                 && c.getPlayer().getMapId() / 100 != 1030008
                                 && c.getPlayer().getMapId() / 100 != 922010
                                 && c.getPlayer().getMapId() / 10 != 13003000) {
                                 MapleShopFactory.getInstance().getShop(61).sendShop(c);
                              } else {
                                 c.getPlayer().dropMessage(5, "You may not use this command here.");
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "You may not use this command here.");
                           }
                           break;
                        case 5450005:
                           c.getPlayer().setConversation(4);
                           c.getPlayer().getStorage().sendStorage(c, 1022005);
                           break;
                        case 5500000: {
                           Item itemxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           int days = 1;
                           if (itemxxxxxxxxxxxxx != null
                              && !GameConstants.isAccessory(itemxxxxxxxxxxxxx.getItemId())
                              && itemxxxxxxxxxxxxx.getExpiration() > -1L
                              && !ii.isCash(itemxxxxxxxxxxxxx.getItemId())
                              && System.currentTimeMillis() + 8640000000L > itemxxxxxxxxxxxxx.getExpiration() + 86400000L) {
                              boolean change = true;

                              for (String z : GameConstants.RESERVED) {
                                 if (c.getPlayer().getName().indexOf(z) != -1 || itemxxxxxxxxxxxxx.getOwner().indexOf(z) != -1) {
                                    change = false;
                                 }
                              }

                              if (change) {
                                 itemxxxxxxxxxxxxx.setExpiration(itemxxxxxxxxxxxxx.getExpiration() + 86400000L);
                                 c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                 used = true;
                              } else {
                                 c.getPlayer().dropMessage(1, "It may not be used on this item.");
                              }
                           }
                           break;
                        }
                        case 5500001: {
                           Item itemxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           int daysx = 7;
                           if (itemxxxxxxxxxxxxxx != null
                              && !GameConstants.isAccessory(itemxxxxxxxxxxxxxx.getItemId())
                              && itemxxxxxxxxxxxxxx.getExpiration() > -1L
                              && !ii.isCash(itemxxxxxxxxxxxxxx.getItemId())
                              && System.currentTimeMillis() + 8640000000L > itemxxxxxxxxxxxxxx.getExpiration() + 604800000L) {
                              boolean change = true;

                              for (String zx : GameConstants.RESERVED) {
                                 if (c.getPlayer().getName().indexOf(zx) != -1 || itemxxxxxxxxxxxxxx.getOwner().indexOf(zx) != -1) {
                                    change = false;
                                 }
                              }

                              if (change) {
                                 itemxxxxxxxxxxxxxx.setExpiration(itemxxxxxxxxxxxxxx.getExpiration() + 604800000L);
                                 c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                 used = true;
                              } else {
                                 c.getPlayer().dropMessage(1, "It may not be used on this item.");
                              }
                           }
                           break;
                        }
                        case 5500002: {
                           Item itemxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           int daysxx = 20;
                           if (itemxxxxxxxxxxxxxxx != null
                              && !GameConstants.isAccessory(itemxxxxxxxxxxxxxxx.getItemId())
                              && itemxxxxxxxxxxxxxxx.getExpiration() > -1L
                              && !ii.isCash(itemxxxxxxxxxxxxxxx.getItemId())
                              && System.currentTimeMillis() + 8640000000L > itemxxxxxxxxxxxxxxx.getExpiration() + 1728000000L) {
                              boolean change = true;

                              for (String zxx : GameConstants.RESERVED) {
                                 if (c.getPlayer().getName().indexOf(zxx) != -1 || itemxxxxxxxxxxxxxxx.getOwner().indexOf(zxx) != -1) {
                                    change = false;
                                 }
                              }

                              if (change) {
                                 itemxxxxxxxxxxxxxxx.setExpiration(itemxxxxxxxxxxxxxxx.getExpiration() + 1728000000L);
                                 c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                 used = true;
                              } else {
                                 c.getPlayer().dropMessage(1, "It may not be used on this item.");
                              }
                           }
                           break;
                        }
                        case 5500005: {
                           Item itemxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           int daysxxx = 50;
                           if (itemxxxxxxxxxxxxxxxx != null
                              && !GameConstants.isAccessory(itemxxxxxxxxxxxxxxxx.getItemId())
                              && itemxxxxxxxxxxxxxxxx.getExpiration() > -1L
                              && !ii.isCash(itemxxxxxxxxxxxxxxxx.getItemId())
                              && System.currentTimeMillis() + 8640000000L > itemxxxxxxxxxxxxxxxx.getExpiration() + 4320000000L) {
                              boolean change = true;

                              for (String zxxx : GameConstants.RESERVED) {
                                 if (c.getPlayer().getName().indexOf(zxxx) != -1 || itemxxxxxxxxxxxxxxxx.getOwner().indexOf(zxxx) != -1) {
                                    change = false;
                                 }
                              }

                              if (change) {
                                 itemxxxxxxxxxxxxxxxx.setExpiration(itemxxxxxxxxxxxxxxxx.getExpiration() + 25032704L);
                                 c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                 used = true;
                              } else {
                                 c.getPlayer().dropMessage(1, "It may not be used on this item.");
                              }
                           }
                           break;
                        }
                        case 5500006: {
                           Item itemxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           int daysxxxx = 99;
                           if (itemxxxxxxxxxxxxxxxxx != null
                              && !GameConstants.isAccessory(itemxxxxxxxxxxxxxxxxx.getItemId())
                              && itemxxxxxxxxxxxxxxxxx.getExpiration() > -1L
                              && !ii.isCash(itemxxxxxxxxxxxxxxxxx.getItemId())
                              && System.currentTimeMillis() + 8640000000L > itemxxxxxxxxxxxxxxxxx.getExpiration() + 8553600000L) {
                              boolean change = true;

                              for (String zxxxx : GameConstants.RESERVED) {
                                 if (c.getPlayer().getName().indexOf(zxxxx) != -1 || itemxxxxxxxxxxxxxxxxx.getOwner().indexOf(zxxxx) != -1) {
                                    change = false;
                                 }
                              }

                              if (change) {
                                 itemxxxxxxxxxxxxxxxxx.setExpiration(itemxxxxxxxxxxxxxxxxx.getExpiration() + -36334592L);
                                 c.getPlayer().forceReAddItem(itemxxxxxxxxxxxxxxxxx, MapleInventoryType.EQUIPPED);
                                 used = true;
                              } else {
                                 c.getPlayer().dropMessage(1, "It may not be used on this item.");
                              }
                           }
                           break;
                        }
                        case 5501001:
                        case 5501002:
                           Skill skil = SkillFactory.getSkill(slea.readInt());
                           if (skil != null
                              && skil.getId() / 10000 == 8000
                              && c.getPlayer().getSkillLevel(skil) > 0
                              && skil.isTimeLimited()
                              && GameConstants.getMountItem(skil.getId(), c.getPlayer()) > 0) {
                              long toAdd = (itemId == 5501001 ? 30 : 60) * 24 * 60 * 60 * 1000L;
                              long expire = c.getPlayer().getSkillExpiry(skil);
                              if (expire >= System.currentTimeMillis() && expire + toAdd < System.currentTimeMillis() + 31536000000L) {
                                 c.getPlayer()
                                    .changeSingleSkillLevel(skil, c.getPlayer().getSkillLevel(skil), c.getPlayer().getMasterLevel(skil), expire + toAdd);
                                 used = true;
                              }
                           }
                           break;
                        case 5520000:
                        case 5520001:
                           if (GameConstants.isYetiPinkBean(c.getPlayer().getJob())) {
                              c.getPlayer().dropMessage(5, "핑크빈과 예티는 카르마의 가위를 사용할 수 없습니다.");
                              c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                              return;
                           }

                           MapleInventoryType typexxxxxxxxxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           itemxxxxxxxxxxxxxxxxxxxxxx = c.getPlayer().getInventory(typexxxxxxxxxxxx).getItem((short)slea.readInt());
                           if (itemxxxxxxxxxxxxxxxxxxxxxx != null
                              && !ItemFlag.KARMA_EQ.check(itemxxxxxxxxxxxxxxxxxxxxxx.getFlag())
                              && !ItemFlag.KARMA_USE.check(itemxxxxxxxxxxxxxxxxxxxxxx.getFlag())
                              && (
                                 itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(itemxxxxxxxxxxxxxxxxxxxxxx.getItemId())
                                    || itemId == 5520001 && MapleItemInformationProvider.getInstance().isPKarmaEnabled(itemxxxxxxxxxxxxxxxxxxxxxx.getItemId())
                              )) {
                              int flagxxxx = itemxxxxxxxxxxxxxxxxxxxxxx.getFlag();
                              if (itemxxxxxxxxxxxxxxxxxxxxxx instanceof Equip) {
                                 Equip equip = (Equip)itemxxxxxxxxxxxxxxxxxxxxxx;
                                 if (equip.getKarmaCount() > 0) {
                                    equip.setKarmaCount((byte)(equip.getKarmaCount() - 1));
                                 }
                              }

                              if (typexxxxxxxxxxxx == MapleInventoryType.EQUIP) {
                                 flagxxxx |= ItemFlag.KARMA_EQ.getValue();
                              } else {
                                 flagxxxx |= ItemFlag.KARMA_USE.getValue();
                              }

                              itemxxxxxxxxxxxxxxxxxxxxxx.setFlag(flagxxxx);
                              c.getPlayer().forceReAddItem_NoUpdate(itemxxxxxxxxxxxxxxxxxxxxxx, typexxxxxxxxxxxx);
                              c.getSession()
                                 .writeAndFlush(
                                    CWvsContext.InventoryPacket.updateSpecialItemUse(
                                       itemxxxxxxxxxxxxxxxxxxxxxx, typexxxxxxxxxxxx.getType(), true, c.getPlayer(), false, (byte)0
                                    )
                                 );
                              used = true;
                           }
                           break;
                        case 5521000:
                           MapleInventoryType typexxxxxxxxxx = MapleInventoryType.getByType((byte)slea.readInt());
                           Item itemxxxxxxxxxx = c.getPlayer().getInventory(typexxxxxxxxxx).getItem((short)slea.readInt());
                           if (itemxxxxxxxxxx != null
                              && !ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.check(itemxxxxxxxxxx.getFlag())
                              && !ItemFlag.KARMA_ACC_USE.check(itemxxxxxxxxxx.getFlag())
                              && MapleItemInformationProvider.getInstance().isShareTagEnabled(itemxxxxxxxxxx.getItemId())) {
                              int flagxxxx = itemxxxxxxxxxx.getFlag();
                              if (ItemFlag.POSSIBLE_TRADING.check(flagxxxx)) {
                                 flagxxxx -= ItemFlag.POSSIBLE_TRADING.getValue();
                              } else if (typexxxxxxxxxx == MapleInventoryType.EQUIP) {
                                 flagxxxx |= ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.getValue();
                              } else {
                                 flagxxxx |= ItemFlag.KARMA_ACC_USE.getValue();
                              }

                              itemxxxxxxxxxx.setFlag(flagxxxx);
                              c.getPlayer().forceReAddItem_NoUpdate(itemxxxxxxxxxx, typexxxxxxxxxx);
                              c.getSession()
                                 .writeAndFlush(
                                    CWvsContext.InventoryPacket.updateSpecialItemUse(
                                       itemxxxxxxxxxx, typexxxxxxxxxx.getType(), true, c.getPlayer(), false, (byte)0
                                    )
                                 );
                              used = true;
                           }
                           break;
                        case 5700000:
                           slea.skip(8);
                           if (c.getPlayer().getAndroid() == null) {
                              c.getPlayer().dropMessage(1, "장착중인 안드로이드가 없어 작명 할 수 없습니다.");
                           } else {
                              String nName = slea.readMapleAsciiString();

                              for (String zxxxxxxx : GameConstants.RESERVED) {
                                 if (c.getPlayer().getAndroid().getName().indexOf(zxxxxxxx) != -1 || nName.indexOf(zxxxxxxx) != -1) {
                                    break;
                                 }
                              }

                              c.getPlayer().getAndroid().setName(nName);
                              c.getPlayer().setAndroid(c.getPlayer().getAndroid());
                              used = true;
                           }
                           break;
                        case 5781002:
                           int uniqueidx = (int)slea.readLong();
                           int color = slea.readInt();
                           MaplePet petxxxx = c.getPlayer().getPet(0L);
                           int slo = 0;
                           label1894:
                           if (petxxxx != null) {
                              if (petxxxx.getUniqueId() != uniqueidx) {
                                 petxxxx = c.getPlayer().getPet(1L);
                                 int var225 = 1;
                                 if (petxxxx == null) {
                                    break label1894;
                                 }

                                 if (petxxxx.getUniqueId() != uniqueidx) {
                                    petxxxx = c.getPlayer().getPet(2L);
                                    var225 = 2;
                                    if (petxxxx == null || petxxxx.getUniqueId() != uniqueidx) {
                                       break label1894;
                                    }
                                 }
                              }

                              petxxxx.setColor(color);
                              c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.showPet(c.getPlayer(), petxxxx, false, false, 0), true);
                           }
                           break;
                        default:
                           if (itemId / 10000 == 512) {
                              MapleItemInformationProvider iix = MapleItemInformationProvider.getInstance();
                              String msgx = iix.getMsg(itemId);
                              String ourMsg = slea.readMapleAsciiString();
                              c.getPlayer().getMap().startMapEffect(ourMsg, itemId);
                              int buff = iix.getStateChangeItem(itemId);
                              if (buff != 0) {
                                 for (MapleCharacter mChar : c.getPlayer().getMap().getCharactersThreadsafe()) {
                                    iix.getItemEffect(buff).applyTo(mChar);
                                 }
                              }

                              used = true;
                           } else if (itemId / 10000 == 510) {
                              c.getPlayer().getMap().startJukebox(c.getPlayer().getName(), itemId);
                              used = true;
                           } else if (itemId / 10000 == 520) {
                              int mesars = MapleItemInformationProvider.getInstance().getMeso(itemId);
                              if (mesars > 0 && c.getPlayer().getMeso() < Integer.MAX_VALUE - mesars) {
                                 used = true;
                                 if (Math.random() > 0.1) {
                                    int gainmes = Randomizer.nextInt(mesars);
                                    c.getPlayer().gainMeso(gainmes, false);
                                    c.getSession().writeAndFlush(CSPacket.sendMesobagSuccess(gainmes));
                                 } else {
                                    c.getSession().writeAndFlush(CSPacket.sendMesobagFailed(false));
                                 }
                              }
                           } else if (itemId / 10000 == 562) {
                              if (UseSkillBook(slot, itemId, c, c.getPlayer())) {
                                 c.getPlayer().gainSP(1);
                              }
                           } else if (itemId / 10000 == 553) {
                              UseRewardItem(slot, itemId, c, c.getPlayer());
                           } else if (itemId / 10000 == 524) {
                              for (MaplePet petxx : c.getPlayer().getPets()) {
                                 if (petxx != null && petxx.canConsume(itemId)) {
                                    int petindex = c.getPlayer().getPetIndex(petxx);
                                    petxx.setFullness(100);
                                    if (petxx.getCloseness() < 30000) {
                                       if (petxx.getCloseness() + 100 > 30000) {
                                          petxx.setCloseness(30000);
                                       } else {
                                          petxx.setCloseness(petxx.getCloseness() + 100);
                                       }

                                       if (petxx.getCloseness() >= GameConstants.getClosenessNeededForLevel(petxx.getLevel() + 1)) {
                                          petxx.setLevel(petxx.getLevel() + 1);
                                          PetLevelUp e3 = new PetLevelUp(c.getPlayer().getId(), 0, c.getPlayer().getPetIndex(petxx));
                                          c.getSession().writeAndFlush(e3.encodeForLocal());
                                          c.getPlayer().getMap().broadcastMessage(c.getPlayer(), e3.encodeForRemote(), false);
                                       }
                                    }

                                    c.getSession()
                                       .writeAndFlush(
                                          PetPacket.updatePet(
                                             c.getPlayer(),
                                             petxx,
                                             c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(petxx.getInventoryPosition()),
                                             false,
                                             c.getPlayer().getPetLoot()
                                          )
                                       );
                                    c.getPlayer()
                                       .getMap()
                                       .broadcastMessage(
                                          c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte)1, (byte)petindex, true, true), true
                                       );
                                 }
                              }

                              used = true;
                           } else if (itemId / 10000 != 519) {
                              System.out.println("Unhandled CS item : " + itemId);
                              System.out.println(slea.toString(true));
                           }
                     }

                     if (used) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short)1, false, true);
                     }

                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     if (cc) {
                        if (!c.getPlayer().isAlive()
                           || c.getPlayer().getEventInstance() != null
                           || FieldLimitType.ChannelSwitch.check(c.getPlayer().getMap().getFieldLimit())) {
                           c.getPlayer().dropMessage(1, "Auto relog failed.");
                           return;
                        }

                        c.getPlayer().dropMessage(5, "Auto relogging. Please wait.");
                        c.getPlayer().fakeRelog();
                     }
                  }
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void Pickup_Player(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.readInt();
      c.getPlayer().setScrolledPosition((short)0);
      slea.skip(1);
      Point Client_Reportedpos = slea.readPos();
      if (chr != null && chr.getMap() != null) {
         MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);
         if (ob == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            Drop mapitem = (Drop)ob;
            mapitem.getLock().lock();

            try {
               if ((mapitem.getItemId() == 2633855 || mapitem.getItemId() == 2633910) && chr.getMap() instanceof Field_MultiSoccer) {
                  ((Field_MultiSoccer)chr.getMap()).updateBall(chr, mapitem);
                  return;
               }

               if (mapitem.isPickedUp()) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != 1) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getQuest() > 0 && !chr.needQuestItem(mapitem.getQuest(), mapitem.getItemId())) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getOwner() != chr.getId()
                  && (!mapitem.isPlayerDrop() && mapitem.getOwnType() == 0 || mapitem.isPlayerDrop() && chr.getMap().getEverlast())
                  && chr.getMap().getId() != 921170050
                  && chr.getMap().getId() != 921170100) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (chr.getTrade() != null) {
                  return;
               }

               if (!mapitem.isPlayerDrop()
                  && mapitem.getOwnType() == 1
                  && mapitem.getOwner() != chr.getId()
                  && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2431835) {
                  int rd = Randomizer.nextInt(2);
                  if (rd == 0) {
                     chr.getStat().setHp(chr.getStat().getCurrentMaxHp(), chr);
                     c.getPlayer().updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                  } else {
                     chr.getStat().setMp(chr.getStat().getCurrentMaxMp(chr), chr);
                     c.getPlayer().updateSingleStat(MapleStat.MP, chr.getStat().getMp());
                  }

                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2431850) {
                  chr.getStat().setHp(chr.getStat().getCurrentMaxHp(), chr);
                  c.getPlayer().updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                  chr.getStat().setMp(chr.getStat().getCurrentMaxMp(chr), chr);
                  c.getPlayer().updateSingleStat(MapleStat.MP, chr.getStat().getMp());
                  MapleItemInformationProvider.getInstance().getItemEffect(2002093).applyTo(chr);
                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2436496) {
                  if (mapitem.getOwnerID() == c.getPlayer().getId()) {
                     c.getPlayer().gainExpLong(mapitem.getExp(), false, true);
                     removeItem(chr, mapitem, ob, false);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2636420) {
                  c.getPlayer().gainSolErdaStrength(10);
                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2636421) {
                  c.getPlayer().gainSolErdaStrength(200);
                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2636422) {
                  c.getPlayer().gainSolErdaStrength(500);
                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 4310229 && !DBConfig.isGanglim) {
                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.isCollisionPickUp()) {
                  if (c.getPlayer().getMap() instanceof Field_ReceivingTreasure) {
                     Field_ReceivingTreasure f = (Field_ReceivingTreasure)c.getPlayer().getMap();
                     f.catchItem(mapitem.getItemId());
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getExp() > 0L) {
                     c.getPlayer().gainExp(mapitem.getExp(), true, true, false);
                     ExpEffect e = new ExpEffect(c.getPlayer().getId(), (int)mapitem.getExp());
                     c.getSession().writeAndFlush(e.encodeForLocal());
                     removeItem(chr, mapitem, ob, false);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() >= 2432393 && mapitem.getItemId() <= 2432394) {
                     int r = mapitem.getItemId() == 2432393 ? 1 : 2;
                     int meso = Randomizer.rand(10000, 50000);
                     meso *= r;
                     c.getPlayer().gainMeso(meso, true);
                     removeItem(chr, mapitem, ob, false);
                     return;
                  }

                  if (mapitem.getItemId() == 2434851) {
                     removeItem(chr, mapitem, ob, false);
                     chr.pickupLiver();
                     return;
                  }

                  if (mapitem.getItemId() == 2633343) {
                     if (Center.sunShineStorage.addSunShineGuage(1)) {
                        c.getPlayer().dropMessage(5, "블라썸 게이지 : " + Center.sunShineStorage.getSunShineGuage() + " / 1000000");
                     }

                     removeItem(chr, mapitem, ob, false);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2633626) {
                     chr.changeCooldown(80001416, -10000L);
                     chr.changeCooldown(80001417, -10000L);
                     chr.changeCooldown(80003062, -10000L);
                     removeItem(chr, mapitem, ob, false);
                     chr.send(CField.environmentChange("Sound/SoundEff.img/flagRace/flagStar", 5, 100));
                     WZEffectBased e = new WZEffectBased(chr.getId(), "Effect/BasicEff.img/flagStar");
                     chr.send(e.encodeForLocal());
                     chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
                     chr.send(CWvsContext.enableActions(chr));
                     return;
                  }

                  if (mapitem.getItemId() == 2432395) {
                     Item item = new Item(2000005, (short)0, (short)1, 0);
                     MapleInventoryManipulator.addFromDrop(c, item, true, mapitem.getDropper() instanceof MapleMonster, true);
                     removeItem(chr, mapitem, ob, false);
                  }

                  if (mapitem.getItemId() >= 2432391 && mapitem.getItemId() <= 2432395) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }
               }

               if (mapitem.getItemId() == 2437606 || mapitem.getItemId() == 2437607 || mapitem.getItemId() >= 2437659 && mapitem.getItemId() <= 2437664) {
                  if (chr.getMap() instanceof Field_Papulatus) {
                     Field_Papulatus f = (Field_Papulatus)chr.getMap();
                     switch (mapitem.getItemId()) {
                        case 2437606:
                           chr.multiplierBuffTime("TimeCurse", 2.0);
                           break;
                        case 2437607:
                           chr.multiplierBuffTime("TimeCurse", 0.5);
                           break;
                        case 2437659:
                           f.addWheelTime(0, 10);
                           break;
                        case 2437660:
                           f.addWheelTime(0, 30);
                           break;
                        case 2437661:
                           f.addWheelTime(0, 50);
                           break;
                        case 2437662:
                           f.addWheelTime(2, 0);
                           break;
                        case 2437663:
                           f.addWheelTime(4, 0);
                           break;
                        case 2437664:
                           f.addWheelTime(9, 0);
                     }
                  }

                  removeItem(chr, mapitem, ob, false);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               if (mapitem.getItemId() == 2002058 && chr.getMap() instanceof Field_Arkaium) {
                  Integer stack = chr.getBuffedValue(SecondaryStatFlag.DeathMark);
                  if (stack != null) {
                     if (stack <= 1) {
                        chr.dispelDebuff(SecondaryStatFlag.DeathMark);
                     } else {
                        long till = chr.getSecondaryStat().getTill(SecondaryStatFlag.DeathMark);
                        int remain = (int)(till - System.currentTimeMillis());
                        chr.giveDebuff(SecondaryStatFlag.DeathMark, stack - 1, 0, remain, MobSkillID.DEATHMARK.getVal(), 1, true);
                     }
                  }
               }

               double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
               if (!(Distance > 5000.0) || mapitem.getMeso() <= 0 && mapitem.getItemId() == 4001025) {
                  if (chr.getPosition().distanceSq(mapitem.getPosition()) > 640000.0) {
                     chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_SERVER);
                  }
               } else {
                  chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_CLIENT, String.valueOf(Distance));
               }

               if (mapitem.getMeso() > 0) {
                  int mesoG = chr.getStat().mesoG;
                  if (mapitem.isPlayerDrop()) {
                     mesoG = 0;
                  }

                  if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                     List<MapleCharacter> toGive = new LinkedList<>();
                     int splitMeso = mapitem.getMeso() * 40 / 100;

                     for (PartyMemberEntry z : chr.getParty().getPartyMemberList()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                           toGive.add(m);
                        }
                     }

                     for (MapleCharacter m : toGive) {
                        int mesos = splitMeso / toGive.size() + (m.getStat().hasPartyBonus ? (int)(mapitem.getMeso() / 20.0) : 0);
                        if (mesoG > 0) {
                           mesos += mesoG;
                        }

                        m.gainMeso(mesos, true, false, true, mesoG);
                     }

                     int mesos = mapitem.getMeso() - splitMeso;
                     if (mesoG > 0) {
                        mesos += mesoG;
                     }

                     chr.gainMeso(mesos, true, false, true, mesoG);
                  } else {
                     int mesos = mapitem.getMeso();
                     if (mesoG > 0) {
                        mesos += mesoG;
                     }

                     if (!mapitem.isPlayerDrop() && ServerConstants.useAchievement) {
                        AchievementFactory.checkPickupMobRewardMeso(chr, mesos);
                     }

                     chr.gainMeso(mesos, true, false, true, mesoG);
                  }

                  removeItem(chr, mapitem, ob, false);
                  return;
               } else if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId())) {
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  c.getPlayer().dropMessage(5, "This item cannot be picked up.");
               } else if (c.getPlayer().inPVP() && Integer.parseInt(c.getPlayer().getEventInstance().getProperty("ice")) == c.getPlayer().getId()) {
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               } else if (useItem(c, mapitem.getItemId())) {
                  removeItem(c.getPlayer(), mapitem, ob, false);
                  if (mapitem.getItemId() / 10000 == 291) {
                     c.getPlayer().getMap().broadcastMessage(CField.getCapturePosition(c.getPlayer().getMap()));
                     c.getPlayer().getMap().broadcastMessage(CField.resetCapture());
                  }
               } else if (mapitem.getItemId() == 2431174) {
                  int hounerexp = Randomizer.rand(1, 100);
                  chr.setInnerExp(chr.getInnerExp() + hounerexp);
                  c.getSession().writeAndFlush(CWvsContext.updateAzwanFame(chr.getInnerExp()));
                  c.getSession().writeAndFlush(CWvsContext.enableActions(chr, true));
                  removeItem(c.getPlayer(), mapitem, ob, false);
               } else if (DBConfig.isGanglim && mapitem.getItemId() == 2632800) {
                  int a = c.getPlayer().getKeyValue(100711, "point") + 1;
                  c.getPlayer().setKeyValue(100711, "point", a + "");
                  c.getPlayer()
                     .updateInfoQuest(
                        100711,
                        "point="
                           + c.getPlayer().getKeyValue(100711, "point")
                           + ";sum=0;date="
                           + GameConstants.getCurrentDate_NoTime()
                           + ";today=0;total=0;lock=0"
                     );
                  c.getSession().writeAndFlush(CWvsContext.enableActions(chr, true));
                  removeItem(chr, mapitem, ob, false);
               } else if (c.getPlayer().getMap().getId() != 921170050 && c.getPlayer().getMap().getId() != 921170100) {
                  if (mapitem.getItemId() / 10000 != 291
                     && MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                     if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true);
                     }

                     if (!mapitem.isPlayerDrop() && ServerConstants.useAchievement) {
                        AchievementFactory.checkPickupMobRewardItem(chr, Arrays.asList(mapitem.getItem()));
                     }

                     if (mapitem.getDropper() instanceof Reactor) {
                        AchievementFactory.checkPickupReactorRewardItem(chr, mapitem.getItemId());
                     }

                     MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster, true);
                     c.getPlayer().setWeeklyQuestCount(0, mapitem.getItemId());
                     removeItem(chr, mapitem, ob, false);
                  } else {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  }
               } else if (mapitem.getItem().getItemId() >= 2435856 && mapitem.getItem().getItemId() <= 2435872) {
                  int itemID = c.getPlayer().getMutoPickupItemID();
                  if (itemID == 0) {
                     c.getPlayer().setMutoPickupItemID(mapitem.getItemId());
                     c.getPlayer().setMutoPickupItemQ(1);
                  } else if (mapitem.getItem().getItemId() == c.getPlayer().getMutoPickupItemID()) {
                     int quantity = c.getPlayer().getMutoPickupItemQ() + 1;
                     c.getPlayer().setMutoPickupItemQ(quantity);
                  } else {
                     c.getPlayer().setMutoPickupItemID(mapitem.getItem().getItemId());
                     c.getPlayer().setMutoPickupItemQ(1);
                  }

                  removeItem(chr, mapitem, ob, false);
                  c.getPlayer()
                     .getMap()
                     .broadcastMessage(
                        new HungryMuto.PickupItemUpdate(c.getPlayer().getId(), c.getPlayer().getMutoPickupItemID(), c.getPlayer().getMutoPickupItemQ())
                           .encode()
                     );
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               }
            } finally {
               mapitem.getLock().unlock();
            }
         }
      }
   }

   public static final void SelectReturnScroll(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Equip beforeItem = player.returnScroll;
         if (beforeItem != null) {
            Equip equip = null;
            if (beforeItem.getPosition() > 0) {
               equip = (Equip)player.getInventory(MapleInventoryType.EQUIP).getItem(player.returnScroll.getPosition());
            } else {
               equip = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem(player.returnScroll.getPosition());
            }

            int tick = slea.readInt();
            boolean selectBefore = slea.readByte() > 0;
            if (selectBefore) {
               equip.set(beforeItem);
            }

            player.returnScroll = null;
            player.send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
            if (ItemFlag.RETURN_SCROLLED.check(beforeItem.getFlag())) {
               beforeItem.setFlag((short)(beforeItem.getFlag() - ItemFlag.RETURN_SCROLLED.getValue()));
            }
         }
      }
   }

   public static final void Pickup_Pet(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null) {
         if (!c.getPlayer().inPVP()) {
            c.getPlayer().setScrolledPosition((short)0);
            byte petz = (byte)slea.readInt();
            MaplePet pet = chr.getPet(petz);
            slea.skip(1);
            slea.readInt();
            Point Client_Reportedpos = slea.readPos();
            MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);
            if (ob != null && pet != null) {
               Drop mapitem = (Drop)ob;
               mapitem.getLock().lock();

               try {
                  if (mapitem.isPickedUp()) {
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     return;
                  }

                  if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                     return;
                  }

                  if (chr.getTrade() != null) {
                     return;
                  }

                  if (mapitem.getOwner() != chr.getId()
                     && (!mapitem.isPlayerDrop() && mapitem.getOwnType() == 0 || mapitem.isPlayerDrop() && chr.getMap().getEverlast())) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (!mapitem.isPlayerDrop()
                     && mapitem.getOwnType() == 1
                     && mapitem.getOwner() != chr.getId()
                     && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2002058
                     || mapitem.getItemId() == 2437606
                     || mapitem.getItemId() == 2437607
                     || mapitem.getItemId() >= 2437659 && mapitem.getItemId() <= 2437664) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != 1) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getQuest() > 0 && !chr.needQuestItem(mapitem.getQuest(), mapitem.getItemId())) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2436496) {
                     if (mapitem.getOwnerID() == c.getPlayer().getId()) {
                        c.getPlayer().gainExpLong(mapitem.getExp(), false, true);
                        removeItem(chr, mapitem, ob, true);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 4310229 && !DBConfig.isGanglim) {
                     removeItem(chr, mapitem, ob, true);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2632800) {
                     int a = c.getPlayer().getKeyValue(100711, "point") + mapitem.getItem().getQuantity();
                     c.getPlayer().setKeyValue(100711, "point", a + "");
                     c.getPlayer()
                        .updateInfoQuest(
                           100711,
                           "point="
                              + c.getPlayer().getKeyValue(100711, "point")
                              + ";sum=0;date="
                              + GameConstants.getCurrentDate_NoTime()
                              + ";today=0;total=0;lock=0"
                        );
                     removeItem(chr, mapitem, ob, true);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2636420) {
                     c.getPlayer().gainSolErdaStrength(10);
                     removeItem(chr, mapitem, ob, false);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2636421) {
                     c.getPlayer().gainSolErdaStrength(200);
                     removeItem(chr, mapitem, ob, false);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2636422) {
                     c.getPlayer().gainSolErdaStrength(500);
                     removeItem(chr, mapitem, ob, false);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.isCollisionPickUp()) {
                     if (mapitem.getExp() > 0L) {
                        c.getPlayer().gainExp(mapitem.getExp(), true, true, false);
                        ExpEffect e = new ExpEffect(c.getPlayer().getId(), (int)mapitem.getExp());
                        c.getSession().writeAndFlush(e.encodeForLocal());
                        removeItem(chr, mapitem, ob, true);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     }

                     if (mapitem.getItemId() == 2434851) {
                        removeItem(chr, mapitem, ob, true);
                        chr.pickupLiver();
                        return;
                     }

                     if (mapitem.getItemId() == 2633343) {
                        if (Center.sunShineStorage.addSunShineGuage(1)) {
                           c.getPlayer().dropMessage(5, "블라썸 게이지 : " + Center.sunShineStorage.getSunShineGuage() + " / 1000000");
                        }

                        removeItem(chr, mapitem, ob, true);
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }
                  }

                  if (mapitem.getItemId() == 2431835) {
                     int rd = Randomizer.nextInt(2);
                     if (rd == 0) {
                        chr.getStat().setHp(chr.getStat().getCurrentMaxHp(), chr);
                        c.getPlayer().updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                     } else {
                        chr.getStat().setMp(chr.getStat().getCurrentMaxMp(chr), chr);
                        c.getPlayer().updateSingleStat(MapleStat.MP, chr.getStat().getMp());
                     }

                     removeItem_Pet(chr, mapitem, petz);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getItemId() == 2431850) {
                     chr.getStat().setHp(chr.getStat().getCurrentMaxHp(), chr);
                     c.getPlayer().updateSingleStat(MapleStat.HP, chr.getStat().getHp());
                     chr.getStat().setMp(chr.getStat().getCurrentMaxMp(chr), chr);
                     c.getPlayer().updateSingleStat(MapleStat.MP, chr.getStat().getMp());
                     MapleItemInformationProvider.getInstance().getItemEffect(2002093).applyTo(chr);
                     removeItem_Pet(chr, mapitem, petz);
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (mapitem.getMeso() > 0) {
                     int mesoG = chr.getStat().mesoG;
                     if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                        List<MapleCharacter> toGive = new LinkedList<>();
                        int splitMeso = mapitem.getMeso() * 40 / 100;

                        for (PartyMemberEntry z : chr.getParty().getPartyMemberList()) {
                           MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                           if (m != null && m.getId() != chr.getId()) {
                              toGive.add(m);
                           }
                        }

                        for (MapleCharacter m : toGive) {
                           m.gainMeso(splitMeso / toGive.size() + (m.getStat().hasPartyBonus ? (int)(mapitem.getMeso() / 20.0) : 0), true, false);
                        }

                        chr.gainMeso(mapitem.getMeso() - splitMeso + mesoG, true, false, mesoG);
                     } else {
                        if (!mapitem.isPlayerDrop() && ServerConstants.useAchievement) {
                           AchievementFactory.checkPickupMobRewardMeso(chr, mapitem.getMeso() + mesoG);
                        }

                        chr.gainMeso(mapitem.getMeso() + mesoG, true, false, mesoG);
                     }

                     removeItem_Pet(chr, mapitem, petz);
                     return;
                  } else if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  } else if (useItem(c, mapitem.getItemId())) {
                     removeItem_Pet(chr, mapitem, petz);
                  } else if (mapitem.getItemId() == 2431174) {
                     int hounerexp = Randomizer.rand(1, 100);
                     chr.gainHonor(hounerexp, false);
                     removeItem_Pet(c.getPlayer(), mapitem, petz);
                  } else if (DBConfig.isGanglim && mapitem.getItemId() == 2632800) {
                     int a = c.getPlayer().getKeyValue(100711, "point") + 1;
                     c.getPlayer().setKeyValue(100711, "point", a + "");
                     c.getPlayer()
                        .updateInfoQuest(
                           100711,
                           "point="
                              + c.getPlayer().getKeyValue(100711, "point")
                              + ";sum=0;date="
                              + GameConstants.getCurrentDate_NoTime()
                              + ";today=0;total=0;lock=0"
                        );
                     removeItem_Pet(chr, mapitem, petz);
                  } else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                     if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true);
                     }

                     if (!mapitem.isPlayerDrop() && ServerConstants.useAchievement) {
                        AchievementFactory.checkPickupMobRewardItem(chr, Arrays.asList(mapitem.getItem()));
                     }

                     if (mapitem.getDropper() instanceof Reactor) {
                        AchievementFactory.checkPickupReactorRewardItem(chr, mapitem.getItemId());
                     }

                     MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster, false);
                     c.getPlayer().setWeeklyQuestCount(0, mapitem.getItemId());
                     removeItem_Pet(chr, mapitem, petz);
                  }
               } finally {
                  mapitem.getLock().unlock();
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      }
   }

   public static final boolean useItem(MapleClient c, int id) {
      if (GameConstants.isUse(id)) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         SecondaryStatEffect eff = ii.getItemEffect(id);
         if (eff == null) {
            return false;
         }

         if (id / 10000 == 291) {
            boolean area = false;

            for (Rectangle rect : c.getPlayer().getMap().getAreas()) {
               if (rect.contains(c.getPlayer().getTruePosition())) {
                  area = true;
                  break;
               }
            }

            if (!c.getPlayer().inPVP() || c.getPlayer().getTeam() == id - 2910000 && area) {
               return false;
            }
         }

         if (!DBConfig.isGanglim && id == 2432970) {
            int rand = ThreadLocalRandom.current().nextInt(500) + 500;
            c.getPlayer().addHonorExp(rand);
            c.getPlayer().dropMessage(5, rand + " 명성치를 획득했습니다.");
            return true;
         }

         int consumeval = eff.getConsume();
         if (consumeval > 0) {
            if (c.getPlayer().getMapId() != 109090300) {
               consumeItem(c, eff);
               consumeItem(c, ii.getItemEffectEX(id));
               c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, (short)1));
               return true;
            }

            for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
               if (chr != null
                  && (id == 2022163 && c.getPlayer().isCatched == chr.isCatched || (id == 2022165 || id == 2022166) && c.getPlayer().isCatched != chr.isCatched)
                  )
                {
                  if (id == 2022163) {
                     ii.getItemEffect(id).applyTo(chr);
                  } else if (id == 2022166) {
                     chr.giveDebuff(SecondaryStatFlag.Stun, MobSkillFactory.getMobSkill(123, 1));
                  } else if (id == 2022165) {
                     chr.giveDebuff(SecondaryStatFlag.Slow, MobSkillFactory.getMobSkill(126, 1));
                  }
               }
            }

            c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(id, (short)1));
            c.getPlayer().getClient().getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
            return true;
         }
      }

      return false;
   }

   public static final void consumeItem(MapleClient c, SecondaryStatEffect eff) {
      if (eff != null) {
         if (eff.getConsume() == 2) {
            if (c.getPlayer().getParty() != null && c.getPlayer().isAlive()) {
               for (PartyMemberEntry pc : c.getPlayer().getParty().getPartyMemberList()) {
                  MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                  if (chr != null && chr.isAlive()) {
                     eff.applyTo(chr);
                  }
               }
            } else {
               eff.applyTo(c.getPlayer());
            }
         } else if (c.getPlayer().isAlive()) {
            eff.applyTo(c.getPlayer());
         }
      }
   }

   public static final void removeItem_Pet(MapleCharacter chr, Drop mapitem, int pet) {
      mapitem.setPickedUp(true);
      chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), pet));
      chr.getMap().removeMapObject(mapitem);
      if (mapitem.isRandDrop()) {
         chr.getMap().spawnRandDrop();
      }
   }

   private static final void removeItem(MapleCharacter chr, Drop mapitem, MapleMapObject ob, boolean byPet) {
      mapitem.setPickedUp(true);
      chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
      chr.getMap().removeMapObject(ob);
      if (ItemCheck.allItemList.contains(mapitem.getItemId())) {
         AchievementFactory.checkComboKillGetMarble(chr, mapitem.getItemId());
      }

      if (mapitem.isRandDrop()) {
         chr.getMap().spawnRandDrop();
      }

      if (!byPet) {
         StringBuilder sb = new StringBuilder();
         sb.append("아이템 획득 ");
         sb.append(" (획득 방법 : ");
         if (byPet) {
            sb.append("펫 줍기)");
         } else {
            sb.append("직접 줍기)");
         }

         long serialNumber = 0L;
         if (mapitem.getItem() instanceof Equip) {
            sb.append("(장비옵션");
            sb.append(((Equip)mapitem.getItem()).toString());
            sb.append(")");
            serialNumber = ((Equip)mapitem.getItem()).getSerialNumberEquip();
         }

         List<Integer> exceptionList = Arrays.asList(2023484, 2023494, 2023495, 2023669, 2432391, 2432392, 2432393, 2432394, 2432395);
         if (!exceptionList.contains(mapitem.getItemId()) && (mapitem.getMeso() == 0 || mapitem.getItem() != null)) {
            LoggingManager.putLog(
               new PickupLog(
                  chr,
                  chr.getClient().getChannel(),
                  mapitem.getItemId(),
                  mapitem.getItem().getQuantity(),
                  chr.getMapId(),
                  ConsumeLogType.PickupItem.getType(),
                  0L,
                  serialNumber,
                  sb
               )
            );
         }
      }
   }

   public static final void addMedalString(MapleCharacter c, StringBuilder sb) {
      int grade = c.getKeyValue(190823, "grade");
      String[] gradeNameList = new String[]{
         "뉴비", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grand Master", "Challenger", "Overload", "Vega", "Sirius"
      };
      String gradeName = gradeNameList[grade];
      sb.append("<");
      if (c.getKeyValue("MedalString") != null) {
         sb.append(c.getKeyValue("MedalString"));
      } else if (c.getClient().getKeyValue("MedalString") != null) {
         sb.append(c.getClient().getKeyValue("MedalString"));
      } else {
         sb.append(gradeName);
      }

      sb.append("> ");
   }

   private static final boolean getIncubatedItems(MapleClient c, int itemId) {
      if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 2
         && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 2
         && c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 2) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int id1 = RandomRewards.getPeanutReward();
         int id2 = RandomRewards.getPeanutReward();

         while (!ii.itemExists(id1)) {
            id1 = RandomRewards.getPeanutReward();
         }

         while (!ii.itemExists(id2)) {
            id2 = RandomRewards.getPeanutReward();
         }

         c.getSession().writeAndFlush(CWvsContext.getIncubatorResult(id1, (short)1, id2, (short)1, itemId));
         MapleInventoryManipulator.addById(c, id1, (short)1, ii.getName(itemId) + " on " + FileoutputUtil.CurrentReadable_Date());
         MapleInventoryManipulator.addById(c, id2, (short)1, ii.getName(itemId) + " on " + FileoutputUtil.CurrentReadable_Date());
         return true;
      } else {
         c.getPlayer().dropMessage(5, "Please make room in your inventory.");
         return false;
      }
   }

   public static final void OwlMinerva(PacketDecoder slea, MapleClient c) {
      byte slot = (byte)slea.readShort();
      int itemid = slea.readInt();
      Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
      if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2310000) {
         int itemSearch = slea.readInt();
         List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
         if (hms.size() > 0) {
            c.getSession().writeAndFlush(CWvsContext.getOwlSearched(itemSearch, hms));
            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
         } else {
            c.getPlayer().dropMessage(1, "아이템을 찾을 수 없습니다.");
         }
      }

      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static final void Owl(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
         c.getSession().writeAndFlush(CWvsContext.getOwlOpen());
      }
   }

   public static final void OwlWarp(PacketDecoder slea, MapleClient c) {
      if (!c.getPlayer().isAlive()) {
         c.getSession().writeAndFlush(CWvsContext.getOwlMessage(4));
      } else if (c.getPlayer().getTrade() != null) {
         c.getSession().writeAndFlush(CWvsContext.getOwlMessage(7));
      } else {
         if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
            int id = slea.readInt();
            slea.skip(1);
            int map = slea.readInt();
            if (map >= 910000001 && map <= 910000022) {
               c.getSession().writeAndFlush(CWvsContext.getOwlMessage(0));
               Field mapp = c.getChannelServer().getMapFactory().getMap(map);
               c.getPlayer().changeMap(mapp, mapp.getPortal(0));
               HiredMerchant merchant = null;
               switch (2) {
                  case 0:
                     for (MapleMapObject obxx : mapp.getAllHiredMerchantsThreadsafe()) {
                        if (obxx instanceof IMaplePlayerShop) {
                           IMaplePlayerShop ips = (IMaplePlayerShop)obxx;
                           if (ips instanceof HiredMerchant) {
                              HiredMerchant merch = (HiredMerchant)ips;
                              if (merch.getOwnerId() == id) {
                                 merchant = merch;
                                 break;
                              }
                           }
                        }
                     }
                     break;
                  case 1:
                     for (MapleMapObject obx : mapp.getAllHiredMerchantsThreadsafe()) {
                        if (obx instanceof IMaplePlayerShop) {
                           IMaplePlayerShop ips = (IMaplePlayerShop)obx;
                           if (ips instanceof HiredMerchant) {
                              HiredMerchant merch = (HiredMerchant)ips;
                              if (merch.getStoreId() == id) {
                                 merchant = merch;
                                 break;
                              }
                           }
                        }
                     }
                     break;
                  default:
                     MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                     if (ob instanceof IMaplePlayerShop) {
                        IMaplePlayerShop ips = (IMaplePlayerShop)ob;
                        if (ips instanceof HiredMerchant) {
                           merchant = (HiredMerchant)ips;
                        }
                     }
               }

               if (merchant != null) {
                  if (merchant.isOwner(c.getPlayer())) {
                     merchant.setOpen(false);
                     merchant.removeAllVisitors(16, 0);
                     c.getPlayer().setPlayerShop(merchant);
                     c.getSession().writeAndFlush(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                  } else if (merchant.isOpen() && merchant.isAvailable()) {
                     if (merchant.getFreeSlot() == -1) {
                        c.getPlayer().dropMessage(1, "You can't enter the room due to full capacity.");
                     } else if (merchant.isInBlackList(c.getPlayer().getName())) {
                        c.getPlayer().dropMessage(1, "You may not enter this store.");
                     } else {
                        c.getPlayer().setPlayerShop(merchant);
                        merchant.addVisitor(c.getPlayer());
                        c.getSession().writeAndFlush(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                     }
                  } else {
                     c.getPlayer().dropMessage(1, "The owner of the store is currently undergoing store maintenance. Please try again in a bit.");
                  }
               } else {
                  c.getPlayer().dropMessage(1, "The room is already closed.");
               }
            } else {
               c.getSession().writeAndFlush(CWvsContext.getOwlMessage(23));
            }
         } else {
            c.getSession().writeAndFlush(CWvsContext.getOwlMessage(23));
         }
      }
   }

   public static final void TeleRock(PacketDecoder slea, MapleClient c) {
      short slot = slea.readShort();
      int itemId = slea.readInt();
      Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
      if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && itemId / 10000 == 232) {
         boolean used = UseTeleRock(slea, c, itemId);
         if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
         }

         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final boolean UseTeleRock(PacketDecoder slea, MapleClient c, int itemId) {
      boolean used = false;
      if (slea.readByte() == 0) {
         int targetID = slea.readInt();
         Field target = c.getChannelServer().getMapFactory().getMap(targetID);
         if (target != null
            && (
               itemId == 5041000 && c.getPlayer().isRockMap(target.getId())
                  || (itemId == 5040000 || itemId == 5040001) && c.getPlayer().isRegRockMap(target.getId())
                  || (itemId == 5040004 || itemId == 5041001) && (c.getPlayer().isHyperRockMap(target.getId()) || GameConstants.isHyperTeleMap(target.getId()))
            )) {
            if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
               && !FieldLimitType.VipRock.check(target.getFieldLimit())
               && !c.getPlayer().isInBlockedMap()) {
               c.getPlayer().changeMap(target, target.getPortal(0));
               used = true;
            } else {
               c.getPlayer().dropMessage(1, "You cannot go to that place.");
            }
         } else {
            c.getPlayer().dropMessage(1, "You cannot go to that place.");
         }
      } else {
         String name = slea.readMapleAsciiString();
         MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
         if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null) {
            if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
               || FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit())
               || victim.isInBlockedMap()
               || c.getPlayer().isInBlockedMap()) {
               c.getPlayer().dropMessage(1, "You cannot go to that place.");
            } else if (itemId != 5041000 && itemId != 5040004 && itemId != 5041001 && victim.getMapId() / 100000000 != c.getPlayer().getMapId() / 100000000) {
               c.getPlayer().dropMessage(1, "You cannot go to that place.");
            } else {
               c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
               used = true;
            }
         } else {
            c.getPlayer().dropMessage(1, "(" + name + ") is currently difficult to locate, so the teleport will not take place.");
         }
      }

      return used;
   }

   public static void UsePetLoot(PacketDecoder slea, MapleClient c) {
      slea.readInt();
      short mode = slea.readShort();
      c.getPlayer().setPetLoot(mode == 1);

      for (int i = 0; i < c.getPlayer().getPets().length; i++) {
         if (c.getPlayer().getPet(i) != null) {
            c.getSession()
               .writeAndFlush(
                  PetPacket.updatePet(
                     c.getPlayer(),
                     c.getPlayer().getPet(i),
                     c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(c.getPlayer().getPet(i).getInventoryPosition()),
                     false,
                     c.getPlayer().getPetLoot()
                  )
               );
         }
      }

      c.getSession().writeAndFlush(PetPacket.updatePetLootStatus(mode));
   }

   public static void UsePetSkillChange(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      slea.readInt();
      int flag = slea.readInt();
      if (flag == 128) {
         byte use = slea.readByte();
         chr.updateOneInfo(12334, "preventAutoBuff", String.valueOf((int)use));
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         chr.dropMessage(5, "지금은 사용하지 않는 기능입니다.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         c.getSession().writeAndFlush(PetPacket.updatePetAutoPetFood());
      }
   }

   public static void SelectPQReward(PacketDecoder slea, MapleClient c) {
      slea.skip(1);
      int randval = RandomRewards.getRandomReward();
      short quantity = (short)Randomizer.rand(1, 10);
      MapleInventoryManipulator.addById(c, randval, quantity, "Reward item: " + randval + " on " + FileoutputUtil.CurrentReadable_Date());
      if (c.getPlayer().getMapId() == 100000203) {
         Field map = c.getChannelServer().getMapFactory().getMap(960000000);
         c.getPlayer().changeMap(map, map.getPortal(0));
      } else {
         c.getPlayer().fakeRelog();
      }

      c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(randval, quantity, true));
   }

   public static void loadZeroEquip(MapleClient c) {
      MapleInventory iv = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
      Collection<Item> equippedC = iv.list();
      List<Item> equipped = new ArrayList<>(iv.list().size());

      for (Item item : equippedC) {
         equipped.add(item);
      }

      for (Item item : equipped) {
         if (item.getPosition() <= -1500 && item.getPosition() > -1600) {
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(item));
         }
      }

      for (Item itemx : equipped) {
         Equip eq = (Equip)itemx;
         if (eq.getStarForce() == 1) {
            Equip neweq = (Equip)eq.copy();
            neweq.setPosition(GameConstants.linkedZeroSlot(eq.getPosition()));
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(neweq));
         }
      }
   }

   public static void resetZeroWeapon(MapleCharacter chr) {
      Equip newa = (Equip)MapleItemInformationProvider.getInstance()
         .getEquipById(chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11).getItemId());
      Equip newb = (Equip)MapleItemInformationProvider.getInstance()
         .getEquipById(chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10).getItemId());
      ((Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11)).set(newa);
      ((Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10)).set(newb);
      chr.getClient()
         .getSession()
         .writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11)));
      chr.getClient()
         .getSession()
         .writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10)));
      chr.dropMessage(5, "제로의 장비는 파괴되는대신 처음 상태로 되돌아갑니다");
   }

   public static void UseNameChangeCoupon(PacketDecoder slea, MapleClient c) {
      short slot = slea.readShort();
      int itemId = slea.readInt();
      Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
      if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
         c.setNameChangeEnable((byte)1);
         MapleCharacter.updateNameChangeCoupon(c);
         c.getSession().writeAndFlush(CWvsContext.nameChangeUI(true));
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, false);
      } else {
         c.getSession().writeAndFlush(CWvsContext.nameChangeUI(false));
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static final void UseSoulEnchanter(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.skip(4);
      short useSlot = slea.readShort();
      short slot = slea.readShort();
      MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
      Item enchanter = useInventory.getItem(useSlot);
      Item equip;
      if (slot == -11) {
         equip = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
      } else {
         equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
      }

      Equip nEquip = (Equip)equip;
      int scrollID = enchanter.getItemId() - 2589999;
      nEquip.setSoulEnchanter((short)scrollID);
      c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(enchanter, nEquip, false, false, slot < 0));
      chr.getMap().broadcastMessage(chr, CField.showEnchanterEffect(chr.getId(), (byte)1), true);
      MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, enchanter.getItemId(), 1, true, false);
      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
   }

   public static final void UseSoulScroll(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      slea.skip(4);
      short useSlot = slea.readShort();
      short targetSlot = slea.readShort();
      MapleInventory useInventory = chr.getInventory(MapleInventoryType.USE);
      Item soul = useInventory.getItem(useSlot);
      if (soul != null) {
         int soulItemID = soul.getItemId();
         int skillID = soulItemID - 2590999;
         Item equip = null;
         if (targetSlot == -11) {
            equip = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(targetSlot);
         } else {
            equip = chr.getInventory(MapleInventoryType.EQUIP).getItem(targetSlot);
         }

         if (equip != null) {
            objects.users.enchant.skilloption.SkillEntry entry = SkillOption.getSkillOption(skillID);
            if (entry == null) {
               System.out.println("[오류] 소울 옵션 데이터를 불러오는데 실패했습니다. (사용된 소울 ID : " + soulItemID + ")");
            } else {
               if (chr.isEquippedSoulWeapon()) {
                  chr.changeSkillLevel(chr.getEquippedSoulSkill(), -1, 0);
               }

               Equip eq = (Equip)equip;
               MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
               if (ii.getReqLevel(eq.getItemId()) < entry.getReqLevel()) {
                  System.out.println("[오류] 소울 옵션을 부여하려는 장비에 장착 제한 레벨보다 소울 옵션 제한 레벨이 더 높습니다.");
               } else {
                  TempOptionEntry tempOptionEntry = entry.getOnePickTempOptionEntry();
                  eq.setSoulName(GameConstants.getSoulName(soulItemID));
                  eq.setSoulPotential((short)tempOptionEntry.getId());
                  eq.setSoulSkill(entry.getSkillID());
                  Equip zeroWeapon = null;
                  if (GameConstants.isZero(chr.getJob())) {
                     if (targetSlot == -11) {
                        zeroWeapon = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
                     } else if (targetSlot == -10) {
                        zeroWeapon = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
                     }
                  }

                  if (zeroWeapon != null) {
                     zeroWeapon.setSoulName(eq.getSoulName());
                     zeroWeapon.setSoulPotential(eq.getSoulPotential());
                     zeroWeapon.setSoulSkill(eq.getSoulSkill());
                     c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateEquipSlot(zeroWeapon));
                  }

                  chr.send(CWvsContext.InventoryPacket.scrolledItem(soul, eq, false, false, targetSlot < 0));
                  chr.getMap().broadcastMessage(chr, CField.showSoulScrollEffect(chr.getId(), (byte)1, false), true);
                  chr.checkSoulSkillLevel();
                  MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, soulItemID, 1, true, false);
                  chr.send(CWvsContext.enableActions(chr));
               }
            }
         }
      }
   }

   public static void UseGoldenHammer(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      byte slot = (byte)slea.readInt();
      int itemId = slea.readInt();
      slea.skip(4);
      byte victimslot = (byte)slea.readInt();
      Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
      Equip victim = (Equip)c.getPlayer().getInventory(victimslot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(victimslot);
      Equip victim2 = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
      if (toUse != null && toUse.getItemId() == itemId && toUse.getQuantity() >= 1) {
         c.getPlayer().setHammerResult(false);
         c.getSession().writeAndFlush(CSPacket.ViciousHammer(true, false));
         victim.setViciousHammer((byte)1);
         if (GameConstants.isZero(c.getPlayer().getJob()) && GameConstants.isBetaWeapon(victim.getItemId())) {
            victim2.setViciousHammer((byte)1);
         }

         if ((itemId != 2470001 && itemId != 2470002 && itemId != 2470009 && itemId != 2470013 || Randomizer.nextInt(100) <= 50)
            && itemId != 2470003
            && itemId != 2470007
            && itemId != 2470010) {
            if (itemId == 2470000) {
               victim.setUpgradeSlots((byte)(victim.getUpgradeSlots() + 1));
               if (GameConstants.isZero(c.getPlayer().getJob()) && GameConstants.isBetaWeapon(victim.getItemId())) {
                  victim2.setUpgradeSlots((byte)(victim2.getUpgradeSlots() + 1));
               }

               c.getPlayer().setHammerResult(true);
            }
         } else {
            victim.setUpgradeSlots((byte)(victim.getUpgradeSlots() + 1));
            if (GameConstants.isZero(c.getPlayer().getJob()) && GameConstants.isBetaWeapon(victim.getItemId())) {
               victim2.setUpgradeSlots((byte)(victim2.getUpgradeSlots() + 1));
            }

            c.getPlayer().setHammerResult(true);
         }

         if (GameConstants.isZero(c.getPlayer().getJob()) && GameConstants.isBetaWeapon(victim.getItemId())) {
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(toUse, victim, false, true, victimslot < 0));
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(toUse, victim2, false, true, victimslot < 0));
            c.getSession().writeAndFlush(CSPacket.ViciousHammer(false, c.getPlayer().isHammerResult()));
         } else {
            c.getSession()
               .writeAndFlush(CWvsContext.InventoryPacket.addInventorySlot(victimslot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, victim));
         }

         StringBuilder sb = new StringBuilder("황금 망치 사용 (");
         sb.append("계정 : ");
         sb.append(c.getAccountName());
         sb.append(", 캐릭터 : ");
         sb.append(c.getPlayer().getName());
         sb.append(" (정보 : ");
         sb.append(victim.toString());
         sb.append("))");
         LoggingManager.putLog(
            new EnchantLog(
               c.getPlayer(),
               itemId,
               victim.getItemId(),
               victim.getSerialNumberEquip(),
               EnchantLogType.GoldenHammer.getType(),
               EnchantLogResult.Success.getType(),
               sb
            )
         );
         MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short)1, true);
      } else {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   public static void UseGoldenHammerResult(PacketDecoder slea, MapleClient c) {
      slea.skip(8);
      c.getSession().writeAndFlush(CSPacket.ViciousHammer(false, c.getPlayer().isHammerResult()));
   }

   public static void ExItemUpgradeItemUseRequest(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      short scrollItemPos = slea.readShort();
      short equipPos = slea.readShort();
      Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(scrollItemPos);
      Item toItem = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equipPos);
      if (item != null) {
         if (toItem != null) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (item.getItemId() / 100 == 20487 && GameConstants.IsExNewScroll(item.getItemId())) {
               BonusStatPlaceType placeType = BonusStatPlaceType.LevelledRebirthFlame;
               if (GameConstants.IsPowerfulRebirthFlame(item.getItemId())) {
                  placeType = BonusStatPlaceType.PowerfulRebirthFlame;
               } else if (GameConstants.IsEternalRebirthFlame(item.getItemId())) {
                  placeType = BonusStatPlaceType.EternalRebirthFlame;
               }

               if (Randomizer.isSuccess(ii.getSuccess(item.getItemId(), c.getPlayer(), toItem)) && BonusStat.resetBonusStat(toItem, placeType)) {
               }
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte)scrollItemPos, (short)1, true);
            c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(item, toItem, false, false, false));
         }
      }
   }

   public static final void CharSlotIncItemUseRequest(PacketDecoder slea, MapleClient c) {
      short useItemSlot = slea.readShort();
      int useItemID = slea.readInt();
      Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(useItemSlot);
      if (item != null) {
         if (item.getItemId() == useItemID) {
            if (c.gainCharacterSlot()) {
               c.getPlayer().dropMessage(5, "캐릭터 슬롯이 증가되었습니다.");
               c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
               MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte)useItemSlot, (short)1, true);
            } else {
               c.getPlayer().dropMessage(1, "캐릭터 슬롯을 더 이상 늘릴 수 없습니다.");
               c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
            }
         }
      }
   }

   public static void userSoulEffectRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         byte req = slea.readByte();
         player.updateOneInfo(26535, "effect", String.valueOf((int)req));
         player.getMap().broadcastMessage(CField.setSoulEffect(c.getPlayer(), req));
      }
   }

   public static final void Open_RingBox(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null) {
         short inventorySlot = slea.readShort();
         int itemId = slea.readInt();
         byte command = slea.readByte();
         Item RingBox = chr.getInventory(MapleInventoryType.USE).getItem(inventorySlot);
         if (itemId == 2028068) {
            NPCScriptManager.getInstance().start(c, 9010010, "consume_2028068", false);
            chr.send(CWvsContext.enableActions(chr));
         } else {
            if (RingBox != null && RingBox.getItemId() == itemId) {
               if (command == 0) {
                  if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1
                     || chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                     || chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
                     c.getPlayer().dropMessage(1, "장비, 소비, 기타 아이템 슬롯을 1칸 이상 비워주세요.");
                     chr.send(CWvsContext.enableActions(chr));
                     return;
                  }

                  c.getSession()
                     .writeAndFlush(
                        CField.TheSeedBox(
                           inventorySlot,
                           itemId,
                           1113098,
                           1113099,
                           1113100,
                           1113101,
                           1113102,
                           1113103,
                           1113104,
                           1113117,
                           1113118,
                           1113119,
                           1113120,
                           1113122,
                           4001832
                        )
                     );
               } else if (command == 1) {
                  ScriptManager.runScript(c, "the_seed_ring", MapleLifeFactory.getNPC(2540000), null, null, itemId);
               }

               chr.send(CWvsContext.enableActions(chr));
            }
         }
      }
   }

   public static void useHasteBox(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null) {
         byte state = slea.readByte();
         switch (state) {
            case 0:
               int indexx = slea.readInt();
               boolean allClear = false;
               if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() > 0 && chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() > 0) {
                  if (Integer.parseInt(chr.getOneInfo(QuestExConstants.HasteEvent.getQuestID(), "openBox")) == indexx) {
                     if (chr.getOneInfo(500081, "openH") != null && chr.getOneInfo(500081, "openH").equals("1")) {
                        return;
                     }

                     try {
                        chr.updateOneInfo(
                           QuestExConstants.HasteEvent.getQuestID(),
                           "openBox",
                           Integer.parseInt(chr.getOneInfo(QuestExConstants.HasteEvent.getQuestID(), "openBox")) + 1 + ""
                        );
                     } catch (Exception var8) {
                        chr.updateOneInfo(QuestExConstants.HasteEvent.getQuestID(), "openBox", "1");
                     }

                     chr.updateOneInfo(
                        QuestExConstants.HasteEvent.getQuestID(),
                        "booster",
                        Integer.parseInt(chr.getOneInfo(QuestExConstants.HasteEvent.getQuestID(), "booster")) + 1 + ""
                     );
                     if (chr.getOneInfo(QuestExConstants.HasteEvent.getQuestID(), "openBox").equals("6")) {
                        chr.updateOneInfo(QuestExConstants.HasteEvent.getQuestID(), "str", "히든 미션이 열렸습니다!");
                        allClear = true;
                     } else {
                        int stage = Integer.parseInt(chr.getOneInfo(QuestExConstants.HasteEvent.getQuestID(), "openBox")) + 1;
                        chr.updateOneInfo(QuestExConstants.HasteEvent.getQuestID(), "str", stage + "단계 상자 도전 중! 일일 미션 1개를 완료하세요!");
                     }

                     chr.send(CWvsContext.enableActions(chr));
                     int[][] itemList = new int[][]{
                        {4001832, 100}, {4001832, 200}, {4001832, 300}, {2711001, 10}, {2711005, 10}, {2711006, 10}, {2048723, 1}, {2048724, 1}
                     };
                     if (DBConfig.isGanglim) {
                        itemList = new int[][]{
                           {4001832, 100}, {4001832, 200}, {4001832, 300}, {5062009, 5}, {5062500, 5}, {2711006, 10}, {2048723, 1}, {2048724, 1}
                        };
                     }

                     int rand = Randomizer.nextInt(itemList.length);
                     chr.gainItem(itemList[rand][0], itemList[rand][1]);
                     chr.send(CWvsContext.InfoPacket.getShowItemGain(itemList[rand][0], itemList[rand][1], true));
                     if (allClear) {
                        chr.updateOneInfo(500081, "openH", "1");
                        MapleQuest.getInstance(500082).forceStart(chr, 9010010, "");
                        ScriptManager.runScript(c, "weekHQuest", MapleLifeFactory.getNPC(9010010), null, null);
                     }
                  }
               } else {
                  chr.dropMessage(5, "소비창과 기타창을 1칸 비워주세요.");
               }
               break;
            case 1:
               int index = slea.readInt();
               if (index == 0 && chr.getQuestStatus(500082) == 1) {
                  if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() > 0 && chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 0) {
                     chr.gainItem(2631097, 1);
                     chr.send(CWvsContext.InfoPacket.getShowItemGain(2631097, 1, true));
                     chr.gainItem(1114317, 1);
                     chr.send(CWvsContext.InfoPacket.getShowItemGain(1114317, 1, true));
                     chr.updateOneInfo(500081, "clear", "1");
                     chr.forceCompleteQuest(500082);
                  } else {
                     chr.dropMessage(5, "장비창과 소비창을 1칸 비워주세요.");
                  }
               }
         }
      }
   }

   public static void useHasteBooster(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getMap() != null) {
         int state = slea.readInt();
         switch (state) {
            case 0:
               ScriptManager.runScript(c, "useHasteBooster", MapleLifeFactory.getNPC(9010010), null, null);
         }
      }
   }

   public static void useSilverKarma(PacketDecoder in, MapleCharacter chr) {
      boolean use = false;
      in.skip(4);
      Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(in.readShort());
      MapleInventoryType type = MapleInventoryType.getByType((byte)in.readShort());
      Item toScroll = chr.getInventory(type).getItem(in.readShort());
      if ((scroll.getItemId() == 2720000 || scroll.getItemId() == 2720001)
         && (
            !ItemFlag.KARMA_EQ.check(toScroll.getFlag()) && !ItemFlag.KARMA_USE.check(toScroll.getFlag())
               || ItemFlag.POSSIBLE_TRADING.check(toScroll.getFlag())
         )) {
         int flag = toScroll.getFlag();
         if (toScroll instanceof Equip) {
            Equip equip = (Equip)toScroll;
            if (equip.getKarmaCount() > 0) {
               equip.setKarmaCount((byte)(equip.getKarmaCount() - 1));
            }
         }

         if (type == MapleInventoryType.EQUIP) {
            flag |= ItemFlag.KARMA_EQ.getValue();
         } else {
            flag |= ItemFlag.KARMA_USE.getValue();
         }

         toScroll.setFlag(flag);
         use = true;
      }

      if (use) {
         chr.removeItem(scroll.getItemId(), -1);
         chr.getClient()
            .getSession()
            .writeAndFlush(CWvsContext.InventoryPacket.updateInventoryItem(GameConstants.getInventoryType(toScroll.getItemId()), toScroll, false, chr));
      } else {
         chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
      }
   }

   public static void checkCubeLevelUp(MapleClient c, Equip eq, GradeRandomOption option, int beforeItemGrade, int afterItemGrade) {
      boolean additional = option == GradeRandomOption.Additional
         || option == GradeRandomOption.OccultAdditional
         || option == GradeRandomOption.AmazingAdditional;
      String levelUpType = "";
      String linkedType = "";
      int questId = GameConstants.getLinkedCubeQuest(option);
      switch (beforeItemGrade) {
         case 1:
            levelUpType = "RtoE";
            linkedType = "rare";
            break;
         case 2:
            levelUpType = "EtoU";
            linkedType = "epic";
            break;
         case 3:
            levelUpType = "UtoL";
            linkedType = "unique";
      }

      if (!levelUpType.isEmpty() && !linkedType.isEmpty() && questId != 0) {
         levelUpType = option.toString() + levelUpType;
         if (afterItemGrade > beforeItemGrade) {
            if (option != GradeRandomOption.AmazingAdditional && option != GradeRandomOption.Black) {
               boolean var19 = false;
            } else {
               boolean var10000 = true;
            }

            c.getPlayer().updateOneInfo(QuestExConstants.CubeLevelUp.getQuestID(), levelUpType, "0");
            c.getPlayer().updateOneInfo(questId, linkedType, "0");
         } else if (beforeItemGrade != 4 && beforeItemGrade == afterItemGrade) {
            String cubeString = "";
            switch (option) {
               case Red:
                  cubeString = "레드 큐브";
                  break;
               case Black:
                  cubeString = "블랙 큐브";
                  break;
               case Additional:
                  cubeString = "에디셔널 큐브";
                  break;
               case AmazingAdditional:
                  cubeString = "화이트 에디셔널 큐브";
                  break;
               default:
                  return;
            }

            String infoString = "";
            switch (beforeItemGrade) {
               case 1:
                  infoString = "레어에서 에픽";
                  break;
               case 2:
                  infoString = "에픽에서 유니크";
                  break;
               case 3:
                  infoString = "유니크에서 레전더리";
            }

            int tryCount = c.getPlayer().getOneInfoQuestInteger(QuestExConstants.CubeLevelUp.getQuestID(), levelUpType);
            int levelUpCount = GameConstants.getCubeLevelUpCount(option, beforeItemGrade);
            if (tryCount >= levelUpCount) {
               GradeRandomOption fixOption = option;
               if (!DBConfig.isGanglim && option == GradeRandomOption.AmazingAdditional) {
                  fixOption = GradeRandomOption.Additional;
               }

               if (additional) {
                  switch (beforeItemGrade) {
                     case 1:
                        eq.setPotential4(eq.getPotential4() + 10000);
                        setPotential(fixOption, false, eq);
                        break;
                     case 2:
                        eq.setPotential4(eq.getPotential4() + 10000);
                        setPotential(fixOption, false, eq);
                        break;
                     case 3:
                        eq.setPotential4(eq.getPotential4() + 10000);
                        setPotential(fixOption, false, eq);
                  }
               } else {
                  switch (beforeItemGrade) {
                     case 1:
                        eq.setItemGrade(2);
                        setPotential(option, false, eq);
                        break;
                     case 2:
                        eq.setItemGrade(3);
                        setPotential(option, false, eq);
                        break;
                     case 3:
                        eq.setItemGrade(4);
                        setPotential(option, false, eq);
                  }
               }

               c.getPlayer().updateOneInfo(QuestExConstants.CubeLevelUp.getQuestID(), levelUpType, "0");
               c.getPlayer().updateOneInfo(questId, linkedType, "0");
               String upgradeGrade = "";
               switch (beforeItemGrade) {
                  case 1:
                     upgradeGrade = "에픽";
                     break;
                  case 2:
                     upgradeGrade = "유니크";
                     break;
                  case 3:
                     upgradeGrade = "레전더리";
               }

               boolean isMemorialCube = option == GradeRandomOption.AmazingAdditional || option == GradeRandomOption.Black;
               if (DBConfig.isJin) {
                  c.getPlayer().dropMessage(5, "[등급 천장] " + cubeString + " 등급 천장 목표 개수를 달성하여 " + upgradeGrade + " 등급으로 확정 상승되었습니다.");
                  if (isMemorialCube) {
                     c.getPlayer().dropMessage(5, "[등급 천장] 해당 큐브는 등급이 상승되어 Before 옵션을 선택해도 천장 목표 개수는 초기화됩니다.");
                  }
               }
            } else {
               c.getPlayer().updateOneInfo(QuestExConstants.CubeLevelUp.getQuestID(), levelUpType, String.valueOf(tryCount + 1));
               c.getPlayer().updateOneInfo(questId, linkedType, String.valueOf(tryCount + 1));
               if (DBConfig.isJin) {
                  c.getPlayer().dropMessage(5, "[등급 천장] " + cubeString + " 사용으로 " + infoString + "까지 남은 목표 개수 (" + (tryCount + 1) + "/" + levelUpCount + ")");
                  if (levelUpCount == tryCount + 1) {
                     String upgradeGrade = "";
                     switch (beforeItemGrade) {
                        case 1:
                           upgradeGrade = "에픽";
                           break;
                        case 2:
                           upgradeGrade = "유니크";
                           break;
                        case 3:
                           upgradeGrade = "레전더리";
                     }

                     c.getPlayer()
                        .dropMessage(5, "[등급 천장] " + cubeString + " 등급 천장 목표 개수를 달성하여 다음 " + infoString + " 등급업 시도 시 " + upgradeGrade + " 등급으로 확정 상승됩니다.");
                  }
               }
            }
         }
      }
   }
}
