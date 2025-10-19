package network.game.processors;

import constants.DailyEventType;
import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import java.util.Date;
import java.util.List;
import logging.LoggingManager;
import logging.entry.EnchantLog;
import logging.entry.EnchantLogResult;
import logging.entry.EnchantLogType;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import network.models.PacketHelper;
import objects.context.SpecialSunday;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.ToadsHammerType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.users.enchant.EnchantType;
import objects.users.enchant.EquipEnchant;
import objects.users.enchant.EquipEnchantOption;
import objects.users.enchant.EquipEnchantScroll;
import objects.users.enchant.EquipSpecialAttribute;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.ItemUpgradeFlag;
import objects.users.enchant.ScrollType;
import objects.users.enchant.StarForceHyperUpgrade;
import objects.utils.Pair;
import objects.utils.Randomizer;
import scripting.newscripting.ScriptManager;

public class EnchantHandler {
   public static void equipmentEnchantWithSingleUIRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      boolean fever = !DBConfig.isGanglim && ServerConstants.JuhunFever == 1;
      byte b = slea.readByte();
      EnchantType type = EnchantType.getEnchantType(b);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      switch (type) {
         case SCROLL_UPGRADE:
            slea.skip(4);
            short equipSlotxxxx = slea.readShort();
            int index = slea.readInt();
            boolean success = false;
            Item itemxxxx = player.getInventory(equipSlotxxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(equipSlotxxxx);
            if (itemxxxx == null) {
               return;
            }

            Equip equipxx = (Equip)itemxxxx;
            EquipEnchant equipEnchant = new EquipEnchant(equipxx, true, fever);
            if (index >= 0 && index < equipEnchant.scrolls.size()) {
               int sucRate = equipEnchant.scrolls.get(index).success;
               if (player.getStat().itemUpgradeBonusR > 0) {
                  equipEnchant.scrolls.stream().filter(s -> s.scrollType != ScrollType.INNOCENT).filter(s -> s.scrollType != ScrollType.WHITE).forEach(s -> {
                     s.success = Math.min(100, s.success + player.getStat().itemUpgradeBonusR);
                     if (player.isGM()) {
                        System.out.println("강화의 달인 적용된 후 확률 : " + s.success);
                     }
                  });
               }

               EquipEnchantScroll scroll = equipEnchant.scrolls.get(index);
               boolean innocent = scroll.scrollType == ScrollType.INNOCENT;
               boolean whitescroll = scroll.scrollType == ScrollType.WHITE;
               if (scroll != null) {
                  PacketEncoder packetxx = new PacketEncoder();
                  packetxx.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
                  int count = player.getInventory(MapleInventoryType.ETC).countById(4001832);
                  int costx = scroll.cost;
                  if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeSpellTrace) {
                     costx /= 2;
                  }

                  if (count < costx) {
                     System.out.println("[핵사용] 주문의 흔적 갯수가 " + count + "개로 부족합니다!! (필요 갯수 : " + costx + ")");
                     return;
                  }

                  Equip zeroEquipxx = null;
                  boolean checkSuc = false;
                  if (player.isAlive()) {
                     Equip beforeEquipxx = (Equip)equipxx.copy();
                     int itemTucProtectR = player.getStat().itemTUCProtectR;
                     if (GameConstants.isZeroWeapon(equipxx.getItemId())) {
                        zeroEquipxx = (Equip)c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equipxx.getPosition() == -11 ? -10 : -11));
                     }

                     int resultx = scroll.upgrade(equipxx, itemTucProtectR, zeroEquipxx);
                     if (resultx == 1) {
                        checkSuc = true;
                     }

                     if (resultx == -1) {
                        System.out.println("[핵사용] 업그레이드가 불가능한 아이템을 업그레이드 시도하였습니다. " + scroll.name + "(" + equipxx.getItemId() + ")");
                        packetxx.write(EnchantType.DISPLAY_UNKNOWN_FAIL_RESULT.getType());
                        packetxx.write(0);
                     } else {
                        packetxx.write(EnchantType.DISPLAY_SCROLL_UPGRADE_RESULT.getType());
                        packetxx.write(0);
                        packetxx.writeInt(resultx);
                        packetxx.writeMapleAsciiString(scroll.name);
                        PacketHelper.addItemInfo(packetxx, beforeEquipxx);
                        PacketHelper.addItemInfo(packetxx, equipxx);
                        if (beforeEquipxx.getUpgradeSlots() != equipxx.getUpgradeSlots()
                           && beforeEquipxx.getUpgradeSlots() != 0
                           && equipxx.getUpgradeSlots() != 0) {
                           Equip origin = (Equip)MapleItemInformationProvider.getInstance().getEquipById(equipxx.getItemId());
                           if (origin.getUpgradeSlots() + equipxx.getViciousHammer() - equipxx.getUpgradeSlots() - equipxx.getLevel() == 0) {
                              success = true;
                           }
                        }
                     }
                  } else {
                     packetxx.write(EnchantType.DISPLAY_UNKNOWN_FAIL_RESULT.getType());
                     packetxx.write(1);
                  }

                  player.send(packetxx.getPacket());
                  player.gainItem(4001832, (short)(-costx), false, -1L, "");
                  c.getSession().writeAndFlush(CWvsContext.onCharacterModified(c.getPlayer(), -1L));
                  HyperHandler.updateSkills(c.getPlayer(), 0);
                  c.getPlayer().updateMatrixSkillsNoLock();
                  player.send(CWvsContext.InventoryPacket.scrolledItem(equipxx, equipxx, false, false, equipxx.getPosition() < 0));
                  if (zeroEquipxx != null) {
                     player.send(CWvsContext.InventoryPacket.scrolledItem(zeroEquipxx, zeroEquipxx, false, false, zeroEquipxx.getPosition() < 0));
                  }

                  int scrollID = innocent ? 2049600 : (whitescroll ? 2049047 : 0);
                  if (ServerConstants.useAchievement) {
                     AchievementFactory.checkSpelltraceEnchant(player, scrollID, sucRate, costx, checkSuc);
                  }

                  StringBuilder sb = new StringBuilder("주문의 흔적 강화 (");
                  sb.append("계정 : ");
                  sb.append(c.getAccountName());
                  sb.append(", 캐릭터 : ");
                  sb.append(c.getPlayer().getName());
                  sb.append(", 사용 주문서 : ");
                  sb.append(scroll.name);
                  sb.append(", 실제 적용 확률 : ");
                  sb.append(sucRate);
                  sb.append("% (정보 : ");
                  sb.append(equipxx.toString());
                  sb.append("))");
                  LoggingManager.putLog(
                     new EnchantLog(
                        c.getPlayer(),
                        scrollID == 0 ? 4001832 : scrollID,
                        equipxx.getItemId(),
                        equipxx.getSerialNumberEquip(),
                        EnchantLogType.EquipEnchant.getType(),
                        checkSuc ? EnchantLogResult.Success.getType() : EnchantLogResult.Failed.getType(),
                        sb
                     )
                  );
                  if (success) {
                     if (player.isQuestStarted(501531)) {
                        if (player.getOneInfoQuestInteger(501531, "value") < 1) {
                           player.updateOneInfo(501531, "value", "1");
                        }

                        if (player.getOneInfoQuestInteger(501524, "state") < 2) {
                           player.updateOneInfo(501524, "state", "2");
                        }
                     }

                     player.send(display_ScrollUpgrade(equipxx, fever));
                  }
               }
            } else {
               System.out.println("[핵사용] 주문의 흔적 업그레이드 인덱스(" + index + ")가 올바르지 않습니다. (최대 : " + equipEnchant.scrolls.size() + ")");
            }
            break;
         case HYPER_UPGRADE:
            slea.skip(4);
            int equipSlotxxx = slea.readShort();
            byte flag = slea.readByte();
            if (flag != 0) {
               slea.skip(4);
            }

            if (!player.getInventory(MapleInventoryType.EQUIP).isFull() && c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() != -1) {
               int num4 = slea.readInt();
               int num5 = slea.readInt();
               boolean preventDestructionx = slea.readByte() == 1;
               slea.skip(1);

               for (index = 0; index < num4; index++) {
                  PacketEncoder packetx = new PacketEncoder();
                  packetx.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
                  Item itemxxx = player.getInventory(equipSlotxxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem((short)equipSlotxxx);
                  if (itemxxx == null) {
                     return;
                  }

                  equipxx = (Equip)itemxxx;
                  Equip zeroEquipxx = null;
                  if (equipxx != null) {
                     if (equipxx.getCHUC() < 12 || equipxx.getCHUC() >= 17) {
                        preventDestructionx = false;
                     }

                     int pos = -1;
                     if (GameConstants.isZeroWeapon(equipxx.getItemId())) {
                        zeroEquipxx = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equipxx.getPosition() == -11 ? -10 : -11));
                     }

                     if (!player.isAlive()) {
                        packetx.write(EnchantType.DISPLAY_UNKNOWN_FAIL_RESULT.getType());
                        packetx.write(1);
                     } else if (GameConstants.getStarLimit(isSuperial(equipxx.getItemId()).right, ii.getReqLevel(equipxx.getItemId())) > equipxx.getCHUC()
                        && !equipxx.isAmazingHyperUpgradeUsed()) {
                        int discountx = 0;
                        int chuc = equipxx.getCHUC();
                        double successRatex = StarForceHyperUpgrade.getSuccessRateForStarForce(isSuperial(equipxx.getItemId()).right, chuc);
                        double curseRatex = preventDestructionx
                           ? 0.0
                           : StarForceHyperUpgrade.getCurseRateForStarForce(isSuperial(equipxx.getItemId()).right, chuc);
                        int itemIDx = equipxx.getItemId();
                        if (itemIDx >= 1009600 && itemIDx <= 1009604
                           || itemIDx >= 1109600 && itemIDx <= 1109604
                           || itemIDx >= 1089600 && itemIDx <= 1089604
                           || itemIDx >= 1159600 && itemIDx <= 1159604
                           || itemIDx >= 1079600 && itemIDx <= 1079604) {
                           curseRatex = 0.0;
                        }

                        if (flag != 0) {
                           successRatex += successRatex * 45.0 / 1000.0;
                        }

                        if (ServerConstants.dailyEventType == DailyEventType.StarForceDiscount) {
                           discountx = 30;
                        }

                        if (SpecialSunday.isActive && new Date().getDay() == 0 && SpecialSunday.activeStarForceDiscount) {
                           discountx = 30;
                        }

                        long costForStarForce = StarForceHyperUpgrade.getCostForStarForce(
                           equipxx.getItemId(), ii.getReqLevel(equipxx.getItemId()), chuc, preventDestructionx, discountx
                        );
                        EquipEnchantOption optionx = createOptionByHyperUpgrade(equipxx, chuc);
                        int downGradablex = StarForceHyperUpgrade.isDowngradable(chuc) ? 1 : 0;
                        Equip beforeEquipx = (Equip)equipxx.copy();
                        boolean chanceTimex = false;
                        boolean zzzz = false;
                        boolean checkx = false;
                        if (GameConstants.isZeroWeapon(equipxx.getItemId())) {
                           if (player.getHuLastFailedUniqueID() != 0L
                              && (
                                 equipxx.getSerialNumberEquip() == player.getHuLastFailedUniqueID()
                                    || zeroEquipxx.getSerialNumberEquip() == player.getHuLastFailedUniqueID()
                              )) {
                              checkx = true;
                           }
                        } else {
                           checkx = player.getHuLastFailedUniqueID() != 0L && player.getHuLastFailedUniqueID() == equipxx.getSerialNumberEquip();
                        }

                        if (player.getHuFailedStreak() >= 2 && checkx) {
                           chanceTimex = true;
                           successRatex = 100.0;
                           curseRatex = 0.0;
                        }

                        if (num5 != -1) {
                           int downGrade = num5 % 101;
                           int v = num5 / 101;
                           int destroy = v % 101;
                           int success2 = v / 101;
                           player.dropMessage(5, "명령어에 의해 수정된 스타포스 확률 : 성공=" + success2 + ", 파괴=" + destroy + ", 하락=" + downGrade);
                           successRatex = success2;
                           curseRatex = destroy;
                           downGradablex = downGrade <= 0 ? 0 : 1;
                        }

                        if (player.isGM()) {
                           successRatex = 100.0;
                           curseRatex = 0.0;
                           chanceTimex = true;
                        }

                        if (SpecialSunday.isActive
                           && new Date().getDay() == 0
                           && SpecialSunday.activeStarForce100
                           && (equipxx.getCHUC() == 5 || equipxx.getCHUC() == 10 || equipxx.getCHUC() == 15)
                           && !isSuperial(equipxx.getItemId()).right) {
                           successRatex = 100.0;
                           curseRatex = 0.0;
                        }

                        if (DBConfig.isGanglim && equipxx.getCHUC() <= 13) {
                           successRatex = 100.0;
                           curseRatex = 0.0;
                        }

                        int result = 0;
                        player.gainMeso(-costForStarForce, true);
                        EnchantLogResult enchantResult = EnchantLogResult.Success;
                        if (Randomizer.isSuccess((int)successRatex * 10, 1000)) {
                           if (SpecialSunday.isActive
                              && new Date().getDay() == 0
                              && SpecialSunday.activeStarForceOpO
                              && equipxx.getCHUC() <= 10
                              && !isSuperial(equipxx.getItemId()).right) {
                              equipxx.setCHUC(
                                 Math.min(
                                    equipxx.getCHUC() + 1,
                                    GameConstants.getStarLimit(isSuperial(equipxx.getItemId()).right, ii.getReqLevel(equipxx.getItemId()))
                                 )
                              );
                           }

                           result = 1;
                           equipxx.setCHUC(
                              Math.min(
                                 equipxx.getCHUC() + 1, GameConstants.getStarLimit(isSuperial(equipxx.getItemId()).right, ii.getReqLevel(equipxx.getItemId()))
                              )
                           );
                           if ((equipxx.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0) {
                              equipxx.setItemState(equipxx.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                           }

                           if (zeroEquipxx != null) {
                              zeroEquipxx.setItemState(equipxx.getItemState());
                              zeroEquipxx.setCHUC(equipxx.getCHUC());
                           }

                           player.setHuFailedStreak(0);
                           player.setHuLastFailedUniqueID(0L);
                        } else if (Randomizer.isSuccess((int)curseRatex * 10, 1000)) {
                           enchantResult = EnchantLogResult.Destroyed;
                           result = 2;
                           equipxx.setCHUC(Math.min(equipxx.getCHUC() - downGradablex, isSuperial(equipxx.getItemId()).left == null ? 12 : 0));
                           player.setHuFailedStreak(0);
                           player.setHuLastFailedUniqueID(0L);
                           equipxx.setSpecialAttribute((short)EquipSpecialAttribute.VESTIGE.getType());
                           if (ItemFlag.POSSIBLE_TRADING.check(equipxx.getFlag())) {
                              equipxx.setItemState(equipxx.getItemState() | ItemStateFlag.VESTIGE_POSSIBLE_TRADING.getValue());
                           }

                           if (ItemFlag.BINDED.check(equipxx.getFlag())) {
                              equipxx.setItemState(equipxx.getItemState() | ItemStateFlag.VESTIGE_BOUND.getValue());
                           }

                           if (ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.check(equipxx.getFlag())) {
                              equipxx.setItemState(equipxx.getItemState() | ItemStateFlag.VESTIGE_APPLIED_ACCOUNT_SHARE.getValue());
                           }

                           if (GameConstants.isZeroWeapon(equipxx.getItemId())) {
                              if (equipxx.getItemId() % 100 >= 7) {
                                 int diffWatk = 0;
                                 Item beforeItem = MapleItemInformationProvider.getInstance().getEquipById(equipxx.getItemId());
                                 Item afterItem = MapleItemInformationProvider.getInstance().getEquipById(equipxx.getItemId() / 1000 * 1000 + 7);
                                 int beforeWatk = ((Equip)beforeItem).getWatk();
                                 int afterWatk = ((Equip)afterItem).getWatk();
                                 diffWatk = beforeWatk - afterWatk;
                                 equipxx.setItemId(equipxx.getItemId() / 1000 * 1000 + 7);
                                 equipxx.setWatk((short)(equipxx.getWatk() - diffWatk));
                              }

                              Item zeroWeapon = c.getPlayer()
                                 .getInventory(MapleInventoryType.EQUIPPED)
                                 .getItem((short)(equipxx.getPosition() == -11 ? -10 : -11));
                              c.getPlayer().send(CWvsContext.InventoryPacket.deleteItem(equipxx));
                              c.getPlayer().send(CWvsContext.InventoryPacket.deleteItem(zeroWeapon));
                              c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem((short)(equipxx.getPosition() == -11 ? -10 : -11));
                              MapleInventoryManipulator.addbyItem(c, equipxx);
                              c.getSession()
                                 .writeAndFlush(
                                    CWvsContext.InventoryPacket.moveInventoryItem(
                                       MapleInventoryType.EQUIPPED, equipxx.getPosition(), equipxx.getPosition(), (short)1, false, false, false
                                    )
                                 );
                              c.getPlayer().equipChanged();
                              ScriptManager.runScript(c, "zero_reinvoke_weapon", MapleLifeFactory.getNPC(2400009));
                           } else if (equipSlotxxx < 0) {
                              MapleInventoryManipulator.unequip(
                                 MapleInventoryType.EQUIP, c, equipxx.getPosition(), c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot()
                              );
                           }
                        } else {
                           enchantResult = EnchantLogResult.Failed;
                           result = downGradablex > 0 ? 0 : 3;
                           equipxx.setCHUC(Math.max(0, equipxx.getCHUC() - downGradablex));
                           if (zeroEquipxx != null) {
                              zeroEquipxx.setCHUC(equipxx.getCHUC());
                           }

                           if (downGradablex > 0) {
                              if (player.getHuFailedStreak() > 0 && player.getHuLastFailedUniqueID() != equipxx.getSerialNumberEquip()) {
                                 player.setHuFailedStreak(0);
                                 zzzz = true;
                              }

                              player.setHuLastFailedUniqueID(equipxx.getSerialNumberEquip());
                              player.setHuFailedStreak(player.getHuFailedStreak() + 1);
                           } else {
                              player.setHuLastFailedUniqueID(0L);
                              player.setHuFailedStreak(0);
                           }
                        }

                        packetx.write(EnchantType.DISPLAY_HYPER_UPGRADE_RESULT.getType());
                        packetx.writeInt(result);
                        packetx.write(false);
                        PacketHelper.addItemInfo(packetx, beforeEquipx, player);
                        PacketHelper.addItemInfo(packetx, equipxx, player);
                        player.send(packetx.getPacket());
                        if (zeroEquipxx != null) {
                           player.send(CWvsContext.InventoryPacket.scrolledItem(zeroEquipxx, zeroEquipxx, false, false, zeroEquipxx.getPosition() < 0));
                        }

                        if (ServerConstants.useAchievement) {
                           int starForce = equipxx.getCHUC();
                           AchievementFactory.checkStarforceEnchant(
                              player,
                              result == 1 ? "success" : (result == 2 ? "destroyed" : "failure"),
                              starForce,
                              flag == 0 ? "failure" : "success",
                              costForStarForce
                           );
                        }

                        if (player.isQuestStarted(501533)) {
                           if (player.getOneInfoQuestInteger(501533, "value") < 1) {
                              player.updateOneInfo(501533, "value", "1");
                           }

                           if (player.getOneInfoQuestInteger(501524, "state") < 2) {
                              player.updateOneInfo(501524, "state", "2");
                           }
                        }

                        StringBuilder sb = new StringBuilder("스타포스 강화 (");
                        sb.append("계정 : ");
                        sb.append(c.getAccountName());
                        sb.append(", 캐릭터 : ");
                        sb.append(c.getPlayer().getName());
                        sb.append(", 적용 확률 : ");
                        sb.append(successRatex);
                        sb.append("% (정보 : ");
                        sb.append(equipxx.toString());
                        sb.append("))");
                        LoggingManager.putLog(
                           new EnchantLog(
                              c.getPlayer(),
                              4001832,
                              equipxx.getItemId(),
                              equipxx.getSerialNumberEquip(),
                              EnchantLogType.HyperUpgrade.getType(),
                              enchantResult.getType(),
                              sb
                           )
                        );
                        player.send(CWvsContext.InventoryPacket.scrolledItem(equipxx, equipxx, false, false, equipxx.getPosition() < 0));
                     } else {
                        packetx.write(EnchantType.DISPLAY_UNKNOWN_FAIL_RESULT.getType());
                        packetx.write(0);
                     }
                  }
               }
            } else {
               player.dropMessage(1, "장비 인벤토리 공간을 1칸 이상 비운 후 다시 시도해주시기 바랍니다.");
            }
            break;
         case DISPLAY_HYPER_UPGRADE:
            int equipSlotxx = slea.readInt();
            boolean preventDestruction = slea.readByte() == 1;
            Item itemxx = player.getInventory(equipSlotxx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem((short)equipSlotxx);
            if (itemxx == null) {
               return;
            }

            Equip equipx = (Equip)itemxx;
            int discount = 0;
            if (equipx.getCHUC() == 0 && (equipx.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0) {
               equipx.setItemState(equipx.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            }

            if (GameConstants.getStarLimit(isSuperial(equipx.getItemId()).right, ii.getReqLevel(equipx.getItemId())) > equipx.getCHUC()
               && !equipx.isAmazingHyperUpgradeUsed()) {
               if (equipx.getCHUC() < 12 || equipx.getCHUC() >= 17) {
                  preventDestruction = false;
               }

               double successRate = StarForceHyperUpgrade.getSuccessRateForStarForce(isSuperial(equipx.getItemId()).right, equipx.getCHUC());
               double curseRate = StarForceHyperUpgrade.getCurseRateForStarForce(isSuperial(equipx.getItemId()).right, equipx.getCHUC());
               long cost = StarForceHyperUpgrade.getCostForStarForce(
                  equipx.getItemId(), ii.getReqLevel(equipx.getItemId()), equipx.getCHUC(), preventDestruction, discount
               );
               if (ServerConstants.dailyEventType == DailyEventType.StarForceDiscount) {
                  discount = 30;
               }

               if (SpecialSunday.isActive && new Date().getDay() == 0 && SpecialSunday.activeStarForceDiscount) {
                  discount = 30;
               }

               long beforeMVP = 0L;
               if (discount > 0) {
                  beforeMVP = cost;
                  cost = StarForceHyperUpgrade.getCostForStarForce(
                     equipx.getItemId(), ii.getReqLevel(equipx.getItemId()), equipx.getCHUC(), preventDestruction, discount
                  );
               }

               EquipEnchantOption option = createOptionByHyperUpgrade(equipx, equipx.getCHUC() + 1);
               int beforeSuccess = 0;
               int beforeCurse = 0;
               boolean chanceTime = false;
               boolean downGradable = StarForceHyperUpgrade.isDowngradable(equipx.getCHUC());
               Equip zeroEquipx = null;
               if (GameConstants.isZeroWeapon(equipx.getItemId())) {
                  zeroEquipx = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equipx.getPosition() == -11 ? -10 : -11));
               }

               boolean check = false;
               if (GameConstants.isZeroWeapon(equipx.getItemId())) {
                  if (player.getHuLastFailedUniqueID() != 0L
                     && (
                        equipx.getSerialNumberEquip() == player.getHuLastFailedUniqueID()
                           || zeroEquipx.getSerialNumberEquip() == player.getHuLastFailedUniqueID()
                     )) {
                     check = true;
                  }
               } else {
                  check = player.getHuLastFailedUniqueID() != 0L && player.getHuLastFailedUniqueID() == equipx.getSerialNumberEquip();
               }

               if (player.getHuFailedStreak() >= 2 && check) {
                  chanceTime = true;
                  successRate = 100.0;
                  curseRate = 0.0;
                  beforeSuccess = 0;
                  beforeCurse = 0;
               }

               int itemID = equipx.getItemId();
               if (itemID >= 1009600 && itemID <= 1009604
                  || itemID >= 1109600 && itemID <= 1109604
                  || itemID >= 1089600 && itemID <= 1089604
                  || itemID >= 1159600 && itemID <= 1159604
                  || itemID >= 1079600 && itemID <= 1079604) {
                  curseRate = 0.0;
               }

               if (SpecialSunday.isActive
                  && new Date().getDay() == 0
                  && SpecialSunday.activeStarForce100
                  && (equipx.getCHUC() == 5 || equipx.getCHUC() == 10 || equipx.getCHUC() == 15)
                  && !isSuperial(equipx.getItemId()).right) {
                  beforeSuccess = (int)successRate * 10;
                  beforeCurse = 0;
                  successRate = 100.0;
                  curseRate = 0.0;
               }

               player.send(display_HyperUpgrade(downGradable, cost, beforeMVP, successRate, curseRate, beforeSuccess, beforeCurse, chanceTime, option));
            }
            break;
         case TRANSMISSION: {
            slea.skip(4);
            short equipSlotx = slea.readShort();
            short vestigeEquipSlot = slea.readShort();
            Item itemx = player.getInventory(equipSlotx < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(equipSlotx);
            if (itemx == null) {
               return;
            }

            Equip equip = (Equip)itemx;
            Equip beforeEquip = (Equip)equip.copy();
            Item vestigeItem = player.getInventory(MapleInventoryType.EQUIP).getItem(vestigeEquipSlot);
            if (vestigeItem == null) {
               return;
            }

            Equip vestigeEquip = (Equip)vestigeItem;
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
            packet.write(EnchantType.DISPLAY_TRANSMISSION_RESULT.getType());
            PacketHelper.addItemInfo(packet, equip);
            equip.set(vestigeEquip, true);
            equip.setOwner(vestigeEquip.getOwner());
            if ((ItemStateFlag.VESTIGE_POSSIBLE_TRADING.getValue() & equip.getItemState()) != 0) {
               equip.setItemState(equip.getItemState() & ~ItemStateFlag.VESTIGE_POSSIBLE_TRADING.getValue());
            }

            if ((ItemStateFlag.VESTIGE_BOUND.getValue() & equip.getItemState()) != 0) {
               equip.setItemState(equip.getItemState() & ~ItemStateFlag.VESTIGE_BOUND.getValue());
            }

            if ((ItemStateFlag.VESTIGE_APPLIED_ACCOUNT_SHARE.getValue() & equip.getItemState()) != 0) {
               equip.setItemState(equip.getItemState() & ~ItemStateFlag.VESTIGE_APPLIED_ACCOUNT_SHARE.getValue());
            }

            if ((equip.getSpecialAttribute() & EquipSpecialAttribute.VESTIGE.getType()) != 0) {
               equip.setSpecialAttribute((short)(equip.getSpecialAttribute() & ~EquipSpecialAttribute.VESTIGE.getType()));
            }

            if ((equip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0) {
               equip.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            }

            equip.setFlag(vestigeEquip.getFlag());
            Equip zeroEquip = null;
            if (GameConstants.isZeroWeapon(equip.getItemId())) {
               zeroEquip = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
               zeroEquip.setUpgradeSlots(vestigeEquip.getUpgradeSlots());
               zeroEquip.setLevel(vestigeEquip.getLevel());
               int value = vestigeEquip.getStr() - beforeEquip.getStr();
               zeroEquip.setStr((short)(zeroEquip.getStr() + value));
               value = vestigeEquip.getDex() - beforeEquip.getDex();
               zeroEquip.setDex((short)(zeroEquip.getDex() + value));
               value = vestigeEquip.getInt() - beforeEquip.getInt();
               zeroEquip.setInt((short)(zeroEquip.getInt() + value));
               value = vestigeEquip.getLuk() - beforeEquip.getLuk();
               zeroEquip.setLuk((short)(zeroEquip.getLuk() + value));
               value = vestigeEquip.getHp() - beforeEquip.getHp();
               zeroEquip.setHp((short)(zeroEquip.getHp() + value));
               value = vestigeEquip.getHp() - beforeEquip.getHp();
               zeroEquip.setHp((short)(zeroEquip.getHp() + value));
               value = vestigeEquip.getMp() - beforeEquip.getMp();
               zeroEquip.setMp((short)(zeroEquip.getMp() + value));
               value = vestigeEquip.getWatk() - beforeEquip.getWatk();
               zeroEquip.setWatk((short)(zeroEquip.getWatk() + value));
               value = vestigeEquip.getMatk() - beforeEquip.getMatk();
               zeroEquip.setMatk((short)(zeroEquip.getMatk() + value));
               value = vestigeEquip.getWdef() - beforeEquip.getWdef();
               zeroEquip.setWdef((short)(zeroEquip.getWdef() + value));
               value = vestigeEquip.getMdef() - beforeEquip.getMdef();
               zeroEquip.setMdef((short)(zeroEquip.getMdef() + value));
               value = vestigeEquip.getAcc() - beforeEquip.getAcc();
               zeroEquip.setAcc((short)(zeroEquip.getAcc() + value));
               value = vestigeEquip.getAvoid() - beforeEquip.getAvoid();
               zeroEquip.setAvoid((short)(zeroEquip.getAvoid() + value));
               value = vestigeEquip.getHands() - beforeEquip.getHands();
               zeroEquip.setHands((short)(zeroEquip.getHands() + value));
               value = vestigeEquip.getSpeed() - beforeEquip.getSpeed();
               zeroEquip.setSpeed((short)(zeroEquip.getSpeed() + value));
               value = vestigeEquip.getJump() - beforeEquip.getJump();
               zeroEquip.setJump((short)(zeroEquip.getJump() + value));
               value = vestigeEquip.getBossDamage() - beforeEquip.getBossDamage();
               zeroEquip.setBossDamage((short)(zeroEquip.getBossDamage() + value));
               value = vestigeEquip.getIgnorePDR() - beforeEquip.getIgnorePDR();
               zeroEquip.setIgnorePDR((short)(zeroEquip.getIgnorePDR() + value));
               zeroEquip.setExceptStr(vestigeEquip.getExceptSTR());
               zeroEquip.setExceptDex(vestigeEquip.getExceptDEX());
               zeroEquip.setExceptInt(vestigeEquip.getExceptINT());
               zeroEquip.setExceptLuk(vestigeEquip.getExceptLUK());
               zeroEquip.setExceptWatk(vestigeEquip.getExceptWATK());
               zeroEquip.setExceptMatk(vestigeEquip.getExceptMATK());
               zeroEquip.setExceptHP(vestigeEquip.getExceptHP());
               zeroEquip.setExceptMP(vestigeEquip.getExceptMP());
               zeroEquip.setIncSkill(vestigeEquip.getIncSkill());
               zeroEquip.setViciousHammer(vestigeEquip.getViciousHammer());
               zeroEquip.setPVPDamage(vestigeEquip.getPVPDamage());
               zeroEquip.setDownLevel(vestigeEquip.getDownLevel());
               zeroEquip.setReqLevel(vestigeEquip.getReqLevel());
               zeroEquip.setGrowthEnchant(vestigeEquip.getGrowthEnchant());
               zeroEquip.setFinalStrike(vestigeEquip.getFinalStrike());
               zeroEquip.setKarmaCount(vestigeEquip.getKarmaCount());
               zeroEquip.setExGradeOption(vestigeEquip.getExGradeOption());
               zeroEquip.setState(vestigeEquip.getState());
               zeroEquip.setLines(vestigeEquip.getLines());
               zeroEquip.setCHUC(vestigeEquip.getCHUC());
               zeroEquip.setPotential1(vestigeEquip.getPotential1());
               zeroEquip.setPotential2(vestigeEquip.getPotential2());
               zeroEquip.setPotential3(vestigeEquip.getPotential3());
               zeroEquip.setPotential4(vestigeEquip.getPotential4());
               zeroEquip.setPotential5(vestigeEquip.getPotential5());
               zeroEquip.setPotential6(vestigeEquip.getPotential6());
               zeroEquip.setSoulSkill(vestigeEquip.getSoulSkill());
               zeroEquip.setSoulPotential(vestigeEquip.getSoulPotential());
               zeroEquip.setSoulName(vestigeEquip.getSoulName());
               zeroEquip.setSoulEnchanter(vestigeEquip.getSoulEnchanter());
               zeroEquip.setSpecialPotential(1);
               zeroEquip.setOwner(equip.getOwner());
               zeroEquip.setSPGrade(equip.getSPGrade());
               zeroEquip.setItemState(equip.getItemState());
               zeroEquip.setFlag(equip.getFlag());
            }

            PacketHelper.addItemInfo(packet, equip);
            player.send(packet.getPacket());
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, vestigeEquipSlot, (short)1, false, false);
            player.send(CWvsContext.InventoryPacket.scrolledItem(equip, equip, false, false, equip.getPosition() < 0));
            if (zeroEquip != null) {
               player.send(CWvsContext.InventoryPacket.scrolledItem(zeroEquip, zeroEquip, false, false, zeroEquip.getPosition() < 0));
            }

            player.getStat().recalcLocalStats(player);
            break;
         }
         case DISPLAY_SCROLL_UPGRADE:
            int equipSlot = slea.readInt();
            Item item = player.getInventory(equipSlot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem((short)equipSlot);
            if (item == null) {
               return;
            }

            Equip equip = (Equip)item;
            if (equip.getCHUC() == 0 && (equip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0) {
               equip.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            }

            player.send(display_ScrollUpgrade(equip, fever));
            player.setJuhunEquipPosition(equipSlot);
            break;
         case DISPLAY_SCROLL_UPDATE: {
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
            packet.write(EnchantType.DISPLAY_SCROLL_UPDATE.getType());
            packet.write(player.getScrollFeverProbInc() > 0);
            player.send(packet.getPacket());
            break;
         }
         case DISPLAY_MINI_GAME: {
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
            packet.write(EnchantType.DISPLAY_MINI_GAME.getType());
            packet.write(0);
            packet.writeInt(Randomizer.nextInt());
            player.send(packet.getPacket());
         }
      }
   }

   public static byte[] display_HyperUpgrade(
      boolean downGradable,
      long cost,
      long beforeMVP,
      double successRate,
      double curseRate,
      int beforeSuccess,
      int beforeCurse,
      boolean chanceTime,
      EquipEnchantOption option
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
      packet.write(EnchantType.DISPLAY_HYPER_UPGRADE.getType());
      packet.write(downGradable);
      packet.writeLong(cost);
      packet.writeLong(0L);
      packet.writeLong(beforeMVP);
      packet.write(0);
      packet.write(0);
      packet.writeInt((int)(successRate * 10.0));
      packet.writeInt((int)(curseRate * 10.0));
      packet.writeInt(beforeSuccess);
      packet.writeInt(beforeCurse);
      packet.write(chanceTime);
      option.encode(packet);
      return packet.getPacket();
   }

   public static byte[] display_ScrollUpgrade(Equip equip, boolean fever) {
      PacketEncoder packet = new PacketEncoder();
      EquipEnchant enchant = new EquipEnchant(equip, true, fever);
      packet.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
      packet.write(EnchantType.DISPLAY_SCROLL_UPGRADE.getType());
      packet.write(fever);
      enchant.encode(packet);
      return packet.getPacket();
   }

   public static EquipEnchantOption createOptionByHyperUpgrade(Equip equip, int chuc) {
      EquipEnchantOption ret = new EquipEnchantOption();
      Equip copyEquip = (Equip)equip.copy();
      copyEquip.setCHUC(chuc);
      int value = 0;
      if ((value = copyEquip.getTotalStr() - equip.getTotalStr()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_STR.getValue();
         ret.incSTR = value;
      }

      if ((value = copyEquip.getTotalDex() - equip.getTotalDex()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_DEX.getValue();
         ret.incDEX = value;
      }

      if ((value = copyEquip.getTotalInt() - equip.getTotalInt()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_INT.getValue();
         ret.incINT = value;
      }

      if ((value = copyEquip.getTotalLuk() - equip.getTotalLuk()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_LUK.getValue();
         ret.incLUK = value;
      }

      if ((value = copyEquip.getTotalHp() - equip.getTotalHp()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_MHP.getValue();
         ret.incMHP = value;
      }

      if ((value = copyEquip.getTotalMp() - equip.getTotalMp()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_MMP.getValue();
         ret.incMMP = value;
      }

      if ((value = copyEquip.getTotalWatk() - equip.getTotalWatk()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_PAD.getValue();
         ret.incPAD = value;
      }

      if ((value = copyEquip.getTotalMatk() - equip.getTotalMatk()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_MAD.getValue();
         ret.incMAD = value;
      }

      if ((value = copyEquip.getTotalWdef() - equip.getTotalWdef()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_PDD.getValue();
         ret.incPDD = value;
      }

      if ((value = copyEquip.getTotalMdef() - equip.getTotalMdef()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_MDD.getValue();
         ret.incMDD = value;
      }

      if ((value = copyEquip.getTotalAcc() - equip.getTotalAcc()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_ACC.getValue();
         ret.incACC = value;
      }

      if ((value = copyEquip.getTotalAvoid() - equip.getTotalAvoid()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_EVA.getValue();
         ret.incEVA = value;
      }

      if ((value = copyEquip.getTotalSpeed() - equip.getTotalSpeed()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_SPEED.getValue();
         ret.incSpeed = value;
      }

      if ((value = copyEquip.getTotalJump() - equip.getTotalJump()) > 0) {
         ret.flag = ret.flag | ItemUpgradeFlag.INC_JUMP.getValue();
         ret.incJump = value;
      }

      return ret;
   }

   public static Pair<String, Boolean> isSuperial(int itemid) {
      if ((itemid < 1102471 || itemid > 1102475) && (itemid < 1072732 || itemid > 1072736) && (itemid < 1132164 || itemid > 1132168)) {
         if ((itemid < 1102476 || itemid > 1102480) && (itemid < 1072737 || itemid > 1072741) && (itemid < 1132169 || itemid > 1132173)) {
            if ((itemid < 1102481 || itemid > 1102485)
               && (itemid < 1072743 || itemid > 1072747)
               && (itemid < 1132174 || itemid > 1132178)
               && (itemid < 1082543 || itemid > 1082547)) {
               return itemid >= 1122241 && itemid <= 1122245 ? new Pair<>("MindPendent", true) : new Pair<>(null, false);
            } else {
               return new Pair<>("Tilent", true);
            }
         } else {
            return new Pair<>("Nova", true);
         }
      } else {
         return new Pair<>("Helisium", true);
      }
   }

   private static Equip getAfter(Equip source, Equip target, int scrollPOS, MapleCharacter player, PacketDecoder slea) {
      if (GameConstants.isToadsHammerAvailableItem(source) && GameConstants.isToadsHammerAvailableItem(target)) {
         if (target.getUpgradeSlots() > 0) {
            EquipEnchant enchant = new EquipEnchant(target, false, false);
            if (scrollPOS == -1) {
               System.out.println("업횟이 있는데 주문서가 지정되지 않음 (name: " + player.getName() + ")");
               return null;
            }

            if (enchant.scrolls.size() <= scrollPOS || scrollPOS < 0) {
               return null;
            }

            EquipEnchantScroll scroll = enchant.scrolls.get(scrollPOS);
            if (scroll.success != 100) {
               System.out.println("100퍼 성공률이 아닌 주문서를 토드에서 사용하려 함 (name: " + player.getName() + ")");
               return null;
            }

            int count = 0;

            while (target.getUpgradeSlots() > 0 && count++ < 1000) {
               scroll.option.applyUpgrade(target);
               target.setUpgradeSlots((byte)(target.getUpgradeSlots() - 1));
               target.setLevel((byte)(target.getLevel() + 1));
            }
         }

         for (int i = 0; i < 6; i++) {
            int option = source.getPotentialOption(i);
            if (option / 10000 > 2) {
               List<Integer> downList = GameConstants.getDowngraded(source.getItemId(), option, 2);
               option = downList.stream().findAny().get();
            }

            target.setPotentialOption(i, option);
         }

         target.setItemGrade(Math.min(2, source.getItemGrade()));
         target.setAdditionalReleased(source.isAdditionalReleased());
         target.setReleased(source.isReleased());
         target.setCHUC(Math.max(0, source.getCHUC() - 1));
         target.setSoulEnchanter(source.getSoulEnchanter());
         target.setSoulName(source.getSoulName());
         target.setSoulPotential(source.getSoulPotential());
         target.setSoulSkill(source.getSoulSkill());
         String enchantx = source.getOwner();
         int enchantLevel = source.getSPGrade();
         int specialPotential = source.getSpecialPotential();
         if (enchantx.contains("성") || enchantLevel > 0 || specialPotential > 0) {
            int lv = enchantLevel;
            int[] allStats = new int[]{5, 5, 10, 10, 20, 20, 30, 50, 75, 100};
            int[] attacks = new int[]{0, 0, 5, 5, 10, 10, 15, 25, 35, 50};

            for (int i = 0; i < lv; i++) {
               target.setStr((short)(source.getStr() + allStats[i]));
               target.setDex((short)(source.getDex() + allStats[i]));
               target.setInt((short)(source.getInt() + allStats[i]));
               target.setLuk((short)(source.getLuk() + allStats[i]));
               target.setWatk((short)(source.getWatk() + attacks[i]));
               target.setMatk((short)(source.getMatk() + attacks[i]));
               target.setSPAllStat(source.getSPAllStat() + allStats[i]);
               target.setSPAttack(source.getSPAttack() + attacks[i]);
            }

            target.setSpecialPotential(1);
            target.setOwner(lv + "성");
            target.setSPGrade(lv);
         }

         target.setItemState(target.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
         return target;
      } else {
         return null;
      }
   }

   public static void userToadsHammerRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         if (player.getLevel() >= 40) {
            ToadsHammerType type = ToadsHammerType.getType(slea.readShort());
            switch (type) {
               case ScrollData:
                  short equipPOS = slea.readShort();
                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.TOADS_HAMMER_REQUEST_RESULT.getValue());
                  packet.writeShort(type.getType());
                  packet.write(0);
                  Item item = player.getInventory(MapleInventoryType.EQUIP).getItem(equipPOS);
                  if (item == null) {
                     return;
                  }

                  Equip equip = (Equip)item;
                  if (equip.getUpgradeSlots() <= 0) {
                     packet.write(0);
                  } else {
                     EquipEnchant enchant = new EquipEnchant(equip, false, false);
                     enchant.encode(packet);
                  }

                  player.send(packet.getPacket());
                  break;
               case PassDownResultPreview:
                  short sourcePOSx = slea.readShort();
                  short targetPOSx = slea.readShort();
                  short scrollPOSx = slea.readShort();
                  Item sourceItemx = player.getInventory(MapleInventoryType.EQUIP).getItem(sourcePOSx);
                  if (sourceItemx == null) {
                     return;
                  }

                  Item targetItem = player.getInventory(MapleInventoryType.EQUIP).getItem(targetPOSx);
                  if (targetItem == null) {
                     return;
                  }

                  Equip source = (Equip)sourceItemx;
                  Equip target = (Equip)targetItem;
                  Equip preview = (Equip)target.copy();
                  Equip after = getAfter((Equip)sourceItemx, preview, scrollPOSx, player, slea);
                  PacketEncoder packet2 = new PacketEncoder();
                  packet2.writeShort(SendPacketOpcode.TOADS_HAMMER_REQUEST_RESULT.getValue());
                  packet2.writeShort(type.getType());
                  PacketHelper.addItemInfo(packet2, after, player);
                  player.send(packet2.getPacket());
                  break;
               case PassDown:
                  short sourcePOS = slea.readShort();
                  short targetPOS = slea.readShort();
                  short scrollPOS = slea.readShort();
                  Item sourceItem = player.getInventory(MapleInventoryType.EQUIP).getItem(sourcePOS);
                  if (sourceItem == null) {
                     return;
                  }

                  targetItem = player.getInventory(MapleInventoryType.EQUIP).getItem(targetPOS);
                  if (targetItem == null) {
                     return;
                  }

                  Item before = targetItem.copy();
                  after = getAfter((Equip)sourceItem, (Equip)targetItem, scrollPOS, player, slea);
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, sourcePOS, (short)1, false, false);
                  player.send(CWvsContext.InventoryPacket.scrolledItem(after, after, false, true, after.getPosition() < 0));
                  PacketEncoder packet3 = new PacketEncoder();
                  packet3.writeShort(SendPacketOpcode.TOADS_HAMMER_REQUEST_RESULT.getValue());
                  packet3.writeShort(type.getType());
                  PacketHelper.addItemInfo(packet3, sourceItem, player);
                  packet3.writeShort(targetPOS);
                  player.send(packet3.getPacket());
            }
         }
      }
   }
}
