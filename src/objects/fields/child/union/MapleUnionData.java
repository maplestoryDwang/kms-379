package objects.fields.child.union;

import constants.GameConstants;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class MapleUnionData {
   public static List<String> rankName = new ArrayList<>();
   public static Map<Integer, List<UnionRankData>> rankData = new HashMap<>();
   public static Map<Integer, UnionCardData> cardData = new HashMap<>();
   public static Map<Integer, UnionSkillInfo> skillInfo = new HashMap<>();
   public static Map<Integer, UnionBoardInfo> boardInfo = new HashMap<>();
   public static Map<Point, UnionBoardInfo> boardInfo_point = new HashMap<>();
   public static Map<Integer, UnionCharacterSizeInfo> characterSizeInfo = new HashMap<>();
   public static UnionCharacterRank characterRank_default;
   public static UnionCharacterRank characterRank_mobile;
   public static UnionCharacterRank characterRank_zero;
   public static List<Integer> changeableGroup = new ArrayList<>();

   public static void loadData() {
      String WZpath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));

      for (MapleData dat : prov.getData("mapleUnion.img")) {
         String var5 = dat.getName();
         switch (var5) {
            case "unionRank":
               for (MapleData rankData : dat) {
                  int rank = Integer.parseInt(rankData.getName());
                  List<UnionRankData> list = new ArrayList<>();

                  for (MapleData tierData : rankData) {
                     if (tierData.getName().equals("info")) {
                        rankName.add(MapleDataTool.getString("name", tierData, ""));
                     } else {
                        int tier = Integer.parseInt(tierData.getName());
                        int attackerCount = MapleDataTool.getInt("attackerCount", tierData, 0);
                        int coinStackMax = MapleDataTool.getInt("coinStackMax", tierData, 0);
                        int level = MapleDataTool.getInt("level", tierData, 0);
                        list.add(new UnionRankData(attackerCount, coinStackMax, level, rank, tier));
                     }
                  }

                  MapleUnionData.rankData.put(rank, list);
               }
               break;
            case "Card":
               for (MapleData d : dat) {
                  cardData.put(Integer.parseInt(d.getName()), new UnionCardData(MapleDataTool.getInt("skillID", d, 0)));
               }
               break;
            case "SkillInfo":
               for (MapleData d : dat) {
                  skillInfo.put(
                     Integer.parseInt(d.getName()), new UnionSkillInfo(MapleDataTool.getInt("changeable", d, 0) == 1, MapleDataTool.getInt("skillID", d, 0))
                  );
               }
               break;
            case "BoardInfo":
               for (MapleData d : dat) {
                  boolean changeable = MapleDataTool.getInt("changeable", d, 0) == 1;
                  int groupIndex = MapleDataTool.getInt("groupIndex", d, 0);
                  int openLevel = MapleDataTool.getInt("openLevel", d, 0);
                  Point pos = new Point(MapleDataTool.getInt("xPos", d, 0), MapleDataTool.getInt("yPos", d, 0));
                  UnionBoardInfo boardInfo = new UnionBoardInfo(changeable, groupIndex, openLevel, pos.x, pos.y);
                  MapleUnionData.boardInfo.put(Integer.parseInt(d.getName()), boardInfo);
                  boardInfo_point.put(pos, boardInfo);
               }
               break;
            case "CharacterSize":
               for (MapleData d : dat) {
                  Map<Integer, List<Point>> points = new HashMap<>();

                  for (MapleData s : d) {
                     List<Point> pos = new ArrayList<>();

                     for (MapleData n : s) {
                        pos.add(MapleDataTool.getPoint(n));
                     }

                     points.put(Integer.parseInt(s.getName()), pos);
                  }

                  UnionCharacterSizeInfo sizeInfo = new UnionCharacterSizeInfo(points);
                  characterSizeInfo.put(Integer.parseInt(d.getName()), sizeInfo);
               }
               break;
            case "CharacterRank":
               List<Integer> r = new ArrayList<>();

               for (MapleData d : dat) {
                  try {
                     int n = Integer.parseInt(d.getName());
                     r.add(MapleDataTool.getInt(d, 0));
                  } catch (NumberFormatException var17) {
                     List<Integer> r_ = new ArrayList<>();

                     for (MapleData d_ : d) {
                        r_.add(MapleDataTool.getInt(d_, 0));
                     }

                     String var41 = d.getName();
                     switch (var41) {
                        case "mobile":
                           characterRank_mobile = new UnionCharacterRank(r_);
                           break;
                        case "zero":
                           characterRank_zero = new UnionCharacterRank(r_);
                     }
                  }
               }

               UnionCharacterRank rank = new UnionCharacterRank(r);
               characterRank_default = rank;
         }

         for (Entry<Integer, UnionSkillInfo> info : skillInfo.entrySet()) {
            if (info.getValue().isChangeable()) {
               changeableGroup.add(info.getKey());
            }
         }
      }
   }

   public static String getRankFullName(int r) {
      int num = r / 100;
      int tier = r % 100;
      return getRankName(num - 1) + " " + tier + "단계";
   }

   public static String getRankName(int r) {
      return rankName.isEmpty() ? "노비스 유니온" : rankName.get(r);
   }

   public static int getRankByJobLevel(int job, int level) {
      if (job == 10000900) {
         return characterRank_mobile.getRank(level);
      } else {
         return GameConstants.isZero(job) ? characterRank_zero.getRank(level) : characterRank_default.getRank(level);
      }
   }

   public static List<Point> getPointsByJobLevel(int job, int level) {
      int rank = getRankByJobLevel(job, level);
      if (rank >= 0) {
         if (GameConstants.isXenon(job)) {
            return characterSizeInfo.get(36).getPoints().get(rank);
         }

         UnionCharacterSizeInfo info = characterSizeInfo.get(GameConstants.jobCategory(job));
         if (info != null) {
            return info.getPoints().get(rank);
         }
      }

      return new ArrayList<>();
   }

   public static UnionRankData getRankData(int rank) {
      return getRankData(rank / 100, rank % 100);
   }

   public static UnionRankData getRankData(int rank, int tier) {
      return rankData.get(rank).get(tier - 1);
   }

   public static UnionCardData getUnionCardData(int job) {
      return cardData.get(job / 10);
   }

   public static UnionBoardInfo getUnionBoardInfo(int index) {
      return boardInfo.get(index);
   }

   public static UnionBoardInfo getUnionBoardInfoByPos(Point pos) {
      return boardInfo_point.get(pos);
   }

   public static UnionSkillInfo getGroupSkillInfo(int group) {
      return skillInfo.get(group);
   }
}
