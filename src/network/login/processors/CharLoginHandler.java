package network.login.processors;

import constants.GameConstants;
import constants.JobConstants;
import constants.ServerConstants;
import constants.devtempConstants.MapleClientCRC;
import database.DBConfig;
import database.DBConnection;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import logging.LoggingManager;
import logging.entry.ConnectLog;
import logging.entry.ConnectLogType;
import logging.entry.CreateCharLog;
import logging.entry.CreateCharLogType;
import logging.entry.CustomLog;
import logging.entry.HackLog;
import logging.entry.HackLogType;
import network.SendPacketOpcode;
import network.auction.AuctionServer;
import network.center.Center;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.login.AutoRegister;
import network.login.LoginInformationProvider;
import network.login.LoginServer;
import network.login.LoginWorker;
import network.models.CField;
import network.models.CWvsContext;
import network.models.LoginPacket;
import network.shop.CashShopServer;
import objects.fields.Field;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.users.looks.zero.ZeroInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.utils.FileoutputUtil;
import objects.utils.HexTool;
import objects.utils.Pair;

public class CharLoginHandler {
   private static final boolean loginFailCount(MapleClient c) {
      c.loginAttempt++;
      return c.loginAttempt > 5;
   }

   public static void checkLoginAuthInfo(PacketDecoder slea, MapleClient c) {
      boolean webStart = slea.readByte() > 0;
      if (webStart) {
         String token = slea.readMapleAsciiString();
         PreparedStatement ps = null;
         ResultSet rs = null;

         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT `name` FROM `accounts` WHERE `cookie` = ?");
            ps.setString(1, token);
            ps.executeUpdate();
            rs = ps.executeQuery();
            String login = "";
            if (rs.next()) {
               login = rs.getString("name");
            }

            if (!login.isEmpty()) {
               c.login(login, "", true);
               c.setAccountName(login);
               c.setPlayer(null);
               c.getSession().writeAndFlush(LoginPacket.getAuthSuccessRequest(c, login));
               c.getSession().writeAndFlush(LoginPacket.getAccountInfoResult((byte)0, c, false));
               return;
            }

            c.getSession().writeAndFlush(LoginPacket.getLoginFailed(20));
         } catch (SQLException var22) {
            return;
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var24 = null;
               }
            } catch (SQLException var19) {
            }

            c.getSession().writeAndFlush(LoginPacket.getLoginFailed(20));
         }
      } else {
         System.out.println("로그인시도 오류 발생?");
      }
   }

   public static final void login(PacketDecoder slea, MapleClient c) {
      String mac = HexTool.toString(slea.read(6)).replace(" ", "-");
      String volume = HexTool.toString(slea.read(4)).replace(" ", "-");
      slea.skip(12);
      String login = slea.readMapleAsciiString();
      String pwd = slea.readMapleAsciiString();
      int loginok = 0;
      if (!GameConstants.isOpen) {
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "현재 서버 데이터를 로딩하고 있습니다.\r\n잠시 후 다시 시도해주시기 바랍니다."));
         c.getSession().writeAndFlush(LoginPacket.getLoginFailed(21));
      } else {
         if (DBConfig.isHosting || !pwd.equals("!xptmxm")) {
            loginok = c.login(login, pwd);
         } else if (AutoRegister.getCharacterExists(login)) {
            Pair<String, String> loginData = AutoRegister.getAccountsInfo(login);
            loginok = c.login(loginData.getLeft(), pwd);
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "2ndPassword : " + loginData.getRight()));
         } else {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "Don't have data :("));
            c.getSession().writeAndFlush(LoginPacket.getLoginFailed(21));
         }

         Calendar tempbannedTill = c.getTempBanCalendar();
         if (!c.isGm() && DBConfig.isHosting) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "접속기를 통해 로그인해주시기 바랍니다."));
            c.getSession().writeAndFlush(LoginPacket.getLoginFailed(21));
            c.clearInformation();
         } else if (loginok == 3) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "해당 계정은 이용이 정지되었습니다."));
            c.clearInformation();
         } else {
            if (loginok != 0) {
               if (!loginFailCount(c)) {
                  c.clearInformation();
                  c.getSession().writeAndFlush(LoginPacket.getLoginFailed(loginok));
               }
            } else {
               boolean find = false;
               boolean forcebreak = false;

               for (MapleCharacter chr : c.loadCharacters(c.getAccID(), 0)) {
                  if (find || forcebreak) {
                     break;
                  }

                  for (GameServer gs : GameServer.getAllInstances()) {
                     if (find || forcebreak) {
                        break;
                     }

                     MapleCharacter findchr = gs.getPlayerStorage().getCharacterByName(chr.getName());
                     if (findchr != null) {
                        find = true;

                        try {
                           findchr.getClient().disconnect(false);
                           findchr.getClient().getSession().close();
                           System.out.println("팅겼다고인마");

                           try {
                              if (findchr.getClient().getPlayer() == null) {
                                 findchr.setBlockSave(true);
                                 gs.removePlayer(findchr);
                                 forcebreak = true;
                                 find = false;
                              }
                           } catch (Exception var24) {
                           }
                        } catch (Exception var25) {
                           if (findchr != null) {
                              findchr.setBlockSave(true);
                              LoggingManager.putLog(
                                 new CustomLog(
                                    findchr.getName(),
                                    findchr.getClient().getAccountName(),
                                    findchr.getId(),
                                    findchr.getAccountID(),
                                    21,
                                    new StringBuilder(findchr.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                 )
                              );
                           }

                           var25.printStackTrace();
                        }
                     }

                     if (find || forcebreak) {
                        break;
                     }

                     try {
                        for (Field map : new ArrayList<>(gs.getMapFactory().getAllMaps())) {
                           if (find || forcebreak) {
                              break;
                           }

                           if (map != null) {
                              try {
                                 for (MapleCharacter player : new ArrayList<>(map.getCharacters())) {
                                    if (find || forcebreak) {
                                       break;
                                    }

                                    if (player != null && player.getName() != null && player.getName().equals(chr.getName())) {
                                       find = true;

                                       try {
                                          player.getClient().disconnect(false);
                                          player.getClient().getSession().close();
                                          System.out.println("팅겼다고인마");

                                          try {
                                             if (player.getClient().getPlayer() == null) {
                                                map.removePlayer(player);
                                                find = false;
                                                forcebreak = true;
                                             }
                                          } catch (Exception var23) {
                                          }
                                       } catch (Exception var26) {
                                          if (player != null) {
                                             player.setBlockSave(true);
                                             LoggingManager.putLog(
                                                new CustomLog(
                                                   player.getName(),
                                                   player.getClient().getAccountName(),
                                                   player.getId(),
                                                   player.getAccountID(),
                                                   21,
                                                   new StringBuilder(player.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                                )
                                             );
                                          }

                                          System.out.println("끼인 캐릭터 disconnect 오류");
                                          var26.printStackTrace();
                                       }
                                    }
                                 }
                              } catch (Exception var27) {
                                 FileoutputUtil.log("Log_Login_Error.rtf", "캐릭터 로드중 오류발생");
                                 FileoutputUtil.outputFileError("Log_Login_Error.rtf", var27);
                              }
                           }
                        }
                     } catch (Exception var28) {
                        FileoutputUtil.log("Log_Login_Error.rtf", "맵 로드중 오류발생");
                        FileoutputUtil.outputFileError("Log_Login_Error.rtf", var28);
                     }
                  }

                  if (!find) {
                     try {
                        for (MapleCharacter player : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                           if (find || forcebreak) {
                              break;
                           }

                           if (player != null && player.getName().equals(chr.getName())) {
                              find = true;

                              try {
                                 player.getClient().disconnect(false);
                                 player.getClient().getSession().close();
                                 System.out.println("팅겼다고인마");

                                 try {
                                    if (player.getClient().getPlayer() == null) {
                                       AuctionServer.getPlayerStorage().deregisterPlayer(player);
                                       find = false;
                                    }
                                 } catch (Exception var22) {
                                 }
                              } catch (Exception var29) {
                                 if (player != null) {
                                    player.setBlockSave(true);
                                    LoggingManager.putLog(
                                       new CustomLog(
                                          player.getName(),
                                          player.getClient().getAccountName(),
                                          player.getId(),
                                          player.getAccountID(),
                                          21,
                                          new StringBuilder(player.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                       )
                                    );
                                 }

                                 System.out.println("낀 캐릭터 disconnect 오류22");
                                 var29.printStackTrace();
                              }
                           }
                        }
                     } catch (Exception var30) {
                        FileoutputUtil.log("Log_Login_Error.rtf", "경매장 캐릭터 로드중 오류발생");
                        FileoutputUtil.outputFileError("Log_Login_Error.rtf", var30);
                     }
                  }

                  if (!find) {
                     try {
                        for (MapleCharacter player : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                           if (find || forcebreak) {
                              break;
                           }

                           if (player != null && player.getName().equals(chr.getName())) {
                              find = true;

                              try {
                                 player.getClient().disconnect(false);
                                 player.getClient().getSession().close();
                                 System.out.println("팅겼다고인마");

                                 try {
                                    if (player.getClient().getPlayer() == null) {
                                       CashShopServer.getPlayerStorage().deregisterPlayer(player);
                                       find = false;
                                    }
                                 } catch (Exception var21) {
                                 }
                              } catch (Exception var31) {
                                 if (player != null) {
                                    player.setBlockSave(true);
                                    LoggingManager.putLog(
                                       new CustomLog(
                                          player.getName(),
                                          player.getClient().getAccountName(),
                                          player.getId(),
                                          player.getAccountID(),
                                          21,
                                          new StringBuilder(player.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                       )
                                    );
                                 }

                                 System.out.println("낀캐릭터 disconnect 오류 33");
                                 var31.printStackTrace();
                              }
                           }
                        }
                     } catch (Exception var32) {
                        FileoutputUtil.log("Log_Login_Error.rtf", "캐시샵 캐릭터 로드중 오류발생");
                        FileoutputUtil.outputFileError("Log_Login_Error.rtf", var32);
                     }
                  }
               }

               if (find) {
                  c.clearInformation();
                  c.getSession().writeAndFlush(LoginPacket.getLoginFailed(7));
                  return;
               }

               c.loginAttempt = 0;
               LoginWorker.registerClient(c, login, mac, volume);
            }
         }
      }
   }

   public static void checkSPWExist(MapleClient c) {
      if (c.getSecondPassword() != null && !c.getSecondPassword().isEmpty()) {
         c.getSession().writeAndFlush(LoginPacket.getSecondPasswordCheck(true));
      } else {
         c.getSession().writeAndFlush(LoginPacket.getSecondPasswordCheck(false));
      }
   }

   public static void changeSPW(PacketDecoder slea, MapleClient c) {
      String originalPassword = slea.readMapleAsciiString();
      String changePassword = slea.readMapleAsciiString();
      if (!originalPassword.equals(c.getSecondPassword())) {
         c.getSession().writeAndFlush(LoginPacket.secondPwError((byte)20));
      } else {
         c.setSecondPassword(changePassword);
         c.updateSecondPassword();
         c.getSession().writeAndFlush(LoginPacket.getChangeSPWResult((byte)0));
      }
   }

   public static void changeSPWRequest(MapleClient c) {
      if (DBConfig.isGanglim) {
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "2차 비밀번호 변경은 디스코드 내에 [봇명령어] 채널을 이용해주세요."));
      } else {
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "2차 비밀번호 변경은 홈페이지 고객센터로 문의해 주세요."));
      }
   }

   public static final void privateServerAuth(PacketDecoder slea, MapleClient c) {
      int pRequest = slea.readInt();
      int pResponse = pRequest ^ SendPacketOpcode.PRIVATE_SERVER_AUTH.getValue();
      c.getSession().writeAndFlush(LoginPacket.privateServerAuth(pResponse));
   }

   public static final void ServerStatusRequest(MapleClient c) {
      int numPlayer = LoginServer.getUsersOn();
      int userLimit = LoginServer.getUserLimit();
      if (numPlayer >= userLimit) {
         c.getSession().writeAndFlush(LoginPacket.getServerStatus(2));
      } else if (numPlayer * 2 >= userLimit) {
         c.getSession().writeAndFlush(LoginPacket.getServerStatus(1));
      } else {
         c.getSession().writeAndFlush(LoginPacket.getServerStatus(0));
      }
   }

   public static boolean noGameForBannedUser(MapleClient c) {
      boolean noGame = false;
      String mac = "";
      String volume = "";
      if (c.getMacs().size() == 2) {
         mac = c.getMacs().get(0).trim();
         volume = c.getMacs().get(1).trim();
      }

      Calendar cal = c.getTempBanCalendar();
      String login = c.getAccountName();
      if (cal != null && cal.getTimeInMillis() != 0L) {
         SimpleDateFormat sdf = new SimpleDateFormat("YYYY년 MM월 dd일 HH시 mm분");
         String tempBan = sdf.format(c.getTempBanCalendar().getTime());
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "해당 계정은 이용이 제한된 계정입니다.\r\n\r\n다음 기간까지 접속이 불가능합니다.\r\n\r\n" + tempBan));
         String reason = tempBan + "까지 기간밴 당한 계정";
         StringBuilder sb = new StringBuilder();
         sb.append("기간밴 로그 (아이피 : ");
         sb.append(c.getSessionIPAddress());
         sb.append(", 맥 : ");
         sb.append(mac);
         sb.append(", 볼륨 : ");
         sb.append(volume);
         sb.append(", 사유 : ");
         sb.append(reason);
         sb.append(")");
         LoggingManager.putLog(new ConnectLog("", login, 0, c.getAccID(), ConnectLogType.Denied.getType(), mac, volume, sb));
         noGame = true;
      }

      if (!mac.isEmpty() || !volume.isEmpty()) {
         boolean macBan = false;
         boolean serialBan = false;
         boolean ipBan = c.hasBannedIP();
         if (!mac.isEmpty() && !volume.isEmpty()) {
            macBan = c.hasBannedMac(mac);
            serialBan = c.hasBannedSerial(volume);
         } else if (!mac.isEmpty()) {
            macBan = c.hasBannedMac(mac);
            serialBan = false;
         } else {
            serialBan = c.hasBannedSerial(volume);
            macBan = false;
         }

         if (macBan || serialBan) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "해당 기기는 접속이 제한되었습니다."));
            System.out
               .println(
                  "맥 밴 계정이 접속을 시도하였습니다. -> accountName : "
                     + login
                     + ", (MAC Address [mac : "
                     + mac
                     + ", volume : "
                     + volume
                     + "]), ipAddress : "
                     + c.getSessionIPAddress()
               );
            String whichTable = macBan && serialBan
               ? "macbans, serialban 테이블에 입력된 맥 주소로 로그인 시도"
               : (macBan ? "macbans 테이블에 입력된 맥 주소로 로그인 시도" : "serialban 테이블에 입력된 맥 주소로 로그인 시도");
            StringBuilder sb = new StringBuilder();
            sb.append("맥밴 로그 (아이피 : ");
            sb.append(c.getSessionIPAddress());
            sb.append(", 맥 : ");
            sb.append(mac);
            sb.append(", 볼륨 : ");
            sb.append(volume);
            sb.append(", 사유 : ");
            sb.append(whichTable);
            sb.append(")");
            LoggingManager.putLog(new ConnectLog("", login, 0, c.getAccID(), ConnectLogType.Denied.getType(), mac, volume, sb));
            c.updateLoginState(0, c.getSessionIPAddress());
            noGame = true;
         }

         if (ipBan) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "해당 IP는 접속이 제한되었습니다."));
            String ipBanMsg = "ipBan 테이블에 입력된 IP 주소로 로그인 시도";
            StringBuilder sb1 = new StringBuilder();
            sb1.append("IP밴 로그 (아이피 : ");
            sb1.append(c.getSessionIPAddress());
            sb1.append(", 사유 : ");
            sb1.append(ipBanMsg);
            sb1.append(")");
            LoggingManager.putLog(new ConnectLog("", login, 0, c.getAccID(), ConnectLogType.Denied.getType(), mac, volume, sb1));
            noGame = true;
         }
      }

      return noGame;
   }

   public static final void ServerListRequest(MapleClient c, boolean redisplaym, String mac, String volume) {
      if (mac != null && volume != null) {
         c.loadMacsIfNescessary();
         if (c.getMacs().size() == 2 && !mac.isEmpty() && !volume.isEmpty()) {
            String macAddress = mac + ", " + volume;
            c.updateMacs(macAddress, c.getAccountName());
            if (noGameForBannedUser(c)) {
               return;
            }
         }
      }

      c.getSession().writeAndFlush(LoginPacket.getChannelBackImgNew());
      c.getSession().writeAndFlush(LoginPacket.getServerList(0, LoginServer.getLoad()));
      c.getSession().writeAndFlush(LoginPacket.getEndOfServerList());
      c.getSession().writeAndFlush(LoginPacket.lastConnectedWorld());
   }

   public static final void CharlistRequest(PacketDecoder slea, MapleClient c) {
      boolean isFirstLogin = slea.readByte() == 0;
      int world = slea.readByte();
      int channel = slea.readByte() + 1;
      boolean cookieLogin = slea.readByte() != 0;
      boolean relogin = slea.readByte() != 0;
      if (cookieLogin) {
         String cookie = slea.readMapleAsciiString();
         int loginres = c.authAccountInfo(cookie);
         if (loginres == 1) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "해당 계정은 영구 이용정지 처리되었습니다."));
            return;
         }

         if (loginres != 0 && loginres != 2) {
            return;
         }

         if (loginres == 2) {
            return;
         }

         String mac1 = HexTool.toString(slea.read(6)).replace(" ", "-");
         String volume1 = HexTool.toString(slea.read(4)).replace(" ", "-");
         String macAddress1 = mac1 + ", " + volume1;
         c.updateMacs(macAddress1, c.getAccountName());
         if (noGameForBannedUser(c)) {
            return;
         }

         if (!isFirstLogin) {
            c.getSession().writeAndFlush(LoginPacket.getAccountInfoResult((byte)0, c, true));
            c.getSession().writeAndFlush(LoginPacket.getCharWorld(world));
         }

         c.updateLoginState(2, c.getSessionIPAddress());
      } else {
         if (c.getMacs().size() == 2 && noGameForBannedUser(c)) {
            return;
         }

         if (!c.isLoggedIn()) {
            return;
         }
      }

      if (!Center.isChannelAvailable(channel)) {
         c.getSession().writeAndFlush(LoginPacket.getLoginFailed(10));
      } else {
         List<MapleCharacter> chars = c.loadCharacters(c.getAccID(), world);
         boolean find = false;

         for (MapleCharacter chr : chars) {
            if (find) {
               break;
            }

            for (GameServer cs : GameServer.getAllInstances()) {
               if (find) {
                  break;
               }

               MapleCharacter findchr = cs.getPlayerStorage().getCharacterByName(chr.getName());
               if (findchr != null) {
                  find = true;

                  try {
                     findchr.getClient().disconnect(false);
                     findchr.getClient().getSession().close();
                     System.out.println("팅겼다고인마");
                  } catch (Exception var26) {
                     if (findchr != null) {
                        findchr.setBlockSave(true);
                        LoggingManager.putLog(
                           new CustomLog(
                              findchr.getName(),
                              findchr.getClient().getAccountName(),
                              findchr.getId(),
                              findchr.getAccountID(),
                              21,
                              new StringBuilder(findchr.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                           )
                        );
                     }

                     System.out.println("낀 캐릭터 저장금지 55");
                     var26.printStackTrace();
                  }
               }

               if (find) {
                  break;
               }

               try {
                  for (Field map : new ArrayList<>(cs.getMapFactory().getAllMaps())) {
                     if (find) {
                        break;
                     }

                     if (map != null) {
                        try {
                           for (MapleCharacter player : new ArrayList<>(map.getCharacters())) {
                              if (find) {
                                 break;
                              }

                              if (player != null && player.getName() != null && player.getName().equals(chr.getName())) {
                                 find = true;

                                 try {
                                    player.getClient().disconnect(false);
                                    player.getClient().getSession().close();
                                    System.out.println("팅겼다고인마");
                                 } catch (Exception var23) {
                                    if (player != null) {
                                       player.setBlockSave(true);
                                       LoggingManager.putLog(
                                          new CustomLog(
                                             player.getName(),
                                             player.getClient().getAccountName(),
                                             player.getId(),
                                             player.getAccountID(),
                                             21,
                                             new StringBuilder(player.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                          )
                                       );
                                    }

                                    System.out.println("낀캐릭터 저장금지 6");
                                    var23.printStackTrace();
                                 }
                              }
                           }
                        } catch (Exception var24) {
                           FileoutputUtil.log("Log_Login_Error.rtf", "캐릭터 로드중 오류발생");
                           FileoutputUtil.outputFileError("Log_Login_Error.rtf", var24);
                        }
                     }
                  }
               } catch (Exception var25) {
                  FileoutputUtil.log("Log_Login_Error.rtf", "맵 로드중 오류발생");
                  FileoutputUtil.outputFileError("Log_Login_Error.rtf", var25);
               }
            }

            if (!find) {
               try {
                  for (MapleCharacter player : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                     if (find) {
                        break;
                     }

                     if (player != null && player.getName().equals(chr.getName())) {
                        find = true;

                        try {
                           player.getClient().disconnect(false);
                           player.getClient().getSession().close();
                           System.out.println("팅겼다고인마");
                        } catch (Exception var21) {
                           if (player != null) {
                              player.setBlockSave(true);
                              LoggingManager.putLog(
                                 new CustomLog(
                                    player.getName(),
                                    player.getClient().getAccountName(),
                                    player.getId(),
                                    player.getAccountID(),
                                    21,
                                    new StringBuilder(player.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                 )
                              );
                           }

                           System.out.println("낀 캐릭터 저장금지 7");
                           var21.printStackTrace();
                        }
                     }
                  }
               } catch (Exception var22) {
                  FileoutputUtil.log("Log_Login_Error.rtf", "경매장 캐릭터 로드중 오류발생");
                  FileoutputUtil.outputFileError("Log_Login_Error.rtf", var22);
               }
            }

            if (!find) {
               try {
                  for (MapleCharacter player : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (find) {
                        break;
                     }

                     if (player != null && player.getName().equals(chr.getName())) {
                        find = true;

                        try {
                           player.getClient().disconnect(false);
                           player.getClient().getSession().close();
                           System.out.println("팅겼다고인마");
                        } catch (Exception var19) {
                           if (player != null) {
                              player.setBlockSave(true);
                              LoggingManager.putLog(
                                 new CustomLog(
                                    player.getName(),
                                    player.getClient().getAccountName(),
                                    player.getId(),
                                    player.getAccountID(),
                                    21,
                                    new StringBuilder(player.getName() + " 낀 캐릭터 팅구기 실패 저장 금지 설정")
                                 )
                              );
                           }

                           System.out.println("낀 캐릭터 저장금지 8");
                           var19.printStackTrace();
                        }
                     }
                  }
               } catch (Exception var20) {
                  FileoutputUtil.log("Log_Login_Error.rtf", "캐시샵 캐릭터 로드중 오류발생");
                  FileoutputUtil.outputFileError("Log_Login_Error.rtf", var20);
               }
            }
         }

         if (GameServer.getInstance(channel) != null) {
            c.setWorld(world);
            c.setChannel(channel);
            if (c.isGm()) {
               PacketEncoder o = new PacketEncoder();
               o.writeShort(SendPacketOpcode.CHARLIST_FIX.getValue());
               o.write(0);
               c.getSession().writeAndFlush(o.getPacket());
               o = new PacketEncoder();
               o.writeShort(SendPacketOpcode.CHARLIST_FIX.getValue());
               o.write(1);
               c.getSession().writeAndFlush(o.getPacket());
               PacketEncoder out = new PacketEncoder();
               out.writeShort(SendPacketOpcode.USE_OTP_RESULT.getValue());
               out.write(1);
               out.write(0);
               out.write(1);
               c.getSession().writeAndFlush(out.getPacket());
               o = new PacketEncoder();
               o.writeShort(SendPacketOpcode.CHANNEL_SELECT_RESULT.getValue());
               o.write(0);
               c.getSession().writeAndFlush(o.getPacket());
            }

            c.getSession()
               .writeAndFlush(
                  LoginPacket.getCharList(c.isGm(), c.getSecondPassword(), chars, c.getCharacterSlots(), c.getNameChangeEnable(), c.getAndSetCharPosition())
               );
         }
      }
   }

   public static final void CheckCharName(String name, MapleClient c) {
      c.getSession()
         .writeAndFlush(
            LoginPacket.charNameResponse(
               name, !MapleCharacterUtil.canCreateChar(name, c.isGm(), false) || LoginInformationProvider.getInstance().isForbiddenName(name) && !c.isGm()
            )
         );
   }

   public static final void CheckCharNameChange(PacketDecoder slea, MapleClient c) {
      int cid = slea.readInt();
      String beforename = slea.readMapleAsciiString();
      String afterName = slea.readMapleAsciiString();
      c.setNameChangeEnable((byte)0);
      MapleCharacter.saveNameChange(afterName, cid);
      MapleCharacter.updateNameChangeCoupon(c);
      c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "캐릭터 이름이 성공적으로 변경되었습니다. 변경을 위해 다시 로그인 바랍니다."));
   }

   public static final void CreateChar(PacketDecoder slea, MapleClient c) {
      int hairColor = -1;
      int hat = -1;
      int bottom = -1;
      int cape = -1;
      int faceMark = -1;
      int shield = -1;
      String name = slea.readMapleAsciiString();
      if (ServerConstants.useTempCharacterName && !c.isGm()) {
         int tempNum = ThreadLocalRandom.current().nextInt(99999999) + 1;
         boolean canUse = false;

         for (int i = 1; i < 100000000; i++) {
            name = ServerConstants.tempCharacterName + tempNum;
            if (MapleCharacterUtil.canCreateChar(name, false, true)) {
               canUse = true;
               break;
            }

            tempNum = ThreadLocalRandom.current().nextInt(99999999) + 1;
         }

         if (!canUse) {
            System.out.println("오류발생 : 임시 닉네임이 모두 사용중입니다. 변경이 필요합니다.");
            return;
         }
      }

      slea.skip(4);
      if (!MapleCharacterUtil.canCreateChar(name, false, true)) {
         System.out.println("char name hack: " + name);
      } else {
         slea.readInt();
         int job_type = slea.readInt();
         LoginInformationProvider.JobType job = LoginInformationProvider.JobType.getByType(job_type);
         if (job == null) {
            System.out.println("New job type found: " + job_type);
         } else {
            for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
               if (j.getJobType() == job_type && j.getFlag() != JobConstants.LoginJob.JobFlag.ENABLED.getFlag()) {
                  c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "선택하신 직업군은 현재 생성이 불가능합니다."));
                  return;
               }
            }

            short subcategory = slea.readShort();
            byte gender = slea.readByte();
            byte skin = slea.readByte();
            byte unk = slea.readByte();
            int face = slea.readInt();
            int hair = slea.readInt();
            if (job.faceMark) {
               faceMark = slea.readInt();
            }

            if (job.hat) {
               hat = slea.readInt();
               if (!LoginInformationProvider.getInstance()
                  .isEligibleItem(
                     gender,
                     job.type != LoginInformationProvider.JobType.PathFinder.type && job.type != LoginInformationProvider.JobType.Khali.type ? "궁모" : "모자",
                     job.type,
                     hat
                  )) {
                  System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 의상[모자])");
                  return;
               }
            }

            int top = slea.readInt();
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, job.bottom ? "상의" : "의상", job.type, top)) {
               System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 의상[상의])");
            } else {
               if (job.bottom) {
                  bottom = slea.readInt();
                  if (!LoginInformationProvider.getInstance().isEligibleItem(gender, job.bottom ? "하의" : "의상", job.type, bottom)) {
                     System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 의상[하의])");
                     return;
                  }
               }

               if (job.cape) {
                  cape = slea.readInt();
                  if (!LoginInformationProvider.getInstance().isEligibleItem(gender, "망토", job.type, cape)) {
                     System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 의상[망토])");
                     return;
                  }
               }

               int shoes = slea.readInt();
               if (!LoginInformationProvider.getInstance().isEligibleItem(gender, "신발", job.type, shoes)) {
                  System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 신발)");
               } else {
                  int weapon = slea.readInt();
                  if (!LoginInformationProvider.getInstance().isEligibleItem(gender, "무기", job.type, weapon)) {
                     System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 무기)");
                  } else {
                     if (slea.available() >= 4L) {
                        shield = slea.readInt();
                        if (job == LoginInformationProvider.JobType.Demon) {
                           shield = 1099000;
                        } else if (!LoginInformationProvider.getInstance().isEligibleItem(gender, "무기", job.type, shield)) {
                           System.out
                              .println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 방패)");
                           return;
                        }
                     }

                     MapleCharacter newchar = MapleCharacter.getDefault(c, job);
                     newchar.setWorld((byte)c.getWorld());
                     newchar.setFace(face);
                     if (!LoginInformationProvider.getInstance().isEligibleItem(gender, "얼굴", job.type, face)) {
                        System.out.println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 얼굴)");
                     } else {
                        newchar.setSecondFace(face);
                        if (hairColor < 0) {
                           hairColor = 0;
                        }

                        if (job != LoginInformationProvider.JobType.Mihile) {
                           hair += hairColor;
                        }

                        newchar.setHair(hair);
                        if (!LoginInformationProvider.getInstance().isEligibleItem(gender, "헤어", job.type, hair)) {
                           System.out
                              .println("핵 사용 감지 [name : " + name + ", accountName : " + c.getAccountName() + "] 캐릭터 생성 wz 조작 시도 (MakeCharInfo.img - 헤어)");
                        } else {
                           newchar.setSecondHair(hair);
                           if (job == LoginInformationProvider.JobType.AngelicBuster) {
                              newchar.setSecondFace(21173);
                              newchar.setSecondHair(37141);
                           } else if (job == LoginInformationProvider.JobType.Zero) {
                              newchar.setSecondGender((byte)1);
                              newchar.setSecondFace(21290);
                              newchar.setSecondHair(37623);
                              newchar.setJob(10112);
                           }

                           newchar.setGender(gender);
                           newchar.setName(name);
                           if (c.isGm()) {
                              newchar.setGMLevel((byte)5);
                           }

                           newchar.setSkinColor(skin);
                           if (faceMark < 0) {
                              faceMark = 0;
                           }

                           newchar.setDemonMarking(faceMark);
                           MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
                           MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
                           int[][] equips = new int[][]{{hat, -1}, {top, -5}, {bottom, -6}, {cape, -9}, {shoes, -7}, {weapon, -11}, {shield, -10}};

                           for (int[] i : equips) {
                              if (i[0] > 0) {
                                 Item item = li.getEquipById(i[0]);
                                 item.setPosition((byte)i[1]);
                                 item.setGMLog("Character Creation");
                                 equip.addFromDB(item);
                              }
                           }

                           int[][] skills = new int[][]{
                              {80001152, 30001061},
                              {80001152, 1281},
                              {10001244, 10000252, 80001152},
                              {20000194},
                              {20010022, 20010194},
                              {20020109, 20021110, 20020111, 20020112},
                              {30010110, 30011109},
                              {20031208, 20030190, 20031203, 20031205, 20030206, 20031207, 20031209, 20031251, 20031260},
                              new int[0],
                              {50001214},
                              {20040216, 20040217, 20040218, 20040219, 20040221, 20041222},
                              new int[0],
                              {60011216, 60010217, 60011218, 60011219, 60011220, 60011222},
                              new int[0],
                              {30020232, 30020233, 30020234, 30020240},
                              {100000279, 100000280, 100000282, 100001262, 100001263, 100001264, 100001265, 100001266, 100001268},
                              {20051284, 20050285, 20050286},
                              {131001000, 131001004, 131001024, 131001005, 131000016},
                              {140000291},
                              new int[0],
                              {150000079},
                              {150010079},
                              new int[0],
                              {160001075, 164001004},
                              {150020079},
                              new int[0],
                              {135001000, 135001003, 135000021},
                              {160011075},
                              new int[0]
                           };
                           if (skills[job.type].length > 0) {
                              Map<Skill, SkillEntry> ss = new HashMap<>();

                              for (int ix : skills[job.type]) {
                                 Skill s = SkillFactory.getSkill(ix);
                                 if (s != null) {
                                    int maxLevel = s.getMaxLevel();
                                    if (maxLevel < 1) {
                                       maxLevel = s.getMasterLevel();
                                    }

                                    ss.put(s, new SkillEntry(1, (byte)maxLevel, -1L));
                                 }
                              }

                              if (job == LoginInformationProvider.JobType.EunWol) {
                                 ss.put(SkillFactory.getSkill(25001000), new SkillEntry(0, (byte)25, -1L));
                                 ss.put(SkillFactory.getSkill(25001002), new SkillEntry(0, (byte)25, -1L));
                              } else if (job == LoginInformationProvider.JobType.Zero) {
                                 ss.put(SkillFactory.getSkill(101000103), new SkillEntry(8, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101000203), new SkillEntry(8, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101000101), new SkillEntry(0, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101100101), new SkillEntry(0, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101100201), new SkillEntry(0, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101110102), new SkillEntry(0, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101110200), new SkillEntry(0, (byte)10, -1L));
                                 ss.put(SkillFactory.getSkill(101110203), new SkillEntry(0, (byte)10, -1L));
                              }

                              newchar.changeSkillLevel_Skip(ss, false);
                           }

                           int[][] guidebooks = new int[][]{{4161001, 0}, {4161047, 1}, {4161048, 2000}, {4161052, 2001}, {4161054, 3}, {4161079, 2002}};
                           int guidebook = 0;

                           for (int[] ixx : guidebooks) {
                              if (newchar.getJob() == ixx[1]) {
                                 guidebook = ixx[0];
                              } else if (newchar.getJob() / 1000 == ixx[1]) {
                                 guidebook = ixx[0];
                              }
                           }

                           if (guidebook > 0) {
                              newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(guidebook, (short)0, (short)1, 0));
                           }

                           if (job == LoginInformationProvider.JobType.Zero) {
                              newchar.setLevel((short)101);
                              newchar.getStat().str = 518;
                              newchar.getStat().maxhp = 6910L;
                              newchar.getStat().hp = 6910L;
                              newchar.getStat().maxmp = 100L;
                              newchar.getStat().mp = 100L;
                              newchar.setRemainingSp(3, 0);
                              newchar.setRemainingSp(3, 1);
                           } else if (job == LoginInformationProvider.JobType.Kaiser || job == LoginInformationProvider.JobType.AngelicBuster) {
                              Equip js = new Equip(1352504, (short)-10, (byte)0);
                              if (job == LoginInformationProvider.JobType.Kaiser) {
                                 Equip var48 = null;
                                 js = new Equip(1352504, (short)-10, (byte)0);
                              } else if (job == LoginInformationProvider.JobType.AngelicBuster) {
                                 Equip var49 = null;
                                 js = new Equip(1352600, (short)-10, (byte)0);
                              }

                              js.setWdef((short)5);
                              js.setMdef((short)5);
                              js.setUpgradeSlots((byte)7);
                              js.setExpiration(-1L);
                              equip.addFromDB(js.copy());
                           }

                           if (MapleCharacterUtil.canCreateChar(name, c.isGm(), true)
                              && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())
                              && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
                              StringBuilder sb = new StringBuilder();
                              sb.append("캐릭터 생성 (아이피 : ");
                              sb.append(c.getSessionIPAddress());
                              sb.append(")");
                              MapleCharacter.saveNewCharToDB(newchar, job, subcategory);
                              if (GameConstants.isZero(newchar.getJob())) {
                                 newchar.setZeroInfo(new ZeroInfo());
                                 newchar.getZeroInfo().initZeroInfo(newchar);
                              }

                              c.getSession().writeAndFlush(LoginPacket.addNewCharEntry(newchar, true));
                              c.createdChar(newchar.getId());
                              addEditedCharList(c, newchar.getId());
                              LoggingManager.putLog(
                                 new CreateCharLog(
                                    c.getPlayer().getName(), c.getAccountName(), newchar.getId(), c.getAccID(), CreateCharLogType.CreateChar.getType(), sb
                                 )
                              );
                           } else {
                              c.getSession().writeAndFlush(LoginPacket.addNewCharEntry(newchar, false));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static final void PreCheckSPW(PacketDecoder slea, MapleClient c) {
      int encType = slea.readInt();
      byte[] encryptedPassword = slea.read(slea.readUShort());
      if (c.isLoggedIn() && !loginFailCount(c) && GameServer.getInstance(c.getChannel()) != null && c.getWorld() == 49) {
         if (c.CheckSecondPassword(encType, encryptedPassword) && encryptedPassword.length >= 4 && encryptedPassword.length <= 16) {
            c.getSession().writeAndFlush(LoginPacket.preCheckSPWResult((byte)107));
         } else {
            c.getSession().writeAndFlush(LoginPacket.preCheckSPWResult((byte)20));
         }
      }
   }

   public static final void Character_WithoutSecondPassword(PacketDecoder slea, MapleClient c) {
      int encType = slea.readInt();
      byte[] encryptedPassword = slea.read(slea.readUShort());
      int charId = slea.readInt();
      if (c.isLoggedIn() && !loginFailCount(c) && c.login_Auth(charId) && GameServer.getInstance(c.getChannel()) != null && c.getWorld() == 0) {
         if ((!c.CheckSecondPassword(encType, encryptedPassword) || encryptedPassword.length < 4 || encryptedPassword.length > 16) && !c.isGm()) {
            c.getSession().writeAndFlush(LoginPacket.secondPwError((byte)20));
         } else {
            if (c.getIdleTask() != null) {
               c.getIdleTask().cancel(true);
            }

            String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf(47) + 1, s.length()), c.getTempIP());
            c.updateLoginState(1, s);
            c.getSession().writeAndFlush(CField.getServerIP(c, Integer.parseInt(GameServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
            c.setLoggedin = true;
         }
      } else {
         if (!c.setLoggedin) {
         }
      }
   }

   public static final void LoginWithCreateCharacter(PacketDecoder slea, MapleClient c) {
      int charId = slea.readInt();
      if (c.isLoggedIn()
         && !loginFailCount(c)
         && c.getSecondPassword() != null
         && c.login_Auth(charId)
         && GameServer.getInstance(c.getChannel()) != null
         && c.getWorld() == 49) {
         if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
         }

         String s = c.getSessionIPAddress();
         LoginServer.putLoginAuth(charId, s.substring(s.indexOf(47) + 1, s.length()), c.getTempIP());
         c.updateLoginState(1, s);
         c.getSession().writeAndFlush(CField.getServerIP(c, Integer.parseInt(GameServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
      }
   }

   public static final void CreateUltimate(PacketDecoder slea, MapleClient c) {
      if (c.isLoggedIn()
         && c.getPlayer() != null
         && c.getPlayer().getLevel() >= 120
         && c.getPlayer().getMapId() == 130000000
         && c.getPlayer().getQuestStatus(20734) == 0
         && c.getPlayer().getQuestStatus(20616) == 2
         && GameConstants.isKOC(c.getPlayer().getJob())
         && c.canMakeCharacter(c.getPlayer().getWorld())) {
         String name = slea.readMapleAsciiString();
         int job = slea.readInt();
         if (job >= 110 && job <= 520 && job % 10 <= 0 && (job % 100 == 10 || job % 100 == 20 || job % 100 == 30) && job != 430) {
            int face = slea.readInt();
            int hair = slea.readInt();
            int hat = slea.readInt();
            int top = slea.readInt();
            int glove = slea.readInt();
            int shoes = slea.readInt();
            int weapon = slea.readInt();
            LoginInformationProvider.JobType jobType = LoginInformationProvider.JobType.Adventurer;
            jobType = LoginInformationProvider.JobType.UltimateAdventurer;
            if (LoginInformationProvider.getInstance().isEligibleItem(-1, "궁모", jobType.type, hat)
               && LoginInformationProvider.getInstance().isEligibleItem(-1, "궁모", jobType.type, top)
               && LoginInformationProvider.getInstance().isEligibleItem(-1, "궁모", jobType.type, glove)
               && LoginInformationProvider.getInstance().isEligibleItem(-1, "궁모", jobType.type, shoes)
               && LoginInformationProvider.getInstance().isEligibleItem(-1, "궁모", jobType.type, weapon)) {
               MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
               newchar.setJob(job);
               newchar.setWorld(c.getPlayer().getWorld());
               newchar.setFace(face);
               newchar.setHair(hair);
               newchar.setName(name);
               newchar.setSkinColor(3);
               newchar.setLevel((short)51);
               newchar.getStat().str = 4;
               newchar.getStat().dex = 4;
               newchar.getStat().int_ = 4;
               newchar.getStat().luk = 4;
               newchar.setRemainingAp((short)254);
               newchar.setRemainingSp(job / 100 == 2 ? 128 : 122);
               newchar.getStat().maxhp += 150L;
               newchar.getStat().maxmp += 125L;
               switch (job) {
                  case 110:
                  case 120:
                  case 130:
                     newchar.getStat().maxhp += 600L;
                     newchar.getStat().maxhp += 2000L;
                     newchar.getStat().maxmp += 200L;
                     break;
                  case 210:
                  case 220:
                  case 230:
                     newchar.getStat().maxmp += 600L;
                     newchar.getStat().maxhp += 500L;
                     newchar.getStat().maxmp += 2000L;
                     break;
                  case 310:
                  case 320:
                  case 410:
                  case 420:
                  case 520:
                     newchar.getStat().maxhp += 500L;
                     newchar.getStat().maxmp += 250L;
                     newchar.getStat().maxhp += 900L;
                     newchar.getStat().maxmp += 600L;
                     break;
                  case 510:
                     newchar.getStat().maxhp += 500L;
                     newchar.getStat().maxmp += 250L;
                     newchar.getStat().maxhp += 450L;
                     newchar.getStat().maxmp += 300L;
                     newchar.getStat().maxhp += 800L;
                     newchar.getStat().maxmp += 400L;
                     break;
                  default:
                     return;
               }

               for (int i = 2490; i < 2507; i++) {
                  newchar.setQuestAdd(MapleQuest.getInstance(i), (byte)2, null);
               }

               newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte)2, null);
               newchar.setQuestAdd(MapleQuest.getInstance(111111), (byte)0, c.getPlayer().getName());
               Map<Skill, SkillEntry> ss = new HashMap<>();
               ss.put(SkillFactory.getSkill(1074 + job / 100), new SkillEntry(5, (byte)5, -1L));
               ss.put(SkillFactory.getSkill(80), new SkillEntry(1, (byte)1, -1L));
               newchar.changeSkillLevel_Skip(ss, false);
               MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
               int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1};

               for (byte i = 0; i < items.length; i++) {
                  Item item = li.getEquipById(items[i]);
                  item.setPosition((byte)(i + 1));
                  newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
               }

               newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (short)0, (short)100, 0));
               newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (short)0, (short)100, 0));
               c.getPlayer().fakeRelog();
               if (!MapleCharacterUtil.canCreateChar(name, c.isGm(), false) || LoginInformationProvider.getInstance().isForbiddenName(name) && !c.isGm()) {
                  c.getSession().writeAndFlush(CField.createUltimate(0));
               } else {
                  MapleCharacter.saveNewCharToDB(newchar, jobType, (short)0);
                  MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
                  c.getSession().writeAndFlush(CField.createUltimate(1));
               }
            } else {
               c.getPlayer().dropMessage(1, "An error occured.");
               c.getSession().writeAndFlush(CField.createUltimate(0));
            }
         } else {
            c.getPlayer().dropMessage(1, "An error has occurred.");
            c.getSession().writeAndFlush(CField.createUltimate(0));
         }
      } else {
         c.getPlayer().dropMessage(1, "You have no character slots.");
         c.getSession().writeAndFlush(CField.createUltimate(0));
      }
   }

   public static final void DeleteChar(PacketDecoder slea, MapleClient c) {
      String Secondpw_Client = slea.readMapleAsciiString();
      int Character_ID = slea.readInt();

      for (MapleCharacter chr : c.loadCharacters(c.getAccID(), 0)) {
         if (chr.getId() == Character_ID) {
         }
      }

      if (c.login_Auth(Character_ID) && c.isLoggedIn() && !loginFailCount(c)) {
         byte state = 0;
         if (c.getSecondPassword() != null) {
            if (Secondpw_Client == null) {
               return;
            }

            if (!c.CheckSecondPassword(Secondpw_Client) && !c.isGm()) {
               state = 20;
            }
         }

         DBConnection db = new DBConnection();
         String name = "";

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE id = ?");
            ps.setInt(1, Character_ID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               name = rs.getString("name");
            }

            rs.close();
            ps.close();
         } catch (SQLException var12) {
         }

         if (state == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("캐릭터 삭제 (아이피 : ");
            sb.append(c.getSessionIPAddress());
            sb.append(")");
            LoggingManager.putLog(new CreateCharLog(name, c.getAccountName(), Character_ID, c.getAccID(), CreateCharLogType.DeleteChar.getType(), sb));
            state = (byte)c.deleteCharacter(Character_ID);
            updateEditedCharList(c);
            c.loginAttempt = 0;
         }

         c.getSession().writeAndFlush(LoginPacket.deleteCharResponse(Character_ID, state));
      }
   }

   public static final void checkSecondPassword(PacketDecoder slea, MapleClient c) {
      String code = slea.readMapleAsciiString();
      if (!c.CheckSecondPassword(code)) {
         c.getSession().writeAndFlush(LoginPacket.secondPwError((byte)20));
      } else {
         c.getSession().writeAndFlush(LoginPacket.getSecondPasswordConfirm((byte)0));
      }
   }

   public static void NewPassWordCheck(MapleClient c) {
      c.getSession().writeAndFlush(LoginPacket.NewSendPasswordWay(c));
   }

   public static final void onlyRegisterSecondPassword(PacketDecoder slea, MapleClient c) {
      String secondpw = slea.readMapleAsciiString();
      if (secondpw.length() >= 6 && secondpw.length() <= 16) {
         c.setSecondPassword(secondpw);
         c.getSession().writeAndFlush(LoginPacket.getSecondPasswordResult(true));
         c.updateSecondPassword();
      } else {
         c.getSession().writeAndFlush(LoginPacket.getSecondPasswordResult(false));
      }
   }

   public static final void checkWEbLoginEmailID(PacketDecoder slea, MapleClient c) {
      String cookie = slea.readMapleAsciiString();
      int result = c.authAccountInfo(cookie);
      if (result == 0) {
         c.getSession().writeAndFlush(LoginPacket.checkWebLoginEmailID((byte)result));
      }
   }

   public static final void addEditedCharList(MapleClient c, int cid) {
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT position FROM character_select_list WHERE account_id = ?");
         ps.setInt(1, c.getAccID());
         rs = ps.executeQuery();
         if (rs.next()) {
            ps.close();
            String pos = rs.getString("position") + "," + cid;
            ps = con.prepareStatement("UPDATE character_select_list SET position = ? WHERE account_id = ?");
            ps.setString(1, pos);
            ps.setInt(2, c.getAccID());
            ps.executeUpdate();
            c.addCharPosition(cid);
         }

         rs.close();
         ps.close();
      } catch (SQLException var15) {
         var15.printStackTrace();
      } finally {
         if (ps != null) {
            PreparedStatement var17 = null;
         }

         if (rs != null) {
            ResultSet var18 = null;
         }
      }
   }

   public static final void sendCRCCheckFileList(MapleClient c) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CRC_CHECK_FILE.getValue());
      Map<Integer, String> selectedMap = ServerConstants.enableCRCBin ? MapleClientCRC.CRCCheckFiles : MapleClientCRC.fastLoad;
      int size = selectedMap.size();
      packet.writeInt(size);

      for (int a = 0; a < size; a++) {
         packet.writeMapleAsciiString(selectedMap.get(a + 1));
      }

      c.getSession().writeAndFlush(packet.getPacket());
   }

   public static final void updateEditedCharList(MapleClient c) {
      List<Integer> charList = new LinkedList<>();
      List<Integer> editedList = new LinkedList<>();
      List<String> editedListString = new LinkedList<>();
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT id FROM characters WHERE accountid = ?");
         ps.setInt(1, c.getAccID());
         rs = ps.executeQuery();

         while (rs.next()) {
            charList.add(rs.getInt("id"));
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("SELECT position FROM character_select_list WHERE account_id = ?");
         ps.setInt(1, c.getAccID());
         rs = ps.executeQuery();
         String position = null;
         if (rs.next()) {
            position = rs.getString("position");
         }

         rs.close();
         ps.close();
         if (position != null) {
            String[] pos = position.split(",");

            for (int i = 0; i < pos.length; i++) {
               Integer cid = Integer.parseInt(pos[i]);
               if (charList.contains(cid)) {
                  editedList.add(cid);
                  editedListString.add(String.valueOf(cid));
               }
            }

            String arr = String.join(",", editedListString);
            c.setCharPosition(editedList);
            ps = con.prepareStatement("UPDATE character_select_list SET position = ? WHERE account_id = ?");
            ps.setString(1, arr);
            ps.setInt(2, c.getAccID());
            ps.executeUpdate();
         }

         ps.close();
      } catch (SQLException var19) {
         var19.printStackTrace();
      } finally {
         if (ps != null) {
            PreparedStatement var21 = null;
         }

         if (rs != null) {
            ResultSet var22 = null;
         }
      }
   }

   public static final void editedCharList(PacketDecoder slea, MapleClient c) {
      slea.skip(4);
      byte mode = slea.readByte();
      List<String> charPositionString = new LinkedList<>();
      List<Integer> charPosition = new LinkedList<>();
      if (mode == 1) {
         int charNum = slea.readInt();

         for (int i = 1; i <= charNum; i++) {
            int characterID = slea.readInt();
            charPositionString.add(String.valueOf(characterID));
            charPosition.add(characterID);
         }

         String arr = String.join(",", charPositionString);
         c.setCharPosition(charPosition);
         PreparedStatement ps = null;
         ResultSet rs = null;
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT id FROM character_select_list WHERE account_id = ?");
            ps.setInt(1, c.getAccID());
            rs = ps.executeQuery();
            if (rs.next()) {
               ps.close();
               rs.close();
               ps = con.prepareStatement("UPDATE character_select_list SET position = ? WHERE account_id = ?");
               ps.setString(1, arr);
               ps.setInt(2, c.getAccID());
               ps.executeUpdate();
            } else {
               ps.close();
               rs.close();
               ps = con.prepareStatement("INSERT INTO character_select_list (account_id, position) VALUES (?, ?)");
               ps.setInt(1, c.getAccID());
               ps.setString(2, arr);
               ps.executeUpdate();
            }

            ps.close();
            rs.close();
         } catch (SQLException var20) {
            var20.printStackTrace();
         } finally {
            if (ps != null) {
               PreparedStatement var24 = null;
            }

            if (rs != null) {
               ResultSet var25 = null;
            }
         }
      }
   }

   public static void touchWorld(PacketDecoder slea, MapleClient c) {
      int a = slea.readByte();
      int world = slea.readInt();
      int aa = slea.readByte();
      if (ServerConstants.workingReboot) {
         c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "현재 서버가 점검 진행중입니다. 점검 내용은 공지사항을 확인해주시기 바랍니다."));
      } else {
         c.getSession().writeAndFlush(LoginPacket.touchWorldResult(world));
      }
   }

   public static void checkFileCRC(PacketDecoder slea, MapleClient c) {
      boolean crcPass = true;
      byte[] checkBytes = slea.read((int)slea.available());
      byte[] origBytes = ServerConstants.origCRCBytes;

      try {
         String CRCFile = "CRC.bin";
         File f = new File(CRCFile);
         if (!f.exists()) {
            try (
               FileOutputStream fos = new FileOutputStream(f);
               BufferedOutputStream bos = new BufferedOutputStream(fos);
               DataOutputStream dos = new DataOutputStream(bos);
            ) {
               dos.write(checkBytes);
            }
         } else {
            if (origBytes == null) {
               try (DataInputStream in = new DataInputStream(new FileInputStream(CRCFile))) {
                  byte[] tempbytes = in.readAllBytes();
                  origBytes = tempbytes;
                  ServerConstants.origCRCBytes = tempbytes;
               }
            }

            if (origBytes != null) {
               if (origBytes.length == checkBytes.length) {
                  if (origBytes.length >= 16) {
                     for (int i = 16; i < origBytes.length; i++) {
                        if (origBytes[i] != checkBytes[i]) {
                           crcPass = false;
                           break;
                        }
                     }
                  } else {
                     crcPass = Arrays.equals(origBytes, checkBytes);
                  }
               } else {
                  crcPass = false;
               }
            }
         }
      } catch (Exception var28) {
         System.out.println("CRC Check Err");
         var28.printStackTrace();
      }

      checkSPWExist(c);
      if (!crcPass) {
         StringBuilder sb = new StringBuilder();
         sb.append("파일 변조가 의심되는 유저 발생 해당 계정 : ");
         sb.append(c.getAccountName());
         String saveName = c.getAccountName();
         if (saveName == null || saveName.isEmpty()) {
            saveName = "IP_" + c.getSessionIPAddress();
         }

         LoggingManager.putLog(new HackLog(HackLogType.WZEdit.getType(), c.getAccID(), saveName, sb));
         File saveFile = new File("CRCDump/" + saveName + ".bin");
         if (saveFile.exists()) {
            saveFile.delete();
         }

         try (
            FileOutputStream fos = new FileOutputStream(saveFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(bos);
         ) {
            dos.write(checkBytes);
         } catch (Exception var19) {
            System.out.println("CRC File Write Err");
            var19.printStackTrace();
         }
      }

      c.getSession().writeAndFlush(LoginPacket.enableDataLoading());
   }
}
