package constants.devtempConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.auction.AuctionServer;
import network.game.GameServer;
import network.shop.CashShopServer;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DummyCharacterName {
   public static long lastUpdateTime = 0L;
   public static List<String> dummyList = new ArrayList<>();
   public static Map<Integer, List<String>> inGameDummyList = new HashMap<>();

   public static void Load() {
      String value = null;
      FileInputStream setting = null;
      XSSFWorkbook workbook = null;
      dummyList.clear();

      try {
         Path filePath = FileSystems.getDefault().getPath(CustomDataSetting.path, CustomDataSetting.filename);
         setting = new FileInputStream(filePath.toString());
         workbook = new XSSFWorkbook(setting);
         XSSFSheet sheet = workbook.getSheet("DummyCharacterNames");
         String name = "";

         for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow curRow = sheet.getRow(i);

            for (int z = 0; z < curRow.getPhysicalNumberOfCells(); z++) {
               XSSFCell curCell = curRow.getCell(z);
               switch (curCell.getCellType()) {
                  case FORMULA:
                     value = curCell.getCellFormula();
                     break;
                  case NUMERIC:
                     value = String.valueOf((int)curCell.getNumericCellValue());
                     break;
                  case STRING:
                     value = curCell.getStringCellValue();
                     break;
                  case BOOLEAN:
                     value = String.valueOf(curCell.getBooleanCellValue());
               }

               switch (z) {
                  case 0:
                     name = value;
                     break;
               }
            }

            dummyList.add(name);
         }

         System.out.println("총 " + dummyList.size() + "개의 더미 캐릭터 데이터를 로드했습니다.");
      } catch (Exception var18) {
         var18.printStackTrace();
      } finally {
         try {
            if (workbook != null) {
               workbook.close();
            }

            if (setting != null) {
               setting.close();
            }
         } catch (IOException var17) {
            var17.printStackTrace();
         }
      }
   }

   public static String getConnected(double rate) {
      String ret = "";
      Map<Integer, List<String>> finalMap = new HashMap<>();
      Map<Integer, List<String>> players = new HashMap<>();
      int realCCU = 0;

      for (GameServer gameServer : GameServer.getAllInstances()) {
         int count = 0;
         List<String> list = new ArrayList<>();

         for (MapleCharacter player : gameServer.getPlayerStorage().getAllCharacters()) {
            if (player != null && player.getClient().getSession().isOpen()) {
               if (player.getName().equals("GM")) {
                  list.add("1772895");
               } else if (player.getName().equals("강림메이플")) {
                  list.add("168789512");
               } else {
                  list.add(player.getName());
               }

               count++;
               realCCU++;
            }
         }

         players.put(gameServer.getChannel(), list);
         finalMap.put(gameServer.getChannel(), list);
      }

      Map<Integer, List<String>> dummy = new HashMap<>(inGameDummyList);
      boolean update = false;
      if (lastUpdateTime == 0L) {
         lastUpdateTime = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() - lastUpdateTime >= 60000L) {
         update = true;
      }

      for (Entry<Integer, List<String>> entry : players.entrySet()) {
         int channel = entry.getKey();
         List<String> p = entry.getValue();
         int ccu = (int)(p.size() * rate);
         List<String> channelDummy = new ArrayList<>();
         if (dummy.get(channel) != null) {
            channelDummy = new ArrayList<>(dummy.get(channel));
         }

         if (update) {
            int size = channelDummy.size();

            for (int i = 0; i < size; i++) {
               if (Randomizer.isSuccess(7)) {
                  channelDummy.remove(0);
               }
            }
         }

         int c = p.size() + channelDummy.size();
         if (c != ccu) {
            int delta = ccu - c;
            if (delta != 0) {
               if (delta > 0) {
                  int count = 0;

                  do {
                     Collections.shuffle(dummyList);
                     String pick = dummyList.stream().findAny().orElse("");
                     if (!pick.isEmpty()) {
                        boolean find = false;

                        label145:
                        for (Entry<Integer, List<String>> e : dummy.entrySet()) {
                           Iterator var21 = e.getValue().iterator();

                           while (true) {
                              if (var21.hasNext()) {
                                 String s = (String)var21.next();
                                 if (!s.equals(pick)) {
                                    continue;
                                 }

                                 find = true;
                              }

                              if (find) {
                                 break label145;
                              }
                              break;
                           }
                        }

                        if (!find) {
                           channelDummy.add(pick);
                           int cc = p.size() + channelDummy.size();
                           if (ccu <= cc) {
                              break;
                           }
                        }
                     }
                  } while (count++ < 10000);
               } else {
                  for (int ix = 0; ix < Math.abs(delta) && !channelDummy.isEmpty(); ix++) {
                     channelDummy.remove(Randomizer.rand(0, channelDummy.size() - 1));
                  }
               }
            }
         }

         dummy.put(channel, channelDummy);
         List<String> l = finalMap.get(channel);
         l.addAll(channelDummy);
         finalMap.put(channel, l);
      }

      inGameDummyList = dummy;
      List<String> finalList = new ArrayList<>();

      for (List<String> e : finalMap.values()) {
         finalList.addAll(e);
      }

      for (MapleCharacter px : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
         if (px != null) {
            if (px.getName().equals("GM")) {
               finalList.add("1772895");
            } else if (px.getName().equals("강림메이플")) {
               finalList.add("168789512");
            } else {
               finalList.add(px.getName());
            }
         }
      }

      for (MapleCharacter pxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
         if (pxx != null) {
            if (pxx.getName().equals("GM")) {
               finalList.add("1772895");
            } else if (pxx.getName().equals("강림메이플")) {
               finalList.add("168789512");
            } else {
               finalList.add(pxx.getName());
            }
         }
      }

      ret = ret + String.join(", ", finalList);
      if (update) {
         lastUpdateTime = System.currentTimeMillis();
      }

      return ret;
   }
}
