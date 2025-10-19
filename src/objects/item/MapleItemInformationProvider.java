package objects.item;

import constants.GameConstants;
import constants.OfficialRandomOption;
import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import network.models.CField;
import network.models.CWvsContext;
import objects.androids.BasicAndroid;
import objects.users.MapleCharacter;
import objects.users.enchant.BonusStat;
import objects.users.enchant.BonusStatPlaceType;
import objects.users.enchant.GradeRandomOption;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemOptionInfo;
import objects.users.enchant.ItemOptionLevelData;
import objects.users.enchant.ItemStateFlag;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.utils.Triple;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataDirectoryEntry;
import objects.wz.provider.MapleDataEntry;
import objects.wz.provider.MapleDataFileEntry;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;
import objects.wz.provider.MapleDataType;

public class MapleItemInformationProvider {
   private static final MapleItemInformationProvider instance = new MapleItemInformationProvider();
   protected final MapleDataProvider chrData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz"));
   protected final MapleDataProvider etcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
   protected final MapleDataProvider itemData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
   protected final MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
   protected final Map<Integer, ItemInformation> dataCache = new HashMap<>();
   protected final Map<String, List<Triple<String, Point, Point>>> afterImage = new HashMap<>();
   protected final Map<Integer, List<ItemOptionLevelData>> potentialCache = new HashMap<>();
   protected final Map<Integer, SecondaryStatEffect> itemEffects = new HashMap<>();
   protected final Map<Integer, SecondaryStatEffect> itemEffectsEx = new HashMap<>();
   protected final Map<Integer, Integer> scriptedItemCache = new HashMap<>();
   protected final Map<Integer, String> scriptedItemScriptCache = new HashMap<>();
   protected final Map<Integer, Integer> mobIds = new HashMap<>();
   protected final Map<Integer, Pair<Integer, Integer>> potLife = new HashMap<>();
   protected final Map<Integer, Pair<List<Integer>, List<Integer>>> androids = new HashMap<>();
   protected final Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> monsterBookSets = new HashMap<>();
   protected final Map<Integer, StructSetItem> setItems = new HashMap<>();
   protected final List<Pair<Integer, String>> itemNameCache = new ArrayList<>();
   protected final List<Pair<Integer, String>> installCache = new ArrayList<>();
   protected final List<Pair<Integer, String>> equipCache = new ArrayList<>();
   protected final List<Pair<Integer, Integer>> potentialOpCache = new LinkedList<>();
   protected final HashMap<Integer, HashMap<Integer, String>> potentialString = new HashMap<>();
   protected final Map<Integer, Integer> scrollUpgradeSlotUse = new HashMap<>();
   protected final Map<Integer, Integer> cursedCache = new HashMap<>();
   protected final Map<Integer, Integer> successCache = new HashMap<>();
   protected final Map<Integer, Integer> androidCache = new HashMap<>();
   protected final Map<Integer, Integer> chairTamingMobCache = new HashMap<>();
   protected final Map<Integer, BasicAndroid> androidBasicCache = new HashMap<>();
   protected final List<Integer> masterLabels = new ArrayList<>();
   protected final List<Integer> specialLabels = new ArrayList<>();
   protected final List<Integer> gmsCashList = new ArrayList<>();
   protected final Map<Integer, String> chairTypeCache = new HashMap<>();
   protected final Map<Integer, String> tamingMobTypeCache = new HashMap<>();
   protected final Map<Integer, Map<String, Short>> scrollInfo = new HashMap<>();
   private ItemInformation tmpInfo = null;

   public int getMasterPieceRewardItemID(boolean isGM, boolean baseRed, int baseID, int gender) {
      int rand = Randomizer.rand(0, 100);
      int count = 0;

      for (boolean masterLabel = rand <= (baseRed ? 8 : 3) || isGM; count <= 3000; count++) {
         if (masterLabel) {
            int id = this.masterLabels.get(Randomizer.rand(0, this.masterLabels.size() - 1));
            if (baseID == -1 || id / 10000 == baseID / 10000) {
               int g = id / 1000;
               int g2 = g % 10;
               if (g2 == gender || g2 > 1) {
                  return id;
               }
            }
         } else {
            int id = this.specialLabels.get(Randomizer.rand(0, this.specialLabels.size() - 1));
            if (baseID == -1 || id / 10000 == baseID / 10000) {
               int g = id / 1000;
               int g2 = g % 10;
               if (g2 == gender || g2 > 1) {
                  return id;
               }
            }
         }
      }

      return -1;
   }

   public List<Integer> getGMSCashItemList() {
      return this.gmsCashList;
   }

   public int getGMSCashItemGacha() {
      return this.gmsCashList.get(Randomizer.rand(0, this.gmsCashList.size() - 1));
   }

   public final boolean isJokerToSetItem(int itemID) {
      ItemInformation i = this.getItemInformation(itemID);
      return i == null ? false : i.jokerToSetItem;
   }

   public void runEtc() {
      if (this.setItems.isEmpty() && this.potentialCache.isEmpty()) {
         for (MapleData dat : this.etcData.getData("SetItemInfo.img")) {
            StructSetItem itemz = new StructSetItem();
            itemz.setItemID = Integer.parseInt(dat.getName());
            itemz.setItemName = MapleDataTool.getString("setItemName", dat, "");
            itemz.completeCount = (byte)MapleDataTool.getIntConvert("completeCount", dat, 0);
            itemz.jokerPossible = MapleDataTool.getIntConvert("jokerPossible", dat, 0) > 0;
            itemz.zeroWeaponJokerPossible = MapleDataTool.getIntConvert("zeroWeaponJokerPossible", dat, 0) > 0;

            for (MapleData level : dat.getChildByPath("ItemID")) {
               if (level.getType() != MapleDataType.INT) {
                  for (MapleData leve : level) {
                     if (!leve.getName().equals("representName") && !leve.getName().equals("typeName")) {
                        itemz.itemIDs.add(MapleDataTool.getInt(leve));
                        itemz.itemParts.add(MapleDataTool.getInt(leve) / 10000);
                     }
                  }
               } else {
                  itemz.itemIDs.add(MapleDataTool.getInt(level));
                  itemz.itemParts.add(MapleDataTool.getInt(level) / 10000);
               }
            }

            for (MapleData levelx : dat.getChildByPath("Effect")) {
               StructSetItem.SetItem itez = new StructSetItem.SetItem();
               itez.incPDD = MapleDataTool.getIntConvert("incPDD", levelx, 0);
               itez.incMDD = MapleDataTool.getIntConvert("incMDD", levelx, 0);
               itez.incSTR = MapleDataTool.getIntConvert("incSTR", levelx, 0);
               itez.incDEX = MapleDataTool.getIntConvert("incDEX", levelx, 0);
               itez.incINT = MapleDataTool.getIntConvert("incINT", levelx, 0);
               itez.incLUK = MapleDataTool.getIntConvert("incLUK", levelx, 0);
               itez.incACC = MapleDataTool.getIntConvert("incACC", levelx, 0);
               itez.incPAD = MapleDataTool.getIntConvert("incPAD", levelx, 0);
               itez.incMAD = MapleDataTool.getIntConvert("incMAD", levelx, 0);
               itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", levelx, 0);
               itez.incMHP = MapleDataTool.getIntConvert("incMHP", levelx, 0);
               itez.incMMP = MapleDataTool.getIntConvert("incMMP", levelx, 0);
               itez.incMHPr = MapleDataTool.getIntConvert("incMHPr", levelx, 0);
               itez.incMMPr = MapleDataTool.getIntConvert("incMMPr", levelx, 0);
               itez.incAllStat = MapleDataTool.getIntConvert("incAllStat", levelx, 0);
               itez.option1 = MapleDataTool.getIntConvert("Option/1/option", levelx, 0);
               itez.option2 = MapleDataTool.getIntConvert("Option/2/option", levelx, 0);
               itez.option1Level = MapleDataTool.getIntConvert("Option/1/level", levelx, 0);
               itez.option2Level = MapleDataTool.getIntConvert("Option/2/level", levelx, 0);
               MapleData activeSkill = levelx.getChildByPath("activeSkill");
               if (activeSkill != null) {
                  itez.activeSkill = new StructSetItem.ActiveSkill(activeSkill);
               }

               itemz.items.put(Integer.parseInt(levelx.getName()), itez);
            }

            this.setItems.put(itemz.setItemID, itemz);
         }

         MapleDataDirectoryEntry e = this.etcData.getRoot("Android_000.wz");

         for (MapleDataEntry d : e.getFiles()) {
            MapleData iz = this.etcData.getData(d.getName());
            List<Integer> hair = new ArrayList<>();
            List<Integer> face = new ArrayList<>();

            for (MapleData ds : iz.getChildByPath("costume/hair")) {
               hair.add(MapleDataTool.getInt(ds, 30000));
            }

            for (MapleData ds : iz.getChildByPath("costume/face")) {
               face.add(MapleDataTool.getInt(ds, 20000));
            }

            this.androids.put(Integer.parseInt(d.getName().substring(0, 4)), new Pair<>(hair, face));
         }

         for (MapleData d : this.etcData.getData("ItemPotLifeInfo.img")) {
            if (d.getChildByPath("info") != null && MapleDataTool.getInt("type", d.getChildByPath("info"), 0) == 1) {
               this.potLife
                  .put(
                     MapleDataTool.getInt("counsumeItem", d.getChildByPath("info"), 0),
                     new Pair<>(Integer.parseInt(d.getName()), d.getChildByPath("level").getChildren().size())
                  );
            }
         }

         List<Triple<String, Point, Point>> thePointK = new ArrayList<>();
         List<Triple<String, Point, Point>> thePointA = new ArrayList<>();
         MapleDataDirectoryEntry a = this.chrData.getRoot("Afterimage_000.wz");

         for (MapleDataEntry b : a.getFiles()) {
            MapleData iz = this.chrData.getData(b.getName());
            List<Triple<String, Point, Point>> thePoint = new ArrayList<>();
            Map<String, Pair<Point, Point>> dummy = new HashMap<>();

            for (MapleData i : iz) {
               for (MapleData xD : i) {
                  if (!xD.getName().contains("prone")
                     && !xD.getName().contains("double")
                     && !xD.getName().contains("triple")
                     && (!b.getName().contains("bow") && !b.getName().contains("Bow") || xD.getName().contains("shoot"))
                     && (!b.getName().contains("gun") && !b.getName().contains("cannon") || xD.getName().contains("shot"))) {
                     if (dummy.containsKey(xD.getName())) {
                        if (xD.getChildByPath("lt") != null) {
                           Point lt = (Point)xD.getChildByPath("lt").getData();
                           Point ourLt = (Point)dummy.get(xD.getName()).left;
                           if (lt.x < ourLt.x) {
                              ourLt.x = lt.x;
                           }

                           if (lt.y < ourLt.y) {
                              ourLt.y = lt.y;
                           }
                        }

                        if (xD.getChildByPath("rb") != null) {
                           Point rb = (Point)xD.getChildByPath("rb").getData();
                           Point ourRb = (Point)dummy.get(xD.getName()).right;
                           if (rb.x > ourRb.x) {
                              ourRb.x = rb.x;
                           }

                           if (rb.y > ourRb.y) {
                              ourRb.y = rb.y;
                           }
                        }
                     } else {
                        Point ltx = null;
                        Point rbx = null;
                        if (xD.getChildByPath("lt") != null) {
                           ltx = (Point)xD.getChildByPath("lt").getData();
                        }

                        if (xD.getChildByPath("rb") != null) {
                           rbx = (Point)xD.getChildByPath("rb").getData();
                        }

                        dummy.put(xD.getName(), new Pair<>(ltx, rbx));
                     }
                  }
               }
            }

            for (Entry<String, Pair<Point, Point>> ez : dummy.entrySet()) {
               if (ez.getKey().length() > 2 && ez.getKey().substring(ez.getKey().length() - 2, ez.getKey().length() - 1).equals("D")) {
                  thePointK.add(new Triple<>(ez.getKey(), (Point)ez.getValue().left, (Point)ez.getValue().right));
               } else if (ez.getKey().contains("PoleArm")) {
                  thePointA.add(new Triple<>(ez.getKey(), (Point)ez.getValue().left, (Point)ez.getValue().right));
               } else {
                  thePoint.add(new Triple<>(ez.getKey(), (Point)ez.getValue().left, (Point)ez.getValue().right));
               }
            }

            this.afterImage.put(b.getName().substring(0, b.getName().length() - 4), thePoint);
         }

         this.afterImage.put("katara", thePointK);
         this.afterImage.put("aran", thePointA);
      }
   }

   public final int getAndroid(int itemId) {
      if (this.androidCache.containsKey(itemId)) {
         return this.androidCache.get(itemId);
      } else {
         int i = MapleDataTool.getIntConvert("info/android", this.getItemData(itemId), 0);
         this.androidCache.put(itemId, i);
         return i;
      }
   }

   public final BasicAndroid getAndroidBasicSettings(int android) {
      if (this.androidBasicCache.containsKey(android)) {
         return this.androidBasicCache.get(android);
      } else {
         List<Integer> hairs = new LinkedList<>();
         List<Integer> faces = new LinkedList<>();
         String name = StringUtil.getLeftPaddedStr(android + "", '0', 4) + ".img";
         MapleData root = this.etcData.getData(name);
         if (root == null) {
            System.err.println("[오류] 서버의 Etc.wz에서 안드로이드 기본 셋팅이 저장된 " + name + " 파일을 발견하지 못했습니다.");
            return null;
         } else {
            MapleData costume = root.getChildByPath("costume");

            for (MapleData d : costume.getChildByPath("hair")) {
               hairs.add(MapleDataTool.getIntConvert(d));
            }

            for (MapleData d : costume.getChildByPath("face")) {
               faces.add(MapleDataTool.getIntConvert(d));
            }

            BasicAndroid basicAnd = new BasicAndroid(hairs, faces, MapleDataTool.getIntConvert(root.getChildByPath("info/gender")));
            this.androidBasicCache.put(android, basicAnd);
            return basicAnd;
         }
      }
   }

   public void runItems() {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_itemdata");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            this.initItemInformation(rs);
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT * FROM wz_itemequipdata ORDER BY itemid");
         rs = ps.executeQuery();

         while (rs.next()) {
            this.initItemEquipData(rs);
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT * FROM wz_itemadddata ORDER BY itemid");
         rs = ps.executeQuery();

         while (rs.next()) {
            this.initItemAddData(rs);
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT * FROM wz_itemrewarddata ORDER BY itemid");
         rs = ps.executeQuery();

         while (rs.next()) {
            this.initItemRewardData(rs);
         }

         rs.close();
         ps.close();

         for (Entry<Integer, ItemInformation> entry : this.dataCache.entrySet()) {
            if (GameConstants.getInventoryType(entry.getKey()) == MapleInventoryType.EQUIP) {
               this.finalizeEquipData(entry.getValue());
            }
         }

         this.cachePotentialItems();
         this.cachePotentialOption();
         this.cachePotentialString();
      } catch (SQLException var9) {
         var9.printStackTrace();
      }
   }

   public final List<ItemOptionLevelData> getPotentialInfo(int potId) {
      return this.potentialCache.get(potId);
   }

   public void cachePotentialOption() {
      for (MapleData data : this.itemData.getData("ItemOption.img")) {
         int potentialID = Integer.parseInt(data.getName());
         String name = MapleDataTool.getString("info/string", data).split(" :")[0];
         int type = MapleDataTool.getInt("info/optionType", data, -1);
         List<Integer> id = new ArrayList<>(100);
         switch (potentialID) {
            case 31001:
            case 31002:
            case 31003:
            case 31004:
            case 60002:
               break;
            default:
               if (potentialID <= 50000 && potentialID >= 10000 && potentialID - potentialID / 10000 * 10000 >= 40) {
                  this.potentialOpCache.add(new Pair<>(potentialID, type));
                  id.add(potentialID);
               }
         }
      }
   }

   public void cachePotentialString() {
      for (MapleData data : this.itemData.getData("ItemOption.img")) {
         int potentialID = Integer.parseInt(data.getName());
         this.potentialString.put(potentialID, new HashMap<>());
         String name = MapleDataTool.getString("info/string", data);

         for (MapleData datat : data.getChildByPath("level")) {
            List<String> keys = new ArrayList<>();

            for (MapleData datatt : datat.getChildren()) {
               if (!datatt.getName().equals("face")) {
                  keys.add(datatt.getName());
               }
            }

            String tempName = name;

            for (String key : keys) {
               tempName = tempName.replace("#" + key, String.valueOf(MapleDataTool.getInt(datat.getChildByPath(key))));
            }

            this.potentialString.get(potentialID).put(Integer.parseInt(datat.getName()), tempName);
         }
      }
   }

   public HashMap<Integer, HashMap<Integer, String>> getPotentialString() {
      return this.potentialString;
   }

   public void cachePotentialItems() {
      for (MapleData data : this.itemData.getData("ItemOption.img")) {
         List<ItemOptionLevelData> items = new LinkedList<>();

         for (MapleData level : data.getChildByPath("level")) {
            ItemOptionLevelData item = new ItemOptionLevelData();
            item.optionType = MapleDataTool.getIntConvert("info/optionType", data, 0);
            item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", data, 0);
            item.weight = MapleDataTool.getIntConvert("info/weight", data, 0);
            item.string = MapleDataTool.getString("info/string", level, "");
            item.face = MapleDataTool.getString("face", level, "");
            item.boss = MapleDataTool.getIntConvert("boss", level, 0) > 0;
            item.potentialID = Integer.parseInt(data.getName());
            item.attackType = (short)MapleDataTool.getIntConvert("attackType", level, 0);
            item.incMHP = (short)MapleDataTool.getIntConvert("incMHP", level, 0);
            item.incMMP = (short)MapleDataTool.getIntConvert("incMMP", level, 0);
            item.incSTR = (byte)MapleDataTool.getIntConvert("incSTR", level, 0);
            item.incDEX = (byte)MapleDataTool.getIntConvert("incDEX", level, 0);
            item.incINT = (byte)MapleDataTool.getIntConvert("incINT", level, 0);
            item.incLUK = (byte)MapleDataTool.getIntConvert("incLUK", level, 0);
            item.incACC = (byte)MapleDataTool.getIntConvert("incACC", level, 0);
            item.incEVA = (byte)MapleDataTool.getIntConvert("incEVA", level, 0);
            item.incAsrR = (byte)MapleDataTool.getIntConvert("incAsrR", level, 0);
            item.incTerR = (byte)MapleDataTool.getIntConvert("incTerR", level, 0);
            item.incSpeed = (byte)MapleDataTool.getIntConvert("incSpeed", level, 0);
            item.incJump = (byte)MapleDataTool.getIntConvert("incJump", level, 0);
            item.incPAD = (byte)MapleDataTool.getIntConvert("incPAD", level, 0);
            item.incMAD = (byte)MapleDataTool.getIntConvert("incMAD", level, 0);
            item.incPDD = (byte)MapleDataTool.getIntConvert("incPDD", level, 0);
            item.prop = (byte)MapleDataTool.getIntConvert("prop", level, 0);
            item.time = (byte)MapleDataTool.getIntConvert("time", level, 0);
            item.incSTRr = (byte)MapleDataTool.getIntConvert("incSTRr", level, 0);
            item.incDEXr = (byte)MapleDataTool.getIntConvert("incDEXr", level, 0);
            item.incINTr = (byte)MapleDataTool.getIntConvert("incINTr", level, 0);
            item.incLUKr = (byte)MapleDataTool.getIntConvert("incLUKr", level, 0);
            item.incMHPr = (byte)MapleDataTool.getIntConvert("incMHPr", level, 0);
            item.incMMPr = (byte)MapleDataTool.getIntConvert("incMMPr", level, 0);
            item.incACCr = (byte)MapleDataTool.getIntConvert("incACCr", level, 0);
            item.incEVAr = (byte)MapleDataTool.getIntConvert("incEVAr", level, 0);
            item.incPADr = (byte)MapleDataTool.getIntConvert("incPADr", level, 0);
            item.incMADr = (byte)MapleDataTool.getIntConvert("incMADr", level, 0);
            item.incPDDr = (byte)MapleDataTool.getIntConvert("incPDDr", level, 0);
            item.incCriticaldamageMax = (byte)MapleDataTool.getIntConvert("incCriticaldamageMax", level, 0);
            item.incCriticaldamageMin = (byte)MapleDataTool.getIntConvert("incCriticaldamageMin", level, 0);
            item.reduceCooltime = (byte)MapleDataTool.getIntConvert("reduceCooltime", level, 0);
            item.incMaxDamage = (byte)MapleDataTool.getIntConvert("incMaxDamage", level, 0);
            item.incCr = (byte)MapleDataTool.getIntConvert("incCr", level, 0);
            item.incDAMr = (byte)MapleDataTool.getIntConvert("incDAMr", level, 0);
            item.RecoveryHP = (byte)MapleDataTool.getIntConvert("RecoveryHP", level, 0);
            item.RecoveryMP = (byte)MapleDataTool.getIntConvert("RecoveryMP", level, 0);
            item.HP = (byte)MapleDataTool.getIntConvert("HP", level, 0);
            item.MP = (byte)MapleDataTool.getIntConvert("MP", level, 0);
            item.level = (byte)MapleDataTool.getIntConvert("level", level, 0);
            item.ignoreTargetDEF = (byte)MapleDataTool.getIntConvert("ignoreTargetDEF", level, 0);
            item.ignoreDAM = (byte)MapleDataTool.getIntConvert("ignoreDAM", level, 0);
            item.DAMreflect = (byte)MapleDataTool.getIntConvert("DAMreflect", level, 0);
            item.mpconReduce = (byte)MapleDataTool.getIntConvert("mpconReduce", level, 0);
            item.mpRestore = (byte)MapleDataTool.getIntConvert("mpRestore", level, 0);
            item.incMesoProp = (byte)MapleDataTool.getIntConvert("incMesoProp", level, 0);
            item.incRewardProp = (byte)MapleDataTool.getIntConvert("incRewardProp", level, 0);
            item.incAllskill = (byte)MapleDataTool.getIntConvert("incAllskill", level, 0);
            item.ignoreDAMr = (byte)MapleDataTool.getIntConvert("ignoreDAMr", level, 0);
            item.RecoveryUP = (byte)MapleDataTool.getIntConvert("RecoveryUP", level, 0);
            item.incSTRlv = (byte)MapleDataTool.getIntConvert("incSTRlv", level, 0);
            item.incDEXlv = (byte)MapleDataTool.getIntConvert("incDEXlv", level, 0);
            item.incINTlv = (byte)MapleDataTool.getIntConvert("incINTlv", level, 0);
            item.incLUKlv = (byte)MapleDataTool.getIntConvert("incLUKlv", level, 0);
            item.incEXPr = (byte)MapleDataTool.getIntConvert("incEXPr", level, 0);
            item.incPADlv = (byte)MapleDataTool.getIntConvert("incPADlv", level, 0);
            item.incMADlv = (byte)MapleDataTool.getIntConvert("incMADlv", level, 0);
            switch (item.potentialID) {
               case 31001:
               case 31002:
               case 31003:
               case 31004:
                  item.skillID = item.potentialID - 23001;
                  break;
               case 41005:
               case 41006:
               case 41007:
                  item.skillID = item.potentialID - 33001;
                  break;
               default:
                  item.skillID = 0;
            }

            items.add(item);
         }

         this.potentialCache.put(Integer.parseInt(data.getName()), items);
      }
   }

   public final Collection<Integer> getMonsterBookList() {
      return this.mobIds.values();
   }

   public final Map<Integer, Integer> getMonsterBook() {
      return this.mobIds;
   }

   public final Pair<Integer, Integer> getPot(int f) {
      return this.potLife.get(f);
   }

   public static final MapleItemInformationProvider getInstance() {
      return instance;
   }

   public int getScriptedItemNpc(int itemId) {
      if (this.scriptedItemCache.containsKey(itemId)) {
         return this.scriptedItemCache.get(itemId);
      } else {
         int npcId = MapleDataTool.getInt("spec/npc", this.getItemData(itemId), 0);
         this.scriptedItemCache.put(itemId, npcId);
         return this.scriptedItemCache.get(itemId);
      }
   }

   public String getScriptedItemScript(int itemId) {
      if (this.scriptedItemScriptCache.containsKey(itemId)) {
         return this.scriptedItemScriptCache.get(itemId);
      } else {
         String script = MapleDataTool.getString("spec/script", this.getItemData(itemId));
         this.scriptedItemScriptCache.put(itemId, script);
         return this.scriptedItemScriptCache.get(itemId);
      }
   }

   public MapleWeaponType getWeaponType(int itemId) {
      int cat = itemId / 10000 % 100;
      MapleWeaponType[] type = new MapleWeaponType[]{
         MapleWeaponType.SWORD1H,
         MapleWeaponType.AXE1H,
         MapleWeaponType.BLUNT1H,
         MapleWeaponType.DAGGER,
         MapleWeaponType.NOT_A_WEAPON,
         MapleWeaponType.NOT_A_WEAPON,
         MapleWeaponType.NOT_A_WEAPON,
         MapleWeaponType.WAND,
         MapleWeaponType.STAFF,
         MapleWeaponType.NOT_A_WEAPON,
         MapleWeaponType.SWORD2H,
         MapleWeaponType.AXE2H,
         MapleWeaponType.BLUNT2H,
         MapleWeaponType.SPEAR,
         MapleWeaponType.POLE_ARM,
         MapleWeaponType.BOW,
         MapleWeaponType.CROSSBOW,
         MapleWeaponType.CLAW,
         MapleWeaponType.KNUCKLE,
         MapleWeaponType.GUN
      };
      if (cat >= 30 && cat <= 49) {
         return type[cat - 30];
      } else {
         return cat == 26 ? MapleWeaponType.LIMITER : MapleWeaponType.NOT_A_WEAPON;
      }
   }

   public final List<Pair<Integer, String>> getAllEquips() {
      if (!this.equipCache.isEmpty()) {
         return this.equipCache;
      } else {
         List<Pair<Integer, String>> itemPairs = new ArrayList<>();
         MapleData itemsData = this.stringData.getData("Eqp.img").getChildByPath("Eqp");

         for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
               Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
               itemPairs.add(p);
               this.equipCache.add(p);
            }
         }

         return itemPairs;
      }
   }

   public final List<Pair<Integer, String>> getAllInstall() {
      if (!this.installCache.isEmpty()) {
         return this.installCache;
      } else {
         List<Pair<Integer, String>> itemPairs = new ArrayList<>();
         MapleData itemsData = this.stringData.getData("Ins.img");

         for (MapleData eqpType : itemsData.getChildren()) {
            Pair<Integer, String> p = new Pair<>(Integer.parseInt(eqpType.getName()), MapleDataTool.getString("name", eqpType, "NO-NAME"));
            itemPairs.add(p);
            this.installCache.add(p);
         }

         return itemPairs;
      }
   }

   public final List<Pair<Integer, String>> getAllItems() {
      if (!this.itemNameCache.isEmpty()) {
         return this.itemNameCache;
      } else {
         List<Pair<Integer, String>> itemPairs = new ArrayList<>();
         MapleData itemsData = this.stringData.getData("Cash.img");

         for (MapleData itemFolder : itemsData.getChildren()) {
            Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
            itemPairs.add(p);
            this.itemNameCache.add(p);
         }

         itemsData = this.stringData.getData("Consume.img");

         for (MapleData itemFolder : itemsData.getChildren()) {
            Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
            itemPairs.add(p);
            this.itemNameCache.add(p);
         }

         itemsData = this.stringData.getData("Eqp.img").getChildByPath("Eqp");

         for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
               Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
               itemPairs.add(p);
               this.itemNameCache.add(p);
            }
         }

         itemsData = this.stringData.getData("Etc.img").getChildByPath("Etc");

         for (MapleData itemFolder : itemsData.getChildren()) {
            Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
            itemPairs.add(p);
            this.itemNameCache.add(p);
         }

         itemsData = this.stringData.getData("Ins.img");

         for (MapleData itemFolder : itemsData.getChildren()) {
            Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
            itemPairs.add(p);
            this.itemNameCache.add(p);
         }

         itemsData = this.stringData.getData("Pet.img");

         for (MapleData itemFolder : itemsData.getChildren()) {
            Pair<Integer, String> p = new Pair<>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME"));
            itemPairs.add(p);
            this.itemNameCache.add(p);
         }

         return itemPairs;
      }
   }

   public final Pair<List<Integer>, List<Integer>> getAndroidInfo(int i) {
      return this.androids.get(i);
   }

   public final Triple<Integer, List<Integer>, List<Integer>> getMonsterBookInfo(int i) {
      return this.monsterBookSets.get(i);
   }

   public final Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> getAllMonsterBookInfo() {
      return this.monsterBookSets;
   }

   protected final MapleData getItemData(int itemId) {
      MapleData ret = null;
      String idStr = StringUtil.getLeftPaddedStr(String.valueOf(itemId), '0', 8);

      for (MapleDataDirectoryEntry root : this.itemData.getRoot()) {
         for (MapleDataFileEntry iFile : root.getFiles()) {
            if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
               ret = this.itemData.getData(iFile.getName());
               if (ret == null) {
                  return null;
               }

               return ret.getChildByPath(idStr);
            }

            if (iFile.getName().equals(idStr.substring(1) + ".img")) {
               return this.itemData.getData(iFile.getName());
            }
         }
      }

      for (MapleDataDirectoryEntry root : this.chrData.getRoot()) {
         for (MapleDataFileEntry iFile : root.getFiles()) {
            if (iFile.getName().equals(idStr + ".img")) {
               return this.chrData.getData(iFile.getName());
            }
         }
      }

      return ret;
   }

   public Integer getItemIdByMob(int mobId) {
      return this.mobIds.get(mobId);
   }

   public Integer getSetId(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.cardSet;
   }

   public final short getSlotMax(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      if (i == null) {
         return 0;
      } else {
         if (DBConfig.isGanglim) {
            switch (itemId) {
               case 40:
               case 2000019:
               case 2003550:
               case 2003551:
               case 2003575:
               case 2023072:
               case 2023658:
               case 2023659:
               case 2023660:
               case 2023661:
               case 2023662:
               case 2023663:
               case 2023664:
               case 2023665:
               case 2023666:
               case 2046251:
               case 2046831:
               case 2046832:
               case 2046970:
               case 2046981:
               case 2047810:
               case 2048226:
               case 2048716:
               case 2048717:
               case 2048724:
               case 2048745:
               case 2048753:
               case 2049153:
               case 2049370:
               case 2049372:
               case 2049704:
               case 2430016:
               case 2430026:
               case 2430027:
               case 2430658:
               case 2431341:
               case 2431940:
               case 2432423:
               case 2433593:
               case 2433979:
               case 2434851:
               case 2435369:
               case 2435719:
               case 2435748:
               case 2435885:
               case 2436039:
               case 2437121:
               case 2437157:
               case 2437158:
               case 2437760:
               case 2438145:
               case 2438396:
               case 2439567:
               case 2439568:
               case 2439653:
               case 2450042:
               case 2450054:
               case 2450064:
               case 2450134:
               case 2450147:
               case 2450148:
               case 2450149:
               case 2591419:
               case 2591420:
               case 2591421:
               case 2591422:
               case 2591423:
               case 2591424:
               case 2591425:
               case 2591426:
               case 2591427:
               case 2591460:
               case 2591461:
               case 2591462:
               case 2591463:
               case 2591464:
               case 2591465:
               case 2591466:
               case 2591467:
               case 2591564:
               case 2591565:
               case 2591566:
               case 2591567:
               case 2591568:
               case 2591569:
               case 2591570:
               case 2591571:
               case 2591572:
               case 2591573:
               case 2591574:
               case 2591575:
               case 2591576:
               case 2591577:
               case 2591578:
               case 2591579:
               case 2591580:
               case 2591581:
               case 2591582:
               case 2591583:
               case 2591584:
               case 2591585:
               case 2591586:
               case 2591587:
               case 2591588:
               case 2591589:
               case 2591590:
               case 2591591:
               case 2591592:
               case 2591593:
               case 2591594:
               case 2591595:
               case 2591596:
               case 2591597:
               case 2591598:
               case 2591632:
               case 2591633:
               case 2591634:
               case 2591635:
               case 2591636:
               case 2591637:
               case 2591638:
               case 2591639:
               case 2591640:
               case 2591641:
               case 2591642:
               case 2591643:
               case 2591644:
               case 2591645:
               case 2591646:
               case 2591647:
               case 2591648:
               case 2591651:
               case 2591652:
               case 2591653:
               case 2591654:
               case 2591655:
               case 2591656:
               case 2591657:
               case 2591658:
               case 2591659:
               case 2591660:
               case 2591661:
               case 2591662:
               case 2591663:
               case 2591664:
               case 2591665:
               case 2591666:
               case 2591667:
               case 2591668:
               case 2591669:
               case 2591670:
               case 2591671:
               case 2591672:
               case 2591673:
               case 2591674:
               case 2591675:
               case 2591676:
               case 2591677:
               case 2591678:
               case 2591679:
               case 2591680:
               case 2591681:
               case 2591682:
               case 2591683:
               case 2591684:
               case 2630127:
               case 2630133:
               case 2630281:
               case 2630291:
               case 2630437:
               case 2630442:
               case 2630755:
               case 2630782:
               case 2633336:
               case 2633616:
               case 2711003:
               case 2711004:
               case 2711005:
               case 2711012:
               case 3993000:
               case 3993002:
               case 3993003:
               case 3996007:
               case 4000006:
               case 4000094:
               case 4000101:
               case 4000178:
               case 4000190:
               case 4000220:
               case 4000439:
               case 4000620:
               case 4000896:
               case 4000965:
               case 4000979:
               case 4001168:
               case 4001209:
               case 4001715:
               case 4001786:
               case 4001832:
               case 4001842:
               case 4001843:
               case 4001868:
               case 4001869:
               case 4001878:
               case 4003002:
               case 4009005:
               case 4009155:
               case 4009239:
               case 4010000:
               case 4010001:
               case 4021016:
               case 4021031:
               case 4021037:
               case 4031213:
               case 4031227:
               case 4031306:
               case 4031307:
               case 4031311:
               case 4031457:
               case 4031466:
               case 4031569:
               case 4031788:
               case 4031831:
               case 4031838:
               case 4033114:
               case 4033151:
               case 4033172:
               case 4033338:
               case 4033884:
               case 4033885:
               case 4033891:
               case 4033892:
               case 4034181:
               case 4034271:
               case 4034803:
               case 4034809:
               case 4034922:
               case 4036068:
               case 4036444:
               case 4036531:
               case 4036573:
               case 4162009:
               case 4260003:
               case 4260004:
               case 4260005:
               case 4310001:
               case 4310027:
               case 4310029:
               case 4310034:
               case 4310036:
               case 4310038:
               case 4310048:
               case 4310057:
               case 4310059:
               case 4310061:
               case 4310063:
               case 4310065:
               case 4310085:
               case 4310086:
               case 4310113:
               case 4310129:
               case 4310153:
               case 4310198:
               case 4310229:
               case 4310237:
               case 4310261:
               case 4310266:
               case 4310291:
               case 5060048:
               case 5062005:
               case 5062009:
               case 5062010:
               case 5062500:
               case 5062503:
               case 5068300:
               case 5068301:
               case 5068302:
               case 5068303:
               case 5068304:
               case 5133000:
               case 5220000:
               case 5220010:
               case 5220013:
               case 5220020:
               case 5520001:
                  return 30000;
               case 4032036:
                  return 3000;
            }
         } else {
            switch (itemId) {
               case 2023207:
               case 2023381:
               case 2023658:
               case 2023659:
               case 2023660:
               case 2023661:
               case 2023662:
               case 2023663:
               case 2023664:
               case 2023665:
               case 2023666:
               case 2439292:
               case 2450039:
               case 2450041:
               case 2450042:
               case 2450043:
               case 2450054:
               case 2450062:
               case 2450064:
               case 2450067:
               case 2450075:
               case 2450085:
               case 2450086:
               case 2450087:
               case 2450088:
               case 2450090:
               case 2450091:
               case 2450092:
               case 2450093:
               case 2450095:
               case 2450110:
               case 2450115:
               case 2450116:
               case 2450122:
               case 2450123:
               case 2450124:
               case 2450130:
               case 2450134:
               case 2450135:
               case 2450141:
               case 2450144:
               case 2450147:
               case 2450148:
               case 2450149:
               case 2450153:
               case 2450155:
               case 2450159:
               case 2450162:
               case 2450163:
               case 2450164:
               case 2450166:
               case 2450167:
               case 2450175:
               case 2450179:
               case 2450181:
                  return 100;
            }
         }

         if (itemId != 2432126 && itemId != 2432127) {
            if ((itemId < 2432122 || itemId > 2432159) && (itemId < 2432160 || itemId > 2432164)) {
               if (itemId == 2430217) {
                  return 5000;
               } else if (itemId == 2450155) {
                  return 100;
               } else if (itemId == 2711003 || itemId == 2711004) {
                  return 100;
               } else if (itemId == 2711006) {
                  return 200;
               } else if (itemId == 2439239 || itemId == 2631614 || itemId == 2450134) {
                  return 100;
               } else if (itemId == 5520001) {
                  return 1;
               } else if (itemId == 5121060) {
                  return 10;
               } else if (itemId == 2048759 || itemId == 2048758 || itemId == 2633349 || itemId == 2048766) {
                  return 999;
               } else if (itemId == 5062010 || itemId == 5062500 || itemId == 5062503) {
                  return 999;
               } else {
                  return itemId == 4036454 ? 100 : i.slotMax;
               }
            } else {
               return 5000;
            }
         } else {
            return 1000;
         }
      }
   }

   public final int getUpgradeScrollUseSlot(int itemid) {
      if (this.scrollUpgradeSlotUse.containsKey(itemid)) {
         return this.scrollUpgradeSlotUse.get(itemid);
      } else {
         int useslot = MapleDataTool.getIntConvert("info/tuc", this.getItemData(itemid), 1);
         this.scrollUpgradeSlotUse.put(itemid, useslot);
         return this.scrollUpgradeSlotUse.get(itemid);
      }
   }

   public final boolean isBossReward(int itemID) {
      ItemInformation i = this.getItemInformation(itemID);
      return i == null ? false : i.bossReward;
   }

   public final boolean isPitchedBossset(int itemID) {
      switch (itemID) {
         case 1012632:
         case 1022278:
         case 1032316:
         case 1113306:
         case 1122430:
         case 1132308:
            return true;
         default:
            return false;
      }
   }

   public final int getNickSkill(int itemID) {
      ItemInformation i = this.getItemInformation(itemID);
      return i == null ? 0 : i.nickSkill;
   }

   public final int getNickSkillTimeLimited(int itemID) {
      ItemInformation i = this.getItemInformation(itemID);
      return i == null ? 0 : i.nickSkillTimeLimited;
   }

   public final int getWholePrice(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? 0 : i.wholePrice;
   }

   public final double getPrice(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? -1.0 : i.price;
   }

   protected int rand(int min, int max) {
      return Math.abs(Randomizer.rand(min, max));
   }

   public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
      Equip nEquip = (Equip)equip.copy();

      try {
         for (Entry<String, Integer> stat : sta.entrySet()) {
            if (stat.getKey().equals("STRMin")) {
               nEquip.setStr((short)(nEquip.getStr() + this.rand(stat.getValue(), sta.get("STRMax"))));
            } else if (stat.getKey().equals("DEXMin")) {
               nEquip.setDex((short)(nEquip.getDex() + this.rand(stat.getValue(), sta.get("DEXMax"))));
            } else if (stat.getKey().equals("INTMin")) {
               nEquip.setInt((short)(nEquip.getInt() + this.rand(stat.getValue(), sta.get("INTMax"))));
            } else if (stat.getKey().equals("LUKMin")) {
               nEquip.setLuk((short)(nEquip.getLuk() + this.rand(stat.getValue(), sta.get("LUKMax"))));
            } else if (stat.getKey().equals("PADMin")) {
               nEquip.setWatk((short)(nEquip.getWatk() + this.rand(stat.getValue(), sta.get("PADMax"))));
            } else if (stat.getKey().equals("PDDMin")) {
               nEquip.setWdef((short)(nEquip.getWdef() + this.rand(stat.getValue(), sta.get("PDDMax"))));
            } else if (stat.getKey().equals("MADMin")) {
               nEquip.setMatk((short)(nEquip.getMatk() + this.rand(stat.getValue(), sta.get("MADMax"))));
            } else if (stat.getKey().equals("MDDMin")) {
               nEquip.setMdef((short)(nEquip.getMdef() + this.rand(stat.getValue(), sta.get("MDDMax"))));
            } else if (stat.getKey().equals("ACCMin")) {
               nEquip.setAcc((short)(nEquip.getAcc() + this.rand(stat.getValue(), sta.get("ACCMax"))));
            } else if (stat.getKey().equals("EVAMin")) {
               nEquip.setAvoid((short)(nEquip.getAvoid() + this.rand(stat.getValue(), sta.get("EVAMax"))));
            } else if (stat.getKey().equals("SpeedMin")) {
               nEquip.setSpeed((short)(nEquip.getSpeed() + this.rand(stat.getValue(), sta.get("SpeedMax"))));
            } else if (stat.getKey().equals("JumpMin")) {
               nEquip.setJump((short)(nEquip.getJump() + this.rand(stat.getValue(), sta.get("JumpMax"))));
            } else if (stat.getKey().equals("MHPMin")) {
               nEquip.setHp((short)(nEquip.getHp() + this.rand(stat.getValue(), sta.get("MHPMax"))));
            } else if (stat.getKey().equals("MMPMin")) {
               nEquip.setMp((short)(nEquip.getMp() + this.rand(stat.getValue(), sta.get("MMPMax"))));
            } else if (stat.getKey().equals("MaxHPMin")) {
               nEquip.setHp((short)(nEquip.getHp() + this.rand(stat.getValue(), sta.get("MaxHPMax"))));
            } else if (stat.getKey().equals("MaxMPMin")) {
               nEquip.setMp((short)(nEquip.getMp() + this.rand(stat.getValue(), sta.get("MaxMPMax"))));
            }
         }
      } catch (NullPointerException var6) {
         var6.printStackTrace();
      }

      return nEquip;
   }

   public final List<Triple<String, String, String>> getEquipAdditions(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.equipAdditions;
   }

   public final String getEquipAddReqs(int itemId, String key, String sub) {
      ItemInformation i = this.getItemInformation(itemId);
      if (i == null) {
         return null;
      } else {
         for (Triple<String, String, String> data : i.equipAdditions) {
            if (data.getLeft().equals("key") && data.getMid().equals("con:" + sub)) {
               return data.getRight();
            }
         }

         return null;
      }
   }

   public final Map<Integer, Map<String, Integer>> getEquipIncrements(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.equipIncs;
   }

   public final List<Integer> getEquipSkills(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.incSkill;
   }

   public final boolean canEquip(
      Map<String, Integer> stats, int itemid, int level, int job, int fame, long str, long dex, long luk, long int_, int supremacy, int downLevel
   ) {
      if (level + supremacy + downLevel >= (stats.containsKey("reqLevel") ? stats.get("reqLevel") : 0)
         && str >= (stats.containsKey("reqSTR") ? stats.get("reqSTR") : 0)
         && dex >= (stats.containsKey("reqDEX") ? stats.get("reqDEX") : 0)
         && luk >= (stats.containsKey("reqLUK") ? stats.get("reqLUK") : 0)
         && int_ >= (stats.containsKey("reqINT") ? stats.get("reqINT") : 0)) {
         Integer fameReq = stats.get("reqPOP");
         return fameReq == null || fame >= fameReq;
      } else {
         return false;
      }
   }

   public final Map<String, Integer> getEquipStats(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.equipStats;
   }

   public final int getReqLevel(int itemId) {
      try {
         return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("reqLevel") ? this.getEquipStats(itemId).get("reqLevel") : 0;
      } catch (Exception var3) {
         return 0;
      }
   }

   public final int getReqJob(int itemId) {
      try {
         return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("reqJob") ? this.getEquipStats(itemId).get("reqJob") : 0;
      } catch (Exception var3) {
         return 0;
      }
   }

   public final int getPad(int itemId) {
      try {
         return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("PAD") ? this.getEquipStats(itemId).get("PAD") : 0;
      } catch (Exception var3) {
         return 0;
      }
   }

   public final int getMad(int itemId) {
      try {
         return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("MAD") ? this.getEquipStats(itemId).get("MAD") : 0;
      } catch (Exception var3) {
         return 0;
      }
   }

   public final int getIncSTR(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incSTR") ? this.getEquipStats(itemId).get("incSTR") : 0;
   }

   public final int getIncDEX(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incDEX") ? this.getEquipStats(itemId).get("incDEX") : 0;
   }

   public final int getIncINT(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incINT") ? this.getEquipStats(itemId).get("incINT") : 0;
   }

   public final int getIncLUK(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incLUK") ? this.getEquipStats(itemId).get("incLUK") : 0;
   }

   public final int getIncMHP(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incMHP") ? this.getEquipStats(itemId).get("incMHP") : 0;
   }

   public final int getIncMMP(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incMMP") ? this.getEquipStats(itemId).get("incMMP") : 0;
   }

   public final int getIncMHPr(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incMHPr") ? this.getEquipStats(itemId).get("incMHPr") : 0;
   }

   public final int getIncMMPr(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incMMPr") ? this.getEquipStats(itemId).get("incMMPr") : 0;
   }

   public final int getIncPAD(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incPAD") ? this.getEquipStats(itemId).get("incPAD") : 0;
   }

   public final int getIncMAD(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incMAD") ? this.getEquipStats(itemId).get("incMAD") : 0;
   }

   public final int getIncPDD(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incPDD") ? this.getEquipStats(itemId).get("incPDD") : 0;
   }

   public final int getIncMDD(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incMDD") ? this.getEquipStats(itemId).get("incMDD") : 0;
   }

   public final int getIncACC(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incACC") ? this.getEquipStats(itemId).get("incACC") : 0;
   }

   public final int getIncEVA(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incEVA") ? this.getEquipStats(itemId).get("incEVA") : 0;
   }

   public final int getIncCraft(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incCraft") ? this.getEquipStats(itemId).get("incCraft") : 0;
   }

   public final int getincSpeed(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incSpeed") ? this.getEquipStats(itemId).get("incSpeed") : 0;
   }

   public final int getIncJump(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("incJump") ? this.getEquipStats(itemId).get("incJump") : 0;
   }

   public final int getBdR(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("bdR") ? this.getEquipStats(itemId).get("bdR") : 0;
   }

   public final int getIMdR(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("imdR") ? this.getEquipStats(itemId).get("imdR") : 0;
   }

   public final int getSlots(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("tuc") ? this.getEquipStats(itemId).get("tuc") : 0;
   }

   public final Integer getSetItemID(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("setItemID") ? this.getEquipStats(itemId).get("setItemID") : 0;
   }

   public final Integer getTamingMob(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("tamingMob") ? this.getEquipStats(itemId).get("setItemID") : 0;
   }

   public final Integer getMaxExceptionalSlots(int itemId) {
      return this.getEquipStats(itemId) != null && this.getEquipStats(itemId).containsKey("Etuc") ? this.getEquipStats(itemId).get("Etuc") : 0;
   }

   public final StructSetItem getSetItem(int setItemId) {
      return this.setItems.get(setItemId);
   }

   public final int getCursed(int itemId, MapleCharacter player) {
      return this.getCursed(itemId, player, null);
   }

   public final int getCursed(int itemId, MapleCharacter player, Item equip) {
      if (this.cursedCache.containsKey(itemId)) {
         return this.cursedCache.get(itemId);
      } else {
         MapleData item = this.getItemData(itemId);
         if (item == null) {
            return -1;
         } else {
            int success = 0;
            success = MapleDataTool.getIntConvert("info/cursed", item, -1);
            this.cursedCache.put(itemId, success);
            return success;
         }
      }
   }

   public final MapleData getTamingMob_(int itemID) {
      for (MapleDataDirectoryEntry root_ : this.chrData.getRoot()) {
         for (MapleDataFileEntry iFile : root_.getFiles()) {
            MapleData root = this.itemData.getData(iFile.getName());
            if (root != null) {
               for (MapleData d : root.getChildren()) {
                  if (d.getName().equals("0" + itemID)) {
                     MapleData info = d.getChildByPath("info");
                     if (info != null) {
                        MapleData dd = info.getChildByPath("customVehicle");
                        if (dd != null) {
                           return dd;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public final MapleData getCustomChair(int itemID) {
      for (MapleDataDirectoryEntry root_ : this.itemData.getRoot()) {
         if (root_.getName().startsWith("Install")) {
            for (MapleDataFileEntry iFile : root_.getFiles()) {
               MapleData root = this.itemData.getData(iFile.getName());
               if (root != null) {
                  for (MapleData d : root.getChildren()) {
                     if (d.getName().equals("0" + itemID)) {
                        MapleData info = d.getChildByPath("info");
                        if (info != null) {
                           MapleData dd = info.getChildByPath("customChair");
                           if (dd != null) {
                              return dd;
                           }

                           dd = info.getChildByPath("lvChairInfo");
                           if (dd != null) {
                              return dd;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public final int getChairTamingMob(int itemID) {
      if (this.chairTamingMobCache.get(itemID) != null) {
         return this.chairTamingMobCache.get(itemID);
      } else {
         for (MapleDataDirectoryEntry root_ : this.itemData.getRoot()) {
            if (root_.getName().startsWith("Install")) {
               for (MapleDataFileEntry iFile : root_.getFiles()) {
                  MapleData root = this.itemData.getData(iFile.getName());
                  if (root != null) {
                     for (MapleData d : root.getChildren()) {
                        if (d.getName().equals("0" + itemID)) {
                           MapleData info = d.getChildByPath("info");
                           if (info != null) {
                              int tamingMob = MapleDataTool.getInt(info.getChildByPath("tamingMob"), 0);
                              this.chairTamingMobCache.put(itemID, tamingMob);
                              return tamingMob;
                           }
                        }
                     }
                  }
               }
            }
         }

         return 0;
      }
   }

   public final boolean isChairTaimingMob(int skillID) {
      boolean check = false;

      try {
         check = this.chairTamingMobCache.containsValue(skillID);
      } catch (Exception var4) {
      }

      return check;
   }

   public final String getTamingMobType(Integer itemID) {
      if (itemID == 0) {
         return "";
      } else if (this.tamingMobTypeCache.containsKey(itemID)) {
         return this.tamingMobTypeCache.get(itemID);
      } else {
         MapleData dd = this.getTamingMob_(itemID);
         if (dd != null) {
            String t = MapleDataTool.getString("type", dd, "");
            this.tamingMobTypeCache.put(itemID, t);
            return t;
         } else {
            this.tamingMobTypeCache.put(itemID, "");
            return "";
         }
      }
   }

   public final String getChairType(Integer itemID) {
      if (itemID == 0) {
         return "";
      } else if (this.chairTypeCache.containsKey(itemID)) {
         return this.chairTypeCache.get(itemID);
      } else {
         MapleData dd = this.getCustomChair(itemID);
         if (dd != null) {
            String t;
            if (dd.getName().equals("lvChairInfo")) {
               t = MapleDataTool.getString("chairType", dd, "");
            } else {
               t = MapleDataTool.getString("type", dd, "");
            }

            this.chairTypeCache.put(itemID, t);
            return t;
         } else {
            this.chairTypeCache.put(itemID, "");
            return "";
         }
      }
   }

   public final Point getPetChairPos(int itemID, int index) {
      MapleData dd = this.getCustomChair(itemID);
      if (dd != null) {
         MapleData info = dd.getChildByPath("petChairInfo");
         if (info != null) {
            MapleData d = info.getChildByPath(String.valueOf(index));
            if (d != null) {
               return MapleDataTool.getPoint("pos", d, new Point(0, 0));
            }
         }
      }

      return new Point(0, 0);
   }

   public final List<Integer> getScrollReqs(int itemId) {
      List<Integer> ret = new ArrayList<>();
      MapleData data = this.getItemData(itemId).getChildByPath("req");
      if (data == null) {
         return ret;
      } else {
         for (MapleData req : data.getChildren()) {
            ret.add(MapleDataTool.getInt(req));
         }

         return ret;
      }
   }

   public final Equip scrollEquipWithId(Item equip, Item scrollId, boolean ws, MapleCharacter chr) {
      Equip nEquip = (Equip)equip;
      Equip nZeroEquip = null;
      if (equip.getType() == 1) {
         if (GameConstants.isZeroWeapon(equip.getItemId())) {
            nZeroEquip = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
         }

         int scrid = scrollId.getItemId();
         if (scrid == 2643131) {
            scrid = 2643133;
         }

         Map<String, Integer> stats = this.getEquipStats(scrid);
         Map<String, Integer> eqstats = this.getEquipStats(equip.getItemId());
         MapleItemInformationProvider ii = getInstance();
         boolean failed = false;
         if (DBConfig.isGanglim && GameConstants.isRoyalSpecialScroll(scrollId.getItemId())) {
            switch (scrollId.getItemId()) {
               case 2046076:
                  nEquip.setStr((short)(nEquip.getStr() + 13));
                  nEquip.setInt((short)(nEquip.getInt() + 13));
                  nEquip.setDex((short)(nEquip.getDex() + 13));
                  nEquip.setLuk((short)(nEquip.getLuk() + 13));
                  nEquip.setWatk((short)(nEquip.getWatk() + 16));
                  break;
               case 2046077:
                  nEquip.setStr((short)(nEquip.getStr() + 13));
                  nEquip.setInt((short)(nEquip.getInt() + 13));
                  nEquip.setDex((short)(nEquip.getDex() + 13));
                  nEquip.setLuk((short)(nEquip.getLuk() + 13));
                  nEquip.setMatk((short)(nEquip.getMatk() + 16));
                  break;
               case 2046150:
                  nEquip.setStr((short)(nEquip.getStr() + 13));
                  nEquip.setInt((short)(nEquip.getInt() + 13));
                  nEquip.setDex((short)(nEquip.getDex() + 13));
                  nEquip.setLuk((short)(nEquip.getLuk() + 13));
                  nEquip.setWatk((short)(nEquip.getWatk() + 16));
                  break;
               case 2046251:
                  nEquip.setStr((short)(nEquip.getStr() + 3));
                  nEquip.setInt((short)(nEquip.getInt() + 3));
                  nEquip.setDex((short)(nEquip.getDex() + 3));
                  nEquip.setLuk((short)(nEquip.getLuk() + 3));
                  nEquip.setMatk((short)(nEquip.getMatk() + 9));
                  nEquip.setWatk((short)(nEquip.getWatk() + 9));
                  break;
               case 2046340:
                  nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.rand(9, 10)));
                  break;
               case 2046341:
                  nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.rand(9, 10)));
                  break;
               case 2046831:
                  nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.rand(6, 7)));
                  break;
               case 2046832:
                  nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.rand(6, 7)));
                  break;
               case 2046970:
                  nEquip.setStr((short)(nEquip.getStr() + 7));
                  nEquip.setInt((short)(nEquip.getInt() + 7));
                  nEquip.setDex((short)(nEquip.getDex() + 7));
                  nEquip.setLuk((short)(nEquip.getLuk() + 7));
                  nEquip.setMatk((short)(nEquip.getMatk() + 13));
                  break;
               case 2046981:
                  nEquip.setStr((short)(nEquip.getStr() + 7));
                  nEquip.setInt((short)(nEquip.getInt() + 7));
                  nEquip.setDex((short)(nEquip.getDex() + 7));
                  nEquip.setLuk((short)(nEquip.getLuk() + 7));
                  nEquip.setWatk((short)(nEquip.getWatk() + 13));
                  break;
               case 2047810:
                  nEquip.setStr((short)(nEquip.getStr() + 7));
                  nEquip.setInt((short)(nEquip.getInt() + 7));
                  nEquip.setDex((short)(nEquip.getDex() + 7));
                  nEquip.setLuk((short)(nEquip.getLuk() + 7));
                  nEquip.setWatk((short)(nEquip.getWatk() + 13));
                  break;
               case 2048047:
                  nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.rand(7, 8)));
                  break;
               case 2048048:
                  nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.rand(7, 8)));
                  break;
               case 2048049:
                  nEquip.setWatk((short)(nEquip.getWatk() + Randomizer.rand(4, 5)));
                  break;
               case 2048050:
                  nEquip.setMatk((short)(nEquip.getMatk() + Randomizer.rand(4, 5)));
            }
         } else {
            label1048: {
               switch (scrollId.getItemId()) {
                  case 2040727:
                     int flagxxxxxx = nEquip.getFlag();
                     flagxxxxxx |= ItemFlag.PREVENT_SLIP.getValue();
                     nEquip.setFlag(flagxxxxxx);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flagxxxxxx);
                     }
                     break label1048;
                  case 2041058:
                     int flagxxxxx = nEquip.getFlag();
                     flagxxxxx |= ItemFlag.BINDED.getValue();
                     nEquip.setFlag(flagxxxxx);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flagxxxxx);
                     }
                     break label1048;
                  case 2046025:
                  case 2046026:
                  case 2046054:
                  case 2046055:
                  case 2046056:
                  case 2046057:
                  case 2046058:
                  case 2046059:
                  case 2046094:
                  case 2046095:
                  case 2046119:
                  case 2046120:
                  case 2046138:
                  case 2046139:
                  case 2046140:
                  case 2046162:
                  case 2046163:
                  case 2046251:
                  case 2046340:
                  case 2046341:
                  case 2046374:
                  case 2046564:
                  case 2046856:
                  case 2046857:
                  case 2046991:
                  case 2046992:
                  case 2047405:
                  case 2047406:
                  case 2047407:
                  case 2047408:
                  case 2047814:
                  case 2048094:
                  case 2048095:
                  case 2048804:
                  case 2048805:
                  case 2048836:
                  case 2048837:
                  case 2048838:
                  case 2048839:
                     if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                        break label1048;
                     }

                     switch (scrollId.getItemId()) {
                        case 2046025:
                           int atkxxxxxxx = Randomizer.rand(7, 8);
                           nEquip.setWatk((short)(nEquip.getWatk() + atkxxxxxxx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + atkxxxxxxx));
                           }
                           break label1048;
                        case 2046026:
                           int atkxxxxxx = Randomizer.rand(7, 8);
                           nEquip.setMatk((short)(nEquip.getMatk() + atkxxxxxx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + atkxxxxxx));
                           }
                           break label1048;
                        case 2046054:
                        case 2046055:
                        case 2046056:
                        case 2046057:
                        case 2046138:
                        case 2046139:
                           if (scrollId.getItemId() != 2046055 && scrollId.getItemId() != 2046057) {
                              nEquip.setWatk((short)(nEquip.getWatk() + 5));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 5));
                              }
                           } else {
                              nEquip.setMatk((short)(nEquip.getMatk() + 5));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + 5));
                              }
                           }

                           nEquip.setStr((short)(nEquip.getStr() + 3));
                           nEquip.setDex((short)(nEquip.getDex() + 3));
                           nEquip.setInt((short)(nEquip.getInt() + 3));
                           nEquip.setLuk((short)(nEquip.getLuk() + 3));
                           nEquip.setAcc((short)(nEquip.getAcc() + 15));
                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                              nZeroEquip.setAcc((short)(nZeroEquip.getAcc() + 15));
                           }
                           break label1048;
                        case 2046058:
                        case 2046059:
                        case 2046140:
                           if (scrollId.getItemId() == 2046059) {
                              nEquip.setMatk((short)(nEquip.getMatk() + 2));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + 2));
                              }
                           } else {
                              nEquip.setWatk((short)(nEquip.getWatk() + 2));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 2));
                              }
                           }

                           nEquip.setStr((short)(nEquip.getStr() + 1));
                           nEquip.setDex((short)(nEquip.getDex() + 1));
                           nEquip.setInt((short)(nEquip.getInt() + 1));
                           nEquip.setLuk((short)(nEquip.getLuk() + 1));
                           nEquip.setAcc((short)(nEquip.getAcc() + 5));
                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 1));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 1));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 1));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 1));
                              nZeroEquip.setAcc((short)(nZeroEquip.getAcc() + 5));
                           }
                           break label1048;
                        case 2046095:
                        case 2046163:
                           int atkxx = Randomizer.rand(7, 9);
                           nEquip.setMatk((short)(nEquip.getMatk() + atkxx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + atkxx));
                           }
                           break label1048;
                        case 2046119:
                           int atkxxxxx = Randomizer.rand(7, 8);
                           nEquip.setWatk((short)(nEquip.getWatk() + atkxxxxx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + atkxxxxx));
                           }
                           break label1048;
                        case 2046120:
                           int atkxxxx = Randomizer.rand(7, 8);
                           nEquip.setMatk((short)(nEquip.getMatk() + atkxxxx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + atkxxxx));
                           }
                           break label1048;
                        case 2046162:
                           int atkxxx = Randomizer.rand(7, 9);
                           nEquip.setWatk((short)(nEquip.getWatk() + atkxxx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + atkxxx));
                           }
                           break label1048;
                        case 2046251:
                           nEquip.setStr((short)(nEquip.getStr() + 3));
                           nEquip.setInt((short)(nEquip.getInt() + 3));
                           nEquip.setDex((short)(nEquip.getDex() + 3));
                           nEquip.setLuk((short)(nEquip.getLuk() + 3));
                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                           }
                           break label1048;
                        case 2046340:
                           nEquip.setWatk((short)(nEquip.getWatk() + 1));
                           if (nZeroEquip != null) {
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 1));
                           }
                           break label1048;
                        case 2046341:
                           nEquip.setMatk((short)(nEquip.getMatk() + 1));
                           if (nZeroEquip != null) {
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + 1));
                           }
                           break label1048;
                        case 2046374:
                           nEquip.setWatk((short)(nEquip.getWatk() + 15));
                           nEquip.setMatk((short)(nEquip.getMatk() + 15));
                           nEquip.setWdef((short)(nEquip.getWdef() + 80));
                           nEquip.setMdef((short)(nEquip.getMdef() + 80));
                           nEquip.setStr((short)(nEquip.getStr() + 25));
                           nEquip.setDex((short)(nEquip.getDex() + 25));
                           nEquip.setInt((short)(nEquip.getInt() + 25));
                           nEquip.setLuk((short)(nEquip.getLuk() + 25));
                           nEquip.setAvoid((short)(nEquip.getAvoid() + 30));
                           nEquip.setAcc((short)(nEquip.getAcc() + 30));
                           nEquip.setSpeed((short)(nEquip.getSpeed() + 3));
                           nEquip.setJump((short)(nEquip.getJump() + 2));
                           if (nZeroEquip != null) {
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 15));
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + 15));
                              nZeroEquip.setWdef((short)(nZeroEquip.getWdef() + 80));
                              nZeroEquip.setMdef((short)(nZeroEquip.getMdef() + 80));
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 25));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 25));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 25));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 25));
                              nZeroEquip.setAvoid((short)(nZeroEquip.getAvoid() + 30));
                              nZeroEquip.setAcc((short)(nZeroEquip.getAcc() + 30));
                              nZeroEquip.setSpeed((short)(nZeroEquip.getSpeed() + 3));
                              nZeroEquip.setJump((short)(nZeroEquip.getJump() + 2));
                           }
                           break label1048;
                        case 2046564:
                           nEquip.setStr((short)(nEquip.getStr() + 5));
                           nEquip.setInt((short)(nEquip.getInt() + 5));
                           nEquip.setDex((short)(nEquip.getDex() + 5));
                           nEquip.setLuk((short)(nEquip.getLuk() + 5));
                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 5));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 5));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 5));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 5));
                           }
                           break label1048;
                        case 2046856:
                        case 2047405:
                        case 2047407:
                        case 2048094:
                        case 2048804:
                        case 2048836:
                        case 2048838:
                           if (Randomizer.nextInt(100) < 15) {
                              nEquip.setWatk((short)(nEquip.getWatk() + 5));
                           } else {
                              nEquip.setWatk((short)(nEquip.getWatk() + 4));
                           }
                           break label1048;
                        case 2046857:
                        case 2047406:
                        case 2047408:
                        case 2048095:
                        case 2048805:
                        case 2048837:
                        case 2048839:
                           if (Randomizer.nextInt(100) < 15) {
                              nEquip.setMatk((short)(nEquip.getMatk() + 5));
                           } else {
                              nEquip.setMatk((short)(nEquip.getMatk() + 4));
                           }
                           break label1048;
                        case 2046991:
                        case 2047814:
                           nEquip.setStr((short)(nEquip.getStr() + 3));
                           nEquip.setInt((short)(nEquip.getInt() + 3));
                           nEquip.setDex((short)(nEquip.getDex() + 3));
                           nEquip.setLuk((short)(nEquip.getLuk() + 3));
                           int randx = Randomizer.rand(9, 11);
                           nEquip.setWatk((short)(nEquip.getWatk() + randx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + randx));
                           }
                           break label1048;
                        case 2046992:
                           nEquip.setStr((short)(nEquip.getStr() + 3));
                           nEquip.setInt((short)(nEquip.getInt() + 3));
                           nEquip.setDex((short)(nEquip.getDex() + 3));
                           nEquip.setLuk((short)(nEquip.getLuk() + 3));
                           int rand = Randomizer.rand(9, 11);
                           nEquip.setMatk((short)(nEquip.getMatk() + rand));
                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + rand));
                           }
                           break label1048;
                        case 5530336:
                        case 5530338:
                           int atkx = Randomizer.rand(2, 4);
                           nEquip.setWatk((short)(nEquip.getWatk() + atkx));
                           if (nZeroEquip != null) {
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + atkx));
                           }
                           break label1048;
                        case 5530337:
                        case 5530339:
                           int atk = Randomizer.rand(2, 4);
                           nEquip.setMatk((short)(nEquip.getMatk() + atk));
                           if (nZeroEquip != null) {
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + atk));
                           }
                        default:
                           break label1048;
                     }
                  case 2046841:
                  case 2046842:
                  case 2046967:
                  case 2046971:
                  case 2047803:
                  case 2047917:
                     if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                     } else {
                        switch (scrollId.getItemId()) {
                           case 2046841:
                              nEquip.setWatk((short)(nEquip.getWatk() + 1));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 1));
                              }
                              break label1048;
                           case 2046842:
                              nEquip.setMatk((short)(nEquip.getMatk() + 1));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + 1));
                              }
                              break label1048;
                           case 2046967:
                              nEquip.setWatk((short)(nEquip.getWatk() + 9));
                              nEquip.setStr((short)(nEquip.getStr() + 3));
                              nEquip.setInt((short)(nEquip.getInt() + 3));
                              nEquip.setDex((short)(nEquip.getDex() + 3));
                              nEquip.setLuk((short)(nEquip.getLuk() + 3));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 9));
                                 nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                                 nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                                 nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                                 nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                              }
                              break label1048;
                           case 2046971:
                              nEquip.setMatk((short)(nEquip.getMatk() + 9));
                              nEquip.setStr((short)(nEquip.getStr() + 3));
                              nEquip.setInt((short)(nEquip.getInt() + 3));
                              nEquip.setDex((short)(nEquip.getDex() + 3));
                              nEquip.setLuk((short)(nEquip.getLuk() + 3));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + 9));
                                 nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                                 nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                                 nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                                 nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                              }
                              break label1048;
                           case 2047803:
                              nEquip.setWatk((short)(nEquip.getWatk() + 9));
                              nEquip.setStr((short)(nEquip.getStr() + 3));
                              nEquip.setInt((short)(nEquip.getInt() + 3));
                              nEquip.setDex((short)(nEquip.getDex() + 3));
                              nEquip.setLuk((short)(nEquip.getLuk() + 3));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + 9));
                                 nZeroEquip.setStr((short)(nZeroEquip.getStr() + 3));
                                 nZeroEquip.setInt((short)(nZeroEquip.getInt() + 3));
                                 nZeroEquip.setDex((short)(nZeroEquip.getDex() + 3));
                                 nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 3));
                              }
                              break label1048;
                           case 2047917:
                              nEquip.setStr((short)(nEquip.getStr() + 9));
                              nEquip.setInt((short)(nEquip.getInt() + 9));
                              nEquip.setDex((short)(nEquip.getDex() + 9));
                              nEquip.setLuk((short)(nEquip.getLuk() + 9));
                              if (nZeroEquip != null) {
                                 nZeroEquip.setStr((short)(nZeroEquip.getStr() + 9));
                                 nZeroEquip.setInt((short)(nZeroEquip.getInt() + 9));
                                 nZeroEquip.setDex((short)(nZeroEquip.getDex() + 9));
                                 nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + 9));
                              }
                        }
                     }
                     break label1048;
                  case 2048306:
                     if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                     } else if (nEquip.getState() <= 32) {
                        nEquip.setState((byte)6);
                        byte linex = 2;
                        if (Randomizer.nextInt(100) < 30) {
                           linex = 3;
                        }

                        nEquip.setLines(linex);
                        if (nZeroEquip != null) {
                           nZeroEquip.setState((byte)6);
                           nZeroEquip.setLines(linex);
                        }
                     }
                     break label1048;
                  case 2049000:
                  case 2049001:
                  case 2049002:
                  case 2049004:
                  case 2049005:
                  case 2049009:
                  case 2049010:
                  case 2049011:
                  case 2049013:
                  case 2049014:
                  case 2049015:
                  case 2049016:
                  case 2049018:
                  case 2049019:
                  case 2049024:
                  case 2049025:
                  case 2049026:
                  case 2049027:
                  case 2049028:
                  case 2049029:
                  case 2049032:
                  case 2049033:
                  case 2049034:
                  case 2049035:
                  case 2049040:
                  case 2049047:
                     if (Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))
                        && nEquip.getLevel() + nEquip.getUpgradeSlots()
                           < ii.getEquipStats(nEquip.getItemId()).get("tuc") + (nEquip.getViciousHammer() > 0 ? 1 : 0)) {
                        nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + 1));
                        if (nZeroEquip != null) {
                           nZeroEquip.setUpgradeSlots((byte)(nZeroEquip.getUpgradeSlots() + 1));
                        }
                     }
                     break label1048;
                  case 2049006:
                  case 2049007:
                  case 2049008:
                     if (nEquip.getLevel() + nEquip.getUpgradeSlots()
                        < ii.getEquipStats(nEquip.getItemId()).get("tuc") + (nEquip.getViciousHammer() > 0 ? 1 : 0)) {
                        nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() + 2));
                        if (nZeroEquip != null) {
                           nZeroEquip.setUpgradeSlots((byte)(nZeroEquip.getUpgradeSlots() + 2));
                        }
                     }
                     break label1048;
                  case 2049600:
                  case 2049601:
                  case 2049602:
                  case 2049603:
                  case 2049604:
                  case 2049605:
                  case 2049606:
                  case 2049607:
                  case 2049610:
                  case 2049611:
                  case 2049612:
                  case 2049615:
                  case 2049616:
                  case 2049618:
                  case 5064200:
                     Equip origin = (Equip)getInstance().getEquipById(nEquip.getItemId());
                     nEquip.innocent(origin);
                     if (nZeroEquip != null) {
                        Equip origin2 = (Equip)getInstance().getEquipById(nZeroEquip.getItemId());
                        nZeroEquip.innocent(origin2);
                     }
                     break label1048;
                  case 2049700:
                  case 2049701:
                  case 2049702:
                  case 2049703:
                     if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                     } else if (nEquip.getState() <= 17) {
                        nEquip.setState((byte)2);
                        byte linexxx = 2;
                        if (Randomizer.nextInt(100) < 30) {
                           linexxx = 3;
                        }

                        nEquip.setLines(linexxx);
                        if (nZeroEquip != null) {
                           nZeroEquip.setState(linexxx);
                           nZeroEquip.setLines(linexxx);
                        }
                     }
                     break label1048;
                  case 2049704:
                  case 5063000:
                     if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                     } else if (nEquip.getState() <= 17) {
                        nEquip.setState((byte)4);
                        byte line = 2;
                        if (Randomizer.nextInt(100) < 30) {
                           line = 3;
                        }

                        nEquip.setLines(line);
                        if (nZeroEquip != null) {
                           nZeroEquip.setState((byte)4);
                           nZeroEquip.setLines(line);
                        }
                     }
                     break label1048;
                  case 2049750:
                  case 2049751:
                  case 2049752:
                  case 2049756:
                  case 2049757:
                  case 2049758:
                  case 2049767:
                  case 2049790:
                  case 2049792:
                     if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                     } else if (nEquip.getState() <= 19) {
                        nEquip.setState((byte)3);
                        byte linexx = 2;
                        if (Randomizer.nextInt(100) < 30) {
                           linexx = 3;
                        }

                        nEquip.setLines(linexx);
                        if (nZeroEquip != null) {
                           nZeroEquip.setState((byte)3);
                           nZeroEquip.setLines(linexx);
                        }
                     }
                     break label1048;
                  case 2049762:
                     for (int i = 0; i < 3; i++) {
                        int optionGrade = 3;
                        int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, nEquip.getPotentials(true, i), GradeRandomOption.Additional);
                        nEquip.setPotentialOption(i + 3, option);
                     }

                     if (nZeroEquip != null) {
                        nZeroEquip.setState(nEquip.getState());
                        nZeroEquip.setPotential1(nEquip.getPotential1());
                        nZeroEquip.setPotential2(nEquip.getPotential2());
                        nZeroEquip.setPotential3(nEquip.getPotential3());
                        nZeroEquip.setPotential4(nEquip.getPotential4());
                        nZeroEquip.setPotential5(nEquip.getPotential5());
                        nZeroEquip.setPotential6(nEquip.getPotential6());
                     }
                     break label1048;
                  case 2530000:
                  case 2530001:
                  case 2530002:
                     int flag = nEquip.getFlag();
                     flag |= ItemFlag.LUCKY_DAY_SCROLLED.getValue();
                     nEquip.setFlag(flag);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flag);
                     }
                     break label1048;
                  case 2531000:
                     int flagxxxx = nEquip.getFlag();
                     flagxxxx |= ItemFlag.PROTECTION_SCROLLED.getValue();
                     nEquip.setFlag(flagxxxx);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flagxxxx);
                     }
                     break label1048;
                  case 2532000:
                     int flagxxx = nEquip.getFlag();
                     flagxxx |= ItemFlag.SAFETY_SCROLLED.getValue();
                     nEquip.setFlag(flagxxx);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flagxxx);
                     }
                     break label1048;
                  case 5064300:
                     int flagxx = nEquip.getFlag();
                     flagxx |= ItemFlag.RECOVERY_SCROLLED.getValue();
                     nEquip.setFlag(flagxx);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flagxx);
                     }
                     break label1048;
                  case 5064400:
                     int flagx = nEquip.getFlag();
                     flagx |= ItemFlag.RETURN_SCROLLED.getValue();
                     nEquip.setFlag(flagx);
                     if (nZeroEquip != null) {
                        nZeroEquip.setFlag(flagx);
                     }
                     break label1048;
               }

               if (GameConstants.isChaosScroll(scrollId.getItemId())) {
                  if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                     if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr, nEquip))) {
                        if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                           return null;
                        }

                        chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                     }

                     failed = true;
                  } else {
                     int[] flags = new int[]{1, 2, 4, 8, 16, 32, 64, 256, 512, 4096, 8192};
                     String itemName = getInstance().getName(scrollId.getItemId());
                     boolean incredible = itemName.contains("놀라운");
                     boolean goodness = itemName.contains("긍정의");
                     boolean incredibleGoodnessScroll = incredible && goodness;
                     boolean goodnessScroll = !incredible && goodness;
                     boolean incredibleScroll = incredible && !goodness;
                     boolean normalChaosScroll = !incredible && !goodness;
                     int value = 0;
                     int flagxxxxxxx = 0;
                     int[] values = new int[11];

                     for (int i = 0; i < 11; i++) {
                        if (incredibleGoodnessScroll) {
                           value = OfficialRandomOption.incredibleChaosScrollOfGoodNess();
                        } else if (goodnessScroll) {
                           value = OfficialRandomOption.chaosScrollOfGoodNess();
                        } else if (incredibleScroll) {
                           value = OfficialRandomOption.incredibleChaosScroll();
                        } else if (normalChaosScroll) {
                           value = OfficialRandomOption.chaosScroll();
                        }

                        if (nEquip.getStatByNumber(i) > 0) {
                           if (i == 7 || i == 8) {
                              value *= 10;
                           }

                           flagxxxxxxx |= flags[i];
                           nEquip.setStatByNumber(i, (short)(nEquip.getStatByNumber(i) + value));
                        }

                        values[i] = value;
                        if (nZeroEquip != null && nZeroEquip.getStatByNumber(i) > 0) {
                           nZeroEquip.setStatByNumber(i, (short)(nZeroEquip.getStatByNumber(i) + value));
                        }
                     }

                     chr.getClient().getSession().writeAndFlush(CField.showChaosScrollResult(scrollId, nEquip, flagxxxxxxx, values));
                  }
               } else {
                  label1029: {
                     if (GameConstants.isEquipScroll(scrollId.getItemId()) && scrollId.getItemId() != 2049360 && scrollId.getItemId() != 2049361) {
                        if (Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                           boolean normal = GameConstants.isNormalEquipScroll(scrollId.getItemId());
                           if (!normal) {
                              int CHUC = MapleDataTool.getIntConvert("info/forceUpgrade", this.getItemData(scrollId.getItemId()));
                              nEquip.setCHUC(CHUC);
                              chr.setHuLastFailedUniqueID(0L);
                              if ((nEquip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0) {
                                 nEquip.setItemState(nEquip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                              }

                              if (nZeroEquip != null) {
                                 nZeroEquip.setCHUC(CHUC);
                                 if ((nZeroEquip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0) {
                                    nZeroEquip.setItemState(nZeroEquip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                                 }
                              }
                           }
                           break label1029;
                        }

                        if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr, nEquip))) {
                           if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                              return null;
                           }

                           chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                        }

                        failed = true;
                     }

                     if (scrollId.getItemId() != 2049360 && scrollId.getItemId() != 2049361) {
                        if (scrollId.getItemId() != 2049762 && scrollId.getItemId() != 2049731) {
                           if (GameConstants.isPotentialScroll(scrollId.getItemId())) {
                              if (scrollId.getItemId() == 2049790 || nEquip.getState() == 0) {
                                 if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                                    if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                                       if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                                          return null;
                                       }

                                       chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                                    }

                                    failed = true;
                                 } else {
                                    int level = 1;
                                    if (scrollId.getItemId() >= 2049750 && scrollId.getItemId() <= 2049767
                                       || scrollId.getItemId() == 2049790
                                       || scrollId.getItemId() == 2049792) {
                                       level = 3;
                                    }

                                    nEquip.setState((byte)level);
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setState((byte)level);
                                    }
                                 }
                              }
                           } else if (GameConstants.isRebirhFireScroll(scrollId.getItemId())) {
                              if (GameConstants.IsExNewScroll(scrollId.getItemId())) {
                                 BonusStatPlaceType placeType = BonusStatPlaceType.LevelledRebirthFlame;
                                 if (GameConstants.IsPowerfulRebirthFlame(scrollId.getItemId())) {
                                    placeType = BonusStatPlaceType.PowerfulRebirthFlame;
                                 } else if (GameConstants.IsEternalRebirthFlame(scrollId.getItemId())) {
                                    placeType = BonusStatPlaceType.EternalRebirthFlame;
                                 } else if (GameConstants.IsBlackRebirthFlame(scrollId.getItemId())) {
                                    placeType = BonusStatPlaceType.BlackRebirthFlame;
                                 }

                                 if (Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                                    if (nEquip.getFire() != -1L) {
                                       try (Connection con = DBConnection.getConnection()) {
                                          PreparedStatement ps = con.prepareStatement("SELECT * FROM fireitem WHERE uniqueid = ?");
                                          ps.setLong(1, nEquip.getFire());
                                          ResultSet rs = ps.executeQuery();
                                          if (rs.next()) {
                                             int value = 0;
                                             if ((value = rs.getInt("watk")) > 0) {
                                                nEquip.setWatk((short)(nEquip.getWatk() - value));
                                             }

                                             if ((value = rs.getInt("matk")) > 0) {
                                                nEquip.setMatk((short)(nEquip.getMatk() - value));
                                             }

                                             if ((value = rs.getInt("wdef")) > 0) {
                                                nEquip.setWdef((short)(nEquip.getWdef() - value));
                                             }

                                             if ((value = rs.getInt("mdef")) > 0) {
                                                nEquip.setMdef((short)(nEquip.getMdef() - value));
                                             }

                                             if ((value = rs.getInt("hp")) > 0) {
                                                nEquip.setHp((short)(nEquip.getHp() - value));
                                             }

                                             if ((value = rs.getInt("mp")) > 0) {
                                                nEquip.setMp((short)(nEquip.getMp() - value));
                                             }

                                             if ((value = rs.getInt("str")) > 0) {
                                                nEquip.setStr((short)(nEquip.getStr() - value));
                                             }

                                             if ((value = rs.getInt("dex")) > 0) {
                                                nEquip.setDex((short)(nEquip.getDex() - value));
                                             }

                                             if ((value = rs.getInt("_int")) > 0) {
                                                nEquip.setInt((short)(nEquip.getInt() - value));
                                             }

                                             if ((value = rs.getInt("luk")) > 0) {
                                                nEquip.setLuk((short)(nEquip.getLuk() - value));
                                             }

                                             if ((value = rs.getInt("acc")) > 0) {
                                                nEquip.setAcc((short)(nEquip.getAcc() - value));
                                             }

                                             if ((value = rs.getInt("avoid")) > 0) {
                                                nEquip.setAvoid((short)(nEquip.getAvoid() - value));
                                             }

                                             if ((value = rs.getInt("jump")) > 0) {
                                                nEquip.setJump((short)(nEquip.getJump() - value));
                                             }

                                             if ((value = rs.getInt("hands")) > 0) {
                                                nEquip.setHands((short)(nEquip.getHands() - value));
                                             }

                                             if ((value = rs.getInt("speed")) > 0) {
                                                nEquip.setSpeed((short)(nEquip.getSpeed() - value));
                                             }

                                             if ((value = rs.getInt("bossdamage")) > 0) {
                                                nEquip.setBossDamage((short)(nEquip.getBossDamage() - value));
                                             }

                                             if ((value = rs.getInt("ignorepdr")) > 0) {
                                                nEquip.setIgnorePDR((short)(nEquip.getIgnorePDR() - value));
                                             }

                                             if ((value = rs.getInt("allstat")) > 0) {
                                                nEquip.setAllStat((byte)(nEquip.getAllStat() - value));
                                             }

                                             ps.close();
                                             rs.close();
                                             ps = con.prepareStatement("DELETE FROM fireitem WHERE uniqueid = ?");
                                             ps.setLong(1, nEquip.getFire());
                                             ps.executeUpdate();
                                             ps.close();
                                          }
                                       } catch (SQLException var27) {
                                          var27.printStackTrace();
                                       }

                                       nEquip.setFire(-1L);
                                    }

                                    if (chr.isQuestStarted(501546)) {
                                       if (chr.getOneInfoQuestInteger(501546, "value") < 1) {
                                          chr.updateOneInfo(501546, "value", "1");
                                       }

                                       if (chr.getOneInfoQuestInteger(501524, "state") < 2) {
                                          chr.updateOneInfo(501524, "state", "2");
                                       }
                                    }

                                    Equip brfB = null;
                                    Equip brfA = null;
                                    if (GameConstants.IsBlackRebirthFlame(scrollId.getItemId())) {
                                       brfB = (Equip)nEquip.copy();
                                       brfA = (Equip)nEquip.copy();
                                       BonusStat.resetBonusStat(brfA, placeType);
                                       BlackRebirthFlame brf = new BlackRebirthFlame(scrollId.getItemId(), brfB, brfA);
                                       chr.setBlackRebirthFlame(brf);
                                    } else if (BonusStat.resetBonusStat(nEquip, placeType) && nZeroEquip != null) {
                                       nZeroEquip.setExGradeOption(nEquip.getExGradeOption());
                                    }
                                 }
                              }
                           } else if (!Randomizer.isSuccess(this.getSuccess(scrollId.getItemId(), chr, nEquip))) {
                              if (Randomizer.isSuccess(this.getCursed(scrollId.getItemId(), chr))) {
                                 if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                                    return null;
                                 }

                                 chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                              }

                              failed = true;
                           } else {
                              for (Entry<String, Integer> stat : stats.entrySet()) {
                                 String key = stat.getKey();
                                 if (key.equals("STR")) {
                                    nEquip.setStr((short)(nEquip.getStr() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setStr((short)(nZeroEquip.getStr() + stat.getValue()));
                                    }
                                 } else if (key.equals("DEX")) {
                                    nEquip.setDex((short)(nEquip.getDex() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setDex((short)(nZeroEquip.getDex() + stat.getValue()));
                                    }
                                 } else if (key.equals("INT")) {
                                    nEquip.setInt((short)(nEquip.getInt() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setInt((short)(nZeroEquip.getInt() + stat.getValue()));
                                    }
                                 } else if (key.equals("LUK")) {
                                    nEquip.setLuk((short)(nEquip.getLuk() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + stat.getValue()));
                                    }
                                 } else if (key.equals("PAD")) {
                                    nEquip.setWatk((short)(nEquip.getWatk() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + stat.getValue()));
                                    }
                                 } else if (key.equals("PDD")) {
                                    nEquip.setWdef((short)(nEquip.getWdef() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setWdef((short)(nZeroEquip.getWdef() + stat.getValue()));
                                    }
                                 } else if (key.equals("MAD")) {
                                    nEquip.setMatk((short)(nEquip.getMatk() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + stat.getValue()));
                                    }
                                 } else if (key.equals("MDD")) {
                                    nEquip.setMdef((short)(nEquip.getMdef() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setMdef((short)(nZeroEquip.getMdef() + stat.getValue()));
                                    }
                                 } else if (key.equals("ACC")) {
                                    nEquip.setAcc((short)(nEquip.getAcc() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setAcc((short)(nZeroEquip.getAcc() + stat.getValue()));
                                    }
                                 } else if (key.equals("EVA")) {
                                    nEquip.setAvoid((short)(nEquip.getAvoid() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setAvoid((short)(nZeroEquip.getAvoid() + stat.getValue()));
                                    }
                                 } else if (key.equals("Speed")) {
                                    nEquip.setSpeed((short)(nEquip.getSpeed() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setSpeed((short)(nZeroEquip.getSpeed() + stat.getValue()));
                                    }
                                 } else if (key.equals("Jump")) {
                                    nEquip.setJump((short)(nEquip.getJump() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setJump((short)(nZeroEquip.getJump() + stat.getValue()));
                                    }
                                 } else if (key.equals("MHP")) {
                                    nEquip.setHp((short)(nEquip.getHp() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setHp((short)(nZeroEquip.getHp() + stat.getValue()));
                                    }
                                 } else if (key.equals("MMP")) {
                                    nEquip.setMp((short)(nEquip.getMp() + stat.getValue()));
                                    if (nZeroEquip != null) {
                                       nZeroEquip.setMp((short)(nZeroEquip.getMp() + stat.getValue()));
                                    }
                                 }
                              }
                           }
                        } else {
                           for (int i = 0; i < 3; i++) {
                              int optionGrade = 3;
                              if (scrollId.getItemId() == 2049731) {
                                 optionGrade = 2;
                              }

                              int option = ItemOptionInfo.getItemOption(
                                 equip.getItemId(), optionGrade, nEquip.getPotentials(true, i), GradeRandomOption.Additional
                              );
                              nEquip.setPotentialOption(i + 3, option);
                           }

                           if (nZeroEquip != null) {
                              nZeroEquip.setState(nEquip.getState());
                              nZeroEquip.setPotential1(nEquip.getPotential1());
                              nZeroEquip.setPotential2(nEquip.getPotential2());
                              nZeroEquip.setPotential3(nEquip.getPotential3());
                              nZeroEquip.setPotential4(nEquip.getPotential4());
                              nZeroEquip.setPotential5(nEquip.getPotential5());
                              nZeroEquip.setPotential6(nEquip.getPotential6());
                           }
                        }
                     } else if (DBConfig.isGanglim) {
                        MapleData IData = this.getItemData(nEquip.getItemId());
                        MapleData info = IData.getChildByPath("info");
                        int levelx = MapleDataTool.getInt("reqLevel", info, 0);
                        if (levelx > 200) {
                           chr.dropMessage(6, "150레벨 이하의 장비 아이템에만 사용하실 수 있습니다.");
                        } else {
                           int chane;
                           switch (nEquip.getCHUC()) {
                              case 0:
                                 chane = 60;
                                 break;
                              case 1:
                                 chane = 55;
                                 break;
                              case 2:
                                 chane = 50;
                                 break;
                              case 3:
                                 chane = 40;
                                 break;
                              case 4:
                                 chane = 30;
                                 break;
                              case 5:
                                 chane = 20;
                                 break;
                              case 6:
                                 chane = 19;
                                 break;
                              case 7:
                                 chane = 18;
                                 break;
                              case 8:
                                 chane = 17;
                                 break;
                              case 9:
                                 chane = 16;
                                 break;
                              case 10:
                                 chane = 14;
                                 break;
                              case 11:
                                 chane = 12;
                                 break;
                              default:
                                 chane = 10;
                           }

                           if (chr.getGMLevel() > 0) {
                              chane = 100;
                           }

                           if (!Randomizer.isSuccess(chane)) {
                              if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                                 return null;
                              }

                              chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                           } else {
                              int ordinary;
                              if (GameConstants.isMagicWeapon(nEquip.getItemId())) {
                                 ordinary = nEquip.getMatk();
                              } else {
                                 ordinary = nEquip.getWatk();
                              }

                              int weaponwatk = ordinary / 50 + 1;
                              int weaponmatk = ordinary / 50 + 1;
                              int reallevel = levelx / 10 * 10;
                              int[] data;
                              switch (reallevel) {
                                 case 80:
                                    data = new int[]{2, 3, 5, 8, 12, 2, 3, 4, 5, 6, 7, 9, 10, 11};
                                    break;
                                 case 90:
                                    data = new int[]{4, 5, 7, 10, 14, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13};
                                    break;
                                 case 100:
                                    data = new int[]{7, 8, 10, 13, 17, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14};
                                    break;
                                 case 110:
                                    data = new int[]{9, 10, 12, 15, 19, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15};
                                    break;
                                 case 120:
                                    data = new int[]{12, 13, 15, 18, 22, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16};
                                    break;
                                 case 130:
                                    data = new int[]{14, 15, 17, 20, 24, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17};
                                    break;
                                 case 140:
                                    data = new int[]{17, 18, 20, 23, 27, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18};
                                    break;
                                 case 150:
                                    data = new int[]{19, 20, 22, 25, 29, 9, 10, 11, 12, 13, 14, 16, 17, 18, 19};
                                    break;
                                 default:
                                    data = new int[]{1, 2, 4, 7, 11, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11};
                              }

                              if (nEquip.getCHUC() < 5) {
                                 nEquip.addStr((short)data[nEquip.getCHUC()]);
                                 nEquip.addDex((short)data[nEquip.getCHUC()]);
                                 nEquip.addInt((short)data[nEquip.getCHUC()]);
                                 nEquip.addLuk((short)data[nEquip.getCHUC()]);
                                 if (nZeroEquip != null) {
                                    nZeroEquip.addStr((short)data[nEquip.getCHUC()]);
                                    nZeroEquip.addDex((short)data[nEquip.getCHUC()]);
                                    nZeroEquip.addInt((short)data[nEquip.getCHUC()]);
                                    nZeroEquip.addLuk((short)data[nEquip.getCHUC()]);
                                 }
                              } else {
                                 nEquip.addWatk((short)data[nEquip.getCHUC()]);
                                 nEquip.addMatk((short)data[nEquip.getCHUC()]);
                                 if (nZeroEquip != null) {
                                    nZeroEquip.addWatk((short)data[nEquip.getCHUC()]);
                                    nZeroEquip.addMatk((short)data[nEquip.getCHUC()]);
                                 }
                              }

                              if (GameConstants.isWeapon(nEquip.getItemId())) {
                                 nEquip.addWatk((short)weaponwatk);
                                 nEquip.addMatk((short)weaponmatk);
                                 if (Randomizer.nextBoolean()) {
                                    nEquip.addWatk((short)1);
                                    nEquip.addMatk((short)1);
                                    if (nZeroEquip != null) {
                                       nZeroEquip.addWatk((short)1);
                                       nZeroEquip.addMatk((short)1);
                                    }
                                 }
                              } else if (GameConstants.isAccessory(nEquip.getItemId()) && Randomizer.nextBoolean()) {
                                 if (levelx < 120) {
                                    if (nEquip.getCHUC() < 5) {
                                       nEquip.addStr((short)1);
                                       nEquip.addDex((short)1);
                                       nEquip.addInt((short)1);
                                       nEquip.addLuk((short)1);
                                       if (nZeroEquip != null) {
                                          nZeroEquip.addStr((short)1);
                                          nZeroEquip.addDex((short)1);
                                          nZeroEquip.addInt((short)1);
                                          nZeroEquip.addLuk((short)1);
                                       }
                                    } else {
                                       nEquip.addStr((short)2);
                                       nEquip.addDex((short)2);
                                       nEquip.addInt((short)2);
                                       nEquip.addLuk((short)2);
                                       if (nZeroEquip != null) {
                                          nZeroEquip.addStr((short)2);
                                          nZeroEquip.addDex((short)2);
                                          nZeroEquip.addInt((short)2);
                                          nZeroEquip.addLuk((short)2);
                                       }
                                    }
                                 } else if (nEquip.getCHUC() < 5) {
                                    int randxx = Randomizer.rand(1, 2);
                                    int rand2 = Randomizer.rand(1, 2);
                                    int rand3 = Randomizer.rand(1, 2);
                                    int rand4 = Randomizer.rand(1, 2);
                                    nEquip.addStr((short)randxx);
                                    nEquip.addDex((short)rand2);
                                    nEquip.addInt((short)rand3);
                                    nEquip.addLuk((short)rand4);
                                    if (nZeroEquip != null) {
                                       nZeroEquip.addStr((short)randxx);
                                       nZeroEquip.addDex((short)rand2);
                                       nZeroEquip.addInt((short)rand3);
                                       nZeroEquip.addLuk((short)rand4);
                                    }
                                 } else {
                                    nEquip.addStr((short)2);
                                    nEquip.addDex((short)2);
                                    nEquip.addInt((short)2);
                                    nEquip.addLuk((short)2);
                                    if (nZeroEquip != null) {
                                       nZeroEquip.addStr((short)2);
                                       nZeroEquip.addDex((short)2);
                                       nZeroEquip.addInt((short)2);
                                       nZeroEquip.addLuk((short)2);
                                    }
                                 }
                              }

                              nEquip.setCHUC((byte)(nEquip.getCHUC() + 1));
                              if ((nEquip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) != 0) {
                                 nEquip.setItemState(nEquip.getItemState() & ~ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                              }

                              if (nZeroEquip != null && (nZeroEquip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) != 0) {
                                 nZeroEquip.setItemState(nZeroEquip.getItemState() & ~ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                              }
                           }
                        }
                     } else {
                        int enhance = nEquip.getCHUC();
                        int statx = 0;
                        int atkxxxxxxx = 0;
                        int success = 0;
                        boolean isWeapon = GameConstants.isWeapon(nEquip.getItemId());
                        byte var87;
                        switch (enhance) {
                           case 0:
                              statx = 19;
                              atkxxxxxxx = isWeapon ? 6 : 0;
                              var87 = 60;
                              break;
                           case 1:
                              statx = 21;
                              atkxxxxxxx = isWeapon ? 6 : 0;
                              var87 = 55;
                              break;
                           case 2:
                              statx = 23;
                              atkxxxxxxx = isWeapon ? 7 : 0;
                              var87 = 50;
                              break;
                           case 3:
                              statx = 25;
                              atkxxxxxxx = isWeapon ? 7 : 0;
                              var87 = 40;
                              break;
                           case 4:
                              statx = 27;
                              atkxxxxxxx = isWeapon ? 8 : 0;
                              var87 = 30;
                              break;
                           case 5:
                              atkxxxxxxx = isWeapon ? 11 : 8;
                              var87 = 20;
                              break;
                           case 6:
                              atkxxxxxxx = isWeapon ? 15 : 9;
                              var87 = 19;
                              break;
                           case 7:
                              atkxxxxxxx = isWeapon ? 17 : 10;
                              var87 = 18;
                              break;
                           case 8:
                              atkxxxxxxx = isWeapon ? 18 : 11;
                              var87 = 17;
                              break;
                           case 9:
                              atkxxxxxxx = isWeapon ? 19 : 12;
                              var87 = 16;
                              break;
                           case 10:
                              atkxxxxxxx = isWeapon ? 22 : 13;
                              var87 = 14;
                              break;
                           case 11:
                              atkxxxxxxx = isWeapon ? 23 : 14;
                              var87 = 12;
                              break;
                           case 12:
                              atkxxxxxxx = isWeapon ? 25 : 15;
                              var87 = 10;
                              break;
                           case 13:
                              atkxxxxxxx = isWeapon ? 26 : 16;
                              var87 = 10;
                              break;
                           default:
                              atkxxxxxxx = isWeapon ? 27 : 17;
                              var87 = 10;
                        }

                        if (chr.isGM()) {
                           var87 = 100;
                        }

                        if (!Randomizer.isSuccess(var87)) {
                           if (Randomizer.isSuccess(100)) {
                              if (!ItemFlag.PROTECTION_SCROLLED.check(nEquip.getFlag())) {
                                 return null;
                              }

                              chr.dropMessage(5, "주문서의 효과로 아이템이 파괴되지 않았습니다.");
                           }

                           failed = true;
                        } else {
                           nEquip.setStr((short)(nEquip.getStr() + statx));
                           nEquip.setDex((short)(nEquip.getDex() + statx));
                           nEquip.setInt((short)(nEquip.getInt() + statx));
                           nEquip.setLuk((short)(nEquip.getLuk() + statx));
                           nEquip.setWatk((short)(nEquip.getWatk() + atkxxxxxxx));
                           nEquip.setMatk((short)(nEquip.getMatk() + atkxxxxxxx));
                           nEquip.setCHUC((byte)(nEquip.getCHUC() + 1));
                           if ((nEquip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) != 0) {
                              nEquip.setItemState(nEquip.getItemState() & ~ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                           }

                           if (nZeroEquip != null) {
                              nZeroEquip.setStr((short)(nZeroEquip.getStr() + statx));
                              nZeroEquip.setDex((short)(nZeroEquip.getDex() + statx));
                              nZeroEquip.setInt((short)(nZeroEquip.getInt() + statx));
                              nZeroEquip.setLuk((short)(nZeroEquip.getLuk() + statx));
                              nZeroEquip.setWatk((short)(nZeroEquip.getWatk() + atkxxxxxxx));
                              nZeroEquip.setMatk((short)(nZeroEquip.getMatk() + atkxxxxxxx));
                              nZeroEquip.setCHUC((byte)(nZeroEquip.getCHUC() + 1));
                              if ((nZeroEquip.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) != 0) {
                                 nZeroEquip.setItemState(nZeroEquip.getItemState() & ~ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!GameConstants.isCleanSlate(scrollId.getItemId())
               && !GameConstants.isSpecialScroll(scrollId.getItemId())
               && !GameConstants.isEquipScroll(scrollId.getItemId())
               && !GameConstants.isPotentialScroll(scrollId.getItemId())
               && !GameConstants.isRebirhFireScroll(scrollId.getItemId())
               && scrollId.getItemId() != 2049360
               && scrollId.getItemId() != 2049361
               && scrollId.getItemId() / 100 != 20496
            || scrollId.getItemId() == 2643132
            || scrollId.getItemId() == 2643133
            || scrollId.getItemId() == 2643131) {
            if ((ItemFlag.SAFETY_SCROLLED.check(nEquip.getFlag()) || ItemFlag.RETURN_SCROLLED.check(nEquip.getFlag())) && failed) {
               chr.dropMessage(5, "주문서의 효과로 업그레이드 가능 횟수가 차감되지 않았습니다.");
            } else {
               nEquip.setUpgradeSlots((byte)(nEquip.getUpgradeSlots() - this.getUpgradeScrollUseSlot(scrollId.getItemId())));
               if (nZeroEquip != null) {
                  nZeroEquip.setUpgradeSlots((byte)(nZeroEquip.getUpgradeSlots() - this.getUpgradeScrollUseSlot(scrollId.getItemId())));
               }
            }

            if (!failed) {
               nEquip.setLevel((byte)(nEquip.getLevel() + 1));
               if (nZeroEquip != null) {
                  nZeroEquip.setLevel((byte)(nZeroEquip.getLevel() + 1));
               }
            }
         }
      }

      equip = nEquip.copy();
      return nEquip;
   }

   public final void zeroEquipReset(MapleCharacter player) {
      Equip nEquip = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
      Equip nEquip2 = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
      Equip origin = (Equip)getInstance().getEquipById(nEquip.getItemId());
      origin.setDurability(nEquip.getDurability());
      origin.setExpiration(nEquip.getExpiration());
      origin.setFlag(nEquip.getFlag());
      origin.setLines(nEquip.getLines());
      origin.setState(nEquip.getState());
      origin.setPotential1(nEquip.getPotential1());
      origin.setPotential2(nEquip.getPotential2());
      origin.setPotential3(nEquip.getPotential3());
      origin.setPotential4(nEquip.getPotential4());
      origin.setPotential5(nEquip.getPotential5());
      origin.setPotential6(nEquip.getPotential6());
      origin.setFusionAnvil(nEquip.getFusionAnvil());
      origin.setEnhance(nEquip.getEnhance());
      origin.setStarForce(nEquip.getStarForce());
      origin.setCHUC(nEquip.getCHUC());
      origin.setPosition((short)-10);
      nEquip = (Equip)origin.copy();
      if (nEquip2 != null) {
         Equip origin2 = (Equip)getInstance().getEquipById(nEquip2.getItemId());
         origin2.setDurability(nEquip2.getDurability());
         origin2.setExpiration(nEquip2.getExpiration());
         origin2.setFlag(nEquip2.getFlag());
         origin2.setLines(nEquip2.getLines());
         origin2.setState(nEquip2.getState());
         origin2.setPotential1(nEquip2.getPotential1());
         origin2.setPotential2(nEquip2.getPotential2());
         origin2.setPotential3(nEquip2.getPotential3());
         origin2.setPotential4(nEquip2.getPotential4());
         origin2.setPotential5(nEquip2.getPotential5());
         origin2.setPotential6(nEquip2.getPotential6());
         origin2.setFusionAnvil(nEquip2.getFusionAnvil());
         origin2.setEnhance(nEquip.getEnhance());
         origin2.setStarForce(nEquip.getStarForce());
         origin2.setPosition((short)-11);
         origin2.setCHUC(nEquip.getCHUC());
         nEquip2 = (Equip)origin2.copy();
      }

      player.forceReAddItem(nEquip, MapleInventoryType.EQUIPPED);
      player.forceReAddItem(nEquip2, MapleInventoryType.EQUIPPED);
      player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(nEquip2, (byte)1, player, (byte)12));
      player.send(CWvsContext.InventoryPacket.updateSpecialItemUse(nEquip, (byte)1, player, (byte)11));
   }

   public Map<String, Short> getScrollData(int scrollId) {
      if (this.scrollInfo.get(scrollId) != null) {
         return this.scrollInfo.get(scrollId);
      } else {
         Map<String, Short> scrollIncData = new HashMap<>();

         for (String stat : GameConstants.stats) {
            MapleData data = this.getItemData(scrollId);
            if (data == null) {
               return null;
            }

            short value = (short)MapleDataTool.getInt("info/" + stat, data, 0);
            if (value != 0) {
               scrollIncData.put(stat, value);
            }
         }

         this.scrollInfo.put(scrollId, scrollIncData);
         return scrollIncData;
      }
   }

   private final short getRandStatFire(short originalValue, int maxRange, boolean isFirst, String type, long uniqueid) {
      short defaultValue = originalValue;
      int rate = Randomizer.rand(1, 10);
      int minus = 0;
      DBConnection db = new DBConnection();
      short value = 0;

      try (Connection con = DBConnection.getConnection()) {
         if (!isFirst) {
            PreparedStatement ps = con.prepareStatement("SELECT " + type + " FROM fireitem WHERE uniqueid = ?");
            ps.setLong(1, uniqueid);
            ResultSet RS = ps.executeQuery();
            if (RS.next()) {
               minus = RS.getInt(type);
               defaultValue = (short)(defaultValue - RS.getInt(type));
            }

            RS.close();
            ps.close();
         }

         if (rate >= 7) {
            int rate2 = Randomizer.rand(1, 10);
            value = rate2 >= 5 ? (short)Randomizer.rand(0, maxRange) : (short)Randomizer.rand(0, (int)Math.ceil(maxRange / 2));
         }

         PreparedStatement ps = con.prepareStatement("UPDATE fireitem SET " + type + " = ? WHERE uniqueid = ?");
         ps.setInt(1, value);
         ps.setLong(2, uniqueid);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var18) {
         var18.printStackTrace();
      }

      return (short)(defaultValue + value);
   }

   public final Equip randomizeStatsFire(Equip equip, boolean isFirst) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         if (isFirst) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO fireitem (`uniqueid`) VALUE (?)");
            ps.setLong(1, equip.getFire());
            ps.executeUpdate();
            ps.close();
         }
      } catch (SQLException var10) {
         var10.printStackTrace();
      }

      equip.setStr(this.getRandStatFire(equip.getStr(), 60, isFirst, "str", equip.getFire()));
      equip.setDex(this.getRandStatFire(equip.getDex(), 60, isFirst, "dex", equip.getFire()));
      equip.setInt(this.getRandStatFire(equip.getInt(), 60, isFirst, "_int", equip.getFire()));
      equip.setLuk(this.getRandStatFire(equip.getLuk(), 60, isFirst, "luk", equip.getFire()));
      equip.setMatk(this.getRandStatFire(equip.getMatk(), GameConstants.isWeapon(equip.getItemId()) ? 40 : 15, isFirst, "matk", equip.getFire()));
      equip.setWatk(this.getRandStatFire(equip.getWatk(), GameConstants.isWeapon(equip.getItemId()) ? 40 : 15, isFirst, "watk", equip.getFire()));
      equip.setJump(this.getRandStatFire(equip.getJump(), 20, isFirst, "jump", equip.getFire()));
      equip.setHands(this.getRandStatFire(equip.getHands(), 40, isFirst, "hands", equip.getFire()));
      equip.setSpeed(this.getRandStatFire(equip.getSpeed(), 20, isFirst, "speed", equip.getFire()));
      equip.setWdef(this.getRandStatFire(equip.getWdef(), 40, isFirst, "wdef", equip.getFire()));
      equip.setHp(this.getRandStatFire(equip.getHp(), 2500, isFirst, "hp", equip.getFire()));
      equip.setMp(this.getRandStatFire(equip.getMp(), 2500, isFirst, "mp", equip.getFire()));
      int flag = equip.getFlag();
      flag |= ItemFlag.POSSIBLE_TRADING.getValue();
      equip.setFlag(flag);
      return equip;
   }

   private static int getEquipLevel(int level) {
      int stat = 0;
      byte var2;
      if (level >= 0 && level <= 50) {
         var2 = 1;
      } else if (level >= 51 && level <= 100) {
         var2 = 2;
      } else {
         var2 = 3;
      }

      return var2;
   }

   public final int getSuccess(int itemId, MapleCharacter player, Item equip) {
      int success = this.getSuccess_(itemId, player, equip);
      if (player.getStat().itemUpgradeBonusR > 0 && !GameConstants.isEquipScroll(itemId)) {
         success = Math.min(100, success + player.getStat().itemUpgradeBonusR);
         if (player.isGM()) {
            System.out.println(player.getStat().itemUpgradeBonusR + " 만큼 증가하여 " + success + "%로 적용");
         }
      }

      return success;
   }

   public final int getSuccess_(int itemId, MapleCharacter player, Item equip) {
      if (player.getGMLevel() > 0) {
         return 100;
      } else {
         Equip t = (Equip)equip.copy();
         if (itemId / 100 == 20493) {
            int success = 0;
            Equip lev = (Equip)equip.copy();
            byte leve = lev.getEnhance();
            switch (itemId) {
               case 2049300:
               case 2049303:
               case 2049306:
               case 2049323:
                  if (leve == 0) {
                     success = 100;
                  } else if (leve == 1) {
                     success = 90;
                  } else if (leve == 2) {
                     success = 80;
                  } else if (leve == 3) {
                     success = 70;
                  } else if (leve == 4) {
                     success = 60;
                  } else if (leve == 5) {
                     success = 50;
                  } else if (leve == 6) {
                     success = 40;
                  } else if (leve == 7) {
                     success = 30;
                  } else if (leve == 8) {
                     success = 20;
                  } else if (leve == 9) {
                     success = 10;
                  } else if (leve >= 10) {
                     success = 5;
                  }

                  return success;
               case 2049301:
               case 2049307:
                  if (leve == 0) {
                     success = 80;
                  } else if (leve == 1) {
                     success = 70;
                  } else if (leve == 2) {
                     success = 60;
                  } else if (leve == 3) {
                     success = 50;
                  } else if (leve == 4) {
                     success = 40;
                  } else if (leve == 5) {
                     success = 30;
                  } else if (leve == 6) {
                     success = 20;
                  } else if (leve == 7) {
                     success = 10;
                  } else if (leve >= 8) {
                     success = 5;
                  }

                  return success;
            }
         }

         switch (itemId) {
            case 2046841:
            case 2046842:
            case 2046967:
            case 2046971:
            case 2047803:
            case 2047917:
               return 20;
            default:
               if (equip == null) {
                  System.err.println("[오류] 주문서의 성공확률을 구하던 중, 장비 아이템 값에 널 값이 입력되었습니다." + itemId);
                  player.dropMessage(5, "[오류] 현재 주문서의 성공확률을 구하는데 실패하였습니다.");
                  player.gainItem(itemId, 1, false, -1L, "주문서 성공확률 얻기 실패로 얻은 주문서");
                  player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
                  return 0;
               } else if (this.successCache.containsKey(itemId)) {
                  return this.successCache.get(itemId);
               } else {
                  MapleData item = this.getItemData(itemId);
                  if (item == null) {
                     System.err.println("[오류] 주문서의 성공확률을 구하던 중, 주문서 데이터 값에 널 값이 입력되었습니다." + itemId);
                     player.dropMessage(5, "[오류] 현재 주문서의 성공확률을 구하는데 실패하였습니다.");
                     player.gainItem(itemId, 1, false, -1L, "주문서 성공확률 얻기 실패로 얻은 주문서");
                     player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
                     return 0;
                  } else {
                     int success = 0;
                     if (item.getChildByPath("info/successRates") != null) {
                        success = MapleDataTool.getIntConvert(t.getLevel() + "", item.getChildByPath("info/successRates"), 20);
                     } else {
                        success = MapleDataTool.getIntConvert("info/success", item, 100);
                     }

                     if (!GameConstants.isPotentialScroll(itemId) && !GameConstants.isEquipScroll(itemId) && ItemFlag.LUCKY_DAY_SCROLLED.check(t.getFlag())) {
                        success += 10;
                     }

                     this.successCache.put(itemId, success);
                     return success;
                  }
               }
         }
      }
   }

   public final int getSetItemCategory(int itemID) {
      MapleData item = this.getItemData(itemID);
      return item != null ? MapleDataTool.getIntConvert("info/setItemCategory", item, -1) : -1;
   }

   public final Item getEquipById(int equipId) {
      return this.getEquipById(equipId, -1L);
   }

   public final Item getEquipById(int equipId, long ringId) {
      ItemInformation i = this.getItemInformation(equipId);
      if (i == null) {
         return new Equip(equipId, (short)0, ringId, 0);
      } else {
         Item eq = i.eq.copy();
         eq.setUniqueId(ringId);
         Equip eqz = (Equip)eq;
         if (!this.isCash(equipId)) {
            BonusStat.resetBonusStat(eq, BonusStatPlaceType.FromNormal, true);
         }

         return eq;
      }
   }

   protected final short getRandStatFusion(short defaultValue, int value1, int value2) {
      if (defaultValue == 0) {
         return 0;
      } else {
         int range = (value1 + value2) / 2 - defaultValue;
         int rand = Randomizer.nextInt(Math.abs(range) + 1);
         return (short)(defaultValue + (range < 0 ? -rand : rand));
      }
   }

   protected final short getRandStat(short defaultValue, int maxRange) {
      if (defaultValue == 0) {
         return 0;
      } else {
         int lMaxRange = (int)Math.min(Math.ceil(defaultValue * 0.1), (double)maxRange);
         return (short)(defaultValue - lMaxRange + Randomizer.nextInt(lMaxRange * 2 + 1));
      }
   }

   protected final short getRandStatAbove(short defaultValue, int maxRange) {
      if (defaultValue <= 0) {
         return 0;
      } else {
         int lMaxRange = (int)Math.min(Math.ceil(defaultValue * 0.1), (double)maxRange);
         return (short)(defaultValue + Randomizer.nextInt(lMaxRange + 1));
      }
   }

   public final Equip randomizeStats(Equip equip) {
      if (!DBConfig.isGanglim) {
         return equip;
      } else {
         equip.setStr(this.getRandStat(equip.getStr(), 0));
         equip.setDex(this.getRandStat(equip.getDex(), 0));
         equip.setInt(this.getRandStat(equip.getInt(), 0));
         equip.setLuk(this.getRandStat(equip.getLuk(), 0));
         equip.setMatk(this.getRandStat(equip.getMatk(), 0));
         equip.setWatk(this.getRandStat(equip.getWatk(), 0));
         equip.setAcc(this.getRandStat(equip.getAcc(), 0));
         equip.setAvoid(this.getRandStat(equip.getAvoid(), 0));
         equip.setJump(this.getRandStat(equip.getJump(), 0));
         equip.setHands(this.getRandStat(equip.getHands(), 0));
         equip.setSpeed(this.getRandStat(equip.getSpeed(), 0));
         equip.setWdef(this.getRandStat(equip.getWdef(), 0));
         equip.setMdef(this.getRandStat(equip.getMdef(), 0));
         equip.setHp(this.getRandStat(equip.getHp(), 0));
         equip.setMp(this.getRandStat(equip.getMp(), 0));
         return equip;
      }
   }

   public final Equip randomizeStats_Above(Equip equip) {
      equip.setStr(this.getRandStatAbove(equip.getStr(), 5));
      equip.setDex(this.getRandStatAbove(equip.getDex(), 5));
      equip.setInt(this.getRandStatAbove(equip.getInt(), 5));
      equip.setLuk(this.getRandStatAbove(equip.getLuk(), 5));
      equip.setMatk(this.getRandStatAbove(equip.getMatk(), 5));
      equip.setWatk(this.getRandStatAbove(equip.getWatk(), 5));
      equip.setAcc(this.getRandStatAbove(equip.getAcc(), 5));
      equip.setAvoid(this.getRandStatAbove(equip.getAvoid(), 5));
      equip.setJump(this.getRandStatAbove(equip.getJump(), 5));
      equip.setHands(this.getRandStatAbove(equip.getHands(), 5));
      equip.setSpeed(this.getRandStatAbove(equip.getSpeed(), 5));
      equip.setWdef(this.getRandStatAbove(equip.getWdef(), 10));
      equip.setMdef(this.getRandStatAbove(equip.getMdef(), 10));
      equip.setHp(this.getRandStatAbove(equip.getHp(), 10));
      equip.setMp(this.getRandStatAbove(equip.getMp(), 10));
      return equip;
   }

   public final Equip randomizeStats_Necro(Equip equip) {
      if (!DBConfig.isGanglim) {
         return equip;
      } else {
         if (equip.getStr() > 0) {
            equip.setStr((short)(equip.getStr() + 30 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(5))));
         }

         if (equip.getDex() > 0) {
            equip.setDex((short)(equip.getDex() + 30 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(5))));
         }

         if (equip.getInt() > 0) {
            equip.setInt((short)(equip.getInt() + 30 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(5))));
         }

         if (equip.getLuk() > 0) {
            equip.setLuk((short)(equip.getLuk() + 30 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(5))));
         }

         if (equip.getMatk() > 0) {
            equip.setMatk((short)(equip.getMatk() + 15 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(5))));
         }

         if (equip.getWatk() > 0) {
            equip.setWatk((short)(equip.getWatk() + 15 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(5))));
         }

         if (equip.getWdef() > 0) {
            equip.setWdef((short)(equip.getWdef() + 200 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(30) : Randomizer.nextInt(30))));
         }

         if (equip.getMdef() > 0) {
            equip.setMdef((short)(equip.getMdef() + 200 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(30) : Randomizer.nextInt(30))));
         }

         if (equip.getHp() > 0) {
            equip.setHp((short)(equip.getHp() + 3000 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(300) : Randomizer.nextInt(300))));
         }

         if (equip.getMp() > 0) {
            equip.setMp((short)(equip.getMp() + 3000 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(300) : Randomizer.nextInt(300))));
         }

         return equip;
      }
   }

   public final Equip randomizeStats_RoyalVonLeon(Equip equip) {
      if (!DBConfig.isGanglim) {
         return equip;
      } else {
         if (equip.getStr() > 0) {
            equip.setStr((short)(equip.getStr() + 40 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getDex() > 0) {
            equip.setDex((short)(equip.getDex() + 40 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getInt() > 0) {
            equip.setInt((short)(equip.getInt() + 40 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getLuk() > 0) {
            equip.setLuk((short)(equip.getLuk() + 40 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getMatk() > 0) {
            equip.setMatk((short)(equip.getMatk() + 20 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getWatk() > 0) {
            equip.setWatk((short)(equip.getWatk() + 20 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getWdef() > 0) {
            equip.setWdef((short)(equip.getWdef() + 250 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(30) : Randomizer.nextInt(40))));
         }

         if (equip.getMdef() > 0) {
            equip.setMdef((short)(equip.getMdef() + 250 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(30) : Randomizer.nextInt(40))));
         }

         if (equip.getHp() > 0) {
            equip.setHp((short)(equip.getHp() + 4000 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(300) : Randomizer.nextInt(400))));
         }

         if (equip.getMp() > 0) {
            equip.setMp((short)(equip.getMp() + 4000 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(300) : Randomizer.nextInt(400))));
         }

         return equip;
      }
   }

   public final Equip randomizeStats_Fensalir(Equip equip) {
      if (!DBConfig.isGanglim) {
         return equip;
      } else {
         if (equip.getStr() > 0) {
            equip.setStr((short)(equip.getStr() + 50 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getDex() > 0) {
            equip.setDex((short)(equip.getDex() + 50 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getInt() > 0) {
            equip.setInt((short)(equip.getInt() + 50 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getLuk() > 0) {
            equip.setLuk((short)(equip.getLuk() + 50 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getMatk() > 0) {
            equip.setMatk((short)(equip.getMatk() + 25 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getWatk() > 0) {
            equip.setWatk((short)(equip.getWatk() + 25 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(5) : Randomizer.nextInt(10))));
         }

         if (equip.getWdef() > 0) {
            equip.setWdef((short)(equip.getWdef() + 300 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(30) : Randomizer.nextInt(40))));
         }

         if (equip.getMdef() > 0) {
            equip.setMdef((short)(equip.getMdef() + 300 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(30) : Randomizer.nextInt(40))));
         }

         if (equip.getHp() > 0) {
            equip.setHp((short)(equip.getHp() + 4500 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(300) : Randomizer.nextInt(400))));
         }

         if (equip.getMp() > 0) {
            equip.setMp((short)(equip.getMp() + 4500 + (Randomizer.nextBoolean() ? -Randomizer.nextInt(300) : Randomizer.nextInt(400))));
         }

         return equip;
      }
   }

   public final Equip fuse(Equip equip1, Equip equip2) {
      if (equip1.getItemId() != equip2.getItemId()) {
         return equip1;
      } else {
         Equip equip = (Equip)this.getEquipById(equip1.getItemId());
         equip.setStr(this.getRandStatFusion(equip.getStr(), equip1.getStr(), equip2.getStr()));
         equip.setDex(this.getRandStatFusion(equip.getDex(), equip1.getDex(), equip2.getDex()));
         equip.setInt(this.getRandStatFusion(equip.getInt(), equip1.getInt(), equip2.getInt()));
         equip.setLuk(this.getRandStatFusion(equip.getLuk(), equip1.getLuk(), equip2.getLuk()));
         equip.setMatk(this.getRandStatFusion(equip.getMatk(), equip1.getMatk(), equip2.getMatk()));
         equip.setWatk(this.getRandStatFusion(equip.getWatk(), equip1.getWatk(), equip2.getWatk()));
         equip.setAcc(this.getRandStatFusion(equip.getAcc(), equip1.getAcc(), equip2.getAcc()));
         equip.setAvoid(this.getRandStatFusion(equip.getAvoid(), equip1.getAvoid(), equip2.getAvoid()));
         equip.setJump(this.getRandStatFusion(equip.getJump(), equip1.getJump(), equip2.getJump()));
         equip.setHands(this.getRandStatFusion(equip.getHands(), equip1.getHands(), equip2.getHands()));
         equip.setSpeed(this.getRandStatFusion(equip.getSpeed(), equip1.getSpeed(), equip2.getSpeed()));
         equip.setWdef(this.getRandStatFusion(equip.getWdef(), equip1.getWdef(), equip2.getWdef()));
         equip.setMdef(this.getRandStatFusion(equip.getMdef(), equip1.getMdef(), equip2.getMdef()));
         equip.setHp(this.getRandStatFusion(equip.getHp(), equip1.getHp(), equip2.getHp()));
         equip.setMp(this.getRandStatFusion(equip.getMp(), equip1.getMp(), equip2.getMp()));
         return equip;
      }
   }

   public final int getTotalStat(Equip equip) {
      return equip.getStr()
         + equip.getDex()
         + equip.getInt()
         + equip.getLuk()
         + equip.getMatk()
         + equip.getWatk()
         + equip.getAcc()
         + equip.getAvoid()
         + equip.getJump()
         + equip.getHands()
         + equip.getSpeed()
         + equip.getHp()
         + equip.getMp()
         + equip.getWdef()
         + equip.getMdef();
   }

   public final SecondaryStatEffect getItemEffect(int itemId) {
      SecondaryStatEffect ret = this.itemEffects.get(itemId);
      if (ret == null) {
         MapleData item = this.getItemData(itemId);
         if (item == null || item.getChildByPath("spec") == null) {
            return null;
         }

         ret = SecondaryStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
         ret.addMapList(item);
         this.itemEffects.put(itemId, ret);
      }

      return ret;
   }

   public final SecondaryStatEffect getItemEffectEX(int itemId) {
      SecondaryStatEffect ret = this.itemEffectsEx.get(itemId);
      if (ret == null) {
         MapleData item = this.getItemData(itemId);
         if (item == null || item.getChildByPath("specEx") == null) {
            return null;
         }

         ret = SecondaryStatEffect.loadItemEffectFromData(item.getChildByPath("specEx"), itemId);
         this.itemEffectsEx.put(itemId, ret);
      }

      return ret;
   }

   public final int getCreateId(int id) {
      ItemInformation i = this.getItemInformation(id);
      return i == null ? 0 : i.create;
   }

   public final int getCardMobId(int id) {
      ItemInformation i = this.getItemInformation(id);
      return i == null ? 0 : i.monsterBook;
   }

   public final int getBagSlotCount(int itemID) {
      MapleData item = this.getItemData(itemID);
      return item != null ? MapleDataTool.getInt("spec/slotCount", item, 0) : 0;
   }

   public final int getBagType_(int itemID) {
      MapleData item = this.getItemData(itemID);
      return item != null ? MapleDataTool.getInt("spec/type", item, -1) : -1;
   }

   public final int getBagType(int id) {
      ItemInformation i = this.getItemInformation(id);
      return i == null ? 0 : i.flag & 15;
   }

   public final int getWatkForProjectile(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i != null && i.equipStats != null && i.equipStats.get("incPAD") != null ? i.equipStats.get("incPAD") : 0;
   }

   public final boolean canScroll(int scrollid, int itemid) {
      return scrollid / 100 % 100 == itemid / 10000 % 100;
   }

   public final String getName(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.name;
   }

   public final String getDesc(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.desc;
   }

   public final String getMsg(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.msg;
   }

   public final short getItemMakeLevel(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? 0 : i.itemMakeLevel;
   }

   public final boolean isDropRestricted(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null
         ? false
         : ((i.flag & 512) != 0 || (i.flag & 1024) != 0 || GameConstants.isDropRestricted(itemId))
            && (itemId == 3012000 || itemId == 3012015 || itemId / 10000 != 301)
            && itemId != 2041200
            && itemId != 5640000
            && itemId != 4170023
            && itemId != 2040124
            && itemId != 2040125
            && itemId != 2040126
            && itemId != 2040211
            && itemId != 2040212
            && itemId != 2040227
            && itemId != 2040228
            && itemId != 2040229
            && itemId != 2040230
            && itemId != 1002926
            && itemId != 1002906
            && itemId != 1002927;
   }

   public final boolean isPickupRestricted(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null
         ? false
         : ((i.flag & 128) != 0 || GameConstants.isPickupRestricted(itemId)) && itemId != 4001168 && itemId != 4031306 && itemId != 4031307;
   }

   public final boolean isEquipTradeBlocked(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      if (i == null) {
         return false;
      } else {
         return GameConstants.isAndroid(itemId) && (i.flag & 256) != 0 ? false : i.equipStats != null && i.equipStats.get("equipTradeBlock") != null;
      }
   }

   public final boolean isTradeBlocked(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      if (i == null) {
         return false;
      } else {
         if (!DBConfig.isGanglim) {
            switch (itemId) {
               case 2434560:
               case 2439259:
               case 2631879:
                  return false;
            }
         }

         return (i.flag & 1024) != 0;
      }
   }

   public final boolean isAccountShared(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 256) != 0;
   }

   public final int getStateChangeItem(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      if (i == null) {
         return 0;
      } else {
         int itemID = i.stateChange;
         if (itemID == 2023558) {
            itemID = 2450124;
         }

         return itemID;
      }
   }

   public final int getMeso(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? 0 : i.meso;
   }

   public final boolean isShareTagEnabled(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 2048) != 0;
   }

   public final boolean isKarmaEnabled(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : i.karmaEnabled == 1;
   }

   public final boolean isPKarmaEnabled(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : i.karmaEnabled == 2;
   }

   public final boolean isPickupBlocked(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 64) != 0;
   }

   public final boolean isLogoutExpire(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 32) != 0;
   }

   public final boolean cantSell(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 16) != 0;
   }

   public final Pair<Integer, List<StructRewardItem>> getRewardItem(int itemid) {
      ItemInformation i = this.getItemInformation(itemid);
      return i == null ? null : new Pair<>(i.totalprob, i.rewardItems);
   }

   public final boolean isMobHP(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 4096) != 0;
   }

   public final boolean isQuestItem(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? false : (i.flag & 512) != 0 && itemId / 10000 != 301;
   }

   public final Pair<Integer, List<Integer>> questItemInfo(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : new Pair<>(i.questId, i.questItems);
   }

   public final Pair<Integer, String> replaceItemInfo(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : new Pair<>(i.replaceItem, i.replaceMsg);
   }

   public final List<Triple<String, Point, Point>> getAfterImage(String after) {
      return this.afterImage.get(after);
   }

   public final String getAfterImage(int itemId) {
      ItemInformation i = this.getItemInformation(itemId);
      return i == null ? null : i.afterImage;
   }

   public final boolean itemExists(int itemId) {
      return itemId / 1000000 != 9 && GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED ? false : this.getItemInformation(itemId) != null;
   }

   public final boolean isCash(int itemId) {
      return this.getEquipStats(itemId) == null
         ? GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH
         : GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || this.getEquipStats(itemId).get("cash") != null;
   }

   public final boolean isSpecialLabel(int itemID) {
      return this.getEquipStats(itemID) == null
         ? true
         : this.getEquipStats(itemID).get("masterSpecial") != null || this.getEquipStats(itemID).get("royalSpecial") != null;
   }

   public final ItemInformation getItemInformation(int itemId) {
      return itemId <= 0 ? null : this.dataCache.get(itemId);
   }

   public void initItemRewardData(ResultSet sqlRewardData) throws SQLException {
      int itemID = sqlRewardData.getInt("itemid");
      if (this.tmpInfo == null || this.tmpInfo.itemId != itemID) {
         if (!this.dataCache.containsKey(itemID)) {
            System.out.println("[initItemRewardData] Tried to load an item while this is not in the cache: " + itemID);
            return;
         }

         this.tmpInfo = this.dataCache.get(itemID);
      }

      if (this.tmpInfo.rewardItems == null) {
         this.tmpInfo.rewardItems = new ArrayList<>();
      }

      StructRewardItem add = new StructRewardItem();
      add.itemid = sqlRewardData.getInt("item");
      add.period = add.itemid == 1122017 ? Math.max(sqlRewardData.getInt("period"), 7200) : sqlRewardData.getInt("period");
      add.prob = sqlRewardData.getInt("prob");
      add.quantity = sqlRewardData.getShort("quantity");
      add.worldmsg = sqlRewardData.getString("worldMsg").length() <= 0 ? null : sqlRewardData.getString("worldMsg");
      add.effect = sqlRewardData.getString("effect");
      this.tmpInfo.rewardItems.add(add);
   }

   public void initItemAddData(ResultSet sqlAddData) throws SQLException {
      int itemID = sqlAddData.getInt("itemid");
      if (this.tmpInfo == null || this.tmpInfo.itemId != itemID) {
         if (!this.dataCache.containsKey(itemID)) {
            System.out.println("[initItemAddData] Tried to load an item while this is not in the cache: " + itemID);
            return;
         }

         this.tmpInfo = this.dataCache.get(itemID);
      }

      if (this.tmpInfo.equipAdditions == null) {
         this.tmpInfo.equipAdditions = new LinkedList<>();
      }

      while (sqlAddData.next()) {
         this.tmpInfo.equipAdditions.add(new Triple<>(sqlAddData.getString("key"), sqlAddData.getString("subKey"), sqlAddData.getString("value")));
      }
   }

   public void initItemEquipData(ResultSet sqlEquipData) throws SQLException {
      int itemID = sqlEquipData.getInt("itemid");
      if (this.tmpInfo == null || this.tmpInfo.itemId != itemID) {
         if (!this.dataCache.containsKey(itemID)) {
            System.out.println("[initItemEquipData] Tried to load an item while this is not in the cache: " + itemID);
            return;
         }

         this.tmpInfo = this.dataCache.get(itemID);
      }

      if (this.tmpInfo.equipStats == null) {
         this.tmpInfo.equipStats = new HashMap<>();
      }

      int itemLevel = sqlEquipData.getInt("itemLevel");
      if (itemLevel == -1) {
         this.tmpInfo.equipStats.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
      } else {
         if (this.tmpInfo.equipIncs == null) {
            this.tmpInfo.equipIncs = new HashMap<>();
         }

         Map<String, Integer> toAdd = this.tmpInfo.equipIncs.get(itemLevel);
         if (toAdd == null) {
            toAdd = new HashMap<>();
            this.tmpInfo.equipIncs.put(itemLevel, toAdd);
         }

         toAdd.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
      }
   }

   public void finalizeEquipData(ItemInformation item) {
      int itemId = item.itemId;
      if (item.equipStats == null) {
         item.equipStats = new HashMap<>();
      }

      item.eq = new Equip(itemId, (short)0, -1L, 0);
      short stats = GameConstants.getStat(itemId, 0);
      if (stats > 0) {
         item.eq.setStr(stats);
         item.eq.setDex(stats);
         item.eq.setInt(stats);
         item.eq.setLuk(stats);
      }

      stats = GameConstants.getATK(itemId, 0);
      if (stats > 0) {
         item.eq.setWatk(stats);
         item.eq.setMatk(stats);
      }

      stats = GameConstants.getHpMp(itemId, 0);
      if (stats > 0) {
         item.eq.setHp(stats);
         item.eq.setMp(stats);
      }

      stats = GameConstants.getDEF(itemId, 0);
      if (stats > 0) {
         item.eq.setWdef(stats);
         item.eq.setMdef(stats);
      }

      if (item.equipStats.size() > 0) {
         if (!item.name.isEmpty()
            && Pattern.matches("^[a-zA-Z|\\s]*$", item.name)
            && itemId / 10000 != 190
            && !item.eq.isMasterLabel()
            && !item.eq.isSpecialLabel()
            && item.equipStats.get("cash") != null) {
            this.gmsCashList.add(itemId);
         }

         for (Entry<String, Integer> stat : item.equipStats.entrySet()) {
            String key = stat.getKey();
            if (key.equals("royalSpecial")) {
               this.specialLabels.add(itemId);
               item.eq.setSpecialLabel(stat.getValue() == 1);
            } else if (key.equals("masterSpecial")) {
               this.masterLabels.add(itemId);
               item.eq.setMasterLabel(stat.getValue() == 1);
            } else if (key.equals("STR")) {
               item.eq.setStr(GameConstants.getStat(itemId, stat.getValue()));
            } else if (key.equals("DEX")) {
               item.eq.setDex(GameConstants.getStat(itemId, stat.getValue()));
            } else if (key.equals("INT")) {
               item.eq.setInt(GameConstants.getStat(itemId, stat.getValue()));
            } else if (key.equals("LUK")) {
               item.eq.setLuk(GameConstants.getStat(itemId, stat.getValue()));
            } else if (key.equals("PAD")) {
               item.eq.setWatk(GameConstants.getATK(itemId, stat.getValue()));
            } else if (key.equals("PDD")) {
               item.eq.setWdef(GameConstants.getDEF(itemId, stat.getValue()));
            } else if (key.equals("MAD")) {
               item.eq.setMatk(GameConstants.getATK(itemId, stat.getValue()));
            } else if (key.equals("MDD")) {
               item.eq.setMdef(GameConstants.getDEF(itemId, stat.getValue()));
            } else if (key.equals("ACC")) {
               item.eq.setAcc((short)stat.getValue().intValue());
            } else if (key.equals("EVA")) {
               item.eq.setAvoid((short)stat.getValue().intValue());
            } else if (key.equals("Speed")) {
               item.eq.setSpeed((short)stat.getValue().intValue());
            } else if (key.equals("Jump")) {
               item.eq.setJump((short)stat.getValue().intValue());
            } else if (key.equals("MHP")) {
               item.eq.setHp(GameConstants.getHpMp(itemId, stat.getValue()));
            } else if (key.equals("MMP")) {
               item.eq.setMp(GameConstants.getHpMp(itemId, stat.getValue()));
            } else if (key.equals("MHPr")) {
               item.eq.setHpR((short)stat.getValue().intValue());
            } else if (key.equals("MMPr")) {
               item.eq.setMpR((short)stat.getValue().intValue());
            } else if (key.equals("tuc")) {
               item.eq.setUpgradeSlots(stat.getValue().byteValue());
            } else if (key.equals("Craft")) {
               item.eq.setHands(stat.getValue().shortValue());
            } else if (key.equals("durability")) {
               item.eq.setDurability(stat.getValue());
            } else if (key.equals("charmEXP")) {
               item.eq.setCharmEXP(stat.getValue().shortValue());
            } else if (key.equals("PVPDamage")) {
               item.eq.setPVPDamage(stat.getValue().shortValue());
            } else if (key.equals("bdR")) {
               item.eq.setBossDamage(stat.getValue().shortValue());
            } else if (key.equals("imdR")) {
               item.eq.setIgnorePDR(stat.getValue().shortValue());
            }
         }

         if (item.equipStats.get("cash") != null && item.eq.getCharmEXP() <= 0) {
            short exp = 0;
            int identifier = itemId / 10000;
            if (GameConstants.isWeapon(itemId) || identifier == 106) {
               exp = 60;
            } else if (identifier == 100) {
               exp = 50;
            } else if (GameConstants.isAccessory(itemId) || identifier == 102 || identifier == 108 || identifier == 107) {
               exp = 40;
            } else if (identifier == 104 || identifier == 105 || identifier == 110) {
               exp = 30;
            }

            item.eq.setCharmEXP(exp);
         }
      }

      if (!DBConfig.isGanglim && item.itemId == 1672077) {
         item.eq.setCHUC(15);
         item.eq.setUpgradeSlots((byte)0);
         item.eq.setLevel((byte)10);
         item.eq.setViciousHammer((byte)1);
      }

      if (!DBConfig.isGanglim && item.itemId == 1032219) {
         item.eq.setCHUC(20);
         item.eq.setUpgradeSlots((byte)0);
         item.eq.setLevel((byte)10);
         item.eq.setViciousHammer((byte)1);
      }

      if (!DBConfig.isGanglim && item.itemId == 1022281) {
         item.eq.setStr((short)200);
         item.eq.setDex((short)200);
         item.eq.setInt((short)200);
         item.eq.setLuk((short)200);
         item.eq.setMatk((short)75);
         item.eq.setWatk((short)75);
      }

      if (!DBConfig.isGanglim && item.itemId == 1022286) {
         item.eq.setStr((short)250);
         item.eq.setDex((short)250);
         item.eq.setInt((short)250);
         item.eq.setLuk((short)250);
         item.eq.setMatk((short)100);
         item.eq.setWatk((short)100);
      }

      if (item.itemId == 1112917 && !DBConfig.isGanglim) {
         item.eq.setStr((short)300);
         item.eq.setDex((short)300);
         item.eq.setInt((short)300);
         item.eq.setLuk((short)300);
         item.eq.setMatk((short)200);
         item.eq.setWatk((short)200);
      }
   }

   public void initItemInformation(ResultSet sqlItemData) throws SQLException {
      ItemInformation ret = new ItemInformation();
      int itemId = sqlItemData.getInt("itemid");
      ret.itemId = itemId;
      ret.slotMax = GameConstants.getSlotMax(itemId) > 0 ? GameConstants.getSlotMax(itemId) : sqlItemData.getShort("slotMax");
      ret.price = Double.parseDouble(sqlItemData.getString("price"));
      ret.wholePrice = sqlItemData.getInt("wholePrice");
      ret.stateChange = sqlItemData.getInt("stateChange");
      ret.name = sqlItemData.getString("name");
      ret.desc = sqlItemData.getString("desc");
      ret.msg = sqlItemData.getString("msg");

      try {
         ret.type = sqlItemData.getInt("type");
      } catch (Exception var12) {
      }

      ret.flag = sqlItemData.getInt("flags");
      ret.karmaEnabled = sqlItemData.getByte("karma");
      ret.meso = sqlItemData.getInt("meso");
      ret.monsterBook = sqlItemData.getInt("monsterBook");
      ret.itemMakeLevel = sqlItemData.getShort("itemMakeLevel");
      ret.questId = sqlItemData.getInt("questId");
      ret.create = sqlItemData.getInt("create");
      ret.replaceItem = sqlItemData.getInt("replaceId");
      ret.replaceMsg = sqlItemData.getString("replaceMsg");
      ret.afterImage = sqlItemData.getString("afterImage");
      ret.bossReward = sqlItemData.getInt("bossReward") == 1;
      ret.jokerToSetItem = sqlItemData.getInt("jokerToSetItem") == 1;
      ret.nickSkill = sqlItemData.getInt("nickSkill");
      ret.nickSkillTimeLimited = sqlItemData.getInt("nickSkillTimeLimited");
      ret.cardSet = 0;
      if (ret.monsterBook > 0 && itemId / 10000 == 238) {
         this.mobIds.put(ret.monsterBook, itemId);

         for (Entry<Integer, Triple<Integer, List<Integer>, List<Integer>>> set : this.monsterBookSets.entrySet()) {
            if (((List)set.getValue().mid).contains(itemId)) {
               ret.cardSet = set.getKey();
               break;
            }
         }
      }

      String scrollRq = sqlItemData.getString("scrollReqs");
      if (scrollRq != null && scrollRq.length() > 0) {
         ret.scrollReqs = new ArrayList<>();
         String[] scroll = scrollRq.split(",");

         for (String s : scroll) {
            if (s.length() > 1) {
               ret.scrollReqs.add(Integer.parseInt(s));
            }
         }
      }

      String consumeItem = sqlItemData.getString("consumeItem");
      if (consumeItem != null && consumeItem.length() > 0) {
         ret.questItems = new ArrayList<>();
         String[] scroll = scrollRq.split(",");

         for (String sx : scroll) {
            if (sx.length() > 1) {
               ret.questItems.add(Integer.parseInt(sx));
            }
         }
      }

      ret.totalprob = sqlItemData.getInt("totalprob");
      String incRq = sqlItemData.getString("incSkill");
      if (incRq.length() > 0) {
         ret.incSkill = new ArrayList<>();
         String[] scroll = incRq.split(",");

         for (String sxx : scroll) {
            if (sxx.length() > 1) {
               ret.incSkill.add(Integer.parseInt(sxx));
            }
         }
      }

      this.dataCache.put(itemId, ret);
   }
}
