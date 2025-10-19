package network.netty;

import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import logging.LoggingManager;
import logging.entry.HackLog;
import logging.entry.HackLogType;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.auction.processors.AuctionHandler;
import network.decode.PacketDecoder;
import network.discordbot.DiscordBotHandler;
import network.encode.PacketEncoder;
import network.game.processors.AllianceHandler;
import network.game.processors.BuddyListHandler;
import network.game.processors.ChatHandler;
import network.game.processors.DueyHandler;
import network.game.processors.EnchantHandler;
import network.game.processors.ErdaSpectrumHandler;
import network.game.processors.FlagRaceHandler;
import network.game.processors.GuildHandler;
import network.game.processors.HyperHandler;
import network.game.processors.InterServerHandler;
import network.game.processors.MiniGameActionHandler;
import network.game.processors.MobHandler;
import network.game.processors.NPCHandler;
import network.game.processors.PartyHandler;
import network.game.processors.PlayerHandler;
import network.game.processors.PlayerInteractionHandler;
import network.game.processors.PlayersHandler;
import network.game.processors.SpiritSaviorHandler;
import network.game.processors.StatsHandling;
import network.game.processors.StepUpHandler;
import network.game.processors.SummonHandler;
import network.game.processors.TangyoonKitchenHandler;
import network.game.processors.UnionHandler;
import network.game.processors.UserInterfaceHandler;
import network.game.processors.VMatrixHandler;
import network.game.processors.achievement.AchievementHandler;
import network.game.processors.inventory.CraftHandler;
import network.game.processors.inventory.InventoryHandler;
import network.game.processors.inventory.ItemMakerHandler;
import network.game.processors.inventory.ItemPotHandler;
import network.game.processors.inventory.PetHandler;
import network.game.processors.job.PinkBeanHandler;
import network.game.processors.job.YetiHandler;
import network.game.processors.job.ZeroHandler;
import network.game.processors.monstercollection.MonsterCollectionHandler;
import network.login.processors.CharLoginHandler;
import network.models.CWvsContext;
import network.models.LoginPacket;
import network.shop.processors.CashShopHandler;
import objects.fields.child.karing.Field_BossGoongiPhase;
import objects.item.MaplePet;
import objects.item.PetDataFactory;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.skills.LinkSkill;
import objects.users.skills.SkillEncode;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.FileoutputUtil;
import objects.utils.HexTool;
import objects.utils.MapleAESOFB;
import objects.utils.StringUtil;

public class MapleNettyHandler extends SimpleChannelInboundHandler<PacketDecoder> {
   private final ServerType serverType;
   private final List<String> BlockedIP = new ArrayList<>();
   private final int channel;

   public MapleNettyHandler(ServerType serverType, int channel) {
      this.serverType = serverType;
      this.channel = channel;
   }

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      String address = ctx.channel().remoteAddress().toString().split(":")[0];
      byte[] serverRecv = new byte[]{-2, 11, -60, 92};
      byte[] serverSend = new byte[]{-39, 62, 62, 80};
      MapleClient client = new MapleClient(ctx.channel(), new MapleAESOFB(serverSend, (short)-380), new MapleAESOFB(serverRecv, (short)379), this.serverType);
      client.setChannel(this.channel);
      if (this.serverType == ServerType.LOGIN) {
         ctx.writeAndFlush(LoginPacket.getHello((short)379, serverSend, serverRecv));
      } else {
         ctx.writeAndFlush(LoginPacket.getServerHello((short)379, serverSend, serverRecv));
      }

      ctx.channel().attr(MapleClient.CLIENTKEY).set(client);
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      MapleClient client = (MapleClient)ctx.channel().attr(MapleClient.CLIENTKEY).get();
      if (client != null) {
         client.disconnect(false);
         ctx.channel().attr(MapleClient.CLIENTKEY).set(null);
      }
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
   }

   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt instanceof IdleStateEvent) {
         IdleStateEvent var3 = (IdleStateEvent)evt;
      }
   }

   protected void channelRead0(ChannelHandlerContext ctx, PacketDecoder slea) throws Exception {
      MapleClient c = (MapleClient)ctx.channel().attr(MapleClient.CLIENTKEY).get();
      short header_num = slea.readShort();
      String address = ctx.channel().remoteAddress().toString().split(":")[0];
      if (ServerConstants.DEBUG_RECEIVE
         && header_num != RecvPacketOpcode.QUEST_ACTION.getValue()
         && header_num != 112
         && header_num != RecvPacketOpcode.HEAL_OVER_TIME.getValue()
         && header_num != RecvPacketOpcode.NPC_ACTION.getValue()
         && header_num != RecvPacketOpcode.MOVE_SUMMON.getValue()
         && header_num != RecvPacketOpcode.MOVE_LIFE.getValue()
         && header_num != RecvPacketOpcode.TAKE_DAMAGE.getValue()
         && header_num != RecvPacketOpcode.AUTO_AGGRO.getValue()
         && header_num != RecvPacketOpcode.MOVE_PET.getValue()
         && header_num != 956
         && header_num != 377
         && header_num != RecvPacketOpcode.KEY_PRESSING.getValue()
         && header_num != RecvPacketOpcode.MOVE_PLAYER.getValue()
         && header_num != RecvPacketOpcode.HEAL_OVER_TIME.getValue()
         && header_num != RecvPacketOpcode.INSTANCE_TABLE.getValue()
         && header_num != RecvPacketOpcode.TEMPORARY_STAT_UPDATE_REQUEST.getValue()) {
         StringBuilder sb = new StringBuilder("[RECEIVE] " + header_num + " : " + RecvPacketOpcode.getOpcodeName(header_num) + " :\n");
         sb.append(HexTool.toString(slea.getByteArray())).append("\n").append(HexTool.toStringFromAscii(slea.getByteArray()) + "\n");
         System.out.println(sb.toString());
      }

      if (!ServerConstants.INGAME_TEST_RECV_BLOCK
         || this.serverType != ServerType.GAME
         || header_num == RecvPacketOpcode.PLAYER_LOGGEDIN.getValue()
         || header_num == RecvPacketOpcode.PRIVATE_SERVER_AUTH.getValue()) {
         if (!ServerConstants.fuckNegativeArray.isEmpty()) {
            for (int check : ServerConstants.fuckNegativeArray) {
               String toHex = Integer.toHexString(check).toUpperCase();
               String last = toHex.substring(6, 8) + " " + toHex.substring(4, 6);
               if (slea.toString().contains(last)) {
                  StringBuilder sb = new StringBuilder("[NegativeArray 발생 의심 패킷] " + header_num + " : " + RecvPacketOpcode.getOpcodeName(header_num) + " :\n");
                  sb.append(HexTool.toString(slea.getByteArray())).append("\n").append(HexTool.toStringFromAscii(slea.getByteArray()) + "\n");
                  FileoutputUtil.log("./ErrorLog/NegativeArrayDebug.txt", sb.toString(), false);
               }
            }
         }

         for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == header_num) {
               try {
                  this.handlePacket(recv, slea, c, this.serverType);
               } catch (Exception var11) {
                  if (!DBConfig.isHosting) {
                     System.out.println("[오류] handlePacket 함수 실행중 오류 발생 (" + RecvPacketOpcode.getOpcodeName(header_num) + ") " + var11.toString());
                     var11.getStackTrace().toString();
                     var11.printStackTrace();
                  }

                  FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var11);
               }

               return;
            }
         }
      }
   }

   public static byte[] getHash(byte[] input) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         return md.digest(input);
      } catch (NoSuchAlgorithmException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static byte[] getHash(InputStream input) throws IOException {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         int read = -1;
         byte[] buffer = new byte[1024];

         while ((read = input.read(buffer)) != -1) {
            md.update(buffer, 0, read);
         }

         return md.digest();
      } catch (NoSuchAlgorithmException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static byte[] getHash(File file) throws IOException {
      byte[] hash = null;
      BufferedInputStream bis = null;

      try {
         bis = new BufferedInputStream(new FileInputStream(file));
         hash = getHash(bis);
      } finally {
         if (bis != null) {
            try {
               bis.close();
            } catch (IOException var9) {
            }
         }
      }

      return hash;
   }

   public void handlePacket(RecvPacketOpcode header, PacketDecoder slea, MapleClient c, ServerType serverType) {
      switch (header) {
         case PONG:
            if (c.getPlayer() != null) {
               c.getPlayer().setLastHeartBeatTime(System.currentTimeMillis());
            }

            c.setReceivePing(true);
            break;
         case CHECK_HOT_FIX:
            try {
               File file = new File("Data.img");
               byte[] datas = Files.readAllBytes(file.toPath());
               byte[] byte2 = getHash(file);
               int hash = HexTool.byteArrayToInt(byte2, 0);
               int elength = datas.length << 1;
               PacketEncoder packet = new PacketEncoder(5);
               int s = 0;

               while (true) {
                  int value = elength >> (s & 31) & 127;
                  if (value == 0) {
                     byte[] endBytes = packet.getPacket();

                     for (int i = 0; i < endBytes.length - 1; i++) {
                        byte numPointer = endBytes[i];
                        endBytes[i] = (byte)(numPointer | 128);
                     }

                     PacketEncoder p = new PacketEncoder();
                     p.writeShort(SendPacketOpcode.HOT_FIX.getValue());
                     p.encodeBuffer(endBytes);
                     p.writeInt(hash);
                     p.encodeBuffer(datas);
                     c.getSession().writeAndFlush(p.getPacket());
                     return;
                  }

                  packet.write(value);
                  s += 7;
               }
            } catch (Exception var43) {
               c.getSession().writeAndFlush(LoginPacket.CheckHotFix());
               break;
            }
         case CLIENT_TIME_CHECK:
            c.getSession().writeAndFlush(LoginPacket.CheckClientTime());
            break;
         case CLIENT_HELLO:
            slea.readByte();
            short mapleVersion = slea.readShort();
            short maplePatch = slea.readShort();
            if (mapleVersion != 379 && maplePatch != 8) {
            }
            break;
         case CHECK_LOGIN_AUTH_INFO:
            CharLoginHandler.checkLoginAuthInfo(slea, c);
            break;
         case PRIVATE_SERVER_AUTH:
            if (serverType == ServerType.AUCTION || serverType == ServerType.SHOP) {
               MapleCharacter player = c.getPlayer();
               if (player != null) {
                  long lastCheckTime = player.getLastSpeedHackCheckTime();
                  long delta = System.currentTimeMillis() - lastCheckTime;
                  player.setLastSpeedHackCheckTime(System.currentTimeMillis());
                  if (lastCheckTime != 0L) {
                     double second = Double.parseDouble(String.format("%.1f", delta / 1000.0));
                     if (second < 50.0) {
                        player.setSpeedHackCheckCount(player.getSpeedHackCheckCount() + 1);
                        if (player.getSpeedHackCheckCount() >= 3) {
                           StringBuilder sb = new StringBuilder("스피드 핵 사용 의심 (");
                           sb.append("계정 : ");
                           sb.append(c.getAccountName());
                           sb.append(", 캐릭터 : ");
                           sb.append(c.getPlayer().getName());
                           sb.append(", 사용 배수 : " + (int)(60.0 / second) + "배");
                           sb.append("))");
                           LoggingManager.putLog(new HackLog(HackLogType.SpeedHack.getType(), c.getPlayer(), sb));
                        }
                     }
                  }
               }
            }

            CharLoginHandler.privateServerAuth(slea, c);
            break;
         case CHECK_SPW_EXIST_REQUEST:
            CharLoginHandler.checkSPWExist(c);
            break;
         case FILE_CRC_CHECK:
            CharLoginHandler.checkFileCRC(slea, c);
            break;
         case CHANGE_SPW_REQUEST:
            CharLoginHandler.changeSPWRequest(c);
            break;
         case LOGIN_PASSWORD:
            CharLoginHandler.login(slea, c);
            break;
         case REDISPLAY_WORLD:
            CharLoginHandler.ServerListRequest(c, true, "", "");
            break;
         case CHARLIST_REQUEST:
            CharLoginHandler.CharlistRequest(slea, c);
            break;
         case CHECK_CHAR_NAME:
            CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), c);
            break;
         case CHAR_NAME_CHANGE:
            CharLoginHandler.CheckCharNameChange(slea, c);
            break;
         case CREATE_CHAR:
            CharLoginHandler.CreateChar(slea, c);
            break;
         case DELETE_CHAR:
            CharLoginHandler.DeleteChar(slea, c);
            break;
         case CHAR_SELECT:
            CharLoginHandler.Character_WithoutSecondPassword(slea, c);
            break;
         case LOGIN_WITH_CREATE_CHAR:
            CharLoginHandler.LoginWithCreateCharacter(slea, c);
            break;
         case ONLY_REG_SECOND_PASSWORD:
            CharLoginHandler.onlyRegisterSecondPassword(slea, c);
            break;
         case CHANGE_SPW:
            CharLoginHandler.changeSPW(slea, c);
            break;
         case PRE_CHECK_SPW:
            CharLoginHandler.PreCheckSPW(slea, c);
            break;
         case AUTH_LOGIN_WITH_SPW:
            CharLoginHandler.checkSecondPassword(slea, c);
            break;
         case NEW_PASSWORD_CHECK:
            CharLoginHandler.NewPassWordCheck(c);
            break;
         case EDITED_CHAR_LIST:
            CharLoginHandler.editedCharList(slea, c);
            break;
         case WVS_SET_UP_STEP:
            int idx = slea.readInt();
            if (idx == 1) {
               CharLoginHandler.sendCRCCheckFileList(c);
            }
            break;
         case U_OTP_REQUEST:
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "U-OTP 기능은 추후 업데이트 예정입니다!"));
            break;
         case PACKET_ERROR:
            if (slea.available() >= 6L) {
               if (!DBConfig.isHosting) {
                  System.out.println(slea.toString());
               }

               short type = slea.readShort();
               slea.skip(4);
               c.updateLoginState(0, c.getAccountName());
               short badPacketSize = slea.readShort();
               slea.skip(4);
               int pHeader = slea.readShort();
               String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
               pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
               String op = SendPacketOpcode.getOpcodeName(pHeader);
               String from = "";
               if (c.getPlayer() != null) {
                  from = "Chr: "
                     + c.getPlayer().getName()
                     + " LVL("
                     + c.getPlayer().getLevel()
                     + ") job: "
                     + c.getPlayer().getJob()
                     + " MapID: "
                     + c.getPlayer().getMapId();
               }

               String Recv = from
                  + "\r\n"
                  + "SendOP(-38): "
                  + op
                  + " ["
                  + pHeaderStr
                  + "] ("
                  + (badPacketSize - 4)
                  + ")\r\n"
                  + slea.toString(false)
                  + "\r\n\r\n";
               if (!op.equals("NPC_TALK")) {
                  if (!DBConfig.isGanglim) {
                     DiscordBotHandler.requestSendTelegram(Recv, -506322922L);
                  }

                  if (!DBConfig.isHosting) {
                     System.out.println(Recv);
                  }

                  FileoutputUtil.log("./ErrorLog/ClientErrorPacket.txt", Recv);
               }
            }
            break;
         case CHANGE_CHANNEL:
         case CHANGE_ROOM_CHANNEL:
            InterServerHandler.ChangeChannel(slea, c, c.getPlayer(), header == RecvPacketOpcode.CHANGE_ROOM_CHANNEL);
            break;
         case PLAYER_LOGGEDIN:
            slea.readInt();
            int playerid = slea.readInt();
            String mac = HexTool.toString(slea.read(6)).replace(" ", "-");
            String volume = HexTool.toString(slea.read(4)).replace(" ", "-");
            int accId = c.getAccIdByCharId(playerid);
            c.setAccID(accId);
            String macAddress = mac + ", " + volume;
            c.updateMacs(macAddress, c.getAccountName());
            boolean noGame = CharLoginHandler.noGameForBannedUser(c);
            if (serverType == ServerType.SHOP) {
               CashShopHandler.EnterCS(playerid, c, noGame);
            } else if (serverType == ServerType.AUCTION) {
               AuctionHandler.EnterAuction(playerid, c, noGame);
            } else {
               InterServerHandler.Loggedin(playerid, c, noGame);
            }
         case CONTENT_GUIDE_MOVE:
         case CURE_POT:
         case REWARD_POT:
         case SHOW_BROADCAST:
         case USE_TRADE:
         default:
            break;
         case STAGE_SET_AUCTION:
            InterServerHandler.EnterAuction(c);
            c.setAuction(true);
            break;
         case ENTER_CASH_SHOP:
            InterServerHandler.EnterCS(c, c.getPlayer(), true);
            break;
         case MOVE_PLAYER:
            PlayerHandler.MovePlayer(slea, c, c.getPlayer());
            break;
         case CHAR_INFO_REQUEST:
            slea.readInt();
            PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
            break;
         case MELEE_ATTACK:
            PlayerHandler.meleeAttack(slea, c, c.getPlayer(), header);
            break;
         case BODY_ATTACK:
            PlayerHandler.meleeAttack(slea, c, c.getPlayer(), header);
            break;
         case SHOOT_ATTACK:
            PlayerHandler.shootAttack(slea, c, c.getPlayer(), header);
            break;
         case SHOW_CUBE_LEVELUP_LIMIT:
            PlayerHandler.showCubeLevelUpLimit(c);
            break;
         case HEXA_MATRIX_OPREATION:
            PlayerHandler.hexaMatrixOperation(slea, c);
            break;
         case MAGIC_ATTACK:
         case MOVING_AREA_ATTACK:
         case AREA_DOT_ATTACK:
         case NON_TARGET_FORCE_ATOM_ATTACK:
            PlayerHandler.magicAttack(slea, c, c.getPlayer(), header);
            break;
         case SPECIAL_MOVE:
            PlayerHandler.useSkillRequest(slea, c, c.getPlayer());
            break;
         case CREATE_PSYCHIC_LOCK:
            PlayerHandler.createPsychicLock(slea, c);
            break;
         case RECREATE_PATH_PSYCHIC_LOCK:
            PlayerHandler.recreatePathPsychicLock(slea, c);
            break;
         case RELEASE_PSYCHIC_LOCK:
            PlayerHandler.releasePsychicLock(slea, c);
            break;
         case RELEASE_PSYCHIC_LOCK_MOB:
            PlayerHandler.releasePsychicLockMob(slea, c);
            break;
         case CREATE_PSYCHIC_AREA:
            PlayerHandler.createPsychicArea(slea, c);
            break;
         case DO_ACTIVE_PSYCHIC_AREA:
            PlayerHandler.doActivePsychicArea(slea, c);
            break;
         case RELEASE_PSYCHIC_AREA:
            PlayerHandler.releasePsychicArea(slea, c);
            break;
         case INSTANCE_TABLE:
            HyperHandler.getSpecialStat(slea, c);
            break;
         case CRAFT_COOLDOWN:
            CraftHandler.craftCooldown(slea, c);
            break;
         case CRAFT_DONE:
            CraftHandler.craftComplete(slea, c, c.getPlayer());
            break;
         case CRAFT_MAKE:
            CraftHandler.craftMake(slea, c.getPlayer());
            break;
         case CRAFT_EFFECT:
            CraftHandler.craftEffect(slea, c, c.getPlayer());
            break;
         case START_HARVEST:
            CraftHandler.startHarvest(slea, c, c.getPlayer());
            break;
         case STOP_HARVEST:
            CraftHandler.stopHarvest(slea, c, c.getPlayer());
            break;
         case MAKE_EXTRACTOR:
            CraftHandler.makeExtractor(slea, c, c.getPlayer());
            break;
         case USE_BAG:
            CraftHandler.useBag(slea, c, c.getPlayer());
            break;
         case USE_RECIPE:
            CraftHandler.useRecipe(slea, c, c.getPlayer());
            break;
         case CASH_BULLET_SAVE:
            PlayerHandler.cashBulletEffectOnOff(slea, c);
            break;
         case MOVE_ANDROID:
            PlayerHandler.MoveAndroid(slea, c, c.getPlayer());
            break;
         case FACE_EXPRESSION:
            PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
            break;
         case FACE_ANDROID:
            PlayerHandler.ChangeAndroidEmotion(slea.readInt(), c.getPlayer());
            break;
         case TAKE_DAMAGE:
            PlayerHandler.onUserHit(slea, c, c.getPlayer());
            break;
         case HEAL_OVER_TIME:
            PlayerHandler.Heal(slea, c.getPlayer());
            break;
         case SKILL_CANCEL:
            PlayerHandler.skillCancel(slea.readInt(), c.getPlayer());
            break;
         case MECH_CANCEL:
            PlayerHandler.CancelMech(slea, c.getPlayer());
            break;
         case CANCEL_ITEM_EFFECT:
            PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
            break;
         case ACTIVATE_NICK_ITEM:
            PlayerHandler.ActivateNickItem(slea.readInt(), c, c.getPlayer());
            break;
         case REQUEST_UPGRADE_TOME_EFFECT:
            PlayerHandler.requestUpgradeTombEffect(slea, c, c.getPlayer());
            break;
         case USE_CHAIR:
            slea.skip(4);
            PlayerHandler.UseChair(slea, c, c.getPlayer());
            break;
         case CANCEL_CHAIR:
            PlayerHandler.CancelChair(slea.readShort(), c, c.getPlayer());
            break;
         case USE_ITEMEFFECT:
            PlayerHandler.UseItemEffect(slea.readInt(), c, c.getPlayer());
            break;
         case SKILL_PREPARE:
            PlayerHandler.SkillPrepare(slea, c.getPlayer());
            break;
         case ON_HIT_FIELD_SKILL:
            PlayerHandler.onHitFieldSkill(slea, c);
            break;
         case MOVING_SHOOT_ATTACK_PREPARE:
            PlayerHandler.movingShootAttackPrepare(slea, c.getPlayer());
            break;
         case THROW_GRENADE:
            PlayerHandler.throwGrenade(slea, c.getPlayer());
            break;
         case DESTROY_GRENADE:
            PlayerHandler.destroyGrenade(slea, c.getPlayer());
            break;
         case MESO_DROP:
            slea.readInt();
            PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
            break;
         case SIXTH_FROZEN_CANCEL:
            PlayerHandler.checkSixthFrozenCancel(slea, c);
            break;
         case CHANGE_KEYMAP:
            PlayerHandler.ChangeKeymap(slea, c.getPlayer());
            break;
         case PET_BUFF:
            PetHandler.ChangePetBuff(slea, c.getPlayer());
            break;
         case CHANGE_MAP:
            if (serverType == ServerType.SHOP) {
               CashShopHandler.LeaveCS(slea, c, c.getPlayer());
            } else {
               PlayerHandler.ChangeMap(slea, c, c.getPlayer());
            }
            break;
         case LEAVE_AUCTION:
            AuctionHandler.LeaveAuction(slea, c, c.getPlayer());
            break;
         case CHANGE_MAP_SPECIAL:
            slea.skip(1);
            PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), c, c.getPlayer());
            break;
         case USE_INNER_PORTAL:
            slea.skip(1);
            PlayerHandler.InnerPortal(slea, c, c.getPlayer());
            break;
         case COMPLETE_SUDDEN_MISSION:
            PlayerHandler.completeHiddenMission(c, c.getPlayer());
            break;
         case TROCK_ADD_MAP:
            PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
            break;
         case ARAN_COMBO:
            int skillid = slea.readInt();
            PlayerHandler.AranCombo(c, c.getPlayer(), skillid);
            break;
         case LOSE_ARAN_COMBO:
            PlayerHandler.LossAranCombo(c, c.getPlayer(), 10);
            break;
         case BLESS_OF_DARKNES:
            PlayerHandler.BlessOfDarkness(c.getPlayer());
            break;
         case SKILL_MACRO:
            PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
            break;
         case SUMMON_SECOND_ATOM_REQUEST:
            PlayerHandler.summonSecondAtomRequest(slea, c);
            break;
         case GIVE_FAME:
            PlayersHandler.GiveFame(slea, c, c.getPlayer());
            break;
         case NOTE_ACTION:
            PlayersHandler.Note(slea, c.getPlayer());
            break;
         case USE_DOOR:
            PlayersHandler.UseDoor(slea, c.getPlayer());
            break;
         case USE_MECH_DOOR:
            PlayersHandler.UseMechDoor(slea, c.getPlayer());
            break;
         case DAMAGE_REACTOR:
            PlayersHandler.HitReactor(slea, c);
            break;
         case CLICK_REACTOR:
         case TOUCH_REACTOR:
            PlayersHandler.TouchReactor(slea, c);
            break;
         case CLOSE_CHALKBOARD:
            c.getPlayer().setChalkboard(null);
            break;
         case ITEM_SORT:
            InventoryHandler.ItemSort(slea, c);
            break;
         case ITEM_GATHER:
            InventoryHandler.ItemGather(slea, c);
            break;
         case ITEM_LOCK:
            InventoryHandler.itemLock(slea, c);
            break;
         case ITEM_SORT_LOCK:
            InventoryHandler.itemSortLock(slea, c);
            break;
         case ITEM_MOVE:
            InventoryHandler.ItemMove(slea, c);
            break;
         case MOVE_BAG:
            InventoryHandler.MoveBag(slea, c);
            break;
         case SWITCH_BAG:
            InventoryHandler.SwitchBag(slea, c);
            break;
         case ITEM_MAKER:
            ItemMakerHandler.ItemMaker(slea, c);
            break;
         case ITEM_PICKUP:
            InventoryHandler.Pickup_Player(slea, c, c.getPlayer());
            break;
         case USE_CASH_ITEM:
            InventoryHandler.UseCashItem(slea, c);
            break;
         case RUNE_STONE_USE_REQ:
            PlayersHandler.RuneStoneUseReq(slea, c.getPlayer());
            break;
         case RUNE_STONE_SKILL_REQ:
            try {
               PlayersHandler.RuneStoneSkillReq(slea, c.getPlayer());
            } catch (Exception var42) {
               System.out.println("Rune Stone REQ");
               var42.printStackTrace();
            }
            break;
         case RUNE_STONE_STEP:
            PlayersHandler.RuneStoneStep(slea, c.getPlayer());
            break;
         case USE_ITEM:
            InventoryHandler.UseItem(slea, c, c.getPlayer());
            break;
         case USE_MAGNIFY_GLASS:
            InventoryHandler.UseMagnify(slea, c);
            break;
         case USE_STAMP:
            InventoryHandler.UseStamp(slea, c);
            break;
         case USE_ADDITIONAL_STAMP:
            InventoryHandler.UseAdditionalStamp(slea, c);
            break;
         case USE_CHOOSE_CUBE:
            InventoryHandler.UseChooseCube(slea, c);
            break;
         case USE_SCRIPTED_NPC_ITEM:
            InventoryHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
            break;
         case USE_RETURN_SCROLL:
            InventoryHandler.UseReturnScroll(slea, c, c.getPlayer());
            break;
         case USE_PET_LOOT:
            InventoryHandler.UsePetLoot(slea, c);
            break;
         case USE_PET_SKILL_CHANGE:
            InventoryHandler.UsePetSkillChange(slea, c);
            break;
         case USE_INGAME_CUBE:
            InventoryHandler.UseInGameCube(slea, c);
            break;
         case GOLDEN_HAMMER:
            InventoryHandler.UseGoldenHammer(slea, c);
            break;
         case GOLDEN_HAMMER_RESULT:
            InventoryHandler.UseGoldenHammerResult(slea, c);
            break;
         case USE_FLAG_SCROLL:
            slea.readInt();
            InventoryHandler.UseFlagScroll(slea, c);
            break;
         case USE_UPGRADE_SCROLL: {
            slea.readInt();
            short scrollPos = slea.readShort();
            short invType = slea.readShort();
            short dst = slea.readShort();
            InventoryHandler.UseUpgradeScroll(scrollPos, dst, slea.readByte(), c, c.getPlayer(), invType);
            break;
         }
         case TRANSMISSION_EX_ITEM_OPTIONS:
            InventoryHandler.UseTransmissionExItemOptions(slea, c);
            break;
         case USE_POTENTIAL_SCROLL:
         case USE_EQUIP_SCROLL:
         case EX_ITEM_UPGRADE_ITEM_USE_REQUEST:
         case BLACK_REBIRTH_FLAME_USE_REQUEST: {
            slea.readInt();
            short scrollPos = slea.readShort();
            short dst = slea.readShort();
            InventoryHandler.UseUpgradeScroll(scrollPos, dst, slea.readByte(), c, c.getPlayer(), 1);
            break;
         }
         case BLACK_REBIRTH_FLAME_USE_RESULT:
            InventoryHandler.UseBlackFlame(slea, c);
            break;
         case USE_ADDITIONAL_SCROLL:
            InventoryHandler.UseAdditionalScroll(slea, c);
            break;
         case USE_EXCEPTIONAL_SCROLL:
            InventoryHandler.UseExceptionalScroll(slea, c);
            break;
         case USE_SUMMON_BAG:
            InventoryHandler.UseSummonBag(slea, c, c.getPlayer());
            break;
         case USE_SKILL_BOOK:
            slea.readInt();
            InventoryHandler.UseSkillBook((byte)slea.readShort(), slea.readInt(), c, c.getPlayer());
            break;
         case USE_CATCH_ITEM:
            InventoryHandler.UseCatchItem(slea, c, c.getPlayer());
            break;
         case USE_MOUNT_FOOD:
            InventoryHandler.UseMountFood(slea, c, c.getPlayer());
            break;
         case USE_SOUL_ENCHANTER:
            InventoryHandler.UseSoulEnchanter(slea, c, c.getPlayer());
            break;
         case USE_SOUL_SCROLL:
            InventoryHandler.UseSoulScroll(slea, c, c.getPlayer());
            break;
         case SET_SOUL_EFFECT:
            InventoryHandler.userSoulEffectRequest(slea, c);
            break;
         case REWARD_ITEM:
            InventoryHandler.UseRewardItem((byte)slea.readShort(), slea.readInt(), c, c.getPlayer());
            break;
         case HYPNOTIZE_DMG:
            MobHandler.HypnotizeDmg(slea, c.getPlayer());
            break;
         case MOB_NODE:
            MobHandler.MobNode(slea, c.getPlayer());
            break;
         case DISPLAY_NODE:
            MobHandler.DisplayNode(slea, c.getPlayer());
            break;
         case BIND_LIFE:
            MobHandler.BindMonster(slea, c);
            break;
         case MOVE_LIFE:
            MobHandler.MoveMonster(slea, c, c.getPlayer());
            break;
         case AUTO_AGGRO:
            MobHandler.AutoAggro(slea, c.getPlayer());
            break;
         case FRIENDLY_DAMAGE:
            MobHandler.FriendlyDamage(slea, c.getPlayer());
            break;
         case REISSUE_MEDAL:
            PlayerHandler.ReIssueMedal(slea, c, c.getPlayer());
            break;
         case MONSTER_BOMB:
            MobHandler.monsterBomb(slea, c.getPlayer());
            break;
         case NPC_SHOP:
            NPCHandler.NPCShop(slea, c, c.getPlayer());
            break;
         case NPC_TALK:
            NPCHandler.NPCTalk(slea, c, c.getPlayer());
            break;
         case NPC_TALK_MORE:
            NPCHandler.NPCMoreTalk(slea, c);
            break;
         case NPC_ACTION:
            NPCHandler.NPCAnimation(slea, c);
            break;
         case QUEST_ACTION:
            NPCHandler.QuestAction(slea, c, c.getPlayer());
            break;
         case STORAGE:
            NPCHandler.Storage(slea, c, c.getPlayer());
            break;
         case USER_COMPLETE_NPC_SPEECH:
            NPCHandler.CompleteNpcSpeech(slea, c, c.getPlayer());
            break;
         case ARCANERIVER_QUICK_PATH_REQUEST:
            NPCHandler.ArcaneRiverQuickPath(slea, c);
            break;
         case GENERAL_CHAT:
            if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
               slea.readInt();
               ChatHandler.GeneralChat(slea.readMapleAsciiString(), slea.readByte(), c, c.getPlayer());
            }
            break;
         case MULTICHAT:
         case MULTICHAT_ITEM:
            try {
               ChatHandler.onMultiChat(slea, c, c.getPlayer(), header);
            } catch (Exception var41) {
               System.out.println("MultiChat Err");
               var41.printStackTrace();
            }
            break;
         case WHISPER:
         case WHISPER_ITEM:
            ChatHandler.onWhisper(slea, c, header);
            break;
         case MESSENGER:
            ChatHandler.Messenger(slea, c);
            break;
         case AUTO_ASSIGN_AP:
            StatsHandling.AutoAssignAP(slea, c, c.getPlayer());
            break;
         case DISTRIBUTE_AP:
            StatsHandling.DistributeAP(slea, c, c.getPlayer());
            break;
         case DISTRIBUTE_SP:
            slea.readInt();
            StatsHandling.DistributeSP(slea.readInt(), slea.readInt(), c, c.getPlayer());
            break;
         case ADD_HYPER_STAT_SKILL:
            HyperHandler.changeHyperStatLevel(slea, c);
            break;
         case RESET_HYPER_STAT_SKILL:
            HyperHandler.resetHyperSkillStat(slea, c);
            break;
         case CHANGE_HYPER_STAT_PRESET:
            HyperHandler.changeHyperStatPreset(slea, c);
            break;
         case PLAYER_INTERACTION:
            PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
            break;
         case MINIGAME_ACTION:
            MiniGameActionHandler.MiniGameAction(slea, c, c.getPlayer());
            break;
         case GUILD_OPERATION:
            GuildHandler.GuildRequest(slea, c);
            break;
         case DENY_GUILD_REQUEST:
            GuildHandler.DenyGuildRequest(slea, c);
            break;
         case USER_RIDE_SET:
            PlayersHandler.userRideSet(slea, c);
            break;
         case TRAIN_MASTER:
            PlayerHandler.trainMaster(slea, c);
            break;
         case REQUEST_GUILD:
            GuildHandler.joinRequest(slea, c.getPlayer());
            break;
         case CANCEL_GUILD_REQUEST:
            GuildHandler.removeJoinRequest(slea, c.getPlayer());
            break;
         case GUILD_ACCEPT_JOIN_REQUEST:
            GuildHandler.acceptJoinRequest(slea, c.getPlayer());
            break;
         case GUILD_DECLINE_JOIN_REQUEST:
            GuildHandler.declineJoinRequest(slea, c.getPlayer());
            break;
         case GUILD_CONTENTS_LOG_REQUEST:
            GuildHandler.guildContentsLogRequest(slea, c.getPlayer());
            break;
         case ALLIANCE_OPERATION:
            AllianceHandler.HandleAlliance(slea, c, false);
            break;
         case DENY_ALLIANCE_REQUEST:
            AllianceHandler.HandleAlliance(slea, c, true);
            break;
         case PARTY_REQUEST:
            PartyHandler.PartyRequest(slea, c);
            break;
         case PARTY_RESULT:
            PartyHandler.PartyResult(slea, c);
            break;
         case ALLOW_PARTY_INVITE:
            PartyHandler.allowPartyInvite(slea, c);
            break;
         case ALLOW_BUDDY_ADD:
            BuddyListHandler.allowBuddyAdd(slea, c);
            break;
         case BUDDYLIST_MODIFY:
            BuddyListHandler.BuddyOperation(slea, c);
            break;
         case SHIP_OBJECT:
            UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
            break;
         case BUY_CS_ITEM:
            CashShopHandler.BuyCashItem(slea, c, c.getPlayer());
            break;
         case COUPON_CODE:
            slea.skip(1);
            CashShopHandler.CouponCode(slea.readMapleAsciiString(), c);
            CashShopHandler.doCSPackets(c);
            break;
         case CS_UPDATE:
            CashShopHandler.CSUpdate(c);
            break;
         case USE_POT:
            ItemPotHandler.usePot(slea, c.getPlayer());
            break;
         case CLEAR_POT:
            ItemPotHandler.clearPot(slea, c.getPlayer());
            break;
         case FEED_POT:
            ItemPotHandler.feedPot(slea, c.getPlayer());
            break;
         case USE_CASH_CONSUME_ITEM:
            PlayerHandler.useCashConsumeItem(slea, c.getPlayer());
            break;
         case DAMAGE_SUMMON:
            SummonHandler.DamageSummon(slea, c.getPlayer());
            break;
         case MOVE_SUMMON:
            SummonHandler.MoveSummon(slea, c.getPlayer());
            break;
         case SUMMON_ATTACK:
            SummonHandler.SummonAttack(slea, c, c.getPlayer());
            break;
         case MOVE_DRAGON:
            SummonHandler.MoveDragon(slea, c.getPlayer());
            break;
         case SUB_SUMMON:
            SummonHandler.OnSkill(slea, c.getPlayer());
            break;
         case REMOVE_SUMMON:
            SummonHandler.RemoveSummon(slea, c);
            break;
         case CURSE_ENCHANT_REQUEST:
            PlayerHandler.curseEnchantRequest(slea, c.getPlayer());
            break;
         case CURSE_ENCHANT_SELECTED:
            PlayerHandler.curseEnchantSelected(slea, c.getPlayer());
            break;
         case SPAWN_PET:
            try {
               PetHandler.SpawnPet(slea, c, c.getPlayer());
            } catch (Exception var40) {
               System.out.println("SpawnPet Err");
               var40.printStackTrace();
            }
            break;
         case MOVE_PET:
            PetHandler.MovePet(slea, c.getPlayer());
            break;
         case PET_CHAT:
            if (slea.available() >= 12L) {
               int petid = slea.readInt();
               slea.readInt();
               PetHandler.PetChat(petid, slea.readShort(), slea.readMapleAsciiString(), c.getPlayer());
            }
            break;
         case PET_COMMAND:
            MaplePet pet = null;
            pet = c.getPlayer().getPet((byte)slea.readInt());
            slea.readByte();
            if (pet == null) {
               return;
            }

            PetHandler.PetCommand(pet, PetDataFactory.getPetCommand(pet.getPetItemId(), slea.readByte()), c, c.getPlayer());
            break;
         case PET_FOOD:
            PetHandler.PetFood(slea, c, c.getPlayer());
            break;
         case PET_LOOT:
            InventoryHandler.Pickup_Pet(slea, c, c.getPlayer());
            break;
         case PET_AUTO_POT:
            PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
            break;
         case PET_EXCEPTION_ITEM:
            PetHandler.Pet_ExceptionList(slea, c, c.getPlayer());
            break;
         case DUEY_ACTION:
            DueyHandler.DueyOperation(slea, c);
            break;
         case LEFT_KNOCK_BACK:
            PlayerHandler.leftKnockBack(slea, c);
            break;
         case SNOWBALL:
            PlayerHandler.snowBall(slea, c);
            break;
         case COCONUT:
            PlayersHandler.hitCoconut(slea, c);
            break;
         case REPAIR:
            NPCHandler.repair(slea, c);
            break;
         case REPAIR_ALL:
            NPCHandler.repairAll(c);
            break;
         case OWL:
            InventoryHandler.Owl(slea, c);
            break;
         case OWL_WARP:
            InventoryHandler.OwlWarp(slea, c);
            break;
         case USE_OWL_MINERVA:
            InventoryHandler.OwlMinerva(slea, c);
            break;
         case USE_SILVER_KARMA_RESULT:
            InventoryHandler.useSilverKarma(slea, c.getPlayer());
            break;
         case USE_ITEM_QUEST:
            NPCHandler.UseItemQuest(slea, c);
            break;
         case AUCTION:
            AuctionHandler.auctionRequestHandler(slea, c);
            break;
         case RPS_GAME:
            NPCHandler.RPSGame(slea, c);
            break;
         case UPDATE_QUEST:
            NPCHandler.UpdateQuest(slea, c);
            break;
         case USE_NAME_CHANGE:
            InventoryHandler.UseNameChangeCoupon(slea, c);
            break;
         case FOLLOW_REQUEST:
            PlayersHandler.FollowRequest(slea, c);
            break;
         case AUTO_FOLLOW_REPLY:
         case FOLLOW_REPLY:
            PlayersHandler.FollowReply(slea, c);
            break;
         case RING_ACTION:
            PlayersHandler.RingAction(slea, c);
            break;
         case PARTY_SEARCH_START:
            PartyHandler.MemberSearch(slea, c);
            break;
         case PARTY_SEARCH_STOP:
            PartyHandler.PartySearch(slea, c);
            break;
         case EXPEDITION_LISTING:
            PartyHandler.PartyListing(slea, c);
            break;
         case USE_TELE_ROCK:
            InventoryHandler.TeleRock(slea, c);
            break;
         case REPORT:
            PlayersHandler.Report(slea, c);
            break;
         case CHECK_WEB_LOGIN_EMAIL_ID:
            slea.skip(2);
            CharLoginHandler.checkWEbLoginEmailID(slea, c);
            break;
         case ACCOUNT_INFO_REQUEST:
            CharLoginHandler.ServerListRequest(c, false, "", "");
            break;
         case RETURN_TO_TITLE:
            c.disconnect(false);
            DBConnection db = new DBConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;

            try (Connection con = DBConnection.getConnection()) {
               ps = con.prepareStatement("SELECT `cookie` FROM `accounts` WHERE id = ?");
               ps.setInt(1, c.getAccID());
               rs = ps.executeQuery();
               if (rs.next()) {
                  String cookie = rs.getString("cookie");
                  if (cookie == null) {
                     cookie = "";
                  }

                  c.updateLoginState(0, c.getSessionIPAddress(), "");
                  c.getSession().writeAndFlush(CWvsContext.serverMessage(""));
                  c.getSession().writeAndFlush(LoginPacket.getIssueReloginCookie(cookie));
                  break;
               }
            } catch (SQLException var45) {
               var45.printStackTrace();
               break;
            } finally {
               if (rs != null) {
                  ResultSet var80 = null;
               }

               if (ps != null) {
                  PreparedStatement var78 = null;
               }
            }

            return;
         case PQ_REWARD:
            InventoryHandler.SelectPQReward(slea, c);
            break;
         case LEGENDARY_INNER_CHANGE:
            PlayerHandler.legendaryAbilityCir(slea, c);
            break;
         case CHAOS_INNER_CHANGE:
            PlayerHandler.chaosAbilityCir(slea, c);
            break;
         case INNER_CHANGE:
            PlayerHandler.requestCharacterPotentialSkillRandSet(slea, c);
            break;
         case BLACK_INNER_CHANGE:
            PlayerHandler.blackAbilityCir(slea, c);
            break;
         case SELECT_BLACK_CICULATOR:
            PlayerHandler.selectBlackCir(slea, c);
            break;
         case ABSORB_REGEN:
            PlayerHandler.forceAtomRegen(slea, c);
            break;
         case WILL_OF_SWORD_COMBO:
            PlayerHandler.absorbingSword(slea, c.getPlayer());
            break;
         case ORBITAL_FLAME:
            PlayerHandler.OrbitalFlame(slea, c);
            break;
         case VIEW_SKILLS:
            PlayersHandler.viewSkills(slea, c);
            break;
         case SKILL_SWIPE:
            PlayersHandler.stealSkill(slea, c);
            break;
         case CHOOSE_SKILL:
            PlayersHandler.ChooseSkill(slea, c);
            break;
         case KEY_DOWN_AREA_MOVE_PATH:
            PlayerHandler.keyDownAreaMovePath(slea, c.getPlayer());
            break;
         case EQUIPMENT_ENCHANT:
            EnchantHandler.equipmentEnchantWithSingleUIRequest(slea, c);
            break;
         case DRESS_UP:
            PlayerHandler.DressUpRequest(c.getPlayer(), slea);
            break;
         case SET_DEFAULT_WING_ITEM:
            PlayerHandler.setDefaultWingItem(slea, c);
            break;
         case SKILL_REQUEST_AREA:
            PlayerHandler.onSkillRequesetArea(slea, c);
            break;
         case VCORE_REQUEST:
            VMatrixHandler.VCoreRequest(slea, c);
            break;
         case VCORE_CHECK_SECOND_PASSWORD:
            VMatrixHandler.VCoreCheckSecondPassowrd(slea, c);
            break;
         case VCORE_QUESTION:
            VMatrixHandler.VCoreQuestion(slea, c);
            break;
         case JOKER_ATTACK:
            PlayerHandler.throwJoker(slea, c);
            break;
         case USER_THROWING_BOMB_ACTION:
            VMatrixHandler.UserThrowingBombAction(slea, c);
            break;
         case SELECT_DICE:
            int data = slea.readInt();
            SecondaryStatEffect effect = c.getPlayer().getSkillLevelData(400051000);
            if (effect != null) {
               c.getPlayer().temporaryStatSet(400051000, Integer.MAX_VALUE, SecondaryStatFlag.LoadedDice, data);
            }
            break;
         case ACTIVE_PRAY_BUFF:
            PlayerHandler.activePrayBuff(c);
            break;
         case MERGE_ARCANE_SYMBOL:
            PlayerHandler.SymbolExp(slea, c);
            break;
         case USER_AUTHENTIC_UPGRADE_REQUEST:
            PlayerHandler.SymbolExp(slea, c);
            break;
         case BRM_SERVER_ON_CALC_REQUEST:
            PlayerHandler.battleRecordServerOnCalcRequest(slea, c);
            break;
         case CHAIN_ARTS_TAKE_DOWN:
            PlayerHandler.chainArtsTakeDown(slea, c);
            break;
         case ACTIVE_ILLUSIONARY_SHOT:
            PlayerHandler.activeIllusionaryShot(slea, c);
            break;
         case SERVANT_EXTEND_CHANGE_POS:
            PlayerHandler.shadowServantExtendChangePos(slea, c);
            break;
         case MEGA_SMASHER_REQUEST:
            PlayerHandler.megaSmasherRequest(slea, c);
            break;
         case SPOTLIGHT_BUFF:
            PlayerHandler.spotlightBuff(slea, c);
            break;
         case PSYCHIC_TORNADO_ACTIVE_BUFF:
            PlayerHandler.psychicTornadoActiveBuff(slea, c);
            break;
         case ACTIVE_VMATRIX_SUMMON:
            SummonHandler.activeVMatrixSummon(slea, c);
            break;
         case CRYSTAL_STACK_REQUEST:
            PlayerHandler.crystalStackRequest(slea, c);
            break;
         case CRYSTAL_ACTION_REQUEST:
            PlayerHandler.crystalActionRequest(slea, c);
            break;
         case ACTIVE_HARMONY_LINK:
            PlayerHandler.activeHarmonyLink(slea, c);
            break;
         case HOLLY:
            PlayerHandler.useHollySkill(slea, c);
            break;
         case UNLINK_SKILL:
            LinkSkill.unlinkSkill(slea, c);
            break;
         case LINK_SKILL:
            LinkSkill.linkSkill(slea, c);
            break;
         case BINGO_CELL_CLICK:
            slea.skip(8);
            PlayerHandler.bingoCellClick(slea.readInt(), c);
            break;
         case BINGO_ADD_RANK:
            PlayerHandler.bingoAddRank(c);
            break;
         case TOUCH_WORLD:
            CharLoginHandler.touchWorld(slea, c);
            break;
         case LINK_ITEM_CHAT:
            ChatHandler.LinkItemChat(slea, c);
            break;
         case SKILL_COMMAND_LOCK:
            PlayerHandler.skillCommandLock(slea, c);
            break;
         case SKILL_AUTO_USE_LOCK:
            PlayerHandler.skillAutoUseLock(slea, c);
            break;
         case ACTIVE_PASSIVE_SKILL:
            PlayerHandler.activePassiveSkill(slea, c);
            break;
         case USER_TOWER_CHAIR_SETTING:
            PlayerHandler.userTowerChairSetting(slea, c);
            break;
         case SPAWN_ARROW_FLATER:
            PlayerHandler.createArrowFlaterRequest(slea, c);
            break;
         case B2BODY_REQUEST:
            PlayerHandler.userB2BodyRequest(slea, c);
            break;
         case BUZZING_HOUSE_REQUEST:
            PlayerHandler.BuzzingHouseRequest(slea, c);
            break;
         case EXIT_BUZZING_HOUSE:
            PlayerHandler.ExitBuzzingHouse(slea, c);
            break;
         case SELECT_MIRROR_DUNGEON:
            NPCHandler.selectMirrorDungeon(slea, c);
            break;
         case PLAYER_RESPAWN:
            PlayerHandler.playerRespawn(slea, c);
            break;
         case DAILY_GIFT_REQUEST:
            PlayerHandler.dailyGiftRequest(slea, c);
            break;
         case FALL_OBSTACLE_ATOM:
            MobHandler.fallObstacleAtom(slea, c);
            break;
         case FALL_STONE_CHECK:
            MobHandler.checkVellumStoneFall(slea, c);
            break;
         case MOB_SKILL_DELAY_END:
            MobHandler.mobSkillDelayEnd(slea, c);
            break;
         case MOB_DAMAGE_SHARE_INFO:
            MobHandler.mobDamageShareInfo(slea, c);
            break;
         case DAMAGE_SKIN_SAVE_REQUEST:
            PlayerHandler.damageSkinSaveRequest(slea, c);
            break;
         case QUICK_SLOT_KEY_MAPPED_MODIFIED:
            PlayerHandler.quickSlotKeyMappedModified(slea, c);
            break;
         case MIRACLE_CIRCULATOR_SELECT_REQUEST:
            PlayerHandler.miracleCirculatorSelectRequest(slea, c);
            break;
         case USER_OPEN_MAPLE_UNION_REQUEST:
            UnionHandler.userOpenMapleUnionRequest(slea, c);
            break;
         case MAPLE_UNION_CHANGE_REQUEST:
            UnionHandler.mapleUnionChangeRequest(slea, c);
            break;
         case MAPLE_UNION_PRESET_REQUEST:
            UnionHandler.mapleUnionPresetRequest(slea, c);
            break;
         case UNION_RAID_OUT_REQUEST:
            UnionHandler.unionRaidOutRequest(slea, c);
            break;
         case MAPLE_UNION_PRESET_MODIFY_REQUEST:
            UnionHandler.mapleUnionPresetModifyRequest(slea, c);
            break;
         case USER_RUN_SCRIPT:
            UnionHandler.userRunScript(slea, c);
            break;
         case USER_PROTECT_BUFF_ON_DIE_ITEM_REQUEST:
            PlayerHandler.protectBuffOnDieItemRequest(slea, c);
            break;
         case CHAR_SLOT_INC_ITEM_USE_REQUEST:
            InventoryHandler.CharSlotIncItemUseRequest(slea, c);
            break;
         case MOB_EXPLOSION_START:
            MobHandler.mobExplosionStart(slea, c);
            break;
         case USER_CATCH_DEBUFF_COLLISION:
            PlayerHandler.userCatchDebuffCollision(slea, c);
            break;
         case MOB_AREA_ATTACK_DIESEASE:
            MobHandler.mobAreaAttackDisease(slea, c);
            break;
         case SUMMONED_SKILL_USE_REQUEST:
            SummonHandler.summonedSkillUseRequest(slea, c);
            break;
         case DEMIAN_OBJECT_MAKE_ENTER_ACK:
            PlayerHandler.demianObjectMakeEnterAck(slea, c);
            break;
         case DEMIAN_OBJECT_NODE_END:
            PlayerHandler.demianNodeEnd(slea, c);
            break;
         case STIGMA_DELEVERY_REQUEST:
            PlayerHandler.stigmaDeleveryRequest(slea, c);
            break;
         case UPDATE_LAPIDIFICATION:
            PlayerHandler.updateLapidification(slea, c);
            break;
         case USER_REQUEST_CHANGE_MOB_ZONE_STATE:
            PlayerHandler.userChangeMobZoneState(slea, c);
            break;
         case KEY_PRESSING:
            PlayerHandler.keyPressing(c);
            break;
         case RW_ACTION_CANCEL:
            PlayerHandler.rwActionCancel(slea, c);
            break;
         case RW_MULTI_CHARGE_CANCEL_REQUEST:
            PlayerHandler.rwMultiChargeCancelRequest(slea, c);
            break;
         case USER_KEY_DOWN_STEP_REQUEST:
            PlayerHandler.userKeyDownStepRequest(slea, c);
            break;
         case RW_CLEAR_CURRENT_ATTACK_REQUEST:
            PlayerHandler.rwClearCurrentAttackRequest(slea, c);
            break;
         case JAGUAR_CHANGE_RQUEST:
            PlayerHandler.jaguarChangeRequest(slea, c);
            break;
         case MOB_ATTACK_MOB:
            MobHandler.mobAttackMob(slea, c);
            break;
         case REQUEST_SET_OFF_TRINITY:
            PlayerHandler.onSetOffTrinity(slea, c);
            break;
         case DEBUFF_PSYCHIC_AREA:
            PlayerHandler.debuffPsychicArea(slea, c);
            break;
         case END_THROWING_BOMB_REQUEST:
            PlayerHandler.endThrowingBombRequest(slea, c);
            break;
         case STOP_SHURRIKANE_REQUEST:
            PlayerHandler.stopShurrikaneRequest(slea, c);
            break;
         case CREATE_AREA_DOT_REQUEST:
            PlayerHandler.createAreaDotRequest(slea, c);
            break;
         case REINSTALL_AREA_REQUEST:
            PlayerHandler.reinstallAreaRequest(slea, c);
            break;
         case BULLET_PARTY_KEY_INPUT:
            PlayerHandler.bulletPartyKeyInput(slea, c);
            break;
         case CASH_CODYCOUPON_REQUEST:
            if (c.getPlayer() != null && c.getPlayer().isGM()) {
               c.getPlayer().dropMessage(5, "CODYCOUPON Packet : " + slea.toString());
            }

            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
            break;
         case SKILL_STACK_UPDATE_REQUEST:
            PlayerHandler.updateStackSkillRequest(slea, c);
            break;
         case UPDATE_KEYDOWN_READY_SKILL_REQUEST:
            PlayerHandler.updateKeydownReadySkillRequest(slea, c);
            break;
         case SUMMONED_CHANGE_SKILL_REQUEST:
            SummonHandler.summonedChangeSkillRequest(slea, c);
            break;
         case SUMMONED_RESPAWN_REQUEST:
            PlayerHandler.summonedRespawnRequest(slea, c);
            break;
         case AFFECTED_AREA_REMOVE_BY_SKILL:
         case AFFECTED_AREA_REMOVE_BY_SKILL3:
            PlayerHandler.affectedAreaRemoveBySkill(slea, c);
            break;
         case AFFECTED_AREA_REMOVE_BY_SKILL2:
            PlayerHandler.affectedAreaRemoveBySkill2(slea, c);
            break;
         case CHARGE_WILD_GRENADE:
            SummonHandler.chargeWildGrenade(slea, c);
            break;
         case CHARGE_INFINITY_FLAME_CIRCLE:
            PlayerHandler.chargeInfinityFlameCircle(slea, c);
            break;
         case TRY_REGISTER_TELEPORT:
            PlayerHandler.tryRegisterTeleport(slea, c);
            break;
         case PASSIVE_SET_STAT_REQUEST:
            PlayerHandler.passiveSetStatRequest(slea, c);
            break;
         case TIDE_OF_BATTLE_REQUEST:
            PlayerHandler.tideOfBattleRequest(slea, c);
            break;
         case BOSS_ENTER_UI_REQUEST:
            UserInterfaceHandler.BossEnter_Request(slea, c);
            break;
         case USER_FIELD_TRANSFER_REQUEST:
            PlayerHandler.userFieldTransferRequest(slea, c);
            break;
         case REMOVE_SECOND_ATOM_REQUEST:
            PlayerHandler.removeSecondAtomRequest(slea, c);
            break;
         case MOVE_SECOND_ATOM:
            PlayerHandler.moveSecondAtom(slea, c);
            break;
         case RECREATE_SECOND_ATOM_REQUEST:
            PlayerHandler.recreateSecondAtom(slea, c);
            break;
         case ARCANE_CATALYST_UNSTABILITY_PREVIEW:
         case ARCANE_CATALYST_RESTORE_PREVIEW:
            PlayerHandler.arcaneCatalystPreview(slea, c, header == RecvPacketOpcode.ARCANE_CATALYST_UNSTABILITY_PREVIEW);
            break;
         case ARCANE_CATALYST_UNSTABILITY_REQUEST:
         case ARCANE_CATALYST_RESTORE_REQUEST:
            PlayerHandler.arcaneCatalystRequest(slea, c, header == RecvPacketOpcode.ARCANE_CATALYST_UNSTABILITY_REQUEST);
            break;
         case REQUEST_FREE_CHANGE_JOB:
            PlayerHandler.requestFreeChangeJob(slea, c);
            break;
         case LUCID_ACTIVATE_STATUE:
            PlayerHandler.lucidActivateStatue(slea, c);
            break;
         case LUCID_CONTAGION:
            PlayerHandler.lucidContagionResult(slea, c);
            break;
         case CABINET_REQUEST:
            PlayerHandler.cabinetRequest(slea, c);
            break;
         case DREAM_BREAKER_SKILL_REQUEST:
            PlayerHandler.dreamBreakerSkillRequest(slea, c);
            break;
         case USE_WILL_MOON_GAUGE:
            PlayerHandler.willUseMoonGauge(slea, c);
            break;
         case TOUCH_SPIDER_WEB:
            PlayerHandler.touchSpiderWeb(slea, c);
            break;
         case JINHILLAH_THREAD_HIT_USER:
            PlayerHandler.jinHillahThreadHitUser(slea, c);
            break;
         case UPDATE_JINHILLAH_ALTAR_REQUEST:
            PlayerHandler.updateJinHillahAltarRequest(slea, c);
            break;
         case ANDROID_SET_EAR:
            PlayerHandler.setAndroidEar(slea, c);
            break;
         case ORCA_DO_ATTACK:
            PlayerHandler.orcaDoAttack(slea, c);
            break;
         case BLACK_MAGE_CHANGE_ATTRIBUTE:
            PlayerHandler.blackMageChangeAttributes(slea, c);
            break;
         case END_BURNING_BREAKER:
            PlayerHandler.endBurningBreaker(slea, c);
            break;
         case PEACE_MAKER_HEAL_USER:
            PlayerHandler.peaceMakerHealUser(slea, c);
            break;
         case PEACE_MAKER_BUFF_USER:
            PlayerHandler.peaceMakerBuffUser(slea, c);
            break;
         case ANTI_MACRO_REQUEST:
            PlayerHandler.antiMacroRequest(slea, c);
            break;
         case USER_ANTI_MACRO_ITEM_USE_REQUEST:
            PlayerHandler.userAntiMacroItemUseRequest(slea, c);
            break;
         case SKILL_EFFECT_ON_OFF:
            PlayerHandler.skillEffectOnOff(slea, c);
            break;
         case USER_SHOOT_ATTACK_IN_FPS:
            PlayerHandler.userShootAttackInFPSMode(slea, c);
            break;
         case COURTSHIP_COMMAND_REQUEST:
            PlayerHandler.courtshipCommandRequest(slea, c);
            break;
         case USE_RANDOM_PORTAL:
            PlayersHandler.UseRandomPortal(slea, c.getPlayer());
            break;
         case MOVE_MIGHTY_MJOLNIR:
            PlayerHandler.moveMightyMjolnir(slea, c);
            break;
         case REMOVE_SECOND_ATOM_WITH_HEAL:
            PlayerHandler.removeSecondAtomWithHeal(slea, c);
            break;
         case DECREMENT_AUTO_CHARGE_STACK:
            PlayerHandler.decrementAutoChargeStack(slea, c);
            break;
         case INCREMENT_AUTO_CHARGE_STACK:
            PlayerHandler.incrementAutoChargeStack(slea, c);
            break;
         case INCREMENT_SILHOUETTE_MIRAGE_STACK:
            PlayerHandler.incrementSilhouetteMirageStack(slea, c);
            break;
         case SKILL_ACTION_CANCEL:
            PlayerHandler.skillActionCancel(slea, c);
            break;
         case REVENANT_RAGE_REQUEST:
            PlayerHandler.revenantRageRequest(slea, c);
            break;
         case POISON_CHAIN_REQUEST:
            PlayerHandler.poisonChainRequest(slea, c);
            break;
         case PHOTON_RAY_FULL_CHARGE:
            PlayerHandler.phtonRayFullCharge(slea, c);
            break;
         case HUNGRY_MUTO_REQUEST:
            PlayerHandler.hungryMutoRequest(slea, c);
            break;
         case SET_CUSTOM_CHAIR:
            PlayerHandler.setCustomChair(slea, c);
            break;
         case INVITE_CHAIR_REQUEST:
            PlayerHandler.inviteChairRequest(slea, c);
            break;
         case INVITE_CHAIR_RESULT:
            PlayerHandler.inviteChairResult(slea, c);
            break;
         case KICK_CHAIR_REQUEST:
            PlayerHandler.kickChairRequest(slea, c);
            break;
         case JUPITER_THUNDER_REQUEST:
            PlayerHandler.jupiterThunderRequest(slea, c);
            break;
         case JUPITER_THUNDER_MOVE_REQUEST:
            PlayerHandler.jupiterThunderMoveRequest(slea, c);
            break;
         case JUPITER_THUNDER_REMOVE_REQUEST:
            PlayerHandler.jupiterThunderRemoveRequest(slea, c);
            break;
         case SKILL_UPDATE_PER_TICK:
            PlayerHandler.skillUpdatePerTick(slea, c);
            break;
         case EXTRA_SKILL_REQUEST_PER_TICK:
            PlayerHandler.extraSkillRequest(slea, c);
            break;
         case APPLY_PIRATE_BLESS:
            PlayerHandler.applyPirateBless(slea, c);
            break;
         case USER_KAISER_COLOR_CHANGE_ITEM_USE_REQUEST:
            PlayerHandler.userKaiserColorChangeItemUseRequest(slea, c);
            break;
         case SET_MOVE_GRENADE:
            PlayerHandler.userOnSetMoveGrenade(slea, c);
            break;
         case PAPULATUS_LASER_COLLISION:
            MobHandler.papulatusLaserCollision(c);
            break;
         case PAPULATUS_HOLD_CRANE:
            MobHandler.papulatusHoldCrane(slea, c);
            break;
         case PAPULATUS_RELEASE_CRANE:
            MobHandler.papulatusReleaseHoldCrane(slea, c);
            break;
         case CHECK_KAIN_STACK_SKILL:
            PlayerHandler.checkKainStackSkillRequest(slea, c);
            break;
         case REMOVE_KAIN_DEATH_BLESSING_REQUEST:
            PlayerHandler.removeKainDeathBlessingRequest(slea, c);
            break;
         case POOL_MAKER_CANCEL_REQUEST:
            PlayerHandler.poolMakerCancelRequest(slea, c);
            break;
         case USER_DRAGON_ACTION:
            PlayerHandler.userDragonAction(slea, c);
            break;
         case USER_DRAGON_BREATH_EARTH_EFFECT:
            PlayerHandler.userDragonBreathEarthEffect(slea, c);
            break;
         case USER_RENAME_REQUEST:
            PlayerHandler.userRenameRequest(slea, c);
            break;
         case USER_RENAME_CHECK_SPW:
            PlayerHandler.userRenameCheckSPW(slea, c);
            break;
         case YUT_GAME_NEXT_TURN:
            PlayerHandler.yutGameNextTurn(slea, c);
            break;
         case YUT_GAME_REQUEST:
            PlayerHandler.yutGameRequest(slea, c);
            break;
         case YUT_GO_HOME_REQUEST:
            PlayerHandler.yutGoHomeRequest(slea, c);
            break;
         case YUT_EXIT_REQUEST:
            PlayerHandler.yutExitRequest(slea, c);
            break;
         case EVENT_SKILL_ON_OFF:
            PlayerHandler.eventSkillOnOff(slea, c);
            break;
         case DIMENSIONAL_MIRROR_REQUEST:
            PlayerHandler.dimensionalMirrorRequest(slea, c);
            break;
         case WAIT_QUEUE_REQUEST:
            PlayerHandler.waitQueueRequest(slea, c);
            break;
         case MISSION_2_SPACE_INPUT:
            PlayerHandler.mission2SpaceInput(slea, c);
            break;
         case MISSION_2_SPACE_EXIT:
            PlayerHandler.mission2SpaceExit(slea, c);
            break;
         case EXTREME_RAIL_END_GAME:
            PlayerHandler.extremeRailEndGame(slea, c);
            break;
         case EXTREME_RAIL_UPDATE_DISTANCE:
            PlayerHandler.extremeRailUpdateDistance(slea, c);
            break;
         case EXTREME_RAIL_EXIT_GAME:
            PlayerHandler.extremeRailExit(slea, c);
            break;
         case USER_OCCULT_ADDITIONAL_CUBE_ITEM_USE_REQUEST:
            InventoryHandler.UseOccultAdditionalCube(slea, c);
            break;
         case USER_TOADS_HAMMER_REQUEST:
            EnchantHandler.userToadsHammerRequest(slea, c);
            break;
         case MANNEQUIN_REQUEST:
            PlayerHandler.mannequinRequest(slea, c);
            break;
         case DEBUFF_OBJ_COLLISION:
            PlayerHandler.debuffObjCollision(slea, c);
            break;
         case MOB_CREATE_FIRE_WALK:
            MobHandler.mobCreateFireWalk(slea, c);
            break;
         case ZERO_TAG:
            ZeroHandler.zeroTag(slea, c);
            break;
         case ZERO_LAST_ASSIST_STATE:
            ZeroHandler.zeroLastAssisState(slea, c);
            break;
         case ZERO_SHARE_CASH_EQUIP_PART:
            ZeroHandler.zeroShareCashEquipPart(slea, c);
            break;
         case CREATE_AURA_BY_GRENADE:
            PlayerHandler.userCreateAuraByGrenade(slea, c);
            break;
         case YETI_BOOSTER_REQUEST:
            YetiHandler.yetiBoosterRequest(slea, c);
            break;
         case HUGE_ROLL:
            PinkBeanHandler.roll_upgrade(slea, c);
            break;
         case YOYO_COUNT_UP:
            PinkBeanHandler.yoyoStackUpRequest(slea, c);
            break;
         case OPEN_EVENT_LIST_UI:
            UserInterfaceHandler.Open_EventList_Request(slea, c);
            break;
         case YETIXPINKBEAN_STEPUP_MISSION_CHECK:
            UserInterfaceHandler.YetixPinkbeanStepUpCheck(slea, c);
            break;
         case INHERITANCE_INFO_REQUEST:
            ZeroHandler.inheritanceInfoRequest(slea, c);
            break;
         case INHERITANCE_UPGRADE_REQUEST:
            ZeroHandler.inheritanceUpgradeRequest(slea, c);
            break;
         case EGO_EQUIP_CREATE_UPGRADE_ITEM_COST_REQUEST:
            ZeroHandler.egoEquipCreateUpgradeItemCostRequest(slea, c);
            break;
         case EGO_EQUIP_GAUGE_COMPLETE_RETURN:
            ZeroHandler.egoEquipGaugeCompleteReturn(slea, c);
            break;
         case EGO_EQUIP_CHECK_UPDATE_ITEM_REQUEST:
            ZeroHandler.egoEquipCheckUpgradeItemRequest(slea, c);
            break;
         case EGO_EQUIP_TALK_REQUEST:
            ZeroHandler.egoEquipTalkRequest(slea, c);
            break;
         case OPEN_RING_OF_ALICIA:
            InventoryHandler.Open_RingBox(slea, c, c.getPlayer());
            break;
         case ERDA_SPECTRUM_BOMB:
            ErdaSpectrumHandler.ErdaSpectrumBombAction(slea, c);
            break;
         case ERDA_SPECTRUM_OBJECT_INTERACTION:
            ErdaSpectrumHandler.ErdaSpectrumTouchObject(slea, c);
            break;
         case ERDA_SPECTRUM_PUCK_OBJECT_IN_AREA:
            ErdaSpectrumHandler.ErdaSpectrumPuckInArea(slea, c);
            break;
         case ATTACH_REACTOR:
            SpiritSaviorHandler.AttachReactor(slea, c);
            break;
         case SPIRIT_SAVIOR_GOAL:
            SpiritSaviorHandler.SpiritSaviorGoalArea(slea, c);
            break;
         case LARA_DRAGON_VEIN_CREATE_REQUEST:
            PlayerHandler.laraDragonVeinCreateRequest(slea, c);
            break;
         case VCORE_MAKE_JAMSTONE_REQUEST:
            VMatrixHandler.VCoreMakeJamStoneRequest(slea, c);
            break;
         case TANGYOON_KITCHEN_PICK_UP_FOOD:
            TangyoonKitchenHandler.PickUpFood(slea, c);
            break;
         case TANGYOON_KITCHEN_CHANGE_STATUS:
            TangyoonKitchenHandler.ReadyPickUpFood(slea, c);
            break;
         case EVENTMAP_AFK_AUTO_EXIT:
            TangyoonKitchenHandler.EventMapAutoExit(slea, c);
            break;
         case MULTI_SOCCER_TRY_SHOOT:
            PlayersHandler.onMultiSoccerTryDoingShoot(slea, c);
            break;
         case USER_LUCKY_ITEM_USE_REQUEST:
            ZeroHandler.userLuckyItemUseRequest(slea, c);
            break;
         case PICK_UP_REACTOR:
            TangyoonKitchenHandler.PickUpReator(slea, c);
            break;
         case PUT_PUZZLE_REACTOR:
            TangyoonKitchenHandler.PutPuzzleReactor(slea, c);
            break;
         case RUN_STEP_UP_SCRIPT:
            StepUpHandler.RunScript(slea, c);
            break;
         case STEP_UP_ARRIVED_CHARACTER:
            StepUpHandler.ArrivedCharacter(slea, c);
            break;
         case SWITCHING_FLAGRACE_CRAMPONS_SHOES:
            FlagRaceHandler.Switching_Shoes(slea, c);
            break;
         case NX_CHARGE:
            CashShopHandler.NxCharge(c);
            break;
         case QUICKMOVE_REQUEST:
            PlayerHandler.quickMoveRequest(slea, c);
            break;
         case SELECT_RETURN_SCROLL:
            InventoryHandler.SelectReturnScroll(slea, c);
            break;
         case DAZZLE_HIT:
            PlayerHandler.dazzleHit(slea, c);
            break;
         case BOUNCE_ATTACK_COLLISION:
            PlayerHandler.bounceAttackCollision(slea, c);
            break;
         case SEREN_DELAY_END:
            PlayerHandler.serenDelayEnd(slea, c);
            break;
         case HOLY_UNITY_CHANGE_TARGET:
            PlayerHandler.HolyUnityChangeTarget(slea, c);
            break;
         case REINCARNATION_ACCEPT:
            PlayerHandler.reincarnationAccept(slea, c);
            break;
         case ZERO_COMBAT_RECOVERY:
            ZeroHandler.zeroCombatRecovery(slea, c);
            break;
         case GUARDIAN_CRYSTAL:
            MobHandler.guardianWaveCrystal(slea, c);
            break;
         case GUARDIAN_WAVE_ENTER_PORTAL:
            MobHandler.guardianWaveEnterPortal(slea, c);
            break;
         case GUARDIAN_ANGEL_SLIME:
            MobHandler.guardianAngelSlime(slea, c);
            break;
         case USE_HASTE_BOX:
            InventoryHandler.useHasteBox(slea, c);
            break;
         case USE_HASTE_BOOSTER:
            InventoryHandler.useHasteBooster(slea, c);
            break;
         case BLACK_LIST:
            PlayerHandler.handleBlackList(slea, c);
            break;
         case MEMORY_CHOICE:
            PlayerHandler.setMemoryChoice(slea, c);
            break;
         case POISON_REGION:
            PlayerHandler.addPoisonRegion(slea, c);
            break;
         case GUARD_STACK_REQUEST:
            PlayerHandler.guardStackRequest(slea, c);
            break;
         case DEMON_AVENGER_INIT:
            PlayerHandler.demonAvengerInit(c);
            break;
         case DEMON_FRENZY_REQUEST:
            PlayerHandler.demonFrenzyRequest(c);
            break;
         case SUMMON_FIX:
            SummonHandler.fixSummonRequest(slea, c);
            break;
         case OPEN_UI_INFO:
            PlayerHandler.openUIInfo(slea, c);
            break;
         case CHATTING_SHORTCUT:
            PlayerHandler.saveChattingShortCut(slea, c);
            break;
         case SKILL_ALARM:
            SkillEncode.skillAlarm(slea, c);
            break;
         case LINK_SKILL_PRESET:
            LinkSkill.linkSkillPreset(slea, c);
            break;
         case SET_LINK_SKILL:
            LinkSkill.setLinkSkill(slea, c);
            break;
         case PARTY_AURA_BUFF_SET:
            PlayersHandler.setPartyAuraBuff(slea, c);
            break;
         case DODGE_SKILL_READY:
            PlayerHandler.setDodgeSkill(slea, c);
            break;
         case REWARD_MONSTER_COLLECTION:
            MonsterCollectionHandler.RewardMonsterCollection(slea, c);
            break;
         case EXPLORE_MONSTER_COLLECTION:
            MonsterCollectionHandler.ExploreMonsterCollection(slea, c);
            break;
         case CANCEL_EXPLORE_MONSTER_COLLECTION:
            MonsterCollectionHandler.CancelExploreMonsterCollection(slea, c);
            break;
         case SKILL_EFFECT_REQUEST:
            PlayerHandler.skillEffectRequest(slea, c);
            break;
         case ACHIEVEMENT_REQUEST:
            AchievementHandler.AchienvementAction(slea, c);
            break;
         case KALOS_ACTION_REQUEST:
            MobHandler.kalosActionRequest(slea, c);
            break;
         case KARING_MATCHING_REQUEST:
            MobHandler.karingMatchRequest(slea, c);
            break;
         case MEMO_REQUEST:
            PlayerHandler.memoRequest(slea, c);
            break;
         case CHANGE_WEAPON_MOTION:
            PlayerHandler.changeWeaponMotion(slea, c);
            break;
         case SHOW_MEDAL:
            PlayerHandler.showMedal(slea, c);
            break;
         case SHOW_ITEMEFFECT:
            PlayerHandler.showItemEffect(slea, c);
            break;
         case CASH_CODY_PRESET_OPERATION:
            PlayerHandler.handleCashCodyPreset(slea, c);
            break;
         case HOWLING_GALE_STACK_REQUEST:
            PlayerHandler.howlingGaleStackRequest(slea, c);
            break;
         case PHALANX_REQUREST:
            PlayerHandler.phalanxRequest(slea, c.getPlayer());
            break;
         case SPEED_HACK_CHECK_REQUEST:
            int unk1 = slea.readInt();
            slea.skip(4);
            int unk2 = slea.readByte();
            if (serverType == ServerType.GAME) {
               MapleCharacter player = c.getPlayer();
               if (player != null) {
                  long lastCheckTime = player.getLastSpeedHackCheckTime();
                  long delta = System.currentTimeMillis() - lastCheckTime;
                  player.setLastSpeedHackCheckTime(System.currentTimeMillis());
                  if (lastCheckTime != 0L) {
                     double second = Double.parseDouble(String.format("%.1f", delta / 1000.0));
                     if (second < 50.0) {
                        player.setSpeedHackCheckCount(player.getSpeedHackCheckCount() + 1);
                        if (player.getSpeedHackCheckCount() >= 3) {
                           StringBuilder sb = new StringBuilder("스피드 핵 사용 의심 (");
                           sb.append("계정 : ");
                           sb.append(c.getAccountName());
                           sb.append(", 캐릭터 : ");
                           sb.append(c.getPlayer().getName());
                           sb.append(", 사용 배수 : " + (int)(60.0 / second) + "배");
                           sb.append("))");
                           LoggingManager.putLog(new HackLog(HackLogType.SpeedHack.getType(), c.getPlayer(), sb));
                        }
                     }
                  }
               }
            }
            break;
         case REMOVE_DIVINE_JUDGEMENT_REQUEST:
            PlayerHandler.removeDivineJudgementRequest(slea, c);
            break;
         case BOSS_CONTENTS_HELP:
            UserInterfaceHandler.bossContentsHelp(slea, c);
            break;
         case CODY_VOTE_CHAIR_REQUEST:
            PlayerHandler.updateCodyVoteChair(slea, c);
            break;
         case UI_OPTION_SAVE_REQUEST:
            PlayerHandler.uiOptionSaveRequest(slea, c);
            break;
         case dool1phaseHandler:
            Field_BossGoongiPhase.unkHandler(slea, c);
            break;
         case GOONGI_ACTION:
            Field_BossGoongiPhase.GoongiTestHandler(slea, c);
      }
   }
}
