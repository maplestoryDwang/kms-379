package network.auction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;
import objects.users.enchant.EquipStat;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemOptionLevelData;
import objects.users.enchant.StarForceHyperUpgrade;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public enum AuctionSearchManager {
   PotentialSoul_AllStatR(3),
   Potential_AllStat(4),
   NormalEx_Str(5),
   NormalEx_Dex(6),
   NormalEx_Int(7),
   NormalEx_Luk(8),
   NormalEx_Pad(9),
   NormalEx_Mad(10),
   All_BdR(11),
   NormalEx_PdR(12),
   NormalEx_MaxHP(13),
   NormalEx_MaxMP(14),
   NormalEx_DecLev(15),
   NormalEx_Damage(16),
   NormalEx_AllStatR(17),
   Normal_Tuc(18),
   Normal_KarmaCount(19),
   Normal_Jump(20),
   Normal_Speed(21),
   Normal_EquipTradeBlock(22),
   Normal_KarmaCountNone(23),
   Normal_Chuc(24),
   PotentialSoul_Str(25),
   PotentialSoul_Dex(26),
   PotentialSoul_Int(27),
   PotentialSoul_Luk(28),
   PotentialSoul_Pad(29),
   PotentialSoul_Mad(30),
   Potential_Pdd(31),
   Potential_Speed(32),
   Potential_Jump(33),
   PotentialSoul_PdR(34),
   Potential_DamR(35),
   PotentialSoul_PadR(36),
   PotentialSoul_MadR(37),
   Potential_StrR(38),
   Potential_DexR(39),
   Potential_IntR(40),
   Potential_LukR(41),
   PotentialSoul_CrR(42),
   Potential_AsrR(43),
   Potential_TerR(44),
   Potential_CrDamR(45),
   Potential_ReduceCooltime(46),
   Potential_DropR(47),
   Potential_MaxHPR(48),
   Potential_MaxMPR(49),
   Potential_SkillHaste(50),
   Potential_SkillMysticDoor(51),
   Potential_SkillSharpEyes(52),
   Potential_SkillHyperBody(53),
   Potential_SkillCombatOrders(54),
   Potential_SkillAdvancedBless(55),
   Potential_SkillPartyBooster(56),
   PotentialSoul_MaxHP(57),
   PotentialSoul_MaxMP(58),
   PotentialSoul_SkillLv(59),
   Potential_AttackOnHealHP(60),
   Potential_AttackOnHealMP(61),
   Potential_StrLv10(62),
   Potential_DexLv10(63),
   Potential_IntLv10(64),
   Potential_LukLv10(65),
   Potential_HealR(66),
   Potential_MesoR(67),
   Potential_PadLv10(68),
   Potential_MadLv10(69);

   private int flag;
   public static final Map<String, Map<String, Map<String, List<Pair<Integer, Integer>>>>> itemDetailCategoryMap = new HashMap<>();

   private AuctionSearchManager(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }

   public static AuctionSearchManager getFlag(int flag) {
      for (AuctionSearchManager f : values()) {
         if (f.flag == flag) {
            return f;
         }
      }

      return null;
   }

   public static void init() {
      String WZpath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));
      MapleData nameData = prov.getData("AuctionData.img");

      for (MapleData data : nameData.getChildren()) {
         if (data.getName().startsWith("ItemDetailCategory_")) {
            Map<String, Map<String, List<Pair<Integer, Integer>>>> infoMap = new HashMap<>();

            for (MapleData info : data) {
               Map<String, List<Pair<Integer, Integer>>> detailMap = new HashMap<>();

               for (MapleData subInfo : info) {
                  if (!subInfo.getName().equals("string")) {
                     List<Pair<Integer, Integer>> itemIDList = new ArrayList<>();

                     for (MapleData subData : subInfo) {
                        int begin = MapleDataTool.getInt("begin", subData, 0);
                        int end = MapleDataTool.getInt("end", subData, 0);
                        if (begin > 0 && end > 0) {
                           itemIDList.add(new Pair<>(begin, end));
                        }
                     }

                     detailMap.put(subInfo.getName(), itemIDList);
                  }
               }

               infoMap.put(info.getName(), detailMap);
            }

            itemDetailCategoryMap.put(data.getName(), infoMap);
         }
      }
   }

   public static List<AuctionItemPackage> setFilterItemType(
      List<AuctionItemPackage> filter, AuctionSearchManager.ClassifyFlag itemTypeFlag, int itemClass, int itemSemiClass
   ) {
      List<AuctionItemPackage> removeList;
      MapleItemInformationProvider ii;
      Map<String, Map<String, List<Pair<Integer, Integer>>>> itemBigData;
      String dataName;
      String semiDataName;
      removeList = new ArrayList<>();
      ii = MapleItemInformationProvider.getInstance();
      itemBigData = new HashMap<>();
      dataName = "";
      semiDataName = "";
      label271:
      switch (itemTypeFlag) {
         case Armor:
            itemBigData = itemDetailCategoryMap.getOrDefault("ItemDetailCategory_Armor", null);
            switch (itemClass) {
               case 0:
                  dataName = "default";
                  semiDataName = "default";
                  break label271;
               case 1:
                  dataName = "Armor";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Cap";
                        break label271;
                     case 2:
                        semiDataName = "Coat";
                        break label271;
                     case 3:
                        semiDataName = "Longcoat";
                        break label271;
                     case 4:
                        semiDataName = "Pants";
                        break label271;
                     case 5:
                        semiDataName = "Shoes";
                        break label271;
                     case 6:
                        semiDataName = "Glove";
                        break label271;
                     case 7:
                        semiDataName = "Shield";
                        break label271;
                     case 8:
                        semiDataName = "Cape";
                     default:
                        break label271;
                  }
               case 2:
                  dataName = "Accessory";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Face";
                        break label271;
                     case 2:
                        semiDataName = "Eye";
                        break label271;
                     case 3:
                        semiDataName = "Ear";
                        break label271;
                     case 4:
                        semiDataName = "Ring";
                        break label271;
                     case 5:
                        semiDataName = "Pendant";
                        break label271;
                     case 6:
                        semiDataName = "Belt";
                        break label271;
                     case 7:
                        semiDataName = "Medal";
                        break label271;
                     case 8:
                        semiDataName = "Shoulder";
                        break label271;
                     case 9:
                        semiDataName = "Pocket";
                        break label271;
                     case 10:
                        semiDataName = "Badge";
                        break label271;
                     case 11:
                        semiDataName = "Emblem";
                        break label271;
                     case 12:
                        semiDataName = "PowerSource";
                     default:
                        break label271;
                  }
               case 3:
                  dataName = "Etc";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Mechanic";
                        break label271;
                     case 2:
                        semiDataName = "Android";
                        break label271;
                     case 3:
                        semiDataName = "MachineHeart";
                        break label271;
                     case 4:
                        semiDataName = "DragonCap";
                  }
               default:
                  break label271;
            }
         case Weapon:
            itemBigData = itemDetailCategoryMap.getOrDefault("ItemDetailCategory_Weapon", null);
            switch (itemClass) {
               case 0:
                  dataName = "default";
                  semiDataName = "default";
                  break label271;
               case 1:
                  dataName = "OneHandedWeapon";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "ShiningRod";
                        break label271;
                     case 2:
                        semiDataName = "SoulShooter";
                        break label271;
                     case 3:
                        semiDataName = "Desperado";
                        break label271;
                     case 4:
                        semiDataName = "EnergySword";
                        break label271;
                     case 5:
                        semiDataName = "Sword";
                        break label271;
                     case 6:
                        semiDataName = "Axe";
                        break label271;
                     case 7:
                        semiDataName = "Mace";
                        break label271;
                     case 8:
                        semiDataName = "Dagger";
                        break label271;
                     case 9:
                        semiDataName = "Cane";
                        break label271;
                     case 10:
                        semiDataName = "Wand";
                        break label271;
                     case 11:
                        semiDataName = "Staff";
                        break label271;
                     case 12:
                        semiDataName = "ESPLimiter";
                        break label271;
                     case 13:
                        semiDataName = "Chain";
                        break label271;
                     case 14:
                        semiDataName = "MagicGauntlet";
                        break label271;
                     case 15:
                        semiDataName = "Fan";
                        break label271;
                     case 16:
                        semiDataName = "Tuner";
                        break label271;
                     case 17:
                        semiDataName = "BreathShooter";
                     default:
                        break label271;
                  }
               case 2:
                  dataName = "TwoHandedWeapon";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Sword";
                        break label271;
                     case 2:
                        semiDataName = "Axe";
                        break label271;
                     case 3:
                        semiDataName = "Mace";
                        break label271;
                     case 4:
                        semiDataName = "Spear";
                        break label271;
                     case 5:
                        semiDataName = "Polearm";
                        break label271;
                     case 6:
                        semiDataName = "Bow";
                        break label271;
                     case 7:
                        semiDataName = "CrossBow";
                        break label271;
                     case 8:
                        semiDataName = "ThrowingGlove";
                        break label271;
                     case 9:
                        semiDataName = "Knuckle";
                        break label271;
                     case 10:
                        semiDataName = "Gun";
                        break label271;
                     case 11:
                        semiDataName = "DualBow";
                        break label271;
                     case 12:
                        semiDataName = "HandCannon";
                        break label271;
                     case 13:
                        semiDataName = "GauntletRevolver";
                        break label271;
                     case 14:
                        semiDataName = "AncientBow";
                     default:
                        break label271;
                  }
               case 3:
                  dataName = "SubWeapon";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "HeroMedal";
                        break label271;
                     case 2:
                        semiDataName = "Rosario";
                        break label271;
                     case 3:
                        semiDataName = "Chain";
                        break label271;
                     case 4:
                        semiDataName = "Book";
                        break label271;
                     case 5:
                        semiDataName = "BowMasterFeather";
                        break label271;
                     case 6:
                        semiDataName = "CrossBowThimble";
                        break label271;
                     case 7:
                        semiDataName = "ShadowerSheath";
                        break label271;
                     case 8:
                        semiDataName = "Blade";
                        break label271;
                     case 9:
                        semiDataName = "NightLordPouch";
                        break label271;
                     case 10:
                        semiDataName = "ViperWristband";
                        break label271;
                     case 11:
                        semiDataName = "CaptainSight";
                        break label271;
                     case 12:
                        semiDataName = "CannonGunPowder";
                        break label271;
                     case 13:
                        semiDataName = "CygnusGem";
                        break label271;
                     case 14:
                        semiDataName = "AranPendulum";
                        break label271;
                     case 15:
                        semiDataName = "EvanPaper";
                        break label271;
                     case 16:
                        semiDataName = "Orb";
                        break label271;
                     case 17:
                        semiDataName = "MagicArrow";
                        break label271;
                     case 18:
                        semiDataName = "Card";
                        break label271;
                     case 19:
                        semiDataName = "EunwolFoxOrb";
                        break label271;
                     case 20:
                        semiDataName = "BattlemageOrb";
                        break label271;
                     case 21:
                        semiDataName = "WildHunterArrowHead";
                        break label271;
                     case 22:
                        semiDataName = "XenonControler";
                        break label271;
                     case 23:
                        semiDataName = "MechanicMagnum";
                        break label271;
                     case 24:
                        semiDataName = "KaiserNova";
                        break label271;
                     case 25:
                        semiDataName = "AngelicBusterSoulRing";
                        break label271;
                     case 26:
                        semiDataName = "ChessPiece";
                        break label271;
                     case 27:
                        semiDataName = "Charge";
                        break label271;
                     case 28:
                        semiDataName = "Transmitter";
                        break label271;
                     case 29:
                        semiDataName = "MagicWing";
                        break label271;
                     case 30:
                        semiDataName = "Pass";
                        break label271;
                     case 31:
                        semiDataName = "Relic";
                        break label271;
                     case 32:
                        semiDataName = "FanStone";
                        break label271;
                     case 33:
                        semiDataName = "Bracelet";
                        break label271;
                     case 34:
                        semiDataName = "ForceShield";
                        break label271;
                     case 35:
                        semiDataName = "SoulShield";
                        break label271;
                     case 36:
                        semiDataName = "WeaponBelt";
                        break label271;
                     case 37:
                        semiDataName = "Norigae";
                  }
               default:
                  break label271;
            }
         case Consume:
            itemBigData = itemDetailCategoryMap.getOrDefault("ItemDetailCategory_Consume", null);
            switch (itemClass) {
               case 0:
                  dataName = "default";
                  semiDataName = "default";
                  break label271;
               case 1:
                  dataName = "Potion";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Potion";
                        break label271;
                     case 2:
                        semiDataName = "Cure";
                     default:
                        break label271;
                  }
               case 2:
                  dataName = "Alchemy";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Potion";
                        break label271;
                     case 2:
                        semiDataName = "Elixer";
                        break label271;
                     case 3:
                        semiDataName = "Buff";
                     default:
                        break label271;
                  }
               case 3:
                  dataName = "Scroll";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Armor";
                        break label271;
                     case 2:
                        semiDataName = "Weapon";
                        break label271;
                     case 3:
                        semiDataName = "Enchant";
                        break label271;
                     case 4:
                        semiDataName = "White";
                        break label271;
                     case 5:
                        semiDataName = "Flame";
                        break label271;
                     case 6:
                        semiDataName = "Pet";
                        break label271;
                     case 7:
                        semiDataName = "Etc";
                     default:
                        break label271;
                  }
               case 4:
                  dataName = "Recipe";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Equip";
                        break label271;
                     case 2:
                        semiDataName = "Accessory";
                        break label271;
                     case 3:
                        semiDataName = "Alchemy";
                     default:
                        break label271;
                  }
               case 5:
                  dataName = "SkillBook";
                  semiDataName = "default";
                  break label271;
               case 6:
                  dataName = "Etc";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "MoveMap";
                        break label271;
                     case 2:
                        semiDataName = "Arrow";
                        break label271;
                     case 3:
                        semiDataName = "Etc";
                  }
               default:
                  break label271;
            }
         case Cash:
            itemBigData = itemDetailCategoryMap.getOrDefault("ItemDetailCategory_Cash", null);
            switch (itemClass) {
               case 0:
                  dataName = "default";
                  semiDataName = "default";
                  break label271;
               case 1:
                  dataName = "Enchant";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Cube";
                        break label271;
                     case 2:
                        semiDataName = "Scroll";
                     default:
                        break label271;
                  }
               case 2:
                  dataName = "Game";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Convenience";
                        break label271;
                     case 2:
                        semiDataName = "Shop";
                        break label271;
                     case 3:
                        semiDataName = "Messenger";
                        break label271;
                     case 4:
                        semiDataName = "WeatherEffect";
                     default:
                        break label271;
                  }
               case 3:
                  dataName = "Coordination";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Coupon";
                        break label271;
                     case 2:
                        semiDataName = "Weapon";
                        break label271;
                     case 3:
                        semiDataName = "Cap";
                        break label271;
                     case 4:
                        semiDataName = "Cape";
                        break label271;
                     case 5:
                        semiDataName = "Longcoat";
                        break label271;
                     case 6:
                        semiDataName = "Coat";
                        break label271;
                     case 7:
                        semiDataName = "Pants";
                        break label271;
                     case 8:
                        semiDataName = "Shoes";
                        break label271;
                     case 9:
                        semiDataName = "Glove";
                        break label271;
                     case 10:
                        semiDataName = "Face";
                        break label271;
                     case 11:
                        semiDataName = "Ear";
                        break label271;
                     case 12:
                        semiDataName = "Ring";
                        break label271;
                     case 13:
                        semiDataName = "Eye";
                        break label271;
                     case 14:
                        semiDataName = "Effect";
                     default:
                        break label271;
                  }
               case 4:
                  dataName = "Beauty";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Hair";
                        break label271;
                     case 2:
                        semiDataName = "Cosmetic";
                        break label271;
                     case 3:
                        semiDataName = "Etc";
                        break label271;
                     case 4:
                        semiDataName = "Emotion";
                     default:
                        break label271;
                  }
               case 5:
                  dataName = "Pet";
                  switch (itemSemiClass) {
                     case 0:
                        semiDataName = "default";
                        break label271;
                     case 1:
                        semiDataName = "Pet";
                        break label271;
                     case 2:
                        semiDataName = "PetEquip";
                        break label271;
                     case 3:
                        semiDataName = "PetFood";
                        break label271;
                     case 4:
                        semiDataName = "PetSkill";
                     default:
                        break label271;
                  }
               case 6:
                  dataName = "Etc";
               default:
                  break label271;
            }
         case Etc:
            itemBigData = itemDetailCategoryMap.getOrDefault("ItemDetailCategory_Etc", null);
            switch (itemClass) {
               case 0:
                  dataName = "default";
                  semiDataName = "default";
                  break;
               case 1:
                  dataName = "Enchant";
                  semiDataName = "default";
                  break;
               case 2:
                  dataName = "Game";
                  semiDataName = "default";
            }
      }

      Map<String, List<Pair<Integer, Integer>>> data = itemBigData.getOrDefault(dataName, null);
      if (data != null) {
         List<Pair<Integer, Integer>> semiData = data.getOrDefault(semiDataName, null);
         if (semiData != null) {
            for (AuctionItemPackage itemPackage : filter) {
               Item item = itemPackage.getItem();
               boolean check = false;

               for (Pair<Integer, Integer> idList : semiData) {
                  boolean isInside = idList.left <= item.getItemId() && item.getItemId() <= idList.right;
                  if (isInside) {
                     check = true;
                     break;
                  }
               }

               if (!check && semiData.size() > 0) {
                  removeList.add(itemPackage);
               } else if (dataName.equals("Coordination") && !ii.isCash(item.getItemId())) {
                  removeList.add(itemPackage);
               }
            }
         }
      }

      return removeList;
   }

   public static boolean checkOptionValueCorrect(int optionValue, AuctionSearchManager flag) {
      if (flag.name().contains("All")) {
         return true;
      } else {
         AuctionSearchManager.OptionValueFlag optionFlag = AuctionSearchManager.OptionValueFlag.getFlag(optionValue);
         if (optionFlag != null) {
            switch (optionFlag) {
               case Normal:
                  return flag.name().contains("Normal");
               case ExGrade:
                  return flag.name().contains("Ex");
               case CsOption:
               case Potential:
               case Additional:
                  return flag.name().contains("Potential");
               case SoulWeapon:
                  return flag.name().contains("Soul");
            }
         }

         return false;
      }
   }

   public static boolean isItemHasOptions(AuctionSearchManager flag, int value, int optionValue, Item item) {
      if (!(item instanceof Equip)) {
         return false;
      } else {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         Equip equip = (Equip)item;
         AuctionSearchManager.OptionValueFlag optionFlag = AuctionSearchManager.OptionValueFlag.getFlag(optionValue);
         if (optionFlag == null) {
            return false;
         } else {
            int[] options = new int[0];
            if (optionFlag == AuctionSearchManager.OptionValueFlag.Potential) {
               options = new int[]{equip.getPotential1(), equip.getPotential2(), equip.getPotential3()};
            } else if (optionFlag == AuctionSearchManager.OptionValueFlag.Additional) {
               options = new int[]{equip.getPotential4(), equip.getPotential5(), equip.getPotential6()};
            }

            int getNormalValue = -1;
            int getExGradeValue = -1;
            int getCsOptionValue = -1;
            List<String> getPotentialValue = new ArrayList<>();
            switch (flag) {
               case PotentialSoul_AllStatR:
                  getPotentialValue.add("incSTRr");
                  getPotentialValue.add("incDEXr");
                  getPotentialValue.add("incINTr");
                  getPotentialValue.add("incLUKr");
                  break;
               case Potential_AllStat:
                  getPotentialValue.add("incSTR");
                  getPotentialValue.add("incDEX");
                  getPotentialValue.add("incINT");
                  getPotentialValue.add("incLUK");
                  break;
               case NormalEx_Str:
                  getNormalValue = equip.getStr();
                  getExGradeValue = equip.getTotalStr();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.STR, (short)getNormalValue);
                  }
                  break;
               case NormalEx_Dex:
                  getNormalValue = equip.getDex();
                  getExGradeValue = equip.getTotalDex();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.DEX, (short)getNormalValue);
                  }
                  break;
               case NormalEx_Int:
                  getNormalValue = equip.getInt();
                  getExGradeValue = equip.getTotalInt();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.INT, (short)getNormalValue);
                  }
                  break;
               case NormalEx_Luk:
                  getNormalValue = equip.getLuk();
                  getExGradeValue = equip.getTotalLuk();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.LUK, (short)getNormalValue);
                  }
                  break;
               case NormalEx_Pad:
                  getNormalValue = equip.getWatk();
                  getExGradeValue = equip.getTotalWatk();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.WATK, (short)getNormalValue);
                  }
                  break;
               case NormalEx_Mad:
                  getNormalValue = equip.getMatk();
                  getExGradeValue = equip.getTotalMatk();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.MATK, (short)getNormalValue);
                  }
                  break;
               case All_BdR:
                  getNormalValue = equip.getBossDamage();
                  getExGradeValue = equip.getTotalBossDamage();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.BOSS_DAMAGE, (short)getNormalValue);
                  }

                  getPotentialValue.add("incDAMr");
                  break;
               case NormalEx_PdR:
                  getNormalValue = equip.getIgnorePDR();
                  getExGradeValue = equip.getTotalIgnorePDR();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.IGNORE_PDR, (short)getNormalValue);
                  }
                  break;
               case NormalEx_MaxHP:
                  getNormalValue = equip.getHp();
                  getExGradeValue = equip.getTotalHp();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.MHP, (short)getNormalValue);
                  }
                  break;
               case NormalEx_MaxMP:
                  getNormalValue = equip.getMp();
                  getExGradeValue = equip.getTotalMp();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.MMP, (short)getNormalValue);
                  }
                  break;
               case NormalEx_DecLev:
                  getNormalValue = equip.getDownLevel();
                  getExGradeValue = equip.getTotalDownLevel();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.DOWNLEVEL, (short)getNormalValue);
                  }
                  break;
               case NormalEx_Damage:
                  getNormalValue = equip.getTotalDamage();
                  getExGradeValue = equip.getTotalMaxDamage();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.TOTAL_DAMAGE, (short)getNormalValue);
                  }
                  break;
               case NormalEx_AllStatR:
                  getNormalValue = equip.getAllStat();
                  getExGradeValue = equip.getTotalAllStat();
                  if (!equip.isAmazingHyperUpgradeUsed()) {
                     getExGradeValue -= StarForceHyperUpgrade.getHUStat(equip, EquipStat.ALL_STAT, (short)getNormalValue);
                  }
                  break;
               case Normal_Tuc:
                  getNormalValue = equip.getUpgradeSlots();
                  break;
               case Normal_KarmaCount:
                  getNormalValue = equip.getKarmaCount();
                  break;
               case Normal_Jump:
                  getNormalValue = equip.getJump();
                  break;
               case Normal_Speed:
                  getNormalValue = equip.getSpeed();
                  break;
               case Normal_KarmaCountNone:
                  return equip.getKarmaCount() == -1;
               case Normal_EquipTradeBlock:
                  return ii.isEquipTradeBlocked(equip.getItemId()) && !ItemFlag.POSSIBLE_TRADING.check(equip.getFlag());
               case Normal_Chuc:
                  getNormalValue = equip.getCHUC();
                  break;
               case PotentialSoul_Str:
                  getCsOptionValue = 11;
                  getPotentialValue.add("incSTR");
                  break;
               case PotentialSoul_Dex:
                  getCsOptionValue = 12;
                  getPotentialValue.add("incDEX");
                  break;
               case PotentialSoul_Int:
                  getCsOptionValue = 13;
                  getPotentialValue.add("incINT");
                  break;
               case PotentialSoul_Luk:
                  getCsOptionValue = 14;
                  getPotentialValue.add("incLUK");
                  break;
               case PotentialSoul_Pad:
                  getCsOptionValue = 21;
                  getPotentialValue.add("incPAD");
                  break;
               case PotentialSoul_Mad:
                  getCsOptionValue = 22;
                  getPotentialValue.add("incMAD");
                  break;
               case Potential_Pdd:
                  getCsOptionValue = 15;
                  getPotentialValue.add("incPDD");
                  break;
               case Potential_Speed:
                  getCsOptionValue = 16;
                  getPotentialValue.add("incSpeed");
                  break;
               case Potential_Jump:
                  getCsOptionValue = 17;
                  getPotentialValue.add("incJump");
                  break;
               case PotentialSoul_PdR:
                  getPotentialValue.add("ignoreTargetDEF");
                  break;
               case Potential_DamR:
                  getPotentialValue.add("ignoreDAMr");
                  break;
               case PotentialSoul_PadR:
                  getPotentialValue.add("incPADr");
                  break;
               case PotentialSoul_MadR:
                  getPotentialValue.add("incMADr");
                  break;
               case Potential_StrR:
                  getPotentialValue.add("incSTRr");
                  break;
               case Potential_DexR:
                  getPotentialValue.add("incDEXr");
                  break;
               case Potential_IntR:
                  getPotentialValue.add("incINTr");
                  break;
               case Potential_LukR:
                  getPotentialValue.add("incLUKr");
                  break;
               case PotentialSoul_CrR:
                  getPotentialValue.add("incCr");
                  break;
               case Potential_AsrR:
                  getPotentialValue.add("incAsrR");
                  break;
               case Potential_TerR:
                  getPotentialValue.add("incTerR");
                  break;
               case Potential_CrDamR:
                  getPotentialValue.add("incCriticaldamageMin");
                  getPotentialValue.add("incCriticaldamageMax");
                  break;
               case Potential_ReduceCooltime:
                  getPotentialValue.add("reduceCooltime");
                  break;
               case Potential_DropR:
                  getPotentialValue.add("incRewardProp");
                  break;
               case Potential_MaxHPR:
                  getPotentialValue.add("incMHPr");
                  break;
               case Potential_MaxMPR:
                  getPotentialValue.add("incMMPr");
                  break;
               case Potential_SkillHaste:
               case Potential_SkillMysticDoor:
               case Potential_SkillSharpEyes:
               case Potential_SkillHyperBody:
               case Potential_SkillCombatOrders:
               case Potential_SkillAdvancedBless:
               case Potential_SkillPartyBooster:
                  getPotentialValue.add("skillID");
                  break;
               case PotentialSoul_MaxHP:
                  getPotentialValue.add("incMHP");
                  break;
               case PotentialSoul_MaxMP:
                  getPotentialValue.add("incMMP");
                  break;
               case PotentialSoul_SkillLv:
                  getPotentialValue.add("incAllskill");
                  break;
               case Potential_AttackOnHealHP:
                  getPotentialValue.add("HP");
                  break;
               case Potential_AttackOnHealMP:
                  getPotentialValue.add("MP");
                  break;
               case Potential_StrLv10:
                  getPotentialValue.add("incSTRlv");
                  break;
               case Potential_DexLv10:
                  getPotentialValue.add("incDEXlv");
                  break;
               case Potential_IntLv10:
                  getPotentialValue.add("incINTlv");
                  break;
               case Potential_LukLv10:
                  getPotentialValue.add("incLUKlv");
                  break;
               case Potential_HealR:
                  getPotentialValue.add("RecoveryUP");
                  break;
               case Potential_MesoR:
                  getPotentialValue.add("incMesoProp");
                  break;
               case Potential_PadLv10:
                  getPotentialValue.add("incPADlv");
                  break;
               case Potential_MadLv10:
                  getPotentialValue.add("incMADlv");
            }

            if (getNormalValue >= 0 && optionFlag == AuctionSearchManager.OptionValueFlag.Normal) {
               return getNormalValue >= value;
            } else if (getExGradeValue >= 0 && optionFlag == AuctionSearchManager.OptionValueFlag.ExGrade) {
               return getExGradeValue - getNormalValue >= value;
            } else if (getCsOptionValue >= 0 && optionFlag == AuctionSearchManager.OptionValueFlag.CsOption) {
               return equip.getCsOption1() / 1000 == getCsOptionValue && equip.getCsOption1() % 1000 >= value
                  || equip.getCsOption2() / 1000 == getCsOptionValue && equip.getCsOption2() % 1000 >= value
                  || equip.getCsOption3() / 1000 == getCsOptionValue && equip.getCsOption3() % 1000 >= value;
            } else {
               if (!getPotentialValue.isEmpty()) {
                  switch (optionFlag) {
                     case Potential:
                     case Additional:
                        for (int option : options) {
                           if (option != 0) {
                              boolean valueCheck = potentialCheck(equip, option, getPotentialValue, value);
                              if (valueCheck) {
                                 return true;
                              }
                           }
                        }

                        return false;
                     case SoulWeapon:
                        return equip.getSoulPotential() != 0 && potentialCheck(equip, equip.getSoulPotential(), getPotentialValue, value);
                  }
               }

               return false;
            }
         }
      }
   }

   public static boolean potentialCheck(Equip equip, int potential, List<String> getPotentialValue, int value) {
      boolean valueCheck = true;

      try {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int reqLev = Math.min(19, ii.getReqLevel(equip.getItemId()) / 10);
         if (ii.getPotentialInfo(potential) == null) {
            return false;
         }

         ItemOptionLevelData potentialInfo = ii.getPotentialInfo(potential).get(reqLev);

         for (String name : getPotentialValue) {
            Object data = potentialInfo.getClass().getDeclaredField(name).get(potentialInfo);
            boolean func = true;
            if (data instanceof Integer) {
               func = (Integer)data < value;
            } else if (data instanceof Byte) {
               func = (Byte)data < value;
            } else if (data instanceof Short) {
               func = (Short)data < value;
            }

            if (func) {
               valueCheck = false;
               break;
            }
         }
      } catch (Exception var12) {
         System.out.println("잠재 체크 오류");
         var12.printStackTrace();
      }

      return valueCheck;
   }

   public static boolean checkItemIfAdd(List<Pair<Integer, Integer>> detailDataList, Item item, int optionValue, boolean isOptionAnd) {
      if (item != null && detailDataList != null) {
         int checkCount = 0;

         for (Pair<Integer, Integer> data : detailDataList) {
            AuctionSearchManager flag = getFlag(data.left);
            if (flag != null && checkOptionValueCorrect(optionValue, flag) && isItemHasOptions(flag, data.right, optionValue, item)) {
               checkCount++;
            }
         }

         return isOptionAnd ? checkCount == detailDataList.size() : checkCount > 0;
      } else {
         return false;
      }
   }

   public static List<AuctionItemPackage> searchEquipDetailOption(
      List<AuctionItemPackage> filter, int optionValue, List<Pair<Integer, Integer>> detailDataList, boolean isOptionAnd
   ) {
      ArrayList<AuctionItemPackage> itemList = new ArrayList<>();

      for (AuctionItemPackage item : filter) {
         if (checkItemIfAdd(detailDataList, item.getItem(), optionValue, isOptionAnd)) {
            itemList.add(item);
         }
      }

      return itemList;
   }

   public static enum ClassifyFlag {
      All(-1),
      Armor(0),
      Weapon(1),
      Consume(2),
      Cash(3),
      Etc(4);

      private int flag;

      private ClassifyFlag(int flag) {
         this.flag = flag;
      }

      public static AuctionSearchManager.ClassifyFlag getFlag(int flag) {
         for (AuctionSearchManager.ClassifyFlag f : values()) {
            if (f.flag == flag) {
               return f;
            }
         }

         return null;
      }
   }

   public static enum OptionValueFlag {
      Normal(0),
      ExGrade(1),
      CsOption(2),
      Potential(3),
      Additional(4),
      SoulWeapon(5);

      private int flag;

      private OptionValueFlag(int flag) {
         this.flag = flag;
      }

      public static AuctionSearchManager.OptionValueFlag getFlag(int flag) {
         for (AuctionSearchManager.OptionValueFlag f : values()) {
            if (f.flag == flag) {
               return f;
            }
         }

         return null;
      }
   }
}
