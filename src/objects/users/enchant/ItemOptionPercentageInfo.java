package objects.users.enchant;

import database.DBConfig;

public class ItemOptionPercentageInfo {
   public static int getItemOptionPercentageInfo(GradeRandomOption option, int potentialId) {
      switch (option) {
         case Red:
         case Black:
         case Amazing:
            switch (potentialId) {
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 13:
                  return 6;
               case 7:
               case 8:
               default:
                  switch (potentialId) {
                     case 10001:
                     case 10002:
                     case 10003:
                     case 10004:
                     case 10005:
                     case 10006:
                     case 10041:
                     case 10042:
                     case 10043:
                     case 10044:
                        return 6;
                     case 10009:
                     case 10010:
                     case 10011:
                     case 10012:
                     case 10013:
                     case 10045:
                     case 10046:
                     case 10053:
                     case 10081:
                        return 4;
                     case 10051:
                     case 10052:
                     case 10055:
                     case 10070:
                     case 10202:
                     case 10207:
                     case 10222:
                     case 10227:
                     case 10232:
                     case 10237:
                     case 10242:
                     case 10247:
                     case 10291:
                        return 2;
                     default:
                        switch (potentialId) {
                           case 20041:
                           case 20042:
                           case 20043:
                           case 20044:
                           case 20045:
                           case 20046:
                              return 5;
                           case 20051:
                           case 20052:
                           case 20055:
                           case 20070:
                           case 20086:
                           case 20202:
                           case 20207:
                           case 20291:
                              return 2;
                           case 20053:
                           case 20366:
                           case 20401:
                           case 20406:
                              return 3;
                           default:
                              switch (potentialId) {
                                 case 30041:
                                 case 30042:
                                 case 30043:
                                 case 30044:
                                    return 5;
                                 case 30045:
                                 case 30046:
                                    return 6;
                                 case 30051:
                                 case 30052:
                                 case 30070:
                                 case 30291:
                                 case 30602:
                                    return 3;
                                 case 30053:
                                 case 30055:
                                 case 30086:
                                 case 30356:
                                 case 30357:
                                 case 30366:
                                 case 30371:
                                 case 30376:
                                 case 30551:
                                 case 31002:
                                 case 31003:
                                 case 31004:
                                    return 4;
                                 case 30091:
                                 case 30092:
                                 case 30093:
                                 case 30094:
                                    if (DBConfig.isGanglim) {
                                       return 0;
                                    }

                                    return 1;
                                 case 30377:
                                    return 2;
                                 default:
                                    switch (potentialId) {
                                       case 40041:
                                       case 40042:
                                       case 40043:
                                       case 40044:
                                       case 40045:
                                       case 40046:
                                          return 4;
                                       case 40051:
                                       case 40052:
                                       case 40055:
                                       case 40070:
                                       case 40291:
                                       case 40292:
                                       case 40557:
                                       case 40603:
                                          return 2;
                                       case 40053:
                                          if (DBConfig.isGanglim && option == GradeRandomOption.Amazing) {
                                             return 0;
                                          }

                                          return 4;
                                       case 40056:
                                       case 40086:
                                       case 40650:
                                       case 40656:
                                       case 41005:
                                       case 41006:
                                       case 41007:
                                          return 3;
                                       case 40091:
                                       case 40092:
                                          if (DBConfig.isGanglim) {
                                             return 0;
                                          }

                                          return 2;
                                       case 40356:
                                       case 40357:
                                       case 40366:
                                       case 40371:
                                       case 40501:
                                       case 40502:
                                       case 40556:
                                          if (DBConfig.isGanglim && option == GradeRandomOption.Amazing) {
                                             return 0;
                                          }

                                          return 3;
                                       case 40601:
                                          return 0;
                                       case 40602:
                                          if (DBConfig.isGanglim && option == GradeRandomOption.Amazing) {
                                             return 0;
                                          }

                                          return 2;
                                       default:
                                          return 0;
                                    }
                              }
                        }
                  }
               case 9:
               case 10:
               case 11:
               case 12:
                  return 4;
            }
         case Normal:
         case Occult:
         case Master:
         case Meister:
            switch (potentialId) {
               case 1:
               case 2:
               case 3:
               case 4:
                  return 4;
               case 5:
               case 6:
               case 9:
               case 10:
               case 13:
               case 901:
               case 902:
               case 903:
               case 904:
               case 905:
                  return 6;
               case 11:
               case 12:
                  return 2;
               default:
                  switch (potentialId) {
                     case 10001:
                     case 10002:
                     case 10003:
                     case 10004:
                     case 10011:
                     case 10012:
                     case 10041:
                     case 10042:
                     case 10043:
                     case 10044:
                     case 10081:
                        return 4;
                     case 10005:
                     case 10006:
                     case 10009:
                     case 10010:
                     case 10013:
                     case 10045:
                     case 10046:
                     case 10053:
                     case 10151:
                     case 10156:
                     case 10201:
                     case 10206:
                     case 10221:
                     case 10226:
                     case 10231:
                     case 10236:
                     case 10241:
                     case 10246:
                        return 6;
                     case 10051:
                     case 10052:
                     case 10055:
                     case 10070:
                     case 10291:
                        return 2;
                     default:
                        switch (potentialId) {
                           case 20041:
                           case 20042:
                           case 20043:
                           case 20044:
                              return 4;
                           case 20045:
                           case 20046:
                           case 20053:
                           case 20201:
                           case 20206:
                           case 20351:
                           case 20352:
                           case 20353:
                           case 20366:
                           case 20401:
                           case 20406:
                              return 6;
                           case 20051:
                           case 20052:
                           case 20055:
                           case 20070:
                           case 20086:
                           case 20291:
                              return 2;
                           default:
                              switch (potentialId) {
                                 case 30041:
                                 case 30042:
                                 case 30043:
                                 case 30044:
                                 case 31001:
                                 case 31002:
                                 case 31004:
                                    return 4;
                                 case 30045:
                                 case 30046:
                                 case 30053:
                                 case 30356:
                                 case 30357:
                                 case 30366:
                                 case 30371:
                                 case 30551:
                                 case 30701:
                                 case 30702:
                                    return 6;
                                 case 30051:
                                 case 30052:
                                 case 30055:
                                 case 30070:
                                 case 30086:
                                 case 30291:
                                 case 30602:
                                    return 2;
                                 default:
                                    switch (potentialId) {
                                       case 40041:
                                       case 40042:
                                       case 40043:
                                       case 40044:
                                       case 40086:
                                       case 40557:
                                          return 8;
                                       case 40045:
                                       case 40046:
                                       case 40053:
                                       case 40356:
                                       case 40357:
                                       case 40366:
                                       case 40371:
                                       case 40376:
                                       case 40377:
                                       case 40501:
                                       case 40502:
                                       case 40551:
                                       case 40556:
                                       case 40650:
                                       case 40656:
                                       case 40701:
                                       case 40702:
                                       case 40703:
                                       case 41005:
                                       case 41007:
                                          return 12;
                                       case 40051:
                                       case 40052:
                                       case 40055:
                                       case 40070:
                                       case 40291:
                                       case 40602:
                                          return 4;
                                       case 40056:
                                          return 16;
                                       case 40292:
                                       case 40603:
                                          return 2;
                                       case 40601:
                                          return 0;
                                       default:
                                          return 0;
                                    }
                              }
                        }
                  }
            }
         case Additional:
         case OccultAdditional:
         case AmazingAdditional:
            switch (potentialId) {
               case 2001:
               case 2002:
               case 2003:
               case 2004:
               case 2011:
               case 2012:
               case 2015:
               case 2016:
                  return 4;
               case 2005:
               case 2006:
               case 2009:
               case 2010:
               case 2013:
                  return 6;
               case 2007:
               case 2008:
               case 2014:
               default:
                  switch (potentialId) {
                     case 12001:
                     case 12002:
                     case 12003:
                     case 12004:
                     case 12005:
                     case 12006:
                     case 12009:
                     case 12010:
                     case 12013:
                     case 12015:
                     case 12016:
                     case 12017:
                     case 12018:
                     case 12082:
                     case 12801:
                        return 6;
                     case 12011:
                     case 12012:
                     case 12019:
                     case 12020:
                     case 12041:
                     case 12042:
                     case 12043:
                     case 12044:
                     case 12045:
                     case 12046:
                     case 12047:
                     case 12048:
                     case 12049:
                     case 12050:
                     case 12053:
                     case 12055:
                     case 12081:
                        return 4;
                     case 12051:
                     case 12052:
                     case 12070:
                        return 2;
                     default:
                        switch (potentialId) {
                           case 22001:
                           case 22002:
                           case 22003:
                           case 22004:
                           case 22005:
                           case 22006:
                           case 22009:
                           case 22010:
                           case 22013:
                           case 22045:
                           case 22046:
                           case 22053:
                           case 22057:
                           case 22058:
                           case 22059:
                           case 22060:
                           case 22201:
                           case 22206:
                              return 6;
                           case 22011:
                           case 22012:
                           case 22041:
                           case 22042:
                           case 22043:
                           case 22044:
                           case 22051:
                           case 22052:
                           case 22086:
                           case 22087:
                           case 22291:
                              return 4;
                           case 22055:
                           case 22070:
                              return 2;
                           default:
                              switch (potentialId) {
                                 case 32001:
                                 case 32002:
                                 case 32003:
                                 case 32004:
                                 case 32005:
                                 case 32006:
                                 case 32013:
                                 case 32045:
                                 case 32046:
                                 case 32059:
                                 case 32060:
                                 case 32061:
                                 case 32062:
                                 case 32201:
                                 case 32206:
                                 case 32551:
                                    return 6;
                                 case 32011:
                                 case 32012:
                                 case 32041:
                                 case 32042:
                                 case 32043:
                                 case 32044:
                                 case 32051:
                                 case 32053:
                                 case 32055:
                                 case 32057:
                                 case 32086:
                                 case 32087:
                                    return 4;
                                 case 32070:
                                 case 32291:
                                 case 32601:
                                    return 2;
                                 case 32091:
                                 case 32092:
                                 case 32093:
                                 case 32094:
                                    if (DBConfig.isGanglim) {
                                       return 0;
                                    }

                                    return 4;
                                 default:
                                    switch (potentialId) {
                                       case 42001:
                                       case 42002:
                                       case 42003:
                                       case 42004:
                                       case 42005:
                                       case 42006:
                                       case 42013:
                                       case 42045:
                                       case 42046:
                                       case 42055:
                                       case 42063:
                                       case 42064:
                                       case 42065:
                                       case 42066:
                                       case 42501:
                                       case 42551:
                                       case 42556:
                                       case 42650:
                                       case 42656:
                                          return 6;
                                       case 42011:
                                       case 42012:
                                       case 42041:
                                       case 42042:
                                       case 42043:
                                       case 42044:
                                       case 42051:
                                       case 42053:
                                       case 42057:
                                       case 42059:
                                       case 42060:
                                       case 42086:
                                       case 42087:
                                          return 4;
                                       case 42070:
                                       case 42292:
                                       case 42602:
                                          return 2;
                                       case 42091:
                                       case 42092:
                                       case 42093:
                                       case 42094:
                                          if (DBConfig.isGanglim) {
                                             return 0;
                                          }

                                          return 4;
                                       case 42095:
                                       case 42096:
                                          if (DBConfig.isGanglim) {
                                             return 0;
                                          }

                                          return 2;
                                    }
                              }
                        }
                  }
            }
      }

      return 0;
   }

   public static ItemOptionPercentageInfo.ItemGradePercentageInfo getItemGradePercentageInfo(GradeRandomOption option, int grade) {
      switch (option) {
         case Red:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 0, 1000, 100);
               case 1:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 600, 1000, 100);
               case 2:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 200, 1000, 100);
               case 3:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 70, 1000, 100);
               default:
                  return null;
            }
         case Black:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 0, 2000, 500);
               case 1:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 1500, 2000, 500);
               case 2:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 350, 2000, 500);
               case 3:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(10000, 100, 2000, 500);
            }
         case Amazing:
         case Normal:
         default:
            break;
         case Occult:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 0, 999, 999);
               case 1:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 9901, 9901, 9901);
               default:
                  return null;
            }
         case Master:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 0, 166667, 166667);
               case 1:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 47619, 47619, 47619);
               case 2:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 11858, 11858, 11858);
               default:
                  return null;
            }
         case Meister:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 0, 166667, 166667);
               case 1:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 79994, 79994, 79994);
               case 2:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 20000, 16959, 16959);
               case 3:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 7000, 1996, 1996);
               default:
                  return null;
            }
         case Additional:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 0, 19608, 19608);
               case 1:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 47619, 47619, 47619);
               case 2:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 19608, 19608, 19608);
               case 3:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 7000, 4975, 4975);
               default:
                  return null;
            }
         case OccultAdditional:
            switch (grade) {
               case 0:
                  return new ItemOptionPercentageInfo.ItemGradePercentageInfo(1000000, 0, 19608, 19608);
            }
      }

      return null;
   }

   static class ItemGradePercentageInfo {
      private static int randomValue;
      private static int firstLine;
      private static int secondLine;
      private static int thirdLine;

      public ItemGradePercentageInfo(int randomValue, int firstLine, int secondLine, int thirdLine) {
         ItemOptionPercentageInfo.ItemGradePercentageInfo.randomValue = randomValue;
         ItemOptionPercentageInfo.ItemGradePercentageInfo.firstLine = firstLine;
         ItemOptionPercentageInfo.ItemGradePercentageInfo.secondLine = secondLine;
         ItemOptionPercentageInfo.ItemGradePercentageInfo.thirdLine = thirdLine;
      }

      public static int getRandomValue() {
         return randomValue;
      }

      public static void setRandomValue(int randomValue) {
         ItemOptionPercentageInfo.ItemGradePercentageInfo.randomValue = randomValue;
      }

      public static int getFirstLine() {
         return firstLine;
      }

      public static void setFirstLine(int firstLine) {
         ItemOptionPercentageInfo.ItemGradePercentageInfo.firstLine = firstLine;
      }

      public static int getSecondLine() {
         return secondLine;
      }

      public static void setSecondLine(int secondLine) {
         ItemOptionPercentageInfo.ItemGradePercentageInfo.secondLine = secondLine;
      }

      public static int getThirdLine() {
         return thirdLine;
      }

      public static void setThirdLine(int thirdLine) {
         ItemOptionPercentageInfo.ItemGradePercentageInfo.thirdLine = thirdLine;
      }

      public static int getPercentageInfo(int lineNumber) {
         switch (lineNumber) {
            case 0:
               return getFirstLine();
            case 1:
               return getSecondLine();
            case 2:
               return getThirdLine();
            default:
               return 0;
         }
      }
   }
}
