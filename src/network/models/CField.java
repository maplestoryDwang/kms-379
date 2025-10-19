package network.models;

import constants.GameConstants;
import constants.ServerConstants;
import constants.devtempConstants.MapleDailyGift;
import constants.devtempConstants.MapleDailyGiftInfo;
import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.auction.AuctionItemPackage;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.game.processors.PlayerInteractionHandler;
import objects.MapleDueyActions;
import objects.androids.Android;
import objects.context.ReportLogEntry;
import objects.context.SpecialSunday;
import objects.context.guild.Guild;
import objects.context.guild.alliance.Alliance;
import objects.context.waitqueue.WaitQueueError;
import objects.context.waitqueue.WaitQueueResult;
import objects.context.waitqueue.WaitQueueType;
import objects.fields.CustomChair;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.ForceAtom_Parallel;
import objects.fields.Grenade;
import objects.fields.MapleDynamicFoothold;
import objects.fields.MapleFoothold;
import objects.fields.MapleMapObject;
import objects.fields.MapleNodes;
import objects.fields.RandomPortal;
import objects.fields.SecondAtom;
import objects.fields.Wreckage;
import objects.fields.child.dojang.DojangMyRanking;
import objects.fields.child.dojang.DojangRanking;
import objects.fields.child.dojang.DojangRankingEntry;
import objects.fields.events.MapleSnowball;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.Dragon;
import objects.fields.gameobject.Drop;
import objects.fields.gameobject.FieldAttackObj;
import objects.fields.gameobject.OpenGate;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.RuneStone;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.NpcTalk;
import objects.fields.gameobject.lifes.ScriptFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleInRowInfo;
import objects.fields.obstacle.ObstacleRadialInfo;
import objects.item.DamageSkinSaveInfo;
import objects.item.Equip;
import objects.item.IntensePowerCrystalData;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.item.MapleRing;
import objects.movepath.LifeMovementFragment;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.QuestEx;
import objects.shop.MapleShop;
import objects.summoned.Summoned;
import objects.users.MapleCabinet;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleKeyLayout;
import objects.users.MapleTrade;
import objects.users.enchant.GradeRandomOption;
import objects.users.skills.CrystalGate;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.PsychicArea;
import objects.users.skills.PsychicLock;
import objects.users.skills.Skill;
import objects.users.skills.SkillEncode;
import objects.users.skills.SkillFactory;
import objects.users.skills.SkillMacro;
import objects.users.skills.TemporarySkill;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.HexTool;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Triple;
import scripting.GameObjectType;
import scripting.ScriptMessageFlag;
import scripting.ScriptMessageType;

public class CField {
   public static final byte[] BlackLabel(String msg, int delay, int textspeed, int type, int x, int y, int type1, int type2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
      mplew.write(61);
      mplew.writeMapleAsciiString(msg);
      mplew.writeInt(delay);
      mplew.writeInt(textspeed);
      mplew.writeInt(type);
      mplew.writeInt(x);
      mplew.writeInt(y);
      mplew.writeInt(type1);
      mplew.writeInt(type2);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeShort(0);
      mplew.writeLong(0L);
      return mplew.getPacket();
   }

   public static byte[] showSpineScreen(
      boolean isBinary, boolean isLoop, boolean isPostRender, String path, String animationName, int endDelay, boolean useKey, String key
   ) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(32);
      mplew.write(isBinary);
      mplew.write(isLoop);
      mplew.write(isPostRender);
      mplew.writeInt(endDelay);
      mplew.writeMapleAsciiString(path);
      mplew.writeMapleAsciiString(animationName);
      mplew.writeMapleAsciiString("");
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(useKey);
      if (useKey) {
         mplew.writeMapleAsciiString(key);
      }

      return mplew.getPacket();
   }

   public static byte[] ShowEventSkillEffect(int skillId, int duration) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.GIFT_SHOW_TIME.getValue());
      mplew.writeInt(skillId);
      mplew.writeInt(duration);
      return mplew.getPacket();
   }

   public static byte[] arkEndlessBeastReduce(int reduce) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ENDLESSLY_STARVING_BEAST_REDUCE.getValue());
      mplew.writeInt(reduce);
      return mplew.getPacket();
   }

   public static byte[] getPacketFromHexString(String hex) {
      return HexTool.getByteArrayFromHexString(hex);
   }

   public static byte[] getServerIP(MapleClient c, int port, int clientId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      mplew.write(0);
      if (c.getTempIP().length() > 0) {
         for (String s : c.getTempIP().split(",")) {
            mplew.write(Integer.parseInt(s));
         }
      } else {
         mplew.encodeBuffer(ServerConstants.getGatewayIP());
      }

      mplew.writeShort(port);
      mplew.writeInt(clientId);
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.write(1);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.write(0);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      return mplew.getPacket();
   }

   public static byte[] getChannelChange(MapleClient c, int port) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
      mplew.write(1);
      if (c.getTempIP().length() > 0) {
         for (String s : c.getTempIP().split(",")) {
            mplew.write(Integer.parseInt(s));
         }
      } else {
         mplew.encodeBuffer(ServerConstants.getGatewayIP());
      }

      mplew.writeShort(port);
      return mplew.getPacket();
   }

   public static byte[] getPVPType(int type, List<Pair<Integer, String>> players1, int team, boolean enabled, int lvl) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_TYPE.getValue());
      mplew.write(type);
      mplew.write(lvl);
      mplew.write(enabled ? 1 : 0);
      mplew.write(0);
      if (type > 0) {
         mplew.write(team);
         mplew.writeInt(players1.size());

         for (Pair<Integer, String> pl : players1) {
            mplew.writeInt(pl.left);
            mplew.writeMapleAsciiString(pl.right);
            mplew.writeShort(2660);
         }
      }

      return mplew.getPacket();
   }

   public static byte[] getPVPTransform(int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_TRANSFORM.getValue());
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] getPVPDetails(List<Pair<Integer, Integer>> players) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_DETAILS.getValue());
      mplew.write(1);
      mplew.write(0);
      mplew.writeInt(players.size());

      for (Pair<Integer, Integer> pl : players) {
         mplew.writeInt(pl.left);
         mplew.write(pl.right);
      }

      return mplew.getPacket();
   }

   public static byte[] enablePVP(boolean enabled) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_ENABLED.getValue());
      mplew.write(enabled ? 1 : 2);
      return mplew.getPacket();
   }

   public static byte[] getPVPScore(int score, boolean kill) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_SCORE.getValue());
      mplew.writeInt(score);
      mplew.write(kill ? 1 : 0);
      System.out.print("피비피스코어" + mplew);
      return mplew.getPacket();
   }

   public static byte[] getPVPResult(List<Pair<Integer, MapleCharacter>> flags, int exp, int winningTeam, int playerTeam) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_RESULT.getValue());
      mplew.writeInt(flags.size());

      for (Pair<Integer, MapleCharacter> f : flags) {
         mplew.writeInt(f.right.getId());
         mplew.writeMapleAsciiString(f.right.getName());
         mplew.writeInt(f.left);
         mplew.writeShort(f.right.getTeam() + 1);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.writeZeroBytes(24);
      mplew.writeInt(exp);
      mplew.write(0);
      mplew.writeShort(100);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(winningTeam);
      mplew.write(playerTeam);
      System.out.print("리절트" + mplew);
      return mplew.getPacket();
   }

   public static byte[] getPVPTeam(List<Pair<Integer, String>> players) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_TEAM.getValue());
      mplew.writeInt(players.size());

      for (Pair<Integer, String> pl : players) {
         mplew.writeInt(pl.left);
         mplew.writeMapleAsciiString(pl.right);
         mplew.writeShort(2660);
      }

      return mplew.getPacket();
   }

   public static byte[] getPVPScoreboard(List<Pair<Integer, MapleCharacter>> flags, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_SCOREBOARD.getValue());
      mplew.writeInt(flags.size());

      for (Pair<Integer, MapleCharacter> f : flags) {
         mplew.writeInt(f.right.getId());
         mplew.writeInt(f.left);
         mplew.write(type == 0 ? 0 : f.right.getTeam() + 1);
         mplew.writeShort(0);
      }

      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] getPVPPoints(int p1, int p2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_POINTS.getValue());
      mplew.writeInt(p1);
      mplew.writeInt(p2);
      return mplew.getPacket();
   }

   public static byte[] getPVPKilled(String lastWords) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_KILLED.getValue());
      mplew.writeMapleAsciiString(lastWords);
      return mplew.getPacket();
   }

   public static byte[] getPVPMode(int mode) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_MODE.getValue());
      mplew.write(mode);
      System.out.print("PVP모드" + mplew);
      return mplew.getPacket();
   }

   public static byte[] getPVPIceHPBar(int hp, int maxHp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_ICEKNIGHT.getValue());
      mplew.writeInt(hp);
      mplew.writeInt(maxHp);
      return mplew.getPacket();
   }

   public static byte[] getCaptureFlags(Field map) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CAPTURE_FLAGS.getValue());
      mplew.encodeRect(map.getArea(0));
      mplew.writeInt(((Point)map.getGuardians().get(0).left).x);
      mplew.writeInt(((Point)map.getGuardians().get(0).left).y);
      mplew.encodeRect(map.getArea(1));
      mplew.writeInt(((Point)map.getGuardians().get(1).left).x);
      mplew.writeInt(((Point)map.getGuardians().get(1).left).y);
      return mplew.getPacket();
   }

   public static byte[] getCapturePosition(Field map) {
      PacketEncoder mplew = new PacketEncoder();
      Point p1 = map.getPointOfItem(2910000);
      Point p2 = map.getPointOfItem(2910001);
      mplew.writeShort(SendPacketOpcode.CAPTURE_POSITION.getValue());
      mplew.write(p1 == null ? 0 : 1);
      if (p1 != null) {
         mplew.writeInt(p1.x);
         mplew.writeInt(p1.y);
      }

      mplew.write(p2 == null ? 0 : 1);
      if (p2 != null) {
         mplew.writeInt(p2.x);
         mplew.writeInt(p2.y);
      }

      return mplew.getPacket();
   }

   public static byte[] resetCapture() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CAPTURE_RESET.getValue());
      return mplew.getPacket();
   }

   public static byte[] getMacros(SkillMacro[] macros) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SKILL_MACRO.getValue());
      int count = 0;

      for (int i = 0; i < 5; i++) {
         if (macros[i] != null) {
            count++;
         }
      }

      mplew.write(count);

      for (int ix = 0; ix < 5; ix++) {
         SkillMacro macro = macros[ix];
         if (macro != null) {
            mplew.writeMapleAsciiString(macro.getName());
            mplew.write(macro.getShout());
            mplew.writeInt(macro.getSkill1());
            mplew.writeInt(macro.getSkill2());
            mplew.writeInt(macro.getSkill3());
         }
      }

      return mplew.getPacket();
   }

   public static byte[] getCharInfo(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
      mplew.writeInt(chr.getClient().getChannel() - 1);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(chr.getAndIncrementTransferFieldCount());
      mplew.writeInt(chr.getMap().getFieldType());
      mplew.write(0);
      mplew.writeInt(Math.abs(chr.getMap().getWidth()));
      mplew.writeInt(Math.abs(chr.getMap().getHeight()));
      mplew.write(true);
      mplew.writeShort(ServerConstants.serverMessage.length() > 0 ? 1 : 0);
      if (ServerConstants.serverMessage.length() > 0) {
         mplew.writeMapleAsciiString(ServerConstants.serverMessage);
         mplew.writeMapleAsciiString(ServerConstants.serverMessage);
      }

      int seed1 = Randomizer.nextInt();
      int seed2 = Randomizer.nextInt();
      int seed3 = Randomizer.nextInt();
      chr.getCalcDamage().SetSeed(seed1, seed2, seed3);
      mplew.writeInt(seed1);
      mplew.writeInt(seed2);
      mplew.writeInt(seed3);
      PacketHelper.addCharacterInfo(mplew, chr, -1L);
      boolean accountProtection = true;
      mplew.write(accountProtection);
      if (accountProtection) {
         boolean protectionMode = false;
         mplew.write(protectionMode);
         if (protectionMode) {
            mplew.writeInt(4);
         }

         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.write(0);
         mplew.writeLong(PacketHelper.getTime(-2L));
      }

      boolean unk1134 = false;
      mplew.write(unk1134);
      if (unk1134) {
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.write(chr.isTransferWhiteFadeOut());
      mplew.write(0);
      mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
      mplew.writeInt(100);
      mplew.write(false);
      mplew.write(false);
      mplew.write(GameConstants.isPhantom(chr.getJob()) ? 0 : 1);
      mplew.write(false);
      int fieldId = chr.getMapId();
      if (isBanbanBaseField(fieldId)) {
         mplew.write(0);
      }

      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      boolean c = true;
      mplew.write(c);
      if (c) {
         mplew.writeInt(-1);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(999999999);
         mplew.writeInt(999999999);
         mplew.writeMapleAsciiString("");
      }

      mplew.write(SpecialSunday.isActive && new Date().getDay() == 0);
      if (SpecialSunday.isActive && new Date().getDay() == 0) {
         mplew.writeMapleAsciiString("UI/UIWindowEvent.img/sundayMaple");
         mplew.writeMapleAsciiString(SpecialSunday.sundayContext);
         mplew.writeMapleAsciiString(SpecialSunday.sundayTitle);
         mplew.writeInt(60);
         mplew.writeInt(220);
      }

      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(1);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   private static boolean isBanbanBaseField(int fieldId) {
      return fieldId / 10 == 10520011 || fieldId / 10 == 10520051 || fieldId == 105200519;
   }

   public static byte[] getWarpToMap(Field to, int spawnPoint, MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
      mplew.writeInt(chr.getClient().getChannel() - 1);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(chr.getAndIncrementTransferFieldCount());
      mplew.writeInt(to.getFieldType());
      mplew.write(0);
      mplew.writeInt(Math.abs(to.getWidth()));
      mplew.writeInt(Math.abs(to.getHeight()));
      mplew.write(0);
      mplew.writeShort(0);
      mplew.write(0);
      mplew.writeInt(to.getId());
      mplew.write(spawnPoint);
      if (!chr.isAlive()) {
         mplew.writeInt(1);
      } else {
         mplew.writeInt(chr.getStat().getHp());
      }

      boolean unk1134 = false;
      mplew.write(unk1134);
      if (unk1134) {
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.write(chr.isTransferWhiteFadeOut());
      mplew.write(chr.isTransferFieldOverlap());
      mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
      mplew.writeInt(100);
      mplew.write(0);
      mplew.write(0);
      mplew.write(GameConstants.isPhantom(chr.getJob()) ? 0 : 1);
      mplew.write(0);
      int fieldId = to.getId();
      if (isBanbanBaseField(fieldId)) {
         mplew.write(0);
      }

      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      boolean unk = true;
      mplew.write(unk);
      if (unk) {
         mplew.writeInt(-1);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(999999999);
         mplew.writeInt(999999999);
         mplew.writeMapleAsciiString("");
      }

      boolean unk2 = false;
      mplew.write(unk2);
      if (unk) {
         mplew.writeMapleAsciiString("");
         mplew.writeMapleAsciiString("");
         mplew.writeMapleAsciiString("");
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] serverBlocked(int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVER_BLOCKED.getValue());
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] channelBlocked(int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHANNEL_BLOCKED.getValue());
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] pvpBlocked(int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_BLOCKED.getValue());
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] showEquipEffect() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
      return mplew.getPacket();
   }

   public static byte[] showEquipEffect(int team) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
      mplew.writeShort(team);
      return mplew.getPacket();
   }

   public static byte[] multiChat(
      MapleCharacter chr, String chattext, int mode, Item item, String itemName, int achievementID, long achievementTime, ReportLogEntry report
   ) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(item == null && achievementID <= 0 ? SendPacketOpcode.MULTICHAT.getValue() : SendPacketOpcode.MULTICHAT_ITEM.getValue());
      mplew.write(mode);
      mplew.writeInt(chr.getAccountID());
      mplew.writeInt(chr.getId());
      mplew.writeMapleAsciiString(chr.getName());
      mplew.writeMapleAsciiString(chattext);
      report.encode(mplew);
      PacketHelper.onChatBonusPacket(mplew, achievementID > 0 ? 2 : (item != null ? 1 : 0), item, itemName, achievementID, achievementTime);
      return mplew.getPacket();
   }

   public static byte[] getFindReplyWithCS(String target, boolean buddy) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
      mplew.write(buddy ? 72 : 9);
      mplew.writeMapleAsciiString(target);
      mplew.write(2);
      mplew.writeInt(-1);
      return mplew.getPacket();
   }

   public static byte[] getWhisper(
      String sender, int channel, String text, Item item, String itemName, int achievementID, long achievementTime, ReportLogEntry report
   ) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
      mplew.write(18);
      mplew.writeInt(0);
      mplew.writeMapleAsciiString(sender);
      mplew.writeInt(0);
      mplew.write(channel - 1);
      mplew.write(0);
      mplew.writeMapleAsciiString(text);
      report.encode(mplew);
      PacketHelper.onChatBonusPacket(mplew, item != null ? 1 : 0, item, itemName, achievementID, achievementTime);
      return mplew.getPacket();
   }

   public static byte[] getWhisperReply(String target, byte reply) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
      mplew.write(10);
      mplew.writeMapleAsciiString(target);
      mplew.write(reply);
      return mplew.getPacket();
   }

   public static byte[] getFindReplyWithMap(String target, int mapid, boolean buddy) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
      mplew.write(buddy ? 72 : 9);
      mplew.writeMapleAsciiString(target);
      mplew.write(1);
      mplew.writeInt(mapid);
      mplew.writeZeroBytes(8);
      return mplew.getPacket();
   }

   public static byte[] getFindReply(String target, int channel, boolean buddy) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
      mplew.write(buddy ? 72 : 9);
      mplew.writeMapleAsciiString(target);
      mplew.write(3);
      mplew.writeInt(channel - 1);
      return mplew.getPacket();
   }

   public static final byte[] MapEff(String path) {
      return environmentChange(path, 19);
   }

   public static final byte[] MapNameDisplay(int mapid) {
      return environmentChange("maplemap/enter/" + mapid, 19);
   }

   public static final byte[] Aran_Start() {
      return environmentChange("Aran/balloon", 4);
   }

   public static byte[] musicChange(String song) {
      return environmentChange(song, 7);
   }

   public static byte[] showEffect(String effect) {
      return environmentChange(effect, 4);
   }

   public static byte[] playSound(String sound) {
      return environmentChange(sound, 5);
   }

   public static byte[] playSound(String sound, int option) {
      return environmentChange(sound, 5, option);
   }

   public static byte[] environmentChange(String env, int mode) {
      return environmentChange(env, mode, 0);
   }

   public static byte[] environmentChange(String env, int mode, int option) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      if (mode >= 5) {
         mode += 2;
      }

      mplew.write(mode);
      mplew.writeMapleAsciiString(env);
      mplew.writeInt(option);
      mplew.writeZeroBytes(20);
      return mplew.getPacket();
   }

   public static byte[] mapEffect(String eff) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(18);
      mplew.writeMapleAsciiString(eff);
      return mplew.getPacket();
   }

   public static byte[] getSpineScreen(int binary, int loop, int postRender, int endDelay, String path, String animationName, String key) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(32);
      mplew.write(binary);
      mplew.write(loop);
      mplew.write(postRender);
      mplew.writeInt(endDelay);
      mplew.writeMapleAsciiString(path);
      mplew.writeMapleAsciiString(animationName);
      mplew.write(key != null);
      if (key != null) {
         mplew.writeMapleAsciiString(key);
      }

      return mplew.getPacket();
   }

   public static byte[] showHasteEffect(boolean start) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
      packet.writeInt(EliteState.HasteEvent.getType());
      packet.writeInt(start ? 1 : 0);
      packet.writeInt(2);
      packet.writeMapleAsciiString("Effect/EventEffect.img/HasteBooster/screenEff");
      return packet.getPacket();
   }

   public static byte[] environmentChange(String env, int mode, int value, int value2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      if (mode >= 5) {
         mode += 2;
      }

      mplew.write(mode);
      mplew.writeMapleAsciiString(env);
      mplew.writeInt(value);
      mplew.writeInt(value2);
      mplew.writeZeroBytes(20);
      return mplew.getPacket();
   }

   public static byte[] makeEffectScreen(String path) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      packet.write(4);
      packet.writeMapleAsciiString(path);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] bgmVolume(int volume, int fadeDuration) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(11);
      mplew.writeInt(volume);
      mplew.writeInt(fadeDuration);
      return mplew.getPacket();
   }

   public static byte[] overlap_Detail(int fadeIn, int wait, int fadeOut, int u) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(27);
      mplew.writeInt(fadeIn);
      mplew.writeInt(wait);
      mplew.writeInt(fadeOut);
      mplew.write(u);
      return mplew.getPacket();
   }

   public static byte[] remove_Overlap_Detail(int fadeOut) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(28);
      mplew.writeInt(fadeOut);
      return mplew.getPacket();
   }

   public static byte[] trembleEffect(int type, int delay) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(1);
      mplew.write(type);
      mplew.writeInt(delay);
      return mplew.getPacket();
   }

   public static byte[] blind(int a1, int a2, int a3, int a4, int a5, int a6, int a7) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(23);
      mplew.write(a1);
      mplew.writeShort(a2);
      mplew.writeShort(a3);
      mplew.writeShort(a4);
      mplew.writeShort(a5);
      mplew.writeInt(a6);
      mplew.writeInt(a7);
      return mplew.getPacket();
   }

   public static byte[] getOnOff(int term, String key, String path, int rx, int ry, int rz, int org, int postRender, int unk1, int unk2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(25);
      mplew.write(0);
      mplew.writeInt(term);
      mplew.writeMapleAsciiString(key);
      mplew.writeInt(rx);
      mplew.writeInt(ry);
      mplew.writeInt(rz);
      mplew.writeMapleAsciiString(path);
      mplew.writeInt(org);
      mplew.write(postRender);
      mplew.writeInt(unk1);
      mplew.write(unk2);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] getOnOffD(int term, String key, int dx, int dy) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(25);
      mplew.write(1);
      mplew.writeInt(term);
      mplew.writeMapleAsciiString(key);
      mplew.writeInt(dx);
      mplew.writeInt(dy);
      return mplew.getPacket();
   }

   public static byte[] getOnOffFade(int term, String key, int unk) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(25);
      mplew.write(2);
      mplew.writeInt(term);
      mplew.writeMapleAsciiString(key);
      mplew.write(unk);
      return mplew.getPacket();
   }

   public static byte[] getSetMapTaggedObjectAnimation(String tag, int value) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SET_MAP_TAGGED_OBJECT_ANIMATION.getValue());
      mplew.writeInt(1);
      mplew.writeMapleAsciiString(tag);
      mplew.writeInt(value);
      return mplew.getPacket();
   }

   public static byte[] environmentMove(String env, int mode) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOVE_ENV.getValue());
      mplew.writeMapleAsciiString(env);
      mplew.writeInt(mode);
      return mplew.getPacket();
   }

   public static byte[] getUpdateEnvironment(Field map) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_ENV.getValue());
      mplew.writeInt(map.getEnvironment().size());

      for (Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
         mplew.writeMapleAsciiString(mp.getKey());
         mplew.writeInt(mp.getValue());
      }

      return mplew.getPacket();
   }

   public static byte[] startMapEffect(String msg, int itemid, boolean active) {
      return startMapEffect(msg, itemid, active, 0);
   }

   public static byte[] startMapEffect(String msg, int itemid, boolean active, int seconds) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BLOW_WEATHER.getValue());
      mplew.writeInt(itemid);
      if (active) {
         mplew.writeMapleAsciiString(msg);
         mplew.writeInt(seconds);
         mplew.write(0);
      }

      return mplew.getPacket();
   }

   public static byte[] removeMapEffect() {
      return startMapEffect(null, 0, false);
   }

   public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OX_QUIZ.getValue());
      mplew.write(askQuestion ? 1 : 0);
      mplew.write(questionSet);
      mplew.writeShort(questionId);
      return mplew.getPacket();
   }

   public static byte[] showEventInstructions() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] getPVPClock(int type, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(3);
      mplew.write(type);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] getClock(int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(2);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] getTimezoneClock(int sec, int direction) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(5);
      mplew.write(direction);
      mplew.writeInt(sec);
      return mplew.getPacket();
   }

   public static byte[] getClockTime(int hour, int min, int sec) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(1);
      mplew.write(hour);
      mplew.write(min);
      mplew.write(sec);
      return mplew.getPacket();
   }

   public static byte[] getStopwatch(int milli) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(6);
      mplew.writeInt(milli);
      return mplew.getPacket();
   }

   public static byte[] getStopClock(int seconds) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(1);
      mplew.writeInt(seconds);
      return mplew.getPacket();
   }

   public static byte[] getStartClock(int seconds) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(0);
      mplew.writeInt(seconds);
      return mplew.getPacket();
   }

   public static byte[] getStartDojangClock(int seconds, int playTime) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(8);
      mplew.writeInt(seconds);
      mplew.writeInt(playTime);
      return mplew.getPacket();
   }

   public static byte[] getToggleDojangClock(boolean pause, int seconds, int pauseSeconds) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.write(7);
      mplew.write(pause);
      mplew.writeInt(seconds);
      mplew.writeInt(pauseSeconds);
      return mplew.getPacket();
   }

   public static byte[] tangyoonClock(int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] boatPacket(int effect, int mode) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BOAT_MOVE.getValue());
      mplew.write(effect);
      mplew.write(mode);
      return mplew.getPacket();
   }

   public static byte[] stopClock() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.STOP_CLOCK.getValue());
      return mplew.getPacket();
   }

   public static byte[] showAriantScoreBoard() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ARIANT_SCOREBOARD.getValue());
      return mplew.getPacket();
   }

   public static byte[] sendPyramidUpdate(int amount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PYRAMID_UPDATE.getValue());
      mplew.writeInt(amount);
      return mplew.getPacket();
   }

   public static byte[] sendPyramidResult(byte rank, int amount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PYRAMID_RESULT.getValue());
      mplew.write(rank);
      mplew.writeInt(amount);
      return mplew.getPacket();
   }

   public static byte[] getMovingPlatforms(Field map) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOVE_PLATFORM.getValue());
      mplew.writeInt(map.getPlatforms().size());

      for (MapleNodes.MaplePlatform mp : map.getPlatforms()) {
         mplew.writeMapleAsciiString(mp.name);
         mplew.writeInt(mp.start);
         mplew.writeInt(mp.SN.size());

         for (int x = 0; x < mp.SN.size(); x++) {
            mplew.writeInt(mp.SN.get(x));
         }

         mplew.writeInt(mp.speed);
         mplew.writeInt(mp.x1);
         mplew.writeInt(mp.x2);
         mplew.writeInt(mp.y1);
         mplew.writeInt(mp.y2);
         mplew.writeInt(mp.x1);
         mplew.writeInt(mp.y1);
         mplew.writeShort(mp.r);
      }

      return mplew.getPacket();
   }

   public static byte[] sendPVPMaps() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_INFO.getValue());
      mplew.write(3);

      for (int i = 0; i < 20; i++) {
         mplew.writeInt(10);
      }

      mplew.writeZeroBytes(124);
      mplew.writeShort(150);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] achievementRatio(int amount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ACHIEVEMENT_RATIO.getValue());
      mplew.writeInt(amount);
      return mplew.getPacket();
   }

   public static byte[] stackEventGauge(String path) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.STACK_EVENT_GAUGE.getValue());
      mplew.write(0);
      mplew.writeInt(1241);
      mplew.writeMapleAsciiString(path);
      return mplew.getPacket();
   }

   public static byte[] spawnPlayerMapobject(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
      mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      mplew.writeInt(chr.getAccountID());
      int playerID = chr.getId();
      if (playerID % 31 == 0) {
         mplew.writeInt(0);
      }

      mplew.writeInt(chr.getId());
      Guild gs = Center.Guild.getGuild(chr.getGuildId());
      mplew.writeInt(gs != null ? gs.getId() : 0);
      mplew.writeInt(chr.getLevel());
      mplew.writeMapleAsciiString(chr.getName());
      MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
      if (ultExplorer != null && ultExplorer.getCustomData() != null) {
         mplew.writeMapleAsciiString(ultExplorer.getCustomData());
      } else {
         mplew.writeMapleAsciiString("");
      }

      if (gs != null) {
         mplew.writeInt(gs.getId());
         mplew.writeMapleAsciiString(gs.getName());
         mplew.writeShort(gs.getLogoBG());
         mplew.write(gs.getLogoBGColor());
         mplew.writeShort(gs.getLogo());
         mplew.write(gs.getLogoColor());
         mplew.writeInt(gs.getCustomEmblem() != null ? gs.getId() : 0);
         mplew.writeInt(gs.getCustomEmblem() != null ? 1 : 0);
      } else {
         mplew.writeInt(0);
         mplew.writeLong(0L);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      mplew.write(chr.getGender());
      mplew.writeInt(chr.getFame());
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      chr.getSecondaryStat().encodeForRemote(mplew, null, chr.getPlayerJob(), true);
      mplew.writeShort(chr.getJob());
      mplew.writeShort(chr.getSubcategory());
      mplew.writeInt(10);
      mplew.writeInt(0);
      mplew.writeInt(0);
      boolean isBeta = false;
      if (chr.getZeroInfo() != null && chr.getZeroInfo().isBeta()) {
         isBeta = true;
      }

      PacketHelper.addCharLook(mplew, chr, true, isBeta);
      if (GameConstants.isZero(chr.getJob())) {
         PacketHelper.addCharLook(mplew, chr, true, !isBeta);
      }

      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(chr.getItemEffect());
      mplew.writeInt(0);
      String v = chr.getOneInfoQuest(19019, "id");
      int nickItemID = 0;
      if (v != null && !v.isEmpty()) {
         nickItemID = Integer.parseInt(v);
      }

      mplew.writeInt(nickItemID);
      mplew.write(chr.getNickItemMsg() != null);
      if (chr.getNickItemMsg() != null) {
         mplew.writeMapleAsciiString(chr.getNickItemMsg());
      }

      mplew.writeInt(0);
      DamageSkinSaveInfo info = chr.getDamageSkinSaveInfo();
      mplew.writeInt(info.getActiveDamageSkinData().getDamageSkinID());
      mplew.writeInt(0);
      mplew.writeMapleAsciiString("");
      mplew.writeMapleAsciiString("");
      mplew.writeInt(chr.getDefaultWingItem());
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(1);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeShort(-1);
      int chairID = chr.getChair();
      mplew.writeInt(chairID);
      mplew.writeInt(0);
      mplew.encodePos(chr.getPosition());
      boolean isStandChair = GameConstants.isStandChair(chairID);
      mplew.write(isStandChair ? 5 : chr.getStance());
      mplew.writeShort(chr.getName().equals("진 귀신") ? 0 : chr.getFH());
      mplew.write(0);
      mplew.write(0);
      mplew.write(chairID != 0);
      if (chairID != 0) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         getCustomChairPacket(mplew, chr, chairID, chr.getChairText(), true);
      }

      if (chr.getName().equals("나이스")) {
      }

      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(1);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      PacketHelper.addAnnounceBox(mplew, chr);
      mplew.write(chr.getChalkboard() != null && chr.getChalkboard().length() > 0 ? 1 : 0);
      if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
         mplew.writeMapleAsciiString(chr.getChalkboard());
      }

      Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
      addRingInfo(mplew, rings.getLeft());
      addRingInfo(mplew, rings.getMid());
      addMRingInfo(mplew, rings.getRight(), chr);
      boolean unk = true;
      mplew.write(unk);
      if (unk) {
         mplew.writeInt(0);
      }

      byte flag = 0;
      if (chr.getSkillLevel(1320016) > 0 && chr.getJob() == 132 && !chr.skillisCooling(1320019)) {
         flag = (byte)(flag | 1);
      }

      mplew.write(flag);
      mplew.writeInt(0);
      if (GameConstants.isKaiser(chr.getJob())) {
         String extern = chr.getOneInfoQuest(12860, "extern");
         String inner = chr.getOneInfoQuest(12860, "inner");
         String primium = chr.getOneInfoQuest(12860, "primium");
         mplew.writeInt(extern == null ? 0 : (extern.isEmpty() ? 0 : Integer.parseInt(extern)));
         mplew.writeInt(inner == null ? 0 : (inner.isEmpty() ? 0 : Integer.parseInt(inner)));
         mplew.write(primium == null ? 0 : (primium.isEmpty() ? 0 : Integer.parseInt(primium)));
      }

      mplew.writeInt(0);
      PacketHelper.addFarmInfo(mplew);

      for (int i = 0; i < 5; i++) {
         mplew.write(-1);
      }

      mplew.writeInt(0);
      mplew.write(1);
      if (chr.getBuffedValue(SecondaryStatFlag.RideVehicle) != null && chr.getBuffedValue(SecondaryStatFlag.RideVehicle) == 1932249) {
         mplew.writeInt(0);
      }

      mplew.writeInt(0);
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(0);

      for (int i = 0; i < 0; i++) {
         mplew.writeInt(0);
         mplew.writeMapleAsciiString("");
      }

      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(chr.getLockKinesisPsychicEnergyShieldEffect());
      mplew.write(chr.getLockBeastFormWingEffect());
      mplew.write(chr.getLockEclipseLook());
      mplew.write(chr.getLockEquilibriumLook());
      mplew.writeInt(1051291);
      mplew.write(DBConfig.isGanglim);
      if (DBConfig.isGanglim) {
         mplew.writeInt(0);
         mplew.writeInt(chr.getOneInfoQuestInteger(190823, "grade"));
      }

      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] removePlayerFromMap(int cid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
      mplew.writeInt(cid);
      return mplew.getPacket();
   }

   public static byte[] onZodiacRankInfo(int cid, int grade) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ZODIAC_RANK_INFO.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(0);
      mplew.writeInt(grade);
      return mplew.getPacket();
   }

   public static byte[] onZodiacInfo() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ZODIAC_INFO.getValue());
      mplew.writeInt(0);
      mplew.write(1);
      mplew.encodeBuffer(HexTool.getByteArrayFromHexString("A0 96 97 C6 D4 09 00 00"));
      return mplew.getPacket();
   }

   public static byte[] getChatText(int cidfrom, String namefrom, String text, boolean whiteBG, int chatFlag, ReportLogEntry report) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHATTEXT.getValue());
      if (cidfrom % 27 == 0) {
         mplew.writeInt(0);
      }

      mplew.writeInt(cidfrom);
      mplew.write(whiteBG ? 1 : 0);
      int emoticon = 0;
      if ((chatFlag & 8) != 0) {
         emoticon = Integer.parseInt(text.replace(":", ""));
         text = "";
      }

      mplew.writeMapleAsciiString(text);
      report.encode(mplew);
      mplew.write(chatFlag);
      mplew.write(0);
      mplew.write(5);
      if ((chatFlag & 8) != 0) {
         mplew.writeInt(emoticon);
      }

      return mplew.getPacket();
   }

   public static byte[] getLinkItemChatText(int cidfrom, String namefrom, String text, boolean whiteBG, int show, Item item, int unk, ReportLogEntry report) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.USER_CHAT_ITEM_LINK.getValue());
      mplew.writeInt(cidfrom);
      mplew.write(whiteBG ? 1 : 0);
      mplew.writeMapleAsciiString(text);
      report.encode(mplew);
      mplew.write(show);
      mplew.write(0);
      mplew.write(5);
      mplew.writeInt(unk);
      mplew.write(1);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeMapleAsciiString("");
      return mplew.getPacket();
   }

   public static byte[] getScrollEffect(int chr, Equip.ScrollResult scrollSuccess, int scrollid, int victimid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
      mplew.writeInt(chr);
      switch (scrollSuccess) {
         case SUCCESS:
            mplew.write(1);
            mplew.write(0);
            mplew.writeInt(scrollid);
            mplew.writeInt(victimid);
            break;
         case FAIL:
            mplew.write(0);
            mplew.write(0);
            mplew.writeInt(scrollid);
            mplew.writeInt(victimid);
            break;
         case CURSE:
            mplew.write(2);
            mplew.write(0);
            mplew.writeInt(scrollid);
            mplew.writeInt(victimid);
      }

      return mplew.getPacket();
   }

   public static byte[] showChaosScrollResult(Item scroll, Item equipment, int flag, int... value) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAOS_SCROLL_RESULT.getValue());
      mplew.writeInt(scroll.getItemId());
      mplew.writeInt(equipment.getItemId());
      mplew.writeInt(flag);
      int[] flags = new int[]{1, 2, 4, 8, 16, 32, 64, 256, 512, 4096, 8192};

      for (int i = 0; i < flags.length; i++) {
         if ((flag & flags[i]) != 0) {
            mplew.writeInt(value[i]);
         }
      }

      return mplew.getPacket();
   }

   public static byte[] showMagnifyingEffect(int chr, short pos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT.getValue());
      mplew.writeInt(chr);
      mplew.writeShort(pos);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] showPotentialReset(int chr, boolean success, int itemid, int equipItemID) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_POTENTIAL_RESET.getValue());
      mplew.writeInt(chr);
      mplew.write(success ? 1 : 0);
      mplew.writeInt(itemid);
      mplew.writeInt(0);
      mplew.writeInt(equipItemID);
      return mplew.getPacket();
   }

   public static byte[] getRedCubeStart(MapleCharacter chr, Item item, int cubeId, boolean changeLevel, int remainCount, int tryCount, int levelUpCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_REDCUBE_EFFECT.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(changeLevel ? 1 : 0);
      mplew.writeInt(cubeId);
      mplew.writeInt(item.getPosition());
      mplew.writeInt(remainCount);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeInt(tryCount);
      mplew.writeInt(levelUpCount);
      return mplew.getPacket();
   }

   public static byte[] getInGameCubeResult(MapleCharacter chr, Item item, int cubeId, boolean changeLevel, int remainCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_INGAME_CUBE_RESULT.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(changeLevel ? 1 : 0);
      mplew.writeInt(cubeId);
      mplew.writeInt(item.getPosition());
      mplew.writeInt(remainCount);
      PacketHelper.addItemInfo(mplew, item);
      return mplew.getPacket();
   }

   public static byte[] getInGameAdditionalCubeResult(MapleCharacter chr, Item item, int cubeId, boolean changeLevel, int remainCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.USER_ITEM_IN_GAME_ADDITIONAL_CUBE_RESULT.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(changeLevel ? 1 : 0);
      mplew.writeInt(cubeId);
      mplew.writeInt(item.getPosition());
      mplew.writeInt(remainCount);
      PacketHelper.addItemInfo(mplew, item);
      return mplew.getPacket();
   }

   public static byte[] getAdditionalCubeResult(MapleCharacter chr, Item item, int cubeId, boolean changeLevel, int remainCount, int tryCount, int levelUpCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_ADDITIONAL_CUBE_RESULT.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(changeLevel ? 1 : 0);
      mplew.writeInt(cubeId);
      mplew.writeInt(item.getPosition());
      mplew.writeInt(remainCount);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeInt(tryCount);
      mplew.writeInt(levelUpCount);
      return mplew.getPacket();
   }

   public static byte[] getBlackCubeResult(MapleCharacter chr, Item item, int cubeID, int remainCount, int tryCount, int levelUpCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BLACK_CUBE_RESULT.getValue());
      mplew.writeLong(item.getTempUniqueID());
      mplew.write(1);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeInt(cubeID);
      mplew.writeInt(item.getPosition());
      mplew.write(0);
      mplew.writeInt(remainCount);
      mplew.writeInt(-1);
      mplew.write(false);
      mplew.writeInt(tryCount);
      mplew.writeInt(levelUpCount);
      return mplew.getPacket();
   }

   public static byte[] getWhiteAdditionalCubeResult(MapleCharacter chr, Item item, int cubeID, int remainCount, int tryCount, int levelUpCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WHITE_ADDITIONAL_CUBE_RESULT.getValue());
      mplew.writeLong(item.getTempUniqueID());
      mplew.write(1);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeInt(cubeID);
      mplew.writeInt(item.getPosition());
      mplew.write(0);
      mplew.writeInt(remainCount);
      mplew.writeInt(-1);
      mplew.write(false);
      mplew.writeInt(tryCount);
      mplew.writeInt(levelUpCount);
      return mplew.getPacket();
   }

   public static byte[] ReturnScroll_Confirm(Equip equip, int scrollid) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RETURN_SCROLL_CONFIRM.getValue());
      packet.writeLong(equip.getTempUniqueID());
      packet.write(1);
      PacketHelper.addItemInfo(packet, equip);
      packet.writeInt(scrollid);
      return packet.getPacket();
   }

   public static byte[] ReturnScroll_Modify(Equip equip, int scrollid) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RETURN_SCROLL_RESULT.getValue());
      packet.write(1);
      PacketHelper.addItemInfo(packet, equip);
      packet.writeInt(scrollid);
      return packet.getPacket();
   }

   public static byte[] showItemMemorialEffect(int cid, int cubeId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_ITEM_MEMORIAL_EFFECT.getValue());
      mplew.writeInt(cid);
      mplew.write(1);
      mplew.writeInt(cubeId);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] getAnvilStart(Item item) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(2);
      mplew.write(0);
      mplew.write(3);
      mplew.write(1);
      mplew.writeShort(item.getPosition());
      mplew.write(0);
      mplew.write(1);
      mplew.writeShort(item.getPosition());
      PacketHelper.addItemInfo(mplew, item);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] getAnvil() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ANVIL.getValue());
      mplew.encodeBuffer(HexTool.getByteArrayFromHexString("3F 4D 00 3D F2 1E 00"));
      return mplew.getPacket();
   }

   public static byte[] showEnchanterEffect(int cid, byte result) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_ENCHANTER_EFFECT.getValue());
      mplew.writeInt(cid);
      mplew.write(result);
      return mplew.getPacket();
   }

   public static byte[] showSoulScrollEffect(int cid, byte result, boolean destroyed) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_SOULSCROLL_EFFECT.getValue());
      mplew.writeInt(cid);
      mplew.write(result);
      mplew.write(destroyed ? 1 : 0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static final byte[] pvpAttack(
      int cid,
      int playerLevel,
      int skill,
      int skillLevel,
      int speed,
      int mastery,
      int projectile,
      int attackCount,
      int chargeTime,
      int stance,
      int direction,
      int range,
      int linkSkill,
      int linkSkillLevel,
      boolean movementSkill,
      boolean pushTarget,
      boolean pullTarget,
      List<AttackPair> attack
   ) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_ATTACK.getValue());
      mplew.writeInt(cid);
      mplew.write(playerLevel);
      mplew.writeInt(skill);
      mplew.write(skillLevel);
      mplew.writeInt(linkSkill != skill ? linkSkill : 0);
      mplew.write(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
      mplew.write(direction);
      mplew.write(movementSkill ? 1 : 0);
      mplew.write(pushTarget ? 1 : 0);
      mplew.write(pullTarget ? 1 : 0);
      mplew.write(0);
      mplew.writeShort(stance);
      mplew.write(speed);
      mplew.write(mastery);
      mplew.writeInt(projectile);
      mplew.writeInt(chargeTime);
      mplew.writeInt(range);
      mplew.writeShort(attack.size());
      mplew.write(attackCount);
      mplew.write(0);

      for (AttackPair p : attack) {
         mplew.writeInt(p.objectid);
         mplew.encodePos(p.point);
         mplew.writeZeroBytes(5);

         for (Pair<Long, Boolean> atk : p.attack) {
            mplew.writeLong(atk.left);
            mplew.write(atk.right ? 1 : 0);
            mplew.writeShort(0);
         }
      }

      mplew.writeZeroBytes(10);
      System.out.print("어택" + mplew);
      return mplew.getPacket();
   }

   public static byte[] getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_MIST.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(mistSkill);
      mplew.write(mistLevel);
      mplew.writeInt(damage);
      mplew.write(8);
      mplew.writeInt(1000);
      return mplew.getPacket();
   }

   public static byte[] pvpCool(int cid, List<Integer> attack) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_COOL.getValue());
      mplew.writeInt(cid);
      mplew.write(attack.size());

      for (int b : attack) {
         mplew.writeInt(b);
      }

      return mplew.getPacket();
   }

   public static byte[] teslaTriangle(int cid, int sum1, int sum2, int sum3) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TESLA_TRIANGLE.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(sum1);
      mplew.writeInt(sum2);
      mplew.writeInt(sum3);
      return mplew.getPacket();
   }

   public static byte[] followEffect(int initiator, int replier, Point toMap) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FOLLOW_EFFECT.getValue());
      mplew.writeInt(initiator);
      mplew.writeInt(replier);
      if (replier == 0) {
         mplew.write(toMap == null ? 0 : 1);
         if (toMap != null) {
            mplew.writeInt(toMap.x);
            mplew.writeInt(toMap.y);
         }
      }

      return mplew.getPacket();
   }

   public static byte[] harvestResult(int cid, boolean success) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HARVESTED.getValue());
      mplew.writeInt(cid);
      mplew.write(success ? 0 : 2);
      return mplew.getPacket();
   }

   public static byte[] onDamage(int dmg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ON_DAMAGE.getValue());
      mplew.writeInt(dmg);
      return mplew.getPacket();
   }

   public static byte[] showPyramidEffect(int chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.NETT_PYRAMID.getValue());
      mplew.writeInt(chr);
      mplew.write(1);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] spawnDragon(Dragon d) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DRAGON_SPAWN.getValue());
      mplew.writeInt(d.getOwner());
      mplew.writeInt(d.getPosition().x);
      mplew.writeInt(d.getPosition().y);
      mplew.write(d.getStance());
      mplew.writeShort(0);
      mplew.writeShort(d.getJobId());
      return mplew.getPacket();
   }

   public static byte[] removeDragon(int chrid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DRAGON_REMOVE.getValue());
      mplew.writeInt(chrid);
      return mplew.getPacket();
   }

   public static byte[] moveDragon(Dragon d, Point startPos, List<LifeMovementFragment> moves) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DRAGON_MOVE.getValue());
      mplew.writeInt(d.getOwner());
      mplew.writeInt(0);
      mplew.encodePos(startPos);
      mplew.writeInt(0);
      PacketHelper.serializeMovementList(mplew, moves);
      return mplew.getPacket();
   }

   public static byte[] spawnAndroid(MapleCharacter cid, Android android) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ANDROID_SPAWN.getValue());
      mplew.writeInt(cid.getId());
      mplew.write(GameConstants.getAndroidType(android.getItemId()));
      mplew.encodePos(android.getPos());
      mplew.write(android.getStance());
      mplew.writeShort(0);
      mplew.writeInt(0);
      PacketHelper.androidInfo(mplew, android);

      for (short i = -1200; i > -1207; i--) {
         Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
         mplew.writeInt(item != null ? item.getItemId() : 0);
      }

      return mplew.getPacket();
   }

   public static byte[] moveAndroid(int cid, Point pos, List<LifeMovementFragment> res) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ANDROID_MOVE.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(0);
      mplew.encodePos(pos);
      mplew.writeInt(Integer.MAX_VALUE);
      PacketHelper.serializeMovementList(mplew, res);
      return mplew.getPacket();
   }

   public static byte[] showAndroidEmotion(int cid, int animation) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ANDROID_EMOTION.getValue());
      mplew.writeInt(cid);
      mplew.write(0);
      mplew.write(animation);
      return mplew.getPacket();
   }

   public static byte[] updateAndroidLook(boolean itemOnly, MapleCharacter cid, Android android) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ANDROID_UPDATE.getValue());
      mplew.writeInt(cid.getId());
      mplew.write(GameConstants.getAndroidType(android.getItemId()));
      mplew.encodePos(android.getPos());
      mplew.write(android.getStance());
      mplew.writeShort(0);
      mplew.writeInt(0);
      PacketHelper.androidInfo(mplew, android);

      for (short i = -1200; i > -1207; i--) {
         Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
         mplew.writeInt(item != null ? item.getItemId() : 0);
      }

      return mplew.getPacket();
   }

   public static byte[] deactivateAndroid(int cid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ANDROID_DEACTIVATED.getValue());
      mplew.writeInt(cid);
      mplew.write(false);
      return mplew.getPacket();
   }

   public static byte[] movePlayer(int cid, List<LifeMovementFragment> moves, Point startPos, Point ovPos, int encodedGatherDuration) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(encodedGatherDuration);
      mplew.encodePos(startPos);
      mplew.encodePos(ovPos);
      PacketHelper.serializeMovementList(mplew, moves);
      return mplew.getPacket();
   }

   public static byte[] closeRangeAttack(MapleCharacter player, boolean energy, AttackInfo attackInfo) {
      return addAttackInfo(energy ? 4 : 0, player, 0, 0, attackInfo);
   }

   public static byte[] rangedAttack(MapleCharacter player, int consumeID, AttackInfo attackInfo) {
      return addAttackInfo(1, player, consumeID, 0, attackInfo);
   }

   public static byte[] strafeAttack(MapleCharacter player, int consumeID, int ultLevel, AttackInfo attackInfo) {
      return addAttackInfo(2, player, consumeID, ultLevel, attackInfo);
   }

   public static byte[] magicAttack(MapleCharacter player, AttackInfo attackInfo) {
      return addAttackInfo(3, player, 0, 0, attackInfo);
   }

   public static byte[] addAttackInfo(int type, MapleCharacter player, int consumeID, int ultLevel, AttackInfo attackInfo) {
      PacketEncoder mplew = new PacketEncoder();
      if (type == 0) {
         mplew.writeShort(SendPacketOpcode.CLOSE_RANGE_ATTACK.getValue());
      } else if (type == 1 || type == 2) {
         mplew.writeShort(SendPacketOpcode.RANGED_ATTACK.getValue());
      } else if (type == 3) {
         mplew.writeShort(SendPacketOpcode.MAGIC_ATTACK.getValue());
      } else {
         mplew.writeShort(SendPacketOpcode.ENERGY_ATTACK.getValue());
      }

      mplew.writeInt(player.getId());
      mplew.write(0);
      mplew.write(attackInfo.tbyte);
      mplew.writeInt(player.getLevel());
      if (attackInfo.skillID <= 0 && type != 3) {
         mplew.writeInt(0);
      } else {
         mplew.writeInt(Math.max(1, attackInfo.skillLevel));
         mplew.writeInt(attackInfo.skillID);
      }

      if (GameConstants.is_zero_skill(attackInfo.skillID) && attackInfo.skillID != 100001283) {
         if (attackInfo.tag) {
            mplew.write(1);
            mplew.encodePos(attackInfo.forcedPos);
            attackInternal(mplew, type, player, consumeID, ultLevel, attackInfo);
            return mplew.getPacket();
         }

         mplew.write(0);
      }

      if (attackInfo.skillID == 101110104) {
         return mplew.getPacket();
      } else {
         int fixConsumeID = consumeID;
         if (GameConstants.isNightLord(player.getJob())) {
            int cashThrowingStar = player.getOneInfoQuestInteger(27038, "bullet");
            if (cashThrowingStar != 0) {
               fixConsumeID = cashThrowingStar;
            }
         }

         attackInternal(mplew, type, player, fixConsumeID, ultLevel, attackInfo);
         return mplew.getPacket();
      }
   }

   public static void attackInternal(PacketEncoder mplew, int type, MapleCharacter player, int consumeID, int ultLevel, AttackInfo attackInfo) {
      if (type == 1
         && (GameConstants.getAdvancedBulletCountHyperSkill(attackInfo.skillID) != 0 || GameConstants.getAdvancedAttackCountHyperSkill(attackInfo.skillID) != 0)
         )
       {
         int passiveId = 0;
         int passiveLv = 0;
         if (GameConstants.getAdvancedBulletCountHyperSkill(attackInfo.skillID) == 0) {
            if ((passiveId = GameConstants.getAdvancedAttackCountHyperSkill(attackInfo.skillID)) == 0) {
               passiveLv = 0;
            } else {
               passiveId = GameConstants.getAdvancedAttackCountHyperSkill(attackInfo.skillID);
               passiveLv = player.getSkillLevel(attackInfo.skillID);
            }
         } else {
            passiveId = GameConstants.getAdvancedBulletCountHyperSkill(attackInfo.skillID);
            passiveLv = player.getSkillLevel(attackInfo.skillID);
         }

         mplew.writeInt(passiveLv);
         if (passiveLv != 0) {
            mplew.writeInt(passiveId);
         }
      }

      if (attackInfo.skillID == 80001850) {
         mplew.writeInt(0);
      }

      int option1 = 0;
      int option2 = 0;
      int option3 = 0;
      if (attackInfo.allDamage.size() > 0 && player.getBuffedValue(SecondaryStatFlag.ShadowPartner) != null) {
         option1 |= 8;
      }

      mplew.write(option1);
      if (player.getBuffedValue(SecondaryStatFlag.BuckShot) != null) {
         option2 |= 2;
      }

      mplew.write(option2);
      mplew.writeInt(option3);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      if ((option2 & 2) > 0) {
         SecondaryStatEffect effect = player.getBuffedEffect(SecondaryStatFlag.BuckShot);
         if (effect != null) {
            int skillID = effect.getSourceId();
            int skillLevel = effect.getLevel();
            mplew.writeInt(skillID);
            mplew.writeInt(skillLevel);
         } else {
            mplew.writeInt(0);
            mplew.writeInt(0);
         }
      }

      if ((option2 & 8) > 0) {
         mplew.write(0);
      }

      mplew.writeShort(attackInfo.display);
      mplew.write(attackInfo.dragonAttackAction);
      mplew.writeShort(attackInfo.dragonPos.x);
      mplew.writeShort(attackInfo.dragonPos.y);
      mplew.write(attackInfo.dragonShowSkillEffect);
      mplew.write(attackInfo.dragonShowAttackAction);
      mplew.write(attackInfo.speed);
      mplew.write(player.getStat().passive_mastery());
      mplew.writeInt(consumeID);
      int dllattack = 0;

      for (AttackPair attackPair : attackInfo.allDamage) {
         if (attackPair.attack != null) {
            mplew.writeInt(attackPair.objectid);
            mplew.write(attackInfo.hitAction);
            mplew.write(attackInfo.lifting);
            mplew.write(attackInfo.alone);
            mplew.writeShort(attackInfo.delay);
            mplew.writeInt(attackInfo.UNK_Additional_1);
            mplew.writeInt(attackInfo.UNK_Additional_2);
            if (attackInfo.skillID == 80001835) {
               mplew.write(attackPair.attack.size());

               for (Pair<Long, Boolean> dmg : attackPair.attack) {
                  mplew.writeLong(dmg.left);
               }
            } else {
               for (Pair<Long, Boolean> dmg : attackPair.attack) {
                  if (attackInfo.dllAttackCount > 0) {
                     if (attackInfo.dllCritical.size() > dllattack && attackInfo.dllCritical.get(dllattack)) {
                        mplew.writeLong(dmg.left | Long.MIN_VALUE);
                     } else {
                        mplew.writeLong(dmg.left);
                     }

                     dllattack++;
                  } else if (dmg.right) {
                     mplew.writeLong(dmg.left + Long.MIN_VALUE);
                  } else {
                     mplew.writeLong(dmg.left);
                  }
               }
            }

            if (GameConstants.isKinesisPsychiclockSkill(attackInfo.skillID)) {
               if (player.getPsychicLock() != null) {
                  Optional<PsychicLock.Info> info = player.getPsychicLock().getLocks().stream().filter(l -> l.getMobId() == attackPair.objectid).findFirst();
                  if (info.isPresent()) {
                     mplew.writeInt(info.get().getPsychicLockKey());
                  } else {
                     mplew.writeInt(0);
                  }
               } else {
                  mplew.writeInt(0);
               }
            }

            if (attackInfo.skillID == 37111005) {
               mplew.write(0);
            } else if (attackInfo.skillID == 164001002) {
               mplew.writeInt(0);
            }
         }
      }

      if (attackInfo.skillID == 2221052 || attackInfo.skillID == 11121052 || attackInfo.skillID == 12121054) {
         mplew.writeInt(attackInfo.keydown);
      } else if (GameConstants.isSuperNovaSkill(attackInfo.skillID)
         || GameConstants.isScreenCenterAttackSkill(attackInfo.skillID)
         || attackInfo.skillID == 101000202
         || attackInfo.skillID == 101000102
         || GameConstants.isFieldAttackSkill(attackInfo.skillID)
         || attackInfo.skillID == 400041019
         || attackInfo.skillID == 400031016
         || attackInfo.skillID == 400041024
         || attackInfo.skillID == 3221019
         || GameConstants.isSeekingAttackSkill(attackInfo.skillID)
         || attackInfo.skillID == 400021075
         || attackInfo.skillID == 400001055
         || attackInfo.skillID == 400001056) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 80002452) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 400011132 || attackInfo.skillID == 400011134) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 400021097 || attackInfo.skillID == 400021098) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 400031063) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 400051075 || attackInfo.skillID == 500061030) {
         mplew.write(attackInfo.unk2);
         Point pos = attackInfo.position2;
         if (pos == null) {
            pos = attackInfo.forcedPos;
         }

         mplew.writeInt(pos.x);
         mplew.writeInt(pos.y);
      }

      if (attackInfo.skillID == 400041062
         || attackInfo.skillID == 400041079
         || attackInfo.skillID == 400051080
         || attackInfo.skillID == 400041074
         || attackInfo.skillID == 400041064
         || attackInfo.skillID == 400041065
         || attackInfo.skillID == 400041066
         || attackInfo.skillID == 400011125
         || attackInfo.skillID == 400011126
         || attackInfo.skillID == 155121007
         || attackInfo.skillID == 80003017) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 400051065 || attackInfo.skillID == 400051067) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 400021107) {
         mplew.writeInt(0);
      }

      if (attackInfo.skillID == 63111005 || attackInfo.skillID == 63111105 || attackInfo.skillID == 63111106) {
         mplew.writeInt(attackInfo.forcedPos.x);
         mplew.writeInt(attackInfo.forcedPos.y);
      }

      if (attackInfo.skillID == 51121009) {
         mplew.write(0);
      }

      if (attackInfo.skillID == 21120019
         || attackInfo.skillID == 37121052
         || attackInfo.skillID >= 400041002 && attackInfo.skillID <= 400041005
         || attackInfo.skillID == 11121014
         || attackInfo.skillID == 5101004) {
         mplew.write(attackInfo.unk2);
         Point pos = attackInfo.position2;
         if (pos == null) {
            pos = attackInfo.forcedPos;
         }

         mplew.writeInt(pos.x);
         mplew.writeInt(pos.y);
      }

      if (attackInfo.skillID == 400021088 || attackInfo.skillID == 400021113) {
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      if (GameConstants.isPathfinderSpecialThrowingBombSkill(attackInfo.skillID) || GameConstants.isThrowingBombSkill(attackInfo.skillID)) {
         mplew.writeInt(attackInfo.bombID);
         mplew.write(attackInfo.bombD);
      }

      if (attackInfo.skillID == 155101104
         || attackInfo.skillID == 155101204
         || attackInfo.skillID == 400051042
         || attackInfo.skillID == 151101003
         || attackInfo.skillID == 151101004) {
         mplew.write(1);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      if (attackInfo.skillID == 23121011 || attackInfo.skillID == 80001913) {
         mplew.write(0);
      }

      mplew.writeShort(attackInfo.forcedPos.x);
      mplew.writeShort(attackInfo.forcedPos.y);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
      mplew.writeLong(0L);
   }

   public static int sub_799020(int skillID) {
      return skillID != 400021053 && skillID != 400021029 && skillID != 64111012 && (skillID <= 400020008 || skillID > 400020011) ? 0 : 1;
   }

   public static int sub_798F00(int skillID) {
      switch (skillID) {
         case 152121004:
         case 400011004:
         case 400021004:
         case 400021009:
         case 400021010:
         case 400021011:
         case 400021028:
         case 400041020:
         case 400041034:
         case 400051003:
         case 400051008:
         case 400051016:
            return 1;
         default:
            return skillID > 400041018 || skillID < 400041016 && (skillID < 400021064 || skillID > 400021065) ? 0 : 1;
      }
   }

   public static int sub_6D9510(int a1) {
      int v1 = 0;
      if (a1 > 12119924) {
         if (a1 <= 31121001) {
            if (a1 == 31121001) {
               return 31120050;
            } else if (a1 > 21111021) {
               if (a1 > 21121017) {
                  if (a1 == 22140023) {
                     return 22170086;
                  }

                  if (a1 == 25121005) {
                     return 25120148;
                  }
               } else {
                  if (a1 >= 21121016) {
                     return 21120066;
                  }

                  if (a1 == 21120006) {
                     return 21120049;
                  }

                  if (a1 - 21120006 == 16) {
                     return 21120066;
                  }
               }

               return v1;
            } else {
               if (a1 != 21111021) {
                  if (a1 <= 15120003) {
                     if (a1 == 15120003) {
                        return 15120045;
                     }

                     if (a1 == 13121002) {
                        return 13120048;
                     }

                     if (a1 == 14121002) {
                        return 14120045;
                     }

                     if (a1 == 15111022) {
                        return 15120045;
                     }

                     return v1;
                  }

                  if (a1 == 15121002) {
                     return 15120048;
                  }

                  if (a1 != 21110020) {
                     return v1;
                  }
               }

               return 21120047;
            }
         } else if (a1 <= 51121008) {
            if (a1 == 51121008) {
               return 51120048;
            } else if (a1 <= 37120001) {
               if (a1 == 37120001) {
                  return 37120045;
               } else if (a1 == 32111003) {
                  return 32120047;
               } else if (a1 == 35121016) {
                  return 35120051;
               } else {
                  return a1 == 37110002 ? 37120045 : v1;
               }
            } else if (a1 != 51120057) {
               return a1 == 51121007 ? 51120051 : v1;
            } else {
               return 51120058;
            }
         } else if (a1 <= 61121201) {
            if (a1 != 61121201) {
               if (a1 == 51121009) {
                  return 51120058;
               }

               if (a1 != 61121100) {
                  return v1;
               }
            }

            return 61120045;
         } else {
            if (a1 >= 65121007 && (a1 <= 65121008 || a1 == 65121101)) {
               v1 = 65120051;
            }

            return v1;
         }
      } else if (a1 == 12119924) {
         return 12119924;
      } else if (a1 <= 5121007) {
         if (a1 == 5121007) {
            return 5120048;
         } else {
            if (a1 > 3121015) {
               if (a1 > 4221007) {
                  if (a1 == 4331000) {
                     return 4340045;
                  }

                  if (a1 == 4341009) {
                     return 4340048;
                  }
               } else {
                  if (a1 == 4221007) {
                     return 4220048;
                  }

                  if (a1 == 3121020) {
                     return 3120051;
                  }

                  if (a1 == 3221017) {
                     return 3220048;
                  }
               }
            } else {
               if (a1 == 3121015) {
                  return 3120048;
               }

               if (a1 > 1221011) {
                  if (a1 == 2121006) {
                     return 2120048;
                  }

                  if (a1 == 2221006) {
                     return 2220048;
                  }
               } else {
                  if (a1 == 1221011) {
                     return 1220050;
                  }

                  if (a1 == 1120017 || a1 == 1121008) {
                     return 1120051;
                  }

                  if (a1 == 1221009) {
                     return 1220048;
                  }
               }
            }

            return v1;
         }
      } else if (a1 <= 11121103) {
         if (a1 != 11121103) {
            if (a1 > 5320011) {
               if (a1 == 5321000) {
                  return 5320048;
               }

               if (a1 - 5321000 != 4) {
                  return v1;
               }
            } else if (a1 != 5320011) {
               if (a1 > 5121020) {
                  if (a1 == 5221016) {
                     return 5220047;
                  }

                  return v1;
               }

               if (a1 != 5121020) {
                  if (a1 - 5121016 <= 1) {
                     return 5120051;
                  }

                  return v1;
               }

               return 5120048;
            }

            return 5320043;
         } else {
            return 11120048;
         }
      } else if (a1 <= 12100028) {
         if (a1 != 12100028) {
            if (a1 == 11121203) {
               return 11120048;
            }

            if (a1 != 12000026) {
               return 0;
            }
         }

         return 12119924;
      } else {
         return a1 != 12110028 && a1 != 12119924 ? v1 : 12119924;
      }
   }

   public static byte[] skillPrepare(MapleCharacter from, int skillId, int level, short display, byte unk, Point position) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SKILL_PREPARE.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(skillId);
      mplew.write(level);
      mplew.writeShort(display);
      mplew.write(unk);
      if (GameConstants.isKeydownSkillRectMoveXY(skillId)) {
         mplew.encodePos(position);
      }

      return mplew.getPacket();
   }

   public static byte[] skillCancel(MapleCharacter from, int skillId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SKILL_CANCEL.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(skillId);
      mplew.writeInt(skillId);
      return mplew.getPacket();
   }

   public static byte[] damagePlayer(int skill, int monsteridfrom, int cid, int damage) {
      return damagePlayer(cid, skill, damage, monsteridfrom, (byte)0, 0, 0, false, 0, (byte)0, null, (byte)0, 0, 0);
   }

   public static byte[] damagePlayer(
      int cid,
      int type,
      int damage,
      int monsteridfrom,
      byte direction,
      int skillid,
      int pDMG,
      boolean pPhysical,
      int pID,
      byte pType,
      Point pPos,
      byte offset,
      int offset_d,
      int fake
   ) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
      mplew.writeInt(cid);
      mplew.write(type);
      mplew.writeInt(damage);
      mplew.write(0);
      boolean guard = false;
      mplew.write(guard);
      if (!guard) {
         mplew.write(0);
      }

      if (type < -1) {
         if (type == -8) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
         }
      } else {
         mplew.writeInt(monsteridfrom);
         mplew.write(direction);
         mplew.writeInt(skillid);
         mplew.writeInt(0);
         mplew.writeInt(pDMG);
         mplew.write(0);
         if (pDMG > 0) {
            mplew.write(pPhysical ? 1 : 0);
            mplew.writeInt(pID);
            mplew.write(pType);
            mplew.encodePos(pPos);
         } else {
            mplew.write(pType);
            mplew.encodePos(pPos);
         }

         mplew.write(offset);
         if (offset == 1) {
            mplew.writeInt(offset_d);
         }
      }

      mplew.writeInt(damage);
      if (damage <= 0 || fake > 0) {
         mplew.writeInt(fake);
      }

      return mplew.getPacket();
   }

   public static byte[] facialExpression(MapleCharacter from, int expression) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(expression);
      mplew.writeInt(-1);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] facialExpressionWithDuration(MapleCharacter from, int expression, int duration) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
      mplew.writeInt(from.getId());
      mplew.writeInt(expression);
      mplew.writeInt(duration);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] setQuickMove() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.QUICK_MOVE.getValue());
      packet.write(1);
      packet.writeInt(0);
      packet.writeInt(1531030);
      packet.writeInt(6);
      packet.writeInt(0);
      packet.writeMapleAsciiString("빠른이동을 사용합니다.");
      packet.writeLong(PacketHelper.getTime(-2L));
      packet.writeLong(PacketHelper.getTime(-1L));
      return packet.getPacket();
   }

   public static byte[] itemEffect(int characterid, int itemid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_ITEM_EFFECT.getValue());
      mplew.writeInt(characterid);
      mplew.writeInt(itemid);
      return mplew.getPacket();
   }

   public static byte[] showChair(MapleCharacter player, int itemID, String message, long meso) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_CHAIR.getValue());
      mplew.writeInt(player.getId());
      mplew.writeInt(itemID);
      mplew.write(itemID == 0 ? 0 : 1);
      getCustomChairPacket(mplew, player, itemID, message, false);
      return mplew.getPacket();
   }

   public static byte[] upcateCodyVoteChair(MapleCharacter target, int unk1, int unk2) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.UPCATE_CODY_VOTE_CHAIR.getValue());
      packet.writeInt(target.getId());
      packet.writeInt(unk1);
      packet.writeInt(target.getCodyVote(0));
      packet.writeInt(target.getCodyVote(1));
      packet.writeInt(target.getCodyVote(2));
      packet.writeInt(unk2);
      packet.writeInt(2);
      return packet.getPacket();
   }

   public static void getCustomChairPacket(PacketEncoder mplew, MapleCharacter player, int itemID, String message, boolean enterField) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      String type = ii.getChairType(itemID);
      switch (type) {
         case "timeChair":
            mplew.writeInt(0);
            break;
         case "popChair":
            mplew.writeInt(player.getPopChairInfos().size());

            for (Pair<String, Integer> pair : player.getPopChairInfos()) {
               mplew.writeMapleAsciiString(pair.left);
               mplew.writeInt(pair.right);
            }
            break;
         case "starForceChair":
            mplew.writeInt(0);
            break;
         case "trickOrTreatChair":
            mplew.writeInt(0);
            mplew.writeInt(0);
            break;
         case "celebChair":
            mplew.writeInt(0);
         case "randomChair":
         case "mirrorChair":
         case "androidChair":
         case "rotatedSleepingBagChair":
         case "eventPointChair":
         case "arcaneForceChair":
            break;
         case "identityChair":
            mplew.write(true);
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
            break;
         case "popButtonChair":
            mplew.writeInt(player.getFame());
            break;
         case "rollingHouseChair":
            mplew.writeInt(itemID);
            mplew.writeInt(0);
            break;
         case "mannequinChair":
            mplew.writeInt(0);
            int z = 0;
            break;
         case "hashTagChair":
            for (int ix = 0; ix < 18; ix++) {
               mplew.writeMapleAsciiString("");
            }
            break;
         case "petChair":
            for (int i = 0; i < 3; i++) {
               MaplePet pet = player.getPet(i);
               if (pet != null) {
                  mplew.writeInt(0);
                  mplew.writeInt(0);
                  mplew.writeInt(0);
               } else {
                  mplew.writeInt(0);
                  mplew.writeInt(0);
                  mplew.writeInt(0);
               }
            }
            break;
         case "charLvChair":
            mplew.writeInt(player.getLevel());
            break;
         case "scoreChair":
            mplew.writeInt(0);
            break;
         case "scaleAvatarChair":
            if (itemID == 3018465) {
               mplew.write(1);
               mplew.writeInt(player.getDojangChairFloor());
            } else {
               mplew.write(0);
            }
            break;
         case "wasteChair":
            mplew.writeLong(player.getMesoChairCount());
            break;
         case "2019rollingHouseChair":
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            break;
         case "unionRankChair":
            mplew.write(false);
            break;
         case "yetiChair":
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            break;
         case "worldLvChair":
         case "atkPwrChair":
         case "worldLvChairNonShowLevel":
            if (itemID == 3019037) {
               mplew.write(player.getChairInfo() != null ? 1 : 0);
               if (player.getChairInfo() != null) {
                  player.getChairInfo().encode(mplew);
               }
            } else {
               mplew.write(false);
            }
            break;
         case "outfitVoteChair":
            mplew.writeInt(player.getCodyVote(0));
            PacketHelper.addCharLook(mplew, player, false, false, false, 0);
            mplew.writeInt(player.getCodyVote(1));
            PacketHelper.addCharLook(mplew, player, false, false, false, 1);
            mplew.writeInt(player.getCodyVote(2));
            PacketHelper.addCharLook(mplew, player, false, false, false, 2);
            break;
         default:
            if (itemID / 1000 == 3014) {
               if (message == null) {
                  message = "";
               }

               mplew.writeMapleAsciiString(message);
               mplew.writeMapleAsciiString(player.getName());
               mplew.writeMapleAsciiString(message);
               mplew.writeInt(0);
               mplew.writeInt(48659);
               mplew.write(0);
               mplew.writeInt(0);
               mplew.writeInt(0);
               mplew.writeMapleAsciiString(player.getName());
               mplew.writeInt(player.getId());
            } else if (itemID != 3015520
               && itemID != 3018071
               && itemID != 3018352
               && itemID != 3018464
               && itemID != 3015798
               && itemID != 3015895
               && itemID != 3018599) {
               if (itemID == 3015440 || itemID == 3015650 || itemID == 3015651 || itemID == 3015897 || itemID == 3018430 | itemID == 3018450) {
                  mplew.writeLong(player.getMesoChairCount());
               } else if (itemID == 3018634) {
                  mplew.writeInt(0);
                  mplew.writeInt(0);
                  mplew.writeInt(0);
               }
            } else {
               mplew.write(player.getChairInfo() != null ? 1 : 0);
               if (player.getChairInfo() != null) {
                  player.getChairInfo().encode(mplew);
               }
            }
      }

      if (GameConstants.isTowerChair(itemID)) {
         QuestEx leftQex = player.getInfoQuest(101309);
         QuestEx midQex = player.getInfoQuest(7266);
         QuestEx rightQex = player.getInfoQuest(101310);
         String leftTowerChair = "";
         String midTowerChair = "";
         String rightTowerChair = "";
         if (midQex != null) {
            midTowerChair = midQex.getData();
         }

         if (midTowerChair.isEmpty()) {
            mplew.writeInt(player.getChair());

            for (int ix = 0; ix < 18; ix++) {
               mplew.writeInt(0);
            }
         } else {
            mplew.writeInt(player.getChair());
            if (leftQex != null) {
               leftTowerChair = leftQex.getData();
            }

            int count = 0;
            String[] leftSplit = leftTowerChair.split(";");

            for (String s : leftSplit) {
               Integer chair = 0;

               try {
                  chair = Integer.parseInt(s.split("=")[1]);
               } catch (Exception var25) {
               }

               mplew.writeInt(chair);
               count++;
            }

            for (int ix = count; ix < 6; ix++) {
               mplew.writeInt(0);
            }

            count = 0;
            String[] midSplit = midTowerChair.split(";");

            for (String s : midSplit) {
               Integer chair = 0;

               try {
                  chair = Integer.parseInt(s.split("=")[1]);
               } catch (Exception var24) {
               }

               mplew.writeInt(chair);
               count++;
            }

            for (int ix = count; ix < 6; ix++) {
               mplew.writeInt(0);
            }

            if (rightQex != null) {
               rightTowerChair = rightQex.getData();
            }

            count = 0;
            String[] rightSplit = rightTowerChair.split(";");

            for (String s : rightSplit) {
               Integer chair = 0;

               try {
                  chair = Integer.parseInt(s.split("=")[1]);
               } catch (Exception var23) {
               }

               mplew.writeInt(chair);
               count++;
            }

            for (int ix = count; ix < 6; ix++) {
               mplew.writeInt(0);
            }
         }
      } else if (!enterField) {
         mplew.writeInt(0);
      }
   }

   public static byte[] setMesoChairCount(int characterID, int itemID, long meso) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SET_MESO_CHAIR_COUNT.getValue());
      mplew.writeInt(characterID);
      mplew.writeInt(itemID);
      mplew.writeLong(meso);
      mplew.writeLong(meso);
      return mplew.getPacket();
   }

   public static byte[] updateCharLook(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(1);
      PacketHelper.addCharLook(mplew, chr, true, false);
      if (GameConstants.isZero(chr.getJob())) {
         PacketHelper.addCharLook(mplew, chr, true, true);
      }

      Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
      addRingInfo(mplew, rings.getLeft());
      addRingInfo(mplew, rings.getMid());
      addMRingInfo(mplew, rings.getRight(), chr);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(curhp);
      mplew.writeInt(maxhp);
      return mplew.getPacket();
   }

   public static byte[] loadGuildData(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.LOAD_GUILD_DATA.getValue());
      mplew.writeInt(chr.getId());
      if (chr.getGuildId() <= 0) {
         mplew.writeInt(0);
         mplew.writeMapleAsciiString("");
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeInt(0);
      } else {
         Guild gs = Center.Guild.getGuild(chr.getGuildId());
         if (gs != null) {
            mplew.writeInt(gs.getId());
            mplew.writeMapleAsciiString(gs.getName());
            mplew.writeShort(gs.getLogoBG());
            mplew.write(gs.getLogoBGColor());
            mplew.writeShort(gs.getLogo());
            mplew.write(gs.getLogoColor());
            mplew.writeInt(gs.getCustomEmblem() != null && gs.getCustomEmblem().length > 0 ? 1 : 0);
            if (gs.getCustomEmblem() != null && gs.getCustomEmblem().length > 0) {
               mplew.writeInt(gs.getId());
               mplew.write(0);
               mplew.writeInt(gs.getCustomEmblem().length);
               mplew.encodeBuffer(gs.getCustomEmblem());
            }
         } else {
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(0);
         }
      }

      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] changeTeam(int cid, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.LOAD_TEAM.getValue());
      mplew.writeInt(cid);
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] showHarvesting(int cid, int tool) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_HARVEST.getValue());
      mplew.writeInt(cid);
      if (tool > 0) {
         mplew.writeInt(1);
         mplew.writeInt(tool);
      } else {
         mplew.writeInt(0);
      }

      return mplew.getPacket();
   }

   public static byte[] getPVPHPBar(int cid, int hp, int maxHp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_HP.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(hp);
      mplew.writeInt(maxHp);
      return mplew.getPacket();
   }

   public static byte[] cancelChair(int id, MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CANCEL_CHAIR.getValue());
      if (id == -1) {
         mplew.writeInt(chr.getId());
         mplew.write(0);
      } else {
         mplew.writeInt(chr.getId());
         mplew.write(1);
         mplew.writeShort(id);
      }

      return mplew.getPacket();
   }

   public static byte[] instantMapWarp(int portal) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CURRENT_MAP_WARP.getValue());
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(portal);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] sendHint(String hint, int width, int height) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
      mplew.writeMapleAsciiString(hint);
      mplew.writeShort(width < 1 ? Math.max(hint.length() * 10, 40) : width);
      mplew.writeShort(Math.max(height, 5));
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] aranCombo(int value) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ARAN_COMBO.getValue());
      mplew.writeInt(value);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] rechargeCombo(int value) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ARAN_COMBO_RECHARGE.getValue());
      mplew.writeInt(value);
      return mplew.getPacket();
   }

   public static byte[] chatMsg(int type, String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAT_MSG.getValue());
      mplew.writeShort(type);
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves, int encodedGatherDuration) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FOLLOW_MOVE.getValue());
      mplew.writeInt(encodedGatherDuration);
      mplew.encodePos(otherStart);
      mplew.encodePos(myStart);
      PacketHelper.serializeMovementList(mplew, moves);
      mplew.write(17);

      for (int i = 0; i < 8; i++) {
         mplew.write(0);
      }

      mplew.write(0);
      mplew.encodePos(otherEnd);
      mplew.encodePos(otherStart);
      return mplew.getPacket();
   }

   public static byte[] getFollowMsg(int opcode) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FOLLOW_MSG.getValue());
      mplew.writeLong(opcode);
      return mplew.getPacket();
   }

   public static byte[] createUltimate(int amount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CREATE_ULTIMATE.getValue());
      mplew.writeInt(amount);
      return mplew.getPacket();
   }

   public static byte[] harvestMessage(int oid, int msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HARVEST_MESSAGE.getValue());
      mplew.writeInt(oid);
      mplew.writeInt(msg);
      return mplew.getPacket();
   }

   public static byte[] dragonBlink(int portalId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DRAGON_BLINK.getValue());
      mplew.write(portalId);
      return mplew.getPacket();
   }

   public static byte[] getTeleport(int excl, int callingType, int data, Point pos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CURRENT_MAP_WARP.getValue());
      mplew.write(excl);
      mplew.write(callingType);
      if (callingType == 0) {
         mplew.writeInt(data);
         mplew.write(0);
      } else {
         mplew.writeInt(data);
         mplew.encodePos(pos);
      }

      return mplew.getPacket();
   }

   public static byte[] getNeedToConfirm(int a, int b, int c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.NEED_TO_CONFIRM.getValue());
      mplew.writeInt(a);
      mplew.writeInt(b);
      mplew.writeInt(c);
      return mplew.getPacket();
   }

   public static byte[] MakeBlind(int... args) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      mplew.write(23);
      mplew.write(args[0]);
      mplew.writeShort(args[1]);
      mplew.writeShort(args[2]);
      mplew.writeShort(args[3]);
      mplew.writeShort(args[4]);
      mplew.writeInt(args[5]);
      mplew.writeInt(args[6]);
      return mplew.getPacket();
   }

   public static byte[] CameraCtrl(int nType, int... args) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CAMERA_CTRL_MSG.getValue());
      mplew.write(nType);
      switch (nType) {
         case 11:
            mplew.writeInt(args[0]);
         case 12:
         case 14:
         default:
            break;
         case 13:
            mplew.write(args[0]);
            mplew.writeInt(args[1]);
            mplew.writeInt(args[2]);
            mplew.writeInt(args[3]);
            break;
         case 15:
            mplew.write(args[0]);
            mplew.writeInt(args[1]);
            mplew.writeInt(args[2]);
      }

      return mplew.getPacket();
   }

   public static byte[] getSetFieldFloating(int field, int a, int b, int c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SET_FIELD_FLOATING.getValue());
      mplew.writeInt(field);
      mplew.writeInt(a);
      mplew.writeInt(b);
      mplew.writeInt(c);
      return mplew.getPacket();
   }

   public static byte[] getCharacterExpression(int a, int b) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHARACTER_EXPRESSION.getValue());
      mplew.writeInt(a);
      mplew.writeInt(b);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] getPVPIceGage(int score) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PVP_ICEGAGE.getValue());
      mplew.writeInt(score);
      return mplew.getPacket();
   }

   public static byte[] skillCooldown(int sid, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
      mplew.writeInt(1);
      mplew.writeInt(sid);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] skillCooldown(List<Pair<Integer, Integer>> dataList) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
      mplew.writeInt(dataList.size());

      for (Pair<Integer, Integer> data : dataList) {
         mplew.writeInt(data.left);
         mplew.writeInt(data.right);
      }

      return mplew.getPacket();
   }

   public static byte[] temporarySkillCooldown(int sid, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TEMPORARY_SKILL_COOLDOWN.getValue());
      mplew.writeInt(sid);
      mplew.writeInt(time);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] skillCooldownReduce(int time, List<Integer> skills) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SKILL_COOLTIME_REDUCE.getValue());
      mplew.writeInt(time);
      mplew.write(skills.size());

      for (Integer skill : skills) {
         mplew.writeInt(skill);
      }

      return mplew.getPacket();
   }

   public static byte[] dropItemFromMapObject(Drop drop, Point dropfrom, Point dropto, byte enterType) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
      mplew.write(drop.getSpecialType());
      mplew.write(enterType);
      mplew.writeInt(drop.getObjectId());
      mplew.write(drop.getMeso() > 0 ? 1 : 0);
      mplew.writeInt(drop.getDropMotionType());
      mplew.writeInt(drop.getDropSpeed());
      mplew.writeInt(drop.getDropSlopeAngle());
      mplew.writeInt(drop.getItemId());
      mplew.writeInt(drop.getOwner());
      mplew.write(drop.getOwnType());
      mplew.encodePos(dropto);
      mplew.writeInt(drop.getOwnType() == 0 ? drop.getOwner() : 0);
      mplew.writeInt(drop.getUnk1());
      mplew.writeLong(0L);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeLong(0L);
      mplew.writeInt(0);
      mplew.writeLong(0L);
      mplew.write(drop.getExplosiveDrop());
      mplew.write(0);
      mplew.write(0);
      if (enterType != 2) {
         mplew.encodePos(dropfrom);
         mplew.writeInt(0);
      }

      mplew.write(drop.isBossDrop() ? 1 : 0);
      if (drop.getMeso() == 0) {
         PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
      }

      mplew.write(drop.isPlayerDrop() ? 0 : 1);
      mplew.write(0);
      mplew.writeShort(0);
      mplew.write(0);
      mplew.writeInt(drop.isCollisionPickUp() ? 1 : 0);
      if (drop.getItemId() / 1000000 != 1 || drop.getMeso() != 0 || drop.getEquip() == null) {
         mplew.write(0);
      } else if (GameConstants.isDropEffect(drop.getEquip().getState())) {
         mplew.write(drop.getEquip().getState());
      } else if (drop.getEquip().getState() != 0) {
         mplew.write(drop.getEquip().getState() - 16);
      } else {
         mplew.write(0);
      }

      mplew.write(drop.isCollisionPickUp() ? 1 : 0);
      mplew.writeInt(drop.getDropDelay());
      mplew.writeInt(drop.getItemId() != 2633909 && drop.getItemId() != 2633910 ? 0 : drop.getOwnerID());
      return mplew.getPacket();
   }

   public static byte[] explodeDrop(int oid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
      mplew.write(4);
      mplew.writeInt(oid);
      mplew.writeShort(655);
      return mplew.getPacket();
   }

   public static byte[] removeItemFromMap(int oid, int animation, int cid) {
      return removeItemFromMap(oid, animation, cid, 0);
   }

   public static byte[] removeItemFromMap(int oid, int animation, int cid, int slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
      mplew.write(animation);
      mplew.writeInt(oid);
      if (animation >= 2) {
         mplew.writeInt(cid);
         if (animation == 5) {
            mplew.writeInt(slot);
         }
      }

      return mplew.getPacket();
   }

   public static byte[] createAffectedArea(AffectedArea mist) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_MIST.getValue());
      mplew.writeInt(mist.getObjectId());
      mplew.write(mist.isMobMist() ? 1 : 0);
      mplew.writeInt(mist.getOwnerId());
      if (mist.getMobSkill() == null) {
         if (mist.getSkillId() == -1) {
            mplew.writeInt(mist.getSourceSkillID());
         } else {
            mplew.writeInt(mist.getSkillId());
         }
      } else {
         mplew.writeInt(mist.getMobSkill().getSkillId());
      }

      mplew.writeShort(mist.getSkillLevel());
      int span = (int)(mist.getStartTime() - System.currentTimeMillis());
      if (mist.getStartTime() == 0L) {
         span = 0;
      }

      if (mist.getMobSkill() == null) {
         if (mist.getSourceSkillID() == 400021040 || mist.getSourceSkillID() == 400021031) {
            mplew.writeShort(26);
         } else if (mist.getSourceSkillID() == 35121052) {
            mplew.writeShort(mist.getSkillDelay());
         } else {
            mplew.writeShort(Math.max(0, span / 100));
         }
      } else {
         mplew.writeShort(Math.max(0, span / 100));
      }

      if (mist.getMistRect() != null) {
         mist.getMistRect().encode(mplew);
      } else {
         mplew.encodeRect(mist.getBox());
      }

      if (mist.getSource() != null && mist.getSource().getSourceId() == 162111000) {
         mist.getBuffRect().encode(mplew);
      }

      mplew.writeInt(mist.getElement().getValue());
      if (mist.getTruePosition() != null) {
         mplew.encodePos(mist.getTruePosition());
      } else if (mist.getPosition() != null) {
         mplew.encodePos(mist.getPosition());
      } else if (mist.getOwner() != null) {
         mplew.encodePos(mist.getOwner().getPosition());
      } else {
         mplew.writeInt(0);
      }

      if (mist.getForcePos() != null) {
         mplew.writeShort(mist.getForcePos().x);
         mplew.writeShort(mist.getForcePos().y);
      } else {
         mplew.writeInt(0);
      }

      mplew.writeInt(mist.getDwOption());
      System.out.println(mist.getDwOption());
      System.out.println(mist.getPreUOL());
      mplew.write(mist.getPreUOL());
      if (mist.getMobSkill() != null) {
         if (mist.getMobSkill().getSkillId() == MobSkillID.AREA_POISON.getVal() && mist.getMobSkill().getSkillLevel() == 26) {
            mplew.writeInt(210);
         } else if (mist.getMobSkill().getSkillId() == MobSkillID.JINHILLAH_MIST.getVal()) {
            mplew.writeInt(250);
         } else {
            mplew.writeInt(0);
         }
      } else if (mist.getOwner() != null && mist.getMistRect() == null) {
         if (mist.getOwner().getPosition().x < mist.getBox().x) {
            mplew.writeInt(1);
         } else {
            mplew.writeInt(0);
         }
      } else {
         mplew.writeInt(0);
      }

      int duration = (int)(mist.getEndTime() - mist.getStartTime());
      mplew.writeInt(mist.getSourceSkillID() == 2241001 ? 4 : 0);
      mplew.writeInt(0);
      if (GameConstants.isAffectedAreaSkill(mist.getSourceSkillID())) {
         mplew.write(mist.getRLType() ^ 1);
      }

      if (mist.getMobSkill() == null) {
         if (mist.getSourceSkillID() != 400021040 && mist.getSourceSkillID() != 400021031) {
            mplew.writeInt(duration);
         } else {
            mplew.writeInt(mist.getTdBreakTime());
         }
      } else {
         mplew.writeInt(duration);
      }

      mplew.writeInt(0);
      mplew.writeInt(0);
      if (mist.getSource() != null) {
         mplew.write(mist.getSourceSkill().getId() == 151121041);
      } else {
         mplew.write(0);
      }

      mplew.write(mist.getSourceSkillID() == 2321015);
      mplew.write(mist.getJHillahMist() != null);
      if (mist.getSourceSkillID() == 2241001) {
         mplew.writeInt(65536);
      }

      if (mist.getJHillahMist() != null) {
         mist.getJHillahMist().encode(mplew, mist.getJMistPhase());
      }

      if (mist.getSourceSkillID() == 2321015) {
         mplew.writeInt(13131);
      }

      return mplew.getPacket();
   }

   public static byte[] removeAffectedArea(int oid, int skillID, boolean eruption) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_MIST.getValue());
      mplew.writeInt(oid);
      mplew.writeInt(0);
      if (skillID == 2111003) {
         mplew.write(0);
      }

      return mplew.getPacket();
   }

   public static byte[] spawnDoor(int oid, Point pos, boolean animation) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_DOOR.getValue());
      mplew.write(animation ? 0 : 1);
      mplew.writeInt(oid);
      mplew.encodePos(pos);
      return mplew.getPacket();
   }

   public static byte[] removeDoor(int oid, boolean animation) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_DOOR.getValue());
      mplew.write(animation ? 0 : 1);
      mplew.writeInt(oid);
      return mplew.getPacket();
   }

   public static byte[] spawnMechDoor(OpenGate md, boolean animated) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MECH_DOOR_SPAWN.getValue());
      mplew.write(animated ? 0 : 1);
      mplew.writeInt(md.getOwnerId());
      mplew.encodePos(md.getTruePosition());
      mplew.write(md.getId());
      mplew.write(md.getId());
      mplew.writeInt(md.getPartyId());
      return mplew.getPacket();
   }

   public static byte[] removeMechDoor(OpenGate md, boolean animated) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MECH_DOOR_REMOVE.getValue());
      mplew.write(animated ? 0 : 1);
      mplew.writeInt(md.getOwnerId());
      mplew.write(md.getId());
      return mplew.getPacket();
   }

   public static byte[] triggerReactor(Reactor reactor, int stance) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REACTOR_HIT.getValue());
      mplew.writeInt(reactor.getObjectId());
      mplew.write(reactor.getState());
      mplew.encodePos(reactor.getTruePosition());
      mplew.writeShort(stance);
      mplew.write(0);
      mplew.write(4);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] spawnReactor(Reactor reactor) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REACTOR_SPAWN.getValue());
      mplew.writeInt(reactor.getObjectId());
      mplew.writeInt(reactor.getReactorId());
      mplew.write(reactor.getState());
      mplew.encodePos(reactor.getTruePosition());
      mplew.write(reactor.getFacingDirection());
      mplew.writeMapleAsciiString(reactor.getName());
      return mplew.getPacket();
   }

   public static byte[] destroyReactor(Reactor reactor) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REACTOR_DESTROY.getValue());
      mplew.writeInt(reactor.getObjectId());
      mplew.write(0);
      mplew.write(reactor.getState());
      mplew.encodePos(reactor.getPosition());
      return mplew.getPacket();
   }

   public static byte[] makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_EXTRACTOR.getValue());
      mplew.writeInt(cid);
      mplew.writeMapleAsciiString(cname);
      mplew.writeInt(pos.x);
      mplew.writeInt(pos.y);
      mplew.writeShort(timeLeft);
      mplew.writeInt(itemId);
      mplew.writeInt(fee);
      return mplew.getPacket();
   }

   public static byte[] removeExtractor(int cid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_EXTRACTOR.getValue());
      mplew.writeInt(cid);
      mplew.writeInt(2);
      return mplew.getPacket();
   }

   public static byte[] rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ROLL_SNOWBALL.getValue());
      mplew.write(type);
      mplew.writeInt(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
      mplew.writeInt(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
      mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
      mplew.write(0);
      mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
      mplew.writeZeroBytes(11);
      return mplew.getPacket();
   }

   public static byte[] enterSnowBall() {
      return rollSnowball(0, null, null);
   }

   public static byte[] hitSnowBall(int team, int damage, int distance, int delay) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HIT_SNOWBALL.getValue());
      mplew.write(team);
      mplew.writeShort(damage);
      mplew.write(distance);
      mplew.write(delay);
      return mplew.getPacket();
   }

   public static byte[] snowballMessage(int team, int message) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SNOWBALL_MESSAGE.getValue());
      mplew.write(team);
      mplew.writeInt(message);
      return mplew.getPacket();
   }

   public static byte[] leftKnockBack() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.LEFT_KNOCK_BACK.getValue());
      return mplew.getPacket();
   }

   public static byte[] hitCoconut(boolean spawn, int id, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HIT_COCONUT.getValue());
      mplew.writeInt(spawn ? '耀' : id);
      mplew.write(spawn ? 0 : type);
      return mplew.getPacket();
   }

   public static byte[] coconutScore(int[] coconutscore) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.COCONUT_SCORE.getValue());
      mplew.writeShort(coconutscore[0]);
      mplew.writeShort(coconutscore[1]);
      return mplew.getPacket();
   }

   public static byte[] updateAriantScore(List<MapleCharacter> players) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
      mplew.write(players.size());

      for (MapleCharacter i : players) {
         mplew.writeMapleAsciiString(i.getName());
         mplew.writeInt(0);
      }

      return mplew.getPacket();
   }

   public static byte[] farmScore(byte wolfscore, byte sheepscore) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FARM_SCORE.getValue());
      mplew.write(wolfscore);
      mplew.write(sheepscore);
      return mplew.getPacket();
   }

   public static byte[] showChaosZakumShrine(boolean spawned, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
      mplew.write(spawned ? 1 : 0);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] showChaosHorntailShrine(boolean spawned, int time) {
      return showHorntailShrine(spawned, time);
   }

   public static byte[] showHorntailShrine(boolean spawned, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
      mplew.write(spawned ? 1 : 0);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] getRPSMode(byte mode, int mesos, int selection, int answer) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RPS_GAME.getValue());
      mplew.write(mode);
      switch (mode) {
         case 6:
            if (mesos != -1) {
               mplew.writeInt(mesos);
            }
            break;
         case 8:
            mplew.writeInt(9000019);
            break;
         case 11:
            mplew.write(selection);
            mplew.write(answer);
      }

      return mplew.getPacket();
   }

   public static byte[] messengerInvite(String from, int messengerid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(3);
      mplew.writeInt(0);
      mplew.writeMapleAsciiString(from);
      mplew.writeInt(0);
      mplew.write(1);
      mplew.writeInt(messengerid);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(0);
      mplew.write(position);
      mplew.writeInt(0);
      PacketHelper.addCharLook(mplew, chr, true, false);
      mplew.writeMapleAsciiString(from);
      mplew.write(channel);
      mplew.write(position);
      mplew.writeInt(chr.getJob());
      return mplew.getPacket();
   }

   public static byte[] removeMessengerPlayer(int position) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(2);
      mplew.write(position);
      return mplew.getPacket();
   }

   public static byte[] updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(8);
      mplew.write(position);
      PacketHelper.addCharLook(mplew, chr, true, false);
      mplew.writeMapleAsciiString(from);
      return mplew.getPacket();
   }

   public static byte[] joinMessenger(int position) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(1);
      mplew.write(position);
      return mplew.getPacket();
   }

   public static byte[] messengerChat(String charname, String text, ReportLogEntry report) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(6);
      mplew.writeMapleAsciiString(charname);
      mplew.writeMapleAsciiString(text);
      report.encode(mplew);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] messengerWhisperChat(String charname, String text, ReportLogEntry report) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(15);
      mplew.writeMapleAsciiString(charname);
      mplew.writeMapleAsciiString(text);
      report.encode(mplew);
      return mplew.getPacket();
   }

   public static byte[] messengerNote(String text, int mode, int mode2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(mode);
      mplew.writeMapleAsciiString(text);
      mplew.write(mode2);
      return mplew.getPacket();
   }

   public static byte[] messengerLike(short like, String charname, String othername) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.writeShort(like);
      mplew.writeMapleAsciiString(charname);
      mplew.writeMapleAsciiString(othername);
      return mplew.getPacket();
   }

   public static byte[] messengerCharInfo(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
      mplew.write(12);
      mplew.writeMapleAsciiString(chr.getName());
      mplew.writeInt(chr.getLevel());
      mplew.writeInt(chr.getJob());
      mplew.writeInt(chr.getFame());
      mplew.writeInt(0);
      if (chr.getGuildId() <= 0) {
         mplew.writeMapleAsciiString("-");
         mplew.writeMapleAsciiString("");
      } else {
         Guild gs = Center.Guild.getGuild(chr.getGuildId());
         if (gs != null) {
            mplew.writeMapleAsciiString(gs.getName());
            if (gs.getAllianceId() > 0) {
               Alliance allianceName = Center.Alliance.getAlliance(gs.getAllianceId());
               if (allianceName != null) {
                  mplew.writeMapleAsciiString(allianceName.getName());
               } else {
                  mplew.writeMapleAsciiString("");
               }
            } else {
               mplew.writeMapleAsciiString("");
            }
         } else {
            mplew.writeMapleAsciiString("-");
            mplew.writeMapleAsciiString("");
         }
      }

      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] removeItemFromDuey(boolean remove, int Package) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DUEY.getValue());
      mplew.write(24);
      mplew.writeInt(Package);
      mplew.write(remove ? 3 : 4);
      return mplew.getPacket();
   }

   public static byte[] checkFailedDuey() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DUEY.getValue());
      mplew.write(9);
      mplew.write(-1);
      return mplew.getPacket();
   }

   public static byte[] sendDuey(byte operation, List<MapleDueyActions> packages, List<MapleDueyActions> expired) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DUEY.getValue());
      mplew.write(operation);
      if (packages == null) {
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
         return mplew.getPacket();
      } else {
         switch (operation) {
            case 9:
               mplew.write(1);
               break;
            case 10:
               mplew.write(0);
               mplew.write(packages.size());

               for (MapleDueyActions dp : packages) {
                  mplew.writeInt(dp.getPackageId());
                  mplew.writeMapleAsciiString_(dp.getSender(), 13);
                  mplew.writeInt(dp.getMesos());
                  mplew.writeLong(PacketHelper.getTime(dp.getExpireTime()));
                  mplew.write(dp.isQuick() ? 1 : 0);
                  mplew.writeMapleAsciiString_(dp.getContent(), 100);
                  mplew.writeZeroBytes(101);
                  if (dp.getItem() != null) {
                     mplew.write(1);
                     PacketHelper.addItemInfo(mplew, dp.getItem());
                  } else {
                     mplew.write(0);
                  }
               }

               if (expired == null) {
                  mplew.write(0);
                  return mplew.getPacket();
               }

               mplew.write(expired.size());

               for (MapleDueyActions dpx : expired) {
                  mplew.writeInt(dpx.getPackageId());
                  mplew.writeMapleAsciiString_(dpx.getSender(), 13);
                  mplew.writeInt(dpx.getMesos());
                  if (dpx.canReceive()) {
                     mplew.writeLong(PacketHelper.getTime(dpx.getExpireTime()));
                  } else {
                     mplew.writeLong(0L);
                  }

                  mplew.write(dpx.isQuick() ? 1 : 0);
                  mplew.writeMapleAsciiString_(dpx.getContent(), 100);
                  mplew.writeInt(0);
                  if (dpx.getItem() != null) {
                     mplew.write(1);
                     PacketHelper.addItemInfo(mplew, dpx.getItem());
                  } else {
                     mplew.write(0);
                  }
               }
         }

         return mplew.getPacket();
      }
   }

   public static byte[] receiveParcel(String from, boolean quick) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DUEY.getValue());
      mplew.write(26);
      mplew.writeMapleAsciiString(from);
      mplew.write(quick ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] getKeymap(MapleKeyLayout[] layout, List<Integer> keyMapped) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.KEYMAP.getValue());

      for (MapleKeyLayout l : layout) {
         packet.write(l == null);
         if (l != null) {
            l.encode(packet);
         }
      }

      if (keyMapped.size() > 0) {
         packet.write(true);

         for (Integer key : keyMapped) {
            packet.writeInt(key);
         }
      } else {
         packet.write(true);
         packet.encodeBuffer(
            HexTool.getByteArrayFromHexString(
               "2A 00 00 00 52 00 00 00 47 00 00 00 49 00 00 00 1D 00 00 00 53 00 00 00 4F 00 00 00 51 00 00 00 02 00 00 00 03 00 00 00 04 00 00 00 05 00 00 00 10 00 00 00 11 00 00 00 12 00 00 00 13 00 00 00 06 00 00 00 07 00 00 00 08 00 00 00 09 00 00 00 14 00 00 00 1E 00 00 00 1F 00 00 00 20 00 00 00 0A 00 00 00 0B 00 00 00 21 00 00 00 22 00 00 00 25 00 00 00 26 00 00 00 31 00 00 00 32 00 00 00"
            )
         );
      }

      return packet.getPacket();
   }

   public static byte[] petAutoHP(int itemId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_AUTO_HP.getValue());
      mplew.writeInt(itemId);
      return mplew.getPacket();
   }

   public static byte[] petAutoMP(int itemId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_AUTO_MP.getValue());
      mplew.writeInt(itemId);
      return mplew.getPacket();
   }

   public static void addRingInfo(PacketEncoder mplew, List<MapleRing> rings) {
      mplew.write(rings.size());

      for (MapleRing ring : rings) {
         mplew.writeLong(ring.getRingId());
         mplew.writeLong(ring.getPartnerRingId());
         mplew.writeInt(ring.getItemId());
      }
   }

   public static void addMRingInfo(PacketEncoder mplew, List<MapleRing> rings, MapleCharacter chr) {
      mplew.write(rings.size());

      for (MapleRing ring : rings) {
         mplew.writeInt(chr.getId());
         mplew.writeInt(ring.getPartnerChrId());
         mplew.writeInt(ring.getItemId());
      }
   }

   public static byte[] updateInnerPotential(byte ability, int skill, int level, int rank) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ENABLE_INNER_ABILITY.getValue());
      mplew.write(1);
      mplew.write(1);
      mplew.writeShort(ability);
      mplew.writeInt(skill);
      mplew.writeShort(level);
      mplew.writeShort(rank);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] HeadTitle(List<Integer> num) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HEAD_TITLE.getValue());

      for (Integer num_ : num) {
         mplew.writeMapleAsciiString("");
         mplew.write(num_ == 0 ? -1 : num_);
      }

      return mplew.getPacket();
   }

   public static byte[] getInternetCafe(byte type, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.INTERNET_CAFE.getValue());
      mplew.write(type);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] spawnRune(RuneStone rune, boolean respawn) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(respawn ? SendPacketOpcode.RESPAWN_RUNE.getValue() : SendPacketOpcode.SPAWN_RUNE.getValue());
      mplew.writeInt(1);
      mplew.writeInt(0);
      mplew.writeInt(2);
      mplew.writeInt(rune.getRuneType().getType());
      mplew.writeInt(rune.getPositionX());
      mplew.writeInt(rune.getPositionY());
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] removeRune(RuneStone rune, MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_RUNE.getValue());
      mplew.writeInt(0);
      mplew.writeInt(chr.getId());
      mplew.writeInt(100);
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static byte[] RuneAction(int type, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RUNE_STONE_USE_ACK.getValue());
      mplew.writeInt(type);
      mplew.writeInt(time);
      if (type == 8) {
         mplew.writeInt(Randomizer.nextInt(3));
         mplew.writeInt(Randomizer.nextInt(3));
         mplew.writeInt(Randomizer.nextInt(3));
         mplew.writeInt(Randomizer.nextInt(3));
      }

      return mplew.getPacket();
   }

   public static byte[] MultiTag(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ZERO_TAG.getValue());
      mplew.writeInt(chr.getId());
      PacketHelper.addCharLook(mplew, chr, true, chr.getGender() == 1, true);
      return mplew.getPacket();
   }

   public static byte[] getWpGain(int gain) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(37);
      mplew.writeInt(gain);
      return mplew.getPacket();
   }

   public static byte[] updateWP(int wp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ZERO_WP.getValue());
      mplew.writeInt(wp);
      return mplew.getPacket();
   }

   public static byte[] shockWave(int skillid, int delay, int direction, Point position) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FINAL_ATTACK_REQUEST.getValue());
      mplew.writeInt(skillid);
      mplew.writeInt(101000102);
      mplew.writeInt(56);
      mplew.writeInt(delay);
      mplew.writeInt(0);
      mplew.write(direction);
      mplew.encodePos(position);
      return mplew.getPacket();
   }

   public static byte[] Clothes(int ct, int ct2) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ZERO_INFO.getValue());
      mplew.write(0);
      mplew.write(1);
      mplew.write(ct);
      mplew.writeShort(ct2);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] Reaction() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
      mplew.write(0);
      mplew.write(0);
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static byte[] spawnArrowFlatter(MapleCharacter chr, int x, int y, byte direction) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_ARROW_FLATTER.getValue());
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.writeInt(chr.getId());
      mplew.writeInt(1);
      mplew.writeInt(x);
      mplew.writeInt(y);
      mplew.write(direction);
      return mplew.getPacket();
   }

   public static byte[] updateSkillStackRequestResult(int skillID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.STACK_SKILL_REQUEST_RESULT.getValue());
      packet.writeInt(skillID);
      packet.write(1);
      return packet.getPacket();
   }

   public static byte[] activeArrow() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ACITVE_ARROW.getValue());
      mplew.writeLong(1L);
      return mplew.getPacket();
   }

   public static byte[] removeArrowFlatter() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REMOVE_ARROW_FLATTER.getValue());
      mplew.writeInt(1);
      mplew.writeInt(1);
      return mplew.getPacket();
   }

   public static byte[] replaceStolenSkill(int base, int skill) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REPLACE_SKILLS.getValue());
      mplew.write(1);
      mplew.write(skill > 0 ? 1 : 0);
      mplew.writeInt(base);
      mplew.writeInt(skill);
      return mplew.getPacket();
   }

   public static byte[] addStolenSkill(int jobNum, int index, int skill, int level) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_STOLEN_SKILLS.getValue());
      mplew.write(1);
      mplew.write(0);
      mplew.writeInt(jobNum);
      mplew.writeInt(index);
      mplew.writeInt(skill);
      mplew.writeInt(level);
      mplew.writeInt(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] removeStolenSkill(int jobNum, int index) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_STOLEN_SKILLS.getValue());
      mplew.write(1);
      mplew.write(3);
      mplew.writeInt(jobNum);
      mplew.writeInt(index);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] viewSkills(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TARGET_SKILL.getValue());
      List<Skill> skillList = chr.getSkills().keySet().stream().filter(skillx -> SkillEncode.is_stealable_skill(skillx.getId())).collect(Collectors.toList());
      mplew.write(1);
      mplew.writeInt(chr.getId());
      mplew.writeInt(skillList.isEmpty() ? 2 : 4);
      mplew.writeInt(chr.getJob());
      mplew.writeInt(skillList.size());

      for (Skill skill : skillList) {
         mplew.writeInt(skill.getId());
      }

      return mplew.getPacket();
   }

   public static byte[] updateCardStack(int total) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PHANTOM_CARD.getValue());
      mplew.write(0);
      mplew.write(total);
      return mplew.getPacket();
   }

   public static byte[] throwGrenade(int playerID, Grenade grenade) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.THROW_GRENADE.getValue());
      mplew.writeInt(playerID);
      grenade.encode(mplew);
      return mplew.getPacket();
   }

   public static byte[] destoryGrenade(int playerID, int objectID) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DESTROY_GRENADE.getValue());
      mplew.writeInt(playerID);
      mplew.writeInt(objectID);
      return mplew.getPacket();
   }

   public static byte[] keyDownAreaMovePath(int playerID, byte[] path) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.KEY_DOWN_AREA_MOVE_PATH.getValue());
      mplew.writeInt(playerID);
      mplew.write(path.length);

      for (int i = 0; i < path.length; i++) {
         mplew.write(path[i]);
      }

      return mplew.getPacket();
   }

   public static byte[] TheSeedBox(short slot, int itemId, int... rewards) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.THE_SEED_ITEM.getValue());
      mplew.writeShort(slot);
      mplew.writeInt(itemId);
      mplew.write(1);
      mplew.writeInt(rewards.length);

      for (int i = 0; i < rewards.length; i++) {
         mplew.writeInt(rewards[i]);
      }

      return mplew.getPacket();
   }

   public static byte[] showForeignDamageSkin(MapleCharacter chr, int skinid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_DAMAGE_SKIN.getValue());
      mplew.writeInt(chr.getId());
      mplew.writeInt(skinid);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] updateDress(int code, MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_DRESS.getValue());
      mplew.writeInt(chr.getId());
      mplew.writeInt(code);
      return mplew.getPacket();
   }

   public static byte[] setOffStateForOnOffSkill(int skillid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SET_OFF_STATE_FOR_ON_OFF_SKILL.getValue());
      mplew.writeInt(skillid);
      return mplew.getPacket();
   }

   public static byte[] resetOnStateForOnOfSkill() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RESET_ON_STATE_FOR_ON_OFF_SKILL.getValue());
      return mplew.getPacket();
   }

   public static byte[] getCreatePsychicLock(PsychicLock lock) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CREATE_PSYCHIC_LOCK.getValue());
      lock.encode(packet);
      return packet.getPacket();
   }

   public static byte[] getRecreatePathPsychicLock(
      int playerId, int skillId, short skillLevel, int action, int actionSpeed, boolean isLeft, List<Pair<Integer, Integer>> skillInfos, List<Integer> keys
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RECREATE_PATH_PSYCHIC_LOCK.getValue());
      packet.writeInt(playerId);
      packet.writeInt(skillId);
      packet.writeShort(skillLevel);
      packet.writeInt(action);
      packet.writeInt(actionSpeed);
      packet.write(isLeft ? 1 : 0);
      packet.writeInt(skillInfos.size());
      skillInfos.forEach(pair -> {
         packet.writeInt(pair.left);
         packet.writeInt(pair.right);
      });
      packet.writeInt(keys.size());
      keys.forEach(key -> packet.writeInt(key));
      return packet.getPacket();
   }

   public static byte[] getReleasePsychicLock(int playerId, int idx) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RELEASE_PSYCHIC_LOCK.getValue());
      packet.writeInt(playerId);
      packet.writeInt(idx);
      return packet.getPacket();
   }

   public static byte[] getReleasePsychicLockMob(int playerId, int mobId) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RELEASE_PSYCHIC_LOCK_MOB.getValue());
      packet.writeInt(playerId);
      packet.write(1);
      packet.writeInt(mobId);
      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] getCreatePsychicArea(int playerId, PsychicArea area) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CREATE_PSYCHIC_AREA.getValue());
      packet.writeInt(playerId);
      area.encode(packet);
      return packet.getPacket();
   }

   public static byte[] getReleasePsychicArea(int playerId, int key) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RELEASE_PSYCHIC_AREA.getValue());
      packet.writeInt(playerId);
      packet.writeInt(key);
      return packet.getPacket();
   }

   public static byte[] getDoActivePsychicArea(int psychicKey, int idx) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DO_ACTIVE_PSYCHIC_AREA.getValue());
      packet.writeInt(psychicKey);
      packet.writeInt(idx);
      return packet.getPacket();
   }

   public static byte[] setDressChanged(boolean set, boolean infinity) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_DRESS_CHANGED.getValue());
      packet.write(set ? 1 : 0);
      packet.write(infinity ? 1 : 0);
      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] setDefaultWingItem(int cid, int itemId) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_DEFAULT_WING_ITEM.getValue());
      packet.writeInt(cid);
      packet.writeInt(itemId);
      return packet.getPacket();
   }

   public static byte[] setSoulEffect(MapleCharacter player, byte use) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_SOUL_EFFECT.getValue());
      packet.writeInt(player.getId());
      packet.write(use);
      return packet.getPacket();
   }

   public static byte[] getRegisterExtraSkill(int skillID, int x, int y, boolean isLeft, List<ExtraSkillInfo> extraSkills, int size) {
      return getRegisterExtraSkill(skillID, x, y, isLeft, extraSkills, size, Collections.emptyList());
   }

   public static byte[] getRegisterExtraSkill(int skillID, int x, int y, boolean isLeft, List<ExtraSkillInfo> extraSkills, int size, List<Integer> targets) {
      return getRegisterExtraSkill(skillID, x, y, isLeft, extraSkills, size, Collections.emptyList(), targets);
   }

   public static byte[] getRegisterExtraSkill(
      int skillID, int x, int y, boolean isLeft, List<ExtraSkillInfo> extraSkills, int size, List<Integer> beforeTargets, List<Integer> targets
   ) {
      return getRegisterExtraSkill(skillID, x, y, isLeft, extraSkills, size, beforeTargets, targets, 0);
   }

   public static byte[] getRegisterExtraSkill(
      int skillID,
      int x,
      int y,
      boolean isLeft,
      List<ExtraSkillInfo> extraSkills,
      int size,
      List<Integer> beforeTargets,
      List<Integer> targets,
      int areaObjectID
   ) {
      return getRegisterExtraSkill(skillID, 0, x, y, isLeft, extraSkills, size, beforeTargets, targets, areaObjectID);
   }

   public static byte[] getRegisterExtraSkill(int skillID, int x, int y, boolean isLeft, ExtraSkillInfo extraSkill, int count, int show) {
      List<ExtraSkillInfo> extraSkills = new ArrayList<>();
      extraSkills.add(extraSkill);
      return getRegisterExtraSkill(skillID, 0, x, y, isLeft, extraSkills, 1, Collections.emptyList(), Collections.emptyList(), 0, count, show);
   }

   public static byte[] getRegisterExtraSkill(
      int skillID,
      int reasonSkillID,
      int x,
      int y,
      boolean isLeft,
      List<ExtraSkillInfo> extraSkills,
      int size,
      List<Integer> beforeTargets,
      List<Integer> targets,
      int areaObjectID
   ) {
      return getRegisterExtraSkill(skillID, reasonSkillID, x, y, isLeft, extraSkills, size, beforeTargets, targets, areaObjectID, 0, 0);
   }

   public static byte[] getRegisterExtraSkill(
      int skillID,
      int reasonSkillID,
      int x,
      int y,
      boolean isLeft,
      List<ExtraSkillInfo> extraSkills,
      int size,
      List<Integer> beforeTargets,
      List<Integer> targets,
      int areaObjectID,
      int count,
      int show
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.REGISTER_EXTRA_SKILL.getValue());
      packet.writeInt(skillID);
      packet.writeShort(extraSkills.size());
      extraSkills.forEach(s -> {
         packet.writeInt(reasonSkillID);
         packet.writeInt(s.skillID);
         packet.writeInt(x);
         packet.writeInt(y);
         packet.writeShort(isLeft ? 1 : 0);
         packet.writeInt(s.delay);
         packet.writeInt(size);
         packet.writeInt(beforeTargets.size());
         beforeTargets.forEach(packet::writeInt);
         packet.writeInt(targets.size());
         targets.forEach(packet::writeInt);
         packet.writeInt(reasonSkillID == 0 ? areaObjectID : 0);
         packet.writeInt(reasonSkillID != 0 ? areaObjectID : 0);
         packet.writeInt(0);
         packet.writeInt(count);
         packet.write(show);
      });
      return packet.getPacket();
   }

   public static byte[] setSlowDown() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_SLOW_DOWN.getValue());
      packet.writeShort(30);
      packet.writeShort(360);
      return packet.getPacket();
   }

   public static byte[] summonAssistAttackRequest(int characterID, int summonOid, int skillType) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMONED_ASSIST_ATTACK_REQUEST.getValue());
      packet.writeInt(characterID);
      packet.writeInt(summonOid);
      packet.writeInt(skillType);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] summonAttackActive(int characterID, int summonOid, int flag) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMONED_ATTACK_ACTIVE.getValue());
      packet.writeInt(characterID);
      packet.writeInt(summonOid);
      packet.write(flag);
      return packet.getPacket();
   }

   public static byte[] summonBeholderShock(int characterID, int summonOid, int skillID) {
      return summonBeholderShock(characterID, summonOid, skillID, true);
   }

   public static byte[] summonBeholderShock(int characterID, int summonOid, int skillID, boolean unk) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_BEHOLDER_SHOCK.getValue());
      packet.writeInt(characterID);
      packet.writeInt(summonOid);
      packet.writeInt(skillID);
      packet.write(unk);
      return packet.getPacket();
   }

   public static byte[] momentAreaOnOffAll(List<String> info) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOMENT_AREA_ON_OFF_ALL.getValue());
      packet.writeShort(0);
      packet.write(info.size() > 0 ? 1 : 0);
      if (info.size() > 0) {
         packet.writeInt(info.size());
         info.forEach(packet::writeMapleAsciiString);
      }

      return packet.getPacket();
   }

   public static byte[] fishingInfo(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FISHING_INFO.getValue());
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] fishingReward(int cid, int itemID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FISHING_REWARD.getValue());
      packet.writeInt(cid);
      packet.writeInt(itemID);
      return packet.getPacket();
   }

   public static byte[] fishingZoneInfo(int info) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FISHING_ZONE_INFO.getValue());
      packet.writeInt(info);
      return packet.getPacket();
   }

   public static byte[] getQuestSay(int qid, int npcid, int nextQuest, boolean startnavi) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
      packet.write(11);
      packet.writeInt(qid);
      packet.writeInt(npcid);
      packet.writeInt(nextQuest);
      packet.write(startnavi);
      return packet.getPacket();
   }

   public static byte[] addPopupSay(int npcID, int duration, String title, String msg) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ADD_POPUP_SAY.getValue());
      packet.writeInt(npcID);
      packet.writeInt(duration);
      packet.writeMapleAsciiString(title);
      packet.writeMapleAsciiString(msg);
      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] addPopupSay(int npcID, int duration, String title, String msg, int value) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ADD_POPUP_SAY.getValue());
      packet.writeInt(npcID);
      packet.writeInt(duration);
      packet.writeMapleAsciiString(title);
      packet.writeMapleAsciiString(msg);
      packet.write(value);
      return packet.getPacket();
   }

   public static byte[] userThrowingBombAck(int skillID, int skillLevel, List<Integer> idx) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_THROWING_BOMB_ACK.getValue());
      packet.write(1);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      packet.writeInt(idx.size());
      idx.forEach(packet::writeInt);
      return packet.getPacket();
   }

   public static byte[] userBonusAttackRequest(int skillID, boolean force, List<Pair<Integer, Integer>> mobList) {
      return userBonusAttackRequest(skillID, force, mobList, 0);
   }

   public static byte[] userBonusAttackRequest(int skillID, boolean force, List<Pair<Integer, Integer>> mobList, int jaguarBleedingStack) {
      return userBonusAttackRequest(skillID, force, mobList, jaguarBleedingStack, 0);
   }

   public static byte[] userBonusAttackRequest(int skillID, boolean force, List<Pair<Integer, Integer>> mobList, int jaguarBleedingStack, int delay) {
      return userBonusAttackRequest(skillID, force, mobList, jaguarBleedingStack, delay, 0);
   }

   public static byte[] userBonusAttackRequest(
      int skillID, boolean force, final List<Pair<Integer, Integer>> mobList, int jaguarBleedingStack, int delay, int afterImage
   ) {
      List<Triple<Integer, Integer, Integer>> mList = new ArrayList<Triple<Integer, Integer, Integer>>() {
         {
            for (Pair<Integer, Integer> pair : mobList) {
               this.add(new Triple<>(pair.left, pair.right, 0));
            }
         }
      };
      return userBonusAttackRequest(skillID, mList, force, jaguarBleedingStack, delay, afterImage);
   }

   public static byte[] userBonusAttackRequest(
      int skillID, List<Triple<Integer, Integer, Integer>> mobList, boolean force, int jaguarBleedingStack, int delay, int afterImage
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_BONUS_ATTACK_REQUEST.getValue());
      packet.writeInt(skillID);
      packet.writeInt(mobList.size());
      packet.write(force);
      packet.writeInt(delay);
      packet.writeInt(jaguarBleedingStack);
      packet.writeInt(afterImage);
      if (skillID != 35121019 && skillID != 35141002) {
         for (Triple<Integer, Integer, Integer> p : mobList) {
            packet.writeInt(p.left);
            packet.writeInt(p.mid);
            if (skillID == 400041030) {
               packet.writeInt(p.right);
            }
         }

         if (skillID == 35121019 || skillID == 35141002) {
            packet.writeInt(0);
            packet.writeInt(0);
         }

         if (skillID == 400011133) {
            packet.writeInt(afterImage);
         }

         if (skillID == 31141502) {
            packet.writeInt(31000004);
         }

         if (skillID == 51111015) {
            packet.write(0);
         }

         if (skillID == 63141502) {
            packet.writeInt(104);
            packet.writeInt(152);
         }

         if (skillID == 400051067 || skillID == 400051065) {
            packet.writeInt(0);
         }

         if (skillID == 400011117) {
            packet.writeInt(afterImage);
         }

         if (skillID == 4361501) {
            packet.writeInt(4341004);
         }

         if ((skillID - 400051065 & -3) == 0) {
            packet.writeInt(0);
            packet.encodePos4Byte(new Point(0, 0));
            packet.encodePos4Byte(new Point(0, 0));
            packet.write(false);
         }
      } else {
         packet.writeInt(0);
         packet.writeInt(800);

         for (Triple<Integer, Integer, Integer> px : mobList) {
            packet.writeInt(px.left);
            packet.writeInt(px.mid);
         }
      }

      return packet.getPacket();
   }

   public static byte[] finalAttackRequest(boolean fromAttack, int finalAttackSkill, int attackSkill, int attackIdx, List<Integer> targets) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FINAL_ATTACK_REQUEST.getValue());
      packet.writeInt(fromAttack ? 1 : 0);
      packet.writeInt(attackSkill);
      packet.writeInt(finalAttackSkill);
      packet.writeInt(attackIdx);
      packet.writeInt(targets.size());
      targets.forEach(packet::writeInt);
      return packet.getPacket();
   }

   public static byte[] setBRMOnServerCalcResult(boolean active) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BRM_SERVER_ON_CALC_REQUEST_RESULT.getValue());
      packet.write(active);
      return packet.getPacket();
   }

   public static byte[] setBRMOnDotDamageInfo(long damage, int skillID, int mobTemplateID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BRM_DOT_DAMAGE_INFO.getValue());
      packet.writeLong(damage);
      packet.writeInt(1);
      packet.write(1);
      packet.writeInt(1);
      packet.writeInt(skillID);
      packet.writeInt(mobTemplateID);
      return packet.getPacket();
   }

   public static byte[] setBRMOnUpdateResult() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BRM_UPDATE_RESULT.getValue());
      return packet.getPacket();
   }

   public static byte[] getDeathCount(MapleCharacter chr, int count) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEATH_COUNT.getValue());
      packet.writeInt(chr.getId());
      packet.writeInt(count);
      return packet.getPacket();
   }

   public static byte[] getPartyDeathCount(List<MapleCharacter> chrList, int count) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PARTY_DEATH_COUNT.getValue());
      packet.writeShort(count);
      packet.writeShort(chrList.size());
      chrList.forEach(chr -> {
         packet.writeInt(chr.getId());
         packet.writeInt(chr.getDeathCount());
      });
      return packet.getPacket();
   }

   public static byte[] getActiveAttackForceAtom(MapleCharacter player, int mobID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ACTIVE_ATTACK_FORCE_ATOM.getValue());
      packet.writeInt(12345);
      packet.writeInt(player.getId());
      packet.writeInt(1);
      packet.writeInt(1);
      packet.writeInt(1);
      packet.writeInt(mobID);
      return packet.getPacket();
   }

   public static byte[] getCreateForceAtom(ForceAtom forceAtom) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CREATE_FORCE_ATOM.getValue());
      forceAtom.encode(packet);
      return packet.getPacket();
   }

   public static byte[] getCreateForceAtomParallel(ForceAtom_Parallel forceAtom) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CREATE_SPELL_BULLETS.getValue());
      forceAtom.encode(packet);
      return packet.getPacket();
   }

   public static byte[] getActiveAttackLightningCascade(int attackSkill, int skillID, int skillLevel) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ACTIVE_ATTACK_LIGHTNING_CASCADE.getValue());
      packet.writeInt(attackSkill);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      return packet.getPacket();
   }

   public static byte[] getShadowServantExtendChangePos(int posX, int posY, byte mode) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SERVANT_EXTEND_CHANGE_POS.getValue());
      packet.writeInt(posX);
      packet.writeInt(posY);
      packet.write(mode);
      return packet.getPacket();
   }

   public static byte[] getActiveChainArtsFury(int posX, int posY) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ACTIVE_CHAIN_ARTS_FURY.getValue());
      packet.writeInt(posX);
      packet.writeInt(posY);
      return packet.getPacket();
   }

   public static byte[] getJaguarSkill(int skillID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JAGUAR_SKILL.getValue());
      packet.writeInt(skillID);
      return packet.getPacket();
   }

   public static byte[] summonCrystalTeleport(int playerID, int oid, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_CRYSTAL_TELEPORT.getValue());
      packet.writeInt(playerID);
      packet.writeInt(oid);
      packet.writeInt(pos.x);
      packet.writeInt(pos.y);
      return packet.getPacket();
   }

   public static byte[] summonCrystalMove(int playerID, int oid, int skillID, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_CRYSTAL_MOVE.getValue());
      packet.writeInt(playerID);
      packet.writeInt(oid);
      packet.writeInt(skillID);
      packet.writeInt(1);
      packet.writeInt(pos.x);
      packet.writeInt(pos.y);
      return packet.getPacket();
   }

   public static byte[] summonEnergyAttack(int playerID, int oid, int skillID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_ENERGY_ATTACK.getValue());
      packet.writeInt(playerID);
      packet.writeInt(oid);
      packet.writeInt(skillID);
      return packet.getPacket();
   }

   public static byte[] megaSmasherAttack(int playerID, Point pos, boolean isLeft, int chargeTime) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MEGA_SMASHER_REQUEST.getValue());
      packet.writeInt(playerID);
      packet.encodePos4Byte(pos);
      packet.write(isLeft);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] crystalReactionCooltime(int playerID, int objectID, int passiveReactionSkillID, int cooltimeMS) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_SET_ENERGY.getValue());
      packet.writeInt(playerID);
      packet.writeInt(objectID);
      packet.writeInt(5);
      packet.writeInt(passiveReactionSkillID);
      packet.writeInt(cooltimeMS);
      return packet.getPacket();
   }

   public static byte[] summonCrystalToggleSkill(MapleCharacter player, Summoned summon, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_CRYSTAL_TOGGLE_SKILL.getValue());
      packet.writeInt(player.getId());
      packet.writeInt(summon.getObjectId());
      packet.writeInt(type);
      if (type == 2) {
         List<Integer> idx = new ArrayList<>();

         for (int index = 0; index < summon.getEnergyLevel(); index++) {
            if (summon.getEnableEnergySkill(index) > 0) {
               idx.add(index);
            }
         }

         packet.writeInt(idx.size());

         for (Integer indexx : idx) {
            packet.writeInt(indexx + 1);
            packet.writeInt(1);
         }
      }

      return packet.getPacket();
   }

   public static byte[] summonForcedAction(MapleCharacter player, int objectID, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_FORCED_ACTION.getValue());
      packet.writeInt(player.getId());
      packet.writeInt(objectID);
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] summonSetEnergy(MapleCharacter player, Summoned summon, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_SET_ENERGY.getValue());
      packet.writeInt(player.getId());
      packet.writeInt(summon.getObjectId());
      packet.writeInt(type);
      switch (type) {
         case 2:
            packet.writeInt(summon.getEnergyCharge());
            packet.writeInt(summon.getEnergyLevel());
         default:
            return packet.getPacket();
      }
   }

   public static byte[] summonEnergyUpdate(int playerID, Summoned summon, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SUMMON_ENERGY_UPDATE.getValue());
      packet.writeInt(playerID);
      packet.writeInt(summon.getObjectId());
      packet.writeInt(type);
      switch (type) {
         case 3:
            packet.writeInt(0);
         default:
            return packet.getPacket();
      }
   }

   public static byte[] BingoGameState(int type, int round) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_GAME_STATE.getValue());
      packet.writeInt(type);
      packet.writeInt(round);
      packet.writeInt(5);
      packet.writeInt(1);
      return packet.getPacket();
   }

   public static byte[] BingoEnterGame(int[][] table) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_ENTER_GAME.getValue());
      packet.writeInt(1);
      packet.writeInt(1);
      packet.writeInt(0);
      packet.writeInt(5);
      packet.writeInt(5);
      packet.writeInt(1);
      packet.write(1);
      packet.writeInt(1);
      packet.writeInt(25);

      for (int y = 0; y < 5; y++) {
         for (int x = 0; x < 5; x++) {
            packet.writeInt(table[x][y]);
         }
      }

      return packet.getPacket();
   }

   public static byte[] BingoHostNumberReady() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_HOST_NUMBER_READY.getValue());
      return packet.getPacket();
   }

   public static byte[] BingoHostNumber(int number, int leftcount) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_HOST_NUMBER.getValue());
      packet.writeInt(number);
      packet.writeInt(leftcount);
      return packet.getPacket();
   }

   public static byte[] BingoCheckNumberAck(int number) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_CHECK_NUMBER_ACK.getValue());
      packet.writeInt(1);
      packet.writeInt(0);
      packet.writeInt(number);
      packet.writeZeroBytes(12);
      return packet.getPacket();
   }

   public static byte[] BingoCheckNumberAck(int index, int type, int junk) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_CHECK_NUMBER_ACK.getValue());
      packet.writeInt(1);
      packet.writeInt(index);
      packet.writeInt(junk);
      packet.writeInt(0);
      packet.writeInt(1);
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] BingoAddRank(MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.HBINGO_ADD_RANK.getValue());
      packet.writeInt(1);
      packet.writeInt(chr.getId());
      packet.writeMapleAsciiString(chr.getName());
      packet.writeInt(0);
      packet.writeInt(1);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] playSE(String SE) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      packet.write(7);
      packet.writeMapleAsciiString(SE);
      packet.writeInt(128);
      packet.writeInt(0);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] getPlayAmbientSound(String path, int volume, int a) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAY_AMBIENT_SOUND.getValue());
      mplew.writeMapleAsciiString(path);
      mplew.writeInt(volume);
      mplew.writeInt(a);
      return mplew.getPacket();
   }

   public static byte[] getStopAmbientSound(String path) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.STOP_AMBIENT_SOUND.getValue());
      mplew.writeMapleAsciiString(path);
      return mplew.getPacket();
   }

   public static byte[] getSpecialMapSound(String path) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPECIAL_MAP_SOUND.getValue());
      mplew.writeMapleAsciiString(path);
      return mplew.getPacket();
   }

   public static byte[] AddWreckage(int cid, Point pos, int duration, int oid, int skillID, int count) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ADD_WRECKAGE.getValue());
      packet.writeInt(cid);
      packet.writeInt(pos.x);
      packet.writeInt(pos.y);
      packet.writeInt(0);
      packet.writeInt(duration);
      packet.writeInt(oid);
      packet.writeInt(skillID);
      packet.writeInt(0);
      packet.writeInt(count);
      return packet.getPacket();
   }

   public static byte[] DelWreckage(int cid, List<Wreckage> oid, boolean darkFogCooltime) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEL_WRECKAGE.getValue());
      packet.writeInt(cid);
      packet.writeInt(oid.size());
      packet.write(darkFogCooltime ? 0 : 1);
      packet.write(0);
      oid.forEach(o -> packet.writeInt(o.getObjectId()));
      return packet.getPacket();
   }

   public static byte[] getDailyGiftRecord(String value) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
      packet.write(31);
      packet.writeInt(15);
      packet.writeMapleAsciiString(value);
      return packet.getPacket();
   }

   public static void DailyGift(PacketEncoder packet) {
      packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      packet.writeLong(PacketHelper.getTime(-2L));
      int v4 = 28;
      packet.writeInt(v4);
      packet.writeInt(2);
      packet.writeInt(16700);
      packet.writeInt(300);
      packet.writeMapleAsciiString("mvpTooltip_0");
      packet.writeInt(MapleDailyGift.dailyItems.size());

      for (MapleDailyGiftInfo item : MapleDailyGift.dailyItems) {
         packet.writeInt(item.getId());
         packet.writeInt(item.getItemId());
         packet.writeInt(item.getQuantity());
         packet.write(1);
         packet.writeInt(item.getSN() > 0 ? 0 : 10080);
         packet.write(item.getSN() > 0 ? 1 : 0);
         packet.writeInt(item.getSN());
         packet.writeInt(0);
         packet.write(item.getSN() > 0 ? 1 : 0);
      }

      packet.writeInt(33);
      packet.writeInt(0);
      int v8 = 0;
      packet.writeInt(v8);
   }

   public static byte[] OnDailyGift(byte type, int nRet, int nItemID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DAILY_GIFT.getValue());
      packet.write(type);
      if (type == 2) {
         packet.writeInt(nRet);
         packet.writeInt(nItemID);
      } else {
         packet.write(1);
         DailyGift(packet);
         int v10 = 0;
         packet.writeInt(0);

         for (int i = 0; i < v10; i++) {
            DailyGift(packet);
         }
      }

      return packet.getPacket();
   }

   public static byte[] getMasterPieceFailed(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MASTER_PIECE_RESULT.getValue());
      packet.write(0);
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] getMasterPieceReward(Item item) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MASTER_PIECE_RESULT.getValue());
      packet.write(1);
      PacketHelper.addItemInfo(packet, item);
      return packet.getPacket();
   }

   public static byte[] getStackEventGauge(int point, int coin) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.STACK_EVENT_GAUGE.getValue());
      boolean unk = true;
      packet.write(unk);
      packet.writeInt(104551);
      packet.writeInt(0);
      packet.writeMapleAsciiString("UI/UIWindowEvent.img/2020neoCoin");
      packet.writeInt(600000);
      packet.writeInt(0);
      packet.write(0);
      packet.writeDouble(1.0);
      if (unk) {
         packet.writeInt(point);
         packet.writeInt(coin);
         packet.write(0);
         packet.writeInt(System.currentTimeMillis());
         packet.writeInt(51);
      }

      return packet.getPacket();
   }

   public static byte[] getUpdateStackEventGauge(int delta, int point, int coin, int deltaCoin, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.UPDATE_STACK_EVENT_GUAGE.getValue());
      packet.writeInt(point);
      packet.writeInt(delta);
      packet.write(0);
      packet.writeInt(coin);
      packet.writeInt(deltaCoin);
      packet.writeInt(type);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(delta);
      packet.writeInt(0);
      packet.writeMapleAsciiString(DBConfig.isGanglim ? "후원 캐시" : "강림 포인트");
      return packet.getPacket();
   }

   public static byte[] FrozenLinkMobCount(int FrozenCount) {
      PacketEncoder w = new PacketEncoder();
      w.writeShort(SendPacketOpcode.FROZEN_LINK_MOB_COUNT.getValue());
      w.write(1);
      w.writeInt(FrozenCount);
      w.writeInt(0);
      return w.getPacket();
   }

   public static byte[] fieldSetVariable(String value, String value2) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_VALUE_VARIABLE.getValue());
      packet.writeMapleAsciiString(value);
      packet.writeMapleAsciiString(value2);
      return packet.getPacket();
   }

   public static byte[] fieldValue(String value, String value2) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_VALUE.getValue());
      packet.writeMapleAsciiString(value);
      packet.writeMapleAsciiString(value2);
      return packet.getPacket();
   }

   public static byte[] onUserTeleport(int x, int y) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.REVIVE_IN_CUR_FIELD_NO_TRANSFER.getValue());
      packet.writeInt(x);
      packet.writeInt(y);
      return packet.getPacket();
   }

   public static byte[] fieldAttackObj_Create(FieldAttackObj obj) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_ATTACK_OBJ_CREATE.getValue());
      obj.encode(packet);
      return packet.getPacket();
   }

   public static byte[] fieldAttackObj_SetAttack(int objectID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_ATTACK_OBJ_SET_ATTACK.getValue());
      packet.writeInt(objectID);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] fieldAttackObj_Remove(int objectID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_ATTACK_OBJ_REMOVE_BY_KEY.getValue());
      packet.writeInt(objectID);
      return packet.getPacket();
   }

   public static byte[] getB2BodyAck(int type, int idx) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.B2BODY_ACK.getValue());
      packet.writeInt(type);
      packet.writeInt(idx);
      return packet.getPacket();
   }

   public static byte[] getBuzzingHouseCoinCount(int coin) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BUZZING_HOUSE_COIN_COUNT.getValue());
      packet.writeInt(coin);
      return packet.getPacket();
   }

   public static byte[] getBuzzingHouseRequest(int velocity, int errorAllowance) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BUZZING_HOUSE_REQUEST.getValue());
      packet.writeInt(300);
      packet.writeInt(5);
      packet.writeInt(0);
      packet.writeInt(1932694);
      boolean a = true;
      packet.writeInt(a ? 1 : 0);
      if (a) {
         packet.writeInt(0);
         packet.writeInt(velocity);
         packet.writeInt(errorAllowance);
         packet.writeInt(3);
         packet.writeInt(5);
         packet.writeInt(0);
         packet.writeInt(200);
         packet.writeInt(200);
      }

      return packet.getPacket();
   }

   public static byte[] getBuzzingHouseResult(int command) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BUZZING_HOUSE_RESULT.getValue());
      packet.writeInt(command);
      return packet.getPacket();
   }

   public static byte[] WeatherPacket_Add(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FieldWeather_Add.getValue());
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] WeatherPacket_Remove(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FieldWeather_Remove.getValue());
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] breakTimeFieldEnter() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BREAK_TIME_FIELD_ENTER.getValue());
      return packet.getPacket();
   }

   public static byte[] createObstacle(ObstacleAtomCreateType oact, ObstacleInRowInfo oiri, ObstacleRadialInfo ori, Set<ObstacleAtom> atomInfos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.OBSTACLE_ATOM_CREATE.getValue());
      packet.writeInt(0);
      packet.writeInt(atomInfos.size());
      packet.write(oact.getValue());
      if (oact == ObstacleAtomCreateType.IN_ROW) {
         oiri.encode(packet);
      } else if (oact == ObstacleAtomCreateType.RADIAL) {
         ori.encode(packet);
      }

      for (ObstacleAtom atomInfo : atomInfos) {
         packet.write(true);
         atomInfo.encode(packet, false);
         if (oact == ObstacleAtomCreateType.DIAGONAL) {
            atomInfo.getObstacleDiagonalInfo().encode(packet);
         }
      }

      return packet.getPacket();
   }

   public static byte[] createSingleObstacle(ObstacleAtomCreateType oact, ObstacleInRowInfo oiri, ObstacleRadialInfo ori, ObstacleAtom atom) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SINGLE_OBSTACLE_ATOM_CREATE.getValue());
      packet.write(oact.getValue());
      packet.write(true);
      atom.encode(packet, true);
      packet.write(false);
      packet.writeInt(0);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] clearObstacle() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.OBSTACLE_ATOM_CLEAR.getValue());
      return packet.getPacket();
   }

   public static byte[] setAutoRespawn(int waitTime, int respawnTime) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_AUTO_RESPAWN.getValue());
      packet.writeInt(waitTime);
      packet.writeInt(respawnTime);
      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] getBounceAttackSkill(MapleMonster mob, MobSkillInfo mobSkill, boolean afterConvexSkill) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BOUNCE_ATTACK_SKILL.getValue());
      packet.writeInt(mob.getObjectId());
      packet.writeInt(mobSkill.getSkillId());
      packet.writeInt(mobSkill.getSkillLevel());
      packet.write(afterConvexSkill);
      if (afterConvexSkill) {
         int count = 2;
         packet.writeInt(count);
         packet.write(true);
         packet.writeInt(-500);
         packet.writeInt(100000);
         packet.writeInt(15);
         packet.writeInt(0);
         packet.writeInt(10000);

         for (int i = 0; i < count; i++) {
            packet.writeInt(mob.getObjectId() + i + 1);
         }
      } else {
         Point point = mob.getTruePosition();
         packet.writeInt(0);
         packet.writeInt(-350);
         int count = mobSkill.getSkillStatIntValue(MobSkillStat.count);
         packet.writeInt(count);

         for (int i = 0; i < count; i++) {
            packet.writeInt(mob.getObjectId() + i + 1);
            packet.writeInt(mobSkill.getLt().x);
            packet.writeInt(mobSkill.getLt().y);
         }

         packet.writeInt(0);
         packet.writeInt(10);
         packet.writeInt(20000);
         packet.writeInt(mobSkill.getSkillStatIntValue(MobSkillStat.delay));
         packet.writeInt(0);
         packet.write(mobSkill.getSkillStatIntValue(MobSkillStat.noGravity));
         boolean notDestroyByCollide = mobSkill.getSkillStatIntValue(MobSkillStat.notDestroyByCollide) != 0;
         packet.write(notDestroyByCollide);
         if (mobSkill.getSkillId() == MobSkillID.BOUNCE_ATTACK.getVal() && (mobSkill.getSkillLevel() == 3 || mobSkill.getSkillLevel() == 4)) {
            packet.writeInt(mobSkill.getRb2().x);
            packet.writeInt(mobSkill.getRb2().y);
         }

         if (notDestroyByCollide) {
            packet.writeInt(10);
            packet.writeInt(200);
            packet.writeInt(24);
            packet.writeInt(8);
         }
      }

      return packet.getPacket();
   }

   public static byte[] setCharacterRegDate() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_CHARACTER_REG_DATE.getValue());
      packet.writeLong(131812543200000000L);
      return packet.getPacket();
   }

   public static byte[] userTossedBySkill(int playerID, int mobObjectID, int mobSkillID, int mobSkillLevel, Point pos) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_TOSSED_BY_SKILL.getValue());
      packet.writeInt(playerID);
      packet.writeInt(mobObjectID);
      packet.writeInt(mobSkillID);
      packet.writeInt(mobSkillLevel);
      packet.encodePos(pos);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] syncDynamicFoothold(MapleDynamicFoothold foothold) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SYNC_DYNAMIC_FOOTHOLD.getValue());
      packet.writeInt(foothold.getDynamicFootholds().size());
      foothold.getDynamicFootholds().forEach(f -> f.encode(packet));
      return packet.getPacket();
   }

   public static byte[] damageSkinSaveResult(byte resultType, DamageSkinSaveInfo info) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DAMAGE_SKIN_SAVE_RESULT.getValue());
      packet.write(resultType);
      packet.write(4);
      info.encode(packet);
      return packet.getPacket();
   }

   public static byte[] sendCreateFallingCatcher(String effect, int idx, List<Point> positions) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_CREATE_FALLING_CATCHER.getValue());
      packet.writeMapleAsciiString(effect);
      packet.writeInt(idx);
      packet.writeInt(positions.size());
      positions.forEach(p -> {
         packet.writeInt(p.x);
         packet.writeInt(p.y);
      });
      return packet.getPacket();
   }

   public static byte[] sendWeatherEffectNotice(int type, int duration, boolean forced, String msg) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WEATHER_EFFECT_NOTICE.getValue());
      packet.writeMapleAsciiString(msg);
      packet.writeInt(type);
      packet.writeInt(duration);
      packet.write(forced);
      return packet.getPacket();
   }

   public static byte[] setTemporarySkill(int index, TemporarySkill[] skills) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_TEMPORARY_SKILL_SET.getValue());
      packet.writeInt(index);
      packet.writeInt(index);
      packet.write(0);
      packet.writeInt(0);
      packet.writeInt(skills.length);

      for (TemporarySkill s : skills) {
         s.encode(packet);
      }

      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] eliteBossCurse(int level) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ELITE_BOSS_CURSE_STATE.getValue());
      if (level == 0 || level == -1) {
         packet.writeMapleAsciiString("룬을 해방하여 엘리트 보스의 저주를 풀어야 합니다!!\\r\\n잠시 후 필드에 엘리트 보스의 저주가 시작됩니다!!");
      } else if (level != -2) {
         packet.writeMapleAsciiString(
            "룬을 해방하여 엘리트 보스의 저주를 풀어야 합니다!!\\r\\n저주 " + level + "단계 :  경험치 획득, 드롭률 " + GameConstants.getCursedRunesRate(level) + "% 감소 효과 적용 중"
         );
      } else {
         packet.writeMapleAsciiString("");
      }

      packet.writeInt(231);
      packet.write(0);
      packet.write(0);
      packet.writeInt(GameConstants.getCursedRunesRate(level));
      packet.writeInt(GameConstants.getCursedRunesRate(level));
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] onSkilllUseRequest(int skillID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SKILL_USE_RESULT.getValue());
      packet.write(1);
      packet.writeInt(skillID);
      return packet.getPacket();
   }

   public static byte[] createSecondAtom(SecondAtom secondAtom) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CREATE_SECOND_ATOM.getValue());
      secondAtom.encode(packet);
      return packet.getPacket();
   }

   public static byte[] removeSecondAtom(int playerID, int key) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.REMOVE_SECOND_ATOM.getValue());
      packet.writeInt(playerID);
      packet.writeInt(1);
      packet.writeInt(key);
      packet.writeInt(0);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] secondAtomAttack(int playerID, int key, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SECOND_ATOM_ATTACK.getValue());
      packet.writeInt(playerID);
      packet.writeInt(key);
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] maplecabinetResult(int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MAPLE_CABINET.getValue());
      packet.writeInt(type);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] maplecabinetResult(MapleCabinet cabinet) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MAPLE_CABINET.getValue());
      packet.writeInt(9);
      cabinet.encode(packet);
      return packet.getPacket();
   }

   public static byte[] miniMapOnOff(int onoff) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MINI_MAP_ON_OFF.getValue());
      packet.write(onoff);
      return packet.getPacket();
   }

   public static byte[] userWarpPortal(String portal) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_WARP_PORTAL.getValue());
      packet.writeMapleAsciiString(portal);
      return packet.getPacket();
   }

   public static byte[] setBossMode(int mode) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_BOSS_MODE.getValue());
      packet.write(mode);
      return packet.getPacket();
   }

   public static byte[] consumeItemCooltime(int seconds) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CONSUME_ITEM_COOLTIME.getValue());
      packet.writeInt(seconds);
      return packet.getPacket();
   }

   public static byte[] incPeaceMakerCount(int key, int delta) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.INC_PEACE_MAKER_COUNT.getValue());
      packet.writeInt(key);
      packet.writeInt(delta);
      return packet.getPacket();
   }

   public static byte[] skillEffectOnOff(int playerID, int value) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SKILL_EFFECT_ON_OFF.getValue());
      packet.writeInt(playerID);
      packet.write(value);
      return packet.getPacket();
   }

   public static byte[] skillEffectLookLock(int playerID, int skillID, int value) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SKILL_EFFECT_LOOK_LOCK.getValue());
      packet.writeInt(playerID);
      packet.write(value);
      packet.writeInt(skillID);
      return packet.getPacket();
   }

   public static byte[] randomPortalCreated(RandomPortal portal) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RANDOM_PORTAL_CREATED.getValue());
      portal.encode(packet);
      return packet.getPacket();
   }

   public static byte[] randomPortalRemoved(RandomPortal portal) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.RANDOM_PORTAL_REMOVED.getValue());
      packet.write(0);
      packet.writeInt(portal.getObjectID());
      packet.writeInt(0);
      return packet.getPacket();
   }

   public static byte[] getMulungDojangRanking(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULUNG_DOJO_RANKING.getValue());
      packet.write(0);
      packet.writeInt(256);
      packet.writeInt(4);
      encodeMulungDojangMyRanking(packet, player, 0);
      encodeMulungDojangMyRanking(packet, player, 1);
      encodeMulungDojangMyRanking(packet, player, 2);
      encodeMulungDojangMyRanking(packet, player, 3);
      packet.writeInt(4);
      encodeMulungDojangRanking(packet, player, 0);
      encodeMulungDojangRanking(packet, player, 1);
      encodeMulungDojangRanking(packet, player, 2);
      encodeMulungDojangRanking(packet, player, 3);
      return packet.getPacket();
   }

   public static byte[] getMulungDojangRankingDisplayTest(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MULUNG_DOJO_RANKING.getValue());
      packet.write(0);
      packet.writeInt(256);
      packet.writeInt(4);
      encodeMulungDojangMyRanking(packet, player, 0);
      encodeMulungDojangMyRanking(packet, player, 1);
      encodeMulungDojangMyRanking(packet, player, 2);
      encodeMulungDojangMyRanking(packet, player, 3);
      packet.writeInt(4);
      encodeMulungDojangRankingDisplayTest(packet, player, 0);
      encodeMulungDojangRankingDisplayTest(packet, player, 1);
      encodeMulungDojangRankingDisplayTest(packet, player, 2);
      encodeMulungDojangRankingDisplayTest(packet, player, 3);
      return packet.getPacket();
   }

   public static void encodeMulungDojangMyRanking(PacketEncoder packet, MapleCharacter player, int type) {
      packet.write(type);
      if (type == 1) {
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(-1);
         packet.writeInt(-1);
         packet.writeInt(-1);
         packet.writeInt(-1);
         packet.writeInt(-1);
         packet.writeInt(-1);
      } else {
         if (type == 3) {
            type = player.getJob();
         }

         DojangMyRanking lastWeek = DojangRanking.getLastWeekMyRank(type, player.getName());
         DojangMyRanking thisWeek = DojangRanking.getThisWeekMyRank(type, player.getName());
         packet.writeInt(player.getJob());
         packet.writeInt(player.getLevel());
         thisWeek.encode(packet);
         lastWeek.encode(packet);
      }
   }

   public static void encodeMulungDojangRanking(PacketEncoder packet, MapleCharacter player, int type) {
      packet.write(type);
      if (type == 1) {
         packet.writeInt(0);
      } else {
         if (type == 3) {
            type = player.getJob();
         }

         List<DojangRankingEntry> list = DojangRanking.getThisWeekRank(type);
         packet.writeInt(list.size());

         for (DojangRankingEntry entry : list) {
            entry.encode(packet);
         }
      }
   }

   public static void encodeMulungDojangRankingDisplayTest(PacketEncoder packet, MapleCharacter player, int type) {
      packet.write(type);
      if (type == 1) {
         packet.writeInt(0);
      } else {
         if (type == 3) {
            type = player.getJob();
         }

         List<DojangRankingEntry> list = DojangRanking.getThisWeekRank(type);
         packet.writeInt(list.size());

         for (DojangRankingEntry entry : list) {
            entry.encodeTest(packet, player);
         }
      }
   }

   public static byte[] createCrystalGate(int playerID, List<CrystalGate> gates) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CRYSTAL_GATE.getValue());
      packet.writeInt(playerID);
      packet.writeInt(gates.size());

      for (CrystalGate gate : gates) {
         gate.encode(packet);
      }

      return packet.getPacket();
   }

   public static byte[] customChairResult(MapleCharacter player, boolean create, boolean update, boolean remove, CustomChair chair) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CUSTOM_CHAIR_RESULT.getValue());
      packet.writeInt(player.getId());
      packet.write(create);
      packet.write(!update);
      if (update) {
         packet.writeInt(chair.getObjectId());
         packet.write(!remove);
         if (!remove) {
            chair.encode(packet);
         }
      } else {
         packet.writeInt(player.getMap().getCustomChairsThreadsafe().size());

         for (MapleMapObject obj : player.getMap().getCustomChairsThreadsafe()) {
            CustomChair cc = (CustomChair)obj;
            packet.writeInt(cc.getObjectId());
            packet.write(create);
            if (create) {
               cc.encode(packet);
            }
         }
      }

      return packet.getPacket();
   }

   public static byte[] sitOnDummyChair(int targetID, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SIT_ON_DUMMY_CHAIR.getValue());
      packet.writeInt(targetID);
      packet.writeInt(type);
      return packet.getPacket();
   }

   public static byte[] userWaitQueueResponse(
      int playerID, WaitQueueType type, WaitQueueResult result, WaitQueueError error, int waitingType, int waitingQueueID, int fieldID
   ) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_WAIT_QUEUE_RESPONSE.getValue());
      packet.writeInt(playerID);
      packet.write(type.getType());
      packet.write(result.getType());
      packet.writeInt(waitingType);
      packet.writeInt(waitingQueueID);
      packet.writeInt(0);
      packet.writeInt(error.getType());
      packet.writeInt(0);
      packet.writeInt(fieldID);
      return packet.getPacket();
   }

   public static byte[] blackJack(int skillID, int skillLv, int posX, int posY) {
      return blackJack(skillID, skillLv, posX, posY, 0);
   }

   public static byte[] blackJack(int skillID, int skillLv, int posX, int posY, int attackCount) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.FORCE_ATTACK_USE.getValue());
      p.writeInt(skillID);
      p.writeInt(skillLv);
      p.writeInt(1);
      p.writeInt(posX);
      p.writeInt(posY);
      if (skillID == 400041080) {
         p.writeInt(attackCount);
      }

      return p.getPacket();
   }

   public static byte[] playMiniGameSound(String url) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.PLAY_MINIGAME_SOUND.getValue());
      p.writeMapleAsciiString(url);
      return p.getPacket();
   }

   public static byte[] onSetRideVehicleUser(MapleCharacter chr, byte[] packet) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.SET_RIDE_USER.getValue());
      p.writeInt(chr.getId());
      p.encodeBuffer(packet);
      return p.getPacket();
   }

   public static byte[] getIntensePowerCrystalInfo(Map<Integer, IntensePowerCrystalData> intensePowerCrystalData) {
      PacketEncoder p = new PacketEncoder();
      p.writeShort(SendPacketOpcode.INTENSE_POWER_CRYSTAL_INFO.getValue());
      p.write(true);
      p.writeInt(1);
      p.writeInt(0);
      p.writeInt(0);
      p.writeInt(2);
      p.writeInt(0);
      p.writeInt(0);
      p.writeInt(1);

      for (int i = 0; i < 1; i++) {
         p.writeLong(PacketHelper.getTime(-2L));
         p.writeLong(PacketHelper.getTime(-1L));
         p.writeInt(intensePowerCrystalData.size());
         intensePowerCrystalData.forEach((bossId, data) -> {
            p.writeInt(data.getNamingMonster());
            p.writeLong(data.getMeso());
            p.writeInt(data.getRealMonster());
         });
      }

      p.writeLong(PacketHelper.getTime(-2L));
      p.writeLong(PacketHelper.getTime(-1L));
      p.writeInt(intensePowerCrystalData.size());
      intensePowerCrystalData.forEach((bossId, data) -> {
         p.writeInt(data.getNamingMonster());
         p.writeLong(data.getMeso());
         p.writeInt(data.getRealMonster());
      });
      return p.getPacket();
   }

   public static byte[] zeroCombatRecovery(int level, int wp) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.ZERO_COMBAT_RECOVERY.getValue());
      o.write(level);
      o.writeInt(wp);
      return o.getPacket();
   }

   public static byte[] onFootholdOnOff(Map<Integer, Boolean> fhList) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.FOOTHOLD_ON_OFF.getValue());
      o.writeInt(fhList.size());

      for (Entry<Integer, Boolean> fhData : fhList.entrySet()) {
         o.writeInt(fhData.getKey());
         o.write(fhData.getValue());
      }

      return o.getPacket();
   }

   public static byte[] updatePortalScript(String name, boolean disable, int value) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.UPDATE_PORTAL_SCRIPT.getValue());
      o.writeInt(1);
      o.writeMapleAsciiString(name);
      o.writeInt(disable ? 1 : 0);
      o.writeInt(0);
      o.writeInt(0);
      o.writeInt(0);
      o.writeInt(value);
      return o.getPacket();
   }

   public static byte[] showAggressiveRank(List<String> characterList) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.AGGRESSIVE_RANK.getValue());
      o.writeInt(characterList.size());
      characterList.forEach(o::writeMapleAsciiString);
      return o.getPacket();
   }

   public static byte[] registerTeleportResult(int skillID) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.REGISTER_TELEPORT_RESULT.getValue());
      o.writeInt(skillID);
      return o.getPacket();
   }

   public static byte[] setMemoryChoice(int skillID) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.MEMORY_CHOICE.getValue());
      o.writeInt(skillID);
      return o.getPacket();
   }

   public static byte[] showCubeLevelupLimit() {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.SEND_CUBE_LEVELUP_LIMIT.getValue());
      GradeRandomOption[] opts = new GradeRandomOption[]{
         GradeRandomOption.Red, GradeRandomOption.Black, GradeRandomOption.Additional, GradeRandomOption.AmazingAdditional
      };

      for (GradeRandomOption opt : opts) {
         for (int i = 1; i <= 3; i++) {
            o.writeInt(GameConstants.getCubeLevelUpCount(opt, i));
         }
      }

      return o.getPacket();
   }

   public static byte[] showOriginSkillPartyEffect(int playerId, int jobId, String plyaerName) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.SHOW_PARTY_ORIGIN_SKILL_EFFECT.getValue());
      o.writeInt(playerId);
      o.writeInt(jobId);
      o.write(true);
      o.writeMapleAsciiString(plyaerName);
      return o.getPacket();
   }

   public static byte[] showMonsterDebuffMark(int skillId, List<Integer> mobList, int duration) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.SET_ADVENTURER_MARK.getValue());
      o.writeInt(skillId);
      o.write(1);
      o.writeInt(mobList.size());

      for (int objId : mobList) {
         o.writeInt(objId);
         o.writeInt(1);
         o.writeInt(0);
         o.writeInt(duration);
         o.writeInt(0);
      }

      return o.getPacket();
   }

   public static byte[] attackMonsterDebuffMark(int skillID, int linkSkillID, int objId, int delay) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ATTACK_ADVENTURER_MARK.getValue());
      packet.writeInt(skillID);
      packet.writeInt(linkSkillID);
      packet.writeInt(0);
      packet.writeInt(1);
      packet.writeInt(objId);
      packet.writeInt(delay);
      return packet.getPacket();
   }

   public static byte[] showMonsterStackedDebuffMark(int skillId, int attackSkillId, List<Pair<Integer, Integer>> mobList, int duration, int maxStack) {
      return showMonsterStackedDebuffMark(skillId, attackSkillId, 0, mobList, duration, maxStack);
   }

   public static byte[] showMonsterStackedDebuffMark(
      int skillId, int attackSkillId, int attackSkillId2, List<Pair<Integer, Integer>> mobList, int duration, int maxStack
   ) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.SET_MONSTER_DEBUFF_MARK.getValue());
      o.write(1);
      o.writeInt(mobList.size());

      for (Pair<Integer, Integer> pair : mobList) {
         o.writeInt(pair.left);
      }

      o.writeInt(maxStack);
      o.writeInt(duration);
      o.writeInt(skillId);
      o.writeInt(attackSkillId);
      o.writeInt(attackSkillId2);
      o.writeInt(0);
      o.writeInt(mobList.size());

      for (Pair<Integer, Integer> pair : mobList) {
         o.writeInt(pair.left);
         o.writeInt(pair.right);
         o.writeInt(0);
         o.writeInt(duration);
      }

      return o.getPacket();
   }

   public static byte[] attackMonsterStackedDebuffMark(int skillID, int stack, int objId, int unk) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ATTACK_MONSTER_DEBUFF_MARK.getValue());
      packet.writeInt(skillID);
      packet.writeInt(stack);
      packet.writeInt(0);
      packet.writeInt(1);
      packet.writeInt(objId);
      packet.writeInt(unk);
      return packet.getPacket();
   }

   public static byte[] cancelAncientAstra(int skillID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CANCEL_ANCIENT_ASTRA.getValue());
      packet.writeInt(skillID);
      return packet.getPacket();
   }

   public static byte[] zodiacBurstEnable(int count) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ZODIAC_BURST_METEO.getValue());
      packet.writeInt(count);
      return packet.getPacket();
   }

   public static class AuctionPacket {
      public static byte[] stageSetAuction(MapleCharacter chr) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.STAGE_SET_AUCTION.getValue());
         PacketHelper.addCharacterInfo(mplew, chr, -1L);
         mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
         return mplew.getPacket();
      }

      public static byte[] addAuctionItem(MapleCharacter chr, AuctionItemPackage aitem) {
         PacketEncoder mplew = new PacketEncoder();
         Item item = aitem.getItem();
         mplew.writeShort(1516);
         mplew.writeInt(70);
         mplew.writeInt(aitem.getItem().getInventoryId());
         mplew.write(1);
         mplew.writeLong(aitem.getItem().getInventoryId());
         mplew.writeInt(aitem.getOwnerId());
         mplew.writeInt(aitem.getOwnerId());
         mplew.writeInt(aitem.getOwnerId() == chr.getId() ? 1 : 0);
         mplew.writeInt(5);
         mplew.writeMapleAsciiString_(aitem.getOwnerName(), 13);
         mplew.writeLong(0L);
         mplew.writeLong(-1L);
         mplew.writeLong(aitem.getMesos());
         mplew.writeLong((int)(aitem.getMesos() / item.getQuantity()));
         long duration = aitem.getExpiredTime();
         mplew.writeLong(PacketHelper.getKoreanTimestamp(duration));
         mplew.writeMapleAsciiString_("Extreme@@", 17);
         mplew.writeInt(-1);
         mplew.writeLong(0L);
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeLong(2000L);
         mplew.writeInt(item.getItemId() / 1000000);
         mplew.writeInt(item.getItemId() / 1000000 - 1);
         mplew.writeLong(PacketHelper.getTime(-2L));
         PacketHelper.addItemInfo(mplew, item);
         return mplew.getPacket();
      }

      public static byte[] auctionResult(MapleCharacter chr, int type, List<AuctionItemPackage> items) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(1516);
         mplew.writeInt(type);
         mplew.writeInt(type != 40 && type != 41 && type != 46 && type != 50 ? 0 : 1000);
         mplew.writeInt(0);
         switch (type) {
            case 40:
               mplew.writeShort(1);
               mplew.writeInt(items.size());

               for (AuctionItemPackage aitem : items) {
                  Item item = aitem.getItem();
                  mplew.writeLong(aitem.getItem().getInventoryId());
                  mplew.writeInt(aitem.getOwnerId());
                  mplew.writeInt(aitem.getOwnerId());
                  mplew.writeInt(aitem.getOwnerId() == chr.getId() ? 1 : 0);
                  mplew.writeInt(5);
                  mplew.writeMapleAsciiString_(aitem.getOwnerName(), 13);
                  mplew.writeLong(0L);
                  mplew.writeLong(-1L);
                  mplew.writeLong(aitem.getMesos());
                  mplew.writeLong((int)(aitem.getMesos() / item.getQuantity()));
                  long duration = aitem.getExpiredTime();
                  mplew.writeLong(PacketHelper.getKoreanTimestamp(duration));
                  mplew.writeMapleAsciiString_("Extreme@@", 17);
                  mplew.writeInt(-1);
                  mplew.writeLong(0L);
                  mplew.writeLong(PacketHelper.getTime(-2L));
                  mplew.writeLong(2000L);
                  mplew.writeInt(item.getItemId() / 1000000);
                  mplew.writeInt(item.getItemId() / 1000000 - 1);
                  mplew.writeLong(PacketHelper.getTime(-2L));
                  PacketHelper.addItemInfo(mplew, item);
               }
               break;
            case 46:
            case 50:
            case 51:
               mplew.writeInt(0);
            case 70:
         }

         return mplew.getPacket();
      }

      public static byte[] AuctionMessage(byte message, byte sub) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION.getValue());
         mplew.write(message);
         mplew.write(sub);
         return mplew.getPacket();
      }

      public static byte[] AuctionOn() {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION.getValue());
         mplew.writeInt(0);
         System.out.print("옥션온" + mplew);
         return mplew.getPacket();
      }

      public static byte[] showItemList(List<AuctionItemPackage> items, boolean isSearch) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION.getValue());
         mplew.write(7);
         mplew.write(isSearch ? 0 : 1);
         mplew.writeInt(items.size());

         for (AuctionItemPackage aitem : items) {
            Item item = aitem.getItem();
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getQuantity());
            addAuctionItemInfo(mplew, aitem);
         }

         if (isSearch) {
            for (AuctionItemPackage aitem : items) {
               mplew.writeLong(aitem.getItem().getItemId());
            }
         }

         return mplew.getPacket();
      }

      public static byte[] showCompleteItemList(List<AuctionItemPackage> items, String buyername, int ownerId) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION.getValue());
         mplew.write(9);
         mplew.writeInt(items.size());

         for (AuctionItemPackage aitem : items) {
            boolean Refund = false;
            Item item = aitem.getItem();
            int status = aitem.getType(ownerId == aitem.getOwnerId(), false);
            mplew.writeInt((int)item.getInventoryId());
            mplew.encodeBuffer(HexTool.getByteArrayFromHexString("05 0A 2C 05"));
            mplew.writeInt(aitem.getBuyer());
            mplew.writeInt(item.getItemId());
            mplew.writeInt(status);
            mplew.writeLong(status == 0 ? Center.Auction.getBidById(ownerId, (int)item.getInventoryId()) : aitem.getBid());
            mplew.writeLong(PacketHelper.getTime(aitem.getBuyTime() + 43200000L));
            mplew.writeLong(aitem.getMesos());
            mplew.writeInt(Refund ? 0 : (aitem.getItem().getItemId() / 1000000 == 1 ? 1 : 2));
            mplew.writeInt(7);
            mplew.write(Refund ? 0 : 1);
            if (!Refund) {
               addCompleteAuctionItemInfo(mplew, aitem, ownerId, buyername);
            }
         }

         return mplew.getPacket();
      }

      public static byte[] AuctionSell(AuctionItemPackage aitem) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION.getValue());
         mplew.write(8);
         mplew.writeInt(1);
         addAuctionItemInfo(mplew, aitem);
         return mplew.getPacket();
      }

      public static byte[] AuctionBuy(AuctionItemPackage aitem, long price, int status) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION_BUY.getValue());
         mplew.write(1);
         mplew.writeInt((int)aitem.getItem().getInventoryId());
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("05 0A 2C 05"));
         mplew.writeInt(aitem.getBuyer());
         mplew.writeInt(aitem.getItem().getItemId());
         mplew.writeInt(status);
         mplew.writeLong(price);
         mplew.writeLong(PacketHelper.getTime(aitem.getBuyTime() + 43200000L));
         mplew.writeLong(0L);
         mplew.writeInt(status == 2 ? 1 : (status == 8 ? 11 : (status == 7 ? 2 : 0)));
         mplew.writeInt(7);
         return mplew.getPacket();
      }

      public static byte[] AuctionBargaining(AuctionItemPackage aitem, long bargainmeso, String bargainstring) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.AUCTION_BUY.getValue());
         mplew.write(0);
         mplew.writeInt(aitem.isBargain() ? 1 : 0);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("76 20 C1 00"));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("9E 95 7B 05"));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("11 88 6D 01"));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("FC 11 06 7D"));
         mplew.writeInt(0);
         mplew.writeMapleAsciiString(aitem.getOwnerName());
         mplew.writeLong(bargainmeso);
         mplew.writeMapleAsciiString(bargainstring);
         mplew.writeLong(0L);
         mplew.writeInt(-1);
         mplew.writeZeroBytes(5);
         return mplew.getPacket();
      }

      public static byte[] addAuctionItemInfo(PacketEncoder mplew, AuctionItemPackage aitem) {
         Item item = aitem.getItem();
         mplew.writeInt((int)item.getInventoryId());
         mplew.writeInt((int)item.getInventoryId());
         mplew.writeInt(aitem.isBargain() ? 1 : 0);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("05 95 B5 00"));
         mplew.writeInt(aitem.getOwnerId());
         mplew.writeInt(0);
         mplew.writeInt(aitem.getItem().getItemId() / 1000000 == 1 ? 1 : 2);
         mplew.writeInt(7);
         mplew.writeMapleAsciiString_(aitem.getOwnerName(), 13);
         mplew.writeLong(aitem.getBid());
         mplew.writeLong(-1L);
         mplew.writeLong(aitem.getMesos());
         mplew.writeLong(PacketHelper.getTime(aitem.getExpiredTime()));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 00 00 00 FF FF"));
         mplew.writeLong(item.getInventoryId());
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("60 D5 FF FF FF FF"));
         mplew.writeLong(item.getInventoryId());
         mplew.writeLong(PacketHelper.getTime(aitem.getStartTime()));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("D0 07 00 00 00 00 00 00 00 00 00 00"));
         PacketHelper.addItemInfo(mplew, item);
         return mplew.getPacket();
      }

      public static byte[] addCompleteAuctionItemInfo(PacketEncoder mplew, AuctionItemPackage aitem, int ownerId, String buyername) {
         Item item = aitem.getItem();
         int status = aitem.getType(ownerId == aitem.getOwnerId(), false);
         mplew.writeInt((int)item.getInventoryId());
         mplew.writeInt((int)item.getInventoryId());
         mplew.writeInt(aitem.isBargain() ? 1 : 0);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("05 95 B5 00"));
         mplew.writeInt(aitem.getOwnerId());
         mplew.writeInt(status == 0 ? 1 : 3);
         mplew.writeInt(1);
         mplew.writeInt(7);
         mplew.writeMapleAsciiString_(aitem.getOwnerName(), 13);
         mplew.writeLong(aitem.getBid());
         mplew.writeLong(aitem.getBid());
         mplew.writeLong(aitem.getMesos());
         mplew.writeLong(PacketHelper.getTime(aitem.getExpiredTime()));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("05 0A 2C 05"));
         mplew.writeMapleAsciiString_(buyername, 13);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("FF FF FF FF"));
         mplew.writeLong(item.getInventoryId());
         mplew.writeLong(PacketHelper.getTime(aitem.getStartTime()));
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString("D0 07 00 00 00 00 00 00 00 00 00 00"));
         PacketHelper.addItemInfo(mplew, item);
         return mplew.getPacket();
      }
   }

   public static class DirectionPacket {
      public static byte[] IntroEnableUI(int a) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
         mplew.write(0);
         mplew.write(a);
         return mplew.getPacket();
      }

      public static byte[] IntroDisableUI(boolean enable) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SET_STAND_ALONE_MODE.getValue());
         mplew.write(enable ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] getDirectionInfo(int type, int value, int value2) {
         PacketEncoder mplew = new PacketEncoder();
         if (type > 0 && type < 5) {
            type++;
         } else if (type >= 5 && type < 10) {
            type += 2;
         } else if (type >= 10) {
            type += 3;
         }

         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(type);
         mplew.writeInt(value);
         mplew.writeInt(value2);
         return mplew.getPacket();
      }

      public static byte[] getDirectionInfo(int type, int value) {
         PacketEncoder mplew = new PacketEncoder();
         if (type > 0 && type < 5) {
            type++;
         } else if (type >= 5 && type < 10) {
            type += 2;
         } else if (type >= 10) {
            type += 3;
         }

         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(type);
         mplew.writeLong(value);
         return mplew.getPacket();
      }

      public static byte[] getDirectionInfo(CField.DirectionPacket.DirectionInfoData d) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(CField.ingameDirectionType.effectPlay.getType());
         mplew.writeMapleAsciiString(d.data);
         mplew.writeInt(d.dur);
         mplew.writeInt(d.x);
         mplew.writeInt(d.y);
         mplew.write(d.hasz);
         if (d.hasz) {
            mplew.writeInt(d.z);
         }

         mplew.write(d.hasbase);
         if (d.hasbase) {
            mplew.writeInt(d.base);
            mplew.write(d.noto);
            mplew.write(d.unk);
         }

         return mplew.getPacket();
      }

      public static final byte[] playMovieWeb(String data, boolean show) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAY_MOVIE.getValue());
         mplew.writeMapleAsciiString(data);
         mplew.write(show ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] getCurNodeEventEnd(int bv) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.CUR_NODE_EVENT_END.getValue());
         mplew.write(bv);
         return mplew.getPacket();
      }

      public static byte[] getForcedMove(int direction, int fixel) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(CField.ingameDirectionType.forceMove.getType());
         mplew.writeInt(direction);
         mplew.writeInt(fixel);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] getCameraMove(int pixelPerSec, int destX, int destY) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(CField.ingameDirectionType.cameraMove.getType());
         mplew.write(0);
         mplew.writeInt(pixelPerSec);
         mplew.writeInt(destX);
         mplew.writeInt(destY);
         return mplew.getPacket();
      }

      public static byte[] getCameraMoveBack(int pixelPerSec, int unk) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(CField.ingameDirectionType.cameraMove.getType());
         mplew.write(1);
         mplew.writeInt(pixelPerSec);
         mplew.write(unk);
         return mplew.getPacket();
      }

      public static byte[] getEffectPlay(String string, int duration, int rx, int ry, int baseNPC, int notOrigin, boolean unk) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(CField.ingameDirectionType.effectPlay.getType());
         mplew.writeMapleAsciiString(string);
         mplew.writeInt(duration);
         mplew.writeInt(rx);
         mplew.writeInt(ry);
         mplew.write(baseNPC != -1);
         if (baseNPC != -1) {
            mplew.writeInt(baseNPC);
         }

         mplew.write(notOrigin > 0);
         if (notOrigin > 0) {
            mplew.writeInt(notOrigin);
            mplew.write(0);
            mplew.write(0);
         }

         mplew.write(unk);
         return mplew.getPacket();
      }

      public static class DirectionInfoData {
         private String data;
         private int dur;
         private int x;
         private int y;
         private boolean hasz;
         private int z;
         private boolean hasbase;
         private int base;
         private boolean noto;
         private boolean unk;

         public DirectionInfoData(String data, int dur, int x, int y) {
            this.data = data;
            this.dur = dur;
            this.x = x;
            this.y = y;
         }

         public DirectionInfoData(String data, int dur, int x, int y, int z) {
            this.data = data;
            this.dur = dur;
            this.x = x;
            this.y = y;
            this.hasz = true;
            this.z = z;
         }

         public DirectionInfoData(String data, int dur, int x, int y, int base, boolean noto, boolean unk) {
            this.data = data;
            this.dur = dur;
            this.x = x;
            this.y = y;
            this.hasbase = true;
            this.base = base;
            this.noto = noto;
            this.unk = unk;
         }

         public DirectionInfoData(String data, int dur, int x, int y, int z, int base, boolean noto, boolean unk) {
            this.data = data;
            this.dur = dur;
            this.x = x;
            this.y = y;
            this.hasz = true;
            this.z = z;
            this.hasbase = true;
            this.base = base;
            this.noto = noto;
            this.unk = unk;
         }
      }
   }

   public static class EffectPacket {
      public static byte[] spineEffect(String path, String animation, int onoff, int loop, int postRender, int endDelay, String key) {
         return spineEffect(path, animation, onoff, loop, postRender, endDelay, key, "", 0, 0, 0);
      }

      public static byte[] spineEffect(
         String path, String animation, int onoff, int loop, int postRender, int endDelay, String key, String unk1, int unk2, int unk3, int unk4
      ) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
         packet.write(32);
         packet.write(onoff);
         packet.write(loop);
         packet.write(postRender);
         packet.writeInt(endDelay);
         packet.writeMapleAsciiString(path);
         packet.writeMapleAsciiString(animation);
         packet.writeMapleAsciiString(unk1);
         packet.write(unk2);
         packet.writeInt(unk3);
         packet.writeInt(unk4);
         packet.writeInt(0);
         packet.writeInt(0);
         if (key.isEmpty()) {
            packet.write(0);
         } else {
            packet.write(1);
            packet.writeMapleAsciiString(key);
         }

         return packet.getPacket();
      }

      public static byte[] showMonsterCollectionMessage(int mobTemplateID) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.MONSTER_COLLECTION_MESSAGE.getValue());
         mplew.writeInt(13);
         mplew.writeInt(0);
         mplew.writeInt(mobTemplateID);
         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }

   public static class InteractionPacket {
      public static byte[] getTradeInvite(MapleCharacter c, boolean isTrade) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
         mplew.write(isTrade ? 4 : 3);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString(c.getName());
         mplew.writeInt(c.getJob());
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getCashTradeInvite(MapleCharacter c) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
         mplew.write(7);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString(c.getName());
         mplew.writeInt(c.getJob());
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getTradeMesoSet(byte number, long meso) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.SET_MESO1.action);
         mplew.write(number);
         mplew.writeLong(meso);
         return mplew.getPacket();
      }

      public static byte[] getTradeItemAdd(byte number, Item item) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.SET_ITEMS1.action);
         mplew.write(number);
         mplew.write(item.getPosition());
         PacketHelper.addItemInfo(mplew, item);
         return mplew.getPacket();
      }

      public static byte[] getTradeStart(MapleClient c, MapleTrade trade, byte number, boolean isTrade) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(20);
         mplew.write(isTrade ? 4 : 3);
         mplew.write(2);
         mplew.write(number);
         if (number == 1) {
            mplew.write(0);
            PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false, false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
            mplew.writeShort(trade.getPartner().getChr().getJob());
            mplew.writeInt(0);
         }

         mplew.write(number);
         PacketHelper.addCharLook(mplew, c.getPlayer(), false, false);
         mplew.writeMapleAsciiString(c.getPlayer().getName());
         mplew.writeShort(c.getPlayer().getJob());
         mplew.writeInt(0);
         mplew.write(255);
         return mplew.getPacket();
      }

      public static byte[] getCashTradeStart(MapleClient c, MapleTrade trade, byte number) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(20);
         mplew.write(7);
         mplew.write(2);
         mplew.write(number);
         if (number == 1) {
            mplew.write(0);
            PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false, false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
            mplew.writeShort(trade.getPartner().getChr().getJob());
            mplew.writeInt(0);
         }

         mplew.write(number);
         PacketHelper.addCharLook(mplew, c.getPlayer(), false, false);
         mplew.writeMapleAsciiString(c.getPlayer().getName());
         mplew.writeShort(c.getPlayer().getJob());
         mplew.writeInt(0);
         mplew.write(255);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] getTradeConfirmation() {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.CONFIRM_TRADE1.action);
         return mplew.getPacket();
      }

      public static byte[] TradeMessage(byte UserSlot, byte message) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.EXIT.action);
         mplew.write(UserSlot);
         mplew.write(message);
         return mplew.getPacket();
      }

      public static byte[] getTradeCancel(byte UserSlot) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
         mplew.writeInt(PlayerInteractionHandler.Interaction.EXIT.action);
         mplew.write(UserSlot);
         mplew.write(2);
         return mplew.getPacket();
      }
   }

   public static class NPCPacket {
      public static byte[] sendCodyColorChange(int flag, int coupon, int[] colors, long android) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.COUPON_HAIRFACE_CHANGE_REQUEST.getValue());
         mplew.write(flag);
         mplew.writeInt(coupon);
         mplew.writeInt(1);
         mplew.writeInt(3);
         if (flag == 100) {
            mplew.writeLong(android);
         }

         mplew.write(colors.length);

         for (int color : colors) {
            mplew.writeInt(color);
         }

         return mplew.getPacket();
      }

      public static byte[] spawnNPC(MapleNPC life, boolean show) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SPAWN_NPC.getValue());
         mplew.writeInt(life.getObjectId());
         mplew.writeInt(life.getId());
         mplew.writeShort(life.getPosition().x);
         mplew.writeShort(life.getCy());
         mplew.writeInt(-1);
         mplew.writeInt(-1);
         mplew.write(life.isCanMove() ? 1 : 0);
         mplew.write(life.getF() == 1 ? 0 : 1);
         mplew.writeShort(life.getFh());
         mplew.writeShort(life.getRx0());
         mplew.writeShort(life.getRx1());
         mplew.write(show ? 1 : 0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.writeInt(-1);
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString("");
         boolean useScreenInfo = life.getId() == 1540000 && ServerConstants.top1Ranker != null;
         mplew.write(useScreenInfo ? 1 : 0);
         if (useScreenInfo && life.getId() == 1540000) {
            mplew.write(3);
            mplew.writeInt(7);
            mplew.writeInt(1);
            mplew.writeInt(0);
            MapleCharacter c = ServerConstants.top1Ranker;
            mplew.writeMapleAsciiString(c.getName());
            PacketHelper.encodePackedCharacterLook(mplew, c);
         }

         return mplew.getPacket();
      }

      public static byte[] removeNPC(int objectid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.REMOVE_NPC.getValue());
         mplew.writeInt(objectid);
         return mplew.getPacket();
      }

      public static byte[] removeNPCController(int objectid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
         mplew.write(0);
         mplew.writeInt(objectid);
         return mplew.getPacket();
      }

      public static byte[] spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(DBConfig.isGanglim ? -1 : SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
         mplew.write(1);
         mplew.writeInt(life.getObjectId());
         mplew.writeInt(life.getId());
         mplew.writeShort(life.getPosition().x);
         mplew.writeShort(life.getCy());
         mplew.writeInt(-1);
         mplew.writeInt(-1);
         mplew.write(life.isCanMove() ? 1 : 0);
         mplew.write(life.getF() == 1 ? 0 : 1);
         mplew.writeShort(life.getFh());
         mplew.writeShort(life.getRx0());
         mplew.writeShort(life.getRx1());
         mplew.write(MiniMap ? 1 : 0);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.writeInt(-1);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString("");
         boolean useScreenInfo = life.getId() == 1540000 && ServerConstants.top1Ranker != null;
         mplew.write(useScreenInfo ? 1 : 0);
         if (useScreenInfo && life.getId() == 1540000) {
            mplew.write(3);
            mplew.writeInt(7);
            mplew.writeInt(1);
            mplew.writeInt(0);
            MapleCharacter c = ServerConstants.top1Ranker;
            mplew.writeMapleAsciiString(c.getName());
            PacketHelper.encodePackedCharacterLook(mplew, c);
         }

         return mplew.getPacket();
      }

      public static byte[] setNPCScriptable(List<Pair<Integer, String>> npcs) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_SCRIPTABLE.getValue());
         mplew.write(npcs.size());

         for (Pair<Integer, String> s : npcs) {
            mplew.writeInt(s.left);
            mplew.writeMapleAsciiString(s.right);
            mplew.writeInt(0);
            mplew.writeInt(Integer.MAX_VALUE);
         }

         return mplew.getPacket();
      }

      public static byte[] getYesNoScenario(int npcID, int dlgColorType, int type, String talk, String endBytes) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(3);
         mplew.writeInt(npcID);
         mplew.write(0);
         mplew.write(type);
         mplew.writeShort(ScriptMessageFlag.NpcReplacedByNpc.getFlag() | ScriptMessageFlag.Scenario.getFlag());
         mplew.write(dlgColorType);
         if (type == 0) {
            mplew.writeInt(0);
         }

         if ((type & 4) != 0) {
            mplew.writeInt(npcID);
         }

         mplew.writeMapleAsciiString(talk);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString(endBytes));
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getScriptMessage(
         int speakerTemplateID,
         int replacedNpcTemplateID,
         byte speakerType,
         int flag,
         ScriptMessageType type,
         int DLGColorType,
         String text,
         PacketEncoder addPacket
      ) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         packet.writeInt(0);
         packet.write(speakerType);
         packet.writeInt(speakerTemplateID);
         if (replacedNpcTemplateID != 0 && (flag & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) == 0) {
            packet.write(1);
            packet.writeInt(replacedNpcTemplateID);
         } else {
            packet.write(0);
         }

         packet.write(type.getType());
         packet.writeShort(flag);
         packet.write(DLGColorType);
         if (type.getType() == 0) {
            packet.writeInt(0);
         }

         if ((flag & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            packet.writeInt(replacedNpcTemplateID);
         }

         packet.writeMapleAsciiString(text);
         if (addPacket != null) {
            packet.encodeBuffer(addPacket.getPacket());
         }

         return packet.getPacket();
      }

      public static byte[] getScriptMessage(
         int speakerTemplateID,
         int replacedNpcTemplateID,
         GameObjectType speakerType,
         int flag,
         ScriptMessageType type,
         int DLGColorType,
         String text,
         PacketEncoder addPacket
      ) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         packet.writeInt(0);
         packet.write(speakerType.getType());
         packet.writeInt(speakerTemplateID);
         if (replacedNpcTemplateID != 0 && (flag & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) == 0) {
            packet.write(1);
            packet.writeInt(replacedNpcTemplateID);
         } else {
            packet.write(0);
         }

         packet.write(type.getType());
         packet.writeShort(flag);
         packet.write(DLGColorType);
         if (type.getType() == 0) {
            packet.writeInt(0);
         }

         if ((flag & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
            packet.writeInt(replacedNpcTemplateID);
         }

         if (type != ScriptMessageType.AskAvatar && type != ScriptMessageType.AskSelectMenu) {
            packet.writeMapleAsciiString(text);
         }

         if (addPacket != null) {
            packet.encodeBuffer(addPacket.getPacket());
         }

         return packet.getPacket();
      }

      public static byte[] getScenarioNpcNoESC(int replacedNpcTemplateID, String talk, int dlgColorType, int time) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(3);
         mplew.writeInt(0);
         if (replacedNpcTemplateID == 0) {
            mplew.write(0);
         } else {
            mplew.write(1);
            mplew.writeInt(replacedNpcTemplateID);
         }

         mplew.write(0);
         mplew.writeShort(ScriptMessageFlag.NoEsc.getFlag() | ScriptMessageFlag.NpcReplacedByNpc.getFlag() | ScriptMessageFlag.Scenario.getFlag());
         mplew.write(dlgColorType);
         mplew.writeInt(0);
         mplew.writeInt(replacedNpcTemplateID);
         mplew.writeMapleAsciiString(talk);
         mplew.write(0);
         mplew.write(1);
         mplew.writeInt(time);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type) {
         return getNPCTalk(npc, msgType, talk, endBytes, type, npc);
      }

      public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, int diffNPC) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         if (npc == 0) {
            npc = 1021;
         }

         if ((type & 4) != 0) {
            diffNPC = npc;
            npc = 0;
         }

         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npc);
         mplew.write(diffNPC != 0);
         if (diffNPC != 0) {
            mplew.writeInt(diffNPC);
         }

         mplew.write(msgType);
         mplew.writeShort(type);
         mplew.write(0);
         if ((type & 4) != 0) {
            mplew.writeInt(diffNPC);
         }

         if (msgType == 19) {
            mplew.writeLong(5L);
         }

         if (msgType == 0) {
            mplew.writeInt(0);
         }

         mplew.writeMapleAsciiString(talk);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString(endBytes));
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getMapSelection(int npcid, String sel) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npcid);
         mplew.write(0);
         mplew.write(17);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(npcid == 2083006 ? 1 : 0);
         mplew.write(0);
         mplew.writeInt(npcid == 9010022 ? 1 : 0);
         mplew.writeMapleAsciiString(sel);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalkMixStyle(int npcId, String talk, boolean isAngelicBuster, boolean isZeroBeta) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         packet.writeInt(0);
         packet.write(4);
         packet.writeInt(npcId);
         packet.write(0);
         packet.write(ScriptMessageType.AskCustomMixHairAndProb.getType());
         packet.writeShort(0);
         packet.write(0);
         packet.writeInt(0);
         packet.write(isAngelicBuster ? 1 : 0);
         packet.writeInt(isZeroBeta ? 1 : 0);
         packet.writeInt(50);
         packet.writeMapleAsciiString(talk);
         return packet.getPacket();
      }

      public static byte[] getNPCTalkStyle(int npc, String talk, boolean bAngelicBuster, List<Integer> args) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npc);
         mplew.write(0);
         mplew.write(10);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString(talk + "히히발싸");
         byte flag = 0;
         if (bAngelicBuster) {
            flag = 1;
         }

         mplew.write(flag);
         mplew.write(args.size());

         for (int i = 0; i < args.size(); i++) {
            mplew.writeInt(args.get(i));
         }

         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalkStyle(int npc, String talk, boolean bAngelicBuster, boolean bZeroBeta, int... args) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(9062676);
         mplew.write(0);
         mplew.write(10);
         mplew.writeShort(2);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString(talk);
         byte flag = 0;
         if (bAngelicBuster) {
            flag = 1;
         }

         if (bZeroBeta) {
            flag = 2;
         }

         mplew.write(flag);
         mplew.write(args.length);

         for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
         }

         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalkStyleAndroid(int npcId, String talk, int... args) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npcId);
         mplew.write(0);
         mplew.write(11);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeMapleAsciiString("");
         mplew.writeMapleAsciiString(talk);
         mplew.write(args.length);

         for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
         }

         return mplew.getPacket();
      }

      public static byte[] getNPCTalkStyleZero(int npcId, String talk, int[] args1, int[] args2) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npcId);
         mplew.write(0);
         mplew.write(ScriptMessageType.AskAvatarZero.getType());
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeMapleAsciiString(talk);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.write(args1.length);

         for (int i = 0; i < args1.length; i++) {
            mplew.writeInt(args1[i]);
         }

         mplew.writeInt(1);
         mplew.write(args2.length);

         for (int i = 0; i < args2.length; i++) {
            mplew.writeInt(args2[i]);
         }

         return mplew.getPacket();
      }

      public static byte[] getNPCTalkNum(int npc, String talk, int def, int min, int max) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npc);
         mplew.write(0);
         mplew.write(5);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeMapleAsciiString(talk);
         mplew.writeLong(def);
         mplew.writeLong(min);
         mplew.writeLong(max);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalkText(int npc, String talk) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npc);
         mplew.write(0);
         mplew.write(4);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeMapleAsciiString(talk);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalkTextFlag(int temp, int npc, String talk, int flag) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(4);
         mplew.writeInt(npc);
         if (temp == 0) {
            mplew.write(0);
         } else {
            mplew.write(1);
            mplew.writeInt(temp);
         }

         mplew.write(4);
         mplew.writeShort(flag);
         mplew.write(0);
         mplew.writeMapleAsciiString(talk);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getEvanTutorial(String data) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         mplew.writeInt(0);
         mplew.write(8);
         mplew.writeInt(0);
         mplew.write(1);
         mplew.write(1);
         mplew.writeShort(0);
         mplew.write(1);
         mplew.writeMapleAsciiString(data);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getNPCTalk(NpcTalk msg) {
         PacketEncoder o = new PacketEncoder();
         o.writeShort(SendPacketOpcode.NPC_TALK.getValue());
         o.writeInt(0);
         o.write(msg.speakerTypeID);
         o.writeInt(msg.speakerTemplateID);
         o.write(msg.bUseOtherTemplate);
         if (msg.bUseOtherTemplate) {
            o.writeInt(msg.otherSpeakerTemplateID);
         }

         o.write(msg.msgType);
         o.writeShort(msg.param);
         o.write(msg.color);
         switch (ScriptFlag.getByFlag(msg.msgType)) {
            case Say:
               msg.onSay(o);
               break;
            case SayImage:
               msg.onSayImage(o);
               break;
            case AskYesNo:
            case AskAccept:
               msg.onAskYesNo(o);
               break;
            case AskText:
               msg.onAskText(o);
               break;
            case AskNumber:
               msg.onAskNumber(o);
               break;
            case AskMenu:
               msg.onAskMenu(o);
               break;
            case AskAvatar:
               msg.onAskAvatar(o);
               break;
            case AskCustomMixHairAndProb:
               msg.onAskCustomMixHairAndProb(o);
         }

         return o.getPacket();
      }

      public static byte[] getNPCShop(int sid, MapleShop shop, MapleClient c) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
         mplew.writeInt(sid);
         mplew.write(false);
         PacketHelper.addShopInfo(mplew, shop, c);
         return mplew.getPacket();
      }

      public static byte[] confirmShopTransaction(byte code, MapleShop shop, MapleClient c, int indexBought) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
         packet.write(code);
         if (code == 11) {
            PacketHelper.addShopInfo(packet, shop, c);
         } else {
            packet.write(indexBought >= 0);
            if (indexBought >= 0) {
               packet.writeInt(indexBought);
            } else {
               packet.writeInt(0);
               packet.writeInt(1000);
               packet.writeInt(0);
            }
         }

         return packet.getPacket();
      }

      public static byte[] npcSpecialAction(int oid, String effect, int dur, int localact) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_SPAWN_EFFECT.getValue());
         mplew.writeInt(oid);
         mplew.writeMapleAsciiString(effect);
         mplew.writeInt(dur);
         mplew.write(localact);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] npcMoveForcely(int oid, int forceX, int moveX, int speedRate) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_MOVE_FORCELY.getValue());
         mplew.writeInt(oid);
         mplew.writeInt(forceX);
         mplew.writeInt(moveX);
         mplew.writeInt(speedRate);
         return mplew.getPacket();
      }

      public static byte[] npcFlipForcely(int oid, int a) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.NPC_FLIP_FORCELY.getValue());
         mplew.writeInt(oid);
         mplew.writeInt(a);
         return mplew.getPacket();
      }
   }

   public static class SummonPacket {
      public static byte[] spawnSummon(Summoned summon, boolean animated) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SPAWN_SUMMON.getValue());
         mplew.writeInt(summon.getOwnerId());
         mplew.writeInt(summon.getObjectId());
         mplew.writeInt(summon.getSkill());
         mplew.writeInt(summon.getOwnerLevel());
         mplew.writeInt(summon.getSkillLevel());
         mplew.encodePos(summon.getPosition());
         mplew.write(
            summon.getSkill() != 14110033
                  && summon.getSkill() != 32111006
                  && summon.getSkill() != 33101005
                  && summon.getSkill() != 400031051
                  && summon.getSkill() != 400031047
               ? 4
               : 5
         );
         if (summon.getSkill() != 152141505
            && summon.getSkill() != 1121055
            && summon.getSkill() != 154110010
            && summon.getSkill() != 14121003
            && summon.getOwner() != null
            && summon.getOwner().getMap() != null) {
            MapleFoothold fh = summon.getOwner().getMap().getFootholds().findBelow(summon.getPosition());
            if (fh != null) {
               mplew.writeShort(fh.getId());
            } else {
               mplew.writeShort(0);
            }
         } else {
            mplew.writeShort(0);
         }

         mplew.write(summon.getMoveAbility().getValue());
         mplew.write(summon.getAssistType());
         mplew.write(animated ? 1 : 0);
         mplew.writeInt(summon.getMobObjectID());
         mplew.write(0);
         mplew.write(summon.getMobObjectID() != 0 ? 0 : 1);
         mplew.writeInt(0);
         mplew.writeInt(0);
         MapleCharacter chr = summon.getOwner();
         boolean avatarLook = (
               summon.getSkill() == 400001071
                  || summon.getSkill() == 4341006
                  || summon.getSkill() == 14111024
                  || summon.getSkill() == 14121054
                  || summon.getSkill() == 14121055
                  || summon.getSkill() == 14121056
                  || summon.getSkill() == 400011005
                  || summon.getSkill() == 131001017
                  || summon.getSkill() >= 400031007 && summon.getSkill() <= 400031009
                  || summon.getSkill() >= 500061046 && summon.getSkill() <= 500061049
                  || summon.getSkill() == 400041028
                  || summon.getSkill() == 500061004
            )
            && chr != null;
         mplew.write(avatarLook);
         if (avatarLook) {
            PacketHelper.addCharLook(mplew, chr, true, summon.isZeroBeta());
         }

         if (summon.getSkill() == 35111002) {
            mplew.write(0);
         }

         if (GameConstants.isShadowServant(summon.getSkill())) {
            if (summon.getSkill() == 400001071) {
               mplew.writeInt(300);
               mplew.writeInt(0);
            } else if (summon.getSkill() == 131001017) {
               mplew.writeInt(200);
               mplew.writeInt(30);
            } else if (summon.getSkill() != 400041028 && summon.getSkill() != 500061004) {
               mplew.writeInt(
                  summon.getSkill() == 14111024 || summon.getSkill() == 400031007 || summon.getSkill() == 500061046 || summon.getSkill() == 400011005
                     ? 400
                     : (summon.getSkill() != 14121055 && summon.getSkill() != 400031008 ? 1200 : 800)
               );
               mplew.writeInt(
                  summon.getSkill() == 14111024 || summon.getSkill() == 400031007 || summon.getSkill() == 500061046 || summon.getSkill() == 400011005
                     ? 30
                     : (summon.getSkill() != 14121055 && summon.getSkill() != 400031008 ? 90 : 60)
               );
            } else {
               mplew.writeInt(450);
               mplew.writeInt(30);
            }
         }

         if (summon.getSkill() == 400001065) {
            System.out.println("asfsafafaf");
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
         }

         boolean isJaguar = summon.getSkill() >= 33001007 && summon.getSkill() <= 33001015;
         mplew.write(false);
         mplew.writeInt(summon.getSummonRemoveTime() - System.currentTimeMillis());
         mplew.write(summon.getSkill() != 5321003 && summon.getSkill() != 152121005 && (summon.getSkill() < 400011012 || summon.getSkill() > 400011014) ? 1 : 0);
         mplew.writeInt(summon.getSummonRLType());
         if (chr != null) {
            SecondaryStatEffect level = chr.getSkillLevelData(summon.getSkill());
            if (level != null) {
               mplew.writeInt(level.getRange());
            } else {
               mplew.writeInt(0);
            }
         } else {
            mplew.writeInt(0);
         }

         mplew.writeInt(0);
         if (isJaguar) {
            boolean m_bJaguarActive = summon.isSubJaguar();
            mplew.write(m_bJaguarActive);
            mplew.write(0);
         }

         boolean isCristal = summon.getSkill() == 400021073 || summon.getSkill() == 152101000;
         if (summon.getSkill() == 400051046) {
            isCristal = true;
         }

         mplew.write(isCristal);
         if (isCristal) {
            mplew.writeInt(summon.getEnergyCharge());
            mplew.writeInt(summon.getEnergyLevel());
         }

         mplew.writeInt(summon.getSummonedGroup().size());

         for (Integer summonedID : summon.getSummonedGroup()) {
            mplew.writeInt(summonedID);
         }

         mplew.writeInt(0);
         mplew.writeInt(-1);
         mplew.write(0);
         mplew.write(summon.isZeroBeta());
         mplew.writeInt(0);
         if (summon.getSkill() == 152101000) {
            List<Integer> idx = new ArrayList<>();

            for (int index = 0; index < summon.getEnergyLevel(); index++) {
               if (summon.getEnableEnergySkill(index) > 0) {
                  idx.add(index);
               }
            }

            mplew.writeInt(idx.size());

            for (Integer indexx : idx) {
               mplew.writeInt(indexx + 1);
               mplew.writeInt(1);
            }
         }

         if (summon.getSkill() == 152141505 || summon.getSkill() == 152141501) {
            mplew.writeInt(summon.getCrystalPos());
         }

         if (summon.getSkill() == 151111001 || summon.getSkill() == 400021047 || summon.getSkill() == 400051011) {
            mplew.writeInt(SkillFactory.getSkill(summon.getSkill()).getEffect(summon.getSkillLevel()).getDuration());
         }

         return mplew.getPacket();
      }

      public static byte[] removeSummon(int ownerId, int objId) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
         mplew.writeInt(ownerId);
         mplew.writeInt(objId);
         mplew.write(10);
         return mplew.getPacket();
      }

      public static byte[] removeSummon(Summoned summon, boolean animated) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
         mplew.writeInt(summon.getOwnerId());
         mplew.writeInt(summon.getObjectId());
         if (animated) {
            switch (summon.getSkill()) {
               case 33101008:
               case 35111001:
               case 35111002:
               case 35111005:
               case 35111009:
               case 35111010:
               case 35111011:
               case 35121009:
               case 35121010:
               case 35121011:
                  mplew.write(5);
                  break;
               case 35121003:
                  mplew.write(10);
                  break;
               default:
                  mplew.write(4);
            }
         } else {
            mplew.write(1);
         }

         return mplew.getPacket();
      }

      public static byte[] moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
         mplew.writeInt(cid);
         mplew.writeInt(oid);
         mplew.writeInt(0);
         mplew.encodePos(startPos);
         mplew.writeInt(0);
         PacketHelper.serializeMovementList(mplew, moves);
         return mplew.getPacket();
      }

      public static byte[] summonAttack(
         int cid, int summonObjId, int summonSkillId, byte animation, byte tbyte, List<Pair<Integer, List<Long>>> allDamage, int level, boolean darkFlare
      ) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
         mplew.writeInt(cid);
         mplew.writeInt(summonObjId);
         mplew.writeInt(level);
         mplew.write(animation);
         mplew.write(tbyte);

         for (Pair<Integer, List<Long>> attackEntry : allDamage) {
            mplew.writeInt(attackEntry.left);
            mplew.write(7);
            if (summonSkillId == 400001071) {
               mplew.writeInt(0);
            }

            for (long dmg : attackEntry.right) {
               mplew.writeLong(dmg);
            }
         }

         mplew.write(darkFlare ? 1 : 0);
         mplew.write(0);
         mplew.writeShort(0);
         mplew.writeShort(0);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.writeShort(0);
         mplew.writeShort(0);
         return mplew.getPacket();
      }

      public static byte[] pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackPair> attack) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PVP_SUMMON.getValue());
         mplew.writeInt(cid);
         mplew.writeInt(oid);
         mplew.write(playerLevel);
         mplew.write(animation);
         mplew.encodePos(pos);
         mplew.writeInt(0);
         mplew.write(attack.size());

         for (AttackPair p : attack) {
            mplew.writeInt(p.objectid);
            mplew.encodePos(p.point);
            mplew.write(p.attack.size());
            mplew.write(0);

            for (Pair<Long, Boolean> atk : p.attack) {
               mplew.writeLong(atk.left);
            }
         }

         return mplew.getPacket();
      }

      public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
         mplew.writeInt(cid);
         mplew.writeInt(summonSkillId);
         mplew.write(newStance);
         return mplew.getPacket();
      }

      public static byte[] damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
         mplew.writeInt(cid);
         mplew.writeInt(summonSkillId);
         mplew.write(unkByte);
         mplew.writeInt(damage);
         mplew.writeInt(monsterIdFrom);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] summonAttackDone(int playerID, int objectID, int targetID) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK_DONE.getValue());
         mplew.writeInt(playerID);
         mplew.writeInt(objectID);
         if (targetID > 0) {
            mplew.write(1);
            mplew.writeInt(targetID);
         }

         return mplew.getPacket();
      }

      public static byte[] summonSetFix(int playerID, int objectID, boolean isFixed) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_SET_FIX.getValue());
         mplew.writeInt(playerID);
         mplew.writeInt(objectID);
         mplew.write(isFixed);
         return mplew.getPacket();
      }
   }

   public static class UIPacket {
      public static byte[] OnSetMirrorDungeonInfo(boolean clear) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SET_MIRROR_DUNGEON_INFO.getValue());
         mplew.writeInt(clear ? 0 : ServerConstants.dungeonList.size());

         for (Pair<String, String> d : ServerConstants.dungeonList) {
            mplew.writeMapleAsciiString(d.left);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(d.right);
         }

         return mplew.getPacket();
      }

      public static byte[] getDirectionStatus(boolean enable) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.DIRECTION_STATUS.getValue());
         mplew.write(enable ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] openUI(int type) {
         PacketEncoder mplew = new PacketEncoder(6);
         mplew.writeShort(SendPacketOpcode.OPEN_UI.getValue());
         mplew.writeInt(type);
         return mplew.getPacket();
      }

      public static byte[] closeUI(int type) {
         PacketEncoder mplew = new PacketEncoder(3);
         mplew.writeShort(SendPacketOpcode.CLOSE_UI.getValue());
         mplew.writeInt(type);
         return mplew.getPacket();
      }

      public static byte[] openUIOption(int ui, int op1, int... op2) {
         PacketEncoder mplew = new PacketEncoder(10);
         mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
         mplew.writeInt(ui);
         mplew.writeInt(op1);
         mplew.writeInt(op2.length);

         for (int i : op2) {
            mplew.writeInt(i);
         }

         return mplew.getPacket();
      }

      public static byte[] sendRepairWindow(int npc) {
         PacketEncoder mplew = new PacketEncoder(10);
         mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
         mplew.writeInt(33);
         mplew.writeInt(npc);
         return mplew.getPacket();
      }

      public static byte[] IntroLock(boolean enable) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INTRO_LOCK.getValue());
         mplew.write(enable ? 1 : 0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] setIngameDirectionMode(boolean zenia, boolean blackFrame, boolean forceMouseOver, boolean showUI) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
         packet.write(zenia);
         packet.write(blackFrame);
         packet.write(forceMouseOver);
         packet.write(showUI);
         return packet.getPacket();
      }

      public static byte[] setIngameDirectionMode(boolean blackFrame, boolean forceMouseOver, boolean showUI) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
         packet.write(1);
         packet.write(blackFrame);
         packet.write(forceMouseOver);
         packet.write(showUI);
         return packet.getPacket();
      }

      public static byte[] endInGameDirectionMode(int blackFrame) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
         packet.write(0);
         packet.write(blackFrame);
         return packet.getPacket();
      }

      public static byte[] setInGameDirectionMode(int wtf) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
         mplew.write(wtf > 0 ? 1 : 0);
         if (wtf > 0) {
            mplew.writeShort(wtf);
         }

         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] setStandAloneMode(boolean enable) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SET_STAND_ALONE_MODE.getValue());
         mplew.write(enable ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] summonHelper(boolean summon) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_HINT.getValue());
         mplew.write(summon ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] summonMessage(int type) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
         mplew.write(1);
         mplew.writeInt(type);
         mplew.writeInt(7000);
         return mplew.getPacket();
      }

      public static byte[] summonMessage(String message) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
         mplew.write(0);
         mplew.writeMapleAsciiString(message);
         mplew.writeInt(200);
         mplew.writeShort(0);
         mplew.writeInt(10000);
         return mplew.getPacket();
      }

      public static byte[] InGameDirectionEvent(String str, int... args) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         int type = args[0];
         if (type > 0 && type < 5) {
            type++;
         } else if (type >= 5 && type < 10) {
            type += 2;
         } else if (type >= 10) {
            type += 3;
         }

         mplew.write(type);
         switch (type) {
            case 0:
               mplew.writeInt(args[1]);
               mplew.writeInt(args[2]);
            case 1:
            case 5:
            case 10:
            case 11:
            case 12:
            case 18:
            case 19:
            case 24:
            default:
               break;
            case 2:
               mplew.writeInt(args[1]);
               break;
            case 3:
               mplew.writeMapleAsciiString(str);
               mplew.writeInt(args[1]);
               mplew.writeInt(args[2]);
               mplew.writeInt(args[3]);
               mplew.write(true);
               mplew.writeInt(args[4]);
               mplew.write(args[5]);
               if (args[5] > 0) {
                  mplew.writeInt(args[6]);
                  mplew.write(args[7]);
                  mplew.write(args[8]);
               }

               mplew.write(0);
               break;
            case 4:
               mplew.writeInt(args[1]);
               break;
            case 6:
               mplew.writeMapleAsciiString(str);
               mplew.writeInt(args[1]);
               mplew.writeInt(args[2]);
               mplew.writeInt(args[3]);
               break;
            case 7:
               mplew.write(args[1] > 0 ? 1 : 0);
               mplew.writeInt(args[2]);
               mplew.write(args[3] > 0 ? 1 : 0);
               if (args[1] > 0 && args[3] > 0) {
                  mplew.writeInt(args[4]);
                  mplew.writeInt(args[5]);
               }
               break;
            case 8:
               mplew.writeInt(args[1]);
               break;
            case 9:
               mplew.write(false);
               mplew.writeInt(args[1]);
               mplew.writeInt(args[2]);
               mplew.writeInt(args[3]);
               mplew.writeInt(args[4]);
               mplew.writeInt(args[5]);
               break;
            case 13:
               mplew.write(args[1]);
               break;
            case 14:
               mplew.writeInt(args[1]);
               break;
            case 15:
               mplew.writeMapleAsciiString(str);
               mplew.write(args[1]);
               break;
            case 16:
               mplew.writeMapleAsciiString(str);
               mplew.write(args[1]);
               mplew.writeShort(args[2]);
               mplew.writeInt(args[3]);
               mplew.writeInt(args[4]);
               break;
            case 17:
               mplew.write(args[1]);

               for (int i = 0; i < args[1]; i++) {
                  mplew.writeInt(args[2]);
               }
               break;
            case 20:
               mplew.writeInt(args[1]);
               mplew.writeInt(args[2]);
               mplew.write(args[3]);
               break;
            case 21:
               mplew.writeInt(args[1]);
               break;
            case 22:
               mplew.write(args[1]);
               break;
            case 23:
               mplew.write(args[1]);
               break;
            case 25:
               mplew.writeInt(args[1]);
               break;
            case 26:
               mplew.writeMapleAsciiString(str);
         }

         return mplew.getPacket();
      }

      public static byte[] setVansheeMode(int mode) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         packet.write(CField.ingameDirectionType.hideCharacter.getType());
         packet.write(mode);
         return packet.getPacket();
      }

      public static byte[] cameraZoom(int time, int scale, int timePos, int endPosX, int endPosY) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         packet.write(CField.ingameDirectionType.cameraZoom.getType());
         packet.write(0);
         packet.writeInt(time);
         packet.writeInt(scale);
         packet.writeInt(timePos);
         packet.writeInt(endPosX);
         packet.writeInt(endPosY);
         packet.writeInt(0);
         packet.writeMapleAsciiString("");
         packet.write(false);
         return packet.getPacket();
      }

      public static byte[] getDirectionInfo(int type, int value) {
         PacketEncoder mplew = new PacketEncoder();
         if (type > 0 && type < 5) {
            type++;
         } else if (type >= 5 && type < 10) {
            type += 2;
         } else if (type >= 10) {
            type += 3;
         }

         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(type);
         mplew.writeLong(value);
         return mplew.getPacket();
      }

      public static byte[] getDirectionInfo(String data, int value, int x, int y, int a, int b) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(4);
         mplew.writeMapleAsciiString(data);
         mplew.writeInt(value);
         mplew.writeInt(x);
         mplew.writeInt(y);
         mplew.write(a);
         if (a > 0) {
            mplew.writeInt(0);
         }

         mplew.write(b);
         if (b > 1) {
            mplew.writeInt(0);
            mplew.write(a);
            mplew.write(b);
         }

         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] hideChar(boolean enable) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(CField.ingameDirectionType.hideCharacter.getType());
         mplew.write(enable);
         return mplew.getPacket();
      }

      public static byte[] getDirectionInfo(String data, int duration, int x, int y, int a, int baseNPC, int notOrigin) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.USER_IN_GAME_DIRECTION_EVENT.getValue());
         mplew.write(3);
         mplew.writeMapleAsciiString(data);
         mplew.writeInt(duration);
         mplew.writeInt(x);
         mplew.writeInt(y);
         mplew.write(1);
         mplew.writeInt(a);
         mplew.write(1);
         mplew.writeInt(baseNPC);
         mplew.write(notOrigin);
         mplew.write(0);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] reissueMedal(int itemId, int type) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.REISSUE_MEDAL.getValue());
         mplew.write(type);
         mplew.writeInt(itemId);
         return mplew.getPacket();
      }

      public static final byte[] playMovie(String data, boolean show) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PLAY_MOVIE.getValue());
         mplew.writeMapleAsciiString(data);
         mplew.write(show ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] progressMessageFont(String msg, boolean RuneSystem) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PROGRESS_MESSAGE_FONT.getValue());
         mplew.writeInt(3);
         mplew.writeInt(RuneSystem ? 17 : 20);
         mplew.writeInt(RuneSystem ? 0 : 15);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.writeMapleAsciiString(msg);
         return mplew.getPacket();
      }

      public static byte[] sendBigScriptProgressMessage(String message, FontType fontType, FontColorType colorType) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.PROGRESS_MESSAGE_FONT.getValue());
         mplew.writeInt(fontType.getType());
         mplew.writeInt(20);
         mplew.writeInt(colorType.getType());
         mplew.writeInt(0);
         mplew.write(0);
         mplew.writeMapleAsciiString(message);
         return mplew.getPacket();
      }
   }

   public static enum ingameDirectionType {
      enableUI(0),
      effectPlay(3),
      cameraMove(7),
      cameraZoom(9),
      hideCharacter(13),
      Monologue(15),
      forceMove(22);

      int type;

      private ingameDirectionType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
