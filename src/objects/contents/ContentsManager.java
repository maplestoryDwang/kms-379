package objects.contents;

import database.DBConfig;
import database.DBConnection;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import network.center.Center;
import network.discordbot.DiscordBotHandler;
import network.models.CField;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import objects.utils.Timer;
import objects.utils.Triple;

public class ContentsManager {
   public static void main(String[] args) {
      ContentsManager.JaumQuizGame.LoadJaumGameDB();
   }

   public static class JaumQuizGame {
      String type;
      String Answer;
      public static List<ContentsManager.JaumQuizGame> jaums = new ArrayList<>();
      public static String currentJaumQuiz = null;
      public static int currentRound = 1;

      public JaumQuizGame(String type, String Answer) {
         this.type = type;
         this.Answer = Answer;
      }

      public String getAnswer() {
         return this.Answer;
      }

      public String getType() {
         return this.type;
      }

      public static void LoadJaumGameDB() {
         if (!DBConfig.isGanglim) {
            try {
               for (String line : Files.readAllLines(Paths.get("garo.txt"), Charset.forName("UTF-8"))) {
                  if (!line.contains("다음 중") && line.contains("(")) {
                     String q = line.substring(line.lastIndexOf("("));
                     q = q.replace("(", "");
                     q = q.replace(")", "");
                     if (Pattern.matches("^[ㄱ-ㅎ가-힣]*$", q)) {
                        String ans = line.substring(0, line.lastIndexOf("("));
                        jaums.add(new ContentsManager.JaumQuizGame(ans, q));
                     }
                  }
               }
            } catch (IOException var5) {
               var5.printStackTrace();
            }

            Collections.shuffle(jaums);
            System.out.println("[자음퀴즈] DB가 로딩되었습니다. " + jaums.size() + "개");
            startGameSchedule();
         }
      }

      public static void startGameSchedule() {
         Timer.EventTimer.getInstance()
            .register(
               new Runnable() {
                  @Override
                  public void run() {
                     if (ContentsManager.JaumQuizGame.currentJaumQuiz != null) {
                        Center.Broadcast.broadcastMessage(CField.chatMsg(7, "정답은 : " + ContentsManager.JaumQuizGame.currentJaumQuiz + " 였습니다!"));
                     }

                     int round = ContentsManager.JaumQuizGame.currentRound;
                     int random = Randomizer.nextInt(ContentsManager.JaumQuizGame.jaums.size());
                     ContentsManager.JaumQuizGame.currentJaumQuiz = ContentsManager.JaumQuizGame.jaums.get(random).Answer;
                     Center.Broadcast.broadcastMessage(
                        CField.chatMsg(
                           6,
                           "상식퀴즈!(유니온 코인지급) 문제 : "
                              + ContentsManager.JaumQuizGame.jaums.get(random).type
                              + " ["
                              + ContentsManager.JaumQuizGame.convertName(ContentsManager.JaumQuizGame.jaums.get(random).Answer)
                              + "]"
                        )
                     );
                     ContentsManager.JaumQuizGame.currentRound++;
                  }
               },
               180000L,
               180000L
            );
      }

      public static boolean checkJaumAnswer(MapleCharacter chr, String chat) {
         if (currentJaumQuiz == null) {
            return false;
         } else if (chat.equals(currentJaumQuiz)) {
            if (System.currentTimeMillis() - chr.getOneInfoQuestLong(1234567, "CheckJaumTime") < 900000L) {
               chr.dropMessage(5, "마지막 정답 이후 15분이 흐르지 않았습니다. 조금 더 기다려 주세요!");
               return false;
            } else {
               Center.Broadcast.broadcastMessage(CField.chatMsg(7, chr.getName() + "님 정답! 유니온 코인 30개가 지급됩니다."));
               Center.Broadcast.broadcastMessage(CField.chatMsg(7, "정답 : " + currentJaumQuiz));
               chr.updateOneInfo(1234567, "CheckJaumTime", String.valueOf(System.currentTimeMillis()));
               currentJaumQuiz = null;
               chr.send(CField.MapEff("Map/Effect.img/killing/clear"));
               return true;
            }
         } else {
            return false;
         }
      }

      public static String convertName(String name) {
         String rtName = "";

         try {
            for (int i = 0; i < name.length(); i++) {
               char epName = name.charAt(i);
               rtName = rtName + Direct(epName);
            }
         } catch (Exception var4) {
         }

         return rtName;
      }

      public static String Direct(char b) {
         String chosung = null;
         int first = (b - '가') / 588;
         switch (first) {
            case 0:
               chosung = "ㄱ";
               break;
            case 1:
               chosung = "ㄲ";
               break;
            case 2:
               chosung = "ㄴ";
               break;
            case 3:
               chosung = "ㄷ";
               break;
            case 4:
               chosung = "ㄸ";
               break;
            case 5:
               chosung = "ㄹ";
               break;
            case 6:
               chosung = "ㅁ";
               break;
            case 7:
               chosung = "ㅂ";
               break;
            case 8:
               chosung = "ㅃ";
               break;
            case 9:
               chosung = "ㅅ";
               break;
            case 10:
               chosung = "ㅆ";
               break;
            case 11:
               chosung = "ㅇ";
               break;
            case 12:
               chosung = "ㅈ";
               break;
            case 13:
               chosung = "ㅉ";
               break;
            case 14:
               chosung = "ㅊ";
               break;
            case 15:
               chosung = "ㅋ";
               break;
            case 16:
               chosung = "ㅌ";
               break;
            case 17:
               chosung = "ㅍ";
               break;
            case 18:
               chosung = "ㅎ";
               break;
            default:
               chosung = String.valueOf(b);
         }

         return chosung;
      }
   }

   public static class SpeedLadderGame {
      public static int currentRound = 1;
      public static long currentGameStartTime = 0L;
      public static List<SpeedLadder> ladders = new ArrayList<>();
      public static HashMap<String, Triple<Byte, Byte, Long>> betMan = new HashMap<>();
      public static long todayTotalBet = 0L;
      public static long todayTotalBetWinning = 0L;
      public static int todayRound = 1;
      public static int todayDay = new Date().getDay();

      public static void LoadLadderGameDB() {
         try {
            DBConnection db = new DBConnection();

            try (
               Connection con = DBConnection.getConnection();
               PreparedStatement ps = con.prepareStatement("SELECT * FROM `speedladder`");
               ResultSet rs = ps.executeQuery();
            ) {
               while (rs.next()) {
                  currentRound = rs.getInt("round") + 1;
                  ladders.add(new SpeedLadder(rs.getInt("round"), rs.getByte("right"), rs.getByte("line"), rs.getByte("odd")));
               }
            }

            if (DBConfig.isGanglim) {
               startGameSchedule();
            }
         } catch (SQLException var12) {
            var12.printStackTrace();
         }
      }

      public static void startGameSchedule() {
         currentGameStartTime = System.currentTimeMillis();
         Timer.EventTimer.getInstance()
            .register(
               new Runnable() {
                  @Override
                  public void run() {
                     int round = ContentsManager.SpeedLadderGame.currentRound;
                     byte right = (byte)Randomizer.rand(0, 1);
                     byte line = (byte)Randomizer.rand(3, 4);
                     byte odd = 0;
                     if (right > 0) {
                        if (line % 2 == 1) {
                           odd = 1;
                        }
                     } else if (line % 2 == 0) {
                        odd = 1;
                     }

                     Center.Broadcast.broadcastMessageLadderGame(
                        CField.chatMsg(
                           8,
                           "["
                              + round
                              + "회차 사다리 결과] : "
                              + (right > 0 ? "우출발 /" : "좌출발 / ")
                              + " "
                              + line
                              + " / "
                              + (odd > 0 ? "홀 /" : "짝 /")
                              + " 당첨되신 분들의 당첨을 축하합니다. (알림을 원하시지 않는다면 @사다리 명령어를 이용해주세요.)"
                        )
                     );

                     try (Connection con = DBConnection.getConnection()) {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO `speedladder` (`round`, `right`, `line`, `odd`) VALUES (?, ?, ?, ?)");
                        ps.setInt(1, ContentsManager.SpeedLadderGame.currentRound);
                        ps.setInt(2, right);
                        ps.setInt(3, line);
                        ps.setInt(4, odd);
                        ps.executeUpdate();
                        ps.close();
                     } catch (SQLException var15) {
                     }

                     ContentsManager.SpeedLadderGame.ladders.add(new SpeedLadder(ContentsManager.SpeedLadderGame.currentRound, right, line, odd));
                     Map<String, Long> winner = new HashMap<>();
                     Map<String, Long> looser = new HashMap<>();

                     for (String key : ContentsManager.SpeedLadderGame.betMan.keySet()) {
                        byte type = (Byte)ContentsManager.SpeedLadderGame.betMan.get(key).left;
                        byte flag = (Byte)ContentsManager.SpeedLadderGame.betMan.get(key).mid;
                        long meso = (Long)ContentsManager.SpeedLadderGame.betMan.get(key).right;
                        switch (type) {
                           case 1:
                              if ((right != 1 || flag != 1) && (right != 0 || flag != 2)) {
                                 looser.put(key, meso);
                              } else {
                                 winner.put(key, (long)(meso * 1.85));
                                 ContentsManager.SpeedLadderGame.todayTotalBetWinning = (long)(
                                    ContentsManager.SpeedLadderGame.todayTotalBetWinning + meso * 1.85
                                 );
                              }
                              break;
                           case 2:
                              if ((line != 3 || flag != 1) && (line != 4 || flag != 2)) {
                                 looser.put(key, meso);
                                 break;
                              }

                              winner.put(key, (long)(meso * 1.85));
                              ContentsManager.SpeedLadderGame.todayTotalBetWinning = (long)(ContentsManager.SpeedLadderGame.todayTotalBetWinning + meso * 1.85);
                              break;
                           case 3:
                              if ((odd != 1 || flag != 1) && (odd != 0 || flag != 2)) {
                                 looser.put(key, meso);
                                 break;
                              }

                              winner.put(key, (long)(meso * 1.85));
                              ContentsManager.SpeedLadderGame.todayTotalBetWinning = (long)(ContentsManager.SpeedLadderGame.todayTotalBetWinning + meso * 1.85);
                              break;
                           case 4:
                              if ((right != 0 || line != 4 || flag != 4)
                                 && (right != 0 || line != 3 || flag != 8)
                                 && (right != 1 || line != 4 || flag != 16)
                                 && (right != 1 || line != 3 || flag != 32)) {
                                 looser.put(key, meso);
                              } else {
                                 winner.put(key, (long)(meso * 3.6));
                                 ContentsManager.SpeedLadderGame.todayTotalBetWinning = (long)(
                                    ContentsManager.SpeedLadderGame.todayTotalBetWinning + meso * 3.6
                                 );
                              }
                        }
                     }

                     DecimalFormat decFormat = new DecimalFormat("###,###");
                     if (!winner.isEmpty()) {
                        List<String> keySetList = new ArrayList<>(winner.keySet());
                        Collections.sort(keySetList, (o1, o2) -> winner.get(o2).compareTo(winner.get(o1)));
                        int index = 0;

                        for (String key : keySetList) {
                           if (index > 2) {
                              break;
                           }

                           Center.Broadcast.broadcastMessageLadderGame(
                              CField.chatMsg(6, "당첨금액 Top." + (index + 1) + " " + key + "님 " + decFormat.format(winner.get(key)) + "메소 ")
                           );
                           index++;
                        }
                     }

                     if (!looser.isEmpty()) {
                        List<String> looserKeySetList = new ArrayList<>(looser.keySet());
                        Collections.sort(looserKeySetList, (o1, o2) -> looser.get(o2).compareTo(looser.get(o1)));
                        Center.Broadcast.broadcastMessageLadderGame(
                           CField.chatMsg(6, "손실금액 Top.1 " + looserKeySetList.get(0) + "님 " + decFormat.format(looser.get(looserKeySetList.get(0))) + "메소 ")
                        );
                     }

                     ContentsManager.SpeedLadderGame.betMan.clear();
                     ContentsManager.SpeedLadderGame.currentRound++;
                     ContentsManager.SpeedLadderGame.currentGameStartTime = System.currentTimeMillis();
                     if (ContentsManager.SpeedLadderGame.todayRound % 10 == 0) {
                        String dealer = "[현재 사다리 메소 현황]\r\n";
                        dealer = dealer + "오늘의 전체 베팅 금액 : " + decFormat.format(ContentsManager.SpeedLadderGame.todayTotalBet) + "\r\n";
                        dealer = dealer + "오늘의 전체 유저 수령 금액 : " + decFormat.format(ContentsManager.SpeedLadderGame.todayTotalBetWinning) + "\r\n";
                        dealer = dealer
                           + "서버입장 순메소수익 : "
                           + decFormat.format(ContentsManager.SpeedLadderGame.todayTotalBet - ContentsManager.SpeedLadderGame.todayTotalBetWinning)
                           + "메소";
                        if (DBConfig.isGanglim) {
                           DiscordBotHandler.requestSendTelegram(dealer);
                        } else {
                           DiscordBotHandler.requestSendTelegram(dealer, -460561418L);
                        }
                     }

                     ContentsManager.SpeedLadderGame.todayRound++;
                     if (ContentsManager.SpeedLadderGame.todayDay != new Date().getDay()) {
                        String dealer = "[★★오늘의 사다리 메소 정산★★]\r\n";
                        dealer = dealer + "오늘의 전체 베팅 금액 : " + decFormat.format(ContentsManager.SpeedLadderGame.todayTotalBet) + "\r\n";
                        dealer = dealer + "오늘의 전체 유저 수령 금액 : " + decFormat.format(ContentsManager.SpeedLadderGame.todayTotalBetWinning) + "\r\n";
                        dealer = dealer
                           + "서버입장 순메소수익 : "
                           + decFormat.format(ContentsManager.SpeedLadderGame.todayTotalBet - ContentsManager.SpeedLadderGame.todayTotalBetWinning)
                           + "메소";
                        ContentsManager.SpeedLadderGame.todayDay = new Date().getDay();
                        ContentsManager.SpeedLadderGame.todayRound = 1;
                        ContentsManager.SpeedLadderGame.todayTotalBet = 0L;
                        ContentsManager.SpeedLadderGame.todayTotalBetWinning = 0L;
                        if (DBConfig.isGanglim) {
                           DiscordBotHandler.requestSendTelegram(dealer);
                        } else {
                           DiscordBotHandler.requestSendTelegram("날짜가 지나 사다리 현황이 초기화 되었습니다.", -460561418L);
                        }
                     }
                  }
               },
               300000L,
               0L
            );
      }

      public static void addBetMan(MapleCharacter player, byte type, byte flag, long meso) {
         todayTotalBet += meso;
         betMan.put(player.getName(), new Triple<>(type, flag, meso));
      }

      public static String currentLadderGameScore() {
         DecimalFormat decFormat = new DecimalFormat("###,###");
         String dealer = "[현재 사다리 메소 정산]\r\n";
         dealer = dealer + "오늘의 라운드 수 : " + todayRound + "\r\n";
         dealer = dealer + "오늘의 전체 베팅 금액 : " + decFormat.format(todayTotalBet) + "\r\n";
         dealer = dealer + "오늘의 전체 유저 수령 금액 : " + decFormat.format(todayTotalBetWinning) + "\r\n";
         return dealer + "서버입장 순메소수익 : " + decFormat.format(todayTotalBet - todayTotalBetWinning) + "메소";
      }

      public static int getCurrentRound() {
         return currentRound;
      }

      public static List<SpeedLadder> getLadders() {
         return ladders;
      }

      public static long getCurrentGameStartTime() {
         return currentGameStartTime;
      }
   }
}
