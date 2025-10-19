package constants;

import database.DBConfig;
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
import objects.utils.Properties;
import objects.utils.Randomizer;
import objects.utils.Table;

public class DummyCharacterName {
   public static long lastUpdateTime = 0L;
   public static List<String> dummyList = new ArrayList<>();
   public static Map<Integer, List<String>> inGameDummyList = new HashMap<>();

   public static void loadDummyCharacterNames() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "DummyCharacterNames.data");
      int count = 0;

      for (Table children : table.list()) {
         String name = children.getProperty("Name");
         dummyList.add(name);
         count++;
      }

      System.out.println("[DEBUG] 총 " + count + "개의 더미 캐릭터 데이터를 로드했습니다.");
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
