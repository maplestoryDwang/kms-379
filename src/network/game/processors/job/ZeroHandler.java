package network.game.processors.job;

import constants.GameConstants;
import database.DBConfig;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.PacketHelper;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.PlayerStats;
import objects.users.enchant.EquipEnchantMan;
import objects.users.enchant.EquipEnchantOption;
import objects.users.enchant.EquipEnchantScroll;
import objects.users.enchant.GradeRandomOption;
import objects.users.enchant.ItemOptionInfo;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.ItemUpgradeFlag;
import objects.users.enchant.ScrollType;
import objects.users.jobs.zero.EgoEquipUpgradeCost;
import objects.users.looks.zero.ZeroInfo;
import objects.users.looks.zero.ZeroInfoFlag;
import objects.users.looks.zero.ZeroLinkCashPartFlag;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Randomizer;
import scripting.newscripting.ScriptManager;

public class ZeroHandler {
   public static void zeroTag(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         int subMP = packet.readInt();
         int mp = packet.readInt();
         PlayerStats stats = player.getStat();
         if (stats != null) {
            ZeroInfo zeroInfo = player.getZeroInfo();
            if (zeroInfo != null) {
               zeroInfo.setBeta(!zeroInfo.isBeta());
               long calcSubMHp = stats.getCurrentMaxHp(player);
               long calcSubMMp = stats.getCurrentMaxMp(player);
               if (!zeroInfo.isBeta() && player.getSkillLevel(101100203) > 0) {
                  SecondaryStatEffect reinForceBody = SkillFactory.getSkill(101100203).getEffect(player.getSkillLevel(101100203));
                  calcSubMMp += calcSubMMp * reinForceBody.getMDF() / 100L;
               }

               long hp = stats.getHp();
               long mhp = stats.getMaxHp();
               long mmp = stats.getMaxMp();
               int subHp = zeroInfo.getSubHP();
               int subMHp = zeroInfo.getSubMHP();
               int subMMp = zeroInfo.getSubMMP();
               stats.setMaxHp(subMHp);
               stats.setHp(subHp, player);
               stats.setMaxMp(subMMp);
               stats.setMp(subMP, player);
               zeroInfo.setSubHP((int)hp);
               zeroInfo.setSubMHP((int)mhp);
               zeroInfo.setSubMP(mp);
               zeroInfo.setSubMMP((int)mmp);
               zeroInfo.setCalcSubMHP((int)calcSubMHp);
               zeroInfo.setCalcSubMMP((int)calcSubMMp);
               zeroInfo.sendUpdateZeroInfo(player, ZeroInfoFlag.IsBeta, ZeroInfoFlag.SubMP, ZeroInfoFlag.SubHP, ZeroInfoFlag.SubMHP, ZeroInfoFlag.SubMMP);
               PacketEncoder p = new PacketEncoder();
               p.writeShort(SendPacketOpcode.ZERO_TAG.getValue());
               p.writeInt(player.getId());
               PacketHelper.addCharLook(p, player, false, zeroInfo.isBeta(), true);
               player.getMap().broadcastMessage(player, p.getPacket(), false);
               stats.recalcLocalStats(player, false);
            }
         }
      }
   }

   public static void zeroLastAssisState(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         PacketEncoder p = new PacketEncoder();
         p.writeShort(SendPacketOpcode.ZERO_LAST_ASSIST_STATE.getValue());
         p.writeInt(player.getId());
         player.getMap().broadcastMessage(player, p.getPacket(), false);
      }
   }

   public static void zeroShareCashEquipPart(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         int bodyPart = packet.readInt();
         boolean link = packet.readByte() == 1;
         ZeroInfo zeroInfo = player.getZeroInfo();
         if (zeroInfo != null) {
            ZeroLinkCashPartFlag f = ZeroLinkCashPartFlag.getByOrder(bodyPart);
            ZeroLinkCashPartFlag flag = ZeroLinkCashPartFlag.getFlag(zeroInfo.getZeroLinkCashPart());
            if (link) {
               if (!flag.hasFlag(f)) {
                  int v = flag.getFlag() | f.getFlag();
                  zeroInfo.setZeroLinkCashPart(v);
               }
            } else if (flag.hasFlag(f)) {
               int v = flag.getFlag() & ~f.getFlag();
               zeroInfo.setZeroLinkCashPart(v);
            }

            zeroInfo.sendUpdateZeroInfo(player, ZeroInfoFlag.LinkCash);
            player.send(CWvsContext.enableActions(player));
         }
      }
   }

   public static void inheritanceInfoRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.INHERITANCE_INFO.getValue());
            Item alphaWeapon = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
            if (alphaWeapon == null) {
               player.dropMessage(1, "더 이상 무기를 성장시킬 수 없습니다.");
               p.write(0);
               player.send(p.getPacket());
            } else {
               Item betaWeapon = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
               if (betaWeapon == null) {
                  player.dropMessage(1, "더 이상 무기를 성장시킬 수 없습니다.");
                  p.write(0);
                  player.send(p.getPacket());
               } else {
                  Equip alpha = (Equip)alphaWeapon;
                  Equip beta = (Equip)betaWeapon;
                  int currentLevel = alpha.getItemId() % 100;
                  int newLevel = currentLevel;
                  int reqNextLvl = 999;
                  if (currentLevel >= 7) {
                     ScriptManager.runScript(player.getClient(), "zero_inheritance", MapleLifeFactory.getNPC(2400009));
                  } else {
                     while (newLevel < 7 && GameConstants.getZeroInheritanceNeedLevel(newLevel + 1) <= player.getLevel()) {
                        reqNextLvl = GameConstants.getZeroInheritanceNeedLevel(newLevel + 1);
                        newLevel = Math.min(newLevel + 1, 7);
                     }

                     if (newLevel > currentLevel && matchedZeroEquip(alpha, 1572000 + currentLevel) && matchedZeroEquip(beta, 1562000 + currentLevel)) {
                        player.encodeZeroInheritanceUpgrade(p, currentLevel, reqNextLvl, newLevel, 0, 0);
                        player.send(p.getPacket());
                     } else {
                        p.write(0);
                        player.send(p.getPacket());
                        player.dropMessage(1, "무기 성장을 하기에 레벨이 부족합니다.");
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean matchedZeroEquip(Equip equip, int itemID) {
      return (ItemStateFlag.VESTIGE_POSSIBLE_TRADING.getValue() & equip.getItemState()) == 0 && GameConstants.isZeroWeapon(itemID)
         ? equip.getItemId() == itemID
         : false;
   }

   public static void inheritanceUpgradeRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            packet.skip(4);
            boolean trans = packet.readByte() == 1;
            packet.skip(1);
            packet.skip(1);
            int goLevel = packet.readInt();
            Item alphaWeapon = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
            if (alphaWeapon != null) {
               Item betaWeapon = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
               if (betaWeapon == null) {
                  player.dropMessage(1, "더 이상 무기를 성장시킬 수 없습니다.");
               } else {
                  Equip alpha = (Equip)alphaWeapon.copy();
                  Equip beta = (Equip)betaWeapon.copy();
                  int currentLevel = alpha.getItemId() % 100;
                  if (currentLevel < 10 && matchedZeroEquip(alpha, 1572000 + currentLevel) && matchedZeroEquip(beta, 1562000 + currentLevel)) {
                     int reqLevel = GameConstants.getZeroInheritanceNeedLevel(goLevel);
                     if (goLevel <= currentLevel || reqLevel > player.getLevel()) {
                        return;
                     }

                     int itemID = 0;
                     int count = 0;
                     switch (goLevel) {
                        case 8:
                           itemID = 4310216;
                           count = 1;
                           break;
                        case 9:
                           itemID = 4310217;
                           count = 1;
                           break;
                        case 10:
                           itemID = 4310260;
                           count = 1;
                     }

                     if (!player.haveItem(itemID, count)) {
                        return;
                     }

                     MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                     Equip alpha_new = (Equip)ii.getEquipById(1572000 + goLevel);
                     Equip beta_new = (Equip)ii.getEquipById(1562000 + goLevel);
                     if (trans) {
                        transZeroEquip(alpha_new, alpha, goLevel);
                        transZeroEquip(beta_new, beta, goLevel);
                     }

                     if (goLevel == 10) {
                        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-20000) == null) {
                           Equip alpha_copy = (Equip)alpha.copy();
                           Equip beta_copy = (Equip)beta.copy();
                           alpha_copy.setPosition((short)-20000);
                           beta_copy.setPosition((short)-20001);
                           player.getInventory(MapleInventoryType.EQUIPPED).addFromDB(alpha_copy);
                           player.getInventory(MapleInventoryType.EQUIPPED).addFromDB(beta_copy);
                        }

                        byte grade = alpha_new.getAdditionalGrade();

                        if (player.getOneInfoQuestInteger(40981, "reset_potential") == 1) {
                           alpha_new.setLines((byte)3);
                           alpha_new.setState((byte)19);

                           for (int i = 0; i < 3; i++) {
                              int optionGrade = 3;
                              int option = ItemOptionInfo.getItemOption(
                                 alpha_new.getItemId(), optionGrade, alpha_new.getPotentials(false, i), GradeRandomOption.Black
                              );
                              alpha_new.setPotentialOption(i, option);
                           }
                        } else {
                           alpha_new.setState(alpha.getState());
                           alpha_new.setLines(alpha.getLines());
                           alpha_new.setPotential1(alpha.getPotential1());
                           alpha_new.setPotential2(alpha.getPotential2());
                           alpha_new.setPotential3(alpha.getPotential3());
                        }

                        if (player.getOneInfoQuestInteger(40981, "reset_e_potential") == 1) {
                           for (int i = 0; i < 3; i++) {
                              int optionGrade = 2;
                              int option = ItemOptionInfo.getItemOption(
                                 alpha_new.getItemId(), optionGrade, alpha_new.getPotentials(true, i), GradeRandomOption.Additional
                              );
                              alpha_new.setPotentialOption(i + 3, option);
                           }
                        } else {
                           alpha_new.setPotential4(alpha.getPotential4());
                           alpha_new.setPotential5(alpha.getPotential5());
                           alpha_new.setPotential6(alpha.getPotential6());
                        }

                        player.updateOneInfo(40981, "reset_potential", "0");
                        player.updateOneInfo(40981, "reset_e_potential", "0");
                        player.changeSkillLevel(80002632, 1, 1);
                        player.changeSkillLevel(80002633, 1, 1);
                        beta_new.setLines(alpha_new.getLines());
                        beta_new.setState(alpha_new.getState());
                        beta_new.setExGradeOption(alpha_new.getExGradeOption());
                        beta_new.setPotential1(alpha_new.getPotential1());
                        beta_new.setPotential2(alpha_new.getPotential2());
                        beta_new.setPotential3(alpha_new.getPotential3());
                        beta_new.setPotential4(alpha_new.getPotential4());
                        beta_new.setPotential5(alpha_new.getPotential5());
                        beta_new.setPotential6(alpha_new.getPotential6());
                     }

                     if (currentLevel == 0) {
                        int pa = ItemOptionInfo.getItemGradePA();
                        int gradex = 3;
                        alpha_new.setItemGrade(gradex);
                        beta_new.setItemGrade(gradex);

                        for (int i = 0; i < pa; i++) {
                           int optionGrade = gradex + (i > 0 ? -1 : 0);
                           int option = ItemOptionInfo.getItemOption(
                              alpha_new.getItemId(), optionGrade, alpha_new.getPotentials(false, i), GradeRandomOption.Meister
                           );
                           alpha_new.setPotentialOption(i, option);
                           beta_new.setPotentialOption(i, option);
                        }
                     }

                     player.getInventory(MapleInventoryType.EQUIPPED).removeItem((short)-11);
                     player.getInventory(MapleInventoryType.EQUIPPED).removeItem((short)-10);
                     alpha_new.setPosition((short)-11);
                     beta_new.setPosition((short)-10);
                     player.getInventory(MapleInventoryType.EQUIPPED).addFromDB(alpha_new);
                     player.getInventory(MapleInventoryType.EQUIPPED).addFromDB(beta_new);
                     player.send(CWvsContext.InventoryPacket.updateEquipSlot(alpha_new));
                     player.send(CWvsContext.InventoryPacket.updateEquipSlot(beta_new));
                     player.removeItem(itemID, -count);
                     player.updateOneInfo(40981, "lv", String.valueOf(goLevel));
                     PacketEncoder p = new PacketEncoder();
                     p.writeShort(SendPacketOpcode.INHERITANCE_COMPLETE.getValue());
                     p.write(goLevel == 10);
                     player.send(p.getPacket());
                  }
               }
            } else {
               player.dropMessage(1, "더 이상 무기를 성장시킬 수 없습니다.");
            }
         }
      }
   }

   private static void transZeroEquip(Equip equip, Equip before, int newLevel) {
      if (newLevel != 10) {
         equip.setUpgradeSlots(before.getUpgradeSlots());
         equip.setLevel(before.getLevel());
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int value = before.getStr() - ii.getIncSTR(before.getItemId());
         equip.setStr((short)(equip.getStr() + value));
         value = before.getDex() - ii.getIncDEX(before.getItemId());
         equip.setDex((short)(equip.getDex() + value));
         value = before.getInt() - ii.getIncINT(before.getItemId());
         equip.setInt((short)(equip.getInt() + value));
         value = before.getLuk() - ii.getIncLUK(before.getItemId());
         equip.setLuk((short)(equip.getLuk() + value));
         value = before.getHp() - ii.getIncMHP(before.getItemId());
         equip.setHp((short)(equip.getHp() + value));
         value = before.getMp() - ii.getIncMMP(before.getItemId());
         equip.setMp((short)(equip.getMp() + value));
         value = before.getWatk() - ii.getIncPAD(before.getItemId());
         equip.setWatk((short)(equip.getWatk() + value));
         value = before.getMatk() - ii.getIncMAD(before.getItemId());
         equip.setMatk((short)(equip.getMatk() + value));
         value = before.getWdef() - ii.getIncPDD(before.getItemId());
         equip.setWdef((short)(equip.getWdef() + value));
         value = before.getMdef() - ii.getIncMDD(before.getItemId());
         equip.setMdef((short)(equip.getMdef() + value));
         value = before.getAcc() - ii.getIncACC(before.getItemId());
         equip.setAcc((short)(equip.getAcc() + value));
         value = before.getAvoid() - ii.getIncEVA(before.getItemId());
         equip.setAvoid((short)(equip.getAvoid() + value));
         value = before.getHands() - ii.getIncCraft(before.getItemId());
         equip.setHands((short)(equip.getHands() + value));
         value = before.getSpeed() - ii.getincSpeed(before.getItemId());
         equip.setSpeed((short)(equip.getSpeed() + value));
         value = before.getJump() - ii.getIncJump(before.getItemId());
         equip.setJump((short)(equip.getJump() + value));
         value = before.getBossDamage() - ii.getBdR(before.getItemId());
         equip.setBossDamage((short)(equip.getBossDamage() + value));
         value = before.getIgnorePDR() - ii.getIMdR(before.getItemId());
         equip.setIgnorePDR((short)(equip.getIgnorePDR() + value));
         equip.setIncSkill(before.getIncSkill());
         equip.setViciousHammer(before.getViciousHammer());
         equip.setPVPDamage(before.getPVPDamage());
         equip.setDownLevel(before.getDownLevel());
         equip.setReqLevel(before.getReqLevel());
         equip.setGrowthEnchant(before.getGrowthEnchant());
         equip.setFinalStrike(before.getFinalStrike());
         equip.setKarmaCount(before.getKarmaCount());
         equip.setExGradeOption(before.getExGradeOption());
         equip.setState(before.getState());
         equip.setLines(before.getLines());
         equip.setCHUC(before.getCHUC());
         equip.setPotential1(before.getPotential1());
         equip.setPotential2(before.getPotential2());
         equip.setPotential3(before.getPotential3());
         equip.setPotential4(before.getPotential4());
         equip.setPotential5(before.getPotential5());
         equip.setPotential6(before.getPotential6());
         equip.setItemState(before.getItemState());
         if (DBConfig.isGanglim) {
            String enchant = before.getOwner();
            int enchantLevel = before.getSPGrade();
            int specialPotential = before.getSpecialPotential();
            int spAllStat = before.getSPAllStat();
            int spAttack = before.getSPAttack();
            equip.setSPAllStat(spAllStat);
            equip.setSPAttack(spAttack);
            equip.setSpecialPotential(1);
            equip.setOwner(enchant);
            equip.setSPGrade(enchantLevel);
            equip.setStr((short)(equip.getStr() + spAllStat));
            equip.setDex((short)(equip.getDex() + spAllStat));
            equip.setInt((short)(equip.getInt() + spAllStat));
            equip.setLuk((short)(equip.getLuk() + spAllStat));
            equip.setWatk((short)(equip.getWatk() + spAttack));
            equip.setMatk((short)(equip.getMatk() + spAttack));
         } else {
            String enchant = before.getOwner();
            int enchantLevel = before.getSPGrade();
            int specialPotential = before.getSpecialPotential();
            if (enchant.contains("성") || enchantLevel > 0 || specialPotential > 0) {
               int lv = enchantLevel;
               int[] allStats = new int[]{5, 5, 10, 10, 20, 20, 30, 50, 75, 100};
               int[] attacks = new int[]{0, 0, 5, 5, 10, 10, 15, 25, 35, 50};

               for (int i = 0; i < lv; i++) {
                  equip.setSPAllStat(equip.getSPAllStat() + allStats[i]);
                  equip.setSPAttack(equip.getSPAttack() + attacks[i]);
               }

               equip.setSpecialPotential(1);
               equip.setOwner(lv + "성");
               equip.setSPGrade(lv);
            }
         }
      } else if (newLevel == 10) {
         int weaponID = equip.getItemId();
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int flag = EquipEnchantMan.filterForJobWeapon(weaponID);
         ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD};
         ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[]{
            ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP
         };
         ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_INT};
         List<EquipEnchantScroll> source = new ArrayList<>();

         for (ItemUpgradeFlag f : flagArray) {
            for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
               int index = 3;
               EquipEnchantOption option = new EquipEnchantOption();
               option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
               if (f2.check(flag)) {
                  option.setOption(
                     f2.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3) * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1)
                  );
                  if (option.flag > 0) {
                     source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
                  }
               }
            }
         }

         if (source.size() <= 0) {
            return;
         }

         EquipEnchantScroll scroll = source.get(0);
         if (scroll == null) {
            return;
         }

         for (int i = 0; i < 8; i++) {
            scroll.upgrade(equip, 0, true, null);
         }

         equip.setCHUC(22);
         equip.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
         equip.setExGradeOption(before.getExGradeOption());
      }

      equip.setSoulSkill(before.getSoulSkill());
      equip.setSoulPotential(before.getSoulPotential());
      equip.setSoulName(before.getSoulName());
      equip.setSoulEnchanter(before.getSoulEnchanter());
   }

   public static void egoEquipCreateUpgradeItemCostRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            int index = packet.readInt();
            boolean buttonClicked = packet.readByte() == 1;
            EgoEquipUpgradeCost cost = GameConstants.getZeroEgoEquipUpgradeCost(index);
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.EGO_EQUIP_CREATE_UPGRADE_ITEM_COST_INFO.getValue());
            p.writeInt(index);
            p.writeInt(cost.getMeso());
            p.writeInt(cost.getWp());
            p.write(buttonClicked);
            p.write(0);
            p.write(0);
            p.write(0);
            player.send(p.getPacket());
         }
      }
   }

   public static void egoEquipGaugeCompleteReturn(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.EGO_EQUIP_GAUGE_COMPLETE_RETURN.getValue());
            player.send(p.getPacket());
         }
      }
   }

   public static void egoEquipCheckUpgradeItemRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            int uTI = packet.readInt();
            int uPOS = packet.readInt();
            int eTI = packet.readInt();
            int ePOS = packet.readInt();
            int slot = packet.readInt();
            Item scroll = player.getInventory(MapleInventoryType.getByType((byte)uTI)).getItem((short)uPOS);
            Equip equip = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)ePOS);
            if (scroll != null && equip != null) {
               if (GameConstants.isZeroWeapon(equip.getItemId())) {
                  PacketEncoder p = new PacketEncoder();
                  p.writeShort(SendPacketOpcode.EGO_EQUIP_CHECK_UPGRADE_ITEM_RESULT.getValue());
                  if (scroll.getItemId() / 10 != 20487 && scroll.getItemId() / 10 == 204936) {
                     p.write(0);
                     p.writeMapleAsciiString("이 아이템을 사용할 수 없습니다.");
                     p.writeInt(0);
                  } else {
                     p.write(1);
                     p.writeMapleAsciiString("");
                     p.writeInt(slot);
                  }

                  player.send(p.getPacket());
               }
            }
         }
      }
   }

   public static void egoEquipTalkRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            ScriptManager.runScript(player.getClient(), "zero_egoequiptalk", MapleLifeFactory.getNPC(2400009));
         }
      }
   }

   public static void userLuckyItemUseRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null) {
         if (GameConstants.isZero(player.getJob())) {
            Equip.ScrollResult scrollSuccess = Equip.ScrollResult.FAIL;
            packet.skip(4);
            short uItemPOS = packet.readShort();
            short eItemPOS = packet.readShort();
            Item useItem = player.getInventory(MapleInventoryType.USE).getItem(uItemPOS);
            Item equip = player.getInventory(MapleInventoryType.EQUIPPED).getItem(eItemPOS);
            if (useItem != null && equip != null) {
               MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
               if (Randomizer.isSuccess(ii.getSuccess(useItem.getItemId(), player, equip))) {
                  scrollSuccess = Equip.ScrollResult.SUCCESS;
                  int setItemCategory = ii.getSetItemCategory(useItem.getItemId());
                  if (setItemCategory == -1) {
                     scrollSuccess = Equip.ScrollResult.FAIL;
                  }

                  MapleQuest.getInstance(41907).forceStart(player, 2003, String.valueOf(setItemCategory));
                  PacketEncoder p = new PacketEncoder();
                  p.writeInt(player.getId());
                  p.write(1);
                  p.writeInt(setItemCategory);
                  player.send(p.getPacket());
                  player.getStat().recalcLocalStats(player);
               }

               player.getMap().broadcastMessage(player, CField.getScrollEffect(player.getId(), scrollSuccess, useItem.getItemId(), equip.getItemId()), true);
               player.send(CWvsContext.enableActions(player));
               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, uItemPOS, (short)1, false);
            }
         }
      }
   }

   public static void zeroCombatRecovery(PacketDecoder o, MapleClient c) {
      int skillID = o.readInt();
      int skillLV = o.readByte();
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getSkillLevel(skillID) == skillLV) {
         SecondaryStatEffect combatRecovery = SkillFactory.getSkill(skillID).getEffect(skillLV);
         chr.addMP(combatRecovery.getZ());
      }
   }
}
