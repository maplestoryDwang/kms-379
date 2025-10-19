package objects.users.enchant;

import constants.GameConstants;
import database.DBConfig;
import java.util.List;
import network.game.processors.EnchantHandler;
import objects.item.Equip;
import objects.item.MapleItemInformationProvider;
import objects.utils.Pair;

public class StarForceHyperUpgrade {
   static int[] mesoEnchants = new int[]{5, 10, 15, 20, 25, 30, 36, 45, 55, 70, 90, 110, 135, 165, 195, 225, 275, 375, 525, 775};
   static List<Pair<String, Integer>> mesoEnchantsH = List.of(
      new Pair<>("△", 975), new Pair<>("★", 1175), new Pair<>("★△", 1425), new Pair<>("★★", 1675), new Pair<>("★★△", 1975), new Pair<>("★★★", 2275)
   );

   public static short getHUStat(Equip equip, EquipStat f, short value) {
      if (DBConfig.isGanglim && GameConstants.isGenesisWeapon(equip.getItemId())) {
         return 0;
      } else {
         short num = value;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         if (equip != null && equip.getItemId() / 10000 != 119 && equip.getItemId() / 10000 != 135 && ii.getSlots(equip.getItemId()) > 0) {
            int equipFlag = 0;
            int reqJob = ii.getReqJob(equip.getItemId());
            if (reqJob == 0 || reqJob == -1 || (reqJob & flag(1)) != 0 || (reqJob & flag(3)) != 0 || (reqJob & flag(5)) != 0) {
               equipFlag = equipFlag | EquipStat.STR.getValue() | EquipStat.DEX.getValue() | EquipStat.WATK.getValue();
            }

            if (reqJob == 0 || (reqJob & flag(2)) != 0) {
               equipFlag = equipFlag | EquipStat.INT.getValue() | EquipStat.LUK.getValue() | EquipStat.MATK.getValue();
            }

            if (reqJob == 0 || (reqJob & flag(4)) != 0) {
               equipFlag = equipFlag | EquipStat.DEX.getValue() | EquipStat.LUK.getValue() | EquipStat.WATK.getValue();
            }

            if ((
                  (
                        equipFlag
                           | EquipStat.MHP.getValue()
                           | EquipStat.MMP.getValue()
                           | EquipStat.ACC.getValue()
                           | EquipStat.AVOID.getValue()
                           | EquipStat.SPEED.getValue()
                           | EquipStat.JUMP.getValue()
                     )
                     & f.getValue()
               )
               != 0) {
               int tempValue = value;

               for (int index = 0; index < equip.getCHUC(); index++) {
                  if (EnchantHandler.isSuperial(equip.getItemId()).right) {
                     String v = EnchantHandler.isSuperial(equip.getItemId()).left;
                     if (index < 5) {
                        switch (v) {
                           case "Helisium":
                              if (f == EquipStat.STR || f == EquipStat.DEX || f == EquipStat.INT || f == EquipStat.LUK) {
                                 switch (index) {
                                    case 0:
                                       value = (short)(value + 4);
                                       break;
                                    case 1:
                                       value = (short)(value + 5);
                                       break;
                                    case 2:
                                       value = (short)(value + 7);
                                       break;
                                    case 3:
                                       value = (short)(value + 10);
                                       break;
                                    case 4:
                                       value = (short)(value + 14);
                                 }
                              }
                              break;
                           case "Nova":
                              if (f == EquipStat.STR || f == EquipStat.DEX || f == EquipStat.INT || f == EquipStat.LUK) {
                                 switch (index) {
                                    case 0:
                                       value = (short)(value + 19);
                                       break;
                                    case 1:
                                       value = (short)(value + 10);
                                       break;
                                    case 2:
                                       value = (short)(value + 12);
                                       break;
                                    case 3:
                                       value = (short)(value + 15);
                                       break;
                                    case 4:
                                       value = (short)(value + 19);
                                 }
                              }
                              break;
                           case "Tilent":
                           case "MindPendent":
                              if (f == EquipStat.STR || f == EquipStat.DEX || f == EquipStat.INT || f == EquipStat.LUK) {
                                 switch (index) {
                                    case 0:
                                       value = (short)(value + 19);
                                       break;
                                    case 1:
                                       value = (short)(value + 20);
                                       break;
                                    case 2:
                                       value = (short)(value + 22);
                                       break;
                                    case 3:
                                       value = (short)(value + 25);
                                       break;
                                    case 4:
                                       value = (short)(value + 29);
                                 }
                              }
                        }
                     } else {
                        switch (v) {
                           case "Helisium":
                              if (f == EquipStat.WATK || f == EquipStat.MATK) {
                                 switch (index) {
                                    case 5:
                                       value = (short)(value + 3);
                                       break;
                                    case 6:
                                       value = (short)(value + 4);
                                       break;
                                    case 7:
                                       value = (short)(value + 5);
                                       break;
                                    case 8:
                                       value = (short)(value + 6);
                                       break;
                                    case 9:
                                       value = (short)(value + 7);
                                       break;
                                    case 10:
                                       value = (short)(value + 9);
                                       break;
                                    case 11:
                                       value = (short)(value + 11);
                                       break;
                                    case 12:
                                       value = (short)(value + 13);
                                       break;
                                    case 13:
                                       value = (short)(value + 15);
                                       break;
                                    case 14:
                                       value = (short)(value + 17);
                                 }
                              }
                              break;
                           case "Nova":
                              if (f == EquipStat.WATK || f == EquipStat.MATK) {
                                 switch (index) {
                                    case 5:
                                       value = (short)(value + 6);
                                       break;
                                    case 6:
                                       value = (short)(value + 7);
                                       break;
                                    case 7:
                                       value = (short)(value + 8);
                                       break;
                                    case 8:
                                       value = (short)(value + 9);
                                       break;
                                    case 9:
                                       value = (short)(value + 10);
                                       break;
                                    case 10:
                                       value = (short)(value + 12);
                                       break;
                                    case 11:
                                       value = (short)(value + 14);
                                       break;
                                    case 12:
                                       value = (short)(value + 16);
                                       break;
                                    case 13:
                                       value = (short)(value + 18);
                                       break;
                                    case 14:
                                       value = (short)(value + 20);
                                 }
                              }
                              break;
                           case "Tilent":
                           case "MindPendent":
                              if (f == EquipStat.WATK || f == EquipStat.MATK) {
                                 switch (index) {
                                    case 5:
                                       value = (short)(value + 9);
                                       break;
                                    case 6:
                                       value = (short)(value + 10);
                                       break;
                                    case 7:
                                       value = (short)(value + 11);
                                       break;
                                    case 8:
                                       value = (short)(value + 12);
                                       break;
                                    case 9:
                                       value = (short)(value + 13);
                                       break;
                                    case 10:
                                       value = (short)(value + 15);
                                       break;
                                    case 11:
                                       value = (short)(value + 17);
                                       break;
                                    case 12:
                                       value = (short)(value + 19);
                                       break;
                                    case 13:
                                       value = (short)(value + 21);
                                       break;
                                    case 14:
                                       value = (short)(value + 23);
                                 }
                              }
                        }
                     }
                  } else {
                     switch (f) {
                        case STR:
                        case DEX:
                        case INT:
                        case LUK:
                           if (index <= 4) {
                              value = (short)(value + 2);
                           } else if (index <= 14) {
                              value = (short)(value + 3);
                           } else if (index <= 21 || DBConfig.isGanglim) {
                              if (ii.getReqLevel(equip.getItemId()) < 138) {
                                 value = (short)(value + 7);
                              } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                 value = (short)(value + 9);
                              } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                 value = (short)(value + 11);
                              } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                 value = (short)(value + 13);
                              } else {
                                 value = (short)(value + 15);
                              }
                           }
                           break;
                        case MHP:
                        case MMP:
                           if (index <= 2) {
                              value = (short)(value + 5);
                           } else if (index <= 4) {
                              value = (short)(value + 10);
                           } else if (index <= 6) {
                              value = (short)(value + 15);
                           } else if (index <= 8) {
                              value = (short)(value + 20);
                           } else if (index <= 14) {
                              value = (short)(value + 25);
                           }
                           break;
                        case WATK:
                        case MATK:
                           if (GameConstants.isWeapon(equip.getItemId())) {
                              if (index >= 15) {
                                 if (f == EquipStat.WATK || f == EquipStat.MATK) {
                                    switch (index) {
                                       case 15:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = (short)(value + 6);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 7);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 8);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 9);
                                          } else {
                                             value = (short)(value + 13);
                                          }
                                          break;
                                       case 16:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = (short)(value + 7);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 8);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 9);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 9);
                                          } else {
                                             value = (short)(value + 13);
                                          }
                                          break;
                                       case 17:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = (short)(value + 7);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 8);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 9);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 10);
                                          } else {
                                             value = (short)(value + 14);
                                          }
                                          break;
                                       case 18:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = (short)(value + 8);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 9);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 10);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 11);
                                          } else {
                                             value = (short)(value + 14);
                                          }
                                          break;
                                       case 19:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = (short)(value + 9);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 10);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 11);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 12);
                                          } else {
                                             value = (short)(value + 15);
                                          }
                                          break;
                                       case 20:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = value;
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 11);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 12);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 13);
                                          } else {
                                             value = (short)(value + 16);
                                          }
                                          break;
                                       case 21:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = value;
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 12);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 13);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 14);
                                          } else {
                                             value = (short)(value + 17);
                                          }
                                          break;
                                       case 22:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = value;
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 30);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 31);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 32);
                                          } else {
                                             value = (short)(value + 34);
                                          }
                                          break;
                                       case 23:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = value;
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 31);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 32);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 33);
                                          } else {
                                             value = (short)(value + 35);
                                          }
                                          break;
                                       case 24:
                                          if (ii.getReqLevel(equip.getItemId()) < 138) {
                                             value = value;
                                          } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                             value = (short)(value + 32);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                             value = (short)(value + 33);
                                          } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                             value = (short)(value + 34);
                                          } else {
                                             value = (short)(value + 36);
                                          }
                                    }
                                 }
                              } else {
                                 int t = 0;
                                 if (DBConfig.isGanglim) {
                                    String owner = equip.getOwner();
                                    if (owner != null && !owner.isEmpty()) {
                                       if (!owner.contains("강")) {
                                          for (Pair<String, Integer> pair : mesoEnchantsH) {
                                             if (owner.equals(pair.left)) {
                                                t = pair.right;
                                                break;
                                             }
                                          }
                                       } else {
                                          int enchant = Integer.parseInt(owner.split("강")[0]);
                                          t = mesoEnchants[enchant - 1];
                                       }
                                    }
                                 }

                                 value += (short)((tempValue - t) / 50 + 1);
                              }
                           } else {
                              if (GameConstants.isGlove(equip.getItemId()) && (index >= 4 && index <= 10 && (index & 1) == 0 || index >= 12 && index <= 14)) {
                                 value++;
                              }

                              switch (index) {
                                 case 15:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = (short)(value + 7);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 8);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 9);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 10);
                                    } else {
                                       value = (short)(value + 12);
                                    }
                                    break;
                                 case 16:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = (short)(value + 8);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 9);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 10);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 11);
                                    } else {
                                       value = (short)(value + 13);
                                    }
                                    break;
                                 case 17:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = (short)(value + 9);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 10);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 11);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 12);
                                    } else {
                                       value = (short)(value + 14);
                                    }
                                    break;
                                 case 18:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = (short)(value + 10);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 11);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 12);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 13);
                                    } else {
                                       value = (short)(value + 15);
                                    }
                                    break;
                                 case 19:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = (short)(value + 11);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 12);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 13);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 14);
                                    } else {
                                       value = (short)(value + 16);
                                    }
                                    break;
                                 case 20:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = value;
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 13);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 14);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 15);
                                    } else {
                                       value = (short)(value + 17);
                                    }
                                    break;
                                 case 21:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = value;
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 15);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 16);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 17);
                                    } else {
                                       value = (short)(value + 19);
                                    }
                                    break;
                                 case 22:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = value;
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 17);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 18);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 19);
                                    } else {
                                       value = (short)(value + 21);
                                    }
                                    break;
                                 case 23:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = value;
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 19);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 20);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 21);
                                    } else {
                                       value = (short)(value + 23);
                                    }
                                    break;
                                 case 24:
                                    if (ii.getReqLevel(equip.getItemId()) < 138) {
                                       value = value;
                                    } else if (ii.getReqLevel(equip.getItemId()) < 150) {
                                       value = (short)(value + 21);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 160) {
                                       value = (short)(value + 22);
                                    } else if (ii.getReqLevel(equip.getItemId()) < 200) {
                                       value = (short)(value + 23);
                                    } else {
                                       value = (short)(value + 25);
                                    }
                              }
                           }
                           break;
                        case WDEF:
                        case MDEF:
                           value += (short)(value * (equip.getItemId() / 10000 == 105 ? 10 : 5) / 100);
                           break;
                        case SPEED:
                        case JUMP:
                           if (GameConstants.isShoes(equip.getItemId()) && index < 5) {
                              value++;
                           }
                     }
                  }
               }
            }
         }

         return (short)(value - num);
      }
   }

   public static int flag(int jc) {
      return jc == 0 ? 0 : 1 << jc - 1;
   }

   public static double getSuccessRateForStarForce(boolean isSuperial, int CHUC) {
      if (isSuperial) {
         switch (CHUC) {
            case 0:
            case 1:
               return 50.0;
            case 2:
               return 45.0;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
               return 40.0;
            case 9:
               return 37.0;
            case 10:
            case 11:
               return 35.0;
            case 12:
               return 3.0;
            case 13:
               return 2.0;
            case 14:
               return 1.0;
         }
      }

      switch (CHUC) {
         case 0:
            return 95.0;
         case 1:
            return 90.0;
         case 2:
         case 3:
            return 85.0;
         case 4:
            return 80.0;
         case 5:
            return 75.0;
         case 6:
            return 70.0;
         case 7:
            return 65.0;
         case 8:
            return 60.0;
         case 9:
            return 55.0;
         case 10:
            return 50.0;
         case 11:
            return 45.0;
         case 12:
            return 40.0;
         case 13:
            return 35.0;
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
            return 30.0;
         case 22:
            return 3.0;
         case 23:
            return 2.0;
         case 24:
            return 1.0;
         default:
            return 0.0;
      }
   }

   public static double getCurseRateForStarForce(boolean isSuperial, int CHUC) {
      if (isSuperial) {
         if (CHUC < 5) {
            return 0.0;
         }

         switch (CHUC) {
            case 5:
               return 1.8;
            case 6:
               return 3.0;
            case 7:
               return 4.2;
            case 8:
               return 6.0;
            case 9:
               return 9.5;
            case 10:
               return 13.0;
            case 11:
               return 16.3;
            case 12:
               return 48.5;
            case 13:
               return 49.0;
            case 14:
               return 49.5;
         }
      }

      if (CHUC < 15) {
         return 0.0;
      } else {
         switch (CHUC) {
            case 15:
            case 16:
            case 17:
               return 2.1;
            case 18:
            case 19:
               return 2.8;
            case 20:
            case 21:
               return 7.0;
            case 22:
               return 19.4;
            case 23:
               return 29.4;
            case 24:
               return 39.6;
            default:
               return 999.0;
         }
      }
   }

   public static long getCostForStarForce(int itemID, int rLevel, int CHUC, boolean preventDestruction, int discount) {
      if (EnchantHandler.isSuperial(itemID).right) {
         String num = EnchantHandler.isSuperial(itemID).left;
         switch (num) {
            case "Helisium":
               return 5956600L;
            case "Nova":
               return 18507900L;
            case "Tilent":
               return 55832200L;
         }
      }

      rLevel = (int)(Math.round(rLevel / 10.0) * 10.0);
      double num = CHUC >= 10
         ? (
            CHUC >= 15
               ? 1000.0 + rLevel * rLevel * rLevel * Math.pow(CHUC + 1, 2.7) / 200.0
               : 1000.0 + rLevel * rLevel * rLevel * Math.pow(CHUC + 1, 2.7) / 400.0
         )
         : 1000.0 + rLevel * rLevel * rLevel * (CHUC + 1) / 25.0;
      return Math.round((num * (preventDestruction ? 2.0 : 1.0) - num * discount / 100.0) / 100.0) * 100L;
   }

   public static boolean isDowngradable(int CHUC) {
      switch (CHUC) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 20:
            return false;
         case 16:
         case 17:
         case 18:
         case 19:
         default:
            return true;
      }
   }
}
