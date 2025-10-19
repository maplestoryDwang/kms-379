package scripting;

import api.DonationRequest;
import constants.GameConstants;
import constants.JosaType;
import constants.Locales;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.script.Invocable;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import logging.entry.ConsumeLog;
import logging.entry.CustomLog;
import logging.entry.EnchantLog;
import network.SendPacketOpcode;
import network.center.Center;
import network.center.WeeklyItemEntry;
import network.center.WeeklyItemManager;
import network.center.praise.PraisePointRank;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.game.processors.HyperHandler;
import network.game.processors.InterServerHandler;
import network.game.processors.PlayersHandler;
import network.login.LoginInformationProvider;
import network.models.CField;
import network.models.CSPacket;
import network.models.CWvsContext;
import network.models.FontColorType;
import network.models.FontType;
import network.models.PacketHelper;
import network.models.StoragePacket;
import objects.context.expedition.ExpeditionType;
import objects.context.guild.Guild;
import objects.context.guild.GuildPacket;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.context.waitqueue.WaitQueue;
import objects.effect.child.TextEffect;
import objects.fields.Event_DojoAgent;
import objects.fields.Event_PyramidSubway;
import objects.fields.Field;
import objects.fields.MapleSquad;
import objects.fields.SpeedRunner;
import objects.fields.child.dojang.DojangMyRanking;
import objects.fields.child.dojang.DojangRanking;
import objects.fields.child.dreambreaker.Field_DreamBreaker;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleMonsterInformationProvider;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.MonsterDropEntry;
import objects.fields.gameobject.lifes.NpcTalk;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.item.MapleRing;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.MobQuest;
import objects.quest.WeeklyQuest;
import objects.shop.MapleShopFactory;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.PlayerStats;
import objects.users.PraisePoint;
import objects.users.enchant.BonusStat;
import objects.users.enchant.BonusStatPlaceType;
import objects.users.enchant.EquipEnchantMan;
import objects.users.enchant.EquipEnchantOption;
import objects.users.enchant.EquipEnchantScroll;
import objects.users.enchant.GradeRandomOption;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemOptionInfo;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.ItemUpgradeFlag;
import objects.users.enchant.ScrollType;
import objects.users.looks.zero.ZeroInfo;
import objects.users.potential.CharacterPotential;
import objects.users.potential.CharacterPotentialHolder;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.users.skills.VCore;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.utils.Timer;
import objects.utils.Triple;

public class NPCConversationManager extends AbstractPlayerInteraction {
   private String getText;
   String script;
   private byte type;
   private byte lastMsg = -1;
   public boolean pendingDisposal = false;
   private Invocable iv;
   public int[] bmWeapons = new int[]{
      1212128,
      1213021,
      1222121,
      1232121,
      1242138,
      1242140,
      1262050,
      1272039,
      1282039,
      1292021,
      1302354,
      1312212,
      1322263,
      1332288,
      1362148,
      1372236,
      1382273,
      1402267,
      1412188,
      1422196,
      1432226,
      1442284,
      1452265,
      1462251,
      1472274,
      1482231,
      1492244,
      1522151,
      1532156,
      1582043,
      1592021,
      1214021
   };

   public NPCConversationManager(MapleClient c, int npc, int questid, byte type, Invocable iv) {
      super(c, npc, questid);
      this.type = type;
      this.iv = iv;
   }

   public Invocable getIv() {
      return this.iv;
   }

   public int getNpc() {
      return this.id;
   }

   public int getQuest() {
      return this.id2;
   }

   public byte getType() {
      return this.type;
   }

   public void safeDispose() {
      this.pendingDisposal = true;
   }

   public void dispose() {
      NPCScriptManager.getInstance().dispose(this.c);
   }

   public void askMapSelection(String sel) {
      if (this.lastMsg <= -1) {
         this.c.getSession().writeAndFlush(CField.NPCPacket.getMapSelection(this.id, sel));
         this.lastMsg = 17;
      }
   }

   public void sendNext(String text) {
      this.sendNext(text, this.id);
   }

   public void sendNext(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)0, text, "00 01", (byte)0));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void askMenuScenarioSelf(String text, ScriptMessageFlag... flag) {
      int f = ScriptMessageFlag.BigScenario.getFlag() | ScriptMessageFlag.NpcReplacedByUser.getFlag();
      if (this.id == 2159361) {
         f = ScriptMessageFlag.Scenario.getFlag()
            | ScriptMessageFlag.NpcReplacedByUser.getFlag()
            | ScriptMessageFlag.Self.getFlag()
            | ScriptMessageFlag.FlipImage.getFlag();
      }

      for (ScriptMessageFlag var7 : flag) {
         ;
      }

      this.c.getPlayer().send(CField.NPCPacket.getScriptMessage(0, 0, GameObjectType.User, f, ScriptMessageType.AskMenu, 1, text, null));
   }

   public void askYesNo(String text, GameObjectType objectType, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      int templateID = this.getNpc();
      int replacedNpcTemplate = 0;
      if ((f & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
         replacedNpcTemplate = this.getNpc();
         templateID = 0;
      }

      this.c
         .getPlayer()
         .send(
            CField.NPCPacket.getScriptMessage(
               templateID,
               replacedNpcTemplate,
               objectType,
               f,
               ScriptMessageType.AskYesNo,
               (f & ScriptMessageFlag.Scenario.getFlag()) == 0 && (f & ScriptMessageFlag.BigScenario.getFlag()) == 0 ? 0 : 1,
               text,
               null
            )
         );
   }

   public void askAccept(String text, GameObjectType objectType, ScriptMessageFlag... flag) {
      this.askAccept(text, objectType, 0, flag);
   }

   public void askAccept(String text, GameObjectType objectType, int DLGColorType, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      int templateID = this.getNpc();
      int replacedNpcTemplate = 0;
      if ((f & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
         replacedNpcTemplate = this.getNpc();
         templateID = 0;
      }

      this.c
         .getPlayer()
         .send(CField.NPCPacket.getScriptMessage(templateID, replacedNpcTemplate, objectType, f, ScriptMessageType.AskAccept, DLGColorType, text, null));
   }

   public void sayNpc(String text, GameObjectType objectType, ScriptMessageFlag... flag) {
      this.sayNpc(text, objectType, false, true, 0, flag);
   }

   public void sayNpc(String text, GameObjectType objectType, boolean prev, boolean next, ScriptMessageFlag... flag) {
      this.sayNpc(text, objectType, prev, next, 0, flag);
   }

   public void sayNpc(String text, GameObjectType objectType, boolean prev, boolean next, int DLGColorType, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      int templateID = this.getNpc();
      int replacedNpcTemplate = 0;
      if ((f & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
         replacedNpcTemplate = this.getNpc();
         templateID = 0;
      }

      if (objectType == GameObjectType.User) {
         templateID = 0;
      }

      PacketEncoder addPacket = new PacketEncoder();
      addPacket.write(prev);
      addPacket.write(next);
      addPacket.writeInt(0);
      this.c
         .getPlayer()
         .send(CField.NPCPacket.getScriptMessage(templateID, replacedNpcTemplate, objectType, f, ScriptMessageType.Say, DLGColorType, text, addPacket));
   }

   public void sayReplacedNpc(String text, boolean prev, boolean next, int DLGColorType, int replacedNpc, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      f |= ScriptMessageFlag.NpcReplacedByNpc.getFlag();
      int templateID = this.getNpc();
      PacketEncoder addPacket = new PacketEncoder();
      addPacket.write(prev);
      addPacket.write(next);
      addPacket.writeInt(0);
      this.c
         .getPlayer()
         .send(CField.NPCPacket.getScriptMessage(templateID, replacedNpc, GameObjectType.Npc, f, ScriptMessageType.Say, DLGColorType, text, addPacket));
   }

   public void askAngelicBuster() {
      int templateID = this.getNpc();
      this.c.getPlayer().send(CField.NPCPacket.getScriptMessage(templateID, 0, GameObjectType.Npc, 0, ScriptMessageType.AskAngelicBuster, 0, "", null));
   }

   public void askMenu(String text, GameObjectType objectType, ScriptMessageFlag... flag) {
      this.askMenu(text, 0, objectType, flag);
   }

   public void askMenu(String text, int dlgColor, GameObjectType objectType, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      int templateID = this.getNpc();
      int replacedNpcTemplate = 0;
      if ((f & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
         replacedNpcTemplate = this.getNpc();
         templateID = 0;
      }

      this.c
         .getPlayer()
         .send(CField.NPCPacket.getScriptMessage(templateID, replacedNpcTemplate, objectType, f, ScriptMessageType.AskMenu, dlgColor, text, null));
   }

   public void sendNext(String text, int type, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)0, text, "00 01", (byte)type));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendPlayerToNpcS(String text) {
      this.sendNextS(text, (byte)3, this.id);
   }

   public void sendNextNoESC(String text) {
      this.sendNextS(text, (byte)1, this.id);
   }

   public void sendNextNoESC(String text, int id) {
      this.sendNextS(text, (byte)1, id);
   }

   public void sendNextSelf(String text) {
      this.sendNextS(text, (byte)16, this.id);
   }

   public void sendNextSelfNoESC(String text) {
      this.sendNextS(text, (byte)17, this.id);
   }

   public void sendNextS(String text, byte type) {
      this.sendNextS(text, type, this.id);
   }

   public void sendNextS(String text, byte type, int idd) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimpleS(text, type);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)0, text, "00 01", type, idd));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendPrev(String text) {
      this.sendPrev(text, this.id);
   }

   public void sendPrev(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)0, text, "01 00", (byte)0));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendPrevS(String text, byte type) {
      this.sendPrevS(text, type, this.id);
   }

   public void sendPrevS(String text, byte type, int idd) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimpleS(text, type);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)0, text, "01 00", type, idd));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendNextPrev(String text) {
      this.sendNextPrev(text, this.id);
   }

   public void sendNextPrev(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)0, text, "01 01", (byte)0));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void PlayerToNpc(String text) {
      this.sendNextPrevS(text, (byte)3);
   }

   public void sendNextPrevS(String text) {
      this.sendNextPrevS(text, (byte)3);
   }

   public void sendNextPrevS(String text, byte type) {
      this.sendNextPrevS(text, type, this.id);
   }

   public void sendNextPrevS(String text, byte type, int idd) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimpleS(text, type);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)0, text, "01 01", type, idd));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendOk(String text) {
      this.sendOk(text, this.id);
   }

   public void sendOk(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)0, text, "00 00", (byte)0));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendOkS(String text, byte type) {
      this.sendOkS(text, type, this.id);
   }

   public void sendOkS(String text, byte type, int idd) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimpleS(text, type);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)0, text, "00 00", type, idd));
            this.lastMsg = (byte)ScriptMessageType.Say.getType();
         }
      }
   }

   public void sendYesNo(String text) {
      this.sendYesNo(text, this.id);
   }

   public void sendYesNo(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)3, text, "", (byte)0));
            this.lastMsg = (byte)ScriptMessageType.AskYesNo.getType();
         }
      }
   }

   public void sendYesNoScenario(String text, int id) {
      if (this.lastMsg <= -1) {
         this.c.getSession().writeAndFlush(CField.NPCPacket.getYesNoScenario(id, 0, 3, text, ""));
         this.lastMsg = (byte)ScriptMessageType.AskYesNo.getType();
      }
   }

   public void sendYesNoS(String text, byte type) {
      this.sendYesNoS(text, type, this.id);
   }

   public void sendYesNoS(String text, byte type, int idd) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimpleS(text, type);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)3, text, "", type, idd));
            this.lastMsg = (byte)ScriptMessageType.AskYesNo.getType();
         }
      }
   }

   public void sendAcceptDecline(String text) {
      this.askAcceptDecline(text);
   }

   public void sendAcceptDeclineNoESC(String text) {
      this.askAcceptDeclineNoESC(text);
   }

   public void askAcceptDecline(String text) {
      this.askAcceptDecline(text, this.id);
   }

   public void askAcceptDecline(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.lastMsg = (byte)ScriptMessageType.AskAccept.getType();
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, this.lastMsg, text, "", (byte)0));
         }
      }
   }

   public void askAcceptDeclineNoESC(String text) {
      this.askAcceptDeclineNoESC(text, this.id);
   }

   public void askAcceptDeclineNoESC(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.lastMsg = (byte)ScriptMessageType.AskAccept.getType();
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, this.lastMsg, text, "", (byte)1));
         }
      }
   }

   public void sendStyle(String text, int... args) {
      this.askAvatar(text, args);
   }

   public void askCustomMixHairAndProb(String text) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkMixStyle(this.id, text, GameConstants.isAngelicBuster(this.c.getPlayer().getJob()), false));
      this.lastMsg = (byte)ScriptMessageType.AskCustomMixHairAndProb.getType();
   }

   public void askCustomMixHairAndProb(String text, int dressUp) {
      this.askCustomMixHairAndProb(text, dressUp, false);
   }

   public void askCustomMixHairAndProb(String text, int dressUp, boolean isBeta) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkMixStyle(this.id, text, dressUp == 1, isBeta));
      this.lastMsg = (byte)ScriptMessageType.AskCustomMixHairAndProb.getType();
   }

   public void askAvatar(String text, int... args) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyle(this.id, text, false, false, args));
      this.lastMsg = (byte)ScriptMessageType.AskAvatar.getType();
   }

   public void sendStyle(String text, int bAngelicBuster, int... args) {
      boolean zeroBeta = false;
      if (GameConstants.isZero(this.c.getPlayer().getJob())) {
         zeroBeta = this.c.getPlayer().getZeroInfo().isBeta();
      }

      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyle(this.id, text, bAngelicBuster == 1, zeroBeta, args));
      this.lastMsg = (byte)ScriptMessageType.AskAvatar.getType();
   }

   public void askAvatar(String text, boolean bAngelicBuster, int... args) {
      boolean zeroBeta = false;
      if (GameConstants.isZero(this.c.getPlayer().getJob())) {
         zeroBeta = this.c.getPlayer().getZeroInfo().isBeta();
      }

      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyle(this.id, text, bAngelicBuster, zeroBeta, args));
      this.lastMsg = (byte)ScriptMessageType.AskAvatar.getType();
   }

   public void askAvatar(String text, int[] args1, int[] args2) {
      if (this.lastMsg <= -1) {
         if (GameConstants.isZero(this.c.getPlayer().getJob())) {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyleZero(this.id, text, args1, args2));
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyle(this.id, text, false, false, args1));
         }

         this.lastMsg = (byte)ScriptMessageType.AskAvatar.getType();
      }
   }

   public void askAvatarAndroid(String text, int... args) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyleAndroid(this.id, text, args));
   }

   public void sendSimple(String text) {
      this.sendSimple(text, this.id);
   }

   public void sendSimple(String text, int id) {
      if (this.lastMsg <= -1) {
         if (!text.contains("#L")) {
            this.sendNext(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(id, (byte)6, text, "", (byte)0));
            this.lastMsg = (byte)ScriptMessageType.AskMenu.getType();
         }
      }
   }

   public void askMenuSelfNew(String text) {
      if (this.lastMsg <= -1) {
         if (!text.contains("#L")) {
            this.sendNext(text);
         } else {
            NpcTalk npctalk = new NpcTalk((byte)3, 0, false, (byte)6, (short)130, (byte)1);
            npctalk.text = text;
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(npctalk));
            this.lastMsg = (byte)ScriptMessageType.AskMenu.getType();
         }
      }
   }

   public void askMenuSelf(String text) {
      if (this.lastMsg <= -1) {
         if (!text.contains("#L")) {
            this.sendNext(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)6, text, "", (byte)16));
            this.lastMsg = (byte)ScriptMessageType.AskMenu.getType();
         }
      }
   }

   public void sendSimpleS(String text, byte type) {
      this.sendSimpleS(text, type, this.id);
   }

   public void sendSimpleS(String text, byte type, int idd) {
      if (this.lastMsg <= -1) {
         if (!text.contains("#L")) {
            this.sendNextS(text, type);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)6, text, "", type, idd));
            this.lastMsg = (byte)ScriptMessageType.AskMenu.getType();
         }
      }
   }

   public void sendStyle(String text, int[] styles1, int[] styles2) {
      if (this.lastMsg <= -1) {
         if (GameConstants.isZero(this.c.getPlayer().getJob())) {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyleZero(this.id, text, styles1, styles2));
            this.lastMsg = (byte)ScriptMessageType.AskAvatarZero.getType();
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyle(this.id, text, false, false, styles1));
            this.lastMsg = (byte)ScriptMessageType.AskAvatar.getType();
         }
      }
   }

   public void sendGetNumber(String text, int def, int min, int max) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkNum(this.id, text, def, min, max));
            this.lastMsg = (byte)ScriptMessageType.AskNumber.getType();
         }
      }
   }

   public void askNumber(String text, GameObjectType objectType, int def, int min, int max, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      int templateID = this.getNpc();
      int replacedNpcTemplate = 0;
      if ((f & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
         replacedNpcTemplate = this.getNpc();
         templateID = 0;
      }

      if (objectType == GameObjectType.User) {
         templateID = 0;
      }

      PacketEncoder addPacket = new PacketEncoder();
      addPacket.writeLong(def);
      addPacket.writeLong(min);
      addPacket.writeLong(max);
      addPacket.writeInt(0);
      this.c
         .getPlayer()
         .send(CField.NPCPacket.getScriptMessage(templateID, replacedNpcTemplate, objectType, f, ScriptMessageType.AskNumber, 0, text, addPacket));
   }

   public void sendGetText(String text) {
      this.sendGetText(text, this.id);
   }

   public void sendGetText(String text, int id) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkText(id, text));
            this.lastMsg = (byte)ScriptMessageType.AskText.getType();
         }
      }
   }

   public void sendGetTextFlag(String text, int id, ScriptMessageFlag... flag) {
      int f = 0;

      for (ScriptMessageFlag smf : flag) {
         f |= smf.getFlag();
      }

      int templateID = this.getNpc();
      int replacedNpcTemplate = 0;
      if ((f & ScriptMessageFlag.NpcReplacedByNpc.getFlag()) != 0) {
         replacedNpcTemplate = this.getNpc();
         templateID = 0;
      }

      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkTextFlag(replacedNpcTemplate, templateID, text, f));
            this.lastMsg = (byte)ScriptMessageType.AskText.getType();
         }
      }
   }

   public void setGetText(String text) {
      this.getText = text;
   }

   public String getText() {
      return this.getText;
   }

   public void setHair(int hair) {
      this.getPlayer().setHair(hair);
      this.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
      this.getPlayer().equipChanged();
   }

   public void setFace(int face) {
      this.getPlayer().setFace(face);
      this.getPlayer().updateSingleStat(MapleStat.FACE, face);
      this.getPlayer().equipChanged();
   }

   public void setSkin(int color) {
      this.getPlayer().setSkinColor((byte)color);
      this.getPlayer().updateSingleStat(MapleStat.SKIN, color);
      this.getPlayer().equipChanged();
   }

   public int setRandomAvatar(int ticket, int... args_all) {
      this.gainItem(ticket, (short)-1);
      int args = args_all[Randomizer.nextInt(args_all.length)];
      if (args < 100) {
         this.c.getPlayer().setSkinColor((byte)args);
         this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
      } else if (args < 30000) {
         this.c.getPlayer().setFace(args);
         this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
      } else {
         this.c.getPlayer().setHair(args);
         this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
      }

      this.c.getPlayer().equipChanged();
      return 1;
   }

   public int setZeroBetaAvatar(int ticket, int args) {
      this.gainItem(ticket, (short)-1);
      ZeroInfo zeroInfo = this.c.getPlayer().getZeroInfo();
      if (zeroInfo == null) {
         return 0;
      } else {
         if (args < 100) {
            zeroInfo.setSubSkin(args);
            this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
         } else if (args < 30000) {
            zeroInfo.setSubFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
         } else {
            zeroInfo.setMixBaseHairColor(-1);
            zeroInfo.setMixAddHairColor(0);
            zeroInfo.setMixHairBaseProb(0);
            zeroInfo.setSubHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
         }

         this.c.getPlayer().equipChanged();
         this.c.getPlayer().fakeRelog();
         return 1;
      }
   }

   public int setAvatar(int ticket, int args) {
      this.gainItem(ticket, (short)-1);
      if (args < 100) {
         this.c.getPlayer().setSkinColor((byte)args);
         this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
      } else if (args >= 30000 && (args <= 50000 || args >= 59999)) {
         this.c.getPlayer().setBaseColor(-1);
         this.c.getPlayer().setAddColor(0);
         this.c.getPlayer().setBaseProb(0);
         this.c.getPlayer().setHair(args);
         this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
      } else {
         this.c.getPlayer().setFaceBaseColor(0);
         this.c.getPlayer().setFaceAddColor(0);
         this.c.getPlayer().setFaceBaseProb(0);
         this.c.getPlayer().setFace(args);
         this.c.getPlayer().updateSingleStat(MapleStat.FACE, args);
      }

      this.c.getPlayer().equipChanged();
      return 1;
   }

   public int setZeroAvatar(int ticket, int args1, int args2) {
      this.gainItem(ticket, (short)-1);
      if (args1 < 100 || args1 < 100) {
         this.c.getPlayer().setSkinColor((byte)args1);
         this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args1);
         this.c.getPlayer().getZeroInfo().setSubSkin(args2);
         this.c.getPlayer().updateSingleStat(MapleStat.SKIN, args2);
      } else if (args1 >= 30000 && args2 >= 30000) {
         this.c.getPlayer().setHair(args1);
         this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args1);
         this.c.getPlayer().getZeroInfo().setSubHair(args2);
         this.c.getPlayer().updateSingleStat(MapleStat.HAIR, args2);
      } else {
         this.c.getPlayer().setFace(args1);
         this.c.getPlayer().updateSingleStat(MapleStat.FACE, args1);
         this.c.getPlayer().getZeroInfo().setSubFace(args2);
         this.c.getPlayer().updateSingleStat(MapleStat.FACE, args2);
      }

      this.c.getPlayer().equipChanged();
      this.c.getPlayer().fakeRelog();
      return 1;
   }

   public void setFaceAndroid(int faceId) {
      this.c.getPlayer().getAndroid().setFace(faceId);
      this.c.getPlayer().updateAndroid();
   }

   public void setHairAndroid(int hairId) {
      this.c.getPlayer().getAndroid().setHair(hairId);
      this.c.getPlayer().updateAndroid();
   }

   public void setSkinAndroid(int skin) {
      this.c.getPlayer().getAndroid().setSkin(skin);
      this.c.getPlayer().updateAndroid();
   }

   public void sendStorage() {
      this.c.getPlayer().setStorageNPC(this.id);
      this.c.getSession().writeAndFlush(StoragePacket.getStorage((byte)23));
   }

   public void openShop(int id) {
      MapleShopFactory.getInstance().getShop(id).sendShop(this.c);
   }

   public void openShopNPC(int id) {
      MapleShopFactory.getInstance().getShop(id).sendShop(this.c, this.id);
   }

   public int gainGachaponItem(int id, int quantity) {
      return this.gainGachaponItem(id, quantity, this.c.getPlayer().getMap().getStreetName());
   }

   public int gainGachaponItem(int id, int quantity, String msg) {
      try {
         if (!MapleItemInformationProvider.getInstance().itemExists(id)) {
            return -1;
         } else {
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short)quantity);
            if (item == null) {
               return -1;
            } else {
               byte rareness = GameConstants.gachaponRareItem(item.getItemId());
               if (rareness > 0) {
                  Center.Broadcast.broadcastMessage(CWvsContext.getGachaponMega(this.c.getPlayer().getName(), " : got a(n)", item, rareness, msg));
               }

               this.c.getSession().writeAndFlush(CWvsContext.InfoPacket.getShowItemGain(item.getItemId(), (short)quantity, true));
               return item.getItemId();
            }
         }
      } catch (Exception var6) {
         System.out.println("gainGachaponItem 함수 실행중 오류발생" + var6.toString());
         var6.printStackTrace();
         return -1;
      }
   }

   public void changeJob(int job) {
      this.c.getPlayer().changeJob(job, true);
   }

   public void startQuest(int idd) {
      MapleQuest.getInstance(idd).start(this.getPlayer(), this.id);
   }

   public void completeQuest(int idd) {
      MapleQuest.getInstance(idd).complete(this.getPlayer(), this.id);
   }

   public void forfeitQuest(int idd) {
      MapleQuest.getInstance(idd).forfeit(this.getPlayer());
   }

   public void forceStartQuest() {
      MapleQuest.getInstance(this.id2).forceStart(this.getPlayer(), this.getNpc(), null);
   }

   @Override
   public void forceStartQuest(int idd) {
      MapleQuest.getInstance(idd).forceStart(this.getPlayer(), this.getNpc(), null);
   }

   public void forceStartQuest(String customData) {
      MapleQuest.getInstance(this.id2).forceStart(this.getPlayer(), this.getNpc(), customData);
   }

   public void forceCompleteQuest() {
      MapleQuest.getInstance(this.id2).forceComplete(this.getPlayer(), this.getNpc());
   }

   @Override
   public void forceCompleteQuest(int idd) {
      MapleQuest.getInstance(idd).forceComplete(this.getPlayer(), this.getNpc());
   }

   public String getQuestCustomData() {
      return this.c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(this.id2)).getCustomData();
   }

   public void setQuestCustomData(String customData) {
      this.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(this.id2)).setCustomData(customData);
   }

   public long getMeso() {
      return this.getPlayer().getMeso();
   }

   public void gainAp(int amount) {
      this.c.getPlayer().gainAp((short)amount);
   }

   public void expandInventory(byte type, int amt) {
      this.c.getPlayer().expandInventory(type, amt);
   }

   public void unequipEverything() {
      MapleInventory equipped = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
      MapleInventory equip = this.getPlayer().getInventory(MapleInventoryType.EQUIP);
      List<Short> ids = new LinkedList<>();

      for (Item item : equipped.newList()) {
         ids.add(item.getPosition());
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (short id : ids) {
         MapleInventoryType type = MapleInventoryType.EQUIP;
         if (ii.isCash(id)) {
            type = MapleInventoryType.CASH_EQUIP;
         }

         MapleInventoryManipulator.unequip(type, this.getC(), id, equip.getNextFreeSlot());
      }
   }

   public final void clearSkills() {
      Map<Skill, SkillEntry> skills = new HashMap<>(this.getPlayer().getSkills());
      Map<Skill, SkillEntry> newList = new HashMap<>();

      for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         if (skill.getKey().getName().contains("라이딩")) {
            Integer id = skill.getKey().getId();
            newList.put(SkillFactory.getSkill(id), skill.getValue());
         } else {
            newList.put(skill.getKey(), new SkillEntry(0, (byte)0, -1L));
         }
      }

      this.getPlayer().changeSkillsLevel(newList);
      newList.clear();
      skills.clear();
   }

   public void autoSkillMaster() {
      for (int i = 0; i < this.getPlayer().getJob() % 10 + 1; i++) {
         int job = i + 1 == this.getPlayer().getJob() % 10 + 1
            ? this.getPlayer().getJob() - this.getPlayer().getJob() % 100
            : this.getPlayer().getJob() - (i + 1);
         if (this.getPlayer().getJob() >= 330 && this.c.getPlayer().getJob() <= 332) {
            if (job == 300) {
               job = 301;
            }
         } else if (this.getPlayer().getJob() >= 530 && this.getPlayer().getJob() <= 532 && job == 500) {
            job = 501;
         }

         if (GameConstants.isDemonAvenger(this.getPlayer().getJob()) && job == 3100) {
            job = 3101;
         }

         this.getPlayer().maxskill(job);
      }

      int div = this.getPlayer().getJob() < 1000 ? 100 : 1000;
      int jobx = this.getPlayer().getJob();
      if (GameConstants.isKain(jobx)) {
         div = 6003;
      } else if (GameConstants.isKadena(jobx)) {
         div = 6002;
      } else if (GameConstants.isAngelicBuster(jobx)) {
         div = 6001;
      } else if (GameConstants.isEvan(jobx)) {
         div = 2001;
      } else if (GameConstants.isMercedes(jobx)) {
         div = 2002;
      } else if (GameConstants.isDemonSlayer(jobx) || GameConstants.isDemonAvenger(jobx)) {
         div = 3001;
      } else if (GameConstants.isPhantom(jobx)) {
         div = 2003;
      } else if (GameConstants.isLuminous(jobx)) {
         div = 2004;
      } else if (GameConstants.isXenon(jobx)) {
         div = 3002;
      } else if (GameConstants.isEunWol(jobx)) {
         div = 2005;
      } else if (GameConstants.isIllium(jobx)) {
         div = 15000;
      } else if (GameConstants.isArk(jobx)) {
         div = 15001;
      } else if (GameConstants.isAdele(jobx)) {
         div = 15002;
      } else if (GameConstants.isKhali(jobx)) {
         div = 15003;
      } else if (GameConstants.isHoyoung(jobx)) {
         div = 16000;
      } else if (GameConstants.isLara(jobx)) {
         div = 16001;
      } else if (GameConstants.isCannon(jobx)) {
         div = 501;
      }

      this.getPlayer().maxskill(this.getPlayer().getJob() / div * div);
      this.getPlayer().resetSP(0);
      this.getPlayer().maxskill(this.getPlayer().getJob());
   }

   public boolean hasSkill(int skillid) {
      Skill theSkill = SkillFactory.getSkill(skillid);
      return theSkill != null ? this.c.getPlayer().getSkillLevel(theSkill) > 0 : false;
   }

   public void showEffect(boolean broadcast, String effect) {
      if (broadcast) {
         this.c.getPlayer().getMap().broadcastMessage(CField.showEffect(effect));
      } else {
         this.c.getSession().writeAndFlush(CField.showEffect(effect));
      }
   }

   public void playSound(boolean broadcast, String sound) {
      if (broadcast) {
         this.c.getPlayer().getMap().broadcastMessage(CField.playSound(sound));
      } else {
         this.c.getSession().writeAndFlush(CField.playSound(sound));
      }
   }

   public void environmentChange(boolean broadcast, String env) {
      if (broadcast) {
         this.c.getPlayer().getMap().broadcastMessage(CField.environmentChange(env, 2));
      } else {
         this.c.getSession().writeAndFlush(CField.environmentChange(env, 2));
      }
   }

   public void updateBuddyCapacity(int capacity) {
      this.c.getPlayer().setBuddyCapacity((byte)capacity);
   }

   public int getBuddyCapacity() {
      return this.c.getPlayer().getBuddyCapacity();
   }

   public int partyMembersInMap() {
      int inMap = 0;
      if (this.getPlayer().getParty() == null) {
         return inMap;
      } else {
         for (MapleCharacter char2 : this.getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() != null && char2.getParty().getId() == this.getPlayer().getParty().getId()) {
               inMap++;
            }
         }

         return inMap;
      }
   }

   public List<MapleCharacter> getPartyMembers() {
      return this.getPlayer().getPartyMembers();
   }

   public List<String> getAllSound() {
      return GameConstants.getBGMNames();
   }

   public void addMapMusic(int select) {
      this.getPlayer().getMap().addMusicList(GameConstants.getBGMPath(select));
   }

   public void addServerMapMusicServer(int select) {
      int mapId = this.getPlayer().getMapId();

      for (GameServer gs : GameServer.getAllInstances()) {
         if (gs.getMapFactory().getMap(mapId) != null) {
            gs.getMapFactory().getMap(mapId).addMusicList(GameConstants.getBGMPath(select));
         }
      }
   }

   public void addMapMusicStr(String bgmpath) {
      this.getPlayer().getMap().addMusicList(bgmpath);
   }

   public void addServerMapMusicStr(String bgmpath) {
      int mapId = this.getPlayer().getMapId();

      for (GameServer gs : GameServer.getAllInstances()) {
         if (gs.getMapFactory().getMap(mapId) != null) {
            gs.getMapFactory().getMap(mapId).addMusicList(bgmpath);
         }
      }
   }

   public List<String> getMapMusicList() {
      List<String> list = new ArrayList<>();

      for (String path : this.getPlayer().getMap().getMusicList()) {
         list.add(GameConstants.getBGMNameUsingPath(path));
      }

      return list;
   }

   public List<String> getMapMusicTimeList() {
      List<String> list = new ArrayList<>();

      for (String path : this.getPlayer().getMap().getMusicTimeList()) {
         list.add(path);
      }

      return list;
   }

   public void warpPartyWithExp(int mapId, int exp) {
      if (this.getPlayer().getParty() == null) {
         this.warp(mapId, 0);
         this.gainExp(exp);
      } else {
         Field target = this.getMap(mapId);

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMember().getPartyMemberList()) {
            MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if (curChar.getEventInstance() == null && this.getPlayer().getEventInstance() == null
               || curChar.getEventInstance() == this.getPlayer().getEventInstance()) {
               curChar.changeMap(target, target.getPortal(0));
               curChar.gainExp(exp, true, false, true);
            }
         }
      }
   }

   public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
      if (this.getPlayer().getParty() == null) {
         this.warp(mapId, 0);
         this.gainExp(exp);
         this.gainMeso(meso);
      } else {
         Field target = this.getMap(mapId);

         for (PartyMemberEntry chr : this.getPlayer().getParty().getPartyMember().getPartyMemberList()) {
            MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if (curChar.getEventInstance() == null && this.getPlayer().getEventInstance() == null
               || curChar.getEventInstance() == this.getPlayer().getEventInstance()) {
               curChar.changeMap(target, target.getPortal(0));
               curChar.gainExp(exp, true, false, true);
               curChar.gainMeso(meso, true);
            }
         }
      }
   }

   public MapleSquad getSquad(String type) {
      return this.c.getChannelServer().getMapleSquad(type);
   }

   public int getSquadAvailability(String type) {
      MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
      return squad == null ? -1 : squad.getStatus();
   }

   public boolean registerSquad(String type, int minutes, String startText) {
      if (this.c.getChannelServer().getMapleSquad(type) == null) {
         MapleSquad squad = new MapleSquad(this.c.getChannel(), type, this.c.getPlayer(), minutes * 60 * 1000, startText);
         boolean ret = this.c.getChannelServer().addMapleSquad(squad, type);
         if (ret) {
            Field map = this.c.getPlayer().getMap();
            map.broadcastMessage(CField.getClock(minutes * 60));
            map.broadcastMessage(CWvsContext.serverNotice(6, this.c.getPlayer().getName() + startText));
         } else {
            squad.clear();
         }

         return ret;
      } else {
         return false;
      }
   }

   public boolean getSquadList(String type, byte type_) {
      try {
         MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
         if (squad == null) {
            return false;
         } else {
            if (type_ == 0 || type_ == 3) {
               this.sendNext(squad.getSquadMemberString(type_));
            } else if (type_ == 1) {
               this.sendSimple(squad.getSquadMemberString(type_));
            } else if (type_ == 2) {
               if (squad.getBannedMemberSize() > 0) {
                  this.sendSimple(squad.getSquadMemberString(type_));
               } else {
                  this.sendNext(squad.getSquadMemberString(type_));
               }
            }

            return true;
         }
      } catch (NullPointerException var4) {
         return false;
      }
   }

   public byte isSquadLeader(String type) {
      MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
      if (squad == null) {
         return -1;
      } else {
         return (byte)(squad.getLeader() != null && squad.getLeader().getId() == this.c.getPlayer().getId() ? 1 : 0);
      }
   }

   public void giveAllStatItemwatk(int itemid, short stat, short watk) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
      item.addStr(stat);
      item.addDex(stat);
      item.addInt(stat);
      item.addLuk(stat);
      item.addWatk(watk);
      item.addMatk(watk);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void gainCashItem(int itemID) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.setUniqueId(MapleInventoryIdentifier.getInstance());
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void giveSupportItem(int itemid, short allStat, short attack, byte downLevel) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
      item.addStr(allStat);
      item.addDex(allStat);
      item.addInt(allStat);
      item.addLuk(allStat);
      item.addWatk(attack);
      item.addMatk(attack);
      item.setDownLevel(downLevel);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public void giveSupportItemPeriod(int itemid, short allStat, short attack, long period) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
      item.addStr(allStat);
      item.addDex(allStat);
      item.addInt(allStat);
      item.addLuk(allStat);
      item.addWatk(attack);
      item.addMatk(attack);
      item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public boolean reAdd(String eim, String squad) {
      EventInstanceManager eimz = this.getDisconnected(eim);
      MapleSquad squadz = this.getSquad(squad);
      if (eimz != null && squadz != null) {
         squadz.reAddMember(this.getPlayer());
         eimz.registerPlayer(this.getPlayer());
         return true;
      } else {
         return false;
      }
   }

   public void banMember(String type, int pos) {
      MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
      if (squad != null) {
         squad.banMember(pos);
      }
   }

   public void acceptMember(String type, int pos) {
      MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
      if (squad != null) {
         squad.acceptMember(pos);
      }
   }

   public int addMember(String type, boolean join) {
      try {
         MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
         return squad != null ? squad.addMember(this.c.getPlayer(), join) : -1;
      } catch (NullPointerException var4) {
         return -1;
      }
   }

   public byte isSquadMember(String type) {
      MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
      if (squad == null) {
         return -1;
      } else if (squad.getMembers().contains(this.c.getPlayer())) {
         return 1;
      } else {
         return (byte)(squad.isBanned(this.c.getPlayer()) ? 2 : 0);
      }
   }

   public void resetReactors() {
      this.getPlayer().getMap().resetReactors();
   }

   public void genericGuildMessage(int code) {
      this.c.getSession().writeAndFlush(CWvsContext.GuildPacket.genericGuildMessage((byte)code));
   }

   public void createGuild() {
      PacketEncoder packet = new PacketEncoder();
      GuildPacket.CreateGuildRequest request = new GuildPacket.CreateGuildRequest();
      request.encode(packet);
      this.c.getSession().writeAndFlush(packet.getPacket());
   }

   public void disbandGuild() {
      int gid = this.c.getPlayer().getGuildId();
      if (gid > 0 && this.c.getPlayer().getGuildRank() == 1) {
         Center.Guild.disbandGuild(gid);
      }
   }

   public void increaseGuildCapacity(boolean trueMax) {
      if (this.c.getPlayer().getMeso() < 500000L && !trueMax) {
         this.c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "You do not have enough mesos."));
      } else {
         int gid = this.c.getPlayer().getGuildId();
         if (gid > 0) {
            if (Center.Guild.increaseGuildCapacity(gid, trueMax)) {
               if (!trueMax) {
                  this.c.getPlayer().gainMeso(-500000L, true, true, true);
               } else {
                  this.gainGP(-25000);
               }
            } else if (!trueMax) {
               this.sendNext("자네의 길드 인원수는 더 이상 늘릴 수 없다네.");
            } else {
               this.sendNext(
                  "Please check if your guild capacity is full, if you have the GP needed or if subtracting GP would decrease a guild level. (Limit: 200)"
               );
            }
         }
      }
   }

   public boolean removePlayerFromInstance() {
      if (this.c.getPlayer().getEventInstance() != null) {
         this.c.getPlayer().getEventInstance().removePlayer(this.c.getPlayer());
         return true;
      } else {
         return false;
      }
   }

   public boolean isPlayerInstance() {
      return this.c.getPlayer().getEventInstance() != null;
   }

   public void changeStat(short slot, int type, int amount) {
      Equip sel = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
      switch (type) {
         case 0:
            sel.setStr((short)amount);
            break;
         case 1:
            sel.setDex((short)amount);
            break;
         case 2:
            sel.setInt((short)amount);
            break;
         case 3:
            sel.setLuk((short)amount);
            break;
         case 4:
            sel.setHp((short)amount);
            break;
         case 5:
            sel.setMp((short)amount);
            break;
         case 6:
            sel.setWatk((short)amount);
            break;
         case 7:
            sel.setMatk((short)amount);
            break;
         case 8:
            sel.setWdef((short)amount);
            break;
         case 9:
            sel.setMdef((short)amount);
            break;
         case 10:
            sel.setAcc((short)amount);
            break;
         case 11:
            sel.setAvoid((short)amount);
            break;
         case 12:
            sel.setHands((short)amount);
            break;
         case 13:
            sel.setSpeed((short)amount);
            break;
         case 14:
            sel.setJump((short)amount);
            break;
         case 15:
            sel.setUpgradeSlots((byte)amount);
            break;
         case 16:
            sel.setViciousHammer((byte)amount);
            break;
         case 17:
            sel.setLevel((byte)amount);
            break;
         case 18:
            sel.setEnhance((byte)amount);
            break;
         case 19:
            sel.setPotential1(amount);
            break;
         case 20:
            sel.setPotential2(amount);
            break;
         case 21:
            sel.setPotential3(amount);
            break;
         case 22:
            sel.setPotential4(amount);
            break;
         case 23:
            sel.setPotential5(amount);
            break;
         case 24:
            sel.setOwner(this.getText());
      }

      this.c.getPlayer().equipChanged();
      this.c.getPlayer().fakeRelog();
   }

   public void openDuey() {
      this.c.getPlayer().setConversation(2);
      this.c.getSession().writeAndFlush(CField.sendDuey((byte)9, null, null));
   }

   public void sendPVPWindow(int op) {
      this.c.getSession().writeAndFlush(CField.UIPacket.openUI(op));
      this.c.getSession().writeAndFlush(CField.sendPVPMaps());
   }

   public void sendRepairWindow() {
      this.c.getSession().writeAndFlush(CField.UIPacket.sendRepairWindow(this.id));
   }

   public void sendProfessionWindow() {
      this.c.getSession().writeAndFlush(CField.UIPacket.openUI(42));
   }

   public void openUI(int type) {
      this.c.getSession().writeAndFlush(CField.UIPacket.openUI(type));
   }

   public final int getDojoPoints() {
      return this.dojo_getPts();
   }

   public final int getDojoRecord() {
      return this.c.getPlayer().getIntNoRecord(150101);
   }

   public void setDojoRecord(boolean reset) {
      if (reset) {
         this.c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(150101)).setCustomData("0");
         this.c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(150100)).setCustomData("0");
      } else {
         this.c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(150101)).setCustomData(String.valueOf(this.c.getPlayer().getIntRecord(150101) + 1));
      }
   }

   public boolean start_DojoAgent(boolean dojo, boolean party) {
      return dojo ? Event_DojoAgent.warpStartDojo(this.c.getPlayer(), party) : Event_DojoAgent.warpStartAgent(this.c.getPlayer(), party);
   }

   public boolean start_PyramidSubway(int pyramid) {
      return pyramid >= 0 ? Event_PyramidSubway.warpStartPyramid(this.c.getPlayer(), pyramid) : Event_PyramidSubway.warpStartSubway(this.c.getPlayer());
   }

   public boolean bonus_PyramidSubway(int pyramid) {
      return pyramid >= 0 ? Event_PyramidSubway.warpBonusPyramid(this.c.getPlayer(), pyramid) : Event_PyramidSubway.warpBonusSubway(this.c.getPlayer());
   }

   public final short getKegs() {
      return this.c.getChannelServer().getFireWorks().getKegsPercentage();
   }

   public void giveKegs(int kegs) {
      this.c.getChannelServer().getFireWorks().giveKegs(this.c.getPlayer(), kegs);
   }

   public final short getSunshines() {
      return this.c.getChannelServer().getFireWorks().getSunsPercentage();
   }

   public void addSunshines(int kegs) {
      this.c.getChannelServer().getFireWorks().giveSuns(this.c.getPlayer(), kegs);
   }

   public final short getDecorations() {
      return this.c.getChannelServer().getFireWorks().getDecsPercentage();
   }

   public void addDecorations(int kegs) {
      try {
         this.c.getChannelServer().getFireWorks().giveDecs(this.c.getPlayer(), kegs);
      } catch (Exception var3) {
         System.out.println("Deco Err");
         var3.printStackTrace();
      }
   }

   public void maxStats() {
      Map<MapleStat, Long> statup = new EnumMap<>(MapleStat.class);
      this.c.getPlayer().getStat().str = 32767;
      this.c.getPlayer().getStat().dex = 32767;
      this.c.getPlayer().getStat().int_ = 32767;
      this.c.getPlayer().getStat().luk = 32767;
      int overrDemon = GameConstants.isDemonSlayer(this.c.getPlayer().getJob()) ? GameConstants.getMPByJob(this.c.getPlayer()) : 500000;
      this.c.getPlayer().getStat().maxhp = 500000L;
      this.c.getPlayer().getStat().maxmp = overrDemon;
      this.c.getPlayer().getStat().setHp(500000L, this.c.getPlayer());
      this.c.getPlayer().getStat().setMp(overrDemon, this.c.getPlayer());
      statup.put(MapleStat.STR, 32767L);
      statup.put(MapleStat.DEX, 32767L);
      statup.put(MapleStat.LUK, 32767L);
      statup.put(MapleStat.INT, 32767L);
      statup.put(MapleStat.HP, 500000L);
      statup.put(MapleStat.MAXHP, 500000L);
      statup.put(MapleStat.MP, (long)overrDemon);
      statup.put(MapleStat.MAXMP, (long)overrDemon);
      this.c.getPlayer().getStat().recalcLocalStats(this.c.getPlayer());
      this.c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statup, this.c.getPlayer()));
   }

   public Triple<String, Map<Integer, String>, Long> getSpeedRun(String typ) {
      ExpeditionType type = ExpeditionType.valueOf(typ);
      return SpeedRunner.getSpeedRunData(type) != null ? SpeedRunner.getSpeedRunData(type) : new Triple<>("", new HashMap<>(), 0L);
   }

   public boolean getSR(Triple<String, Map<Integer, String>, Long> ma, int sel) {
      if (ma.mid.get(sel) != null && ma.mid.get(sel).length() > 0) {
         this.sendOk(ma.mid.get(sel));
         return true;
      } else {
         this.dispose();
         return false;
      }
   }

   public String getAllItem() {
      StringBuilder string = new StringBuilder();

      for (Item item : this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).list()) {
         string.append("#L" + item.getUniqueId() + "##i " + item.getItemId() + "#\r\n");
      }

      return string.toString();
   }

   public Equip getEquip(int itemid) {
      return (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
   }

   public void setExpiration(Object statsSel, long expire) {
      if (statsSel instanceof Equip) {
         ((Equip)statsSel).setExpiration(System.currentTimeMillis() + expire * 24L * 60L * 60L * 1000L);
      }
   }

   public void setLock(Object statsSel) {
      if (statsSel instanceof Equip) {
         Equip eq = (Equip)statsSel;
         if (eq.getExpiration() == -1L) {
            eq.setFlag((byte)(eq.getFlag() | ItemFlag.PROTECTED.getValue()));
         } else {
            eq.setFlag((byte)(eq.getFlag() | ItemFlag.POSSIBLE_TRADING.getValue()));
         }
      }
   }

   public boolean addFromDrop(Object statsSel) {
      if (!(statsSel instanceof Item)) {
         return false;
      } else {
         Item it = (Item)statsSel;
         return MapleInventoryManipulator.checkSpace(this.getClient(), it.getItemId(), it.getQuantity(), it.getOwner())
            && MapleInventoryManipulator.addFromDrop(this.getClient(), it, false);
      }
   }

   public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
      return this.replaceItem(slot, invType, statsSel, offset, type, false);
   }

   public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
      MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
      if (inv == null) {
         return false;
      } else {
         Item item = this.getPlayer().getInventory(inv).getItem((short)slot);
         if (item == null || statsSel instanceof Item) {
            item = (Item)statsSel;
         }

         if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
               return false;
            }

            Equip eq = (Equip)item;
            if (takeSlot) {
               if (eq.getUpgradeSlots() < 1) {
                  return false;
               }

               eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() - 1));
               if (eq.getExpiration() == -1L) {
                  eq.setFlag((byte)(eq.getFlag() | ItemFlag.PROTECTED.getValue()));
               } else {
                  eq.setFlag((byte)(eq.getFlag() | ItemFlag.POSSIBLE_TRADING.getValue()));
               }
            }

            if (type.equalsIgnoreCase("Slots")) {
               eq.setUpgradeSlots((byte)(eq.getUpgradeSlots() + offset));
               eq.setViciousHammer((byte)(eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
               eq.setLevel((byte)(eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
               eq.setViciousHammer((byte)(eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
               eq.setStr((short)(eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
               eq.setDex((short)(eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
               eq.setInt((short)(eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
               eq.setLuk((short)(eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
               eq.setHp((short)(eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
               eq.setMp((short)(eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
               eq.setWatk((short)(eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
               eq.setMatk((short)(eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
               eq.setWdef((short)(eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
               eq.setMdef((short)(eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
               eq.setAcc((short)(eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
               eq.setAvoid((short)(eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
               eq.setHands((short)(eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
               eq.setSpeed((short)(eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
               eq.setJump((short)(eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
               eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
               eq.setExpiration(eq.getExpiration() + offset);
            } else if (type.equalsIgnoreCase("Flag")) {
               eq.setFlag((byte)(eq.getFlag() + offset));
            }

            item = eq.copy();
         }

         MapleInventoryManipulator.removeFromSlot(this.getClient(), inv, (short)slot, item.getQuantity(), false);
         return MapleInventoryManipulator.addFromDrop(this.getClient(), item, false);
      }
   }

   public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
      return this.replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
   }

   public boolean isCash(int itemId) {
      return MapleItemInformationProvider.getInstance().isCash(itemId);
   }

   public boolean isPet(int itemID) {
      return GameConstants.isPet(itemID);
   }

   public boolean isSpecialLabel(int itemID) {
      return MapleItemInformationProvider.getInstance().isSpecialLabel(itemID);
   }

   public int getTotalStat(int itemId) {
      return MapleItemInformationProvider.getInstance().getTotalStat((Equip)MapleItemInformationProvider.getInstance().getEquipById(itemId));
   }

   public int getReqLevel(int itemId) {
      return MapleItemInformationProvider.getInstance().getReqLevel(itemId);
   }

   public SecondaryStatEffect getEffect(int buff) {
      return MapleItemInformationProvider.getInstance().getItemEffect(buff);
   }

   public void buffGuild(int buff, int duration, String msg) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (ii.getItemEffect(buff) != null && this.getPlayer().getGuildId() > 0) {
         SecondaryStatEffect mse = ii.getItemEffect(buff);

         for (GameServer cserv : GameServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
               if (chr.getGuildId() == this.getPlayer().getGuildId()) {
                  mse.applyTo(chr, chr, true, null, duration, (byte)0, true, false);
                  chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
               }
            }
         }
      }
   }

   public boolean createAlliance(String alliancename) {
      Party pt = this.c.getPlayer().getParty();
      MapleCharacter otherChar = this.c.getChannelServer().getPlayerStorage().getCharacterById(pt.getPartyMember().getMemberByIndex(1).getId());
      if (otherChar != null && otherChar.getId() != this.c.getPlayer().getId()) {
         try {
            return Center.Alliance.createAlliance(
               alliancename, this.c.getPlayer().getId(), otherChar.getId(), this.c.getPlayer().getGuildId(), otherChar.getGuildId()
            );
         } catch (Exception var5) {
            System.out.println("createAlliance 함수 실행중 오류발생" + var5.toString());
            var5.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean addCapacityToAlliance() {
      try {
         Guild gs = Center.Guild.getGuild(this.c.getPlayer().getGuildId());
         if (gs != null
            && this.c.getPlayer().getGuildRank() == 1
            && this.c.getPlayer().getAllianceRank() == 1
            && Center.Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId()
            && Center.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
            this.gainMeso(-10000000L);
            return true;
         }
      } catch (Exception var2) {
         System.out.println("addCapacityToAlliance 함수 실행중 오류발생" + var2.toString());
         var2.printStackTrace();
      }

      return false;
   }

   public boolean disbandAlliance() {
      try {
         Guild gs = Center.Guild.getGuild(this.c.getPlayer().getGuildId());
         if (gs != null
            && this.c.getPlayer().getGuildRank() == 1
            && this.c.getPlayer().getAllianceRank() == 1
            && Center.Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId()
            && Center.Alliance.disbandAlliance(gs.getAllianceId())) {
            return true;
         }
      } catch (Exception var2) {
         System.out.println("disbandAlliance 함수 실행중 오류발생" + var2.toString());
         var2.printStackTrace();
      }

      return false;
   }

   public byte getLastMsg() {
      return this.lastMsg;
   }

   public final void setLastMsg(byte last) {
      this.lastMsg = last;
   }

   public final void maxAllSkills() {
      HashMap<Skill, SkillEntry> sa = new HashMap<>();

      for (Skill skil : SkillFactory.getAllSkills()) {
         if (GameConstants.isApplicableSkill(skil.getId()) && skil.getId() < 90000000) {
            sa.put(skil, new SkillEntry((byte)skil.getMaxLevel(), (byte)skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
         }
      }

      this.getPlayer().changeSkillsLevel(sa);
   }

   public final void maxSkillsByJob() {
      HashMap<Skill, SkillEntry> sa = new HashMap<>();

      for (Skill skil : SkillFactory.getAllSkills()) {
         if (GameConstants.isApplicableSkill(skil.getId()) && skil.canBeLearnedBy(this.getPlayer().getJob())) {
            sa.put(skil, new SkillEntry((byte)skil.getMaxLevel(), (byte)skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
         }
      }

      this.getPlayer().changeSkillsLevel(sa);
   }

   public final void resetStats(int str, int dex, int z, int luk) {
      this.c.getPlayer().resetStats(str, dex, z, luk);
   }

   public final boolean dropItem(int slot, int invType, int quantity) {
      MapleInventoryType inv = MapleInventoryType.getByType((byte)invType);
      return inv == null ? false : MapleInventoryManipulator.drop(this.c, inv, (short)slot, (short)quantity, true);
   }

   public final void sendRPS() {
      this.c.getSession().writeAndFlush(CField.getRPSMode((byte)8, -1, -1, -1));
   }

   public final void setQuestRecord(Object ch, int questid, String data) {
      ((MapleCharacter)ch).getQuestIfNullAdd(MapleQuest.getInstance(questid)).setCustomData(data);
   }

   public final void doWeddingEffect(Object ch) {
      final MapleCharacter chr = (MapleCharacter)ch;
      final MapleCharacter player = this.getPlayer();
      this.getMap()
         .broadcastMessage(
            CWvsContext.yellowChat(
               player.getName() + ", do you take " + chr.getName() + " as your wife and promise to stay beside her through all downtimes, crashes, and lags?"
            )
         );
      Timer.CloneTimer.getInstance()
         .schedule(
            new Runnable() {
               @Override
               public void run() {
                  if (chr != null && player != null) {
                     chr.getMap()
                        .broadcastMessage(
                           CWvsContext.yellowChat(
                              chr.getName()
                                 + ", do you take "
                                 + player.getName()
                                 + " as your husband and promise to stay beside him through all downtimes, crashes, and lags?"
                           )
                        );
                  } else {
                     NPCConversationManager.this.warpMap(680000500, 0);
                  }
               }
            },
            10000L
         );
      Timer.CloneTimer.getInstance()
         .schedule(
            new Runnable() {
               @Override
               public void run() {
                  if (chr != null && player != null) {
                     NPCConversationManager.this.setQuestRecord(player, 160001, "2");
                     NPCConversationManager.this.setQuestRecord(chr, 160001, "2");
                     NPCConversationManager.this.sendNPCText(
                        player.getName()
                           + " and "
                           + chr.getName()
                           + ", I wish you two all the best on your "
                           + chr.getClient().getChannelServer().getServerName()
                           + " journey together!",
                        9201002
                     );
                     chr.getMap().startExtendedMapEffect("You may now kiss the bride, " + player.getName() + "!", 5120006);
                     if (chr.getGuildId() > 0) {
                        Center.Guild.guildPacket(chr.getGuildId(), CWvsContext.sendMarriage(false, chr.getName()));
                     }

                     if (player.getGuildId() > 0) {
                        Center.Guild.guildPacket(player.getGuildId(), CWvsContext.sendMarriage(false, player.getName()));
                     }
                  } else {
                     if (player != null) {
                        NPCConversationManager.this.setQuestRecord(player, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(player, 160002, "0");
                     } else if (chr != null) {
                        NPCConversationManager.this.setQuestRecord(chr, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(chr, 160002, "0");
                     }

                     NPCConversationManager.this.warpMap(680000500, 0);
                  }
               }
            },
            20000L
         );
   }

   public void putKey(int index, int key, int type, int action) {
      this.getPlayer().changeKeybinding(index, key, (byte)type, action);
      this.getClient().getSession().writeAndFlush(CField.getKeymap(this.getPlayer().getKeyLayout(), this.getPlayer().getQuickSlotKeyMapped()));
   }

   public void logDonator(String log, int previous_points) {
      StringBuilder logg = new StringBuilder();
      logg.append(MapleCharacterUtil.makeMapleReadable(this.getPlayer().getName()));
      logg.append(" [CID: ").append(this.getPlayer().getId()).append("] ");
      logg.append(" [Account: ").append(MapleCharacterUtil.makeMapleReadable(this.getClient().getAccountName())).append("] ");
      logg.append(log);
      logg.append(" [Previous: " + previous_points + "] [Now: " + this.getPlayer().getPoints() + "]");
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("INSERT INTO donorlog VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)");
         ps.setString(1, MapleCharacterUtil.makeMapleReadable(this.getClient().getAccountName()));
         ps.setInt(2, this.getClient().getAccID());
         ps.setString(3, MapleCharacterUtil.makeMapleReadable(this.getPlayer().getName()));
         ps.setInt(4, this.getPlayer().getId());
         ps.setString(5, log);
         ps.setString(6, FileoutputUtil.CurrentReadable_Time());
         ps.setInt(7, previous_points);
         ps.setInt(8, this.getPlayer().getPoints());
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var10) {
         System.out.println("logDonator 함수 실행중 오류발생" + var10.toString());
         var10.printStackTrace();
      }

      FileoutputUtil.log("Log_Donator.rtf", logg.toString());
   }

   public void doRing(String name, int itemid) {
      PlayersHandler.DoRing(this.getClient(), name, itemid);
   }

   public int getNaturalStats(int itemid, String it) {
      Map<String, Integer> eqStats = MapleItemInformationProvider.getInstance().getEquipStats(itemid);
      return eqStats != null && eqStats.containsKey(it) ? eqStats.get(it) : 0;
   }

   public boolean isEligibleName(String t) {
      return MapleCharacterUtil.canCreateChar(t, this.getPlayer().isGM(), true)
         && (!LoginInformationProvider.getInstance().isForbiddenName(t) || this.getPlayer().isGM());
   }

   public String checkDrop(int mobId) {
      List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
      if (ranks != null && ranks.size() > 0) {
         int num = 0;
         int itemId = 0;
         int ch = 0;
         StringBuilder name = new StringBuilder();

         for (int i = 0; i < ranks.size(); i++) {
            MonsterDropEntry de = ranks.get(i);
            if (de.chance > 0 && (de.questid <= 0 || de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0)) {
               itemId = de.itemId;
               if (num == 0) {
                  name.append("Drops for #o" + mobId + "#\r\n");
                  name.append("--------------------------------------\r\n");
               }

               String namez = "#z" + itemId + "#";
               if (itemId == 0) {
                  itemId = 4031041;
                  namez = de.Minimum * this.getClient().getChannelServer().getMesoRate()
                     + " to "
                     + de.Maximum * this.getClient().getChannelServer().getMesoRate()
                     + " meso";
               }

               ch = (int)(de.chance * this.getClient().getChannelServer().getDropRate());
               name.append(
                  num
                     + 1
                     + ") #v"
                     + itemId
                     + "#"
                     + namez
                     + " - "
                     + Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0
                     + "% chance. "
                     + (
                        de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0
                           ? "Requires quest " + MapleQuest.getInstance(de.questid).getName() + " to be started."
                           : ""
                     )
                     + "\r\n"
               );
               num++;
            }
         }

         if (name.length() > 0) {
            return name.toString();
         }
      }

      return "No drops was returned.";
   }

   public String getLeftPadded(String in, char padchar, int length) {
      return StringUtil.getLeftPaddedStr(in, padchar, length);
   }

   public void handleDivorce() {
      if (this.getPlayer().getMarriageId() <= 0) {
         this.sendNext("Please make sure you have a marriage.");
      } else {
         int chz = Center.Find.findChannel(this.getPlayer().getMarriageId());
         if (chz == -1) {
            DBConnection db = new DBConnection();

            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("UPDATE queststatus SET customData = ? WHERE characterid = ? AND (quest = ? OR quest = ?)");
               ps.setString(1, "0");
               ps.setInt(2, this.getPlayer().getMarriageId());
               ps.setInt(3, 160001);
               ps.setInt(4, 160002);
               ps.executeUpdate();
               ps.close();
               ps = con.prepareStatement("UPDATE characters SET marriageid = ? WHERE id = ?");
               ps.setInt(1, 0);
               ps.setInt(2, this.getPlayer().getMarriageId());
               ps.executeUpdate();
               ps.close();
            } catch (SQLException var8) {
               this.outputFileError(var8);
               return;
            }

            this.setQuestRecord(this.getPlayer(), 160001, "0");
            this.setQuestRecord(this.getPlayer(), 160002, "0");
            this.getPlayer().setMarriageId(0);
            this.sendNext("You have been successfully divorced...");
         } else if (chz < -1) {
            this.sendNext("Please make sure your partner is logged on.");
         } else {
            MapleCharacter cPlayer = GameServer.getInstance(chz).getPlayerStorage().getCharacterById(this.getPlayer().getMarriageId());
            if (cPlayer != null) {
               cPlayer.dropMessage(1, "Your partner has divorced you.");
               cPlayer.setMarriageId(0);
               this.setQuestRecord(cPlayer, 160001, "0");
               this.setQuestRecord(this.getPlayer(), 160001, "0");
               this.setQuestRecord(cPlayer, 160002, "0");
               this.setQuestRecord(this.getPlayer(), 160002, "0");
               this.getPlayer().setMarriageId(0);
               this.sendNext("You have been successfully divorced...");
            } else {
               this.sendNext("An error occurred...");
            }
         }
      }
   }

   public String getReadableMillis(long startMillis, long endMillis) {
      return StringUtil.getReadableMillis(startMillis, endMillis);
   }

   public void sendUltimateExplorer() {
      this.getClient().getSession().writeAndFlush(CWvsContext.ultimateExplorer());
   }

   public void sendPendant(boolean b) {
      this.c.getSession().writeAndFlush(CWvsContext.pendantSlot(b));
   }

   public Triple<Integer, Integer, Integer> getCompensation() {
      Triple<Integer, Integer, Integer> ret = null;
      DBConnection db = new DBConnection();

      try {
         Triple var6;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM compensationlog_confirmed WHERE chrname LIKE ?");
            ps.setString(1, this.getPlayer().getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               ret = new Triple<>(rs.getInt("value"), rs.getInt("taken"), rs.getInt("donor"));
            }

            rs.close();
            ps.close();
            var6 = ret;
         }

         return var6;
      } catch (SQLException var9) {
         return ret;
      }
   }

   public boolean deleteCompensation(int taken) {
      DBConnection db = new DBConnection();

      try {
         boolean var5;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE compensationlog_confirmed SET taken = ? WHERE chrname LIKE ?");
            ps.setInt(1, taken);
            ps.setString(2, this.getPlayer().getName());
            ps.executeUpdate();
            ps.close();
            var5 = true;
         }

         return var5;
      } catch (SQLException var8) {
         return false;
      }
   }

   public void gainAPS(int gain) {
      this.getPlayer().gainAPS(gain);
   }

   public void setInnerStatsLine1() {
      CharacterPotentialHolder isvh = CharacterPotential.getInstance().renewSkill(this.c.getPlayer().getInnerSkills(), Collections.EMPTY_LIST, 0, false);
      this.c.getPlayer().getInnerSkills().add(isvh);
      this.c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), isvh.getSkillLevel(), isvh.getSkillLevel());
      this.c.getSession().writeAndFlush(CField.updateInnerPotential((byte)1, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank()));
   }

   public void setInnerStatsLine2() {
      CharacterPotentialHolder isvh = CharacterPotential.getInstance().renewSkill(this.c.getPlayer().getInnerSkills(), Collections.EMPTY_LIST, 0, false);
      this.c.getPlayer().getInnerSkills().add(isvh);
      this.c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), isvh.getSkillLevel(), isvh.getSkillLevel());
      this.c.getSession().writeAndFlush(CField.updateInnerPotential((byte)2, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank()));
   }

   public void setInnerStatsLine3() {
      CharacterPotentialHolder isvh = CharacterPotential.getInstance().renewSkill(this.c.getPlayer().getInnerSkills(), Collections.EMPTY_LIST, 0, false);
      this.c.getPlayer().getInnerSkills().add(isvh);
      this.c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), isvh.getSkillLevel(), isvh.getSkillLevel());
      this.c.getSession().writeAndFlush(CField.updateInnerPotential((byte)3, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank()));
   }

   public void openAuctionUI() {
      this.c.getSession().writeAndFlush(CField.UIPacket.openUI(161));
   }

   public void startCatch() {
      int MaxCatchSize = this.getMap().getCharactersSize() / 5 * 2;
      byte CatchSize = 0;
      String CatchingName = "";
      String CatchingName2 = "";
      Field map = GameServer.getInstance(this.getClient().getChannel()).getMapFactory().getMap(109090300);
      map.stopCatch();
      List<MapleCharacter> players = new ArrayList<>();
      players.addAll(this.getMap().getCharacters());
      Collections.addAll(players);
      Collections.shuffle(players);

      for (MapleCharacter chr : players) {
         chr.cancelAllBuffs();
         if (MaxCatchSize > CatchSize) {
            chr.isCatching = true;
            chr.isCatched = false;
            chr.changeMap(map, new Point(875, -453));
            CatchingName = CatchingName + chr.getName() + ",";
            CatchingName2 = CatchingName2 + chr.getName() + "\r\n";
            CatchSize++;
            chr.giveDebuff(SecondaryStatFlag.Stun, MobSkillFactory.getMobSkill(123, 1));
         } else {
            chr.isCatched = true;
            chr.isCatching = false;
            chr.changeMap(map, new Point(-592, -451));
         }

         chr.setWolfScore((byte)MaxCatchSize);
         chr.setSheepScore(CatchSize);
         map.broadcastMessage(CField.farmScore((byte)MaxCatchSize, CatchSize));
      }

      map.broadcastMessage(CWvsContext.serverNotice(1, "[술래 목록]\r\n" + CatchingName2));
      map.broadcastMessage(CWvsContext.serverNotice(6, "[술래 목록] " + CatchingName));
      map.startCatch();
   }

   public void gainSponserItem(int item, String name, short allstat, short damage, byte upgradeslot) {
      if (GameConstants.isEquip(item)) {
         Equip Item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(item);
         Item.setOwner(name);
         Item.setStr(allstat);
         Item.setDex(allstat);
         Item.setInt(allstat);
         Item.setLuk(allstat);
         Item.setWatk(damage);
         Item.setMatk(damage);
         Item.setUpgradeSlots(upgradeslot);
         MapleInventoryManipulator.addFromDrop(this.c, Item, false);
      } else {
         this.gainItem(item, allstat, damage);
      }
   }

   public void gainZeniaItemA(int item, String name, short allstat, short damage, byte allstatp, byte damagep, byte sponbossdr, byte sponidr) {
      if (GameConstants.isEquip(item)) {
         Equip Item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(item);
         Item.setOwner(name);
         Item.setStr((short)(Item.getStr() + allstat));
         Item.setDex((short)(Item.getDex() + allstat));
         Item.setInt((short)(Item.getInt() + allstat));
         Item.setLuk((short)(Item.getLuk() + allstat));
         Item.setWatk((short)(Item.getWatk() + damage));
         Item.setMatk((short)(Item.getMatk() + damage));
         Item.setAllStat((byte)(Item.getAllStat() + allstatp));
         Item.setTotalDamage((byte)(Item.getTotalDamage() + damagep));
         Item.setBossDamage((byte)(Item.getBossDamage() + sponbossdr));
         Item.setIgnorePDR((byte)(Item.getIgnorePDR() + sponidr));
         MapleInventoryManipulator.addFromDrop(this.c, Item, false);
      } else {
         this.gainItem(item, allstat, damage);
      }
   }

   public void gainZeniaItem(
      int item, String name, short allstat, short damage, byte allstatp, byte damagep, byte sponbossdr, byte sponidr, byte upgradeslot, int flag, int Potential
   ) {
      if (GameConstants.isEquip(item)) {
         Equip Item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(item);
         Item.setOwner(name);
         Item.setStr((short)(Item.getStr() + allstat));
         Item.setDex((short)(Item.getDex() + allstat));
         Item.setInt((short)(Item.getInt() + allstat));
         Item.setLuk((short)(Item.getLuk() + allstat));
         Item.setWatk((short)(Item.getWatk() + damage));
         Item.setMatk((short)(Item.getMatk() + damage));
         Item.setAllStat((byte)(Item.getAllStat() + allstatp));
         Item.setTotalDamage((byte)(Item.getTotalDamage() + damagep));
         Item.setBossDamage((byte)(Item.getBossDamage() + sponbossdr));
         Item.setIgnorePDR((byte)(Item.getIgnorePDR() + sponidr));
         Item.setPotential1(Potential);
         Item.setPotential2(Potential);
         Item.setPotential3(Potential);
         Item.setPotential4(Potential);
         Item.setPotential5(Potential);
         Item.setPotential6(Potential);
         Item.setFlag(flag);
         Item.setUpgradeSlots(upgradeslot);
         Item.setKarmaCount((byte)0);
         MapleInventoryManipulator.addFromDrop(this.c, Item, false);
      } else {
         this.gainItem(item, allstat, damage);
      }
   }

   public void askAvatar(String text, List<Integer> args) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalkStyle(this.id, text, false, args));
      this.lastMsg = 10;
   }

   public void SearchItem(String text, int type) {
      NPCConversationManager cm = this;
      if (text.getBytes().length < 4) {
         this.sendOk("검색어는 두글자 이상으로 해주세요.");
         this.dispose();
      } else if (!text.equals("헤어") && !text.equals("얼굴")) {
         String kk = "";
         String chat = "";
         String nchat = "";
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int i = 0;

         for (Pair<Integer, String> item : ii.getAllEquips()) {
            if (item.getRight().toLowerCase().contains(text.toLowerCase())) {
               String color = "#b";
               String isuse = "";
               if (cm.getPlayer().getCashWishList().contains(item.getLeft())) {
                  color = "#Cgray#";
                  isuse = " (선택된 항목)";
               }

               if (type == 1 && ii.isCash(item.getLeft()) && item.getLeft() >= 1000000 && item.getLeft() / 1000000 == 1) {
                  chat = chat + "\r\n" + color + "#L" + item.getLeft() + "##i" + item.getLeft() + " ##z" + item.getLeft() + "#" + isuse;
                  i++;
               } else if (type == 0 && item.getLeft() / 10000 >= 2 && item.getLeft() / 10000 < 3) {
                  chat = chat + "\r\n" + color + "#L" + item.getLeft() + "##i" + item.getLeft() + " ##z" + item.getLeft() + "#" + isuse;
                  i++;
               } else if (type == 2 && item.getLeft() / 10000 >= 3 && item.getLeft() / 10000 <= 5) {
                  chat = chat + "\r\n" + color + "#L" + item.getLeft() + "##i" + item.getLeft() + " ##z" + item.getLeft() + "#" + isuse;
                  i++;
               }
            }
         }

         if (i != 0) {
            kk = kk + "총 " + i + "개 검색되었습니다. 추가 하실 항목을 선택해주세요.";
            kk = kk + "\r\n#L0#항목 선택을 마칩니다.  \r\n#L1#항목을 재검색합니다.";
            nchat = kk + chat;
            cm.sendSimple(nchat);
         } else {
            kk = kk + "검색된 아이템이 없습니다.";
            cm.sendOk(kk);
            cm.dispose();
         }
      } else {
         this.sendOk("데이터량이 너무 많습니다. 다른 검색어를 이용해주세요.");
         this.dispose();
      }
   }

   public void sendPacket(String args) {
      this.c.getSession().writeAndFlush(PacketHelper.sendPacket(args));
   }

   public void enableMatrix() {
      MapleQuest quest = MapleQuest.getInstance(1465);
      MapleQuestStatus qs = this.c.getPlayer().getQuest(quest);
      if (quest != null && qs.getStatus() != 2) {
         qs.setStatus((byte)2);
         this.c.getPlayer().updateQuest(this.c.getPlayer().getQuest(quest), true);
      }
   }

   public void openCS() {
      InterServerHandler.EnterCS(this.c, this.c.getPlayer(), false);
   }

   public boolean isExistFH(int id) {
      return ServerConstants.cacheFaceHair.contains(String.valueOf(id));
   }

   public int getAndroidGender() {
      int itemid = this.c.getPlayer().getAndroid().getItemId();
      return MapleItemInformationProvider.getInstance().getAndroidBasicSettings(MapleItemInformationProvider.getInstance().getAndroid(itemid)).getGender();
   }

   public void setJaguar(int mobid) {
      MapleQuestStatus stats = this.c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(111112));
      stats.setCustomData(String.valueOf(mobid));
      this.c.getPlayer().updateQuest(stats, true);
      this.c.getSession().writeAndFlush(CWvsContext.updateJaguar(this.c.getPlayer()));
   }

   public void addEquip(short pos, int itemid) {
      MapleInventory equip = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
      boolean isExist = false;
      if (equip.getItem((short)-10) != null) {
         isExist = true;
      }

      Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
      int flag = eq.getFlag();
      if (ItemFlag.POSSIBLE_TRADING.check(flag)) {
         flag -= ItemFlag.POSSIBLE_TRADING.getValue();
      }

      eq.setFlag(flag);
      eq.setPosition(pos);
      eq.setState((byte)0);
      equip.addFromDB(eq.copy());
      this.getChar().fakeRelog();
   }

   public void increaseGuildCapacity() {
      if (this.c.getPlayer().getMeso() < 5000000L) {
         this.c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "500만 메소가 충분하지 않습니다."));
      } else {
         int gid = this.c.getPlayer().getGuildId();
         if (gid > 0) {
            Center.Guild.increaseGuildCapacity(gid, false);
            this.c.getPlayer().gainMeso(-5000000L, true, true, true);
         }
      }
   }

   public int getGuildId() {
      return this.c.getPlayer().getGuild() == null ? 0 : this.c.getPlayer().getGuildId();
   }

   public int getRC() {
      return this.c.getPlayer().getRealCash();
   }

   public void gainRC(int rc) {
      this.c.getPlayer().gainRealCash(rc);
   }

   public void setRC(int rc) {
      this.c.getPlayer().setRealCash(rc);
   }

   public void gainAddDamageSin(int value) {
      this.c.getPlayer().gainAddDamageSin(value);
   }

   public int getAddDamageSin() {
      return this.c.getPlayer().getAddDamageSin();
   }

   public void setAddDamageSin(int value) {
      this.c.getPlayer().setAddDamageSin(value);
   }

   public void gainAddDamage(long value) {
      this.c.getPlayer().gainAddDamage(value);
   }

   public long getAddDamage() {
      return this.c.getPlayer().getAddDamage();
   }

   public void setAddDamage(long value) {
      this.c.getPlayer().setAddDamage(value);
   }

   public int getHongboPoint() {
      return this.c.getPlayer().getHongboPoint();
   }

   public void gainHongboPoint(int value) {
      this.c.getPlayer().gainHongboPoint(value);
   }

   public void setHongboPoint(int value) {
      this.c.getPlayer().setHongboPoint(value);
   }

   public void setAllStat(int itemid, short stat, short watk, byte upgrade) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
      item.addStr(stat);
      item.addDex(stat);
      item.addInt(stat);
      item.addLuk(stat);
      item.addWatk(watk);
      item.addMatk(watk);
      item.setUpgradeSlots(upgrade);
      MapleInventoryManipulator.addbyItem(this.c, item);
   }

   public void setAllStat(int itemid, short stat, short watk) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemid);
      item.addStr(stat);
      item.addDex(stat);
      item.addInt(stat);
      item.addLuk(stat);
      item.addWatk(watk);
      item.addMatk(watk);
      MapleInventoryManipulator.addbyItem(this.c, item);
   }

   public int getTSD() {
      return this.c.getPlayer().getTSDPoint();
   }

   public void gainTSD(int value) {
      this.c.getPlayer().gainTSDPoint(value);
   }

   public void setTSD(int value) {
      this.c.getPlayer().setTSDPoint(value);
   }

   public int getTSDTotalPoint() {
      return this.c.getPlayer().getTSDTotalPoint();
   }

   public boolean CountCheck(String key, int value) {
      return this.c.getPlayer().CountCheck(key, value);
   }

   public int GetCount(String key) {
      return this.c.getPlayer().GetCount(key);
   }

   public void CountAdd(String key) {
      this.c.getPlayer().CountAdd(key);
   }

   public void setStat_s(int value, String key) {
      switch (key) {
         case "MP":
            long maxMP = Math.min(500000L, this.c.getPlayer().getStat().getMaxMp() + value);
            this.c.getPlayer().getStat().setMaxMp(maxMP, this.c.getPlayer());
            this.c.getPlayer().updateSingleStat(MapleStat.MAXMP, maxMP);
            break;
         case "HP":
            long maxHP = Math.min(500000L, this.c.getPlayer().getStat().getMaxHp() + value);
            this.c.getPlayer().getStat().setMaxHp(maxHP, this.c.getPlayer());
            this.c.getPlayer().updateSingleStat(MapleStat.MAXHP, maxHP);
      }
   }

   public void setClock(int clock) {
      this.getPlayer().getMap().broadcastMessage(CField.getClock(clock));
   }

   public void setClock(int hour, int min, int sec) {
      this.getPlayer().getMap().broadcastMessage(CField.getClockTime(hour, min, sec));
   }

   public void enableActions() {
      this.getPlayer().send(CWvsContext.enableActions(this.getPlayer()));
      this.getClient().removeClickedNPC();
   }

   public void temporaryStatSet(int skillID, int skillLevel) {
      SecondaryStatEffect effect = SkillFactory.getSkill(skillID).getEffect(skillLevel);
      if (effect != null) {
         effect.applyTo(this.getPlayer());
      }
   }

   public void temporaryStatReset(int skillID) {
      this.getPlayer().temporaryStatResetBySkillID(skillID);
   }

   public void temporaryStatSet(int skillID, int duration, String buffFlag, int value) {
      this.getPlayer().temporaryStatSet(skillID, duration, SecondaryStatFlag.valueOf(buffFlag), value);
   }

   public void resetAP() {
      PlayerStats playerst = this.c.getPlayer().getStat();
      Map statupdate = new EnumMap<>(MapleStat.class);
      playerst.setStr((short)4, this.c.getPlayer());
      statupdate.put(MapleStat.STR, 4);
      playerst.setDex((short)4, this.c.getPlayer());
      statupdate.put(MapleStat.DEX, 4);
      playerst.setInt((short)4, this.c.getPlayer());
      statupdate.put(MapleStat.INT, 4);
      playerst.setLuk((short)4, this.c.getPlayer());
      statupdate.put(MapleStat.LUK, 4);
      this.c.getSession().writeAndFlush(CWvsContext.updatePlayerStats(statupdate, true, this.c.getPlayer()));
   }

   public void showInfo(String msg) {
      this.c.getSession().writeAndFlush(CWvsContext.getStaticScreenMessage(msg, false, 1));
   }

   public void BuyPET(int itemID) {
      Item item = new Item(itemID, (short)1, (short)1, 0);
      item.setExpiration(System.currentTimeMillis() + 2592000000L);
      MaplePet pet = MaplePet.createPet(itemID, MapleInventoryIdentifier.getInstance());
      item.setPet(pet);
      item.setUniqueId(pet.getUniqueId());
      MapleInventoryManipulator.addFromDrop(this.getClient(), item, false);
   }

   public void chatMsg(int type, String msg) {
      this.c.getSession().writeAndFlush(CField.chatMsg(type, msg));
   }

   public boolean checkItem(int itemID) {
      int i = 0;
      Iterator iter = this.getPartyMembers().iterator();

      while (iter.hasNext()) {
         if (((MapleCharacter)iter.next()).getItemQuantity(itemID, false) > 0) {
            i++;
         }
      }

      return i == this.getPartyMembers().size();
   }

   public boolean checkLevel(int minLevel, int maxLevel) {
      int i = 0;
      Iterator iter = this.getPartyMembers().iterator();

      while (iter.hasNext()) {
         MapleCharacter player;
         if ((player = (MapleCharacter)iter.next()).getLevel() >= minLevel && player.getLevel() <= maxLevel) {
            i++;
         }
      }

      return i == this.getPartyMembers().size();
   }

   public void gainPartyItem(int itemID, short quantity) {
      Iterator iter = this.getPartyMembers().iterator();

      while (iter.hasNext()) {
         ((MapleCharacter)iter.next()).gainItem(itemID, quantity, false, 0L, "");
      }
   }

   public void forcePartyStartQuest(int questID) {
      for (MapleCharacter player : this.getPartyMembers()) {
         MapleQuest.getInstance(questID).forceStart(player, this.getNpc(), null);
      }
   }

   public final void forcePartyStartQuest(int questID, String customData) {
      for (MapleCharacter player : this.getPartyMembers()) {
         MapleQuest.getInstance(questID).forceStart(player, 0, customData);
      }
   }

   public void forcePartyStartQuest(String customData) {
      for (MapleCharacter player : this.getPartyMembers()) {
         MapleQuest.getInstance(0).forceStart(player, this.getNpc(), customData);
      }
   }

   public final void forcePartyCompleteQuest(int questID) {
      for (MapleCharacter player : this.getPartyMembers()) {
         MapleQuest.getInstance(questID).forceComplete(player, 0);
      }
   }

   public final int getSPGrade(short slot) {
      Item item = this.getInventory(1).getItem(slot);
      if (item == null) {
         return -1;
      } else {
         Equip equip = (Equip)item;
         return equip.getSPGrade();
      }
   }

   public final String getSPGradeString(short slot) {
      Item item = this.getInventory(1).getItem(slot);
      if (item == null) {
         return "";
      } else {
         Equip equip = (Equip)item;
         return equip.getOwner();
      }
   }

   public final int getEnchantPoint() {
      return this.getPlayer().getEnchantPoint();
   }

   public final void setEnchantPoint(int point) {
      this.getPlayer().setEnchantPoint(point);
   }

   public final void destroyEquip(Equip equip) {
      this.getPlayer().getInventory(MapleInventoryType.EQUIP).removeItem(equip.getPosition());
      this.getPlayer().send(CWvsContext.InventoryPacket.clearInventoryItem(MapleInventoryType.EQUIP, equip.getPosition(), false));
   }

   public final void enchantFallEquip(Equip equip, int allStat, int attack) {
      equip.setSPGrade(equip.getSPGrade() - 1);
      equip.setStr((short)(equip.getStr() - allStat));
      equip.setInt((short)(equip.getInt() - allStat));
      equip.setDex((short)(equip.getDex() - allStat));
      equip.setLuk((short)(equip.getLuk() - allStat));
      equip.setMatk((short)(equip.getMatk() - attack));
      equip.setWatk((short)(equip.getWatk() - attack));
      equip.setSPAllStat(equip.getSPAllStat() - allStat);
      equip.setSPAttack(equip.getSPAttack() - attack);
      equip.setOwner(equip.getSPGrade() + "성");
      this.c.getPlayer().send(CWvsContext.InventoryPacket.updateSpecialItemUse(equip, (byte)1, this.c.getPlayer(), (byte)0));
   }

   public final void enchantEquip_(Equip equip, int allStat, int attack) {
      equip.setSPGrade(equip.getSPGrade() + 1);
      equip.setSpecialPotential(1);
      if (DBConfig.isGanglim) {
         if (equip.getSPGrade() >= 10) {
            allStat = Randomizer.rand(30, 50);
            attack = Randomizer.rand(20, 30);
         } else {
            allStat = Randomizer.rand(15, 30);
            attack = Randomizer.rand(10, 15);
         }

         if (equip.getItemId() / 10000 == 105) {
            allStat *= 2;
            attack *= 2;
         }
      }

      equip.setStr((short)(equip.getStr() + allStat));
      equip.setInt((short)(equip.getInt() + allStat));
      equip.setDex((short)(equip.getDex() + allStat));
      equip.setLuk((short)(equip.getLuk() + allStat));
      equip.setWatk((short)(equip.getWatk() + attack));
      equip.setMatk((short)(equip.getMatk() + attack));
      equip.setSPAllStat(equip.getSPAllStat() + allStat);
      equip.setSPAttack(equip.getSPAttack() + attack);
      equip.setOwner(equip.getSPGrade() + "성");
      byte invType = 1;
      if (GameConstants.isZeroWeapon(equip.getItemId())) {
         this.c.getPlayer().forceReAddItem(equip, MapleInventoryType.EQUIPPED);
      } else {
         this.c.getPlayer().send(CWvsContext.InventoryPacket.updateSpecialItemUse(equip, invType, this.c.getPlayer(), (byte)0));
      }

      if (GameConstants.isZeroWeapon(equip.getItemId())) {
         Equip zeroEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
         zeroEquip.setSPGrade(zeroEquip.getSPGrade() + 1);
         zeroEquip.setSpecialPotential(1);
         zeroEquip.setStr((short)(zeroEquip.getStr() + allStat));
         zeroEquip.setInt((short)(zeroEquip.getInt() + allStat));
         zeroEquip.setDex((short)(zeroEquip.getDex() + allStat));
         zeroEquip.setLuk((short)(zeroEquip.getLuk() + allStat));
         zeroEquip.setWatk((short)(zeroEquip.getWatk() + attack));
         zeroEquip.setMatk((short)(zeroEquip.getMatk() + attack));
         zeroEquip.setSPAllStat(zeroEquip.getSPAllStat() + allStat);
         zeroEquip.setSPAttack(zeroEquip.getSPAttack() + attack);
         zeroEquip.setOwner(zeroEquip.getSPGrade() + "성");
         this.c.getPlayer().forceReAddItem(zeroEquip, MapleInventoryType.EQUIPPED);
      }
   }

   public final void enchantEquip2(Equip equip, int allStat, int attack) {
      equip.setCashEnchantCount(equip.getCashEnchantCount() + 1);
      equip.setStr((short)(equip.getStr() + allStat));
      equip.setInt((short)(equip.getInt() + allStat));
      equip.setDex((short)(equip.getDex() + allStat));
      equip.setLuk((short)(equip.getLuk() + allStat));
      equip.setWatk((short)(equip.getWatk() + attack));
      equip.setMatk((short)(equip.getMatk() + attack));
      this.c.getPlayer().send(CWvsContext.InventoryPacket.updateSpecialItemUse(equip, (byte)6, this.c.getPlayer(), (byte)0));
   }

   public final boolean renewSpecialPotential(short slot, boolean useEssence) {
      Item item = this.getInventory(1).getItem(slot);
      if (item == null) {
         return false;
      } else {
         Equip equip = (Equip)item;
         String[] gradeString = new String[]{"최하급", "하급", "중급", "상급", "최상급"};
         int[][] attack = new int[][]{{2, 4}, {3, 7}, {5, 11}, {8, 16}, {14, 21}};
         int[][] allStat = new int[][]{{4, 7}, {6, 11}, {10, 17}, {17, 25}, {25, 35}};
         int[][] attack2 = new int[][]{{4, 7}, {6, 11}, {10, 18}, {17, 25}, {24, 32}};
         int[][] allStat2 = new int[][]{{12, 16}, {15, 21}, {20, 28}, {31, 40}, {44, 55}};
         int grade = -1;
         int rand = Randomizer.rand(0, 100);
         int allStat_ = 0;
         int attack_ = 0;
         byte var14;
         if (useEssence) {
            if (rand <= 45) {
               var14 = 1;
            } else if (rand > 45 && rand <= 65) {
               var14 = 2;
            } else if (rand > 65 && rand <= 80) {
               var14 = 3;
            } else if (rand > 80 && rand <= 91) {
               var14 = 4;
            } else {
               var14 = 5;
            }

            allStat_ = Randomizer.rand(allStat2[var14 - 1][0], allStat2[var14 - 1][1]);
            attack_ = Randomizer.rand(attack2[var14 - 1][0], attack2[var14 - 1][1]);
         } else {
            if (rand <= 50) {
               var14 = 1;
            } else if (rand > 50 && rand <= 70) {
               var14 = 2;
            } else if (rand > 70 && rand <= 85) {
               var14 = 3;
            } else if (rand > 85 && rand <= 95) {
               var14 = 4;
            } else {
               var14 = 5;
            }

            allStat_ = Randomizer.rand(allStat[var14 - 1][0], allStat[var14 - 1][1]);
            attack_ = Randomizer.rand(attack[var14 - 1][0], attack[var14 - 1][1]);
         }

         equip.setSpecialPotential(1);
         equip.setStr((short)(equip.getStr() - equip.getSPAllStat() + allStat_));
         equip.setInt((short)(equip.getInt() - equip.getSPAllStat() + allStat_));
         equip.setDex((short)(equip.getDex() - equip.getSPAllStat() + allStat_));
         equip.setLuk((short)(equip.getLuk() - equip.getSPAllStat() + allStat_));
         equip.setMatk((short)(equip.getMatk() - equip.getSPAttack() + attack_));
         equip.setWatk((short)(equip.getWatk() - equip.getSPAttack() + attack_));
         equip.setSPGrade(var14);
         equip.setSPAllStat(allStat_);
         equip.setSPAttack(attack_);
         equip.setOwner("<" + gradeString[var14 - 1] + ">");
         this.c.getPlayer().send(CWvsContext.InventoryPacket.updateSpecialItemUse(equip, (byte)1, this.c.getPlayer(), (byte)0));
         if (this.c.getPlayer().isGM()) {
            this.c
               .getPlayer()
               .dropMessage(
                  5,
                  "무기 감정 결과 : 등급("
                     + gradeString[var14 - 1]
                     + ") / 올스탯 : "
                     + allStat_
                     + ", 공마 : "
                     + attack_
                     + ", equipAllStat : "
                     + equip.getAllStat()
                     + ", equipAttack : "
                     + equip.getMatk()
               );
         }

         return true;
      }
   }

   public final int gachaGMSCashItem() {
      int itemID = MapleItemInformationProvider.getInstance().getGMSCashItemGacha();
      if (!this.canHold(itemID, 1)) {
         return -1;
      } else {
         Item rewardItem = MapleItemInformationProvider.getInstance().getEquipById(itemID);
         Equip rewardEquip = null;
         if (rewardItem != null) {
            rewardEquip = (Equip)rewardItem;
         }

         if (rewardEquip != null) {
            rewardEquip.setUniqueId(MapleInventoryIdentifier.getInstance());
            short TI = MapleInventoryManipulator.addFromAuction(this.c, rewardEquip);
            if (TI == -1) {
               return -1;
            }

            this.c.getSession().writeAndFlush(CWvsContext.onCharacterModified(this.c.getPlayer(), 4L));
            HyperHandler.updateSkills(this.c.getPlayer(), 0);
            this.c.getPlayer().updateMatrixSkillsNoLock();
         }

         return itemID;
      }
   }

   public final void showGMSCashList() {
      String msg = "뽑기에서 나오는 아이템 목록이야!\r\n\r\n";
      boolean find = false;

      for (Integer itemID : MapleItemInformationProvider.getInstance().getGMSCashItemList()) {
         msg = msg + "#v" + itemID + "##t" + itemID + "#\r\n";
         if (!find) {
            find = true;
         }
      }

      if (find) {
         this.sendNext(msg);
         this.dispose();
      } else {
         this.sendNext("현재 해외 캐시템 물량이 없네.. 다음에 다시 찾아와줘!");
         this.dispose();
      }
   }

   public final boolean isGMSCashItem(int itemID) {
      boolean find = false;

      for (Integer id : MapleItemInformationProvider.getInstance().getGMSCashItemList()) {
         if (id == itemID) {
            find = true;
            break;
         }
      }

      return find;
   }

   public final int unboxingMapleRoyal() {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int rewardItemID = ii.getMasterPieceRewardItemID(this.c.getPlayer().isGM(), false, -1, this.getPlayer().getGender());
      Item rewardItem = ii.getEquipById(rewardItemID);
      Equip rewardEquip = (Equip)rewardItem;
      rewardEquip.setUniqueId(MapleInventoryIdentifier.getInstance());
      if (rewardEquip.isMasterLabel()) {
         rewardEquip.setCsGrade(3);
         int atk = Randomizer.rand(35, 45);
         rewardEquip.setCsOption1(21000 + atk);
         rewardEquip.setCsOption2(22000 + atk);
         int stat = Randomizer.rand(35, 45);
         rewardEquip.setCsOption3(10000 + Randomizer.rand(1, 4) * 1000 + stat);
         rewardEquip.setCsOptionExpireDate(System.currentTimeMillis() + 31536000000L);
         Center.Broadcast.broadcastGachaponMessage(
            this.c.getPlayer().getName() + "님이 " + ii.getName(5680157) + "에서 {" + ii.getName(rewardEquip.getItemId()) + "} 아이템을 획득하였습니다", 5068300, rewardEquip
         );
      } else {
         int rand = Randomizer.rand(0, 100);
         if (rand <= 8) {
            rewardEquip.setCsGrade(1);
            int atk = Randomizer.rand(10, 15);
            rewardEquip.setCsOption1(21000 + atk);
            rewardEquip.setCsOption2(22000 + atk);
            int stat = Randomizer.rand(10, 15);
            rewardEquip.setCsOption3(10000 + Randomizer.rand(1, 4) * 1000 + stat);
            rewardEquip.setCsOptionExpireDate(System.currentTimeMillis() + 31536000000L);
            rewardEquip.setItemState(ItemStateFlag.RED_LABEL_ITEM.getValue());
         }
      }

      rewardEquip.setSpecialRoyal(true);
      if (this.c.getPlayer().getKeyValue("tAble") != null) {
         this.c.getPlayer().setKeyValue("tAble", null);
         rewardEquip.setFlag((short)ItemFlag.KARMA_EQ.getValue());
      }

      short TI = MapleInventoryManipulator.addFromAuction(this.c, rewardEquip);
      if (TI == -1) {
         return -1;
      } else {
         this.c.getSession().writeAndFlush(CWvsContext.onCharacterModified(this.c.getPlayer(), -1L));
         HyperHandler.updateSkills(this.c.getPlayer(), 0);
         this.c.getPlayer().updateMatrixSkillsNoLock();
         return rewardEquip.getItemId();
      }
   }

   public final void makeRing(int itemID, String targetName) {
      try {
         MapleCharacter target = null;

         for (GameServer cs : GameServer.getAllInstances()) {
            target = cs.getPlayerStorage().getCharacterByName(targetName);
            if (target != null) {
               break;
            }
         }

         if (target == null) {
            return;
         }

         long[] ringID = MapleRing.makeRing(itemID, this.c.getPlayer(), target);
         Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID, ringID[1]);
         MapleRing ring = MapleRing.loadFromDb(ringID[1]);
         if (ring != null) {
            eq.setRing(ring);
         }

         MapleInventoryManipulator.addbyItem(this.c, eq);
         eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID, ringID[0]);
         ring = MapleRing.loadFromDb(ringID[0]);
         if (ring != null) {
            eq.setRing(ring);
         }

         MapleInventoryManipulator.addbyItem(target.getClient(), eq);
         target.fakeRelog();
         this.c.getPlayer().fakeRelog();
      } catch (Exception var7) {
         FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var7);
      }
   }

   public void StartBuzzingHouse() {
      this.c.getPlayer().startBuzzingHouseTask();
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            final MapleClient c = NPCConversationManager.this.getClient();
            c.getSession().writeAndFlush(CField.onUserTeleport(65535, 0));
            c.getSession().writeAndFlush(CField.UIPacket.setStandAloneMode(true));
            c.getSession().writeAndFlush(CField.UIPacket.IntroLock(true));
            c.getSession().writeAndFlush(CField.getStackEventGauge(c.getPlayer().getKillPoint() % 100, c.getPlayer().getKillPoint() / 100));
            Timer.MapTimer.getInstance().schedule(new Runnable() {
               @Override
               public void run() {
                  c.getSession().writeAndFlush(CField.getClock(300));
                  c.getSession().writeAndFlush(CField.getBuzzingHouseResult(1));
                  c.getSession().writeAndFlush(CField.getBuzzingHouseResult(2));
                  c.getSession().writeAndFlush(CField.getBuzzingHouseRequest(100, 10));
               }
            }, 3000L);
         }
      }, 1000L);
   }

   public void setMirrorDungeonInfo(boolean clear) {
      this.c.getSession().writeAndFlush(CField.UIPacket.OnSetMirrorDungeonInfo(clear));
   }

   public final int checkSymbolCount() {
      int src = this.c.getPlayer().getSymbolSrc();
      if (src > 0) {
         Equip source = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)src);
         if (source.getArcLevel() == 1 && source.getArcEXP() <= 0) {
            int itemID = source.getItemId();
            AtomicInteger count = new AtomicInteger(0);
            this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).list().forEach(item -> {
               Equip equip = (Equip)item;
               if (equip.getItemId() == itemID && equip.getArcEXP() == 0 && equip.getArcLevel() == 1) {
                  count.getAndAdd(1);
               }
            });
            return count.get();
         } else {
            return 1;
         }
      } else {
         return 1;
      }
   }

   public final void mergeSymbol() {
      int src = this.c.getPlayer().getSymbolSrc();
      if (src > 0) {
         Equip source = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)src);
         List<Short> removes = new LinkedList<>();

         for (Item item : this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).list()) {
            Equip srcE = (Equip)item;
            if (source.getItemId() == item.getItemId() && srcE.getArcLevel() == 1 && srcE.getArcEXP() == 0) {
               this.symbolExp(item.getPosition(), true);
               removes.add(item.getPosition());
               Equip target = null;
               if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1600) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1600).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1600);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1601) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1601).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1601);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1602) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1602).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1602);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1603) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1603).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1603);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1604) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1604).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1604);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1605) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1605).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1605);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1700) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1700).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1700);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1701) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1701).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1701);
               } else if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1702) != null
                  && source.getItemId() == this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1702).getItemId()) {
                  target = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1702);
               }

               if (target != null
                  && (
                     GameConstants.isAuthenticSymbol(target.getItemId())
                        ? GameConstants.getExpNeededForAuthenticSymbol(target.getArcLevel()) <= target.getArcEXP()
                        : GameConstants.getExpNeededForArcaneSymbol(target.getArcLevel()) <= target.getArcEXP()
                  )) {
                  break;
               }
            }
         }

         removes.forEach(pos -> this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeItem(pos));
      }
   }

   public final void symbolExp(int src, boolean multi) {
      try {
         Equip source = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)src);
         Equip target = null;
         if (source == null) {
            this.c.getSession().writeAndFlush(CWvsContext.enableActions(this.c.getPlayer()));
            return;
         }

         Item temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1600);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1601);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1602);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1603);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1604);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1605);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1700);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1701);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         temp = this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-1702);
         if (temp != null && source.getItemId() == temp.getItemId()) {
            target = (Equip)temp;
         }

         if (target == null) {
            this.c.getSession().writeAndFlush(CWvsContext.enableActions(this.c.getPlayer()));
            return;
         }

         if (GameConstants.isAuthenticSymbol(target.getItemId())) {
            if (target.getArcLevel() >= 11) {
               this.c.getPlayer().dropMessage(1, "더 이상 강화할 수 없습니다.");
               this.c.getSession().writeAndFlush(CWvsContext.enableActions(this.c.getPlayer()));
               return;
            }
         } else if (target.getArcLevel() >= 20) {
            this.c.getPlayer().dropMessage(1, "더 이상 강화할 수 없습니다.");
            this.c.getSession().writeAndFlush(CWvsContext.enableActions(this.c.getPlayer()));
            return;
         }

         this.c.getSession().writeAndFlush(CWvsContext.InventoryPacket.mergeArcaneSymbol(target, source));
         int exp = Math.min(target.getArcEXP() + source.getArcEXP() + 1, GameConstants.getExpNeededForArcaneSymbol(target.getArcLevel()));
         if (GameConstants.isAuthenticSymbol(target.getItemId())) {
            exp = Math.min(target.getArcEXP() + source.getArcEXP() + 1, GameConstants.getExpNeededForAuthenticSymbol(target.getArcLevel()));
         }

         target.setArcEXP(exp);
         if (!multi) {
            this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot((short)src);
            List<Item> removes = new ArrayList<>();

            for (Item item : this.c.getPlayer().getSymbols()) {
               if (item.getPosition() == src) {
                  removes.add(item);
               }
            }

            removes.stream().collect(Collectors.toList()).forEach(itemx -> this.c.getPlayer().getSymbols().remove(itemx));
         }

         this.c.getSession().writeAndFlush(CWvsContext.InventoryPacket.updateArcaneSymbol(target));
      } catch (Exception var10) {
         System.out.println("symbolExp 함수 실행중 오류발생" + var10.toString());
         var10.printStackTrace();
      }
   }

   public final int getMobCount(int questID, int mobID) {
      List<MobQuest> mobQuest = this.getPlayer().getMobQuest(questID);
      if (mobQuest == null) {
         return 0;
      } else {
         for (MobQuest q : mobQuest) {
            if (q.getMobID() == mobID) {
               return q.getMobCount();
            }
         }

         return 0;
      }
   }

   public final void startMobQuest(int questID, int needCount, int... mobID) {
      for (int mob : mobID) {
         this.getPlayer().registerMobQuest(questID, new MobQuest(questID, mob, needCount, 0));
      }
   }

   public final boolean canCompleteMobQuest(int questID) {
      return this.getPlayer().canCompleteMobQuest(questID);
   }

   public final boolean increaseStorageSlots(int slot) {
      if (this.getPlayer().getStorage().getSlots() + slot < 152) {
         this.getPlayer().getStorage().increaseSlots((byte)slot);
         this.getPlayer().dropMessage(1, "창고슬롯을 늘렸습니다. 현재 창고 슬롯은 " + this.getPlayer().getStorage().getSlots() + "칸 입니다.");
         return true;
      } else {
         this.getPlayer().dropMessage(1, "슬롯을 더 이상 늘릴 수 없습니다.");
         return false;
      }
   }

   public final boolean increasePendantSlots(int days) {
      MapleQuestStatus marr = this.c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122700));
      if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
         this.getPlayer().dropMessage(1, "이미 펜던트 늘리기가 적용중입니다.");
         return false;
      } else {
         this.getPlayer()
            .getQuestIfNullAdd(MapleQuest.getInstance(122700))
            .setCustomData(String.valueOf(System.currentTimeMillis() + days * 24L * 60L * 60000L));
         this.c.getSession().writeAndFlush(CSPacket.buyPendantSlot((short)30));
         return true;
      }
   }

   public final boolean increaseDamageSkinSlotCount(int slot) {
      if (this.getPlayer().getDamageSkinSaveInfo().getSlotCount() >= 60) {
         return false;
      } else {
         this.getPlayer().getDamageSkinSaveInfo().addSlotCount(slot);
         this.getPlayer().setSaveFlag(this.getPlayer().getSaveFlag() | CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag());
         this.getPlayer().updateOneInfo(13190, "slotCount", String.valueOf(this.getPlayer().getDamageSkinSaveInfo().getSlotCount()));
         return true;
      }
   }

   public final boolean setExGradeOption(int itemSlot, int lines, int level, int... source) {
      Item item = this.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short)itemSlot);
      if (item == null) {
         return false;
      } else {
         BonusStat.test(item, lines, level, source);
         this.c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(item, item, false, false, false));
         return true;
      }
   }

   public final boolean isMixHair() {
      return this.getPlayer().getBaseColor() != -1;
   }

   public final void resetMixHair() {
      this.getPlayer().setBaseColor(-1);
      this.getPlayer().setBaseProb(0);
      this.getPlayer().setAddColor(0);
   }

   public final String getMobName(int mobID) {
      MapleMonster mob = MapleLifeFactory.getMonster(mobID);
      return mob != null ? mob.getStats().getName() : "";
   }

   public final void startWeeklyQuest(int questID, int[][] quest, String msg) {
      this.c.getPlayer().removeWeeklyQuest(questID);

      for (int[] q : quest) {
         WeeklyQuest wq = new WeeklyQuest(questID, q[0], q[1], q[2], 0);
         this.c.getPlayer().addWeeklyQuest(wq);
      }

      this.c.getPlayer().send(CField.addPopupSay(9010060, 15000, "#e[익스트림 주간 퀘스트]#n\r\n\r\n" + msg + "\r\n퀘스트가 등록되었습니다.", ""));
   }

   public final void addPopupSay(String title, String message, int npc, int duration) {
      this.c.getPlayer().send(CField.addPopupSay(npc, duration, title, message));
   }

   public final boolean canCompleteWeeklyQuest(int questID) {
      return this.c.getPlayer().canCompleteWeeklyQuest(questID);
   }

   public final String displayWeeklyQuestStatus(int questID) {
      return this.c.getPlayer().displayWeeklyQuestStatus(questID);
   }

   public final void completeWeeklyQuest(int questID) {
      this.c.getPlayer().getWeeklyQuest().stream().filter(q -> q.getQuestID() == questID).forEach(q -> {
         if (q.getType() == 0) {
            this.c.getPlayer().gainItem(q.getNeedID(), (short)(-q.getNeedQuantity()), false, -1L, "");
         }
      });
   }

   public boolean isNickSkillTimeLimited(int itemID) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      return ii.getNickSkillTimeLimited(itemID) == 1;
   }

   public final void gainTteokgukPoint(int point) {
      this.c.getPlayer().setTteokgukPoint(this.c.getPlayer().getTteokgukPoint() + point);
   }

   public final int getTteokgukPoint() {
      return this.c.getPlayer().getTteokgukPoint();
   }

   public void sendUIJobChange() {
      if (this.c.getPlayer().getJob() / 1000 != 0) {
         this.c.getPlayer().dropMessage(1, "모험가 직업군만 자유전직이 가능합니다.");
      } else {
         for (VCore core : this.c.getPlayer().getVCoreSkillsNoLock()) {
            if (core.getState() == 2) {
               this.c.getPlayer().dropMessage(1, "V스킬 코어를 전부 장착해제 후 시도해주시기 바랍니다.");
               return;
            }
         }

         Equip test2 = null;
         if (this.c.getPlayer().getJob() / 100 == 4) {
            test2 = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
         }

         if (test2 == null) {
            this.c.getPlayer().send(CField.UIPacket.openUI(164));
         } else {
            this.c.getPlayer().dropMessage(1, "도적 직업군은 보조무기/방패/블레이드를 해제하셔야 합니다.");
         }
      }
   }

   public void setIngameDirectionMode(boolean blackFrame, boolean forceMouseOver, boolean showUI) {
      this.c.getSession().writeAndFlush(CField.UIPacket.setIngameDirectionMode(blackFrame, forceMouseOver, showUI));
   }

   public void setBlind(int a1, int a2, int a3, int a4, int a5, int a6, int a7) {
      this.c.getSession().writeAndFlush(CField.blind(a1, a2, a3, a4, a5, a6, a7));
   }

   public void cameraZoom(int time, int scale, int timePos, int endPosX, int endPosY) {
      this.c.getPlayer().send(CField.UIPacket.cameraZoom(time, scale, timePos, endPosX, endPosY));
   }

   public void cameraMove(int pixelPerSec, int destX, int destY) {
      this.c.getPlayer().send(CField.DirectionPacket.getCameraMove(pixelPerSec, destX, destY));
   }

   public void cameraMoveBack(int pixelPerSec, int unk) {
      this.c.getPlayer().send(CField.DirectionPacket.getCameraMoveBack(pixelPerSec, unk));
   }

   public void overlapDetail(int fadeIn, int wait, int fadeOut, int u) {
      this.c.getPlayer().send(CField.overlap_Detail(fadeIn, wait, fadeOut, u));
   }

   public void removeOverlapDetail(int fadeOut) {
      this.c.getPlayer().send(CField.remove_Overlap_Detail(fadeOut));
   }

   public void forcedMove(int direction, int fixel) {
      this.c.getPlayer().send(CField.DirectionPacket.getForcedMove(direction, fixel));
   }

   public void delay(int time) {
      this.c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(1, time));
   }

   public void effectPlay(String path, int duration, int x, int y, int a, int baseNPC, int notOrigin) {
      this.c.getSession().writeAndFlush(CField.UIPacket.getDirectionInfo(path, duration, x, y, a, baseNPC, notOrigin));
   }

   public void effectPlay(String path, int duration, int rx, int ry, int baseNPC, int notOrigin, boolean unk) {
      if (notOrigin > 0) {
         MapleNPC npc = this.getMap().getNPCById(notOrigin);
         if (npc != null) {
            notOrigin = npc.getObjectId();
         }
      }

      this.c.getPlayer().send(CField.DirectionPacket.getEffectPlay(path, duration, rx, ry, baseNPC, notOrigin, unk));
   }

   public void effectSound(String path) {
      this.c.getSession().writeAndFlush(CField.playSE(path));
   }

   public void spineEffect(String path, String animation, String unk1, int onoff, int loop, int postRender, int endDelay) {
      this.c.getSession().writeAndFlush(CField.EffectPacket.spineEffect(path, animation, 0, 0, 1, 0, "", unk1, 0, 0, 0));
   }

   public void environmentChange(int mode, String env, int option) {
      this.c.getSession().writeAndFlush(CField.environmentChange(env, mode, option));
   }

   public void getOnOffFade(int term, String key, int unk) {
      this.c.getSession().writeAndFlush(CField.getOnOffFade(term, key, unk));
   }

   public void getOnOff(int term, String key, String path, int rx, int ry, int rz, int org, int postRender, int unk1, int unk2) {
      this.c.getSession().writeAndFlush(CField.getOnOff(term, key, path, rx, ry, rz, org, postRender, unk1, unk2));
   }

   public void sendScenarioNpcNoESC(String message, int dlgColorType, int time) {
      this.c.getSession().writeAndFlush(CField.NPCPacket.getScenarioNpcNoESC(this.id, message, dlgColorType, time));
   }

   public void endIngameDirectionMode(int blackFrame) {
      this.c.getPlayer().send(CField.UIPacket.endInGameDirectionMode(blackFrame));
   }

   public void displayDreamBreakerRank() {
      this.c.getPlayer().send(Field_DreamBreaker.dreamBreakerRanking(this.c.getPlayer().getName()));
   }

   public boolean canBuyWeeklyItems() {
      return WeeklyItemManager.canBuyWeeklyItems();
   }

   public void tryUnboxingGoldenBox() {
      int rand = Randomizer.rand(0, 1000);
      List<WeeklyItemEntry> bonusItems = WeeklyItemManager.getBonusItems();
      List<WeeklyItemEntry> extremeItems = WeeklyItemManager.getExtremeItems();
      boolean broadcast = false;
      boolean f = false;
      int itemID = 0;
      int itemQuantity = 1;
      if (rand >= 250 && rand < 300 && bonusItems.size() > 0) {
         Collections.shuffle(bonusItems);

         for (WeeklyItemEntry e : bonusItems) {
            if (e.getRemainCount() > 0) {
               itemID = e.getItemID();
               itemQuantity = e.getItemQuantity();
               e.setRemainCount(e.getRemainCount() - 1);
               broadcast = true;
               break;
            }
         }

         f = true;
      }

      if (rand >= 370 && rand < 400 && extremeItems.size() > 0) {
         Collections.shuffle(extremeItems);

         for (WeeklyItemEntry ex : extremeItems) {
            if (ex.getRemainCount() > 0) {
               itemID = ex.getItemID();
               itemQuantity = ex.getItemQuantity();
               ex.setRemainCount(ex.getRemainCount() - 1);
               broadcast = true;
               break;
            }
         }

         f = true;
      }

      if (!f) {
         rand = Randomizer.rand(0, 100);
         if (DBConfig.isGanglim) {
            if (rand >= 0 && rand < 10) {
               itemID = 2439660;
               broadcast = true;
            } else if (rand >= 10 && rand < 45) {
               itemID = 2435890;
            } else if (rand >= 45 && rand < 50) {
               itemID = 2434556;
            } else if (rand >= 50 && rand < 60) {
               itemID = 2450155;
            } else if (rand >= 60 && rand < 80) {
               itemID = 2028263;
            } else if (rand >= 80 && rand < 86) {
               itemID = 2049372;
            } else if (rand >= 86 && rand < 95) {
               itemID = 2437093;
               broadcast = true;
            }
         } else if (rand >= 0 && rand < 10) {
            itemID = 2439660;
            broadcast = true;
         } else if (rand >= 10 && rand < 45) {
            itemID = 2435890;
         } else if (rand >= 45 && rand < 50) {
            itemID = 2049762;
         } else if (rand >= 50 && rand < 60) {
            itemID = 2450155;
         } else if (rand >= 60 && rand < 80) {
            itemID = 2450042;
         } else if (rand >= 80 && rand < 86) {
            itemID = 2049372;
         } else if (rand >= 86 && rand < 95) {
            itemID = 2437093;
            broadcast = true;
         }
      }

      StringBuilder sb = new StringBuilder(
         "페어리 브로의 황금상자 사용 (당첨 아이템 : "
            + (itemID == 0 ? "꽝" : MapleItemInformationProvider.getInstance().getName(itemID))
            + ", 닉네임 : "
            + this.getPlayer().getName()
            + ")"
      );
      LoggingManager.putLog(new ConsumeLog(this.c.getPlayer(), 1, sb));
      String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
      this.getPlayer().dropMessage(5, "4,000 " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 잃었습니다.");
      if (itemID == 0) {
         this.sendSimple("아쉽게도 #b페어리 브로의 황금 상자#k에서 아무런 아이템도 획득하지 못했어요.. 다음에 더 좋은 결과가 있길 바랄게요 !\r\n\r\n#L0#한 번 더 개봉하겠습니다.#l");
      } else {
         this.getPlayer().gainItem(itemID, itemQuantity, false, 0L, "페어리 브로의 황금상자에서 얻은 아이템");
         this.sendSimple("축하합니다! #b페어리 브로의 황금 상자#k에서 #b#i" + itemID + "##z" + itemID + "# " + itemQuantity + "개#k를 획득하였습니다!!\r\n\r\n#L0#한 번 더 개봉하겠습니다.#l");
         if (broadcast) {
            Item item = new Item(itemID, (short)1, (short)itemQuantity, 0);
            if (itemID / 1000000 == 1) {
               Center.Broadcast.broadcastGachaponMessage(
                  this.getPlayer().getName() + "님이 페어리 브로의 황금상자에서 " + MapleItemInformationProvider.getInstance().getName(itemID) + "을(를) 획득하였습니다.",
                  5060048,
                  item
               );
            } else {
               Center.Broadcast.broadcastGachaponMessage(this.getPlayer().getName() + "님이 페어리 브로의 황금상자에서 {" + itemID + "}을(를) 획득하였습니다.", 5060048, item);
            }
         }
      }
   }

   public void displayWeeklyItems() {
      String text = "안녕하세요? 저는 #e페어리 브로의 황금상자#n를 담당하는 #b요정 웡키#k라고 합니다. 페어리 브로의 황금상자에서는 #b한정 수량 아이템#k #e#r<에픽 아이템>#k#n과 #e#r<레전드리 아이템>#k#n 그리고 #b노멀 항목 아이템#k이 출현해요. 해당 상품들은 #e매주 토요일 오후 9시, 일요일 오후 3시#n에 보급된답니다.\r\n단, 모든 한정 수량 아이템이 재고가 없을때는 다음 보급까지 이용할 수 없답니다.\r\n\r\n";
      if (DBConfig.isGanglim) {
         text = "안녕하세요? 저는 #e페어리 브로의 황금상자#n를 담당하는 #b요정 웡키#k라고 합니다. 페어리 브로의 황금상자에서는 #b한정 수량 아이템#k #e#r<유니크 아이템>#k#n과 #e#r<강림 아이템>#k#n 그리고 #b에픽 아이템#k이 출현해요. 해당 상품들은 #e매주 토요일 오후 2시, 일요일 오후 6시#n에 보급된답니다.\r\n단, 모든 한정 수량 아이템의 재고가 떨어지면 다음 보급까지 이용할 수 없답니다.\r\n\r\n";
      }

      int size = WeeklyItemManager.getBonusItems().size() + WeeklyItemManager.getExtremeItems().size();
      if (size == 0) {
         if (DBConfig.isGanglim) {
            text = text + "현재 물품 보급 대기중이에요. 토요일은 오후 2시, 일요일은 오후 6시에 보급되니 조금만 기다려주세요!";
         } else {
            text = text + "현재 물품 보급 대기중이에요. 토요일은 오후 9시, 일요일은 오후 3시에 보급되니 조금만 기다려주세요!";
         }
      } else {
         text = text + "현재 남은 상품은 아래와 같아요!\r\n\r\n";
         if (DBConfig.isGanglim) {
            text = text + "#e<유니크 아이템>#n\r\n";
         } else {
            text = text + "#e<에픽 아이템>#n\r\n";
         }

         for (WeeklyItemEntry entry : WeeklyItemManager.getBonusItems()) {
            text = text + "  #b#i" + entry.getItemID() + "##z" + entry.getItemID() + "# " + entry.getItemQuantity() + "개#k - #e재고";
            if (entry.getRemainCount() == 0) {
               text = text + " 없음";
            } else {
               text = text + " " + entry.getRemainCount() + "개 남음";
            }

            text = text + "#n\r\n";
         }

         if (DBConfig.isGanglim) {
            text = text + "\r\n#e<강림 아이템>#n\r\n";
         } else {
            text = text + "\r\n#e<레전드리 아이템>#n\r\n";
         }

         for (WeeklyItemEntry entry : WeeklyItemManager.getExtremeItems()) {
            text = text + "  #b#i" + entry.getItemID() + "##z" + entry.getItemID() + "# " + entry.getItemQuantity() + "개#k - #e재고";
            if (entry.getRemainCount() == 0) {
               text = text + " 없음";
            } else {
               text = text + " " + entry.getRemainCount() + "개 남음";
            }

            text = text + "#n\r\n";
         }

         if (WeeklyItemManager.canBuyWeeklyItems()) {
            text = text + "\r\n#L0##b#e페어리 브로의 황금상자#n를 개봉해보고 싶어요! #e#k(4,000 " + (DBConfig.isGanglim ? "후원 캐시" : "강림 포인트") + ")#n#l\r\n";
         } else {
            text = text + "\r\n#b현재 모든 한정 수량 아이템이 매진되었습니다.";
         }
      }

      text = text + "\r\n#L1##b다음에 이용하도록 하겠습니다.#l\r\n";
      this.sendSimple(text);
   }

   public void showDojangRanking() {
      if (!this.canEnterDojang()) {
         this.sendNext("#e#b토요일 23시 40분#k#n부터 #e#b일요일 00시 04분#k#n까지 #b랭킹 집계#k를 위해 지금은 랭킹을 볼 수 없어. #b#e00시 05#n#k분 이후에 다시 시도해줘.");
         this.dispose();
      } else {
         this.c.getPlayer().send(CField.getMulungDojangRanking(this.c.getPlayer()));
      }
   }

   public void tryGetDojangPoint() {
      int pointGet = this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_point_get");
      if (pointGet <= 0) {
         int type = 2;
         if (this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_reward_c") > 0) {
            type = 0;
         }

         DojangMyRanking ranking = DojangRanking.getLastWeekMyRank(type, this.c.getPlayer().getName());
         if (ranking != null && ranking.getRank() != 0) {
            int percent = DojangRanking.getLastWeekPercentage(type, this.c.getPlayer().getName());
            int point = 0;
            if (percent >= 76) {
               if (type == 2) {
                  point = 3000;
               } else {
                  point = 8000;
               }
            } else if (percent >= 51 && percent <= 75) {
               if (type == 2) {
                  point = 4500;
               } else {
                  point = 11000;
               }
            } else if (percent >= 41 && percent <= 50) {
               if (type == 2) {
                  point = 5000;
               } else {
                  point = 12000;
               }
            } else if (percent >= 31 && percent <= 40) {
               if (type == 2) {
                  point = 5500;
               } else {
                  point = 13000;
               }
            } else if (percent >= 21 && percent <= 30) {
               if (type == 2) {
                  point = 6000;
               } else {
                  point = 14000;
               }
            } else if (percent >= 11 && percent <= 20) {
               if (type == 2) {
                  point = 6500;
               } else {
                  point = 15000;
               }
            } else if (percent >= 6 && percent <= 10) {
               if (type == 2) {
                  point = 7000;
               } else {
                  point = 16000;
               }
            } else if (percent >= 0 && percent <= 5) {
               if (type == 2) {
                  point = 8000;
               } else {
                  point = 18000;
               }
            }

            if (type == 0) {
               point *= 2;
            }

            this.c.getPlayer().updateOneInfo(1234590, "dojang_point_get", "1");
            this.c.getPlayer().dropMessage(5, point + "포인트를 획득하였습니다.");
            this.c
               .getPlayer()
               .updateOneInfo(42003, "point", String.valueOf(Math.min(500000, this.c.getPlayer().getOneInfoQuestInteger(42003, "point") + point)));
            String text = "너는 지난주 무릉도장";
            if (type == 0) {
               text = text + "(챌린지)";
            }

            text = text + " 상위 " + percent + "%를 달성하여 " + point + "포인트를 지급해줬어.\r\n다음엔 더 좋은 결과 있기를 바란다구.";
            this.sendNext(text);
            this.dispose();
         } else {
            Pair<Integer, Integer> pair = DojangRanking.getLastTryDojang(type, this.c.getPlayer().getName());
            String v0 = "뭐야. 너는 지난주 무릉도장에 도전한 기록이 없는데?\r\n뭔가 착각한 거 아냐?\r\n\r\n";
            v0 = v0 + "너는 마지막으로...어디 보자...\r\n";
            if (pair == null) {
               v0 = v0 + "무릉도장에 도전한 기록이 없군.";
            } else {
               Calendar cal = Calendar.getInstance();
               cal.set(1, pair.left);
               cal.set(3, pair.right);
               cal.set(7, 1);
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
               String v1 = sdf.format(cal.getTime());
               cal = Calendar.getInstance();
               cal.set(1, pair.left);
               cal.set(3, pair.right);
               cal.set(7, 7);
               String v2 = sdf.format(cal.getTime());
               v0 = v0 + "#e#r" + v1 + " ~ " + v2 + "#n#k 사이에\r\n무릉도장에 도전했군.";
            }

            this.sendNext(v0);
            this.dispose();
         }
      } else {
         this.sendNext("너는 이미 무릉도장 포인트를 정산 받았잖아?\r\n정산은 한 번만 받을 수 있다고.");
         this.dispose();
      }
   }

   public void displayDojangResult() {
      String v0 = "뭐, 고생 많았어. 계속 도전해보라고.\r\n";
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd, hh시 mm분");
      v0 = v0 + "(현재 시간:" + sdf.format(cal.getTime()) + ")\r\n\r\n";
      v0 = v0 + "<최근 기록 정보>\r\n#b";
      String r = "통달";
      int rv = 2;
      if (this.c.getPlayer().getOneInfoQuestInteger(1234590, "open_challenge") > 0) {
         r = "챌린지";
         rv = 0;
      }

      DojangMyRanking ranking = DojangRanking.getThisWeekMyRank(rv, this.c.getPlayer().getName());
      if (ranking != null && ranking.getRank() != 0) {
         v0 = v0 + "  - 랭킹 구간 : " + r + "\r\n";
         v0 = v0 + "  - 클리어 층 : " + ranking.getPoint() / 1000 + " 층\r\n";
         v0 = v0 + "  - 걸린 시간 : " + (1000 - ranking.getPoint() % 1000) + " 초\r\n";
      } else {
         v0 = v0 + "최근 기록 없음\r\n";
      }

      v0 = v0 + "\r\n#k이전 기록보다 좋은 기록을 달성했다면 #r무릉 순위표#k에 자동으로 등록될 거야.\r\n등록에 시간이 조금 걸릴 수 있으니 알아두라고.";
      this.sendOk(v0);
   }

   public void bigScriptProgressMessage(String message) {
      this.c.getPlayer().send(CField.UIPacket.sendBigScriptProgressMessage(message, FontType.NanumGothic, FontColorType.Yellow));
   }

   public void bigScriptProgressMessage(String message, FontType fontType, FontColorType fontColorType) {
      this.c.getPlayer().send(CField.UIPacket.sendBigScriptProgressMessage(message, fontType, fontColorType));
   }

   public void tryGetDojangRankerReward() {
      boolean challenge = false;
      if (this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_reward_c") > 0) {
         challenge = true;
      }

      if (challenge) {
         int rank = this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_reward_c");
         if (rank <= 0) {
            DojangMyRanking ranking = DojangRanking.getLastWeekMyRank(0, this.c.getPlayer().getName());
            if (ranking != null && ranking.getRank() != 0) {
               this.sendNext("아쉽게도 너는 지난주 랭킹 " + ranking.getRank() + "위로 보상을 받을 자격이 없어.\r\n더 분발해서 좋은 결과가 있기를 바란다.");
               this.dispose();
               return;
            }

            Pair<Integer, Integer> pair = DojangRanking.getLastTryDojang(0, this.c.getPlayer().getName());
            String v0 = "뭐야. 너는 지난주 무릉도장에 도전한 기록이 없는데?\r\n뭔가 착각한 거 아냐?\r\n\r\n";
            v0 = v0 + "너는 마지막으로...어디 보자...\r\n";
            if (pair == null) {
               v0 = v0 + "무릉도장에 도전한 기록이 없군.";
            } else {
               Calendar cal = Calendar.getInstance();
               cal.set(1, pair.left);
               cal.set(3, pair.right);
               cal.set(7, 1);
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
               String v1 = sdf.format(cal.getTime());
               cal = Calendar.getInstance();
               cal.set(1, pair.left);
               cal.set(3, pair.right);
               cal.set(7, 7);
               String v2 = sdf.format(cal.getTime());
               v0 = v0 + "#e#r" + v1 + " ~ " + v2 + "#n#k 사이에\r\n무릉도장에 도전했군.";
            }

            this.sendNext(v0);
            this.dispose();
            return;
         }

         int v = this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_reward_get_c");
         if (v > 0) {
            this.sendNext("너는 이미 랭킹 " + rank + "위 보상을 지급 받았잖아?\r\n랭커 보상은 한 번만 받을 수 있다고.");
            this.dispose();
            return;
         }

         if (!this.canHold(3700525, 1)) {
            this.sendNext("인벤토리 공간을 확보하고 다시 시도해줘.");
            this.dispose();
            return;
         }

         this.c.getPlayer().updateOneInfo(1234590, "dojang_reward_get_c", "1");
         int itemID = 0;
         if (rank == 1) {
            itemID = 3700525;
         } else {
            if (rank < 2 || rank > 5) {
               this.sendNext("너는 보상을 받을 자격이 없어보이는걸.");
               this.dispose();
               return;
            }

            itemID = 3700308;
         }

         this.c.getPlayer().gainItem(itemID, 1, false, 0L, "무릉도장 랭커 보상으로 획득한 아이템");
         this.sendNext("보상이 지급되었어. 이 아이템은 #b토요일 23시 59분#k까지 사용할 수 있어. 잘 사용하길 바란다구.");
         this.dispose();
      } else {
         int rankx = this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_reward");
         if (rankx <= 0) {
            DojangMyRanking rankingx = DojangRanking.getLastWeekMyRank(2, this.c.getPlayer().getName());
            if (rankingx != null && rankingx.getRank() != 0) {
               this.sendNext("아쉽게도 너는 지난주 랭킹 " + rankingx.getRank() + "위로 보상을 받을 자격이 없어.\r\n더 분발해서 좋은 결과가 있기를 바란다.");
               this.dispose();
               return;
            }

            Pair<Integer, Integer> pair = DojangRanking.getLastTryDojang(2, this.c.getPlayer().getName());
            String v0 = "뭐야. 너는 지난주 무릉도장에 도전한 기록이 없는데?\r\n뭔가 착각한 거 아냐?\r\n\r\n";
            v0 = v0 + "너는 마지막으로...어디 보자...\r\n";
            if (pair == null) {
               v0 = v0 + "무릉도장에 도전한 기록이 없군.";
            } else {
               Calendar cal = Calendar.getInstance();
               cal.set(1, pair.left);
               cal.set(3, pair.right);
               cal.set(7, 1);
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
               String v1 = sdf.format(cal.getTime());
               cal = Calendar.getInstance();
               cal.set(1, pair.left);
               cal.set(3, pair.right);
               cal.set(7, 7);
               String v2 = sdf.format(cal.getTime());
               v0 = v0 + "#e#r" + v1 + " ~ " + v2 + "#n#k 사이에\r\n무릉도장에 도전했군.";
            }

            this.sendNext(v0);
            this.dispose();
            return;
         }

         int vx = this.c.getPlayer().getOneInfoQuestInteger(1234590, "dojang_reward_get");
         if (vx > 0) {
            this.sendNext("너는 이미 랭킹 " + rankx + "위 보상을 지급 받았잖아?\r\n랭커 보상은 한 번만 받을 수 있다고.");
            this.dispose();
            return;
         }

         if (!this.canHold(3700525, 1)) {
            this.sendNext("인벤토리 공간을 확보하고 다시 시도해줘.");
            this.dispose();
            return;
         }

         this.c.getPlayer().updateOneInfo(1234590, "dojang_reward_get", "1");
         int itemID = 0;
         if (rankx == 1) {
            itemID = 3700526;
         } else {
            if (rankx < 2 || rankx > 5) {
               this.sendNext("너는 보상을 받을 자격이 없어보이는걸.");
               this.dispose();
               return;
            }

            itemID = 3700307;
         }

         this.c.getPlayer().gainItem(itemID, 1, false, 0L, "무릉도장 랭커 보상으로 획득한 아이템");
         this.sendNext("보상이 지급되었어. 이 아이템은 #b토요일 23시 59분#k까지 사용할 수 있어. 잘 사용하길 바란다구.");
         this.dispose();
      }
   }

   public boolean canEnterDojang() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      int hours = Integer.parseInt(dates[3]);
      int minutes = Integer.parseInt(dates[4]);
      int seconds = Integer.parseInt(dates[5]);
      int zellerMonth = 0;
      int zellerYear = 0;
      if (month < 3) {
         zellerMonth = month + 12;
         zellerYear = year - 1;
      } else {
         zellerMonth = month;
         zellerYear = year;
      }

      int computation = day + 26 * (zellerMonth + 1) / 10 + zellerYear + zellerYear / 4 + 6 * (zellerYear / 100) + zellerYear / 400;
      int dayOfWeek = computation % 7;
      return (dayOfWeek != 0 || hours != 23 || minutes < 40) && (dayOfWeek != 1 || hours != 0 || minutes >= 5);
   }

   public boolean checkBMQuestEquip() {
      List<Integer> blockedList = new ArrayList<>();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      this.c
         .getPlayer()
         .getInventory(MapleInventoryType.EQUIPPED)
         .list()
         .forEach(
            item -> {
               if (!ii.isCash(item.getItemId())) {
                  if (item.getPosition() != -11
                     && item.getPosition() != -10
                     && (item.getPosition() > -1600 || item.getPosition() < -1700)
                     && item.getPosition() != -117
                     && item.getPosition() != -122
                     && item.getPosition() != -131) {
                     blockedList.add(item.getItemId());
                  } else if (item.getPosition() == -11) {
                     boolean find = false;

                     for (int ix : this.bmWeapons) {
                        if (item.getItemId() == ix) {
                           find = true;
                           break;
                        }
                     }

                     if (!find) {
                        blockedList.add(item.getItemId());
                     }
                  }
               }
            }
         );
      if (blockedList.size() <= 0) {
         return true;
      } else {
         String v0 = "#r무기#k와 #b보조무기#k만 착용하고 도전해야 한다.\r\n\r\n#r<착용 해제해야 하는 아이템>#k\r\n";

         for (int i : blockedList) {
            v0 = v0 + "#i" + i + "# #z" + i + "#\r\n";
         }

         this.sendNextSelf(v0);
         return false;
      }
   }

   public int doGenesisWeaponFirstUpgrade() {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Equip equip = null;

      for (Item item : new ArrayList<>(this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).list())) {
         for (int i : this.bmWeapons) {
            if (item.getItemId() == i) {
               equip = (Equip)item;
               break;
            }
         }
      }

      if (equip == null) {
         for (Item item : new ArrayList<>(this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).list())) {
            for (int ix : this.bmWeapons) {
               if (item.getItemId() == ix) {
                  equip = (Equip)item;
                  break;
               }
            }
         }
      }

      if (equip == null) {
         this.sendNext("알 수 없는 오류가 발생했습니다.");
         this.dispose();
         return -1;
      } else {
         int weaponID = equip.getItemId() + 1;
         Equip genesis = (Equip)ii.getEquipById(weaponID);
         if (genesis == null) {
            this.sendNext("알 수 없는 오류가 발생했습니다.");
            this.dispose();
            return -1;
         } else {
            if (BonusStat.resetBonusStat(genesis, BonusStatPlaceType.LevelledRebirthFlame)) {
            }

            MapleInventoryManipulator.removeById(this.c, MapleInventoryType.getByType(equip.getType()), equip.getItemId(), 1, false, false);
            MapleInventoryManipulator.addbyItem(this.c, genesis);
            return genesis.getItemId();
         }
      }
   }

   public void doGenesisWeaponUpgrade() {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Equip equip = null;

      for (Item item : new ArrayList<>(this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).list())) {
         for (int i : this.bmWeapons) {
            if (item.getItemId() == i + 1) {
               equip = (Equip)item;
               break;
            }
         }
      }

      if (equip == null) {
         for (Item item : new ArrayList<>(this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).list())) {
            for (int ix : this.bmWeapons) {
               if (item.getItemId() == ix + 1) {
                  equip = (Equip)item;
                  break;
               }
            }
         }
      }

      if (equip == null) {
         this.sendNext("알 수 없는 오류가 발생했습니다.");
         this.dispose();
      } else {
         int weaponID = equip.getItemId();
         Equip genesis = (Equip)ii.getEquipById(weaponID);
         if (genesis == null) {
            this.sendNext("알 수 없는 오류가 발생했습니다.");
            this.dispose();
         } else {
            int flag = EquipEnchantMan.filterForJobWeapon(weaponID);
            ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_PAD, ItemUpgradeFlag.INC_MAD};
            ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[]{
               ItemUpgradeFlag.INC_STR, ItemUpgradeFlag.INC_DEX, ItemUpgradeFlag.INC_LUK, ItemUpgradeFlag.INC_MHP
            };
            ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[]{ItemUpgradeFlag.INC_INT};
            List<EquipEnchantScroll> source = new ArrayList<>();

            for (ItemUpgradeFlag f : flagArray) {
               for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
                  int index = 3;
                  EquipEnchantOption option = new EquipEnchantOption();
                  option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
                  if (f2.check(flag)) {
                     option.setOption(
                        f2.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3) * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1)
                     );
                     if (option.flag > 0) {
                        source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
                     }
                  }
               }
            }

            if (equip.getItemId() == 1242140) {
               source.clear();
               EquipEnchantOption option = new EquipEnchantOption();
               option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
               option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));
               source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
            }

            if (equip.getItemId() == 1232121) {
               source.clear();
               EquipEnchantOption option = new EquipEnchantOption();
               option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
               option.setOption(ItemUpgradeFlag.INC_MHP.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3) * 50);
               source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
            }

            if (equip.getItemId() == 1292021) {
               source.clear();
               EquipEnchantOption option = new EquipEnchantOption();
               option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
               option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));
               source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
            }

            if (equip.getItemId() == 1362148) {
               source.clear();
               EquipEnchantOption option = new EquipEnchantOption();
               option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
               option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));
               source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
            }

            if (equip.getItemId() == 1362148) {
               source.clear();
               EquipEnchantOption option = new EquipEnchantOption();
               option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
               option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));
               source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
            }

            if (source.size() <= 0) {
               this.sendNext("알 수 없는 오류가 발생했습니다.");
               this.dispose();
            } else {
               EquipEnchantScroll scroll = source.get(0);
               if (scroll == null) {
                  this.sendNext("알 수 없는 오류가 발생했습니다.");
                  this.dispose();
               } else {
                  Equip zeroEquip = null;
                  if (GameConstants.isZero(this.c.getPlayer().getJob())) {
                     zeroEquip = (Equip)this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
                  }

                  for (int ixx = 0; ixx < 8; ixx++) {
                     scroll.upgrade(genesis, 0, true, zeroEquip);
                  }

                  genesis.setCHUC(22);
                  genesis.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
                  byte grade = genesis.getAdditionalGrade();

                  genesis.setLines((byte)3);
                  genesis.setState((byte)19);

                  for (int ixx = 0; ixx < 3; ixx++) {
                     int optionGrade = 3;
                     int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, genesis.getPotentials(false, ixx), GradeRandomOption.Black);
                     genesis.setPotentialOption(ixx, option);
                  }

                  for (int ixx = 0; ixx < 3; ixx++) {
                     int optionGrade = 2;
                     int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, genesis.getPotentials(true, ixx), GradeRandomOption.Additional);
                     genesis.setPotentialOption(ixx + 3, option);
                  }

                  if (BonusStat.resetBonusStat(genesis, BonusStatPlaceType.LevelledRebirthFlame)) {
                  }

                  if (zeroEquip != null) {
                     zeroEquip.setCHUC(genesis.getCHUC());
                     zeroEquip.setItemState(genesis.getItemState());
                     zeroEquip.setExGradeOption(genesis.getExGradeOption());
                     zeroEquip.setLines(genesis.getLines());
                     zeroEquip.setState(genesis.getState());
                     zeroEquip.setPotential1(genesis.getPotential1());
                     zeroEquip.setPotential2(genesis.getPotential2());
                     zeroEquip.setPotential3(genesis.getPotential3());
                     zeroEquip.setPotential4(genesis.getPotential4());
                     zeroEquip.setPotential5(genesis.getPotential5());
                     zeroEquip.setPotential6(genesis.getPotential6());
                  }

                  MapleInventoryManipulator.removeFromSlot(
                     this.c, MapleInventoryType.getByType(equip.getType()), equip.getPosition(), equip.getQuantity(), false, false
                  );
                  MapleInventoryManipulator.addbyItem(this.c, genesis);
                  Center.Broadcast.broadcastMessage(
                     CWvsContext.serverNotice(6, this.c.getPlayer().getName() + "님이 봉인된 힘을 해방하고 검은 마법사의 힘이 담긴 제네시스 무기의 주인이 되었습니다.")
                  );
                  this.dispose();
               }
            }
         }
      }
   }

   public void askGenesisWeaponUpgrade() {
      AtomicInteger weapon = new AtomicInteger(0);
      this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).list().stream().forEach(item -> {
         for (int i : this.bmWeapons) {
            if (item.getItemId() == i + 1) {
               weapon.set(i);
               break;
            }
         }
      });
      if (weapon.get() == 0) {
         this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).list().stream().forEach(item -> {
            for (int i : this.bmWeapons) {
               if (item.getItemId() == i + 1) {
                  weapon.set(i);
                  break;
               }
            }
         });
      }

      if (weapon.get() == 0) {
         this.sendNext("제네시스 무기가 없으면 해당 퀘스트를 진행할 수 없습니다.");
         this.dispose();
      } else {
         String v0 = "#e<제네시스 무기>#n\r\n제네시스 무기가 강력한 힘으로 가득 찼습니다.\r\n제네시스 무기에 잠재된 힘을 완전히 깨울 수 있을 것 같은데, 해방을 시작해 보시겠습니까?\r\n\r\n#r- 15% 주문서로 모든 강화 완료\r\n- 스타포스 22성\r\n- 유니크 잠재능력 보유\r\n- 에픽 에디셔널 잠재능력 보유\r\n- 주문서/스타포스 강화 불가\r\n- 추가옵션/소울은 완전 해방 시 초기화\r\n#b#L0##i"
            + (weapon.get() + 1)
            + "# #z"
            + (weapon.get() + 1)
            + "##l";
         this.sendSimple(v0);
      }
   }

   public void displayPraiseRank() {
      Map<Integer, PraisePoint> ranks = PraisePointRank.getRanks();
      String v0 = "#e<칭찬 포인트 랭킹>#n\r\n\r\n";
      int count = 0;
      if (ranks.isEmpty()) {
         v0 = v0 + "랭킹 데이터가 부족합니다.";
      } else {
         NumberFormat nf = NumberFormat.getInstance();

         for (Entry<Integer, PraisePoint> entry : ranks.entrySet()) {
            v0 = v0
               + "#e["
               + entry.getKey()
               + "위]#n #b"
               + entry.getValue().getName()
               + "#k, 누적 포인트 : "
               + nf.format((long)entry.getValue().getTotalPoint())
               + "\r\n";
            if (++count >= 30) {
               break;
            }
         }
      }

      this.sendNext(v0);
      this.dispose();
   }

   public boolean canPraiseFindDB() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      boolean var14;
      try (Connection con = DBConnection.getConnection()) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
         Calendar CAL = new GregorianCalendar(Locale.KOREA);
         String fDate = sdf.format(CAL.getTime());
         String[] dates = fDate.split("-");
         int day = Integer.parseInt(dates[2]);
         ps = con.prepareStatement("SELECT * FROM `praise_point_log` WHERE from_account_id = ?");
         ps.setInt(1, this.c.getAccID());
         rs = ps.executeQuery();
         long maxTime = 0L;

         while (rs.next()) {
            long time = rs.getTimestamp("praise_time").getTime();
            if (maxTime < time) {
               maxTime = time;
            }
         }

         if (maxTime == 0L) {
            return true;
         }

         String date = sdf.format(maxTime);
         String[] dates2 = date.split("-");
         int lastDay = Integer.parseInt(dates2[2]);
         if (day == lastDay) {
            return false;
         }

         var14 = true;
      } catch (SQLException var30) {
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var32 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var33 = null;
            }
         } catch (SQLException var27) {
         }
      }

      return var14;
   }

   public boolean canPraise(String toName) {
      Timestamp timestamp = this.c.getCreated();
      long delta = System.currentTimeMillis() - timestamp.getTime();
      if (delta >= 2592000000L) {
         this.sendNext("계정 생성 후 30일 이전까지만 칭찬에 참여할 수 있습니다.");
         this.dispose();
         return false;
      } else if (!this.canDoPraise()) {
         this.sendNext("현재 랭킹 정산이 진행중입니다. 00시 05분부터 참여할 수 있습니다.");
         this.dispose();
         return false;
      } else if (!this.canPraiseFindDB()) {
         this.sendNext("오늘은 이미 칭찬에 참여해서 오늘은 더 이상 참여할 수 없습니다.");
         this.dispose();
         return false;
      } else if (!PraisePointRank.doPraise(this.c.getPlayer(), toName, this)) {
         this.dispose();
         return false;
      } else {
         this.c.getPlayer().updateOneInfo(1235858, "praise", "1");
         this.sendNext("#b" + toName + "#k님을 칭찬하셨습니다. #e500 칭찬 포인트#n를 획득했습니다.");
         this.dispose();
         return true;
      }
   }

   public boolean canDoPraise() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      int hours = Integer.parseInt(dates[3]);
      int minutes = Integer.parseInt(dates[4]);
      int seconds = Integer.parseInt(dates[5]);
      int zellerMonth = 0;
      int zellerYear = 0;
      if (month < 3) {
         zellerMonth = month + 12;
         zellerYear = year - 1;
      } else {
         zellerMonth = month;
         zellerYear = year;
      }

      int computation = day + 26 * (zellerMonth + 1) / 10 + zellerYear + zellerYear / 4 + 6 * (zellerYear / 100) + zellerYear / 400;
      int dayOfWeek = computation % 7;
      return (day != 1 || hours != 0 || minutes < 0) && (day != 1 || hours != 0 || minutes >= 5);
   }

   public int checkEnterSnowman() {
      List<Field> maps = new ArrayList<>();

      for (int i = 1; i <= 3; i++) {
         Field map = GameServer.getInstance(i).getMapFactory().getMap(209000001);
         if (map != null && !map.getAllMonstersThreadsafe().isEmpty()) {
            maps.add(map);
         }
      }

      if (!maps.isEmpty()) {
         List<Field> targets = maps.stream().sorted((a, b) -> a.getCharactersSize() - b.getCharactersSize()).collect(Collectors.toList());
         if (targets != null && !targets.isEmpty()) {
            return targets.get(0).getChannel();
         }
      }

      return -1;
   }

   public int checkEnterRabbit() {
      List<Field> maps = new ArrayList<>();

      for (int i = 1; i <= 3; i++) {
         Field map = GameServer.getInstance(i).getMapFactory().getMap(910010000);
         if (map != null && !map.getAllMonstersThreadsafe().isEmpty()) {
            maps.add(map);
         }
      }

      if (!maps.isEmpty()) {
         List<Field> targets = maps.stream().sorted((a, b) -> a.getCharactersSize() - b.getCharactersSize()).collect(Collectors.toList());
         if (targets != null && !targets.isEmpty()) {
            return targets.get(0).getChannel();
         }
      }

      return -1;
   }

   public boolean canDonationEvent(String point) {
      if (point.contains("초심자")) {
         return false;
      } else {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
         Calendar CAL = new GregorianCalendar(Locale.KOREA);
         String fDate = sdf.format(CAL.getTime());
         String[] dates = fDate.split("-");
         int year = Integer.parseInt(dates[0]);
         int month = Integer.parseInt(dates[1]);
         int day = Integer.parseInt(dates[2]);
         if (year == 2022
            && (month == 1 || month == 2)
            && (day == 28 || day == 29 || day == 30 || day == 31 || day == 1 || day == 2 || day == 3 || day == 4 || day == 5 || day == 6)) {
            PreparedStatement ps = null;
            ResultSet rs = null;

            boolean var13;
            try (Connection con = DBConnection.getConnection()) {
               ps = con.prepareStatement("SELECT `name` FROM `donation_log` WHERE `account` = ?");
               ps.setString(1, this.getClient().getAccountName());
               rs = ps.executeQuery();
               boolean find = false;

               while (rs.next()) {
                  if (rs.getString("name").equals("보너스이벤트")) {
                     find = true;
                     break;
                  }
               }

               if (find) {
                  return false;
               }

               var13 = true;
            } catch (SQLException var28) {
               return false;
            } finally {
               try {
                  if (ps != null) {
                     ps.close();
                     PreparedStatement var30 = null;
                  }

                  if (rs != null) {
                     rs.close();
                     ResultSet var31 = null;
                  }
               } catch (SQLException var25) {
               }
            }

            return var13;
         } else {
            return false;
         }
      }
   }

   public int putDonationRequest(String point, String realName, boolean event) {
      int p = 0;
      int type = 0;
      boolean first = true;
      if (!point.contains("초심자")
         && !point.contains("설날")
         && !point.contains("어린이날")
         && !point.contains("추석")
         && !point.contains("3주년")
         && !point.contains("크리스마스")
         && !point.contains("가정의달")
         && !point.contains("상시패키지")
         && !point.contains("2023")
         && !point.contains("5월")) {
         try {
            p = Integer.parseInt(point);
         } catch (NumberFormatException var86) {
            this.sendNext("초심자 패키지가 아닌 경우 금액을 구둣점을 제외한 숫자로 적어주시기 바랍니다.");
            return -1;
         }

         if (p < 10000) {
            this.sendNext("최소 충전 금액은 1만원 이상부터 가능합니다.");
            return -1;
         }

         if (p % 1000 > 0) {
            this.sendNext("천원 단위로만 충전 가능합니다.");
            return -1;
         }

         PreparedStatement ps = null;
         ResultSet rs = null;

         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT * FROM `donation_log` WHERE `account` = ?");
            ps.setString(1, this.getClient().getAccountName());
            rs = ps.executeQuery();

            while (rs.next()) {
               if (!rs.getString("name").equals("초심자패키지") && !rs.getString("name").contains("설날")) {
                  first = false;
               }
            }
         } catch (SQLException var91) {
            this.sendNext("알 수 없는 오류가 발생하였습니다.");
            this.dispose();
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var98 = null;
               }

               if (rs != null) {
                  rs.close();
                  ResultSet var103 = null;
               }
            } catch (SQLException var83) {
            }
         }
      } else {
         PreparedStatement ps = null;
         ResultSet rs = null;

         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT * FROM `donation_log` WHERE `account` = ?");
            ps.setString(1, this.getClient().getAccountName());
            rs = ps.executeQuery();
            boolean find = false;

            while (rs.next()) {
               String name = rs.getString("name");
               if (point.contains("초심자") && name.equals("초심자패키지")
                  || point.contains("설날A") && name.equals("설날A")
                  || point.contains("설날B") && name.equals("설날B")
                  || point.contains("설날C") && name.equals("설날C")
                  || point.contains("가정의달C") && name.equals("가정의달C")
                  || point.contains("가정의달B") && name.equals("가정의달B")
                  || point.contains("가정의달A") && name.equals("가정의달A")
                  || point.contains("가정의달S") && name.equals("가정의달S")
                  || point.contains("가정의달SS") && name.equals("가정의달SS")
                  || point.contains("가정의달SSS") && name.equals("가정의달SSS")
                  || point.equals("추석패키지I") && name.equals("추석패키지I")
                  || point.equals("추석패키지II") && name.equals("추석패키지II")
                  || point.equals("추석패키지III") && name.equals("추석패키지III")
                  || point.equals("추석패키지IV") && name.equals("추석패키지IV")
                  || point.equals("3주년패키지I") && name.equals("3주년패키지I")
                  || point.equals("3주년패키지II") && name.equals("3주년패키지II")
                  || point.equals("3주년패키지III") && name.equals("3주년패키지III")
                  || point.equals("크리스마스패키지1") && name.equals("크리스마스패키지1")
                  || point.equals("크리스마스패키지2") && name.equals("크리스마스패키지2")
                  || point.equals("크리스마스패키지3") && name.equals("크리스마스패키지3")
                  || point.equals("크리스마스패키지4") && name.equals("크리스마스패키지4")
                  || point.equals("2023패키지1") && name.equals("2023패키지1")
                  || point.equals("2023패키지2") && name.equals("2023패키지2")
                  || point.equals("2023패키지3") && name.equals("2023패키지3")
                  || point.equals("2023패키지4") && name.equals("2023패키지4")
                  || point.equals("2023패키지5") && name.equals("2023패키지5")
                  || point.equals("2023패키지6") && name.equals("2023패키지6")
                  || point.equals("2023패키지7") && name.equals("2023패키지7")
                  || point.equals("2023패키지8") && name.equals("2023패키지8")
                  || point.equals("2023패키지9") && name.equals("2023패키지9")
                  || point.equals("2023패키지10") && name.equals("2023패키지10")
                  || point.equals("2023패키지11") && name.equals("2023패키지11")
                  || point.equals("5월패키지1") && name.equals("5월패키지1")
                  || point.equals("5월패키지2") && name.equals("5월패키지2")
                  || point.equals("5월패키지3") && name.equals("5월패키지3")
                  || point.equals("5월패키지4") && name.equals("5월패키지4")
                  || point.equals("5월패키지5") && name.equals("5월패키지5")
                  || point.equals("5월패키지6") && name.equals("5월패키지6")
                  || point.equals("5월패키지7") && name.equals("5월패키지7")
                  || point.equals("5월패키지8") && name.equals("5월패키지8")
                  || point.equals("5월패키지9") && name.equals("5월패키지9")
                  || point.equals("5월패키지10") && name.equals("5월패키지10")
                  || point.equals("5월패키지11") && name.equals("5월패키지11")) {
                  find = true;
               }
            }

            if (find) {
               this.sendNext("이미 해당 패키지를 구매하여 구매 불가능합니다.");
               return -1;
            }
         } catch (SQLException var94) {
            this.sendNext("알 수 없는 오류가 발생하였습니다.");
            this.dispose();
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var96 = null;
               }

               if (rs != null) {
                  rs.close();
                  ResultSet var101 = null;
               }
            } catch (SQLException var82) {
            }
         }

         if (point.contains("초심자")) {
            p = 10000;
            type = 2;
         } else if (point.equals("설날A")) {
            p = 100000;
            type = 5;
         } else if (point.equals("설날B")) {
            p = 100000;
            type = 6;
         } else if (point.equals("설날C")) {
            p = 50000;
            type = 7;
         } else if (point.equals("어린이날")) {
            p = 55000;
            type = 8;
         } else if (point.equals("가정의달C")) {
            p = 50000;
            type = 9;
         } else if (point.equals("가정의달B")) {
            p = 100000;
            type = 10;
         } else if (point.equals("가정의달A")) {
            p = 300000;
            type = 11;
         } else if (point.equals("가정의달S")) {
            p = 500000;
            type = 12;
         } else if (point.equals("가정의달SS")) {
            p = 1000000;
            type = 13;
         } else if (point.equals("가정의달SSS")) {
            p = 1500000;
            type = 14;
         } else if (point.equals("상시패키지1")) {
            p = 500000;
            type = 15;
         } else if (point.equals("상시패키지2")) {
            p = 1000000;
            type = 16;
         } else if (point.equals("추석패키지I")) {
            p = 10000;
            type = 17;
         } else if (point.equals("추석패키지II")) {
            p = 25000;
            type = 18;
         } else if (point.equals("추석패키지III")) {
            p = 75000;
            type = 19;
         } else if (point.equals("추석패키지IV")) {
            p = 110000;
            type = 20;
         } else if (point.equals("3주년패키지I")) {
            p = 55000;
            type = 21;
         } else if (point.equals("3주년패키지II")) {
            p = 55000;
            type = 22;
         } else if (point.equals("3주년패키지III")) {
            p = 110000;
            type = 23;
         } else if (point.equals("크리스마스패키지1")) {
            p = 30000;
            type = 24;
         } else if (point.equals("크리스마스패키지2")) {
            p = 50000;
            type = 25;
         } else if (point.equals("크리스마스패키지3")) {
            p = 50000;
            type = 26;
         } else if (point.equals("크리스마스패키지4")) {
            p = 110000;
            type = 27;
         } else if (point.equals("2023패키지1")) {
            p = 30000;
            type = 28;
         } else if (point.equals("2023패키지2")) {
            p = 50000;
            type = 29;
         } else if (point.equals("2023패키지3")) {
            p = 50000;
            type = 30;
         } else if (point.equals("2023패키지4")) {
            p = 50000;
            type = 31;
         } else if (point.equals("2023패키지5")) {
            p = 110000;
            type = 32;
         } else if (point.equals("2023패키지6")) {
            p = 50000;
            type = 33;
         } else if (point.equals("2023패키지7")) {
            p = 100000;
            type = 34;
         } else if (point.equals("2023패키지8")) {
            p = 300000;
            type = 35;
         } else if (point.equals("2023패키지9")) {
            p = 500000;
            type = 36;
         } else if (point.equals("2023패키지10")) {
            p = 1000000;
            type = 37;
         } else if (point.equals("2023패키지11")) {
            p = 1500000;
            type = 38;
         } else if (point.equals("5월패키지1")) {
            p = 30000;
            type = 39;
         } else if (point.equals("5월패키지2")) {
            p = 50000;
            type = 40;
         } else if (point.equals("5월패키지3")) {
            p = 50000;
            type = 41;
         } else if (point.equals("5월패키지4")) {
            p = 50000;
            type = 42;
         } else if (point.equals("5월패키지5")) {
            p = 110000;
            type = 43;
         } else if (point.equals("5월패키지6")) {
            p = 50000;
            type = 44;
         } else if (point.equals("5월패키지7")) {
            p = 100000;
            type = 45;
         } else if (point.equals("5월패키지8")) {
            p = 300000;
            type = 46;
         } else if (point.equals("5월패키지9")) {
            p = 500000;
            type = 47;
         } else if (point.equals("5월패키지10")) {
            p = 1000000;
            type = 48;
         } else if (point.equals("5월패키지11")) {
            p = 1500000;
            type = 49;
         }
      }

      if (event) {
         type = 3;
      }

      PreparedStatement ps = null;
      ResultSet rs = null;

      label1573: {
         byte var108;
         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement(
               "INSERT INTO `donation_request` (`account_name`, `player_name`, `real_name`, `point`, `type`, `status`) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, this.getClient().getAccountName());
            ps.setString(2, this.getPlayer().getName());
            ps.setString(3, realName);
            ps.setInt(4, p);
            ps.setInt(5, type);
            ps.setInt(6, 0);
            ps.executeQuery();
            break label1573;
         } catch (SQLException var88) {
            var108 = -1;
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var100 = null;
               }

               if (rs != null) {
                  rs.close();
                  ResultSet var105 = null;
               }
            } catch (SQLException var80) {
            }
         }

         return var108;
      }

      if (first && type != 2 && type < 8) {
         type = 4;
      }

      DonationRequest.init();
      return type;
   }

   public boolean deleteDonationRequest(int sel) {
      PreparedStatement ps = null;
      ResultSet rs = null;

      boolean var25;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT `status` FROM `donation_request` WHERE `id` = ? and `account_name` = ?");
         ps.setInt(1, sel);
         ps.setString(2, this.getClient().getAccountName());
         rs = ps.executeQuery();
         int status = 0;

         while (rs.next()) {
            status = rs.getInt("status");
         }

         if (status != 1) {
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `donation_request` WHERE `id` = ? and `account_name` = ?");
            ps.setInt(1, sel);
            ps.setString(2, this.getClient().getAccountName());
            ps.executeQuery();
            this.sendNext((DBConfig.isGanglim ? "#fs11#" : "#fs12#") + "해당 충전 신청이 취소되었습니다.");
            this.dispose();
            DonationRequest.init();
            return true;
         }

         var25 = false;
      } catch (SQLException var21) {
         this.sendNext("알 수 없는 오류가 발생하였습니다.");
         this.dispose();
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var24 = null;
            }
         } catch (SQLException var18) {
         }
      }

      return var25;
   }

   public void askDeleteDonationRequest(int sel) {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `donation_request` WHERE `id` = ? and `account_name` = ?");
         ps.setInt(1, sel);
         ps.setString(2, this.getClient().getAccountName());
         rs = ps.executeQuery();
         String msg = (DBConfig.isGanglim ? "#fs11#" : "#fs12#")
            + "다음 신청 내역을 취소하시겠습니까? 입금 후 취소 시 취소건 처리에 대해 문의해주셔야 하고 시간이 소요될 수 있으므로 반드시 입금 전 취소해주시기 바랍니다.\r\n\r\n";

         while (rs.next()) {
            int status = rs.getInt("status");
            msg = msg + "#e캐릭터 이름#n : #b" + rs.getString("player_name") + "#k\r\n";
            msg = msg + "#e신청 금액#n : #b" + rs.getInt("point") + "#k\r\n";
            msg = msg + "#e입금자 명#n : #b" + rs.getString("real_name") + "#k\r\n";
            msg = msg + "#e유형#n : #b";
            int type = rs.getInt("type");
            if (type == 0) {
               msg = msg + "일반 충전#k\r\n";
            } else if (type == 1) {
               msg = msg + "이벤트 충전#k\r\n";
            } else if (type == 2) {
               msg = msg + "초심자 패키지#k\r\n";
            } else if (type == 3) {
               msg = msg + "신년 이벤트 충전#k\r\n";
            }

            Timestamp time = rs.getTimestamp("time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fDate = sdf.format(time.getTime());
            msg = msg + "#e신청 날짜#n : #b" + fDate + "#k\r\n";
            msg = msg + "#e처리 상태#n : ";
            if (status == 0) {
               msg = msg + "미처리#k\r\n";
            } else if (status == 1) {
               msg = msg + "#b처리 완료#k\r\n";
            } else if (status == 2) {
               msg = msg + "#r입금 누락#k\r\n";
            }
         }

         this.sendYesNo(msg);
      } catch (SQLException var23) {
         this.sendNext("알 수 없는 오류가 발생하였습니다.");
         this.dispose();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var25 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var26 = null;
            }
         } catch (SQLException var20) {
         }
      }
   }

   public boolean displayDonationRequest() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      int id;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `donation_request` WHERE `account_name` = ?");
         ps.setString(1, this.getClient().getAccountName());
         rs = ps.executeQuery();
         boolean find = false;

         String msg;
         for (msg = (DBConfig.isGanglim ? "#fs11#" : "#fs12#") + "#e<신청한 " + (DBConfig.isGanglim ? "후원 캐시" : "강림 포인트") + " 충전 신청 내역>#n\r\n";
            rs.next();
            find = true
         ) {
            id = rs.getInt("id");
            int status = rs.getInt("status");
            if (status == 0) {
               msg = msg + "#L" + id * 12438 + "#\r\n";
            } else {
               msg = msg + "\r\n";
            }

            msg = msg + "#e캐릭터 이름#n : #b" + rs.getString("player_name") + "#k\r\n";
            msg = msg + "#e신청 금액#n : #b" + rs.getInt("point") + "#k\r\n";
            msg = msg + "#e입금자 명#n : #b" + rs.getString("real_name") + "#k\r\n";
            msg = msg + "#e유형#n : #b";
            int type = rs.getInt("type");
            if (type == 0) {
               msg = msg + "일반 충전#k\r\n";
            } else if (type == 1) {
               msg = msg + "이벤트 충전#k\r\n";
            } else if (type == 2) {
               msg = msg + "초심자 패키지#k\r\n";
            } else if (type == 3) {
               msg = msg + "신년 이벤트 충전#k\r\n";
            }

            Timestamp time = rs.getTimestamp("time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fDate = sdf.format(time.getTime());
            msg = msg + "#e신청 날짜#n : #b" + fDate + "#k\r\n";
            msg = msg + "#e처리 상태#n : ";
            if (status == 0) {
               msg = msg + "미처리#k\r\n";
            } else if (status == 1) {
               msg = msg + "#b처리 완료#k\r\n";
            } else if (status == 2) {
               msg = msg + "#r입금 누락#k\r\n";
            }

            if (status == 0) {
               msg = msg + "                                                              #r취소하기#k#l\r\n\r\n";
            }
         }

         if (find) {
            this.sendNext(msg);
            return true;
         }

         this.sendNext("등록하신 " + (DBConfig.isGanglim ? "후원 캐시" : "강림 포인트") + " 충전 신청 내역이 없습니다.");
         id = 0;
      } catch (SQLException var25) {
         this.sendNext("알 수 없는 오류가 발생하였습니다.");
         return false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var27 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var28 = null;
            }
         } catch (SQLException var22) {
         }
      }

      return id == 1;
   }

   public void displayDonationLog() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `donation_log` WHERE `account` = ?");
         ps.setString(1, this.getClient().getAccountName());
         rs = ps.executeQuery();
         boolean find = false;
         String msg = (DBConfig.isGanglim ? "#fs11#" : "#fs12#") + "#e<" + (DBConfig.isGanglim ? "후원 캐시" : "강림 포인트") + " 충전 내역>#n\r\n";
         int totalPoint = 0;

         String msg2;
         for (msg2 = ""; rs.next(); find = true) {
            String name = rs.getString("name");
            msg2 = msg2 + "#e캐릭터 이름#n : #b" + name + "#k\r\n";
            msg2 = msg2 + "#e충전 금액#n : #b" + rs.getString("price") + "#k\r\n";
            String p = rs.getString("price").replace(",", "");
            if (name.equals("이벤트참여")) {
               msg2 = msg2 + "#e충전 유형#n : #b이벤트 참여#k\r\n";
            } else if (name.equals("신년이벤트")) {
               msg2 = msg2 + "#e충전 유형#n : #b신년 이벤트 참여#k\r\n";
            } else if (name.equals("클스마스이벤트")) {
               msg2 = msg2 + "#e충전 유형#n : #b크리스마스 이벤트 참여#k\r\n";
            } else if (name.equals("보너스이벤트")) {
               msg2 = msg2 + "#e충전 유형#n : #b보너스 이벤트 참여#k\r\n";
            } else if (name.equals("초보자패키지")) {
               msg2 = msg2 + "#e충전 유형#n : #b초심자패키지#k\r\n";
            } else if (name.equals("어린이날")) {
               msg2 = msg2 + "#e충전 유형#n : #b어린이 날 패키지#k\r\n";
            } else if (name.contains("가정의달")) {
               msg2 = msg2 + "#e충전 유형#n : #b가정의 달 패키지#k\r\n";
            } else if (name.contains("추석")) {
               msg2 = msg2 + "#e충전 유형#n : #b추석 패키지#k\r\n";
            } else if (name.contains("3주년")) {
               msg2 = msg2 + "#e충전 유형#n : #b3주년 패키지#k\r\n";
            } else if (name.contains("크리스마스")) {
               msg2 = msg2 + "#e충전 유형#n : #b크리스마스 패키지#k\r\n";
            } else if (name.contains("2023")) {
               msg2 = msg2 + "#e충전 유형#n : #b2023 패키지#k\r\n";
            } else if (name.contains("5월")) {
               msg2 = msg2 + "#e충전 유형#n : #b23 가정의 달 패키지#k\r\n";
            } else if (name.contains("상시패키지")) {
               msg2 = msg2 + "#e충전 유형#n : #b상시 패키지#k\r\n";
            } else {
               msg2 = msg2 + "#e충전 유형#n : #b일반 충전#k\r\n";
            }

            if (!name.contains("패키지") && !name.contains("어린이날")
               || name.contains("추석")
               || name.contains("가정의달")
               || name.contains("3주년")
               || name.contains("크리스마스패키지")
               || name.contains("상시패키지")
               || name.contains("2023패키지")
               || name.contains("5월")) {
               totalPoint += Integer.parseInt(p);
            }

            msg2 = msg2 + "#e충전 날짜#n : #b" + rs.getString("date") + "#k\r\n\r\n";
         }

         NumberFormat nf = NumberFormat.getInstance();
         msg = msg + "#e총 누적 금액#n : #b" + nf.format((long)totalPoint) + "#k\r\n\r\n#e<충전 내역>#n\r\n";
         if (!find) {
            msg = msg + "충전 내역이 없습니다.";
         } else {
            msg = msg + msg2;
         }

         this.sendNext(msg);
      } catch (SQLException var22) {
         this.sendNext("알 수 없는 오류가 발생하였습니다.");
         this.dispose();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var24 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var25 = null;
            }
         } catch (SQLException var19) {
         }
      }
   }

   public boolean canBuyChuseokPackage(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year != 2022 || month != 9) {
         return false;
      } else if (day >= 2 && day <= 30) {
         return this.getPlayer().getOneInfoQuestInteger(1234579, "chuseok_package_" + type) >= 1
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_chuseok_package_" + type) < 1;
      } else {
         return false;
      }
   }

   public boolean can3AnniversaryPackage(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year != 2022 || month != 10) {
         return false;
      } else if (day >= 16 && day <= 31) {
         return this.getPlayer().getOneInfoQuestInteger(1234579, "3aniv_package_" + type) >= 1
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_3aniv_package_" + type) < 1;
      } else {
         return false;
      }
   }

   public boolean canBuyMayPackage(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year != 2022 || month != 5) {
         return false;
      } else if (day >= 5 && day <= 20) {
         return this.getPlayer().getOneInfoQuestInteger(1234579, "may_package_" + type) >= 1
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_may_package_" + type) < 1;
      } else {
         return false;
      }
   }

   public boolean canBuyChildrensDayPackage() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year == 2022 && month == 5 && day == 5
         || year == 2022 && month == 5 && day == 6
         || year == 2022 && month == 5 && day == 7
         || year == 2022 && month == 5 && day == 28) {
         return this.getPlayer().getOneInfoQuestInteger(1235999, "cd_package") >= 2
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_cd_package") < 2;
      } else {
         return false;
      }
   }

   public boolean canBuyNewYearsDayPackage(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      return year == 2022
            && (month == 1 || month == 2)
            && (day == 28 || day == 29 || day == 30 || day == 31 || day == 1 || day == 2 || day == 3 || day == 4 || day == 5 || day == 6)
         ? this.getPlayer().getOneInfoQuestInteger(1234579, "nyd_package" + type) < 1
         : false;
   }

   public boolean canBuy2023MayPackage(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year == 2023 && month == 5) {
         return this.getPlayer().getOneInfoQuestInteger(1234579, "2023_may_package_" + type) >= 1
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_2023_may_package_" + type) < 1;
      } else {
         return false;
      }
   }

   public boolean canBuy2023Package(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year == 2023 && (month == 2 && day >= 22 || month == 3 && day <= 25)) {
         return this.getPlayer().getOneInfoQuestInteger(1234579, "2023_package_" + type) >= 1
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_2023_package_" + type) < 1;
      } else {
         return false;
      }
   }

   public boolean canBuyNewbiePackage() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      boolean check;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `beginner_package` WHERE `accountid` = ?");
         ps.setInt(1, this.getClient().getAccID());
         rs = ps.executeQuery();
         check = true;

         while (rs.next()) {
            check = false;
         }

         return check;
      } catch (SQLException var19) {
         this.sendNext("알 수 없는 오류가 발생하였습니다.");
         this.dispose();
         check = false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var21 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var22 = null;
            }
         } catch (SQLException var16) {
         }
      }

      return check;
   }

   public boolean canXMasPackage(int type) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Calendar CAL = new GregorianCalendar(Locale.KOREA);
      String fDate = sdf.format(CAL.getTime());
      String[] dates = fDate.split("-");
      int year = Integer.parseInt(dates[0]);
      int month = Integer.parseInt(dates[1]);
      int day = Integer.parseInt(dates[2]);
      if (year != 2022 || month != 12) {
         return false;
      } else if (day >= 18 && day <= 27) {
         return this.getPlayer().getOneInfoQuestInteger(1234579, "xmas_package_" + type) >= 1
            ? false
            : this.getPlayer().getOneInfoQuestInteger(1236000, "get_xmas_package_" + type) < 1;
      } else {
         return false;
      }
   }

   public boolean canDonationRequest() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      boolean check;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `donation_request` WHERE `account_name` = ?");
         ps.setString(1, this.getClient().getAccountName());
         rs = ps.executeQuery();
         check = true;

         while (rs.next()) {
            int status = rs.getInt("status");
            if (status == 0) {
               check = false;
            }
         }

         return check;
      } catch (SQLException var19) {
         this.sendNext("알 수 없는 오류가 발생하였습니다.");
         this.dispose();
         check = false;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var21 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var22 = null;
            }
         } catch (SQLException var16) {
         }
      }

      return check;
   }

   public boolean canEnterBoss(String key) {
      if (!DBConfig.isGanglim && !DBConfig.isHosting) {
         return true;
      } else {
         String v = this.c.getPlayer().getOneInfoQuest(1234569, key);
         if (!v.isEmpty()) {
            long canTime = Long.valueOf(v);
            long now = System.currentTimeMillis();
            long delta = canTime - now;
            if (delta > 0L) {
               int minute = (int)(delta / 1000L / 60L);
               this.sendNext(minute + "분 후에 입장할 수 있습니다.");
               this.dispose();
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   }

   public void setBossEnter(Party party, String bossName, String bossKeyValue, String canTimeKey, int canTimeType) {
      this.getChannelServer().getPartyMembers(party).forEach(p -> {
         StringBuilder sb = new StringBuilder("보스 " + bossName + " 입장");
         LoggingManager.putLog(new BossLog(p, BossLogType.EnterLog.getType(), sb));
         if (!DBConfig.isGanglim) {
            p.CountAdd(bossKeyValue);
            if (canTimeKey != null) {
               p.updateCanTime(canTimeKey, canTimeType);
            }
         }
      });
   }

   public void logBossEnter(String bossName, String... playerName) {
      PreparedStatement ps = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("INSERT INTO `boss_log` (`bossname`, `name`) VALUES(?, ?)");

         for (String name : playerName) {
            ps.setString(1, bossName);
            ps.setString(2, name);
            ps.addBatch();
         }

         ps.executeBatch();
      } catch (SQLException var21) {
         var21.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }
         } catch (SQLException var18) {
            var18.printStackTrace();
         }
      }
   }

   public int getTotalPrice() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      int totalPoint;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `donation_log` WHERE `account` = ?");
         ps.setString(1, this.getClient().getAccountName());
         rs = ps.executeQuery();
         totalPoint = 0;

         while (rs.next()) {
            String name = rs.getString("name");
            String p = rs.getString("price").replace(",", "");
            if (!name.contains("패키지") && !name.contains("어린이날")
               || name.contains("추석")
               || name.contains("가정의달")
               || name.contains("3주년")
               || name.contains("크리스마스")
               || name.contains("상시패키지")
               || name.contains("2023")
               || name.contains("5월")) {
               totalPoint += Integer.parseInt(p);
            }
         }

         return totalPoint;
      } catch (SQLException var19) {
         totalPoint = -1;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var21 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var22 = null;
            }
         } catch (SQLException var16) {
         }
      }

      return totalPoint;
   }

   public void openRenameUI() {
      this.c.getPlayer().send(CField.UIPacket.openUIOption(1110, 4034803));
   }

   public void renameResult(int result) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_RENAME_RESULT.getValue());
      packet.write(result);
      if (result == 9) {
         packet.writeInt(0);
      }

      this.c.getPlayer().send(packet.getPacket());
   }

   public void effectText(String text, int textSpeed, int fadeSpeed, int textWidth, int type, int nameWidth, int nameHeight) {
      TextEffect e = new TextEffect(-1, text, textSpeed, fadeSpeed, textWidth, type, nameWidth, nameHeight);
      this.c.getPlayer().send(e.encodeForLocal());
   }

   public void spawnLocalNpc(int templateID, int x, int y, int f, int fh, boolean canMove) {
      MapleNPC npc = MapleLifeFactory.getNPC(templateID);
      if (npc != null && !npc.getName().equals("MISSINGNO")) {
         Point pos = new Point(x, y);
         npc.setPosition(pos);
         npc.setCy(y);
         npc.setRx0(x - 50);
         npc.setRx1(x + 50);
         npc.setF(f);
         npc.setFh(fh);
         npc.setCustom(true);
         npc.setLocalUserID(this.getPlayer().getId());
         npc.setCanMove(canMove);
         this.c.getPlayer().getMap().addMapObject(npc);
         this.c.getPlayer().send(CField.NPCPacket.spawnNPC(npc, true));
         this.c.getPlayer().send(CField.NPCPacket.npcSpecialAction(npc.getObjectId(), "summon", 0, 0));
      }
   }

   public void removeLocalNpc(int templateID) {
      this.c.getPlayer().getMap().removeNpc(templateID);
   }

   public int getNpcObjectID(int templateID) {
      MapleNPC npc = this.c.getPlayer().getMap().getNPCById(templateID);
      return npc != null ? npc.getObjectId() : 0;
   }

   public void npcFlipForcely(int templateID, int flip) {
      this.c.getPlayer().send(CField.NPCPacket.npcFlipForcely(this.getNpcObjectID(templateID), flip));
   }

   public void npcMoveForcely(int templateID, int forceX, int moveX, int speedRate) {
      this.c.getPlayer().send(CField.NPCPacket.npcMoveForcely(this.getNpcObjectID(templateID), forceX, moveX, speedRate));
   }

   public void setQuickMove() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.QUICK_MOVE.getValue());
      packet.write(1);
      packet.writeInt(0);
      packet.writeInt(9010022);
      packet.writeInt(2);
      packet.writeInt(10);
      packet.writeLong(PacketHelper.getTime(-1L));
      packet.writeLong(PacketHelper.getTime(-2L));
      this.c.getPlayer().send(packet.getPacket());
   }

   public void dimensionalMirror() {
      PacketEncoder packet = new PacketEncoder();
      ServerConstants.dimensionalMirror.encode(packet);
      this.c.getPlayer().send(packet.getPacket());
   }

   public void registerWaitQueue(int fieldID) {
      Center.GameWaitQueue.registerQueue(fieldID, this.getPlayer());
   }

   public void enterBuzzingHouse() {
      for (int i = 993190400; i < 993190490; i++) {
         Field f = this.c.getChannelServer().getMapFactory().getMap(i);
         if (f != null && f.getCharactersSize() <= 0) {
            this.c.getPlayer().warp(i);
            return;
         }
      }

      this.c.getPlayer().dropMessage(5, "현재 많은 유저가 참여중입니다. 잠시 후 시도해주세요.");
   }

   public void enterMission2Space() {
      for (int i = 993190800; i < 993190890; i++) {
         Field f = this.c.getChannelServer().getMapFactory().getMap(i);
         if (f != null && f.getCharactersSize() <= 0) {
            this.c.getPlayer().warp(i);
            return;
         }
      }

      this.c.getPlayer().dropMessage(5, "현재 많은 유저가 참여중입니다. 잠시 후 시도해주세요.");
   }

   public void enterExtremeRail() {
      for (int i = 993190000; i < 993190090; i++) {
         Field f = this.c.getChannelServer().getMapFactory().getMap(i);
         if (f != null && f.getCharactersSize() <= 0) {
            this.c.getPlayer().warp(i);
            return;
         }
      }

      this.c.getPlayer().dropMessage(5, "현재 많은 유저가 참여중입니다. 잠시 후 시도해주세요.");
   }

   public int getRecoveryExtremePointWeek() {
      String b = "2021. 3. 27";
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");

      try {
         long time = System.currentTimeMillis();
         long target = sdf.parse(b).getTime();
         long delta = target - time;
         int deltaDay = (int)(delta / 86400000L);
         int remainPoint = this.getPlayer().getExtremeRealCash();
         if (remainPoint > 0) {
            int canCount = 0;
            if (deltaDay > -7) {
               canCount = 1;
            } else if (deltaDay > -14) {
               canCount = 2;
            } else if (deltaDay > -21) {
               canCount = 3;
            } else if (deltaDay > -28) {
               canCount = 4;
            } else if (deltaDay <= -28) {
               canCount = 5;
            }

            return canCount;
         }
      } catch (Exception var12) {
      }

      return 0;
   }

   public int doRecoveryExtremePoint(int count) {
      String b = "2021. 3. 27";
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd");

      try {
         long time = System.currentTimeMillis();
         long target = sdf.parse(b).getTime();
         long delta = target - time;
         int deltaDay = (int)(delta / 86400000L);
         int remainPoint = this.getPlayer().getExtremeRealCash();
         int getCount = this.getPlayer().getGetExtremeRealCash();
         if (remainPoint > 0) {
            int canCount = 0;
            if (deltaDay > -7) {
               canCount = 1;
            } else if (deltaDay > -14) {
               canCount = 2;
            } else if (deltaDay > -21) {
               canCount = 3;
            } else if (deltaDay > -28) {
               canCount = 4;
            } else if (deltaDay <= -28) {
               canCount = 5;
            }

            int c = canCount - getCount;
            if (c > 0) {
               int p = (int)(remainPoint * (0.2 * c));
               this.getPlayer().setGetExtremeRealCash(canCount);
               this.getPlayer().gainRealCash(p);
               String cashName = DBConfig.isGanglim ? "후원 캐시" : "강림 포인트";
               this.getPlayer().dropMessage(5, p + " " + cashName + Locales.getKoreanJosa(cashName, JosaType.을를) + " 획득했습니다.");
               return p;
            }

            return -1;
         }
      } catch (Exception var17) {
      }

      return -1;
   }

   public int tryGetGoodbyeExtreme() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      byte var35;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `accounts` WHERE id = ?");
         ps.setInt(1, this.getClient().getAccID());
         rs = ps.executeQuery();
         String a = "2021-03-15 12:00:00";
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         long target = 0L;

         try {
            target = sdf.parse(a).getTime();
         } catch (Exception var29) {
         }

         if (!rs.next()) {
            return -1;
         }

         long time = rs.getTimestamp("createdat").getTime();
         long delta = target - time;
         int deltaMinute = (int)(delta / 86400000L);
         if (deltaMinute >= 0) {
            return 0;
         }

         var35 = -1;
      } catch (SQLException var31) {
         return -1;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var33 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var34 = null;
            }
         } catch (SQLException var27) {
         }
      }

      return var35;
   }

   public void exchangeSupportEquip(int itemID, int allStat, int attack, int downLevel) {
      Equip item = (Equip)MapleItemInformationProvider.getInstance().getEquipById(itemID);
      item.addStr((short)allStat);
      item.addDex((short)allStat);
      item.addInt((short)allStat);
      item.addLuk((short)allStat);
      item.addWatk((short)attack);
      item.addMatk((short)attack);
      item.setDownLevel((byte)downLevel);
      item.setCHUC(10);
      item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
      item.setUpgradeSlots((byte)0);
      item.setState((byte)2);
      item.setLines((byte)3);
      if (!DBConfig.isGanglim) {
         item.setState((byte)19);
         item.setPotential1(40086);
         item.setPotential2(30086);
         item.setPotential3(30086);
      }

      MapleInventoryManipulator.addFromDrop(this.c, item, false);
   }

   public static Object[] add(Object[] arr, Object... elements) {
      Object[] tempArr = new Object[arr.length + elements.length];
      System.arraycopy(arr, 0, tempArr, 0, arr.length);
      System.arraycopy(elements, 0, tempArr, arr.length, elements.length);
      return tempArr;
   }

   public void sendDimensionGate(String text) {
      if (this.lastMsg <= -1) {
         if (text.contains("#L")) {
            this.sendSimple(text);
         } else {
            this.c.getSession().writeAndFlush(CField.NPCPacket.getNPCTalk(this.id, (byte)19, text, "00 00", (byte)0));
            this.lastMsg = 0;
         }
      }
   }

   public String getCharacterList(int accountid) {
      String ret = "";
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ?");
         ps.setInt(1, accountid);
         rs = ps.executeQuery();

         while (rs.next()) {
            ret = ret + "#L" + rs.getInt("id") + "#" + rs.getString("name") + "\r\n";
         }

         rs.close();
         ps.close();
         con.close();
      } catch (SQLException var24) {
         var24.printStackTrace();
      } finally {
         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var21) {
               var21.printStackTrace();
            }
         }

         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var20) {
               var20.printStackTrace();
            }
         }
      }

      return ret;
   }

   public boolean setZodiacGrade(int grade) {
      if (this.c.getPlayer().getKeyValue(190823, "grade") >= grade) {
         return false;
      } else {
         this.c.getPlayer().setKeyValue(190823, "grade", String.valueOf(grade));
         this.c.getPlayer().getMap().broadcastMessage(this.c.getPlayer(), CField.onZodiacRankInfo(this.c.getPlayer().getId(), grade), true);
         this.c.getSession().writeAndFlush(CField.playSE("Sound/MiniGame.img/Result_Yut"));
         this.showEffect(false, "Effect/CharacterEff.img/gloryonGradeup");
         return true;
      }
   }

   public static void writeLog(String path, String data, boolean writeafterend) {
      try {
         File fFile = new File(path);
         if (!fFile.exists()) {
            fFile.createNewFile();
         }

         FileOutputStream out = new FileOutputStream(path, true);
         long time = System.currentTimeMillis();
         SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         String str = dayTime.format(new Date(time));
         String msg = str + " | " + data;
         out.write(msg.getBytes());
         out.close();
         out.flush();
      } catch (IOException var10) {
         var10.printStackTrace();
      }
   }

   public void updateChar() {
      Field currentMap = this.c.getPlayer().getMap();
      currentMap.removePlayer(this.c.getPlayer());
      currentMap.addPlayer(this.c.getPlayer());
   }

   public String getScript() {
      return this.script;
   }

   public void addConsumeLog(int itemID, String message) {
      LoggingManager.putLog(new ConsumeLog(this.c.getPlayer(), 2432098, new StringBuilder(message)));
   }

   public void addEnchantLog(int itemID, int equipItemID, long serialNumber, int type, int result, String log) {
      LoggingManager.putLog(new EnchantLog(this.c.getPlayer(), itemID, equipItemID, serialNumber, type, result, new StringBuilder(log)));
   }

   public void addCustomLog(int type, String message) {
      LoggingManager.putLog(
         new CustomLog(this.c.getPlayer().getName(), this.c.getAccountName(), this.c.getPlayer().getId(), this.c.getAccID(), type, new StringBuilder(message))
      );
   }

   public void setSaveQuestInfoFlag() {
      this.c.getPlayer().setSaveFlag(this.c.getPlayer().getSaveFlag() | CharacterSaveFlag.QUEST_INFO.getFlag());
   }

   public boolean inBoss() {
      return this.getPlayer().getMap().getFieldSetInstance() != null;
   }

   public String showMiniGameWaitList(int gameNum) {
      int fieldId = 0;
      StringBuilder text = new StringBuilder("\t현재 대기열에 있는 플레이어입니다.\r\n\r\n");
      boolean found = false;
      if (gameNum == 7) {
         fieldId = 993189800;
      } else if (gameNum == 8) {
         fieldId = 993189400;
      } else if (gameNum == 9) {
         fieldId = 993195100;
      } else if (gameNum == 10) {
         fieldId = 993189600;
      }

      Map<Integer, WaitQueue> queueMap = Center.GameWaitQueue.retrieveAllQueues();

      for (Entry<Integer, WaitQueue> waitQueue : queueMap.entrySet()) {
         if (fieldId == waitQueue.getValue().getFieldID()) {
            for (MapleCharacter chr : waitQueue.getValue().getPlayers()) {
               text.append(" - 플레이어 이름 : ").append(chr.getName()).append("\r\n");
               found = true;
            }
         }
      }

      if (!found) {
         text = new StringBuilder("현재 대기열에 있는 인원이 없습니다.");
      }

      return text.toString();
   }

   public int loadWaitQueue(int fieldId) {
      Map<Integer, WaitQueue> queueMap = Center.GameWaitQueue.retrieveAllQueues();

      for (Entry<Integer, WaitQueue> waitQueue : queueMap.entrySet()) {
         if (fieldId == waitQueue.getValue().getFieldID()) {
            return waitQueue.getValue().getPlayers().size();
         }
      }

      return 0;
   }

   public void resetZeroSkills() {
      for (Entry<Skill, SkillEntry> entry : this.c.getPlayer().getSkills().entrySet()) {
         Skill skill = entry.getKey();
         int skillid = skill.getId();
         int skillbase = skillid / 10000;
         if (!skill.isInvisible() && GameConstants.isJobCode(skillbase) && !GameConstants.isNovice(skillbase)) {
            this.c.getPlayer().changeSingleSkillLevel(skill, 0, (byte)skill.getMasterLevel());
         }
      }

      this.c.getPlayer().setRemainingSp(0, 0);
      this.c.getPlayer().setRemainingSp(0, 1);
      this.c.getPlayer().gainSP(310, 0);
      this.c.getPlayer().gainSP(310, 1);
   }

   public boolean skipIntro() {
      return DBConfig.isGanglim && this.c.getPlayer().getParty() != null && this.c.getPlayer().getParty().getLeader().isSkipIntro();
   }

   private void sendHairColorChange(boolean android, boolean dressUp, int[] hairs) {
      int flag = 0;
      long androidID = 0L;
      if (dressUp) {
         flag = 1;
      }

      if (GameConstants.isZero(this.c.getPlayer().getJob()) && this.c.getPlayer().getZeroInfo().isBeta()) {
         flag = 2;
      }

      if (android) {
         flag = 100;
         if (this.c.getPlayer().getAndroid() != null) {
            androidID = this.c.getPlayer().getAndroid().getUniqueId();
         }
      }

      this.c.getPlayer().setCodyType(flag);
      this.c.getPlayer().setCodyColors(hairs);
      int[] copy = new int[hairs.length];

      for (int i = 0; i < hairs.length; i++) {
         copy[i] = hairs[i] % 10;
      }

      this.c.getPlayer().send(CField.NPCPacket.sendCodyColorChange(flag, 5151036, copy, androidID));
   }

   private void sendLensColorChange(boolean android, boolean dressUp, int[] lenses) {
      int flag = 0;
      long androidID = 0L;
      if (dressUp) {
         flag = 1;
      }

      if (GameConstants.isZero(this.c.getPlayer().getJob()) && this.c.getPlayer().getZeroInfo().isBeta()) {
         flag = 2;
      }

      if (android) {
         flag = 100;
         if (this.c.getPlayer().getAndroid() != null) {
            androidID = this.c.getPlayer().getAndroid().getUniqueId();
         }
      }

      this.c.getPlayer().setCodyType(flag);
      this.c.getPlayer().setCodyColors(lenses);
      int[] copy = new int[lenses.length];

      for (int i = 0; i < lenses.length; i++) {
         copy[i] = lenses[i] / 100 % 10;
      }

      this.c.getPlayer().send(CField.NPCPacket.sendCodyColorChange(flag, 5152111, copy, androidID));
   }

   private void sendSkinColorChange(boolean android, boolean dressUp, int[] skins) {
      int flag = 0;
      long androidID = 0L;
      if (dressUp) {
         flag = 1;
      }

      if (GameConstants.isZero(this.c.getPlayer().getJob()) && this.c.getPlayer().getZeroInfo().isBeta()) {
         flag = 2;
      }

      if (android) {
         flag = 100;
         if (this.c.getPlayer().getAndroid() != null) {
            androidID = this.c.getPlayer().getAndroid().getUniqueId();
         }
      }

      this.c.getPlayer().setCodyType(flag);
      this.c.getPlayer().setCodyColors(skins);
      this.c.getPlayer().send(CField.NPCPacket.sendCodyColorChange(flag, 5153015, skins, androidID));
   }

   public void sendHairColorChange(boolean dressUp, int[] hairs) {
      this.sendHairColorChange(false, dressUp, hairs);
   }

   public void sendLensColorChange(boolean dressUp, int[] lenses) {
      this.sendLensColorChange(false, dressUp, lenses);
   }

   public void sendSkinColorChange(boolean dressUp, int[] skins) {
      this.sendSkinColorChange(false, dressUp, skins);
   }

   public void sendAndroidHairColorChange(int[] hairs) {
      this.sendHairColorChange(true, false, hairs);
   }

   public void sendAndroidLensColorChange(int[] lenses) {
      this.sendLensColorChange(true, false, lenses);
   }

   public void sendAndroidSkinColorChange(int[] skins) {
      this.sendSkinColorChange(true, false, skins);
   }
}
