package network.models;

import com.google.common.collect.UnmodifiableIterator;
import constants.GameConstants;
import constants.HexaMatrixConstants;
import constants.QuestExConstants;
import constants.devtempConstants.MapleEventList;
import constants.devtempConstants.MapleStatusInfo;
import database.DBConfig;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.SendPacketOpcode;
import network.auction.AuctionAlarmType;
import network.center.Center;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.context.ExpIncreaseInfo;
import objects.context.ReportLogEntry;
import objects.context.SpecialSunday;
import objects.context.SundayEventList;
import objects.context.friend.FriendEntry;
import objects.context.guild.Guild;
import objects.context.guild.GuildCharacter;
import objects.context.guild.GuildSkill;
import objects.context.guild.alliance.Alliance;
import objects.fields.child.dojang.DojangMyRanking;
import objects.fields.child.dojang.DojangRanking;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.quest.MapleQuestStatus;
import objects.shop.BuyLimitEntry;
import objects.shop.HiredMerchant;
import objects.shop.MaplePlayerShopItem;
import objects.users.BlackList;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.MapleTrait;
import objects.users.potential.CharacterPotentialHolder;
import objects.users.skills.LarknessDirection;
import objects.users.skills.LinkSkill;
import objects.users.skills.LinkSkillEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.VCore;
import objects.users.stats.Flag992;
import objects.users.stats.HexaCore;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.HexTool;
import objects.utils.Pair;
import objects.utils.StringUtil;

public class CWvsContext {
   public static byte[] onMesoPickupResult(int meso) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.MOB_DROP_MESO_PICKUP.getValue());
      o.writeInt(meso);
      return o.getPacket();
   }

   public static byte[] showNewEffect(String title, String msg, boolean b) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.SHOW_NEW_EFFECT.getValue());
      o.write(true);
      o.writeMapleAsciiString(title);
      o.writeMapleAsciiString(msg);
      o.write(b);
      o.write(0);
      return o.getPacket();
   }

   public static byte[] setLinkSkillPreset(LinkSkill skills) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.SET_LINK_SKILL_PRESET.getValue());
      o.write(skills.getLinkSkillPresets().size());

      for (List<LinkSkillEntry> skillList : skills.getLinkSkillPresets()) {
         o.writeInt(skills.getLinkSkillPresets().indexOf(skillList) + 1);
         o.writeInt(skillList.size());

         for (LinkSkillEntry skill : skillList) {
            o.writeInt(skill.getRealSkillID());
            o.writeInt(skill.getLinkingPlayerID());
         }
      }

      return o.getPacket();
   }

   public static byte[] enableActions(MapleCharacter chr) {
      return enableActions(chr, true);
   }

   public static byte[] enableActions(MapleCharacter chr, boolean exclusive) {
      return updatePlayerStats(new EnumMap<>(MapleStat.class), exclusive, chr);
   }

   public static byte[] updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
      return updatePlayerStats(stats, false, chr);
   }

   public static byte[] updatePlayerStats(Map<MapleStat, Long> mystats, boolean exclusive, MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
      mplew.write(exclusive);
      mplew.write(0);
      int updateMask = 0;

      for (MapleStat statupdate : mystats.keySet()) {
         updateMask |= statupdate.getValue();
      }

      mplew.write(1);
      mplew.writeInt(updateMask);

      for (Entry<MapleStat, Long> statupdate : mystats.entrySet()) {
         switch ((MapleStat)statupdate.getKey()) {
            case SKIN:
            case BATTLE_RANK:
            case ICE_GAGE:
               mplew.write(statupdate.getValue().byteValue());
               break;
            case STR:
            case DEX:
            case INT:
            case LUK:
            case AVAILABLEAP:
            case FATIGUE:
               mplew.writeShort(statupdate.getValue().shortValue());
               break;
            case AVAILABLESP:
               if (GameConstants.isSeparatedSp(chr.getJob())) {
                  mplew.write(chr.getRemainingSpSize());

                  for (int i = 0; i < chr.getRemainingSps().length; i++) {
                     if (chr.getRemainingSp(i) > 0) {
                        mplew.write(i + 1);
                        mplew.writeInt(chr.getRemainingSp(i));
                     }
                  }
                  break;
               }

               mplew.writeShort(chr.getRemainingSp());
               break;
            case TRAIT_LIMIT:
               for (int i = 0; i < 2; i++) {
                  for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
                     mplew.writeShort(chr.getTrait(t).getTodayExp());
                  }
               }

               mplew.write(0);
               mplew.writeLong(PacketHelper.getTime(-2L));
               break;
            case EXP:
            case MESO:
            case BATTLE_POINTS:
            case VIRTUE:
               mplew.writeLong(statupdate.getValue());
               break;
            case PET:
               mplew.writeLong(statupdate.getValue().intValue());
               mplew.writeLong(statupdate.getValue().intValue());
               mplew.writeLong(statupdate.getValue().intValue());
               break;
            case JOB:
               mplew.writeShort(statupdate.getValue().intValue());
               mplew.writeShort(chr.getSubcategory());
               break;
            default:
               mplew.writeInt(statupdate.getValue().intValue());
         }
      }

      mplew.write(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] temporaryStats_Aran() {
      Map<MapleStat.Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);
      stats.put(MapleStat.Temp.STR, 999);
      stats.put(MapleStat.Temp.DEX, 999);
      stats.put(MapleStat.Temp.INT, 999);
      stats.put(MapleStat.Temp.LUK, 999);
      stats.put(MapleStat.Temp.WATK, 255);
      stats.put(MapleStat.Temp.ACC, 999);
      stats.put(MapleStat.Temp.AVOID, 999);
      stats.put(MapleStat.Temp.SPEED, 140);
      stats.put(MapleStat.Temp.JUMP, 120);
      return temporaryStats(stats);
   }

   public static byte[] Hyper(String name, int lvl, int sp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.HYPER.getValue());
      mplew.writeMapleAsciiString(name);
      mplew.writeInt(lvl);
      mplew.writeInt(sp);
      mplew.write(1);
      mplew.writeInt(lvl);
      return mplew.getPacket();
   }

   public static byte[] temporaryStats(Map<MapleStat.Temp, Integer> mystats) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TEMP_STATS.getValue());
      int updateMask = 0;

      for (MapleStat.Temp statupdate : mystats.keySet()) {
         updateMask |= statupdate.getValue();
      }

      mplew.writeInt(updateMask);

      for (Entry<MapleStat.Temp, Integer> statupdate : mystats.entrySet()) {
         switch ((MapleStat.Temp)statupdate.getKey()) {
            case SPEED:
            case JUMP:
            case UNKNOWN:
               mplew.write(statupdate.getValue().byteValue());
               break;
            default:
               mplew.writeShort(statupdate.getValue().shortValue());
         }
      }

      return mplew.getPacket();
   }

   public static byte[] temporaryStats_Reset() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TEMP_STATS_RESET.getValue());
      return mplew.getPacket();
   }

   public static byte[] updateMatrixSkills(Map<Skill, SkillEntry> update) {
      PacketEncoder mplew = new PacketEncoder(7 + update.size() * 20);
      mplew.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
      mplew.write(0);
      mplew.writeShort(0);
      mplew.writeShort(update.size());

      for (Entry<Skill, SkillEntry> z : update.entrySet()) {
         mplew.writeInt(z.getKey().getId());
         mplew.writeInt(z.getValue().skillevel);
         mplew.writeInt(z.getValue().masterlevel);
         PacketHelper.addExpirationTime(mplew, z.getValue().expiration);
      }

      mplew.write(7);
      return mplew.getPacket();
   }

   public static byte[] updateSkills(Map<Skill, SkillEntry> skills, boolean unk, boolean devoteActivated, byte type) {
      PacketEncoder mplew = new PacketEncoder(7 + skills.size() * 20);
      mplew.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
      mplew.write(unk);
      mplew.writeShort(devoteActivated ? 1 : 0);
      mplew.writeShort(skills.size());
      skills.forEach((skill, skillEntry) -> {
         mplew.writeInt(skill.getId());
         mplew.writeInt(skillEntry.skillevel);
         mplew.writeInt(skillEntry.masterlevel);
         PacketHelper.addExpirationTime(mplew, skillEntry.expiration);
      });
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] giveFameErrorResponse(int op) {
      return OnFameResult(op, null, true, 0);
   }

   public static byte[] OnFameResult(int op, String charname, boolean raise, int newFame) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
      mplew.write(op);
      if (op == 0 || op == 5) {
         mplew.writeMapleAsciiString(charname == null ? "" : charname);
         mplew.write(raise ? 1 : 0);
         if (op == 0) {
            mplew.writeInt(newFame);
         }
      }

      return mplew.getPacket();
   }

   public static byte[] BombLieDetector(boolean error, int mapid, int channel) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BOMB_LIE_DETECTOR.getValue());
      mplew.write(error ? 2 : 1);
      mplew.writeInt(mapid);
      mplew.writeInt(channel);
      return mplew.getPacket();
   }

   public static byte[] report(int mode) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.REPORT_RESPONSE.getValue());
      mplew.write(mode);
      if (mode == 2) {
         mplew.write(0);
         mplew.writeInt(1);
      }

      return mplew.getPacket();
   }

   public static byte[] OnSetClaimSvrAvailableTime(int from, int to) {
      PacketEncoder mplew = new PacketEncoder(4);
      mplew.writeShort(SendPacketOpcode.REPORT_TIME.getValue());
      mplew.write(from);
      mplew.write(to);
      return mplew.getPacket();
   }

   public static byte[] OnClaimSvrStatusChanged(boolean enable) {
      PacketEncoder mplew = new PacketEncoder(3);
      mplew.writeShort(SendPacketOpcode.REPORT_STATUS.getValue());
      mplew.write(enable ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] updateMount(MapleCharacter chr, boolean levelup) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_MOUNT.getValue());
      mplew.writeInt(chr.getId());
      mplew.writeInt(chr.getMount().getLevel());
      mplew.writeInt(chr.getMount().getExp());
      mplew.writeInt(chr.getMount().getFatigue());
      mplew.write(levelup ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] getShowQuestCompletion(int id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_QUEST_COMPLETION.getValue());
      mplew.writeInt(id);
      return mplew.getPacket();
   }

   public static byte[] useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.USE_SKILL_BOOK.getValue());
      mplew.write(0);
      mplew.writeInt(chr.getId());
      mplew.write(1);
      mplew.writeInt(skillid);
      mplew.writeInt(maxlevel);
      mplew.write(canuse ? 1 : 0);
      mplew.write(success ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] useAPSPReset(boolean spReset, int cid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(spReset ? SendPacketOpcode.SP_RESET.getValue() : SendPacketOpcode.AP_RESET.getValue());
      mplew.write(1);
      mplew.writeInt(cid);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] finishedGather(int type) {
      return gatherSortItem(true, type);
   }

   public static byte[] finishedSort(int type) {
      return gatherSortItem(false, type);
   }

   public static byte[] gatherSortItem(boolean gather, int type) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(gather ? SendPacketOpcode.FINISH_GATHER.getValue() : SendPacketOpcode.FINISH_SORT.getValue());
      mplew.write(1);
      mplew.write(type);
      return mplew.getPacket();
   }

   public static byte[] updateGender(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_GENDER.getValue());
      mplew.write(chr.getGender());
      return mplew.getPacket();
   }

   public static byte[] charInfo(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAR_INFO.getValue());
      mplew.writeInt(chr.getId());
      mplew.writeInt(chr.getLevel());
      mplew.writeShort(chr.getJob());
      mplew.writeShort(chr.getSubcategory());
      mplew.write(chr.getStat().pvpRank);
      mplew.writeInt(chr.getFame());
      mplew.write(chr.getMarriageId() > 0 ? 1 : 0);
      if (chr.getMarriageId() > 0) {
         mplew.writeZeroBytes(48);
      }

      List<Integer> prof = chr.getProfessions();
      mplew.write(prof.size());

      for (int i : prof) {
         mplew.writeShort(i);
      }

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

      DojangMyRanking rank = DojangRanking.getThisWeekMyRank(2, chr.getName());
      Pair<Integer, Integer> pair = DojangRanking.getLastTryDojang(2, chr.getName());
      if (rank == null) {
         rank = DojangRanking.getLastWeekMyRank(2, chr.getName());
      }

      int dojangRank = 0;
      if (rank != null && pair != null) {
         dojangRank = rank.getPoint() / 1000;
      } else {
         dojangRank = 0;
      }

      SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
      Calendar cal = Calendar.getInstance();
      if (pair != null) {
         cal.set(1, pair.left);
         cal.set(3, pair.right);
      }

      int totalUnion = chr.getUnionLevel();
      mplew.writeInt(dojangRank);
      mplew.writeMapleAsciiString(rank != null ? sdf.format(cal.getTime().getTime()) : "");
      mplew.writeInt(totalUnion);
      mplew.write(-1);
      mplew.write(0);
      int petCount = 0;

      for (MaplePet pet : chr.getPets()) {
         if (pet != null) {
            petCount++;
         }
      }

      mplew.write(petCount);
      Item inv = null;
      int peteqid = 0;
      int petindex = 0;
      int position = 114;

      for (MaplePet petx : chr.getPets()) {
         if (petx != null) {
            if (petindex >= 1) {
               position = 123 + petindex;
            }

            inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(-position));
            peteqid = inv != null ? inv.getItemId() : 0;
            mplew.write(1);
            mplew.writeInt(petindex++);
            mplew.writeInt(petx.getPetItemId());
            mplew.writeMapleAsciiString(petx.getName());
            mplew.write(petx.getLevel());
            mplew.writeShort(petx.getCloseness());
            mplew.write(petx.getFullness());
            mplew.writeShort(petx.getFlags());
            mplew.writeInt(peteqid);
            mplew.writeInt(-1);
         }
      }

      mplew.write(0);
      Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-46);
      mplew.writeInt(medal == null ? 0 : medal.getItemId());
      List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
      mplew.writeShort(medalQuests.size());

      for (Pair<Integer, Long> x : medalQuests) {
         mplew.writeInt(x.left);
         mplew.writeLong(x.right);
      }

      chr.getDamageSkinSaveInfo().encode(mplew);

      for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
         mplew.write(chr.getTrait(t).getLevel());
      }

      mplew.writeInt(chr.getClient().getAccID());
      PacketHelper.addFarmInfo(mplew);
      return mplew.getPacket();
   }

   public static byte[] spawnPortal(int townId, int targetId, int skillId, Point pos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
      mplew.writeInt(townId);
      mplew.writeInt(targetId);
      if (townId != 999999999 && targetId != 999999999) {
         mplew.writeInt(skillId);
         mplew.encodePos(pos);
      }

      return mplew.getPacket();
   }

   public static byte[] mechPortal(boolean first, int characterID, Point pos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MECH_PORTAL.getValue());
      mplew.write(0);
      mplew.writeInt(characterID);
      mplew.encodePos(pos);
      mplew.write(first);
      return mplew.getPacket();
   }

   public static byte[] echoMegaphone(String name, String message) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ECHO_MESSAGE.getValue());
      mplew.write(0);
      mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
      mplew.writeMapleAsciiString(name);
      mplew.writeMapleAsciiString(message);
      return mplew.getPacket();
   }

   public static byte[] showQuestMsg(String msg) {
      return serverNotice(5, msg);
   }

   public static byte[] Mulung_Pts(int recv, int total) {
      return showQuestMsg("You have received " + recv + " training points, for the accumulated total of " + total + " training points.");
   }

   public static byte[] serverMessage(String message) {
      return serverMessage(4, 0, message, false);
   }

   public static byte[] serverNotice(int type, String message) {
      return serverMessage(type, 0, message, false);
   }

   public static byte[] serverNotice(int type, int channel, String message) {
      return serverMessage(type, channel, message, false);
   }

   public static byte[] serverNotice(int type, int channel, String message, boolean smegaEar) {
      return serverMessage(type, channel, message, smegaEar);
   }

   private static byte[] serverMessage(int type, int channel, String message, boolean megaEar) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      mplew.write(type);
      if (type == 4) {
         mplew.write(1);
      }

      if (type != 23 && type != 24) {
         mplew.writeMapleAsciiString(message);
      }

      switch (type) {
         case 3:
         case 9:
         case 23:
         case 26:
         case 27:
            if (type == 3 || type == 23) {
               String[] split = message.split(":");
               String sender = split[0];
               String msg = split[1];
               ReportLogEntry report = new ReportLogEntry(sender, msg, 0);
               report.encode(mplew);
            }

            mplew.write(channel - 1);
            if (type != 9) {
               mplew.write(megaEar ? 1 : 0);
            }
         case 4:
         case 5:
         case 7:
         case 8:
         case 10:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 21:
         case 22:
         case 24:
         default:
            break;
         case 6:
         case 11:
         case 20:
            mplew.writeInt(channel >= 1000000 && channel < 6000000 ? channel : 0);
            break;
         case 12:
            mplew.writeInt(channel);
            break;
         case 25:
            mplew.writeShort(0);
      }

      return mplew.getPacket();
   }

   public static byte[] getHyperMegaphone(String text, String msg, int bEar, int op, MapleCharacter chr, MapleClient c, PacketDecoder slea) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      packet.write(8);
      packet.writeMapleAsciiString(text);
      ReportLogEntry report = new ReportLogEntry(chr.getName(), msg, chr.getId());
      report.encode(packet);
      if (c.getChannel() == 5) {
         packet.write(104);
      } else {
         packet.write(c.getChannel() - 1);
      }

      packet.write(bEar);
      packet.writeInt(5076100);
      Item item = null;
      String itemName = null;
      int achievementID = 0;
      long achievementTime = 0L;
      if (op == 1) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         byte invType = (byte)slea.readInt();
         byte pos = (byte)slea.readInt();
         MapleInventoryType inv;
         if (pos > 0) {
            inv = MapleInventoryType.getByType(invType);
         } else {
            inv = MapleInventoryType.EQUIPPED;
         }

         if (inv != null) {
            item = chr.getInventory(inv).getItem(pos);
            itemName = ii.getName(item.getItemId());
         }
      } else if (op == 2) {
         achievementID = slea.readInt();
         achievementTime = slea.readLong();
      }

      PacketHelper.onChatBonusPacket(packet, op, item, itemName, achievementID, achievementTime);
      return packet.getPacket();
   }

   public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, String gacha) {
      return getGachaponMega(name, message, item, rareness, false, gacha);
   }

   public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, boolean dragon, String gacha) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      mplew.write(13);
      mplew.writeMapleAsciiString(name + message);
      if (!dragon) {
         mplew.writeInt(0);
         mplew.writeInt(item.getItemId());
      }

      mplew.writeMapleAsciiString(gacha);
      PacketHelper.addItemInfo(mplew, item);
      return mplew.getPacket();
   }

   public static byte[] getBroadcastMsgGachapon(String message, int itemID, Item item) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      mplew.write(11);
      mplew.writeMapleAsciiString(message);
      mplew.writeInt(itemID);
      mplew.write(item != null);
      PacketHelper.addItemInfo(mplew, item);
      return mplew.getPacket();
   }

   public static byte[] getAniMsg(int questID, int time) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      mplew.write(23);
      mplew.writeInt(questID);
      mplew.writeInt(time);
      return mplew.getPacket();
   }

   public static byte[] tripleSmega(List<String> message, boolean ear, int channel) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      mplew.write(10);
      if (message.get(0) != null) {
         mplew.writeMapleAsciiString(message.get(0));
      }

      String[] split = message.get(0).split(":");
      String sender = split[0];
      String msg_ = split[1];
      ReportLogEntry report = new ReportLogEntry(sender, msg_, 0);
      report.encode(mplew);
      mplew.write(message.size());

      for (int i = 1; i < message.size(); i++) {
         if (message.get(i) != null) {
            mplew.writeMapleAsciiString(message.get(i));
         }

         split = message.get(i).split(":");
         sender = split[0];
         msg_ = split[1];
         report = new ReportLogEntry(sender, msg_, 0);
         report.encode(mplew);
      }

      mplew.write(channel - 1);
      mplew.write(ear ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] itemMegaphone(String msg, boolean whisper, int channel, Item item, String itemName) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
      mplew.write(8);
      mplew.writeMapleAsciiString(msg);
      String[] split = msg.split(":");
      String sender = split[0];
      String msg_ = split[1];
      ReportLogEntry report = new ReportLogEntry(sender, msg_, 0);
      report.encode(mplew);
      mplew.write(channel - 1);
      mplew.write(whisper ? 1 : 0);
      mplew.writeInt(5071000);
      PacketHelper.onChatBonusPacket(mplew, item != null ? 1 : 0, item, itemName, 0, 0L);
      return mplew.getPacket();
   }

   public static byte[] getIncubatorResult(int rewardID, short quantity, int rewardID2, short quantity2, int gachaponItemID) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.INCUBATOR_RESULT.getValue());
      mplew.write(true);
      mplew.writeInt(rewardID);
      mplew.writeShort(quantity);
      mplew.writeInt(gachaponItemID);
      mplew.writeInt(0);
      mplew.writeInt(rewardID2);
      mplew.writeInt((int)quantity2);
      mplew.write(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] getOwlOpen() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
      mplew.write(10);
      mplew.write(GameConstants.owlItems.length);

      for (int i : GameConstants.owlItems) {
         mplew.writeInt(i);
      }

      return mplew.getPacket();
   }

   public static byte[] getOwlSearched(int itemSearch, List<HiredMerchant> hms) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
      mplew.write(9);
      mplew.writeInt(0);
      mplew.writeShort(0);
      mplew.writeInt(itemSearch);
      int size = 0;

      for (HiredMerchant hm : hms) {
         size += hm.searchItem(itemSearch).size();
      }

      mplew.writeInt(size);

      for (HiredMerchant hm : hms) {
         for (MaplePlayerShopItem item : hm.searchItem(itemSearch)) {
            mplew.writeMapleAsciiString(hm.getOwnerName());
            mplew.writeInt(hm.getMap().getId());
            mplew.writeMapleAsciiString(hm.getDescription());
            mplew.writeInt(item.item.getQuantity());
            mplew.writeInt(item.bundles);
            mplew.writeLong(item.price);
            switch (2) {
               case 0:
                  mplew.writeInt(hm.getOwnerId());
                  break;
               case 1:
                  mplew.writeInt(hm.getStoreId());
                  break;
               default:
                  mplew.writeInt(hm.getObjectId());
            }

            mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
            mplew.write(GameConstants.getInventoryType(itemSearch).getType());
            if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
               PacketHelper.addItemInfo(mplew, item.item);
            }
         }
      }

      return mplew.getPacket();
   }

   public static byte[] getOwlMessage(int msg) {
      PacketEncoder mplew = new PacketEncoder(3);
      mplew.writeShort(SendPacketOpcode.OWL_RESULT.getValue());
      mplew.write(msg);
      return mplew.getPacket();
   }

   public static byte[] sendEngagementRequest(String name, int cid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ENGAGE_REQUEST.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString(name);
      mplew.writeInt(cid);
      return mplew.getPacket();
   }

   public static byte[] sendEngagement(byte msg, int item, MapleCharacter male, MapleCharacter female) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ENGAGE_RESULT.getValue());
      mplew.write(msg);
      if (msg == 11 || msg == 12) {
         mplew.writeInt(0);
         mplew.writeInt(male.getId());
         mplew.writeInt(female.getId());
         mplew.writeShort(1);
         mplew.writeInt(item);
         mplew.writeInt(item);
         mplew.writeMapleAsciiString_(male.getName(), 13);
         mplew.writeMapleAsciiString_(female.getName(), 13);
      } else if (msg == 15) {
         mplew.writeMapleAsciiString_("Male", 13);
         mplew.writeMapleAsciiString_("Female", 13);
         mplew.writeShort(0);
      }

      return mplew.getPacket();
   }

   public static byte[] sendWeddingGive() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
      mplew.write(9);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] sendWeddingReceive() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
      mplew.write(10);
      mplew.writeLong(-1L);
      mplew.writeInt(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] giveWeddingItem() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
      mplew.write(11);
      mplew.write(0);
      mplew.writeLong(0L);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] receiveWeddingItem() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
      mplew.write(15);
      mplew.writeLong(0L);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] yellowChat(String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.YELLOW_CHAT.getValue());
      mplew.write(-1);
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] catchMob(int mobid, int itemid, byte success) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CATCH_MOB.getValue());
      mplew.write(success);
      mplew.writeInt(itemid);
      mplew.writeInt(mobid);
      return mplew.getPacket();
   }

   public static byte[] spawnPlayerNPC(PlayerNPC npc, MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAYER_NPC.getValue());
      mplew.write(1);
      mplew.writeInt(npc.getId());
      mplew.writeMapleAsciiString(npc.getName());
      PacketHelper.addCharLook(mplew, npc.getChr(), true, false);
      return mplew.getPacket();
   }

   public static byte[] MulungEnergy(int energy) {
      return sendPyramidEnergy("energy", String.valueOf(energy));
   }

   public static byte[] sendPyramidEnergy(String type, String amount) {
      return sendString(1, type, amount);
   }

   public static byte[] sendGhostPoint(String type, String amount) {
      return sendString(2, type, amount);
   }

   public static byte[] sendGhostStatus(String type, String amount) {
      return sendString(3, type, amount);
   }

   public static byte[] sendString(int type, String object, String amount) {
      PacketEncoder mplew = new PacketEncoder();
      switch (type) {
         case 1:
            mplew.writeShort(SendPacketOpcode.SESSION_VALUE.getValue());
            break;
         case 2:
            mplew.writeShort(SendPacketOpcode.PARTY_VALUE.getValue());
            break;
         case 3:
            mplew.writeShort(SendPacketOpcode.MAP_VALUE.getValue());
      }

      mplew.writeMapleAsciiString(object);
      mplew.writeMapleAsciiString(amount);
      return mplew.getPacket();
   }

   public static byte[] sendLevelup(boolean family, int level, String name) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.LEVEL_UPDATE.getValue());
      mplew.write(family ? 1 : 2);
      mplew.writeInt(level);
      mplew.writeMapleAsciiString(name);
      return mplew.getPacket();
   }

   public static byte[] sendMarriage(boolean family, String name) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MARRIAGE_UPDATE.getValue());
      mplew.write(family ? 1 : 0);
      mplew.writeMapleAsciiString(name);
      return mplew.getPacket();
   }

   public static byte[] sendJobup(boolean family, int jobid, String name) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.JOB_UPDATE.getValue());
      mplew.write(family ? 1 : 0);
      mplew.writeInt(jobid);
      mplew.writeMapleAsciiString((!family ? "> " : "") + name);
      return mplew.getPacket();
   }

   public static byte[] getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> text, String msg, boolean ear) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.AVATAR_MEGA.getValue());
      mplew.writeInt(itemId);
      mplew.writeMapleAsciiString(chr.getName());

      for (String i : text) {
         mplew.writeMapleAsciiString(i);
      }

      ReportLogEntry report = new ReportLogEntry(chr.getName(), msg, chr.getId());
      report.encode(mplew);
      mplew.writeInt(channel - 1);
      mplew.write(ear ? 1 : 0);
      PacketHelper.addCharLook(mplew, chr, true, chr.getGender() == 1);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] pendantSlot(boolean p) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PENDANT_SLOT.getValue());
      mplew.write(p ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] followRequest(int chrid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.FOLLOW_REQUEST.getValue());
      mplew.writeInt(chrid);
      return mplew.getPacket();
   }

   public static byte[] getScriptProgressMessage(String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TOP_MSG.getValue());
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] getScriptProgressMessage(int itemID, String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TOP_MSG_ITEM.getValue());
      mplew.writeInt(itemID);
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] getStaticScreenMessage(String msg, boolean keep, int index) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.STATIC_SCREEN_MESSAGE.getValue());
      mplew.write(index);
      mplew.writeMapleAsciiString(msg);
      mplew.write(keep ? 0 : 1);
      return mplew.getPacket();
   }

   public static byte[] getOffStaticScreenMessage() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OFF_STATIC_SCREEN_MESSAGE.getValue());
      return mplew.getPacket();
   }

   public static byte[] updateJaguar(MapleCharacter from) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_JAGUAR.getValue());
      PacketHelper.addJaguarInfo(mplew, from);
      return mplew.getPacket();
   }

   public static byte[] ultimateExplorer() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ULTIMATE_EXPLORER.getValue());
      return mplew.getPacket();
   }

   public static byte[] updateSpecialStat(String stat, int array, int mode, int amount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SPECIAL_STAT.getValue());
      mplew.writeMapleAsciiString(stat);
      mplew.writeInt(array);
      mplew.writeInt(mode);
      mplew.write(1);
      mplew.writeInt(amount);
      return mplew.getPacket();
   }

   public static byte[] getMulungRanking() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MULUNG_DOJO_RANKING.getValue());
      mplew.writeInt(1);
      mplew.writeShort(1);
      mplew.writeMapleAsciiString("hi");
      mplew.writeLong(2L);
      return mplew.getPacket();
   }

   public static byte[] getMulungMessage(boolean dc, String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MULUNG_MESSAGE.getValue());
      mplew.write(dc ? 1 : 0);
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] updateMaplePoint(int mp) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MAPLE_POINT.getValue());
      mplew.writeInt(mp);
      return mplew.getPacket();
   }

   public static byte[] popupHomePage() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.POPUP_HOMEPAGE.getValue());
      mplew.write(0);
      mplew.write(1);
      mplew.writeMapleAsciiString("https://maplejin.com");
      return mplew.getPacket();
   }

   public static byte[] updateAzwanFame(int fame) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_HONOUR.getValue());
      mplew.writeInt(fame);
      return mplew.getPacket();
   }

   public static byte[] showPopupMessage(String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.POPUP_MSG.getValue());
      mplew.writeMapleAsciiString(msg);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static byte[] nameChangeUI(boolean use) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAR_NAME_CHANGE.getValue());
      mplew.writeInt(use ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] UltimateMaterial(
      int code, int speed, int unk0, int skill, short level, byte unk1, short unk2, short unk3, short unk4, int posx, int posy
   ) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ULTIMATE_MATERIAL.getValue());
      mplew.writeInt(85930057);
      mplew.writeInt(1);
      mplew.writeInt(code);
      mplew.writeInt(speed);
      mplew.writeInt(unk0);
      mplew.writeInt(skill);
      mplew.writeShort(level);
      mplew.writeInt(1);
      mplew.writeInt(14000);
      mplew.write(unk1);
      mplew.writeShort(unk2);
      mplew.writeShort(unk3);
      mplew.writeShort(unk4);
      mplew.writeInt(posx);
      mplew.writeInt(posy);
      return mplew.getPacket();
   }

   public static byte[] AddCore(VCore core) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ADD_CORE.getValue());
      mplew.writeInt(core.getCoreId());
      mplew.writeInt(core.getLevel());
      mplew.writeInt(core.getSkill1());
      mplew.writeInt(core.getSkill2());
      mplew.writeInt(core.getSkill3());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] UpdateCore(MapleCharacter chr, boolean update, int type, int number) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.CORE_LIST.getValue());
      PacketHelper.addMatrixInfo(o, chr.getVCoreSkillsNoLock(), chr.getVMatrixSlots());
      o.write(update);
      if (update) {
         o.writeInt(type);
         if (type == 0 || type == 1) {
            o.writeInt(number);
         }
      }

      return o.getPacket();
   }

   public static byte[] CoreEnforcementResult(int slot, int maxLevel, int beforeLevel, int afterlevel) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CORE_ENFORCEMENT_RESULT.getValue());
      mplew.writeInt(slot);
      mplew.writeInt(maxLevel);
      mplew.writeInt(beforeLevel);
      mplew.writeInt(afterlevel);
      return mplew.getPacket();
   }

   public static byte[] UpdateCoreQuantity(int quantity) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_CORE_QUANTITY.getValue());
      mplew.writeInt(quantity);
      return mplew.getPacket();
   }

   public static byte[] MakeCoreResult(VCore core, int size) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MAKE_CORE_RESULT.getValue());
      mplew.writeInt(core.getCoreId());
      mplew.writeInt(core.getLevel());
      mplew.writeInt(core.getSkill1());
      mplew.writeInt(core.getSkill2());
      mplew.writeInt(core.getSkill3());
      mplew.writeInt(size);
      return mplew.getPacket();
   }

   public static byte[] MatrixSkill(int skillid, byte level, int[] angle) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MATRIX_SKILL.getValue());
      mplew.write(1);
      mplew.writeInt(skillid);
      mplew.write(level);
      mplew.writeInt(angle.length);

      for (int j : angle) {
         mplew.writeInt(j);
      }

      return mplew.getPacket();
   }

   public static final byte[] OpenUIOnDead(MapleCharacter chr, int reviveType, int reviveType2) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.OPEN_UI_ON_DEAD.getValue());
      packet.writeInt(reviveType);
      packet.writeInt(reviveType2);
      packet.writeInt(0);
      packet.write(reviveType2 == 4 ? 1 : 0);
      packet.writeInt(reviveType2 == 4 ? 30 : 0);
      packet.writeInt(reviveType2 == 4 ? 5 : 0);
      if (reviveType2 == 4 && chr.getDeathCount() == 0) {
         packet.write(1);
      } else {
         packet.write(0);
      }

      return packet.getPacket();
   }

   public static final byte[] scrollUpgradeFeverTime(int mode) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SCROLL_UPGRADE_FEVER_TIME.getValue());
      packet.writeInt(mode);
      return packet.getPacket();
   }

   public static final byte[] setBuffProtector(int itemID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_BUFF_PROTECTOR.getValue());
      packet.writeInt(itemID);
      packet.write(0);
      return packet.getPacket();
   }

   public static byte[] setSonOfLinkedSkillResult() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_SON_OF_LINKED_SKILL_RESULT.getValue());
      packet.writeInt(1);
      packet.writeInt(50001214);
      packet.writeMapleAsciiString("asdasd");
      packet.writeMapleAsciiString("ddd");
      return packet.getPacket();
   }

   public static byte[] changeLinkSkillState(int skillID, boolean link) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CHANGE_LINK_SKILL_STATE.getValue());
      packet.writeInt(skillID);
      packet.writeInt(link ? 1 : 0);
      return packet.getPacket();
   }

   public static byte[] battleStatCoreInfo() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(338);
      packet.writeInt(5);
      packet.writeInt(1);
      packet.writeInt(540);
      packet.writeInt(101);
      packet.writeInt(540);
      packet.writeInt(201);
      packet.writeInt(540);
      packet.writeInt(301);
      packet.writeInt(540);
      packet.writeInt(401);
      packet.writeInt(540);
      return packet.getPacket();
   }

   public static byte[] onCharacterModified(MapleCharacter player, long flag) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CHARACTER_MODIFIED.getValue());
      PacketHelper.addCharacterInfo(packet, player, flag);
      return packet.getPacket();
   }

   public static byte[] onTowerChairSettingResult() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.TOWER_CHAIR_SETTING.getValue());
      return packet.getPacket();
   }

   public static byte[] wonderBerryResult(int itemID, Item item) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WONDER_BERRY_RESULT.getValue());
      packet.write(1);
      packet.writeInt(itemID);
      PacketHelper.addItemInfo(packet, item);
      return packet.getPacket();
   }

   public static byte[] wonderBerryResult(int failReason) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.WONDER_BERRY_RESULT.getValue());
      packet.write(0);
      packet.writeInt(failReason);
      return packet.getPacket();
   }

   public static byte[] miracleCirculatorWindow(int itemID, long uniqueID, int itemPos, List<CharacterPotentialHolder> skills) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MIRACLE_CIRCULATOR_WINDOW.getValue());
      packet.writeInt(skills.size());
      int pos = 1;

      for (CharacterPotentialHolder holder : skills) {
         packet.writeInt(holder.getSkillId());
         packet.write(holder.getSkillLevel());
         packet.write(pos++);
         packet.write(holder.getRank());
      }

      packet.writeInt(itemID);
      packet.writeLong(uniqueID);
      packet.writeInt(itemPos);
      return packet.getPacket();
   }

   public static byte[] setBuyLimitCount(BuyLimitEntry copy) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_BUY_LIMIT_COUNT.getValue());
      packet.write(false);
      copy.encode(packet);
      return packet.getPacket();
   }

   public static byte[] arcaneCatalyst(Equip equip, int qty) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ARCANE_CATALYST.getValue());
      PacketHelper.addItemInfo(mplew, equip);
      mplew.writeInt(qty);
      return mplew.getPacket();
   }

   public static byte[] arcaneCatalyst2(Equip equip) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ARCANE_CATALYST2.getValue());
      PacketHelper.addItemInfo(mplew, equip);
      return mplew.getPacket();
   }

   public static byte[] AuctionAlarm(AuctionAlarmType type, int itemID, long price, int totalQuantity, int buyQuantity) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.AUCTION_ALARM.getValue());
      mplew.write(0);
      mplew.writeLong(0L);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(itemID);
      mplew.writeInt(3);
      mplew.writeLong(price);
      mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      mplew.writeLong(totalQuantity);
      mplew.writeInt(buyQuantity);
      mplew.writeInt(5);
      return mplew.getPacket();
   }

   public static byte[] initSecurity() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.INIT_SECURITY.getValue());
      mplew.writeInt(2);
      mplew.write(0);
      mplew.write(0);
      mplew.writeMapleAsciiString("10000-AABBCCDD-EEFFAC");
      mplew.write(1);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] updateSecurity() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_SECURITY.getValue());
      int count = 1;
      mplew.writeInt(count);

      for (int i = 0; i < count; i++) {
         mplew.writeLong(0L);
         mplew.writeMapleAsciiString("10000-AABBCCDD-EEFFAC");
         mplew.write(0);
         mplew.writeMapleAsciiString(DBConfig.isGanglim ? "강림PC" : "진PC");
         mplew.writeInt(2);
         mplew.writeLong(PacketHelper.getTime(0L));
         mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
         mplew.write(1);
         mplew.writeInt(2);
         mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
         mplew.writeLong(0L);
      }

      return mplew.getPacket();
   }

   public static final byte[] bossClearCheck() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BOSS_CLEAR_CHECK.getValue());
      mplew.writeInt(QuestExConstants.bossQuests.size());
      UnmodifiableIterator var1 = QuestExConstants.bossQuests.keySet().iterator();

      while (var1.hasNext()) {
         Integer bossId = (Integer)var1.next();
         mplew.writeInt(bossId);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt((Integer)QuestExConstants.bossQuests.get(bossId));
      }

      return mplew.getPacket();
   }

   public static final byte[] blackList(boolean guild, byte type, String name, String denoteName, int id, int unk) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BLACK_LIST.getValue());
      mplew.write(guild);
      mplew.write(type);
      mplew.writeMapleAsciiString(name);
      if (!guild) {
         mplew.writeMapleAsciiString(denoteName);
      }

      mplew.writeInt(id);
      if (!guild) {
         mplew.writeInt(unk);
      }

      return mplew.getPacket();
   }

   public static final byte[] loadBlackLists(boolean guild, List<BlackList> blackLists) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.BLACK_LIST.getValue());
      mplew.write(guild);
      mplew.write(4);
      mplew.writeShort(blackLists.size());
      blackLists.forEach(blackList -> {
         if (guild) {
            mplew.writeInt(blackList.getId());
            mplew.writeMapleAsciiString(blackList.getName());
         } else {
            mplew.writeMapleAsciiString(blackList.getName());
            mplew.writeMapleAsciiString(blackList.getDenoteName());
            mplew.writeInt(blackList.getId());
            mplew.writeInt(blackList.getUnk());
         }
      });
      return mplew.getPacket();
   }

   public static final byte[] eventList() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.EVENT_LIST.getValue());
      mplew.writeInt(0);
      mplew.write(1);
      mplew.writeMapleAsciiString("메이플 이벤트");
      mplew.write(0);
      mplew.writeInt(0);
      LinkedHashMap<Integer, MapleEventList> eventList = MapleEventList.EventList;
      mplew.writeInt(eventList.size());

      for (Integer eKey : eventList.keySet()) {
         MapleEventList e = eventList.get(eKey);
         mplew.writeInt(eKey);
         mplew.writeMapleAsciiString(e.getEventName());
         mplew.writeMapleAsciiString(e.getEventDesc());
         mplew.writeInt(0);
         mplew.writeInt(e.getEventEndHour());
         mplew.writeInt(e.getEventStartTime());
         mplew.writeInt(e.getEventEndTime());
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
         mplew.write(1);
         mplew.writeInt(e.getItems().size());

         for (Integer item : e.getItems()) {
            mplew.writeInt(item);
         }

         mplew.writeInt(0);
         mplew.write(0);
         mplew.write(0);
      }

      LinkedList<SundayEventList> sEventList = SundayEventList.sEventList;
      int eventSize = sEventList.size();
      if (SpecialSunday.isActive) {
         if (SpecialSunday.activeRuneEXP) {
            eventSize++;
         }

         if (SpecialSunday.activeCombokillEXP) {
            eventSize++;
         }

         if (SpecialSunday.activeSpellTrace) {
            eventSize++;
         }

         if (SpecialSunday.activeAbility) {
            eventSize++;
         }

         if (SpecialSunday.activeStarForceOpO) {
            eventSize++;
         }

         if (SpecialSunday.activeStarForce100) {
            eventSize++;
         }

         if (SpecialSunday.activeStarForceDiscount) {
            eventSize++;
         }

         if (SpecialSunday.activeSoulGacha) {
            eventSize++;
         }
      }

      mplew.writeInt(26);
      mplew.writeInt(eventSize);

      for (SundayEventList s : sEventList) {
         mplew.writeMapleAsciiString(s.getEventName());
         mplew.writeInt(s.getEventFlag0());
         mplew.writeInt(s.getEventFlag1());
         mplew.writeInt(s.getEventVal0());
         mplew.writeInt(s.getEventStartTime());
         mplew.writeInt(s.getEventEndTime());
         mplew.writeInt(s.getEventVal1());
         mplew.writeInt(s.getEventEndHour());
         mplew.writeInt(s.getEventVal2());
         mplew.writeInt(-1);
         mplew.write(1);
         mplew.writeInt(s.getStarForceSaleValue());
         mplew.writeInt(s.getEventVal3());
         mplew.writeInt(s.getEventVal4());
         mplew.writeInt(s.getStarForce100Success().size());

         for (Pair<Integer, Integer> s100 : s.getStarForce100Success()) {
            mplew.writeInt(s100.left);
            mplew.writeInt(s100.right);
         }

         mplew.writeInt(s.getStarForceDoubleValue());
         mplew.writeMapleAsciiString(s.getEventDesc0());
         mplew.writeMapleAsciiString(s.getEventDesc1());
         mplew.writeMapleAsciiString(s.getEventDesc2());
      }

      if (SpecialSunday.isActive) {
         SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
         Calendar c = Calendar.getInstance();
         c.set(7, 1);
         if (SpecialSunday.activeRuneEXP) {
            mplew.writeMapleAsciiString("룬 경험치 버프 효과 +100%");
            mplew.writeInt(0);
            mplew.writeInt(17);
            mplew.writeInt(200);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeCombokillEXP) {
            mplew.writeMapleAsciiString("콤보킬 구슬 경험치 획득량 +300%");
            mplew.writeInt(0);
            mplew.writeInt(37);
            mplew.writeInt(300);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeSpellTrace) {
            mplew.writeMapleAsciiString("주문의 흔적 강화 비용 50% 할인");
            mplew.writeInt(3);
            mplew.writeInt(4);
            mplew.writeInt(50);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("#e일요일은 메이플! <썬데이 메이플> 이벤트!\r\n\r\n주문의 흔적 강화 비용\r\n#fc0xFFFFCC00#50% 할인!#k");
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeAbility) {
            mplew.writeMapleAsciiString("어빌리티 재설정 비용 50% 할인");
            mplew.writeInt(6);
            mplew.writeInt(4);
            mplew.writeInt(50);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("어빌리티 재설정 비용 50% 할인!");
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeStarForceOpO) {
            mplew.writeMapleAsciiString("스타포스 10성 이하 1+1 강화");
            mplew.writeInt(4);
            mplew.writeInt(40);
            mplew.writeInt(1);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(12);
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("#e일요일은 메이플! <썬데이 메이플> 이벤트!\r\n\r\n#fc0xFFFFCC00#10성 이하 스타포스 강화 성공시 1+1 강화!#k#n\r\n(슈페리얼 장비는 이벤트 대상에서 제외됩니다)");
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeStarForce100) {
            mplew.writeMapleAsciiString("스타포스 5, 10, 15성 100% 강화 성공");
            mplew.writeInt(4);
            mplew.writeInt(5);
            mplew.writeInt(0);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(3);
            mplew.writeInt(16);
            mplew.writeInt(1000);
            mplew.writeInt(11);
            mplew.writeInt(1000);
            mplew.writeInt(6);
            mplew.writeInt(1000);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString(
               "#e일요일은 메이플! <썬데이 메이플> 이벤트!\r\n\r\n#fc0xFFFFCC00#스타포스 5성, 10성, 15성#k에서 강화 시도 시 성공 확률 100%!#n\r\n(슈페리얼 장비는 이벤트 대상에서 제외됩니다)"
            );
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeStarForceDiscount) {
            mplew.writeMapleAsciiString("스타포스 강화#k 비용 30% 할인");
            mplew.writeInt(4);
            mplew.writeInt(4);
            mplew.writeInt(0);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(30);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString(
               "#e일요일은 메이플! <썬데이 메이플> 이벤트!\r\n\r\n#fc0xFFFFCC00#스타포스 강화#k 비용 30% 할인#n\r\n(파괴방지 비용과 슈페리얼 장비는 이벤트 대상에서 제외되며, MVP/PC방 할인은 30% 할인된 가격에 추가로 적용됩니다)"
            );
            mplew.writeMapleAsciiString("");
         }

         if (SpecialSunday.activeSoulGacha) {
            mplew.writeMapleAsciiString("위대한 소울 획득 확률 5배");
            mplew.writeInt(20);
            mplew.writeInt(32);
            mplew.writeInt(5);
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(Integer.parseInt(date.format(c.getTime())));
            mplew.writeInt(0);
            mplew.writeInt(235900);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString("위대한 소울 획득 확률 5배!");
            mplew.writeMapleAsciiString("");
            mplew.writeMapleAsciiString("");
         }
      }

      return mplew.getPacket();
   }

   public static byte[] TOPMSG_ITEM(int itemID, String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TOP_MSG_ITEM.getValue());
      mplew.writeInt(itemID);
      mplew.writeMapleAsciiString(msg);
      return mplew.getPacket();
   }

   public static byte[] updateWeaponMotion(int chrid, int show) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_WEAPON_MOTION.getValue());
      mplew.writeInt(chrid);
      mplew.writeInt(show);
      return mplew.getPacket();
   }

   public static byte[] updateShowMedal(int chrid, int show) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_MEDAL.getValue());
      mplew.writeInt(chrid);
      mplew.write(show);
      return mplew.getPacket();
   }

   public static byte[] updateShowItemEffect(int chrid, int show) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.UPDATE_ITEMEFFECT.getValue());
      mplew.writeInt(chrid);
      mplew.write(show);
      return mplew.getPacket();
   }

   public static byte[] onUseMixLensResult(int itemId, int baseFace, int newFace, boolean isDressUp, byte flag) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHANGE_MIX_LENS_RESULT.getValue());
      mplew.writeInt(itemId);
      mplew.write(true);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      mplew.write(isDressUp);
      mplew.write(flag);
      if (flag == 100) {
         mplew.writeLong(0L);
      }

      mplew.writeInt(1);
      mplew.writeInt(0);
      mplew.writeInt(newFace);
      mplew.writeInt(baseFace);
      mplew.write(255);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] loadChattingShortCut(Map<Integer, byte[]> keymap) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.CHATTING_QUICKSHORT.getValue());
      o.writeInt(keymap.size());

      for (Entry<Integer, byte[]> entry : keymap.entrySet()) {
         o.writeInt(entry.getKey());
         o.encodeBuffer(entry.getValue());
      }

      return o.getPacket();
   }

   public static byte[] setCashCodyPreset(int preset) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.CODY_PRESET.getValue());
      o.write(true);

      for (int i = 0; i < 3; i++) {
         o.writeInt(i == preset ? 1 : 0);
      }

      return o.getPacket();
   }

   public static byte[] hexaMatrixReslut(int type, int msgtype, int coreid, int corelevel) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.HEXA_MATRIX_RESULT.getValue());
      o.writeInt(type);
      o.writeInt(msgtype);
      o.writeInt(coreid);
      o.writeInt(corelevel);
      return o.getPacket();
   }

   public static byte[] hexaMatrixCoreLevelUp(HexaCore core) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.HEXA_MATRIX_LEVELUP.getValue());
      Map<Integer, Integer> skillCore = core.getSkillCores();
      o.writeInt(skillCore.size());

      for (Entry<Integer, Integer> entry : skillCore.entrySet()) {
         o.writeInt(entry.getKey());
         o.writeInt(entry.getValue());
         o.write(1);
         o.write(core.isDisabledSkillCore(entry.getKey()));
      }

      o.write(0);
      o.writeInt(0);
      return o.getPacket();
   }

   public static byte[] hexaMatrixStatUpdate(HexaCore core) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.HEXA_MATRIX_STAT_UPDATE.getValue());
      int size = core.getStatSize();
      o.writeInt(size);
      HexaCore.HexaStatData data = core.getStat(0);
      int coreid = data.getCoreId();
      Map<Integer, HexaCore.HexaStatInfo> statmap = data.getStats();
      HexaCore.HexaStatInfo info0 = statmap.get(0);
      HexaCore.HexaStatInfo info1 = statmap.get(1);
      HexaCore.HexaStatInfo info2 = statmap.get(2);
      int stat0 = info0 == null ? -1 : info0.type.getType();
      int stat1 = info1 == null ? -1 : info1.type.getType();
      int stat2 = info2 == null ? -1 : info2.type.getType();
      int level0 = info0 == null ? 0 : info0.level;
      int level1 = info1 == null ? 0 : info1.level;
      int level2 = info2 == null ? 0 : info2.level;

      for (int i = 0; i < size; i++) {
         o.writeInt(coreid);
         o.writeInt(coreid);
         o.writeInt(0);
         o.writeInt(statmap.isEmpty() ? 0 : 1);
         o.writeInt(stat0);
         o.writeInt(level0);
         o.writeInt(stat1);
         o.writeInt(level1);
         o.writeInt(stat2);
         o.writeInt(level2);
      }

      o.writeInt(coreid);
      if (core.getSavedStatData() == null) {
         o.writeInt(1);
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
      } else {
         o.writeInt(1);
         Map<Integer, HexaCore.HexaStatInfo> sstatmap = core.getSavedStatData().getStats();
         o.writeInt(sstatmap.isEmpty() ? 0 : 1);
         HexaCore.HexaStatInfo sinfo0 = sstatmap.get(0);
         HexaCore.HexaStatInfo sinfo1 = sstatmap.get(1);
         HexaCore.HexaStatInfo sinfo2 = sstatmap.get(2);
         int sstat0 = sinfo0 == null ? -1 : sinfo0.type.getType();
         int sstat1 = sinfo1 == null ? -1 : sinfo1.type.getType();
         int sstat2 = sinfo2 == null ? -1 : sinfo2.type.getType();
         int slevel0 = sinfo0 == null ? 0 : sinfo0.level;
         int slevel1 = sinfo1 == null ? 0 : sinfo1.level;
         int slevel2 = sinfo2 == null ? 0 : sinfo2.level;
         o.writeInt(sstat0);
         o.writeInt(slevel0);
         o.writeInt(sstat1);
         o.writeInt(slevel1);
         o.writeInt(sstat2);
         o.writeInt(slevel2);
      }

      o.writeInt(size);

      for (int i = 0; i < size; i++) {
         o.writeInt(coreid);
         o.writeInt(coreid);
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
      }

      return o.getPacket();
   }

   public static byte[] hexaMatrixInformation() {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.HEXA_MATRIX_INFORMATION.getValue());
      HexaMatrixConstants.HexaMatrixFlag[] flags = HexaMatrixConstants.getSkillFlags();
      o.writeInt(flags.length);

      for (HexaMatrixConstants.HexaMatrixFlag flag : flags) {
         o.writeInt(flag.getFlag());
         o.writeInt(HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(flag));
         o.writeInt(HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(flag));
      }

      o.writeInt(flags.length);

      for (HexaMatrixConstants.HexaMatrixFlag flag : flags) {
         o.writeInt(flag.getFlag());
         int maxLevel = HexaMatrixConstants.getHexaSkillMasterLevel(flag);
         o.writeInt(maxLevel);

         for (int i = 1; i <= maxLevel; i++) {
            o.writeInt(i);
            o.writeInt(HexaMatrixConstants.getNeedSolErdaToUpgradeHexaSkill(flag, i));
            o.writeInt(HexaMatrixConstants.getNeedSolErdaPieceToUpgradeHexaSkill(flag, i));
         }
      }

      o.writeInt(HexaMatrixConstants.getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT));
      o.writeInt(HexaMatrixConstants.getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT));
      int maxLevel = HexaMatrixConstants.getHexaSkillMasterLevel(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT);
      o.writeInt(maxLevel);

      for (int i = 0; i < maxLevel; i++) {
         o.writeInt(i);
         o.writeInt(HexaMatrixConstants.getNeedSolErdaToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT, i));
         o.writeInt(HexaMatrixConstants.getNeedSolErdaPieceToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag.HEXA_STAT, i));
      }

      maxLevel = HexaMatrixConstants.getHexaStatMasterLevel();
      o.writeInt(maxLevel);

      for (int i = 0; i < maxLevel; i++) {
         o.writeInt(i);
         o.encodeDouble(HexaMatrixConstants.getHexaStatWeight(i));
      }

      o.writeInt(21);

      for (int i = 10; i <= 30; i++) {
         o.writeInt(i);
         o.writeLong(10000000L);
      }

      o.writeInt(31);

      for (int i = 0; i <= 30; i++) {
         o.writeInt(i);
         o.writeLong(100000000L);
      }

      return o.getPacket();
   }

   public static byte[] cashBulletOnOffResult(boolean success) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CASH_BULLET_EFFECT_ONOFF_RESULT.getValue());
      packet.write(success);
      return packet.getPacket();
   }

   public static class AlliancePacket {
      public static byte[] getAllianceInfo(Alliance alliance) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(12);
         mplew.write(alliance == null ? 0 : 1);
         if (alliance != null) {
            addAllianceInfo(mplew, alliance);
         }

         return mplew.getPacket();
      }

      private static void addAllianceInfo(PacketEncoder mplew, Alliance alliance) {
         mplew.writeInt(alliance.getId());
         mplew.writeMapleAsciiString(alliance.getName());

         for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRank(i));
         }

         mplew.write(alliance.getGuildCount());

         for (int i = 0; i < alliance.getGuildCount(); i++) {
            mplew.writeInt(alliance.getGuildId(i));
         }

         mplew.writeInt(alliance.getCapacity());
         mplew.writeMapleAsciiString(alliance.getNotice());
      }

      public static byte[] getGuildAlliance(Alliance alliance) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(13);
         if (alliance == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
         } else {
            int noGuilds = alliance.getGuildCount();
            Guild[] g = new Guild[noGuilds];

            for (int i = 0; i < alliance.getGuildCount(); i++) {
               g[i] = Center.Guild.getGuild(alliance.getGuildId(i));
               if (g[i] == null) {
                  return CWvsContext.enableActions(null);
               }
            }

            mplew.writeInt(noGuilds);

            for (Guild gg : g) {
               CWvsContext.GuildPacket.getGuildInfo(mplew, gg);
            }

            return mplew.getPacket();
         }
      }

      public static byte[] allianceMemberOnline(int alliance, int gid, int id, boolean online) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(14);
         mplew.writeInt(alliance);
         mplew.writeInt(gid);
         mplew.writeInt(id);
         mplew.write(online ? 1 : 0);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] removeGuildFromAlliance(Alliance alliance, Guild expelledGuild, boolean expelled) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(16);
         addAllianceInfo(mplew, alliance);
         mplew.writeInt(expelledGuild.getId());
         CWvsContext.GuildPacket.getGuildInfo(mplew, expelledGuild);
         mplew.write(expelled ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] addGuildToAlliance(Alliance alliance, Guild newGuild) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(18);
         addAllianceInfo(mplew, alliance);
         mplew.writeInt(newGuild.getId());
         CWvsContext.GuildPacket.getGuildInfo(mplew, newGuild);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] sendAllianceInvite(String allianceName, MapleCharacter inviter) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(3);
         mplew.writeInt(inviter.getGuildId());
         mplew.writeMapleAsciiString(inviter.getName());
         mplew.writeMapleAsciiString(allianceName);
         return mplew.getPacket();
      }

      public static byte[] getAllianceUpdate(Alliance alliance) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(23);
         addAllianceInfo(mplew, alliance);
         return mplew.getPacket();
      }

      public static byte[] createGuildAlliance(Alliance alliance) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(15);
         addAllianceInfo(mplew, alliance);
         int noGuilds = alliance.getGuildCount();
         Guild[] g = new Guild[noGuilds];

         for (int i = 0; i < alliance.getGuildCount(); i++) {
            g[i] = Center.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
               return CWvsContext.enableActions(null);
            }
         }

         for (Guild gg : g) {
            CWvsContext.GuildPacket.getGuildInfo(mplew, gg);
         }

         return mplew.getPacket();
      }

      public static byte[] updateAlliance(GuildCharacter mgc, int allianceid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(24);
         mplew.writeInt(allianceid);
         mplew.writeInt(mgc.getGuildId());
         mplew.writeInt(mgc.getId());
         mplew.writeInt(mgc.getLevel());
         mplew.writeInt(mgc.getJobId());
         return mplew.getPacket();
      }

      public static byte[] updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(25);
         mplew.writeInt(allianceid);
         mplew.writeInt(oldLeader);
         mplew.writeInt(newLeader);
         return mplew.getPacket();
      }

      public static byte[] allianceRankChange(int aid, String[] ranks) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(26);
         mplew.writeInt(aid);
         mplew.writeInt(0);

         for (String r : ranks) {
            mplew.writeMapleAsciiString(r);
         }

         return mplew.getPacket();
      }

      public static byte[] updateAllianceRank(GuildCharacter mgc) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(27);
         mplew.writeInt(mgc.getId());
         mplew.write(mgc.getAllianceRank());
         return mplew.getPacket();
      }

      public static byte[] changeAllianceNotice(int allianceid, String notice) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(28);
         mplew.writeInt(allianceid);
         mplew.writeMapleAsciiString(notice);
         return mplew.getPacket();
      }

      public static byte[] disbandAlliance(int alliance) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(30);
         mplew.writeInt(alliance);
         return mplew.getPacket();
      }

      public static byte[] changeAlliance(Alliance alliance, boolean in) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(1);
         mplew.write(in ? 1 : 0);
         mplew.writeInt(in ? alliance.getId() : 0);
         int noGuilds = alliance.getGuildCount();
         Guild[] g = new Guild[noGuilds];

         for (int i = 0; i < noGuilds; i++) {
            g[i] = Center.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
               return CWvsContext.enableActions(null);
            }
         }

         mplew.write(noGuilds);

         for (int ix = 0; ix < noGuilds; ix++) {
            mplew.writeInt(g[ix].getId());
            Collection<GuildCharacter> members = g[ix].getMembers();
            mplew.writeInt(members.size());

            for (GuildCharacter mgc : members) {
               mplew.writeInt(mgc.getId());
               mplew.write(in ? mgc.getAllianceRank() : 0);
            }
         }

         return mplew.getPacket();
      }

      public static byte[] changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(2);
         mplew.writeInt(allianceid);
         mplew.writeInt(oldLeader);
         mplew.writeInt(newLeader);
         return mplew.getPacket();
      }

      public static byte[] changeGuildInAlliance(Alliance alliance, Guild guild, boolean add) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(4);
         mplew.writeInt(add ? alliance.getId() : 0);
         mplew.writeInt(guild.getId());
         Collection<GuildCharacter> members = guild.getMembers();
         mplew.writeInt(members.size());

         for (GuildCharacter mgc : members) {
            mplew.writeInt(mgc.getId());
            mplew.write(add ? mgc.getAllianceRank() : 0);
         }

         return mplew.getPacket();
      }

      public static byte[] changeAllianceRank(int allianceid, GuildCharacter player) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
         mplew.write(5);
         mplew.writeInt(allianceid);
         mplew.writeInt(player.getId());
         mplew.writeInt(player.getAllianceRank());
         return mplew.getPacket();
      }
   }

   public static class BuddylistPacket {
      public static byte[] updateBuddylist(Collection<FriendEntry> buddylist, FriendEntry buddies, byte op) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
         mplew.write(op);
         if (op == 21) {
            int buddysize = 0;

            for (FriendEntry buddy : buddylist) {
               if (buddy.isVisible()) {
                  buddysize++;
               }
            }

            mplew.writeInt(buddysize);

            for (FriendEntry buddyx : buddylist) {
               if (buddyx.isVisible()) {
                  mplew.writeInt(buddyx.getCharacterId());
                  mplew.writeMapleAsciiString_(buddyx.getName(), 13);
                  mplew.write(7);
                  mplew.writeInt(buddyx.getChannel() == -1 ? -1 : buddyx.getChannel() - 1);
                  mplew.writeMapleAsciiString_(buddyx.getGroupName(), 18);
                  mplew.writeInt(buddyx.getAccountId());
                  mplew.writeMapleAsciiString_(buddyx.getName(), 13);
                  mplew.writeMapleAsciiString_(buddyx.getMemo(), 260);
               }
            }
         } else if (op == 24) {
            mplew.writeInt(buddies.getCharacterId());
            mplew.writeInt(buddies.getAccountId());
            mplew.write(false);
            mplew.writeInt(buddies.getCharacterId());
            mplew.writeMapleAsciiString_(buddies.getName(), 13);
            mplew.write(7);
            mplew.writeInt(buddies.getChannel() == -1 ? -1 : buddies.getChannel() - 1);
            mplew.writeMapleAsciiString_(buddies.getGroupName(), 18);
            mplew.writeInt(buddies.getAccountId());
            mplew.writeMapleAsciiString_(buddies.getName(), 13);
            mplew.writeMapleAsciiString_(buddies.getMemo(), 260);
         } else if (op == 43) {
            mplew.write(1);
            mplew.writeInt(buddies.getAccountId());
         } else if (op == 40) {
            mplew.writeInt(buddies.getCharacterId());
            mplew.writeMapleAsciiString_(buddies.getName(), 13);
            mplew.write(7);
            mplew.writeInt(buddies.getChannel() - 1);
            mplew.writeMapleAsciiString_(buddies.getGroupName(), 18);
            mplew.writeInt(buddies.getAccountId());
            mplew.writeMapleAsciiString_(buddies.getName(), 13);
            mplew.writeMapleAsciiString_(buddies.getMemo(), 260);
         }

         return mplew.getPacket();
      }

      public static byte[] buddyAddMessage(String charname) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
         mplew.write(27);
         mplew.writeMapleAsciiString(charname);
         return mplew.getPacket();
      }

      public static byte[] requestBuddylistAdd(
         int cidFrom, int accId, String nameFrom, int levelFrom, int jobFrom, MapleClient c, String groupName, String memo
      ) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
         mplew.write(26);
         mplew.write(1);
         mplew.writeInt(cidFrom);
         mplew.writeInt(accId);
         mplew.writeMapleAsciiString(nameFrom);
         mplew.writeInt(levelFrom);
         mplew.writeInt(jobFrom);
         mplew.writeInt(0);
         mplew.writeInt(cidFrom);
         mplew.writeMapleAsciiString_(nameFrom, 13);
         mplew.write(6);
         mplew.writeInt(c.getChannel() - 1);
         mplew.writeMapleAsciiString_(groupName, 18);
         mplew.writeInt(accId);
         mplew.writeMapleAsciiString_(nameFrom, 13);
         mplew.writeMapleAsciiString_(memo, 260);
         return mplew.getPacket();
      }

      public static byte[] updateBuddyChannel(int characterId, int accountId, int channel, String name) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
         mplew.write(45);
         mplew.writeInt(characterId);
         mplew.writeInt(accountId);
         mplew.write(true);
         mplew.writeInt(channel);
         mplew.write(true);
         mplew.write(1);
         mplew.writeMapleAsciiString(name);
         return mplew.getPacket();
      }

      public static byte[] updateBuddyCapacity(int capacity) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
         mplew.write(47);
         mplew.write(capacity);
         return mplew.getPacket();
      }
   }

   public static class BuffPacket {
      public static byte[] sendLarknessStack(int larkness, LarknessDirection larknessDirection) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.CHANGE_LARKNESS_STACK.getValue());
         mplew.writeInt(larkness);
         mplew.write(larknessDirection.getDirection());
         return mplew.getPacket();
      }

      public static byte[] showAngelicBlessBuffEffect(int cid, int ringid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
         mplew.writeInt(cid);
         PacketHelper.writeSingleMask(mplew, SecondaryStatFlag.RepeatEffect);
         mplew.writeShort(1);
         mplew.writeInt(-ringid);
         mplew.writeShort(0);
         mplew.write(0);
         mplew.writeShort(15);
         mplew.writeLong(0L);
         mplew.writeLong(0L);
         mplew.writeZeroBytes(7);
         mplew.writeInt(0);
         mplew.write(1);
         return mplew.getPacket();
      }

      public static byte[] cancelDebuff(SecondaryStatFlag mask) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
         mplew.writeInt(0);
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
         PacketHelper.writeSingleMask(mplew, mask);
         mplew.write(3);
         mplew.write(1);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] remoteTemporaryStatSet(MapleCharacter player, Flag992 flag992) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
         mplew.writeInt(player.getId());
         player.getSecondaryStat().encodeForRemote(mplew, flag992, player.getPlayerJob(), false);
         mplew.encodeBuffer(new byte[30]);
         mplew.writeShort(0);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] remoteTemporaryStatReset(MapleCharacter player, Flag992 flag992) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
         mplew.writeInt(player.getId());
         flag992.encode(mplew);
         if (flag992.check(SecondaryStatFlag.PoseType)) {
            mplew.write(0);
         }

         if (flag992.check(SecondaryStatFlag.ProtectionOfAncientWarrior)) {
            mplew.write(0);
         }

         player.getSecondaryStat().encodeIndieTemporaryStats(mplew, flag992, true);
         mplew.write(4);
         mplew.write(1);
         return mplew.getPacket();
      }

      public static byte[] cancelForeignDebuff(int cid, SecondaryStatFlag mask) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
         mplew.writeInt(cid);
         PacketHelper.writeSingleMask(mplew, mask);
         mplew.write(3);
         mplew.write(1);
         mplew.write(0);
         return mplew.getPacket();
      }
   }

   public static class GuildPacket {
      public static byte[] showSearchGuildInfo(List<Guild> guilds, int searchType, boolean allMatch, String keyword) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(searchType);
         mplew.write(0);
         mplew.writeMapleAsciiString(keyword);
         mplew.write(allMatch);
         mplew.write(0);
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeInt(guilds.size());

         for (Guild guild : guilds) {
            mplew.writeInt(guild.getId());
            mplew.write(guild.getLevel());
            mplew.writeMapleAsciiString(guild.getName());
            mplew.writeMapleAsciiString(guild.getLeaderName());
            mplew.writeShort(guild.getMembers().size());
            mplew.writeShort(guild.getAvgLevel());
            mplew.write(0);
            mplew.writeLong(0L);
            mplew.write(guild.isAllowJoinRequest());
            mplew.writeMapleAsciiString(guild.getNotice());
            mplew.writeInt(guild.getConnectTimeFlag());
            mplew.writeInt(guild.getActivityFlag());
            mplew.writeInt(guild.getAgeGroupFlag());
            mplew.write(0);
         }

         return mplew.getPacket();
      }

      public static byte[] joinRequestResult(Guild guild) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(80);
         mplew.writeInt(guild.getId());
         mplew.write(guild.getLevel());
         mplew.writeMapleAsciiString(guild.getName());
         mplew.writeMapleAsciiString(guild.getLeaderName());
         mplew.writeShort(guild.getMembers().size());
         mplew.writeShort(guild.getAvgLevel());
         mplew.write(0);
         mplew.writeLong(0L);
         mplew.write(guild.isAllowJoinRequest());
         mplew.writeMapleAsciiString(guild.getNotice());
         mplew.writeInt(guild.getConnectTimeFlag());
         mplew.writeInt(guild.getActivityFlag());
         mplew.writeInt(guild.getAgeGroupFlag());
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] getJoinRequestGuilds(List<Guild> guilds) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(188);
         mplew.writeInt(guilds.size());

         for (Guild g : guilds) {
            mplew.writeInt(g.getId());
            mplew.write(g.getLevel());
            mplew.writeMapleAsciiString(g.getName());
            mplew.writeMapleAsciiString(g.getLeaderName());
            mplew.writeShort(g.getMembers().size());
            mplew.writeShort(g.getAvgLevel());
            mplew.write(0);
            mplew.writeLong(0L);
            mplew.write(g.isAllowJoinRequest());
            mplew.writeMapleAsciiString(g.getNotice());
            mplew.writeInt(g.getConnectTimeFlag());
            mplew.writeInt(g.getActivityFlag());
            mplew.writeInt(g.getAgeGroupFlag());
            mplew.write(0);
         }

         return mplew.getPacket();
      }

      public static byte[] cancelJoinRequest(int playerID, int guildID) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(86);
         mplew.writeInt(playerID);
         mplew.writeInt(guildID);
         mplew.writeMapleAsciiString("");
         return mplew.getPacket();
      }

      public static byte[] visitGuild(Guild guild) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(55);
         mplew.writeInt(guild.getId());
         mplew.writeMapleAsciiString("");
         getGuildInfo(mplew, guild);

         for (int i = 1; i <= 4; i++) {
            mplew.writeInt(0);
         }

         return mplew.getPacket();
      }

      public static byte[] showGuildRank(int rank1, int rank2, int rank3) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(56);
         mplew.writeShort(rank1);
         mplew.writeShort(rank2);
         mplew.writeShort(rank3);
         return mplew.getPacket();
      }

      public static byte[] guildInvite(int gid, String guildName, int characterID, String charName, int levelFrom, int jobFrom) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(103);
         mplew.writeInt(gid);
         mplew.writeMapleAsciiString(guildName);
         mplew.writeInt(characterID);
         mplew.writeMapleAsciiString(charName);
         mplew.writeInt(levelFrom);
         mplew.writeInt(jobFrom);
         mplew.writeInt(0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] cancelGuildRequest(int gid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(69);
         mplew.writeInt(gid);
         return mplew.getPacket();
      }

      public static byte[] requestGuild(int gid, MapleCharacter chr) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(-1);
         mplew.writeInt(gid);
         mplew.writeInt(chr.getId());
         mplew.writeMapleAsciiString_(chr.getName(), 13);
         mplew.writeInt(chr.getJob());
         mplew.writeInt(chr.getLevel());
         mplew.writeInt(3);
         mplew.writeInt(1);
         mplew.writeInt(3);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeLong(PacketHelper.getTime(-2L));
         return mplew.getPacket();
      }

      public static byte[] showGuildInfo(MapleCharacter c) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         if (c != null && c.getMGC() != null) {
            mplew.write(53);
            Guild g = Center.Guild.getGuild(c.getGuildId());
            if (g == null) {
               mplew.write(0);
               mplew.write(0);
               return CWvsContext.enableActions(c);
            } else {
               mplew.writeInt(50000);
               mplew.write(true);
               getGuildInfo(mplew, g);
               mplew.writeInt(Guild.getGuildPointTable().length);

               for (int exp : Guild.getGuildPointTable()) {
                  mplew.writeInt(exp);
               }

               return mplew.getPacket();
            }
         } else {
            return CWvsContext.enableActions(c);
         }
      }

      public static byte[] joinGuildRequest(int gid, int cid, Guild.JoinRequester r) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(-1);
         packet.write(78);
         packet.writeInt(gid);
         packet.writeInt(cid);
         packet.writeMapleAsciiString(r.getIntroduce());
         r.encode(packet);
         return packet.getPacket();
      }

      public static byte[] getAttendanceCheckResult(Guild guild, int playerID, int date) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(-1);
         packet.write(183);
         packet.writeInt(guild.getId());
         packet.writeInt(playerID);
         packet.writeInt(date);
         return packet.getPacket();
      }

      public static void getGuildInfo(PacketEncoder mplew, Guild guild) {
         mplew.writeInt(guild.getId());
         mplew.writeMapleAsciiString(guild.getName());

         for (int i = 1; i <= 10; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
            mplew.writeInt(guild.getRankPermission(i - 1));
         }

         guild.encodeMemberData(mplew);
         mplew.writeShort(guild.getJoinRequesters().size());

         for (Entry<Integer, Guild.JoinRequester> entry : guild.getJoinRequesters().entrySet()) {
            mplew.writeInt(entry.getKey());
         }

         for (Entry<Integer, Guild.JoinRequester> entry : guild.getJoinRequesters().entrySet()) {
            entry.getValue().encode(mplew);
         }

         mplew.writeInt(guild.getJoinRequesters().size());

         for (Guild.JoinRequester join : guild.getJoinRequesters().values()) {
            mplew.writeMapleAsciiString(join.getIntroduce());
         }

         mplew.writeInt(guild.getCapacity());
         mplew.writeShort(guild.getLogoBG());
         mplew.write(guild.getLogoBGColor());
         mplew.writeShort(guild.getLogo());
         mplew.write(guild.getLogoColor());
         mplew.writeMapleAsciiString(guild.getNotice());
         mplew.writeInt(guild.getHonorEXP());
         mplew.writeInt(guild.getHonorEXP());
         mplew.writeInt(guild.getAllianceId());
         mplew.write(guild.getLevel());
         mplew.writeInt(guild.getGP());
         mplew.writeInt(1000);
         mplew.writeInt(20210805);
         mplew.write(guild.isAllowJoinRequest());
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeInt(guild.getConnectTimeFlag());
         mplew.writeInt(guild.getActivityFlag());
         mplew.writeInt(guild.getAgeGroupFlag());
         mplew.writeShort(guild.getSkills().size());

         for (GuildSkill i : guild.getSkills()) {
            mplew.writeInt(i.skillID);
            mplew.writeShort(i.level);
            mplew.writeLong(PacketHelper.getTime(i.timestamp));
            mplew.writeMapleAsciiString(i.purchaser);
            mplew.writeMapleAsciiString(i.activator);
         }

         if (guild.getLogoColor() > 0) {
            mplew.write(-1);
            mplew.writeInt(0);
         } else {
            byte[] customEmblem = guild.getCustomEmblem();
            mplew.write(customEmblem == null ? 0 : 1);
            mplew.writeInt(customEmblem == null ? 0 : customEmblem.length);
            if (customEmblem != null && customEmblem.length > 0) {
               mplew.encodeBuffer(customEmblem);
            }
         }
      }

      public static byte[] newGuildInfo(MapleCharacter c) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(61);
         if (c != null && c.getMGC() != null) {
            Guild g = Center.Guild.getGuild(c.getGuildId());
            if (g == null) {
               return genericGuildMessage((byte)68);
            } else {
               getGuildInfo(mplew, g);
               return mplew.getPacket();
            }
         } else {
            return genericGuildMessage((byte)68);
         }
      }

      public static byte[] newGuildMember(GuildCharacter mgc) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(69);
         mplew.writeInt(mgc.getGuildId());
         mplew.writeInt(mgc.getId());
         mplew.writeInt(mgc.getGuildId());
         mplew.writeMapleAsciiString_(mgc.getName(), 13);
         mplew.writeInt(mgc.getJobId());
         mplew.writeInt(mgc.getLevel());
         mplew.writeInt(mgc.getGuildRank());
         mplew.writeInt(mgc.isOnline() ? 1 : 0);
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeInt(mgc.getAllianceRank());
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeInt(mgc.getLastAttendanceDate());
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeLong(PacketHelper.getTime(-2L));
         return mplew.getPacket();
      }

      public static byte[] editJoinSetting(int guildID, int playerID, boolean allowJoinRequest, int connectTimeFlag, int activityFlag, int ageGroupFlag) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(144);
         mplew.writeInt(guildID);
         mplew.writeInt(playerID);
         mplew.write(allowJoinRequest);
         mplew.writeInt(connectTimeFlag);
         mplew.writeInt(activityFlag);
         mplew.writeInt(ageGroupFlag);
         return mplew.getPacket();
      }

      public static byte[] memberLeft(GuildCharacter mgc, boolean bExpelled) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(bExpelled ? 91 : 87);
         mplew.writeInt(mgc.getGuildId());
         mplew.writeInt(mgc.getId());
         mplew.writeMapleAsciiString(mgc.getName());
         return mplew.getPacket();
      }

      public static byte[] guildDisband(int gid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(94);
         mplew.writeInt(gid);
         return mplew.getPacket();
      }

      public static byte[] guildCapacityChange(int gid, int capacity) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(111);
         mplew.writeInt(gid);
         mplew.write(capacity);
         return mplew.getPacket();
      }

      public static byte[] drawCustomGuildMark(Guild guild) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(-1);
         packet.write(193);
         packet.writeInt(guild.getId());
         packet.write(1);
         byte[] customEmblem = guild.getCustomEmblem();
         packet.writeInt(customEmblem == null ? 0 : customEmblem.length);
         if (customEmblem != null && customEmblem.length > 0) {
            packet.encodeBuffer(customEmblem);
         }

         packet.writeInt(1);
         return packet.getPacket();
      }

      public static byte[] guildContribution(int gid, int cid, int c) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(130);
         mplew.writeInt(gid);
         mplew.writeInt(cid);
         mplew.writeInt(c);
         mplew.writeInt(c);
         mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis() + 43200000L));
         return mplew.getPacket();
      }

      public static byte[] changeRank(GuildCharacter mgc) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(128);
         mplew.writeInt(mgc.getGuildId());
         mplew.writeInt(mgc.getId());
         mplew.write(mgc.getGuildRank());
         return mplew.getPacket();
      }

      public static byte[] changeRankTitleRole(int gid, int playerID, int index, String newName, String[] ranks, int[] permission) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(122);
         mplew.writeInt(gid);
         mplew.writeInt(playerID);

         for (int i = 0; i < 10; i++) {
            mplew.writeInt(permission[i]);
            mplew.writeMapleAsciiString(ranks[i]);
         }

         return mplew.getPacket();
      }

      public static byte[] addRankTitleRole(int gid, int playerID, int index, String newName, String[] ranks, int[] permission) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(124);
         mplew.writeInt(gid);
         mplew.writeInt(playerID);

         for (int i = 0; i < 10; i++) {
            mplew.writeInt(permission[i]);
            mplew.writeMapleAsciiString(ranks[i]);
         }

         return mplew.getPacket();
      }

      public static byte[] removeRankTitleRole(int gid, int playerID, int index, String newName, String[] ranks, int[] permission) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(126);
         mplew.writeInt(gid);
         mplew.writeInt(playerID);
         mplew.write(index);
         mplew.writeMapleAsciiString(newName);

         for (int i = 0; i < 10; i++) {
            mplew.writeInt(permission[i]);
            mplew.writeMapleAsciiString(ranks[i]);
         }

         return mplew.getPacket();
      }

      public static byte[] guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor, boolean isCustom, byte[] imageData, int playerID) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(132);
         mplew.writeInt(gid);
         mplew.writeInt(playerID);
         mplew.write(0);
         mplew.writeShort(bg);
         mplew.write(bgcolor);
         mplew.writeShort(logo);
         mplew.write(logocolor);
         mplew.write(0);
         mplew.writeInt(isCustom ? imageData.length : 0);
         if (isCustom) {
            mplew.encodeBuffer(imageData);
         }

         mplew.writeInt(isCustom ? 1 : 0);
         return mplew.getPacket();
      }

      public static byte[] updateGuildPoints(int gid, int GP, int honorEXP, int glevel) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(152);
         mplew.writeInt(gid);
         mplew.writeInt(honorEXP);
         mplew.writeInt(glevel);
         mplew.writeInt(GP);
         mplew.writeInt(GP);
         return mplew.getPacket();
      }

      public static byte[] guildNotice(int gid, int playerID, String notice) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(142);
         mplew.writeInt(gid);
         mplew.writeInt(playerID);
         mplew.writeMapleAsciiString(notice);
         return mplew.getPacket();
      }

      public static byte[] guildMemberLevelJobUpdate(GuildCharacter mgc) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(115);
         mplew.writeInt(mgc.getGuildId());
         mplew.writeInt(mgc.getId());
         mplew.writeInt(mgc.getLevel());
         mplew.writeInt(mgc.getJobId());
         return mplew.getPacket();
      }

      public static byte[] guildMemberOnline(int gid, int cid, boolean bOnline) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(116);
         mplew.writeInt(gid);
         mplew.writeInt(cid);
         mplew.write(bOnline ? 1 : 0);
         if (!bOnline) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
         }

         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] guildSkillPurchased(int gid, int sid, int level, long expiration, String purchase, String activate) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(160);
         mplew.writeInt(gid);
         mplew.writeInt(sid);
         mplew.writeInt(0);
         mplew.writeShort(level);
         mplew.writeLong(PacketHelper.getTime(expiration));
         mplew.writeMapleAsciiString(purchase);
         mplew.writeMapleAsciiString(activate);
         return mplew.getPacket();
      }

      public static byte[] guildLeaderChanged(int gid, int oldLeader, int newLeader, int allianceId) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(175);
         mplew.writeInt(gid);
         mplew.writeInt(oldLeader);
         mplew.writeInt(newLeader);
         mplew.write(0);
         mplew.write(1);
         mplew.writeInt(allianceId);
         return mplew.getPacket();
      }

      public static byte[] denyGuildInvitation(String charname) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(101);
         mplew.writeMapleAsciiString(charname);
         return mplew.getPacket();
      }

      public static byte[] useGuildSkill(int skillid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.write(163);
         mplew.writeInt(0);
         mplew.writeInt(skillid);
         return mplew.getPacket();
      }

      public static byte[] genericGuildMessage(byte code) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(-1);
         mplew.writeInt((int)code);
         if (code == 94 || code == 129) {
            mplew.writeInt(0);
         }

         if (code == 5 || code == 61 || code == 62 || code == 63 || code == 89 || code == 92) {
            mplew.writeMapleAsciiString("");
         }

         return mplew.getPacket();
      }
   }

   public static class InfoPacket {
      public static byte[] showMesoGain(long gain, boolean inChat) {
         return showMesoGain(gain, inChat, 0);
      }

      public static byte[] showMesoGain(long gain, boolean inChat, int changeMoney) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         if (!inChat) {
            mplew.write(0);
            mplew.write(MapleStatusInfo.GAINMESO.getType());
            mplew.writeShort(1);
            mplew.writeInt(gain);
            mplew.writeShort(changeMoney);
         } else {
            mplew.write(6);
            mplew.writeLong(gain);
            mplew.writeInt(-1);
         }

         return mplew.getPacket();
      }

      public static byte[] getShowInventoryStatus(int mode) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(0);
         mplew.write(0);
         mplew.write(mode);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getShowItemGain(int itemId, short quantity) {
         return getShowItemGain(itemId, quantity, false);
      }

      public static byte[] getShowItemGain(int itemId, int quantity, boolean inChat) {
         PacketEncoder mplew = new PacketEncoder();
         if (inChat) {
            mplew.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
            mplew.write(8);
            mplew.write(1);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
            mplew.write(0);
         } else {
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
            mplew.write(0);
         }

         return mplew.getPacket();
      }

      public static byte[] updateQuest(MapleQuestStatus quest) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(1);
         mplew.writeInt(quest.getQuest().getId());
         mplew.write(quest.getStatus());
         switch (quest.getStatus()) {
            case 0:
               mplew.write(0);
               break;
            case 1:
               mplew.writeMapleAsciiString(quest.getCustomData() != null ? quest.getCustomData() : "");
               break;
            case 2:
               mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
         }

         return mplew.getPacket();
      }

      public static byte[] updateQuestMobKills(MapleQuestStatus status) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.updateQuestMobKills.getType());
         mplew.writeInt(status.getQuest().getId());
         mplew.write(1);
         StringBuilder sb = new StringBuilder();

         for (int kills : status.getMobKills().values()) {
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
         }

         mplew.writeMapleAsciiString(sb.toString());
         mplew.writeLong(0L);
         return mplew.getPacket();
      }

      public static byte[] itemExpired(int itemid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.itemExpired.getType());
         mplew.writeInt(itemid);
         return mplew.getPacket();
      }

      public static byte[] gainExp(ExpIncreaseInfo info) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         packet.write(MapleStatusInfo.GainEXP.getType());
         info.decode(packet);
         return packet.getPacket();
      }

      public static byte[] getSPMsg(byte sp, short job) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.getSPMsg.getType());
         mplew.writeShort(job);
         mplew.write(sp);
         return mplew.getPacket();
      }

      public static byte[] getShowFameGain(int gain) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.ShowFameGain.getType());
         mplew.writeInt(gain);
         return mplew.getPacket();
      }

      public static byte[] getGPMsg(int itemid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.getGPMsg.getType());
         mplew.writeInt(itemid);
         return mplew.getPacket();
      }

      public static byte[] getCommitmentMsg(int iGP, int maxCommitment) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.getCommitment.getType());
         mplew.writeInt(iGP);
         mplew.writeInt(maxCommitment);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] getStatusMsg(int itemid) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.StatusMsg.getType());
         mplew.writeInt(itemid);
         return mplew.getPacket();
      }

      public static byte[] brownMessage(String message) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.brownMessage.getType());
         mplew.writeMapleAsciiString(message);
         return mplew.getPacket();
      }

      public static byte[] updateInfoQuest(int quest, String data) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.updateInfoQuest.getType());
         mplew.writeInt(quest);
         mplew.writeMapleAsciiString(data);
         return mplew.getPacket();
      }

      public static byte[] updatePandoraBox(String text) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.updateInfoQuest.getType());
         mplew.writeInt(14940);
         mplew.encodeBuffer(HexTool.getByteArrayFromHexString(text));
         return mplew.getPacket();
      }

      public static byte[] showItemReplaceMessage(List<String> message) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.showItemReplaceMessage.getType());
         mplew.write(message.size());

         for (String x : message) {
            mplew.writeMapleAsciiString(x);
         }

         return mplew.getPacket();
      }

      public static byte[] showTraitGain(MapleTrait.MapleTraitType trait, int amount) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.showTraitGain.getType());
         mplew.writeInt(trait.getStat().getValue());
         mplew.writeInt(amount);
         return mplew.getPacket();
      }

      public static byte[] showTraitMaxed(MapleTrait.MapleTraitType trait) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.showTraitMaxed.getType());
         mplew.writeLong(trait.getStat().getValue());
         return mplew.getPacket();
      }

      public static byte[] getBPMsg(int amount) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.getBPMsg.getType());
         mplew.writeInt(amount);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      public static byte[] showExpireMessage(byte type, List<Integer> item) {
         PacketEncoder mplew = new PacketEncoder(4 + item.size() * 4);
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(type);
         mplew.write(item.size());

         for (Integer it : item) {
            mplew.writeInt(it);
         }

         return mplew.getPacket();
      }

      public static byte[] showStatusMessage(int mode, String info, String data) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(mode);
         if (mode == 26) {
            mplew.writeMapleAsciiString(info);
            mplew.writeMapleAsciiString(data);
         }

         return mplew.getPacket();
      }

      public static byte[] showReturnStone(int act) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.showReturnStone.getType());
         mplew.write(act);
         return mplew.getPacket();
      }

      public static final byte[] comboKill(int skin, int combo, int monster, long exp) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.comboKill.getType());
         mplew.write(1);
         mplew.writeInt(combo);
         mplew.writeInt(monster);
         mplew.writeInt(skin);
         mplew.writeInt(combo);
         return mplew.getPacket();
      }

      public static byte[] multiKill(int skin, int count, long exp) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.multiKill.getType());
         mplew.write(0);
         mplew.writeLong(exp);
         mplew.writeInt(1);
         mplew.writeInt(count);
         mplew.writeInt(skin);
         return mplew.getPacket();
      }

      public static byte[] setMobCollectionOnFirst(int questID, String data) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.setMobCollectionOnFirst.getType());
         mplew.writeInt(questID);
         mplew.writeMapleAsciiString(data);
         return mplew.getPacket();
      }

      public static byte[] setMobOnCollection(int questID, String setNewString) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
         mplew.write(MapleStatusInfo.setMobOnCollection.getType());
         mplew.writeInt(questID);
         mplew.writeMapleAsciiString(setNewString.toString());
         return mplew.getPacket();
      }
   }

   public static class InventoryPacket {
      public static byte[] enableInventory() {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] addInventorySlot(MapleInventoryType type, Item item) {
         return addInventorySlot(type, item, false);
      }

      public static byte[] addInventorySlot(MapleInventoryType type, Item item, boolean exclusive) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(exclusive ? 1 : 0);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 10 : 0);
         mplew.write(type.getType());
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] addInventorySlot(MapleInventoryType type, Item item, boolean exclusive, MapleCharacter chr) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(exclusive ? 1 : 0);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 10 : 0);
         mplew.write(type.getType());
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item, chr);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] updateInventorySlot(MapleInventoryType type, Item item, boolean exclusive) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(exclusive ? 1 : 0);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 7 : 1);
         mplew.write(Math.max(1, type.getType()));
         mplew.writeShort(item.getPosition());
         mplew.writeShort(item.getQuantity());
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] updateInventorySlot(MapleInventoryType type, Item item, int shopPos) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 7 : 1);
         mplew.write(type.getType());
         mplew.writeShort(shopPos);
         mplew.writeShort(item.getPosition());
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] mergeArcaneSymbol(Item target, Item source) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(2);
         mplew.write(0);
         mplew.write(3);
         mplew.write(source.getType());
         mplew.writeShort(source.getPosition());
         mplew.write(1);
         mplew.write(target.getType());
         mplew.writeShort(source.getPosition());
         mplew.writeShort(1);
         mplew.write(16);
         return mplew.getPacket();
      }

      public static byte[] deleteItem(Item item) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(3);
         mplew.write(item.getPosition() < 0 ? -1 : 1);
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] updateEquipSlot(Item item) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(2);
         mplew.write(0);
         mplew.write(3);
         mplew.write(1);
         mplew.writeShort(item.getPosition());
         mplew.write(0);
         mplew.write(item.getType());
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, boolean bag, boolean bothBag) {
         return moveInventoryItem(type, src, dst, (short)-1, bag, bothBag, false);
      }

      public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator, boolean bag, boolean bothBag, boolean arcane) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(bag ? (bothBag ? 9 : 6) : 2);
         mplew.write(type.getType());
         mplew.writeShort(src);
         mplew.writeShort(dst);
         mplew.write(0);
         if (bag) {
            mplew.writeShort(0);
         }

         if (equipIndicator != -1) {
            mplew.write((int)equipIndicator);
         }

         return mplew.getPacket();
      }

      public static byte[] moveAndMergeInventoryItem(
         MapleInventoryType type, short src, short dst, short total, boolean bag, boolean switchSrcDst, boolean bothBag
      ) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(2);
         mplew.write(0);
         mplew.write(!bag || !switchSrcDst && !bothBag ? 3 : 8);
         mplew.write(type.getType());
         mplew.writeShort(src);
         mplew.write(!bag || switchSrcDst && !bothBag ? 1 : 7);
         mplew.write(type.getType());
         mplew.writeShort(dst);
         mplew.writeShort(total);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] moveAndMergeWithRestInventoryItem(
         MapleInventoryType type, short src, short dst, short srcQ, short dstQ, boolean bag, boolean switchSrcDst, boolean bothBag
      ) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(2);
         mplew.write(0);
         mplew.write(!bag || !switchSrcDst && !bothBag ? 1 : 7);
         mplew.write(type.getType());
         mplew.writeShort(src);
         mplew.writeShort(srcQ);
         mplew.write(!bag || switchSrcDst && !bothBag ? 1 : 7);
         mplew.write(type.getType());
         mplew.writeShort(dst);
         mplew.writeShort(dstQ);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(fromDrop ? 1 : 0);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(slot, type.getType()) ? 8 : 3);
         mplew.write(Math.max(1, type.getType()));
         mplew.writeShort(slot);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] updateInventoryItem(MapleInventoryType type, Item item, boolean exclusive, MapleCharacter chr) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(exclusive);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(5);
         mplew.write(type.getType());
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item, chr);
         return mplew.getPacket();
      }

      public static byte[] updateSpecialItemUse(Item item, byte invType, MapleCharacter chr, byte zerotype) {
         return updateSpecialItemUse(item, invType, false, chr, true, zerotype);
      }

      public static byte[] updateSpecialItemUse(Item item, byte invType, MapleCharacter chr) {
         return updateSpecialItemUse(item, invType, false, chr, false, (byte)0);
      }

      public static byte[] updateSpecialItemUse(Item item, byte invType, boolean theShort, MapleCharacter chr, boolean zeroweapon, byte zerotype) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(2);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(item.getPosition(), invType) ? 8 : 3);
         mplew.write(invType);
         mplew.writeShort(item.getPosition());
         mplew.write(0);
         mplew.write(invType);
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item, chr);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] updateArcaneSymbol(Item item) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(0);
         mplew.write(GameConstants.getInventoryType(item.getItemId()).getType());
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] updateSpecialItemUse_(Item item, byte invType, MapleCharacter chr) {
         return updateSpecialItemUse_(item, invType, item.getPosition(), chr);
      }

      public static byte[] updateSpecialItemUse_(Item item, byte invType, short pos, MapleCharacter chr) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(2);
         mplew.write(0);
         mplew.write(3);
         mplew.write(invType);
         mplew.writeShort(pos);
         mplew.write(0);
         mplew.write(invType);
         mplew.writeShort(item.getPosition());
         PacketHelper.addItemInfo(mplew, item, chr);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] scrolledItem(Item scroll, Item item, boolean destroyed, boolean potential, boolean equipped) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(true);
         mplew.write(0);
         mplew.writeInt(destroyed ? 2 : 3);
         mplew.write(0);
         MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
         if (type == MapleInventoryType.EQUIP && MapleItemInformationProvider.getInstance().isCash(item.getItemId())) {
            type = MapleInventoryType.CASH_EQUIP;
         }

         if (scroll == null) {
            mplew.write(1);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());
         } else if (scroll.getQuantity() > 0) {
            mplew.write(1);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
            mplew.writeShort(scroll.getQuantity());
         } else {
            mplew.write(3);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
         }

         if (!destroyed) {
            mplew.write(3);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.write(0);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            PacketHelper.addItemInfo(mplew, item);
         } else {
            mplew.write(3);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
         }

         if (!potential) {
            mplew.write(7);
         } else {
            mplew.write(11);
         }

         return mplew.getPacket();
      }

      public static byte[] updateScrollandItem(Item scroll, Item item, boolean destroyed) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(true);
         mplew.write(0);
         mplew.writeInt(3);
         mplew.write(0);
         if (scroll.getQuantity() > 0) {
            mplew.write(1);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
            mplew.writeShort(scroll.getQuantity());
         } else {
            mplew.write(3);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
         }

         if (!destroyed) {
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(GameConstants.getInventoryType(item.getItemId()).getType());
            mplew.writeShort(item.getPosition());
            PacketHelper.addItemInfo(mplew, item);
         } else {
            mplew.write(3);
            mplew.write(GameConstants.getInventoryType(item.getItemId()).getType());
            mplew.writeShort(item.getPosition());
         }

         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] moveAndUpgradeItem(MapleInventoryType type, Item item, short oldpos, short newpos, MapleCharacter chr) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(3);
         mplew.write(0);
         mplew.write(GameConstants.isInBag(newpos, type.getType()) ? 8 : 3);
         mplew.write(type.getType());
         mplew.writeShort(oldpos);
         mplew.write(0);
         MapleInventoryType t = GameConstants.getInventoryType(item.getItemId());
         if (t == MapleInventoryType.EQUIP && MapleItemInformationProvider.getInstance().isCash(item.getItemId())) {
            t = MapleInventoryType.CASH_EQUIP;
         }

         mplew.write(t.getType());
         mplew.writeShort(oldpos);
         PacketHelper.addItemInfo(mplew, item, chr);
         mplew.write(2);
         mplew.write(type.getType());
         mplew.writeShort(oldpos);
         mplew.writeShort(newpos);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] removeInventoryBag(MapleInventoryType type, int index) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(11);
         mplew.write(type.getType());
         mplew.writeShort(index);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] dropInventoryItem(MapleInventoryType type, short src) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(3);
         mplew.write(type.getType());
         mplew.writeShort(src);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] dropInventoryItemUpdate(MapleInventoryType type, Item item) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(1);
         mplew.write(0);
         mplew.write(1);
         mplew.write(type.getType());
         mplew.writeShort(item.getPosition());
         mplew.writeShort(item.getQuantity());
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] getInventoryFull() {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] getInventoryStatus() {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
         mplew.write(0);
         mplew.write(0);
         mplew.writeInt(0);
         mplew.write(0);
         return mplew.getPacket();
      }

      public static byte[] getSlotUpdate(byte invType, byte newSlots) {
         PacketEncoder mplew = new PacketEncoder();
         mplew.writeShort(SendPacketOpcode.INVENTORY_GROW.getValue());
         mplew.write(invType);
         mplew.write(newSlots);
         return mplew.getPacket();
      }

      public static byte[] getShowInventoryFull() {
         return CWvsContext.InfoPacket.getShowInventoryStatus(255);
      }

      public static byte[] showItemUnavailable() {
         return CWvsContext.InfoPacket.getShowInventoryStatus(254);
      }
   }
}
