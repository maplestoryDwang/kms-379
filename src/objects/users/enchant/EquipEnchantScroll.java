package objects.users.enchant;

import database.DBConfig;
import objects.item.Equip;
import objects.item.MapleItemInformationProvider;
import objects.utils.Randomizer;

public class EquipEnchantScroll {
   public int index;
   public int cost;
   public ScrollType scrollType;
   public int scrollOption;
   public String name;
   public int success;
   public EquipEnchantOption option;

   public EquipEnchantScroll(int equipItemID, int index, EquipEnchantOption option, ScrollType scrollType, int scrollOption, boolean fever) {
      if (index <= 3) {
         this.cost = EquipEnchantMan.GetScrollVestigeCost(equipItemID, index);
         this.success = EquipEnchantMan.getSuccessByIndex(index, fever);
         this.name = this.success + "% " + EquipEnchantMan.getNameByFlag(option.flag) + " 주문서";
      }

      this.option = option;
      this.index = index;
      this.scrollType = scrollType;
      this.scrollOption = scrollOption;
      switch (scrollType) {
         case INNOCENT:
            if (scrollOption == 1) {
               this.name = "이노센트 주문서 100%";
               this.success = 100;
               this.cost = 12000;
            } else if (scrollOption == 4) {
               this.name = "아크 이노센트 주문서 100%";
               this.success = 100;
               this.cost = 2400;
            }
            break;
         case WHITE:
            if (scrollOption == 1) {
               this.name = "순백의 주문서 100%";
               this.success = 100;
               this.cost = 20000;
            }
      }
   }

   public int upgrade(Equip equip, int tucProtectR, Equip zeroEquip) {
      return this.upgrade(equip, tucProtectR, false, zeroEquip);
   }

   public int upgrade(Equip equip, int tucProtectR, boolean success_, Equip zeroEquip) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      switch (this.scrollType) {
         case INNOCENT:
            int success = this.success;
            switch (this.scrollOption) {
               case 1:
                  if (!Randomizer.isSuccess(success)) {
                     return 0;
                  }

                  innocent(equip, InnocentFlag.HYPER.getType() | InnocentFlag.BONUS_STAT.getType(), zeroEquip);
                  return 1;
               case 4:
                  if (equip.isAmazingHyperUpgradeUsed()) {
                     return -1;
                  } else {
                     if (!Randomizer.isSuccess(success)) {
                        return 0;
                     }

                     innocent(equip, InnocentFlag.NONE.getType() | InnocentFlag.BONUS_STAT.getType(), zeroEquip);
                     return 1;
                  }
               default:
                  return -1;
            }
         case WHITE:
            Equip origin = (Equip)MapleItemInformationProvider.getInstance().getEquipById(equip.getItemId());
            if (origin == null) {
               return -1;
            } else {
               if (this.scrollOption == 1 && origin.getUpgradeSlots() + equip.getViciousHammer() - equip.getLevel() - equip.getUpgradeSlots() > 0) {
                  if (!Randomizer.isSuccess(this.success)) {
                     return 0;
                  }

                  equip.setUpgradeSlots((byte)(equip.getUpgradeSlots() + 1));
                  if (zeroEquip != null) {
                     zeroEquip.setUpgradeSlots(equip.getUpgradeSlots());
                  }

                  return 1;
               }

               return -1;
            }
         case UPGRADE:
            if (equip.getUpgradeSlots() <= 0) {
               return -1;
            } else {
               boolean attribute = ItemFlag.SAFETY_SCROLLED.check(equip.getFlag());
               success = this.success;
               if (ItemFlag.LUCKY_DAY_SCROLLED.check(equip.getFlag())) {
                  success += 10;
                  equip.setFlag((short)(equip.getFlag() & ~ItemFlag.LUCKY_DAY_SCROLLED.getValue()));
               }

               if (success_) {
                  success = 100;
               }

               equip.setFlag((short)(equip.getFlag() & ~ItemFlag.SAFETY_SCROLLED.getValue()));
               if (zeroEquip != null) {
                  zeroEquip.setFlag(equip.getFlag());
               }

               if (Randomizer.isSuccess(success)) {
                  equip.setUpgradeSlots((byte)(equip.getUpgradeSlots() - 1));
                  equip.setLevel((byte)(equip.getLevel() + 1));
                  this.option.applyUpgrade(equip);
                  if (zeroEquip != null) {
                     zeroEquip.setUpgradeSlots(equip.getUpgradeSlots());
                     zeroEquip.setLevel(equip.getLevel());
                     this.option.applyUpgrade(zeroEquip);
                  }

                  return 1;
               } else if (!attribute && (tucProtectR == 0 || !Randomizer.isSuccess(tucProtectR))) {
                  equip.setUpgradeSlots((byte)(equip.getUpgradeSlots() - 1));
                  if (zeroEquip != null) {
                     zeroEquip.setUpgradeSlots(equip.getUpgradeSlots());
                  }
               }
            }
         default:
            return 0;
      }
   }

   public static void innocent(Equip equip, int flag, Equip zeroEquip) {
      innocent(equip, flag, false, zeroEquip);
   }

   public static void innocent(Equip equip, int flag, boolean force, Equip zeroEquip) {
      if ((InnocentFlag.HYPER.getType() & flag) == 0 && equip.isAmazingHyperUpgradeUsed() && !force) {
         System.out.println("[오류] 놀장강 아이템에 하이퍼 업그레이드 플래그 없이 이노센트 사용 시도.");
      } else {
         if (!DBConfig.isGanglim) {
            if (equip.getItemId() == 1032216 || equip.getItemId() == 1032219) {
               return;
            }

            if (equip.getItemId() == 1062077) {
               return;
            }
         }

         Equip origin = (Equip)MapleItemInformationProvider.getInstance().getEquipById(equip.getItemId());
         Equip zeroOrigin = null;
         if (origin != null) {
            if (zeroEquip != null) {
               zeroOrigin = (Equip)MapleItemInformationProvider.getInstance().getEquipById(zeroEquip.getItemId());
               if (zeroOrigin == null) {
                  return;
               }
            }

            String enchant = equip.getOwner();
            int enchantLevel = equip.getSPGrade();
            int specialPotential = equip.getSpecialPotential();
            Equip temp = (Equip)equip.copy();
            equip.set(origin);
            equip.setPotential1(temp.getPotential1());
            equip.setPotential2(temp.getPotential2());
            equip.setPotential3(temp.getPotential3());
            equip.setPotential4(temp.getPotential4());
            equip.setPotential5(temp.getPotential5());
            equip.setPotential6(temp.getPotential6());
            equip.setState(temp.getState());
            equip.setSoulEnchanter(temp.getSoulEnchanter());
            equip.setSoulName(temp.getSoulName());
            equip.setSoulPotential(temp.getSoulPotential());
            equip.setSoulSkill(temp.getSoulSkill());
            if (zeroEquip != null) {
               zeroEquip.set(zeroOrigin);
               zeroEquip.setPotential1(equip.getPotential1());
               zeroEquip.setPotential2(equip.getPotential2());
               zeroEquip.setPotential3(equip.getPotential3());
               zeroEquip.setPotential4(equip.getPotential4());
               zeroEquip.setPotential5(equip.getPotential5());
               zeroEquip.setPotential6(equip.getPotential6());
               zeroEquip.setState(equip.getState());
               zeroEquip.setSoulEnchanter(equip.getSoulEnchanter());
               zeroEquip.setSoulName(equip.getSoulName());
               zeroEquip.setSoulPotential(equip.getSoulPotential());
               zeroEquip.setSoulSkill(equip.getSoulSkill());
            }

            if (DBConfig.isGanglim) {
               equip.setOwner("");
               if (zeroEquip != null) {
                  zeroEquip.setOwner("");
               }
            } else if (enchant.contains("성") || enchantLevel > 0 || specialPotential > 0) {
               int lv = enchantLevel;
               int[] allStats = new int[]{5, 5, 10, 10, 20, 20, 30, 50, 75, 100};
               int[] attacks = new int[]{0, 0, 5, 5, 10, 10, 15, 25, 35, 50};

               for (int i = 0; i < lv; i++) {
                  equip.setStr((short)(equip.getStr() + allStats[i]));
                  equip.setDex((short)(equip.getDex() + allStats[i]));
                  equip.setInt((short)(equip.getInt() + allStats[i]));
                  equip.setLuk((short)(equip.getLuk() + allStats[i]));
                  equip.setWatk((short)(equip.getWatk() + attacks[i]));
                  equip.setMatk((short)(equip.getMatk() + attacks[i]));
                  equip.setSPAllStat(equip.getSPAllStat() + allStats[i]);
                  equip.setSPAttack(equip.getSPAttack() + attacks[i]);
                  if (zeroEquip != null) {
                     zeroEquip.setStr((short)(zeroEquip.getStr() + allStats[i]));
                     zeroEquip.setDex((short)(zeroEquip.getDex() + allStats[i]));
                     zeroEquip.setInt((short)(zeroEquip.getInt() + allStats[i]));
                     zeroEquip.setLuk((short)(zeroEquip.getLuk() + allStats[i]));
                     zeroEquip.setWatk((short)(zeroEquip.getWatk() + allStats[i]));
                     zeroEquip.setMatk((short)(zeroEquip.getMatk() + allStats[i]));
                     zeroEquip.setSPAllStat((short)(zeroEquip.getSPAllStat() + allStats[i]));
                     zeroEquip.setSPAttack((short)(zeroEquip.getSPAttack() + allStats[i]));
                  }
               }

               equip.setSpecialPotential(1);
               equip.setOwner(lv + "성");
               equip.setSPGrade(lv);
               if (zeroEquip != null) {
                  zeroEquip.setSpecialPotential(1);
                  zeroEquip.setOwner(equip.getOwner());
                  zeroEquip.setSPGrade(equip.getSPGrade());
               }
            }

            int itemID = equip.getItemId();
            if (itemID == 1002140
               || itemID >= 1009600 && itemID <= 1009604
               || itemID >= 1109600 && itemID <= 1109604
               || itemID >= 1089600 && itemID <= 1089604
               || itemID >= 1159600 && itemID <= 1159604
               || itemID >= 1079600 && itemID <= 1079604) {
               equip.setAllStat((byte)20);
               equip.setTotalDamage((byte)20);
            }

            if ((flag & InnocentFlag.HYPER.getType()) != 0) {
               equip.setCHUC(0);
            } else {
               equip.setCHUC(temp.getCHUC());
            }

            if (equip.getKarmaCount() > temp.getKarmaCount()) {
               equip.setKarmaCount(temp.getKarmaCount());
            }

            equip.setItemState(ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            equip.setFlag((short)(equip.getFlag() & ~ItemFlag.PREVENT_SLIP.getValue()));
            if (origin.getDurability() > 0) {
               equip.setDurability(origin.getDurability());
            }

            if ((InnocentFlag.TRADING.getType() & flag) != 0 && equip.getKarmaCount() > 0 && equip.getKarmaCount() < 20) {
               equip.setFlag((short)(equip.getFlag() & ~ItemFlag.BINDED.getValue()));
               equip.setFlag((short)(equip.getFlag() & ~ItemFlag.POSSIBLE_TRADING.getValue()));
               equip.setFlag((short)(equip.getFlag() & ~ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.getValue()));
            }

            if (zeroEquip != null) {
               zeroEquip.setCHUC(equip.getCHUC());
               zeroEquip.setItemState(equip.getItemState());
               zeroEquip.setFlag(equip.getFlag());
               zeroEquip.setDurability(equip.getDurability());
               zeroEquip.setKarmaCount(equip.getKarmaCount());
            }

            if ((InnocentFlag.BONUS_STAT.getType() & flag) != 0) {
               equip.setExGradeOption(temp.getExGradeOption());
               if (zeroEquip != null) {
                  zeroEquip.setExGradeOption(equip.getExGradeOption());
               }
            } else {
               equip.setExGradeOption(0L);
               if (zeroEquip != null) {
                  zeroEquip.setExGradeOption(0L);
               }
            }
         }
      }
   }
}
