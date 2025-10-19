package commands;

import constants.AutoHottimeManager;
import constants.GameConstants;
import constants.HottimeItemManager;
import constants.ServerConstants;
import constants.devtempConstants.MapleAutoNotice;
import constants.devtempConstants.MapleDailyGiftInfo;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import database.loader.CharacterSaveFlag2;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import network.SendPacketOpcode;
import network.auction.AuctionServer;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.PacketHelper;
import network.shop.CashShopServer;
import objects.androids.Android;
import objects.context.MonsterCollection;
import objects.context.guild.GuildPacket;
import objects.effect.Effect;
import objects.effect.child.TextEffect;
import objects.fields.EliteMobEvent;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.Portal;
import objects.fields.RandomPortal;
import objects.fields.RandomPortalGameType;
import objects.fields.RandomPortalType;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.child.etc.Field_EventRabbit;
import objects.fields.child.lucid.Field_LucidBattlePhase2;
import objects.fields.child.slime.Field_GuardianAngelSlime;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.ReactorFactory;
import objects.fields.gameobject.ReactorStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.Spawns;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.item.Equip;
import objects.item.IntensePowerCrystal;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.AntiMacroType;
import objects.users.MapleCabinet;
import objects.users.MapleCabinetItem;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleCoolDownValueHolder;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AdminClient;
import objects.utils.CurrentTime;
import objects.utils.HexTool;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.StringUtil;
import objects.utils.Timer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;
import scripting.EventInstanceManager;
import scripting.NPCScriptManager;

public class DebugCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception {
      MapleCharacter player = c.getPlayer();
      if (splitted[0].equals("퀘스트초기화")) {
         MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
      } else if (splitted[0].equals("가까운포탈")) {
         Portal portal = player.getMap().findClosestSpawnpoint(player.getPosition());
         c.getPlayer().dropMessage(6, portal.getName() + " 포탈아이디: " + portal.getId() + " 스크립트: " + portal.getScriptName());
      } else if (splitted[0].equals("초대장")) {
         PacketEncoder p = new PacketEncoder();
         GuildPacket.InviteGuild g = new GuildPacket.InviteGuild(1, "테스트", 1, "이름", 1, 112, 3, 3);
         g.encode(p);
         c.getPlayer().send(p.getPacket());
      } else if (splitted[0].equals("스크립트실행")) {
         NPCScriptManager.getInstance().dispose(c);
         NPCScriptManager.getInstance().start(c, 2003, splitted[1], true);
      } else if (splitted[0].equals("소환개체")) {
         c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
      } else if (splitted[0].equals("쓰레드")) {
         Thread[] threads = new Thread[Thread.activeCount()];
         Thread.enumerate(threads);
         String filter = "";
         if (splitted.length > 1) {
            filter = splitted[1];
         }

         for (int i = 0; i < threads.length; i++) {
            String tstring = threads[i].toString();
            if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
               c.getPlayer().dropMessage(6, i + ": " + tstring);
            }
         }
      } else if (splitted[0].equals("경로추적")) {
         if (splitted.length < 1) {
            throw new IllegalCommandSyntaxException(1);
         }

         Thread[] threads = new Thread[Thread.activeCount()];
         Thread.enumerate(threads);
         Thread t = threads[Integer.parseInt(splitted[1])];
         c.getPlayer().dropMessage(6, t.toString() + ":");

         for (StackTraceElement elem : t.getStackTrace()) {
            c.getPlayer().dropMessage(6, elem.toString());
         }
      } else if (splitted[0].equals("겟찰")) {
         c.getSession().writeAndFlush(CWvsContext.charInfo(player));
         player.getMap().removePlayer(player);
         player.getMap().addPlayer(player);
      } else if (splitted[0].equals("드롭토글")) {
         player.getMap().toggleDrops();
      } else if (splitted[0].equalsIgnoreCase("리액터소환")) {
         ReactorStats reactorSt = ReactorFactory.getReactor(Integer.parseInt(splitted[1]));
         Reactor reactor = new Reactor(reactorSt, Integer.parseInt(splitted[1]));
         reactor.setDelay(-1);
         reactor.setPosition(c.getPlayer().getPosition());
         c.getPlayer().getMap().spawnReactor(reactor);
      } else if (splitted[0].equals("리액터치기")) {
         c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
      } else if (!splitted[0].equals("리액터목록") && !splitted[0].equals("현재맵리액터")) {
         if (splitted[0].equals("리액터파괴")) {
            Field map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(
               c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR)
            );
            if (splitted[1].equals("모두")) {
               for (MapleMapObject reactorL : reactors) {
                  Reactor reactor2l = (Reactor)reactorL;
                  c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
               }
            } else {
               c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
         } else if (splitted[0].equals("리액터초기화")) {
            c.getPlayer().getMap().resetReactors();
         } else if (splitted[0].equals("리액터설정")) {
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
         } else if (splitted[0].equals("드롭삭제")) {
            for (MapleMapObject ix : c.getPlayer()
               .getMap()
               .getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM))) {
               c.getPlayer().getMap().removeMapObject(ix);
               c.getPlayer().getMap().broadcastMessage(CField.removeItemFromMap(ix.getObjectId(), 0, 0), ix.getPosition());
            }
         } else if (splitted[0].equals("경험치배율")) {
            if (splitted.length > 1) {
               int rate = Integer.parseInt(splitted[1]);
               c.getChannelServer().setExpRate(rate);
               c.getPlayer().dropMessage(6, "경험치 배율이 " + rate + "x 배로 수정되었습니다.");
            } else {
               c.getPlayer().dropMessage(6, "사용법: !경험치배율 <숫자>");
            }
         } else if (splitted[0].equals("메소배율")) {
            if (splitted.length > 1) {
               int rate = Integer.parseInt(splitted[1]);
               c.getChannelServer().setMesoRate(rate);
               c.getPlayer().dropMessage(6, "메소 배율이 " + rate + "x 배로 수정되었습니다.");
            } else {
               c.getPlayer().dropMessage(6, "사용법: !메소배율 <숫자>");
            }
         } else if (splitted[0].equals("모두튕")) {
            for (GameServer cs : GameServer.getAllInstances()) {
               cs.getPlayerStorage().disconnectAll();
            }
         } else if (splitted[0].equals("현재맵엔피시")) {
            Field map = c.getPlayer().getMap();
            c.getPlayer().dropMessage(5, "현재 맵 : " + map.getId() + " - " + map.getStreetName() + " : " + map.getMapName());

            for (MapleMapObject mo : map.getAllNPCs()) {
               c.getPlayer().dropMessage(6, ((MapleNPC)mo).getId() + " : " + ((MapleNPC)mo).getName());
               c.getPlayer()
                  .dropMessage(
                     6,
                     "좌표 (x: "
                        + mo.getPosition().x
                        + ", y: "
                        + mo.getPosition().y
                        + ", fh: "
                        + ((MapleNPC)mo).getFh()
                        + ", f: "
                        + ((MapleNPC)mo).getF()
                        + ", rx0: "
                        + ((MapleNPC)mo).getRx0()
                        + ", rx1: "
                        + ((MapleNPC)mo).getRx1()
                        + "), objectID: "
                        + mo.getObjectId()
                  );
            }
         } else if (splitted[0].equals("현재맵포탈")) {
            Field map = c.getPlayer().getMap();
            c.getPlayer().dropMessage(5, "현재 맵 : " + map.getId() + " - " + map.getStreetName() + " : " + map.getMapName());

            for (Portal mp : map.getPortals()) {
               c.getPlayer().dropMessage(6, mp.getId() + " : " + mp.getName() + " 스크립트 : " + mp.getScriptName() + " 목적지 : " + mp.getTarget());
            }
         } else if (splitted[0].equals("소울게이지")) {
            if (splitted.length < 2) {
               return;
            }

            int gauge = Integer.parseInt(splitted[1]);
            c.getPlayer().setSoulCount((short)gauge);
            c.getPlayer().temporaryStatResetBySkillID(c.getPlayer().getEquippedSoulSkill());
            SecondaryStatEffect skill = SkillFactory.getSkill(c.getPlayer().getEquippedSoulSkill())
               .getEffect(c.getPlayer().getSkillLevel(c.getPlayer().getEquippedSoulSkill()));
            if (c.getPlayer().getSoulCount() >= skill.getSoulMPCon()) {
               c.getPlayer()
                  .temporaryStatSet(c.getPlayer().getEquippedSoulSkill(), Integer.MAX_VALUE, SecondaryStatFlag.FullSoulMP, c.getPlayer().getSoulCount());
            }

            c.getPlayer().temporaryStatSet(c.getPlayer().getEquippedSoulSkill(), Integer.MAX_VALUE, SecondaryStatFlag.SoulMP, gauge);
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), false));
         } else if (splitted[0].equals("아란콤보")) {
            c.getPlayer().setLastCombo(System.currentTimeMillis());
            c.getPlayer().setCombo(Short.parseShort(splitted[1]));
            c.getSession().writeAndFlush(CField.aranCombo(Short.parseShort(splitted[1])));
         } else if (splitted[0].equals("빙결")) {
            TextEffect e = new TextEffect(-1, "[데일리 이벤트] 데일리 이벤트가 종료되었습니다.\r\n데일리 이벤트는 매일 오후 8시 ~ 10시에 진행됩니다.", 50, 5000, 4, 0);
            Center.Broadcast.broadcastMessage(e.encodeForLocal());
         } else if (splitted[0].equals("몹스탯")) {
            int type = Randomizer.isSuccess(20) ? 3 : 2;
            int gameType = Integer.parseInt(splitted[1]);
            RandomPortalType portalType = RandomPortalType.get(type);
            RandomPortalGameType portalGameType = RandomPortalGameType.get(gameType);
            RandomPortal portal = new RandomPortal(portalType, Randomizer.rand(1000000, 9999999), player.getTruePosition(), player.getId(), portalGameType);
            int totalLevel = 0;
            long totalExp = 0L;
            long totalHp = 0L;
            int count = 0;

            for (Spawns s : player.getMap().getClosestSpawns(player.getTruePosition(), 30)) {
               int mobTemplateID = s.getMonster().getId();
               MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
               totalLevel += mob.getStats().getLevel();
               totalExp += mob.getStats().getExp();
               totalHp += mob.getStats().getMaxHp();
               count++;
            }

            totalLevel /= count;
            totalExp /= count;
            totalHp /= count;
            portal.setMobAvgHp(totalHp);
            portal.setMobAvgExp(totalExp);
            portal.setMobAvgLevel(totalLevel);
            player.updateOneInfo(26022, "exp", String.valueOf(totalExp));
            player.updateOneInfo(26022, "map", String.valueOf(player.getMapId()));
            if (type == 2) {
               player.send(CWvsContext.getScriptProgressMessage("현상금 사냥꾼의 포탈이 등장했습니다!"));
            } else {
               player.send(CWvsContext.getScriptProgressMessage("불꽃늑대의 소굴로 향하는 포탈이 등장했습니다!"));
            }

            player.setRandomPortal(portal);
            player.setRandomPortalSpawnedTime(System.currentTimeMillis());
            player.send(CField.randomPortalCreated(portal));
         } else if (splitted[0].equals("몹스탯2")) {
            for (int ix = 1; ix <= 3; ix++) {
               Field target = GameServer.getInstance(ix).getMapFactory().getMap(910010000);
               if (target == null) {
                  return;
               }

               Field_EventRabbit f = (Field_EventRabbit)target;
               if (f != null) {
                  f.resetFully(false);
                  MapleMonster mob = MapleLifeFactory.getMonster(9500006 + Randomizer.rand(0, 1));
                  mob.setHp(50000000L);
                  mob.getStats().setHp(50000000L);
                  target.spawnMonsterOnGroundBelow(mob, new Point(-82, 153));
                  f.setRabbitSpawnedTime(System.currentTimeMillis());
               }
            }

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                  if (chr != null) {
                     int type = 1;
                     int gameType = 9;
                     chr.removeRandomPortal();
                     RandomPortalType portalType = RandomPortalType.get(type);
                     RandomPortalGameType portalGameType = RandomPortalGameType.get(gameType);
                     Point pos = chr.getMap().calcDropPos(chr.getTruePosition(), chr.getPosition());
                     RandomPortal portal = new RandomPortal(portalType, Randomizer.rand(1000000, 9999999), pos, chr.getId(), portalGameType);
                     chr.updateOneInfo(15142, "gameType", "9");
                     chr.send(CField.addPopupSay(9062000, 5000, "추석 이벤트 소굴이 열렸습니다. 격파되기 전에 입장해야 보상을 획득할 수 있습니다.", ""));
                     chr.send(CWvsContext.getScriptProgressMessage("추석 이벤트 소굴이 열렸습니다. 격파되기 전에 입장해야 보상을 획득할 수 있습니다."));
                     chr.setRandomPortal(portal);
                     chr.setRandomPortalSpawnedTime(System.currentTimeMillis());
                     chr.send(CField.randomPortalCreated(portal));
                  }
               }
            }
         } else if (splitted[0].equals("핫타임이벤트리셋")) {
            ServerConstants.expFeverRate = 1.0;
            ServerConstants.dropFeverRate = 1.0;
            ServerConstants.mesoFeverRate = 1.0;
            Center.cancelAutoFeverTask();
            AutoHottimeManager.loadAutoHottime();
            Center.registerAutoFever();
            c.getPlayer().dropMessage(5, "핫타임 이벤트가 리셋되었습니다.");
         } else if (splitted[0].equals("핫타임아이템리셋")) {
            Center.cancelAutoHotTimeItemTask();
            HottimeItemManager.loadHottimeItem();
            Center.registerHottimeItem();
            c.getPlayer().dropMessage(5, "핫타임 아이템이 리셋되었습니다.");
         } else if (splitted[0].equals("낚시1")) {
            player.tryAntiMacro(AntiMacroType.Auto, null);
         } else if (splitted[0].equals("데일리초기화")) {
            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chrx : cs.getPlayerStorage().getAllCharacters()) {
                  if (chrx != null) {
                     MapleDailyGiftInfo gift = chrx.getDailyGift();
                     gift.setDailyDay(0);
                     gift.setDailyCount(0);
                     chrx.send(CField.getDailyGiftRecord("count=0;day=0;date=" + CurrentTime.getCurrentTime2()));
                     chrx.updateInfoQuest(16700, "count=0;day=0;date=" + CurrentTime.getCurrentTime2());
                  }
               }
            }

            PreparedStatement ps = null;

            try (Connection con = DBConnection.getConnection()) {
               ps = con.prepareStatement("DELETE FROM `dailygift`");
               ps.executeUpdate();
            } catch (SQLException var151) {
               var151.printStackTrace();
            } finally {
               try {
                  if (ps != null) {
                     ps.close();
                     PreparedStatement var201 = null;
                  }
               } catch (SQLException var118) {
                  var118.printStackTrace();
               }
            }
         } else if (splitted[0].equals("관리기실행")) {
            AdminClient.main();
         } else if (splitted[0].equals("쿨타임초기화")) {
            for (MapleCoolDownValueHolder holder : c.getPlayer().getCooldowns()) {
               c.getPlayer().removeCooldown(holder.skillId);
               c.getSession().writeAndFlush(CField.skillCooldown(holder.skillId, 0));
            }
         } else if (splitted[0].equals("데일리해제")) {
            ServerConstants.dailyEventType = null;
            c.getPlayer().dropMessage(5, "데일리 이벤트가 강제 종료되었습니다.");
            TextEffect e = new TextEffect(-1, "[데일리 이벤트] 데일리 이벤트가 종료되었습니다.\r\n데일리 이벤트는 매일 오후 8시 ~ 10시에 진행됩니다.", 50, 5000, 4, 0);
            Center.Broadcast.broadcastMessage(e.encodeForLocal());
            Center.Broadcast.broadcastMessage(CField.chatMsg(3, "[데일리 이벤트] 데일리 이벤트가 종료되었습니다. 데일리 이벤트는 매일 오후 8시 ~ 10시에 진행됩니다."));
         } else if (splitted[0].equals("스킬레벨체크")) {
            if (splitted.length < 2) {
               c.getPlayer().dropMessage(5, "문법 오류! /스킬레벨체크 skillID");
               return;
            }

            int skillID = 0;

            try {
               skillID = Integer.valueOf(splitted[1]);
            } catch (NumberFormatException var139) {
               String searchName = "";

               for (int ix = 1; ix < splitted.length; ix++) {
                  String str = splitted[ix];
                  searchName = searchName + str;
                  if (ix < splitted.length - 1) {
                     searchName = searchName + " ";
                  }
               }

               MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
               MapleData data = dataProvider.getData("Skill.img");
               List<Pair<Integer, String>> skillPairList = new LinkedList<>();

               for (MapleData skillIdData : data.getChildren()) {
                  try {
                     skillPairList.add(
                        new Pair<>(Integer.parseInt(skillIdData.getName()), MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME"))
                     );
                  } catch (NumberFormatException var138) {
                  }
               }

               for (Pair<Integer, String> skillPair : skillPairList) {
                  if (skillPair.getRight().toLowerCase().equals(searchName)) {
                     skillID = skillPair.left;
                     break;
                  }
               }
            }

            if (skillID == 0) {
               c.getPlayer().dropMessage(5, "입력된 스킬(" + skillID + ")은 존재하지 않는 스킬입니다.");
               return;
            }

            int skillLevel = c.getPlayer().getTotalSkillLevel(skillID);
            Skill skill = SkillFactory.getSkill(skillID);
            if (skill == null) {
               c.getPlayer().dropMessage(5, "입력된 스킬(" + skillID + ")은 존재하지 않는 스킬입니다.");
               return;
            }

            c.getPlayer().dropMessage(5, skillID + "(" + SkillFactory.getSkill(skillID).getName() + ") 스킬의 레벨은 " + skillLevel + "입니다.");
         } else if (splitted[0].equals("캐시보너스설정")) {
            int setrate = 0;

            try {
               setrate = Integer.parseInt(splitted[1]);
            } catch (Exception var137) {
               c.getPlayer().dropMessage(5, "캐시 배율 설정에 실패하였습니다. 숫자를 입력한 것이 맞는지 확인해주세요.");
               return;
            }

            ServerConstants.cashPlusRate = setrate;
            c.getPlayer().dropMessage(5, "캐시보너스를 " + setrate + "%(총" + (100 + setrate) + "%) 으로 설정하였습니다.");
         } else if (splitted[0].equals("캐시보너스확인")) {
            int cashrate = ServerConstants.cashPlusRate;
            c.getPlayer().dropMessage(5, "현재 캐시보너스는 " + cashrate + "%(총" + (100 + cashrate) + "%) 입니다.");
         } else if (splitted[0].equals("시작맵") || splitted[0].equals("StartMap")) {
            try {
               int startMap = Integer.parseInt(splitted[1]);
               ServerConstants.StartMap = startMap;
               c.getPlayer().dropMessage(5, "시작 맵코드가 " + startMap + "으로 설정되었습니다.");
            } catch (Exception var136) {
               c.getPlayer().dropMessage(5, "설정 오류 발생 숫자입력이 맞는지 확인해주세요.");
            }
         } else if (splitted[0].equals("타운맵") || splitted[0].equals("TownMap")) {
            try {
               int townMap = Integer.parseInt(splitted[1]);
               ServerConstants.TownMap = townMap;
               c.getPlayer().dropMessage(5, "타운 맵코드가 " + townMap + "으로 설정되었습니다.");
            } catch (Exception var135) {
               c.getPlayer().dropMessage(5, "설정 오류 발생 숫자입력이 맞는지 확인해주세요.");
            }
         } else if (splitted[0].equals("BGM리로드")) {
            GameConstants.loadBGM();
            c.getPlayer().dropMessage(5, GameConstants.getBGMSize() + "개의 BGM 리스트를 불러왔습니다.");
         } else if (splitted[0].equals("맵BGM초기화")) {
            if (DBConfig.isGanglim) {
               for (GameServer gs : GameServer.getAllInstances()) {
                  gs.getMapFactory().getMap(c.getPlayer().getMapId()).clearMusicList();
               }

               c.getPlayer().dropMessage(5, "서버내 모든 채널 같은 맵에 등록된 BGM리스트를 초기화했습니다.");
            } else {
               c.getPlayer().getMap().clearMusicList();
               c.getPlayer().dropMessage(5, "맵에 등록된 BGM리스트를 초기화했습니다.");
            }
         } else if (splitted[0].equals("지엠해제")) {
            c.getPlayer().setGMLevel((byte)0);
            c.getPlayer().dropMessage(5, "지엠 풀었음");
         } else if (splitted[0].equals("퀘스트시작")) {
            try {
               int questid = Integer.parseInt(splitted[1]);
               c.getPlayer().updateOneInfo(questid, "clear", "");
               c.getPlayer().updateQuest(new MapleQuestStatus(MapleQuest.getInstance(questid), 0));
               c.getPlayer().updateQuest(new MapleQuestStatus(MapleQuest.getInstance(questid), 1));
            } catch (Exception var134) {
               c.getPlayer().dropMessage(5, "오류발생.");
            }
         } else if (splitted[0].equals("협동포인트")) {
            if (!DBConfig.isGanglim) {
               try {
                  int togetherPoint = Integer.parseInt(splitted[1]);
                  c.getPlayer().gainTogetherPoint(togetherPoint);
                  c.getPlayer().dropMessage(5, "협포 얻음 총 " + c.getPlayer().getTogetherPoint());
               } catch (Exception var133) {
                  c.getPlayer().dropMessage(5, "숫자만 가능");
               }
            }
         } else if (splitted[0].equals("채팅금지")) {
            if (splitted.length < 3) {
               c.getPlayer().dropMessage(5, "<문법오류> !채팅금지 대상 기간(일)");
               return;
            }

            String targetName = splitted[1];
            int days = 0;

            try {
               days = Integer.parseInt(splitted[2]);
            } catch (Exception var132) {
               c.getPlayer().dropMessage(5, "기간은 숫자만 가능");
               return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long bantime = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000;
            boolean found = false;

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chrxx : cs.getPlayerStorage().getAllCharacters()) {
                  if (chrxx.getName().equals(targetName)) {
                     chrxx.getClient().setAccountChatBanTime(bantime);
                     c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                     found = true;
                     break;
                  }
               }
            }

            if (!found) {
               for (MapleCharacter chrxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                  if (chrxxx.getName().equals(targetName)) {
                     chrxxx.getClient().setAccountChatBanTime(bantime);
                     c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                     found = true;
                     break;
                  }
               }
            }

            if (!found) {
               for (MapleCharacter chrxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                  if (chrxxxx.getName().equals(targetName)) {
                     chrxxxx.getClient().setAccountChatBanTime(bantime);
                     c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                     found = true;
                     break;
                  }
               }
            }

            boolean f = false;
            if (!found) {
               try (Connection con = DBConnection.getConnection()) {
                  PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
                  ps.setString(1, targetName);
                  ResultSet rs = ps.executeQuery();

                  while (rs.next()) {
                     boolean ff = false;

                     for (GameServer cs : GameServer.getAllInstances()) {
                        for (MapleCharacter chrxxxxx : cs.getPlayerStorage().getAllCharacters()) {
                           if (chrxxxxx.getAccountID() == rs.getInt("accountid")) {
                              chrxxxxx.getClient().setAccountChatBanTime(bantime);
                              c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                              ff = true;
                              f = true;
                              break;
                           }
                        }
                     }

                     if (!ff) {
                        for (MapleCharacter chrxxxxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                           if (chrxxxxxx.getAccountID() == rs.getInt("accountid")) {
                              chrxxxxxx.getClient().setAccountChatBanTime(bantime);
                              c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                              ff = true;
                              f = true;
                              break;
                           }
                        }
                     }

                     if (!ff) {
                        for (MapleCharacter chrxxxxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                           if (chrxxxxxxx.getAccountID() == rs.getInt("accountid")) {
                              chrxxxxxxx.getClient().setAccountChatBanTime(bantime);
                              c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                              ff = true;
                              f = true;
                              break;
                           }
                        }
                     }

                     if (!ff) {
                        try (PreparedStatement ps2 = con.prepareStatement("UPDATE `accounts` SET `chat_ban_time` = ? WHERE `id` = ?")) {
                           Timestamp ts = new Timestamp(bantime);
                           ps2.setTimestamp(1, ts);
                           ps2.setInt(2, rs.getInt("accountid"));
                           ps2.executeUpdate();
                           c.getPlayer().dropMessage(5, "해당 캐릭터의 계정 " + sdf.format(bantime) + " 까지 채팅정지 완료");
                           f = true;
                           ps2.close();
                        }
                     }
                  }

                  rs.close();
                  ps.close();
               } catch (SQLException var142) {
               }
            }

            if (!f) {
               c.getPlayer().dropMessage(5, "대상을 찾지 못했습니다.");
            }
         } else if (splitted[0].equals("패킷저장")) {
            if (c.getDebugPacketSend()) {
               c.setDebugPacketSend(false);
               c.getPlayer().dropMessage(5, "패킷저장 취소");
            } else {
               c.setDebugPacketSend(true);
               c.setDebugPacketSendTime(System.currentTimeMillis() + 3600000L);
               c.getPlayer().dropMessage(5, "채널이동 전까지 1시간동안 패킷 저장");
            }
         } else if (splitted[0].equals("자동저장길드")) {
            if (ServerConstants.royalGuildSave) {
               ServerConstants.royalGuildSave = false;
               c.getPlayer().dropMessage(5, "자동저장시 길드저장 상시화가 해제되었습니다.");
            } else {
               ServerConstants.royalGuildSave = true;
               c.getPlayer().dropMessage(5, "자동저장시 길드저장 상시화가 설정되었습니다.");
            }
         } else if (splitted[0].equals("CRC온오프")) {
            if (ServerConstants.enableCRCBin) {
               ServerConstants.enableCRCBin = false;
               c.getPlayer().dropMessage(5, "CRC 감지가 해제되었습니다.");
            } else {
               ServerConstants.enableCRCBin = true;
               c.getPlayer().dropMessage(5, "CRC 감지가 설정되었습니다.");
            }
         } else if (splitted[0].equals("솔에르다")) {
            int count = 0;

            try {
               count = Integer.parseInt(splitted[1]);
            } catch (Exception var129) {
               c.getPlayer().dropMessage(5, "오류발생");
               return;
            }

            MapleCharacter chrxxxxxxxx = c.getPlayer();
            chrxxxxxxxx.gainSolErda(count);
         } else if (splitted[0].equals("네거티브어레이")) {
            if (splitted[1].equals("초기화")) {
               ServerConstants.fuckNegativeArray.clear();
               return;
            }

            int fucknegative = 0;

            try {
               fucknegative = Integer.parseInt(splitted[1]);
            } catch (Exception var128) {
               c.getPlayer().dropMessage(5, "오류발생 숫자만 가능");
               return;
            }

            if (fucknegative == 0) {
               return;
            }

            if (ServerConstants.fuckNegativeArray.contains(fucknegative)) {
               c.getPlayer().dropMessage(5, "이미 등록된 음수체크");
               return;
            }

            ServerConstants.fuckNegativeArray.add(fucknegative);
         } else if (splitted[0].equals("공격디버그")) {
            ServerConstants.DEBUG_DAMAGE = !ServerConstants.DEBUG_DAMAGE;
            player.dropMessage(5, "공격디버그 " + (ServerConstants.DEBUG_DAMAGE ? "ON" : "OFF"));
         } else if (splitted[0].equals("테스트1")) {
            String file = "SendDebug.txt";
            new File(file);
            Timer.EtcTimer.getInstance().schedule(() -> {
               try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                  String line = null;

                  while (true) {
                     int opcode;
                     String packet;
                     PacketEncoder mplew;
                     while (true) {
                        if ((line = in.readLine()) == null) {
                           return;
                        }

                        if (line.contains("Inbound")) {
                           String packetFull = line.split("\\[Inbound \\]")[1];
                           opcode = Integer.parseInt(packetFull.split("\\[")[1].split("\\]")[0], 16);
                           if (opcode != 65535) {
                              packet = packetFull.split("\\]")[1].substring(1);
                              mplew = new PacketEncoder();
                              mplew.writeShort(opcode);
                              if (packet.isEmpty()) {
                                 break;
                              }

                              try {
                                 mplew.encodeBuffer(HexTool.getByteArrayFromHexString(packet));
                                 break;
                              } catch (Exception var11x) {
                                 var11x.printStackTrace();
                              }
                           }
                        }
                     }

                     player.send(mplew.getPacket());
                     player.dropMessage(5, opcode + "(" + SendPacketOpcode.getOpcodeName(opcode) + ") // " + packet);

                     try {
                        Thread.sleep(200L);
                     } catch (Exception var10x) {
                        var10x.printStackTrace();
                     }
                  }
               } catch (Exception var13x) {
                  var13x.printStackTrace();
               }
            }, 0L);
         } else if (splitted[0].equals("옵코드무시")) {
            try {
               int check = 0;
               if (splitted[1].startsWith("0x")) {
                  String hexop = splitted[1].replace("0x", "");
                  check = Integer.parseInt(hexop, 16);
               } else {
                  check = Integer.parseInt(splitted[1]);
               }

               if (check == 0) {
                  c.getPlayer().dropMessage(5, "파싱실패");
                  return;
               }

               for (int op : GameConstants.ignoreOpcode) {
                  if (op == check) {
                     c.getPlayer().dropMessage(5, "이미 등록된 무시옵코드");
                     return;
                  }
               }

               GameConstants.ignoreOpcode.add(check);
               String opname = SendPacketOpcode.getOpcodeName(check);
               c.getPlayer().dropMessage(5, check + "(" + opname + ") 옵코드가 무시등록됨");
            } catch (Exception var153) {
               c.getPlayer().dropMessage(5, "오류발생");
            }
         } else if (splitted[0].equals("옵코드무시리셋")) {
            GameConstants.ignoreOpcode.clear();
            c.getPlayer().dropMessage(4, "센드 옵코드 무시리스트가 초기화됨");
         } else if (splitted[0].equals("무릉순위테스트")) {
            c.getPlayer().send(CField.getMulungDojangRankingDisplayTest(player));
         } else if (splitted[0].equals("테스트2")) {
            for (Entry<Integer, Integer> entry : Effect.decodeList.entrySet()) {
               c.getPlayer().dropMessage(5, entry.getValue() + " | " + entry.getKey());
            }
         } else if (splitted[0].equals("테스트3")) {
            Field_LucidBattlePhase2 fx = (Field_LucidBattlePhase2)c.getPlayer().getMap();
            fx.sendLucidStainedGlassOnOff(null, true);
         } else {
            if (splitted[0].equals("팅테스트")) {
               c.getPlayer()
                  .send(
                     HexTool.getByteArrayFromHexString(
                        "57 04 63 0E 04 00 95 B4 07 00 30 51 D7 00 13 01 00 00 14 00 00 00 19 FF D7 00 04 00 00 08 00 01 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 90 01 00 00 1E 00 00 00 00 7C 7C 08 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF 00"
                     )
                  );
               c.getPlayer().dropMessage(5, "패킷전송완료5");
               return;
            }

            if (splitted[0].equals("서든미션") || splitted[0].equals("히든미션")) {
               c.getPlayer().updateInfoQuestHiddenMission(Integer.parseInt(splitted[1]));
            } else if (splitted[0].equals("클리어")) {
               c.getPlayer().checkHiddenMissionComplete(Integer.parseInt(splitted[1]));
            } else if (splitted[0].equals("패킷")) {
               if (c.getPlayer().getMap() instanceof Field_WillBattle) {
                  List<Point> posList = List.of(new Point(0, 158), new Point(0, -2020));
                  if (c.getPlayer().getMap().getAffectedAreaByMobSkill(MobSkillFactory.getMobSkill(242, 4)) == null) {
                     for (Point pos : posList) {
                        Rect rect = new Rect(-362, pos.y - 362, 204, pos.y + 20);
                        AffectedArea area = new AffectedArea(
                           rect, c.getPlayer().getId(), MobSkillFactory.getMobSkill(242, 4), pos, 0, System.currentTimeMillis() + 60000L
                        );
                        c.getPlayer().getMap().spawnMist(area);
                     }
                  }
               }
            } else if (splitted[0].equals("폭탄")) {
               MobSkillInfo skill = MobSkillFactory.getMobSkill(176, 38);
               c.getPlayer().onDamageByMobSkill(MobSkillID.DAMAGE, 38, skill, 0, 0, 1, skill.getMobSkillStatsInt(MobSkillStat.fixDamR));
            } else if (splitted[0].equals("몬컬등록")) {
               String name = StringUtil.joinStringFrom(splitted, 1);
               MonsterCollection.CollectionMobData triple = MonsterCollection.mobByName.getOrDefault(name, null);
               if (triple != null && !MonsterCollection.checkIfMobOnCollection(c.getPlayer(), triple)) {
                  MonsterCollection.setMobOnCollection(c.getPlayer(), triple);
               }
            } else if (splitted[0].equals("골렘")) {
               for (MapleMonster mob : c.getPlayer().getMap().getAllMonster()) {
                  c.getPlayer().dropMessage(5, mob.getHp() + " | " + (long)(mob.getMobMaxHp() * 0.9));
                  mob.damage(c.getPlayer(), mob.getMobMaxHp() * 9L / 500L, true);
               }
            } else if (splitted[0].equals("발판")) {
               if (c.getPlayer().getMap() instanceof Field_GuardianAngelSlime) {
                  ((Field_GuardianAngelSlime)c.getPlayer().getMap()).lastGuardianWaveTime = System.currentTimeMillis();
                  ((Field_GuardianAngelSlime)c.getPlayer().getMap()).initGuardianWave();
               }
            } else if (splitted[0].equals("UI")) {
               c.getSession().writeAndFlush(CField.UIPacket.openUI(Integer.parseInt(splitted[1])));
            } else if (splitted[0].equals("전문기술")) {
               c.getPlayer().changeProfessionLevelExp(92000000, 8, 4800, (byte)10);
            } else if (splitted[0].equals("엘몹")) {
               EliteMobEvent event = new EliteMobEvent(c.getPlayer().getMap(), System.currentTimeMillis() + 600000L, c.getPlayer().getPosition(), 1);
               c.getPlayer().getMap().setFieldEvent(event);
            } else if (splitted[0].equals("랜덤포탈")) {
               try {
                  List<Spawns> spawns = c.getPlayer().getMap().getClosestSpawns(c.getPlayer().getTruePosition(), 50);
                  Collections.shuffle(spawns);
                  Spawns spawn = spawns.stream().findAny().orElse(null);
                  Point pos = spawn.getPosition();
                  int type = Randomizer.isSuccess(20) ? 3 : 2;
                  if (type == 3) {
                     byte var10000 = 8;
                  } else {
                     Randomizer.rand(0, 7);
                  }

                  RandomPortalType portalType = RandomPortalType.get(Integer.parseInt(splitted[1]));
                  RandomPortalGameType portalGameType = RandomPortalGameType.get(Integer.parseInt(splitted[2]));
                  RandomPortal portal = new RandomPortal(portalType, Randomizer.rand(1000000, 9999999), pos, c.getPlayer().getId(), portalGameType);
                  int totalLevel = 0;
                  long totalExp = 0L;
                  long totalHp = 0L;
                  int cc = 0;

                  for (Spawns s : c.getPlayer().getMap().getClosestSpawns(c.getPlayer().getTruePosition(), 30)) {
                     int mobTemplateID = s.getMonster().getId();
                     MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
                     totalLevel += mob.getStats().getLevel();
                     totalExp += mob.getStats().getExp();
                     totalHp += mob.getStats().getMaxHp();
                     cc++;
                  }

                  totalLevel /= cc;
                  totalExp /= cc;
                  totalHp /= cc;
                  portal.setMobAvgHp(totalHp);
                  portal.setMobAvgExp(totalExp);
                  portal.setMobAvgLevel(totalLevel);
                  c.getPlayer().updateOneInfo(26022, "exp", String.valueOf(totalExp));
                  c.getPlayer().updateOneInfo(26022, "map", String.valueOf(c.getPlayer().getMapId()));
                  if (type == 2) {
                     c.getPlayer().send(CWvsContext.getScriptProgressMessage("현상금 사냥꾼의 포탈이 등장했습니다!"));
                  } else {
                     c.getPlayer().send(CWvsContext.getScriptProgressMessage("불꽃늑대의 소굴로 향하는 포탈이 등장했습니다!"));
                  }

                  c.getPlayer().setRandomPortal(portal);
                  c.getPlayer().setRandomPortalSpawnedTime(System.currentTimeMillis());
                  c.getPlayer().send(CField.randomPortalCreated(portal));
               } catch (Exception var149) {
                  var149.printStackTrace();
               }
            } else if (splitted[0].equals("밴포인트")) {
               List<MapleMapObject> objs = player.getMap().getMapObjectsInRange(player.getTruePosition(), 500000.0, Arrays.asList(MapleMapObjectType.MONSTER));
               List<MapleMonster> monsters = new ArrayList<>();

               for (int ixx = 0; ixx < 1; ixx++) {
                  int rand = Randomizer.rand(0, objs.size() - 1);
                  if (!objs.isEmpty()) {
                     monsters.add((MapleMonster)objs.get(rand));
                     objs.remove(rand);
                  }
               }

               if (monsters.size() > 0) {
                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new EnumMap<>(MobTemporaryStatFlag.class);
                  monsters.forEach(mob -> {
                     MobSkillInfo skill = new MobSkillInfo(152, 1);
                     stats.put(MobTemporaryStatFlag.P_GUARD_UP, new MobTemporaryStatEffect(MobTemporaryStatFlag.P_GUARD_UP, 1, 152, skill, true));
                     mob.applyMonsterBuff(stats, 152, 10000L, skill, Collections.EMPTY_LIST);
                  });
               }
            } else if (splitted[0].equals("무적")) {
               c.getPlayer().temporaryStatSet(1221054, Integer.MAX_VALUE, SecondaryStatFlag.indiePartialNotDamaged, 1);
               if (c.getPlayer().isInvincible()) {
                  c.getPlayer().setInvincible(false);
               } else {
                  c.getPlayer().setInvincible(true);
               }
            } else if (splitted[0].equals("몹리젠")) {
               Center.registerRespawn(Integer.parseInt(splitted[1]));
            } else if (splitted[0].equals("crate")) {
               double rate = Double.parseDouble(splitted[1]);
               ServerConstants.connectedRate = rate;
               c.getPlayer().dropMessage(5, "가동접 배율을 " + rate + "배로 수정하였습니다.");
            } else if (splitted[0].equals("윌워프")) {
               c.getPlayer().temporaryStatSet(SecondaryStatFlag.AutoChargeStack, 400021047, Integer.MAX_VALUE, 1);
            } else if (splitted[0].equals("아이템전송")) {
               try {
                  int TI = Integer.parseInt(splitted[1]);
                  int slot = Integer.parseInt(splitted[2]);
                  if (TI == 3) {
                     TI = 4;
                  } else if (TI == 4) {
                     TI = 3;
                  }

                  int quantity = Integer.parseInt(splitted[3]);
                  String targetx = splitted[4];
                  MapleCharacter p = null;

                  for (GameServer gs : GameServer.getAllInstances()) {
                     for (Field map : gs.getMapFactory().getAllMaps()) {
                        for (MapleCharacter ch : new ArrayList<>(map.getCharacters())) {
                           if (ch != null && ch.getName().equals(targetx)) {
                              p = ch;
                              break;
                           }
                        }
                     }
                  }

                  Item find = c.getPlayer().getInventory(MapleInventoryType.getByType((byte)TI)).getItem((short)slot);
                  if (find == null) {
                     c.getPlayer().dropMessage(5, "아이템을 발견하지 못했습니다.");
                  } else {
                     Item item = find.copy();
                     item.setQuantity((short)quantity);
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
                     Calendar CAL = new GregorianCalendar(Locale.KOREA);
                     String fDate = sdf.format(CAL.getTime());
                     if (p == null) {
                        PreparedStatement ps = null;
                        PreparedStatement pse = null;
                        ResultSet rs = null;

                        try (Connection con = DBConnection.getConnection()) {
                           int accountID = 0;
                           int lastIndex = 0;
                           ps = con.prepareStatement("SELECT `accountid` FROM `characters` WHERE `name` = ?");
                           ps.setString(1, targetx);
                           rs = ps.executeQuery();

                           while (rs.next()) {
                              accountID = rs.getInt("accountid");
                           }

                           rs.close();
                           ps.close();
                           if (accountID <= 0) {
                              c.getPlayer().dropMessage(5, "존재 하지 않는 유저입니다.");
                           } else {
                              for (GameServer gs : GameServer.getAllInstances()) {
                                 for (Field map : gs.getMapFactory().getAllMaps()) {
                                    for (MapleCharacter chx : new ArrayList<>(map.getCharacters())) {
                                       if (chx != null && chx.getAccountID() == accountID) {
                                          p = chx;
                                          break;
                                       }
                                    }
                                 }
                              }

                              if (p != null) {
                                 MapleCabinet cabinet = p.getCabinet();
                                 if (cabinet == null) {
                                    c.getPlayer().dropMessage(5, "해당 유저의 보관함을 열람하는 과정에서 오류가 발생하였습니다.");
                                    return;
                                 }

                                 cabinet.addCabinetItem(
                                    new MapleCabinetItem(
                                       cabinet.getNextIndex(),
                                       System.currentTimeMillis() + 604800000L,
                                       "[GM 선물]",
                                       fDate + "에 " + c.getPlayer().getName() + "이(가) 보낸 아이템입니다.",
                                       item
                                    )
                                 );
                                 p.send(CField.maplecabinetResult(8));
                                 p.setSaveFlag(c.getPlayer().getSaveFlag() | CharacterSaveFlag.CABINET.getFlag());
                                 p.dropMessage(5, "[알림] " + c.getPlayer().getName() + "으로 부터 선물이 도착하였습니다. 메이플 보관함을 통해 수령해주시기 바랍니다.");
                                 c.getPlayer().dropMessage(5, "해당 플레이어와 동일한 계정 내에 캐릭터에게 전송되었습니다.");
                              } else {
                                 ps = con.prepareStatement("SELECT `cabinet_index` FROM `cabinet_items` WHERE `accountid` = ? ORDER BY `cabinet_index` DESC");
                                 ps.setInt(1, accountID);
                                 rs = ps.executeQuery();
                                 if (rs.next()) {
                                    lastIndex = rs.getInt("cabinet_index");
                                 }

                                 ps = con.prepareStatement(
                                    "INSERT INTO `cabinet_items` (accountid, itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, `type`, sender, once_trade, cabinet_index, cabinet_expired_time, cabinet_title, cabinet_desc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                    1
                                 );
                                 pse = con.prepareStatement(
                                    "INSERT INTO `cabinet_equipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                                 );
                                 MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                 MapleInventoryType mit = MapleInventoryType.getByType((byte)(item.getItemId() / 1000000));
                                 if (item.getPosition() == -55) {
                                    return;
                                 }

                                 if (mit == MapleInventoryType.EQUIP && ii.isCash(item.getItemId())) {
                                    mit = MapleInventoryType.CASH_EQUIP;
                                 }

                                 AtomicInteger idx = new AtomicInteger(1);
                                 ps.setInt(idx.getAndIncrement(), accountID);
                                 ps.setInt(idx.getAndIncrement(), item.getItemId());
                                 ps.setInt(idx.getAndIncrement(), mit.getType());
                                 ps.setInt(idx.getAndIncrement(), item.getPosition());
                                 ps.setInt(idx.getAndIncrement(), item.getQuantity());
                                 ps.setString(idx.getAndIncrement(), item.getOwner());
                                 ps.setString(idx.getAndIncrement(), item.getGMLog());
                                 if (item.getPet() != null) {
                                    ps.setLong(idx.getAndIncrement(), Math.max(item.getUniqueId(), item.getPet().getUniqueId()));
                                 } else {
                                    ps.setLong(idx.getAndIncrement(), item.getUniqueId());
                                 }

                                 ps.setLong(idx.getAndIncrement(), item.getExpiration());
                                 ps.setInt(idx.getAndIncrement(), item.getFlag());
                                 ps.setByte(idx.getAndIncrement(), (byte)8);
                                 ps.setString(idx.getAndIncrement(), item.getGiftFrom());
                                 ps.setInt(idx.getAndIncrement(), item.getOnceTrade());
                                 ps.setInt(idx.getAndIncrement(), lastIndex + 1);
                                 ps.setLong(idx.getAndIncrement(), System.currentTimeMillis() + 604800000L);
                                 ps.setString(idx.getAndIncrement(), "[GM 선물]");
                                 ps.setString(idx.getAndIncrement(), fDate + "에 " + c.getPlayer().getName() + "이(가) 보낸 아이템입니다.");
                                 ps.executeUpdate();
                                 rs = ps.getGeneratedKeys();
                                 if (!rs.next()) {
                                    rs.close();
                                    return;
                                 }

                                 long iid = rs.getLong(1);
                                 rs.close();
                                 item.setInventoryId(iid);
                                 if (mit.equals(MapleInventoryType.EQUIP)
                                    || mit.equals(MapleInventoryType.EQUIPPED)
                                    || mit.equals(MapleInventoryType.CASH_EQUIP)) {
                                    Equip equip = (Equip)item;
                                    if (equip.getUniqueId() > 0L && equip.getItemId() / 10000 == 166) {
                                       Android android = equip.getAndroid();
                                       if (android != null) {
                                          android.saveToDb();
                                       }
                                    }

                                    int index = 1;
                                    pse.setLong(index++, iid);
                                    pse.setInt(index++, 0);
                                    pse.setInt(index++, accountID);
                                    pse.setInt(index++, equip.getItemId());
                                    pse.setInt(index++, Math.max(0, equip.getUpgradeSlots()));
                                    pse.setInt(index++, equip.getLevel());
                                    pse.setInt(index++, equip.getStr());
                                    pse.setInt(index++, equip.getDex());
                                    pse.setInt(index++, equip.getInt());
                                    pse.setInt(index++, equip.getLuk());
                                    pse.setInt(index++, equip.getArc());
                                    pse.setInt(index++, equip.getArcEXP());
                                    pse.setInt(index++, equip.getArcLevel());
                                    pse.setInt(index++, equip.getHp());
                                    pse.setInt(index++, equip.getMp());
                                    pse.setInt(index++, equip.getHpR());
                                    pse.setInt(index++, equip.getMpR());
                                    pse.setInt(index++, equip.getWatk());
                                    pse.setInt(index++, equip.getMatk());
                                    pse.setInt(index++, equip.getWdef());
                                    pse.setInt(index++, equip.getMdef());
                                    pse.setInt(index++, equip.getAcc());
                                    pse.setInt(index++, equip.getAvoid());
                                    pse.setInt(index++, equip.getHands());
                                    pse.setInt(index++, equip.getSpeed());
                                    pse.setInt(index++, equip.getJump());
                                    pse.setInt(index++, equip.getViciousHammer());
                                    pse.setInt(index++, equip.getItemEXP());
                                    pse.setInt(index++, equip.getDurability());
                                    pse.setByte(index++, equip.getEnhance());
                                    pse.setByte(index++, equip.getState());
                                    pse.setByte(index++, equip.getLines());
                                    pse.setInt(index++, equip.getPotential1());
                                    pse.setInt(index++, equip.getPotential2());
                                    pse.setInt(index++, equip.getPotential3());
                                    pse.setInt(index++, equip.getPotential4());
                                    pse.setInt(index++, equip.getPotential5());
                                    pse.setInt(index++, equip.getPotential6());
                                    pse.setInt(index++, equip.getFusionAnvil());
                                    pse.setInt(index++, equip.getIncSkill());
                                    pse.setShort(index++, equip.getCharmEXP());
                                    pse.setShort(index++, equip.getPVPDamage());
                                    pse.setShort(index++, equip.getSpecialAttribute());
                                    pse.setByte(index++, equip.getReqLevel());
                                    pse.setByte(index++, equip.getGrowthEnchant());
                                    pse.setByte(index++, (byte)(equip.getFinalStrike() ? 1 : 0));
                                    pse.setShort(index++, equip.getBossDamage());
                                    pse.setShort(index++, equip.getIgnorePDR());
                                    pse.setByte(index++, equip.getTotalDamage());
                                    pse.setByte(index++, equip.getAllStat());
                                    pse.setByte(index++, equip.getKarmaCount());
                                    pse.setShort(index++, equip.getSoulName());
                                    pse.setShort(index++, equip.getSoulEnchanter());
                                    pse.setShort(index++, equip.getSoulPotential());
                                    pse.setInt(index++, equip.getSoulSkill());
                                    pse.setLong(index++, equip.getFire());
                                    pse.setByte(index++, equip.getStarForce());
                                    pse.setInt(index++, 0);
                                    pse.setInt(index++, equip.getDownLevel());
                                    pse.setInt(index++, equip.getSpecialPotential());
                                    pse.setInt(index++, equip.getSPGrade());
                                    pse.setInt(index++, equip.getSPAttack());
                                    pse.setInt(index++, equip.getSPAllStat());
                                    pse.setInt(index++, equip.getItemState());
                                    pse.setInt(index++, equip.getCsGrade());
                                    pse.setInt(index++, equip.getCsOption1());
                                    pse.setInt(index++, equip.getCsOption2());
                                    pse.setInt(index++, equip.getCsOption3());
                                    pse.setLong(index++, equip.getCsOptionExpireDate());
                                    pse.setLong(index++, equip.getExGradeOption());
                                    pse.setInt(index++, equip.getCHUC());
                                    pse.setInt(index++, equip.getClearCheck());
                                    pse.setInt(index++, equip.isSpecialRoyal() ? 1 : 0);
                                    pse.setLong(index++, equip.getSerialNumberEquip());
                                    pse.setInt(index++, equip.getCashEnchantCount());
                                    pse.executeUpdate();
                                 }
                              }

                              c.getPlayer().dropMessage(5, "해당 플레이어에게 오프라인 지급되었습니다.");
                           }
                        } catch (SQLException var155) {
                           new RuntimeException(var155);
                        } finally {
                           try {
                              if (ps != null) {
                                 ps.close();
                                 PreparedStatement var416 = null;
                              }

                              if (pse != null) {
                                 pse.close();
                                 PreparedStatement var419 = null;
                              }

                              if (rs != null) {
                                 rs.close();
                                 ResultSet var427 = null;
                              }
                           } catch (SQLException var119) {
                           }
                        }

                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType((byte)TI), (short)slot, (short)quantity, false, false);
                     } else {
                        MapleCabinet cabinet = p.getCabinet();
                        if (cabinet == null) {
                           c.getPlayer().dropMessage(5, "해당 유저의 보관함을 열람하는 과정에서 오류가 발생하였습니다.");
                           return;
                        }

                        cabinet.addCabinetItem(
                           new MapleCabinetItem(
                              cabinet.getNextIndex(),
                              System.currentTimeMillis() + 604800000L,
                              "[GM 선물]",
                              fDate + "에 " + c.getPlayer().getName() + "이(가) 보낸 아이템입니다.",
                              item
                           )
                        );
                        p.send(CField.maplecabinetResult(8));
                        p.setSaveFlag(p.getSaveFlag() | CharacterSaveFlag.CABINET.getFlag());
                        p.dropMessage(5, "[알림] " + c.getPlayer().getName() + "으로 부터 선물이 도착하였습니다. 메이플 보관함을 통해 수령해주시기 바랍니다.");
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType((byte)TI), (short)slot, (short)quantity, false, false);
                        c.getPlayer().dropMessage(5, "아이템 전송이 완료되었습니다.");
                     }
                  }
               } catch (NumberFormatException var157) {
                  c.getPlayer().dropMessage(5, "문법 오류 /아이템전송 <아이템타입[1-5]> <아이템위치[1부터 시작]> <갯수> <받는 캐릭터>");
               }
            } else if (splitted[0].equals("운영자맵")) {
               Field targetx = c.getChannelServer().getMapFactory().getMap(180000100);
               Portal targetPortal = null;
               if (splitted.length > 1) {
                  try {
                     targetPortal = targetx.getPortal(Integer.parseInt(splitted[1]));
                  } catch (IndexOutOfBoundsException var127) {
                     c.getPlayer().dropMessage(5, "없는 포탈의 값이 있습니다.");
                  }
               }

               if (targetPortal == null) {
                  targetPortal = targetx.getPortal(0);
               }

               if (c.getPlayer().getStat().getHp() != 0L) {
                  EventInstanceManager eim = c.getPlayer().getEventInstance();
                  if (eim != null && eim.hasEventTimer()) {
                     eim.stopEventTimer();
                     eim.dispose();
                     c.getPlayer().setClock(0);
                     c.getPlayer().setDeathCount(0);
                  }

                  c.getPlayer().changeMap(targetx, targetPortal);
               } else {
                  c.getPlayer().dropMessage(5, "[알림] 죽어있는 상태에서는 @마을 명령어를 사용할 수 없습니다.");
               }
            } else if (splitted[0].equals("접속자")) {
               int channel = Integer.parseInt(splitted[1]);
               int mapID = Integer.parseInt(splitted[2]);
               GameServer gs = GameServer.getInstance(channel);
               Field map = gs.getMapFactory().getMap(mapID);
               List<String> onlines = new ArrayList<>();

               for (MapleCharacter p : map.getCharactersThreadsafe()) {
                  onlines.add(p.getName());
               }

               c.getPlayer().dropMessage(5, channel + "채널 " + map.getMapName() + "(" + mapID + ") 맵에 접속자는 아래와 같습니다.");
               String msg = "";
               msg = String.join(", ", onlines);
               c.getPlayer().dropMessage(5, msg);
            } else if (splitted[0].equals("계정찾기")) {
               if (splitted.length < 2) {
                  return;
               }

               boolean find;
               String v;
               String targetName;
               find = false;
               v = "";
               String targetxx = splitted[1];
               targetName = "";
               label3201:
               if (targetxx != null) {
                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (Field map : cs.getMapFactory().getAllMaps()) {
                        for (MapleCharacter chrxxxxxxxx : new ArrayList<>(map.getCharacters())) {
                           if (chrxxxxxxxx != null && chrxxxxxxxx.getClient().getAccountName().equals(targetxx)) {
                              v = chrxxxxxxxx.getClient().getChannel() + "채널 / 맵코드 : " + chrxxxxxxxx.getMapId();
                              targetName = chrxxxxxxxx.getName();
                              find = true;
                              break;
                           }
                        }
                     }
                  }

                  for (MapleCharacter p : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (p != null && p.getClient().getAccountName().equals(targetxx)) {
                        v = " 캐시샵";
                        find = true;
                        targetName = p.getName();
                        break;
                     }
                  }

                  Iterator var319 = new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters()).iterator();

                  MapleCharacter px;
                  do {
                     if (!var319.hasNext()) {
                        break label3201;
                     }

                     px = (MapleCharacter)var319.next();
                  } while (px == null || !px.getClient().getAccountName().equals(targetxx));

                  v = "경매장";
                  find = true;
                  targetName = px.getName();
               }

               if (find) {
                  c.getPlayer().dropMessage(5, splitted[1] + " 계정 내 캐릭터 (" + targetName + ")의 현재 위치 : " + v);
               } else {
                  c.getPlayer().dropMessage(5, "대상을 찾을 수 없습니다.");
               }
            } else if (splitted[0].equals("캐릭터조회")) {
               if (splitted.length < 2) {
                  return;
               }

               boolean findx;
               String targetAccName;
               findx = false;
               String vx = "";
               String targetxxx = splitted[1];
               targetAccName = "";
               label3287:
               if (targetxxx != null) {
                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (Field map : cs.getMapFactory().getAllMaps()) {
                        for (MapleCharacter chrxxxxxxxxx : new ArrayList<>(map.getCharacters())) {
                           if (chrxxxxxxxxx != null && chrxxxxxxxxx.getName().equals(targetxxx)) {
                              vx = chrxxxxxxxxx.getClient().getChannel() + "채널 / 맵코드 : " + chrxxxxxxxxx.getMapId();
                              targetAccName = chrxxxxxxxxx.getClient().getAccountName();
                              findx = true;
                              break;
                           }
                        }
                     }
                  }

                  for (MapleCharacter px : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (px != null && px.getName().equals(targetxxx)) {
                        vx = " 캐시샵";
                        findx = true;
                        targetAccName = px.getClient().getAccountName();
                        break;
                     }
                  }

                  Iterator var322 = new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters()).iterator();

                  MapleCharacter pxx;
                  do {
                     if (!var322.hasNext()) {
                        break label3287;
                     }

                     pxx = (MapleCharacter)var322.next();
                  } while (pxx == null || !pxx.getName().equals(targetxxx));

                  vx = "경매장";
                  findx = true;
                  targetAccName = pxx.getClient().getAccountName();
               }

               if (findx) {
                  c.getPlayer().dropMessage(5, splitted[1] + " 캐릭터의 계정 : (" + targetAccName + ")");
               } else {
                  try (
                     Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM `characters` WHERE `name` = ?");
                  ) {
                     ps.setString(1, splitted[1]);

                     try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                           int accid = rs.getInt("accountid");

                           try (PreparedStatement ps2 = con.prepareStatement("SELECT `name` FROM `accounts` WHERE `id` = ?")) {
                              ps2.setInt(1, accid);

                              try (ResultSet rs2 = ps2.executeQuery()) {
                                 if (rs2.next()) {
                                    findx = true;
                                    targetAccName = rs2.getString("name");
                                 }
                              }
                           }
                        }
                     }
                  } catch (Exception var148) {
                  }

                  if (findx) {
                     c.getPlayer().dropMessage(5, splitted[1] + " 캐릭터의 계정 : (" + targetAccName + ")");
                  } else {
                     c.getPlayer().dropMessage(5, "대상을 찾을 수 없습니다.");
                  }
               }
            } else if (splitted[0].equals("현접해제계정")) {
               if (splitted.length < 2) {
                  return;
               }

               String targetxxxx = splitted[1];
               boolean findxx = false;
               String vxx = "";
               if (targetxxxx != null) {
                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (Field map : cs.getMapFactory().getAllMaps()) {
                        for (MapleCharacter chrxxxxxxxxxx : new ArrayList<>(map.getCharacters())) {
                           if (chrxxxxxxxxxx != null && chrxxxxxxxxxx.getClient().getAccountName().equals(targetxxxx)) {
                              vxx = " 이름 : "
                                 + chrxxxxxxxxxx.getName()
                                 + " / 위치 : "
                                 + chrxxxxxxxxxx.getClient().getChannel()
                                 + "채널 / 맵코드 : "
                                 + chrxxxxxxxxxx.getMapId();
                              map.removePlayer(chrxxxxxxxxxx);
                              chrxxxxxxxxxx.getClient().disconnect(false, map);
                              System.out.println("[알림] 현접 해제 요청 완료.");
                              chrxxxxxxxxxx.getClient().getSession().close();
                              System.out.println("팅겼다고인마");
                              findxx = true;
                           }
                        }
                     }
                  }

                  for (MapleCharacter pxx : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (pxx != null && pxx.getClient().getAccountName().equals(targetxxxx)) {
                        vxx = " 이름 : " + pxx.getName() + " / 위치 : 캐시샵";
                        pxx.getClient().setPlayer(pxx);
                        pxx.getClient().disconnect(false);
                        pxx.getClient().getSession().close();
                        System.out.println("팅겼다고인마");
                        findxx = true;
                     }
                  }

                  for (MapleCharacter pxxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                     if (pxxx != null && pxxx.getClient().getAccountName().equals(targetxxxx)) {
                        vxx = " 이름 : " + pxxx.getName() + " / 위치 : 경매장";
                        pxxx.getClient().setPlayer(pxxx);
                        pxxx.getClient().disconnect(false);
                        pxxx.getClient().getSession().close();
                        System.out.println("팅겼다고인마");
                        findxx = true;
                     }
                  }
               }

               if (findxx) {
                  c.getPlayer().dropMessage(5, "현접 해제가 완료되었습니다. (" + vxx + " )");
               } else {
                  c.getPlayer().dropMessage(5, "대상을 찾을 수 없습니다.");
               }
            } else if (splitted[0].equals("현접해제")) {
               if (splitted.length < 2) {
                  return;
               }

               String targetxxxxx = splitted[1];
               boolean findxxx = false;
               if (targetxxxxx != null) {
                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (Field map : cs.getMapFactory().getAllMaps()) {
                        for (MapleCharacter chrxxxxxxxxxxx : new ArrayList<>(map.getCharacters())) {
                           if (chrxxxxxxxxxxx != null && chrxxxxxxxxxxx.getName().equals(targetxxxxx)) {
                              chrxxxxxxxxxxx.getClient().disconnect(false);
                              chrxxxxxxxxxxx.getClient().getSession().close();
                              System.out.println("팅겼다고인마");
                              findxxx = true;
                           }
                        }
                     }
                  }

                  for (MapleCharacter pxxxx : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (pxxxx.getName().equals(targetxxxxx)) {
                        pxxxx.getClient().setPlayer(pxxxx);
                        pxxxx.getClient().disconnect(false);
                        pxxxx.getClient().getSession().close();
                        System.out.println("팅겼다고인마");
                        findxxx = true;
                     }
                  }

                  for (MapleCharacter pxxxxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                     if (pxxxxx.getName().equals(targetxxxxx)) {
                        pxxxxx.getClient().setPlayer(pxxxxx);
                        pxxxxx.getClient().disconnect(false);
                        pxxxxx.getClient().getSession().close();
                        System.out.println("팅겼다고인마");
                        findxxx = true;
                     }
                  }
               }

               if (findxxx) {
                  c.getPlayer().dropMessage(5, "현접 해제가 완료되었습니다.");
               } else {
                  c.getPlayer().dropMessage(5, "대상을 찾을 수 없습니다.");
               }
            } else if (splitted[0].equals("단어테스트")) {
               c.getPlayer().dropMessage(5, "생성된 단어 (" + splitted[1] + "글자) : " + StringUtil.getRandomCaptcha(Integer.parseInt(splitted[1])));
            } else if (splitted[0].equals("거탐테스트")) {
               String targetxxxxxx = splitted[1];
               MapleCharacter pxxxxxx = c.getPlayer().getMap().getCharacterByName(targetxxxxxx);
               if (player == null) {
                  c.getPlayer().dropMessage(1, "해당 캐릭터를 찾을 수 없습니다.");
                  return;
               }

               pxxxxxx.tryAntiMacro(AntiMacroType.FromGM, c.getPlayer());
            } else if (splitted[0].equals("qex")) {
               c.getPlayer().updateOneInfo(Integer.parseInt(splitted[1]), splitted[2], splitted[3]);
               c.getPlayer().dropMessage(5, splitted[1] + "번 퀘스트의 " + splitted[2] + "키의 값이 " + splitted[3] + "로 업데이트 되었습니다.");
            } else if (splitted[0].equals("COP")) {
               Item item = new Item(4001886, (short)0, (short)1, 0);
               item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 명령어로 얻은 아이템.");
               MapleInventoryManipulator.addbyItem(c, item);
               long uniqueID = item.getUniqueId();
               int mobID = Integer.parseInt(splitted[1]);
               int partyMember = Integer.parseInt(splitted[2]);
               int price = Integer.parseInt(splitted[3]);
               c.getPlayer()
                  .getIntensePowerCrystals()
                  .put(
                     uniqueID,
                     new IntensePowerCrystal(c.getPlayer().getId(), uniqueID, partyMember, mobID, price, 0L, PacketHelper.getTime(System.currentTimeMillis()))
                  );
               c.getPlayer().sendIntensePowerCrystalUpdate();
               c.getPlayer().setSaveFlag2(c.getPlayer().getSaveFlag2() | CharacterSaveFlag2.INTENSE_POWER_CRYSTAL.getFlag());
            } else if (splitted[0].equals("mqtr")) {
               MapleQuest.loadModifiedQuestTime();
               GameServer.getAllInstances().forEach(game -> game.getPlayerStorage().getAllCharacters().forEach(MapleQuest::sendModifiedQuestTime));
               c.getPlayer().dropMessage(5, "퀘스트 시간 설정 데이터 로드 완료.");
            } else if (splitted[0].equals("자동공지리셋")) {
               MapleAutoNotice.autoNotice = null;
               Center.cancelAutoNoticeTask();
               MapleAutoNotice.autoNotice = new MapleAutoNotice();
               MapleAutoNotice.autoNotice.Load();
               Center.registerAutoNotice();
               c.getPlayer().dropMessage(5, "자동 공지사항 리로드가 완료되었습니다.");
            } else if (splitted[0].equals("전투력측정초기화")) {
               DamageMeasurementRank.resetRank();
               c.getPlayer().dropMessage(5, "[시스템] 전투력측정 랭킹이 초기화되었습니다.");

               for (GameServer cs : GameServer.getAllInstances()) {
                  for (Field map : cs.getMapFactory().getAllMaps()) {
                     for (MapleCharacter chrxxxxxxxxxxxx : new ArrayList<>(map.getCharacters())) {
                        if (chrxxxxxxxxxxxx != null) {
                           DamageMeasurementRank.applyDamageRankBuff(chrxxxxxxxxxxxx);
                        }
                     }
                  }
               }

               for (MapleCharacter chrxxxxxxxxxxxxx : CashShopServer.getPlayerStorage().getAllCharacters()) {
                  if (chrxxxxxxxxxxxxx != null) {
                     DamageMeasurementRank.applyDamageRankBuff(chrxxxxxxxxxxxxx);
                  }
               }

               for (MapleCharacter chrxxxxxxxxxxxxxx : AuctionServer.getPlayerStorage().getAllCharacters()) {
                  if (chrxxxxxxxxxxxxxx != null) {
                     DamageMeasurementRank.applyDamageRankBuff(chrxxxxxxxxxxxxxx);
                  }
               }

               Center.Broadcast.broadcastMessage(CField.chatMsg(5, "전투력 측정 랭킹이 초기화되며, 랭킹 보상 및 버프가 지급되었습니다."));
            }
         }
      } else {
         Field map = c.getPlayer().getMap();

         for (MapleMapObject reactorL : map.getMapObjectsInRange(
            c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR)
         )) {
            Reactor reactor2l = (Reactor)reactorL;
            c.getPlayer()
               .dropMessage(
                  6,
                  "리액터: 오브젝트id: "
                     + reactor2l.getObjectId()
                     + " 리액터ID: "
                     + reactor2l.getReactorId()
                     + " 위치: "
                     + reactor2l.getPosition().toString()
                     + " 상태: "
                     + reactor2l.getState()
               );
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("모두튕", "", "현재 채널의 모든 플레이어를 강제로 접속종료합니다.", 6),
         new CommandDefinition("드롭삭제", "", "현재 맵의 모든 아이템을 삭제합니다.", 4),
         new CommandDefinition("퀘스트초기화", "", "현재 받아있는 퀘스트를 모두 지워버립니다.", 2),
         new CommandDefinition("가까운포탈", "", "가장 가까이 있는 포탈을 출력합니다.", 2),
         new CommandDefinition("소환개체", "", "현재 소환된 개체를 출력합니다.", 5),
         new CommandDefinition("확성기토글", "", "모든 채널의 확성기 사용가능 상태를 변경합니다.", 5),
         new CommandDefinition("쓰레드", "", "활성화된 쓰레드를 출력합니다.", 6),
         new CommandDefinition("경로추적", "<쓰레드넘버>", "해당 쓰레드가 어디서 어떻게 시작되었는지를 추적합니다.", 6),
         new CommandDefinition("겟찰", "", "캐릭터 정보 패킷을 전송합니다.", 6),
         new CommandDefinition("드롭토글", "", "현재 맵에서 몬스터의 드롭가능상태를 변경합니다.", 2),
         new CommandDefinition("리액터소환", "<리액터ID>", "입력한 ID의 리액터를 소환합니다.", 4),
         new CommandDefinition("리액터치기", "<리액터오브젝트ID>", "입력한 ID의 리액터를 칩니다.", 4),
         new CommandDefinition("리액터초기화", "", "모든 리액터의 상태를 0으로 만듭니다.", 4),
         new CommandDefinition("리액터목록", "", "현재 맵의 모든 리액터를 출력합니다.", 4),
         new CommandDefinition("현재맵리액터", "", "현재 맵의 모든 리액터를 출력합니다.", 4),
         new CommandDefinition("리액터파괴", "<리액터오브젝트ID>", "해당 ID의 리액터를 파괴합니다.", 4),
         new CommandDefinition("리액터설정", "<리액터오브젝트ID> <상태>", "해당 ID의 리액터의 상태를 조종합니다.", 4),
         new CommandDefinition("경험치배율", "<배율>", "현재 채널의 경험치 배율을 조정합니다.", 6),
         new CommandDefinition("메소배율", "<배율>", "현재 채널의 메소 배율을 조정합니다.", 6),
         new CommandDefinition("드롭배율", "<배율>", "현재 채널의 드롭 배율을 조정합니다.", 6),
         new CommandDefinition("현재맵엔피시", "", "현재맵에 있는 엔피시의 목록을 모두 출력합니다.", 1),
         new CommandDefinition("현재맵포탈", "", "현재맵에 있는 포탈의 목록을 모두 출력합니다.", 1),
         new CommandDefinition("소울게이지", "<설정할소울게이지>", "소울게이지를 원하는수치로 설정합니다.", 4),
         new CommandDefinition("아란콤보", "<설정할콤보>", "아란콤보를 원하는수치로 설정합니다.", 4),
         new CommandDefinition("현접해제", "<해제대상이름>", "현접을 강제로 해제시킵니다.", 6),
         new CommandDefinition("현접해제계정", "<해제대상계정이름>", "현접을 강제로 해제시킵니다.", 6),
         new CommandDefinition("아이템전송", "<아이템타입[1-5]> <아이템위치[1부터 시작]> <갯수> <받을캐릭터이름>", "아이템을 전송합니다.", 6),
         new CommandDefinition("접속자", "<채널> <맵코드>", "해당 맵에 접속자를 보여줍니다.", 6),
         new CommandDefinition("계정찾기", "<계정>", "해당 계정내의 접속중인 캐릭터의 위치를 찾아줍니다.", 4),
         new CommandDefinition("캐릭터조회", "", "", 6),
         new CommandDefinition("운영자맵", "", "운영자맵으로 보내줍니다.", 6),
         new CommandDefinition("관리기실행", "", "서버 관리기를 실행합니다.", 6),
         new CommandDefinition("데일리해제", "", "진행중인 데일리 이벤트를 강제로 종료합니다.", 6),
         new CommandDefinition("자동공지리셋", "", "자동 공지사항을 리로드합니다.", 6),
         new CommandDefinition("빙결", "", "", 4),
         new CommandDefinition("몹스탯", "", "", 6),
         new CommandDefinition("몹스탯2", "", "", 6),
         new CommandDefinition("핫타임이벤트리셋", "", "", 6),
         new CommandDefinition("핫타임아이템리셋", "", "", 6),
         new CommandDefinition("낚시1", "", "", 6),
         new CommandDefinition("데일리초기화", "", "", 6),
         new CommandDefinition("쿨타임초기화", "", "", 6),
         new CommandDefinition("캐시보너스설정", "", "", 6),
         new CommandDefinition("캐시보너스확인", "", "", 6),
         new CommandDefinition("타운맵", "", "", 6),
         new CommandDefinition("TownMap", "", "", 6),
         new CommandDefinition("시작맵", "", "", 6),
         new CommandDefinition("StartMap", "", "", 6),
         new CommandDefinition("BGM리로드", "", "", 6),
         new CommandDefinition("맵BGM초기화", "", "", 6),
         new CommandDefinition("지엠해제", "", "", 6),
         new CommandDefinition("퀘스트시작", "", "", 6),
         new CommandDefinition("협동포인트", "", "", 6),
         new CommandDefinition("채팅금지", "", "", 6),
         new CommandDefinition("패킷저장", "", "", 6),
         new CommandDefinition("옵코드무시", "", "", 6),
         new CommandDefinition("옵코드무시리셋", "", "", 6),
         new CommandDefinition("무릉순위테스트", "", "", 6),
         new CommandDefinition("자동저장길드", "", "", 6),
         new CommandDefinition("CRC온오프", "", "", 6),
         new CommandDefinition("솔에르다", "", "", 6),
         new CommandDefinition("네거티브어레이", "", "", 6),
         new CommandDefinition("공격디버그", "", "", 6),
         new CommandDefinition("테스트1", "", "", 6),
         new CommandDefinition("테스트2", "", "", 6),
         new CommandDefinition("테스트3", "", "", 6),
         new CommandDefinition("팅테스트", "", "", 6),
         new CommandDefinition("서든미션", "", "", 6),
         new CommandDefinition("히든미션", "", "", 6),
         new CommandDefinition("클리어", "", "", 6),
         new CommandDefinition("초대장", "", "", 4),
         new CommandDefinition("스크립트실행", "", "", 4),
         new CommandDefinition("밴포인트", "", "", 6),
         new CommandDefinition("패킷", "", "", 6),
         new CommandDefinition("무적", "", "", 6),
         new CommandDefinition("몹리젠", "", "", 6),
         new CommandDefinition("윌워프", "", "", 6),
         new CommandDefinition("단어테스트", "", "", 6),
         new CommandDefinition("거탐테스트", "", "", 6),
         new CommandDefinition("COP", "", "", 6),
         new CommandDefinition("스킬레벨체크", "", "", 6),
         new CommandDefinition("qex", "", "", 6),
         new CommandDefinition("mqtr", "", "", 6),
         new CommandDefinition("crate", "", "", 6),
         new CommandDefinition("폭탄", "", "", 6),
         new CommandDefinition("몬컬등록", "", "", 6),
         new CommandDefinition("골렘", "", "", 6),
         new CommandDefinition("발판", "", "", 6),
         new CommandDefinition("전문기술", "", "", 6),
         new CommandDefinition("UI", "", "", 6),
         new CommandDefinition("엘몹", "", "", 6),
         new CommandDefinition("랜덤포탈", "", "", 6),
         new CommandDefinition("전투력측정초기화", "", "", 6)
      };
   }
}
