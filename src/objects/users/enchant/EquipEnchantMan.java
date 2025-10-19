package objects.users.enchant;

import constants.GameConstants;
import objects.item.MapleItemInformationProvider;

public class EquipEnchantMan {
   public static int getIncAllStatArmor(int rLevel) {
      if (rLevel < 60) {
         return 1;
      } else {
         return rLevel < 120 ? 2 : 3;
      }
   }

   public static int getIncPrimaryStatArmor(int rLevel, int index) {
      if (rLevel < 60) {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 3;
         }
      } else if (rLevel < 120) {
         switch (index) {
            case 0:
               return 2;
            case 1:
               return 3;
            case 2:
               return 5;
         }
      } else {
         switch (index) {
            case 0:
               return 3;
            case 1:
               return 4;
            case 2:
               return 7;
         }
      }

      return 0;
   }

   public static int getIncPrimaryMHPStatArmor(int rLevel, int index) {
      if (rLevel < 60) {
         switch (index) {
            case 0:
               return 55;
            case 1:
               return 115;
            case 2:
               return 180;
         }
      } else if (rLevel < 120) {
         switch (index) {
            case 0:
               return 120;
            case 1:
               return 190;
            case 2:
               return 320;
         }
      } else {
         switch (index) {
            case 0:
               return 180;
            case 1:
               return 270;
            case 2:
               return 470;
         }
      }

      return 0;
   }

   public static int getIncPrimaryStatAccessory(int rLevel, int index) {
      if (rLevel <= 70) {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 3;
         }
      } else if (rLevel <= 120) {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 4;
         }
      } else {
         switch (index) {
            case 0:
               return 2;
            case 1:
               return 3;
            case 2:
               return 5;
         }
      }

      return 0;
   }

   public static int getIncMHPArmor(int rLevel, int index) {
      if (rLevel < 60) {
         switch (index) {
            case 0:
               return 5;
            case 1:
               return 15;
            case 2:
               return 30;
         }
      } else if (rLevel < 120) {
         switch (index) {
            case 0:
               return 20;
            case 1:
               return 40;
            case 2:
               return 70;
         }
      } else {
         switch (index) {
            case 0:
               return 30;
            case 1:
               return 70;
            case 2:
               return 120;
         }
      }

      return 0;
   }

   public static int getIncDEFArmor(int rLevel, int index) {
      if (rLevel < 60) {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 4;
         }
      } else if (rLevel < 120) {
         switch (index) {
            case 0:
               return 2;
            case 1:
               return 4;
            case 2:
               return 7;
         }
      } else {
         switch (index) {
            case 0:
               return 3;
            case 1:
               return 5;
            case 2:
               return 10;
         }
      }

      return 0;
   }

   public static String getNameByFlag(int flag) {
      if (ItemUpgradeFlag.INC_PAD.check(flag)) {
         return "공격력";
      } else if (ItemUpgradeFlag.INC_MAD.check(flag)) {
         return "마력";
      } else if (ItemUpgradeFlag.INC_STR.check(flag)
         && ItemUpgradeFlag.INC_DEX.check(flag)
         && ItemUpgradeFlag.INC_INT.check(flag)
         && ItemUpgradeFlag.INC_LUK.check(flag)) {
         return "올스탯";
      } else if (ItemUpgradeFlag.INC_STR.check(flag)
         || (ItemUpgradeFlag.INC_STR.getValue() | ItemUpgradeFlag.INC_PDD.getValue() | ItemUpgradeFlag.INC_MHP.getValue()) == flag) {
         return "힘";
      } else if ((ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_STR.getValue()) == flag) {
         return "공격력(힘)";
      } else if (ItemUpgradeFlag.INC_DEX.check(flag)
         || (ItemUpgradeFlag.INC_DEX.getValue() | ItemUpgradeFlag.INC_PDD.getValue() | ItemUpgradeFlag.INC_MHP.getValue()) == flag) {
         return "민첩";
      } else if ((ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_DEX.getValue()) == flag) {
         return "공격력(민첩)";
      } else if (ItemUpgradeFlag.INC_INT.check(flag)
         || (ItemUpgradeFlag.INC_INT.getValue() | ItemUpgradeFlag.INC_PDD.getValue() | ItemUpgradeFlag.INC_MHP.getValue()) == flag) {
         return "지력";
      } else if ((ItemUpgradeFlag.INC_MAD.getValue() | ItemUpgradeFlag.INC_INT.getValue()) == flag) {
         return "마력(지력)";
      } else if (ItemUpgradeFlag.INC_LUK.check(flag)
         || (ItemUpgradeFlag.INC_LUK.getValue() | ItemUpgradeFlag.INC_PDD.getValue() | ItemUpgradeFlag.INC_MHP.getValue()) == flag) {
         return "운";
      } else if ((ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_LUK.getValue()) == flag) {
         return "공격력(운)";
      } else if (ItemUpgradeFlag.INC_MHP.check(flag) || (ItemUpgradeFlag.INC_PDD.getValue() | ItemUpgradeFlag.INC_MHP.getValue()) == flag) {
         return "체력";
      } else if ((ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_MHP.getValue()) == flag) {
         return "공격력(체력)";
      } else {
         return ItemUpgradeFlag.INC_PDD.check(flag) ? "방어력" : "";
      }
   }

   public static int filterForJobArmor(int itemID) {
      int flag = 0;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int rjob = ii.getReqJob(itemID);
      switch (rjob) {
         case -1:
            flag = flag | ItemUpgradeFlag.INC_STR.getValue() | ItemUpgradeFlag.INC_MHP.getValue();
            break;
         case 0:
            flag = flag
               | ItemUpgradeFlag.INC_STR.getValue()
               | ItemUpgradeFlag.INC_DEX.getValue()
               | ItemUpgradeFlag.INC_INT.getValue()
               | ItemUpgradeFlag.INC_LUK.getValue()
               | ItemUpgradeFlag.INC_MHP.getValue();
            break;
         default:
            if ((rjob & flag(1)) == 0) {
               if ((rjob & flag(2)) != 0) {
                  flag |= ItemUpgradeFlag.INC_INT.getValue();
               } else if ((rjob & flag(3)) != 0) {
                  flag |= ItemUpgradeFlag.INC_DEX.getValue();
               } else if ((rjob & flag(4)) != 0 || (rjob & flag(5)) != 0) {
                  flag = flag | ItemUpgradeFlag.INC_STR.getValue() | ItemUpgradeFlag.INC_DEX.getValue() | ItemUpgradeFlag.INC_LUK.getValue();
               }
            } else {
               flag = ItemUpgradeFlag.INC_STR.getValue() | ItemUpgradeFlag.INC_MHP.getValue();
            }
      }

      return flag;
   }

   public static int filterForJobGloves(int itemID) {
      int flag = 0;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int rjob = ii.getReqJob(itemID);
      int rlevel = ii.getReqLevel(itemID);
      switch (rjob) {
         case -1:
            flag |= ItemUpgradeFlag.INC_PAD.getValue();
            break;
         case 0:
            flag = flag | ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_MAD.getValue();
            break;
         default:
            if ((rjob & flag(1)) != 0 || (rjob & flag(3)) != 0 || (rjob & flag(4)) != 0 || (rjob & flag(5)) != 0) {
               flag |= ItemUpgradeFlag.INC_PAD.getValue();
            } else if ((rjob & flag(2)) != 0) {
               flag |= ItemUpgradeFlag.INC_MAD.getValue();
            }
      }

      if (rlevel <= 60) {
         flag |= ItemUpgradeFlag.INC_PDD.getValue();
      }

      return flag;
   }

   public static int filterForJobWeapon(int itemID) {
      int flag = 0;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int rjob = ii.getReqJob(itemID);
      switch (rjob) {
         case -1:
            flag = flag | ItemUpgradeFlag.INC_STR.getValue() | ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_MHP.getValue();
            break;
         case 0:
            flag = flag
               | ItemUpgradeFlag.INC_PAD.getValue()
               | ItemUpgradeFlag.INC_STR.getValue()
               | ItemUpgradeFlag.INC_DEX.getValue()
               | ItemUpgradeFlag.INC_LUK.getValue()
               | ItemUpgradeFlag.INC_MHP.getValue()
               | ItemUpgradeFlag.INC_MAD.getValue()
               | ItemUpgradeFlag.INC_INT.getValue();
            break;
         default:
            if ((rjob & flag(1)) == 0) {
               if ((rjob & flag(2)) != 0) {
                  flag = flag | ItemUpgradeFlag.INC_MAD.getValue() | ItemUpgradeFlag.INC_INT.getValue();
               } else if ((rjob & flag(3)) != 0) {
                  flag = flag | ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_DEX.getValue();
               } else if ((rjob & flag(4)) != 0 || (rjob & flag(5)) != 0) {
                  flag = flag
                     | ItemUpgradeFlag.INC_PAD.getValue()
                     | ItemUpgradeFlag.INC_STR.getValue()
                     | ItemUpgradeFlag.INC_DEX.getValue()
                     | ItemUpgradeFlag.INC_LUK.getValue();
               }
            } else {
               flag = flag | ItemUpgradeFlag.INC_STR.getValue() | ItemUpgradeFlag.INC_PAD.getValue() | ItemUpgradeFlag.INC_MHP.getValue();
            }
      }

      return flag;
   }

   public static int getIncATTDEFGloves(int rLevel, int index, ItemUpgradeFlag flag) {
      if (rLevel > 70) {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 3;
            default:
               return 0;
         }
      } else {
         switch (index) {
            case 0:
               if (flag == ItemUpgradeFlag.INC_PDD) {
                  return 3;
               }
               break;
            case 1:
               if (flag == ItemUpgradeFlag.INC_PAD || flag == ItemUpgradeFlag.INC_MAD) {
                  return 1;
               }
               break;
            case 2:
               if (flag == ItemUpgradeFlag.INC_PAD || flag == ItemUpgradeFlag.INC_MAD) {
                  return 2;
               }
         }

         return 0;
      }
   }

   public static int getIncATTWeapon(int rLevel, int index) {
      if (rLevel < 60) {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 3;
            case 3:
               return 5;
         }
      } else if (rLevel < 120) {
         switch (index) {
            case 0:
               return 2;
            case 1:
               return 3;
            case 2:
               return 5;
            case 3:
               return 7;
         }
      } else {
         switch (index) {
            case 0:
               return 3;
            case 1:
               return 5;
            case 2:
               return 7;
            case 3:
               return 9;
         }
      }

      return 0;
   }

   public static int getIncPrimaryStatWeapon(int rLevel, int index) {
      if (rLevel < 60) {
         switch (index) {
            case 0:
            case 1:
               return 0;
            case 2:
               return 1;
            case 3:
               return 2;
         }
      } else if (rLevel < 120) {
         switch (index) {
            case 0:
               return 0;
            case 1:
               return 1;
            case 2:
               return 2;
            case 3:
               return 3;
         }
      } else {
         switch (index) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 3;
            case 3:
               return 4;
         }
      }

      return 0;
   }

   public static boolean IsArmorCategory(int itemID) {
      return !GameConstants.isCap(itemID)
            && !GameConstants.isCoat(itemID)
            && !GameConstants.isLongcoat(itemID)
            && !GameConstants.isPants(itemID)
            && !GameConstants.isGlove(itemID)
            && !GameConstants.isShoes(itemID)
            && !GameConstants.isShield(itemID)
            && !GameConstants.isCape(itemID)
         ? GameConstants.isShoulder(itemID)
         : true;
   }

   public static boolean IsAccessoryCategory(int itemID) {
      int num = itemID / 10000;
      return num - 101 <= 2 || num - 111 <= 3;
   }

   public static int GetScrollVestigeCost(int itemID, int index) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int rlevel = ii.getReqLevel(itemID);
      if (GameConstants.isGlove(itemID)) {
         int[][] arr = new int[][]{
            {2, 2, 3},
            {2, 2, 3},
            {2, 3, 4},
            {4, 6, 6},
            {5, 6, 8},
            {6, 8, 10},
            {7, 10, 11},
            {9, 11, 14},
            {18, 24, 29},
            {23, 30, 37},
            {29, 38, 45},
            {34, 45, 55},
            {125, 160, 195},
            {160, 205, 250},
            {200, 260, 310},
            {245, 320, 380},
            {295, 385, 460},
            {350, 455, 550},
            {410, 530, 650},
            {475, 610, 760},
            {545, 695, 880},
            {620, 785, 1010},
            {700, 880, 1150},
            {785, 980, 1300},
            {875, 1085, 1460},
            {970, 1195, 1630}
         };
         return arr[rlevel / 10][index];
      } else if (GameConstants.isWeapon(itemID)) {
         int[][] arr = new int[][]{
            {2, 3, 4, 5},
            {2, 3, 4, 5},
            {3, 4, 5, 6},
            {5, 7, 8, 10},
            {6, 8, 10, 12},
            {8, 10, 12, 14},
            {9, 12, 14, 17},
            {11, 14, 17, 20},
            {23, 30, 36, 43},
            {29, 38, 46, 55},
            {36, 47, 56, 67},
            {43, 56, 67, 80},
            {155, 200, 240, 290},
            {200, 260, 310, 370},
            {240, 320, 380, 460},
            {300, 390, 470, 570},
            {380, 470, 575, 690},
            {460, 560, 680, 830},
            {545, 660, 810, 985},
            {635, 770, 940, 1160},
            {735, 895, 1095, 1350},
            {860, 1030, 1280, 1570},
            {1000, 1180, 1465, 1815},
            {1155, 1350, 1675, 2095},
            {1335, 1535, 1900, 2410},
            {1535, 1745, 2155, 2760}
         };
         return arr[rlevel / 10][index];
      } else if (IsArmorCategory(itemID)) {
         int[][] arr = new int[][]{
            {1, 2, 2},
            {1, 2, 2},
            {2, 2, 3},
            {3, 4, 5},
            {4, 5, 6},
            {5, 6, 7},
            {5, 7, 8},
            {7, 8, 10},
            {14, 18, 22},
            {17, 23, 28},
            {22, 28, 34},
            {26, 34, 40},
            {95, 120, 145},
            {120, 155, 190},
            {150, 195, 230},
            {185, 240, 290},
            {225, 290, 345},
            {270, 345, 405},
            {320, 405, 470},
            {375, 470, 540},
            {435, 540, 615},
            {500, 615, 695},
            {570, 695, 780},
            {645, 780, 870},
            {725, 870, 965},
            {810, 965, 1065}
         };
         return arr[rlevel / 10][index];
      } else if (IsAccessoryCategory(itemID)) {
         int[][] arr = new int[][]{
            {1, 1, 2},
            {1, 1, 2},
            {2, 2, 3},
            {3, 3, 4},
            {3, 4, 5},
            {4, 5, 6},
            {5, 6, 7},
            {6, 7, 9},
            {12, 15, 18},
            {15, 19, 23},
            {18, 24, 28},
            {22, 28, 34},
            {80, 100, 120},
            {100, 130, 155},
            {125, 160, 195},
            {155, 200, 240},
            {190, 245, 290},
            {230, 295, 345},
            {275, 350, 406},
            {325, 410, 470},
            {380, 475, 540},
            {440, 545, 615},
            {505, 620, 695},
            {575, 700, 780},
            {650, 785, 870},
            {730, 875, 965}
         };
         return arr[rlevel / 10][index];
      } else if (GameConstants.isAndroidHeart(itemID)) {
         if (rlevel < 30) {
            return new int[]{2, 3, 4}[index];
         } else if (rlevel < 50) {
            return new int[]{5, 7, 8}[index];
         } else if (rlevel < 80) {
            return new int[]{11, 14, 18}[index];
         } else if (rlevel < 100) {
            return new int[]{22, 30, 37}[index];
         } else if (rlevel < 120) {
            return new int[]{36, 47, 56}[index];
         } else if (rlevel < 130) {
            return new int[]{51, 69, 82}[index];
         } else {
            return rlevel < 150 ? new int[]{120, 180, 240}[index] : new int[]{200, 280, 360}[index];
         }
      } else {
         return 9999;
      }
   }

   public static int getSuccessByIndex(int index, boolean fever) {
      switch (index) {
         case 0:
            return 100;
         case 1:
            if (fever) {
               return 95;
            }

            return 70;
         case 2:
            if (fever) {
               return 45;
            }

            return 30;
         case 3:
            if (fever) {
               return 25;
            }

            return 15;
         case 4:
            if (fever) {
               return 45;
            }

            return 30;
         case 5:
            if (fever) {
               return 10;
            }

            return 5;
         default:
            return 0;
      }
   }

   public static int flag(int jc) {
      return jc == 0 ? 0 : 1 << jc - 1;
   }
}
